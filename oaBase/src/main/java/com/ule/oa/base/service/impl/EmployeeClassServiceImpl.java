package com.ule.oa.base.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.ule.oa.base.mapper.AnnualVacationMapper;
import com.ule.oa.base.mapper.ApplicationEmployeeClassMapper;
import com.ule.oa.base.mapper.EmployeeClassMapper;
import com.ule.oa.base.mapper.ScheduleGroupMapper;
import com.ule.oa.base.po.AnnualVacation;
import com.ule.oa.base.po.ApplicationEmployeeClass;
import com.ule.oa.base.po.CompanyConfig;
import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.EmpMsg;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.po.Position;
import com.ule.oa.base.po.ScheduleGroup;
import com.ule.oa.base.service.AnnualVacationService;
import com.ule.oa.base.service.ApplicationEmployeeClassService;
import com.ule.oa.base.service.ClassSetPersonService;
import com.ule.oa.base.service.CompanyConfigService;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.EmpMsgService;
import com.ule.oa.base.service.EmployeeClassService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.PositionService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.util.SendMailUtil;
import com.ule.oa.common.po.CommonPo;
import com.ule.oa.common.utils.BeanUtils;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.ExcelUtil;
import com.ule.oa.common.utils.PageModel;
import com.ule.oa.common.utils.json.JSONUtils;


@Service
public class EmployeeClassServiceImpl implements EmployeeClassService{
	protected final Logger logger = LoggerFactory.getLogger(EmployeeClassServiceImpl.class);
	
	@Resource
	private EmployeeClassMapper employeeClassMapper;
	@Resource
	private AnnualVacationService annualVacationService;
	@Resource
	private CompanyConfigService companyConfigService;
	@Resource
	private EmployeeService employeeService;
	@Resource
	private ConfigService configService;
	@Resource
	private AnnualVacationMapper annualVacationMapper;
	@Autowired
	private UserService userService;
	@Resource
	private ClassSetPersonService classSetPersonService;
	@Resource
	private  ApplicationEmployeeClassService applicationEmployeeClassService;
	@Autowired
	private EmpMsgService empMsgService;
	@Autowired
	private PositionService positionService;
	@Resource
	private ApplicationEmployeeClassMapper applicationEmployeeClassMapper;
	@Autowired
	private ScheduleGroupMapper scheduleGroupMapper;

	@Override
	public Map<String,Object> getEmployeeClassSetting(Employee employee,List<String> datas) {
        
        List<AnnualVacation> list = annualVacationMapper.getList();
        Map<String,AnnualVacation> map = new HashMap<String, AnnualVacation>();
        for (AnnualVacation annualVacation : list) {
        	map.put(DateUtils.format(annualVacation.getAnnualDate(), DateUtils.FORMAT_SHORT), annualVacation);	
		}
        Map<String,Object> returnMap = new HashMap<String, Object>();
        String beginData = "";
        String endData ="";
        for (String da : datas) {
        	EmployeeClass result = null;
        	//2.准备员工信息
        	Boolean isWorkDay = annualVacationService.judgeWorkOrNot(map,da);//是否不上班;true:工作;false:休息。
        	
			EmployeeClass employeeClass = new EmployeeClass();
			employeeClass.setEmployId(employee.getId());
			employeeClass.setClassDate(DateUtils.parse(da, DateUtils.FORMAT_SHORT));
			result = employeeClassMapper.getEmployeeClassSetting(employeeClass);
			if(null != result){
				Date startClassTime,endClassTime;  
				startClassTime = concatDateTime(result.getClassDate(), result.getStartTime());
				endClassTime = concatDateTime(result.getClassDate(), result.getEndTime());
				if(1 == result.getIsInterDay().intValue()){
					endClassTime = DateUtils.addDay(endClassTime, 1);
				}
				result.setStartTime(startClassTime);
				result.setEndTime(endClassTime);
			}else{
				//排班员工查看这个月有没有排班，没有的话默认标准班次
				EmployeeClass isScheduleParam = new EmployeeClass();
				isScheduleParam.setEmployId(employee.getId());
				isScheduleParam.setStartTime(DateUtils.getFirstDay(employeeClass.getClassDate()));
				isScheduleParam.setEndTime(DateUtils.getLastDay(employeeClass.getClassDate()));
				List<EmployeeClass> isScheduleList = employeeClassMapper.getEmployeeClassList(isScheduleParam);
				if((isScheduleList==null||isScheduleList.size()<=0)&&isWorkDay){
					result = new EmployeeClass();
					result.setClassDate(employeeClass.getClassDate());
					result.setStartTime(DateUtils.parse("09:00:00", DateUtils.FORMAT_HH_MM_SS));
					result.setEndTime(DateUtils.parse("18:00:00", DateUtils.FORMAT_HH_MM_SS));
					Date startClassTime,endClassTime;  
    				startClassTime = concatDateTime(result.getClassDate(), result.getStartTime());
    				endClassTime = concatDateTime(result.getClassDate(), result.getEndTime());
    				result.setEmployId(employee.getId());
					result.setIsInterDay(0);
					result.setMustAttnTime(8d);
					result.setName("白");
    				if(1 == result.getIsInterDay().intValue()){
    					endClassTime = DateUtils.addDay(endClassTime, 1);
    				}
    				result.setStartTime(startClassTime);
    				result.setEndTime(endClassTime);
				}
			}		
        	if(result != null && beginData.equals("")) {
        		beginData = da;
        	}
        	if(result != null) {
        		endData = da;
        	}
        	returnMap.put(da, result);
		}
        
        returnMap.put("beginData", beginData);
        returnMap.put("endData", endData);
		return returnMap;
	}
	
	
	@Override
	public EmployeeClass getEmployeeClassSetting(EmployeeClass employeeClass) {
        
		Long employeeId = employeeClass.getEmployId();//员工Id
		
		EmployeeClass result = null;
		
        //2.准备员工信息
        Employee employee = employeeService.getById(employeeId);
		
		Boolean isWorkDay = annualVacationService.judgeWorkOrNot(employeeClass.getClassDate());//是否不上班;true:工作;false:休息。
		
		employeeId = employee.getId();
        	
		result = employeeClassMapper.getEmployeeClassSetting(employeeClass);
		
			if(result==null){
				//排班员工查看这个月有没有排班，没有的话默认标准班次
				EmployeeClass isScheduleParam = new EmployeeClass();
				isScheduleParam.setEmployId(employeeId);
				isScheduleParam.setStartTime(DateUtils.getFirstDay(employeeClass.getClassDate()));
				isScheduleParam.setEndTime(DateUtils.getLastDay(employeeClass.getClassDate()));
				List<EmployeeClass> isScheduleList = employeeClassMapper.getEmployeeClassList(isScheduleParam);
				if((isScheduleList==null||isScheduleList.size()<=0)&&isWorkDay){
					result = new EmployeeClass();
					result.setClassDate(employeeClass.getClassDate());
					result.setStartTime(DateUtils.parse("09:00:00", DateUtils.FORMAT_HH_MM_SS));
					result.setEndTime(DateUtils.parse("18:00:00", DateUtils.FORMAT_HH_MM_SS));
					result.setEmployId(employeeId);
					result.setIsInterDay(0);
					result.setMustAttnTime(8d);
					result.setName("白");
			}
			appendClassTime(result,isWorkDay);
			
        }else{
        	appendClassTime(result,isWorkDay);
        }

		logger.info("员工：{}，排班结果：{}",employeeId,JSONUtils.write(result));
		return result;
	}
	
