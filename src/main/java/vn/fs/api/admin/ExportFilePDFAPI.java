package vn.fs.api.admin;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lowagie.text.DocumentException;

import vn.fs.model.response.StatisticalOrderDetailOfProduct;
import vn.fs.service.IOrderDetailService;
import vn.fs.util.ReportProductPDFExporter;

@Controller(value = "ExportFile")
@RequestMapping("/api")
public class ExportFilePDFAPI {
	@Autowired
	private IOrderDetailService orderDetailService;
	
	@GetMapping(value ="/export/pdf/reportProduct")
	public void exportAllToPDF(HttpServletResponse response) throws DocumentException, IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/pdf;charset=UTF-8");
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss_dd-MM-yyyy");
		String currentDateTime = dateFormat.format(new Date());
		String headerKey = "Content-Disposition";
		String headerValue = "attachment;filename=ReportProduct_"+currentDateTime+".pdf";
		response.setHeader(headerKey, headerValue);
		List<StatisticalOrderDetailOfProduct> listReportCommon = orderDetailService.findAllOrderDetailOfProduct();
		ReportProductPDFExporter exporter = new ReportProductPDFExporter(listReportCommon);
		exporter.export(response);		
	}
}
