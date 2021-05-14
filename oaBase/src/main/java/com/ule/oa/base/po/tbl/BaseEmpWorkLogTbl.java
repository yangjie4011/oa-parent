package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 员工工作日志
 * @author yangjie
 *
 */
public class BaseEmpWorkLogTbl implements Serializable{

	private static final long serialVersionUID = -8594366282964303187L;
	
	private Long id;
	
	private Long employeeId;
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date workDate;
	
	private String workContent;
	
	private String nextDayWorkPlan;
	
	private String workProblem;
	
	private Date createTime;
	
	private String createUser;
	
	private Date updateTime;
	
	private String updateUser;
	
	private Integer approvalStatus;
	
	private Integer delFlag;
	
	private String processId;

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
	public Date getWorkDate() {
		return workDate;
	}

	public void setWorkDate(Date workDate) {
		this.workDate = workDate;
	}

	public String getWorkContent() {
		return workContent;
	}

	public void setWorkContent(String workContent) {
		this.workContent = workContent;
	}

	public String getNextDayWorkPlan() {
		return nextDayWorkPlan;
	}

	public void setNextDayWorkPlan(String nextDayWorkPlan) {
		this.nextDayWorkPlan = nextDayWorkPlan;
	}

	public String getWorkProblem() {
		return workProblem;
	}

	public void setWorkProblem(String workProblem) {
		this.workProblem = workProblem;
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

	public Integer getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(Integer approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	public Integer getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}
	

}
