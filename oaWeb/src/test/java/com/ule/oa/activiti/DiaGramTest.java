package com.ule.oa.activiti;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.HistoryService;
import org.flowable.engine.ManagementService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.task.Comment;
import org.flowable.task.api.Task;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Maps;
import com.ule.oa.base.po.ActPermission;
import com.ule.oa.base.service.ActPermissionService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.impl.ActivitiServiceImpl;
import com.ule.oa.web.service.impl.BaseServiceTest;

public class DiaGramTest extends BaseServiceTest{
	@Autowired
	private ActivitiServiceImpl activitiServiceImpl;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private HistoryService historyService;
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private ManagementService managementService;
	@Autowired
	private ActPermissionService actPermissionService;
	@Autowired
	private EmployeeService employeeService;
	
	
	
	@Test
	//发布流程
	public void deploy() {
		repositoryService.createDeployment()
		.addClasspathResource("./diagram/scheduling.bpmn")
		.name("排班申请")
		.deploy();
	}
	/* =============================流程开始==============================*/
	@Test
	//开启流程
	public void start() {
		Map<String,Object> map = Maps.newHashMap();
		map.put("isSelf", true);
		activitiServiceImpl.startByKey("abnormalAttendance",map);
	}
	@Test
	//申请人操作：提交申请
	public void proposerTask() throws Exception {
		Task task= activitiServiceImpl.getTaskByEmpId("1716", "cancelLeave");
		//添加批注
		taskService.addComment(task.getId(), task.getProcessInstanceId(), "申请请假");
		taskService.complete(task.getId());
		
	}
	@Test
	//查询该用户最新创建的流程实例
	public void queryRUTask() throws Exception {
		Task task= activitiServiceImpl.getTaskByEmpId("1716", "cancelLeave");
		System.out.println("--------------id--------------"+task.getId());
	}
	
	
	@Test
	//上级领导审批
	public void superiorTask() throws Exception {
		Map<String,Object> variables = Maps.newHashMap();
		Task task= activitiServiceImpl.getTaskByEmpId("1720", "cancelLeave");
		variables.put("leaveDay", 3);
		Map<String, Object> variableMap = taskService.getVariables(task.getId());
		Set<Entry<String,Object>> entrySet = variableMap.entrySet();
		System.out.println("");
		System.out.println("");
		for (Entry<String, Object> entry : entrySet) {
			System.out.println(entry.getKey()+":"+entry.getValue());
		}
		System.out.println("");
		System.out.println("");
		taskService.complete(task.getId(), variables);
	}
	
	@Test
	//部门领导人审批
	public void departHeadTask() throws Exception {
		Task task= activitiServiceImpl.getTaskByEmpId("1656", "cancelLeave");
		System.out.println("=======================id==============="+task.getId());
		taskService.complete(task.getId());
	}
	@Test
	//人事节点设置接任务模式：只要具备某个特性的人群都能看到该任务,审批后该任务自动设置办理人为审批人
	public void personnerTask() throws Exception {
		Task task= activitiServiceImpl.getTaskByEmpId("1656", "cancelLeave");
		System.out.println("=======================id==============="+task.getId());
	}
	@Test
	//接受任务
	public void claimCandidateTask() {
		List<Task> tasks = activitiServiceImpl.getUserHaveTasks(1661L);
		for (Task task : tasks) {
			System.out.println("-----------------id-------------"+task.getId());
			activitiServiceImpl.claimTask(task.getId(), "1661");
		}
	}
	
