package com.ule.oa.base.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.flowable.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ule.oa.base.po.ApplicationEmployeeClass;
import com.ule.oa.base.po.ApplicationEmployeeDuty;
import com.ule.oa.base.po.EmpApplicationAbnormalAttendance;
import com.ule.oa.base.po.EmpApplicationBusiness;
import com.ule.oa.base.po.EmpApplicationLeave;
import com.ule.oa.base.po.EmpApplicationLeaveAbolish;
import com.ule.oa.base.po.EmpApplicationOutgoing;
import com.ule.oa.base.po.EmpApplicationOvertime;
import com.ule.oa.base.po.EmpApplicationRegister;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.User;
import com.ule.oa.base.po.tbl.ViewTaskInfoTbl;
import com.ule.oa.base.service.ApplicationEmployeeClassService;
import com.ule.oa.base.service.ApplicationEmployeeDutyService;
import com.ule.oa.base.service.BaseEmpWorkLogService;
import com.ule.oa.base.service.EmpApplicationAbnormalAttendanceService;
import com.ule.oa.base.service.EmpApplicationBusinessService;
import com.ule.oa.base.service.EmpApplicationLeaveAbolishService;
import com.ule.oa.base.service.EmpApplicationLeaveService;
import com.ule.oa.base.service.EmpApplicationOutgoingService;
import com.ule.oa.base.service.EmpApplicationOvertimeService;
import com.ule.oa.base.service.EmpApplicationRegisterService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.RemoveSubordinateAbsenceService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.service.UserTaskService;
import com.ule.oa.base.service.ViewMyTaskService;
import com.ule.oa.base.service.ViewTaskInfoService;
import com.ule.oa.base.vo.TaskVO;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.PageModel;

@Service
public class UserTaskServiceImpl implements UserTaskService{
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private ActivitiServiceImpl activitiServiceImpl;
	@Autowired
	private EmpApplicationLeaveService empApplicationLeaveService;
	@Autowired
	private UserService userService;
	@Autowired
	private ApplicationEmployeeClassService applicationEmployeeClassService;
	@Autowired
	private EmpApplicationOvertimeService empApplicationOvertimeService;
	@Autowired
	private ApplicationEmployeeDutyService applicationEmployeeDutyService;
	@Autowired
	private EmpApplicationAbnormalAttendanceService abnormalAttendanceService;
	@Autowired
	private EmpApplicationRegisterService empApplicationRegisterService;
	@Autowired
	private ViewTaskInfoService viewTaskInfoService;
	@Autowired
	private ViewMyTaskService viewMyTaskService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private EmpApplicationOutgoingService empApplicationOutgoingService;
	@Autowired
	private EmpApplicationBusinessService empApplicationBusinessService;
	@Autowired
	private EmpApplicationLeaveAbolishService empApplicationLeaveAbolishService;
	@Autowired
	private RemoveSubordinateAbsenceService removeSubordinateAbsenceService;
	@Autowired
	private BaseEmpWorkLogService baseEmpWorkLogService;
	/**
	 * 查询员工待办任务总数
	 */
	@Override
	public Long queryTaskCountByEmpId(Long empId) {
		if(null == empId) {
			empId = userService.getCurrentUser().getEmployee().getId();
		}
		return activitiServiceImpl.queryTaskCountByEmpId(empId);
	}

