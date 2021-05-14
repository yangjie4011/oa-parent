package com.ule.oa.base.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ule.oa.base.po.EmpLeaveReport;
import com.ule.oa.base.po.Employee;

/**
 * @ClassName: 员工请假报表
 * @Description: 员工请假报表
 * @author yangjie
 * @date 2017年10月24日
 */
public interface EmpLeaveReportService {
	
	public void batchSave(List<EmpLeaveReport> list);

	public Map<String, List<Map<String, Object>>> getExcelDataByYearAndMonth(
			String[] monthTitles, Date startTime, Date endTime, Long departId,List<Long> empTypeIdList);

	public Map<String, List<Map<String, Object>>> getExcelDataByYearAndMonthpageBean(
			String[] monthTitles, Date startTime, Date endTime, Long departId,Employee cp);

	public Map<String, List<Map<String, Object>>> getmonthLeaveSummaryPageBean(
			Date startTime, Date endTime, Long departId, String[] monthTitles,
			Employee cp);

	public List<EmpLeaveReport> getListByCondition(EmpLeaveReport model);

	public void batchUpdate(List<EmpLeaveReport> batchUpdateList);

	public void asyncBatchSave(List<EmpLeaveReport> batchSaveList);

	public void deleteByCondition(EmpLeaveReport elrModel);

	public Map<String, List<Map<String, Object>>> getmonthLeaveSummary(Date startTime,
			Date endTime, Long departId, String[] monthTitles,List<Long> empTypeIdList);

	public void deleteAll();

}
