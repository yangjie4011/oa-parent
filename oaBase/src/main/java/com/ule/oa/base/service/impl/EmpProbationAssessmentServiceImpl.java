package com.ule.oa.base.service.impl;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ule.oa.base.mapper.EmpProbationAssessmentMapper;
import com.ule.oa.base.po.EmpProbationAssessment;
import com.ule.oa.base.service.EmpProbationAssessmentService;
import com.ule.oa.base.service.EmpSelfAssessmentService;
import com.ule.oa.common.po.CommonPo;

/**
 * 
  * @ClassName: EmpProbationAssessmentServiceImpl
  * @Description: 员工试用期评估
  * @author jiwenhang
  * @date 2017年5月27日 下午1:27:17
 */
@Service
public class EmpProbationAssessmentServiceImpl implements EmpProbationAssessmentService{
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private EmpProbationAssessmentMapper empProbationAssessmentMapper;
	@Autowired
	private EmpSelfAssessmentService empSelfAssessmentService;
	
	@Override
	public void setEmp(HttpServletRequest request, Long id) {
		empSelfAssessmentService.setEmp(request, id);
	}

	@Override
	public EmpProbationAssessment saveAndUpdate(EmpProbationAssessment model) {
		EmpProbationAssessment ema = null;
		if(null != model.getEmployeeId() && 0L != model.getEmployeeId()){
			ema = empProbationAssessmentMapper.getByEmpId(model.getEmployeeId());
		}	
		if(ema == null){
			model.setCreateTime(new Date());
			model.setCreateUser("system");
			model.setDelFlag(CommonPo.STATUS_NORMAL);
			empProbationAssessmentMapper.save(model);
		}else{
			model.setId(ema.getId());
			model.setUpdateTime(new Date());
			model.setUpdateUser("system");
			model.setDelFlag(CommonPo.STATUS_NORMAL);
			empProbationAssessmentMapper.update(model);
		}
		return empProbationAssessmentMapper.getById(model.getId());
	}
}
