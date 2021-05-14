package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ule.oa.common.po.CommonPo;

/**
 * @ClassName: 延时工作申请
 * @Description: 延时工作申请
 * @author yangjie
 * @date 2017年6月12日
 */
public class EmpApplicationOvertimeTbl extends CommonPo implements Serializable{

	private static final long serialVersionUID = -1456003890180018294L;
	
	private Long id;
	//员工ID
	private Long employeeId;
	//员工姓名
	private String cnName;
	//员工编号
	private String code;
	//部门
	private Long departId;
	//部门名称
	private String departName;
	//职位名称
	private String positionName;
	//职位ID
	private Long positionId;
	//提交日期
	private Date submitDate;
	//预计开始时间
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date expectStartTime;
	//预计结束时间
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date expectEndTime;
	//预计工作时长
	private Double expectDuration;
	//实际开始时间
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date actualStartTime;
	//实际结束时间
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date actualEndTime;
	//实际工作时长
	private Double actualDuration;
	//申请事由:100-项目、200-会议、300-日常工作、400-其他
	private Integer applyType;
	//申请日期
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date applyDate;
	//项目名称
	private String projectName;
	//项目ID
	private Long projectId;
	//事由说明
	private String reason;
	//审批状态：100-待审批 、200-已审批 、300-已拒
	private Integer approvalStatus;
	//审批意见
	private String approvalReason;
	//流程实例id
 	private String processInstanceId;
	
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
	public String getPositionName() {
		return positionName;
	}
	public void setPositionName(String positionName) {
		this.positionName = positionName;
	}
	public Long getPositionId() {
		return positionId;
	}
	public void setPositionId(Long positionId) {
		this.positionId = positionId;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getSubmitDate() {
		return submitDate;
	}
	public void setSubmitDate(Date submitDate) {
		this.submitDate = submitDate;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getExpectStartTime() {
		return expectStartTime;
	}
	public void setExpectStartTime(Date expectStartTime) {
		this.expectStartTime = expectStartTime;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getExpectEndTime() {
		return expectEndTime;
	}
	public void setExpectEndTime(Date expectEndTime) {
		this.expectEndTime = expectEndTime;
	}
	public Double getExpectDuration() {
		return expectDuration;
	}
	public void setExpectDuration(Double expectDuration) {
		this.expectDuration = expectDuration;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getActualStartTime() {
		return actualStartTime;
	}
	public void setActualStartTime(Date actualStartTime) {
		this.actualStartTime = actualStartTime;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getActualEndTime() {
		return actualEndTime;
	}
	public void setActualEndTime(Date actualEndTime) {
		this.actualEndTime = actualEndTime;
	}
	public Double getActualDuration() {
		return actualDuration;
	}
	public void setActualDuration(Double actualDuration) {
		this.actualDuration = actualDuration;
	}
	public Integer getApplyType() {
		return applyType;
	}
	public void setApplyType(Integer applyType) {
		this.applyType = applyType;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public Integer getApprovalStatus() {
		return approvalStatus;
	}
	public void setApprovalStatus(Integer approvalStatus) {
		this.approvalStatus = approvalStatus;
	}
	public String getApprovalReason() {
		return approvalReason;
	}
	public void setApprovalReason(String approvalReason) {
		this.approvalReason = approvalReason;
	}
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	public Date getApplyDate() {
		return applyDate;
	}
	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

}