	public EmployeeClass getEmployeeClassByCondition(EmployeeClass condition){
		List<EmployeeClass> classList = employeeClassMapper.getEmployeeClassList(condition);
		if(null != classList && classList.size() > 0){
			return classList.get(0);
		}
		
		return null;
	}

	@Override
	public Map<Long, EmployeeClass> getEmployeeClassMap(EmployeeClass condition) {

		List<EmployeeClass> classList = employeeClassMapper.getEmployeeClassList(condition);
		
		Map<Long,EmployeeClass> mappedEmployeeClass = Maps.uniqueIndex(classList, new Function <EmployeeClass,Long> () {  
	          public Long apply(EmployeeClass from) {  
	            return from.getEmployId() == null?-1L:from.getEmployId();   
	    }});
		
		//return classList.stream().collect(Collectors.toMap(EmployeeClass::getEmployId, employeeClass -> employeeClass));
		return mappedEmployeeClass;
	}

	@Override
	public EmployeeClass getEmployeeClass(EmployeeClass condition) {

		EmployeeClass employeeClass = employeeClassMapper.getEmployeeClassSetting(condition);
		return employeeClass;
	}

	@Override
	public EmployeeClass getEmployeeClassHours(EmployeeClass condition) {
		return employeeClassMapper.getEmployeeClassHours(condition);
	}

	@Override
	public List<EmployeeClass> getEmployeeClassPeriodList(EmployeeClass employeeClass) {
		
		List<EmployeeClass> result = new ArrayList<EmployeeClass>();
		
		//本月总天数
		Calendar aCalendar = Calendar.getInstance();
		aCalendar.setTime(employeeClass.getStartTime());//只要是当月的日期就行
		Double monthTotal = aCalendar.getActualMaximum(Calendar.DATE) * 1.0;//当月总天数，用来计算综合工时和默认排班工时应出勤时间
		
		Long employeeId = employeeClass.getEmployId();//员工Id
		Long workTypeId;//工时类型ID
		Long whetherScheduling;//是否排班
		String workTypeDisplayCode,schedulingDisplayCode;//工时类型displayCode,是否排班displayCode
		
		//1.准备工时类型map，key:配置ID，value：配置编码
		CompanyConfig companyConfigConditon = new CompanyConfig();//查询配置表条件
		companyConfigConditon.setCode("typeOfWork");//工时类型
		List<CompanyConfig> workTypeList = companyConfigService.getListByCondition(companyConfigConditon);
		
		Config configCondition = new Config();
		configCondition.setCode("whetherScheduling");//是否排班
		List<Config> whetherSchedulingList = configService.getListByCondition(configCondition);
		
        Map<Long,String> workTypeMap = new HashMap<Long,String>();//存放配置的map
        Map<Long,String> schedulingMap = new HashMap<Long,String>();//存放配置的map
        
        for(CompanyConfig cConfig:workTypeList){
        	workTypeMap.put(cConfig.getId(), cConfig.getDisplayCode());
        }
        for(Config config:whetherSchedulingList){
        	schedulingMap.put(config.getId(), config.getDisplayCode());
        }
		
        //2.准备员工信息
        Employee employee = employeeService.getById(employeeId);
		
		Boolean isWorkDay = true;//是否不上班;true:工作;false:休息。
		
		employeeId = employee.getId();
		workTypeId = employee.getWorkType();
		whetherScheduling = employee.getWhetherScheduling();
    	workTypeDisplayCode = workTypeMap.get(workTypeId);
    	schedulingDisplayCode = schedulingMap.get(whetherScheduling);
    	
    	//准备本月，节假日信息
    	String month = DateUtils.format(employeeClass.getStartTime(), "yyyy-mm");
    	Map<String,Boolean> isWorkMap =  annualVacationService.judgeWorkOrNot(month);
    	
        if("comprehensive".equals(workTypeDisplayCode)){//综合工时，没有排班这一说，默认9:00--18:00
			  
            result = buildDefaultClassList(employeeClass.getStartTime(),monthTotal,isWorkMap);
        }else if("standard".equals(workTypeDisplayCode)){//标准工时
			
			if("yes".equals(schedulingDisplayCode)){//如果需要排班,则无节假日,排了就上班，没排就不上班

                //有数据返回数据，没有就直接返回null
	        	result = employeeClassMapper.getEmployeeClassList(employeeClass);
			}else{//不需要排班，给默认时间，有节假日
				if(isWorkDay){//工作日，默认9:00--18:00
		            result = buildDefaultClassList(employeeClass.getStartTime(),monthTotal,isWorkMap);
				}
			}
        	
        }
		
		return result;
	}

