package vn.fs.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
	private Long categoryId;
	private String categoryName;
	private String CategoryImage;
	private Boolean status;
}
