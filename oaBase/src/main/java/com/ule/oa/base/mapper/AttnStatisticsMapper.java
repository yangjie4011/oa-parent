package com.ule.oa.base.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.AttnStatistics;
import com.ule.oa.base.po.AttnWorkHours;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

public interface AttnStatisticsMapper extends OaSqlMapper{

    /**
      * saveBatch批量保存考勤统计数据
      * @Title: saveBatch
      * @param attnStatisticsList
      * @return    设定文件
      * Integer    返回类型
      * @throws
     */
	Integer saveBatch(@Param("list")List<AttnStatistics> attnStatisticsList);

	/**
	  * 查询员工汇总的打卡数据，从attn_work_hours获取
	  * @Title: getAttnStatisticsList
	  * @param hoursCondition
	  * @return    设定文件
	  * List<AttnStatistics>    返回类型
	  * @throws
	 */
	List<AttnStatistics> getAttnStatisticsListByAttnWork(AttnWorkHours hoursCondition);//list
	AttnStatistics getAttnStatisticsByAttnWork(AttnWorkHours hoursCondition);//单个
	
	/**
	  * 查询考勤的单据数据(请假，调休...)
	  * @Title: getAttnStatisticsFormList
	  * @param hoursCondition
	  * @return    设定文件
	  * List<AttnStatistics>    返回类型
	  * @throws
	 */
	List<AttnStatistics> getAttnStatisticsList(AttnWorkHours hoursCondition);//list
	//AttnStatistics getAttnStatisticsForm(AttnWorkHours hoursCondition);//单个

	/**
	  * 条件查询单个考勤数据
	  * @Title: getAttnStatisticsByCondition
	  * @param statisticsCondition
	  * @return    设定文件
	  * AttnStatistics    返回类型
	  * @throws
	 */
	AttnStatistics getAttnStatisticsByCondition(AttnStatistics statisticsCondition);

	/**
	  * updateAttnStatistics(这里用一句话描述这个方法的作用)
	  * @Title: updateAttnStatistics
	  * @param existAttStatistics    设定文件
	  * void    返回类型
	  * @throws
	 */
	Integer updateById(AttnStatistics existAttStatistics);

	/**
	  * 查询考勤数据
	  * @Title: getAttStatisticsList
	  * @param condition
	  * @return    设定文件
	  * List<AttnWorkHours>    返回类型
	  * @throws
	 */
	List<AttnStatistics> getAttStatisticsList(AttnStatistics condition);

	/**
	  * 查询汇总的应出勤，总出勤
	  * @Title: getTotalAttnMap
	  * @param condition
	  * @return    设定文件
	  * Map    返回类型
	  * @throws
	 */
	AttnStatistics getTotalAttStatistics(AttnStatistics condition);
	
	/**
	  * 查询标准工时汇总的应出勤，总出勤
	  * @Title: getTotalAttnMap
	  * @param condition
	  * @return    设定文件
	  * Map    返回类型
	  * @throws
	 */
	AttnStatistics getStandardTotalAttStatistics(AttnStatistics condition);
	
	List<AttnStatistics> getStandardAttStatisticsList(AttnStatistics condition);

	/**
	  * 查询下属员工的考勤
	  * @Title: getSubAttStatisticsList
	  * @param condition
	  * @return    设定文件
	  * List<Map<String,String>>    返回类型
	  * @throws
	 */
	List<Map<String, String>> getSubAttStatisticsList(AttnStatistics condition);

	/**
	  * 保存单条考勤数据
	  * @Title: save
	  * @param attnStatistics
	  * @return    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	Integer save(AttnStatistics attnStatistics);

	/**
	  * 得到各种考勤异常类型的人数
	  * @Title: getNumberByType
	  * @param condition
	  * @return    设定文件
	  * Map<String,String>    返回类型
	  * @throws
	 */
	Map<String, String> getNumberByType(AttnStatistics condition);

	/**
	  * 查询员工异常类型（迟到早退缺勤旷工）次数
	  * @Title: getEmpAttnTimes
	  * @param condition
	  * @return    设定文件
	  * List<Map<String,String>>    返回类型
	  * @throws
	 */
	List<Map<String, String>> getEmpAttnTimes(AttnStatistics condition);

	/**
	  * 报表查询--员工考勤明细
	  * @Title: getEmpAttnDetailReport
	  * @param startTime
	  * @param endTime
	  * @param departId
	  * @return    设定文件
	  * List<Object>    返回类型
	  * @throws
	 */
	List<Map<String, Object>> getEmpAttnDetailReport(@Param("startTime")Date startTime, @Param("endTime")Date endTime,@Param("departId")Long departId,
			@Param("empTypeIdList")List<Long> empTypeIdList);
	
	/**
	 * 报表查询--员工考勤明细(减少表关联)
	 * @param startTime
	 * @param endTime
	 * @param departId
	 * @param empTypeIdList
	 * @return
	 */
	List<AttnStatistics> getYGKQMXReport(@Param("startTime")Date startTime, @Param("endTime")Date endTime,@Param("departId")Long departId,
			@Param("empTypeIdList")List<Long> empTypeIdList);

	/**
	  * 查询已经存在的考勤数据
	  * @Title: getExistIdStatistics
	  * @param resultAttnStatistics
	  * @return    设定文件
	  * Long    返回类型
	  * @throws
	 */
	Long getExistIdStatistics(AttnStatistics resultAttnStatistics);

	/**
	 * 报表查询--月度缺勤统计
	 * @param startTime
	 * @param endTime
	 * @param departId
	 * @return
	 */
	List<Map<String, Object>> getEmpAttnMonthLack(@Param("startTime")Date startTime, @Param("endTime")Date endTime,@Param("departId")Long departId,@Param("empTypeIdList")List<Long> empTypeIdList);

	/**
	 * 报表查询--月度缺勤统计
	 * @param startTime
	 * @param endTime
	 * @param departId
	 * @return
	 */
	List<Map<String, Object>> getEmpAttnMonthLackDetail(@Param("startTime")Date startTime, @Param("endTime")Date endTime,@Param("departId")Long departId,@Param("empTypeIdList")List<Long> empTypeIdList);

	/**
	  * getAttnExReaminList(获得异常考勤员工列表)
	  * @Title: getAttnExReaminList
	  * @Description: 获得异常考勤员工列表
	  * @param resultAttnStatistics
	  * @return    设定文件
	  * List<AttnStatistics>    返回类型
	  * @throws
	 */
	List<AttnStatistics> getAttnExReaminList(AttnStatistics attnStatistics);

	void deleteOriginalBillWorkHour(@Param("startTime")Date startTime, @Param("endTime")Date endTime, @Param("employeeId")Long employeeId);
	
	/**
	  * 条件查询单个考勤数据
	  * @Title: getAttnStatistics
	  * @param employId
	  * @param attnDate
	  * @return    设定文件
	  * AttnStatistics    返回类型
	  * @throws
	 */
	AttnStatistics getAttnStatistics(@Param("employId")Long employId, @Param("attnDate")Date attnDate);
	
	/**
	  * getActAttnTimeGroupByemployIds(按时间统计多个员工的实际出勤工时==标准出勤工时)
	  * @Title: getActAttnTimeGroupByemployIds
	  * @Description: 按时间统计多个员工的实际出勤工时==标准出勤工时
	  * @param resultAttnStatistics
	  * @return    设定文件
	  * List<AttnStatistics>    返回类型
	  * @throws
	 */
	List<AttnStatistics> getActAttnTimeGroupByemployIds(@Param("employIdList")List<Long> employIdList, @Param("startDate")Date startDate, @Param("endDate")Date endDate);
	
	
}