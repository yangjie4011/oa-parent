package com.ule.oa.base.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.EmpSelfAssessmentTbl;

/**
 * 
  * @ClassName: EmpSelfAssessment
  * @Description: 员工自我评估
  * @author jiwenhang
  * @date 2017年5月23日 下午1:25:37
 */
@JsonInclude(Include.NON_NULL)
public class EmpSelfAssessment extends EmpSelfAssessmentTbl {

	private static final long serialVersionUID = 5123459659739046878L;
	
}
