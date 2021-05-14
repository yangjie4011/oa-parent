package com.ule.oa.base.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.EmpTrainingTbl;

/**
  * @ClassName: EmpTraining
  * @Description: 员工培训表
  * @author minsheng
  * @date 2017年5月8日 下午1:46:24
 */
@JsonInclude(Include.NON_NULL)
public class EmpTraining extends EmpTrainingTbl {

	  /**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = 4280399466979985933L;

}