	private void appendClassTime(EmployeeClass result,Boolean isWorkDay){
    	if(result == null) {
    		return;
    	}
//    	if(isWorkDay){//工作日返回      FORMAT_HH_MM
			String startDateStr = DateUtils.format(result.getClassDate(), DateUtils.FORMAT_SHORT)+" "+DateUtils.format(result.getStartTime(), DateUtils.FORMAT_HH_MM_SS);
			String endDateStr = DateUtils.format(result.getClassDate(), DateUtils.FORMAT_SHORT)+" "+DateUtils.format(result.getEndTime(), DateUtils.FORMAT_HH_MM_SS);
			
			Date classStartTime = DateUtils.parse(startDateStr, DateUtils.FORMAT_LONG);
			Date classEndTime = DateUtils.parse(endDateStr, DateUtils.FORMAT_LONG);
			if(result.getIsInterDay()==1) {
				classEndTime = DateUtils.addDay(classEndTime, 1);
			}
			result.setStartTime(classStartTime);
			result.setEndTime(classEndTime);
//    	}
	}
	
	private EmployeeClass buildDefaultClassSingle(Date date,Boolean isWorkDay){
    	
		EmployeeClass single = null;
    	if(isWorkDay){//工作日返回
			String classDateStr = DateUtils.format(date, DateUtils.FORMAT_SHORT);
			String startDateStr = classDateStr+" 09:00:00";
			String endDateStr = classDateStr+" 18:00:00";
			
			Date classStartTime = DateUtils.parse(startDateStr, DateUtils.FORMAT_LONG);
			Date classEndTime = DateUtils.parse(endDateStr, DateUtils.FORMAT_LONG);
			
			single = new EmployeeClass();
			single.setClassDate(date);
			single.setStartTime(classStartTime);
			single.setEndTime(classEndTime);
			single.setMustAttnTime(8d);
			single.setName("白班");
			single.setIsInterDay(0);
    	}
		return single;
		
	}
	
	private List<EmployeeClass> buildDefaultClassList(Date date,Double monthTotal,Map<String,Boolean> isWorkMap) {
		
		List<EmployeeClass> result = new ArrayList<EmployeeClass>();
		Boolean isWorkDay = false;
	    Calendar cal = Calendar.getInstance();  
	    cal.setTime(date);//month 为指定月份任意日期  
	    cal.set(Calendar.DAY_OF_MONTH, 1);// 从一号开始  
  
	    for (int i = 0; i < monthTotal; i++, cal.add(Calendar.DATE, 1)) {  
	        Date d = cal.getTime();  
	        isWorkDay = isWorkMap.get(DateUtils.format(d, DateUtils.FORMAT_SHORT));
			EmployeeClass single = buildDefaultClassSingle(d,isWorkDay);
			
			if(null != single){
				result.add(single);
			}
	   } 
	   System.gc();
	   return result;
	}

	/************************************************************************************************** 排班管理页面---根据部门获取员工当月和下月排班信息 start ************************************************/
	/**
	 * @throws Exception 
	  * getEmployeeClassInfoByDepartId(根据部门获取员工排班信息)
	  * @Title: getEmployeeClassInfoByDepartId
	  * @Description: 根据部门获取员工排班信息
	  * @param departId
	  * @return    设定文件
	  * List<EmployeeClass>    返回类型
	  * @throws
	 */
	@Override
	public List<EmployeeClass> getEmployeeClassInfoByDepartId(Long departId) throws Exception{
		List<EmployeeClass> empClassList = new ArrayList<EmployeeClass>();
		EmployeeClass employeeClass = null;
		
		//需要排班人数
		Integer mustClassSettingCount = getMustClassSettingCountByDepartId(departId);
		
		//当月已经排班信息
		employeeClass = new EmployeeClass();
		EmployeeClass clazz = getAlreadyClassSettingByDepartIdAndMonth(departId, true);
		if(null != clazz){
			BeanUtils.copyProperties(clazz, employeeClass);
		}
		employeeClass.setMustClassSettingCount(mustClassSettingCount);
		empClassList.add(employeeClass);
		
		//下月已经排班信息
		employeeClass = new EmployeeClass();
		clazz = getAlreadyClassSettingByDepartIdAndMonth(departId, false);
		if(null != clazz){
			BeanUtils.copyProperties(clazz, employeeClass);
		}
		employeeClass.setMustClassSettingCount(mustClassSettingCount);
		empClassList.add(employeeClass);
		
		return empClassList;
	}
	
