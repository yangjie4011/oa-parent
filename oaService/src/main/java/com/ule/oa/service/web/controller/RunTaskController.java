package com.ule.oa.service.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ule.oa.base.service.RunTaskService;
import com.ule.oa.base.service.TimingRunTaskService;

/**
  * @ClassName: PositionController
  * @Description: 流程定时任务管理
  * @author mahaitao
  * @date 2017年5月31日 下午20:47:07
 */
@Controller
@RequestMapping("runTask")
public class RunTaskController {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private RunTaskService runTaskService;
	@Autowired
	private TimingRunTaskService timingRunTaskService;
	
	@ResponseBody
	@RequestMapping("/runTaskMsg.htm")
	public String runTaskMsg() {
		runTaskService.runTaskMsg();
		return "OK";
	}
	
	@ResponseBody
	@RequestMapping("/startRunTask.htm")
	public String startRunTask() {
		timingRunTaskService.startRunTask();
		return "OK";
	}
	
}
