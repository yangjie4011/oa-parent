package com.ule.oa.base.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.Process;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.task.Comment;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.ule.oa.base.mapper.ActivitiMapper;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.ConfigConstants;

@Service
public class ActivitiServiceImpl{
	@Autowired
	private TaskService taskService;
	@Autowired
	private HistoryService historyService;
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private ActivitiMapper activitiMapper;
	   /** 日志 */  
    protected Logger logger = LoggerFactory.getLogger(ActivitiServiceImpl.class);  
  
  
    //  List<task> list = processEngine.getTaskService()//与正在执行的任务管理相关的Service  
    //            .createTaskQuery()//创建任务查询对象  
    /**查询条件（where部分）*/  
    //            .taskAssignee(assignee)//指定个人任务查询，指定办理人  
    //          .taskCandidateUser(candidateUser)//User组任务的办理人查询  
    //          .taskCandidateGroup(candidateGroup)//Group组任务的办理人查询  
    //          .processDefinitionId(processDefinitionId)//使用流程定义ID查询  
    //          .processInstanceId(processInstanceId)//使用流程实例ID查询  
    //          .executionId(executionId)//使用执行对象ID查询  
    /**排序*/  
    //            .orderByTaskCreateTime().asc()//使用创建时间的升序排列  
    /**返回结果集*/  
    //          .singleResult()//返回惟一结果集  
    //          .count()//返回结果集的数量  
    //          .listPage(firstResult, maxResults);//分页查询  
    //            .list();//返回列表  
  
  
    /** 
     * 获取当前部署了的流程 
     * @param category 类型 
     * @return 
     */  
    public List<ProcessDefinition> getProcessDefinitions(String category) {  
        logger.info("【获取当前部署了的流程】category={}", category);  
        List<ProcessDefinition> processDefinitions = new ArrayList<ProcessDefinition>();  
        if (StringUtils.isBlank(category)) {  
            processDefinitions = repositoryService.createProcessDefinitionQuery().list();  
        } else {  
            processDefinitions = repositoryService.createProcessDefinitionQuery()  
                .processDefinitionCategory(category).list();  
        }  
        return processDefinitions;  
    }  
  
    
    /** 
     * 获取流程 
     * @return 
     */  
    public List<ProcessDefinition> getProcessDefinition(String id) {  
        return repositoryService.createProcessDefinitionQuery().processDefinitionId(id).list();  
    }  
  
  
    /** 
     * 部署流程图 
     * @param 工作流地址 
     */  
    public void processActivit(String activitiName) {  
        logger.info("【部署流程图】activitiName={}", activitiName);  
        repositoryService.createDeployment()  
            .addClasspathResource("process/" + activitiName).deploy();  
    }  
  
  
    /** 
     * 启动工作流 
     * @param processDefinitionId 部署了的流程id 
     * @param querys 代办组或者代办人 格式 users:******* (条件) 
     * @return 启动的工作流id 
     */  
    public String startById(String processDefinitionId, Map<String, Object> querys) {  
        logger.info("【启动工作流】processDefinitionId={},users={}", processDefinitionId, querys);  
        // 5.获取流程实例  
        ProcessInstance pi = runtimeService.startProcessInstanceById(processDefinitionId, querys);  
        return pi.getId();  
    } 
    
    /** 
     * 启动工作流 
     * @param key
     * @return 流程实例id
     */  
    public String startByKey(String key) {  
        logger.info("【启动工作流】processDefinitionKey={}", key);  
        // 5.获取流程实例  
        ProcessInstance pi = runtimeService.startProcessInstanceByKey(key);  
        return pi.getId();  
    } 
    /** 
     * 启动工作流 
     * @param key
     * @return 流程实例id
     */  
    public String startByKey(String key,Map<String,Object> variables) {  
        logger.info("【启动工作流】processDefinitionKey={}", key);  
        // 5.获取流程实例  
        ProcessInstance pi = runtimeService.startProcessInstanceByKey(key,variables);  
        return pi.getId();  
    }
  
    /** 
     * 获取工作流所处的位置 
     * @param instanceId 工作流id 
     * @return 位置 
     */  
    public String getActivitiNow(String instanceId) {  
        logger.info("【获取工作流所处的位置】instanceId={}", instanceId);  
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(instanceId);  
        return instance.getActivityId();  
    }  
  
  
    /** 
     * 验证工作流是不是已经停止 
     * @param instanceId 工作流id 
     * @return true:已经停止,false:没有停止 
     * . create at 2017年4月17日 上午9:52:18 
     */  
    public Boolean validateActiviti(String instanceId) {  
        logger.info("【验证工作流是不是已经停止】instanceId={}", instanceId);  
        ProcessInstance pi = runtimeService.createProcessInstanceQuery()  
            .processInstanceId(instanceId).singleResult();  
        if (pi != null) {  
            return false;  
        }  
        return true;  
    }  
  
  
    /** 
     * 验证用户是否处于该工作流的当前任务组 group  user  assignee 都查 
     * @param userId 用户id 
     * @return true:处于,false:不处于 
     * . create at 2017年4月17日 上午10:30:47 
     */  
    public Boolean validateUserIn(String userId) {  
        logger.info("【验证用户是否处于工作流的当前任务组】userId={}", userId);  
        List<Task> list = getUserHaveTasks(Long.valueOf(userId));  
        if (list != null && list.size() > 0) {  
            return true;  
        }  
        return false;  
    }  
  
  
    /** 
     * 获取用户当前处于的任务集合 group  user  assignee 都查  
     * @param userId 用户 
     * @return 任务集合 
     * . create at 2017年4月17日 下午3:13:03 
     */  
    public List<Task> getUserHaveTasks(Long userId) {  
        logger.info("【获取用户当前处于的任务集合】userId={}", userId);  
        List<Task> list = taskService.createTaskQuery().taskCandidateGroup(String.valueOf(userId)).orderByTaskCreateTime().desc().list();  
        List<Task> listTwo = taskService.createTaskQuery().taskAssignee(String.valueOf(userId)).orderByTaskCreateTime().desc().list();  
        List<Task> listThree = taskService.createTaskQuery().taskCandidateUser(String.valueOf(userId)).orderByTaskCreateTime().desc().list();  
        //排除重复的  
        for (int i = 0; i < listTwo.size(); i++) {  
            if (!list.contains(listTwo.get(i))) {  
                list.add(listTwo.get(i));  
            }  
        }  
        for (Task task : listThree) {  
            if (!list.contains(task)) {  
                list.add(task);  
            }  
        }  
        return list;  
    }  
    
  
  
