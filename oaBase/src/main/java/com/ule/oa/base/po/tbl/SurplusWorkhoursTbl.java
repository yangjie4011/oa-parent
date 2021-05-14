package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: 排班员工剩余工时
 * @Description: 排班员工剩余工时
 * @author yangjie
 * @date 2017年11月9日
 */
public class SurplusWorkhoursTbl implements Serializable{
	
	private static final long serialVersionUID = -6339951886786029590L;
	
	private Long id;
	//公司Id
	private Long companyId;
	//员工id
	private Long employeeId;
	//员工姓名
	private String employeeName;
	//月份
	private String month;
	//当月剩余工时
	private double surplusHours;
	private Integer delFlag;
	private Date createTime;
	private String createUser;
	private String updateUser;
	private Date updateTime;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
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
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public double getSurplusHours() {
		return surplusHours;
	}
	public void setSurplusHours(double surplusHours) {
		this.surplusHours = surplusHours;
	}
	public Integer getDelFlag() {
		return delFlag;
	}
	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
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
	public String getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	

}
