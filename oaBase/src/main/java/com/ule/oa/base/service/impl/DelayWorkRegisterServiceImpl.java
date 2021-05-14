package com.ule.oa.base.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ule.oa.base.mapper.AttnStatisticsMapper;
import com.ule.oa.base.mapper.DelayWorkRegisterMapper;
import com.ule.oa.base.mapper.EmpDelayHoursMapper;
import com.ule.oa.base.mapper.RabcRoleMapper;
import com.ule.oa.base.po.AttnSignRecord;
import com.ule.oa.base.po.AttnStatistics;
import com.ule.oa.base.po.CompanyConfig;
import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.DelayWorkRegister;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.EmpDelayHours;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.po.RabcRole;
import com.ule.oa.base.po.RequestParamQueryEmpCondition;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.AnnualVacationService;
import com.ule.oa.base.service.AttnSignRecordService;
import com.ule.oa.base.service.CompanyConfigService;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.DelayWorkRegisterService;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmployeeClassService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.ExcelUtil;
import com.ule.oa.common.utils.GetHours;
import com.ule.oa.common.utils.PageModel;

@Service
public class DelayWorkRegisterServiceImpl implements DelayWorkRegisterService {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private UserService userService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private DelayWorkRegisterMapper delayWorkRegisterMapper;
	@Autowired
	private CompanyConfigService companyConfigService;
	@Autowired
	private AnnualVacationService annualVacationService;
	@Autowired
	private ConfigService configService;
	@Autowired
	private DepartService departService;
	@Autowired
	private EmpDelayHoursMapper empDelayHoursMapper;
	@Resource
	private EmployeeClassService employeeClassService;
	@Resource
	private AttnSignRecordService attnSignRecordService;
	@Resource
	private AttnStatisticsMapper attnStatisticsMapper;
	@Autowired
	private RabcRoleMapper rabcRoleMapper;

