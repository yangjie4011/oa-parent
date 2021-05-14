package com.ule.oa.base.po;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.ule.oa.base.po.tbl.EmpApplicationAbnormalAttendanceTbl;

/**
 * @ClassName: 考勤异常消除申请
 * @Description: 考勤异常消除申请
 * @author yangjie
 * @date 2017年6月15日
 */
public class EmpApplicationAbnormalAttendance extends
		EmpApplicationAbnormalAttendanceTbl {

	private static final long serialVersionUID = -3684121253625861935L;
	
	private Date monthStart;
	
	private Date monthEnd;
	
	private String nodeCode;//节点CODE
	
	private String token;
	
	private String applyName;//申请人姓名
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date applyStartDate;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date applyEndDate;

	private List<Integer> departList;//部门
	private Integer empTypeId;//员工类型
	private Integer whetherScheduling;//是否排班
	private String auditUser;//审核人
	private String hrAuditUser;
	private String approvalStatusDesc;//审核状态
	private Integer firstDepart;//页面上一级部门
	private Integer secondDepart;//页面上二级部门
	private String workType1;//工时类型
	private String startResult;//上班考勤结果
	private String endResult;//下班考勤结果
	private String assignee;//查询条件:请假申请的当前审批人
	private List<Long> currentUserDepart;//当前用户所拥有的部门权限	
	private List<Long> subEmployeeIdList;//下属员工

	public List<Long> getCurrentUserDepart() {
		return currentUserDepart;
	}

	public void setCurrentUserDepart(List<Long> currentUserDepart) {
		this.currentUserDepart = currentUserDepart;
	}
	
	public String getNodeCode() {
		return nodeCode;
	}

	public void setNodeCode(String nodeCode) {
		this.nodeCode = nodeCode;
	}

	public Date getMonthStart() {
		return monthStart;
	}

	public void setMonthStart(Date monthStart) {
		this.monthStart = monthStart;
	}

	public Date getMonthEnd() {
		return monthEnd;
	}

	public void setMonthEnd(Date monthEnd) {
		this.monthEnd = monthEnd;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
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

	public String getApprovalStatusDesc() {
		return approvalStatusDesc;
	}

	public void setApprovalStatusDesc(String approvalStatusDesc) {
		this.approvalStatusDesc = approvalStatusDesc;
	}

	public Integer getFirstDepart() {
		return firstDepart;
	}

	public void setFirstDepart(Integer firstDepart) {
		this.firstDepart = firstDepart;
	}

	public Integer getSecondDepart() {
		return secondDepart;
	}

	public void setSecondDepart(Integer secondDepart) {
		this.secondDepart = secondDepart;
	}

	public String getApplyName() {
		return applyName;
	}

	public void setApplyName(String applyName) {
		this.applyName = applyName;
	}

	public String getWorkType1() {
		return workType1;
	}

	public void setWorkType1(String workType1) {
		this.workType1 = workType1;
	}

	public String getStartResult() {
		return startResult;
	}

	public void setStartResult(String startResult) {
		this.startResult = startResult;
	}

	public String getEndResult() {
		return endResult;
	}

	public void setEndResult(String endResult) {
		this.endResult = endResult;
	}

	public String getHrAuditUser() {
		return hrAuditUser;
	}

	public void setHrAuditUser(String hrAuditUser) {
		this.hrAuditUser = hrAuditUser;
	}

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	public List<Long> getSubEmployeeIdList() {
		return subEmployeeIdList;
	}

	public void setSubEmployeeIdList(List<Long> subEmployeeIdList) {
		this.subEmployeeIdList = subEmployeeIdList;
	}

	
	
}
