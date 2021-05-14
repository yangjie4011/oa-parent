package com.ule.oa.base.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.ule.oa.base.mapper.DepartMapper;
import com.ule.oa.base.mapper.EmpAttnMapper;
import com.ule.oa.base.mapper.EmpDelayHoursMapper;
import com.ule.oa.base.mapper.EmployeeMapper;
import com.ule.oa.base.mapper.RabcResourceMapper;
import com.ule.oa.base.mapper.RabcRoleMapper;
import com.ule.oa.base.mapper.RabcUserMapper;
import com.ule.oa.base.mapper.RemoveSubordinateAbsenceMapper;
import com.ule.oa.base.po.AttnSignRecord;
import com.ule.oa.base.po.AttnStatistics;
import com.ule.oa.base.po.AttnWorkHours;
import com.ule.oa.base.po.CompanyConfig;
import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.EmpAttn;
import com.ule.oa.base.po.EmpDelayHours;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.po.RabcResource;
import com.ule.oa.base.po.RabcRole;
import com.ule.oa.base.po.RemoveSubordinateAbsence;
import com.ule.oa.base.po.RequestParamQueryEmpCondition;
import com.ule.oa.base.po.User;
import com.ule.oa.base.po.tbl.ViewTaskInfoTbl;
import com.ule.oa.base.service.AnnualVacationService;
import com.ule.oa.base.service.AttnSignRecordService;
import com.ule.oa.base.service.AttnStatisticsService;
import com.ule.oa.base.service.AttnWorkHoursService;
import com.ule.oa.base.service.CommonService;
import com.ule.oa.base.service.CompanyConfigService;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.EmployeeClassService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.RemoveSubordinateAbsenceService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.service.ViewTaskInfoService;
import com.ule.oa.base.vo.TaskVO;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.ExcelUtil;
import com.ule.oa.common.utils.PageModel;
import com.ule.oa.common.utils.http.IPUtils;


@Service
public class RemoveSubordinateAbsenceServiceImpl implements RemoveSubordinateAbsenceService {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private UserService userService;
	@Autowired
	private CompanyConfigService companyConfigService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private RemoveSubordinateAbsenceMapper removeSubordinateAbsenceMapper;
	@Autowired
	private EmpAttnMapper empAttnMapper;
	@Autowired
	private AttnStatisticsService attnStatisticsService;
	@Autowired
	private AnnualVacationService annualVacationService;
	@Autowired
	private EmployeeMapper employeeMapper;
	@Autowired
	private EmpDelayHoursMapper empDelayHoursMapper;
	@Autowired
	private ViewTaskInfoService viewTaskInfoService;
	@Autowired
	private ActivitiServiceImpl activitiServiceImpl;
	@Autowired
	private EmployeeClassService employeeClassService;
	@Autowired
	private ConfigService configService;
	@Autowired
	private CommonService commonService;
	@Autowired
	private AttnSignRecordService attnSignRecordService;
	@Autowired
	private AttnWorkHoursService attnWorkHoursService;
	@Autowired
	private RabcUserMapper rabcUserMapper;
	@Autowired
	private RabcRoleMapper rabcRoleMapper;
	@Autowired
	private RabcResourceMapper rabcResourceMapper;
	@Autowired
	private DepartMapper departMapper;
	
	private final String REST_TIME_1=" 12:00:00";//休息开始时间
	private final String REST_TIME_2  =" 12:30:00";//休息中间时间
	private final String REST_TIME_3  =" 13:00:00";//休息结束时间
	private final String REST_TIME_4  =" 13:30:00";//休息结束时间
	private final String REST_TIME_5  =" 14:00:00";//休息结束时间
	
	private final String COMMIT_URL = "removeSubordinateAbsence/commitApplicationForm.htm";
	

	
	// 提交消下属缺勤
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void commitApplicationForm(HttpServletRequest request,RemoveSubordinateAbsence removeSubordinateAbsence) throws OaException {
		
		User user = userService.getCurrentUser();
		if (removeSubordinateAbsence == null) {
			throw new OaException("参数错误");
		}
		
		logger.info("消下属缺勤commitApplicationForm入参:EmployeeId="+removeSubordinateAbsence.getEmployeeId()
				+";SubmitterId="+removeSubordinateAbsence.getSubmitterId()
				+";AttendanceDate="+removeSubordinateAbsence.getAttendanceDate()
				+";AttendanceHour="+removeSubordinateAbsence.getAttendanceHour()
				+";RemoveAbsenceHours="+removeSubordinateAbsence.getRemoveAbsenceHours()
				+";Reason="+removeSubordinateAbsence.getRemoveAbsenceReason()
				+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));
		
		if (removeSubordinateAbsence.getEmployeeId() == null) {
			throw new OaException("消勤员工姓名不能为空！");
		}
		
		Depart depart = departMapper.getByEmpId(removeSubordinateAbsence.getEmployeeId());
		Long departId = depart!=null?depart.getId():null;
		RabcResource hasPrivilege = rabcResourceMapper.getOperationByUserIdAndUrlAndDeaprtId(user.getId(),COMMIT_URL,departId);
		if(hasPrivilege==null) {
			throw new OaException("您没有该操作权限！");
		}
		
		if (removeSubordinateAbsence.getAttendanceDate() == null) {
			throw new OaException("考勤日期不能为空！");
		}
		if (removeSubordinateAbsence.getAttendanceHour() == null) {
			throw new OaException("考勤时间不能为空！");
		}
		if (removeSubordinateAbsence.getRemoveAbsenceHours() == null || removeSubordinateAbsence.getRemoveAbsenceHours().doubleValue() <= 0) {
			throw new OaException("消缺勤小时数必须大于0！");
		}
		if (StringUtils.isBlank(removeSubordinateAbsence.getRemoveAbsenceReason())) {
			throw new OaException("缺勤理由不能为空！");
		}
		removeSubordinateAbsence.setSubmitterId(user.getEmployee().getId());
		
		Double removeAbsenceHours = getRemoveAbsenceHours(removeSubordinateAbsence.getEmployeeId(),removeSubordinateAbsence.getAttendanceDate());
		
		if(removeAbsenceHours>removeSubordinateAbsence.getRemoveAbsenceHours()) {
			throw new OaException("消缺勤小时数须大于实际缺勤小时数！");
		}
		
		// 判断是否在审批时间范围内
		Date now = new Date();
		Date attendanceMonth = DateUtils.getFirstDay(removeSubordinateAbsence.getAttendanceDate());
		Date startMonth = DateUtils.parse(DateUtils.getYear(removeSubordinateAbsence.getAttendanceDate())+"-01-01", DateUtils.FORMAT_SHORT);
		Date nextMonth = DateUtils.addMonth(attendanceMonth, 1);
		//次月第五个工作日
		Config configCondition = new Config();
		configCondition.setCode("timeLimit5");
		List<Config> limit = configService.getListByCondition(configCondition);
		int num = Integer.valueOf(limit.get(0).getDisplayCode());
		
		//如果是人事部门，时间放成5天
		if("107".equals(user.getDepart().getCode())) {
			//次月第五个工作日
			configCondition.setCode("timeLimit7");
			limit = configService.getListByCondition(configCondition);
			num = Integer.valueOf(limit.get(0).getDisplayCode());
		}

