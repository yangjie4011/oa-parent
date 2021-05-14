package com.ule.oa.base.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.CompanyTbl;

/**
  * @ClassName: Company
  * @Description: 公司
  * @author minsheng
  * @date 2017年5月8日 下午1:10:01
 */
@JsonInclude(Include.NON_NULL)
public class Company extends CompanyTbl {
	  /**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = -3253044824838969316L;
	
}
