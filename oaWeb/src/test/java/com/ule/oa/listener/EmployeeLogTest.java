package com.ule.oa.listener;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.ule.oa.base.po.tbl.EmployeeTbl;
import com.ule.oa.common.listener.LogEvent;
import com.ule.oa.web.service.impl.BaseServiceTest;

/**
  * @ClassName: EmployeeLogTest
  * @Description: 记录日志单元测试
  * @author minsheng
  * @date 2018年1月8日 下午4:05:27
 */
public class EmployeeLogTest extends BaseServiceTest{
	@Autowired
	private ApplicationContext applicationContext;
	
	/**
	  * testSave(请异步调用，不要捕获异常，新增表数据增加日志--这里的参数一定要传tbl，不要传po)
	  * @Title: testSave
	  * @Description: 新增日志---这里的参数一定要传tbl，不要传po
	  * void    返回类型
	  * @throws
	 */
	@Test
	@Ignore
	public void testSave(){
		EmployeeTbl emp = new EmployeeTbl();
		emp.setId(1L);
		emp.setAge(21);
		emp.setCnName("123");
    	
    	applicationContext.publishEvent(new LogEvent(emp,null));
	}
	
	/**
	  * testUpdate(请异步调用，不要捕获异常，修改表数据记录日志---这里的参数一定要传tbl，不要传po)
	  * @Title: testUpdate
	  * @Description: 修改表数据记录日志---这里的参数一定要传tbl，不要传po
	  * void    返回类型
	  * @throws
	 */
	@Test
	@Ignore
	public void testUpdate(){
		EmployeeTbl oldEmp = new EmployeeTbl();
		oldEmp.setId(1L);
		oldEmp.setAge(21);
		oldEmp.setCnName("123");
		
		EmployeeTbl newEmp = new EmployeeTbl();
		newEmp.setId(1L);
		newEmp.setAge(20);
		newEmp.setCnName("456");
    	
    	applicationContext.publishEvent(new LogEvent(oldEmp,newEmp));
	}
}
