package com.ule.oa.admin.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import com.google.common.collect.Maps;
import com.ule.oa.admin.util.IsMenu;
import com.ule.oa.admin.util.IsOperation;
import com.ule.oa.admin.util.ResponseUtil;
import com.ule.oa.base.po.EmpDepart;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.EmpDepartService;
import com.ule.oa.base.service.EmployeeService;
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
	@Resource
	private EmpDepartService empDepartService;
	@Resource
	private ConfigService configService;

	/**
	 * getEmpMobileByUserName(根据用户名获取员工电话) @Title:
	 * getEmpMobileByUserName @Description: 根据用户名获取员工电话 @param
	 * userName @return @throws OaException 设定文件 EmployeeApp 返回类型 @throws
	 */
	@ResponseBody
	@RequestMapping("/getEmpMobileByUserName.htm")
	public JSON getEmpMobileByUserName(String userName) {
		
		try {
			return JsonWriter.successfulResult(employeeService.getEmpMobileByUserName(userName));
		} catch (OaException e) {
			return JsonWriter.failedMessage(e.getMessage());
		}
	}

	/**
	 * getPowerIndex(跳转到权利行使人页面) @Title: getPowerIndex @Description:
	 * 跳转到权利行使人页面 @return 设定文件 String 返回类型 @throws
	 */
	@RequestMapping("/getPowerIndex.htm")
	public String getPowerIndex() {
		return "base/emp/power_index";
	}

	/**
	 * getLeaderIndex(跳转到部门负责人页面) @Title: getLeaderIndex @Description:
	 * 跳转到部门负责人页面 @return 设定文件 String 返回类型 @throws
	 */
	@RequestMapping("/getLeaderIndex.htm")
	public String getLeaderIndex() {
		return "base/emp/leader_index";
	}

	//员工信息管理-员工查询-查询
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/getPageList.htm")
	public PageModel<Employee> getPageList(Employee employee) {
		
		PageModel<Employee> pm = new PageModel<Employee>();
		pm.setRows(new java.util.ArrayList<Employee>());
		pm.setTotal(0);
		pm.setPageNo(1);

		try {
			pm = employeeService.getPageList(employee);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return pm;
	}

	//员工信息管理-员工查询-查询
	@ResponseBody
	@RequestMapping("/getDeptList.htm")
	public PageModel<Employee> getDeptList(Employee employee) {
		
		PageModel<Employee> pm = new PageModel<Employee>();
		pm.setRows(new java.util.ArrayList<Employee>());
		pm.setTotal(0);
		pm.setPageNo(1);

		try {
			pm = employeeService.getDeptList(employee);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return pm;
	}
	
	
	
	@ResponseBody
	@RequestMapping(value = "/getListByPage.htm", produces = "text/json;charset=UTF-8")
	public void getListByPage(Employee employee, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
	
		response.setContentType("text/plain");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		PrintWriter out = response.getWriter();
		String jsonpCallback = request.getParameter("jsonpCallback");// 客户端请求参数
		PageModel<Employee> pm = new PageModel<Employee>();
		pm.setRows(new java.util.ArrayList<Employee>());
		pm.setTotal(0);
		pm.setPageNo(1);
		List<Long> jobStatusList = new ArrayList<Long>();
		jobStatusList.add(0L);
		jobStatusList.add(2L);
		employee.setJobStatusList(jobStatusList);
		pm = employeeService.getSelectDivList(employee);
		out.println(jsonpCallback + "(" + JSONUtils.write(pm) + ")");// 返回jsonp格式数据
		out.flush();
		out.close();
	}

	@IsMenu
	@RequestMapping("/empQueryList.htm")
	public ModelAndView empQueryList() {
		return new ModelAndView("base/emp/empQueryList");
	}

	//员工信息管理-员工查询-导出
	@IsOperation(returnType=true)//需要校验操作权限
	@RequestMapping("/exportEmployeeList.htm")
	public void exportEmployeeList(Employee employee, HttpServletResponse response, HttpServletRequest request) throws IOException {
	
		OutputStream os = null;

		try {
			employee.setCode(URLDecoder.decode(employee.getCode(), "UTF-8"));
			employee.setCnName(URLDecoder.decode(employee.getCnName(), "UTF-8"));
			ResponseUtil.setDownLoadExeclInfo(response,request,"员工列表_");
			HSSFWorkbook hSSFWorkbook = employeeService.exportEmployeeList(employee);
			os = response.getOutputStream();
			if(hSSFWorkbook!=null){
				hSSFWorkbook.write(os);
			}
		} catch (Exception e) {
			
		}finally{
			if(os!=null) {
				os.flush();
				os.close();
			}
		}
	}
	/**
	 * 员工信息管理-员工查询-上传
	 * @param file
	 * @return
	 */
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping(value="/importEmployeeList.htm",method = RequestMethod.POST)
	public Map<String, String> importEmployee(@RequestParam(value="file",required = false)MultipartFile file) {

		String result = null;
		try {
			Map<String,Object> resultMap = employeeService.importEmployee(file);
			result = (String) resultMap.get("resultMsg");
			Map<Long,String> updateEmpMap = (Map<Long, String>) resultMap.get("updateEmpMap");
			employeeService.updateEmpReportLeader(updateEmpMap);
		} catch (OaException e) {
			result = e.getMessage();
		} catch (Exception e) {
			result = "请下载最新模板后重试";
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("response", result);
        return map;
	}

    //员工信息管理-员工查询-批量修改
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/updReporterBatch.htm")
	public Map<String, String> updReporterBatch(Long reporterId, String employeeIds) {
		
		String result = "修改成功！";
		try {
			employeeService.updReporterBatch(reporterId, employeeIds);
		} catch (Exception e) {
			result = "修改失败！";
		}

		Map<String, String> map = new HashMap<>();
		map.put("response", result);
		return map;
	}

	// 弹框的 增删改
	@ResponseBody
	@RequestMapping("/getEmpInfo.htm")
	public Employee getConfigbyId(Long id) {
		
		Employee employee = new Employee();
		try {
			Employee employeeInfo = new Employee();
			employeeInfo.setId(id);
			employee = employeeService.queryEmpInfoById(employeeInfo);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return employee;
	}
	

	//入离职管理-员工离职通知-离职
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping(value = "/updateEmpQuit.htm", produces = "application/json; charset=utf-8")
	public Map<String, Object> update(Employee employee) {
		
		Map<String, Object> result = Maps.newHashMap();
		try {
			result = employeeService.updateEmpQuitInfo(employee);
		} catch (OaException e) {
			result.put("flag", false);
			result.put("code", "0001");
		} catch (Exception e) {
			result.put("flag", false);
			result.put("code", "0002");
		}
		return result;
	}
	
	//入离职管理-员工离职通知-离职日期修改
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping(value = "/updateEmpQuitTime.htm", produces = "application/json; charset=utf-8")
	public Map<String, Object> updateEmpQuitTime(Employee employee) {
		
		Map<String, Object> result = Maps.newHashMap();
		try {
			result = employeeService.updateEmpQuitInfo(employee);
		} catch (OaException e) {
			result.put("flag", false);
			result.put("code", "0001");
		} catch (Exception e) {
			result.put("flag", false);
			result.put("code", "0002");
		}
		return result;
	}

	//员工信息管理-员工查询-修改指纹ID
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/updateEmpFingerprintId.htm")
	public JSON updateEmpFingerprintId(Employee employee) throws Throwable {
		
		return JsonWriter.successfulResult(employeeService.updateEmpFingerprintId(employee));
	}

	@ResponseBody
	@RequestMapping("/deleteEmp.htm")
	public JSON deleteCompanyConfig(Long id, Long version) {
	
		Map<String, Object> map = new HashMap<String, Object>();
		Employee employee = new Employee();
		try {
			employee.setId(id);
			employee.setDelFlag(1);
			employee.setVersion(version);
			employeeService.updateById(employee);
			map.put("message", "删除成功");
			map.put("flag", true);
		} catch (Exception e) {
			map.put("flag", false);
			map.put("message", "删除失败," + e.getMessage());
		}
		return JsonWriter.successfulResult(map);
	}
	
	//根据部门获取汇报人
	@ResponseBody
	@RequestMapping("/getReportPerson.htm")
	public List<Employee> getReportPerson(Long departId){
	
		List<Employee> pm=new ArrayList<Employee>();
		EmpDepart empDepart = new EmpDepart();
		empDepart.setDepartId(departId);
		Employee em = new Employee();
		em.setEmpDepart(empDepart);
		try {
			pm = employeeService.getReportPerson(em);
		} catch (Exception e) {
			
		}
		return pm;
	}
	@IsMenu
	@RequestMapping("/empDepartManages.htm")
	public ModelAndView empDepartManages() {
		return new ModelAndView("base/emp/empDepartManages");
	}
	
	//员工信息管理-员工部门管理-确认
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping(value = "/updateEmpDepart.htm", produces = "application/json; charset=utf-8")
	public Map<String, Object> updateEmpDepart(Employee employee) {
		
		Map<String, Object> result = Maps.newHashMap();
		try {
			result = employeeService.updateEmpDepart(employee);
			result.put("flag", true);
		}catch (OaException e) {
			result.put("flag", false);
		}  catch (Exception e) {
			result.put("flag", false);
			result.put("code", "0002");
		}
		return result;
	}
	
	@IsMenu
	@RequestMapping("/reportingObjectManage.htm")
	public ModelAndView reportingObjectManage() {
		return new ModelAndView("base/emp/reportingObjectManage");
	}
	
	//获取所有汇报人
	@ResponseBody
	@RequestMapping("/getAllReportPerson.htm")
	public List<Employee> getAllReportPerson(){
		List<Employee> pm = employeeService.getAllReportPerson();
		return pm;
	}
	//批量保存汇报人
	//员工信息管理-汇报对象管理-确认
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/saveReportPerson.htm")
	public Map<String, Object> saveReportPerson(String empIds,String employeeLeader){
		
		Map<String, Object> result = Maps.newHashMap();
		try {
			result = employeeService.saveReportPerson(empIds,employeeLeader);
		} catch (OaException o) {
			result.put("success", false);
			result.put("message", o.getMessage());
		} catch (Exception e) {
			result.put("success", false);
			result.put("message", "保存失败");
		}
		return result;
	}
}
