package com.ule.oa.base.mapper;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ule.oa.base.po.EmpPostRecord;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

public interface EmpPostRecordMapper extends OaSqlMapper{
	List<EmpPostRecord> getListByCondition(EmpPostRecord empPostRecord);

	Integer save(EmpPostRecord empPostRecord);

	Integer updateById(EmpPostRecord empPostRecord);
	
	List<EmpPostRecord> getListByEmployeeId(@Param("employeeId")Long employeeId);
	
	int saveBatch(List<EmpPostRecord> empPostRecords);
	
	int deleteByEmployeeId(@Param("employeeId")Long employeeId,@Param("updateUser")String updateUser,@Param("updateTime")Date updateTime);
}