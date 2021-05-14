package com.ule.oa.attendance.service.impl;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ule.oa.base.service.ApplicationEmployeeClassService;
import com.ule.oa.web.service.impl.BaseServiceTest;

/**
  * @ClassName: ApplicationEmployeeClassServiceImplTest
  * @Description: 员工排班单元测试
  * @author minsheng
  * @date 2017年11月7日 上午9:43:02
 */
public class ApplicationEmployeeClassServiceImplTest extends BaseServiceTest{
	@Autowired
	private ApplicationEmployeeClassService applicationEmployeeClassService;
	
	/**
	  * testExportEmpClassReprotById(排班查询报表导出)
	  * @Title: testExportEmpClassReprotById
	  * @Description: 排班查询报表导出
	  * void    返回类型
	  * @throws
	 */
	@Test
	public void testExportEmpClassReprotById() {
		try {
			applicationEmployeeClassService.exportEmpClassReprotById(1L);
		} catch (Exception e) {

		};
	}

}
