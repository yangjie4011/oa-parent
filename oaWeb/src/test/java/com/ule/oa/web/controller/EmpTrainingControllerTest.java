package com.ule.oa.web.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ule.oa.BaseControllerTest;
import com.ule.oa.base.po.EmpTraining;
import com.ule.oa.base.service.EmpTrainingService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.DateUtils;

public class EmpTrainingControllerTest extends BaseControllerTest {
	@Autowired
	private EmpTrainingService empTrainingService;
	
//	@Test
	public void testSave() {
		EmpTraining train = new EmpTraining();
		train.setEmployeeId(16L);
		train.setIsCompanyTraining(0);
		train.setStartTime(DateUtils.parse("2016-01-01 00:00:00"));
		train.setEndTime(DateUtils.parse("2016-03-31 23:59:59"));
		train.setTrainingInstitutions("英孚教育");
		train.setTrainingProName("计算机英语");
		train.setContent("培训内容是什么呢？");
		train.setObtainCertificate("java");
		train.setRemark("我是备注");
		
		empTrainingService.save(train);
	}

	@Test
	public void testUpdateById() {
		EmpTraining train = new EmpTraining();
		train.setId(6L);
		train.setEmployeeId(16L);
		train.setIsCompanyTraining(1);
		train.setStartTime(DateUtils.parse("2016-02-01 00:00:00"));
		train.setEndTime(DateUtils.parse("2016-04-31 23:59:59"));
		train.setTrainingInstitutions("英孚教育002");
		train.setTrainingProName("计算机英语002");
		train.setContent("培训内容是什么呢？002");
		train.setObtainCertificate("java002");
		train.setRemark("我是备注002");
		train.setVersion(1L);
		
		try {
			empTrainingService.updateById(train);
		} catch (OaException e) {

		}
	}

}
