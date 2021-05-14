package com.ule.oa.base.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.ule.oa.base.mapper.BaseEmpWorkLogMapper;
import com.ule.oa.base.mapper.EmployeeMapper;
import com.ule.oa.base.po.AttnSignRecord;
import com.ule.oa.base.po.BaseEmpWorkLog;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.po.RequestParamQueryEmpCondition;
import com.ule.oa.base.po.User;
import com.ule.oa.base.po.tbl.ViewTaskInfoTbl;
import com.ule.oa.base.service.AnnualVacationService;
import com.ule.oa.base.service.AttnStatisticsService;
import com.ule.oa.base.service.BaseEmpWorkLogService;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.EmployeeClassService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.service.ViewTaskInfoService;
import com.ule.oa.base.util.SendMailUtil;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.ExcelUtil;
import com.ule.oa.common.utils.PageModel;
import com.ule.oa.common.utils.http.IPUtils;

/**
 * 员工工作日志
 * @author yangjie
 *
 */
@Service
public class BaseEmpWorkLogServiceImpl implements BaseEmpWorkLogService {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private BaseEmpWorkLogMapper baseEmpWorkLogMapper;
	@Autowired
	private UserService userService;
	@Autowired
	private ViewTaskInfoService viewTaskInfoService;
	@Autowired
	private ActivitiServiceImpl activitiServiceImpl;
	@Autowired
	private ConfigService configService;
	@Autowired
	private AnnualVacationService annualVacationService;
	@Autowired
	private EmployeeMapper employeeMapper;
	@Autowired
	private AttnStatisticsService attnStatisticsService;
	@Autowired
	private EmployeeClassService employeeClassService;
	@Autowired
	private EmployeeService employeeService;
	

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
	public Map<String, Object> save(BaseEmpWorkLog workLog) throws OaException {
		
		User user = userService.getCurrentUser();
		
		Map<String,Object> result = new HashMap<String,Object>();
		
		if(user==null||user.getEmployee()==null){
			throw new OaException("请重新登录系统。");
		}
		
		if(workLog==null){
			throw new OaException("参数错误。");
		}
		
		logger.info("员工工作日志save入参:employeeId="+user.getEmployeeId()
				+";workDate="+workLog.getWorkDate()
				+";workContent="+workLog.getWorkContent()
				+";nextDayWorkPlan="+workLog.getNextDayWorkPlan()
				+";workProblem="+workLog.getWorkProblem());
		
		if (workLog.getWorkDate() == null) {
			throw new OaException("日期不能为空！");
		}
		if (StringUtils.isBlank(workLog.getWorkContent())) {
			throw new OaException("工作内容与输出不能为空！");
		}
		if (StringUtils.isBlank(workLog.getNextDayWorkPlan())) {
			throw new OaException("下一个工作日工作计划不能为空！");
		}
		
		EmployeeClass employeeClass = new EmployeeClass(); 
		employeeClass.setEmployId(user.getEmployee().getId());
		employeeClass.setClassDate(workLog.getWorkDate());
		EmployeeClass isWorkDay = employeeClassService.getEmployeeClassSetting(employeeClass);
		if(isWorkDay==null||isWorkDay.getStartTime()==null){
			throw new OaException("非工作日不需要申请！");
		}
		
		//提交时间限制（当日零点-到次日24:00间）
		Date startTime = DateUtils.parse(DateUtils.format(workLog.getWorkDate(), DateUtils.FORMAT_SHORT)+" 00:00:00", DateUtils.FORMAT_LONG);
		Date endTime = DateUtils.parse(DateUtils.format(DateUtils.addDay(workLog.getWorkDate(), 1), DateUtils.FORMAT_SHORT)+" 23:59:59", DateUtils.FORMAT_LONG);
		Date now = new Date();
		if(now.getTime()<startTime.getTime()||now.getTime()>endTime.getTime()){
			throw new OaException("请在规定时间内填写员工日志！");
		}
		
		//查询是否已经提交过员工日志
		BaseEmpWorkLog isExist = new BaseEmpWorkLog();
		isExist.setWorkDate(workLog.getWorkDate());
		isExist.setEmployeeId(user.getEmployee().getId());
		BaseEmpWorkLog list = baseEmpWorkLogMapper.selectEffectiveApplyByCondition(workLog.getWorkDate(),user.getEmployee().getId());
		if(list!=null){
			throw new OaException("已填写过当日员工日志！");
		}
		
		workLog.setEmployeeId(user.getEmployee().getId());
		// 启动流程
		Map<String,Object> v = Maps.newHashMap();
		v.put("proposerId", workLog.getEmployeeId());
		String processInstanceId = activitiServiceImpl.startByKey(ConfigConstants.WORK_LOG_KEY,v);
		workLog.setProcessId(processInstanceId);
		//创建申请单
		workLog.setDelFlag(0);
		workLog.setApprovalStatus(100);
		workLog.setCreateTime(new Date());
		workLog.setCreateUser(user.getEmployee().getCnName());
		baseEmpWorkLogMapper.save(workLog);
		
		// -----------------start-----------------------保存流程节点信息
		ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
		flow.setAssigneeName(user.getEmployee().getCnName());
		flow.setDepartName(user.getDepart().getName());
		flow.setPositionName(user.getPosition().getPositionName());
		flow.setFinishTime(new Date());
		flow.setComment("");
		flow.setProcessId(processInstanceId);
		flow.setProcessKey(ConfigConstants.WORK_LOG_KEY);
		flow.setStatu(0);
		viewTaskInfoService.save(flow);
		result.put("message", "保存成功！");
		result.put("success", true);
		return result;
	}

	@Override
	public BaseEmpWorkLog getById(Long id) {
		return baseEmpWorkLogMapper.getById(id);
	}

