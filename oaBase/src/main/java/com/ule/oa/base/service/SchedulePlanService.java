package com.ule.oa.base.service;

import java.util.List;

import com.ule.oa.base.po.SchedulePlan;
import com.ule.oa.common.utils.PageModel;

public interface SchedulePlanService {
	/**
	  * save(班次设置信息)
	  * @Title: save
	  * @Description: 保存班次设置信息
	  * @param schedulePlan    设定文件
	  * void    返回类型
	  * @throws
	 */
	public void save(SchedulePlan schedulePlan);
	
	/**
	  * updateById(更新班次设置信息)
	  * @Title: updateById
	  * @Description: 根据主键修改班次设置信息
	  * @param config
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
	public int updateById(SchedulePlan schedulePlan);
	
	/**
	  * getListByCondition(根据条件获取所有班次设置信息)
	  * @Title: getListByCondition
	  * @Description: 根据条件获取所有班次设置信息
	  * @param schedulePlan
	  * @return    设定文件
	  * List<SchedulePlan>    返回类型
	  * @throws
	 */
	public List<SchedulePlan> getListByCondition(SchedulePlan schedulePlan);
	
	/**
	 * 根据id获取班次设置信息
	 * @param id
	 * @return
	 */
	public SchedulePlan getById(Long id); 
	
	/**
	 * 分页查询
	  * getByPagenation(这里用一句话描述这个方法的作用)
	  * @Title: getByPagenation
	  * @Description: 
	  * @param schedulePlan
	  * @return    设定文件
	  * PageModel<com.ule.oa.web.po.SchedulePlan>    返回类型
	  * @throws
	 */
	public PageModel<SchedulePlan> getByPagenation(SchedulePlan schedulePlan);
	
	

}
