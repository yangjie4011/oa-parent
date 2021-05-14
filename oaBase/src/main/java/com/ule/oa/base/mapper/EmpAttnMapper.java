package com.ule.oa.base.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.ClassSetting;
import com.ule.oa.base.po.EmpAttn;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

public interface EmpAttnMapper  extends OaSqlMapper{

	Integer getAttnReportCount(EmpAttn empAttn);

	List<Map<String, Object>> getAttnReportPageList(EmpAttn empAttn);
	
	List<Map<String, Object>> getSignWorkHoursList(@Param("startTime")Date startTime,@Param("endTime")Date endTime);
	
	List<Map<String, Object>> getNotSignWorkHoursList(@Param("startTime")Date startTime,@Param("endTime")Date endTime);
	
	Integer getSignRecordReportCount(EmpAttn empAttn);

	List<Map<String, Object>> getSignRecordReportList(EmpAttn empAttn);

	Integer getClassSettingCount(ClassSetting classSetting);

	List<ClassSetting> getClassSettingList(ClassSetting classSetting);

	List<Map<String, Object>> getMonthLackDetailList(EmpAttn empAttn);

	Integer getMonthLackDetailCount(EmpAttn empAttn);

	Integer getMonthLackTotalCount(EmpAttn empAttn);

	List<Map<String, Object>> getMonthLackTotalList(EmpAttn empAttn);
	
	//通过id和考情时间 来查询员工当天的上下班打卡时间
	EmpAttn getAttnTimeByIdAndAttnTime(EmpAttn empAttn);
	
	//员工签到
	Integer getEmployeeSignReportCount(EmpAttn empAttn);
	//员工签到
	List<Map<String, Object>> getEmployeeSignReportList(EmpAttn empAttn);
	
	//定位签到
	Integer getLocationCheckInDataCount(EmpAttn empAttn);
	
	//定位签到
	List<Map<String, Object>> getLocationCheckInDataList(EmpAttn empAttn);
	
	List<EmpAttn> getAbnormalAttnList(@Param("employIdList")List<Long> employIdList, @Param("startDate")Date startDate, @Param("endDate")Date endDate);

}
