package vn.fs.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vn.fs.commom.CommomDataService;
import vn.fs.converter.UserConverter;
import vn.fs.entities.FavoriteEntity;
import vn.fs.entities.ProductEntity;
import vn.fs.entities.UserEntity;
import vn.fs.model.dto.UserDto;
import vn.fs.repository.FavoriteRepository;
import vn.fs.repository.ProductRepository;

/**
 * @author DongTHD
 *
 */
@Controller
public class FavoriteController extends CommomController {

	@Autowired
	FavoriteRepository favoriteRepository;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	CommomDataService commomDataService;
	
	@Autowired
	private UserConverter userConverter;
	
	@GetMapping(value = "/favorite")
	public String favorite(Model model, UserDto userDto) {
		List<FavoriteEntity> favorites = favoriteRepository.selectAllSaves(userDto.getUserId());
//		UserEntity userEntity = userConverter.toEntity(userDto);
		commomDataService.commonData(model,userDto );
		model.addAttribute("favorites", favorites);
		return "web/favorite";
	}

	@GetMapping(value = "/doFavorite")
	public String doFavorite(Model model, FavoriteEntity favorite, UserDto userDto, @RequestParam("id") Long id) {
		ProductEntity product = productRepository.findById(id).orElse(null);
		UserEntity userEntity = userConverter.toEntity(userDto);
		favorite.setProduct(product);
		favorite.setUser(userEntity);
		product.setFavorite(true);
		favoriteRepository.save(favorite);
		commomDataService.commonData(model, userDto);
		return "redirect:/products";
	}

	@GetMapping(value = "/doUnFavorite")
	public String doUnFavorite(Model model, ProductEntity product, UserDto userDto, @RequestParam("id") Long id) {
		FavoriteEntity favorite = favoriteRepository.selectSaves(id, userDto.getUserId());
		product = productRepository.findById(id).orElse(null);
		product.setFavorite(false);
		favoriteRepository.delete(favorite);
		commomDataService.commonData(model, userDto);
		return "redirect:/products";
	}
}
