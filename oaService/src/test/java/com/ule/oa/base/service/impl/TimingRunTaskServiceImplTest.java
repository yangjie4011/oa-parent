package com.ule.oa.base.service.impl;

import javax.annotation.Resource;

import org.junit.Test;

import com.ule.oa.base.service.TimingRunTaskService;

public class TimingRunTaskServiceImplTest extends BaseServiceImpl{

	@Resource
	private TimingRunTaskService timingRunTaskService;
	

	/**
	 * 测试审批提醒
	 */
	@Test
	public void testStartRunTask() {
		try {
			timingRunTaskService.startRunTask();
		} catch (Exception e) {

		}
	}
}
