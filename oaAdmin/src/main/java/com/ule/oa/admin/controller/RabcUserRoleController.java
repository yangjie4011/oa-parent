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
import com.ule.oa.admin.util.IsMenu;
import com.ule.oa.base.po.RabcRole;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.RabcUserRoleService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.JSON;
import com.ule.oa.common.utils.JsonWriter;
import com.ule.oa.common.utils.PageModel;

/**
 * 
  * @ClassName: URoleController
  * @Description: 角色管理
  * @author xujintao
  * @date 2019年4月12日 下午1:53:28
 */
@Controller
@RequestMapping("userRole")
public class RabcUserRoleController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private RabcUserRoleService rabcUserRoleService;
	
	
	//用户管理权限操作//
	
	/**
	  * index(跳转到用户管理首页)
	  * @Title: index
	  * @Description: 跳转到用户管理首页
	  * @param request
	  * @return
	  * @throws OaException    设定文件
	  * String    返回类型
	  * @throws
	 */
	@IsMenu
	@RequestMapping("/index.htm")
	public String index(HttpServletRequest request) throws OaException{
		request.setAttribute("companyId", employeeService.getCurrentEmployee().getCompanyId());
		return "base/role/user_index";
	}
	
	
	/**
	  * getPageList(分页查询角色)
	  * @Title: getPageList
	  * @Description: 分页查询角色
	  * @param role
	  * @return    设定文件
	  * PageModel<Role>    返回类型
	  * @throwss
	 */
	@ResponseBody
	@RequestMapping("/getPageList.htm")
	public PageModel<User> getPageList(User user){
	
		PageModel<User> pm=new PageModel<User>();
		pm.setRows(new java.util.ArrayList<User>());
		try {
			pm = rabcUserRoleService.getPageList(user);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	@ResponseBody
	@RequestMapping("/queryUserById.htm")
	public JSON queryUserById(Integer id){

		Map<String,Object> map = new HashMap<String,Object>();
		try{
			User usre = rabcUserRoleService.getUserById(id);
			map.put("usre", usre);
			map.put("flag", true);
		}catch(Exception e){
			map.put("flag", false);
			map.put("message", "查询失败,"+e.getMessage());
		}
		return JsonWriter.successfulResult(map);
	}
	
	

	@ResponseBody
	@RequestMapping("/forzenUserById.htm")
	public JSON forzenUserById(User user){

		Map<String,Object> map = new HashMap<String,Object>();
		try{
			rabcUserRoleService.forzenUserById(user);
			map.put("message", "更新成功");
			map.put("flag", true);
		}catch(Exception e){
			map.put("flag", false);
			map.put("message", "更新失败,"+e.getMessage());
		}
		return JsonWriter.successfulResult(map);
	}
	
	@ResponseBody
	@RequestMapping("/updateById.htm")
	public JSON update(User user){

		Map<String,Object> map = new HashMap<String,Object>();
		try{
			rabcUserRoleService.updateUserById(user);
			map.put("message", "更新成功");
			map.put("flag", true);
		}catch(Exception e){
			map.put("flag", false);
			map.put("message", "更新失败,"+e.getMessage());
		}
		return JsonWriter.successfulResult(map);
	}

	
	
	//通过部门获取 权限
	@ResponseBody
	@RequestMapping("/getRoleInfosbyDepartId.htm")
	public Map<String,Object> getSettingInfos(Long departId,Long userId){

		Map<String,Object> result = new HashMap<String,Object>();
		try {
			result=rabcUserRoleService.getRoleInfobyDepartIdAndUserId(departId,userId);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return result;
	}	
	
	
	//通过部门获取 权限
	@ResponseBody
	@RequestMapping("/getDepartRoleListByUserId.htm")
	public List<RabcRole> getDepartRoleList(Long userId){

		List<RabcRole> rabcRolelist=null;
		try {
			 rabcRolelist =rabcUserRoleService.getDepartRoleListByUserId(userId);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return rabcRolelist;
	}	
	
	
	
}
