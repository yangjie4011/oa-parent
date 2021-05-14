package com.ule.oa.base.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.HrEmpCheckDetailTbl;

/**
 * @ClassName: 离职办理清单详细
 * @Description: 离职办理清单详细
 * @author yangjie
 * @date 2017年6月6日
 */
@JsonInclude(Include.NON_NULL)
public class HrEmpCheckDetail extends HrEmpCheckDetailTbl{

	private static final long serialVersionUID = 7621791847465805610L;

}
