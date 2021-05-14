package com.ule.oa.base.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.CompanySeatTbl;

/**
 * 
  * @ClassName: CompanySeat
  * @Description: 公司座位信息
  * @author jiwenhang
  * @date 2017年5月23日 下午1:26:03
 */
@JsonInclude(Include.NON_NULL)
public class CompanySeat extends CompanySeatTbl {

	private static final long serialVersionUID = 3292618749248169634L;

}
