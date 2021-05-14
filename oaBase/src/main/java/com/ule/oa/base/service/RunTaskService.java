package com.ule.oa.base.service;

import java.util.List;
import java.util.Map;

import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.RuProcdef;
import com.ule.oa.base.po.RunTask;
import com.ule.oa.base.po.User;
import com.ule.oa.common.utils.PageModel;

/**
 * @ClassName: PositionService
 * @Description: 流程管理
 * @author mahaitao
 * @date 2017年5月27日 14：37
*/
public interface RunTaskService {
	
	/**
	 * 根据ID获取
	 * @param id
	 * @return
	 */
	RunTask getById(Long id);
	
	/**
	 * 获取列表
	 * @param RunTask
	 * @return
	 */
	List<RunTask> getPageList(RunTask runTask);
	
	/**
	 * 获取总数
	 * @param RunTask
	 * @return
	 */
	Integer getCount(RunTask runTask);
	
	/**
	 * 保存
	 * @param RunTask
	 * @return
	 */
	Long save(RunTask runTask);
	
	/**
	 * 批量保存
	 * @param RunTask
	 */
	void batchSave(RunTask runTask);
	
	/**
	 * 更新
	 * @param id
	 * @return
	 */
	Integer updateById(RunTask runTask);
	
	/**
	 * 定时启动流程
	 * @param code
	 * @param employee
	 * @param entityId
	 * @return
	 */
	public Map<String, String> timingStartFlow(String code, Employee employee, String entityId);
	
	/**
	 * 启动流程
	 * @param reProcdefId 流程ID
	 * @param nodeCode 执行CODE
	 * @param nextNode 待执行CODE
	 * @param user 发起人
	 * @param entityId 实体ID
	 * @return
	 */
	Map<String, String> startFlow(String code, User user, String entityId);
	
	Map<String, String> startFlow(String code, User user, String entityId, String nextNodeCode);
	
	Map<String, String> startFlow(String code, User user, String entityId, String nextNodeCode, Boolean isAgent);
	
	/**
	 * 流程处理
	 * @param runTaskId 运行流程ID
	 * @param nodeCode 当前节点Code
	 * @param nextNode 下一步执行Code
	 * @param assigneeId 受理人ID
	 * @param assignee 受理人名称
	 * @param opinion 审批意见
	 * @return
	 */
	Map<String, String> nextFlow(String reProcdefId,Long processType, String processName, RunTask runTask, String nodeCode, User user, String opinion, String entityId,Map<Long, String> empIdMap, Boolean isAgent);
	
	Map<String, String> nextFlow(Long ruProcdefId, User user,Map<Long, String> empIdMap, String opinion, String entityId,String nextCodeValue, Boolean isAgent);
	Map<String, String> nextFlow(Long ruProcdefId, User user, String opinion, String entityId,String nextCodeValue, Boolean isAgent);
	
	Map<String, String> nextFlow(Long ruProcdefId, User user,Map<Long, String> empIdMap, String opinion, String entityId,String nextCodeValue);
	Map<String, String> nextFlow(Long ruProcdefId, User user, String opinion, String entityId);
	Map<String, String> nextFlow(String reProcdefId,Long processType, String processName, RunTask runTask, String nodeCode, User user, String opinion, String entityId,Map<Long, String> empIdMap);
	
	/**
	 * 分页查询我的协作
	 * @param runTask
	 * @return
	 */
	public PageModel<RunTask> getByPagenation(RunTask runTask);
	
	/**
	 * 分页查询我的申请
	 * @param runTask
	 * @return
	 */
	public PageModel<RunTask> getApplyByPagenation(RunTask runTask);
	
	/**
	 * 拒绝流程
	 * @param ruProcdefId
	 * @param opinion
	 * @return
	 */
	Map<String, String> refuse(Long ruProcdefId, User user, String opinion);
	
	/**
	 * 撤消流程 
	 * @param runId
	 * @param user
	 * @param opinion
	 * @return
	 */
	Map<String, String> cancel(Long ruTaskId, User user, String opinion);
	
	/**
	 * 根据实体获取
	 * @param entityId
	 * @return
	 */
	RunTask getRunTask(RunTask runTask);
	
	public PageModel<RunTask> allExaminePageList(RunTask runTask);
	
	/**
	 * 判断是否部门负责人
	 * @param ruProcdefId 任务ID
	 * @param entityId 员工 ID
	 * @return
	 */
	boolean isDh(Long ruProcdefId,Long entityId);
	
	
	void updateEntyStatus(RuProcdef ruProcdef, User user,Integer approvalStatus);
	
	void runTaskMsg();
}
