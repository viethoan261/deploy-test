package vn.fs.controller.admin;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import vn.fs.model.dto.OrderExcelExporter;
import vn.fs.model.dto.UserDto;
import vn.fs.entities.OrderEntity;
import vn.fs.entities.OrderDetailEntity;
import vn.fs.entities.ProductEntity;
import vn.fs.entities.UserEntity;
import vn.fs.repository.OrderDetailRepository;
import vn.fs.repository.OrderRepository;
import vn.fs.repository.ProductRepository;
import vn.fs.repository.UserRepository;
import vn.fs.service.IUserService;
import vn.fs.service.SendMailService;
import vn.fs.service.impl.OrderDetailService;


/**
 * @author DongTHD
 *
 */
@Controller
@RequestMapping("/admin")
public class OrderController {

	@Autowired
	HttpSession session;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	OrderDetailService orderDetailService;

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	OrderDetailRepository orderDetailRepository;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	SendMailService sendMailService;

	@Autowired
	UserRepository userRepository;

	@ModelAttribute(value = "user")
	public UserDto user(Model model, Principal principal, UserDto userDto, HttpServletRequest request) {
		if (principal != null) {
			model.addAttribute("userDto", new UserDto());
			session = request.getSession();
			String email = session.getAttribute("email").toString();
			String login = session.getAttribute("login").toString();
			if (email != null) {
				userDto = userService.findByEmail(email);
				model.addAttribute("user", userDto);
			}
			if (login== null) {
				userDto = userService.findByEmail(principal.getName());
				if (userDto != null) {
					model.addAttribute("user", userDto);
				}
			}
		}
		return userDto;
	}

	// list order
	@GetMapping(value = "/orders")
	public String orders(Model model, Principal principal) {

		List<OrderEntity> orderDetails = orderRepository.findAll();
		model.addAttribute("orderDetails", orderDetails);

		return "admin/orders";
	}

	@GetMapping("/order/detail/{order_id}")
	public ModelAndView detail(ModelMap model, @PathVariable("order_id") Long id) {

		List<OrderDetailEntity> listO = orderDetailRepository.findByOrderId(id);

		model.addAttribute("amount", orderRepository.findById(id).get().getAmount());
		model.addAttribute("orderDetail", listO);
		model.addAttribute("orderId", id);
		// set active front-end
		model.addAttribute("menuO", "menu");
		return new ModelAndView("admin/editOrder", model);
	}

	@RequestMapping("/order/cancel/{order_id}")
	public ModelAndView cancel(ModelMap model, @PathVariable("order_id") Long id) {
		Optional<OrderEntity> o = orderRepository.findById(id);
		if (o.isPresent()) {
			OrderEntity oReal = o.get();
			oReal.setStatus((short) 3);
			orderRepository.save(oReal);

			return new ModelAndView("forward:/admin/orders", model);
			
			
		}
		return new ModelAndView("forward:/admin/orders", model);
	}

	@RequestMapping("/order/confirm/{order_id}")
	public ModelAndView confirm(ModelMap model, @PathVariable("order_id") Long id) {
		Optional<OrderEntity> o = orderRepository.findById(id);
		if (o.isPresent()) {
			OrderEntity oReal = o.get();
			oReal.setStatus((short) 1);
			orderRepository.save(oReal);

			return new ModelAndView("forward:/admin/orders", model);
		}
		return new ModelAndView("forward:/admin/orders", model);		
	}

	@RequestMapping("/order/delivered/{order_id}")
	public ModelAndView delivered(ModelMap model, @PathVariable("order_id") Long id) {
		Optional<OrderEntity> o = orderRepository.findById(id);
		if (o.isPresent()) {
			OrderEntity oReal = o.get();
			oReal.setStatus((short) 2);
			orderRepository.save(oReal);

			ProductEntity p = null;
			List<OrderDetailEntity> listDe = orderDetailRepository.findByOrderId(id);
			for (OrderDetailEntity od : listDe) {
				p = od.getProduct();
				p.setQuantity(p.getQuantity() - od.getQuantity());
				productRepository.save(p);
			}

			return new ModelAndView("forward:/admin/orders", model);
			
			
		}
		return new ModelAndView("forward:/admin/orders", model);
	}

	// to excel
	@GetMapping(value = "/export")
	public void exportToExcel(HttpServletResponse response) throws IOException {

		response.setContentType("application/octet-stream");
		String headerKey = "Content-Disposition";
		String headerValue = "attachement; filename=orders.xlsx";

		response.setHeader(headerKey, headerValue);

		List<OrderEntity> lisOrders = orderDetailService.listAll();

		OrderExcelExporter excelExporter = new OrderExcelExporter(lisOrders);
		excelExporter.export(response);

	}

}
