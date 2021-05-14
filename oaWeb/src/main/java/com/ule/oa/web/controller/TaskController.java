package com.ule.oa.web.controller;


import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.google.common.collect.Maps;
import com.ule.oa.base.po.tbl.ViewTaskInfoTbl;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.service.UserTaskService;
import com.ule.oa.base.service.impl.ActivitiServiceImpl;
import com.ule.oa.base.vo.TaskVO;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.PageModel;
import com.ule.oa.web.util.ReturnUrlUtil;
import com.ule.oa.web.util.Token;

@Controller
@RequestMapping("/ruProcdef")
public class TaskController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private UserTaskService userTaskService;
	@Autowired
	private UserService userService;
	@Autowired
	private RuntimeService runtimeService;
	
	
	@RequestMapping("/index.htm")
	public ModelAndView index(){
		return new ModelAndView("base/cooperation/index");
	}
	
	/**
	 * 跳转已处理页面
	 * @return
	 */
	@RequestMapping("/ruProcdef_processing.htm")
	public ModelAndView ruProcdef_processing(){
		return new ModelAndView("base/cooperation/ruProcdef_processing");
	}
	
	/**
	 * 跳转我的审批页面
	 * @return
	 */
	@RequestMapping("/my_examine.htm")
	public ModelAndView my_examine(HttpServletRequest request, String current, String urlType){
		//
		request.setAttribute("isLeader", userService.haveApprovalAuthority());
		if(current == null || current.equals("")) {
    		current = "0";
    	}
    	request.setAttribute("current", current);
    	request.setAttribute("returnUrl", ReturnUrlUtil.getReturnUrl(urlType));
		return new ModelAndView("base/cooperation/my_examine");
	}
	
	/**
	  * getCount(我的待办查询总的记录数)
	  * @Title: getCount
	  * @Description: 我的待办查询总的记录数
	  * @param ruProcdef
	  * @return    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/getCount.htm")
	public int getCount(){
//		int count = 0;
//		PageModel<TaskVO> pm = userTaskService.queryTaskInfoList(null,null);
//		count = pm.getTotal();
		return userTaskService.queryTaskCount();
	}
	
	/**
	 * 分页查询待办列表
	 * @param ruProcdef
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getPageList.htm")
	public PageModel<TaskVO> getPageList(String flag){
		return userTaskService.queryTaskInfoList1(flag,null);
		//return userTaskService.queryTaskInfoList(flag,null);
	}
	
	/**
	 * 分页查询失效列表
	 * @param ruProcdef
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getOverDuePageList.htm")
	public PageModel<TaskVO> getOverDuePageList(String flag){
		return userTaskService.queryTaskInfoList2(flag,null);
	}
	
	/**
	 * 分页查询已办列表
	 * @param ruProcdef
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getHiPageList.htm")
	public PageModel<TaskVO> getHiPageList(String flag,int pageNo,int pageSize){
		PageModel<TaskVO> page = new PageModel<>();
		page.setPageNo(pageNo);
		page.setPageSize(pageSize);
		return userTaskService.queryHiTaskInfoList(flag,page);
	}
	
	/**
	 * 分页查询我的申请列表,直接用sql查询业务表,不跟流程挂钩
	 * @param ruProcdef
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getMyPageList.htm")
	public PageModel<Map<String,Object>> getMyTaskList(String flag,int pageNo,int pageSize){
		PageModel<Map<String,Object>> page = new PageModel<>();
		page.setPageNo(pageNo);
		page.setPageSize(pageSize);
		userTaskService.queryMyHiTaskInfoList(flag,page);
		return page;
	}
	
	/**
	 * 分页查询申请列表,直接用sql查询业务表,不跟流程挂钩
	 * @param ruProcdef
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getTaskPageList.htm")
	public PageModel<Map<String,Object>> getTaskList(String nameOrCode,String departId,int pageNo,int pageSize,String startTime,String endTime){
		PageModel<Map<String,Object>> page = new PageModel<>();
		Map<String,Object> map = Maps.newHashMap();
		if(StringUtils.isNotBlank(departId)) {
			map.put("departId", departId);
		}
		if(StringUtils.isNotBlank(nameOrCode)) {
			map.put("nameOrCode", nameOrCode);
		}
		if(StringUtils.isNotBlank(startTime) && !StringUtils.equalsIgnoreCase(startTime, "开始日期")) {
			map.put("startTime", startTime);
		}
		if(StringUtils.isNotBlank(endTime) && !StringUtils.equalsIgnoreCase(endTime,"结束日期")) {
			map.put("endTime", endTime);
		}
		page.setPageNo(pageNo);
		page.setPageSize(pageSize);
		userTaskService.queryTaskList(map,page);
		return page;
	}
	
	
	/**
	 * 点击任务跳转页面
	 * @param request
	 * @param processId
	 * @param redirectUrl
	 * @param urlType
	 * @return
	 */
	@RequestMapping("/edit.htm")
	public ModelAndView edit(HttpServletRequest request, String processId,String redirectUrl, String urlType){
		if(processId == null) {
			return new ModelAndView("forward:/404");
		} else {
			if(urlType == null || urlType.equals("")) {
				urlType = "1";
			}
			ModelMap mmap = new ModelMap();  
			mmap.put("urlType", urlType);
			String foward = "forward:" + redirectUrl.trim();
			return new ModelAndView(foward, mmap); 
		}
	}
	
	
	@RequestMapping("/view.htm")
	//任务显示
	public ModelAndView view(HttpServletRequest request, String redirectUrl,String urlType){
		ModelMap mmap = new ModelMap(); 
		if(urlType == null || urlType.equals("")) {
			urlType = "1";
		}
		mmap.put("urlType", urlType);
		String forwardUrl = "forward:" + redirectUrl;
		return new ModelAndView(forwardUrl, mmap);
	}
	/**
	 * 通过操作
	 * @param request
	 * @param taskId 任务节点id
	 * @param message 审批意见
	 * @return
	 */
	@RequestMapping("/pass.htm")
	@ResponseBody
	@Token(remove = true)
	public Map<String,Object> passTask(HttpServletRequest request,String processId,String message){
		Map<String,Object> result = Maps.newHashMap();
		try {
			userTaskService.completeTask(request,processId,message,ConfigConstants.PASS);
			result.put("success", true);
		}catch (Exception e) {
			result.put("success", false);
			result.put("message", e.getMessage());
		}
		return result;
	}
	/**
	 * 通过操作
	 * @param request
	 * @param taskId 任务节点id
	 * @param message 审批意见
	 * @return
	 */
	@RequestMapping("/passTasks.htm")
	@ResponseBody
	public Map<String,Object> passTasks(HttpServletRequest request,String processIds){
		Map<String,Object> result = Maps.newHashMap();
		try {
			String[] strings = processIds.split(",");
			for (String processId : strings) {
				userTaskService.completeTask(request,processId,"",ConfigConstants.PASS);
			}

			result.put("code", "0000");
			result.put("success", true);
		}catch (Exception e) {
			result.put("code", "9999");
			result.put("success", false);
			result.put("msg", "部分任务已审批,请再次确认无法审批的任务！");
		}
		return result;
	}
	/**
	 * 拒绝操作
	 * @param request
	 * @param taskId 任务节点id
	 * @param message 审批意见
	 * @return
	 */
	@RequestMapping("/refuse.htm")
	@ResponseBody
	@Token(remove = true)
	public Map<String,Object> refuseTask(HttpServletRequest request,String processId,String message){
		Map<String,Object> result = Maps.newHashMap();
		try {
			userTaskService.completeTask(request,processId,message,ConfigConstants.REFUSE);
			result.put("success", true);
		}catch (Exception e) {
			result.put("success", false);
			result.put("message", e.getMessage());
		}
		return result;
	}
	
	/**
	 * 撤销出差报告流程 用于流程结束
	 */
	@RequestMapping("/backBusinessReport.htm")
	@ResponseBody
	public Map<String,Object> backBusinessReport(String processInstanceId){
		Map<String,Object> result = Maps.newHashMap();
		try {
			runtimeService.deleteProcessInstance(processInstanceId, ConfigConstants.BACK);
			result.put("success", true);
		}catch (Exception e) {
			result.put("success", false);
			result.put("message", e.getMessage());
		}
		return result;
	} 
	
	
	/**
	 * 拒绝操作
	 * @param request
	 * @param taskId 任务节点id
	 * @param message 审批意见
	 * @return
	 */
	@RequestMapping("/refuseTasks.htm")
	@ResponseBody
	public Map<String,Object> refuseTasks(HttpServletRequest request,String processIds){
		Map<String,Object> result = Maps.newHashMap();
		try {
			String[] strings = processIds.split(",");
			for (String processId : strings) {
				userTaskService.completeTask(request,processId,"",ConfigConstants.REFUSE);
			}
			result.put("code", "0000");
			result.put("success", true);
		}catch (Exception e) {
			result.put("code", "9999");
			result.put("success", false);
			result.put("msg", "部分任务已审批,请再次确认无法审批的任务！");
		}
		return result;
	}
	/**
	 * 撤回操作
	 * @param request
	 * @param taskId 任务节点id
	 * @param message 审批意见
	 * @return
	 */
	@RequestMapping("/back.htm")
	@ResponseBody
	@Token(remove = true)
	public Map<String,Object> backTask(HttpServletRequest request,String processId,String message){
		Map<String,Object> result = Maps.newHashMap();
		try {
			userTaskService.completeTask(request,processId,message,ConfigConstants.BACK);
			result.put("success", true);
		}catch (Exception e) {
			result.put("success", false);
			result.put("message", e.getMessage());
		}
		return result;
	}
	
	/**
	 * 审批流程页面
	 * @param request
	 * @param taskId 任务节点id
	 * @param message 审批意见
	 * @return
	 */
	@RequestMapping("/viewTaskFlow.htm")
	public String viewTaskFlow(HttpServletRequest request,String instanceId, int statu,String title){
		List<ViewTaskInfoTbl> taskFlows = userTaskService.queryTaskFlow(instanceId, statu);
		request.setAttribute("flows", taskFlows);
		request.setAttribute("statu", statu);
		request.setAttribute("title", title);
		return "base/empApplicationLeave/process";
	}
	
	@RequestMapping("/deployTask.htm")
	@ResponseBody
	public String deploy(String fileName,String fileCnName) {
		String msg = "success";
		logger.info("开始发布"+fileCnName);
		try {
			userTaskService.deployTask(fileName,fileCnName);
			logger.info("发布----"+fileCnName+"----成功");
		} catch (Exception e) {
			logger.error("发布----"+fileCnName+"----失败");
			msg = "fail";
		}
			return msg;
		
	}
	
	//处理待办人离职（待办没有通过，流程自动走入先一个节点**和考勤相关的，最后一个节点需要插入考勤数据并且重算）
	@RequestMapping("/completeTaskBySystem.htm")
	@ResponseBody
	public Map<String,Object> completeTaskBySystem(HttpServletRequest request,String processId,String message){
		Map<String,Object> result = Maps.newHashMap();
		try {
			userTaskService.completeTaskBySystem(request,processId,message,ConfigConstants.PASS);
			result.put("success", true);
		}catch (Exception e) {
			result.put("success", false);
			result.put("message", e.getMessage());
		}
		return result;
	}
}
