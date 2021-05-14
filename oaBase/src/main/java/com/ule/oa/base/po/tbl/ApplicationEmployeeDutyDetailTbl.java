package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @ClassName: 值班申请明细表
 * @Description: 值班申请明细表
 * @author yangjie
 * @date 2017年8月30日
 */
public class ApplicationEmployeeDutyDetailTbl implements Serializable{

	private static final long serialVersionUID = 396909512881558586L;
	
	private Long id;
	//值班申请表ID
	private Long attnApplicationEmployDutyId;
	//假期日期
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date vacationDate;
    //值班事项
    private String dutyItem;
    //值班人员
    private String employeeIds;
    //开始时间
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    //结束时间
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    //工作小时数
    private Double workHours;
    private Integer delFlag;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    private String createUser;
    private String updateUser;
    private Date updateTime;
    private Integer isMove;
    private String remarks;
	
    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getAttnApplicationEmployDutyId() {
		return attnApplicationEmployDutyId;
	}
	public void setAttnApplicationEmployDutyId(Long attnApplicationEmployDutyId) {
		this.attnApplicationEmployDutyId = attnApplicationEmployDutyId;
	}
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	public Date getVacationDate() {
		return vacationDate;
	}
	public void setVacationDate(Date vacationDate) {
		this.vacationDate = vacationDate;
	}
	public String getDutyItem() {
		return dutyItem;
	}
	public void setDutyItem(String dutyItem) {
		this.dutyItem = dutyItem;
	}
	public String getEmployeeIds() {
		return employeeIds;
	}
	public void setEmployeeIds(String employeeIds) {
		this.employeeIds = employeeIds;
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
	public Double getWorkHours() {
		return workHours;
	}
	public void setWorkHours(Double workHours) {
		this.workHours = workHours;
	}
	public Integer getDelFlag() {
		return delFlag;
	}
	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
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
	public Integer getIsMove() {
		return isMove;
	}
	public void setIsMove(Integer isMove) {
		this.isMove = isMove;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	
}
