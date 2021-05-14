package com.ule.oa.base.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ule.oa.base.mapper.EmpTypeMapper;
import com.ule.oa.base.po.EmpType;
import com.ule.oa.base.service.EmpTypeService;

@Service
public class EmpTypeServiceImpl implements EmpTypeService {
	@Autowired
	private EmpTypeMapper empTypeMapper;
	
	@Override
	public int save(EmpType empType) {
		return empTypeMapper.save(empType);
	}

	@Override
	public int updateById(EmpType empType) {
		return empTypeMapper.save(empType);
	}

	@Override
	public List<EmpType> getListByCondition(EmpType empType){
		return empTypeMapper.getListByCondition(empType);
	}
	
	@Override
	public EmpType getByCondition(EmpType empType){
		List<EmpType> etList = getListByCondition(empType);
		
		if(null != etList && etList.size()>0){
			return etList.get(0);
		}
		
		return empType;
	}
}
