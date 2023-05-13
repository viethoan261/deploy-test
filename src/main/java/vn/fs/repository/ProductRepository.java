package vn.fs.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.fs.entities.ProductEntity;

/**
 * @author DongTHD
 *
 */
@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

	// List product by category
	@Query(value = "SELECT * FROM products WHERE category_id = ?", nativeQuery = true)
	public List<ProductEntity> listProductByCategory(Long categoryId);

	// Top 10 product by category
	@Query(value = "SELECT * FROM products AS b WHERE b.category_id = ?;", nativeQuery = true)
	List<ProductEntity> listProductByCategory10(Long categoryId);
	
	// List product new
	@Query(value = "SELECT * FROM products ORDER BY entered_date DESC LIMIT 20;", nativeQuery = true)
	public List<ProductEntity> findListProductNew20();
	
	// Search Product
	@Query(value = "SELECT * FROM products WHERE product_name LIKE %?1%" , nativeQuery = true)
	public List<ProductEntity> searchProduct(String productName);
	
	// count quantity by product
	@Query(value = "SELECT c.category_id,c.category_name,\r\n"
			+ "COUNT(*) AS SoLuong\r\n"
			+ "FROM products p\r\n"
			+ "JOIN categories c ON p.category_id = c.category_id\r\n"
			+ "GROUP BY c.category_name,c.category_id;" , nativeQuery = true)
	List<Object[]> listCategoryByProductName();
	
	// Top 20 product best sale
	// top 20 Sản phẩm bán chạy
	@Query(value = "SELECT p.product_id,\r\n" + 
			"p.product_name,\r\n" + 
			"COUNT(*) AS SoLuong\r\n" + 
			"FROM order_details as o\r\n" + 
			"JOIN products as p ON o.product_id = p.product_id\r\n" + 
			"GROUP BY p.product_id,p.product_name\r\n" +
			"ORDER by SoLuong DESC limit 20;", nativeQuery = true)
	public List<Object[]> bestSaleProduct20();
	
	@Query(value = "select * from products o where product_id in :ids", nativeQuery = true)
	List<ProductEntity> findByInventoryIds(@Param("ids") List<Integer> listProductId);

	@Query(value = "SELECT count(*) FROM products", nativeQuery= true)
	int getTotalItem();
	
	@Query(value="SELECT count(*) FROM products as p where p.product_name like %:keyword% ", nativeQuery = true)
	int getTotalItem(@Param("keyword") String keyword);
	
	@Query(value="SELECT p.* FROM products as p where p.product_name like %:keyword%",
			  countQuery= "SELECT count(*) FROM products as p where p.product_name like %:keyword% ",
			  nativeQuery = true)
		Page<ProductEntity> findByName (@Param("keyword") String keyword, Pageable pageable);
}