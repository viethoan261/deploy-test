package vn.fs.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginateResponse {
	//Hiển thị số page hiện tại
	private int page;// CurrentPage
	private Integer totalPage; // Tổng số Page
	private int start;
	private int end;
}
