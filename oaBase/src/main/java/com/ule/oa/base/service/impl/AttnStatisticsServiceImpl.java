package com.ule.oa.base.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.ule.oa.base.mapper.AttnStatisticsMapper;
import com.ule.oa.base.mapper.AttnWorkHoursMapper;
import com.ule.oa.base.mapper.DelayWorkRegisterMapper;
import com.ule.oa.base.mapper.EmpDelayHoursMapper;
import com.ule.oa.base.mapper.EmployeeBaseInfoMapper;
import com.ule.oa.base.mapper.EmployeeClassMapper;
import com.ule.oa.base.mapper.RemoveSubordinateAbsenceMapper;
import com.ule.oa.base.po.AttnStatistics;
import com.ule.oa.base.po.AttnTaskRecord;
import com.ule.oa.base.po.AttnWorkHours;
import com.ule.oa.base.po.Company;
import com.ule.oa.base.po.CompanyConfig;
import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.DelayWorkRegister;
import com.ule.oa.base.po.EmpMsg;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeBaseInfoDTO;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.po.RemoveSubordinateAbsence;
import com.ule.oa.base.po.RunTask;
import com.ule.oa.base.service.AnnualVacationService;
import com.ule.oa.base.service.AttnReportService;
import com.ule.oa.base.service.AttnSignRecordService;
import com.ule.oa.base.service.AttnStatisticsService;
import com.ule.oa.base.service.AttnTaskRecordService;
import com.ule.oa.base.service.AttnWorkHoursService;
import com.ule.oa.base.service.CompanyConfigService;
import com.ule.oa.base.service.CompanyService;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmpApplicationOvertimeService;
import com.ule.oa.base.service.EmpLeaveReportService;
import com.ule.oa.base.service.EmpMsgService;
import com.ule.oa.base.service.EmployeeClassService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.util.SendMailUtil;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.po.CommonPo;
import com.ule.oa.common.spring.SpringContextUtils;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.ExcelUtil;

@Service
public class AttnStatisticsServiceImpl implements AttnStatisticsService{
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private EmployeeService employeeService;
	
	@Resource
	private AttnStatisticsMapper attnStatisticsMapper;
	
	@Resource
	private AttnWorkHoursService attnWorkHoursService;
	@Resource
	private AttnWorkHoursMapper attnWorkHoursMapper;
	
	@Resource
	private EmployeeClassService employeeClassService;
	@Autowired
	private EmployeeClassMapper employeeClassMapper;
	
	@Resource
	private CompanyConfigService companyConfigService;
	
	@Resource
	private UserService userService;
	
	@Resource
	private ConfigService configService;
	
	@Resource
	private AnnualVacationService annualVacationService;
	
	@Resource
	private DepartService departService;
	
	@Resource
	private CompanyService companyService;
	
	@Resource
	private AttnTaskRecordService attnTaskRecordService;
	
	@Resource
	private AttnSignRecordService attnSignRecordService;
	
	@Autowired
	private EmpApplicationOvertimeService empApplicationOvertimeService;
	
	@Autowired
	private EmpLeaveReportService empLeaveReportService;
	
	@Autowired
	private AttnReportService attnReportService;
	
	@Autowired
	private EmpMsgService empMsgService;
	@Autowired
	private EmpDelayHoursMapper empDelayHoursMapper;
	
	@Autowired
	private RemoveSubordinateAbsenceMapper removeSubordinateAbsenceMapper;
	@Autowired
	private EmployeeBaseInfoMapper employeeBaseInfoMapper;
	@Autowired
	private DelayWorkRegisterMapper delayWorkRegisterMapper;
	
	private Date CURRENT_TIME = null;
	private final Integer COMPLETE_STATUS = 0;//已完成，或者状态正常
	private final Integer NOT_COMPLETE_STATUS = 1;//未完成，或者状态异常
    private final String SIX = " 06:00:00";//6点整
    private final String COMPREHENSIVE="comprehensive";//综合工时
    //private final String STANDARD = "standard";//标准工时
    private final String SCHEDULING_YES="yes";//排班
    //private final String SCHEDULING_NO="no";//不排班
    private final String NIGHT=" 09:00:00";//9点
    private final String EIGHTEEN=" 18:00:00";//18点
    private final String CREATE_USER = "oaService";//考勤数据创建人：api
    private final Double FULL_WORK_HOURS = 8.0;//正常工时：8小时
    private final Integer PAGE_SIZE = 200;//分页处理每页数据条数

    private final Integer LATE_MINUTES = 5;//合理迟到时间，单位分钟
    
	@Resource(name="threadPoolTaskExecutor")
	private ThreadPoolTaskExecutor pool;
    
    private class SendExeclMail implements Runnable {
		
		private HSSFWorkbook hSSFWorkbook;
		
		private String toMail;
		
		private String subject;
		
		private String sttachmentName;
		
		SendExeclMail(HSSFWorkbook hSSFWorkbook,String toMail,String subject,String sttachmentName) {
			this.hSSFWorkbook = hSSFWorkbook;
			this.toMail = toMail;
			this.subject = subject;
			this.sttachmentName = sttachmentName;
		}
		
		public void run() {
			sendExeclMail(hSSFWorkbook,toMail,subject,sttachmentName);
		}
	}
    
    public void sendExeclMail(HSSFWorkbook hSSFWorkbook, String to,String subject,String sttachmentName) {
    	try {
			SendMailUtil.sendExcelMail(hSSFWorkbook,to,subject,sttachmentName);
		} catch (Exception e) {
			logger.error("发送execl邮件失败",e);
		}
    }

	@Override
	public void setAttStatistics(AttnStatistics condition) {
		
		final Date kafkaStartTime = condition.getStartTime();
		final Date kafkaEndTime = condition.getEndTime();
		
		// 一.判断排班
		// 二.判断节假日
		Long employeeId = null;//员工id
		String employeeName = null;//员工名字
		Long attnTaskId = null;//考勤数据主键，用来做更新
		
		//查询邮乐公司信息
		Company uleCompanyCondition  = new Company();
		uleCompanyCondition.setIsUle(1);//邮乐
		Company uleCompany = companyService.getByCondition(uleCompanyCondition);
		final Long uleId = uleCompany.getId();
		
        //1.分页面查询出所有状态未完成员工,从任务表获取
		AttnTaskRecord attnTaskRecordCondition = new AttnTaskRecord();
		//----------------------------------------
		//attnTaskRecordCondition.setEmployId(1716L);//需删除
		attnTaskRecordCondition.setSetAttnStatisticsStatus(NOT_COMPLETE_STATUS);//查询所有未完成的
		attnTaskRecordCondition.setCompanyId(uleId);
    	attnTaskRecordCondition.setOffset(0);
		attnTaskRecordCondition.setLimit(PAGE_SIZE);
		attnTaskRecordCondition.setStartTime(kafkaStartTime);//计算范围就是传过来的范围
		attnTaskRecordCondition.setEndTime(kafkaEndTime);//计算范围就是传过来的范围
		List<Employee> taskEmpList = new ArrayList<Employee>();//查询未完成考勤明细统计的员工
		
		//存放需要更新状态的结果
		AttnTaskRecord attnTaskRecordResult = null;
		
		//优化，存放节假日map
		Map<Date, Boolean> workDayMap = new HashMap<Date, Boolean>();
		
        //2.参数准备
		String workTypeDisplayCode,schedulingDisplayCode;//工时类型displayCode,是否排班displayCode
		Map<Long,EmployeeClass> employeeClassMap = null;
		EmployeeClass employeeClass = null;//排班实体
		
		/*Map<Long,AttnStatistics> empCardAttnStatisticsMap = null;
		AttnStatistics empCardAttnStatistics = null;*///打卡出勤工时实体
		Map<Long,List<AttnStatistics>> empAttnStatisticsListMap = null;
		List<AttnStatistics> empAttnStatisticsList = null;//表单出勤工时实体
		AttnStatistics resultAttnStatistics = null;

		final AttnWorkHours hoursCondition = new AttnWorkHours();//查询考勤记录条件，表attn_sign_record
		final EmployeeClass classCondition = new EmployeeClass();//查询班次条件
		
		String countDateStr,startDateStr,endDateStr ;//转换日期用的字符串
		Date empAttnDate = null,startTime ,endTime;//条件考勤日期,开始，结束时间
		
		Boolean isWorkDay = true;//是否不上班true:工作;false:休息。
        int i=0;
		try{
	        while (i==0 || !taskEmpList.isEmpty()) {//list能查到数据，继续往后查,直到查不到
	        	taskEmpList = attnTaskRecordService.selectEmpByAttnTask(attnTaskRecordCondition); 
	        	
	        	if(null !=taskEmpList && !taskEmpList.isEmpty()){
	        		
	        		List<Long> employeesIds = new ArrayList<Long>();
	        		for(Employee employee:taskEmpList){//这个循环是为了批量查询打卡记录，大量减少数据库交互
	        			employeeId = employee.getId();
	        			employeesIds.add(employeeId);//每次这里变了之后，相应的attnWorkHoursMap，employeeClassMap都得变
	        		}
	        		Boolean reSelect = true;//重新查询工时，排班标记
	    			hoursCondition.setEmployeeIds(employeesIds);
	    			classCondition.setEmployeeIds(employeesIds);
	        		
		        	for(Employee employee:taskEmpList){
		        		
		    			employeeId = employee.getId();
		    			
		    			employeeName = employee.getCnName();
		    			attnTaskId = employee.getAttnTaskId();
		    			workTypeDisplayCode = employee.getWorkTypeName();
		    			schedulingDisplayCode = employee.getWhetherSchedulingName();
		        		
		        		if(null==empAttnDate || !empAttnDate.equals(employee.getAttnDate())){//考勤日期发生变化，参数重新计算
			        		empAttnDate = employee.getAttnDate();
			        		countDateStr = DateUtils.format(empAttnDate, DateUtils.FORMAT_SHORT)+SIX;
			    	        startTime = DateUtils.parse(countDateStr, DateUtils.FORMAT_LONG); //开始时间，统计日6点
			    	        endTime =  DateUtils.addDay(startTime, 1);//结束时间，统计日加一天6点
			    	        
			    			hoursCondition.setWorkDate(empAttnDate);
			    			//hoursCondition.setDataType(0);//考勤数据

		    				classCondition.setCompanyId(uleId);//只查询邮乐员工
		    				classCondition.setClassDate(empAttnDate);
		        		}else{
		        			
		        		}
		    	        
		        		if(reSelect){
		        			reSelect = false;
			    	        /**3.准备清洗员工打卡数据,返回打卡数据对象map
			    			empCardAttnStatisticsMap = getAttnStatisticsMap(hoursCondition);
			    			3.5 准备清洗员工单据数据,返回单据数据对象map**/
		        			
			    			/**修改点：不再分单据和打卡，拿出所有记录，进行汇总计算,但是sql必须严格按照开始时间结束时间排序**/
			    			empAttnStatisticsListMap = getAttnStatisticsListMap(hoursCondition);
			    			
			    			/**4.准备排班数据**/
							employeeClassMap = employeeClassService.getEmployeeClassMap(classCondition);
		        		}
		        		
		    			if(!workDayMap.containsKey(empAttnDate)){//首次查询这一天，放入map
			    			isWorkDay = annualVacationService.judgeWorkOrNot(empAttnDate);//是否不上班true:工作;false:休息。
			    			workDayMap.put(empAttnDate, isWorkDay);
		    			}else{                                   //不是首次，直接获取
		    				isWorkDay = workDayMap.get(empAttnDate);
		    			}
		    			
		    	        
		    			/**3.找到昨天已经汇总的打卡数据,每个员工唯一*
		    			empCardAttnStatistics = empCardAttnStatisticsMap.get(employeeId);*/
		    			/**4.找到汇总的,员工单据数据;由于多个单据时间可能和打卡时间重叠，断层，所以不能再叠加计算**/
		    			
		    			/**根据员工id得到所有的数据**/
		    			empAttnStatisticsList = empAttnStatisticsListMap.get(employeeId);
		    			
		    			/**考勤对象公用部分**/
	    				resultAttnStatistics = new AttnStatistics();
	    				resultAttnStatistics.setEmployId(employeeId);
	    				resultAttnStatistics.setCompanyId(employee.getCompanyId());
	    				resultAttnStatistics.setAttnDate(empAttnDate);//统计的考勤时间
	    				resultAttnStatistics.setDelFlag(COMPLETE_STATUS);
		    			
		    			//5.根据不同工时类型，进行考勤出勤汇总
		    			if(null == workTypeDisplayCode){
		    				resultAttnStatistics = null;
    						logger.error("员工 id: {},姓名:{} 没有定义工时类型，不做计算。",employeeId,employeeName);
		    			}
		    			else if(COMPREHENSIVE.equals(workTypeDisplayCode)){//综合工时，没有排班这一说,弹性制，没迟到早退
		    				
		    				if(!isWorkDay){//如果是假期，如果有打卡，或者单据，则记录异常考勤
		    					if(null != empAttnStatisticsList){
		    						resultAttnStatistics = buildComprehensive(resultAttnStatistics,empAttnStatisticsList,null);
		    						resultAttnStatistics.setAttnStatus(NOT_COMPLETE_STATUS);//异常
		    						resultAttnStatistics.setMustAttnTime(0.0);//应出勤0小时
		    						resultAttnStatistics.setLackAttnTime(0.0);//缺勤0小时
		    						resultAttnStatistics.setAllAttnTime(0.0);//缺勤0小时
		    						resultAttnStatistics.setActAttnTime(0.0);//实际出勤设为0
		    					}else{
		    						/*假期无数据不做操作*/
		    						resultAttnStatistics = null;
		    					}
		    				}else{
		    					    employeeClass = employeeClassMap.get(employeeId);
		    					    //如果是工作日,正常记录打卡，如果无打卡记录，则记录异常
		    						resultAttnStatistics = buildComprehensive(resultAttnStatistics,empAttnStatisticsList,employeeClass);
		    				}
		    				
		    			}else{//标准工时，判断是否排班,是：根据排班进行计算,没有节假日；否：默认排班，且有节假日。
							employeeClass = employeeClassMap.get(employeeId);
							if(null == schedulingDisplayCode){
								resultAttnStatistics = null;
	    						logger.error("员工 id: {},姓名:{} 没有定义是否排班，不做计算。",employeeId,employeeName);
							}
							else if(schedulingDisplayCode.equals(SCHEDULING_YES)){//如果需要排班,则无节假日
		    					
		    					if(null == employeeClass){//找不到排班数据....记录打卡时间，但是全部异常，没打卡数据不作处理
		    						logger.error("员工 id: {},姓名:{} 没有定义排班数据",employeeId,employeeName);
		    						
		    						//重算考勤，需要更改，（可能以前旷工的那天改成了休息，状态需要改成正常，- -）
		    						resultAttnStatistics = readyStandardFreeDay(resultAttnStatistics,empAttnStatisticsList);
		    						/**调班修改，未排班的那天不记录，置为逻辑删除**/
		    						//resultAttnStatistics.setDelFlag(NOT_COMPLETE_STATUS);
		    						
		    					}else{//有排班数据，正常记录，没打卡数据，记异常
		    						
	    							startTime = employeeClass.getStartTime();
	    						
	    							endTime = employeeClass.getEndTime();
		    						
	    							empAttnDate = employeeClass.getClassDate();
		    						startTime = concatDateTime(empAttnDate, startTime);
		    						endTime = concatDateTime(empAttnDate, endTime);
		    						
		    						if(1 == employeeClass.getIsInterDay()){//跨天+1
		    							endTime = DateUtils.addDay(endTime, 1);
		    						}
		    						
		    						//调用公共的标准工时封装方法
		    						resultAttnStatistics = readyStandardWorkDay(startTime,endTime,resultAttnStatistics,empAttnStatisticsList);
		    					}
		    				}else{//不需要排班,赋予默认排班时间,设置默认出勤时间9:00 -- 18：00,有节假日
		    					
		    					if(!isWorkDay){//如果今天放假，但是有考勤或者单据数据
		    						
		    						resultAttnStatistics = readyStandardFreeDay(resultAttnStatistics,empAttnStatisticsList);
		    						
		    					}else{
		    						
		    						countDateStr = DateUtils.format(empAttnDate, DateUtils.FORMAT_SHORT);
		    						if(employeeClass!=null){
		    							startDateStr = countDateStr+" "+DateUtils.format(employeeClass.getStartTime(), DateUtils.FORMAT_HH_MM_SS);
			    						endDateStr = countDateStr+" "+DateUtils.format(employeeClass.getEndTime(), DateUtils.FORMAT_HH_MM_SS);
		    						}else{
		    							startDateStr = countDateStr+NIGHT;
			    						endDateStr = countDateStr+EIGHTEEN;
		    						}
		    						
		    						startTime = DateUtils.parse(startDateStr, DateUtils.FORMAT_LONG);
		    						endTime = DateUtils.parse(endDateStr, DateUtils.FORMAT_LONG);
		    						
		    						//调用公共的标准工时封装方法
		    						resultAttnStatistics = readyStandardWorkDay(startTime,endTime,resultAttnStatistics,empAttnStatisticsList);
		    						
		    					}
		    				}
		    				
		    			}
		    			if(null != resultAttnStatistics){
			    			  Long existId = null;
			    			  existId = attnStatisticsMapper.getExistIdStatistics(resultAttnStatistics);
			    			  AttnStatisticsService attnStatisticsService = SpringContextUtils.getContext().getBean(AttnStatisticsService.class);
			    			 
			    			  if(null == existId){
			    				  if(resultAttnStatistics.getDelFlag().intValue() != NOT_COMPLETE_STATUS){
				    				  attnStatisticsService.save(resultAttnStatistics); 
			    				  }
			    			  }else{
			    				  resultAttnStatistics.setId(existId);
			    				  attnStatisticsService.updateById(resultAttnStatistics);
			    			  }
		    			}
		    			CURRENT_TIME = DateUtils.parse(DateUtils.getNow(),DateUtils.FORMAT_LONG);
		    			attnTaskRecordResult = new AttnTaskRecord();
		    			attnTaskRecordResult.setSetAttnStatisticsStatus(COMPLETE_STATUS);//状态设置为完成
		    			attnTaskRecordResult.setId(attnTaskId);
	    			    attnTaskRecordResult.setUpdateTime(CURRENT_TIME);
	    			    attnTaskRecordService.updateById(attnTaskRecordResult);//更新任务状态表
		        	}
	        	}
				System.gc();//垃圾回收
	        	i++;
	        }
		}catch (Exception e){
			logger.error("考勤结果统计出错:{}",e.getMessage());
		}
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRES_NEW)
	public Integer saveBatch(List<AttnStatistics> attnStatisticsList) {
		return attnStatisticsMapper.saveBatch(attnStatisticsList);
	}
	
