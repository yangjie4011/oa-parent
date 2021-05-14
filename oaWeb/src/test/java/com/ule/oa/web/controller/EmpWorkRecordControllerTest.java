package com.ule.oa.web.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ule.oa.BaseControllerTest;
import com.ule.oa.base.po.EmpWorkRecord;
import com.ule.oa.base.service.EmpWorkRecordService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.DateUtils;

public class EmpWorkRecordControllerTest extends BaseControllerTest {
	@Autowired
	private EmpWorkRecordService empWorkRecordService;

//	@Test
	public void testSave() {
		EmpWorkRecord record = new EmpWorkRecord();
		record.setEmployeeId(16L);
		record.setStartTime(DateUtils.parse("2016-01-01 00:00:00"));
		record.setEndTime(DateUtils.parse("2016-02-31 23:59:59"));
		record.setCompanyName("上海邮乐网络科技有限公司");
		record.setPositionName("测试职位");
		record.setPositionTitle("测试职称");
		record.setPositionTask("测试主办业务");
		
		empWorkRecordService.save(record);
	}

	@Test
	public void testUpdateById() {
		EmpWorkRecord record = new EmpWorkRecord();
		record.setId(4L);
		record.setEmployeeId(16L);
		record.setStartTime(DateUtils.parse("2016-02-01 00:00:00"));
		record.setEndTime(DateUtils.parse("2016-03-31 23:59:59"));
		record.setCompanyName("上海邮乐网络科技有限公司002");
		record.setPositionName("测试职位002");
		record.setPositionTitle("测试职称002");
		record.setPositionTask("测试主办业务002");
		record.setVersion(0L);
		
		try {
			empWorkRecordService.updateById(record);
		} catch (OaException e) {
		
		}
	}

}
