package com.ule.oa.base.po;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ule.oa.base.po.tbl.EmpAttnTbl;

public class EmpAttn extends EmpAttnTbl{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date startTime;
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date endTime;
	private Long workType;
	private String workTypeName;
	private List<Integer> departList;
	private Long empTypeId;
	private Long whetherScheduling;
	private String cnName;
	private String code;
	private Long departId;
	private String departName;
	private String positionName;
	private String className;
	private String dayofweek;
	private String fontColor;
	
	private Integer firstDepart;
	private Integer secondDepart;
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date yesterDayEndAttnTime;//昨天的下班时间
	
	private List<Long> currentUserDepart;//当前用户所授权的部门集合
	private List<Long> subEmployeeIdList;//所属下属员工id列表
	private List<Long> empTypeIdList;//员工类型id列表
	private List<Long> employeeIdList;//符合条件的员工列表
	

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getYesterDayEndAttnTime() {
		return yesterDayEndAttnTime;
	}

	public void setYesterDayEndAttnTime(Date yesterDayEndAttnTime) {
		this.yesterDayEndAttnTime = yesterDayEndAttnTime;
	}

	public String getFontColor() {
		return fontColor;
	}

	public void setFontColor(String fontColor) {
		this.fontColor = fontColor;
	}

	public List<Long> getSubEmployeeIdList() {
		return subEmployeeIdList;
	}

	public void setSubEmployeeIdList(List<Long> subEmployeeIdList) {
		this.subEmployeeIdList = subEmployeeIdList;
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
	public Long getWorkType() {
		return workType;
	}
	public void setWorkType(Long workType) {
		this.workType = workType;
	}
	public String getWorkTypeName() {
		return workTypeName;
	}
	public void setWorkTypeName(String workTypeName) {
		this.workTypeName = workTypeName;
	}
	public List<Integer> getDepartList() {
		return departList;
	}
	public void setDepartList(List<Integer> departList) {
		this.departList = departList;
	}
	public Long getEmpTypeId() {
		return empTypeId;
	}
	public void setEmpTypeId(Long empTypeId) {
		this.empTypeId = empTypeId;
	}
	public Long getWhetherScheduling() {
		return whetherScheduling;
	}
	public void setWhetherScheduling(Long whetherScheduling) {
		this.whetherScheduling = whetherScheduling;
	}
	public String getCnName() {
		return cnName;
	}
	public void setCnName(String cnName) {
		this.cnName = cnName;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Long getDepartId() {
		return departId;
	}
	public void setDepartId(Long departId) {
		this.departId = departId;
	}
	public String getDepartName() {
		return departName;
	}
	public void setDepartName(String departName) {
		this.departName = departName;
	}
	public String getPositionName() {
		return positionName;
	}
	public void setPositionName(String positionName) {
		this.positionName = positionName;
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
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getDayofweek() {
		return dayofweek;
	}
	public void setDayofweek(String dayofweek) {
		this.dayofweek = dayofweek;
	}
	public List<Long> getCurrentUserDepart() {
		return currentUserDepart;
	}
	public void setCurrentUserDepart(List<Long> currentUserDepart) {
		this.currentUserDepart = currentUserDepart;
	}

	public List<Long> getEmpTypeIdList() {
		return empTypeIdList;
	}

	public void setEmpTypeIdList(List<Long> empTypeIdList) {
		this.empTypeIdList = empTypeIdList;
	}

	public List<Long> getEmployeeIdList() {
		return employeeIdList;
	}

	public void setEmployeeIdList(List<Long> employeeIdList) {
		this.employeeIdList = employeeIdList;
	}
	
	
}