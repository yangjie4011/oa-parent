package com.ule.oa.base.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.EmpApplicationBusinessDetailTbl;

/**
 * @ClassName: 出差申请明细表
 * @Description: 出差申请明细表
 * @author yangjie
 * @date 2017年6月13日
 */

@JsonInclude(Include.NON_NULL)
public class EmpApplicationBusinessDetail extends EmpApplicationBusinessDetailTbl {

	private static final long serialVersionUID = 7118994187516094950L;

}
