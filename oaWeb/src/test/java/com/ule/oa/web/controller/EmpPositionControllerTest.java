package com.ule.oa.web.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ule.oa.BaseControllerTest;
import com.ule.oa.base.po.EmpContract;
import com.ule.oa.base.po.EmpDepart;
import com.ule.oa.base.po.EmployeeApp;
import com.ule.oa.base.po.Position;
import com.ule.oa.base.service.EmpPositionService;
import com.ule.oa.common.utils.DateUtils;

public class EmpPositionControllerTest extends BaseControllerTest{
	@Autowired
	private EmpPositionService empPositionService;
	
	/**
	  * testUpdateEmpPositionInfo(员工在职信息测试)
	  * @Title: testUpdateEmpPositionInfo
	  * @Description: 员工在职信息测试
	  * @throws Exception    设定文件
	  * void    返回类型
	  * @throws
	 */
	@Test
	public void testUpdateEmpPositionInfo() throws Exception {
		//员工部门
		EmpDepart ed = new EmpDepart();
		//职位
		Position po = new Position();
		
		EmployeeApp employee = new EmployeeApp();
		employee.setCode("sp20170623002");
		employee.setWorkType(2L);
		employee.setEmpTypeId(2L);
		employee.setOurAge(5.0);
		
		ed.setDepartId(2L);
		employee.setEmpDepart(ed);
//		employee.setLeader(22L);
		
		po.setPositionLevelId(2L);
		po.setPositionSeqId(3L);
		employee.setPosition(po);
		
		employee.setFirstEntryTime(DateUtils.parse("2016-02-01 00:00:00"));
		
		
		
		
		
		
		empPositionService.updateEmpPositionInfo(employee);
	}

}
