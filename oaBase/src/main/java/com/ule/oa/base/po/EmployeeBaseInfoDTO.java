package com.ule.oa.base.po;

/**
 * 员工基本信息
 * @author yangjie
 *
 */
public class EmployeeBaseInfoDTO {
	
	private Long id;//员工id
	
	private String code;//员工编号
	
	private String cnName;//员工姓名
	
	private String departName;//部门
	
	private String positionName;//职位
	
	private Long workType;//工时类型

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public Long getWorkType() {
		return workType;
	}

	public void setWorkType(Long workType) {
		this.workType = workType;
	}

}
