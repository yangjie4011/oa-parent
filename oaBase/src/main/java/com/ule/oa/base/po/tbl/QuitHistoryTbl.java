package com.ule.oa.base.po.tbl;

import java.io.Serializable;

import com.ule.oa.common.po.CommonPo;
/**
 * @ClassName: QuitHistory
 * @Description: 离职历史表
 * @author zhoujinliang
 * @date 2018年7月4日
*/
//离职历史表
public class QuitHistoryTbl extends CommonPo implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long id;
	//员工id
	private Long employeeId;
	//员工类型
	private String employeeType;
	//部门名称
	private String departName;
	//职位名称
	private String positionName;
	//汇报对象
	private String reportLeader;
	//部门负责人
	private String departHeader;

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

	public String getReportLeader() {
		return reportLeader;
	}

	public void setReportLeader(String reportLeader) {
		this.reportLeader = reportLeader;
	}

	public String getDepartHeader() {
		return departHeader;
	}

	public void setDepartHeader(String departHeader) {
		this.departHeader = departHeader;
	}

	public String getEmployeeType() {
		return employeeType;
	}

	public void setEmployeeType(String employeeType) {
		this.employeeType = employeeType;
	}

}
