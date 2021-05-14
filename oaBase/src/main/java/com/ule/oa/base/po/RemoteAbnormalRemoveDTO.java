package com.ule.oa.base.po;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ule.oa.common.po.CommonPo;

/**
 * 远程异常消除
 * @author yangjie
 *
 */
public class RemoteAbnormalRemoveDTO extends CommonPo implements Serializable{

	private static final long serialVersionUID = -3870016700870938966L;
	
	private Long id;
	
	private Long employeeId;//员工id
	
	private String cnName;//员工姓名
	
	private String code;//员工编号
	
	private Long departId;//部门id
	
	private String departName;//部门名称
	
	private Long reportToLeader;//汇报对象
	
	private String reportToLeaderName;//汇报对象名称
	
	private Long workType;//工时类型
	
	private String workTypeName;//工时类型名称
	
	private Integer approvalStatus;//审批状态
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date startDate;//开始日期
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date endDate;//结束日期
	
	private String className;//班次名称
	
	private Date classStartTime;//班次开始时间
	
	private Date classEndTime;//班次结束时间
	
	private Double mustAttnTime;
	
	private Date registerDate;//登记日期
	
	private String updateUser;//审核人
	
	private String reason;
	
	private Long empTypeId;

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public String getCnName() {
		return cnName;
	}

	public void setCnName(String cnName) {
		this.cnName = cnName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Long getDepartId() {
		return departId;
	}

	public void setDepartId(Long departId) {
		this.departId = departId;
	}

	public String getDepartName() {
		return departName;
	}

	public void setDepartName(String departName) {
		this.departName = departName;
	}

	public Long getReportToLeader() {
		return reportToLeader;
	}

	public void setReportToLeader(Long reportToLeader) {
		this.reportToLeader = reportToLeader;
	}

	public String getReportToLeaderName() {
		return reportToLeaderName;
	}

	public void setReportToLeaderName(String reportToLeaderName) {
		this.reportToLeaderName = reportToLeaderName;
	}

	public Long getWorkType() {
		return workType;
	}

	public void setWorkType(Long workType) {
		this.workType = workType;
	}

	public String getWorkTypeName() {
		return workTypeName;
	}

	public void setWorkTypeName(String workTypeName) {
		this.workTypeName = workTypeName;
	}

	public Integer getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(Integer approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	@JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
	public Date getClassStartTime() {
		return classStartTime;
	}

	public void setClassStartTime(Date classStartTime) {
		this.classStartTime = classStartTime;
	}
	
	@JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
	public Date getClassEndTime() {
		return classEndTime;
	}

	public void setClassEndTime(Date classEndTime) {
		this.classEndTime = classEndTime;
	}

	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	public Date getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Long getEmpTypeId() {
		return empTypeId;
	}

	public void setEmpTypeId(Long empTypeId) {
		this.empTypeId = empTypeId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getMustAttnTime() {
		return mustAttnTime;
	}

	public void setMustAttnTime(Double mustAttnTime) {
		this.mustAttnTime = mustAttnTime;
	}
	
	
	
	
}
