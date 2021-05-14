package com.ule.oa.base.service;

import javax.servlet.http.HttpServletRequest;

import com.ule.oa.base.po.EmpSelfAssessment;


/**
 * 
  * @ClassName: EmpSelfAssessmentService
  * @Description: 员工自我评估业务层
  * @author jiwenhang
  * @date 2017年5月24日 上午11:07:56
 */
public interface EmpSelfAssessmentService {

	void setEmp(HttpServletRequest request, Long id);

	EmpSelfAssessment saveAndUpdate(EmpSelfAssessment model);

}
