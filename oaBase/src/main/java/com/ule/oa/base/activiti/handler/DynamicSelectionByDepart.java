package com.ule.oa.base.activiti.handler;

import java.util.ArrayList;
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
import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.service.ActPermissionService;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.SendMailService;
import com.ule.oa.base.service.impl.ActivitiServiceImpl;
import com.ule.oa.common.exception.OaException;
//根据流程的key和节点的key动态选择办理人
@Component
@SuppressWarnings("serial")
public class DynamicSelectionByDepart implements TaskListener{
	@Autowired
	private ActivitiServiceImpl activitiServiceImpl;
	@Autowired
	private ActPermissionService actPermissionService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private SendMailService sendMailService;
	@Autowired
	private ConfigService configService;
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Override
	public void notify(DelegateTask delegateTask) {
		String key = activitiServiceImpl.queryProcessKeyByDefinitionId(delegateTask.getProcessDefinitionId());
		String actKey = delegateTask.getTaskDefinitionKey();
		 Employee proposer = employeeService.getByEmpId((Long)delegateTask.getVariable("proposerId")).get(0);
		 ProcessDefinition process = activitiServiceImpl.getProcessDefinition(delegateTask.getProcessDefinitionId()).get(0);
		ActPermission permission = new ActPermission();
		Long proposerId = (Long)delegateTask.getVariable("proposerId");
		permission.setProcessKey(key);
		permission.setActId(actKey);
		//設置申請人部門id
		permission.setDepartId(String.valueOf(employeeService.queryDepartHeadIdByEmpId(proposerId).get("departId")));
		List<Long> emps = actPermissionService.queryEmpIdByPermission(permission);
		try {
			logger.info("发送待办任务邮件开始,taskId={}",delegateTask.getId());
			sendMailService.sendTaskEmail(emps,process.getName(),proposer.getEmpDepart().getDepart().getName(),proposer.getCnName());
			logger.info("发送待办任务邮件结束,taskId={}",delegateTask.getId());
			if(emps.size()>1) {
				List<String> users = emps.stream().map(e-> e.toString().trim()).collect(Collectors.toList()); //内部遍历
				delegateTask.addCandidateUsers(users);
			}else {
				if(emps.size()>0){
					taskService.setAssignee(delegateTask.getId(),emps.get(0).toString().trim());
				}else{
					//查询默认待办人
					List<Config> daibanList = configService.getListByCode("HR_DEFAULT_DAIBAN_PRESON");
					List<String> users = new ArrayList<>();
					for(Config data:daibanList){
						Employee daiban = employeeService.getEmployeeByCode(data.getDisplayCode());
						if(daiban!=null&&daiban.getId()!=null){
							users.add(daiban.getId().toString());
						}
					}
					if(users!=null&&users.size()>0){
						delegateTask.addCandidateUsers(users);
					}else{
						logger.error("没有指定待办人,taskId={}",delegateTask.getId());
						throw new RuntimeException("发送待办任务邮件失败！");
					}
				}
			}
		} catch (Exception e) {
			logger.error("发送待办任务邮件失败,taskId={}",delegateTask.getId());
			throw new RuntimeException("发送待办任务邮件失败！");
		} 
	}
}
