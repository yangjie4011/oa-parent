package com.ule.oa.web.controller;

import javax.annotation.Resource;

import org.junit.Test;

import com.ule.oa.BaseControllerTest;
import com.ule.oa.base.po.EmpPostRecord;
import com.ule.oa.base.service.EmpPostRecordService;
import com.ule.oa.common.utils.DateUtils;

public class EmpPostRecordControllerTest extends BaseControllerTest {
	
	@Resource
	private EmpPostRecordService empPostRecordService;
	
	//@Test
	public void testSave() {
		EmpPostRecord empPostRecord = new EmpPostRecord();
		empPostRecord.setAdjustDate(DateUtils.getToday());
		empPostRecord.setDepartId(1L);
		empPostRecord.setEffectiveDate(DateUtils.getToday());
		empPostRecord.setEmployeeId(1L);
		empPostRecord.setIsCurrentPosition(1);
		empPostRecord.setPositionId(1L);
		empPostRecord.setPreDepartId(2L);
		empPostRecord.setRemark("remark");
		
		empPostRecordService.save(empPostRecord);
	}
	
	@Test
	public void testUpdate() throws Exception {
		EmpPostRecord empPostRecord = new EmpPostRecord();
		empPostRecord.setId(3L);
		empPostRecord.setAdjustDate(DateUtils.getToday());
		empPostRecord.setDepartId(2L);
		empPostRecord.setEffectiveDate(DateUtils.getToday());
		empPostRecord.setEmployeeId(2L);
		empPostRecord.setIsCurrentPosition(2);
		empPostRecord.setPositionId(2L);
		empPostRecord.setPreDepartId(3L);
		empPostRecord.setRemark("remark1");
		empPostRecord.setPrePositionId(5L);
		empPostRecord.setVersion(1L);
		
		empPostRecordService.updateById(empPostRecord);
	}
}
