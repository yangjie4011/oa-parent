package com.ule.oa.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ule.oa.base.po.ApplicationEmployeeClassDetail;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeApp;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.service.ApplicationEmployeeClassDetailService;
import com.ule.oa.base.service.EmpPositionService;
import com.ule.oa.base.service.EmployeeClassService;
import com.ule.oa.base.service.EmployeeService;

@Controller
@RequestMapping("empPosition")
public class EmpPositionController {
	@Autowired
	private EmpPositionService empPositionService;
	
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private ApplicationEmployeeClassDetailService applicationEmployeeClassDetailService;
	@Autowired
	private EmployeeClassService employeeClassService;
	/**
	  * updateEmpPositionInfo(更新-在职信息)
	  * @Title: updateEmpPositionInfo
	  * @Description: 更新员工在职信息
	  * @param employee
	  * @return
	  * @throws Exception    设定文件
	  * boolean    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/updateEmpPositionInfo.htm")
	public ModelMap updateEmpPositionInfo(EmployeeApp employee)throws Exception{
		ModelMap map = new ModelMap(); 
		map.put("success", "true");
		try{
			Employee condition = new Employee();
			condition.setCode(employee.getCode());
			condition.setDelFlag(0);
			List<Employee> empList = null;//判断编号是否已经存在
			empList = employeeService.getListByCondition(condition);
			Integer codeCount = empList.size();
			if(codeCount > 1){
				map.put("success", "false");
				map.put("result", "修改失败，员工编号已存在！");
			}else if(codeCount == 1){
				condition = empList.get(0);
				if(employee.getId().equals(condition.getId())){
					empPositionService.updateEmpPositionInfo(employee);
					map.put("result", "修改成功！");
					
				}else{
					map.put("success", "false");
					map.put("result", "修改失败，员工编号已存在！");
				}
			}else{
				empPositionService.updateEmpPositionInfo(employee);
				map.put("result", "修改成功！");
			}
			try{
				if(employee.getQuitTime()!=null){
					ApplicationEmployeeClassDetail classDetail = new ApplicationEmployeeClassDetail();
					classDetail.setEmployId(employee.getId());
					classDetail.setClassDate(employee.getQuitTime());
					applicationEmployeeClassDetailService.deleteByQuitTime(classDetail);
					EmployeeClass employeeClass = new EmployeeClass();
					employeeClass.setEmployId(employee.getId());
					employeeClass.setClassDate(employee.getQuitTime());
					employeeClassService.deleteByQuitTime(employeeClass);
				}
			}catch(Exception e1){
				
			}
		}catch (Exception e){
			map.put("success", "false");
			map.put("msg", e.getMessage());
			map.put("result", "修改失败！");
		}
		return map;	
	}
}