	@Test
	//接受任务
	public void queryUserTask() {
		List<Task> tasks = activitiServiceImpl.getUserHaveTasks(1716L);
		for (Task task : tasks) {
			System.out.println("-----------------id-------------"+task.getId());
		}
	}
	@Test
	//人事审批：结束流程
	public void finishLeave() {
		List<Task> tasks = activitiServiceImpl.getUserHaveTasks(1661L);
		for (Task task : tasks) {
			System.out.println("-----------------id-------------"+task.getId());
			taskService.addComment(task.getId(), task.getProcessInstanceId(), "111");
			activitiServiceImpl.completeTask(task.getId(), "1661");
		}
	}
	
	
	@Test
	public void callBackProcess() throws Exception {
//		activitiUtil.callBackProcess("232502", "superior");
	}
	@Test
	public void candidateUsers() {
		List<Task> list = taskService.createTaskQuery().taskCandidateUser("1667").list();
		for (Task task : list) {
			taskService.claim(task.getId(), "1667");
		}
	}
	@Test
	public void personnelTask() {
		List<Task> list = taskService.createTaskQuery().taskAssignee("1667").list();
		for (Task task : list) {
			taskService.complete(task.getId());
		}
	}
	//42508 40005
	@Test
	public void complete() {
		Map<String, Object> map = new HashMap<>();
				map.put("leaveDay","3");
		taskService.complete("42508",map);
	}
	
	@Test
	public void deleteProcess() {
		List<Task> list = taskService.createTaskQuery().list();
		for (Task task : list) {
			runtimeService.deleteProcessInstance(task.getProcessInstanceId(), task.getId());
		}
	}
	
	@Test
	public void endProcess() throws Exception{
		List<Task> list = taskService.createTaskQuery().orderByTaskCreateTime().desc().list();
		for (Task task : list) {
			System.out.println(task.getProcessDefinitionId());
			System.out.println(task.getProcessInstanceId());
			System.out.println(task.getTaskDefinitionKey());
			System.out.println();
			System.out.println();
		}
	}
	/* =============================流程结束==============================*/

	@Test
	public void queryHiVariable() {
		historyService.createHistoricVariableInstanceQuery().variableName("");
	}
	@Test
	public void queryComment() {
		 List<Comment> historyCommnets = new ArrayList<>();
		 
		 List<HistoricActivityInstance> hais = historyService.createHistoricActivityInstanceQuery().processInstanceId("25001").activityType("userTask").list();
		 System.out.println("==================="+hais.size()+"=======================");
		 //		 3）查询每个历史任务的批注
		 
        for (HistoricActivityInstance hai : hais) {
        	
            String historytaskId = hai.getTaskId();
            List<Comment> comments = taskService.getTaskComments(historytaskId);
            // 4）如果当前任务有批注信息，添加到集合中
            if(comments!=null && comments.size()>0){
                historyCommnets.addAll(comments);
                System.out.println(comments.get(0).getFullMessage());
            }
        }
	}
	@Test
	public void queryAct() {
		Collection<UserTask> taskByKey = activitiServiceImpl.queryProcessTaskByKey("leave", UserTask.class);
		for (UserTask userTask : taskByKey) {
			System.out.println(userTask.getId());
			System.out.println(userTask.getName());
			System.out.println(userTask.getXmlColumnNumber());
			System.out.println(userTask.getXmlRowNumber());
		}
		}
	
	@Test
	public void ttttttttttttt() {
		Collection<UserTask> keys = activitiServiceImpl.queryProcessTaskByKey("leave", UserTask.class);
		
		for (UserTask user : keys) {
			System.out.println(user.getId());
			System.out.println(user.getName());
			
		}
	}
	
	@Test
	public void queryTask() {
		List<Task> list = taskService.createTaskQuery().processInstanceId("65017").list();
		for (Task task : list) {
			
			System.out.println(task.getId());
		}
	}
	
	@Test
	public void insert() {
		Collection<UserTask> keys = activitiServiceImpl.queryProcessTaskByKey("scheduling", UserTask.class);
		for (UserTask task : keys) {
			ActPermission actPermission = new ActPermission();
			actPermission.setActId(task.getId());
			actPermission.setActName(task.getName());
			actPermission.setProcessKey("scheduling");
			actPermission.setProcessName("排班流程");
			actPermission.setCreateTime(new Date());
			actPermissionService.insertPermission(actPermission);
			}
		}
	@Test
	public void queryLeard() {
		Map<String, Object> map = employeeService.queryDepartHeadIdByEmpId(1716L);
		Set<Entry<String, Object>> entrySet = map.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			System.out.println(entry.getKey()+entry.getValue());
		}
	
	}
	}