	@Override
	public PageModel<TaskVO> queryTaskInfoList(String flag,PageModel<TaskVO> page) {
	    //新增过期状态，这块代码需要改一下
		Long total = queryTaskCountByEmpId(null);
		PageModel<TaskVO> pm = new PageModel<TaskVO>();
		pm.setPageNo(1);
		List<Task> tasks = activitiServiceImpl.getUserHaveTasks(userService.getCurrentUser().getEmployee().getId());
		List<TaskVO> rus = Lists.newArrayList();
		for (Task task : tasks) {
			TaskVO taskVO = new TaskVO();
			taskVO.setProcessStatu(100);
			//流程key
			String key = activitiServiceImpl.queryProcessKeyByDefinitionId(task.getProcessDefinitionId());
			switch (key) {
			case  ConfigConstants.LEAVE_KEY://假期任务
				empApplicationLeaveService.setValueToVO(taskVO,task.getProcessInstanceId());
				if(taskVO.getProcessStatu().intValue()==ConfigConstants.OVERDUE_STATUS.intValue()){
					total = total - 1;
				}else{
					rus.add(taskVO);
				}
				break;
			case  ConfigConstants.CANCELLEAVE_KEY://销假申请
				empApplicationLeaveAbolishService.setValueToVO(taskVO,task.getProcessInstanceId());
				if(taskVO.getProcessStatu().intValue()==ConfigConstants.OVERDUE_STATUS.intValue()){
					total = total - 1;
				}else{
					rus.add(taskVO);
				}
			    break;
			case ConfigConstants.SCHEDULING_KEY://排班任务
				applicationEmployeeClassService.setValueToVO(taskVO, task.getProcessInstanceId());
				rus.add(taskVO);
				break;
			case ConfigConstants.OVERTIME_KEY://加班任务
				empApplicationOvertimeService.setValueToVO(taskVO,task.getProcessInstanceId(),task.getTaskDefinitionKey());
				if(taskVO.getProcessStatu().intValue()==ConfigConstants.OVERDUE_STATUS.intValue()){
					total = total - 1;
				}else{
					rus.add(taskVO);
				}
				break;
			case ConfigConstants.DUTY_KEY://值班任务
				applicationEmployeeDutyService.setValueToVO(taskVO,task.getProcessInstanceId());
				rus.add(taskVO);
				break;
			case ConfigConstants.ABNORMALATTENDANCE_KEY://考勤异常任务
				abnormalAttendanceService.setValueToVO(taskVO,task.getProcessInstanceId());
				if(taskVO.getProcessStatu().intValue()==ConfigConstants.OVERDUE_STATUS.intValue()){
					total = total - 1;
				}else{
					rus.add(taskVO);
				}
				break;
			case ConfigConstants.OUTGOING_KEY://外出任务
				empApplicationOutgoingService.setValueToVO(taskVO,task.getProcessInstanceId());
				if(taskVO.getProcessStatu().intValue()==ConfigConstants.OVERDUE_STATUS.intValue()){
					total = total - 1;
				}else{
					rus.add(taskVO);
				}
				break;
			case ConfigConstants.BUSINESS_KEY://出差任务
				empApplicationBusinessService.setValueToVO(taskVO,task.getProcessInstanceId());
				if(taskVO.getProcessStatu().intValue()==ConfigConstants.OVERDUE_STATUS.intValue()){
					total = total - 1;
				}else{
					rus.add(taskVO);
				}
				break;
			case ConfigConstants.BUSINESSREPORT_KEY://出差任务
				empApplicationBusinessService.setValueToReportVO(taskVO,task.getProcessInstanceId());
				if(taskVO.getProcessStatu().intValue()==ConfigConstants.OVERDUE_STATUS.intValue()){
					total = total - 1;
				}else{
					rus.add(taskVO);
				}
				break;
			case ConfigConstants.ENTRY_KEY://新员工入职
				empApplicationRegisterService.setValueToVO(taskVO,task.getProcessInstanceId(),task.getId());
				rus.add(taskVO);
				break;
			case ConfigConstants.REMOVESUBABSENCE_KEY://消下属缺勤任务
				removeSubordinateAbsenceService.setValueToVO(taskVO,task.getProcessInstanceId());
				if(taskVO.getProcessStatu().intValue()==ConfigConstants.OVERDUE_STATUS.intValue()){
					total = total - 1;
				}else{
					rus.add(taskVO);
				}
				break;
			default:
				break;
			}
		}
		pm.setTotal(total.intValue());
		pm.setRows(rus);
		return pm;
	}
	
