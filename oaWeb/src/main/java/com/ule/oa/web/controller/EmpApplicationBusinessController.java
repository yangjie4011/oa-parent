package com.ule.oa.web.controller;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.flowable.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.ule.oa.base.po.EmpApplicationBusiness;
import com.ule.oa.base.po.EmpApplicationBusinessDetail;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.HiActinst;
import com.ule.oa.base.po.RunTask;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmpApplicationBusinessService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.HiActinstService;
import com.ule.oa.base.service.RunTaskService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.service.impl.ActivitiServiceImpl;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.RedisUtils;
import com.ule.oa.common.utils.UUIDGenerator;
import com.ule.oa.web.util.ReturnUrlUtil;
import com.ule.oa.web.util.Token;

/**
 * @ClassName: 出差申请表
 * @Description: 出差申请表
 * @author yangjie
 * @date 2017年6月13日
 */
@Controller
@RequestMapping("empApplicationBusiness")
public class EmpApplicationBusinessController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private UserService userService;
	@Autowired
	private EmpApplicationBusinessService empApplicationBusinessService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private HiActinstService hiActinstService;
	@Autowired
	private RunTaskService runTaskService;
	@Autowired
	private DepartService departService;
	@Autowired
	private ActivitiServiceImpl activitiServiceImpl; 
	
	//出差申请首页
	@RequestMapping("/index.htm")
	@Token(generate=true)//生成token
	public String index(HttpServletRequest request, String urlType){
		User user = userService.getCurrentUser();
		try {
			request.setAttribute("returnUrl", ReturnUrlUtil.getReturnUrl(urlType));
		} catch (Exception e) {
			logger.error(user.getEmployee().getCnName()+"出差-首页："+e.toString());
			return "base/empApplicationBusiness/index";
		}
		return "base/empApplicationBusiness/index";
	}
	
	//保存
	@ResponseBody
	@RequestMapping("/save.htm")
	@Token(remove = true)//保存完信息以后需要移除token
	public Map<String, Object> save(HttpServletRequest request){
		User user = userService.getCurrentUser();
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			map = empApplicationBusinessService.save(request,user);
		}catch(Exception e){
			logger.error(user.getEmployee().getCnName()+"出差-提交："+e.toString());
			String uuId = UUIDGenerator.generate();
            try {
				RedisUtils.setString(String.valueOf(user.getEmployee().getId()),uuId,1800000);
				map.put("token", uuId);
			} catch (Exception e1) {
				logger.error(user.getEmployee().getCnName()+"出差申请："+e1.toString());
			}
			map.put("repeatFlag", false);
			map.put("message", e.getMessage());
			map.put("success",false);
			return map;
		}
		return map;
	}
	//保存
	@ResponseBody
	@RequestMapping("/update.htm")
	@Token(remove = true)//保存完信息以后需要移除token
	public Map<String, Object> update(HttpServletRequest request){
		User user = userService.getCurrentUser();
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			map = empApplicationBusinessService.update(request,user);
		}catch(Exception e){
			logger.error(user.getEmployee().getCnName()+"出差-修改："+e.toString());
			map.put("repeatFlag", false);
			map.put("message", e.getMessage());
			map.put("success",false);
			return map;
		}
		return map;
	}
	
	//出差申请审批页
	@RequestMapping("/approval.htm")
	@Token(generate=true)//生成token
	public String approval(HttpServletRequest request,Long businessId,String flag,String urlType){
		User user = userService.getCurrentUser();
		try {
			EmpApplicationBusiness business  = empApplicationBusinessService.getById(businessId);
			request.setAttribute("business", business);
			List<EmpApplicationBusinessDetail> detailList = empApplicationBusinessService.getBusinessDetailList(businessId);
			request.setAttribute("detailList", detailList);
			request.setAttribute("returnUrl", ReturnUrlUtil.getReturnUrl(urlType));
			request.setAttribute("isSelf", false);
			request.setAttribute("canApprove", false);
			if(business.getEmployeeId().longValue()==user.getEmployeeId().longValue()){
				request.setAttribute("isSelf", true);
			}
			//审批显示
			if(business.getApprovalStatus().intValue()==100) {
				//办理中,taskService查询任务
				Task task = activitiServiceImpl.queryTaskByProcessInstanceId(business.getProcessinstanceId());
				request.setAttribute("actName", task.getName());
				if(StringUtils.equalsIgnoreCase("can", flag)) {
					request.setAttribute("canApprove", true);
				}
			}
		} catch (Exception e) {
			logger.error(user.getEmployee().getCnName()+"出差-审批首页："+e.toString());
			return "base/empApplicationBusiness/approval";
		}
		return "base/empApplicationBusiness/approval";
	}
	
	//出差审批流程页面
	@RequestMapping("/process.htm")
	public String process(HttpServletRequest request, Long businessId,Long ruProcdefId){
		EmpApplicationBusiness business  = empApplicationBusinessService.getById(businessId);
		request.setAttribute("business", business);
		RunTask param = new RunTask();
		param.setReProcdefCode(RunTask.RUN_CODE_80);
		boolean isDh = departService.checkLeaderIsDh(business.getEmployeeId());
		request.setAttribute("isDh", isDh);
		if(isDh){
			param.setReProcdefCode(RunTask.RUN_CODE_81);
		}
		param.setEntityId(String.valueOf(businessId));
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
		return "base/empApplicationBusiness/process";
	}
	
	
	//出差总结报告首页
	@RequestMapping("/workSummary.htm")
	@Token(generate=true)//生成token
	public String workSummary(HttpServletRequest request,Long businessId,String urlType){
		User user = userService.getCurrentUser();
		try {
			EmpApplicationBusiness business  = empApplicationBusinessService.getById(businessId);
			request.setAttribute("business", business);
			List<EmpApplicationBusinessDetail> detailList = empApplicationBusinessService.getBusinessDetailList(businessId);
			request.setAttribute("detailList", detailList);
			request.setAttribute("returnUrl", ReturnUrlUtil.getReturnUrl(urlType));
		} catch (Exception e) {
			logger.error(user.getEmployee().getCnName()+"出差报告-首页："+e.toString());
			return "base/empApplicationBusiness/workSummary";
		}
		return "base/empApplicationBusiness/workSummary";
	}
	
	//提交出差总结报告
	@ResponseBody
	@RequestMapping("/saveWorkSummary.htm")
	@Token(remove = true)
	public Map<String, Object> saveWorkSummary(HttpServletRequest request){
		User user = userService.getCurrentUser();
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			map = empApplicationBusinessService.saveWorkSummary(request,user);
		}catch(Exception e){
			logger.error(user.getEmployee().getCnName()+"出差报告-提交："+e.toString());
			String uuId = UUIDGenerator.generate();
            try {
				RedisUtils.setString(String.valueOf(user.getEmployee().getId()),uuId,1800000);
				map.put("token", uuId);
			} catch (Exception e1) {
				logger.error("提交出差总结报告："+e1.toString());
			}
			map.put("repeatFlag", false);
			map.put("message", e.getMessage());
			map.put("success",false);
			return map;
		}
		return map;
	}
		
	//出差审批页
	@RequestMapping("/approveWorkSummary.htm")
	@Token(generate=true)//生成token
	public String approveWorkSummary(HttpServletRequest request,Long businessId,String flag, String urlType){
		User user = userService.getCurrentUser();
		try {
			
			if(user==null||user.getEmployee()==null) {
				 return "security/login";
			}
			
			request.setAttribute("imgUrl",(null != user && user.getEmployee().getPicture()!=null&&!"".equals(user.getEmployee().getPicture()))?user.getEmployee().getPicture():ConfigConstants.HEAD_PICTURE_URL);
			EmpApplicationBusiness business  = empApplicationBusinessService.getById(businessId);
			if(business.getApprovalReportStatus().intValue() == 0) {
				return "forward:/empApplicationBusiness/workSummary.htm?businessId="+businessId+"&urlType="+urlType;
			}
			request.setAttribute("business", business);
			List<EmpApplicationBusinessDetail> detailList = empApplicationBusinessService.getBusinessDetailList(businessId);
			request.setAttribute("detailList", detailList);
			request.setAttribute("returnUrl", ReturnUrlUtil.getReturnUrl(urlType));
			request.setAttribute("isSelf", false);
			request.setAttribute("canApprove", false);
			if(business.getEmployeeId().longValue()==user.getEmployeeId().longValue()){
				request.setAttribute("isSelf", true);
			}
			//审批显示
			if(business.getApprovalReportStatus().intValue()==100||business.getApprovalReportStatus().intValue()==0) {
				//办理中,taskService查询任务
				Task task = activitiServiceImpl.queryTaskByProcessInstanceId(business.getProcessinstanceReportId());
				request.setAttribute("actName", task.getName());
				if(StringUtils.equalsIgnoreCase("can", flag)) {
					request.setAttribute("canApprove", true);
				}
			}
		} catch (Exception e) {
			logger.error(user.getEmployee().getCnName()!=null?user.getEmployee().getCnName():""+"出差报告-审批首页："+e.toString());
			return "base/empApplicationBusiness/approveWorkSummary";
		}
		return "base/empApplicationBusiness/approveWorkSummary";
	}
	
	//出差审批流程页面
	@RequestMapping("/processWorkSummary.htm")
	public String processWorkSummary(HttpServletRequest request, Long businessId,Long ruProcdefId){
		EmpApplicationBusiness business  = empApplicationBusinessService.getById(businessId);
		request.setAttribute("business", business);
		RunTask param = new RunTask();
		param.setReProcdefCode(RunTask.RUN_CODE_90);
		boolean isDh = departService.checkLeaderIsDh(business.getEmployeeId());
		if(isDh){
			param.setReProcdefCode(RunTask.RUN_CODE_91);
		}
		param.setEntityId(String.valueOf(businessId));
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
		return "base/empApplicationBusiness/processWorkSummary";
	}
	
	//通过
	@ResponseBody
	@RequestMapping("/passWorkSummary.htm")
	@Token(remove = true)
	public Map<String, Object> passWorkSummary(HttpServletRequest request,EmpApplicationBusiness business){
		User user = userService.getCurrentUser();
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			business.setUpdateTime(new Date());
			business.setUpdateUser(user.getEmployee().getCnName());
			business.setApprovalStatus(EmpApplicationBusiness.APPROVAL_STATUS_YES);
			map = empApplicationBusinessService.updateById(business,"2");
		}catch(Exception e){
			logger.error(user.getEmployee().getCnName()+"出差报告-审批通过："+e.toString());
			map.put("repeatFlag", false);
			map.put("message", e.getMessage());
			map.put("success",false);
			return map;
		}
		return map;
	}
		
	//拒绝
	@ResponseBody
	@RequestMapping("/refuseWorkSummary.htm")
	@Token(remove = true)
	public Map<String, Object> refuseWorkSummary(HttpServletRequest request,EmpApplicationBusiness business){
		User user = userService.getCurrentUser();
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			business.setUpdateTime(new Date());
			business.setUpdateUser(user.getEmployee().getCnName());
			business.setApprovalStatus(EmpApplicationBusiness.APPROVAL_STATUS_NO);
			map = empApplicationBusinessService.updateById(business,"2");
		}catch(Exception e){
			logger.error(user.getEmployee().getCnName()+"出差报告-审批拒绝："+e.toString());
			map.put("repeatFlag", false);
			map.put("message", e.getMessage());
			map.put("success",false);
			return map;
		}
		return map;
	}
	
	//出差导出
	@ResponseBody
	@RequestMapping("/exportBusinessPdf.htm")
	@Token(remove = true)
	public Map<String, Object> exportBusinessPdf(HttpServletRequest request,EmpApplicationBusiness business){
		User user = userService.getCurrentUser();
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			empApplicationBusinessService.exportApplyResultByBusinessId(business.getId(),business.getRuProcdefId());
			map.put("success", true);
		}catch(Exception e){
			if(e.getCause() instanceof MailSendException || e.getCause() instanceof MessagingException || e.getCause() instanceof SocketException){
				map.put("message", "邮件系统繁忙，请重试");
			}else{
				map.put("message", "导出失败，请稍后重试！");
			}
			String uuId = UUIDGenerator.generate();
            try {
				RedisUtils.setString(String.valueOf(user.getEmployee().getId()),uuId,1800000);
				map.put("token", uuId);
			} catch (Exception e1) {
				logger.error("出差打印："+e1.toString());
			}
			logger.error(user.getEmployee().getCnName()+"出差报告-审批拒绝："+e.toString());
			map.put("repeatFlag", false);
			map.put("success",false);
			return map;
		}
		return map;
   }
	
	//出差报告导出
	@ResponseBody
	@RequestMapping("/exportBusinessReportPdf.htm")
	@Token(remove = true)
	public Map<String, Object> exportBusinessReportPdf(HttpServletRequest request,EmpApplicationBusiness business){
		User user = userService.getCurrentUser();
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			empApplicationBusinessService.exportApplyReportByBusinessId(business.getId(),business.getRuProcdefId());
			map.put("success", true);
		}catch(Exception e){
			if(e.getCause() instanceof MailSendException || e.getCause() instanceof MessagingException || e.getCause() instanceof SocketException){
				map.put("message", "邮件系统繁忙，请重试");
			}else{
				map.put("message", "导出失败，请稍后重试！");
			}
			String uuId = UUIDGenerator.generate();
            try {
				RedisUtils.setString(String.valueOf(user.getEmployee().getId()),uuId,1800000);
				map.put("token", uuId);
			} catch (Exception e1) {
				logger.error("出差总结报告打印："+e1.toString());
			}
			logger.error(user.getEmployee().getCnName()+"出差报告-审批拒绝："+e.toString());
			map.put("repeatFlag", false);
			map.put("success",false);
			return map;
		}
		return map;
   }
	//出差销假申请首页
	@RequestMapping("/businessBack.htm")
	@Token(generate=true)//生成token
	public String index(HttpServletRequest request, Long businessId, String urlType){
		User user = userService.getCurrentUser();
		try {
			
			if(user==null||user.getEmployee()==null) {
				 return "security/login";
			}
			
			request.setAttribute("imgUrl",(null != user && user.getEmployee().getPicture()!=null&&!"".equals(user.getEmployee().getPicture()))?user.getEmployee().getPicture():ConfigConstants.HEAD_PICTURE_URL);
			EmpApplicationBusiness business  = empApplicationBusinessService.getById(businessId);
			String address = business.getAddress();
			String[] splitAddress = address.split("-");
			List<String> cityList=new ArrayList<String>();
			/*for (int i = 1; i < splitAddress.length-1; i++) {
				String string = splitAddress[i];
				cityList.add(business.getTravelCity+i());
			}*/
			
			request.setAttribute("business", business);
			List<EmpApplicationBusinessDetail> detailList = empApplicationBusinessService.getBusinessDetailList(businessId);
			request.setAttribute("detailList", detailList);
			request.setAttribute("returnUrl", ReturnUrlUtil.getReturnUrl(urlType));
			request.setAttribute("isSelf", false);
			request.setAttribute("canApprove", false);
			if(business.getEmployeeId().longValue()==user.getEmployeeId().longValue()){
				request.setAttribute("isSelf", true);
			}
			//审批显示
			if(business.getApprovalStatus().intValue()==100) {
				//办理中,taskService查询任务
				Task task = activitiServiceImpl.queryTaskByProcessInstanceId(business.getProcessinstanceId());
				request.setAttribute("actName", task.getName());
				/*if(StringUtils.equalsIgnoreCase("can", flag)) {
					request.setAttribute("canApprove", true);
				}*/
			}
		} catch (Exception e) {
			logger.error(user.getEmployee().getCnName()!=null?user.getEmployee().getCnName():""+"出差-审批首页："+e.toString());
			return "base/empApplicationBusiness/approvalBack";
		}
		return "base/empApplicationBusiness/approvalBack";
	}	
	//根据 出差id查询出差详情表
	@ResponseBody
	@RequestMapping("/getBusinessDetail.htm")
	public Map<String,Object> getBusinessDetail(Long businessId){
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			List<EmpApplicationBusinessDetail> businessDetailList = empApplicationBusinessService.getBusinessDetailList(businessId);
			map.put("data", businessDetailList);
			map.put("success", true);
		}catch(Exception e){
			map.put("success",false);
		}
	return map;
	}
	
	// 出差提示页面
	@RequestMapping("/businessRule.htm")
	public String businessRule(HttpServletRequest request) {
		return "base/empApplicationBusiness/businessRule";
	}
	
	@RequestMapping("/completeTaskBySystem.htm")
	@ResponseBody
	public Map<String,Object> completeTaskBySystem(HttpServletRequest request,String processId,String message){
		Map<String,Object> result = Maps.newHashMap();
		try {
			empApplicationBusinessService.completeTaskBySystem(request,processId,message,ConfigConstants.PASS);
			result.put("success", true);
		}catch (Exception e) {
			result.put("success", false);
			result.put("message", e.getMessage());
		}
		return result;
	}
}
