package com.ule.oa.web.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.lang3.StringUtils;
import sun.misc.BASE64Encoder;

public class SignUtil {
	public static final String charset = "UTF-8"; // 字符集
	/**
	 * 计算签名
	 * 
	 * @param content
	 * @return
	 */
	public static String md5(String content) {
		return md5(content, charset);
	}

	/**
	 * 计算签名
	 * 
	 * @param content
	 * @return
	 */
	public static String md5(String content, String charset) {
		if (StringUtils.isBlank(charset)) {
			charset = SignUtil.charset;
		}
		String sign = "";
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			BASE64Encoder base64Encoder = new BASE64Encoder();
			sign = base64Encoder.encode(md5.digest(content.getBytes(charset)));
		} catch (UnsupportedEncodingException e) {

		} catch (NoSuchAlgorithmException e) {

		}
		return sign;
	}

	/**
	 * 根据参数名排序并拼装字符串
	 * 
	 * @param params
	 * @return
	 */
	public static String getSortParams(Map<String, String> params) {
		if (params == null || params.isEmpty()) {
			return "";
		}
		// 删掉sign参数
		Map<String, String> sortedMap = new TreeMap<String, String>(params);
		sortedMap.remove("sign");

		StringBuffer content = new StringBuffer();
		for (Map.Entry<String, String> entry : sortedMap.entrySet()) {
			//System.out.println(entry.getKey() + ":" + entry.getValue());
			if(!StringUtils.isEmpty(entry.getKey())){
				content.append(entry.getKey().trim());
			}
			if(!StringUtils.isEmpty(entry.getValue())){
				content.append(entry.getValue());
			}
		}
		
		return content.toString();
	}

	/**
	 * 计算签名
	 * 
	 * @param params
	 * @param charset
	 * @return
	 */
	public static String getSign(Map<String, String> params, String appKey,
			String appSecret) {
		String content = appKey.trim() + getSortParams(params).trim() + appSecret.trim();
		String result = md5(content);

		return result;
	}

	/***
	 * 不需要盐值的签名
	 * 
	 * @param params
	 * @return
	 */
	public static String getSign(Map<String, String> params) {
		String content = getSortParams(params);
		String result = md5(content);
		return result;
	}
}