    /** 
     * 获取用户当前处于的任务id集合   group  user  assignee 都查  
     * @param userId 用户 
     * @return 任务id集合 
     * . create at 2017年4月17日 下午3:16:09 
     */  
    public List<String> getUserHaveTaskIds(Long userId) {  
        logger.info("【获取用户当前处于的任务id集合】userId={}", userId);  
        List<Task> list = getUserHaveTasks(userId);  
        List<String> ids = new ArrayList<String>();  
        for (Task task : list) {  
            ids.add(task.getId());  
        }  
        return ids;  
    }  
  
  
    /** 
     * 获取用户当前处于签收人的任务集合 只查Assignee 
     * @param userId 用户 
     * @return 任务集合 
     * create at 2017年4月17日 下午3:13:03 
     */  
    public List<Task> getUserHaveTasksAssignee(String userId) {  
        logger.info("【获取用户当前处于的任务集合】userId={}", userId);  
        List<Task> listTwo = taskService.createTaskQuery().taskAssignee(userId).orderByTaskCreateTime().desc().list();  
        return listTwo;  
    }  
  
  
    /** 
     * 获取当前用户处于签收人的工作流id集合 只查assignee 
     * @param userId 用户 
     * @return 
     * . create at 2017年4月17日 下午3:18:37 
     */  
    public List<String> getUserHaveActivitiIdsAssignee(String userId) {  
        logger.info("【获取当前用户处于的工作流id集合】userId={}", userId);  
        List<Task> tasks = getUserHaveTasksAssignee(userId);  
        List<String> ids = new ArrayList<String>();  
        for (Task task : tasks) {  
            ids.add(task.getProcessInstanceId());  
        }  
        return ids;  
    }  
  
  
    /** 
     * 获取用户当前处于签收人的任务id集合 只查Assignee 
     * @param userId 用户 
     * @return 任务id集合 
     * . create at 2017年4月17日 下午3:16:09 
     */  
    public List<String> getUserHaveTasksIdsAssignee(String userId) {  
        logger.info("【获取用户当前处于的任务id集合】userId={}", userId);  
        List<Task> list = getUserHaveTasksAssignee(userId);  
        List<String> ids = new ArrayList<String>();  
        for (Task task : list) {  
            ids.add(task.getId());  
        }  
        return ids;  
    }  
  
  
    /** 
     * 获取用户当前处于代办人的任务集合 只查Group 
     * @param userId 用户 
     * @return 任务集合 
     * . create at 2017年4月17日 下午3:13:03 
     */  
    public List<Task> getUserHaveTasksGroup(String userId) {  
        logger.info("【获取用户当前处于的任务集合】userId={}", userId);  
        List<Task> list = taskService.createTaskQuery().taskCandidateGroup(userId).list();  
        return list;  
    }  
  
  
    /** 
     * 获取用户当前处于代办人的任务id集合 只查Group 
     * @param userId 用户 
     * @return 任务id集合 
     * . create at 2017年4月17日 下午3:16:09 
     */  
    public List<String> getUserHaveTasksIdsGroup(String userId) {  
        logger.info("【获取用户当前处于的任务id集合】userId={}", userId);  
        List<Task> list = getUserHaveTasksGroup(userId);  
        List<String> ids = new ArrayList<String>();  
        for (Task task : list) {  
            ids.add(task.getId());  
        }  
        return ids;  
    }  
  
  
    /** 
     * 获取当前用户处于代办人的工作流id集合 只查group 
     * @param userId 用户 
     * @return 
     * . create at 2017年4月17日 下午3:18:37 
     */  
    public List<String> getUserHaveActivitiIdsGroup(String userId) {  
        logger.info("【获取当前用户处于的工作流id集合】userId={}", userId);  
        List<Task> tasks = getUserHaveTasksGroup(userId);  
        List<String> ids = new ArrayList<String>();  
        for (Task task : tasks) {  
            ids.add(task.getProcessInstanceId());  
        }  
        return ids;  
    }  
  
  
    /** 
     * 获取用户当前处于代办人的任务集合 只查user 
     * @param userId 用户 
     * @return 任务集合 
     * . create at 2017年4月17日 下午3:13:03 
     */  
    public List<Task> getUserHaveTasksUser(String userId) {  
        logger.info("【获取用户当前处于的任务集合】userId={}", userId);  
        List<Task> list = taskService.createTaskQuery().taskCandidateUser(userId).list();  
        return list;  
    }  
  
  
    /** 
     * 获取当前用户处于代办人的工作流id集合 只查user 
     * @param userId 用户 
     * @return 
     * . create at 2017年4月17日 下午3:18:37 
     */  
    public List<String> getUserHaveActivitiIdsUser(String userId) {  
        logger.info("【获取当前用户处于的工作流id集合】userId={}", userId);  
        List<Task> tasks = getUserHaveTasksUser(userId);  
        List<String> ids = new ArrayList<String>();  
        for (Task task : tasks) {  
            ids.add(task.getProcessInstanceId());  
        }  
        return ids;  
    }  
  
  
    /** 
     * 获取用户当前处于代办人的任务id集合 只查Group 
     * @param userId 用户 
     * @return 任务id集合 
     * . create at 2017年4月17日 下午3:16:09 
     */  
    public List<String> getUserHaveTasksIdsUser(String userId) {  
        logger.info("【获取用户当前处于的任务id集合】userId={}", userId);  
        List<Task> list = getUserHaveTasksUser(userId);  
        List<String> ids = new ArrayList<String>();  
        for (Task task : list) {  
            ids.add(task.getId());  
        }  
        return ids;  
    }  
  
  
    /** 
     * 获取当前工作流的当前任务 
     * @param instanceId 工作流id 
     * @return {@link List<Task>} 
     * . create at 2017年4月17日 下午3:26:56 
     */  
    public List<Task> getInstanceTasks(String instanceId) {  
        logger.info("【获取工作流的当前任务的当前任务】instanceId={}", instanceId);  
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(instanceId).list();  
        return tasks;  
    }  
  
  
    /** 
     * 获取当前工作流的当前任务id集合 
     * @param instanceId 工作流id 
     * @return {@link List<String>} 
     * . create at 2017年4月17日 下午3:26:56 
     */  
    public List<String> getInstanceTaskIds(String instanceId) {  
        logger.info("【获取当前工作流的当前任务id集合】instanceId={}", instanceId);  
        List<Task> tasks = getInstanceTasks(instanceId);  
        List<String> taskIds = new ArrayList<String>();  
        for (Task task : tasks) {  
            taskIds.add(task.getId());  
        }  
        return taskIds;  
    }  
  
  
    /** 
     * 获取当前工作流的该用户的当前任务 只查了 group 和 assignee 
     * @param instanceId 工作流id 
     * @param userId 用户id 
     * @return {@link List<Task>} 
     * . create at 2017年4月17日 下午3:26:56 
     */  
    public List<Task> getInstanceUserTasks(String instanceId, String userId) {  
        logger.info("【获取当前工作流的该用户的当前任务】instanceId={}", instanceId);  
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(instanceId)  
            .taskCandidateGroup(userId).list();  
        List<Task> listTwo = taskService.createTaskQuery().processInstanceId(instanceId)  
            .taskAssignee(userId).list();  
        //排除重复的  
        for (int i = 0; i < listTwo.size(); i++) {  
            if (!tasks.contains(listTwo.get(i))) {  
                tasks.add(listTwo.get(i));  
            }  
        }  
        return tasks;  
    }  
  
  
    /** 
     * 获取当前工作流的该用户的当前任务id集合 只查了 group 和 assignee 
     * @param instanceId 工作流id 
     * @param userId 用户id 
     * @return {@link List<String>} 
     */  
    public List<String> getInstanceUserTaskIds(String instanceId, String userId) {  
        logger.info("【获取工作流的当前任务的当前任务id集合】instanceId={}", instanceId);  
        List<Task> tasks = getInstanceUserTasks(instanceId, userId);  
        List<String> taskIds = new ArrayList<String>();  
        for (Task task : tasks) {  
            taskIds.add(task.getId());  
        }  
        return taskIds;  
    }  
  
  
    /** 
     * 获取当前工作流的该用户的当前任务id集合  查了 group 和 assignee user 
     * @param instanceId 工作流id 
     * @param userId 用户id 
     * @param userId 角色或者 继续放入 userId 
     * @return {@link List<String>} 
     */  
    public List<String> getInstanceUserTaskIds(String instanceId, String userId, String role) {  
        logger.info("【获取工作流的当前任务的当前任务id集合】instanceId={}", instanceId);  
        List<Task> tasks = getInstanceUserTasks(instanceId, userId, role);  
        List<String> taskIds = new ArrayList<String>();  
        for (Task task : tasks) {  
            taskIds.add(task.getId());  
        }  
        return taskIds;  
    }  
  
  
    /** 
     * 获取当前工作流的该用户的当前任务 查了 group 和 assignee user 
     * @param instanceId 工作流id 
     * @param userId 用户id 
     * @param userId 角色或者 继续放入 userId 
     * @return {@link List<Task>} 
     */  
    public List<Task> getInstanceUserTasks(String instanceId, String userId, String role) {  
        logger.info("【获取当前工作流的该用户的当前任务】instanceId={}", instanceId);  
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(instanceId)  
            .taskCandidateGroup(userId).list();  
        List<Task> listTwo = taskService.createTaskQuery().processInstanceId(instanceId)  
            .taskAssignee(userId).list();  
        List<Task> listThree = taskService.createTaskQuery().processInstanceId(instanceId)  
            .taskCandidateUser(role).list();  
        //排除重复的  
        for (Task task : listThree) {  
            if (!tasks.contains(task)) {  
                tasks.add(task);  
            }  
        }  
        for (Task task : listTwo) {  
            if (!tasks.contains(task)) {  
                tasks.add(task);  
            }  
        }  
        return tasks;  
    }  
  
  
    /** 
     * 获取工作流的当前任务的用户组  group user assignee 
     * @param instanceId 工作流id 
     * @return Map<String,List<String>> key:taskId,value:用户集合 
     */  
    public Map<String, List<String>> getUserIdsMap(String instanceId) {  
        logger.info("【获取工作流的当前任务的用户组】instanceId={}", instanceId);  
        List<Task> tasks = getInstanceTasks(instanceId);  
        Map<String, List<String>> userIdsAll = new HashMap<String, List<String>>();  
        for (Task task : tasks) {  
            logger.info("【当前任务id】taskId={}", task.getName());  
            List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(task.getId());  
            List<String> userIds = new ArrayList<String>();  
            String userId = "";  
            if (StringUtils.isNotBlank(task.getAssignee())) {  
                userIds.add(task.getAssignee());  
            }  
            for (IdentityLink identityLink : identityLinks) {  
                //获取用户封装入list  
                userId = identityLink.getGroupId();  
                if (StringUtils.isBlank(userId)) {  
                    //group中无用户  
                    userId = identityLink.getUserId();  
                    if (StringUtils.isBlank(userId)) {  
                        //group中无用户和userId中无用户  
                        continue;  
                    }  
                }  
                if (userIds.contains(userId)) {  
                    continue;  
                }  
                userIds.add(userId);  
            }  
            //加入返回 key:taskId,value:用户集合  
            userIdsAll.put(task.getId(), userIds);  
        }  
        return userIdsAll;  
    }  
  
  
    /** 
     * 获取用户所处的任务id集合 根据groupId查找 
     * @param instanceId 
     * @param userId 
     * @return 
     */  
    public List<String> getInstanceGroupTaskIds(String instanceId, String userId) {  
        List<String> taskIds = new ArrayList<String>();  
        List<Task> tasks = getInstanceTasks(instanceId);  
        for (Task task : tasks) {  
            if (StringUtils.isNotBlank(task.getAssignee())) {  
                continue;  
            }  
            List<String> userIds = getGroupUserIds(task);  
            if (userIds.contains(userId)) {  
                taskIds.add(task.getId());  
            }  
        }  
        return taskIds;  
    }  
  
  
    /** 
     * 获取用户所处的任务id集合 根据userId查找 
     * @param instanceId 
     * @param userId 
     * @return 
     */  
    public List<String> getInstanceUsersTaskIds(String instanceId, String userId) {  
        List<String> taskIds = new ArrayList<String>();  
        List<Task> tasks = getInstanceTasks(instanceId);  
        for (Task task : tasks) {  
            if (StringUtils.isNotBlank(task.getAssignee())) {  
                continue;  
            }  
            List<String> userIds = getUserUserIds(task);  
            if (userIds.contains(userId)) {  
                taskIds.add(task.getId());  
            }  
        }  
        return taskIds;  
    }  
  
  
    /** 
     * 获取用户所处的任务id集合 根据Assignee查找 
     * @param instanceId 
     * @param userId 
     * @return 
     */  
    public List<String> getInstanceAssigneeTaskIds(String instanceId, String userId) {  
        List<String> taskIds = new ArrayList<String>();  
        List<Task> tasks = getInstanceTasks(instanceId);  
        for (Task task : tasks) {  
            String userIds = getAssigneeUserIds(task);  
            if (userId.equals(userIds)) {  
                taskIds.add(task.getId());  
            }  
        }  
        return taskIds;  
    }  
  
  
    /** 
     * 获取工作流的当前任务的所有用户组 groupId  userId  assignee 
     * @param instanceId 工作流id 
     * @return List<List<String>> 表示当前任务可能不只有一个 
     */  
    public List<List<String>> getUserIds(String instanceId) {  
        logger.info("【获取工作流的当前任务的用户组】instanceId={}", instanceId);  
        List<Task> tasks = getInstanceTasks(instanceId);  
        List<List<String>> userIdsAll = new ArrayList<List<String>>();  
        for (Task task : tasks) {  
            logger.info("【当前任务id】taskId={}", task.getName());  
            List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(task.getId());  
            List<String> userIds = new ArrayList<String>();  
            String userId = "";  
            String groupId = "";  
            if (StringUtils.isNotBlank(task.getAssignee())) {  
                userIds.add(task.getAssignee());  
                logger.info("【有签收人不用再获取用户】");  
                continue;  
            }  
            for (IdentityLink identityLink : identityLinks) {  
                //获取用户封装入list  
                groupId = identityLink.getGroupId();  
                if (StringUtils.isNotBlank(groupId)) {  
                    if (groupId.indexOf(",") > 0) {  
                        String[] idStr = groupId.split(",");  
                        for (String idOne : idStr) {  
                            if (!userIds.contains(idOne)) {  
                                userIds.add(idOne);  
                            }  
                        }  
                    }  
                }  
                if (groupId.length() == 1) {  
                    if (!userIds.contains(groupId)) {  
                        userIds.add(groupId);  
                    }  
                }  
                userId = identityLink.getUserId();  
                if (StringUtils.isNotBlank(userId)) {  
                    if (userId.indexOf(",") > 0) {  
                        String[] idStr = userId.split(",");  
                        for (String idOne : idStr) {  
                            if (!userIds.contains(idOne)) {  
                                userIds.add(idOne);  
                            }  
                        }  
                    }  
                }  
                if (userId.length() == 1) {  
                    if (!userIds.contains(userId)) {  
                        userIds.add(userId);  
                    }  
                }  
            }  
            //加入返回List<List<String>>  
            userIdsAll.add(userIds);  
        }  
        return userIdsAll;  
    }  
  
  
    /** 
     * 获取工作流的当前任务的所有用户组 groupId  userId  assignee 
     * @param instanceId 工作流id 
     * @return  
     */  
    public List<String> getUserIdsOneList(String instanceId) {  
        List<List<String>> lists = getUserIds(instanceId);  
        List<String> ids = new ArrayList<String>();  
        for (List<String> list : lists) {  
            for (String id : list) {  
                if (!ids.contains(id)) {  
                    ids.add(id);  
                }  
            }  
        }  
        return ids;  
    }  
    
  
    /** 
     * 完成任务 
     * @param taskId 任务id 
     */  
    public void completeTask(String taskId) {  
        logger.info("【完成任务】taskId={}", taskId);  
        taskService.complete(taskId);  
    }  
  
  
    /** 
     * 完成任务 
     * @param taskId 任务id 
     * @param userId 用户id 
     */  
    public void completeTask(String taskId, String userId) {  
        logger.info("【完成任务】taskId={},userId={}", taskId, userId);  
        taskService.complete(taskId);
    }  
  
  
    /** 
     * 认领任务 
     * @param taskId 任务id 
     * @param userId 用户id 
     */  
    public void claimTask(String taskId, String userId) {  
        taskService.claim(taskId, userId);  
    }  
  
  
    /** 
     * 查询是否结束 
     * @param instanceId 流程id 
     * @return true:已经结束 false:没有结束 
     */  
    public Boolean validateEnd(String instanceId) {  
        ProcessInstance rpi = runtimeService
            .createProcessInstanceQuery()//创建流程实例查询对象  
            .processInstanceId(instanceId).singleResult();  
        //说明流程实例结束了  
        if (rpi == null) {  
            return true;  
        }  
        return false;  
    }  
  
  
    /** 
     * 获取用户id集合 
     * @param nameIds 
     * @return 
     */  
    public List<String> analysisUserId(String nameIds) {  
        List<String> ids = new ArrayList<String>();  
        while (nameIds.lastIndexOf("),") > 0) {  
            int startIndex = nameIds.indexOf("(");  
            int endIndex = nameIds.indexOf("),");  
            String id = nameIds.substring(startIndex + 1, endIndex);  
            ids.add(id);  
            nameIds = nameIds.substring(endIndex + 2);  
        }  
        return ids;  
    }  
  
  
    /** 
     * 修改变量 
     * @param activitiId 工作流id 
     * @param variables 条件 
     */  
    public void setVar(String activitiId, Map<String, Object> variables) {  
        logger.info("【修改变量】activitiId={},variables={}", activitiId, variables);  
        List<Task> tasks = getInstanceTasks(activitiId);  
        for (Task task : tasks) {  
            taskService.setVariables(task.getId(), variables);  
        }  
    }  
  
  
    /** 
     * 获取工作流的当前任务的groupId用户组 
     * @param instanceId 工作流id 
     * @return List<List<String>> 表示当前任务可能不只有一个 
     */  
    public List<List<String>> getGroupUserIds(String instanceId) {  
        logger.info("【获取工作流的当前任务的用户组】instanceId={}", instanceId);  
        List<Task> tasks = getInstanceTasks(instanceId);  
        List<List<String>> userIdsAll = new ArrayList<List<String>>();  
        for (Task task : tasks) {  
            logger.info("【当前任务id】taskId={}", task.getName());  
            List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(task.getId());  
            List<String> userIds = new ArrayList<String>();  
            String userId = "";  
            if (StringUtils.isNotBlank(task.getAssignee())) {  
                logger.info("【有签收人 不用获取用户】");  
                continue;  
            }  
            for (IdentityLink identityLink : identityLinks) {  
                //获取用户封装入list  
                userId = identityLink.getGroupId();  
                if (StringUtils.isBlank(userId)) {  
                    //group中无用户  
                    continue;  
                }  
                if (userId.indexOf(",") > 0) {  
                    String[] idStr = userId.split(",");  
                    for (String idOne : idStr) {  
                        if (!userIds.contains(idOne)) {  
                            userIds.add(idOne);  
                        }  
                    }  
                }  
                if (userId.length() == 1) {  
                    if (!userIds.contains(userId)) {  
                        userIds.add(userId);  
                    }  
                }  
            }  
            //加入返回List<List<String>>  
            userIdsAll.add(userIds);  
        }  
        return userIdsAll;  
    }  
  
  
    /** 
     * 获取工作流的当前任务的groupId用户组 
     * @param instanceId 工作流id 
     * @return  
     */  
    public List<String> getGroupUserIdsOneList(String instanceId) {  
        List<List<String>> lists = getGroupUserIds(instanceId);  
        List<String> ids = new ArrayList<String>();  
        for (List<String> list : lists) {  
            for (String id : list) {  
                if (!ids.contains(id)) {  
                    ids.add(id);  
                }  
            }  
        }  
        return ids;  
    }  
  
  
    /** 
     * 获取工作流的当前任务的userId用户组 
     * @param instanceId 工作流id 
     * @return List<List<String>> 表示当前任务可能不只有一个 
     */  
    public List<List<String>> getUserUserIds(String instanceId) {  
        logger.info("【获取工作流的当前任务的用户组】instanceId={}", instanceId);  
        List<Task> tasks = getInstanceTasks(instanceId);  
        List<List<String>> userIdsAll = new ArrayList<List<String>>();  
        for (Task task : tasks) {  
            logger.info("【当前任务id】taskId={}", task.getName());  
            List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(task.getId());  
            List<String> userIds = new ArrayList<String>();  
            String userId = "";  
            if (StringUtils.isNotBlank(task.getAssignee())) {  
                logger.info("【有签收人 不用获取用户】");  
                continue;  
            }  
            for (IdentityLink identityLink : identityLinks) {  
                userId = identityLink.getUserId();  
                if (StringUtils.isBlank(userId)) {  
                    continue;  
                }  
                if (userId.indexOf(",") > 0) {  
                    String[] idStr = userId.split(",");  
                    for (String idOne : idStr) {  
                        if (!userIds.contains(idOne)) {  
                            userIds.add(idOne);  
                        }  
                    }  
                }  
                if (userId.length() == 1) {  
                    if (!userIds.contains(userId)) {  
                        userIds.add(userId);  
                    }  
                }  
            }  
            //加入返回List<List<String>>  
            userIdsAll.add(userIds);  
        }  
        return userIdsAll;  
    }  
    /**
     * 根据流程key和员工id查询最近的一个可执行任务
     * @param empId
     * @param processKey
     * @return
     * @throws Exception
     */
    public Task getTaskByEmpId(String empId,String processKey)throws OaException {
    	List<Task> list = taskService.createTaskQuery().taskAssignee(empId).processDefinitionKey(processKey).orderByTaskCreateTime().desc().list();
    	if(list==null || list.size()<=0) {
    		logger.error("根据processKey={},empId={}查询不到可执行任务", processKey, empId);
    		throw new OaException("根据processKey="+processKey+",empId="+empId+"查询不到可执行任务");
    	}
    	return list.get(0);
    }
    
