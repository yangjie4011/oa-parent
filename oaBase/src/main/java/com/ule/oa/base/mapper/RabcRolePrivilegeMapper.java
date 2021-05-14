package com.ule.oa.base.mapper;

import java.util.Date;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.util.jdbc.OaSqlMapper;

public interface RabcRolePrivilegeMapper extends OaSqlMapper{
	
	void delByRoleId(@Param("roleId")Long roleId,@Param("updateTime")Date updateTime,@Param("updateUser")String updateUser);

}
