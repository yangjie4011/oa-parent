package com.ule.oa.service.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ule.oa.base.po.AttnUsers;
import com.ule.oa.base.service.AttnUsersService;
import com.ule.oa.common.utils.json.JSONUtils;

@Controller
@RequestMapping("attnUsers")
public class AttnUsersController {

	@Resource
	private AttnUsersService attnUsersService;
	
	@RequestMapping("/save.htm")
	public Map<String, String> save(String jsonData){
		Map<String, String> map  = new HashMap<String, String>();
		try{
			AttnUsers user = JSONUtils.read(jsonData, AttnUsers.class);
			attnUsersService.save(user);
			map.put("result", "success");
		}catch(Exception e){
			map.put("result", "fail");
		}
		return map;
	}
	
	@ResponseBody
	@RequestMapping("/saveBacth.htm")
	public Map<String, String> saveBacth(String param){
		Map<String, String> map  = new HashMap<String, String>();
		List<AttnUsers> list = new ArrayList<AttnUsers>();
		try{
			map.put("result", "fail");
			List<Map> paramList = JSONUtils.read(param,java.util.List.class);
			if(paramList!=null&&paramList.size()>0){
				for(int i=0;i<paramList.size();i++){
					Map u = paramList.get(i);
					AttnUsers user = new AttnUsers();
					user.setDeptid(Integer.valueOf(u.get("deptid").toString()));
					user.setName(u.get("name").toString());
					user.setOaEmpId(Long.valueOf((u.get("oaEmpId").toString())));
					list.add(user);
				}
				attnUsersService.saveBatch(list);
				map.put("result", "success");
			}
			
		}catch(Exception e){
			map.put("result", "fail");
		}
		return map;
	}
	
	@ResponseBody
	@RequestMapping("/selectByFingerprintId.htm")
	public Map<String, String> selectByFingerprintId(String fingerprintId){
		Map<String, String> map  = new HashMap<String, String>();
		try{
			AttnUsers attnUsers = attnUsersService.selectByFingerprintId(Integer.valueOf(fingerprintId));
			if(attnUsers!=null){
				map.put("result", "success");
				map.put("userid", String.valueOf(attnUsers.getUserid()));
			}else{
				map.put("result", "false");
				map.put("message", "根据指纹ID未找到相应用户！");
			}
		}catch(Exception e){
			map.put("result", "fail");
		}
		return map;
	}
	
	@ResponseBody
	@RequestMapping("/updateById.htm")
	public Map<String, String> updateById(String jsonData){
		Map<String, String> map  = new HashMap<String, String>();
		try{
			AttnUsers user = JSONUtils.read(jsonData, AttnUsers.class);
			attnUsersService.updateById(user);
			map.put("result", "success");
		}catch(Exception e){
			map.put("result", "fail");
		}
		return map;
	}
	@ResponseBody
	@RequestMapping("/bindFingerPrint.htm")
	public Map<String, String> bindFingerPrint(String fingerPrint,String empId){
		Map<String, String> map  = new HashMap<String, String>();
		try{
			attnUsersService.bindFingerPrint(fingerPrint,empId);
			map.put("result", "success");
		}catch(Exception e){
			map.put("result", "fail");
		}
		return map;
	}
}
