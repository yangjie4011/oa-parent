package com.ule.oa.base.po;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ule.oa.common.po.CommonPo;


public class AttentanceSetDTO extends CommonPo implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long employeeId;
	
	private String code;//员工编号
	
	private String cnName;//员工姓名
	
	private String departName;//部门名称
	
	private Long reportToLeader;//汇报对象
	
	private Long workType;//工时类型
	
	private String className;//班次名称
	
	private Date startTime;//班次开始时间
	
	private Date endTime;//班次结束时间
	
	private Integer page;
	private Integer rows;
	
	private Integer firstDepart;//页面上一级部门
	
	private String reportToLeaderName;//汇报对象
	
	private Long departId;//部门id
	
    private String month;//月份
    
    private Integer jobStatus;//在职状态
    
    private Long empTypeId;//员工类型
    
    private Long whetherScheduling;//是否排班
    
    private String workTypeName;
    
    private Integer count;
    
    private List<Long> jobStatusList;

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCnName() {
		return cnName;
	}

	public void setCnName(String cnName) {
		this.cnName = cnName;
	}

	public String getDepartName() {
		return departName;
	}

	public void setDepartName(String departName) {
		this.departName = departName;
	}

	public Long getReportToLeader() {
		return reportToLeader;
	}

	public void setReportToLeader(Long reportToLeader) {
		this.reportToLeader = reportToLeader;
	}

	public Long getWorkType() {
		return workType;
	}

	public void setWorkType(Long workType) {
		this.workType = workType;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	@JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	@JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
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

	public Integer getFirstDepart() {
		return firstDepart;
	}

	public void setFirstDepart(Integer firstDepart) {
		this.firstDepart = firstDepart;
	}

	public String getReportToLeaderName() {
		return reportToLeaderName;
	}

	public void setReportToLeaderName(String reportToLeaderName) {
		this.reportToLeaderName = reportToLeaderName;
	}

	public Long getDepartId() {
		return departId;
	}

	public void setDepartId(Long departId) {
		this.departId = departId;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public Integer getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(Integer jobStatus) {
		this.jobStatus = jobStatus;
	}

	public Long getEmpTypeId() {
		return empTypeId;
	}

	public void setEmpTypeId(Long empTypeId) {
		this.empTypeId = empTypeId;
	}

	public Long getWhetherScheduling() {
		return whetherScheduling;
	}

	public void setWhetherScheduling(Long whetherScheduling) {
		this.whetherScheduling = whetherScheduling;
	}

	public String getWorkTypeName() {
		return workTypeName;
	}

	public void setWorkTypeName(String workTypeName) {
		this.workTypeName = workTypeName;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public List<Long> getJobStatusList() {
		return jobStatusList;
	}

	public void setJobStatusList(List<Long> jobStatusList) {
		this.jobStatusList = jobStatusList;
	}
	
    

}
