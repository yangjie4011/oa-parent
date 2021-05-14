package com.ule.oa.base.service;

import java.util.List;

import com.ule.oa.base.po.AttnUsers;

public interface AttnUsersService {

	/**
	  * 保存一条数据
	  * @Title: save
	  * @param attnUsers
	  * @return    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	Integer save(AttnUsers attnUsers);

	/**
	 * @return 
	  * 批量保存数据
	  * saveBatch(这里用一句话描述这个方法的作用)
	  * @Title: saveBatch
	  * @param list    设定文件
	  * void    返回类型
	  * @throws
	 */
	Integer saveBatch(List<AttnUsers> list);
	
	//根据指纹ID查询用户
	AttnUsers selectByFingerprintId(Integer fingerprintId);
	
	void updateById(AttnUsers attnUsers);

	void bindFingerPrint(String fingerPrint, String empId);
}
