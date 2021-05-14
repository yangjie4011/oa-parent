package com.ule.oa.base.po;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

public class ResponseQueryLeaveRecordDetail implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -175796151646277120L;
	
	//流水创建时间
	private String createTime;
	//假期类型
	private String leaveType;
	//使用天数
	private Double useDays;
	//剩余可使用天数
	private Double allowRemainDays;
	//创建人
	private String createUser;

	
	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}

	public Double getUseDays() {
		return useDays;
	}

	public void setUseDays(Double useDays) {
		this.useDays = useDays;
	}

	public Double getAllowRemainDays() {
		return allowRemainDays;
	}

	public void setAllowRemainDays(Double allowRemainDays) {
		this.allowRemainDays = allowRemainDays;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	

}
