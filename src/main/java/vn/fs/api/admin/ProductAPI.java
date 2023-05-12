package vn.fs.api.admin;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.fs.model.dto.ProductDto;
import vn.fs.model.response.PageLayOut;
import vn.fs.model.response.PaginateResponse;
import vn.fs.service.IProductService;

@RestController(value = "Product")
@RequestMapping("/api")
public class ProductAPI {
	@Autowired
	private IProductService productService;

	@GetMapping(value ="/products")
	public PageLayOut findProductsOfPage (@RequestParam int currentPage, @RequestParam int limit, @RequestParam(value="key", required = false) String key) {
		Pageable pageable = PageRequest.of(currentPage -1, limit);
		PaginateResponse paginateResponse = new PaginateResponse();
		List<ProductDto> productDtos = new ArrayList<>();
		if (key == null|| key.isEmpty()) {
			paginateResponse.setTotalPage((int) Math.ceil((double) productService.getTotalItem()/limit));
			paginateResponse.setPage(currentPage);
			productDtos = productService.findAllProductOfPage(pageable);
		}else {
			paginateResponse.setTotalPage((int) Math.ceil((double) productService.getTotalItem(key)/limit));
			paginateResponse.setPage(currentPage);
			productDtos = productService.findProductOfName(key, pageable);
		}
		PageLayOut pageLayOut = new PageLayOut();
		String tbody="";
		for (ProductDto productDto : productDtos) {
			Locale localeVN = new Locale("vi", "VN");
		    NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);
		    String price = currencyVN.format(productDto.getPrice());
			tbody+="<tr>\n" + 
				   "<!-- <td>"+productDto.getProductId()+"</td> -->\n" + 
				   "<td>\n" + 
				   "<img src='/loadImage?imageName="+productDto.getProductImage()+"' width='80px' alt='product'>\n" + 
				   "</td>\n" + 
				   "<td>"+productDto.getProductName()+"</td>\n" + 
				   "<td>"+productDto.getCategory().getCategoryName()+"</td>\n" + 
				   "<td>"+price+"</td>\n" + 
				   "<td>"+productDto.getDiscount()+"%</td>\n" + 
				   "<td>"+productDto.getQuantity()+"</td>\n" + 
				   "<td>"+productDto.getEnteredDate()+"</td>\n" + 
				   "<td>"+productDto.getDescription()+"</td>\n" + 
				   "<td>\n" + 
				   "<div class='form-button-action'>\n" + 
				   "<a href='/admin/editProduct/"+productDto.getProductId()+"' type='button' data-toggle='tooltip' title='' class='btn btn-link btn-primary btn-lg' data-original-title='Chỉnh sửa'>\n" + 
				   "<i class='fa fa-edit'></i>\n" + 
				   "</a>\n" + 
				   "<button data-id='"+productDto.getProductId()+"' data-name='"+productDto.getProductName()+"' onclick='showConfigModalDialog(this.getAttribute(\"data-id\"), this.getAttribute(\"data-name\"))' type='button' data-toggle='tooltip' title='' class='btn btn-link btn-danger' data-original-title='Xóa'>\n" + 
				   "<i class='fa fa-times'></i>\n" + 
				   "</button>\n" + 
				   "</div>\n" + 
				   "</td>\n" + 
				   "</tr>";
		}
		String pagination ="";
		for (int i = 1; i <= paginateResponse.getTotalPage(); i++) {
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
	
	@GetMapping(value ="/searchproducts")
	public PageLayOut getProductsOfName (@RequestParam int limit, @RequestParam String key) {
		Pageable pageable = PageRequest.of(1-1, limit);
		PaginateResponse paginateResponse = new PaginateResponse();
		paginateResponse.setTotalPage((int) Math.ceil((double) productService.getTotalItem(key)/limit));
		paginateResponse.setPage(1);
		List<ProductDto> productDtos = productService.findProductOfName(key, pageable);
		PageLayOut pageLayOut = new PageLayOut();
		String tbody="";
		for (ProductDto productDto : productDtos) {
			Locale localeVN = new Locale("vi", "VN");
		    NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);
		    String price = currencyVN.format(productDto.getPrice());
			tbody+="<tr>\n" + 
				   "<!-- <td>"+productDto.getProductId()+"</td> -->\n" + 
				   "<td>\n" + 
				   "<img src='/loadImage?imageName="+productDto.getProductImage()+"' width='80px' alt='product'>\n" + 
				   "</td>\n" + 
				   "<td>"+productDto.getProductName()+"</td>\n" + 
				   "<td>"+productDto.getCategory().getCategoryName()+"</td>\n" + 
				   "<td>"+price+"</td>\n" + 
				   "<td>"+productDto.getDiscount()+"%</td>\n" + 
				   "<td>"+productDto.getQuantity()+"</td>\n" + 
				   "<td>"+productDto.getEnteredDate()+"</td>\n" + 
				   "<td>"+productDto.getDescription()+"</td>\n" + 
				   "<td>\n" + 
				   "<div class='form-button-action'>\n" + 
				   "<a href='/admin/editProduct/"+productDto.getProductId()+"' type='button' data-toggle='tooltip' title='' class='btn btn-link btn-primary btn-lg' data-original-title='Chỉnh sửa'>\n" + 
				   "<i class='fa fa-edit'></i>\n" + 
				   "</a>\n" + 
				   "<button data-id='"+productDto.getProductId()+"' data-name='"+productDto.getProductName()+"' onclick='showConfigModalDialog(this.getAttribute(\"data-id\"), this.getAttribute(\"data-name\"))' type='button' data-toggle='tooltip' title='' class='btn btn-link btn-danger' data-original-title='Xóa'>\n" + 
				   "<i class='fa fa-times'></i>\n" + 
				   "</button>\n" + 
				   "</div>\n" + 
				   "</td>\n" + 
				   "</tr>";
		}
		String pagination ="";
		for (int i = 1; i <= paginateResponse.getTotalPage(); i++) {
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
		pageLayOut.setBody(tbody);
		pageLayOut.setPagination(pagination);
		return pageLayOut;
	}
}
