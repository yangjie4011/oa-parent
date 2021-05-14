package com.ule.oa.base.service.impl;

import java.io.IOException;

import javax.mail.MessagingException;

import com.itextpdf.text.DocumentException;
import com.ule.oa.base.service.EmpApplicationBusinessService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.web.service.impl.BaseServiceTest;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
  * @ClassName: EmpApplicationBusinessServiceImplTest
  * @Description: 出差打印单元测试
  * @author minsheng
  * @date 2017年10月24日 下午7:06:46
 */
public class EmpApplicationBusinessServiceImplTest extends BaseServiceTest{
	@Autowired
	private EmpApplicationBusinessService empApplicationBusinessService;

	/**
	 * @throws OaException 
	  * testExportApplyResultByBusinessId(出差申请pdf导出单元测试)
	  * @Title: testExportApplyResultByBusinessId
	  * @Description: 出差申请pdf导出单元测试
	  * void    返回类型
	  * @throws
	 */
//	@Ignore
	@Test
	public void testExportApplyResultByBusinessId() throws DocumentException, IOException, MessagingException, OaException {
		empApplicationBusinessService.exportApplyResultByBusinessId(43L,1660L);
	}

	
	/**
	  * testExportApplyReportByBusinessId(出差总结报告pdf单元测试)
	  * @Title: testExportApplyReportByBusinessId
	  * @Description: 出差总结报告pdf单元测试
	  * void    返回类型
	  * @throws
	 */
	@Ignore
	@Test
	public void testExportApplyReportByBusinessId() {
		empApplicationBusinessService.exportApplyReportByBusinessId(42L,1646L);
	}

}