	/**
	  * getMustClassSettingCountByDepartId(根据部门id获取需要排班人数)
	  * @Title: getMustClassSettingCountByDepartId
	  * @Description: 根据部门id获取需要排班人数
	  * @param departId
	  * @return    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	public Integer getMustClassSettingCountByDepartId(Long departId){
		return employeeClassMapper.getMustClassSettingCountByDepartId(departId);
	}
	
	/**
	 * @throws Exception 
	  * getAlreadyClassSettingByDepartIdAndMonth(根据部门id和排班时间获取已经排班人数和排班人)
	  * @Title: getAlreadyClassSettingByDepartIdAndMonth
	  * @Description: 根据部门id和排班时间获取已经排班人数和排班人
	  * @param departId
	  * @return    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	public EmployeeClass getAlreadyClassSettingByDepartIdAndMonth(Long departId,boolean currOrNext) throws Exception{
		//获取当月排班开始时间和结束时间
		Map<String,Date> schedulMap = getStartAndEndTimeByFlag(currOrNext);
		
		EmployeeClass employeeClass = new EmployeeClass();
		employeeClass.setDepartId(departId);
		employeeClass.setStartTime(schedulMap.get("startTime"));
		employeeClass.setEndTime(schedulMap.get("endTime"));
		
		return employeeClassMapper.getAlreadyClassSettingByDepartIdAndMonth(employeeClass);
	}
	
	/**
	 * @throws Exception 
	  * getStartAndEndTimeByFlag(这里用一句话描述这个方法的作用)
	  * @Title: getStartAndEndTimeByFlag
	  * @Description: 获取当月或者下月第一天和最后一天
	  * @param flag:true标识当月，false标识下月
	  * @return    设定文件
	  * Map<String,Date>    返回类型
	  * @throws
	 */
	public static Map<String,Date> getStartAndEndTimeByFlag(boolean flag) throws Exception{
		Map<String,Date> schedulMap = new HashMap<String,Date>();
		Date startTime = null;//排班开始时间
		Date endTime = null;//排班结束时间
		
		if(flag){//获取本月第一天和最后一天
			startTime = DateUtils.getFirstDay(new Date());
			endTime = DateUtils.getLastDay(new Date());
		}else{//获取下个月第一天和最后一天
			startTime = DateUtils.addMonth(DateUtils.getFirstDay(new Date()), 1);//下个月第一天 = 本月第一天加1个月
			endTime = DateUtils.getLastDay(startTime);//下个月最后一天 = 根据本月第一天+1个月-1天
		}
		
		schedulMap.put("startTime", startTime);
		schedulMap.put("endTime", endTime);
		
		return schedulMap;
	}
	
	/************************************************************************************************** 排班管理页面---根据部门获取员工当月和下月排班信息 end ************************************************/
	
