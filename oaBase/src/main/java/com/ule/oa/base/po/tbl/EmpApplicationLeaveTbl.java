package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ule.oa.common.po.CommonPo;

/**
 * @ClassName: 请假申请
 * @Description: 请假申请
 * @author yangjie
 * @date 2017年6月14日
 */
public class EmpApplicationLeaveTbl extends CommonPo implements Serializable{

	private static final long serialVersionUID = -3011617090226882837L;
	
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
	//开始时间
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date startTime;
	//结束时间
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date endTime;
	//外出时长
	private Double duration;
	//请假事由
	private String reason;
	//联系电话
	private String mobile;
	//代理人Id
	private Long agentId;
	//代理人
	private String agent;
	//代理人手机
	private String agentMobile;
	//审批状态：100-待审批 、200-已审批 、300-已拒、400-已撤销
	private Integer approvalStatus;
	//审批意见
	private String approvalReason;
	//假期类型特殊标记(一边回传产前假结束日期)0-产假，1-产前假，2-其他
	private Integer leaveTypeFlag;
	//流程实例id
	private String processInstanceId;
	//邮件抄送人
	private String toPersions;
	//抄送邮箱
	private String toEmails;
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
	public Double getDuration() {
		return duration;
	}
	public void setDuration(Double duration) {
		this.duration = duration;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getAgent() {
		return agent;
	}
	public void setAgent(String agent) {
		this.agent = agent;
	}
	public String getAgentMobile() {
		return agentMobile;
	}
	public void setAgentMobile(String agentMobile) {
		this.agentMobile = agentMobile;
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
	public Integer getLeaveTypeFlag() {
		return leaveTypeFlag;
	}
	public void setLeaveTypeFlag(Integer leaveTypeFlag) {
		this.leaveTypeFlag = leaveTypeFlag;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public Long getAgentId() {
		return agentId;
	}
	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}
	public String getToPersions() {
		return toPersions;
	}
	public void setToPersions(String toPersions) {
		this.toPersions = toPersions;
	}
	public String getToEmails() {
		return toEmails;
	}
	public void setToEmails(String toEmails) {
		this.toEmails = toEmails;
	}
	
}
