package com.ule.oa.base.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ule.oa.base.po.AttnStatistics;
import com.ule.oa.base.po.AttnWorkHours;
import com.ule.oa.common.exception.OaException;

public interface AttnStatisticsService {

	/**
	  * 批量保存考勤统计数据
	  * @Title: saveBatch
	  * @param attnStatisticsList
	  * @return    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	Integer saveBatch(List<AttnStatistics> attnStatisticsList);

	/**
	  * 定时计算考勤统计数据
	  * @Title: saveBatch
	  * @param attnStatisticsList
	  * @throws
	 */
	void setAttStatistics(AttnStatistics condition);
	
	/**
	  * 获取单据数据里面的考勤数据（请假，调休...）,先保存到明细表，再重新计算考勤
	  * @Title: setAttStatistics
	  * @param condition    设定文件
	  * void    返回类型
	  * @throws
	 */
	void setAttStatisticsForm(AttnWorkHours condition);
	
    void calculateAttnForm(Long companyId, Long employeeId, Date formDate, Date yesterday);
	
	
	/**
	 * @throws OaException 
	  * 查询考勤数据列表
	  * @Title: getAttStatisticsList
	  * @param condition    设定文件
	  * void    返回类型
	  * @throws
	 */
	List<AttnStatistics> getAttStatisticsList(AttnStatistics condition) throws OaException;

	/**
	 * @throws OaException 
	  * 查询共出勤，共应出勤
	  * @Title: getTotalAttStatistics
	  * @param condition
	  * @return    设定文件
	  * AttnStatistics    返回类型
	  * @throws
	 */
	AttnStatistics getTotalAttStatistics(AttnStatistics condition) throws OaException;

	/**
	 * @throws OaException 
	  * 查询月度总共应出勤（整月）
	  * @Title: getTotalAttStatistics
	  * @param condition
	  * @return    设定文件
	  * AttnStatistics    返回类型
	  * @throws
	 */
	AttnStatistics getTotalMustAtten(AttnStatistics condition,Boolean firstDayOfMonth) throws OaException;
	
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
	  * 条件查询单个考勤数据
	  * @Title: getAttnStatistics
	  * @param employId
	  * @param attnDate
	  * @return    设定文件
	  * AttnStatistics    返回类型
	  * @throws
	 */
	AttnStatistics getAttnStatistics(Long employId,Date attnDate);

	/**
	  * 查询下属员工的考勤
	  * @Title: getSubAttStatisticsList
	  * @param condition
	  * @return    设定文件
	  * List<Map<?,?>>    返回类型
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
	 * @throws Exception 
	  * 根据条件导出数据并发送邮件
	  * @Title: exportData
	  * @param reportId
	  * @param startTime
	  * @param endTime
	  * @param departId    设定文件
	  * void    返回类型
	  * @throws
	 */
	void exportData(String reportId, Date startTime, Date endTime, Long departId) throws Exception;

	/**
	  * 根据ID更新
	  * @Title: updateById
	  * @param attnStatistics
	  * @return    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	Integer updateById(AttnStatistics attnStatistics);

	/**
	 * 得到月度总共应出勤
	 * @param condition
	 * @return
	 * @throws OaException 
	 */
	AttnStatistics getMonthTotalMustAtten(AttnStatistics condition) throws OaException;
	
	/**
	 * @throws Exception 
	  * attnExMsgRemind(异常考勤提醒)
	  * @Title: attnExMsgRemind
	  * @Description: 异常考勤提醒()
	  * void    返回类型
	  * @throws
	 */
	public void attnExMsgRemind() throws Exception;

	/***
	 * 包含考勤明细的考勤列表
	 * @param condition
	 * @return
	 * @throws OaException 
	 */
	List<Map<String, Object>> getAttStatisticsDetailList(
			AttnStatistics condition) throws OaException;
	
	/***
	 * 导出月份假期
	 * @param condition
	 * @return
	 * @throws OaException 
	 */
	XSSFWorkbook exportMonthLeaveDetail(Date startTime, Date endTime,
			Long departId) throws Exception;
	
	/**
	  * getActAttnTimeGroupByemployIds(按时间统计多个员工的实际出勤工时==标准出勤工时)
	  * @Title: getActAttnTimeGroupByemployIds
	  * @Description: 按时间统计多个员工的实际出勤工时==标准出勤工时
	  * @param resultAttnStatistics
	  * @return    设定文件
	  * List<AttnStatistics>    返回类型
	  * @throws
	 */
	List<AttnStatistics> getActAttnTimeGroupByemployIds(List<Long> employIdList, Date startDate,Date endDate);

	void recalculationAttForRemoveSubAbsence(AttnWorkHours condition);

}