	/**
	 * 查询延时工作登记
	 */
	@Override
	public Map<String, Object> getDelayWorkPage(Long departId, String leaderName,String date, String empCode, String empCnName,
			Integer page, Integer rows) {
		
		long time1 = System.currentTimeMillis();

		User user = userService.getCurrentUser();
		Map<String, Object> result = new HashMap<String, Object>();
		// 日期为空则查当月
		Date month = new Date();
		if (date != null) {
			String dateStr = date.substring(0, 7);
			month = DateUtils.parse(dateStr + "-01", DateUtils.FORMAT_SHORT);
		}
		Date startDay = DateUtils.getFirstDay(month);// 本月第一天
		Date lastDay = DateUtils.getLastDay(month);// 本月最后一天
		// 表头，星期和天数
		List<String> weekDays = new ArrayList<String>();
		// 表头，天数
		List<String> days = new ArrayList<String>();
		// 表头，日期
		List<String> dates = new ArrayList<String>();
		days.add("员工编号");
		days.add("姓名");
		weekDays.add("");
		weekDays.add("");
		Date fristDay = DateUtils.getFirstDay(month);// 本月第一天
		int i = 1;
		while (true) {
			String week = DateUtils.getWeek(fristDay);
			weekDays.add(week);
			days.add(String.valueOf(i));
			dates.add(DateUtils.format(fristDay, DateUtils.FORMAT_SHORT));
			fristDay = DateUtils.addDay(fristDay, 1);
			i = i + 1;
			if (!DateUtils.format(fristDay, "yyyy-MM").equals(DateUtils.format(lastDay, "yyyy-MM"))) {
				break;
			}
		}
		days.add("合计");
		days.add("出勤多余小时数");
		days.add("状态");
		result.put("weekDays", weekDays);
		result.put("days", days);
		result.put("dates", dates);

		PageModel<Map<String, Object>> pm = new PageModel<Map<String, Object>>();
		List<Map<String, Object>> delayWorkDetailList = new ArrayList<Map<String, Object>>();
		pm.setPageNo(page == null ? 0 : page);
		pm.setPageSize(rows == null ? 0 : rows);
		Integer limit = pm.getLimit();
		Integer offset = pm.getOffset();
		
		//查询登录用户所拥有的部门角色，按部门角色获取每个部门下属员工的集合
		List<RabcRole> roleList = rabcRoleMapper.getListByUserId(user.getId(),departId);
		//标记是否已经取了该部门的所有员工
		Map<Long,Boolean> hasGetMap = new HashMap<Long,Boolean>();
		//需要查询的员工id列表
		List<Long> employeeIdList = new ArrayList<Long>();
		// 配置查询表中工时为标准工时的id
		CompanyConfig condition = new CompanyConfig();
		condition.setCode("typeOfWork");
		condition.setDisplayCode("standard");
		CompanyConfig companyConfig = companyConfigService.getByCondition(condition);
		
		//查询特殊角色配置
		Config configCondition = new Config();
		configCondition.setCode("get_all_employee_role");
		List<Config> isGetAllEmployeList = configService.getListByCondition(configCondition);
		
		
		for(RabcRole role:roleList){
			boolean isGetAll = false;
			//已经取得该部们所有员工，无须重复获取
			if(hasGetMap!=null&&hasGetMap.containsKey(role.getDepartId())){
				continue;
			}
			for(Config data:isGetAllEmployeList){
				if(data.getDisplayCode().equals(role.getName())){
					isGetAll = true;
					continue;
				}
			}
			//特殊角色获取该部门所有员工数据
		    if(isGetAll){
		    	//获取该部门所有员工
		    	List<Long> idList = employeeService.getSubEmployeeList(user.getEmployeeId(), role.getDepartId(),isGetAll);
		    	employeeIdList.addAll(idList);
		    	hasGetMap.put(role.getDepartId(), true);
		    	continue;
		    }
		    //一般角色获取所有标准工时下属数据（可能多级）
		    List<Long> idList = employeeService.getSubEmployeeList(user.getEmployeeId(), role.getDepartId(),isGetAll);
		    employeeIdList.addAll(idList);
			
		}
		
		//根据所有下属id查询详细信息
		List<Employee> empList = new ArrayList<Employee>();
		Integer total = 0;
		RequestParamQueryEmpCondition hrQueryParam = new RequestParamQueryEmpCondition();
		//筛选条件
		//汇报对象
		if(StringUtils.isNotBlank(leaderName)){
			hrQueryParam.setSearchLeader(leaderName);
		}
		// 部门
		hrQueryParam.setDepartId(departId);
		hrQueryParam.setEmpCode(empCode);
		hrQueryParam.setEmpCnName(empCnName);
		hrQueryParam.setChooseMonth(startDay);
		//查询指定员工类型数据
		hrQueryParam.setEmpTypeIdList(configService.getNeedEmpTypeIdList());
		//标准工时
		if(companyConfig!=null){
			hrQueryParam.setWorkType(companyConfig.getId());
		}
		//所有下属id列表
		if(employeeIdList==null || employeeIdList.size()<=0){
			pm.setRows(delayWorkDetailList);
			pm.setTotal(0);
			result.put("page", pm);
			result.put("success", true);
			return result;
			
		}
		
		hrQueryParam.setEmployeeIdList(employeeIdList);
		total = employeeService.getStandardEmpCountByCount(hrQueryParam);
		// 分页
		hrQueryParam.setLimit(limit);
		hrQueryParam.setOffset(offset);
		empList = employeeService.getAllEmpByWorkTypeAndLeaderAndDepart(hrQueryParam);
		
		if (empList != null && empList.size() > 0) {
			// 5.查询员工延时工作登记
			for (Employee employee : empList) {
				Map<String, Object> delayWorkDetail = new HashMap<String, Object>();
				delayWorkDetail.put("empId", employee.getId());
				delayWorkDetail.put("empCode", employee.getCode());
				delayWorkDetail.put("empCnName", employee.getCnName());
				List<DelayWorkRegister> delayWorkList = delayWorkRegisterMapper
						.getDelayWorkRegisterByMonth(employee.getId(), startDay, lastDay);
				if (delayWorkList != null && delayWorkList.size() > 0) {
					for (DelayWorkRegister delayWorkRegister : delayWorkList) {
						// 6.返回颜色：已匹配考勤，未确认，实际时长不等于计划时长，比较后取小值，重新返回实际时长
						if (delayWorkRegister.getIsMatched().intValue() == 1
								&& delayWorkRegister.getIsConfirm().intValue() == 0
								&& delayWorkRegister.getExpectDelayHour() != null
								&& delayWorkRegister.getActualDelayHour() != null
								&& !delayWorkRegister.getExpectDelayHour().equals(delayWorkRegister.getActualDelayHour())) {
							delayWorkRegister.setFontColor("blue");
							delayWorkRegister.setActualDelayHour(
									delayWorkRegister.getActualDelayHour().doubleValue() < delayWorkRegister.getExpectDelayHour().doubleValue() ? delayWorkRegister.getActualDelayHour() : delayWorkRegister.getExpectDelayHour());
						} else {
							delayWorkRegister.setFontColor("black");
						}
						// 7.返回日期
						String week = DateUtils.getWeek(delayWorkRegister.getDelayDate());
						delayWorkRegister.setWeek(week);
						String day = DateUtils.getDayOfMonth(delayWorkRegister.getDelayDate());
						delayWorkDetail.put(Integer.valueOf(day).toString(), delayWorkRegister);
					}
				}
				// 8.设置参数
				double totalDelayHours = 0;
				double overHoursOfAttendance = 0;
				Integer status = 0;
				EmpDelayHours empDelayHours = empDelayHoursMapper.getByEmpAndMonth(employee.getId(), startDay);
				if(empDelayHours != null){
					totalDelayHours = empDelayHours.getTotalDelayHours();
					overHoursOfAttendance = totalDelayHours - empDelayHours.getUsedDelayHours().doubleValue() - empDelayHours.getLockedDelayHours().doubleValue();
					status = empDelayHours.getStatus();
				}
				delayWorkDetail.put("count", totalDelayHours);
				delayWorkDetail.put("overHoursOfAttendance", overHoursOfAttendance);
				delayWorkDetail.put("status", status);
				delayWorkDetailList.add(delayWorkDetail);
			}
			pm.setRows(delayWorkDetailList);
			pm.setTotal(total);
		}
		result.put("page", pm);
		result.put("success", true);
		
		long time2 = System.currentTimeMillis();
		logger.info("getDelayWorkPage uses time is:"+(time2-time1));
		
		return result;
	}

	@Override
	public Map<String, Object> getDelayWorkDetail(Long empId, Date delayDate) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (empId == null || delayDate == null) {
			result.put("message", "员工或日期不能为空！");
			result.put("success", false);
			return result;
		}
		// 判断员工是否离职
		Integer isQuit = employeeService.isQuitThisDay(empId, delayDate);
		if (isQuit > 0) {
			result.put("message", "员工已离职不可进行登记！");
			result.put("success", false);
			return result;
		}
		// 判断是否为法定节假日
		Integer dayType = annualVacationService.getDayType(delayDate);
		if (dayType != null && dayType.equals(3)) {
			result.put("message", "法定节假日当日不可进行登记！");
			result.put("success", false);
			return result;
		}
		DelayWorkRegister delayWorkRegister = delayWorkRegisterMapper.getDelayWorkDetail(empId, delayDate);
		// 判断所选日期是否为工作日
		EmployeeClass condition = new EmployeeClass();
		condition.setEmployId(empId);
		condition.setClassDate(delayDate);
		EmployeeClass empClass = employeeClassService.getEmployeeClassSetting(condition);