	//已办任务
	@Override
	public PageModel<TaskVO> queryHiTaskInfoList(String flag,PageModel<TaskVO> pm) {
		Long total = queryTaskCountByEmpId(null);
		pm.setTotal(total.intValue());
		List<Map<String,Object>> hiTasks = activitiServiceImpl.getHistoryAssigneeActivitiIds(String.valueOf(userService.getCurrentUser().getEmployeeId()),pm.getPageNo(),pm.getPageSize());
		List<TaskVO> rus = Lists.newArrayList();
		for (Map<String,Object> map:hiTasks) {
			TaskVO taskVO = new TaskVO();
			String instanceId = (String)map.get("instanceId");
			String definitionId = (String)map.get("definitionId");
			String taskDefinitionKey = (String)map.get("taskDefinitionKey");
			//流程key
			String key = activitiServiceImpl.queryProcessKeyByDefinitionId(definitionId);
			switch (key) {
			case  ConfigConstants.LEAVE_KEY://假期任务
				empApplicationLeaveService.setValueToVO(taskVO,instanceId);
				break;
			case  ConfigConstants.CANCELLEAVE_KEY://销假申请
				empApplicationLeaveAbolishService.setValueToVO(taskVO,instanceId);
				break;
			case ConfigConstants.SCHEDULING_KEY://排班任务
				applicationEmployeeClassService.setValueToVO(taskVO,instanceId);
				break;
			case ConfigConstants.OVERTIME_KEY://加班任务
				empApplicationOvertimeService.setValueToVO1(taskVO,instanceId,taskDefinitionKey);
				break;
			case ConfigConstants.DUTY_KEY://值班任务
				applicationEmployeeDutyService.setValueToVO(taskVO,instanceId);
				break;
			case ConfigConstants.ABNORMALATTENDANCE_KEY://考勤异常任务
				abnormalAttendanceService.setValueToVO(taskVO,instanceId);
				break;
			case ConfigConstants.OUTGOING_KEY://外出任务
				empApplicationOutgoingService.setValueToVO(taskVO,instanceId);
				break;	
			case ConfigConstants.BUSINESS_KEY://出差任务
				empApplicationBusinessService.setValueToVO(taskVO,instanceId);
				break;
			case ConfigConstants.BUSINESSREPORT_KEY://出差任务
				empApplicationBusinessService.setValueToReportVO(taskVO,instanceId);
				break;
			case ConfigConstants.REMOVESUBABSENCE_KEY://消下属缺勤任务
				removeSubordinateAbsenceService.setValueToVO(taskVO,instanceId);
				break;
			
			default:
				break;
			}
			if(taskVO.getProcessId()!=null&&!"".equals(taskVO.getProcessId())){
				rus.add(taskVO);
			}
		}
		pm.setRows(rus);
		return pm;
	}
	/**
	 *	执行任务：
	 *	1.审核通过：保存任务节点在act_task_info中
	 *	2.审核拒绝：保存任务节点在act_task_info中,同时修改相关业务表单数据的状态
	 *	3.单据撤回：保存任务节点在act_task_info中,同时修改相关业务表单数据的状态
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void completeTask(HttpServletRequest request,String processId, String comment, String commentType) throws Exception{
		//通过：completeAndComment
		//拒绝：deleteReason=refuse
		//撤回：deleteReason=back,理由填在variable中{key:back,value:comment}
			String key = activitiServiceImpl.queryKeyByInstanceId(processId);
			switch (key) {
			//假期任务完成逻辑
			case ConfigConstants.LEAVE_KEY:
				empApplicationLeaveService.completeTask(request,processId,comment,commentType);
				break;
			//销假申请完成逻辑
			case ConfigConstants.CANCELLEAVE_KEY:
				empApplicationLeaveAbolishService.completeTask(request,processId,comment,commentType);
				break;
			//排班任务完成逻辑
			case ConfigConstants.SCHEDULING_KEY:
				applicationEmployeeClassService.completeTask(request,processId, comment, commentType);
				break;
			//加班任务完成逻辑
			case ConfigConstants.OVERTIME_KEY:
				empApplicationOvertimeService.completeTask(request,processId, comment, commentType,null);
				break;
			//值班任务完成逻辑
			case ConfigConstants.DUTY_KEY:
				applicationEmployeeDutyService.completeTask(request,processId, comment, commentType);
				break;
			//异常考勤完成逻辑：
			case ConfigConstants.ABNORMALATTENDANCE_KEY:
				abnormalAttendanceService.completeTask(request,processId, comment, commentType);
				break;
			//外出完成逻辑：
			case ConfigConstants.OUTGOING_KEY:
				empApplicationOutgoingService.completeTask(request,processId, comment, commentType);
				break;	
			//出差完成逻辑：
			case ConfigConstants.BUSINESS_KEY:
				empApplicationBusinessService.completeTask(request,processId, comment, commentType);
				break;
			//出差报告完成逻辑：
			case ConfigConstants.BUSINESSREPORT_KEY:
				empApplicationBusinessService.completeReportTask(request,processId, comment, commentType);
				break;
			//消下属缺勤完成逻辑：
			case ConfigConstants.REMOVESUBABSENCE_KEY:
				removeSubordinateAbsenceService.completeTask(request,processId, comment, commentType);
				break;
			//员工日志完成逻辑：
			case ConfigConstants.WORK_LOG_KEY:
				baseEmpWorkLogService.completeTask(request,processId, comment, commentType);
				break;	
			//FIXME:流程在这添加
				
			default:
				break;
		}
	}
	
	/**
	 * 更换成viewMyTaskService
	 * 我的申请页面:已拒绝,已撤回,已完成：查历史表
	 * 我的申请页面:处理中：查历史表			
	 */
	@Override
	public PageModel<Map<String,Object>> queryMyHiTaskInfoList(String flag, PageModel<Map<String,Object>> pm) {
		Map<String,Object> param = Maps.newHashMap();
		param.put("pageNo", pm.getPageNo()*pm.getPageSize());
		param.put("pageSize", pm.getPageSize());
		if(!StringUtils.equalsIgnoreCase(flag,"all")) {
			String[] statusArryStr = flag.split(",");
			List<Integer> statusArry=new ArrayList<Integer>();
			if(statusArryStr.length>=1){
				for (int i = 0; i < statusArryStr.length; i++) {
					statusArry.add(Integer.parseInt(statusArryStr[i]));
				}
			}else{
				statusArry.add(Integer.parseInt(flag));
			}
			param.put("statu", statusArry);
		}
		List<Map<String, Object>> result = viewMyTaskService.queryMyAskTask(param);
		pm.setRows(result);
		return pm;
	}
	
