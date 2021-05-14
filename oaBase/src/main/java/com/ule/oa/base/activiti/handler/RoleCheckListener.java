package com.ule.oa.base.activiti.handler;

import java.util.Map;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ule.oa.base.service.EmployeeService;

@Component
@SuppressWarnings("serial")
public class RoleCheckListener implements ExecutionListener{
	@Autowired
	private EmployeeService employeeService;
	@Override
	public void notify(DelegateExecution execution) {
		Long proposerId = (Long)execution.getVariable("proposerId");
		execution.setVariable("isHeader", employeeService.isLeaderByEmpId(proposerId));
		Map<String, Object> map = employeeService.queryDepartHeadIdByEmpId(proposerId);
		//角色判断：汇报对象是否是部门负责人
		//获取部门负责人
		Long headerId = (Long)map.get("departLeader");
		//获取汇报对象
		Long reportToLeader = employeeService.getById(proposerId).getReportToLeader();
		if(reportToLeader!=null){
			execution.setVariable("leaderIsHeader", reportToLeader.equals(headerId));
		}else{
			execution.setVariable("leaderIsHeader", false);
		}
	}

}
