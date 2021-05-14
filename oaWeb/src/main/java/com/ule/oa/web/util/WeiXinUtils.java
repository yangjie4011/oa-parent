package com.ule.oa.web.util;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.ule.oa.common.utils.RedisUtils;


public class WeiXinUtils {
	

/** 

 * jsapi_ticket是公众号用于调用微信JS接口的临时票据

 * */

public static String getJsApiTicket(String accessToken, String type){

	String jsApiTicket = "";

	// 这个url链接地址和参数皆不能变

	String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="+accessToken+"&type="+type;

	try {

		if(StringUtils.isNotBlank(RedisUtils.getString("WX_JSAPI_TICKECT"))){
			jsApiTicket = RedisUtils.getString("WX_JSAPI_TICKECT");
		}else{
			JSONObject jsonObject = UrlConUtil.httpsRequest(url, "GET", null);

			Long expiresIn = jsonObject.getLong("expires_in");


		    jsApiTicket = jsonObject.getString("ticket");
		    RedisUtils.setString("WX_JSAPI_TICKECT", jsApiTicket, 5000);

		}
		
	}catch (Exception e){

		

	}

	return jsApiTicket;

}


/** 

 * access_token是公众号的全局唯一接口调用凭据

 * */

public static String getAccessToken(){

	String accessToken = "";

	String grantType = "client_credential";// 获取access_token填写client_credential
	
	String appId ="wx572a19b1a0e16d30";// 第三方用户唯一凭证

	String secret = "cb907f6d5988ff7044e161c568f3017b";// 第三方用户唯一凭证密钥，即appsecret

	// 这个url链接地址和参数皆不能变

	String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=" + grantType + "&appid=" + appId + "&secret="+ secret;

	try {
		
		if(StringUtils.isNotBlank(RedisUtils.getString("WX_ACCESS_TOKEN"))){
			accessToken = RedisUtils.getString("WX_ACCESS_TOKEN");
		}else{
			JSONObject jsonObject = UrlConUtil.httpsRequest(url, "GET", null);

			Long expiresIn = jsonObject.getLong("expires_in");

			accessToken = jsonObject.getString("access_token");
			RedisUtils.setString("WX_ACCESS_TOKEN", accessToken, 5000);
		}
	}catch (Exception e){

	

	}

	return accessToken;

}

	
	

}
