package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.ule.oa.common.po.CommonPo;

/**
 * @ClassName: 值班明细表
 * @Description: 值班明细表
 * @author yangjie
 * @date 2017年12月11日
 */
public class ApplyEmployDutyDetailTbl extends CommonPo implements Serializable{
	
	private static final long serialVersionUID = -3741093939983626101L;
	
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
    private Date startTime;
    //结束时间
    private Date endTime;
    //工作小时数
    private Double workHours;
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
	public Long getAttnApplicationEmployDutyId() {
		return attnApplicationEmployDutyId;
	}
	public void setAttnApplicationEmployDutyId(Long attnApplicationEmployDutyId) {
		this.attnApplicationEmployDutyId = attnApplicationEmployDutyId;
	}
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
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
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
