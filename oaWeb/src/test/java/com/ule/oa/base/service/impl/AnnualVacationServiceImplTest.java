package com.ule.oa.base.service.impl;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ule.oa.base.po.AnnualVacation;
import com.ule.oa.base.service.AnnualVacationService;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.web.service.impl.BaseServiceTest;

/**
  * @ClassName: AnnualVacationServiceImplTest
  * @Description: 法定节假日信息单元测试类
  * @author minsheng
  * @date 2017年8月3日 上午9:24:08
 */
public class AnnualVacationServiceImplTest extends BaseServiceTest{
	@Autowired
	private AnnualVacationService annualVacationService;

	@Test
	public void testGetCurrVacttionBySub() {
		AnnualVacation vacation = new AnnualVacation();
		vacation.setSubject("春节");
		vacation.setType(3);
		
		List<AnnualVacation> list = annualVacationService.getCurrVacttionBySubAndType(vacation);
		System.out.println("================根据指定节假日名称获取当年的节假日信息================");
		System.out.println("假期日期" + "\t" + "假期类型" + "\t" + "假期名称" + "\t" + "假期描述");
		for(AnnualVacation va : list){
			System.out.println(DateUtils.format(va.getAnnualDate(),DateUtils.FORMAT_SHORT) + "\t" + va.getType() + "\t" + va.getSubject() + "\t" + va.getContent());
		}
	}

}
