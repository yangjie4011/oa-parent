package com.ule.oa.base.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.CompanyPositionLevel;
import com.ule.oa.base.util.jdbc.OaSqlMapper;
import com.ule.oa.common.persistence.SqlMapper;

public interface CompanyPositionLevelMapper extends OaSqlMapper{

    int insert(CompanyPositionLevel companyPositionLevel);

    int updateById(CompanyPositionLevel companyPositionLevel);
    
	List<CompanyPositionLevel> getListByCondition(CompanyPositionLevel companyPositionLevel);

	Integer count(CompanyPositionLevel companyPositionLevel);

	List<CompanyPositionLevel> getByPagenation(CompanyPositionLevel companyPositionLevel);
	
	CompanyPositionLevel getById(Long id);

	int queryCode(@Param("code")String code,@Param("id")int id);

	int queryName(@Param("name")String name,@Param("id")int id);

	Integer updatePositionLevelById(CompanyPositionLevel companyPositionLevel);

}