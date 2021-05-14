package com.ule.oa.attendance.service.impl;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ule.oa.base.service.EmployeeClassService;
import com.ule.oa.base.service.impl.BaseServiceImpl;

public class EmployeeClassServiceImplTest extends BaseServiceImpl{
	@Autowired
	private EmployeeClassService employeeClassService;
	
	@Test
	public void testMustArrangeOfWorkMind() throws Exception {
		employeeClassService.mustArrangeOfWorkMind();
	}

}
