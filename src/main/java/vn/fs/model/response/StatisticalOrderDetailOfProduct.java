package vn.fs.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticalOrderDetailOfProduct {
	private Long productId;
	private String productName;
	private Boolean status;
	private int quantity;// Tổng số lượng bán ra của sản phẩm
	private double sumPrice;
	private double averagePrice;// Mức giá trung bình của sản phẩm
	private double minimumPrice;// Mức giá tối thiểu hay là mức giá nhỏ nhất 
	private double maximumPrice;
}
