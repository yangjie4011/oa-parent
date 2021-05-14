package com.ule.oa.base.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.util.jdbc.OaSqlMapper;

/**
 * @ClassName: 后台用户
 * @Description: 后台用户
 * @author yangjie
 * @date 2019年9月16日
 */

public interface RabcUserMapper extends OaSqlMapper{
	
	
	/**
	 * 根据员工id查询用户的数据权限
	 * @param employId
	 * @return List<Long>
	 */
	List<Long> getDataPermissions(@Param("employId")Long employId);

	List<Integer> getDeptIdsByUser(Long employeeId);
	
}
