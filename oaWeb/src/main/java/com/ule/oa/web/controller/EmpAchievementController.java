package com.ule.oa.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ule.oa.base.po.EmpAchievement;
import com.ule.oa.base.service.EmpAchievementService;

/**
 * 
  * @ClassName: EmpAchievementController
  * @Description: 业绩和奖惩控制层
  * @author minsheng
  * @date 2017年6月22日 下午6:22:53
 */
@Controller
@RequestMapping("empAchievement")
public class EmpAchievementController {
	@Autowired
	private EmpAchievementService empAchievementService;
	
	@ResponseBody
	@RequestMapping("/getListByCondition.htm")
	public List<EmpAchievement> getListByCondition(EmpAchievement empAchievement){
		return empAchievementService.getListByCondition(empAchievement);
	}	
	
	@RequestMapping("/save.htm")
	@ResponseBody
	public Map<String,Object> save(EmpAchievement empAchievement){
		Map<String,Object> map = new HashMap<String,Object>();
		
		try{
			empAchievementService.save(empAchievement);
			map.put("result", "保存成功!");
			map.put("flag", true);
		}catch(Exception e){
			map.put("msg", e.getMessage());
			map.put("result", "保存失败!");
			map.put("flag", false);
		}
		
		return map;
	}
	
	@RequestMapping("/updateById.htm")
	@ResponseBody
	public Map<String,Object> updateById(EmpAchievement empAchievement){
		Map<String,Object> map = new HashMap<String,Object>();
		String option;
		if(0 == empAchievement.getDelFlag().intValue()){
			option = "修改";
		}else{
			option = "删除";
		}
		try{
			empAchievementService.updateById(empAchievement);
			map.put("result", option+"成功！");
		}catch(Exception e){
			map.put("msg", e.getMessage());
			map.put("flag", false);
			map.put("result", option+"失败！");
		}
		
		return map;
	}
}
