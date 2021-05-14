package com.ule.oa.base.mapper;

import com.ule.oa.base.po.QuitHistory;
import com.ule.oa.base.util.jdbc.OaSqlMapper;
import com.ule.oa.common.persistence.SqlMapper;


//离职历史表
public interface QuitHistoryMapper extends OaSqlMapper{
	
	int save(QuitHistory quitHistory);

}
