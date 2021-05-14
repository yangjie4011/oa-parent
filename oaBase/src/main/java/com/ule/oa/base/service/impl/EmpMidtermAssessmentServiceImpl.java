package com.ule.oa.base.service.impl;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ule.oa.base.mapper.EmpMidtermAssessmentMapper;
import com.ule.oa.base.po.EmpMidtermAssessment;
import com.ule.oa.base.service.EmpMidtermAssessmentService;
import com.ule.oa.base.service.EmpSelfAssessmentService;
import com.ule.oa.common.po.CommonPo;

/**
 * 
  * @ClassName: EmpMidtermAssessmentServiceImpl
  * @Description: 员工转正期中评估
  * @author jiwenhang
  * @date 2017年5月27日 下午1:26:47
 */
@Service
public class EmpMidtermAssessmentServiceImpl implements EmpMidtermAssessmentService{
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private EmpMidtermAssessmentMapper empMidtermAssessmentMapper;
	@Autowired
	private EmpSelfAssessmentService empSelfAssessmentService;
	
	@Override
	public void setEmp(HttpServletRequest request, Long id) {
		empSelfAssessmentService.setEmp(request, id);
	}

	@Override
	public EmpMidtermAssessment saveAndUpdate(EmpMidtermAssessment model) {
		EmpMidtermAssessment ema = null;
		if(null != model.getEmployeeId() && 0L != model.getEmployeeId()){
			ema = empMidtermAssessmentMapper.getByEmpId(model.getEmployeeId());
		}	
		if(ema == null){
			model.setCreateTime(new Date());
			model.setCreateUser("system");
			model.setDelFlag(CommonPo.STATUS_NORMAL);
			empMidtermAssessmentMapper.save(model);
		}else{
			model.setId(ema.getId());
			model.setUpdateTime(new Date());
			model.setUpdateUser("system");
			model.setDelFlag(CommonPo.STATUS_NORMAL);
			empMidtermAssessmentMapper.update(model);
		}
		return empMidtermAssessmentMapper.getById(model.getId());
	}
	
}
