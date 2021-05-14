package com.ule.oa.base.po;

import com.ule.oa.base.po.tbl.LeaveRecordDetailTbl;

/**
 * @author yangjie
 * 假期流水明细表
 *
 */
public class LeaveRecordDetail extends LeaveRecordDetailTbl{

	private static final long serialVersionUID = 7244554337500289669L;
	
	private Integer type;//假期类型

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

}
