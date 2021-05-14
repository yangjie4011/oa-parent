package com.ule.oa.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ule.oa.base.service.ServiceMonitorService;

/**
  * @ClassName: ServiceMonitorController
  * @Description: 服务监控
  * @author minsheng
  * @date 2017年1月11日 上午11:23:02
 */
@Controller
@RequestMapping("/monitor")
public class ServiceMonitorController{
	@Autowired
	private ServiceMonitorService serviceMonitorService;
	
	@ResponseBody
	@RequestMapping(value="/serviceCheck.htm")
	public String getServiceMonitor(){
		return serviceMonitorService.getServiceMonitor();
	}
}