    /**
     * 根据流程taskId查询最近的一个可执行任务
     * @param taskId
     * @return
     * @throws Exception
     */
    public Task getTaskById(String taskId)throws OaException {
    	Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    	if(task==null) {
    		logger.error("根据taskId={}查询不到可执行任务", taskId);
    		throw new OaException("根据taskId="+taskId+"查询不到可执行任务");
    	}
    	return task;
    }
  
    /** 
     * 获取工作流的当前任务的userId用户组 
     * @param instanceId 工作流id 
     * @return  
     */  
    public List<String> getUserUserIdsOneList(String instanceId) {  
        List<List<String>> lists = getUserUserIds(instanceId);  
        List<String> ids = new ArrayList<String>();  
        for (List<String> list : lists) {  
            for (String id : list) {  
                if (!ids.contains(id)) {  
                    ids.add(id);  
                }  
            }  
        }  
        return ids;  
    }  
  
  
    /** 
     * 获取工作流的当前任务的groupId用户组 
     * @param instanceId 工作流id 
     * @return List<List<String>> 表示当前任务可能不只有一个 
     */  
    public List<String> getGroupUserIds(Task task) {  
        logger.info("【当前任务id】taskId={}", task.getName());  
        List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(task.getId());  
        List<String> userIds = new ArrayList<String>();  
        String userId = "";  
        if (StringUtils.isNotBlank(task.getAssignee())) {  
            logger.info("【有签收人 不用获取用户】");  
            return userIds;  
        }  
        for (IdentityLink identityLink : identityLinks) {  
            //获取用户封装入list  
            userId = identityLink.getGroupId();  
            if (StringUtils.isBlank(userId)) {  
                //group中无用户  
                continue;  
            }  
            if (userId.indexOf(",") > 0) {  
                String[] idStr = userId.split(",");  
                for (String idOne : idStr) {  
                    if (!userIds.contains(idOne)) {  
                        userIds.add(idOne);  
                    }  
                }  
            }  
            if (userId.length() == 1) {  
                if (!userIds.contains(userId)) {  
                    userIds.add(userId);  
                }  
            }  
        }  
        //加入返回List<List<String>>  
        return userIds;  
    }  
  
  
    /** 
     * 获取工作流的当前任务的userId用户组 
     * @param instanceId 工作流id 
     * @return List<List<String>> 表示当前任务可能不只有一个 
     */  
    public List<String> getUserUserIds(Task task) {  
        logger.info("【当前任务id】taskId={}", task.getName());  
        List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(task.getId());  
        List<String> userIds = new ArrayList<String>();  
        String userId = "";  
        if (StringUtils.isNotBlank(task.getAssignee())) {  
            logger.info("【有签收人 不用获取用户】");  
            return userIds;  
        }  
        for (IdentityLink identityLink : identityLinks) {  
            userId = identityLink.getUserId();  
            if (StringUtils.isBlank(userId)) {  
                continue;  
            }  
            if (userId.indexOf(",") > 0) {  
                String[] idStr = userId.split(",");  
                for (String idOne : idStr) {  
                    if (!userIds.contains(idOne)) {  
                        userIds.add(idOne);  
                    }  
                }  
            }  
            if (userId.length() == 1) {  
                if (!userIds.contains(userId)) {  
                    userIds.add(userId);  
                }  
            }  
        }  
        return userIds;  
    }  
  
  
    /** 
     * 获取工作流的当前任务的assignee用户组 
     * @param instanceId 工作流id 
     * @return List<List<String>> 表示当前任务可能不只有一个 
     */  
    public List<String> getAssigneeUserIds(String instanceId) {  
        logger.info("【获取工作流的当前任务的用户组】instanceId={}", instanceId);  
        List<Task> tasks = getInstanceTasks(instanceId);  
        List<String> userIds = new ArrayList<String>();  
        for (Task task : tasks) {  
            logger.info("【当前任务id】taskId={}", task.getName());  
            String userId = "";  
            if (StringUtils.isNotBlank(task.getAssignee())) {  
                userId = task.getAssignee();  
                if (StringUtils.isBlank(userId)) {  
                    continue;  
                }  
                if (userIds.contains(userId)) {  
                    continue;  
                }  
                userIds.add(userId);  
            }  
        }  
        return userIds;  
    }  
  
  
    /** 
     * 获取工作流的当前任务的assignee用户组 
     * @param instanceId 工作流id 
     * @return List<List<String>> 表示当前任务可能不只有一个 
     */  
    public String getAssigneeUserIds(Task task) {  
        logger.info("【当前任务id】taskId={}", task.getName());  
        String userId = "";  
        if (StringUtils.isNotBlank(task.getAssignee())) {  
            userId = task.getAssignee();  
            if (StringUtils.isBlank(userId)) {  
                return null;  
            }  
        }  
        return userId;  
    }  
  
  
    /** 
     * 设置组用户 放入groupId中 
     * @param ids 用户 
     * @param task 
     */  
    public void addGroupIds(List<String> ids, Task task) {  
        String idStr = "";  
        for (String id : ids) {  
            idStr += id + ",";  
        }  
        taskService.addCandidateGroup(task.getId(), idStr);  
    }  
  
  
    /** 
     * 获取已办任务列表 assignee 
     */  
    public List<Map<String,Object>> getHistoryAssigneeActivitiIds(String userId,int pageNo,int pageSize) {
    	Map<String,Object> map = Maps.newHashMap();
    	map.put("assignee",userId);
    	map.put("pageNo",pageNo*pageSize);
    	map.put("pageSize",pageSize);
    	 return activitiMapper.queryTaskByAssignee(map);
    }  
  
