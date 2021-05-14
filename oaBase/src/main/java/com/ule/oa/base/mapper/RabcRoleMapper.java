package com.ule.oa.base.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ule.oa.base.po.RabcRole;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

public interface RabcRoleMapper extends OaSqlMapper{

	List<RabcRole> getAllURoleList();

	RabcRole getRoleInfo(@Param("roleId")Long roleId);

	void save(RabcRole uRole);

	void deleteRole(RabcRole uRole);

	Integer getCountByRoleName(@Param("name")String name,@Param("departId")Long departId);

	Integer getUserCountByRoleId(@Param("roleId")Long roleId);

	List<RabcRole> getAllURoleListByDepartId(@Param("departId")Long departId);

	List<String> getAllURoleNameByDepartId(@Param("departId")Long departId);
	
	List<Long> findInitDepartBydepartId(@Param("departId")Long departId);
	
	List<RabcRole> getListByDepartIdAndName(@Param("departId")Long departId,@Param("name")String name);
	
	//查询登录用户拥有的角色list
	List<RabcRole> getListByUserId(@Param("userId")Long userId,@Param("departId")Long departId);

}
