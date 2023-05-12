package vn.fs.service.impl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.fs.converter.UserConverter;
import vn.fs.entities.AuthenticationType;
import vn.fs.entities.UserEntity;
import vn.fs.model.dto.UserDto;
import vn.fs.repository.UserRepository;
import vn.fs.service.IUserService;

@Service
public class UserService implements IUserService{
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserConverter userConverter;
	
	@Override
	public UserDto findByEmail(String email) {
		// TODO Auto-generated method stub
		UserEntity userEntity = userRepository.findByEmail(email);
		if (userEntity != null) {
			UserDto userDto = userConverter.toDto(userEntity);
			return userDto;
		}
		else {
			return null;
		}
	}
	@Transactional
	public void updateAuthenticationType(String username, String oauth2ClientName) {
    	AuthenticationType authType = AuthenticationType.valueOf(oauth2ClientName.toUpperCase());
    	String auth = authType.toString();
    	userRepository.updateAuthenticationType(username, auth);
    	System.out.println("Updated user's authentication type to " + authType);
    }

}
