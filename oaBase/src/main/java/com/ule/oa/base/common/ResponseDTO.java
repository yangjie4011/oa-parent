package com.ule.oa.base.common;

import java.io.Serializable;

/**
 * 操作返回
 * @author yangjie
 *
 */
public class ResponseDTO implements Serializable{

	private static final long serialVersionUID = 8871245201290078839L;
	
	@SuppressWarnings("unused")
	public static final String SUCCESS_CODE = "true";
	@SuppressWarnings("unused")
	public static final String FAIL_CODE = "false";
	
	private String code;
	
	private String message;
	
	private Object data;
	
	public ResponseDTO(String code) {
		this(code, null, null);
	}

	public ResponseDTO(String code, Object data) {
		this(code, null, data);
	}
	
	public ResponseDTO(String code, String message) {
		this.code = code;
		this.message = message;
	}
    
	/**
	 * code=true返回成功，code=false返回失败
	 * @param code
	 * @param message
	 * @param data
	 */
	public ResponseDTO(String code, String message, Object data) {
		this.message = message;
		this.data = data;
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
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

}
