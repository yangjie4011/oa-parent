package com.ule.oa.base.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ule.oa.base.mapper.AttentanceSetMapper;
import com.ule.oa.base.mapper.ClassSettingMapper;
import com.ule.oa.base.mapper.EmployeeClassMapper;
import com.ule.oa.base.mapper.EmployeeMapper;
import com.ule.oa.base.mapper.ScheduleGroupMapper;
import com.ule.oa.base.po.AnnualVacation;
import com.ule.oa.base.po.AttentanceSetDTO;
import com.ule.oa.base.po.ClassSetting;
import com.ule.oa.base.po.CompanyConfig;
import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.po.ImportAttentanceSetExcel;
import com.ule.oa.base.po.ScheduleGroup;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.AnnualVacationService;
import com.ule.oa.base.service.AttentanceSetService;
import com.ule.oa.base.service.CompanyConfigService;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.util.SendMailUtil;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.PageModel;
import com.ule.oa.common.utils.excel.ExcelImportUtil;

@Service
public class AttentanceSetServiceImpl implements AttentanceSetService {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private AttentanceSetMapper attentanceSetMapper;
	@Autowired
	private UserService userService;
	@Autowired
	private ConfigService configService;
	@Autowired
	private CompanyConfigService companyConfigService;
	@Autowired
	private EmployeeMapper employeeMapper;
	@Autowired
	private ClassSettingMapper classSettingMapper;
	@Autowired
	private AnnualVacationService annualVacationService;
	@Autowired
	private EmployeeClassMapper employeeClassMapper;
	@Autowired
	private ScheduleGroupMapper scheduleGroupMapper;

	@Override
	public PageModel<AttentanceSetDTO> getPageList(AttentanceSetDTO param) {
		
		long time1 = System.currentTimeMillis();
		
		if(param.getJobStatus()==null){
			List<Long> jobStatusList = new ArrayList<Long>();
			jobStatusList.add(0L);
			jobStatusList.add(2L);
			param.setJobStatusList(jobStatusList);
		}
		
		String month = param.getMonth();
		if(StringUtils.isBlank(month)){
			month = DateUtils.format(new Date(), "yyyy-MM");
		}
		
		Date startDate = DateUtils.parse(month+"-01", DateUtils.FORMAT_SHORT);
		Date endDate = DateUtils.getLastDay(startDate);
		
		int page = param.getPage() == null ? 0 : param.getPage();
		int rows = param.getRows() == null ? 0 : param.getRows();
		
		PageModel<AttentanceSetDTO> pm = new PageModel<AttentanceSetDTO>();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		
		Long whetherScheduling = null;
		
		//是否排班
		Config configCondition = new Config();
		configCondition.setCode("whetherScheduling");
		List<Config> whetherSchedulingList = configService.getListByCondition(configCondition);
        for(Config config:whetherSchedulingList){
			if("no".equals(config.getDisplayCode())){
				whetherScheduling = config.getId();
			}
		}
        param.setWhetherScheduling(whetherScheduling);
        
        //工时类型
        CompanyConfig companyConfigConditon = new CompanyConfig();//查询配置表条件
		companyConfigConditon.setCode("typeOfWork");//工时类型
		List<CompanyConfig> workTypeList = companyConfigService.getListByCondition(companyConfigConditon);
		Map<Long,String> workTypeMap =  workTypeList.stream().collect(Collectors.toMap(CompanyConfig :: getId, CompanyConfig :: getDisplayName));
		
		
		Integer total = attentanceSetMapper.getEmployeeCount(param);
		pm.setTotal(total);
		param.setOffset(pm.getOffset());
		param.setLimit(pm.getLimit());
		List<AttentanceSetDTO> list = attentanceSetMapper.getEmployeeList(param);
		for(AttentanceSetDTO data:list){
			if(workTypeMap!=null&&workTypeMap.containsKey(data.getWorkType())){
				data.setWorkTypeName(workTypeMap.get(data.getWorkType()));
			}
			AttentanceSetDTO classInfo = attentanceSetMapper.getEmployeeClassInfo(data.getEmployeeId(), startDate, endDate);
			if(classInfo!=null){
				data.setClassName(classInfo.getClassName());
				data.setStartTime(classInfo.getStartTime());
				data.setEndTime(classInfo.getEndTime());
			}
			
		}

		pm.setRows(list);
		
		long time2 = System.currentTimeMillis();
		logger.info("AttentanceSetService getPageList uses time is:"+(time2-time1));
		return pm;
	}

