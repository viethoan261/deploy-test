package vn.fs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.fs.entities.CommentEntity;

/**
 * @author DongTHD
 *
 */
@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long>{

}
