package com.ule.oa.base.mapper;

import java.util.List;
import com.ule.oa.base.po.AttnUpdateLog;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

public interface AttnUpdateLogMapper extends OaSqlMapper{
	
	int insert(AttnUpdateLog attnUpdateLog);

    List<AttnUpdateLog> selectByCondition(AttnUpdateLog attnUpdateLog);
    
    Integer selectCountByCondition(AttnUpdateLog attnUpdateLog);

}
