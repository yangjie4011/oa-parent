package com.ule.oa.base.activiti.handler;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ule.oa.base.service.EmployeeService;


/**
 * 是否是技术开发部
 * @author yangjie
 *
 */

@Component
@SuppressWarnings("serial")
public class CheckDepartIsTDListener implements ExecutionListener{

	@Autowired
	private EmployeeService employeeService;

	@Override
	public void notify(DelegateExecution execution) {
		Long proposerId = (Long)execution.getVariable("proposerId");
		boolean isTDDepart = employeeService.isTDDepart(proposerId);//是否是技术开发部
		execution.setVariable("isTDDepart",isTDDepart);
		//判断申请人汇报对象是否已经是 总监，副总监，VP，COO,部门负责人
		boolean isNextSuperior = true;//是否需要到下一级汇报对象
		//申请人第一级汇报对象
		Long assignee = employeeService.getById(proposerId).getReportToLeader();
		//判断该人职位序列是总监，副总监，VP，COO或者部门负责人
		if(employeeService.hasPowerSuperior(assignee)){
			isNextSuperior = false;
        }
		execution.setVariable("isNextSuperior",isNextSuperior);
	}

}
