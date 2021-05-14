package com.ule.oa.web.controller;

import javax.annotation.Resource;

import org.junit.Test;

import com.ule.oa.BaseControllerTest;
import com.ule.oa.base.po.EmpAchievement;
import com.ule.oa.base.service.EmpAchievementService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.DateUtils;

public class EmpAchievementControllerTest  extends BaseControllerTest{

	@Resource
	private EmpAchievementService empAchievementService;
	
	public void testSave(){
		
		EmpAchievement empAchievement = new EmpAchievement();
		empAchievement.setEmployeeId(1L);
		empAchievement.setProcessTime(DateUtils.getToday());
		empAchievement.setContent("内容");
		empAchievement.setRemark("remark");
		
		empAchievementService.save(empAchievement);
	}
	
	@Test
	public void testUpdate() throws OaException{
		
		EmpAchievement empAchievement = new EmpAchievement();
		empAchievement.setId(3L);
		empAchievement.setEmployeeId(2L);
		empAchievement.setProcessTime(DateUtils.getToday());
		empAchievement.setContent("内容3");
		empAchievement.setRemark("remark3");
		empAchievement.setVersion(0L);
		
		empAchievementService.updateById(empAchievement);
	}
}
