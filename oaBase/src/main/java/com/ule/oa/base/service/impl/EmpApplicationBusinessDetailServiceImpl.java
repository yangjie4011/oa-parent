package com.ule.oa.base.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ule.oa.base.mapper.EmpApplicationBusinessDetailMapper;
import com.ule.oa.base.po.EmpApplicationBusinessDetail;
import com.ule.oa.base.service.EmpApplicationBusinessDetailService;


@Service
public class EmpApplicationBusinessDetailServiceImpl implements EmpApplicationBusinessDetailService{

	@Autowired
	private EmpApplicationBusinessDetailMapper empApplicationBusinessDetailMapper;
	@Override
	public List<EmpApplicationBusinessDetail> getListByCondition(Long businessId) {
		return empApplicationBusinessDetailMapper.getListByCondition(businessId);
	}

}
