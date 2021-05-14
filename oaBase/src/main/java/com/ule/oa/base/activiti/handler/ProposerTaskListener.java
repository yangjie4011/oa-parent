package com.ule.oa.base.activiti.handler;

import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ule.oa.base.po.User;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.service.impl.ActivitiServiceImpl;
import com.ule.oa.common.utils.ConfigConstants;

/**
 * 设置流程节点的的办理人为当前登录用户
 * @author wangwencan
 *
 */
@Component
@SuppressWarnings("serial")
public class ProposerTaskListener implements TaskListener {
	
	@Autowired
	private UserService userService;
	@Autowired
	private ActivitiServiceImpl activitiServiceImpl;
	@Autowired
	private TaskService taskService;
//	@Autowired
//	private EmployeeService employeeService;
	@Override
	public void notify(DelegateTask delegateTask) {
		
		User proposer = userService.getCurrentUser();
		taskService.setAssignee(delegateTask.getId(),String.valueOf(proposer.getEmployeeId()));
		if(delegateTask.getVariable("proposerId")==null){//proposerId不为空的时候，是别人给申请人申请的单据
			//设置参数,添加申请人id
			delegateTask.setVariable("proposerId", proposer.getEmployeeId());
		}
		//流程自动提交
		if(StringUtils.equalsIgnoreCase(delegateTask.getTaskDefinitionKey(),ConfigConstants.AUTOCOMMIT_KEY)) {
			activitiServiceImpl.completeTask(delegateTask.getId());
		}
	}
}