	@Override
	public Integer save(AttnStatistics attnStatistics) {
		return attnStatisticsMapper.save(attnStatistics);
	}
	
	@Override
	public Integer updateById(AttnStatistics attnStatistics) {
		return attnStatisticsMapper.updateById(attnStatistics);
	}

	/**得到已经分组好的单据数据，可以有多条，所以key：List<Object>**/
	private Map<Long, List<AttnStatistics>> getAttnStatisticsListMap(AttnWorkHours hoursCondition) {
		List<AttnStatistics> attnStatisticsList = attnStatisticsMapper.getAttnStatisticsList(hoursCondition);
		Map<Long, List<AttnStatistics>> groupBy = attnStatisticsList.stream().collect(Collectors.groupingBy(AttnStatistics::getEmployId));  
		return groupBy;
	}

	/**得到已经分组好的打卡数据，只有一条，所以key：Object
	private Map<Long, AttnStatistics> getAttnStatisticsMap(AttnWorkHours hoursCondition) {
		List<AttnStatistics> attnStatisticsList = attnStatisticsMapper.getAttnStatisticsListByAttnWork(hoursCondition);
		Map<Long, AttnStatistics> attnStatisticsMap = Maps.uniqueIndex(attnStatisticsList, new Function <AttnStatistics,Long> () {  
	          public Long apply(AttnStatistics from) {  
		            return from.getEmployId() == null?-1L:from.getEmployId();   
		    }}); 
		return attnStatisticsMap;
	}
	**/
	

    /**
     * 标准工时非工作日考勤计算（周末或者找不到排班的）
     * @param empCardAttnStatistics
     * @param empAttnStatisticsList
     * @return
     */
	private  AttnStatistics readyStandardFreeDay(AttnStatistics resultAttnStatistics,List<AttnStatistics> empAttnStatisticsList){
		
		
		if(null != empAttnStatisticsList){/**存在考勤数据**/
			Date earliestAttnTime = null;//最早打卡时间
			Date leatestAttnTime = null;//最晚打卡时间
			Date startTime = null,endTime = null;
			String type = null;//数据类型，打卡或者各种单据/**如果有考勤数据（单据或者打卡）**/
			
			/**按顺序逐个计算出勤时间**/
			Integer i = 0;/**循环次数标记**/
			Integer size = 0;/**list的size**/
			Boolean addToList = false;/**默认不把得到的map加进list**/
			List<Map<String,Object>> dateList = new ArrayList<>();/**存放得到的日期范围list**/
			for(AttnStatistics attn:empAttnStatisticsList){
				
				size = dateList.size();
				
				/**数据类型,合并的时候保留类型为打卡数据的**/
				type = attn.getType();
				
				/**绝对修改**/
				if("3".equals(type)){
					earliestAttnTime = attn.getStartWorkTime();
					leatestAttnTime = attn.getEndWorkTime();
					if(earliestAttnTime!=null&&leatestAttnTime!=null){
						Map<String,Object> map = new HashMap<>();
						map.put("startTime", earliestAttnTime);
						map.put("endTime", leatestAttnTime);
						map.put("type", type);
						dateList.add(map);
					}
					break;
				}
				
				if(attn.getStartWorkTime() == null || attn.getEndWorkTime() == null){
					/**只有单个时间，不做操作，因为没有意义**/
					if(empAttnStatisticsList.size() == 1){
						earliestAttnTime = attn.getStartWorkTime();
						leatestAttnTime = attn.getEndWorkTime();
					}
				}else{
					Map<String,Object> map = new HashMap<>();
					//allAttnTime = allAttnTime + attn.getAllAttnTime();/**工时**/
					
					if(startTime == null && endTime == null){/**首次进入,i=0**/
						addToList = true;
						startTime = attn.getStartWorkTime();/**数据开始时间**/
						endTime = attn.getEndWorkTime();/**数据结束时间**/
					}else{
						
						if(attn.getStartWorkTime().after(endTime) ){/**如果后面的开始时间大于前面的结束时间**/
						    /**产生一个新的开始时间和结束时间**/
							addToList = true;
							startTime = attn.getStartWorkTime();
							endTime = attn.getEndWorkTime();
						}else{
							/**如果后面的开始时间大于等于前面的结束时间**/
							/**startTime不变**/
							/**优先保留dataType=0的**/
							addToList = false;
							if(attn.getEndWorkTime().after(endTime)){/**结束时间在上个单据结束时间之后**/
								/**更新endTIme**/
								map = dateList.get(size-1);/**得到上一个map**/
								endTime = attn.getEndWorkTime();
								
								if(!type.equals(map.get("type"))){
									if(map.get("type").equals("0")){/**打卡数据**/
										type = (String) map.get("type");
									}else{
									   
									}
								}else{
									
								}
							}else{
								/**endTIme不变**/
							}
						}
					}
					i++;
					
					if(earliestAttnTime == null) {
						earliestAttnTime = startTime;/**考勤开始时间赋值最早的那个**/
					}
					leatestAttnTime = endTime;/**考勤结束时间赋值最晚的那个**/
					
					map.put("startTime", startTime);
					map.put("endTime", endTime);
					map.put("type", type);
					if(addToList){
						dateList.add(map);
					}else{
						
					}
				}
			}

		    Double allAttnTime = 0.0;      //共出勤时间(小时),非工作日不计算出勤时间！！！！！
			resultAttnStatistics.setMustAttnTime(0.0);
			resultAttnStatistics = buildStandard(resultAttnStatistics);
			resultAttnStatistics.setAllAttnTime(((int)((allAttnTime*60)/30))*0.5);//无排班，出勤时间为0.0
			resultAttnStatistics.setActAttnTime(((int)((allAttnTime*60)/30))*0.5);//无排班，出勤时间为0.0
			resultAttnStatistics.setLackAttnTime(0.0);//无排班，缺勤0小时
			resultAttnStatistics.setAbsenteeismTime(0);//无排班，缺勤0小时
			resultAttnStatistics.setComeLateTime(0);//无排班，迟到0小时
			resultAttnStatistics.setLeftEarlyTime(0);//无排班，早退0小时
			resultAttnStatistics.setStartWorkTime(earliestAttnTime);
			resultAttnStatistics.setEndWorkTime(leatestAttnTime);
			resultAttnStatistics.setAttnStatus(NOT_COMPLETE_STATUS);//无排班数据打卡异常
		}else{
			resultAttnStatistics.setDelFlag(NOT_COMPLETE_STATUS);//置为删除
		}
		return resultAttnStatistics;
		
	}
	
	/**
	  * 准备封装标准工时数据
	  * @Title: readyStandard
	  * @param classStartTime 排班开始时间
	  * @param classEndTime 排版结束时间
	  * @param empCardAttnStatistics 员工打卡数据
	  * @param empFormAttnStatistics 员工单据数据
	  * @return    resultAttnStatistics
	  * AttnStatistics    返回类型
	  * @throws
	 */
	private AttnStatistics readyStandardWorkDay(Date classStartTime,Date classEndTime,
			AttnStatistics resultAttnStatistics,List<AttnStatistics> empAttnStatisticsList){
		
		Boolean minusHour = true;
		
		Date earliestAttnTime = null;//最早打卡时间
		Date leatestAttnTime = null;//最晚打卡时间
		Date startTime = null,endTime = null;
		String type = null,leaveType = null;//数据类型，打卡或者各种单据;请假类型
		
		
	    Integer comeLateTime = 0;    //迟到时间(分钟)
	    Integer leftEarlyTime = 0;   //早退时间(分钟)
	    Integer absenteeismTime = 0; //旷工时间(分钟) 
	    Double mustAttnTime = 0.0;     //应出勤时间(小时)
	    Double allAttnTime = 0.0;      //共出勤时间(小时)根据规则计算
	    Double actAttnTime = 0.0;      //共出勤时间(小时)实际
	    Double lackAttnTime = 0.0;     //缺勤工时(小时)
	    Double cancelSubAbsenceTime = 0.0;//消下属考勤
	    int attnStatus = COMPLETE_STATUS;
	    
		mustAttnTime = getIntervalMinutes(classEndTime,classStartTime)*1.0;
		
//		if(mustAttnTime >= 60*12){/**排班达到12h不减2**/
//			minusHour = false;
//		}else{
//			
//		}
		
		/**满5 减1，满10 减2,单位分钟，此方法单位全部为分钟**/
		/*if(minusHour){
			mustAttnTime = getActualTimeMinute(mustAttnTime);
		}else{
			
		}*/

		Boolean hasLeave = false;/**记录是否有指定类型的请假**/
		int usefulDataSize = 0;/**有意义的数据条数，即排除空卡的**/
		Boolean hasCancelSubAbsence = false;//是否有消下属缺勤
		
		for(AttnStatistics attn:empAttnStatisticsList){
			if("140".equals(attn.getType())){
				hasCancelSubAbsence = true;
				break;
			}
		}

		if(null == empAttnStatisticsList){/**如果没有任何考勤数据**/
			
			attnStatus = NOT_COMPLETE_STATUS;
			absenteeismTime = mustAttnTime.intValue();
			
		}else{/**如果有考勤数据（单据或者打卡）**/
			
			/**按顺序逐个计算出勤时间**/
			//Integer i = 0;/**循环次数标记**/
			Integer size = 0;/**list的size**/
			Boolean addToList = false;/**默认不把得到的map加进list**/
			List<Map<String,Object>> dateList = new ArrayList<>();/**存放得到的日期范围list**/
			for(AttnStatistics attn:empAttnStatisticsList){
				
				size = dateList.size();
				
				/**数据类型,合并的时候保留类型为打卡数据的**/
				type = attn.getType();
				/**判断绝对修改，有的话直接取开始结束时间**/
				if("3".equals(type)){
					earliestAttnTime = attn.getStartWorkTime();
					leatestAttnTime = attn.getEndWorkTime();
					if(earliestAttnTime!=null&&leatestAttnTime!=null){
						Map<String,Object> map = new HashMap<>();
						map.put("startTime", earliestAttnTime);
						map.put("endTime", leatestAttnTime);
						map.put("type", type);
						dateList.clear();
						dateList.add(map);
					}
					break;
				}
				
				//消下属缺勤
				if("140".equals(type)){
					cancelSubAbsenceTime = cancelSubAbsenceTime + attn.getAllAttnTime();
					continue;
				}
				
				leaveType = attn.getLeaveType();
				
				if(attn.getStartWorkTime() == null || attn.getEndWorkTime() == null){
					/**只有单个时间，不做操作，因为没有意义**/
					if(empAttnStatisticsList.size() == 1||(hasCancelSubAbsence&&empAttnStatisticsList.size() == 2)){
						earliestAttnTime = attn.getStartWorkTime();
						leatestAttnTime = attn.getEndWorkTime();
					}
				}else{
					/**有意义的数据条数加1**/
					if(DateUtils.getIntervalHours(attn.getStartWorkTime(), attn.getEndWorkTime())>=1){
						usefulDataSize++;
					}
					Map<String,Object> map = new HashMap<>();
					//allAttnTime = allAttnTime + attn.getAllAttnTime();/**工时**/
					
					if(startTime == null && endTime == null){/**首次进入,i=0**/
						addToList = true;
						startTime = attn.getStartWorkTime();/**数据开始时间**/
						endTime = attn.getEndWorkTime();/**数据结束时间**/
					}else{
						
						if(attn.getStartWorkTime().after(endTime) ){/**如果后面的开始时间大于前面的结束时间**/
						    /**产生一个新的开始时间和结束时间**/
							addToList = true;
							startTime = attn.getStartWorkTime();
							endTime = attn.getEndWorkTime();
						}else{
							/**如果后面的开始时间大于等于前面的结束时间**/
							/**startTime不变**/
							/**优先保留dataType=0的**/
							addToList = false;
							if(attn.getEndWorkTime().after(endTime)){/**结束时间在上个单据结束时间之后**/
								/**更新endTIme**/
								map = dateList.get(size-1);/**得到上一个map**/
								endTime = attn.getEndWorkTime();
								
								if(!type.equals(map.get("type"))){
									if(map.get("type").equals("0")){/**打卡数据**/
										type = (String) map.get("type");
									}else{
									   
									}
								}else{
									
								}
							}else{
								/**endTIme不变**/
							}
						}
					}
					//i++;
					
					if(earliestAttnTime == null) {
						earliestAttnTime = startTime;/**考勤开始时间赋值最早的那个**/
					}
					leatestAttnTime = endTime;/**考勤结束时间赋值最晚的那个**/
					
					map.put("startTime", startTime);
					map.put("endTime", endTime);
					map.put("type", type);
					
					if(null != leaveType){
						map.put("leaveType", leaveType);/**保留请假类型**/
					}
					if(addToList){
						dateList.add(map);
					}else{
						
					}
				}
			}
			/**从已经计算好的时间段得到总共应出勤时间**/
			/**要从多个时间段得到迟到早退，很麻烦**/
			
			/**情况：
			 * 1.统计出来只有一段时间，则比较简单，根据这一段时间来计算出勤。
			 * 2.统计出来有两段时间，那么分别根据这两段时间算出出勤，如果满出勤，那么统计满出勤，异常项都为0；
			 *   如果不是满出勤，则分为旷工，或者非旷工（有迟到早退），如果达到旷工，则记录旷工，无迟到早退；
			 *   如果没达到旷工，则对比打卡的上下班时间，得出迟到，早退
			 * 3.合并的时候dataType优先级，卡>其他所有
			 **/
			Double workMinutes = 0.0;
			Integer tmpComeLateTime = null,tmpLeftEarlyTime = null;/**用来取最小迟到早退时间**/
			for(Map<String, Object> map:dateList){
				
				startTime = (Date) map.get("startTime");
				endTime = (Date) map.get("endTime");
				type = (String) map.get("type");
				leaveType = (String) map.get("leaveType");
				
				if(null != leaveType){
					if(leaveType.equals("1") || leaveType.equals("2") || leaveType.equals("11") || leaveType.equals("12")){
						hasLeave = true;/**有指定请假类型**/
					}
				}
				
				if(null !=startTime && null != endTime ){
					
					//if(type.equals("0")){/**计算出打卡考勤**/
						/**计算打卡**/
						if(startTime.before(classEndTime)){//  下班之前有打卡
							
							if(endTime.after(classStartTime)){//在最早开始时间前过后有打卡
								tmpComeLateTime = 0;
								if(classStartTime.getTime()<startTime.getTime()){
									tmpComeLateTime = getIntervalMinutes2(startTime, classStartTime).intValue();
								}
								if(classStartTime!=null&&classEndTime!=null
										&&"09:00:00".equals(DateUtils.format(classStartTime, DateUtils.FORMAT_HH_MM_SS))
										&&"18:00:00".equals(DateUtils.format(classEndTime, DateUtils.FORMAT_HH_MM_SS))){
									if(tmpComeLateTime <= LATE_MINUTES) {
										tmpComeLateTime = 0;//合理迟到时间，算正常
									} 
								}
								tmpLeftEarlyTime = 0;
								if(classEndTime.getTime()>endTime.getTime()){
									tmpLeftEarlyTime = getIntervalMinutes1(classEndTime,endTime).intValue();
								}
								if(tmpLeftEarlyTime <= 0) {
									tmpLeftEarlyTime = 0;
								} 
								
								lackAttnTime = (tmpComeLateTime + tmpLeftEarlyTime)*1.0;
								workMinutes = mustAttnTime - lackAttnTime<0?0:mustAttnTime - lackAttnTime;
								
								/*if(0 == dateListIndex){*//**首次循环，直接赋值迟到早退**//*
									comeLateTime = tmpComeLateTime;*//**一定是最小的迟到时间**//*
									leftEarlyTime = tmpLeftEarlyTime;*//**一定是最小的早退时间**//*
								}else{
									if(comeLateTime > tmpComeLateTime){
										comeLateTime = tmpComeLateTime;*//**一定是最小的迟到时间**//*
									}
									if(leftEarlyTime > tmpLeftEarlyTime){
										leftEarlyTime = tmpLeftEarlyTime;*//**一定是最小的早退时间**//*
									}
								}*/
								
								if(type.equals("0")||type.equals("2")||type.equals("5")){
									comeLateTime = tmpComeLateTime;
									leftEarlyTime = tmpLeftEarlyTime;
								}else{
									
								}
								
							}else{//旷工
								workMinutes = 0.0;
							}
								
						}else{//旷工
							workMinutes = 0.0;
						}
					/*}else{
						workMinutes = getIntervalMinutes(endTime,startTime)*1.0;
					}*/
				}else{
					workMinutes = 0.0;
				}
				allAttnTime = allAttnTime + workMinutes;
			}
			
			if(minusHour){
				mustAttnTime = getActualTimeMinute(mustAttnTime);
				allAttnTime = getActualTimeMinute(allAttnTime);
			}else{
				
			}
			logger.info("最终计算的出勤工时为：{}",allAttnTime);
			actAttnTime = allAttnTime;
			if(allAttnTime<mustAttnTime){/**出勤时间不够**/
				
				/**重新计算迟到早退时间**/
				lackAttnTime = mustAttnTime - allAttnTime;/**任然缺少这么多工时**/
				
				/*用计算好的总共缺勤与上文的迟到早退进行对比*/
				if(leftEarlyTime <= lackAttnTime){
					comeLateTime = (int) (lackAttnTime - leftEarlyTime);
				}else if(comeLateTime <= lackAttnTime){
					leftEarlyTime = (int) (lackAttnTime - comeLateTime);
				}
				
				//经 {周园园} 确认，迟到早退分开计算！！不相加，不相加！！
				//迟到
				if(comeLateTime >0 && comeLateTime<=60){//迟到小于1h，
					
				}else if(comeLateTime>60 && comeLateTime<=180){//迟到1-3h，旷工半天
					comeLateTime = 0;//迟到0分钟，旷工半天
					absenteeismTime = (int) (0.5*mustAttnTime);
					
				}else if(comeLateTime>180){//迟到大于3h，旷工整天
					comeLateTime = 0;//迟到0分钟，旷工1天
					absenteeismTime = mustAttnTime.intValue();
					leftEarlyTime = 0;
				}
				
				//如果已经算得旷工1天！
				if(absenteeismTime >= mustAttnTime.intValue()){
					leftEarlyTime = 0;
				}else{//如果已经算得旷工不足1天！
					//早退
					if(leftEarlyTime >0 && leftEarlyTime<=60){//迟到小于1h，
						
					}else if(leftEarlyTime>60 && leftEarlyTime<=180){//迟到1-3h，旷工半天
						leftEarlyTime = 0;//早退0分钟，旷工半天
						absenteeismTime = absenteeismTime+(int) (0.5*mustAttnTime);//旷工和之前迟到算得的旷工相加！！
						
					}else if(leftEarlyTime>180){//早退大于3h，旷工整天
						leftEarlyTime = 0;//早退0分钟，旷工1天
						absenteeismTime = mustAttnTime.intValue();
						comeLateTime = 0;
					}
				}
				
				attnStatus = NOT_COMPLETE_STATUS;//0:正常，1：异常
			}else{
				comeLateTime = 0;
				leftEarlyTime = 0;
				lackAttnTime = 0.0;
				attnStatus = COMPLETE_STATUS;//0:正常，1：异常
			}
		}

		if(absenteeismTime>0){
			/**新规则，请假中的病假2，年假1，其他12，事假11，只能请半天和一天，也就是4h和8h**/
			if(hasLeave){/**有指定的请假类型**/
				allAttnTime = mustAttnTime/2;
				absenteeismTime = (int) (mustAttnTime/2);
			}else{
				allAttnTime = mustAttnTime - absenteeismTime<0?0:mustAttnTime - absenteeismTime;
			}
			
		}else{
			allAttnTime = mustAttnTime - lackAttnTime<0?0:mustAttnTime - lackAttnTime;
		}
		
		//存在消下属考勤，考勤肯定正常
		if(cancelSubAbsenceTime>0){
			comeLateTime = 0;
			leftEarlyTime = 0;
			lackAttnTime = 0.0;
			absenteeismTime=0;
			attnStatus = COMPLETE_STATUS;//0:正常，1：异常
		}
		
		resultAttnStatistics.setStartWorkTime(earliestAttnTime);
		resultAttnStatistics.setEndWorkTime(leatestAttnTime);
		resultAttnStatistics.setComeLateTime(comeLateTime);
		resultAttnStatistics.setLeftEarlyTime(leftEarlyTime);
		resultAttnStatistics.setAbsenteeismTime(absenteeismTime);
		resultAttnStatistics.setMustAttnTime(mustAttnTime/60.0);//分钟转换小时
		resultAttnStatistics.setAllAttnTime(((int)(allAttnTime/30))*0.5);
		resultAttnStatistics.setActAttnTime(((int)(actAttnTime/30))*0.5);
		resultAttnStatistics.setLackAttnTime(0.0);//因为标准工时不计算缺勤！！！全部为0
		resultAttnStatistics.setAttnStatus(attnStatus);
		resultAttnStatistics = buildStandard(resultAttnStatistics);
		
	    return resultAttnStatistics;
	}

