package com.ule.oa.base.service;

import java.util.List;
import java.util.Map;

import com.ule.oa.base.po.RabcRole;
import com.ule.oa.base.po.User;
import com.ule.oa.common.exception.OaException;

public interface RabcRoleService {

	List<RabcRole> getAllURoleList();

	List<Map<String, Object>> getRoleResourceTree(Long roleId);

	RabcRole getRoleInfo(Long roleId);

	void addRole(RabcRole uRole) throws OaException;

	void saveResource(Long roleId, String resourceIds) throws OaException;

	void delRole(Long roleId) throws OaException;

	List<Map<String, String>> getOperationTree(Integer resourceId,Long roleId);

	List<Long> initAllOperationIds(Long roleId);

	List<RabcRole> getAllURoleListByDepartId(Long departId);

	Map<String, String> initDepartRoles(Long departId,String departName,User user);


}
