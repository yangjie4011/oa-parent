package com.ule.oa.common.utils;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

public class UrlRedirect {
	private static String forwardStr;
	
	public static String getHttpUrl(HttpServletRequest request) {
		if(!StringUtils.isBlank(forwardStr)){
			return forwardStr;
		}
		
		String header = request.getHeader("X-Forwarded-Proto");
		if(header!=null){
			if(header.indexOf("https")>-1){
				forwardStr = "https://"+request.getServerName()+request.getContextPath();
			}
		}else{
//			forwardStr = "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
			
			if("local".equalsIgnoreCase(ConfigConstants.ENV)){
				forwardStr = "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
			}else{
				forwardStr = "http://"+request.getServerName()+request.getContextPath();
			}
		}
		return forwardStr;
	}
}
