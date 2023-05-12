package vn.fs.controller.admin;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import vn.fs.entities.CategoryEntity;
import vn.fs.model.dto.CategoryDto;
import vn.fs.model.dto.UserDto;
import vn.fs.model.response.PaginateResponse;
import vn.fs.repository.CategoryRepository;
import vn.fs.repository.UserRepository;
import vn.fs.service.ICategoryService;
import vn.fs.service.IUserService;


/**
 * @author DongTHD
 *
 */
@Controller
@RequestMapping("/admin")
public class CategoryController {
	@Autowired
	HttpSession session;

	@Autowired
	CategoryRepository categoryRepository;
	
	@Autowired
	private ICategoryService categoryService;

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	private IUserService userService;
	
	private String message;
	
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

	// show list category - table list
	// Hiển thị danh mục - danh sách thể loại
//	@ModelAttribute("categories")
//	public List<CategoryEntity> showCategory(Model model) {
//		List<CategoryEntity> categories = categoryRepository.findAll();
//		model.addAttribute("categories", categories);
//		model.addAttribute("message", message);
//		message=null;
//		return categories;
//	}

	@GetMapping(value = "/categories")
	public String categories(Model model, Principal principal) {
		model.addAttribute("category", new CategoryEntity());
		int currentPage = 1;
		int limit = 5;
		Pageable pageable = PageRequest.of(currentPage-1, limit);	
		model.addAttribute("categories", categoryService.findAllCategoryOfPage(pageable));
		PaginateResponse paginateResponse = new PaginateResponse();
		paginateResponse.setTotalPage((int) Math.ceil((double) categoryService.getTotalItem() /limit));
		paginateResponse.setPage(currentPage);
		model.addAttribute("paginate", paginateResponse);
		if(message != null && !message.isEmpty()) {
			model.addAttribute("message", message);
			message = null;
		}
		return "admin/categories";
	}

	// add category
	@PostMapping(value = "/addCategory")
	public String addCategory(@Validated @ModelAttribute("category") CategoryDto categoryDto, ModelMap model,
			BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			model.addAttribute("error", "failure");

			return "admin/categories";
		}
		categoryDto = categoryService.insert(categoryDto);
		if (null != categoryDto) {
			message = "successful!";
		}
		

		return "redirect:/admin/categories";
	}

	// get Edit category
	@GetMapping(value = "/editCategory/{id}")
	public String editCategory(@PathVariable("id") Long id, ModelMap model) {
		CategoryEntity category = categoryRepository.findById(id).orElse(null);

		model.addAttribute("category", category);

		return "admin/editCategory";
	}

	// delete category
	@GetMapping("/delete/{id}")
	public String delCategory(@PathVariable("id") Long id, Model model) {
//		categoryRepository.deleteById(id);
//		message = "Delete successful!";
		CategoryDto categoryDto = categoryService.getByID(id);
		if (categoryDto != null) {
			categoryDto = categoryService.delete(categoryDto);
		}
		if (null != categoryDto) {
			message = "Delete successful!";
		}
		model.addAttribute("message", message);

		return "redirect:/admin/categories";
	}
}
