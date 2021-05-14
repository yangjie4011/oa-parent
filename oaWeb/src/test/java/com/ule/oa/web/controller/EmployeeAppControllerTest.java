package com.ule.oa.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ule.oa.BaseControllerTest;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeApp;
import com.ule.oa.base.service.EmployeeAppService;
import com.ule.oa.base.service.EmployeeService;


public class EmployeeAppControllerTest extends BaseControllerTest{
	@Autowired
	private EmployeeAppService employeeAppService;
	@Autowired
	private EmployeeService employeeService;
	
	/**
	  * testUpdateEmpBaseInfo(编辑员工基本信息)
	  * @Title: testUpdateEmpBaseInfo
	  * @Description: 编辑员工基本信息
	  * void    返回类型
	  * @throws
	 */
//	@Test
	public void testUpdateEmpBaseInfo() {
		try{
			EmployeeApp employee = new EmployeeApp();
			employee.setCnName("小王子");
			employee.setEngName("王三");
			employee.setBirthday(new Date());
			employee.setAge(1);
			employee.setSex(1);
			employee.setCountry(1);
			employee.setNation(1L);
			employee.setHouseholdRegister("中国湖北");
			employee.setMaritalStatus(1L);
			employee.setPoliticalStatus(1L);
			employee.setPositionTitle("高级开发工程师");
			employee.setDegreeOfEducation(1L);
			employee.setAddress("适当放松放松的方式的方式");
			employee.setId(16L);
			employee.setVersion(1L);
			
			employeeAppService.updateEmpBaseInfo(employee);
		}catch(Exception e){

		}
	}
	
	@Test
	public void list(){
		Employee employeeCondition = new Employee();
		employeeCondition.setJobStatus(0);//0：在职，1：离职
		employeeCondition.setQuitTime(new Date());//
		employeeCondition.setId(555555L);
  	    employeeCondition.setLimit(100);
		List<Employee> empList = new ArrayList<Employee>();
		int i=0;
        while (i==0 || !empList.isEmpty()) {//list能查到数据，继续往后查,直到查不到
	      	  employeeCondition.setOffset(i*100);
        	  empList = employeeService.getAttnEmpListByCondition(employeeCondition);
        	  System.out.println(empList.size());
        	  i++;
        }
	}
}
