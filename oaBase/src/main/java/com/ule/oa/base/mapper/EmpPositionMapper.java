package com.ule.oa.base.mapper;

import java.util.List;

import com.ule.oa.base.po.EmpPosition;
import com.ule.oa.base.util.jdbc.OaSqlMapper;
import com.ule.oa.common.persistence.SqlMapper;

public interface EmpPositionMapper extends OaSqlMapper{
	public void save(EmpPosition model);
	
	public int updateById(EmpPosition config);
	
	public List<EmpPosition> getListByCondition(EmpPosition config);
	
	public EmpPosition getById(Long id);
	
	public List<EmpPosition> getListByPositionName(EmpPosition empPosition);
	
	int updateByEmployeeId(EmpPosition config);
}