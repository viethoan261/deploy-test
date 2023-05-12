package vn.fs.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.zxing.WriterException;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;

import vn.fs.commom.CommomDataService;
import vn.fs.config.PaypalPaymentIntent;
import vn.fs.config.PaypalPaymentMethod;
import vn.fs.converter.ProductConverter;
import vn.fs.converter.UserConverter;
import vn.fs.entities.CartItemEntity;
import vn.fs.entities.OrderDetailEntity;
import vn.fs.entities.OrderEntity;
import vn.fs.entities.ProductEntity;
import vn.fs.entities.UserEntity;
import vn.fs.model.dto.ProductDto;
import vn.fs.model.dto.UserDto;
import vn.fs.repository.OrderDetailRepository;
import vn.fs.repository.OrderRepository;
import vn.fs.repository.ProductRepository;
import vn.fs.service.IProductService;
import vn.fs.service.IShoppingCartService;
import vn.fs.service.PaypalService;
import vn.fs.util.QRCodeGenerator;
import vn.fs.util.Utils;

/**
 * @author DongTHD
 *
 */
@Controller
public class CartController extends CommomController {

	@Autowired
	HttpSession session;

	@Autowired
	private CommomDataService commomDataService;

	@Autowired
	private IShoppingCartService shoppingCartService;

	@Autowired
	private PaypalService paypalService;

	@Autowired
	private IProductService productService;

	@Autowired
	private ProductConverter productConverter;
	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private UserConverter userConverter;

	@Autowired
	private OrderDetailRepository orderDetailRepository;

	public OrderEntity orderFinal = new OrderEntity();

	public static final String URL_PAYPAL_SUCCESS = "pay/success";
	public static final String URL_PAYPAL_CANCEL = "pay/cancel";
	private Logger log = LoggerFactory.getLogger(getClass());

	@GetMapping(value = "/shoppingCart_checkout")
	public String shoppingCart(Model model) {

		Collection<CartItemEntity> cartItems = shoppingCartService.getCartItems();
		model.addAttribute("cartItems", cartItems);
		model.addAttribute("total", shoppingCartService.getAmount());
		double totalPrice = 0;
		for (CartItemEntity cartItem : cartItems) {
			double price = cartItem.getQuantity() * cartItem.getProduct().getPrice();
			totalPrice += price - (price * cartItem.getProduct().getDiscount() / 100);
		}

		model.addAttribute("totalPrice", totalPrice);
		model.addAttribute("totalCartItems", shoppingCartService.getCountCart());

		return "web/shoppingCart_checkout";
	}

	// add cartItem
	// Thêm vào giỏ hàng
//	@GetMapping(value = "/addToCart")
//	public String add(@RequestParam("productId") Long productId, HttpServletRequest request, Model model) {
//
//		ProductEntity product = productRepository.findById(productId).orElse(null);
//
//		session = request.getSession();
//		Collection<CartItemEntity> cartItems = shoppingCartService.getCartItems();
//		if (product != null) {
//			CartItemEntity item = new CartItemEntity();
//			BeanUtils.copyProperties(product, item);
//			item.setQuantity(1);
//			item.setProduct(product);
//			item.setId(productId);
//			shoppingCartService.add(item);
//		}
//		session.setAttribute("cartItems", cartItems);
//		model.addAttribute("totalCartItems", shoppingCartService.getCount());
//
//		return "redirect:/products";
//	}

	// Thêm vào giỏ hàng
	@GetMapping(value = "/addToCart")
	public String add(@RequestParam("productId") Long productId, HttpServletRequest request, Model model) {

		ProductDto productDto = productService.findById(productId);

		session = request.getSession();
		Collection<CartItemEntity> cartItems = shoppingCartService.getCartItems();
		if (productDto != null) {
			CartItemEntity item = new CartItemEntity();
			BeanUtils.copyProperties(productDto, item);
			item.setQuantity(1);
			item.setProduct(productConverter.toEntity(productDto));
			item.setId(productId);
			shoppingCartService.add(item);
		}
		session.setAttribute("cartItems", cartItems);
		model.addAttribute("totalCartItems", shoppingCartService.getCountCart());

		return "redirect:/products";
	}

