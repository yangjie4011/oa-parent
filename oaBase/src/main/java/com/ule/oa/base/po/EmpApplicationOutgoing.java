package com.ule.oa.base.po;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.EmpApplicationOutgoingTbl;

/**
 * @ClassName: 外出申请
 * @Description: 外出申请
 * @author yangjie
 * @date 2017年6月9日
 */
@JsonInclude(Include.NON_NULL)
public class EmpApplicationOutgoing extends EmpApplicationOutgoingTbl {

	private static final long serialVersionUID = -2509818214267365920L;
	
	public static final int APPROVAL_STATUS_WAIT = 100;//待审批
	public static final int APPROVAL_STATUS_YES = 200;//已审批
	public static final int APPROVAL_STATUS_NO = 300;//已拒
	
	private String nodeCode;//节点CODE
	
	private Integer page;
	private Integer rows;
	private String token;
	private String workType;//工时类型
	private List<Integer> departList;//部门
	private Integer empTypeId;//员工类型
	private Integer whetherScheduling;//是否排班
	private String auditUser;//审核人
	private String approvalStatusDesc;//审核状态
	private Integer firstDepart;//页面上一级部门
	private Integer secondDepart;//页面上二级部门
	
	private List<Long> currentUserDepart;//当前用户所拥有的部门权限
	private List<Long> subEmployeeIdList;//下属员工

	private Integer year;//页面上年份
	private Integer times;//出差次数

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

	public String getApprovalStatusDesc() {
		return approvalStatusDesc;
	}

	public void setApprovalStatusDesc(String approvalStatusDesc) {
		this.approvalStatusDesc = approvalStatusDesc;
	}

	public String getAuditUser() {
		return auditUser;
	}

	public void setAuditUser(String auditUser) {
		this.auditUser = auditUser;
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

	public List<Integer> getDepartList() {
		return departList;
	}

	public void setDepartList(List<Integer> departList) {
		this.departList = departList;
	}

	public String getWorkType() {
		return workType;
	}

	public void setWorkType(String workType) {
		this.workType = workType;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getNodeCode() {
		return nodeCode;
	}

	public void setNodeCode(String nodeCode) {
		this.nodeCode = nodeCode;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getRows() {
		return rows;
	}

	public void setRows(Integer rows) {
		this.rows = rows;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getTimes() {
		return times;
	}

	public void setTimes(Integer times) {
		this.times = times;
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
