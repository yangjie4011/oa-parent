package com.ule.oa.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.flowable.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ule.oa.base.po.AnnualVacation;
import com.ule.oa.base.po.AttnSignRecord;
import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.EmpApplicationOvertime;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.po.EmployeeDuty;
import com.ule.oa.base.po.HiActinst;
import com.ule.oa.base.po.RunTask;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.AnnualVacationService;
import com.ule.oa.base.service.AttnSignRecordService;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.EmpApplicationOvertimeService;
import com.ule.oa.base.service.EmployeeClassService;
import com.ule.oa.base.service.EmployeeDutyService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.HiActinstService;
import com.ule.oa.base.service.RunTaskService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.service.UserTaskService;
import com.ule.oa.base.service.impl.ActivitiServiceImpl;
import com.ule.oa.base.vo.TaskVO;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.GetHours;
import com.ule.oa.common.utils.RedisUtils;
import com.ule.oa.common.utils.UUIDGenerator;
import com.ule.oa.web.util.ReturnUrlUtil;
import com.ule.oa.web.util.Token;

/**
 * @ClassName: 延时工作申请
 * @Description: 延时工作申请
 * @author yangjie
 * @date 2017年6月12日
 */
@Controller
@RequestMapping("empApplicationOvertime")
public class EmpApplicationOvertimeController {
	
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private UserService userService;
	@Autowired
	private EmpApplicationOvertimeService empApplicationOvertimeService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private HiActinstService hiActinstService;
	@Autowired
	private AnnualVacationService annualVacationService;
	@Autowired
	private RunTaskService runTaskService;
	@Autowired
	private EmployeeClassService employeeClassService;
	@Autowired
	private AttnSignRecordService attnSignRecordService;
	@Autowired
	private ConfigService configService;
	@Autowired
	private EmployeeDutyService employeeDutyService;
	@Autowired
	private UserTaskService userTaskService;
	@Autowired
	private ActivitiServiceImpl activitiServiceImpl;
	
	
	//延时工作申请首页
	@RequestMapping("/index.htm")
	@Token(generate=true)//生成token
	public String index(HttpServletRequest request, String urlType){
		User user = userService.getCurrentUser();
		try {
			request.setAttribute("wetherSchedule", false);
			Config config = configService.getConfigInfoById(user.getEmployee().getWhetherScheduling());
			if(config!=null&&"yes".equals(config.getDisplayCode())){
				request.setAttribute("wetherSchedule", true);
			}
			request.setAttribute("returnUrl", ReturnUrlUtil.getReturnUrl(urlType));
		} catch (Exception e) {
			logger.error(user.getEmployee().getCnName()+"延时-首页："+e.toString());
			return "base/empApplicationOvertime/index";
		}
		return "base/empApplicationOvertime/index";
	}
	
