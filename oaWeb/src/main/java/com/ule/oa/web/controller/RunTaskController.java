package com.ule.oa.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.flowable.engine.TaskService;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.ule.arch.basehttpclient.BaseHttpClientFactory;
import com.ule.arch.basehttpclient.client.BaseHttpClient;
import com.ule.arch.basehttpclient.model.HttpRequest;
import com.ule.arch.basehttpclient.model.HttpResponse;
import com.ule.oa.base.common.OaCommon;
import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.EmpApplicationAbnormalAttendance;
import com.ule.oa.base.po.EmpApplicationBusiness;
import com.ule.oa.base.po.EmpApplicationLeave;
import com.ule.oa.base.po.EmpApplicationOutgoing;
import com.ule.oa.base.po.EmpApplicationOvertime;
import com.ule.oa.base.po.EmpApplicationOvertimeLate;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.po.ReProcdef;
import com.ule.oa.base.po.RuProcdef;
import com.ule.oa.base.po.RunTask;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.AnnualVacationService;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.EmpApplicationAbnormalAttendanceService;
import com.ule.oa.base.service.EmpApplicationBusinessService;
import com.ule.oa.base.service.EmpApplicationLeaveService;
import com.ule.oa.base.service.EmpApplicationOutgoingService;
import com.ule.oa.base.service.EmpApplicationOvertimeLateService;
import com.ule.oa.base.service.EmpApplicationOvertimeService;
import com.ule.oa.base.service.EmployeeClassService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.ReProcdefService;
import com.ule.oa.base.service.RuProcdefService;
import com.ule.oa.base.service.RunTaskService;
import com.ule.oa.base.service.SmsService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.service.UserTaskService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.po.CommonPo;
import com.ule.oa.common.spring.config.CustomPropertyPlaceholderConfigurer;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.MD5Encoder;
import com.ule.oa.common.utils.PageModel;
import com.ule.oa.common.utils.json.JSONUtils;
import com.ule.oa.web.util.ReturnUrlUtil;
import com.ule.oa.web.util.Token;

/**
  * @ClassName: PositionController
  * @Description: 流程管理
  * @author mahaitao
  * @date 2017年5月31日 下午20:47:07
 */
@Controller
@RequestMapping("runTask")
public class RunTaskController {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private RunTaskService runTaskService;
	@Autowired
	private ReProcdefService reProcdefService;
	@Autowired
	private RuProcdefService ruProcdefService;
	@Autowired
	private UserService userService;
	@Autowired
	private EmpApplicationOvertimeService empApplicationOvertimeService;
	@Autowired
	private EmpApplicationAbnormalAttendanceService empApplicationAbnormalAttendanceService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private EmpApplicationOutgoingService empApplicationOutgoingService;
	@Autowired
	private EmpApplicationBusinessService empApplicationBusinessService;
	@Autowired
	private EmpApplicationOvertimeLateService empApplicationOvertimeLateService;
	@Autowired
	private EmployeeClassService employeeClassService;
	@Autowired
	private EmpApplicationLeaveService empApplicationLeaveService;
	@Autowired
	private AnnualVacationService annualVacationService;
	@Autowired
	private ConfigService configService;
	@Autowired
	private SmsService smsService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private UserTaskService userTaskService;
	private final static String ENV = (String)CustomPropertyPlaceholderConfigurer.getProperty("cache.env");
	
	private static BaseHttpClient client = BaseHttpClientFactory.getClient();
	
	@RequestMapping("/start.htm")
	public ModelAndView start(HttpServletRequest request, String code){
		//1:拿到流程
		ReProcdef reProcdef = reProcdefService.getByCode(code);
		ModelMap mmap = new ModelMap();  
		mmap.addAttribute("reProcdefId", code);
		return new ModelAndView("forward:" + reProcdef.getRedirectUrl().trim(), mmap); 
	}
	
	@RequestMapping("/nonAuth/toView.htm")
	public ModelAndView toView(HttpServletRequest request,String empId,String ruTaskId,String urlType,String sign){
		logger.info("跳转到审批详情页，流程id={}",ruTaskId+";empId="+empId+";sign="+sign+";md5="+MD5Encoder.md5Hex(ruTaskId));
		
		if(StringUtils.isNotBlank(sign) && sign.equals(MD5Encoder.md5Hex(ruTaskId))){
			return view(request, Long.parseLong(ruTaskId), urlType);
		}
		
		return new ModelAndView("error/noPower"); 
	}
	
