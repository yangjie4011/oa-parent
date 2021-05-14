package com.ule.oa.service.web.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ule.oa.base.po.DelayWorkRegister;
import com.ule.oa.base.service.DelayWorkRegisterService;
import com.ule.oa.common.utils.DateUtils;

@Controller
@RequestMapping("delayWork")
public class DelayWorkRegisterController {
	
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private DelayWorkRegisterService delayWorkRegisterService;
	
	@ResponseBody
	@RequestMapping("/macthDelayWork.htm") 
	public Map<String,Object> macthDelayWork(){
		Map<String,Object> result = new HashMap<String,Object>();
		logger.info("macthDelayWork strat");
		List<DelayWorkRegister> list = delayWorkRegisterService.getUnMatchedListByDelayDate(DateUtils.addDay(new Date(), -1));
		for(DelayWorkRegister data:list){
			try{
				 delayWorkRegisterService.macthDelayWork(data);
			}catch(Exception e){
				logger.error("macthDelayWork:"+e.getMessage());
			}
		}
		result.put("message","延时工作登记实际考勤登记成功！");
		result.put("success",true);
		logger.info("macthDelayWork end");
		return result;
	}

}
