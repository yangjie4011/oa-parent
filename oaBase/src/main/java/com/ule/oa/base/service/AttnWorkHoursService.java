package com.ule.oa.base.service;

import java.util.List;
import java.util.Map;

import com.ule.oa.base.po.AttnWorkHours;

public interface AttnWorkHoursService {

	/**
	 * @param createUser 
	 * @param currentTime 
	 * @param uleId 
	  * saveTransToAttnWorkBatch数据迁移
	  * @Title: saveTransToAttnWorkBatch
	  * @param list
	  * @return    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	/*List<AttnWorkHours> getTransWorkCollect(AttnWorkHours attnWorkHours);*/

	/**
	  * 根据条件获得员工的考勤数据
	  * @Title: getTransCollectByDate
	  * @param conditon
	  * @return    设定文件
	  * AttnWorkHours    返回类型
	  * @throws
	 */
	/*AttnWorkHours getTransCollectByDate(AttnWorkHours conditon);*/
	
	/**
	  * 批量保存考勤日志数据
	  * @Title: saveBatch
	  * @param attnWorkHours
	  * @return    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	Integer saveBatch(List<AttnWorkHours> attnWorkHoursList);

	/**
	  * 根据条件批量获得员工的考勤数据
	  * @Title: getTransMapByDate
	  * @param hoursCondition
	  * @return    设定文件
	  * Map<Long,AttnWorkHours>    返回类型
	  * @throws
	 */
	Map<Long, AttnWorkHours> getTransMapByDate(AttnWorkHours condition);

	
	/**
	  * 将原始考勤数据汇总到
	  * @Title: setWorkHours
	  * @param object    设定文件
	  * void    返回类型
	  * @throws
	 */
	void setWorkHours(AttnWorkHours condition);

	/**
	  * 根据员工，工作日期，分组查询该日期的考勤数据
	  * @Title: getAttnWorkHoursGroupByDate
	  * @param hoursCondition
	  * @return    设定文件
	  * AttnWorkHours    返回类型
	  * @throws
	 */
	AttnWorkHours getAttnWorkHoursGroupByDate(AttnWorkHours hoursCondition);

	/**
	  * 保存一条考勤数据
	  * @Title: save
	  * @param condition    设定文件
	  * void    返回类型
	  * @throws
	 */
	void save(AttnWorkHours condition);

	/**
	  * 根据ID更新
	  * @Title: updateById
	  * @param attnWorkHours    设定文件
	  * void    返回类型
	  * @throws
	 */
	void updateById(AttnWorkHours attnWorkHours);
	
	/**
	 * 得到考勤明细的列表
	 * @param attnWorkHours
	 * @return
	 */
	List<AttnWorkHours> getAttnWorkHoursList(AttnWorkHours attnWorkHours);

	/**
	 * 根据条件取消充过的考勤
	 * @param condition
	 */
	void cancelAttnWorkHours(AttnWorkHours condition);
	
	/**
	 * 查询绝对修改考勤明细列表
	 * @param attnWorkHours
	 * @return
	 */
	List<AttnWorkHours> getAbsoluteAttnWorkHoursList(AttnWorkHours attnWorkHours);
	
	/**
	 * 得到考勤明细的列表
	 * @param attnWorkHours
	 * @return
	 */
	List<AttnWorkHours> getListByCondition(AttnWorkHours attnWorkHours);


}
