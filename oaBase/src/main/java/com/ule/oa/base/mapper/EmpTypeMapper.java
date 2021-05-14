package com.ule.oa.base.mapper;

import java.util.List;

import com.ule.oa.base.po.EmpType;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

public interface EmpTypeMapper extends OaSqlMapper{
    int save(EmpType record);

    int updateById(EmpType record);
    
    List<EmpType> getListByCondition(EmpType empType);
    
    EmpType getById(Long id);
}