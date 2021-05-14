package com.ule.oa.base.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.ReProcdefTbl;

/**
 * @ClassName: ReProcdef
 * @Description: 流程待办表
 * @author mahaitao
 * @date 2017年5月27日 11:58
*/
@JsonInclude(Include.NON_NULL)
public class ReProcdef extends ReProcdefTbl{

	private static final long serialVersionUID = 1249832438988834861L;
	
	/**审批类型:100-一级审批、200-逐级审批*/
	public static final long APPROVAL_TYPE_100 = 100L;
	public static final long APPROVAL_TYPE_200 = 200L;

}
