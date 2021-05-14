package com.ule.oa.base.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ule.oa.base.mapper.AttnWorkHoursMapper;
import com.ule.oa.base.po.AttnTaskRecord;
import com.ule.oa.base.po.AttnWorkHours;
import com.ule.oa.base.po.Company;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.service.AnnualVacationService;
import com.ule.oa.base.service.AttnTaskRecordService;
import com.ule.oa.base.service.AttnWorkHoursService;
import com.ule.oa.base.service.CompanyConfigService;
import com.ule.oa.base.service.CompanyService;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.EmployeeClassService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.common.spring.SpringContextUtils;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.MsgUtils;

@Service
public class AttnWorkHoursServiceImpl implements AttnWorkHoursService{
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private EmployeeService employeeService;
	
	@Resource
	private CompanyConfigService companyConfigService;
	
	@Resource
	private ConfigService configService;
	
	@Resource
	private EmployeeClassService employeeClassService;

	@Resource
	private AttnWorkHoursMapper attnWorkHoursMapper;
	
	@Resource
	private AnnualVacationService annualVacationService;
	
	@Resource
	private CompanyService companyService;
	
	@Resource
	private AttnTaskRecordService attnTaskRecordService;
	
	private Date CURRENT_TIME = null;
	private final Integer COMPLETE_STATUS = 0;//已完成
	private final Integer NOT_COMPLETE_STATUS = 1;//未完成
	private final String CREATE_USER = "oaService";//考勤数据创建人
    private final String SIX = " 06:30:00";//6点整
    private final String COMPREHENSIVE="comprehensive";//综合工时
    private final String STANDARD = "standard";//标准工时
    private final String SCHEDULING_YES = "yes";//排班
    //private final String SCHEDULING_NO = "no";//不排班
    private final String NIGHT ="  09:00:00";//9点
    private final String EIGHTEEN = " 18:00:00";//18点
    private final Double FULL_WORK_HOURS = 9.0;/**正常工时：8小时,吃饭1小时**/
    private final Integer PAGE_SIZE = 200;//分页处理每页数据条数
    private final Calendar ca = Calendar.getInstance();//通用日历
	private final Integer END_CLASS_HOUR = 18;//下班小时
	
