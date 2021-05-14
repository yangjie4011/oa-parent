package com.ule.oa.web.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class JsApiTicketUtils {
	 
	 private static Map<String, String> jsApiTicketMap = new HashMap<String, String>();	
	 
	 private final static String JSAPI_TICKET = "jsapi";//jsapi_ticket	
	 private final static String WX_CARD_JSAPI_TICKET = "wx_card";//微信卡券jsapi_ticket 	
	 /** 	* 获取jsapi_ticket 	*/	
	 public static Map<String, String> getJsApiTicket() {       
		 String time = jsApiTicketMap.get("jsapi_ticket_time");       
		 String jsApiTicket = jsApiTicketMap.get("jsapi_ticket");       
		 Long nowDate = System.currentTimeMillis();      
		 if (StringUtils.isNotBlank(jsApiTicket) && time != null && nowDate - Long.parseLong(time) < (1.5*60*60*1000)) {             
			 return jsApiTicketMap;       
	      }        
		
		 synchronized (JsApiTicketUtils.class) {            
			 if(StringUtils.isNotBlank(jsApiTicket) && time != null && nowDate - Long.parseLong(time) < (1.5*60*60*1000)) {               
				 return jsApiTicketMap;       
			 }         
			 try {			
				 //获取accessToken           
				 String accessToken=WeiXinUtils.getAccessToken();			
				 //根据accessToken获取jsapiTicket			
				 String jsapiTicket = WeiXinUtils.getJsApiTicket(accessToken,JSAPI_TICKET);          		
				 jsApiTicketMap.put("jsapi_ticket_time", nowDate + "");			
				 jsApiTicketMap.put("jsapi_ticket", jsapiTicket);       
			 } catch (Exception e) {           
					 
			 }		
			 return jsApiTicketMap;  
			 }
		 }
		 
	 }
	 



