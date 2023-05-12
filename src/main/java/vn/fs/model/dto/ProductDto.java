package vn.fs.model.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.fs.entities.CategoryEntity;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
	private Long productId;
	private String productName;
	private int quantity;
	private double price;
	private int discount;
	private String productImage;
	private String description;
	private Date enteredDate;
	private Boolean status;
	private Boolean favorite;
	private CategoryEntity category;
}
