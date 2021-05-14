package com.ule.oa.web.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ule.oa.BaseControllerTest;
import com.ule.oa.base.po.EmpUrgentContact;
import com.ule.oa.base.service.EmpUrgentContactService;
import com.ule.oa.common.exception.OaException;

public class EmpUrgentContactControllerTest extends BaseControllerTest {
	@Autowired
	private EmpUrgentContactService empUrgentContactService;
	
//	@Test
	public void testSave() {
		EmpUrgentContact empUrgentContact = new EmpUrgentContact();
		empUrgentContact.setEmployeeId(16L);
		empUrgentContact.setPriority(1);
		empUrgentContact.setShortName("王爷");
		empUrgentContact.setName("王上");
		empUrgentContact.setAge(20);
		empUrgentContact.setSex(1);
		empUrgentContact.setCompanyName("大明王朝2");
		empUrgentContact.setMobile("135792468102");
		empUrgentContact.setTelphone("0713-34621892");
		
		empUrgentContactService.save(empUrgentContact);
	}

	@Test
	public void testUpdateById() {
		EmpUrgentContact empUrgentContact = new EmpUrgentContact();
		empUrgentContact.setId(6L);
		empUrgentContact.setEmployeeId(16L);
		empUrgentContact.setPriority(2);
		empUrgentContact.setShortName("王爷2");
		empUrgentContact.setName("王上2");
		empUrgentContact.setAge(22);
		empUrgentContact.setSex(2);
		empUrgentContact.setCompanyName("大明王朝2");
		empUrgentContact.setMobile("135792468102");
		empUrgentContact.setTelphone("0713-34621892");
		empUrgentContact.setVersion(1L);
		
		try {
			empUrgentContactService.updateById(empUrgentContact);
		} catch (OaException e) {

		}
	}

}
