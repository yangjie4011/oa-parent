package com.ule.oa.base.activiti.handler;

import java.util.ArrayList;
import java.util.List;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.service.delegate.DelegateTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.SendMailService;
import com.ule.oa.base.service.impl.ActivitiServiceImpl;
import com.ule.oa.common.exception.OaException;


/**
 * @ClassName: 常务副总裁
 * @Description: 常务副总裁
 * @author yangjie
 * @date 2019年4月12日
 */
@Component
@SuppressWarnings("serial")
public class VepTaskListener implements TaskListener{

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
	private DepartService departService;
	@Autowired
	private ConfigService configService;
	@Autowired
	private RuntimeService runtimeService;

	@Override
	public void notify(DelegateTask delegateTask) {
		//申请人员工id
		Long employeeId = (Long) runtimeService.getVariable(delegateTask.getExecutionId(), "employeeId");
		//查询申请人所在部门
		Depart depart = departService.getInfoByEmpId(employeeId);
		if(depart!=null){
			//根据部门查询分管副总裁
			Config con = new Config();
			con.setDisplayCode(depart.getCode());
			List<Config> conList = configService.getListByCondition(con);
			if(conList!=null&&conList.size()>0){
				Employee param = new Employee();
				param.setCode(conList.get(0).getUserDef1());
				Employee vep = employeeService.getByCondition(param);
				taskService.setAssignee(delegateTask.getId(),String.valueOf(vep.getId()));
				Employee proposer = employeeService.getByEmpId((Long)delegateTask.getVariable("proposerId")).get(0);
				ProcessDefinition process = activitiServiceImpl.getProcessDefinition(delegateTask.getProcessDefinitionId()).get(0);
				List<Long> emps = new ArrayList<Long>();
				emps.add(vep.getId());
				try {
					logger.info("发送待办任务邮件开始,taskId={}",delegateTask.getId());
					sendMailService.sendTaskEmail(emps,process.getName(),proposer.getEmpDepart().getDepart().getName(),proposer.getCnName());
					logger.info("发送待办任务邮件结束,taskId={}",delegateTask.getId());
				} catch (Exception e) {
					logger.error("发送待办任务邮件失败,taskId={}",delegateTask.getId());
					throw new RuntimeException("发送待办任务邮件失败！");
				}
			}else{
				logger.error("未查到分管副总裁信息,taskId={}",delegateTask.getId());
			}
		}else{
			logger.error("未查到申请人部门信息,taskId={}",delegateTask.getId());
		}
	}
}
