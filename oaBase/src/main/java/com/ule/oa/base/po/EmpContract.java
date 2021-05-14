package com.ule.oa.base.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.EmpContractTbl;

/**
  * @ClassName: EmpContract
  * @Description: 员工合同表
  * @author minsheng
  * @date 2017年5月8日 下午1:44:47
 */
@JsonInclude(Include.NON_NULL)
public class EmpContract extends EmpContractTbl {

	  /**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = 8707472163439373435L;
	
	//生效合同
	public static final Integer IS_ACTIVE_NORMAL = 1;
	//过期合同
	public static final Integer IS_ACTIVE_DEL = 0;

}
