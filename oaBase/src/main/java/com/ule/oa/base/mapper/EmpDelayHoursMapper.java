package com.ule.oa.base.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.EmpDelayHours;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

public interface EmpDelayHoursMapper extends OaSqlMapper {

	EmpDelayHours getByEmpAndMonth(@Param("employeeId")Long employeeId, @Param("delayMonth")Date delayMonth);

	void insertEmpDelayHours(EmpDelayHours empDelayHours);

	void updateEmpDelayHours(EmpDelayHours empDelayHours);
	
	List<EmpDelayHours> getByemployIdListAndMonth(@Param("employIdList")List<Long> employIdList, @Param("startMonth")Date startMonth, @Param("endMonth")Date endMonth);
	
	EmpDelayHours getCountByEmpAndMonth(@Param("employeeId")Long employeeId, @Param("startMonth")Date startMonth, @Param("endMonth")Date endMonth);
	
	List<EmpDelayHours> getByEmpAndYear(@Param("employeeId")Long employeeId, @Param("startMonth")Date startMonth, @Param("endMonth")Date endMonth);
	
	List<EmpDelayHours> getLockedList(@Param("employeeId")Long employeeId, @Param("startMonth")Date startMonth, @Param("endMonth")Date endMonth,@Param("sort")String sort);
	
	List<Map<String,Object>> getLastMonthTotalHours(@Param("startMonth")Date startMonth, @Param("endMonth")Date endMonth,@Param("departId")Long departId);
	
}
