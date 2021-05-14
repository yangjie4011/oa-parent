package com.ule.oa.base.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import com.ule.oa.base.po.Employee;


/**
 * 考勤报表
 * @author yangjie
 *
 */
public interface AttnReportService {
	
	
	/**
	 * 根据员工生成日度餐费与交通补贴明细报表
	 * @param employee
	 * @param lastMonthStart-开始时间
	 * @param lastMonthEnd-结束时间
	 * @param workTypeMap-工时类型集合
	 * @param whetherSchedulingMap-排班类型集合
	 * @param vacationMap-时间段内节假日集合
	 * @param optUser-操作人
	 */
	public void generateDayWCandJTallowReportByEmp(Employee employee,Date lastMonthStart,Date lastMonthEnd,Map<Long,String> workTypeMap,
			Map<Long,String> whetherSchedulingMap,Map<Date,Integer> vacationMap,String optUser);
	
	
	/**
	 * 根据条件查询餐费与交通补贴明细报表
	 * @param startDate
	 * @param endDate
	 * @param departId
	 * @return
	 */
	List<Map<String,Object>> getDinnerAndTrafficAllowanceMonthDetail(Date startDate,Date endDate,Long departId);
	
	/**
	 * 根据条件查询餐费与交通补贴月度统计
	 * @param startDate
	 * @param endDate
	 * @param departId
	 * @return
	 */
	List<Map<String,Object>> getDinnerAndTrafficAllowanceMonthSummary(Date startDate,Date endDate,Long departId);

}
