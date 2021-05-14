package com.ule.oa.base.po;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.AttnSignRecordTbl;

@JsonInclude(Include.NON_NULL)
public class AttnSignRecord extends AttnSignRecordTbl{

	  /**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = 22189223410062500L;
	
	private Date startTime;
	
	private Date endTime; 
	
	private int count;
	
	private String str;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date alertStartTime;
	
	public Date getAlertStartTime() {
		return alertStartTime;
	}

	public void setAlertStartTime(Date alertStartTime) {
		this.alertStartTime = alertStartTime;
	}

	public Date getAlertEndTime() {
		return alertEndTime;
	}

	public void setAlertEndTime(Date alertEndTime) {
		this.alertEndTime = alertEndTime;
	}

	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date alertEndTime;
	
	private Integer absenteeismAlertDay;//旷工几个工作日

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Integer getAbsenteeismAlertDay() {
		return absenteeismAlertDay;
	}

	public void setAbsenteeismAlertDay(Integer absenteeismAlertDay) {
		this.absenteeismAlertDay = absenteeismAlertDay;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}
	
}