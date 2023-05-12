package vn.fs.controller.admin;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import vn.fs.entities.ProductEntity;
import vn.fs.entities.UserEntity;
import vn.fs.model.dto.CategoryDto;
import vn.fs.model.dto.ProductDto;
import vn.fs.model.dto.UserDto;
import vn.fs.model.response.PaginateResponse;
import vn.fs.repository.CategoryRepository;
import vn.fs.repository.ProductRepository;
import vn.fs.repository.UserRepository;
import vn.fs.service.ICategoryService;
import vn.fs.service.IProductService;
import vn.fs.service.IUserService;

/**
 * @author DongTHD
 *
 */
@Controller
@RequestMapping("/admin")
public class ProductController{
	@Autowired
	HttpSession session;
	
	@Value("${upload.path}")
	private String pathUploadImage;
	//Vị trí lưu file là :"/upload/images"
	
	@Autowired
	private IProductService productService;

	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	private ICategoryService categoryService;

	@Autowired
	CategoryRepository categoryRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	private IUserService userService;
	
//	private String message;
	
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

	public ProductController(CategoryRepository categoryRepository,
			ProductRepository productRepository) {
		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
	}

	// show list product - table list
	// Hiển thị list và phân trang sản phẩm
	@GetMapping(value = "/products")
	public String products(Model model, Principal principal) {
		model.addAttribute("product", new ProductEntity());
		int currentPage = 1;
		int limit = 5;
		Pageable pageable = PageRequest.of(currentPage-1, limit);
		List<ProductDto> products = productService.findAllProductOfPage(pageable);
		PaginateResponse paginateResponse = new PaginateResponse();
		paginateResponse.setTotalPage((int) Math.ceil((double) productService.getTotalItem()/limit));
		paginateResponse.setPage(currentPage);
		model.addAttribute("products", products);
		model.addAttribute("paginate", paginateResponse);
		return "admin/products";
	}

	// add product
	@PostMapping(value = "/addProduct")
	public String addProduct(@ModelAttribute("product") ProductDto productDto , ModelMap model,
			@RequestParam("file") MultipartFile file, HttpServletRequest httpServletRequest) {
		ProductDto dto = productService.insert(productDto, file);
		if (null != dto) {
			model.addAttribute("message", "Update success");
			model.addAttribute("product", dto);
		} else {
			model.addAttribute("message", "Update failure");
			model.addAttribute("product", dto);
		}
		return "redirect:/admin/products";
	}

	// show select option ở add product
	// Hiển thị các thể loại sản phẩm
	@ModelAttribute("categoryList")
	public List<CategoryDto> showCategory(Model model) {
		List<CategoryDto> categoryList = categoryService.findAllCategory();
		model.addAttribute("categoryList", categoryList);
		return categoryList;
	}
	
	// get Edit brand
	// Chuyển đến giao diện trang edit
	@GetMapping(value = "/editProduct/{id}")
	public String editCategory(@PathVariable("id") Long id, ModelMap model) {
		ProductDto productDto = productService.findById(id);
		
		model.addAttribute("product", productDto);

		return "admin/editProduct";
	}

	// delete category
	@GetMapping("/deleteProduct/{id}")
	public String delProduct(@PathVariable("id") Long id, Model model) {
		productRepository.deleteById(id);
		model.addAttribute("message", "Delete successful!");

		return "redirect:/admin/products";
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setLenient(true);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
	}
}
