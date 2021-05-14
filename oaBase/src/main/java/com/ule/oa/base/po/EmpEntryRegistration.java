package com.ule.oa.base.po;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.EmpEntryRegistrationTbl;

@JsonInclude(Include.NON_NULL)
public class EmpEntryRegistration extends EmpEntryRegistrationTbl{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1626228687525837793L;

	private String name;
	
	private String typeCName;
		
	private String dpname;

	private String positionName;
	
	private String leaderName;

	private String deptLeaderName;
	
	private Integer pageNo;
	
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date employmentDateBegin;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date createTimeBegin;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date employmentDateEnd;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date createTimeEnd;
	
	private Integer canCancel;//能否取消入职（0-能，1-不能）
	
	
	public Date getCreateTimeBegin() {
		return createTimeBegin;
	}

	public void setCreateTimeBegin(Date createTimeBegin) {
		this.createTimeBegin = createTimeBegin;
	}

	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date applyStartDateFrist;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date applyEndDateFrist;
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date applyStartDate;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date applyEndDate;
	
	public Date getApplyStartDateFrist() {
		return applyStartDateFrist;
	}

	public void setApplyStartDateFrist(Date applyStartDateFrist) {
		this.applyStartDateFrist = applyStartDateFrist;
	}

	public Date getApplyEndDateFrist() {
		return applyEndDateFrist;
	}

	public void setApplyEndDateFrist(Date applyEndDateFrist) {
		this.applyEndDateFrist = applyEndDateFrist;
	}

	public Date getApplyStartDate() {
		return applyStartDate;
	}

	public void setApplyStartDate(Date applyStartDate) {
		this.applyStartDate = applyStartDate;
	}

	public Date getApplyEndDate() {
		return applyEndDate;
	}

	public void setApplyEndDate(Date applyEndDate) {
		this.applyEndDate = applyEndDate;
	}

	private Integer yanqi;
	
	private List<Integer> departList;//部门
	private String StatusDesc;//审核状态
	private Integer firstDepart;//页面上一级部门
	private Integer secondDepart;//页面上二级部门
	
	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}
	
	public Date getEmploymentDateEnd() {
		return employmentDateEnd;
	}

	public void setEmploymentDateEnd(Date employmentDateEnd) {
		this.employmentDateEnd = employmentDateEnd;
	}

	public Date getCreateTimeEnd() {
		return createTimeEnd;
	}

	public void setCreateTimeEnd(Date createTimeEnd) {
		this.createTimeEnd = createTimeEnd;
	}



	public Integer getYanqi() {
		return yanqi;
	}

	public void setYanqi(Integer yanqi) {
		this.yanqi = yanqi;
	}

	public String getDeptLeaderName() {
		return deptLeaderName;
	}

	public void setDeptLeaderName(String deptLeaderName) {
		this.deptLeaderName = deptLeaderName;
	}

	public String getLeaderName() {
		return leaderName;
	}

	public void setLeaderName(String leaderName) {
		this.leaderName = leaderName;
	}

	public String getPositionName() {
		return positionName;
	}

	public void setPositionName(String positionName) {
		this.positionName = positionName;
	}

	public String getStatusDesc() {
		return StatusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		StatusDesc = statusDesc;
	}

	public Integer getFirstDepart() {
		return firstDepart;
	}

	public void setFirstDepart(Integer firstDepart) {
		this.firstDepart = firstDepart;
	}

	public Integer getSecondDepart() {
		return secondDepart;
	}

	public void setSecondDepart(Integer secondDepart) {
		this.secondDepart = secondDepart;
	}

	public List<Integer> getDepartList() {
		return departList;
	}

	public void setDepartList(List<Integer> departList) {
		this.departList = departList;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTypeCName() {
		return typeCName;
	}

	public void setTypeCName(String typeCName) {
		this.typeCName = typeCName;
	}

	public String getDpname() {
		return dpname;
	}

	public void setDpname(String dpname) {
		this.dpname = dpname;
	}
	public Date getEmploymentDateBegin() {
		return employmentDateBegin;
	}

	public void setEmploymentDateBegin(Date employmentDateBegin) {
		this.employmentDateBegin = employmentDateBegin;
	}

	public Integer getCanCancel() {
		return canCancel;
	}

	public void setCanCancel(Integer canCancel) {
		this.canCancel = canCancel;
	}
	

}
