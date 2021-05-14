package com.ule.oa.web.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ule.oa.BaseControllerTest;
import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.EmpSchool;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.EmpSchoolService;
import com.ule.oa.common.utils.DateUtils;

public class EmpSchoolControllerTest extends BaseControllerTest{
	@Autowired
	private EmpSchoolService empSchoolService;
	@Autowired
	private ConfigService configService;

//	@Test
	public void testSave() {
		Config config = new Config();
		config.setCode("educationLevel");
		Config conf = configService.getByCondition(config);
		
		EmpSchool empSchool = new EmpSchool();
		empSchool.setEmployeeId(16L);
		empSchool.setStartTime(DateUtils.parse("2010-01-01 00:00:00"));
		empSchool.setEndTime(DateUtils.parse("2010-3-31 23:59:59"));
		empSchool.setSchool("武汉大学");
		empSchool.setEducation(conf.getId().intValue());
		empSchool.setMajor("法制建设");
		empSchool.setDegree(281);
		
		empSchoolService.save(empSchool);
	}

	@Test
	public void testUpdateById() {
		EmpSchool empSchool = new EmpSchool();
		Config config = new Config();
		config.setCode("educationLevel");
		Config conf = configService.getByCondition(config);
		
		empSchool.setId(6L);
		empSchool.setEmployeeId(17L);
		empSchool.setStartTime(DateUtils.parse("2010-02-01 00:00:00"));
		empSchool.setEndTime(DateUtils.parse("2010-5-31 23:59:59"));
		empSchool.setSchool("武汉大学002");
		empSchool.setEducation(conf.getId().intValue()+1);
		empSchool.setMajor("法制建设002");
		empSchool.setDegree(281);
		empSchool.setVersion(0L);
		
		try {
			empSchoolService.updateById(empSchool);
		} catch (Exception e) {

		}
	}

}
