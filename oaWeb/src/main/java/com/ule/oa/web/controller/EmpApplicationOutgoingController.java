package com.ule.oa.web.controller;

import java.util.ArrayList;
import java.util.Calendar;
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
import com.ule.oa.base.po.EmpApplicationOutgoing;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.po.HiActinst;
import com.ule.oa.base.po.RunTask;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.EmpApplicationOutgoingService;
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
 * @ClassName: 外出申请
 * @Description: 外出申请
 * @author yangjie
 * @date 2017年6月9日
 */
@Controller
@RequestMapping("empApplicationOutgoing")
public class EmpApplicationOutgoingController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private UserService userService;
	@Autowired
	private EmpApplicationOutgoingService empApplicationOutgoingService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private HiActinstService hiActinstService;
	@Autowired
	private EmployeeClassService employeeClassService;
	@Autowired
	private RunTaskService runTaskService;
	@Autowired
	private ActivitiServiceImpl activitiServiceImpl;
	
	//外出申请首页
	@RequestMapping("/index.htm")
	@Token(generate=true)//生成token
	public String index(HttpServletRequest request, String urlType){
		User user = userService.getCurrentUser();
		try {
			request.setAttribute("mobile",user.getEmployee().getMobile());
			request.setAttribute("returnUrl", ReturnUrlUtil.getReturnUrl(urlType));
		} catch (Exception e) {
			logger.error(user.getEmployee().getCnName()+"外出-首页："+e.toString());
			return "base/empApplicationOutgoing/index";
		}
		return "base/empApplicationOutgoing/index";
	}
	
	//计算加班时间
	@ResponseBody
	@RequestMapping("/getDuration.htm")
	public Map<String, Object> getDuration(HttpServletRequest request){
		Map<String,Object> map = new HashMap<String, Object>();
		User user = userService.getCurrentUser();
		try{
			String startTime = request.getParameter("startTime");
			String endTime = request.getParameter("endTime");
			String outDate = request.getParameter("outDate");
			EmployeeClass employeeClass = new EmployeeClass();
			employeeClass.setEmployId(user.getEmployeeId());
			employeeClass.setClassDate(DateUtils.parse(outDate,DateUtils.FORMAT_SHORT));
			EmployeeClass result_e = employeeClassService.getEmployeeClassSetting(employeeClass);
			//不是节假日
			if(result_e!=null){
			    Date startDate = DateUtils.parse(outDate+" "+startTime+":00", DateUtils.FORMAT_LONG);
				Date endDate = DateUtils.parse(outDate+" "+endTime+":00", DateUtils.FORMAT_LONG);
				calmiu(startDate,endDate);
                //时间在排班开始时间之前或者之后
				if((startDate.getTime()<=result_e.getStartTime().getTime()&&endDate.getTime()<=result_e.getStartTime().getTime())
						||(startDate.getTime()>=result_e.getEndTime().getTime()&&endDate.getTime()>=result_e.getEndTime().getTime())){
					 map.put("duration", 0);
					 map.put("success", true);
				}else if(startDate.getTime()>=result_e.getStartTime().getTime()&&endDate.getTime()<=result_e.getEndTime().getTime()){
					 double duration =  ((endDate.getTime()-startDate.getTime())/(1800*1000));
					 map.put("duration", duration/2);
					 map.put("success", true);
				}else if(startDate.getTime()<result_e.getStartTime().getTime()&&endDate.getTime()<=result_e.getEndTime().getTime()){
					 double duration = ((endDate.getTime()-result_e.getStartTime().getTime())/(1800*1000));
					 map.put("duration", duration/2);
					 map.put("success", true);
				}else if(startDate.getTime()>=result_e.getStartTime().getTime()&&endDate.getTime()>result_e.getEndTime().getTime()){
					double duration = ((result_e.getEndTime().getTime()-startDate.getTime())/(1800*1000));
					map.put("duration", duration/2);
					map.put("success", true);
				}
			}else{
				 map.put("duration", 0);
				 map.put("success", true);
				 return map;
			}
		}catch(Exception e){
			logger.error(user.getEmployee().getCnName()+"外出-计算时间："+e.toString());
			map.put("message", "系统异常");
			map.put("success",false);
		}
		return map;
	}
	
	//将分钟数设置成整点或者半点
	public void calmiu(Date start_date,Date end_date){
		Calendar startDate = Calendar.getInstance();
		startDate.setTime(start_date);//开始时间
		int startMinute = startDate.get(Calendar.MINUTE);
		
		Calendar endDate = Calendar.getInstance();
		endDate.setTime(end_date);//结束时间
		int endMinute = startDate.get(Calendar.MINUTE);
		
		if(startMinute > 0 && startMinute < 30){
			startDate.set(Calendar.MINUTE, 0);
			startDate.set(Calendar.SECOND, 0);
		}
		if(startMinute > 30 && startMinute <= 59){
			startDate.set(Calendar.MINUTE, 30);
			startDate.set(Calendar.SECOND, 0);
		}
		if(endMinute >0 && endMinute < 30){
			endDate.set(Calendar.MINUTE, 30);
			endDate.set(Calendar.SECOND, 0);
		}
		if(endMinute > 30 && endMinute <= 59){
			endDate.add(Calendar.HOUR, 1);
			endDate.set(Calendar.MINUTE, 0);
			endDate.set(Calendar.SECOND, 0);
		}
		
		start_date = startDate.getTime();
		end_date = endDate.getTime();
	}
	
	//保存
	@ResponseBody
	@RequestMapping("/save.htm")
	@Token(remove = true)
	public Map<String, Object> save(HttpServletRequest request,EmpApplicationOutgoing userOutgoing){
		User user = userService.getCurrentUser();
		logger.info("外出申请单save入参:OutDate="+userOutgoing.getOutDate()+";startTime="+userOutgoing.getStartTime()+";endTime="+userOutgoing.getEndTime()
				+";duration="+userOutgoing.getDuration()+";mobile="+userOutgoing.getMobile()+";address="+userOutgoing.getAddress()
				+";reason="+userOutgoing.getReason()+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			if(userOutgoing.getOutDate()==null){
				throw new OaException("外出日期不能为空！");
			}
			//当日18点前只能请当日，18点后只能请次日及以后
			String nowDate18 = DateUtils.getNow("yyyy-MM-dd") + " 18:00:00";
			if(DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_LONG), DateUtils.FORMAT_LONG).getTime()<=DateUtils.parse(nowDate18, DateUtils.FORMAT_LONG).getTime()){
				//18点前只能请当日及以后的
				if(DateUtils.getIntervalDays(DateUtils.parse(DateUtils.getNow("yyyy-MM-dd"),DateUtils.FORMAT_SHORT),userOutgoing.getOutDate())<0){
					throw new OaException("18点前只能申请当日及以后的外出申请！");
				}
			}else{
				//18点后
				if(DateUtils.getIntervalDays(DateUtils.parse(DateUtils.getNow("yyyy-MM-dd"),DateUtils.FORMAT_SHORT),userOutgoing.getOutDate())<=0){
					throw new OaException("18点后只能申请次日以后的外出申请！");
				}
			}
			if(userOutgoing.getStartTime()==null){
				throw new OaException("外出开始时间不能为空！");
			}
            if(userOutgoing.getEndTime()==null){
            	throw new OaException("外出结束时间不能为空！");
			}
            if(userOutgoing.getDuration()==null||userOutgoing.getDuration()<=0){
            	throw new OaException("外出时长必须大于0！");
			}
            if(StringUtils.isBlank(userOutgoing.getAddress())){
            	throw new OaException("外出地点不能为空！");
			}
            if(StringUtils.isBlank(userOutgoing.getMobile())){
            	throw new OaException("联系电话不能为空！");
			}
            if(StringUtils.isBlank(userOutgoing.getReason())){
            	throw new OaException("外出事由不能为空！");
			}
			userOutgoing.setEmployeeId(user.getEmployee().getId());
			userOutgoing.setCnName(user.getEmployee().getCnName());
			userOutgoing.setCode(user.getEmployee().getCode());
			userOutgoing.setDepartId(user.getDepart().getId());
			userOutgoing.setDepartName(user.getDepart().getName());
			userOutgoing.setPositionId(user.getPosition().getId());
			userOutgoing.setPositionName(user.getPosition().getPositionName());
			userOutgoing.setDelFlag(CommonPo.STATUS_NORMAL);
			userOutgoing.setSubmitDate(new Date());
			userOutgoing.setCreateTime(new Date());
			userOutgoing.setCreateUser(user.getEmployee().getCnName());
			userOutgoing.setApprovalStatus(EmpApplicationOutgoing.APPROVAL_STATUS_WAIT);
			userOutgoing.setVersion(null);
			map = empApplicationOutgoingService.save(userOutgoing);
		}catch(Exception e){
			logger.error(user.getEmployee().getCnName()+"外出-提交："+e.toString());
			String uuId = UUIDGenerator.generate();
            try {
				RedisUtils.setString(String.valueOf(user.getEmployee().getId()),uuId,1800000);
				map.put("token", uuId);
			} catch (Exception e1) {
				logger.error("外出申请："+e1.toString());
			}
			map.put("repeatFlag", false);
			map.put("message",e.getMessage());
			map.put("success",false);
			return map;
		}
		return map;
	}
	
	//外出申请审批页面
	@RequestMapping("/approval.htm")
	@Token(generate=true)//生成token
	public String approval(HttpServletRequest request,Long outgoingId,String flag, String urlType){
		User user = userService.getCurrentUser();
		try {
			request.setAttribute("imgUrl",(user.getEmployee().getPicture()!=null&&!"".equals(user.getEmployee().getPicture()))?user.getEmployee().getPicture():ConfigConstants.HEAD_PICTURE_URL);
			EmpApplicationOutgoing userOutgoing = empApplicationOutgoingService.getById(outgoingId);
			request.setAttribute("userOutgoing", userOutgoing);
			request.setAttribute("isSelf", false);
			request.setAttribute("canApprove", false);
			if(userOutgoing.getApprovalStatus().intValue()==100) {
				if(userOutgoing.getEmployeeId().longValue()==user.getEmployeeId().longValue()){
					request.setAttribute("isSelf", true);
				}
				//办理中,taskService查询任务
				Task task = activitiServiceImpl.queryTaskByProcessInstanceId(userOutgoing.getProcessInstanceId());
				request.setAttribute("actName", task.getName());
				if(StringUtils.equalsIgnoreCase("can", flag)) {
					request.setAttribute("canApprove", true);
				}
			}
			request.setAttribute("returnUrl", ReturnUrlUtil.getReturnUrl(urlType));
		} catch (Exception e) {
			logger.error(user.getEmployee().getCnName()+"外出-审批首页："+e.toString());
			return "base/empApplicationOutgoing/approval";
		}
		return "base/empApplicationOutgoing/approval";
	}
	
	//外出申请流程页面
	@RequestMapping("/process.htm")
	public String process(HttpServletRequest request, Long outgoingId,Long ruProcdefId){
		EmpApplicationOutgoing userOutgoing = empApplicationOutgoingService.getById(outgoingId);
		request.setAttribute("userOutgoing", userOutgoing);
		RunTask param = new RunTask();
		param.setReProcdefCode(RunTask.RUN_CODE_70);
		param.setEntityId(String.valueOf(outgoingId));
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
		return "base/empApplicationOutgoing/process";
	}

}
