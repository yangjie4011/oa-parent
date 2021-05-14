package com.ule.oa.base.po;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.EmpApplicationLeaveTbl;

/**
 * @ClassName: 请假申请
 * @Description: 请假申请
 * @author yangjie
 * @date 2017年6月14日
 */
@JsonInclude(Include.NON_NULL)
public class EmpApplicationLeave extends EmpApplicationLeaveTbl {

	private static final long serialVersionUID = 1286754691618370356L;

	public static final int APPROVAL_STATUS_WAIT = 100;// 待审批
	public static final int APPROVAL_STATUS_YES = 200;// 已审批
	public static final int APPROVAL_STATUS_NO = 300;// 已拒

	private String nodeCode;// 节点CODE
	private Date applyDate;// 请假时间

	private List<Integer> departList;// 部门
	private Integer empTypeId;// 员工类型
	private Integer whetherScheduling;// 是否排班
	private String auditUser;// 审核人
	private Integer firstDepart;// 页面上一级部门
	private Integer secondDepart;// 页面上二级部门
	private String workType;// 工时类型
	private Double leaveDays;// 请假天数
	private Double leaveHours;// 请假小时数
	private Integer leaveType;// 请假类型
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date applyStartDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date applyEndDate;
	private String assignee;//查询条件:请假申请的当前审批人
	private List<Long> currentUserDepart;//当前用户所拥有的部门权限	
	private List<Long> subEmployeeIdList;//所属下属员工id列表

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

	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
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

	public String getWorkType() {
		return workType;
	}

	public void setWorkType(String workType) {
		this.workType = workType;
	}

	public Double getLeaveDays() {
		return leaveDays;
	}

	public void setLeaveDays(Double leaveDays) {
		this.leaveDays = leaveDays;
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

	public Double getLeaveHours() {
		return leaveHours;
	}

	public void setLeaveHours(Double leaveHours) {
		this.leaveHours = leaveHours;
	}

	public List<Long> getSubEmployeeIdList() {
		return subEmployeeIdList;
	}

	public void setSubEmployeeIdList(List<Long> subEmployeeIdList) {
		this.subEmployeeIdList = subEmployeeIdList;
	}
	
}
