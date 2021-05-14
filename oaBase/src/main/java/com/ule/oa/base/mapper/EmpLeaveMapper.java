package com.ule.oa.base.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.EmpLeave;
import com.ule.oa.base.util.jdbc.OaSqlMapper;
import com.ule.oa.common.persistence.SqlMapper;

public interface EmpLeaveMapper extends OaSqlMapper{
    int updateById(EmpLeave record);
    
    int save(EmpLeave record);

    EmpLeave getById(Long id);
    
    /**
      * getTotalReaminLeaveOther(获取事假和其它假剩余假期)
      * @Title: getTotalReaminLeaveOther
      * @Description: 获取事假和其它假剩余假期
      * @param record
      * @return    设定文件
      * EmpLeave    返回类型
      * @throws
     */
    EmpLeave getTotalReaminLeaveOther(EmpLeave record);
    
    /**
      * getTotalReaminLeave(获得总的剩余假期)
      * @Title: getTotalReaminLeave
      * @Description: 获得总的剩余假期
      * @param record
      * @return    设定文件
      * EmpLeave    返回类型
      * @throws
     */
    EmpLeave getTotalReaminLeave(EmpLeave record);
    
    /**
      * getReaminLeaveList(获得剩余假期明细)
      * @Title: getReaminLeaveList
      * @Description: 获得剩余假期明细
      * @param record
      * @return    设定文件
      * List<Employee>    返回类型
      * @throws
     */
    List<EmpLeave> getReaminLeaveList(EmpLeave record);

    /**
     * 我的假期页面专用，请勿改动--zhangjintao
     * @Title: getMyViewLeave
     * @Description: 假期汇总
     * @param empId 员工ID
     * @param today 日期
     * @param typeList  假期类型list
     * @return    设定文件
     * List<EmpLeave>    返回类型
     * @throws
    */
	List<EmpLeave> getMyViewLeave(@Param("employeeId")Long employeeId, @Param("dateList")List<Date> dateList, @Param("typeList")List<Integer> typeList);
	
	/**
	  * getListByCondition(这里用一句话描述这个方法的作用)
	  * @Title: getListByCondition
	  * @Description: TODO
	  * @param empLeave
	  * @return    设定文件
	  * List<EmpLeave>    返回类型
	  * @throws
	 */
	List<EmpLeave> getListByCondition(EmpLeave empLeave);
	
	/**
	  * getPageList(这里用一句话描述这个方法的作用)
	  * @Title: getListByCondition
	  * @Description: TODO
	  * @param empLeave
	  * @return    设定文件
	  * List<EmpLeave>    返回类型
	  * @throws
	 */
	List<EmpLeave> getPageList(EmpLeave empLeave);
	
	/**
	  * getPageList(查询所有员工信息集合)
	  * @Title: getListByCondition
	  * @Description: TODO
	  * @param empLeave
	  * @return    设定文件
	  * List<EmpLeave>    返回类型
	  * @throws
	 */
	List<EmpLeave> getEmpPageList(EmpLeave empLeave);
	
	/**
	  * getCount(这里用一句话描述这个方法的作用)
	  * @Title: getListByCondition
	  * @Description: TODO
	  * @param empLeave
	  * @return    设定文件
	  * int  返回类型
	  * @throws
	 */
	
	Integer getEmpPageCount(EmpLeave empLeave);
	
	/**
	  * getCount(这里用一句话描述这个方法的作用)
	  * @Title: getListByCondition
	  * @Description: TODO
	  * @param empLeave
	  * @return    设定文件
	  * int  返回类型
	  * @throws
	 */
	
	Integer getCount(EmpLeave empLeave);
	
	/**
	  * saveNextLeaveByCondition(生成下一年度的假期)
	  * @Title: saveNextLeaveByCondition
	  * @Description: 生成下一年度的年假
	  * @param empLeaveList
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
	int saveNextLeaveByCondition(List<EmpLeave> empLeaveList);
	
	/**
	  * delNextLeaveByCondition(删除下一年度的假期数据---逻辑删除)
	  * @Title: delNextLeaveByCondition
	  * @Description: 删除下一年度的假期数据---逻辑删除
	  * @param empLeave
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
	int delNextLeaveByCondition(EmpLeave empLeave);
	
	/**
	  * getNewLeaveByCondition(根据条件获取最新的假期信息)
	  * @Title: getNewLeaveByCondition
	  * @Description: 根据条件获取最新的假期信息
	  * @param empLeave
	  * @return    设定文件
	  * EmpLeave    返回类型
	  * @throws
	 */
	EmpLeave getNewLeaveByCondition(EmpLeave empLeave);
	
