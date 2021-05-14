package com.ule.oa.base.mapper;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ule.oa.base.po.EmpSchool;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

public interface EmpSchoolMapper extends OaSqlMapper{
    int save(EmpSchool empSchool);

    int updateById(EmpSchool empSchool);
    
    List<EmpSchool> getListByCondition(EmpSchool empSchool);

	int deleteBatchNotApply(@Param("list")List<EmpSchool> empSchools, @Param("employeeId")Long employeeId,
			@Param("updateUser")String updateUser, @Param("updateTime")Date udateTime);

	int saveBatch(List<EmpSchool> empSchools);
	
	List<EmpSchool> getListByEmployeeId(@Param("employeeId")Long employeeId);
	
	int deleteByEmployeeId(@Param("employeeId")Long employeeId,@Param("updateUser")String updateUser,@Param("updateTime")Date updateTime);
}