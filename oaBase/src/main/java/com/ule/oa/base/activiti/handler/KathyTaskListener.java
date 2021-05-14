package com.ule.oa.base.activiti.handler;

import java.util.ArrayList;
import java.util.List;

import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.service.delegate.DelegateTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ule.oa.base.po.Employee;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.SendMailService;
import com.ule.oa.base.service.impl.ActivitiServiceImpl;
import com.ule.oa.common.exception.OaException;

/**
 * @ClassName: kathy审批
 * @Description: kathy审批
 * @author yangjie
 * @date 2018年5月22日
 */
@Component
@SuppressWarnings("serial")
public class KathyTaskListener implements TaskListener{
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private TaskService taskService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private SendMailService sendMailService;
	@Autowired
	private ActivitiServiceImpl activitiServiceImpl;

	@Override
	public void notify(DelegateTask delegateTask) {
		Employee param = new Employee();
		param.setCode("SP298");
		Employee kathy = employeeService.getByCondition(param);
		taskService.setAssignee(delegateTask.getId(),String.valueOf(kathy.getId()));
		 Employee proposer = employeeService.getByEmpId((Long)delegateTask.getVariable("proposerId")).get(0);
		 ProcessDefinition process = activitiServiceImpl.getProcessDefinition(delegateTask.getProcessDefinitionId()).get(0);
		List<Long> emps = new ArrayList<Long>();
		emps.add(kathy.getId());
		try {
			logger.info("发送待办任务邮件开始,taskId={}",delegateTask.getId());
			sendMailService.sendTaskEmail(emps,process.getName(),proposer.getEmpDepart().getDepart().getName(),proposer.getCnName());
			logger.info("发送待办任务邮件结束,taskId={}",delegateTask.getId());
		} catch (Exception e) {
			logger.error("发送待办任务邮件失败,taskId={}",delegateTask.getId());
			throw new RuntimeException("发送待办任务邮件失败！");
		}
	}

}
