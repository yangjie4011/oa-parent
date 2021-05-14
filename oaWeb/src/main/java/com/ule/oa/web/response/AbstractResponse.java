package com.ule.oa.web.response;

import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractResponse implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8304215743944399126L;
	
	Logger logger = LoggerFactory.getLogger(AbstractResponse.class);

	/**
	 * 返回码
	 */
	private String returnCode;
	
	/**
	 * 返回信息
	 */
	private String returnMessage;
	
	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getReturnMessage() {
		return returnMessage;
	}

	public void setReturnMessage(String returnMessage) {
		this.returnMessage = returnMessage;
	}

	@Override
	public String toString() {
		return "AbstractResponse [returnCode=" + returnCode
				+ ", returnMessage=" + returnMessage + "]";
	}
	
}
