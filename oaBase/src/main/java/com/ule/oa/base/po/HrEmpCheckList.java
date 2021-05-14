package com.ule.oa.base.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.HrEmpCheckListTbl;

/**
 * @ClassName: 离职办理清单
 * @Description: 离职办理清单
 * @author yangjie
 * @date 2017年6月6日
 */
@JsonInclude(Include.NON_NULL)
public class HrEmpCheckList extends HrEmpCheckListTbl{

	private static final long serialVersionUID = 1955181373990949312L;
	
	public static final int CHECK_STATUS_YES = 100;//已完成
	public static final int CHECK_STATUS_NO = 200;//无此项目

}
