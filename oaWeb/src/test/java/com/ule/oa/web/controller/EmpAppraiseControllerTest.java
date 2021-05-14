package com.ule.oa.web.controller;

import javax.annotation.Resource;

import org.junit.Test;

import com.ule.oa.BaseControllerTest;
import com.ule.oa.base.po.EmpAppraise;
import com.ule.oa.base.service.EmpAppraiseService;
import com.ule.oa.common.utils.DateUtils;

public class EmpAppraiseControllerTest extends BaseControllerTest {

	@Resource
	private EmpAppraiseService empAppraiseService;
	
	//@Test
	public void testSave() {
		EmpAppraise empAppraise = new EmpAppraise();
		empAppraise.setAnnualExaminationTime(DateUtils.getToday());
		empAppraise.setConclusion("aaa");
		empAppraise.setEmployeeId(1L);
		empAppraise.setEndTime(DateUtils.getToday());
		empAppraise.setRemark("remark");
		empAppraise.setScore("54");
		empAppraise.setStartTime(DateUtils.getToday());
		empAppraiseService.save(empAppraise);
	}
	
	@Test
	public void testUpdate() throws Exception {
		EmpAppraise empAppraise = new EmpAppraise();
		empAppraise.setId(3L);
		empAppraise.setAnnualExaminationTime(DateUtils.getToday());
		empAppraise.setConclusion("aaa1");
		empAppraise.setEmployeeId(2L);
		empAppraise.setEndTime(DateUtils.getToday());
		empAppraise.setRemark("remark1");
		empAppraise.setScore("541");
		empAppraise.setStartTime(DateUtils.getToday());
		empAppraise.setVersion(0L);
		empAppraiseService.updateById(empAppraise);
	}
}
