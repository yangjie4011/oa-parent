package com.ule.oa.base.service;

import java.util.List;

import com.ule.oa.base.po.EmpContract;

public interface EmpContractService {
	public List<EmpContract> getListByCondition(EmpContract empContract);
	public EmpContract getByCondition(EmpContract empContract);
	public int updateById(EmpContract empContract) throws Exception;
	

	/**
	  * save一条数据
	  * @Title: save
	  * @param empContract
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
	int save(EmpContract empContract);
}
