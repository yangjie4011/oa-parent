package com.ule.oa.admin.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ule.oa.admin.util.IsMenu;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.JSON;
import com.ule.oa.common.utils.JsonWriter;
import com.ule.oa.common.utils.json.JSONUtils;

/**
 * 
  * @ClassName: DepartController
  * @Description: 部门
  * @author jiwenhang
  * @date 2017年5月9日 下午1:53:28
 */
@Controller
@RequestMapping("depart")
public class DepartController {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private DepartService departService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private UserService userService;
	
	@IsMenu
	@RequestMapping("/index.htm")
	public String index(HttpServletRequest request) throws OaException{
		request.setAttribute("companyId", employeeService.getCurrentEmployee().getCompanyId());
		
		return "base/org/depart";
	}
	
	/**
	 * 组织机构图
	 * @return
	 */
	@RequestMapping("/orgChartIndex.htm")
	public String orgChartIndex(){
		return "base/orgChart/org_chart";
	}
	
	/**
	 * 部门汇报关系图
	 * @return
	 */
	@RequestMapping("/orgEmpChartIndex.htm")
	public String orgEmpChartIndex(){
		return "base/orgChart/org_emp_chart";
	}
	
	/**
	 * 右侧显示列表
	 * @param request
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/list.htm")
	public String list(HttpServletRequest request, Long id){

		List<Depart> departList = departService.getDepartList(id);
		if(departList != null && departList.size() > 0) {
			for (Depart depart : departList) {
				if(depart.getType() != null && depart.getType().intValue() == 1) {
					//一级部门,查询该部门下所有部门的员工数
					depart.setEmpCount(employeeService.getEmpTotalByDepartId(depart.getId()));
				} else {
					//非一级部门,查询该部门下的员工数
					depart.setEmpCount(employeeService.getEmpCountByDepartId(depart.getId()));
				}
			}
		}
		return JSONUtils.write(departList);
	}
	
	/**
	 * 左侧树结构
	 * @param depart
	 * @return
	 */
	@ResponseBody 
	@RequestMapping("/tree.htm")
	public String tree(Depart depart){

		return JSONUtils.write(departService.getTreeList(depart));
	}
	
	/**
	 * APP树结构
	 * @param depart
	 * @return
	 * @throws IOException 
	 */
	@ResponseBody 
	@RequestMapping(value = "/treeApp.htm", produces = "text/json;charset=UTF-8")
	public void treeApp(Depart depart,HttpServletRequest request,HttpServletResponse response) throws IOException{
		response.setContentType("text/plain");  
        response.setHeader("Pragma", "No-cache");  
        response.setHeader("Cache-Control", "no-cache");  
        response.setDateHeader("Expires", 0);  
        Map<String,String> map = new HashMap<String,String>();   
        map.put("result", "content");  
        PrintWriter out = response.getWriter();       
        String jsonpCallback = request.getParameter("jsonpCallback");//客户端请求参数  
        out.println(jsonpCallback+"("+JSONUtils.write(departService.getTreeAppList(depart))+")");//返回jsonp格式数据  
        out.flush();  
        out.close();  
	}
	
	/**
	 * 组织机构图  
	 * 查询部门树结构（部门名称,负责人职位,负责人姓名）
	 * @param departId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getDepartList.htm")
	public String getDepartList(Long departId){

		Depart depart = new Depart();
		depart.setId(departId);
		List<Depart> list = departService.getDListByParentId(depart);  
        return JSONUtils.write(list);
	}
	
	/**
	 * 部门汇报关系图
	 * 查询部门树结构（部门名称,负责人职位,负责人姓名,人员姓名,人员职称）
	 * @param departId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getDepartEmpsList.htm")
	public String getDepartEmpsList(Long departId){

		Depart depart = new Depart();
		depart.setId(departId);
		List<Depart> list = departService.getDEListByParentId(depart);  
        return JSONUtils.write(list);
	}
	
	/**
	 * 跳转编辑页面
	 * @param request
	 * @param departName
	 * @return
	 */
	@RequestMapping("/toEdit.htm")
	public String toEdit(HttpServletRequest request, String departId){

		Depart depart = departService.getInfoById(Long.valueOf(departId));
		request.setAttribute("depart", depart);
		return "base/org/edit";
	}