	/**
	  * save(逐单保存员工排班信息)
	  * @Title: save
	  * @Description: 逐单保存员工排班信息
	  * @param list
	  * @return    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	public Integer save(EmployeeClass empClass){
		empClass.setCreateUser(userService.getCurrentAccount());
		empClass.setCreateTime(new Date());
		empClass.setUpdateUser(userService.getCurrentAccount());
		empClass.setUpdateTime(new Date());
		
		return employeeClassMapper.save(empClass);
	}
	
	/**
	  * BatchSaveEmpClass(批量保存员工排班信息)
	  * @Title: BatchSaveEmpClass
	  * @Description: 批量保存员工排班信息
	  * @param list
	  * @return    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	public Integer batchSave(List<EmployeeClass> list){
		for(EmployeeClass empClass : list){
			empClass.setCreateUser(userService.getCurrentAccount());
			empClass.setCreateTime(new Date());
			empClass.setUpdateUser(userService.getCurrentAccount());
			empClass.setUpdateTime(new Date());
		}
		
		return employeeClassMapper.batchSave(list);
	}


	@Override
	public int deleteByQuitTime(EmployeeClass employeeClass) {
		return employeeClassMapper.deleteByQuitTime(employeeClass);
	}
	
	/**
	  * mustArrangeOfWorkMind(排班消息提醒)
	  * @Title: mustArrangeOfWorkMind
	  * @Description: 排班消息提醒
	  * @throws Exception    设定文件
	  * @see com.ule.oa.attendance.service.EmployeeClassService#mustArrangeOfWorkMind()
	  * @throws
	 */
	public void mustArrangeOfWorkMind() throws Exception{
		logger.info("进入排班提醒... ...");
		//1.判断当前时间是否是当月倒数第7或者第4个工作日
		logger.info("[step01]判断当前时间是否是当月倒数第7或者第4个工作日...");
		List<Config> configs = configService.getListByCode(Config.MUST_ARRANGE_OF_WORK_MIND);
		Map<String,Date> confDate = new HashMap<String,Date>();
		configs.forEach((Config conf) -> {
			try {
				Date backDate = annualVacationService.isBackWorkDate(new Date(), Integer.parseInt(conf.getDisplayCode()));
				confDate.put(DateUtils.format(backDate,DateUtils.FORMAT_SHORT), backDate);
				logger.info("排班提醒时间："+DateUtils.format(backDate,DateUtils.FORMAT_SHORT));
			} catch (Exception e) {
				
			}
		});
		String sysDate = DateUtils.format(new Date(),DateUtils.FORMAT_SHORT);
		logger.info("排班提醒时间服务器时间："+sysDate);
		if(null == confDate.get(sysDate)){
			logger.info("当前时间还没到排班提醒时间");
			return;
		}
		
		//2.查找需要排班的部门
		logger.info("[step02]查找需要排班的部门...");
		ScheduleGroup scheduleGroup = new ScheduleGroup();
		List<ScheduleGroup> schedulerList = scheduleGroupMapper.getListByCondition(scheduleGroup);
		
		//3.根据需要排班的部门找到部门下面是否存在次月排班
		logger.info("[step03]根据需要排班的部门找到部门下面是否存在次月排班...");
		String lastFourDay = DateUtils.format(annualVacationService.isBackWorkDate(new Date(), 4),DateUtils.FORMAT_MM_DD);
		schedulerList.forEach((ScheduleGroup person) -> {
			try {
				List<Integer> approvalStatusNoList = new ArrayList<Integer>();
				approvalStatusNoList.add(ConfigConstants.REFUSE_STATUS);
				approvalStatusNoList.add(ConfigConstants.BACK_STATUS);
				
				ApplicationEmployeeClass applyEmpClass = new ApplicationEmployeeClass();
				applyEmpClass.setDepartId(person.getDepartId());
				applyEmpClass.setClassMonth(DateUtils.getFirstDay(DateUtils.addMonth(new Date(), 1)));
				applyEmpClass.setApprovalStatusNoList(approvalStatusNoList);
				applyEmpClass.setIsMove(ApplicationEmployeeClass.IS_MOVE_0);
				
				List<ApplicationEmployeeClass> applyEmpClassList = applicationEmployeeClassService.getByCondition(applyEmpClass);
				Employee emp = employeeService.getById(person.getScheduler());
				String content = "";
				
				//生成排班消息提醒
				EmpMsg empMsg = new EmpMsg();
				empMsg.setDelFlag(CommonPo.STATUS_NORMAL);
				empMsg.setType(EmpMsg.type_200);
				empMsg.setCompanyId(emp.getCompanyId());
				empMsg.setEmployeeId(emp.getId());
				empMsg.setTitle("排班提醒");
				empMsg.setReadFlag(EmpMsg.READ_FLAG_NO);
				empMsg.setCreateTime(new Date());
				if(null == applyEmpClassList || applyEmpClassList.size() == 0){
					logger.info("[step04]生成排班消息和邮件提醒");
					empMsg.setContent("您好，请在"+ lastFourDay +"前完成部门内人员排班，逾期将无法提交，请及时安排，谢谢!!!");
					empMsgService.save(empMsg);
					
					//发送邮件
					content = "Dear " + emp.getCnName() +":<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;请在"+ lastFourDay +"前完成部门内人员排班，逾期将无法提交，请及时安排，谢谢!!!";
					SendMailUtil.sendNormalMail(content, emp.getEmail(), emp.getCnName(), "排班提醒");
				}else{
					ApplicationEmployeeClass aEmpClass = applyEmpClassList.get(0);
					Integer departEmpNums = aEmpClass.getEmployeeNum();//部门人数
					Integer mustPlanNums = aEmpClass.getClassEmployeeNum();//需要排班人数
					
					if(departEmpNums > mustPlanNums){
						logger.info("[step04]生成排班邮件提醒");
						empMsg.setContent("您好，当前部门人数为"+ departEmpNums +"人，实际已排班人数为"+mustPlanNums+"人，请检查是否所有员工均已排班，谢谢!!!");
						empMsgService.save(empMsg);
						
						content = "Dear " + emp.getCnName() +":<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;当前部门人数为"+ departEmpNums +"人，实际已排班人数为"+mustPlanNums+"人，请检查是否所有员工均已排班，谢谢!!!";
						SendMailUtil.sendNormalMail(content, emp.getEmail(), emp.getCnName(), "排班提醒");
					}else{
						logger.info("[step04]排班数据已经设置好");
					}
				}
			} catch (Exception e) {
				logger.error("{}部门提醒排班消息失败，时间转换错误",person.getName());
			}
		});
	}


	@Override
	public EmployeeClass getEmployeeClassSetting(EmployeeClass employeeClass,
			Map<Long, String> workTypeMap, Map<Long, String> schedulingMap) {
		
		Long employeeId = employeeClass.getEmployId();//员工Id
		EmployeeClass result = null;
		result = employeeClassMapper.getEmployeeClassSetting(employeeClass);
		logger.info("员工：{}，排班结果：{}",employeeId,JSONUtils.write(result));
		return result;
	
	}


