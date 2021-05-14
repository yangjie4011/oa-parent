package com.ule.oa.admin.controller;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.google.common.collect.Maps;
import com.ule.oa.base.service.EmpApplicationOvertimeService;
import com.ule.oa.base.service.UserTaskService;
import com.ule.oa.common.exception.OaException;


@Controller
@RequestMapping("task")
public class TaskController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private UserTaskService userTaskService;
	@Autowired
	private EmpApplicationOvertimeService empApplicationOvertimeService;
	
	@ResponseBody
	@RequestMapping("/passTaskByAdmin.htm")
	public Map<String,Object> completeLeave(HttpServletRequest request,String processId,String comment,String commentType){
		Map<String,Object> result = Maps.newHashMap();
		try {
			userTaskService.completeTaskByAdmin(request,processId, comment, commentType);
			result.put("sucess", true);
		} catch (OaException e) {
			result.put("sucess", false);
			result.put("msg", e.getMessage());
		}catch (Exception e) {
			result.put("sucess", false);
			result.put("msg", "审批出现问题,与开发人员联系");
		}
		return result;
	}
	
	/**
	 * 回退流程
	 * @param procisetId
	 * @param taskDefKey
	 * @return
	 */
	@RequestMapping("/backProcess.htm")
	@ResponseBody
	public String backProcess(String procisetId,String taskDefKey) {
		logger.info("backProcess参数：procisetId="+procisetId+";taskDefKey="+taskDefKey);
		String flag = "success";
		try{
			empApplicationOvertimeService.backProcess(procisetId, taskDefKey);
		}catch(Exception e){
			logger.error("回退失败="+e.getMessage());
			flag = e.getMessage();
		}
		
		return flag;
		
	}

}
