package com.ule.oa.base.po;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.ApplicationEmployeeDutyTbl;

/**
 * @ClassName: 值班申请表
 * @Description: 值班申请表
 * @author yangjie
 * @date 2017年8月31日
 */
@JsonInclude(Include.NON_NULL)
public class ApplicationEmployeeDuty extends ApplicationEmployeeDutyTbl{

	private static final long serialVersionUID = -4414491245188552485L;
	
	private String token;
	
	private List<Long> departIds;
	
	private String parentDepartName;
	
	private String employeeNames;
	
	private List<Integer> typeList;
	
	private List<Integer> approvalStatusList;
	
	private String hrAuditor;
	
	private String assignee;//当前登录用户

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public List<Long> getDepartIds() {
		return departIds;
	}

	public void setDepartIds(List<Long> departIds) {
		this.departIds = departIds;
	}

	public String getParentDepartName() {
		return parentDepartName;
	}

	public void setParentDepartName(String parentDepartName) {
		this.parentDepartName = parentDepartName;
	}

	public String getEmployeeNames() {
		return employeeNames;
	}

	public void setEmployeeNames(String employeeNames) {
		this.employeeNames = employeeNames;
	}

	public List<Integer> getTypeList() {
		return typeList;
	}

	public void setTypeList(List<Integer> typeList) {
		this.typeList = typeList;
	}

	public List<Integer> getApprovalStatusList() {
		return approvalStatusList;
	}

	public void setApprovalStatusList(List<Integer> approvalStatusList) {
		this.approvalStatusList = approvalStatusList;
	}

	public String getHrAuditor() {
		return hrAuditor;
	}

	public void setHrAuditor(String hrAuditor) {
		this.hrAuditor = hrAuditor;
	}

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	
}
