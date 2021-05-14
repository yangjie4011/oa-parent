package com.ule.oa.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ule.oa.base.po.EmpFamilyMember;
import com.ule.oa.base.service.EmpFamilyMemberService;

/**
  * @ClassName: EmpFamilyMemberController
  * @Description: 员工家属信息表
  * @author minsheng
  * @date 2017年5月19日 下午1:44:04
 */
@Controller
@RequestMapping("empFamilyMember")
public class EmpFamilyMemberController {
	@Autowired
	private EmpFamilyMemberService empFamilyMemberService;
	
	/**
	  * save(保存家属信息)
	  * @Title: save
	  * @Description: 保存家属信息
	  * @param empFamilyMember
	  * @return    设定文件
	  * Map<String,Object>    返回类型
	  * @throws
	 */
	@RequestMapping("/save.htm")
	@ResponseBody
	public Map<String,Object> save(EmpFamilyMember empFamilyMember){
		Map<String,Object> map = new HashMap<String,Object>();
		
		try{
			empFamilyMemberService.save(empFamilyMember);
			map.put("result", "保存成功!");
			map.put("flag", true);
		}catch(Exception e){
			map.put("msg", e.getMessage());
			map.put("result", "保存失败!");
			map.put("flag", false);
		}
		
		return map;
	}
	
	/**
	  * update(修改家属信息)
	  * @Title: update
	  * @Description: 保存家属信息
	  * @param empFamilyMember
	  * @return    设定文件
	  * Map<String,Object>    返回类型
	  * @throws
	 */
	@RequestMapping("/updateById.htm")
	@ResponseBody
	public Map<String,Object> updateById(EmpFamilyMember empFamilyMember){
		Map<String,Object> map = new HashMap<String,Object>();
		String option;
		if(0 == empFamilyMember.getDelFlag().intValue()){
			option = "修改";
		}else{
			option = "删除";
		}
		try{
			empFamilyMemberService.updateById(empFamilyMember);
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
	public List<EmpFamilyMember> getListByCondition(EmpFamilyMember empFamilyMember){
		return empFamilyMemberService.getListByCondition(empFamilyMember);
	}
}
