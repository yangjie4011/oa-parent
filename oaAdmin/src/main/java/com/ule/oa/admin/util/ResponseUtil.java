package com.ule.oa.admin.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ule.oa.common.utils.DateUtils;

public class ResponseUtil {
	
	
	public static void setDownLoadExeclInfo(HttpServletResponse response,HttpServletRequest request,String name) throws UnsupportedEncodingException {
		String agent=request.getHeader("User-Agent").toLowerCase();
		//设置返回类型
		String fileName = "";
		if(agent.indexOf("firefox")>0){
			fileName = new String(name.getBytes("UTF-8"), "ISO8859-1") + DateUtils.format(new Date(),DateUtils.FORMAT_SIMPLE) + ".xls";
		}else{
			fileName = URLEncoder.encode(name + DateUtils.format(new Date(),DateUtils.FORMAT_SIMPLE) + ".xls","UTF-8");
		}
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-disposition", "attachment; filename="+fileName);
	}

}
