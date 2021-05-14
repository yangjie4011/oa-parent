package com.ule.oa.base.po;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.AttnStatisticsTbl;

@JsonInclude(Include.NON_NULL)
public class AttnStatistics extends AttnStatisticsTbl {

	  /**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = -7471548144847111411L;
	
	//查询条件
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date startTime;
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date endTime;
	
    private Long reportToLeader;//汇报对象
	
    private Long departId;//部门ID
	
	private String startWorkTimeStr;
	
	private String endWorkTimeStr;
	
	private String workTypeName;//工时类型名称
	
	private String nameOrCode;//查询条件，编号或者姓名
	
	private String type;//用来接收异常考勤类型，迟到，早退，缺勤，旷工
	
	private String leaveType;//请假类型
	
	private Integer exTimes;//异常考勤次数
	
    private List<Long> employeeIds;//批量查询时，存放批量员工id
    
    private Double actAttnTimeCount;//实际出勤工时总数
    
    private List<Long> empTypeIdList;//员工类型集合
	
	
	public List<Long> getEmployeeIds() {
		return employeeIds;
	}
	public void setEmployeeIds(List<Long> employeeIds) {
		this.employeeIds = employeeIds;
	}
	public String getLeaveType() {
		return leaveType;
	}
	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}
	public Integer getExTimes() {
		return exTimes;
	}
	public void setExTimes(Integer exTimes) {
		this.exTimes = exTimes;
	}
	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Long getReportToLeader() {
		return reportToLeader;
	}
	public void setReportToLeader(Long reportToLeader) {
		this.reportToLeader = reportToLeader;
	}
	public String getStartWorkTimeStr() {
		return startWorkTimeStr;
	}
	public void setStartWorkTimeStr(String startWorkTimeStr) {
		this.startWorkTimeStr = startWorkTimeStr;
	}
	public Long getDepartId() {
		return departId;
	}
	public void setDepartId(Long departId) {
		this.departId = departId;
	}
	public String getWorkTypeName() {
		return workTypeName;
	}
	public void setWorkTypeName(String workTypeName) {
		this.workTypeName = workTypeName;
	}
	public String getEndWorkTimeStr() {
		return endWorkTimeStr;
	}
	public void setEndWorkTimeStr(String endWorkTimeStr) {
		this.endWorkTimeStr = endWorkTimeStr;
	}
	public String getNameOrCode() {
		return nameOrCode;
	}
	public void setNameOrCode(String nameOrCode) {
		this.nameOrCode = nameOrCode;
	}
	public Double getActAttnTimeCount() {
		return actAttnTimeCount;
	}
	public void setActAttnTimeCount(Double actAttnTimeCount) {
		this.actAttnTimeCount = actAttnTimeCount;
	}
	public List<Long> getEmpTypeIdList() {
		return empTypeIdList;
	}
	public void setEmpTypeIdList(List<Long> empTypeIdList) {
		this.empTypeIdList = empTypeIdList;
	}
	
}
