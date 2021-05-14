package com.ule.oa.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ule.oa.base.po.EmpSchool;
import com.ule.oa.base.service.EmpSchoolService;

@Controller
@RequestMapping("empSchool")
public class EmpSchoolController {
	@Autowired
	private EmpSchoolService empSchoolService;
	
	/**
	  * save(添加教育经历)
	  * @Title: save
	  * @Description: 添加教育经历
	  * @param empSchool
	  * @return    设定文件
	  * Map<String,Object>    返回类型
	  * @throws
	 */
	@RequestMapping("/save.htm")
	@ResponseBody
	public Map<String,Object> save(EmpSchool empSchool){
		Map<String,Object> map = new HashMap<String,Object>();
		
		try{
			empSchoolService.save(empSchool);
			map.put("flag", true);
			map.put("result", "保存成功！");
		}catch(Exception e){
			map.put("msg", e.getMessage());
			map.put("flag", false);
			map.put("result", "保存失败！");
		}
		
		return map;
	}
	
	/**
	  * updateById(修改教育经历)
	  * @Title: updateById
	  * @Description: 修改教育经历
	  * @param empSchool
	  * @return    设定文件
	  * Map<String,Object>    返回类型
	  * @throws
	 */
	@RequestMapping("/updateById.htm")
	@ResponseBody
	public Map<String,Object> updateById(EmpSchool empSchool){
		Map<String,Object> map = new HashMap<String,Object>();
		
		String option = null;
		if(0 == empSchool.getDelFlag()){
			option = "修改";
		}else{
			option = "删除";
		}
		try{
			empSchoolService.updateById(empSchool);
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
	public List<EmpSchool> getListByCondition(EmpSchool empSchool){
		return empSchoolService.getListByCondition(empSchool);
	}
}
