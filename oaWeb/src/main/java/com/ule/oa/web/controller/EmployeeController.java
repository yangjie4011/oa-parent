package com.ule.oa.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.CompanyService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.JSON;
import com.ule.oa.common.utils.JsonWriter;
import com.ule.oa.common.utils.PageModel;
import com.ule.oa.common.utils.json.JSONUtils;

@Controller
@RequestMapping("employee")
public class EmployeeController {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private UserService userService;
	
	
	@RequestMapping("/index.htm")
	public String index(HttpServletRequest request) throws OaException{
		request.setAttribute("id", companyService.getCurrentCompanyId());
		request.setAttribute("companyId", companyService.getCurrentCompanyId());
		request.setAttribute("currentCompanyId", employeeService.getCurrentEmployeeId());
		
		return "base/employee/employee_index";
	}
	
	@RequestMapping("/indexPerson.htm")//个人中心
	public String indexPerson(HttpServletRequest request) throws OaException{
		
		User user = userService.getCurrentUser();
		
		Long empId = user.getEmployeeId();
		Employee conditon  = new Employee();
		conditon.setId(empId);
		
		Employee empDetail = employeeService.getEmpDetailByCondition(conditon);	
		
		request.setAttribute("workAddressType", user.getEmployee().getWorkAddressType());
		request.setAttribute("employeeId", empId);
		request.setAttribute("cnName", empDetail.getCnName());
		request.setAttribute("departName", empDetail.getEmpDepart().getDepart().getName());
		request.setAttribute("positionName", empDetail.getEmpPosition().getPosition().getPositionName());	
		request.setAttribute("reportToLeader", empDetail.getReportToLeaderName());	
		request.setAttribute("empPic", user.getEmployee().getPicture());//登录成功以后，首页获取图片的时候，默认将获取到的图片已经set到缓存中了
		return "index/index_person";
	}
	
