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
import org.springframework.web.servlet.ModelAndView;
import com.ule.oa.admin.util.IsMenu;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.RemoteWorkRegisterService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.PageModel;


@Controller
@RequestMapping("workManagement")
public class RemoteWorkRegisterController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private RemoteWorkRegisterService empWorkManagementService;
	@Autowired
	private EmployeeService employeeService;
	
	//工作管理-远程工作登记
	@RequestMapping("/teleworkRegistration.htm")
	public ModelAndView teleworkRegistration(){
		
		return new ModelAndView("base/workManagement/teleworkRegistration");
	}
	
	//排班审核查询页面
	@IsMenu
	@RequestMapping("/remoteException.htm")
	public ModelAndView remoteException(){
		return new ModelAndView("base/workManagement/remoteException");
	}
	
	//工作管理-远程工作登记-查询
	//@IsOperation(returnType=false)//需要校验操作权限
	@RequestMapping("/queryList.htm")
	@ResponseBody
	public PageModel<Employee> queryChangeClass(Employee employee) throws OaException{
	
		PageModel<Employee> pm=new PageModel<Employee>();
		try{
			pm.setRows(new java.util.ArrayList<Employee>());
			pm=employeeService.getPageList(employee);
		}catch(Exception e){
			logger.error("queryChangeClass:",e);
		}
		return pm;
	}	
	
	//工作管理-远程工作登记-查询详情月日历
	//@IsOperation(returnType=false)//需要校验操作权限
	@RequestMapping("/queryWorkRegistInfo.htm")
	@ResponseBody
	public Map<String,Object> queryWorkRegistInfo(Long empId,String month) throws OaException{
	
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			
			result = empWorkManagementService.queryWorkRegistByCondition(empId, month);
		}catch(Exception e){
			logger.error("queryWorkRegistInfo:",e);
		}
		return result;
	}
	
	//工作管理-远程工作登记-提交 
	@RequestMapping("/saveMapWorkRegister.htm")
	@ResponseBody
	public Map<String,Object> saveMapWorkRegister(HttpServletRequest request,String info) throws OaException{
		
		Map<String,Object> result=new HashMap<String,Object>();
		
		try{
			empWorkManagementService.saveMapStr(info);
			result.put("success", true);
			result.put("message", "保存成功");
		}catch(Exception e){
			result.put("success", false);
			result.put("message", e.getMessage());
		}
		return result;
	}
	
	//工作管理-远程工作登记-判断日期是否能登记
	@RequestMapping("/isWorkDate.htm")
	@ResponseBody
	public Map<String,Object> isWorkDate(String date,Long employeeId) throws OaException{
		
		Map<String,Object> result=new HashMap<String,Object>();
		
		try{
			result = empWorkManagementService.isWorkDate(date,employeeId);
		}catch(Exception e){
			result.put("success", false);
			result.put("message", e.getMessage());
			}
			return result;
		}
}
