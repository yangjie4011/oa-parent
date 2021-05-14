package com.ule.oa.base.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.flowable.task.api.Task;

import com.ule.oa.base.po.User;
import com.ule.oa.base.po.tbl.ViewTaskInfoTbl;
import com.ule.oa.base.vo.TaskVO;
import com.ule.oa.common.utils.PageModel;


public interface UserTaskService {

	Long queryTaskCountByEmpId(Long string);

	void completeTask(HttpServletRequest request,String taskId, String comment, String commentType) throws Exception;
	
	void completeTaskByAdmin(HttpServletRequest request,String processId, String comment, String commentType) throws Exception;

	PageModel<TaskVO> queryTaskInfoList(String flag,PageModel<TaskVO> page);
	
	PageModel<TaskVO> queryOverDueTaskInfoList(String flag,PageModel<TaskVO> page);

	PageModel<TaskVO> queryHiTaskInfoList(String flag,PageModel<TaskVO> page);

	PageModel<Map<String,Object>> queryMyHiTaskInfoList(String flag, PageModel<Map<String,Object>> page);
	
	List<ViewTaskInfoTbl> queryTaskFlow(String instanceId,int statu);
	
	String getForwardSuccessUrl(Task task);

	PageModel<Map<String,Object>> queryTaskList(Map<String,Object> map, PageModel<Map<String, Object>> page);

	void deployTask(String fileName, String fileCnName);
	
	void completeTaskBySystem(HttpServletRequest request,String processId, String comment, String commentType) throws Exception;
	
	Integer queryTaskCount();
	
	PageModel<TaskVO> queryTaskInfoList1(String flag,PageModel<TaskVO> page);
	
	PageModel<TaskVO> queryTaskInfoList2(String flag,PageModel<TaskVO> page);

}