	@RequestMapping("/view.htm")
	public ModelAndView view(HttpServletRequest request, Long ruTaskId, String urlType){
		//1:拿到执行流程
		RuProcdef runProcdef = ruProcdefService.getUrlByRuTaskId(ruTaskId);
		ModelMap mmap = new ModelMap();  
		if(urlType == null || urlType.equals("")) {
			urlType = "1";
		}
		RunTask runTask = runTaskService.getById(runProcdef.getRuTaskId());
		String rdirectUrl = runProcdef.getRedirectUrl().trim();
		if(runTask.getProcessStatus().equals(RunTask.PROCESS_STATUS_400) || runTask.getProcessStatus().equals(RunTask.PROCESS_STATUS_300)) {
			rdirectUrl = runTask.getRedirectUrl().trim();
		}
		User user = userService.getCurrentUser();
		String forwardUrl = "forward:" + rdirectUrl + runProcdef.getEntityId() + "&ruProcdefId=" + runProcdef.getId();
		if(runTask.getProcessStatus().equals(RunTask.PROCESS_STATUS_100) && runTask.getCreatorId().equals(user.getEmployeeId())) {
			forwardUrl = forwardUrl + "&nodeCode=isEmployeeSelf";
		}
		mmap.put("urlType", urlType);
		return new ModelAndView(forwardUrl, mmap);
	}
	
	@RequestMapping("/edit.htm")
	public ModelAndView edit(HttpServletRequest request, Long ruProcdefId, String urlType){
		//1:拿到执行流程
		RuProcdef ruProcdef = ruProcdefService.getById(ruProcdefId);
		User user = userService.getCurrentUser();
		if(ruProcdef == null || !user.getEmployeeId().equals(ruProcdef.getAssigneeId())) {
			return new ModelAndView("forward:/404");
		} else {
			if(urlType == null || urlType.equals("")) {
				urlType = "1";
			}
			ModelMap mmap = new ModelMap();  
			mmap.put("urlType", urlType);
			String foward = "forward:" + ruProcdef.getRedirectUrl().trim() + ruProcdef.getEntityId()+ "&ruProcdefId=" + ruProcdefId;
			if(ruProcdef.getDelFlag().equals(CommonPo.STATUS_NORMAL) &&  ruProcdef.getProcessStatus().equals(RuProcdef.PROCESS_STATUS_100)) {
				foward = "forward:" + ruProcdef.getRedirectUrl().trim() + ruProcdef.getEntityId()+ "&nodeCode=" + ruProcdef.getNodeCode().trim() + "&ruProcdefId=" + ruProcdefId;
			}
			return new ModelAndView(foward, mmap); 
		}
	}
	
