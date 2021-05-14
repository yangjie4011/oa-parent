package com.ule.oa.base.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ule.oa.base.mapper.DepartMapper;
import com.ule.oa.base.mapper.RabcPrivilegeMapper;
import com.ule.oa.base.mapper.RabcResourceMapper;
import com.ule.oa.base.mapper.RabcRoleMapper;
import com.ule.oa.base.po.RabcPrivilege;
import com.ule.oa.base.po.RabcResource;
import com.ule.oa.base.po.RabcRole;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.RabcRoleService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.exception.OaException;

@Service
public class RabcRoleServiceImpl implements RabcRoleService {

	@Autowired
	private RabcRoleMapper rabcRoleMapper;

	@Autowired
	private RabcResourceMapper rabcResourceMapper;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private RabcPrivilegeMapper rabcPrivilegeMapper;
	
	@Autowired
	private DepartMapper departMapper;
	/**
	 * 查詢所有角色
	 */
	@Override
	public List<RabcRole> getAllURoleList() {
		List<RabcRole> uRoleList = rabcRoleMapper.getAllURoleList();
		return uRoleList;
	}
	
	
	/**
	 *根据角色查询勾选菜单
	 */
	@Override
	public List<Map<String, Object>> getRoleResourceTree(Long roleId) {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		// 查询所有菜单
		RabcResource resource = new RabcResource();
		List<Integer> typeList = new ArrayList<Integer>();
		typeList.add(1);
		typeList.add(3);
		resource.setTypeList(typeList);
		List<RabcResource> resourceList = rabcResourceMapper.getListByCondition(resource);
		List<Long> checkedList = new ArrayList<Long>();
		if (roleId != null) {
			checkedList = rabcPrivilegeMapper.getResourceByPrivilege(roleId);
		}
		if (resourceList != null && resourceList.size() > 0) {
			for (RabcResource res : resourceList) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", res.getId().toString());
				map.put("pId", res.getParentId() == null ? "0" : res.getParentId().toString());
				map.put("name", res.getResourceName());
				map.put("rank", res.getRank() + "");
				if (checkedList != null && checkedList.size() > 0) {
					for (Long checkedId : checkedList) {
						if (res.getId().equals(checkedId)) {
							map.put("checked", "true");
						}
					}
				}
				resultList.add(map);
			}
		}
		return resultList;
	}
	
	/**
	 * 查询角色信息
	 */
	@Override
	public RabcRole getRoleInfo(Long roleId) {
		RabcRole uRole = rabcRoleMapper.getRoleInfo(roleId);
		return uRole;
	}

	/**
	 * 新建角色
	 * @throws OaException 
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addRole(RabcRole uRole) throws OaException {
		User user = userService.getCurrentUser();
		if(uRole == null ||StringUtils.isBlank(uRole.getName()) || uRole.getDepartId() == null){
			throw new OaException("角色名或部门不能为空！");
		}
		Integer count = rabcRoleMapper.getCountByRoleName(uRole.getName(),uRole.getDepartId());
		if(count > 0){
			throw new OaException("该部门已存在该角色");
		}
		uRole.setDelFlag(0);
		uRole.setCreateTime(new Date());
		uRole.setCreateUser(user.getEmployee().getId().toString());
		rabcRoleMapper.save(uRole);
	}

	/**
	 * 保存菜单
	 * @throws OaException 
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveResource(Long roleId, String resourceIds) throws OaException {
		User user = userService.getCurrentUser();
		if(roleId == null){
			throw new OaException("请选择角色！");
		}
		//先删除该角色所有菜单，再进行保存
		rabcPrivilegeMapper.deleteResourceByRoleId(roleId,user.getEmployee().getId().toString());
		String[] resourceArray = resourceIds.split(",");
		List<Map<String,Object>> resourceList = new ArrayList<Map<String,Object>>();
		if(resourceIds != null && resourceArray.length > 0){
			for (String resourceIdStr : resourceArray) {
				Map<String,Object> resourceMap = new HashMap<String,Object>();
				if(StringUtils.isNotBlank(resourceIdStr)){
					Long resourceId = Long.parseLong(resourceIdStr);
					//根据resourceceId查询privilegeId
					RabcPrivilege uPrivilege = rabcPrivilegeMapper.getByResourceId(resourceId);
					if(uPrivilege == null){
						throw new OaException("配置错误，请联系管理员！");
					}
					resourceMap.put("roleId", roleId);
					resourceMap.put("privilegeId", uPrivilege.getId());
					resourceMap.put("createTime", new Date());
					resourceMap.put("createUser", user.getEmployee().getId().toString());
					resourceMap.put("delflag", 0);
					resourceList.add(resourceMap);
				}
			}
		}
		//保存菜单
		if(resourceList != null && resourceList.size() > 0){
			rabcPrivilegeMapper.saveResource(resourceList);
		}
	}

	/**
	 * 删除角色
	 * @throws OaException 
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delRole(Long roleId) throws OaException {
		User user = userService.getCurrentUser();
		if(roleId == null){
			throw new OaException("请选择角色！");
		}
		//判断该角色是否绑定员工，如果有则不可删除
		Integer count = rabcRoleMapper.getUserCountByRoleId(roleId);
		if(count > 0){
			throw new OaException("该角色已绑定员工！");
		}
		//删除该角色所有菜单
		rabcPrivilegeMapper.deleteResourceByRoleId(roleId,user.getEmployee().getId().toString());
		RabcRole uRole = new RabcRole();
		uRole.setId(roleId);
		uRole.setUpdateTime(new Date());
		uRole.setUpdateUser(user.getEmployee().getId().toString());
		uRole.setDelFlag(1);
		//删除角色
		rabcRoleMapper.deleteRole(uRole);
	}

	
	@Override
	public List<Map<String, String>> getOperationTree(Integer resourceId, Long roleId) {
		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		// 查询所有操作资源
		RabcResource resource = new RabcResource();
		resource.setType(2);
		resource.setParentId(resourceId);
		List<RabcResource> resourceList = rabcResourceMapper.getListByCondition(resource);
		if (resourceList != null && resourceList.size() > 0) {
			for (RabcResource res : resourceList) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("id", res.getId().toString());
				map.put("pId", res.getParentId() == null ? "0" : res.getParentId().toString());
				map.put("name", res.getResourceName());
				map.put("rank", res.getRank() + "");
				resultList.add(map);
			}
		}
		return resultList;
	}


	@Override
	public List<Long> initAllOperationIds(Long roleId) {
		List<Long> resourceIds = rabcPrivilegeMapper.getOperationByPrivilege(roleId);
		return resourceIds;
	}
	
	/**
	 * 根据部门id查詢所有角色
	 */
	@Override
	public List<RabcRole> getAllURoleListByDepartId(Long departId) {
		List<RabcRole> uRoleList = rabcRoleMapper.getAllURoleListByDepartId(departId);
		return uRoleList;
	}

	/**
	 * 初始化部门角色
	 * @throws OaException 
	 */
	@Override
	public Map<String, String> initDepartRoles(Long departId,String departName,User user){
		
		Map<String, String> resultMap = new HashMap<String, String>();
		String directSupervisor = "直接主管";
		String departLeader = "部门负责人";
		String hrbp = "HRBP";
		String hrAdmin = "HR管理员";//初始化除员工签到外所有资源
		String superAdmin = "超级管理员";//所有资源
		Date createTime = new Date();
		List<Long> privilegeList = new ArrayList<Long>();
		
		//直接主管
		//查询直接主管默认拥有的所有资源
		privilegeList = rabcPrivilegeMapper.getListByBelongRole(directSupervisor);
		initRoleAndPrivilege(departId,directSupervisor,departName,createTime,user,privilegeList);
		//部门负责人
		//查询部门负责人默认拥有的所有资源
		privilegeList = rabcPrivilegeMapper.getListByBelongRole(departLeader);
		initRoleAndPrivilege(departId,departLeader,departName,createTime,user,privilegeList);
		//HRBP
		//查询HRBP默认拥有的所有资源
		privilegeList = rabcPrivilegeMapper.getListByBelongRole(hrbp);
		initRoleAndPrivilege(departId,hrbp,departName,createTime,user,privilegeList);
		//HR管理员
		//查询HR管理员默认拥有的所有资源
		privilegeList = rabcPrivilegeMapper.getListOfHrAdmin();
		initRoleAndPrivilege(departId,hrAdmin,departName,createTime,user,privilegeList);
		//超级管理员
		//查询超级管理员默认拥有的所有资源 
		privilegeList = rabcPrivilegeMapper.getAllList();
		initRoleAndPrivilege(departId,superAdmin,departName,createTime,user,privilegeList);
		
		resultMap.put("resultMsg", "初始化部门角色成功");
		return resultMap;
	}
	
	//根据部门和角色名称初始化该角色及其默认拥有的权限资源
	public void initRoleAndPrivilege(Long departId,String roleName,String departName,Date createTime,User user,List<Long> privilegeList){
		
		//创建角色
		RabcRole role = new RabcRole();
		role.setDepartId(departId);
		role.setName(roleName);
		role.setRemark(departName+"-"+roleName);
		role.setDelFlag(0);
		role.setCreateTime(createTime);
		role.setCreateUser(user.getEmployee().getCnName());
		rabcRoleMapper.save(role);
		
		//创建部门负责人所属权限
		List<Map<String,Object>> rolePrivilege = new ArrayList<Map<String,Object>>();
		for(Long privilegeId:privilegeList){
			Map<String,Object> data = new HashMap<String,Object>();
			data.put("roleId", role.getId());
			data.put("privilegeId", privilegeId);
			data.put("createTime", createTime);
			data.put("createUser", user.getEmployee().getCnName());
			data.put("delflag", 0L);
			rolePrivilege.add(data);
		}
		if(rolePrivilege!=null&&rolePrivilege.size()>0){
			rabcPrivilegeMapper.saveResource(rolePrivilege);
		}
		
	}
	
}
