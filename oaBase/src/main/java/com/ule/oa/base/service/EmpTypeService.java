package com.ule.oa.base.service;

import java.util.List;

import com.ule.oa.base.po.EmpType;

public interface EmpTypeService {
	public int save(EmpType empType);

	public int updateById(EmpType empType);
	
	public List<EmpType> getListByCondition(EmpType empType);
	
	public EmpType getByCondition(EmpType empType);
}