	/**
	  * getPageList(分页查询员工信息)
	  * @Title: getPageList
	  * @Description: 分页查询员工信息
	  * @param position
	  * @return    设定文件
	  * PageModel<employee>    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/getPageList.htm")
	public PageModel<Employee> getPageList(Employee employee){
		PageModel<Employee> pm=new PageModel<Employee>();
		pm.setRows(new java.util.ArrayList<Employee>());
		pm.setTotal(0);
		pm.setPageNo(1);
		
		try {
			pm = employeeService.getPageList(employee);
		} catch (Exception e) {
			logger.error("getPageList.htm"+e.getMessage());
		}
		
		return pm;
	}
	
	/**
	  * toEmployeeAdd(跳转到新增页面)
	  * @Title: toEmployeeAdd
	  * @Description: 跳转到新增页面
	  * @return    设定文件
	  * ModelAndView    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/toEmployeeAdd.htm")
	public ModelAndView toEmployeeAdd(Employee employee){
		return new ModelAndView("base/employee/employee_add");
	}
	
	/**
	 * @throws OaException 
	  * toEmployeeEdit(跳转到编辑页面)
	  * @Title: toEmployeeEdit
	  * @Description: 跳转到编辑页面
	  * @return    设定文件
	  * ModelAndView    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/toEmployeeEdit.htm")
	public ModelAndView toEmployeeEdit(Employee employee) throws OaException{
		Map<String,Object> paraMap = new HashMap<String,Object>();
		paraMap.put("id", employee.getId());
		paraMap.put("currentCompanyId", employeeService.getCurrentEmployeeId());
		
		return new ModelAndView("base/employee/employee_edit",paraMap);
	}
	
	/**
	  * toEmployeeCheck(跳转到查看页面)
	  * @Title: toEmployeeCheck
	  * @Description: 跳转到查看页面
	  * @param employee
	  * @return    设定文件
	  * ModelAndView    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/toEmployeeCheck.htm")
	public ModelAndView toEmployeeCheck(Employee employee){
		Map<String,Object> paraMap = new HashMap<String,Object>();
		paraMap.put("id", employee.getId());
		
		return new ModelAndView("base/employee/employee_check",paraMap);
	}
	
	/**
	  * getLeaderById(获取部门负责人姓名和职位名称)
	  * @Title: getLeaderById
	  * @Description: 获取部门负责人姓名和职位名称
	  * @param id
	  * @return    设定文件
	  * Employee    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/getLeaderById.htm")
	public Employee getLeaderById(Long id){
		Employee em = new Employee();
		try {
			em = employeeService.getLeaderById(id);
		} catch (Exception e) {
			logger.error("getLeaderById.htm"+e.getMessage());
		}
		return em;
	}
	
	/**
	  * getEmployeeByCondition(根据条件查询员工信息)
	  * @Title: getEmployeeByCondition
	  * @Description: 根据条件查询员工信息
	  * @param employee
	  * @return    设定文件
	  * Json    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/get.htm")
	public Employee getEmpDetailByCondition(Employee employee){
 		return employeeService.getEmpDetailByCondition(employee);
	}
	
	/**
	  * getListByCondition(根据条件查询所有)
	  * @Title: getListByCondition
	  * @Description: 根据条件查询所有
	  * @param employee    设定文件
	  * void    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/getListByCondition.htm")
	public JSON getListByCondition(Employee employee){
		return JsonWriter.successfulResult(JSONUtils.write(employeeService.getListByCondition(employee)));
	}
	
	/**
	 * 
	  * getListByDid(根据部门ID分页获取员工信息)
	  * @Title: getListByDid
	  * @Description: 根据部门ID分页获取员工信息
	  * @param employee
	  * @return    设定文件
	  * PageModel<Employee>    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/getListForBoardroomDue.htm")
	public PageModel<Employee> getListForBoardroomDue(Employee employee){
 		return employeeService.getListForBoardroomDue(employee);
	}
	
	/**
	  * saveEmployeeInfo(在职员工管理页面--保存员工信息与成长履历表)
	  * @Title: saveEmployeeInfo
	  * @Description: 在职员工管理页面--保存员工信息与成长履历表
	  * @param file
	  * @param employee
	  * @return    设定文件
	  * JSON    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/saveEmployeeInfo.htm")
	public Map<String,Object> saveEmployeeInfo(@RequestParam("file") CommonsMultipartFile file,Employee employee){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("flag", false);
		
		try{
			employeeService.saveEmployeeInfo(file,employee);
			
			map.put("flag", true);
			map.put("msg","保存员工信息与成长履历表成功");
		}catch(Exception e){
			map.put("msg", e.getMessage());
		}
		return map;
	}
	
	/**
	  * saveEmployeeBase(在职员工管理页面--保存员工基础信息)
	  * @Title: saveEmployeeBase
	  * @Description: 在职员工管理页面--保存员工基础信息
	  * @param file
	  * @param employee
	  * @return    设定文件
	  * JSON    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/saveEmployeeBase.htm")
	public Map<String,Object> saveEmployeeBase(Employee employee){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("flag", false);
		
		try{
			employeeService.saveEmployeeBase(employee);
			
			map.put("flag", true);
			map.put("msg","保存员工基本信息成功");
		}catch(Exception e){
			map.put("msg", e.getMessage());
		}
		return map;
	}
	
	/**
	  * getMLeaderByDepartId(获得M级所有的员工信息)
	  * @Title: getMLeaderByDepartId
	  * @Description: 获得M级所有的员工信息
	  * @param departId
	  * @return    设定文件
	  * List<Employee>    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/getMLeaderByDepartId.htm")
	public List<Employee> getMLeaderByDepartId(Long departId){
		return employeeService.getMLeaderByDepartId(departId);
	}
	
	/**
	 * 根据姓名或编号查询员工信息
	 * @param condition
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getEmpListByCondition.htm")
	public PageModel<Employee> getEmpListByCondition(Employee employee){
		PageModel<Employee> pm=new PageModel<Employee>();
		pm.setRows(new java.util.ArrayList<Employee>());
		pm.setTotal(0);
		pm.setPageNo(1);
		try {
			pm = employeeService.getEmpListByCondition(employee);
		} catch (Exception e) {
			logger.error("getEmpListByCondition"+e.getMessage());
		}
		return pm;
	}
	
	/**
	  * 得到汇报对象下拉列表，得到除当年员工外所有的M级人员
	  * @Title: getReportPersonList
	  * @param id  当前编辑的员工id
	  * @return    设定文件
	  * List<Employee>    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/getReportPersonList.htm")
	public List<Employee> getReportPersonList(Long id){
		return employeeService.getReportPersonList(id);
	}
	
	/**
	  * 根据编号获取员工信息
	  * @Title: getInfoByCode
	  * @param code 员工编号
	  * @return    设定文件
	  * Map<String,Object>    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/getInfoByCode.htm")
	public Map<String,Object> getInfoByCode(String code){
		return employeeService.getInfoByCode(code);
	}
}
