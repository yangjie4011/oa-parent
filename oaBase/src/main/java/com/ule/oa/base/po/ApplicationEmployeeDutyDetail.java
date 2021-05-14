package com.ule.oa.base.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.ApplicationEmployeeDutyDetailTbl;

/**
 * @ClassName: 值班申请明细表
 * @Description: 值班申请明细表
 * @author yangjie
 * @date 2017年8月30日
 */
@JsonInclude(Include.NON_NULL)
public class ApplicationEmployeeDutyDetail extends ApplicationEmployeeDutyDetailTbl{

	private static final long serialVersionUID = 7378188634801068080L;
	
	private String employeeNames;
	
	private String weekStr;//日期为星期几
	
	private String names;
	
	private String codes;
	
	private String departName;
	
	private Long approvalStatus;
	
	private String vacationName;
	
	private String year;
	
	private Long departId;
	
	private String startHours;
	
	private String endHours;

	public String getNames() {
		return names;
	}

	public void setNames(String names) {
		this.names = names;
	}

	public String getCodes() {
		return codes;
	}

	public void setCodes(String codes) {
		this.codes = codes;
	}

	public String getEmployeeNames() {
		return employeeNames;
	}

	public void setEmployeeNames(String employeeNames) {
		this.employeeNames = employeeNames;
	}

	public String getWeekStr() {
		return weekStr;
	}

	public void setWeekStr(String weekStr) {
		this.weekStr = weekStr;
	}

	public String getDepartName() {
		return departName;
	}

	public void setDepartName(String departName) {
		this.departName = departName;
	}

	public Long getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(Long approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	public String getVacationName() {
		return vacationName;
	}

	public void setVacationName(String vacationName) {
		this.vacationName = vacationName;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public Long getDepartId() {
		return departId;
	}

	public void setDepartId(Long departId) {
		this.departId = departId;
	}

	public String getStartHours() {
		return startHours;
	}

	public void setStartHours(String startHours) {
		this.startHours = startHours;
	}

	public String getEndHours() {
		return endHours;
	}

	public void setEndHours(String endHours) {
		this.endHours = endHours;
	}
	
}
