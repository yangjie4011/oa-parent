package com.ule.oa.base.service.impl;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ule.oa.base.service.EmpQuitRecordService;
import com.ule.oa.common.exception.OaException;

/**
  * @ClassName: EmpQuitRecordServiceImplTest
  * @Description: 员工离职记录单元测试
  * @author minsheng
  * @date 2017年7月27日 下午4:35:05
 */
public class EmpQuitRecordServiceImplTest extends BaseServiceImpl{
	@Autowired
	private EmpQuitRecordService empQuitRecordService;
	
	/**
	  * testEmpJobStatusDoc(离职员工归档)
	  * @Title: testEmpJobStatusDoc
	  * @Description: 离职员工归档
	  * void    返回类型
	  * @throws
	 */
	@Test
	public void testEmpJobStatusDoc() {
		try {
			empQuitRecordService.empJobStatusDoc();
		} catch (OaException e) {

		}
	}

}
