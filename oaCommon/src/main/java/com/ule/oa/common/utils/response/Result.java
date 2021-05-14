package com.ule.oa.common.utils.response;

import java.io.Serializable;

/**
 * @Description: ResponseModel<T> 中Result
 * @author zengli
 * @date 2015年11月12日 下午2:24:47
 */
public class Result implements Serializable {

	private static final long serialVersionUID = 1L;
	/** 编号 */
	private String code;
	/** 原因 */
	private String reason;
	
	public Result() {
		super();
	}
	
	public Result(String code,String reason) {
		super();
		this.setCode(code);
		this.setReason(reason);
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
