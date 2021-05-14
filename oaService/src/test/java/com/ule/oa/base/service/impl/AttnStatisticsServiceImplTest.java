package com.ule.oa.base.service.impl;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ule.oa.base.po.AttnStatistics;
import com.ule.oa.base.service.AttnStatisticsService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.DateUtils;

public class AttnStatisticsServiceImplTest extends BaseServiceImpl{
	@Autowired
	private AttnStatisticsService attnStatisticsService;

	@Test
	@Ignore
	public void testGetTotalAttStatistics() {
		AttnStatistics condition = new AttnStatistics();
		condition.setEmployId(1716L);
		condition.setStartTime(DateUtils.parse("2018-02-01 00:00:00"));
		condition.setEndTime(DateUtils.parse("2018-02-28 00:00:00"));
		
		try {
			AttnStatistics attnStatistics = attnStatisticsService.getTotalAttStatistics(condition);
			
			System.out.println(attnStatistics.getMustAttnTime() + "\t" + attnStatistics.getAllAttnTime());
		} catch (OaException e) {

		}
	}

	@Test
	public void testAttnExMsgRemind(){
		try {
			attnStatisticsService.attnExMsgRemind();
		} catch (Exception e) {
		
		}
	}
}
