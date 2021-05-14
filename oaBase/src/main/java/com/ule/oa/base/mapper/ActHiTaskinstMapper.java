package com.ule.oa.base.mapper;

import java.util.List;

import com.ule.oa.base.po.ActHiTaskinst;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

public interface ActHiTaskinstMapper extends OaSqlMapper{
	
	List<ActHiTaskinst> selectByCondition(ActHiTaskinst actHiTaskinst);

}
