package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

public class DinnerAndTrafficAllowanceDayTbl implements Serializable{

	private static final long serialVersionUID = 5938088397198674765L;
	
	private Long id;//主键
	
	private Long reportId;//考勤报表主表
	
	private Date attnDate;//考勤日期
	
	private String week;//星期
	
	private String attnClass;//班次
	
	private Date offDutyTime;//下班时间
	
	private Double overTimeHours;//加班时长
	
	private Integer dinnerAllowance;//晚间餐费
	
	private String traffiAllowance;//晚间交通费(0，45，实报实销  3种情形)
	
	private Date createTime;//修改时间
	
	private String createUser;//创建人
	
	private Date updateTime;//修改时间
	
	private String updateUser;//修改人
	
	private Integer delFlag;//0-正常,1-删除

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getReportId() {
		return reportId;
	}

	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}

	public Date getAttnDate() {
		return attnDate;
	}

	public void setAttnDate(Date attnDate) {
		this.attnDate = attnDate;
	}

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public String getAttnClass() {
		return attnClass;
	}

	public void setAttnClass(String attnClass) {
		this.attnClass = attnClass;
	}

	public Date getOffDutyTime() {
		return offDutyTime;
	}

	public void setOffDutyTime(Date offDutyTime) {
		this.offDutyTime = offDutyTime;
	}

	public Double getOverTimeHours() {
		return overTimeHours;
	}

	public void setOverTimeHours(Double overTimeHours) {
		this.overTimeHours = overTimeHours;
	}

	public Integer getDinnerAllowance() {
		return dinnerAllowance;
	}

	public void setDinnerAllowance(Integer dinnerAllowance) {
		this.dinnerAllowance = dinnerAllowance;
	}

	public String getTraffiAllowance() {
		return traffiAllowance;
	}

	public void setTraffiAllowance(String traffiAllowance) {
		this.traffiAllowance = traffiAllowance;
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

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}