	/**
	 * 更换成viewMyTaskService
	 * 我的申请页面:已拒绝,已撤回,已完成：查历史表
	 * 我的申请页面:处理中：查历史表			
	 */
	@Override
	public PageModel<Map<String,Object>> queryTaskList(Map<String,Object> map, PageModel<Map<String,Object>> pm) {
		if(map.get("departId")!= null) {
			List<Employee> employees = employeeService.getEmpsByDepart((String)map.get("departId"));
			map.put("emps", employees);
		}
		if(map.get("nameOrCode")!= null) {
			List<Employee> emp = employeeService.queryByNameOrCode((String)map.get("nameOrCode"));
			map.put("nameOrCode", emp);
		}
		map.put("pageNo", pm.getPageNo()*pm.getPageSize());
		map.put("pageSize", pm.getPageSize());
		List<Map<String, Object>> result = viewMyTaskService.queryAskTask(map);
		pm.setRows(result);
		return pm;
	}
	
	/**
	 * 查询流程走向
	 */
	@Override
	public List<ViewTaskInfoTbl> queryTaskFlow(String instanceId,int statu) {
		List<ViewTaskInfoTbl> flows = viewTaskInfoService.queryTasksByProcessId(instanceId);
		//状态为处理中,则还缺一个节点
		if(statu==ConfigConstants.DOING_STATUS.intValue()||statu==ConfigConstants.ASK_STATUS.intValue()||statu==ConfigConstants.OVERDUE_STATUS.intValue()) {
			List<Task> taskList = activitiServiceImpl.queryTaskListByProcessInstanceId(instanceId);
			
			for(int i=0;i<taskList.size();i++){
				Task task = taskList.get(i);
				ViewTaskInfoTbl endFlow = new ViewTaskInfoTbl();
				endFlow.setStatu(null);	
				if(statu==ConfigConstants.ASK_STATUS.intValue()) {
					endFlow.setStatu(ConfigConstants.ASK_STATUS);	
				}
				
				if(StringUtils.isBlank(task.getAssignee())||StringUtils.equalsIgnoreCase(task.getAssignee(), "null")) {
					//待办为空,则取节点name
					endFlow.setDepartName(task.getName());
				}else {
					Map<String, String> result = employeeService.queryEmpBaseInfoById(Long.valueOf(task.getAssignee()));
					endFlow.setAssigneeName(result.get("cnName"));
					endFlow.setDepartName(result.get("departName"));
					endFlow.setPositionName(result.get("positionName"));
				}
				
				flows.add(i,endFlow);
			}
		
		}
		return flows;
	}

