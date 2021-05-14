package com.ule.oa.base.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.LeaveRecord;
import com.ule.oa.base.po.LeaveRecordDetail;
import com.ule.oa.base.po.RequestQueryLeaveRecord;
import com.ule.oa.base.po.ResponseQueryLeaveRecord;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

/**
 * @author yangjie 假期流水表
 */
public interface LeaveRecordMapper extends OaSqlMapper {

	// 申请请假/加班审批通过时使用
	int save(LeaveRecord record);

	// 根据员工id获取员工已用各类假期天数（包括加班小时）
	List<LeaveRecord> getUsedLeaveListByEmployeeId(Long employeeId);

	// 根据员工idList获取员工已用各类假期天数（包括加班小时）
	List<LeaveRecord> getUsedLeaveListByEmployeeIds(List<Long> employeeIdList);


	// 根据单据日期范围查询单据id
	List<Long> getBillIdBySubmitDate(@Param("submitStartDate") Date submitStartDate,
			@Param("submitEndDate") Date submitEndDate);

	// 查询假期流水
	List<LeaveRecord> getLeaveRecordList(RequestQueryLeaveRecord requestQueryLeaveRecord);
	//查询总条数
	Integer getLeaveRecordCount(RequestQueryLeaveRecord requestQueryLeaveRecord);

	Employee getEmployeeById(@Param("employeeId")Long employeeId);

	String getLeaveTypeText(@Param("type")Integer type);

	List<LeaveRecordDetail> getLeaveIdByLeaveRecordId(@Param("leaveRecordId")Long leaveRecordId);
	
	List<LeaveRecord> getUsedDaysByYear(LeaveRecord record);
	
	List<LeaveRecord> getUsedDaysByYears(LeaveRecord record);
	
	List<LeaveRecord> getUsedDaysByYearAndTypes(LeaveRecord record);
	
	List<LeaveRecord> selectByCondition(LeaveRecord record);
	
	void deleteByCondition(LeaveRecord record);

	List<LeaveRecordDetail> getLeaveRecordDetailList(RequestQueryLeaveRecord requestQueryLeaveRecord);

	LeaveRecord getleaveRecordInfo(@Param("leaveRecordId")Long leaveRecordId);

	List<Employee> getEmpInfo(@Param("employeeCode")String employeeCode, @Param("employeeName")String employeeName);

	String getDepartName(@Param("employeeId")Long id);

	List<Long> getIdByBillType(@Param("billType")String billType);

	void deleteByBillType(@Param("billType")String billType);

	void deleteDetailByLeaveRecordId(List<Long> leaveRecordIds);
	
	List<LeaveRecord> getRecordListByYearAndEmployee(@Param("employeeId")Long employeeId,@Param("year")Integer year);
	
}
