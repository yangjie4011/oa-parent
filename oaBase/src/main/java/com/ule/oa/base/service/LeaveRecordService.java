package com.ule.oa.base.service;

import java.util.List;
import java.util.Map;

import com.ule.oa.base.po.LeaveRecord;

public interface LeaveRecordService {
	
	//根据员工id获取员工已用各类假期天数（包括加班小时）
	Map<String,Object> getUsedLeaveListByEmployeeId(Long employeeId);
	
	//初始化所有假期流水
	void initLeaveRecord();
	
	List<LeaveRecord> getRecordListByYearAndEmployee(String yearType);

}
