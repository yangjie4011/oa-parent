package com.ule.oa.base.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.EmpUrgentContact;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

public interface EmpUrgentContactMapper extends OaSqlMapper{
	int updateById(EmpUrgentContact empUrgentContact);

	int save(EmpUrgentContact empUrgentContact);

    List<EmpUrgentContact> getListByCondition(EmpUrgentContact empUrgentContact);

	int saveBatch(@Param("list")List<EmpUrgentContact> empUrgentContacts);

	int deleteBatchNotApply(@Param("list") List<EmpUrgentContact> empUrgentContacts,
			@Param("employeeId")Long employeeId, @Param("updateUser")String updateUser,@Param("updateTime") Date updateTime);
	
	List<EmpUrgentContact> getListByEmployeeId(@Param("employeeId")Long employeeId);
	
	int deleteByEmployeeId(@Param("employeeId")Long employeeId,@Param("updateUser")String updateUser,@Param("updateTime")Date updateTime);
}