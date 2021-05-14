package com.ule.oa.web.controller;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.apache.commons.lang3.StringUtils;
import org.flowable.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.ule.oa.base.cache.ConfigCacheManager;
import com.ule.oa.base.mapper.BaseEmpWorkLogMapper;
import com.ule.oa.base.mapper.ScheduleGroupMapper;
import com.ule.oa.base.po.AnnualVacation;
import com.ule.oa.base.po.ApplicationEmployeeClass;
import com.ule.oa.base.po.ApplicationEmployeeClassDetail;
import com.ule.oa.base.po.ApplicationEmployeeDuty;
import com.ule.oa.base.po.ApplicationEmployeeDutyDetail;
import com.ule.oa.base.po.BaseEmpWorkLog;
import com.ule.oa.base.po.ClassSetting;
import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.po.EmployeeDuty;
import com.ule.oa.base.po.HiActinst;
import com.ule.oa.base.po.RunTask;
import com.ule.oa.base.po.ScheduleGroup;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.AnnualVacationService;
import com.ule.oa.base.service.ApplicationEmployeeClassDetailService;
import com.ule.oa.base.service.ApplicationEmployeeClassService;
import com.ule.oa.base.service.ApplicationEmployeeDutyService;
import com.ule.oa.base.service.ClassSetPersonService;
import com.ule.oa.base.service.ClassSettingService;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmployeeClassService;
import com.ule.oa.base.service.EmployeeDutyService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.HiActinstService;
import com.ule.oa.base.service.RuProcdefService;
import com.ule.oa.base.service.RunTaskService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.service.UserTaskService;
import com.ule.oa.base.service.impl.ActivitiServiceImpl;
import com.ule.oa.base.vo.TaskVO;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.JSON;
import com.ule.oa.common.utils.JsonWriter;
import com.ule.oa.common.utils.RedisUtils;
import com.ule.oa.common.utils.json.JSONUtils;
import com.ule.oa.web.util.ReturnUrlUtil;
import com.ule.oa.web.util.Token;

@Controller
@RequestMapping("employeeClass")
public class EmployeeClassController {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Resource
	private EmployeeClassService employeeClassService;
	@Resource
	private ApplicationEmployeeClassService applicationEmployeeClassService;
	@Resource
	private ApplicationEmployeeClassDetailService applicationEmployeeClassDetailService;
	@Resource
	private ApplicationEmployeeDutyService applicationEmployeeDutyService;
	@Resource
	private EmployeeService employeeService;
	@Autowired
	private UserService userService;
	@Autowired
	private HiActinstService hiActinstService;
	@Autowired
	private RunTaskService runTaskService;
	@Resource
	private DepartService departService;
	@Resource
	private AnnualVacationService annualVacationService;
	@Resource
	private ClassSetPersonService classSetPersonService;
	@Resource
	private RuProcdefService ruProcdefService;
	@Resource
	private ClassSettingService classSettingService;
	@Resource
	private ConfigService configService;
	@Resource
	private EmployeeDutyService employeeDutyService;
	@Resource
	private ConfigCacheManager configCacheManager;
	@Autowired
	private ActivitiServiceImpl activitiServiceImpl;
	@Autowired
	private UserTaskService userTaskService;
	@Autowired
	private ScheduleGroupMapper scheduleGroupMapper;
	@Autowired
	private BaseEmpWorkLogMapper baseEmpWorkLogMapper;
	
	/**
	 * 排班首页
	 * @return
	 */
	@RequestMapping("/index.htm")
	@Token(generate=true)//生成token
	public String index(HttpServletRequest request, String urlType){
		try {
			User user = userService.getCurrentUser();
			boolean isSetPerson = classSetPersonService.isSetPerson(user.getEmployee().getId());
	    	request.setAttribute("isSetPerson",isSetPerson);
	    	boolean isDh = departService.checkLeaderIsDh(user.getEmployee().getId());
	    	request.setAttribute("isDh",isDh);
			request.setAttribute("returnUrl", ReturnUrlUtil.getReturnUrl(urlType));
			request.setAttribute("lastMonth",DateUtils.format(DateUtils.addMonth(new Date(),1),"MM"));
		} catch (Exception e) {
			return "employeeClass/index";
		}
		return "employeeClass/index";
	}
	
	//新增下月排班申请数据
	@RequestMapping("/addLastMonth.htm")
	@ResponseBody
	@Token(remove = true)
	public Map<String,Object> addLastMonth(HttpServletRequest request,String token){
		Map<String,Object> map = new HashMap<String, Object>();
		User user = userService.getCurrentUser();
		try{
			applicationEmployeeClassService.addLastMonth(user);
			map.put("message", "新增成功!");
			map.put("success", true);
			return map;
		}catch(Exception e){
			logger.error(user.getEmployee().getCnName()+"新增下月排班申请数据失败，原因={}",e.getMessage());
			map.put("repeatFlag", false);
			map.put("message",e.getMessage());
			map.put("success",false);
			return map;
		}
	}
	
	/**
	 * 排班查询页面
	 * @return
	 */
	@RequestMapping("/query.htm")
	@Token(generate=true)//生成token
	public String query(HttpServletRequest request,String urlType){
		request.setAttribute("returnUrl", ReturnUrlUtil.getReturnUrl(urlType));
		return "employeeClass/query";
	}
	
	/**
	 * 编辑排班（日常）页面
	 * @return
	 */
	@RequestMapping("/updateNormal.htm")
	@Token(generate=true)//生成token
	public String updateNormal(HttpServletRequest request,Long id){
		try {
			User user = userService.getCurrentUser();
			ApplicationEmployeeClass applicationEmployeeClass = new ApplicationEmployeeClass();
			applicationEmployeeClass.setId(id);
			List<ApplicationEmployeeClass> list = applicationEmployeeClassService.getByCondition(applicationEmployeeClass);
			if(list!=null&&list.size()>0){
				request.setAttribute("classdetailId", id);
				request.setAttribute("departId", list.get(0).getDepartId());
				request.setAttribute("classMonth",DateUtils.format(list.get(0).getClassMonth(), "yyyy-MM"));
				request.setAttribute("month",DateUtils.format(list.get(0).getClassMonth(), "yyyy年MM月"));
				RedisUtils.delete("classsSet_"+DateUtils.format(list.get(0).getClassMonth(), "yyyy-MM")+"_"+String.valueOf(user.getEmployeeId()));
				RedisUtils.delete("delete_classsSet_"+DateUtils.format(list.get(0).getClassMonth(), "yyyy-MM")+"_"+String.valueOf(user.getEmployeeId()));
			}
		} catch (Exception e) {
			return "employeeClass/update_normal";
		}
		return "employeeClass/update_normal";
	}
	
	//查询部门所有员工数据(编辑也，详情页，审批页的头信息)
	@RequestMapping("/getEmployeeClassHours.htm")
	@ResponseBody
	public List<ApplicationEmployeeClassDetail> getEmployeeClassHours(Long attnApplicationEmployClassId){
		ApplicationEmployeeClassDetail classDetail = new ApplicationEmployeeClassDetail();
		classDetail.setAttnApplicationEmployClassId(attnApplicationEmployClassId);
		ApplicationEmployeeClass employeeClass = new ApplicationEmployeeClass();
		employeeClass.setId(attnApplicationEmployClassId);
		List<ApplicationEmployeeClass> list = applicationEmployeeClassService.getByCondition(employeeClass);
		if(list!=null&&list.size()>0){
			classDetail.setDepartId(list.get(0).getDepartId());
			classDetail.setGroupId(list.get(0).getGroupId());
		}
		return applicationEmployeeClassDetailService.getEmployeeClassHours(classDetail);
	}
	
	/**
	 * 排班（日常）查看页面
	 * @return
	 */
	@RequestMapping("/detailNormal.htm")
	public String detailNormal(HttpServletRequest request,Long id,String type){
		//type（控制前端跳转页面）
		request.setAttribute("type", type);
		ApplicationEmployeeClass applicationEmployeeClass = new ApplicationEmployeeClass();
		applicationEmployeeClass.setId(id);
		List<ApplicationEmployeeClass> list = applicationEmployeeClassService.getByCondition(applicationEmployeeClass);
		if(list!=null&&list.size()>0){
			request.setAttribute("classdetailId", id);
			request.setAttribute("departId", list.get(0).getDepartId());
			request.setAttribute("classMonth",DateUtils.format(list.get(0).getClassMonth(), "yyyy-MM"));
			request.setAttribute("month",DateUtils.format(list.get(0).getClassMonth(), "yyyy年MM月"));
		}
		return "employeeClass/detail_normal";
	}
	
	/**
	 * 添加班次页面
	 * @return
	 */
	@RequestMapping("/classSet.htm")
	@Token(generate=true)//生成token
	public String classSet(HttpServletRequest request, String urlType){
		request.setAttribute("returnUrl", ReturnUrlUtil.getReturnUrl(urlType));
		return "employeeClass/classSet";
	}
	
	/**
	 * 审核排班页面
	 * @return
	 */
	@RequestMapping("/approveNormal.htm")
	@Token(generate=true)//生成token
	public String approveNormal(HttpServletRequest request,String flag,Long employeeClassId,String urlType){
		try {
			User user = userService.getCurrentUser();
			ApplicationEmployeeClass applicationEmployeeClass = new ApplicationEmployeeClass();
			applicationEmployeeClass.setId(employeeClassId);
			List<ApplicationEmployeeClass> list = applicationEmployeeClassService.getByCondition(applicationEmployeeClass);
			if(list!=null&&list.size()>0){
				request.setAttribute("employeeClass", list.get(0));
				Task task = activitiServiceImpl.queryTaskByProcessInstanceId(list.get(0).getProcessInstanceId());
				TaskVO taskVO = new TaskVO();
				if(task!=null){
					taskVO.setActName(task.getName());
				}
				taskVO.setProcessId(list.get(0).getProcessInstanceId());
				request.setAttribute("taskVO", taskVO);
				request.setAttribute("classMonth",DateUtils.format(list.get(0).getClassMonth(), "yyyy-MM"));
				request.setAttribute("month",DateUtils.format(list.get(0).getClassMonth(), "yyyy年MM月"));
				request.setAttribute("returnUrl", ReturnUrlUtil.getReturnUrl(urlType));
				request.setAttribute("isSelf", false);
			    request.setAttribute("canApprove", false);
 				//审批显示
 				if(list.get(0).getApprovalStatus().intValue()==100) {
 					if(list.get(0).getEmployeeId().longValue()==user.getEmployeeId().longValue()){
 						request.setAttribute("isSelf", true);
 					}
 					if(StringUtils.equalsIgnoreCase("can", flag)) {
 						request.setAttribute("canApprove", true);
 					}
 				}
			}
		} catch (Exception e) {
			return "employeeClass/approve_normal";
		}
		return "employeeClass/approve_normal";
	}
	
	/**
	 * hr单独审核排班页面
	 * @return
	 */
	@RequestMapping("/toHrApprove.htm")
	@Token(generate=true)//生成token
	public String toHrApprove(HttpServletRequest request,Long employeeClassId){
		try {
			ApplicationEmployeeClass applicationEmployeeClass = new ApplicationEmployeeClass();
			applicationEmployeeClass.setId(employeeClassId);
			List<ApplicationEmployeeClass> list = applicationEmployeeClassService.getByCondition(applicationEmployeeClass);
			if(list!=null&&list.size()>0){
				request.setAttribute("employeeClass", list.get(0));
				Task task = activitiServiceImpl.queryTaskByProcessInstanceId(list.get(0).getProcessInstanceId());
				TaskVO taskVO = new TaskVO();
				if(task!=null){
					taskVO.setActName(task.getName());
				}
				taskVO.setProcessId(list.get(0).getProcessInstanceId());
				request.setAttribute("taskVO", taskVO);
				request.setAttribute("classMonth",DateUtils.format(list.get(0).getClassMonth(), "yyyy-MM"));
				request.setAttribute("month",DateUtils.format(list.get(0).getClassMonth(), "yyyy年MM月"));
				request.setAttribute("classdetailId", employeeClassId);
				request.setAttribute("departId", list.get(0).getDepartId());
				request.setAttribute("classMonth",DateUtils.format(list.get(0).getClassMonth(), "yyyy-MM"));
				request.setAttribute("month",DateUtils.format(list.get(0).getClassMonth(), "yyyy年MM月"));
			}
		} catch (Exception e) {
			return "employeeClass/hr_approve_normal";
		}
		return "employeeClass/hr_approve_normal";
	}
	
