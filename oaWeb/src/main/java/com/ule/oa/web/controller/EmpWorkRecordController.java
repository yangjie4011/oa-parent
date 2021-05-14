package com.ule.oa.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ule.oa.base.po.EmpWorkRecord;
import com.ule.oa.base.service.EmpWorkRecordService;

@Controller
@RequestMapping("empWorkRecord")
public class EmpWorkRecordController {
	@Autowired
	private EmpWorkRecordService empWorkRecordService;
	
	@RequestMapping("/save.htm")
	@ResponseBody
	public Map<String,Object> save(EmpWorkRecord empWorkRecord){
		Map<String,Object> map = new HashMap<String,Object>();
		
		try{
			empWorkRecordService.save(empWorkRecord);
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
	public Map<String,Object> updateById(EmpWorkRecord empWorkRecord){
		Map<String,Object> map = new HashMap<String,Object>();

		String option;
		if(0 == empWorkRecord.getDelFlag()){
			option = "修改";
		}else{
			option = "删除";
		}
		try{
			empWorkRecordService.updateById(empWorkRecord);
			map.put("flag", true);
			map.put("result", option+"成功！");
		}catch(Exception e){
			map.put("msg", e.getMessage());
			map.put("flag", false);
			map.put("result", option+"失败！");
		}
		
		return map;
	}
	
	@RequestMapping("/getListByCondition.htm")
	@ResponseBody
	public List<EmpWorkRecord> getListByCondition(EmpWorkRecord empWorkRecord){
		return empWorkRecordService.getListByCondition(empWorkRecord);
	}
}