    /** 
     * 获取历史任务 查询用户处于代办组 group 
     */  
    public List<HistoricTaskInstance> getHistoryGroupActivitiIds(String userId) {  
        List<HistoricTaskInstance> list = historyService // 历史任务Service    
            .createHistoricTaskInstanceQuery() // 创建历史任务实例查询    
            .taskCandidateGroup(userId).finished() // 查询已经完成的任务      
            .list();  
        return list;  
    }  
  
  
    /** 
     * 获取历史任务 查询用户处于代办组 group 
     */  
    public List<String> getHistoryGroupActivitiIdsString(String userId) {  
        List<HistoricTaskInstance> list = getHistoryGroupActivitiIds(userId);  
        List<String> ids = new ArrayList<String>();  
        for (HistoricTaskInstance historicTaskInstance : list) {  
            if (!ids.contains(historicTaskInstance.getProcessInstanceId())) {  
                ids.add(historicTaskInstance.getProcessInstanceId());  
            }  
        }  
        return ids;  
    }  
  
  
    /** 
     * 获取历史任务 查询用户处于代办组 group 
     */  
    public List<HistoricTaskInstance> getHistoryUserActivitiIds(String userId) {  
        List<HistoricTaskInstance> list = historyService // 历史任务Service    
            .createHistoricTaskInstanceQuery() // 创建历史任务实例查询    
            .taskCandidateUser(userId).finished() // 查询已经完成的任务      
            .list();  
        return list;  
    }  
  
  
    /** 
     * 获取历史任务 查询用户处于代办组 group 
     */  
    public List<String> getHistoryUserActivitiIdsString(String userId) {  
        List<HistoricTaskInstance> list = getHistoryUserActivitiIds(userId);  
        List<String> ids = new ArrayList<String>();  
        for (HistoricTaskInstance historicTaskInstance : list) {  
            if (!ids.contains(historicTaskInstance.getProcessInstanceId())) {  
                ids.add(historicTaskInstance.getProcessInstanceId());  
            }  
        }  
        return ids;  
    }
    
