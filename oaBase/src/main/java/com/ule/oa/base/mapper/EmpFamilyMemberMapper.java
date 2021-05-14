package com.ule.oa.base.mapper;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ule.oa.base.po.EmpFamilyMember;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

public interface EmpFamilyMemberMapper extends OaSqlMapper{
    int save(EmpFamilyMember empFamilyMember);

    List<EmpFamilyMember> getListByCondition(EmpFamilyMember empFamilyMember);

    int updateById(EmpFamilyMember empFamilyMember);

	int saveBatch(@Param("list")List<EmpFamilyMember> empFamilyMembers);

	int deleteBatchNotApply(@Param("list") List<EmpFamilyMember> empFamilyMembers,
			@Param("employeeId")Long employeeId, @Param("updateUser") String currentUser, @Param("updateTime") Date currentTime);
	
	List<EmpFamilyMember> getListByEmployeeId(@Param("employeeId")Long employeeId);
	
	int deleteByEmployeeId(@Param("employeeId")Long employeeId,@Param("updateUser")String updateUser,@Param("updateTime")Date updateTime,@Param("relation") Integer relation);
}