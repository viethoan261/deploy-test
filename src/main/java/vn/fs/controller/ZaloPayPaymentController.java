package vn.fs.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.fs.config.ZaloPayConfig;
import vn.fs.util.HMACUtil;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController(value ="ZaloPay")
@RequestMapping("/api")
public class ZaloPayPaymentController {
	public static String getCurrentTimeString(String format) {
		Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
		SimpleDateFormat fmt = new SimpleDateFormat(format);
		fmt.setCalendar(cal);
		return fmt.format(cal.getTimeInMillis());
	}
	
	@GetMapping(value = "/zalopay")
	public ResponseEntity<?>createVNPayPayment(HttpServletRequest request, HttpServletResponse response) throws ClientProtocolException, IOException{
		Map<String, Object> zalopay_Params = new HashMap<>();
		zalopay_Params.put("appid", ZaloPayConfig.APP_ID);
		zalopay_Params.put("apptransid", getCurrentTimeString("yyMMdd") + "_" + new Date().getTime());
		//zalopay_Params.put("apptime", System.currentTimeMillis());
		zalopay_Params.put("apptime", "1683485849715");
		String appuser = "Nguuyễn Văn Dũng";
		/*zalopay_Params.put("appuser", appuser);*/
		zalopay_Params.put("appuser", "demo");
		long amount = 15000;
		zalopay_Params.put("amount", amount);
		long order_id = 10;
		zalopay_Params.put("description", "Thanh toan don hang #" + order_id);
		zalopay_Params.put("bankcode", "zalopayapp");
		//zalopay_Params.put("item", new JSONObject(order.getOrder_details()).toString());
		zalopay_Params.put("item", "[{\"itemid\":\"knb\",\"itemname\":\"kim nguyen bao\",\"itemprice\":198400,\"itemquantity\":1}]");
		zalopay_Params.put("apptransid ", "230508_01523601383");
		// embeddata
		// Trong trường hợp Merchant muốn trang cổng thanh toán chỉ hiện thị danh sách
		// các ngân hàng ATM,
		// thì Merchant để bankcode="" và thêm bankgroup = ATM vào embeddata như ví dụ
		// bên dưới
		// embeddata={"bankgroup": "ATM"}
		// bankcode=""
		Map<String, String> embeddata = new HashMap<>();
		embeddata.put("merchantinfo", "eshop123");
		embeddata.put("promotioninfo", "");
		embeddata.put("redirecturl", ZaloPayConfig.REDIRECT_URL);

		Map<String, String> columninfo = new HashMap<String, String>();
		columninfo.put("store_name", "E-Shop");
		embeddata.put("columninfo", new JSONObject(columninfo).toString());
		zalopay_Params.put("embeddata", new JSONObject(embeddata).toString());

		String data = zalopay_Params.get("appid") + "|" + zalopay_Params.get("apptransid") + "|"
				+ zalopay_Params.get("appuser") + "|" + zalopay_Params.get("amount") + "|"
				+ zalopay_Params.get("apptime") + "|" + zalopay_Params.get("embeddata") + "|"
				+ zalopay_Params.get("item");
		zalopay_Params.put("mac", HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, ZaloPayConfig.KEY1, data));
//		zalopay_Params.put("phone", order.getPhone());
//		zalopay_Params.put("email", order.getEmail());
//		zalopay_Params.put("address", order.getAddress());
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost post = new HttpPost(ZaloPayConfig.CREATE_ORDER_URL);

		List<NameValuePair> params = new ArrayList<>();
		for (Map.Entry<String, Object> e : zalopay_Params.entrySet()) {
			params.add(new BasicNameValuePair(e.getKey(), e.getValue().toString()));
		}
		post.setEntity(new UrlEncodedFormEntity(params));
		CloseableHttpResponse res = client.execute(post);
		BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
		StringBuilder resultJsonStr = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			resultJsonStr.append(line);
		}
		JSONObject result = new JSONObject(resultJsonStr.toString());
		Map<String, Object> kq = new HashMap<String, Object>();
		kq.put("returnmessage", result.get("returnmessage"));
		kq.put("orderurl", result.get("orderurl"));
		kq.put("returncode", result.get("returncode"));
		kq.put("zptranstoken", result.get("zptranstoken"));
		return ResponseEntity.status(HttpStatus.OK).body(kq);
	}
}
