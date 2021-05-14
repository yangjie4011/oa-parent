package com.ule.oa.common.exception;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.NoSuchMessageException;

import com.ule.oa.common.spring.SpringContextUtils;

/**
 * 短信异常
 * 
 * @author 樊航
 */
public class SmsException extends Exception {
	
	private static final long serialVersionUID = 6476473452739657628L;
	
	private String[] errorValues;

	public SmsException() {
	}

	public SmsException(String messageOrCode) {
		super(messageOrCode);
	}

	public SmsException(String messageOrCode, String... errorValues) {
		super(messageOrCode);
		this.errorValues = errorValues;
	}

	public SmsException(String messageOrCode, String errorValue) {
		super(messageOrCode);
		this.errorValues = new String[] { errorValue == null ? null
				: errorValue };
	}

	public SmsException(String messageOrCode, Throwable cause) {
		super(messageOrCode, cause);
	}

	public SmsException(Throwable cause) {
		super(cause);
	}

	public SmsException(String messageOrCode, String[] errorValues,
			Throwable cause) {
		super(messageOrCode, cause);
		this.errorValues = errorValues;
	}

	public SmsException(String messageOrCode, String errorValue, Throwable cause) {
		super(messageOrCode, cause);
		this.errorValues = new String[] { errorValue == null ? null
				: errorValue };
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
