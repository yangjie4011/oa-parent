package com.ule.oa.web.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.flowable.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ule.oa.base.po.AttnStatistics;
import com.ule.oa.base.po.AttnWorkHours;
import com.ule.oa.base.po.CompanyConfig;
import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.EmpApplicationAbnormalAttendance;
import com.ule.oa.base.po.EmpApplicationOvertime;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.po.HiActinst;
import com.ule.oa.base.po.RunTask;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.AnnualVacationService;
import com.ule.oa.base.service.AttnStatisticsService;
import com.ule.oa.base.service.AttnWorkHoursService;
import com.ule.oa.base.service.CompanyConfigService;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmpApplicationAbnormalAttendanceService;
import com.ule.oa.base.service.EmpApplicationOvertimeService;
import com.ule.oa.base.service.EmployeeClassService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.HiActinstService;
import com.ule.oa.base.service.RunTaskService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.service.impl.ActivitiServiceImpl;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.po.CommonPo;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.RedisUtils;
import com.ule.oa.common.utils.UUIDGenerator;
import com.ule.oa.common.utils.http.IPUtils;
import com.ule.oa.web.util.ReturnUrlUtil;
import com.ule.oa.web.util.Token;

/**
 * @ClassName: ????????????????????????
 * @Description: ????????????????????????
 * @author yangjie
 * @date 2017???6???15???
 */
@Controller
@RequestMapping("empApplicationAbnormalAttendance")
public class EmpApplicationAbnormalAttendanceController {
	
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private UserService userService;
	@Autowired
	private EmpApplicationAbnormalAttendanceService empApplicationAbnormalAttendanceService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private HiActinstService hiActinstService;
	@Autowired
	private AttnStatisticsService attnStatisticsService;
	@Autowired
	private AnnualVacationService annualVacationService;
	@Autowired
	private RunTaskService runTaskService;
	@Autowired
	private EmployeeClassService employeeClassService;
	@Autowired
	private EmpApplicationOvertimeService empApplicationOvertimeService;
	@Autowired
	private CompanyConfigService companyConfigService;
	@Resource
	private ConfigService configService;
	@Resource
	private DepartService departService;
	@Autowired
	private ActivitiServiceImpl activitiServiceImpl;
	@Autowired
	private AttnWorkHoursService attnWorkHoursService;
	
	//??????????????????????????????
	@RequestMapping("/index.htm")
	@Token(generate=true)//??????token
	public String index(HttpServletRequest request, String urlType){
		User user = userService.getCurrentUser();
		try {
			request.setAttribute("returnUrl", ReturnUrlUtil.getReturnUrl(urlType));
			request.setAttribute("applyType",0);
			request.setAttribute("employId",user.getEmployee().getId());
		} catch (Exception e) {
			logger.error(user.getEmployee().getCnName()+"??????-?????????"+e.toString());
			return "base/empApplicationAbnormalAttendance/index";
		}
		return "base/empApplicationAbnormalAttendance/index";
	}
	
	@RequestMapping("/myAttnToIndex.htm")//?????????????????????????????????
	@Token(generate=true)//??????token
	public String myAttnToIndex(HttpServletRequest request,String urlType,String attnDate,String startWorkTime,String endWorkTime,
			String startTime,String endTime,String employId,String type){
		User user = userService.getCurrentUser();
		try {
			request.setAttribute("attnDate",attnDate);
			request.setAttribute("startWorkTime",startWorkTime);
			request.setAttribute("endWorkTime",endWorkTime);
			request.setAttribute("employId",employId);
			request.setAttribute("startTime",startTime);
			request.setAttribute("endTime",endTime);
			if(!"1".equals(type)){
				type = "0";
			}else{
				request.setAttribute("agentId",user.getEmployee().getId());
				request.setAttribute("agentName",user.getEmployee().getCnName());
				Employee agent = employeeService.getById((employId!=null&&!"".equals(employId))?Long.valueOf(employId):null);
				if(agent!=null){
					request.setAttribute("employeeName",agent.getCnName());
				}
			}
			request.setAttribute("applyType",type);
			request.setAttribute("returnUrl", ReturnUrlUtil.getReturnUrl(urlType));
		} catch (Exception e) {
			logger.error(user.getEmployee().getCnName()+"??????-???????????????????????????"+e.toString());
			return "base/empApplicationAbnormalAttendance/index";
		}
		return "base/empApplicationAbnormalAttendance/index";
	}
	
