package com.ule.oa.base.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.RabcRole;
import com.ule.oa.base.po.RabcUserRole;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

public interface RabcUserRoleMapper extends OaSqlMapper{

	List<RabcUserRole> getAllUserRoleList();

	RabcUserRole getUserRoleByUserRole(RabcUserRole rabcUserRole);
	
	RabcUserRole getRabcUserInfo(@Param("roleId")Long roleId);

	void save(RabcUserRole uRole);

	void updateUserRole(RabcUserRole rabcUserRole);
	
	List<RabcRole> getAllUserRoleListByDepartId(@Param("departId")Long departId);

	List<Long> getSelectedRoleIdByDepartIdAndUserId(@Param("departId")Long departId, @Param("userId")Long userId);

	List<RabcRole> getDepartRoleListByUserId(@Param("userId")Long userId);

	void updateUserRoleSInit(@Param("roleList")List<Long> roleList,@Param("userId") Long userId,@Param("createName") String createName);

	void savesInit(@Param("roleList")List<Long> roleList,@Param("userId")Long userId,@Param("createName") String createName);

	List<Integer> getDeptIdsByUser(@Param("employeeId") Long employeeId);
	
	List<Depart> getDepartByUser(@Param("employeeId") Long employeeId);
	
	void delByRoleId(@Param("roleId")Long roleId,@Param("updateTime")Date updateTime,@Param("updateUser")String updateUser);
}
