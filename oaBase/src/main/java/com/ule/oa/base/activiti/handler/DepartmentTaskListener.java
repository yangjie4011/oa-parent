package com.ule.oa.base.activiti.handler;

import java.util.List;
import java.util.Map;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.api.Task;
import org.flowable.task.service.delegate.DelegateTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ule.oa.base.mapper.ApplicationEmployeeClassMapper;
import com.ule.oa.base.mapper.DepartMapper;
import com.ule.oa.base.po.ApplicationEmployeeClass;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.SendMailService;
import com.ule.oa.base.service.impl.ActivitiServiceImpl;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.ConfigConstants;

/**
 * 部门领导人节点
 * @author wangwencan
 *
 */
//TODO : 现有逻辑是通过申请人找部门领导人,以后要改成找该部门的在这个节点的审批权限
@Component
@SuppressWarnings("serial")
public class DepartmentTaskListener implements TaskListener{
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private SendMailService sendMailService;
	@Autowired
	private ActivitiServiceImpl activitiServiceImpl;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private ApplicationEmployeeClassMapper applicationEmployeeClassMapper;
	@Autowired
	private DepartMapper departMapper;
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public void notify(DelegateTask delegateTask) {
		boolean isSendEmail = true;//是否发送邮件
		String key = activitiServiceImpl.queryProcessKeyByDefinitionId(delegateTask.getProcessDefinitionId());
		if(ConfigConstants.LEAVE_KEY.equals(key)||ConfigConstants.ABNORMALATTENDANCE_KEY.equals(key)){
			Object isAdmin = runtimeService.getVariable(delegateTask.getExecutionId(), "isAdmin");
			if(isAdmin!=null&&(boolean) isAdmin){
				isSendEmail = false;
			}
		}
		try {
			
			if(ConfigConstants.SCHEDULING_KEY.equals(key)){
				Employee proposer = employeeService.getByEmpId((Long)delegateTask.getVariable("proposerId")).get(0);
				ApplicationEmployeeClass apply = applicationEmployeeClassMapper.queryByProcessInstanceId(delegateTask.getProcessInstanceId());
				Depart depart= departMapper.getById(apply.getDepartId());
				ProcessDefinition process = activitiServiceImpl.getProcessDefinition(delegateTask.getProcessDefinitionId()).get(0);
				Long assignee = depart.getLeader();
				List<Long> emps = Lists.newArrayList(assignee);
				if(isSendEmail){
					logger.info("发送待办任务邮件开始,taskId={}",delegateTask.getId());
					sendMailService.sendTaskEmail(emps,process.getName(),proposer.getEmpDepart().getDepart().getName(),proposer.getCnName());
					logger.info("发送待办任务邮件结束,taskId={}",delegateTask.getId());
				}
				taskService.setAssignee(delegateTask.getId(),String.valueOf(assignee));
			}else{
				Employee proposer = employeeService.getByEmpId((Long)delegateTask.getVariable("proposerId")).get(0);
				Map<String, Object> result = employeeService.queryDepartHeadIdByEmpId((Long)delegateTask.getVariable("proposerId"));
				
				ProcessDefinition process = activitiServiceImpl.getProcessDefinition(delegateTask.getProcessDefinitionId()).get(0);
				Long assignee = Long.valueOf(result.get("departLeader").toString().trim());
				List<Long> emps = Lists.newArrayList(assignee);
				if(isSendEmail){
					logger.info("发送待办任务邮件开始,taskId={}",delegateTask.getId());
					sendMailService.sendTaskEmail(emps,process.getName(),proposer.getEmpDepart().getDepart().getName(),proposer.getCnName());
					logger.info("发送待办任务邮件结束,taskId={}",delegateTask.getId());
				}
				taskService.setAssignee(delegateTask.getId(),String.valueOf(assignee));
			}
		} catch (Exception e) {
			logger.error("发送待办任务邮件失败,taskId={}",delegateTask.getId());
			throw new RuntimeException("发送待办任务邮件失败！");
		}
	}

}
