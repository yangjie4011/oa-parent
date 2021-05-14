package com.ule.oa.base.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.EmpApplicationLeave;
import com.ule.oa.base.po.EmpApplicationLeaveAbolish;
import com.ule.oa.base.po.LeaveDaysGBTypeResultDto;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

/**
 * @ClassName: 请假申请
 * @Description: 请假申请
 * @author yangjie
 * @date 2017年6月14日
 */
public interface EmpApplicationLeaveMapper extends OaSqlMapper {
	
    void save(EmpApplicationLeave leave);
	
	int updateById(EmpApplicationLeave leave);
	
	EmpApplicationLeave getById(Long id);
	
	List<EmpApplicationLeave> getListByEmployeeId(Long employeeId);
	//获取产前假
	List<EmpApplicationLeave> getAntenatalLeaveList(EmpApplicationLeave leave);

	List<EmpApplicationLeave> getListByCondition(EmpApplicationLeave model);
	
	int getReportCount(EmpApplicationLeave leave);
	
	List<EmpApplicationLeave> getReportPageList(EmpApplicationLeave leave);
	
	List<EmpApplicationLeave> myLeaveTaskList(EmpApplicationLeave leave);
	
	int myLeaveTaskListCount(EmpApplicationLeave leave);
	
	List<EmpApplicationLeave> getExportReportList(EmpApplicationLeave leave);

	List<Map<String,Object>> getRepairDate();

	EmpApplicationLeave queryByProcessId(String processId);

	void insertProcessInstanceId(EmpApplicationLeave empApplicationLeave);
	
	//读取2018级以后的所有除（年假，病假，调休）的审核通过的假期申请单
	List<EmpApplicationLeave> getOtherLeaveAfter2018();
	
	//查询员工对应假期类型已用总数
	List<Map<String,Object>> getUsedDaysGroupByEmployeeId(Long leaveType);
	
	//根据员工Id和假期类型按时间倒叙查询假单
	List<EmpApplicationLeave> getListByTypeAndEmployeeId(EmpApplicationLeave leave);
	
	//按年和员工统计员工已用病假天数
	List<Map<String,Object>> getSickUsedDays();

	List<EmpApplicationLeaveAbolish> myLeaveTaskList(
			EmpApplicationLeaveAbolish leave);
	
	/**
	 * 查询员工一个月内指定假期类型总请假天数
	 * @param leaveTypes
	 * @param startTime
	 * @param employeeId
	 * @return
	 */
	List<LeaveDaysGBTypeResultDto> getInOneMonthLeaveDaysByTypes(@Param("employeeId")Long employeeId,@Param("startTime")Date startTime,@Param("endTime")Date endTime);
	
	/**
	 * 查询员工一个月内指定假期类型最小开始时间
	 * @param employeeId
	 * @param startTime
	 * @param endTime
	 * @param leaveTypes
	 * @return
	 */
	Date getInOneMonthMinStartTimeByTypes(@Param("employeeId")Long employeeId,@Param("startTime")Date startTime,@Param("endTime")Date endTime,@Param("leaveTypes")List<Integer> leaveTypes);
}
