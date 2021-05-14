package com.ule.oa.base.mapper;

import java.util.List;

import com.ule.oa.base.po.CompanySeat;
import com.ule.oa.base.po.tbl.CompanySeatTbl;
import com.ule.oa.base.util.jdbc.OaSqlMapper;
import com.ule.oa.common.persistence.SqlMapper;

public interface CompanySeatMapper extends OaSqlMapper{
    int deleteByPrimaryKey(Long id);

    int insert(CompanySeatTbl record);

    int insertSelective(CompanySeatTbl record);

    CompanySeatTbl selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CompanySeatTbl record);

    int updateByPrimaryKey(CompanySeatTbl record);
    
    List<CompanySeat> getListByCondition(CompanySeat model);
}