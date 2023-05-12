package vn.fs.api.admin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.fs.model.dto.CategoryDto;
import vn.fs.model.response.PageLayOut;
import vn.fs.model.response.PaginateResponse;
import vn.fs.service.ICategoryService;

@RestController(value = "Category")
@RequestMapping("/api")
public class CategoryAPI {
	@Autowired
	private ICategoryService categoryService;
	
	@GetMapping(value = "/categories")
	public PageLayOut getCategoryOfPage(@RequestParam int currentPage, @RequestParam int limit, @RequestParam(value="key", required = false) String key) {
		Pageable pageable = PageRequest.of(currentPage-1, limit);
		List<CategoryDto> categoryDtos = new ArrayList<>();
		PaginateResponse paginateResponse = new PaginateResponse();
		if (key == null|| key.isEmpty()) {
			paginateResponse.setTotalPage((int) Math.ceil((double) categoryService.getTotalItem() /limit));
			paginateResponse.setPage(currentPage);
			categoryDtos = categoryService.findAllCategoryOfPage(pageable);
		}else {			
			paginateResponse.setTotalPage((int) Math.ceil((double) categoryService.getTotalItem(key) /limit));
			paginateResponse.setPage(currentPage);
			categoryDtos = categoryService.findCategoryOfName(key, pageable);
		}
		PageLayOut pageLayOut = new PageLayOut();
		String tbody = "";
		for (CategoryDto categoryDto : categoryDtos) {
			tbody+="<tr>\n" + 
				   "<td>"+categoryDto.getCategoryId()+"</td>\n" + 
				   "<td>"+categoryDto.getCategoryName()+"</td>\r\n" +
				   "<td>"+categoryDto.getStatus()+"</td>\r\n" + 
				   "<td>\n" + 
				   "<div class='form-button-action'>\n" + 
				   "<a href='/admin/editCategory/"+categoryDto.getCategoryId()+"' type='button' data-toggle='tooltip' title='' class='btn btn-link btn-primary btn-lg' data-original-title='Chỉnh sửa'>\n" + 
				   "<i class='fa fa-edit'></i>\n" + 
				   "</a>\n" + 
			   	   "<button data-id='"+categoryDto.getCategoryId()+"' data-name='"+categoryDto.getCategoryName()+"' onclick='showConfigModalDialog(this.getAttribute(\"data-id\"), this.getAttribute(\"data-name\"))' type='button' data-toggle='tooltip' title='' class='btn btn-link btn-danger' data-original-title='Xóa'>\n" + 
				   "<i class='fa fa-times'></i>\n" + 
				   "</button>\n" + 
			       "</div>\n" + 
				   "</td>\n" + 
				   "</tr>";					
		}
		String pagination ="";
		for (int i = 1; i <=paginateResponse.getTotalPage(); i++) {
			if (i==currentPage) {
				pagination +="<li class='paginate_button page-item active'>\n" + 
						"	     <a aria-controls='add-row' data-dt-idx='"+i+"' tabindex='0' class='page-link'>"+i+"</a>\n" + 
						"	  </li>";
			}else {
				pagination +="<li class='paginate_button page-item'>\n" + 
						"	     <a aria-controls='add-row' data-dt-idx='"+i+"' tabindex='0' class='page-link'>"+i+"</a>\n" + 
						"	  </li>";
			}
		}
		pageLayOut.setBody(tbody);
		pageLayOut.setPagination(pagination);
		return pageLayOut;
	}
	
	@GetMapping(value = "/searchcategories")
	public PageLayOut getCategoryOfName(@RequestParam int limit , @RequestParam String key) {
		Pageable pageable = PageRequest.of(1-1, limit);
		PaginateResponse paginateResponse = new PaginateResponse();
		paginateResponse.setTotalPage((int) Math.ceil((double) categoryService.getTotalItem(key) /limit));
		paginateResponse.setPage(1);
		List<CategoryDto> categoryDtos = categoryService.findCategoryOfName(key, pageable);
		PageLayOut pageLayOut = new PageLayOut();
		String body = "";
		for (CategoryDto categoryDto : categoryDtos) {
			 body+="<tr>\n" + 
			"<td>"+categoryDto.getCategoryId()+"</td>\n" + 
			"<td>"+categoryDto.getCategoryName()+"</td>\r\n" +
			"<td>"+categoryDto.getStatus()+"</td>\r\n" +
			"<td>\n" + 
			"<div class='form-button-action'>\n" + 
			"<a href='/admin/editCategory/"+categoryDto.getCategoryId()+"' type='button' data-toggle='tooltip' title class='btn btn-link btn-primary btn-lg' data-original-title='Chỉnh sửa'>\n" + 
			"<i class='fa fa-edit'></i>\n" + 
			"</a>\n" + 
			"<button data-id='"+categoryDto.getCategoryId()+"' data-name='"+categoryDto.getCategoryName()+"' onclick='showConfigModalDialog(this.getAttribute(\"data-id\"), this.getAttribute(\"data-name\"))' type='button' data-toggle='tooltip' title class='btn btn-link btn-danger' data-original-title='Xóa'>\n" + 
			"<i class='fa fa-times'></i>\n" + 
			"</button>\n" + 
			"</div>\n" + 
		    "</td>\n" + 
			"</tr>";
		}
		String pagination ="";
		for (int i = 1; i <=paginateResponse.getTotalPage(); i++) {
			if (i==1) {
				pagination +="<li class='paginate_button page-item active'>\n" + 
						"	     <a aria-controls='add-row' data-dt-idx='"+i+"' tabindex='0' class='page-link'>"+i+"</a>\n" + 
						"	  </li>";
			}else {
				pagination +="<li class='paginate_button page-item'>\n" + 
						"	     <a aria-controls='add-row' data-dt-idx='"+i+"' tabindex='0' class='page-link'>"+i+"</a>\n" + 
						"	  </li>";
			}
		}
		pageLayOut.setBody(body);
		pageLayOut.setPagination(pagination);
		return pageLayOut;
	}
}
