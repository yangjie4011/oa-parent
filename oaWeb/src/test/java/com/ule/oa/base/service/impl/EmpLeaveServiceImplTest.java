package com.ule.oa.base.service.impl;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ule.oa.base.po.EmpLeave;
import com.ule.oa.base.service.EmpLeaveService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.web.service.impl.BaseServiceTest;

public class EmpLeaveServiceImplTest extends BaseServiceTest{
	@Autowired
	private EmpLeaveService empLeaveService;
	
	/**
	  * testCalculationOurAge(自动计算司龄，实际年假，实际病假)
	  * @Title: testCalculationOurAge
	  * @Description: 自动计算司龄，实际年假，实际病假
	  * void    返回类型
	  * @throws
	 */
	@Ignore
	@Test
	public void testCalculationOurAge(){
		empLeaveService.calculationOurAge();
	}
	
	/**
	  * testCalculationYearLeave(自动计实际年假)
	  * @Title: testCalculationYearLeave
	  * @Description: 
	  * void    返回类型
	  * @throws
	 */
	@Ignore
	@Test
	public void testCalculationYearLeave() {
		empLeaveService.calculationYearLeave();
	}
	
	/**
	  * testCalculationYearLeaveByEmpId(根据员工id重新计算年假)
	  * @Title: testCalculationYearLeaveByEmpId
	  * @Description: 根据员工id重新计算年假
	  * void    返回类型
	  * @throws
	 */
	@Ignore
	@Test
	public void testCalculationYearLeaveByEmpId(){
		empLeaveService.calculationYearLeaveByEmpId(1809L);
	}
	
	/**
	  * testCalculationSickLeaveByEmpId(根据员工id计算员工病假)
	  * @Title: testCalculationSickLeaveByEmpId
	  * @Description: 根据员工id计算员工病假
	  * void    返回类型
	  * @throws
	 */
	@Ignore
	@Test
	public void testCalculationSickLeaveByEmpId(){
		empLeaveService.calculationSickLeaveByEmpId(1674L);
	}

	/**
	  * testCalculationSickLeaveByEmp(自动计实际病假)
	  * @Title: testCalculationSickLeaveByEmp
	  * @Description: 自动计实际病假
	  * void    返回类型
	  * @throws
	 */
	@Ignore
	@Test
	public void testCalculationSickLeaveByEmp() {
		empLeaveService.calculationSickLeave();
	}
	
	
	/**
	  * testInitNextYearLeave(自动计算下一年度的年假)
	  * @Title: testInitNextYearLeave
	  * @Description: 自动计算下一年度的年假
	  * void    返回类型
	  * @throws
	 */
	@Ignore
	@Test
	public void testInitNextYearLeave(){
		empLeaveService.initNextYearLeave();
	}
	
	/**
	  * testInitNextSickLeave(自动计算下一年度的病假)
	  * @Title: testInitNextSickLeave
	  * @Description: 自动计算下一年度的病假
	  * void    返回类型
	  * @throws
	 */
	@Ignore
	@Test
	public void testInitNextSickLeave(){
		empLeaveService.initNextSickLeave();
	}
	
//	/**
//	 * @throws OaException 
//	  * testGenerateDefaultYearLeave(入职登记生成默认的年假)
//	  * @Title: testGenerateDefaultYearLeave
//	  * @Description: 入职登记生成默认的年假
//	  * void    返回类型
//	  * @throws
//	 */
//	@Ignore
//	@Test
//	public void testGenerateDefaultYearLeave() throws OaException{
//		empLeaveService.generateDefaultYearLeave(1L,1L,2017);
//	}
//	
//	/**
//	 * @throws OaException 
//	  * testGenerateDefaultSickLeave(入职登记生成默认的病假)
//	  * @Title: testGenerateDefaultSickLeave
//	  * @Description: 入职登记生成默认的病假
//	  * void    返回类型
//	  * @throws
//	 */
//	@Ignore
//	@Test
//	public void testGenerateDefaultSickLeave() throws OaException{
//		empLeaveService.generateDefaultSickLeave(1L,1L,2017);
//	}
	
	/**
	  * testCheckLeaveIsValidate(验证假期有效性)
	  * @Title: testCheckLeaveIsValidate
	  * @Description: 验证假期有效性
	  * @throws OaException    设定文件
	  * void    返回类型
	  * @throws
	 */
	@Ignore
	@Test
	public void testCheckLeaveIsValidate() throws OaException{
		EmpLeave planleave = new EmpLeave();
		planleave.setEmployeeId(1674L);
		planleave.setType(ConfigConstants.LEAVE_TYPE_1);//假期类型
		planleave.setYear(2017);//假期年份
		planleave.setPlanStartTime(DateUtils.parse("2017-12-31 00:00:00"));//请假开始时间
		planleave.setPlanEndTime(DateUtils.parse("2018-01-01 00:00:00"));//请假结束时间
		planleave.setPlanDays(2.0);//预计请假天数
		
		empLeaveService.checkLeaveIsValidate(planleave);
	}
	
	/**
	  * testRepairLeaveDatas(多条福利基数合并成一条)
	  * @Title: testRepairLeaveDatas
	  * @Description: 多条福利基数合并成一条
	  * void    返回类型
	  * @throws
	 */
	@Ignore
	@Test
	public void testRepairLeaveDatas(){
		empLeaveService.repairLeaveDatas(null);
	}
	
	/**
	  * testSplitYearLeave(这里用一句话描述这个方法的作用)
	  * @Title: testSplitYearLeave
	  * @Description: 拆分假期
	  * @throws OaException    设定文件
	  * void    返回类型
	  * @throws
	 */
	@Ignore
	@Test
	public void testSplitYearLeave() throws Exception{
		EmpLeave planleave = new EmpLeave();
		planleave.setEmployeeId(1674L);
		planleave.setType(ConfigConstants.LEAVE_TYPE_1);//假期类型
		planleave.setYear(2017);//假期年份
		planleave.setPlanStartTime(DateUtils.parse("2017-12-25 09:00:00"));//请假开始时间
		planleave.setPlanEndTime(DateUtils.parse("2018-01-02 14:00:00"));//请假结束时间
		planleave.setPlanDays(5.5);//预计请假天数
		
		empLeaveService.splitLeave(planleave);
	}
}
