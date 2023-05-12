package vn.fs.config;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import vn.fs.entities.RoleEntity;
import vn.fs.entities.UserEntity;

public class MyUserDetails implements UserDetails{
	
	private UserEntity userEntity;
	
	public MyUserDetails(UserEntity userEntity) {
		this.userEntity = userEntity;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		Collection<RoleEntity> roleEntities = userEntity.getRoles();
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		for (RoleEntity  roleEntity : roleEntities) {
			authorities.add(new SimpleGrantedAuthority(roleEntity.getName()));
		}
		return null;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return userEntity.getPassword();
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return userEntity.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return userEntity.isStatus();
	}

}