    /** 
     * 根据taskId查询variables
     * @param taskId
     * @return
     */
    public Map<String,Object> getVariablesByTaskId(String taskId) {  
        return taskService.getVariables(taskId);
    }
    /**
     * 处理任务
     * @param taskId
     * @param processInstanceId
     * @param message
     */
    public void completeTask(String taskId,String message,Map<String, Object> variables,String type) {
    	Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    	if(StringUtils.equalsIgnoreCase(type, ConfigConstants.PASS)||StringUtils.equalsIgnoreCase(type, ConfigConstants.OVERDUEPASS)) {
    		//通过
    		if(message==null) {
    			message="";
    		}
    		taskService.addComment(taskId, task.getProcessInstanceId(), message);
    		taskService.complete(taskId, variables);
    		//保存act_task_info
    		
    	}else if(StringUtils.equalsIgnoreCase(type, ConfigConstants.REFUSE)||StringUtils.equalsIgnoreCase(type, ConfigConstants.OVERDUEREFUSE)) {
    		//拒绝,deleteReason填rufuse 
    		taskService.addComment(taskId, task.getProcessInstanceId(), message);
    		runtimeService.deleteProcessInstance(task.getProcessInstanceId(), ConfigConstants.REFUSE);
    	}else if(StringUtils.equalsIgnoreCase(type, ConfigConstants.BACK)) {
    		//撤回,理由放在variable中,deleteReason填back
    		taskService.setVariable(taskId, ConfigConstants.BACK, message);
    		taskService.setAssignee(taskId, null);
    		runtimeService.deleteProcessInstance(task.getProcessInstanceId(), ConfigConstants.BACK);
    	}
    }
    
