package com.ule.oa.common.exception;

public class UleException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3712819532033491282L;

	private String errCode;
	private String errMsg;

	public UleException() {
	}

	public UleException(String message, Throwable cause) {
		super(message, cause);
	}

	public UleException(String message) {
		super(message);
	}

	public UleException(Throwable cause) {
		super(cause);
	}

	public UleException(String errCode, String errMsg) {
		super(errCode + ": " + errMsg);
		this.errCode = errCode;
		this.errMsg = errMsg;
	}

	public String getErrCode() {
		return this.errCode;
	}

	public String getErrMsg() {
		return this.errMsg;
	}

}