		// 1.初始登记
		if (delayWorkRegister == null) {
			// 封装返回参数
			delayWorkRegister = new DelayWorkRegister();
			delayWorkRegister.setUpdateType(1);
			//1.1判断是加班前登记还是加班后登记
			int compareResult = DateUtils.compareDayDate(new Date(),delayDate);
			//1.2查询员工当日实际打卡时间与实际小时数
			Map<String, Object> actualMap = getActualHours(empId, delayDate);
			Date startTime = (Date) actualMap.get("startTime");
			Date endTime = (Date) actualMap.get("endTime");
			double hours = (double) actualMap.get("workHours");
			//加班前
			if(compareResult != 1){
				// 1.1.1工作日
				if (empClass != null) {
					// 返回预计班次开始时间+1
					delayWorkRegister.setExpectStartTime(startTime);
					delayWorkRegister.setIsWorkDay(0);
				}else{
					//1.1.2非工作日
					delayWorkRegister.setIsWorkDay(1);
				}
			}else{
				//加班后
				// 1.2.1工作日返回班次开始时间+1和下班打卡时间
				// 1.2.2非工作日返回上班打卡时间和下班打卡时间
				delayWorkRegister.setExpectStartTime(startTime);
				delayWorkRegister.setExpectEndTime(endTime);
				delayWorkRegister.setExpectDelayHour(hours);
				if(empClass != null){
					delayWorkRegister.setIsWorkDay(0);
				}else{
					delayWorkRegister.setIsWorkDay(1);
				}
			}
		} else if (delayWorkRegister.getActualStartTime() == null && delayWorkRegister.getActualEndTime() == null
				&& delayWorkRegister.getActualDelayHour() == null) {
			// 2.未匹配(修改)
			delayWorkRegister.setUpdateType(2);
			if (empClass != null) {
				delayWorkRegister.setIsWorkDay(0);
			}else{
				//非工作日
				delayWorkRegister.setIsWorkDay(1);
			}
		} else if (delayWorkRegister.getIsConfirm() == 0
				&& delayWorkRegister.getActualDelayHour().equals(delayWorkRegister.getExpectDelayHour())) {
			// 3.匹配考勤且匹配结果相等
			delayWorkRegister.setUpdateType(3);
		} else if (delayWorkRegister.getIsConfirm() == 0
				&& !delayWorkRegister.getActualDelayHour().equals(delayWorkRegister.getExpectDelayHour())) {
			// 4.匹配考勤且结果不相等
			//比较后取小值，重新返回实际时长
			delayWorkRegister.setActualDelayHour(delayWorkRegister.getActualDelayHour().doubleValue() < delayWorkRegister.getExpectDelayHour().doubleValue() ? delayWorkRegister.getActualDelayHour() : delayWorkRegister.getExpectDelayHour());

			delayWorkRegister.setUpdateType(4);
		} else if (delayWorkRegister.getIsConfirm() == 1) {
			// 5.确认
			delayWorkRegister.setUpdateType(5);
		}

		result.put("delayWorkRegister", delayWorkRegister);
		result.put("success", true);
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void macthDelayWork(DelayWorkRegister match) throws OaException {
		/**
		 * 规则
		 * 1.工作日--取出排班时间，取出attn_sign_record（当天排班结束时间+1点-排班结束时间+12点的时间），算出实际加班开始结束时间和小时数
		 * 2.休息日--取出打卡时间，取出attn_sign_record（当天6点-次日6点打卡时间），算出实际加班开始结束时间和小时数
		 * 3.如果leader申请了延时，但是员工没有加班，比如18:10打卡，显示19:00-18:10,延时工作小时数：0
		 * 4.预计大于实际，已实际为准，预计小于实际，已预计为准
		 * 
		 */
		logger.info("匹配员工" + match.getEmployeeId() + "于"
				+ DateUtils.format(match.getDelayDate(), DateUtils.FORMAT_SHORT) + "延时登记");

		Date actualEndTime1 = null;// 结束时间（yyyy-MM-dd HH:mm:ss）
		Date actuaStartTime1 = null;// 开始时间（yyyy-MM-dd HH:mm:ss）
		double duration = 0;// 延时小时数

		Map<String,Object> matchMap= getActualHours(match.getEmployeeId(),match.getDelayDate());
		actuaStartTime1 = matchMap.get("startTime")!=null?(Date) matchMap.get("startTime"):null;
		actualEndTime1 =  matchMap.get("endTime")!=null?(Date) matchMap.get("endTime"):null;
		duration = matchMap.get("workHours")!=null?(double) matchMap.get("workHours"):0d;
		// 更新匹配状态
		match.setActualDelayHour(duration);
		match.setActualStartTime(actuaStartTime1);
		match.setActualEndTime(actualEndTime1);
		match.setIsMatched(1);// 已匹配
		delayWorkRegisterMapper.matchActaulTime(match);
		addDelayHoursByMonth("system", match.getDelayDate(), match.getEmployeeId());
	}

	@Override
	public List<DelayWorkRegister> getUnMatchedListByDelayDate(Date delayDate) {
		return delayWorkRegisterMapper.getUnMatchedListByDelayDate(delayDate);
	}

	/**
	 * 提交延时工作登记
	 * @throws OaException 
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> commitDelayWorkDetail(DelayWorkRegister requestParam) throws OaException {
		User user = userService.getCurrentUser();
		Map<String, Object> result = new HashMap<String, Object>();
		if(requestParam == null){
			result.put("message", "参数错误！");
			result.put("success", false);
			return result;
		}
		if(requestParam.getEmployeeId() == null){
			result.put("message", "请选择员工！");
			result.put("success", false);
			return result;
		}
		if(requestParam.getDelayDate() == null){
			result.put("message", "请选择延时工作日期！");
			result.put("success", false);
			return result;
		}
		if (StringUtils.isBlank(requestParam.getStartTime()) || StringUtils.isBlank(requestParam.getEndTime())
				|| requestParam.getExpectDelayHour() == null) {
			result.put("message", "请登记延时工作时间后提交！");
			result.put("success", false);
			return result;
		}
		if(requestParam.getExpectDelayHour().doubleValue() < 1){
			result.put("message", "延时工作时长不能低于一小时！");
			result.put("success", false);
			return result;
		}
	    
		Depart empD = departService.getInfoByEmpId(requestParam.getEmployeeId());
		if("117".equals(empD.getCode())) {
			//技术开发部需大于2h
			if(requestParam.getExpectDelayHour().doubleValue() < 2){
				result.put("message", "延时工作时长不能低于2小时！");
				result.put("success", false);
				return result;
			}
		}
		
		Date startDate = DateUtils.parse(requestParam.getStartTime());
		Date endDate = DateUtils.parse(requestParam.getEndTime());
		requestParam.setExpectStartTime(startDate);
		requestParam.setExpectEndTime(endDate);
		/**判断是否为人事**/
		// 查询根据条件查询所有标准工时的员工
		// 配置查询表中工时为标准工时的id
		CompanyConfig condition = new CompanyConfig();
		condition.setCode("typeOfWork");
		condition.setDisplayCode("standard");
		CompanyConfig companyConfig = companyConfigService.getByCondition(condition);
		RequestParamQueryEmpCondition requestParamQueryEmpByCondition = new RequestParamQueryEmpCondition();
		
