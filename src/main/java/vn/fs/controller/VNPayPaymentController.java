package vn.fs.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.fs.config.VNPayConfig;
import vn.fs.model.dto.PaymentResDto;
import vn.fs.model.dto.TransactionCompleteDto;
import vn.fs.util.Utils;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController(value ="VNPay")
@RequestMapping("/api")
public class VNPayPaymentController {
	@GetMapping(value = "/vnpay")
	public ResponseEntity<?>createVNPayPayment(HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException{
		String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = req.getParameter("ordertype");
//        long amount = Integer.parseInt(req.getParameter("amount"))*100;
        String bankCode = req.getParameter("bankCode");
        
        long amount = 100000000;
        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
        String vnp_IpAddr = VNPayConfig.getIpAddress(req);
        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;
        
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        
        if (bankCode != null && !bankCode.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bankCode);
        }
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);

        String locate = req.getParameter("language");
        if (locate != null && !locate.isEmpty()) {
            vnp_Params.put("vnp_Locale", locate);
        } else {
            vnp_Params.put("vnp_Locale", "vn");
        }
        String urlrtu= Utils.getBaseURL(req)+"/"+"checkout_success";
        vnp_Params.put("vnp_ReturnUrl",urlrtu);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        
        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
        
        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + queryUrl;
//        com.google.gson.JsonObject job = new JsonObject();
//        job.addProperty("code", "00");
//        job.addProperty("message", "success");
//        job.addProperty("data", paymentUrl);
//        Gson gson = new Gson();
//        resp.getWriter().write(gson.toJson(job));
        
        PaymentResDto paymentResDto = new PaymentResDto();
        paymentResDto.setStatus("OK");
        paymentResDto.setMessage("Successfully");
        paymentResDto.setURL(paymentUrl);
        paymentResDto.setBankName(bankCode);
        return ResponseEntity.status(HttpStatus.OK).body(paymentResDto);
	}
	
	@GetMapping("/vnpay_result")
	public ResponseEntity<?> completeTransactionVNPayPayment (HttpServletRequest req, HttpServletResponse resp,
			@RequestParam(name = "vnp_OrderInfo", required = false) String vnp_OrderInfo,
			@RequestParam(name = "vnp_Amount", required = false) Integer vnp_Amount,
			@RequestParam(name = "vnp_BankCode", required = false) String vnp_BankCode,
			@RequestParam(name = "vnp_BankTranNo", required = false) String vnp_BankTranNo,
			@RequestParam(name = "vnp_CardType", required = false) String vnp_CardType,
			@RequestParam(name = "vnp_PayDate", required = false) String vnp_PayDate,
			@RequestParam(name = "vnp_ResponseCode", required = false) String vnp_ResponseCode,
			@RequestParam(name = "vnp_TransactionNo", required = false) String vnp_TransactionNo,
			@RequestParam(name = "vnp_TxnRef",required= false) String vnp_TxnRef
			){
		// Lấy thời gian khi thanh toán thành công
		String year = vnp_PayDate.substring(0, 4);
		String month = vnp_PayDate.substring(4, 6);
		String date = vnp_PayDate.substring(6, 8);
		String hour = vnp_PayDate.substring(8, 10);
		String minutes = vnp_PayDate.substring(10, 12);
		String second = vnp_PayDate.substring(12, 14);
		String timePay = date + "-" + month + "-" + year + " " + hour + ":" + minutes + ":" + second;
		TransactionCompleteDto transactionCompleteDto = new TransactionCompleteDto();
		if (vnp_ResponseCode.equals("00")) {
			//xử lý sự kiện sau khi thanh toán thành công
			transactionCompleteDto.setStatus(true);
			transactionCompleteDto.setAmount(vnp_Amount);
			transactionCompleteDto.setBankName(vnp_BankCode);
			transactionCompleteDto.setMessage("Successfully");
			transactionCompleteDto.setData("");
		}else {
			//xử lý sự kiện sau khi thanh toán không thành công
			transactionCompleteDto.setStatus(false);
			transactionCompleteDto.setAmount(vnp_Amount);
			transactionCompleteDto.setBankName(vnp_BankCode);
			transactionCompleteDto.setMessage("Failed");
			transactionCompleteDto.setData("");
		}
		return ResponseEntity.status(HttpStatus.OK).body(transactionCompleteDto);
	}
}