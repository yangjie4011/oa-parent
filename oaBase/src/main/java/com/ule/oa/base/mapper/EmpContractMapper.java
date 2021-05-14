package com.ule.oa.base.mapper;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ule.oa.base.po.EmpContract;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

public interface EmpContractMapper extends OaSqlMapper{
	List<EmpContract> getListByCondition(EmpContract empContract);
	
	int updateById(EmpContract empContract);

	int save(EmpContract empContract);
	
	List<EmpContract> getListByEmployeeId(@Param("employeeId")Long employeeId);
	
    int saveBatch(List<EmpContract> empContracts);
	
	int deleteByEmployeeId(@Param("employeeId")Long employeeId,@Param("updateUser")String updateUser,@Param("updateTime")Date updateTime);
}