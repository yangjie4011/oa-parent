package com.ule.oa.base.common;

import java.util.HashMap;
import java.util.Map;

/**
 * 常量
 * @author mahaitao
 *
 */
public class OaCommon {
	
	@SuppressWarnings("serial")
	protected static final Map<String,String> CODE_MAP = new HashMap<String,String>();
	
	//100-延迟工作晚到
	@SuppressWarnings("serial")
	protected static final Map<String,String> RUN_CODE_100_MAP = new HashMap<String,String>();
	
	/**
	 * key:假期类型(假期类型参考LEAVE_TYPE_MAP定义)
	 * value:充工时的小时数
	 */
	@SuppressWarnings("serial")
	protected static final Map<String,String> LEAVE_MAP = new HashMap<String,String>();
	
	/**
	 * 假期类型定义
	 * key：假期类型编号
	 * value:假期类型名称
	 */
	@SuppressWarnings("serial")
	protected static final Map<Integer,String> LEAVE_TYPE_MAP = new HashMap<Integer,String>();
	
	@SuppressWarnings("serial")
	protected static final Map<Integer,String> APPLY_TYPE_MAP = new HashMap<Integer,String>();
	
	@SuppressWarnings("serial")
	protected static final Map<Integer,String> VEHICLE_TYPE_MAP = new HashMap<Integer,String>();
	
	public static Map<String, String> getCodeMap() {
		return CODE_MAP;
	}
	
	public static Map<String, String> getRunCode100Map() {
		return RUN_CODE_100_MAP;
	}
	
	public static Map<String, String> getLeaveMap() {
		return LEAVE_MAP;
	}
	
	public static Map<Integer, String> getLeaveTypeMap() {
		return LEAVE_TYPE_MAP;
	}
	
	public static Map<Integer, String> getApplyTypeMap() {
		return APPLY_TYPE_MAP;
	}
	
	public static Map<Integer, String> getVehicleTypeMap() {
		return VEHICLE_TYPE_MAP;
	}
	
	static {
		CODE_MAP.put(OaCommon.CODE_OK, "操作成功");
		CODE_MAP.put(OaCommon.CODE_ERROR, "内部错误");
		
		RUN_CODE_100_MAP.put("112","在线运营部");
		RUN_CODE_100_MAP.put("113","商品部");
		RUN_CODE_100_MAP.put("115","增值业务部");
		RUN_CODE_100_MAP.put("111","线下批销部");
		
		LEAVE_MAP.put("3","8");
		LEAVE_MAP.put("4","1");
		LEAVE_MAP.put("6","8");
		LEAVE_MAP.put("7","8");
		LEAVE_MAP.put("8","8");
		LEAVE_MAP.put("9","8");
		
		LEAVE_TYPE_MAP.put(1,"年假");
		LEAVE_TYPE_MAP.put(2,"病假");
		LEAVE_TYPE_MAP.put(3,"婚假");
		LEAVE_TYPE_MAP.put(4,"哺乳假");
		LEAVE_TYPE_MAP.put(5,"调休");
		LEAVE_TYPE_MAP.put(6,"产前假");
		LEAVE_TYPE_MAP.put(7,"产假");
		LEAVE_TYPE_MAP.put(8,"流产假");
		LEAVE_TYPE_MAP.put(9,"陪产假");
		LEAVE_TYPE_MAP.put(10,"丧假");
		LEAVE_TYPE_MAP.put(11,"事假");
		LEAVE_TYPE_MAP.put(12,"其他");
		
		APPLY_TYPE_MAP.put(100,"项目");
		APPLY_TYPE_MAP.put(200,"会议");
		APPLY_TYPE_MAP.put(300,"日常工作");
		APPLY_TYPE_MAP.put(400,"其他");
		
		VEHICLE_TYPE_MAP.put(100,"火车");
		VEHICLE_TYPE_MAP.put(200,"飞机");
		VEHICLE_TYPE_MAP.put(300,"汽车");
	}
	

	public static final String CREATE_USERE_OA = "SYS_OA"; 
	
	public static final String CODE_OK = "0000";//操作成功
	public static final String CODE_ERROR = "9999";//内部错误
	
	public static final String CODE_SUCCESS = "success";
	public static final String CODE_MESSAGE = "message";
	
	
}
