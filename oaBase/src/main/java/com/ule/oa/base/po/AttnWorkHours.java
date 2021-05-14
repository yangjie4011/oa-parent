package com.ule.oa.base.po;


import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.AttnWorkHoursTbl;

@JsonInclude(Include.NON_NULL)
public class AttnWorkHours extends AttnWorkHoursTbl{

	private static final long serialVersionUID = -9018616472035731390L;
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date countDate;//考勤统计日期
    private List<Long> employeeIds;//批量查询时，存放批量员工id
    private String dataTypes;//合并和的数据类型字符串，以逗号隔开
    
    private String startTimeStr;
    private String endTimeStr;
    
	public String getStartTimeStr() {
		return startTimeStr;
	}

	public void setStartTimeStr(String startTimeStr) {
		this.startTimeStr = startTimeStr;
	}

	public String getEndTimeStr() {
		return endTimeStr;
	}

	public void setEndTimeStr(String endTimeStr) {
		this.endTimeStr = endTimeStr;
	}

	public Date getCountDate() {
		return countDate;
	}

	public void setCountDate(Date countDate) {
		this.countDate = countDate;
	}

	public List<Long> getEmployeeIds() {
		return employeeIds;
	}

	public void setEmployeeIds(List<Long> employeeIds) {
		this.employeeIds = employeeIds;
	}

	public String getDataTypes() {
		return dataTypes;
	}

	public void setDataTypes(String dataTypes) {
		this.dataTypes = dataTypes;
	}  
	
	
}