		requestParamQueryEmpByCondition.setLeaderId(user.getEmployee().getId());
		// 标准工时类型
		if (companyConfig != null) {
			requestParamQueryEmpByCondition.setWorkType(companyConfig.getId());
		}
		// 判断是否有下属员工
		List<Employee> subordinateEmp = employeeService
				.getAllEmpByWorkTypeAndLeaderAndDepart(requestParamQueryEmpByCondition);
		Date now = new Date();
		Date firstDay = DateUtils.getFirstDay(requestParam.getDelayDate());
		Date nextMonthFirstDay = DateUtils.getFirstDay(DateUtils.addMonth(firstDay, 1));
		/**判断是否在可登记时间范围内**/ 
		//非人事月初到次月五个工作日
		if(subordinateEmp != null && subordinateEmp.size() > 0){
			//次月第五个工作日
			Config configCondition = new Config();
			configCondition.setCode("timeLimit5");
			List<Config> limit = configService.getListByCondition(configCondition);
			int num = Integer.valueOf(limit.get(0).getDisplayCode());
			//次月第五个工作日
			Date nextmonth5WorkingDay = annualVacationService.getWorkingDayOfMonth(nextMonthFirstDay,num+1);
			if (DateUtils.compareDayDate(firstDay, now) == 1
					|| DateUtils.compareDayDate(now,nextmonth5WorkingDay) == 1) {
				result.put("message", "最早登记时间为当月1日，最晚登记时间为次月3个工作日之内！");
				result.put("success", false);
				return result;
			}
		}else{
			//次月第七个工作日
			Config configCondition = new Config();
			configCondition.setCode("timeLimit7");
			List<Config> limit = configService.getListByCondition(configCondition);
			int num2 = Integer.valueOf(limit.get(0).getDisplayCode());
			//次月第七个工作日
			Date nextmonth7WorkingDay = annualVacationService.getWorkingDayOfMonth(nextMonthFirstDay,num2+1);
			if (DateUtils.compareDayDate(firstDay, now) == 1
					|| DateUtils.compareDayDate(now,nextmonth7WorkingDay) == 1) {
				result.put("message", "最早登记时间为当月1日，最晚登记时间为次月5个工作日之内！");
				result.put("success", false);
				return result;
			}
		}
		
		//判断是加班前登记还是加班后登记
		int compareResult = DateUtils.compareDayDate(new Date(),requestParam.getDelayDate());
		if(compareResult == 1){
			//加班后登记判断登记时长不能大于员工当天打卡小时数
			Map<String, Object> actualMap = getActualHours(requestParam.getEmployeeId(), requestParam.getDelayDate());
			double hours = (double) actualMap.get("workHours");
			if(requestParam.getExpectDelayHour().doubleValue() > hours){
				result.put("message", "延时工作时长不可超过员工延时工作起止时间！");
				result.put("success", false);
				return result;
			}
		}
		//判断是否有登记
		DelayWorkRegister delayWorkDetail = delayWorkRegisterMapper.getDelayWorkDetail(requestParam.getEmployeeId(), requestParam.getDelayDate());
		
		// 初始登记
		if (delayWorkDetail == null) {
			DelayWorkRegister delayWorkRegister = new DelayWorkRegister();
			delayWorkRegister.setEmployeeId(requestParam.getEmployeeId());
			delayWorkRegister.setDelayDate(requestParam.getDelayDate());
			// 预计开始时间
			delayWorkRegister.setExpectStartTime(requestParam.getExpectStartTime());
			// 预计结束时间
			delayWorkRegister.setExpectEndTime(requestParam.getExpectEndTime());
			// 时长
			delayWorkRegister.setExpectDelayHour(requestParam.getExpectDelayHour());
			// 工作内容
			delayWorkRegister.setDelayItem(requestParam.getDelayItem());
			delayWorkRegister.setCreateTime(new Date());
			delayWorkRegister.setCreateUser(user.getEmployee().getId().toString());
			delayWorkRegister.setDelFlag(0);
			delayWorkRegisterMapper.insertDelayWorkDetail(delayWorkRegister);
			requestParam.setId(delayWorkRegister.getId());
		} else {
			// 修改
			if(delayWorkDetail.getIsMatched() == 1 || delayWorkDetail.getIsConfirm() == 1){
				result.put("message", "已匹配或已确认的延时工作登记不可提交修改！");
				result.put("success", false);
				return result;
			}
			requestParam.setId(delayWorkDetail.getId());
			requestParam.setUpdateTime(now);
			requestParam.setUpdateUser(user.getEmployee().getId().toString());
			delayWorkRegisterMapper.updateDelayWorkDetail(requestParam);
		}
		//九点前提交前天及之前的登记，或九点后提交昨天或之前的登记，匹配考勤
		String hours = DateUtils.getHours(now);
		int hour = Integer.parseInt(hours);
		//昨天日期
		Date yesterDay = DateUtils.addDay(now, -1);
		//前天日期
		Date beforeYesterDay = DateUtils.addDay(now, -2);
		
