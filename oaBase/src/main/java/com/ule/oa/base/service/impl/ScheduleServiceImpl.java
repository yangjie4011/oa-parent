package com.ule.oa.base.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ule.oa.base.mapper.ApplicationEmployeeClassDetailMapper;
import com.ule.oa.base.mapper.ApplicationEmployeeClassMapper;
import com.ule.oa.base.mapper.ClassSettingMapper;
import com.ule.oa.base.mapper.DepartMapper;
import com.ule.oa.base.mapper.EmployeeClassMapper;
import com.ule.oa.base.mapper.EmployeeMapper;
import com.ule.oa.base.mapper.ScheduleGroupMapper;
import com.ule.oa.base.po.AnnualVacation;
import com.ule.oa.base.po.ApplicationEmployeeClass;
import com.ule.oa.base.po.ApplicationEmployeeClassDetail;
import com.ule.oa.base.po.ClassSetting;
import com.ule.oa.base.po.CompanyConfig;
import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.EmpLeave;
import com.ule.oa.base.po.EmpScheduleGroup;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.po.Position;
import com.ule.oa.base.po.ScheduleGroup;
import com.ule.oa.base.po.SendMail;
import com.ule.oa.base.po.User;
import com.ule.oa.base.po.tbl.ViewTaskInfoTbl;
import com.ule.oa.base.service.AnnualVacationService;
import com.ule.oa.base.service.ClassSettingService;
import com.ule.oa.base.service.CompanyConfigService;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmployeeClassService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.PositionService;
import com.ule.oa.base.service.ScheduleService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.service.ViewTaskInfoService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.ExcelUtil;
import com.ule.oa.common.utils.PageModel;

@Service
public class ScheduleServiceImpl implements ScheduleService{
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private DepartMapper departMapper;
	
	@Autowired
	private ScheduleGroupMapper scheduleGroupMapper;
	
	@Autowired
	private EmployeeMapper employeeMapper;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CompanyConfigService companyConfigService;
	
	@Autowired
	private ConfigService configService;
	
	@Autowired
	private EmployeeClassMapper employeeClassMapper;
	
	@Autowired
	private ClassSettingMapper classSettingMapper;
	
	@Autowired
	private PositionService positionService;
	
	@Autowired
	private ApplicationEmployeeClassMapper applicationEmployeeClassMapper;
	
	@Autowired
	private ApplicationEmployeeClassDetailMapper applicationEmployeeClassDetailMapper;
	
	@Autowired
	private AnnualVacationService annualVacationService;
	
	@Autowired
	private EmployeeClassService employeeClassService;
	
	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private DepartService departService;
	
	@Autowired
	private ClassSettingService classSettingService;
	@Autowired
	private ActivitiServiceImpl activitiServiceImpl;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private ViewTaskInfoService viewTaskInfoService;
	
	@Override
	public List<Map<String, String>> getTreeList(Depart model) {
		//返回结果集
		List<Map<String, String>> rusultlist = new ArrayList<Map<String, String>>();
		//根据公司id查出公司所有需要排版的部门作为一级菜单
		model.setWhetherScheduling(ConfigConstants.IS_YES_INTEGER);
		List<Depart> departList = departMapper.getListByCondition(model);
		Map<String, String> map;
		for (Depart depart : departList) {
			map = new HashMap<String, String>();
			map.put("id", depart.getId() + "");
			map.put("pId", depart.getParentId() == null ? "0" : depart.getParentId() + "");
			map.put("name", depart.getName());
			rusultlist.add(map);
			//根据部门id查出排班组作为二级菜单
			List<ScheduleGroup> scheduleGroupList = scheduleGroupMapper.getListByDepartId(depart.getId());
			for (ScheduleGroup scheduleGroup : scheduleGroupList) {
				map = new HashMap<String, String>();
				map.put("id", scheduleGroup.getId().toString());
				map.put("pId", depart.getId().toString());
				map.put("name", scheduleGroup.getName());
				rusultlist.add(map);
			}
		}
		
		return rusultlist;
	}


	@Override
	public List<ScheduleGroup> getGroupListByDepartId(Long departId) {
		//查询部门下所有排班组别
		List<ScheduleGroup> groupList = scheduleGroupMapper.getListByDepartId(departId);
		//统计各组员工总数
		if(groupList != null && groupList.size() > 0){
			for (ScheduleGroup scheduleGroup : groupList) {
				Integer empCount = scheduleGroupMapper.getEmpCount(scheduleGroup.getId());
				scheduleGroup.setEmpCount(empCount);
				Employee scheduler = employeeMapper.getById(scheduleGroup.getScheduler());
				if(scheduler != null){
					scheduleGroup.setSchedulerName(scheduler.getCnName());
				}
				Employee auditor = employeeMapper.getById(scheduleGroup.getAuditor());
				if(auditor != null){
					scheduleGroup.setAuditorName(auditor.getCnName());
				}
			}
		}
		return groupList;
	}