	@Override
	public List<EmployeeClass> selectByCondition(EmployeeClass employeeClass) {
		return employeeClassMapper.selectByCondition(employeeClass);
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
	public String getEmployeeClassJsonData(String employeeId,String classDate) {
		
		EmployeeClass employeeClass = new EmployeeClass();
		employeeClass.setEmployId(Long.parseLong(employeeId));
		employeeClass.setClassDate(DateUtils.parse(classDate,DateUtils.FORMAT_SHORT));
		
        EmployeeClass empClass = getEmployeeClassByCondition(employeeClass);
		
		Map<String, String> map = new HashMap<String, String>();
		if(null != empClass && null != empClass.getId()){
			String startTime = classDate + " " + DateUtils.format(empClass.getStartTime(), DateUtils.FORMAT_HH_MM_SS);
			String endTime = classDate + " " + DateUtils.format(empClass.getEndTime(), DateUtils.FORMAT_HH_MM_SS);
			
			map.put("className", empClass.getName());
			map.put("startTime", startTime);
			map.put("endTime", endTime);
		}else{//没有排班，默认就按照早9晚18点
			map.put("className", "默认班次");
			map.put("startTime", classDate + " 9:00:00");
			map.put("endTime", classDate + " 18:00:00");
		}
		return JSONUtils.write(map);
	}


	@Override
	public Map<String, Object> queryClassByCondition(Long departId, String month,Long id,boolean isQueryflag,Long groupId) throws Exception{
		
		Map<String,Object> result = new HashMap<String,Object>();
		Date now = new Date();
		if(month==null||"".equals(month)){
			month = DateUtils.format(now, "yyyy-MM");
		}else{
			now = DateUtils.parse(month+"-01", DateUtils.FORMAT_SHORT);
		}
		
		//表头，星期和天数
		List<String> weekDays = new ArrayList<String>();
		//表头，天数
		List<String> days = new ArrayList<String>();
		
		List<String> dates = new ArrayList<String>();
		weekDays.add("职位");
		weekDays.add("姓名");
		weekDays.add("应出勤工时");
		weekDays.add("排班天数");
		weekDays.add("排班工时");
		days.add("");
		days.add("");
		days.add("");
		days.add("");
		days.add("");
		
		dates.add("");
		dates.add("");
		dates.add("");
		dates.add("");
		dates.add("");
		
		Date fristDay = DateUtils.getFirstDay(now);
		Date lastDay = DateUtils.getLastDay(now);
		
		result.put("applyDate", "");
		ApplicationEmployeeClass apply = new ApplicationEmployeeClass();
		apply.setClassMonth(fristDay);
		apply.setDepartId(departId);
		apply.setId(id);
		List<ApplicationEmployeeClass> applyList = applicationEmployeeClassService.getByCondition(apply);
		if(applyList!=null&&applyList.size()>0){
			result.put("applyDate", DateUtils.format(applyList.get(0).getCreateTime(), DateUtils.FORMAT_SHORT));
		}
		
		//查询部门员工排班情况
		ApplicationEmployeeClass param = new ApplicationEmployeeClass();
		param.setDepartId(departId);
		param.setStartTime(fristDay);
		param.setEndTime(lastDay);
		param.setId(id);
		List<Map<String,Object>> datas = applicationEmployeeClassMapper.getDetailById(id,0L);
		
		
		ApplicationEmployeeClass changeClassParam = new ApplicationEmployeeClass();		
		List<Map<String,Object>> datas1=new ArrayList<Map<String,Object>>();
		 //排班最终查询点进来的
		//如果是查询所有排班信息   查询所有在次月份的调班 
		changeClassParam.setStartTime(fristDay);
		changeClassParam.setEndTime(lastDay);
		changeClassParam.setDepartId(departId);
		if(groupId!=null){
			changeClassParam.setGroupId(groupId); //加上 组id 查询
		}
		datas1 = applicationEmployeeClassMapper.getFlagDataCountByInfo(changeClassParam);//统计的排班申请明细表
			//查询 排班最终数据
		datas = applicationEmployeeClassMapper.getFlagDataByInfo(changeClassParam);
		
		//查询排班 员工是否离职、待入职
		
		
		int i = 1;
		dates.add(DateUtils.format(fristDay, "yyyy-MM-dd"));
		while(true){
			String week = DateUtils.getWeek(fristDay);
			weekDays.add(week);
			days.add(String.valueOf(i));
			fristDay = DateUtils.addDay(fristDay, 1);
			
			dates.add(DateUtils.format(fristDay, "yyyy-MM-dd"));
			i = i + 1;
			if(!DateUtils.format(fristDay, "yyyy-MM").equals(DateUtils.format(lastDay, "yyyy-MM"))){
				break;
			}
		}
		result.put("weekDays", weekDays);
		result.put("days", days);
		
		for(Map<String,Object> data1 : datas1){
			String employ_id = String.valueOf(data1.get("employ_id"));
			Position position = positionService.getByEmpId((Long)data1.get("employ_id"));
			Employee emp  = employeeService.getById(Long.valueOf(String.valueOf(data1.get("employ_id"))));
			data1.put("should_time", getShouldTime(applyList.get(0).getClassMonth(),emp));
			data1.put("positionName", position!=null?position.getPositionName():"");
			for(Map<String,Object> data : datas){
				String empId = String.valueOf(data.get("empId"));
				if(employ_id.equals(empId)){
					data1.put(String.valueOf(data.get("classDay")), String.valueOf(data.get("name"))+","+String.valueOf(data.get("classDate"))+","+String.valueOf(data.get("classSettingId")+",notMove,"+String.valueOf(data.get("empId"))+","+String.valueOf(data.get("mustAttnTime"))+""));
				}
			}
		}
		result.put("dates", dates);
		result.put("classDetail", datas1);
		String nowDate = DateUtils.format(new Date(), DateUtils.FORMAT_LONG);
		result.put("nowDate",nowDate);
		
		result.put("count", 0);
		if(datas1!=null&&datas1.size()>0){
			result.put("count", datas1.size());
		}
		
		return result;
	}
	
	//获取员工的应出勤时间(只支持操作时间未当月的)
	public double getShouldTime(Date month_start,Employee old){
		double shouldTime = 0;
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(month_start);
			calendar.add(Calendar.MONTH, 1);
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			Date month_end = calendar.getTime();
			boolean isEndTime = true;
			do{
				//如果入职时间在该月，需去除部分的应出勤时间
				if(old.getQuitTime()==null&&old.getFirstEntryTime()!=null){
					if(old.getFirstEntryTime().getTime()<=month_start.getTime()){
						if(annualVacationService.judgeWorkOrNot(month_start)){
							shouldTime = shouldTime + 8;
						}
					}
				}
				//如果离职时间在该月，需去除部分的应出勤时间
				if(old.getQuitTime()!=null&&DateUtils.format(month_start, "yyyy-MM").equals(DateUtils.format(old.getQuitTime(), "yyyy-MM"))){
					if(old.getQuitTime().getTime()>=month_start.getTime()){
						if(annualVacationService.judgeWorkOrNot(month_start)){
							shouldTime = shouldTime + 8;
						}
					}
				}
				month_start = DateUtils.addDay(month_start, 1);
				if(DateUtils.getIntervalDays(month_start, month_end) < 0) {
					isEndTime = false;
				}
			} while(isEndTime);
		} catch (Exception e) {

		}
		return shouldTime;
	}


