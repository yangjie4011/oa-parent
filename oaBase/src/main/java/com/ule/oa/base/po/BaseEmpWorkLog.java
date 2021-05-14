package com.ule.oa.base.po;

import java.util.Date;
import java.util.List;

import com.ule.oa.base.po.tbl.BaseEmpWorkLogTbl;

public class BaseEmpWorkLog extends BaseEmpWorkLogTbl {

	private static final long serialVersionUID = 8841817122488920136L;
	
	private String cnName;
	
	private String departName;
	
	private Date startWorkDate;
	
	private Date endWorkDate;
	
	private List<Long> employeeIdList;

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

	public Date getStartWorkDate() {
		return startWorkDate;
	}

	public void setStartWorkDate(Date startWorkDate) {
		this.startWorkDate = startWorkDate;
	}

	public Date getEndWorkDate() {
		return endWorkDate;
	}

	public void setEndWorkDate(Date endWorkDate) {
		this.endWorkDate = endWorkDate;
	}

	public List<Long> getEmployeeIdList() {
		return employeeIdList;
	}

	public void setEmployeeIdList(List<Long> employeeIdList) {
		this.employeeIdList = employeeIdList;
	}
}
