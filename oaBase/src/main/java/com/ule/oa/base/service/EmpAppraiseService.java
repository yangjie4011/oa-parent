package com.ule.oa.base.service;

import java.util.List;

import com.ule.oa.base.po.EmpAppraise;

public interface EmpAppraiseService {
	public List<EmpAppraise> getListByCondition(EmpAppraise empAppraise);
	
	/**
	  * 保存一条数据
	  * @Title: save
	  * @param empAppraise
	  * @return    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	Integer save(EmpAppraise empAppraise);
	
	/**
	  * 跟新一条数据
	  * @Title: updateById
	  * @param empAppraise
	  * @return
	  * @throws Exception    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	Integer updateById(EmpAppraise empAppraise) throws Exception;
}
