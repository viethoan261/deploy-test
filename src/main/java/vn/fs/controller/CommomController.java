package vn.fs.controller;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import vn.fs.entities.CategoryEntity;
import vn.fs.model.dto.UserDto;
import vn.fs.repository.CategoryRepository;
import vn.fs.repository.ProductRepository;
import vn.fs.repository.UserRepository;
import vn.fs.service.IUserService;

/**
 * @author DongTHD
 *
 */
@Controller
public class CommomController {
	@Autowired
	HttpSession session;

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	private IUserService userService;

	@Autowired
	ProductRepository productRepository;

	@ModelAttribute(value = "user")
	public UserDto user(Model model, Principal principal, UserDto userDto, HttpServletRequest request) {
		if (principal != null) {
			model.addAttribute("userDto", new UserDto());
			session = request.getSession();
			String email = session.getAttribute("email").toString();
			String login = session.getAttribute("login").toString();
			if (login.equals("DATABASE")) {
				userDto = userService.findByEmail(principal.getName());
				if (userDto != null) {
					model.addAttribute("userDto", userDto);
				}
			}else {
				if (email != null) {
					userDto = userService.findByEmail(email);
					model.addAttribute("userDto", userDto);
				}
			}
		}
		return userDto;
	}

	@ModelAttribute("categoryList")
	public List<CategoryEntity> showCategory(Model model) {
		List<CategoryEntity> categoryList = categoryRepository.findAll();
		model.addAttribute("categoryList", categoryList);

		return categoryList;
	}

}