	@Override
	public String getForwardSuccessUrl(Task task) {
		//流程key
		String key = activitiServiceImpl.queryProcessKeyByDefinitionId(task.getProcessDefinitionId());
		String forwardSuccessUrl = "forward:";
		switch (key) {
		case  ConfigConstants.LEAVE_KEY://假期任务
			EmpApplicationLeave leaveInfo = empApplicationLeaveService.queryByProcessInstanceId(task.getProcessInstanceId());
			forwardSuccessUrl += "/empApplicationLeave/approval.htm?flag=can&leaveId="+leaveInfo.getId();
			break;
		case  ConfigConstants.CANCELLEAVE_KEY://销假申请
			EmpApplicationLeaveAbolish leaveAbolish = empApplicationLeaveAbolishService.queryByProcessInstanceId(task.getProcessInstanceId());
			forwardSuccessUrl += "/empApplicationLeaveBack/approve.htm?flag=can&leaveId="+leaveAbolish.getId();
			break;
		case ConfigConstants.SCHEDULING_KEY://排班任务
			ApplicationEmployeeClass employeeClass = applicationEmployeeClassService.queryByProcessInstanceId(task.getProcessInstanceId());
			forwardSuccessUrl += "/employeeClass/approveNormal.htm?flag=can&employeeClassId="+employeeClass.getId();
			break;
		case ConfigConstants.OVERTIME_KEY://加班任务
			EmpApplicationOvertime overtime = empApplicationOvertimeService.queryByProcessInstanceId(task.getProcessInstanceId());
			forwardSuccessUrl += "/empApplicationOvertime/approval.htm?flag=can&overtimeId="+overtime.getId();
			if("actualTime".equals(task.getTaskDefinitionKey())){
				forwardSuccessUrl += "/empApplicationOvertime/toAddActualTime.htm?flag=can&overtimeId="+overtime.getId();
	    	}
			break;
		case ConfigConstants.DUTY_KEY://值班任务
			ApplicationEmployeeDuty duty = applicationEmployeeDutyService.queryByProcessInstanceId(task.getProcessInstanceId());
			forwardSuccessUrl += "/employeeClass/approveVacation.htm?flag=can&dutyId="+duty.getId();
			break;
		case ConfigConstants.ABNORMALATTENDANCE_KEY://考勤异常任务
			EmpApplicationAbnormalAttendance attendance = abnormalAttendanceService.queryByProcessInstanceId(task.getProcessInstanceId());
			forwardSuccessUrl += "/empApplicationAbnormalAttendance/approval.htm?flag=can&attendanceId="+attendance.getId();
			break;
		case ConfigConstants.OUTGOING_KEY://外出任务
			EmpApplicationOutgoing outgoing = empApplicationOutgoingService.queryByProcessInstanceId(task.getProcessInstanceId());
			forwardSuccessUrl += "/empApplicationOutgoing/approval.htm?flag=can&outgoingId="+outgoing.getId();
			break;
		case ConfigConstants.BUSINESS_KEY://出差任务
			EmpApplicationBusiness business = empApplicationBusinessService.queryByProcessInstanceId(task.getProcessInstanceId());
			forwardSuccessUrl += "/empApplicationBusiness/approval.htm?flag=can&businessId="+business.getId();
			break;
		case ConfigConstants.BUSINESSREPORT_KEY://出差报告任务
			EmpApplicationBusiness businessReport = empApplicationBusinessService.queryByReportProcessInstanceId(task.getProcessInstanceId());
			forwardSuccessUrl += "/empApplicationBusiness/approveWorkSummary.htm?flag=can&businessId="+businessReport.getId();
			break;
		case ConfigConstants.ENTRY_KEY://新员工入职
			EmpApplicationRegister register = empApplicationRegisterService.queryByProcessInstanceId(task.getProcessInstanceId());
			forwardSuccessUrl += "/employeeRegister/toHandle.htm?flag=can&id="+register.getId()+"&taskId="+task.getId();
			break;
		default:
			forwardSuccessUrl = "";
			break;
		}
		return forwardSuccessUrl;
	}

	@Override
	@Transactional(rollbackFor=Exception.class,propagation = Propagation.REQUIRES_NEW)
	public void deployTask(String fileName, String fileCnName) {
		activitiServiceImpl.deploy(fileName, fileCnName);
	}

