package com.ule.oa.base.service;

import java.util.List;
import java.util.Map;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.RabcResource;

/**
  * @ClassName: ResourceService
  * @Description: 资源（菜单和按钮）业务层接口
  * @author minsheng
  * @date 2017年11月22日 下午4:00:17
 */
public interface RabcResourceService {
	/**
	  * getListByCondition(根据指定条件获得菜单)
	  * @Title: getListByCondition
	  * @Description: 根据指定条件获得菜单
	  * @return    设定文件
	  * List<Resource>    返回类型
	  * @throws
	 */
	public List<RabcResource> getListByCondition(RabcResource Resource);
	
	/**
	  * getTreeList(这里用一句话描述这个方法的作用)
	  * @Title: getTreeList
	  * @Description: TODO
	  * @param resource
	  * @return    设定文件
	  * List<Map<String,String>>    返回类型
	  * @throws
	 */
	public List<Map<String, String>> getTreeList(RabcResource resource);
	
	public void saveSet(String roleId,String sourceIds,Employee user);
	
	
	/**
	  * getAllAdminMenuListByUserId(根据指定条件获得后台所有菜单)
	  * @Title: getAllAdminMenuListByUserId
	  * @Description: 根据指定条件获得后台所有菜单
	  * @return    设定文件
	  * List<Resource>    返回类型
	  * @throws
	 */
	List<RabcResource> getAllAdminMenuListByUserId(Long userId);
	
	/**
	  * getFristAdminMenuListByUserId(根据员工id获得后台菜单)
	  * @Title: getFristAdminMenuListByUserId
	  * @Description: 根据员工id获得后台菜单
	  * @return    设定文件
	  * List<Resource>    返回类型
	  * @throws
	 */
	public List<RabcResource> getFristAdminMenuListByUserId(Long userId);
	
	/**
	  * getAllAdminTabListByUserId(根据指定条件获得后台所有菜单Tab)
	  * @Title: getAllAdminTabListByUserId
	  * @Description: 根据指定条件获得后台所有菜单
	  * @return    设定文件
	  * List<Resource>    返回类型
	  * @throws
	 */
	List<RabcResource> getAllAdminTabListByUserId(Long userId);
	
	/**
	  * getAllAdminTabListByUserId(根据指定条件获得后台所有菜单Tab)
	  * @Title: getAllAdminTabListByUserId
	  * @Description: 根据指定条件获得后台所有菜单
	  * @return    设定文件
	  * List<Resource>    返回类型
	  * @throws
	 */
	List<RabcResource> getAllAdminTabListByUserIdAndParentId(Long userId,Integer parentId);
	
	/**
	  * getAllAdminOperationListByUserId(根据指定条件获得后台所有操作)
	  * @Title: getAllAdminOperationListByUserId
	  * @Description: 根据指定条件获得后台所有操作
	  * @return    设定文件
	  * List<Resource>    返回类型
	  * @throws
	 */
	List<RabcResource> getAllAdminOperationListByUserId(Long userId);
	
	/**
	  * getOperationByUserIdAndUrl(根据指定条件获得后台所有操作)
	  * @Title: getOperationByUserIdAndUrl
	  * @Description: 根据指定条件获得后台所有操作
	  * @return    设定文件
	  * List<Resource>    返回类型
	  * @throws
	 */
	RabcResource getOperationByUserIdAndUrl(Long userId,String url);
	
	/**
	  * getMenuByUserIdAndUrl(根据指定条件获得后台所有菜单)
	  * @Title: getMenuByUserIdAndUrl
	  * @Description: 根据指定条件获得后台所有菜单
	  * @return    设定文件
	  * List<Resource>    返回类型
	  * @throws
	 */
	RabcResource getMenuByUserIdAndUrl(Long userId,String url);
}
