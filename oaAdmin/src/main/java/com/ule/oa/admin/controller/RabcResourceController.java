package com.ule.oa.admin.controller;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ule.oa.base.po.RabcResource;
import com.ule.oa.base.po.Role;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.RabcResourceService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.JSON;
import com.ule.oa.common.utils.JsonWriter;
import com.ule.oa.common.utils.json.JSONUtils;

/**
  * @ClassName: ResourceController
  * @Description: 资源（菜单和按钮）控制层
  * @author minsheng
  * @date 2017年11月22日 下午3:59:11
 */
@Controller
@RequestMapping("resource")
public class RabcResourceController {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private RabcResourceService resourceService;
	@Autowired
	private EmployeeService employeeService;
	
	/**
	 * @throws OaException 
	  * getResourceTree(跳转到资源树)
	  * @Title: getResourceTree
	  * @Description: 跳转到资源树
	  * @return    设定文件
	  * String    返回类型
	  * @throws
	 */
	@RequestMapping("/getResourceTree.htm")
	public String getResourceTree(HttpServletRequest request,Role role) throws OaException{
		request.setAttribute("companyId", employeeService.getCurrentEmployee().getCompanyId());
		request.setAttribute("roleId", role.getId());
		request.setAttribute("type",role.getDelFlag());
		return "base/resource/resource_tree";
	}
	
	/**
	  * getTreeList(获取资源树)
	  * @Title: getTreeList
	  * @Description: 获取资源树
	  * @param resource
	  * @return    设定文件
	  * JSON    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/getTreeList.htm")
	public JSON getTreeList(RabcResource resource){
		return JsonWriter.successfulResult(JSONUtils.write(resourceService.getTreeList(resource)));
	}
	
	@ResponseBody
	@RequestMapping("/saveSet.htm")
	public JSON save(String roleId,String sourceIds){
		Map<String,Object> map = new HashMap<String,Object>();
		
		try{
			resourceService.saveSet(roleId, sourceIds, employeeService.getCurrentEmployee());
			map.put("message", "保存成功");
			map.put("flag", true);
		}catch(Exception e){
			map.put("flag", false);
			map.put("message", "保存失败,"+e.getMessage());
		}
		
		return JsonWriter.successfulResult(map);
	}
}
