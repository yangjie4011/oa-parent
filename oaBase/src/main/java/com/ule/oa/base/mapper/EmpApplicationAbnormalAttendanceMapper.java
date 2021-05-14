package com.ule.oa.base.mapper;

import java.util.List;

import com.ule.oa.base.po.EmpApplicationAbnormalAttendance;
import com.ule.oa.base.util.jdbc.OaSqlMapper;
import com.ule.oa.common.persistence.SqlMapper;

/**
 * @ClassName: 考勤异常消除申请
 * @Description: 考勤异常消除申请
 * @author yangjie
 * @date 2017年6月15日
 */
public interface EmpApplicationAbnormalAttendanceMapper extends OaSqlMapper {
	
    int save(EmpApplicationAbnormalAttendance attendance);
    
    int getEaoByEmpAndDateCount(EmpApplicationAbnormalAttendance attendance);
	
	int updateById(EmpApplicationAbnormalAttendance attendance);
	
	EmpApplicationAbnormalAttendance getById(Long id);
	
	List<EmpApplicationAbnormalAttendance> getListByMonth(EmpApplicationAbnormalAttendance attendance);
	
	int getReportCount(EmpApplicationAbnormalAttendance attendance);
		
	List<EmpApplicationAbnormalAttendance> getReportPageList(EmpApplicationAbnormalAttendance attendance);
	
	List<EmpApplicationAbnormalAttendance> getExportReportList(EmpApplicationAbnormalAttendance attendance);
	
	EmpApplicationAbnormalAttendance queryByProcessId(String processId);

	Integer myAttnTaskListCount(EmpApplicationAbnormalAttendance attendance);

	List<EmpApplicationAbnormalAttendance> myAttnTaskList(
			EmpApplicationAbnormalAttendance attendance);
}
