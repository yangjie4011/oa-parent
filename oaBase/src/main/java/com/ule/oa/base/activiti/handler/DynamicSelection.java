package com.ule.oa.base.activiti.handler;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.service.delegate.DelegateTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ule.oa.base.mapper.EmpApplicationBusinessMapper;
import com.ule.oa.base.po.ActPermission;
import com.ule.oa.base.po.EmpApplicationBusiness;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.tbl.ViewTaskInfoTbl;
import com.ule.oa.base.service.ActPermissionService;
import com.ule.oa.base.service.EmpApplicationBusinessService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.SendMailService;
import com.ule.oa.base.service.ViewTaskInfoService;
import com.ule.oa.base.service.impl.ActivitiServiceImpl;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.ConfigConstants;

import org.flowable.task.api.Task;
//根据流程的key和节点的key动态选择办理人
@Component
@SuppressWarnings("serial")
public class DynamicSelection implements TaskListener{
	@Autowired
	private ActivitiServiceImpl activitiServiceImpl;
	@Autowired
	private ActPermissionService actPermissionService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private SendMailService sendMailService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private EmpApplicationBusinessMapper empApplicationBusinessMapper;
	@Autowired
	private ViewTaskInfoService viewTaskInfoService;
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Override
	public void notify(DelegateTask delegateTask){
		boolean isSendEmail = true;//是否发送邮件
		String key = activitiServiceImpl.queryProcessKeyByDefinitionId(delegateTask.getProcessDefinitionId());
		Employee proposer = employeeService.getByEmpId((Long)delegateTask.getVariable("proposerId")).get(0);
		ProcessDefinition process = activitiServiceImpl.getProcessDefinition(delegateTask.getProcessDefinitionId()).get(0);
		String actKey = delegateTask.getTaskDefinitionKey();
		if(ConfigConstants.LEAVE_KEY.equals(key)||ConfigConstants.ABNORMALATTENDANCE_KEY.equals(key) ||ConfigConstants.CANCELLEAVE_KEY.equals(key)){
			Object isAdmin = runtimeService.getVariable(delegateTask.getExecutionId(), "isAdmin");
			if(isAdmin!=null&&(boolean) isAdmin){
				isSendEmail = false;
			}
		}
		ActPermission permission = new ActPermission();
		permission.setProcessKey(key);
		permission.setActId(actKey);
		List<Long> emps = actPermissionService.queryEmpIdByPermission(permission);
		try {
			if(isSendEmail){
				logger.info("发送待办任务邮件开始,taskId={}",delegateTask.getId());
				sendMailService.sendTaskEmail(emps,process.getName(),proposer.getEmpDepart().getDepart().getName(),proposer.getCnName());
				logger.info("发送待办任务邮件结束,taskId={}",delegateTask.getId());
			}
			if(emps.size()>1) {
				List<String> users = emps.stream().map(e-> e.toString().trim()).collect(Collectors.toList()); //内部遍历
				delegateTask.addCandidateUsers(users);
			}else {
				if(StringUtils.equalsIgnoreCase(emps.get(0).toString().trim(),String.valueOf(delegateTask.getVariable("proposerId")))) {
					activitiServiceImpl.completeTask(delegateTask.getId());
					Task task = taskService.createTaskQuery().taskId(delegateTask.getId()).singleResult();
					//申请人和待办人事同一个人，并且任务已经结束
					if(task==null){
						if(ConfigConstants.BUSINESS_KEY.equals(key)){
							EmpApplicationBusiness business = empApplicationBusinessMapper.queryByProcessId(delegateTask.getProcessInstanceId());
							business.setApprovalStatus(ConfigConstants.PASS_STATUS);
							empApplicationBusinessMapper.updateById(business);
							List<ViewTaskInfoTbl> flowInfoList = viewTaskInfoService.queryTasksByProcessId(delegateTask.getProcessInstanceId());
							if(flowInfoList!=null&&flowInfoList.size()>0){
								flowInfoList.get(0).setStatu(ConfigConstants.PASS_STATUS);
								viewTaskInfoService.updateStatusById(flowInfoList.get(0));
							}
						}
					}
				}else {
					//如果是出差流程，则校验人事总监是否审批过，如果审批过则结束任务
					if(ConfigConstants.BUSINESS_KEY.equals(key)){
						//查出所有节点
						List<ViewTaskInfoTbl> flowInfoList = viewTaskInfoService.queryTasksByProcessId(delegateTask.getProcessInstanceId());
						if(flowInfoList != null && flowInfoList.size() > 0){
							for (ViewTaskInfoTbl viewTaskInfoTbl : flowInfoList) {
								if(viewTaskInfoTbl.getPositionName() != null && "人力资源及行政总监".equals(viewTaskInfoTbl.getPositionName())){
									ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
									flow.setAssigneeName(viewTaskInfoTbl.getAssigneeName());
									flow.setDepartName(viewTaskInfoTbl.getDepartName());
									flow.setPositionName(viewTaskInfoTbl.getPositionName());
									flow.setFinishTime(new Date());
									flow.setComment(viewTaskInfoTbl.getComment());
									flow.setTaskId("personnelLeader");
									flow.setProcessId(viewTaskInfoTbl.getProcessId());
									flow.setProcessKey(viewTaskInfoTbl.getProcessKey());
									flow.setStatu(viewTaskInfoTbl.getStatu());
									viewTaskInfoService.save(flow);
									activitiServiceImpl.completeTask(delegateTask.getId());
								}
							}
						}
					}
					taskService.setAssignee(delegateTask.getId(),emps.get(0).toString().trim());
				}
			}
		} catch (Exception e) {
			logger.error("发送待办任务邮件失败,taskId={}",delegateTask.getId());
			throw new RuntimeException("发送待办任务邮件失败！");
		} 
	}
}
