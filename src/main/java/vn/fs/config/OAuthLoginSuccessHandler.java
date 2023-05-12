package vn.fs.config;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import vn.fs.commom.CustomOAuth2User;
import vn.fs.entities.RoleEntity;
import vn.fs.model.dto.UserDto;
import vn.fs.service.IUserService;

@Component
// Sau đó viết mã trình xử lý xác thực thành công như sau:
public class OAuthLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler{
	@Autowired
	HttpSession session;
	
	@Autowired
	private IUserService userService;
	
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
		boolean hasRoleUser = false;
		boolean hasAdmin = false;
		
		CustomOAuth2User oauth2User = (CustomOAuth2User) authentication.getPrincipal();
		String oauth2ClientName = oauth2User.getOauth2ClientName();
		String username = oauth2User.getEmail();
		UserDto userDto = userService.findByEmail(username);
		if (userDto != null) {
			userService.updateAuthenticationType(username, oauth2ClientName);
			Collection<RoleEntity> roleEntities = userDto.getRoleEntities();
			for (RoleEntity roleEntity : roleEntities) {
				if(roleEntity.getName().equals("ROLE_USER")) {
					hasRoleUser = true;
					break;
				}else if (roleEntity.getName().equals("ROLE_ADMIN")) {
					hasAdmin = true;
					break;
				}
			}
			session = request.getSession();
			session.setAttribute("email", username);
			session.setAttribute("login", oauth2ClientName);
			if (hasRoleUser) {
				redirectStrategy.sendRedirect(request, response, "/checkout");
				return;
			} else if (hasAdmin) {
				redirectStrategy.sendRedirect(request, response, "/admin/home");
				return;
			} else {
				throw new IllegalStateException();
			}
		}else {
			redirectStrategy.sendRedirect(request, response, "/login?error=true");
		}
		super.onAuthenticationSuccess(request, response, authentication);
	}
}