		Date lastSubDate = annualVacationService.getWorkingDayOfMonth(nextMonth, num+1);
		if (DateUtils.getIntervalDays(now, lastSubDate) < 0) {
			throw new OaException("本月3个工作日前可申请上月异常！");
		}
		// 判断员工是否为标准工时
		CompanyConfig condition = new CompanyConfig();
		condition.setCode("typeOfWork");
		condition.setDisplayCode("standard");
		CompanyConfig companyConfig = companyConfigService.getByCondition(condition);
		Employee employee = employeeMapper.getById(removeSubordinateAbsence.getEmployeeId());
		if (!companyConfig.getId().equals(employee.getWorkType())) {
			throw new OaException("消下属缺勤消除对象仅适用标准工时员工！");
		}
		// 判断是否有审核中的单据
		List<RemoveSubordinateAbsence> list = removeSubordinateAbsenceMapper.getByEmpAndDate(
				removeSubordinateAbsence.getEmployeeId(), removeSubordinateAbsence.getAttendanceDate());
		if (list != null && list.size() > 0) {
			for (RemoveSubordinateAbsence exitApplication : list) {
				Integer approalStatus = exitApplication.getApproalStatus().intValue();
				if (approalStatus != null
						&& (approalStatus.equals(ConfigConstants.DOING_STATUS)
								|| approalStatus.equals(ConfigConstants.PASS_STATUS)
								|| approalStatus.equals(ConfigConstants.OVERDUE_STATUS)
								|| approalStatus.equals(ConfigConstants.OVERDUEPASS_STATUS))) {
					throw new OaException("已提交申请无法再次提交！");
				}
			}
		}
		// 判断出勤多余小时数是否小于消缺勤小时数
		EmpDelayHours empDelayHours = empDelayHoursMapper.getCountByEmpAndMonth(employee.getId(), startMonth, attendanceMonth);
		double overHoursOfAttendance = 0D;
		if (empDelayHours != null) {
			// 出勤多余小时数
			overHoursOfAttendance = empDelayHours.getTotalDelayHours().doubleValue()
					- empDelayHours.getUsedDelayHours().doubleValue()
					- empDelayHours.getLockedDelayHours().doubleValue();
			if (overHoursOfAttendance < removeSubordinateAbsence.getRemoveAbsenceHours().doubleValue()) {
				throw new OaException("该员工出勤多余小时数不足，无法申请！");
			}
		} else {
			throw new OaException("该员工出勤多余小时数不足，无法申请！");
		}
		
		//锁定
		lockDelayHours(employee.getId(),startMonth,attendanceMonth,removeSubordinateAbsence.getRemoveAbsenceHours(),user);
		
		removeSubordinateAbsence.setOverHoursOfAttendance(overHoursOfAttendance);
		removeSubordinateAbsence.setApproalStatus(ConfigConstants.DOING_STATUS.longValue());
		removeSubordinateAbsence.setCreateTime(now);
		removeSubordinateAbsence.setCreateUser(user.getEmployee().getId().toString());
		removeSubordinateAbsence.setDelFlag(0);
		
		// 启动流程
		Map<String,Object> v = Maps.newHashMap();
		v.put("proposerId", removeSubordinateAbsence.getEmployeeId());
		String processInstanceId = activitiServiceImpl.startByKey(ConfigConstants.REMOVESUBABSENCE_KEY,v);
		removeSubordinateAbsence.setProcessinstanceId(processInstanceId);
		//创建申请单
		removeSubordinateAbsenceMapper.insertRemoveSubordinateAbsence(removeSubordinateAbsence);
		
