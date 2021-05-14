package com.ule.oa.web.controller;

import javax.annotation.Resource;

import org.junit.Test;

import com.ule.oa.BaseControllerTest;
import com.ule.oa.base.po.EmpContract;
import com.ule.oa.base.service.EmpContractService;
import com.ule.oa.common.utils.DateUtils;

public class EmpContractControllerTest extends BaseControllerTest {
	
	@Resource
	private EmpContractService empContractService;

 	//@Test
	public void testSave() {
		EmpContract empContract = new EmpContract();
		empContract.setCompanyId(1L);
		empContract.setEmployeeId(1L);
		empContract.setContractCode("4444");
		empContract.setContractDescription("描述");
		empContract.setContractEndTime(DateUtils.getToday());
		empContract.setContractPeriod(5.3);
		empContract.setContractSignedDate(DateUtils.getToday());
		empContract.setContractStartTime(DateUtils.getToday());
		empContract.setContractPeriod(2.3);
		empContract.setIsActive(0);
		empContract.setRemark("remark");
		empContractService.save(empContract);
	}
	
	@Test
	public void testUpdate() throws Exception {
		EmpContract empContract = new EmpContract();
		empContract.setId(5L);
		empContract.setCompanyId(2L);
		empContract.setEmployeeId(2L);
		empContract.setContractCode("44441");
		empContract.setContractDescription("描述1");
		empContract.setContractEndTime(DateUtils.getToday());
		empContract.setContractPeriod(5.3);
		empContract.setContractSignedDate(DateUtils.getToday());
		empContract.setContractStartTime(DateUtils.getToday());
		empContract.setContractPeriod(2.3);
		empContract.setIsActive(0);
		empContract.setRemark("remark1");
		empContract.setVersion(0L);
		empContractService.updateById(empContract);
	}
}
