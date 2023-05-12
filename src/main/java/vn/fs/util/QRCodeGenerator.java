package vn.fs.util;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Value;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import vn.fs.entities.CartItemEntity;
import vn.fs.entities.OrderEntity;
public class QRCodeGenerator {
	
	@Value("${upload.qrcode.path}")
	private String pathUploadQRCodeImage;
	// Vị trí lưu ảnh mã QRCode
	
	public void generateQRCode (OrderEntity orderEntity, Collection<CartItemEntity> cartItemEntities) throws WriterException, IOException {
		String qrCodePath = "QRCode/images" ;
		String qrCodeName = qrCodePath+"/"+orderEntity.getUser().getName()+ orderEntity.getOrderId()+"-QRCODE.png";
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		StringBuilder stringBuilder = new StringBuilder();
		int index=1;
		for (CartItemEntity cartItemEntity : cartItemEntities) {
			stringBuilder.append("--Product Numerical :" + index +"\n");
			stringBuilder.append("  Product Name :" + cartItemEntity.getProduct().getProductName()+"\n");
			stringBuilder.append("  Product Price :"+ cartItemEntity.getProduct().getPrice()+"\n");
			stringBuilder.append("  The number of Products :" + cartItemEntity.getQuantity()+"\n");
			stringBuilder.append("  Total Money :" + cartItemEntity.getTotalPrice()+"\n\n");
			index ++;
		}
		BitMatrix bitMatrix = qrCodeWriter.encode(stringBuilder.toString(), BarcodeFormat.QR_CODE, 500, 500);
		Path path = FileSystems.getDefault().getPath(qrCodeName);
		MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
	}
}
