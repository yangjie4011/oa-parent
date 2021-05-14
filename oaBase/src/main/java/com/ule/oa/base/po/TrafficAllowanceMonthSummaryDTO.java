package com.ule.oa.base.po;

import java.io.Serializable;

public class TrafficAllowanceMonthSummaryDTO implements Serializable{

	private static final long serialVersionUID = -7075072634015137071L;
	
	private String codeAndMonth;//员工编号&month(格式-SP1002020-01)
	
	private Integer trafficCount;//交通费累计次数

	public String getCodeAndMonth() {
		return codeAndMonth;
	}

	public void setCodeAndMonth(String codeAndMonth) {
		this.codeAndMonth = codeAndMonth;
	}

	public Integer getTrafficCount() {
		return trafficCount;
	}

	public void setTrafficCount(Integer trafficCount) {
		this.trafficCount = trafficCount;
	}

}
