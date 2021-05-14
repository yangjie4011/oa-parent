package com.ule.oa.base.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.TrafficAllowanceMonthSummaryDTO;
import com.ule.oa.base.po.tbl.AttnReportTbl;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

public interface AttnReportMapper extends OaSqlMapper{
	
	int save(AttnReportTbl attnReport);
	
	/**
	 * 只支持查询一个月内的报表数据
	 * @param employeeId
	 * @param startDate-月初
	 * @param endDate-月末
	 * @return
	 */
	AttnReportTbl getDinnerAndTrafficAllowanceDay(@Param("employeeId") Long employeeId,
			@Param("startDate") Date startDate,@Param("endDate") Date endDate);
	
	int deleteById(@Param("id")Long id,@Param("updateUser")String updateUser);
	
	/**
	 * 晚间餐费与交通费补贴月度明细
	 * @param startDate 开始日期
	 * @param endDate 结束日期
	 * @param departId 部门id
	 * @return
	 */
	List<Map<String,Object>> getDinnerAndTrafficAllowanceMonthDetail(@Param("startDate")Date startDate,@Param("endDate")Date endDate,@Param("departId")Long departId);
	
	/**
	 * 晚间餐费补贴月度汇总
	 * @param startDate 开始日期
	 * @param endDate 结束日期
	 * @param departId 部门id
	 * @return
	 */
	List<Map<String,Object>> getDinnerAllowanceMonthSummary(@Param("startDate")Date startDate,@Param("endDate")Date endDate,@Param("departId")Long departId);
	
	/**
	 * 交通费补贴补贴月度汇总
	 * @param startDate 开始日期
	 * @param endDate 结束日期
	 * @param departId 部门id
	 * @return
	 */
	List<TrafficAllowanceMonthSummaryDTO> getTrafficAllowanceMonthSummary(@Param("startDate")Date startDate,@Param("endDate")Date endDate,@Param("departId")Long departId,@Param("trafficAllowance")String trafficAllowance);

}
