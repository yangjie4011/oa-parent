package com.ule.oa.base.po;

import java.util.List;

/**
 * 员工基本信息查询条件
 * @author yangjie
 *
 */
public class EmployeeBaseInfoParam {
	
	private String code;//员工编号
	
	private String cnName;//员工姓名
	
	private Long departId;//部门
	
	private Long whetherScheduling;//是否排班
	
	private Long workType;//工时类型
	
	private Long empTypeId;//员工类型
	
	private List<Long> empTypeIdList;//员工类型列表
	
	private List<Long> currentUserDepart;//当前用户所授权的部门集合
	private List<Long> subEmployeeIdList;//所属下属员工id列表

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCnName() {
		return cnName;
	}

	public void setCnName(String cnName) {
		this.cnName = cnName;
	}

	public Long getDepartId() {
		return departId;
	}

	public void setDepartId(Long departId) {
		this.departId = departId;
	}

	public Long getWhetherScheduling() {
		return whetherScheduling;
	}

	public void setWhetherScheduling(Long whetherScheduling) {
		this.whetherScheduling = whetherScheduling;
	}

	public Long getWorkType() {
		return workType;
	}

	public void setWorkType(Long workType) {
		this.workType = workType;
	}

	public Long getEmpTypeId() {
		return empTypeId;
	}

	public void setEmpTypeId(Long empTypeId) {
		this.empTypeId = empTypeId;
	}

	public List<Long> getEmpTypeIdList() {
		return empTypeIdList;
	}

	public void setEmpTypeIdList(List<Long> empTypeIdList) {
		this.empTypeIdList = empTypeIdList;
	}

	public List<Long> getCurrentUserDepart() {
		return currentUserDepart;
	}

	public void setCurrentUserDepart(List<Long> currentUserDepart) {
		this.currentUserDepart = currentUserDepart;
	}

	public List<Long> getSubEmployeeIdList() {
		return subEmployeeIdList;
	}

	public void setSubEmployeeIdList(List<Long> subEmployeeIdList) {
		this.subEmployeeIdList = subEmployeeIdList;
	}
	

}
