package vn.fs.util;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import vn.fs.model.response.StatisticalOrderDetailOfProduct;

public class ReportProductPDFExporter {
	private List<StatisticalOrderDetailOfProduct> listReportCommon ;

	public ReportProductPDFExporter(List<StatisticalOrderDetailOfProduct> listReportCommon) {
		this.listReportCommon = listReportCommon;
	}
	private void writeTableHeader(PdfPTable table) {
		PdfPCell cell = new PdfPCell();
		cell.setBackgroundColor(Color.BLUE);
		cell.setPadding(5);
		Font font = FontFactory.getFont("Fonts/ARIAL.TTF", BaseFont.IDENTITY_H, 12.5f);
		//Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN); 
		font.setColor(Color.WHITE);
		cell.setPhrase(new Phrase("ID",font));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Status",font));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Sản Phẩm",font));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Số Lượng bán ra",font));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Doanh thu", font));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Giá bán trung bình", font));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Giá bán thấp nhất", font));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Giá bán cao nhất", font));
		table.addCell(cell); 
	}
	private void writeTableData(PdfPTable table) {
		for (StatisticalOrderDetailOfProduct statisticalOrderDetailOfProduct : listReportCommon) {
			table.addCell(String.valueOf(statisticalOrderDetailOfProduct.getProductId()));
			table.addCell(String.valueOf(statisticalOrderDetailOfProduct.getStatus()));
			Font font = FontFactory.getFont("Fonts/TIMES.TTF", BaseFont.IDENTITY_H, 11.5f);
			table.addCell(new Phrase(statisticalOrderDetailOfProduct.getProductName(),font));
			table.addCell(String.valueOf(statisticalOrderDetailOfProduct.getQuantity()));
			table.addCell(String.valueOf(statisticalOrderDetailOfProduct.getSumPrice()+"đ"));
			table.addCell(String.valueOf(statisticalOrderDetailOfProduct.getAveragePrice()+"đ"));
			table.addCell(String.valueOf(statisticalOrderDetailOfProduct.getMinimumPrice()+"đ"));
			table.addCell(String.valueOf(statisticalOrderDetailOfProduct.getMaximumPrice()+"đ"));
		}
	}
	public void export(HttpServletResponse response) throws DocumentException, IOException {
		Document document = new Document(PageSize.A4);
		PdfWriter.getInstance(document, response.getOutputStream());
		document.open();
		Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD,18f);
		font.setColor(Color.RED);
		Paragraph title = new Paragraph("List All Report Product",font);
		title.setAlignment(Paragraph.ALIGN_CENTER);
		document.add(title);
		PdfPTable table = new PdfPTable(8);
		table.setWidthPercentage(100);
		table.setWidths(new float[] {1.1f,1.8f, 3.1f, 2.5f, 2.9f, 2.9f, 2.9f,2.9f});
		table.setSpacingBefore(15);
		writeTableHeader(table);
		writeTableData(table);
		document.add(table);
		document.close();
	}
}
