package com.ule.oa.service.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ule.oa.base.po.Depart;
import com.ule.oa.base.service.DepartService;

@Controller
@RequestMapping("depart")
public class DepartController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private DepartService departService;
	
	@ResponseBody
	@RequestMapping("/list") 
	public Map<String,Object> list(){
		logger.info("DepartController/list.htm：start");
		Map<String,Object> result = new HashMap<String,Object>();
		List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
		try {
			List<Depart> list = departService.getListByCondition(null);
			for(Depart data:list) {
				Map<String,String> map = new HashMap<String,String>();
				map.put("code", data.getCode());
				map.put("name", data.getName());
				mapList.add(map);
			}
			result.put("code", "0000");
			result.put("data", mapList);
			result.put("msg", "查询成功！");
		}catch(Exception e) {
			result.put("code", "9999");
			result.put("msg", "系统异常，请稍后重试！");
		}
		logger.info("DepartController/list.htm：end");
		return result;
	}
	
	@ResponseBody
	@RequestMapping("/departDetail") 
	public Map<String,Object> macthDelayWork(String code,String name){
		logger.info("DepartController/departDetail.htm：start");
		Map<String,Object> result = new HashMap<String,Object>();
		try {
			result = departService.getDepartAndEmpListByCodeOrName(code,name);
		}catch(Exception e) {
			result.put("code", "9999");
			result.put("msg", "系统异常，请稍后重试！");
		}
		logger.info("DepartController/departDetail.htm：end");
		return result;
	}

}
