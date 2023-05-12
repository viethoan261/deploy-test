package vn.fs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import vn.fs.commom.CommomDataService;
import vn.fs.entities.UserEntity;
import vn.fs.model.dto.UserDto;

/**
 * @author DongTHD
 *
 */
@Controller
public class AboutController extends CommomController {

	@Autowired
	CommomDataService commomDataService;

	@GetMapping(value = "/aboutUs")
	public String about(Model model, UserDto userDto) {

		commomDataService.commonData(model, userDto);
		return "web/about";
	}
}
