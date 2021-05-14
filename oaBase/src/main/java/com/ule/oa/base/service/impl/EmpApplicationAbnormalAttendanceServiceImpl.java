package com.ule.oa.base.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.flowable.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.google.common.collect.Maps;
import com.ule.oa.base.mapper.EmpApplicationAbnormalAttendanceMapper;
import com.ule.oa.base.mapper.RabcUserMapper;
import com.ule.oa.base.po.AttnSignRecord;
import com.ule.oa.base.po.EmpApplicationAbnormalAttendance;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.po.RunTask;
import com.ule.oa.base.po.User;
import com.ule.oa.base.po.tbl.ViewTaskInfoTbl;
import com.ule.oa.base.service.AnnualVacationService;
import com.ule.oa.base.service.AttnSignRecordService;
import com.ule.oa.base.service.CompanyConfigService;
import com.ule.oa.base.service.EmpApplicationAbnormalAttendanceService;
import com.ule.oa.base.service.EmployeeClassService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.SendMailService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.service.ViewTaskInfoService;
import com.ule.oa.base.util.AttnStatisticsUtil;
import com.ule.oa.base.vo.TaskVO;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.ExcelUtil;
import com.ule.oa.common.utils.PageModel;
import com.ule.oa.common.utils.http.IPUtils;

/**
 * @ClassName: 考勤异常消除申请
 * @Description: 考勤异常消除申请
 * @author yangjie
 * @date 2017年6月15日
 */
