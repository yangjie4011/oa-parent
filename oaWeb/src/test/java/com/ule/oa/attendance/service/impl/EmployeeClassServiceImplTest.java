package com.ule.oa.attendance.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.service.EmployeeClassService;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.web.service.impl.BaseServiceTest;

public class EmployeeClassServiceImplTest extends BaseServiceTest{
	@Autowired
	private EmployeeClassService employeeClassService;
	
	/**
	  * testGetEmployeeClassInfoByDepartId(这里用一句话描述这个方法的作用)
	  * @Title: testGetEmployeeClassInfoByDepartId
	  * @Description: 获取指定部门排班情况（需要排班人数，已经排班人数）
	  * void    返回类型
	  * @throws
	 */
	@Ignore
	@Test
	public void testGetEmployeeClassInfoByDepartId() {
		try {
			List<EmployeeClass> classList = employeeClassService.getEmployeeClassInfoByDepartId(154L);
			
			for(EmployeeClass clazz : classList){
				System.out.println("需要排班人数" + "\t" + "已经排班人数" + "\t" + "排班人");
				System.out.println(clazz.getMustClassSettingCount() + "\t" + clazz.getAlreadyClassSettingCount() + "\t" + clazz.getClassSettingPerson());
			}
		} catch (Exception e) {

		}
	}

	/**
	  * testSave(逐单保存员工排班信息)
	  * @Title: testSave
	  * @Description: 逐单保存员工排班信息
	  * void    返回类型
	  * @throws
	 */
	@Ignore
	@Test
	public void testSave(){
		EmployeeClass employeeClass = new EmployeeClass();
		employeeClass.setCompanyId(3L);
		employeeClass.setEmployId(123L);
		employeeClass.setEmployName("张三");
		employeeClass.setClassDate(DateUtils.parse("2017-08-02 00:00:00"));
		employeeClass.setClassSettingId(1L);
		employeeClass.setClassSettingCode("abc");
		employeeClass.setClassSettingPerson("小张");
		
		employeeClassService.save(employeeClass);
	}
	
	/**
	  * testBatchSave(批量保存员工排班信息)
	  * @Title: testBatchSave
	  * @Description: 批量保存员工排班信息
	  * void    返回类型
	  * @throws
	 */
	@Test
	public void testBatchSave(){
		List<EmployeeClass> list = new ArrayList<EmployeeClass>();
		
		EmployeeClass employeeClass = new EmployeeClass();
		employeeClass.setCompanyId(3L);
		employeeClass.setEmployId(123L);
		employeeClass.setEmployName("张三");
		employeeClass.setClassDate(DateUtils.parse("2017-08-01 00:00:00"));
		employeeClass.setClassSettingId(1L);
		employeeClass.setClassSettingCode("abc");
		employeeClass.setClassSettingPerson("小张");
		list.add(employeeClass);
		
		employeeClass = new EmployeeClass();
		employeeClass.setCompanyId(3L);
		employeeClass.setEmployId(123L);
		employeeClass.setEmployName("张三");
		employeeClass.setClassDate(DateUtils.parse("2017-08-02 00:00:00"));
		employeeClass.setClassSettingId(1L);
		employeeClass.setClassSettingCode("abc");
		employeeClass.setClassSettingPerson("小张");
		
		list.add(employeeClass);
		
		employeeClassService.batchSave(list);
	}
}
