package com.ule.oa.base.service;

import javax.servlet.http.HttpServletRequest;

import com.ule.oa.base.po.EmpMidtermAssessment;

/**
 * 
  * @ClassName: EmpMidtermAssessmentService
  * @Description: 员工转正期中评估
  * @author jiwenhang
  * @date 2017年5月27日 下午1:26:33
 */
public interface EmpMidtermAssessmentService {

	void setEmp(HttpServletRequest request, Long id);

	EmpMidtermAssessment saveAndUpdate(EmpMidtermAssessment model);

}
