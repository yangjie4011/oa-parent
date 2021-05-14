package com.ule.oa.web.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ule.oa.BaseControllerTest;
import com.ule.oa.base.po.EmpFamilyMember;
import com.ule.oa.base.service.EmpFamilyMemberService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.DateUtils;

public class EmpFamilyMemberControllerTest extends BaseControllerTest {
	@Autowired
	private EmpFamilyMemberService empFamilyMemberService;
	
//	@Test
	public void testSave() {
		EmpFamilyMember empFamilyMember = new EmpFamilyMember();
		empFamilyMember.setEmployeeId(16L);
		empFamilyMember.setMemberName("黄后");
		empFamilyMember.setMemberAge(25);
		empFamilyMember.setMemberSex(1);
		empFamilyMember.setRelation(1);
		empFamilyMember.setMemberCompanyName("大秦帝国");
		empFamilyMember.setMemberMobile("13579246810");
		empFamilyMember.setMemberTelphone("0713-2468139");
		empFamilyMember.setBirthday(DateUtils.parse("2016-01-01 15:30:30"));
		
		empFamilyMemberService.save(empFamilyMember);
	}

	@Test
	public void testUpdateById() {
		EmpFamilyMember empFamilyMember = new EmpFamilyMember();
		empFamilyMember.setId(10L);
		empFamilyMember.setEmployeeId(16L);
		empFamilyMember.setMemberName("黄后002");
		empFamilyMember.setMemberAge(26);
		empFamilyMember.setMemberSex(2);
		empFamilyMember.setRelation(2);
		empFamilyMember.setMemberCompanyName("大秦帝国002");
		empFamilyMember.setMemberMobile("135792468102");
		empFamilyMember.setMemberTelphone("0713-24681392");
		empFamilyMember.setBirthday(DateUtils.parse("2016-01-01 15:30:32"));
		empFamilyMember.setVersion(0L);
		
		try {
			empFamilyMemberService.updateById(empFamilyMember);
		} catch (OaException e) {

		}
	}

}