    /**
     * 根据流程实例查询流程定义key
     */
    public String queryProcessKeyByDefinitionId(String definitionId) {
    	ProcessDefinition process = repositoryService.createProcessDefinitionQuery().processDefinitionId(definitionId).singleResult();
    	return process.getKey();
    }
    
    /**
     * 根据流程实例查询流程定义对象
     */
    public ProcessDefinition queryProcessByInstanceId(String instanceId) {
    	return repositoryService.createProcessDefinitionQuery().processDefinitionId(queryDefinitionIdByInstanceId(instanceId)).singleResult();
    }
    
    /**
     * 根据实例id查询历史定义
     */
    public HistoricProcessInstance queryHiProcessByInstanceId(String instanceId) {
    	return historyService.createHistoricProcessInstanceQuery().processInstanceId(instanceId).singleResult();
    }
    
    /**
     * 
     */
    public String queryKeyByInstanceId(String instanceId) {
    	return activitiMapper.queryKeyByInstanceId(instanceId);
    }
    
    /**
     * 根据流程实例查询流程定义
     */
    public String queryDefinitionIdByInstanceId(String instanceId) {
    	//
    	return taskService.createTaskQuery().processInstanceId(instanceId).singleResult().getProcessDefinitionId();
    }
    
    
    /**
     * 根据流程定义key查询最新的流程的所有属性为type的节点
     * @param key
     * @param type
     * @return
     */
    public <FlowElementType extends FlowElement> Collection<FlowElementType> queryProcessTaskByKey(String key,Class<FlowElementType> type){
    	ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(key).latestVersion().list().get(0);
    	Process process = repositoryService.getBpmnModel(processDefinition.getId()).getProcesses().get(0);
    	List<FlowElementType> elementTypes = process.findFlowElementsOfType(type);
    	return elementTypes;
    }
    
