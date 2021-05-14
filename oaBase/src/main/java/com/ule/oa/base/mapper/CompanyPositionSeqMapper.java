package com.ule.oa.base.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.CompanyPositionSeq;
import com.ule.oa.base.util.jdbc.OaSqlMapper;
import com.ule.oa.common.persistence.SqlMapper;

public interface CompanyPositionSeqMapper extends OaSqlMapper{
    int insert(CompanyPositionSeq companyPositionSeq);

    int updateById(CompanyPositionSeq companyPositionSeq);
    
    int updatePositionSeqById(CompanyPositionSeq companyPositionSeq);
    
	List<CompanyPositionSeq> getListByCondition(CompanyPositionSeq companyPositionSeq);

	Integer count(CompanyPositionSeq companyPositionSeq);

	List<CompanyPositionSeq> getByPagenation(CompanyPositionSeq companyPositionSeq);
	
	CompanyPositionSeq getById(Long id);
	
	CompanyPositionSeq getPositionSeqById(Long id);

	int queryCode(@Param("code")String code,@Param("id")int id);

	int queryName(@Param("name")String name,@Param("id")int id);
	
	List<CompanyPositionSeq> getListByEmployeeId(@Param("employeeId")Long employeeId);
}