package com.ule.oa.base.service;

import java.util.List;

import com.ule.oa.base.po.ActPermission;

public interface ActPermissionService {

	/**
	 * 根据流程节点查询审批人Id
	 * @param actPermission
	 * @return
	 */
	List<Long> queryEmpIdByPermission(ActPermission actPermission);
	
	/**
	 * 根据条件查询流程节点
	 * @param actPermission
	 * @return
	 */
	List<ActPermission> queryListByPermission(ActPermission actPermission);
	
	/**
	 * 根据条件查询单个节点
	 * @param actPermission
	 * @return
	 */
	ActPermission queryOneByPermission(ActPermission actPermission);
	
	/**
	 * 保存单个流程节点
	 * @param actPermission
	 * @return
	 */
	int insertPermission(ActPermission actPermission);
	
	
}
