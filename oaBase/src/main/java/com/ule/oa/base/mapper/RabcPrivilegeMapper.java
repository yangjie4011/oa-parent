package com.ule.oa.base.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.RabcPrivilege;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

public interface RabcPrivilegeMapper extends OaSqlMapper{
	
	List<Long> getResourceByPrivilege(@Param("roleId")Long roleId);

	List<Long> getOperationByPrivilege(@Param("roleId")Long roleId);
	
	void deleteResourceByRoleId(@Param("roleId")Long roleId, @Param("updateUser")String updateUser);
	
	void saveResource(List<Map<String,Object>> list);

	RabcPrivilege getByResourceId(@Param("resourceId")Long resourceId);
	
	//根据角色名称查询默认拥有的权限数据
	List<Long> getListByBelongRole(@Param("belongRole")String belongRole);
	
	//查询HR管理员默认拥有的权限数据
	List<Long> getListOfHrAdmin();
	
	//查询所有的权限数据
	List<Long> getAllList();
	
}
