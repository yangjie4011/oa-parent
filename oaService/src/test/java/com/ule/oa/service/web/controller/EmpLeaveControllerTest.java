package com.ule.oa.service.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ule.oa.base.po.EmpLeave;
import com.ule.oa.base.service.EmpLeaveService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.http.HttpUtils;
import com.ule.oa.common.utils.json.JSONUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = {"classpath:applicationContext.xml","classpath:spring-dataSource.xml"})
public class EmpLeaveControllerTest {
	@Autowired
	private EmpLeaveService empLeaveService;

	/**
	  * testUpdateEmpLeaveApply(提交请假申请调用的接口)
	  * @Title: testUpdateEmpLeaveApply
	  * @Description: 提交请假申请调用的接口
	  * void    返回类型
	  * @throws
	 */
	@Ignore
	@Test
	public void testUpdateEmpLeaveApply(){
		List<EmpLeave> empLeaves = new ArrayList<EmpLeave>();
		
		EmpLeave empLeave = new EmpLeave();
		empLeave.setEmployeeId(1716L);
		empLeave.setType(ConfigConstants.LEAVE_TYPE_5);//假期类型
		empLeave.setPlanStartTime(DateUtils.parse("2017-12-26 09:00:00"));
		empLeave.setPlanEndTime(DateUtils.parse("2018-01-10 18:00:00"));//请假结束时间
		empLeave.setPlanDays(88.0);//预计请假天数
		empLeave.setOptUser("闵胜");
		empLeaves.add(empLeave);
		
		Map<String,String> paramMap = new HashMap<String,String>();
		paramMap.put("data", JSONUtils.write(empLeaves));
		
		try {
			String response = HttpUtils.sendByPost("http://127.0.0.1:8082/oaService/empLeave/updateEmpLeaveApply.htm", paramMap,false);
			System.out.println(response);
		} catch (OaException e) {

		}
	}
	
	/**
	  * testUpdateEmpLeaveAudit(审批通过后调用的接口)
	  * @Title: testUpdateEmpLeaveAudit
	  * @Description: 审批通过后调用的接口
	  * void    返回类型
	  * @throws
	 */
	@Ignore
	@Test
	public void testUpdateEmpLeaveAudit(){
		List<EmpLeave> empLeaves = new ArrayList<EmpLeave>();
		
		EmpLeave empLeave = new EmpLeave(); 
		empLeave.setCompanyId(5L);
		empLeave.setEmployeeId(1716L);
		empLeave.setType(ConfigConstants.LEAVE_TYPE_5);
		empLeave.setPlanDays(32.0);
		empLeave.setOptUser("api");
		empLeave.setApplyStatus(false);
		empLeave.setPlanStartTime(DateUtils.parse("2017-12-26 09:00:00"));
		empLeave.setPlanEndTime(DateUtils.parse("2018-01-02 18:00:00"));//请假结束时间
		empLeaves.add(empLeave);
		
		Map<String,String> paramMap = new HashMap<String,String>();
		paramMap.put("data", JSONUtils.write(empLeaves));
		
		try {
			String response = HttpUtils.sendByPost("http://127.0.0.1:8082/oaService/empLeave/updateEmpLeaveAudit.htm", paramMap,false);
			System.out.println(response);
		} catch (OaException e) {

		}
	}

}
