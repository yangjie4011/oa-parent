package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author yangjie
 * 假期流水表
 */
public class LeaveRecordTbl implements Serializable{

	private static final long serialVersionUID = 7095799754951485235L;
	
	private Long id;
	//员工id
	private Long employeeId;
	//假期类型
	private Integer type;
	//假期天数/小时数
	private Double days;
	//假期单位（0-天，1-小时）
	private Integer daysUnit;
	//关联单据id
	private Long billId;
	//leave-请假申请表，overtime-加班申请表
	private String billType;
	//修改类型修改类型(0-已用，1-总数,2-剩余)
	private Integer updateType;
	//状态（1：删除，0：有效）
	private Integer delFlag;
    //创建时间
    private Date createTime;
    //更新时间
    private Date updateTime;
    //创建人
    private String createUser;
    //更新人
    private String updateUser;
    //途径(0-正常系统录入，1-人事修改)
    private Integer source;
    //备注
    private String remark;
	
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
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Double getDays() {
		return days;
	}
	public void setDays(Double days) {
		this.days = days;
	}
	public Integer getDaysUnit() {
		return daysUnit;
	}
	public void setDaysUnit(Integer daysUnit) {
		this.daysUnit = daysUnit;
	}
	public Long getBillId() {
		return billId;
	}
	public void setBillId(Long billId) {
		this.billId = billId;
	}
	public String getBillType() {
		return billType;
	}
	public void setBillType(String billType) {
		this.billType = billType;
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
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
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
	public Integer getUpdateType() {
		return updateType;
	}
	public void setUpdateType(Integer updateType) {
		this.updateType = updateType;
	}
    

}