	@RequestMapping("/handle.htm")
	public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, String taskId, String assigneeId, String urlType, String callbackMail, String value, @RequestParam(value = "token", required = true)String token, String messageid){
		logger.info("随心邮参数：taskId＝" +taskId);
		logger.info("随心邮参数：assigneeId＝" +assigneeId);
		logger.info("随心邮参数：urlType＝" +urlType);
		logger.info("随心邮参数：callbackMail＝" +callbackMail);
		logger.info("随心邮参数：value＝" +value);
		logger.info("随心邮参数：token＝" +token);
		String forwardSuccessUrl = "";//登录以后自动跳转的url
		
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		if(task==null){
			logger.info("taskId="+taskId+"的任务已完成或者不存在。");
			return new ModelAndView("forward:/login/login.htm");
        }
	
		List<IdentityLink> list = taskService.getIdentityLinksForTask(taskId);
		if(list==null||list.size()<=0){
			logger.info("taskId="+taskId+"的任务已完成或者不存在。");
			return new ModelAndView("forward:/login/login.htm");
		}
		
		boolean isMatch = true;
		for(IdentityLink link:list){
			if(link.getUserId().equals(assigneeId)){
				isMatch = false;
				break;
			}
		}
		if(isMatch){
			logger.info("taskId="+taskId+"的任务未分配给assigneeId="+assigneeId+"的用户。");
			return new ModelAndView("forward:/login/login.htm");
		}
		
		forwardSuccessUrl = userTaskService.getForwardSuccessUrl(task);
		//-----------------end
		if(callbackMail == null || callbackMail.equals("") || value == null || value.equals("") || !MD5Encoder.md5Hex(callbackMail).equals(value)) {
			request.setAttribute("forwardSuccessUrl", forwardSuccessUrl);
			return new ModelAndView("forward:/login/login.htm");
		}
		
		Employee emp = employeeService.getById(Long.valueOf(assigneeId));
		if (emp != null && emp.getEmail() != null && emp.getEmail().equals(callbackMail)) {
			try {
				logger.info("随心邮调用地址：" + ConfigConstants.WEBMAIL_TOKEN + "?token=" + token);

				HttpRequest req = new HttpRequest.Builder().url(ConfigConstants.WEBMAIL_TOKEN + "?token=" + token).get().build();
	    		HttpResponse rep = client.sendRequest(req);
				String str = rep.fullBody();
				
				@SuppressWarnings("unchecked")
				Map<String, String> strMap = (Map<String, String>) JSONUtils.readAsMap(str);
				if(strMap.get("code").equals("001") && strMap.get("codemsg").equals(callbackMail)) {
					Subject currentUser = SecurityUtils.getSubject();
					User user = new User();
					if (currentUser.isAuthenticated()) {
						user = userService.getCurrentUser();
					}
					if(user == null || user.getEmployeeId() == null || !user.getEmployeeId().equals(emp.getId())) {
						logger.info("账号不是登陆状态,模仿登陆");
						UsernamePasswordToken upToken = new UsernamePasswordToken("callbackMaileSuiXinYou", emp.getId().toString());
						upToken.setRememberMe(false);
						try {
							currentUser.login(upToken);
							logger.info("账号不是登陆状态,模仿登陆成功");
						} catch (Exception e) {
						}
						user = userService.getCurrentUser();
					}
					if(user== null) {
						request.setAttribute("forwardSuccessUrl", forwardSuccessUrl);
						return new ModelAndView("forward:/login/login.htm");
					} else if(!user.getEmployeeId().toString().equals(assigneeId)) {
						request.setAttribute("forwardSuccessUrl", forwardSuccessUrl);
						return new ModelAndView("forward:/login/login.htm");
					} else {
						//记录来源是从随心邮收件箱进入的
						Cookie dataSourceUrl=new Cookie("dataSourceUrl","1"); 
						dataSourceUrl.setMaxAge(30*24*60*60);   //存活期为一个月 30*24*60*60
						dataSourceUrl.setPath("/");
						response.addCookie(dataSourceUrl);
						
						
						Cookie callbackMaileSuiXinYou=new Cookie("callbackMaileSuiXinYou","1"); 
						callbackMaileSuiXinYou.setMaxAge(30*24*60*60);   //存活期为一个月 30*24*60*60
						callbackMaileSuiXinYou.setPath("/");
						response.addCookie(callbackMaileSuiXinYou);
						
						if(urlType == null || urlType.equals("")) {
							urlType = "1";
						}
						ModelMap mmap = new ModelMap();  
						mmap.put("urlType", urlType);
						return new ModelAndView(forwardSuccessUrl, mmap);
					}
				} else {
					logger.info("随心邮参数ERROR:" + strMap.get("code") + "-" + strMap.get("codemsg"));
					request.setAttribute("forwardSuccessUrl", forwardSuccessUrl);
					return new ModelAndView("forward:/login/login.htm");
				}
			} catch (Exception e1) {
				logger.error("随心邮调用地址失败" + e1.toString());
				request.setAttribute("forwardSuccessUrl", forwardSuccessUrl);
				return new ModelAndView("forward:/login/login.htm");
			}
		} else {
			request.setAttribute("forwardSuccessUrl", forwardSuccessUrl);
			return new ModelAndView("forward:/login/login.htm");
		}
		
	}
	
	public String getForwardSuccessUrl(RuProcdef ruProcdef){
		String foward = "forward:" + ruProcdef.getRedirectUrl().trim() + ruProcdef.getEntityId()+ "&ruProcdefId=" + ruProcdef.getId();
		if(ruProcdef.getDelFlag().equals(CommonPo.STATUS_NORMAL) &&  ruProcdef.getProcessStatus().equals(RuProcdef.PROCESS_STATUS_100)) {
			foward = "forward:" + ruProcdef.getRedirectUrl().trim() + ruProcdef.getEntityId()+ "&nodeCode=" + ruProcdef.getNodeCode().trim() + "&ruProcdefId=" + ruProcdef.getId();
		}
		
		return foward;
	}
	
	@ResponseBody
	@RequestMapping("/cancel.htm")
	@Token(remove = true)
	public Map<String, Object> cancel(HttpServletRequest request, Long ruProcdefId, String opinion, String token) {
		User user = userService.getCurrentUser();
		Map<String,Object> returnMap = new HashMap<String, Object>();
		
		RuProcdef ruProcdef = ruProcdefService.getById(ruProcdefId);
		RunTask runTask = runTaskService.getById(ruProcdef.getRuTaskId());
		if(!user.getEmployeeId().equals(runTask.getCreatorId())) {
			returnMap.put("code", OaCommon.CODE_ERROR);
			returnMap.put("msg", OaCommon.getCodeMap().get(OaCommon.CODE_ERROR));
			return returnMap;
		} else if(!runTask.getProcessStatus().equals(RuProcdef.PROCESS_STATUS_100)) {
			returnMap.put("code", OaCommon.CODE_ERROR);
			returnMap.put("msg", runTask.getProcessName() + runTask.getCreator() + "已经不是待处理,请确认。");
			return returnMap;
		} else {
			if(opinion == null || opinion.equals("")) {
				opinion = "申请撤回";
			}
			Map<String, String> refuseMap = runTaskService.cancel(ruProcdef.getRuTaskId(), user, opinion);
			if(refuseMap.get("code").toString().equals(OaCommon.CODE_ERROR)) {
				returnMap.put("repeatFlag", false);
				returnMap.put("message", refuseMap.get("msg").toString());
				returnMap.put("success",false);
				return returnMap;
			} else {
				runTaskService.updateEntyStatus(ruProcdef, user, 400);
				//请假
				if(RunTask.RUN_CODE_60.equals(ruProcdef.getReProcdefCode())){
					try {
						empApplicationLeaveService.lockVecation(Long.valueOf(ruProcdef.getEntityId()),false,user);
					} catch (NumberFormatException e) {
						returnMap.put("code", OaCommon.CODE_ERROR);
						returnMap.put("msg", e.getMessage());
						return returnMap;
					} catch (Exception e) {
						returnMap.put("code", OaCommon.CODE_ERROR);
						returnMap.put("msg", e.getMessage());
						return returnMap;
					}
				}
			}
		}
		returnMap.put("success",true);
		returnMap.put("msg", OaCommon.getCodeMap().get(OaCommon.CODE_OK));
		return returnMap;
	}
	
	@ResponseBody
	@RequestMapping("/refuse.htm")
	public Map<String, String> refuse(HttpServletRequest request, String ruProcdefIds, String opinion) {
		String[] ruProcdefIdStr = ruProcdefIds.split(",");
		User user = userService.getCurrentUser();
		Map<String,String> returnMap = new HashMap<String, String>();
		for (String string : ruProcdefIdStr) {
			RuProcdef ruProcdef = ruProcdefService.getById(Long.valueOf(string));
			if(!user.getEmployeeId().equals(ruProcdef.getAssigneeId())) {
				returnMap.put("code", OaCommon.CODE_ERROR);
				returnMap.put("msg", OaCommon.getCodeMap().get(OaCommon.CODE_ERROR));
				return returnMap;
			} else if(!ruProcdef.getProcessStatus().equals(RuProcdef.PROCESS_STATUS_100)) {
				RuProcdef runProcdef = ruProcdefService.getUrlByRuTaskId(ruProcdef.getRuTaskId());
				returnMap.put("code", OaCommon.CODE_ERROR);
				returnMap.put("msg", ruProcdef.getProcessName() + runProcdef.getAssignee() + "已经不是待处理,请确认。");
				return returnMap;
			} else {
				if(opinion == null || opinion.equals("")) {
					opinion = "审批拒绝";
				}
				Map<String, String> refuseMap = runTaskService.refuse(Long.valueOf(string), user, opinion);
				if(refuseMap.get("code").toString().equals(OaCommon.CODE_ERROR)) {
					return refuseMap;
				} else {
					runTaskService.updateEntyStatus(ruProcdef, user, 300);
					//请假
					if(RunTask.RUN_CODE_60.equals(ruProcdef.getReProcdefCode())){
						try {
							empApplicationLeaveService.lockVecation(Long.valueOf(ruProcdef.getEntityId()),false,user);
						} catch (NumberFormatException e) {
							returnMap.put("code", OaCommon.CODE_ERROR);
							returnMap.put("msg", e.getMessage());
							return returnMap;
						} catch (Exception e) {
							returnMap.put("code", OaCommon.CODE_ERROR);
							returnMap.put("msg", e.getMessage());
							return returnMap;
						}
					}
				}
			}
		}
		returnMap.put("code", OaCommon.CODE_OK);
		returnMap.put("msg", OaCommon.getCodeMap().get(OaCommon.CODE_OK));
		return returnMap;
	}
	
	@ResponseBody
	@RequestMapping("/approval.htm")
	public Map<String, String> approval(HttpServletRequest request, String ruProcdefIds, String opinion) {
		String[] ruProcdefIdStr = ruProcdefIds.split(",");
		User user = userService.getCurrentUser();
		Map<String,String> returnMap = new HashMap<String, String>();
		for (String string : ruProcdefIdStr) {
			RuProcdef ruProcdef = ruProcdefService.getById(Long.valueOf(string));
			if(!user.getEmployeeId().equals(ruProcdef.getAssigneeId())) {
				returnMap.put("code", OaCommon.CODE_ERROR);
				returnMap.put("msg", OaCommon.getCodeMap().get(OaCommon.CODE_ERROR));
				return returnMap;
			} else if(!ruProcdef.getProcessStatus().equals(RuProcdef.PROCESS_STATUS_100)) {
				RuProcdef runProcdef = ruProcdefService.getUrlByRuTaskId(ruProcdef.getRuTaskId());
				returnMap.put("code", OaCommon.CODE_ERROR);
				returnMap.put("msg", ruProcdef.getProcessName() + runProcdef.getAssignee() + "已经不是待处理,请确认。");
				return returnMap;
			} else {
				if(opinion == null || opinion.equals("")) {
					opinion = "审批通过";
				}
				try {
					updateEntityStatus(ruProcdef, user, 200);
				} catch (Exception e) {
					returnMap.put("code", OaCommon.CODE_ERROR);
					returnMap.put("msg", e.getMessage());
					return returnMap;
				}
			}
		}
		returnMap.put("code", OaCommon.CODE_OK);
		returnMap.put("msg", OaCommon.getCodeMap().get(OaCommon.CODE_OK));
		return returnMap;
	}
	
	public void sendErrorMsgByType(String reason){
		List<Config> configs = configService.getListByCode(ConfigConstants.LEAVE_APPLY_WARN_PHONE);
		for(Config conf : configs){
			try{
				logger.info("发送异常信息给用户{}",conf.getDisplayCode());
				smsService.sendMessage(conf.getDisplayCode(), "[MO办公自动化-"+ENV+"]PROBLEM:" + reason + "出现问题，请尽快处理[短信生成时间="+DateUtils.format(new Date(),DateUtils.FORMAT_DD_HH_MM)+"]！！！");
			}catch(Exception e){
				logger.error("给{}用户发送MO预警信息失败，失败原因:{}",conf.getDisplayCode(),e.getMessage());
			}
		}
	}
	
	//处理流程
	public void updateEntityStatus(RuProcdef ruProcdef, User user,Integer approvalStatus) throws Exception {
		boolean type = ("RESIGN_HR".equals(ruProcdef.getNodeCode()) || "RESIGN_HR_DIRECTOR".equals(ruProcdef.getNodeCode()))?true:false;
		if (RunTask.RUN_CODE_30.equals(ruProcdef.getReProcdefCode())) {
			//延时工作
			EmpApplicationOvertime old = empApplicationOvertimeService.getById(Long.valueOf(ruProcdef.getEntityId()));
			if(old!=null){
				if(annualVacationService.check5WorkingDayNextmonth(DateUtils.format(old.getApplyDate(), DateUtils.FORMAT_SHORT),type)){
					logger.error(user.getEmployee().getCnName()+"审批员工Id="+old.getEmployeeId()+"的延时工作:已超出有效时间,无法操作！");
					throw new OaException(user.getEmployee().getCnName()+"审批员工Id="+old.getEmployeeId()+"的延时工作:已超出有效时间,无法操作！");
				}else{
					EmpApplicationOvertime overtime = new EmpApplicationOvertime();
					overtime.setApprovalStatus(approvalStatus);
					overtime.setId(Long.valueOf(ruProcdef.getEntityId()));
					overtime.setNodeCode(ruProcdef.getNodeCode());
					overtime.setApprovalReason("审批通过");
					overtime.setRuProcdefId(ruProcdef.getId());
					//empApplicationOvertimeService.updateById(overtime);
				}
			}else{
				logger.error(user.getEmployee().getCnName()+":延时工作审批数据不存在！");
			}
		} else if (RunTask.RUN_CODE_40.equals(ruProcdef.getReProcdefCode())||RunTask.RUN_CODE_41.equals(ruProcdef.getReProcdefCode())||RunTask.RUN_CODE_120.equals(ruProcdef.getReProcdefCode())) {
			// 异常考勤
			EmpApplicationAbnormalAttendance old = empApplicationAbnormalAttendanceService.getById(Long.valueOf(ruProcdef.getEntityId()));
			if(old!=null){
				if(annualVacationService.check5WorkingDayNextmonth(DateUtils.format(old.getAbnormalDate(), DateUtils.FORMAT_SHORT),type)){
					logger.error(user.getEmployee().getCnName()+"审批员工Id="+old.getEmployeeId()+"的异常考勤："+"已超过审批时间无法审批！");
				}else{
					EmpApplicationAbnormalAttendance attendance = new EmpApplicationAbnormalAttendance();
					attendance.setApprovalStatus(approvalStatus);
					attendance.setId(Long.valueOf(ruProcdef.getEntityId()));
					attendance.setNodeCode(ruProcdef.getNodeCode());
					attendance.setApprovalReason("审批通过");
					attendance.setRuProcdefId(ruProcdef.getId());
					empApplicationAbnormalAttendanceService.updateById(attendance);
				}
			}else{
				logger.error(user.getEmployee().getCnName()+":异常考勤审批数据不存在！");
			}
		} else if (RunTask.RUN_CODE_60.equals(ruProcdef.getReProcdefCode())) {
			// 请假
			EmpApplicationLeave old = empApplicationLeaveService.getById(Long.valueOf(ruProcdef.getEntityId()));
			if(old!=null){
				if(annualVacationService.check5WorkingDayNextmonth(DateUtils.format(old.getStartTime(), DateUtils.FORMAT_SHORT),type)){
					logger.error(user.getEmployee().getCnName()+"审批员工Id="+old.getEmployeeId()+"的请假："+"已超过审批时间无法审批！");
				}else{
					EmpApplicationLeave leave = new EmpApplicationLeave();
					leave.setApprovalStatus(approvalStatus);
					leave.setId(Long.valueOf(ruProcdef.getEntityId()));
					leave.setNodeCode(ruProcdef.getNodeCode());
					leave.setApprovalReason("审批通过");
					leave.setRuProcdefId(ruProcdef.getId());
					empApplicationLeaveService.updateById(leave);
				}
			}else{
				logger.error(user.getEmployee().getCnName()+":请假审批数据不存在！");
			}
		} else if (RunTask.RUN_CODE_70.equals(ruProcdef.getReProcdefCode())) {
			// 外出
			EmpApplicationOutgoing old = empApplicationOutgoingService.getById(Long.valueOf(ruProcdef.getEntityId()));
			if(old!=null){
				if(annualVacationService.check5WorkingDayNextmonth(DateUtils.format(old.getOutDate(), DateUtils.FORMAT_SHORT),type)){
					logger.error(user.getEmployee().getCnName()+"审批员工Id="+old.getEmployeeId()+"的外出："+"已超过审批时间无法审批！");
				}else{
					EmpApplicationOutgoing outgoing = new EmpApplicationOutgoing();
					outgoing.setApprovalStatus(approvalStatus);
					outgoing.setId(Long.valueOf(ruProcdef.getEntityId()));
					outgoing.setNodeCode(ruProcdef.getNodeCode());
					outgoing.setApprovalReason("审批通过");
					outgoing.setRuProcdefId(ruProcdef.getId());
					empApplicationOutgoingService.updateById(outgoing);
				}
			}else{
				logger.error(user.getEmployee().getCnName()+":外出审批数据不存在！");
			}
		} else if (RunTask.RUN_CODE_80.equals(ruProcdef.getReProcdefCode())||RunTask.RUN_CODE_81.equals(ruProcdef.getReProcdefCode())) { 
			// 出差
			EmpApplicationBusiness old = empApplicationBusinessService.getById(Long.valueOf(ruProcdef.getEntityId()));
			if(old!=null){
				if(annualVacationService.check5WorkingDayNextmonth(DateUtils.format(old.getStartTime(), DateUtils.FORMAT_SHORT),type)){
					logger.error(user.getEmployee().getCnName()+"审批员工Id="+old.getEmployeeId()+"的出差："+"已超过审批时间无法审批！");
				}else{
					EmpApplicationBusiness business = new EmpApplicationBusiness();
					business.setApprovalStatus(approvalStatus);
					business.setId(Long.valueOf(ruProcdef.getEntityId()));
					business.setNodeCode(ruProcdef.getNodeCode());
					business.setApprovalReason("审批通过");
					business.setRuProcdefId(ruProcdef.getId());
					empApplicationBusinessService.updateById(business, "1");
				}
			}else{
				logger.error(user.getEmployee().getCnName()+":出差审批数据不存在！");
			}
		} else if (RunTask.RUN_CODE_100.equals(ruProcdef.getReProcdefCode())) { 
			//晚到
			EmpApplicationOvertimeLate old = empApplicationOvertimeLateService.getById(Long.valueOf(ruProcdef.getEntityId()));
			if(old!=null){
				EmpApplicationOvertimeLate overtimeLate = new EmpApplicationOvertimeLate();
				overtimeLate.setApprovalStatus(approvalStatus);
				overtimeLate.setId(Long.valueOf(ruProcdef.getEntityId()));
				overtimeLate.setNodeCode(ruProcdef.getNodeCode());
				overtimeLate.setApprovalReason("审批通过");
				overtimeLate.setRuProcdefId(ruProcdef.getId());
				empApplicationOvertimeLateService.updateById(overtimeLate, user);
			}else{
				logger.error(user.getEmployee().getCnName()+":晚到审批数据不存在！");
			}
		} else if (RunTask.RUN_CODE_90.equals(ruProcdef.getReProcdefCode())) { 
			//出差总结报告
			EmpApplicationBusiness old = new EmpApplicationBusiness();
			old.setApprovalStatus(approvalStatus);
			old.setId(Long.valueOf(ruProcdef.getEntityId()));
			old.setNodeCode(ruProcdef.getNodeCode());
			old.setApprovalReason("审批通过");
			old.setRuProcdefId(ruProcdef.getId());
			empApplicationBusinessService.updateById(old, "2");
		}
	}
	
	public double getHours(EmpApplicationAbnormalAttendance old,Employee employee,String workType){
		double hours = 0;
		EmployeeClass employeeClass = new EmployeeClass(); 
		employeeClass.setEmployId(employee.getId());
		employeeClass.setClassDate(old.getAbnormalDate());
		employeeClass = employeeClassService.getEmployeeClassSetting(employeeClass);
		if(employeeClass!=null){
			String fmtS = DateUtils.format(old.getAbnormalDate(), "yyyy-MM-dd") + " " +  DateUtils.format(employeeClass.getStartTime(), "HH:mm:ss");
			String fmtE = DateUtils.format(old.getAbnormalDate(), "yyyy-MM-dd") + " " +  DateUtils.format(employeeClass.getEndTime(), "HH:mm:ss");
			Date ecStartTime = DateUtils.addMinute(DateUtils.parse(fmtS), 5);
			Date ecEndTime = DateUtils.parse(fmtE);
			//开始时间小于结束时间
			int startWorkTime = DateUtils.compareDate(ecStartTime, ecEndTime);
			if(startWorkTime == 1) {
				ecEndTime = DateUtils.addDay(ecEndTime, 1);
				employeeClass.setEndTime(ecEndTime);
			}
			
			String fmtS1 = DateUtils.format(old.getAbnormalDate(), "yyyy-MM-dd") + " " +  DateUtils.format(old.getStartTime(), "HH:mm:ss");
			String fmtE1 = DateUtils.format(old.getAbnormalDate(), "yyyy-MM-dd") + " " +  DateUtils.format(old.getEndTime(), "HH:mm:ss");
			Date ecStartTime1 = DateUtils.addMinute(DateUtils.parse(fmtS1), -5);
			Date ecEndTime1 = DateUtils.parse(fmtE1);
			//开始时间小于结束时间
			int startWorkTime1 = DateUtils.compareDate(ecStartTime1, ecEndTime1);
			if(startWorkTime1 == 1) {
				ecEndTime1 = DateUtils.addDay(ecEndTime1, 1);
				old.setEndTime(ecEndTime1);
			}
			
			if(ecStartTime1.getTime()<DateUtils.parse(fmtS).getTime()){
				old.setStartTime(DateUtils.parse(fmtS));
			}
			if("standard".equals(workType)){
				if(old.getEndTime().getTime()>ecEndTime.getTime()){
					old.setEndTime(ecEndTime);
				}
			}
		}
		hours =  ((double)old.getEndTime().getTime()-(double)old.getStartTime().getTime())/(1800*1000)/2;
		if(hours>=5&&hours<10){
			hours = hours-1;
		}
		if(hours>10){
			hours = hours-2;
		}
		return hours;
	}
	
	@RequestMapping("/index.htm")
	public ModelAndView index(HttpServletRequest request, String urlType){
		
    	request.setAttribute("returnUrl", ReturnUrlUtil.getReturnUrl(urlType));
		return new ModelAndView("base/apply/index");
	}
	
	/**
	 * 我的协作-跳转已处理页面
	 * @return
	 */
	@RequestMapping("/runTask_processed.htm")
	public ModelAndView runTask_processed(){
		return new ModelAndView("base/cooperation/runTask_processed");
	}
	
	/**
	 * 我的协作-跳转已完成页面
	 * @return
	 */
	@RequestMapping("/runTask_done.htm")
	public ModelAndView runTask_done(){
		return new ModelAndView("base/cooperation/runTask_done");
	}
	
	/**
	 * 我的协作-分页查询已处理已完成列表
	 * @param runTask
	 * @param flag flag=1是已处理,flag=2是已完成
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getRunPageList.htm")
	public PageModel<RunTask> getRunPageList(RunTask runTask, String flag, String pageNo, String pageSize){
		PageModel<RunTask> pm = new PageModel<RunTask>();
		pm.setRows(new java.util.ArrayList<RunTask>());
		pm.setTotal(0);
		pm.setPageNo(1);
		//flag=1是已处理, flag=2是已完成
		if("1".equals(flag)) {
			runTask.setProcessStatus(RunTask.PROCESS_STATUS_100);
		} else {
			runTask.setProcessStatus(null);
		}
		//获取当前登录人
		User user = userService.getCurrentUser();
		logger.info("getRunPageList已办："+user.getEmployeeId());
		runTask.setAssigneeId(user.getEmployee().getId()+"");
		runTask.setPage(pageNo == null ? 1 : Integer.valueOf(pageNo));
		runTask.setRows(pageSize == null ? 11 : Integer.valueOf(pageSize));
		try {
			pm = runTaskService.getByPagenation(runTask);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return pm;
	}
	
	/**
	 * 我的申请-分页查询列表
	 * @param runTask
	 * @param flag flag=0是全部,flag=1是处理中,flag=2是已完成,flag=3是已撤回
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getApplyPageList.htm")
	public PageModel<RunTask> getApplyPageList(RunTask runTask, String flag, Integer pageNo, Integer pageSize){
		runTask.setOffset(pageNo*pageSize);
		runTask.setLimit(pageSize);
		PageModel<RunTask> pm = new PageModel<RunTask>();
		pm.setRows(new java.util.ArrayList<RunTask>());
		pm.setTotal(0);
		pm.setPageNo(1);
		if("1".equals(flag)) {
			runTask.setProcessStatus(RunTask.PROCESS_STATUS_100);
		} else if("2".equals(flag)){
			List<Long> proList = new ArrayList<Long>();
			proList.add(RunTask.PROCESS_STATUS_200);
			proList.add(RunTask.PROCESS_STATUS_300);
			runTask.setProcessStatuss(proList);
		} else if("3".equals(flag)){
			runTask.setProcessStatus(RunTask.PROCESS_STATUS_400);
		} else {
			runTask.setProcessStatus(null);
		}
		//获取当前登录人
		User user = userService.getCurrentUser();
		logger.info("我的申请getApplyPageList："+user.getEmployeeId());
		runTask.setCreatorId(user.getEmployee().getId());
		try {
			pm = runTaskService.getApplyByPagenation(runTask);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return pm;
	}
	
	/**
	 * 全部审批页面
	 * @return
	 */
	@RequestMapping("/allExamine.htm")
	public ModelAndView allExamine(HttpServletRequest request, String urlType){
		request.setAttribute("returnUrl", ReturnUrlUtil.getReturnUrl(urlType));
		return new ModelAndView("base/cooperation/allExamine");
	}
	
	@ResponseBody
	@RequestMapping("/allExaminePageList.htm")
	public PageModel<RunTask> allExaminePageList(RunTask runTask,Integer pageNo, Integer pageSize, Long partId, String nameOrCode, String examineDate,String examineDate1) {
		runTask.setOffset(pageNo*pageSize);
		runTask.setLimit(pageSize);
		PageModel<RunTask> pm = new PageModel<RunTask>();
		pm.setRows(new java.util.ArrayList<RunTask>());
		if(partId != null) {
			runTask.setDepartId(partId);	
		}
		if(nameOrCode != null && !nameOrCode.equals("")) {
			runTask.setCnName(nameOrCode);
		}
		if(examineDate != null && !examineDate.equals("") && !"开始日期".equals(examineDate)) {
			runTask.setCreateTimeStr(examineDate);
		}
		if(examineDate1 != null && !examineDate1.equals("") && !"结束日期".equals(examineDate1)) {
			runTask.setCreateTimeStr1(examineDate1);
		}
		try {
			pm = runTaskService.allExaminePageList(runTask);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return pm;
	}
}
