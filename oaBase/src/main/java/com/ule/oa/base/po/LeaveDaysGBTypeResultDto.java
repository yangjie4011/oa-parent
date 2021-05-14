package com.ule.oa.base.po;

import java.io.Serializable;

/**
 * 员工请假天数根据类型统计结果
 * @author yangjie
 *
 */
public class LeaveDaysGBTypeResultDto implements Serializable{
	
	private static final long serialVersionUID = -9119870288139608380L;

	//请假天数
	private Double leaveDays;
	//假期类型
	private Integer leaveType;

	public Double getLeaveDays() {
		return leaveDays;
	}

	public void setLeaveDays(Double leaveDays) {
		this.leaveDays = leaveDays;
	}

	public Integer getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(Integer leaveType) {
		this.leaveType = leaveType;
	}
	
}