	@Override
	public PageModel<TaskVO> queryOverDueTaskInfoList(String flag,
			PageModel<TaskVO> page) {
	    //新增过期状态，这块代码需要改一下
		Long total = queryTaskCountByEmpId(null);
		PageModel<TaskVO> pm = new PageModel<TaskVO>();
		pm.setPageNo(1);
		List<Task> tasks = activitiServiceImpl.getUserHaveTasks(userService.getCurrentUser().getEmployee().getId());
		List<TaskVO> rus = Lists.newArrayList();
		for (Task task : tasks) {
			TaskVO taskVO = new TaskVO();
			//流程key
			String key = activitiServiceImpl.queryProcessKeyByDefinitionId(task.getProcessDefinitionId());
			switch (key) {
			case  ConfigConstants.LEAVE_KEY://假期任务
				empApplicationLeaveService.setValueToVO(taskVO,task.getProcessInstanceId());
				if(taskVO.getProcessStatu().intValue()==ConfigConstants.OVERDUE_STATUS.intValue()){
					rus.add(taskVO);
				}else{
					total = total - 1;
				}
				break;
			case  ConfigConstants.CANCELLEAVE_KEY://销假申请
						empApplicationLeaveAbolishService.setValueToVO(taskVO,task.getProcessInstanceId());
				if(taskVO.getProcessStatu().intValue()==ConfigConstants.OVERDUE_STATUS.intValue()){
					rus.add(taskVO);
				}else{
					total = total - 1;
				}
				break;
			case ConfigConstants.OVERTIME_KEY://加班任务
				empApplicationOvertimeService.setValueToVO(taskVO,task.getProcessInstanceId(),task.getTaskDefinitionKey());
				if(taskVO.getProcessStatu().intValue()==ConfigConstants.OVERDUE_STATUS.intValue()){
					rus.add(taskVO);
				}else{
					total = total - 1;
				}
				break;
//			case ConfigConstants.DUTY_KEY://值班任务
//				applicationEmployeeDutyService.setValueToVO(taskVO,task.getProcessInstanceId());
//				rus.add(taskVO);
//				break;
			case ConfigConstants.ABNORMALATTENDANCE_KEY://考勤异常任务
				abnormalAttendanceService.setValueToVO(taskVO,task.getProcessInstanceId());
				if(taskVO.getProcessStatu().intValue()==ConfigConstants.OVERDUE_STATUS.intValue()){
					rus.add(taskVO);
				}else{
					total = total - 1;
				}
				break;
			case ConfigConstants.OUTGOING_KEY://外出任务
				empApplicationOutgoingService.setValueToVO(taskVO,task.getProcessInstanceId());
				if(taskVO.getProcessStatu().intValue()==ConfigConstants.OVERDUE_STATUS.intValue()){
					rus.add(taskVO);
				}else{
					total = total - 1;
				}
				break;
			case ConfigConstants.BUSINESS_KEY://出差任务
				empApplicationBusinessService.setValueToVO(taskVO,task.getProcessInstanceId());
				if(taskVO.getProcessStatu().intValue()==ConfigConstants.OVERDUE_STATUS.intValue()){
					rus.add(taskVO);
				}else{
					total = total - 1;
				}
				break;
			case ConfigConstants.BUSINESSREPORT_KEY://出差任务
				empApplicationBusinessService.setValueToReportVO(taskVO,task.getProcessInstanceId());
				if(taskVO.getProcessStatu().intValue()==ConfigConstants.OVERDUE_STATUS.intValue()){
					rus.add(taskVO);
				}else{
					total = total - 1;
				}
				break;
			case ConfigConstants.REMOVESUBABSENCE_KEY://消下属缺勤任务
				removeSubordinateAbsenceService.setValueToVO(taskVO,task.getProcessInstanceId());
				if(taskVO.getProcessStatu().intValue()==ConfigConstants.OVERDUE_STATUS.intValue()){
					rus.add(taskVO);
				}else{
					total = total - 1;
				}
				break;
//			case ConfigConstants.ENTRY_KEY://新员工入职
//				empApplicationRegisterService.setValueToVO(taskVO,task.getProcessInstanceId(),task.getId());
//				rus.add(taskVO);
//				break;
			default:
				break;
			}
		}
		pm.setTotal(total.intValue());
		pm.setRows(rus);
		return pm;
	}

