package com.ule.oa.base.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.AttnWorkHours;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

public interface AttnWorkHoursMapper extends OaSqlMapper{
	
	/**
	  * getTransCollectByDate汇总某天打卡时间
	  * @Title: getTransCollectByDate
	  * @param dateBegin
	  * @param dateEnd
	  * @return    设定文件
	  * AttnWorkHours    返回类型
	  * @throws
	 */
	AttnWorkHours getTransCollectByDate(AttnWorkHours conditon);

	/**
	  * saveBatch批量保存
	  * @Title: saveBatch
	  * @param attnWorkHoursList
	  * @return    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	Integer saveBatch(@Param("list")List<AttnWorkHours> attnWorkHoursList);

	/**
	  * 获取多个员工在某个日期的打卡数据
	  * @Title: getTransListByDate
	  * @param condition
	  * @return    设定文件
	  * List<AttnWorkHours>    返回类型
	  * @throws
	 */
	List<AttnWorkHours> getTransListByDate(AttnWorkHours condition);

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
	  * save保存
	  * @Title: save
	  * @param attnWorkHours    设定文件
	  * void    返回类型
	  * @throws
	 */
	void save(AttnWorkHours attnWorkHours);

	/**
	  * 判段某个日期某个员工是否已存在考勤
	  * @Title: getExistCountWorkHours
	  * @param attnWorkHours
	  * @return    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	List<Long> getExistIdWorkHours(AttnWorkHours attnWorkHours);

	/**
	  * 根据ID更新数据
	  * @Title: updateById
	  * @param attnWorkHours    设定文件
	  * void    返回类型
	  * @throws
	 */
	void updateById(AttnWorkHours attnWorkHours);
	
	void updateById1(@Param("id")Long id,@Param("delFlag")Integer delFlag);

	/**
	 * 查询考勤明细列表
	 * @param attnWorkHours
	 * @return
	 */
	List<AttnWorkHours> getAttnWorkHoursList(AttnWorkHours attnWorkHours);
	
	/**
	 * 查询需要修复数据的列表
	 * @return
	 */
	List<AttnWorkHours> getNeedRepairList();
	
	/**
	 * 查询绝对修改考勤明细列表
	 * @param attnWorkHours
	 * @return
	 */
	List<AttnWorkHours> getAbsoluteAttnWorkHoursList(AttnWorkHours attnWorkHours);
	
	/**
	 * 查询实际打卡考勤明细列表
	 * @param attnWorkHours
	 * @return
	 */
	List<AttnWorkHours> getClockedAttnWorkHoursList(AttnWorkHours attnWorkHours);

	/**
	 * 取消充进来的单据
	 * @param condition
	 */
	void cancelAttnWorkHours(AttnWorkHours condition);
	
	/**
	  * 根据条件删除数据
	  * @Title: deleteByCondition
	  * @param attnWorkHours    设定文件
	  * void    返回类型
	  * @throws
	 */
	void deleteByCondition(AttnWorkHours attnWorkHours);
	
	/**
	  * 根据单据Id删除数据
	  * @Title: deleteByBillId
	  * @param attnWorkHours    设定文件
	  * void    返回类型
	  * @throws
	 */
	void deleteByBillId(AttnWorkHours attnWorkHours);
	
	List<AttnWorkHours> getListByCondition(AttnWorkHours attnWorkHours);
	
	/**
	 * 删除远程消异常数据
	 * @param attnWorkHours
	 */
	void deleteRemoteAbnormalRemoveDate(AttnWorkHours attnWorkHours);
	
	List<AttnWorkHours> getListByDate(@Param("startDate")Date startDate, @Param("endDate")Date endDate);
	
	AttnWorkHours getRemoteAbnormalRemoveDate(@Param("workDate")Date workDate,@Param("employeeId")Long employeeId);
	
	void repairDate(@Param("startTime")Date startTime,@Param("endTime")Date endTime,@Param("id")Long id);
	
}