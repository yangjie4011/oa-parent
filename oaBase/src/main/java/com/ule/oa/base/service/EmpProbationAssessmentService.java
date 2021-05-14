package com.ule.oa.base.service;

import javax.servlet.http.HttpServletRequest;

import com.ule.oa.base.po.EmpProbationAssessment;

/**
 * 
  * @ClassName: EmpProbationAssessmentService
  * @Description: 员工试用期评估
  * @author jiwenhang
  * @date 2017年5月27日 下午1:27:05
 */
public interface EmpProbationAssessmentService {

	void setEmp(HttpServletRequest request, Long id);

	EmpProbationAssessment saveAndUpdate(EmpProbationAssessment model);

}
