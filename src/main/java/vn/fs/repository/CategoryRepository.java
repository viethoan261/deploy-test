package vn.fs.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.fs.entities.CategoryEntity;

/**
 * @author DongTHD
 *
 */
@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
	@Query(value="SELECT count(*) FROM categories", nativeQuery = true)
	int getTotalItem();
	
	@Query(value="SELECT count(*) FROM categories as c where c.category_name like %:keyword% ", nativeQuery = true)
	int getTotalItem(@Param("keyword") String keyword);
	
	@Query(value="SELECT c.* FROM categories as c where c.category_name like %:keyword%",
		  countQuery= "SELECT count(*) FROM categories as c where c.category_name like %:keyword% ",
		  nativeQuery = true)
	Page<CategoryEntity> findByName (@Param("keyword") String keyword, Pageable pageable);
}
