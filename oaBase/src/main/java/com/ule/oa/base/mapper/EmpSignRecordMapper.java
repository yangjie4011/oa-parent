package com.ule.oa.base.mapper;

import com.ule.oa.base.po.AttnSignRecord;
import com.ule.oa.base.util.jdbc.OaSqlMapper;
import com.ule.oa.common.persistence.SqlMapper;

public interface EmpSignRecordMapper extends OaSqlMapper{
	
	int insert(AttnSignRecord signRecord);
	
	int updateByPrimaryKeySelective(AttnSignRecord record);
}