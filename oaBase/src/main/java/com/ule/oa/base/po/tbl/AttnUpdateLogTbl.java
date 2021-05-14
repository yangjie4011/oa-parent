package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ule.oa.common.po.CommonPo;

//考勤修改日志
public class AttnUpdateLogTbl extends CommonPo implements Serializable{
	
	private static final long serialVersionUID = -8875604734185733150L;
	
	private Long id;
	//员工ID
	private Long employeeId;
	//修改考勤日期
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date updateAttnDate;
	//修改上班时间
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date updateStartTime;
	//修改下班时间
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date updateEndTime;
	//修改下班时间
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date insertAttnTime;	
	
	
	//修改类型
	private Integer type;
	//说明
	private String remark;
	//修改人
	private String createUser;
	//修改时间
	private Date createTime;
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
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	public Date getUpdateAttnDate() {
		return updateAttnDate;
	}
	public void setUpdateAttnDate(Date updateAttnDate) {
		this.updateAttnDate = updateAttnDate;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getUpdateStartTime() {
		return updateStartTime;
	}
	public void setUpdateStartTime(Date updateStartTime) {
		this.updateStartTime = updateStartTime;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getUpdateEndTime() {
		return updateEndTime;
	}
	public void setUpdateEndTime(Date updateEndTime) {
		this.updateEndTime = updateEndTime;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getInsertAttnTime() {
		return insertAttnTime;
	}
	public void setInsertAttnTime(Date insertAttnTime) {
		this.insertAttnTime = insertAttnTime;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	
	

}
