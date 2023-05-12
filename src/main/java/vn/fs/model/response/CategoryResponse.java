package vn.fs.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryResponse {
	private Long category_id;
	private String category_name;
	private int countProduct;
}
