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
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.SendMailService;
import com.ule.oa.base.service.impl.ActivitiServiceImpl;

/**
 * 判断当前对象是否是部门负责人，是的话直接跳到下个节点
 * @author 杨杰
 *
 */
@Component
@SuppressWarnings("serial")
public class CheckLeaderIsDepartLeader implements TaskListener{
	
	@Autowired
	private DepartService departService;
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
		//一级汇报对象
		Long firstLeader = employeeService.getById((Long)delegateTask.getVariable("proposerId")).getReportToLeader();
		//两级汇报对象
		Long secondLeader = employeeService.getById(firstLeader).getReportToLeader();
		//判断二级汇报对象是否是部门负责人
		if(departService.checkLeaderIsDh(secondLeader)){
			//二级汇报对象是部门负责人，直接跳过（跳到部门负责人节点）
			taskService.complete(delegateTask.getId());
		}else{
			try{
				Employee proposer = employeeService.getByEmpId((Long)delegateTask.getVariable("proposerId")).get(0);
				ProcessDefinition process = activitiServiceImpl.getProcessDefinition(delegateTask.getProcessDefinitionId()).get(0);
				List<Long> emps = Lists.newArrayList(secondLeader);
				logger.info("发送待办任务邮件开始,taskId={}",delegateTask.getId());
				sendMailService.sendTaskEmail(emps,process.getName(),proposer.getEmpDepart().getDepart().getName(),proposer.getCnName());
				logger.info("发送待办任务邮件结束,taskId={}",delegateTask.getId());
				taskService.setAssignee(delegateTask.getId(),String.valueOf(secondLeader));
			}catch(Exception e){
				logger.error("发送待办任务邮件失败,taskId={}",delegateTask.getId());
				throw new RuntimeException("发送待办任务邮件失败！");
			}
			
		}
		
	}

}
