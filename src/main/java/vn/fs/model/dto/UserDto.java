package vn.fs.model.dto;

import java.util.Collection;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.fs.entities.AuthenticationType;
import vn.fs.entities.RoleEntity;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {
	private Long userId;
	private String name;
	private String email;//gmail
	private String password;
	private String avatar;
	private Date registerDate;
	private Boolean status;
	private Collection<RoleEntity> roleEntities;
	private AuthenticationType authType;
}
