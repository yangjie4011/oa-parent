package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ule.oa.common.po.CommonPo;

/**
 * @ClassName: 考勤异常消除申请
 * @Description: 考勤异常消除申请
 * @author yangjie
 * @date 2017年6月15日
 */
public class EmpApplicationAbnormalAttendanceTbl extends CommonPo implements Serializable{

	private static final long serialVersionUID = -261730750256397367L;
	
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
	//工时类型
	private Integer workType;
	//开始时间
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date startTime;
	//结束时间
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date endTime;
	//打卡开始时间
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date startPunchTime;
	//打卡结束时间
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date endPunchTime;
	//异常考勤日期
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date abnormalDate;
	//申诉理由
	private String reason;
	//审批状态：100-待审批 、200-已审批 、300-已拒
	private Integer approvalStatus;
	//审批意见
	private String approvalReason;
	//代申诉人ID
	private Long agentId;
	//代申诉人ID
	private String agentName;
	//申请类型(0:异常考勤，1-下属异常考勤)
	private Integer applyType;
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
	public Integer getWorkType() {
		return workType;
	}
	public void setWorkType(Integer workType) {
		this.workType = workType;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getStartPunchTime() {
		return startPunchTime;
	}
	public void setStartPunchTime(Date startPunchTime) {
		this.startPunchTime = startPunchTime;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getEndPunchTime() {
		return endPunchTime;
	}
	public void setEndPunchTime(Date endPunchTime) {
		this.endPunchTime = endPunchTime;
	}
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	public Date getAbnormalDate() {
		return abnormalDate;
	}
	public void setAbnormalDate(Date abnormalDate) {
		this.abnormalDate = abnormalDate;
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
	public Long getAgentId() {
		return agentId;
	}
	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}
	public String getAgentName() {
		return agentName;
	}
	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}
	public Integer getApplyType() {
		return applyType;
	}
	public void setApplyType(Integer applyType) {
		this.applyType = applyType;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

}
