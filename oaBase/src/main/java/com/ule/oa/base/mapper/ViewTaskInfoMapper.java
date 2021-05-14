package com.ule.oa.base.mapper;


import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.tbl.ViewTaskInfoTbl;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

public interface ViewTaskInfoMapper extends BaseMapper<ViewTaskInfoTbl>,OaSqlMapper{
	
	int updateStatusById(ViewTaskInfoTbl viewTaskInfoTbl);
	
	List<ViewTaskInfoTbl> queryTasksByProcessIdList(@Param("processIdList")List<String> processIdList);
	
}
