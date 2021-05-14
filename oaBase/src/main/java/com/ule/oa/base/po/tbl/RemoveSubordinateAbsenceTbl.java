package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ule.oa.common.po.CommonPo;

//消下属缺勤
public class RemoveSubordinateAbsenceTbl extends CommonPo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8338071939970902160L;
	
	private Long id;
	private Long employeeId;//员工
	private Long submitterId;//申请主管
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date yesterdayOffTime;//前一天下班时间
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date attendanceDate;//考勤日期
	private String attendanceHour;//考勤时间
	private Double removeAbsenceHours;//消缺勤小时数
	private String removeAbsenceReason;//消缺勤理由
	private Long approalStatus;//审批状态：100-待审批 、200-已审批 、300-已拒
	private String approalReason;//审批意见
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date createTime;
	private String createUser;
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date updateTime;
	private String updateUser;
	private Integer delFlag;
	private String processinstanceId;
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
	public Long getSubmitterId() {
		return submitterId;
	}
	public void setSubmitterId(Long submitterId) {
		this.submitterId = submitterId;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getYesterdayOffTime() {
		return yesterdayOffTime;
	}
	public void setYesterdayOffTime(Date yesterdayOffTime) {
		this.yesterdayOffTime = yesterdayOffTime;
	}
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	public Date getAttendanceDate() {
		return attendanceDate;
	}
	public void setAttendanceDate(Date attendanceDate) {
		this.attendanceDate = attendanceDate;
	}
	public String getAttendanceHour() {
		return attendanceHour;
	}
	public void setAttendanceHour(String attendanceHour) {
		this.attendanceHour = attendanceHour;
	}
	public Double getRemoveAbsenceHours() {
		return removeAbsenceHours;
	}
	public void setRemoveAbsenceHours(Double removeAbsenceHours) {
		this.removeAbsenceHours = removeAbsenceHours;
	}
	public String getRemoveAbsenceReason() {
		return removeAbsenceReason;
	}
	public void setRemoveAbsenceReason(String removeAbsenceReason) {
		this.removeAbsenceReason = removeAbsenceReason;
	}
	public Long getApproalStatus() {
		return approalStatus;
	}
	public void setApproalStatus(Long approalStatus) {
		this.approalStatus = approalStatus;
	}
	public String getApproalReason() {
		return approalReason;
	}
	public void setApproalReason(String approalReason) {
		this.approalReason = approalReason;
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
	public String getProcessinstanceId() {
		return processinstanceId;
	}
	public void setProcessinstanceId(String processinstanceId) {
		this.processinstanceId = processinstanceId;
	}
	
}
