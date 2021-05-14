package com.ule.oa.base.po;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.EmpApplicationLeaveAbolishTbl;

/**
 * @ClassName: 销假申请
 * @Description: 销假申请
 * @author yangjie
 * @date 2018年11月23日
 */
@JsonInclude(Include.NON_NULL)
public class EmpApplicationLeaveAbolish extends EmpApplicationLeaveAbolishTbl {

	private static final long serialVersionUID = -1971432423162109260L;
	
	private String token;

	private String nodeCode;// 节点CODE
	private Date applyDate;// 请假时间

	private List<Integer> departList;// 部门
	private Long firstDepart;
	private Integer empTypeId;// 员工类型
	private Integer whetherScheduling;// 是否排班
	private String auditUser;// 审核人
	private String workType;// 工时类型
	private Integer leaveType;// 请假类型
	
	private List<Long> currentUserDepart;//当前用户所拥有的部门权限	
	private List<Long> subEmployeeIdList;//下属员工

	public List<Long> getCurrentUserDepart() {
		return currentUserDepart;
	}

	public void setCurrentUserDepart(List<Long> currentUserDepart) {
		this.currentUserDepart = currentUserDepart;
	}
	
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
    
	
	
	
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date startTimeDetail;//详情开始时间
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date endTimeDetail;//详情结束时间
	private Double leaveDaysDetail;//详情天数
	private Double leaveHoursDetail;//详情小时	
	
	private String actualEndTime;//销假结束时间
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date applyStartDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date applyEndDate;
	private String assignee;//查询条件:请假申请的当前审批人

	
	
	
	
	
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

	public String getActualEndTime() {
		return actualEndTime;
	}

	public void setActualEndTime(String actualEndTime) {
		this.actualEndTime = actualEndTime;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getStartTimeDetail() {
		return startTimeDetail;
	}

	public void setStartTimeDetail(Date startTimeDetail) {
		this.startTimeDetail = startTimeDetail;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getEndTimeDetail() {
		return endTimeDetail;
	}

	public void setEndTimeDetail(Date endTimeDetail) {
		this.endTimeDetail = endTimeDetail;
	}

	public Double getLeaveDaysDetail() {
		return leaveDaysDetail;
	}

	public void setLeaveDaysDetail(Double leaveDaysDetail) {
		this.leaveDaysDetail = leaveDaysDetail;
	}

	public Double getLeaveHoursDetail() {
		return leaveHoursDetail;
	}

	public void setLeaveHoursDetail(Double leaveHoursDetail) {
		this.leaveHoursDetail = leaveHoursDetail;
	}

	public Long getFirstDepart() {
		return firstDepart;
	}

	public void setFirstDepart(Long firstDepart) {
		this.firstDepart = firstDepart;
	}
  
	public String getNodeCode() {
		return nodeCode;
	}

	public void setNodeCode(String nodeCode) {
		this.nodeCode = nodeCode;
	}

	public Date getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}

	public List<Integer> getDepartList() {
		return departList;
	}

	public void setDepartList(List<Integer> departList) {
		this.departList = departList;
	}

	public Integer getEmpTypeId() {
		return empTypeId;
	}

	public void setEmpTypeId(Integer empTypeId) {
		this.empTypeId = empTypeId;
	}

	public Integer getWhetherScheduling() {
		return whetherScheduling;
	}

	public void setWhetherScheduling(Integer whetherScheduling) {
		this.whetherScheduling = whetherScheduling;
	}

	public String getAuditUser() {
		return auditUser;
	}

	public void setAuditUser(String auditUser) {
		this.auditUser = auditUser;
	}

	public String getWorkType() {
		return workType;
	}

	public void setWorkType(String workType) {
		this.workType = workType;
	}

	public Integer getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(Integer leaveType) {
		this.leaveType = leaveType;
	}

	public Date getApplyStartDate() {
		return applyStartDate;
	}

	public void setApplyStartDate(Date applyStartDate) {
		this.applyStartDate = applyStartDate;
	}

	public Date getApplyEndDate() {
		return applyEndDate;
	}

	public void setApplyEndDate(Date applyEndDate) {
		this.applyEndDate = applyEndDate;
	}

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public List<Long> getSubEmployeeIdList() {
		return subEmployeeIdList;
	}

	public void setSubEmployeeIdList(List<Long> subEmployeeIdList) {
		this.subEmployeeIdList = subEmployeeIdList;
	}
	

}
