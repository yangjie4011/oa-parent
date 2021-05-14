package com.ule.oa.base.po;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.EmployeeClassTbl;

@JsonInclude(Include.NON_NULL)
public class EmployeeClass extends EmployeeClassTbl {

	  /**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = -7242328909327096696L;
	
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startTime;//排班开始时间

	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endTime;//排班结束时间
	
	private String name;//班次名称
    
    private Integer isInterDay;//是否跨天

    private Double mustAttnTime;//排班出勤时间

    private List<Long> employeeIds;//批量查询时，存放批量员工id
    private Long departId;//部门id
    private Integer alreadyClassSettingCount;//已经排班的人数
    private Integer mustClassSettingCount;//应该排班人数
    private Integer daysCount;//排班天数    
    private Long empClassId;//排班表id
    
    public Long getEmpClassId() {
		return empClassId;
	}

	public void setEmpClassId(Long empClassId) {
		this.empClassId = empClassId;
	}

	public Integer getMustClassSettingCount() {
		return mustClassSettingCount;
	}

	public void setMustClassSettingCount(Integer mustClassSettingCount) {
		this.mustClassSettingCount = mustClassSettingCount;
	}

	public Integer getAlreadyClassSettingCount() {
		return alreadyClassSettingCount;
	}

	public void setAlreadyClassSettingCount(Integer alreadyClassSettingCount) {
		this.alreadyClassSettingCount = alreadyClassSettingCount;
	}

	public Long getDepartId() {
		return departId;
	}

	public void setDepartId(Long departId) {
		this.departId = departId;
	}

	public List<Long> getEmployeeIds() {
		return employeeIds;
	}

	public void setEmployeeIds(List<Long> employeeIds) {
		this.employeeIds = employeeIds;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Double getMustAttnTime() {
		return mustAttnTime;
	}

	public void setMustAttnTime(Double mustAttnTime) {
		this.mustAttnTime = mustAttnTime;
	}

	public Integer getIsInterDay() {
		return isInterDay;
	}

	public void setIsInterDay(Integer isInterDay) {
		this.isInterDay = isInterDay;
	}

	public Integer getDaysCount() {
		return daysCount;
	}

	public void setDaysCount(Integer daysCount) {
		this.daysCount = daysCount;
	}


}