	@Override
	public void setWorkHours(AttnWorkHours condition) {
		
		final Date kafkaStartTime = condition.getStartTime();
		final Date kafkaEndTime = condition.getEndTime();
		final List<Long> ids = condition.getEmployeeIds();
		
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
		attnTaskRecordCondition.setSetWorkHoursStatus(NOT_COMPLETE_STATUS);//查询所有未完成的
		attnTaskRecordCondition.setCompanyId(uleId);
    	attnTaskRecordCondition.setOffset(0);//注意，因为在list循环内部对数据进行了修改，这里下标不能变动
		attnTaskRecordCondition.setLimit(PAGE_SIZE);
		attnTaskRecordCondition.setStartTime(kafkaStartTime);//计算范围就是传过来的范围
		attnTaskRecordCondition.setEndTime(kafkaEndTime);//计算范围就是传过来的范围
		
		attnTaskRecordCondition.setIds(ids);
		List<Employee> taskEmpList = new ArrayList<Employee>();//查询未完成考勤明细统计的员工
		
		//存放需要更新状态的结果
		AttnTaskRecord attnTaskRecordResult = null;
		
		//优化，存放节假日map
		Map<Date, Boolean> workDayMap = new HashMap<Date, Boolean>();
		
        //2.参数准备
		String workTypeDisplayCode,schedulingDisplayCode;//工时类型displayCode,是否排班displayCode
		Map<Long,AttnWorkHours> attnWorkHoursMap = null;
		AttnWorkHours attnWorkHours = null;//出勤工时实体
		Map<Long,EmployeeClass> employeeClassMap = null;
		EmployeeClass employeeClass = null;//排班实体
		final AttnWorkHours hoursCondition = new AttnWorkHours();//查询考勤记录条件，表attn_sign_record
		final EmployeeClass classCondition = new EmployeeClass();//查询班次条件
		
		String countDateStr = null,startDateStr,endDateStr ;//转换日期用的字符串
		Date empAttnDate = null,startTime = null ,endTime = null ;//条件开始，结束时间
		
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
	    			hoursCondition.setEmployeeIds(employeesIds);//批量查询
					classCondition.setEmployeeIds(employeesIds);
	        		
					Boolean delFlag = false;/**记录这条数据是否需要被删除，调班后可能已经计算到的旷工需要删除**/
		        	for(Employee employee:taskEmpList){//这个循环是进行考勤数据计算
		        		delFlag = false;
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

			    			hoursCondition.setStartTime(startTime);
			    			hoursCondition.setEndTime(endTime);

			    			classCondition.setCompanyId(uleId);//只查询邮乐员工
							classCondition.setClassDate(empAttnDate);
		        		}else{//不用重新计算
		        			
		        		}
		    	        
		        		if(reSelect){
		        			reSelect = false;
			    	        //3.准备清洗员工打卡数据,返回打卡数据对象map
			    			attnWorkHoursMap = getTransMapByDate(hoursCondition);
			    			
			    			//4.准备排班数据
							employeeClassMap = employeeClassService.getEmployeeClassMap(classCondition);
		        		}
		    			
		    			if(!workDayMap.containsKey(empAttnDate)){//首次查询这一天，放入map
			    			isWorkDay = annualVacationService.judgeWorkOrNot(empAttnDate);//是否不上班true:工作;false:休息。
			    			workDayMap.put(empAttnDate, isWorkDay);
		    			}else{                                   //不是首次，直接获取
		    				isWorkDay = workDayMap.get(empAttnDate);
		    			}
		    			
		    			attnWorkHours = attnWorkHoursMap.get(employeeId);
	                    //4.根据不同工时类型，进行考勤统计

		    			if(null == workTypeDisplayCode){
    						logger.error("员工 id: {},姓名:{} 没有定义工时类型，不做计算。",employeeId,employeeName);
		    			}else{
		    				if(COMPREHENSIVE.equals(workTypeDisplayCode)){//综合工时，没有排班这一说
		    				
		    				if(!isWorkDay){//若今天不上班,有考勤数据全算   异常考勤;无考勤数据，不做任何操作
		    					if(null != attnWorkHours){
		    						attnWorkHours = buildComprehensive(attnWorkHours,null);
		    						attnWorkHours.setAttnStatus(NOT_COMPLETE_STATUS);//一定是异常
		    					}
		    				}else{ //若今天上班，没有找到考勤数据，记录异常；若找到考勤，则正常记录
		    					employeeClass = employeeClassMap.get(employeeId);
		    					if(null != attnWorkHours){
		    						attnWorkHours = buildComprehensive(attnWorkHours,employeeClass);
		    					}else{
		    						attnWorkHours = new AttnWorkHours();
		    						attnWorkHours.setEmployeeId(employeeId);
		    						attnWorkHours.setCompanyId(uleId);
		    						attnWorkHours.setWorkDate(empAttnDate);//统计的考勤时间是昨天
		    						attnWorkHours = buildComprehensive(attnWorkHours,employeeClass);
		    					}
		    				}
		    				
		    			}else if(STANDARD.equals(workTypeDisplayCode)){//标准工时,排班工时
							
		    				Double classHours = 0.0;
		    				
							if(null == schedulingDisplayCode){
	    						logger.error("员工 id: {},姓名:{} 没有定义是否排班，不做计算。",employeeId,employeeName);
	    						attnWorkHours = null;
							}else{
								if(schedulingDisplayCode.equals(SCHEDULING_YES)){//如果需要排班,则无节假日
							
		    					    //员工考勤数据
		    						employeeClass = employeeClassMap.get(employeeId);
		    						
		    						if(null == employeeClass){//找不到排班数据....记录打卡时间，但是全部异常
		    							if(null != attnWorkHours){
		    								classHours = 0.0;
		    								attnWorkHours = buildStandard(attnWorkHours,null);
		    							}else{/**因为可能调班后，重新计算考勤；可能有些已存在的考勤需要删除（原来旷工那天调成休息）**/
		    								/**需要删除的**/
		    								delFlag = true;
		    							}
		    							logger.error("员工 id: {},姓名:{} 没有定义排班时间",employeeId,employeeName);
		    						}else{//找到了排班数据....记录打卡时间，未打卡，记录异常
		    							Date classStartTime = concatDateTime(employeeClass.getClassDate(), employeeClass.getStartTime());
		    							Date classEndTime = concatDateTime(employeeClass.getClassDate(), employeeClass.getEndTime());
	    								if(1 == employeeClass.getIsInterDay().intValue()){
	    									classEndTime = DateUtils.addDay(classEndTime, 1);
	    								}
		    							 classHours = DateUtils.getIntervalMinutes(classEndTime, classStartTime)/60.0;
		    							 
		    							 if(null != attnWorkHours){
		    								attnWorkHours = buildStandard(attnWorkHours,classEndTime);
		    							}else{
		    								attnWorkHours = new AttnWorkHours();
		    								attnWorkHours.setEmployeeId(employeeId);
		    								attnWorkHours.setCompanyId(employee.getCompanyId());
		    								attnWorkHours.setWorkDate(empAttnDate);
		    								attnWorkHours = buildStandard(attnWorkHours,null);
		    							}
		    						}
		    					
		    				}else{
		    					//如果不需要排班,赋予默认排班时间,设置默认出勤时间9:00 -- 18：00,有节假日(老逻辑按9:00-18:00)
		    					/**
		    					 * 新逻辑-每月初始化一份9-18点的班次到排班表，和排班一个逻辑计算
		    					 */
		    					employeeClass = employeeClassMap.get(employeeId);
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
		    					
		    					classHours = DateUtils.getIntervalMinutes(startTime, endTime)/60.0;
		    					attnWorkHours = readyStandard(attnWorkHours,employee,isWorkDay,empAttnDate,endTime);
		    					
		    				}
		    				if(null != attnWorkHours){
		    					
		    					logger.info("ID："+employeeId+",日期"+countDateStr+"计算得到出勤时间:{},排班时间:{}",attnWorkHours.getWorkHours(),classHours);
			    				if(attnWorkHours.getWorkHours().compareTo(classHours)>=0 && attnWorkHours.getWorkHours()>0 && classHours>0){
			    					attnWorkHours.setAttnStatus(0);
			    				}else{
			    					attnWorkHours.setAttnStatus(1);
			    				}
		    				}
						}
		    		}
			    		  if(null != attnWorkHours){
			    			  
			    			  AttnWorkHoursService attnWorkHoursService = SpringContextUtils.getContext().getBean(AttnWorkHoursService.class);
				    			 
			    			  //添加判段，如果已经存在当天的考勤，那么更新
			    			  List<Long> existId = null;
			    			  existId = attnWorkHoursMapper.getExistIdWorkHours(attnWorkHours);
			    			  if(null == existId|| existId.size()<=0){
			    				  if(attnWorkHours.getDelFlag().intValue() == 0){//有效的才保存
				    				  attnWorkHoursService.save(attnWorkHours);//每一条数据是一个事务
			    				  }
			    			  }else{
			    				  for(Long existId1:existId){
			    					  if(delFlag){
				    					  attnWorkHours.setDelFlag(NOT_COMPLETE_STATUS);  
				    				  }
			    					  attnWorkHours.setCreateTime(null);
				    				  attnWorkHours.setId(existId1);
				    				  attnWorkHoursService.updateById(attnWorkHours);
			    				  }
			    			  }
			    		  }
		    			}
		    			CURRENT_TIME = DateUtils.parse(DateUtils.getNow(),DateUtils.FORMAT_LONG);
		    			attnTaskRecordResult = new AttnTaskRecord();
		    			attnTaskRecordResult.setSetWorkHoursStatus(COMPLETE_STATUS);//状态设置为完成
		    			attnTaskRecordResult.setId(attnTaskId);
	    			    attnTaskRecordResult.setUpdateTime(CURRENT_TIME);
	    			    attnTaskRecordService.updateById(attnTaskRecordResult);//更新任务状态表
		        	}
	        	}
				System.gc();//垃圾回收
	        	i++;
	        }
	        try {
				String beginDate = null,endDate = null;
				if(null != kafkaStartTime){
					beginDate=DateUtils.format(kafkaStartTime, DateUtils.FORMAT_SHORT);
				}
				if(null != kafkaEndTime){
					endDate=DateUtils.format(kafkaEndTime, DateUtils.FORMAT_SHORT);
				}
				HashMap<String, Object> map = new HashMap<String, Object>();
				// kafka消息头，必须有时间范围，否则后续会重复消费数据
				map.put("beginDate", beginDate);// 可为null
				map.put("endDate", endDate);
				map.put("ids", ids);
				MsgUtils.asynSendKafkaMsgByMap(map, "com.ule.oa.service.attnWorkToAttnStatisProducer");
			} catch (Exception e) {
				logger.error("发送kafka失败  com.ule.oa.service.attnWorkToAttnStatisProducer",e);
			}
		}catch (Exception e){
			logger.error("考勤明细汇总出错,不会触发后续kafka发送:",e);
		}
	}
	
	//综合工时考勤开始时间为9：00！ 
	private AttnWorkHours buildComprehensive(AttnWorkHours attnWorkHours,EmployeeClass employeeClass){
		
		ca.setTime(attnWorkHours.getWorkDate());
		ca.set(Calendar.HOUR_OF_DAY, END_CLASS_HOUR);
		if(employeeClass!=null){
			ca.set(Calendar.HOUR_OF_DAY, Integer.valueOf(DateUtils.format(employeeClass.getEndTime(), "HH")));
			ca.set(Calendar.MINUTE, Integer.valueOf(DateUtils.format(employeeClass.getEndTime(), "mm")));
		}
		Date endClassTime = ca.getTime();
		
		ca.set(Calendar.HOUR_OF_DAY, 9);
		if(employeeClass!=null){
			ca.set(Calendar.HOUR_OF_DAY, Integer.valueOf(DateUtils.format(employeeClass.getStartTime(), "HH")));
			ca.set(Calendar.MINUTE, Integer.valueOf(DateUtils.format(employeeClass.getStartTime(), "mm")));
		}
		Date startClassTime = ca.getTime();
		
		CURRENT_TIME = DateUtils.parse(DateUtils.getNow(),DateUtils.FORMAT_LONG);
		attnWorkHours.setDataType(attnWorkHours.getDataType()==null?0:attnWorkHours.getDataType());
		attnWorkHours.setDelFlag(0);
		attnWorkHours.setCreateUser(CREATE_USER);
		attnWorkHours.setUpdateUser(CREATE_USER);
		attnWorkHours.setCreateTime(CURRENT_TIME);
		attnWorkHours.setUpdateTime(CURRENT_TIME);
		
		if(null == attnWorkHours.getStartTime() && null == attnWorkHours.getEndTime()){//数据为空，旷工
			
			attnWorkHours.setAttnStatus(1);
			attnWorkHours.setWorkHours(0.0);
		}else if(attnWorkHours.getStartTime().equals(attnWorkHours.getEndTime())){//最早最晚打卡时间相同，只打一次卡，旷工
			
			attnWorkHours.setAttnStatus(1);
			attnWorkHours.setWorkHours(0.0);
			
			ca.setTime(attnWorkHours.getStartTime());
			if(attnWorkHours.getEndTime().before(endClassTime)){//查出来的开始时间和结束时间一样，根据下班时间来判断，是上班打卡还是下班打卡
				attnWorkHours.setEndTime(null);
			}else{
				attnWorkHours.setStartTime(null);
			}
			
		}else{//最早最晚打卡时间不相同，打了两次卡，注意:最早考勤时间为：9:00！
			
			Integer status = 0;
			Date startTime = attnWorkHours.getStartTime();
			Long minutes = 0L;
			Double hours = 0.0;
			Calendar cal = Calendar.getInstance();
			
			cal.setTime(startTime);
			Integer hour = cal.get(Calendar.HOUR_OF_DAY);//得到是几点打卡的
			int startHours = 9;
			if(employeeClass!=null){
				if(startTime.getTime()<startClassTime.getTime()){
					startTime = startClassTime;
				}
			}else{
				if(hour < startHours){//9点以前打卡的，算9点
					cal.set(Calendar.HOUR, startHours);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND,0);
				}
				startTime = cal.getTime();
			}
			
			
			//设置时间9:00后需要重新判断大小
			minutes = getIntervalMinutes(attnWorkHours.getEndTime(),startTime);//考勤的分钟数
			if(minutes > 0){
				
				//hours = getActualAllAttnTime((int)(minutes/30)*0.5);/**转换成小时数,这里不满5减一，满10减2，最后计算出勤才减**/
				int halfHoursCount = (int)(minutes/30);
				hours = halfHoursCount*0.5;
			}
			
			if(hours < FULL_WORK_HOURS){
				status = 1;
			}
			
			attnWorkHours.setAttnStatus(status);
			attnWorkHours.setWorkHours(hours);
		}
		return attnWorkHours;
	}
	
	//准备标准工时的数据
	private AttnWorkHours readyStandard(AttnWorkHours attnWorkHours,Employee employee,Boolean isWorkDay,Date countDate,Date endClassTime){
		
		if(!isWorkDay){//是否节假日,是：有打卡则记录异常，否：正常记录打卡未打卡数据
			
			if(null != attnWorkHours){
				attnWorkHours = buildStandard(attnWorkHours,null);
			}
		}else{
			if(null != attnWorkHours){
				attnWorkHours = buildStandard(attnWorkHours,endClassTime);
			}else{
				attnWorkHours = new AttnWorkHours();
				attnWorkHours.setEmployeeId(employee.getId());
				attnWorkHours.setCompanyId(employee.getCompanyId());
				attnWorkHours.setWorkDate(countDate);//统计的考勤时间是昨天
				attnWorkHours = buildStandard(attnWorkHours,endClassTime);
			}
		}
		
		return attnWorkHours;
	}
	
	//封装标准工时数据
	private AttnWorkHours buildStandard(AttnWorkHours attnWorkHours, Date endClassTime){
		
		if(null == endClassTime){
			ca.setTime(attnWorkHours.getWorkDate());
			ca.set(Calendar.HOUR_OF_DAY, END_CLASS_HOUR);
		}else{
			ca.setTime(endClassTime);
		}
		endClassTime = ca.getTime();
		
		CURRENT_TIME = DateUtils.parse(DateUtils.getNow(),DateUtils.FORMAT_LONG);
		attnWorkHours.setDataType(attnWorkHours.getDataType()==null?0:attnWorkHours.getDataType());
		attnWorkHours.setDelFlag(0);
		attnWorkHours.setCreateUser(CREATE_USER);
		attnWorkHours.setUpdateUser(CREATE_USER);
		attnWorkHours.setCreateTime(CURRENT_TIME);
		attnWorkHours.setUpdateTime(CURRENT_TIME);
		
		if(null == attnWorkHours.getStartTime() && null == attnWorkHours.getEndTime()){//数据为空，旷工
			
			attnWorkHours.setWorkHours(0.0);
		}else if(attnWorkHours.getStartTime().equals(attnWorkHours.getEndTime())){//最早最晚打卡时间相同，只打一次卡，旷工
			
			attnWorkHours.setWorkHours(0.0);
			
			if(attnWorkHours.getEndTime().before(endClassTime)){//查出来的开始时间和结束时间一样，根据下班时间来判断，是上班打卡还是下班打卡
				attnWorkHours.setEndTime(null);
			}else{
				attnWorkHours.setStartTime(null);
			}
			
		}else{//最早最晚打卡时间不相同，打了两次卡
			
			//计算出勤工时
			Long minutes = DateUtils.getIntervalMinutes(attnWorkHours.getStartTime(), attnWorkHours.getEndTime());//不足30分钟不算考勤时间
			int halfHoursCount = (int) (minutes/30);
			
			Double hours = halfHoursCount*0.5;//转换成小时数
			
			attnWorkHours.setWorkHours(hours);
		}
		return attnWorkHours;
	}

	@Override
	public void save(AttnWorkHours attnWorkHours) {
		
		attnWorkHoursMapper.save(attnWorkHours);
	}

	@Override
	@Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRES_NEW)
	public void updateById(AttnWorkHours attnWorkHours) {
		
		attnWorkHoursMapper.updateById(attnWorkHours);
	}

	@Override
	@Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRES_NEW)
	public Integer saveBatch(List<AttnWorkHours> attnWorkHoursList) {
		return attnWorkHoursMapper.saveBatch(attnWorkHoursList);
	}

	@Override
	public Map<Long, AttnWorkHours> getTransMapByDate(AttnWorkHours condition) {
		List<AttnWorkHours> attnWorkHoursList = attnWorkHoursMapper.getTransListByDate(condition);
		
		Map<Long,AttnWorkHours> mappedAttnWorkHours = attnWorkHoursList.stream().collect(Collectors.toMap(AttnWorkHours::getEmployeeId, Function.identity(), (key1, key2) -> key2));
		return mappedAttnWorkHours;
	}

	@Override
	public AttnWorkHours getAttnWorkHoursGroupByDate(AttnWorkHours hoursCondition) {
		return attnWorkHoursMapper.getAttnWorkHoursGroupByDate(hoursCondition);
	}

	@Override
	public List<AttnWorkHours> getAttnWorkHoursList(AttnWorkHours attnWorkHours) {
		return attnWorkHoursMapper.getAttnWorkHoursList(attnWorkHours);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void cancelAttnWorkHours(AttnWorkHours condition) {
		attnWorkHoursMapper.cancelAttnWorkHours(condition);
	}
	
	private Long getIntervalMinutes(Date date, Date otherDate) {
		long time = date.getTime() - otherDate.getTime();
		return (long) time == 0 ? 0 : time/(60 * 1000);
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

	@Override
	public List<AttnWorkHours> getAbsoluteAttnWorkHoursList(
			AttnWorkHours attnWorkHours) {
		return attnWorkHoursMapper.getAbsoluteAttnWorkHoursList(attnWorkHours);
	}

	@Override
	public List<AttnWorkHours> getListByCondition(AttnWorkHours attnWorkHours) {
		return attnWorkHoursMapper.getListByCondition(attnWorkHours);
	}
}

