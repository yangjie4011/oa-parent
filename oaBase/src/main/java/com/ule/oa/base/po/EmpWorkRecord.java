package com.ule.oa.base.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.EmpWorkRecordTbl;

/**
  * @ClassName: EmpWorkRecord
  * @Description: 员工工作经历表
  * @author minsheng
  * @date 2017年5月8日 下午1:46:50
 */
@JsonInclude(Include.NON_NULL)
public class EmpWorkRecord extends EmpWorkRecordTbl {

	  /**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = -4712371193001283575L;

}
