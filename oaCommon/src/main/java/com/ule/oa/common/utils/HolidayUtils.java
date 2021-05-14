package com.ule.oa.common.utils;

import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.http.HttpUtils;

public class HolidayUtils {
	
	public static String getHolidayJson(){  
        String httpUrl="http://www.easybots.cn/api/holiday";  
        String json = "";
        
        try {
			json = HttpUtils.sendByPost(httpUrl, null,false);
		} catch (OaException e) {
		}
        
        return json;
    }  
	
	public static void main(String[] args) {
		getHolidayJson();
	}
}
