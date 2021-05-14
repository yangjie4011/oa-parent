package com.ule.oa.base.service;

import java.util.Map;

import com.ule.oa.base.po.TransNormal;

public interface TransNormalService {
	
	/**
	  * 自动跑考勤数据的开端！！！！！每小时一次
	  * @Title: setAttnSignRecord
	  * @param transNormal
	  * @return    设定文件
	  * @throws
	 */
	void setAttnSignRecord(TransNormal transNormal);

	/**
	  * 跑考勤数据的开端！！！！！一条线会把后续流程跑完，不能随意调用！！
	  * @Title: startAttnByTime
	  * @param transNormal  考勤开始时间 --- 考勤结束时间，到天！！
	  * void    返回类型
	  * @throws
	 */
	void startAttnByTime(TransNormal transNormal);
	
	Map<String, String> recalculateAttnByCondition(final TransNormal transNormal);
}
