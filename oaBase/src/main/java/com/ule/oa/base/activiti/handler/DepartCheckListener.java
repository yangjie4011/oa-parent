package com.ule.oa.base.activiti.handler;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmployeeService;

@Component
@SuppressWarnings("serial")
public class DepartCheckListener implements ExecutionListener{

	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private DepartService departService;
	@Override
	public void notify(DelegateExecution execution) {
		Long proposerId = (Long)execution.getVariable("proposerId");
		//产品管理部+财务部+结算运营部+大数据部+系统运维部 的 异常考勤流程  部门负责人未放权给汇报对象
		boolean isPMDepart = employeeService.isPMDepart(proposerId);//是否是 产品管理部 或者 财务部 或者 大数据部 或者 系统运维部 或者 结算运营部
		boolean isTDDepart = employeeService.isTDDepart(proposerId);//是否是技术开发部
		//产品管理部+财务部+技术开发部 部门负责人特殊处理
		if(isPMDepart||isTDDepart){
			if(departService.checkLeaderIsDh(proposerId)){
				isPMDepart = false;
				isTDDepart = false;
			}
		}
		execution.setVariable("isTDDepart",isTDDepart);
		execution.setVariable("isPMDepart",isPMDepart);
	}

}
