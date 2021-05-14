package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @ClassName: 员工值班表
 * @Description: 员工值班表
 * @author yangjie
 * @date 2018年1月9日
 */
public class EmployeeDutyTbl implements Serializable{

	private static final long serialVersionUID = -8744081903817492977L;
	
	private Long id;
	//公司ID
    private Long companyId;
    //部门ID
    private Long departId;
    //员工ID
    private Long employId;
    //员工姓名
    private String employName;
    //节日年份
    private String year;
    //节假日名称
    private String vacationName;
    //值班日期
    private Date dutyDate;
    //值班事项
    private String dutyItem;
    //开始时间
    private Date startTime;
    //结束时间
    private Date endTime;
    //工作小时数
    private Double workHours;
    //排班人
    private String classSettingPerson;
    //状态（1：删除，0：有效）
    private Integer delFlag;
    private Date createTime;
    private String createUser;
    private String updateUser;
    private Date updateTime;
    //插入途径（1：排班申请，0：值班申请，2：Hr直接调班）
    private Integer source;
    //备注
    private String remark;
	
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
	public Long getDepartId() {
		return departId;
	}
	public void setDepartId(Long departId) {
		this.departId = departId;
	}
	public Long getEmployId() {
		return employId;
	}
	public void setEmployId(Long employId) {
		this.employId = employId;
	}
	public String getEmployName() {
		return employName;
	}
	public void setEmployName(String employName) {
		this.employName = employName;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getVacationName() {
		return vacationName;
	}
	public void setVacationName(String vacationName) {
		this.vacationName = vacationName;
	}
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	public Date getDutyDate() {
		return dutyDate;
	}
	public void setDutyDate(Date dutyDate) {
		this.dutyDate = dutyDate;
	}
	public String getDutyItem() {
		return dutyItem;
	}
	public void setDutyItem(String dutyItem) {
		this.dutyItem = dutyItem;
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
	public Double getWorkHours() {
		return workHours;
	}
	public void setWorkHours(Double workHours) {
		this.workHours = workHours;
	}
	public String getClassSettingPerson() {
		return classSettingPerson;
	}
	public void setClassSettingPerson(String classSettingPerson) {
		this.classSettingPerson = classSettingPerson;
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
	public Integer getSource() {
		return source;
	}
	public void setSource(Integer source) {
		this.source = source;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
    
    
}
