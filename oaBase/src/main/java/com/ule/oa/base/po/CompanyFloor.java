package com.ule.oa.base.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.CompanyFloorTbl;

/**
 * 
  * @ClassName: CompanyFloor
  * @Description: 公司楼层信息
  * @author jiwenhang
  * @date 2017年5月23日 下午1:26:25
 */
@JsonInclude(Include.NON_NULL)
public class CompanyFloor extends CompanyFloorTbl {

	private static final long serialVersionUID = 6019116471284606315L;

}
