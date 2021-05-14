package com.ule.oa.common.exception;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.NoSuchMessageException;

import com.ule.oa.common.spring.SpringContextUtils;

public class OaException extends Exception {
	
	private static final long serialVersionUID = 6476473452739657628L;
	
	private String[] errorValues;

	public OaException() {
	}

	public OaException(String messageOrCode) {
		super(messageOrCode);
	}

	public OaException(String messageOrCode, String... errorValues) {
		super(messageOrCode);
		this.errorValues = errorValues;
	}

	public OaException(String messageOrCode, String errorValue) {
		super(messageOrCode);
		this.errorValues = new String[] { errorValue == null ? null
				: errorValue };
	}

	public OaException(String messageOrCode, Throwable cause) {
		super(messageOrCode, cause);
	}

	public OaException(Throwable cause) {
		super(cause);
	}

	public OaException(String messageOrCode, String[] errorValues,
			Throwable cause) {
		super(messageOrCode, cause);
		this.errorValues = errorValues;
	}

	public OaException(String messageOrCode, String errorValue, Throwable cause) {
		super(messageOrCode, cause);
		this.errorValues = new String[] { errorValue == null ? null
				: errorValue };
	}
	
	/**
	 * 获取异常信息第一个占位符
	 */
	public String getExceptionCode(){
		return errorValues[0];
	}

	public String getMessage() {
		return formatMessage(super.getMessage(), this.errorValues);
	}

	public String getLocalizedMessage() {
		return getMessage();
	}

	private String formatMessage(String messageOrCode, String... errorValues) {
		String result = null;
		try {
			result = SpringContextUtils.getContext().getMessage(
					messageOrCode, errorValues, Locale.SIMPLIFIED_CHINESE);
		} catch (NoSuchMessageException localNoSuchMessageException) {
		}
		if (StringUtils.isNotEmpty(result)) {
			return result;
		}
		return messageOrCode;
	}
}