	/**参数1：统计后的结果，参数2：数据List
	 * @param resultAttnStatistics 
	 * 注意，此方法前提必须是，数据已经按照开始时间，结束时间排好序
	 * **/
	private AttnStatistics buildComprehensive(AttnStatistics resultAttnStatistics, List<AttnStatistics> empAttnStatisticsList,EmployeeClass employeeClass) {
		
		Calendar cal = Calendar.getInstance();
		Double workHours = 0.0,workMinutes = 0.0,lackHours=0.0;//考勤时间，单据申请时间,缺勤时间
		Date startTime = null,endTime = null;/**存放每个时间段的开始，结束时间**/
		Date startWorkTime = null,endWorkTime = null;/**存放合计后的考勤开始，结束时间**/
		Integer attnStatus;
		String type,leaveType;
		double feedingLeaveH = 0;//哺乳假小时数
		int startHours = 9;
		
		resultAttnStatistics.setComeLateTime(0);//迟到
		resultAttnStatistics.setLeftEarlyTime(0);//早退
		resultAttnStatistics.setAbsenteeismTime(0);//旷工
		resultAttnStatistics.setMustAttnTime(FULL_WORK_HOURS);//应出勤
		resultAttnStatistics.setActAttnTime(0d);
		
		if(null == empAttnStatisticsList){
			/**该上班而没有任何数据**/
			attnStatus = NOT_COMPLETE_STATUS;//异常
			lackHours = FULL_WORK_HOURS;//缺勤FULL_WORK_HOURS小时
			//workHours = 0.0;缺勤0小时
			
		}else{
			/**按顺序逐个计算出勤时间**/
			Integer size = 0;/**list的size**/
			Boolean addToList = false;/**默认不把得到的map加进list**/
			List<Map<String,Object>> dateList = new ArrayList<>();/**存放得到的日期范围list**/
			int usefulDataSize = 0;/**有意义的数据条数**/
			for(AttnStatistics attn:empAttnStatisticsList){
				
				/**数据类型**/
				type = attn.getType();
				
				/**判断绝对修改，有的话直接取开始结束时间**/
				if("3".equals(type)){
					startWorkTime = attn.getStartWorkTime();
					endWorkTime = attn.getEndWorkTime();
					if(startWorkTime!=null&&endWorkTime!=null){
						Map<String,Object> map = new HashMap<>();
						map.put("startTime", startWorkTime);
						map.put("endTime", endWorkTime);
						map.put("type", type);
						dateList.clear();
						dateList.add(map);
					}
					break;
				}
				
				leaveType = attn.getLeaveType();
				
				//如果包含哺乳假
				if("4".equals(leaveType)){
					feedingLeaveH = feedingLeaveH + DateUtils.getIntervalHours(attn.getStartWorkTime(),attn.getEndWorkTime());/**出勤分钟数**/
					continue;
				}
				
				size = dateList.size();/**动态变化的size**/
				if(attn.getStartWorkTime() == null || attn.getEndWorkTime() == null){
					/**只有单个时间，不做操作，因为没有意义**/
					if(empAttnStatisticsList.size() == 1){
						startWorkTime = attn.getStartWorkTime();
						endWorkTime = attn.getEndWorkTime();
					}
				}else{
					if(DateUtils.getIntervalHours(attn.getStartWorkTime(), attn.getEndWorkTime())>=1){
						usefulDataSize++;
					}
					Map<String,Object> map = new HashMap<>();
					//workHours = workHours + attn.getAllAttnTime();/**工时**/
					
					if(startTime == null && endTime == null){/**首次进入,i=0**/
						addToList = true;
						startTime = attn.getStartWorkTime();/**数据开始时间**/
						endTime = attn.getEndWorkTime();/**数据结束时间**/
					}else{
						
						if(attn.getStartWorkTime().after(endTime) ){/**如果后面的开始时间大于前面的结束时间**/
						    /**产生一个新的开始时间和结束时间**/
							addToList = true;
							startTime = attn.getStartWorkTime();
							endTime = attn.getEndWorkTime();
						}else{
							/**如果后面的开始时间大于等于前面的结束时间**/
							/**startTime不变**/
							addToList = false;
							if(attn.getEndWorkTime().after(endTime)){/**结束时间在上个单据结束时间之后**/
								/**更新endTIme**/
								map = dateList.get(size-1);/**得到上一个map**/
								
								endTime = attn.getEndWorkTime();
								
								if(!type.equals(map.get("type"))){
									if(map.get("type").equals("60")){/**请假数据**/
										type = (String) map.get("type");
									}else{
									   
									}
								}else{
									
								}
							}else{
								/**endTIme不变**/
							}
						}
					}
					
					if(startWorkTime == null) {
						startWorkTime = startTime;/**考勤开始时间赋值最早的那个**/
					}
					endWorkTime = endTime;/**考勤结束时间赋值最晚的那个**/
					
					map.put("startTime", startTime);
					map.put("endTime", endTime);
					map.put("type", type);
					map.put("leaveType", leaveType);
					if(addToList){
						dateList.add(map);
					}else{
						
					}
				}
			}
			/**从已经计算好的时间段得到总共应出勤时间**/
			Long minutes = 0L;
			Boolean hasLeave = false;/**记录是否有指定类型的请假**/
			for(Map<String,Object> map:dateList){
				
				startTime = (Date) map.get("startTime");
				endTime = (Date) map.get("endTime");
				type = (String) map.get("type");
				leaveType = (String) map.get("leaveType");
				
				if(null != leaveType){
					if(leaveType.equals("1") || leaveType.equals("2") || leaveType.equals("11") || leaveType.equals("12")){
						hasLeave = true;/**有指定请假类型**/
					}
				}
				
				if(null != startTime && null != endTime ){
					if(employeeClass!=null){
						cal.setTime(resultAttnStatistics.getAttnDate());
						cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(DateUtils.format(employeeClass.getStartTime(), "HH")));
						cal.set(Calendar.MINUTE, Integer.valueOf(DateUtils.format(employeeClass.getStartTime(), "mm")));
						Date startClassTime = cal.getTime();
						if(startTime.getTime()<startClassTime.getTime()){
							startTime = startClassTime;
						}
					}else{
						cal.setTime(startTime);
						Integer hour = cal.get(Calendar.HOUR_OF_DAY);/**得到开始时间**/
						if(hour < startHours){/**9点以前打卡的，算9点**/
							cal.set(Calendar.HOUR, 9);
							cal.set(Calendar.MINUTE, 0);
							cal.set(Calendar.SECOND,0);
						}
						startTime = cal.getTime();
					}
					
					minutes = getIntervalMinutes2(endTime,startTime);/**出勤分钟数**/
					if(minutes < 0) {
						minutes = 0L;
					} 
				}else{
					minutes = 0L;
				}
				workMinutes = workMinutes + minutes;
				
			}
			workHours = (int)(workMinutes/30)*0.5;
		    workHours = getActualAllAttnTime(workHours);/**满减**/
			
			logger.info("最终计算的出勤工时为：{}",workHours);
			if(workHours<FULL_WORK_HOURS){
				
				/**新规则，请假中的病假2，年假1，其他12，事假11，只能请半天和一天，也就是4h和8h**/
				if((workHours.intValue()==4 || workHours.intValue()==5) && hasLeave){/**只有一条请假数据,并且是制定的请假类型**/
					workHours = FULL_WORK_HOURS/2;
				}
				lackHours = FULL_WORK_HOURS - workHours;
				attnStatus = NOT_COMPLETE_STATUS;//0:正常，1：异常
				if(feedingLeaveH>=lackHours){
					lackHours = 0.0;
					attnStatus = COMPLETE_STATUS;
					workHours = FULL_WORK_HOURS;
				}
			}else{
				attnStatus = COMPLETE_STATUS;//0:正常，1：异常
			}
		}
		
		resultAttnStatistics.setStartWorkTime(startWorkTime);
		resultAttnStatistics.setEndWorkTime(endWorkTime);
		resultAttnStatistics.setLackAttnTime(lackHours);
		resultAttnStatistics.setAllAttnTime(workHours);
		resultAttnStatistics.setActAttnTime(workHours);
		resultAttnStatistics.setAttnStatus(attnStatus);
		resultAttnStatistics = buildStandard(resultAttnStatistics);

