package com.ule.oa.attendance.service.impl;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ule.oa.base.service.AttnSignRecordService;
import com.ule.oa.base.service.impl.BaseServiceImpl;

/**
  * @ClassName: AttnSignRecordServiceImplTest
  * @Description: 打卡记录业务层单元测试
  * @author minsheng
  * @date 2017年11月8日 下午2:43:58
 */
public class AttnSignRecordServiceImplTest extends BaseServiceImpl{
	@Autowired
	private AttnSignRecordService attnSignRecordService;
	
	@Test
	public void testSignRecordExMsgRemind() throws Exception {
		attnSignRecordService.signRecordExMsgRemind();
	}

}