	//hr单独审核通过
	@ResponseBody
	@RequestMapping("/hrPassNormal.htm")
	@Token(remove = true)
	public Map<String, Object> hrPassNormal(HttpServletRequest request,String processInstanceId,String token){
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			userTaskService.completeTask(request,processInstanceId, "审批通过",ConfigConstants.PASS);
			map.put("success",true);
		}catch(Exception e){
			map.put("repeatFlag", false);
			map.put("message",e.getMessage());
			map.put("success",false);
			return map;
		}
		return map;
	}
	
	//hr单独审核拒绝
	@ResponseBody
	@RequestMapping("/hrRefuseNormal.htm")
	@Token(remove = true)
	public Map<String, Object> hrRefuseNormal(HttpServletRequest request,String processInstanceId,String token){
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			userTaskService.completeTask(request,processInstanceId, "审批拒绝",ConfigConstants.REFUSE);
			map.put("success",true);
		}catch(Exception e){
			map.put("repeatFlag", false);
			map.put("message",e.getMessage());
			map.put("success",false);
			return map;
		}
		return map;
	}
	
	/**
	 * Hr单独调班日常页面
	 * @return
	 */
	@RequestMapping("/toHrMoveEmployeeClass.htm")
	@Token(generate=true)//生成token
	public String toHrMoveEmployeeClass(HttpServletRequest request,Long id,Long departId){
		User user = userService.getCurrentUser();
		logger.info("员工toMoveEmployeeClass,employeeId="+user.getEmployee().getId()+";id="+id);
		try {
			ApplicationEmployeeClass applicationEmployeeClass = new ApplicationEmployeeClass();
			applicationEmployeeClass.setId(id);
			List<ApplicationEmployeeClass> list = applicationEmployeeClassService.getByCondition(applicationEmployeeClass);
			if(list!=null&&list.size()>0){
				request.setAttribute("classdetailId", id);
				request.setAttribute("departId", list.get(0).getDepartId());
				request.setAttribute("classMonth",DateUtils.format(list.get(0).getClassMonth(), "yyyy-MM"));
				request.setAttribute("month",DateUtils.format(list.get(0).getClassMonth(), "yyyy年MM月"));
				RedisUtils.delete("moveClasssSet_"+DateUtils.format(list.get(0).getClassMonth(), "yyyy-MM")+"_"+String.valueOf(user.getEmployeeId()));
				RedisUtils.delete("delete_moveClasssSet_"+DateUtils.format(list.get(0).getClassMonth(), "yyyy-MM")+"_"+String.valueOf(user.getEmployeeId()));
			}
			request.setAttribute("departId", departId);
		} catch (Exception e) {
			logger.error("toMoveEmployeeClass"+e.toString());
		}
		return "employeeClass/hr_move_normal";
	}
	
	//Hr调班提交
	@RequestMapping("/hrMoveEmployeeClass.htm")
	@ResponseBody
	@Token(remove = true)
	public Map<String,Object> hrMoveEmployeeClass(HttpServletRequest request,String token,Long classdetailId,String classMonth){
		Map<String,Object> map = new HashMap<String, Object>();
		User user = userService.getCurrentUser();
		try{
			String update = RedisUtils.getString("moveClasssSet_"+classMonth+"_"+String.valueOf(user.getEmployeeId()));
			String delete = RedisUtils.getString("delete_moveClasssSet_"+classMonth+"_"+String.valueOf(user.getEmployeeId()));
			if((update==null||"".equals(update))&&(delete==null||"".equals(delete))){
				throw new OaException("没有选择员工调班！");
			}
			Map<String,Map<String,String>> classDetailMap = new HashMap<String,Map<String,String>>();
			Map<String,Map<String,String>> classDetailMap1 = new HashMap<String,Map<String,String>>();
			if(update!=null&&!"".equals(update)){
				classDetailMap = (Map<String, Map<String, String>>) JSONUtils.readAsMap(update);
			}
			if(delete!=null&&!"".equals(delete)){
				classDetailMap1 = (Map<String, Map<String, String>>) JSONUtils.readAsMap(delete);
			}
			applicationEmployeeClassService.HrMoveEmployeeClass(classdetailId, user, classDetailMap,classDetailMap1);
			RedisUtils.delete("moveClasssSet_"+classMonth+"_"+String.valueOf(user.getEmployeeId()));
			RedisUtils.delete("delete_moveClasssSet_"+classMonth+"_"+String.valueOf(user.getEmployeeId()));
			map.put("message", "保存成功!");
			map.put("success", true);
			return map;
		}catch(Exception e){
			logger.error(user.getEmployee().getCnName()+"调班提交，原因={}",e.getMessage());
			try {
				RedisUtils.delete("moveClasssSet_"+classMonth+"_"+String.valueOf(user.getEmployeeId()));
			} catch (Exception e1) {
				logger.error(user.getEmployee().getCnName()+"调班提交，原因={}","获取缓存失败");
			}
			map.put("repeatFlag", false);
			map.put("message",e.getMessage());
			map.put("success",false);
			return map;
		}
	}
	
	//排班审批流程页面
	@RequestMapping("/processNormal.htm")
	public String processNormal(HttpServletRequest request, Long employeeClassId,Long ruProcdefId){
		ApplicationEmployeeClass applicationEmployeeClass = new ApplicationEmployeeClass();
		applicationEmployeeClass.setId(employeeClassId);
		List<ApplicationEmployeeClass> employeeClassList = applicationEmployeeClassService.getByCondition(applicationEmployeeClass);
		if(employeeClassList!=null&&employeeClassList.size()>0){
			request.setAttribute("employeeClass", employeeClassList.get(0));
			RunTask param = new RunTask();
			param.setReProcdefCode(RunTask.RUN_CODE_50);
			param.setEntityId(String.valueOf(employeeClassId));
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
		}
		return "employeeClass/process_normal";
	}
	
	//通过
	@ResponseBody
	@RequestMapping("/passNormal.htm")
	@Token(remove = true)
	public Map<String, Object> passNormal(HttpServletRequest request,String processInstanceId,String token){
		User user = userService.getCurrentUser();
		Map<String,Object> map = new HashMap<String, Object>();
		try{
		    applicationEmployeeClassService.completeTask(request,processInstanceId, "审批通过", ConfigConstants.PASS);
		    map.put("success",true);
		}catch(Exception e){
			map.put("repeatFlag", false);
			map.put("message",e.getMessage());
			map.put("success",false);
			return map;
		}
		return map;
	}
	
	//拒绝
	@ResponseBody
	@RequestMapping("/refuseNormal.htm")
	@Token(remove = true)
	public Map<String, Object> refuseNormal(HttpServletRequest request,String processInstanceId,String token){
		User user = userService.getCurrentUser();
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			 applicationEmployeeClassService.completeTask(request,processInstanceId,"审批拒绝", ConfigConstants.REFUSE);
		     map.put("success",true);
		}catch(Exception e){
			map.put("repeatFlag", false);
			map.put("message",e.getMessage());
			map.put("success",false);
			return map;
		}
		return map;
	}
	
	//提交审核
	@RequestMapping("/submitEmployeeClass.htm")
	@ResponseBody
	@Token(remove = true)
	public Map<String,Object> submitEmployeeClass(HttpServletRequest request,String token,Long classdetailId){
		Map<String,Object> map = new HashMap<String, Object>();
		User user = userService.getCurrentUser();
		try{
			ApplicationEmployeeClass applicationEmployeeClass = new ApplicationEmployeeClass();
			applicationEmployeeClass.setId(classdetailId);
			List<ApplicationEmployeeClass> employeeClassList = applicationEmployeeClassService.getByCondition(applicationEmployeeClass);
			if(employeeClassList!=null&&employeeClassList.size()>0){
				if(employeeClassList.get(0).getIsMove().intValue()==0){
					//审核时间限制
					Config configCondition = new Config();
					configCondition.setCode("timeLimit3");
					List<Config> list = configService.getListByCondition(configCondition);
					int num = Integer.valueOf(list.get(0).getDisplayCode());
					Date date = annualVacationService.getWorkingDayPre(num, DateUtils.addDay(employeeClassList.get(0).getClassMonth(), -1));
					if(DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_SHORT), DateUtils.FORMAT_SHORT).getTime()>date.getTime()){
						throw new OaException("已超过提交审核时间！");
					}
				}
			}else{
				throw new OaException("审核数据不存在！");
			}
			map = applicationEmployeeClassService.submitApprove(classdetailId, user);
			return map;
		}catch(Exception e){
			logger.error("提交审核失败，原因={}",e);
			map.put("repeatFlag", false);
			map.put("message",e.getMessage());
			map.put("success",false);
			return map;
		}
	}
	
	/**
	 * 调班日常页面
	 * @return
	 */
	@RequestMapping("/toMoveEmployeeClass.htm")
	@Token(generate=true)//生成token
	public String toMoveEmployeeClass(HttpServletRequest request,Long id){
		User user = userService.getCurrentUser();
		logger.info("员工toMoveEmployeeClass,employeeId="+user.getEmployee().getId()+";id="+id);
		try {
			ApplicationEmployeeClass applicationEmployeeClass = new ApplicationEmployeeClass();
			applicationEmployeeClass.setId(id);
			List<ApplicationEmployeeClass> list = applicationEmployeeClassService.getByCondition(applicationEmployeeClass);
			if(list!=null&&list.size()>0){
				request.setAttribute("classdetailId", id);
				request.setAttribute("departId", list.get(0).getDepartId());
				request.setAttribute("classMonth",DateUtils.format(list.get(0).getClassMonth(), "yyyy-MM"));
				request.setAttribute("month",DateUtils.format(list.get(0).getClassMonth(), "yyyy年MM月"));
				RedisUtils.delete("moveClasssSet_"+DateUtils.format(list.get(0).getClassMonth(), "yyyy-MM")+"_"+String.valueOf(user.getEmployeeId()));
				RedisUtils.delete("delete_moveClasssSet_"+DateUtils.format(list.get(0).getClassMonth(), "yyyy-MM")+"_"+String.valueOf(user.getEmployeeId()));
			}
		} catch (Exception e) {
			logger.error("toMoveEmployeeClass"+e.toString());
		}
		return "employeeClass/move_normal";
	}
	
	//缓存调班数据
	@RequestMapping("/cacheMoveEmployeeClass.htm")
	@ResponseBody
	public void cacheMoveEmployeeClass(HttpServletRequest request,String employeeId,String classDate,String classSetId,String classMonth,String type){
		 User user = userService.getCurrentUser();
		try{
		    String update = RedisUtils.getString("moveClasssSet_"+classMonth+"_"+String.valueOf(user.getEmployeeId()));
		    String delete = RedisUtils.getString("delete_moveClasssSet_"+classMonth+"_"+String.valueOf(user.getEmployeeId()));
		    if(update!=null&&!"".equals(update)){
		    	Map<String,Map<String,String>> map = (Map<String, Map<String, String>>) JSONUtils.readAsMap(update);
		    	if(map.containsKey(employeeId)){
		    		Map<String,String> employeeMap = map.get(employeeId);
		    		employeeMap.put(classDate, classSetId);
		    		RedisUtils.setString("moveClasssSet_"+classMonth+"_"+String.valueOf(user.getEmployeeId()),JSONUtils.write(map), 1800000);
		    	}else{
		    		Map<String,String> employeeMap = new HashMap<String,String>();
			    	employeeMap.put(classDate, classSetId);
			    	map.put(employeeId, employeeMap);
			    	RedisUtils.setString("moveClasssSet_"+classMonth+"_"+String.valueOf(user.getEmployeeId()),JSONUtils.write(map), 1800000);
		    	}
		    }else{
		    	Map<String,Map<String,String>> map = new HashMap<String, Map<String,String>>();
		    	Map<String,String> employeeMap = new HashMap<String,String>();
		    	employeeMap.put(classDate, classSetId);
		    	map.put(employeeId, employeeMap);
		    	RedisUtils.setString("moveClasssSet_"+classMonth+"_"+String.valueOf(user.getEmployeeId()),JSONUtils.write(map),1800000);
		    }
		    if(delete!=null&&!"".equals(delete)){
		    	Map<String,Map<String,String>> map = (Map<String, Map<String, String>>) JSONUtils.readAsMap(delete);
		    	if(map.containsKey(employeeId)){
		    		Map<String,String> employeeMap = map.get(employeeId);
		    		if(employeeMap.containsKey(classDate)){
		    			employeeMap.remove(classDate);
		    			RedisUtils.setString("delete_moveClasssSet_"+classMonth+"_"+String.valueOf(user.getEmployeeId()),JSONUtils.write(map), 1800000);
		    		}
		    	}
		    }
		}catch(Exception e){
			logger.error(user.getEmployee().getCnName()+"缓存调班数据失败"+e.getMessage());
		}
	}
	
	//去除员工调班缓存数据
	@RequestMapping("/removeEmployeeClassCache.htm")
	@ResponseBody
	public void removeEmployeeClassCache(HttpServletRequest request,String employeeId,String classDate,String classSetId,String classMonth,String type){
		User user = userService.getCurrentUser();
		try{
		    String update = RedisUtils.getString("moveClasssSet_"+classMonth+"_"+String.valueOf(user.getEmployeeId()));
		    String delete = RedisUtils.getString("delete_moveClasssSet_"+classMonth+"_"+String.valueOf(user.getEmployeeId()));
		    if(update!=null&&!"".equals(update)){
		    	Map<String,Map<String,String>> map = (Map<String, Map<String, String>>) JSONUtils.readAsMap(update);
		    	if(map.containsKey(employeeId)){
		    		Map<String,String> employeeMap = map.get(employeeId);
		    		if(employeeMap.containsKey(classDate)){
		    			employeeMap.remove(classDate);
		    			RedisUtils.setString("moveClasssSet_"+classMonth+"_"+String.valueOf(user.getEmployeeId()),JSONUtils.write(map), 1800000);
		    		}
		    	}
		    }
		    //选择取消时，存储缓存
		    if("1".equals(type)){
		    	if(delete!=null&&!"".equals(delete)){
			    	Map<String,Map<String,String>> map = (Map<String, Map<String, String>>) JSONUtils.readAsMap(delete);
			    	if(map.containsKey(employeeId)){
			    		Map<String,String> employeeMap = map.get(employeeId);
			    		employeeMap.put(classDate, classSetId);
			    		RedisUtils.setString("delete_moveClasssSet_"+classMonth+"_"+String.valueOf(user.getEmployeeId()),JSONUtils.write(map), 1800000);
			    	}else{
			    		Map<String,String> employeeMap = new HashMap<String,String>();
				    	employeeMap.put(classDate, classSetId);
				    	map.put(employeeId, employeeMap);
				    	RedisUtils.setString("delete_moveClasssSet_"+classMonth+"_"+String.valueOf(user.getEmployeeId()),JSONUtils.write(map), 1800000);
			    	}
			    }else{
			    	Map<String,Map<String,String>> map = new HashMap<String, Map<String,String>>();
			    	Map<String,String> employeeMap = new HashMap<String,String>();
			    	employeeMap.put(classDate, classSetId);
			    	map.put(employeeId, employeeMap);
			    	RedisUtils.setString("delete_moveClasssSet_"+classMonth+"_"+String.valueOf(user.getEmployeeId()),JSONUtils.write(map),1800000);
			    }
		    }
		}catch(Exception e){
			logger.error(user.getEmployee().getCnName()+"去除员工调班缓存数据失败"+e.getMessage());
		}
	}
	
	//去除员工编辑缓存数据并且新增待删除的缓存
	@RequestMapping("/removeCache.htm")
	@ResponseBody
	public void removeCache(HttpServletRequest request,String employeeId,String classDate,String classSetId,String classMonth){
		User user = userService.getCurrentUser();
		try{
		    //清除新增/待修改缓存
		    String addOrUpdate = RedisUtils.getString("classsSet_"+classMonth+"_"+String.valueOf(user.getEmployeeId()));
		    String delete = RedisUtils.getString("delete_classsSet_"+classMonth+"_"+String.valueOf(user.getEmployeeId()));
		    if(addOrUpdate!=null&&!"".equals(addOrUpdate)){
		    	Map<String,Map<String,String>> map = (Map<String, Map<String, String>>) JSONUtils.readAsMap(addOrUpdate);
		    	if(map.containsKey(employeeId)){
		    		Map<String,String> employeeMap = map.get(employeeId);
		    		if(employeeMap.containsKey(classDate)){
		    			employeeMap.remove(classDate);
		    			RedisUtils.setString("classsSet_"+classMonth+"_"+String.valueOf(user.getEmployeeId()),JSONUtils.write(map), 1800000);
		    		}
		    	}
		    }
		    //新增待删缓存
		    if(delete!=null&&!"".equals(delete)){
		    	Map<String,Map<String,String>> map = (Map<String, Map<String, String>>) JSONUtils.readAsMap(delete);
		    	if(map.containsKey(employeeId)){
		    		Map<String,String> employeeMap = map.get(employeeId);
		    		employeeMap.put(classDate, classSetId);
		    		RedisUtils.setString("delete_classsSet_"+classMonth+"_"+String.valueOf(user.getEmployeeId()),JSONUtils.write(map), 1800000);
		    	}else{
		    		Map<String,String> employeeMap = new HashMap<String,String>();
			    	employeeMap.put(classDate, classSetId);
			    	map.put(employeeId, employeeMap);
			    	RedisUtils.setString("delete_classsSet_"+classMonth+"_"+String.valueOf(user.getEmployeeId()),JSONUtils.write(map), 1800000);
		    	}
		    }else{
		    	Map<String,Map<String,String>> map = new HashMap<String, Map<String,String>>();
		    	Map<String,String> employeeMap = new HashMap<String,String>();
		    	employeeMap.put(classDate, classSetId);
		    	map.put(employeeId, employeeMap);
		    	RedisUtils.setString("delete_classsSet_"+classMonth+"_"+String.valueOf(user.getEmployeeId()),JSONUtils.write(map),1800000);
		    }
		}catch(Exception e){
			logger.error(user.getEmployee().getCnName()+"去除员工编辑缓存数据并且新增待删除的缓存失败"+e.getMessage());
		}
	}

	//调班提交
	@RequestMapping("/moveEmployeeClass.htm")
	@ResponseBody
	@Token(remove = true)
	public Map<String,Object> moveEmployeeClass(HttpServletRequest request,String token,Long classdetailId,String classMonth){
		Map<String,Object> map = new HashMap<String, Object>();
		User user = userService.getCurrentUser();
		try{
			String update = RedisUtils.getString("moveClasssSet_"+classMonth+"_"+String.valueOf(user.getEmployeeId()));
			String delete = RedisUtils.getString("delete_moveClasssSet_"+classMonth+"_"+String.valueOf(user.getEmployeeId()));
			if((update==null||"".equals(update))&&(delete==null||"".equals(delete))){
				throw new OaException("没有选择员工调班！");
			}
			Map<String,Map<String,String>> classDetailMap = new HashMap<String,Map<String,String>>();
			Map<String,Map<String,String>> classDetailMap1 = new HashMap<String,Map<String,String>>();
			if(update!=null&&!"".equals(update)){
				classDetailMap = (Map<String, Map<String, String>>) JSONUtils.readAsMap(update);
			}
			if(delete!=null&&!"".equals(delete)){
				classDetailMap1 = (Map<String, Map<String, String>>) JSONUtils.readAsMap(delete);
			}
			applicationEmployeeClassService.moveEmployeeClass(request,classdetailId, user, classDetailMap,classDetailMap1);
			RedisUtils.delete("moveClasssSet_"+classMonth+"_"+String.valueOf(user.getEmployeeId()));
			RedisUtils.delete("delete_moveClasssSet_"+classMonth+"_"+String.valueOf(user.getEmployeeId()));
			map.put("message", "已提交，审核通过后生效!");
			map.put("success", true);
			return map;
		}catch(Exception e){
			logger.error("调班提交，原因={}",e);
			map.put("repeatFlag", false);
			map.put("message",e.getMessage());
			map.put("success",false);
			return map;
		}
	}
	
	//删除排班（日常）
	@RequestMapping("/delEmployeeClass.htm")
	@ResponseBody
	@Token(remove = true)
	public Map<String,Object> delEmployeeClass(HttpServletRequest request,String token,Long id){
		Map<String,Object> map = new HashMap<String, Object>();
		User user = userService.getCurrentUser();
		logger.info("员工delEmployeeClass,employeeId="+user.getEmployee().getId()+";id="+id);
		try{
			applicationEmployeeClassService.deleteEmployeeClass(id, user);
			map.put("message", "删除成功!");
			map.put("success", true);
			return map;
		}catch(Exception e){
			logger.error("delEmployeeClass，原因={}",e);
			map.put("repeatFlag", false);
			map.put("message",e.getMessage());
			map.put("success",false);
			return map;
		}
	}
	
	//缓存排班数据
	@RequestMapping("/cacheEmployeeClassDetail.htm")
	@ResponseBody
	public void cacheEmployeeClassDetail(HttpServletRequest request,String employeeId,String classDate,String classSetId,String classMonth){
		User user = userService.getCurrentUser();
		try{
		    //增加 新增/编辑的缓存
		    String addOrUpdate = RedisUtils.getString("classsSet_"+classMonth+"_"+String.valueOf(user.getEmployeeId()));
		    String delete = RedisUtils.getString("delete_classsSet_"+classMonth+"_"+String.valueOf(user.getEmployeeId()));
		    if(addOrUpdate!=null&&!"".equals(addOrUpdate)){
		    	Map<String,Map<String,String>> map = (Map<String, Map<String, String>>) JSONUtils.readAsMap(addOrUpdate);
		    	if(map.containsKey(employeeId)){
		    		Map<String,String> employeeMap = map.get(employeeId);
		    		employeeMap.put(classDate, classSetId);
		    		RedisUtils.setString("classsSet_"+classMonth+"_"+String.valueOf(user.getEmployeeId()),JSONUtils.write(map), 1800000);
		    	}else{
		    		Map<String,String> employeeMap = new HashMap<String,String>();
			    	employeeMap.put(classDate, classSetId);
			    	map.put(employeeId, employeeMap);
			    	RedisUtils.setString("classsSet_"+classMonth+"_"+String.valueOf(user.getEmployeeId()),JSONUtils.write(map), 1800000);
		    	}
		    }else{
		    	Map<String,Map<String,String>> map = new HashMap<String, Map<String,String>>();
		    	Map<String,String> employeeMap = new HashMap<String,String>();
		    	employeeMap.put(classDate, classSetId);
		    	map.put(employeeId, employeeMap);
		    	RedisUtils.setString("classsSet_"+classMonth+"_"+String.valueOf(user.getEmployeeId()),JSONUtils.write(map),1800000);
		    }
		    //清除待删缓存
		    if(delete!=null&&!"".equals(delete)){
		    	Map<String,Map<String,String>> map = (Map<String, Map<String, String>>) JSONUtils.readAsMap(delete);
		    	if(map.containsKey(employeeId)){
		    		Map<String,String> employeeMap = map.get(employeeId);
		    		if(employeeMap.containsKey(classDate)){
		    			employeeMap.remove(classDate);
		    			RedisUtils.setString("delete_classsSet_"+classMonth+"_"+String.valueOf(user.getEmployeeId()),JSONUtils.write(map), 1800000);
		    		}
		    	}
		    }
		}catch(Exception e){
			logger.error(user.getEmployee().getCnName()+"cacheEmployeeClassDetail"+e.getMessage());
		}
	}
	
	//保存、编辑排班数据
	@RequestMapping("/saveClassDetail.htm")
	@ResponseBody
	@Token(remove = true)
	public Map<String,Object> saveClassDetail(HttpServletRequest request,String token,Long classdetailId,String classMonth){
		Map<String,Object> map = new HashMap<String, Object>();
		User user = userService.getCurrentUser();
		try{
			String addOrUpdate = RedisUtils.getString("classsSet_"+classMonth+"_"+String.valueOf(user.getEmployeeId()));
			String delete = RedisUtils.getString("delete_classsSet_"+classMonth+"_"+String.valueOf(user.getEmployeeId()));
			if((addOrUpdate==null||"".equals(addOrUpdate))&&(delete==null||"".equals(delete))){
				throw new OaException("请选择员工排班班次！");
			}
			Map<String,Map<String,String>> classDetailMap = new HashMap<String,Map<String,String>>();
			if(addOrUpdate!=null&&!"".equals(addOrUpdate)){
				classDetailMap = (Map<String, Map<String, String>>) JSONUtils.readAsMap(addOrUpdate);
			}
			Map<String,Map<String,String>> delete_classDetailMap = new HashMap<String,Map<String,String>>();
			if(delete!=null&&!"".equals(delete)){
				delete_classDetailMap = (Map<String, Map<String, String>>) JSONUtils.readAsMap(delete);
			}
			applicationEmployeeClassService.save(classdetailId,classMonth,classDetailMap,delete_classDetailMap,user);
			RedisUtils.delete("classsSet_"+classMonth+"_"+String.valueOf(user.getEmployeeId()));
			RedisUtils.delete("delete_classsSet_"+classMonth+"_"+String.valueOf(user.getEmployeeId()));
			map.put("message", "新增成功!");
			map.put("success", true);
			return map;
		}catch(Exception e){
			logger.error("保存、编辑排班数据失败，原因={}",e);
			map.put("repeatFlag", false);
			map.put("message",e.getMessage());
			map.put("success",false);
			return map;
		}
	}
	
	//排班数据导出
	@ResponseBody
	@RequestMapping("/exportEmpClassReprotById.htm")
	@Token(remove = true)
	public Map<String, Object> exportEmpClassReprotById(HttpServletRequest request,ApplicationEmployeeClass employeeClass){
		User user = userService.getCurrentUser();
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			applicationEmployeeClassService.exportEmpClassReprotById(employeeClass.getId());
			map.put("success", true);
			map.put("message", "导出成功，请稍后查看邮箱！");
		}catch(Exception e){
			if(e.getCause() instanceof MailSendException || e.getCause() instanceof MessagingException || e.getCause() instanceof SocketException){
				map.put("message", "邮件系统繁忙，请重试");
			}else{
				map.put("message", e.getMessage());
			}
			logger.error(user.getEmployee().getCnName()+"排班数据导出："+e.toString());
			map.put("repeatFlag", false);
			map.put("success",false);
			return map;
		}
		return map;
   }
	
	//值班数据导出
	@ResponseBody
	@RequestMapping("/exportEmpDutyReprotById.htm")
	@Token(remove = true)
	public Map<String, Object> exportEmpDutyReprotById(HttpServletRequest request,Long departId,String vacationName,String year){
		User user = userService.getCurrentUser();
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			applicationEmployeeDutyService.exportEmpDutyReprotById(departId,vacationName,year,user);
			map.put("success", true);
			map.put("message", "导出成功，请稍后查看邮箱！");
		}catch(Exception e){
			if(e.getCause() instanceof MailSendException || e.getCause() instanceof MessagingException || e.getCause() instanceof SocketException){
				map.put("message", "邮件系统繁忙，请重试");
			}else{
				map.put("message", e.getMessage());
			}
			logger.error(user.getEmployee().getCnName()+"值班数据导出："+e.toString());
			map.put("repeatFlag", false);
			map.put("success",false);
			return map;
		}
		return map;
   }
	

	/**
	 * 编辑排班（值班）页面
	 * @return
	 */
	@RequestMapping("/updateVacation.htm")
	@Token(generate=true)//生成token
	public String updateVacation(HttpServletRequest request,Long dutyId){
		ApplicationEmployeeDuty duty = applicationEmployeeDutyService.getById(dutyId);
		if(duty!=null){
			request.setAttribute("duty", duty);
			//查询节假日的法定天数
			AnnualVacation vacation = new AnnualVacation();
			List<Integer> typeList = new ArrayList<Integer>();
			typeList.add(AnnualVacation.YYPE_LEGAL);
			typeList.add(AnnualVacation.YYPE_VACATION);
			vacation.setTypeList(typeList);
			vacation.setStartDate(DateUtils.parse(duty.getYear()+"-01-01", DateUtils.FORMAT_SHORT));
			vacation.setEndDate(DateUtils.parse(duty.getYear()+"-12-31", DateUtils.FORMAT_SHORT));
			vacation.setSubject(duty.getVacationName());
			Map<Date, List<ApplicationEmployeeDutyDetail>> dutyDetailMap = new LinkedHashMap<Date, List<ApplicationEmployeeDutyDetail>>();  
			List<AnnualVacation> list = annualVacationService.getListByCondition(vacation);
			List<AnnualVacation> vacationList = new ArrayList<AnnualVacation>();
			if(list!=null&&list.size()>0){
				//获取节假日前连续非工作日
				List<Date> startList = new ArrayList<Date>();
				Date startDate = DateUtils.addDay(list.get(0).getAnnualDate(), -1);
				while(true){
					if(annualVacationService.judgeWorkOrNot(startDate)){
						break;
					}
					startList.add(startDate);
					startDate = DateUtils.addDay(startDate, -1);
				}
				//获取节假日后连续非工作日
				List<Date> endList = new ArrayList<Date>();
				Date endDate = DateUtils.addDay(list.get(list.size()-1).getAnnualDate(),1);
				while(true){
					if(annualVacationService.judgeWorkOrNot(endDate)){
						break;
					}
					endList.add(endDate);
					endDate = DateUtils.addDay(endDate, 1);
				}
				if(startList!=null&&startList.size()>0){
					for(int i=startList.size()-1;i>=0;i--){
						AnnualVacation va = new AnnualVacation();
						va.setAnnualDate(startList.get(i));
						vacationList.add(va);
					}
				}
				for(AnnualVacation va:list){
					vacationList.add(va);
				}
				if(endList!=null&&endList.size()>0){
					for(Date date:endList){
						AnnualVacation va = new AnnualVacation();
						va.setAnnualDate(date);
						vacationList.add(va);
					}
				}
				request.setAttribute("vacationList", vacationList);
			}
			for(AnnualVacation va:vacationList){
				List<ApplicationEmployeeDutyDetail> dutyList = new ArrayList<ApplicationEmployeeDutyDetail>();
				dutyDetailMap.put(va.getAnnualDate(), dutyList);
			}
			//查询员工的部门职位信息
			List<Long> ids = new ArrayList<Long>();
			if(duty.getEmployeeIds()!=null){
				for(String id:duty.getEmployeeIds().split(",")){
					ids.add(Long.valueOf(id));
				}
			}
			String names = "";
			Map<String,String> nameMap = new HashMap<String,String>();
			if(ids!=null&&ids.size()>0){
				List<Employee> employeeList = employeeService.getByIds(ids);
				for(Employee emp:employeeList){
					nameMap.put(String.valueOf(emp.getId()), emp.getCnName());
					names += emp.getCnName()+" ";
				}
				duty.setEmployeeNames(names);
			}
			//查询值班明细数据
			ApplicationEmployeeDutyDetail dutyDetail = new ApplicationEmployeeDutyDetail();
			dutyDetail.setAttnApplicationEmployDutyId(dutyId);
			List<ApplicationEmployeeDutyDetail> dutyDetailList = applicationEmployeeDutyService.selectByCondition(dutyDetail);
			for(ApplicationEmployeeDutyDetail param:dutyDetailList){
				String employeeNames = "";
				for(String id:param.getEmployeeIds().split(",")){
					employeeNames +=nameMap.get(id)+" ";
				}
				employeeNames = employeeNames.substring(0,employeeNames.length()-1);
				param.setEmployeeNames(employeeNames);
				dutyDetailMap.get(param.getVacationDate()).add(param);
			}
			request.setAttribute("dutyDetailMap", dutyDetailMap);
		}
		return "employeeClass/update_vacation";
	}

	
	/**
	 * 新增值班（节假日）页面
	 * @return
	 */
	@RequestMapping("/toAddVacation.htm")
	@Token(generate=true)//生成token
	public String toAddVacation(HttpServletRequest request){
		try {
			User user = userService.getCurrentUser();
			request.setAttribute("classSetPerson", user.getEmployee().getCnName());
		} catch (Exception e) {
			return "employeeClass/add_vacation";
		}
		return "employeeClass/add_vacation";
	}
	
	/**
	 * 审核值班页面
	 * @return
	 */
	@RequestMapping("/approveVacation.htm")
	@Token(generate=true)//生成token
	public String approveVacation(HttpServletRequest request,String flag,Long dutyId,String urlType){
		try {
			User user = userService.getCurrentUser();
			request.setAttribute("returnUrl", ReturnUrlUtil.getReturnUrl(urlType));
			ApplicationEmployeeDuty duty = applicationEmployeeDutyService.getById(dutyId);
			if(duty!=null){
				request.setAttribute("duty", duty);
				//查询节假日的法定天数
				AnnualVacation vacation = new AnnualVacation();
				List<Integer> typeList = new ArrayList<Integer>();
				typeList.add(AnnualVacation.YYPE_LEGAL);
				typeList.add(AnnualVacation.YYPE_VACATION);
				vacation.setTypeList(typeList);
				vacation.setStartDate(DateUtils.parse(duty.getYear()+"-01-01", DateUtils.FORMAT_SHORT));
				vacation.setEndDate(DateUtils.parse(duty.getYear()+"-12-31", DateUtils.FORMAT_SHORT));
				vacation.setSubject(duty.getVacationName());
				List<AnnualVacation> list = annualVacationService.getListByCondition(vacation);
				List<AnnualVacation> vacationList = new ArrayList<AnnualVacation>();
				if(list!=null&&list.size()>0){
					//获取节假日前连续非工作日
					List<Date> startList = new ArrayList<Date>();
					Date startDate = DateUtils.addDay(list.get(0).getAnnualDate(), -1);
					while(true){
						if(annualVacationService.judgeWorkOrNot(startDate)){
							break;
						}
						startList.add(startDate);
						startDate = DateUtils.addDay(startDate, -1);
					}
					//获取节假日后连续非工作日
					List<Date> endList = new ArrayList<Date>();
					Date endDate = DateUtils.addDay(list.get(list.size()-1).getAnnualDate(),1);
					while(true){
						if(annualVacationService.judgeWorkOrNot(endDate)){
							break;
						}
						endList.add(endDate);
						endDate = DateUtils.addDay(endDate, 1);
					}
					if(startList!=null&&startList.size()>0){
						for(int i=startList.size()-1;i>=0;i--){
							AnnualVacation va = new AnnualVacation();
							va.setAnnualDate(startList.get(i));
							vacationList.add(va);
						}
					}
					for(AnnualVacation va:list){
						vacationList.add(va);
					}
					if(endList!=null&&endList.size()>0){
						for(Date date:endList){
							AnnualVacation va = new AnnualVacation();
							va.setAnnualDate(date);
							vacationList.add(va);
						}
					}
					request.setAttribute("vacationList", vacationList);
				}
				//查询员工的部门职位信息
				List<Long> ids = new ArrayList<Long>();
				if(duty.getEmployeeIds()!=null){
					for(String id:duty.getEmployeeIds().split(",")){
						ids.add(Long.valueOf(id));
					}
				}
				List<Employee> employeeList = employeeService.getByIds(ids);
				Map<String,String> nameMap = new HashMap<String,String>();
				for(Employee emp:employeeList){
					nameMap.put(String.valueOf(emp.getId()), emp.getCnName());
				}
				//查询值班明细数据
				ApplicationEmployeeDutyDetail dutyDetail = new ApplicationEmployeeDutyDetail();
				dutyDetail.setAttnApplicationEmployDutyId(dutyId);
				List<ApplicationEmployeeDutyDetail> dutyDetailList = applicationEmployeeDutyService.selectByCondition(dutyDetail);
				for(ApplicationEmployeeDutyDetail param:dutyDetailList){
					String employeeNames = "";
					for(String id:param.getEmployeeIds().split(",")){
						employeeNames +=nameMap.get(id)+" ";
					}
					employeeNames = employeeNames.substring(0,employeeNames.length()-1);
					param.setEmployeeNames(employeeNames);
				}
				request.setAttribute("dutyDetailList", dutyDetailList);
				
				Task task = activitiServiceImpl.queryTaskByProcessInstanceId(duty.getProcessInstanceId());
				TaskVO taskVO = new TaskVO();
				if(task!=null){
					taskVO.setActName(task.getName());
				}
				taskVO.setProcessId(duty.getProcessInstanceId());
				request.setAttribute("taskVO", taskVO);
				request.setAttribute("isSelf", false);
				//审批显示
 				if(duty.getApprovalStatus().intValue()==100) {
 					if(duty.getEmployeeId().longValue()==user.getEmployeeId().longValue()){
 						request.setAttribute("isSelf", true);
 					}
 					if(StringUtils.equalsIgnoreCase("can", flag)) {
 						request.setAttribute("canApprove", true);
 					}
 				}
				if(duty.getEmployeeId().longValue()==user.getEmployeeId().longValue()){
					request.setAttribute("isSelf", true);
				}
			}
		} catch (Exception e) {
			return "employeeClass/approve_vacation";
		}
		return "employeeClass/approve_vacation";
	}
	
	/**
	 * 审核值班查看页面
	 * @return
	 */
	@RequestMapping("/detailVacation.htm")
	public String detailVacation(HttpServletRequest request,Long dutyId){
		try {
			ApplicationEmployeeDuty duty = applicationEmployeeDutyService.getById(dutyId);
			if(duty!=null){
				request.setAttribute("duty", duty);
				//查询节假日的法定天数
				AnnualVacation vacation = new AnnualVacation();
				List<Integer> typeList = new ArrayList<Integer>();
				typeList.add(AnnualVacation.YYPE_LEGAL);
				typeList.add(AnnualVacation.YYPE_VACATION);
				vacation.setTypeList(typeList);
				vacation.setStartDate(DateUtils.parse(duty.getYear()+"-01-01", DateUtils.FORMAT_SHORT));
				vacation.setEndDate(DateUtils.parse(duty.getYear()+"-12-31", DateUtils.FORMAT_SHORT));
				vacation.setSubject(duty.getVacationName());
				List<AnnualVacation> list = annualVacationService.getListByCondition(vacation);
				List<AnnualVacation> vacationList = new ArrayList<AnnualVacation>();
				if(list!=null&&list.size()>0){
					//获取节假日前连续非工作日
					List<Date> startList = new ArrayList<Date>();
					Date startDate = DateUtils.addDay(list.get(0).getAnnualDate(), -1);
					while(true){
						if(annualVacationService.judgeWorkOrNot(startDate)){
							break;
						}
						startList.add(startDate);
						startDate = DateUtils.addDay(startDate, -1);
					}
					//获取节假日后连续非工作日
					List<Date> endList = new ArrayList<Date>();
					Date endDate = DateUtils.addDay(list.get(list.size()-1).getAnnualDate(),1);
					while(true){
						if(annualVacationService.judgeWorkOrNot(endDate)){
							break;
						}
						endList.add(endDate);
						endDate = DateUtils.addDay(endDate, 1);
					}
					if(startList!=null&&startList.size()>0){
						for(int i=startList.size()-1;i>=0;i--){
							AnnualVacation va = new AnnualVacation();
							va.setAnnualDate(startList.get(i));
							vacationList.add(va);
						}
					}
					for(AnnualVacation va:list){
						vacationList.add(va);
					}
					if(endList!=null&&endList.size()>0){
						for(Date date:endList){
							AnnualVacation va = new AnnualVacation();
							va.setAnnualDate(date);
							vacationList.add(va);
						}
					}
					request.setAttribute("vacationList", vacationList);
				}
				//查询员工的部门职位信息
				List<Long> ids = new ArrayList<Long>();
				if(duty.getEmployeeIds()!=null){
					for(String id:duty.getEmployeeIds().split(",")){
						ids.add(Long.valueOf(id));
					}
				}
				List<Employee> employeeList = new ArrayList<Employee>();
				if(ids!=null&&ids.size()>0){
				   employeeList = employeeService.getByIds(ids);
				}
				Map<String,String> nameMap = new HashMap<String,String>();
				for(Employee emp:employeeList){
					nameMap.put(String.valueOf(emp.getId()), emp.getCnName());
				}
				
				//查询值班数据
				EmployeeDuty employeeDutyP = new EmployeeDuty();
				employeeDutyP.setDepartId(duty.getDepartId());
				employeeDutyP.setYear(duty.getYear());
				employeeDutyP.setVacationName(duty.getVacationName());
				List<EmployeeDuty> employeeDutyList = employeeDutyService.selectByCondition(employeeDutyP);
				//组装值班申请明细数据
				Map<String,List<EmployeeDuty>> item = new HashMap<String,List<EmployeeDuty>>();
				for(EmployeeDuty employeeDuty:employeeDutyList){
					String key = DateUtils.format(employeeDuty.getDutyDate(), DateUtils.FORMAT_SHORT)+"_"
				               + DateUtils.format(employeeDuty.getStartTime(), "HH:mm")+"_"
				               + DateUtils.format(employeeDuty.getEndTime(), "HH:mm")+"_"+ employeeDuty.getDutyItem();
					if(item!=null&&item.containsKey(key)){
						List<EmployeeDuty> detail = item.get(key);
						detail.add(employeeDuty);
					}else{
						List<EmployeeDuty> detail = new ArrayList<EmployeeDuty>();
						detail.add(employeeDuty);
						item.put(key, detail);
					}
				}
				List<ApplicationEmployeeDutyDetail> dutyDetailList = new ArrayList<ApplicationEmployeeDutyDetail>();
				for (Map.Entry<String, List<EmployeeDuty>> entry : item.entrySet()) {
					List<EmployeeDuty> detail = entry.getValue();
					if(detail!=null&&detail.size()>0){
						ApplicationEmployeeDutyDetail dutyDetail = new ApplicationEmployeeDutyDetail();
						dutyDetail.setVacationDate(detail.get(0).getDutyDate());
						dutyDetail.setStartTime(detail.get(0).getStartTime());
						dutyDetail.setEndTime(detail.get(0).getEndTime());
						dutyDetail.setDutyItem(detail.get(0).getDutyItem());
						dutyDetail.setWorkHours(detail.get(0).getWorkHours());
						String employeeNames = "";
						for(EmployeeDuty employeeName:detail){
							employeeNames += employeeName.getEmployName()+" ";
						}
						dutyDetail.setEmployeeNames(employeeNames);
						dutyDetailList.add(dutyDetail);
					}
				}
				request.setAttribute("dutyDetailList", dutyDetailList);
			}
		} catch (Exception e) {
			return "employeeClass/detail_vacation";
		}
		return "employeeClass/detail_vacation";
	}
	
	/**
	 * 调班值班页面
	 * @return
	 */
	@RequestMapping("/toMoveEmployeeDuty.htm")
	@Token(generate=true)//生成token
	public String toMoveEmployeeDuty(HttpServletRequest request,Long dutyId){
		ApplicationEmployeeDuty duty = applicationEmployeeDutyService.getById(dutyId);
		if(duty!=null){
			request.setAttribute("duty", duty);
			//查询节假日的法定天数
			AnnualVacation vacation = new AnnualVacation();
			List<Integer> typeList = new ArrayList<Integer>();
			typeList.add(AnnualVacation.YYPE_LEGAL);
			typeList.add(AnnualVacation.YYPE_VACATION);
			vacation.setTypeList(typeList);
			vacation.setStartDate(DateUtils.parse(duty.getYear()+"-01-01", DateUtils.FORMAT_SHORT));
			vacation.setEndDate(DateUtils.parse(duty.getYear()+"-12-31", DateUtils.FORMAT_SHORT));
			vacation.setSubject(duty.getVacationName());
			List<AnnualVacation> list = annualVacationService.getListByCondition(vacation);
			List<AnnualVacation> vacationList = new ArrayList<AnnualVacation>();
			Map<Date, List<ApplicationEmployeeDutyDetail>> dutyDetailMap = new LinkedHashMap<Date, List<ApplicationEmployeeDutyDetail>>();
			if(list!=null&&list.size()>0){
				//获取节假日前连续非工作日
				List<Date> startList = new ArrayList<Date>();
				Date startDate = DateUtils.addDay(list.get(0).getAnnualDate(), -1);
				while(true){
					if(annualVacationService.judgeWorkOrNot(startDate)){
						break;
					}
					startList.add(startDate);
					startDate = DateUtils.addDay(startDate, -1);
				}
				//获取节假日后连续非工作日
				List<Date> endList = new ArrayList<Date>();
				Date endDate = DateUtils.addDay(list.get(list.size()-1).getAnnualDate(),1);
				while(true){
					if(annualVacationService.judgeWorkOrNot(endDate)){
						break;
					}
					endList.add(endDate);
					endDate = DateUtils.addDay(endDate, 1);
				}
				if(startList!=null&&startList.size()>0){
					for(int i=startList.size()-1;i>=0;i--){
						AnnualVacation va = new AnnualVacation();
						va.setAnnualDate(startList.get(i));
						vacationList.add(va);
					}
				}
				for(AnnualVacation va:list){
					vacationList.add(va);
				}
				if(endList!=null&&endList.size()>0){
					for(Date date:endList){
						AnnualVacation va = new AnnualVacation();
						va.setAnnualDate(date);
						vacationList.add(va);
					}
				}
				request.setAttribute("vacationList", vacationList);
			}
			for(AnnualVacation va:vacationList){
				List<ApplicationEmployeeDutyDetail> dutyList = new ArrayList<ApplicationEmployeeDutyDetail>();
				dutyDetailMap.put(va.getAnnualDate(), dutyList);
			}
			//查询员工的部门职位信息
			List<Long> ids = new ArrayList<Long>();
			if(duty.getEmployeeIds()!=null){
				for(String id:duty.getEmployeeIds().split(",")){
					ids.add(Long.valueOf(id));
				}
			}
			String names = "";
			Map<String,String> nameMap = new HashMap<String,String>();
			if(ids!=null&&ids.size()>0){
				List<Employee> employeeList = employeeService.getByIds(ids);
				for(Employee emp:employeeList){
					nameMap.put(String.valueOf(emp.getId()), emp.getCnName());
					names = emp.getCnName()+" ";
				}
				duty.setEmployeeNames(names);
			}
			//查询值班明细数据
			ApplicationEmployeeDutyDetail dutyDetail = new ApplicationEmployeeDutyDetail();
			dutyDetail.setAttnApplicationEmployDutyId(dutyId);
			List<ApplicationEmployeeDutyDetail> dutyDetailList = applicationEmployeeDutyService.selectByCondition(dutyDetail);
			for(ApplicationEmployeeDutyDetail param:dutyDetailList){
				String employeeNames = "";
				for(String id:param.getEmployeeIds().split(",")){
					employeeNames +=nameMap.get(id)+" ";
				}
				employeeNames = employeeNames.substring(0,employeeNames.length()-1);
				param.setEmployeeNames(employeeNames);
				dutyDetailMap.get(param.getVacationDate()).add(param);
			}
			request.setAttribute("dutyDetailMap", dutyDetailMap);
		}
		return "employeeClass/move_vacation";
	}
	
	/**
	 * hr调班值班页面
	 * @return
	 */
	@RequestMapping("/toHrMoveEmployeeDuty.htm")
	@Token(generate=true)//生成token
	public String toHrMoveEmployeeDuty(HttpServletRequest request,Long departId,String vacationName,String year){
		try {
			request.setAttribute("departName", "");
			request.setAttribute("vacationName",vacationName);
			Depart depart = departService.getById(departId);
			if(depart!=null){
				request.setAttribute("departName",depart.getName());
			}
			//查询节假日的法定天数
			AnnualVacation vacation = new AnnualVacation();
			List<Integer> typeList = new ArrayList<Integer>();
			typeList.add(AnnualVacation.YYPE_LEGAL);
			typeList.add(AnnualVacation.YYPE_VACATION);
			vacation.setTypeList(typeList);
			vacation.setStartDate(DateUtils.parse(year +"-01-01", DateUtils.FORMAT_SHORT));
			vacation.setEndDate(DateUtils.parse(year +"-12-31", DateUtils.FORMAT_SHORT));
			vacation.setSubject(vacationName);
			List<AnnualVacation> list = annualVacationService.getListByCondition(vacation);
			List<AnnualVacation> vacationList = new ArrayList<AnnualVacation>();
			if(list!=null&&list.size()>0){
				//获取节假日前连续非工作日
				List<Date> startList = new ArrayList<Date>();
				Date startDate = DateUtils.addDay(list.get(0).getAnnualDate(), -1);
				while(true){
					if(annualVacationService.judgeWorkOrNot(startDate)){
						break;
					}
					startList.add(startDate);
					startDate = DateUtils.addDay(startDate, -1);
				}
				//获取节假日后连续非工作日
				List<Date> endList = new ArrayList<Date>();
				Date endDate = DateUtils.addDay(list.get(list.size()-1).getAnnualDate(),1);
				while(true){
					if(annualVacationService.judgeWorkOrNot(endDate)){
						break;
					}
					endList.add(endDate);
					endDate = DateUtils.addDay(endDate, 1);
				}
				if(startList!=null&&startList.size()>0){
					for(int i=startList.size()-1;i>=0;i--){
						AnnualVacation va = new AnnualVacation();
						va.setAnnualDate(startList.get(i));
						vacationList.add(va);
					}
				}
				for(AnnualVacation va:list){
					vacationList.add(va);
				}
				if(endList!=null&&endList.size()>0){
					for(Date date:endList){
						AnnualVacation va = new AnnualVacation();
						va.setAnnualDate(date);
						vacationList.add(va);
					}
				}
				request.setAttribute("vacationList", vacationList);
			}

			//查询值班数据
			EmployeeDuty employeeDutyP = new EmployeeDuty();
			employeeDutyP.setDepartId(departId);
			employeeDutyP.setYear(year);
			employeeDutyP.setVacationName(vacationName);
			List<EmployeeDuty> employeeDutyList = employeeDutyService.selectByCondition(employeeDutyP);
			//组装值班申请明细数据
			Map<String,List<EmployeeDuty>> item = new HashMap<String,List<EmployeeDuty>>();
			for(EmployeeDuty employeeDuty:employeeDutyList){
				String key = DateUtils.format(employeeDuty.getDutyDate(), DateUtils.FORMAT_SHORT)+"_"
			               + DateUtils.format(employeeDuty.getStartTime(), "HH:mm")+"_"
			               + DateUtils.format(employeeDuty.getEndTime(), "HH:mm")+"_"+ employeeDuty.getDutyItem();
				if(item!=null&&item.containsKey(key)){
					List<EmployeeDuty> detail = item.get(key);
					detail.add(employeeDuty);
				}else{
					List<EmployeeDuty> detail = new ArrayList<EmployeeDuty>();
					detail.add(employeeDuty);
					item.put(key, detail);
				}
			}
			List<ApplicationEmployeeDutyDetail> dutyDetailList = new ArrayList<ApplicationEmployeeDutyDetail>();
			for (Map.Entry<String, List<EmployeeDuty>> entry : item.entrySet()) {
				List<EmployeeDuty> detail = entry.getValue();
				if(detail!=null&&detail.size()>0){
					ApplicationEmployeeDutyDetail dutyDetail = new ApplicationEmployeeDutyDetail();
					dutyDetail.setVacationDate(detail.get(0).getDutyDate());
					dutyDetail.setStartTime(detail.get(0).getStartTime());
					dutyDetail.setEndTime(detail.get(0).getEndTime());
					dutyDetail.setDutyItem(detail.get(0).getDutyItem());
					dutyDetail.setWorkHours(detail.get(0).getWorkHours());
					String employeeNames = "";
					for(EmployeeDuty employeeName:detail){
						employeeNames += employeeName.getEmployName()+" ";
					}
					dutyDetail.setEmployeeNames(employeeNames);
					dutyDetailList.add(dutyDetail);
				}
			}
			request.setAttribute("dutyDetailList", dutyDetailList);
		} catch (Exception e) {
			return "employeeClass/hr_move_vacation";
		}
		return "employeeClass/hr_move_vacation";
	}
	
	//新增值班
	@RequestMapping("/addClassDuty.htm")
	@ResponseBody
	@Token(remove = true)
	public Map<String,Object> addClassDuty(HttpServletRequest request,String token,ApplicationEmployeeDuty duty){
		Map<String,Object> map = new HashMap<String, Object>();
		User user = userService.getCurrentUser();
		try{
			applicationEmployeeDutyService.addEmployeeDuty(user, duty);
			map.put("message", "新增成功!");
			map.put("success", true);
			return map;
		}catch(Exception e){
			logger.error("新增值班申请数据失败，原因={}",e);
			map.put("repeatFlag", false);
			map.put("message",e.getMessage());
			map.put("success",false);
			return map;
		}
	}
	
	//保存编辑值班事项
	@RequestMapping("/saveOrUpdateDutyDetail.htm")
	@ResponseBody
	@Token(remove = true)
	public Map<String,Object> saveOrUpdateDutyDetail(HttpServletRequest request,String token,Long id,String dutyDetail){
		Map<String,Object> map = new HashMap<String, Object>();
		User user = userService.getCurrentUser();
		logger.info("saveOrUpdateDutyDetail:dutyDetail="+dutyDetail);
		try{
			List<ApplicationEmployeeDutyDetail> list = new ArrayList<ApplicationEmployeeDutyDetail>();
			JSONObject obiect = JSONObject.fromObject(dutyDetail);
			if(obiect==null||obiect.size()<=0){
				throw new OaException("数据不能为空！");
			}
		    Iterator it = obiect.keys();  
            while (it.hasNext()) {  
                String key = (String) it.next();  
                JSONArray array = obiect.getJSONArray(key);  
				for(int i=0;i<array.size();i++){
					ApplicationEmployeeDutyDetail duty = new ApplicationEmployeeDutyDetail();
					JSONObject jsonObject = JSONObject.fromObject(array.get(i), new JsonConfig());
					duty.setId(null);
					if(jsonObject.containsKey("id")){
						if(jsonObject.getString("id")!=null&&!"".equals(jsonObject.getString("id"))){
							duty.setId(Long.valueOf(jsonObject.getString("id")));
						}
					}
					duty.setEmployeeIds(jsonObject.getString("employeeIds"));
					duty.setWorkHours(jsonObject.getDouble("workHours"));
					duty.setDutyItem(jsonObject.getString("dutyItem"));
					duty.setStartTime(DateUtils.parse(key+ " "+jsonObject.getString("startTime")+":00", DateUtils.FORMAT_LONG));
					duty.setEndTime(DateUtils.parse(key+ " "+jsonObject.getString("endTime")+":00", DateUtils.FORMAT_LONG));
					duty.setVacationDate(DateUtils.parse(key, DateUtils.FORMAT_SHORT));
					list.add(duty);
				}
            }  
            applicationEmployeeDutyService.updateEmployeeDuty(user, id, list);
			map.put("message", "新增成功!");
			map.put("success", true);
			return map;
		}catch(Exception e){
			logger.error("新增值班申请数据失败，原因={}",e);
			map.put("repeatFlag", false);
			map.put("message",e.getMessage());
			map.put("success",false);
			return map;
		}
	}
	
	//值班调班保存
	@RequestMapping("/moveEmployeeDuty.htm")
	@ResponseBody
	@Token(remove = true)
	public Map<String,Object> moveEmployeeDuty(HttpServletRequest request,String token,Long id,String dutyDetail){
		Map<String,Object> map = new HashMap<String, Object>();
		User user = userService.getCurrentUser();
		try{
			List<ApplicationEmployeeDutyDetail> list = new ArrayList<ApplicationEmployeeDutyDetail>();
			JSONObject obiect = JSONObject.fromObject(dutyDetail);
			if(obiect==null||obiect.size()<=0){
				throw new OaException("数据不能为空！");
			}
		    Iterator it = obiect.keys();  
            while (it.hasNext()) {  
                String key = (String) it.next();  
                String value = obiect.getString(key);  
                JSONArray array = obiect.getJSONArray(key);  
				for(int i=0;i<array.size();i++){
					ApplicationEmployeeDutyDetail duty = new ApplicationEmployeeDutyDetail();
					JSONObject jsonObject = JSONObject.fromObject(array.get(i), new JsonConfig());
					duty.setId((!jsonObject.containsKey("id")||jsonObject.getString("id")==null||"".equals(jsonObject.getString("id")))?null:Long.valueOf(jsonObject.getString("id")));
					duty.setEmployeeIds(jsonObject.getString("employeeIds"));
					duty.setWorkHours(jsonObject.getDouble("workHours"));
					duty.setDutyItem(jsonObject.getString("dutyItem"));
					duty.setStartTime(DateUtils.parse(key+ " "+jsonObject.getString("startTime")+":00", DateUtils.FORMAT_LONG));
					duty.setEndTime(DateUtils.parse(key+ " "+jsonObject.getString("endTime")+":00", DateUtils.FORMAT_LONG));
					duty.setVacationDate(DateUtils.parse(key, DateUtils.FORMAT_SHORT));
					list.add(duty);
				}
            }  
            applicationEmployeeDutyService.moveEmployeeDuty(user, id, list);
			map.put("message", "新增成功!");
			map.put("success", true);
			return map;
		}catch(Exception e){
			logger.error("新增值班申请数据失败，原因={}",e);
			map.put("repeatFlag", false);
			map.put("message",e.getMessage());
			map.put("success",false);
			return map;
		}
	}
	
	//提交审核
	@RequestMapping("/submitEmployeeDuty.htm")
	@ResponseBody
	@Token(remove = true)
	public Map<String,Object> submitEmployeeDuty(HttpServletRequest request,String token,Long id){
		Map<String,Object> map = new HashMap<String, Object>();
		User user = userService.getCurrentUser();
		try{
			map = applicationEmployeeDutyService.submitApprove(id, user);
			return map;
		}catch(Exception e){
			map.put("repeatFlag", false);
			map.put("message",e.getMessage());
			map.put("success",false);
			return map;
		}
	}
	
	//通过
	@ResponseBody
	@RequestMapping("/passVacation.htm")
	@Token(remove = true)
	public Map<String, Object> passVacation(HttpServletRequest request,String processInstanceId,String token,String approvalReason){
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			userTaskService.completeTask(request,processInstanceId, approvalReason, ConfigConstants.PASS);
			map.put("success",true);
		}catch(Exception e){
			map.put("repeatFlag", false);
			map.put("message",e.getMessage());
			map.put("success",false);
			return map;
		}
		return map;
	}
	
	//拒绝
	@ResponseBody
	@RequestMapping("/refuseVacation.htm")
	@Token(remove = true)
	public Map<String, Object> refuseVacation(HttpServletRequest request,String processInstanceId,String token,String approvalReason){
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			userTaskService.completeTask(request,processInstanceId, approvalReason, ConfigConstants.REFUSE);
			map.put("success",true);
		}catch(Exception e){
			map.put("repeatFlag", false);
			map.put("message",e.getMessage());
			map.put("success",false);
			return map;
		}
		return map;
	}
	
    //查询部门所有值班数据
	@RequestMapping("/getEmployeeDutyByDepart.htm")
	@ResponseBody
	public List<ApplicationEmployeeDuty> getEmployeeDutyByDepart(){
		User user = userService.getCurrentUser();
		List<ApplicationEmployeeDuty> list = new ArrayList<ApplicationEmployeeDuty>();
		//查询用户所负责的所有部门
		Depart model = new Depart();
		model.setLeader(user.getEmployee().getId());
		List<Depart> dapartList = departService.getListByLeaderOrPower(model);
		if(dapartList!=null&&dapartList.size()>0){
			List<Long> departIds = new ArrayList<Long>();
			for(Depart deaprt:dapartList){
				departIds.add(deaprt.getId());
			}
			ApplicationEmployeeDuty duty = new ApplicationEmployeeDuty();
			duty.setDepartIds(departIds);
			duty.setVersion(0L);
			list = applicationEmployeeDutyService.getByCondition(duty);
		}
		return list;
	}
	
	//人事查询值班数据
	@RequestMapping("/getEmployeeDutyByDepartAndVacation.htm")
	@ResponseBody
	public List<EmployeeDuty> getEmployeeDutyByDepartAndVacation(String departId,String vacationName,String year){
		List<EmployeeDuty> list = new ArrayList<EmployeeDuty>();
		try {
			Long depart = null;
			if(!departId.equals("1")){
				depart = Long.valueOf(departId);
			}
			list = applicationEmployeeDutyService.querySingleDutyByCondition(vacationName,year,depart);
		} catch (Exception e) {
			
		}
		return list;
	}
	
	//删除值班
	@RequestMapping("/delEmployeeDuty.htm")
	@ResponseBody
	@Token(remove = true)
	public Map<String,Object> delEmployeeDuty(HttpServletRequest request,String token,Long id){
		Map<String,Object> map = new HashMap<String, Object>();
		User user = userService.getCurrentUser();
		logger.info("员工delEmployeeDuty,employeeId="+user.getEmployee().getId()+";id="+id);
		try{
			applicationEmployeeDutyService.deleteEmployeeDuty(id, user);
			map.put("message", "删除成功!");
			map.put("success", true);
			return map;
		}catch(Exception e){
			map.put("repeatFlag", false);
			map.put("message",e.getMessage());
			map.put("success",false);
			return map;
		}
	}
	
	//值班审批流程页面
	@RequestMapping("/processVacation.htm")
	public String processVacation(HttpServletRequest request, Long employeeDutyId,Long ruProcdefId){
		ApplicationEmployeeDuty applicationEmployeeDuty = new ApplicationEmployeeDuty();
		applicationEmployeeDuty.setId(employeeDutyId);
		List<ApplicationEmployeeDuty> employeeDutyList = applicationEmployeeDutyService.getByCondition(applicationEmployeeDuty);
		if(employeeDutyList!=null&&employeeDutyList.size()>0){
			request.setAttribute("employeeDuty", employeeDutyList.get(0));
			RunTask param = new RunTask();
			param.setReProcdefCode(RunTask.RUN_CODE_110);
			param.setEntityId(String.valueOf(employeeDutyId));
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
		}
		return "employeeClass/process_vacation";
	}
	
	
	
	
	
	
	
	//人事根据部门和日期查询排班
	@RequestMapping("/getByCondition.htm")
	@ResponseBody
	public List<ApplicationEmployeeClass> getByCondition(Long departId,String startTime,String endTime){
		User user = userService.getCurrentUser();
		ApplicationEmployeeClass applicationEmployeeClass = new ApplicationEmployeeClass();
		applicationEmployeeClass.setDepartId(user.getDepart().getId());
		return applicationEmployeeClassService.getByCondition(applicationEmployeeClass);
	}
	
	//查询部门所有员工数据
	@RequestMapping("/getEmployeeClassSetByMonth.htm")
	@ResponseBody
	public List<ApplicationEmployeeClassDetail> getEmployeeClassSetByMonth(Long attnApplicationEmployClassId,Long employeeId,String type,String classMonth){
		User user = userService.getCurrentUser();
		List<ApplicationEmployeeClassDetail> list = new ArrayList<ApplicationEmployeeClassDetail>();
		try{
			ApplicationEmployeeClass applicationEmployeeClass = new ApplicationEmployeeClass();
			applicationEmployeeClass.setId(attnApplicationEmployClassId);
			List<ApplicationEmployeeClass> list1 =  applicationEmployeeClassService.getByCondition(applicationEmployeeClass);
			if(list1.get(0).getIsMove().intValue()==0){
				if(list1.get(0).getApprovalStatus()==null||list1.get(0).getApprovalStatus().intValue()==100
						||list1.get(0).getApprovalStatus().intValue()==300||list1.get(0).getApprovalStatus().intValue()==400){
					ApplicationEmployeeClassDetail classDetail = new ApplicationEmployeeClassDetail();
					classDetail.setAttnApplicationEmployClassId(attnApplicationEmployClassId);
					classDetail.setEmployId(employeeId);
					classDetail.setIsMove(0);
				    list = applicationEmployeeClassDetailService.getEmployeeClassSetByMonth(classDetail);
				}else if(list1.get(0).getApprovalStatus().intValue()==200){
					EmployeeClass employeeClass = new EmployeeClass();
					employeeClass.setEmployId(employeeId);
					employeeClass.setDepartId(list1.get(0).getDepartId());
					employeeClass.setStartTime(list1.get(0).getClassMonth());
					employeeClass.setEndTime(DateUtils.getLastDay(list1.get(0).getClassMonth()));
					List<EmployeeClass> employeeClassList = employeeClassService.selectByCondition(employeeClass);
					for(EmployeeClass eClass:employeeClassList){
						ApplicationEmployeeClassDetail detail = new ApplicationEmployeeClassDetail();
						detail.setEmployName(eClass.getEmployName());
						detail.setEmployId(eClass.getEmployId());
						detail.setShouldTime(eClass.getShouldTime());
						detail.setClassDate(eClass.getClassDate());
						detail.setClassSettingId(eClass.getClassSettingId());
						detail.setIsMove(0);
						detail.setName(eClass.getName());
						detail.setMustAttnTime(eClass.getMustAttnTime());
						detail.setStartTime(eClass.getStartTime());
						detail.setEndTime(eClass.getEndTime());
						detail.setOldClassSettingId(eClass.getClassSettingId());
						detail.setOldName(eClass.getName());
						detail.setOldStartTime(eClass.getStartTime());
						detail.setOldEndTime(eClass.getEndTime());
						list.add(detail);
					}
				}
			}else if(list1.get(0).getIsMove().intValue()==1){
				//排班的原始数据
				EmployeeClass employeeClass = new EmployeeClass();
				employeeClass.setEmployId(employeeId);
				employeeClass.setDepartId(list1.get(0).getDepartId());
				employeeClass.setStartTime(list1.get(0).getClassMonth());
				employeeClass.setEndTime(DateUtils.getLastDay(list1.get(0).getClassMonth()));
				List<EmployeeClass> employeeClassList = employeeClassService.selectByCondition(employeeClass);
				List<ApplicationEmployeeClassDetail> list3 = new ArrayList<ApplicationEmployeeClassDetail>();
				for(EmployeeClass eClass:employeeClassList){
					ApplicationEmployeeClassDetail detail = new ApplicationEmployeeClassDetail();
					detail.setEmployName(eClass.getEmployName());
					detail.setEmployId(eClass.getEmployId());
					detail.setShouldTime(eClass.getShouldTime());
					detail.setClassDate(eClass.getClassDate());
					detail.setClassSettingId(eClass.getClassSettingId());
					detail.setIsMove(0);
					detail.setName(eClass.getName());
					detail.setMustAttnTime(eClass.getMustAttnTime());
					detail.setStartTime(eClass.getStartTime());
					detail.setEndTime(eClass.getEndTime());
					detail.setOldClassSettingId(eClass.getClassSettingId());
					detail.setOldName(eClass.getName());
					detail.setOldStartTime(eClass.getStartTime());
					detail.setOldEndTime(eClass.getEndTime());
					list3.add(detail);
				}
				Map<Date,ApplicationEmployeeClassDetail> map = new HashMap<Date,ApplicationEmployeeClassDetail>();
				for(ApplicationEmployeeClassDetail detail:list3){
					map.put(detail.getClassDate(), detail);
				}
				//调班原始数据(原排班与现排班数据都有)
				ApplicationEmployeeClassDetail classDetail = new ApplicationEmployeeClassDetail();
				classDetail.setAttnApplicationEmployClassId(attnApplicationEmployClassId);
				classDetail.setEmployId(employeeId);
			    list = applicationEmployeeClassDetailService.getEmployeeClassSetByMonth(classDetail);
			  //  List<ApplicationEmployeeClassDetail> moveAdd = new ArrayList<ApplicationEmployeeClassDetail>();
			    Map<Date,ApplicationEmployeeClassDetail> moveAdd = new HashMap<Date,ApplicationEmployeeClassDetail>();
			    for(ApplicationEmployeeClassDetail move:list){
			    	if(moveAdd.containsKey(move.getClassDate())){
			    		moveAdd.get(move.getClassDate()).setIsMove(1);//设置调班属性，前端显示有用到
			    		//判断是老数据还是新数据
			    		if(move.getIsMove().intValue()==0){//老数据
			    			if(move.getClassSettingId()!=null){
			    				moveAdd.get(move.getClassDate()).setOldClassSettingId(move.getClassSettingId());
			    				moveAdd.get(move.getClassDate()).setOldName(move.getName());
			    				moveAdd.get(move.getClassDate()).setOldStartTime(move.getStartTime());
			    				moveAdd.get(move.getClassDate()).setOldEndTime(move.getEndTime());
			    			}else{
			    				moveAdd.get(move.getClassDate()).setOldStartTime(DateUtils.parse(DateUtils.format(new Date(),DateUtils.FORMAT_SHORT)+" 00:00:00", DateUtils.FORMAT_LONG));
			    				moveAdd.get(move.getClassDate()).setOldEndTime(DateUtils.parse(DateUtils.format(new Date(),DateUtils.FORMAT_SHORT)+" 00:00:00", DateUtils.FORMAT_LONG));
			    				moveAdd.get(move.getClassDate()).setOldName("休");
			    			}
			    		}else{//新数据
			    			if(move.getClassSettingId()!=null){
			    				moveAdd.get(move.getClassDate()).setClassSettingId(move.getClassSettingId());
			    				moveAdd.get(move.getClassDate()).setName(move.getName());
			    				moveAdd.get(move.getClassDate()).setOldStartTime(move.getStartTime());
			    				moveAdd.get(move.getClassDate()).setOldEndTime(move.getEndTime());
			    			}else{
			    				moveAdd.get(move.getClassDate()).setStartTime(DateUtils.parse(DateUtils.format(new Date(),DateUtils.FORMAT_SHORT)+" 00:00:00", DateUtils.FORMAT_LONG));
			    				moveAdd.get(move.getClassDate()).setEndTime(DateUtils.parse(DateUtils.format(new Date(),DateUtils.FORMAT_SHORT)+" 00:00:00", DateUtils.FORMAT_LONG));
			    				moveAdd.get(move.getClassDate()).setName("休");
			    			}
			    		}
			    	}else{
			    		ApplicationEmployeeClassDetail detail = new ApplicationEmployeeClassDetail();
						detail.setEmployName(move.getEmployName());
						detail.setEmployId(move.getEmployId());
						detail.setShouldTime(move.getShouldTime());
						detail.setClassDate(move.getClassDate());
						detail.setIsMove(1);
						detail.setMustAttnTime(move.getMustAttnTime());
						if(move.getIsMove().intValue()==0){//老数据
			    			if(move.getClassSettingId()!=null){
			    				detail.setOldClassSettingId(move.getClassSettingId());
								detail.setOldName(move.getName());
								detail.setOldStartTime(move.getStartTime());
								detail.setOldEndTime(move.getEndTime());
			    			}else{
			    				detail.setOldStartTime(DateUtils.parse(DateUtils.format(new Date(),DateUtils.FORMAT_SHORT)+" 00:00:00", DateUtils.FORMAT_LONG));
			    				detail.setOldEndTime(DateUtils.parse(DateUtils.format(new Date(),DateUtils.FORMAT_SHORT)+" 00:00:00", DateUtils.FORMAT_LONG));
			    				detail.setOldName("休");
			    			}
			    		}else{//新数据
	                        if(move.getClassSettingId()!=null){
	                        	detail.setClassSettingId(move.getClassSettingId());
	                        	detail.setName(move.getName());
	                        	detail.setStartTime(move.getStartTime());
	                        	detail.setEndTime(move.getEndTime());
			    			}else{
			    				detail.setStartTime(DateUtils.parse(DateUtils.format(new Date(),DateUtils.FORMAT_SHORT)+" 00:00:00", DateUtils.FORMAT_LONG));
			    				detail.setEndTime(DateUtils.parse(DateUtils.format(new Date(),DateUtils.FORMAT_SHORT)+" 00:00:00", DateUtils.FORMAT_LONG));
			    				detail.setName("休");
			    			}
			    		}
			    		moveAdd.put(move.getClassDate(), detail);
			    	}
			    }
			    list.clear();
			    //组装数据
			    for (Map.Entry<Date, ApplicationEmployeeClassDetail> entry: moveAdd.entrySet()) {
			    	map.put(entry.getKey(), entry.getValue());
			    }
			    for (Map.Entry<Date, ApplicationEmployeeClassDetail> entry: map.entrySet()) {
			    	list.add(entry.getValue());
			    }
			}
		    Map<String,Map<String,String>> classDetailMap = new HashMap<String,Map<String,String>>();
			Map<String,Map<String,String>> classDetailMap1 = new HashMap<String,Map<String,String>>();
		    if("1".equals(type)){
				String addOrUpdate = RedisUtils.getString("classsSet_"+classMonth+"_"+String.valueOf(user.getEmployeeId()));
				String delete = RedisUtils.getString("delete_classsSet_"+classMonth+"_"+String.valueOf(user.getEmployeeId()));
				if(addOrUpdate!=null&&!"".equals(addOrUpdate)){
					classDetailMap = (Map<String, Map<String, String>>) JSONUtils.readAsMap(addOrUpdate);
				}
                if(delete!=null&&!"".equals(delete)){
                	classDetailMap1 = (Map<String, Map<String, String>>) JSONUtils.readAsMap(delete);
				}
			}else if("2".equals(type)){
				String addOrUpdate = RedisUtils.getString("moveClasssSet_"+classMonth+"_"+String.valueOf(user.getEmployeeId()));
				String delete = RedisUtils.getString("delete_moveClasssSet_"+classMonth+"_"+String.valueOf(user.getEmployeeId()));
				if(addOrUpdate!=null&&!"".equals(addOrUpdate)){
					classDetailMap = (Map<String, Map<String, String>>) JSONUtils.readAsMap(addOrUpdate);
				}
                if(delete!=null&&!"".equals(delete)){
                	classDetailMap1 = (Map<String, Map<String, String>>) JSONUtils.readAsMap(delete);
				}
			}
		    //
		    List<ApplicationEmployeeClassDetail> new_list = new ArrayList<ApplicationEmployeeClassDetail>();
		    //新增的排班集合-{1716={2019-11-07=111}}
		    if(classDetailMap!=null){
		    	if(classDetailMap.containsKey(String.valueOf(employeeId))){
		    		Map<String,String> map = classDetailMap.get(String.valueOf(employeeId));
					Iterator<Map.Entry<String, String>> it = map.entrySet().iterator(); 
					while(it.hasNext()){ 
						Map.Entry<String, String> entry = it.next(); 
						if(list!=null&&list.size()>0){
							for(ApplicationEmployeeClassDetail detail:list){
								if(DateUtils.format(detail.getClassDate(),DateUtils.FORMAT_SHORT).equals(entry.getKey())){
		    						ClassSetting classset =  classSettingService.getById(Long.valueOf(map.get(DateUtils.format(detail.getClassDate(),DateUtils.FORMAT_SHORT))));
									detail.setClassSettingId(Long.valueOf(map.get(DateUtils.format(detail.getClassDate(),DateUtils.FORMAT_SHORT))));
									detail.setName(classset.getName());
									detail.setMustAttnTime(classset.getMustAttnTime());
									detail.setStartTime(classset.getStartTime());
									detail.setEndTime(classset.getEndTime());
									if("2".equals(type)){
										detail.setIsMove(1);
									}
									it.remove();
		    					}
							}
						}else{
							ClassSetting classset =  classSettingService.getById(Long.valueOf(entry.getValue()));
		    				ApplicationEmployeeClassDetail detail = new ApplicationEmployeeClassDetail();
		    				detail.setEmployId(employeeId);
		    				detail.setClassDate(DateUtils.parse(entry.getKey(), DateUtils.FORMAT_SHORT));
		    				detail.setClassSettingId(classset.getId());
							detail.setName(classset.getName());
							detail.setMustAttnTime(classset.getMustAttnTime());
							detail.setStartTime(classset.getStartTime());
							detail.setEndTime(classset.getEndTime());
							new_list.add(detail);
							if("2".equals(type)){
								detail.setIsMove(1);
							}
							it.remove();
						}
						
					} 
					if(map!=null&&map.size()>0){
						for (Map.Entry<String, String> entry: map.entrySet()) {
							ClassSetting classset =  classSettingService.getById(Long.valueOf(entry.getValue()));
		    				ApplicationEmployeeClassDetail detail = new ApplicationEmployeeClassDetail();
		    				detail.setEmployId(employeeId);
		    				detail.setClassDate(DateUtils.parse(entry.getKey(), DateUtils.FORMAT_SHORT));
		    				detail.setClassSettingId(classset.getId());
							detail.setName(classset.getName());
							detail.setMustAttnTime(classset.getMustAttnTime());
							detail.setStartTime(classset.getStartTime());
							detail.setEndTime(classset.getEndTime());
							new_list.add(detail);
							if("2".equals(type)){
								detail.setIsMove(1);
							}
						}
					}
		    	}
		    }
		    if(classDetailMap1!=null){
		    	if(classDetailMap1.containsKey(String.valueOf(employeeId))){
		    		Map<String,String> map = classDetailMap1.get(String.valueOf(employeeId));
		    		for (Map.Entry<String, String> entry: map.entrySet()) {
		    			if(list!=null&&list.size()>0){
		    				for(ApplicationEmployeeClassDetail detail:list){
		    					if(DateUtils.format(detail.getClassDate(),DateUtils.FORMAT_SHORT).equals(entry.getKey())){
		    						detail.setClassSettingId(0L);
									detail.setName("休");
									detail.setMustAttnTime(0d);
									detail.setStartTime(DateUtils.parse(DateUtils.format(new Date(),DateUtils.FORMAT_SHORT)+" 00:00:00", DateUtils.FORMAT_LONG));
									detail.setEndTime(DateUtils.parse(DateUtils.format(new Date(),DateUtils.FORMAT_SHORT)+" 00:00:00", DateUtils.FORMAT_LONG));
									if("2".equals(type)){
										detail.setIsMove(1);
									}
		    					}
		    				}
		    			}else{
		    				ApplicationEmployeeClassDetail detail = new ApplicationEmployeeClassDetail();
		    				detail.setClassSettingId(0L);
							detail.setName("休");
							detail.setMustAttnTime(0d);
							detail.setStartTime(DateUtils.parse(DateUtils.format(new Date(),DateUtils.FORMAT_SHORT)+" 00:00:00", DateUtils.FORMAT_LONG));
							detail.setEndTime(DateUtils.parse(DateUtils.format(new Date(),DateUtils.FORMAT_SHORT)+" 00:00:00", DateUtils.FORMAT_LONG));
							new_list.add(detail);
							if("2".equals(type)){
								detail.setIsMove(1);
							}
		    			}
		    		}
		    	}
		    }
		    for(ApplicationEmployeeClassDetail detail:new_list){
		    	list.add(detail);
		    }
		}catch(Exception e){
			logger.error(user.getEmployee().getCnName()+"getEmployeeClassSetByMonth"+e.getMessage());
		}
		return list;
	}
	
	 //根据部门和月份查询排班数据
	@RequestMapping("/getEmployeeClassByDepartAndMonth.htm")
	@ResponseBody
	public List<ApplicationEmployeeClass> getEmployeeClassByDepartAndMonth(Long departId,String month){
		List<ApplicationEmployeeClass> list = new ArrayList<ApplicationEmployeeClass>();
		if(month==null||"".equals(month)){
			return list;
		}
		ApplicationEmployeeClass applicationEmployeeClass = new ApplicationEmployeeClass();
		applicationEmployeeClass.setDepartId(departId);
		applicationEmployeeClass.setIsMove(0);
		if(departId.equals(1L)){
			applicationEmployeeClass.setDepartId(null);
		}
		applicationEmployeeClass.setClassMonth(DateUtils.parse(month, "yyyy年MM月"));
		list = applicationEmployeeClassService.getByCondition(applicationEmployeeClass);
		return list;
	}
	
	//查询部门所有排班数据
	@RequestMapping("/getEmployeeClassByDepart.htm")
	@ResponseBody
	public List<ApplicationEmployeeClass> getEmployeeClassByDepart(){
		User user = userService.getCurrentUser();
		List<ApplicationEmployeeClass> list = new ArrayList<ApplicationEmployeeClass>();
		//判断用户是否排班人
	    List<ScheduleGroup> schedulerList = scheduleGroupMapper.getListByScheduler(user.getEmployee().getId());
		if(schedulerList!=null&&schedulerList.size()>0){
			ApplicationEmployeeClass applicationEmployeeClass = new ApplicationEmployeeClass();
			applicationEmployeeClass.setDepartId(schedulerList.get(0).getDepartId());
			applicationEmployeeClass.setGroupId(schedulerList.get(0).getId());
			applicationEmployeeClass.setIsMove(0);
			list = applicationEmployeeClassService.getByCondition(applicationEmployeeClass);
		}
		return list;
	}
	
	@RequestMapping(value="/getEmpClassTime.htm", produces = "text/json;charset=UTF-8")
	@ResponseBody
	public String getEmpClassTime(HttpServletRequest request,EmployeeClass employeeClass,String callback) throws OaException{
		
		Long employeeId = null;
		if(null == employeeClass.getEmployId()){
			employeeId = employeeService.getCurrentEmployeeId();
			logger.info("重新赋值employId:旧：{}，新：{}",employeeClass.getEmployId(),employeeId);
			employeeClass.setEmployId(employeeId);
			
		}
		EmployeeClass empClass = employeeClassService.getEmployeeClassSetting(employeeClass);
		
		Map<String, Object> map = new HashMap<String, Object>();
		if(null != empClass){
			String startTime = DateUtils.format(empClass.getStartTime(), "HH:mm");
			String endTime = DateUtils.format(empClass.getEndTime(), "HH:mm");
			
			map.put("className", empClass.getName());
			map.put("startTime", startTime);
			map.put("endTime", endTime);
			//工作日志
			BaseEmpWorkLog workLogP = new BaseEmpWorkLog();
			workLogP.setEmployeeId(employeeId);
			workLogP.setWorkDate(employeeClass.getClassDate());
			List<BaseEmpWorkLog> workLogList = baseEmpWorkLogMapper.selectByCondition(workLogP);
			List<Map<String,Object>> workLogList1 = new ArrayList<Map<String,Object>>();
        	String url1 = request.getRequestURL().toString().split("oaWeb")[0]+"oaWeb";
			
			if(workLogList==null||workLogList.size()<=0){
				Map<String,Object> workLogM = new HashMap<String,Object>();
				workLogM.put("a_url", url1+"/workLog/index.htm?workDate="+DateUtils.format(employeeClass.getClassDate(), DateUtils.FORMAT_SHORT));
				workLogM.put("a_name", "填写");
				workLogM.put("span_name", "&nbsp;");
				workLogList1.add(workLogM);
				map.put("workLogList", workLogList1);
			}else{
				for(BaseEmpWorkLog data:workLogList){
					Map<String,Object> workLogM = new HashMap<String,Object>();
					workLogM.put("a_url", url1+"/workLog/approval.htm?index=wdsq&flag=no&urlType=11&taskId="+data.getId());
					workLogM.put("a_name", "查看");
					
					String span_name = "&nbsp;";
					if(data.getApprovalStatus()!=null){
						switch (data.getApprovalStatus().intValue()) {
						case 100:
							span_name = "审阅中";
							break;
						case 200:
							span_name = "通过";
							break;	
						case 300:
							span_name = "不通过";
							break;
						case 400:
							span_name = "已撤销";
							break;
						case 500:
							span_name = "已失效";
							break;	
						case 600:
							span_name = "通过";
							break;
						case 700:
							span_name = "不通过";
							break;
						}
					}
					workLogM.put("span_name", span_name);
					workLogList1.add(workLogM);
					map.put("workLogList", workLogList1);
				}
			}
		}	
		
		EmployeeDuty dutyP = new EmployeeDuty();
		dutyP.setEmployId(employeeClass.getEmployId());
		dutyP.setDutyDate(employeeClass.getClassDate());
		List<EmployeeDuty> dutyList = employeeDutyService.selectByCondition(dutyP);
		if(dutyList!=null&&dutyList.size()>0){
			String startTime = DateUtils.format(dutyList.get(0).getStartTime(), "HH:mm");
			String endTime = DateUtils.format(dutyList.get(0).getEndTime(), "HH:mm");
			
			map.put("className", "值班");
			map.put("startTime", startTime);
			map.put("endTime", endTime);
		}
        String json = JSONUtils.write(map);
		callback = callback+"("+json+")";
		return callback;
		
	}
	
	@RequestMapping(value="/getClassTime.htm", produces = "text/json;charset=UTF-8")
	@ResponseBody
	public String getClassTime(String employeeId,String classDate) throws OaException{
		String classJsonData = employeeClassService.getEmployeeClassJsonData(employeeId,classDate);
		return classJsonData;
	}

	
	/**
	 * @throws OaException 
	  * 查看我的排班
	  * @Title: myScheduleView
	  * @param yearMonth年月
	  * @return    设定文件
	  * ModelAndView    返回类型
	  * @throws
	 */
	@RequestMapping("/myClassView.htm")
	public ModelAndView myScheduleView(String yearMonth) throws OaException{
		
		List<EmployeeClass> classList = new ArrayList<EmployeeClass>();
		//修改点：原来一次显示一个月，现在修改为一天，默认获取当天
		Long employeeId = employeeService.getCurrentEmployeeId();
		Date month ;
		if(null == yearMonth){
			month = DateUtils.parse(DateUtils.format(new Date(), DateUtils.FORMAT_SHORT), DateUtils.FORMAT_SHORT);
		}else{
			month = DateUtils.parse(yearMonth, "yyyy-mm-dd");
		}
		
		ModelMap map = new ModelMap();
		
		//设置员工id，开始时间，结束时间
		EmployeeClass condition = new EmployeeClass();
		condition.setEmployId(employeeId);

		condition.setClassDate(month);
		EmployeeClass eClass = employeeClassService.getEmployeeClassSetting(condition);
		
		if(null == eClass){
			eClass = new EmployeeClass();
			eClass.setClassDate(month);
		}
		classList.add(eClass);
		
		yearMonth = DateUtils.format(month, "yyyy年MM月");
		map.put("yearMonth", yearMonth);
		map.put("list", classList);
		return new ModelAndView("attendance/myClass",map);
	}
	
	/**
	  * getEmployeeClassInfoByDepartId(根据部门获取员工排班信息)
	  * @Title: getEmployeeClassInfoByDepartId
	  * @Description: 根据部门获取员工排班信息
	  * @param departId
	  * @return
	  * @throws Exception    设定文件
	  * List<EmployeeClass>    返回类型
	  * @throws
	 */
	@RequestMapping("/getEmployeeClassInfoByDepartId.htm")
	@ResponseBody
	public List<EmployeeClass> getEmployeeClassInfoByDepartId(Long departId) throws Exception{
		return employeeClassService.getEmployeeClassInfoByDepartId(departId);
	}
	
	/**
	  * save(逐单保存员工排班信息)
	  * @Title: save
	  * @Description: 逐单保存员工排班信息
	  * @param employeeClass
	  * @return    设定文件
	  * JSON    返回类型
	  * @throws
	 */
	@RequestMapping("/save.htm")
	@ResponseBody
	public JSON save(EmployeeClass employeeClass){
		try{
			employeeClassService.save(employeeClass);
			
			return JsonWriter.successfulMessage("批量保存员工排班信息成功");
		}catch(Exception e){
			logger.error("批量保存员工排班信息失败，原因={}",e.getMessage());
			return JsonWriter.failedMessage("批量保存员工排班信息失败，原因="+e.getMessage());
		}
	}
	
	/**
	 * 删除startDate之后的排班数据
	 * @param startDate
	 * @return
	 */
	@RequestMapping("/deleteByStartDate.htm")
	@ResponseBody
	public JSON deleteByStartDate(String startDate){
		try{
			employeeClassService.deleteByStartDate(DateUtils.parse(startDate, DateUtils.FORMAT_SHORT));
			return JsonWriter.successfulMessage("批量删除员工排班信息成功");
		}catch(Exception e){
			logger.error("批量删除员工排班信息失败，原因={}",e.getMessage());
			return JsonWriter.failedMessage("批量删除员工排班信息失败，原因="+e.getMessage());
		}
	}
	
}
