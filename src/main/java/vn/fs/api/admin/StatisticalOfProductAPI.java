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

import vn.fs.model.response.PageLayOut;
import vn.fs.model.response.PaginateResponse;
import vn.fs.model.response.StatisticalOrderDetailOfProduct;
import vn.fs.service.IOrderDetailService;

@RestController(value ="StatisticalOfProduct")
@RequestMapping("/api")
public class StatisticalOfProductAPI{

	@Autowired
	private IOrderDetailService orderDetailService;
	
	@GetMapping(value ="/reportProduct")
	public PageLayOut getreportOfProduct (@RequestParam int currentPage, @RequestParam int limit, @RequestParam(value="key", required = false) String key) {
		Pageable pageable = PageRequest.of(currentPage-1, limit);
		List<StatisticalOrderDetailOfProduct> statisticalOrderDetailOfProducts = new ArrayList<>();
		PaginateResponse paginateResponse = new PaginateResponse();
		if (key == null|| key.isEmpty()) {
			paginateResponse.setTotalPage((int) Math.ceil((double) orderDetailService.getTotalItem() /limit));
			paginateResponse.setPage(currentPage);
			statisticalOrderDetailOfProducts = orderDetailService.findOrderDetailOfProduct(pageable);
		}else {
			paginateResponse.setTotalPage((int) Math.ceil((double) orderDetailService.getTotalItem(key) /limit));
			paginateResponse.setPage(currentPage);
			statisticalOrderDetailOfProducts = orderDetailService.findProductOfName(key, pageable);
		}
		PageLayOut pageLayOut = new PageLayOut();
		String tbody = "";
		for (StatisticalOrderDetailOfProduct detailOfProduct : statisticalOrderDetailOfProducts) {
			Locale localeVN = new Locale("vi", "VN");
		    NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);
		    String sumPrice = currencyVN.format(detailOfProduct.getSumPrice());
		    String averagePrice = currencyVN.format(detailOfProduct.getAveragePrice());
		    String minPrice = currencyVN.format(detailOfProduct.getMinimumPrice());
		    String maxPrice = currencyVN.format(detailOfProduct.getMaximumPrice());
			tbody +="<tr>\n" + 
					"<td>"+detailOfProduct.getStatus()+"</td>\n" + 
					"<td>"+detailOfProduct.getProductName()+"</td>\n" + 
					"<td>"+detailOfProduct.getQuantity()+"</td>\n" + 
					"<td>"+sumPrice+"</td>\n" + 
					"<td>"+averagePrice+"</td>\n" + 
					"<td>"+minPrice+"</td>\n" + 
					"<td>"+maxPrice+"</td>\n" + 
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
	
	@GetMapping(value ="/searchreportProduct")
	public PageLayOut getreportOfProduct ( @RequestParam int limit, @RequestParam String key) {
		Pageable pageable = PageRequest.of(1-1, limit);
		List<StatisticalOrderDetailOfProduct> statisticalOrderDetailOfProducts = new ArrayList<>();
		PaginateResponse paginateResponse = new PaginateResponse();
		paginateResponse.setTotalPage((int) Math.ceil((double) orderDetailService.getTotalItem(key) /limit));
		paginateResponse.setPage(1);
		statisticalOrderDetailOfProducts = orderDetailService.findProductOfName(key, pageable);
		PageLayOut pageLayOut = new PageLayOut();
		String tbody = "";
		for (StatisticalOrderDetailOfProduct detailOfProduct : statisticalOrderDetailOfProducts) {
			int i = 1;
			Locale localeVN = new Locale("vi", "VN");
		    NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);
		    String sumPrice = currencyVN.format(detailOfProduct.getSumPrice());
		    String averagePrice = currencyVN.format(detailOfProduct.getAveragePrice());
		    String minPrice = currencyVN.format(detailOfProduct.getMinimumPrice());
		    String maxPrice = currencyVN.format(detailOfProduct.getMaximumPrice());
			tbody +="<tr>\n" + 
					"<td>"+detailOfProduct.getStatus()+"</td>\n" + 
					"<td>"+detailOfProduct.getProductName()+"</td>\n" + 
					"<td>"+detailOfProduct.getQuantity()+"</td>\n" + 
					"<td>"+sumPrice+"</td>\n" + 
					"<td>"+averagePrice+"</td>\n" + 
					"<td>"+minPrice+"</td>\n" + 
					"<td>"+maxPrice+"</td>\n" + 
					"</tr>";
			i++;
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
