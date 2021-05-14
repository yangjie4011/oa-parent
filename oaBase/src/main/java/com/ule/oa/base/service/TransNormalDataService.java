package com.ule.oa.base.service;

import java.util.List;

import com.ule.oa.base.po.TransNormal;

public interface TransNormalDataService {
	
	Integer getTotalRows(TransNormal transNormal);
	
	List<TransNormal> getTransNormalList(TransNormal transNormal);
	
	//根据指纹id查询打卡记录
	List<TransNormal> getListByCardId(TransNormal transNormal);

}