	/**
	  * updateTimeByIds(刷数据专用)
	  * @Title: updateTimeByIds
	  * @Description: 刷数据专用
	  * @param empLeave
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
	int updateTimeByIds(EmpLeave empLeave);
	
	/**
	  * getMaxAllowDays(获得最大的允许休假天数)
	  * @Title: getMaxAllowDays
	  * @Description: 获得最大的允许休假天数
	  * @param empLeave
	  * @return    设定文件
	  * Map<String,Object>    返回类型
	  * @throws
	 */
	Map<String,Object> getMaxAllowDays(EmpLeave empLeave);
	
	/**
	  * getMaxAllowDays(获得最大的已用休假天数)
	  * @Title: getMaxAllowDays
	  * @Description: 获得最大的已用休假天数
	  * @param empLeave
	  * @return    设定文件
	  * Map<String,Object>    返回类型
	  * @throws
	 */
	Map<String,Object> getMaxUsedDays(EmpLeave empLeave);
	
	/**
	  * getAllowDays(获得符合休假时间范围内的有效假期明细)
	  * @Title: getAllowDays
	  * @Description: 获得符合休假时间范围内的有效假期明细
	  * @param empLeave
	  * @return    设定文件
	  * List<EmpLeave>    返回类型
	  * @throws
	 */
	List<EmpLeave> getAllowDays(EmpLeave empLeave);
	
	/**
	  * getAllowBlockDays(获得符合休假时间范围内的有效假期明细)
	  * @Title: getAllowBlockDays
	  * @Description: 获得符合休假时间范围内的有效假期明细
	  * @param empLeave
	  * @return    设定文件
	  * List<EmpLeave>    返回类型
	  * @throws
	 */
	List<EmpLeave> getAllowBlockDays(EmpLeave empLeave);
	
	/**
	  * getAllowBlockDaysDesc(获得符合休假时间范围内的有效假期明细)倒叙
	  * @Title: getAllowBlockDaysDesc
	  * @Description: 获得符合休假时间范围内的有效假期明细倒叙
	  * @param empLeave
	  * @return    设定文件
	  * List<EmpLeave>    返回类型
	  * @throws
	 */
	List<EmpLeave> getAllowBlockDaysDesc(EmpLeave empLeave);
	
	/**
	  * getAllowUsedDays(获得符合休假时间范围内的有效假期明细)
	  * @Title: getAllowUsedDays
	  * @Description: 获得符合休假时间范围内的有效假期明细
	  * @param empLeave
	  * @return    设定文件
	  * List<EmpLeave>    返回类型
	  * @throws
	 */
	List<EmpLeave> getAllowUsedDays(EmpLeave empLeave);
	
	
	/**
	  * getTotalAllowDays(获得总的可用调休小时)
	  * @Title: getTotalAllowDays
	  * @Description: 获得总的可用调休小时
	  * @param empLeave
	  * @return    设定文件
	  * List<EmpLeave>    返回类型
	  * @throws
	 */
	List<EmpLeave> getTotalAllowDays(EmpLeave empLeave);
	
	/**
	  * getTotalAllowDays(获得总的已用调休小时)
	  * @Title: getTotalAllowDays
	  * @Description: 获得总的已用调休小时
	  * @param empLeave
	  * @return    设定文件
	  * List<EmpLeave>    返回类型
	  * @throws
	 */
	List<EmpLeave> getTotalUsedDays(EmpLeave empLeave);
	
	
	/**
	  * getTotalBlockDays(获得总的可用调休小时)
	  * @Title: getTotalBlockDays
	  * @Description: 获得总的可用调休小时
	  * @param empLeave
	  * @return    设定文件
	  * List<EmpLeave>    返回类型
	  * @throws
	 */
	List<EmpLeave> getTotalBlockDays(EmpLeave empLeave);
	
	List<EmpLeave> getByPid(Long parendId);
	
