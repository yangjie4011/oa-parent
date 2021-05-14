package com.ule.oa.base.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.AttnUsers;
import com.ule.oa.base.util.jdbc.AttenceSqlMapper;

public interface AttnUsersMapper extends AttenceSqlMapper {

    int save(AttnUsers record);

    AttnUsers selectByCondition(AttnUsers attnUsers);

    int updateById(AttnUsers record);

	Integer saveBatch(List<AttnUsers> list);
	
	//根据指纹ID查询用户
	AttnUsers selectByFingerprintId(Integer fingerprintId);

	void bindFingerPrint(@Param("fingerPrint")Long fingerPrint, @Param("empId")Long empId);
}