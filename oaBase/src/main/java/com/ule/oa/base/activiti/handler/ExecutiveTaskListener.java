package com.ule.oa.base.activiti.handler;

import java.util.List;
import java.util.stream.Collectors;

import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.service.delegate.DelegateTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ule.oa.base.po.ActPermission;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.service.ActPermissionService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.SendMailService;
import com.ule.oa.base.service.impl.ActivitiServiceImpl;
import com.ule.oa.common.exception.OaException;

/**
 * @ClassName: 行政部门
 * @Description: 行政部门
 * @author yangjie
 * @date 2018年5月16日
 */
@Component
@SuppressWarnings("serial")
public class ExecutiveTaskListener implements TaskListener{
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private ActivitiServiceImpl activitiServiceImpl;
	@Autowired
	private ActPermissionService actPermissionService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private SendMailService sendMailService;
	@Autowired
	private EmployeeService employeeService;

	@Override
	public void notify(DelegateTask delegateTask) {
		String key = activitiServiceImpl.queryProcessKeyByDefinitionId(delegateTask.getProcessDefinitionId());
		 Employee proposer = employeeService.getByEmpId((Long)delegateTask.getVariable("proposerId")).get(0);
		 ProcessDefinition process = activitiServiceImpl.getProcessDefinition(delegateTask.getProcessDefinitionId()).get(0);
		String actId = delegateTask.getTaskDefinitionKey();
		ActPermission permission = new ActPermission();
		permission.setProcessKey(key);
		permission.setActId(actId);
		List<Long> emps = actPermissionService.queryEmpIdByPermission(permission);
		if(emps.size()>1) {
			List<String> users = emps.stream().map(e->e.toString().trim()).collect(Collectors.toList()); //内部遍历
			delegateTask.addCandidateUsers(users);
		}else {
			taskService.setAssignee(delegateTask.getId(),emps.get(0).toString().trim());
		}
		try {
			logger.info("发送待办任务邮件开始,taskId={}",delegateTask.getId());
			sendMailService.sendTaskEmail(emps,process.getName(),proposer.getEmpDepart().getDepart().getName(),proposer.getCnName());
			logger.info("发送待办任务邮件结束,taskId={}",delegateTask.getId());
		} catch (Exception e1) {
			logger.error("发送待办任务邮件失败,taskId={}",delegateTask.getId());
			throw new RuntimeException("发送待办任务邮件失败！");
		}
	}

}
