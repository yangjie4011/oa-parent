package com.ule.oa.base.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import com.ule.oa.base.mapper.AttnReportMapper;
import com.ule.oa.base.mapper.AttnStatisticsMapper;
import com.ule.oa.base.mapper.DelayWorkRegisterMapper;
import com.ule.oa.base.mapper.DinnerAndTrafficAllowanceDayMapper;
import com.ule.oa.base.mapper.EmpApplicationOvertimeMapper;
import com.ule.oa.base.mapper.EmployeeClassMapper;
import com.ule.oa.base.po.AttnStatistics;
import com.ule.oa.base.po.DelayWorkRegister;
import com.ule.oa.base.po.EmpApplicationOvertime;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.po.TrafficAllowanceMonthSummaryDTO;
import com.ule.oa.base.po.tbl.AttnReportTbl;
import com.ule.oa.base.po.tbl.DinnerAndTrafficAllowanceDayTbl;
import com.ule.oa.base.service.AnnualVacationService;
import com.ule.oa.base.service.AttnReportService;
import com.ule.oa.base.service.EmployeeClassService;
import com.ule.oa.common.utils.DateUtils;

@Service
public class AttnReportServiceImpl implements AttnReportService {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public static final String TIME_21 = " 21:00:00";
	public static final String TIME_23 = " 23:00:00";
	public static final String CLASS_NORMAL = "常白班";
	public static final String CLASS_REST = "休息";
	public static final String TRAFFIC_ALLOWANCE_45 = "45";
	public static final String TRAFFIC_ALLOWANCE_SBSX = "实报实销";
	
	@Autowired
	private AnnualVacationService annualVacationService;
	@Autowired
	private AttnStatisticsMapper attnStatisticsMapper;
	@Autowired
	private DelayWorkRegisterMapper delayWorkRegisterMapper;
	@Autowired
	private EmpApplicationOvertimeMapper empApplicationOvertimeMapper;
	@Autowired
	private EmployeeClassMapper employeeClassMapper;
	@Autowired
	private AttnReportMapper attnReportMapper;
	@Autowired
	private DinnerAndTrafficAllowanceDayMapper dinnerAndTrafficAllowanceDayMapper;
	@Autowired
	private EmployeeClassService employeeClassService;