	/**
	 * 根据班组id查询班组信息
	 */
	@Override
	public List<ScheduleGroup> getGroupInfoByGroupId(Long groupId) {
		logger.info("-------------查询班组信息---------------");
		if(groupId == null){
			return null;
		}
		List<ScheduleGroup> scheduleGroupList = new ArrayList<ScheduleGroup>();
		ScheduleGroup scheduleGroup = scheduleGroupMapper.getGroupById(groupId);
		Integer empCount = scheduleGroupMapper.getEmpCount(scheduleGroup.getId());
		scheduleGroup.setEmpCount(empCount);
		Employee scheduler = employeeMapper.getById(scheduleGroup.getScheduler());
		if(scheduler != null){
			scheduleGroup.setSchedulerName(scheduler.getCnName());
		}
		Employee auditor = employeeMapper.getById(scheduleGroup.getAuditor());
		if(auditor != null){
			scheduleGroup.setAuditorName(auditor.getCnName());
		}
		scheduleGroupList.add(scheduleGroup);
		return scheduleGroupList;
	}


	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveScheduleGroup(ScheduleGroup group) throws OaException {
		logger.info("-------------保存排班班组开始---------------");
		//获取当前登录人
		User user = userService.getCurrentUser();
		//获取当前时间
		Date nowDate = DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_LONG), DateUtils.FORMAT_LONG);
		
		if(group != null){
			Long departId = group.getDepartId();
			Long scheduler = group.getScheduler();
			Long auditor = group.getAuditor();
			String name = group.getName();
			if(departId == null || scheduler == null || auditor == null || StringUtils.isBlank(name)){
				logger.error("参数错误");
				throw new OaException("必填信息不能为空！");
			}
			group.setDepartId(departId);
			name = replaceBlank(name);
			//校验部门下是否存在同名班组
			Integer count = scheduleGroupMapper.isNotSameName(group);
			if(count > 0){
				logger.error("该部门下已存在同名班组！");
				throw new OaException("该部门下已存在同名班组！");
			}
			ScheduleGroup scheduleGroup = new ScheduleGroup();
			if(group.getId() == null){
				scheduleGroup.setDepartId(departId);
				scheduleGroup.setName(group.getName());
				scheduleGroup.setScheduler(scheduler);
				scheduleGroup.setAuditor(auditor);
				scheduleGroup.setDelFlag(ConfigConstants.IS_NO_INTEGER);
				scheduleGroup.setCreateUser(user.getId());
				scheduleGroup.setCreateTime(nowDate);
				scheduleGroupMapper.insertScheduleGroup(scheduleGroup);
			}else{
				//修改分组
				scheduleGroup.setId(group.getId());
				scheduleGroup.setName(group.getName());
				scheduleGroup.setScheduler(scheduler);
				scheduleGroup.setAuditor(auditor);
				scheduleGroup.setDelFlag(ConfigConstants.IS_NO_INTEGER);
				scheduleGroup.setUpdateUser(user.getId());
				scheduleGroup.setUpdateTime(nowDate);
				scheduleGroupMapper.updateScheduleGroup(scheduleGroup);
			}
			logger.info("-------------保存排班班组成功---------------");
			
		}
		
	}
	
	/**
	 * 去除所有空格回车换行符制表符
	 * 
	 * @param str
	 * @return
	 */
	public static String replaceBlank(String oldString) {
		String dest = "";
		if (oldString != null) {
			// 空格\t、回车\n、换行符\r、制表符\t
			Matcher m = ConfigConstants.REPLACE_BLACK_PATTERN.matcher(oldString);
			dest = m.replaceAll("");
		}
		return dest;
	}

	/**
	 * 查询班组下所有在职员工信息
	 */
	@Override
	public List<Employee> getAllGroupEmp(Long groupId,String condition) {
		logger.info("-------------查询班组下所有在职员工信息---------------");
		//查询所属部门名称
		String departName = scheduleGroupMapper.getDepartNameByGroupId(groupId);
		//查询该班组下所有在职员工
		List<Employee> empList = scheduleGroupMapper.getAllGroupEmp(groupId,condition,null);
		if(empList != null && StringUtils.isNotBlank(departName)){
			for (Employee employee : empList) {
				employee.setDepartName(departName);
			}
		}
		return empList;
	}

	/**
	 * 查询该组所属部门下所有未分配组的员工
	 */
	@Override
	public List<Employee> getUngroupedEmp(Employee emp) {
		//1.员工表中标记可排班标识的员工
		Long workTypeId = null;
		Long whetherScheduleId = null;
		CompanyConfig companyConfigConditon = new CompanyConfig();//查询配置表条件
		companyConfigConditon.setCode("typeOfWork");//工时类型
		List<CompanyConfig> workTypeList = companyConfigService.getListByCondition(companyConfigConditon);
		for(CompanyConfig companyConfig:workTypeList){
			if("standard".equals(companyConfig.getDisplayCode())){
				workTypeId = companyConfig.getId();
				break;
			}
		}
		Config configCondition = new Config();
		configCondition.setCode("whetherScheduling");//是否排班
		List<Config> whetherSchedulingList = configService.getListByCondition(configCondition);
        for(Config config:whetherSchedulingList){
			if("yes".equals(config.getDisplayCode())){
				whetherScheduleId = config.getId();
				break;
			}
		}
        emp.setWorkType(workTypeId);
        emp.setWhetherScheduling(whetherScheduleId);
		//2.未离职未删除员工
		//3.未被分配组的员工(包括分配后删除的员工)
		logger.info("-------------查询该组所属部门下所有未分配组的员工---------------");
		List<Employee> empList = scheduleGroupMapper.getUngroupedEmp(emp);
		return empList;
	}

	
	/**
	 * 添加组员
	 * @throws OaException 
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addMember(String empList, Long groupId) throws OaException {
		logger.info("-------------添加组员开始---------------");
		//获取当前登录人
		User user = userService.getCurrentUser();
		//获取当前时间
		Date nowDate = DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_LONG), DateUtils.FORMAT_LONG);
		if(StringUtils.isBlank(empList) || groupId == null){
			logger.error("参数错误！员工编号或班组id为空");
			throw new OaException("参数错误！");
		}
		String[] employeeList = empList.split(",");
		if(employeeList.length < 1){
			logger.error("参数错误！");
			throw new OaException("请选择员工！");
		}
		EmpScheduleGroup empScheduleGroup = new EmpScheduleGroup();
		for (String empStr : employeeList) {
			Long empId = Long.parseLong(empStr);
			empScheduleGroup.setEmpId(empId);
			empScheduleGroup.setGroupId(groupId);
			empScheduleGroup.setDelFlag(0);
			empScheduleGroup.setCreateTime(nowDate);
			empScheduleGroup.setCreateUser(user.getId());
			scheduleGroupMapper.addMember(empScheduleGroup);
			//判断员工当月及以后是否有排班班次
			Date firstDay = DateUtils.getFirstDay(nowDate);
			EmployeeClass condition = new EmployeeClass();
			condition.setStartTime(firstDay);
			condition.setEmployId(empId);
			List<EmployeeClass> empClassList = employeeClassMapper.getEmployeeThisMonthClassList(condition);
			if(empClassList != null && empClassList.size() > 0){
				//当月及以后存在排班，将员工排班班次所属班组修改
				for (EmployeeClass employeeClass : empClassList) {
					EmployeeClass empClass = new EmployeeClass();
					empClass.setId(employeeClass.getId());
					empClass.setGroupId(groupId);
					empClass.setUpdateTime(nowDate);
					empClass.setUpdateUser(user.getCnName());
					employeeClassMapper.updateGroupId(empClass);
				}
			}
		}
		
		logger.info("-------------添加组员成功---------------");
		
	}

	/**
	 * 删除组员
	 * @throws OaException 
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMember(Long empId, Long groupId) throws OaException {
		logger.info("-------------删除组员开始---------------");
		//获取当前登录人
		User user = userService.getCurrentUser();
		Long updateUser = user.getId();
		//获取当前时间
		Date updateTime = DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_LONG), DateUtils.FORMAT_LONG);
		if(empId == null || groupId == null){
			logger.error("参数错误！员工编号或班组id为空");
			throw new OaException("参数错误！");
		}
		scheduleGroupMapper.delMember(empId,groupId,updateUser,updateTime);
		//判断员工当月及以后是否有排班班次
		Date firstDay = DateUtils.getFirstDay(updateTime);
		EmployeeClass condition = new EmployeeClass();
		condition.setStartTime(firstDay);
		condition.setEmployId(empId);
		List<EmployeeClass> empClassList = employeeClassMapper.getEmployeeThisMonthClassList(condition);
		if(empClassList != null && empClassList.size() > 0){
			//当月及以后存在排班，将员工排班班次设为null
			for (EmployeeClass employeeClass : empClassList) {
				EmployeeClass empClass = new EmployeeClass();
				empClass.setId(employeeClass.getId());
				empClass.setGroupId(null);
				empClass.setUpdateTime(updateTime);
				empClass.setUpdateUser(user.getCnName());
				employeeClassMapper.updateGroupId(empClass);
			}
		}
		logger.info("-------------删除组员成功---------------");
	}

	/**
	 * 查询所有需要排班的部门返回列表
	 */
	@Override
	public List<Depart> getScheduleDepartList() {
		return scheduleGroupMapper.getScheduleDepartList();
	}


	@Override
	public HSSFWorkbook exportScheduleTemplate(Long departId,Long groupId){
		
		//获取下月月份
		Date nextMonthDate = DateUtils.addMonth(new Date(), 1);
		String nextMonth = DateUtils.format(nextMonthDate, "yyyy-MM");
		Date fristDay = DateUtils.getFirstDay(nextMonthDate);
		Date lastDay = DateUtils.getLastDay(nextMonthDate);
		//查询班组信息
		ScheduleGroup scheduleGroup = scheduleGroupMapper.getGroupById(groupId);
		//查询该班组下所有员工
		List<Employee> empList = scheduleGroupMapper.getAllGroupEmp(groupId,null,fristDay);
		String sheetName = scheduleGroup.getName()+nextMonth+"月排班";
		HSSFWorkbook workbook = new HSSFWorkbook();
		
		// 表头标题样式
		HSSFCellStyle colstyle = workbook.createCellStyle();
		colstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
		colstyle.setBorderBottom(XSSFCellStyle.BORDER_THIN); //下边框
		colstyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);//左边框
		colstyle.setBorderTop(XSSFCellStyle.BORDER_THIN);//上边框
		colstyle.setBorderRight(XSSFCellStyle.BORDER_THIN);//右边框
		//sheet1
		HSSFSheet sheet1 = workbook.createSheet(sheetName);
		Map<String,List<String>> titleMap = getEmpClassReportTitle(nextMonth);//动态生成表头
		List<String> title1 = titleMap.get("t1");//第一行表头
		List<String> title2 = titleMap.get("t2");//第二行表头
			
		//建立第一行表头
		HSSFRow row = sheet1.createRow((short) 0);
		for(int colIndex = 0; colIndex < title1.size(); colIndex++){
			if(colIndex >=0 && colIndex <= 3){
				sheet1.setColumnWidth(colIndex, 5000);//设置表格宽度
			}
			
			Object obj = title1.get(colIndex);
			if(obj instanceof Long || obj instanceof Double || obj instanceof Integer || obj instanceof Float || obj instanceof BigDecimal){
				ExcelUtil.createRow(row, colIndex, colstyle, HSSFCell.CELL_TYPE_NUMERIC, obj);
			}else{
				ExcelUtil.createRow(row, colIndex, colstyle, HSSFCell.CELL_TYPE_STRING, obj);
			}
    	}
		//建立第二行表头
		row = sheet1.createRow((short) 1);
		//创建第二行表头内容
		for(int colIndex = 4; colIndex < title2.size(); colIndex++){
			if(colIndex >=4){
				sheet1.setColumnWidth(colIndex, 1200);//设置表格宽度
			}else{
				sheet1.setColumnWidth(colIndex, 5000);//设置表格宽度
			}
			
			Object obj = title2.get(colIndex);
			if(obj instanceof Long || obj instanceof Double || obj instanceof Integer || obj instanceof Float || obj instanceof BigDecimal){
				ExcelUtil.createRow(row, colIndex, colstyle, HSSFCell.CELL_TYPE_NUMERIC, obj);
			}else{
				ExcelUtil.createRow(row, colIndex, colstyle, HSSFCell.CELL_TYPE_STRING, obj);
			}
    	}
		//合并单元格
		CellRangeAddress cra1 = new CellRangeAddress(0,1,0,0);
		CellRangeAddress cra2 = new CellRangeAddress(0,1,1,1);
		CellRangeAddress cra3 = new CellRangeAddress(0,1,2,2);
		CellRangeAddress cra4 = new CellRangeAddress(0,1,3,3);
		sheet1.addMergedRegion(cra1);
		sheet1.addMergedRegion(cra2);
		sheet1.addMergedRegion(cra3);
		sheet1.addMergedRegion(cra4);
		//设置合并单元格边框样式
		// 下边框
		RegionUtil.setBorderBottom(1, cra1, sheet1,workbook);
		RegionUtil.setBorderBottom(1, cra2, sheet1,workbook);
		RegionUtil.setBorderBottom(1, cra3, sheet1,workbook);
		RegionUtil.setBorderBottom(1, cra4, sheet1,workbook);
		// 右边框
		RegionUtil.setBorderRight(1, cra1, sheet1,workbook);
		RegionUtil.setBorderRight(1, cra2, sheet1,workbook);
		RegionUtil.setBorderRight(1, cra3, sheet1,workbook);
		RegionUtil.setBorderRight(1, cra4, sheet1,workbook);
		int index = 2;
		//查询当月所有假期计算应出勤工时
		if(empList != null){
			//2.应出勤工时
			Map<Long, Integer> leastAttendanceHours = employeeClassService.getLeastAttendanceHours(empList, fristDay);
			for (Employee employee : empList) {
				row = sheet1.createRow(index);
				//查询员工职位
				Position position = positionService.getByEmpId(employee.getId());
				Integer shouldTime = leastAttendanceHours.get(employee.getId());
				ExcelUtil.createRow(row, 0, null, HSSFCell.CELL_TYPE_STRING,employee.getCode());
				ExcelUtil.createRow(row, 1, null, HSSFCell.CELL_TYPE_STRING,employee.getCnName());
				ExcelUtil.createRow(row, 2, null, HSSFCell.CELL_TYPE_STRING,null == position ? "" : position.getPositionName());
				ExcelUtil.createRow(row, 3, null, HSSFCell.CELL_TYPE_NUMERIC,shouldTime == null ? 0 : shouldTime);
				index++;
			}
		}
			
		ClassSetting classSetting = new ClassSetting();
		//sheet2
		classSetting.setGroupId(groupId);
		List<ClassSetting> classSettingList = classSettingMapper.getListByCondition(classSetting);
		HSSFSheet sheet2 = workbook.createSheet("班次");
		//建立第一行表头
		HSSFRow row2 = sheet2.createRow((short) 0);
		//创建第一行表头内容
		ExcelUtil.createRow(row2, 0, null, HSSFCell.CELL_TYPE_STRING,"班次名称");
		ExcelUtil.createRow(row2, 1, null, HSSFCell.CELL_TYPE_STRING,"简称");
		ExcelUtil.createRow(row2, 2, null, HSSFCell.CELL_TYPE_STRING,"开始时间");
		ExcelUtil.createRow(row2, 3, null, HSSFCell.CELL_TYPE_STRING,"结束时间");
		ExcelUtil.createRow(row2, 4, null, HSSFCell.CELL_TYPE_STRING,"应出勤工时（小时）");
		
		if(classSettingList!=null&&classSettingList.size()>0){
			for(int i=0;i<classSettingList.size();i++){
				row2 = sheet2.createRow((short) (i+1));
				ExcelUtil.createRow(row2, 0, null, HSSFCell.CELL_TYPE_STRING,classSettingList.get(i).getFullName());
				ExcelUtil.createRow(row2, 1, null, HSSFCell.CELL_TYPE_STRING,classSettingList.get(i).getName());
				ExcelUtil.createRow(row2, 2, null, HSSFCell.CELL_TYPE_STRING,DateUtils.format(classSettingList.get(i).getStartTime(), "HH:mm"));
				ExcelUtil.createRow(row2, 3, null, HSSFCell.CELL_TYPE_STRING,DateUtils.format(classSettingList.get(i).getEndTime(), "HH:mm"));
				ExcelUtil.createRow(row2, 4, null, HSSFCell.CELL_TYPE_NUMERIC,(Double)classSettingList.get(i).getMustAttnTime());
			}
		}

		return workbook;
	}


	private Map<Date, Integer> getVacationList(Date fristDay, Date lastDay) {
		AnnualVacation vacation = new AnnualVacation();
		vacation.setStartDate(fristDay);
		vacation.setEndDate(lastDay);
		List<AnnualVacation> vacationList = annualVacationService.getListByCondition(vacation);
		Map<Date,Integer> vacationMap = new HashMap<Date,Integer>();
		for(AnnualVacation va:vacationList){
			vacationMap.put(va.getAnnualDate(), va.getType());
		}
		return vacationMap;
	}
	private Double getShouldTime(Date startTime,Date endTime,Map<Date,Integer> vacationMap){
		
		boolean isEndTime1 = true;
	    double should_time = 0;
	    do{
	    	if(annualVacationService.judgeWorkOrNot(startTime,vacationMap)){//判断是否是周末或者节假日
	    		should_time += 8;
			}
	    	startTime = DateUtils.addDay(startTime, 1);
			if(DateUtils.getIntervalDays(startTime, endTime) < 0) {
				isEndTime1 = false;
			}
		} while(isEndTime1);
	    
		return should_time;
	
	}
	public Map<String,List<String>> getEmpClassReportTitle(String yearMonth){
		Map<String,List<String>> titleMap = new HashMap<String,List<String>>();		
		String[] titlesArray1 = {"员工编号","姓名", "职位", "应出勤工时"};
		String[] titlesArray2 = {"","","","","1", "2", "3" ,"4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", 
				"16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27"};
		List<String> titles1 = new ArrayList<String>(Arrays.asList(titlesArray1));
		List<String> titles2 = new ArrayList<String>(Arrays.asList(titlesArray2));
		//获得当月最大天数
		Calendar cal = Calendar.getInstance();
		cal.setTime(DateUtils.parse(yearMonth + "-01 00:00:00"));
		Integer maxDay = cal.getActualMaximum(Calendar.DATE);
		
		//根据最大天数生成日期
		if(maxDay == 28){
			titles2.add("28");
		}else if(maxDay == 29){
			titles2.add("28");
			titles2.add("29");
		}else if(maxDay == 30){
			titles2.add("28");
			titles2.add("29");
			titles2.add("30");
		}else{
			titles2.add("28");
			titles2.add("29");
			titles2.add("30");
			titles2.add("31");
		}
		
		titleMap.put("t2", titles2);
		
		//动态生成第二行标题
		String date = "";
		for(int index = 4;index < titles2.size();index++){
			date = titles2.get(index);
			if(StringUtils.isNotBlank(date)){
				String week = DateUtils.getWeek(DateUtils.parse(yearMonth + "-" + date + " 00:00:00"));
				titles1.add(week);
			}
		}
		titleMap.put("t1", titles1);
		
		return titleMap;
	}

	@Override
	public List<ScheduleGroup> getListByScheduler(Long scheduler) {
		return scheduleGroupMapper.getListByScheduler(scheduler);
	}


	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, String> importScheduleTemplate(MultipartFile file,Long departId,Long groupId) throws OaException, Exception {
		logger.info("导入排班-----开始");
		// 获取当前登录人
		User user = userService.getCurrentUser();
		//返回结果
		Map<String, String> resultMap = new HashMap<String, String>();
		// 判断文件是否存在
		if (null == file) {
			logger.error("文件不存在！");
			throw new OaException("文件不存在！");
		}
		if(departId == null || groupId == null){
			logger.error("参数错误！");
			throw new OaException("请选择排班班组！");
		}
		// 获得文件名
		String fileName = file.getOriginalFilename();
		/**1.判断文件是否是excel文件*/
		if (!fileName.endsWith("xls") && !fileName.endsWith("xlsx")) {
			logger.error(fileName + "不是excel文件");
			throw new OaException("请上传标准表格文件！");
		}
//		/**2.判断用户是否排班人*/
//		ScheduleGroup scheduleGroup = scheduleGroupMapper.getGroupById(groupId);
//		if(!scheduleGroup.getScheduler() .equals(user.getEmployee().getId()) ){
//			logger.error("没有排班权限！");
//			throw new OaException("没有排班权限！");
//		}
		/**3.判断下月是否有排班*/
		//获取下月月份
		Date nextMonthDate = DateUtils.addMonth(new Date(), 1);
		String nextMonth = DateUtils.format(nextMonthDate, "yyyy-MM");
		Date firstDay = DateUtils.getFirstDay(nextMonthDate);
		Date lastDay = DateUtils.getLastDay(nextMonthDate);
		//查询下月排班总数
		int count = employeeClassMapper.getClassCountByMonth(groupId,firstDay,lastDay);
		if(count > 0){
			logger.error("下月已有排班！");
			throw new OaException("下月已有排班！");
		}
		/**4.判断是否存在已提交的申请单*/
		//查询下月排班申请单
		ApplicationEmployeeClass condition  = new ApplicationEmployeeClass();
		condition.setDepartId(departId);
		condition.setGroupId(groupId);
		condition.setClassMonth(firstDay);
		//状态为已拒绝，已撤回，失效拒绝的可再次提交生成申请单
		ArrayList<Integer> approvalStatusNoList = new ArrayList<Integer>();
		approvalStatusNoList.add(ConfigConstants.REFUSE_STATUS);
		approvalStatusNoList.add(ConfigConstants.BACK_STATUS);
		approvalStatusNoList.add(ConfigConstants.OVERDUEREFUSE_STATUS);
		condition.setApprovalStatusNoList(approvalStatusNoList);
		//查询是否有未提交的申请单（即approvalStatus为null）同时查询是否有审批通过或超时的单据
		List<ApplicationEmployeeClass> applicationClassList = applicationEmployeeClassMapper.getNotSubmittedClass(condition);
		if(applicationClassList != null && applicationClassList.size() > 0){
			for (ApplicationEmployeeClass applicationClass : applicationClassList) {
				if(applicationClass.getApprovalStatus() != null && (applicationClass.getApprovalStatus().equals(ConfigConstants.DOING_STATUS) 
						|| applicationClass.getApprovalStatus().equals(ConfigConstants.PASS_STATUS)
						|| applicationClass.getApprovalStatus().equals(ConfigConstants.OVERDUE_STATUS)
						|| applicationClass.getApprovalStatus().equals(ConfigConstants.OVERDUEPASS_STATUS))){
						logger.error("已提交排班无法再次上传！");
						throw new OaException("已提交排班无法再次上传！");
					}
				
			}
		}
		// 读取文件转换为实体类
		List<List<Object>> excelList = ExcelUtil.readExcel(file, fileName, 34);
		/**5.判断表中员工与数量是否与班组内员工一致*/
		//查询表格中的所有员工
		ArrayList<Long> excelEmpList = new ArrayList<Long>();
		List<LinkedList<String>> excel = (List<LinkedList<String>>) (List) excelList;
		//去除所有空格
		for (LinkedList<String> arrayList : excel) {
			replaceListBlank(arrayList);
		}
		for(int i = 2; i < excel.size(); i++){
			String empCode = excel.get(i).get(0);
			if(StringUtils.isNotBlank(empCode)){
				Long empId = employeeMapper.getEmpIdByCode(empCode);
				excelEmpList.add(empId);
			}
		}
		//查询班组下所有员工
		List<Long> groupEmpList = scheduleGroupMapper.getAllEmpIdByGroupId(groupId,condition.getClassMonth());
		if(excelEmpList == null || groupEmpList == null || excelEmpList.size() != groupEmpList.size() || !excelEmpList.containsAll(groupEmpList)){
			logger.error("排班组别人员已变更，请下载最新模板进行排班！");
			throw new OaException("排班组别人员已变更，请下载最新模板进行排班！");
		}
		/**6.判断表中排班是否为下月排班*/
		//获取日
		String lastDayStr = DateUtils.getDayOfMonth(lastDay);
		//获取表头日期栏最后一天
		LinkedList<String> excelDayList = excel.get(1);
		String excelLastDay = null;
		for(int i = excelDayList.size()-1; i >= 0; i--){
			if(StringUtils.isNotBlank(excelDayList.get(i))){
				excelLastDay = excelDayList.get(i);
				break;
			}
		}
		
		//比较日期是否相同
		if(StringUtils.isBlank(lastDayStr) || StringUtils.isBlank(excelLastDay) || !lastDayStr.equals(excelLastDay)){
			logger.error("月份变更，请下载最新排班模板进行排班！");
			throw new OaException("月份变更，请下载最新排班模板进行排班！");
		}
		if(applicationClassList != null && applicationClassList.size() > 0){
			for (ApplicationEmployeeClass applicationEmployeeClass : applicationClassList) {
				//未提交审核的申请单再次导入会修改原申请单
				if(applicationEmployeeClass.getApprovalStatus() == null){
					//修改主表数据
					applicationEmployeeClass.setClassMonth(firstDay);
					applicationEmployeeClass.setEmployeeNum(groupEmpList.size());
					applicationEmployeeClass.setClassSettingPerson(user.getEmployee().getCnName());
					applicationEmployeeClass.setUpdateTime(new Date());
					applicationEmployeeClass.setUpdateUser(user.getEmployee().getCnName());
					applicationEmployeeClass.setIsMove(0);
					applicationEmployeeClass.setEmployeeId(user.getEmployee().getId());
					applicationEmployeeClassMapper.updateById(applicationEmployeeClass);
					Long attnApplicationEmployClassId = applicationEmployeeClass.getId();
					saveApplicationEmpClassDetail(user, nextMonth, excel, attnApplicationEmployClassId);
				}
			}
		}else{
			//新建申请单
			Depart depart = departMapper.getById(departId);
			ApplicationEmployeeClass applicationEmployeeClass = new ApplicationEmployeeClass();
			applicationEmployeeClass.setDepartId(departId);
			applicationEmployeeClass.setGroupId(groupId);
			applicationEmployeeClass.setDepartName(depart.getName());
			applicationEmployeeClass.setClassMonth(firstDay);
			applicationEmployeeClass.setEmployeeNum(groupEmpList.size());
			applicationEmployeeClass.setClassEmployeeNum(groupEmpList.size());
			applicationEmployeeClass.setClassSettingPerson(user.getEmployee().getCnName());
			applicationEmployeeClass.setVersion(0L);
			applicationEmployeeClass.setDelFlag(0);
			applicationEmployeeClass.setCreateTime(new Date());
			applicationEmployeeClass.setCreateUser(user.getEmployee().getCnName());
			applicationEmployeeClass.setIsMove(0);
			applicationEmployeeClass.setEmployeeId(user.getEmployee().getId());
			applicationEmployeeClassMapper.save(applicationEmployeeClass);
			Long attnApplicationEmployClassId = applicationEmployeeClass.getId();
			saveApplicationEmpClassDetail(user, nextMonth, excel, attnApplicationEmployClassId);
			
		}
		resultMap.put("result", "success");
		resultMap.put("resultMsg", "导入成功！");
		return resultMap;
	}


	private void saveApplicationEmpClassDetail(User user, String nextMonth, List<LinkedList<String>> excel,
			Long attnApplicationEmployClassId) {
		/**计算应出勤工时（此字段不从表中读取，重新计算）*/
		/**1.获取月初月末日期*/
		Date nextMonthDate = DateUtils.parse(nextMonth, DateUtils.FORMAT_YYYY_MM);
		Date fristDay = DateUtils.getFirstDay(nextMonthDate);
		Date lastDay = DateUtils.getLastDay(nextMonthDate);
		/**2.查询当月所有假期*/
		Map<Date, Integer> vacationMap = getVacationList(fristDay, lastDay);
		//修改详情表数据
		//读取excel内容
		for(int i = 2; i < excel.size(); i++){
			//遍历每个员工
			Long empId = employeeMapper.getEmpIdByCode(excel.get(i).get(0));
			Employee emp = employeeMapper.getById(empId);
			/**3.1默认计算整月*/
			Date startDate = fristDay;
			Date endDate = lastDay;
			/**3.2判断员工是否在排班月入职或离职*/
			Date firstEntryTime = emp.getFirstEntryTime();
			Date quitTime = emp.getQuitTime();
			if(firstEntryTime != null && firstEntryTime.after(fristDay) && firstEntryTime.before(lastDay)){
				//在排班月入职
				startDate = firstEntryTime;
			}
			if(quitTime != null && quitTime.after(fristDay) && quitTime.before(lastDay)){
				endDate = quitTime;
			}
			/**4.得出结果*/
			Double shouldTime = getShouldTime(startDate, endDate, vacationMap);
			LinkedList<String> linkedList = excel.get(i);
			//排班数据从表中第4列开始
			for (int j = 4; j < linkedList.size(); j++) {
				//根据名称查询班次id
				Long classSettingId = classSettingMapper.getIdByName(linkedList.get(j));
				//拼接日期
				String classDateStr = nextMonth + "-" +(j-3);
				Date classDate = DateUtils.parse(classDateStr, DateUtils.FORMAT_SHORT);
				ApplicationEmployeeClassDetail applicationEmployeeClassDetail = new ApplicationEmployeeClassDetail();
				applicationEmployeeClassDetail.setClassDate(classDate);
				applicationEmployeeClassDetail.setEmployId(empId);
				applicationEmployeeClassDetail.setAttnApplicationEmployClassId(attnApplicationEmployClassId);
				//查询该员工当天是否存在排班详情
				List<ApplicationEmployeeClassDetail> isInit = applicationEmployeeClassDetailMapper.selectByCondition(applicationEmployeeClassDetail);
				List<ApplicationEmployeeClassDetail> batch = new ArrayList<ApplicationEmployeeClassDetail>();
				//存在
				if(isInit!=null && isInit.size()>0){
					applicationEmployeeClassDetail.setClassSettingId(classSettingId);
					applicationEmployeeClassDetail.setUpdateTime(new Date());
					applicationEmployeeClassDetail.setUpdateUser(user.getEmployee().getCnName());
					applicationEmployeeClassDetailMapper.updateByCondition(applicationEmployeeClassDetail);
				}else{
					//不存在则新建
					applicationEmployeeClassDetail.setAttnApplicationEmployClassId(attnApplicationEmployClassId);
					applicationEmployeeClassDetail.setClassSettingId(classSettingId);
					applicationEmployeeClassDetail.setCreateTime(new Date());
					applicationEmployeeClassDetail.setCreateUser(user.getEmployee().getCnName());
					applicationEmployeeClassDetail.setClassSettingPerson(user.getEmployee().getCnName());
					applicationEmployeeClassDetail.setDelFlag(0);
					applicationEmployeeClassDetail.setVersion(0L);
					applicationEmployeeClassDetail.setIsMove(0);
					applicationEmployeeClassDetail.setCompanyId(user.getCompanyId());
					applicationEmployeeClassDetail.setEmployName(emp.getCnName());
					applicationEmployeeClassDetail.setShouldTime(shouldTime);
					batch.add(applicationEmployeeClassDetail);
				}
				if(batch!=null&&batch.size()>0){
					applicationEmployeeClassDetailMapper.batchSave(batch);
				}
			}
		}
	}
	/**
	 * 去除所有空格回车换行符制表符
	 * 
	 * @param str
	 * @return
	 */
	public static LinkedList<String> replaceListBlank(LinkedList<String> arrayList) {
		String dest = "";
		LinkedList<String> resultList = new LinkedList<String>();
		if (arrayList != null && arrayList.size() > 0) {
			for (String str : arrayList) {
				if (str != null) {
					// 空格\t、回车\n、换行符\r、制表符\t
					Matcher m = ConfigConstants.REPLACE_BLACK_PATTERN.matcher(str);
					dest = m.replaceAll("");
					resultList.add(dest);
				}
			}
		}
		return resultList;
	}


	@Override
	public List<ScheduleGroup> getScheduleList() {
		// TODO Auto-generated method stub
		return scheduleGroupMapper.getAllListByCondition(null);
	}


	@Override
	public void deleteScheduleGroup(Long groupId) throws OaException {
		List<Employee> empList = scheduleGroupMapper.getAllGroupEmp(groupId,null,null);
		if(groupId==null){
			throw new OaException("删除失败，请刷新页面重试！");
		}
		if(empList!=null&&empList.size()>0){
			throw new OaException("分组中存在排班人员请先移除后，再删除排班分组！");
		}
		User user = userService.getCurrentUser();
		ScheduleGroup scheduleGroup = new ScheduleGroup();
		scheduleGroup.setDelFlag(1);
		scheduleGroup.setCreateTime(new Date());
		scheduleGroup.setCreateUser(user.getEmployee().getId());
		scheduleGroup.setId(groupId);
		scheduleGroupMapper.updateScheduleGroup(scheduleGroup);
	}
	
	@Override
	public PageModel<Employee> getEmpClassListByCondition(Employee empClass) {
		
		int page = empClass.getPage() == null ? 0 : empClass.getPage();
		int rows = empClass.getRows() == null ? 0 : empClass.getRows();
		
		PageModel<Employee> pm = new PageModel<Employee>();
		
		pm.setPageNo(page);
		pm.setPageSize(rows);
		
		empClass.setOffset(pm.getOffset());
		empClass.setLimit(pm.getLimit());
		
		Integer total = scheduleGroupMapper.getEmpClassListByConditionCount(empClass);
		pm.setTotal(total);
		pm.setRows(scheduleGroupMapper.getEmpClassListByCondition(empClass));
		return pm;
	}
	

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateDepartWhetherScheduling(Depart depart) {
		User user = userService.getCurrentUser();
		depart.setUpdateTime(new Date());
		depart.setUpdateUser(user.getEmployee().getCnName());
		int count = departMapper.updateWhetherScheduling(depart);
		return count;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void updateEmpClassInfo(String isWhetherScheduling,String empClassIds) {
		// TODO Auto-generated method stub
		User user = userService.getCurrentUser();
		Long whetherSchedulingYes =null;
		Long whetherSchedulingNo =null;
		Long whetherScheduling =null;
		
		Config configCondition = new Config();
		configCondition.setCode("whetherScheduling");
		List<Config> whetherSchedulingList = configService.getListByCondition(configCondition);
        for(Config config:whetherSchedulingList){
			if("yes".equals(config.getDisplayCode())){
				whetherSchedulingYes = config.getId();
			}
			if("no".equals(config.getDisplayCode())){
				whetherSchedulingNo = config.getId();
			}
		}
        if("yes".equals(isWhetherScheduling)){
        	whetherScheduling=whetherSchedulingYes;
        }else{
        	whetherScheduling=whetherSchedulingNo;
        }
        //批量修改 员工排班
  		String pageStr = empClassIds;
  		logger.info("修改排班员工属性为" + ("yes".equals(isWhetherScheduling)?"是":"否")+"修改人:"+ user.getEmployee().getCnName()+ " 员工id集合为={}", pageStr+ "");
  		String[] updateEmpClass = pageStr.split(",");
  		if(updateEmpClass.length>0){
  			for (int i = 0; i < updateEmpClass.length; i++) { 
  				Employee employee =new Employee();
  				employee.setId(Long.parseLong(updateEmpClass[i]));
  				employee.setWhetherScheduling(whetherScheduling);
  				employee.setUpdateTime(new Date());
  				employeeService.updateById(employee);
  				//改为 不排班  删除组别
  	  			if("no".equals(isWhetherScheduling)){
  	  				scheduleGroupMapper.delMember(Long.parseLong(updateEmpClass[i]),null,user.getId(),new Date());
  	  			}
  			}
  			if("no".equals(isWhetherScheduling)){
  				logger.info("排班属性改为否 删除员工排班组别 员工id集合为={}", pageStr+ "");
  			}
  			
  		}
  		logger.info("修改排班员工属性成功" );		
	}


	@Override
	public HSSFWorkbook exportChangeClassTemplate(String month, Long departId,
			Long groupId) {
		ApplicationEmployeeClass employClass = new ApplicationEmployeeClass();
		Date now = new Date();
		if(month==null||"".equals(month)){
			month = DateUtils.format(now, "yyyy-MM");
		}else{
			now = DateUtils.parse(month+"-01", DateUtils.FORMAT_SHORT);
		}
		Date fristDay = DateUtils.getFirstDay(now);
		Date lastDay = DateUtils.getLastDay(now);
		employClass.setStartTime(fristDay);
		employClass.setEndTime(lastDay);
		employClass.setDepartId(departId);
		if(groupId!=null){
			employClass.setGroupId(groupId);
		}
		
		List<Map<String,Object>> datas = applicationEmployeeClassMapper.getEmpClassReprotById(employClass);
		
		String departName = departService.getDepartAllLeaveName(departId);
		String fileName = departName+"-"+DateUtils.format(fristDay, "yyyyMM");
		String excelName = fileName;
		HSSFWorkbook workbook = new HSSFWorkbook();
		
		HSSFSheet sheet = workbook.createSheet(excelName);
		if (datas != null && datas.size() > 0) {
			String yearMonth = (String)datas.get(0).get("yearMonth");
			Map<String,List<String>> titleMap = getEmpClassReportTitle(yearMonth);//动态生成表头
			List<String> title2 = titleMap.get("t1");//第二行表头
			List<String> title3 = titleMap.get("t2");//第三行表头
			// 表头标题样式
			HSSFCellStyle colstyle = workbook.createCellStyle();
			colstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
			colstyle.setBorderBottom(XSSFCellStyle.BORDER_THIN); //下边框
			colstyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);//左边框
			colstyle.setBorderTop(XSSFCellStyle.BORDER_THIN);//上边框
			colstyle.setBorderRight(XSSFCellStyle.BORDER_THIN);//右边框
			
			//建立第一行表头
			HSSFRow row = sheet.createRow((short) 0);
			//创建第一行表头内容
			ExcelUtil.createRow(row, 0, null, HSSFCell.CELL_TYPE_STRING,yearMonth+"排班表");
			sheet.addMergedRegion(new CellRangeAddress(0,0,0,3));
			
			//建立第二行表头
			row = sheet.createRow((short) 1);
			//创建第二行表头内容
			for(int colIndex = 0; colIndex < title2.size(); colIndex++){
				if(colIndex >=0 && colIndex <= 3){
					sheet.setColumnWidth(colIndex, 5000);//设置表格宽度
				}
				
				Object obj = title2.get(colIndex);
				if(obj instanceof Long || obj instanceof Double || obj instanceof Integer || obj instanceof Float || obj instanceof BigDecimal){
					ExcelUtil.createRow(row, colIndex, colstyle, HSSFCell.CELL_TYPE_NUMERIC, obj);
				}else{
					ExcelUtil.createRow(row, colIndex, colstyle, HSSFCell.CELL_TYPE_STRING, obj);
				}
        	}
			sheet.addMergedRegion(new CellRangeAddress(1,2,0,0));
    		sheet.addMergedRegion(new CellRangeAddress(1,2,1,1));
    		sheet.addMergedRegion(new CellRangeAddress(1,2,2,2));
    		sheet.addMergedRegion(new CellRangeAddress(1,2,3,3));
			
			//建立第三行表头(从第二行开始算，第一行表头是动态生成的数据)
			row = sheet.createRow((short) 2);
			//创建第三行表头内容
			for(int colIndex = 4; colIndex < title3.size(); colIndex++){
				if(colIndex >=4 && colIndex <= title3.size() - 2){
					sheet.setColumnWidth(colIndex, 1200);//设置表格宽度
				}else{
					sheet.setColumnWidth(colIndex, 5000);//设置表格宽度
				}
				
				Object obj = title3.get(colIndex);
				if(obj instanceof Long || obj instanceof Double || obj instanceof Integer || obj instanceof Float || obj instanceof BigDecimal){
					ExcelUtil.createRow(row, colIndex, colstyle, HSSFCell.CELL_TYPE_NUMERIC, obj);
				}else{
					ExcelUtil.createRow(row, colIndex, colstyle, HSSFCell.CELL_TYPE_STRING, obj);
				}
        	}
			
			Long empId = null;
			int index = 3;
			for(Map<String,Object> data : datas){
				Long classDay = Long.valueOf(String.valueOf(data.get("classDay")));
				
				if(null != empId && empId.equals(data.get("empId"))){//是同一个员工记录
					ExcelUtil.createRow(row, Integer.valueOf(classDay+"") + 3, null, HSSFCell.CELL_TYPE_STRING,data.get("name"));//排班工时
				}else{
					row = sheet.createRow((short) index);
					ExcelUtil.createRow(row, Integer.valueOf(classDay+"") + 3, null, HSSFCell.CELL_TYPE_STRING,data.get("name"));//排班工时
					
					Position position = positionService.getByEmpId((Long)data.get("empId"));
					ExcelUtil.createRow(row, 0, null, HSSFCell.CELL_TYPE_STRING,data.get("code"));
					ExcelUtil.createRow(row, 1, null, HSSFCell.CELL_TYPE_STRING,data.get("empName"));
					ExcelUtil.createRow(row, 2, null, HSSFCell.CELL_TYPE_STRING,null == position ? "" : position.getPositionName());
					ExcelUtil.createRow(row, 3, null, HSSFCell.CELL_TYPE_NUMERIC,data.get("shouldTime"));//应出勤工时
					empId = (Long)data.get("empId");
					index++;
				}
			}
		} else {
			ExcelUtil.createRow(sheet.createRow(1), 0, null,HSSFCell.CELL_TYPE_STRING, "无数据");
		}
		
		ClassSetting classSetting = new ClassSetting();
		classSetting.setGroupId(groupId);
		List<ClassSetting> classSettingList = classSettingMapper.getListByCondition(classSetting);
		
		HSSFSheet sheet1 = workbook.createSheet("班次");
		//建立第一行表头
		HSSFRow row = sheet1.createRow((short) 0);
		//创建第一行表头内容
		ExcelUtil.createRow(row, 0, null, HSSFCell.CELL_TYPE_STRING,"班次名称");
		ExcelUtil.createRow(row, 1, null, HSSFCell.CELL_TYPE_STRING,"简称");
		ExcelUtil.createRow(row, 2, null, HSSFCell.CELL_TYPE_STRING,"开始时间");
		ExcelUtil.createRow(row, 3, null, HSSFCell.CELL_TYPE_STRING,"结束时间");
		ExcelUtil.createRow(row, 4, null, HSSFCell.CELL_TYPE_STRING,"应出勤工时（小时）");
		
		if(classSettingList!=null&&classSettingList.size()>0){
			for(int i=0;i<classSettingList.size();i++){
				row = sheet1.createRow((short) (i+1));
				ExcelUtil.createRow(row, 0, null, HSSFCell.CELL_TYPE_STRING,classSettingList.get(i).getFullName());
				ExcelUtil.createRow(row, 1, null, HSSFCell.CELL_TYPE_STRING,classSettingList.get(i).getName());
				ExcelUtil.createRow(row, 2, null, HSSFCell.CELL_TYPE_STRING,DateUtils.format(classSettingList.get(i).getStartTime(), "HH:mm"));
				ExcelUtil.createRow(row, 3, null, HSSFCell.CELL_TYPE_STRING,DateUtils.format(classSettingList.get(i).getEndTime(), "HH:mm"));
				ExcelUtil.createRow(row, 4, null, HSSFCell.CELL_TYPE_NUMERIC,(Double)classSettingList.get(i).getMustAttnTime());
			}
		}

		return workbook;
	}


	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, String> importChangeClassTemplate(MultipartFile file,
			String month,Long departId, Long groupId) throws OaException, Exception {
		// 获取当前登录人
		User user = userService.getCurrentUser();
		if(user==null||user.getEmployee()==null){
			throw new OaException("请重新登录！");
		}
		logger.info(user.getEmployee().getCnName()+"导入调班-----开始");
		//返回结果
		Map<String, String> resultMap = new HashMap<String, String>();
		// 判断文件是否存在
		if (null == file) {
			logger.error("文件不存在！");
			throw new OaException("文件不存在！");
		}
		if(StringUtils.isBlank(month)|| departId == null || groupId == null){
			logger.error("参数错误！");
			throw new OaException("请选择调班月份和调班班组！");
		}
		// 获得文件名
		String fileName = file.getOriginalFilename();
		/**1.判断文件是否是excel文件*/
		if (!fileName.endsWith("xls") && !fileName.endsWith("xlsx")) {
			logger.error(fileName + "不是excel文件");
			throw new OaException("请上传标准表格文件！");
		}
		/**3.判断下月是否有排班*/
		//获取下月月份
		Date nextMonthDate = DateUtils.parse(month, "yyyy-MM");
		String nextMonth = month;
		Date firstDay = DateUtils.getFirstDay(nextMonthDate);
		Date lastDay = DateUtils.getLastDay(nextMonthDate);

		/**4.判断是否存在已提交的申请单*/
		//查询下月排班申请单
		ApplicationEmployeeClass condition  = new ApplicationEmployeeClass();
		condition.setDepartId(departId);
		condition.setGroupId(groupId);
		condition.setClassMonth(firstDay);

		// 读取文件转换为实体类
		List<List<Object>> excelList = ExcelUtil.readExcel(file, fileName, 34);
		/**5.判断表中员工与数量是否与班组内员工一致*/
		//查询表格中的所有员工
		ArrayList<Long> excelEmpList = new ArrayList<Long>();
		List<LinkedList<String>> excel = (List<LinkedList<String>>) (List) excelList;
		//去除所有空格
		for (LinkedList<String> arrayList : excel) {
			replaceListBlank(arrayList);
		}
		for(int i = 2; i < excel.size(); i++){
			String empCode = excel.get(i).get(0);
			if(StringUtils.isNotBlank(empCode)){
				Long empId = employeeMapper.getEmpIdByCode(empCode);
				excelEmpList.add(empId);
			}
		}
		//查询班组下所有员工
		List<Long> groupEmpList = scheduleGroupMapper.getAllEmpIdByGroupId(groupId,condition.getClassMonth());
		if(excelEmpList == null || groupEmpList == null || excelEmpList.size() != groupEmpList.size() || !excelEmpList.containsAll(groupEmpList)){
			logger.error("排班组别人员已变更，请下载最新模板进行排班！");
			throw new OaException("排班组别人员已变更，请下载最新模板进行排班！");
		}
		
		//查询该组别下面所有员工现有班次
		ClassSetting classSetting = new ClassSetting();
		List<ClassSetting> classSettingList = classSettingMapper.getListByCondition(classSetting);
		Map<String,ClassSetting> classSetMap = classSettingList.stream().collect(Collectors.toMap(ClassSetting :: getName,a->a,(k1,k2)->k1));
		//查询该组别下面所有员工已有排班
		EmployeeClass areadyParam = new EmployeeClass();
		areadyParam.setStartTime(firstDay);
		areadyParam.setEndTime(lastDay);
		areadyParam.setEmployeeIds(groupEmpList);
		List<EmployeeClass> areadyList = employeeClassMapper.getEmployeeClassList(areadyParam);
		Map<String,EmployeeClass> areadyMap = new HashMap<String,EmployeeClass>();
		for(EmployeeClass data:areadyList){
			areadyMap.put(String.valueOf(data.getEmployId())+"_"+DateUtils.format(data.getClassDate(), "dd"), data);
		}
		List<ApplicationEmployeeClassDetail> updateList = new ArrayList<ApplicationEmployeeClassDetail>();
		
		//判断操作人是否是人事(人事可以调整个月的班次，不是人事只能调当天及之后的班次)
		boolean isHr = false;
	   
		if(user.getPosition()!=null&&("人力资源及行政总监".equals(user.getPosition().getPositionName())||"企业文化专员".equals(user.getPosition().getPositionName())
				||"人力资源高级经理".equals(user.getPosition().getPositionName())||"HRBP专员".equals(user.getPosition().getPositionName())
				||"高级HRBP".equals(user.getPosition().getPositionName())||"HRBP".equals(user.getPosition().getPositionName())
				||"HRBP助理".equals(user.getPosition().getPositionName())||"人力资源助理".equals(user.getPosition().getPositionName())
				||"薪资福利高级专员".equals(user.getPosition().getPositionName())||"高级企业文化专员".equals(user.getPosition().getPositionName()))){
			isHr = true;
		}
		//控制调班开始日期
		int startNum = 0;
		if(!isHr){
			 if(Integer.valueOf(month.replaceAll("-", ""))<Integer.valueOf(DateUtils.format(new Date(), "yyyyMM"))){
				 throw new OaException("只能调整当月及之后的班次！");
			 }
			 startNum = Integer.valueOf(month.replaceAll("-", "")).equals(Integer.valueOf(DateUtils.format(new Date(), "yyyyMM")))
					 ?Integer.valueOf(DateUtils.format(new Date(), "dd"))-1:0;
		}
		
		boolean containLeagl = false;//调班是否包含法定假日
		int moveNum = 0;
		//节假日前四个工作日
		Config configCondition = new Config();
		configCondition.setCode("dutyCommitTimeLimit");
		List<Config> limit = configService.getListByCondition(configCondition);
		int num = Integer.valueOf(limit.get(0).getDisplayCode());
		
		/**2.查询当月所有假期*/
		Map<Date, Integer> vacationMap = getVacationList(firstDay, lastDay);
		//修改详情表数据
		//读取excel内容
		for(int i = 3; i < excel.size(); i++){ 
			boolean moveSuccess = false;
			//遍历每个员工
			Long empId = employeeMapper.getEmpIdByCode(excel.get(i).get(0));
			Employee emp = employeeMapper.getById(empId);
			/**3.1默认计算整月*/
			Date startDate = firstDay;
			Date endDate = lastDay;
			/**3.2判断员工是否在排班月入职或离职*/
			Date firstEntryTime = emp.getFirstEntryTime();
			Date quitTime = emp.getQuitTime();
			if(firstEntryTime != null && firstEntryTime.after(firstDay) && firstEntryTime.before(lastDay)){
				//在排班月入职
				startDate = firstEntryTime;
			}
			if(quitTime != null && quitTime.after(firstDay) && quitTime.before(lastDay)){
				endDate = quitTime;
			}
			
			/**4.得出结果*/
			Double shouldTime = getShouldTime(startDate, endDate, vacationMap);
			
			LinkedList<String> linkedList = excel.get(i);
			//排班数据从表中第4列开始
			for (int j = 4+startNum; j < linkedList.size(); j++) {
				//查出execl表里的班次
				Long classSettingId = StringUtils.isNotBlank(linkedList.get(j))?classSetMap.get(linkedList.get(j)).getId():null;
				String dateStr = String.valueOf(j-3);
				if(j-3<10){
					dateStr ="0" + String.valueOf(j-3);
				}
				//查询该员工已排班次
				EmployeeClass areadyClass = areadyMap.get(empId+"_"+dateStr);
				String classDateStr = nextMonth + "-" +(j-3);
				Date classDate = DateUtils.parse(classDateStr, DateUtils.FORMAT_SHORT);
				//判断该员工当天班次是否有变动
				if(classSettingId!=null){
					//execl表里班次存在
					//判断已排的班次
					if(areadyClass!=null&&areadyClass.getClassSettingId()!=null){
						if(classSettingId.longValue()==areadyClass.getClassSettingId().longValue()){
							//班次未发生变化，不做处理
						}else{
							//班次发生变化
							ApplicationEmployeeClassDetail applicationEmployeeClassDetail = new ApplicationEmployeeClassDetail();
							applicationEmployeeClassDetail.setCompanyId(user.getCompanyId());
							applicationEmployeeClassDetail.setEmployId(empId);
							applicationEmployeeClassDetail.setCreateTime(new Date());
							applicationEmployeeClassDetail.setCreateUser(user.getEmployee().getCnName());
							applicationEmployeeClassDetail.setClassSettingId(classSettingId);
							applicationEmployeeClassDetail.setClassSettingPerson(user.getEmployee().getCnName());
							applicationEmployeeClassDetail.setClassDate(classDate);
							applicationEmployeeClassDetail.setDelFlag(0);
							applicationEmployeeClassDetail.setIsMove(1);
							//原先班次记录
							ApplicationEmployeeClassDetail oldSchedule = new ApplicationEmployeeClassDetail();
							oldSchedule.setCompanyId(user.getCompanyId());
							oldSchedule.setEmployId(empId);
							oldSchedule.setCreateTime(new Date());
							oldSchedule.setCreateUser(user.getEmployee().getCnName());
							oldSchedule.setClassSettingId(areadyClass.getClassSettingId());
							oldSchedule.setClassSettingPerson(user.getEmployee().getCnName());
							oldSchedule.setClassDate(classDate);
							oldSchedule.setDelFlag(0);
							oldSchedule.setIsMove(0);
							updateList.add(applicationEmployeeClassDetail);
							updateList.add(oldSchedule);
							if(vacationMap!=null&&vacationMap.containsKey(classDate)&&vacationMap.get(classDate)==3){
								containLeagl= true;
								if(!isHr){
									Date lastSubDate = annualVacationService.getWorkingDayPre(num,classDate);
									if(DateUtils.getIntervalDays(new Date(),lastSubDate)<0){
										throw new OaException("调班日期包含法定节假日，只能前4个工作日提交！");
									}
								}
							}
							moveSuccess = true;
						}
					}else{
						ApplicationEmployeeClassDetail applicationEmployeeClassDetail = new ApplicationEmployeeClassDetail();
						applicationEmployeeClassDetail.setCompanyId(user.getCompanyId());
						applicationEmployeeClassDetail.setEmployId(empId);
						applicationEmployeeClassDetail.setCreateTime(new Date());
						applicationEmployeeClassDetail.setCreateUser(user.getEmployee().getCnName());
						applicationEmployeeClassDetail.setClassSettingId(classSettingId);
						applicationEmployeeClassDetail.setClassSettingPerson(user.getEmployee().getCnName());
						applicationEmployeeClassDetail.setClassDate(classDate);
						applicationEmployeeClassDetail.setDelFlag(0);
						applicationEmployeeClassDetail.setIsMove(1);
						//原先班次记录
						ApplicationEmployeeClassDetail oldSchedule = new ApplicationEmployeeClassDetail();
						oldSchedule.setCompanyId(user.getCompanyId());
						oldSchedule.setEmployId(empId);
						oldSchedule.setCreateTime(new Date());
						oldSchedule.setCreateUser(user.getEmployee().getCnName());
						oldSchedule.setClassSettingId(null);
						oldSchedule.setClassSettingPerson(user.getEmployee().getCnName());
						oldSchedule.setClassDate(classDate);
						oldSchedule.setDelFlag(0);
						oldSchedule.setIsMove(0);
						updateList.add(applicationEmployeeClassDetail);
						updateList.add(oldSchedule);
						if(vacationMap!=null&&vacationMap.containsKey(classDate)&&vacationMap.get(classDate)==3){
							containLeagl= true;
							if(!isHr){
								Date lastSubDate = annualVacationService.getWorkingDayPre(num,classDate);
								if(DateUtils.getIntervalDays(new Date(),lastSubDate)<0){
									throw new OaException("调班日期包含法定节假日，只能前4个工作日提交！");
								}
							}
						}
						moveSuccess = true;
					}
				}else{
					//execl表里班次为空或者不存在
					if(areadyClass!=null&&areadyClass.getClassSettingId()!=null){
						ApplicationEmployeeClassDetail applicationEmployeeClassDetail = new ApplicationEmployeeClassDetail();
						applicationEmployeeClassDetail.setCompanyId(user.getCompanyId());
						applicationEmployeeClassDetail.setEmployId(empId);
						applicationEmployeeClassDetail.setCreateTime(new Date());
						applicationEmployeeClassDetail.setCreateUser(user.getEmployee().getCnName());
						applicationEmployeeClassDetail.setClassSettingId(null);
						applicationEmployeeClassDetail.setClassSettingPerson(user.getEmployee().getCnName());
						applicationEmployeeClassDetail.setClassDate(classDate);
						applicationEmployeeClassDetail.setDelFlag(0);
						applicationEmployeeClassDetail.setIsMove(1);
						//原先班次记录
						ApplicationEmployeeClassDetail oldSchedule = new ApplicationEmployeeClassDetail();
						oldSchedule.setCompanyId(user.getCompanyId());
						oldSchedule.setEmployId(empId);
						oldSchedule.setCreateTime(new Date());
						oldSchedule.setCreateUser(user.getEmployee().getCnName());
						oldSchedule.setClassSettingId(areadyClass.getClassSettingId());
						oldSchedule.setClassSettingPerson(user.getEmployee().getCnName());
						oldSchedule.setClassDate(classDate);
						oldSchedule.setDelFlag(0);
						oldSchedule.setIsMove(0);
						updateList.add(applicationEmployeeClassDetail);
						updateList.add(oldSchedule);
						if(vacationMap!=null&&vacationMap.containsKey(classDate)&&vacationMap.get(classDate)==3){
							containLeagl= true;
							if(!isHr){
								Date lastSubDate = annualVacationService.getWorkingDayPre(num,classDate);
								if(DateUtils.getIntervalDays(new Date(),lastSubDate)<0){
									throw new OaException("调班日期包含法定节假日，只能前4个工作日提交！");
								}
							}
						}
						moveSuccess = true;
					}else{
						//班次未发生变化，不做处理
					}
				}
			}
			if(moveSuccess){
				moveNum = moveNum+1;
			}
		}
		
		if(updateList==null||updateList.size()<=0){
			throw new OaException("班次未发生变动，不需要调班！");
		}
		
		//查询该部门的排班人和审核人
		ScheduleGroup schedulerList = scheduleGroupMapper.getGroupById(groupId);
		Long auditor = schedulerList.getAuditor();//排班审核人
		//启动流程
		Map<String, Object> variables = new HashMap<>();
		variables.put("approver",auditor+"");
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("scheduling",variables);
		
		//-----------------start-----------------------保存流程节点信息
		ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
		flow.setAssigneeName(user.getEmployee().getCnName());
		flow.setDepartName(user.getDepart().getName());
		flow.setPositionName(user.getPosition().getPositionName());
		flow.setFinishTime(new Date());
		flow.setComment("排班申请");
		flow.setProcessId(processInstance.getId());
		flow.setProcessKey(ConfigConstants.SCHEDULING_KEY);
		flow.setStatu(0);
		viewTaskInfoService.save(flow);
		
		Depart depart = departService.getById(departId);
		
		ApplicationEmployeeClass applicationEmployeeClass = new ApplicationEmployeeClass();
		applicationEmployeeClass.setDepartId(departId);
		applicationEmployeeClass.setDepartName(depart!=null?depart.getName():null);
		applicationEmployeeClass.setClassMonth(firstDay);
		applicationEmployeeClass.setEmployeeNum(moveNum);
		applicationEmployeeClass.setClassEmployeeNum(moveNum);
		applicationEmployeeClass.setClassSettingPerson(user.getEmployee().getCnName());
		applicationEmployeeClass.setApprovalStatus(ConfigConstants.DOING_STATUS);
		applicationEmployeeClass.setVersion(0L);
		applicationEmployeeClass.setDelFlag(0);
		applicationEmployeeClass.setCreateUser(user.getEmployee().getCnName());
		applicationEmployeeClass.setCreateTime(new Date());
		applicationEmployeeClass.setIsMove(1);
		applicationEmployeeClass.setProcessInstanceId(processInstance.getId());
		applicationEmployeeClass.setEmployeeId(user.getEmployee().getId());
		applicationEmployeeClass.setGroupId(groupId);
		applicationEmployeeClass.setMoveType(0);//调班申请
		applicationEmployeeClassMapper.save(applicationEmployeeClass);
		
		for(ApplicationEmployeeClassDetail move:updateList){
			move.setAttnApplicationEmployClassId(applicationEmployeeClass.getId());
		}
		applicationEmployeeClassDetailMapper.batchSave(updateList);
		
		if(user.getEmployee().getId().equals(auditor)){
			
			Map<String, Object> variables1 = new HashMap<>();
			variables1.put("isContainLegal",containLeagl);
			Task task = activitiServiceImpl.queryTaskByProcessInstanceId(processInstance.getId());
			Task task1 = taskService.createTaskQuery().taskId(task.getId()).list().get(0);
	    	taskService.addComment(task1.getId(), task1.getProcessInstanceId(), "排班申请");
	    	
	    	//-----------------start-----------------------保存流程节点信息
			ViewTaskInfoTbl flow1 = new ViewTaskInfoTbl();
			flow1.setAssigneeName(user.getEmployee().getCnName());
			flow1.setDepartName(user.getDepart().getName());
			flow1.setPositionName(user.getPosition().getPositionName());
			flow1.setFinishTime(new Date());
			flow1.setComment("排班申请");
			flow1.setProcessId(processInstance.getId());
			flow1.setProcessKey(ConfigConstants.SCHEDULING_KEY);
			flow1.setStatu(100);
			viewTaskInfoService.save(flow1);
			
	    	taskService.complete(task1.getId(), variables1);
	    }	
		
		resultMap.put("result", "success");
		resultMap.put("resultMsg", "导入调班申请成功！");
		return resultMap;
	}
}
