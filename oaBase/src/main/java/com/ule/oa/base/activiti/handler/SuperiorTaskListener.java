package com.ule.oa.base.activiti.handler;

import java.util.List;

import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.service.delegate.DelegateTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.SendMailService;
import com.ule.oa.base.service.impl.ActivitiServiceImpl;
import com.ule.oa.common.exception.OaException;

/**
 * 设置当前节点的办理人为登录人的上级领导
 * @author wangwencan
 *
 */
@Component
@SuppressWarnings("serial")
public class SuperiorTaskListener implements TaskListener{
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private SendMailService sendMailService;
	@Autowired
	private ActivitiServiceImpl activitiServiceImpl;
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public void notify(DelegateTask delegateTask) {
		try {
			Long assignee = employeeService.getById((Long)delegateTask.getVariable("proposerId")).getReportToLeader();
			Employee proposer = employeeService.getByEmpId((Long)delegateTask.getVariable("proposerId")).get(0);
			ProcessDefinition process = activitiServiceImpl.getProcessDefinition(delegateTask.getProcessDefinitionId()).get(0);
			List<Long> emps = Lists.newArrayList(assignee);
			logger.info("发送待办任务邮件开始,taskId={}",delegateTask.getId());
			sendMailService.sendTaskEmail(emps,process.getName(),proposer.getEmpDepart().getDepart().getName(),proposer.getCnName());
			logger.info("发送待办任务邮件结束,taskId={}",delegateTask.getId());
			taskService.setAssignee(delegateTask.getId(),String.valueOf(assignee));
		} catch (Exception e) {
			logger.error("发送待办任务邮件失败,taskId={}",delegateTask.getId());
			throw new RuntimeException("发送待办任务邮件失败！");
		}
	}
}