	//??????????????????
	@ResponseBody
	@RequestMapping("/getSignTime.htm")
	public Map<String, Object> getSignTime(HttpServletRequest request,Long employId,Integer applyType){
		String abnormalDate = request.getParameter("abnormalDate");
		int countDays = DateUtils.getIntervalDays(DateUtils.parse(abnormalDate, DateUtils.FORMAT_SHORT),DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_SHORT),DateUtils.FORMAT_SHORT));
		Map<String,Object> map = new HashMap<String, Object>();
		if(countDays < 0) {
			map.put("success", false);
			map.put("msg", "????????????????????????????????????");
			return map;
		}
		//???????????????????????????????????????
		AttnStatistics attnStatistics = attnStatisticsService.getAttnStatistics(employId, DateUtils.parse(abnormalDate, DateUtils.FORMAT_SHORT));
		if(attnStatistics!=null&&attnStatistics.getAttnStatus()!=null&&attnStatistics.getAttnStatus().intValue()==0){
			map.put("success", false);
			map.put("msg", "?????????????????????");
			return map;
		}
		//????????????????????????????????????
		AttnWorkHours absoluteUpdate = new AttnWorkHours();
		absoluteUpdate.setEmployeeId(employId);
		absoluteUpdate.setWorkDate(DateUtils.parse(abnormalDate, DateUtils.FORMAT_SHORT));
		List<AttnWorkHours> absoluteUpdateList = attnWorkHoursService.getAbsoluteAttnWorkHoursList(absoluteUpdate);
		if(absoluteUpdateList!=null&&absoluteUpdateList.size()>0){
			map.put("success", false);
			map.put("msg", "?????????????????????????????????????????????HR?????????");
			return map;
		}
		//??????5?????????????????????????????????????????????????????????????????????5?????????????????????????????????????????????????????????
		Config configCondition = new Config();
		configCondition.setCode("timeLimit5");
		List<Config> limit = configService.getListByCondition(configCondition);
		int num = Integer.valueOf(limit.get(0).getDisplayCode());
		
		String nowDate10 = DateUtils.format(annualVacationService.get5WorkingDayNextmonth(num), DateUtils.FORMAT_SHORT);
		int nowCountDays = DateUtils.getIntervalDays(DateUtils.parse(nowDate10, DateUtils.FORMAT_SHORT),DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_SHORT),DateUtils.FORMAT_SHORT));
		//??????10?????????????????????????????????
		if(nowCountDays > 0) {
			String nowDate01 = DateUtils.getNow("yyyy-MM") + "-01";
			int nowCountDays01 = DateUtils.getIntervalDays(DateUtils.parse(nowDate01, DateUtils.FORMAT_SHORT),DateUtils.parse(abnormalDate,DateUtils.FORMAT_SHORT));
			if(nowCountDays01 < 0) {
				map.put("success", false);
				map.put("msg", "??????????????????3????????????????????????????????????????????????");
				return map;
			}
		} else {
			//?????????????????????????????????
			String nowDate01 = DateUtils.getNow("yyyy-MM") + "-01";
			Date addDate = DateUtils.addMonth(DateUtils.parse(nowDate01, DateUtils.FORMAT_SHORT), -1);
			int nowCountDays01 = DateUtils.getIntervalDays(addDate,DateUtils.parse(abnormalDate,DateUtils.FORMAT_SHORT));
			if(nowCountDays01 < 0) {
				map.put("success", false);
				map.put("msg", "??????????????????????????????????????????");
				return map;
			}
		}
		Employee user =  employeeService.getById(employId);
		if(user!=null){
			EmployeeClass employeeClass = new EmployeeClass(); 
			employeeClass.setEmployId(user.getId());
			employeeClass.setClassDate(DateUtils.parse(abnormalDate,DateUtils.FORMAT_SHORT));
			employeeClass = employeeClassService.getEmployeeClassSetting(employeeClass);
			if(employeeClass == null) {
				EmpApplicationOvertime userOvertime = new EmpApplicationOvertime();
				userOvertime.setEmployeeId(user.getId());
				userOvertime.setApplyDate(DateUtils.parse(abnormalDate,DateUtils.FORMAT_SHORT));
				userOvertime.setApprovalStatus(ConfigConstants.PASS_STATUS);
				int count = empApplicationOvertimeService.getEaoByEmpAndDateCount(userOvertime);
				if(count == 0) {
					map.put("success", false);
					map.put("msg", "???????????????????????????");
					return map;
				}
			}
			
			
			EmpApplicationAbnormalAttendance attendance = new EmpApplicationAbnormalAttendance();
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(DateUtils.parse(abnormalDate,DateUtils.FORMAT_SHORT));
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			Date monthStart = calendar.getTime();  
			attendance.setMonthStart(monthStart);
			calendar.add(Calendar.MONTH, 1);
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			Date monthEnd = calendar.getTime();
			attendance.setMonthEnd(monthEnd);
			attendance.setEmployeeId(user.getId());
			
			List<EmpApplicationAbnormalAttendance> list = empApplicationAbnormalAttendanceService.getListByMonth(attendance);
			boolean isOk = true;
			int countCheck = 0;
			if(list != null && list.size() == 2) {
				isOk = false;
				if(applyType.intValue()==1){
					isOk = true;
				}
			}  else if(list.size() == 1) {
				EmpApplicationAbnormalAttendance empApplicationAbnormalAttendance = list.get(0);
			
				AttnWorkHours conCheck = new AttnWorkHours();
				conCheck.setWorkDate(empApplicationAbnormalAttendance.getAbnormalDate());
				conCheck.setEmployId(user.getId());
				conCheck.setDataType(9);
				List<AttnWorkHours> attnWorkHoursList = attnWorkHoursService.getAttnWorkHoursList(conCheck);
				if(attnWorkHoursList==null||attnWorkHoursList.size()<=0){
					map.put("success", false);
					map.put("msg", "??????????????????????????????????????????????????????");
					return map;
				}
				conCheck = attnWorkHoursList.get(0);//?????????????????????????????????????????????
				//???????????????????????????-?????????????????????????????????????????????????????????????????????
				boolean flag = false;
				for(AttnWorkHours a:attnWorkHoursList){
					if(a.getStartTime()==null&&a.getEndTime()==null&&a.getDataType().intValue()==0){
						flag = true;
						continue;
					}
					if(flag){
						if(a.getDataType().intValue()==2){
							conCheck = a;
							break;
						}
					}
				}
				
				countCheck = Integer.valueOf(checkAttnStatistics(user,empApplicationAbnormalAttendance.getAbnormalDate(),conCheck,empApplicationAbnormalAttendance,0).get("countCheck"));
				if(countCheck >= 2) {
					isOk = false;
					if(applyType.intValue()==1){
						isOk = true;
					}
				}
			} else if(list.size() == 0) {
				isOk = true;
			} else {
				isOk = false;
				if(applyType.intValue()==1){
					isOk = true;
				}
			}
			if(isOk) {
				EmpApplicationAbnormalAttendance empApplicationAbnormalAttendance = new EmpApplicationAbnormalAttendance();
				AttnWorkHours conCheck = new AttnWorkHours();
				conCheck.setWorkDate(DateUtils.parse(abnormalDate,DateUtils.FORMAT_SHORT));
				conCheck.setEmployId(user.getId());
				conCheck.setDataType(9);
				
				List<AttnWorkHours> attnWorkHoursList = attnWorkHoursService.getAttnWorkHoursList(conCheck);
				if(attnWorkHoursList==null||attnWorkHoursList.size()<=0){
					map.put("success", false);
					map.put("msg", "??????????????????????????????????????????????????????");
					return map;
				}
				conCheck = attnWorkHoursList.get(0);//?????????????????????????????????????????????
				//???????????????????????????-?????????????????????????????????????????????????????????????????????
				boolean flag = false;
				for(AttnWorkHours a:attnWorkHoursList){
					if(a.getStartTime()==null&&a.getEndTime()==null&&a.getDataType().intValue()==0){
						flag = true;
						continue;
					}
					if(flag){
						if(a.getDataType().intValue()==2){
							conCheck = a;
							break;
						}
					}
				}
				
				Map<String, String> checkAttnStatisticsMap = checkAttnStatistics(user,DateUtils.parse(abnormalDate,DateUtils.FORMAT_SHORT),conCheck,empApplicationAbnormalAttendance,1);
				countCheck = countCheck + Integer.valueOf(checkAttnStatisticsMap.get("countCheck"));
				if(countCheck > 2) {
					if(applyType.intValue()==0){
						map.put("success", false);
						map.put("msg", "??????2???????????????????????????????????????????????????1?????????");
					}else{
						getAbnormalSignTime(map,checkAttnStatisticsMap,countCheck,conCheck);
					}
					return map;
				} else{
					getAbnormalSignTime(map,checkAttnStatisticsMap,countCheck,conCheck);
				}
			} else {
				map.put("success", false);
				map.put("msg", "??????2???????????????????????????????????????????????????1?????????");
				return map;
			}
		}
		return map;
	}
	
	public void getAbnormalSignTime(Map<String,Object> map,Map<String, String> checkAttnStatisticsMap,int countCheck,AttnWorkHours conCheck){
		if(checkAttnStatisticsMap.get("firstPunch").equals("??????") && checkAttnStatisticsMap.get("lasePunch").equals("??????")) {
			if(conCheck.getAttnStatus().intValue()==0){
				map.put("success", false);
				map.put("msg", "????????????????????????????????????");
			}else{
				map.put("firstPunch", "??????");
				map.put("lasePunch", "??????");
				map.put("startPunchTime", DateUtils.format(conCheck.getStartTime(), DateUtils.FORMAT_LONG));
				map.put("endPunchTime",DateUtils.format(conCheck.getEndTime(), DateUtils.FORMAT_LONG));
				map.put("success", true);
				map.put("attnStatus", 1);
				map.put("countCheck", countCheck+1);
			}
		} else {
			if(conCheck!=null){
				if(checkAttnStatisticsMap.get("lasePunch").equals("??????") && conCheck.getEndTime()== null) {
					map.put("startPunchTime", conCheck.getEndTime()==null?"":DateUtils.format(conCheck.getEndTime(), DateUtils.FORMAT_LONG));
					map.put("endPunchTime", conCheck.getStartTime()==null?"":DateUtils.format(conCheck.getStartTime(), DateUtils.FORMAT_LONG));
				} else {
					map.put("startPunchTime", conCheck.getStartTime()==null?"":DateUtils.format(conCheck.getStartTime(), DateUtils.FORMAT_LONG));
					map.put("endPunchTime", conCheck.getEndTime()==null?"":DateUtils.format(conCheck.getEndTime(), DateUtils.FORMAT_LONG));
				}
				map.put("firstPunch", checkAttnStatisticsMap.get("firstPunch"));
				map.put("lasePunch", checkAttnStatisticsMap.get("lasePunch"));
				map.put("success", true);
				map.put("countCheck", countCheck);
			}else{
				map.put("firstPunch", "");
				map.put("lasePunch", "");
				map.put("startPunchTime", "");
				map.put("endPunchTime","");
				map.put("success", true);
				map.put("countCheck", countCheck);
			}
		}
	}

	public Map<String, String> checkAttnStatistics(Employee user,Date attnDate,AttnWorkHours conCheck,EmpApplicationAbnormalAttendance attendance,int type) {
		Map<String, String> map = new HashMap<String, String>();
		int countCheck = 0;
		if(type==0){
			if(attendance.getStartPunchTime()==null){
				countCheck++;
			}else{
				if(DateUtils.compareDate(attendance.getStartTime(),attendance.getStartPunchTime())!=0){
					countCheck++;
				}
			}
            if(attendance.getEndPunchTime()==null){
            	countCheck++;
			}else{
	            if(DateUtils.compareDate(attendance.getEndTime(),attendance.getEndPunchTime())!=0){
	            	countCheck++;
				}
			}
		}else{
			if(conCheck != null) {
				EmployeeClass employeeClass1 = new EmployeeClass(); 
				employeeClass1.setEmployId(user.getId());
				employeeClass1.setClassDate(attnDate);
				employeeClass1 = employeeClassService.getEmployeeClassSetting(employeeClass1);
				Date ecStartTime = new Date();
				Date ecEndTime = new Date();
				if(employeeClass1 != null) {
					String fmtS = DateUtils.format(attnDate, "yyyy-MM-dd") + " " +  DateUtils.format(employeeClass1.getStartTime(), "HH:mm:ss");
					String fmtE = DateUtils.format(attnDate, "yyyy-MM-dd") + " " +  DateUtils.format(employeeClass1.getEndTime(), "HH:mm:ss");
					Long workTypeId = null;
					CompanyConfig companyConfigConditon = new CompanyConfig();//?????????????????????
					companyConfigConditon.setCode("typeOfWork");//????????????
					List<CompanyConfig> workTypeList = companyConfigService.getListByCondition(companyConfigConditon);
					for(CompanyConfig companyConfig:workTypeList){
						if("standard".equals(companyConfig.getDisplayCode())){
							workTypeId = companyConfig.getId();
							break;
						}
					}
					if("09:00:00".equals(DateUtils.format(employeeClass1.getStartTime(), DateUtils.FORMAT_HH_MM_SS))&&
							"18:00:00".equals(DateUtils.format(employeeClass1.getEndTime(), DateUtils.FORMAT_HH_MM_SS))){
						ecStartTime = DateUtils.addMinute(DateUtils.parse(fmtS), 4);
					}else{
						ecStartTime = DateUtils.parse(fmtS);
					}
					ecEndTime = DateUtils.parse(fmtE);
					//??????????????????????????????
					int startWorkTime = DateUtils.compareDate(ecStartTime, ecEndTime);
					if(startWorkTime == 1) {
						ecEndTime = DateUtils.addDay(ecEndTime, 1);
					}
				}
				if(conCheck.getStartTime()==null) {
					map.put("firstPunch", "??????");
					countCheck++;
				} else {
					//??????????????????????????????
					if(ecStartTime.getTime()>=conCheck.getStartTime().getTime()){
						map.put("firstPunch", "??????");
					}
				}
				if(conCheck.getEndTime()==null) {
					map.put("lasePunch", "??????");
					if(map.containsKey("firstPunch")&&"??????".equals(map.get("firstPunch"))){
						countCheck++;
					}
				} else {
					int endWorkTime = DateUtils.compareDate(conCheck.getEndTime(), ecEndTime);
					if(ecEndTime.getTime()<=conCheck.getEndTime().getTime()){
						map.put("lasePunch", "??????");
					}
				}
			} else {
				countCheck = 1;
				map.put("firstPunch", "??????");
				map.put("lasePunch", "??????");
			}
			if(!map.containsKey("lasePunch")){
				map.put("lasePunch", "??????");
			}
           if(!map.containsKey("firstPunch")){
        	   map.put("firstPunch", "??????");
			}
		}
		map.put("countCheck", countCheck + "");
		return map;
	}
	
	//??????
	@ResponseBody
	@RequestMapping("/save.htm")
	@Token(remove = true)//?????????????????????????????????token
	public Map<String, Object> save(HttpServletRequest request,EmpApplicationAbnormalAttendance attendance){
		User user = userService.getCurrentUser();
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			logger.info(user.getUserName()+"??????????????????save:"+"AbnormalDate="+attendance.getAbnormalDate()+
					";StartTime="+attendance.getStartTime()+";EndTime="+attendance.getEndTime()+";Reason="+attendance.getReason()+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));
				if(attendance.getAbnormalDate()==null){
					throw new OaException("???????????????????????????");
				}
				if(StringUtils.isBlank(attendance.getReason())){
					throw new OaException("???????????????????????????");
				}
				if(attendance.getStartTime()==null&&attendance.getEndTime()==null){
					throw new OaException("??????????????????????????????");
				}
				
				//????????????????????????????????????????????????????????????
				Long workTypeId = null;
				CompanyConfig companyConfigConditon = new CompanyConfig();//?????????????????????
				companyConfigConditon.setCode("typeOfWork");//????????????
				List<CompanyConfig> workTypeList = companyConfigService.getListByCondition(companyConfigConditon);
				for(CompanyConfig companyConfig:workTypeList){
					if("comprehensive".equals(companyConfig.getDisplayCode())){
						workTypeId = companyConfig.getId();
						break;
					}
				}
				if(user!=null&&user.getEmployee()!=null&&user.getEmployee().getWorkType()!=null&&
						user.getEmployee().getWorkType().equals(workTypeId)) {
					//??????6???30
					Date nextDayEndTime = DateUtils.parse(DateUtils.format(DateUtils.addDay(attendance.getAbnormalDate(), 1), DateUtils.FORMAT_SHORT)+" 06:30:00",DateUtils.FORMAT_LONG);
				    if(attendance.getEndTime()!=null&&attendance.getEndTime().getTime()>nextDayEndTime.getTime()) {
				    	throw new OaException("?????????????????????????????????????????????");
				    }
				}
				
				//???????????????????????????????????????
				AttnStatistics attnStatistics = attnStatisticsService.getAttnStatistics(attendance.getEmployeeId(), attendance.getAbnormalDate());
				if(attnStatistics!=null&&attnStatistics.getAttnStatus()!=null&&attnStatistics.getAttnStatus().intValue()==0){
					throw new OaException("?????????????????????");
				}
				//????????????????????????????????????
				AttnWorkHours absoluteUpdate = new AttnWorkHours();
				absoluteUpdate.setEmployeeId(attendance.getEmployeeId());
				absoluteUpdate.setWorkDate(attendance.getAbnormalDate());
				List<AttnWorkHours> absoluteUpdateList = attnWorkHoursService.getAbsoluteAttnWorkHoursList(absoluteUpdate);
				if(absoluteUpdateList!=null&&absoluteUpdateList.size()>0){
					throw new OaException("?????????????????????????????????????????????HR?????????");
				}
				
				AttnWorkHours con = new AttnWorkHours();
				con.setWorkDate(attendance.getAbnormalDate());
				con.setEmployId(attendance.getEmployeeId());
				con.setDataType(9);
				List<AttnWorkHours> attnWorkHoursList = attnWorkHoursService.getAttnWorkHoursList(con);
				if(attnWorkHoursList==null||attnWorkHoursList.size()<=0){
					throw new OaException("??????????????????????????????????????????????????????");
				}
				
				AttnWorkHours attn = attnWorkHoursList.get(0);//?????????????????????????????????????????????
				//???????????????????????????-?????????????????????????????????????????????????????????????????????
				boolean flag = false;
				for(AttnWorkHours a:attnWorkHoursList){
					if(a.getStartTime()==null&&a.getEndTime()==null&&a.getDataType().intValue()==0){
						flag = true;
						continue;
					}
					if(flag){
						if(a.getDataType().intValue()==2){
							attn = a;
							break;
						}
					}
				}
				
				if(attn!=null){
					if(attendance.getStartTime()==null){
						attendance.setStartTime(attn.getStartTime());
					}
					if(attendance.getEndTime()==null){
						attendance.setEndTime(attn.getEndTime());
					}
				}
				if(attendance.getStartTime()!=null&&attendance.getEndTime()!=null){
					if(attendance.getStartTime().getTime()>=attendance.getEndTime().getTime()){
						attendance.setEndTime(DateUtils.addDay(attendance.getEndTime(), 1));
					}
				}
				EmpApplicationAbnormalAttendance atten = new EmpApplicationAbnormalAttendance();
				atten.setEmployeeId(attendance.getEmployeeId());
				atten.setAbnormalDate(attendance.getAbnormalDate());
				int count = empApplicationAbnormalAttendanceService.getEaoByEmpAndDateCount(atten);
				if(count>0){
					throw new OaException("?????????"+DateUtils.format(attendance.getAbnormalDate(), DateUtils.FORMAT_SHORT)+"??????????????????");
				}
				
				int countCheck = 0;
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(attendance.getAbnormalDate());
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				Date monthStart = calendar.getTime();  
				attendance.setMonthStart(monthStart);
				calendar.add(Calendar.MONTH, 1);
				calendar.add(Calendar.DAY_OF_MONTH, -1);
				Date monthEnd = calendar.getTime();
				attendance.setMonthEnd(monthEnd);
				
				List<EmpApplicationAbnormalAttendance> list = empApplicationAbnormalAttendanceService.getListByMonth(attendance);
				for(EmpApplicationAbnormalAttendance attn1:list){
					if(attn1.getStartPunchTime()==null){
						countCheck++;
					}
					if(attn1.getStartPunchTime()!=null&&DateUtils.compareDate(attn1.getStartTime(), attn1.getStartPunchTime())==2){
						countCheck++;
					}
					if(attn1.getEndPunchTime()==null){
						countCheck = countCheck + 1;
					}
					if(attn1.getEndPunchTime()!=null&&DateUtils.compareDate(attn1.getEndTime(),attn1.getEndPunchTime())==1){
						countCheck = countCheck + 1;
					}
				}
				
				if(attn!=null){
					attendance.setStartPunchTime(attn.getStartTime());
					attendance.setEndPunchTime(attn.getEndTime());
					if(attendance.getStartPunchTime()==null){
						countCheck = countCheck + 1;
					}
					if(attendance.getStartPunchTime()!=null&&DateUtils.compareDate(attendance.getStartTime(), attendance.getStartPunchTime())==2){
						countCheck = countCheck + 1;
					}
					if(attendance.getEndPunchTime()==null){
						countCheck = countCheck + 1;
					}
					if(attendance.getEndPunchTime()!=null&&DateUtils.compareDate(attendance.getEndTime(),attendance.getEndPunchTime())==1){
						countCheck = countCheck + 1;
					}
				}
				if(countCheck>2){
					if(attendance.getApplyType().intValue()==0){
						throw new OaException("??????2???????????????????????????????????????????????????1????????????");
					}
				}
				List<Employee> employee = employeeService.getByEmpId(attendance.getEmployeeId());
				if(employee!=null&&employee.size()>0){
					attendance.setCnName(employee.get(0).getCnName());
					attendance.setCode(employee.get(0).getCode());
					attendance.setDepartId(employee.get(0).getEmpDepart().getDepart().getId());
					attendance.setDepartName(employee.get(0).getEmpDepart().getDepart().getName());
					attendance.setPositionId(employee.get(0).getEmpPosition().getPosition().getId());
					attendance.setPositionName(employee.get(0).getEmpPosition().getPosition().getPositionName());
				}
				attendance.setAgentId(user.getEmployee().getId());
				attendance.setAgentName(user.getEmployee().getCnName());
				attendance.setSubmitDate(new Date());
				attendance.setDelFlag(CommonPo.STATUS_NORMAL);
				attendance.setCreateTime(new Date());
				attendance.setCreateUser(user.getEmployee().getCnName());
				attendance.setApprovalStatus(ConfigConstants.DOING_STATUS);
				attendance.setVersion(null);
				map = empApplicationAbnormalAttendanceService.save(attendance);
		}catch(Exception e){
			logger.error(user.getEmployee().getCnName()+"??????-?????????"+e.toString());
			String uuId = UUIDGenerator.generate();
            try {
				RedisUtils.setString(String.valueOf(user.getEmployee().getId()),uuId,1800000);
				map.put("token", uuId);
			} catch (Exception e1) {
				logger.error(user.getEmployee().getCnName()+"?????????????????????"+e1.toString());
			}
			map.put("repeatFlag", false);
			map.put("message",e.getMessage());
			map.put("success",false);
			return map;
		}
		return map;
	}
	
	//????????????????????????????????????
	@RequestMapping("/approval.htm")
	@Token(generate=true)//??????token
	public String approval(HttpServletRequest request,Long attendanceId,String flag,String urlType){
		User user = userService.getCurrentUser();
		try {
			
			request.setAttribute("imgUrl",(user.getEmployee().getPicture()!=null&&!"".equals(user.getEmployee().getPicture()))?user.getEmployee().getPicture():ConfigConstants.HEAD_PICTURE_URL);
			EmpApplicationAbnormalAttendance attendance = empApplicationAbnormalAttendanceService.getById(attendanceId);
			request.setAttribute("attendance", attendance);
			request.setAttribute("isSelf", false);
			request.setAttribute("canApprove", false);
			if(attendance.getApprovalStatus().intValue()==100) {
				if(attendance.getEmployeeId().longValue()==user.getEmployeeId().longValue()){
					request.setAttribute("isSelf", true);
				}
				//?????????,taskService????????????
				Task task = activitiServiceImpl.queryTaskByProcessInstanceId(attendance.getProcessInstanceId());
				if(StringUtils.equalsIgnoreCase("can", flag)) {
					request.setAttribute("canApprove", true);
				}
				if(task != null){
					request.setAttribute("actName", task.getName());
				}
			}
			
			request.setAttribute("returnUrl", ReturnUrlUtil.getReturnUrl(urlType));
		} catch (Exception e) {
			logger.error(user.getEmployee().getCnName()+"??????-???????????????"+e.toString());
			return "base/empApplicationAbnormalAttendance/approval";
		}
		return "base/empApplicationAbnormalAttendance/approval";
	}
	
	//??????????????????????????????
	@RequestMapping("/process.htm")
	public String process(HttpServletRequest request, Long attendanceId,Long ruProcdefId){
		EmpApplicationAbnormalAttendance attendance = empApplicationAbnormalAttendanceService.getById(attendanceId);
		request.setAttribute("attendance", attendance);
		RunTask param = new RunTask();
		param.setReProcdefCode(RunTask.RUN_CODE_40);
		Depart depart = departService.getById(attendance.getDepartId());
		if("116".equals(depart.getCode())){
			param.setReProcdefCode(RunTask.RUN_CODE_41);
		}
		if(attendance.getApplyType().intValue()==1){
			param.setReProcdefCode(RunTask.RUN_CODE_120);
		}
		param.setEntityId(String.valueOf(attendanceId));
		RunTask runTask = runTaskService.getRunTask(param);
		request.setAttribute("runTask", runTask);
		List<HiActinst> hiActinstList = hiActinstService.getListByRunId(ruProcdefId);
		//??????????????????
		List<Long> empIdList = new ArrayList<Long>();
		List<Employee> list = new ArrayList<Employee>();
		for(HiActinst hiActinst:hiActinstList){
			if(hiActinst.getAssigneeId()!=null){
				empIdList.add(hiActinst.getAssigneeId());
			}
		}
		if(empIdList!=null&&empIdList.size()>0){
			list = employeeService.getByEmpIdList(empIdList);
		}
		for(HiActinst hiActinst:hiActinstList){
			for(Employee employee:list){
				if(employee.getId().equals(hiActinst.getAssigneeId())){
					hiActinst.setPositionName(employee.getPositionName());
				}
			}
		}
		request.setAttribute("hiActinstList", hiActinstList);
		return "base/empApplicationAbnormalAttendance/process";
	}
	
}