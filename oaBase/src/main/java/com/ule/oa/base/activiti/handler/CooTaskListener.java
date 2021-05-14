package com.ule.oa.base.activiti.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.service.delegate.DelegateTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ule.oa.base.po.EmpApplicationBusiness;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.tbl.ViewTaskInfoTbl;
import com.ule.oa.base.service.EmpApplicationBusinessService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.SendMailService;
import com.ule.oa.base.service.ViewTaskInfoService;
import com.ule.oa.base.service.impl.ActivitiServiceImpl;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.ConfigConstants;

/**
 * @ClassName: 运营总裁
 * @Description: 运营总裁
 * @author yangjie
 * @date 2019年4月12日
 */
@Component
@SuppressWarnings("serial")
public class CooTaskListener implements TaskListener{
	
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private TaskService taskService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private SendMailService sendMailService;
	@Autowired
	private ActivitiServiceImpl activitiServiceImpl;
	@Autowired
	private ViewTaskInfoService viewTaskInfoService;
	@Autowired
	private EmpApplicationBusinessService empApplicationBusinessService;

	@Override
	public void notify(DelegateTask delegateTask) {
		String key = activitiServiceImpl.queryProcessKeyByDefinitionId(delegateTask.getProcessDefinitionId());
		Employee param = new Employee();
		param.setCode("TOM001");
		Employee coo = employeeService.getByCondition(param);
		taskService.setAssignee(delegateTask.getId(),String.valueOf(coo.getId()));
		Employee proposer = employeeService.getByEmpId((Long)delegateTask.getVariable("proposerId")).get(0);
		ProcessDefinition process = activitiServiceImpl.getProcessDefinition(delegateTask.getProcessDefinitionId()).get(0);
		List<Long> emps = new ArrayList<Long>();
		emps.add(coo.getId());
		try {
			if(ConfigConstants.BUSINESS_KEY.equals(key)){
				//判断coo是否审批过
				ViewTaskInfoTbl viewTask = new ViewTaskInfoTbl();
				boolean flag = false;
				List<ViewTaskInfoTbl> flowInfoList = viewTaskInfoService.queryTasksByProcessId(delegateTask.getProcessInstanceId());
				for (ViewTaskInfoTbl viewTaskInfoTbl : flowInfoList) {
					if(StringUtils.isNotBlank(viewTaskInfoTbl.getPositionName()) && "运营总裁".equals(viewTaskInfoTbl.getPositionName())){
						viewTask  = viewTaskInfoTbl;
						flag = true;
						break;
					}
				}
				if(flag) {
					ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
					flow.setAssigneeName(viewTask.getAssigneeName());
					flow.setDepartName(viewTask.getDepartName());
					flow.setPositionName(viewTask.getPositionName());
					flow.setFinishTime(new Date());
					flow.setComment(viewTask.getComment());
					flow.setTaskId("end_coo");
					flow.setProcessId(viewTask.getProcessId());
					flow.setProcessKey(viewTask.getProcessKey());
					flow.setStatu(viewTask.getStatu());
					viewTaskInfoService.save(flow);
					activitiServiceImpl.completeTask(delegateTask.getId());
					EmpApplicationBusiness b = empApplicationBusinessService.queryByProcessInstanceId(delegateTask.getProcessInstanceId());
					b.setApprovalStatus(ConfigConstants.PASS_STATUS);
					b.setUpdateTime(new Date());
					b.setUpdateUser(coo.getCnName());
					empApplicationBusinessService.updateById(b,"1");
				}else {
					logger.info("发送待办任务邮件开始,taskId={}",delegateTask.getId());
					sendMailService.sendTaskEmail(emps,process.getName(),proposer.getEmpDepart().getDepart().getName(),proposer.getCnName());
					logger.info("发送待办任务邮件结束,taskId={}",delegateTask.getId());
				}
			}else if(ConfigConstants.BUSINESSREPORT_KEY.equals(key)){
				//判断coo是否审批过
				ViewTaskInfoTbl viewTask = new ViewTaskInfoTbl();
				boolean flag = false;
				List<ViewTaskInfoTbl> flowInfoList = viewTaskInfoService.queryTasksByProcessId(delegateTask.getProcessInstanceId());
				for (ViewTaskInfoTbl viewTaskInfoTbl : flowInfoList) {
					if(StringUtils.isNotBlank(viewTaskInfoTbl.getPositionName()) && "运营总裁".equals(viewTaskInfoTbl.getPositionName())){
						viewTask  = viewTaskInfoTbl;
						flag = true;
						break;
					}
				}
				if(flag) {
					ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
					flow.setAssigneeName(viewTask.getAssigneeName());
					flow.setDepartName(viewTask.getDepartName());
					flow.setPositionName(viewTask.getPositionName());
					flow.setFinishTime(new Date());
					flow.setComment(viewTask.getComment());
					flow.setTaskId("end_coo");
					flow.setProcessId(viewTask.getProcessId());
					flow.setProcessKey(viewTask.getProcessKey());
					flow.setStatu(viewTask.getStatu());
					viewTaskInfoService.save(flow);
					activitiServiceImpl.completeTask(delegateTask.getId());
					EmpApplicationBusiness b = empApplicationBusinessService.queryByReportProcessInstanceId(delegateTask.getProcessInstanceId());
					b.setApprovalReportStatus(ConfigConstants.PASS_STATUS);
					b.setUpdateTime(new Date());
					b.setUpdateUser(coo.getCnName());
					empApplicationBusinessService.updateById(b,"2");
				}else {
					logger.info("发送待办任务邮件开始,taskId={}",delegateTask.getId());
					sendMailService.sendTaskEmail(emps,process.getName(),proposer.getEmpDepart().getDepart().getName(),proposer.getCnName());
					logger.info("发送待办任务邮件结束,taskId={}",delegateTask.getId());
				}
			}else {
				logger.info("发送待办任务邮件开始,taskId={}",delegateTask.getId());
				sendMailService.sendTaskEmail(emps,process.getName(),proposer.getEmpDepart().getDepart().getName(),proposer.getCnName());
				logger.info("发送待办任务邮件结束,taskId={}",delegateTask.getId());
			}
		} catch (OaException e) {
			logger.error("发送待办任务邮件失败,taskId={}",delegateTask.getId());
			throw new RuntimeException("发送待办任务邮件失败！");
		} catch (Exception e) {
			logger.error("processId="+delegateTask.getProcessInstanceId()+"审批失败",e);
		}
		
	}

}
