package com.ule.oa.web.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ule.oa.base.po.EmpFamilyMember;
import com.ule.oa.base.po.EmpSchool;
import com.ule.oa.base.po.EmpTraining;
import com.ule.oa.base.po.EmpUrgentContact;
import com.ule.oa.base.po.EmpWorkRecord;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.service.EmpApplicationService;

@Controller
public class EmpApplicationController {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private EmpApplicationService empApplicationService;
	
	@RequestMapping("employeeApplication/index.htm")
	public String index(HttpServletRequest request){
		return "base/employee/employee_application";
	}
	
	@RequestMapping("employeeApplication/check.htm")
	public String check(HttpServletRequest request){
		return "base/employee/employee_application_check";
	}
	
	@RequestMapping(value="employeeApplication/apply.htm",method=RequestMethod.POST)
	@ResponseBody
	public String apply(Employee employee){
		//判断是否存在待审核的数据
		Integer isExistApply = 0;
		isExistApply = empApplicationService.checkExistApply(employee);
		if(isExistApply.intValue() == 0){
			empApplicationService.apply(employee);
			return "{\"response\":\"申请成功，等待审核\"}";
		}else{
			return "{\"response\":\"申请失败，已存在待审核数据\"}";	
		}
	}
	
	@RequestMapping(value="employeeApplication/pass.htm",method=RequestMethod.POST)
	@ResponseBody
	public String pass(Employee employee) throws Exception{
		//判断是否存在待审核的数据
		Integer isExistApply = 0;
		isExistApply = empApplicationService.checkExistApply(employee);
		if(isExistApply != 0){
			empApplicationService.pass(employee);
			return "{\"response\":\"审核已通过\"}";
		}else{
			return "{\"response\":\"审核失败，不存在存在待审核数据\"}";	
		}
	}
	
	@RequestMapping(value="employeeApplication/reject.htm",method=RequestMethod.POST)
	@ResponseBody
	public String reject(Employee employee){
		//判断是否存在待审核的数据
		Integer isExistApply = 0;
		isExistApply = empApplicationService.checkExistApply(employee);
		if(isExistApply != 0){
			empApplicationService.reject(employee);
			return "{\"response\":\"审核已驳回\"}";
		}else{
			return "{\"response\":\"驳回失败，不存在存在待审核数据\"}";	
		}
	}
	
	@RequestMapping(value="employeeApplication/checkEmployeeMain.htm",method=RequestMethod.POST)
	@ResponseBody
	public Employee checkEmployeeMain(Employee employee){
		Employee employeeRs = empApplicationService.checkEmployeeMain(employee);
		return employeeRs;
	}
	
	@RequestMapping(value="employeeApplication/checkEmployeeFamily.htm",method=RequestMethod.POST)
	@ResponseBody
	public List<EmpFamilyMember> checkEmployeeFamily(Employee employee){
		List<EmpFamilyMember> employeeFamilys = empApplicationService.checkEmployeeFamily(employee);
		return employeeFamilys;
	}
	
	@RequestMapping(value="employeeApplication/checkEmployeeUrgent.htm",method=RequestMethod.POST)
	@ResponseBody
	public List<EmpUrgentContact> checkEmployeeUrgent(Employee employee){
		List<EmpUrgentContact> empUrgentContacts = empApplicationService.checkEmployeeUrgent(employee);
		return empUrgentContacts;
	}
	
	@RequestMapping(value="employeeApplication/checkEmployeeSchool.htm",method=RequestMethod.POST)
	@ResponseBody
	public List<EmpSchool> checkEmployeeSchool(Employee employee){
		logger.info("list{}",employee.getEmpWorkRecords());
		List<EmpSchool> empSchoolRs = empApplicationService.checkEmployeeSchool(employee);
		return empSchoolRs;
	}
	
	@RequestMapping(value="employeeApplication/checkEmployeeTraining.htm",method=RequestMethod.POST)
	@ResponseBody
	public List<EmpTraining> checkEmployeeTraining(Employee employee){
		List<EmpTraining> empTrainings = empApplicationService.checkEmployeeTraining(employee);
		return empTrainings;
	}
	
	@RequestMapping(value="employeeApplication/checkEmployeeWork.htm",method=RequestMethod.POST)
	@ResponseBody
	public List<EmpWorkRecord> checkEmployeeWork(Employee employee){
		List<EmpWorkRecord> empWorkRecords = empApplicationService.checkEmployeeWork(employee);
		return empWorkRecords;
	}

}
