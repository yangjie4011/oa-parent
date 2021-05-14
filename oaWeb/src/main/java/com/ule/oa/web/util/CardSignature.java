package com.ule.oa.web.util;

import java.security.MessageDigest;
import java.util.Formatter;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;


public class CardSignature {
	
	private static String byteToHex(final byte[] hash) {		
		  Formatter formatter = new Formatter();		
		  for (byte b : hash) {			
			  formatter.format("%02x", b);		
		  }		
		  String result = formatter.toString();		
		  formatter.close();		
		  return result;	
		  } 	
	     /**	 * 签名	 
	      * * @param request	 
	      * * @param cardId	卡券第三方id	 
	      * * @param appid		appid	 
	      * * @return	 * @throws Exception	 
	      * */
	    public static WechatConfig getSignature(HttpServletRequest request,String appid,String curUrl) throws Exception {		
	    	WechatConfig config = new WechatConfig();		
	    	config.setNonceStr(UUID.randomUUID().toString().replaceAll("-", ""));		
	    	// 时间戳		
	    	config.setTimestamp(Long.toString(System.currentTimeMillis() / 1000));		
	    	// 当前网页的URL，不包含#及其后面部分 主微信appid		
	    	config.setAppId(appid); 		
	    	if(StringUtils.isBlank(curUrl)){
		    	StringBuffer curUrl1 = request.getRequestURL();		
		    	String params = request.getQueryString();		
		    	if (!StringUtils.isBlank(params)) {			
		    		curUrl1 = curUrl1.append("?").append(params);		
		    	}	
		    	curUrl = curUrl1.toString();
	    	}

	    	config.setUrl(curUrl); 		
	    	//获取jsapi_ticket		
	    	Map<String, String> jsApiTicketMap = JsApiTicketUtils.getJsApiTicket();		
	    	String jsapiTicket = jsApiTicketMap.get("jsapi_ticket");				
	    	// 对所有待签名参数按照字段名的ASCII		// 码从小到大排序（字典序）后，使用URL键值对的格式（即key1=value1&key2=value2…）拼接成字符串		// 所有参数名均为小写字符。对string1作sha1加密，字段名和字段值都采用原始值，不进行URL 转义		
	    	StringBuffer sBuffer = new StringBuffer("jsapi_ticket=").append(jsapiTicket);		
	    	sBuffer.append("&noncestr=").append(config.getNonceStr());		
	    	sBuffer.append("&timestamp=").append(config.getTimestamp());		
	    	sBuffer.append("&url=").append(config.getUrl()); 		
	    	MessageDigest crypt = MessageDigest.getInstance("SHA-1");		
	    	crypt.reset();		
	    	crypt.update(sBuffer.toString().getBytes("UTF-8"));				
	    	config.setSignature(byteToHex(crypt.digest()));		
	    	return config;	
	    	}
	


}
