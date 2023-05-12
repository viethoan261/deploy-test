package vn.fs.commom;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import vn.fs.entities.CartItemEntity;
import vn.fs.entities.OrderEntity;
import vn.fs.model.dto.UserDto;
import vn.fs.model.response.CategoryResponse;
import vn.fs.repository.FavoriteRepository;
import vn.fs.repository.ProductRepository;
import vn.fs.service.IFavoriteService;
import vn.fs.service.IProductService;
import vn.fs.service.IShoppingCartService;

/**
 * @author DongTHD
 *
 */
@Service
public class CommomDataService {
	
	@Autowired
	FavoriteRepository favoriteRepository;
	
	@Autowired
	IShoppingCartService shoppingCartService;
	
	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	private IProductService productService;
	
	@Autowired
	public JavaMailSender emailSender;
	
	@Autowired
	private IFavoriteService favoriteService;
	
	@Autowired
	TemplateEngine templateEngine;

	public void commonData(Model model, UserDto userDto) {
		listCategoryByProductName(model);
		Integer totalSave = 0;
		// get count yêu thích
		if (userDto.getUserId() != null) {
			totalSave = favoriteService.selectCountFavoriteSave(userDto.getUserId());
		}

		Integer totalCartItems = shoppingCartService.getCountCart();
		model.addAttribute("totalSave", totalSave);

		model.addAttribute("totalCartItems", totalCartItems);

		Collection<CartItemEntity> cartItems = shoppingCartService.getCartItems();
		model.addAttribute("cartItems", cartItems);

	}
	
	// count product by category
	// Lấy đếm sản phẩm theo danh mục
	public void listCategoryByProductName(Model model) {

		//List<Object[]> coutnProductByCategory = productRepository.listCategoryByProductName();
		List<CategoryResponse> coutnProductByCategory = productService.listCategoryByProductName();
		model.addAttribute("coutnProductByCategory", coutnProductByCategory);
	}
	
	//sendEmail by order success
	public void sendSimpleEmail(String email, String subject, String contentEmail, Collection<CartItemEntity> cartItems,
			double totalPrice, OrderEntity orderFinal) throws MessagingException {
		Locale locale = LocaleContextHolder.getLocale();

		// Prepare the evaluation context
		Context ctx = new Context(locale);
		ctx.setVariable("cartItems", cartItems);
		ctx.setVariable("totalPrice", totalPrice);
		ctx.setVariable("orderFinal", orderFinal);
		// Prepare message using a Spring helper
		MimeMessage mimeMessage = emailSender.createMimeMessage();
		MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");
		mimeMessageHelper.setSubject(subject);
		mimeMessageHelper.setTo(email);
		// Create the HTML body
		String htmlContent = "";
		htmlContent = templateEngine.process("mail/email_en.html", ctx);
		mimeMessageHelper.setText(htmlContent, true);

		// Send Message!
		emailSender.send(mimeMessage);

	}
}
