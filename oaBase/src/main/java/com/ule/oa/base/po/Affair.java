package com.ule.oa.base.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.AffairTbl;

/**
 * @ClassName: Affair
 * @Description: 日程信息
 * @author wufei
 * @date 2017年5月17日 上午9:08:10
*/
@JsonInclude(Include.NON_NULL)
public class Affair extends AffairTbl {
	
	private static final long serialVersionUID = 7245680597756525262L;
	
	/** 删除 */
	public static final Integer STATUS_DELETE = 0;
	/** 正常 */
	public static final Integer STATUS_NORMAL = 1;
	
}
