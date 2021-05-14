package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.ule.oa.common.po.CommonPo;

/**
 * @ClassName: 晚到申请
 * @Description: 晚到申请
 * @author yangjie
 * @date 2017年6月19日
 */
public class EmpApplicationOvertimeLateTbl extends CommonPo implements Serializable{
	
	private static final long serialVersionUID = -8707155892502243888L;
	
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
	//延时工作日期
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date overTimeDate;
	//延时工作开始时间
	@DateTimeFormat(pattern="HH:mm:ss")
	private Date overTimeStartTime;
	//延时工作结束时间
	@DateTimeFormat(pattern="HH:mm:ss")
	private Date overTimeEndTime;
	//请假事由
	private String reason;
	//上班打卡
	private Date startWorkTime;
	//下班打卡
	private Date endWorkTime;
	//允许晚到时间
	private String allowTime;
	//实际晚到时间
	private String actualTime;
	//审批状态：100-待审批 、200-已审批 、300-已拒
	private Integer approvalStatus;
	//审批意见
	private String approvalReason;
	
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
	public Date getSubmitDate() {
		return submitDate;
	}
	public void setSubmitDate(Date submitDate) {
		this.submitDate = submitDate;
	}
	public Date getStartWorkTime() {
		return startWorkTime;
	}
	public void setStartWorkTime(Date startWorkTime) {
		this.startWorkTime = startWorkTime;
	}
	public Date getEndWorkTime() {
		return endWorkTime;
	}
	public void setEndWorkTime(Date endWorkTime) {
		this.endWorkTime = endWorkTime;
	}
	public String getAllowTime() {
		return allowTime;
	}
	public void setAllowTime(String allowTime) {
		this.allowTime = allowTime;
	}
	public String getActualTime() {
		return actualTime;
	}
	public void setActualTime(String actualTime) {
		this.actualTime = actualTime;
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
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public Date getOverTimeDate() {
		return overTimeDate;
	}
	public void setOverTimeDate(Date overTimeDate) {
		this.overTimeDate = overTimeDate;
	}
	public Date getOverTimeStartTime() {
		return overTimeStartTime;
	}
	public void setOverTimeStartTime(Date overTimeStartTime) {
		this.overTimeStartTime = overTimeStartTime;
	}
	public Date getOverTimeEndTime() {
		return overTimeEndTime;
	}
	public void setOverTimeEndTime(Date overTimeEndTime) {
		this.overTimeEndTime = overTimeEndTime;
	}

}
