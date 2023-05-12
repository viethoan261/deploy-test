package vn.fs.commom;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import vn.fs.entities.RoleEntity;
import vn.fs.entities.UserEntity;
import vn.fs.repository.UserRepository;

//Cập nhật OAuth2User và OAuth2UserService
@Service
public class CustomOAuth2UserService extends CustomDefaultOAuth2UserService{
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		String clientName = userRequest.getClientRegistration().getClientName();
	//Ở đây, câu lệnh này giúp xác định xem người dùng đã đăng nhập thông qua Google hay thông qua Facebook 
    //Biến clientName có thể là Google hoặc Facebook
		
        OAuth2User user =  super.loadUser(userRequest);
        UserEntity userEntity = userRepository.findByEmail(user.<String>getAttribute("email"));
        if (userEntity != null) {
        	Collection<RoleEntity> roleEntities = userEntity.getRoles();
            user = super.loadUserNew(userRequest, roleEntities);
        }
        return new CustomOAuth2User(user, clientName);
    }
}
