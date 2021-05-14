package com.ule.oa.base.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ule.oa.base.service.TimingRunTaskService;
import com.ule.oa.common.spring.SpringContextUtils;

public class ScheduledExecutorTimingRunTask {
	private ScheduledExecutorService scheduExec;

	private long start;

	public ScheduledExecutorTimingRunTask() {
		this.scheduExec = Executors.newScheduledThreadPool(2);
		this.start = System.currentTimeMillis();
	}
	//1秒 1000
	//1分钟 1000*60
	//1小时 1000*60*60
	//1天1000*60*60*24
	public void timerRun() {
		scheduExec.scheduleAtFixedRate(new Runnable() {
			public void run() {
				try {
					TimingRunTaskService timingRunTaskService = SpringContextUtils.getContext().getBean(TimingRunTaskService.class);
					timingRunTaskService.startRunTask();
				} catch (Exception e) {
				}
			}
		}, 1000, 1000*60*60*24L, TimeUnit.MILLISECONDS);
	}
	
	public long getStart() {
		return start;
	}
	public void setStart(long start) {
		this.start = start;
	}
	
}
