package vn.fs.controller.admin;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import vn.fs.model.dto.UserDto;
import vn.fs.service.IUserService;

/**
 * @author DongTHD
 *
 */
@Controller
@RequestMapping("/admin")
public class IndexAdminController{
	
	@Autowired
	HttpSession session;
	
	@Autowired
	private IUserService userService;
	
	@ModelAttribute(value = "user")
	public UserDto user(Model model, Principal principal, UserDto userDto, HttpServletRequest request) {
		if (principal != null) {
			model.addAttribute("userDto", new UserDto());
			session = request.getSession();
			String email = session.getAttribute("email").toString();
			String login = session.getAttribute("login").toString();
			if (email != null) {
				userDto = userService.findByEmail(email);
				model.addAttribute("userDto", userDto);
			}
			if (login== null) {
				userDto = userService.findByEmail(principal.getName());
				if (userDto != null) {
					model.addAttribute("userDto", userDto);
				}
			}
		}
		return userDto;
	}

	@GetMapping(value = "/home")
	public String index() {
		return "admin/index";
	}
}
