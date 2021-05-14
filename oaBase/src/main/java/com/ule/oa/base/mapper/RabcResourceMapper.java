package com.ule.oa.base.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.RabcResource;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

public interface RabcResourceMapper extends OaSqlMapper{
    RabcResource getById(Long id);
    
    int save(RabcResource record);

    int updateById(RabcResource record);
    
    /**
	  * getListByCondition(根据指定条件获得菜单)
	  * @Title: getListByCondition
	  * @Description: 根据指定条件获得菜单
	  * @return    设定文件
	  * List<Resource>    返回类型
	  * @throws
	 */
	List<RabcResource> getListByCondition(RabcResource Resource);
	
	/**
	  * getTreeList(获取资源树)
	  * @Title: getTreeList
	  * @Description: 获取资源树
	  * @param Resource
	  * @return    设定文件
	  * List<Resource>    返回类型
	  * @throws
	 */
	List<RabcResource> getTreeList(RabcResource Resource);
	
	/**
	  * getFristAdminMenuListByEmployId(根据指定条件获得后台一级菜单)
	  * @Title: getFristAdminMenuListByEmployId
	  * @Description: 根据指定条件获得后台一级菜单
	  * @return    设定文件
	  * List<Resource>    返回类型
	  * @throws
	 */
	List<RabcResource> getFristAdminMenuList(@Param("idList")List<Long> idList);
	
	/**
	  * getAllAdminMenuListByEmployId(根据userId获得员工拥有后台所有菜单)
	  * @Title: getAllAdminMenuListByEmployId
	  * @Description: 根据指定条件获得后台所有菜单
	  * @return    设定文件
	  * List<Resource>    返回类型
	  * @throws
	 */
	List<RabcResource> getAllAdminMenuListByUserId(@Param("userId")Long userId);
	
	List<RabcResource> getAllAdminTabListByUserIdAndParentId(@Param("userId")Long userId,@Param("parentId")Integer parentId);
	
	/**
	  * getIdListByUserId(根据userId查询用户拥有的菜单)
	  * @Title: getIdListByUserId
	  * @Description: 根据userId查询用户拥有的菜单
	  * @return    设定文件
	  * List<Resource>    返回类型
	  * @throws
	 */
	List<RabcResource> getIdListByUserId(@Param("userId")Long userId);
	
	/**
	  * getAllAdminTabListByUserId(根据userId获得员工拥有后台所有菜单Tab)
	  * @Title: getAllAdminTabListByUserId
	  * @Description: 根据指定条件获得后台所有菜单Tab
	  * @return    设定文件
	  * List<Resource>    返回类型
	  * @throws
	 */
	List<RabcResource> getAllAdminTabListByUserId(@Param("userId")Long userId);
	
	
	/**
	  * getAllAdminOperationListByUserId(根据userId获得员工拥有后台所有数据权限)
	  * @Title: getAllAdminOperationListByUserId
	  * @Description: 根据userId获得员工拥有后台所有数据权限
	  * @return    设定文件
	  * List<Resource>    返回类型
	  * @throws
	 */
	List<RabcResource> getAllAdminOperationListByUserId(@Param("userId")Long userId);
	
	/**
	  * getOperationByUserIdAndUrl(根据userId和url获得员工拥有后台数据权限)
	  * @Title: getOperationByUserIdAndUrl
	  * @Description: 根据userId和url获得员工拥有后台数据权限
	  * @return    设定文件
	  * List<Resource>    返回类型
	  * @throws
	 */
	RabcResource getOperationByUserIdAndUrl(@Param("userId")Long userId,@Param("url")String url);
	
	/**
	  * getMenuByUserIdAndUrl(根据userId和url获得员工拥有后台所有菜单)
	  * @Title: getMenuByUserIdAndUrl
	  * @Description: 根据指定条件获得后台所有菜单
	  * @return    设定文件
	  * List<Resource>    返回类型
	  * @throws
	 */
	RabcResource getMenuByUserIdAndUrl(@Param("userId")Long userId,@Param("url")String url);
	
	RabcResource getOperationByUserIdAndUrlAndDeaprtId(@Param("userId")Long userId,@Param("url")String url,@Param("departId")Long departId);
}