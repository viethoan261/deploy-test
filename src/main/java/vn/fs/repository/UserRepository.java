package vn.fs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.fs.entities.UserEntity;

/**
 * @author DongTHD
 *
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

	UserEntity findByEmail(String email);

	@Modifying
	@Query(value ="UPDATE user as u SET u.auth_type = :authType WHERE u.email = :username",nativeQuery = true )
	public void updateAuthenticationType(@Param("username") String username,@Param("authType") String authType);
}