	@Override
	public void completeTaskByAdmin(HttpServletRequest request,String processId, String comment,
			String commentType) throws Exception {
			String key = activitiServiceImpl.queryKeyByInstanceId(processId);
			Task task = activitiServiceImpl.queryTaskByProcessInstanceId(processId);
			User user = userService.getCurrentUser();
			switch (key) {
			//假期任务完成逻辑
			case ConfigConstants.LEAVE_KEY:
				while(task!=null){
					empApplicationLeaveService.completeTaskByAdmin(request,processId,comment,commentType,user,task);
					task = activitiServiceImpl.queryTaskByProcessInstanceId(processId);
				}
				break;
				//销假任务完成逻辑
			case ConfigConstants.CANCELLEAVE_KEY:
				while(task!=null){
					empApplicationLeaveAbolishService.completeTaskByAdmin(request,processId,comment,commentType,user,task);
					task = activitiServiceImpl.queryTaskByProcessInstanceId(processId);
				}
				break;	
			//异常考勤完成逻辑：
			case ConfigConstants.ABNORMALATTENDANCE_KEY:
				while(task!=null){
					abnormalAttendanceService.completeTaskByAdmin(request,processId,comment,commentType,user,task);
					task = activitiServiceImpl.queryTaskByProcessInstanceId(processId);
				}
				break;
			//外出完成逻辑：
			case ConfigConstants.OUTGOING_KEY:
				while(task!=null){
					empApplicationOutgoingService.completeTaskByAdmin(request,processId,comment,commentType,user,task);
					task = activitiServiceImpl.queryTaskByProcessInstanceId(processId);
				}
			break;
			//出差完成逻辑：
			case ConfigConstants.BUSINESS_KEY:
				while(task!=null){
					empApplicationBusinessService.completeTaskByAdmin(request,processId,comment,commentType,user,task);
					task = activitiServiceImpl.queryTaskByProcessInstanceId(processId);
				}
			break;
			//FIXME:流程在这添加
			default:
				break;
		}
		
	}

	@Override
	public void completeTaskBySystem(HttpServletRequest request,String processId, String comment,
			String commentType) throws Exception {
		String key = activitiServiceImpl.queryKeyByInstanceId(processId);
		switch (key) {
			//出差报告完成逻辑：
			case ConfigConstants.BUSINESSREPORT_KEY:
				empApplicationBusinessService.completeReportTaskBySystem(processId, comment, commentType);
				break;				
			//FIXME:流程在这添加
				
			default:
				break;
		}
	}

	@Override
	public Integer queryTaskCount() {
		//查询用户所有任务
		List<Task> tasks = activitiServiceImpl.getUserHaveTasks(userService.getCurrentUser().getEmployee().getId());
		List<String> processinstanceIdList = new ArrayList<String>();
		for(Task data:tasks){
			processinstanceIdList.add(data.getProcessInstanceId());
		}
		if(processinstanceIdList==null||processinstanceIdList.size()<=0){
			return 0;
		}
		List<Integer> list = activitiServiceImpl.queryTaskCount(processinstanceIdList);
		Integer total = 0;
		for(Integer count:list){
			total = total+count;
		}
		return total;
	}

	@Override
	public PageModel<TaskVO> queryTaskInfoList1(String flag,
			PageModel<TaskVO> page) {
		//新增过期状态，这块代码需要改一下
		Long total = queryTaskCountByEmpId(null);
		PageModel<TaskVO> pm = new PageModel<TaskVO>();
		pm.setPageNo(1);
		Long employeeId = userService.getCurrentUser().getEmployee().getId();
		List<TaskVO> rus = viewMyTaskService.queryAskTask(employeeId);
		pm.setTotal(total.intValue());
		pm.setRows(rus);
		return pm;
	}

	@Override
	public PageModel<TaskVO> queryTaskInfoList2(String flag,
			PageModel<TaskVO> page) {
		 //新增过期状态，这块代码需要改一下
		Long total = queryTaskCountByEmpId(null);
		PageModel<TaskVO> pm = new PageModel<TaskVO>();
		pm.setPageNo(1);
		Long employeeId = userService.getCurrentUser().getEmployee().getId();
		List<TaskVO> rus = viewMyTaskService.queryAskTask2(employeeId);
		pm.setTotal(total.intValue());
		pm.setRows(rus);
		return pm;
	}
}
