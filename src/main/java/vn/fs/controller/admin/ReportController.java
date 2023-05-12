package vn.fs.controller.admin;

import java.security.Principal;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import vn.fs.entities.OrderDetailEntity;
import vn.fs.model.dto.UserDto;
import vn.fs.model.response.PaginateResponse;
import vn.fs.model.response.StatisticalOrderDetailOfProduct;
import vn.fs.repository.OrderDetailRepository;
import vn.fs.repository.UserRepository;
import vn.fs.service.IOrderDetailService;
import vn.fs.service.IUserService;

/**
 * @author DongTHD
 *
 */
@Controller
public class ReportController {
	@Autowired
	HttpSession session;

	@Autowired
	UserRepository userRepository;

	@Autowired
	private OrderDetailRepository orderDetailRepository;
	
	@Autowired
	private IOrderDetailService orderDetailService;
	
	@Autowired
	private IUserService userService;

	// Statistics by product sold
	// Thống kê từ các sản phẩm đã bám được
	@GetMapping(value = "/admin/reports")
	public String report(Model model, Principal principal,HttpServletRequest request) throws SQLException {
		if (principal != null) {
			model.addAttribute("userDto", new UserDto());
			session = request.getSession();
			String email = session.getAttribute("email").toString();
			String login = session.getAttribute("login").toString();
			if (email != null) {
				UserDto userDto = userService.findByEmail(email);
				model.addAttribute("user", userDto);
			}
			if (login== null) {
				UserDto userDto = userService.findByEmail(principal.getName());
				if (userDto != null) {
					model.addAttribute("user", userDto);
				}
			}
		}

		int currentPage = 1;
		int limit = 5;
		Pageable pageable = PageRequest.of(currentPage-1, limit);
		List<StatisticalOrderDetailOfProduct> listReportCommon = orderDetailService.findOrderDetailOfProduct(pageable);
		model.addAttribute("listReportCommon", listReportCommon);
		PaginateResponse paginateResponse = new PaginateResponse();
		paginateResponse.setTotalPage((int) Math.ceil((double) orderDetailService.getTotalItem() /limit));
		paginateResponse.setPage(currentPage);
		model.addAttribute("paginate", paginateResponse);
		return "admin/statistical";
	}

	// Statistics by category sold
	@RequestMapping(value = "/admin/reportCategory")
	public String reportcategory(Model model, Principal principal, HttpServletRequest request) throws SQLException {
		if (principal != null) {
			model.addAttribute("userDto", new UserDto());
			session = request.getSession();
			String email = session.getAttribute("email").toString();
			String login = session.getAttribute("login").toString();
			if (email != null) {
				UserDto userDto = userService.findByEmail(email);
				model.addAttribute("user", userDto);
			}
			if (login== null) {
				UserDto userDto = userService.findByEmail(principal.getName());
				if (userDto != null) {
					model.addAttribute("user", userDto);
				}
			}
		}


		OrderDetailEntity orderDetail = new OrderDetailEntity();
		model.addAttribute("orderDetail", orderDetail);
		List<Object[]> listReportCommon = orderDetailRepository.repoWhereCategory();
		model.addAttribute("listReportCommon", listReportCommon);
		return "admin/statistical";
	}

	// Statistics of products sold by year
	@RequestMapping(value = "/admin/reportYear")
	public String reportyear(Model model, Principal principal) throws SQLException {
		UserDto user = userService.findByEmail(principal.getName());
		model.addAttribute("user", user);

		OrderDetailEntity orderDetail = new OrderDetailEntity();
		model.addAttribute("orderDetail", orderDetail);
		List<Object[]> listReportCommon = orderDetailRepository.repoWhereYear();
		model.addAttribute("listReportCommon", listReportCommon);

		return "admin/statistical";
	}

	// Statistics of products sold by month
	@RequestMapping(value = "/admin/reportMonth")
	public String reportmonth(Model model, Principal principal) throws SQLException {
		UserDto user = userService.findByEmail(principal.getName());
		model.addAttribute("user", user);

		OrderDetailEntity orderDetail = new OrderDetailEntity();
		model.addAttribute("orderDetail", orderDetail);
		List<Object[]> listReportCommon = orderDetailRepository.repoWhereMonth();
		model.addAttribute("listReportCommon", listReportCommon);

		return "admin/statistical";
	}

	// Statistics of products sold by quarter
	@RequestMapping(value = "/admin/reportQuarter")
	public String reportquarter(Model model, Principal principal) throws SQLException {
		UserDto user = userService.findByEmail(principal.getName());
		model.addAttribute("user", user);

		OrderDetailEntity orderDetail = new OrderDetailEntity();
		model.addAttribute("orderDetail", orderDetail);
		List<Object[]> listReportCommon = orderDetailRepository.repoWhereQUARTER();
		model.addAttribute("listReportCommon", listReportCommon);

		return "admin/statistical";
	}

	// Statistics by user
	@RequestMapping(value = "/admin/reportOrderCustomer")
	public String reportordercustomer(Model model, Principal principal) throws SQLException {
		UserDto user = userService.findByEmail(principal.getName());
		model.addAttribute("user", user);

		OrderDetailEntity orderDetail = new OrderDetailEntity();
		model.addAttribute("orderDetail", orderDetail);
		List<Object[]> listReportCommon = orderDetailRepository.reportCustommer();
		model.addAttribute("listReportCommon", listReportCommon);

		return "admin/statistical";
	}
}
