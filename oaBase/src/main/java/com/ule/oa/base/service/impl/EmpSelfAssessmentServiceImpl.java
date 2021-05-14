package com.ule.oa.base.service.impl;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ule.oa.base.mapper.EmpSelfAssessmentMapper;
import com.ule.oa.base.po.EmpSelfAssessment;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.service.EmpSelfAssessmentService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.common.po.CommonPo;

/**
 * 
  * @ClassName: EmpSelfAssessmentServiceImpl
  * @Description: 员工自我评估业务层实现
  * @author jiwenhang
  * @date 2017年5月24日 上午11:08:26
 */
@Service
public class EmpSelfAssessmentServiceImpl implements EmpSelfAssessmentService{
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private EmpSelfAssessmentMapper empSelfAssessmentMapper;
	
	@Autowired
	private EmployeeService employeeService;

	@Override
	public void setEmp(HttpServletRequest request, Long id) {
		Employee employee = employeeService.getForEmpSelfAssessmentById(id);
		if(employee == null){
			employee = new Employee();
		}
		request.setAttribute("employee", employee);
	}

	@Override
	public EmpSelfAssessment saveAndUpdate(EmpSelfAssessment model) {
		EmpSelfAssessment esa = empSelfAssessmentMapper.getByEmpId(model.getEmployeeId());
		if(esa == null){
			model.setCreateTime(new Date());
			model.setCreateUser("system");
			model.setDelFlag(CommonPo.STATUS_NORMAL);
			empSelfAssessmentMapper.save(model);
		}else{
			model.setId(esa.getId());
			model.setUpdateTime(new Date());
			model.setUpdateUser("system");
			model.setDelFlag(CommonPo.STATUS_NORMAL);
			empSelfAssessmentMapper.update(model);
		}
		return empSelfAssessmentMapper.getById(model.getId());
	}

}
