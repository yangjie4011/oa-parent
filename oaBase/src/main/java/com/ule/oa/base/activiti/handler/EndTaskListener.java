package com.ule.oa.base.activiti.handler;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ule.oa.base.po.User;
import com.ule.oa.base.service.ApplicationEmployeeClassService;
import com.ule.oa.base.service.ApplicationEmployeeDutyService;
import com.ule.oa.base.service.EmpApplicationLeaveAbolishService;
import com.ule.oa.base.service.EmpApplicationOvertimeService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.service.impl.ActivitiServiceImpl;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.ConfigConstants;


/**
 * @ClassName: 流程结束节点
 * @Description: 流程最终成功结束，处理最终数据
 * @author yangjie
 * @date 2018年5月8日
 */
@Component
@SuppressWarnings("serial")
public class EndTaskListener implements ExecutionListener{
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private UserService userService;
	@Autowired
	private ActivitiServiceImpl activitiServiceImpl;
	@Autowired
	private ApplicationEmployeeClassService applicationEmployeeClassService;
	@Autowired
	private EmpApplicationOvertimeService empApplicationOvertimeService;
	@Autowired
	private ApplicationEmployeeDutyService applicationEmployeeDutyService;
	@Autowired
	private EmpApplicationLeaveAbolishService empApplicationLeaveAbolishService;


	@Override
	public void notify(DelegateExecution execution) {
		String key = activitiServiceImpl.queryProcessKeyByDefinitionId(execution.getProcessDefinitionId());
		User user = userService.getCurrentUser();
		//排班，调班申请
		if(ConfigConstants.SCHEDULING_KEY.equals(key)){
			logger.info("排班最终数据生成start"+execution.getProcessInstanceId());
			applicationEmployeeClassService.endHandle(execution.getProcessInstanceId(), user);
			logger.info("排班最终数据生成end"+execution.getProcessInstanceId());
		}else if(ConfigConstants.OVERTIME_KEY.equals(key)){
			logger.info("加班充值调休start"+execution.getProcessInstanceId());
			try {
				empApplicationOvertimeService.endHandle(execution.getProcessInstanceId(), user);
			} catch (Exception e) {
				logger.error("加班充值调休:"+e.getMessage());
				throw new RuntimeException(); 
			}
			logger.info("加班充值调休end"+execution.getProcessInstanceId());
		}else if(ConfigConstants.DUTY_KEY.equals(key)){
			logger.info("值班最终数据生成start"+execution.getProcessInstanceId());
			applicationEmployeeDutyService.endHandle(execution.getProcessInstanceId(), user);
			logger.info("值班最终数据生成end"+execution.getProcessInstanceId());
		}else if(ConfigConstants.CANCELLEAVE_KEY.equals(key)){
			logger.info("销假最终还假期start"+execution.getProcessInstanceId());
			try {
				empApplicationLeaveAbolishService.endHandle(execution.getProcessInstanceId(), user);
			} catch (Exception e) {
				logger.error("销假最终还假期:"+e.getMessage());
				throw new RuntimeException(); 
				
			}
			logger.info("销假最终还假期end"+execution.getProcessInstanceId());
		}			
	}

}
