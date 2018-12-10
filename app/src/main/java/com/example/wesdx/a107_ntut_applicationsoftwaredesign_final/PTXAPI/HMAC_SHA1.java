package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI;

import java.security.SignatureException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HMAC_SHA1 {
	public static String Signature(String xData, String AppKey) throws java.security.SignatureException {
		try {
			//final Base64.Encoder encoder = Base64.getEncoder();

			// get an hmac_sha1 key from the raw key bytes
			SecretKeySpec signingKey = new SecretKeySpec(AppKey.getBytes("UTF-8"),"HmacSHA1");

			// get an hmac_sha1 Mac instance and initialize with the signing key
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(signingKey);

			// compute the hmac on input data bytes
			byte[] rawHmac = mac.doFinal(xData.getBytes("UTF-8"));
			String result = android.util.Base64.encodeToString(rawHmac, android.util.Base64.DEFAULT); //這行要改成這樣，不然就只能在 Andorid 8.0 才能使用
			result=result.replace("\n", ""); // 要加這一行，不然會認證失敗
			return result;

		} catch (Exception e) {
			throw new SignatureException("Failed to generate HMAC : "+ e.getMessage());
		}
	}
}
