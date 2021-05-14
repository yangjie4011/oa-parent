package com.ule.oa.base.mapper;

import java.util.List;

import com.ule.oa.base.po.CoopCompany;
import com.ule.oa.base.util.jdbc.OaSqlMapper;
import com.ule.oa.common.persistence.SqlMapper;

public interface CoopCompanyMapper extends OaSqlMapper{
    int save(CoopCompany coopCompany);

    int updateById(CoopCompany coopCompany);
    
    List<CoopCompany> getListByCondition(CoopCompany coopCompany);
}