	@Override
	@Transactional(rollbackFor = Exception.class)
	public void generateDayWCandJTallowReportByEmp(Employee employee,Date lastMonthStart,Date lastMonthEnd,
			Map<Long,String> workTypeMap,Map<Long,String> whetherSchedulingMap,Map<Date,Integer> vacationMap,String optUser) {
		
		//餐补与交通补贴头表
		AttnReportTbl attnReport = new AttnReportTbl();
		attnReport.setEmployeeId(employee.getId());
		attnReport.setEmployeeCode(employee.getCode());
		attnReport.setEmployeeName(employee.getCnName());
		attnReport.setDepartId(employee.getDepartId());
		attnReport.setDepartName(employee.getDepartName());
		attnReport.setPositionName(employee.getPositionName());
		attnReport.setReportType(2);
		attnReport.setStatisticType(3);
		attnReport.setDelFlag(0);
		attnReport.setWorkType(workTypeMap.get(employee.getWorkType()));
		attnReport.setCreateUser(optUser);
		//餐补与交通补贴明细
		List<DinnerAndTrafficAllowanceDayTbl> addList = new ArrayList<DinnerAndTrafficAllowanceDayTbl>();
		
		if(workTypeMap!=null&&workTypeMap.containsKey(employee.getWorkType())){
			
			if("comprehensive".equals(workTypeMap.get(employee.getWorkType()))){
				
				/**
				 * 餐补
				 *  综合工时无餐补。
				 * 
				 * 交通补贴
				 *  工作日&非工作日：看考勤时间
					小于21:00=0
				           大于等于21:00小于23:00=45
		                                   大于等于23:00=实报实销
				 * 
				 */
				
				//直接看上月考勤
				AttnStatistics attnParam = new AttnStatistics();
				attnParam.setStartTime(lastMonthStart);
				attnParam.setEndTime(lastMonthEnd);
				attnParam.setEmployId(employee.getId());
				
				List<AttnStatistics> attnList = attnStatisticsMapper.getAttStatisticsList(attnParam);
				for(AttnStatistics data:attnList){
					DinnerAndTrafficAllowanceDayTbl reportData = new DinnerAndTrafficAllowanceDayTbl();
					reportData.setAttnDate(data.getAttnDate());
					if(annualVacationService.judgeWorkOrNot(data.getAttnDate(), vacationMap)){
						reportData.setAttnClass(CLASS_NORMAL);
					}else{
						reportData.setAttnClass(CLASS_REST);
					}
					reportData.setCreateUser(optUser);
					reportData.setCreateTime(new Date());
					reportData.setDelFlag(0);
					reportData.setDinnerAllowance(0);//餐补
					reportData.setOffDutyTime(data.getEndWorkTime());//下班时间
					reportData.setWeek("星期"+DateUtils.getWeek(data.getAttnDate()));//星期几
					reportData.setOverTimeHours(0d);//综合工时无加班
					reportData.setTraffiAllowance("0");
					Date attnDate9Time = DateUtils.parse(DateUtils.format(data.getAttnDate(), DateUtils.FORMAT_SHORT)
							+TIME_21, DateUtils.FORMAT_LONG);
					Date attnDate23Time = DateUtils.parse(DateUtils.format(data.getAttnDate(), DateUtils.FORMAT_SHORT)
							+ TIME_23, DateUtils.FORMAT_LONG);
					if(data.getEndWorkTime()!=null&&data.getEndWorkTime().getTime()>=attnDate9Time.getTime()
							&&data.getEndWorkTime().getTime()<attnDate23Time.getTime()){
						reportData.setTraffiAllowance(TRAFFIC_ALLOWANCE_45);
					}else if(data.getEndWorkTime()!=null&&data.getEndWorkTime().getTime()>=attnDate23Time.getTime()){
						reportData.setTraffiAllowance(TRAFFIC_ALLOWANCE_SBSX);
					}
					addList.add(reportData);
				}
				
				//保存报表数据
				if(addList!=null&&addList.size()>0){
					//删除历史数据
					AttnReportTbl oldAttnReport = attnReportMapper.getDinnerAndTrafficAllowanceDay(employee.getId(),lastMonthStart, lastMonthEnd);
					if(oldAttnReport!=null){
						attnReportMapper.deleteById(oldAttnReport.getId(),optUser);
						dinnerAndTrafficAllowanceDayMapper.deleteByReportId(oldAttnReport.getId(),optUser);
					}
					//生成新的报表数据
					attnReportMapper.save(attnReport);
					for(DinnerAndTrafficAllowanceDayTbl data:addList){
						data.setReportId(attnReport.getId());
					}
					dinnerAndTrafficAllowanceDayMapper.batchSave(addList);
				}

			}else if("standard".equals(workTypeMap.get(employee.getWorkType()))){
				
				if(whetherSchedulingMap!=null&&"yes".equals(whetherSchedulingMap.get(employee.getWhetherScheduling()))){
					/**
					 * 餐补
					 * 
					 * 	工作日（包括排班与常日班），员工“延时工作小时数”≥2小时，餐补15元
			        	非工作日（节假日/周末/非排班日） 当天“延时工作小时数”≥8小时，餐补15元
					 *
					 * 交通补贴
					 *
					       工作日：班次结束时间小于21:00看延时工作小时数
						大于等于1再看考勤时间
						小于21:00=0
						大于等于21:00小于23:00=45
						大于等于23:00=实报实销
			
						班次结束时间大于等于21:00小于23:00看下班考勤时间
						小于21:00=0
						大于等于21:00小于23:00=45
						大于等于23:00看延时工作小时数
							延时工作小时数大于等于1h=实报实销
							延时工作小时数小于23:00=45
			
						班次结束时间大于等于23:00看下班考勤时间
						小于21:00=0
						大于等于21:00小于23:00=45
						大于等于23:00=实报实销

						非工作日：
									延时工作小时数小于1=0
						延时工作小时数大于等于1
						考勤时间小于21:00=0
						考勤时间大于等于21:00小于23:00=45
										考勤时间大于等于23:00=实报实销
					 *
					 */
					
					//直接看上月考勤
					AttnStatistics attnParam = new AttnStatistics();
					attnParam.setStartTime(lastMonthStart);
					attnParam.setEndTime(lastMonthEnd);
					attnParam.setEmployId(employee.getId());
					List<AttnStatistics> attnList = attnStatisticsMapper.getAttStatisticsList(attnParam);
					//查询该员工上月延时工作登记
					List<DelayWorkRegister> delayWorkList = delayWorkRegisterMapper.getDelayWorkRegisterByMonth(employee.getId(), lastMonthStart, lastMonthEnd);
					Map<Date,Double> delayWorkMap = delayWorkList.stream().collect(Collectors.toMap(DelayWorkRegister :: getDelayDate, DelayWorkRegister :: getActualDelayHour));
					//查询该员工上月延时工作申请（只针对法定节假日）
					EmpApplicationOvertime overtimeParam = new EmpApplicationOvertime();
					overtimeParam.setStartTime(lastMonthStart);
					overtimeParam.setEndTime(lastMonthEnd);
					overtimeParam.setEmployeeId(employee.getId());
					List<EmpApplicationOvertime> overtimeList = empApplicationOvertimeMapper.getCompleteList(overtimeParam);
					Map<Date,Double> overtimeMap = overtimeList.stream().collect(Collectors.toMap(EmpApplicationOvertime :: getApplyDate, EmpApplicationOvertime :: getActualDuration));
					//查询员工上月所有排班
					EmployeeClass classParam = new EmployeeClass();
					classParam.setStartTime(lastMonthStart);
					classParam.setEndTime(lastMonthEnd);
					classParam.setEmployId(employee.getId());
					List<EmployeeClass> classList = employeeClassMapper.getEmployeeClassList(classParam);
					Map<Date,EmployeeClass> classNameMap = classList.stream().collect(Collectors.toMap(EmployeeClass :: getClassDate,a->a,(k1,k2)->k1));
					
					for(AttnStatistics data:attnList){
						generateDayWCandJTallowReport1(data,classNameMap,delayWorkMap,
								overtimeMap,vacationMap,optUser,addList);
					}
					
					//保存报表数据
					if(addList!=null&&addList.size()>0){
						//删除历史数据
						AttnReportTbl oldAttnReport = attnReportMapper.getDinnerAndTrafficAllowanceDay(employee.getId(),lastMonthStart, lastMonthEnd);
						if(oldAttnReport!=null){
							attnReportMapper.deleteById(oldAttnReport.getId(),optUser);
							dinnerAndTrafficAllowanceDayMapper.deleteByReportId(oldAttnReport.getId(),optUser);
						}
						//生成新的报表数据
						attnReportMapper.save(attnReport);
						for(DinnerAndTrafficAllowanceDayTbl data:addList){
							data.setReportId(attnReport.getId());
						}
						dinnerAndTrafficAllowanceDayMapper.batchSave(addList);
					}
					
				}else if(whetherSchedulingMap!=null&&"no".equals(whetherSchedulingMap.get(employee.getWhetherScheduling()))){
					/**
					 * 餐补
					 *  工作日（包括排班与常日班），员工“延时工作小时数”≥2小时，餐补15元
						非工作日（节假日/周末/非排班日） 当天“延时工作小时数”≥8小时，餐补15元
					 * 
					 * 交通补贴
					 *  工作日：延时工作小时数小于2=0
							延时工作小时数大于等于2小于4=45
							延时工作小时数大于等于4=实报实销
						非工作日：
							延时工作小时数小于1=0
							延时工作小时数大于等于1
							考勤时间小于21:00=0
							考勤时间大于等于21:00小于23:00=45
							考勤时间大于等于23:00=实报实销
					 */
					
					
					//直接看上月考勤
					AttnStatistics attnParam = new AttnStatistics();
					attnParam.setStartTime(lastMonthStart);
					attnParam.setEndTime(lastMonthEnd);
					attnParam.setEmployId(employee.getId());
					List<AttnStatistics> attnList = attnStatisticsMapper.getAttStatisticsList(attnParam);
					//查询该员工上月延时工作登记
					List<DelayWorkRegister> delayWorkList = delayWorkRegisterMapper.getDelayWorkRegisterByMonth(employee.getId(), lastMonthStart, lastMonthEnd);
					Map<Date,Double> delayWorkMap = delayWorkList.stream().collect(Collectors.toMap(DelayWorkRegister :: getDelayDate, DelayWorkRegister :: getActualDelayHour));
					//查询该员工上月延时工作申请（只针对法定节假日）
					EmpApplicationOvertime overtimeParam = new EmpApplicationOvertime();
					overtimeParam.setStartTime(lastMonthStart);
					overtimeParam.setEndTime(lastMonthEnd);
					overtimeParam.setEmployeeId(employee.getId());
					List<EmpApplicationOvertime> overtimeList = empApplicationOvertimeMapper.getCompleteList(overtimeParam);
					Map<Date,Double> overtimeMap = overtimeList.stream().collect(Collectors.toMap(EmpApplicationOvertime :: getApplyDate, EmpApplicationOvertime :: getActualDuration));
					
					EmployeeClass classParam = new EmployeeClass();
					classParam.setStartTime(lastMonthStart);
					classParam.setEndTime(lastMonthEnd);
					classParam.setEmployId(employee.getId());
					List<EmployeeClass> classList = employeeClassMapper.getEmployeeClassList(classParam);
					Map<Date,EmployeeClass> classNameMap = classList.stream().collect(Collectors.toMap(EmployeeClass :: getClassDate,a->a,(k1,k2)->k1));
					
					
					for(AttnStatistics data:attnList){
						
						EmployeeClass employeeClass = new EmployeeClass();
						employeeClass.setEmployId(employee.getId());
						employeeClass.setClassDate(data.getAttnDate());
						EmployeeClass empClass = employeeClassService.getEmployeeClassSetting(employeeClass);
						if(empClass!=null&&empClass.getStartTime()!=null){
							
							if("09:00".equals(DateUtils.format(empClass.getStartTime(), DateUtils.FORMAT_HH_MM))
									&&"18:00".equals(DateUtils.format(empClass.getEndTime(), DateUtils.FORMAT_HH_MM))){
								
								generateDayWCandJTallowReport2(data,delayWorkMap,
										overtimeMap,vacationMap,optUser,addList);
								
							}else{
								generateDayWCandJTallowReport1(data,classNameMap,delayWorkMap,
										overtimeMap,vacationMap,optUser,addList);
							}
						}else{
							generateDayWCandJTallowReport2(data,delayWorkMap,
									overtimeMap,vacationMap,optUser,addList);
						}
					}
					
					//保存报表数据
					if(addList!=null&&addList.size()>0){
						//删除历史数据
						AttnReportTbl oldAttnReport = attnReportMapper.getDinnerAndTrafficAllowanceDay(employee.getId(),lastMonthStart, lastMonthEnd);
						if(oldAttnReport!=null){
							attnReportMapper.deleteById(oldAttnReport.getId(),optUser);
							dinnerAndTrafficAllowanceDayMapper.deleteByReportId(oldAttnReport.getId(),optUser);
						}
						//生成新的报表数据
						attnReportMapper.save(attnReport);
						for(DinnerAndTrafficAllowanceDayTbl data:addList){
							data.setReportId(attnReport.getId());
						}
						dinnerAndTrafficAllowanceDayMapper.batchSave(addList);
					}
					
				}else{
					logger.info("未识别员工："+employee.getCnName()+";的工时类型");
				}
			}
		}else{
			logger.info("未识别员工："+employee.getCnName()+";的排班类型");
		}
		
	}
	
