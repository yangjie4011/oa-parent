package com.ule.oa.attendance.service.impl;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ule.oa.base.service.AttnStatisticsService;
import com.ule.oa.base.service.impl.BaseServiceImpl;

/**
  * @ClassName: AttnStatisticsServiceImplTest
  * @Description: 考勤统计业务层单元测试
  * @author minsheng
  * @date 2017年11月13日 下午1:36:34
 */
public class AttnStatisticsServiceImplTest extends BaseServiceImpl{
	@Autowired
	private AttnStatisticsService attnStatisticsService;
	
	/**
	  * testAttnExMsgRemind(异常考勤提醒)
	  * @Title: testAttnExMsgRemind
	  * @Description: 异常考勤提醒
	  * void    返回类型
	  * @throws
	 */
	@Test
	public void testAttnExMsgRemind() {
		try {
			attnStatisticsService.attnExMsgRemind();
		} catch (Exception e) {
		}
	}

}