		return resultAttnStatistics;
		
	}

	//所有计算都在上层
	private AttnStatistics buildStandard(AttnStatistics resultAttnStatistics) {
		CURRENT_TIME = DateUtils.parse(DateUtils.getNow(),DateUtils.FORMAT_LONG);
		
		resultAttnStatistics.setCreateUser(CREATE_USER);
		resultAttnStatistics.setUpdateUser(CREATE_USER);
		resultAttnStatistics.setCreateTime(CURRENT_TIME);
		resultAttnStatistics.setUpdateTime(CURRENT_TIME);
			
		return resultAttnStatistics;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void setAttStatisticsForm(AttnWorkHours condition) {
		
		// 保存单据数据，并重新计算该员工考勤
		Long employeeId = condition.getEmployeeId();
		String employeeName;
		Long companyId = condition.getCompanyId();
  		Date formDate  = condition.getWorkDate();//考勤时间
 		Date yesterday = DateUtils.addDay(DateUtils.getToday(),-1);
		condition.setDelFlag(0);
		condition.setCreateTime(new Date());
		Long workMinutes = 0L;
	    if(condition.getStartTime()!=null&&condition.getEndTime()!=null){
	         workMinutes = DateUtils.getIntervalMinutes(condition.getStartTime(), condition.getEndTime());
		}
	    int halfHoursCount = (int)(workMinutes/30);
		final Double workHours = 0.5*halfHoursCount;
		condition.setWorkHours(workHours);
		attnWorkHoursService.save(condition);
		if(formDate.after(yesterday)){//如果提交的单据是昨天以后的，本次不做计算，会有定时汇总计算
			logger.info("该单据在定时计算前提交，本次不做计算，会有定时汇总计算。");
		}else{
			/**立即计算考勤**/
			calculateAttnForm(companyId,employeeId,formDate,yesterday);
		}
	}
	
	/**销假重算考勤接口**/
	@Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
	public void cancelAttStatisticsForm(AttnWorkHours condition) {
		
		/** 删除已经充进来单据数据，并重新计算该员工考勤；
		         注意 分为取消以前的，还是今后（含今天）**/
		Long employeeId = condition.getEmployeeId();
		Long companyId = condition.getCompanyId();
		Date formDate  = condition.getWorkDate();//考勤时间
		Date yesterday = DateUtils.addDay(DateUtils.getToday(),-1);
		condition.setDelFlag(0);
		
		//attnWorkHoursService.save(condition);
		attnWorkHoursService.cancelAttnWorkHours(condition);
		if(formDate.after(yesterday)){//如果提交的单据是昨天以后的，本次不做计算，会有定时汇总计算
			logger.info("该单据在定时计算前提交，本次不做计算，会有定时汇总计算。");
		}else{
			/**立即计算考勤**/
			//TODO.......
			calculateAttnForm(companyId,employeeId,formDate,yesterday);
		}
	}
	
	/**根据表单计算考勤
	 * @param yesterday 
	 * @param formDate 
	 * @param employeeId 
	 * @param companyId **/
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void calculateAttnForm(Long companyId, Long employeeId, Date formDate, Date yesterday){

		String employeeName;
		String countDateStr,startDateStr,endDateStr ;//转换日期用的字符串
	    Date startTime ,endTime;//条件考勤日期,开始，结束时间
	
		Boolean isWorkDay = annualVacationService.judgeWorkOrNot(formDate);//是否不上班true:工作;false:休息。
		List<AttnStatistics> empAttnStatisticsList = null;//表单出勤工时实体
		AttnStatistics resultAttnStatistics = null;
		
		AttnTaskRecord attnTaskRecordCondition = new AttnTaskRecord();
		attnTaskRecordCondition.setEmployId(employeeId);
		attnTaskRecordCondition.setAttnDate(formDate);
		List<Employee> taskEmpList = attnTaskRecordService.selectEmpByAttnTask(attnTaskRecordCondition); 
		
		if(null == taskEmpList || taskEmpList.isEmpty()){
			logger.info("该员工考勤任务尚未建立，本次不做计算，会有定时汇总计算。");
		}else{
			Employee employee = taskEmpList.get(0);
			String workTypeDisplayCode = employee.getWorkTypeName();
			String schedulingDisplayCode = employee.getWhetherSchedulingName();
			employeeName = employee.getCnName();
			
			//1.根据员工id，考勤日期找出已经汇总的考勤
			AttnWorkHours hoursCondition = new AttnWorkHours();
			hoursCondition.setCompanyId(companyId);
			hoursCondition.setEmployeeId(employeeId);
			hoursCondition.setWorkDate(formDate);
			
			Map<Long,List<AttnStatistics>> empAttnStatisticsListMap = null;
			empAttnStatisticsListMap = getAttnStatisticsListMap(hoursCondition);
			
			
			/**找到该员工的排班**/
			EmployeeClass employeeClass = null;//排班实体
			Map<Long,EmployeeClass> employeeClassMap = null;
			final EmployeeClass classCondition = new EmployeeClass();//查询班次条件
			classCondition.setCompanyId(companyId);
			classCondition.setEmployId(employeeId);
			classCondition.setClassDate(formDate);
			employeeClassMap = employeeClassService.getEmployeeClassMap(classCondition);
			
			//2.得到已统计的考勤
			AttnStatistics statisticsCondition =new AttnStatistics();
			statisticsCondition.setCompanyId(companyId);
			statisticsCondition.setEmployId(employeeId);
			statisticsCondition.setAttnDate(formDate);
			AttnStatistics existAttStatistics = attnStatisticsMapper.getAttnStatisticsByCondition(statisticsCondition);
			
			/**3.找到昨天已经汇总的打卡数据,每个员工唯一*
			empCardAttnStatistics = empCardAttnStatisticsMap.get(employeeId);*/
			/**4.找到汇总的,员工单据数据;由于多个单据时间可能和打卡时间重叠，断层，所以不能再叠加计算**/
			
			/**根据员工id得到所有的数据**/
			empAttnStatisticsList = empAttnStatisticsListMap.get(employeeId);
			
			/**考勤对象公用部分**/
			if(null == existAttStatistics){
				resultAttnStatistics = new AttnStatistics();
				resultAttnStatistics.setEmployId(employeeId);
				resultAttnStatistics.setCompanyId(companyId);
				resultAttnStatistics.setAttnDate(formDate);//统计的考勤时间
				resultAttnStatistics.setDelFlag(COMPLETE_STATUS);
			}else{
				resultAttnStatistics = existAttStatistics;
			}
			
			//5.根据不同工时类型，进行考勤出勤汇总
			if(null == workTypeDisplayCode){
				resultAttnStatistics = null;
				logger.error("员工 id: {},姓名:{} 没有定义工时类型，不做计算。",employeeId,employeeName);
			}
			else if(COMPREHENSIVE.equals(workTypeDisplayCode)){//综合工时，没有排班这一说,弹性制，没迟到早退
				
				if(!isWorkDay){//如果是假期，如果有打卡，或者单据，则记录异常考勤
					if(null != empAttnStatisticsList){
						resultAttnStatistics = buildComprehensive(resultAttnStatistics,empAttnStatisticsList,null);
						resultAttnStatistics.setAttnStatus(NOT_COMPLETE_STATUS);//异常
						resultAttnStatistics.setMustAttnTime(0.0);//应出勤0小时
						resultAttnStatistics.setLackAttnTime(0.0);//缺勤0小时
						resultAttnStatistics.setAllAttnTime(0.0);//缺勤0小时
					}else{
						/*假期无数据不做操作*/
						resultAttnStatistics = null;
					}
				}else{
					    employeeClass = employeeClassMap.get(employeeId);
					    //如果是工作日,正常记录打卡，如果无打卡记录，则记录异常
						resultAttnStatistics = buildComprehensive(resultAttnStatistics,empAttnStatisticsList,employeeClass);
				}
				
			}else{//标准工时，判断是否排班,是：根据排班进行计算,没有节假日；否：默认排班，且有节假日。
				employeeClass = employeeClassMap.get(employeeId);
				if(null == schedulingDisplayCode){
					resultAttnStatistics = null;
					logger.error("员工 id: {},姓名:{} 没有定义是否排班，不做计算。",employeeId,employeeName);
				}
				else if(schedulingDisplayCode.equals(SCHEDULING_YES)){//如果需要排班,则无节假日
					
					if(null == employeeClass){//找不到排班数据....记录打卡时间，但是全部异常，没打卡数据不作处理
						logger.error("员工 id: {},姓名:{} 没有定义排班数据",employeeId,employeeName);
						
						resultAttnStatistics = readyStandardFreeDay(resultAttnStatistics,empAttnStatisticsList);
						
					}else{//有排班数据，正常记录，没打卡数据，记异常
						
						startTime = employeeClass.getStartTime();
					
						endTime = employeeClass.getEndTime();
						
						startTime = concatDateTime(formDate, startTime);
						endTime = concatDateTime(formDate, endTime);
						
						if(1 == employeeClass.getIsInterDay()){//跨天+1
							endTime = DateUtils.addDay(endTime, 1);
						}
						
						//调用公共的标准工时封装方法
						resultAttnStatistics = readyStandardWorkDay(startTime,endTime,resultAttnStatistics,empAttnStatisticsList);
					}
				}else{//不需要排班,赋予默认排班时间,设置默认出勤时间9:00 -- 18：00,有节假日
					
					if(!isWorkDay){//如果今天放假，但是有考勤或者单据数据
						
						resultAttnStatistics = readyStandardFreeDay(resultAttnStatistics,empAttnStatisticsList);
						
					}else{
						
						employeeClass = employeeClassMap.get(employeeId);
						
						countDateStr = DateUtils.format(formDate, DateUtils.FORMAT_SHORT);
						
						
						if(employeeClass!=null){
							startDateStr = countDateStr+" "+DateUtils.format(employeeClass.getStartTime(), DateUtils.FORMAT_HH_MM_SS);
							endDateStr = countDateStr+" "+DateUtils.format(employeeClass.getEndTime(), DateUtils.FORMAT_HH_MM_SS);
						}else{
							startDateStr = countDateStr+NIGHT;
							endDateStr = countDateStr+EIGHTEEN;
						}
						startTime = DateUtils.parse(startDateStr, DateUtils.FORMAT_LONG);
						endTime = DateUtils.parse(endDateStr, DateUtils.FORMAT_LONG);
						
						//调用公共的标准工时封装方法
						resultAttnStatistics = readyStandardWorkDay(startTime,endTime,resultAttnStatistics,empAttnStatisticsList);
						
					}
				}
				
			}
			if(null != resultAttnStatistics){
    			  Long existId = null;
    			  existId = attnStatisticsMapper.getExistIdStatistics(resultAttnStatistics);
    			  AttnStatisticsService attnStatisticsService = SpringContextUtils.getContext().getBean(AttnStatisticsService.class);
    			 
    			  if(null == existId){
    				  if(resultAttnStatistics.getDelFlag().intValue() != NOT_COMPLETE_STATUS){
	    				  attnStatisticsService.save(resultAttnStatistics); 
    				  }
    			  }else{
    				  resultAttnStatistics.setId(existId);
    				  attnStatisticsService.updateById(resultAttnStatistics);
    			  }
			}
		}
	}

	@Override
	public List<AttnStatistics> getAttStatisticsList(AttnStatistics condition) throws OaException {
    	
    	if(null == condition) {
    		condition=new AttnStatistics();
    	}
    	condition.setAttnStatus(1);//只查询异常
		
		if(null == condition.getEmployId()){
			
			Long currentEmpId = employeeService.getCurrentEmployeeId();
			condition.setEmployId(currentEmpId);
		}
		
		//如果没有日期，默认查询本月,
		if(condition.getStartTime() == null && condition.getEndTime() == null){
			Calendar cal=Calendar.getInstance();
			cal.set(Calendar.DAY_OF_MONTH,1);
			condition.setStartTime(DateUtils.parse(DateUtils.format(cal.getTime(), DateUtils.FORMAT_SHORT), DateUtils.FORMAT_SHORT));
			
			cal.add(Calendar.MONTH, 1);
			cal.set(Calendar.DAY_OF_MONTH,0);
			condition.setEndTime(DateUtils.parse(DateUtils.format(cal.getTime(), DateUtils.FORMAT_SHORT), DateUtils.FORMAT_SHORT));
		}
		
		
		List<AttnStatistics> list = attnStatisticsMapper.getAttStatisticsList(condition);
		
		for (AttnStatistics attnStatistics : list) {
			attnStatistics.setStartWorkTimeStr(DateUtils.format(attnStatistics.getStartWorkTime(), "HH:mm:ss"));
			attnStatistics.setEndWorkTimeStr(DateUtils.format(attnStatistics.getEndWorkTime(), "HH:mm:ss"));
		}
		return list;
	}

	@Override
	public List<Map<String, Object>> getAttStatisticsDetailList(AttnStatistics condition) throws OaException {
    	
    	if(null == condition) {
    		condition=new AttnStatistics();
    	}
    	condition.setAttnStatus(1);//只查询异常
    	condition.setMustAttnTime(0d);//只查询工作日
    	
		if(null == condition.getEmployId()){
			
			Long currentEmpId = employeeService.getCurrentEmployeeId();
			condition.setEmployId(currentEmpId);
		}
		
		//如果没有日期，默认查询本月,
		if(condition.getStartTime() == null && condition.getEndTime() == null){
			Calendar cal=Calendar.getInstance();
			cal.set(Calendar.DAY_OF_MONTH,1);
			condition.setStartTime(DateUtils.parse(DateUtils.format(cal.getTime(), DateUtils.FORMAT_SHORT), DateUtils.FORMAT_SHORT));
			
			cal.add(Calendar.MONTH, 1);
			cal.set(Calendar.DAY_OF_MONTH,0);
			condition.setEndTime(DateUtils.parse(DateUtils.format(cal.getTime(), DateUtils.FORMAT_SHORT), DateUtils.FORMAT_SHORT));
			
		}
		
		
		List<AttnStatistics> list = attnStatisticsMapper.getAttStatisticsList(condition);
		
		/**查询考勤明细**/
		AttnWorkHours itemCondition = new AttnWorkHours();
		itemCondition.setEmployeeId(condition.getEmployId());
		
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		for (AttnStatistics attnStatistics : list) {
			attnStatistics.setStartWorkTimeStr(DateUtils.format(attnStatistics.getStartWorkTime(), "HH:mm:ss"));
			attnStatistics.setEndWorkTimeStr(DateUtils.format(attnStatistics.getEndWorkTime(), "HH:mm:ss"));
			
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("attnStatistics", attnStatistics);//放最终考勤结果
			
			/**查询明细,因为异常考勤数据本身就比较少，所以可以写在循环里面**/
			itemCondition.setWorkDate(attnStatistics.getAttnDate());
			List<AttnWorkHours> itemList = attnWorkHoursService.getAttnWorkHoursList(itemCondition);
			itemList.forEach(attnWorkHours -> {
				attnWorkHours.setStartTimeStr(DateUtils.format(attnWorkHours.getStartTime(), "HH:mm:ss"));
				attnWorkHours.setEndTimeStr(DateUtils.format(attnWorkHours.getEndTime(), "HH:mm:ss"));
			});
			map.put("attnWorkHoursList",itemList);
			
			resultList.add(map);
		}
		return resultList;
	}
	

	@Override
	public List<Map<String, String>> getSubAttStatisticsList(AttnStatistics condition) {
		List<Map<String, String>> list = null;
		
		//根据汇报对象查询下级员工
		condition.setReportToLeader(condition.getEmployId());
		//查询符合条件的员工类型数据
    	condition.setEmpTypeIdList(configService.getNeedEmpTypeIdList());
		list = attnStatisticsMapper.getSubAttStatisticsList(condition);
		
		return list;
	}

	@Override
	public AttnStatistics getTotalAttStatistics(AttnStatistics condition) throws OaException {
    	
		Boolean firstDayOfMonth = false;/**标记是否为月度第一天**/
		
    	if(null == condition) {
    		condition=new AttnStatistics();
    	}
    	Employee employee = null;
		Long currentEmpId = null;
		
		if(null == condition.getEmployId()){
			employee = employeeService.getCurrentEmployee();
			currentEmpId = employee.getId();
			condition.setEmployId(currentEmpId);
		}else{
			employee = employeeService.getById(condition.getEmployId());
		}
		
		//如果没有日期，默认查询本月,截止到昨日！！！！
		final Calendar cal=Calendar.getInstance();
		Integer dayOfMonth;
		final int monthOfYear = cal.get(Calendar.MONTH);
		if(condition.getStartTime() == null && condition.getEndTime() == null){
			dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
			if(dayOfMonth.intValue() == 1){/**本月第一天**/
				firstDayOfMonth = true;
			}else{
				cal.add(Calendar.DAY_OF_MONTH, -1);/**截止到昨日**/
			}
			
			condition.setEndTime(DateUtils.parse(DateUtils.format(cal.getTime(), DateUtils.FORMAT_SHORT), DateUtils.FORMAT_SHORT));
			
			cal.set(Calendar.DAY_OF_MONTH,1);
			condition.setStartTime(DateUtils.parse(DateUtils.format(cal.getTime(), DateUtils.FORMAT_SHORT), DateUtils.FORMAT_SHORT));
		}else{/**传过来有值，但是本月应出勤只算到昨日，所以结束日期需要减去一天**/
			cal.setTime(condition.getEndTime());
			
			if(monthOfYear == (cal.get(Calendar.MONTH))){//查本月
				
				dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
				if(dayOfMonth.intValue() == 1){/**本月第一天**/
					firstDayOfMonth = true;
				}else{
					cal.add(Calendar.DAY_OF_MONTH, -1);/**截止到昨日**/
				}
				condition.setEndTime(DateUtils.parse(DateUtils.format(cal.getTime(), DateUtils.FORMAT_SHORT), DateUtils.FORMAT_SHORT));
			}else{/**查询上月，不做操作**/
				
			}
		}
		
		CompanyConfig companyConfigConditon = new CompanyConfig();//查询配置表条件
		companyConfigConditon.setCode("typeOfWork");//工时类型
		List<CompanyConfig> workTypeList = companyConfigService.getListByCondition(companyConfigConditon);
		Map<Long,String> workTypeMap = new HashMap<Long,String>();//存放工时类型配置的map
		for(CompanyConfig cConfig:workTypeList){
        	workTypeMap.put(cConfig.getId(), cConfig.getDisplayCode());
        }
		
		//查询时间段内考勤数据
		AttnStatistics totalAttn = new AttnStatistics();
		if("comprehensive".equals(workTypeMap.get(employee.getWorkType()))) {
			
			totalAttn = attnStatisticsMapper.getTotalAttStatistics(condition);
			
			if(null == totalAttn){//没有任何考勤数据
				totalAttn = new AttnStatistics();
				totalAttn.setCompanyId(condition.getCompanyId());
				totalAttn.setEmployId(condition.getEmployId());
				totalAttn.setAllAttnTime(0.0);
				totalAttn.setActAttnTime(0.0);
			}
		}else {
			List<AttnStatistics> totalAttnLsit = attnStatisticsMapper.getStandardAttStatisticsList(condition);
			//查询时间段内消下属考勤
			List<RemoveSubordinateAbsence> removeList = removeSubordinateAbsenceMapper.getAbsenceHoursList(condition.getEmployId(), condition.getStartTime(), condition.getEndTime());
			Map<Date,RemoveSubordinateAbsence> removeMap = removeList.stream().collect(Collectors.toMap(RemoveSubordinateAbsence :: getAttendanceDate,a->a,(k1,k2)->k1));
			Double must_attn_time = 0.0;
			Double all_attn_time = 0.0;
			Double act_attn_time = 0.0;
			for(AttnStatistics data:totalAttnLsit) {
				must_attn_time += data.getMustAttnTime();
				all_attn_time += data.getAllAttnTime();
				if(removeMap!=null&&removeMap.containsKey(data.getAttnDate())) {
					double act_time = (data.getActAttnTime() + removeMap.get(data.getAttnDate()).getRemoveAbsenceHours())>data.getMustAttnTime()?data.getMustAttnTime():(data.getActAttnTime() + removeMap.get(data.getAttnDate()).getRemoveAbsenceHours());
					act_attn_time += act_time;
				}else {
					act_attn_time += data.getActAttnTime();
				}
			}
			totalAttn.setEmployId(condition.getEmployId());
			totalAttn.setMustAttnTime(must_attn_time);
			totalAttn.setActAttnTime(act_attn_time);
			totalAttn.setAllAttnTime(all_attn_time);
		}
		
		condition.setAttnDate(condition.getStartTime());
		AttnStatistics mustHours =  getTotalMustAtten(condition,firstDayOfMonth);
		
		if(null != mustHours) {
			totalAttn.setMustAttnTime(mustHours.getMustAttnTime());
		}
		totalAttn.setWorkTypeName(workTypeMap.get(employee.getWorkType()));
		return totalAttn;
	}
	


	@Override
	public AttnStatistics getMonthTotalMustAtten(AttnStatistics condition) throws OaException {
		
		/**获取月度第一天和最后一天**/
		final Calendar cal=Calendar.getInstance();
		cal.setTime(condition.getStartTime());/**月份的任何一天，无所谓**/
		
		/**设置日期为本月第一天**/
		cal.set(Calendar.DAY_OF_MONTH,1);
		condition.setStartTime(DateUtils.parse(DateUtils.format(cal.getTime(), DateUtils.FORMAT_SHORT), DateUtils.FORMAT_SHORT));
		
		/**设置日期为本月最大日期  **/
		cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
		condition.setEndTime(DateUtils.parse(DateUtils.format(cal.getTime(), DateUtils.FORMAT_SHORT), DateUtils.FORMAT_SHORT));
		
		return getTotalMustAtten(condition,false);
	}
	

    //月度应出勤
	@Override
	public AttnStatistics getTotalMustAtten(AttnStatistics condition, Boolean firstDayOfMonth) throws OaException {
		
		Long employeeId = condition.getEmployId();
		//1.准备员工
		Employee employee = employeeService.getById(employeeId);
		
		Date classDate = condition.getAttnDate();
		Date cStartTime = condition.getStartTime();
        if(Integer.valueOf(DateUtils.format(cStartTime,DateUtils.FORMAT_SIMPLE))<Integer.valueOf(DateUtils.format(employee.getFirstEntryTime(),DateUtils.FORMAT_SIMPLE))){
        	cStartTime = employee.getFirstEntryTime();
		}
		Date cEndTime = condition.getEndTime();
	    if(Integer.valueOf(DateUtils.format(cEndTime,DateUtils.FORMAT_SIMPLE))<Integer.valueOf(DateUtils.format(employee.getFirstEntryTime(),DateUtils.FORMAT_SIMPLE))){
	    	cEndTime = employee.getFirstEntryTime();
		}
		//本月总天数
		Calendar aCalendar = Calendar.getInstance();
		aCalendar.setTime(classDate);
		
		//修改点：不按整月查询，改为查询指定时间段
		//Double monthTotal = aCalendar.getActualMaximum(Calendar.DATE) * 1.0;//当月总天数，用来计算综合工时和默认排班工时应出勤时间
		Double dayTotal = DateUtils.getIntervalDays(cStartTime, cEndTime)*1.0;
		
		if(dayTotal.intValue() == 0 && firstDayOfMonth){/**说明是每个月第一天，不计算**/
			condition.setMustAttnTime(0.0);
		}else{
			dayTotal++;
			Integer freeDays = 0;
			
			//2.准备工时类型和是否排班map，key:配置ID，value：配置编码
			CompanyConfig companyConfigConditon = new CompanyConfig();//查询配置表条件
			companyConfigConditon.setCode("typeOfWork");//工时类型
			List<CompanyConfig> workTypeList = companyConfigService.getListByCondition(companyConfigConditon);
			
			Config configCondition = new Config();
			configCondition.setCode("whetherScheduling");//是否排班
			List<Config> whetherSchedulingList = configService.getListByCondition(configCondition);
			
	        Map<Long,String> workTypeMap = new HashMap<Long,String>();//存放工时类型配置的map
	        Map<Long,String> schedulingMap = new HashMap<Long,String>();//存放是否排班配置的map
	        
	        for(CompanyConfig cConfig:workTypeList){
	        	workTypeMap.put(cConfig.getId(), cConfig.getDisplayCode());
	        }
	        for(Config config:whetherSchedulingList){
	        	schedulingMap.put(config.getId(), config.getDisplayCode());
	        }
	        
			
			//3.查询员工排班数据map，key:员工ID，value：考勤数据
			EmployeeClass classCondition = new EmployeeClass();//查询班次条件
			classCondition.setEmployId(employeeId);
			classCondition.setClassDate(null);
			classCondition.setStartTime(cStartTime);
			classCondition.setEndTime(cEndTime);
			
			//4.计算应出勤工时
			Long workTypeId;//工时类型ID
			Long whetherScheduling;//是否排班
			String workTypeDisplayCode = null,schedulingDisplayCode;//工时类型displayCode,是否排班displayCode
			
			Boolean isWorkDay = true;//是否不上班true:工作;false:休息。

			workTypeId = employee.getWorkType();
			whetherScheduling = employee.getWhetherScheduling();

	        if(workTypeMap.containsKey(workTypeId)){//有工时类型
	        	workTypeDisplayCode = workTypeMap.get(workTypeId);
	        }else{
	        	workTypeDisplayCode = "standard";
				logger.error("员工 id: {},姓名:{} 没有工时类型！",employeeId,employee.getCnName());
	        }
	        
	        //准备本月，节假日信息
	    	String month = DateUtils.format(classDate, "yyyy-MM");
	    	Map<String,Boolean> isWorkMap =  annualVacationService.judgeWorkOrNot(month);
			if("comprehensive".equals(workTypeDisplayCode)){//综合工时，没有排班这一说,月份天数减去假期
				  
			    Calendar cal = Calendar.getInstance();  
			    cal.setTime(classDate);//month 为指定月份任意日期  
			    //int dayNumOfMonth = TimeUtils.getDaysByYearMonth(year, m);  
			    cal.set(Calendar.DAY_OF_MONTH, 1);// 从一号开始  
			    Date d;
			    for (int i = 0; i < dayTotal; i++, cal.add(Calendar.DATE, 1)) {  
			        d = cal.getTime();  
			        if(Integer.valueOf(DateUtils.format(d,DateUtils.FORMAT_SIMPLE))<Integer.valueOf(DateUtils.format(employee.getFirstEntryTime(),DateUtils.FORMAT_SIMPLE))){
			        	cal.add(Calendar.DATE, DateUtils.getIntervalDays(d, employee.getFirstEntryTime()));
			        	d = employee.getFirstEntryTime();
					}
					isWorkDay = isWorkMap.get(DateUtils.format(d, DateUtils.FORMAT_SHORT));//是否不上班true:工作;false:休息。
					if(null == isWorkDay) {
						isWorkDay=false;
					}
					if(!isWorkDay){
						freeDays++;
					}
			   } 
			    dayTotal = dayTotal - freeDays;
			    dayTotal = dayTotal*8;//转换成小时
			}else if("standard".equals(workTypeDisplayCode)){
				
	            if(schedulingMap.containsKey(whetherScheduling)){//有工时类型
	            	schedulingDisplayCode = schedulingMap.get(whetherScheduling);
	            }else{
					logger.error("员工 id: {},姓名:{} 没有设置是否排班.默认不排班",employeeId,employee.getCnName());
					schedulingDisplayCode = "no";
	            }
					
				if(schedulingDisplayCode.equals("yes")){//如果需要排班,则无节假日

					EmployeeClass employeeClassHours = employeeClassService.getEmployeeClassHours(classCondition);//得到员工的月度排班数据
					if(null != employeeClassHours){
						dayTotal = employeeClassHours.getMustAttnTime();
					}else{
						logger.error("员工 id: {},姓名:{} 需要排班，但是没有设置排班数据",employeeId,employee.getCnName());
						dayTotal = 0.0;
					}
					
				}else{//如果不需要排班,赋予默认排班时间,设置默认出勤时间9:00 -- 18：00,有节假日
					  
				    Calendar cal = Calendar.getInstance();  
				    cal.setTime(classDate);//month 为指定月份任意日期  
				    //int dayNumOfMonth = TimeUtils.getDaysByYearMonth(year, m);  
				    cal.set(Calendar.DAY_OF_MONTH, 1);// 从一号开始  
				    Date d ;
					    for (int i = 0; i < dayTotal; i++, cal.add(Calendar.DATE, 1)) {  
					        d = cal.getTime();  
					        if(Integer.valueOf(DateUtils.format(d,DateUtils.FORMAT_SIMPLE))<Integer.valueOf(DateUtils.format(employee.getFirstEntryTime(),DateUtils.FORMAT_SIMPLE))){
					        	cal.add(Calendar.DATE, DateUtils.getIntervalDays(d, employee.getFirstEntryTime()));
					        	d = employee.getFirstEntryTime();
							}
							//isWorkDay = annualVacationService.judgeWorkOrNot(d);//是否不上班true:工作;false:休息。
					        //TODO:
					        isWorkDay = isWorkMap.get(DateUtils.format(d, DateUtils.FORMAT_SHORT));
							if(isWorkDay!=null && !isWorkDay){
								freeDays++;
							}
					   } 
					    dayTotal = dayTotal - freeDays;
					    dayTotal = dayTotal*8;//转换成小时
				   }
	            }
			condition.setMustAttnTime(dayTotal);
		}
		return condition;
	}

	//date1的年月日拼接上date2的时分秒
	private Date concatDateTime(Date date1,Date date2){
		Calendar cal1 = Calendar.getInstance(); //获取日历实例  
		Calendar cal2 = Calendar.getInstance();  
		
		cal1.setTime(date1); //字符串按照指定格式转化为日期  
		cal2.setTime(date2);
	    
	    int i = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);  
	    cal2.add(Calendar.YEAR, i);
	    
	    i = cal1.get(Calendar.MONTH) - cal2.get(Calendar.MONTH);  
	    cal2.add(Calendar.MONTH, i);
		
	    i = cal1.get(Calendar.DATE) - cal2.get(Calendar.DATE);  
	    cal2.add(Calendar.DATE, i);
		
		return cal2.getTime();  
	}
	
	private Long getIntervalMinutes(Date date, Date otherDate) {
		long time = date.getTime() - otherDate.getTime();
		long s = time == 0 ? 0 : time/1000;
		long sup = s%60;
		long returnT = time == 0 ? 0 : time/(60 * 1000);
		if(sup!=0){
			returnT = returnT +1;
		}
		return returnT;
	}
	
	private Long getIntervalMinutes1(Date date, Date otherDate) {
		long time = date.getTime() - otherDate.getTime();
		long s = time == 0 ? 0 : time/1000;
		long sup = s%60;
		long returnT = time == 0 ? 0 : time/(60 * 1000);
		if(time>0&&sup!=0){
			returnT = returnT +1;
		}
		return returnT;
	}
	
	private Long getIntervalMinutes2(Date date, Date otherDate) {
		long time = date.getTime() - otherDate.getTime();
		long returnT = time == 0 ? 0 : time/(60 * 1000);
		return returnT;
	}

	@Override
	public AttnStatistics getAttnStatisticsByCondition(
			AttnStatistics statisticsCondition) {
		return attnStatisticsMapper.getAttnStatisticsByCondition(statisticsCondition);
	}

	private Double getActualTimeMinute(Double mustAttnTime){

		if(mustAttnTime.compareTo(300.0)>=0 && mustAttnTime.compareTo(600.0)<0){
			mustAttnTime = mustAttnTime-60.0;
			if(mustAttnTime.compareTo(480.0)>0){
				mustAttnTime = 480.0;
			}
		}else if(mustAttnTime.compareTo(600.0)>=0){
			mustAttnTime = mustAttnTime-120.0;
		}
		return mustAttnTime;
	}
	


	/**得到实际出勤时间,单位h**/
	private Double getActualAllAttnTime(Double allAttnTime) {
		
		if(allAttnTime >= 10){
			allAttnTime = allAttnTime-2;
		}else if(allAttnTime >= 5 && allAttnTime < 10){
			allAttnTime = allAttnTime-1;
			if(allAttnTime > 8){//10小时以内，最多取8小时
				allAttnTime = 8.0;
			}
		}
		
		return allAttnTime;
		
	}

	@Override
	public Map<String, String> getNumberByType(AttnStatistics condition) {
		//只查询指定员工类型的数据
    	condition.setEmpTypeIdList(configService.getNeedEmpTypeIdList());
		Map<String, String> map = attnStatisticsMapper.getNumberByType(condition);
		return map;
	}

	@Override
	public List<Map<String, String>> getEmpAttnTimes(AttnStatistics condition) {
		//只查询指定员工类型的数据
    	condition.setEmpTypeIdList(configService.getNeedEmpTypeIdList());
		List<Map<String, String>> list = attnStatisticsMapper.getEmpAttnTimes(condition);
		return list;
	}
	@Override
	public XSSFWorkbook exportMonthLeaveDetail(Date startTime, Date endTime,Long departId) throws Exception {
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		String subject = null, sttachmentName = null,departName = null;		
		//列数超过255行
		//假期明细报表
		XSSFWorkbook xSSFWorkbook = null;
		subject = "月度假期明细报表";
		sttachmentName = "月度假期明细报表";
		String[] keys = {"code", "name", 
				/*年假:1*/"1_1", "1_2", "1_3", "1_4", "1_5", "1_6", "1_7", "1_8", "1_9", "1_10", "1_11", "1_12", "1_13", "1_14", "1_15", "1_16", "1_17", "1_18", "1_19", "1_20", "1_21", "1_22", "1_23", "1_24", "1_25", "1_26", "1_27", "1_28", "1_29", "1_30", "1_31",
				/*事假:11*/"11_1", "11_2", "11_3", "11_4", "11_5", "11_6", "11_7", "11_8", "11_9", "11_10", "11_11", "11_12", "11_13", "11_14", "11_15", "11_16", "11_17", "11_18", "11_19", "11_20", "11_21", "11_22", "11_23", "11_24", "11_25", "11_26", "11_27", "11_28", "11_29", "11_30", "11_31",
				/*病假:2*/"2_1", "2_2", "2_3", "2_4", "2_5", "2_6", "2_7", "2_8", "2_9", "2_10", "2_11", "2_12", "2_13", "2_14", "2_15", "2_16", "2_17", "2_18", "2_19", "2_20", "2_21", "2_22", "2_23", "2_24", "2_25", "2_26", "2_27", "2_28", "2_29", "2_30", "2_31",
				/*调休:5*/"5_1", "5_2", "5_3", "5_4", "5_5", "5_6", "5_7", "5_8", "5_9", "5_10", "5_11", "5_12", "5_13", "5_14", "5_15", "5_16", "5_17", "5_18", "5_19", "5_20", "5_21", "5_22", "5_23", "5_24", "5_25", "5_26", "5_27", "5_28", "5_29", "5_30", "5_31",
				/*产假:7*/"7_1", "7_2", "7_3", "7_4", "7_5", "7_6", "7_7", "7_8", "7_9", "7_10", "7_11", "7_12", "7_13", "7_14", "7_15", "7_16", "7_17", "7_18", "7_19", "7_20", "7_21", "7_22", "7_23", "7_24", "7_25", "7_26", "7_27", "7_28", "7_29", "7_30", "7_31",
				/*陪产假:9*/"9_1", "9_2", "9_3", "9_4", "9_5", "9_6", "9_7", "9_8", "9_9", "9_10", "9_11", "9_12", "9_13", "9_14", "9_15", "9_16", "9_17", "9_18", "9_19", "9_20", "9_21", "9_22", "9_23", "9_24", "9_25", "9_26", "9_27", "9_28", "9_29", "9_30", "9_31",
				/*丧假:10*/"10_1", "10_2", "10_3", "10_4", "10_5", "10_6", "10_7", "10_8", "10_9", "10_10", "10_11", "10_12", "10_13", "10_14", "10_15", "10_16", "10_17", "10_18", "10_19", "10_20", "10_21", "10_22", "10_23", "10_24", "10_25", "10_26", "10_27", "10_28", "10_29", "10_30", "10_31",
				/*婚假:3*/"3_1", "3_2", "3_3", "3_4", "3_5", "3_6", "3_7", "3_8", "3_9", "3_10", "3_11", "3_12", "3_13", "3_14", "3_15", "3_16", "3_17", "3_18", "3_19", "3_20", "3_21", "3_22", "3_23", "3_24", "3_25", "3_26", "3_27", "3_28", "3_29", "3_30", "3_31",
				/*产前假:6*/"6_1", "6_2", "6_3", "6_4", "6_5", "6_6", "6_7", "6_8", "6_9", "6_10", "6_11", "6_12", "6_13", "6_14", "6_15", "6_16", "6_17", "6_18", "6_19", "6_20", "6_21", "6_22", "6_23", "6_24", "6_25", "6_26", "6_27", "6_28", "6_29", "6_30", "6_31",
				/*流产假:8*/"8_1", "8_2", "8_3", "8_4", "8_5", "8_6", "8_7", "8_8", "8_9", "8_10", "8_11", "8_12", "8_13", "8_14", "8_15", "8_16", "8_17", "8_18", "8_19", "8_20", "8_21", "8_22", "8_23", "8_24", "8_25", "8_26", "8_27", "8_28", "8_29", "8_30", "8_31"
				/*哺乳假:4*///"4_1", "4_2", "4_3", "4_4", "4_5", "4_6", "4_7", "4_8", "4_9", "4_10", "4_11", "4_12", "4_13", "4_14", "4_15", "4_16", "4_17", "4_18", "4_19", "4_20", "4_21", "4_22", "4_23", "4_24", "4_25", "4_26", "4_27", "4_28", "4_29", "4_30", "4_31",
				/*其他:12*///"12_1", "12_2", "12_3", "12_4", "12_5", "12_6", "12_7", "12_8", "12_9", "12_10", "12_11", "12_12", "12_13", "12_14", "12_15", "12_16", "12_17", "12_18", "12_19", "12_20", "12_21", "12_22", "12_23", "12_24", "12_25", "12_26", "12_27", "12_28", "12_29", "12_30", "12_31"
		};
		//title的第一行
		String[] titles0 = {"年假", "事假", "病假", "调休", "产假", "陪产假", "丧假", "婚假", "产前假", "流产假"/*, "哺乳假", "其他"*/};
		//title的第一行标题对应的列数坐标{起始行号，终止行号， 起始列号，终止列号}
		int[][] title0Coordinate = {{0,0,2,32},{0,0,33,63},{0,0,64,94},{0,0,95,125},{0,0,126,156},{0,0,157,187},{0,0,188,218},{0,0,219,249},{0,0,250,280},{0,0,281,311}/*,{0,0,312,342},{0,0,343,383}*/};
		List<Date> months = DateUtils.findMonths(startTime, endTime);
		String[] monthTitles = new String[months.size()];
		for (int i = 0; i < months.size(); i++) {
			monthTitles[i] = DateUtils.getYearAndMonth(months.get(i));
		}
		String[] titles1 = {"员工编号", "姓名", 
				/*年假:1*/"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
				/*事假:11*/"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
				/*病假:2*/"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
				/*调休:5*/"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
				/*产假:7*/"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
				/*陪产假:9*/"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
				/*丧假:10*/"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
				/*婚假:3*/"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
				/*产前假:6*/"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
				/*流产假:8*/"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"
				/*哺乳假:4*///"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
				/*其他:12*///"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"
		};
		Map<String, List<Map<String, Object>>> datasMap = empLeaveReportService.getExcelDataByYearAndMonth(monthTitles, startTime, endTime, departId,null);
		String fileNames = "月度假期明细报表"+monthTitles[0].substring(0,4)+"年";
		xSSFWorkbook = ExcelUtil.exportExcelForLeaveReports(datasMap,keys,titles0,title0Coordinate,titles1,monthTitles,fileNames+".xls");
		return xSSFWorkbook;
	}
	
	
	@Override
	public void exportData(String reportId, Date startTime, Date endTime,Long departId) throws Exception {
		Employee currentEmployee = employeeService.getCurrentEmployee();
		String toMail = currentEmployee.getEmail();
		
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		String subject = null, sttachmentName = null,departName = null;
		
		Map<Long,String> departMap = new HashMap<>();/**用来存放，id对应的部门名称，防止多次数据交互**/
		
		if("leaveReports".equals(reportId)){
			//列数超过255行
			//假期明细报表
			XSSFWorkbook xSSFWorkbook = null;
			subject = "月度假期明细报表";
			sttachmentName = "月度假期明细报表";
			String[] keys = {"code", "name", 
					/*年假:1*/"1_1", "1_2", "1_3", "1_4", "1_5", "1_6", "1_7", "1_8", "1_9", "1_10", "1_11", "1_12", "1_13", "1_14", "1_15", "1_16", "1_17", "1_18", "1_19", "1_20", "1_21", "1_22", "1_23", "1_24", "1_25", "1_26", "1_27", "1_28", "1_29", "1_30", "1_31",
					/*事假:11*/"11_1", "11_2", "11_3", "11_4", "11_5", "11_6", "11_7", "11_8", "11_9", "11_10", "11_11", "11_12", "11_13", "11_14", "11_15", "11_16", "11_17", "11_18", "11_19", "11_20", "11_21", "11_22", "11_23", "11_24", "11_25", "11_26", "11_27", "11_28", "11_29", "11_30", "11_31",
					/*病假:2*/"2_1", "2_2", "2_3", "2_4", "2_5", "2_6", "2_7", "2_8", "2_9", "2_10", "2_11", "2_12", "2_13", "2_14", "2_15", "2_16", "2_17", "2_18", "2_19", "2_20", "2_21", "2_22", "2_23", "2_24", "2_25", "2_26", "2_27", "2_28", "2_29", "2_30", "2_31",
					/*调休:5*/"5_1", "5_2", "5_3", "5_4", "5_5", "5_6", "5_7", "5_8", "5_9", "5_10", "5_11", "5_12", "5_13", "5_14", "5_15", "5_16", "5_17", "5_18", "5_19", "5_20", "5_21", "5_22", "5_23", "5_24", "5_25", "5_26", "5_27", "5_28", "5_29", "5_30", "5_31",
					/*产假:7*/"7_1", "7_2", "7_3", "7_4", "7_5", "7_6", "7_7", "7_8", "7_9", "7_10", "7_11", "7_12", "7_13", "7_14", "7_15", "7_16", "7_17", "7_18", "7_19", "7_20", "7_21", "7_22", "7_23", "7_24", "7_25", "7_26", "7_27", "7_28", "7_29", "7_30", "7_31",
					/*陪产假:9*/"9_1", "9_2", "9_3", "9_4", "9_5", "9_6", "9_7", "9_8", "9_9", "9_10", "9_11", "9_12", "9_13", "9_14", "9_15", "9_16", "9_17", "9_18", "9_19", "9_20", "9_21", "9_22", "9_23", "9_24", "9_25", "9_26", "9_27", "9_28", "9_29", "9_30", "9_31",
					/*丧假:10*/"10_1", "10_2", "10_3", "10_4", "10_5", "10_6", "10_7", "10_8", "10_9", "10_10", "10_11", "10_12", "10_13", "10_14", "10_15", "10_16", "10_17", "10_18", "10_19", "10_20", "10_21", "10_22", "10_23", "10_24", "10_25", "10_26", "10_27", "10_28", "10_29", "10_30", "10_31",
					/*婚假:3*/"3_1", "3_2", "3_3", "3_4", "3_5", "3_6", "3_7", "3_8", "3_9", "3_10", "3_11", "3_12", "3_13", "3_14", "3_15", "3_16", "3_17", "3_18", "3_19", "3_20", "3_21", "3_22", "3_23", "3_24", "3_25", "3_26", "3_27", "3_28", "3_29", "3_30", "3_31",
					/*产前假:6*/"6_1", "6_2", "6_3", "6_4", "6_5", "6_6", "6_7", "6_8", "6_9", "6_10", "6_11", "6_12", "6_13", "6_14", "6_15", "6_16", "6_17", "6_18", "6_19", "6_20", "6_21", "6_22", "6_23", "6_24", "6_25", "6_26", "6_27", "6_28", "6_29", "6_30", "6_31",
					/*流产假:8*/"8_1", "8_2", "8_3", "8_4", "8_5", "8_6", "8_7", "8_8", "8_9", "8_10", "8_11", "8_12", "8_13", "8_14", "8_15", "8_16", "8_17", "8_18", "8_19", "8_20", "8_21", "8_22", "8_23", "8_24", "8_25", "8_26", "8_27", "8_28", "8_29", "8_30", "8_31"
					/*哺乳假:4*///"4_1", "4_2", "4_3", "4_4", "4_5", "4_6", "4_7", "4_8", "4_9", "4_10", "4_11", "4_12", "4_13", "4_14", "4_15", "4_16", "4_17", "4_18", "4_19", "4_20", "4_21", "4_22", "4_23", "4_24", "4_25", "4_26", "4_27", "4_28", "4_29", "4_30", "4_31",
					/*其他:12*///"12_1", "12_2", "12_3", "12_4", "12_5", "12_6", "12_7", "12_8", "12_9", "12_10", "12_11", "12_12", "12_13", "12_14", "12_15", "12_16", "12_17", "12_18", "12_19", "12_20", "12_21", "12_22", "12_23", "12_24", "12_25", "12_26", "12_27", "12_28", "12_29", "12_30", "12_31"
			};
			//title的第一行
			String[] titles0 = {"年假", "事假", "病假", "调休", "产假", "陪产假", "丧假", "婚假", "产前假", "流产假"/*, "哺乳假", "其他"*/};
			//title的第一行标题对应的列数坐标{起始行号，终止行号， 起始列号，终止列号}
			int[][] title0Coordinate = {{0,0,2,32},{0,0,33,63},{0,0,64,94},{0,0,95,125},{0,0,126,156},{0,0,157,187},{0,0,188,218},{0,0,219,249},{0,0,250,280},{0,0,281,311}/*,{0,0,312,342},{0,0,343,383}*/};
			List<Date> months = DateUtils.findMonths(startTime, endTime);
			String[] monthTitles = new String[months.size()];
			for (int i = 0; i < months.size(); i++) {
				monthTitles[i] = DateUtils.getYearAndMonth(months.get(i));
			}
			String[] titles1 = {"员工编号", "姓名", 
					/*年假:1*/"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
					/*事假:11*/"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
					/*病假:2*/"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
					/*调休:5*/"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
					/*产假:7*/"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
					/*陪产假:9*/"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
					/*丧假:10*/"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
					/*婚假:3*/"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
					/*产前假:6*/"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
					/*流产假:8*/"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"
					/*哺乳假:4*///"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
					/*其他:12*///"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"
			};
			//查询符合条件的员工类型的数据
    		List<Long> empTypeIdList = configService.getNeedEmpTypeIdList();
			Map<String, List<Map<String, Object>>> datasMap = empLeaveReportService.getExcelDataByYearAndMonth(monthTitles, startTime, endTime, departId,empTypeIdList);
			String fileNames = "月度假期明细报表"+monthTitles[0].substring(0,4)+"年";
			xSSFWorkbook = ExcelUtil.exportExcelForLeaveReports(datasMap,keys,titles0,title0Coordinate,titles1,monthTitles,fileNames+".xls");
			SendMailUtil.sendMailForLargeColWorkbook(xSSFWorkbook,toMail,fileNames,fileNames);
		}else{
			//列数范围(0~255)
			HSSFWorkbook hSSFWorkbook = null;
			
			//reportId:empAttnDetail员工打卡明细,actualTimeRecord实时打卡明细
			if(reportId.equals("empAttnDetail")){//员工考勤明细,
				subject = "员工考勤明细";
				sttachmentName = "员工考勤明细";
				//查询符合条件的员工类型的数据
	    		List<Long> empTypeIdList = configService.getNeedEmpTypeIdList();
				
	    		//查询出所有符合条件的员工数据
	    		List<EmployeeBaseInfoDTO> baseInfoList = employeeBaseInfoMapper.getYGKQMXReport
	    				(startTime, endTime, departId, empTypeIdList);
	    		//查询出考勤统计数据
	    		List<AttnStatistics> attnStatisticsList = attnStatisticsMapper.getYGKQMXReport(startTime, endTime, departId, empTypeIdList);
	    		Map<Long,List<AttnStatistics>> attnStatisticsMap = attnStatisticsList.stream().collect(Collectors.groupingBy(AttnStatistics :: getEmployId));
	    		
	    		//延时工作登记
	    		List<DelayWorkRegister> workRegisterList = delayWorkRegisterMapper.getListByDate(startTime, endTime);
	    		Map<Long,List<DelayWorkRegister>> workRegisterMap = workRegisterList.stream().collect(Collectors.groupingBy(DelayWorkRegister :: getEmployeeId));
	    		
	    		//消下属缺勤
	    		List<RemoveSubordinateAbsence> absenceList = removeSubordinateAbsenceMapper.getCompletedListByDate(startTime, endTime);
	    		Map<Long,List<RemoveSubordinateAbsence>> absenceListMap = absenceList.stream().collect(Collectors.groupingBy(RemoveSubordinateAbsence :: getEmployeeId));
	    		
	    		//考勤明细
	    		List<AttnWorkHours> workHoursList = attnWorkHoursMapper.getListByDate(startTime, endTime);
	    		Map<Long,List<AttnWorkHours>> workHoursMap = workHoursList.stream().collect(Collectors.groupingBy(AttnWorkHours :: getEmployId));
	    		
	    		//班次
	    		EmployeeClass employeeClassP = new EmployeeClass();
    			employeeClassP.setStartTime(startTime);
    			employeeClassP.setEndTime(endTime);
    			List<EmployeeClass> employeeClassList = employeeClassMapper.getEmployeeClassList(employeeClassP);
    			Map<Long,List<EmployeeClass>> employeeClassMap = employeeClassList.stream().collect(Collectors.groupingBy(EmployeeClass :: getEmployId));
    			
    			//工时制
    			CompanyConfig companyConfigConditon = new CompanyConfig();//查询配置表条件
    			companyConfigConditon.setCode("typeOfWork");//工时类型
    			List<CompanyConfig> workTypeList = companyConfigService.getListByCondition(companyConfigConditon);
    			Map<Long,CompanyConfig> workTypeMap = workTypeList.stream().collect(Collectors.toMap(CompanyConfig :: getId,a->a,(k1,k2)->k1));
    			
	    		//组装报表
	    		for(EmployeeBaseInfoDTO emp:baseInfoList){
	    			
	    			//工时类型
	    			String workTypeName = "";
	    			if(workTypeMap!=null&&workTypeMap.containsKey(emp.getWorkType())){
	    				workTypeName = workTypeMap.get(emp.getWorkType()).getDisplayName();
	    			}
	    			
	    			//考勤结果，一天一条数据
	    			List<AttnStatistics> attnStatisticsListEmp = new ArrayList<AttnStatistics>();
	    			//获取该员工指定时间段的考勤数据
	    			if(attnStatisticsMap!=null&&attnStatisticsMap.containsKey(emp.getId())){
	    				attnStatisticsListEmp = attnStatisticsMap.get(emp.getId());
	    			}
	    			
	    			//考勤明细（包括单据，打卡，定位签到记录等）,根据员工按天分类
	    			Map<Date,List<AttnWorkHours>> workHoursListEmpDateMap = new HashMap<Date,List<AttnWorkHours>>();
	    			if(workHoursMap!=null&&workHoursMap.containsKey(emp.getId())){
	    				workHoursListEmpDateMap = workHoursMap.get(emp.getId()).stream().collect(Collectors.groupingBy(AttnWorkHours :: getWorkDate));
	    			}
	    			
	    			//延时工作登记
	    			Map<Date,List<DelayWorkRegister>> workRegisterEmpDateMap = new HashMap<Date,List<DelayWorkRegister>>();
	    			if(workRegisterMap!=null&&workRegisterMap.containsKey(emp.getId())){
	    				workRegisterEmpDateMap = workRegisterMap.get(emp.getId()).stream().collect(Collectors.groupingBy(DelayWorkRegister :: getDelayDate));
	    			}
	    			
	    			//消下属缺勤
	    			Map<Date,List<RemoveSubordinateAbsence>> absenceListEmpDateMap = new HashMap<Date,List<RemoveSubordinateAbsence>>();
	    			if(absenceListMap!=null&&absenceListMap.containsKey(emp.getId())){
	    				absenceListEmpDateMap = absenceListMap.get(emp.getId()).stream().collect(Collectors.groupingBy(RemoveSubordinateAbsence :: getAttendanceDate));
	    			}
	    			
	    			//班次
	    			Map<Date,List<EmployeeClass>> employeeClassListEmpDateMap = new HashMap<Date,List<EmployeeClass>>();
	    			if(employeeClassMap!=null&&employeeClassMap.containsKey(emp.getId())){
	    				employeeClassListEmpDateMap = employeeClassMap.get(emp.getId()).stream().collect(Collectors.groupingBy(EmployeeClass :: getClassDate));
	    			}
	    			
	    			//组装考勤数据
	    			for(AttnStatistics sta:attnStatisticsListEmp){
	    				Map<String,Object> data = new HashMap<String,Object>();
		    			data.put("code", emp.getCode());//员工编号
		    			data.put("cn_name", emp.getCnName());//员工姓名
		    			data.put("departName", emp.getDepartName());//部门名称
		    			data.put("positionName", emp.getPositionName());//职位名称
		    			data.put("workTypeName", workTypeName);//工时类型
		    			data.put("attn_date", DateUtils.format(sta.getAttnDate(), DateUtils.FORMAT_SHORT));//考勤日期
		    			data.put("dayofweek", "星期"+DateUtils.getWeek(sta.getAttnDate()));//星期
		    			data.put("className", "");//班次
		    			List<EmployeeClass> employeeClassListEmpDate = new ArrayList<EmployeeClass>();
		    			if(employeeClassListEmpDateMap!=null&&employeeClassListEmpDateMap.containsKey(sta.getAttnDate())){
		    				employeeClassListEmpDate = employeeClassListEmpDateMap.get(sta.getAttnDate());
		    				if(employeeClassListEmpDate!=null&&employeeClassListEmpDate.size()>0){
		    					data.put("className", employeeClassListEmpDate.get(0).getName());
		    				}
		    			}
		    			String start_work_time = "";//上班打卡
		    			String end_work_time = "";//下班打卡
		    			Double work_hours = 0d;//考勤工时
		    			
		    			List<AttnWorkHours> workHoursListEmpDate = new ArrayList<AttnWorkHours>();
		    			if(workHoursListEmpDateMap!=null&&workHoursListEmpDateMap.containsKey(sta.getAttnDate())){
		    				workHoursListEmpDate = workHoursListEmpDateMap.get(sta.getAttnDate());
		    			}
		    			data.put("data_type","");//说明
		    			String data_type = "";
		    			String leave_type = "";
		    			String data_reason = "";
		    			
		    			for(AttnWorkHours awh:workHoursListEmpDate){
		    				//正常打卡，定位签到，员工签到
		    				if(awh.getDataType()!=null&&(awh.getDataType().intValue()==0||awh.getDataType().intValue()==5||awh.getDataType().intValue()==4)){
		    					start_work_time = awh.getStartTime()!=null?DateUtils.format(awh.getStartTime(), DateUtils.FORMAT_HH_MM_SS):"";
		    					end_work_time = awh.getEndTime()!=null?DateUtils.format(awh.getEndTime(), DateUtils.FORMAT_HH_MM_SS):"";
		    					work_hours = awh.getWorkHours();
		    				}
		    				//消下属缺勤
		    				data_type += awh.getDataType()+",";
		    				leave_type += awh.getLeaveType()+",";
		    				data_reason += awh.getDataReason()+",";
		    			}
		    			if(null != data_type && data_type.length()>0){
		    				data_type = getReasonByType(data_type,leave_type,data_reason);
		    				data.put("data_type", data_type);
						}
		    			data.put("start_work_time", start_work_time);//上班打卡
		    			data.put("end_work_time", end_work_time);//下班打卡
		    			data.put("classStartTime", sta.getStartWorkTime()!=null?DateUtils.format(sta.getStartWorkTime(),DateUtils.FORMAT_HH_MM_SS):"");//上班考勤
		    			data.put("classEndTime", sta.getEndWorkTime()!=null?DateUtils.format(sta.getEndWorkTime(),DateUtils.FORMAT_HH_MM_SS):"");//下班考勤
		    			data.put("come_late_time", sta.getComeLateTime());//迟到
		    			data.put("left_early_time", sta.getLeftEarlyTime());//早退
		    			Double must_attn_time = sta.getMustAttnTime()!=null?sta.getMustAttnTime():8d;
		    			Integer absenteeism_time = sta.getAbsenteeismTime()!=null?sta.getAbsenteeismTime():0;
		    			Double absenteeism_time_d = 0d;
		    			if(must_attn_time>=8){
		    				absenteeism_time_d = absenteeism_time/480.0;
						}else{
							if(must_attn_time>0){
								absenteeism_time_d =  absenteeism_time/(must_attn_time*60);
							}else{
								absenteeism_time_d = 0d;
							}
							
						}
		    			data.put("absenteeism_time",absenteeism_time_d);//旷工
		    			data.put("lack_attn_time",sta.getLackAttnTime());//缺勤
		    			data.put("must_attn_time",must_attn_time);//应出勤
		    			data.put("work_hours",work_hours);//考勤工时
		    			data.put("all_attn_time",sta.getAllAttnTime()!=null?sta.getAllAttnTime():0d);//实际出勤工时
		    			data.put("act_attn_time",sta.getActAttnTime()!=null?sta.getActAttnTime():0d);//标准出勤工时
		    			
		    			//延时工作小时数
		    			List<DelayWorkRegister> workRegisterEmpDate = new ArrayList<DelayWorkRegister>();
		    			if(workRegisterEmpDateMap!=null&&workRegisterEmpDateMap.containsKey(sta.getAttnDate())){
		    				workRegisterEmpDate = workRegisterEmpDateMap.get(sta.getAttnDate());
		    			}
		    			//延时工作小时数
		    			data.put("delay_hours", 0);
						if(workRegisterEmpDate!=null&&workRegisterEmpDate.size()>0){
							if(workRegisterEmpDate.get(0).getExpectDelayHour()!=null&&workRegisterEmpDate.get(0).getIsMatched()!=null
									&&workRegisterEmpDate.get(0).getIsMatched().intValue()==0){
								data.put("delay_hours", 0);
							}
							if(workRegisterEmpDate.get(0).getExpectDelayHour()!=null&&workRegisterEmpDate.get(0).getIsMatched()!=null
									&&workRegisterEmpDate.get(0).getIsMatched().intValue()==1&&workRegisterEmpDate.get(0).getActualDelayHour()!=null){
								data.put("delay_hours", workRegisterEmpDate.get(0).getActualDelayHour()>workRegisterEmpDate.get(0).getExpectDelayHour()
										 ?workRegisterEmpDate.get(0).getExpectDelayHour():workRegisterEmpDate.get(0).getActualDelayHour());
							}
							if(workRegisterEmpDate.get(0).getIsConfirm()!=null&&workRegisterEmpDate.get(0).getIsConfirm().intValue()==1){
								data.put("delay_hours", workRegisterEmpDate.get(0).getActualDelayHour());
							}
		    			}
						//抵扣小时数
						List<RemoveSubordinateAbsence> absenceMapEmpDate = new ArrayList<RemoveSubordinateAbsence>();
						if(absenceListEmpDateMap!=null&&absenceListEmpDateMap.containsKey(sta.getAttnDate())){
							absenceMapEmpDate = absenceListEmpDateMap.get(sta.getAttnDate());
						}
						data.put("remove_absence_hours","0");
						if(absenceMapEmpDate!=null&&absenceMapEmpDate.size()>0){
							data.put("remove_absence_hours",absenceMapEmpDate.get(0).getRemoveAbsenceHours());
						}
		    			
		    			data.put("attn_status",(sta.getAttnStatus()!=null&&sta.getAttnStatus().intValue()==0)?"正常":"异常");//考勤状态
		    			datas.add(data);
	    			}
	    		}
	    		
//	    		datas = attnStatisticsMapper.getEmpAttnDetailReport(startTime,endTime,departId,empTypeIdList);
//				
//				String dataType,leaveType,dataReason;
//				Double expect_delay_hour,actual_delay_hour,must_attn_time,absenteeism_time;
//				Integer is_matched,is_confirm;
//				
//				for(Map<String, Object> o:datas){
//					dataType = (String) o.get("data_type");//考勤说明,这里需要转化为汉字
//					leaveType = (String) o.get("leave_type");//考勤说明,这里需要转化为汉字
//					dataReason = (String) o.get("data_reason");//考勤说明,这里需要转化为汉字
//					expect_delay_hour = (Double) o.get("expect_delay_hour");//预计延时小时数
//					actual_delay_hour = (Double) o.get("actual_delay_hour");//实际延时小时数
//					is_matched = (Integer) o.get("is_matched");//是否匹配考勤
//					is_confirm = (Integer) o.get("is_confirm");//是否确认
//					Integer absenteeism_time_m = o.get("absenteeism_time")!=null?(Integer) o.get("absenteeism_time"):0;//旷工时间
//					must_attn_time = o.get("must_attn_time")!=null?(Double) o.get("must_attn_time"):8d;//应出勤
//					
//					if(must_attn_time>=8){
//						absenteeism_time = absenteeism_time_m/480.0;
//					}else{
//						if(must_attn_time>0){
//							absenteeism_time =  absenteeism_time_m/(must_attn_time*60);
//						}else{
//							absenteeism_time = 0d;
//						}
//						
//					}
//					o.put("absenteeism_time", absenteeism_time);
//					
//					if(null != dataType && dataType.length()>0){
//						dataType = getReasonByType(dataType,leaveType,dataReason);
//						o.put("data_type", dataType);
//					}
//					
//					departId = (Long) o.get("departId");
//					if(departMap.containsKey(departId)){
//						departName = departMap.get(departId);
//					}else{
//						departName = departService.getDepartAllLeaveName(departId);
//						departMap.put(departId,departName);
//					}
//					o.put("departName", departName);
//					//延时工作小时数
//					o.put("delay_hours", 0);
//					//
//					if(expect_delay_hour!=null&&is_matched!=null&&is_matched==0){
//						o.put("delay_hours", 0);
//					}
//					if(expect_delay_hour!=null&&is_matched!=null&&is_matched==1&&actual_delay_hour!=null){
//						o.put("delay_hours", actual_delay_hour>expect_delay_hour?expect_delay_hour:actual_delay_hour);
//					}
//					if(is_confirm!=null&&is_confirm==1){
//						o.put("delay_hours", actual_delay_hour);
//					}
//					
//				}
				
				String[] titles = { "员工编号","员工姓名","部门","职位名称","工时制","考勤日期","星期","班次","上班打卡","下班打卡","上班考勤","下班考勤","迟到（分）","早退（分）","旷工（天）","缺勤（时）","应出勤工时","考勤工时","实际出勤工时","标准出勤工时","延时工作小时数","抵扣小时数","考勤状态","说明"};
				String[] keys = { "code","cn_name","departName","positionName","workTypeName","attn_date","dayofweek","className","start_work_time","end_work_time","classStartTime","classEndTime","come_late_time",
						"left_early_time","absenteeism_time","lack_attn_time","must_attn_time","work_hours","all_attn_time","act_attn_time","delay_hours","remove_absence_hours","attn_status","data_type"};
				
				hSSFWorkbook = ExcelUtil.exportExcel(datas,keys,titles,"workbook.xls");
			}else if(reportId.equals("actualTimeRecord")){//实时打卡明细
				
				subject = "实时打卡明细";
				sttachmentName = "实时打卡明细";
				String[] titles = {"员工编号","员工姓名","部门","工时制","考勤日期","星期","班次","打卡时间"};
				String[] keys = {"code","cn_name","departName","workTypeName","attn_date","dayofweek","className","sign_time"};
				//查询符合条件的员工类型的数据
	    		List<Long> empTypeIdList = configService.getNeedEmpTypeIdList();
				datas = attnSignRecordService.getSSDKMXReport(startTime,endTime,departId,empTypeIdList);

//				for(Map<String, Object> o:datas){
//					departId = (Long) o.get("departId");
//					if(departMap.containsKey(departId)){
//						departName = departMap.get(departId);
//					}else{
//						departName = departService.getDepartAllLeaveName(departId);
//						departMap.put(departId,departName);
//					}
//					o.put("departName", departName);
//				}
				
				hSSFWorkbook = ExcelUtil.exportExcel(datas,keys,titles,"workbook.xls");
				
			}else if(reportId.equals("applicationOverTimeDetail")){//月度加班单明细表
				
				subject = "月度加班单明细表";
				sttachmentName = "月度加班单明细表";
				String[] titles = {"员工编号","申请人","部门","职位","加班日期","星期","加班事由",
						"预计加班开始时间","预计加班结束时间","预计加班小时数","实际加班开始时间","实际加班结束时间",
						"实际加班小时数","批核主管","加班申请日期","状态","备注"};
				String[] keys = {"code","cn_name","depart_name","position_name","apply_date","dayofweek","apply_type",
						"expect_start_time","expect_end_time","expect_duration","actual_start_time","actual_end_time",
						"actual_duration","report_to_leader","submit_date","approval_status","reason"};
				datas = empApplicationOvertimeService.getApplyOverTimeExcel(startTime,endTime,departId);
				
				hSSFWorkbook = ExcelUtil.exportExcel(datas,keys,titles,"workbook.xls");
			}else if(reportId.equals("applicationOverTimeSumReport")){//月度加班统计汇总表
				
				subject = "月度加班统计汇总表";
				sttachmentName = "月度加班统计汇总表";
				String[] titles = {"员工编号","申请人","部门","职位","月度","1.5倍小时数","2倍小时数",
						"3倍小时数","加班总计小时数","月度增加调休小时","总剩余调休小时","晚间餐费次数","晚间交通费次数1(21：00-22:59)","晚间交通费次数2(23点之后)",
						"加班原因","备注"};
				String[] keys = {"code","cn_name","depart_name","position_name","yearAndMonth","time1","time2",
						"time3","actual_duration","remainRest","canUseTime","meals","trafficMeals1","trafficMeals2",
						"",""};
				datas = empApplicationOvertimeService.getApplyOverTimeSumReport(startTime,endTime,departId);
				
				hSSFWorkbook = ExcelUtil.exportExcel(datas,keys,titles,"workbook.xls");
			}else if(reportId.equals("monthLack")){//月度缺勤统计表
				
				subject = "月度缺勤统计表";
				sttachmentName = "月度缺勤统计表";
				String[] titles = {"员工编号","姓名","部门","缺勤次数"};
				String[] keys = {"code","cn_name","departName","total"};
				//查询符合条件的员工类型的数据
	    		List<Long> empTypeIdList = configService.getNeedEmpTypeIdList();
				datas = attnStatisticsMapper.getEmpAttnMonthLack(startTime,endTime,departId,empTypeIdList);

				for(Map<String, Object> o:datas){
					departId = (Long) o.get("departId");
					if(departMap.containsKey(departId)){
						departName = departMap.get(departId);
					}else{
						departName = departService.getDepartAllLeaveName(departId);
						departMap.put(departId,departName);
					}
					o.put("departName", departName);
				}
				
				hSSFWorkbook = ExcelUtil.exportExcel(datas,keys,titles,"workbook.xls");
				
			}else if(reportId.equals("monthLackDetail")){//月度缺勤统计总数表，其实就是明细
				
				subject = "月度缺勤总数表";
				sttachmentName = "月度缺勤总数表";
 				String[] titles = {"员工编号","姓名","部门","职位名称","工时制","是否排班","月度","迟到+早退（分）","旷工（天）","缺勤（时）","应出勤工时","实际出勤工时","标准出勤工时","当月延时工作小时数","上月累计剩余延时工作小时数","本月可用延时工作小时数","抵扣小时数","抵扣后实际出勤小时数","合计出勤小时","本月累计剩余延时工作小时数"};
				String[] keys = {"code","cn_name","departName","positionName","workTypeName","className","attn_date","lateAndEarly",
						         "absenteeism_time","lack_attn_time","must_attn_time","all_attn_time","act_attn_time","total_delay_hours","lastMonthTotalHours","currentMonthTotalHours","deduct_hours","after_deduct_act_attn_hours","sum_attn_hours","currentMonthRemoveTotalHours"};
				//查询符合条件的员工类型的数据
	    		List<Long> empTypeIdList = configService.getNeedEmpTypeIdList();
				datas = attnStatisticsMapper.getEmpAttnMonthLackDetail(startTime,endTime,departId,empTypeIdList);
				
				//当前月份
				String month = DateUtils.format(startTime, DateUtils.FORMAT_YYYY_MM);
				
				//查询上月累计登记小时数
				Map<String,Double> lastMonthTotalHoursMap = new HashMap<String,Double>();
				Date startMonth = DateUtils.parse(month.substring(0, 4)+"-01-01", DateUtils.FORMAT_SHORT);
				Date endMonth = DateUtils.parse(month+"-01", DateUtils.FORMAT_SHORT);
				List<Map<String,Object>> lastMonthTotalHoursList = empDelayHoursMapper.getLastMonthTotalHours(startMonth, endMonth, departId);
				for(Map<String,Object> lastMonthTotalHours:lastMonthTotalHoursList){
					lastMonthTotalHoursMap.put(lastMonthTotalHours.get("code").toString()+"_"+month, 
							Double.valueOf(lastMonthTotalHours.get("totalDelayHours").toString()));
				}
				//查询上月累计使用小时数
				Map<String,Double> lastMonthTotalUsedHoursMap = new HashMap<String,Double>();
				List<Map<String,Object>> lastMonthTotalUsedHoursList = removeSubordinateAbsenceMapper.getLastMonthTotalUsedHours(startMonth, endMonth, departId);
				for(Map<String,Object> lastMonthTotalUsedHours:lastMonthTotalUsedHoursList){
					lastMonthTotalUsedHoursMap.put(lastMonthTotalUsedHours.get("code").toString()+"_"+month, 
							Double.valueOf(lastMonthTotalUsedHours.get("usedHours").toString()));
				}
				
				//当月累计剩余延时工作小时数
				Map<String,Double> currentMonthTotalHoursMap = new HashMap<String,Double>();
				endMonth = DateUtils.addMonth(endMonth, 1);
				List<Map<String,Object>> currentMonthTotalHoursList = empDelayHoursMapper.getLastMonthTotalHours(startMonth, endMonth, departId);
				for(Map<String,Object> currentMonthTotalHours:currentMonthTotalHoursList){
					currentMonthTotalHoursMap.put(currentMonthTotalHours.get("code").toString()+"_"+month, 
							Double.valueOf(currentMonthTotalHours.get("totalDelayHours").toString()));
				}
				
				//查询上月累计使用小时数
				Map<String,Double> currentMonthTotalUsedHoursMap = new HashMap<String,Double>();
				List<Map<String,Object>> currentMonthTotalUsedHoursList = removeSubordinateAbsenceMapper.getLastMonthTotalUsedHours(startMonth, endMonth, departId);
				for(Map<String,Object> currentMonthTotalUsedHours:currentMonthTotalUsedHoursList){
					currentMonthTotalUsedHoursMap.put(currentMonthTotalUsedHours.get("code").toString()+"_"+month, 
							Double.valueOf(currentMonthTotalUsedHours.get("usedHours").toString()));
				}
				
				for(Map<String, Object> o:datas){
					
					//上月累计剩余延时工作小时数
					String key  = (String)o.get("code") +"_"+ o.get("attn_date").toString();
					Double lastMonthTotalHours = (lastMonthTotalHoursMap!=null&&lastMonthTotalHoursMap.containsKey(key))?lastMonthTotalHoursMap.get(key):0d;
					Double lastMonthTotalUsedHours = (lastMonthTotalUsedHoursMap!=null&&lastMonthTotalUsedHoursMap.containsKey(key))?lastMonthTotalUsedHoursMap.get(key):0d;
					logger.info(o.get("code")+";lastMonthTotalHours="+lastMonthTotalHours+";lastMonthTotalUsedHours="+lastMonthTotalUsedHours);
					o.put("lastMonthTotalHours", lastMonthTotalHours-lastMonthTotalUsedHours);
					
					//当月延时工作小时数
					Double currentMonthHours = o.get("total_delay_hours")!=null?Double.valueOf(o.get("total_delay_hours").toString()):0d;
					logger.info(o.get("code")+";currentMonthHours="+currentMonthHours);
					
					//本月可用延时工作小时数
					Double currentMonthTotalHours = lastMonthTotalHours - lastMonthTotalUsedHours + currentMonthHours;
					logger.info(o.get("code")+";currentMonthTotalHours="+currentMonthTotalHours);
					
					//本月累计剩余延时工作小时数
					Double currentMonthRemoveTotalHours = (currentMonthTotalHoursMap!=null&&currentMonthTotalHoursMap.containsKey(key))?currentMonthTotalHoursMap.get(key):0d;
					Double currentMonthTotalUsedHours = (currentMonthTotalUsedHoursMap!=null&&currentMonthTotalUsedHoursMap.containsKey(key))?currentMonthTotalUsedHoursMap.get(key):0d;
					logger.info(o.get("code")+";currentMonthRemoveTotalHours="+currentMonthRemoveTotalHours+";currentMonthTotalUsedHours="+currentMonthTotalUsedHours);
					o.put("currentMonthTotalHours", currentMonthTotalHours);
					o.put("currentMonthRemoveTotalHours", currentMonthRemoveTotalHours-currentMonthTotalUsedHours);
					
					departId = (Long) o.get("departId");
					if(departMap.containsKey(departId)){
						departName = departMap.get(departId);
					}else{
						departName = departService.getDepartAllLeaveName(departId);
						departMap.put(departId,departName);
					}
					o.put("departName", departName);
				}
				
				hSSFWorkbook = ExcelUtil.exportExcel(datas,keys,titles,"workbook.xls");
			}else if(reportId.equals("monthLeaveSummary")){//月度假期统计表
				subject = "月度假期统计表";
				sttachmentName = "月度假期统计表";
				String[] titles = {"员工编号","姓名","部门","当月年假次数","当月年假天数","当月病假次数","当月病假天数","当月事假次数","当月事假天数","当月调休次数","当月调休小时数","当月产假次数","当月产假天数","当月陪产假次数","当月陪产假天数","当月丧假次数","当月丧假天数","当月婚假次数","当月婚假天数","当月产前假次数","当月产前假天数","当月流产假次数","当月流产假天数"};
				String[] keys = {"code","cnName","departName","count_1","days_1","count_2","days_2","count_11","days_11","count_5","days_5","count_7","days_7","count_9","days_9","count_10","days_10","count_3","days_3","count_6","days_6","count_8","days_8"};
				
				List<Date> months = DateUtils.findMonths(startTime, endTime);
				String[] monthTitles = new String[months.size()];
				for (int i = 0; i < months.size(); i++) {
					monthTitles[i] = DateUtils.getYearAndMonth(months.get(i));
				}
				//查询符合条件的员工类型的数据
	    		List<Long> empTypeIdList = configService.getNeedEmpTypeIdList();
				Map<String, List<Map<String, Object>>> datasMap = empLeaveReportService.getmonthLeaveSummary(startTime,endTime,departId,monthTitles,empTypeIdList);
				
				hSSFWorkbook = ExcelUtil.exportExcelForMonths(datasMap,keys,titles,monthTitles,"月度假期统计表.xls");
			}else if(reportId.equals("monthAllowanceTimes")){//晚间餐费与晚间交通费次数统计
				subject = "晚间餐费与交通费补贴月度汇总";
				sttachmentName = "晚间餐费与交通费补贴月度汇总";
				String[] titles = {"员工编号","员工姓名","部门","职位","工时制","月度","晚间餐费次数","晚间交通费次数1(21：00-22:59)","晚间交通费次数2(23点之后)"};
				String[] keys = {"employeeCode","employeeName","departName","positionName","workType","month","dinnerCount","traffic45","trafficSBSX"};
				datas = attnReportService.getDinnerAndTrafficAllowanceMonthSummary(startTime, endTime, departId);
				
				hSSFWorkbook = ExcelUtil.exportExcel(datas,keys,titles,"workbook.xls");
			}else if(reportId.equals("monthAllowanceDetails")){
				subject = "晚间餐费与交通费补贴月度明细";
				sttachmentName = "晚间餐费与交通费补贴月度明细";
				String[] titles = {"员工编号","员工姓名","部门","职位","工时制","考勤日期","星期","班次","下班考勤","延时工作小时数","晚间餐费","晚间交通费额度"};
				String[] keys = {"employeeCode","employeeName","departName","positionName","workType","attnDate","week","attnClass","offDutyTime","overtimeHours","dinnerAllowance","trafficAllowance"};
				datas = attnReportService.getDinnerAndTrafficAllowanceMonthDetail(startTime, endTime, departId);

				hSSFWorkbook = ExcelUtil.exportExcel(datas,keys,titles,"workbook.xls");
			}
			
			String excelName = sttachmentName;
			String startMonth = DateUtils.format(startTime,DateUtils.FORMAT_SIMPLE);
			if(StringUtils.isNotBlank(startMonth)){
				excelName += startMonth;
			}
			String endMonth = DateUtils.format(endTime,DateUtils.FORMAT_SIMPLE);
			if(StringUtils.isNotBlank(endMonth)){
				excelName += "-" + endMonth;
			}
			
			pool.execute(new SendExeclMail(hSSFWorkbook,toMail,subject,excelName));
			//SendMailUtil.sendExcelMail(hSSFWorkbook,toMail,subject,excelName);
		}
		
	}
	
	private String getReasonByType(String dataType, String leaveType,String dataReason){
		
		StringBuffer sb = new StringBuffer("");
		String reason = "";
		
		String[] dataTypeArray = dataType.split(",");
		String[] dataTypeArray1 =  Arrays.stream(dataTypeArray).distinct().toArray(String[]::new);
		for(String type:dataTypeArray1){
			if(RunTask.RUN_CODE_10.equals(type)){
				sb.append("入职登记申请,");
			}else if(RunTask.RUN_CODE_20.equals(type)){
				sb.append("离职申请,");
			}else if(RunTask.RUN_CODE_30.equals(type)){
				sb.append("延时工作申请,");
			}else if(RunTask.RUN_CODE_40.equals(type)||RunTask.RUN_CODE_41.equals(type)){
				sb.append("异常考勤,");
			}else if(RunTask.RUN_CODE_50.equals(type)){
				sb.append("排班,");
			}else if(RunTask.RUN_CODE_60.equals(type)){
				
				/**针对请假做特殊处理**/
				if(null == leaveType || "".equals(leaveType)){
					sb.append("请假申请,");
				}else{
					sb.append(getLeaveReasonByType(leaveType,dataReason));
				}
				
			}else if(RunTask.RUN_CODE_70.equals(type)){
				sb.append("外出申请,");
			}else if(RunTask.RUN_CODE_80.equals(type)){
				sb.append("出差申请,");
			}else if(RunTask.RUN_CODE_90.equals(type)){
				sb.append("总结报告,");
			}else if(RunTask.RUN_CODE_100.equals(type)){
				sb.append("延迟工作晚到,");
			}else if(RunTask.RUN_CODE_110.equals(type)){
				sb.append("值班,");
			}else if(RunTask.RUN_CODE_120.equals(type)){
				sb.append("下属异常考勤,");
			}else if("2".equals(type)){
				sb.append("HR输入,");
			}else if("3".equals(type)){
				sb.append("HR修改,");
			}else if("4".equals(type)){
				sb.append("员工签到,");
			}else if("5".equals(type)){
				sb.append("定位签到,");
			}else if("6".equals(type)){
				sb.append("远程,");
			}else if(RunTask.RUN_CODE_140.equals(type)){
				sb.append("消下属缺勤,");
			}
		}
		
		Integer length = sb.length();
		if(length>0){
			reason = sb.substring(0,length-1);
		}
		
		return reason;
	}
	
	private String getLeaveReasonByType(String leaveType, String dataReason){
		
		StringBuffer sb = new StringBuffer("");
		String reason = "";
		
		String[] leaveTypeArray = leaveType.split(",");
		String[] leaveTypeArray1 =  Arrays.stream(leaveTypeArray).distinct().toArray(String[]::new);
		
		for(String type:leaveTypeArray1){
			if("1".equals(type)){
				sb.append("年假,");
			}else if("2".equals(type)){
				sb.append("病假,");
			}else if("3".equals(type)){
				sb.append("婚假,");
			}else if("4".equals(type)){
				sb.append("哺乳假,");
			}else if("5".equals(type)){
				sb.append("调休,");
			}else if("6".equals(type)){
				sb.append("产前假,");
			}else if("7".equals(type)){
				sb.append("产假,");
			}else if("8".equals(type)){
				sb.append("流产假,");
			}else if("9".equals(type)){
				sb.append("陪产假,");
			}else if("10".equals(type)){
				sb.append("丧假,");
			}else if("11".equals(type)){
				sb.append("事假,");
			}else if("12".equals(type)){
				sb.append("其他,");
				if(null == dataReason){
					
				}else{
					sb.append(dataReason+",");
				}
			}
		}
		
		reason = sb.toString();
		return reason;
	}
	
	/**
	 * @throws Exception 
	  * attnExMsgRemind(异常考勤提醒)
	  * @Title: attnExMsgRemind
	  * @Description: 异常考勤提醒()
	  * void    返回类型
	  * @throws
	 */
	public void attnExMsgRemind() throws Exception{
		long startTime = System.currentTimeMillis();
		logger.info("异常考勤提醒定时start ... ...");
		
		AttnStatistics attnStatistics = new AttnStatistics();
		attnStatistics.setStartTime(DateUtils.getFirstDay(DateUtils.addMonth(new Date(),-1)));
		attnStatistics.setEndTime(DateUtils.getLastDay(DateUtils.addMonth(new Date(),-1)));
		//只查询指定员工类型数据
		attnStatistics.setEmpTypeIdList(configService.getNeedEmpTypeIdList());
		
		logger.info("[step01]查询员工是否存在异常考勤记录 ... ...");
		List<AttnStatistics> attnStatisticss = attnStatisticsMapper.getAttnExReaminList(attnStatistics);
		if(null == attnStatisticss || attnStatisticss.size() == 0){
			logger.info("全体员工没有考勤数据");
			return;
		}
		
		//获取不用提醒人群员工编号
		logger.info("[step02]获取不用提醒人群员工编号 ... ...");
		CompanyConfig msgConfig = new CompanyConfig();
		msgConfig.setCode(CompanyConfig.ATTN_EX_MSG_REMIND_EXCEPT);
		List<CompanyConfig> msgList = companyConfigService.getListByCondition(msgConfig);
		Map<String,String> msgMap = msgList.stream().collect(Collectors.toMap(CompanyConfig :: getDisplayCode, CompanyConfig :: getDisplayCode));
		
		//获取综合工时的配置
		logger.info("[step03]获取综合工时的配置 ... ...");
		CompanyConfig companyConfig = new CompanyConfig();
		companyConfig.setCode(CompanyConfig.TYPE_OF_WORK);
		companyConfig.setDisplayCode(CompanyConfig.TYPE_OF_WORK_ZH);
		CompanyConfig zhConf = companyConfigService.getByCondition(companyConfig);
		
		//当前时间往后推3个工作日
		Date lastDate = annualVacationService.get5WorkingDayNextmonth(3);

		//异常提醒判断
		for(AttnStatistics as : attnStatisticss){
			try{
				attnExMsgRemind(msgMap, as,zhConf,lastDate);
			}catch(Exception e){
				logger.error("发送异常考勤提醒异常,msg={}",e.getMessage());
			}
		}
		
		long endTime = System.currentTimeMillis();
		logger.info("异常考勤提醒定时end,cost={}",endTime - startTime);
	}
	
	public void attnExMsgRemind(Map<String,String> msgMap,AttnStatistics as,CompanyConfig zhConf,Date lastDate) throws Exception{
		Employee emp = employeeService.getById(as.getEmployId());
		
		if(emp==null) {
			logger.info("attnExMsgRemind:员工信息不存在");
			return;
		}
		
		if(!msgMap.isEmpty() && null != emp && StringUtils.isNotEmpty(emp.getCode()) 
				&& StringUtils.isNotBlank(msgMap.get(emp.getCode())) 
				&& msgMap.get(emp.getCode()).equals(emp.getCode())){
			logger.info("员工{}不用发送异常考勤提醒",emp.getCnName());
			return;
		}
		
		//判断员工是否是综合工时，综合工时平均每个工作日满8小时则视作全勤，不用发送异常申诉
		if(emp.getWorkType().equals(zhConf.getId())){//综合工时
			AttnStatistics condition = new AttnStatistics();
			condition.setEmployId(emp.getId());
			condition.setStartTime(DateUtils.getFirstDay(DateUtils.addMonth(new Date(), -1)));
			condition.setEndTime(DateUtils.getLastDay(DateUtils.addMonth(new Date(), -1)));
			
			try {
				AttnStatistics attnStatistics = getTotalAttStatistics(condition);
				
				logger.warn("员工{}上月应出勤工时为{},实际出勤工时为" + attnStatistics.getAllAttnTime(),emp.getCnName(),attnStatistics.getMustAttnTime());
				if(attnStatistics.getAllAttnTime() >= attnStatistics.getMustAttnTime()){
					return;
				}
			} catch (OaException e) {

			}
		}
		
		//生成异常考勤提醒
		EmpMsg empMsg = new EmpMsg();
		empMsg.setDelFlag(CommonPo.STATUS_NORMAL);
		empMsg.setType(EmpMsg.type_200);
		empMsg.setCompanyId(emp.getCompanyId());
		empMsg.setEmployeeId(emp.getId());
		empMsg.setTitle(DateUtils.format(DateUtils.addMonth(lastDate, -1),"yyyy年MM月")+"异常考勤提醒");
		empMsg.setContent("您上月累计异常考勤"+as.getExTimes()+"次,请于"+DateUtils.format(lastDate,"MM月dd日")+"前,在MO系统H5端及时完成异常考勤、补假申请和审批流程,逾时将无法申报!");
		empMsg.setReadFlag(EmpMsg.READ_FLAG_NO);
		empMsg.setCreateTime(new Date());
		empMsgService.save(empMsg);
		
		logger.info("[step03]员工{}生成异常考勤消息提醒",emp.getCnName());
		
		//发送异常考勤邮件
		String content = "Dear " + emp.getCnName() +":<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;您上月累计异常考勤"+as.getExTimes()+"次,请于"+DateUtils.format(lastDate,"MM月dd日")+"前,在MO系统H5端及时完成异常考勤、补假申请和审批流程,逾时将无法申报!";
		try {
			SendMailUtil.sendNormalMail(content, emp.getEmail(), emp.getCnName(), DateUtils.format(DateUtils.addMonth(lastDate, -1),"yyyy年MM月")+"异常考勤提醒");
			logger.info("[step04]员工{}生成异常考勤邮件提醒",emp.getCnName());
		} catch (Exception e) {
		}
	}

	@Override
	public AttnStatistics getAttnStatistics(Long employId, Date attnDate) {
		return attnStatisticsMapper.getAttnStatistics(employId, attnDate);
	}

	@Override
	public List<AttnStatistics> getActAttnTimeGroupByemployIds(
			List<Long> employIdList, Date startDate, Date endDate) {
		return attnStatisticsMapper.getActAttnTimeGroupByemployIds(employIdList, startDate, endDate);
	}
	
	/**
	 * 消下属缺勤重新计算考勤
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void recalculationAttForRemoveSubAbsence(AttnWorkHours condition) {
		// 保存单据数据，并重新计算该员工考勤
		Long employeeId = condition.getEmployeeId();
		Long companyId = condition.getCompanyId();
  		Date formDate  = condition.getWorkDate();//考勤时间
 		Date yesterday = DateUtils.addDay(DateUtils.getToday(),-1);
		attnWorkHoursService.save(condition);
		if(formDate.after(yesterday)){//如果提交的单据是昨天以后的，本次不做计算，会有定时汇总计算
			logger.info("该单据在定时计算前提交，本次不做计算，会有定时汇总计算。");
		}else{
			/**立即计算考勤**/
			calculateAttnForm(companyId,employeeId,formDate,yesterday);
		}
		
	}
}