	@Override
	public Map<String, Object> importTemplate(MultipartFile file)
			throws OaException, Exception {
		logger.info("导入弹性工时设置-----开始");
		try {
			Map<String, Object> resultMap = new HashMap<String, Object>();
			// 获取当前登录人
			User currentUser = userService.getCurrentUser();
			// 判断文件是否存在
			if (null == file) {
				logger.error("文件不存在！");
				throw new OaException("文件不存在！");
			}
			// 获得文件名
			String fileName = file.getOriginalFilename();
			InputStream inputStream = file.getInputStream();
			// 判断文件是否是excel文件
			if (!fileName.endsWith("xls") && !fileName.endsWith("xlsx")) {
				logger.error(fileName + "不是excel文件");
				throw new OaException(fileName + "不是excel文件");
			}
			// 读取文件转换为实体类
			List<Object> excelToBean = ExcelImportUtil.excelToBean(ImportAttentanceSetExcel.class, inputStream, 0, 0);
			List<ImportAttentanceSetExcel> employeeList = (List<ImportAttentanceSetExcel>) (List) excelToBean;
			int count = employeeList.size(); // 插入总条数
			int failCount = 0; // 失败条数
			StringBuffer msg = new StringBuffer();
			Map<Long,String> updateEmpMap = new HashMap<Long,String>();
			importTemple:
			for (int i = 0; i < employeeList.size(); i++) {
				ImportAttentanceSetExcel attentanceSet = employeeList.get(i);
				// 校验必填字段
				if (StringUtils.isBlank(attentanceSet.getCode())) {
					failCount++;
					msg.append("\n第" + (i + 1) + "条数据：员工编号不能为空");
					continue importTemple;
				}
				if (StringUtils.isBlank(attentanceSet.getClassSetName())) {
					failCount++;
					msg.append("\n第" + (i + 1) + "条数据：班次不能为空");
					continue importTemple;
				}
				if (StringUtils.isBlank(attentanceSet.getYear())) {
					failCount++;
					msg.append("\n第" + (i + 1) + "条数据：生效年份不能为空");
					continue importTemple;
				}
				if (StringUtils.isBlank(attentanceSet.getMonth())) {
					failCount++;
					msg.append("\n第" + (i + 1) + "条数据：生效月份不能为空");
					continue importTemple;
				}
				Employee employee = employeeMapper.getInfoByCode(attentanceSet.getCode());
				if (employee == null||employee.getId()==null) {
					msg.append("\n第" + (i + 1) + "条数据：该员工在系统中不存在");
					failCount++;
					continue importTemple;
				}
				ClassSetting classSetting = classSettingMapper.getByName(attentanceSet.getClassSetName());
				if (classSetting==null||classSetting.getId() == null) {
					msg.append("\n第" + (i + 1) + "条数据：该班次在系统中不存在");
					failCount++;
					continue importTemple;
				}
				if(classSetting.getMustAttnTime().doubleValue()!=8){
					msg.append("\n第" + (i + 1) + "条数据：只支持8小时班次");
					failCount++;
					continue importTemple;
				}
				Long classSettingId = classSetting.getId();
                //获取导入班次开始结束时间
				Date startDate = DateUtils.parse(attentanceSet.getYear()+"-"+attentanceSet.getMonth()+"-01", DateUtils.FORMAT_SHORT);
				Date endDate = DateUtils.getLastDay(startDate);
				AnnualVacation vacationParam = new AnnualVacation();
				vacationParam.setStartDate(startDate);
				vacationParam.setEndDate(endDate);
				List<AnnualVacation> vacationList = annualVacationService.getListByCondition(vacationParam);
				Map<Date,Integer> vacationMap = vacationList.stream().collect(Collectors.toMap(AnnualVacation :: getAnnualDate, AnnualVacation :: getType));
				EmployeeClass employeeClassParam = new EmployeeClass();
				employeeClassParam.setStartTime(startDate);
				employeeClassParam.setEndTime(endDate);
				employeeClassParam.setEmployId(employee.getId());
				List<EmployeeClass> employeeClassList = employeeClassMapper.selectByCondition(employeeClassParam);
				Map<Date,EmployeeClass> employeeClassMap = employeeClassList.stream().collect(Collectors.toMap(EmployeeClass :: getClassDate,a->a,(k1,k2)->k1));
				ScheduleGroup group = scheduleGroupMapper.getGroupByEmployeeId(employee.getId());
				Long groupId = group!=null?group.getId():null;
				initEmployClass(employee,classSettingId,currentUser,startDate,endDate,vacationMap,employeeClassMap,groupId);
			}
			msg.append("\n本次共导入" + count + "条数据，导入失败" + failCount + "条"); // 返回信息
			logger.info(msg.toString());
			logger.info("导入导入弹性工时设置-----结束");
			resultMap.put("resultMsg", msg.toString());
			resultMap.put("updateEmpMap", updateEmpMap);
			return resultMap;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new RuntimeException(e.getMessage());
		}

	}