		int yesterDayInt = Integer.parseInt(DateUtils.format(yesterDay, DateUtils.FORMAT_SIMPLE));
		int beforeYesterDayInt = Integer.parseInt(DateUtils.format(beforeYesterDay, DateUtils.FORMAT_SIMPLE));
		int delayDateInt = Integer.parseInt(DateUtils.format(requestParam.getDelayDate(), DateUtils.FORMAT_SIMPLE));
		//匹配考勤
		if((hour <= 9 && delayDateInt <= beforeYesterDayInt) || (hour >= 9 && delayDateInt <= yesterDayInt)){
			Date actualEndTime1 = null;// 结束时间（yyyy-MM-dd HH:mm:ss）
			Date actuaStartTime1 = null;// 开始时间（yyyy-MM-dd HH:mm:ss）
			double duration = 0;// 延时小时数

			Map<String,Object> matchMap= getActualHours(requestParam.getEmployeeId(),requestParam.getDelayDate());
			actuaStartTime1 = matchMap.get("startTime")!=null?(Date) matchMap.get("startTime"):null;
			actualEndTime1 =  matchMap.get("endTime")!=null?(Date) matchMap.get("endTime"):null;
			duration = matchMap.get("workHours")!=null?(double) matchMap.get("workHours"):0d;
			// 更新匹配状态
			requestParam.setActualDelayHour(duration);
			requestParam.setActualStartTime(actuaStartTime1);
			requestParam.setActualEndTime(actualEndTime1);
			requestParam.setIsMatched(1);// 已匹配
			delayWorkRegisterMapper.matchActaulTime(requestParam);
			addDelayHoursByMonth("system", requestParam.getDelayDate(), requestParam.getEmployeeId());
		}
		result.put("success", true);
		return result;
	}

	/**
	 * 确认延时工作登记
	 * @throws OaException 
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> confirmDelayWorkDetail(DelayWorkRegister requestParam) throws OaException {
		User user = userService.getCurrentUser();
		Map<String, Object> result = new HashMap<String, Object>();
		if(requestParam == null || requestParam.getId() == null || requestParam.getActualDelayHour() == null){
			result.put("message", "参数错误！");
			result.put("success", false);
			return result;
		}
		
		DelayWorkRegister delayWorkDetail = delayWorkRegisterMapper.getById(requestParam.getId());
		if(delayWorkDetail.getIsMatched() == null ||  delayWorkDetail.getIsMatched() == 0 || delayWorkDetail.getActualDelayHour() == null){
			result.put("message", "未匹配考勤不可确认！");
			result.put("success", false);
			return result;
		}
		//加班后登记判断登记时长不能大于员工当天打卡小时数
		Map<String, Object> actualMap = getActualHours(requestParam.getEmployeeId(), requestParam.getDelayDate());
		double hours = (double) actualMap.get("workHours");
		if(requestParam.getActualDelayHour().doubleValue() > hours){
			result.put("message", "延时工作时长不可超过员工延时工作起止时间！");
			result.put("success", false);
			return result;
		}
		DelayWorkRegister parameter = new DelayWorkRegister();
		parameter.setId(delayWorkDetail.getId());
		parameter.setIsConfirm(1);
		parameter.setActualDelayHour(requestParam.getActualDelayHour());
		parameter.setUpdateTime(new Date());
		parameter.setUpdateUser(user.getEmployee().getId().toString());
		delayWorkRegisterMapper.confirmDelayWorkDetail(parameter);
		//修改当月延时工作小时总数
		Date delayDate = delayWorkDetail.getDelayDate();
		Long employeeId = delayWorkDetail.getEmployeeId();
		//增加当月延时工作小时总数
		addDelayHoursByMonth(user.getEmployee().getId().toString(), delayDate, employeeId);
		result.put("success", true);
		result.put("message", "确认成功！");
		return result;
	}
	
	//增加当月延时工作小时总数
	public void addDelayHoursByMonth(String userId, Date delayDate, Long employeeId) throws OaException {
		if(delayDate == null || employeeId == null){
			throw new OaException("参数错误！");
		}
		Date now = new Date();
		Date startDay = DateUtils.getFirstDay(delayDate);
		Date lastDay = DateUtils.getLastDay(delayDate);
			
		//重新统计当月所有加班数据
		List<DelayWorkRegister> delayWorkList = delayWorkRegisterMapper
				.getDelayWorkRegisterByMonth(employeeId, startDay, lastDay);
		double totalDelayHours = 0;
		if (delayWorkList != null && delayWorkList.size() > 0) {
			
			for (DelayWorkRegister delayWorkRegister : delayWorkList) {
				// 统计合计
				//已匹配未确认的，比较实际与计划取小值
				if (delayWorkRegister.getIsMatched().intValue() == 1 && delayWorkRegister.getIsConfirm().intValue() == 0) {
					double lessHour = delayWorkRegister.getActualDelayHour().doubleValue() < delayWorkRegister.getExpectDelayHour().doubleValue() ? delayWorkRegister.getActualDelayHour() : delayWorkRegister.getExpectDelayHour();
					totalDelayHours += lessHour;
				}
				//已确认的，取实际
				if (delayWorkRegister.getIsConfirm().intValue() == 1) {
					totalDelayHours += delayWorkRegister.getActualDelayHour().doubleValue();
				}
			}
		}
		//根据员工与月份查询当月是否有数据
		EmpDelayHours empDelayHours = empDelayHoursMapper.getByEmpAndMonth(employeeId,startDay);
		if(empDelayHours == null){
			//不存在则新建当月延时小时总数
			empDelayHours = new EmpDelayHours();
			empDelayHours.setEmployeeId(employeeId);
			empDelayHours.setDelayMonth(startDay);
			empDelayHours.setTotalDelayHours(totalDelayHours);
			empDelayHours.setCreateTime(now);
			empDelayHours.setCreateUser(userId);
			empDelayHours.setDelFlag(0);
			empDelayHoursMapper.insertEmpDelayHours(empDelayHours);
		}else{
			//已使用
			Double usedDelayHours = empDelayHours.getUsedDelayHours();
			//已锁定
			Double lockedDelayHours = empDelayHours.getLockedDelayHours();
			//已使用和锁定的和
			double countUsedHours = usedDelayHours.doubleValue() + lockedDelayHours.doubleValue();

			//如果重新统计的总数小于已使用的小时数，则不可修改
			if(totalDelayHours < countUsedHours){
				//抛异常回滚数据
				throw new OaException("该延时工作小时已被用于消下属缺勤使用，可撤销消下属缺勤申请后尝试修改！");
			}
			empDelayHours.setTotalDelayHours(totalDelayHours);
			empDelayHours.setUpdateTime(now);
			empDelayHours.setUpdateUser(userId);
			empDelayHoursMapper.updateEmpDelayHours(empDelayHours);
		}
			
	}

	/**
	 * 删除延时工作登记
	 * @throws OaException 
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> deleteDelayWorkDetail(DelayWorkRegister requestParam) throws OaException {
		User user = userService.getCurrentUser();
		Date now = new Date();
		Map<String, Object> result = new HashMap<String, Object>();
		if(requestParam == null || requestParam.getId() == null){
			throw new OaException("参数错误！");
		}
		DelayWorkRegister workRegister = delayWorkRegisterMapper.getById(requestParam.getId());
		//可删除未确认的登记
		if(workRegister != null && workRegister.getIsConfirm() != null && workRegister.getIsConfirm() != 1){
			
			//如果已匹配的则修改当月加班合计中的总计与可用
			
			//根据员工与月份查询当月是否有数据
			EmpDelayHours empDelayHours = empDelayHoursMapper.getByEmpAndMonth(workRegister.getEmployeeId(),DateUtils.getFirstDay(workRegister.getDelayDate()));

			if(empDelayHours != null && workRegister.getActualDelayHour() != null && workRegister.getIsMatched() != null && workRegister.getIsMatched() == 1){
				//剩余可用
				double overHoursOfAttendance = empDelayHours.getTotalDelayHours() - empDelayHours.getUsedDelayHours() - empDelayHours.getLockedDelayHours();
				//判断登记的实际和计划，取小值。
				double addHours = workRegister.getActualDelayHour().doubleValue() < workRegister.getExpectDelayHour().doubleValue() ? workRegister.getActualDelayHour().doubleValue() : workRegister.getExpectDelayHour().doubleValue();
				//如果重新统计的总数小于已使用的小时数，则不可修改
				if(overHoursOfAttendance < addHours){
					throw new OaException("该延时工作小时已被用于消下属缺勤使用，可撤销消下属缺勤申请后尝试删除！");
				}else{
					EmpDelayHours updateParam = new EmpDelayHours();
					updateParam.setId(empDelayHours.getId());
					updateParam.setTotalDelayHours(empDelayHours.getTotalDelayHours() - addHours);
					updateParam.setUpdateTime(now);
					updateParam.setUpdateUser(user.getEmployee().getId().toString());
					empDelayHoursMapper.updateEmpDelayHours(updateParam);
				}
			}
			DelayWorkRegister delayWorkRegister = new DelayWorkRegister();
			delayWorkRegister.setId(workRegister.getId());
			delayWorkRegister.setDelFlag(1);
			delayWorkRegister.setUpdateTime(now);
			delayWorkRegister.setUpdateUser(user.getEmployee().getId().toString());
			delayWorkRegisterMapper.deleteDelayWorkDetail(delayWorkRegister);
			result.put("success", true);
			result.put("message", "删除成功！");
		}else{
			throw new OaException("已确认的延时工作登记不可删除！");
		}
		
		return result;
	}

	@Override
	public HSSFWorkbook exportDelayWorkPage(Long departId,String leaderName, String date, String empCode, String empCnName) {

		User user = userService.getCurrentUser();
		// 日期为空则查当月
		Date month = new Date();
		if (date != null) {
			String dateStr = date.substring(0, 7);
			month = DateUtils.parse(dateStr + "-01", DateUtils.FORMAT_SHORT);
		}
		String year = DateUtils.getYear(month);
		String monthofYear = DateUtils.getMonthofYear(month);
		String fileName = "";
		if (departId != null) {
			Depart depart = departService.getById(departId);
			fileName = depart.getName() + year + "年" + monthofYear + "月延时工作登记表";
		} else {
			fileName = year + "年" + monthofYear + "月延时工作登记表";
		}

		Date startDay = DateUtils.getFirstDay(month);// 本月第一天
		Date lastDay = DateUtils.getLastDay(month);// 本月最后一天
		// 表头，星期和天数
		List<String> title1 = new ArrayList<String>();
		// 表头，天数
		List<String> title2 = new ArrayList<String>();
		// 表头，日期
		List<String> days = new ArrayList<String>();
		title2.add("员工编号");
		title2.add("姓名");
		title1.add("");
		title1.add("");
		days.add("");
		days.add("");
		Date fristDay = DateUtils.getFirstDay(month);// 本月第一天
		int i = 1;
		while (true) {
			String week = DateUtils.getWeek(fristDay);
			title1.add(week);
			title2.add(String.valueOf(i));
			days.add(String.valueOf(i));
			fristDay = DateUtils.addDay(fristDay, 1);
			i = i + 1;
			if (!DateUtils.format(fristDay, "yyyy-MM").equals(DateUtils.format(lastDay, "yyyy-MM"))) {
				break;
			}
		}
		title2.add("合计");
		title2.add("出勤多余小时数");
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// 查询根据条件查询所有标准工时的员工
		// 配置查询表中工时为标准工时的id
		CompanyConfig condition = new CompanyConfig();
		condition.setCode("typeOfWork");
		condition.setDisplayCode("standard");
		CompanyConfig companyConfig = companyConfigService.getByCondition(condition);
		List<Employee> empList = new ArrayList<Employee>();
		RequestParamQueryEmpCondition requestParamQueryEmpByCondition = new RequestParamQueryEmpCondition();
		// 1.判断是否为人事
		requestParamQueryEmpByCondition.setLeaderId(user.getEmployee().getId());
		// 标准工时类型
		if (companyConfig != null) {
			requestParamQueryEmpByCondition.setWorkType(companyConfig.getId());
		}
		//查询指定员工类型数据
		requestParamQueryEmpByCondition.setEmpTypeIdList(configService.getNeedEmpTypeIdList());
		
		// 判断是否有下属员工
		List<Employee> subordinateEmp = employeeService
				.getAllEmpByWorkTypeAndLeaderAndDepart(requestParamQueryEmpByCondition);
		if (subordinateEmp == null || subordinateEmp.size() == 0) {
			// 没有下属员工则为人事
			days.add("状态");
			// 2.登录人为人事，可查所有
			RequestParamQueryEmpCondition hrQueryParam = new RequestParamQueryEmpCondition();
			// 标准工时类型
			if (companyConfig != null) {
				hrQueryParam.setWorkType(companyConfig.getId());
			}
			if(StringUtils.isNotBlank(leaderName)){
				hrQueryParam.setSearchLeader(leaderName);
			}
			hrQueryParam.setDepartId(departId);
			hrQueryParam.setEmpCode(empCode);
			hrQueryParam.setEmpCnName(empCnName);
			hrQueryParam.setChooseMonth(startDay);
			//查询指定员工类型数据
			hrQueryParam.setEmpTypeIdList(configService.getNeedEmpTypeIdList());
			// 分页
			empList = employeeService.getAllEmpByWorkTypeAndLeaderAndDepart(hrQueryParam);
		} else {
			// 3.登录人为汇报对象或部门负责人，查下属标准工时员工
			RequestParamQueryEmpCondition leaderQueryParam = new RequestParamQueryEmpCondition();
			// 标准工时类型
			if (companyConfig != null) {
				leaderQueryParam.setWorkType(companyConfig.getId());
			}
			leaderQueryParam.setDepartId(departId);
			// 4.登录人是汇报对象，查下属员工
			if (subordinateEmp != null && subordinateEmp.size() > 0) {
				leaderQueryParam.setLeaderId(user.getEmployee().getId());
			}
			if(StringUtils.isNotBlank(leaderName)){
				leaderQueryParam.setSearchLeader(leaderName);
			}
			leaderQueryParam.setEmpCode(empCode);
			leaderQueryParam.setEmpCnName(empCnName);
			leaderQueryParam.setChooseMonth(startDay);
			//查询指定员工类型数据
			leaderQueryParam.setEmpTypeIdList(configService.getNeedEmpTypeIdList());
			empList = employeeService.getAllEmpByWorkTypeAndLeaderAndDepart(leaderQueryParam);
		}
		if (empList != null && empList.size() > 0) {
			// 4.查询员工延时工作登记
			for (Employee employee : empList) {
				Map<String, Object> delayWorkDetail = new HashMap<String, Object>();
				delayWorkDetail.put("empId", employee.getId());
				delayWorkDetail.put("empCode", employee.getCode());
				delayWorkDetail.put("empCnName", employee.getCnName());
				List<DelayWorkRegister> delayWorkList = delayWorkRegisterMapper
						.getDelayWorkRegisterByMonth(employee.getId(), startDay, lastDay);
				if (delayWorkList != null && delayWorkList.size() > 0) {
					for (DelayWorkRegister delayWorkRegister : delayWorkList) {
						String day = DateUtils.getDayOfMonth(delayWorkRegister.getDelayDate());
						Double actualDelayHour = delayWorkRegister.getActualDelayHour();
						Double expectDelayHour = delayWorkRegister.getExpectDelayHour();
						if (actualDelayHour != null) {
							delayWorkDetail.put(Integer.valueOf(day).toString(), actualDelayHour);
						} else {
							delayWorkDetail.put(Integer.valueOf(day).toString(), expectDelayHour);
						}

					}
				}
				double totalDelayHours = 0;
				double overHoursOfAttendance = 0;
				EmpDelayHours empDelayHours = empDelayHoursMapper.getByEmpAndMonth(employee.getId(), startDay);
				if(empDelayHours != null){
					totalDelayHours = empDelayHours.getTotalDelayHours();
					overHoursOfAttendance = totalDelayHours - empDelayHours.getUsedDelayHours().doubleValue() - empDelayHours.getLockedDelayHours().doubleValue();
				}
				delayWorkDetail.put("count", totalDelayHours);
				delayWorkDetail.put("overHoursOfAttendance", overHoursOfAttendance);
				list.add(delayWorkDetail);
			}
		}
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet(fileName);
		// 表头标题样式
		HSSFCellStyle colstyle = workbook.createCellStyle();
		colstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中

		// 建立第一行表头
		HSSFRow row = sheet.createRow((short) 0);
		for (int colIndex = 0; colIndex < title1.size(); colIndex++) {
			if (colIndex >= 2 && colIndex < title1.size()) {
				sheet.setColumnWidth(colIndex, 1200);// 设置表格宽度
			} else {
				sheet.setColumnWidth(colIndex, 5000);// 设置表格宽度
			}

			Object obj = title1.get(colIndex);
			if (obj instanceof Long || obj instanceof Double || obj instanceof Integer || obj instanceof Float
					|| obj instanceof BigDecimal) {
				ExcelUtil.createRow(row, colIndex, colstyle, HSSFCell.CELL_TYPE_NUMERIC, obj);
			} else {
				ExcelUtil.createRow(row, colIndex, colstyle, HSSFCell.CELL_TYPE_STRING, obj);
			}
		}
		// 建立第二行表头
		row = sheet.createRow((short) 1);
		// 创建第二行表头内容
		for (int colIndex = 0; colIndex < title2.size(); colIndex++) {
			if (colIndex >= 2 && colIndex < title1.size()) {
				sheet.setColumnWidth(colIndex, 1200);// 设置表格宽度
			} else {
				sheet.setColumnWidth(colIndex, 5000);// 设置表格宽度
			}

			Object obj = title2.get(colIndex);
			if (obj instanceof Long || obj instanceof Double || obj instanceof Integer || obj instanceof Float
					|| obj instanceof BigDecimal) {
				ExcelUtil.createRow(row, colIndex, colstyle, HSSFCell.CELL_TYPE_NUMERIC, obj);
			} else {
				ExcelUtil.createRow(row, colIndex, colstyle, HSSFCell.CELL_TYPE_STRING, obj);
			}
		}

		// 延时加班数据
		for (int j = 0; j < list.size(); j++) {
			row = sheet.createRow((short) (2 + j));
			ExcelUtil.createRow(row, 0, colstyle, HSSFCell.CELL_TYPE_STRING, list.get(j).get("empCode"));
			ExcelUtil.createRow(row, 1, colstyle, HSSFCell.CELL_TYPE_STRING, list.get(j).get("empCnName"));
			for (int k = 2; k < days.size(); k++) {
				if (list.get(j).containsKey(days.get(k))) {
					if(list.get(j).get(days.get(k)) != null){
						ExcelUtil.createRow(row, k, colstyle, HSSFCell.CELL_TYPE_NUMERIC, list.get(j).get(days.get(k)));
					}
				}
			}
			ExcelUtil.createRow(row, days.size()-1, colstyle, HSSFCell.CELL_TYPE_NUMERIC, list.get(j).get("count"));
			ExcelUtil.createRow(row, days.size(), colstyle, HSSFCell.CELL_TYPE_NUMERIC, list.get(j).get("overHoursOfAttendance"));
		}

		return workbook;
	}

	@Override
	public Map<String,Object> getActualHours(Long employeeId, Date delayDate) {
		Map<String,Object> result= new HashMap<>();
		// 查询员工当天班次信息
		EmployeeClass param1 = new EmployeeClass();
		param1.setEmployId(employeeId);
		param1.setClassDate(delayDate);
		EmployeeClass empClass = employeeClassService.getEmployeeClassSetting(param1);
		String actualEndTime = "";// 结束时间（HH:mm）
		Date actualEndTime1 = null;// 结束时间（yyyy-MM-dd HH:mm:ss）
		String actuaStartTime = "";// 开始时间（HH:mm）
		Date actuaStartTime1 = null;// 开始时间（yyyy-MM-dd HH:mm:ss）
		double duration = 0;// 延时小时数
		boolean isRest = false;
		if (null != empClass) {
			// 正常工作日
			actuaStartTime = DateUtils.format(DateUtils.addHour(empClass.getEndTime(), 1), "HH:mm");
			actuaStartTime1 = DateUtils.addHour(empClass.getEndTime(), 1);
			AttnSignRecord record = new AttnSignRecord();
			record.setEmployeeId(employeeId);
			record.setStartTime(DateUtils.parse(
					DateUtils.format(DateUtils.addHour(empClass.getEndTime(), 1), DateUtils.FORMAT_LONG),
					DateUtils.FORMAT_LONG));
			record.setEndTime(DateUtils.parse(
					DateUtils.format(DateUtils.addHour(empClass.getEndTime(), 12), DateUtils.FORMAT_LONG),
					DateUtils.FORMAT_LONG));
			record.setEndTime(DateUtils.addMinute(record.getEndTime(), 20));
			List<AttnSignRecord> list = attnSignRecordService.getListBefore9(record);
			if (list != null && list.size() > 0) {
				actualEndTime = DateUtils.format(list.get(0).getSignTime(), "HH:mm");
				actualEndTime1 = list.get(0).getSignTime();
			}
			if (actualEndTime1 == null) {
				// 如果没有取得下班一小时后的打卡，看考勤统计表有无数据
				AttnStatistics attn = attnStatisticsMapper.getAttnStatistics(employeeId,
						delayDate);
				actualEndTime1 = attn != null ? attn.getEndWorkTime() : null;
			}
		} else {
			isRest = true;
			// 休息日
			AttnSignRecord record = new AttnSignRecord();
			Date nextDay = DateUtils.addDay(delayDate, 1);
			record.setEmployeeId(employeeId);
			record.setStartTime(
					DateUtils.parse(DateUtils.format(delayDate, DateUtils.FORMAT_SHORT) + " 06:00:00",
							DateUtils.FORMAT_LONG));
			record.setEndTime(DateUtils.parse(DateUtils.format(nextDay, DateUtils.FORMAT_SHORT) + " 06:00:00",
					DateUtils.FORMAT_LONG));
			List<AttnSignRecord> list3 = attnSignRecordService.getListBefore9(record);
			if (list3 != null && list3.size() > 0) {
				actualEndTime = DateUtils.format(list3.get(0).getSignTime(), "HH:mm");
				actuaStartTime = DateUtils.format(list3.get(list3.size() - 1).getSignTime(), "HH:mm");
				actuaStartTime1 = list3.get(list3.size() - 1).getSignTime();
				actualEndTime1 = list3.get(0).getSignTime();
			}
		}
		// 实际小时数
		if (!"".equals(actuaStartTime) && !"".equals(actualEndTime)) {
			int sH = Integer.valueOf(actuaStartTime.substring(0, 2));
			int sM = Integer.valueOf(actuaStartTime.substring(3, 5));
			int eH = Integer.valueOf(actualEndTime.substring(0, 2));
			int eM = Integer.valueOf(actualEndTime.substring(3, 5));
			if (sM > 0 && sM < 30) {
				sM = 30;
			}
			if (sM > 30 && sM <= 59) {
				sM = 0;
				sH = sH + 1;
			}
			if (eM > 0 && eM < 30) {
				eM = 0;
			}
			if (eM > 30 && eM <= 59) {
				eM = 30;
			}
			duration = GetHours.getHours(sH, sM, eH, eM);
			if (isRest) {
				if (duration >= 10) {
					duration = duration - 2;
				} else if (duration >= 5) {
					duration = duration - 1;
				}
			}
		}
		result.put("startTime", actuaStartTime1);
		result.put("endTime", actualEndTime1);
		result.put("workHours", duration);
		return result;
	}

	@Override
	public Map<String, Object> toReview(Long empId, String month) {
		User user = userService.getCurrentUser();
		Map<String,Object> result= new HashMap<String,Object>();
		if(empId == null || month == null){
			result.put("message", "参数错误！");
			result.put("success", false);
		}
		EmpDelayHours empDelayHours = empDelayHoursMapper.getByEmpAndMonth(empId, DateUtils.parse(month+"-01",DateUtils.FORMAT_SHORT));
		//可删除未确认的登记
		if(empDelayHours != null){
			empDelayHours.setStatus(1);
			empDelayHours.setUpdateTime(new Date());
			empDelayHours.setUpdateUser(user.getEmployee().getId().toString());
			empDelayHoursMapper.updateEmpDelayHours(empDelayHours);
			result.put("success", true);
			result.put("message", "复核通过！");
		}else{
			result.put("message", "没有可复核的延时工作登记！");
			result.put("success", false);
		}
		
		return result;
	}
	
}