		// -----------------start-----------------------保存流程节点信息
		ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
		flow.setAssigneeName(user.getEmployee().getCnName());
		flow.setDepartName(user.getDepart().getName());
		flow.setPositionName(user.getPosition().getPositionName());
		flow.setFinishTime(new Date());
		flow.setComment(removeSubordinateAbsence.getRemoveAbsenceReason());
		flow.setProcessId(processInstanceId);
		flow.setProcessKey(ConfigConstants.REMOVESUBABSENCE_KEY);
		flow.setStatu(0);
		viewTaskInfoService.save(flow);
	}
	
	public Double getRemoveAbsenceHours(Long employeeId,Date attendanceDate) {
		Double removeAbsenceHours = 0d;
		Double removeAbsenceHoursReduce = 0d;
		
		AttnStatistics attnStatistics = attnStatisticsService.getAttnStatistics(employeeId,attendanceDate);
		if(attnStatistics!=null){
			removeAbsenceHours = (attnStatistics.getMustAttnTime()!=null?attnStatistics.getMustAttnTime():0)
					- (attnStatistics.getActAttnTime()!=null?attnStatistics.getActAttnTime():0);
			
			EmployeeClass toadycondition = new EmployeeClass();
			toadycondition.setEmployId(employeeId);
			toadycondition.setClassDate(attendanceDate);
			EmployeeClass toadyEmpClass = employeeClassService.getEmployeeClassSetting(toadycondition);
			/**
			 * 9-18点班次
			 * 考勤开始或者结束时间在有在12:00-13:00的范围，去除12:00-13:00的休息时间
			 */
			if(toadyEmpClass!=null
					&&"09:00:00".equals(DateUtils.format(toadyEmpClass.getStartTime(), DateUtils.FORMAT_HH_MM_SS))
					&&"18:00:00".equals(DateUtils.format(toadyEmpClass.getEndTime(), DateUtils.FORMAT_HH_MM_SS))){
				Date restTime_1 = DateUtils.parse(DateUtils.format(attnStatistics.getAttnDate(), DateUtils.FORMAT_SHORT)+REST_TIME_1, DateUtils.FORMAT_LONG);
				Date restTime_2 = DateUtils.parse(DateUtils.format(attnStatistics.getAttnDate(), DateUtils.FORMAT_SHORT)+REST_TIME_2, DateUtils.FORMAT_LONG);
				Date restTime_3 = DateUtils.parse(DateUtils.format(attnStatistics.getAttnDate(), DateUtils.FORMAT_SHORT)+REST_TIME_3, DateUtils.FORMAT_LONG);
				Date restTime_4 = DateUtils.parse(DateUtils.format(attnStatistics.getAttnDate(), DateUtils.FORMAT_SHORT)+REST_TIME_4, DateUtils.FORMAT_LONG);
				Date restTime_5 = DateUtils.parse(DateUtils.format(attnStatistics.getAttnDate(), DateUtils.FORMAT_SHORT)+REST_TIME_5, DateUtils.FORMAT_LONG);
				
				//上班
				if(attnStatistics.getStartWorkTime()!=null
						&&attnStatistics.getStartWorkTime().getTime()>restTime_1.getTime()
						&&attnStatistics.getStartWorkTime().getTime()<=restTime_3.getTime()){
					if(attnStatistics.getStartWorkTime().getTime()<=restTime_2.getTime()){
						removeAbsenceHoursReduce += 0.5;
					}
					if(attnStatistics.getStartWorkTime().getTime()>restTime_2.getTime()){
						removeAbsenceHoursReduce += 1;
					}
				}
				//下班
				if(attnStatistics.getEndWorkTime()!=null
						&&attnStatistics.getEndWorkTime().getTime()>=restTime_2.getTime()
						&&attnStatistics.getEndWorkTime().getTime()<restTime_5.getTime()){
					if(attnStatistics.getEndWorkTime().getTime()>=restTime_2.getTime()&&
							attnStatistics.getEndWorkTime().getTime()<restTime_3.getTime()){
						removeAbsenceHoursReduce -= 0.5;
					}
					
					if(attnStatistics.getEndWorkTime().getTime()>=restTime_3.getTime()&&
							attnStatistics.getEndWorkTime().getTime()<restTime_4.getTime()){
						removeAbsenceHoursReduce -= 1;
					}
					
					if(attnStatistics.getEndWorkTime().getTime()>=restTime_4.getTime()&&
							attnStatistics.getEndWorkTime().getTime()<restTime_5.getTime()){
						removeAbsenceHoursReduce -= 1;
					}
					
				}
				
				//满天缺勤有问题不处理
				if(removeAbsenceHours<8){
					removeAbsenceHours = removeAbsenceHours - removeAbsenceHoursReduce;
				}
				
			}
		}
		
		return removeAbsenceHours;
	}
	
	//锁定登记小时数（提交申请时调用）
	public void lockDelayHours(Long employeeId,Date startMonth,Date endMonth, Double removeAbsenceHours,User user){
		
		List<EmpDelayHours> surplusList = empDelayHoursMapper.getByEmpAndYear(employeeId, startMonth, endMonth);
		
		for(EmpDelayHours data:surplusList){
			
			if(removeAbsenceHours<=0){
				break;
			}
			
			double surplusHors = data.getTotalDelayHours() - data.getUsedDelayHours() - data.getLockedDelayHours();
			if(surplusHors>=removeAbsenceHours){
				EmpDelayHours lockEmpDelayHours = new EmpDelayHours();
				lockEmpDelayHours.setId(data.getId());
				lockEmpDelayHours.setLockedDelayHours(removeAbsenceHours + data.getLockedDelayHours().doubleValue());
				lockEmpDelayHours.setUpdateTime(new Date());
				lockEmpDelayHours.setUpdateUser(user.getEmployee().getId().toString());
				empDelayHoursMapper.updateEmpDelayHours(lockEmpDelayHours);
				removeAbsenceHours = 0d;
				break;
			}else{
				EmpDelayHours lockEmpDelayHours = new EmpDelayHours();
				lockEmpDelayHours.setId(data.getId());
				lockEmpDelayHours.setLockedDelayHours(surplusHors + data.getLockedDelayHours().doubleValue());
				lockEmpDelayHours.setUpdateTime(new Date());
				lockEmpDelayHours.setUpdateUser(user.getEmployee().getId().toString());
				empDelayHoursMapper.updateEmpDelayHours(lockEmpDelayHours);
				removeAbsenceHours = removeAbsenceHours - surplusHors;
			}
		}
		
	}
	
	//扣减锁定登记小时数（通过申请时调用）
	public void reduceDelayHours(Long employeeId,Date startMonth,Date endMonth, Double removeAbsenceHours,User user){
		
		List<EmpDelayHours> lockedList = empDelayHoursMapper.getLockedList(employeeId, startMonth, endMonth,"asc");
		
		for(EmpDelayHours data:lockedList){
			
			if(removeAbsenceHours<=0){
				break;
			}
			
			if(data.getLockedDelayHours()>=removeAbsenceHours){
				//锁定扣减
				EmpDelayHours empDelayHours = new EmpDelayHours();
				empDelayHours.setId(data.getId());
				empDelayHours.setLockedDelayHours(data.getLockedDelayHours() - removeAbsenceHours);
				//增加已用
				empDelayHours.setUsedDelayHours(data.getUsedDelayHours() + removeAbsenceHours);
				empDelayHours.setUpdateTime(new Date());
				empDelayHours.setUpdateUser(user.getEmployee().getId().toString());
				empDelayHoursMapper.updateEmpDelayHours(empDelayHours);
				removeAbsenceHours = 0d;
			}else{
				//锁定扣减
				EmpDelayHours empDelayHours = new EmpDelayHours();
				empDelayHours.setId(data.getId());
				empDelayHours.setLockedDelayHours(0d);
				//增加已用
				empDelayHours.setUsedDelayHours(data.getUsedDelayHours() + data.getLockedDelayHours());
				empDelayHours.setUpdateTime(new Date());
				empDelayHours.setUpdateUser(user.getEmployee().getId().toString());
				empDelayHoursMapper.updateEmpDelayHours(empDelayHours);
				removeAbsenceHours = removeAbsenceHours - data.getLockedDelayHours();
			}
			
		}
		
	}
	
	//还原锁定登记小时数（拒绝，撤销申请时调用）
	public void restoreDelayHours(Long employeeId,Date startMonth,Date endMonth, Double removeAbsenceHours,User user){
		
		List<EmpDelayHours> lockedList = empDelayHoursMapper.getLockedList(employeeId, startMonth, endMonth,"desc");
		
		for(EmpDelayHours data:lockedList){
			
			if(removeAbsenceHours<=0){
				break;
			}
			
			if(data.getLockedDelayHours()>=removeAbsenceHours){
				//还原扣减
				EmpDelayHours empDelayHours = new EmpDelayHours();
				empDelayHours.setId(data.getId());
				empDelayHours.setLockedDelayHours(data.getLockedDelayHours() - removeAbsenceHours);
				empDelayHours.setUpdateTime(new Date());
				empDelayHours.setUpdateUser(user.getEmployee().getId().toString());
				empDelayHoursMapper.updateEmpDelayHours(empDelayHours);
				removeAbsenceHours = 0d;
			}else{
				//还原扣减
				EmpDelayHours empDelayHours = new EmpDelayHours();
				empDelayHours.setId(data.getId());
				empDelayHours.setLockedDelayHours(0d);
				empDelayHours.setUpdateTime(new Date());
				empDelayHours.setUpdateUser(user.getEmployee().getId().toString());
				empDelayHoursMapper.updateEmpDelayHours(empDelayHours);
				removeAbsenceHours = removeAbsenceHours - data.getLockedDelayHours();
			}
			
		}
		
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void completeTask(HttpServletRequest request,String processId, String comment, String commentType) throws OaException {
		
		User user = userService.getCurrentUser();
		logger.info("消下属缺勤completeTask入参:processId="+processId+";comment="+comment+";commentType="+commentType
				+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));
		
		Date now = new Date();
		Task task = activitiServiceImpl.queryTaskByProcessInstanceId(processId);
		if (task == null) {
			logger.error("实例Id为" + processId + "的流程不存在！");
			throw new OaException("任务已完成！");
		}
		boolean type = StringUtils.startsWithIgnoreCase(task.getTaskDefinitionKey(), "personnel");
		RemoveSubordinateAbsence removeSubordinateAbsence = removeSubordinateAbsenceMapper
				.getByProcessId(processId);
		if (removeSubordinateAbsence == null) {
			logger.error("流程实例为" + processId + "的审批数据不存在！");
			throw new OaException("任务已完成！");
		}
		//查询当月该员工的延时工作小时总数
		Date delayDate = DateUtils.getFirstDay(removeSubordinateAbsence.getAttendanceDate());
		Date startMonth = DateUtils.parse(DateUtils.getYear(removeSubordinateAbsence.getAttendanceDate())+"-01-01", DateUtils.FORMAT_SHORT);
		EmpDelayHours empDelayHours = empDelayHoursMapper.getCountByEmpAndMonth(removeSubordinateAbsence.getEmployeeId(), startMonth, delayDate);
		if(empDelayHours == null ||empDelayHours.getLockedDelayHours().doubleValue() == 0
				||empDelayHours.getLockedDelayHours().doubleValue() < removeSubordinateAbsence.getRemoveAbsenceHours()){
			throw new OaException("延时工作小时数不足！");
		}
		//判断是否超过审批时间
		Date attendanceMonth = DateUtils.getFirstDay(removeSubordinateAbsence.getAttendanceDate());
		Date nextMonth = DateUtils.addMonth(attendanceMonth, 1);
		//次月第五个工作日
		Config configCondition = new Config();
		configCondition.setCode("timeLimit7");
		List<Config> limit = configService.getListByCondition(configCondition);
		int num = Integer.valueOf(limit.get(0).getDisplayCode());

		Date lastSubDate = annualVacationService.getWorkingDayOfMonth(nextMonth, num+1);
		//同意或拒绝增加时间限制
		if ((StringUtils.equalsIgnoreCase(commentType, ConfigConstants.PASS)|| StringUtils.equalsIgnoreCase(commentType, ConfigConstants.REFUSE))
				&&DateUtils.getIntervalDays(now, lastSubDate) < 0) {
			throw new OaException("本月5个工作日前可审批上月异常！");
		}
		Integer approvalStatus;
		if (StringUtils.equalsIgnoreCase(commentType, ConfigConstants.PASS)) {
			/**同意**/
			approvalStatus = type ? ConfigConstants.PASS_STATUS : ConfigConstants.DOING_STATUS;
			// 判断当前节点是否有办理人assignee,没有则将当前代理人设为办理人
			if (StringUtils.isBlank(task.getAssignee())) {
				activitiServiceImpl.claimTask(task.getId(),
						String.valueOf(userService.getCurrentUser().getEmployeeId()));
			}
			//如果状态为审批通过则代表流程结束，扣除锁定小时数，增加使用小时数
			if(ConfigConstants.PASS_STATUS.equals(approvalStatus)){
				
				reduceDelayHours(removeSubordinateAbsence.getEmployeeId(),startMonth,delayDate, 
						removeSubordinateAbsence.getRemoveAbsenceHours(),user);
				
				//重算考勤
				Employee employee = employeeService.getById(removeSubordinateAbsence.getEmployeeId());
				AttnWorkHours attnWorkHours = new AttnWorkHours();
				attnWorkHours.setCompanyId(employee.getCompanyId());
				attnWorkHours.setEmployeeId(removeSubordinateAbsence.getEmployeeId());
				attnWorkHours.setWorkDate(removeSubordinateAbsence.getAttendanceDate());
				attnWorkHours.setDataType(Integer.parseInt(ConfigConstants.REMOVESUBABSENCE_CODE));
				
				Double removeAbsenceHoursReduce = 0d;
				/**
				 * 9-18点班次
				 * 考勤开始或者结束时间在有在12:00-13:00的范围，补上12:00-13:00的休息时间
				 */
				AttnStatistics attnStatistics = attnStatisticsService.getAttnStatistics(removeSubordinateAbsence.getEmployeeId(),removeSubordinateAbsence.getAttendanceDate());
				if(attnStatistics!=null){
					EmployeeClass toadycondition = new EmployeeClass();
					toadycondition.setEmployId(removeSubordinateAbsence.getEmployeeId());
					toadycondition.setClassDate(removeSubordinateAbsence.getAttendanceDate());
					EmployeeClass toadyEmpClass = employeeClassService.getEmployeeClassSetting(toadycondition);
					
					if(toadyEmpClass!=null
							&&"09:00:00".equals(DateUtils.format(toadyEmpClass.getStartTime(), DateUtils.FORMAT_HH_MM_SS))
							&&"18:00:00".equals(DateUtils.format(toadyEmpClass.getEndTime(), DateUtils.FORMAT_HH_MM_SS))){
						Date restTime_1 = DateUtils.parse(DateUtils.format(attnStatistics.getAttnDate(), DateUtils.FORMAT_SHORT)+REST_TIME_1, DateUtils.FORMAT_LONG);
						Date restTime_2 = DateUtils.parse(DateUtils.format(attnStatistics.getAttnDate(), DateUtils.FORMAT_SHORT)+REST_TIME_2, DateUtils.FORMAT_LONG);
						Date restTime_3 = DateUtils.parse(DateUtils.format(attnStatistics.getAttnDate(), DateUtils.FORMAT_SHORT)+REST_TIME_3, DateUtils.FORMAT_LONG);
						Date restTime_4 = DateUtils.parse(DateUtils.format(attnStatistics.getAttnDate(), DateUtils.FORMAT_SHORT)+REST_TIME_4, DateUtils.FORMAT_LONG);
						Date restTime_5 = DateUtils.parse(DateUtils.format(attnStatistics.getAttnDate(), DateUtils.FORMAT_SHORT)+REST_TIME_5, DateUtils.FORMAT_LONG);
						
						//上班
						if(attnStatistics.getStartWorkTime()!=null
								&&attnStatistics.getStartWorkTime().getTime()>restTime_1.getTime()
								&&attnStatistics.getStartWorkTime().getTime()<=restTime_3.getTime()){
							if(attnStatistics.getStartWorkTime().getTime()<=restTime_2.getTime()){
								removeAbsenceHoursReduce += 0.5;
							}
							if(attnStatistics.getStartWorkTime().getTime()>restTime_2.getTime()){
								removeAbsenceHoursReduce += 1;
							}
						}
						//下班
						if(attnStatistics.getEndWorkTime()!=null
								&&attnStatistics.getEndWorkTime().getTime()>=restTime_2.getTime()
								&&attnStatistics.getEndWorkTime().getTime()<restTime_5.getTime()){
							if(attnStatistics.getEndWorkTime().getTime()>=restTime_2.getTime()&&
									attnStatistics.getEndWorkTime().getTime()<restTime_3.getTime()){
								removeAbsenceHoursReduce -= 0.5;
							}
							
							if(attnStatistics.getEndWorkTime().getTime()>=restTime_3.getTime()&&
									attnStatistics.getEndWorkTime().getTime()<restTime_4.getTime()){
								removeAbsenceHoursReduce -= 1;
							}
							
							if(attnStatistics.getEndWorkTime().getTime()>=restTime_4.getTime()&&
									attnStatistics.getEndWorkTime().getTime()<restTime_5.getTime()){
								removeAbsenceHoursReduce -= 1;
							}
							
						}
						
					}
				}
				
				attnWorkHours.setWorkHours(removeSubordinateAbsence.getRemoveAbsenceHours()+removeAbsenceHoursReduce);
				attnWorkHours.setDelFlag(0);
				attnWorkHours.setCreateTime(now);
				attnWorkHours.setCreateUser(user.getEmployee().getId().toString());
				attnWorkHours.setDataReason(removeSubordinateAbsence.getRemoveAbsenceReason());
				attnWorkHours.setBillId(removeSubordinateAbsence.getId());
				attnStatisticsService.recalculationAttForRemoveSubAbsence(attnWorkHours);
			}
		} else if (StringUtils.equalsIgnoreCase(commentType, ConfigConstants.REFUSE)) {
			/**拒绝**/
			approvalStatus = ConfigConstants.REFUSE_STATUS;
			// 判断当前节点是否有办理人assignee,没有则将当前代理人设为办理人
			if (StringUtils.isBlank(task.getAssignee())) {
				activitiServiceImpl.claimTask(task.getId(),
						String.valueOf(userService.getCurrentUser().getEmployeeId()));
			}
			
			restoreDelayHours(removeSubordinateAbsence.getEmployeeId(),startMonth,delayDate, 
					removeSubordinateAbsence.getRemoveAbsenceHours(),user);
			
		}else if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.OVERDUEPASS)){
			/**失效同意**/
			approvalStatus = type ? ConfigConstants.OVERDUEPASS_STATUS : ConfigConstants.DOING_STATUS;
			// 判断当前节点是否有办理人assignee,没有则将当前代理人设为办理人
			if (StringUtils.isBlank(task.getAssignee())) {
				activitiServiceImpl.claimTask(task.getId(),
						String.valueOf(userService.getCurrentUser().getEmployeeId()));
			}
			//如果状态为审批通过则代表流程结束，扣除锁定小时数，增加使用小时数
			if(ConfigConstants.OVERDUEPASS_STATUS.equals(approvalStatus)){
				
				reduceDelayHours(removeSubordinateAbsence.getEmployeeId(),startMonth,delayDate, 
						removeSubordinateAbsence.getRemoveAbsenceHours(),user);
				
				//重算考勤
				Employee employee = employeeService.getById(removeSubordinateAbsence.getEmployeeId());
				AttnWorkHours attnWorkHours = new AttnWorkHours();
				attnWorkHours.setCompanyId(employee.getCompanyId());
				attnWorkHours.setEmployeeId(removeSubordinateAbsence.getEmployeeId());
				attnWorkHours.setWorkDate(removeSubordinateAbsence.getAttendanceDate());
				attnWorkHours.setDataType(Integer.parseInt(ConfigConstants.REMOVESUBABSENCE_CODE));
				attnWorkHours.setWorkHours(removeSubordinateAbsence.getRemoveAbsenceHours());
				attnWorkHours.setDelFlag(0);
				attnWorkHours.setCreateTime(now);
				attnWorkHours.setCreateUser(user.getEmployee().getId().toString());
				attnWorkHours.setDataReason(removeSubordinateAbsence.getRemoveAbsenceReason());
				attnWorkHours.setBillId(removeSubordinateAbsence.getId());
				attnStatisticsService.recalculationAttForRemoveSubAbsence(attnWorkHours);
			}
			
		}else if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.OVERDUEREFUSE)){
			/**失效拒绝**/
			approvalStatus = ConfigConstants.OVERDUEREFUSE_STATUS;
			// 判断当前节点是否有办理人assignee,没有则将当前代理人设为办理人
			if (StringUtils.isBlank(task.getAssignee())) {
				activitiServiceImpl.claimTask(task.getId(),
						String.valueOf(userService.getCurrentUser().getEmployeeId()));
			}
			
			restoreDelayHours(removeSubordinateAbsence.getEmployeeId(),startMonth,delayDate, 
					removeSubordinateAbsence.getRemoveAbsenceHours(),user);
			
		}else {
			/**撤回**/
			approvalStatus = ConfigConstants.BACK_STATUS;
			
			restoreDelayHours(removeSubordinateAbsence.getEmployeeId(),startMonth,delayDate, 
					removeSubordinateAbsence.getRemoveAbsenceHours(),user);
		}
		//修改申请单审批状态
		RemoveSubordinateAbsence updateStatus = new RemoveSubordinateAbsence();
		updateStatus.setId(removeSubordinateAbsence.getId());
		updateStatus.setApproalStatus(approvalStatus.longValue());
		updateStatus.setApproalReason(comment);
		updateStatus.setUpdateTime(new Date());
		updateStatus.setUpdateUser(user.getEmployee().getId().toString());
		removeSubordinateAbsenceMapper.updateById(updateStatus);
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
	
	/**
	 * 查询消下属缺勤
	 * @throws OaException 
	 */
	@Override
	public Map<String, Object> getRemoveSubordinateAbsencePage(Long departId,String date, String empCode, String empCnName,
			Integer page, Integer rows) throws OaException {
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
		boolean isCurrentMonth = false;//是否是当月
		//如果是当月，应出勤算到前一天
		if(DateUtils.format(startDay,"yyyy-MM").equals(DateUtils.format(new Date(),"yyyy-MM"))){
			isCurrentMonth =true;
		}
		// 表头，星期和天数
		List<String> weekDays = new ArrayList<String>();
		// 表头，天数
		List<String> days = new ArrayList<String>();
		// 表头，日期
		List<String> dates = new ArrayList<String>();
		days.add("员工编号");
		days.add("姓名");
		days.add("出勤小时数（月累计/应出勤）");
		days.add("出勤多余小时数");
		days.add("异常考勤天数");
		weekDays.add("");
		weekDays.add("");
		weekDays.add("");
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
		result.put("weekDays", weekDays);
		result.put("days", days);
		result.put("dates", dates);

		PageModel<Map<String, Object>> pm = new PageModel<Map<String, Object>>();
		List<Map<String, Object>> attnDetailList = new ArrayList<Map<String, Object>>();
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
		    	List<Long> idList = employeeService.getSubEmployeeList(user.getEmployeeId(), role.getDepartId(),isGetAll);
		    	employeeIdList.addAll(idList);
		    	hasGetMap.put(role.getDepartId(), true);
		    	continue;
		    }
		    //一般角色获取所有标准工时下属数据（可能多级）
		    List<Long> idList = employeeService.getSubEmployeeList(user.getEmployeeId(), role.getDepartId(),isGetAll);
		    employeeIdList.addAll(idList);
			
		}
		
		//所有下属id列表
		if(employeeIdList==null || employeeIdList.size()<=0){
			pm.setRows(attnDetailList);
			pm.setTotal(0);
			result.put("page", pm);
			result.put("success", true);
			return result;
		}

		RequestParamQueryEmpCondition requestParamQueryEmpByCondition = new RequestParamQueryEmpCondition();
		//所有下属
		requestParamQueryEmpByCondition.setEmployeeIdList(employeeIdList);
		// 标准工时类型
		if (companyConfig != null) {
			requestParamQueryEmpByCondition.setWorkType(companyConfig.getId());
		}
		if(departId!=null){
			requestParamQueryEmpByCondition.setDepartId(departId);
		}
		if(StringUtils.isNotBlank(empCode)){
			requestParamQueryEmpByCondition.setEmpCode(empCode);
		}
		if(StringUtils.isNotBlank(empCnName)){
			requestParamQueryEmpByCondition.setEmpCnName(empCnName);
		}
		//查询指定员工类型数据
		requestParamQueryEmpByCondition.setEmpTypeIdList(configService.getNeedEmpTypeIdList());
		requestParamQueryEmpByCondition.setChooseMonth(startDay);
		
		//下属总数
		int total = employeeService.getStandardEmpCountByCount(requestParamQueryEmpByCondition);
		
		requestParamQueryEmpByCondition.setOffset(offset);
		requestParamQueryEmpByCondition.setLimit(limit);
		
		//分页查询下属
		List<Employee> subordinateEmp = employeeService
				.getAllEmpByWorkTypeAndLeaderAndDepart(requestParamQueryEmpByCondition);
		
		//是否排班属性
		Map<Long,String> whetherSchedulingMap = new HashMap<Long,String>();
		Config whetherScheduling = new Config();
        whetherScheduling.setCode("whetherScheduling");
        List<Config> whetherSchedulingLsit = configService.getListByCondition(whetherScheduling);
        for(Config data:whetherSchedulingLsit){
        	whetherSchedulingMap.put(data.getId(), data.getDisplayCode());
        }
		
		if (subordinateEmp != null && subordinateEmp.size() > 0) {
			
			Map<Long,Double> actAttnTimeMap = new HashMap<Long,Double>();//标准出勤工时
			Map<Long,Double> delayHoursMap = new HashMap<Long,Double>();//月延时工作登记时间
			Map<Long,Double> attnSurplusHoursMap = new HashMap<Long,Double>();//出勤多余小时数
			Map<Long,List<EmpAttn>> abnormalAttnMap = new HashMap<Long,List<EmpAttn>>();//异常考勤次数
			Map<Long,List<RemoveSubordinateAbsence>> applyMap = new HashMap<Long,List<RemoveSubordinateAbsence>>();//销下属考勤申请
			Map<Long,Double> mustAttnTimeMap = new HashMap<Long,Double>();//应出勤小时数
			
			List<Long> employIdList = new ArrayList<Long>();
			for(Employee data:subordinateEmp){
				employIdList.add(data.getId());
				actAttnTimeMap.put(data.getId(), 0d);//初始化为0
				delayHoursMap.put(data.getId(), 0d);//初始化为0
				attnSurplusHoursMap.put(data.getId(), 0d);//初始化为0
				List<EmpAttn> attnList = new ArrayList<EmpAttn>();
				abnormalAttnMap.put(data.getId(), attnList);
				List<RemoveSubordinateAbsence> applyList = new ArrayList<RemoveSubordinateAbsence>();
				applyMap.put(data.getId(), applyList);
				mustAttnTimeMap.put(data.getId(), 0d);
			}
			
			//统计多个员工的标准出勤工时
			List<AttnStatistics> actAttnTimeList = attnStatisticsService.getActAttnTimeGroupByemployIds(employIdList, startDay, lastDay);
			for(AttnStatistics data:actAttnTimeList){
				actAttnTimeMap.put(data.getEmployId(), 0d);
				if(data.getActAttnTimeCount()!=null){
					actAttnTimeMap.put(data.getEmployId(), data.getActAttnTimeCount());
				}
			}
			
			//统计多个员工的月延时工作登记时间+出勤多余小时数
			Date startMonth = DateUtils.parse(DateUtils.getYear(startDay)+"-01-01", DateUtils.FORMAT_SHORT);
			List<EmpDelayHours> delayHoursList = empDelayHoursMapper.getByemployIdListAndMonth(employIdList,startMonth,startDay);
			for(EmpDelayHours data:delayHoursList){
				delayHoursMap.put(data.getEmployeeId(),data.getTotalDelayHours());
				attnSurplusHoursMap.put(data.getEmployeeId(),data.getTotalDelayHours()-data.getUsedDelayHours()-data.getLockedDelayHours());
			}
			
			//统计员工的异常考勤
			List<EmpAttn> abnormalAttnList = empAttnMapper.getAbnormalAttnList(employIdList, startDay, lastDay);
			for(EmpAttn data:abnormalAttnList){
				abnormalAttnMap.get(data.getEmployId()).add(data);
			}
			
			//查询员工消下属缺勤申请
			List<RemoveSubordinateAbsence> subAbsenceList = removeSubordinateAbsenceMapper.getListByEmployIdsandDate(employIdList, startDay, lastDay);
			for(RemoveSubordinateAbsence data:subAbsenceList){
				applyMap.get(data.getEmployeeId()).add(data);
			}
			
			//统计应出勤小时数
			EmployeeClass employeeClass = new EmployeeClass();
			employeeClass.setStartTime(startDay);
			employeeClass.setEndTime(lastDay);
			if(isCurrentMonth){
				employeeClass.setEndTime(DateUtils.addDay(new Date(), -1));
			}
			employeeClass.setEmployeeIds(employIdList);
			List<Map<String,Object>> mustAttnTimeList = employeeClassService.getClassHoursMapList(employeeClass);
			for(Map<String,Object> data:mustAttnTimeList){
				mustAttnTimeMap.put((Long) data.get("employ_id"), data.get("must_attn_time")!=null?(double) data.get("must_attn_time"):0d);
			}
			for (Employee employee : subordinateEmp) {
				//统计每个员工的应出勤（要考虑入离职时间-标准工时非排班，排班人员直接统计排班数据）
				double mustAttnTime = 0d;//应出勤（出勤天数*8）
				if("no".equals(whetherSchedulingMap.get(employee.getWhetherScheduling()))){
					Date startTime = startDay;
					Date endTime = lastDay;
					if(employee.getFirstEntryTime()!=null&&DateUtils.format(employee.getFirstEntryTime(),DateUtils.FORMAT_YYYY_MM).equals(DateUtils.format(startDay,DateUtils.FORMAT_YYYY_MM))){
						startTime = employee.getFirstEntryTime();
					}
					if(employee.getQuitTime()!=null&&DateUtils.format(employee.getQuitTime(),DateUtils.FORMAT_YYYY_MM).equals(DateUtils.format(startDay,DateUtils.FORMAT_YYYY_MM))){
						endTime = employee.getQuitTime();
					}
					if(isCurrentMonth){
						endTime = DateUtils.addDay(new Date(), -1);
					}
					mustAttnTime = commonService.getWorkDaysCount(startTime, endTime)*8.0;
				}else{
					mustAttnTime = mustAttnTimeMap.get(employee.getId());
				}
				
				//月累计（标准出勤工时+月延时工作登记时间==出勤多余小时数）
				double actAttnTime = actAttnTimeMap.get(employee.getId())+delayHoursMap.get(employee.getId());
				
				//出勤多余小时数
				double overHoursOfAttendance = attnSurplusHoursMap.get(employee.getId());
				
				Map<String, Object> attnDetail = new HashMap<String, Object>();
				attnDetail.put("empId", employee.getId());
				attnDetail.put("empCode", employee.getCode());
				attnDetail.put("empCnName", employee.getCnName());
				attnDetail.put("empAttnCount", actAttnTime+"/"+mustAttnTime);
				attnDetail.put("overEmpAttnHours", overHoursOfAttendance);
				attnDetail.put("leaderName", employee.getLeaderName());
				attnDetail.put("leaderId", employee.getLeaderId());
				List<EmpAttn> monthAbnormalAttendanceList = abnormalAttnMap.get(employee.getId());
				//异常考勤次数
				int attnStatusCount = 0;
				if (monthAbnormalAttendanceList != null && monthAbnormalAttendanceList.size() > 0) {
					attnStatusCount = monthAbnormalAttendanceList.size();
					for (EmpAttn abnormalAttn : monthAbnormalAttendanceList) {
						abnormalAttn.setFontColor("black");
						String day = DateUtils.getDayOfMonth(abnormalAttn.getAttnDate());
						attnDetail.put(Integer.valueOf(day).toString(), abnormalAttn);
					}
				}
				
				List<RemoveSubordinateAbsence> removeSubordinateAbsencelist = applyMap.get(employee.getId());
				for (RemoveSubordinateAbsence removeSubordinateAbsences : removeSubordinateAbsencelist) {
					EmpAttn removeSubordinateAbsenceAttn=new EmpAttn();
					switch (removeSubordinateAbsences.getApproalStatus().intValue()) {
					case 100:
						removeSubordinateAbsenceAttn.setFontColor("blue");
						break;
					case 200:
						removeSubordinateAbsenceAttn.setFontColor("gray");
						attnStatusCount+=1;
						break;	
					case 300:
						removeSubordinateAbsenceAttn.setFontColor("black");
						break;
					case 500://失效 变成蓝色字体
						removeSubordinateAbsenceAttn.setFontColor("blue");
						break;	
					case 600:
						removeSubordinateAbsenceAttn.setFontColor("gray");
						attnStatusCount+=1;
						break;	
					//审批通过 和 失效同意  异常考勤天数加1 保持缺勤总数不变	
					}
					String day = DateUtils.getDayOfMonth(removeSubordinateAbsences.getAttendanceDate());
					removeSubordinateAbsenceAttn.setEmployId(removeSubordinateAbsences.getEmployeeId());
					removeSubordinateAbsenceAttn.setAttnDate(removeSubordinateAbsences.getAttendanceDate());
					attnDetail.put(Integer.valueOf(day).toString(), removeSubordinateAbsenceAttn);
				}
				attnDetail.put("attnStatusCount", attnStatusCount);
				attnDetailList.add(attnDetail);
			}
			pm.setRows(attnDetailList);
			pm.setTotal(total);
		}
		result.put("page", pm);
		result.put("success", true);
		return result;
	}

	@Override
	public Map<String, Object> getAttnDetail(Long empId, Date date) throws OaException {
		//返回值
		Map<String, Object> result = new HashMap<String, Object>();
		//参数校验
		if (empId == null || date == null) {
			result.put("message", "员工或日期不能为空！");
			result.put("success", false);
			return result;
		}
		// 判断员工是否离职
		Integer isQuit = employeeService.isQuitThisDay(empId, date);
		if (isQuit > 0) {
			result.put("message", "员工已离职不可进行登记！");
			result.put("success", false);
			return result;
		}
		
		//查询员工与申诉主管信息
		Employee emp = employeeMapper.getById(empId);
		if(emp!=null && emp.getReportToLeader()!=null){
			Employee empLeader = employeeMapper.getById(emp.getReportToLeader());
			emp.setLeaderName(empLeader!=null?empLeader.getCnName():"");
		}
		result.put("emp", emp);
		
		boolean removeAbsenceHoursFlag = true;//是否获取实时消缺勤小时数
		double removeAbsenceHours = 0d;
		boolean isGetCurrentOverHours = true;//是否获取实时的 出勤多余小时数
		double overHoursOfAttendance = 0;//出勤多余小时数
		Double removeAbsenceHoursReduce = 0d;
		Integer buttonType = 1;//控制按钮类型（1-提交，2-撤销，3-关闭）
		RemoveSubordinateAbsence removeSubordinateAbsence =new RemoveSubordinateAbsence();
		removeSubordinateAbsence.setAttendanceDate(date);
		removeSubordinateAbsence.setEmployeeId(empId);
		List<RemoveSubordinateAbsence> removeSubordinateAbsencelist = removeSubordinateAbsenceMapper.getList(removeSubordinateAbsence);
		if(removeSubordinateAbsencelist!=null && removeSubordinateAbsencelist.size()>0){
			for (RemoveSubordinateAbsence removeSubordinateAbsences : removeSubordinateAbsencelist) {
				if(removeSubordinateAbsences.getApproalStatus()==ConfigConstants.PASS_STATUS.longValue() || removeSubordinateAbsences.getApproalStatus()==ConfigConstants.OVERDUEPASS_STATUS.longValue() ){
					result.put("removeSubordinateAbsence", removeSubordinateAbsences);
					buttonType=3;
					isGetCurrentOverHours = false;
					removeAbsenceHoursFlag = false;
					break;
				}
				if(removeSubordinateAbsences.getApproalStatus()==ConfigConstants.DOING_STATUS.longValue()){
					result.put("removeSubordinateAbsence", removeSubordinateAbsences);
					buttonType=2;
					removeAbsenceHoursFlag = false;
					break;
				}
				if(removeSubordinateAbsences.getApproalStatus()==ConfigConstants.OVERDUE_STATUS.longValue()){
					result.put("removeSubordinateAbsence", removeSubordinateAbsences);
					buttonType=2;
					break;
				}
			}
			
		}
		result.put("buttonType", buttonType);
		
		//获取实时的 出勤多余小时数
		if(isGetCurrentOverHours){
			Date startMonth = DateUtils.parse(DateUtils.getYear(date)+"-01-01", DateUtils.FORMAT_SHORT);
			EmpDelayHours empDelayHours = empDelayHoursMapper.getCountByEmpAndMonth(empId,startMonth,DateUtils.getFirstDay(date));
			if(empDelayHours != null&&empDelayHours.getTotalDelayHours()!=null){
				overHoursOfAttendance = empDelayHours.getTotalDelayHours() - empDelayHours.getUsedDelayHours().doubleValue() - empDelayHours.getLockedDelayHours().doubleValue();
			}
			result.put("overHoursOfAttendance", overHoursOfAttendance);
		}
		
		//考勤时间，分段显示
		AttnWorkHours attnTime = new AttnWorkHours(); 
		attnTime.setEmployeeId(empId);
		attnTime.setWorkDate(date);
		List<AttnWorkHours> attnTimeList = attnWorkHoursService.getAttnWorkHoursList(attnTime);
		List<AttnWorkHours> attnTimeList1 = new ArrayList<AttnWorkHours>();
		for(AttnWorkHours data:attnTimeList){
			if(data.getDataType().intValue()!=140){//去除date_type=140的数据
				attnTimeList1.add(data);
			}
		}
		result.put("attnDetailList", attnTimeList1);
		
		//考勤日期（非工作日不会存在考勤异常，直接转换日期）
		result.put("attnDate", DateUtils.format(date, DateUtils.FORMAT_SHORT_CN) + "（工作日）");
		
		//消缺勤小时数
		if(removeAbsenceHoursFlag){
			removeAbsenceHours = getRemoveAbsenceHours(empId,date);
		}
		
		result.put("removeAbsenceHours", removeAbsenceHours);
		
		//前一天下班时间 
		result.put("yesterdayOffTimeStr", "");
		result.put("yesterdayOffTime", "");
		//判断前一天是否是工作日
		EmployeeClass yesterDaycondition = new EmployeeClass();
		yesterDaycondition.setEmployId(empId);
		yesterDaycondition.setClassDate(DateUtils.addDay(date, -1));
		EmployeeClass yesterDayempClass = employeeClassService.getEmployeeClassSetting(yesterDaycondition);
		if(yesterDayempClass!=null){
			//工作日直接取考勤数据
			AttnWorkHours yesterDayWorkHours = new AttnWorkHours(); 
			yesterDayWorkHours.setEmployeeId(empId);
			yesterDayWorkHours.setWorkDate(DateUtils.addDay(date, -1));
			List<AttnWorkHours> yesterDayWorkHoursList = attnWorkHoursService.getListByCondition(yesterDayWorkHours);
			Map<Integer,Date> yesterdsayMap = new HashMap<Integer,Date>();
			for(AttnWorkHours data:yesterDayWorkHoursList){
				yesterdsayMap.put(data.getDataType(), data.getEndTime());
			}
			Date yesterdayOffTime = null;
			//正常打卡数据
			if(yesterdsayMap!=null&&yesterdsayMap.containsKey(0)){
				yesterdayOffTime = yesterdsayMap.get(0);
			}
			//异常考勤数据
            if(yesterdsayMap!=null&&yesterdsayMap.containsKey(40)){
				yesterdayOffTime = yesterdsayMap.get(40);
			}
            //相对修改
            if(yesterdsayMap!=null&&yesterdsayMap.containsKey(2)){
				yesterdayOffTime = yesterdsayMap.get(2);
			}
			//绝对修改
            if(yesterdsayMap!=null&&yesterdsayMap.containsKey(3)){
				yesterdayOffTime = yesterdsayMap.get(3);
			}
            //定位打卡
            if(yesterdsayMap!=null&&yesterdsayMap.containsKey(5)){
				yesterdayOffTime = yesterdsayMap.get(5);
			}
            //当天与昨天都是工作日
			if(yesterdayOffTime!=null){
				result.put("yesterdayOffTimeStr", DateUtils.format(yesterdayOffTime, DateUtils.FORMAT_SHORT_CN)+"（工作日）"+DateUtils.format(yesterdayOffTime, DateUtils.FORMAT_HH_MM));
				result.put("yesterdayOffTime",DateUtils.format(yesterdayOffTime, DateUtils.FORMAT_LONG));
			}else{
				result.put("yesterdayOffTimeStr", DateUtils.format(yesterdayOffTime, DateUtils.FORMAT_SHORT_CN)+"（工作日） 空卡");
				result.put("yesterdayOffTime",DateUtils.format(yesterdayOffTime, DateUtils.FORMAT_LONG));
			}
		}else{
			//非工作日取（6：00 -6；00 的打卡数据）
			AttnSignRecord signRecordCondition=new AttnSignRecord();
			Date yestday = DateUtils.addDay(date, -1);
			yestday.setHours(06);
			Date today = date;
			today.setHours(06);
			signRecordCondition.setEmployeeId(empId);
			signRecordCondition.setStartTime(yestday);
			signRecordCondition.setEndTime(today);
			List<AttnSignRecord> attnSignRecordList = attnSignRecordService.getListBefore9(signRecordCondition);
			if(attnSignRecordList!=null && attnSignRecordList.size()>0){
				Date yesterdayOffTime = attnSignRecordList.get(0).getSignTime();
				//下班时间与所选日期在一天
				if(DateUtils.format(date, DateUtils.FORMAT_SHORT).equals(DateUtils.format(yesterdayOffTime, DateUtils.FORMAT_SHORT))){
					//肯定是工作日
					result.put("yesterdayOffTimeStr", DateUtils.format(yesterdayOffTime, DateUtils.FORMAT_SHORT_CN)+"（工作日）"+DateUtils.format(yesterdayOffTime, DateUtils.FORMAT_HH_MM));
					result.put("yesterdayOffTime",DateUtils.format(yesterdayOffTime, DateUtils.FORMAT_LONG));
				}else{
					//非工作日
					result.put("yesterdayOffTimeStr", DateUtils.format(yesterdayOffTime, DateUtils.FORMAT_SHORT_CN)+"（非工作日）"+DateUtils.format(yesterdayOffTime, DateUtils.FORMAT_HH_MM));
					result.put("yesterdayOffTime",DateUtils.format(yesterdayOffTime, DateUtils.FORMAT_LONG));
				}
			}else{
				//非工作日
				result.put("yesterdayOffTimeStr", DateUtils.format(yestday, DateUtils.FORMAT_SHORT_CN)+"（非工作日） 空卡");
			}
		}
		result.put("success", true);
		return result;
	}
	
	/**
	 * H5协作审批我的待办
	 */
	@Override
	public void setValueToVO(TaskVO taskVO, String processInstanceId) {
		//消下属缺勤
		RemoveSubordinateAbsence removeSubordinateAbsence = removeSubordinateAbsenceMapper
				.getByProcessId(processInstanceId);
		if(removeSubordinateAbsence!=null){
			taskVO.setProcessName("消下属缺勤单");
			String redirectUrl = "/removeSubordinateAbsence/approval.htm?flag=no&removeSubordinateAbsenceId="+removeSubordinateAbsence.getId();
			if(!(taskVO.getProcessStatu()==null)) {
				redirectUrl = "/removeSubordinateAbsence/approval.htm?flag=can&removeSubordinateAbsenceId="+removeSubordinateAbsence.getId();
			}
			removeSubordinateAbsence.setAttendanceHour(removeSubordinateAbsence.getAttendanceHour() == null ? "空卡" : removeSubordinateAbsence.getAttendanceHour());
			taskVO.setCreatorDepart(removeSubordinateAbsence.getDepartName());
			taskVO.setProcessStatu(removeSubordinateAbsence.getApproalStatus().intValue());
			taskVO.setOperation(ConfigConstants.STATUS.getOperationByStatu(removeSubordinateAbsence.getApproalStatus().intValue()));
			taskVO.setCreator(removeSubordinateAbsence.getEmpName());
			taskVO.setCreateTime(removeSubordinateAbsence.getCreateTime());
			taskVO.setReProcdefCode(ConfigConstants.REMOVESUBABSENCE_CODE);
			taskVO.setProcessId(removeSubordinateAbsence.getProcessinstanceId());
			taskVO.setRedirectUrl(redirectUrl);
		}
		
	}


	@Override
	public RemoveSubordinateAbsence getById(Long removeSubordinateAbsenceId) {
		RemoveSubordinateAbsence removeSubordinateAbsence = removeSubordinateAbsenceMapper.getById(removeSubordinateAbsenceId);
		if(removeSubordinateAbsence != null){
			Employee emp = employeeMapper.getById(removeSubordinateAbsence.getEmployeeId());
			removeSubordinateAbsence.setEmpName(emp.getCnName());
			Employee submitter = employeeMapper.getById(removeSubordinateAbsence.getSubmitterId());
			removeSubordinateAbsence.setSubmitterName(submitter.getCnName());
		}
		
		return removeSubordinateAbsence;
	}

	@Override
	public PageModel<RemoveSubordinateAbsence> getReportPageList(
			RemoveSubordinateAbsence attendance) {
		// TODO Auto-generated method stub
		int page = attendance.getPage() == null ? 0 : attendance.getPage();
		int rows = attendance.getRows() == null ? 0 : attendance.getRows();
		PageModel<RemoveSubordinateAbsence> pm = new PageModel<RemoveSubordinateAbsence>();
		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
		if(deptDataByUserList==null || deptDataByUserList.size()==0){
			pm.setPageNo(0);
			pm.setPageSize(0);
			pm.setTotal(0);
			attendance.setOffset(pm.getOffset());
			attendance.setLimit(pm.getLimit());
			pm.setRows(new ArrayList<RemoveSubordinateAbsence>());
			return pm;
		}else{
			//查询加数据权限  审批则不加数据查询
			if(attendance.getApproalStatus()==null &&attendance.getApproalStatusList()==null){
				attendance.setCurrentUserDepart(deptDataByUserList);
				List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(currentUser.getEmployeeId());//下属员工
				if(subEmployeeIdList!=null&&subEmployeeIdList.size()>0){
					attendance.setSubEmployeeIdList(subEmployeeIdList);
				}
			}
			pm.setPageNo(page);
			pm.setPageSize(rows);
			Integer total=null;
			if(attendance.getApproalStatusList()!=null &&attendance.getApproalStatusList().size()==4){//已办数据
				total = removeSubordinateAbsenceMapper.myTaskListCount(attendance);
			}else{
				total = removeSubordinateAbsenceMapper.getCount(attendance); 
			}
			pm.setTotal(total);
			
			attendance.setOffset(pm.getOffset());
			attendance.setLimit(pm.getLimit());
			List<RemoveSubordinateAbsence> roles=null;
			if(attendance.getApproalStatusList()!=null &&attendance.getApproalStatusList().size()==4){//已办数据
				roles = removeSubordinateAbsenceMapper.myTaskList(attendance);
			}else{
				roles = removeSubordinateAbsenceMapper.getPageList(attendance);
			}
			
			
			for(RemoveSubordinateAbsence og:roles){
				if(og.getApproalStatus()!=ConfigConstants.BACK_STATUS.intValue()){
					try{
						ViewTaskInfoTbl taskInfo = null;
						taskInfo = viewTaskInfoService.getFirstAuditUser(og.getProcessinstanceId(),ConfigConstants.REMOVESUBABSENCE_KEY,true);
						og.setAuditUser("");
						if(null != taskInfo){
							og.setAuditUser(taskInfo.getAssigneeName());
						}
					}catch(Exception e){
						og.setAuditUser(" ");
					}
				}else{
					og.setAuditUser("");
				}	
			}
			pm.setRows(roles);
			return pm;
		}	
	}

	@Override
	public RemoveSubordinateAbsence queryByProcessInstanceId(String processId) {
		// TODO Auto-generated method stub
		
		return removeSubordinateAbsenceMapper.queryByProcessId(processId);
	}
	@Override
	public HSSFWorkbook exportExcel(RemoveSubordinateAbsence subAttn) {
		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
		if(deptDataByUserList==null || deptDataByUserList.size()==0){
			subAttn.setPage(null);
			subAttn.setOffset(null);
			return null;
		}else{
			subAttn.setPage(null);
			subAttn.setOffset(null);
			List<Map<String,Object>> sMapList = new ArrayList<Map<String,Object>>();
			List<RemoveSubordinateAbsence> list = removeSubordinateAbsenceMapper.getPageList(subAttn);
			if(list!=null&&list.size()>0) {
				for(RemoveSubordinateAbsence og:list){
					try{
						ViewTaskInfoTbl taskInfo = null;
						taskInfo = viewTaskInfoService.getFirstAuditUser(og.getProcessinstanceId(),ConfigConstants.REMOVESUBABSENCE_KEY,true);
						og.setAuditUser("");
						if(null != taskInfo){
							og.setAuditUser(taskInfo.getAssigneeName());
						}
					}catch(Exception e){
						og.setAuditUser(" ");
					}
				}
				for (RemoveSubordinateAbsence attn : list) {
					//封装数据
					Map<String,Object> sdoMap = new HashMap<String, Object>();
					sdoMap.put("empCode",attn.getEmpCode());
					sdoMap.put("empName",attn.getEmpName());
					sdoMap.put("departName",attn.getDepartName());
					sdoMap.put("leaderName",attn.getLeaderName());
					sdoMap.put("overHoursOfAttendance",attn.getOverHoursOfAttendance());
					
					
					
					//上班考勤结果
					sdoMap.put("yesterdayOffTime",DateUtils.format(attn.getYesterdayOffTime(), DateUtils.FORMAT_LONG));
					//下班考勤结果
					sdoMap.put("attendanceDate",DateUtils.format(attn.getAttendanceDate(), DateUtils.FORMAT_SHORT));
					sdoMap.put("attendanceHour",attn.getAttendanceHour());
					sdoMap.put("removeAbsenceHours",attn.getRemoveAbsenceHours());
					sdoMap.put("removeAbsenceReason",attn.getRemoveAbsenceReason());
					
					
					try{
						if(attn.getApproalStatus()!=ConfigConstants.BACK_STATUS.intValue()){
							ViewTaskInfoTbl taskInfo = null;
							taskInfo = viewTaskInfoService.getFirstAuditUser(attn.getProcessinstanceId(),ConfigConstants.REMOVESUBABSENCE_KEY,true);
							attn.setAuditUser("");
							if(null != taskInfo){
								attn.setAuditUser(taskInfo.getAssigneeName());
							}
						}else{
							attn.setAuditUser("");
						}	
					}catch(Exception e){
						attn.setAuditUser(" ");
					}
					
					
					sdoMap.put("auditUser",attn.getAuditUser());
					String approvalStatus = "";
					if(attn.getApproalStatus().intValue()==100){
						approvalStatus = "待审批";
					}else if(attn.getApproalStatus().intValue()==200){
						approvalStatus = "已审批";
					}else if(attn.getApproalStatus().intValue()==300){
						approvalStatus = "已拒绝";
					}else if(attn.getApproalStatus().intValue()==400){
						approvalStatus = "已撤销";
					}else if(attn.getApproalStatus().intValue()==500){
						approvalStatus = "失效";
					}else if(attn.getApproalStatus().intValue()==600){
						approvalStatus = "失效同意";
					}else if(attn.getApproalStatus().intValue()==700){
						approvalStatus = "失效拒绝";
					}
					sdoMap.put("approvalStatus",approvalStatus);
					sMapList.add(sdoMap);
				}
			}
			String[] keys={"empCode", "empName", "departName", "leaderName","overHoursOfAttendance","yesterdayOffTime", "attendanceDate", "attendanceHour", "removeAbsenceHours", "removeAbsenceReason","auditUser","approvalStatus"};
			String[] titles={"员工编号", "消缺勤员工姓名", "部门","申诉主管","出勤多余小时数", "前一天下班时间", "考勤日期", "考勤时间", "消缺勤小时数", "消缺勤理由", "人事批核人", "状态"}; 
			return ExcelUtil.exportExcel(sMapList, keys, titles, "消下属缺勤.xls");
		}	
	}
}