	//获取默认的加班开始结束时间
	@RequestMapping("/getDefaultTime.htm")
	@ResponseBody
	public Map<String, String> getDefaultTime(HttpServletRequest request){
		User user = userService.getCurrentUser();
		Map<String, String> map = new HashMap<String, String>();
		map.put("startTime", "");
		map.put("endTime", "");
		map.put("s_data_nextday", "0");
		map.put("e_data_nextday", "0");
		String date_type = "0";//0-正常工作日，1-正常休息日，2-法定节假日，3-法定调休日
        //查看假期表
        AnnualVacation vacation = new AnnualVacation();
		vacation.setAnnualDate(DateUtils.parse(request.getParameter("classDate"), DateUtils.FORMAT_SHORT));
		List<AnnualVacation> vacationList = annualVacationService.getListByCondition(vacation);
		for(AnnualVacation va:vacationList){
			if(va.getType().intValue()==AnnualVacation.YYPE_LEGAL.intValue()){
				date_type = "2";
				break;
			}
			if(va.getType().intValue()==AnnualVacation.YYPE_VACATION.intValue()){
				date_type = "3";
				break;
			}
		}
		EmployeeClass empClass = null;
		if("0".equals(date_type)){
			EmployeeClass employeeClass = new EmployeeClass();
			employeeClass.setEmployId(user.getEmployeeId());
			employeeClass.setClassDate(DateUtils.parse(request.getParameter("classDate"), DateUtils.FORMAT_SHORT));
			empClass = employeeClassService.getEmployeeClassSetting(employeeClass);
			if(null != empClass){
				date_type = "0";
			}else{
				date_type = "1";
			}
		}
		
		if("0".equals(date_type)&&null != empClass){
			map.put("startTime", DateUtils.format(DateUtils.addMinute(empClass.getEndTime(), 60), "HH:mm"));
			if(!DateUtils.format(DateUtils.addMinute(empClass.getEndTime(), 60), DateUtils.FORMAT_SHORT).equals(DateUtils.format(empClass.getClassDate(), DateUtils.FORMAT_SHORT))){
				map.put("s_data_nextday", "1");
			}
			map.put("endTime", DateUtils.format(DateUtils.addMinute(empClass.getEndTime(), 120), "HH:mm"));
			if(!DateUtils.format(DateUtils.addMinute(empClass.getEndTime(), 120), DateUtils.FORMAT_SHORT).equals(DateUtils.format(empClass.getClassDate(), DateUtils.FORMAT_SHORT))){
				map.put("e_data_nextday", "1");
			}
		}else if("1".equals(date_type)){
			map.put("startTime", "09:00");
			map.put("endTime", "10:00");
		}else if("2".equals(date_type)){
			//查询值班记录
			EmployeeDuty dutyP = new EmployeeDuty();
			dutyP.setEmployId(user.getEmployeeId());
			dutyP.setDutyDate(DateUtils.parse(request.getParameter("classDate"), DateUtils.FORMAT_SHORT));
			List<EmployeeDuty> dutyList = employeeDutyService.selectByCondition(dutyP);
			if(dutyList!=null&&dutyList.size()>0){
				//判断是否次日
				if(!DateUtils.format(dutyList.get(0).getStartTime(), DateUtils.FORMAT_SIMPLE).equals(DateUtils.format(dutyList.get(0).getEndTime(), DateUtils.FORMAT_SIMPLE))){
					map.put("e_data_nextday", "1");
				}
				map.put("startTime", DateUtils.format(dutyList.get(0).getStartTime(), "HH:mm"));
				map.put("endTime", DateUtils.format(dutyList.get(0).getEndTime(), "HH:mm"));
			}
		}else if("3".equals(date_type)){
			//查询值班记录
			EmployeeDuty dutyP = new EmployeeDuty();
			dutyP.setEmployId(user.getEmployeeId());
			dutyP.setDutyDate(DateUtils.parse(request.getParameter("classDate"), DateUtils.FORMAT_SHORT));
			List<EmployeeDuty> dutyList = employeeDutyService.selectByCondition(dutyP);
			if(dutyList!=null&&dutyList.size()>0){
				//判断是否次日
				if(!DateUtils.format(dutyList.get(0).getStartTime(), DateUtils.FORMAT_SIMPLE).equals(DateUtils.format(dutyList.get(0).getEndTime(), DateUtils.FORMAT_SIMPLE))){
					map.put("e_data_nextday", "1");
				}
				map.put("startTime", DateUtils.format(DateUtils.addMinute(dutyList.get(0).getEndTime(), 60), "HH:mm"));
				map.put("endTime", DateUtils.format(DateUtils.addMinute(dutyList.get(0).getEndTime(), 120), "HH:mm"));
			}
		}
		return map;
	}
	
