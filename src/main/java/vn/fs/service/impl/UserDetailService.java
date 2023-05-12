package vn.fs.service.impl;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import vn.fs.entities.RoleEntity;
import vn.fs.entities.UserEntity;
import vn.fs.repository.UserRepository;

/**
 * @author DongTHD
 *
 */
@Service
public class UserDetailService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserEntity user = userRepository.findByEmail(email);
		if (user == null) {
			throw new UsernameNotFoundException("Invalid username or password.");
		}
		User us = new User(user.getEmail(), user.getPassword(),mapRolesToAuthorities(user.getRoles()));
//		return new User(user.getEmail(), user.getPassword(),
//				mapRolesToAuthorities(user.getRoles()));
		return us;

	}

	private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<RoleEntity> roles) {
		for (RoleEntity roleEntity : roles) {
			System.out.println(roleEntity.getName());
		}
		return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
	}

}
//org.springframework.security.core.userdetails.User