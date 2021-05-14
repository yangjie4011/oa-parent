package com.ule.oa.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ule.oa.base.po.CompanyConfig;
import com.ule.oa.base.util.ScheduledExecutorTimingRunTask;

@Controller
@RequestMapping("scheduledExecutor")
public class ScheduledExecutorController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@ResponseBody
	@RequestMapping("/timing.htm")
	public String update(HttpServletRequest request,CompanyConfig companyConfig,String phone,String validateCode){
		ScheduledExecutorTimingRunTask runTask = new ScheduledExecutorTimingRunTask();
		runTask.timerRun();
		return "0000";
	}
}
