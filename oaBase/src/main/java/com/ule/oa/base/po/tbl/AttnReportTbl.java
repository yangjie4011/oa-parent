package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

/**
 * 考勤报表头表
 */
public class AttnReportTbl implements Serializable{
	
	private static final long serialVersionUID = 3808598174873555314L;

	private Long id;//主键
   
    private Long employeeId;
   
    private String employeeName;
   
    private String employeeCode;
   
    private Long departId;
    
    private String departName;
   
    private String positionName;
    
    private String workType;
   
    private Integer statisticType;
   
    private Integer reportType;
   
    private Date createTime;
   
    private String createUser;
   
    private Date updateTime;
   
    private String updateUser;
   
    private Integer delFlag;

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getEmployeeId() {
		return employeeId;
	}
	
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	
	public String getEmployeeName() {
		return employeeName;
	}
	
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	
	public String getEmployeeCode() {
		return employeeCode;
	}
	
	public void setEmployeeCode(String employeeCode) {
		this.employeeCode = employeeCode;
	}
	
	public Long getDepartId() {
		return departId;
	}
	
	public void setDepartId(Long departId) {
		this.departId = departId;
	}
	
	public String getDepartName() {
		return departName;
	}
	
	public void setDepartName(String departName) {
		this.departName = departName;
	}
	
	public String getPositionName() {
		return positionName;
	}
	
	public void setPositionName(String postionName) {
		this.positionName = postionName;
	}
	
	public String getWorkType() {
		return workType;
	}
	
	public void setWorkType(String workType) {
		this.workType = workType;
	}
	
	public Integer getStatisticType() {
		return statisticType;
	}
	
	public void setStatisticType(Integer statisticType) {
		this.statisticType = statisticType;
	}
	
	public Integer getReportType() {
		return reportType;
	}
	
	public void setReportType(Integer reportType) {
		this.reportType = reportType;
	}
	
	public Date getCreateTime() {
		return createTime;
	}
	
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	public String getCreateUser() {
		return createUser;
	}
	
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	
	public Date getUpdateTime() {
		return updateTime;
	}
	
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	public String getUpdateUser() {
		return updateUser;
	}
	
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
	
	public Integer getDelFlag() {
		return delFlag;
	}
	
	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}
   
}
