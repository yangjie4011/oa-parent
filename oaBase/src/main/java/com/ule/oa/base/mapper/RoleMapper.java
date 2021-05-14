package com.ule.oa.base.mapper;

import java.util.List;

import com.ule.oa.base.po.Role;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

public interface RoleMapper extends OaSqlMapper{
	Role getById(Long id);
	
	Integer save(Role role);
	
	Integer updateById(Role role);
	
	/**
	  * getPageList(分页查询角色)
	  * @Title: getPageList
	  * @Description: 分页查询角色
	  * @param role
	  * @return    设定文件
	  * List<Role>    返回类型
	  * @throws
	 */
	List<Role> getPageList(Role role);
	
	/**
	  * getCount(查询角色总记录数)
	  * @Title: getCount
	  * @Description: 查询角色总记录数
	  * @param role
	  * @return    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	Integer getCount(Role role);
	
	/**
	  * getListByCondition(根据条件查询所有的角色)
	  * @Title: getListByCondition
	  * @Description: 根据条件查询所有的角色
	  * @param role
	  * @return    设定文件
	  * List<Role>    返回类型
	  * @throws
	 */
	List<Role> getListByCondition(Role role);
}
