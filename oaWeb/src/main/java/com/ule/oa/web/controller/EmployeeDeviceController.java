package com.ule.oa.web.controller;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ule.oa.base.po.EmployeeDevice;
import com.ule.oa.base.service.EmployeeDeviceService;


@Controller
@RequestMapping("employeeDevice")
public class EmployeeDeviceController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private EmployeeDeviceService employeeDeviceService;
	
	//保存员工设备信息
	@ResponseBody
	@RequestMapping("/save.htm")
	public void save(HttpServletResponse response,EmployeeDevice device){
		try{
			employeeDeviceService.save(response,device);
		}catch(Exception e){
			logger.error("employeeDevice/save"+e.getMessage());
		}
	}
	
	@ResponseBody
	@RequestMapping("/getByEmployeeId.htm")
	public EmployeeDevice getByEmployeeId(Long  employeeId){
		return employeeDeviceService.getByEmployeeId(employeeId);
	}

}