	/**
	 * 生成非9-18点标准工时报表数据
	 * @param data
	 * @param classNameMap
	 * @param delayWorkMap
	 * @param overtimeMap
	 * @param vacationMap
	 * @param optUser
	 * @param addList
	 */
	public void generateDayWCandJTallowReport1(AttnStatistics data,Map<Date,EmployeeClass> classNameMap,Map<Date,Double> delayWorkMap,
			Map<Date,Double> overtimeMap,Map<Date,Integer> vacationMap,String optUser,List<DinnerAndTrafficAllowanceDayTbl> addList){
		Date attnDate9Time = DateUtils.parse(DateUtils.format(data.getAttnDate(), DateUtils.FORMAT_SHORT)
				+ TIME_21, DateUtils.FORMAT_LONG);
		Date attnDate23Time = DateUtils.parse(DateUtils.format(data.getAttnDate(), DateUtils.FORMAT_SHORT)
				+ TIME_23, DateUtils.FORMAT_LONG);
		
		DinnerAndTrafficAllowanceDayTbl reportData = new DinnerAndTrafficAllowanceDayTbl();
		reportData.setAttnDate(data.getAttnDate());
		reportData.setDinnerAllowance(0);//餐补
		reportData.setTraffiAllowance("0");//交通补贴
	    if(classNameMap!=null&&classNameMap.containsKey(data.getAttnDate())){
			//排班人员只要排了班次就是工作日---注意--法定节假日可能会排班
	    	reportData.setAttnClass(classNameMap.get(data.getAttnDate()).getName()+"班");//班次
	    	if(delayWorkMap!=null&&delayWorkMap.containsKey(data.getAttnDate())){
				if(delayWorkMap.get(data.getAttnDate())>=2){
					reportData.setDinnerAllowance(15);//餐补
				}
			}
	    	if(overtimeMap!=null&&overtimeMap.containsKey(data.getAttnDate())){
				if(overtimeMap.get(data.getAttnDate())>=8){
					reportData.setDinnerAllowance(15);//餐补
				}
			}
	    	//交通补贴
	    	//班次结束时间
	    	Date classEndTime =  DateUtils.parse(DateUtils.format(data.getAttnDate(), DateUtils.FORMAT_SHORT)
					+" "+DateUtils.format(classNameMap.get(data.getAttnDate()).getEndTime(), DateUtils.FORMAT_HH_MM_SS), DateUtils.FORMAT_LONG);
	    	//跨日班次
	    	if(classNameMap.get(data.getAttnDate()).getIsInterDay().intValue()==1){
	    		classEndTime = DateUtils.addDay(classEndTime, 1);
	    	}
	    	if(classEndTime.getTime()<attnDate9Time.getTime()){
	    		//21点之前的班次,延时工作做登记必须大于1
	    		if(delayWorkMap!=null&&delayWorkMap.containsKey(data.getAttnDate())&&delayWorkMap.get(data.getAttnDate())>=1){
	    			if(data.getEndWorkTime()!=null&&data.getEndWorkTime().getTime()>=attnDate9Time.getTime()
							&&data.getEndWorkTime().getTime()<attnDate23Time.getTime()){
						reportData.setTraffiAllowance(TRAFFIC_ALLOWANCE_45);//交通补贴
					}else if(data.getEndWorkTime()!=null&&data.getEndWorkTime().getTime()>=attnDate23Time.getTime()){
						reportData.setTraffiAllowance(TRAFFIC_ALLOWANCE_SBSX);//交通补贴
					}
	    		}
	    		//法定处理
				if(overtimeMap!=null&&overtimeMap.containsKey(data.getAttnDate())){
					if(overtimeMap.get(data.getAttnDate())>=1){
						if(data.getEndWorkTime()!=null&&data.getEndWorkTime().getTime()>=attnDate9Time.getTime()
								&&data.getEndWorkTime().getTime()<attnDate23Time.getTime()){
							reportData.setTraffiAllowance(TRAFFIC_ALLOWANCE_45);//交通补贴
						}else if(data.getEndWorkTime()!=null&&data.getEndWorkTime().getTime()>=attnDate23Time.getTime()){
							reportData.setTraffiAllowance(TRAFFIC_ALLOWANCE_SBSX);//交通补贴
						}
					}
				}
	    	}else if(classEndTime.getTime()>=attnDate9Time.getTime()&&classEndTime.getTime()<attnDate23Time.getTime()){
	    		//21点-23点间的班次
	    		if(data.getEndWorkTime()!=null&&data.getEndWorkTime().getTime()>=attnDate9Time.getTime()
						&&data.getEndWorkTime().getTime()<attnDate23Time.getTime()){
					reportData.setTraffiAllowance(TRAFFIC_ALLOWANCE_45);//交通补贴
				}else if(data.getEndWorkTime()!=null&&data.getEndWorkTime().getTime()>=attnDate23Time.getTime()){
					//看延时工作
					reportData.setTraffiAllowance(TRAFFIC_ALLOWANCE_45);//交通补贴
					if(delayWorkMap!=null&&delayWorkMap.containsKey(data.getAttnDate())&&delayWorkMap.get(data.getAttnDate())>=1){
						reportData.setTraffiAllowance(TRAFFIC_ALLOWANCE_SBSX);//交通补贴
					}
					if(overtimeMap!=null&&overtimeMap.containsKey(data.getAttnDate())){
						if(overtimeMap.get(data.getAttnDate())>=1){
							reportData.setTraffiAllowance(TRAFFIC_ALLOWANCE_SBSX);//交通补贴
						}
					}
				}
	    	}else if(classEndTime.getTime()>=attnDate23Time.getTime()){
	    		//23点之后的班次
	    		if(data.getEndWorkTime()!=null&&data.getEndWorkTime().getTime()>=attnDate9Time.getTime()
						&&data.getEndWorkTime().getTime()<attnDate23Time.getTime()){
					reportData.setTraffiAllowance(TRAFFIC_ALLOWANCE_45);//交通补贴
				}else if(data.getEndWorkTime()!=null&&data.getEndWorkTime().getTime()>=attnDate23Time.getTime()){
					reportData.setTraffiAllowance(TRAFFIC_ALLOWANCE_SBSX);//交通补贴
				}
	    	}
		}else{
			//未排班---计非工作日
			if(delayWorkMap!=null&&delayWorkMap.containsKey(data.getAttnDate())){
				if(delayWorkMap.get(data.getAttnDate())>=8){
					reportData.setDinnerAllowance(15);//餐补
				}
				if(delayWorkMap.get(data.getAttnDate())>=1){
					if(data.getEndWorkTime()!=null&&data.getEndWorkTime().getTime()>=attnDate9Time.getTime()
							&&data.getEndWorkTime().getTime()<attnDate23Time.getTime()){
						reportData.setTraffiAllowance(TRAFFIC_ALLOWANCE_45);//交通补贴
					}else if(data.getEndWorkTime()!=null&&data.getEndWorkTime().getTime()>=attnDate23Time.getTime()){
						reportData.setTraffiAllowance(TRAFFIC_ALLOWANCE_SBSX);//交通补贴
					}
				}
			}
		}
		reportData.setCreateUser(optUser);
		reportData.setCreateTime(new Date());
		reportData.setDelFlag(0);
		reportData.setOffDutyTime(data.getEndWorkTime());//下班时间
		reportData.setWeek("星期"+DateUtils.getWeek(data.getAttnDate()));//星期几
		Double overTimeHours  = 0d;
		if(vacationMap!=null&&vacationMap.containsKey(data.getAttnDate())){
			//法定取延时工作申请里的数据
			if(vacationMap.get(data.getAttnDate()).intValue()==3){
				overTimeHours = (overtimeMap!=null&&overtimeMap.containsKey(data.getAttnDate()))
						?overtimeMap.get(data.getAttnDate()):0d;
			}
		}else{
			//非法定取延时工作登记里的数据
			overTimeHours = (delayWorkMap!=null&&delayWorkMap.containsKey(data.getAttnDate()))
					?delayWorkMap.get(data.getAttnDate()):0d;
		}
		reportData.setOverTimeHours(overTimeHours);
		addList.add(reportData);
	}
	
