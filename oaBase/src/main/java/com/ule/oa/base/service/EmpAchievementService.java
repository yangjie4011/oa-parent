package com.ule.oa.base.service;

import java.util.List;

import com.ule.oa.base.po.EmpAchievement;
import com.ule.oa.common.exception.OaException;

public interface EmpAchievementService {
	public List<EmpAchievement> getListByCondition(EmpAchievement empAchievement);
	
	/**
	  * save保存整条数据
	  * @Title: save
	  * @param empAchievement
	  * @return    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	Integer save(EmpAchievement empAchievement);
	
	/**
	 * @throws OaException 
	  * 根据主键修改
	  * @Title: updateById
	  * @param empAchievement
	  * @return    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	Integer updateById(EmpAchievement empAchievement) throws OaException;
}
