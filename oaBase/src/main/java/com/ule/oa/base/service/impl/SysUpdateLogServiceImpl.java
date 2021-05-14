package com.ule.oa.base.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ule.oa.base.mapper.SysUpdateLogMapper;
import com.ule.oa.base.mapper.ViewMyTaskMapper;
import com.ule.oa.base.po.tbl.SysUpdateLogTbl;
import com.ule.oa.base.po.tbl.ViewTaskInfoTbl;
import com.ule.oa.base.service.SysUpdateLogService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.service.ViewMyTaskService;

import tk.mybatis.mapper.entity.Example;

@Service
public class SysUpdateLogServiceImpl implements SysUpdateLogService{
	
	
	@Autowired
	private SysUpdateLogMapper sysUpdateLogMapper;
	@Autowired
	private UserService userService;
	
	@Override
	@Transactional
	public int save(SysUpdateLogTbl sysUpdateLog) {
		return sysUpdateLogMapper.insert(sysUpdateLog);
	}
	@Override
	public List<SysUpdateLogTbl> queryByCondition(SysUpdateLogTbl sysUpdateLog) {
		Example example = new Example(SysUpdateLogTbl.class);
		example.setOrderByClause("create_time desc");
		example.createCriteria().andEqualTo("resourceEmployeeId", sysUpdateLog.getResourceEmployeeId())
								.andEqualTo("resourceType",sysUpdateLog.getResourceType());
		return this.sysUpdateLogMapper.selectByExample(example);
	}
	
	
}
