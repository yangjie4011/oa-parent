package com.ule.oa.base.service;

import java.util.List;

import com.ule.oa.base.po.TimingRunTask;

public interface TimingRunTaskService {
	public List<TimingRunTask> getList();
	
	public void startRunTask();
}
