package com.ule.oa.base.po;

import java.util.List;

import com.ule.oa.base.po.tbl.EmpLeaveReportTbl;

/**
 * @ClassName: 员工请假报表
 * @Description: 员工请假报表
 * @author yangjie
 * @date 2017年10月24日
 */
public class EmpLeaveReport extends EmpLeaveReportTbl {

	private static final long serialVersionUID = -550520006321437442L;
	
	private String strType;
	
	public String getStrType() {
		return strType;
	}

	public void setStrType(String strType) {
		this.strType = strType;
	}

	private List<String> months;

	public List<String> getMonths() {
		return months;
	}

	public void setMonths(List<String> months) {
		this.months = months;
	}
}
