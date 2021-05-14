package com.ule.oa.base.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.DelayWorkRegister;
import com.ule.oa.base.po.EmpApplicationAbnormalAttendance;
import com.ule.oa.base.po.RemoveSubordinateAbsence;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

public interface RemoveSubordinateAbsenceMapper extends OaSqlMapper {

	List<RemoveSubordinateAbsence> getByEmpAndDate(@Param("employeeId")Long employeeId, @Param("attendanceDate")Date attendanceDate);

	void insertRemoveSubordinateAbsence(RemoveSubordinateAbsence removeSubordinateAbsence);

	RemoveSubordinateAbsence getByProcessId(@Param("processId")String processId);
	
	//根据月份查询该员工所有延时工作登记
	List<RemoveSubordinateAbsence> getRemoveSubordinateAbsenceMapperByMonth(@Param("employeeId")Long employeeId, @Param("fristDay")Date fristDay, @Param("lastDay")Date lastDay);
	
	//查询当前时间以前所有未匹配考勤的登记数据
	List<RemoveSubordinateAbsence> getUnMatchedListByDelayDate(@Param("delayDate")Date delayDate);
	
	//匹配实际考勤
	int matchActaulTime(RemoveSubordinateAbsenceMapper match);
	
	//根据员工id与日期查询延时工作记录
	RemoveSubordinateAbsenceMapper getDelayWorkDetail(@Param("employeeId")Long employeeId, @Param("delayDate")Date delayDate);	
	
	//修改登记
	void updateDelayWorkDetail(DelayWorkRegister delayWorkRegister);

	//确认登记
	void confirmDelayWorkDetail(DelayWorkRegister delayWorkRegister);
	
	//根据id查询延时工作详情
	RemoveSubordinateAbsence getById(@Param("id")Long id);
	
	Integer getApplicationListCount(RemoveSubordinateAbsence removeSubordinateAbsence);
	
	List<RemoveSubordinateAbsence> getApplicationPageList(RemoveSubordinateAbsence removeSubordinateAbsence);
	
	List<RemoveSubordinateAbsence> getList(RemoveSubordinateAbsence removeSubordinateAbsence);
	
	void updateById(RemoveSubordinateAbsence removeSubordinateAbsence);
	
	List<RemoveSubordinateAbsence> getListByEmployIdsandDate(@Param("employIdList")List<Long> employIdList, @Param("startDate")Date startDate, @Param("endDate")Date endDate);
	
	Integer getCount(RemoveSubordinateAbsence attendance);

	List<RemoveSubordinateAbsence> getPageList(
			RemoveSubordinateAbsence attendance);
	//查询待已办数据
	Integer myTaskListCount(RemoveSubordinateAbsence attendance);
	//查询待办数据
	List<RemoveSubordinateAbsence> myTaskList(
			RemoveSubordinateAbsence attendance);
	
	//查询员工一段时间消缺勤小时数总数
	RemoveSubordinateAbsence getAbsenceHoursCount(@Param("employId")Long employId, @Param("startDate")Date startDate, @Param("endDate")Date endDate);
	
	List<RemoveSubordinateAbsence> getAbsenceHoursList(@Param("employId")Long employId, @Param("startDate")Date startDate, @Param("endDate")Date endDate);

	RemoveSubordinateAbsence queryByProcessId(String processId);
	
	List<RemoveSubordinateAbsence> getCompletedListByDate(@Param("startDate")Date startDate, @Param("endDate")Date endDate);
	
	List<Map<String,Object>> getLastMonthTotalUsedHours(@Param("startMonth")Date startMonth, @Param("endMonth")Date endMonth,@Param("departId")Long departId);


}