    /**
     * TODO:拒绝操作,应该跳转到结束的节点,而不是删除
     * @param taskId
     * @param comment
     */
	public void refuse(String taskId, String comment) {
		runtimeService.deleteProcessInstance(taskService.createTaskQuery().taskId(taskId).list().get(0).getProcessInstanceId(), comment);
	}
	
	/**
	 * 根据taskid查询审批意见
	 * @param taskId
	 * @return
	 */
	public String queryTaskCommentByTaskId(String taskId) {
		
		String comment = "";
		List<Comment> taskComments = taskService.getTaskComments(taskId);
		if(taskComments!=null && taskComments.size()>0){
            comment = taskComments.get(0).getFullMessage();
        }
		return comment;
	}
	/**
	 * 根据流程实例查询办理中任务
	 */
	public Task queryTaskByProcessInstanceId(String processInstanceId) {
		return taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
	}
	
	/**
	 * 根据流程实例和待办人查询办理中任务
	 */
	public Task queryTaskByInstanceIdAndAssignee(String instanceId,String assignee) {
		return taskService.createTaskQuery().processInstanceId(instanceId).taskAssignee(assignee).singleResult();
	}
	
	/**
	 * 根据流程实例和待办人查询办理中任务
	 */
	public Task queryTaskByTaskIdAndCandidate(String taskId,String candidate) {
		return taskService.createTaskQuery().taskId(taskId).taskCandidateUser(candidate).singleResult();
	}
	
