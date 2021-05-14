package com.ule.oa.base.service;

import java.util.Date;


public interface CommonService {
	
	
	/**
	  * getWorkDaysCount(查询时间段内工作天数--不适用于排班员工)
	  * @Title: getWorkDaysCount
	  * @Description: 查询时间段内工作天数
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
	int getWorkDaysCount(Date startDate,Date endDate);

}
