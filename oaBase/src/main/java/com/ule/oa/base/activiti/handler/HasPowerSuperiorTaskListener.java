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
 * 设置当前节点的办理人为单据申请人有权限的上级领导（汇报对象或者汇报对象的上级汇报对象一直往上找--汇报对象的职位序列是总监，副总监，VP，COO,部门负责人）
 * @author yangjie
 *
 */
@Component
@SuppressWarnings("serial")
public class HasPowerSuperiorTaskListener implements TaskListener{
	
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
			//申请人员工id
			Long applyEmployId = (Long)delegateTask.getVariable("proposerId");
			//申请人
			Employee proposer = employeeService.getByEmpId(applyEmployId).get(0);
			//申请人第一级汇报对象
			Long assignee = employeeService.getById(applyEmployId).getReportToLeader();
			//
			int i= 0;
			while(true){
				i = i + 1;
				if(i>10){
					break;//员工汇报关系不可能超过10级
				}
				if(assignee!=null){
					//判断该人职位序列是总监，副总监，VP，COO或者部门负责人
					if(employeeService.hasPowerSuperior(assignee)){
						break;
					}else{
						assignee = employeeService.getById(assignee).getReportToLeader();
					}
				}else{
					break;
				}
			}
			if(i>10){
				throw new RuntimeException("员工汇报关系有问题，请联系人事！");
			}
			if(assignee==null){
				throw new RuntimeException("没有找到汇报对象！");
			}
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
