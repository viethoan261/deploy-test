package vn.fs.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDto {
	private Long Id;
	private int quantity;
	private Double price;
	private String productId;
	private Long orderId;
}
