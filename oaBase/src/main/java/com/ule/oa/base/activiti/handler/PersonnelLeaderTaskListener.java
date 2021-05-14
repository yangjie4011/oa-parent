package com.ule.oa.base.activiti.handler;

import java.util.List;
import java.util.stream.Collectors;

import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ule.oa.base.po.ActPermission;
import com.ule.oa.base.service.ActPermissionService;
import com.ule.oa.base.service.impl.ActivitiServiceImpl;

/**
 * 设置当前节点的办理人为人事总监
 * @author wangwencan
 *
 */
@Component
@SuppressWarnings("serial")
public class PersonnelLeaderTaskListener implements TaskListener{
	@Autowired
	private ActivitiServiceImpl activitiServiceImpl;
	@Autowired
	private ActPermissionService actPermissionService;
	@Autowired
	private TaskService taskService;
	@Override
	public void notify(DelegateTask delegateTask) {
		String key = activitiServiceImpl.queryProcessKeyByDefinitionId(delegateTask.getProcessDefinitionId());
		String actKey = delegateTask.getTaskDefinitionKey();
		ActPermission permission = new ActPermission();
		permission.setProcessKey(key);
		permission.setActId(actKey);
		List<Long> emps = actPermissionService.queryEmpIdByPermission(permission);
		if(emps.size()>1) {
			List<String> users = emps.stream().map(e->e.toString().trim()).collect(Collectors.toList()); //内部遍历
			delegateTask.addCandidateUsers(users);
		}else {
			taskService.setAssignee(delegateTask.getId(),emps.get(0).toString().trim());
		}
	}
}