	@RequestMapping("/getEmpClassTime.htm")
	@ResponseBody
	public Map<String, String> getEmpClassTime(HttpServletRequest request){
		User user = userService.getCurrentUser();
		String date_type = "0";//0-正常工作日，1-正常休息日，2-法定节假日，3-法定调休日
        //查看假期表
        AnnualVacation vacation = new AnnualVacation();
		vacation.setAnnualDate(DateUtils.parse(request.getParameter("classDate"), DateUtils.FORMAT_SHORT));
		List<AnnualVacation> vacationList = annualVacationService.getListByCondition(vacation);
		for(AnnualVacation va:vacationList){
			if(va.getType().intValue()==AnnualVacation.YYPE_LEGAL.intValue()){
				date_type = "2";
				break;
			}
			if(va.getType().intValue()==AnnualVacation.YYPE_VACATION.intValue()){
				date_type = "3";
				break;
			}
		}
		EmployeeClass empClass = null;
		if("0".equals(date_type)){
			EmployeeClass employeeClass = new EmployeeClass();
			employeeClass.setEmployId(user.getEmployeeId());
			employeeClass.setClassDate(DateUtils.parse(request.getParameter("classDate"), DateUtils.FORMAT_SHORT));
			empClass = employeeClassService.getEmployeeClassSetting(employeeClass);
			if(null != empClass){
				date_type = "0";
			}else{
				date_type = "1";
			}
		}
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("startTime", "");
		map.put("endTime", "");
		map.put("date_type", date_type);
		if("0".equals(date_type)&&empClass!=null){
			String startTime = DateUtils.format(empClass.getStartTime(), "HH:mm");
			String endTime = DateUtils.format(empClass.getEndTime(), "HH:mm");
			map.put("startTime", startTime);
			map.put("endTime", endTime);
			map.put("isInterDay",empClass.getIsInterDay().toString());
		}else if("2".equals(date_type)||"3".equals(date_type)){
			//查询值班记录
			EmployeeDuty dutyP = new EmployeeDuty();
			dutyP.setEmployId(user.getEmployeeId());
			dutyP.setDutyDate(DateUtils.parse(request.getParameter("classDate"), DateUtils.FORMAT_SHORT));
			List<EmployeeDuty> dutyList = employeeDutyService.selectByCondition(dutyP);
			if(dutyList!=null&&dutyList.size()>0){
				//判断是否次日
				if(!DateUtils.format(dutyList.get(0).getStartTime(), DateUtils.FORMAT_SIMPLE).equals(DateUtils.format(dutyList.get(0).getEndTime(), DateUtils.FORMAT_SIMPLE))){
					map.put("isInterDay","1");
				}
				String startTime = DateUtils.format(dutyList.get(0).getStartTime(), "HH:mm");
				String endTime = DateUtils.format(dutyList.get(0).getEndTime(), "HH:mm");
				map.put("startTime", startTime);
				map.put("endTime", endTime);
			}
		}
		
		return map;
		
	}
	
	//保存
	@ResponseBody
	@RequestMapping("/save.htm")
	@Token(remove = true)
	public Map<String, Object> save(HttpServletRequest request,EmpApplicationOvertime overtime){
		User user = userService.getCurrentUser();
		Map<String,Object> map = new HashMap<String, Object>();
		try{
		    empApplicationOvertimeService.save(request,overtime);
		    map.put("success",true);
		}catch(Exception e){
			logger.error(user.getEmployee().getCnName()+"延时-提交："+e.toString());
			String uuId = UUIDGenerator.generate();
            try {
				RedisUtils.setString(String.valueOf(user.getEmployee().getId()),uuId,1800000);
				map.put("token", uuId);
			} catch (Exception e1) {
				logger.error("延时工作申请："+e1.toString());
			}
			map.put("repeatFlag", false);
			map.put("message", e.getMessage());
			map.put("success",false);
			return map;
		}
		return map;
	}
	
	//审批页面
	@RequestMapping("/approval.htm")
	@Token(generate=true)//生成token
	public String approveExcept(HttpServletRequest request,String flag,Long overtimeId,String urlType){
		User user = userService.getCurrentUser();
		try {
			EmpApplicationOvertime overtime = empApplicationOvertimeService.getById(overtimeId);
			request.setAttribute("overtime", overtime);
			Task task = activitiServiceImpl.queryTaskByProcessInstanceId(overtime.getProcessInstanceId());
			TaskVO taskVO = new TaskVO();
			if(task!=null){
				taskVO.setActName(task.getName());
			}
			taskVO.setProcessId(overtime.getProcessInstanceId());
			request.setAttribute("taskVO", taskVO);
			request.setAttribute("isSelf", false);
			request.setAttribute("canApprove", false);
			//审批显示
			if(overtime.getApprovalStatus().intValue()==100) {
				if(overtime.getEmployeeId().longValue()==user.getEmployeeId().longValue()){
					request.setAttribute("isSelf", true);
				}
				if(StringUtils.equalsIgnoreCase("can", flag)) {
					request.setAttribute("canApprove", true);
				}
			}
			request.setAttribute("returnUrl", ReturnUrlUtil.getReturnUrl(urlType));
		} catch (Exception e) {
			logger.error(user.getEmployee().getCnName()+"延时-审批首页："+e.toString());
			return "base/empApplicationOvertime/approval";
		}
		return "base/empApplicationOvertime/approval";
	}
	
