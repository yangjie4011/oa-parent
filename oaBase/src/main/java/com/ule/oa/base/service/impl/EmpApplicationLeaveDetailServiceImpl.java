package com.ule.oa.base.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ule.oa.base.mapper.EmpApplicationLeaveDetailMapper;
import com.ule.oa.base.po.EmpApplicationLeaveDetail;
import com.ule.oa.base.service.EmpApplicationLeaveDetailService;

@Service
public class EmpApplicationLeaveDetailServiceImpl implements EmpApplicationLeaveDetailService{

	@Autowired
	private EmpApplicationLeaveDetailMapper empApplicationLeaveDetailMapper;
	
	@Override
	public List<EmpApplicationLeaveDetail> getList(Long leaveId) {
		return empApplicationLeaveDetailMapper.getListByCondition(leaveId);
	}

}
