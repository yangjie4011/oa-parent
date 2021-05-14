package com.ule.oa.common.utils.http;

import java.util.Map;

import org.junit.Test;

import com.google.common.collect.Maps;
import com.ule.oa.common.exception.OaException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class HttpUtilsTest {
	
	private static String countryLeaveUrl = "http://v.juhe.cn/calendar/year";
	private static String key = "5a6995a905a252ddd8e2fb44dc565507";
	private static String monthLeaveUrl = "http://v.juhe.cn/calendar/month";
	@Test
	public void testCountryLeave(){
		Map<String,String> paramMap = Maps.newHashMap();
		paramMap.put("key", key);
		paramMap.put("year", "2018");
		String result;
		try {
		result = HttpUtils.sendByGet(countryLeaveUrl, paramMap, true);
		 JSONObject jsonObject = JSONObject.fromObject(result);
		 JSONObject jsonResult = (JSONObject)jsonObject.get("result");
		 JSONObject jsonData = (JSONObject)jsonResult.get("data");
		 JSONArray holidays = (JSONArray)jsonData.get("holidaylist");
		 for(int i = 0 ; i<holidays.size() ; i++) {
			 JSONObject holiday = holidays.getJSONObject(i);
			 System.out.println(holiday.toString());
		 }
		} catch (OaException e) {
			
		}
	}
	
	@Test
	public void testMonthLeave() throws OaException {
		Map<String,String> paramMap = Maps.newHashMap();
		paramMap.put("key", key);
		String year = "2018-";
		for(int i = 1 ; i<=12 ; i=i+3) {
			paramMap.put("year-month", year+i);
			String result = HttpUtils.sendByGet(monthLeaveUrl, paramMap, true);
			 JSONObject json = JSONObject.fromObject(result);
			 JSONObject jsonResult = (JSONObject)json.get("result");
			 JSONObject jsonData = (JSONObject)jsonResult.get("data");
			 JSONArray holidays = (JSONArray)jsonData.get("holiday");
			 //得到当月假期列表
			 for(int j = 0 ; j< holidays.size() ; j++) {
				 JSONObject holiday = (JSONObject)holidays.get(j);
				 //当list#num#=1时,不是法定节假日,不存储
				 if((int)holiday.get("list#num#")==1) {
					 continue;
				 }
				 //rest：建议：如-------拼假建议：2018年4月2日（周一）~2018年4月4日（周三）请假3天，可拼8天清明节小长假
				 //desc：详情：如-------4月29日至5月1日放假，4月28日（星期六）上班
				 System.out.println((String)holiday.get("name"));
				 
				 JSONArray list = (JSONArray)holiday.get("list");
				 for(int m = 0 ; m<list.size() ; m++) {
					 //date:yyyy-mm 日期    status:1：放假;2：法定调班.
					System.out.println((JSONObject)list.get(m));
				 }
			 }
		}
	}
}
