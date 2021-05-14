package com.ule.oa.base.activiti.handler;

import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ule.oa.base.po.EmpApplicationOvertime;
import com.ule.oa.base.service.EmpApplicationOvertimeService;

/**
 * @ClassName:  加班填写实际工时
 * @Description: 加班填写实际工时
 * @author yangjie
 * @date 2018年5月10日
 */
@Component
@SuppressWarnings("serial")
public class ActualTimeTaskListener implements TaskListener{
	
	@Autowired
	private TaskService taskService;
	@Autowired
	private EmpApplicationOvertimeService empApplicationOvertimeService;
	
	@Override
	public void notify(DelegateTask delegateTask) {
		EmpApplicationOvertime overtime = empApplicationOvertimeService.queryByProcessInstanceId(delegateTask.getProcessInstanceId());
		taskService.setAssignee(delegateTask.getId(),String.valueOf(overtime.getEmployeeId()));
	}
}