	/**
	 * 跳转人员选择页面
	 * @param request
	 * @return
	 */
	@RequestMapping("/toChooseEmps.htm")
	public String toChooseEmps(HttpServletRequest request, String departId){

		request.setAttribute("departId", departId);
		return "base/org/choose_employs_by_depart";
	}
	
	/**
	 * 根据部门名称查询该部门下的员工信息以及职称信息
	 * @param request
	 * @param departName
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/powerList.htm")
	public List<Employee> powerList(HttpServletRequest request, String departId){

		return departService.getPowerList(departId);
	}
	
   //获取部门详细
	@ResponseBody
	@RequestMapping("/getInfoById.htm")
	public Depart getInfoById(Long departId){

		return departService.getInfoById(departId);
	}
	
	/**
	  * getTreeList(根据条件查询部门结果集)
	  * @Title: getTreeList
	  * @Description: 根据条件查询部门结果集
	  * @param depart    设定文件
	  * void    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/getTreeList.htm")
	public JSON getTreeList(Depart depart){

		return JsonWriter.successfulResult(JSONUtils.write(departService.getTreeList(depart)));
	}
	
	
	@ResponseBody 
	@RequestMapping(value = "/getEmployeeTreeList.htm", produces = "text/json;charset=UTF-8")
	public void getEmployeeTreeList(Depart depart,HttpServletRequest request,HttpServletResponse response) throws IOException{

		response.setContentType("text/plain");  
        response.setHeader("Pragma", "No-cache");  
        response.setHeader("Cache-Control", "no-cache");  
        response.setDateHeader("Expires", 0);  
        PrintWriter out = response.getWriter();       
        String jsonpCallback = request.getParameter("jsonpCallback");//客户端请求参数  
        out.println(jsonpCallback+"("+JSONUtils.write(departService.getEmployeeTreeList(depart))+")");//返回jsonp格式数据  
        out.flush();  
        out.close();  
	}
	
	/**
	  * getByCondition(根据条件查询部门信息)
	  * @Title: getByCondition
	  * @Description: 根据条件查询部门信息
	  * @param depart
	  * @return    设定文件
	  * JSON    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/getByCondition.htm")
	public Depart getByCondition(Depart depart){

		return departService.getByCondition(depart);
	}
	
	/**
	 * 新增部门
	 * @param depart
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/save.htm")
	public Map<String, Object> save(Depart depart){

		Date currentTime = DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_LONG), DateUtils.FORMAT_LONG);
		Map<String,Object> map = new HashMap<String, Object>();
		//获取当前登录人
		User user = userService.getCurrentUser();
		depart.setCode("D" + System.currentTimeMillis());
		depart.setCompanyId(user.getEmployee().getCompanyId());
		depart.setDelFlag(Depart.DEL_FLAG_NORMAL);
		depart.setCreateTime(currentTime);
		depart.setCreateUser(user.getEmployee() != null ? user.getEmployee().getCnName() : "");
		try{
			departService.save(depart);
			map.put("code", "0000");
			map.put("message", "保存成功");
		}catch(Exception e){
			map.put("code", "9999");
			map.put("message",e.getMessage());
		}
		return map;
	}
	
	/**
	 * 修改部门
	 * @param depart
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/update.htm")
	public Map<String,Object> update(Depart depart){

		Date currentTime = DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_LONG), DateUtils.FORMAT_LONG);
		//获取当前登录人
		User user = userService.getCurrentUser();
		Map<String,Object> map = new HashMap<String,Object>();
		depart.setUpdateTime(currentTime);
		depart.setUpdateUser(user.getEmployee() != null ? user.getEmployee().getCnName() : "");
		try{
			departService.updateById(depart);
			map.put("code", "0000");
			map.put("message", "修改成功");
		}catch(Exception e){
			map.put("code", "9999");
			map.put("message", "修改失败");
		}
		return map;
	}
	
	@ResponseBody
	@RequestMapping("/updateDeptLeaderById.htm")
	public Map<String,Object> updateDeptLeaderById(Depart depart){

		Map<String,Object> map = new HashMap<String,Object>();
		try{
			departService.updateDeptLeaderById(depart);
			map.put("code", "0000");
			map.put("message", "修改成功");
		}catch(Exception e){
			map.put("code", "9999");
			map.put("message",e.getMessage());
		}
		return map;
	}
	
	/**
	 * 删除部门
	 * @param depart
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/delete.htm")
	public Map<String,Object> delete(Long id){

		Map<String,Object> map = new HashMap<String,Object>();
		try{
			departService.deleteById(id);
			map.put("code", "0000");
			map.put("message", "删除成功");
		}catch(Exception e){
			map.put("code", "9999");
			map.put("message", e.getMessage());
		}
		return map;
	}
	
	/**
	  * getListByCondition(根据条件查询所有)
	  * @Title: getListByCondition
	  * @Description: 根据条件查询所有
	  * @param depart    设定文件
	  * void    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/getListByCondition.htm")
	public JSON getListByCondition(Depart depart){

		return JsonWriter.successfulResult(JSONUtils.write(departService.getListByCondition(depart)));
	}
	
	/**
	 * 获取职位序列最高的部门负责人
	 * @param departId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getMLeaderByDepartId.htm")
	public List<Employee> getMLeaderByDepartId(String departId){

		Map<Integer, List<Employee>> littelList = new HashMap<Integer, List<Employee>>();
		List<Employee> mLeaderList = employeeService.getMLeaderByDepartId(Long.valueOf(departId));
		Integer minValue = 0;
		if(mLeaderList != null && mLeaderList.size() > 0) {
			minValue = mLeaderList.get(0).getEmpPosition().getPosition().getSeqRank();
			for (Employee employee : mLeaderList) {
				if(employee.getEmpPosition().getPosition().getSeqRank() < minValue) {
					minValue = employee.getEmpPosition().getPosition().getSeqRank();
				}
				List<Employee> eList = new ArrayList<Employee>();
				if(littelList.containsKey(employee.getEmpPosition().getPosition().getSeqRank())) {
					eList = littelList.get(employee.getEmpPosition().getPosition().getSeqRank());
				}
				eList.add(employee);
				littelList.put(employee.getEmpPosition().getPosition().getSeqRank(), eList);
			}
		}
		List<Employee> returnList = littelList.get(minValue);
		return returnList;
	}
	
	/**
	 * 查询根据人员Id查询该人员所在部门负责人
	 * @param empId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getInfoByEmpId.htm", produces = "text/json;charset=UTF-8")
	public String getInfoByEmpId(String empId){

		Depart depart = departService.getInfoByEmpId(Long.valueOf(empId));
        return JSONUtils.write(depart);
	}
	
	
	/**
	 * 查询根据人员Id查询该人员所在部门负责人
	 * @param empId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getDepartByEmpId.htm")
	public Depart getDepartByEmpId(String empId){

		return departService.getInfoByEmpId(Long.valueOf(empId));
	}
	
	/**
	  * getFirstDepart(这里用一句话描述这个方法的作用)
	  * @Title: getFirstDepart
	  * @Description: TODO
	  * @return    设定文件
	  * List<Depart>    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping(value="/getFirstDepart.htm")
	public List<Depart> getFirstDepart(){
		return departService.getFirstDepart();
	}
	
	/**
	  * getAllDepart(获取所有部门包括历史部门)
	  * @Title: getAllDepart
	  * @Description: TODO
	  * @return    设定文件
	  * List<Depart>    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping(value="/getAllDepart.htm")
	public List<Depart> getAllDepart(){
		return departService.getAllDepart();
	}
	
	/**
	  * getByParentId(根据父节点获取子节点)
	  * @Title: getByParentId
	  * @Description: 根据父节点获取子节点
	  * @param id
	  * @return    设定文件
	  * List<Depart>    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping(value="/getByParentId.htm")
	public List<Depart> getByParentId(Long id){

		return departService.getByParentId(id);
	}
}