	@Override
	public Map<String, Object> queryClassByMonth(ApplicationEmployeeClass empClassTemp) {
		// TODO Auto-generated method stub
		
		
		Map<String,Object> result = new HashMap<String,Object>();
		
		Date now = new Date();
		Date endMonth = new Date();
		if(empClassTemp.getMonth()==null||"".equals(empClassTemp.getMonth())){
			empClassTemp.setMonth(DateUtils.format(now, "yyyy-MM"));
		}else{
			now = DateUtils.parse(empClassTemp.getMonth()+"-01", DateUtils.FORMAT_SHORT);
			endMonth = DateUtils.getLastDay(now);
		}
		
		
		//排班数据
		ApplicationEmployeeClass empClass=new ApplicationEmployeeClass();
		
		
		empClass.setDepartId(empClassTemp.getDepartId());
		empClass.setClassMonth(now);
		List<Integer> statusArrys=new ArrayList<Integer>();
		statusArrys.add(ConfigConstants.PASS_STATUS);
		statusArrys.add(ConfigConstants.OVERDUEPASS_STATUS);
		empClass.setApprovalStatusList(statusArrys);
		if(empClassTemp.getGroupId()!=null){
			empClass.setGroupId(empClassTemp.getGroupId());
		}
		empClass.setIsMove(empClass.IS_MOVE_0);
		List<ApplicationEmployeeClass> empClassList = applicationEmployeeClassMapper.getByCondition(empClass);
		if(empClassList.size()==0){
			empClass.setDepartId(empClassTemp.getDepartId());
			empClass.setStartTime(now);
			empClass.setEndTime(endMonth);
			if(empClassTemp.getGroupId()!=null){
				empClass.setGroupId(empClassTemp.getGroupId());
			}
		   empClassList = applicationEmployeeClassMapper.getCountDataCountByInfo(empClass);			
		}else{
			empClass.setStartTime(now);
			empClass.setEndTime(endMonth);
			for (int i = 0; i < empClassList.size(); i++) {
				ScheduleGroup scheduleGroup = scheduleGroupMapper.getGroupById(empClassList.get(i).getGroupId());
				empClassList.get(i).setGroupName(scheduleGroup!=null?scheduleGroup.getName():"");
			}
		}
		
		//调班数据
		ApplicationEmployeeClass shiftClass=new ApplicationEmployeeClass();
		shiftClass.setDepartId(empClassTemp.getDepartId());
		shiftClass.setClassMonth(now);
		statusArrys.add(ConfigConstants.PASS_STATUS);
		statusArrys.add(ConfigConstants.OVERDUEPASS_STATUS);
		shiftClass.setApprovalStatusList(statusArrys);
		
		if(empClassTemp.getGroupId()!=null){
			shiftClass.setGroupId(empClassTemp.getGroupId());
		}
		shiftClass.setIsMove(shiftClass.IS_MOVE_1);
		
		//进行调班数据分页查询
		PageModel<ApplicationEmployeeClass> pm = new PageModel<ApplicationEmployeeClass>();
		
		int page = empClassTemp.getPage() == null ? 0 : empClassTemp.getPage();
		int rows = empClassTemp.getRows() == null ? 0 : empClassTemp.getRows();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		shiftClass.setLimit(pm.getLimit());
		shiftClass.setOffset(pm.getOffset());	
		
		List<ApplicationEmployeeClass> shiftClassList = applicationEmployeeClassService.getByCondition(shiftClass);
		
		
		Double shouldTime=null;
		if(empClassList!=null &&empClassList.size()>0){
			 shouldTime = applicationEmployeeClassService.getShouldTime(empClassList.get(0).getClassMonth(),DateUtils.getLastDay(empClassList.get(0).getClassMonth()));		
		}
		
		if(shiftClassList!=null &&shiftClassList.size()>0){
			for (int i = 0; i < shiftClassList.size(); i++) {
				if(shiftClassList.get(i).getGroupId()!=null){
					ScheduleGroup scheduleGroup = scheduleGroupMapper.getGroupById(shiftClassList.get(i).getGroupId());
					shiftClassList.get(i).setGroupName(scheduleGroup.getName());
				}
			}		
		}
		
		pm.setRows(shiftClassList);
		pm.setTotal(applicationEmployeeClassMapper.getCountByCondition(shiftClass));
		
		result.put("pm", pm);
		result.put("empClass", empClassList);
		result.put("shouldTime",shouldTime);
		result.put("shiftClass", shiftClassList);
		
		return result;
	}


	@Override
	public List<Map<String, Object>> getClassHoursMapList(
			EmployeeClass employeeClass) {
		return employeeClassMapper.getClassHoursMap(employeeClass);
	}


