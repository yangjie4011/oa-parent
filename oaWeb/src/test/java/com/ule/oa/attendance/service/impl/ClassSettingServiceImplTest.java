package com.ule.oa.attendance.service.impl;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ule.oa.base.po.ClassSetting;
import com.ule.oa.base.service.ClassSettingService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.web.service.impl.BaseServiceTest;

/**
  * @ClassName: ClassSettingServiceImplTest
  * @Description: 班次设置单元测试类
  * @author minsheng
  * @date 2017年7月31日 下午4:09:07
 */
public class ClassSettingServiceImplTest extends BaseServiceTest{
	@Autowired
	private ClassSettingService classSettingService;
	
	/**
	  * testSave(班次保存)
	  * @Title: testSave
	  * @Description: 班次保存
	  * void    返回类型
	  * @throws
	 */
	@Test
	public void testSave() {
		ClassSetting classSetting = new ClassSetting();
		classSetting.setCompanyId(1L);
		classSetting.setDepartId(1L);
		classSetting.setName("A班003");
		classSetting.setStartTime(DateUtils.parse("2017-01-01 09:00:00"));
		classSetting.setEndTime(DateUtils.parse("2017-01-01 18:00:00"));
		classSetting.setIsInterDay(0);
		classSetting.setMustAttnTime(8.0);
		
		try {
			classSettingService.save(classSetting);
		} catch (OaException e) {

		}
	}
}
