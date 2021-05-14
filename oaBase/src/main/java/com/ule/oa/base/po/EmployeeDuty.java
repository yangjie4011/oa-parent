package com.ule.oa.base.po;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.EmployeeDutyTbl;

/**
 * @ClassName: 员工值班表
 * @Description: 员工值班表
 * @author yangjie
 * @date 2018年1月9日
 */
@JsonInclude(Include.NON_NULL)
public class EmployeeDuty extends EmployeeDutyTbl { 

	private static final long serialVersionUID = -6310232806336810606L;
	
	private Integer employDutyCount;//值班人数
	private String departName;
	private String employCode;//员工编号
	private String weekDay;//星期
	private List<Integer> sourceList;//来源list

	public Integer getEmployDutyCount() {
		return employDutyCount;
	}

	public void setEmployDutyCount(Integer employDutyCount) {
		this.employDutyCount = employDutyCount;
	}

	public String getDepartName() {
		return departName;
	}

	public void setDepartName(String departName) {
		this.departName = departName;
	}

	public String getEmployCode() {
		return employCode;
	}

	public void setEmployCode(String employCode) {
		this.employCode = employCode;
	}

	public String getWeekDay() {
		return weekDay;
	}

	public void setWeekDay(String weekDay) {
		this.weekDay = weekDay;
	}

	public List<Integer> getSourceList() {
		return sourceList;
	}

	public void setSourceList(List<Integer> sourceList) {
		this.sourceList = sourceList;
	}
	

}
