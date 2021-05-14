package com.ule.oa.base.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.EmpAppraiseTbl;

/**
  * @ClassName: EmpAppraise
  * @Description: 员工考核表
  * @author minsheng
  * @date 2017年5月8日 下午1:43:14
 */
@JsonInclude(Include.NON_NULL)
public class EmpAppraise extends EmpAppraiseTbl {

	  /**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = 3761432499122465482L;
}
