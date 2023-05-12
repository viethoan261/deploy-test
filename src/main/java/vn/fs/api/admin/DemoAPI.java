package vn.fs.api.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.fs.service.IUserService;

@RestController(value = "demo")
@RequestMapping("/api")
public class DemoAPI {
	
	@Autowired
	private IUserService userService;
	
	@GetMapping(value ="/demo")
	public void demo () {
		String gmail="nguyenvandung02052002@gmail.com";
		String au = "Facebook";
		userService.updateAuthenticationType(gmail, au);
		System.out.println("Cập nhật thành công");
	}
}
