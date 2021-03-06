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
	
	private final String REST_TIME_1=" 12:00:00";//??????????????????
	private final String REST_TIME_2  =" 12:30:00";//??????????????????
	private final String REST_TIME_3  =" 13:00:00";//??????????????????
	private final String REST_TIME_4  =" 13:30:00";//??????????????????
	private final String REST_TIME_5  =" 14:00:00";//??????????????????
	
	private final String COMMIT_URL = "removeSubordinateAbsence/commitApplicationForm.htm";
	

	
	// ?????????????????????
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void commitApplicationForm(HttpServletRequest request,RemoveSubordinateAbsence removeSubordinateAbsence) throws OaException {
		
		User user = userService.getCurrentUser();
		if (removeSubordinateAbsence == null) {
			throw new OaException("????????????");
		}
		
		logger.info("???????????????commitApplicationForm??????:EmployeeId="+removeSubordinateAbsence.getEmployeeId()
				+";SubmitterId="+removeSubordinateAbsence.getSubmitterId()
				+";AttendanceDate="+removeSubordinateAbsence.getAttendanceDate()
				+";AttendanceHour="+removeSubordinateAbsence.getAttendanceHour()
				+";RemoveAbsenceHours="+removeSubordinateAbsence.getRemoveAbsenceHours()
				+";Reason="+removeSubordinateAbsence.getRemoveAbsenceReason()
				+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));
		
		if (removeSubordinateAbsence.getEmployeeId() == null) {
			throw new OaException("?????????????????????????????????");
		}
		
		Depart depart = departMapper.getByEmpId(removeSubordinateAbsence.getEmployeeId());
		Long departId = depart!=null?depart.getId():null;
		RabcResource hasPrivilege = rabcResourceMapper.getOperationByUserIdAndUrlAndDeaprtId(user.getId(),COMMIT_URL,departId);
		if(hasPrivilege==null) {
			throw new OaException("???????????????????????????");
		}
		
		if (removeSubordinateAbsence.getAttendanceDate() == null) {
			throw new OaException("???????????????????????????");
		}
		if (removeSubordinateAbsence.getAttendanceHour() == null) {
			throw new OaException("???????????????????????????");
		}
		if (removeSubordinateAbsence.getRemoveAbsenceHours() == null || removeSubordinateAbsence.getRemoveAbsenceHours().doubleValue() <= 0) {
			throw new OaException("??????????????????????????????0???");
		}
		if (StringUtils.isBlank(removeSubordinateAbsence.getRemoveAbsenceReason())) {
			throw new OaException("???????????????????????????");
		}
		removeSubordinateAbsence.setSubmitterId(user.getEmployee().getId());
		
		Double removeAbsenceHours = getRemoveAbsenceHours(removeSubordinateAbsence.getEmployeeId(),removeSubordinateAbsence.getAttendanceDate());
		
		if(removeAbsenceHours>removeSubordinateAbsence.getRemoveAbsenceHours()) {
			throw new OaException("???????????????????????????????????????????????????");
		}
		
		// ????????????????????????????????????
		Date now = new Date();
		Date attendanceMonth = DateUtils.getFirstDay(removeSubordinateAbsence.getAttendanceDate());
		Date startMonth = DateUtils.parse(DateUtils.getYear(removeSubordinateAbsence.getAttendanceDate())+"-01-01", DateUtils.FORMAT_SHORT);
		Date nextMonth = DateUtils.addMonth(attendanceMonth, 1);
		//????????????????????????
		Config configCondition = new Config();
		configCondition.setCode("timeLimit5");
		List<Config> limit = configService.getListByCondition(configCondition);
		int num = Integer.valueOf(limit.get(0).getDisplayCode());
		
		//????????????????????????????????????5???
		if("107".equals(user.getDepart().getCode())) {
			//????????????????????????
			configCondition.setCode("timeLimit7");
			limit = configService.getListByCondition(configCondition);
			num = Integer.valueOf(limit.get(0).getDisplayCode());
		}

		Date lastSubDate = annualVacationService.getWorkingDayOfMonth(nextMonth, num+1);
		if (DateUtils.getIntervalDays(now, lastSubDate) < 0) {
			throw new OaException("??????3???????????????????????????????????????");
		}
		// ?????????????????????????????????
		CompanyConfig condition = new CompanyConfig();
		condition.setCode("typeOfWork");
		condition.setDisplayCode("standard");
		CompanyConfig companyConfig = companyConfigService.getByCondition(condition);
		Employee employee = employeeMapper.getById(removeSubordinateAbsence.getEmployeeId());
		if (!companyConfig.getId().equals(employee.getWorkType())) {
			throw new OaException("?????????????????????????????????????????????????????????");
		}
		// ?????????????????????????????????
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
					throw new OaException("????????????????????????????????????");
				}
			}
		}
		// ?????????????????????????????????????????????????????????
		EmpDelayHours empDelayHours = empDelayHoursMapper.getCountByEmpAndMonth(employee.getId(), startMonth, attendanceMonth);
		double overHoursOfAttendance = 0D;
		if (empDelayHours != null) {
			// ?????????????????????
			overHoursOfAttendance = empDelayHours.getTotalDelayHours().doubleValue()
					- empDelayHours.getUsedDelayHours().doubleValue()
					- empDelayHours.getLockedDelayHours().doubleValue();
			if (overHoursOfAttendance < removeSubordinateAbsence.getRemoveAbsenceHours().doubleValue()) {
				throw new OaException("??????????????????????????????????????????????????????");
			}
		} else {
			throw new OaException("??????????????????????????????????????????????????????");
		}
		
		//??????
		lockDelayHours(employee.getId(),startMonth,attendanceMonth,removeSubordinateAbsence.getRemoveAbsenceHours(),user);
		
		removeSubordinateAbsence.setOverHoursOfAttendance(overHoursOfAttendance);
		removeSubordinateAbsence.setApproalStatus(ConfigConstants.DOING_STATUS.longValue());
		removeSubordinateAbsence.setCreateTime(now);
		removeSubordinateAbsence.setCreateUser(user.getEmployee().getId().toString());
		removeSubordinateAbsence.setDelFlag(0);
		
		// ????????????
		Map<String,Object> v = Maps.newHashMap();
		v.put("proposerId", removeSubordinateAbsence.getEmployeeId());
		String processInstanceId = activitiServiceImpl.startByKey(ConfigConstants.REMOVESUBABSENCE_KEY,v);
		removeSubordinateAbsence.setProcessinstanceId(processInstanceId);
		//???????????????
		removeSubordinateAbsenceMapper.insertRemoveSubordinateAbsence(removeSubordinateAbsence);
		
		// -----------------start-----------------------????????????????????????
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
			 * 9-18?????????
			 * ???????????????????????????????????????12:00-13:00??????????????????12:00-13:00???????????????
			 */
			if(toadyEmpClass!=null
					&&"09:00:00".equals(DateUtils.format(toadyEmpClass.getStartTime(), DateUtils.FORMAT_HH_MM_SS))
					&&"18:00:00".equals(DateUtils.format(toadyEmpClass.getEndTime(), DateUtils.FORMAT_HH_MM_SS))){
				Date restTime_1 = DateUtils.parse(DateUtils.format(attnStatistics.getAttnDate(), DateUtils.FORMAT_SHORT)+REST_TIME_1, DateUtils.FORMAT_LONG);
				Date restTime_2 = DateUtils.parse(DateUtils.format(attnStatistics.getAttnDate(), DateUtils.FORMAT_SHORT)+REST_TIME_2, DateUtils.FORMAT_LONG);
				Date restTime_3 = DateUtils.parse(DateUtils.format(attnStatistics.getAttnDate(), DateUtils.FORMAT_SHORT)+REST_TIME_3, DateUtils.FORMAT_LONG);
				Date restTime_4 = DateUtils.parse(DateUtils.format(attnStatistics.getAttnDate(), DateUtils.FORMAT_SHORT)+REST_TIME_4, DateUtils.FORMAT_LONG);
				Date restTime_5 = DateUtils.parse(DateUtils.format(attnStatistics.getAttnDate(), DateUtils.FORMAT_SHORT)+REST_TIME_5, DateUtils.FORMAT_LONG);
				
				//??????
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
				//??????
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
				
				//??????????????????????????????
				if(removeAbsenceHours<8){
					removeAbsenceHours = removeAbsenceHours - removeAbsenceHoursReduce;
				}
				
			}
		}
		
		return removeAbsenceHours;
	}
	
	//????????????????????????????????????????????????
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
	
	//??????????????????????????????????????????????????????
	public void reduceDelayHours(Long employeeId,Date startMonth,Date endMonth, Double removeAbsenceHours,User user){
		
		List<EmpDelayHours> lockedList = empDelayHoursMapper.getLockedList(employeeId, startMonth, endMonth,"asc");
		
		for(EmpDelayHours data:lockedList){
			
			if(removeAbsenceHours<=0){
				break;
			}
			
			if(data.getLockedDelayHours()>=removeAbsenceHours){
				//????????????
				EmpDelayHours empDelayHours = new EmpDelayHours();
				empDelayHours.setId(data.getId());
				empDelayHours.setLockedDelayHours(data.getLockedDelayHours() - removeAbsenceHours);
				//????????????
				empDelayHours.setUsedDelayHours(data.getUsedDelayHours() + removeAbsenceHours);
				empDelayHours.setUpdateTime(new Date());
				empDelayHours.setUpdateUser(user.getEmployee().getId().toString());
				empDelayHoursMapper.updateEmpDelayHours(empDelayHours);
				removeAbsenceHours = 0d;
			}else{
				//????????????
				EmpDelayHours empDelayHours = new EmpDelayHours();
				empDelayHours.setId(data.getId());
				empDelayHours.setLockedDelayHours(0d);
				//????????????
				empDelayHours.setUsedDelayHours(data.getUsedDelayHours() + data.getLockedDelayHours());
				empDelayHours.setUpdateTime(new Date());
				empDelayHours.setUpdateUser(user.getEmployee().getId().toString());
				empDelayHoursMapper.updateEmpDelayHours(empDelayHours);
				removeAbsenceHours = removeAbsenceHours - data.getLockedDelayHours();
			}
			
		}
		
	}
	
	//???????????????????????????????????????????????????????????????
	public void restoreDelayHours(Long employeeId,Date startMonth,Date endMonth, Double removeAbsenceHours,User user){
		
		List<EmpDelayHours> lockedList = empDelayHoursMapper.getLockedList(employeeId, startMonth, endMonth,"desc");
		
		for(EmpDelayHours data:lockedList){
			
			if(removeAbsenceHours<=0){
				break;
			}
			
			if(data.getLockedDelayHours()>=removeAbsenceHours){
				//????????????
				EmpDelayHours empDelayHours = new EmpDelayHours();
				empDelayHours.setId(data.getId());
				empDelayHours.setLockedDelayHours(data.getLockedDelayHours() - removeAbsenceHours);
				empDelayHours.setUpdateTime(new Date());
				empDelayHours.setUpdateUser(user.getEmployee().getId().toString());
				empDelayHoursMapper.updateEmpDelayHours(empDelayHours);
				removeAbsenceHours = 0d;
			}else{
				//????????????
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
		logger.info("???????????????completeTask??????:processId="+processId+";comment="+comment+";commentType="+commentType
				+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));
		
		Date now = new Date();
		Task task = activitiServiceImpl.queryTaskByProcessInstanceId(processId);
		if (task == null) {
			logger.error("??????Id???" + processId + "?????????????????????");
			throw new OaException("??????????????????");
		}
		boolean type = StringUtils.startsWithIgnoreCase(task.getTaskDefinitionKey(), "personnel");
		RemoveSubordinateAbsence removeSubordinateAbsence = removeSubordinateAbsenceMapper
				.getByProcessId(processId);
		if (removeSubordinateAbsence == null) {
			logger.error("???????????????" + processId + "???????????????????????????");
			throw new OaException("??????????????????");
		}
		//????????????????????????????????????????????????
		Date delayDate = DateUtils.getFirstDay(removeSubordinateAbsence.getAttendanceDate());
		Date startMonth = DateUtils.parse(DateUtils.getYear(removeSubordinateAbsence.getAttendanceDate())+"-01-01", DateUtils.FORMAT_SHORT);
		EmpDelayHours empDelayHours = empDelayHoursMapper.getCountByEmpAndMonth(removeSubordinateAbsence.getEmployeeId(), startMonth, delayDate);
		if(empDelayHours == null ||empDelayHours.getLockedDelayHours().doubleValue() == 0
				||empDelayHours.getLockedDelayHours().doubleValue() < removeSubordinateAbsence.getRemoveAbsenceHours()){
			throw new OaException("??????????????????????????????");
		}
		//??????????????????????????????
		Date attendanceMonth = DateUtils.getFirstDay(removeSubordinateAbsence.getAttendanceDate());
		Date nextMonth = DateUtils.addMonth(attendanceMonth, 1);
		//????????????????????????
		Config configCondition = new Config();
		configCondition.setCode("timeLimit7");
		List<Config> limit = configService.getListByCondition(configCondition);
		int num = Integer.valueOf(limit.get(0).getDisplayCode());

		Date lastSubDate = annualVacationService.getWorkingDayOfMonth(nextMonth, num+1);
		//?????????????????????????????????
		if ((StringUtils.equalsIgnoreCase(commentType, ConfigConstants.PASS)|| StringUtils.equalsIgnoreCase(commentType, ConfigConstants.REFUSE))
				&&DateUtils.getIntervalDays(now, lastSubDate) < 0) {
			throw new OaException("??????5???????????????????????????????????????");
		}
		Integer approvalStatus;
		if (StringUtils.equalsIgnoreCase(commentType, ConfigConstants.PASS)) {
			/**??????**/
			approvalStatus = type ? ConfigConstants.PASS_STATUS : ConfigConstants.DOING_STATUS;
			// ????????????????????????????????????assignee,??????????????????????????????????????????
			if (StringUtils.isBlank(task.getAssignee())) {
				activitiServiceImpl.claimTask(task.getId(),
						String.valueOf(userService.getCurrentUser().getEmployeeId()));
			}
			//????????????????????????????????????????????????????????????????????????????????????????????????
			if(ConfigConstants.PASS_STATUS.equals(approvalStatus)){
				
				reduceDelayHours(removeSubordinateAbsence.getEmployeeId(),startMonth,delayDate, 
						removeSubordinateAbsence.getRemoveAbsenceHours(),user);
				
				//????????????
				Employee employee = employeeService.getById(removeSubordinateAbsence.getEmployeeId());
				AttnWorkHours attnWorkHours = new AttnWorkHours();
				attnWorkHours.setCompanyId(employee.getCompanyId());
				attnWorkHours.setEmployeeId(removeSubordinateAbsence.getEmployeeId());
				attnWorkHours.setWorkDate(removeSubordinateAbsence.getAttendanceDate());
				attnWorkHours.setDataType(Integer.parseInt(ConfigConstants.REMOVESUBABSENCE_CODE));
				
				Double removeAbsenceHoursReduce = 0d;
				/**
				 * 9-18?????????
				 * ???????????????????????????????????????12:00-13:00??????????????????12:00-13:00???????????????
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
						
						//??????
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
						//??????
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
			/**??????**/
			approvalStatus = ConfigConstants.REFUSE_STATUS;
			// ????????????????????????????????????assignee,??????????????????????????????????????????
			if (StringUtils.isBlank(task.getAssignee())) {
				activitiServiceImpl.claimTask(task.getId(),
						String.valueOf(userService.getCurrentUser().getEmployeeId()));
			}
			
			restoreDelayHours(removeSubordinateAbsence.getEmployeeId(),startMonth,delayDate, 
					removeSubordinateAbsence.getRemoveAbsenceHours(),user);
			
		}else if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.OVERDUEPASS)){
			/**????????????**/
			approvalStatus = type ? ConfigConstants.OVERDUEPASS_STATUS : ConfigConstants.DOING_STATUS;
			// ????????????????????????????????????assignee,??????????????????????????????????????????
			if (StringUtils.isBlank(task.getAssignee())) {
				activitiServiceImpl.claimTask(task.getId(),
						String.valueOf(userService.getCurrentUser().getEmployeeId()));
			}
			//????????????????????????????????????????????????????????????????????????????????????????????????
			if(ConfigConstants.OVERDUEPASS_STATUS.equals(approvalStatus)){
				
				reduceDelayHours(removeSubordinateAbsence.getEmployeeId(),startMonth,delayDate, 
						removeSubordinateAbsence.getRemoveAbsenceHours(),user);
				
				//????????????
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
			/**????????????**/
			approvalStatus = ConfigConstants.OVERDUEREFUSE_STATUS;
			// ????????????????????????????????????assignee,??????????????????????????????????????????
			if (StringUtils.isBlank(task.getAssignee())) {
				activitiServiceImpl.claimTask(task.getId(),
						String.valueOf(userService.getCurrentUser().getEmployeeId()));
			}
			
			restoreDelayHours(removeSubordinateAbsence.getEmployeeId(),startMonth,delayDate, 
					removeSubordinateAbsence.getRemoveAbsenceHours(),user);
			
		}else {
			/**??????**/
			approvalStatus = ConfigConstants.BACK_STATUS;
			
			restoreDelayHours(removeSubordinateAbsence.getEmployeeId(),startMonth,delayDate, 
					removeSubordinateAbsence.getRemoveAbsenceHours(),user);
		}
		//???????????????????????????
		RemoveSubordinateAbsence updateStatus = new RemoveSubordinateAbsence();
		updateStatus.setId(removeSubordinateAbsence.getId());
		updateStatus.setApproalStatus(approvalStatus.longValue());
		updateStatus.setApproalReason(comment);
		updateStatus.setUpdateTime(new Date());
		updateStatus.setUpdateUser(user.getEmployee().getId().toString());
		removeSubordinateAbsenceMapper.updateById(updateStatus);
		// -----------------start-----------------------????????????????????????
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
		activitiServiceImpl.completeTask(task.getId(),"????????????",null,commentType);
		// -----------------end-------------------------
	}
	
	/**
	 * ?????????????????????
	 * @throws OaException 
	 */
	@Override
	public Map<String, Object> getRemoveSubordinateAbsencePage(Long departId,String date, String empCode, String empCnName,
			Integer page, Integer rows) throws OaException {
		User user = userService.getCurrentUser();
		Map<String, Object> result = new HashMap<String, Object>();
		// ????????????????????????
		Date month = new Date();
		if (date != null) {
			String dateStr = date.substring(0, 7);
			month = DateUtils.parse(dateStr + "-01", DateUtils.FORMAT_SHORT);
		}
		Date startDay = DateUtils.getFirstDay(month);// ???????????????
		Date lastDay = DateUtils.getLastDay(month);// ??????????????????
		boolean isCurrentMonth = false;//???????????????
		//??????????????????????????????????????????
		if(DateUtils.format(startDay,"yyyy-MM").equals(DateUtils.format(new Date(),"yyyy-MM"))){
			isCurrentMonth =true;
		}
		// ????????????????????????
		List<String> weekDays = new ArrayList<String>();
		// ???????????????
		List<String> days = new ArrayList<String>();
		// ???????????????
		List<String> dates = new ArrayList<String>();
		days.add("????????????");
		days.add("??????");
		days.add("???????????????????????????/????????????");
		days.add("?????????????????????");
		days.add("??????????????????");
		weekDays.add("");
		weekDays.add("");
		weekDays.add("");
		weekDays.add("");
		weekDays.add("");
		Date fristDay = DateUtils.getFirstDay(month);// ???????????????
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
		
		
		//???????????????????????????????????????????????????????????????????????????????????????????????????
		List<RabcRole> roleList = rabcRoleMapper.getListByUserId(user.getId(),departId);
		//????????????????????????????????????????????????
		Map<Long,Boolean> hasGetMap = new HashMap<Long,Boolean>();
		//?????????????????????id??????
		List<Long> employeeIdList = new ArrayList<Long>();
		// ??????????????????????????????????????????id
		CompanyConfig condition = new CompanyConfig();
		condition.setCode("typeOfWork");
		condition.setDisplayCode("standard");
		CompanyConfig companyConfig = companyConfigService.getByCondition(condition);
		
		//????????????????????????
		Config configCondition = new Config();
		configCondition.setCode("get_all_employee_role");
		List<Config> isGetAllEmployeList = configService.getListByCondition(configCondition);
		
		for(RabcRole role:roleList){
			boolean isGetAll = false;
			//??????????????????????????????????????????????????????
			if(hasGetMap!=null&&hasGetMap.containsKey(role.getDepartId())){
				continue;
			}
			for(Config data:isGetAllEmployeList){
				if(data.getDisplayCode().equals(role.getName())){
					isGetAll = true;
					continue;
				}
			}
			//?????????????????????????????????????????????
			if(isGetAll){
		    	List<Long> idList = employeeService.getSubEmployeeList(user.getEmployeeId(), role.getDepartId(),isGetAll);
		    	employeeIdList.addAll(idList);
		    	hasGetMap.put(role.getDepartId(), true);
		    	continue;
		    }
		    //??????????????????????????????????????????????????????????????????
		    List<Long> idList = employeeService.getSubEmployeeList(user.getEmployeeId(), role.getDepartId(),isGetAll);
		    employeeIdList.addAll(idList);
			
		}
		
		//????????????id??????
		if(employeeIdList==null || employeeIdList.size()<=0){
			pm.setRows(attnDetailList);
			pm.setTotal(0);
			result.put("page", pm);
			result.put("success", true);
			return result;
		}

		RequestParamQueryEmpCondition requestParamQueryEmpByCondition = new RequestParamQueryEmpCondition();
		//????????????
		requestParamQueryEmpByCondition.setEmployeeIdList(employeeIdList);
		// ??????????????????
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
		//??????????????????????????????
		requestParamQueryEmpByCondition.setEmpTypeIdList(configService.getNeedEmpTypeIdList());
		requestParamQueryEmpByCondition.setChooseMonth(startDay);
		
		//????????????
		int total = employeeService.getStandardEmpCountByCount(requestParamQueryEmpByCondition);
		
		requestParamQueryEmpByCondition.setOffset(offset);
		requestParamQueryEmpByCondition.setLimit(limit);
		
		//??????????????????
		List<Employee> subordinateEmp = employeeService
				.getAllEmpByWorkTypeAndLeaderAndDepart(requestParamQueryEmpByCondition);
		
		//??????????????????
		Map<Long,String> whetherSchedulingMap = new HashMap<Long,String>();
		Config whetherScheduling = new Config();
        whetherScheduling.setCode("whetherScheduling");
        List<Config> whetherSchedulingLsit = configService.getListByCondition(whetherScheduling);
        for(Config data:whetherSchedulingLsit){
        	whetherSchedulingMap.put(data.getId(), data.getDisplayCode());
        }
		
		if (subordinateEmp != null && subordinateEmp.size() > 0) {
			
			Map<Long,Double> actAttnTimeMap = new HashMap<Long,Double>();//??????????????????
			Map<Long,Double> delayHoursMap = new HashMap<Long,Double>();//???????????????????????????
			Map<Long,Double> attnSurplusHoursMap = new HashMap<Long,Double>();//?????????????????????
			Map<Long,List<EmpAttn>> abnormalAttnMap = new HashMap<Long,List<EmpAttn>>();//??????????????????
			Map<Long,List<RemoveSubordinateAbsence>> applyMap = new HashMap<Long,List<RemoveSubordinateAbsence>>();//?????????????????????
			Map<Long,Double> mustAttnTimeMap = new HashMap<Long,Double>();//??????????????????
			
			List<Long> employIdList = new ArrayList<Long>();
			for(Employee data:subordinateEmp){
				employIdList.add(data.getId());
				actAttnTimeMap.put(data.getId(), 0d);//????????????0
				delayHoursMap.put(data.getId(), 0d);//????????????0
				attnSurplusHoursMap.put(data.getId(), 0d);//????????????0
				List<EmpAttn> attnList = new ArrayList<EmpAttn>();
				abnormalAttnMap.put(data.getId(), attnList);
				List<RemoveSubordinateAbsence> applyList = new ArrayList<RemoveSubordinateAbsence>();
				applyMap.put(data.getId(), applyList);
				mustAttnTimeMap.put(data.getId(), 0d);
			}
			
			//???????????????????????????????????????
			List<AttnStatistics> actAttnTimeList = attnStatisticsService.getActAttnTimeGroupByemployIds(employIdList, startDay, lastDay);
			for(AttnStatistics data:actAttnTimeList){
				actAttnTimeMap.put(data.getEmployId(), 0d);
				if(data.getActAttnTimeCount()!=null){
					actAttnTimeMap.put(data.getEmployId(), data.getActAttnTimeCount());
				}
			}
			
			//????????????????????????????????????????????????+?????????????????????
			Date startMonth = DateUtils.parse(DateUtils.getYear(startDay)+"-01-01", DateUtils.FORMAT_SHORT);
			List<EmpDelayHours> delayHoursList = empDelayHoursMapper.getByemployIdListAndMonth(employIdList,startMonth,startDay);
			for(EmpDelayHours data:delayHoursList){
				delayHoursMap.put(data.getEmployeeId(),data.getTotalDelayHours());
				attnSurplusHoursMap.put(data.getEmployeeId(),data.getTotalDelayHours()-data.getUsedDelayHours()-data.getLockedDelayHours());
			}
			
			//???????????????????????????
			List<EmpAttn> abnormalAttnList = empAttnMapper.getAbnormalAttnList(employIdList, startDay, lastDay);
			for(EmpAttn data:abnormalAttnList){
				abnormalAttnMap.get(data.getEmployId()).add(data);
			}
			
			//?????????????????????????????????
			List<RemoveSubordinateAbsence> subAbsenceList = removeSubordinateAbsenceMapper.getListByEmployIdsandDate(employIdList, startDay, lastDay);
			for(RemoveSubordinateAbsence data:subAbsenceList){
				applyMap.get(data.getEmployeeId()).add(data);
			}
			
			//????????????????????????
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
				//?????????????????????????????????????????????????????????-???????????????????????????????????????????????????????????????
				double mustAttnTime = 0d;//????????????????????????*8???
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
				
				//??????????????????????????????+???????????????????????????==????????????????????????
				double actAttnTime = actAttnTimeMap.get(employee.getId())+delayHoursMap.get(employee.getId());
				
				//?????????????????????
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
				//??????????????????
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
					case 500://?????? ??????????????????
						removeSubordinateAbsenceAttn.setFontColor("blue");
						break;	
					case 600:
						removeSubordinateAbsenceAttn.setFontColor("gray");
						attnStatusCount+=1;
						break;	
					//???????????? ??? ????????????  ?????????????????????1 ????????????????????????	
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
		//?????????
		Map<String, Object> result = new HashMap<String, Object>();
		//????????????
		if (empId == null || date == null) {
			result.put("message", "??????????????????????????????");
			result.put("success", false);
			return result;
		}
		// ????????????????????????
		Integer isQuit = employeeService.isQuitThisDay(empId, date);
		if (isQuit > 0) {
			result.put("message", "????????????????????????????????????");
			result.put("success", false);
			return result;
		}
		
		//?????????????????????????????????
		Employee emp = employeeMapper.getById(empId);
		if(emp!=null && emp.getReportToLeader()!=null){
			Employee empLeader = employeeMapper.getById(emp.getReportToLeader());
			emp.setLeaderName(empLeader!=null?empLeader.getCnName():"");
		}
		result.put("emp", emp);
		
		boolean removeAbsenceHoursFlag = true;//????????????????????????????????????
		double removeAbsenceHours = 0d;
		boolean isGetCurrentOverHours = true;//????????????????????? ?????????????????????
		double overHoursOfAttendance = 0;//?????????????????????
		Double removeAbsenceHoursReduce = 0d;
		Integer buttonType = 1;//?????????????????????1-?????????2-?????????3-?????????
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
		
		//??????????????? ?????????????????????
		if(isGetCurrentOverHours){
			Date startMonth = DateUtils.parse(DateUtils.getYear(date)+"-01-01", DateUtils.FORMAT_SHORT);
			EmpDelayHours empDelayHours = empDelayHoursMapper.getCountByEmpAndMonth(empId,startMonth,DateUtils.getFirstDay(date));
			if(empDelayHours != null&&empDelayHours.getTotalDelayHours()!=null){
				overHoursOfAttendance = empDelayHours.getTotalDelayHours() - empDelayHours.getUsedDelayHours().doubleValue() - empDelayHours.getLockedDelayHours().doubleValue();
			}
			result.put("overHoursOfAttendance", overHoursOfAttendance);
		}
		
		//???????????????????????????
		AttnWorkHours attnTime = new AttnWorkHours(); 
		attnTime.setEmployeeId(empId);
		attnTime.setWorkDate(date);
		List<AttnWorkHours> attnTimeList = attnWorkHoursService.getAttnWorkHoursList(attnTime);
		List<AttnWorkHours> attnTimeList1 = new ArrayList<AttnWorkHours>();
		for(AttnWorkHours data:attnTimeList){
			if(data.getDataType().intValue()!=140){//??????date_type=140?????????
				attnTimeList1.add(data);
			}
		}
		result.put("attnDetailList", attnTimeList1);
		
		//???????????????????????????????????????????????????????????????????????????
		result.put("attnDate", DateUtils.format(date, DateUtils.FORMAT_SHORT_CN) + "???????????????");
		
		//??????????????????
		if(removeAbsenceHoursFlag){
			removeAbsenceHours = getRemoveAbsenceHours(empId,date);
		}
		
		result.put("removeAbsenceHours", removeAbsenceHours);
		
		//????????????????????? 
		result.put("yesterdayOffTimeStr", "");
		result.put("yesterdayOffTime", "");
		//?????????????????????????????????
		EmployeeClass yesterDaycondition = new EmployeeClass();
		yesterDaycondition.setEmployId(empId);
		yesterDaycondition.setClassDate(DateUtils.addDay(date, -1));
		EmployeeClass yesterDayempClass = employeeClassService.getEmployeeClassSetting(yesterDaycondition);
		if(yesterDayempClass!=null){
			//??????????????????????????????
			AttnWorkHours yesterDayWorkHours = new AttnWorkHours(); 
			yesterDayWorkHours.setEmployeeId(empId);
			yesterDayWorkHours.setWorkDate(DateUtils.addDay(date, -1));
			List<AttnWorkHours> yesterDayWorkHoursList = attnWorkHoursService.getListByCondition(yesterDayWorkHours);
			Map<Integer,Date> yesterdsayMap = new HashMap<Integer,Date>();
			for(AttnWorkHours data:yesterDayWorkHoursList){
				yesterdsayMap.put(data.getDataType(), data.getEndTime());
			}
			Date yesterdayOffTime = null;
			//??????????????????
			if(yesterdsayMap!=null&&yesterdsayMap.containsKey(0)){
				yesterdayOffTime = yesterdsayMap.get(0);
			}
			//??????????????????
            if(yesterdsayMap!=null&&yesterdsayMap.containsKey(40)){
				yesterdayOffTime = yesterdsayMap.get(40);
			}
            //????????????
            if(yesterdsayMap!=null&&yesterdsayMap.containsKey(2)){
				yesterdayOffTime = yesterdsayMap.get(2);
			}
			//????????????
            if(yesterdsayMap!=null&&yesterdsayMap.containsKey(3)){
				yesterdayOffTime = yesterdsayMap.get(3);
			}
            //????????????
            if(yesterdsayMap!=null&&yesterdsayMap.containsKey(5)){
				yesterdayOffTime = yesterdsayMap.get(5);
			}
            //??????????????????????????????
			if(yesterdayOffTime!=null){
				result.put("yesterdayOffTimeStr", DateUtils.format(yesterdayOffTime, DateUtils.FORMAT_SHORT_CN)+"???????????????"+DateUtils.format(yesterdayOffTime, DateUtils.FORMAT_HH_MM));
				result.put("yesterdayOffTime",DateUtils.format(yesterdayOffTime, DateUtils.FORMAT_LONG));
			}else{
				result.put("yesterdayOffTimeStr", DateUtils.format(yesterdayOffTime, DateUtils.FORMAT_SHORT_CN)+"??????????????? ??????");
				result.put("yesterdayOffTime",DateUtils.format(yesterdayOffTime, DateUtils.FORMAT_LONG));
			}
		}else{
			//??????????????????6???00 -6???00 ??????????????????
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
				//????????????????????????????????????
				if(DateUtils.format(date, DateUtils.FORMAT_SHORT).equals(DateUtils.format(yesterdayOffTime, DateUtils.FORMAT_SHORT))){
					//??????????????????
					result.put("yesterdayOffTimeStr", DateUtils.format(yesterdayOffTime, DateUtils.FORMAT_SHORT_CN)+"???????????????"+DateUtils.format(yesterdayOffTime, DateUtils.FORMAT_HH_MM));
					result.put("yesterdayOffTime",DateUtils.format(yesterdayOffTime, DateUtils.FORMAT_LONG));
				}else{
					//????????????
					result.put("yesterdayOffTimeStr", DateUtils.format(yesterdayOffTime, DateUtils.FORMAT_SHORT_CN)+"??????????????????"+DateUtils.format(yesterdayOffTime, DateUtils.FORMAT_HH_MM));
					result.put("yesterdayOffTime",DateUtils.format(yesterdayOffTime, DateUtils.FORMAT_LONG));
				}
			}else{
				//????????????
				result.put("yesterdayOffTimeStr", DateUtils.format(yestday, DateUtils.FORMAT_SHORT_CN)+"?????????????????? ??????");
			}
		}
		result.put("success", true);
		return result;
	}
	
	/**
	 * H5????????????????????????
	 */
	@Override
	public void setValueToVO(TaskVO taskVO, String processInstanceId) {
		//???????????????
		RemoveSubordinateAbsence removeSubordinateAbsence = removeSubordinateAbsenceMapper
				.getByProcessId(processInstanceId);
		if(removeSubordinateAbsence!=null){
			taskVO.setProcessName("??????????????????");
			String redirectUrl = "/removeSubordinateAbsence/approval.htm?flag=no&removeSubordinateAbsenceId="+removeSubordinateAbsence.getId();
			if(!(taskVO.getProcessStatu()==null)) {
				redirectUrl = "/removeSubordinateAbsence/approval.htm?flag=can&removeSubordinateAbsenceId="+removeSubordinateAbsence.getId();
			}
			removeSubordinateAbsence.setAttendanceHour(removeSubordinateAbsence.getAttendanceHour() == null ? "??????" : removeSubordinateAbsence.getAttendanceHour());
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
			//?????????????????????  ???????????????????????????
			if(attendance.getApproalStatus()==null &&attendance.getApproalStatusList()==null){
				attendance.setCurrentUserDepart(deptDataByUserList);
				List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(currentUser.getEmployeeId());//????????????
				if(subEmployeeIdList!=null&&subEmployeeIdList.size()>0){
					attendance.setSubEmployeeIdList(subEmployeeIdList);
				}
			}
			pm.setPageNo(page);
			pm.setPageSize(rows);
			Integer total=null;
			if(attendance.getApproalStatusList()!=null &&attendance.getApproalStatusList().size()==4){//????????????
				total = removeSubordinateAbsenceMapper.myTaskListCount(attendance);
			}else{
				total = removeSubordinateAbsenceMapper.getCount(attendance); 
			}
			pm.setTotal(total);
			
			attendance.setOffset(pm.getOffset());
			attendance.setLimit(pm.getLimit());
			List<RemoveSubordinateAbsence> roles=null;
			if(attendance.getApproalStatusList()!=null &&attendance.getApproalStatusList().size()==4){//????????????
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
					//????????????
					Map<String,Object> sdoMap = new HashMap<String, Object>();
					sdoMap.put("empCode",attn.getEmpCode());
					sdoMap.put("empName",attn.getEmpName());
					sdoMap.put("departName",attn.getDepartName());
					sdoMap.put("leaderName",attn.getLeaderName());
					sdoMap.put("overHoursOfAttendance",attn.getOverHoursOfAttendance());
					
					
					
					//??????????????????
					sdoMap.put("yesterdayOffTime",DateUtils.format(attn.getYesterdayOffTime(), DateUtils.FORMAT_LONG));
					//??????????????????
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
						approvalStatus = "?????????";
					}else if(attn.getApproalStatus().intValue()==200){
						approvalStatus = "?????????";
					}else if(attn.getApproalStatus().intValue()==300){
						approvalStatus = "?????????";
					}else if(attn.getApproalStatus().intValue()==400){
						approvalStatus = "?????????";
					}else if(attn.getApproalStatus().intValue()==500){
						approvalStatus = "??????";
					}else if(attn.getApproalStatus().intValue()==600){
						approvalStatus = "????????????";
					}else if(attn.getApproalStatus().intValue()==700){
						approvalStatus = "????????????";
					}
					sdoMap.put("approvalStatus",approvalStatus);
					sMapList.add(sdoMap);
				}
			}
			String[] keys={"empCode", "empName", "departName", "leaderName","overHoursOfAttendance","yesterdayOffTime", "attendanceDate", "attendanceHour", "removeAbsenceHours", "removeAbsenceReason","auditUser","approvalStatus"};
			String[] titles={"????????????", "?????????????????????", "??????","????????????","?????????????????????", "?????????????????????", "????????????", "????????????", "??????????????????", "???????????????", "???????????????", "??????"}; 
			return ExcelUtil.exportExcel(sMapList, keys, titles, "???????????????.xls");
		}	
	}
}