	public void generateDayWCandJTallowReport2(AttnStatistics data,Map<Date,Double> delayWorkMap,
			Map<Date,Double> overtimeMap,Map<Date,Integer> vacationMap,String optUser,List<DinnerAndTrafficAllowanceDayTbl> addList){
		DinnerAndTrafficAllowanceDayTbl reportData = new DinnerAndTrafficAllowanceDayTbl();
		reportData.setAttnDate(data.getAttnDate());
		reportData.setDinnerAllowance(0);//餐补
		reportData.setTraffiAllowance("0");//交通补贴
		if(annualVacationService.judgeWorkOrNot(data.getAttnDate(), vacationMap)){
			reportData.setAttnClass(CLASS_NORMAL);
			if(delayWorkMap!=null&&delayWorkMap.containsKey(data.getAttnDate())){
				if(delayWorkMap.get(data.getAttnDate())>=2){
					reportData.setDinnerAllowance(15);//餐补
					reportData.setTraffiAllowance(TRAFFIC_ALLOWANCE_45);//交通补贴
				}
				if(delayWorkMap.get(data.getAttnDate())>=4){
					reportData.setTraffiAllowance(TRAFFIC_ALLOWANCE_SBSX);//交通补贴
				}
			}
		}else{
			reportData.setAttnClass(CLASS_REST);
			Date attnDate9Time = DateUtils.parse(DateUtils.format(data.getAttnDate(), DateUtils.FORMAT_SHORT)
			+ TIME_21, DateUtils.FORMAT_LONG);
			Date attnDate23Time = DateUtils.parse(DateUtils.format(data.getAttnDate(), DateUtils.FORMAT_SHORT)
					+ TIME_23, DateUtils.FORMAT_LONG);
			//正常休息日
			if(delayWorkMap!=null&&delayWorkMap.containsKey(data.getAttnDate())){
				if(delayWorkMap.get(data.getAttnDate())>=1){
					if(data.getEndWorkTime()!=null&&data.getEndWorkTime().getTime()>=attnDate9Time.getTime()
							&&data.getEndWorkTime().getTime()<attnDate23Time.getTime()){
						reportData.setTraffiAllowance(TRAFFIC_ALLOWANCE_45);//交通补贴
					}else if(data.getEndWorkTime()!=null&&data.getEndWorkTime().getTime()>=attnDate23Time.getTime()){
						reportData.setTraffiAllowance(TRAFFIC_ALLOWANCE_SBSX);//交通补贴
					}
				}
				if(delayWorkMap.get(data.getAttnDate())>=8){
					reportData.setDinnerAllowance(15);//餐补
				}
			}
			//法定节假日
			if(overtimeMap!=null&&overtimeMap.containsKey(data.getAttnDate())){
				if(overtimeMap.get(data.getAttnDate())>=1){
					if(data.getEndWorkTime()!=null&&data.getEndWorkTime().getTime()>=attnDate9Time.getTime()
							&&data.getEndWorkTime().getTime()<attnDate23Time.getTime()){
						reportData.setTraffiAllowance(TRAFFIC_ALLOWANCE_45);//交通补贴
					}else if(data.getEndWorkTime()!=null&&data.getEndWorkTime().getTime()>=attnDate23Time.getTime()){
						reportData.setTraffiAllowance(TRAFFIC_ALLOWANCE_SBSX);//交通补贴
					}
				}
				if(overtimeMap.get(data.getAttnDate())>=8){
					reportData.setDinnerAllowance(15);//餐补
				}
			}
		}
		reportData.setCreateUser(optUser);
		reportData.setCreateTime(new Date());
		reportData.setDelFlag(0);
		reportData.setOffDutyTime(data.getEndWorkTime());//下班时间
		reportData.setWeek("星期"+DateUtils.getWeek(data.getAttnDate()));//星期几
		Double overTimeHours  = 0d;
		if(vacationMap!=null&&vacationMap.containsKey(data.getAttnDate())){
			//法定取延时工作申请里的数据
			if(vacationMap.get(data.getAttnDate()).intValue()==3){
				overTimeHours = (overtimeMap!=null&&overtimeMap.containsKey(data.getAttnDate()))
						?overtimeMap.get(data.getAttnDate()):0d;
			}
		}else{
			//非法定取延时工作登记里的数据
			overTimeHours = (delayWorkMap!=null&&delayWorkMap.containsKey(data.getAttnDate()))
					?delayWorkMap.get(data.getAttnDate()):0d;
		}
		reportData.setOverTimeHours(overTimeHours);
		addList.add(reportData);
	}
	
	

