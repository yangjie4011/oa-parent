package com.ule.oa.base.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ule.oa.base.mapper.ActPermissionMapper;
import com.ule.oa.base.po.ActPermission;
import com.ule.oa.base.service.ActPermissionService;

@Service
public class ActPermissionServiceImpl implements ActPermissionService{

	@Resource
	private ActPermissionMapper actPermissionMapper;
	
	@Override
	public List<Long> queryEmpIdByPermission(ActPermission actPermission) {
		return actPermissionMapper.getEmpIdByPermission(actPermission);
	}

	@Override
	public int insertPermission(ActPermission actPermission) {
		return actPermissionMapper.insert(actPermission);
	}

	@Override
	public List<ActPermission> queryListByPermission(ActPermission actPermission) {
		return actPermissionMapper.select(actPermission);
	}
	
	@Override
	public ActPermission queryOneByPermission(ActPermission actPermission) {
		return actPermissionMapper.selectOne(actPermission);
	}
}
