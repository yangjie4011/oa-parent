package com.ule.oa.base.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ule.oa.base.mapper.RabcResourceMapper;
import com.ule.oa.base.mapper.RolePrivilegeMapper;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.RabcResource;
import com.ule.oa.base.po.RolePrivilege;
import com.ule.oa.base.service.RabcResourceService;

import net.sf.json.JSONArray;

/**
 * @ClassName: ResourceService
 * @Description: 资源（菜单和按钮）业务层
 * @author minsheng
 * @date 2017年11月22日 下午4:00:17
*/
@Service
public class RabcResourceServiceImpl implements RabcResourceService {
	@Autowired
	private RabcResourceMapper resourceMapper;
	@Autowired
	private RolePrivilegeMapper rolePrivilegeMapper;
	
	/**
	  * getListByCondition(根据指定条件获得菜单)
	  * @Title: getListByCondition
	  * @Description: 根据指定条件获得菜单
	  * @return    设定文件
	  * List<Resource>    返回类型
	  * @throws
	 */
	public List<RabcResource> getListByCondition(RabcResource Resource){
		return resourceMapper.getListByCondition(Resource);
	}
	
	/**
	  * getTreeList(获得资源树)
	  * @Title: getTreeList
	  * @Description: 获得资源树
	  * @param position
	  * @return    设定文件
	  * List<Map<String, String>>    返回类型
	  * @throws
	 */
	public List<Map<String, String>> getTreeList(RabcResource resource){
		List<RabcResource> listByCondition = resourceMapper.getListByCondition(resource);
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> map;
		List<RolePrivilege> pList = new ArrayList<RolePrivilege>();
		if(resource.getRoleId()!=null){
		    pList = rolePrivilegeMapper.getByRoleId(resource.getRoleId());
		}
		for (RabcResource res : listByCondition) {
			map = new HashMap<String, String>();
			map.put("id", res.getId() + "");
			map.put("pId", res.getParentId() == null ? "0" : res.getParentId() + "");
			map.put("name", res.getResourceName());
			map.put("rank", res.getRank() + "");
			for(RolePrivilege rolePrivilege:pList){
				if(res.getId().equals(rolePrivilege.getSourceId())){
					map.put("checked", "true");
				}
			}
			list.add(map);
		}
		
		return list;
	}

	@Override
	public void saveSet(String roleId, String sourceIds,Employee user) {
		//获取角色已经拥有的权限
		List<RolePrivilege>  oldList = rolePrivilegeMapper.getByRoleId(Long.valueOf(roleId));
		JSONArray array = JSONArray.fromObject(sourceIds);
		if(array==null||array.size()<=0){
			//将后台权限全部删除
			for(RolePrivilege delete:oldList){
				delete.setUpdateTime(new Date());
				delete.setUpdateUser(user.getCnName());
				delete.setDelFlag(1);
				rolePrivilegeMapper.updateById(delete);
			}
		}else{
			for(int i=0;i<array.size();i++){
				RolePrivilege add = new RolePrivilege();
				add.setCompanyId(user.getCompanyId());
				add.setRoleId(Long.valueOf(roleId));
				add.setSourceId(array.getLong(i));
				add.setCreateTime(new Date());
				add.setCreateUser(user.getCnName());
				add.setDelFlag(0);
				rolePrivilegeMapper.save(add);
			}
			//老数据全部删除
			for(RolePrivilege delete:oldList){
				delete.setUpdateTime(new Date());
				delete.setUpdateUser(user.getCnName());
				delete.setDelFlag(1);
				rolePrivilegeMapper.updateById(delete);
			}
		}
	}

	@Override
	public List<RabcResource> getFristAdminMenuListByUserId(Long userId) {
		List<RabcResource> result = resourceMapper.getIdListByUserId(userId);
		return result;
	}

	@Override
	public List<RabcResource> getAllAdminMenuListByUserId(Long userId) {
		return resourceMapper.getAllAdminMenuListByUserId(userId);
	}

	@Override
	public List<RabcResource> getAllAdminTabListByUserId(Long userId) {
		return resourceMapper.getAllAdminTabListByUserId(userId);
	}

	@Override
	public List<RabcResource> getAllAdminOperationListByUserId(Long userId) {
		return resourceMapper.getAllAdminOperationListByUserId(userId);
	}

	@Override
	public RabcResource getOperationByUserIdAndUrl(Long userId, String url) {
		return resourceMapper.getOperationByUserIdAndUrl(userId,url);
	}

	@Override
	public RabcResource getMenuByUserIdAndUrl(Long userId, String url) {
		return resourceMapper.getMenuByUserIdAndUrl(userId,url);
	}

	@Override
	public List<RabcResource> getAllAdminTabListByUserIdAndParentId(
			Long userId, Integer parentId) {
		return resourceMapper.getAllAdminTabListByUserIdAndParentId(userId, parentId);
	}
}
