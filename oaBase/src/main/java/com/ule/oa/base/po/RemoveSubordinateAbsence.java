package com.ule.oa.base.po;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ule.oa.base.po.tbl.RemoveSubordinateAbsenceTbl;
public class RemoveSubordinateAbsence extends RemoveSubordinateAbsenceTbl{


	/**
	 * 
	 */
	private static final long serialVersionUID = 5530862158825166188L;
	
	//员工编号
	private String empCode;
	//员工姓名
	private String empName;
	//申请主管姓名
	private String submitterName;
	//出勤多余小时数
	private Double overHoursOfAttendance;
	//部门id
	private Long departId;
	//考勤开始时间
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date attendanceStartDate;
	//考勤结束时间
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date attendanceEndDate;
	private String auditUser;
	
	private String departName;
	
	private String leaderName;
	
	private Long attnOverHours;
	
	private Integer softType;//排序类型 // 代办——申请时间排序 1 失效——申请时间排序1，最近在最前,查询——考勤日期排序      申请时间=创建时间  审批时间查已办已经sql查询 
	
	
	private List<Long> currentUserDepart;//当前用户所拥有的部门权限	
	private List<Long> subEmployeeIdList;//下属员工
	private List<Long> approalStatusList;//状态集合
	
	//待办人
	private String assignee;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date startTime;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date endTime;
	
	
	
	public Integer getSoftType() {
		return softType;
	}

	public void setSoftType(Integer softType) {
		this.softType = softType;
	}

	public List<Long> getApproalStatusList() {
		return approalStatusList;
	}

	public void setApproalStatusList(List<Long> approalStatusList) {
		this.approalStatusList = approalStatusList;
	}

	public String getDepartName() {
		return departName;
	}

	public void setDepartName(String departName) {
		this.departName = departName;
	}

	public String getLeaderName() {
		return leaderName;
	}

	public void setLeaderName(String leaderName) {
		this.leaderName = leaderName;
	}

	public Long getAttnOverHours() {
		return attnOverHours;
	}

	public void setAttnOverHours(Long attnOverHours) {
		this.attnOverHours = attnOverHours;
	}

	public String getAuditUser() {
		return auditUser;
	}

	public void setAuditUser(String auditUser) {
		this.auditUser = auditUser;
	}
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getEmpCode() {
		return empCode;
	}

	public void setEmpCode(String empCode) {
		this.empCode = empCode;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public Long getDepartId() {
		return departId;
	}

	public void setDepartId(Long departId) {
		this.departId = departId;
	}

	public Date getAttendanceStartDate() {
		return attendanceStartDate;
	}

	public void setAttendanceStartDate(Date attendanceStartDate) {
		this.attendanceStartDate = attendanceStartDate;
	}

	public Date getAttendanceEndDate() {
		return attendanceEndDate;
	}

	public void setAttendanceEndDate(Date attendanceEndDate) {
		this.attendanceEndDate = attendanceEndDate;
	}

	private String fontColor="";

	public String getFontColor() {
		return fontColor;
	}

	public void setFontColor(String fontColor) {
		this.fontColor = fontColor;
	}

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	public String getSubmitterName() {
		return submitterName;
	}

	public void setSubmitterName(String submitterName) {
		this.submitterName = submitterName;
	}

	public Double getOverHoursOfAttendance() {
		return overHoursOfAttendance;
	}

	public void setOverHoursOfAttendance(Double overHoursOfAttendance) {
		this.overHoursOfAttendance = overHoursOfAttendance;
	}

	public List<Long> getCurrentUserDepart() {
		return currentUserDepart;
	}

	public void setCurrentUserDepart(List<Long> currentUserDepart) {
		this.currentUserDepart = currentUserDepart;
	}

	public List<Long> getSubEmployeeIdList() {
		return subEmployeeIdList;
	}

	public void setSubEmployeeIdList(List<Long> subEmployeeIdList) {
		this.subEmployeeIdList = subEmployeeIdList;
	}

	
	
}
