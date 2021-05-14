package com.ule.oa.common.utils;


public class JsonWriter {
	
	/**
	 * success = true
	 * 可设置result和message
	 */
	public static JSON successfulJson(Object result, String message) {
		return newJSONInstance(true, result, message);
	}
	
	/**
	 * success = true, result = null, message = null
	 */
	public static JSON successfulJson() {
		return successfulJson(null, null);
	}
	
	/**
	 * success = true, result = null
	 * 可设置message
	 */
	public static JSON successfulMessage(String message) {
		return successfulJson(null, message);
	}
	
	/**
	 * success = true, message = null
	 * 可设置result
	 */
	public static JSON successfulResult(Object result) {
		return successfulJson(result, null);
	}
	
	/**
	 * success = false
	 * 可设置result和message
	 */
	public static JSON failedJson(Object result, String message) {
		return newJSONInstance(false, result, message);
	}
	
	/**
	 * success = false, result = null, message = null
	 */
	public static JSON failedJson() {
		return failedJson(null, null);
	}

	/**
	 * success = false, result = null
	 * 可设置message
	 */
	public static JSON failedMessage(String message) {
		return failedJson(null, message);
	}
	
	/**
	 * success = false, message = null
	 * 可设置result
	 */
	public static JSON failedResult(Object result) {
		return failedJson(result, null);
	}
	
	private static JSON newJSONInstance(boolean success, Object result, String message) {
		return new JSON(success, result, message);
	}
}
