package com.ule.oa.base.po;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ule.oa.base.po.tbl.QuitHistoryTbl;


//离职历史表
public class QuitHistory extends QuitHistoryTbl {

	private static final long serialVersionUID = 1L;
	//员工编号
	private String code;
	//员工姓名
	private String employeeName;
	//入职日期
	private Date entryTime;
	//离职日期
	private Date quitTime;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date quitStartTime;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date quitEndTime;
	
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	public Date getEntryTime() {
		return entryTime;
	}
	public void setEntryTime(Date entryTime) {
		this.entryTime = entryTime;
	}
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	public Date getQuitTime() {
		return quitTime;
	}
	public void setQuitTime(Date quitTime) {
		this.quitTime = quitTime;
	}
	public Date getQuitStartTime() {
		return quitStartTime;
	}
	public void setQuitStartTime(Date quitStartTime) {
		this.quitStartTime = quitStartTime;
	}
	public Date getQuitEndTime() {
		return quitEndTime;
	}
	public void setQuitEndTime(Date quitEndTime) {
		this.quitEndTime = quitEndTime;
	}
	
	

}
