package com.ule.oa.base.po;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.HrEmpResignTbl;

@JsonInclude(Include.NON_NULL)
public class HrEmpResign extends HrEmpResignTbl{

	/**
	 *  离职
	 */
	
	private static final long serialVersionUID = 3840483144354323466L;
	//员工表离职时间
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date quitTime;
	//公司名称
	private String name;
	//员工类型
	private String typeCName;
	//部门	
	private String dpname;
	//职位
	private String positionName;
	//汇报对象
	private String leaderName;
	//部门负责人
	private String deptLeaderName;
	
	//页面上的值  没有经过get处理 转成 北京时间 处理了则 前台报400
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date applyStartDateFrist;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date applyEndDateFrist;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date applyStartDate;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date applyEndDate;
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date entryDateEnd;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date resignationDateEnd;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getEntryDateEnd() {
		return entryDateEnd;
	}

	public void setEntryDateEnd(Date entryDateEnd) {
		this.entryDateEnd = entryDateEnd;
	}
	
	

	//下拉值
	private String yanqi;	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getResignationDateEnd() {
		return resignationDateEnd;
	}

	public void setResignationDateEnd(Date resignationDateEnd) {
		this.resignationDateEnd = resignationDateEnd;
	}
	private List<Integer> departList;//部门
	private Integer firstDepart;//页面上一级部门
	private Integer secondDepart;//页面上二级部门
	
	public Date getQuitTime() {
		return quitTime;
	}

	public void setQuitTime(Date quitTime) {
		this.quitTime = quitTime;
	}

	
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
	
	//时间时 转 北京时间 @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")

	public String getYanqi() {
		return yanqi;
	}

	public void setYanqi(String yanqi) {
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

	public List<Integer> getDepartList() {
		return departList;
	}

	public void setDepartList(List<Integer> departList) {
		this.departList = departList;
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
	
	
	
	
}
