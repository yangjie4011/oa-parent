package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ule.oa.common.po.CommonPo;

/**
 * @ClassName: 出差申请明细表
 * @Description: 出差申请明细表
 * @author yangjie
 * @date 2017年6月13日
 */
public class EmpApplicationBusinessDetailTbl extends CommonPo implements Serializable{

	private static final long serialVersionUID = 400522912164052652L;
	
	private Long id;
    //出差申请ID
	private Long businessId;
	//出差日期
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date businessDate;
	//任务开始日期
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date workStartDate;
	//任务结束日期
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date workEndDate;
	//工作计划
	private String workPlan;
	//工作目标
	private String workObjective;
	//总结报告
	private String workSummary;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getBusinessId() {
		return businessId;
	}
	public void setBusinessId(Long businessId) {
		this.businessId = businessId;
	}
	public Date getBusinessDate() {
		return businessDate;
	}
	public void setBusinessDate(Date businessDate) {
		this.businessDate = businessDate;
	}
	public String getWorkPlan() {
		return workPlan;
	}
	public void setWorkPlan(String workPlan) {
		this.workPlan = workPlan;
	}
	public String getWorkObjective() {
		return workObjective;
	}
	public void setWorkObjective(String workObjective) {
		this.workObjective = workObjective;
	}
	public String getWorkSummary() {
		return workSummary;
	}
	public void setWorkSummary(String workSummary) {
		this.workSummary = workSummary;
	}
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	public Date getWorkStartDate() {
		return workStartDate;
	}
	public void setWorkStartDate(Date workStartDate) {
		this.workStartDate = workStartDate;
	}
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	public Date getWorkEndDate() {
		return workEndDate;
	}
	public void setWorkEndDate(Date workEndDate) {
		this.workEndDate = workEndDate;
	}
	
	
}
