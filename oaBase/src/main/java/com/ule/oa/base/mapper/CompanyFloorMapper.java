package com.ule.oa.base.mapper;

import java.util.List;

import com.ule.oa.base.po.CompanyFloor;
import com.ule.oa.base.util.jdbc.OaSqlMapper;
import com.ule.oa.common.persistence.SqlMapper;

public interface CompanyFloorMapper extends OaSqlMapper{
    
	List<CompanyFloor> getListByCondition(CompanyFloor model);
	
	CompanyFloor selectByPrimaryKey(Long id);

	int update(CompanyFloor model);
}