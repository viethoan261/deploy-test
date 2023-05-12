package vn.fs.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CartItemResponse {
	private String productImage;
	private String productName;
	private double priceCartItem;
	private String categoryName;
	private int quantity;
	
}
