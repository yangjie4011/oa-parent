package com.ule.oa.base.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.EmpApplicationLeaveDetailTbl;

/**
 * @ClassName: 请假申请明细
 * @Description: 请假申请明细
 * @author yangjie
 * @date 2017年6月14日
 */
@JsonInclude(Include.NON_NULL)
public class EmpApplicationLeaveDetail extends EmpApplicationLeaveDetailTbl {

	private static final long serialVersionUID = 2348268954190611812L;
	
	private Long employeeId;

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

}