	@Override
	public Map<Long,Integer> getLeastAttendanceHours(List<Employee> empList, Date month) {
		//获取当前月份所有日期集合
		Date firstDay = DateUtils.getFirstDay(month);
		Date lastDay = DateUtils.getLastDay(month);
		List<Date> allDates = DateUtils.findDates(firstDay, lastDay);
		List<Integer> typeList = new ArrayList<Integer>();
		typeList.add(3);
		typeList.add(4);
		//查询vacation表中当月所有节假日
		AnnualVacation vacation = new AnnualVacation();
		vacation.setStartDate(firstDay);
		vacation.setEndDate(lastDay);
		vacation.setDelFlag(0);
		vacation.setTypeList(typeList);
		//当月所有法定节假日
		List<Date> vacationDayList = annualVacationService.getAnnualDateByCondition(vacation);
		//去掉所有法定节假日
		allDates.removeAll(vacationDayList);
		AnnualVacation workDay = new AnnualVacation();
		workDay.setStartDate(firstDay);
		workDay.setEndDate(lastDay);
		workDay.setDelFlag(0);
		workDay.setType(1);
		//当月所有调休工作日
		List<Date> workDayList = annualVacationService.getAnnualDateByCondition(workDay);
		Map<String,String> workDayStrMap = new HashMap<String,String>();
		for (Date date : workDayList) {
			String dateStr = DateUtils.format(date,DateUtils.FORMAT_SHORT);
			workDayStrMap.put(dateStr,dateStr);
		}
		//去除当月所有周六周日（不包括调休工作日）
		Iterator<Date> it = allDates.iterator();
		while(it.hasNext()){
			Date next = it.next();
			String nextStr = DateUtils.format(next,DateUtils.FORMAT_SHORT);
			if("六".equals(DateUtils.getWeek(next)) || "日".equals(DateUtils.getWeek(next))){
				if(workDayStrMap != null && workDayStrMap.size() > 0){
					if(!workDayStrMap.containsKey(nextStr)) {
						it.remove();
					}
				}else{
					it.remove();
				}
				
			}
		}
		Map<Long,Integer> resultMap = new HashMap<Long,Integer>();
		for (Employee employee : empList) {
			Integer hour = 0;
			for (Date date : allDates) {
				//入职日期与离职日期都不为空
				if(employee.getFirstEntryTime() != null && employee.getQuitTime() != null){
					if(DateUtils.compareDate(employee.getFirstEntryTime(), date) != 1 && DateUtils.compareDate(date,employee.getQuitTime()) != 1){
						hour += 8;
					}
				}
				//入职日期为空离职日期不为空
				if(employee.getFirstEntryTime() != null && employee.getQuitTime() == null){
					if(DateUtils.compareDate(employee.getFirstEntryTime(), date) != 1){
						hour += 8;
					}
				}
			}
			resultMap.put(employee.getId(), hour);
		}
		return resultMap;
	}


	@Override
	public Map<Date, EmployeeClass> getEmployeeClassSetting(Employee employee,
			Date startDate, Date endDate) {
		    Map<Date,EmployeeClass> returnMap = new HashMap<Date, EmployeeClass>();
			EmployeeClass employeeClass = new EmployeeClass();
			employeeClass.setEmployId(employee.getId());
			employeeClass.setStartTime(startDate);
			employeeClass.setEndTime(endDate);
			List<EmployeeClass> list = employeeClassMapper.getEmployeeClassList(employeeClass);
			for(EmployeeClass data:list){
				returnMap.put(data.getClassDate(), data);
			}
			return returnMap;
	}


	@Override
	public Map<Long, List<EmployeeClass>> getEmployeeClassSetting(
			List<Long> employeeIds, Date startDate, Date endDate) {
		Map<Long,List<EmployeeClass>> returnMap = new HashMap<Long, List<EmployeeClass>>();
		EmployeeClass employeeClass = new EmployeeClass();
		employeeClass.setEmployeeIds(employeeIds);
		employeeClass.setStartTime(startDate);
		employeeClass.setEndTime(endDate);
		List<EmployeeClass> list = employeeClassMapper.getEmployeeClassList(employeeClass);
		returnMap = list.stream().collect(Collectors.groupingBy(EmployeeClass :: getEmployId));
		return returnMap;
	}


	@Override
	public HSSFWorkbook exportScheduleDataByMonthAndGroupId(Long departId, Long groupId, String month) {
		if(StringUtils.isBlank(month)) {
			month = DateUtils.format(new Date(), DateUtils.FORMAT_YYYY_MM);
		}
		Date startDate = DateUtils.parse(month+"-01", DateUtils.FORMAT_SHORT);
		Date endDate = DateUtils.getLastDay(startDate);
		if(departId==null) {
			departId = 193L;//系统运维部
		}
		if(groupId==null){
			groupId = 3L;//运维组
		}
		List<EmployeeClass> scheduleList = employeeClassMapper.exportScheduleDataByMonthAndGroupId(departId, groupId, startDate, endDate);
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		for(EmployeeClass data:scheduleList) {
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("cnName", data.getEmployName());
			map.put("startTime", DateUtils.format(data.getClassDate(), DateUtils.FORMAT_SHORT)+" "+DateUtils.format(data.getStartTime(), DateUtils.FORMAT_HH_MM));
			if(data.getIsInterDay()==0) {
				map.put("endTime", DateUtils.format(data.getClassDate(), DateUtils.FORMAT_SHORT)+" "+DateUtils.format(data.getEndTime(), DateUtils.FORMAT_HH_MM));
			}else {
				map.put("endTime", DateUtils.format(DateUtils.addDay(data.getClassDate(), 1), DateUtils.FORMAT_SHORT)+" "+DateUtils.format(data.getEndTime(), DateUtils.FORMAT_HH_MM));
			}
			list.add(map);
		}
		String[] keys={"cnName", "startTime", "endTime"};
		String[] titles={"员工","开始时间","结束时间"}; 
		return ExcelUtil.exportExcel(list, keys, titles, "排班报表.xls");
	}


	@Override
	public int deleteByStartDate(Date startDate) {
		return employeeClassMapper.deleteByStartDate(startDate);
	}
}