	/**
	 * 根据流程实例查询办理中任务
	 */
	public List<Task> queryTaskListByProcessInstanceId(String processInstanceId) {
		return taskService.createTaskQuery().processInstanceId(processInstanceId).list();
	}
	
	/**
	 * 根据流程实例设置任务参数
	 */
	public void setVariablesByProcessInstanceId(String processInstanceId,Map<String,Object> variables) {
		taskService.setVariables(queryTaskByProcessInstanceId(processInstanceId).getId(), variables);
	}
	//查询申请人,办理中任务
	public List<HistoricTaskInstance> queryTaskByProposer(Long proposerId) {
		List<HistoricTaskInstance> hiTask = new ArrayList<>();
		List<Task> list = taskService.createTaskQuery().processVariableValueEquals(proposerId).orderByTaskCreateTime().desc().list();
		for (Task task : list) {
			hiTask.add(historyService.createHistoricTaskInstanceQuery().processInstanceId(task.getProcessInstanceId()).taskAssignee(String.valueOf(proposerId)).singleResult());
		}
		return hiTask;
	}
	//查询申请人,已完成任务
	public List<HistoricTaskInstance> queryPassTaskByProposer(Long proposerId) {
		return historyService.createHistoricTaskInstanceQuery().processVariableValueEquals(proposerId).processFinished().taskAssignee(String.valueOf(proposerId)).orderByTaskCreateTime().desc().list();
	}
	//查询申请人,已拒绝任务
	public List<HistoricTaskInstance> queryRefuseTaskByProposer(Long proposerId) {
		return historyService.createHistoricTaskInstanceQuery().taskAssignee(String.valueOf(proposerId)).processVariableValueEquals(proposerId).taskDeleteReason(ConfigConstants.REFUSE).orderByTaskCreateTime().desc().list();
	}
	//查询申请人,已撤回任务
	public List<HistoricTaskInstance> queryBackTaskByProposer(Long proposerId) {
		return historyService.createHistoricTaskInstanceQuery().taskAssignee(String.valueOf(proposerId)).processVariableValueEquals(proposerId).taskDeleteReason(ConfigConstants.BACK).orderByTaskCreateTime().desc().list();
	}

	//根据流程实例id查询历史任务表
	public List<HistoricTaskInstance> queryTasksByProcessInstanceId(String instanceId) {
		return historyService.createHistoricTaskInstanceQuery().processInstanceId(instanceId).orderByHistoricTaskInstanceEndTime().desc().list();
	}


	public Long queryTaskCountByEmpId(Long empId) {
		return taskService.createTaskQuery().taskAssignee(String.valueOf(empId)).count()+taskService.createTaskQuery().taskCandidateUser(String.valueOf(empId)).count();
	}
	public Task queryTaskById(String taskId) {
		return taskService.createTaskQuery().taskId(taskId).singleResult();
	}
	
	public void deploy(String fileName,String fileCnName) {
		repositoryService.createDeployment()
		.addClasspathResource("./diagram/"+fileName+".bpmn")
		.name(fileCnName)
		.deploy();
	}
	public void setAssignessToTask(String taskId,String userId) {
		taskService.setAssignee(taskId, userId);
	}
	
	//流程回退到指定节点(只支持单流程)
	public void back(String procisetId,String executionId,String taskDefKey){
		runtimeService.createChangeActivityStateBuilder()
        .processInstanceId(procisetId)
        .moveExecutionToActivityId(executionId, taskDefKey)
        .changeState();
	}
	
	public List<Integer> queryTaskCount(List<String> processinstanceIdList){
		return activitiMapper.queryTaskCount(processinstanceIdList);
	}
}