@Service
public class EmpApplicationAbnormalAttendanceServiceImpl implements
		EmpApplicationAbnormalAttendanceService {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private EmpApplicationAbnormalAttendanceMapper empApplicationAbnormalAttendanceMapper;
	@Autowired
	private UserService userService;
	@Autowired
	private AttnSignRecordService attnSignRecordService;
	@Autowired
	private CompanyConfigService companyConfigService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private EmployeeClassService employeeClassService;
	@Autowired
	private ActivitiServiceImpl activitiServiceImpl;
	@Autowired
	private AnnualVacationService annualVacationService;
	@Autowired
	private SendMailService sendMailService;
	@Autowired
	private ViewTaskInfoService viewTaskInfoService;
	@Autowired
	private RabcUserMapper rabcUserMapper;
	
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String,Object> save(EmpApplicationAbnormalAttendance attendance) throws Exception{
		Map<String,Object> map = new HashMap<String, Object>();
		User user = userService.getCurrentUser();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(attendance.getAbnormalDate());
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date monthStart = calendar.getTime();  
		attendance.setMonthStart(monthStart);
		calendar.add(Calendar.MONTH, 1);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		Date monthEnd = calendar.getTime();
		attendance.setMonthEnd(monthEnd);
		List<EmpApplicationAbnormalAttendance> list = empApplicationAbnormalAttendanceMapper.getListByMonth(attendance);
		if(list!=null&&list.size()>=2){
			if(attendance.getApplyType().intValue()==0){
				throw new OaException("每月只能提交两次申请！");
			}
		}
		//开启异常考勤流程
		Map<String,Object> variables= Maps.newHashMap();
		if(attendance.getApplyType().intValue()==1) {
			variables.put("isSelf", false);
		}else {
			variables.put("isSelf", true);
		}
		String processInstanceId = activitiServiceImpl.startByKey(ConfigConstants.ABNORMALATTENDANCE_KEY,variables);
		//-----------------start-----------------------保存流程节点信息
		ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
		flow.setAssigneeName(user.getEmployee().getCnName());
		flow.setDepartName(user.getDepart().getName());
		flow.setPositionName(user.getPosition().getPositionName());
		flow.setFinishTime(new Date());
		flow.setComment(attendance.getReason());
		flow.setProcessId(processInstanceId);
		flow.setProcessKey(activitiServiceImpl.queryProcessByInstanceId(processInstanceId).getKey());
		flow.setStatu(0);
		viewTaskInfoService.save(flow);
		//-----------------end-------------------------
		attendance.setProcessInstanceId(processInstanceId);
		empApplicationAbnormalAttendanceMapper.save(attendance);
		map.put("success", true);
		return map;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String,Object> updateById(EmpApplicationAbnormalAttendance attendance) throws Exception{
		Map<String,Object> map = new HashMap<String, Object>();
		User user = userService.getCurrentUser();
		EmpApplicationAbnormalAttendance old = empApplicationAbnormalAttendanceMapper.getById(attendance.getId());
		if(attendance.getApprovalStatus() != null && (!attendance.getApprovalStatus().equals(ConfigConstants.PASS_STATUS))||(!attendance.getApprovalStatus().equals(ConfigConstants.OVERDUEPASS_STATUS))) {
			empApplicationAbnormalAttendanceMapper.updateById(attendance);
		}
		
		if(attendance.getApprovalStatus().equals(ConfigConstants.PASS_STATUS) || attendance.getApprovalStatus().equals(ConfigConstants.OVERDUEPASS_STATUS)) {
			//异常考勤流程同意结束
				if(attendance.getApprovalStatus() != null && (attendance.getApprovalStatus().equals(ConfigConstants.PASS_STATUS)) || (attendance.getApprovalStatus().equals(ConfigConstants.OVERDUEPASS_STATUS))) {
					empApplicationAbnormalAttendanceMapper.updateById(attendance);
				}
				//充时间
				Employee employee = employeeService.getById(old.getEmployeeId());
			    //判断是否为综合工时
			    String workType = companyConfigService.getWorkTypeByEmployeeId(old.getEmployeeId());
			    //获取异常考勤小时数
			    double hours = getHours(old,employee,workType);
			    AttnStatisticsUtil attnStatisticsUtil= new AttnStatisticsUtil();
			    attnStatisticsUtil.calWorkHours(employee.getCompanyId(), employee.getId(), user.getEmployee().getCnName(),old.getAbnormalDate(),hours, RunTask.RUN_CODE_40,old.getStartTime(),old.getEndTime(),null,old.getReason(),old.getId());
			    //充打卡记录
			    AttnSignRecord sSignRecord = new AttnSignRecord();
			    sSignRecord.setCompanyId(user.getEmployee().getCompanyId());
			    sSignRecord.setEmployeeId(old.getEmployeeId());
			    sSignRecord.setEmployeeName(old.getCnName());
			    sSignRecord.setSignTime(old.getStartTime());
			    sSignRecord.setAttnRecordId(0L);
			    sSignRecord.setCreateTime(new Date());
			    sSignRecord.setCreateUser(user.getEmployee().getCnName());
			    sSignRecord.setDelFlag(0);
			    sSignRecord.setType(1);
			    attnSignRecordService.save(sSignRecord);
			    AttnSignRecord eSignRecord = new AttnSignRecord();
			    eSignRecord.setCompanyId(user.getEmployee().getCompanyId());
			    eSignRecord.setEmployeeId(old.getEmployeeId());
			    eSignRecord.setEmployeeName(old.getCnName());
			    eSignRecord.setSignTime(old.getEndTime());
			    eSignRecord.setAttnRecordId(0L);
			    eSignRecord.setCreateTime(new Date());
			    eSignRecord.setCreateUser(user.getEmployee().getCnName());
			    eSignRecord.setDelFlag(0);
			    eSignRecord.setType(1);
			    attnSignRecordService.save(eSignRecord);
		} 
		map.put("success", true);
		return map;
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
			
		}
		hours = (double) ((old.getEndTime().getTime()*1.0-old.getStartTime().getTime())/(1800*1000))/2;
		if(hours>=5&&hours<10){
			hours = hours-1;
		}
		if(hours>10){
			hours = hours-2;
		}
		return hours;
	}

	@Override
	public EmpApplicationAbnormalAttendance getById(Long id) {
		return empApplicationAbnormalAttendanceMapper.getById(id);
	}

	@Override
	public int getEaoByEmpAndDateCount(
			EmpApplicationAbnormalAttendance attendance) {
		return empApplicationAbnormalAttendanceMapper.getEaoByEmpAndDateCount(attendance);
	}

	@Override
	public List<EmpApplicationAbnormalAttendance> getListByMonth(
			EmpApplicationAbnormalAttendance attendance) {
		return empApplicationAbnormalAttendanceMapper.getListByMonth(attendance);
	}

	@Override
	public PageModel<EmpApplicationAbnormalAttendance> getReportPageList(
			EmpApplicationAbnormalAttendance attendance) {
		int page = attendance.getPage() == null ? 0 : attendance.getPage();
		int rows = attendance.getRows() == null ? 0 : attendance.getRows();
		
		PageModel<EmpApplicationAbnormalAttendance> pm = new PageModel<EmpApplicationAbnormalAttendance>();
		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
		if(deptDataByUserList==null || deptDataByUserList.size()==0){
			pm.setPageNo(0);
			pm.setPageSize(0);
			pm.setTotal(0);
			attendance.setOffset(pm.getOffset());
			attendance.setLimit(pm.getLimit());
			pm.setRows(new ArrayList<EmpApplicationAbnormalAttendance>());
			return pm;
		}else{
			attendance.setCurrentUserDepart(deptDataByUserList);
			List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(currentUser.getEmployeeId());//下属员工
			if(subEmployeeIdList!=null&&subEmployeeIdList.size()>0){
				attendance.setSubEmployeeIdList(subEmployeeIdList);
			}
			pm.setPageNo(page);
			pm.setPageSize(rows);
			Integer total = empApplicationAbnormalAttendanceMapper.getReportCount(attendance);
			pm.setTotal(total);
			
			attendance.setOffset(pm.getOffset());
			attendance.setLimit(pm.getLimit());
			
			List<EmpApplicationAbnormalAttendance> roles = empApplicationAbnormalAttendanceMapper.getReportPageList(attendance);
			//查询审核人
			List<String> processIdList = new ArrayList<String>();
			for(EmpApplicationAbnormalAttendance og:roles) {
				processIdList.add(og.getProcessInstanceId());
			}
			List<ViewTaskInfoTbl> auditUserList = new ArrayList<ViewTaskInfoTbl>();
			LinkedHashMap<String, List<ViewTaskInfoTbl>> auditUsermap = new LinkedHashMap<String, List<ViewTaskInfoTbl>>();
			if(processIdList!=null && processIdList.size()>0) {
				auditUserList = viewTaskInfoService.queryTasksByProcessIdList(processIdList);
			}
			auditUsermap = auditUserList.stream().sorted(Comparator.comparing(ViewTaskInfoTbl::getId)).collect(Collectors.groupingBy(ViewTaskInfoTbl::getProcessId, LinkedHashMap::new, Collectors.toList()));
			
			for(EmpApplicationAbnormalAttendance og:roles){
				try{
					String auditUser = (auditUsermap!=null&&auditUsermap.containsKey(og.getProcessInstanceId()))?getAttnAuditUser(auditUsermap.get(og.getProcessInstanceId()),og.getApplyType()):"";
					og.setAuditUser(auditUser);
				}catch(Exception e){
					og.setAuditUser("");
				}
			}
			pm.setRows(roles);
			return pm;
		}	
	}
	
	public String getAttnAuditUser(List<ViewTaskInfoTbl> list,int attnType) {
		String auditUser = "";

		if(attnType==1){//下属异常考勤  人事审批人
			if(list!=null&&list.size()==2){
				auditUser = list.get(1).getAssigneeName();
			}
		}else{			//异常考勤	   人事审批人
			if(list!=null&&list.size()>2){
				auditUser = list.get(2).getAssigneeName();
			}
		}
		return auditUser;
	}

	@Override
	public HSSFWorkbook exportExcel(List<EmpApplicationAbnormalAttendance> list) {
		List<Map<String,Object>> sMapList = new ArrayList<Map<String,Object>>();
		if(list!=null&&list.size()>0) {
			
			//查询审核人
			List<String> processIdList = new ArrayList<String>();
			for(EmpApplicationAbnormalAttendance og:list) {
				processIdList.add(og.getProcessInstanceId());
			}
			List<ViewTaskInfoTbl> auditUserList = viewTaskInfoService.queryTasksByProcessIdList(processIdList);
			LinkedHashMap<String, List<ViewTaskInfoTbl>> auditUsermap = auditUserList.stream().sorted(Comparator.comparing(ViewTaskInfoTbl::getId)).collect(Collectors.groupingBy(ViewTaskInfoTbl::getProcessId, LinkedHashMap::new, Collectors.toList()));
			
			for(EmpApplicationAbnormalAttendance og:list){
				try{
					String auditUser = (auditUsermap!=null&&auditUsermap.containsKey(og.getProcessInstanceId()))?getAttnAuditUser(auditUsermap.get(og.getProcessInstanceId()),og.getApplyType()):"";
					og.setAuditUser(auditUser);
				}catch(Exception e){
					og.setAuditUser(" ");
				}
			}
			for (EmpApplicationAbnormalAttendance attn : list) {
				//封装数据
				Map<String,Object> sdoMap = new HashMap<String, Object>();
				sdoMap.put("code",attn.getCode());
				sdoMap.put("cnName",attn.getCnName());
				sdoMap.put("departName",attn.getDepartName());
				sdoMap.put("positionName",attn.getPositionName());
				sdoMap.put("workType1",attn.getWorkType1());
				sdoMap.put("abnormalDate",DateUtils.format(attn.getAbnormalDate(), DateUtils.FORMAT_SHORT));
				if(attn.getStartPunchTime()!=null){
					sdoMap.put("startPunchTime",DateUtils.format(attn.getStartPunchTime(), DateUtils.FORMAT_LONG));
				}else{
					sdoMap.put("startPunchTime","");
				}
				if(attn.getEndPunchTime()!=null){
					sdoMap.put("endPunchTime",DateUtils.format(attn.getEndPunchTime(), DateUtils.FORMAT_LONG));
				}else{
					sdoMap.put("endPunchTime","");
				}
				//上班考勤结果
				sdoMap.put("startResult",attn.getStartResult());
				//下班考勤结果
				sdoMap.put("endResult",attn.getEndResult());
				sdoMap.put("startTime",DateUtils.format(attn.getStartTime(), DateUtils.FORMAT_LONG));
				sdoMap.put("endTime",DateUtils.format(attn.getEndTime(), DateUtils.FORMAT_LONG));
				sdoMap.put("reason",attn.getReason());
				sdoMap.put("auditUser",attn.getAuditUser());
				sdoMap.put("submitDate",DateUtils.format(attn.getSubmitDate(), DateUtils.FORMAT_LONG));
				String approvalStatus = "";
				if(attn.getApprovalStatus().intValue()==100){
					approvalStatus = "待审批";
				}else if(attn.getApprovalStatus().intValue()==200){
					approvalStatus = "已审批";
				}else if(attn.getApprovalStatus().intValue()==300){
					approvalStatus = "已拒绝";
				}else if(attn.getApprovalStatus().intValue()==400){
					approvalStatus = "已撤销";
				}else if(attn.getApprovalStatus().intValue()==500){
					approvalStatus = "失效";
				}else if(attn.getApprovalStatus().intValue()==600){
					approvalStatus = "失效同意";
				}else if(attn.getApprovalStatus().intValue()==700){
					approvalStatus = "失效拒绝";
				}
				sdoMap.put("approvalStatus",approvalStatus);
				sMapList.add(sdoMap);
			}
		}
		String[] keys={"code", "cnName", "departName", "positionName","workType1","cnName", "abnormalDate", "startPunchTime", "startResult", "endPunchTime", "endResult", "startTime", "endTime", "reason","submitDate","auditUser","approvalStatus"};
		String[] titles={"申诉人员工编号", "申诉人姓名", "部门","职位名称","工时制", "申请人姓名", "异常考勤日期", "上班考勤时间", "上班考勤结果", "下班考勤时间", "下班考勤结果", "上班申诉考勤时间", "下班申诉考勤时间", "申诉理由","申请时间","批核人","状态"}; 
		return ExcelUtil.exportExcel(sMapList, keys, titles, "异常考勤.xls");
	}

	@Override
	public List<EmpApplicationAbnormalAttendance> getExportReportList(
			EmpApplicationAbnormalAttendance attendance) {
		return empApplicationAbnormalAttendanceMapper.getExportReportList(attendance);
	}

	//考勤异常提交任务
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void completeTask(HttpServletRequest request,String instanceId, String comment, String commentType)throws Exception {
		
		logger.info("异常考勤申请单completeTask:start。。");
		Long time1 = System.currentTimeMillis();
		
		User user = userService.getCurrentUser();
		logger.info("异常考勤申请单completeTask入参:instanceId="+instanceId+";comment="+comment+";commentType="+commentType
			+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));

		Task task = activitiServiceImpl.queryTaskByProcessInstanceId(instanceId);
		EmpApplicationAbnormalAttendance attendance = queryByProcessInstanceId(instanceId);
		if(task==null){
			throw new OaException("实例Id为"+instanceId+"的流程不存在！");
		}else if(!StringUtils.equalsIgnoreCase(task.getAssignee(), user.getEmployeeId().toString())
				&&!StringUtils.equalsIgnoreCase(attendance.getEmployeeId().toString(), user.getEmployeeId().toString())){
			task = activitiServiceImpl.queryTaskByTaskIdAndCandidate(task.getId(), user.getEmployeeId().toString());
			if(task == null) {
				throw new OaException("该条申请单没有权限操作,请确认后再操作。");
			}
		}
		boolean type = StringUtils.startsWithIgnoreCase(task.getTaskDefinitionKey(), "end_");
		
		// 考勤异常
		if(attendance!=null){
			if(annualVacationService.check5WorkingDayNextmonth(DateUtils.format(attendance.getAbnormalDate(), DateUtils.FORMAT_SHORT),type)){
				logger.error(userService.getCurrentUser().getEmployee().getCnName()+"审批员工Id="+attendance.getEmployeeId()+"的考勤异常："+"已超过有效时间,无法操作！");
				throw new OaException("已超过有效时间,无法操作！");
			}else{
				Integer approvalStatus ;
				if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.PASS)) {
					approvalStatus=type?ConfigConstants.PASS_STATUS:ConfigConstants.DOING_STATUS;
					
				}else if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.REFUSE)) {
					approvalStatus=ConfigConstants.REFUSE_STATUS;
				}else {
					approvalStatus=ConfigConstants.BACK_STATUS;
				}
				attendance.setApprovalStatus(approvalStatus);
				attendance.setUpdateTime(new Date());
				attendance.setUpdateUser(user.getEmployee().getCnName());
				updateById(attendance);
				//判断当前节点是否有办理人assignee,没有则将当前代理人设为办理人
				if(StringUtils.isBlank(task.getAssignee())) {
					activitiServiceImpl.claimTask(task.getId(), String.valueOf(userService.getCurrentUser().getEmployeeId()));
				}
				sendMailService.sendCommentMail(employeeService.getByEmpId(attendance.getEmployeeId()).get(0).getEmail(), "考勤异常申请单", task.getName()+"-"+user.getEmployee().getCnName(), approvalStatus);
				//-----------------start-----------------------保存流程节点信息
				ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
				flow.setAssigneeName(user.getEmployee().getCnName());
				flow.setDepartName(user.getDepart().getName());
				flow.setPositionName(user.getPosition().getPositionName());
				flow.setFinishTime(new Date());
				flow.setComment(comment);
				flow.setProcessId(instanceId);
				flow.setProcessKey(activitiServiceImpl.queryProcessByInstanceId(instanceId).getKey());
				flow.setStatu(approvalStatus);
				viewTaskInfoService.save(flow);
				//-----------------end-------------------------
				Map<String,Object> isAdmin = new HashMap<String,Object>();
				isAdmin.put("isAdmin", false);
				activitiServiceImpl.completeTask(task.getId(), comment,isAdmin,commentType);
			}
		}else{
			logger.error("流程实例为"+instanceId+"的考勤异常审批数据不存在！");
			throw new OaException("流程实例为"+instanceId+"的考勤异常审批数据不存在！");
		}
		
		Long time2 = System.currentTimeMillis();
		logger.info("异常考勤申请单completeTask:use time="+(time2-time1));
		logger.info("异常考勤申请单completeTask:end。。");
	}

	@Override
	public EmpApplicationAbnormalAttendance queryByProcessInstanceId(String instanceId) {
		return empApplicationAbnormalAttendanceMapper.queryByProcessId(instanceId);
	}

	@Override
	public void setValueToVO(TaskVO taskVO, String processInstanceId) {

		EmpApplicationAbnormalAttendance attendance = queryByProcessInstanceId(processInstanceId);
		if(attendance!=null){
			String view3 = "";
			String view4 = "";
			if(attendance.getStartTime()==null){
				view3 = "空卡~";
			}else{
				view3 = DateUtils.format(attendance.getStartTime(), "HH:mm")+"~";
			}
			if(attendance.getEndTime()==null){
				view3 += "空卡";
			}else{
				view3 += DateUtils.format(attendance.getEndTime(), "HH:mm");
			}
			if(attendance.getStartPunchTime()==null){
				view4 = "空卡~";
			}else{
				view4 = DateUtils.format(attendance.getStartPunchTime(), "HH:mm")+"~";
			}
			if(attendance.getEndPunchTime()==null){
				view4 += "空卡";
			}else{
				view4 += DateUtils.format(attendance.getEndPunchTime(), "HH:mm");
			}
			if(attendance.getApplyType().intValue()==1){
				taskVO.setView6(attendance.getCnName());
				taskVO.setProcessName("下属异常考勤单");
			}else {
				taskVO.setProcessName("异常考勤单");
			}
			String redirectUrl = "/empApplicationAbnormalAttendance/approval.htm?flag=no&attendanceId="+attendance.getId();
			if(!(taskVO.getProcessStatu()==null)) {
				redirectUrl = "/empApplicationAbnormalAttendance/approval.htm?flag=can&attendanceId="+attendance.getId();
			}
			taskVO.setRedirectUrl(redirectUrl);
			taskVO.setCreatorDepart(attendance.getDepartName());
			taskVO.setProcessStatu(attendance.getApprovalStatus());
			taskVO.setOperation(ConfigConstants.STATUS.getOperationByStatu(attendance.getApprovalStatus()));
			taskVO.setCreator(attendance.getCreateUser());
			taskVO.setCreateTime(attendance.getCreateTime());
			taskVO.setView3(view3);
			taskVO.setView4(view4);
			taskVO.setView5(attendance.getReason());
			taskVO.setReProcdefCode("40");
			taskVO.setProcessId(attendance.getProcessInstanceId());
			taskVO.setResourceId(String.valueOf(attendance.getId()));
		}
	}

	@Override
	public void updateProcessInstanceId(EmpApplicationAbnormalAttendance newAbnormal) {
		empApplicationAbnormalAttendanceMapper.updateById(newAbnormal);
	}

	@Override
	public PageModel<EmpApplicationAbnormalAttendance> myAttnTaskList(
			EmpApplicationAbnormalAttendance attendance) {
		// TODO Auto-generated method stub
		if(attendance.getApplyStartDate()!=null){
			attendance.setApplyStartDate(DateUtils.parse(DateUtils.format(attendance.getApplyStartDate(), DateUtils.FORMAT_SHORT)+" 00:00:01", DateUtils.FORMAT_LONG));
		}
		if(attendance.getApplyEndDate()!=null){
			attendance.setApplyEndDate(DateUtils.parse(DateUtils.format(attendance.getApplyEndDate(), DateUtils.FORMAT_SHORT)+" 23:59:59", DateUtils.FORMAT_LONG));
		}
		
		int page = attendance.getPage() == null ? 0 : attendance.getPage();
		int rows = attendance.getRows() == null ? 0 : attendance.getRows();
		
		PageModel<EmpApplicationAbnormalAttendance> pm = new PageModel<EmpApplicationAbnormalAttendance>();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		Integer total = empApplicationAbnormalAttendanceMapper.myAttnTaskListCount(attendance);
		pm.setTotal(total);
		
		attendance.setOffset(pm.getOffset());
		attendance.setLimit(pm.getLimit());
		
		List<EmpApplicationAbnormalAttendance> roles = empApplicationAbnormalAttendanceMapper.myAttnTaskList(attendance);
		
		//查询审核人
		List<String> processIdList = new ArrayList<String>();
		for(EmpApplicationAbnormalAttendance og:roles) {
			processIdList.add(og.getProcessInstanceId());
		}
		List<ViewTaskInfoTbl> auditUserList = viewTaskInfoService.queryTasksByProcessIdList(processIdList);
		LinkedHashMap<String, List<ViewTaskInfoTbl>> auditUsermap = auditUserList.stream().sorted(Comparator.comparing(ViewTaskInfoTbl::getId)).collect(Collectors.groupingBy(ViewTaskInfoTbl::getProcessId, LinkedHashMap::new, Collectors.toList()));
		
		for(EmpApplicationAbnormalAttendance og:roles){
			try{
				String auditUser = (auditUsermap!=null&&auditUsermap.containsKey(og.getProcessInstanceId()))?getAttnAuditUser(auditUsermap.get(og.getProcessInstanceId()),og.getApplyType()):"";
				og.setAuditUser(auditUser);
			}catch(Exception e){
				og.setAuditUser(" ");
			}
		}
		
		pm.setRows(roles);
		return pm;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void completeTaskByAdmin(HttpServletRequest request,String processId, String comment,
			String commentType, User user, Task task) throws Exception {
		
		logger.info("异常考勤申请单completeTaskByAdmin:start。。");
		Long time1 = System.currentTimeMillis();
		logger.info("请假申请单completeTask入参:processId="+processId+";comment="+comment+";commentType="+commentType
			+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));
		
		if(null == task) {
			throw new OaException("该条异常考勤单已被结束流程,请确认后再操作。");
		}
		
		boolean type = StringUtils.startsWithIgnoreCase(task.getTaskDefinitionKey(), "end_");
		EmpApplicationAbnormalAttendance attendance = queryByProcessInstanceId(processId);
		
		// 考勤异常
		if(attendance!=null){
			Integer approvalStatus ;
			if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.PASS)) {
				approvalStatus=type?ConfigConstants.PASS_STATUS:ConfigConstants.DOING_STATUS;
  			}else if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.OVERDUEPASS)){
				approvalStatus=type?ConfigConstants.OVERDUEPASS_STATUS:ConfigConstants.DOING_STATUS;
 			}else if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.REFUSE)) {
				approvalStatus=ConfigConstants.REFUSE_STATUS;
 			}else if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.OVERDUEREFUSE)){
 				approvalStatus=ConfigConstants.OVERDUEREFUSE_STATUS;
			}else {
				approvalStatus=ConfigConstants.BACK_STATUS;
			}
			attendance.setApprovalStatus(approvalStatus);
 			attendance.setUpdateTime(new Date());
 			attendance.setUpdateUser(user.getEmployee().getCnName());
 			updateById(attendance);
			//判断当前节点是否有办理人assignee,没有则将当前代理人设为办理人
			if(StringUtils.isBlank(task.getAssignee())) {
				activitiServiceImpl.claimTask(task.getId(), String.valueOf(userService.getCurrentUser().getEmployeeId()));
			}
			sendMailService.sendCommentMail(employeeService.getByEmpId(attendance.getEmployeeId()).get(0).getEmail(), "考勤异常申请单", task.getName()+"-"+user.getEmployee().getCnName(), approvalStatus);
			//-----------------start-----------------------保存流程节点信息
			ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
			flow.setAssigneeName(user.getEmployee().getCnName());
			flow.setDepartName(user.getDepart().getName());
			flow.setPositionName(user.getPosition().getPositionName());
			flow.setFinishTime(new Date());
			flow.setComment(comment);
 			flow.setProcessId(processId);
   			flow.setProcessKey(activitiServiceImpl.queryProcessByInstanceId(processId).getKey());
			flow.setStatu(approvalStatus);
			viewTaskInfoService.save(flow);
			//-----------------end-------------------------
			Map<String,Object> isAdmin = new HashMap<String,Object>();
			isAdmin.put("isAdmin", true);
			activitiServiceImpl.completeTask(task.getId(), comment,isAdmin,commentType);
		}else{
			logger.error("流程实例为"+processId+"的考勤异常审批数据不存在！");
			throw new OaException("流程实例为"+processId+"的考勤异常审批数据不存在！");
		}
		
		Long time2 = System.currentTimeMillis();
		logger.info("异常考勤申请单completeTaskByAdmin:use time="+(time2-time1));
		logger.info("异常考勤申请单completeTaskByAdmin:end。。");
		
	}

	@Override
	public void updateApprovalStatusById(Long id, Integer approvalStatus) {
		EmpApplicationAbnormalAttendance data = new EmpApplicationAbnormalAttendance();
		data.setId(id);
		data.setApprovalStatus(approvalStatus);
		empApplicationAbnormalAttendanceMapper.updateById(data);
	}
}
