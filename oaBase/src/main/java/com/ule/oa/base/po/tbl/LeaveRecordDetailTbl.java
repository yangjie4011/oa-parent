package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

/**
 * @author yangjie
 * 假期流水明细表
 */
public class LeaveRecordDetailTbl implements Serializable{
	
	private static final long serialVersionUID = 3539746646651439684L;
	
	private Long id;
	//假期流水表id
	private Long leaveRecordId;
	//假期表id
	private Long baseEmpLeaveId;
	//假期天数/小时数
	private Double days;
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
	
    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getLeaveRecordId() {
		return leaveRecordId;
	}
	public void setLeaveRecordId(Long leaveRecordId) {
		this.leaveRecordId = leaveRecordId;
	}
	public Long getBaseEmpLeaveId() {
		return baseEmpLeaveId;
	}
	public void setBaseEmpLeaveId(Long baseEmpLeaveId) {
		this.baseEmpLeaveId = baseEmpLeaveId;
	}
	public Double getDays() {
		return days;
	}
	public void setDays(Double days) {
		this.days = days;
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
    
    

}
