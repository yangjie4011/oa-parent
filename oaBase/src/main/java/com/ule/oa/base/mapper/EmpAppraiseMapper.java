package com.ule.oa.base.mapper;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ule.oa.base.po.EmpAppraise;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

public interface EmpAppraiseMapper extends OaSqlMapper{
	List<EmpAppraise> getListByCondition(EmpAppraise empAppraise);

	Integer save(EmpAppraise empAppraise);

	Integer updateById(EmpAppraise empAppraise);
	
	List<EmpAppraise> getListByEmployeeId(@Param("employeeId")Long employeeId);
	
    int saveBatch(List<EmpAppraise> empAppraises);
    
	int deleteByEmployeeId(@Param("employeeId")Long employeeId,@Param("updateUser")String updateUser,@Param("updateTime")Date updateTime);
}