	@Override
	public void initEmployClass(Employee employee, Long classSettingId,
			User user, Date startDate, Date endDate,Map<Date,Integer> vacationMap,Map<Date,EmployeeClass> employeeClassMap,Long groupId) {
       
		logger.info("初始化"+employee.getCnName()+DateUtils.format(startDate, DateUtils.FORMAT_SHORT)+"至"+DateUtils.format(endDate, DateUtils.FORMAT_SHORT)+"班次开始");
		
		//获取初始化班次开始结束时间
		Date month_start1 = startDate;
		Date month_end1 = endDate;
		
		//计算该月应出勤时间
		boolean isEndTime1 = true;
	    double should_time = 0;
	    do{
	    	if(annualVacationService.judgeWorkOrNot(month_start1,vacationMap)){
	    		should_time += 8;
			}
			month_start1 = DateUtils.addDay(month_start1, 1);
			if(DateUtils.getIntervalDays(month_start1, month_end1) < 0) {
				isEndTime1 = false;
			}
		} while(isEndTime1);
		double shouldTime = should_time;
		
		Date month_start = startDate;
		Date month_end = endDate;
		boolean isEndTime = true;
		
		Date init_start = startDate;
		Date init_end = endDate;
		do{
			//如果入职时间在该月，需去除部分的应出勤时间
			if(employee.getQuitTime()==null&&employee.getFirstEntryTime()!=null&&DateUtils.format(month_start, "yyyy-MM").equals(DateUtils.format(employee.getFirstEntryTime(), "yyyy-MM"))){
				init_start = employee.getFirstEntryTime();
				if(employee.getFirstEntryTime().getTime()>month_start.getTime()){
					if(annualVacationService.judgeWorkOrNot(month_start,vacationMap)){
						shouldTime = shouldTime - 8;
					}
				}
			}
			//如果离职时间在该月，需去除部分的应出勤时间
			if(employee.getQuitTime()!=null&&DateUtils.format(month_start, "yyyy-MM").equals(DateUtils.format(employee.getQuitTime(), "yyyy-MM"))){
				init_end = employee.getQuitTime();
				if(employee.getQuitTime().getTime()<month_start.getTime()){
					if(annualVacationService.judgeWorkOrNot(month_start,vacationMap)){
						shouldTime = shouldTime - 8;
					}
				}
			}
			month_start = DateUtils.addDay(month_start, 1);
			if(DateUtils.getIntervalDays(month_start, month_end) < 0) {
				isEndTime = false;
			}
		} while(isEndTime);
		
		boolean isInitEnd = true;
		List<EmployeeClass> employeeClassList = new ArrayList<EmployeeClass>();
		List<Long> deleteIdList = new ArrayList<Long>();
		do{ 
			if(employeeClassMap!=null&&employeeClassMap.containsKey(init_start)&&employeeClassMap.get(init_start).getId()!=null){
				deleteIdList.add(employeeClassMap.get(init_start).getId());
			}
			if(annualVacationService.judgeWorkOrNot(init_start,vacationMap)){
				EmployeeClass employeeClass = new EmployeeClass();
				employeeClass.setClassDate(init_start);
				employeeClass.setCompanyId(1L);
				employeeClass.setDepartId(employee.getDepartId());
				employeeClass.setGroupId(groupId);
				employeeClass.setShouldTime(shouldTime);
				employeeClass.setEmployId(employee.getId());
				employeeClass.setEmployName(employee.getCnName());
				employeeClass.setClassSettingPerson(user.getEmployee().getCnName());
				employeeClass.setCreateTime(new Date());
				employeeClass.setCreateUser(user.getEmployee().getCnName());
				employeeClass.setDelFlag(0);
				employeeClass.setClassSettingId(classSettingId);
				employeeClassList.add(employeeClass);
			}
			init_start = DateUtils.addDay(init_start, 1);
			if(DateUtils.getIntervalDays(init_start, init_end) < 0) {
				isInitEnd = false;
			}
		} while(isInitEnd);
		
		if(deleteIdList!=null&&deleteIdList.size()>0){
			employeeClassMapper.deleteByIdList(deleteIdList, user.getEmployee().getCnName());
		}
		if(employeeClassList!=null&&employeeClassList.size()>0){
			employeeClassMapper.batchSave(employeeClassList);
		}
		
		logger.info("初始化"+employee.getCnName()+DateUtils.format(startDate, DateUtils.FORMAT_SHORT)+"至"+DateUtils.format(endDate, DateUtils.FORMAT_SHORT)+"班次结束");
	}

