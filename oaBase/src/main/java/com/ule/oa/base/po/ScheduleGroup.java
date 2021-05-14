package com.ule.oa.base.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.ScheduleGroupTbl;

@JsonInclude(Include.NON_NULL)
public class ScheduleGroup extends ScheduleGroupTbl{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7927458898596586558L;
	
	//员工总数
	private Integer empCount;
	
	//排班人姓名
	private String schedulerName;
	
	//排班审核人姓名
	private String auditorName;
	
	private String departName;
	public Integer getEmpCount() {
		return empCount;
	}

	public void setEmpCount(Integer empCount) {
		this.empCount = empCount;
	}

	public String getSchedulerName() {
		return schedulerName;
	}

	public void setSchedulerName(String schedulerName) {
		this.schedulerName = schedulerName;
	}

	public String getAuditorName() {
		return auditorName;
	}

	public void setAuditorName(String auditorName) {
		this.auditorName = auditorName;
	}

	public String getDepartName() {
		return departName;
	}

	public void setDepartName(String departName) {
		this.departName = departName;
	}

}
