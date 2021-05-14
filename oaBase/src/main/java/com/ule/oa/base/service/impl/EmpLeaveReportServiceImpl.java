package com.ule.oa.base.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ule.oa.base.mapper.DepartMapper;
import com.ule.oa.base.mapper.EmpLeaveReportMapper;
import com.ule.oa.base.mapper.EmployeeMapper;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.EmpLeaveReport;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmpLeaveReportService;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;

/**
 * @ClassName: 员工请假报表
 * @Description: 员工请假报表
 * @author yangjie
 * @date 2017年10月24日
 */
@Service
public class EmpLeaveReportServiceImpl implements EmpLeaveReportService {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private EmpLeaveReportMapper empLeaveReportMapper;
	@Autowired
	private EmployeeMapper employeeMapper;
	@Autowired
	private DepartMapper departMapper;
	@Resource
	private DepartService departService;
	
	@Resource(name="threadPoolTaskExecutor")
	private ThreadPoolTaskExecutor pool;
	
	private Date CURRENT_TIME = DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_LONG), DateUtils.FORMAT_LONG);//当前时间
	
	private class SaveEmpLeaveReport implements Runnable {
		
		private List<EmpLeaveReport> list;
		
		SaveEmpLeaveReport(List<EmpLeaveReport> list) {
			this.list = list;
		}
		
		public void run() {
		   saveEmpLeaveReport(list);
		}
	}
	
	public void saveEmpLeaveReport(List<EmpLeaveReport> list){
		if(null != list && !list.isEmpty()){
			for (EmpLeaveReport empLeaveReport : list) {
				if(null != empLeaveReport){
					empLeaveReport.setCreateTime(CURRENT_TIME);
					empLeaveReport.setDelFlag(ConfigConstants.IS_NO_INTEGER);
				}
			}
		}
		empLeaveReportMapper.batchSave(list);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void batchSave(List<EmpLeaveReport> list) {
		pool.execute(new SaveEmpLeaveReport(list));
	}

	/**
	 * 假期明细报表数据导出(起始时间不是1号,则月初到起始时间之间的天数假期不显示;结束时间不是月最后一天,则结束时间到月末之间的天数假期不显示)
	 */
	@Override
	public Map<String, List<Map<String, Object>>> getExcelDataByYearAndMonth(
			String[] monthTitles, Date startTime, Date endTime, Long departId,List<Long> empTypeIdList) {
		EmpLeaveReport model = new EmpLeaveReport();
		Employee model1 = new Employee();
		//获取月份参数
		List<String> months = new ArrayList<String>();
		for (int i = 0; i < monthTitles.length; i++) {
			months.add(monthTitles[i]);
		}
		model.setMonths(months);
		//获取员工id
		if(null != departId){
			model.setDepartId(departId);
			model1.setDepartId(departId);
		}		
		model1.setFirstEntryTime(endTime);
		model1.setWorkAddressType(0);//只查询工作地点是上海员工的数据
		
		//<月, <员工ID, 员工假期数据>>
		Map<String, Map<Long, Map<String, Object>>> monthDatasMap = new LinkedHashMap<String, Map<Long, Map<String, Object>>>();
		Map<Long, Map<String, Object>> datasMap = null;
		
		//获取所有员工
		//查询指定员工类型的数据
		model1.setEmpTypeIdList(empTypeIdList);
		
		List<Employee> employeeList = employeeMapper.getReportList(model1);
		Map<Long,Employee> employMap = new HashMap<Long,Employee>();
		if(employeeList!=null&&employeeList.size()>0){
			Map<String, Object> data = null;
			Integer type = null;
			String startMonth = DateUtils.getYearAndMonth(startTime);
			int startDay = Integer.parseInt(DateUtils.getDayOfMonth(startTime));
			String endMonth = DateUtils.getYearAndMonth(endTime);
			int endDay = Integer.parseInt(DateUtils.getDayOfMonth(endTime));
			String month = "";
			Long employeeId = null;
			for(Employee employ:employeeList){
				employMap.put(employ.getId(), employ);
				//离职日期的年份在搜索开始日期的前一年的不要
				if(employ.getQuitTime()!=null&&employ.getQuitTime().getTime()<=DateUtils.parse(DateUtils.getYear(startTime)+"-01-01",DateUtils.FORMAT_SHORT).getTime()){
					continue;
				}
				//根据月份分类
				month = startMonth;
				type = 1;
				employeeId = employ.getId();
				if(StringUtils.isNotBlank(month)){
					if(!monthDatasMap.containsKey(month)){
						//月份不存在,初始化数据
						datasMap = new LinkedHashMap<Long, Map<String, Object>>();
						monthDatasMap.put(month, datasMap);
					}
					//月份存在,获取当月员工数据
					datasMap = monthDatasMap.get(month);
					//新增员工数据
					data = new LinkedHashMap<String, Object>();
					EmpLeaveReport er = new EmpLeaveReport();
					er.setType(1);
					er.setEmployeeId(employeeId);
					er.setEmployeeName(employ.getCnName());
					er.setMonth(month);
					er.setDate1(0);er.setDate2(0);er.setDate3(0);er.setDate4(0);er.setDate5(0);
					er.setDate6(0);er.setDate7(0);er.setDate8(0);er.setDate9(0);er.setDate10(0);
					er.setDate11(0);er.setDate12(0);er.setDate13(0);er.setDate14(0);er.setDate15(0);
					er.setDate16(0);er.setDate17(0);er.setDate18(0);er.setDate19(0);er.setDate20(0);
					er.setDate21(0);er.setDate22(0);er.setDate23(0);er.setDate24(0);er.setDate25(0);
					er.setDate26(0);er.setDate27(0);er.setDate28(0);er.setDate29(0);er.setDate30(0);
					er.setDate31(0);
					setData(data, type.intValue(), er, startMonth, endMonth, startDay, endDay,employMap);
					//若员工数据存在当前员工,则合并
					if(datasMap.containsKey(employeeId)){
						mergeData(data, datasMap.get(employeeId));
					}
					datasMap.put(employeeId, data);
				}
			}
		}
		
		List<EmpLeaveReport> empLeaveReports = empLeaveReportMapper.getListByCondition(model);
		
		if(null != empLeaveReports && !empLeaveReports.isEmpty()){
			Map<String, Object> data = null;
			Integer type = null;
			String startMonth = DateUtils.getYearAndMonth(startTime);
			int startDay = Integer.parseInt(DateUtils.getDayOfMonth(startTime));
			String endMonth = DateUtils.getYearAndMonth(endTime);
			int endDay = Integer.parseInt(DateUtils.getDayOfMonth(endTime));
			String month = "";
			Long employeeId = null;
			for (EmpLeaveReport er : empLeaveReports) {
				//根据月份分类
				month = er.getMonth();
				type = er.getType();
				employeeId = er.getEmployeeId();
				if(StringUtils.isNotBlank(month)){
					if(!monthDatasMap.containsKey(month)){
						//月份不存在,初始化数据
						datasMap = new LinkedHashMap<Long, Map<String, Object>>();
						monthDatasMap.put(month, datasMap);
					}
					//月份存在,获取当月员工数据
					datasMap = monthDatasMap.get(month);
					//新增员工数据
					data = new LinkedHashMap<String, Object>();
					setData(data, type.intValue(), er, startMonth, endMonth, startDay, endDay,employMap);
					//若员工数据存在当前员工,则合并
					if(datasMap.containsKey(employeeId)){
						mergeData(data, datasMap.get(employeeId));
					}
					datasMap.put(employeeId, data);
				}
			}
		}
		Map<String, List<Map<String, Object>>> returnMap = new LinkedHashMap<String, List<Map<String, Object>>>();
		if(monthDatasMap != null && !monthDatasMap.isEmpty()){
			for (String month : monthDatasMap.keySet()) {
				returnMap.put(month, new ArrayList<Map<String, Object>>(monthDatasMap.get(month).values()));
			}
		}
		return returnMap;
	}
	
	
	@Override
	public Map<String, List<Map<String, Object>>> getExcelDataByYearAndMonthpageBean(
			String[] monthTitles, Date startTime, Date endTime, Long departId,Employee cp) {
		EmpLeaveReport model = new EmpLeaveReport();
/*		model.setLimit(cp.getLimit());
		model.setOffset(cp.getOffset());*/
		Employee model1 = new Employee();
		//获取月份参数
		List<String> months = new ArrayList<String>();
		for (int i = 0; i < monthTitles.length; i++) {
			months.add(monthTitles[i]);
		}
		model.setMonths(months);
		//获取员工id
		if(null != departId){
			model.setDepartId(departId);
			model1.setDepartId(departId);
		}		
		model1.setFirstEntryTime(endTime);
		
		//<月, <员工ID, 员工假期数据>>
		Map<String, Map<Long, Map<String, Object>>> monthDatasMap = new LinkedHashMap<String, Map<Long, Map<String, Object>>>();
		Map<Long, Map<String, Object>> datasMap = null;
		model1.setOffset(cp.getOffset());
		model1.setLimit(cp.getLimit());
		model1.setCnName(cp.getCnName());
		model1.setCode(cp.getCode());
		model1.setCurrentUserDepart(cp.getCurrentUserDepart());
		model1.setSubEmployeeIdList(cp.getSubEmployeeIdList());
		model1.setEmpTypeIdList(cp.getEmpTypeIdList());
		//获取所有员工
		model1.setWorkAddressType(0);
		List<Employee> employeeList = employeeMapper.getReportList(model1);		
		Map<Long,Employee> employMap = new HashMap<Long,Employee>();
		if(employeeList!=null&&employeeList.size()>0){
			Map<String, Object> data = null;
			Integer type = null;
			String startMonth = DateUtils.getYearAndMonth(startTime);
			int startDay = Integer.parseInt(DateUtils.getDayOfMonth(startTime));
			String endMonth = DateUtils.getYearAndMonth(endTime);
			int endDay = Integer.parseInt(DateUtils.getDayOfMonth(endTime));
			String month = "";
			Long employeeId = null;
			for(Employee employ:employeeList){
				employMap.put(employ.getId(), employ);
				//离职日期的年份在搜索开始日期的前一年的不要
				if(employ.getQuitTime()!=null&&employ.getQuitTime().getTime()<=DateUtils.parse(DateUtils.getYear(startTime)+"-01-01",DateUtils.FORMAT_SHORT).getTime()){
					continue;
				}
				//根据月份分类
				month = startMonth;
				type = 1;
				employeeId = employ.getId();
				if(StringUtils.isNotBlank(month)){
					if(!monthDatasMap.containsKey(month)){
						//月份不存在,初始化数据
						datasMap = new LinkedHashMap<Long, Map<String, Object>>();
						monthDatasMap.put(month, datasMap);
					}
					//月份存在,获取当月员工数据
					datasMap = monthDatasMap.get(month);
					//新增员工数据
					data = new LinkedHashMap<String, Object>();
					EmpLeaveReport er = new EmpLeaveReport();
					er.setType(1);
					er.setEmployeeId(employeeId);
					er.setEmployeeName(employ.getCnName());
					er.setMonth(month);
					er.setDate1(0);er.setDate2(0);er.setDate3(0);er.setDate4(0);er.setDate5(0);
					er.setDate6(0);er.setDate7(0);er.setDate8(0);er.setDate9(0);er.setDate10(0);
					er.setDate11(0);er.setDate12(0);er.setDate13(0);er.setDate14(0);er.setDate15(0);
					er.setDate16(0);er.setDate17(0);er.setDate18(0);er.setDate19(0);er.setDate20(0);
					er.setDate21(0);er.setDate22(0);er.setDate23(0);er.setDate24(0);er.setDate25(0);
					er.setDate26(0);er.setDate27(0);er.setDate28(0);er.setDate29(0);er.setDate30(0);
					er.setDate31(0);
					setData(data, type.intValue(), er, startMonth, endMonth, startDay, endDay,employMap);
					//若员工数据存在当前员工,则合并
					if(datasMap.containsKey(employeeId)){
						mergeData(data, datasMap.get(employeeId));
					}
					datasMap.put(employeeId, data);
				}
			}
		}
		if(cp.getExportStauts()==1){
			
		}else{		
			List<EmpLeaveReport> empLeaveReports = empLeaveReportMapper.getListByCondition(model);			
			if(null != empLeaveReports && !empLeaveReports.isEmpty()){
				Map<String, Object> data = null;
				Integer type = null;
				String startMonth = DateUtils.getYearAndMonth(startTime);
				int startDay = Integer.parseInt(DateUtils.getDayOfMonth(startTime));
				String endMonth = DateUtils.getYearAndMonth(endTime);
				int endDay = Integer.parseInt(DateUtils.getDayOfMonth(endTime));
				String month = "";
				Long employeeId = null;
				for (EmpLeaveReport er : empLeaveReports) {
					//根据月份分类
					month = er.getMonth();
					type = er.getType();
					employeeId = er.getEmployeeId();
					if(StringUtils.isNotBlank(month)){
						if(!monthDatasMap.containsKey(month)){
							//月份不存在,初始化数据
							datasMap = new LinkedHashMap<Long, Map<String, Object>>();
							monthDatasMap.put(month, datasMap);
						}
						//月份存在,获取当月员工数据
						datasMap = monthDatasMap.get(month);
						//新增员工数据
						data = new LinkedHashMap<String, Object>();
						setData(data, type.intValue(), er, startMonth, endMonth, startDay, endDay,employMap);
						//若员工数据存在当前员工,则合并
						if(datasMap.containsKey(employeeId)){
							mergeData(data, datasMap.get(employeeId));
						}
						datasMap.put(employeeId, data);
					}
				}
			}		
		}		
		Map<String, List<Map<String, Object>>> returnMap = new LinkedHashMap<String, List<Map<String, Object>>>();
		if(monthDatasMap != null && !monthDatasMap.isEmpty()){
			for (String month : monthDatasMap.keySet()) {
				returnMap.put(month, new ArrayList<Map<String, Object>>(monthDatasMap.get(month).values()));
			}
		} 
		return returnMap;
	}

	private void mergeData(Map<String, Object> oldData, Map<String, Object> newData) {
		int type = Integer.parseInt(newData.get("type").toString());
		oldData.put(type + "_1", doubleToStr(doubleInit(oldData.get(type + "_1")) + doubleInit(newData.get(type + "_1"))));
		oldData.put(type + "_2", doubleToStr(doubleInit(oldData.get(type + "_2")) + doubleInit(newData.get(type + "_2"))));
		oldData.put(type + "_3", doubleToStr(doubleInit(oldData.get(type + "_3")) + doubleInit(newData.get(type + "_3"))));
		oldData.put(type + "_4", doubleToStr(doubleInit(oldData.get(type + "_4")) + doubleInit(newData.get(type + "_4"))));
		oldData.put(type + "_5", doubleToStr(doubleInit(oldData.get(type + "_5")) + doubleInit(newData.get(type + "_5"))));
		oldData.put(type + "_6", doubleToStr(doubleInit(oldData.get(type + "_6")) + doubleInit(newData.get(type + "_6"))));
		oldData.put(type + "_7", doubleToStr(doubleInit(oldData.get(type + "_7")) + doubleInit(newData.get(type + "_7"))));
		oldData.put(type + "_8", doubleToStr(doubleInit(oldData.get(type + "_8")) + doubleInit(newData.get(type + "_8"))));
		oldData.put(type + "_9", doubleToStr(doubleInit(oldData.get(type + "_9")) + doubleInit(newData.get(type + "_9"))));
		oldData.put(type + "_10", doubleToStr(doubleInit(oldData.get(type + "_10")) + doubleInit(newData.get(type + "_10"))));
		oldData.put(type + "_11", doubleToStr(doubleInit(oldData.get(type + "_11")) + doubleInit(newData.get(type + "_11"))));
		oldData.put(type + "_12", doubleToStr(doubleInit(oldData.get(type + "_12")) + doubleInit(newData.get(type + "_12"))));
		oldData.put(type + "_13", doubleToStr(doubleInit(oldData.get(type + "_13")) + doubleInit(newData.get(type + "_13"))));
		oldData.put(type + "_14", doubleToStr(doubleInit(oldData.get(type + "_14")) + doubleInit(newData.get(type + "_14"))));
		oldData.put(type + "_15", doubleToStr(doubleInit(oldData.get(type + "_15")) + doubleInit(newData.get(type + "_15"))));
		oldData.put(type + "_16", doubleToStr(doubleInit(oldData.get(type + "_16")) + doubleInit(newData.get(type + "_16"))));
		oldData.put(type + "_17", doubleToStr(doubleInit(oldData.get(type + "_17")) + doubleInit(newData.get(type + "_17"))));
		oldData.put(type + "_18", doubleToStr(doubleInit(oldData.get(type + "_18")) + doubleInit(newData.get(type + "_18"))));
		oldData.put(type + "_19", doubleToStr(doubleInit(oldData.get(type + "_19")) + doubleInit(newData.get(type + "_19"))));
		oldData.put(type + "_20", doubleToStr(doubleInit(oldData.get(type + "_20")) + doubleInit(newData.get(type + "_20"))));
		oldData.put(type + "_21", doubleToStr(doubleInit(oldData.get(type + "_21")) + doubleInit(newData.get(type + "_21"))));
		oldData.put(type + "_22", doubleToStr(doubleInit(oldData.get(type + "_22")) + doubleInit(newData.get(type + "_22"))));
		oldData.put(type + "_23", doubleToStr(doubleInit(oldData.get(type + "_23")) + doubleInit(newData.get(type + "_23"))));
		oldData.put(type + "_24", doubleToStr(doubleInit(oldData.get(type + "_24")) + doubleInit(newData.get(type + "_24"))));
		oldData.put(type + "_25", doubleToStr(doubleInit(oldData.get(type + "_25")) + doubleInit(newData.get(type + "_25"))));
		oldData.put(type + "_26", doubleToStr(doubleInit(oldData.get(type + "_26")) + doubleInit(newData.get(type + "_26"))));
		oldData.put(type + "_27", doubleToStr(doubleInit(oldData.get(type + "_27")) + doubleInit(newData.get(type + "_27"))));
		oldData.put(type + "_28", doubleToStr(doubleInit(oldData.get(type + "_28")) + doubleInit(newData.get(type + "_28"))));
		oldData.put(type + "_29", doubleToStr(doubleInit(oldData.get(type + "_29")) + doubleInit(newData.get(type + "_29"))));
		oldData.put(type + "_30", doubleToStr(doubleInit(oldData.get(type + "_30")) + doubleInit(newData.get(type + "_30"))));
		oldData.put(type + "_31", doubleToStr(doubleInit(oldData.get(type + "_31")) + doubleInit(newData.get(type + "_31"))));
	}


	
	private void setData(Map<String, Object> data, int type,
			EmpLeaveReport er, String startMonth, String endMonth,
			int startDay, int endDay,Map<Long,Employee> employMap) {
		data.put("code", employMap.containsKey(er.getEmployeeId())?employMap.get(er.getEmployeeId()).getCode():"");
		data.put("employeeId", er.getEmployeeId());//不展示
		data.put("name", er.getEmployeeName());
		data.put("month", er.getMonth());
		data.put("type", er.getType());
		if(startMonth.equals(er.getMonth())){
			if(startDay <= 1) {
				data.put(type + "_1", doubleToStr(er.getDate1()));
			}
			if(startDay <= 2) {
				data.put(type + "_2", doubleToStr(er.getDate2()));
			}
			if(startDay <= 3) {
				data.put(type + "_3", doubleToStr(er.getDate3()));
			}
			if(startDay <= 4) {
				data.put(type + "_4", doubleToStr(er.getDate4()));
			}
			if(startDay <= 5) {
				data.put(type + "_5", doubleToStr(er.getDate5()));
			}
			if(startDay <= 6) {
				data.put(type + "_6", doubleToStr(er.getDate6()));
			}
			if(startDay <= 7) {
				data.put(type + "_7", doubleToStr(er.getDate7()));
			}
			if(startDay <= 8) {
				data.put(type + "_8", doubleToStr(er.getDate8()));
			}
			if(startDay <= 9) {
				data.put(type + "_9", doubleToStr(er.getDate9()));
			}
			if(startDay <= 10) {
				data.put(type + "_10", doubleToStr(er.getDate10()));
			}
			if(startDay <= 11) {
				data.put(type + "_11", doubleToStr(er.getDate11()));
			}
			if(startDay <= 12) {
				data.put(type + "_12", doubleToStr(er.getDate12()));
			}
			if(startDay <= 13) {
				data.put(type + "_13", doubleToStr(er.getDate13()));
			}
			if(startDay <= 14) {
				data.put(type + "_14", doubleToStr(er.getDate14()));
			}
			if(startDay <= 15) {
				data.put(type + "_15", doubleToStr(er.getDate15()));
			}
			if(startDay <= 16) {
				data.put(type + "_16", doubleToStr(er.getDate16()));
			}
			if(startDay <= 17) {
				data.put(type + "_17", doubleToStr(er.getDate17()));
			}
			if(startDay <= 18) {
				data.put(type + "_18", doubleToStr(er.getDate18()));
			}
			if(startDay <= 19) {
				data.put(type + "_19", doubleToStr(er.getDate19()));
			}
			if(startDay <= 20) {
				data.put(type + "_20", doubleToStr(er.getDate20()));
			}
			if(startDay <= 21) {
				data.put(type + "_21", doubleToStr(er.getDate21()));
			}
			if(startDay <= 22) {
				data.put(type + "_22", doubleToStr(er.getDate22()));
			}
			if(startDay <= 23) {
				data.put(type + "_23", doubleToStr(er.getDate23()));
			}
			if(startDay <= 24) {
				data.put(type + "_24", doubleToStr(er.getDate24()));
			}
			if(startDay <= 25) {
				data.put(type + "_25", doubleToStr(er.getDate25()));
			}
			if(startDay <= 26) {
				data.put(type + "_26", doubleToStr(er.getDate26()));
			}
			if(startDay <= 27) {
				data.put(type + "_27", doubleToStr(er.getDate27()));
			}
			if(startDay <= 28) {
				data.put(type + "_28", doubleToStr(er.getDate28()));
			}
			if(startDay <= 29) {
				data.put(type + "_29", doubleToStr(er.getDate29()));
			}
			if(startDay <= 30) {
				data.put(type + "_30", doubleToStr(er.getDate30()));
			}
			if(startDay <= 31) {
				data.put(type + "_31", doubleToStr(er.getDate31()));
			}
		}else if(endMonth.equals(er.getMonth())){
			if(endDay >= 1) {
				data.put(type + "_1", doubleToStr(er.getDate1()));
			}
			if(endDay >= 2) {
				data.put(type + "_2", doubleToStr(er.getDate2()));
			}
			if(endDay >= 3) {
				data.put(type + "_3", doubleToStr(er.getDate3()));
			}
			if(endDay >= 4) {
				data.put(type + "_4", doubleToStr(er.getDate4()));
			}
			if(endDay >= 5) {
				data.put(type + "_5", doubleToStr(er.getDate5()));
			}
			if(endDay >= 6) {
				data.put(type + "_6", doubleToStr(er.getDate6()));
			}
			if(endDay >= 7) {
				data.put(type + "_7", doubleToStr(er.getDate7()));
			}
			if(endDay >= 8) {
				data.put(type + "_8", doubleToStr(er.getDate8()));
			}
			if(endDay >= 9) {
				data.put(type + "_9", doubleToStr(er.getDate9()));
			}
			if(endDay >= 10) {
				data.put(type + "_10", doubleToStr(er.getDate10()));
			}
			if(endDay >= 11) {
				data.put(type + "_11", doubleToStr(er.getDate11()));
			}
			if(endDay >= 12) {
				data.put(type + "_12", doubleToStr(er.getDate12()));
			}
			if(endDay >= 13) {
				data.put(type + "_13", doubleToStr(er.getDate13()));
			}
			if(endDay >= 14) {
				data.put(type + "_14", doubleToStr(er.getDate14()));
			}
			if(endDay >= 15) {
				data.put(type + "_15", doubleToStr(er.getDate15()));
			}
			if(endDay >= 16) {
				data.put(type + "_16", doubleToStr(er.getDate16()));
			}
			if(endDay >= 17) {
				data.put(type + "_17", doubleToStr(er.getDate17()));
			}
			if(endDay >= 18) {
				data.put(type + "_18", doubleToStr(er.getDate18()));
			}
			if(endDay >= 19) {
				data.put(type + "_19", doubleToStr(er.getDate19()));
			}
			if(endDay >= 20) {
				data.put(type + "_20", doubleToStr(er.getDate20()));
			}
			if(endDay >= 21) {
				data.put(type + "_21", doubleToStr(er.getDate21()));
			}
			if(endDay >= 22) {
				data.put(type + "_22", doubleToStr(er.getDate22()));
			}
			if(endDay >= 23) {
				data.put(type + "_23", doubleToStr(er.getDate23()));
			}
			if(endDay >= 24) {
				data.put(type + "_24", doubleToStr(er.getDate24()));
			}
			if(endDay >= 25) {
				data.put(type + "_25", doubleToStr(er.getDate25()));
			}
			if(endDay >= 26) {
				data.put(type + "_26", doubleToStr(er.getDate26()));
			}
			if(endDay >= 27) {
				data.put(type + "_27", doubleToStr(er.getDate27()));
			}
			if(endDay >= 28) {
				data.put(type + "_28", doubleToStr(er.getDate28()));
			}
			if(endDay >= 29) {
				data.put(type + "_29", doubleToStr(er.getDate29()));
			}
			if(endDay >= 30) {
				data.put(type + "_30", doubleToStr(er.getDate30()));
			}
			if(endDay >= 31) {
				data.put(type + "_31", doubleToStr(er.getDate31()));
			}
				
		}else{
			data.put(type + "_1", doubleToStr(er.getDate1()));
			data.put(type + "_2", doubleToStr(er.getDate2()));
			data.put(type + "_3", doubleToStr(er.getDate3()));
			data.put(type + "_4", doubleToStr(er.getDate4()));
			data.put(type + "_5", doubleToStr(er.getDate5()));
			data.put(type + "_6", doubleToStr(er.getDate6()));
			data.put(type + "_7", doubleToStr(er.getDate7()));
			data.put(type + "_8", doubleToStr(er.getDate8()));
			data.put(type + "_9", doubleToStr(er.getDate9()));
			data.put(type + "_10", doubleToStr(er.getDate10()));
			data.put(type + "_11", doubleToStr(er.getDate11()));
			data.put(type + "_12", doubleToStr(er.getDate12()));
			data.put(type + "_13", doubleToStr(er.getDate13()));
			data.put(type + "_14", doubleToStr(er.getDate14()));
			data.put(type + "_15", doubleToStr(er.getDate15()));
			data.put(type + "_16", doubleToStr(er.getDate16()));
			data.put(type + "_17", doubleToStr(er.getDate17()));
			data.put(type + "_18", doubleToStr(er.getDate18()));
			data.put(type + "_19", doubleToStr(er.getDate19()));
			data.put(type + "_20", doubleToStr(er.getDate20()));
			data.put(type + "_21", doubleToStr(er.getDate21()));
			data.put(type + "_22", doubleToStr(er.getDate22()));
			data.put(type + "_23", doubleToStr(er.getDate23()));
			data.put(type + "_24", doubleToStr(er.getDate24()));
			data.put(type + "_25", doubleToStr(er.getDate25()));
			data.put(type + "_26", doubleToStr(er.getDate26()));
			data.put(type + "_27", doubleToStr(er.getDate27()));
			data.put(type + "_28", doubleToStr(er.getDate28()));
			data.put(type + "_29", doubleToStr(er.getDate29()));
			data.put(type + "_30", doubleToStr(er.getDate30()));
			data.put(type + "_31", doubleToStr(er.getDate31()));
		}
	}

	private String doubleToStr(double d){
		if(d == 0.0){
			return "";
		}else{
			return d + "";
		}
	}
	
	private double doubleInit(Object d){
		try {
			return Double.parseDouble(d.toString());
		} catch (Exception e) {
			return 0.0;
		}
	}

	@Override
	public List<EmpLeaveReport> getListByCondition(EmpLeaveReport model) {
		return empLeaveReportMapper.getListByCondition(model);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void batchUpdate(List<EmpLeaveReport> list) {
		if(null != list && !list.isEmpty()){
			for (EmpLeaveReport empLeaveReport : list) {
				if(null != empLeaveReport){
					empLeaveReport.setUpdateTime(CURRENT_TIME);
					empLeaveReport.setDelFlag(ConfigConstants.IS_NO_INTEGER);
					empLeaveReportMapper.update(empLeaveReport);
				}
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void asyncBatchSave(List<EmpLeaveReport> batchSaveList) {
		saveEmpLeaveReport(batchSaveList);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteByCondition(EmpLeaveReport elrModel) {
		empLeaveReportMapper.deleteByCondition(elrModel);
	}

	@Override
	public Map<String, List<Map<String, Object>>> getmonthLeaveSummary(Date startTime,
			Date endTime, Long departId, String[] monthTitles,List<Long> empTypeIdList) {	
		Map<String, List<Map<String, Object>>> datas = new LinkedHashMap<String, List<Map<String, Object>>>();
		Map<String, List<EmpLeaveReport>> temDatas = new LinkedHashMap<String, List<EmpLeaveReport>>();
		EmpLeaveReport model = new EmpLeaveReport();
		Employee model1 = new Employee();
		model1.setEmpTypeIdList(empTypeIdList);
		//获取月份参数
		List<String> months = new ArrayList<String>();
		for (int i = 0; i < monthTitles.length; i++) {
			months.add(monthTitles[i]);
			datas.put(monthTitles[i], new ArrayList<Map<String, Object>>());
			temDatas.put(monthTitles[i], new ArrayList<EmpLeaveReport>());
		}
		model.setMonths(months);
		//获取员工id
		if(null != departId){
			model.setDepartId(departId);
			model1.setDepartId(departId);
		}
		model1.setFirstEntryTime(endTime);
		
		List<EmpLeaveReport> empLeaveReports = empLeaveReportMapper.getListByCondition(model);
		
		Map<Long,Long> isExist = new HashMap<Long,Long>();
		
		for(EmpLeaveReport re:empLeaveReports){
			if(!isExist.containsKey(re.getEmployeeId())){
				isExist.put(re.getEmployeeId(), re.getEmployeeId());
			}
			
		}
		
		//获取所有员工
		model1.setWorkAddressType(0);
		List<Employee> employeeList = employeeMapper.getReportList(model1);
		Map<Long,Employee> employMap = new HashMap<Long,Employee>();
		Map<Long,String> departMap = new HashMap<>();
		if(employeeList!=null&&employeeList.size()>0){
			for(Employee employ:employeeList){
				//离职日期的年份在搜索开始日期的前一年的不要
				if(employ.getQuitTime()!=null&&employ.getQuitTime().getTime()<=DateUtils.parse(DateUtils.getYear(startTime)+"-01-01",DateUtils.FORMAT_SHORT).getTime()){
					continue;
				}
				Depart depart = departMapper.getByEmpId(employ.getId());
				String departName = depart!=null?depart.getName():"";
				if(depart!=null&&departMap.containsKey(depart.getId())){
					departName = departMap.get(departId);
				}else{
					if(depart!=null){
						departName = departService.getDepartAllLeaveName(depart.getId());
						departMap.put(depart.getId(),departName);
					}
				}
				employMap.put(employ.getId(), employ);
				if(isExist.containsKey(employ.getId())){
					model.setEmployeeId(employ.getId());
					List<EmpLeaveReport> empLeaveReport = empLeaveReportMapper.getListByCondition(model);
					for (EmpLeaveReport elr : empLeaveReport) {
						if(StringUtils.isNotBlank(elr.getMonth())){
							temDatas.get(elr.getMonth()).add(elr);
						}
					}
					continue;
				}
				List<EmpLeaveReport> list = null;
				Map<String, Map<String, Object>> temData = null;
				Map<String, Object> data = null;
				String key = "";
				String startMonth = DateUtils.getYearAndMonth(startTime);
				int startDay = Integer.parseInt(DateUtils.getDayOfMonth(startTime));
				String endMonth = DateUtils.getYearAndMonth(endTime);
				int endDay = Integer.parseInt(DateUtils.getDayOfMonth(endTime));
				for (String month : datas.keySet()) {
					temData = new LinkedHashMap<String, Map<String, Object>>();
					list = temDatas.get(month);
					key = depart!=null?employ.getId() +  "_" + employ.getCnName() + "_" + depart.getName():employ.getId() +  "_" + employ.getCnName() + "_";
					if(temData.containsKey(key)){
						data = temData.get(key);
					}else{
						data = new HashMap<String, Object>();
					}
					EmpLeaveReport elr = new EmpLeaveReport();
					elr.setEmployeeId(employ.getId());
					elr.setEmployeeName(employ.getCnName());
					elr.setDepartName("");
					if(depart!=null){
						elr.setDepartName(departMap.get(depart.getId()));
					}
					elr.setMonth(month);
					elr.setType(1);
					elr.setDate1(0);elr.setDate2(0);elr.setDate3(0);elr.setDate4(0);elr.setDate5(0);
					elr.setDate6(0);elr.setDate7(0);elr.setDate8(0);elr.setDate9(0);elr.setDate10(0);
					elr.setDate11(0);elr.setDate12(0);elr.setDate13(0);elr.setDate14(0);elr.setDate15(0);
					elr.setDate16(0);elr.setDate17(0);elr.setDate18(0);elr.setDate19(0);elr.setDate20(0);
					elr.setDate21(0);elr.setDate22(0);elr.setDate23(0);elr.setDate24(0);elr.setDate25(0);
					elr.setDate26(0);elr.setDate27(0);elr.setDate28(0);elr.setDate29(0);elr.setDate30(0);
					elr.setDate31(0);
					list.add(elr);
					temDatas.put(month, list);
				}
			}
		}
		List<EmpLeaveReport> list = null;
		Map<String, Map<String, Object>> temData = null;
		Map<String, Object> data = null;
		String key = "";
		String startMonth = DateUtils.getYearAndMonth(startTime);
		int startDay = Integer.parseInt(DateUtils.getDayOfMonth(startTime));
		String endMonth = DateUtils.getYearAndMonth(endTime);
		int endDay = Integer.parseInt(DateUtils.getDayOfMonth(endTime));
		for (String month : datas.keySet()) {
			temData = new LinkedHashMap<String, Map<String, Object>>();
			list = temDatas.get(month);
			if(null != list && !list.isEmpty()){
				for (EmpLeaveReport elr : list) {
					key = elr.getEmployeeId() +  "_" + elr.getEmployeeName() + "_" + elr.getDepartName();
					if(temData.containsKey(key)){
						data = temData.get(key);
					}else{
						data = new LinkedHashMap<String, Object>();
					}
					if(departMap.containsKey(elr.getDepartId())){
						elr.setDepartName(departMap.get(elr.getDepartId()));
					}
					temData.put(key, setData(data, elr, startMonth, startDay, endMonth, endDay,employMap));
				}
			}
			if(null != temData.values() && !temData.values().isEmpty()){
				for(Map<String, Object> bb:new ArrayList<Map<String, Object>>(temData.values())){
					datas.put(month, new ArrayList<Map<String, Object>>(temData.values()));
				}
			}
		}
		return datas;
	}
	
	@Override
	public Map<String, List<Map<String, Object>>> getmonthLeaveSummaryPageBean(Date startTime,
			Date endTime, Long departId, String[] monthTitles,Employee cp) {
		Map<String, List<Map<String, Object>>> datas = new LinkedHashMap<String, List<Map<String, Object>>>();
		Map<String, List<EmpLeaveReport>> temDatas = new LinkedHashMap<String, List<EmpLeaveReport>>();
		EmpLeaveReport model = new EmpLeaveReport();
		Employee model1 = new Employee();
		//获取月份参数
		List<String> months = new ArrayList<String>();
		for (int i = 0; i < monthTitles.length; i++) {
			months.add(monthTitles[i]);
			datas.put(monthTitles[i], new ArrayList<Map<String, Object>>());
			temDatas.put(monthTitles[i], new ArrayList<EmpLeaveReport>());
		}
		model.setMonths(months);
		//获取员工id
		if(null != departId){
			model.setDepartId(departId);
			model1.setDepartId(departId);
		}
		model1.setFirstEntryTime(endTime);
		model1.setCnName(cp.getCnName());
		model1.setCode(cp.getCode());
		model1.setCurrentUserDepart(cp.getCurrentUserDepart());
		model1.setSubEmployeeIdList(cp.getSubEmployeeIdList());
		model1.setEmpTypeIdList(cp.getEmpTypeIdList());
		
		List<EmpLeaveReport> empLeaveReports = empLeaveReportMapper.getListByCondition(model);
		Map<Long,Long> isExist = new HashMap<Long,Long>();
		
		for(EmpLeaveReport re:empLeaveReports){
			if(!isExist.containsKey(re.getEmployeeId())){
				isExist.put(re.getEmployeeId(), re.getEmployeeId());
			}
			
		}
		model1.setOffset(cp.getOffset());
		model1.setLimit(cp.getLimit());
		//获取所有员工
		model1.setWorkAddressType(0);
		List<Employee> employeeList = employeeMapper.getReportList(model1);
		Map<Long,Employee> employMap = new HashMap<Long,Employee>();
		Map<Long,String> departMap = new HashMap<>();
		if(employeeList!=null&&employeeList.size()>0){
			for(Employee employ:employeeList){
				//离职日期的年份在搜索开始日期的前一年的不要
				if(employ.getQuitTime()!=null&&employ.getQuitTime().getTime()<=DateUtils.parse(DateUtils.getYear(startTime)+"-01-01",DateUtils.FORMAT_SHORT).getTime()){
					continue;
				}
				Depart depart = departMapper.getByEmpId(employ.getId());
				if(depart==null){
					
				}else{
				String departName = depart.getName();
				if(departMap.containsKey(depart.getId())){
					departName = departMap.get(departId);
				}else{
					departName = departService.getDepartAllLeaveName(depart.getId());
					departMap.put(depart.getId(),departName);
				}
				employMap.put(employ.getId(), employ);
				if(isExist.containsKey(employ.getId())){
					model.setEmployeeId(employ.getId());
					List<EmpLeaveReport> empLeaveReport = empLeaveReportMapper.getListByCondition(model);
					for (EmpLeaveReport elr : empLeaveReport) {
						if(StringUtils.isNotBlank(elr.getMonth())){
							temDatas.get(elr.getMonth()).add(elr);
						}
					}
					continue;
				}
				List<EmpLeaveReport> list = null;
				Map<String, Map<String, Object>> temData = null;
				Map<String, Object> data = null;
				String key = "";
				String startMonth = DateUtils.getYearAndMonth(startTime);
				int startDay = Integer.parseInt(DateUtils.getDayOfMonth(startTime));
				String endMonth = DateUtils.getYearAndMonth(endTime);
				int endDay = Integer.parseInt(DateUtils.getDayOfMonth(endTime));
				for (String month : datas.keySet()) {
					temData = new LinkedHashMap<String, Map<String, Object>>();
					list = temDatas.get(month);
					key = employ.getId() +  "_" + employ.getCnName() + "_" + depart.getName();
					if(temData.containsKey(key)){
						data = temData.get(key);
					}else{
						data = new HashMap<String, Object>();
					}
					EmpLeaveReport elr = new EmpLeaveReport();
					elr.setEmployeeId(employ.getId());
					elr.setEmployeeName(employ.getCnName());
					elr.setDepartName(departMap.get(depart.getId()));
					elr.setMonth(month);
					elr.setType(1);
					elr.setDate1(0);elr.setDate2(0);elr.setDate3(0);elr.setDate4(0);elr.setDate5(0);
					elr.setDate6(0);elr.setDate7(0);elr.setDate8(0);elr.setDate9(0);elr.setDate10(0);
					elr.setDate11(0);elr.setDate12(0);elr.setDate13(0);elr.setDate14(0);elr.setDate15(0);
					elr.setDate16(0);elr.setDate17(0);elr.setDate18(0);elr.setDate19(0);elr.setDate20(0);
					elr.setDate21(0);elr.setDate22(0);elr.setDate23(0);elr.setDate24(0);elr.setDate25(0);
					elr.setDate26(0);elr.setDate27(0);elr.setDate28(0);elr.setDate29(0);elr.setDate30(0);
					elr.setDate31(0);
					list.add(elr);
					temDatas.put(month, list);
				}
			}
		  }		
		}
		List<EmpLeaveReport> list = null;
		Map<String, Map<String, Object>> temData = null;
		Map<String, Object> data = null;
		String key = "";
		String startMonth = DateUtils.getYearAndMonth(startTime);
		int startDay = Integer.parseInt(DateUtils.getDayOfMonth(startTime));
		String endMonth = DateUtils.getYearAndMonth(endTime);
		int endDay = Integer.parseInt(DateUtils.getDayOfMonth(endTime));
		for (String month : datas.keySet()) {
			temData = new LinkedHashMap<String, Map<String, Object>>();
			list = temDatas.get(month);
			if(null != list && !list.isEmpty()){
				for (EmpLeaveReport elr : list) {
					key = elr.getEmployeeId() +  "_" + elr.getEmployeeName() + "_" + elr.getDepartName();
					if(temData.containsKey(key)){
						data = temData.get(key);
					}else{
						data = new LinkedHashMap<String, Object>();
					}
					if(departMap.containsKey(elr.getDepartId())){
						elr.setDepartName(departMap.get(elr.getDepartId()));
					}
					temData.put(key, setData(data, elr, startMonth, startDay, endMonth, endDay,employMap));
				}
			}
			if(null != temData.values() && !temData.values().isEmpty()){
				for(Map<String, Object> bb:new ArrayList<Map<String, Object>>(temData.values())){
					datas.put(month, new ArrayList<Map<String, Object>>(temData.values()));
				}
			}
		}
		return datas;
	}

	private Map<String, Object> setData(Map<String, Object> data,
			EmpLeaveReport elr, String startMonth, int startDay,
			String endMonth, int endDay,Map<Long,Employee> employMap) {
		data.put("code", employMap.get(elr.getEmployeeId()).getCode());
		data.put("cnName", elr.getEmployeeName());
		data.put("departName", elr.getDepartName());
		data.put("type", elr.getType());
		if(data.get("count_" + ConfigConstants.LEAVE_TYPE_1) == null){
			data.put("count_" + ConfigConstants.LEAVE_TYPE_1, 0);
		}
		if(data.get("days_" + ConfigConstants.LEAVE_TYPE_1) == null){
			data.put("days_" + ConfigConstants.LEAVE_TYPE_1, 0);
		}
		if(data.get("count_" + ConfigConstants.LEAVE_TYPE_2) == null){
			data.put("count_" + ConfigConstants.LEAVE_TYPE_2, 0);
		}
		if(data.get("days_" + ConfigConstants.LEAVE_TYPE_2) == null){
			data.put("days_" + ConfigConstants.LEAVE_TYPE_2, 0);
		}
		if(data.get("count_" + ConfigConstants.LEAVE_TYPE_3) == null){
			data.put("count_" + ConfigConstants.LEAVE_TYPE_3, 0);
		}
		if(data.get("days_" + ConfigConstants.LEAVE_TYPE_3) == null){
			data.put("days_" + ConfigConstants.LEAVE_TYPE_3, 0);
		}
		if(data.get("count_" + ConfigConstants.LEAVE_TYPE_5) == null){
			data.put("count_" + ConfigConstants.LEAVE_TYPE_5, 0);
		}
		if(data.get("days_" + ConfigConstants.LEAVE_TYPE_5) == null){
			data.put("days_" + ConfigConstants.LEAVE_TYPE_5, 0);
		}
		if(data.get("count_" + ConfigConstants.LEAVE_TYPE_6) == null){
			data.put("count_" + ConfigConstants.LEAVE_TYPE_6, 0);
		}
		if(data.get("days_" + ConfigConstants.LEAVE_TYPE_6) == null){
			data.put("days_" + ConfigConstants.LEAVE_TYPE_6, 0);
		}
		if(data.get("count_" + ConfigConstants.LEAVE_TYPE_7) == null){
			data.put("count_" + ConfigConstants.LEAVE_TYPE_7, 0);
		}
		if(data.get("days_" + ConfigConstants.LEAVE_TYPE_7) == null){
			data.put("days_" + ConfigConstants.LEAVE_TYPE_7, 0);
		}
		if(data.get("count_" + ConfigConstants.LEAVE_TYPE_8) == null){
			data.put("count_" + ConfigConstants.LEAVE_TYPE_8, 0);
		}
		if(data.get("days_" + ConfigConstants.LEAVE_TYPE_8) == null){
			data.put("days_" + ConfigConstants.LEAVE_TYPE_8, 0);
		}
		if(data.get("count_" + ConfigConstants.LEAVE_TYPE_9) == null){
			data.put("count_" + ConfigConstants.LEAVE_TYPE_9, 0);
		}
		if(data.get("days_" + ConfigConstants.LEAVE_TYPE_9) == null){
			data.put("days_" + ConfigConstants.LEAVE_TYPE_9, 0);
		}
		if(data.get("count_" + ConfigConstants.LEAVE_TYPE_10) == null){
			data.put("count_" + ConfigConstants.LEAVE_TYPE_10, 0);
		}
		if(data.get("days_" + ConfigConstants.LEAVE_TYPE_10) == null){
			data.put("days_" + ConfigConstants.LEAVE_TYPE_10, 0);
		}
		if(data.get("count_" + ConfigConstants.LEAVE_TYPE_11) == null){
			data.put("count_" + ConfigConstants.LEAVE_TYPE_11, 0);
		}
		if(data.get("days_" + ConfigConstants.LEAVE_TYPE_11) == null){
			data.put("days_" + ConfigConstants.LEAVE_TYPE_11, 0);
		}
		
		double dCount = daySummery(elr, startMonth, startDay,
			 endMonth, endDay);
		double dDays = 1;
		String keyCount = "count_" + elr.getType();
		String keyDays = "days_" + elr.getType();
		if(data.containsKey(keyCount)){
			dCount = dCount + Double.parseDouble(data.get(keyCount).toString());
		}
		if(data.containsKey(keyDays)){
			dDays = dDays + Double.parseDouble(data.get(keyDays).toString());
		}
		data.put(keyCount, dDays);
		data.put(keyDays, dCount);
		return data;
	}

	private double dayTimeSummery(EmpLeaveReport elr, String startMonth, int startDay, String endMonth, int endDay) {
		double i = 0;
		if(startMonth.equals(elr.getMonth())){
			if(startDay <= 1) {
				i += countByType(elr.getDate1(), elr.getType());
			}
			if(startDay <= 2) {
				i += countByType(elr.getDate2(), elr.getType());
			}
			if(startDay <= 3) {
				i += countByType(elr.getDate3(), elr.getType());
			}
			if(startDay <= 4) {
				i += countByType(elr.getDate4(), elr.getType());
			}
			if(startDay <= 5) {
				i += countByType(elr.getDate5(), elr.getType());
			}
			if(startDay <= 6) {
				i += countByType(elr.getDate6(), elr.getType());
			}
			if(startDay <= 7) {
				i += countByType(elr.getDate7(), elr.getType());
			}
			if(startDay <= 8) {
				i += countByType(elr.getDate8(), elr.getType());
			}
			if(startDay <= 9) {
				i += countByType(elr.getDate9(), elr.getType());
			}
			if(startDay <= 10) {
				i += countByType(elr.getDate10(), elr.getType());
			}
			if(startDay <= 11) {
				i += countByType(elr.getDate11(), elr.getType());
			}
			if(startDay <= 12) {
				i += countByType(elr.getDate12(), elr.getType());
			}
			if(startDay <= 13) {
				i += countByType(elr.getDate13(), elr.getType());
			}
			if(startDay <= 14) {
				i += countByType(elr.getDate14(), elr.getType());
			}
			if(startDay <= 15) {
				i += countByType(elr.getDate15(), elr.getType());
			}
			if(startDay <= 16) {
				i += countByType(elr.getDate16(), elr.getType());
			}
			if(startDay <= 17) {
				i += countByType(elr.getDate17(), elr.getType());
			}
			if(startDay <= 18) {
				i += countByType(elr.getDate18(), elr.getType());
			}
			if(startDay <= 19) {
				i += countByType(elr.getDate19(), elr.getType());
			}
			if(startDay <= 20) {
				i += countByType(elr.getDate20(), elr.getType());
			}
			if(startDay <= 21) {
				i += countByType(elr.getDate21(), elr.getType());
			}
			if(startDay <= 22) {
				i += countByType(elr.getDate22(), elr.getType());
			}
			if(startDay <= 23) {
				i += countByType(elr.getDate23(), elr.getType());
			}
			if(startDay <= 24) {
				i += countByType(elr.getDate24(), elr.getType());
			}
			if(startDay <= 25) {
				i += countByType(elr.getDate25(), elr.getType());
			}
			if(startDay <= 26) {
				i += countByType(elr.getDate26(), elr.getType());
			}
			if(startDay <= 27) {
				i += countByType(elr.getDate27(), elr.getType());
			}
			if(startDay <= 28) {
				i += countByType(elr.getDate28(), elr.getType());
			}
			if(startDay <= 29) {
				i += countByType(elr.getDate29(), elr.getType());
			}
			if(startDay <= 30) {
				i += countByType(elr.getDate30(), elr.getType());
			}
			if(startDay <= 31) {
				i += countByType(elr.getDate31(), elr.getType());
			}
		}else if(endMonth.equals(elr.getMonth())){
			if(endDay >= 1) {
				i += countByType(elr.getDate1(), elr.getType());
			}
			if(endDay >= 2) {
				i += countByType(elr.getDate2(), elr.getType());
			}
			if(endDay >= 3) {
				i += countByType(elr.getDate3(), elr.getType());
			}
			if(endDay >= 4) {
				i += countByType(elr.getDate4(), elr.getType());
			}
			if(endDay >= 5) {
				i += countByType(elr.getDate5(), elr.getType());
			}
			if(endDay >= 6) {
				i += countByType(elr.getDate6(), elr.getType());
			}
			if(endDay >= 7) {
				i += countByType(elr.getDate7(), elr.getType());
			}
			if(endDay >= 8) {
				i += countByType(elr.getDate8(), elr.getType());
			}
			if(endDay >= 9) {
				i += countByType(elr.getDate9(), elr.getType());
			}
			if(endDay >= 10) {
				i += countByType(elr.getDate10(), elr.getType());
			}
			if(endDay >= 11) {
				i += countByType(elr.getDate11(), elr.getType());
			}
			if(endDay >= 12) {
				i += countByType(elr.getDate12(), elr.getType());
			}
			if(endDay >= 13) {
				i += countByType(elr.getDate13(), elr.getType());
			}
			if(endDay >= 14) {
				i += countByType(elr.getDate14(), elr.getType());
			}
			if(endDay >= 15) {
				i += countByType(elr.getDate15(), elr.getType());
			}
			if(endDay >= 16) {
				i += countByType(elr.getDate16(), elr.getType());
			}
			if(endDay >= 17) {
				i += countByType(elr.getDate17(), elr.getType());
			}
			if(endDay >= 18) {
				i += countByType(elr.getDate18(), elr.getType());
			}
			if(endDay >= 19) {
				i += countByType(elr.getDate19(), elr.getType());
			}
			if(endDay >= 20) {
				i += countByType(elr.getDate20(), elr.getType());
			}
			if(endDay >= 21) {
				i += countByType(elr.getDate21(), elr.getType());
			}
			if(endDay >= 22) {
				i += countByType(elr.getDate22(), elr.getType());
			}
			if(endDay >= 23) {
				i += countByType(elr.getDate23(), elr.getType());
			}
			if(endDay >= 24) {
				i += countByType(elr.getDate24(), elr.getType());
			}
			if(endDay >= 25) {
				i += countByType(elr.getDate25(), elr.getType());
			}
			if(endDay >= 26) {
				i += countByType(elr.getDate26(), elr.getType());
			}
			if(endDay >= 27) {
				i += countByType(elr.getDate27(), elr.getType());
			}
			if(endDay >= 28) {
				i += countByType(elr.getDate28(), elr.getType());
			}
			if(endDay >= 29) {
				i += countByType(elr.getDate29(), elr.getType());
			}
			if(endDay >= 30) {
				i += countByType(elr.getDate30(), elr.getType());
			}
			if(endDay >= 31) {
				i += countByType(elr.getDate31(), elr.getType());
			}
		}else{
			i += countByType(elr.getDate1(), elr.getType());
			i += countByType(elr.getDate2(), elr.getType());
			i += countByType(elr.getDate3(), elr.getType());
			i += countByType(elr.getDate4(), elr.getType());
			i += countByType(elr.getDate5(), elr.getType());
			i += countByType(elr.getDate6(), elr.getType());
			i += countByType(elr.getDate7(), elr.getType());
			i += countByType(elr.getDate8(), elr.getType());
			i += countByType(elr.getDate9(), elr.getType());
			i += countByType(elr.getDate10(), elr.getType());
			i += countByType(elr.getDate11(), elr.getType());
			i += countByType(elr.getDate12(), elr.getType());
			i += countByType(elr.getDate13(), elr.getType());
			i += countByType(elr.getDate14(), elr.getType());
			i += countByType(elr.getDate15(), elr.getType());
			i += countByType(elr.getDate16(), elr.getType());
			i += countByType(elr.getDate17(), elr.getType());
			i += countByType(elr.getDate18(), elr.getType());
			i += countByType(elr.getDate19(), elr.getType());
			i += countByType(elr.getDate20(), elr.getType());
			i += countByType(elr.getDate21(), elr.getType());
			i += countByType(elr.getDate22(), elr.getType());
			i += countByType(elr.getDate23(), elr.getType());
			i += countByType(elr.getDate24(), elr.getType());
			i += countByType(elr.getDate25(), elr.getType());
			i += countByType(elr.getDate26(), elr.getType());
			i += countByType(elr.getDate27(), elr.getType());
			i += countByType(elr.getDate28(), elr.getType());
			i += countByType(elr.getDate29(), elr.getType());
			i += countByType(elr.getDate30(), elr.getType());
			i += countByType(elr.getDate31(), elr.getType());
		}
		return i;
	}
	
	private int countByType(double dateValue, Integer type) {
		if(ConfigConstants.LEAVE_TYPE_1.intValue() == type.intValue() || ConfigConstants.LEAVE_TYPE_2.intValue() == type.intValue() || ConfigConstants.LEAVE_TYPE_11.intValue() == type.intValue()){
			//*当月年假、病假、事假次数，每大于等于0.5天算1次，
			if(dateValue >= 0.5){
				return 1;
			}
		}else if(ConfigConstants.LEAVE_TYPE_5.intValue() == type.intValue()){
			//调休大于等于3小时算1次
			if(dateValue >= 3){
				return 1;
			}
		}else if(ConfigConstants.LEAVE_TYPE_3.intValue() == type.intValue() || ConfigConstants.LEAVE_TYPE_10.intValue() == type.intValue() || ConfigConstants.LEAVE_TYPE_7.intValue() == type.intValue() || ConfigConstants.LEAVE_TYPE_9.intValue() == type.intValue() || ConfigConstants.LEAVE_TYPE_6.intValue() == type.intValue() || ConfigConstants.LEAVE_TYPE_8.intValue() == type.intValue()){
			//剩余婚假、丧假、产假、陪产假、产前假、流产假等假期，1天算1次
			if(dateValue == 1){
				return 1;
			}
		}
		return 0;
	}

	private double daySummery(EmpLeaveReport elr, String startMonth, int startDay, String endMonth, int endDay) {
		double i = 0;
		if(startMonth.equals(elr.getMonth())){
			if(startDay <= 1) {
				i += elr.getDate1();
			}
			if(startDay <= 2) {
				i += elr.getDate2();
			}
			if(startDay <= 3) {
				i += elr.getDate3();
			}
			if(startDay <= 4) {
				i += elr.getDate4();
			}
			if(startDay <= 5) {
				i += elr.getDate5();
			}
			if(startDay <= 6) {
				i += elr.getDate6();
			}
			if(startDay <= 7) {
				i += elr.getDate7();
			}
			if(startDay <= 8) {
				i += elr.getDate8();
			}
			if(startDay <= 9) {
				i += elr.getDate9();
			}
			if(startDay <= 10) {
				i += elr.getDate10();
			}
			if(startDay <= 11) {
				i += elr.getDate11();
			}
			if(startDay <= 12) {
				i += elr.getDate12();
			}
			if(startDay <= 13) {
				i += elr.getDate13();
			}
			if(startDay <= 14) {
				i += elr.getDate14();
			}
			if(startDay <= 15) {
				i += elr.getDate15();
			}
			if(startDay <= 16) {
				i += elr.getDate16();
			}
			if(startDay <= 17) {
				i += elr.getDate17();
			}
			if(startDay <= 18) {
				i += elr.getDate18();
			}
			if(startDay <= 19) {
				i += elr.getDate19();
			}
			if(startDay <= 20) {
				i += elr.getDate20();
			}
			if(startDay <= 21) {
				i += elr.getDate21();
			}
			if(startDay <= 22) {
				i += elr.getDate22();
			}
			if(startDay <= 23) {
				i += elr.getDate23();
			}
			if(startDay <= 24) {
				i += elr.getDate24();
			}
			if(startDay <= 25) {
				i += elr.getDate25();
			}
			if(startDay <= 26) {
				i += elr.getDate26();
			}
			if(startDay <= 27) {
				i += elr.getDate27();
			}
			if(startDay <= 28) {
				i += elr.getDate28();
			}
			if(startDay <= 29) {
				i += elr.getDate29();
			}
			if(startDay <= 30) {
				i += elr.getDate30();
			}
			if(startDay <= 31) {
				i += elr.getDate31();	
			}
		}else if(endMonth.equals(elr.getMonth())){
			if(endDay >= 1) {
				i += elr.getDate1();
			}
			if(endDay >= 2) {
				i += elr.getDate2();
			}
			if(endDay >= 3) {
				i += elr.getDate3();
			}
			if(endDay >= 4) {
				i += elr.getDate4();
			}
			if(endDay >= 5) {
				i += elr.getDate5();
			}
			if(endDay >= 6) {
				i += elr.getDate6();
			}
			if(endDay >= 7) {
				i += elr.getDate7();
			}
			if(endDay >= 8) {
				i += elr.getDate8();
			}
			if(endDay >= 9) {
				i += elr.getDate9();
			}
			if(endDay >= 10) {
				i += elr.getDate10();
			}
			if(endDay >= 11) {
				i += elr.getDate11();
			}
			if(endDay >= 12) {
				i += elr.getDate12();
			}
			if(endDay >= 13) {
				i += elr.getDate13();
			}
			if(endDay >= 14) {
				i += elr.getDate14();
			}
			if(endDay >= 15) {
				i += elr.getDate15();
			}
			if(endDay >= 16) {
				i += elr.getDate16();
			}
			if(endDay >= 17) {
				i += elr.getDate17();
			}
			if(endDay >= 18) {
				i += elr.getDate18();
			}
			if(endDay >= 19) {
				i += elr.getDate19();
			}
			if(endDay >= 20) {
				i += elr.getDate20();
			}
			if(endDay >= 21) {
				i += elr.getDate21();
			}
			if(endDay >= 22) {
				i += elr.getDate22();
			}
			if(endDay >= 23) {
				i += elr.getDate23();
			}
			if(endDay >= 24) {
				i += elr.getDate24();
			}
			if(endDay >= 25) {
				i += elr.getDate25();
			}
			if(endDay >= 26) {
				i += elr.getDate26();
			}
			if(endDay >= 27) {
				i += elr.getDate27();
			}
			if(endDay >= 28) {
				i += elr.getDate28();
			}
			if(endDay >= 29) {
				i += elr.getDate29();
			}
			if(endDay >= 30) {
				i += elr.getDate30();
			}
			if(endDay >= 31) {
				i += elr.getDate31();
			}
		}else{
			i += elr.getDate1();
			i += elr.getDate2();
			i += elr.getDate3();
			i += elr.getDate4();
			i += elr.getDate5();
			i += elr.getDate6();
			i += elr.getDate7();
			i += elr.getDate8();
			i += elr.getDate9();
			i += elr.getDate10();
			i += elr.getDate11();
			i += elr.getDate12();
			i += elr.getDate13();
			i += elr.getDate14();
			i += elr.getDate15();
			i += elr.getDate16();
			i += elr.getDate17();
			i += elr.getDate18();
			i += elr.getDate19();
			i += elr.getDate20();
			i += elr.getDate21();
			i += elr.getDate22();
			i += elr.getDate23();
			i += elr.getDate24();
			i += elr.getDate25();
			i += elr.getDate26();
			i += elr.getDate27();
			i += elr.getDate28();
			i += elr.getDate29();
			i += elr.getDate30();
			i += elr.getDate31();
		}
		return i;
	}

	/**
	 * 统计所有假期报表时清除掉报表中的所有数据(注意:全表删除)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteAll() {
		empLeaveReportMapper.deleteAll();
	}
}
