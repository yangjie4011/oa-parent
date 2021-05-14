package com.ule.oa.common.utils;

import java.io.Serializable;

/**
 * 操作返回Json模版
 * 
 * @author 樊航
 */
public class ApiResult implements Serializable {
	private static final long serialVersionUID = -968874676536194217L;

	private String code;

	/**
	 * 返回信息
	 */
	private String message;

	private Object data;

	public ApiResult(String code) {
		this(code, null, null);
	}

	public ApiResult(String code, Object data) {
		this(code, null, data);
	}
	
	public ApiResult(String code, String message) {
		this.code = code;
		this.message = message;
	}

	/**
	 * @param status
	 *            true:成功, false:失败
	 * @param message
	 *            操作返回信息
	 * @param data
	 *            自定义数据
	 */
	public ApiResult(String code, String message, Object data) {
		this.message = message;
		this.data = data;
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