	@Override
	public void completeTask(HttpServletRequest request, String processId,
			String comment, String commentType) throws OaException {
		User user = userService.getCurrentUser();
		logger.info("员工日志completeTask入参:processId="+processId+";comment="+comment+";commentType="+commentType
				+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));
		
		Task task = activitiServiceImpl.queryTaskByProcessInstanceId(processId);
		if (task == null) {
			logger.error("实例Id为" + processId + "的流程不存在！");
			throw new OaException("任务已完成！");
		}
		
		BaseEmpWorkLog workLog = baseEmpWorkLogMapper.getByProcessId(processId);
		if (workLog == null) {
			logger.error("流程实例为" + processId + "的审批数据不存在！");
			throw new OaException("任务已完成！");
		}
		
		boolean type = StringUtils.startsWithIgnoreCase(task.getTaskDefinitionKey(), "personnel");
		
		Integer approvalStatus;
		if (StringUtils.equalsIgnoreCase(commentType, ConfigConstants.PASS)) {
			
			if(!StringUtils.equalsIgnoreCase(task.getAssignee(), user.getEmployeeId().toString())
					&&!StringUtils.equalsIgnoreCase(workLog.getEmployeeId().toString(), user.getEmployeeId().toString())){
				task = activitiServiceImpl.queryTaskByTaskIdAndCandidate(task.getId(), user.getEmployeeId().toString());
				if(task == null) {
					throw new OaException("该条单据没有权限操作,请确认后再操作。");
				}
			}
			
			/**同意**/
			if(annualVacationService.check5WorkingDayNextmonth(DateUtils.format(workLog.getWorkDate(), DateUtils.FORMAT_SHORT),type)){			
				logger.error(userService.getCurrentUser().getEmployee().getCnName()+"审批员工Id="+workLog.getEmployeeId()+"的工作日志：已超出有效时间,无法操作！");
				throw new OaException("已超出有效时间,无法操作！");
			}
			
			approvalStatus = type ? ConfigConstants.PASS_STATUS : ConfigConstants.DOING_STATUS;
			// 判断当前节点是否有办理人assignee,没有则将当前代理人设为办理人
			if (StringUtils.isBlank(task.getAssignee())) {
				activitiServiceImpl.claimTask(task.getId(),
						String.valueOf(userService.getCurrentUser().getEmployeeId()));
			}
			//如果状态为审批通过则代表流程结束，扣除锁定小时数，增加使用小时数
			if(ConfigConstants.PASS_STATUS.equals(approvalStatus)){
				approvalStatus = ConfigConstants.PASS_STATUS;
			}
		} else if (StringUtils.equalsIgnoreCase(commentType, ConfigConstants.REFUSE)) {
			
			if(!StringUtils.equalsIgnoreCase(task.getAssignee(), user.getEmployeeId().toString())
					&&!StringUtils.equalsIgnoreCase(workLog.getEmployeeId().toString(), user.getEmployeeId().toString())){
				task = activitiServiceImpl.queryTaskByTaskIdAndCandidate(task.getId(), user.getEmployeeId().toString());
				if(task == null) {
					throw new OaException("该条单据没有权限操作,请确认后再操作。");
				}
			}
			
			/**拒绝**/
			if(annualVacationService.check5WorkingDayNextmonth(DateUtils.format(workLog.getWorkDate(), DateUtils.FORMAT_SHORT),type)){			
				logger.error(userService.getCurrentUser().getEmployee().getCnName()+"审批员工Id="+workLog.getEmployeeId()+"的工作日志：已超出有效时间,无法操作！");
				throw new OaException("已超出有效时间,无法操作！");
			}
			approvalStatus = ConfigConstants.REFUSE_STATUS;
			// 判断当前节点是否有办理人assignee,没有则将当前代理人设为办理人
			if (StringUtils.isBlank(task.getAssignee())) {
				activitiServiceImpl.claimTask(task.getId(),
						String.valueOf(userService.getCurrentUser().getEmployeeId()));
			}
		}else if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.OVERDUEPASS)){
			/**失效同意**/
			approvalStatus = type ? ConfigConstants.OVERDUEPASS_STATUS : ConfigConstants.DOING_STATUS;
			// 判断当前节点是否有办理人assignee,没有则将当前代理人设为办理人
			if (StringUtils.isBlank(task.getAssignee())) {
				activitiServiceImpl.claimTask(task.getId(),
						String.valueOf(userService.getCurrentUser().getEmployeeId()));
			}
		}else if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.OVERDUEREFUSE)){
			/**失效拒绝**/
			approvalStatus = ConfigConstants.OVERDUEREFUSE_STATUS;
			// 判断当前节点是否有办理人assignee,没有则将当前代理人设为办理人
			if (StringUtils.isBlank(task.getAssignee())) {
				activitiServiceImpl.claimTask(task.getId(),
						String.valueOf(userService.getCurrentUser().getEmployeeId()));
			}
		}else {
			/**撤回**/
			approvalStatus = ConfigConstants.BACK_STATUS;
		}
		//修改申请单审批状态
		BaseEmpWorkLog updateStatus = new BaseEmpWorkLog();
		updateStatus.setId(workLog.getId());
		updateStatus.setApprovalStatus(approvalStatus);
		updateStatus.setUpdateTime(new Date());
		updateStatus.setUpdateUser(user.getEmployee().getCnName());
		baseEmpWorkLogMapper.updateById(updateStatus);
		// -----------------start-----------------------保存流程节点信息
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
		activitiServiceImpl.completeTask(task.getId(),"审批通过",null,commentType);
		// -----------------end-------------------------
		
	}

	@Override
	public Map<String, Object> queryWorkLogDetalInfoByMonth(String month) {
		
		User user = userService.getCurrentUser();
		
		Map<String,Object> result = new HashMap<String,Object>();
		
		if(StringUtils.isBlank(month)){
			Date now = new Date();
			month = DateUtils.format(now, "yyyy-MM");
		}
		
		Date firstDay = DateUtils.parse(month+"-01", DateUtils.FORMAT_SHORT);
		Date lastDay = DateUtils.getLastDay(firstDay);
		Date firstDay1 = firstDay;
		Date lastDay1 = lastDay;
		
		//表头，星期和天数
		List<String> weekDays = new ArrayList<String>();
		weekDays.add("&nbsp;");
		weekDays.add("&nbsp;");
		//表头，天数
		List<String> days = new ArrayList<String>();
		days.add("员工编号");
		days.add("姓名");
		List<String> dates = new ArrayList<String>();
		dates.add("");
		dates.add("");
		//第三行数据
		List<Object> workLogDetail  = new ArrayList<Object>();
		workLogDetail.add(user.getEmployee().getCode());
		workLogDetail.add(user.getEmployee().getCnName());
		
		//工作日志集合
		BaseEmpWorkLog workLogMonth =new BaseEmpWorkLog();
		workLogMonth.setEmployeeId(user.getEmployee().getId());
		workLogMonth.setStartWorkDate(firstDay);
		workLogMonth.setEndWorkDate(lastDay);
		List<BaseEmpWorkLog> workLogMonthList = baseEmpWorkLogMapper.selectByCondition(workLogMonth);
		Map<Date,BaseEmpWorkLog> workLogMonthMap = workLogMonthList.stream().collect(Collectors.toMap(BaseEmpWorkLog :: getWorkDate,a->a,(k1,k2)->k1));
		//查询该月班次信息
		Map<Date,EmployeeClass> clasMap = employeeClassService.getEmployeeClassSetting(user.getEmployee(), firstDay,lastDay);
		
		int i = 1;
		dates.add(DateUtils.format(firstDay, "yyyy-MM-dd"));
		while(true){
			String week = DateUtils.getWeek(firstDay);
			weekDays.add(week);
			days.add(String.valueOf(i));
			firstDay = DateUtils.addDay(firstDay, 1);
			
			dates.add(DateUtils.format(firstDay, "yyyy-MM-dd"));
			i = i + 1;
			if(!DateUtils.format(firstDay, "yyyy-MM").equals(DateUtils.format(lastDay, "yyyy-MM"))){
				break;
			}
		}
		result.put("weekDays", weekDays);
		result.put("days", days);
		result.put("dates", dates);
		
		
		//工作日志详情
		while(true){
			Date key = firstDay1;
			if(clasMap!=null&&clasMap.containsKey(key)){
				EmployeeClass employeeClass = (EmployeeClass) clasMap.get(key);
				if(employeeClass!=null&&employeeClass.getStartTime()!=null){
					if(workLogMonthMap!=null&&workLogMonthMap.containsKey(firstDay1)){
						if(workLogMonthMap.get(firstDay1).getApprovalStatus()!=null
								&&workLogMonthMap.get(firstDay1).getApprovalStatus().intValue()==100){
							workLogDetail.add("审阅中");
						}else if(workLogMonthMap.get(firstDay1).getApprovalStatus()!=null
								&&workLogMonthMap.get(firstDay1).getApprovalStatus().intValue()==200){
							workLogDetail.add("√");
						}else if(workLogMonthMap.get(firstDay1).getApprovalStatus()!=null
								&&workLogMonthMap.get(firstDay1).getApprovalStatus().intValue()==300){
							workLogDetail.add("X");
						}else if(workLogMonthMap.get(firstDay1).getApprovalStatus()!=null
								&&workLogMonthMap.get(firstDay1).getApprovalStatus().intValue()==400){
							workLogDetail.add("撤销");
						}else if(workLogMonthMap.get(firstDay1).getApprovalStatus()!=null
								&&workLogMonthMap.get(firstDay1).getApprovalStatus().intValue()==500){
							workLogDetail.add("失效");
						}else if(workLogMonthMap.get(firstDay1).getApprovalStatus()!=null
								&&workLogMonthMap.get(firstDay1).getApprovalStatus().intValue()==600){
							workLogDetail.add("√");
						}else if(workLogMonthMap.get(firstDay1).getApprovalStatus()!=null
								&&workLogMonthMap.get(firstDay1).getApprovalStatus().intValue()==700){
							workLogDetail.add("X");
						}else{
							workLogDetail.add("未提交");
						}
					}else{
						workLogDetail.add("未填");
					}
				}else{
					workLogDetail.add("/");
				}
			}else{
				workLogDetail.add("/");
			}
			firstDay1 = DateUtils.addDay(firstDay1, 1);
			if(!DateUtils.format(firstDay1, "yyyy-MM").equals(DateUtils.format(lastDay1, "yyyy-MM"))){
				break;
			}
		}
		
		
		
		result.put("workLogDetail", workLogDetail);
		
		result.put("success", true);
		
		return result;	
	}

	@Override
	public Map<String, Object> getProcessInfoDiv(String workDate,Long employeeId)
			throws OaException {
		Map<String,Object> result = new HashMap<String,Object>();
		User user = userService.getCurrentUser();
		
		if(user==null||user.getEmployee()==null){
			throw new OaException("请重新登录系统。");
		}
		
		if (StringUtils.isBlank(workDate)) {
			throw new OaException("参数有误！");
		}
		
		
		if(employeeId == null){
			employeeId = user.getEmployee().getId();
			result.put("cnName", user.getEmployee().getCnName());//员工姓名
			result.put("departName", user.getDepart().getName());//部门名称
		}else{
			List<Employee> employee = employeeMapper.getByEmpId(employeeId);
			result.put("cnName", "");//员工姓名
			result.put("departName", "");//部门名称
			if(employee!=null&&employee.size()>0){
				result.put("cnName", employee.get(0).getCnName());//员工姓名
				result.put("departName", employee.get(0).getEmpDepart().getDepart().getName());//部门名称
			}
			
		}
		
		result.put("workDate", workDate);//工作日期
		
		//当天工作日志
		BaseEmpWorkLog workLog = baseEmpWorkLogMapper.getByWorkDateAndEmployeeId(DateUtils.parse(workDate, DateUtils.FORMAT_SHORT),employeeId);
		if(workLog==null){
			throw new OaException("当天未申请工作日志。");
		}
		result.put("processId", workLog.getProcessId());//流程id
		
		//查看流程状态
		Task task = activitiServiceImpl.queryTaskByProcessInstanceId(workLog.getProcessId());
		result.put("approvalStatus", getApprovalStatus(workLog.getApprovalStatus(),task));//流程状态
		result.put("leaderStatus", "");//主管审阅
		result.put("hrStatus", "");//人事审阅
		result.put("hrComment", "无");//批示意见
		result.put("leaderComment", "无");//批示意见
		List<ViewTaskInfoTbl> taskInfoList = viewTaskInfoService.queryTasksByProcessId(workLog.getProcessId());
		if(taskInfoList!=null&&taskInfoList.size()==2){
			result.put("leaderStatus", taskInfoList.get(1).getStatu());//主管审阅
			result.put("leaderComment", StringUtils.isNotBlank(taskInfoList.get(1).getComment())?taskInfoList.get(1).getComment():"无");//批示意见
		}else if(taskInfoList!=null&&taskInfoList.size()==3){
			result.put("leaderStatus", taskInfoList.get(1).getStatu());//主管审阅
			result.put("hrStatus", taskInfoList.get(0).getStatu());//人事审阅
			result.put("hrComment", StringUtils.isNotBlank(taskInfoList.get(0).getComment())?taskInfoList.get(0).getComment():"无");//批示意见
			result.put("leaderComment", StringUtils.isNotBlank(taskInfoList.get(1).getComment())?taskInfoList.get(1).getComment():"无");//批示意见
		}
		
		int maxSize = 0;
		//工作内容
		if(StringUtils.isNotBlank(workLog.getWorkContent())){
			String[] workContentList = workLog.getWorkContent().split("===");
			result.put("workContent",workContentList);
			maxSize = workContentList.length;
		}
		//工作计划
		if(StringUtils.isNotBlank(workLog.getNextDayWorkPlan())){
			String[] workPlanList = workLog.getNextDayWorkPlan().split("===");
			result.put("workPlan",workPlanList);
			if(workPlanList.length>maxSize){
				maxSize = workPlanList.length;
			}
		}
		//工作问题
		String[] workProblemList = {" "};
		result.put("workProblem",workProblemList);
		if(StringUtils.isNotBlank(workLog.getWorkProblem())){
			workProblemList = workLog.getWorkProblem().split("===");
			result.put("workProblem",workProblemList);
			if(workProblemList.length>maxSize){
				maxSize = workProblemList.length;
			}
		}
		
		result.put("maxSize", maxSize);
		result.put("success", true);
		return result;
	}
	
	public String getApprovalStatus(Integer approvalStatus,Task task){
		String status = "";
		String taskName = "";
		if(approvalStatus!=null){
			switch (approvalStatus.intValue()) {
			case 100:
				taskName = task!=null?task.getName():"";
				status = taskName+"审阅中";
				break;
			case 200:
				status = "通过";
				break;	
			case 300:
				status = "不通过";
				break;
			case 400:
				status = "已撤销";
				break;
			case 500:
			    taskName = task!=null?task.getName():"";
				status = taskName+"已失效";
				break;	
			case 600:
				status = "通过";
				break;
			case 700:
				status = "不通过";
				break;
			}
		}
		return status;
	}
	
	public String getLeaderStatus(Integer approvalStatus){
		String status = "";
		if(approvalStatus!=null){
			switch (approvalStatus.intValue()) {
			case 100:
				status = "通过";
				break;
			case 200:
				status = "通过";
				break;	
			case 300:
				status = "不通过";
				break;
			case 600:
				status = "通过";
				break;
			case 700:
				status = "不通过";
				break;
			}
		}
		return status;
	}

	@Override
	public Map<String, Object> getApporvalPage(String month,
			Long departId, String leaderName, String empCode, String empCnName,
			Integer page, Integer rows,String index) {
		
		long time1 = System.currentTimeMillis();

		User user = userService.getCurrentUser();
		Map<String, Object> result = new HashMap<String, Object>();
        
		if(StringUtils.isBlank(month)){
			month = DateUtils.format(new Date(), DateUtils.FORMAT_YYYY_MM);
		}
		
		Date startDay = DateUtils.parse(month+"-01",DateUtils.FORMAT_SHORT);// 本月第一天
		Date lastDay = DateUtils.getLastDay(startDay);// 本月最后一天
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
		Date fristDay = startDay;// 本月第一天
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
		result.put("weekDays", weekDays);
		result.put("days", days);
		result.put("dates", dates);

		PageModel<Map<String, Object>> pm = new PageModel<Map<String, Object>>();
		List<Map<String, Object>> workLogDetailList = new ArrayList<Map<String, Object>>();
		pm.setPageNo(page == null ? 0 : page);
		pm.setPageSize(rows == null ? 0 : rows);
		Integer limit = pm.getLimit();
		Integer offset = pm.getOffset();
		
		//根据所有下属id查询详细信息
		List<Employee> empList = new ArrayList<Employee>();
		Integer total = 0;
		RequestParamQueryEmpCondition hrQueryParam = new RequestParamQueryEmpCondition();
		//筛选条件
		//汇报对象
		if(StringUtils.isNotBlank(leaderName)){
			hrQueryParam.setSearchLeader(leaderName);
		}
		if("leaderTab".equals(index)){
			hrQueryParam.setLeaderId(user.getEmployee().getId());
		}
		// 部门
		hrQueryParam.setDepartId(departId);
		hrQueryParam.setEmpCode(empCode);
		hrQueryParam.setEmpCnName(empCnName);
		hrQueryParam.setChooseMonth(startDay);
		
		total = baseEmpWorkLogMapper.getCountByCondition(hrQueryParam);
		// 分页
		hrQueryParam.setLimit(limit);
		hrQueryParam.setOffset(offset);
		empList = baseEmpWorkLogMapper.getListByCondition(hrQueryParam);
		List<Long>  employeeIdList = new ArrayList<Long>();
		for (Employee employee : empList) {
			employeeIdList.add(employee.getId());
		}
		
		if (empList != null && empList.size() > 0) {
			
			//查询员工所有工作日志申请
			BaseEmpWorkLog workLogListP = new BaseEmpWorkLog();
			workLogListP.setEmployeeIdList(employeeIdList);
			workLogListP.setStartWorkDate(startDay);
			workLogListP.setEndWorkDate(lastDay);
			List<BaseEmpWorkLog> workLogList = baseEmpWorkLogMapper.selectByCondition(workLogListP);
			Map<Long,List<BaseEmpWorkLog>> groupByEmployeeIdMap = workLogList.stream().collect(Collectors.groupingBy(BaseEmpWorkLog :: getEmployeeId));
			//查询所有员工班次信息
			Map<Long,List<EmployeeClass>> gorupByEmployeeEmpClass = employeeClassService.getEmployeeClassSetting(employeeIdList, startDay, lastDay);
            //查询所有工作日志流程审批信息
			List<String> processIdList = new ArrayList<String>();
			for(BaseEmpWorkLog data:workLogList){
				processIdList.add(data.getProcessId());
			}
			List<ViewTaskInfoTbl> taskInfoList = new ArrayList<ViewTaskInfoTbl>();
			if(processIdList!=null&&processIdList.size()>0){
				taskInfoList = viewTaskInfoService.queryTasksByProcessIdList(processIdList);
			}
			Map<String,List<ViewTaskInfoTbl>> taskInfoMap = taskInfoList.stream().collect(Collectors.groupingBy(ViewTaskInfoTbl :: getProcessId));
			
			for (Employee employee : empList) {
				//表头
				Map<String, Object> delayWorkDetail = new HashMap<String, Object>();
				delayWorkDetail.put("empId", employee.getId());
				delayWorkDetail.put("empCode", employee.getCode());
				delayWorkDetail.put("empCnName", employee.getCnName());
				//工作日志信息
				List<BaseEmpWorkLog> list = new ArrayList<BaseEmpWorkLog>();
				if(groupByEmployeeIdMap!=null&&groupByEmployeeIdMap.containsKey(employee.getId())){
					list = groupByEmployeeIdMap.get(employee.getId());
				}
				Map<Date,BaseEmpWorkLog> groupByWorkDate = list.stream().collect(Collectors.toMap(BaseEmpWorkLog :: getWorkDate,a->a,(k1,k2)->k1));
				//班次信息
				List<EmployeeClass> classList = new ArrayList<EmployeeClass>();
				if(gorupByEmployeeEmpClass!=null&&gorupByEmployeeEmpClass.containsKey(employee.getId())){
					classList = gorupByEmployeeEmpClass.get(employee.getId());
				}
				Map<Date,EmployeeClass> classGroupByWorkDate = classList.stream().collect(Collectors.toMap(EmployeeClass :: getClassDate,a->a,(k1,k2)->k1));
				
				Date start = startDay;
				Date end = lastDay;
				i = 1;
				while (true) {
					if(classGroupByWorkDate!=null&&classGroupByWorkDate.containsKey(start)){
						if(groupByWorkDate!=null&&groupByWorkDate.containsKey(start)){
							if(groupByWorkDate.get(start).getApprovalStatus()!=null
									&&groupByWorkDate.get(start).getApprovalStatus().intValue()==100){
								if(taskInfoMap!=null&&taskInfoMap.containsKey(groupByWorkDate.get(start).getProcessId())){
									List<ViewTaskInfoTbl> processInfoList = taskInfoMap.get(groupByWorkDate.get(start).getProcessId());
									if(processInfoList!=null&&processInfoList.size()==1){
										delayWorkDetail.put(DateUtils.format(start, DateUtils.FORMAT_SHORT), "待审阅_blue");
									}else if(processInfoList!=null&&processInfoList.size()==2){
										delayWorkDetail.put(DateUtils.format(start, DateUtils.FORMAT_SHORT), "审阅中_grey");
									}
								}else{
									delayWorkDetail.put(DateUtils.format(start, DateUtils.FORMAT_SHORT), "√_green");
								}
							}else if(groupByWorkDate.get(start).getApprovalStatus()!=null
									&&groupByWorkDate.get(start).getApprovalStatus().intValue()==200){
								delayWorkDetail.put(DateUtils.format(start, DateUtils.FORMAT_SHORT), "√_green");
							}else if(groupByWorkDate.get(start).getApprovalStatus()!=null
									&&groupByWorkDate.get(start).getApprovalStatus().intValue()==300){
								delayWorkDetail.put(DateUtils.format(start, DateUtils.FORMAT_SHORT), "X_red");
							}else if(groupByWorkDate.get(start).getApprovalStatus()!=null
									&&groupByWorkDate.get(start).getApprovalStatus().intValue()==400){
								delayWorkDetail.put(DateUtils.format(start, DateUtils.FORMAT_SHORT), "撤销_grey");
							}else if(groupByWorkDate.get(start).getApprovalStatus()!=null
									&&groupByWorkDate.get(start).getApprovalStatus().intValue()==500){
								delayWorkDetail.put(DateUtils.format(start, DateUtils.FORMAT_SHORT), "失效_blue");
							}else if(groupByWorkDate.get(start).getApprovalStatus()!=null
									&&groupByWorkDate.get(start).getApprovalStatus().intValue()==600){
								delayWorkDetail.put(DateUtils.format(start, DateUtils.FORMAT_SHORT), "√_green");
							}else if(groupByWorkDate.get(start).getApprovalStatus()!=null
									&&groupByWorkDate.get(start).getApprovalStatus().intValue()==700){
								delayWorkDetail.put(DateUtils.format(start, DateUtils.FORMAT_SHORT), "X_red");
							}else{
								delayWorkDetail.put(DateUtils.format(start, DateUtils.FORMAT_SHORT), "未提交_black");
							}
						}else{
							delayWorkDetail.put(DateUtils.format(start, DateUtils.FORMAT_SHORT), "未填_black");
						}
					}else{
						delayWorkDetail.put(DateUtils.format(start, DateUtils.FORMAT_SHORT), "/_grey");
					}
					start = DateUtils.addDay(start, 1);
					i = i + 1;
					if (!DateUtils.format(start, "yyyy-MM").equals(DateUtils.format(end, "yyyy-MM"))) {
						break;
					}
				}
				workLogDetailList.add(delayWorkDetail);
			}
			pm.setRows(workLogDetailList);
			pm.setTotal(total);
		}
		result.put("page", pm);
		result.put("success", true);
		
		long time2 = System.currentTimeMillis();
		logger.info("getApporvalPage uses time is:"+(time2-time1));
		
		return result;
	}

	@Override
	public Map<String, Object> getWorkLogSearchPage(String month,
			Long departId, String leaderName, String empCode, String empCnName,
			Integer page, Integer rows) {
		long time1 = System.currentTimeMillis();

		User user = userService.getCurrentUser();
		Map<String, Object> result = new HashMap<String, Object>();
        
		if(StringUtils.isBlank(month)){
			month = DateUtils.format(new Date(), DateUtils.FORMAT_YYYY_MM);
		}
		
		Date startDay = DateUtils.parse(month+"-01",DateUtils.FORMAT_SHORT);// 本月第一天
		Date lastDay = DateUtils.getLastDay(startDay);// 本月最后一天
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
		Date fristDay = startDay;// 本月第一天
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
		result.put("weekDays", weekDays);
		result.put("days", days);
		result.put("dates", dates);

		PageModel<Map<String, Object>> pm = new PageModel<Map<String, Object>>();
		List<Map<String, Object>> workLogDetailList = new ArrayList<Map<String, Object>>();
		pm.setPageNo(page == null ? 0 : page);
		pm.setPageSize(rows == null ? 0 : rows);
		Integer limit = pm.getLimit();
		Integer offset = pm.getOffset();
		
		//根据所有下属id查询详细信息
		List<Employee> empList = new ArrayList<Employee>();
		Integer total = 0;
		RequestParamQueryEmpCondition hrQueryParam = new RequestParamQueryEmpCondition();
		//筛选条件
		//汇报对象
		if(StringUtils.isNotBlank(leaderName)){
			hrQueryParam.setSearchLeader(leaderName);
		}
		
		List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(user.getEmployee().getId());//下属员工
		
		if(subEmployeeIdList!=null&&subEmployeeIdList.size()>0){
			hrQueryParam.setEmployeeIdList(subEmployeeIdList);
		}

		// 部门
		hrQueryParam.setDepartId(departId);
		hrQueryParam.setEmpCode(empCode);
		hrQueryParam.setEmpCnName(empCnName);
		hrQueryParam.setChooseMonth(startDay);
		
		total = baseEmpWorkLogMapper.getCountByCondition(hrQueryParam);
		// 分页
		hrQueryParam.setLimit(limit);
		hrQueryParam.setOffset(offset);
		empList = baseEmpWorkLogMapper.getListByCondition(hrQueryParam);
		List<Long>  employeeIdList = new ArrayList<Long>();
		for (Employee employee : empList) {
			employeeIdList.add(employee.getId());
		}
		
		if (empList != null && empList.size() > 0) {
			
			//查询员工所有工作日志申请
			BaseEmpWorkLog workLogListP = new BaseEmpWorkLog();
			workLogListP.setEmployeeIdList(employeeIdList);
			workLogListP.setStartWorkDate(startDay);
			workLogListP.setEndWorkDate(lastDay);
			List<BaseEmpWorkLog> workLogList = baseEmpWorkLogMapper.selectByCondition(workLogListP);
			Map<Long,List<BaseEmpWorkLog>> groupByEmployeeIdMap = workLogList.stream().collect(Collectors.groupingBy(BaseEmpWorkLog :: getEmployeeId));
			//查询所有员工班次信息
			Map<Long,List<EmployeeClass>> gorupByEmployeeEmpClass = employeeClassService.getEmployeeClassSetting(employeeIdList, startDay, lastDay);
            //查询所有工作日志流程审批信息
			List<String> processIdList = new ArrayList<String>();
			for(BaseEmpWorkLog data:workLogList){
				processIdList.add(data.getProcessId());
			}
			List<ViewTaskInfoTbl> taskInfoList = new ArrayList<ViewTaskInfoTbl>();
			if(processIdList!=null&&processIdList.size()>0){
				taskInfoList = viewTaskInfoService.queryTasksByProcessIdList(processIdList);
			}
			Map<String,List<ViewTaskInfoTbl>> taskInfoMap = taskInfoList.stream().collect(Collectors.groupingBy(ViewTaskInfoTbl :: getProcessId));
			
			for (Employee employee : empList) {
				//表头
				Map<String, Object> delayWorkDetail = new HashMap<String, Object>();
				delayWorkDetail.put("empId", employee.getId());
				delayWorkDetail.put("empCode", employee.getCode());
				delayWorkDetail.put("empCnName", employee.getCnName());
				//工作日志信息
				List<BaseEmpWorkLog> list = new ArrayList<BaseEmpWorkLog>();
				if(groupByEmployeeIdMap!=null&&groupByEmployeeIdMap.containsKey(employee.getId())){
					list = groupByEmployeeIdMap.get(employee.getId());
				}
				Map<Date,BaseEmpWorkLog> groupByWorkDate = list.stream().collect(Collectors.toMap(BaseEmpWorkLog :: getWorkDate,a->a,(k1,k2)->k1));
				//班次信息
				List<EmployeeClass> classList = new ArrayList<EmployeeClass>();
				if(gorupByEmployeeEmpClass!=null&&gorupByEmployeeEmpClass.containsKey(employee.getId())){
					classList = gorupByEmployeeEmpClass.get(employee.getId());
				}
				Map<Date,EmployeeClass> classGroupByWorkDate = classList.stream().collect(Collectors.toMap(EmployeeClass :: getClassDate,a->a,(k1,k2)->k1));
				
				Date start = startDay;
				Date end = lastDay;
				i = 1;
				while (true) {
					if(classGroupByWorkDate!=null&&classGroupByWorkDate.containsKey(start)){
						if(groupByWorkDate!=null&&groupByWorkDate.containsKey(start)){
							if(groupByWorkDate.get(start).getApprovalStatus()!=null
									&&groupByWorkDate.get(start).getApprovalStatus().intValue()==100){
								if(taskInfoMap!=null&&taskInfoMap.containsKey(groupByWorkDate.get(start).getProcessId())){
									List<ViewTaskInfoTbl> processInfoList = taskInfoMap.get(groupByWorkDate.get(start).getProcessId());
									if(processInfoList!=null&&processInfoList.size()==1){
										delayWorkDetail.put(DateUtils.format(start, DateUtils.FORMAT_SHORT), "待审阅_blue");
									}else if(processInfoList!=null&&processInfoList.size()==2){
										delayWorkDetail.put(DateUtils.format(start, DateUtils.FORMAT_SHORT), "审阅中_grey");
									}
								}else{
									delayWorkDetail.put(DateUtils.format(start, DateUtils.FORMAT_SHORT), "√_green");
								}
							}else if(groupByWorkDate.get(start).getApprovalStatus()!=null
									&&groupByWorkDate.get(start).getApprovalStatus().intValue()==200){
								delayWorkDetail.put(DateUtils.format(start, DateUtils.FORMAT_SHORT), "√_green");
							}else if(groupByWorkDate.get(start).getApprovalStatus()!=null
									&&groupByWorkDate.get(start).getApprovalStatus().intValue()==300){
								delayWorkDetail.put(DateUtils.format(start, DateUtils.FORMAT_SHORT), "X_red");
							}else if(groupByWorkDate.get(start).getApprovalStatus()!=null
									&&groupByWorkDate.get(start).getApprovalStatus().intValue()==400){
								delayWorkDetail.put(DateUtils.format(start, DateUtils.FORMAT_SHORT), "撤销_grey");
							}else if(groupByWorkDate.get(start).getApprovalStatus()!=null
									&&groupByWorkDate.get(start).getApprovalStatus().intValue()==500){
								delayWorkDetail.put(DateUtils.format(start, DateUtils.FORMAT_SHORT), "失效_blue");
							}else if(groupByWorkDate.get(start).getApprovalStatus()!=null
									&&groupByWorkDate.get(start).getApprovalStatus().intValue()==600){
								delayWorkDetail.put(DateUtils.format(start, DateUtils.FORMAT_SHORT), "√_green");
							}else if(groupByWorkDate.get(start).getApprovalStatus()!=null
									&&groupByWorkDate.get(start).getApprovalStatus().intValue()==700){
								delayWorkDetail.put(DateUtils.format(start, DateUtils.FORMAT_SHORT), "X_red");
							}else{
								delayWorkDetail.put(DateUtils.format(start, DateUtils.FORMAT_SHORT), "未提交_black");
							}
						}else{
							delayWorkDetail.put(DateUtils.format(start, DateUtils.FORMAT_SHORT), "未填_black");
						}
					}else{
						delayWorkDetail.put(DateUtils.format(start, DateUtils.FORMAT_SHORT), "/_grey");
					}
					start = DateUtils.addDay(start, 1);
					i = i + 1;
					if (!DateUtils.format(start, "yyyy-MM").equals(DateUtils.format(end, "yyyy-MM"))) {
						break;
					}
				}
				workLogDetailList.add(delayWorkDetail);
			}
			pm.setRows(workLogDetailList);
			pm.setTotal(total);
		}
		result.put("page", pm);
		result.put("success", true);
		
		long time2 = System.currentTimeMillis();
		logger.info("getApporvalPage uses time is:"+(time2-time1));
		
		return result;
	}

	@Override
	public HSSFWorkbook exportWorkLog(String month, Long departId,
			String leaderName, String empCode, String empCnName) {
		User user = userService.getCurrentUser();
		
        
		if(StringUtils.isBlank(month)){
			month = DateUtils.format(new Date(), DateUtils.FORMAT_YYYY_MM);
		}
		
		Date startDay = DateUtils.parse(month+"-01",DateUtils.FORMAT_SHORT);// 本月第一天
		Date lastDay = DateUtils.getLastDay(startDay);// 本月最后一天
		
		List<Map<String, Object>> workLogDetailList = new ArrayList<Map<String, Object>>();
		
		
		//根据所有下属id查询详细信息
		List<Employee> empList = new ArrayList<Employee>();
		RequestParamQueryEmpCondition hrQueryParam = new RequestParamQueryEmpCondition();
		//筛选条件
		//汇报对象
		if(StringUtils.isNotBlank(leaderName)){
			hrQueryParam.setSearchLeader(leaderName);
		}
		
		List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(user.getEmployee().getId());//下属员工
		
		if(subEmployeeIdList!=null&&subEmployeeIdList.size()>0){
			hrQueryParam.setEmployeeIdList(subEmployeeIdList);
		}

		// 部门
		hrQueryParam.setDepartId(departId);
		hrQueryParam.setEmpCode(empCode);
		hrQueryParam.setEmpCnName(empCnName);
		hrQueryParam.setChooseMonth(startDay);
		
		empList = baseEmpWorkLogMapper.getListByCondition(hrQueryParam);
		
		List<Long>  employeeIdList = new ArrayList<Long>();
		for (Employee employee : empList) {
			employeeIdList.add(employee.getId());
		}
		
		String[] keys={"code", "cnName", "departName", "workDate","workContent","workPlan","workProblem","approvalStatus"};
		String[] titles={"员工编号", "姓名", "所属部门","日期","工作内容与输出", "下一个工作日工作计划", "遇到困难和问题","状态"}; 
		List<Map<String, Object>> delayWorkDetail = new ArrayList<Map<String, Object>>();
		
		if (empList != null && empList.size() > 0) {
			//查询员工所有工作日志申请
			BaseEmpWorkLog workLogListP = new BaseEmpWorkLog();
			workLogListP.setEmployeeIdList(employeeIdList);
			workLogListP.setStartWorkDate(startDay);
			workLogListP.setEndWorkDate(lastDay);
			List<BaseEmpWorkLog> workLogList = baseEmpWorkLogMapper.selectByCondition(workLogListP);
			Map<Long,List<BaseEmpWorkLog>> groupByEmployeeIdMap = workLogList.stream().collect(Collectors.groupingBy(BaseEmpWorkLog :: getEmployeeId));
			for (Employee employee : empList) {
				List<BaseEmpWorkLog> list = new ArrayList<BaseEmpWorkLog>();
				if(groupByEmployeeIdMap!=null&&groupByEmployeeIdMap.containsKey(employee.getId())){
					list = groupByEmployeeIdMap.get(employee.getId());
				}
				for(BaseEmpWorkLog data:list){
					Map<String, Object> exportMap = new HashMap<String, Object>();
					exportMap.put("code", employee.getCode());
					exportMap.put("cnName", employee.getCnName());
					exportMap.put("departName", employee.getDepartName());
					exportMap.put("workDate",DateUtils.format(data.getWorkDate(), DateUtils.FORMAT_SHORT));
					exportMap.put("approvalStatus", getApprovalStatus(data.getApprovalStatus(),null));
					exportMap.put("workContent", "");
					exportMap.put("workPlan", "");
					exportMap.put("workProblem", "");
					if(StringUtils.isNotBlank(data.getWorkContent())){
						String workContent = "";
						String[] workContentList = data.getWorkContent().split("===");
						for(int i=0;i<workContentList.length;i++){
							workContent += (i+1)+". "+workContentList[i]+"\r\n";
						}
						exportMap.put("workContent", workContent);
					}
					if(StringUtils.isNotBlank(data.getNextDayWorkPlan())){
						String workPlan = "";
						String[] workPlanList = data.getNextDayWorkPlan().split("===");
						for(int i=0;i<workPlanList.length;i++){
							workPlan += (i+1)+". "+workPlanList[i]+"\r\n";
						}
						exportMap.put("workPlan", workPlan);
					}
					if(StringUtils.isNotBlank(data.getWorkProblem())){
						String workProblem = "";
						String[] workProblemList = data.getWorkProblem().split("===");
						for(int i=0;i<workProblemList.length;i++){
							workProblem += (i+1)+". "+workProblemList[i]+"\r\n";
						}
						exportMap.put("workProblem", workProblem);
					}
					
					delayWorkDetail.add(exportMap);
				}
			}
		}else{
			return ExcelUtil.exportExcel(delayWorkDetail, keys, titles, "工作日志报表.xls");
		}
		return ExcelUtil.exportExcel(delayWorkDetail, keys, titles, "工作日志报表.xls");
	}

	@Override
	public void sendUnSignNotice() {
		logger.info("工作日志未登记提醒start");
		Date now = new Date();
		
		if("16".equals(DateUtils.format(now, DateUtils.FORMAT_HH))){
			
			Date lastDay  = DateUtils.addDay(now, -1);
			//遍历所有员工
			Employee emp = new Employee();
			emp.setCompanyId(1L);
			List<Long> JobStatusList=new ArrayList<Long>();
			JobStatusList.add(0L);//在职
			JobStatusList.add(2L);//待离职
			emp.setJobStatusList(JobStatusList);
			emp.setDelFlag(ConfigConstants.IS_NO_INTEGER);
			emp.setWorkAddressType(0);//本地员工
			List<Employee> emps = employeeService.getListByCondition(emp);
			
			for(Employee data:emps){
				
				EmployeeClass employeeClass = new EmployeeClass(); 
				employeeClass.setEmployId(data.getId());
				employeeClass.setClassDate(lastDay);
				EmployeeClass isWorkDay = employeeClassService.getEmployeeClassSetting(employeeClass);
				if(isWorkDay==null||isWorkDay.getStartTime()==null){
					logger.info("工作日志未登记提醒:非工作日不需提醒，cn_name="+data.getCnName());
				}else{
					BaseEmpWorkLog list = baseEmpWorkLogMapper.selectEffectiveApplyByCondition(lastDay,data.getId());
					if(list==null){
						logger.info("发送未登记工作日志email：cnName="+data.getCnName());
						
						String title = "日志提醒—"+DateUtils.format(lastDay, DateUtils.FORMAT_SIMPLE);
						
						String content = "Dear "+data.getCnName()+":<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 您昨日的工作日志还未完成，请尽快前往随心邮H5端MO系统填写完成后提交，今天24点之后将无法再提交昨日的工作日志。）";
						
						content += "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;工作日志作为工作绩效评估的重要依据，请认真对待！";
						
					    try {
					    	if(!"TOM001".equals(data.getCode())){
					    		SendMailUtil.sendNormalMail(content, data.getEmail(),data.getCnName(), title);
					    	}
					    } catch (Exception e) {
					    	
					    }
					}
				}
			}
		}else{
			logger.info("工作日志未登记提醒未到时间");
		}
		
	}

}
