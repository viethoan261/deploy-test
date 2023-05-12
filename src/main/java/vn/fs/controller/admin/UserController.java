package vn.fs.controller.admin;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import vn.fs.entities.UserEntity;
import vn.fs.model.dto.UserDto;
import vn.fs.repository.UserRepository;
import vn.fs.service.IUserService;

/**
 * @author DongTHD
 *
 */
@Controller
public class UserController{
	
	@Autowired
	HttpSession session;
	
	@Autowired
	private IUserService userService;

	@Autowired
	UserRepository userRepository;

	@GetMapping(value = "/admin/users")
	public String customer(Model model, Principal principal ,HttpServletRequest request, UserDto userDto) {
		
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
		
		List<UserEntity> users = userRepository.findAll();
		model.addAttribute("users", users);
		
		return "/admin/users";
	}
}
