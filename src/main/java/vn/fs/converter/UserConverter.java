package vn.fs.converter;

import org.springframework.stereotype.Component;

import vn.fs.entities.UserEntity;
import vn.fs.model.dto.UserDto;
@Component
public class UserConverter {
	public UserDto toDto (UserEntity userEntity) {
		UserDto userDto = new UserDto();
		userDto.setUserId(userEntity.getUserId());
		userDto.setName(userEntity.getName());
		userDto.setEmail(userEntity.getEmail());
		userDto.setPassword(userEntity.getPassword());
		userDto.setAvatar(userEntity.getAvatar());
		userDto.setRegisterDate(userEntity.getRegisterDate());
		userDto.setStatus(userEntity.getStatus());
		userDto.setRoleEntities(userEntity.getRoles());
		return userDto;
	}
	
	public UserEntity toEntity (UserDto userDto) {
		UserEntity userEntity = new UserEntity();
		userEntity.setUserId(userDto.getUserId());
		userEntity.setName(userDto.getName());
		userEntity.setEmail(userDto.getEmail());
		userEntity.setPassword(userDto.getPassword());
		userEntity.setAvatar(userDto.getAvatar());
		userEntity.setRegisterDate(userDto.getRegisterDate());
		userEntity.setStatus(userDto.getStatus());
		userEntity.setRoles(userDto.getRoleEntities());
		return userEntity;
	}
}