	List<EmpLeave> getByPidDesc(Long parendId);
	
	/**
	  * checkLeaveIsValidate(验证假期的有效性)
	  * @Title: checkLeaveIsValidate
	  * @Description: 验证假期的有效性
	  * @param empLeave
	  * @return    设定文件
	  * Map<String,Object>    返回类型
	  * @throws
	 */
	Map<String,Object> checkLeaveIsValidate(EmpLeave empLeave);

	/**
	 * 找到需要修复的假期数据
	 * @param empLeave
	 * @return
	 */
	List<EmpLeave> getRepairLeaveDatas(EmpLeave empLeave);
	
	/**
	  * getInvalidLeaveList(获得无效的年假或者调休数据)
	  * @Title: getInvalidLeaveList
	  * @Description: 获得无效的年假或者调休数据
	  * @return    设定文件
	  * List<EmpLeave>    返回类型
	  * @throws
	 */
	List<EmpLeave> getInvalidLeaveList();
	
	/**
	  * getRemainRest(获得剩余调休)
	  * @Title: getRemainRest
	  * @Description: 获得剩余调休
	  * @param empLeave
	  * @return    设定文件
	  * EmpLeave    返回类型
	  * @throws
	 */
	EmpLeave getRemainRest(EmpLeave empLeave);

	Map<String,Object> getSickBlockDaysByEmpId(Long employeeId);
	
	//删除2018及以后的除（年假，病假，调休）的数据
	void deleteOtherLeaveAfter2018();
	
	Long getEmployeeId(@Param("code")String code);

	Date getEntryTime(@Param("empId")Long empId);

	int getCountEmployee(@Param("empId")Long empId,@Param("year")Integer year);
	
	//查询初始化流水历史数据(总的)
	List<EmpLeave> getHistoryTotalReaminLeave(EmpLeave empLeave);
	
	//查询初始化流水历史数据(明细)
	List<EmpLeave> getHistoryReaminLeaveList(EmpLeave empLeave);
	
	//查询初始化其它假期流水历史数据(明细)
	List<EmpLeave> getOtherHistoryUsedList();

	List<Double> getBenefitAllowRemainDays(@Param("employeeId")Long employeeId, @Param("year")int year);
	
	//获取历史使用记录
	List<EmpLeave> getHistoryUsedList(EmpLeave empLeave);
	
	
	//初始化流水查询年假
	List<EmpLeave> getYearLeaveList();
	//初始化流水查询调休
	List<EmpLeave> getOffLeaveList();
	//初始化流水查询其他假期
	List<EmpLeave> getOtherLeaveList();

	List<EmpLeave> getUnexpiredRestLeaveDays(EmpLeave planLeave);

	List<EmpLeave> getBlockedRestLeaveDaysDesc(EmpLeave type13LeaveCondition);

	List<EmpLeave> getBlockedRestLeaveDays(EmpLeave type13LeaveCondition);
	
	List<EmpLeave> getAllowedRestLeaveDays(EmpLeave type13LeaveCondition);

	List<EmpLeave> getTotalBlockedDays(EmpLeave planLeave);
	
	//获取员工其它调休总数
	Double getOtherRestLeaveCount(@Param("employeeId")Long employeeId);
	
	//获取员工其它调休列表
	List<EmpLeave> getOtherRestLeaveList(@Param("employeeId")Long employeeId, @Param("yearList")List<Integer> yearList);
	
	//查询符合失效条件的其它调休
	List<EmpLeave> getInvalidOtherRestLeaveList(@Param("endTime")Date endTime);

	Integer getCountOvertime(EmpLeave empovertimeLeave);

	List<EmpLeave> getSumOvertimeDay(@Param("year")Integer year,@Param("employeeIdList") List<Long> employeeIdList);
	
	/**
	  * getAllowRemainDays(查询员工指定类型假期剩余天数)
	  * @Title: getAllowRemainDays
	  * @Description: 查询员工指定类型假期剩余天数
	  * @param employeeId
	  * @param typeList
	  * @return    设定文件
	  * List<EmpLeave>    返回类型
	  * @throws
	 */
	List<EmpLeave> getAllowRemainDays(@Param("employeeId")Long employeeId, @Param("typeList")List<Integer> typeList,@Param("year")Integer year);
	
	
}