	@Override
	public void initAllEmployClass(String month) {
		
		//获取非排班标识
		Long whetherScheduling = null;
		Config configCondition = new Config();
		configCondition.setCode("whetherScheduling");
		List<Config> whetherSchedulingList = configService.getListByCondition(configCondition);
        for(Config config:whetherSchedulingList){
			if("no".equals(config.getDisplayCode())){
				whetherScheduling = config.getId();
			}
		}
        if(whetherScheduling==null){
        	logger.error("initAllEmployClass失败："+"未获取到非排班标识。。");
        	return;
        }
		
        //获取9-18点的班次
        ClassSetting param = new ClassSetting();
    	param.setStartTime(DateUtils.parse("09:00:00", DateUtils.FORMAT_HH_MM_SS));
    	param.setEndTime(DateUtils.parse("18:00:00", DateUtils.FORMAT_HH_MM_SS));
    	List<ClassSetting> setList = classSettingMapper.getListByCondition(param);
    	if(setList==null||setList.size()<=0){
    		logger.error("initAllEmployClass失败："+"未获取到9-18点的班次。。");
        	return;
    	}
        Long classSettingId = setList.get(0).getId();
        
        //获取节假日信息
        Date startDate = DateUtils.parse(month+"-01", DateUtils.FORMAT_SHORT);
		Date endDate = DateUtils.getLastDay(startDate);
		AnnualVacation vacationParam = new AnnualVacation();
		vacationParam.setStartDate(startDate);
		vacationParam.setEndDate(endDate);
		List<AnnualVacation> vacationList = annualVacationService.getListByCondition(vacationParam);
		Map<Date,Integer> vacationMap = vacationList.stream().collect(Collectors.toMap(AnnualVacation :: getAnnualDate, AnnualVacation :: getType));
        
		//查询所有符合条件的员工
		Employee employeeParam = new Employee();
		employeeParam.setWhetherScheduling(whetherScheduling);
		List<Long> jobStatusList = new ArrayList<Long>();
		jobStatusList.add(0L);
		jobStatusList.add(2L);
		employeeParam.setJobStatusList(jobStatusList);
		List<Employee> emplist = employeeMapper.getListByCondition(employeeParam);
		
		for(Employee employee : emplist){
			try{
				EmployeeClass employeeClassParam = new EmployeeClass();
				employeeClassParam.setStartTime(startDate);
				employeeClassParam.setEndTime(endDate);
				employeeClassParam.setEmployId(employee.getId());
				List<EmployeeClass> employeeClassList = employeeClassMapper.selectByCondition(employeeClassParam);
				Map<Date,EmployeeClass> employeeClassMap = employeeClassList.stream().collect(Collectors.toMap(EmployeeClass :: getClassDate,a->a,(k1,k2)->k1));
				User user = new User();
				Employee userEmp = new Employee();
				userEmp.setCnName("system");
				user.setEmployee(userEmp);
				ScheduleGroup group = scheduleGroupMapper.getGroupByEmployeeId(employee.getId());
				Long groupId = group!=null?group.getId():null;
				initEmployClass(employee,classSettingId,user,startDate,endDate,vacationMap,employeeClassMap,groupId);
			}catch(Exception e){
				logger.error("initAllEmployClass失败：employeeName="+employee.getCnName());
			}
			
        }
		
	}

