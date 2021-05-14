package com.ule.oa.base.po;

import java.io.Serializable;

/**
 * 查询假期流水返回结果
 * @author xujintao
 *
 */
public class ResponseQueryLeaveRecord implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4411766276465724280L;
	//-----------流水信息表-------------
	//id
	private Long leaveRecordId;
	//员工编号
	private String employeeCode;
	//员工姓名
	private String employeeName;
	//假期类型文本
	private String leaveTypeText;
	//子类型(法定，福利，带薪，非带薪。。。)
	private String childLeaveType;
	//假期年份
	private Integer leaveYear;
	//修改类型(?)
	private String updateType;
	//假期天数/小时数
	private String days;
	//假期单位文本
	private String unitText;
	//流水类型(0-系统正常流程，1-人事修改)
	private String sourceText;
	//操作人
	private String createUser;
	//备注
	private String remark;
	//流水创建时间
	private String createTime;
	//------------员工信息表--------------
	//员工编号
	private String empCode;
	//员工姓名
	private String empName;
	//部门名称
	private String departName;
	//入职日期
	private String firstEntryTime;
	//离职日期
	private String quitTime;
	//是否在职
	private String jobStatus;
	
	public Long getLeaveRecordId() {
		return leaveRecordId;
	}
	public void setLeaveRecordId(Long leaveRecordId) {
		this.leaveRecordId = leaveRecordId;
	}
	public String getDays() {
		return days;
	}
	public void setDays(String days) {
		this.days = days;
	}
	public String getEmployeeCode() {
		return employeeCode;
	}
	public void setEmployeeCode(String employeeCode) {
		this.employeeCode = employeeCode;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public String getChildLeaveType() {
		return childLeaveType;
	}
	public void setChildLeaveType(String childLeaveType) {
		this.childLeaveType = childLeaveType;
	}
	public Integer getLeaveYear() {
		return leaveYear;
	}
	public void setLeaveYear(Integer leaveYear) {
		this.leaveYear = leaveYear;
	}
	public String getSourceText() {
		return sourceText;
	}
	public void setSourceText(String sourceText) {
		this.sourceText = sourceText;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getLeaveTypeText() {
		return leaveTypeText;
	}
	public void setLeaveTypeText(String leaveTypeText) {
		this.leaveTypeText = leaveTypeText;
	}
	public String getUnitText() {
		return unitText;
	}
	public void setUnitText(String unitText) {
		this.unitText = unitText;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getUpdateType() {
		return updateType;
	}
	public void setUpdateType(String updateType) {
		this.updateType = updateType;
	}
	public String getEmpCode() {
		return empCode;
	}
	public void setEmpCode(String empCode) {
		this.empCode = empCode;
	}
	public String getEmpName() {
		return empName;
	}
	public void setEmpName(String empName) {
		this.empName = empName;
	}
	public String getDepartName() {
		return departName;
	}
	public void setDepartName(String departName) {
		this.departName = departName;
	}
	public String getFirstEntryTime() {
		return firstEntryTime;
	}
	public void setFirstEntryTime(String firstEntryTime) {
		this.firstEntryTime = firstEntryTime;
	}
	public String getQuitTime() {
		return quitTime;
	}
	public void setQuitTime(String quitTime) {
		this.quitTime = quitTime;
	}
	public String getJobStatus() {
		return jobStatus;
	}
	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}
	
	
	

}
