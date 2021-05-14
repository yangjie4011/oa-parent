package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

//延时工作登记
public class DelayWorkRegisterTbl implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private Long employeeId;//员工
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date delayDate;//延时工作日期
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date expectStartTime;//预计开始时间
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date expectEndTime; //预计结束时间
	private Double expectDelayHour;//预计工作小时数
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date actualStartTime;//实际开始时间
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date actualEndTime;//实际结束时间
	private Double actualDelayHour;//实际工作小时数
	private String delayItem;//工作内容
	private Integer isMatched;//是否已经匹配
	private Integer isConfirm;//是否已经确认
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date createTime;
	private String createUser;
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date updateTime;
	private String updateUser;
	private Integer delFlag;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	public Date getDelayDate() {
		return delayDate;
	}
	public void setDelayDate(Date delayDate) {
		this.delayDate = delayDate;
	}
	public Date getExpectStartTime() {
		return expectStartTime;
	}
	public void setExpectStartTime(Date expectStartTime) {
		this.expectStartTime = expectStartTime;
	}
	public Date getExpectEndTime() {
		return expectEndTime;
	}
	public void setExpectEndTime(Date expectEndTime) {
		this.expectEndTime = expectEndTime;
	}
	public Double getExpectDelayHour() {
		return expectDelayHour;
	}
	public void setExpectDelayHour(Double expectDelayHour) {
		this.expectDelayHour = expectDelayHour;
	}
	public Date getActualStartTime() {
		return actualStartTime;
	}
	public void setActualStartTime(Date actualStartTime) {
		this.actualStartTime = actualStartTime;
	}
	public Date getActualEndTime() {
		return actualEndTime;
	}
	public void setActualEndTime(Date actualEndTime) {
		this.actualEndTime = actualEndTime;
	}
	public Double getActualDelayHour() {
		return actualDelayHour;
	}
	public void setActualDelayHour(Double actualDelayHour) {
		this.actualDelayHour = actualDelayHour;
	}
	public String getDelayItem() {
		return delayItem;
	}
	public void setDelayItem(String delayItem) {
		this.delayItem = delayItem;
	}
	public Integer getIsMatched() {
		return isMatched;
	}
	public void setIsMatched(Integer isMatched) {
		this.isMatched = isMatched;
	}
	public Integer getIsConfirm() {
		return isConfirm;
	}
	public void setIsConfirm(Integer isConfirm) {
		this.isConfirm = isConfirm;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
	public Integer getDelFlag() {
		return delFlag;
	}
	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}

}