	// delete cartItem
	@SuppressWarnings("unlikely-arg-type")
	@GetMapping(value = "/remove/{id}")
	public String remove(@PathVariable("id") Long id, HttpServletRequest request, Model model) {
		ProductEntity product = productRepository.findById(id).orElse(null);

		Collection<CartItemEntity> cartItems = shoppingCartService.getCartItems();
		session = request.getSession();
		if (product != null) {
			CartItemEntity item = new CartItemEntity();
			BeanUtils.copyProperties(product, item);
			item.setProduct(product);
			item.setId(id);
			cartItems.remove(session);
			shoppingCartService.remove(item);
		}
		model.addAttribute("totalCartItems", shoppingCartService.getCountCart());
		return "redirect:/checkout";
	}

	// show check out
	// Hiển thị thanh toán
	@GetMapping(value = "/checkout")
	public String checkOut(Model model, UserDto userDto) {

		OrderEntity order = new OrderEntity();
		model.addAttribute("order", order);

		Collection<CartItemEntity> cartItems = shoppingCartService.getCartItems();
		for (CartItemEntity cartItemEntity : cartItems) {
			cartItemEntity.setProduct(productRepository.getById(cartItemEntity.getProduct().getProductId()));
		}
		model.addAttribute("cartItems", cartItems);
		model.addAttribute("total", shoppingCartService.getAmount());
		model.addAttribute("NoOfItems", shoppingCartService.getCountCart());
		double totalPrice = 0;
		for (CartItemEntity cartItem : cartItems) {
			double price = cartItem.getQuantity() * cartItem.getProduct().getPrice();
			totalPrice += price - (price * cartItem.getProduct().getDiscount() / 100);
		}

		model.addAttribute("totalPrice", totalPrice);
		model.addAttribute("totalCartItems", shoppingCartService.getCountCart());
		commomDataService.commonData(model, userDto);

		return "web/shoppingCart_checkout";
	}

	// submit checkout
	@PostMapping(value = "/checkout")
	@Transactional
	public String checkedOut(Model model, OrderEntity order, HttpServletRequest request, UserDto userDto)
			throws MessagingException, WriterException, IOException {

		String checkOut = request.getParameter("checkOut");

		Collection<CartItemEntity> cartItems = shoppingCartService.getCartItems();

		double totalPrice = 0;
		for (CartItemEntity cartItem : cartItems) {
			double price = cartItem.getQuantity() * cartItem.getProduct().getPrice();
			totalPrice += price - (price * cartItem.getProduct().getDiscount() / 100);
			cartItem.setTotalPrice(totalPrice);
		}

		BeanUtils.copyProperties(order, orderFinal);
		if (StringUtils.equals(checkOut, "paypal")) {

			String cancelUrl = Utils.getBaseURL(request) + "/" + URL_PAYPAL_CANCEL;
			String successUrl = Utils.getBaseURL(request) + "/" + URL_PAYPAL_SUCCESS;
			try {
				totalPrice = totalPrice / 22;
				Payment payment = paypalService.createPayment(totalPrice, "USD", PaypalPaymentMethod.paypal,
						PaypalPaymentIntent.sale, "payment description", cancelUrl, successUrl);
				for (Links links : payment.getLinks()) {
					if (links.getRel().equals("approval_url")) {
						return "redirect:" + links.getHref();
					}
				}
			} catch (PayPalRESTException e) {
				log.error(e.getMessage());
			}

		}

		session = request.getSession();
		Date date = new Date();
		order.setOrderDate(date);
		order.setStatus(0);
		order.getOrderId();
		order.setAmount(totalPrice);
		order.setUser(userConverter.toEntity(userDto));
		//Lưu thông tin đơn đặt hàng vào trong database
		orderRepository.save(order);

		for (CartItemEntity cartItem : cartItems) {
			OrderDetailEntity orderDetail = new OrderDetailEntity();
			orderDetail.setQuantity(cartItem.getQuantity());
			orderDetail.setOrder(order);
			orderDetail.setProduct(cartItem.getProduct());
			double unitPrice = cartItem.getProduct().getPrice();
			orderDetail.setPrice(unitPrice);
			// Lưu chi tiết từng đơn hàng vào trong database
			orderDetailRepository.save(orderDetail);
		}
		QRCodeGenerator codeGenerator = new QRCodeGenerator();
		codeGenerator.generateQRCode(order, cartItems);
		// sendMail
		commomDataService.sendSimpleEmail(userDto.getEmail(), "Greeny-Shop Xác Nhận Đơn hàng", "aaaa", cartItems,
				totalPrice, order);

		shoppingCartService.clear();
		session.removeAttribute("cartItems");
		model.addAttribute("orderId", order.getOrderId());

		return "redirect:/checkout_success";
	}

