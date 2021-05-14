package com.ule.oa.base.mapper;

import java.util.List;

import com.ule.oa.base.po.TransNormal;
import com.ule.oa.base.util.jdbc.AttenceSqlMapper;

public interface TransNormalMapper extends AttenceSqlMapper{

	Integer getTotalRows(TransNormal transNormal);

	List<TransNormal> getTransNormalList(TransNormal transNormal);
	
	//根据指纹id查询打卡记录
	List<TransNormal> getListByCardId(TransNormal transNormal);
}