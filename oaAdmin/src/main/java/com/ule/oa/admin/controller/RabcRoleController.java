package com.ule.oa.admin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.ule.oa.admin.util.IsMenu;
import com.ule.oa.base.po.RabcRole;
import com.ule.oa.base.service.RabcRoleService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.JSON;
import com.ule.oa.common.utils.JsonWriter;
import com.ule.oa.common.utils.json.JSONUtils;

/**
 * 
  * @ClassName: URoleController
  * @Description: 角色管理
  * @author xujintao
  * @date 2019年4月12日 下午1:53:28
 */
@Controller
@RequestMapping("uRole")
public class RabcRoleController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private RabcRoleService rabcRoleService;
	
	/**
	 * 菜单权限
	 * @param request
	 * @return
	 */
	@IsMenu
	@RequestMapping("/roleManagement.htm")
	public ModelAndView scheduleClass(HttpServletRequest request){
		return new ModelAndView("base/role/role_resource");
	}
	
	/**
	  * getRoleInfo(查询角色详情)
	  * @Title: getRoleInfo
	  * @Description: TODO
	  * @return    设定文件
	  * List<URole>    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping(value="/getRoleInfo.htm")
	public RabcRole getRoleInfo(Long roleId){
	
		return rabcRoleService.getRoleInfo(roleId);
	}
	
	/**
	  * getAllURoleList(查询所有角色)
	  * @Title: getAllURoleList
	  * @Description: TODO
	  * @return    设定文件
	  * List<URole>    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping(value="/getAllURoleList.htm")
	public List<RabcRole> getAllURoleList(Long departId){
	
		return rabcRoleService.getAllURoleListByDepartId(departId);
	}
	
	@ResponseBody
	@RequestMapping("/getRoleResourceTree.htm")
	public JSON getRoleResourceTree(Long roleId){
		
		return JsonWriter.successfulResult(JSONUtils.write(rabcRoleService.getRoleResourceTree(roleId)));
	}
	
	//新建角色
	@ResponseBody
	@RequestMapping(value="/addRole.htm")
	public Map<String, Object> addRole(RabcRole uRole){
	
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			rabcRoleService.addRole(uRole);
			map.put("success", true);
			map.put("message", "保存成功");
		}catch(OaException o){
			map.put("success", false);
			map.put("message", o.getMessage());
		}catch(Exception e){
			map.put("success", false);
			map.put("message", e.getMessage());
		}
		return map;
	}
	
	//保存菜单
	@ResponseBody
	@RequestMapping(value="/saveResource.htm")
	public Map<String, Object> saveResource(Long roleId, String resourceIds){
		
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			rabcRoleService.saveResource(roleId,resourceIds);
			map.put("success", true);
			map.put("message", "保存成功");
		}catch(OaException o){
			map.put("success", false);
			map.put("message", o.getMessage());
		}catch(Exception e){
			map.put("success", false);
			map.put("message", e.getMessage());
		}
		return map;
	}
	
	//删除角色
	@ResponseBody
	@RequestMapping(value="/delRole.htm")
	public Map<String, Object> delRole(Long roleId){
		
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			rabcRoleService.delRole(roleId);
			map.put("success", true);
			map.put("message", "删除成功");
		}catch(OaException o){
			map.put("success", false);
			map.put("message", o.getMessage());
		}catch(Exception e){
			map.put("success", false);
			map.put("message", e.getMessage());
		}
		return map;
	}
	@ResponseBody
	@RequestMapping(value="/getOperationTree.htm")
	public JSON getOperationTree(Integer resourceId,Long roleId){
		
		return JsonWriter.successfulResult(JSONUtils.write(rabcRoleService.getOperationTree(resourceId,roleId)));
	}
	
	@ResponseBody
	@RequestMapping("/initAllOperationIds.htm")
	public List<Long> initAllOperationIds(Long roleId){
	
		List<Long> resourceIds = rabcRoleService.initAllOperationIds(roleId);
		return resourceIds;
	}
	
}
