package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ule.oa.common.po.CommonPo;


/**
 * @ClassName: base_emp_application_leave_abolish
 * @Description: 销假假期对象
 * @author zhoujinliang
 * @date 2018年7月4日
*/
public class EmpApplicationLeaveAbolishTbl extends CommonPo implements Serializable{
	
	private static final long serialVersionUID = 3335486829813252384L;

	private Long id;
    private Long leaveId;//假期id
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startTime;//开始时间
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endTime;//结束时间
    private Double leaveDays;//销假天数
    private Double leaveHours;//销假小时
    private String comment;//销假原因
    private String processInstanceId;//流程实例ID
    private Integer approvalStatus;//'审批状态：100-待审批 、200-已审批 、300-已拒、400-已撤回'
    private Date createTime;//申请时间
    private String createUser;//申请人
    private Date updateTime;//修改时间
    private String updateUser;//修改人
	
	
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getLeaveId() {
		return leaveId;
	}
	public void setLeaveId(Long leaveId) {
		this.leaveId = leaveId;
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
	public Double getLeaveDays() {
		return leaveDays;
	}
	public void setLeaveDays(Double leaveDays) {
		this.leaveDays = leaveDays;
	}
	public Double getLeaveHours() {
		return leaveHours;
	}
	public void setLeaveHours(Double leaveHours) {
		this.leaveHours = leaveHours;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public Integer getApprovalStatus() {
		return approvalStatus;
	}
	public void setApprovalStatus(Integer approvalStatus) {
		this.approvalStatus = approvalStatus;
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
	
}