	// paypal
	@GetMapping(URL_PAYPAL_SUCCESS)
	public String successPay(@RequestParam("" + "" + "") String paymentId, @RequestParam("PayerID") String payerId,
			HttpServletRequest request, UserEntity user, Model model) throws MessagingException {
		Collection<CartItemEntity> cartItems = shoppingCartService.getCartItems();
		model.addAttribute("cartItems", cartItems);
		model.addAttribute("total", shoppingCartService.getAmount());

		double totalPrice = 0;
		for (CartItemEntity cartItem : cartItems) {
			double price = cartItem.getQuantity() * cartItem.getProduct().getPrice();
			totalPrice += price - (price * cartItem.getProduct().getDiscount() / 100);
		}
		model.addAttribute("totalPrice", totalPrice);
		model.addAttribute("totalCartItems", shoppingCartService.getCountCart());

		try {
			Payment payment = paypalService.executePayment(paymentId, payerId);
			if (payment.getState().equals("approved")) {

				session = request.getSession();
				Date date = new Date();
				orderFinal.setOrderDate(date);
				orderFinal.setStatus(2);
				orderFinal.getOrderId();
				orderFinal.setUser(user);
				orderFinal.setAmount(totalPrice);
				orderRepository.save(orderFinal);

				for (CartItemEntity cartItem : cartItems) {
					OrderDetailEntity orderDetail = new OrderDetailEntity();
					orderDetail.setQuantity(cartItem.getQuantity());
					orderDetail.setOrder(orderFinal);
					orderDetail.setProduct(cartItem.getProduct());
					double unitPrice = cartItem.getProduct().getPrice();
					orderDetail.setPrice(unitPrice);
					orderDetailRepository.save(orderDetail);
				}

				// sendMail
				commomDataService.sendSimpleEmail(user.getEmail(), "Greeny-Shop Xác Nhận Đơn hàng", "aaaa", cartItems,
						totalPrice, orderFinal);

				shoppingCartService.clear();
				session.removeAttribute("cartItems");
				model.addAttribute("orderId", orderFinal.getOrderId());
				orderFinal = new OrderEntity();
				return "redirect:/checkout_paypal_success";
			}
		} catch (PayPalRESTException e) {
			log.error(e.getMessage());
		}
		return "redirect:/";
	}

	// done checkout ship cod
	@GetMapping(value = "/checkout_success")
	public String checkoutSuccess(Model model, UserDto userDto) {
		commomDataService.commonData(model, userDto);

		return "web/checkout_success";

	}

	// done checkout paypal
	@GetMapping(value = "/checkout_paypal_success")
	public String paypalSuccess(Model model, UserDto userDto) {
		commomDataService.commonData(model, userDto);

		return "web/checkout_paypal_success";

	}

}
