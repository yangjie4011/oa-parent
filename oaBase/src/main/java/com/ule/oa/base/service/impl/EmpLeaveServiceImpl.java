package com.ule.oa.base.service.impl;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.flowable.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.ule.arch.basehttpclient.BaseHttpClientFactory;
import com.ule.arch.basehttpclient.client.BaseHttpClient;
import com.ule.arch.basehttpclient.model.HttpRequest;
import com.ule.arch.basehttpclient.model.HttpResponse;
import com.ule.arch.basehttpclient.standard.util.ContentCoverter;
import com.ule.oa.base.cache.ConfigCacheManager;
import com.ule.oa.base.common.OaCommon;
import com.ule.oa.base.mapper.AnnualVacationMapper;
import com.ule.oa.base.mapper.ApplicationEmployeeDutyMapper;
import com.ule.oa.base.mapper.EmpApplicationAbnormalAttendanceMapper;
import com.ule.oa.base.mapper.EmpApplicationBusinessMapper;
import com.ule.oa.base.mapper.EmpApplicationLeaveAbolishMapper;
import com.ule.oa.base.mapper.EmpApplicationLeaveDetailMapper;
import com.ule.oa.base.mapper.EmpApplicationLeaveMapper;
import com.ule.oa.base.mapper.EmpApplicationOutgoingMapper;
import com.ule.oa.base.mapper.EmpApplicationOvertimeMapper;
import com.ule.oa.base.mapper.EmpDepartMapper;
import com.ule.oa.base.mapper.EmpLeaveMapper;
import com.ule.oa.base.mapper.EmpLeaveReportMapper;
import com.ule.oa.base.mapper.EmpLeaveTaskMapper;
import com.ule.oa.base.mapper.EmployeeAppMapper;
import com.ule.oa.base.mapper.EmployeeClassMapper;
import com.ule.oa.base.mapper.EmployeeMapper;
import com.ule.oa.base.mapper.LeaveRecordDetailMapper;
import com.ule.oa.base.mapper.LeaveRecordMapper;
import com.ule.oa.base.mapper.RabcUserMapper;
import com.ule.oa.base.mapper.RemoveSubordinateAbsenceMapper;
import com.ule.oa.base.po.AnnualVacation;
import com.ule.oa.base.po.ApplicationEmployeeClass;
import com.ule.oa.base.po.ApplicationEmployeeDuty;
import com.ule.oa.base.po.AttnWorkHours;
import com.ule.oa.base.po.ClassSetting;
import com.ule.oa.base.po.CompanyConfig;
import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.EmpApplicationAbnormalAttendance;
import com.ule.oa.base.po.EmpApplicationBusiness;
import com.ule.oa.base.po.EmpApplicationLeave;
import com.ule.oa.base.po.EmpApplicationLeaveAbolish;
import com.ule.oa.base.po.EmpApplicationLeaveDetail;
import com.ule.oa.base.po.EmpApplicationOutgoing;
import com.ule.oa.base.po.EmpApplicationOvertime;
import com.ule.oa.base.po.EmpDepart;
import com.ule.oa.base.po.EmpLeave;
import com.ule.oa.base.po.EmpLeaveReport;
import com.ule.oa.base.po.EmpLeaveTask;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeApp;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.po.LeaveRecord;
import com.ule.oa.base.po.LeaveRecordDetail;
import com.ule.oa.base.po.RemoveSubordinateAbsence;
import com.ule.oa.base.po.RequestQueryLeaveRecord;
import com.ule.oa.base.po.ResponseQueryLeaveRecord;
import com.ule.oa.base.po.User;
import com.ule.oa.base.po.tbl.SysUpdateLogTbl;
import com.ule.oa.base.service.AnnualVacationService;
import com.ule.oa.base.service.AttnStatisticsService;
import com.ule.oa.base.service.AttnWorkHoursService;
import com.ule.oa.base.service.ClassSettingService;
import com.ule.oa.base.service.CompanyConfigService;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmpLeaveReportService;
import com.ule.oa.base.service.EmpLeaveService;
import com.ule.oa.base.service.EmpLeaveTaskService;
import com.ule.oa.base.service.EmployeeAppService;
import com.ule.oa.base.service.EmployeeClassService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.SmsService;
import com.ule.oa.base.service.SysUpdateLogService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.util.AttnStatisticsUtil;
import com.ule.oa.base.util.SendMailUtil;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.spring.config.CustomPropertyPlaceholderConfigurer;
import com.ule.oa.common.utils.CommonUtils;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.ExcelUtil;
import com.ule.oa.common.utils.PageModel;
import com.ule.oa.common.utils.json.JSONUtils;

/**
 * @ClassName: EmpLeaveServiceImpl
 * @Description: 员工假期业务层
 * @author minsheng
 * @date 2017年6月13日 下午12:55:03
 */
@Service
public class EmpLeaveServiceImpl implements EmpLeaveService {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private EmpLeaveMapper empLeaveMapper;
	@Autowired
	private EmployeeAppService employeeAppService;
	@Autowired
	private EmployeeMapper employeeMapper;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private EmployeeAppMapper employeeAppMapper;
	@Autowired
	private EmpLeaveTaskMapper empLeaveTaskMapper;
	@Autowired
	private EmpLeaveTaskService empLeaveTaskService;
	@Autowired
	private CompanyConfigService companyConfigService;
	@Autowired
	private SmsService smsService;
	@Autowired
	private ConfigService configService;
	@Autowired
	private DepartService departService;
	@Autowired
	private UserService userService;
	@Autowired
	private EmpApplicationLeaveDetailMapper empApplicationLeaveDetailMapper;
	@Autowired
	private EmpApplicationLeaveMapper empApplicationLeaveMapper;
	@Resource
	private AnnualVacationService annualVacationService;
	@Resource
	private AttnStatisticsService attnStatisticsService;
	@Autowired
	private EmpLeaveReportService empLeaveReportService;
	@Autowired
	private EmpLeaveReportMapper empLeaveReportMapper;
	@Resource
	private ConfigCacheManager configCacheManager;
	@Autowired
	private SysUpdateLogService sysUpdateLogService;
	@Autowired
	private ActivitiServiceImpl activitiServiceImpl;
	@Resource
	private AttnWorkHoursService attnWorkHoursService;
	@Autowired
	private AnnualVacationMapper annualVacationMapper;
	@Autowired
	private ClassSettingService classSettingService;
	@Autowired
	private EmployeeClassService employeeClassService;
	// 定时失效
	@Autowired
	EmpApplicationLeaveAbolishMapper empApplicationLeaveAbolishMapper;
	@Autowired
	private EmpApplicationOutgoingMapper empApplicationOutgoingMapper;
	@Autowired
	private EmpApplicationBusinessMapper empApplicationBusinessMapper;
	@Autowired
	private EmpApplicationAbnormalAttendanceMapper empApplicationAbnormalAttendanceMapper;
	@Autowired
	private EmpApplicationOvertimeMapper empApplicationOvertimeMapper;
	@Autowired
	private EmployeeClassMapper employeeClassMapper;
	@Autowired
	private ApplicationEmployeeDutyMapper applicationEmployeeDutyMapper;
	@Autowired
	private LeaveRecordMapper leaveRecordMapper;
	@Autowired
	private LeaveRecordDetailMapper leaveRecordDetailMapper;
	@Autowired
	private EmpDepartMapper empDepartMapper;
	@Autowired
	private RemoveSubordinateAbsenceMapper removeSubordinateAbsenceMapper;
	@Autowired
	private RabcUserMapper rabcUserMapper;
	
	private static BaseHttpClient client = BaseHttpClientFactory.getClient();

	
	// 病假和年假每次跑200个任务
	private static Integer TASK_SIZE = 200;
	// 获取环境
	private final static String ENV = (String) CustomPropertyPlaceholderConfigurer.getProperty("cache.env");
	// 2017年假期特殊处理
	private final static String YEAR_2017 = "2017";
	// 最小假期数为3条记录
	private final static Integer LEAVE_MIN_COUNT_3 = 3;

	/**
	 * @throws Exception
	 * @throws OaException
	 *             splitLeave(如果申请的假期存在跨年或者跨延期节点，则需要拆分后扣减假期) @Title:
	 *             splitLeave @Description:
	 *             如果申请的假期存在跨年或者跨延期节点，则需要拆分后扣减假期 @return 设定文件 List<EmpLeave>
	 *             返回类型 @throws
	 */
	public List<EmpLeave> splitLeave(EmpLeave planleave) throws Exception {
		Date startTime = planleave.getPlanStartTime();// 计划请假开始时间
		Date endTime = planleave.getPlanEndTime();// 计划请假结束时间
		
		Date nodeTime1 = DateUtils.parse(DateUtils.getYear(startTime) + "-12-31 23:00:00", DateUtils.FORMAT_LONG);// 12月31号
		Date nodeTime2 = DateUtils.parse(DateUtils.getYear(startTime) + "-03-31 23:00:00", DateUtils.FORMAT_LONG);// 当年3月31号
		Long employeeId = planleave.getEmployeeId();
		
		EmployeeClass employeeParam = new EmployeeClass(); 
		employeeParam.setEmployId(employeeId);
		employeeParam.setClassDate(DateUtils.parse(DateUtils.getYear(startTime) + "-12-31",DateUtils.FORMAT_SHORT));
		EmployeeClass class_12_31 = employeeClassService.getEmployeeClassSetting(employeeParam);
		if(class_12_31!=null&&class_12_31.getEndTime()!=null){
			nodeTime1 = DateUtils.parse(DateUtils.format(class_12_31.getClassDate(), DateUtils.FORMAT_SHORT) 
					+" "+ DateUtils.format(class_12_31.getEndTime(), DateUtils.FORMAT_HH_MM) + ":00", DateUtils.FORMAT_LONG);
			if(class_12_31.getIsInterDay()==1){
				nodeTime1 = DateUtils.addDay(nodeTime1, 1);
			}
		}
		employeeParam.setClassDate(DateUtils.parse(DateUtils.getYear(startTime) + "-03-31",DateUtils.FORMAT_SHORT));
		EmployeeClass class_03_31 = employeeClassService.getEmployeeClassSetting(employeeParam);
		if(class_03_31!=null&&class_03_31.getEndTime()!=null){
			nodeTime2 = DateUtils.parse(DateUtils.format(class_03_31.getClassDate(), DateUtils.FORMAT_SHORT) 
					+" "+ DateUtils.format(class_03_31.getEndTime(), DateUtils.FORMAT_HH_MM) + ":00", DateUtils.FORMAT_LONG);
			if(class_03_31.getIsInterDay()==1){
				nodeTime2 = DateUtils.addDay(nodeTime2, 1);
			}
		}
		
		String leaveType = String.valueOf(planleave.getType());
		List<EmpLeave> leaves = new ArrayList<EmpLeave>();

		// 1.判断是否存在跨年或者跨延期节点（例如：12月31号为跨年节点，3月31号为假期延期节点）
		logger.info("拆分前的开始时间={},结束时间={},休假天数=" + planleave.getPlanDays(),
				DateUtils.format(planleave.getPlanStartTime()), DateUtils.format(planleave.getPlanEndTime()));
		if (startTime.compareTo(nodeTime1) < 0 && endTime.compareTo(nodeTime1) > 0) {// 按照12月31号节点拆分假期
			// 按照12月31号拆分假期，假期为两段，12月31号之前为第一段假期，12月31号之后为第二段假期。第一段假期只能扣减17年假期，第二段假期优先扣除延期假期（假如17年年假延期）然后再扣除18年假期
			String startTime1 = DateUtils.format(startTime, DateUtils.FORMAT_SHORT);// 第一段假期开始日期
			String startHour1 = DateUtils.getHours(startTime);// 第一段假期开始小时
			String endTime1 = DateUtils.format(nodeTime1, DateUtils.FORMAT_SHORT);// 第一段假期结束日期
			String endHour1 = DateUtils.getHours(nodeTime1);// 第一段假期结束小时

			String startTime2 = DateUtils.format(DateUtils.addDay(nodeTime1, 1), DateUtils.FORMAT_SHORT);// 第二段假期开始日期
			String startHour2 = "09";// 第二段假期开始小时
			String endTime2 = DateUtils.format(endTime, DateUtils.FORMAT_SHORT);// 第二段假期结束日期
			String endHour2 = DateUtils.getHours(endTime);// 第二段假期结束小时

			splitLeave(leaves, planleave.getEmployeeId(), startTime1, startHour1, endTime1, endHour1, leaveType,
					planleave.getOptUser(), planleave.getApplyStatus());
			splitLeave(leaves, planleave.getEmployeeId(), startTime2, startHour2, endTime2, endHour2, leaveType,
					planleave.getOptUser(), planleave.getApplyStatus());
		} else if (startTime.compareTo(nodeTime2) < 0 && endTime.compareTo(nodeTime2) > 0  && planleave.getType()!=2) {// 按照3月31号节点拆分假期 注：这里排除病假
			// 按照3月31号拆分假期,假期为两段，3月31号之前为第一段假期，3月31号之后为第二段假期。第一段假期优先扣减17年假期然后再扣减18年假期，第二段假期只能扣减18年假期
			String startTime1 = DateUtils.format(startTime, DateUtils.FORMAT_SHORT);// 第一段假期开始日期
			String startHour1 = DateUtils.getHours(startTime);// 第一段假期开始小时
			String endTime1 = DateUtils.format(nodeTime2, DateUtils.FORMAT_SHORT);// 第一段假期结束日期
			String endHour1 = DateUtils.getHours(nodeTime2);// 第一段假期结束小时

			String startTime2 = DateUtils.format(DateUtils.addDay(nodeTime2, 1), DateUtils.FORMAT_SHORT);
			// 第二段假期开始日期
			String startHour2 = "09";// 第二段假期开始小时
			employeeParam.setClassDate(DateUtils.addDay(nodeTime2, 1));
			EmployeeClass class_04_01 = employeeClassService.getEmployeeClassSetting(employeeParam);
			if(class_04_01!=null&&class_04_01.getStartTime()!=null){
				 startHour2 = DateUtils.getHours(class_04_01.getStartTime());// 第二段假期结束小时
			}
			
			String endTime2 = DateUtils.format(endTime, DateUtils.FORMAT_SHORT);// 第二段假期结束日期
			String endHour2 = DateUtils.getHours(endTime);// 第二段假期结束小时

			splitLeave(leaves, planleave.getEmployeeId(), startTime1, startHour1, endTime1, endHour1, leaveType,
					planleave.getOptUser(), planleave.getApplyStatus());
			splitLeave(leaves, planleave.getEmployeeId(), startTime2, startHour2, endTime2, endHour2, leaveType,
					planleave.getOptUser(), planleave.getApplyStatus());
		} else {// 其它,则不用拆分
			logger.info("假期无需拆分!!!");
			leaves.add(planleave);
		}

		return leaves;
	}
	
	public void bulidEmpLeave(List<EmpLeave> result,EmpLeave planleave,Double planDays,Date startTime,Date endTime){
		EmpLeave data = new EmpLeave();
		data.setEmployeeId(planleave.getEmployeeId());
		data.setType(planleave.getType());
		data.setPlanDays(planDays);
		data.setPlanStartTime(startTime);
		data.setPlanEndTime(endTime);
		data.setApplyStatus(planleave.getApplyStatus());
		result.add(data);
	}
	
	//把调休请假单拆分成天
	public List<EmpLeave> splitRestLeave(EmpLeave planleave) throws Exception {
		List<EmpLeave> result = new ArrayList<EmpLeave>();
		Employee emp = employeeService.getById(planleave.getEmployeeId());
		String startTime1 = DateUtils.format(planleave.getPlanStartTime(), DateUtils.FORMAT_LONG).substring(0,10);
		String startTime2 = DateUtils.format(planleave.getPlanStartTime(), DateUtils.FORMAT_LONG).substring(11,13);
		String endTime1 = DateUtils.format(planleave.getPlanEndTime(), DateUtils.FORMAT_LONG).substring(0,10);
		String endTime2 = DateUtils.format(planleave.getPlanEndTime(), DateUtils.FORMAT_LONG).substring(11,13);
		Map<Long, ClassSetting> csMap = new HashMap<Long, ClassSetting>();
		List<ClassSetting> csList = classSettingService.getList();
		for (ClassSetting classSetting : csList) {
			csMap.put(classSetting.getId(), classSetting);
		}
		// 开始时间
		Date dayStart = DateUtils.parse(startTime1, DateUtils.FORMAT_SHORT);
		// 结束时间
		Date dayEnd = DateUtils.parse(endTime1, DateUtils.FORMAT_SHORT);
		String beginData = "";
		String endData = "";
		int length = DateUtils.getIntervalDays(dayStart, dayEnd) + 1;
		if (OaCommon.getLeaveMap().containsKey(5)) {
			beginData = startTime1;
			endData = endTime1;
		}
	    Map<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
			boolean isEndTime = true;
			List<String> datas = new ArrayList<String>();
			do {
				datas.add(DateUtils.format(dayStart, DateUtils.FORMAT_SHORT));
				dayStart = DateUtils.addDay(dayStart, 1);
				if (DateUtils.getIntervalDays(dayStart, dayEnd) < 0) {
					isEndTime = false;
				}
			} while (isEndTime);
			
			resultMap = employeeClassService.getEmployeeClassSetting(emp, datas);
			beginData = (String) resultMap.get("beginData");
			endData = (String) resultMap.get("endData");
			for (Map.Entry<String, Object> entry : resultMap.entrySet()) {
				if (!entry.getKey().equals("beginData") && !entry.getKey().equals("endData")
						&& !entry.getKey().equals(startTime1) && !entry.getKey().equals(endTime1)) {
					EmployeeClass result_e = (EmployeeClass) resultMap.get(entry.getKey());
					if (result_e != null) {
						if (result_e.getClassDate() != null) {
							bulidEmpLeave(result,planleave,result_e.getMustAttnTime(),result_e.getStartTime(),result_e.getEndTime());
						}
					}
				}
			}

			if (beginData.equals(startTime1)) {
				EmployeeClass classSetting = (EmployeeClass) resultMap.get(beginData);
				Boolean isCommon = false;
				if (classSetting != null) {
					isCommon = true;
				}
				if (isCommon) {
					boolean standard = ("09:00".equals(DateUtils.format(classSetting.getStartTime(), "HH:mm"))
							&& "18:00".equals(DateUtils.format(classSetting.getEndTime(), "HH:mm"))) ? true : false;
					boolean standard1 = (classSetting.getMustAttnTime() == 8) ? true : false;
					boolean standard2 = (classSetting.getMustAttnTime() >= 16) ? true : false;
					boolean standard3 = (classSetting.getMustAttnTime() >= 10 && classSetting.getMustAttnTime() < 16)
							? true : false;
					boolean standard4 = (classSetting.getMustAttnTime() < 8) ? true : false;
					if (standard) {
						if (length == 1) {
							if ((Integer.valueOf(startTime2).intValue() == 9
									&& Integer.valueOf(endTime2).intValue() == 14)
									|| (Integer.valueOf(startTime2).intValue() == 9
											&& Integer.valueOf(endTime2).intValue() == 13)) {
								bulidEmpLeave(result,planleave,3d,planleave.getPlanStartTime(),planleave.getPlanEndTime());
							} else if (Integer.valueOf(startTime2).intValue() == 12
									&& Integer.valueOf(endTime2).intValue() == 18) {
								bulidEmpLeave(result,planleave,5d,planleave.getPlanStartTime(),planleave.getPlanEndTime());
							} else if (Integer.valueOf(startTime2).intValue() == 9
									&& Integer.valueOf(endTime2).intValue() == 18) {
								bulidEmpLeave(result,planleave,8d,planleave.getPlanStartTime(),planleave.getPlanEndTime());
							}
						} else {
							if (Integer.valueOf(startTime2).intValue() == 9) {
								bulidEmpLeave(result,planleave,8d,planleave.getPlanStartTime(),classSetting.getEndTime());
							} else if (Integer.valueOf(startTime2).intValue() == 12) {
								bulidEmpLeave(result,planleave,5d,planleave.getPlanStartTime(),classSetting.getEndTime());
							}
						}
					} else if (standard1) {
						if (length == 1) {
							if (Integer.valueOf(startTime2).intValue() == Integer.valueOf(DateUtils.format(classSetting.getStartTime(),"HH")).intValue()
									&& Integer.valueOf(endTime2).intValue() == Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getEndTime(), -300), "HH")).intValue()) {
								bulidEmpLeave(result,planleave,4d,planleave.getPlanStartTime(),planleave.getPlanEndTime());
							} else if (Integer.valueOf(startTime2).intValue() == Integer.valueOf(
									DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 300), "HH"))
									.intValue()
									&& Integer.valueOf(endTime2).intValue() == Integer
											.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()) {
								bulidEmpLeave(result,planleave,4d,planleave.getPlanStartTime(),planleave.getPlanEndTime());
							} else if (Integer.valueOf(startTime2).intValue() == Integer
									.valueOf(DateUtils.format(classSetting.getStartTime(), "HH")).intValue()
									&& Integer.valueOf(endTime2).intValue() == Integer
											.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()) {
								bulidEmpLeave(result,planleave,8d,planleave.getPlanStartTime(),planleave.getPlanEndTime());
							}
						} else {
							if (Integer.valueOf(startTime2).intValue() == Integer
									.valueOf(DateUtils.format(classSetting.getStartTime(), "HH")).intValue()) {
								bulidEmpLeave(result,planleave,8d,planleave.getPlanStartTime(),classSetting.getEndTime());
							} else if (Integer.valueOf(startTime2).intValue() == Integer.valueOf(
									DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 300), "HH"))
									.intValue()) {
								bulidEmpLeave(result,planleave,4d,planleave.getPlanStartTime(),classSetting.getEndTime());
							}
						}
					} else if (standard2) {
						if (length == 1) {
							if (Integer.valueOf(startTime2).intValue() == Integer
									.valueOf(DateUtils.format(classSetting.getStartTime(), "HH")).intValue()
									&& Integer.valueOf(endTime2).intValue() == Integer
											.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()) {
								bulidEmpLeave(result,planleave,16d,planleave.getPlanStartTime(),planleave.getPlanEndTime());
							} else if (Integer.valueOf(startTime2).intValue() == Integer.valueOf(
									DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 240), "HH"))
									.intValue()
									&& Integer.valueOf(endTime2).intValue() == Integer
											.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()) {
								bulidEmpLeave(result,planleave,12d,planleave.getPlanStartTime(),planleave.getPlanEndTime());
							} else if (Integer.valueOf(startTime2).intValue() == Integer.valueOf(
									DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 480), "HH"))
									.intValue()
									&& Integer.valueOf(endTime2).intValue() == Integer
											.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()) {
								bulidEmpLeave(result,planleave,8d,planleave.getPlanStartTime(),planleave.getPlanEndTime());
							} else if (Integer.valueOf(startTime2).intValue() == Integer.valueOf(
									DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 720), "HH"))
									.intValue()
									&& Integer.valueOf(endTime2).intValue() == Integer
											.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()) {
								bulidEmpLeave(result,planleave,4d,planleave.getPlanStartTime(),planleave.getPlanEndTime());
							}
						} else {
							if (Integer.valueOf(startTime2).intValue() == Integer
									.valueOf(DateUtils.format(classSetting.getStartTime(), "HH")).intValue()) {
								bulidEmpLeave(result,planleave,16d,planleave.getPlanStartTime(),classSetting.getEndTime());
							} else if (Integer.valueOf(startTime2).intValue() == Integer.valueOf(
									DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 240), "HH"))
									.intValue()) {
								bulidEmpLeave(result,planleave,12d,planleave.getPlanStartTime(),classSetting.getEndTime());
							} else if (Integer.valueOf(startTime2).intValue() == Integer.valueOf(
									DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 480), "HH"))
									.intValue()) {
								bulidEmpLeave(result,planleave,8d,planleave.getPlanStartTime(),classSetting.getEndTime());
							} else if (Integer.valueOf(startTime2).intValue() == Integer.valueOf(
									DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 720), "HH"))
									.intValue()) {
								bulidEmpLeave(result,planleave,4d,planleave.getPlanStartTime(),classSetting.getEndTime());
							}
						}
					} else if (standard3) {
						if (length == 1) {
							if (Integer.valueOf(startTime2).intValue() == Integer
									.valueOf(DateUtils.format(classSetting.getStartTime(), "HH")).intValue()
									&& Integer.valueOf(endTime2).intValue() == Integer
											.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()) {
								bulidEmpLeave(result,planleave,12d,planleave.getPlanStartTime(),planleave.getPlanEndTime());
							} else if (Integer.valueOf(startTime2).intValue() == Integer.valueOf(
									DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 240), "HH"))
									.intValue()
									&& Integer.valueOf(endTime2).intValue() == Integer
											.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()) {
								bulidEmpLeave(result,planleave,8d,planleave.getPlanStartTime(),planleave.getPlanEndTime());
							} else if (Integer.valueOf(startTime2).intValue() == Integer.valueOf(
									DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 480), "HH"))
									.intValue()
									&& Integer.valueOf(endTime2).intValue() == Integer
											.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()) {
								bulidEmpLeave(result,planleave,4d,planleave.getPlanStartTime(),planleave.getPlanEndTime());
							}
						} else {
							if (Integer.valueOf(startTime2).intValue() == Integer
									.valueOf(DateUtils.format(classSetting.getStartTime(), "HH")).intValue()) {
								bulidEmpLeave(result,planleave,12d,planleave.getPlanStartTime(),classSetting.getEndTime());
							} else if (Integer.valueOf(startTime2).intValue() == Integer.valueOf(
									DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 240), "HH"))
									.intValue()) {
								bulidEmpLeave(result,planleave,8d,planleave.getPlanStartTime(),classSetting.getEndTime());
							} else if (Integer.valueOf(startTime2).intValue() == Integer.valueOf(
									DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 480), "HH"))
									.intValue()) {
								bulidEmpLeave(result,planleave,4d,planleave.getPlanStartTime(),classSetting.getEndTime());
							}
						}
					}else if(standard4){
						bulidEmpLeave(result,planleave,classSetting.getMustAttnTime(),planleave.getPlanStartTime(),classSetting.getEndTime());
					}
				}
			}

			if (length > 1 && endData.equals(endTime1)) {
				EmployeeClass classSetting = (EmployeeClass) resultMap.get(endData);
				Boolean isCommon = false;
				if (classSetting != null) {
					isCommon = true;
				}
				if (isCommon) {
					boolean standard = ("09:00".equals(DateUtils.format(classSetting.getStartTime(), "HH:mm"))
							&& "18:00".equals(DateUtils.format(classSetting.getEndTime(), "HH:mm"))) ? true : false;
					boolean standard1 = (classSetting.getMustAttnTime() == 8) ? true : false;
					boolean standard2 = (classSetting.getMustAttnTime() >= 16) ? true : false;
					boolean standard3 = (classSetting.getMustAttnTime() >= 10 && classSetting.getMustAttnTime() < 16)
							? true : false;
					boolean standard4 = (classSetting.getMustAttnTime() < 8) ? true : false;
					if (standard) {
						if (Integer.valueOf(endTime2).intValue() == 18) {
							bulidEmpLeave(result,planleave,8d,classSetting.getStartTime(),planleave.getPlanEndTime());
						} else if (Integer.valueOf(endTime2).intValue() == 14
								|| Integer.valueOf(endTime2).intValue() == 13) {
							bulidEmpLeave(result,planleave,3d,classSetting.getStartTime(),planleave.getPlanEndTime());
						}
					} else if (standard1) {
						if (Integer.valueOf(endTime2).intValue() == Integer
								.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()) {
							bulidEmpLeave(result,planleave,8d,classSetting.getStartTime(),planleave.getPlanEndTime());
						} else if (Integer.valueOf(endTime2).intValue() == Integer
								.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getEndTime(), -300), "HH"))
								.intValue()) {
							bulidEmpLeave(result,planleave,4d,classSetting.getStartTime(),planleave.getPlanEndTime());
						}
					} else if (standard2) {
						if (Integer.valueOf(endTime2).intValue() == Integer
								.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()) {
							bulidEmpLeave(result,planleave,16d,classSetting.getStartTime(),planleave.getPlanEndTime());
						}
					} else if (standard3) {
						if (Integer.valueOf(endTime2).intValue() == Integer
								.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()) {
							bulidEmpLeave(result,planleave,12d,classSetting.getStartTime(),planleave.getPlanEndTime());
						}
					}else if(standard4){
						bulidEmpLeave(result,planleave,classSetting.getMustAttnTime(),classSetting.getStartTime(),planleave.getPlanEndTime());
					}
				}
			}
		} catch (Exception e) {
			logger.error(emp.getCnName() + "拆分调休：" + e.toString());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<EmpLeave> splitLeave(List<EmpLeave> leaves, Long empId, String startTime, String startHour,
			String endTime, String endHour, String leaveType, String optUser, boolean applyStatus) throws Exception {
		try {
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("empId", empId + "");
			paramMap.put("startTime1", startTime);
			paramMap.put("startTime2", startHour);
			paramMap.put("endTime1", endTime);
			paramMap.put("endTime2", endHour);
			paramMap.put("leaveType", leaveType);

			String OA_URL = configCacheManager.getConfigDisplayCode("MO_WEB_URL");
			
			HttpRequest req = new HttpRequest.Builder().url(OA_URL + "/empApplicationLeave/getLeaveDays.htm").post(HttpRequest.HttpMediaType.FORM_URLENCODE, 
    				ContentCoverter.formConvertAsString(paramMap)).build();
    		HttpResponse rep = client.sendRequest(req);
			String response = rep.fullBody();
			
			Map<String, Object> resMap = JSONUtils.read(response, Map.class);

			EmpLeave leave = new EmpLeave();
			leave.setType(Integer.parseInt(leaveType));
			leave.setEmployeeId(empId);
			leave.setPlanStartTime(DateUtils.parse(startTime + " " + startHour + ":00:00"));
			leave.setPlanEndTime(DateUtils.parse(endTime + " " + endHour + ":00:00"));
			if (leaveType.equals(ConfigConstants.LEAVE_TYPE_1 + "")) {// 年假
				leave.setPlanDays((Double) resMap.get("leaveDays"));
			} else if (leaveType.equals(ConfigConstants.LEAVE_TYPE_5 + "")) {// 调休
				leave.setPlanDays((Double) resMap.get("leaveHours"));
			} else if (leaveType.equals(ConfigConstants.LEAVE_TYPE_2 + "")) {// 病假   新增病假
				leave.setPlanDays((Double) resMap.get("leaveDays"));
			}
			leave.setOptUser(optUser);
			leave.setApplyStatus(applyStatus);

			leaves.add(leave);

			logger.info("拆分后的开始时间={},结束时间={},休假天数=" + leave.getPlanDays(), DateUtils.format(leave.getPlanStartTime()),
					DateUtils.format(leave.getPlanEndTime()));
		} catch (Exception e) {
			logger.error("假期拆分报错,错误原因={}", e.getMessage());
			throw e;
		}
		return leaves;
	}

	public boolean checkLeaveIsValidate(EmpLeave planleave) throws OaException {
		// 验证假期在不在有效期范围内
		Map<String, Object> map = empLeaveMapper.checkLeaveIsValidate(planleave);
		if (null == map || map.isEmpty()) {
			logger.error("假期不在有效期范围内");
			return false;
		}

		// 验证可用假期
		Double allow_remain_days = (Double) map.get("allow_remain_days");// 总的允许可休假剩余天数
		Double blocked_days = (Double) map.get("blocked_days");// 总的占用天数
		Double planDays = planleave.getPlanDays();
		Integer planType = planleave.getType();
		if (planDays > allow_remain_days - blocked_days) {
			if (planType.equals(ConfigConstants.LEAVE_TYPE_1)) {// 年假
				throw new OaException("您的当前允许可用年假为" + allow_remain_days + "天,其中审批中的年假为" + blocked_days + "天,计划休假应小于或等于"
						+ (allow_remain_days - blocked_days) + "天");
			} else if (planType.equals(ConfigConstants.LEAVE_TYPE_2)) {// 病假
				throw new OaException("您的当前允许可用病假为" + allow_remain_days + "天,其中审批中的病假为" + blocked_days + "天,计划休假应小于或等于"
						+ (allow_remain_days - blocked_days) + "天");
			} else if (planType.equals(ConfigConstants.LEAVE_TYPE_5)) {// 调休
				throw new OaException("您的当前允许可用调休为" + allow_remain_days + "小时,其中审批中的调休小时为" + blocked_days
						+ "小时,计划休假应小于或等于" + (allow_remain_days - blocked_days) + "小时");
			}
		}

		return true;
	}

	/**
	 * @throws Exception
	 *             updateEmpLeaveApply(请假申请调的接口) @Title:
	 *             updateEmpLeaveApply @Description: 请假申请调的接口 @param
	 *             planLeaves @return @throws Exception 设定文件 boolean
	 *             返回类型 @throws
	 */
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
	public boolean updateEmpLeaveApply(List<Map<String, Object>> planLeaves) throws Exception {
		boolean flag = false;// 返回状态

		for (Map<String, Object> leaveMap : planLeaves) {
			EmpLeave planLeave = JSONUtils.read(JSONUtils.write(leaveMap), EmpLeave.class);
			// 非空校验
			if (null == planLeave) {
				throw new OaException("您当前的请假信息为空");
			} else {
				if (null == planLeave.getEmployeeId()) {
					throw new OaException("您当前的请假的员工id为空");
				}
				if (null == planLeave.getType()) {
					throw new OaException("您当前的请假的假期类型为空");
				}
				if (null == planLeave.getPlanStartTime()) {
					throw new OaException("您当前的请假的开始时间为空");
				}
				if (null == planLeave.getPlanEndTime()) {
					throw new OaException("您当前的请假的结束时间为空");
				}
				if (null == planLeave.getPlanDays()) {
					throw new OaException("您当前的请假的天数为空");
				}
				if (StringUtils.isBlank(planLeave.getOptUser())) {
					throw new OaException("假期申请人不能为空");
				}
			}

			// 根据计划休假信息找到总的剩余假期信息
			if (planLeave.getType().intValue() == ConfigConstants.LEAVE_TYPE_1.intValue()) {// 年假
				List<EmpLeave> leaves = splitLeave(planLeave);// 拆分假期
				flag = updateBolckedYearLeave(leaves);
			} else if (planLeave.getType().intValue() == ConfigConstants.LEAVE_TYPE_5.intValue()) {// 调休
				List<EmpLeave> leaves = splitRestLeave(planLeave);// 拆分假期
				flag = updateBolckedRestLeave(leaves);
			} else {
				flag = true;
			}
		}

		return flag;
	}

	public Long saveLeaveRecord(EmpLeave planLeave, String billType) {
		LeaveRecord record = new LeaveRecord();
		record.setEmployeeId(planLeave.getEmployeeId());
		record.setType(planLeave.getType());
		record.setDays(planLeave.getPlanDays());
		record.setBillId(planLeave.getEmpapplicationleaveId());
		record.setBillType(billType);
		record.setDaysUnit(0);// 单位
	    if(planLeave.getType()==ConfigConstants.LEAVE_TYPE_5.intValue()){
	    	record.setDaysUnit(1);// 单位
		}
		record.setCreateTime(new Date());
		record.setCreateUser(planLeave.getOptUser());
		record.setDelFlag(0);
		record.setSource(0);
		record.setRemark("请假申请");
		if (ConfigConstants.CANCELLEAVE_KEY.equals(billType)) {
			record.setRemark("销假申请");
			record.setDays(-planLeave.getPlanDays());
		}else if(ConfigConstants.REGISTER_LEAVE_KEY.equals(billType)) {
			record.setRemark(planLeave.getRemark());
			record.setSource(2);
		}
		leaveRecordMapper.save(record);
		return record.getId();
	}

	/*public boolean updateBolckedRestLeave(List<EmpLeave> planLeaves) throws OaException {
		boolean flag = false;

		for (EmpLeave planLeave : planLeaves) {// 拆分以后的假期，分两段扣减(如果不需要拆分，则只有一段假期)
			Double planDays = planLeave.getPlanDays();// 计划休假天数
			String optUser = planLeave.getOptUser();

			try {
				// 1.根据员工id,假期开始时间，假期类型找到可用的假期天数总数，check预计请假天数是否超出可用假期天数
				Map<String, Object> totalLeave = empLeaveMapper.getMaxAllowDays(planLeave);
				if (null == totalLeave || totalLeave.size() == 0) {
					throw new OaException("您的当前可用调休为空");
				}
				// 计划请假天数必须 <= (允许请假天数 - 总的占用天数)
				Double totalAllowRemainDays = Double.valueOf(totalLeave.get("allowRemainDays") + "");// 总的允许可休假剩余天数
				Double totalBlockedDays = Double.valueOf(totalLeave.get("blockedDays") + "");// 总的占用天数
				if (planDays > totalAllowRemainDays - totalBlockedDays) {
					throw new OaException("您的当前允许可用调休为" + totalAllowRemainDays + "小时,其中审批中的调休小时为" + totalBlockedDays
							+ "小时,计划休假应小于或等于" + (totalAllowRemainDays - totalBlockedDays) + "小时");
				}

				// 2.扣减总的调休
				List<EmpLeave> totalLeaves = empLeaveMapper.getTotalAllowDays(planLeave);
				Double remainPlanDays = planDays;// 剩余计划休假天数
				Double remainBlockedDays = planDays;// 剩余扣减的占用天数
				Double remainYearPlanDays = planDays;
				loop: for (EmpLeave total : totalLeaves) {
					remainBlockedDays = remainBlockedDays > total.getAllowRemainDays() - total.getBlockedDays()
							? (total.getAllowRemainDays() - total.getBlockedDays()) : remainBlockedDays;// 需增加的占用天数

					total.setUpdateUser(optUser);
					total.setUpdateTime(new Date());
					total.setBlockedDays(total.getBlockedDays() + remainBlockedDays);

					empLeaveMapper.updateById(total);
					remainYearPlanDays = remainYearPlanDays - remainBlockedDays;
					remainBlockedDays = planDays - remainBlockedDays > 0 ? planDays - remainBlockedDays : 0;// 余下还需增加的占用天数
					// 3.扣减调休明细
					List<EmpLeave> leaves = empLeaveMapper.getByPid(total.getId());
					for (EmpLeave empLeave : leaves) {
						EmpLeave leave = empLeaveMapper.getById(empLeave.getId());
						leave.setUpdateUser(optUser);
						leave.setUpdateTime(new Date());

						// 计划休假时间小于当前剩余休假天数
						if (remainPlanDays <= leave.getAllowRemainDays()) {// 预计休假天数大于当前剩余休假天数,则直接扣减当前记录，跳出整个循环
							leave.setBlockedDays(leave.getBlockedDays() + remainPlanDays);
							leave.setAllowRemainDays(leave.getAllowRemainDays() - remainPlanDays);
							empLeaveMapper.updateById(leave);
                            if(remainYearPlanDays<=0){
                            	break loop;
                            }
						} else {// 预计休假天数大于当前剩余休假天数,则直接扣减当前记录,并且扣减下条假期
							remainPlanDays = remainPlanDays - leave.getAllowRemainDays();// 余下还需扣减的假期天数
							leave.setBlockedDays(leave.getBlockedDays() + leave.getAllowRemainDays());
							leave.setAllowRemainDays(0.0);
							empLeaveMapper.updateById(leave);
						}
					}
				}

				flag = true;
			} catch (Exception e) {
				logger.error("增加占用调休小时数出错，出错原因=" + e.toString());
				throw e;
			}
		}

		return flag;
	}*/
	
	public boolean updateBolckedRestLeave(List<EmpLeave> planLeaves) throws OaException {
		boolean flag = false;
		if(planLeaves == null || planLeaves.size() == 0){
			logger.error("调休拆分出错！");
			throw new OaException("调休申请时间不能为空！");
		}
		for (EmpLeave planLeave : planLeaves) {// 拆分以后的假期
			Double planDays = planLeave.getPlanDays();// 计划休假天数
			String optUser = planLeave.getOptUser();
			Double remainBlockedDays = planDays;// 剩余需扣减的占用天数
			Double remainPlanDays = planDays; //剩余需扣减的天数
			try {
				//type为13或5的未过期调休
				//排序顺序：①假期类型13 --> 5 
			    		//②有效期结束时间小的 --> 有效期结束时间大的（快过期的）
				List<EmpLeave> restLeaveDays = empLeaveMapper.getUnexpiredRestLeaveDays(planLeave);
				if(restLeaveDays != null && restLeaveDays.size() > 0){
					for (EmpLeave empLeave : restLeaveDays) {
						//如果是类型5则需要用AllowRemainDays - BlockedDays计算可用,因为类型5在锁定时不会修改可用
						if(ConfigConstants.LEAVE_TYPE_5.equals(empLeave.getType())){
							remainBlockedDays = remainBlockedDays > empLeave.getAllowRemainDays() - empLeave.getBlockedDays()
									? (empLeave.getAllowRemainDays() - empLeave.getBlockedDays()) : remainBlockedDays;// 需增加的占用天数
						}else{
							remainBlockedDays = remainBlockedDays > empLeave.getAllowRemainDays() ? empLeave.getAllowRemainDays() : remainBlockedDays;
						}
						empLeave.setUpdateUser(optUser);
						empLeave.setUpdateTime(new Date());
						empLeave.setBlockedDays(empLeave.getBlockedDays() + remainBlockedDays);
						//如果是13则修改可用小时数
						if(ConfigConstants.LEAVE_TYPE_13.equals(empLeave.getType())){
							empLeave.setAllowRemainDays(empLeave.getAllowRemainDays()-remainBlockedDays);
						}
						empLeaveMapper.updateById(empLeave);
						remainPlanDays = remainPlanDays - remainBlockedDays;
						remainBlockedDays = remainPlanDays;// 余下还需增加的占用天数
						if(remainPlanDays <= 0){
							break;
						}
					}
					//循环结束后还未扣减完则抛异常回滚，假期不够
					if(remainPlanDays != 0){
						throw new OaException("您的当前允许可用调休小时数不足！");
					}
				}else{
					throw new OaException("您的当前可用调休为空！");
				}

				flag = true;
			} catch (Exception e) {
				logger.error("增加占用调休小时数出错，出错原因=" + e.toString());
				throw e;
			}
		}

		return flag;
	}
	public void saveRecordDetail(Long recordId, Long baseEmpLeaveId, Double days, String optUser) {
		LeaveRecordDetail recordDetail = new LeaveRecordDetail();
		recordDetail.setLeaveRecordId(recordId);
		recordDetail.setBaseEmpLeaveId(baseEmpLeaveId);
		recordDetail.setDays(days);
		recordDetail.setCreateTime(new Date());
		recordDetail.setCreateUser(optUser);
		recordDetail.setDelFlag(0);
		List<LeaveRecordDetail> isExist = leaveRecordDetailMapper.selectByCondition(recordDetail);
		if (isExist != null && isExist.size() > 0) {
			isExist.get(0).setDays(isExist.get(0).getDays() + days);
			leaveRecordDetailMapper.updateById(isExist.get(0));
		} else {
			leaveRecordDetailMapper.save(recordDetail);
		}
	}

	public boolean updateBolckedYearLeave(List<EmpLeave> planLeaves) throws OaException {
		boolean flag = false;

		for (EmpLeave planLeave : planLeaves) {// 拆分以后的假期，分两段扣减(如果不需要拆分，则只有一段假期)
			Double planDays = planLeave.getPlanDays();// 计划休假天数
			String optUser = planLeave.getOptUser();

			try {
				// 1.根据员工id,假期开始时间，假期类型找到可用的假期天数总数，check预计请假天数是否超出可用假期天数
				Map<String, Object> totalLeave = empLeaveMapper.getMaxAllowDays(planLeave);
				if (null == totalLeave || totalLeave.size() == 0) {
					throw new OaException("您的当前可用年假为空");
				}
				// 计划请假天数必须 <= (允许请假天数 - 总的占用天数)
				Double totalAllowRemainDays = Double.valueOf(totalLeave.get("allowRemainDays") + "");// 总的允许可休假剩余天数
				Double totalBlockedDays = Double.valueOf(totalLeave.get("blockedDays") + "");// 总的占用天数
				if (planDays > totalAllowRemainDays - totalBlockedDays) {
					throw new OaException("您的当前允许可用年假为" + totalAllowRemainDays + "天,其中审批中的假期为" + totalBlockedDays
							+ "天,计划休假应小于或等于" + (totalAllowRemainDays - totalBlockedDays) + "天");
				}

				// 2.步骤1校验通过后，分年扣减假期天数
				List<EmpLeave> leaves = empLeaveMapper.getAllowDays(planLeave);
				Integer year = null;
				Double remainPlanDays = planDays;// 剩余计划休假天数
				Double remainBlockedDays = planDays;// 剩余扣减的占用天数
				loop: for (EmpLeave empLeave : leaves) {
					EmpLeave leave = empLeaveMapper.getById(empLeave.getId());
					leave.setUpdateUser(optUser);
					leave.setUpdateTime(new Date());

					// 总的年假
					EmpLeave totalleave = empLeave;
					totalleave = empLeaveMapper.getById(empLeave.getId());
					totalleave.setUpdateUser(optUser);

					if (null == year || !year.equals(empLeave.getYear())) {
						remainBlockedDays = remainBlockedDays > leave.getAllowRemainDays() - leave.getBlockedDays()
								? (leave.getAllowRemainDays() - leave.getBlockedDays()) : remainBlockedDays;// 需增加的占用天数

						totalleave.setBlockedDays(totalleave.getBlockedDays() + remainBlockedDays);
						empLeaveMapper.updateById(totalleave);
						remainBlockedDays = planDays > remainBlockedDays ? planDays - remainBlockedDays
								: remainBlockedDays;// 余下还需增加的占用天数

						year = empLeave.getYear();
						continue;
					}

					// 计划休假时间小于当前剩余休假天数
					if (remainPlanDays <= leave.getAllowRemainDays()) {// 预计休假天数大于当前剩余休假天数,则直接扣减当前记录，跳出整个循环
						leave.setBlockedDays(leave.getBlockedDays() + remainPlanDays);
						leave.setAllowRemainDays(leave.getAllowRemainDays() - remainPlanDays);
						empLeaveMapper.updateById(leave);

						break loop;
					} else {// 预计休假天数大于当前剩余休假天数,则直接扣减当前记录,并且扣减下条假期
						remainPlanDays = remainPlanDays - leave.getAllowRemainDays();// 余下还需扣减的假期天数
						leave.setBlockedDays(leave.getBlockedDays() + leave.getAllowRemainDays());
						leave.setAllowRemainDays(0.0);
						empLeaveMapper.updateById(leave);

					}
					year = leave.getYear();
				}
				flag = true;
			} catch (Exception e) {
				logger.error("增加占用年假出错，出错原因=" + e.toString());
				throw e;
			}
		}

		return flag;
	}

	/**
	 * updateEmpLeaveAudit(假期审批结束后调的接口) @Title:
	 * updateEmpLeaveAudit @Description: 假期审批结束后调的接口 @param
	 * planLeaves @return @throws Exception 设定文件 boolean 返回类型 @throws
	 */
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
	public boolean updateEmpLeaveAudit(List<Map<String, Object>> planLeaves) throws Exception {
		boolean flag = false;// 返回状态
		EmpLeave planLeave = null;
		for (Map<String, Object> leaveMap : planLeaves) {
			planLeave = JSONUtils.read(JSONUtils.write(leaveMap), EmpLeave.class);
			// 非空校验
			if (null == planLeave) {
				throw new OaException("您当前的请假信息为空");
			} else {
				if (null == planLeave.getEmployeeId()) {
					throw new OaException("您当前的请假的员工id为空");
				}
				if (null == planLeave.getType()) {
					throw new OaException("您当前的请假的假期类型为空");
				}
				if (null == planLeave.getPlanStartTime()) {
					throw new OaException("您当前的请假的开始时间为空");
				}
				if (null == planLeave.getPlanEndTime()) {
					throw new OaException("您当前的请假的结束时间为空");
				}
				if (null == planLeave.getPlanDays()) {
					throw new OaException("您当前的请假的天数为空");
				}
				if (StringUtils.isBlank(planLeave.getOptUser())) {
					throw new OaException("假期申请人不能为空");
				}
			}

			if (planLeave.getType().intValue() == ConfigConstants.LEAVE_TYPE_1.intValue()) {// 年假
				List<EmpLeave> leaves = splitLeave(planLeave);// 拆分假期
				Long recordId = null;
				if (leaves.get(0).getApplyStatus()) {
					recordId = saveLeaveRecord(planLeave, ConfigConstants.LEAVE_KEY);// 新增假期流水
				}
				flag = yearLeaveApply(leaves, recordId);
			} else if (planLeave.getType().intValue() == ConfigConstants.LEAVE_TYPE_2.intValue()) {// 病假				
				List<EmpLeave> leaves = splitLeave(planLeave);// 拆分假期 				
				flag = sickLeaveApply(leaves,planLeave);//同意 新增流水
			} else if (planLeave.getType().intValue() == ConfigConstants.LEAVE_TYPE_5.intValue()) {// 调休
				List<EmpLeave> leaves = splitRestLeave(planLeave);// 拆分假期
				Long recordId = null;
				if (leaves.get(0).getApplyStatus()) {
					recordId = saveLeaveRecord(planLeave, ConfigConstants.LEAVE_KEY);// 新增假期流水
				}
				flag = restLeaveApply(leaves, recordId);
			} else {// 其它假期
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * 
	 * @param EmpapplicationleaveId
	 * @throws OaException
	 *             restLeaveApply(病假审核或拒绝) @Title: restLeaveApply @Description:
	 *             病假审核或拒绝 @param planLeave @return 设定文件 boolean 返回类型 @throws
	 */
	public boolean restLeaveApply(List<EmpLeave> leaves, Long recordId) throws OaException {
		boolean applyStatus = leaves.get(0).getApplyStatus();

		if (applyStatus) {// 审核通过
			return restLeaveApplySuccess(leaves, recordId);
		} else {// 审核拒绝
			return restLeaveApplyFail(leaves);
		}
	}

/*	public boolean restLeaveApplySuccess(List<EmpLeave> planLeaves, Long recordId) throws OaException {
		boolean flag = false;

		for (EmpLeave planLeave : planLeaves) {// 拆分以后的假期，分两段扣减(如果不需要拆分，则只有一段假期)
			Double planDays = planLeave.getPlanDays();// 计划休假天数
			String optUser = planLeave.getOptUser();

			try {
				// 1.根据员工id,假期开始时间，假期类型找到可用的假期天数总数，check预计请假天数是否超出可用假期天数
				Map<String, Object> totalLeave = empLeaveMapper.getMaxAllowDays(planLeave);
				if (null == totalLeave || totalLeave.size() == 0) {
					throw new OaException("您的当前可用调休为空");
				}
				// 计划请假天数必须 <= (允许请假天数 - 总的占用天数)
				Double totalBlockedDays = Double.valueOf(totalLeave.get("blockedDays") + "");// 总的占用天数
				if (planDays > totalBlockedDays) {
					throw new OaException("您当前计划审批的假期为" + planDays + "小时,申请小时数为" + totalBlockedDays + "小时,非法请求");
				}

				// 2.扣减总的调休
				List<EmpLeave> totalLeaves = empLeaveMapper.getTotalBlockDays(planLeave);
				Double remainPlanDays = planDays;// 剩余计划休假天数
				Double remainBlockedDays = planDays;// 剩余扣减的占用天数
				Double remainYearPlanDays = planDays;
				loop: for (EmpLeave total : totalLeaves) {
					remainBlockedDays = remainBlockedDays > total.getBlockedDays() ? total.getBlockedDays()
							: remainBlockedDays;// 需扣减的占用天数

					total.setBlockedDays(total.getBlockedDays() - remainBlockedDays);
					total.setUsedDays(total.getUsedDays() + remainBlockedDays);
					total.setAllowRemainDays(total.getAllowRemainDays() - remainBlockedDays);
					remainYearPlanDays = remainYearPlanDays - remainBlockedDays;
					empLeaveMapper.updateById(total);
					// 流水明细
					saveRecordDetail(recordId, total.getId(), remainBlockedDays, optUser);
					remainBlockedDays = planDays > remainBlockedDays ? planDays - remainBlockedDays : 0.0;// 余下还需增加的占用天数

					// 3.扣减调休明细
					List<EmpLeave> leaves = empLeaveMapper.getByPid(total.getId());
					for (EmpLeave empLeave : leaves) {
						EmpLeave leave = empLeaveMapper.getById(empLeave.getId());
						leave.setUpdateUser(optUser);
						leave.setUpdateTime(new Date());

						// 计划休假时间小于当前剩余休假天数
						if (remainPlanDays <= leave.getBlockedDays()) {// 预计休假天数大于当前剩余休假天数,则直接扣减当前记录，跳出整个循环
							leave.setBlockedDays(leave.getBlockedDays() - remainPlanDays);
							leave.setUsedDays(leave.getUsedDays() + remainPlanDays);
							empLeaveMapper.updateById(leave);
                            if(remainYearPlanDays<=0){
                            	break loop;
                            }
						} else {// 预计休假天数大于当前剩余休假天数,则直接扣减当前记录,并且扣减下条假期
							remainPlanDays = remainPlanDays - leave.getBlockedDays();// 余下还需扣减的假期天数

							leave.setUsedDays(leave.getUsedDays() + leave.getBlockedDays());
							leave.setBlockedDays(0.0);
							empLeaveMapper.updateById(leave);
						}
					}
				}

				flag = true;
			} catch (Exception e) {
				logger.error("增加占用调休小时数出错，出错原因=" + e.toString());
				throw e;
			}
		}

		return flag;
	}*/
	
	public boolean restLeaveApplySuccess(List<EmpLeave> planLeaves, Long recordId) throws OaException {
		boolean flag = false;
		if(planLeaves == null || planLeaves.size() == 0){
			logger.error("调休拆分出错！");
			throw new OaException("调休申请时间不能为空！");
		}
		Double totalPlanDays = 0D;
		Double totalBlackDays = 0D;
		for (EmpLeave planLeave : planLeaves) {// 拆分以后的假期，分两段扣减(如果不需要拆分，则只有一段假期)
			Double planDays = planLeave.getPlanDays();// 计划休假天数
			String optUser = planLeave.getOptUser();
			totalPlanDays += planDays;

			try {
				Double remainBlockedDays = planDays;// 剩余扣减的占用天数
				Double remainBlockedTotalDays = planDays;// 扣减总天数
				//扣减顺序：①假期类型13 --> 5 
					    //②有效期结束时间小的 --> 有效期结束时间大的（快过期的）
				List<EmpLeave> leaveDays = empLeaveMapper.getBlockedRestLeaveDays(planLeave);
				if(leaveDays != null && leaveDays.size() > 0){
					for (EmpLeave empLeave : leaveDays) {
						remainBlockedDays = remainBlockedDays > empLeave.getBlockedDays()
								? empLeave.getBlockedDays() : remainBlockedDays;// 需增加的占用天数
						empLeave.setUpdateUser(optUser);
						empLeave.setUpdateTime(new Date());
						empLeave.setBlockedDays(empLeave.getBlockedDays() - remainBlockedDays);
						empLeave.setUsedDays(empLeave.getUsedDays()+remainBlockedDays);
						//如果是5则减去可用小时数
						if(ConfigConstants.LEAVE_TYPE_5.equals(empLeave.getType())){
							empLeave.setAllowRemainDays(empLeave.getAllowRemainDays()-remainBlockedDays);
						}
						empLeaveMapper.updateById(empLeave);
						remainBlockedTotalDays = remainBlockedTotalDays - remainBlockedDays;
						// 流水明细
						saveRecordDetail(recordId, empLeave.getId(), remainBlockedDays, optUser);
						totalBlackDays += remainBlockedDays;
						remainBlockedDays = remainBlockedTotalDays;
						if(remainBlockedTotalDays <= 0){
							break;
						}
					}
					if(remainBlockedTotalDays > 0){
						throw new OaException("您当前计划审批的假期为" + totalPlanDays + "小时,申请小时数为" + (totalPlanDays-totalBlackDays) + "小时,非法请求");
					}
				}else{
					throw new OaException("您的当前可用调休为空");
				}

				flag = true;
			} catch (Exception e) {
				logger.error("增加占用调休小时数出错，出错原因=" + e.toString());
				throw e;
			}
		}

		return flag;
	}
	/*public boolean restLeaveApplyFail(List<EmpLeave> planLeaves) throws OaException {
		boolean flag = false;

		for (EmpLeave planLeave : planLeaves) {// 拆分以后的假期，分两段扣减(如果不需要拆分，则只有一段假期)
			Double planDays = planLeave.getPlanDays();// 计划休假天数
			String optUser = planLeave.getOptUser();

			try {
				// 1.根据员工id,假期开始时间，假期类型找到可用的假期天数总数，check预计请假天数是否超出可用假期天数
				Map<String, Object> totalLeave = empLeaveMapper.getMaxAllowDays(planLeave);
				if (null == totalLeave || totalLeave.size() == 0) {
					throw new OaException("您的当前可用调休为空");
				}
				// 计划请假天数必须 <= (允许请假天数 - 总的占用天数)
				Double totalBlockedDays = Double.valueOf(totalLeave.get("blockedDays") + "");// 总的占用天数
				if (planDays > totalBlockedDays) {
					throw new OaException("您当前计划审批的假期为" + planDays + "小时,申请小时数为" + totalBlockedDays + "小时,非法请求");
				}

				// 2.扣减总的调休
				List<EmpLeave> totalLeaves = empLeaveMapper.getTotalBlockDays(planLeave);
				Double remainPlanDays = planDays;// 剩余计划休假天数
				Double remainBlockedDays = planDays;// 剩余扣减的占用天数
				Double remainYearPlanDays = planDays;
				loop: for (EmpLeave total : totalLeaves) {
					remainBlockedDays = remainBlockedDays > total.getBlockedDays() ? total.getBlockedDays()
							: remainBlockedDays;// 需扣减的占用天数

					total.setBlockedDays(total.getBlockedDays() - remainBlockedDays);
					remainYearPlanDays = remainYearPlanDays - remainBlockedDays;
					empLeaveMapper.updateById(total);
					remainBlockedDays = planDays > remainBlockedDays ? planDays - remainBlockedDays : 0.0;// 余下还需增加的占用天数

					// 3.扣减调休明细
					List<EmpLeave> leaves = empLeaveMapper.getByPid(total.getId());
					for (EmpLeave empLeave : leaves) {
						EmpLeave leave = empLeaveMapper.getById(empLeave.getId());
						leave.setUpdateUser(optUser);
						leave.setUpdateTime(new Date());

						// 计划休假时间小于当前剩余休假天数
						if (remainPlanDays <= leave.getBlockedDays()) {// 预计休假天数大于当前剩余休假天数,则直接扣减当前记录，跳出整个循环
							leave.setAllowRemainDays(leave.getAllowRemainDays() + remainPlanDays);
							leave.setBlockedDays(leave.getBlockedDays() - remainPlanDays);
							empLeaveMapper.updateById(leave);
                            if(remainYearPlanDays<=0){
                            	break loop;
                            }
						} else {// 预计休假天数大于当前剩余休假天数,则直接扣减当前记录,并且扣减下条假期
							remainPlanDays = remainPlanDays - leave.getBlockedDays();// 余下还需扣减的假期天数

							leave.setAllowRemainDays(leave.getAllowRemainDays() + leave.getBlockedDays());
							leave.setBlockedDays(0.0);
							empLeaveMapper.updateById(leave);
						}
					}
				}
				flag = true;
			} catch (Exception e) {
				logger.error("增加占用调休小时数出错，出错原因=" + e.toString());
				throw e;
			}
		}

		return flag;
	}*/
	
	public boolean restLeaveApplyFail(List<EmpLeave> planLeaves) throws OaException {
		boolean flag = false;
		if(planLeaves == null || planLeaves.size() == 0){
			logger.error("调休拆分出错！");
			throw new OaException("调休申请时间不能为空！");
		}
		Double totalPlanDays = 0D;
		Double totalBlackDays = 0D;
		//将planleaves重新排序，按照调休时间倒着还
		for (int j = 0; j < planLeaves.size()-1; j++) {
			for(int i = 0; i < planLeaves.size()-1-i; i++){
				if(planLeaves.get(i).getPlanStartTime().getTime()<planLeaves.get(i+1).getPlanStartTime().getTime()){
					EmpLeave temp = planLeaves.get(i+1);
					planLeaves.set(i+1, planLeaves.get(i));
					planLeaves.set(i,temp);
				}
			}
		}
		for (EmpLeave planLeave : planLeaves) {// 拆分以后的假期，分两段扣减(如果不需要拆分，则只有一段假期)
			Double planDays = planLeave.getPlanDays();// 计划休假天数
			String optUser = planLeave.getOptUser();
			totalPlanDays += planDays;
			try {
				// 1.根据员工id,假期开始时间，假期类型找到可用的假期天数总数，check预计请假天数是否超出可用假期天数
				//增加占用小时数
				Double remainBlockedDays = planDays;// 剩余扣减的占用天数
				Double remainBlockedTotalDays = planDays;
				//增加调休
				//增加顺序：①假期类型5 --> 13 
			    //②有效期结束时间小的 --> 有效期结束时间小的（过期时间迟的）
				List<EmpLeave> empLeaves = empLeaveMapper.getBlockedRestLeaveDaysDesc(planLeave);
				if(empLeaves != null && empLeaves.size() > 0){
					for (EmpLeave empLeave : empLeaves) {
						remainBlockedDays = remainBlockedDays > empLeave.getBlockedDays() ? empLeave.getBlockedDays()
								: remainBlockedDays;// 需扣减的占用天数
						empLeave.setUpdateUser(optUser);
						empLeave.setUpdateTime(new Date());
						empLeave.setBlockedDays(empLeave.getBlockedDays()- remainBlockedDays);
						//如果是13则增加可用小时数
						if(ConfigConstants.LEAVE_TYPE_13.equals(empLeave.getType())){
							empLeave.setAllowRemainDays(empLeave.getAllowRemainDays()+remainBlockedDays);
						}
						remainBlockedTotalDays = remainBlockedTotalDays - remainBlockedDays;
						totalBlackDays += remainBlockedDays;
						remainBlockedDays = remainBlockedTotalDays;
						empLeaveMapper.updateById(empLeave);
						if(remainBlockedTotalDays <= 0){
							break;
						}
					}
					if(remainBlockedTotalDays > 0){
						throw new OaException("您当前计划审批的假期为" + totalPlanDays + "小时,申请小时数为" + (totalPlanDays-totalBlackDays) + "小时,非法请求");
					}
				}else{
					throw new OaException("您的当前可用调休为空");
				}

				flag = true;
			} catch (Exception e) {
				logger.error("增加占用调休小时数出错，出错原因=" + e.toString());
				throw e;
			}
		}

		return flag;
	}

	/**
	 * @throws OaException
	 *             sickLeaveApply(病假审核或拒绝) @Title: sickLeaveApply @Description:
	 *             病假审核或拒绝 @param leaves planLeave  @return 设定文件 boolean 返回类型 @throws
	 */
	public boolean sickLeaveApply(List<EmpLeave> leaves,EmpLeave planLeave) throws OaException {
		boolean applyStatus = leaves.get(0).getApplyStatus();

		if (applyStatus) {// 审核通过
			Long recordId = null;
			if (leaves.get(0).getApplyStatus()) {
				//这块生产流水
				recordId = saveLeaveRecord(planLeave, ConfigConstants.LEAVE_KEY);// 新增假期流水
			}
			return sickLeaveApplySuccess(leaves,recordId);
		} else {// 审核拒绝
			return true;
		}
	}

	
	
	/**
	 * @throws OaException
	 *             sickLeaveApplySuccess(病假审核通过) @Title:
	 *             sickLeaveApplySuccess @Description: 病假审核通过 @param
	 *             planLeave @return 设定文件 boolean 返回类型 @throws
	 */
	public boolean sickLeaveApplySuccess(List<EmpLeave> planLeaves, Long recordId) throws OaException {
		// 带薪病假规则修改 新添非带薪病假状态
		// 如果请病假超过 actual_days全年实际可用休假天数 超出的范围 添加到 非带薪病假里去
		// 入职未满三个月 请病假都是 请的非带薪病假
		// 因为非带薪病假 新添加 满以上需求 先查看有没有 状态category=2的非带薪病假数据 没有则 添加一条
		boolean flag = false;

		for (EmpLeave planLeave : planLeaves) {// 拆分以后的假期，分两段扣减(如果不需要拆分，则只有一段假期)
			try{
				// 1.查询此员工病假信息
				planLeave.setYear(Integer.parseInt(DateUtils.getYear(planLeave.getPlanStartTime())));
				planLeave.setCategory(0);// 新添查询 带薪病假
				List<EmpLeave> leaves = empLeaveMapper.getListByCondition(planLeave);
				EmpLeave totalLeave = leaves.get(0);
				totalLeave.setUpdateUser(planLeave.getOptUser());
				totalLeave.setUpdateTime(new Date());
	
				Employee emp = employeeMapper.getById(planLeave.getEmployeeId());
	
				Date flagStaus = DateUtils.addMonth(emp.getFirstEntryTime(), 3);
				if (1 == DateUtils.compareDayDate(flagStaus, planLeave.getPlanStartTime())) {// 入职三个月
																								// 请的都是非带薪病假
	
					planLeave.setYear(Integer.parseInt(DateUtils.getYear(planLeave.getPlanStartTime())));
					planLeave.setCategory(2);// 新添查询 非带薪病假
					List<EmpLeave> unSickLeaveList = empLeaveMapper.getListByCondition(planLeave);
					if (unSickLeaveList.size() > 0) {
						EmpLeave unSickLeave = unSickLeaveList.get(0);
						unSickLeave.setUpdateUser(planLeave.getOptUser());
						unSickLeave.setUpdateTime(new Date());
						unSickLeave.setUsedDays(unSickLeave.getUsedDays() + planLeave.getPlanDays());
						empLeaveMapper.updateById(unSickLeave);
						// 流水明细
						saveRecordDetail(recordId, unSickLeaveList.get(0).getId(), planLeave.getPlanDays(),
								planLeave.getOptUser());
					} else {
						EmpLeave newUnSickLeave = new EmpLeave();
						newUnSickLeave.setCompanyId(totalLeave.getCompanyId());
						newUnSickLeave.setEmployeeId(totalLeave.getEmployeeId());
						newUnSickLeave.setYear(totalLeave.getYear());
						newUnSickLeave.setType(totalLeave.getType());
						// 基数、当年可用、允许可用 占用 全年可用 都不用添加 只需添加一个已用
						newUnSickLeave.setUsedDays(planLeave.getPlanDays());
	
						newUnSickLeave.setCategory(2);
						newUnSickLeave.setParendId(0L);// 绑定父病假id
						newUnSickLeave.setStartTime(totalLeave.getStartTime());
						newUnSickLeave.setEndTime(totalLeave.getEndTime());
						newUnSickLeave.setIsActive(totalLeave.getIsActive());
						newUnSickLeave.setCreateTime(new Date());
						newUnSickLeave.setUpdateTime(new Date());
						newUnSickLeave.setCreateUser("API");
						newUnSickLeave.setUpdateUser(planLeave.getOptUser());
						newUnSickLeave.setDelFlag(0);
						newUnSickLeave.setVersion(0L);
						empLeaveMapper.save(newUnSickLeave);
						// 流水明细
						saveRecordDetail(recordId, newUnSickLeave.getId(), planLeave.getPlanDays(),
								planLeave.getOptUser());
					}
				} else {
					// 如果请病假超过 actual_days全年实际可用休假天数 超出的范围 添加到 非带薪病假里去
					if ((planLeave.getPlanDays() + totalLeave.getUsedDays()) > totalLeave.getAllowDays()) {
						// 先把 带薪病假 给弥补满 剩余的在加到非带薪病假中去
						Double flagNum = totalLeave.getAllowDays() - totalLeave.getUsedDays();
						Double flagNumResult = planLeave.getPlanDays() - flagNum;
						// 把 带薪病假 弥补满
						totalLeave.setUsedDays(totalLeave.getUsedDays() + flagNum);
						totalLeave.setAllowRemainDays(0d);
						// 流水明细
						saveRecordDetail(recordId, totalLeave.getId(), flagNum, planLeave.getOptUser());
						planLeave.setYear(Integer.parseInt(DateUtils.getYear(planLeave.getPlanStartTime())));
						planLeave.setCategory(2);// 新添查询 非带薪病假
						List<EmpLeave> unSickLeaveList = empLeaveMapper.getListByCondition(planLeave);
						if (unSickLeaveList.size() > 0) {
							EmpLeave unSickLeave = unSickLeaveList.get(0);
							unSickLeave.setUpdateUser(planLeave.getOptUser());
							unSickLeave.setUpdateTime(new Date());
							unSickLeave.setUsedDays(unSickLeave.getUsedDays() + flagNumResult);
							empLeaveMapper.updateById(unSickLeave);
							// 流水明细
							saveRecordDetail(recordId, unSickLeave.getId(), flagNumResult, planLeave.getOptUser());
						} else {
							EmpLeave newUnSickLeave = new EmpLeave();
							newUnSickLeave.setCompanyId(totalLeave.getCompanyId());
							newUnSickLeave.setEmployeeId(totalLeave.getEmployeeId());
							newUnSickLeave.setYear(totalLeave.getYear());
							newUnSickLeave.setType(totalLeave.getType());
							// 基数、当年可用、允许可用 占用 全年可用 都不用添加 只需添加一个已用
							newUnSickLeave.setUsedDays(flagNumResult);
							newUnSickLeave.setCategory(2);
							newUnSickLeave.setParendId(0L);// 绑定父病假id
							newUnSickLeave.setStartTime(totalLeave.getStartTime());
							newUnSickLeave.setEndTime(totalLeave.getEndTime());
							newUnSickLeave.setIsActive(totalLeave.getIsActive());
							newUnSickLeave.setCreateTime(new Date());
							newUnSickLeave.setUpdateTime(new Date());
							newUnSickLeave.setCreateUser("API");
							newUnSickLeave.setUpdateUser(planLeave.getOptUser());
							newUnSickLeave.setDelFlag(0);
							newUnSickLeave.setVersion(0L);
							empLeaveMapper.save(newUnSickLeave);
							// 流水明细
							saveRecordDetail(recordId, newUnSickLeave.getId(), flagNumResult, planLeave.getOptUser());
						}
					} else {
						totalLeave.setUsedDays(totalLeave.getUsedDays() + planLeave.getPlanDays());
						totalLeave.setAllowRemainDays((totalLeave.getAllowDays() - totalLeave.getUsedDays()) > 0
								? totalLeave.getAllowDays() - totalLeave.getUsedDays() : 0);
						// 流水明细
						saveRecordDetail(recordId, totalLeave.getId(), planLeave.getPlanDays(),
								planLeave.getOptUser());
					}
				}
				empLeaveMapper.updateById(totalLeave);
			}catch (Exception e) {
				throw new OaException(e);
			}
		} 
		flag = true;
		return flag;
	}
	
	
	// 旧方法
	/**
	 * @throws OaException
	 *             sickLeaveApplySuccess(病假审核通过) @Title:
	 *             sickLeaveApplySuccess @Description: 病假审核通过 @param
	 *             planLeave @return 设定文件 boolean 返回类型 @throws
	 */
	public boolean sickLeaveApplySuccess(EmpLeave planLeave) throws OaException {
		boolean flag = false;
		// 带薪病假规则修改 新添非带薪病假状态
		// 如果请病假超过 actual_days全年实际可用休假天数 超出的范围 添加到 非带薪病假里去
		// 入职未满三个月 请病假都是 请的非带薪病假
		// 因为非带薪病假 新添加 满以上需求 先查看有没有 状态category=2的非带薪病假数据 没有则 添加一条
		try {
			LeaveRecord record = new LeaveRecord();
			record.setEmployeeId(planLeave.getEmployeeId());
			record.setType(planLeave.getType());
			record.setDays(planLeave.getPlanDays());
			record.setBillId(planLeave.getEmpapplicationleaveId());
			record.setBillType(ConfigConstants.LEAVE_KEY);
			record.setDaysUnit(0);// 单位
			record.setCreateTime(new Date());
			record.setCreateUser(planLeave.getOptUser());
			record.setDelFlag(0);
			record.setSource(0);
			record.setRemark("请假申请");
			leaveRecordMapper.save(record);

			// 1.查询此员工病假信息
			planLeave.setYear(Integer.parseInt(DateUtils.getYear(new Date())));
			planLeave.setCategory(0);// 新添查询 带薪病假
			List<EmpLeave> leaves = empLeaveMapper.getListByCondition(planLeave);
			EmpLeave totalLeave = leaves.get(0);
			totalLeave.setUpdateUser(planLeave.getOptUser());
			totalLeave.setUpdateTime(new Date());

			Employee emp = employeeMapper.getById(planLeave.getEmployeeId());

			Date flagStaus = DateUtils.addMonth(emp.getFirstEntryTime(), 3);
			if (1 == DateUtils.compareDayDate(flagStaus, planLeave.getPlanStartTime())) {// 入职三个月
																							// 请的都是非带薪病假

				planLeave.setYear(Integer.parseInt(DateUtils.getYear(new Date())));
				planLeave.setCategory(2);// 新添查询 非带薪病假
				List<EmpLeave> unSickLeaveList = empLeaveMapper.getListByCondition(planLeave);
				if (unSickLeaveList.size() > 0) {
					EmpLeave unSickLeave = unSickLeaveList.get(0);
					unSickLeave.setUpdateUser(planLeave.getOptUser());
					unSickLeave.setUpdateTime(new Date());
					unSickLeave.setUsedDays(unSickLeave.getUsedDays() + planLeave.getPlanDays());
					empLeaveMapper.updateById(unSickLeave);
					// 流水明细
					saveRecordDetail(record.getId(), unSickLeaveList.get(0).getId(), planLeave.getPlanDays(),
							planLeave.getOptUser());
				} else {
					EmpLeave newUnSickLeave = new EmpLeave();
					newUnSickLeave.setCompanyId(totalLeave.getCompanyId());
					newUnSickLeave.setEmployeeId(totalLeave.getEmployeeId());
					newUnSickLeave.setYear(totalLeave.getYear());
					newUnSickLeave.setType(totalLeave.getType());
					// 基数、当年可用、允许可用 占用 全年可用 都不用添加 只需添加一个已用
					newUnSickLeave.setUsedDays(planLeave.getPlanDays());

					newUnSickLeave.setCategory(2);
					newUnSickLeave.setParendId(0L);// 绑定父病假id
					newUnSickLeave.setStartTime(totalLeave.getStartTime());
					newUnSickLeave.setEndTime(totalLeave.getEndTime());
					newUnSickLeave.setIsActive(totalLeave.getIsActive());
					newUnSickLeave.setCreateTime(new Date());
					newUnSickLeave.setUpdateTime(new Date());
					newUnSickLeave.setCreateUser("API");
					newUnSickLeave.setUpdateUser(planLeave.getOptUser());
					newUnSickLeave.setDelFlag(0);
					newUnSickLeave.setVersion(0L);
					empLeaveMapper.save(newUnSickLeave);
					// 流水明细
					saveRecordDetail(record.getId(), newUnSickLeave.getId(), planLeave.getPlanDays(),
							planLeave.getOptUser());
				}
			} else {
				// 如果请病假超过 actual_days全年实际可用休假天数 超出的范围 添加到 非带薪病假里去
				if ((planLeave.getPlanDays() + totalLeave.getUsedDays()) > totalLeave.getAllowDays()) {
					// 先把 带薪病假 给弥补满 剩余的在加到非带薪病假中去
					Double flagNum = totalLeave.getAllowDays() - totalLeave.getUsedDays();
					Double flagNumResult = planLeave.getPlanDays() - flagNum;
					// 把 带薪病假 弥补满
					totalLeave.setUsedDays(totalLeave.getUsedDays() + flagNum);
					totalLeave.setAllowRemainDays(0d);
					// 流水明细
					saveRecordDetail(record.getId(), totalLeave.getId(), flagNum, planLeave.getOptUser());
					planLeave.setYear(Integer.parseInt(DateUtils.getYear(new Date())));
					planLeave.setCategory(2);// 新添查询 非带薪病假
					List<EmpLeave> unSickLeaveList = empLeaveMapper.getListByCondition(planLeave);
					if (unSickLeaveList.size() > 0) {
						EmpLeave unSickLeave = unSickLeaveList.get(0);
						unSickLeave.setUpdateUser(planLeave.getOptUser());
						unSickLeave.setUpdateTime(new Date());
						unSickLeave.setUsedDays(unSickLeave.getUsedDays() + flagNumResult);
						empLeaveMapper.updateById(unSickLeave);
						// 流水明细
						saveRecordDetail(record.getId(), unSickLeave.getId(), flagNumResult, planLeave.getOptUser());
					} else {
						EmpLeave newUnSickLeave = new EmpLeave();
						newUnSickLeave.setCompanyId(totalLeave.getCompanyId());
						newUnSickLeave.setEmployeeId(totalLeave.getEmployeeId());
						newUnSickLeave.setYear(totalLeave.getYear());
						newUnSickLeave.setType(totalLeave.getType());
						// 基数、当年可用、允许可用 占用 全年可用 都不用添加 只需添加一个已用
						newUnSickLeave.setUsedDays(flagNumResult);
						newUnSickLeave.setCategory(2);
						newUnSickLeave.setParendId(0L);// 绑定父病假id
						newUnSickLeave.setStartTime(totalLeave.getStartTime());
						newUnSickLeave.setEndTime(totalLeave.getEndTime());
						newUnSickLeave.setIsActive(totalLeave.getIsActive());
						newUnSickLeave.setCreateTime(new Date());
						newUnSickLeave.setUpdateTime(new Date());
						newUnSickLeave.setCreateUser("API");
						newUnSickLeave.setUpdateUser(planLeave.getOptUser());
						newUnSickLeave.setDelFlag(0);
						newUnSickLeave.setVersion(0L);
						empLeaveMapper.save(newUnSickLeave);
						// 流水明细
						saveRecordDetail(record.getId(), newUnSickLeave.getId(), flagNumResult, planLeave.getOptUser());
					}
				} else {
					totalLeave.setUsedDays(totalLeave.getUsedDays() + planLeave.getPlanDays());
					totalLeave.setAllowRemainDays((totalLeave.getAllowDays() - totalLeave.getUsedDays()) > 0
							? totalLeave.getAllowDays() - totalLeave.getUsedDays() : 0);
					// 流水明细
					saveRecordDetail(record.getId(), totalLeave.getId(), planLeave.getPlanDays(),
							planLeave.getOptUser());
				}
			}
			empLeaveMapper.updateById(totalLeave);
			flag = true;
		} catch (Exception e) {
			throw new OaException(e);
		}

		return flag;
	}

	@Override
	public void SickLeaveSplit() {
		// 所有在职的员工
		EmpLeave empSinckList = new EmpLeave();
		empSinckList.setYear(Integer.parseInt(DateUtils.getYear(new Date())));
		empSinckList.setCategory(0);// 新添查询 带薪病假
		empSinckList.setType(2);
		List<EmpLeave> listByCondition = empLeaveMapper.getListByCondition(empSinckList);
		for (EmpLeave employee : listByCondition) {
			try {
				EmpLeave planLeave = new EmpLeave();
				EmpLeave totalLeave = new EmpLeave();
				// 1.查询此员工病假信息
				planLeave.setYear(Integer.parseInt(DateUtils.getYear(new Date())));
				planLeave.setCategory(0);// 新添查询 带薪病假
				planLeave.setType(2);
				planLeave.setEmployeeId(employee.getEmployeeId());
				List<EmpLeave> unSickLeaveList = empLeaveMapper.getListByCondition(planLeave);
				if (unSickLeaveList.size() > 0) {
					totalLeave = unSickLeaveList.get(0);
				} else {
					return;
				}
				// 查找 有没有 非带薪病假 数据
				EmpLeave sinckLeaveQuery = new EmpLeave();
				sinckLeaveQuery.setEmployeeId(totalLeave.getEmployeeId());
				sinckLeaveQuery.setYear(Integer.parseInt(DateUtils.getYear(new Date())));
				sinckLeaveQuery.setCategory(2);// 新添查询 非带薪病假
				sinckLeaveQuery.setType(2);
				List<EmpLeave> unFeiSickLeaveList = empLeaveMapper.getListByCondition(sinckLeaveQuery);

				// 判断 病假期 如果 目前可用 allow_ramian_days 小于 0 则此员工为离职状态 判断他
				// 入职日期是否为2018年前 病假基数就是5
				if (totalLeave.getAllowRemainDays() < 0) {
					Employee empSinckLeave = employeeMapper.getById(totalLeave.getEmployeeId());
					int compareDayDate = DateUtils.compareDayDate(empSinckLeave.getFirstEntryTime(),
							DateUtils.getYearBegin());
					// 入职日期是否为2018年前 病假基数就是5
					if (compareDayDate != 1) {
						if (totalLeave.getUsedDays() > 5) {
							logger.info("修改员工病假数据 begin,员工id" + totalLeave.getEmployeeId() + "	已使用病假		"
									+ totalLeave.getUsedDays() + "	当年总病假		" + totalLeave.getAllowDays()
									+ "	剩余病假		" + totalLeave.getAllowRemainDays());

							EmpLeave newUnSickLeave = new EmpLeave();
							newUnSickLeave.setCompanyId(totalLeave.getCompanyId());
							newUnSickLeave.setEmployeeId(totalLeave.getEmployeeId());
							newUnSickLeave.setYear(totalLeave.getYear());
							newUnSickLeave.setType(totalLeave.getType());
							// 基数、当年可用、允许可用 占用 全年可用 都不用添加 只需添加一个已用
							newUnSickLeave.setUsedDays(totalLeave.getUsedDays() - 5);// 上面计算出的
																						// 绝对值
							newUnSickLeave.setCategory(2);
							newUnSickLeave.setParendId(0L);// 绑定父病假id
							newUnSickLeave.setStartTime(totalLeave.getStartTime());
							newUnSickLeave.setEndTime(totalLeave.getEndTime());
							newUnSickLeave.setIsActive(totalLeave.getIsActive());
							newUnSickLeave.setCreateTime(new Date());
							newUnSickLeave.setUpdateTime(new Date());
							newUnSickLeave.setCreateUser("API");
							newUnSickLeave.setUpdateUser("API");
							newUnSickLeave.setDelFlag(0);
							newUnSickLeave.setVersion(0L);
							empLeaveMapper.save(newUnSickLeave);
							logger.info("新增一条非带薪病假数据（今年之前入职   离职的员工，使用带薪天数超过5天）,员工id" + newUnSickLeave.getEmployeeId()
									+ "	已使用非带薪病假		" + newUnSickLeave.getUsedDays());

							totalLeave.setUpdateUser("API");
							totalLeave.setUpdateTime(new Date());
							totalLeave.setAllowRemainDays(totalLeave.getAllowDays() - 5);
							totalLeave.setUsedDays(5d); // 此员工为离职状态
														// 又是2018前入职的员工、已用为负数
														// 这里变成5

							empLeaveMapper.updateById(totalLeave);
							logger.info("修改员工病假数据 after,员工id" + totalLeave.getEmployeeId() + "	已使用病假		"
									+ totalLeave.getUsedDays() + "	当年总病假		" + totalLeave.getAllowDays()
									+ "	剩余病假		" + totalLeave.getAllowRemainDays());

						} else {
							EmpLeave newUnSickLeave = new EmpLeave();
							newUnSickLeave.setCompanyId(totalLeave.getCompanyId());
							newUnSickLeave.setEmployeeId(totalLeave.getEmployeeId());
							newUnSickLeave.setYear(totalLeave.getYear());
							newUnSickLeave.setType(totalLeave.getType());
							// 基数、当年可用、允许可用 占用 全年可用 都不用添加 只需添加一个已用
							newUnSickLeave.setUsedDays(0d);// 离职人员 为超过五天病假 不算
															// 非带薪病假
							newUnSickLeave.setCategory(2);
							newUnSickLeave.setParendId(0L);// 绑定父病假id
							newUnSickLeave.setStartTime(totalLeave.getStartTime());
							newUnSickLeave.setEndTime(totalLeave.getEndTime());
							newUnSickLeave.setIsActive(totalLeave.getIsActive());
							newUnSickLeave.setCreateTime(new Date());
							newUnSickLeave.setUpdateTime(new Date());
							newUnSickLeave.setCreateUser("API");
							newUnSickLeave.setUpdateUser("API");
							newUnSickLeave.setDelFlag(0);
							newUnSickLeave.setVersion(0L);
							empLeaveMapper.save(newUnSickLeave);
							logger.info("新增一条非带薪病假数据（今年之前入职   离职的员工，使用带薪天数未超过5天）,员工id" + newUnSickLeave.getEmployeeId()
									+ "	已使用非带薪病假		" + newUnSickLeave.getUsedDays());

						}
					} else {
						EmpLeave newUnSickLeave = new EmpLeave();
						newUnSickLeave.setCompanyId(totalLeave.getCompanyId());
						newUnSickLeave.setEmployeeId(totalLeave.getEmployeeId());
						newUnSickLeave.setYear(totalLeave.getYear());
						newUnSickLeave.setType(totalLeave.getType());
						// 基数、当年可用、允许可用 占用 全年可用 都不用添加 只需添加一个已用
						newUnSickLeave.setUsedDays(0d);// 离职人员 为超过五天病假 不算 非带薪病假
						newUnSickLeave.setCategory(2);
						newUnSickLeave.setParendId(0L);// 绑定父病假id
						newUnSickLeave.setStartTime(totalLeave.getStartTime());
						newUnSickLeave.setEndTime(totalLeave.getEndTime());
						newUnSickLeave.setIsActive(totalLeave.getIsActive());
						newUnSickLeave.setCreateTime(new Date());
						newUnSickLeave.setUpdateTime(new Date());
						newUnSickLeave.setCreateUser("API");
						newUnSickLeave.setUpdateUser("API");
						newUnSickLeave.setDelFlag(0);
						newUnSickLeave.setVersion(0L);
						empLeaveMapper.save(newUnSickLeave);
						logger.info("新增一条非带薪病假数据（今年入职   离职的员工）,员工id" + newUnSickLeave.getEmployeeId()
								+ "	已使用非带薪病假		" + newUnSickLeave.getUsedDays());

					}
				} else {
					// 如果请病假超 过 getAllowRemainDays 全年可用休假天数 超出的范围 添加到 非带薪病假里去
					if (totalLeave.getUsedDays() > totalLeave.getAllowDays()) {
						logger.info("修改员工病假数据,员工id begin" + totalLeave.getEmployeeId() + "	已使用病假		"
								+ totalLeave.getUsedDays() + "	当年总病假		" + totalLeave.getAllowDays()
								+ "	剩余病假		" + totalLeave.getAllowRemainDays());

						// 先把 带薪病假 给弥补满 剩余的在加到非带薪病假中去
						Double flagNum = totalLeave.getAllowDays() - totalLeave.getUsedDays(); // 看是否超出
						// 把 带薪病假 弥补满
						totalLeave.setUsedDays(totalLeave.getUsedDays() + flagNum);
						totalLeave.setUpdateUser("API");
						totalLeave.setUpdateTime(new Date());
						totalLeave.setAllowRemainDays(totalLeave.getAllowDays() - totalLeave.getUsedDays());
						empLeaveMapper.updateById(totalLeave); // 修改病假
						logger.info("修改员工病假数据,员工id after" + totalLeave.getEmployeeId() + "	已使用病假		"
								+ totalLeave.getUsedDays() + "	当年总病假		" + totalLeave.getAllowDays()
								+ "	剩余病假		" + totalLeave.getAllowRemainDays());

						if (unFeiSickLeaveList.size() > 0) {
							EmpLeave unSickLeave = unFeiSickLeaveList.get(0);
							unSickLeave.setUpdateUser("API");
							unSickLeave.setUpdateTime(new Date());
							unSickLeave.setUsedDays(unSickLeave.getUsedDays() + Math.abs(flagNum));// 如果有
																									// 病假数据就
																									// 已用
																									// +
																									// 上面计算出的
																									// 绝对值
							empLeaveMapper.updateById(unSickLeave);
							logger.info("修改员工 非带薪病假数据,员工id" + totalLeave.getEmployeeId() + "	已使用病假		"
									+ totalLeave.getUsedDays() + "	当年总病假		" + totalLeave.getAllowDays()
									+ "	剩余病假		" + totalLeave.getAllowRemainDays());

						} else {
							EmpLeave newUnSickLeave = new EmpLeave();
							newUnSickLeave.setCompanyId(totalLeave.getCompanyId());
							newUnSickLeave.setEmployeeId(totalLeave.getEmployeeId());
							newUnSickLeave.setYear(totalLeave.getYear());
							newUnSickLeave.setType(totalLeave.getType());
							// 基数、当年可用、允许可用 占用 全年可用 都不用添加 只需添加一个已用
							newUnSickLeave.setUsedDays(Math.abs(flagNum));// 上面计算出的
																			// 绝对值
							newUnSickLeave.setCategory(2);
							newUnSickLeave.setParendId(0L);// 绑定父病假id
							newUnSickLeave.setStartTime(totalLeave.getStartTime());
							newUnSickLeave.setEndTime(totalLeave.getEndTime());
							newUnSickLeave.setIsActive(totalLeave.getIsActive());
							newUnSickLeave.setCreateTime(new Date());
							newUnSickLeave.setUpdateTime(new Date());
							newUnSickLeave.setCreateUser("API");
							newUnSickLeave.setUpdateUser("API");
							newUnSickLeave.setDelFlag(0);
							newUnSickLeave.setVersion(0L);
							empLeaveMapper.save(newUnSickLeave);
							logger.info("新增一条非带薪病假数据（已用 大于 可用）,员工id" + newUnSickLeave.getEmployeeId()
									+ "	已使用非带薪病假		" + newUnSickLeave.getUsedDays());
						}
					} else {
						if (unFeiSickLeaveList.size() == 0) {
							EmpLeave newUnSickLeave = new EmpLeave();
							newUnSickLeave.setCompanyId(totalLeave.getCompanyId());
							newUnSickLeave.setEmployeeId(totalLeave.getEmployeeId());
							newUnSickLeave.setYear(totalLeave.getYear());
							newUnSickLeave.setType(totalLeave.getType());
							// 基数、当年可用、允许可用 占用 全年可用 都不用添加 只需添加一个已用
							newUnSickLeave.setUsedDays(0d);// 上面计算出的 绝对值
							newUnSickLeave.setCategory(2);
							newUnSickLeave.setParendId(0L);
							newUnSickLeave.setStartTime(totalLeave.getStartTime());
							newUnSickLeave.setEndTime(totalLeave.getEndTime());
							newUnSickLeave.setIsActive(totalLeave.getIsActive());
							newUnSickLeave.setCreateTime(new Date());
							newUnSickLeave.setUpdateTime(new Date());
							newUnSickLeave.setCreateUser("API");
							newUnSickLeave.setUpdateUser("API");
							newUnSickLeave.setDelFlag(0);
							newUnSickLeave.setVersion(0L);
							empLeaveMapper.save(newUnSickLeave);
							logger.info("新增一条非带薪病假数据（已用 小于 可用 不进行任何数据操作）,员工id" + newUnSickLeave.getEmployeeId()
									+ "	已使用非带薪病假		" + newUnSickLeave.getUsedDays());
						}
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				logger.info("修改员工病假数据,员工id   数据修改报错 " + employee.getEmployeeId() + "	已使用病假		"
						+ employee.getUsedDays() + "	当年总病假		" + employee.getAllowDays() + "	剩余病假		"
						+ employee.getAllowRemainDays());

			}
		}

	}

	/**
	 * @throws OaException
	 *             yearLeaveApply(年假和事假审核通过或拒绝) @Title:
	 *             yearLeaveApply @Description: 年假和事假审核通过或拒绝 @param
	 *             planLeave @return 设定文件 boolean 返回类型 @throws @param
	 *             empapplicationleaveId @throws OaException
	 *             yearLeaveApply(年假和事假审核通过或拒绝) @Title:
	 *             yearLeaveApply @Description: 年假和事假审核通过或拒绝 @param
	 *             planLeave @return 设定文件 boolean 返回类型 @throws
	 */
	public boolean yearLeaveApply(List<EmpLeave> leaves, Long recordId) throws OaException {
		boolean applyStatus = leaves.get(0).getApplyStatus();

		if (applyStatus) {// 审核通过
			return yearLeaveApplySuccess(leaves, recordId);
		} else {// 审核拒绝
			return yearLeaveApplyFail(leaves);
		}
	}

	// XXX:待优化
	public boolean yearLeaveApplySuccess(List<EmpLeave> planLeaves, Long recordId) throws OaException {
		boolean flag = false;

		Map<String, Object> cutLeaveDetail = Maps.newHashMap();
		// List<Map<Integer,Object>> list = null;

		for (EmpLeave planLeave : planLeaves) {// 拆分以后的假期，分两段扣减(如果不需要拆分，则只有一段假期)
			Double planDays = planLeave.getPlanDays();// 计划休假天数
			String optUser = planLeave.getOptUser();

			try {
				// 1.根据员工id,假期开始时间，假期类型找到可用的假期天数总数，check预计请假天数是否超出可用假期天数
				Map<String, Object> totalLeave = empLeaveMapper.getMaxAllowDays(planLeave);
				if (null == totalLeave || totalLeave.size() == 0) {
					throw new OaException("您的当前可用年假为空");
				}
				// 计划请假天数必须 <= (允许请假天数 - 总的占用天数)
				Double totalBlockedDays = Double.valueOf(totalLeave.get("blockedDays") + "");// 总的占用天数
				if (planDays > totalBlockedDays) {
					throw new OaException("您当前审批的假期天数为" + planDays + "天,占用天数为" + totalBlockedDays + "天,属于非法请求");
				}

				// 2.步骤1校验通过后，分年扣减假期天数
				List<EmpLeave> leaves = empLeaveMapper.getAllowBlockDays(planLeave);
				Integer year = null;
				Double remainPlanDays = planDays;// 剩余计划休假天数
				Double remainBlockedDays = planDays;// 剩余扣减的占用天数
				loop: for (EmpLeave empLeave : leaves) {
					EmpLeave leave = empLeaveMapper.getById(empLeave.getId());
					leave.setUpdateUser(optUser);
					leave.setUpdateTime(new Date());

					// 总的年假
					EmpLeave totalleave = empLeave;
					totalleave = empLeaveMapper.getById(empLeave.getId());
					totalleave.setUpdateUser(optUser);

					if (null == year || !year.equals(empLeave.getYear())) {
						remainBlockedDays = remainBlockedDays > leave.getBlockedDays() ? leave.getBlockedDays()
								: remainBlockedDays;// 需扣减的占用天数

						totalleave.setBlockedDays(totalleave.getBlockedDays() - remainBlockedDays);
						totalleave.setUsedDays(totalleave.getUsedDays() + remainBlockedDays);
						totalleave.setAllowRemainDays(totalleave.getAllowRemainDays() - remainBlockedDays);
						empLeaveMapper.updateById(totalleave);
						year = empLeave.getYear();
						// 保存对应年份扣减的数据
						cutLeaveDetail.put(String.valueOf(year), remainBlockedDays);
						/*
						 * list = Lists.newArrayList();
						 * cutLeaveDetail.put(String.valueOf(year), list);
						 */

						remainBlockedDays = planDays > remainBlockedDays ? planDays - remainBlockedDays : 0.0;// 余下还需增加的占用天数

						continue;
					}

					// 计划休假时间小于当前剩余休假天数
					if (remainPlanDays <= leave.getBlockedDays()) {// 预计休假天数大于占用天数,则直接扣减当前记录，跳出整个循环

						/*
						 * Map<Integer,Object> detail = Maps.newHashMap();
						 * detail.put(leave.getCategory(), remainPlanDays);
						 * list.add(detail);
						 */
						// 流水明细
						saveRecordDetail(recordId, leave.getId(), remainPlanDays, optUser);
						leave.setBlockedDays(leave.getBlockedDays() - remainPlanDays);
						leave.setUsedDays(leave.getUsedDays() + remainPlanDays);
						empLeaveMapper.updateById(leave);

						break loop;
					} else {// 预计休假天数大于当前剩余休假天数,则直接扣减当前记录,并且扣减下条假期

						/*
						 * Map<Integer,Object> detail = Maps.newHashMap();
						 * detail.put(leave.getCategory(),
						 * leave.getBlockedDays()); list.add(detail);
						 */

						remainPlanDays = remainPlanDays - leave.getBlockedDays();// 余下还需扣减的假期天数
						// 流水明细
						if (leave.getBlockedDays() > 0) {
							saveRecordDetail(recordId, leave.getId(), leave.getBlockedDays(), optUser);
						}
						leave.setUsedDays(leave.getUsedDays() + leave.getBlockedDays());
						leave.setBlockedDays(0.0);
						empLeaveMapper.updateById(leave);

					}
					year = leave.getYear();
				}
				flag = true;
			} catch (Exception e) {
				logger.error("增加占用年假出错，出错原因=" + e.toString());
				throw e;
			}
		}
		// 记录假期扣减详情
		EmpApplicationLeaveDetail leaveDetail = new EmpApplicationLeaveDetail();
		leaveDetail.setId(planLeaves.get(0).getEmpapplicationleaveId());
		leaveDetail.setCutLeaveDetail(JSONUtils.write(cutLeaveDetail));
		empApplicationLeaveDetailMapper.updateById(leaveDetail);

		return flag;
	}

	//还假
	public boolean yearLeaveApplyFail(List<EmpLeave> planLeaves) throws OaException {
		boolean flag = false;
		
		//如果planLeaves，多条，按开始时间倒叙，先还今年再换去年
		planLeaves = planLeaves.stream().sorted((u1, u2) -> u2.getPlanStartTime().compareTo(u1.getPlanStartTime())).collect(Collectors.toList());

		for (EmpLeave planLeave : planLeaves) {// 拆分以后的假期，分两段扣减(如果不需要拆分，则只有一段假期)
			Double planDays = planLeave.getPlanDays();// 计划休假天数
			String optUser = planLeave.getOptUser();

			try {
				// 1.根据员工id,假期开始时间，假期类型找到可用的假期天数总数，check预计请假天数是否超出可用假期天数
				Map<String, Object> totalLeave = empLeaveMapper.getMaxAllowDays(planLeave);
				if (null == totalLeave || totalLeave.size() == 0) {
					throw new OaException("您的当前可用年假为空");
				}
				// 计划请假天数必须 <= (允许请假天数 - 总的占用天数)
				Double totalBlockedDays = Double.valueOf(totalLeave.get("blockedDays") + "");// 总的占用天数
				if (planDays > totalBlockedDays) {
					throw new OaException("您当前审批的假期天数为" + planDays + ",占用天数为" + totalBlockedDays + "天,属于非法请求");
				}

				// 2.步骤1校验通过后，分年扣减假期天数
				List<EmpLeave> leaves = empLeaveMapper.getAllowBlockDaysDesc(planLeave);
				Double remainPlanDays = planDays;// 剩余计划休假天数
				Double remainBlockedDays = planDays;// 剩余扣减的占用天数
				loop: for (EmpLeave empLeave : leaves) {
					EmpLeave leave = empLeaveMapper.getById(empLeave.getId());
					leave.setUpdateUser(optUser);
					leave.setUpdateTime(new Date());

					// 总的年假
					EmpLeave totalleave = empLeave;
					totalleave = empLeaveMapper.getById(empLeave.getId());
					totalleave.setUpdateUser(optUser);

					if (empLeave.getCategory().intValue() == 0) {
						remainBlockedDays = remainBlockedDays > leave.getBlockedDays() ? leave.getBlockedDays()
								: remainBlockedDays;// 需扣减的占用天数
                        if(remainBlockedDays<=0){
                        	continue;
                        }
						totalleave.setBlockedDays(totalleave.getBlockedDays() - remainBlockedDays);
						empLeaveMapper.updateById(totalleave);
						remainBlockedDays = planDays > remainBlockedDays ? planDays - remainBlockedDays : 0.0;// 余下还需增加的占用天数
						continue;
					}

					// 计划休假时间小于当前剩余休假天数
					if (remainPlanDays <= leave.getBlockedDays()) {// 预计休假天数大于占用天数,则直接扣减当前记录，跳出整个循环
						if (remainPlanDays <= 0) {
							
						}else{
							leave.setBlockedDays(leave.getBlockedDays() - remainPlanDays);
							leave.setAllowRemainDays(leave.getAllowRemainDays() + remainPlanDays);
							empLeaveMapper.updateById(leave);
							remainPlanDays = 0.0;
						}
					} else {// 预计休假天数大于当前剩余休假天数,则直接扣减当前记录,并且扣减下条假期
						remainPlanDays = remainPlanDays - leave.getBlockedDays();// 余下还需扣减的假期天数

						leave.setAllowRemainDays(leave.getAllowRemainDays() + leave.getBlockedDays());
						leave.setBlockedDays(0.0);
						empLeaveMapper.updateById(leave);
					}
				}
				flag = true;
			} catch (Exception e) {
				logger.error("增加占用年假出错，出错原因=" + e.toString());
				throw e;
			}
		}

		return flag;
	}

	@Override
	public Map<String, Object> getYearLeaveView(Long employeeId, int year) throws OaException {

		Date today = DateUtils.getToday();

		List<Integer> typeList = new ArrayList<Integer>();
		typeList.add(ConfigConstants.LEAVE_TYPE_1);// 只查询年假

		List<Date> dateList = new ArrayList<Date>();
		dateList.add(today);// 今年
		dateList.add(DateUtils.addMonth(today, -12));// 去年

		// 查询去年和今年的年假list
		List<EmpLeave> list = empLeaveMapper.getMyViewLeave(employeeId, dateList, typeList);

		Map<String, Object> map = new HashMap<String, Object>();

		// 转换成map
		Integer leaveYear = null;
		if (null != list) {
			for (EmpLeave empLeave : list) {

				leaveYear = empLeave.getYear();

				if (leaveYear == year) {// 今年
					map.put("thisYearStartTime", empLeave.getStartTime());
					map.put("thisYearEndTime", empLeave.getEndTime());
					map.put("thisYearAllowRemain", getGreaterThan0(empLeave.getAllowDays()));// 可用年假总天数
					map.put("thisYearUsed", getGreaterThan0(empLeave.getUsedDays()));// 已使用年假总天数
					map.put("thisYearLawDays", getGreaterThan0(empLeave.getLawDays()));// 法定年假总天数
					map.put("thisYearWelfareDays", getGreaterThan0(empLeave.getAllowDays() - empLeave.getLawDays()));// 福利年假总天数
					map.put("thisYearRemain", getGreaterThan0(empLeave.getAllowRemainDays()));// 剩余年假

					Double overUsed = empLeave.getUsedDays() - empLeave.getActualDays() <= 0 ? 0
							: empLeave.getUsedDays() - empLeave.getActualDays();// 透支年假天数
					Double thisReductionDays = empLeave.getActualDays() - empLeave.getUsedDays();

					map.put("thisYearActual", getGreaterThan0(empLeave.getActualDays()));// 折算年假天数
					map.put("thisReductionDays", getGreaterThan0(thisReductionDays));// 折算的剩余年假总天数

					map.put("thisYearOverUsed", getGreaterThan0(overUsed));
					if (map.containsKey("lastYearRemain")) {

					} else {
						map.put("lastYearRemain", 0);// 剩余年假总天数
					}
				} else if (leaveYear == year - 1) {// 去年

					map.put("lastYearStartTime", empLeave.getStartTime());
					map.put("lastYearEndTime", empLeave.getEndTime());
					map.put("lastYearAllowRemain", getGreaterThan0(empLeave.getAllowDays()));
					map.put("lastYearUsed", getGreaterThan0(empLeave.getUsedDays()));
					map.put("lastYearLawDays", getGreaterThan0(empLeave.getLawDays()));// 法定年假总天数
					map.put("lastYearWelfareDays", getGreaterThan0(empLeave.getAllowDays() - empLeave.getLawDays()));// 福利年假总天数

					/** 判断是否为4月第6个工作日 **/
					Calendar cal = Calendar.getInstance();
					cal.set(Calendar.MONTH, 3);
					Date dayInApril = cal.getTime();
					Date theSixthWorkDay = annualVacationService.getWorkingDayOfMonth(dayInApril, 6);
					Date thisDay = DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_SHORT), DateUtils.FORMAT_SHORT);

					Double lastYearRemainDays = 0.0;
					if (thisDay.before(theSixthWorkDay)) {
						lastYearRemainDays = getGreaterThan0(empLeave.getAllowRemainDays());
					} else {
						lastYearRemainDays = 0.0;
					}
					map.put("lastYearRemain", lastYearRemainDays);// 剩余年假总天数

					Double overUsed = empLeave.getUsedDays() - empLeave.getActualDays() <= 0 ? 0
							: empLeave.getUsedDays() - empLeave.getActualDays();
					// Double lastReductionDays = empLeave.getActualDays() -
					// empLeave.getUsedDays();
					//
					// map.put("lastYearRemain",
					// getGreaterThan0(lastReductionDays));//剩余年假总天数
					map.put("lastYearActual", getGreaterThan0(empLeave.getActualDays()));
					map.put("lastYearOverUsed", getGreaterThan0(overUsed));

				}
			}
		}

		return map;
	}

	@Override
	// TODO:假期详情显示有问题
	public Map<String, Object> getSickLeaveView(Long employeeId, int year) throws OaException {

		Integer type = ConfigConstants.LEAVE_TYPE_2;
		Date today = DateUtils.getToday();

		List<Date> dateList = new ArrayList<Date>();
		dateList.add(today);// 今年

		List<Integer> typeList = new ArrayList<Integer>();
		typeList.add(type);// 只查询病假

		// 查询今年的病假list
		List<EmpLeave> list = empLeaveMapper.getMyViewLeave(employeeId, dateList, typeList);

		Map<String, Object> map = new HashMap<String, Object>();
		Double sickUsed = 0d;
		if (null != list) {
			for (EmpLeave empLeave : list) {

				// 带薪
				if (empLeave.getCategory().intValue() == 0) {
					sickUsed += empLeave.getUsedDays();
					map.put("sickStartTime", empLeave.getStartTime());
					map.put("sickEndTime", empLeave.getEndTime());
					map.put("sickAllowDays", getGreaterThan0(empLeave.getAllowDays()));
					map.put("sickAllowRemain", getGreaterThan0(empLeave.getAllowRemainDays()));
					map.put("sickSalaryUsed", getGreaterThan0(empLeave.getUsedDays()));// 已使用带薪病假
					Double sickOverUsed = empLeave.getUsedDays() - empLeave.getActualDays() > 0
							? empLeave.getUsedDays() - empLeave.getActualDays() : 0;
					map.put("sickOverUsed", getGreaterThan0(sickOverUsed));// 透支
					map.put("sickActual", getGreaterThan0(empLeave.getActualDays()));// 总天数:allow
					Double reductionDays = empLeave.getActualDays() - empLeave.getUsedDays() > 0
							? empLeave.getActualDays() - empLeave.getUsedDays() : 0;
					map.put("reductionDays", getGreaterThan0(reductionDays));// 截至目前折算剩余带薪
				}
				// 非带薪
				if (empLeave.getCategory().intValue() == 2) {
					sickUsed += empLeave.getUsedDays();
					map.put("sickNotSalaryUsed", getGreaterThan0(empLeave.getUsedDays()));// 非带薪病假
				}else{//没有非带薪 初始化为0
					map.put("sickNotSalaryUsed", 0);// 非带薪病假
				}

			}
		}
			
		map.put("sickUsed", getGreaterThan0(sickUsed));// 带薪和非带薪之和
		return map;
	}

	@Override
	public Map<String, Object> getRestLeaveView(Long employeeId, int year) {

		Date today = DateUtils.getToday();

		Calendar cal = Calendar.getInstance();
		cal.setTime(today);
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.DAY_OF_MONTH, 1);

		final Date thisYearBegin = cal.getTime();
		cal.set(Calendar.MONTH, 2);
		cal.set(Calendar.DATE, 1);
		final Date thisYearEnd = DateUtils.addDay(DateUtils.addYear(cal.getTime(), 1), -1);
		final Date lastYearBegin = DateUtils.addYear(thisYearBegin, -1);
		final Date lastYearEnd = DateUtils.addYear(thisYearEnd, -1);

		List<Integer> typeList = new ArrayList<Integer>();
		typeList.add(ConfigConstants.LEAVE_TYPE_5);// 只查询调休

		List<Date> dateList = new ArrayList<Date>();
		dateList.add(today);// 今年
		dateList.add(DateUtils.addMonth(today, -12));// 去年

		// 查询去年和今年的年假list
		List<EmpLeave> list = empLeaveMapper.getMyViewLeave(employeeId, dateList, typeList);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("thisYearStartTime", thisYearBegin);
		map.put("thisYearEndTime", thisYearEnd);
		map.put("thisYearRemain", 0);
		map.put("thisYearUsed", 0);
		map.put("lastYearStartTime", lastYearBegin);
		map.put("lastYearEndTime", lastYearEnd);
		map.put("lastYearRemain", 0);
		map.put("lastYearUsed", 0);
		map.put("thisYearOtherRemain", 0d);//今年其它调休剩余
		map.put("lastYearOtherRemain", 0d);//去年其它调休剩余
		List<EmpLeave> thisYearOtherList = new ArrayList<EmpLeave>();
		map.put("thisYearOtherList", thisYearOtherList);//今年其它调休列表
		List<EmpLeave> lastYearOtherList = new ArrayList<EmpLeave>();
		map.put("lastYearOtherList", lastYearOtherList);//去年其它调休列表
		
		// 转换成map
		Integer leaveYear = null;
		if (null != list) {
			for (EmpLeave empLeave : list) {

				leaveYear = empLeave.getYear();

				if (leaveYear == year) {// 今年
					map.put("thisYearStartTime", empLeave.getStartTime());
					map.put("thisYearEndTime", empLeave.getEndTime());
					map.put("thisYearRemain", getGreaterThan0(empLeave.getAllowRemainDays()));
					map.put("thisYearUsed", getGreaterThan0(empLeave.getUsedDays()));
					if (map.containsKey("lastYearRemain")) {

					} else {
						map.put("lastYearRemain", 0);// 剩余年假总天数
					}
				} else if (leaveYear == year - 1) {// 去年

					map.put("lastYearStartTime", empLeave.getStartTime());
					map.put("lastYearEndTime", empLeave.getEndTime());

					/** 判断是否为4月第6个工作日 **/
					Calendar cal1 = Calendar.getInstance();
					cal1.set(Calendar.MONTH, 3);
					Date dayInApril = cal1.getTime();
					Date theSixthWorkDay = annualVacationService.getWorkingDayOfMonth(dayInApril, 6);
					Date thisDay = DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_SHORT), DateUtils.FORMAT_SHORT);

					Double lastYearRemainDays = 0.0;
//					if (thisDay.before(theSixthWorkDay)) {
//						lastYearRemainDays = getGreaterThan0(empLeave.getAllowRemainDays());
//					} else {
//						lastYearRemainDays = 0.0;
//					}
					map.put("lastYearRemain", lastYearRemainDays);
					map.put("lastYearUsed", getGreaterThan0(empLeave.getUsedDays()));

				}
			}
		}
		
		List<Integer> yearList = new ArrayList<Integer>();
		yearList.add(year);
		yearList.add(year-1);
		
		//查询其它调休
		List<EmpLeave> otherRestList = empLeaveMapper.getOtherRestLeaveList(employeeId, yearList);
		if(otherRestList!=null){
			for(EmpLeave empLeave : otherRestList){
				leaveYear = empLeave.getYear();
				if (leaveYear == year) {// 今年
					thisYearOtherList.add(empLeave);
					map.put("thisYearOtherRemain", (Double)map.get("thisYearOtherRemain")+empLeave.getAllowRemainDays());//今年其它调休剩余
				} else if (leaveYear == year - 1) {// 去年
					map.put("lastYearOtherRemain", (Double)map.get("lastYearOtherRemain")+empLeave.getAllowRemainDays());//去年其它调休剩余
					lastYearOtherList.add(empLeave);
				}
			}
		}
		
		return map;
	}

	@Override
	public Map<String, Object> getMyViewLeave(Long empId) throws OaException {
		// 获得当前用户，当前年份
		Date today = DateUtils.getToday();
		int year = Integer.parseInt(DateUtils.getYear(today));

		List<Integer> typeList = new ArrayList<Integer>();
		typeList.add(ConfigConstants.LEAVE_TYPE_1);
		typeList.add(ConfigConstants.LEAVE_TYPE_2);
		typeList.add(ConfigConstants.LEAVE_TYPE_5);

		List<Date> dateList = new ArrayList<Date>();
		dateList.add(today);
		dateList.add(DateUtils.addMonth(today, -12));// 去年

		List<EmpLeave> list = empLeaveMapper.getMyViewLeave(empId, dateList, typeList);

		// 不管查询结果如何，页面都要显示，年假，病假，调休信息,
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("year", DateUtils.format(today, "yyyy"));// 传到前台，供后续查询使用
		map.put("employeeId", empId);

		Integer type = null;
		Integer leaveYear = null;

		Double remainYearHolidayDays = 0.0, remainRestHolidayDays = 0.0;
		
		/** 判断是否为4月第6个工作日,需求：每年4月第6个工作日把去年年假显示为零。。。。。。。（跟数据库不一致） **/
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, 3);
		Date dayInApril = cal.getTime();
		Date theSixthWorkDay = annualVacationService.getWorkingDayOfMonth(dayInApril, 6);
		Date thisDay = DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_SHORT), DateUtils.FORMAT_SHORT);
		//年假是否能透支
		boolean overDrawFlag = configService.checkYearLeaveCanOverDraw();
		if(!overDrawFlag) {
			//今日可用年假
			Double endTodayDays = 0d;
			try {
				endTodayDays = getActualYearDays(today,empId);
			} catch (Exception e) {
			}
			
			map.put("endTodayDays", endTodayDays);
			//今日可用年假
		}
		
		if (null != list) {
			for (EmpLeave empLeave : list) {

				leaveYear = empLeave.getYear();
				type = empLeave.getType();

				if (leaveYear == year) {

					if (ConfigConstants.LEAVE_TYPE_1.equals(type)) {

						map.put("totalYearHolidayDays", getGreaterThan0(empLeave.getAllowDays()));
						// map.put("remainYearHolidayDays",
						// getGreaterThan0(empLeave.getAllowRemainDays()));
						remainYearHolidayDays = remainYearHolidayDays + getGreaterThan0(empLeave.getAllowRemainDays());
						map.put("usedYearHolidayDays", getGreaterThan0(empLeave.getUsedDays()));
						map.put("yearStartTime", empLeave.getStartTime());
						map.put("yearEndTime", empLeave.getEndTime());
					} else if (ConfigConstants.LEAVE_TYPE_2.equals(type)) {

						if (empLeave.getCategory().intValue() == 0) {
							map.put("totalSickHolidayDays", getGreaterThan0(empLeave.getAllowDays()));
							map.put("remainSickHolidayDays", getGreaterThan0(empLeave.getAllowRemainDays()));
							map.put("sickStartTime", empLeave.getStartTime());
							map.put("sickEndTime", empLeave.getEndTime());
							map.put("usedSickHolidayDays", getGreaterThan0(empLeave.getUsedDays()));
						}

					} else if (ConfigConstants.LEAVE_TYPE_5.equals(type)) {

						map.put("totalRestHolidayDays", getGreaterThan0(empLeave.getAllowDays()));
						// map.put("remainRestHolidayDays",
						// getGreaterThan0(empLeave.getAllowRemainDays()));
						remainRestHolidayDays = remainRestHolidayDays + getGreaterThan0(empLeave.getAllowRemainDays());
						map.put("usedRestHolidayDays", getGreaterThan0(empLeave.getUsedDays()));
						map.put("restStartTime", empLeave.getStartTime());
						map.put("restEndTime", empLeave.getEndTime());
					}

				} else if (leaveYear == year - 1) {

					if (ConfigConstants.LEAVE_TYPE_1.equals(type)) {

						Double lastYearRemainDays = 0.0;
						if (thisDay.before(theSixthWorkDay)) {
							lastYearRemainDays = getGreaterThan0(empLeave.getAllowRemainDays());
						} else {
							remainYearHolidayDays = 0.0;
						}
						remainYearHolidayDays = remainYearHolidayDays + lastYearRemainDays;
					} else if (ConfigConstants.LEAVE_TYPE_2.equals(type)) {

					} else if (ConfigConstants.LEAVE_TYPE_5.equals(type)) {

						Double lastYearRemainRestHolidayDays = 0.0;
						if (thisDay.before(theSixthWorkDay)) {
							lastYearRemainRestHolidayDays = getGreaterThan0(empLeave.getAllowRemainDays());
						} else {
							lastYearRemainRestHolidayDays = 0.0;
						}

						remainRestHolidayDays = remainRestHolidayDays + lastYearRemainRestHolidayDays;
					}

				}
			}
			map.put("remainYearHolidayDays", remainYearHolidayDays);
			map.put("remainRestHolidayDays", remainRestHolidayDays);
		}

		return map;
	}

	/**
	 * getLeaveRaduixConfig(获得假期配置) @Title: getLeaveRaduixConfig @Description:
	 * 获得假期配置 @return 设定文件 Map<String,Object> 返回类型 @throws
	 */
	public Map<String, Object> getLeaveRaduixConfig() {
		List<CompanyConfig> configs = companyConfigService.getListByCode(ConfigConstants.LEAVE_RADUIX_WELFARE);
		Map<String, Object> leaveMap = new HashMap<String, Object>();

		for (CompanyConfig conf : configs) {
			leaveMap.put(conf.getDisplayCode(), conf.getUserDef1());
		}

		return leaveMap;
	}

	/*****************************************************************************
	 * 根据司龄自动计算实际可用病假 start
	 ***********************************************************************************/
	public void calculationSickLeaveByEmpId(Long empId) {
		// 1.删除任务表
		EmpLeaveTask delTask = new EmpLeaveTask();
		delTask.setType(ConfigConstants.LEAVE_TYPE_2);
		delTask.setEmployeeId(empId);
		empLeaveTaskService.deleteAllByCondition(delTask);

		// 2.生成任务表
		EmpLeaveTask task = new EmpLeaveTask();
		task.setEmployeeId(empId);
		task.setCalDate(new Date());
		task.setStatus(ConfigConstants.CAL_STATUS_1);
		task.setType(ConfigConstants.LEAVE_TYPE_2);
		task.setCreateTime(new Date());
		task.setCreateUser(ConfigConstants.API);
		task.setVersion(ConfigConstants.DEFAULT_VERSION);
		task.setRemark("手动重跑员工病假");

		empLeaveTaskService.save(task);

		// 3.计算病假
		EmpLeaveTask leaveTask = new EmpLeaveTask();
		leaveTask.setStatus(ConfigConstants.CAL_STATUS_1);// 计算中
		leaveTask.setType(ConfigConstants.TASK_TYPE_2);
		leaveTask.setLimit(TASK_SIZE);
		leaveTask.setOffset(0);
		boolean flag = false;// 是否有下一页记录
		do {
			List<EmpLeaveTask> tasks = empLeaveTaskMapper.getListByCondition(leaveTask);

			for (EmpLeaveTask empLeaveTask : tasks) {
				calculationSickLeaveByTask(empLeaveTask);
			}

			flag = tasks.size() == TASK_SIZE.intValue() ? true : false;
		} while (flag);

	}

	/**
	 * calculationSickLeave(自动计算病假实际可用天数) @Title:
	 * calculationSickLeave @Description: 自动计算病假实际可用天数 void 返回类型 @throws
	 */
	@Override
	public void calculationSickLeave() {
		long startTime = System.currentTimeMillis();
		logger.info("计算病假开始... ...");

		// 1.删除原有任务
		logger.info("自动计算病假实际可用天数[step001] start...");
		int delCount = empLeaveTaskMapper.deleteAllByType(ConfigConstants.LEAVE_TYPE_2);
		logger.info("自动计算病假实际可用天数[step001],删除任务数={} end", delCount);

		// 2.批量生成员工任务
		logger.info("批量生成员工病假任务[step002] start...");
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("type", ConfigConstants.LEAVE_TYPE_2);
		data.put("empTypeIdList", configService.getNeedEmpTypeIdList());
		int saveCount = empLeaveTaskMapper.batchSaveYearTaskByType(data);
		logger.info("批量生成员工病假任务[step002],生成任务数={} end", saveCount);

		// 是否还有未计算年假的员工
		EmpLeaveTask leaveTask = new EmpLeaveTask();
		leaveTask.setStatus(ConfigConstants.CAL_STATUS_1);// 计算中
		leaveTask.setType(ConfigConstants.TASK_TYPE_2);
		leaveTask.setLimit(TASK_SIZE);
		leaveTask.setOffset(0);
		boolean flag = false;// 是否有下一页记录
		do {
			List<EmpLeaveTask> tasks = empLeaveTaskMapper.getListByCondition(leaveTask);

			for (EmpLeaveTask task : tasks) {
				calculationSickLeaveByTask(task);
			}

			flag = tasks.size() == TASK_SIZE.intValue() ? true : false;
		} while (flag);

		// 所有员工假期都已计算完，统计失败的员工数发送短信
		if (!flag) {
			sendErrorMsgByType(ConfigConstants.LEAVE_TYPE_2);
		}

		long endTime = System.currentTimeMillis();
		logger.info("计算病假结束,耗时{}秒", (endTime - startTime) / 1000);
	}

	/**
	 * calculationSickLeaveByTask(根据任务表计算病假) @Title:
	 * calculationSickLeaveByTask @Description: 根据任务表计算病假 @param task @throws
	 */
	public void calculationSickLeaveByTask(EmpLeaveTask task) {
		// 根据任务表里面的员工id找到员工信息
		EmployeeApp emp = null;

		try {
			emp = employeeAppMapper.getById(task.getEmployeeId());
			if (!"TOM001".equals(emp.getCode()) && !"TOM002".equals(emp.getCode()) && !"YZ001".equals(emp.getCode())) {// 杨总帐号不参与病假计算
				// 3.取出员工病假
				logger.info("取出员工{}病假[step003] start...", emp.getCnName());
				List<EmpLeave> empLeaves = getEmpLeaveByEmpIdAndType(emp.getId(),ConfigConstants.LEAVE_TYPE_2);
				logger.info("取出员工{}病假[step003],查询记录数={} end", emp.getCnName(), empLeaves.size());
				if (null == empLeaves || empLeaves.size() == 0) {
					throw new OaException("员工" + emp.getCnName() + "本年度病假信息数据为空");
				}

				// 4. 计算实际可用假期
				logger.info("计算员工{}实际可用病假假期[step004] start...", emp.getCnName());
				updateSickLeave(emp, empLeaves);
				logger.info("计算员工{}实际可用病假假期[step004] end", emp.getCnName());
			}

			// 8.更新任务状态为成功
			logger.info("更新员工{}病假任务状态为成功[step005] start...", emp.getCnName());
			task.setStatus(ConfigConstants.CAL_STATUS_2);
			updateTaskStatus(task);
			logger.info("更新员工{}病假任务状态为成功[step005] end", emp.getCnName());
		} catch (Exception e) {
			logger.error("员工{}病假计算出错，出错原因={}", emp!=null?emp.getCnName():"", e);

			logger.info("更新员工{}任务状态为失败 start ...", emp!=null?emp.getCnName():"");
			task.setStatus(ConfigConstants.YEAR_CAL_STATUS_3);
			task.setRemark(e.getMessage());
			updateTaskStatus(task);
			logger.info("更新员工{}任务状态为失败 end", emp!=null?emp.getCnName():"");
		}
	}

	/**
	 * updateSickLeave(更新病假信息) @Title: updateSickLeave @Description:
	 * 更新病假信息 @param emp 员工对象 @param empLeaves 假期信息 @throws OaException @throws
	 */
	public void updateSickLeave(EmployeeApp emp, List<EmpLeave> empLeaves) throws OaException {
		try {
			
			Date now = new Date();
			//判断离职日期与系统时间是否在同一年（是：按离职时间设定基数与实际，有效期;否：按年底设置）
			Date endDay = DateUtils.parse(DateUtils.getYear(now) + "-12-31 00:00:00");

			Double totalAlowDays = 0.0;// 总的允许年假可用天数
			Double totalActuDays = 0.0;// 总的实际年假可用天数
			Date joinInTime = emp.getFirstEntryTime();// 入职时间
			
			String sysYear = DateUtils.getYear(new Date());
			Date sysFirstDays = DateUtils.parse(DateUtils.getYear(new Date()) + "-01-01 00:00:00",
					DateUtils.FORMAT_SHORT);// 当年1月1号
			Date twelveMonthDate = DateUtils.parse(DateUtils.getYear(new Date()) + "-12-31 23:59:59",
					DateUtils.FORMAT_SHORT);// 当年12-31
			
			// 判断是否是2017年，2017年上半年是按照基数10计算病假，下半年是按照5计算病假
			if (YEAR_2017.equals(sysYear)) {
			     
			} else {// 2017年以后
				
				if (null == empLeaves || empLeaves.size() <=0) {
					throw new OaException("员工{}假期数据不存在", emp.getCnName());
				}
				
				if (null != empLeaves && empLeaves.size() > 1) {
					throw new OaException("员工{}假期数据出现重复", emp.getCnName());
				}
				
				// 判断入职日期是否在1月1号之前
				if (joinInTime.compareTo(sysFirstDays) <= 0) {
					joinInTime = sysFirstDays;// 入职开始时间
				}
				Integer joinInDays = DateUtils.getIntervalDays(joinInTime, now) + 1;// 入职天数
				if(emp.getQuitTime()!=null && DateUtils.getYear(now).equals(DateUtils.getYear(emp.getQuitTime()))){
					totalAlowDays = getLeaveDays(DateUtils.getIntervalDays(joinInTime, emp.getQuitTime()) + 1,
							empLeaves.get(0).getRaduix());
				}else{
					totalAlowDays = getLeaveDays(DateUtils.getIntervalDays(joinInTime, twelveMonthDate) + 1,
							empLeaves.get(0).getRaduix());
				}
				
				;// 总的允许休假天数
				totalActuDays = getLeaveDays(joinInDays, empLeaves.get(0).getRaduix());// 实际允许休假天数
																						// =
																						// 入职天数/当年总的天数*病假基数

				// 不分阶段扣减假期
				updateSickLeave(empLeaves, false, null, null, null, null, totalAlowDays, totalActuDays);
			}
		} catch (Exception e) {
			throw new OaException(e);
		}
	}

	/**
	 * @throws OaException
	 *             updateSickLeave(更新假期数据) @Title: updateSickLeave @Description:
	 *             更新假期数据 @param isStage true:分阶段更新,不分 @param firstAlowDays
	 *             第一阶段允许休假天数 @param firstActuDays 第一接阶段实际休假天数 @param
	 *             secondAlowDays 第二阶段允许休假天数 @param secondActuDays
	 *             第二接阶段实际休假天数 @param totalAlowDays 总的允许休假天数 @param
	 *             totalActuDays 总的实际休假天数 void 返回类型 @throws
	 */
	public void updateSickLeave(List<EmpLeave> empLeaves, boolean isStage, Double firstAlowDays, Double firstActuDays,
			Double secondAlowDays, Double secondActuDays, Double totalAlowDays, Double totalActuDays)
			throws OaException {
		try {
			if (isStage) {// 分阶段更新假期数据
				for (int i = 0; i < empLeaves.size(); i++) {
					EmpLeave empLeave = empLeaves.get(i);
					if (i == 0) {// 更新总的病假信息
						empLeave.setAllowDays(totalAlowDays);
						empLeave.setActualDays(totalActuDays);
						empLeave.setAllowRemainDays(totalAlowDays - empLeave.getUsedDays() > 0
								? totalAlowDays - empLeave.getUsedDays() : 0.0);// 这里不算占用天数
					} else if (i == 1) {// 更新第一阶段病假信息
						empLeave.setAllowDays(firstAlowDays);
						empLeave.setActualDays(firstActuDays);
						empLeave.setAllowRemainDays(
								firstAlowDays - empLeave.getUsedDays() - empLeave.getBlockedDays() > 0
										? firstAlowDays - empLeave.getUsedDays() - empLeave.getBlockedDays() : 0.0);
					} else if (i == 2) {// 更新第二阶段病假信息
						empLeave.setAllowDays(secondAlowDays);
						empLeave.setActualDays(secondActuDays);
						empLeave.setAllowRemainDays(
								secondAlowDays - empLeave.getUsedDays() - empLeave.getBlockedDays() > 0
										? secondAlowDays - empLeave.getUsedDays() - empLeave.getBlockedDays() : 0.0);
					}
					empLeave.setUpdateTime(new Date());
					empLeave.setUpdateUser(ConfigConstants.API);
					empLeaveMapper.updateById(empLeave);
				}
			} else {// 不分阶段更新假期数据
					// TODO:病假定时修改
				EmpLeave empLeave = empLeaves.get(0);
				empLeave.setAllowDays(totalAlowDays);
				empLeave.setActualDays(totalActuDays);
				empLeave.setAllowRemainDays(empLeave.getAllowDays()-(empLeave.getUsedDays()!=null?empLeave.getUsedDays():0));// 允许可用与使用值无关
				
				/*// 初入职的员工AllowRemainDays>AllowDays,这时还不会扣减,所以要将AllowRemainDays改为AllowDays
				empLeave.setAllowRemainDays(empLeave.getAllowRemainDays() > empLeave.getAllowDays()
						? empLeave.getAllowDays() : empLeave.getAllowRemainDays());// 允许可用与使用值无关
*/				
				empLeave.setUpdateTime(new Date());
				empLeave.setUpdateUser(ConfigConstants.API);
				empLeaveMapper.updateById(empLeave);
			}
		} catch (Exception e) {
			throw new OaException(e);
		}
	}

	/*****************************************************************************
	 * 根据司龄自动计算实际可用病假 end
	 ***********************************************************************************/

	/**
	 * getLeaveDays(根据入职天数和基数计算允许调休天数) @Title: getLeaveDays @Description:
	 * 根据入职天数和基数计算允许调休天数 @param joinInDays 入职天数 @param raduix 假期基数 @return 设定文件
	 * Double 返回类型 @throws
	 */
	public Double getLeaveDays(Integer joinInDays, Double raduix) {
		Integer sysYearDays = DateUtils.getMaxDaysOfYear(Integer.parseInt(DateUtils.getYear(new Date())));// 今年的总天数
		Double leaveDays = BigDecimal.valueOf((float) joinInDays / sysYearDays * raduix)
				.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		leaveDays = leaveDays.intValue() + ((leaveDays - leaveDays.intValue()) * 10 >= 5 ? 0.5 : 0);// 实际病假可用年假
																									// =
																									// 入职天数/本年度总天数
																									// *
																									// 病假基数(不满0.5算0,超过0.5不满1算0.5)

		return leaveDays;
	}

	/*****************************************************************************
	 * 根据司龄自动计算实际可用和剩余福利年假和总的实际可用和剩余年假 start
	 ***********************************************************************************/
	/**
	 * calculationYearLeaveByEmpId(根据员工id计算员工假期) @Title:
	 * calculationYearLeaveByEmpId @Description: 根据员工id计算员工假期 @param empId 设定文件
	 * void 返回类型 @throws
	 */
	@Override
	public void calculationYearLeaveByEmpId(Long empId) {
		// 1.删除任务表
		EmpLeaveTask delTask = new EmpLeaveTask();
		delTask.setType(ConfigConstants.LEAVE_TYPE_1);
		delTask.setEmployeeId(empId);
		empLeaveTaskService.deleteAllByCondition(delTask);

		// 2.生成任务表
		EmpLeaveTask task = new EmpLeaveTask();
		task.setEmployeeId(empId);
		task.setCalDate(new Date());
		task.setStatus(ConfigConstants.CAL_STATUS_1);
		task.setType(ConfigConstants.LEAVE_TYPE_1);
		task.setCreateTime(new Date());
		task.setCreateUser(ConfigConstants.API);
		task.setVersion(ConfigConstants.DEFAULT_VERSION);
		task.setRemark("手动重跑员工年假");

		empLeaveTaskService.save(task);

		// 3.获得福利假期配置
		Map<String, Object> leaveMap = getLeaveRaduixConfig();

		// 4.计算年假
		calculationYearLeaveByTask(task, leaveMap);

	}

	/**
	 * calculationYearLeave(自动计算可用年假和实际年假) @Title:
	 * calculationYearLeave @Description: 自动计算可用年假和实际年假 void 返回类型 @throws
	 */
	@Override
	public void calculationYearLeave() {
		String year = DateUtils.getYear(new Date());
		if ("2017".equals(year)) {
			logger.info("2017年年假不再计算");
			return;
		}

		long startTime = System.currentTimeMillis();
		logger.info("计算福利年假开始... ...");

		// 1.删除原有任务
		logger.info("计算福利年假[step001] start...");
		int delCount = empLeaveTaskMapper.deleteAllByType(ConfigConstants.LEAVE_TYPE_1);
		logger.info("计算福利年假[step001],删除记录数={} end", delCount);

		// 2.批量生成员工任务,只计算未设置离职日期的员工,Quit_time is nullH
		logger.info("批量生成员工年假任务[step002] start...");
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("type", ConfigConstants.LEAVE_TYPE_1);
		// data.put("autoCalculateLeave", "1");
		data.put("empTypeIdList", configService.getNeedEmpTypeIdList());
		int saveCount = empLeaveTaskMapper.batchSaveYearTaskByType(data);
		logger.info("批量生成员工年假任务[step002],获取记录数={} end", saveCount);

		// 3.福利假期配置
		logger.info("获取福利假期配置[step003] start...");
		Map<String, Object> leaveMap = getLeaveRaduixConfig();
		logger.info("获取福利假期配置[step003],获取记录数={} end", leaveMap.size());

		// 是否还有未计算年假的员工
		EmpLeaveTask leaveTask = new EmpLeaveTask();
		leaveTask.setStatus(ConfigConstants.CAL_STATUS_1);// 计算中
		leaveTask.setType(ConfigConstants.TASK_TYPE_1);
		leaveTask.setLimit(TASK_SIZE);
		leaveTask.setOffset(0);
		boolean flag = false;// 是否有下一页记录
		do {
			List<EmpLeaveTask> tasks = empLeaveTaskMapper.getListByCondition(leaveTask);

			for (EmpLeaveTask task : tasks) {
				calculationYearLeaveByTask(task, leaveMap);
			}

			flag = tasks.size() == TASK_SIZE.intValue() ? true : false;
		} while (flag);

		// 所有员工假期都已计算完，统计失败的员工数发送短信
		if (!flag) {
			sendErrorMsgByType(ConfigConstants.LEAVE_TYPE_1);
		}

		long endTime = System.currentTimeMillis();
		logger.info("计算福利年假结束,耗时{}秒", (endTime - startTime) / 1000);
	}

	/**
	 * sendErrorMsgByType(发送预警信息) @Title: sendErrorMsgByType @Description:
	 * 发送预警信息(0：司龄,1：年假，2：病假) @param type 预警类型 @throws
	 */
	public void sendErrorMsgByType(Integer type) {
		Long errorTaskCount = getErrorTaskCount(type);

		if (errorTaskCount > 0) {
			List<Config> configs = configService.getListByCode(ConfigConstants.LEAVE_CAL_FAIL_WARN_PHONE);

			for (Config conf : configs) {
				try {
					logger.info("发送异常信息给用户{}", conf.getDisplayCode());
					if (type.intValue() == ConfigConstants.TASK_TYPE_1.intValue()) {
						smsService.sendMessage(conf.getDisplayCode(),
								"[MO办公自动化系统-" + ENV + "]PROBLEM:" + errorTaskCount + "位员工实际年假计算出现问题，请尽快处理[短信生成时间="
										+ DateUtils.format(new Date(), DateUtils.FORMAT_DD_HH_MM) + "]");
					} else if (type.intValue() == ConfigConstants.TASK_TYPE_2.intValue()) {
						smsService.sendMessage(conf.getDisplayCode(),
								"[MO办公自动化系统-" + ENV + "]PROBLEM:" + errorTaskCount + "位员工实际病假计算出现问题，请尽快处理[短信生成时间="
										+ DateUtils.format(new Date(), DateUtils.FORMAT_DD_HH_MM) + "]");
					} else if (type.intValue() == ConfigConstants.TASK_TYPE_0.intValue()) {
						smsService.sendMessage(conf.getDisplayCode(),
								"[MO办公自动化系统-" + ENV + "]PROBLEM:员工司龄计算出现问题，请尽快处理[短信生成时间="
										+ DateUtils.format(new Date(), DateUtils.FORMAT_DD_HH_MM) + "]");
					}
				} catch (Exception e) {
					logger.error("给{}用户发送OA预警信息失败，失败原因:{}", conf.getDisplayCode(), e.getMessage());
				}
			}
		}
	}

	/**
	 * getErrorTaskCount(获取计算失败的任务记录数) @Title: getErrorTaskCount @Description:
	 * 获取计算失败的任务记录数 @param type 任务类型(0：司龄,1：年假，2：病假) @return 计算失败的任务记录数 @throws
	 */
	public Long getErrorTaskCount(Integer type) {
		EmpLeaveTask task = new EmpLeaveTask();
		task.setStatus(ConfigConstants.YEAR_CAL_STATUS_3);// 计算失败
		task.setType(type);

		return empLeaveTaskService.getCount(task);
	}

	/**
	 * calculationYearLeaveByTask(根据任务表计算年假) @Title:
	 * calculationYearLeaveByTask @Description: 根据任务表计算年假 @param task
	 * 年假任务 @param leaveMap 假期配置 void 返回类型 @throws
	 */
	public void calculationYearLeaveByTask(EmpLeaveTask task, Map<String, Object> leaveMap) {
		EmployeeApp emp = null;
		

		try {
			// 根据任务表里面的员工id找到员工信息
			emp = employeeAppMapper.getById(task.getEmployeeId());
			if (!"TOM001".equals(emp.getCode()) && !"TOM002".equals(emp.getCode()) && !"YZ001".equals(emp.getCode())) {// 杨总帐号不参与年假计算
				// 4.获得计算司龄
				logger.info("员工{}根据入职时间为:{}计算司龄[不满1年按0计算][step004] start...", emp.getCnName(), emp.getFirstEntryTime());
				Map<String, Object> ourAgeMap = getOurAge(emp);// 获得实际司龄和计算司龄（实际司龄=入职天数/12保留一位小树，计算司龄=不满1年按0算）
				Double acuOurAge = (Double) ourAgeMap.get("acuOurAge");// 计算司龄(不满1年按0计算)
				Double ourAge = emp.getOurAge();// 现有司龄
				logger.info("员工{}计算司龄为[不满1年按0计算]:{}年,实际司龄为" + ourAge + "年[step004] end", emp.getCnName(), acuOurAge);

				// 5.根据计算司龄获得最新的法定/福利假期基数
				if (emp.getAutoCalculateLeave().intValue() == ConfigConstants.AUTO_CAL_YEAR_RADUIX) {
					updateYearRaduix(acuOurAge, emp, leaveMap);
				}
				// 6.取出员工年假
				logger.info("取出员工{}年假[step006] start...", emp.getCnName());
				List<EmpLeave> empLeaves = getEmpLeaveByEmpIdAndType(emp.getId(), ConfigConstants.LEAVE_TYPE_1);
				logger.info("取出员工{}年假[step006],查询记录数={} end", emp.getCnName(), empLeaves.size());
				if (null == empLeaves || empLeaves.size() == 0) {
					throw new OaException("员工" + emp.getCnName() + "本年度年假信息数据为空");
				} else if (empLeaves.size() < LEAVE_MIN_COUNT_3) {
					logger.error("员工{}本年度年假信息数据出错", emp.getCnName());
					throw new OaException("员工" + emp.getCnName() + "本年度年假信息数据出错");
				}

				// 7.计算可用假期
				logger.info("员工{}更新可用假期 start[step007] ...", emp.getCnName());
				updateYearLeave(emp.getAutoCalculateLeave(), emp.getFirstEntryTime(), emp.getQuitTime(), empLeaves);
				logger.info("员工{}更新可用假期 end[step007]", emp.getCnName());
			}

			// 8.更新任务状态为成功
			logger.info("更新员工{}任务状态为成功[step008] start...", emp.getCnName());
			task.setStatus(ConfigConstants.CAL_STATUS_2);
			updateTaskStatus(task);
			logger.info("更新员工{}任务状态为成功[step008] end", emp.getCnName());
		} catch (Exception e) {
			logger.error("员工{}年假计算出错，出错原因={}", emp!=null?emp.getCnName():"", e);

			logger.info("更新员工{}任务状态为失败 start ...", emp!=null?emp.getCnName():"");
			task.setStatus(ConfigConstants.YEAR_CAL_STATUS_3);
			task.setRemark(e.getMessage());
			updateTaskStatus(task);
			logger.info("更新员工{}任务状态为失败 end", emp!=null?emp.getCnName():"");
		}
	}

	public void updateYearRaduix(Double calOurAge, EmployeeApp emp, Map<String, Object> leaveMap) throws OaException {
		// 更改基数的同时需要同时修改使用和绑定的值
		EmpLeave oldFixLeave = getNewLeave(emp.getId(), ConfigConstants.LEAVE_TYPE_1, ConfigConstants.CATEGORY_1);// 现有法定假期
		EmpLeave oldWelfareLeave = getNewLeave(emp.getId(), ConfigConstants.LEAVE_TYPE_1, ConfigConstants.CATEGORY_2);// 获得现有福利假期
		if (null == oldFixLeave || null == oldWelfareLeave) {
			return;
			//throw new OaException("员工" + emp.getCnName() + "本年度年假信息数据为空!");
		}

		// 未满一年按一年算
		if (calOurAge == 0) {
			calOurAge = 1.0;
		}
		// calourAge = beforeAge + workAge;
		if (emp.getOurAge() == null) {
			emp.setOurAge(0d);
		}
		Double ourAge = calOurAge - emp.getBeforeWorkAge().intValue();
		if (calOurAge < 10) {// 总工龄不满10年
			Double newWelfareRaduix = CommonUtils.converToDouble(leaveMap.get("5_" + ourAge.intValue()));// 新的福利假期基数
			if (null != newWelfareRaduix) {// 可以取到福利基数
				// 对比取消的福利基数和现有福利基数是否一致
				if (!isChangeWelfareRaduix(oldWelfareLeave.getRaduix(), newWelfareRaduix)) {// 需调整福利基数
					logger.info("员工{}福利基数由{}更新成" + newWelfareRaduix + "start[step006] ...", emp.getCnName(),
							oldWelfareLeave.getRaduix());
					oldWelfareLeave.setRaduix(newWelfareRaduix);
					empLeaveMapper.updateById(oldWelfareLeave);
					logger.info("员工{}福利基数由{}更新成" + newWelfareRaduix + "end[step006]", emp.getCnName(),
							oldWelfareLeave.getRaduix());
				}
			} else {// 取不到福利基数
				logger.info("员工{}取不到福利假期基数", emp.getCnName());
			}
		} else if (calOurAge >= 10 && calOurAge < 20) {
			// 总工龄满10不满20
			if (!isChangeWelfareRaduix(oldFixLeave.getRaduix(), 10.0)) {// 不一致，则调整基数
				logger.info("员工总工龄已经满10年{}法定基数由{}更新成" + 10 + "start[step006] ...", emp.getCnName(),
						oldFixLeave.getRaduix());
				oldFixLeave.setRaduix(10.0);
				empLeaveMapper.updateById(oldFixLeave);
				logger.info("员工总工龄已经满10年{}法定基数由{}更新成" + 10 + "end[step006]", emp.getCnName(), oldFixLeave.getRaduix());
			}
			// 在司工龄与福利年假挂钩
			if (ourAge >= 10) {
				ourAge = 9d;
			}
			Double newWelfareRaduix = CommonUtils.converToDouble(leaveMap.get("10_" + ourAge.intValue()));
			if (!isChangeWelfareRaduix(oldWelfareLeave.getRaduix(), newWelfareRaduix)) {
				logger.info("员工在邮乐工作已经满" + ourAge + "年{}福利基数由{}更新成" + newWelfareRaduix + "start[step006] ...",
						emp.getCnName(), oldWelfareLeave.getRaduix());
				oldWelfareLeave.setRaduix(newWelfareRaduix);
				empLeaveMapper.updateById(oldWelfareLeave);
				logger.info("员工在邮乐工作已经满" + ourAge + "年{}福利基数由{}更新成" + newWelfareRaduix + "end[step006]",
						emp.getCnName(), oldWelfareLeave.getRaduix());
			}
		} else if (calOurAge >= 20) {// 总工龄满20年，则法定基数为15，福利为0
			// 对比调整前的假期基数和调整后的假期基数是否一致
			if (!isChangeWelfareRaduix(oldFixLeave.getRaduix(), 15.0)) {// 不一致，则调整基数
				logger.info("员工总工龄已经满20年{}法定基数由{}更新成" + 15 + "start[step006] ...", emp.getCnName(),
						oldFixLeave.getRaduix());
				oldFixLeave.setRaduix(15.0);
				empLeaveMapper.updateById(oldFixLeave);
				logger.info("员工总工龄已经满20年{}法定基数由{}更新成" + 15 + "end[step006]", emp.getCnName(), oldFixLeave.getRaduix());

				logger.info("员工总工龄已经满20年{}福利基数由{}更新成" + 0 + "start[step006] ...", emp.getCnName(),
						oldWelfareLeave.getRaduix());
				oldWelfareLeave.setRaduix(0.0);
				empLeaveMapper.updateById(oldWelfareLeave);
				logger.info("员工总工龄已经满20年{}福利基数由{}更新成" + 0 + "end[step006]", emp.getCnName(),
						oldWelfareLeave.getRaduix());
			}
		}
	}

	/**
	 * updateYearLeave(不分阶段更新年假) @Title: updateYearLeave @Description:
	 * 不分阶段更新年假 @param emp @param empLeaves @throws OaException 设定文件 void
	 * 返回类型 @throws
	 */

	public void updateYearLeave(int isUpdate, Date joinInTime, Date quitTime, List<EmpLeave> empLeaves)
			throws OaException {
		try {
			Date now = new Date();
			//判断离职日期与系统时间是否在同一年（是：按离职时间设定基数与实际，有效期;否：按年底设置）
			Date endDay = DateUtils.parse(DateUtils.getYear(now) + "-12-31 00:00:00");
			Date firstDay = DateUtils.parse(DateUtils.getYear(new Date()) + "-01-01 00:00:00");
			Date endDay1 = DateUtils.parse(DateUtils.format(DateUtils.addMonth(DateUtils.getCurrYearLast(), 3), DateUtils.FORMAT_SHORT)+" 23:59:59", DateUtils.FORMAT_LONG);
			
			// 假期最后有效时间改成离职日期这个时间
			if (quitTime != null) {
				for (int i = 0; i < empLeaves.size(); i++) {
					empLeaves.get(i).setEndTime(DateUtils.parse(DateUtils.format(quitTime, DateUtils.FORMAT_SHORT)+" 23:59:59", DateUtils.FORMAT_LONG));
				}
			} else {
				// 获取最后一天
				for (int i = 0; i < empLeaves.size(); i++) {
					empLeaves.get(i).setEndTime(endDay1);
				}
			}
			
			Date sysDate = new Date();
			
			Integer joinInDays = 0;// 入职天数

			if (joinInTime.compareTo(firstDay) <= 0) {// 入职时间在1月1号之前,入职天数 = 当前时间
														// – 1月1号;
				joinInTime = firstDay;
				joinInDays = DateUtils.getIntervalDays(firstDay, sysDate);
			} else {// 入职天数 = 当前时间 – 入职时间;
				joinInDays = DateUtils.getIntervalDays(joinInTime, sysDate);
			}

			Integer category = null;
			EmpLeave empLeave = null;
			EmpLeave fixLeave = new EmpLeave();
			EmpLeave welLeave = new EmpLeave();
			EmpLeave totleLeave = new EmpLeave();
			Double actualDays = 0.0;// 福利/法定实际休假天数
			Double allowDays = 0.0;// 福利/法定允许休假天数
			Double raduix = 0.0;// 福利/法定基数
			Double blockedDays = 0.0;// 福利/法定占用天数
			// TODO:基数变更都会导致剩余为负数,动态去均衡年假使用情况

			for (EmpLeave leave : empLeaves) {
				if (leave.getCategory().intValue() == ConfigConstants.CATEGORY_0.intValue()) {
					totleLeave = leave;
				} else if (leave.getCategory().intValue() == ConfigConstants.CATEGORY_1.intValue()) {
					fixLeave = leave;
				} else if (leave.getCategory().intValue() == ConfigConstants.CATEGORY_2.intValue()) {
					welLeave = leave;
				}
			}
			for (int i = empLeaves.size() - 1; i >= 0; i--) {
				empLeave = empLeaves.get(i);
				category = empLeave.getCategory();

				if (category == ConfigConstants.CATEGORY_2.intValue()
						|| category == ConfigConstants.CATEGORY_1.intValue()) {// 福利/法定
					// 设置了离职时间,结束日期按离职那天算
					if (quitTime != null && DateUtils.getYear(now).equals(DateUtils.getYear(quitTime))) {
						if (isUpdate == ConfigConstants.AUTO_CAL_YEAR_RADUIX) {
							empLeave.setAllowDays(getLeaveDays(DateUtils.getIntervalDays(joinInTime, quitTime) + 1,
									empLeave.getRaduix()));
						}
					} else {
						if (isUpdate == ConfigConstants.AUTO_CAL_YEAR_RADUIX) {
							empLeave.setAllowDays(getLeaveDays(
									DateUtils.getIntervalDays(joinInTime,
											endDay) + 1,
									empLeave.getRaduix()));
						}
					}
					allowDays += empLeave.getAllowDays();
					actualDays += getLeaveDays(joinInDays + 1, empLeave.getRaduix());
					raduix += empLeave.getRaduix();
					blockedDays += empLeave.getBlockedDays();
					empLeave.setActualDays(getLeaveDays(joinInDays + 1, empLeave.getRaduix()));
					empLeave.setAllowRemainDays(
							empLeave.getAllowDays() - empLeave.getUsedDays() - empLeave.getBlockedDays());
				} else {// 总假期
					empLeave.setRaduix(raduix);
					empLeave.setAllowDays(allowDays);
					empLeave.setActualDays(actualDays);
					empLeave.setBlockedDays(blockedDays);
					empLeave.setAllowRemainDays(empLeave.getAllowDays() - empLeave.getUsedDays());
				}
				empLeave.setUpdateTime(new Date());
				empLeave.setUpdateUser(ConfigConstants.API);
				updateById(empLeave);
			}

		} catch (Exception e) {
			throw new OaException(e);
		}

	}

	public void updateQuitYearLeave(Date joinInTime, Date quitTime, List<EmpLeave> empLeaves) throws OaException {
		// 只要年假期总数据没有错
		// 假期最后有效时间改成离职日期这个时间
		if (quitTime != null) {
			for (int i = 0; i < empLeaves.size(); i++) {
				empLeaves.get(i).setEndTime(quitTime);
			}
		} else {
			// 获取最后一天
			for (int i = 0; i < empLeaves.size(); i++) {
				empLeaves.get(i).setEndTime(DateUtils.getCurrYearLast());
			}
		}
		Date sysDate = quitTime == null ? new Date() : quitTime;// 若员工离职日期为空,则使用当前时间
		Date firstDay = DateUtils.parse(DateUtils.getYear(new Date()) + "-01-01 00:00:00");
		Integer joinInDays = 0;// 入职天数

		if (joinInTime.compareTo(firstDay) <= 0) {// 入职时间在1月1号之前,入职天数 = 当前时间 –
													// 1月1号;
			joinInTime = firstDay;
			joinInDays = DateUtils.getIntervalDays(firstDay, sysDate);
		} else {// 入职天数 = 当前时间 – 入职时间;
			joinInDays = DateUtils.getIntervalDays(joinInTime, sysDate);
		}

		// 遍历假期信息(可能存在4条假期数,2条福利)
		Integer category = null;
		EmpLeave empLeaveCount = null;// 总年假
		EmpLeave empLeaveFD = null;// 法定年假
		EmpLeave welfareLeave = null;// 福利年假
		for (int z = 0; z < empLeaves.size(); z++) {
			if (empLeaves.get(z).getCategory().intValue() == 0) {
				empLeaveCount = empLeaves.get(z);
			}
			if (empLeaves.get(z).getCategory().intValue() == 1) {
				empLeaveFD = empLeaves.get(z);
			}
			if (empLeaves.get(z).getCategory().intValue() == 2) {
				welfareLeave = empLeaves.get(z);
			}
		}
		
		if(empLeaveCount==null&&empLeaveFD==null&&welfareLeave==null){
			return;
		}
		
		if(empLeaveCount==null||empLeaveFD==null) {
			return;
		}
		
		if (empLeaveCount.getBlockedDays() != 0 || empLeaveFD.getBlockedDays() != 0
				|| (welfareLeave!=null&&welfareLeave.getBlockedDays() != 0)) {
			throw new OaException("操作失败：该员工有假期未审批！");
		}
		empLeaveFD.setAllowDays(getLeaveDays(DateUtils.getIntervalDays(joinInTime, empLeaveFD.getEndTime()) + 1,
				empLeaveFD.getRaduix()));
		if(welfareLeave!=null) {
			welfareLeave.setAllowDays(getLeaveDays(DateUtils.getIntervalDays(joinInTime, empLeaveFD.getEndTime()) + 1,
					welfareLeave.getRaduix()));
			welfareLeave.setActualDays(welfareLeave.getAllowDays());
		}
		empLeaveCount.setAllowDays((welfareLeave!=null?welfareLeave.getAllowDays():0d) + empLeaveFD.getAllowDays());
		empLeaveFD.setActualDays(empLeaveFD.getAllowDays());
		if (quitTime == null) {
			empLeaveFD.setActualDays(getLeaveDays(DateUtils.getIntervalDays(joinInTime, DateUtils.getToday()) + 1,
					empLeaveFD.getRaduix()));
			if(welfareLeave!=null) {
				welfareLeave.setActualDays(getLeaveDays(DateUtils.getIntervalDays(joinInTime, DateUtils.getToday()) + 1,
						welfareLeave.getRaduix()));
			}
		}
		empLeaveCount.setActualDays(empLeaveFD.getActualDays() + (welfareLeave!=null?welfareLeave.getActualDays():0d));
		/*
		 * if(quitTime != null) { //设置了离职时间,结束日期按离职那天算 每次离职时 根据离职时间重新计算
		 * AllowDays
		 * empLeaveFD.setAllowDays(getLeaveDays(DateUtils.getIntervalDays(
		 * joinInTime, quitTime) + 1, empLeaveFD.getRaduix()));
		 * welfareLeave.setAllowDays(getLeaveDays(DateUtils.getIntervalDays(
		 * joinInTime, quitTime) + 1, welfareLeave.getRaduix())); }else {
		 * //未设置离职时间,结束日期按可用假期最后一天算 取消离职时 根据 年末 时间 重新计算 AllowDays
		 * empLeaveFD.setAllowDays(getLeaveDays(DateUtils.getIntervalDays(
		 * joinInTime, empLeaveFD.getEndTime()) + 1, empLeaveFD.getRaduix()));
		 * welfareLeave.setAllowDays(getLeaveDays(DateUtils.getIntervalDays(
		 * joinInTime, welfareLeave.getEndTime()) +
		 * 1,welfareLeave.getRaduix())); }
		 */

		empLeaveCount.setAllowRemainDays(empLeaveCount.getAllowDays() - empLeaveCount.getUsedDays());
		if (empLeaveFD.getAllowDays() >= empLeaveCount.getUsedDays()) {
			// 法定假期允许可用>=总的使用
			empLeaveFD.setUsedDays(empLeaveCount.getUsedDays());
			empLeaveFD.setAllowRemainDays(empLeaveFD.getAllowDays() - empLeaveFD.getUsedDays());
			if(welfareLeave!=null) {
				welfareLeave.setUsedDays(0d);
				welfareLeave.setAllowRemainDays(welfareLeave.getAllowDays());
			}
		} else {
			// 法定假期允许可用<总的使用
			empLeaveFD.setUsedDays(empLeaveFD.getAllowDays());
			empLeaveFD.setAllowRemainDays(0d);
			if(welfareLeave!=null) {
				welfareLeave.setUsedDays(empLeaveCount.getUsedDays() - empLeaveFD.getUsedDays());
				welfareLeave.setAllowRemainDays(empLeaveCount.getAllowRemainDays());
			}
		}

		empLeaveFD.setUpdateTime(new Date());
		empLeaveFD.setUpdateUser(ConfigConstants.API);
		updateById(empLeaveFD);
		if(welfareLeave!=null) {
			welfareLeave.setUpdateTime(new Date());
			welfareLeave.setUpdateUser(ConfigConstants.API);
			updateById(welfareLeave);
		}
		empLeaveCount.setUpdateTime(new Date());
		empLeaveCount.setUpdateUser(ConfigConstants.API);
		updateById(empLeaveCount);
		/*
		 * if(empLeaveCount.getBlockedDays()!=0 ||
		 * empLeaveFD.getBlockedDays()!=0 || welfareLeave.getBlockedDays()!=0){
		 * throw new OaException("操作失败：人事在审批假期"); }else{ category =
		 * empLeaveFD.getCategory(); //每天 有定时任务重新算下所有年假 if(category ==
		 * ConfigConstants.CATEGORY_2 || category ==
		 * ConfigConstants.CATEGORY_1){//福利/法定 if(quitTime != null) {
		 * //设置了离职时间,结束日期按离职那天算 每次离职时 根据离职时间重新计算 AllowDays
		 * empLeaveFD.setAllowDays(getLeaveDays(DateUtils.getIntervalDays(
		 * joinInTime, quitTime) + 1, empLeaveFD.getRaduix()));
		 * welfareLeave.setAllowDays(getLeaveDays(DateUtils.getIntervalDays(
		 * joinInTime, quitTime) + 1, welfareLeave.getRaduix())); }else {
		 * //未设置离职时间,结束日期按可用假期最后一天算 取消离职时 根据 年末 时间 重新计算 AllowDays
		 * empLeaveFD.setAllowDays(getLeaveDays(DateUtils.getIntervalDays(
		 * joinInTime, empLeaveFD.getEndTime()) + 1, empLeaveFD.getRaduix()));
		 * welfareLeave.setAllowDays(getLeaveDays(DateUtils.getIntervalDays(
		 * joinInTime, welfareLeave.getEndTime()) +
		 * 1,welfareLeave.getRaduix())); } } //如果 总年假已用数据大于 总可用假期 大于多的数据
		 * 在法定假期中已负数显示
		 * if(empLeaveCount.getUsedDays()>empLeaveCount.getActualDays()){ //福利
		 * welfareLeave.setUsedDays(empLeaveCount.getUsedDays()-empLeaveFD.
		 * getActualDays());
		 * welfareLeave.setAllowRemainDays(welfareLeave.getActualDays()-(
		 * empLeaveCount.getUsedDays()-empLeaveFD.getActualDays())); //法定假期计算
		 * empLeaveFD.setUsedDays(empLeaveFD.getActualDays()); //法定可用基数 -
		 * （总年假已用-法定可用基数） empLeaveFD.setAllowRemainDays(0d); }else{//否则就先
		 * 把法定假期用完 在去把福利假期填完 //如果已用年假大于 可用法定年假 就把法定年假补满
		 * if(empLeaveCount.getUsedDays()>empLeaveFD.getActualDays()){
		 * empLeaveFD.setUsedDays(empLeaveFD.getActualDays());
		 * empLeaveFD.setAllowRemainDays(0d); //福利假期计算
		 * welfareLeave.setUsedDays(empLeaveCount.getUsedDays()-empLeaveFD.
		 * getActualDays()); //福利可用基数 - （总年假已用-法定可用基数）
		 * welfareLeave.setAllowRemainDays(welfareLeave.getActualDays()-(
		 * empLeaveCount.getUsedDays()-empLeaveFD.getActualDays())); }else{
		 * empLeaveFD.setUsedDays(empLeaveCount.getUsedDays());
		 * empLeaveFD.setAllowRemainDays(empLeaveFD.getActualDays()-
		 * empLeaveCount.getUsedDays()); //福利假期计算 welfareLeave.setUsedDays(0d);
		 * //福利可用基数 - （总年假已用-法定可用基数）
		 * welfareLeave.setAllowRemainDays(welfareLeave.getActualDays() -
		 * welfareLeave.getUsedDays()); } } empLeaveFD.setUpdateTime(new
		 * Date()); empLeaveFD.setUpdateUser(ConfigConstants.API);
		 * updateById(empLeaveFD); welfareLeave.setUpdateTime(new Date());
		 * welfareLeave.setUpdateUser(ConfigConstants.API);
		 * updateById(welfareLeave);
		 * empLeaveCount.setRaduix(empLeaveCount.getRaduix());
		 * empLeaveCount.setUsedDays(empLeaveFD.getUsedDays()+welfareLeave.
		 * getUsedDays());
		 * empLeaveCount.setAllowDays(empLeaveFD.getAllowDays()+welfareLeave.
		 * getAllowDays());
		 * empLeaveCount.setActualDays(empLeaveFD.getActualDays()+welfareLeave.
		 * getActualDays());
		 * empLeaveCount.setBlockedDays(empLeaveFD.getBlockedDays()+welfareLeave
		 * .getBlockedDays());
		 * empLeaveCount.setAllowRemainDays(empLeaveFD.getAllowRemainDays() +
		 * welfareLeave.getAllowRemainDays()); empLeaveCount.setUpdateTime(new
		 * Date()); empLeaveCount.setUpdateUser(ConfigConstants.API);
		 * updateById(empLeaveCount); }
		 */
	}

	/**
	 * updateYearLeave(不分阶段更新病假) @Title: updateSickYearLeave @Description:
	 * 不分阶段更新病假 @param emp @param empLeaves @throws OaException 设定文件 void
	 * 返回类型 @throws
	 */
	public void updateSickYearLeave(Date joinInTime, Date quitTime, List<EmpLeave> empLeaves) throws OaException {
		if(empLeaves==null||empLeaves.size()<=0){
			return;
		}else if (empLeaves.get(0).getBlockedDays() != 0) {
			throw new OaException("操作失败：人事在审批假期");
		} else {
			try {
				// 假期最后有效时间改成离职日期这个时间
				if (quitTime != null) {
					for (int i = 0; i < empLeaves.size(); i++) {
						empLeaves.get(i).setEndTime(quitTime);
					}
				} else {
					// 获取最后一天
					for (int i = 0; i < empLeaves.size(); i++) {
						empLeaves.get(i).setEndTime(DateUtils.getCurrYearLast());
					}
				}
				Date sysDate = quitTime == null ? new Date() : quitTime;// 若员工离职日期为空,则使用当前时间
				Date firstDay = DateUtils.parse(DateUtils.getYear(new Date()) + "-01-01 00:00:00");
				Integer joinInDays = 0;// 入职天数

				if (joinInTime.compareTo(firstDay) <= 0) {// 入职时间在1月1号之前,入职天数 =
															// 当前时间 – 1月1号;
					joinInTime = firstDay;
					joinInDays = DateUtils.getIntervalDays(firstDay, sysDate);
				} else {// 入职天数 = 当前时间 – 入职时间;
					joinInDays = DateUtils.getIntervalDays(joinInTime, sysDate);
				}

				// 遍历假期信息(可能存在4条假期数,2条福利)
				Integer category = null;
				EmpLeave empLeave = null;
				Double actualDays = 0.0;// 福利/法定实际休假天数
				Double allowDays = 0.0;// 福利/法定允许休假天数
				Double raduix = 0.0;// 福利/法定基数
				Double blockedDays = 0.0;// 福利/法定占用天数
				for (int i = empLeaves.size() - 1; i >= 0; i--) {
					empLeave = empLeaves.get(i);
					category = empLeave.getCategory();

					// 总的病假数据
					if (category == ConfigConstants.CATEGORY_0.intValue()) {
						if (quitTime != null) {
							// 设置了离职时间,结束日期按离职那天算
							empLeave.setAllowDays(getLeaveDays(DateUtils.getIntervalDays(joinInTime, quitTime) + 1,
									empLeave.getRaduix()));
						} else {
							// 未设置离职时间,结束日期按可用假期最后一天算
							empLeave.setAllowDays(
									getLeaveDays(DateUtils.getIntervalDays(joinInTime, empLeave.getEndTime()) + 1,
											empLeave.getRaduix()));
						}
						// 只有一条数据 没有福利法定家亲戚 不必累加 直接赋值
						allowDays = empLeave.getAllowDays();
						actualDays = getLeaveDays(joinInDays + 1, empLeave.getRaduix());
						raduix = empLeave.getRaduix();
						blockedDays = empLeave.getBlockedDays();
						empLeave.setActualDays(getLeaveDays(joinInDays + 1, empLeave.getRaduix()));
						// 这里减去都是 用 实际可修-已用假期
						empLeave.setAllowRemainDays(empLeave.getActualDays() - empLeave.getUsedDays());

					}
					empLeave.setUpdateTime(new Date());
					empLeave.setUpdateUser(ConfigConstants.API);
					updateById(empLeave);
				}
			} catch (Exception e) {
				throw new OaException(e);
			}
		}
	}

	/**
	 * isChangeWelfareRaduix(判断是否需要调整福利假期基数) @Title:
	 * isChangeWelfareRaduix @Description: 判断是否需要调整福利假期基数 @param
	 * oldWelfareFixLeaveRaduix 现有的福利假期基数 @param newWelfareRaduix
	 * 根据司龄计算出来的福利基数 @return 设定文件 boolean
	 * true：根据司龄计算出来的福利基数和现有福利基数一致,false:不一致 @throws
	 */
	public boolean isChangeWelfareRaduix(Double oldWelfareFixLeaveRaduix, Double newWelfareRaduix) {
		// 根据司龄计算出来的福利基数和现有福利基数是否一致
		if (newWelfareRaduix.equals(oldWelfareFixLeaveRaduix)) {// 一致，则不用调整福利假期基数
			return true;
		} else {// 不一致，需要调整福利假期和总的年假基数
			return false;
		}
	}

	/**
	 * generateYearLeave(生成新的年假假期数据) @Title: generateYearLeave @Description:
	 * 生成新的年假假期数据 @param empLeave 原有年假数据 @param raduix 新的假期基数 @param allowDays
	 * 新的允许休假天数 @param actualDays 新的实际休假天数 @param usedDays 新的已用假期天数 @param
	 * blockedDays 新的占用假期天数 @param allowRemainDays 新的剩余假期天数 @param type
	 * 假期类型 @param category 二级分类 @param parendId 父节点id @throws
	 */
	public void generateYearLeave(EmpLeave empLeave, Double raduix, Double allowDays, Double actualDays,
			Double usedDays, Double blockedDays, Double allowRemainDays, Integer type, Integer category, Date startTime,
			Date endTime) {
		empLeave.setRaduix(raduix);
		empLeave.setAllowDays(allowDays);
		empLeave.setActualDays(actualDays);
		empLeave.setUsedDays(usedDays);
		empLeave.setBlockedDays(blockedDays);
		empLeave.setAllowRemainDays(allowRemainDays);
		empLeave.setStartTime(startTime);
		empLeave.setEndTime(endTime);
		empLeave.setCreateTime(new Date());
		empLeave.setCreateUser(ConfigConstants.API);
		empLeave.setUpdateTime(new Date());
		empLeave.setUpdateUser(ConfigConstants.API);

		empLeaveMapper.save(empLeave);
	}

	/**
	 * getNewLeave(获得最新的假期信息) @Title: getNewLeave @Description: 获得最新的假期信息 @param
	 * empId 员工id @param type 假期类型 @param category 二级分类 @return EmpLeave
	 * 返回类型 @throws
	 */
	public EmpLeave getNewLeave(Long empId, Integer type, Integer category) {
		EmpLeave empLeave = new EmpLeave();

		empLeave.setEmployeeId(empId);
		empLeave.setYear(Integer.parseInt(DateUtils.getYear(new Date())));
		empLeave.setType(type);
		empLeave.setCategory(category);

		return empLeaveMapper.getNewLeaveByCondition(empLeave);
	}

	/**
	 * getEmpLeaveByEmpId(根据员工id获取员工假期信息) @Title:
	 * getEmpLeaveByEmpId @Description: 根据员工id获取员工假期信息 @param emp @return 设定文件
	 * List<EmpLeave> 返回类型 @throws
	 */
	public List<EmpLeave> getEmpLeaveByEmpIdAndType(Long empId,Integer type) {
		EmpLeave empLeave = new EmpLeave();
		empLeave.setType(type);
		empLeave.setEmployeeId(empId);
		empLeave.setYear(Integer.parseInt(DateUtils.getYear(new Date())));
		empLeave.setIsActive(ConfigConstants.IS_ACTIVE_INTEGER);
		if (type.intValue() == 2) {
			empLeave.setCategory(0);
		}

		return empLeaveMapper.getListByCondition(empLeave);
	}

	/*****************************************************************************
	 * 根据司龄自动计算福利年假和总的年假 start
	 ***********************************************************************************/

	/*******************************************************************************
	 * 根据入职时间自动计算员工司龄 start
	 ****************************************************************/
	/**
	 * calculationOurAge(根据入职时间自动计算员工司龄) @Title: calculationOurAge @Description:
	 * 根据入职时间自动计算员工司龄 void 返回类型 @throws
	 */
	@Override
	public void calculationOurAge() {
		long startTime = System.currentTimeMillis();

		try {
			// 计算司龄
			Date now = new Date();
			int year = Integer.valueOf(DateUtils.format(now, "yyyy"));
			if ("0101".equals(DateUtils.format(now, "MMdd"))) {
				year = year - 1;
				// 年初将不自动计算年假的员工属性还原
				employeeMapper.resetAutoCalculateLeave();
			}
			employeeAppService.updateAllEmpOurAge(DateUtils.getMaxDaysOfYear(year));
		} catch (OaException e) {
			logger.error("员工司龄计算出错,出错原因={}", e);
			sendErrorMsgByType(ConfigConstants.TASK_TYPE_0);
		}

		long endTime = System.currentTimeMillis();
		logger.info("根据入职时间自动计算员工司龄结束,耗时{}秒", (endTime - startTime) / 1000);

		// 同步计算年假
		calculationYearLeave();

		// 同步计算病假
		calculationSickLeave();

	}

	/**
	 * @throws OaException
	 *             getOurAge(计算员工司龄) @Title: getOurAge @Description:
	 *             计算员工司龄 @param emp @return 设定文件 Double 返回类型 @throws
	 */
	public Map<String, Object> getOurAge(EmployeeApp emp) throws OaException {
		Date joinInTime = emp.getFirstEntryTime();// 入职时间
		// 入职前司龄
		Double beforeWorkAge = emp.getBeforeWorkAge();
		if (null == joinInTime) {
			throw new OaException("员工{}入职时间为空，不能自动计算假期", emp.getCnName());
		}

		Map<String, Object> ourAgeMap = new HashMap<String, Object>();
		Double acuOurAge = 0.0;// 实际司龄（精确到一位小树）
		Integer calOurAge = 0;// 计算司龄，不满1年按0算
		Date sysDate = emp.getQuitTime() == null ? new Date() : emp.getQuitTime();// 当前时间,若有离职时间按离职时间算
		// 计算司龄_公司司龄
		acuOurAge = DateUtils.getJoinYear(joinInTime, sysDate) + beforeWorkAge.intValue();
		calOurAge = acuOurAge.intValue();

		ourAgeMap.put("calOurAge", calOurAge);// 计算司龄，不满1年按0算
		ourAgeMap.put("acuOurAge", acuOurAge);// 实际司龄，不满1年按四舍五入保留一位小树算

		return ourAgeMap;
	}

	/**
	 * updateOurAge(变更司龄) @Title: updateOurAge @Description: 变更司龄 @param
	 * emp @param ourAgeMap @return @throws OaException 设定文件 boolean
	 * 返回类型 @throws
	 */
	public boolean updateOurAge(EmployeeApp emp, Double acuOurAge) throws OaException {
		boolean flag = false;

		// 更新司龄
		emp.setOurAge(acuOurAge);
		emp.setUpdateTime(new Date());
		emp.setUpdateUser(ConfigConstants.API);
		int updateCount = employeeAppMapper.updateById(emp);

		if (updateCount == 0) {
			logger.info("员工{}变更司龄失败", emp.getCnName());
			throw new OaException("当前员工正在被另外一个线程更新");
		}

		flag = true;
		return flag;
	}

	/*******************************************************************************
	 * 根据入职时间自动计算员工司龄 end
	 ****************************************************************/

	/*******************************************************************************
	 * 初始化下一年的年假 start
	 ********************************************************************/
	/**
	 * initNextYearLeave(初始化下一年的年假) @Title: initNextYearLeave @Description:
	 * 初始化下一年的年假 void 返回类型 @throws
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public void initNextYearLeave() {
		logger.info("初始化所有员工下一年度的年假信息开始... ...");
		Long startTime = System.currentTimeMillis();

		// 1.删除下一年度的假期数据
		EmpLeave leave = new EmpLeave();
		leave.setType(ConfigConstants.LEAVE_TYPE_1);
		leave.setYear(Integer.parseInt(DateUtils.getYear(new Date())) + 1);
		empLeaveMapper.delNextLeaveByCondition(leave);

		// 2.找出所有在职员工
		List<EmployeeApp> empList = employeeAppMapper.getInitEmpList(configService.getNeedEmpTypeIdList());
		logger.info("有{}位员工需要初始化下一年度的年假信息", empList.size());

		// 3.遍历所有员工，生成每个员工的年假数据
		for (EmployeeApp emp : empList) {
			saveDefaultNextYearLeave(emp,null,0);
		}
		Long endTime = System.currentTimeMillis();
		logger.info("初始化所有员工下一年度的年假信息结束,耗时={}秒", (endTime - startTime) / 1000);
	}

	/**
	 * saveDefaultNextYearLeave(生成默认的下一年度年假信息) @Title:
	 * saveDefaultNextYearLeave @Description: 生成默认的下一年度年假信息 @param emp @return
	 * 设定文件 List<EmpLeave> 返回类型 @throws
	 */
	public List<EmpLeave> saveDefaultNextYearLeave(EmployeeApp emp,String yearStr,Integer betDays) {
		Integer year = 0;
		int thisYear = 0;
		if(StringUtils.isNotBlank(yearStr)) {
			year = Integer.parseInt(yearStr);
			// 获得今年年份
			thisYear = year - 1;
		}else {
			year = Integer.parseInt(DateUtils.getYear(new Date())) + 1;
			// 获得今年年份
			thisYear= Integer.parseInt(DateUtils.getYear(new Date()));
		}
		// 获取入职年份
		int entryYear = Integer.parseInt(DateUtils.getYear(emp.getFirstEntryTime()));
		// 获得今年年底日期
		Date endOfYearDate = DateUtils.parse(thisYear + "-12-31 00:00:00");
		List<EmpLeave> empLeaves = new ArrayList<EmpLeave>();
		EmpLeave leave = null;
		Long parendId = 0L;// 父节点id
		/****************** 计算假期基数 **********************/
		// 总年假基数
		Double totalLeaveRaduix = ConfigConstants.DEFAULE_YEAR * 2;
		// 法定年假基数
		Double fixRaduix = ConfigConstants.DEFAULE_YEAR;
		// 福利年假基数
		Double welfareRaduix = ConfigConstants.DEFAULE_YEAR;
		// 根据入职时间算出假期基数
		Map<String, Double> leaveRaduixMap = getRadiuxByEntryTime(emp,yearStr,betDays);
		if (leaveRaduixMap != null && leaveRaduixMap.size() > 0 && leaveRaduixMap.get("fixRaduix") != null
				&& leaveRaduixMap.get("welfareRaduix") != null) {
			// 法定年假基数
			fixRaduix = leaveRaduixMap.get("fixRaduix");
			// 福利年假基数
			welfareRaduix = leaveRaduixMap.get("welfareRaduix");
			// 总年假基数
			totalLeaveRaduix = fixRaduix + welfareRaduix;

		}
		/****************** 计算假期基数 **********************/

		for (int i = 0; i < LEAVE_MIN_COUNT_3; i++) {
			leave = new EmpLeave();

			leave.setCreateTime(new Date());
			leave.setCreateUser(ConfigConstants.API);
			leave.setUpdateTime(new Date());
			leave.setUpdateUser(ConfigConstants.API);
			leave.setType(ConfigConstants.LEAVE_TYPE_1);
			leave.setYear(year);
			leave.setEmployeeId(emp.getId());
			leave.setCompanyId(emp.getCompanyId());
			leave.setStartTime(DateUtils.parse(year + "-01-01 00:00:00"));
			leave.setEndTime(
					emp.getQuitTime() == null ? DateUtils.parse((year + 1) + "-03-31 23:59:59") : emp.getQuitTime());
			leave.setParendId(parendId);
			leave.setIsActive(ConfigConstants.IS_ACTIVE_INTEGER);
			leave.setDelFlag(ConfigConstants.IS_NO_INTEGER);
			leave.setVersion(ConfigConstants.DEFAULT_VERSION);
			// 允许休假天数等于基数
			Double totalLeaveDays = totalLeaveRaduix;
			Double fixLeaveDays = fixRaduix;
			Double welfareLeaveDays = welfareRaduix;
			// 如果是待离职的员工重新计算天数
			if (emp.getQuitTime() != null && emp.getJobStatus() == 2) {
				fixLeaveDays = getLeaveDays(
						DateUtils.getIntervalDays(DateUtils.getYearBegin(emp.getQuitTime()), emp.getQuitTime()) + 1,
						fixRaduix);
				welfareLeaveDays = getLeaveDays(
						DateUtils.getIntervalDays(DateUtils.getYearBegin(emp.getQuitTime()), emp.getQuitTime()) + 1,
						welfareRaduix);
				totalLeaveDays = fixLeaveDays + welfareLeaveDays;
			}
			// 如果是今年入职则根据入职天数重新计算可休假天数
			if (entryYear == thisYear) {
				// 计算入职后到年底的司龄
				Integer intervalDays = DateUtils.getIntervalDays(emp.getFirstEntryTime(), endOfYearDate) + 1;
				fixLeaveDays = BigDecimal.valueOf((float) intervalDays / DateUtils.getMaxDaysOfYear(thisYear) * fixRaduix)
						.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				fixLeaveDays = fixLeaveDays.intValue() + ((fixLeaveDays - fixLeaveDays.intValue()) * 10 >= 5 ? 0.5 : 0);
				welfareLeaveDays = BigDecimal.valueOf(
						(float) intervalDays / DateUtils.getMaxDaysOfYear(thisYear) * welfareRaduix)
								.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				welfareLeaveDays = welfareLeaveDays.intValue()
						+ ((welfareLeaveDays - welfareLeaveDays.intValue()) * 10 >= 5 ? 0.5 : 0);
				totalLeaveDays = fixLeaveDays + welfareLeaveDays;
			}
			if (i == 0) {// 生成总的年假
				leave.setActualDays(totalLeaveDays);
				leave.setAllowDays(totalLeaveDays);
				leave.setAllowRemainDays(totalLeaveDays);
				leave.setCategory(ConfigConstants.CATEGORY_0);
				leave.setRaduix(totalLeaveRaduix);
				empLeaveMapper.save(leave);

				parendId = leave.getId();
				empLeaves.add(leave);
				continue;
			}

			if (i == 1) {// 生成法定年假
				leave.setRaduix(fixRaduix);
				leave.setActualDays(fixLeaveDays);
				leave.setAllowDays(fixLeaveDays);
				leave.setAllowRemainDays(fixLeaveDays);
				leave.setCategory(ConfigConstants.CATEGORY_1);
				empLeaveMapper.save(leave);
				empLeaves.add(leave);
				continue;
			}

			if (i == 2) {// 生成福利年假
				leave.setRaduix(welfareRaduix);
				leave.setActualDays(welfareLeaveDays);
				leave.setAllowDays(welfareLeaveDays);
				leave.setAllowRemainDays(welfareLeaveDays);
				leave.setCategory(ConfigConstants.CATEGORY_2);
				empLeaveMapper.save(leave);
				empLeaves.add(leave);
			}
		}

		return empLeaves;
	}

	/*******************************************************************************
	 * 初始化下一年的年假 end
	 **********************************************************************/

	/*******************************************************************************
	 * 初始化下一年的病假 end
	 **********************************************************************/
	/**
	 * initNextSickLeave(初始化下一年的病假) @Title: initNextSickLeave @Description:
	 * 初始化下一年的病假 void 返回类型 @throws
	 */
	@Override
	public void initNextSickLeave() {
		logger.info("初始化所有员工下一年度的病假信息开始... ...");
		Long startTime = System.currentTimeMillis();

		// 1.删除下一年度的假期数据
		EmpLeave leave = new EmpLeave();
		leave.setType(ConfigConstants.LEAVE_TYPE_2);
		leave.setYear(Integer.parseInt(DateUtils.getYear(new Date())) + 1);
		empLeaveMapper.delNextLeaveByCondition(leave);
		
		//员工类型列表
		List<Long> empTypeIdList = configService.getNeedEmpTypeIdList();

		// 2.找出所有在职,待离职>下一年1月1号的员工
		EmployeeApp employee = new EmployeeApp();
		employee.setJobStatus(ConfigConstants.JOB_STATUS_0);
		employee.setEmpTypeIdList(empTypeIdList);
		employee.setWorkAddressType(0);
		List<EmployeeApp> empList = employeeAppMapper.getListByCondition(employee);
		employee.setJobStatus(ConfigConstants.JOB_STATUS_2);
		// 设置离职时间为第二年的开始时间
		employee.setQuitTime(DateUtils.getNextYearBegin());
		List<EmployeeApp> quitingEmpList = employeeAppMapper.getListByCondition(employee);
		logger.info("有{}位员工需要初始化下一年度的病假信息", empList.size() + quitingEmpList.size());

		// 3.遍历所有员工，生成每个员工的病假数据
		for (EmployeeApp emp : empList) {
			generatorNextSickLeaveByEmp(emp);
		}
		for (EmployeeApp quitingEmp : quitingEmpList) {
			generatorNextSickLeaveByEmpAndQuitTime(quitingEmp);
		}
		Long endTime = System.currentTimeMillis();
		logger.info("初始化所有员工下一年度的病假信息结束,耗时={}秒", (endTime - startTime) / 1000);
	}

	/**
	 * generatorNextSickLeaveByEmp(生成每个员工的病假数据) @Title:
	 * generatorNextSickLeaveByEmp @Description: 生成每个员工的年假数据 @param emp 设定文件
	 * void 返回类型 @throws
	 */
	public void generatorNextSickLeaveByEmp(EmployeeApp emp) {
		// 生成默认的下一年度的假期（基数5）
		saveDefaultNextSickLeave(emp.getId(), emp.getCompanyId());
	}

	/**
	 * saveDefaultNextSickLeave(生成默认的下一年度病假信息) @Title:
	 * saveDefaultNextSickLeave @Description: 生成默认的下一年度年假信息 @param emp @return
	 * 设定文件 List<EmpLeave> 返回类型 @throws
	 */
	public void saveDefaultNextSickLeave(Long empId, Long companyId) {
		Integer year = Integer.parseInt(DateUtils.getYear(new Date())) + 1;
		EmpLeave leave = new EmpLeave();
		// 根据入职时间查询病假基数，如果入职满一年，则设置病假基数为5
		EmployeeApp employeeInfo = employeeAppMapper.getById(empId);
		// 获得入职年份
		int entryYear = Integer.parseInt(DateUtils.getYear(employeeInfo.getFirstEntryTime()));
		// 获得今年年份
		int thisYear = Integer.parseInt(DateUtils.getYear(new Date()));
		// 获得今年年底日期
		Date endOfYearDate = DateUtils.parse(thisYear + "-12-31 00:00:00");
		Double sickRaduix = ConfigConstants.DEFAULT_SICK;
		leave.setRaduix(sickRaduix);
		leave.setCreateTime(new Date());
		leave.setCreateUser(ConfigConstants.API);
		leave.setUpdateTime(new Date());
		leave.setUpdateUser(ConfigConstants.API);
		leave.setType(ConfigConstants.LEAVE_TYPE_2);
		leave.setYear(year);
		leave.setEmployeeId(empId);
		leave.setCompanyId(companyId);
		leave.setStartTime(DateUtils.parse(year + "-01-01 00:00:00"));
		leave.setEndTime(DateUtils.parse(year + "-12-31 23:59:59"));
		leave.setParendId(0L);
		leave.setIsActive(ConfigConstants.IS_ACTIVE_INTEGER);
		leave.setDelFlag(ConfigConstants.IS_NO_INTEGER);
		leave.setVersion(ConfigConstants.DEFAULT_VERSION);
		// 休假天数等于基数
		Double leaveDays = sickRaduix;
		// 如果是今年入职则根据入职天数计算病假天数
		if (entryYear == thisYear) {
			// 计算入职后到年底的司龄
			Integer intervalDays = DateUtils.getIntervalDays(employeeInfo.getFirstEntryTime(), endOfYearDate) + 1;
			leaveDays = BigDecimal.valueOf((float) intervalDays / DateUtils.getMaxDaysOfYear(thisYear) * sickRaduix)
					.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			leaveDays = leaveDays.intValue() + ((leaveDays - leaveDays.intValue()) * 10 >= 5 ? 0.5 : 0);
		}
		leave.setActualDays(leaveDays);
		leave.setAllowDays(leaveDays);
		leave.setAllowRemainDays(leaveDays);
		leave.setCategory(ConfigConstants.CATEGORY_0);

		empLeaveMapper.save(leave);

	}

	public void generatorNextSickLeaveByEmpAndQuitTime(EmployeeApp emp) {
		// 生成默认的下一年度的假期（基数5）
		saveQuitingEmpNextSickLeave(emp.getId(), emp.getCompanyId(), emp.getQuitTime());
	}

	/**
	 * saveDefaultNextSickLeave(生成默认的下一年度病假信息) @Title:
	 * saveDefaultNextSickLeave @Description: 生成默认的下一年度年假信息 @param emp @return
	 * 设定文件 List<EmpLeave> 返回类型 @throws
	 */
	public void saveQuitingEmpNextSickLeave(Long empId, Long companyId, Date quittime) {
		Integer year = Integer.parseInt(DateUtils.getYear(new Date())) + 1;
		EmpLeave leave = new EmpLeave();
		// 根据入职时间查询病假基数，如果入职满一年，则设置病假基数为5
		EmployeeApp employeeInfo = employeeAppMapper.getById(empId);
		// 获得入职年份
		int entryYear = Integer.parseInt(DateUtils.getYear(employeeInfo.getFirstEntryTime()));
		// 获得今年年份
		int thisYear = Integer.parseInt(DateUtils.getYear(new Date()));
		// 获得今年年底日期
		Date endOfYearDate = DateUtils.parse(thisYear + "-12-31 00:00:00");
		Double sickRaduix = ConfigConstants.DEFAULT_SICK;
		leave.setRaduix(sickRaduix);
		leave.setCreateTime(new Date());
		leave.setCreateUser(ConfigConstants.API);
		leave.setUpdateTime(new Date());
		leave.setUpdateUser(ConfigConstants.API);
		leave.setType(ConfigConstants.LEAVE_TYPE_2);
		leave.setYear(year);
		leave.setEmployeeId(empId);
		leave.setCompanyId(companyId);
		leave.setStartTime(DateUtils.parse(year + "-01-01 00:00:00"));
		leave.setEndTime(DateUtils.parse(DateUtils.format(quittime, DateUtils.FORMAT_SHORT) + " 23:59:59"));
		leave.setParendId(0L);
		leave.setIsActive(ConfigConstants.IS_ACTIVE_INTEGER);
		leave.setDelFlag(ConfigConstants.IS_NO_INTEGER);
		leave.setVersion(ConfigConstants.DEFAULT_VERSION);
		Double leaveDays = getLeaveDays(DateUtils.getIntervalDays(DateUtils.getYearBegin(quittime), quittime) + 1,
				sickRaduix);
		// 如果是今年入职则根据入职天数计算病假天数
		if (entryYear == thisYear) {
			// 计算入职后到年底的司龄
			Integer intervalDays = DateUtils.getIntervalDays(employeeInfo.getFirstEntryTime(), endOfYearDate) + 1;
			leaveDays = BigDecimal.valueOf((float) intervalDays / DateUtils.getMaxDaysOfYear(thisYear) * sickRaduix)
					.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			leaveDays = leaveDays.intValue() + ((leaveDays - leaveDays.intValue()) * 10 >= 5 ? 0.5 : 0);
		}
		leave.setActualDays(leaveDays);
		leave.setAllowDays(leaveDays);
		leave.setAllowRemainDays(leaveDays);
		leave.setCategory(ConfigConstants.CATEGORY_0);

		empLeaveMapper.save(leave);

	}

	/*******************************************************************************
	 * 初始化下一年的病假 end
	 **********************************************************************/

	public void updateTaskStatus(EmpLeaveTask task) {
		task.setUpdateTime(new Date());
		task.setUpdateUser(ConfigConstants.API);
		empLeaveTaskMapper.updateById(task);
	}

	@Override
	public EmpLeave getByCondition(EmpLeave empLeave) throws OaException {
		List<EmpLeave> list = getByListCondition(empLeave);

		if (null != list && list.size() > 0) {
			return list.get(0);
		}

		return empLeave;
	}

	@Override
	public List<EmpLeave> getByListCondition(EmpLeave empLeave) throws OaException {
		return empLeaveMapper.getListByCondition(empLeave);
	}

	@Override
	public int updateById(EmpLeave record) throws OaException {
		return empLeaveMapper.updateById(record);
	}

	@Override
	public int save(EmpLeave record) throws OaException {
		return empLeaveMapper.save(record);
	}

	private Double getGreaterThan0(Double param) {

		if (null == param || param < 0) {
			return 0.0;
		} else {
			return param;
		}
	}

	/**
	 * @throws OaException
	 *             generateDefaultYearLeave(生成默认的年假) @Title:
	 *             generateDefaultYearLeave @Description: 生成默认的年假 @param empId
	 *             员工id @param companyId 公司id @param year 假期所属年份 @throws
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public void generateDefaultYearLeave(Employee emp, Integer year, Date entryTime) throws OaException {
		logger.info("生成员工{}{}年年假开始... ...", emp.getId(), year);
		// 入职往后 年病假往后推三个月
		List<EmpLeave> leaves = getLeaveByEmpIdAndTypeAndYear(emp.getId(), ConfigConstants.LEAVE_TYPE_1, year);
		if (null == leaves || leaves.size() == 0) {
			EmpLeave totalLeave = new EmpLeave();

			totalLeave.setCreateTime(new Date());
			totalLeave.setCreateUser(ConfigConstants.API);
			totalLeave.setUpdateTime(new Date());
			totalLeave.setUpdateUser(ConfigConstants.API);
			totalLeave.setType(ConfigConstants.LEAVE_TYPE_1);
			totalLeave.setYear(year);
			totalLeave.setEmployeeId(emp.getId());
			totalLeave.setCompanyId(emp.getCompanyId());
			totalLeave.setStartTime(entryTime);
			totalLeave.setEndTime(DateUtils.parse((year + 1) + "-03-31 23:59:59"));
			totalLeave.setIsActive(ConfigConstants.IS_ACTIVE_INTEGER);
			totalLeave.setDelFlag(ConfigConstants.IS_NO_INTEGER);
			totalLeave.setVersion(ConfigConstants.DEFAULT_VERSION);

			EmployeeApp employeeInfo = new EmployeeApp();
			employeeInfo.setId(emp.getId());
			employeeInfo.setCnName(emp.getCnName());
			employeeInfo.setFirstEntryTime(emp.getFirstEntryTime());
			employeeInfo.setBeforeWorkAge(emp.getBeforeWorkAge() == null ? 0 : emp.getBeforeWorkAge());
			employeeInfo.setQuitTime(emp.getQuitTime());
			// 复制基础信息
			EmpLeave baseLeave = new EmpLeave();
			BeanUtils.copyProperties(totalLeave, baseLeave);
			// 总年假基数
			Double totalLeaveRaduix = ConfigConstants.DEFAULE_YEAR * 2;
			// 法定年假基数
			Double fixRaduix = ConfigConstants.DEFAULE_YEAR;
			// 福利年假基数
			Double welfareRaduix = ConfigConstants.DEFAULE_YEAR;
			// 根据入职时间计算年假基数
			Map<String, Double> leaveRaduixMap = getRadiuxByEntryTime(employeeInfo,null,0);
			if (leaveRaduixMap != null && leaveRaduixMap.size() > 0 && leaveRaduixMap.get("fixRaduix") != null
					&& leaveRaduixMap.get("welfareRaduix") != null) {
				// 总年假基数
				totalLeaveRaduix = leaveRaduixMap.get("fixRaduix") + leaveRaduixMap.get("welfareRaduix");
				// 法定年假基数
				fixRaduix = leaveRaduixMap.get("fixRaduix");
				// 福利年假基数
				welfareRaduix = leaveRaduixMap.get("welfareRaduix");
			}
			// 休假天数等于基数
			Double totalLeaveDays = totalLeaveRaduix;
			Double fixLeaveDays = totalLeaveRaduix;
			Double welfareLeaveDays = totalLeaveRaduix;
			// 获取入职年份
			int entryYear = Integer.parseInt(DateUtils.getYear(emp.getFirstEntryTime()));
			// 获得今年年份
			int thisYear = Integer.parseInt(DateUtils.getYear(new Date()));
			// 获得今年年底日期
			Date endOfYearDate = DateUtils.parse(thisYear + "-12-31 00:00:00");
			// 如果是今年入职则根据入职天数重新计算可休假天数
			if (entryYear == thisYear) {
				// 计算入职后到年底的司龄
				Integer intervalDays = DateUtils.getIntervalDays(emp.getFirstEntryTime(), endOfYearDate) + 1;
				fixLeaveDays = BigDecimal.valueOf((float) intervalDays / DateUtils.getMaxDaysOfYear(thisYear) * fixRaduix)
						.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				fixLeaveDays = fixLeaveDays.intValue() + ((fixLeaveDays - fixLeaveDays.intValue()) * 10 >= 5 ? 0.5 : 0);
				welfareLeaveDays = BigDecimal.valueOf(
						(float) intervalDays / DateUtils.getMaxDaysOfYear(thisYear) * welfareRaduix)
								.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				welfareLeaveDays = welfareLeaveDays.intValue()
						+ ((welfareLeaveDays - welfareLeaveDays.intValue()) * 10 >= 5 ? 0.5 : 0);
				totalLeaveDays = fixLeaveDays + welfareLeaveDays;
			}
			// 生成总的年假
			totalLeave.setAllowDays(totalLeaveDays);
			totalLeave.setAllowRemainDays(totalLeaveDays);
			totalLeave.setCategory(ConfigConstants.CATEGORY_0);
			totalLeave.setRaduix(totalLeaveRaduix);
			empLeaveMapper.save(totalLeave);

			Long parendId = totalLeave.getId();

			// 生成法定年假
			EmpLeave fixLeave = baseLeave;

			fixLeave.setRaduix(fixRaduix);
			fixLeave.setAllowDays(fixLeaveDays);
			fixLeave.setAllowRemainDays(fixLeaveDays);
			fixLeave.setCategory(ConfigConstants.CATEGORY_1);
			fixLeave.setParendId(parendId);
			empLeaveMapper.save(fixLeave);

			// 生成福利年假
			EmpLeave welfareLeave = baseLeave;
			welfareLeave.setRaduix(welfareRaduix);
			welfareLeave.setAllowDays(welfareLeaveDays);
			welfareLeave.setAllowRemainDays(welfareLeaveDays);
			welfareLeave.setCategory(ConfigConstants.CATEGORY_2);
			welfareLeave.setParendId(parendId);
			empLeaveMapper.save(welfareLeave);
			return;
		}
		logger.info("生成员工{}{}年年假结束!!! !!!", emp.getId(), year);

		throw new OaException("员工" + emp.getId() + "[" + year + "年]已经存在年假，不再自动生成年假数据");
	}

	/**
	 * @throws OaException
	 *             generateDefaultSickLeave(生成默认的病假) @Title:
	 *             generateDefaultSickLeave @Description: 生成默认的病假 @param empId
	 *             员工id @param companyId 公司id @param year 假期所属年份 @throws
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public void generateDefaultSickLeave(Employee emp, Integer year, Date entryTime) throws OaException {
		logger.info("生成员工{}{}年病假开始... ...", emp.getId(), year);
		List<EmpLeave> leaves = getLeaveByEmpIdAndTypeAndYear(emp.getId(), ConfigConstants.LEAVE_TYPE_2, year);
		if (null == leaves || leaves.size() == 0) {

			EmpLeave empLeave = new EmpLeave();
			Double sickRaduix = ConfigConstants.DEFAULT_SICK;
			// 计算病假基数
			EmployeeApp employeeInfo = new EmployeeApp();
			employeeInfo.setId(emp.getId());
			employeeInfo.setCnName(emp.getCnName());
			employeeInfo.setFirstEntryTime(emp.getFirstEntryTime());
			employeeInfo.setBeforeWorkAge(emp.getBeforeWorkAge() == null ? 0 : emp.getBeforeWorkAge());
			employeeInfo.setQuitTime(emp.getQuitTime());
			empLeave.setCreateTime(new Date());
			empLeave.setCreateUser(ConfigConstants.API);
			empLeave.setUpdateTime(new Date());
			empLeave.setUpdateUser(ConfigConstants.API);
			empLeave.setType(ConfigConstants.LEAVE_TYPE_2);
			empLeave.setYear(year);
			empLeave.setEmployeeId(emp.getId());
			empLeave.setCompanyId(emp.getCompanyId());
			empLeave.setStartTime(entryTime);
			empLeave.setEndTime(DateUtils.parse(year + "-12-31 23:59:59"));
			empLeave.setIsActive(ConfigConstants.IS_ACTIVE_INTEGER);
			empLeave.setDelFlag(ConfigConstants.IS_NO_INTEGER);
			empLeave.setVersion(ConfigConstants.DEFAULT_VERSION);
			// 休假天数等于基数
			Double leaveDays = sickRaduix;
			// 获取入职年份
			int entryYear = Integer.parseInt(DateUtils.getYear(emp.getFirstEntryTime()));
			// 获得今年年份
			int thisYear = Integer.parseInt(DateUtils.getYear(new Date()));
			// 获得今年年底日期
			Date endOfYearDate = DateUtils.parse(thisYear + "-12-31 00:00:00");
			// 如果是今年入职则根据入职天数重新计算可休假天数
			if (entryYear == thisYear) {
				// 计算入职后到年底的司龄
				Integer intervalDays = DateUtils.getIntervalDays(emp.getFirstEntryTime(), endOfYearDate) + 1;
				leaveDays = BigDecimal.valueOf((float) intervalDays / DateUtils.getMaxDaysOfYear(thisYear) * sickRaduix)
						.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				leaveDays = leaveDays.intValue() + ((leaveDays - leaveDays.intValue()) * 10 >= 5 ? 0.5 : 0);
			}
			empLeave.setAllowDays(leaveDays);
			empLeave.setAllowRemainDays(leaveDays);
			empLeave.setCategory(ConfigConstants.CATEGORY_0);
			empLeave.setRaduix(sickRaduix);
			empLeaveMapper.save(empLeave);
			logger.info("生成员工{}{}年病假结束... ...", emp.getId(), year);
			return;
		}
		throw new OaException("员工" + emp.getId() + "[" + year + "年]已经存在病假，不再自动生成病假数据");
	}

	/**
	 * getLeaveByEmpIdAndTypeAndYear(根据员工id获得假期) @Title:
	 * getLeaveByEmpIdAndTypeAndYear @Description: 根据员工id获得假期 @param
	 * empId @return 设定文件 List<EmpLeave> 返回类型 @throws
	 */
	public List<EmpLeave> getLeaveByEmpIdAndTypeAndYear(Long empId, Integer type, Integer year) {
		EmpLeave leave = new EmpLeave();
		leave.setEmployeeId(empId);
		leave.setType(type);
		leave.setYear(year);

		return empLeaveMapper.getListByCondition(leave);
	}

	@Override
	public int updateTimeByIds(EmpLeave empLeave) {
		return empLeaveMapper.updateTimeByIds(empLeave);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public void repairLeaveDatas(Long id) {

		EmployeeApp employee = null;
		if (null != id) {
			employee = new EmployeeApp();
			employee.setId(id);
		}
		List<EmployeeApp> eList = employeeAppService.getListByCondition(employee);

		EmpLeave empLeave = new EmpLeave();
		List<EmpLeave> leaveList = null;
		int listSize = 0;

		EmpLeave empLeave1 = null;// 第一个假期
		EmpLeave empLeave2 = null;// 第二个假期
		EmpLeave empLeave3 = null;// 第三个假期

		Double allowDays, actualDays, usedDays, blockedDays, allowRemainDays;

		Date now = null;
		for (EmployeeApp emp : eList) {

			id = emp.getId();
			empLeave.setEmployeeId(id);
			leaveList = empLeaveMapper.getRepairLeaveDatas(empLeave);
			listSize = leaveList.size();

			now = DateUtils.parse(DateUtils.getNow(), DateUtils.FORMAT_LONG);
			if (listSize == 2) {
				empLeave1 = leaveList.get(0);
				empLeave2 = leaveList.get(1);

				allowDays = empLeave1.getAllowDays() + empLeave2.getAllowDays();
				actualDays = empLeave1.getActualDays() + empLeave2.getActualDays();
				usedDays = empLeave1.getUsedDays() + empLeave2.getUsedDays();
				blockedDays = empLeave1.getBlockedDays() + empLeave2.getBlockedDays();
				allowRemainDays = empLeave1.getAllowRemainDays() + empLeave2.getAllowRemainDays();

				empLeave2.setAllowDays(allowDays);
				empLeave2.setActualDays(actualDays);
				empLeave2.setUsedDays(usedDays);
				empLeave2.setBlockedDays(blockedDays);
				empLeave2.setAllowRemainDays(allowRemainDays);
				empLeave2.setUpdateTime(now);

				empLeaveMapper.updateById(empLeave2);

				empLeave1.setUpdateTime(now);
				empLeave1.setDelFlag(1);/** 逻辑删除这条数据 **/
				empLeaveMapper.updateById(empLeave1);
			} else if (listSize == 3) {
				empLeave1 = leaveList.get(0);
				empLeave2 = leaveList.get(1);
				empLeave3 = leaveList.get(2);

				allowDays = empLeave1.getAllowDays() + empLeave2.getAllowDays() + empLeave3.getAllowDays();
				actualDays = empLeave1.getActualDays() + empLeave2.getActualDays() + empLeave3.getActualDays();
				usedDays = empLeave1.getUsedDays() + empLeave2.getUsedDays() + empLeave3.getUsedDays();
				blockedDays = empLeave1.getBlockedDays() + empLeave2.getBlockedDays() + empLeave3.getBlockedDays();
				allowRemainDays = empLeave1.getAllowRemainDays() + empLeave2.getAllowRemainDays()
						+ empLeave3.getAllowRemainDays();

				empLeave3.setAllowDays(allowDays);
				empLeave3.setActualDays(actualDays);
				empLeave3.setUsedDays(usedDays);
				empLeave3.setBlockedDays(blockedDays);
				empLeave3.setAllowRemainDays(allowRemainDays);
				empLeave3.setUpdateTime(now);

				empLeaveMapper.updateById(empLeave3);

				empLeave1.setUpdateTime(now);
				empLeave1.setDelFlag(1);/** 逻辑删除这条数据 **/
				empLeaveMapper.updateById(empLeave1);

				empLeave2.setUpdateTime(now);
				empLeave2.setDelFlag(1);/** 逻辑删除这条数据 **/
				empLeaveMapper.updateById(empLeave2);
			} else {
				/** 什么也不做 **/
			}

		}

	}

	/**
	 * getReportPageList(查询假期余额报表) @Title: getReportPageList @Description:
	 * 查询假期余额报表 @param empLeave @return 设定文件 PageModel<EmpLeave> 返回类型 @throws
	 */
	public PageModel<EmpLeave> getReportPageList(Employee emp) {
		int page = emp.getPage() == null ? 0 : emp.getPage();
		int rows = emp.getRows() == null ? 0 : emp.getRows();

		PageModel<EmpLeave> pm = new PageModel<EmpLeave>();
		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
		if(deptDataByUserList==null || deptDataByUserList.size()==0){
			pm.setPageNo(0);
			pm.setPageSize(0);
			pm.setTotal(0);
			emp.setOffset(pm.getOffset());
			emp.setLimit(pm.getLimit());
			pm.setRows(new ArrayList<EmpLeave>());
			return pm;
		}else{
			emp.setCurrentUserDepart(deptDataByUserList);//数据权限
			List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(currentUser.getEmployeeId());//下属员工
			if(subEmployeeIdList!=null&&subEmployeeIdList.size()>0){
				emp.setSubEmployeeIdList(subEmployeeIdList);
			}
			pm.setPageNo(page);
			pm.setPageSize(rows);
	
			// 封装部门
			Integer firstDepart = emp.getFirstDepart();// 页面上一级部门
			Integer secondDepart = emp.getSecondDepart();// 页面上二级部门
			List<Integer> departList = new ArrayList<Integer>();
			if (null != secondDepart) {// 选择了二级部门
				departList.add(secondDepart);
			} else if (null == secondDepart && null != firstDepart) {// 只选择了一级部门
				// 根据一级部门将一级部门下面的所有二级部门查询出来
				departList.add(firstDepart);
				List<Depart> departs = departService.getByParentId(Long.valueOf(firstDepart + ""));
				departs.forEach((Depart dp) -> {
					departList.add(Integer.parseInt(dp.getId() + ""));
				});
			}
			emp.setDepartList(departList);
			
			//查询指定员工类型数据
			emp.setEmpTypeIdList(configService.getNeedEmpTypeIdList());
			//emp.setWorkAddressType(0);
	
			Integer total = getReprtTotal(emp);
			pm.setTotal(total);
	
			emp.setOffset(pm.getOffset());
			emp.setLimit(pm.getLimit());
	
			List<EmpLeave> leaves = getReportList(emp);
	
			pm.setRows(leaves);
			return pm;
		}	
	}

	public Integer getReprtTotal(Employee emp) {
		return employeeMapper.getCount(emp);
	}

	public List<EmpLeave> getReportList(Employee emp) {
		List<EmpLeave> reportList = new ArrayList<EmpLeave>();

		// 1.根据页面上的条件将符合条件的员工给查询出来
		List<Employee> empList = employeeMapper.getPageList(emp);
		// 获得部门名称（一级部门+"_"+当前部门）
		employeeService.getDepartName(empList);

		// 2.遍历员工
		empList.forEach((Employee employee) -> {
			employee.setYear(emp.getYear());
			EmpLeave empLeave = new EmpLeave();
			empLeave.setEmployee(employee);

			// 2.1 封装去年和今年年假
			setYearReport(employee, empLeave);

			// 2.2 封装病假
			setSickReprot(employee, empLeave);

			// 2.3 封装调休
			setRestReport(employee, empLeave);

			// 2.4 封装除年假、病假和调休以外的假期
			setOtherReprt(employee, empLeave);

			// 2.5 将所有的假期信息封装到一起
			reportList.add(empLeave);
		});

		return reportList;
	}

	public List<EmpLeave> getReportListNew(Employee emp) {
		List<EmpLeave> reportList = new ArrayList<EmpLeave>();

		// 1.根据页面上的条件将符合条件的员工给查询出来
		List<Employee> empList = employeeMapper.getPageList(emp);
		// 获得部门名称（一级部门+"_"+当前部门）
		employeeService.getDepartName(empList);

		// 2.遍历员工
		empList.forEach((Employee employee) -> {
			employee.setYear(emp.getYear());
			EmpLeave empLeave = new EmpLeave();
			empLeave.setEmployee(employee);

			// 2.1 封装去年和今年年假
			setYearReportNew(employee, empLeave);

			// 2.2 封装病假
			setSickReprotNew(employee, empLeave);

			// 2.3 封装调休
			setRestReportNew(employee, empLeave);

			// 2.4 封装除年假、病假和调休以外的假期
			setOtherReprtNew(employee, empLeave);

			// 2.5 将所有的假期信息封装到一起
			reportList.add(empLeave);
		});

		return reportList;
	}

	/**
	 * setOtherReprt(假期余额报表--封装除年假、病假和调休以外的假期) @Title:
	 * setOtherReprt @Description: 假期余额报表--封装除年假、病假和调休以外的假期 @param
	 * employee @param empLeave 设定文件 void 返回类型 @throws
	 */
	public void setOtherReprt(Employee employee, EmpLeave empLeave) {
		List<Map<String, Object>> leaveList = getOtherLeave(employee);

		Integer leaveType = null;
		Double leaveDays = 0.0;
		for (Map<String, Object> leaveMap : leaveList) {
			leaveType = Integer.parseInt(leaveMap.get("leaveType") + "");
			leaveDays = Double.parseDouble(leaveMap.get("leaveDays") + "");

			if (leaveType.equals(ConfigConstants.LEAVE_TYPE_3)) {// 婚假
				empLeave.setLeave21(leaveDays);
			} else if (leaveType.equals(ConfigConstants.LEAVE_TYPE_4)) {// 哺乳假
				empLeave.setLeave26(leaveDays);
			} else if (leaveType.equals(ConfigConstants.LEAVE_TYPE_6)) {// 产前假
				empLeave.setLeave24(leaveDays);
			} else if (leaveType.equals(ConfigConstants.LEAVE_TYPE_7)) {// 产假
				empLeave.setLeave25(leaveDays);
			} else if (leaveType.equals(ConfigConstants.LEAVE_TYPE_8)) {// 流产假
				empLeave.setLeave27(leaveDays);
			} else if (leaveType.equals(ConfigConstants.LEAVE_TYPE_9)) {// 陪产假
				empLeave.setLeave23(leaveDays);
			} else if (leaveType.equals(ConfigConstants.LEAVE_TYPE_10)) {// 丧假
				empLeave.setLeave22(leaveDays);
			} else if (leaveType.equals(ConfigConstants.LEAVE_TYPE_11)) {// 事假
				empLeave.setLeave19(leaveDays);
			} else if (leaveType.equals(ConfigConstants.LEAVE_TYPE_12)) {// 其它
				empLeave.setLeave28(leaveDays);
			}
		}
	}

	/**
	 * setOtherReprt(假期余额报表--封装除年假、病假和调休以外的假期) @Title:
	 * setOtherReprt @Description: 假期余额报表--封装除年假、病假和调休以外的假期 @param
	 * employee @param empLeave 设定文件 void 返回类型 @throws
	 */
	public void setOtherReprtNew(Employee employee, EmpLeave empLeave) {
		// 统计流水查询已用
		LeaveRecord leaveRecord = new LeaveRecord();
		leaveRecord.setYear(employee.getYear());
		List<Integer> types = new ArrayList<Integer>();
		types.add(ConfigConstants.LEAVE_TYPE_3);
		types.add(ConfigConstants.LEAVE_TYPE_4);
		types.add(ConfigConstants.LEAVE_TYPE_6);
		types.add(ConfigConstants.LEAVE_TYPE_7);
		types.add(ConfigConstants.LEAVE_TYPE_8);
		types.add(ConfigConstants.LEAVE_TYPE_9);
		types.add(ConfigConstants.LEAVE_TYPE_10);
		types.add(ConfigConstants.LEAVE_TYPE_11);
		types.add(ConfigConstants.LEAVE_TYPE_12);
		leaveRecord.setTypes(types);
		leaveRecord.setEmployeeId(employee.getId());

		List<LeaveRecord> leaveRecordList = leaveRecordMapper.getUsedDaysByYearAndTypes(leaveRecord);

		Integer leaveType = null;
		Double leaveDays_3 = 0.0;
		Double leaveDays_4 = 0.0;
		Double leaveDays_6 = 0.0;
		Double leaveDays_7 = 0.0;
		Double leaveDays_8 = 0.0;
		Double leaveDays_9 = 0.0;
		Double leaveDays_10 = 0.0;
		Double leaveDays_11 = 0.0;
		Double leaveDays_12 = 0.0;
		for (LeaveRecord data : leaveRecordList) {
			leaveType = data.getType();
			if (leaveType.equals(ConfigConstants.LEAVE_TYPE_3)) {// 婚假
				if (ConfigConstants.LEAVE_KEY.equals(data.getBillType())) {
					leaveDays_3 = leaveDays_3 + data.getDays();
				}
				if (ConfigConstants.CANCELLEAVE_KEY.equals(data.getBillType())) {
					leaveDays_3 = leaveDays_3 - data.getDays();
				}
			} else if (leaveType.equals(ConfigConstants.LEAVE_TYPE_4)) {// 哺乳假
				if (ConfigConstants.LEAVE_KEY.equals(data.getBillType())) {
					leaveDays_4 = leaveDays_4 + data.getDays();
				}
				if (ConfigConstants.CANCELLEAVE_KEY.equals(data.getBillType())) {
					leaveDays_4 = leaveDays_4 - data.getDays();
				}
			} else if (leaveType.equals(ConfigConstants.LEAVE_TYPE_6)) {// 产前假
				if (ConfigConstants.LEAVE_KEY.equals(data.getBillType())) {
					leaveDays_6 = leaveDays_6 + data.getDays();
				}
				if (ConfigConstants.CANCELLEAVE_KEY.equals(data.getBillType())) {
					leaveDays_6 = leaveDays_6 - data.getDays();
				}
			} else if (leaveType.equals(ConfigConstants.LEAVE_TYPE_7)) {// 产假
				if (ConfigConstants.LEAVE_KEY.equals(data.getBillType())) {
					leaveDays_7 = leaveDays_7 + data.getDays();
				}
				if (ConfigConstants.CANCELLEAVE_KEY.equals(data.getBillType())) {
					leaveDays_7 = leaveDays_7 - data.getDays();
				}
			} else if (leaveType.equals(ConfigConstants.LEAVE_TYPE_8)) {// 流产假
				if (ConfigConstants.LEAVE_KEY.equals(data.getBillType())) {
					leaveDays_8 = leaveDays_8 + data.getDays();
				}
				if (ConfigConstants.CANCELLEAVE_KEY.equals(data.getBillType())) {
					leaveDays_8 = leaveDays_8 - data.getDays();
				}
			} else if (leaveType.equals(ConfigConstants.LEAVE_TYPE_9)) {// 陪产假
				if (ConfigConstants.LEAVE_KEY.equals(data.getBillType())) {
					leaveDays_9 = leaveDays_9 + data.getDays();
				}
				if (ConfigConstants.CANCELLEAVE_KEY.equals(data.getBillType())) {
					leaveDays_9 = leaveDays_9 - data.getDays();
				}
			} else if (leaveType.equals(ConfigConstants.LEAVE_TYPE_10)) {// 丧假
				if (ConfigConstants.LEAVE_KEY.equals(data.getBillType())) {
					leaveDays_10 = leaveDays_10 + data.getDays();
				}
				if (ConfigConstants.CANCELLEAVE_KEY.equals(data.getBillType())) {
					leaveDays_10 = leaveDays_10 - data.getDays();
				}
			} else if (leaveType.equals(ConfigConstants.LEAVE_TYPE_11)) {// 事假
				if (ConfigConstants.LEAVE_KEY.equals(data.getBillType())) {
					leaveDays_11 = leaveDays_11 + data.getDays();
				}
				if (ConfigConstants.CANCELLEAVE_KEY.equals(data.getBillType())) {
					leaveDays_11 = leaveDays_11 - data.getDays();
				}
			} else if (leaveType.equals(ConfigConstants.LEAVE_TYPE_12)) {// 其它
				if (ConfigConstants.LEAVE_KEY.equals(data.getBillType())) {
					leaveDays_12 = leaveDays_12 + data.getDays();
				}
				if (ConfigConstants.CANCELLEAVE_KEY.equals(data.getBillType())) {
					leaveDays_12 = leaveDays_12 - data.getDays();
				}
			}
		}
		empLeave.setLeave21(leaveDays_3);
		empLeave.setLeave26(leaveDays_4);
		empLeave.setLeave24(leaveDays_6);
		empLeave.setLeave25(leaveDays_7);
		empLeave.setLeave27(leaveDays_8);
		empLeave.setLeave23(leaveDays_9);
		empLeave.setLeave22(leaveDays_10);
		empLeave.setLeave19(leaveDays_11);
		empLeave.setLeave28(leaveDays_12);
	}

	public List<Map<String, Object>> getOtherLeave(Employee employee) {
		EmpLeave empLeave = new EmpLeave();
		empLeave.setEmployeeId(employee.getId());
		empLeave.setYear(employee.getYear());// Integer.valueOf(DateUtils.getYear(new
												// Date()))
		List<EmpLeave> empLeaveList = empLeaveMapper.getListByCondition(empLeave);
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		for (EmpLeave data : empLeaveList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("leaveType", data.getType());
			map.put("leaveDays", data.getUsedDays());
			result.add(map);
		}
		// EmpApplicationLeave empApplicationLeave = new EmpApplicationLeave();
		// empApplicationLeave.setEmployeeId(employee.getId());
		// empApplicationLeave.setStartTime(DateUtils.parse(DateUtils.getYear(new
		// Date()) + "-01-01 00:00:00"));
		// empApplicationLeave.setEndTime(DateUtils.parse(DateUtils.getYear(new
		// Date()) + "-12-31 23:59:59"));
		//
		// return
		// empApplicationLeaveDetailMapper.getOtherReprt(empApplicationLeave);
		return result;
	}

	/**
	 * setRestReport(假期余额报表--封装调休相关假期<已用根据流水统计>) @Title:
	 * setRestReport @Description: 假期余额报表--封装调休相关假期 @param employee @param
	 * empLeave 设定文件 void 返回类型 @throws
	 */
	public void setRestReportNew(Employee employee, EmpLeave empLeave) {
		Integer currYear = employee.getYear();// Integer.parseInt(DateUtils.getYear(new
												// Date()));//当年
		List<EmpLeave> currYearLeaves = getRestLeaveByType(employee, currYear);// 当年调休数据

		// 统计流水查询已用
		LeaveRecord leaveRecord = new LeaveRecord();
		leaveRecord.setType(ConfigConstants.LEAVE_TYPE_5);
		leaveRecord.setYear(currYear);
		leaveRecord.setEmployeeId(employee.getId());

		List<LeaveRecord> leaveRecordList_C = leaveRecordMapper.getUsedDaysByYear(leaveRecord);

		Double usedDays_C = 0.0;// 今年已用

		for (LeaveRecord data : leaveRecordList_C) {
			if (data.getCategory().intValue() == ConfigConstants.CATEGORY_1.intValue()
					&& data.getBillType().equals(ConfigConstants.LEAVE_KEY)) {
				usedDays_C = usedDays_C + data.getDays();
			}
			if (data.getCategory().intValue() == ConfigConstants.CATEGORY_1.intValue()
					&& data.getBillType().equals(ConfigConstants.CANCELLEAVE_KEY)) {
				usedDays_C = usedDays_C - data.getDays();
			}
		}

		leaveRecord.setYear(currYear - 1);

		List<LeaveRecord> leaveRecordList_L = leaveRecordMapper.getUsedDaysByYear(leaveRecord);

		Double usedDays_L = 0.0;// 今年已用

		for (LeaveRecord data : leaveRecordList_L) {
			if (data.getCategory().intValue() == ConfigConstants.CATEGORY_1.intValue()
					&& data.getBillType().equals(ConfigConstants.LEAVE_KEY)) {
				usedDays_L = usedDays_L + data.getDays();
			}
			if (data.getCategory().intValue() == ConfigConstants.CATEGORY_1.intValue()
					&& data.getBillType().equals(ConfigConstants.CANCELLEAVE_KEY)) {
				usedDays_L = usedDays_L - data.getDays();
			}
		}

		List<EmpLeave> preYearLeaves = getRestLeaveByType(employee, currYear - 1);// 去年调休数据
		Double totalRemain = 0.0;// 剩余假期天数-调休小时数(去年+当年)
		Double totalUsed = usedDays_C;// 当年已使用假期天数-调休小时数
		Double preTotalRemain = 0.0;// 去年剩余调休小时数
		Double preTotalUsed = usedDays_L;// 去年已用调休小时数

		for (EmpLeave leave : currYearLeaves) {// 当年假期
			totalRemain += leave.getAllowRemainDays();// 允许休假天数
		}
		for (int i = 0; i < preYearLeaves.size(); i++) {
			preTotalRemain += preYearLeaves.get(i).getAllowRemainDays();
			// 去年 调休假 跟现在时间做对比 如果有 大于 则增加调休数 否则 去年 调休失效
			// a.after(b)返回一个boolean，如果a的时间在b之后（不包括等于）返回true
			// a.equals(b)返回一个boolean,如果a的时间和b相等返回true
			if (preYearLeaves.get(i).getEndTime().after(new Date())
					|| preYearLeaves.get(i).getEndTime().equals(new Date())) {
				totalRemain += preYearLeaves.get(i).getAllowRemainDays();
				// 允许休假天数+占用天数
			}
		}
		empLeave.setLeave36(preTotalRemain);// 去年剩余调休小时数
		empLeave.setLeave37(preTotalUsed);// 去年已用调休小时数
		empLeave.setLeave11(totalRemain);// 剩余假期天数-调休小时数
		empLeave.setLeave20(totalUsed);// 当年已使用假期天数-调休小时数
	}

	/**
	 * setRestReport(假期余额报表--封装调休相关假期) @Title: setRestReport @Description:
	 * 假期余额报表--封装调休相关假期 @param employee @param empLeave 设定文件 void 返回类型 @throws
	 */
	public void setRestReport(Employee employee, EmpLeave empLeave) {
		Integer currYear = employee.getYear();// Integer.parseInt(DateUtils.getYear(new
												// Date()));//当年
		List<EmpLeave> currYearLeaves = getRestLeaveByType(employee, currYear);// 当年调休数据
		List<EmpLeave> preYearLeaves = getRestLeaveByType(employee, currYear - 1);// 去年调休数据
		Double totalRemain = 0.0;// 剩余假期天数-调休小时数(去年+当年)
		Double totalUsed = 0.0;// 当年已使用假期天数-调休小时数
		Double preTotalRemain = 0.0;// 去年剩余调休小时数
		Double preTotalUsed = 0.0;// 去年已用调休小时数

		for (EmpLeave leave : currYearLeaves) {// 当年假期
			totalRemain += leave.getAllowRemainDays();// 允许休假天数
			totalUsed += leave.getUsedDays();
		}
		for (int i = 0; i < preYearLeaves.size(); i++) {
			preTotalRemain += preYearLeaves.get(i).getAllowRemainDays();
			preTotalUsed += preYearLeaves.get(i).getUsedDays();
			// 去年 调休假 跟现在时间做对比 如果有 大于 则增加调休数 否则 去年 调休失效
			// a.after(b)返回一个boolean，如果a的时间在b之后（不包括等于）返回true
			// a.equals(b)返回一个boolean,如果a的时间和b相等返回true
		}
		empLeave.setLeave36(preTotalRemain);// 去年剩余调休小时数
		empLeave.setLeave37(preTotalUsed);// 去年已用调休小时数
		empLeave.setLeave11(totalRemain);// 剩余假期天数-调休小时数
		empLeave.setLeave20(totalUsed);// 当年已使用假期天数-调休小时数
	}

	/**
	 * getRestLeaveByType(假期余额报表--查询调休相关假期) @Title:
	 * getRestLeaveByType @Description: 假期余额报表--查询调休相关假期 @param employee @return
	 * 设定文件 List<EmpLeave> 返回类型 @throws
	 */
	public List<EmpLeave> getRestLeaveByType(Employee employee, Integer year) {
		List<Integer> yearList = new ArrayList<Integer>();
		yearList.add(year);

		EmpLeave leave = new EmpLeave();
		leave.setType(ConfigConstants.LEAVE_TYPE_5);// 假期类型
		leave.setIsActive(ConfigConstants.IS_ACTIVE_INTEGER);// 有效的假期
		leave.setDelFlag(ConfigConstants.IS_NO_INTEGER);// 未删除
		leave.setEmployeeId(employee.getId());
		leave.setYearList(yearList);

		return empLeaveMapper.getListByCondition(leave);
	}

	/**
	 * setSickReprot(假期余额报表--封装病假相关假期) @Title: setSickReprot @Description:
	 * 假期余额报表--封装病假相关假期 @param employee @param empLeave 设定文件 void 返回类型 @throws
	 */
	public void setSickReprot(Employee employee, EmpLeave empLeave) {
		List<EmpLeave> yearLeaves = getSickLeaveByType(employee);

		EmpLeave leave = new EmpLeave();
		if (yearLeaves.size() > 0) {
			leave = yearLeaves.get(0);// 带薪假 第一条1条记录
		} else {
			return;
		}
		EmpLeave unLeave = null;
		if (yearLeaves.size() > 1) {
			unLeave = yearLeaves.get(1);// 非带薪假 第二条记录
		}

		Double sickSalaryUsed = leave.getAllowRemainDays() > 0 ? leave.getAllowDays() - leave.getAllowRemainDays()
				: leave.getAllowDays();
		// Double sickOverUsed = sickSalaryUsed - leave.getActualDays() >=0 ?
		// sickSalaryUsed - leave.getActualDays() : 0 ;
		Double reductionDays = leave.getActualDays() - leave.getUsedDays();// 剩余带薪

		empLeave.setLeave4(leave.getAllowDays());// 法定年假总数-带薪病假
		empLeave.setLeave10(leave.getAllowRemainDays());// 剩余假期天数-带薪病假
		empLeave.setLeave17(leave.getUsedDays());// 当年已使用假期天数-带薪病假

		empLeave.setLeave18(unLeave != null ? unLeave.getUsedDays() : 0);// 当年已使用假期天数-非带薪病假
		// 透支假期天数-带薪病假 已用- 允许可用 如果 大于 0 就透支 否则就 是 0
		empLeave.setLeave30(
				(leave.getUsedDays() - leave.getActualDays()) > 0 ? leave.getUsedDays() - leave.getActualDays() : 0);

		// 截止目前剩余假期天数-带薪病假 实际可用-已用>0 则 实际可用-已用,否则 0
		empLeave.setLeave31((reductionDays == null || reductionDays < 0) ? 0 : reductionDays);

	}

	/**
	 * setSickReprot(假期余额报表--封装病假相关假期<已用根据流水来统计>) @Title:
	 * setSickReprot @Description: 假期余额报表--封装病假相关假期 @param employee @param
	 * empLeave 设定文件 void 返回类型 @throws
	 */
	public void setSickReprotNew(Employee employee, EmpLeave empLeave) {
		List<EmpLeave> yearLeaves = getSickLeaveByType(employee);

		// 统计流水查询已用
		LeaveRecord leaveRecord = new LeaveRecord();
		leaveRecord.setType(ConfigConstants.LEAVE_TYPE_2);
		leaveRecord.setYear(employee.getYear());
		leaveRecord.setEmployeeId(employee.getId());

		List<LeaveRecord> leaveRecordList = leaveRecordMapper.getUsedDaysByYear(leaveRecord);

		Double usedDays_0 = 0.0;// 带薪
		Double usedDays_2 = 0.0;// 非带薪

		for (LeaveRecord data : leaveRecordList) {
			if (data.getCategory().intValue() == ConfigConstants.CATEGORY_0.intValue()
					&& data.getBillType().equals(ConfigConstants.LEAVE_KEY)) {
				usedDays_0 = usedDays_0 + data.getDays();
			}
			if (data.getCategory().intValue() == ConfigConstants.CATEGORY_0.intValue()
					&& data.getBillType().equals(ConfigConstants.CANCELLEAVE_KEY)) {
				usedDays_0 = usedDays_0 - data.getDays();
			}
			if (data.getCategory().intValue() == ConfigConstants.CATEGORY_2.intValue()
					&& data.getBillType().equals(ConfigConstants.LEAVE_KEY)) {
				usedDays_2 = usedDays_2 + data.getDays();
			}
			if (data.getCategory().intValue() == ConfigConstants.CATEGORY_2.intValue()
					&& data.getBillType().equals(ConfigConstants.CANCELLEAVE_KEY)) {
				usedDays_2 = usedDays_2 - data.getDays();
			}
		}

		EmpLeave leave = new EmpLeave();
		if (yearLeaves.size() > 0) {
			leave = yearLeaves.get(0);// 带薪假 第一条1条记录
			leave.setUsedDays(usedDays_0);
		} else {
			return;
		}
		EmpLeave unLeave = null;
		if (yearLeaves.size() > 1) {
			unLeave = yearLeaves.get(1);// 非带薪假 第二条记录
			unLeave.setUsedDays(usedDays_2);
		}

		Double sickSalaryUsed = leave.getAllowRemainDays() > 0 ? leave.getAllowDays() - leave.getAllowRemainDays()
				: leave.getAllowDays();
		// Double sickOverUsed = sickSalaryUsed - leave.getActualDays() >=0 ?
		// sickSalaryUsed - leave.getActualDays() : 0 ;
		Double reductionDays = leave.getActualDays() - leave.getUsedDays();// 剩余带薪

		empLeave.setLeave4(leave.getAllowDays());// 法定年假总数-带薪病假
		empLeave.setLeave10(leave.getAllowRemainDays());// 剩余假期天数-带薪病假
		empLeave.setLeave17(leave.getUsedDays());// 当年已使用假期天数-带薪病假

		empLeave.setLeave18(unLeave != null ? unLeave.getUsedDays() : 0);// 当年已使用假期天数-非带薪病假
		// 透支假期天数-带薪病假 已用- 允许可用 如果 大于 0 就透支 否则就 是 0
		empLeave.setLeave30(
				(leave.getUsedDays() - leave.getActualDays()) > 0 ? leave.getUsedDays() - leave.getActualDays() : 0);

		// 截止目前剩余假期天数-带薪病假 实际可用-已用>0 则 实际可用-已用,否则 0
		empLeave.setLeave31((reductionDays == null || reductionDays < 0) ? 0 : reductionDays);

	}

	/**
	 * getSickLeaveByType(假期余额报表--查询病假相关假期) @Title:
	 * getSickLeaveByType @Description: 假期余额报表--查询病假相关假期 @param employee @return
	 * 设定文件 List<EmpLeave> 返回类型 @throws
	 */
	public List<EmpLeave> getSickLeaveByType(Employee employee) {
		Integer currYear = employee.getYear();// Integer.parseInt(DateUtils.getYear(new
												// Date()));//当年
		List<Integer> yearList = new ArrayList<Integer>();
		yearList.add(currYear);
		EmpLeave leave = new EmpLeave();
		leave.setType(ConfigConstants.LEAVE_TYPE_2);// 假期类型
		leave.setIsActive(ConfigConstants.IS_ACTIVE_INTEGER);// 有效的假期
		leave.setDelFlag(ConfigConstants.IS_NO_INTEGER);// 未删除
		leave.setEmployeeId(employee.getId());
		leave.setYearList(yearList);

		return empLeaveMapper.getListByCondition(leave);
	}

	/**
	 * setYearReport(假期余额报表--封装年假相关假期) @Title: setYearReport @Description:
	 * 假期余额报表--封装年假相关假期 @param employee @param empLeave 设定文件 void 返回类型 @throws
	 */
	public void setYearReport(Employee employee, EmpLeave empLeave) {
		List<EmpLeave> yearLeaves = getYearLeaveByType(employee);

		Integer currYear = employee.getYear();// Integer.parseInt(DateUtils.getYear(new
												// Date()));//当年
		Double totalReamin = 0.0;// 剩余假期天数-年假=去年法定剩余 + 当年法定剩余 + 去年福利剩余 + 当年福利剩余
		Double currentLegalRemain = 0.0;
		Double currentwelfareRemain = 0.0;
		Double curWelfareYearAllowDays = 0.0;// 当年福利允许
		Double curWelfareYearRemain = 0.0;// 当年福利剩余

		empLeave.setLeave5(0d);// 剩余假期天数-去年法定年假
		empLeave.setLeave32(0d);// 去年年假总天数-法定
		empLeave.setLeave34(0d);// 去年已用年假天数-法定
		empLeave.setLeave6(0d);// 剩余假期天数-去年福利年假
		empLeave.setLeave33(0d);// 去年年假总天数-福利
		empLeave.setLeave35(0d);// 去年已用年假天数-福利

		for (EmpLeave leave : yearLeaves) {
			Integer year = leave.getYear();
			Integer category = leave.getCategory();

			if (year.equals(currYear)) {// 当年
				if (category.equals(ConfigConstants.CATEGORY_0)) {// 总的年假
					empLeave.setLeave1(leave.getAllowDays());// 当年假期总数-年假
					empLeave.setLeave16(leave.getUsedDays());// 当年已使用假期天数-年假
					empLeave.setLeave29(leave.getActualDays() - leave.getUsedDays() > 0 ? 0.0
							: leave.getUsedDays() - leave.getActualDays());// 透支假期天数-年假
				} else if (category.equals(ConfigConstants.CATEGORY_1)) {// 法定年假
					empLeave.setLeave2(leave.getAllowDays());// 当年假期总数-法定年假
					empLeave.setLeave7(leave.getAllowRemainDays() + leave.getBlockedDays());// 剩余假期天数-当年法定年假
					currentLegalRemain = leave.getActualDays() - leave.getUsedDays();// 截止目前法定剩余（可能为负数）
					empLeave.setLeave12(currentLegalRemain > 0 ? currentLegalRemain : 0.0);// 截止目前剩余假期天数-当年法定年假
					empLeave.setLeave14(leave.getUsedDays());// 当年已使用假期天数-法定年假

					totalReamin += empLeave.getLeave7();
				} else if (category.equals(ConfigConstants.CATEGORY_2)) {// 福利年假
					curWelfareYearAllowDays = curWelfareYearAllowDays + leave.getAllowDays();
					curWelfareYearRemain = curWelfareYearRemain + leave.getAllowRemainDays() + leave.getBlockedDays();
					currentwelfareRemain = leave.getActualDays() - leave.getUsedDays() > 0
							? leave.getActualDays() - leave.getUsedDays() : 0.0;
					empLeave.setLeave15(leave.getUsedDays());// 当年已使用假期天数-福利年假

					totalReamin += empLeave.getLeave8();
				}
			} else {// 去年
				if (category.equals(ConfigConstants.CATEGORY_0)) {// 总的年假

				} else if (category.equals(ConfigConstants.CATEGORY_1)) {// 法定年假
					empLeave.setLeave5((leave.getAllowRemainDays() + leave.getBlockedDays()) > 0
							? (leave.getAllowRemainDays() + leave.getBlockedDays()) : 0);// 剩余假期天数-去年法定年假
					empLeave.setLeave32(leave.getAllowDays());// 去年年假总天数-法定
					empLeave.setLeave34(leave.getUsedDays());// 去年已用年假天数-法定
					totalReamin += DateUtils.isBefore(new Date(), leave.getEndTime()) ? empLeave.getLeave5() : 0.0;
				} else if (category.equals(ConfigConstants.CATEGORY_2)) {// 福利年假
					empLeave.setLeave6((leave.getAllowRemainDays() + leave.getBlockedDays()) > 0
							? (leave.getAllowRemainDays() + leave.getBlockedDays()) : 0);// 剩余假期天数-去年福利年假
					empLeave.setLeave33(leave.getAllowDays());// 去年年假总天数-福利
					empLeave.setLeave35(leave.getUsedDays());// 去年已用年假天数-福利
					totalReamin += DateUtils.isBefore(new Date(), leave.getEndTime()) ? empLeave.getLeave6() : 0.0;
				}
			}
		}
		if (currentLegalRemain < 0) {
			currentwelfareRemain = currentwelfareRemain + currentLegalRemain;
		}
		empLeave.setLeave3(curWelfareYearAllowDays);
		empLeave.setLeave8(curWelfareYearRemain);// 剩余假期天数-当年福利年假
		empLeave.setLeave13(currentwelfareRemain > 0 ? currentwelfareRemain : 0);// 截止目前剩余假期天数-当年福利年假
		empLeave.setLeave9(totalReamin);// 剩余假期天数-年假
	}

	/**
	 * setYearReport(假期余额报表--封装年假相关假期<已用根据流水来统计>) @Title:
	 * setYearReport @Description: 假期余额报表--封装年假相关假期 @param employee @param
	 * empLeave 设定文件 void 返回类型 @throws
	 */
	public void setYearReportNew(Employee employee, EmpLeave empLeave) {
		List<EmpLeave> yearLeaves = getYearLeaveByType(employee);

		Integer currYear = employee.getYear();// Integer.parseInt(DateUtils.getYear(new
												// Date()));//当年
		// 统计流水查询已用
		LeaveRecord leaveRecord = new LeaveRecord();
		leaveRecord.setType(ConfigConstants.LEAVE_TYPE_1);
		List<Integer> years = new ArrayList<Integer>();
		years.add(currYear);
		years.add(currYear - 1);
		leaveRecord.setYears(years);
		leaveRecord.setEmployeeId(employee.getId());

		List<LeaveRecord> leaveRecordList = leaveRecordMapper.getUsedDaysByYears(leaveRecord);

		Double usedDays_C = 0.0;// 总的已用
		Double usedDays_C_L = 0.0;// 法定已用
		Double usedDays_C_W = 0.0;// 福利已用
		Double usedDays_L = 0.0;// 总的已用
		Double usedDays_L_L = 0.0;// 法定已用
		Double usedDays_L_W = 0.0;// 福利已用

		for (LeaveRecord data : leaveRecordList) {
			if (data.getYear().intValue() == currYear
					&& data.getCategory().intValue() == ConfigConstants.CATEGORY_1.intValue()
					&& data.getBillType().equals(ConfigConstants.LEAVE_KEY)) {
				usedDays_C_L = usedDays_C_L + data.getDays();
			}
			if (data.getYear().intValue() == currYear
					&& data.getCategory().intValue() == ConfigConstants.CATEGORY_1.intValue()
					&& data.getBillType().equals(ConfigConstants.CANCELLEAVE_KEY)) {
				usedDays_C_L = usedDays_C_L - data.getDays();
			}
			if (data.getYear().intValue() == currYear
					&& data.getCategory().intValue() == ConfigConstants.CATEGORY_2.intValue()
					&& data.getBillType().equals(ConfigConstants.LEAVE_KEY)) {
				usedDays_C_W = usedDays_C_W + data.getDays();
			}
			if (data.getYear().intValue() == currYear
					&& data.getCategory().intValue() == ConfigConstants.CATEGORY_2.intValue()
					&& data.getBillType().equals(ConfigConstants.CANCELLEAVE_KEY)) {
				usedDays_C_W = usedDays_C_W - data.getDays();
			}

			if (data.getYear().intValue() == currYear - 1
					&& data.getCategory().intValue() == ConfigConstants.CATEGORY_1.intValue()
					&& data.getBillType().equals(ConfigConstants.LEAVE_KEY)) {
				usedDays_L_L = usedDays_L_L + data.getDays();
			}
			if (data.getYear().intValue() == currYear - 1
					&& data.getCategory().intValue() == ConfigConstants.CATEGORY_1.intValue()
					&& data.getBillType().equals(ConfigConstants.CANCELLEAVE_KEY)) {
				usedDays_L_L = usedDays_L_L - data.getDays();
			}
			if (data.getYear().intValue() == currYear - 1
					&& data.getCategory().intValue() == ConfigConstants.CATEGORY_2.intValue()
					&& data.getBillType().equals(ConfigConstants.LEAVE_KEY)) {
				usedDays_L_W = usedDays_L_W + data.getDays();
			}
			if (data.getYear().intValue() == currYear - 1
					&& data.getCategory().intValue() == ConfigConstants.CATEGORY_2.intValue()
					&& data.getBillType().equals(ConfigConstants.CANCELLEAVE_KEY)) {
				usedDays_L_W = usedDays_L_W - data.getDays();
			}
		}

		usedDays_C = usedDays_C_L + usedDays_C_W;
		usedDays_L = usedDays_L_L + usedDays_L_W;

		Double totalReamin = 0.0;// 剩余假期天数-年假=去年法定剩余 + 当年法定剩余 + 去年福利剩余 + 当年福利剩余
		Double currentLegalRemain = 0.0;
		Double currentwelfareRemain = 0.0;
		Double curWelfareYearAllowDays = 0.0;// 当年福利允许
		Double curWelfareYearRemain = 0.0;// 当年福利剩余
		for (EmpLeave leave : yearLeaves) {
			Integer year = leave.getYear();
			Integer category = leave.getCategory();

			if (year.equals(currYear)) {// 当年
				if (category.equals(ConfigConstants.CATEGORY_0)) {// 总的年假
					leave.setUsedDays(usedDays_C);
					empLeave.setLeave1(leave.getAllowDays());// 当年假期总数-年假
					empLeave.setLeave16(leave.getUsedDays());// 当年已使用假期天数-年假
					empLeave.setLeave29(leave.getActualDays() - leave.getUsedDays() > 0 ? 0.0
							: leave.getUsedDays() - leave.getActualDays());// 透支假期天数-年假
				} else if (category.equals(ConfigConstants.CATEGORY_1)) {// 法定年假
					leave.setUsedDays(usedDays_C_L);
					empLeave.setLeave2(leave.getAllowDays());// 当年假期总数-法定年假
					empLeave.setLeave7(leave.getAllowRemainDays() + leave.getBlockedDays());// 剩余假期天数-当年法定年假
					currentLegalRemain = leave.getActualDays() - leave.getUsedDays();// 截止目前法定剩余（可能为负数）
					empLeave.setLeave12(currentLegalRemain > 0 ? currentLegalRemain : 0.0);// 截止目前剩余假期天数-当年法定年假
					empLeave.setLeave14(leave.getUsedDays());// 当年已使用假期天数-法定年假

					totalReamin += empLeave.getLeave7();
				} else if (category.equals(ConfigConstants.CATEGORY_2)) {// 福利年假
					leave.setUsedDays(usedDays_C_W);
					curWelfareYearAllowDays = curWelfareYearAllowDays + leave.getAllowDays();
					curWelfareYearRemain = curWelfareYearRemain + leave.getAllowRemainDays() + leave.getBlockedDays();
					currentwelfareRemain = leave.getActualDays() - leave.getUsedDays() > 0
							? leave.getActualDays() - leave.getUsedDays() : 0.0;
					empLeave.setLeave15(leave.getUsedDays());// 当年已使用假期天数-福利年假

					totalReamin += empLeave.getLeave8();
				}
			} else {// 去年
				if (category.equals(ConfigConstants.CATEGORY_0)) {// 总的年假

				} else if (category.equals(ConfigConstants.CATEGORY_1)) {// 法定年假
					leave.setUsedDays(usedDays_L_L);
					empLeave.setLeave5((leave.getAllowRemainDays() + leave.getBlockedDays()) > 0
							? (leave.getAllowRemainDays() + leave.getBlockedDays()) : 0);// 剩余假期天数-去年法定年假
					empLeave.setLeave32(leave.getAllowDays());// 去年年假总天数-法定
					empLeave.setLeave34(leave.getUsedDays());// 去年已用年假天数-法定
					totalReamin += DateUtils.isBefore(new Date(), leave.getEndTime()) ? empLeave.getLeave5() : 0.0;
				} else if (category.equals(ConfigConstants.CATEGORY_2)) {// 福利年假
					leave.setUsedDays(usedDays_L_W);
					empLeave.setLeave6((leave.getAllowRemainDays() + leave.getBlockedDays()) > 0
							? (leave.getAllowRemainDays() + leave.getBlockedDays()) : 0);// 剩余假期天数-去年福利年假
					empLeave.setLeave33(leave.getAllowDays());// 去年年假总天数-福利
					empLeave.setLeave35(leave.getUsedDays());// 去年已用年假天数-福利
					totalReamin += DateUtils.isBefore(new Date(), leave.getEndTime()) ? empLeave.getLeave6() : 0.0;
				}
			}
		}
		if (currentLegalRemain < 0) {
			currentwelfareRemain = currentwelfareRemain + currentLegalRemain;
		}
		empLeave.setLeave3(curWelfareYearAllowDays);
		empLeave.setLeave8(curWelfareYearRemain);// 剩余假期天数-当年福利年假
		empLeave.setLeave13(currentwelfareRemain > 0 ? currentwelfareRemain : 0);// 截止目前剩余假期天数-当年福利年假
		empLeave.setLeave9(totalReamin);// 剩余假期天数-年假
	}

	/**
	 * getYearLeaveByType(假期余额报表--查询年假相关假期) @Title:
	 * getYearLeaveByType @Description: 假期余额报表--查询年假相关假期 @param employee @return
	 * 设定文件 List<EmpLeave> 返回类型 @throws
	 */
	public List<EmpLeave> getYearLeaveByType(Employee employee) {
		Integer currYear = employee.getYear();// Integer.parseInt(DateUtils.getYear(new
												// Date()));//当年
		List<Integer> yearList = new ArrayList<Integer>();
		yearList.add(currYear);
		yearList.add((currYear - 1));// 去年

		EmpLeave leave = new EmpLeave();
		leave.setType(ConfigConstants.LEAVE_TYPE_1);// 假期类型
		leave.setIsActive(ConfigConstants.IS_ACTIVE_INTEGER);// 有效的假期
		leave.setDelFlag(ConfigConstants.IS_NO_INTEGER);// 未删除
		leave.setEmployeeId(employee.getId());
		leave.setYearList(yearList);

		return empLeaveMapper.getListByCondition(leave);
	}

	/**
	 * exportReport(导出假期余额报表) @Title: exportReport @Description: 导出假期余额报表 @param
	 * empLeave @return 设定文件 HSSFWorkbook 返回类型 @throws
	 */
	public HSSFWorkbook exportReport(Employee emp) {
		// 封装部门
		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
		if(deptDataByUserList==null || deptDataByUserList.size()==0){
			return null;
		}else{
			emp.setCurrentUserDepart(deptDataByUserList);//数据权限
			List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(currentUser.getEmployeeId());//下属员工
			if(subEmployeeIdList!=null&&subEmployeeIdList.size()>0){
				emp.setSubEmployeeIdList(subEmployeeIdList);
			}
			Integer firstDepart = emp.getFirstDepart();// 页面上一级部门
			Integer secondDepart = emp.getSecondDepart();// 页面上二级部门
			List<Integer> departList = new ArrayList<Integer>();
			if (null != secondDepart) {// 选择了二级部门
				departList.add(secondDepart);
			} else if (null == secondDepart && null != firstDepart) {// 只选择了一级部门
				// 根据一级部门将一级部门下面的所有二级部门查询出来
				departList.add(firstDepart);
				List<Depart> departs = departService.getByParentId(Long.valueOf(firstDepart + ""));
				departs.forEach((Depart dp) -> {
					departList.add(Integer.parseInt(dp.getId() + ""));
				});
			}
			emp.setDepartList(departList);
			emp.setYear(Integer.parseInt(DateUtils.getYear(new Date())));
			List<EmpLeave> list = getReportList(emp);
			List<Map<String, Object>> datas = convertExportList(list);// 将查询的结果转换成excle需要的类型
	
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("假期余额");
	
			if (datas != null && datas.size() > 0) {
				setLeaveRemainReport(workbook, datas);// 封装假期余额报表excel数据
			} else {
	
				ExcelUtil.createRow(sheet.createRow(1), 0, null, HSSFCell.CELL_TYPE_STRING, "无数据");
			}
	
			return workbook;
		}	
	}

	public void setLeaveRemainReport(HSSFWorkbook workbook, List<Map<String, Object>> datas) {
		HSSFSheet sheet = workbook.getSheetAt(0);

		String[] title1 = { "员工编号", "员工姓名", "部门", "离职日期", "在职状态", "当年假期总数", "剩余假期天数", "截止目前剩余假期天数", "当年已使用假期天数",
				"透支假期天数" };
		String[] title2 = { "", "", "", "", "", "年假", "法定年假", "福利年假", "带薪病假", "去年法定年假", "去年福利年假", "当年法定年假", "当年福利年假",
				"年假", "带薪病假", "调休小时数", "当年法定年假", "当年福利年假", "带薪病假", "法定年假", "福利年假", "年假", "带薪病假", "非带薪病假", "事假", "调休小时数",
				"婚假", "丧假", "陪产假", "产前假", "产假", "哺乳假", "流产假", "其他", "年假", "带薪病假" };

		// 表头标题样式
		HSSFCellStyle colstyle = workbook.createCellStyle();
		colstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
		colstyle.setBorderBottom(XSSFCellStyle.BORDER_THIN); // 下边框
		colstyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);// 左边框
		colstyle.setBorderTop(XSSFCellStyle.BORDER_THIN);// 上边框
		colstyle.setBorderRight(XSSFCellStyle.BORDER_THIN);// 右边框

		// 建立第一行表头
		HSSFRow row = sheet.createRow((short) 0);

		// 创建第一行表头内容
		for (int colIndex = 0; colIndex < title1.length; colIndex++) {
			if (colIndex <= 4) {
				// CellRangeAddress参数详解：起始行号 结尾行号 起始列号 结尾列号
				// 第一行标题跨行跨列设置
				sheet.addMergedRegion(new CellRangeAddress(0, 1, colIndex, colIndex));// 员工编号
																						// 员工姓名
																						// 部门
																						// 离职日期
																						// 在职状态
				ExcelUtil.createRow(row, colIndex, colstyle, HSSFCell.CELL_TYPE_STRING, title1[colIndex]);
				sheet.setColumnWidth(colIndex, 5000);// 设置表格宽度
			} else if (colIndex == 5) {
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 5, 8));// 当年假期总数
				ExcelUtil.createRow(row, 5, colstyle, HSSFCell.CELL_TYPE_STRING, title1[colIndex]);
			} else if (colIndex == 6) {
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 9, 15));// 剩余假期天数
				ExcelUtil.createRow(row, 9, colstyle, HSSFCell.CELL_TYPE_STRING, title1[colIndex]);
			} else if (colIndex == 7) {
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 16, 18));// 截止目前剩余假期天数
				ExcelUtil.createRow(row, 16, colstyle, HSSFCell.CELL_TYPE_STRING, title1[colIndex]);
			} else if (colIndex == 8) {
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 19, 33));// 当年已使用假期天数
				ExcelUtil.createRow(row, 19, colstyle, HSSFCell.CELL_TYPE_STRING, title1[colIndex]);
			} else if (colIndex == 9) {
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 34, 35));// 透支假期天数
				ExcelUtil.createRow(row, 34, colstyle, HSSFCell.CELL_TYPE_STRING, title1[colIndex]);
			}
		}

		// 建立第二行表头
		row = sheet.createRow((short) 1);
		// 创建第二行表头内容
		for (int colIndex = 5; colIndex < title2.length; colIndex++) {
			ExcelUtil.createRow(row, colIndex, colstyle, HSSFCell.CELL_TYPE_STRING, title2[colIndex]);
			sheet.setColumnWidth(colIndex, 3500);// 设置表格宽度
		}

		// 从第三行开始就是excel数据
		int index = 2;
		for (Map<String, Object> data : datas) {
			row = sheet.createRow((short) index);

			ExcelUtil.createRow(row, 0, null, HSSFCell.CELL_TYPE_STRING, data.get("code"));
			ExcelUtil.createRow(row, 1, null, HSSFCell.CELL_TYPE_STRING, data.get("cnName"));
			ExcelUtil.createRow(row, 2, null, HSSFCell.CELL_TYPE_STRING, data.get("departName"));
			ExcelUtil.createRow(row, 3, null, HSSFCell.CELL_TYPE_STRING, data.get("quitTime"));
			ExcelUtil.createRow(row, 4, null, HSSFCell.CELL_TYPE_STRING, data.get("jobStatus"));
			ExcelUtil.createRow(row, 5, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave1"));
			ExcelUtil.createRow(row, 6, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave2"));
			ExcelUtil.createRow(row, 7, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave3"));
			ExcelUtil.createRow(row, 8, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave4"));
			ExcelUtil.createRow(row, 9, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave5"));
			ExcelUtil.createRow(row, 10, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave6"));
			ExcelUtil.createRow(row, 11, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave7"));
			ExcelUtil.createRow(row, 12, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave8"));
			ExcelUtil.createRow(row, 13, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave9"));
			ExcelUtil.createRow(row, 14, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave10"));
			ExcelUtil.createRow(row, 15, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave11"));
			ExcelUtil.createRow(row, 16, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave12"));
			ExcelUtil.createRow(row, 17, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave13"));
			ExcelUtil.createRow(row, 18, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave31"));
			ExcelUtil.createRow(row, 19, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave14"));
			ExcelUtil.createRow(row, 20, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave15"));
			ExcelUtil.createRow(row, 21, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave16"));
			ExcelUtil.createRow(row, 22, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave17"));
			ExcelUtil.createRow(row, 23, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave18"));
			ExcelUtil.createRow(row, 24, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave19"));
			ExcelUtil.createRow(row, 25, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave20"));
			ExcelUtil.createRow(row, 26, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave21"));
			ExcelUtil.createRow(row, 27, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave22"));
			ExcelUtil.createRow(row, 28, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave23"));
			ExcelUtil.createRow(row, 29, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave24"));
			ExcelUtil.createRow(row, 30, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave25"));
			ExcelUtil.createRow(row, 31, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave26"));
			ExcelUtil.createRow(row, 32, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave27"));
			ExcelUtil.createRow(row, 33, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave28"));
			ExcelUtil.createRow(row, 34, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave29"));
			ExcelUtil.createRow(row, 35, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave30"));

			index++;
		}
	}

	/**
	 * convertExportList(将查询的报表数据转换成exce需要的数据格式) @Title:
	 * convertExportList @Description: 将查询的报表数据转换成exce需要的数据格式 @param
	 * list @return 设定文件 List<Map<String,Object>> 返回类型 @throws
	 */
	public List<Map<String, Object>> convertExportList(List<EmpLeave> list) {
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;

		for (EmpLeave leave : list) {
			map = new HashMap<String, Object>();
			map.put("code", leave.getEmployee().getCode());
			map.put("cnName", leave.getEmployee().getCnName());
			if (null != leave.getEmployee().getDepart()) {
				map.put("departName", leave.getEmployee().getDepart().getName());
			} else {
				map.put("departName", "");
			}
			map.put("entryTime", DateUtils.format(leave.getEmployee().getFirstEntryTime(), DateUtils.FORMAT_SHORT));
			map.put("quitTime", DateUtils.format(leave.getEmployee().getQuitTime(), DateUtils.FORMAT_SHORT));
			if (null != leave.getEmployee().getJobStatus() && 1 == leave.getEmployee().getJobStatus()) {
				map.put("jobStatus", "离职");
			} else {
				map.put("jobStatus", "在职");
			}
			map.put("leave1", leave.getLeave1() != null ? leave.getLeave1() : 0);// 法定年假总数-年假
			map.put("leave2", leave.getLeave2() != null ? leave.getLeave2() : 0);// 法定年假总数-法定年假
			map.put("leave3", leave.getLeave3() != null ? leave.getLeave3() : 0);// 法定年假总数-福利年假
			map.put("leave4", leave.getLeave4() != null ? leave.getLeave4() : 0);// 法定年假总数-带薪病假
			map.put("leave5", leave.getLeave5() != null ? leave.getLeave5() : 0);// 剩余假期天数-去年法定年假
			map.put("leave6", leave.getLeave6() != null ? leave.getLeave6() : 0);// 剩余假期天数-去年福利年假
			map.put("leave7", leave.getLeave7() != null ? leave.getLeave7() : 0);// 剩余假期天数-当年法定年假
			map.put("leave8", leave.getLeave8() != null ? leave.getLeave8() : 0);// 剩余假期天数-当年福利年假
			map.put("leave9", leave.getLeave9() != null ? leave.getLeave9() : 0);// 剩余假期天数-年假
			map.put("leave10", leave.getLeave10() != null ? leave.getLeave10() : 0);// 剩余假期天数-带薪病假
			map.put("leave11", leave.getLeave11() != null ? leave.getLeave11() : 0);// 剩余假期天数-调休小时数
			map.put("leave12", leave.getLeave12() != null ? leave.getLeave12() : 0);// 截止目前剩余假期天数-当年法定年假
			map.put("leave13", leave.getLeave13() != null ? leave.getLeave13() : 0);// 截止目前剩余假期天数-当年福利年假
			map.put("leave31", leave.getLeave31() != null ? leave.getLeave31() : 0);// 截止目前剩余假期天数-带薪病假
			map.put("leave14", leave.getLeave14() != null ? leave.getLeave14() : 0);// 当年已使用假期天数-法定年假
			map.put("leave15", leave.getLeave15() != null ? leave.getLeave15() : 0);// 当年已使用假期天数-福利年假
			map.put("leave16", leave.getLeave16() != null ? leave.getLeave16() : 0);// 当年已使用假期天数-年假
			map.put("leave17", leave.getLeave17() != null ? leave.getLeave17() : 0);// 当年已使用假期天数-带薪病假
			map.put("leave18", leave.getLeave18() != null ? leave.getLeave18() : 0);// 当年已使用假期天数-非带薪病假
			map.put("leave19", leave.getLeave19() != null ? leave.getLeave19() : 0);// 当年已使用假期天数-事假
			map.put("leave20", leave.getLeave20() != null ? leave.getLeave20() : 0);// 当年已使用假期天数-调休小时数
			map.put("leave21", leave.getLeave21() != null ? leave.getLeave21() : 0);// 当年已使用假期天数-婚假
			map.put("leave22", leave.getLeave22() != null ? leave.getLeave22() : 0);// 当年已使用假期天数-丧假
			map.put("leave23", leave.getLeave23() != null ? leave.getLeave23() : 0);// 当年已使用假期天数-陪产假
			map.put("leave24", leave.getLeave24() != null ? leave.getLeave24() : 0);// 当年已使用假期天数-产前假
			map.put("leave25", leave.getLeave25() != null ? leave.getLeave25() : 0);// 当年已使用假期天数-产假
			map.put("leave26", leave.getLeave26() != null ? leave.getLeave26() : 0);// 当年已使用假期天数-哺乳假
			map.put("leave27", leave.getLeave27() != null ? leave.getLeave27() : 0);// 当年已使用假期天数-流产假
			map.put("leave28", leave.getLeave28() != null ? leave.getLeave28() : 0);// 当年已使用假期天数-其他
			map.put("leave29", leave.getLeave29() != null ? leave.getLeave29() : 0);// 透支假期天数-年假
			map.put("leave30", leave.getLeave30() != null ? leave.getLeave30() : 0);// 透支假期天数-带薪病假
			map.put("leave32", leave.getLeave32() != null ? leave.getLeave32() : 0);// 去年年假总天数-法定
			map.put("leave33", leave.getLeave33() != null ? leave.getLeave33() : 0);// 去年年假总天数-福利
			map.put("leave34", leave.getLeave34() != null ? leave.getLeave34() : 0);// 去年已用年假天数-法定
			map.put("leave35", leave.getLeave35() != null ? leave.getLeave35() : 0);// 去年已用年假天数-福利
			map.put("leave36", leave.getLeave36() != null ? leave.getLeave36() : 0);// 去年剩余调休小时数
			map.put("leave37", leave.getLeave37() != null ? leave.getLeave37() : 0);// 去年已用调休小时数

			datas.add(map);
		}

		return datas;
	}

	public void repairSickLeave() throws OaException {
		List<Map<String, Object>> dataMapList = empApplicationLeaveMapper.getRepairDate();

		EmpLeave el = null;
		for (Map<String, Object> dataMap : dataMapList) {
			el = new EmpLeave();
			el.setYear(Integer.parseInt(DateUtils.getYear(new Date())));
			el.setEmployeeId((Long) dataMap.get("employee_id"));
			el.setType(ConfigConstants.LEAVE_TYPE_2);// 病假
			EmpLeave leave = getByCondition(el);

			leave.setUsedDays(leave.getUsedDays() + (Double) dataMap.get("duration"));
			leave.setAllowRemainDays(leave.getAllowRemainDays() - (Double) dataMap.get("duration") >= 0
					? leave.getAllowRemainDays() - (Double) dataMap.get("duration") : 0);

			empLeaveMapper.updateById(leave);
		}
	}

	/**
	 * getRemainRest(获得剩余调休) @Title: getRemainRest @Description: 获得剩余调休 @param
	 * empLeave @return 设定文件 EmpLeave 返回类型 @throws
	 */
	public EmpLeave getRemainRest(EmpLeave empLeave) {
		return empLeaveMapper.getRemainRest(empLeave);
	}

	@Override
	public PageModel<Map<String, Object>> MonthLeaveDetailPageImpl(Employee cp) {
		PageModel<Map<String, Object>> pm = new PageModel<Map<String, Object>>();
		pm.setRows(new java.util.ArrayList<Map<String, Object>>());
		int page = cp.getPage() == null ? 0 : cp.getPage();
		int rows = cp.getRows() == null ? 0 : cp.getRows();
		//数据权限校验
		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
		if(deptDataByUserList==null || deptDataByUserList.size()==0){
			pm.setPageNo(0);
			pm.setPageSize(0);
			pm.setTotal(0);
			cp.setOffset(pm.getOffset());
			cp.setLimit(pm.getLimit());
			return pm;
		}else{
			pm.setPageNo(page);
			pm.setPageSize(rows);
			cp.setOffset(pm.getOffset());
			cp.setLimit(pm.getLimit());
			try {
				// pm = empLeaveService.getReportPageList(employee);
				String monthTitles[] = { cp.getYearAndMonth() };
				Date startTime;
				Date endTime;
				Integer total;
				Long departId = null;
				if (cp.getFirstDepart() != null) {
					departId = cp.getFirstDepart().longValue();
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
				try {
					cp.setOffset(pm.getOffset());
					cp.setLimit(pm.getLimit());
					cp.setCurrentUserDepart(deptDataByUserList);//数据权限
					List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(currentUser.getEmployeeId());//下属员工
					if(subEmployeeIdList!=null&&subEmployeeIdList.size()>0){
						cp.setSubEmployeeIdList(subEmployeeIdList);
					}
					if (StringUtils.isNotBlank(cp.getYearAndMonth())) {
						startTime = sdf.parse(cp.getYearAndMonth());
					//	DateUtils.getFirstDay(startTime);
						endTime = sdf.parse(cp.getYearAndMonth());
						//DateUtils.getLastDay(endTime);
						cp.setEmpTypeIdList(configService.getNeedEmpTypeIdList());
						Map<String, List<Map<String, Object>>> datasMap = empLeaveReportService
								.getExcelDataByYearAndMonthpageBean(monthTitles, startTime, endTime, departId, cp);
						// 行数查询
						if (null != departId) {
							cp.setDepartId(departId);
						}
						cp.setFirstEntryTime(endTime);
						cp.setWorkAddressType(0);
						int count = employeeMapper.getReportListCount(cp);
						
						if (count == 0) {
						} else {
							total = count;
							pm.setMapList(datasMap);
							pm.setPageSize(rows);
							pm.setTotal(total);
						}
						
					}
				} catch (ParseException e) {

				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			return pm;
		}
	}

	@Override
	public PageModel<Map<String, Object>> MonthLeaveCountPageImpl(Employee cp) {
		PageModel<Map<String, Object>> pm = new PageModel<Map<String, Object>>();
		pm.setRows(new java.util.ArrayList<Map<String, Object>>());
		int page = cp.getPage() == null ? 0 : cp.getPage();
		int rows = cp.getRows() == null ? 0 : cp.getRows();
		//数据权限校验
		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
		if(deptDataByUserList==null || deptDataByUserList.size()==0){
			pm.setPageNo(0);
			pm.setPageSize(0);
			pm.setTotal(0);
			cp.setOffset(pm.getOffset());
			cp.setLimit(pm.getLimit());
			pm.setRows(new ArrayList<>());
			return pm;
		}else{
			pm.setPageNo(page);
			pm.setPageSize(rows);
			cp.setOffset(pm.getOffset());
			cp.setLimit(pm.getLimit());
			try {
				String monthTitles[] = { cp.getYearAndMonth() };
				Date startTime;
				Date endTime;
				Integer total;
				Long departId = null;
				if (cp.getFirstDepart() != null) {
					departId = cp.getFirstDepart().longValue();
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
				try {
					if (StringUtils.isNotBlank(cp.getYearAndMonth())) {
						startTime = sdf.parse(cp.getYearAndMonth());
					//	DateUtils.getFirstDay(startTime);
						endTime = sdf.parse(cp.getYearAndMonth());
					//	DateUtils.getLastDay(endTime);
						cp.setCurrentUserDepart(deptDataByUserList);//数据权限
						List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(currentUser.getEmployeeId());//下属员工
						if(subEmployeeIdList!=null&&subEmployeeIdList.size()>0){
							cp.setSubEmployeeIdList(subEmployeeIdList);
						}
						//查询指定员工类型数据
						cp.setEmpTypeIdList(configService.getNeedEmpTypeIdList());
						
						Map<String, List<Map<String, Object>>> datasMap = empLeaveReportService
								.getmonthLeaveSummaryPageBean(DateUtils.getFirstDay(startTime),
										DateUtils.getLastDay(endTime), departId, monthTitles, cp);
						if (null != departId) {
							cp.setDepartId(departId);
						}
						cp.setFirstEntryTime(endTime);
						cp.setWorkAddressType(0);
						int count = employeeMapper.getReportListLeverCount(cp);
						if (count == 0) {
						} else {
							total = count;
							pm.setMapList(datasMap);
							pm.setPageSize(rows);
							pm.setTotal(total);
						}
					}
				} catch (ParseException e) {
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			return pm;
		}
	}

	@Override
	public XSSFWorkbook exportReportMonthLeaveDetail(Employee cp) throws Exception {
		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
		if(deptDataByUserList==null || deptDataByUserList.size()==0){
			return null;
		}else{
			XSSFWorkbook xSSFWorkbook = null;
			String[] keys = { "code", "name", /* 年假:1 */"1_1", "1_2", "1_3", "1_4", "1_5", "1_6", "1_7", "1_8", "1_9",
					"1_10", "1_11", "1_12", "1_13", "1_14", "1_15", "1_16", "1_17", "1_18", "1_19", "1_20", "1_21", "1_22",
					"1_23", "1_24", "1_25", "1_26", "1_27", "1_28", "1_29", "1_30", "1_31",
					/* 事假:11 */"11_1", "11_2", "11_3", "11_4", "11_5", "11_6", "11_7", "11_8", "11_9", "11_10", "11_11",
					"11_12", "11_13", "11_14", "11_15", "11_16", "11_17", "11_18", "11_19", "11_20", "11_21", "11_22",
					"11_23", "11_24", "11_25", "11_26", "11_27", "11_28", "11_29", "11_30", "11_31", /* 病假:2 */"2_1", "2_2",
					"2_3", "2_4", "2_5", "2_6", "2_7", "2_8", "2_9", "2_10", "2_11", "2_12", "2_13", "2_14", "2_15", "2_16",
					"2_17", "2_18", "2_19", "2_20", "2_21", "2_22", "2_23", "2_24", "2_25", "2_26", "2_27", "2_28", "2_29",
					"2_30", "2_31", /* 产假:7 */"7_1", "7_2", "7_3", "7_4", "7_5", "7_6", "7_7", "7_8", "7_9", "7_10", "7_11",
					"7_12", "7_13", "7_14", "7_15", "7_16", "7_17", "7_18", "7_19", "7_20", "7_21", "7_22", "7_23", "7_24",
					"7_25", "7_26", "7_27", "7_28", "7_29", "7_30", "7_31",
					/* 调休:5 */"5_1", "5_2", "5_3", "5_4", "5_5", "5_6", "5_7", "5_8", "5_9", "5_10", "5_11", "5_12", "5_13",
					"5_14", "5_15", "5_16", "5_17", "5_18", "5_19", "5_20", "5_21", "5_22", "5_23", "5_24", "5_25", "5_26",
					"5_27", "5_28", "5_29", "5_30", "5_31", /* 陪产假:9 */"9_1", "9_2", "9_3", "9_4", "9_5", "9_6", "9_7",
					"9_8", "9_9", "9_10", "9_11", "9_12", "9_13", "9_14", "9_15", "9_16", "9_17", "9_18", "9_19", "9_20",
					"9_21", "9_22", "9_23", "9_24", "9_25", "9_26", "9_27", "9_28", "9_29", "9_30", "9_31",
					/* 丧假:10 */"10_1", "10_2", "10_3", "10_4", "10_5", "10_6", "10_7", "10_8", "10_9", "10_10", "10_11",
					"10_12", "10_13", "10_14", "10_15", "10_16", "10_17", "10_18", "10_19", "10_20", "10_21", "10_22",
					"10_23", "10_24", "10_25", "10_26", "10_27", "10_28", "10_29", "10_30", "10_31", /* 婚假:3 */"3_1", "3_2",
					"3_3", "3_4", "3_5", "3_6", "3_7", "3_8", "3_9", "3_10", "3_11", "3_12", "3_13", "3_14", "3_15", "3_16",
					"3_17", "3_18", "3_19", "3_20", "3_21", "3_22", "3_23", "3_24", "3_25", "3_26", "3_27", "3_28", "3_29",
					"3_30", "3_31", /* 产前假:6 */"6_1", "6_2", "6_3", "6_4", "6_5", "6_6", "6_7", "6_8", "6_9", "6_10",
					"6_11", "6_12", "6_13", "6_14", "6_15", "6_16", "6_17", "6_18", "6_19", "6_20", "6_21", "6_22", "6_23",
					"6_24", "6_25", "6_26", "6_27", "6_28", "6_29", "6_30", "6_31",
					/* 流产假:8 */"8_1", "8_2", "8_3", "8_4", "8_5", "8_6", "8_7", "8_8", "8_9", "8_10", "8_11", "8_12",
					"8_13", "8_14", "8_15", "8_16", "8_17", "8_18", "8_19", "8_20", "8_21", "8_22", "8_23", "8_24", "8_25",
					"8_26", "8_27", "8_28", "8_29", "8_30", "8_31"
					/* 哺乳假:4 */// "4_1", "4_2", "4_3", "4_4", "4_5", "4_6", "4_7",
					// "4_8", "4_9", "4_10", "4_11", "4_12", "4_13",
					// "4_14", "4_15", "4_16", "4_17", "4_18", "4_19",
					// "4_20", "4_21", "4_22", "4_23", "4_24", "4_25",
					// "4_26", "4_27", "4_28", "4_29", "4_30", "4_31",
					/* 其他:12 */// "12_1", "12_2", "12_3", "12_4", "12_5", "12_6",
					// "12_7", "12_8", "12_9", "12_10", "12_11",
					// "12_12", "12_13", "12_14", "12_15", "12_16",
					// "12_17", "12_18", "12_19", "12_20", "12_21",
					// "12_22", "12_23", "12_24", "12_25", "12_26",
					// "12_27", "12_28", "12_29", "12_30", "12_31"
			};
			// title的第一行
			String[] titles0 = { "年假", "事假", "病假", "调休", "产假", "陪产假", "丧假", "婚假", "产前假",
			"流产假"/* , "哺乳假", "其他" */ };
			// title的第一行标题对应的列数坐标{起始行号，终止行号， 起始列号，终止列号}
			int[][] title0Coordinate = { { 0, 0, 2, 32 }, { 0, 0, 33, 63 }, { 0, 0, 64, 94 }, { 0, 0, 95, 125 },
					{ 0, 0, 126, 156 }, { 0, 0, 157, 187 }, { 0, 0, 188, 218 }, { 0, 0, 219, 249 }, { 0, 0, 250, 280 },
					{ 0, 0, 281, 311 }/* ,{0,0,312,342},{0,0,343,383} */ };
			String[] titles1 = { "员工编号", "姓名", /* 年假:1 */"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
					"13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29",
					"30", "31", /* 事假:11 */"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
					"16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
					/* 病假:2 */"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17",
					"18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
					/* 调休:5 */"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17",
					"18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
					/* 产假:7 */"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17",
					"18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
					/* 陪产假:9 */"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17",
					"18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
					/* 丧假:10 */"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17",
					"18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
					/* 婚假:3 */"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17",
					"18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
					/* 产前假:6 */"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17",
					"18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
					/* 流产假:8 */"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17",
					"18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"
					/* 哺乳假:4 */// "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
					// "11", "12", "13", "14", "15", "16", "17", "18",
					// "19", "20", "21", "22", "23", "24", "25", "26",
					// "27", "28", "29", "30", "31",
					/* 其他:12 */// "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
					// "11", "12", "13", "14", "15", "16", "17", "18",
					// "19", "20", "21", "22", "23", "24", "25", "26",
					// "27", "28", "29", "30", "31"
			};
			
			// shijian
			String monthTitlesa[] = { cp.getYearAndMonth() };
			Date startTime;
			Date endTime;
			Integer total;
			Long departId = null;
			if (cp.getFirstDepart() != null) {
				departId = cp.getFirstDepart().longValue();
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
			if (StringUtils.isNotBlank(cp.getYearAndMonth())) {
				startTime = sdf.parse(cp.getYearAndMonth());
				DateUtils.getFirstDay(startTime);
				endTime = sdf.parse(cp.getYearAndMonth());
				DateUtils.getLastDay(endTime);
				List<Date> months = DateUtils.findMonths(startTime, endTime);
				String[] monthTitles = new String[months.size()];
				for (int i = 0; i < months.size(); i++) {
					monthTitles[i] = DateUtils.getYearAndMonth(months.get(i));
				}
				cp.setExportStauts(1);
				cp.setCurrentUserDepart(deptDataByUserList);//数据权限
				List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(currentUser.getEmployeeId());//下属员工
				if(subEmployeeIdList!=null&&subEmployeeIdList.size()>0){
					cp.setSubEmployeeIdList(subEmployeeIdList);
				}
				//查询指定员工类型数据
				cp.setEmpTypeIdList(configService.getNeedEmpTypeIdList());
				
				Map<String, List<Map<String, Object>>> datasMap = empLeaveReportService
						.getExcelDataByYearAndMonthpageBean(monthTitles, startTime, endTime, departId, cp);
				String fileNames = "月度假期明细报表" + monthTitles[0].substring(0, 4) + "年";
				xSSFWorkbook = ExcelUtil.exportExcelForLeaveReports(datasMap, keys, titles0, title0Coordinate, titles1,
						monthTitles, fileNames + ".xls");
			}
			return xSSFWorkbook;
		}
	}

	@Override
	public HSSFWorkbook exportReportMonthLeaveCount(Employee cp) throws Exception {
		//数据权限
		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
		if(deptDataByUserList==null || deptDataByUserList.size()==0){
			return null;
		}else{
			HSSFWorkbook hSSFWorkbook = null;
			String subject = "月度假期统计表";
			String sttachmentName = "月度假期统计表";
			String[] titles = { "员工编号", "姓名", "部门", "当月年假次数", "当月年假天数", "当月病假次数", "当月病假天数", "当月事假次数", "当月事假天数", "当月调休次数",
					"当月调休小时数", "当月产假次数", "当月产假天数", "当月陪产假次数", "当月陪产假天数", "当月丧假次数", "当月丧假天数", "当月婚假次数", "当月婚假天数", "当月产前假次数",
					"当月产前假天数", "当月流产假次数", "当月流产假天数" };
			String[] keys = { "code", "cnName", "departName", "count_1", "days_1", "count_2", "days_2", "count_11",
					"days_11", "count_5", "days_5", "count_7", "days_7", "count_9", "days_9", "count_10", "days_10",
					"count_3", "days_3", "count_6", "days_6", "count_8", "days_8" };
			// shijian
			String monthTitlesa[] = { cp.getYearAndMonth() };
			Date startTime;
			Date endTime;
			Integer total;
			Long departId = null;
			if (cp.getFirstDepart() != null) {
				departId = cp.getFirstDepart().longValue();
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
			if (StringUtils.isNotBlank(cp.getYearAndMonth())) {
				startTime = sdf.parse(cp.getYearAndMonth());
				//DateUtils.getFirstDay(startTime);
				endTime = sdf.parse(cp.getYearAndMonth());
			//	DateUtils.getLastDay(endTime);
				List<Date> months = DateUtils.findMonths(startTime, endTime);
				String[] monthTitles = new String[months.size()];
				for (int i = 0; i < months.size(); i++) {
					monthTitles[i] = DateUtils.getYearAndMonth(months.get(i));
				}
				cp.setCurrentUserDepart(deptDataByUserList);//数据权限
				List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(currentUser.getEmployeeId());//下属员工
				if(subEmployeeIdList!=null&&subEmployeeIdList.size()>0){
					cp.setSubEmployeeIdList(subEmployeeIdList);
				}
				//查询指定员工类型数据
				cp.setEmpTypeIdList(configService.getNeedEmpTypeIdList());
				
				Map<String, List<Map<String, Object>>> datasMap = empLeaveReportService
						.getmonthLeaveSummaryPageBean(startTime, endTime, departId, monthTitles, cp);
				hSSFWorkbook = ExcelUtil.exportExcelForMonths(datasMap, keys, titles, monthTitles, "月度假期统计表.xls");
			}
			return hSSFWorkbook;
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
	public void updateLeaveByEmpInfo(EmployeeApp employee) throws Exception {
		// 获取司龄对应的年假基数
		Map<String, Object> leaveMap = getLeaveRaduixConfig();
		// 入职日期
		Date joinTime = employee.getFirstEntryTime();
		// 离职日期
		Date quitTime = employee.getQuitTime();
		Map<String, Object> ourAgeMap = getOurAge(employee);// 获得实际司龄和计算司龄（实际司龄=入职天数/12保留一位小树，计算司龄=不满1年按0算）
		Double acuOurAge = (Double) ourAgeMap.get("acuOurAge");// 计算司龄(不满1年按0计算)
		// 更新假期的基数
		updateYearRaduix(acuOurAge, employee, leaveMap);
		// 取出员工年假
		List<EmpLeave> empLeaves = getEmpLeaveByEmpIdAndType(employee.getId(),ConfigConstants.LEAVE_TYPE_1);
		// 根据当年的入职时间计算对应的年假天数
		updateYearLeave(employee.getAutoCalculateLeave(), joinTime, quitTime, empLeaves);
	}

	@Override
	public void updateQuitLeaveByEmpInfo(EmployeeApp employee) throws Exception {
		// 获取司龄对应的年假基数
		Map<String, Object> leaveMap = getLeaveRaduixConfig();
		// 入职日期
		Date joinTime = employee.getFirstEntryTime();
		// 离职日期
		Date quitTime = employee.getQuitTime();
		Map<String, Object> ourAgeMap = getOurAge(employee);// 获得实际司龄和计算司龄（实际司龄=入职天数/12保留一位小树，计算司龄=不满1年按0算）
		Double acuOurAge = (Double) ourAgeMap.get("acuOurAge");// 计算司龄(不满1年按0计算)
		// 更新假期的基数
		updateYearRaduix(acuOurAge, employee, leaveMap);
		// 取出员工年假
		List<EmpLeave> empLeaves = getEmpLeaveByEmpIdAndType(employee.getId(),ConfigConstants.LEAVE_TYPE_1);
		// 根据当年的入职时间计算对应的年假天数
		updateQuitYearLeave(joinTime, quitTime, empLeaves);
	}

	@Override
	public void updateSickLeaveByEmpInfo(EmployeeApp employee) throws Exception {
		// 获取司龄对应的年假基数 病假基数就是 5
		/// Map<String,Object> leaveMap = getLeaveRaduixConfig();
		// 入职日期
		Date joinTime = employee.getFirstEntryTime();
		// 离职日期
		Date quitTime = employee.getQuitTime();
		// Map<String,Object> ourAgeMap =
		// getOurAge(employee);//获得实际司龄和计算司龄（实际司龄=入职天数/12保留一位小树，计算司龄=不满1年按0算）
		// Integer calOurAge =
		// (Integer)ourAgeMap.get("calOurAge");//计算司龄(不满1年按0计算)
		// 更新假期的基数
		// updateYearRaduix(calOurAge, employee, leaveMap); //5
		// 取出员工年假
		List<EmpLeave> empLeaves = getEmpLeaveByEmpIdAndType(employee.getId(),ConfigConstants.LEAVE_TYPE_2);
		// 根据当年的入职时间计算对应的年假天数
		updateSickYearLeave(joinTime, quitTime, empLeaves);
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
	public Map<String, Object> updateUsedLeaveByEmpId(EmpLeave empLeave) throws Exception {
		// TODO:记录操作日志
		Map<String, Object> result = Maps.newHashMap();
		User user = userService.getCurrentUser();

		SysUpdateLogTbl sysUpdateLog = new SysUpdateLogTbl();
		sysUpdateLog.setUpdateEmployeeId(user.getEmployeeId());
		sysUpdateLog.setUpdateEmployeeName(user.getEmployee().getCnName());
		sysUpdateLog.setResourceEmployeeId(empLeave.getEmployeeId());
		sysUpdateLog.setCreateTime(new Date());
		sysUpdateLog.setResourceType(1);
		// 新增 备注记录
		if (StringUtils.isNotBlank(empLeave.getRemark())) {
			sysUpdateLog.setRemarkRecord(empLeave.getRemark());
		}
		JsonObject json = new JsonObject();
		EmpLeave param = new EmpLeave();
		param.setCompanyId(1L);
		param.setYear(Integer.valueOf(DateUtils.format(new Date(), "yyyy")));
		param.setEmployeeId(empLeave.getEmployeeId());
		switch (empLeave.getLeaveType()) {
		case "1":
			json.addProperty("type", "年假");
			// 年假
			param.setType(ConfigConstants.LEAVE_TYPE_1);
			List<EmpLeave> yearLeaves = this.empLeaveMapper.getListByCondition(param);
			for (EmpLeave yearLeave : yearLeaves) {
				// 今年假期
				if (yearLeave.getCategory().intValue() == 0) {
					// 总年假
					json.addProperty("beforeTotleAllowRemainDays", yearLeave.getAllowRemainDays());
					json.addProperty("beforeTotleUsedDays", yearLeave.getUsedDays());
					yearLeave.setAllowRemainDays(yearLeave.getAllowRemainDays()
							- (empLeave.getLeave14() + empLeave.getLeave15() - yearLeave.getUsedDays()));
					yearLeave.setUsedDays(empLeave.getLeave14() + empLeave.getLeave15());
					json.addProperty("afterTotleAllowRemainDays", yearLeave.getAllowRemainDays());
					json.addProperty("afterTotleUsedDays", yearLeave.getUsedDays());
				} else if (yearLeave.getCategory().intValue() == 1) {
					// leave14 今年已用年假天数-法定
					json.addProperty("beforeLegalAllowRemainDays", yearLeave.getAllowRemainDays());
					json.addProperty("beforeLegalUsedDays", yearLeave.getUsedDays());
					yearLeave.setAllowRemainDays(
							yearLeave.getAllowRemainDays() - (empLeave.getLeave14() - yearLeave.getUsedDays()));
					yearLeave.setUsedDays(empLeave.getLeave14());
					json.addProperty("afterLegalAllowRemainDays", yearLeave.getAllowRemainDays());
					json.addProperty("afterLegalUsedDays", yearLeave.getUsedDays());
				} else if (yearLeave.getCategory().intValue() == 2) {
					// leave15 今年已用年假天数-福利
					json.addProperty("beforeWelfareAllowRemainDays", yearLeave.getAllowRemainDays());
					json.addProperty("beforeWelfareUsedDays", yearLeave.getUsedDays());
					yearLeave.setAllowRemainDays(
							yearLeave.getAllowRemainDays() - (empLeave.getLeave15() - yearLeave.getUsedDays()));
					yearLeave.setUsedDays(empLeave.getLeave15());
					json.addProperty("afterWelfareAllowRemainDays", yearLeave.getAllowRemainDays());
					json.addProperty("afterWelfareUsedDays", yearLeave.getUsedDays());
				}
				this.empLeaveMapper.updateById(yearLeave);
			}
			param.setYear(Integer.valueOf(DateUtils.format(DateUtils.addYear(new Date(), -1), "yyyy")));
			List<EmpLeave> lastyearLeaves = this.empLeaveMapper.getListByCondition(param);
			for (EmpLeave lastyearLeave : lastyearLeaves) {
				// 去年假期
				if (lastyearLeave.getCategory() == 0) {
					// 总年假
					json.addProperty("lastYearBeforeTotleAllowRemainDays", lastyearLeave.getAllowRemainDays());
					json.addProperty("lastYearBeforeTotleUsedDays", lastyearLeave.getUsedDays());
					lastyearLeave.setAllowRemainDays(lastyearLeave.getAllowRemainDays()
							- (empLeave.getLeave34() + empLeave.getLeave35() - lastyearLeave.getUsedDays()));
					lastyearLeave.setUsedDays(empLeave.getLeave34() + empLeave.getLeave35());
					json.addProperty("lastYearAfterTotleAllowRemainDays", lastyearLeave.getAllowRemainDays());
					json.addProperty("lastYearAfterTotleUsedDays", lastyearLeave.getUsedDays());
				} else if (lastyearLeave.getCategory() == 1) {
					// leave34 去年已用年假天数-法定
					json.addProperty("lastYearBeforeLegalAllowRemainDays", lastyearLeave.getAllowRemainDays());
					json.addProperty("lastYearBeforeLegalUsedDays", lastyearLeave.getUsedDays());
					lastyearLeave.setAllowRemainDays(
							lastyearLeave.getAllowRemainDays() - (empLeave.getLeave34() - lastyearLeave.getUsedDays()));
					lastyearLeave.setUsedDays(empLeave.getLeave34());
					json.addProperty("lastYearAfterLegalAllowRemainDays", lastyearLeave.getAllowRemainDays());
					json.addProperty("lastYearAfterLegalUsedDays", lastyearLeave.getUsedDays());
				} else if (lastyearLeave.getCategory() == 2) {
					// leave35 去年已用年假天数-福利
					json.addProperty("lastYearBeforeWelfareAllowRemainDays", lastyearLeave.getAllowRemainDays());
					json.addProperty("lastYearBeforeWelfareUsedDays", lastyearLeave.getUsedDays());
					lastyearLeave.setAllowRemainDays(
							lastyearLeave.getAllowRemainDays() - (empLeave.getLeave35() - lastyearLeave.getUsedDays()));
					lastyearLeave.setUsedDays(empLeave.getLeave35());
					json.addProperty("lastYearAfterWelfareAllowRemainDays", lastyearLeave.getAllowRemainDays());
					json.addProperty("lastYearAfterWelfareUsedDays", lastyearLeave.getUsedDays());
				}
				this.empLeaveMapper.updateById(lastyearLeave);
			}
			break;
		case "2":
			// 病假
			json.addProperty("type", "病假");
			param.setType(ConfigConstants.LEAVE_TYPE_2);
			EmpLeave sickLeave = this.empLeaveMapper.getListByCondition(param).get(0);
			EmpLeave unSickLeave = null;
			if (this.empLeaveMapper.getListByCondition(param).size() > 1) {
				unSickLeave = this.empLeaveMapper.getListByCondition(param).get(1);
			}

			// leave17;//当年已使用假期天数-带薪病假
			// leave18;//当年已使用假期天数-非带薪病假
			json.addProperty("beforeUserPaidDays", sickLeave.getAllowDays() - sickLeave.getAllowRemainDays());
			json.addProperty("beforeUserUnPaidDays", sickLeave.getUsedDays() - sickLeave.getAllowRemainDays());
			json.addProperty("afterUserPaidDays", empLeave.getLeave17());
			json.addProperty("afterUserUnPaidDays", empLeave.getLeave18());
			// 修改 带薪病假 通过员工id 查询 为带薪病假的员工在进行修改
			sickLeave.setUsedDays(empLeave.getLeave17());
			sickLeave.setAllowRemainDays(sickLeave.getAllowDays() - empLeave.getLeave17());
			// 修改 非带薪病假 通过员工id 查询 为带薪病假的员工在进行修改
			if (unSickLeave != null) {
				unSickLeave.setUsedDays(empLeave.getLeave18());
				this.empLeaveMapper.updateById(unSickLeave);
			}
			this.empLeaveMapper.updateById(sickLeave);
			break;
		case "3":
			// 调休
			json.addProperty("type", "调休");
			param.setType(ConfigConstants.LEAVE_TYPE_5);
			// 今年调休
			// leave11;//剩余假期天数-调休小时数
			// leave20;//当年已使用调休
			List<EmpLeave> listByCondition = this.empLeaveMapper.getListByCondition(param);
			EmpLeave overtimeLeave = new EmpLeave();
			if (listByCondition != null && listByCondition.size() > 0) {
				overtimeLeave = listByCondition.get(0);
			} else {
				json.addProperty("beforeOvertimeAllowRemainDays", 0);
				json.addProperty("beforeOvertimeUsedDays", 0);
				json.addProperty("afterOvertimeAllowRemainDays", empLeave.getLeave11());
				json.addProperty("afterOvertimeUsedDays", empLeave.getLeave20());
				EmpLeave total = new EmpLeave();
				total.setYear(Integer.valueOf(DateUtils.format(new Date(), "yyyy")));
				total.setUsedDays(empLeave.getLeave20());
				total.setAllowDays(80d);
				total.setAllowRemainDays(empLeave.getLeave11());
				total.setEmployeeId(empLeave.getEmployeeId());
				total.setDelFlag(0);
				total.setType(5);
				total.setCategory(0);
				total.setParendId(0L);
				total.setIsActive(0);
				total.setCreateTime(new Date());
				total.setUpdateTime(new Date());
				total.setCompanyId(1L);
				total.setUpdateUser("system");
				total.setCreateUser("system");
				total.setStartTime(DateUtils.parse(DateUtils.format(new Date(), "yyyy") + "-01-01 00:00:00",
						DateUtils.FORMAT_LONG));
				total.setEndTime(DateUtils.parse(DateUtils.format(new Date(), "yyyy") + "-12-31 23:59:59",
						DateUtils.FORMAT_LONG));
				this.save(total);
				total.setYear(1);
				total.setParendId(total.getId());
				total.setId(null);
				total.setAllowDays(36d);
				total.setCategory(1);
				this.save(total);
				break;
			}
			json.addProperty("beforeOvertimeAllowRemainDays", overtimeLeave.getAllowRemainDays());
			json.addProperty("beforeOvertimeUsedDays", overtimeLeave.getUsedDays());
			param.setYear(null);
			param.setParendId(overtimeLeave.getId());
			List<EmpLeave> overtimeLeaves = this.empLeaveMapper.getListByCondition(param);
			Double needDeductUsedDays = overtimeLeave.getUsedDays() - empLeave.getLeave20();
			Double needDeductRemainDays = overtimeLeave.getAllowRemainDays() - empLeave.getLeave11();
			overtimeLeave.setUsedDays(empLeave.getLeave20());
			// 先算出总的值,剩余和使用都已确定
			if (needDeductRemainDays == 0) {
				overtimeLeave.setAllowRemainDays(overtimeLeave.getAllowRemainDays() + needDeductUsedDays);
			} else {
				overtimeLeave.setAllowRemainDays(empLeave.getLeave11());
			}
			json.addProperty("afterOvertimeAllowRemainDays", overtimeLeave.getAllowRemainDays());
			json.addProperty("afterOvertimeUsedDays", overtimeLeave.getUsedDays());
			needDeductUsedDays = overtimeLeave.getUsedDays();
			needDeductRemainDays = overtimeLeave.getAllowRemainDays();
			Double leaveTotleDays = 0.0;
			// 再for循环与现有的值进行相减
			// 分配剩余情况,blockDays+allowRemainDays
			for (int i = overtimeLeaves.size() - 1; i >= 0; i--) {
				EmpLeave empLeave2 = overtimeLeaves.get(i);
				needDeductRemainDays = needDeductRemainDays - empLeave2.getAllowRemainDays()
						- empLeave2.getBlockedDays();
				if (needDeductRemainDays < 0) {
					empLeave2.setAllowRemainDays(empLeave2.getAllowRemainDays() + needDeductRemainDays);
					empLeave2.setUsedDays(empLeave2.getUsedDays() - needDeductRemainDays);
					needDeductRemainDays = 0.0;
				} else if (needDeductRemainDays == 0) {
					empLeave2.setUsedDays(empLeave2.getUsedDays() + empLeave2.getAllowRemainDays());
					// empLeave2.setAllowRemainDays(0.0);
				}
			}
			// 还有未充完的调休
			if (needDeductRemainDays > 0) {
				Date date = DateUtils.addMonth(new Date(), -1);
				param.setYear(Integer.valueOf(DateUtils.getMonthofYear(date)));
				List<EmpLeave> lastMonthLeave = this.empLeaveMapper.getListByCondition(param);
				if (null == lastMonthLeave || lastMonthLeave.size() == 0) {
					// 上个月假期数据不存在
					EmpLeave add = new EmpLeave();
					Employee employee = employeeMapper.getByEmpId(empLeave.getEmployeeId()).get(0);
					add.setCreateUser(ConfigConstants.API);
					add.setCreateTime(new Date());
					add.setCompanyId(employee.getCompanyId());
					add.setEmployeeId(employee.getId());
					add.setYear(param.getYear());
					add.setType(ConfigConstants.LEAVE_TYPE_5);
					add.setAllowDays(36d);
					add.setUsedDays(0d);
					add.setAllowRemainDays(Math.abs(needDeductUsedDays));
					add.setIsActive(0);
					add.setCategory(1);
					add.setVersion(0L);
					add.setDelFlag(0);
					add.setStartTime(
							DateUtils.parse(DateUtils.getYear(date) + "-01-01 00:00:00", DateUtils.FORMAT_LONG));
					add.setEndTime(DateUtils.parse(DateUtils.getYear(date) + "-12-31 23:59:59", DateUtils.FORMAT_LONG));
					add.setParendId(overtimeLeave.getId());
					overtimeLeaves.add(overtimeLeaves.size() - 1, add);
				} else {
					EmpLeave empLeave2 = overtimeLeaves.get(overtimeLeaves.size() - 2);
					empLeave2.setAllowRemainDays(empLeave2.getAllowRemainDays() + Math.abs(needDeductRemainDays));
				}
			}
			// 分配使用情况
			for (EmpLeave leave : overtimeLeaves) {
				leaveTotleDays = leave.getUsedDays() + leave.getAllowRemainDays();
				needDeductUsedDays = needDeductUsedDays - leaveTotleDays;
				if (needDeductUsedDays < 0) {
					leave.setUsedDays(leaveTotleDays + needDeductUsedDays);
					needDeductUsedDays = 0.0;
					break;
				}
			}
			if (needDeductUsedDays > 0) {
				throw new OaException("修改后当年剩余调休为负数,请核对后修改");
			}

			this.empLeaveMapper.updateById(overtimeLeave);
			for (EmpLeave empLeave2 : overtimeLeaves) {
				this.empLeaveMapper.updateById(empLeave2);
			}
			// 去年调休
			// leave36;//去年剩余调休小时数
			// leave37;//去年已用调休小时数
			param.setParendId(null);
			param.setYear(Integer.valueOf(DateUtils.format(DateUtils.addYear(new Date(), -1), "yyyy")));
			List<EmpLeave> lastYearOvertimeLeaveList = this.empLeaveMapper.getListByCondition(param);
			if (lastYearOvertimeLeaveList != null && lastYearOvertimeLeaveList.size() > 0) {
				EmpLeave lastYearOvertimeLeave = lastYearOvertimeLeaveList.get(0);
				json.addProperty("oldBeforeOvertimeAllowRemainDays", lastYearOvertimeLeave.getAllowRemainDays());
				json.addProperty("oldBeforeOvertimeUsedDays", lastYearOvertimeLeave.getUsedDays());
				param.setYear(null);
				param.setParendId(lastYearOvertimeLeave.getId());
				List<EmpLeave> lastYearOvertimeLeaves = this.empLeaveMapper.getListByCondition(param);
				needDeductUsedDays = lastYearOvertimeLeave.getUsedDays() - empLeave.getLeave37();
				needDeductRemainDays = lastYearOvertimeLeave.getAllowRemainDays() - empLeave.getLeave36();
				lastYearOvertimeLeave.setUsedDays(empLeave.getLeave37());
				// 先算出总的值,剩余和使用都已确定
				if (needDeductRemainDays == 0) {
					lastYearOvertimeLeave
							.setAllowRemainDays(lastYearOvertimeLeave.getAllowRemainDays() - needDeductUsedDays);
				} else {
					lastYearOvertimeLeave.setAllowRemainDays(empLeave.getLeave36());
				}
				json.addProperty("oldAfterOvertimeAllowRemainDays", lastYearOvertimeLeave.getAllowRemainDays());
				json.addProperty("oldAfterOvertimeUsedDays", lastYearOvertimeLeave.getUsedDays());
				needDeductUsedDays = lastYearOvertimeLeave.getUsedDays();
				needDeductRemainDays = lastYearOvertimeLeave.getAllowRemainDays();
				// 再for循环与现有的值进行相减
				// 分配剩余情况
				for (int i = lastYearOvertimeLeaves.size() - 1; i >= 0; i--) {
					EmpLeave empLeave2 = lastYearOvertimeLeaves.get(i);
					needDeductRemainDays = needDeductRemainDays - empLeave2.getAllowRemainDays()
							- empLeave2.getBlockedDays();
					if (needDeductRemainDays < 0) {
						empLeave2.setAllowRemainDays(empLeave2.getAllowRemainDays() + needDeductRemainDays);
						needDeductRemainDays = 0.0;
					} else if (needDeductRemainDays == 0) {
						empLeave2.setAllowRemainDays(0.0);
					}
				}
				// //还有未充完的调休
				// if(needDeductRemainDays > 0) {
				// Date date = DateUtils.addMonth(new Date(), -1);
				// param.setYear(12);
				// List<EmpLeave> lastMonthLeave =
				// this.empLeaveMapper.getListByCondition(param);
				// if(null == lastMonthLeave || lastMonthLeave.size() == 0) {
				// //上个月假期数据不存在
				// EmpLeave add = new EmpLeave();
				// Employee employee =
				// employeeMapper.getByEmpId(empLeave.getEmployeeId()).get(0);
				// add.setCreateUser(ConfigConstants.API);
				// add.setCreateTime(new Date());
				// add.setCompanyId(employee.getCompanyId());
				// add.setEmployeeId(employee.getId());
				// add.setYear(param.getYear());
				// add.setType(ConfigConstants.LEAVE_TYPE_5);
				// add.setAllowDays(36d);
				// add.setAllowRemainDays(Math.abs(needDeductUsedDays));
				// add.setIsActive(0);
				// add.setCategory(1);
				// add.setVersion(0l);
				// add.setDelFlag(0);
				// add.setStartTime(DateUtils.parse(DateUtils.getYear(date)+"-01-01
				// 00:00:00",DateUtils.FORMAT_LONG));
				// add.setEndTime(DateUtils.parse(DateUtils.getYear(date)+"-12-31
				// 23:59:59",DateUtils.FORMAT_LONG));
				// add.setParendId(overtimeLeave.getId());
				// lastYearOvertimeLeaves.add(lastYearOvertimeLeaves.size(),
				// add);
				// }else {
				// EmpLeave empLeave2 =
				// lastYearOvertimeLeaves.get(overtimeLeaves.size());
				// empLeave2.setAllowRemainDays(empLeave2.getAllowRemainDays()+Math.abs(needDeductRemainDays));
				// }
				// }
				// 分配使用情况
				for (EmpLeave leave : lastYearOvertimeLeaves) {
					leaveTotleDays = (leave.getUsedDays() != null ? leave.getUsedDays() : 0)
							+ (leave.getAllowRemainDays() != null ? leave.getAllowRemainDays() : 0);
					needDeductUsedDays = needDeductUsedDays - leaveTotleDays;
					if (needDeductUsedDays < 0) {
						leave.setUsedDays(leaveTotleDays + needDeductUsedDays);
						needDeductUsedDays = 0.0;
					}
				}
				if (needDeductUsedDays < 0) {
					throw new OaException("修改后去年剩余调休为负数,请核对后修改");
				}

				this.empLeaveMapper.updateById(lastYearOvertimeLeave);
				for (EmpLeave empLeave2 : lastYearOvertimeLeaves) {
					this.empLeaveMapper.updateById(empLeave2);
				}
			}
			break;
		case "4":
			// 其他
			json.addProperty("type", "其他");
			for (Integer leaveType : Arrays.asList(ConfigConstants.getOtherleaveType())) {
				param.setType(leaveType);
				EmpLeave oldLeave = this.getByCondition(param);
				if (null == oldLeave || oldLeave.getId() == null) {
					setUsedOtherLeaveDays(leaveType, empLeave, param, json);
					param.setDelFlag(0);
					this.empLeaveMapper.save(param);
					param.setId(null);
				} else {
					setUsedOtherLeaveDays(leaveType, empLeave, oldLeave, json);
					this.empLeaveMapper.updateById(oldLeave);
				}
			}
			break;
		default:
			break;
		}
		sysUpdateLog.setJsonInfo(json.toString());
		sysUpdateLog.setDelFlag(0);
		sysUpdateLogService.save(sysUpdateLog);
		result.put("sucess", true);
		result.put("msg", "假期数据修改成功");
		return result;
	}

	private void setUsedOtherLeaveDays(Integer leaveType, EmpLeave empLeave, EmpLeave param, JsonObject json)
			throws OaException {
		switch (leaveType) {
		case 3:
			json.addProperty("beforeLeave3", param.getLeave21() == null ? 0 : param.getLeave21());
			param.setUsedDays(empLeave.getLeave21());
			json.addProperty("afterLeave3", param.getUsedDays());
			break;
		case 4:
			json.addProperty("beforeLeave4", param.getLeave26() == null ? 0 : param.getLeave26());
			param.setUsedDays(empLeave.getLeave26());
			json.addProperty("afterLeave4", param.getUsedDays());
			break;
		case 6:
			json.addProperty("beforeLeave6", param.getLeave24() == null ? 0 : param.getLeave24());
			param.setUsedDays(empLeave.getLeave24());
			json.addProperty("afterLeave6", param.getUsedDays());
			break;
		case 7:
			json.addProperty("beforeLeave7", param.getLeave25() == null ? 0 : param.getLeave25());
			param.setUsedDays(empLeave.getLeave25());
			json.addProperty("afterLeave7", param.getUsedDays());
			break;
		case 8:
			json.addProperty("beforeLeave8", param.getLeave27() == null ? 0 : param.getLeave27());
			param.setUsedDays(empLeave.getLeave27());
			json.addProperty("afterLeave8", param.getUsedDays());
			break;
		case 9:
			json.addProperty("beforeLeave9", param.getLeave23() == null ? 0 : param.getLeave23());
			param.setUsedDays(empLeave.getLeave23());
			json.addProperty("afterLeave9", param.getUsedDays());
			break;
		case 10:
			json.addProperty("beforeLeave10", param.getLeave22() == null ? 0 : param.getLeave22());
			param.setUsedDays(empLeave.getLeave22());
			json.addProperty("afterLeave10", param.getUsedDays());
			break;
		case 11:
			json.addProperty("beforeLeave11", param.getLeave19() == null ? 0 : param.getLeave19());
			param.setUsedDays(empLeave.getLeave19());
			json.addProperty("afterLeave11", param.getUsedDays());
			break;
		case 12:
			json.addProperty("beforeLeave12", param.getLeave28() == null ? 0 : param.getLeave28());
			param.setUsedDays(empLeave.getLeave28());
			json.addProperty("afterLeave12", param.getUsedDays());
			break;
		default:
			break;
		}
	}

	@Override
	public PageModel<Map<String, Object>> getLeaveRadixList(Employee employee) {
		int page = employee.getPage() == null ? 0 : employee.getPage();
		int rows = employee.getRows() == null ? 0 : employee.getRows();

		PageModel<Map<String, Object>> pm = new PageModel<Map<String, Object>>();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		
		//查询指定员工类型数据
		employee.setEmpTypeIdList(configService.getNeedEmpTypeIdList());
		employee.setWorkAddressType(0);

		Integer total = getReprtTotal(employee);
		pm.setTotal(total);

		employee.setOffset(pm.getOffset());
		employee.setLimit(pm.getLimit());
		List<Employee> employees = employeeMapper.getPageList(employee);

		List<Map<String, Object>> employeeList = new ArrayList<Map<String, Object>>();
		EmpLeave empLeave = new EmpLeave();
		Integer currYear = Integer.parseInt(DateUtils.getYear(new Date()));// 当年
		empLeave.setType(ConfigConstants.LEAVE_TYPE_1);// 假期类型
		empLeave.setIsActive(ConfigConstants.IS_ACTIVE_INTEGER);// 有效的假期
		empLeave.setDelFlag(ConfigConstants.IS_NO_INTEGER);// 未删除
		empLeave.setEmployeeId(employee.getId());
		empLeave.setYear(currYear);
		for (Employee data : employees) {
			Map<String, Object> data1 = new HashMap<String, Object>();
			data1.put("id", data.getId());
			data1.put("code", data.getCode());
			data1.put("name", data.getCnName());
			data1.put("entryTime", data.getFirstEntryTime() != null
					? DateUtils.format(data.getFirstEntryTime(), DateUtils.FORMAT_SHORT) : null);
			data1.put("departName", data.getDepartName());
			data1.put("workTypeName", data.getWorkTypeName());
			empLeave.setEmployeeId(data.getId());
			List<EmpLeave> empLeaveList = empLeaveMapper.getListByCondition(empLeave);
			for (EmpLeave data2 : empLeaveList) {
				if (data2.getCategory().equals(ConfigConstants.CATEGORY_0)) {// 总年假天数
					data1.put("leaveCount", data2.getAllowDays());
				} else if (data2.getCategory().equals(ConfigConstants.CATEGORY_1)) {// 法定年假基数
					data1.put("legalCount", data2.getRaduix());
				} else if (data2.getCategory().equals(ConfigConstants.CATEGORY_2)) {// 福利年假基数
					data1.put("welfareCount", data2.getRaduix());
				}
			}
			employeeList.add(data1);
		}
		pm.setRows(employeeList);
		return pm;
	}

	@Override
	public Map<String, Object> queryEmpLeaveInfoByDate(Date quitTime, Long employeeId) throws Exception {
		Map<String, Object> result = Maps.newHashMap();

		EmpLeave param = new EmpLeave();
		// 今年
		Employee employee = this.employeeService.getById(employeeId);
		result.put("firstEntryTime", DateUtils.format(employee.getFirstEntryTime(), DateUtils.FORMAT_SHORT));
		param.setEmployeeId(employeeId);
		param.setYear(Integer.valueOf(DateUtils.format(new Date(), "yyyy")));
		param.setType(1);
		List<EmpLeave> yearLeaves = this.getByListCondition(param);
		Double legalRaduix = 0.0;
		Double welfareRaduix = 0.0;
		Double UsedDays = 0.0;
		Double UsedLeagalDays = 0.0;
		Double UsedWefareDays = 0.0;
		for (EmpLeave empLeave : yearLeaves) {
			if (empLeave.getCategory().intValue() == 0) {
				result.put("totalDays", empLeave.getAllowDays());
				result.put("totalAllowRemainDays", empLeave.getAllowRemainDays());
				UsedDays = empLeave.getUsedDays();
				result.put("totalUsedLeaveDays", empLeave.getUsedDays());
				result.put("totalBlockedDays", empLeave.getBlockedDays());
			} else if (empLeave.getCategory().intValue() == 1) {
				legalRaduix = empLeave.getRaduix();
				UsedLeagalDays = empLeave.getUsedDays();
				result.put("legalLeaveDays", empLeave.getAllowRemainDays() + empLeave.getBlockedDays());
			} else if (empLeave.getCategory().intValue() == 2) {
				welfareRaduix = empLeave.getRaduix();
				UsedWefareDays = empLeave.getUsedDays();
				result.put("welfareLeaveDays", empLeave.getAllowRemainDays() + empLeave.getBlockedDays());
			}
		}
		param.setType(2);
		if (this.getByListCondition(param).size() < 1) {
			result.put("sucess", false);
			result.put("msg", "此员工没有病假数据");
			return result;
		}
		EmpLeave sickLeave = null;
		EmpLeave firstSickLeave = this.getByListCondition(param).get(0);
		EmpLeave unSickLeave = null;
		EmpLeave secondSickLeave = null;
		if (this.getByListCondition(param).size() > 1) {
			secondSickLeave = this.getByListCondition(param).get(1);
			if (secondSickLeave.getCategory() == 2) {
				unSickLeave = secondSickLeave;
			} else if (firstSickLeave.getCategory() == 0) {
				sickLeave = secondSickLeave;
			}
		}
		if (firstSickLeave.getCategory() == 0) {
			sickLeave = firstSickLeave;
		} else if (firstSickLeave.getCategory() == 2) {
			unSickLeave = firstSickLeave;
		}

		result.put("totalSickDays", sickLeave.getAllowDays());
		result.put("allowPaidSickDays", sickLeave.getAllowRemainDays());
		result.put("uesdPaidSickDays", sickLeave.getAllowDays() - sickLeave.getAllowRemainDays());
		result.put("uesdUnPaidSickDays", unSickLeave != null ? unSickLeave.getUsedDays() : 0);// 非带薪病假
		result.put("cnName", employee.getCnName());
		result.put("code", employee.getCode());
		// 去年
		result.put("lastYearTotalAllowRemainDays", 0);
		result.put("lastYearLegalLeaveDays", 0);
		result.put("lastYearWelfareLeaveDays", 0);
		param.setType(1);
		param.setYear(Integer.valueOf(DateUtils.format(DateUtils.addYear(new Date(), -1), "yyyy")));
		List<EmpLeave> lastyearLeaves = this.getByListCondition(param);
		for (EmpLeave empLeave : lastyearLeaves) {
			if (empLeave.getCategory().intValue() == 0) {
				result.put("lastYearTotalAllowRemainDays", empLeave.getAllowRemainDays());
			} else if (empLeave.getCategory().intValue() == 1) {
				result.put("lastYearLegalLeaveDays", empLeave.getAllowRemainDays());
			} else if (empLeave.getCategory().intValue() == 2) {
				result.put("lastYearWelfareLeaveDays", empLeave.getAllowRemainDays());
			}
		}
		result.put("toQuitTotalLeaveDays", 0);
		result.put("toQuitTotalSickDays", 0);
		if (quitTime == null) {
			quitTime = employee.getQuitTime();
		}
		if (quitTime != null) {
			Integer intervalDays = DateUtils
					.getIntervalDays(DateUtils.isBefore(employee.getFirstEntryTime(), DateUtils.getYearBegin())
							? DateUtils.getYearBegin() : employee.getFirstEntryTime(), quitTime)
					+ 1;
			// 改过基数,年假计算就按查到的来
			// 未改过,动态计算
			if (employee.getAutoCalculateLeave() != null && employee.getAutoCalculateLeave() != 2
					&& employee.getAutoCalculateLeave() != 0) {
				// 员工未被修改过基数,动态算基数
				int joinYear = DateUtils.getJoinYear(employee.getFirstEntryTime(), quitTime).intValue();
				Double workYear = joinYear + employee.getBeforeWorkAge();
				// 获取司龄对应的年假基数
				Map<String, Object> leaveMap = this.getLeaveRaduixConfig();
				if (joinYear < 1) {
					joinYear = 1;
				}
				if (workYear < 10) {
					legalRaduix = 5.0;
					welfareRaduix = CommonUtils.converToDouble(leaveMap.get("5_" + joinYear));
				} else if (workYear >= 10 && workYear < 20) {
					legalRaduix = 10.0;
					welfareRaduix = CommonUtils.converToDouble(leaveMap.get("10_" + joinYear));
				} else if (workYear > 20) {
					legalRaduix = 15.0;
					welfareRaduix = 0.0;
				}
			}
			// 动态修改 法定 福利年假
			Double legalLeaveDays = this.getLeaveDays(intervalDays, legalRaduix) - UsedLeagalDays;
			Double welfareLeaveDays = this.getLeaveDays(intervalDays, welfareRaduix) - UsedWefareDays;
			result.put("legalLeaveDays", legalLeaveDays > 0 ? legalLeaveDays : 0);
			if (legalLeaveDays < 0) {
				welfareLeaveDays = welfareLeaveDays + legalLeaveDays;
			}
			result.put("welfareLeaveDays", (welfareLeaveDays) > 0 ? (welfareLeaveDays) : 0);
			// 当前可用总年假 截止离职日期总天数 - 已修 总病假
			result.put("totalAllowRemainDays",
					(this.getLeaveDays(intervalDays, legalRaduix) + this.getLeaveDays(intervalDays, welfareRaduix))
							- UsedDays);
			result.put("allowPaidSickDays", this.getLeaveDays(intervalDays, sickLeave.getRaduix())
					- (sickLeave.getAllowDays() - sickLeave.getAllowRemainDays()));
			// 离职当前可用总年假 总病假
			result.put("toQuitTotalLeaveDays",
					this.getLeaveDays(intervalDays, legalRaduix) + this.getLeaveDays(intervalDays, welfareRaduix));
			result.put("toQuitTotalSickDays", this.getLeaveDays(intervalDays, sickLeave.getRaduix()));
		}
		Map<String, Object> sickBlockDays = this.empLeaveMapper.getSickBlockDaysByEmpId(employeeId);
		Double totalSickBlockDays = (Double) sickBlockDays.get("sickBlockDays");
		Double paidSickBlockDay = 0.0;
		Double sickBlockDay = 0.0;
		if (totalSickBlockDays - sickLeave.getAllowRemainDays() > 0) {
			paidSickBlockDay = sickLeave.getAllowRemainDays();
			sickBlockDay = totalSickBlockDays - sickLeave.getAllowRemainDays();
		} else {
			paidSickBlockDay = totalSickBlockDays;
		}
		result.put("sickBlockDays", paidSickBlockDay);
		result.put("unSickBlockDays", sickBlockDay);
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Map<String, Object> updateLeaveRuduix(Double legalRaduix, Double welfareRaduix, Integer type,
			Long employeeId, String remarkRecord) throws Exception {
		Map<String, Object> result = Maps.newHashMap();
		Employee employee = this.employeeService.getById(employeeId);
		employee.setAutoCalculateLeave(type);
		this.employeeService.updateById(employee);
		EmpLeave param = new EmpLeave();
		param.setEmployeeId(employeeId);
		param.setYear(Integer.valueOf(DateUtils.format(new Date(), "yyyy")));
		param.setType(1);
		List<EmpLeave> listByCondition = this.empLeaveMapper.getListByCondition(param);
		EmpLeave total = new EmpLeave();
		EmpLeave legal = new EmpLeave();
		EmpLeave welfare = new EmpLeave();

		for (EmpLeave empLeave : listByCondition) {
			if (empLeave.getCategory().intValue() == 0) {
				total = empLeave;
			} else if (empLeave.getCategory().intValue() == 1) {
				legal = empLeave;
			} else if (empLeave.getCategory().intValue() == 2) {
				welfare = empLeave;
			}
		}
		// 添加基数修改日志
		SysUpdateLogTbl sysUpdateLog = new SysUpdateLogTbl();
		// 新增 备注记录
		if (StringUtils.isNotBlank(remarkRecord)) {
			sysUpdateLog.setRemarkRecord(remarkRecord);
		}
		sysUpdateLog.setDelFlag(0);
		sysUpdateLog.setResourceEmployeeId(employeeId);
		sysUpdateLog.setResourceType(2);
		User updateUser = this.userService.getCurrentUser();
		sysUpdateLog.setUpdateEmployeeId(updateUser.getEmployeeId());
		sysUpdateLog.setUpdateEmployeeName(updateUser.getEmployee().getCnName());
		JsonObject json = new JsonObject();
		json.addProperty("updateTime", DateUtils.format(new Date()));
		json.addProperty("beforeLegalRaduix", legal.getRaduix());
		json.addProperty("beforeWelfareRaduix", welfare.getRaduix());
		json.addProperty("afterLegalRaduix", legalRaduix);
		json.addProperty("afterWelfareRaduix", welfareRaduix);
		json.addProperty("updateType", type);
		json.addProperty("remarkRecord", remarkRecord);
		sysUpdateLog.setCreateTime(new Date());
		sysUpdateLog.setJsonInfo(json.toString());
		sysUpdateLog.setRemarkRecord(remarkRecord);
		this.sysUpdateLogService.save(sysUpdateLog);
		// 今年入职的员工
		if (StringUtils.equalsIgnoreCase(DateUtils.getYear(new Date()),
				DateUtils.getYear(employee.getFirstEntryTime()))) {
			Integer intervalDays = DateUtils.getIntervalDays(employee.getFirstEntryTime(),
					DateUtils.getYearLast(Integer.valueOf(DateUtils.getYear(new Date()))));
			legal.setAllowDays(this.getLeaveDays(intervalDays, legalRaduix));
			welfare.setAllowDays(this.getLeaveDays(intervalDays, welfareRaduix));
			total.setAllowDays(legal.getAllowDays() + welfare.getAllowDays());
		} else {
			total.setAllowDays(welfareRaduix + legalRaduix);
			legal.setAllowDays(legalRaduix);
			welfare.setAllowDays(welfareRaduix);
		}
		total.setRaduix(welfareRaduix + legalRaduix);
		legal.setRaduix(legalRaduix);
		welfare.setRaduix(welfareRaduix);
		// 占用的大于法定,法定剩余0
		if (total.getUsedDays() + total.getBlockedDays() - legal.getAllowDays() > 0) {
			legal.setAllowRemainDays(0d);
			welfare.setAllowRemainDays(total.getAllowDays() - total.getUsedDays() - total.getBlockedDays());
			if (total.getUsedDays() > legal.getAllowDays()) {
				legal.setUsedDays(legal.getAllowDays());
				legal.setBlockedDays(0d);
				welfare.setUsedDays(total.getUsedDays() - legal.getUsedDays());
				welfare.setBlockedDays(total.getBlockedDays());
			} else {
				legal.setUsedDays(total.getUsedDays());
				legal.setBlockedDays(legal.getAllowDays() - legal.getUsedDays());
				welfare.setUsedDays(0d);
				welfare.setBlockedDays(total.getBlockedDays() - legal.getBlockedDays());
			}
		} else {
			legal.setUsedDays(total.getUsedDays());
			legal.setBlockedDays(total.getBlockedDays());
			legal.setAllowRemainDays(legal.getAllowDays() - total.getUsedDays() - total.getBlockedDays());
			welfare.setUsedDays(0d);
			welfare.setBlockedDays(0d);
			welfare.setAllowRemainDays(welfare.getAllowDays());
		}
		this.empLeaveMapper.updateById(total);
		this.empLeaveMapper.updateById(legal);
		this.empLeaveMapper.updateById(welfare);
		result.put("sucess", true);
		result.put("msg", "修改成功");
		return result;
	}

	@Override
	public void leaveFailure() {
		// TODO Auto-generated method stub
		// 查出所有请假申请单 状态为100 如果 到了人事 则有效期为7天 其他为五天
		EmpApplicationLeave leaves = new EmpApplicationLeave();
		leaves.setApprovalStatus(100);
		List<EmpApplicationLeave> roles = empApplicationLeaveMapper.getReportPageList(leaves);
		for (EmpApplicationLeave og : roles) {
			try {
				if (og.getProcessInstanceId() != null) {
					List<Task> taskList = activitiServiceImpl.getInstanceTasks(og.getProcessInstanceId());
					if (taskList != null) {
						boolean isPersonnel = taskList.get(0).getName().contains("人事") ? true : false;
						if (annualVacationService.check5WorkingDayNextmonth(DateUtils.format(og.getStartTime()),
								isPersonnel)) {
							og.setApprovalStatus(ConfigConstants.OVERDUE_STATUS);// 已失效状态500
							empApplicationLeaveMapper.updateById(og);
						}
					}
				}
			} catch (Exception e) {
			}
		}
	}

	@Override
	public void Failure() {
		logger.info("失效定时开始");
		// TODO Auto-generated method stub
		// 查出所有请假申请单 状态为100 如果 到了人事 则有效期为7天 其他为五天
		EmpApplicationLeave leaves = new EmpApplicationLeave(); // 前端以过滤掉 500的状态
																// 在 我的待办加个失效
																//  后台加个 失效查询
		EmpApplicationLeaveAbolish leaveAbolish=new EmpApplicationLeaveAbolish();  //新增销假定时	
		EmpApplicationOutgoing outgoing = new EmpApplicationOutgoing();
		EmpApplicationBusiness business = new EmpApplicationBusiness();// 出差
		EmpApplicationAbnormalAttendance abnormalAttendance = new EmpApplicationAbnormalAttendance();
		EmpApplicationOvertime overtime = new EmpApplicationOvertime();
		RemoveSubordinateAbsence  subAbsence=new RemoveSubordinateAbsence();
		// 排班 值班跟上面的规则不一样
		ApplicationEmployeeClass empclass = new ApplicationEmployeeClass();
		ApplicationEmployeeDuty empduty = new ApplicationEmployeeDuty();

		leaves.setApprovalStatus(100);
		leaveAbolish.setApprovalStatus(100);
		outgoing.setApprovalStatus(100);
		business.setApprovalStatus(100);
		abnormalAttendance.setApprovalStatus(100);
		overtime.setApprovalStatus(100);
		// 排班 值班 规则不一样
		empclass.setApprovalStatus(100);
		empduty.setApprovalStatus(100);
		subAbsence.setApproalStatus(100L);
		List<EmpApplicationLeave> leavesroles = empApplicationLeaveMapper.getReportPageList(leaves);
		List<EmpApplicationLeaveAbolish> leaveAbolishsroles = empApplicationLeaveAbolishMapper.getReportPageList(leaveAbolish);
		List<EmpApplicationOutgoing> outgoingroles = empApplicationOutgoingMapper.getReportPageList(outgoing);
		List<EmpApplicationBusiness> businessroles = empApplicationBusinessMapper.getReportPageList(business);
		List<EmpApplicationBusiness> businessRepotroles = empApplicationBusinessMapper.getUnCompleteReportList();
		List<EmpApplicationAbnormalAttendance> abnormalAttendanceroles = empApplicationAbnormalAttendanceMapper
				.getReportPageList(abnormalAttendance);
		List<EmpApplicationOvertime> overtimeroles = empApplicationOvertimeMapper.getReportPageList(overtime);
		List<RemoveSubordinateAbsence> subAbsenceroles = removeSubordinateAbsenceMapper.getPageList(subAbsence);
		
		// 排班 值班 规则不一样
		/*
		 * List<ApplicationEmployeeClass> empclassroles =
		 * employeeClassMapper.getReportPageList(empclass);
		 * List<ApplicationEmployeeDuty> empdutyroles =
		 * employeeDutyMapper.getReportPageList(empduty);
		 */
		
		//   employeeDutyMapper.selectByCondition(empduty);
		//消下属异常考勤失效
		for (RemoveSubordinateAbsence og : subAbsenceroles) {
			try {
				if (og.getProcessinstanceId() != null && !"".equals(og.getProcessinstanceId())) {
					List<Task> taskList = activitiServiceImpl.getInstanceTasks(og.getProcessinstanceId());
					if (taskList != null && taskList.size() > 0) {
						boolean isPersonnel = taskList.get(0).getName().contains("人事") ? true : false;
						if (annualVacationService.check5WorkingDayNextmonth(DateUtils.format(og.getAttendanceDate()),
								isPersonnel)) {
							og.setApproalStatus(500L);// 已失效状态500
							removeSubordinateAbsenceMapper.updateById(og);
							logger.error("消下属异常考勤 失效修改 流程id"+og.getProcessinstanceId());
						}
					}
				}
			} catch (Exception e) {
			}
		}
		
		List<ApplicationEmployeeDuty> empDutyroles = applicationEmployeeDutyMapper.getByCondition(empduty);
		//值班失效
		for (ApplicationEmployeeDuty og : empDutyroles) {
			try {
				if (og.getProcessInstanceId() != null && !"".equals(og.getProcessInstanceId())) {
					List<Task> taskList = activitiServiceImpl.getInstanceTasks(og.getProcessInstanceId());
					if (taskList != null && taskList.size() > 0) {
						boolean isPersonnel = taskList.get(0).getName().contains("人事") ? true : false;
						//看假期在哪个法定节假日内 取节假日最后时间作为 时间节点
						AnnualVacation vacation=new AnnualVacation();
						vacation.setSubject(og.getVacationName());
						vacation.setYear(Integer.parseInt(og.getYear()));
						List<AnnualVacation> vacationList = annualVacationMapper.getListByCondition(vacation);
						if(vacationList!=null && vacationList.size()>0){
							Date annualLastDate= vacationList.get(vacationList.size()-1).getAnnualDate();
							if (annualVacationService.check5WorkingDayNextmonth(DateUtils.format(annualLastDate),
									isPersonnel)) {
								og.setApprovalStatus(ConfigConstants.OVERDUE_STATUS);// 已失效状态500
								applicationEmployeeDutyMapper.updateById(og);
								logger.error("值班 失效修改 流程id"+og.getProcessInstanceId());
							}
						}
					}
				}
			} catch (Exception e) {
			}
		}
		
		
		//假期失效
		for (EmpApplicationLeave og : leavesroles) {
			try {
				List<EmpApplicationLeaveDetail> detailList = empApplicationLeaveDetailMapper.getListByCondition(og.getId());
				//产假不做失效处理
				if (detailList!=null&&detailList.size()>0&&detailList.get(0).getLeaveType()!=7
						&&og.getProcessInstanceId() != null && !"".equals(og.getProcessInstanceId())) {
					List<Task> taskList = activitiServiceImpl.getInstanceTasks(og.getProcessInstanceId());
					if (taskList != null && taskList.size() > 0) {
						boolean isPersonnel = taskList.get(0).getName().contains("人事") ? true : false;
						if (annualVacationService.check5WorkingDayNextmonth(DateUtils.format(og.getStartTime()),
								isPersonnel)) {
							og.setApprovalStatus(ConfigConstants.OVERDUE_STATUS);// 已失效状态500
							empApplicationLeaveMapper.updateById(og);
							logger.error("请假 失效修改 流程id"+og.getProcessInstanceId());
						}
					}
				}
			} catch (Exception e) {

			}
		}
		//销假失效
		for (EmpApplicationLeaveAbolish og : leaveAbolishsroles) {
			try {
				if (og.getProcessInstanceId() != null && !"".equals(og.getProcessInstanceId())) {
					List<Task> taskList = activitiServiceImpl.getInstanceTasks(og.getProcessInstanceId());
					if (taskList != null && taskList.size() > 0) {
						boolean isPersonnel = taskList.get(0).getName().contains("人事") ? true : false;
						if (annualVacationService.check5WorkingDayNextmonth(DateUtils.format(og.getStartTime()),
								isPersonnel)) {
							og.setApprovalStatus(ConfigConstants.OVERDUE_STATUS);// 已失效状态500
							empApplicationLeaveAbolishMapper.updateById(og);
							logger.error("请假 失效修改 流程id"+og.getProcessInstanceId());
						}
					}
				}
			} catch (Exception e) {

			}
		}
		
		// 外出
		for (EmpApplicationOutgoing og : outgoingroles) {
			try {
				if (og.getProcessInstanceId() != null && !"".equals(og.getProcessInstanceId())) {
					List<Task> taskList = activitiServiceImpl.getInstanceTasks(og.getProcessInstanceId());
					if (taskList != null && taskList.size() > 0) {
						boolean isPersonnel = taskList.get(0).getName().contains("人事") ? true : false;
						if (annualVacationService.check5WorkingDayNextmonth(DateUtils.format(og.getStartTime()),
								isPersonnel)) {
							og.setApprovalStatus(ConfigConstants.OVERDUE_STATUS);// 已失效状态500
							empApplicationOutgoingMapper.updateById(og);
							logger.error("外出 失效修改 流程id"+og.getProcessInstanceId());
						}
					}
				}
			} catch (Exception e) {

			}
		}
		// 出差
		for (EmpApplicationBusiness og : businessroles) {
			try {
				if (og.getProcessinstanceId() != null && !"".equals(og.getProcessinstanceId())) {
					List<Task> taskList = activitiServiceImpl.getInstanceTasks(og.getProcessinstanceId());
					if (taskList != null && taskList.size() > 0) {
						boolean isPersonnel = taskList.get(0).getName().contains("人事") ? true : false;
						if (annualVacationService.check5WorkingDayNextmonth(DateUtils.format(og.getStartTime()),
								isPersonnel)) {
							og.setApprovalStatus(ConfigConstants.OVERDUE_STATUS);// 已失效状态500
							empApplicationBusinessMapper.updateById(og);
							logger.error("出差 失效修改 流程id"+og.getProcessinstanceId());
						}
					}
				}
			} catch (Exception e) {

			}
		}
		// 出差报告
		for (EmpApplicationBusiness og : businessRepotroles) {
			try {
				if (og.getProcessinstanceReportId() != null && !"".equals(og.getProcessinstanceReportId())) {
					List<Task> taskList = activitiServiceImpl.getInstanceTasks(og.getProcessinstanceReportId());
					if (taskList != null && taskList.size() > 0) {
						boolean isPersonnel = false;
						if (annualVacationService.check5WorkingDayNextmonth(DateUtils.format(og.getStartTime()),
								isPersonnel)) {
							og.setApprovalReportStatus(ConfigConstants.OVERDUE_STATUS);// 已失效状态500
																						// 改相对于的report
																						// 状态
							empApplicationBusinessMapper.updateById(og);
							logger.error("出差报告 失效修改 流程id"+og.getProcessinstanceReportId());
						}
					}
				}
			} catch (Exception e) {

			}
		}
		// 考勤
		for (EmpApplicationAbnormalAttendance og : abnormalAttendanceroles) {
			try {
				if (og.getProcessInstanceId() != null && !"".equals(og.getProcessInstanceId())) {
					List<Task> taskList = activitiServiceImpl.getInstanceTasks(og.getProcessInstanceId());
					if (taskList != null && taskList.size() > 0) {
						boolean isPersonnel = taskList.get(0).getName().contains("人事") ? true : false;
						if (annualVacationService.check5WorkingDayNextmonth(DateUtils.format(og.getStartTime()),
								isPersonnel)) {
							og.setApprovalStatus(ConfigConstants.OVERDUE_STATUS);// 已失效状态500
							empApplicationAbnormalAttendanceMapper.updateById(og);
							logger.error("考勤 失效修改 流程id"+og.getProcessInstanceId());
						}
					}
				}
			} catch (Exception e) {

			}
		}
		// 加班
		for (EmpApplicationOvertime og : overtimeroles) {
			try {
				if (og.getProcessInstanceId() != null && !"".equals(og.getProcessInstanceId())) {
					List<Task> taskList = activitiServiceImpl.getInstanceTasks(og.getProcessInstanceId());
					if (taskList != null && taskList.size() > 0) {
						boolean isPersonnel = taskList.get(0).getName().contains("人事") ? true : false;
						if (og.getExpectStartTime() != null) {
							if (annualVacationService.check5WorkingDayNextmonth(
									DateUtils.format(og.getExpectStartTime()), isPersonnel)) {
								og.setApprovalStatus(ConfigConstants.OVERDUE_STATUS);// 已失效状态500
								empApplicationOvertimeMapper.updateById(og);
								logger.error("延时工作 失效修改 流程id"+og.getProcessInstanceId());
							}
						}
					}
				}
			} catch (Exception e) {

			}
		}
		logger.info("失效定时结束");
	}

	@Override
	public List<EmpLeave> getReaminLeaveList(EmpLeave record) {
		return empLeaveMapper.getReaminLeaveList(record);
	}

	@Override
	public void deleteOtherLeaveAfter2018() {
		empLeaveMapper.deleteOtherLeaveAfter2018();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String importEmpLeaveList(MultipartFile file) throws OaException, Exception {
		logger.info("导入员工假期数据-----开始");
		try {
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
			List<List<Object>> excelList = ExcelUtil.readExcel(file, fileName, 9);
			int count = 0; // 插入总条数
			int failCount = 0; // 失败条数
			StringBuffer msg = new StringBuffer(); // 返回信息
			List<LinkedList<String>> excel = (List<LinkedList<String>>) (List) excelList;
			if (excel == null || excel.size() < 2) { // 不包括表头
				logger.error(fileName + "文件内容为空");
				throw new OaException(fileName + "文件内容为空");
			}
			count = excel.size() - 1;
			for (int i = 1; i < excel.size(); i++) {
				// 获得行数据
				LinkedList<String> linkedList = excel.get(i);
				// 非空校验
				if (StringUtils.isBlank(linkedList.get(0))) {
					msg.append(" \n 第" + i + "条数据：员工编号不能为空");
					failCount++;
					continue;
				}
				if (StringUtils.isBlank(linkedList.get(1))) {
					msg.append(" \n 第" + i + "条数据：中文名不能为空");
					failCount++;
					continue;
				}
				if (StringUtils.isBlank(linkedList.get(2))) {
					msg.append(" \n 第" + i + "条数据：法定年假总数不能为空");
					failCount++;
					continue;
				}
				if (StringUtils.isBlank(linkedList.get(3))) {
					msg.append(" \n 第" + i + "条数据：福利年假总数不能为空");
					failCount++;
					continue;
				}
				if (StringUtils.isBlank(linkedList.get(4))) {
					msg.append(" \n 第" + i + "条数据：今年年假合计不能为空");
					failCount++;
					continue;
				}
				if (StringUtils.isBlank(linkedList.get(5))) {
					msg.append(" \n 第" + i + "条数据：已用年假不能为空");
					failCount++;
					continue;
				}
				if (StringUtils.isBlank(linkedList.get(6))) {
					msg.append(" \n 第" + i + "条数据：带薪病假总数不能为空");
					failCount++;
					continue;
				}
				if (StringUtils.isBlank(linkedList.get(7))) {
					msg.append(" \n 第" + i + "条数据：已用病假不能为空");
					failCount++;
					continue;
				}
				if (StringUtils.isBlank(linkedList.get(8))) {
					msg.append(" \n 第" + i + "条数据：截止上月底调休剩余小时不能为空");
					failCount++;
					continue;
				}
				// 去除表格中的空格
				linkedList = replaceBlank(linkedList);
				Long empId = empLeaveMapper.getEmployeeId(linkedList.get(0));
				if (null == empId) {
					msg.append(" \n 第" + i + "条数据：该员工不存在于系统中");
					failCount++;
					continue;
				}
				// 导入的假期数据都是未存在于表中的员工(只看当年的年假调休病假)
				int countEmployee = empLeaveMapper.getCountEmployee(empId,Integer.parseInt(DateUtils.getYear(new Date())));
				if (countEmployee > 0) {
					msg.append(" \n 第" + i + "条数据：该员工已有假期数据，请勿重复导入");
					failCount++;
					continue;
				}
				// 获取系统时间
				Date now = new Date();
				// 得到次年三月三十一号的日期
				// 导入文件为上个月的数据,这里的year是上个月的
				String lastMonthDate = DateUtils.getLastYearMonth(now);
				String yearStr = lastMonthDate.split("-")[0];
				String monthStr = lastMonthDate.split("-")[1];
				int year = Integer.parseInt(yearStr);
				int month = Integer.parseInt(monthStr);

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				// 获得年初的日期
				String beginYearStr = year + "-01-01 00:00:00";
				Date beginYearDate = sdf.parse(beginYearStr);
				// 获得年底的日期
				String endYearStr = year + "-12-31 23:59:59";
				Date endYearDate = sdf.parse(endYearStr);
				// 获得次年三月三十一日的日期
				String dateStr = (year + 1) + "-03-31 23:59:59";
				Date nextYearDate = sdf.parse(dateStr);

				// 获取员工入职时间
				Date empEntryTime = empLeaveMapper.getEntryTime(empId);
				int entryYear = Integer.parseInt(DateUtils.getYear(empEntryTime));
				//判断是否是今年之前入职的
				boolean isThisYearEntry = entryYear >= year;
				// 调休数据初始化
				// 1.保存当年调休剩余(1)
				EmpLeave empRestLeave = new EmpLeave();
				empRestLeave.setCompanyId(1L);
				empRestLeave.setEmployeeId(empId);
				empRestLeave.setYear(year);
				empRestLeave.setType(5);
				empRestLeave.setAllowDays(0D);
				empRestLeave.setActualDays(0D);
				empRestLeave.setUsedDays(0D);
				empRestLeave.setAllowRemainDays(Double.parseDouble(linkedList.get(8)));
				empRestLeave.setCategory(ConfigConstants.CATEGORY_0);
				empRestLeave.setParendId(0L);
				// 调休有效期入职到次年3月31日
				empRestLeave.setStartTime(isThisYearEntry ? empEntryTime : beginYearDate);
				empRestLeave.setEndTime(nextYearDate);
				empRestLeave.setIsActive(DateUtils.isBefore(now, nextYearDate) ? 0 : 1);// 0有效1无效
				empRestLeave.setCreateTime(now);
				empRestLeave.setCreateUser(currentUser.getEmployeeId() + "");
				empRestLeave.setUpdateTime(now);
				empRestLeave.setUpdateUser(currentUser.getEmployeeId() + "");
				empRestLeave.setDelFlag(0);
				empLeaveMapper.save(empRestLeave);
				// 保存当年调休剩余(2)
				EmpLeave empRestLeave2 = new EmpLeave();
				empRestLeave2.setCompanyId(1L);
				empRestLeave2.setEmployeeId(empId);
				empRestLeave2.setYear(month);
				empRestLeave2.setType(ConfigConstants.LEAVE_TYPE_5);
				empRestLeave2.setAllowDays(0D);
				empRestLeave2.setActualDays(0D);
				empRestLeave2.setUsedDays(0D);
				empRestLeave2.setAllowRemainDays(Double.parseDouble(linkedList.get(8)));
				empRestLeave2.setCategory(1);
				empRestLeave2.setParendId(empRestLeave.getId());
				// 调休有效期2入职到次年3月31日
				empRestLeave2.setStartTime(isThisYearEntry ? empEntryTime : beginYearDate);
				empRestLeave2.setEndTime(nextYearDate);
				empRestLeave2.setIsActive(DateUtils.isBefore(now, nextYearDate) ? 0 : 1);// 0有效1无效
				empRestLeave2.setCreateTime(now);
				empRestLeave2.setCreateUser(currentUser.getEmployeeId() + "");
				empRestLeave2.setUpdateTime(now);
				empRestLeave2.setUpdateUser(currentUser.getEmployeeId() + "");
				empRestLeave2.setDelFlag(0);
				empLeaveMapper.save(empRestLeave2);
				// 2.带薪病假数据初始化
				EmpLeave sickLeave = new EmpLeave();
				sickLeave.setCompanyId(1L);
				sickLeave.setEmployeeId(empId);
				sickLeave.setRaduix(ConfigConstants.DEFAULT_SICK);
				sickLeave.setYear(year);
				sickLeave.setType(ConfigConstants.LEAVE_TYPE_2);
				sickLeave.setActualDays(0D);
				// 病假使用天数如果大于带薪病假天数则设置使用天数为带薪病假天数
				sickLeave.setUsedDays(Double.parseDouble(linkedList.get(7)) < Double.parseDouble(linkedList.get(6))
						? Double.parseDouble(linkedList.get(7)) : Double.parseDouble(linkedList.get(6)));
				sickLeave.setAllowDays(Double.parseDouble(linkedList.get(6)));
				// 病假使用天数如果大于带薪病假天数则设置允许使用剩余带薪病假天数为0
				sickLeave.setAllowRemainDays(
						Double.parseDouble(linkedList.get(7)) < Double.parseDouble(linkedList.get(6))
								? Double.parseDouble(linkedList.get(6)) - Double.parseDouble(linkedList.get(7)) : 0D);
				sickLeave.setCategory(ConfigConstants.CATEGORY_0);
				sickLeave.setParendId(0L);
				// 病假有效期入职三个月起到当年年底
				sickLeave.setStartTime(isThisYearEntry ? empEntryTime : beginYearDate);
				sickLeave.setEndTime(endYearDate);
				sickLeave.setIsActive(DateUtils.isBefore(now, endYearDate) ? 0 : 1);// 0有效1无效
				sickLeave.setCreateTime(now);
				sickLeave.setCreateUser(currentUser.getEmployeeId() + "");
				sickLeave.setUpdateTime(now);
				sickLeave.setUpdateUser(currentUser.getEmployeeId() + "");
				sickLeave.setDelFlag(0);
				empLeaveMapper.save(sickLeave);
				// 2.1非带薪病假初始化
				EmpLeave sickLeave2 = new EmpLeave();
				sickLeave2.setCompanyId(1L);
				sickLeave2.setEmployeeId(empId);
				sickLeave2.setYear(year);
				sickLeave2.setType(ConfigConstants.LEAVE_TYPE_2);
				sickLeave2.setActualDays(0D);
				// 病假使用天数如果大于带薪病假天数则设置使用天数为带薪病假天数
				sickLeave2
						.setUsedDays(Double.parseDouble(linkedList.get(7)) < Double.parseDouble(linkedList.get(6)) ? 0D
								: Double.parseDouble(linkedList.get(7)) - Double.parseDouble(linkedList.get(6)));
				sickLeave2.setCategory(ConfigConstants.CATEGORY_2);
				sickLeave2.setParendId(sickLeave.getId());
				sickLeave2.setCreateTime(now);
				sickLeave2.setCreateUser(currentUser.getEmployeeId() + "");
				sickLeave2.setUpdateTime(now);
				sickLeave2.setUpdateUser(currentUser.getEmployeeId() + "");
				sickLeave2.setIsActive(0);
				sickLeave2.setDelFlag(0);
				empLeaveMapper.save(sickLeave2);
				// 3.年假数据初始化
				// ---初始化法定年假基数
				Double legalRaduix = Double.parseDouble(linkedList.get(2)) < 5 ? 5
						: Double.parseDouble(linkedList.get(2));
				// ---初始化福利年假基数
				Double benefitsRaduix = Double.parseDouble(linkedList.get(3)) < 5 ? 5
						: Double.parseDouble(linkedList.get(3));
				EmpLeave annualLeave = new EmpLeave();
				annualLeave.setCompanyId(1L);
				annualLeave.setEmployeeId(empId);
				annualLeave.setYear(year);
				annualLeave.setType(ConfigConstants.LEAVE_TYPE_1);
				annualLeave.setRaduix(legalRaduix + benefitsRaduix);
				annualLeave.setAllowDays(Double.parseDouble(linkedList.get(4)));
				annualLeave.setActualDays(0D);
				annualLeave.setUsedDays(Double.parseDouble(linkedList.get(5)));
				annualLeave.setBlockedDays(0D);
				annualLeave.setAllowRemainDays(
						Double.parseDouble(linkedList.get(4)) - Double.parseDouble(linkedList.get(5)));
				if (annualLeave.getAllowRemainDays() < 0) {
					annualLeave.setAllowRemainDays(0D);
				}
				annualLeave.setCategory(ConfigConstants.CATEGORY_0);
				annualLeave.setParendId(0L);
				// 年假有效期
				annualLeave.setStartTime(isThisYearEntry ? empEntryTime : beginYearDate);
				annualLeave.setEndTime(nextYearDate);
				annualLeave.setIsActive(DateUtils.isBefore(now, annualLeave.getEndTime()) ? 0 : 1);// 0有效1无效
				annualLeave.setCreateTime(now);
				annualLeave.setCreateUser(currentUser.getEmployeeId() + "");
				annualLeave.setUpdateTime(now);
				annualLeave.setUpdateUser(currentUser.getEmployeeId() + "");
				annualLeave.setDelFlag(0);
				empLeaveMapper.save(annualLeave);
				// 3.1初始化法定年假
				EmpLeave legalAnnualLeave = new EmpLeave();
				legalAnnualLeave.setCompanyId(1L);
				legalAnnualLeave.setEmployeeId(empId);
				legalAnnualLeave.setYear(year);
				legalAnnualLeave.setType(ConfigConstants.LEAVE_TYPE_1);
				legalAnnualLeave.setRaduix(legalRaduix);
				legalAnnualLeave.setAllowDays(Double.parseDouble(linkedList.get(2)));
				legalAnnualLeave.setActualDays(0D);
				// 年假扣减先扣法定，使用天数如果大于法定天数则设置使用天数为法定天数反之设为使用天数
				legalAnnualLeave
						.setUsedDays(Double.parseDouble(linkedList.get(5)) > Double.parseDouble(linkedList.get(2))
								? Double.parseDouble(linkedList.get(2)) : Double.parseDouble(linkedList.get(5)));
				legalAnnualLeave.setBlockedDays(0D);
				// 年假扣减先扣法定，使用天数如果大于法定天数则设置允许使用剩余天数为0反之设为法定天数减去使用天数
				legalAnnualLeave.setAllowRemainDays(
						Double.parseDouble(linkedList.get(5)) > Double.parseDouble(linkedList.get(2)) ? 0
								: Double.parseDouble(linkedList.get(2)) - Double.parseDouble(linkedList.get(5)));
				// 剩余可用天数最小为0
				if (legalAnnualLeave.getAllowRemainDays() < 0) {
					legalAnnualLeave.setAllowRemainDays(0D);
				}
				legalAnnualLeave.setCategory(ConfigConstants.CATEGORY_1);
				legalAnnualLeave.setParendId(annualLeave.getId());
				// 法定年假有效期
				legalAnnualLeave.setStartTime(isThisYearEntry ? empEntryTime : beginYearDate);
				legalAnnualLeave.setEndTime(nextYearDate);
				legalAnnualLeave.setIsActive(DateUtils.isBefore(now, annualLeave.getEndTime()) ? 0 : 1);// 0有效1无效
				legalAnnualLeave.setCreateTime(now);
				legalAnnualLeave.setCreateUser(currentUser.getEmployeeId() + "");
				legalAnnualLeave.setUpdateTime(now);
				legalAnnualLeave.setUpdateUser(currentUser.getEmployeeId() + "");
				legalAnnualLeave.setDelFlag(0);
				empLeaveMapper.save(legalAnnualLeave);
				// 3.2初始化福利年假
				EmpLeave benefitsLeave = new EmpLeave();
				benefitsLeave.setCompanyId(1L);
				benefitsLeave.setEmployeeId(empId);
				benefitsLeave.setYear(year);
				benefitsLeave.setType(ConfigConstants.LEAVE_TYPE_1);
				benefitsLeave.setRaduix(benefitsRaduix);
				benefitsLeave.setAllowDays(Double.parseDouble(linkedList.get(3)));
				benefitsLeave.setActualDays(0D);
				// 年假扣减先扣法定，使用天数如果大于法定天数则设置福利年假使用天数为使用天数减去法定天数反之设为0
				benefitsLeave.setUsedDays(Double.parseDouble(linkedList.get(5)) > Double.parseDouble(linkedList.get(2))
						? Double.parseDouble(linkedList.get(5)) - Double.parseDouble(linkedList.get(2)) : 0);
				benefitsLeave.setBlockedDays(0D);
				// 年假扣减先扣法定，使用天数如果大于法定天数则设置福利年假剩余允许使用福利年假=福利年假-(使用天数-法定年假)为0反之设为福利年假天数
				benefitsLeave.setAllowRemainDays(
						Double.parseDouble(linkedList.get(5)) > Double.parseDouble(linkedList.get(2))
								? Double.parseDouble(linkedList.get(3)) - (Double.parseDouble(linkedList.get(5))
										- Double.parseDouble(linkedList.get(2)))
								: Double.parseDouble(linkedList.get(3)));
				// 剩余可用天数最小为0
				if (benefitsLeave.getAllowRemainDays() < 0) {
					benefitsLeave.setAllowRemainDays(0D);
				}
				benefitsLeave.setCategory(ConfigConstants.CATEGORY_2);
				benefitsLeave.setParendId(annualLeave.getId());
				// 福利年假有效期
				benefitsLeave.setStartTime(isThisYearEntry ? empEntryTime : beginYearDate);
				benefitsLeave.setEndTime(nextYearDate);
				benefitsLeave.setIsActive(DateUtils.isBefore(now, annualLeave.getEndTime()) ? 0 : 1);// 0有效1无效
				benefitsLeave.setCreateTime(now);
				benefitsLeave.setCreateUser(currentUser.getEmployeeId() + "");
				benefitsLeave.setUpdateTime(now);
				benefitsLeave.setUpdateUser(currentUser.getEmployeeId() + "");
				benefitsLeave.setDelFlag(0);
				empLeaveMapper.save(benefitsLeave);
				// 如果已用年假使用天数大于0则生成年假流水
				if (annualLeave.getUsedDays() > 0) {
					// 1.生成流水总记录
					Long recordId = initLeaveRecord(annualLeave, 0, ConfigConstants.LEAVE_KEY);
					// 2.1生成法定明细
					initRecordDetail(recordId, legalAnnualLeave.getId(), legalAnnualLeave.getUsedDays(),
							currentUser.getEmployeeId() + "");
					// 2.2判断已用年假是否大于法定年假
					if (annualLeave.getUsedDays() > legalAnnualLeave.getUsedDays()) {
						// 生成福利年假流水明细
						initRecordDetail(recordId, benefitsLeave.getId(), benefitsLeave.getUsedDays(),
								currentUser.getEmployeeId() + "");
					}
				}
				// 如果带薪病假大于0则生成带薪病假流水
				if (sickLeave.getUsedDays() > 0) {
					Long recordId = initLeaveRecord(sickLeave, 0, ConfigConstants.LEAVE_KEY);
					// 保存明细
					initRecordDetail(recordId, sickLeave.getId(), sickLeave.getUsedDays(),
							currentUser.getEmployeeId() + "");
				}
				// 如果非带薪病假大于0则生成非带薪病假流水
				if (sickLeave2.getUsedDays() > 0) {
					Long recordId = initLeaveRecord(sickLeave2, 0, ConfigConstants.LEAVE_KEY);
					// 保存明细
					initRecordDetail(recordId, sickLeave2.getId(), sickLeave2.getUsedDays(),
							currentUser.getEmployeeId() + "");
				}
				// 生成加班流水
				if (empRestLeave.getAllowRemainDays() > 0) {
					Long recordId = initLeaveRecord(empRestLeave, 1, ConfigConstants.OVERTIME_KEY);
					// 生成加班流水明细
					initRecordDetail(recordId, empRestLeave2.getId(), empRestLeave2.getAllowRemainDays(),
							currentUser.getEmployeeId() + "");
				}
			}
			msg.append(" \n 本次共导入" + count + "条数据，导入失败" + failCount + "条");
			logger.info("导入员工假期数据-----结束");
			return msg.toString();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new RuntimeException(e.getMessage());
		}

	}

	/**
	 * 初始化假期流水
	 * 
	 * @param planLeave
	 * @return
	 */
	public Long initLeaveRecord(EmpLeave planLeave, int unit, String billType) {
		LeaveRecord record = new LeaveRecord();
		record.setEmployeeId(planLeave.getEmployeeId());
		record.setType(planLeave.getType());
		// 加班设置天数为剩余调休时间
		if (billType.equals(ConfigConstants.OVERTIME_KEY)) {
			record.setDays(planLeave.getAllowRemainDays());
		} else {
			// 假期设置天数为使用天数
			record.setDays(planLeave.getUsedDays());
		}
		record.setBillId(0L);
		record.setBillType(billType);
		record.setDaysUnit(unit);// 单位
		record.setCreateTime(new Date());
		record.setCreateUser(planLeave.getCreateUser());
		record.setDelFlag(0);
		record.setSource(0);
		record.setRemark("老数据导入");
		leaveRecordMapper.save(record);
		return record.getId();
	}

	/**
	 * 初始化假期流水详情
	 * 
	 * @param recordId
	 * @param baseEmpLeaveId
	 * @param days
	 * @param optUser
	 */
	public void initRecordDetail(Long recordId, Long baseEmpLeaveId, Double days, String optUser) {
		LeaveRecordDetail recordDetail = new LeaveRecordDetail();
		recordDetail.setLeaveRecordId(recordId);
		recordDetail.setBaseEmpLeaveId(baseEmpLeaveId);
		recordDetail.setDays(days);
		recordDetail.setCreateTime(new Date());
		recordDetail.setCreateUser(optUser);
		recordDetail.setDelFlag(0);
		List<LeaveRecordDetail> isExist = leaveRecordDetailMapper.selectByCondition(recordDetail);
		if (isExist != null && isExist.size() > 0) {
			isExist.get(0).setDays(isExist.get(0).getDays() + days);
			leaveRecordDetailMapper.updateById(isExist.get(0));
		} else {
			leaveRecordDetailMapper.save(recordDetail);
		}
	}

	/**
	 * 去除所有空格回车换行符制表符
	 * 
	 * @param str
	 * @return
	 */
	public static LinkedList<String> replaceBlank(LinkedList<String> linkedList) {
		String dest = "";
		LinkedList<String> resultList = new LinkedList<String>();
		if (linkedList != null && linkedList.size() > 0) {
			for (String str : linkedList) {
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
	public HSSFWorkbook exportLeaveRemainReport(Employee emp) {
		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
		if(deptDataByUserList==null || deptDataByUserList.size()==0){
			return null;
		}else{
			emp.setCurrentUserDepart(deptDataByUserList);//数据权限
			List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(currentUser.getEmployeeId());//下属员工
			if(subEmployeeIdList!=null&&subEmployeeIdList.size()>0){
				emp.setSubEmployeeIdList(subEmployeeIdList);
			}
		    // 封装部门
			Integer firstDepart = emp.getFirstDepart();// 页面上一级部门
			Integer secondDepart = emp.getSecondDepart();// 页面上二级部门
			List<Integer> departList = new ArrayList<Integer>();
			if (null != secondDepart) {// 选择了二级部门
				departList.add(secondDepart);
			} else if (null == secondDepart && null != firstDepart) {// 只选择了一级部门
				// 根据一级部门将一级部门下面的所有二级部门查询出来
				departList.add(firstDepart);
				List<Depart> departs = departService.getByParentId(Long.valueOf(firstDepart + ""));
				departs.forEach((Depart dp) -> {
					departList.add(Integer.parseInt(dp.getId() + ""));
				});
			}
			emp.setDepartList(departList);
			emp.setYear(Integer.parseInt(DateUtils.getYear(new Date())));
			//查询指定员工类型数据
			emp.setEmpTypeIdList(configService.getNeedEmpTypeIdList());
			emp.setWorkAddressType(0);
			
			List<EmpLeave> list = getReportList(emp);
			List<Map<String, Object>> datas = convertExportList(list);// 将查询的结果转换成excle需要的类型
	
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("剩余假期");
	
			if (datas != null && datas.size() > 0) {
				setLeaveRemainReport1(workbook, datas);// 封装剩余假期报表excel数据
			} else {
	
				ExcelUtil.createRow(sheet.createRow(1), 0, null, HSSFCell.CELL_TYPE_STRING, "无数据");
			}
	
			return workbook;
		}	
	}

	public void setLeaveRemainReport1(HSSFWorkbook workbook, List<Map<String, Object>> datas) {
		HSSFSheet sheet = workbook.getSheetAt(0);

		String[] title1 = { "员工编号", "员工姓名", "入职日期", "部门", "离职日期", "在职状态", "当年年假总天数", "当年年假剩余天数", "当年已用年假天数",
				"截止目前年假剩余天数", "透支年假天数", "去年年假总天数", "去年已用年假天数", "去年年假剩余天数", "当年带薪病假总天数", "当年已用带薪病假天数", "当年剩余带薪病假天数",
				"当年已用非带薪病假天数", "截止目前剩余带薪病假天数", "已透支带薪病假天数", "当年剩余调休小时数", "当年已用调休小时数", "去年剩余调休小时数", "去年已用调休小时数",
				"当年已用假期天数" };
		String[] title2 = { "", "", "", "", "", "", "法定", "福利", "法定", "福利", "法定", "福利", "法定", "福利", "", "法定", "福利",
				"法定", "福利", "法定", "福利", "", "", "", "", "", "", "", "", "", "", "事假", "丧假", "婚假", "陪产假", "产假", "产前假",
				"哺乳假", "流产假", "其他" };

		// 表头标题样式
		HSSFCellStyle colstyle = workbook.createCellStyle();
		colstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
		colstyle.setBorderBottom(XSSFCellStyle.BORDER_THIN); // 下边框
		colstyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);// 左边框
		colstyle.setBorderTop(XSSFCellStyle.BORDER_THIN);// 上边框
		colstyle.setBorderRight(XSSFCellStyle.BORDER_THIN);// 右边框

		// 建立第一行表头
		HSSFRow row = sheet.createRow((short) 0);

		// 创建第一行表头内容
		for (int colIndex = 0; colIndex < title1.length; colIndex++) {
			if (colIndex <= 5) {
				// CellRangeAddress参数详解：起始行号 结尾行号 起始列号 结尾列号
				// 第一行标题跨行跨列设置
				// 员工编号,员工姓名,入职日期,部门,离职日期,在职状态
				sheet.addMergedRegion(new CellRangeAddress(0, 1, colIndex, colIndex));
				ExcelUtil.createRow(row, colIndex, colstyle, HSSFCell.CELL_TYPE_STRING, title1[colIndex]);
				sheet.setColumnWidth(colIndex, 5000);// 设置表格宽度
			} else if (colIndex >= 6 && colIndex <= 9) {
				// 当年年假总天数,当年年假剩余天数,当年已用年假天数,截止目前年假剩余天数
				sheet.setColumnWidth(2 * colIndex - 6, 5000);// 设置表格宽度
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 2 * colIndex - 6, 2 * colIndex - 5));
				ExcelUtil.createRow(row, 2 * colIndex - 6, colstyle, HSSFCell.CELL_TYPE_STRING, title1[colIndex]);
			} else if (colIndex == 10) {
				sheet.setColumnWidth(14, 5000);// 设置表格宽度
				sheet.addMergedRegion(new CellRangeAddress(0, 1, 14, 14));// 透支年假天数
				ExcelUtil.createRow(row, 14, colstyle, HSSFCell.CELL_TYPE_STRING, title1[colIndex]);
			} else if (colIndex >= 11 && colIndex <= 13) {
				// 去年年假总天数,去年已用年假天数,去年年假剩余天数
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 2 * colIndex - 7, 2 * colIndex - 6));
				ExcelUtil.createRow(row, 2 * colIndex - 7, colstyle, HSSFCell.CELL_TYPE_STRING, title1[colIndex]);
			} else if (colIndex >= 14 && colIndex <= 23) {
				// 当年带薪病假总天数,当年已用带薪病假天数,当年剩余带薪病假天数,,当年已用非带薪病假天数,截止目前剩余带薪病假天数
				// 已透支带薪病假天数,当年剩余调休小时数,当年已用调休小时数,去年剩余调休小时数,去年已用调休小时数
				sheet.addMergedRegion(new CellRangeAddress(0, 1, colIndex + 7, colIndex + 7));
				sheet.setColumnWidth(colIndex + 7, 7000);// 设置表格宽度
				ExcelUtil.createRow(row, colIndex + 7, colstyle, HSSFCell.CELL_TYPE_STRING, title1[colIndex]);
			} else if (colIndex == 24) {
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 31, 39));// 当年已用假期天数
				ExcelUtil.createRow(row, 31, colstyle, HSSFCell.CELL_TYPE_STRING, title1[colIndex]);
			}

		}

		// 建立第二行表头
		row = sheet.createRow((short) 1);
		// 创建第二行表头内容
		for (int colIndex = 6; colIndex < title2.length; colIndex++) {
			if (colIndex > 30 || colIndex < 21) {
				if (colIndex != 14) {
					ExcelUtil.createRow(row, colIndex, colstyle, HSSFCell.CELL_TYPE_STRING, title2[colIndex]);
					sheet.setColumnWidth(colIndex, 2500);// 设置表格宽度
				}
			}
		}

		// 从第三行开始就是excel数据
		int index = 2;

		for (Map<String, Object> data : datas) {
			row = sheet.createRow((short) index);
			ExcelUtil.createRow(row, 0, null, HSSFCell.CELL_TYPE_STRING, data.get("code"));
			ExcelUtil.createRow(row, 1, null, HSSFCell.CELL_TYPE_STRING, data.get("cnName"));
			ExcelUtil.createRow(row, 2, null, HSSFCell.CELL_TYPE_STRING, data.get("entryTime"));
			ExcelUtil.createRow(row, 3, null, HSSFCell.CELL_TYPE_STRING, data.get("departName"));
			ExcelUtil.createRow(row, 4, null, HSSFCell.CELL_TYPE_STRING, data.get("quitTime"));
			ExcelUtil.createRow(row, 5, null, HSSFCell.CELL_TYPE_STRING, data.get("jobStatus"));
			ExcelUtil.createRow(row, 6, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave2"));// 法定年假总数-法定年假
			ExcelUtil.createRow(row, 7, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave3"));// 法定年假总数-福利年假
			ExcelUtil.createRow(row, 8, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave7"));// 剩余假期天数-当年法定年假
			ExcelUtil.createRow(row, 9, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave8"));// 剩余假期天数-当年福利年假
			ExcelUtil.createRow(row, 10, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave14"));// 当年已使用假期天数-法定年假
			ExcelUtil.createRow(row, 11, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave15"));// 当年已使用假期天数-福利年假
			ExcelUtil.createRow(row, 12, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave12"));// 截止目前剩余假期天数-当年法定年假
			ExcelUtil.createRow(row, 13, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave13"));// 截止目前剩余假期天数-当年福利年假
			ExcelUtil.createRow(row, 14, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave29"));// 透支假期天数-年假
			ExcelUtil.createRow(row, 15, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave32"));// 去年年假总天数-法定
			ExcelUtil.createRow(row, 16, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave33"));// 去年年假总天数-福利
			ExcelUtil.createRow(row, 17, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave34"));// 去年已用年假天数-法定
			ExcelUtil.createRow(row, 18, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave35"));// 去年已用年假天数-福利
			ExcelUtil.createRow(row, 19, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave5"));// 剩余假期天数-去年法定年假
			ExcelUtil.createRow(row, 20, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave6"));// 剩余假期天数-去年福利年假
			ExcelUtil.createRow(row, 21, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave4"));// 法定年假总数-带薪病假
			ExcelUtil.createRow(row, 22, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave17"));// 当年已使用假期天数-带薪病假
			ExcelUtil.createRow(row, 23, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave10"));// 剩余假期天数-带薪病假
			ExcelUtil.createRow(row, 24, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave18"));// 当年已使用假期天数-非带薪病假
			ExcelUtil.createRow(row, 25, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave31"));// 截止目前剩余假期天数-带薪病假
			ExcelUtil.createRow(row, 26, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave30"));// 透支假期天数-带薪病假
			ExcelUtil.createRow(row, 27, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave11"));// 剩余假期天数-调休小时数
			ExcelUtil.createRow(row, 28, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave20"));// 当年已使用假期天数-调休小时数
			ExcelUtil.createRow(row, 29, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave36"));// 去年剩余调休小时数
			ExcelUtil.createRow(row, 30, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave37"));// 去年已用调休小时数
			ExcelUtil.createRow(row, 31, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave19"));// 当年已使用假期天数-事假
			ExcelUtil.createRow(row, 32, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave22"));// 当年已使用假期天数-丧假
			ExcelUtil.createRow(row, 33, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave21"));// 当年已使用假期天数-婚假
			ExcelUtil.createRow(row, 34, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave23"));// 当年已使用假期天数-陪产假
			ExcelUtil.createRow(row, 35, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave25"));// 当年已使用假期天数-产假
			ExcelUtil.createRow(row, 36, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave24"));// 当年已使用假期天数-产前假
			ExcelUtil.createRow(row, 37, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave26"));// 当年已使用假期天数-哺乳假
			ExcelUtil.createRow(row, 38, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave27"));// 当年已使用假期天数-流产假
			ExcelUtil.createRow(row, 39, null, HSSFCell.CELL_TYPE_NUMERIC, data.get("leave28"));// 当年已使用假期天数-其他

			index++;
		}
	}

	/**
	 * 查询假期流水
	 */
	@Override
	public PageModel<ResponseQueryLeaveRecord> getLeaveRecordList(RequestQueryLeaveRecord requestQueryLeaveRecord) {
		logger.info("-----------查询假期流水列表开始-----------");
		int page = requestQueryLeaveRecord.getPage() == null ? 0 : requestQueryLeaveRecord.getPage();
		int rows = requestQueryLeaveRecord.getRows() == null ? 0 : requestQueryLeaveRecord.getRows();

		PageModel<ResponseQueryLeaveRecord> pm = new PageModel<ResponseQueryLeaveRecord>();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		requestQueryLeaveRecord.setOffset(pm.getOffset());
		requestQueryLeaveRecord.setLimit(pm.getLimit());
		//假期类型
		List<Integer> leaveTypeList = new ArrayList<Integer>();
		if(requestQueryLeaveRecord.getLeaveType()!=null){
			if(requestQueryLeaveRecord.getLeaveType()==5){
				leaveTypeList.add(5);//调休
				leaveTypeList.add(13);//其它调休
			}else{
				leaveTypeList.add(requestQueryLeaveRecord.getLeaveType());
			}
			requestQueryLeaveRecord.setLeaveTypeList(leaveTypeList);
		}
		List<LeaveRecordDetail> leaveRecordDetailList = leaveRecordMapper
				.getLeaveRecordDetailList(requestQueryLeaveRecord);
		List<ResponseQueryLeaveRecord> responseQueryLeaveRecords = new LinkedList<ResponseQueryLeaveRecord>();
		for (LeaveRecordDetail leaveRecordDetail : leaveRecordDetailList) {
			ResponseQueryLeaveRecord responseQueryLeaveRecord = new ResponseQueryLeaveRecord();
			// 根据编号和姓名查询员工信息
			List<Employee> employeeList = leaveRecordMapper.getEmpInfo(requestQueryLeaveRecord.getEmployeeCode(),
					requestQueryLeaveRecord.getEmployeeName());
			// 如果查出来的员工只有一个，则员工信息表中展示员工信息
			if (employeeList != null && employeeList.size() == 1 && employeeList.get(0) != null) {
				// 员工编码
				responseQueryLeaveRecord.setEmpCode(employeeList.get(0).getCode());
				// 员工姓名
				responseQueryLeaveRecord.setEmpName(employeeList.get(0).getCnName());
				// 入职日期
				if (employeeList.get(0).getFirstEntryTime() != null) {
					responseQueryLeaveRecord.setFirstEntryTime(
							DateUtils.format(employeeList.get(0).getFirstEntryTime(), DateUtils.FORMAT_SHORT));
				}
				// 离职日期
				if (employeeList.get(0).getQuitTime() != null) {
					String quitTime = DateUtils.format(employeeList.get(0).getQuitTime(), DateUtils.FORMAT_SHORT);
					responseQueryLeaveRecord.setQuitTime(quitTime);
				}
				// 在职状态
				responseQueryLeaveRecord.setJobStatus(employeeList.get(0).getJobStatus() == 0 ? "在职" : "离职");
				// 查询部门
				String departName = leaveRecordMapper.getDepartName(employeeList.get(0).getId());
				responseQueryLeaveRecord.setDepartName(departName);
			}
			if (leaveRecordDetail.getLeaveRecordId() != null) {
				// 根据流水id查流水表
				LeaveRecord leaveRecord = leaveRecordMapper.getleaveRecordInfo(leaveRecordDetail.getLeaveRecordId());
				responseQueryLeaveRecord.setLeaveRecordId(leaveRecord.getId());
				Employee employee = leaveRecordMapper.getEmployeeById(leaveRecord.getEmployeeId());
				if (employee != null) {
					// 员工编码
					responseQueryLeaveRecord.setEmployeeCode(employee.getCode());
					// 员工姓名
					responseQueryLeaveRecord.setEmployeeName(employee.getCnName());
				}
				if (leaveRecord.getUpdateType() != null) {
					switch (leaveRecord.getUpdateType()) {
					case 0:
						responseQueryLeaveRecord.setUpdateType("已用");
						break;
					case 1:
						responseQueryLeaveRecord.setUpdateType("总数");
						break;
					case 2:
						responseQueryLeaveRecord.setUpdateType("剩余");
						break;
					default:
						break;
					}
				}
				// 流水类型
				if(leaveRecord.getSource().equals(0)) {
					responseQueryLeaveRecord.setSourceText(ConfigConstants.LEAVE_RECORD_SOURCE_SYSTEM);
				}else if(leaveRecord.getSource().equals(1)) {
					responseQueryLeaveRecord.setSourceText(ConfigConstants.LEAVE_RECORD_SOURCE_HR_U);
				}else {
					responseQueryLeaveRecord.setSourceText(ConfigConstants.LEAVE_RECORD_SOURCE_HR_R);
				}
				
				// 备注
				responseQueryLeaveRecord.setRemark(leaveRecord.getRemark());
				// 假期单位
				responseQueryLeaveRecord.setUnitText(leaveRecord.getDaysUnit() == 0 ? ConfigConstants.TIME_UNIT_DAY
						: ConfigConstants.TIME_UNIT_HOUR);
				// 假期类型
				if(leaveRecordDetail.getType()!=null&&leaveRecordDetail.getType()==13){
					responseQueryLeaveRecord.setLeaveTypeText("调休");
				}else{
					String leaveType = leaveRecordMapper.getLeaveTypeText(leaveRecordDetail.getType());
					responseQueryLeaveRecord.setLeaveTypeText(leaveType);
				}
				// 操作人(系统设置为--)
				responseQueryLeaveRecord
						.setCreateUser(leaveRecord.getSource() == 0 ? "--" : leaveRecordDetail.getCreateUser());
			}
			;
			// 生成时间
			Date createTimeDate = leaveRecordDetail.getCreateTime();
			String createTime = DateUtils.format(createTimeDate);
			responseQueryLeaveRecord.setCreateTime(createTime);
			// 天数
			responseQueryLeaveRecord.setDays(leaveRecordDetail.getDays() > 0 ? "+"+leaveRecordDetail.getDays() : leaveRecordDetail.getDays().toString());
			EmpLeave empLeave = empLeaveMapper.getById(leaveRecordDetail.getBaseEmpLeaveId());
			if (empLeave != null) {
				responseQueryLeaveRecord.setLeaveYear(empLeave.getYear());
				// 判断是否是年假如果是年假在判断是法定年假还是福利年假
				if (empLeave.getType() == 1) {
					// 年假，判断二级分类。此处的二级分类只可能为1或2，不为0.扣减年假是通过扣减法定年假或福利年假来影响总年假天数
					if (empLeave.getCategory() == 1) {
						// 子类型法定年假
						responseQueryLeaveRecord.setChildLeaveType(ConfigConstants.ANNUAL_LEAVE_LEGAL);
					} else if (empLeave.getCategory() == 2) {
						// 福利年假
						responseQueryLeaveRecord.setChildLeaveType(ConfigConstants.ANNUAL_LEAVE_BENEFIT);
					}
				} else if (empLeave.getType() == 2) {
					// 病假，判断带薪或非带薪
					if (empLeave.getCategory() == 0) {
						responseQueryLeaveRecord.setChildLeaveType(ConfigConstants.PAID_SICK_LEAVE);
					} else {
						responseQueryLeaveRecord.setChildLeaveType(ConfigConstants.UNPAID_SICK_LEAVE);
					}

				} else {
					// 其他假期类型没有子类型
					responseQueryLeaveRecord.setChildLeaveType("--");
				}

			}
			responseQueryLeaveRecords.add(responseQueryLeaveRecord);
		}
		pm.setRows(responseQueryLeaveRecords);
		// 总条数
		Integer count = leaveRecordMapper.getLeaveRecordCount(requestQueryLeaveRecord);
		pm.setTotal(count);
		logger.info("-----------查询假期流水列表结束-----------");
		return pm;
	}

	/**
	 * @throws Exception
	 *             updateEmpLeaveApply(销假申请调的接口) @Title:
	 *             updateEmpLeaveApply @Description: 销假申请调的接口 @param
	 *             planLeaves @return @throws Exception 设定文件 boolean
	 *             返回类型 @throws
	 */
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
	public boolean updateEmpLeaveAbolish(List<Map<String, Object>> planLeaves) throws Exception {
		boolean flag = false;// 返回状态

		for (Map<String, Object> leaveMap : planLeaves) {
			EmpLeave planLeave = JSONUtils.read(JSONUtils.write(leaveMap), EmpLeave.class);
			// 非空校验
			if (null == planLeave) {
				throw new OaException("您当前的销假信息为空");
			} else {
				if (null == planLeave.getEmployeeId()) {
					throw new OaException("您当前的销假申请的员工id为空");
				}
				if (null == planLeave.getType()) {
					throw new OaException("您当前的销假的假期类型为空");
				}
				if (null == planLeave.getPlanStartTime()) {
					throw new OaException("您当前的销假的开始时间为空");
				}
				if (null == planLeave.getPlanEndTime()) {
					throw new OaException("您当前的销假的结束时间为空");
				}
				if (null == planLeave.getPlanDays()) {
					throw new OaException("您当前的销假的天数为空");
				}
				if (StringUtils.isBlank(planLeave.getOptUser())) {
					throw new OaException("销假申请人不能为空");
				}
			}

			// 根据计划休假信息找到总的剩余假期信息
			if (planLeave.getType().intValue() == ConfigConstants.LEAVE_TYPE_1.intValue()) {// 年假
				List<EmpLeave> leaves = splitLeave(planLeave);// 拆分假期
				Long recordId = saveLeaveRecord(planLeave, ConfigConstants.CANCELLEAVE_KEY);// 新增假期流水
				flag = updateUsedYearLeave(leaves, recordId);
			} else if (planLeave.getType().intValue() == ConfigConstants.LEAVE_TYPE_5.intValue()) {// 调休
				Long recordId = saveLeaveRecord(planLeave, ConfigConstants.CANCELLEAVE_KEY);// 新增假期流水
				flag = updateUsedRestLeave(planLeave,recordId);
			} else if (planLeave.getType().intValue() == ConfigConstants.LEAVE_TYPE_2.intValue()) {// 病假
				flag = sickLeaveAbolishSuccess(planLeave);
			} else {
				flag = true;
			}
		}

		return flag;
	}

	public boolean updateUsedYearLeave(List<EmpLeave> planLeaves, Long recordId) throws OaException {
		boolean flag = false;
		
		//如果planLeaves，多条，按开始时间倒叙，先还今年再换去年
		planLeaves = planLeaves.stream().sorted((u1, u2) -> u2.getPlanStartTime().compareTo(u1.getPlanStartTime())).collect(Collectors.toList());

		for (EmpLeave planLeave : planLeaves) {// 拆分以后的假期，分两段扣减(如果不需要拆分，则只有一段假期)
			Double planDays = planLeave.getPlanDays();// 计划休假天数
			String optUser = planLeave.getOptUser();

			try {
				// 1.根据员工id,假期开始时间，假期类型找到可用的假期天数总数，check预计请假天数是否超出可用假期天数
				Map<String, Object> totalLeave = empLeaveMapper.getMaxUsedDays(planLeave);
				if (null == totalLeave || totalLeave.size() == 0) {
					throw new OaException("您的当前可销年假为空");
				}
				// 计划请假天数必须 <= 允许销假天数
				Double usedDays = Double.valueOf(totalLeave.get("usedDays") + "");// 总的允许销假天数
				if (planDays > usedDays) {
					throw new OaException("您的当前允许可销年假为" + usedDays + "天," + "计划休假应小于或等于" + usedDays + "天");
				}

				// 2.步骤1校验通过后，分年扣减假期天数
				List<EmpLeave> leaves = empLeaveMapper.getAllowUsedDays(planLeave);
				Double remainPlanDays = planDays;// 剩余计划休假天数
				Double remainUsedDays = planDays;// 剩余扣减的占用天数
				loop: for (EmpLeave empLeave : leaves) {
					EmpLeave leave = empLeaveMapper.getById(empLeave.getId());
					leave.setUpdateUser(optUser);
					leave.setUpdateTime(new Date());

					if (empLeave.getCategory().intValue() == 0) {
						remainUsedDays = remainUsedDays > leave.getUsedDays() ? leave.getUsedDays() : remainUsedDays;// 需销的占用天数

						leave.setUsedDays(leave.getUsedDays() - remainUsedDays);
						leave.setAllowRemainDays(leave.getAllowRemainDays() + remainUsedDays);
						empLeaveMapper.updateById(leave);
						remainUsedDays = planDays > remainUsedDays ? planDays - remainUsedDays : remainUsedDays;// 余下还需销假的天数
						
						//已用还完就跳出循环
						if(remainPlanDays <= 0){
							break loop;
						}
						
						continue;
					}

					// 计划休假时间小于当前剩余休假天数
					if (remainPlanDays <= leave.getUsedDays()) {// 预计销假天数小于已用天数，跳出整个循环

						if (remainPlanDays <= 0) {
							
						}else{
							// 流水明细
							saveRecordDetail(recordId, leave.getId(), -remainPlanDays, optUser);

							leave.setUsedDays(leave.getUsedDays() - remainPlanDays);
							leave.setAllowRemainDays(leave.getAllowRemainDays() + remainPlanDays);
							empLeaveMapper.updateById(leave);
							remainPlanDays = 0.0;
						}
					} else {// 预计销假天数大于当前已用天数,则直接扣减当前记录,继续销下条假期
						remainPlanDays = remainPlanDays - leave.getUsedDays();// 余下还需扣减的假期天数

						// 流水明细
						if (leave.getUsedDays() > 0) {
							saveRecordDetail(recordId, leave.getId(), -leave.getUsedDays(), optUser);
						}
						leave.setAllowRemainDays(leave.getAllowRemainDays() + leave.getUsedDays());
						leave.setUsedDays(0.0);
						empLeaveMapper.updateById(leave);

					}
				}
				flag = true;
			} catch (Exception e) {
				logger.error("销假还年假出错，出错原因=" + e.toString());
				throw e;
			}
		}

		return flag;
	}

	/*public boolean updateUsedRestLeave(List<EmpLeave> planLeaves, Long recordId) throws OaException {
		boolean flag = false;

		for (EmpLeave planLeave : planLeaves) {// 拆分以后的假期，分两段扣减(如果不需要拆分，则只有一段假期)
			Double planDays = planLeave.getPlanDays();// 计划休假天数
			String optUser = planLeave.getOptUser();

			try {
				// 1.根据员工id,假期开始时间，假期类型找到可用的假期天数总数，check预计请假天数是否超出可用假期天数
				Map<String, Object> totalLeave = empLeaveMapper.getMaxUsedDays(planLeave);
				if (null == totalLeave || totalLeave.size() == 0) {
					throw new OaException("您的当前可销调休为空");
				}
				// 计划销假天数必须 <= 允许已用天数
				Double usedDays = Double.valueOf(totalLeave.get("usedDays") + "");// 已用
				if (planDays > usedDays) {
					throw new OaException("您的当前允许可销调休为" + usedDays + "小时," + ",计划销假应小于或等于" + usedDays + "小时");
				}

				// 2.扣减总的调休
				List<EmpLeave> totalLeaves = empLeaveMapper.getTotalUsedDays(planLeave);
				Double remainPlanDays = planDays;// 剩余计划休假天数
				Double remainUsedDays = planDays;// 剩余扣减的占用天数
				loop: for (EmpLeave total : totalLeaves) {
					remainUsedDays = remainUsedDays > total.getUsedDays() ? total.getUsedDays() : remainUsedDays;// 需要还的假期

					total.setUpdateUser(optUser);
					total.setUpdateTime(new Date());
					total.setUsedDays(total.getUsedDays() - remainUsedDays);
					total.setAllowRemainDays(total.getAllowRemainDays() + remainUsedDays);
					empLeaveMapper.updateById(total);
					// 流水明细
					saveRecordDetail(recordId, total.getId(), -remainUsedDays, optUser);
					remainUsedDays = planDays - remainUsedDays > 0 ? planDays - remainUsedDays : 0;// 余下需要还的假期

					// 3.还调休明细
					List<EmpLeave> leaves = empLeaveMapper.getByPidDesc(total.getId());
					for (EmpLeave empLeave : leaves) {
						EmpLeave leave = empLeaveMapper.getById(empLeave.getId());
						leave.setUpdateUser(optUser);
						leave.setUpdateTime(new Date());

						// 计划销假时间小于当前已用
						if (remainPlanDays <= leave.getUsedDays()) {

							if (remainPlanDays <= 0) {
								break loop;
							}

							leave.setUsedDays(leave.getUsedDays() - remainPlanDays);
							leave.setAllowRemainDays(leave.getAllowRemainDays() + remainPlanDays);
							empLeaveMapper.updateById(leave);
							remainPlanDays = 0.0;
						} else {
							remainPlanDays = remainPlanDays - leave.getUsedDays();// 余下还需还的假期天数

							leave.setAllowRemainDays(leave.getAllowRemainDays() + leave.getUsedDays());
							leave.setUsedDays(0.0);
							empLeaveMapper.updateById(leave);
						}
					}
				}

				flag = true;
			} catch (Exception e) {
				logger.error("销假还调休小时数出错，出错原因=" + e.toString());
				throw e;
			}
		}

		return flag;
	}*/
	public boolean updateUsedRestLeave(EmpLeave planLeave, Long recordId) throws OaException{
		boolean flag = false;
		try {
			//调休申请单id
			Long empApplicationleaveId = planLeave.getEmpapplicationleaveId();
			//销假小时数
			Double planDays = planLeave.getPlanDays();
			String optUser = planLeave.getOptUser();
			Double remainUsedDays = planDays;// 还的占用天数
			Double remainUsededTotalDays = planDays;	//一共要还的占用天数
			if(empApplicationleaveId == null){
				logger.error("销假申请单不存在！");
				throw new OaException("销假申请单不存在！");
			}
			//查询该调休所有流水
			List<LeaveRecordDetail> leaveIdList = leaveRecordDetailMapper.getByApplicationAbolishLeaveId(empApplicationleaveId);
			if(leaveIdList != null && leaveIdList.size() > 0){
				for (LeaveRecordDetail leaveRecordDetail : leaveIdList) {
					//判断销假天数与流水天数
					remainUsedDays = remainUsedDays > leaveRecordDetail.getDays() ? leaveRecordDetail.getDays()
							: remainUsedDays;// 需扣减的占用天数
					//查出关联假期
					EmpLeave empLeave = empLeaveMapper.getById(leaveRecordDetail.getBaseEmpLeaveId());
					empLeave.setUpdateUser(optUser);
					empLeave.setUpdateTime(new Date());
					empLeave.setUsedDays(empLeave.getUsedDays()- remainUsedDays);
					empLeave.setAllowRemainDays(empLeave.getAllowRemainDays()+remainUsedDays);
					empLeaveMapper.updateById(empLeave);
					//生成流水
					saveRecordDetail(recordId, empLeave.getId(), -remainUsedDays, optUser);
					remainUsededTotalDays = remainUsededTotalDays - remainUsedDays;
					remainUsedDays = remainUsededTotalDays;
					if(remainUsededTotalDays <= 0){
						break;
					}
				}
				if(remainUsededTotalDays > 0){
					logger.error("该员工已用假期数据错误！");
					throw new OaException("该员工已用假期数据错误！");
				}
				flag = true;
			}
		} catch (Exception e) {
			logger.error("销假还调休小时数出错，出错原因=" + e.toString());
			throw e;
		}
		
		return flag;
	}
	/**
	 * @throws OaException
	 *             sickLeaveApplySuccess(病假撤销审核通过) @Title:
	 *             sickLeaveApplySuccess @Description: 病假审核通过 @param
	 *             planLeave @return 设定文件 boolean 返回类型 @throws
	 */
	public boolean sickLeaveAbolishSuccess(EmpLeave planLeave) throws OaException {
		boolean flag = false;
		try {
			LeaveRecord record = new LeaveRecord();
			record.setEmployeeId(planLeave.getEmployeeId());
			record.setType(planLeave.getType());
			record.setDays(-planLeave.getPlanDays());
			record.setBillId(planLeave.getEmpapplicationleaveId());
			record.setBillType(ConfigConstants.CANCELLEAVE_KEY);
			record.setDaysUnit(0);// 单位
			record.setCreateTime(new Date());
			record.setCreateUser(planLeave.getOptUser());
			record.setDelFlag(0);
			record.setSource(0);
			record.setRemark("销假申请");
			leaveRecordMapper.save(record);

			Date startTime = null;

			// 原请假单据
			EmpApplicationLeave leaveapply = empApplicationLeaveMapper.getById(planLeave.getOriginalBillId());

			if (leaveapply != null) {
				startTime = leaveapply.getStartTime();
			} else {
				startTime = planLeave.getPlanStartTime();
				logger.error("销假还病假，原单据id为空=" + planLeave.getEmpapplicationleaveId());
			}

			// 1.查询此员工病假信息
			planLeave.setYear(Integer.parseInt(DateUtils.getYear(new Date())));
			planLeave.setCategory(0);// 新添查询 带薪病假
			List<EmpLeave> leaves = empLeaveMapper.getListByCondition(planLeave);
			EmpLeave totalLeave = leaves.get(0);
			totalLeave.setUpdateUser(planLeave.getOptUser());
			totalLeave.setUpdateTime(new Date());

			Employee emp = employeeMapper.getById(planLeave.getEmployeeId());

			Date flagStaus = DateUtils.addMonth(emp.getFirstEntryTime(), 3);
			if (1 == DateUtils.compareDayDate(flagStaus, startTime)) {
				planLeave.setYear(Integer.parseInt(DateUtils.getYear(new Date())));
				planLeave.setCategory(2);
				List<EmpLeave> unSickLeaveList = empLeaveMapper.getListByCondition(planLeave);
				unSickLeaveList.get(0).setUsedDays(unSickLeaveList.get(0).getUsedDays() - planLeave.getPlanDays());
				unSickLeaveList.get(0).setUpdateUser(planLeave.getOptUser());
				unSickLeaveList.get(0).setUpdateTime(new Date());
				empLeaveMapper.updateById(unSickLeaveList.get(0));
				// 流水明细
				saveRecordDetail(record.getId(), unSickLeaveList.get(0).getId(), -planLeave.getPlanDays(),
						planLeave.getOptUser());
			} else {
				planLeave.setYear(Integer.parseInt(DateUtils.getYear(new Date())));
				planLeave.setCategory(2);
				List<EmpLeave> unSickLeaveList = empLeaveMapper.getListByCondition(planLeave);
				Double sickLeaveRe = 0.0;
				if (unSickLeaveList != null && unSickLeaveList.size() > 0) {
					if (unSickLeaveList.get(0).getUsedDays() != null
							&& unSickLeaveList.get(0).getUsedDays() >= planLeave.getPlanDays()) {
						// 流水明细
						saveRecordDetail(record.getId(), unSickLeaveList.get(0).getId(), -planLeave.getPlanDays(),
								planLeave.getOptUser());

						unSickLeaveList.get(0)
								.setUsedDays(unSickLeaveList.get(0).getUsedDays() - planLeave.getPlanDays());
						unSickLeaveList.get(0).setUpdateUser(planLeave.getOptUser());
						unSickLeaveList.get(0).setUpdateTime(new Date());
						empLeaveMapper.updateById(unSickLeaveList.get(0));
					} else {
						// 流水明细
						saveRecordDetail(record.getId(), unSickLeaveList.get(0).getId(),
								-unSickLeaveList.get(0).getUsedDays(), planLeave.getOptUser());
						sickLeaveRe = planLeave.getPlanDays() - (unSickLeaveList.get(0).getUsedDays() != null
								? unSickLeaveList.get(0).getUsedDays() : 0);
						unSickLeaveList.get(0).setUsedDays(0.0);
						unSickLeaveList.get(0).setUpdateUser(planLeave.getOptUser());
						unSickLeaveList.get(0).setUpdateTime(new Date());
						empLeaveMapper.updateById(unSickLeaveList.get(0));

						// 流水明细
						saveRecordDetail(record.getId(), totalLeave.getId(), -sickLeaveRe, planLeave.getOptUser());

						totalLeave.setUsedDays(totalLeave.getUsedDays() - sickLeaveRe);
						totalLeave.setAllowRemainDays(totalLeave.getAllowRemainDays() + sickLeaveRe);
						empLeaveMapper.updateById(totalLeave);
					}
				} else {
					// 流水明细
					saveRecordDetail(record.getId(), totalLeave.getId(), -planLeave.getPlanDays(),
							planLeave.getOptUser());

					totalLeave.setUsedDays(totalLeave.getUsedDays() - planLeave.getPlanDays());
					totalLeave.setAllowRemainDays(totalLeave.getAllowRemainDays() + planLeave.getPlanDays());
					empLeaveMapper.updateById(totalLeave);
				}
			}
			flag = true;
		} catch (Exception e) {
			throw new OaException(e);
		}

		return flag;
	}

	@Override
	public HSSFWorkbook exportLeaveRemainReportNew(Employee emp) {
		// 封装部门
		Integer firstDepart = emp.getFirstDepart();// 页面上一级部门
		Integer secondDepart = emp.getSecondDepart();// 页面上二级部门
		List<Integer> departList = new ArrayList<Integer>();
		if (null != secondDepart) {// 选择了二级部门
			departList.add(secondDepart);
		} else if (null == secondDepart && null != firstDepart) {// 只选择了一级部门
			// 根据一级部门将一级部门下面的所有二级部门查询出来
			departList.add(firstDepart);
			List<Depart> departs = departService.getByParentId(Long.valueOf(firstDepart + ""));
			departs.forEach((Depart dp) -> {
				departList.add(Integer.parseInt(dp.getId() + ""));
			});
		}
		emp.setDepartList(departList);
		emp.setYear(Integer.parseInt(DateUtils.getYear(new Date())));
		List<EmpLeave> list = getReportListNew(emp);
		List<Map<String, Object>> datas = convertExportList(list);// 将查询的结果转换成excle需要的类型

		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("剩余假期");

		if (datas != null && datas.size() > 0) {
			setLeaveRemainReport1(workbook, datas);// 封装剩余假期报表excel数据
		} else {

			ExcelUtil.createRow(sheet.createRow(1), 0, null, HSSFCell.CELL_TYPE_STRING, "无数据");
		}

		return workbook;

	}

	public void saveLeaveRecord(Long employeeId, Integer type, double now, double old, Integer unit, User user,
			Integer updateType, String remark, Long leaveId) {
		LeaveRecord record = new LeaveRecord();
		record.setEmployeeId(employeeId);
		record.setType(type);
		record.setDays(now - old);
		record.setBillId(0L);
		record.setBillType(ConfigConstants.LEAVE_KEY);
		record.setDaysUnit(unit);// 单位
		record.setCreateTime(new Date());
		record.setCreateUser(user.getEmployee().getCnName());
		record.setDelFlag(0);
		record.setSource(1);
		record.setUpdateType(updateType);
		record.setRemark(remark);
		leaveRecordMapper.save(record);
		LeaveRecordDetail recordDetail = new LeaveRecordDetail();
		recordDetail.setLeaveRecordId(record.getId());
		recordDetail.setBaseEmpLeaveId(leaveId);
		recordDetail.setDays(now - old);
		recordDetail.setCreateTime(new Date());
		recordDetail.setCreateUser(user.getEmployee().getCnName());
		recordDetail.setDelFlag(0);
		leaveRecordDetailMapper.save(recordDetail);
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
	public Map<String, Object> updateLeaveByEmpId(EmpLeave empLeave) throws Exception {
		Map<String, Object> result = Maps.newHashMap();
		User user = userService.getCurrentUser();
        int year = Integer.valueOf(DateUtils.format(new Date(), "yyyy")); 
		EmpLeave param = new EmpLeave();
		param.setCompanyId(1L);
		param.setYear(year);
		param.setEmployeeId(empLeave.getEmployeeId());
		switch (empLeave.getLeaveType()) {
		case "1":
			// 年假
			param.setType(ConfigConstants.LEAVE_TYPE_1);
			List<EmpLeave> yearLeaves = this.empLeaveMapper.getListByCondition(param);

			for (EmpLeave yearLeave : yearLeaves) {
				// 今年假期
				if (yearLeave.getCategory().intValue() == 0) {
					// 总年假
					yearLeave.setAllowRemainDays(empLeave.getLeave2() - empLeave.getLeave14() + empLeave.getLeave3()
							- empLeave.getLeave15());
					yearLeave.setUsedDays(empLeave.getLeave14() + empLeave.getLeave15());
					yearLeave.setAllowDays(empLeave.getLeave2() + empLeave.getLeave3());
				} else if (yearLeave.getCategory().intValue() == 1) {
					// leave2-当年法定总数,leave14-当年法定已用
					if (empLeave.getLeave2().doubleValue() != yearLeave.getAllowDays().doubleValue()) {
						// 判断数据是否被修改(修改了就生成流水数据)
						saveLeaveRecord(empLeave.getEmployeeId(), 1, empLeave.getLeave2().doubleValue(),
								yearLeave.getAllowDays().doubleValue(), 0, user, 1, empLeave.getRemark(),
								yearLeave.getId());
						Employee emp = new Employee();
						emp.setAutoCalculateLeave(ConfigConstants.NOT_CAL_YEAR_RADUIX_CURRENT_YEAR);
						emp.setId(empLeave.getEmployeeId());
						employeeMapper.updateAutoCaLculateLeave(emp);
					}
					if (yearLeave.getAllowDays().doubleValue() - yearLeave.getAllowRemainDays().doubleValue()
							+ yearLeave.getBlockedDays().doubleValue() != empLeave.getLeave14().doubleValue()) {
						// 判断数据是否被修改(修改了就生成流水数据)
						saveLeaveRecord(empLeave.getEmployeeId(), 1, empLeave.getLeave14().doubleValue(),
								yearLeave.getAllowDays().doubleValue() - yearLeave.getAllowRemainDays().doubleValue()
										- yearLeave.getBlockedDays().doubleValue(),
								0, user, 0, empLeave.getRemark(), yearLeave.getId());
					}
					yearLeave.setAllowRemainDays(
							empLeave.getLeave2() - empLeave.getLeave14() - yearLeave.getBlockedDays());
					yearLeave.setUsedDays(empLeave.getLeave14());
					yearLeave.setAllowDays(empLeave.getLeave2());
				} else if (yearLeave.getCategory().intValue() == 2) {
					// leave3-当年福利总数,leave15 -当年福利已用
					if (empLeave.getLeave3().doubleValue() != yearLeave.getAllowDays().doubleValue()) {
						// 判断数据是否被修改(修改了就生成流水数据)
						saveLeaveRecord(empLeave.getEmployeeId(), 1, empLeave.getLeave3().doubleValue(),
								yearLeave.getAllowDays().doubleValue(), 0, user, 1, empLeave.getRemark(),
								yearLeave.getId());
						Employee emp = new Employee();
						emp.setAutoCalculateLeave(ConfigConstants.NOT_CAL_YEAR_RADUIX_CURRENT_YEAR);
						emp.setId(empLeave.getEmployeeId());
						employeeMapper.updateAutoCaLculateLeave(emp);
					}
					if (yearLeave.getAllowDays().doubleValue() - yearLeave.getAllowRemainDays().doubleValue()
							+ yearLeave.getBlockedDays().doubleValue() != empLeave.getLeave15().doubleValue()) {
						// 判断数据是否被修改(修改了就生成流水数据)
						saveLeaveRecord(empLeave.getEmployeeId(), 1, empLeave.getLeave15().doubleValue(),
								yearLeave.getAllowDays().doubleValue() - yearLeave.getAllowRemainDays().doubleValue()
										- yearLeave.getBlockedDays().doubleValue(),
								0, user, 0, empLeave.getRemark(), yearLeave.getId());
					}
					yearLeave.setAllowRemainDays(
							empLeave.getLeave3() - empLeave.getLeave15() - yearLeave.getBlockedDays());
					yearLeave.setUsedDays(empLeave.getLeave15());
					yearLeave.setAllowDays(empLeave.getLeave3());
				}
				this.empLeaveMapper.updateById(yearLeave);
			}

			param.setYear(year-1);
			List<EmpLeave> lastyearLeaves = this.empLeaveMapper.getListByCondition(param);

			for (EmpLeave lastyearLeave : lastyearLeaves) {
				// 去年假期
				if (lastyearLeave.getCategory() == 0) {
					// 总年假
					lastyearLeave.setAllowRemainDays(empLeave.getLeave32() - empLeave.getLeave34()
							+ empLeave.getLeave33() - empLeave.getLeave35());
					lastyearLeave.setUsedDays(empLeave.getLeave34() + empLeave.getLeave35());
					lastyearLeave.setAllowDays(empLeave.getLeave32() + empLeave.getLeave33());
				} else if (lastyearLeave.getCategory() == 1) {
					// leave32-去年法定总数，leave34 -去年法定已用
					if (empLeave.getLeave32().doubleValue() != lastyearLeave.getAllowDays().doubleValue()) {
						// 判断数据是否被修改(修改了就生成流水数据)
						saveLeaveRecord(empLeave.getEmployeeId(), 1, empLeave.getLeave32().doubleValue(),
								lastyearLeave.getAllowDays().doubleValue(), 0, user, 1, empLeave.getRemark(),
								lastyearLeave.getId());
					}
					if (lastyearLeave.getAllowDays().doubleValue() - lastyearLeave.getAllowRemainDays().doubleValue()
							+ lastyearLeave.getBlockedDays().doubleValue() != empLeave.getLeave34().doubleValue()) {
						// 判断数据是否被修改(修改了就生成流水数据)
						saveLeaveRecord(empLeave.getEmployeeId(), 1, empLeave.getLeave34().doubleValue(),
								lastyearLeave.getAllowDays().doubleValue()
										- lastyearLeave.getAllowRemainDays().doubleValue()
										- lastyearLeave.getBlockedDays().doubleValue(),
								0, user, 0, empLeave.getRemark(), lastyearLeave.getId());
					}
					lastyearLeave.setAllowRemainDays(
							empLeave.getLeave32() - empLeave.getLeave34() - lastyearLeave.getBlockedDays());
					lastyearLeave.setUsedDays(empLeave.getLeave34());
					lastyearLeave.setAllowDays(empLeave.getLeave32());
				} else if (lastyearLeave.getCategory() == 2) {
					// leave33-去年福利总数，leave35 -去年福利已用
					if (empLeave.getLeave33().doubleValue() != lastyearLeave.getAllowDays().doubleValue()) {
						// 判断数据是否被修改(修改了就生成流水数据)
						saveLeaveRecord(empLeave.getEmployeeId(), 1, empLeave.getLeave33().doubleValue(),
								lastyearLeave.getAllowDays().doubleValue(), 0, user, 1, empLeave.getRemark(),
								lastyearLeave.getId());
					}
					if (lastyearLeave.getAllowDays().doubleValue() - lastyearLeave.getAllowRemainDays().doubleValue()
							+ lastyearLeave.getBlockedDays().doubleValue() != empLeave.getLeave35().doubleValue()) {
						// 判断数据是否被修改(修改了就生成流水数据)
						saveLeaveRecord(empLeave.getEmployeeId(), 1, empLeave.getLeave35().doubleValue(),
								lastyearLeave.getAllowDays().doubleValue()
										- lastyearLeave.getAllowRemainDays().doubleValue()
										- lastyearLeave.getBlockedDays().doubleValue(),
								0, user, 0, empLeave.getRemark(), lastyearLeave.getId());
					}
					lastyearLeave.setAllowRemainDays(
							empLeave.getLeave33() - empLeave.getLeave35() - lastyearLeave.getBlockedDays());
					lastyearLeave.setUsedDays(empLeave.getLeave35());
					lastyearLeave.setAllowDays(empLeave.getLeave33());
				}
				this.empLeaveMapper.updateById(lastyearLeave);
			}
			break;
		case "2":
			// 病假
			param.setType(ConfigConstants.LEAVE_TYPE_2);
			EmpLeave sickLeave = this.empLeaveMapper.getListByCondition(param).get(0);
			// 判断是否有修改
			if (sickLeave.getUsedDays().doubleValue() != empLeave.getLeave17().doubleValue()) {
				saveLeaveRecord(empLeave.getEmployeeId(), 2, empLeave.getLeave17().doubleValue(),
						sickLeave.getUsedDays().doubleValue(), 0, user, 0, empLeave.getRemark(), sickLeave.getId());
			}
			EmpLeave unSickLeave = null;
			if (this.empLeaveMapper.getListByCondition(param).size() > 1) {
				unSickLeave = this.empLeaveMapper.getListByCondition(param).get(1);
				// 判断是否有修改
				if (unSickLeave.getUsedDays().doubleValue() != empLeave.getLeave18().doubleValue()) {
					saveLeaveRecord(empLeave.getEmployeeId(), 2, empLeave.getLeave18().doubleValue(),
							unSickLeave.getUsedDays().doubleValue(), 0, user, 0, empLeave.getRemark(),
							unSickLeave.getId());
				}
			}
			// leave17;//当年已使用假期天数-带薪病假
			// leave18;//当年已使用假期天数-非带薪病假
			// 修改 带薪病假 通过员工id 查询 为带薪病假的员工在进行修改
			sickLeave.setUsedDays(empLeave.getLeave17());
			sickLeave.setAllowRemainDays(sickLeave.getAllowDays() - empLeave.getLeave17());
			// 修改 非带薪病假 通过员工id 查询 为带薪病假的员工在进行修改
			if (unSickLeave != null) {
				unSickLeave.setUsedDays(empLeave.getLeave18());
				this.empLeaveMapper.updateById(unSickLeave);
			}
			this.empLeaveMapper.updateById(sickLeave);
			break;
		case "3":
			// 调休
			param.setType(ConfigConstants.LEAVE_TYPE_5);
			// 今年调休
			// leave11;//剩余假期天数-调休小时数
			// leave20;//当年已使用调休
			List<EmpLeave> listByCondition = this.empLeaveMapper.getListByCondition(param);
			EmpLeave overtimeLeave = new EmpLeave();
			if (listByCondition != null && listByCondition.size() > 0) {
				overtimeLeave = listByCondition.get(0);
				// 判断是否有改动
				if (empLeave.getLeave20().doubleValue() != overtimeLeave.getUsedDays().doubleValue()) {
					saveLeaveRecord(empLeave.getEmployeeId(), 5, empLeave.getLeave20().doubleValue(),
							overtimeLeave.getUsedDays().doubleValue(), 1, user, 0, empLeave.getRemark(), overtimeLeave.getId());
				}
				if (empLeave.getLeave11().doubleValue() != overtimeLeave.getAllowRemainDays().doubleValue()) {
					saveLeaveRecord(empLeave.getEmployeeId(), 5, empLeave.getLeave11().doubleValue(),
							overtimeLeave.getAllowRemainDays().doubleValue(), 1, user, 2, empLeave.getRemark(),
							overtimeLeave.getId());
				}

				overtimeLeave.setUsedDays(empLeave.getLeave20());
				overtimeLeave.setAllowRemainDays(empLeave.getLeave11());
				this.empLeaveMapper.updateById(overtimeLeave);
			} else {
				EmpLeave total = new EmpLeave();
				total.setYear(year);
				total.setUsedDays(empLeave.getLeave20());
				total.setAllowDays(80d);
				total.setAllowRemainDays(empLeave.getLeave11());
				total.setEmployeeId(empLeave.getEmployeeId());
				total.setDelFlag(0);
				total.setType(5);
				total.setCategory(0);
				total.setParendId(0L);
				total.setIsActive(0);
				total.setCreateTime(new Date());
				total.setUpdateTime(new Date());
				total.setCompanyId(1L);
				total.setUpdateUser("system");
				total.setCreateUser("system");
				total.setStartTime(DateUtils.parse(String.valueOf(year) + "-01-01 00:00:00",
						DateUtils.FORMAT_LONG));
//				total.setEndTime(DateUtils.parse(String.valueOf(year) + "-12-31 23:59:59",
//						DateUtils.FORMAT_LONG));
				total.setEndTime(DateUtils.parse(String.valueOf(year+1) + "-02-29 23:59:59",
						DateUtils.FORMAT_LONG));
				this.save(total);

				// 判断是否有改动
				if (empLeave.getLeave20().doubleValue() != 0) {
					saveLeaveRecord(empLeave.getEmployeeId(), 5, empLeave.getLeave20().doubleValue(), 0, 1, user, 0,
							empLeave.getRemark(), total.getId());
				}
				if (empLeave.getLeave11().doubleValue() != 0) {
					saveLeaveRecord(empLeave.getEmployeeId(), 5, empLeave.getLeave11().doubleValue(), 0, 1, user, 2,
							empLeave.getRemark(), total.getId());
				}
			}

			// 去年调休
			// leave36;//去年剩余调休小时数
			// leave37;//去年已用调休小时数
			param.setParendId(null);
			param.setYear(year-1);
			List<EmpLeave> lastYearOvertimeLeaveList = this.empLeaveMapper.getListByCondition(param);
			if (lastYearOvertimeLeaveList != null && lastYearOvertimeLeaveList.size() > 0) {
				EmpLeave lastYearOvertimeLeave = lastYearOvertimeLeaveList.get(0);

				// 判断是否有改动
				if (empLeave.getLeave37().doubleValue() != lastYearOvertimeLeave.getUsedDays().doubleValue()) {
					saveLeaveRecord(empLeave.getEmployeeId(), 5, empLeave.getLeave37().doubleValue(),
							lastYearOvertimeLeave.getUsedDays().doubleValue(), 1, user, 0, empLeave.getRemark(),
							lastYearOvertimeLeave.getId());
				}
				if (empLeave.getLeave36().doubleValue() != lastYearOvertimeLeave.getAllowRemainDays().doubleValue()) {
					saveLeaveRecord(empLeave.getEmployeeId(), 5, empLeave.getLeave36().doubleValue(),
							lastYearOvertimeLeave.getAllowRemainDays().doubleValue(), 1, user, 2, empLeave.getRemark(),
							lastYearOvertimeLeave.getId());
				}

				lastYearOvertimeLeave.setUsedDays(empLeave.getLeave37());
				lastYearOvertimeLeave.setAllowRemainDays(empLeave.getLeave36());
				this.empLeaveMapper.updateById(lastYearOvertimeLeave);
			}else{
				EmpLeave total = new EmpLeave();
				total.setYear(year-1);
				total.setUsedDays(empLeave.getLeave37());
				total.setAllowDays(80d);
				total.setAllowRemainDays(empLeave.getLeave36());
				total.setEmployeeId(empLeave.getEmployeeId());
				total.setDelFlag(0);
				total.setType(5);
				total.setCategory(0);
				total.setParendId(0L);
				total.setIsActive(0);
				total.setCreateTime(new Date());
				total.setUpdateTime(new Date());
				total.setCompanyId(1L);
				total.setUpdateUser("system");
				total.setCreateUser("system");
				total.setStartTime(DateUtils.parse(String.valueOf(year-1) + "-01-01 00:00:00",
						DateUtils.FORMAT_LONG));
//				total.setEndTime(DateUtils.parse(String.valueOf(year-1) + "-12-31 23:59:59",
//						DateUtils.FORMAT_LONG));
				total.setEndTime(DateUtils.parse(String.valueOf(year) + "-02-29 23:59:59",
						DateUtils.FORMAT_LONG));
				this.save(total);
				
				// 判断是否有改动
				if (empLeave.getLeave37().doubleValue() != 0) {
					saveLeaveRecord(empLeave.getEmployeeId(), 5, empLeave.getLeave37().doubleValue(), 0, 1, user, 0,
							empLeave.getRemark(), total.getId());
				}
				if (empLeave.getLeave36().doubleValue() != 0) {
					saveLeaveRecord(empLeave.getEmployeeId(), 5, empLeave.getLeave36().doubleValue(), 0, 1, user, 2,
							empLeave.getRemark(), total.getId());
				}
			}
			break;
		case "4":
			// 其他
			for (Integer leaveType : Arrays.asList(ConfigConstants.getOtherleaveType())) {
				param.setType(leaveType);
				param.setIsActive(0);
				EmpLeave oldLeave = this.getByCondition(param);
				if (null == oldLeave || oldLeave.getId() == null) {
					param.setDelFlag(0);
					if (leaveType.intValue() == 3 && empLeave.getLeave21().doubleValue() != 0) {// 已用婚假

						param.setUsedDays(empLeave.getLeave21());
						this.empLeaveMapper.save(param);
						saveLeaveRecord(empLeave.getEmployeeId(), 3, empLeave.getLeave21().doubleValue(), 0, 0, user, 0,
								empLeave.getRemark(), param.getId());

					} else if (leaveType.intValue() == 4 && empLeave.getLeave26().doubleValue() != 0) {// 已用哺乳假

						param.setUsedDays(empLeave.getLeave26());
						this.empLeaveMapper.save(param);
						saveLeaveRecord(empLeave.getEmployeeId(), 4, empLeave.getLeave26().doubleValue(), 0, 0, user, 0,
								empLeave.getRemark(), param.getId());

					} else if (leaveType.intValue() == 6 && empLeave.getLeave24().doubleValue() != 0) {// 已用产前假

						param.setUsedDays(empLeave.getLeave24());
						this.empLeaveMapper.save(param);
						saveLeaveRecord(empLeave.getEmployeeId(), 6, empLeave.getLeave24().doubleValue(), 0, 0, user, 0,
								empLeave.getRemark(), param.getId());

					} else if (leaveType.intValue() == 7 && empLeave.getLeave25().doubleValue() != 0) {

						param.setUsedDays(empLeave.getLeave25());
						this.empLeaveMapper.save(param);
						saveLeaveRecord(empLeave.getEmployeeId(), 7, empLeave.getLeave25().doubleValue(), 0, 0, user, 0,
								empLeave.getRemark(), param.getId());

					} else if (leaveType.intValue() == 8 && empLeave.getLeave27().doubleValue() != 0) {

						param.setUsedDays(empLeave.getLeave27());
						this.empLeaveMapper.save(param);
						saveLeaveRecord(empLeave.getEmployeeId(), 8, empLeave.getLeave27().doubleValue(), 0, 0, user, 0,
								empLeave.getRemark(), param.getId());

					} else if (leaveType.intValue() == 9 && empLeave.getLeave23().doubleValue() != 0) {
						param.setUsedDays(empLeave.getLeave23());
						this.empLeaveMapper.save(param);
						saveLeaveRecord(empLeave.getEmployeeId(), 9, empLeave.getLeave23().doubleValue(), 0, 0, user, 0,
								empLeave.getRemark(), param.getId());

					} else if (leaveType.intValue() == 10 && empLeave.getLeave22().doubleValue() != 0) {

						param.setUsedDays(empLeave.getLeave22());
						this.empLeaveMapper.save(param);
						saveLeaveRecord(empLeave.getEmployeeId(), 10, empLeave.getLeave22().doubleValue(), 0, 0, user,
								0, empLeave.getRemark(), param.getId());

					} else if (leaveType.intValue() == 11 && empLeave.getLeave19().doubleValue() != 0) {

						param.setUsedDays(empLeave.getLeave19());
						this.empLeaveMapper.save(param);
						saveLeaveRecord(empLeave.getEmployeeId(), 11, empLeave.getLeave19().doubleValue(), 0, 0, user,
								0, empLeave.getRemark(), param.getId());

					} else if (leaveType.intValue() == 12 && empLeave.getLeave28().doubleValue() != 0) {

						param.setUsedDays(empLeave.getLeave28());
						this.empLeaveMapper.save(param);
						saveLeaveRecord(empLeave.getEmployeeId(), 12, empLeave.getLeave28().doubleValue(), 0, 0, user,
								0, empLeave.getRemark(), param.getId());

					}
					param.setId(null);
				} else {
					if (leaveType.intValue() == 3) {
						if (oldLeave.getUsedDays().doubleValue() - empLeave.getLeave21().doubleValue() != 0) {
							saveLeaveRecord(empLeave.getEmployeeId(), 3, empLeave.getLeave21().doubleValue(),
									oldLeave.getUsedDays().doubleValue(), 0, user, 0, empLeave.getRemark(),
									oldLeave.getId());
							oldLeave.setUsedDays(empLeave.getLeave21());
						}
					} else if (leaveType.intValue() == 4) {
						if (oldLeave.getUsedDays().doubleValue() - empLeave.getLeave26().doubleValue() != 0) {
							saveLeaveRecord(empLeave.getEmployeeId(), 4, empLeave.getLeave26().doubleValue(),
									oldLeave.getUsedDays().doubleValue(), 0, user, 0, empLeave.getRemark(),
									oldLeave.getId());
							oldLeave.setUsedDays(empLeave.getLeave26());
						}
					} else if (leaveType.intValue() == 6) {
						if (oldLeave.getUsedDays().doubleValue() - empLeave.getLeave24().doubleValue() != 0) {
							saveLeaveRecord(empLeave.getEmployeeId(), 6, empLeave.getLeave24().doubleValue(),
									oldLeave.getUsedDays().doubleValue(), 0, user, 0, empLeave.getRemark(),
									oldLeave.getId());
							oldLeave.setUsedDays(empLeave.getLeave24());
						}
					} else if (leaveType.intValue() == 7) {
						if (oldLeave.getUsedDays().doubleValue() - empLeave.getLeave25().doubleValue() != 0) {
							saveLeaveRecord(empLeave.getEmployeeId(), 7, empLeave.getLeave25().doubleValue(),
									oldLeave.getUsedDays().doubleValue(), 0, user, 0, empLeave.getRemark(),
									oldLeave.getId());
							oldLeave.setUsedDays(empLeave.getLeave25());
						}
					} else if (leaveType.intValue() == 8) {
						if (oldLeave.getUsedDays().doubleValue() - empLeave.getLeave27().doubleValue() != 0) {
							saveLeaveRecord(empLeave.getEmployeeId(), 8, empLeave.getLeave27().doubleValue(),
									oldLeave.getUsedDays().doubleValue(), 0, user, 0, empLeave.getRemark(),
									oldLeave.getId());
							oldLeave.setUsedDays(empLeave.getLeave27());
						}
					} else if (leaveType.intValue() == 9) {
						if (oldLeave.getUsedDays().doubleValue() - empLeave.getLeave23().doubleValue() != 0) {
							saveLeaveRecord(empLeave.getEmployeeId(), 9, empLeave.getLeave23().doubleValue(),
									oldLeave.getUsedDays().doubleValue(), 0, user, 0, empLeave.getRemark(),
									oldLeave.getId());
							oldLeave.setUsedDays(empLeave.getLeave23());
						}
					} else if (leaveType.intValue() == 10) {
						if (oldLeave.getUsedDays().doubleValue() - empLeave.getLeave22().doubleValue() != 0) {
							saveLeaveRecord(empLeave.getEmployeeId(), 10, empLeave.getLeave22().doubleValue(),
									oldLeave.getUsedDays().doubleValue(), 0, user, 0, empLeave.getRemark(),
									oldLeave.getId());
							oldLeave.setUsedDays(empLeave.getLeave22());
						}
					} else if (leaveType.intValue() == 11) {
						if (oldLeave.getUsedDays().doubleValue() - empLeave.getLeave19().doubleValue() != 0) {
							saveLeaveRecord(empLeave.getEmployeeId(), 11, empLeave.getLeave19().doubleValue(),
									oldLeave.getUsedDays().doubleValue(), 0, user, 0, empLeave.getRemark(),
									oldLeave.getId());
							oldLeave.setUsedDays(empLeave.getLeave19());
						}
					} else if (leaveType.intValue() == 12) {
						if (oldLeave.getUsedDays().doubleValue() - empLeave.getLeave28().doubleValue() != 0) {
							saveLeaveRecord(empLeave.getEmployeeId(), 12, empLeave.getLeave28().doubleValue(),
									oldLeave.getUsedDays().doubleValue(), 0, user, 0, empLeave.getRemark(),
									oldLeave.getId());
							oldLeave.setUsedDays(empLeave.getLeave28());
						}
					}
					this.empLeaveMapper.updateById(oldLeave);
				}
			}
			break;
		default:
			break;
		}
		result.put("sucess", true);
		result.put("msg", "假期数据修改成功");
		return result;
	}

	/**
	 * 根据入职时间算出假期基数
	 * 
	 * @param firstEntryTime
	 * @param empInfo
	 * @return
	 */
	public Map<String, Double> getRadiuxByEntryTime(EmployeeApp empInfo,String year,Integer betDays) {
		Map<String, Double> radiuxMap = new HashMap<String, Double>();
		radiuxMap.put("fixRaduix", ConfigConstants.DEFAULE_YEAR);
		radiuxMap.put("welfareRaduix", ConfigConstants.DEFAULE_YEAR);
		// 查询假期基数配置表
		Map<String, Object> leaveMap = getLeaveRaduixConfig();
		// 判断是否今年入职，如果今年入职基数设为默认5
		int entryYear = Integer.parseInt(DateUtils.getYear(empInfo.getFirstEntryTime()));
		// 获得今年年份
		int thisYear = Integer.parseInt(DateUtils.getYear(new Date()));
		if(StringUtils.isNotBlank(year)) {
			thisYear = thisYear - 1;
		}
		
		// 查询年假基数
		// 判断是否在今年之前入职
		if (entryYear < thisYear) {
			try {
				// 计算工龄
				Map<String, Object> ourAgeMap = getWorkYear(empInfo);
				Integer calOurAge = (Integer) ourAgeMap.get("calOurAge");
				// 司龄
				Double ourAge = (empInfo.getOurAge()!=null?empInfo.getOurAge():0d)-(betDays/365d);
				if (calOurAge < 10) {
					radiuxMap.put("fixRaduix", 5.0);
					Double welfareRaduix = CommonUtils.converToDouble(leaveMap.get("5_" + ourAge.intValue()));// 新的福利假期基数
					radiuxMap.put("welfareRaduix", welfareRaduix);
				} else if (calOurAge >= 10 && calOurAge < 20) {
					// 总工龄满10不满20
					radiuxMap.put("fixRaduix", 10.0);
					// 在司工龄与福利年假挂钩
					if (ourAge >= 10) {
						ourAge = 9d;
					}
					Double welfareRaduix = CommonUtils.converToDouble(leaveMap.get("10_" + ourAge.intValue()));
					radiuxMap.put("welfareRaduix", welfareRaduix);
				} else if (calOurAge >= 20) {// 总工龄满20年，则法定基数为15，福利为0
					radiuxMap.put("fixRaduix", 15.0);
					radiuxMap.put("welfareRaduix", 0.0);
				}

			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}

		} else {
			// 设为默认
			radiuxMap.put("fixRaduix", ConfigConstants.DEFAULE_YEAR);
			radiuxMap.put("welfareRaduix", ConfigConstants.DEFAULE_YEAR);
		}

		return radiuxMap;

	}

	/** 
	 * @throws OaException
	 *             getWorkYear(计算员工司龄) @Title: getOurAge @Description:
	 *             计算员工司龄 @param emp @return 设定文件 Double 返回类型 @throws
	 */
	public Map<String, Object> getWorkYear(EmployeeApp emp) throws OaException {
		Date joinInTime = emp.getFirstEntryTime();// 入职时间
		// 入职前司龄
		Double beforeWorkAge = emp.getBeforeWorkAge();
		if (null == joinInTime) {
			throw new OaException("员工{}入职时间为空，无法更新", emp.getCnName());
		}

		Map<String, Object> ourAgeMap = new HashMap<String, Object>();
		Double acuOurAge = 0.0;// 实际司龄（精确到一位小树）
		Integer calOurAge = 0;// 计算司龄，不满1年按0算
		Date sysDate = emp.getQuitTime() == null ? new Date() : emp.getQuitTime();// 当前时间,若有离职时间按离职时间算
		// 计算司龄_公司司龄
		acuOurAge = DateUtils.getJoinYear(joinInTime, sysDate) + beforeWorkAge.intValue();
		calOurAge = acuOurAge.intValue();

		ourAgeMap.put("calOurAge", calOurAge);// 计算司龄，不满1年按0算
		ourAgeMap.put("acuOurAge", acuOurAge);// 实际司龄，不满1年按四舍五入保留一位小数算

		return ourAgeMap;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void reduceYearLeave(Long employeeId,List<String> dateList,String employeeName,String workTypeDisplayCode,String schedulingDisplayCode) throws Exception{
		List<EmpLeave> planLeaves = new ArrayList<EmpLeave>();
		EmpLeave planLeaveP = new EmpLeave();
		planLeaveP.setType(ConfigConstants.LEAVE_TYPE_1);
		planLeaveP.setEmployeeId(employeeId);
		planLeaveP.setPlanStartTime(DateUtils.parse(dateList.get(0)+" 09:00:00", DateUtils.FORMAT_LONG));
		planLeaveP.setPlanEndTime(DateUtils.parse(dateList.get(dateList.size()-1)+" 18:00:00", DateUtils.FORMAT_LONG));
		planLeaveP.setPlanDays((double) dateList.size());
		planLeaves.add(planLeaveP);
		
		//生成假期流水
		LeaveRecord record = new LeaveRecord();
		record.setEmployeeId(employeeId);
		record.setType(ConfigConstants.LEAVE_TYPE_1);
		record.setDays((double) dateList.size());
		record.setBillId(0L);
		record.setBillType(ConfigConstants.LEAVE_KEY);
		record.setDaysUnit(0);// 单位
		record.setCreateTime(new Date());
		record.setCreateUser("system");
		record.setDelFlag(0);
		record.setSource(0);
		record.setRemark("2021春节提前放假，年假统一减"+dateList.size()+"天");
		leaveRecordMapper.save(record);
		
		
		for (EmpLeave planLeave : planLeaves) {
			Double planDays = planLeave.getPlanDays();//计划扣减天数
			String optUser = "system";

			Map<String, Object> totalLeave = empLeaveMapper.getMaxAllowDays(planLeave);
			if (null == totalLeave || totalLeave.size() == 0) {
				throw new OaException("您的当前可用年假为空");
			}
			// 计划请假天数必须 <= (允许请假天数 - 总的占用天数)
			Double totalAllowRemainDays = Double.valueOf(totalLeave.get("allowRemainDays") + "");// 总的允许可休假剩余天数
			Double totalBlockedDays = Double.valueOf(totalLeave.get("blockedDays") + "");// 总的占用天数
			if (planDays > totalAllowRemainDays - totalBlockedDays) {
				logger.error("当前可用年假小于"+dateList.size()+"天，不能系统扣除");
				throw new OaException("当前可用年假小于"+dateList.size()+"天，不能系统扣除");
			}

			// 2.步骤1校验通过后，分年扣减假期天数
			List<EmpLeave> leaves = empLeaveMapper.getAllowDays(planLeave);
			Integer year = null;
			Double remainPlanDays = planDays;// 剩余计划休假天数
			Double remainBlockedDays = planDays;// 剩余扣减的占用天数
			loop: for (EmpLeave empLeave : leaves) {
				EmpLeave leave = empLeaveMapper.getById(empLeave.getId());
				leave.setUpdateUser(optUser);
				leave.setUpdateTime(new Date());

				// 总的年假
				EmpLeave totalleave = empLeave;
				totalleave = empLeaveMapper.getById(empLeave.getId());
				totalleave.setUpdateUser(optUser);

				if (null == year || !year.equals(empLeave.getYear())) {
                    //需要扣减的年检天数
					remainBlockedDays = remainBlockedDays > leave.getAllowRemainDays() - leave.getBlockedDays()
							? (leave.getAllowRemainDays() - leave.getBlockedDays()) : remainBlockedDays;

					totalleave.setUsedDays(totalleave.getUsedDays()+remainBlockedDays);
					totalleave.setAllowRemainDays(totalleave.getAllowRemainDays()-remainBlockedDays);
					empLeaveMapper.updateById(totalleave);
					remainBlockedDays = planDays > remainBlockedDays ? planDays - remainBlockedDays
							: remainBlockedDays;

					year = empLeave.getYear();
					continue;
				}

				if (remainPlanDays <= leave.getAllowRemainDays()) {
					LeaveRecordDetail recordDetail = new LeaveRecordDetail();
					recordDetail.setLeaveRecordId(record.getId());
					recordDetail.setBaseEmpLeaveId(leave.getId());
					recordDetail.setDays(remainPlanDays);
					recordDetail.setCreateTime(new Date());
					recordDetail.setCreateUser("system");
					recordDetail.setDelFlag(0);
					leaveRecordDetailMapper.save(recordDetail);
					leave.setUsedDays(leave.getUsedDays()+remainPlanDays);
					leave.setAllowRemainDays(leave.getAllowRemainDays() - remainPlanDays);
					empLeaveMapper.updateById(leave);

					break loop;
				} else {
					LeaveRecordDetail recordDetail = new LeaveRecordDetail();
					recordDetail.setLeaveRecordId(record.getId());
					recordDetail.setBaseEmpLeaveId(leave.getId());
					recordDetail.setDays(leave.getAllowRemainDays());
					recordDetail.setCreateTime(new Date());
					recordDetail.setCreateUser("system");
					recordDetail.setDelFlag(0);
					leaveRecordDetailMapper.save(recordDetail);
					remainPlanDays = remainPlanDays - leave.getAllowRemainDays();
					leave.setUsedDays(leave.getUsedDays()+leave.getAllowRemainDays());
					leave.setAllowRemainDays(0.0);
					empLeaveMapper.updateById(leave);

				}
				year = leave.getYear();
			}
		}
		
		//部门id
		EmpDepart model = new EmpDepart();
		model.setEmployeeId(employeeId);
		List<EmpDepart> empDapartList = empDepartMapper.getListByCondition(model);
		
		//生成考勤数据和报表数据
		for(String date:dateList){
			//考勤数据
			AttnWorkHours condition = new AttnWorkHours();
			condition.setCompanyId(1L);
			condition.setEmployeeId(employeeId);
			condition.setWorkDate(DateUtils.parse(date, DateUtils.FORMAT_SHORT));
			condition.setDataType(60);
			condition.setWorkHours(8d);
			condition.setCreateUser("system");
			if("standard".equals(workTypeDisplayCode)&&schedulingDisplayCode.equals("yes")){
				EmployeeClass employeeClass = new EmployeeClass();
				employeeClass.setEmployId(employeeId);
				employeeClass.setClassDate(DateUtils.parse(date, DateUtils.FORMAT_SHORT));
				EmployeeClass result = employeeClassMapper.getEmployeeClassSetting(employeeClass);
    			if(null != result&&result.getStartTime() != null&&result.getEndTime() != null){
    				condition.setStartTime(DateUtils.parse(date+" "+DateUtils.format(result.getStartTime(), DateUtils.FORMAT_HH_MM_SS), DateUtils.FORMAT_LONG));
    				condition.setEndTime(DateUtils.parse(date+" "+DateUtils.format(result.getEndTime(), DateUtils.FORMAT_HH_MM_SS), DateUtils.FORMAT_LONG));
    			}else{
    				condition.setStartTime(DateUtils.parse(date+" 09:00:00", DateUtils.FORMAT_LONG));
    				condition.setEndTime(DateUtils.parse(date+" 18:00:00", DateUtils.FORMAT_LONG));
    			}
			}else{
			    condition.setStartTime(DateUtils.parse(date+" 09:00:00", DateUtils.FORMAT_LONG));
				condition.setEndTime(DateUtils.parse(date+" 18:00:00", DateUtils.FORMAT_LONG));
			}
			condition.setBillId(0L);
			condition.setLeaveType(1);
			condition.setDelFlag(0);
			condition.setCreateTime(new Date());
			attnWorkHoursService.save(condition);
			
			//报表数据
			EmpLeaveReport reportParam = new EmpLeaveReport();
			reportParam.setEmployeeId(employeeId);
			reportParam.setMonth(date.substring(0,7));
			reportParam.setType(1);
			List<EmpLeaveReport> reportList = empLeaveReportMapper.getList(reportParam);
			
			if(reportList!=null&&reportList.size()>0){
				//修改
				reportList.get(0).setUpdateTime(new Date());
				reportList.get(0).setUpdateUser("system");
				if(Integer.valueOf(date.substring(8,10))==2){
					reportList.get(0).setDate2(reportList.get(0).getDate2()+1);
					empLeaveReportMapper.update(reportList.get(0));
				}else if(Integer.valueOf(date.substring(8,10))==3){
					reportList.get(0).setDate3(reportList.get(0).getDate3()+1);
					empLeaveReportMapper.update(reportList.get(0));
				}
			}else{
				EmpLeaveReport report = new EmpLeaveReport();
				report.setEmployeeId(employeeId);
				report.setEmployeeName(employeeName);
				report.setMonth(date.substring(0,7));
				report.setDelFlag(0);
				report.setDepartId((empDapartList!=null&&empDapartList.size()>0)?empDapartList.get(0).getDepartId():null);
				report.setType(1);
				report.setCreateTime(new Date());
				report.setCreateUser("system");
				//新增
                if(Integer.valueOf(date.substring(8,10))==2){
                	report.setDate2(1);
                	empLeaveReportMapper.save(report);
				}else if(Integer.valueOf(date.substring(8,10))==3){
					report.setDate3(1);
					empLeaveReportMapper.save(report);
				}
                
			}
			
		}
		
		
	}

	@Override
	public Double getOtherRestLeaveCount(Long employeeId) {
		double result = 0;
		Double otherRestLeaveCount = empLeaveMapper.getOtherRestLeaveCount(employeeId);
		if(otherRestLeaveCount!=null){
			result = otherRestLeaveCount;
		}
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void reduceInvalidOtherRestLeave(EmpLeave data) {
		logger.info("其它调休到期，修改假期状态参数:employeeId="+data.getEmployeeId()+";id="+data.getId()+";endTime="+data.getEndTime());
		
		//将该条假期数据改成失效
		EmpLeave condition = new EmpLeave();
		condition.setId(data.getId());
		condition.setVersion(data.getVersion());
		condition.setIsActive(1);
		empLeaveMapper.updateById(condition);
		
		//生成假期失效流水
		LeaveRecord record = new LeaveRecord();
		record.setEmployeeId(data.getEmployeeId());
		record.setType(13);//假期类型-其它调休
		record.setDays(-data.getAllowRemainDays());
		record.setUpdateType(3);//修改类型(0-已用，1-总数,2-剩余，3-过期)
		record.setBillId(0L);
		record.setBillType("leave");//单据类型
		record.setDaysUnit(1);// 单位-小时
		record.setCreateTime(new Date());
		record.setCreateUser("系统");
		record.setDelFlag(0);
		record.setSource(0);
		record.setRemark("其它调休过期失效");
		leaveRecordMapper.save(record);
		LeaveRecordDetail recordDetail = new LeaveRecordDetail();
		recordDetail.setLeaveRecordId(record.getId());
		recordDetail.setBaseEmpLeaveId(data.getId());
		recordDetail.setDays(-data.getAllowRemainDays());
		recordDetail.setCreateTime(new Date());
		recordDetail.setCreateUser("系统");
		recordDetail.setDelFlag(0);
		List<LeaveRecordDetail> isExist = leaveRecordDetailMapper.selectByCondition(recordDetail);
		if (isExist != null && isExist.size() > 0) {
			isExist.get(0).setDays(isExist.get(0).getDays() - data.getAllowRemainDays());
			leaveRecordDetailMapper.updateById(isExist.get(0));
		} else {
			leaveRecordDetailMapper.save(recordDetail);
		}
		
	}

	@Override
	public List<EmpLeave> getInvalidOtherRestLeaveList(Date endTime) {
		return empLeaveMapper.getInvalidOtherRestLeaveList(endTime);
	}

	@Override
	public Double getActualYearDays(Date quitTime, Long employeeId)
			throws Exception {
		Double endTodayYearDays = 0d;//截止指定时间年假天数

		EmpLeave param = new EmpLeave();
		// 今年
		Employee employee = this.employeeService.getById(employeeId);
		
		param.setEmployeeId(employeeId);
		param.setYear(Integer.valueOf(DateUtils.format(new Date(), "yyyy")));
		param.setType(1);
		List<EmpLeave> yearLeaves = this.getByListCondition(param);
		Double legalRaduix = 0.0;
		Double welfareRaduix = 0.0;
		Double UsedDays = 0.0;
		Double UsedLeagalDays = 0.0;
		Double UsedWefareDays = 0.0;
		Double BlockedDays = 0.0;
		for (EmpLeave empLeave : yearLeaves) {
			if (empLeave.getCategory().intValue() == 0) {
				UsedDays = empLeave.getUsedDays();
				BlockedDays = empLeave.getBlockedDays();
			} else  if(empLeave.getCategory().intValue() == 1){
				legalRaduix = empLeave.getAllowDays();
			}else  if(empLeave.getCategory().intValue() == 2){
				welfareRaduix = empLeave.getAllowDays();
			}
		}

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, 3);
		Date dayInApril = cal.getTime();
		Date theSixthWorkDay = annualVacationService.getWorkingDayOfMonth(dayInApril, 4);
		theSixthWorkDay = DateUtils.addDay(theSixthWorkDay, 1);
		
		Date thisDay = DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_SHORT), DateUtils.FORMAT_SHORT);
		
		Double LastRemainDays = 0.0;
		Double LastBlockedDays = 0.0;
		//今日可用年假
		if (thisDay.before(theSixthWorkDay)) {
			// 去年
			param.setType(1);
			param.setYear(Integer.valueOf(DateUtils.format(DateUtils.addYear(new Date(), -1), "yyyy")));
			List<EmpLeave> lastyearLeaves = this.getByListCondition(param);
			for (EmpLeave empLeave : lastyearLeaves) {
				if (empLeave.getCategory().intValue() == 0) {
					LastRemainDays = empLeave.getAllowRemainDays();
					LastBlockedDays = empLeave.getBlockedDays();
				} 
			}
		}
		
		if (employee.getQuitTime() != null) {
			quitTime = employee.getQuitTime();
		}
		if (quitTime != null) {
			Integer intervalDays = DateUtils
					.getIntervalDays(DateUtils.isBefore(employee.getFirstEntryTime(), DateUtils.getYearBegin())
							? DateUtils.getYearBegin() : employee.getFirstEntryTime(), quitTime)
					+ 1;
			// 改过基数,年假计算就按查到的来
			// 未改过,动态计算
			if (employee.getAutoCalculateLeave() != null && employee.getAutoCalculateLeave() != 2
					&& employee.getAutoCalculateLeave() != 0) {
				// 员工未被修改过基数,动态算基数
				int joinYear = DateUtils.getJoinYear(employee.getFirstEntryTime(), quitTime).intValue();
				Double workYear = joinYear + employee.getBeforeWorkAge();
				// 获取司龄对应的年假基数
				Map<String, Object> leaveMap = this.getLeaveRaduixConfig();
				if (joinYear < 1) {
					joinYear = 1;
				}
				if (workYear < 10) {
					legalRaduix = 5.0;
					welfareRaduix = CommonUtils.converToDouble(leaveMap.get("5_" + joinYear));
				} else if (workYear >= 10 && workYear < 20) {
					legalRaduix = 10.0;
					welfareRaduix = 5.0;
				} else if (workYear >=20) {
					legalRaduix = 15.0;
					welfareRaduix = 0.0;
				}
			}
			// 动态修改 法定 福利年假
			Double legalLeaveDays = this.getLeaveDays(intervalDays, legalRaduix) - UsedLeagalDays;
			Double welfareLeaveDays = this.getLeaveDays(intervalDays, welfareRaduix) - UsedWefareDays;
			if (legalLeaveDays < 0) {
				welfareLeaveDays = welfareLeaveDays + legalLeaveDays;
			}

			// 离职当前可用总年假 总病假
			endTodayYearDays = 
					this.getLeaveDays(intervalDays, legalRaduix) + this.getLeaveDays(intervalDays, welfareRaduix);
		}
		endTodayYearDays = endTodayYearDays - UsedDays - BlockedDays+LastRemainDays -LastBlockedDays;
		return endTodayYearDays;
	}

	@Override
	public String reduceAffairAndYearLeave(MultipartFile file)
			throws OaException, Exception {
		logger.info("扣除员工年假事假-----开始");
		try {
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
			List<List<Object>> excelList = ExcelUtil.readExcel(file, fileName, 6);
			int count = 0; // 插入总条数
			int failCount = 0; // 失败条数
			StringBuffer msg = new StringBuffer(); // 返回信息
			if (excelList == null || excelList.size() < 2) { // 不包括表头
				logger.error(fileName + "文件内容为空");
				throw new OaException(fileName + "文件内容为空");
			}
			count = excelList.size() - 1;
			for (int i = 1; i < excelList.size(); i++) {
				try{
					// 获得行数据
					List<Object> linkedList = excelList.get(i);
					// 非空校验
					if (linkedList.get(0)==null) {
						msg.append(" \n 第" + i + "条数据：员工编号不能为空");
						failCount++;
						continue;
					}
					String code = (String) linkedList.get(0);
					Employee employee = employeeMapper.getInfoByCode(code);
					if(employee==null){
						msg.append(" \n 第" + i + "条数据：员工编号查不到对应员工信息");
						failCount++;
						continue;
					}
					List<Date> dateList = new ArrayList<Date>();
					if(linkedList.get(2)!=null&&!"".equals(linkedList.get(2))){
						dateList.add((Date) linkedList.get(2));
					}
					if(linkedList.get(3)!=null&&!"".equals(linkedList.get(3))){
						dateList.add((Date) linkedList.get(3));
					}
					if(linkedList.get(4)!=null&&!"".equals(linkedList.get(4))){
						dateList.add((Date) linkedList.get(4));
					}
					if(linkedList.get(5)!=null&&!"".equals(linkedList.get(5))){
						dateList.add((Date) linkedList.get(5));
					}
					if(linkedList.get(6)!=null&&!"".equals(linkedList.get(6))){
						dateList.add((Date) linkedList.get(6));
					}
					reduceAffairAndYearLeaveByEmp(employee,dateList);
				}catch(Exception e){
					logger.error("扣除第"+i+"年假事假-----失败，原因="+e.getMessage());
				}
			}
			msg.append(" \n 本次共导入" + count + "条数据，导入失败" + failCount + "条");
			logger.info("扣除员工年假事假-----结束");
			return msg.toString();
		} catch (Exception e) {
			logger.error("扣除员工年假事假-----失败，原因="+e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public void reduceAffairAndYearLeaveByEmp(Employee employee,List<Date> dateList){
		logger.info("扣除员工="+employee.getCnName()+"年假事假-----开始");
		Date noticeAffairStartTime = null;
		Date noticeYearStartTime = null;
		Date noticeAffairEndTime = null;
		Date noticeYearEndTime = null;
		Double noticeAffairDays = 0.0;
		Double noticeYearDays = 0.0;
		
		for(Date data:dateList){
			//查询改天的班次
			EmployeeClass employeeClass = new EmployeeClass();
			employeeClass.setEmployId(employee.getId());
			employeeClass.setClassDate(data);
			EmployeeClass result = employeeClassService.getEmployeeClassSetting(employeeClass);
			if(result!=null&&result.getStartTime()!=null){
				Double reduceDays = 1.0;//需要扣除的假期天数
				if(result.getMustAttnTime()<=8){
					reduceDays = 1.0;
				}else if(result.getMustAttnTime()>8&&result.getMustAttnTime()<=12){
					reduceDays = 1.5;
				}else if(result.getMustAttnTime()>12){
					reduceDays = 2.0;
				}
				//查询截止当前的年假天数
				double surplusYearLeave = 0.0;
				try {
					 surplusYearLeave = getActualYearDays(data,employee.getId());
				} catch (Exception e) {}
				if(surplusYearLeave<=0){
					//不存在年假，直接扣除事假
					AttnStatisticsUtil attnStatisticsUtil= new AttnStatisticsUtil();
					
					String AstartTime = " " +DateUtils.format(result.getStartTime(), DateUtils.FORMAT_HH_MM_SS);
					String AendTime = " " +DateUtils.format(result.getEndTime(), DateUtils.FORMAT_HH_MM_SS);
					
					generateAffairLeaveRecord(employee,data,AstartTime,AendTime,reduceDays);
					//事假考勤部分
					Date affairStartTime = DateUtils.parse(DateUtils.format(data, DateUtils.FORMAT_SHORT)+AstartTime, DateUtils.FORMAT_LONG);
					Date affairEndTime = DateUtils.parse(DateUtils.format(data, DateUtils.FORMAT_SHORT)+AendTime, DateUtils.FORMAT_LONG);
					attnStatisticsUtil.calWorkHours(1L,employee.getId(),"system",data,9.0,"60",affairStartTime,affairEndTime,"11","",0L);
					//事假邮件通知
					if(noticeAffairStartTime == null) {
						noticeAffairStartTime = affairStartTime;
					}
					noticeAffairEndTime = affairEndTime;
					noticeAffairDays = noticeAffairDays + reduceDays;
				}else{
					if(surplusYearLeave>=reduceDays){
						
						//年假充足，直接扣除年假
						//年假流水部分
						String startTime = " " +DateUtils.format(result.getStartTime(), DateUtils.FORMAT_HH_MM_SS);
						String endTime = " " +DateUtils.format(result.getEndTime(), DateUtils.FORMAT_HH_MM_SS);
						boolean isCalAttn = generateYearLeaveRecord(employee,data,startTime,endTime,reduceDays);
						
						//年假考勤部分
						AttnStatisticsUtil attnStatisticsUtil= new AttnStatisticsUtil();
						if(isCalAttn){
							Date yearStartTime = DateUtils.parse(DateUtils.format(data, DateUtils.FORMAT_SHORT)+startTime, DateUtils.FORMAT_LONG);
							Date yearEndTime = DateUtils.parse(DateUtils.format(data, DateUtils.FORMAT_SHORT)+endTime, DateUtils.FORMAT_LONG);
							attnStatisticsUtil.calWorkHours(1L,employee.getId(),"system",data,9.0,"60",yearStartTime,yearEndTime,"1","",0L);
						    //年假邮件通知
							if(noticeYearStartTime == null) {
								noticeYearStartTime = yearStartTime;
							}
							noticeYearEndTime = yearEndTime;
							noticeYearDays = noticeYearDays + reduceDays;
						}
					}else{
						//年假不够，先扣年假，再扣事假
						Double reduceAffairLeaveDay =  reduceDays - surplusYearLeave;
						boolean standard = ("09:00".equals(DateUtils.format(result.getStartTime(), "HH:mm"))
								&& "18:00".equals(DateUtils.format(result.getEndTime(), "HH:mm"))) ? true : false;
						if(surplusYearLeave==0.5){
							if(standard){
								//9:00-14:00年假，12:00-18:00事假
								String YstartTime = " " +DateUtils.format(result.getStartTime(), DateUtils.FORMAT_HH_MM_SS);
								String YendTime = " " +DateUtils.format(DateUtils.addHour(result.getStartTime(), 5), DateUtils.FORMAT_HH_MM_SS);
								
								boolean isCalAttn = generateYearLeaveRecord(employee,data,YstartTime,YendTime,surplusYearLeave);
								
								//年假考勤部分
								AttnStatisticsUtil attnStatisticsUtil= new AttnStatisticsUtil();
								if(isCalAttn){
									Date yearStartTime = DateUtils.parse(DateUtils.format(data, DateUtils.FORMAT_SHORT)+YstartTime, DateUtils.FORMAT_LONG);
									Date yearEndTime = DateUtils.parse(DateUtils.format(data, DateUtils.FORMAT_SHORT)+YendTime, DateUtils.FORMAT_LONG);
									attnStatisticsUtil.calWorkHours(1L,employee.getId(),"system",data,5.0,"60",yearStartTime,yearEndTime,"1","",0L);
									//年假邮件通知
									if(noticeYearStartTime == null) {
										noticeYearStartTime = yearStartTime;
									}
									noticeYearEndTime = yearEndTime;
									noticeYearDays = noticeYearDays + surplusYearLeave;
								}
								
								String AstartTime = " " +DateUtils.format(DateUtils.addHour(result.getStartTime(), 3), DateUtils.FORMAT_HH_MM_SS);
								String AendTime = " " +DateUtils.format(result.getEndTime(), DateUtils.FORMAT_HH_MM_SS);
								
								generateAffairLeaveRecord(employee,data,AstartTime,AendTime,reduceAffairLeaveDay);
								//事假考勤部分
								Date affairStartTime = DateUtils.parse(DateUtils.format(data, DateUtils.FORMAT_SHORT)+AstartTime, DateUtils.FORMAT_LONG);
								Date affairEndTime = DateUtils.parse(DateUtils.format(data, DateUtils.FORMAT_SHORT)+AendTime, DateUtils.FORMAT_LONG);
								attnStatisticsUtil.calWorkHours(1L,employee.getId(),"system",data,5.0,"60",affairStartTime,affairEndTime,"11","",0L);
								//事假邮件通知
								if(noticeAffairStartTime == null) {
									noticeAffairStartTime = affairStartTime;
								}
								noticeAffairEndTime = affairEndTime;
								noticeAffairDays = noticeAffairDays + reduceAffairLeaveDay;
							
							}else{
								//8:00-13:00年假，12:00-17:00事假
								String YstartTime = " " +DateUtils.format(result.getStartTime(), DateUtils.FORMAT_HH_MM_SS);
								String YendTime = " " +DateUtils.format(DateUtils.addHour(result.getStartTime(), 5), DateUtils.FORMAT_HH_MM_SS);
								
								boolean isCalAttn = generateYearLeaveRecord(employee,data,YstartTime,YendTime,surplusYearLeave);
								
								//年假考勤部分
								AttnStatisticsUtil attnStatisticsUtil= new AttnStatisticsUtil();
								if(isCalAttn){
									Date yearStartTime = DateUtils.parse(DateUtils.format(data, DateUtils.FORMAT_SHORT)+YstartTime, DateUtils.FORMAT_LONG);
									Date yearEndTime = DateUtils.parse(DateUtils.format(data, DateUtils.FORMAT_SHORT)+YendTime, DateUtils.FORMAT_LONG);
									attnStatisticsUtil.calWorkHours(1L,employee.getId(),"system",data,5.0,"60",yearStartTime,yearEndTime,"1","",0L);
									//年假邮件通知
									if(noticeYearStartTime == null) {
										noticeYearStartTime = yearStartTime;
									}
									noticeYearEndTime = yearEndTime;
									noticeYearDays = noticeYearDays + surplusYearLeave;
								}
								
								String AstartTime = " " +DateUtils.format(DateUtils.addHour(result.getStartTime(), 4), DateUtils.FORMAT_HH_MM_SS);
								String AendTime = " " +DateUtils.format(result.getEndTime(), DateUtils.FORMAT_HH_MM_SS);
								
								generateAffairLeaveRecord(employee,data,AstartTime,AendTime,reduceAffairLeaveDay);
								//事假考勤部分
								Date affairStartTime = DateUtils.parse(DateUtils.format(data, DateUtils.FORMAT_SHORT)+AstartTime, DateUtils.FORMAT_LONG);
								Date affairEndTime = DateUtils.parse(DateUtils.format(data, DateUtils.FORMAT_SHORT)+AendTime, DateUtils.FORMAT_LONG);
								attnStatisticsUtil.calWorkHours(1L,employee.getId(),"system",data,5.0,"60",affairStartTime,affairEndTime,"11","",0L);
								//事假邮件通知
								if(noticeAffairStartTime == null) {
									noticeAffairStartTime = affairStartTime;
								}
								noticeAffairEndTime = affairEndTime;
								noticeAffairDays = noticeAffairDays + reduceAffairLeaveDay;
							}
						}
					}
				}
			}else{
				logger.info("员工="+employee.getCnName()+"在"+DateUtils.format(data, DateUtils.FORMAT_SHORT)+"班次为空，不做扣除操作");
			}
		}
		
		logger.info("扣除员工="+employee.getCnName()+"年假事假-----结束");
	  
		//发送系统扣假通知
		try {
			
			if(noticeYearDays+noticeAffairDays>0) {
				String title = "MO系统扣假通知";
				String content = "Dear "+employee.getCnName()+":<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 公司安排的休假，";
				
				if(noticeYearDays>0) {
					content += DateUtils.format(noticeYearStartTime, DateUtils.FORMAT_LONG_CN_MM)+
							"至"+DateUtils.format(noticeYearEndTime, DateUtils.FORMAT_LONG_CN_MM)+"已在MO系统中自动扣除年假"+noticeYearDays+"天，";
				}
				if(noticeAffairDays>0) {
					content += DateUtils.format(noticeAffairStartTime, DateUtils.FORMAT_LONG_CN_MM)+
							"至"+DateUtils.format(noticeAffairEndTime, DateUtils.FORMAT_LONG_CN_MM)+"已在MO系统中自动扣除事假"+noticeAffairDays+"天，";
				}
				content += "请知晓！";
				SendMailUtil.sendNormalMail(content, employee.getEmail(),employee.getCnName(), title);
			}
		
		}catch(Exception e) {
			logger.error("发送系统扣假邮件通知失败："+employee.getCnName());
		}
		
	}
	
	public boolean generateYearLeaveRecord(Employee employee,Date data,String startTime,String endTime,Double reduceDays){
		
		boolean isCalAttn = true;
		
		List<EmpLeave> planLeaves = new ArrayList<EmpLeave>();
		EmpLeave planLeaveP = new EmpLeave();
		planLeaveP.setType(ConfigConstants.LEAVE_TYPE_1);
		planLeaveP.setEmployeeId(employee.getId());
		planLeaveP.setPlanStartTime(DateUtils.parse(DateUtils.format(data, DateUtils.FORMAT_SHORT)+startTime, DateUtils.FORMAT_LONG));
		planLeaveP.setPlanEndTime(DateUtils.parse(DateUtils.format(data, DateUtils.FORMAT_SHORT)+endTime, DateUtils.FORMAT_LONG));
		planLeaveP.setPlanDays(reduceDays);
		planLeaves.add(planLeaveP);
		
		LeaveRecord record = new LeaveRecord();
		record.setEmployeeId(employee.getId());
		record.setType(ConfigConstants.LEAVE_TYPE_1);
		record.setDays(reduceDays);
		record.setBillId(0L);
		record.setBillType(ConfigConstants.LEAVE_KEY);
		record.setDaysUnit(0);// 单位
		record.setCreateTime(new Date());
		record.setCreateUser("system");
		record.setDelFlag(0);
		record.setSource(0);
		record.setRemark("系统扣假");
		leaveRecordMapper.save(record);
		
		for (EmpLeave planLeave : planLeaves) {
			Double planDays = planLeave.getPlanDays();//计划扣减天数
			String optUser = "system";

			Map<String, Object> totalLeave = empLeaveMapper.getMaxAllowDays(planLeave);
			if (null == totalLeave || totalLeave.size() == 0) {
				logger.info("扣除员工="+employee.getCnName()+"年假:=您的当前可用年假为空");
				isCalAttn = false;
				break;
			}
			// 计划请假天数必须 <= (允许请假天数 - 总的占用天数)
			Double totalAllowRemainDays = Double.valueOf(totalLeave.get("allowRemainDays") + "");// 总的允许可休假剩余天数
			Double totalBlockedDays = Double.valueOf(totalLeave.get("blockedDays") + "");// 总的占用天数
			if (planDays > totalAllowRemainDays - totalBlockedDays) {
				logger.info("扣除员工="+employee.getCnName()+"年假:=当前可用年假不足");
				isCalAttn = false;
				break;
			}

			// 2.步骤1校验通过后，分年扣减假期天数
			List<EmpLeave> leaves = empLeaveMapper.getAllowDays(planLeave);
			Integer year = null;
			Double remainPlanDays = planDays;// 剩余计划休假天数
			Double remainBlockedDays = planDays;// 剩余扣减的占用天数
			loop: for (EmpLeave empLeave : leaves) {
				EmpLeave leave = empLeaveMapper.getById(empLeave.getId());
				leave.setUpdateUser(optUser);
				leave.setUpdateTime(new Date());

				// 总的年假
				EmpLeave totalleave = empLeave;
				totalleave = empLeaveMapper.getById(empLeave.getId());
				totalleave.setUpdateUser(optUser);

				if (null == year || !year.equals(empLeave.getYear())) {
                    //需要扣减的年检天数
					remainBlockedDays = remainBlockedDays > leave.getAllowRemainDays() - leave.getBlockedDays()
							? (leave.getAllowRemainDays() - leave.getBlockedDays()) : remainBlockedDays;

					totalleave.setUsedDays(totalleave.getUsedDays()+remainBlockedDays);
					totalleave.setAllowRemainDays(totalleave.getAllowRemainDays()-remainBlockedDays);
					empLeaveMapper.updateById(totalleave);
					remainBlockedDays = planDays > remainBlockedDays ? planDays - remainBlockedDays
							: remainBlockedDays;

					year = empLeave.getYear();
					continue;
				}

				if (remainPlanDays <= leave.getAllowRemainDays()) {
					LeaveRecordDetail recordDetail = new LeaveRecordDetail();
					recordDetail.setLeaveRecordId(record.getId());
					recordDetail.setBaseEmpLeaveId(leave.getId());
					recordDetail.setDays(remainPlanDays);
					recordDetail.setCreateTime(new Date());
					recordDetail.setCreateUser("system");
					recordDetail.setDelFlag(0);
					leaveRecordDetailMapper.save(recordDetail);
					leave.setUsedDays(leave.getUsedDays()+remainPlanDays);
					leave.setAllowRemainDays(leave.getAllowRemainDays() - remainPlanDays);
					empLeaveMapper.updateById(leave);

					break loop;
				} else {
					LeaveRecordDetail recordDetail = new LeaveRecordDetail();
					recordDetail.setLeaveRecordId(record.getId());
					recordDetail.setBaseEmpLeaveId(leave.getId());
					recordDetail.setDays(leave.getAllowRemainDays());
					recordDetail.setCreateTime(new Date());
					recordDetail.setCreateUser("system");
					recordDetail.setDelFlag(0);
					leaveRecordDetailMapper.save(recordDetail);
					remainPlanDays = remainPlanDays - leave.getAllowRemainDays();
					leave.setUsedDays(leave.getUsedDays()+leave.getAllowRemainDays());
					leave.setAllowRemainDays(0.0);
					empLeaveMapper.updateById(leave);

				}
				year = leave.getYear();
			}
		}
		
		return isCalAttn;
	}
	
	public void generateAffairLeaveRecord(Employee employee,Date data,String startTime,String endTime,Double reduceDays){
		//事假假期流水部分
		LeaveRecord record1 = new LeaveRecord();
		record1.setEmployeeId(employee.getId());
		record1.setType(ConfigConstants.LEAVE_TYPE_11);
		record1.setDays(reduceDays);
		record1.setBillId(0L);
		record1.setBillType(ConfigConstants.LEAVE_KEY);
		record1.setDaysUnit(0);//单位
		record1.setCreateTime(new Date());
		record1.setCreateUser("system");
		record1.setDelFlag(0);
		record1.setSource(0);
		record1.setRemark("系统扣假");
		leaveRecordMapper.save(record1);
		EmpLeave isExist = new EmpLeave();
		isExist.setType(ConfigConstants.LEAVE_TYPE_11);
		isExist.setYear(2020);
		isExist.setEmployeeId(employee.getId());
		List<EmpLeave> isExistList = new ArrayList<EmpLeave>();
		try {
			isExistList = getByListCondition(isExist);
		} catch (OaException e1) {
		}
		if(isExistList!=null&&isExistList.size()>0){
			isExistList.get(0).setUsedDays(isExistList.get(0).getUsedDays()+reduceDays);
			try {
				updateById(isExistList.get(0));
			} catch (OaException e) {
				
			}
			//流水明细
			LeaveRecordDetail recordDetail = new LeaveRecordDetail();
			recordDetail.setLeaveRecordId(record1.getId());
			recordDetail.setBaseEmpLeaveId(isExistList.get(0).getId());
			recordDetail.setDays(reduceDays);
			recordDetail.setCreateTime(new Date());
			recordDetail.setCreateUser("system");
			recordDetail.setDelFlag(0);
			leaveRecordDetailMapper.save(recordDetail);
		}else{
			EmpLeave empLeave = new EmpLeave();
			empLeave.setCompanyId(1L);
			empLeave.setEmployeeId(employee.getId());
			empLeave.setYear(2020);
			empLeave.setType(ConfigConstants.LEAVE_TYPE_11);
			empLeave.setUsedDays(reduceDays);
			empLeave.setCategory(0);
			empLeave.setIsActive(0);
			empLeave.setCreateTime(new Date());
			empLeave.setCreateUser("system");
			empLeave.setDelFlag(0);
			try {
				save(empLeave);
			} catch (OaException e) {
				
			}
			//流水明细
			LeaveRecordDetail recordDetail = new LeaveRecordDetail();
			recordDetail.setLeaveRecordId(record1.getId());
			recordDetail.setBaseEmpLeaveId(empLeave.getId());
			recordDetail.setDays(reduceDays);
			recordDetail.setCreateTime(new Date());
			recordDetail.setCreateUser("system");
			recordDetail.setDelFlag(0);
			leaveRecordDetailMapper.save(recordDetail);
		}
		
	}

	@Override
	public boolean updateSickEmpLeaveByHrRegister(List<Map<String, Object>> planLeaves) throws Exception {
		boolean flag = false;// 返回状态
		EmpLeave planLeave = null;
		for (Map<String, Object> leaveMap : planLeaves) {
			planLeave = JSONUtils.read(JSONUtils.write(leaveMap), EmpLeave.class);
			// 非空校验
			if (null == planLeave) {
				throw new OaException("您当前的请假信息为空");
			} else {
				if (null == planLeave.getEmployeeId()) {
					throw new OaException("您当前的请假的员工id为空");
				}
				if (null == planLeave.getType()) {
					throw new OaException("您当前的请假的假期类型为空");
				}
				if (null == planLeave.getPlanStartTime()) {
					throw new OaException("您当前的请假的开始时间为空");
				}
				if (null == planLeave.getPlanEndTime()) {
					throw new OaException("您当前的请假的结束时间为空");
				}
				if (null == planLeave.getPlanDays()) {
					throw new OaException("您当前的请假的天数为空");
				}
				if (StringUtils.isBlank(planLeave.getOptUser())) {
					throw new OaException("假期申请人不能为空");
				}
			}	
			List<EmpLeave> leaves = splitLeave(planLeave);		
			Long recordId = saveLeaveRecord(planLeave, ConfigConstants.REGISTER_LEAVE_KEY);
			flag = sickLeaveApplySuccess(leaves,recordId);
		}
		return flag;
	}

	@Override
	public void initNextYearLeaveByYear(String year,Integer betDays) {
		logger.info("初始化所有员工下一年度的年假信息开始... ...");
		Long startTime = System.currentTimeMillis();

		// 1.删除下一年度的假期数据
		EmpLeave leave = new EmpLeave();
		leave.setType(ConfigConstants.LEAVE_TYPE_1);
		leave.setYear(Integer.parseInt(year));
		empLeaveMapper.delNextLeaveByCondition(leave);

		// 2.找出所有在职员工
		List<EmployeeApp> empList = employeeAppMapper.getInitEmpList(configService.getNeedEmpTypeIdList());
		logger.info("有{}位员工需要初始化下一年度的年假信息", empList.size());

		// 3.遍历所有员工，生成每个员工的年假数据
		for (EmployeeApp emp : empList) {
			saveDefaultNextYearLeave(emp,year,betDays);
		}
		Long endTime = System.currentTimeMillis();
		logger.info("初始化所有员工下一年度的年假信息结束,耗时={}秒", (endTime - startTime) / 1000);
	}


}
