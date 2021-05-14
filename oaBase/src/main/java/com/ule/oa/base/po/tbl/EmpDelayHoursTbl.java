package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

//员工延时工作小时数
public class EmpDelayHoursTbl implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5249361535883042814L;
	
	
	private Long id;
	private Long employeeId;//员工
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date delayMonth;//延时工作月份
	private Double totalDelayHours;//总计小时数
	private Double usedDelayHours;//已使用小时数
	private Double lockedDelayHours;//锁定小时数
	private Integer status;//复核状态
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
	public Date getDelayMonth() {
		return delayMonth;
	}
	public void setDelayMonth(Date delayMonth) {
		this.delayMonth = delayMonth;
	}
	public Double getTotalDelayHours() {
		return totalDelayHours;
	}
	public void setTotalDelayHours(Double totalDelayHours) {
		this.totalDelayHours = totalDelayHours;
	}
	public Double getUsedDelayHours() {
		return usedDelayHours;
	}
	public void setUsedDelayHours(Double usedDelayHours) {
		this.usedDelayHours = usedDelayHours;
	}
	public Double getLockedDelayHours() {
		return lockedDelayHours;
	}
	public void setLockedDelayHours(Double lockedDelayHours) {
		this.lockedDelayHours = lockedDelayHours;
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
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
}