	//加班申请流程页面
	@RequestMapping("/process.htm")
	public String process(HttpServletRequest request, Long overtimeId,Long ruProcdefId){
		EmpApplicationOvertime overtime = empApplicationOvertimeService.getById(overtimeId);
		request.setAttribute("overtime", overtime);
		RunTask param = new RunTask();
		param.setReProcdefCode(RunTask.RUN_CODE_30);
		param.setEntityId(String.valueOf(overtimeId));
		RunTask runTask = runTaskService.getRunTask(param);
		request.setAttribute("runTask", runTask);
		List<HiActinst> hiActinstList = hiActinstService.getListByRunId(ruProcdefId);
		//组装职位信息
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
		return "base/empApplicationOvertime/process";
	}
	
	//通过
	@ResponseBody
	@RequestMapping("/pass.htm")
	@Token(remove = true)
	public Map<String, Object> pass(HttpServletRequest request,EmpApplicationOvertime overtime){
		User user = userService.getCurrentUser();
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			userTaskService.completeTask(request,overtime.getProcessInstanceId(),overtime.getApprovalReason(), ConfigConstants.PASS);
			map.put("success",true);
		}catch(Exception e){
			logger.error(user.getEmployee().getCnName()+"延时-审批通过："+e.toString());
			map.put("repeatFlag", false);
			map.put("message",e.getMessage());
			map.put("success",false);
			return map;
		}
		return map;
	}
	
	//拒绝
	@ResponseBody
	@RequestMapping("/refuse.htm")
	@Token(remove = true)
	public Map<String, Object> refuse(HttpServletRequest request,EmpApplicationOvertime overtime){
		User user = userService.getCurrentUser();
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			userTaskService.completeTask(request,overtime.getProcessInstanceId(),overtime.getApprovalReason(), ConfigConstants.REFUSE);
			map.put("success",true);
		}catch(Exception e){
			logger.error(user.getEmployee().getCnName()+"延时-审批拒绝："+e.toString());
			map.put("repeatFlag", false);
			map.put("message", e.getMessage());
			map.put("success",false);
			return map;
		}
		return map;
	}
	
	//审批页面
	@RequestMapping("/toAddActualTime.htm")
	@Token(generate=true)//生成token
	public String toAddActualTime(HttpServletRequest request,String flag,Long overtimeId,String urlType){
		User user = userService.getCurrentUser();
		try {
			request.setAttribute("data_nextday", 0);
			request.setAttribute("s_data_nextday", 0);
			request.setAttribute("e_data_nextday", 0);
			EmpApplicationOvertime ovrertime = empApplicationOvertimeService.getById(overtimeId);
			if(ovrertime!=null){
				EmployeeClass employeeClass = new EmployeeClass();
				employeeClass.setEmployId(ovrertime.getEmployeeId());
				employeeClass.setClassDate(ovrertime.getApplyDate());
				EmployeeClass empClass = employeeClassService.getEmployeeClassSetting(employeeClass);
				AnnualVacation vacation = new AnnualVacation();
				vacation.setAnnualDate(ovrertime.getApplyDate());
				List<AnnualVacation> vacationList = annualVacationService.getListByCondition(vacation);
				if(null != empClass){
					request.setAttribute("startTime", DateUtils.format(DateUtils.addMinute(empClass.getEndTime(), 60), "HH:mm"));
					if(!DateUtils.format(DateUtils.addMinute(empClass.getEndTime(), 60), DateUtils.FORMAT_SHORT).equals(DateUtils.format(employeeClass.getClassDate(), DateUtils.FORMAT_SHORT))){
						request.setAttribute("s_data_nextday", 1);
						request.setAttribute("e_data_nextday", 1);
						request.setAttribute("data_nextday", 1);
					}
				}
				if(vacationList!=null&&vacationList.size()>0){
					//查询值班记录
					EmployeeDuty dutyP = new EmployeeDuty();
					dutyP.setEmployId(ovrertime.getEmployeeId());
					dutyP.setDutyDate(ovrertime.getApplyDate());
					List<EmployeeDuty> dutyList = employeeDutyService.selectByCondition(dutyP);
					if(dutyList!=null&&dutyList.size()>0){
						if(vacationList.get(0).getType().intValue()==AnnualVacation.YYPE_LEGAL.intValue()){
							request.setAttribute("startTime", DateUtils.format(dutyList.get(0).getStartTime(), "HH:mm"));
						}else if(vacationList.get(0).getType().intValue()==AnnualVacation.YYPE_VACATION.intValue()){
							request.setAttribute("startTime", DateUtils.format(DateUtils.addMinute(dutyList.get(0).getEndTime(), 60), "HH:mm"));
						}
					}
					
				}
				Task task = activitiServiceImpl.queryTaskByProcessInstanceId(ovrertime.getProcessInstanceId());
				TaskVO taskVO = new TaskVO();
				taskVO.setActName(task.getName());
				taskVO.setProcessId(ovrertime.getProcessInstanceId());
				request.setAttribute("taskVO", taskVO);
				request.setAttribute("overtime", ovrertime);
				request.setAttribute("isSelf", false);
				request.setAttribute("canApprove", false);
				//审批显示
				if(ovrertime.getApprovalStatus().intValue()==100) {
					if(ovrertime.getEmployeeId().longValue()==user.getEmployeeId().longValue()){
						request.setAttribute("isSelf", true);
					}
					if(StringUtils.equalsIgnoreCase("can", flag)) {
						request.setAttribute("canApprove", true);
					}
				}
				request.setAttribute("returnUrl", ReturnUrlUtil.getReturnUrl(urlType));
			}
		} catch (Exception e) {
			logger.error(user.getEmployee().getCnName()+"延时-填写实际时间页："+e.toString());
		}
		return "base/empApplicationOvertime/addActualTime";
	}
	
	public Map<String,Object> getAcTualTime(EmpApplicationOvertime ovrertime){
		Map<String,Object> map = new HashMap<String, Object>();
		String actualEndTime = "";
		String actualEndTime1 = "";
		String actuaStartTime = "";
        double duration = 0;
        //标记法定节假日加班时间只能在班次时间内
        String flag = "0";
        String date_type = "0";//0-正常工作日，1-正常休息日，2-法定节假日，3-法定调休日
        //查看假期表
        AnnualVacation vacation = new AnnualVacation();
		vacation.setAnnualDate(ovrertime.getApplyDate());
		List<AnnualVacation> vacationList = annualVacationService.getListByCondition(vacation);
		for(AnnualVacation va:vacationList){
			if(va.getType().intValue()==AnnualVacation.YYPE_LEGAL.intValue()){
				date_type = "2";
				break;
			}
			if(va.getType().intValue()==AnnualVacation.YYPE_VACATION.intValue()){
				date_type = "3";
				break;
			}
		}
		EmployeeClass empClass = null;
		if("0".equals(date_type)){
			EmployeeClass employeeClass = new EmployeeClass();
			employeeClass.setEmployId(ovrertime.getEmployeeId());
			employeeClass.setClassDate(ovrertime.getApplyDate());
		    empClass = employeeClassService.getEmployeeClassSetting(employeeClass);
			if(null != empClass){
				date_type = "0";
			}else{
				date_type = "1";
			}
		}
        
		if("0".equals(date_type)){
			actuaStartTime = String.valueOf(Integer.valueOf(DateUtils.format(empClass.getEndTime(), "HH:mm").substring(0,2))+1)+":00";
			//延时工作考勤计算规则（隔天加班）：5.50分到6点有打卡，就需要找6点到9点10分的最后一次打卡时间，
			//如有卡则取打卡时间，如打卡在9点与9点10分之间则取9点，如果没有打卡，就找6点前最后一次打卡时间，作为加班的工时累计的时间点
			AttnSignRecord record = new AttnSignRecord();
			Date nextDay = DateUtils.addDay(ovrertime.getApplyDate(), 1);
			record.setEmployeeId(ovrertime.getEmployeeId());
			record.setStartTime(DateUtils.parse(DateUtils.format(nextDay, DateUtils.FORMAT_SHORT)+" 05:50:00", DateUtils.FORMAT_LONG));
			record.setEndTime(DateUtils.parse(DateUtils.format(nextDay, DateUtils.FORMAT_SHORT)+" 06:00:00", DateUtils.FORMAT_LONG));
			List<AttnSignRecord> list = attnSignRecordService.getListBefore9(record);
			if(list!=null&&list.size()>0){
				//找6点到9点05分的最后一次打卡时间，
				record.setStartTime(DateUtils.parse(DateUtils.format(nextDay, DateUtils.FORMAT_SHORT)+" 06:00:00", DateUtils.FORMAT_LONG));
				record.setEndTime(DateUtils.parse(DateUtils.format(nextDay, DateUtils.FORMAT_SHORT)+" 09:05:00", DateUtils.FORMAT_LONG));
				List<AttnSignRecord> list1 = attnSignRecordService.getListBefore9(record);
				if(list1!=null&&list1.size()>0){
					actualEndTime = DateUtils.format(list1.get(0).getSignTime(), "HH:mm");
					actualEndTime1 = DateUtils.format(list1.get(0).getSignTime(), DateUtils.FORMAT_LONG);
				}else{
					actualEndTime = DateUtils.format(list.get(0).getSignTime(), "HH:mm");
					actualEndTime1 = DateUtils.format(list.get(0).getSignTime(), DateUtils.FORMAT_LONG);
				}
			}else{
				//5.50分到6点没有打卡，找20:00到05:50的记录
				String endTime = String.valueOf(Integer.valueOf(DateUtils.format(empClass.getEndTime(), "HH:mm").substring(0,2))+2);
				record.setStartTime(DateUtils.parse(DateUtils.format(ovrertime.getApplyDate(), DateUtils.FORMAT_SHORT)+" "+endTime+":00:00", DateUtils.FORMAT_LONG));
				if(Integer.valueOf(DateUtils.format(empClass.getEndTime(), "HH:mm").substring(0,2))+2>=24){
					endTime = String.valueOf(Integer.valueOf(DateUtils.format(empClass.getEndTime(), "HH:mm").substring(0,2))+2-24);
					record.setStartTime(DateUtils.parse(DateUtils.format(nextDay, DateUtils.FORMAT_SHORT)+" "+endTime+":00:00", DateUtils.FORMAT_LONG));
				}
				record.setEndTime(DateUtils.parse(DateUtils.format(nextDay, DateUtils.FORMAT_SHORT)+" 06:00:00", DateUtils.FORMAT_LONG));
				List<AttnSignRecord> list2 = attnSignRecordService.getListBefore9(record);
				if(list2!=null&&list2.size()>0){
					actualEndTime = DateUtils.format(list2.get(0).getSignTime(), "HH:mm");
					actualEndTime1 = DateUtils.format(list2.get(0).getSignTime(), DateUtils.FORMAT_LONG);
				}
			}
		}else if("1".equals(date_type)){
			AttnSignRecord record = new AttnSignRecord();
			Date nextDay = DateUtils.addDay(ovrertime.getApplyDate(), 1);
			record.setEmployeeId(ovrertime.getEmployeeId());
			record.setStartTime(DateUtils.parse(DateUtils.format(ovrertime.getApplyDate(), DateUtils.FORMAT_SHORT)+" 06:00:00", DateUtils.FORMAT_LONG));
			record.setEndTime(DateUtils.parse(DateUtils.format(nextDay, DateUtils.FORMAT_SHORT)+" 06:00:00", DateUtils.FORMAT_LONG));
			List<AttnSignRecord> list3 = attnSignRecordService.getListBefore9(record);
			if(list3!=null&&list3.size()>0){
			    actualEndTime = DateUtils.format(list3.get(0).getSignTime(), "HH:mm");
				actuaStartTime = DateUtils.format(list3.get(list3.size()-1).getSignTime(), "HH:mm");
				actualEndTime1 = DateUtils.format(list3.get(0).getSignTime(), DateUtils.FORMAT_LONG);
			}
		}else if("2".equals(date_type)){
			//查询值班记录
			EmployeeDuty dutyP = new EmployeeDuty();
			dutyP.setEmployId(ovrertime.getEmployeeId());
			dutyP.setDutyDate(ovrertime.getApplyDate());
			List<EmployeeDuty> dutyList = employeeDutyService.selectByCondition(dutyP);
			
			AttnSignRecord record = new AttnSignRecord();
			Date nextDay = DateUtils.addDay(ovrertime.getApplyDate(), 1);
			record.setEmployeeId(ovrertime.getEmployeeId());
			record.setStartTime(DateUtils.parse(DateUtils.format(ovrertime.getApplyDate(), DateUtils.FORMAT_SHORT)+" 06:00:00", DateUtils.FORMAT_LONG));
			record.setEndTime(DateUtils.parse(DateUtils.format(nextDay, DateUtils.FORMAT_SHORT)+" 06:00:00", DateUtils.FORMAT_LONG));
			List<AttnSignRecord> list3 = attnSignRecordService.getListBefore9(record);
			if(list3!=null&&list3.size()>0){
			    actualEndTime = DateUtils.format(list3.get(0).getSignTime(), "HH:mm");
				actuaStartTime = DateUtils.format(list3.get(list3.size()-1).getSignTime(), "HH:mm");
				actualEndTime1 = DateUtils.format(list3.get(0).getSignTime(), DateUtils.FORMAT_LONG);
				String fmtS = DateUtils.format(dutyList.get(0).getStartTime(), DateUtils.FORMAT_LONG);
				String fmtE = DateUtils.format(dutyList.get(0).getEndTime(), DateUtils.FORMAT_LONG);
				if(DateUtils.addMinute(DateUtils.parse(fmtS), 5).getTime()>=list3.get(list3.size()-1).getSignTime().getTime()
						&&list3.get(list3.size()-1).getSignTime().getTime()>DateUtils.parse(fmtS).getTime()){
					actuaStartTime = DateUtils.format(dutyList.get(0).getStartTime(), "HH:mm");
				}
				if(!(ovrertime.getActualStartTime().getTime()>=DateUtils.parse(fmtS).getTime()&&ovrertime.getActualEndTime().getTime()<=DateUtils.parse(fmtE).getTime())){
					flag = "1";
				}
			}
		}else if("3".equals(date_type)){
			//查询值班记录
			EmployeeDuty dutyP = new EmployeeDuty();
			dutyP.setEmployId(ovrertime.getEmployeeId());
			dutyP.setDutyDate(ovrertime.getApplyDate());
			List<EmployeeDuty> dutyList = employeeDutyService.selectByCondition(dutyP);
			
			actuaStartTime = String.valueOf(Integer.valueOf(DateUtils.format(dutyList.get(0).getEndTime(), "HH:mm").substring(0,2))+1)+":00";
			//延时工作考勤计算规则（隔天加班）：5.50分到6点有打卡，就需要找6点到9点10分的最后一次打卡时间，
			//如有卡则取打卡时间，如打卡在9点与9点10分之间则取9点，如果没有打卡，就找6点前最后一次打卡时间，作为加班的工时累计的时间点
			AttnSignRecord record = new AttnSignRecord();
			Date nextDay = DateUtils.addDay(ovrertime.getApplyDate(), 1);
			record.setEmployeeId(ovrertime.getEmployeeId());
			record.setStartTime(DateUtils.parse(DateUtils.format(nextDay, DateUtils.FORMAT_SHORT)+" 05:50:00", DateUtils.FORMAT_LONG));
			record.setEndTime(DateUtils.parse(DateUtils.format(nextDay, DateUtils.FORMAT_SHORT)+" 06:00:00", DateUtils.FORMAT_LONG));
			List<AttnSignRecord> list = attnSignRecordService.getListBefore9(record);
			if(list!=null&&list.size()>0){
				//找6点到9点05分的最后一次打卡时间，
				record.setStartTime(DateUtils.parse(DateUtils.format(nextDay, DateUtils.FORMAT_SHORT)+" 06:00:00", DateUtils.FORMAT_LONG));
				record.setEndTime(DateUtils.parse(DateUtils.format(nextDay, DateUtils.FORMAT_SHORT)+" 09:05:00", DateUtils.FORMAT_LONG));
				List<AttnSignRecord> list1 = attnSignRecordService.getListBefore9(record);
				if(list1!=null&&list1.size()>0){
					actualEndTime = DateUtils.format(list1.get(0).getSignTime(), "HH:mm");
					actualEndTime1 = DateUtils.format(list1.get(0).getSignTime(), DateUtils.FORMAT_LONG);
				}else{
					actualEndTime = DateUtils.format(list.get(0).getSignTime(), "HH:mm");
					actualEndTime1 = DateUtils.format(list.get(0).getSignTime(), DateUtils.FORMAT_LONG);
				}
			}else{
				//5.50分到6点没有打卡，找20:00到05:50的记录
				String endTime = String.valueOf(Integer.valueOf(DateUtils.format(dutyList.get(0).getEndTime(), "HH:mm").substring(0,2))+2);
				record.setStartTime(DateUtils.parse(DateUtils.format(ovrertime.getApplyDate(), DateUtils.FORMAT_SHORT)+" "+endTime+":00:00", DateUtils.FORMAT_LONG));
				record.setEndTime(DateUtils.parse(DateUtils.format(nextDay, DateUtils.FORMAT_SHORT)+" 06:00:00", DateUtils.FORMAT_LONG));
				List<AttnSignRecord> list2 = attnSignRecordService.getListBefore9(record);
				if(list2!=null&&list2.size()>0){
					actualEndTime = DateUtils.format(list2.get(0).getSignTime(), "HH:mm");
					actualEndTime1 = DateUtils.format(list2.get(0).getSignTime(), DateUtils.FORMAT_LONG);
				}
			}
		}
		
		if(!"".equals(actuaStartTime)&&!"".equals(actualEndTime)){
			int sH = Integer.valueOf(actuaStartTime.substring(0, 2));
			int sM = Integer.valueOf(actuaStartTime.substring(3, 5));
			int eH = Integer.valueOf(actualEndTime.substring(0, 2));
			int eM = Integer.valueOf(actualEndTime.substring(3, 5));
			if(sM>0&&sM<30){
				sM = 30;
			}
			if(sM>30&&sM<=59){
				sM = 0;
				sH = sH+1;
			}
			if(eM>0&&eM<30){
				eM = 0;
			}
			if(eM>30&&eM<=59){
				eM = 30;
			}
			duration = GetHours.getHours(sH, sM, eH, eM);
			if("1".equals(date_type)||"2".equals(date_type)){
				if(duration>=10){
					duration = duration-2;
				}else if(duration>=5){
					duration = duration-1;
				}
			}
		}
		
		map.put("actuaStartTime", actuaStartTime);
		map.put("actualEndTime", actualEndTime);
		map.put("actualEndTime1", actualEndTime1);
		map.put("duration", duration);
		map.put("flag", flag);
		return map;
	}
	
	//填写实际时间
	@ResponseBody
	@RequestMapping("/addActualTime.htm")
	@Token(remove = true)
	public Map<String, Object> addActualTime(HttpServletRequest request,EmpApplicationOvertime overtime){
		User user = userService.getCurrentUser();
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			if(overtime.getActualStartTime()==null){
				throw new OaException("实际开始时间不能为空！");
			}
            if(overtime.getActualEndTime()==null){
        	    throw new OaException("实际结束时间不能为空！");
			}
			empApplicationOvertimeService.completeTask(request,overtime.getProcessInstanceId(),"填写实际工时", ConfigConstants.PASS,overtime);
			map.put("success",true);
		}catch(Exception e){
			logger.error(user.getEmployee().getCnName()+"延时-填写实际工时："+e.toString());
			String uuId = UUIDGenerator.generate();
            try {
				RedisUtils.setString(String.valueOf(user.getEmployee().getId()),uuId,1800000);
				map.put("token", uuId);
			} catch (Exception e1) {
				logger.error("延时-填写实际工时："+e1.toString());
			}
			map.put("message", e.getMessage());
			map.put("success",false);
			return map;
		}
		return map;
	}
	
	@RequestMapping("/backProcess.htm")
	@ResponseBody
	public String backProcess(String procisetId,String taskDefKey) {
		logger.info("backProcess参数：procisetId="+procisetId+";taskDefKey="+taskDefKey);
		String flag = "success";
		try{
			empApplicationOvertimeService.backProcess(procisetId, taskDefKey);
		}catch(Exception e){
			logger.error("回退失败="+e.getMessage());
			flag = e.getMessage();
		}
		
		return flag;
		
	}

}