	@Override
	public List<Map<String, Object>> getDinnerAndTrafficAllowanceMonthDetail(
			Date startDate, Date endDate, Long departId) {
		List<Map<String, Object>> list = attnReportMapper.getDinnerAndTrafficAllowanceMonthDetail(startDate, endDate, departId);
		for(Map<String, Object> data:list){
			String workType = data.get("workType")!=null?String.valueOf(data.get("workType")):"";
			if("comprehensive".equals(workType)){
				data.put("workType", "综合工时");
			}else if("standard".equals(workType)){
				data.put("workType", "标准工时");
			}else{
				data.put("workType", " ");
			}
		}
		return list;
	}

	@Override
	public List<Map<String, Object>> getDinnerAndTrafficAllowanceMonthSummary(
			Date startDate, Date endDate, Long departId) {
		
		//统计餐费次数
		List<Map<String, Object>> dinnerAllowanceList = attnReportMapper.
				getDinnerAllowanceMonthSummary(startDate, endDate, departId);
		
		//统计交通补贴45的次数 TRAFFIC_ALLOWANCE_45	
		List<TrafficAllowanceMonthSummaryDTO> trafficAllowance45List = attnReportMapper.
				getTrafficAllowanceMonthSummary(startDate, endDate, departId, TRAFFIC_ALLOWANCE_45);
		Map<String,TrafficAllowanceMonthSummaryDTO> trafficAllowance45Map = trafficAllowance45List.stream().
				collect(Collectors.toMap(TrafficAllowanceMonthSummaryDTO :: getCodeAndMonth,a->a,(k1,k2)->k1));
		
		//统计交通补贴实报实销的次数	 TRAFFIC_ALLOWANCE_SBSX
		List<TrafficAllowanceMonthSummaryDTO> trafficAllowanceSBSXList = attnReportMapper.
				getTrafficAllowanceMonthSummary(startDate, endDate, departId, TRAFFIC_ALLOWANCE_SBSX);
		Map<String,TrafficAllowanceMonthSummaryDTO> trafficAllowanceSBSXMap = trafficAllowanceSBSXList.stream().
				collect(Collectors.toMap(TrafficAllowanceMonthSummaryDTO :: getCodeAndMonth,a->a,(k1,k2)->k1));
		
		//组装交通补贴
		for(Map<String, Object> data:dinnerAllowanceList){
			data.put("traffic45", "0");
			data.put("trafficSBSX", "0");
			String key = (data.get("employeeCode")!=null?String.valueOf(data.get("employeeCode")):" ")
				+ (data.get("month")!=null?String.valueOf(data.get("month")):" ");
			if(trafficAllowance45Map!=null&&trafficAllowance45Map.containsKey(key)){
				data.put("traffic45", trafficAllowance45Map.get(key).getTrafficCount());
			}
			if(trafficAllowanceSBSXMap!=null&&trafficAllowanceSBSXMap.containsKey(key)){
				data.put("trafficSBSX", trafficAllowanceSBSXMap.get(key).getTrafficCount());
			}
			String workType = data.get("workType")!=null?String.valueOf(data.get("workType")):"";
			if("comprehensive".equals(workType)){
				data.put("workType", "综合工时");
			}else if("standard".equals(workType)){
				data.put("workType", "标准工时");
			}else{
				data.put("workType", " ");
			}
			Integer dinnerCount = data.get("dinnerCount")!=null?Integer.valueOf(String.valueOf(data.get("dinnerCount"))):0;
			data.put("dinnerCount", dinnerCount/15);
		}
		
		return dinnerAllowanceList;
	}

}
