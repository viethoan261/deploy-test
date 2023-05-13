package vn.fs.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import vn.fs.config.MomoPayConfig;
import vn.fs.config.PaypalPaymentIntent;
import vn.fs.config.PaypalPaymentMethod;
import vn.fs.config.VNPayConfig;
import vn.fs.converter.ProductConverter;
import vn.fs.converter.UserConverter;
import vn.fs.entities.CartItemEntity;
import vn.fs.entities.OrderDetailEntity;
import vn.fs.entities.OrderEntity;
import vn.fs.entities.ProductEntity;
import vn.fs.entities.UserEntity;
import vn.fs.model.dto.PaymentResDto;
import vn.fs.model.dto.ProductDto;
import vn.fs.model.dto.UserDto;
import vn.fs.repository.OrderDetailRepository;
import vn.fs.repository.OrderRepository;
import vn.fs.repository.ProductRepository;
import vn.fs.service.IProductService;
import vn.fs.service.IShoppingCartService;
import vn.fs.service.PaypalService;
import vn.fs.util.MomoEncoderUtils;
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
	public static final String URL_VNPAY_SUCCESS = "";
	public static final String URL_VNPAY_CANCEL = "";
	public static final String URL_MOMOPAY_SUCCESS ="";
	public static final String URL_MOMOPAY_CANCEL ="";
	public static final String URL_ZALOPAY_SUCCESS ="";
	public static final String URL_ZALOPAY_CANCEL ="";
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
			throws MessagingException, WriterException, IOException, InvalidKeyException, NoSuchAlgorithmException {

		String checkOut = request.getParameter("checkOut");
		// String checkOut xác định loại phương thức thanh toán
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
			//http://localhost:8080/pay/cancel
			String successUrl = Utils.getBaseURL(request) + "/" + URL_PAYPAL_SUCCESS;
			//http://localhost:8080/pay/success
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
		
		if (StringUtils.equals(checkOut, "vnpay")) {
			String cancelUrlVnpay = Utils.getBaseURL(request)+"/"+URL_VNPAY_CANCEL;
			String successUrlVnpay = Utils.getBaseURL(request)+"/"+URL_PAYPAL_SUCCESS;
			String vnp_Version = "2.1.0";
	        String vnp_Command = "pay";
	        String orderType = request.getParameter("ordertype");
//	        long amount = Integer.parseInt(req.getParameter("amount"))*100;
	        String bankCode = request.getParameter("bankCode");
	        
	        long amount =(long) (totalPrice*100);
	        
	        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
	        String vnp_IpAddr = VNPayConfig.getIpAddress(request);
	        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;
	        
	        Map<String, String> vnp_Params = new HashMap<>();
	        vnp_Params.put("vnp_Version", vnp_Version);
	        vnp_Params.put("vnp_Command", vnp_Command);
	        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
	        vnp_Params.put("vnp_Amount", String.valueOf(amount));
	        vnp_Params.put("vnp_CurrCode", "VND");
	        
	        if (bankCode != null && !bankCode.isEmpty()) {
	            vnp_Params.put("vnp_BankCode", bankCode);
	        }
	        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
	        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
	        vnp_Params.put("vnp_OrderType", orderType);

	        String locate = request.getParameter("language");
	        if (locate != null && !locate.isEmpty()) {
	            vnp_Params.put("vnp_Locale", locate);
	        } else {
	            vnp_Params.put("vnp_Locale", "vn");
	        }
	        vnp_Params.put("vnp_ReturnUrl", Utils.getBaseURL(request)+"/"+"checkout_success");
	        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

	        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
	        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
	        String vnp_CreateDate = formatter.format(cld.getTime());
	        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
	        
	        cld.add(Calendar.MINUTE, 15);
	        String vnp_ExpireDate = formatter.format(cld.getTime());
	        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
	        
	        List fieldNames = new ArrayList(vnp_Params.keySet());
	        Collections.sort(fieldNames);
	        StringBuilder hashData = new StringBuilder();
	        StringBuilder query = new StringBuilder();
	        Iterator itr = fieldNames.iterator();
	        while (itr.hasNext()) {
	            String fieldName = (String) itr.next();
	            String fieldValue = (String) vnp_Params.get(fieldName);
	            if ((fieldValue != null) && (fieldValue.length() > 0)) {
	                //Build hash data
	                hashData.append(fieldName);
	                hashData.append('=');
	                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
	                //Build query
	                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
	                query.append('=');
	                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
	                if (itr.hasNext()) {
	                    query.append('&');
	                    hashData.append('&');
	                }
	            }
	        }
	        String queryUrl = query.toString();
	        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.vnp_HashSecret, hashData.toString());
	        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
	        String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + queryUrl;	        
	        PaymentResDto paymentResDto = new PaymentResDto();
	        paymentResDto.setStatus("OK");
	        paymentResDto.setMessage("Successfully");
	        paymentResDto.setURL(paymentUrl);
	        paymentResDto.setBankName(bankCode);
	        String url = paymentResDto.getURL();
	        return "redirect:" + paymentResDto.getURL();
		}
		if (StringUtils.equals(checkOut, "momopay")) {
			String cancelUrlVnpay = Utils.getBaseURL(request)+"/"+URL_MOMOPAY_CANCEL;
			String successUrlVnpay = Utils.getBaseURL(request)+"/"+URL_MOMOPAY_SUCCESS;
			JSONObject json = new JSONObject();
			long amount = 2000000;
			long oder_id = 11;
			String partnerCode = MomoPayConfig.PARTNER_CODE;
			String accessKey = MomoPayConfig.ACCESS_KEY;
			String secretKey = MomoPayConfig.SECRET_KEY;
			String returnUrl = MomoPayConfig.REDIRECT_URL;
			String notifyUrl = MomoPayConfig.NOTIFY_URL;
			json.put("partnerCode", partnerCode);
			json.put("accessKey", accessKey);
			json.put("requestId", String.valueOf(System.currentTimeMillis()));
			json.put("amount", String.valueOf(amount));
			json.put("orderId", String.valueOf(oder_id));
			json.put("orderInfo", "Thanh toan don hang " +String.valueOf(oder_id));
			json.put("returnUrl", returnUrl);
			json.put("notifyUrl", notifyUrl);
			json.put("requestType", "captureMoMoWallet");

			String data = "partnerCode=" + partnerCode 
					+ "&accessKey=" + accessKey 
					+ "&requestId=" + json.get("requestId")
					+ "&amount=" + String.valueOf(amount) 
					+ "&orderId=" + json.get("orderId") 
					+ "&orderInfo=" + json.get("orderInfo") 
					+ "&returnUrl=" + returnUrl 
					+ "&notifyUrl=" + notifyUrl 
					+ "&extraData=";

			String hashData = MomoEncoderUtils.signHmacSHA256(data, secretKey);
			json.put("signature", hashData);
			CloseableHttpClient client = HttpClients.createDefault();
			HttpPost post = new HttpPost(MomoPayConfig.CREATE_ORDER_URL);
			StringEntity stringEntity = new StringEntity(json.toString());
			post.setHeader("content-type", "application/json");
			post.setEntity(stringEntity);

			CloseableHttpResponse res = client.execute(post);
			BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
			StringBuilder resultJsonStr = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
				resultJsonStr.append(line);
			}
			JSONObject result = new JSONObject(resultJsonStr.toString());
			Map<String, Object> kq = new HashMap<>();
			if (result.get("errorCode").toString().equalsIgnoreCase("0")) {
				kq.put("requestType", result.get("requestType"));
				kq.put("orderId", result.get("orderId"));
				kq.put("payUrl", result.get("payUrl"));
				kq.put("signature", result.get("signature"));
				kq.put("requestId", result.get("requestId"));
				kq.put("errorCode", result.get("errorCode"));
				kq.put("message", result.get("message"));
				kq.put("localMessage", result.get("localMessage"));
			} else {
				kq.put("requestType", result.get("requestType"));
				kq.put("orderId", result.get("orderId"));
				kq.put("signature", result.get("signature"));
				kq.put("requestId", result.get("requestId"));
				kq.put("errorCode", result.get("errorCode"));
				kq.put("message", result.get("message"));
				kq.put("localMessage", result.get("localMessage"));
			}
			return "redirect:" + result.get("payUrl");
		}
		if (StringUtils.equals(checkOut, "zalopay")) {
			String cancelUrlVnpay = Utils.getBaseURL(request)+"/"+URL_ZALOPAY_CANCEL;
			String successUrlVnpay = Utils.getBaseURL(request)+"/"+URL_ZALOPAY_SUCCESS;
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
		// Tạo mã QRCode
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