	@Override
	public void sendMailToClassChangeEmp() {
		//获取当前时间
		Date now = new Date();
		//根据当前时间获取到月初时间和月底时间
		Date monthStart = DateUtils.parse(DateUtils.format(now, "yyyy-MM")+"-01", DateUtils.FORMAT_SHORT);
		Date monthEnd = DateUtils.getLastDay(monthStart);
		
		//判断当前时间是月初还是月底
		if(DateUtils.format(now, DateUtils.FORMAT_SHORT).equals(DateUtils.format(monthStart, DateUtils.FORMAT_SHORT))){
			//月初，发送当月班次不是9-18的邮件提醒
			sendNoticeMail(monthStart,monthEnd,1);
		}else if(DateUtils.format(now, DateUtils.FORMAT_SHORT).equals(DateUtils.format(monthEnd, DateUtils.FORMAT_SHORT))){
			//月末，发送下月班次不是9-18的邮件提醒
			Date nextMonthStart = DateUtils.addMonth(monthStart, 1);
			Date nextMonthEnd= DateUtils.getLastDay(nextMonthStart);
			sendNoticeMail(nextMonthStart,nextMonthEnd,2);
		}else{
			logger.error("sendMailToClassChangeEmp:不是月初和月末不做邮件提醒。");
		}
	}
	
	public void sendNoticeMail(Date monthStart,Date monthEnd,int type){
		//获取非排班标识
		Long whetherScheduling = null;
		Config configCondition = new Config();
		configCondition.setCode("whetherScheduling");
		List<Config> whetherSchedulingList = configService.getListByCondition(configCondition);
        for(Config config:whetherSchedulingList){
			if("no".equals(config.getDisplayCode())){
				whetherScheduling = config.getId();
			}
		}
        if(whetherScheduling==null){
        	logger.error("sendNoticeMail失败："+"未获取到非排班标识。。");
        	return;
        }
        //查询所有符合条件的员工
  		Employee employeeParam = new Employee();
  		employeeParam.setWhetherScheduling(whetherScheduling);
  		List<Long> jobStatusList = new ArrayList<Long>();
  		jobStatusList.add(0L);
  		jobStatusList.add(2L);
  		employeeParam.setJobStatusList(jobStatusList);
  		List<Employee> emplist = employeeMapper.getListByCondition(employeeParam);
  		for(Employee data:emplist){
  			
  			//查询上个月班次
  			Date lastMonthStart = DateUtils.addMonth(monthStart, -1);
  			Date lastMonthEnd = DateUtils.getLastDay(lastMonthStart);
  			
  			EmployeeClass param = new EmployeeClass();
  			param.setStartTime(lastMonthStart);
  			param.setEndTime(lastMonthEnd);
  			param.setEmployId(data.getId());
  			
  			List<EmployeeClass> lastMonthList = employeeClassMapper.getEmployeeClassList(param);
  			String lastMonthStartTime = "";
  			String lastMonthEndTime = "";
  			if(lastMonthList!=null&&lastMonthList.size()>0){
  				EmployeeClass lastEmployeeClass = lastMonthList.get(0);
  				lastMonthStartTime = (lastEmployeeClass!=null&&lastEmployeeClass.getStartTime()!=null)?DateUtils.format(lastEmployeeClass.getStartTime(), DateUtils.FORMAT_HH_MM):"";
  				lastMonthEndTime = (lastEmployeeClass!=null&&lastEmployeeClass.getEndTime()!=null)?DateUtils.format(lastEmployeeClass.getEndTime(), DateUtils.FORMAT_HH_MM):"";
  			}
  			
  			AttentanceSetDTO classInfo = attentanceSetMapper.getEmployeeClassInfo(data.getId(), monthStart, monthEnd);
			if(classInfo!=null&&classInfo.getStartTime()!=null&&classInfo.getEndTime()!=null
					&&(!lastMonthStartTime.equals(DateUtils.format(classInfo.getStartTime(), DateUtils.FORMAT_HH_MM))
					||!lastMonthEndTime.equals(DateUtils.format(classInfo.getEndTime(), DateUtils.FORMAT_HH_MM)))){
				String title = "考勤时间调整提醒";
				String month = DateUtils.format(monthStart, DateUtils.FORMAT_SHORT_CN);
				String classStart = DateUtils.format(classInfo.getStartTime(), DateUtils.FORMAT_HH_MM);
				String classEnd = DateUtils.format(classInfo.getEndTime(), DateUtils.FORMAT_HH_MM);
				String content = "";
				if(type==1){
					content = "Dear "+data.getCnName()+":<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 您在今天起的考勤时间调整为"+classStart+"-"+classEnd+"，请知晓！";
				}else{
					content = "Dear "+data.getCnName()+":<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 您在"+month+"起的考勤时间调整为"+classStart+"-"+classEnd+"，请知晓！";
				}
			    try {
			    	if(!"TOM001".equals(data.getCode())){
			    		SendMailUtil.sendNormalMail(content, data.getEmail(),data.getCnName(), title);
			    	}
				} catch (Exception e) {
				}
			}
  		}
	}

}
