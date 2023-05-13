package vn.fs.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import vn.fs.commom.CommomDataService;
import vn.fs.model.dto.ProductDto;
import vn.fs.model.dto.UserDto;
import vn.fs.repository.ProductRepository;
import vn.fs.service.IProductService;

/**
 * @author DongTHD
 *
 */
@Controller
public class HomeController extends CommomController {

	@Autowired
	ProductRepository productRepository;

	@Autowired
	CommomDataService commomDataService;

	@Autowired
	private IProductService productService;

	@GetMapping(value = {"/","home",""})
	public String home(Model model, UserDto userDto) {

		commomDataService.commonData(model, userDto);
		bestSaleProduct20(model, userDto);
		return "web/home";
	}

	// list product ở trang chủ limit 10 sản phẩm mới nhất
	// Lấy ra được danh sách 10 sản phẩm mới nhất ở trang chủ
	@ModelAttribute("listProduct10")
	public List<ProductDto> listProduct10(Model model) {
		List<ProductDto> productList = productService.findListProductNewLimit();
		model.addAttribute("productList", productList);
		return productList;
	}

	// Top 20 best sale.
	// Top 20 Sản phẩm bán chạy nhất
	public void bestSaleProduct20(Model model, UserDto userDto) {
		List<ProductDto> listProductNew = productService.findTopProductBestSale(userDto);
		if (listProductNew != null) {
			model.addAttribute("bestSaleProduct20", listProductNew);
		}
	}
	
	@GetMapping(value="/logout")
	public void LogOut(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		session.invalidate();
		if (authentication != null) {
			new SecurityContextLogoutHandler().logout(request, response, authentication);
		}
	}
}