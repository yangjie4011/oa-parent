package com.ule.oa.base.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.flowable.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;












import com.ule.oa.base.cache.ConfigCacheManager;
import com.ule.oa.base.common.OaCommon;
import com.ule.oa.base.mapper.ActHiTaskinstMapper;
import com.ule.oa.base.mapper.EmpApplicationBusinessMapper;
import com.ule.oa.base.mapper.EmpApplicationOvertimeMapper;
import com.ule.oa.base.mapper.EmpLeaveMapper;
import com.ule.oa.base.mapper.EmployDutyMapper;
import com.ule.oa.base.mapper.EmployeeMapper;
import com.ule.oa.base.mapper.LeaveRecordDetailMapper;
import com.ule.oa.base.mapper.LeaveRecordMapper;
import com.ule.oa.base.mapper.RabcUserMapper;
import com.ule.oa.base.po.ActHiTaskinst;
import com.ule.oa.base.po.AnnualVacation;
import com.ule.oa.base.po.AttnSignRecord;
import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.EmpApplicationBusiness;
import com.ule.oa.base.po.EmpApplicationOvertime;
import com.ule.oa.base.po.EmpLeave;
import com.ule.oa.base.po.EmployDuty;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.po.EmployeeDuty;
import com.ule.oa.base.po.LeaveRecord;
import com.ule.oa.base.po.LeaveRecordDetail;
import com.ule.oa.base.po.User;
import com.ule.oa.base.po.tbl.ViewTaskInfoTbl;
import com.ule.oa.base.service.AnnualVacationService;
import com.ule.oa.base.service.AttnSignRecordService;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmpApplicationOvertimeService;
import com.ule.oa.base.service.EmpLeaveService;
import com.ule.oa.base.service.EmployeeAppService;
import com.ule.oa.base.service.EmployeeClassService;
import com.ule.oa.base.service.EmployeeDutyService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.service.ViewTaskInfoService;
import com.ule.oa.base.util.SendMailUtil;
import com.ule.oa.base.vo.TaskVO;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.po.CommonPo;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.ExcelUtil;
import com.ule.oa.common.utils.GetHours;
import com.ule.oa.common.utils.PageModel;
import com.ule.oa.common.utils.http.IPUtils;
import com.ule.oa.common.utils.json.JSONUtils;

/**
 * @ClassName: ??????????????????
 * @Description: ??????????????????
 * @author yangjie
 * @date 2017???6???12???
 */
@Service
public class EmpApplicationOvertimeServiceImpl implements EmpApplicationOvertimeService {
	private Logger logger = LoggerFactory.getLogger(EmpApplicationOvertimeServiceImpl.class);
	
	@Autowired
	private EmpApplicationOvertimeMapper empApplicationOvertimeMapper;
	@Autowired
	private UserService userService;
	@Autowired
	private AnnualVacationService annualVacationService;
	@Autowired
	private EmpLeaveService empLeaveService;
	@Autowired
	private EmployeeMapper employeeMapper;
	@Autowired
	private EmployeeAppService employeeAppService;
	@Autowired
	private EmployDutyMapper employDutyMapper;
	@Autowired
	private DepartService departService;	
	@Resource
	private ConfigCacheManager configCacheManager;
	@Resource
	private EmployeeClassService employeeClassService;
	@Autowired
	private ActivitiServiceImpl activitiServiceImpl;
	@Resource
	private ConfigService configService;
	@Autowired
	private AttnSignRecordService attnSignRecordService;
	@Autowired
	private EmployeeDutyService employeeDutyService;
	@Autowired
	private ViewTaskInfoService viewTaskInfoService;
	@Autowired
	private LeaveRecordMapper leaveRecordMapper;
	@Autowired
	private LeaveRecordDetailMapper leaveRecordDetailMapper;
	@Autowired
	private ActHiTaskinstMapper actHiTaskinstMapper;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private EmpLeaveMapper empLeaveMapper;
	@Autowired
	private RabcUserMapper rabcUserMapper;
	@Autowired
	private EmpApplicationBusinessMapper empApplicationBusinessMapper;
	
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void save(HttpServletRequest request,EmpApplicationOvertime userOvertime) throws Exception{
		
		User user = userService.getCurrentUser();
		logger.info("??????????????????save??????:ApplyDate="+userOvertime.getApplyDate()+";ApplyType="+userOvertime.getApplyType()+";ExpectStartTime="+userOvertime.getExpectStartTime()
				+";ExpectEndTime="+userOvertime.getExpectEndTime()+";Reason="+userOvertime.getReason()
				+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));

		if(userOvertime.getApplyDate()==null){
			throw new OaException("?????????????????????????????????");
		}
		//?????????????????????????????????
//		Config departPrivilegeCon = new Config();
//		departPrivilegeCon.setCode("overtime_apply_depart_privilege");
//		List<Config> departPrivilegeConL = configService.getListByCondition(departPrivilegeCon);
		boolean hasPrivilege = false;
//		for(Config data:departPrivilegeConL){
//			if(data.getDisplayCode().equals(user.getDepart().getCode())){
//				hasPrivilege = true;
//				break;
//			}
//		}
		
		//???????????????
		boolean isAgent = true;
		//???????????????
	    boolean isAgent1 = true;
		
	    //???????????????
		AnnualVacation vacation = new AnnualVacation();
		vacation.setAnnualDate(userOvertime.getApplyDate());
		List<AnnualVacation> vacationList = annualVacationService.getListByCondition(vacation);
		for(AnnualVacation va:vacationList){
			if(va.getType().intValue()==AnnualVacation.YYPE_LEGAL.intValue()){
				isAgent = false;
				break;
			}
			if(va.getType().intValue()==AnnualVacation.YYPE_VACATION.intValue()){
				isAgent1 = false;
				break;
			}
		}
		
		//???????????????????????????????????????????????????
		if(!hasPrivilege&&isAgent){
			throw new OaException("????????????????????????????????????????????????????????????");
		}
		
		//18??????????????????????????????????????????18???????????????????????????????????????
		Config configCondition = new Config();
		configCondition.setCode("timeLimit1");
		List<Config> limit = configService.getListByCondition(configCondition);
		int num = Integer.valueOf(limit.get(0).getDisplayCode());
		
		String nowDate18 = DateUtils.getNow("yyyy-MM-dd") + " 18:00:00";
		if(DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_LONG), DateUtils.FORMAT_LONG).getTime()<=DateUtils.parse(nowDate18, DateUtils.FORMAT_LONG).getTime()){
			//18????????????????????????????????????
			if(DateUtils.getIntervalDays(DateUtils.parse(DateUtils.getNow("yyyy-MM-dd"),DateUtils.FORMAT_SHORT),userOvertime.getApplyDate())<num-1){
				throw new OaException("18??????????????????????????????????????????????????????");
			}
		}else{
			//18???????????????????????????????????????
			if(DateUtils.getIntervalDays(DateUtils.parse(DateUtils.getNow("yyyy-MM-dd"),DateUtils.FORMAT_SHORT),userOvertime.getApplyDate())<num){
				throw new OaException("18????????????????????????????????????????????????");
			}
		}
		if(userOvertime.getApplyType()==null){
			throw new OaException("????????????????????????");
		}
        if(userOvertime.getExpectStartTime()==null){
        	throw new OaException("?????????????????????????????????");
		}
        if(userOvertime.getExpectEndTime()==null){
        	throw new OaException("?????????????????????????????????");
		}
        if(StringUtils.isBlank(userOvertime.getReason())){
        	throw new OaException("???????????????????????????");
		}
		userOvertime.setEmployeeId(user.getEmployeeId());
		userOvertime.setApplyDate(userOvertime.getApplyDate());
		int count = getEaoByEmpAndDateCount(userOvertime);
		if(count>0){
			throw new OaException("????????????????????????");
		}
		userOvertime.setStartTime(DateUtils.getFirstDay(userOvertime.getApplyDate()));
		userOvertime.setEndTime(DateUtils.getLastDay(userOvertime.getApplyDate()));
		Double monthCount = getTotalWorkHours(userOvertime);
		if(monthCount!=null&&monthCount+userOvertime.getExpectDuration()>36){
			throw new OaException("??????????????????????????????36?????????");
		}
		
		String date = DateUtils.format(userOvertime.getApplyDate(), DateUtils.FORMAT_SHORT);
		
		//????????????????????????????????????
		userOvertime.setStartTime(DateUtils.parse(date.substring(0, 4)+"-01-01",DateUtils.FORMAT_SHORT));
		userOvertime.setEndTime(DateUtils.parse(date.substring(0, 4)+"-12-31",DateUtils.FORMAT_SHORT));
		Double unCompleteHours = getUnCompleteHours(userOvertime);
		
		//????????????????????????
		EmpLeave empLeave = new EmpLeave();
		empLeave.setEmployeeId(user.getEmployee().getId());
		empLeave.setYear(Integer.valueOf(date.substring(0, 4)));
		empLeave.setType(ConfigConstants.LEAVE_TYPE_5);
		empLeave.setIsActive(0);
		EmpLeave empLeaveY = empLeaveService.getByCondition(empLeave);
		if(empLeaveY!=null&&empLeaveY.getAllowRemainDays()!=null){
			
			if((empLeaveY.getAllowRemainDays()+userOvertime.getExpectDuration()+unCompleteHours)>80d){
				throw new OaException("????????????????????????????????????80???????????????????????????");
			}
		}
		
		//????????????????????????parentId
		if(empLeaveY!=null&&empLeaveY.getId()!=null){
			empLeave.setParendId(empLeaveY!=null?empLeaveY.getId():null);
			empLeave.setYear(Integer.valueOf(date.substring(5, 7)));
			EmpLeave empLeaveM = empLeaveService.getByCondition(empLeave);
			if(empLeaveM!=null&&empLeaveM.getAllowRemainDays()!=null){
				double usedDays = 0d;
				double blockedDays = 0d;
				if(empLeaveM.getUsedDays()!=null){
					usedDays = empLeaveM.getUsedDays();
				}
				if(empLeaveM.getBlockedDays()!=null){
					blockedDays = empLeaveM.getBlockedDays();
				}
				if((blockedDays+usedDays+empLeaveM.getAllowRemainDays()+userOvertime.getExpectDuration())>36d){
					throw new OaException("??????????????????????????????36?????????");
				}
			}
		}
		
		if(userOvertime.getExpectStartTime().getTime()>userOvertime.getExpectEndTime().getTime()){
			userOvertime.setExpectEndTime(DateUtils.addDay(userOvertime.getExpectEndTime(), 1));
		}
		userOvertime.setEmployeeId(user.getEmployee().getId());
		userOvertime.setCnName(user.getEmployee().getCnName());
		userOvertime.setCode(user.getEmployee().getCode());
		userOvertime.setDepartId(user.getDepart().getId());
		userOvertime.setDepartName(user.getDepart().getName());
		userOvertime.setPositionId(user.getPosition().getId());
		userOvertime.setPositionName(user.getPosition().getPositionName());
		userOvertime.setSubmitDate(new Date());
		userOvertime.setDelFlag(CommonPo.STATUS_NORMAL);
		userOvertime.setCreateTime(new Date());
		userOvertime.setCreateUser(user.getEmployee().getCnName());
		userOvertime.setApprovalStatus(ConfigConstants.DOING_STATUS);
		userOvertime.setVersion(null);
		
		Date over24 = DateUtils.parse(DateUtils.format(DateUtils.addDay(userOvertime.getApplyDate(), 1), DateUtils.FORMAT_SHORT)+" 00:00:01", DateUtils.FORMAT_LONG);
		
		//?????????????????????????????????24???
		AnnualVacation legal = new AnnualVacation();
		legal.setType(AnnualVacation.YYPE_LEGAL);
		List<AnnualVacation> legalList = annualVacationService.getListByCondition(legal);
		Map<Date,Date> legalBeforeMap = new HashMap<Date,Date>();
		for(AnnualVacation va:legalList){
			legalBeforeMap.put(DateUtils.addDay(va.getAnnualDate(), -1), DateUtils.addDay(va.getAnnualDate(), -1));
		}
		if(legalBeforeMap!=null&&legalBeforeMap.containsKey(userOvertime.getApplyDate())){
			if(userOvertime.getExpectEndTime().getTime()>over24.getTime()){
				throw new OaException("??????????????????????????????????????????????????????24??????");
			}
		}
		
		boolean isduty = false;
		Date start = null;
		Date end = null;
		if(!isAgent){
			//???????????????????????????
			EmployDuty dutyP = new EmployDuty();
			dutyP.setEmployId(userOvertime.getEmployeeId());
			dutyP.setDutyDate(userOvertime.getApplyDate());
			List<EmployDuty> dutyList = employDutyMapper.selectByCondition(dutyP);
		    if(dutyList!=null&&dutyList.size()>0){
		    	isduty = true;
		    	start = dutyList.get(0).getStartTime();
				end = dutyList.get(0).getEndTime();
		    }
		}
		//??????????????????????????????
		if(!isAgent&&!isduty){
			throw new OaException("????????????????????????????????????????????????");
		}
		if(!isAgent&&isduty){
			if(!(userOvertime.getExpectStartTime().getTime()>=start.getTime()&&userOvertime.getExpectEndTime().getTime()<=end.getTime())){
				throw new OaException("?????????????????????????????????????????????????????????");
			}
			//??????????????????????????????24???
			if(userOvertime.getExpectEndTime().getTime()>over24.getTime()){
				throw new OaException("?????????????????????????????????????????????24??????");
			}
		}
		
		boolean isduty1 = false;
		Date end1 = null;
		if(!isAgent1){
			//???????????????????????????
			EmployDuty dutyP = new EmployDuty();
			dutyP.setEmployId(userOvertime.getEmployeeId());
			dutyP.setDutyDate(userOvertime.getApplyDate());
			List<EmployDuty> dutyList = employDutyMapper.selectByCondition(dutyP);
		    if(dutyList!=null&&dutyList.size()>0){
		    	isduty1 = true;
				end1 = dutyList.get(0).getEndTime();
		    }
		}
		//??????????????????????????????
		if(!isAgent1&&!isduty1){
			throw new OaException("????????????????????????????????????????????????");
		}
		if(!isAgent1&&isduty1){
			if(userOvertime.getExpectStartTime().getTime()<DateUtils.addHour(end1, 1).getTime()){
				throw new OaException("??????????????????????????????????????????????????????");
			}
			//??????????????????????????????24???
			if(userOvertime.getExpectEndTime().getTime()>over24.getTime()){
				throw new OaException("?????????????????????????????????????????????24??????");
			}
		}
		//XXX----------------------------????????????------------------------------
		String processInstanceId = activitiServiceImpl.startByKey(ConfigConstants.OVERTIME_KEY);
		
		//-----------------start-----------------------????????????????????????
		ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
		flow.setAssigneeName(user.getEmployee().getCnName());
		flow.setDepartName(user.getDepart().getName());
		flow.setPositionName(user.getPosition().getPositionName());
		flow.setFinishTime(new Date());
		flow.setComment(userOvertime.getReason());
		flow.setProcessId(processInstanceId);
		flow.setProcessKey(ConfigConstants.OVERTIME_KEY);
		flow.setStatu(0);
		viewTaskInfoService.save(flow);
		
		//???????????????????????????
		userOvertime.setProcessInstanceId(processInstanceId);
		empApplicationOvertimeMapper.save(userOvertime);
	}

	@Override
	public EmpApplicationOvertime getById(Long id) {
		return empApplicationOvertimeMapper.getById(id);
	}

	@Override
	public void chargeTime(EmpApplicationOvertime old,User user) throws Exception {
		String date = DateUtils.format(old.getApplyDate(), DateUtils.FORMAT_SHORT);
		EmpLeave empLeave = new EmpLeave();
		empLeave.setEmployeeId(old.getEmployeeId());
		empLeave.setYear(Integer.valueOf(date.substring(0, 4)));
		empLeave.setType(ConfigConstants.LEAVE_TYPE_5);
		empLeave.setIsActive(0);
		EmpLeave empLeaveList = empLeaveService.getByCondition(empLeave);
		//??????????????????????????????
		Employee employee = employeeMapper.getById(old.getEmployeeId());
		//????????????
		LeaveRecord leaveRecord = new LeaveRecord();
		leaveRecord.setEmployeeId(employee.getId());
		leaveRecord.setType(ConfigConstants.LEAVE_TYPE_5);
		leaveRecord.setDays(old.getActualDuration());
		leaveRecord.setBillId(old.getId());
		leaveRecord.setBillType(ConfigConstants.OVERTIME_KEY);
		leaveRecord.setDaysUnit(1);//??????
		leaveRecord.setCreateTime(new Date());
		leaveRecord.setCreateUser(user.getEmployee().getCnName());
		leaveRecord.setDelFlag(0);
		leaveRecord.setSource(0);
		leaveRecord.setRemark("??????????????????");
		leaveRecordMapper.save(leaveRecord);
		if(empLeaveList!=null&&empLeaveList.getId()==null){
			//??????????????????
			EmpLeave record = new EmpLeave();
			record.setCompanyId(employee.getCompanyId());
			record.setEmployeeId(employee.getId());
			record.setYear(Integer.valueOf(date.substring(0, 4)));
			record.setType(ConfigConstants.LEAVE_TYPE_5);
			record.setAllowDays(80d);
			record.setAllowRemainDays(old.getActualDuration());
			record.setIsActive(0);
			record.setCategory(0);
			record.setVersion(0L);
			record.setDelFlag(0);
			record.setStartTime(DateUtils.parse(date.substring(0, 4)+"-01-01 00:00:00",DateUtils.FORMAT_LONG));
			//record.setEndTime(DateUtils.parse(date.substring(0, 4)+"-12-31 23:59:59",DateUtils.FORMAT_LONG));
			record.setEndTime(DateUtils.parse((Integer.parseInt(date.substring(0, 4))+1)+"-02-29 23:59:59",DateUtils.FORMAT_LONG));
			record.setCreateTime(new Date());
			record.setCreateUser(user.getEmployee().getCnName());
		    empLeaveService.save(record);
		    EmpLeave month = new EmpLeave();
		    month.setCompanyId(employee.getCompanyId());
		    month.setEmployeeId(employee.getId());
		    month.setYear(Integer.valueOf(date.substring(5, 7)));
		    month.setType(ConfigConstants.LEAVE_TYPE_5);
		    month.setAllowDays(36d);
		    month.setAllowRemainDays(old.getActualDuration());
		    month.setIsActive(0);
		    month.setCategory(1);
		    month.setVersion(0L);
		    month.setDelFlag(0);
		    month.setStartTime(DateUtils.parse(date.substring(0, 4)+"-01-01 00:00:00",DateUtils.FORMAT_LONG));
		   // month.setEndTime(DateUtils.parse(date.substring(0, 4)+"-12-31 23:59:59",DateUtils.FORMAT_LONG));
		    month.setEndTime(DateUtils.parse((Integer.parseInt(date.substring(0, 4))+1)+"-02-29 23:59:59",DateUtils.FORMAT_LONG));
		    month.setParendId(record.getId());
		    month.setCreateTime(new Date());
		    month.setCreateUser(user.getEmployee().getCnName());
		    empLeaveService.save(month);
		    //????????????
			LeaveRecordDetail recordDetail = new LeaveRecordDetail();
			recordDetail.setLeaveRecordId(leaveRecord.getId());
			recordDetail.setBaseEmpLeaveId(month.getId());
			recordDetail.setDays(old.getActualDuration());
			recordDetail.setCreateTime(new Date());
			recordDetail.setCreateUser(user.getEmployee().getCnName());
			recordDetail.setDelFlag(0);
			leaveRecordDetailMapper.save(recordDetail);
		}else if(empLeaveList!=null&&empLeaveList.getId()!=null){
			//??????????????????
			EmpLeave currentY = new EmpLeave();
			currentY.setUpdateUser(user.getEmployee().getCnName());
			currentY.setUpdateTime(new Date());
			if(empLeaveList!=null&&empLeaveList.getAllowRemainDays()!=null){
				currentY.setAllowRemainDays((empLeaveList.getAllowRemainDays()+old.getActualDuration())>80d?80d:(empLeaveList.getAllowRemainDays()+old.getActualDuration()));
			}else{
				currentY.setAllowRemainDays(old.getActualDuration());
			}
			currentY.setVersion(empLeaveList.getVersion());
			currentY.setId(empLeaveList.getId());
			empLeaveService.updateById(currentY);
			//??????????????????????????????????????????
			EmpLeave currentM = new EmpLeave();
			currentM.setEmployeeId(old.getEmployeeId());
			currentM.setYear(Integer.valueOf(date.substring(5, 7)));
			currentM.setType(ConfigConstants.LEAVE_TYPE_5);
			currentM.setIsActive(0);
			currentM.setParendId(empLeaveList.getId());			
			EmpLeave currentMIsEx = empLeaveService.getByCondition(currentM);
			if(currentMIsEx!=null&&currentMIsEx.getId()!=null){
				//??????
				EmpLeave up = new EmpLeave();
				up.setUpdateUser(user.getEmployee().getCnName());
				up.setUpdateTime(new Date());
				if(currentMIsEx.getAllowRemainDays()!=null){
					double usedDays = 0d;
					double blockedDays = 0d;
					if(currentMIsEx.getUsedDays()!=null){
						usedDays = currentMIsEx.getUsedDays();
					}
					if(currentMIsEx.getBlockedDays()!=null){
						blockedDays = currentMIsEx.getBlockedDays();
					}
					up.setAllowRemainDays((blockedDays+usedDays+currentMIsEx.getAllowRemainDays()+old.getActualDuration())>36d?36d:(currentMIsEx.getAllowRemainDays()+old.getActualDuration()));
				}else{
					up.setAllowRemainDays(old.getActualDuration());
				}
				up.setId(currentMIsEx.getId());
				up.setVersion(currentMIsEx.getVersion());
				empLeaveService.updateById(up);
				//????????????
				LeaveRecordDetail recordDetail = new LeaveRecordDetail();
				recordDetail.setLeaveRecordId(leaveRecord.getId());
				recordDetail.setBaseEmpLeaveId(currentMIsEx.getId());
				recordDetail.setDays(old.getActualDuration());
				recordDetail.setCreateTime(new Date());
				recordDetail.setCreateUser(user.getEmployee().getCnName());
				recordDetail.setDelFlag(0);
				leaveRecordDetailMapper.save(recordDetail);
			}else{
				//??????
				EmpLeave add = new EmpLeave();
				add.setCreateUser(user.getEmployee().getCnName());
				add.setCreateTime(new Date());
				add.setCompanyId(employee.getCompanyId());
				add.setEmployeeId(employee.getId());
				add.setYear(Integer.valueOf(date.substring(5, 7)));
				add.setType(ConfigConstants.LEAVE_TYPE_5);
				add.setAllowDays(36d);
				add.setAllowRemainDays(old.getActualDuration());
				add.setIsActive(0);
				add.setCategory(1); 
				add.setVersion(0L);
			    add.setDelFlag(0);
			    add.setStartTime(DateUtils.parse(date.substring(0, 4)+"-01-01 00:00:00",DateUtils.FORMAT_LONG));
			   // add.setEndTime(DateUtils.parse(date.substring(0, 4)+"-12-31 23:59:59",DateUtils.FORMAT_LONG));
			    add.setEndTime(DateUtils.parse((Integer.parseInt(date.substring(0, 4))+1)+"-02-29 23:59:59",DateUtils.FORMAT_LONG));
			    add.setParendId(empLeaveList.getId());
			    empLeaveService.save(add);
			    //????????????
				LeaveRecordDetail recordDetail = new LeaveRecordDetail();
				recordDetail.setLeaveRecordId(leaveRecord.getId());
				recordDetail.setBaseEmpLeaveId(add.getId());
				recordDetail.setDays(old.getActualDuration());
				recordDetail.setCreateTime(new Date());
				recordDetail.setCreateUser(user.getEmployee().getCnName());
				recordDetail.setDelFlag(0);
				leaveRecordDetailMapper.save(recordDetail);
			}
		}
	}

	@Override
	public EmpApplicationOvertime getEaoByEmpAndDate(
			EmpApplicationOvertime userOvertime) {
		return empApplicationOvertimeMapper.getEaoByEmpAndDate(userOvertime);
	}

	@Override
	public int getEaoByEmpAndDateCount(EmpApplicationOvertime userOvertime) {
		return empApplicationOvertimeMapper.getEaoByEmpAndDateCount(userOvertime);
	}

	@Override
	public Double getTotalWorkHours(EmpApplicationOvertime userOvertime) {
		double workHours = 0;
		List<EmpApplicationOvertime> list = empApplicationOvertimeMapper.getTotalWorkHours(userOvertime);
		for(EmpApplicationOvertime overtime:list){
			if(overtime.getActualDuration()!=null){
				workHours += overtime.getActualDuration().doubleValue();
			}else if(overtime.getExpectDuration()!=null){
				workHours += overtime.getExpectDuration().doubleValue();
			}
		}
		return workHours;
	}
	
	//?????????????????????????????????
	public Double getUnCompleteHours(EmpApplicationOvertime userOvertime){
		double unCompleteHours = 0;
		List<EmpApplicationOvertime> list = empApplicationOvertimeMapper.getUnCompleteList(userOvertime);
		for(EmpApplicationOvertime overtime:list){
			if(overtime.getActualDuration()!=null){
				unCompleteHours += overtime.getActualDuration().doubleValue();
			}else if(overtime.getExpectDuration()!=null){
				unCompleteHours += overtime.getExpectDuration().doubleValue();
			}
		}
		return unCompleteHours;
	}
	
	public List<Map<String, Object>> getApplyOverTimeExcel(Date applyStartDate,Date applyEndDate,Long departId) throws Exception{
		EmpApplicationOvertime userOvertime = new EmpApplicationOvertime();
		
		userOvertime.setApplyStartDate(applyStartDate);
		userOvertime.setApplyEndDate(applyEndDate);
		userOvertime.setDepartId(departId);
		
		List<Map<String, Object>> listMap = empApplicationOvertimeMapper.getApplyOverTimeExcel(userOvertime);
		
		Long dpId = null;//??????id
		for(Map<String,Object> map : listMap){
			dpId = (Long)map.get("depart_id");
			
			map.put("depart_name", employeeAppService.getDepartName(dpId));
		}
		
		return listMap;
	}
	
	/**
	  * getApplyOverTimeSumReport(??????????????????????????????)
	  * @Title: getApplyOverTimeSumReport
	  * @Description: ??????????????????????????????
	  * @param applyStartDate
	  * @param applyEndDate
	  * @param departId
	  * @return
	  * @throws Exception    ????????????
	  * List<Map<String,Object>>    ????????????
	  * @throws
	 */
	public List<Map<String,Object>> getApplyOverTimeSumReport(Date applyStartDate,Date applyEndDate,Long departId) throws Exception{
		EmpApplicationOvertime userOvertime = new EmpApplicationOvertime();
		userOvertime.setApplyStartDate(applyStartDate);
		userOvertime.setApplyEndDate(applyEndDate);
		userOvertime.setDepartId(departId);
		userOvertime.setApprovalStatus(ConfigConstants.PASS_STATUS);
		
		//1.????????????????????????????????????
		List<Map<String,Object>> reportList = empApplicationOvertimeMapper.getApplyOverTimeSumReport(userOvertime);
		
		//2.?????????????????????????????????????????????
		for(Map<String,Object> reportMap : reportList){
			getApplyOverTime(reportMap);
		}
		
		//3.???????????????,???????????????????????????
		Long preEmpId = null;//????????????????????????id
		Long preMonth = null;//????????????????????????
		Map<String,Object> map = null;
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		Long dpId = null;//??????id
		for(Map<String,Object> reportMap : reportList){
			if(null != preEmpId && ( 
					preEmpId.equals((Long)reportMap.get("employee_id")) 
					&& preMonth.equals((Long)reportMap.get("month")) 
				)){
				
				map.put("time1", ((Double)reportMap.get("time1")).doubleValue() + ((Double)map.get("time1")));
				map.put("time2", ((Double)reportMap.get("time2")).doubleValue() + ((Double)map.get("time2")));
				map.put("time3", ((Double)reportMap.get("time3")).doubleValue() + ((Double)map.get("time3")));
				map.put("actual_duration", ((Double)reportMap.get("actual_duration")) + ((Double)map.get("actual_duration")));
				map.put("meals", ((Integer)reportMap.get("meals")) + ((Integer)map.get("meals")));
				map.put("trafficMeals1", ((Integer)reportMap.get("trafficMeals1")) + ((Integer)map.get("trafficMeals1")));
				map.put("trafficMeals2", ((Integer)reportMap.get("trafficMeals2")) + ((Integer)map.get("trafficMeals2")));
			}else{
				map = reportMap;
				
				//?????????????????????????????????+????????????
				dpId = (Long)map.get("depart_id");
				map.put("depart_name", employeeAppService.getDepartName(dpId));
				
				//???????????????????????????
				map.put("remainRest", getRemainRest(applyStartDate, (Long)reportMap.get("month"),(Long)reportMap.get("employee_id")));
				//????????????????????????
				map.put("canUseTime", getRemainRest(applyStartDate, null,(Long)reportMap.get("employee_id")));
				
				list.add(map);
			}
			
			preEmpId = (Long)reportMap.get("employee_id");
			preMonth = (Long)reportMap.get("month");
		}
		
		return list;
	}
	
	public Double getRemainRest(Date applyStartDate,Long month,Long employeeId){
		//???????????????????????????
		EmpLeave leave = new EmpLeave();
		leave.setYear(Integer.parseInt(DateUtils.getYear(applyStartDate)));
		if(month!=null) {
			leave.setMonth(Integer.parseInt(month + ""));
		}
		leave.setEmployeeId(employeeId);
		EmpLeave el = empLeaveService.getRemainRest(leave);
		
		if(null != el){
			return el.getAllowRemainDays() + el.getBlockedDays();
		}else{
			return 0.0;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void getApplyOverTime(Map<String,Object> reportMap){
		try{
				//??????????????????
				Date applyDate = (Date)reportMap.get("apply_date");
				//???????????????????????????1.5????????????2????????????3???
				Integer dayType = annualVacationService.getDayType(applyDate);
				Integer maxOverTime = 8;//??????????????????????????????8??????????????????[????????????????????????????????????-??????????????????>=9?????????????????????????????????2017-12-26 14:50:00,??????:4022???????????????????????????PM]
				Date compareDate_21 = DateUtils.parse(DateUtils.format(applyDate,DateUtils.FORMAT_SHORT) + " 21:00:00");
				Date compareDate_23 = DateUtils.parse(DateUtils.format(applyDate,DateUtils.FORMAT_SHORT) + " 23:00:00");
				Date actualEndTime = (Date)reportMap.get("actual_end_time");//????????????????????????
				Double actualDuration = (Double)reportMap.get("actual_duration");
				
				//??????1.5/2/3???????????????
				reportMap.put("time1", 0.00);
				reportMap.put("time2", 0.00);
				reportMap.put("time3", 0.00);
				reportMap.put("meals", 0);
				reportMap.put("trafficMeals1", 0);
				reportMap.put("trafficMeals2", 0);
				if(dayType.intValue() == 1){//???????????????
					reportMap.put("time1", actualDuration);
					
					//????????????????????????????????????3?????????????????????
					/*Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("employeeId", reportMap.get("employee_id") + "");
					paramMap.put("classDate", DateUtils.format(applyDate,DateUtils.FORMAT_SHORT));
					String OA_URL = configCacheManager.getConfigDisplayCode("MO_WEB_URL");*/
					String response = employeeClassService.getEmployeeClassJsonData((reportMap.get("employee_id") + ""), DateUtils.format(applyDate,DateUtils.FORMAT_SHORT));
					logger.info("???????????????????????????{}",response);
					
					Map<String, String> returnMap = JSONUtils.read(response, Map.class);
					Date compareDate_meals = DateUtils.addHour(DateUtils.parse(returnMap.get("endTime")),3);//????????????????????????9??????18??????
					logger.info("??????"+reportMap.get("cn_name")+"????????????{}-{}["+returnMap.get("className")+"]",returnMap.get("startTime"),returnMap.get("endTime"));
					
					if(actualEndTime.compareTo(compareDate_meals) >= 0){
						reportMap.put("meals", 1);
					}
				}else if(dayType.intValue() == 2){//????????????
					reportMap.put("time2", actualDuration);
					
					if(actualDuration >= maxOverTime){
						reportMap.put("meals", 1);
					}
				}else if(dayType.intValue() == 3){//?????????????????????
					reportMap.put("time3", actualDuration);
					
					if(actualDuration >= maxOverTime){
						reportMap.put("meals", 1);
					}
				}
				
				//???????????????????????????1(21???00-22:59)
				if(actualEndTime.compareTo(compareDate_21) >= 0 && actualEndTime.compareTo(compareDate_23) < 0){
					reportMap.put("trafficMeals1", 1);
				}else if(actualEndTime.compareTo(compareDate_23) >= 0){//???????????????????????????2(23?????????)
					reportMap.put("trafficMeals2", 1);
				}
			}catch(Exception e){
			logger.error("?????????????????????{}",e.toString());
		}
	}

	@Override
	public PageModel<EmpApplicationOvertime> getReportPageList(
			EmpApplicationOvertime userOvertime) {
		int page = userOvertime.getPage() == null ? 0 : userOvertime.getPage();
		int rows = userOvertime.getRows() == null ? 0 : userOvertime.getRows();
		
		PageModel<EmpApplicationOvertime> pm = new PageModel<EmpApplicationOvertime>();

		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
		if(deptDataByUserList==null || deptDataByUserList.size()==0){
			pm.setPageNo(0);
			pm.setPageSize(0);
			pm.setTotal(0);
			userOvertime.setOffset(pm.getOffset());
			userOvertime.setLimit(pm.getLimit());
			pm.setRows(new ArrayList<EmpApplicationOvertime>());
			return pm;
		}else{
			List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(currentUser.getEmployeeId());//????????????
			if(subEmployeeIdList!=null&&subEmployeeIdList.size()>0){
				userOvertime.setSubEmployeeIdList(subEmployeeIdList);
			}
			userOvertime.setCurrentUserDepart(deptDataByUserList);
			pm.setPageNo(page);
			pm.setPageSize(rows);
			Integer total = empApplicationOvertimeMapper.getReportCount(userOvertime);
			pm.setTotal(total);
			
			userOvertime.setOffset(pm.getOffset());
			userOvertime.setLimit(pm.getLimit());
			
			List<EmpApplicationOvertime> roles = empApplicationOvertimeMapper.getReportPageList(userOvertime);
			for(EmpApplicationOvertime og:roles){
				try{
					ViewTaskInfoTbl taskInfo = null;
					taskInfo = viewTaskInfoService.getFirstAuditUser(og.getProcessInstanceId(),ConfigConstants.OVERTIME_KEY,true);
					og.setAuditUser("");
					if(null != taskInfo){
						og.setAuditUser(taskInfo.getAssigneeName());
					}
				}catch(Exception e){
					og.setAuditUser(" ");
				}
			}
			pm.setRows(roles);
			return pm;
		}
	}
	
	@Override
	public HSSFWorkbook exportExcel(EmpApplicationOvertime overtime) {
		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
	
		if(deptDataByUserList==null || deptDataByUserList.size()==0){
			return null;
		}else{
			List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(currentUser.getEmployeeId());//????????????
			if(subEmployeeIdList!=null&&subEmployeeIdList.size()>0){
				overtime.setSubEmployeeIdList(subEmployeeIdList);
			}
			
			overtime.setCurrentUserDepart(deptDataByUserList);
		
			List<Map<String,Object>> sMapList = new ArrayList<Map<String,Object>>();
			List<EmpApplicationOvertime> list = empApplicationOvertimeMapper.getExportReportList(overtime);
			for(EmpApplicationOvertime og:list){
				try{
					ViewTaskInfoTbl taskInfo = null;
					taskInfo = viewTaskInfoService.getFirstAuditUser(og.getProcessInstanceId(),ConfigConstants.OVERTIME_KEY,true);
					og.setAuditUser("");
					if(null != taskInfo){
						og.setAuditUser(taskInfo.getAssigneeName());
					}
				}catch(Exception e){
					og.setAuditUser(" ");
				}
			}
			if(list!=null&&list.size()>0) {
				for (EmpApplicationOvertime re : list) {
					//????????????
					Map<String,Object> sdoMap = new HashMap<String, Object>();
					sdoMap.put("code",re.getCode());
					sdoMap.put("cnName",re.getCnName());
					sdoMap.put("departName",re.getDepartName());
					sdoMap.put("positionName",re.getPositionName());
					sdoMap.put("applyDate",DateUtils.format(re.getApplyDate(), DateUtils.FORMAT_SHORT));
					sdoMap.put("dayofweek",re.getDayofweek());
					String applyType = "";
					if(re.getApplyType().intValue()==100){
						applyType = "??????";
					}else if(re.getApplyType().intValue()==200){
						applyType = "??????";
					}else if(re.getApplyType().intValue()==300){
						applyType = "????????????";
					}else if(re.getApplyType().intValue()==400){
						applyType = "??????";
					}
					sdoMap.put("applyType",applyType);
					sdoMap.put("expectStartTime",DateUtils.format(re.getExpectStartTime(), DateUtils.FORMAT_LONG));
					sdoMap.put("expectEndTime",DateUtils.format(re.getExpectEndTime(), DateUtils.FORMAT_LONG));
					sdoMap.put("expectDuration",re.getExpectDuration());
					sdoMap.put("actualStartTime",re.getActualStartTime()==null?"":DateUtils.format(re.getActualStartTime(), DateUtils.FORMAT_LONG));
					sdoMap.put("actualEndTime",re.getActualEndTime()==null?"":DateUtils.format(re.getActualEndTime(), DateUtils.FORMAT_LONG));
					sdoMap.put("actualDuration",re.getActualDuration()==null?"":re.getActualDuration());
					sdoMap.put("auditUser",re.getAuditUser());
					sdoMap.put("submitDate",DateUtils.format(re.getSubmitDate(), DateUtils.FORMAT_LONG));
					String approvalStatus = "";
					if(re.getApprovalStatus().intValue()==100){
						approvalStatus = "?????????";
					}else if(re.getApprovalStatus().intValue()==200){
						approvalStatus = "?????????";
					}else if(re.getApprovalStatus().intValue()==300){
						approvalStatus = "?????????";
					}else if(re.getApprovalStatus().intValue()==400){
						approvalStatus = "?????????";
					}
					sdoMap.put("approvalStatus",approvalStatus);
					sdoMap.put("reason",re.getReason());
					sMapList.add(sdoMap);
				}
			}
			String[] keys={"code", "cnName", "departName", "positionName","applyDate","dayofweek", "applyType", "expectStartTime", "expectEndTime", "expectDuration", "actualStartTime", "actualEndTime", "actualDuration", "auditUser","submitDate","approvalStatus","reason"};
			String[] titles={"????????????", "?????????", "??????","??????","????????????", "??????", "????????????", "????????????????????????", "????????????????????????", "?????????????????????", "????????????????????????", "????????????????????????", "?????????????????????", "????????????","??????????????????","??????","????????????"}; 
			return ExcelUtil.exportExcel(sMapList, keys, titles, "??????????????????.xls");
		}
	}

	@Override
	public PageModel<Map<String, Object>> getApplyOverTimeSumReportByPage(
			EmpApplicationOvertime userOvertime) throws Exception {
		
		int page = userOvertime.getPage() == null ? 0 : userOvertime.getPage();
		int rows = userOvertime.getRows() == null ? 0 : userOvertime.getRows();
		
		if(userOvertime.getYearAndMonth()!=null&&!"".equals(userOvertime.getYearAndMonth())){
			userOvertime.setApplyStartDate(DateUtils.parse(userOvertime.getYearAndMonth()+"-01", DateUtils.FORMAT_SHORT));
			userOvertime.setApplyEndDate(DateUtils.getLastDay(DateUtils.parse(userOvertime.getYearAndMonth()+"-01", DateUtils.FORMAT_SHORT)));
		}else{
			userOvertime.setApplyStartDate(DateUtils.addMonth(DateUtils.getFirstDay(new Date()), -1));
			userOvertime.setApplyEndDate(DateUtils.addMonth(DateUtils.getLastDay(new Date()), -1));
		}
		
		PageModel<Map<String, Object>> pm = new PageModel<Map<String, Object>>();
		//??????????????????????????????
		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
		if(deptDataByUserList==null || deptDataByUserList.size()==0){
			pm.setPageNo(0);
			pm.setPageSize(0);
			pm.setTotal(0);
			userOvertime.setOffset(pm.getOffset());
			userOvertime.setLimit(pm.getLimit());
			pm.setRows(new ArrayList<>());
			return pm;
		}else{
			List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(currentUser.getEmployeeId());//????????????			
			pm.setPageNo(page);
			pm.setPageSize(rows);
			userOvertime.setApprovalStatus(ConfigConstants.PASS_STATUS);
			
			Employee emp = new Employee();
			if(subEmployeeIdList!=null&&subEmployeeIdList.size()>0){
				emp.setSubEmployeeIdList(subEmployeeIdList);
			}
			emp.setCnName(userOvertime.getCnName());
			emp.setId(userOvertime.getEmployeeId());
			emp.setCode(userOvertime.getCode());
			//????????????
			emp.setCurrentUserDepart(deptDataByUserList);
			if(null != userOvertime.getEmpTypeId()){
				emp.setEmpTypeId(userOvertime.getEmpTypeId().longValue());
			}else{
				emp.setEmpTypeIdList(configService.getNeedEmpTypeIdList());
			}
				
			if(null != userOvertime.getWorkType() && !"".equals(userOvertime.getWorkType())) {
				emp.setWorkType(Long.parseLong(userOvertime.getWorkType()));
			}
			if(null != userOvertime.getWhetherScheduling()) {
				emp.setWhetherScheduling(userOvertime.getWhetherScheduling().longValue());
			}
			List<Integer> departList = new ArrayList<Integer>();
			if(userOvertime.getSecondDepart()!=null){
				departList.add(userOvertime.getSecondDepart());
				emp.setDepartList(departList);
			}else if(userOvertime.getSecondDepart()==null&&userOvertime.getFirstDepart()!=null){
				departList.add(userOvertime.getFirstDepart());
				List<Depart> list = departService.getByParentId(Long.valueOf(String.valueOf(userOvertime.getFirstDepart())));
				for(Depart depart:list){
					departList.add(Integer.valueOf(String.valueOf(depart.getId())));
				}
				emp.setDepartList(departList);
			}
			emp.setWorkAddressType(0);
			
			Integer total = employeeMapper.getCount(emp);
			pm.setTotal(total);
			emp.setOffset(pm.getOffset());
			emp.setLimit(pm.getLimit());
			
			
			List<Map<String,Object>> reportList = new ArrayList<>();
			
			List<Employee> empList = employeeMapper.getPageList(emp);
			if(!empList.isEmpty()){
				List<Long> employeeIdList = new ArrayList<Long>();
				for(Employee employee:empList){
					employeeIdList.add(employee.getId());
				}
				userOvertime.setEmployeeIdList(employeeIdList);
				//1.????????????????????????????????????
				
				reportList = empApplicationOvertimeMapper.getApplyOverTimeSumReportByPage(userOvertime);
			}
			
			//2.?????????????????????????????????????????????
			for(Map<String,Object> reportMap : reportList){
				getApplyOverTime(reportMap);
			}
			
			//3.???????????????,???????????????????????????
			Long preEmpId = null;//????????????????????????id
			String preMonth = null;//????????????????????????
			Map<String,Object> map = null;
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			Long dpId = null;//??????id
			for(Map<String,Object> reportMap : reportList){
				if(null != preEmpId && ( 
						preEmpId.equals((Long)reportMap.get("employee_id")) 
						&& preMonth.equals((String)reportMap.get("month")) 
						)){
					
					map.put("time1", ((Double)reportMap.get("time1")).doubleValue() + ((Double)map.get("time1")));
					map.put("time2", ((Double)reportMap.get("time2")).doubleValue() + ((Double)map.get("time2")));
					map.put("time3", ((Double)reportMap.get("time3")).doubleValue() + ((Double)map.get("time3")));
					map.put("actual_duration", ((Double)reportMap.get("actual_duration")) + ((Double)map.get("actual_duration")));
					map.put("meals", ((Integer)reportMap.get("meals")) + ((Integer)map.get("meals")));
					map.put("trafficMeals1", ((Integer)reportMap.get("trafficMeals1")) + ((Integer)map.get("trafficMeals1")));
					map.put("trafficMeals2", ((Integer)reportMap.get("trafficMeals2")) + ((Integer)map.get("trafficMeals2")));
				}else{
					map = reportMap;
					
					//?????????????????????????????????+????????????
					dpId = (Long)map.get("depart_id");
					map.put("depart_name", employeeAppService.getDepartName(dpId));
					//???????????????????????????
					map.put("remainRest", getRemainRest(userOvertime.getApplyStartDate(), Long.valueOf(reportMap.get("month").toString().split("-")[1]),(Long)reportMap.get("employee_id")));
					list.add(map);
				}
				
				preEmpId = (Long)reportMap.get("employee_id");
				preMonth = (String)reportMap.get("month");
			}
			Map<Long,Map<String,Object>> isExist = new HashMap<Long,Map<String,Object>>();
			for(Map<String,Object> reportMap : list){
				isExist.put((Long)reportMap.get("employee_id"), reportMap);
			}
			
			List<Map<String,Object>> list0 = new ArrayList<Map<String,Object>>();
			for(Employee employee:empList){
				if(isExist!=null&&!isExist.containsKey(employee.getId())){
					Map<String,Object> map1 = new HashMap<String,Object>();
					map1.put("code", employee.getCode());
					map1.put("cn_name",employee.getCnName());
					map1.put("depart_name",employee.getEmpDepart()!=null?employee.getEmpDepart().getDepart().getName():"");
					map1.put("position_name",employee.getEmpPosition()!=null?employee.getEmpPosition().getPosition().getPositionName():"");
					map1.put("month",DateUtils.format(userOvertime.getApplyStartDate(), "yyyy-MM"));
					map1.put("time1", 0);
					map1.put("time2", 0);
					map1.put("time3", 0);
					map1.put("actual_duration", 0);
					map1.put("remainRest", getRemainRest(userOvertime.getApplyStartDate(), Long.valueOf(DateUtils.format(userOvertime.getApplyStartDate(), "MM")),employee.getId()));
					map1.put("meals", 0);
					map1.put("trafficMeals1", 0);
					map1.put("trafficMeals2", 0);
					list0.add(map1);
				}else{
					list0.add(isExist.get(employee.getId()));
				}
			}
			
			pm.setRows(list0);
			return pm;
		}
	}

	@Override
	public HSSFWorkbook exportSumReport(EmpApplicationOvertime userOvertime) throws Exception{
		//??????????????????
		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
		if(deptDataByUserList==null || deptDataByUserList.size()==0){
			return null;
		}else{
			List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(currentUser.getEmployeeId());//????????????
			
			if(userOvertime.getYearAndMonth()!=null&&!"".equals(userOvertime.getYearAndMonth())){
				userOvertime.setApplyStartDate(DateUtils.parse(userOvertime.getYearAndMonth()+"-01", DateUtils.FORMAT_SHORT));
				userOvertime.setApplyEndDate(DateUtils.getLastDay(DateUtils.parse(userOvertime.getYearAndMonth()+"-01", DateUtils.FORMAT_SHORT)));
			}else{
				userOvertime.setApplyStartDate(DateUtils.addMonth(DateUtils.getFirstDay(new Date()), -1));
				userOvertime.setApplyEndDate(DateUtils.addMonth(DateUtils.getLastDay(new Date()), -1));
			}
			userOvertime.setApprovalStatus(ConfigConstants.PASS_STATUS);
			
			Employee emp = new Employee();
			emp.setCnName(userOvertime.getCnName());
			emp.setId(userOvertime.getEmployeeId());
			emp.setCode(userOvertime.getCode());
			//????????????
			emp.setCurrentUserDepart(deptDataByUserList);
			if(null != userOvertime.getEmpTypeId()){
				emp.setEmpTypeId(userOvertime.getEmpTypeId().longValue());
			}else{
				emp.setEmpTypeIdList(configService.getNeedEmpTypeIdList());
			}
				
			if(null != userOvertime.getWorkType() && !"".equals(userOvertime.getWorkType())) {
				emp.setWorkType(Long.parseLong(userOvertime.getWorkType()));
			}
			if(null != userOvertime.getWhetherScheduling()) {
				emp.setWhetherScheduling(userOvertime.getWhetherScheduling().longValue());
			}
			
			List<Integer> departList = new ArrayList<Integer>();
			if(userOvertime.getSecondDepart()!=null){
				departList.add(userOvertime.getSecondDepart());
				emp.setDepartList(departList);
			}else if(userOvertime.getSecondDepart()==null&&userOvertime.getFirstDepart()!=null){
				departList.add(userOvertime.getFirstDepart());
				List<Depart> list = departService.getByParentId(Long.valueOf(String.valueOf(userOvertime.getFirstDepart())));
				for(Depart depart:list){
					departList.add(Integer.valueOf(String.valueOf(depart.getId())));
				}
				emp.setDepartList(departList);
			}
			if(subEmployeeIdList!=null&&subEmployeeIdList.size()>0){
				emp.setSubEmployeeIdList(subEmployeeIdList);
			}
			
			emp.setWorkAddressType(0);
			List<Employee> empList = employeeMapper.getPageList(emp);
			List<Long> employeeIdList = new ArrayList<Long>();
			for(Employee employee:empList){
				employeeIdList.add(employee.getId());
			}
			userOvertime.setEmployeeIdList(employeeIdList);
			//1.????????????????????????????????????
			List<Map<String,Object>> reportList = empApplicationOvertimeMapper.getApplyOverTimeSumReportByPage(userOvertime);
			
			//2.?????????????????????????????????????????????
			for(Map<String,Object> reportMap : reportList){
				getApplyOverTime(reportMap);
			}
			
			//3.???????????????,???????????????????????????
			Long preEmpId = null;//????????????????????????id
			String preMonth = null;//????????????????????????
			Map<String,Object> map = null;
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			Long dpId = null;//??????id
			for(Map<String,Object> reportMap : reportList){
				if(null != preEmpId && ( 
						preEmpId.equals((Long)reportMap.get("employee_id")) 
						&& preMonth.equals((String)reportMap.get("month")) 
						)){
					
					map.put("time1", ((Double)reportMap.get("time1")).doubleValue() + ((Double)map.get("time1")));
					map.put("time2", ((Double)reportMap.get("time2")).doubleValue() + ((Double)map.get("time2")));
					map.put("time3", ((Double)reportMap.get("time3")).doubleValue() + ((Double)map.get("time3")));
					map.put("actual_duration", ((Double)reportMap.get("actual_duration")) + ((Double)map.get("actual_duration")));
					map.put("meals", ((Integer)reportMap.get("meals")) + ((Integer)map.get("meals")));
					map.put("trafficMeals1", ((Integer)reportMap.get("trafficMeals1")) + ((Integer)map.get("trafficMeals1")));
					map.put("trafficMeals2", ((Integer)reportMap.get("trafficMeals2")) + ((Integer)map.get("trafficMeals2")));
				}else{
					map = reportMap;
					
					//?????????????????????????????????+????????????
					dpId = (Long)map.get("depart_id");
					map.put("depart_name", employeeAppService.getDepartName(dpId));
					//???????????????????????????
					map.put("remainRest", getRemainRest(userOvertime.getApplyStartDate(), Long.valueOf(reportMap.get("month").toString().split("-")[1]),(Long)reportMap.get("employee_id")));
					list.add(map);
				}
				
				preEmpId = (Long)reportMap.get("employee_id");
				preMonth = (String)reportMap.get("month");
			}
			Map<Long,Map<String,Object>> isExist = new HashMap<Long,Map<String,Object>>();
			for(Map<String,Object> reportMap : list){
				isExist.put((Long)reportMap.get("employee_id"), reportMap);
			}
			
			List<Map<String,Object>> list0 = new ArrayList<Map<String,Object>>();
			for(Employee employee:empList){
				if(isExist!=null&&!isExist.containsKey(employee.getId())){
					Map<String,Object> map1 = new HashMap<String,Object>();
					map1.put("code", employee.getCode());
					map1.put("cn_name",employee.getCnName());
					map1.put("depart_name",employee.getEmpDepart()!=null?employee.getEmpDepart().getDepart().getName():"");
					map1.put("position_name",employee.getEmpPosition()!=null?employee.getEmpPosition().getPosition().getPositionName():"");
					map1.put("month",DateUtils.format(userOvertime.getApplyStartDate(), "yyyy-MM"));
					map1.put("time1", 0);
					map1.put("time2", 0);
					map1.put("time3", 0);
					map1.put("actual_duration", 0);
					map1.put("remainRest", getRemainRest(userOvertime.getApplyStartDate(), Long.valueOf(DateUtils.format(userOvertime.getApplyStartDate(), "MM")),employee.getId()));
					map1.put("meals", 0);
					map1.put("trafficMeals1", 0);
					map1.put("trafficMeals2", 0);
					list0.add(map1);
				}else{
					list0.add(isExist.get(employee.getId()));
				}
			}
			String[] keys={"code", "cn_name", "depart_name", "position_name","month","time1", "time2", "time3", "actual_duration","remainRest", "meals", "trafficMeals1", "trafficMeals2"};
			String[] titles={"????????????", "?????????", "??????","??????","??????", "1.5????????????", "2????????????", "3????????????", "?????????????????????","?????????????????????", "??????????????????", "????????????????????????21:00-22:59???", "????????????????????????23????????????"}; 
			return ExcelUtil.exportExcel(list0, keys, titles, "??????????????????.xls");
		}

	}

	@Override
	public EmpApplicationOvertime queryByProcessInstanceId(
			String processInstanceId) {
		return empApplicationOvertimeMapper.queryByProcessInstanceId(processInstanceId);
	}

	@Override
	public void setValueToVO(TaskVO taskVO,String processInstanceId,String key) {
		EmpApplicationOvertime overtime = empApplicationOvertimeMapper.queryByProcessInstanceId(processInstanceId);
        if(overtime!=null){
        	taskVO.setProcessName("??????????????????");
    		taskVO.setCreatorDepart(overtime.getDepartName());
    		taskVO.setCreator(overtime.getCnName());
    		taskVO.setCreateTime(overtime.getCreateTime());
    		taskVO.setReProcdefCode("30");
    		taskVO.setProcessId(overtime.getProcessInstanceId());
    		taskVO.setResourceId(String.valueOf(overtime.getId()));
    		taskVO.setRedirectUrl("/empApplicationOvertime/approval.htm?flag=no&overtimeId="+overtime.getId());
    		if(!(taskVO.getProcessStatu()==null)) {
    			taskVO.setRedirectUrl("/empApplicationOvertime/approval.htm?flag=can&overtimeId="+overtime.getId());
    		}
    		if("actualTime".equals(key)){
    			taskVO.setRedirectUrl("/empApplicationOvertime/toAddActualTime.htm?flag=no&overtimeId="+overtime.getId());
    			if(!(taskVO.getProcessStatu()==null)) {
    				taskVO.setRedirectUrl("/empApplicationOvertime/toAddActualTime.htm?flag=can&overtimeId="+overtime.getId());
    			}
    		}
    		taskVO.setProcessStatu(overtime.getApprovalStatus());
			taskVO.setOperation(ConfigConstants.STATUS.getOperationByStatu(overtime.getApprovalStatus()));
			if(overtime.getApplyType().equals(100)) {
				taskVO.setView3(overtime.getProjectName());
			} else {
				taskVO.setView3(OaCommon.getApplyTypeMap().get(overtime.getApplyType()));
			}
			if(overtime.getActualStartTime() != null) {
				taskVO.setView4(DateUtils.format(overtime.getActualStartTime(), ConfigConstants.DATEFORMAT)+"~"+DateUtils.format(overtime.getActualEndTime(), ConfigConstants.DATEFORMAT) + "&nbsp;&nbsp;" + overtime.getActualDuration() + "??????");
			} else {
				taskVO.setView4(DateUtils.format(overtime.getExpectStartTime(), ConfigConstants.DATEFORMAT)+"~"+DateUtils.format(overtime.getExpectEndTime(), ConfigConstants.DATEFORMAT) + "&nbsp;&nbsp;" + overtime.getExpectDuration() + "??????");
			}
			taskVO.setView5(overtime.getReason());
        }
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void endHandle(String processInstanceId, User user) throws Exception{
		EmpApplicationOvertime old = empApplicationOvertimeMapper.queryByProcessInstanceId(processInstanceId);
		//???????????????????????????
		AnnualVacation vacation = new AnnualVacation();
		vacation.setType(AnnualVacation.YYPE_LEGAL);
		vacation.setAnnualDate(old.getApplyDate());
		List<AnnualVacation> vacationList = annualVacationService.getListByCondition(vacation);
		if(vacationList==null||vacationList.size()<=0){
			chargeTime(old,user);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void completeTask(HttpServletRequest request,String processId, String comment,
			String commentType,EmpApplicationOvertime param) throws Exception {
		
		logger.info("??????????????????completeTask:start??????");
		Long time1 = System.currentTimeMillis();
	
		User user = userService.getCurrentUser();
		logger.info("??????????????????completeTask??????:processId="+processId+";comment="+comment+";commentType="+commentType
			+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));

		Task task = activitiServiceImpl.queryTaskByProcessInstanceId(processId);
		EmpApplicationOvertime overtime = empApplicationOvertimeMapper.queryByProcessInstanceId(processId);
		if(task==null){
			throw new OaException("??????Id???"+processId+"?????????????????????");
		}else if(!StringUtils.equalsIgnoreCase(task.getAssignee(), user.getEmployeeId().toString())
				&&!StringUtils.equalsIgnoreCase(overtime.getEmployeeId().toString(), user.getEmployeeId().toString())){
			task = activitiServiceImpl.queryTaskByTaskIdAndCandidate(task.getId(), user.getEmployeeId().toString());
			if(task == null) {
				throw new OaException("?????????????????????????????????,????????????????????????");
			}
		}
		boolean type = StringUtils.startsWithIgnoreCase(task.getTaskDefinitionKey(), "personnel");
		if(overtime!=null){
			if(annualVacationService.check5WorkingDayNextmonth(DateUtils.format(overtime.getApplyDate(), DateUtils.FORMAT_SHORT),type)){
				logger.error(userService.getCurrentUser().getEmployee().getCnName()+"????????????Id="+overtime.getEmployeeId()+"???????????????:?????????????????????,???????????????");
				throw new OaException("?????????????????????,???????????????");
			}
			if(param!=null){
				overtime.setActualStartTime(param.getActualStartTime());
				overtime.setActualEndTime(param.getActualEndTime());
				
				//???????????????
				boolean isAgent = true;
				//???????????????
			    boolean isAgent1 = true;
			    //???????????????
				AnnualVacation vacation = new AnnualVacation();
				vacation.setAnnualDate(overtime.getApplyDate());
				List<AnnualVacation> vacationList = annualVacationService.getListByCondition(vacation);
				for(AnnualVacation va:vacationList){
					if(va.getType().intValue()==AnnualVacation.YYPE_LEGAL.intValue()){
						isAgent = false;
						break;
					}
					if(va.getType().intValue()==AnnualVacation.YYPE_VACATION.intValue()){
						isAgent1 = false;
						break;
					}
				}
				
				boolean isduty = false;
				Date start = null;
				Date end = null;
				Date over24 = DateUtils.parse(DateUtils.format(DateUtils.addDay(overtime.getApplyDate(), 1), DateUtils.FORMAT_SHORT)+" 00:00:01", DateUtils.FORMAT_LONG);
				if(!isAgent){
					//???????????????????????????
					EmployDuty dutyP = new EmployDuty();
					dutyP.setEmployId(overtime.getEmployeeId());
					dutyP.setDutyDate(overtime.getApplyDate());
					List<EmployDuty> dutyList = employDutyMapper.selectByCondition(dutyP);
				    if(dutyList!=null&&dutyList.size()>0){
				    	isduty = true;
				    	start = dutyList.get(0).getStartTime();
						end = dutyList.get(0).getEndTime();
				    }
				}
				//??????????????????????????????
				if(!isAgent&&!isduty){
					throw new OaException("????????????????????????????????????????????????");
				}
				if(!isAgent&&isduty){
					if(!(overtime.getActualStartTime().getTime()>=start.getTime()&&overtime.getActualEndTime().getTime()<=end.getTime())){
						throw new OaException("?????????????????????????????????????????????????????????");
					}
					//??????????????????????????????24???
					if(overtime.getActualEndTime().getTime()>over24.getTime()){
						throw new OaException("?????????????????????????????????????????????24??????");
					}
				}
				
				boolean isduty1 = false;
				Date end1 = null;
				if(!isAgent1){
					//???????????????????????????
					EmployDuty dutyP = new EmployDuty();
					dutyP.setEmployId(overtime.getEmployeeId());
					dutyP.setDutyDate(overtime.getApplyDate());
					List<EmployDuty> dutyList = employDutyMapper.selectByCondition(dutyP);
				    if(dutyList!=null&&dutyList.size()>0){
				    	isduty1 = true;
						end1 = dutyList.get(0).getEndTime();
				    }
				}
				//??????????????????????????????
				if(!isAgent1&&!isduty1){
					throw new OaException("????????????????????????????????????????????????");
				}
				if(!isAgent1&&isduty1){
					if(overtime.getActualStartTime().getTime()<DateUtils.addHour(end1, 1).getTime()){
						throw new OaException("??????????????????????????????????????????????????????");
					}
					//??????????????????????????????24???
					if(overtime.getActualEndTime().getTime()>over24.getTime()){
						throw new OaException("?????????????????????????????????????????????24??????");
					}
				}
				
				Map<String,Object> result = getAcTualTime(overtime);
				if(param.getActualStartTime().getTime()>param.getActualEndTime().getTime()){
					param.setActualEndTime(DateUtils.addDay(param.getActualEndTime(), 1));
				}
				if(!"".equals(result.get("actualEndTime1"))){
					if(DateUtils.compareDate(DateUtils.parse(result.get("actualEndTime1").toString(), DateUtils.FORMAT_LONG), param.getActualEndTime())==2){
						throw new OaException("???????????????????????????");
					}
				}else{
					throw new OaException("????????????????????????,???????????????");
				}
				if(param.getActualDuration()>Double.valueOf(String.valueOf(result.get("duration")))){
					throw new OaException("??????????????????????????????????????????");
				}
				if("1".equals(result.get("flag"))){
					throw new OaException("??????????????????????????????");
				}
				String date = DateUtils.format(overtime.getApplyDate(), DateUtils.FORMAT_SHORT);
				
				//????????????????????????????????????
				overtime.setStartTime(DateUtils.parse(date.substring(0, 4)+"-01-01",DateUtils.FORMAT_SHORT));
				overtime.setEndTime(DateUtils.parse(date.substring(0, 4)+"-12-31",DateUtils.FORMAT_SHORT));
				Double unCompleteHours = getUnCompleteHours(overtime);
				
				EmpLeave empLeave = new EmpLeave();
				empLeave.setEmployeeId(overtime.getEmployeeId());
				empLeave.setYear(Integer.valueOf(date.substring(0, 4)));
				empLeave.setType(ConfigConstants.LEAVE_TYPE_5);
				empLeave.setIsActive(0);
				EmpLeave empLeaveY = empLeaveService.getByCondition(empLeave);
				if(empLeaveY!=null&&empLeaveY.getAllowRemainDays()!=null){
					if((empLeaveY.getAllowRemainDays()+param.getActualDuration()+unCompleteHours-overtime.getExpectDuration())>80d){
						throw new OaException("????????????????????????????????????80???????????????????????????");
					}
				}
				empLeave.setYear(Integer.valueOf(date.substring(5, 7)));
				//????????????????????????parentId
				if(empLeaveY!=null&&empLeaveY.getId()!=null){
					empLeave.setParendId(empLeaveY!=null?empLeaveY.getId():null);
					EmpLeave empLeaveM = empLeaveService.getByCondition(empLeave);
					if(empLeaveM!=null&&empLeaveM.getAllowRemainDays()!=null){
						double usedDays = 0d;
						double blockedDays = 0d;
						if(empLeaveM.getUsedDays()!=null){
							usedDays = empLeaveM.getUsedDays();
						}
						if(empLeaveM.getBlockedDays()!=null){
							blockedDays = empLeaveM.getBlockedDays();
						}
						if((blockedDays+usedDays+empLeaveM.getAllowRemainDays()+param.getActualDuration())>36d){
							throw new OaException("??????????????????????????????36?????????");
						}
					}
				}
			}
			Integer approvalStatus ;
			if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.PASS)) {
				approvalStatus=type?ConfigConstants.PASS_STATUS:ConfigConstants.DOING_STATUS;
				
			}else if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.REFUSE)) {
				approvalStatus=ConfigConstants.REFUSE_STATUS;
			}else {
				approvalStatus=ConfigConstants.BACK_STATUS;
			}
			EmpApplicationOvertime update = new EmpApplicationOvertime();
			update.setApprovalStatus(approvalStatus);
			update.setId(overtime.getId());
			if(param!=null){
				update.setActualStartTime(param.getActualStartTime());
				update.setActualEndTime(param.getActualEndTime());
				update.setActualDuration(param.getActualDuration());
				if(param.getActualDuration()>overtime.getExpectDuration()){
					throw new OaException("?????????????????????????????????????????????");
				}
			}
			empApplicationOvertimeMapper.updateById(update);
			//-----------------start-----------------------????????????????????????
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
			//????????????????????????????????????assignee,??????????????????????????????????????????
			if(StringUtils.isBlank(task.getAssignee())) {
				activitiServiceImpl.claimTask(task.getId(), String.valueOf(user.getEmployeeId()));
			}
			activitiServiceImpl.completeTask(task.getId(),comment,null,commentType);
		}else{
			throw new OaException("???????????????"+processId+"?????????????????????????????????");
		}
		
		Long time2 = System.currentTimeMillis();
		logger.info("??????????????????completeTask:use time="+(time2-time1));
		logger.info("??????????????????completeTask:end??????");
		
	}
	
	
	public Map<String, Object> getAcTualTime(EmpApplicationOvertime ovrertime) {
		Map<String,Object> map = new HashMap<String, Object>();
		String actualEndTime = "";
		String actualEndTime1 = "";
		String actuaStartTime = "";
        double duration = 0;
        //?????????????????????????????????????????????????????????
        String flag = "0";
        String date_type = "0";//0-??????????????????1-??????????????????2-??????????????????3-???????????????
        //???????????????
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
			actuaStartTime = DateUtils.format(DateUtils.addHour(empClass.getEndTime(), 1), "HH:mm");                    
			//XXX:??????????????????????????????????????????,????????????????????????:??????????????????????????????12????????????????????????.
			//??????????????????1-12?????????????????????????????????????????????
			AttnSignRecord record = new AttnSignRecord();
			record.setEmployeeId(ovrertime.getEmployeeId());
			record.setStartTime(DateUtils.parse(DateUtils.format(DateUtils.addHour(empClass.getEndTime(), 1), DateUtils.FORMAT_LONG), DateUtils.FORMAT_LONG));
			record.setEndTime(DateUtils.parse(DateUtils.format(DateUtils.addHour(empClass.getEndTime(), 12), DateUtils.FORMAT_LONG),DateUtils.FORMAT_LONG));
			record.setEndTime(DateUtils.addMinute(record.getEndTime(), 20));
			List<AttnSignRecord> list = attnSignRecordService.getListBefore9(record);
			if(list!=null&&list.size()>0){
				actualEndTime = DateUtils.format(list.get(0).getSignTime(), "HH:mm");
				actualEndTime1 = DateUtils.format(list.get(0).getSignTime(), DateUtils.FORMAT_LONG);
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
			//??????????????????
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
			//??????????????????
			EmployeeDuty dutyP = new EmployeeDuty();
			dutyP.setEmployId(ovrertime.getEmployeeId());
			dutyP.setDutyDate(ovrertime.getApplyDate());
			List<EmployeeDuty> dutyList = employeeDutyService.selectByCondition(dutyP);
			
			actuaStartTime = DateUtils.format(DateUtils.addHour(dutyList.get(0).getEndTime(), 1), "HH:mm"); 
			
			//???????????????????????????????????????????????????5.50??????6???????????????????????????6??????9???10?????????????????????????????????
			//??????????????????????????????????????????9??????9???10???????????????9?????????????????????????????????6????????????????????????????????????????????????????????????????????????
			AttnSignRecord record = new AttnSignRecord();
			Date nextDay = DateUtils.addDay(ovrertime.getApplyDate(), 1);
			record.setEmployeeId(ovrertime.getEmployeeId());
			record.setStartTime(DateUtils.parse(DateUtils.format(nextDay, DateUtils.FORMAT_SHORT)+" 05:50:00", DateUtils.FORMAT_LONG));
			record.setEndTime(DateUtils.parse(DateUtils.format(nextDay, DateUtils.FORMAT_SHORT)+" 06:00:00", DateUtils.FORMAT_LONG));
			List<AttnSignRecord> list = attnSignRecordService.getListBefore9(record);
			if(list!=null&&list.size()>0){
				//???6??????9???05?????????????????????????????????
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
				//5.50??????6?????????????????????20:00???05:50?????????
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

	@Override
	public void updateProcessInstanceId(EmpApplicationOvertime newOvertime) {
			empApplicationOvertimeMapper.updateById(newOvertime);
	}

	@Override
	public void setValueToVO1(TaskVO taskVO, String processInstanceId,
			String key) {
		EmpApplicationOvertime overtime = empApplicationOvertimeMapper.queryByProcessInstanceId(processInstanceId);
        if(overtime!=null){
        	taskVO.setProcessName("??????????????????");
    		taskVO.setCreatorDepart(overtime.getDepartName());
    		taskVO.setCreator(overtime.getCnName());
    		taskVO.setCreateTime(overtime.getCreateTime());
    		taskVO.setReProcdefCode("30");
    		taskVO.setProcessId(overtime.getProcessInstanceId());
    		taskVO.setResourceId(String.valueOf(overtime.getId()));
    		taskVO.setRedirectUrl("/empApplicationOvertime/approval.htm?flag=no&overtimeId="+overtime.getId());
    		taskVO.setProcessStatu(overtime.getApprovalStatus());
			taskVO.setOperation(ConfigConstants.STATUS.getOperationByStatu(overtime.getApprovalStatus()));
			if(overtime.getApplyType().equals(100)) {
				taskVO.setView3(overtime.getProjectName());
			} else {
				taskVO.setView3(OaCommon.getApplyTypeMap().get(overtime.getApplyType()));
			}
			if(overtime.getActualStartTime() != null) {
				taskVO.setView4(DateUtils.format(overtime.getActualStartTime(), ConfigConstants.DATEFORMAT)+"~"+DateUtils.format(overtime.getActualEndTime(), ConfigConstants.DATEFORMAT) + "&nbsp;&nbsp;" + overtime.getActualDuration() + "??????");
			} else {
				taskVO.setView4(DateUtils.format(overtime.getExpectStartTime(), ConfigConstants.DATEFORMAT)+"~"+DateUtils.format(overtime.getExpectEndTime(), ConfigConstants.DATEFORMAT) + "&nbsp;&nbsp;" + overtime.getExpectDuration() + "??????");
			}
			taskVO.setView5(overtime.getReason());
        }
	}

	@Override
	public void backProcess(String processInstanceId, String taskDefKey)
			throws Exception {
		EmpApplicationBusiness overtime = empApplicationBusinessMapper.queryByProcessId(processInstanceId);
		if(overtime!=null){
			//??????flowable??????????????????
			ActHiTaskinst actHiTaskinst = new ActHiTaskinst();
			actHiTaskinst.setProcInstId(processInstanceId);
			actHiTaskinst.setTaskDefKey(taskDefKey);
			List<ActHiTaskinst> list = actHiTaskinstMapper.selectByCondition(actHiTaskinst);
			if(list!=null&&list.size()>0){
				activitiServiceImpl.back(processInstanceId, list.get(0).getExecutionId(), taskDefKey);
			}
		}
	}

	@Override
	public PageModel<EmpLeave> getOvertimeManagePageList(
			EmpLeave empovertimeLeave) throws OaException {
		int page = empovertimeLeave.getPage() == null ? 0 : empovertimeLeave.getPage();
		int rows = empovertimeLeave.getRows() == null ? 0 : empovertimeLeave.getRows();
		
		PageModel<EmpLeave> pm = new PageModel<EmpLeave>();

		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
		if(deptDataByUserList==null || deptDataByUserList.size()==0){
			pm.setPageNo(0);
			pm.setPageSize(0);
			pm.setTotal(0);
			empovertimeLeave.setOffset(pm.getOffset());
			empovertimeLeave.setLimit(pm.getLimit());
			pm.setRows(new ArrayList<EmpLeave>());
			return pm;
		}else{
			empovertimeLeave.setCurrentUserDepart(deptDataByUserList);
			pm.setPageNo(page);
			pm.setPageSize(rows);
			empovertimeLeave.setType(13);//??????????????????
			
			empovertimeLeave.setOffset(pm.getOffset());
			empovertimeLeave.setLimit(pm.getLimit());
			
			List<Long> jobStatusList=new ArrayList<Long>();
			jobStatusList.add(0L);
			jobStatusList.add(2L);
			empovertimeLeave.setJobStatusList(jobStatusList);
			
			//??????????????????????????????
			empovertimeLeave.setEmpTypeIdList(configService.getNeedEmpTypeIdList());

			Integer total = empLeaveMapper.getEmpPageCount(empovertimeLeave);
			pm.setTotal(total);
			List<EmpLeave> getEmpPageList = empLeaveMapper.getEmpPageList(empovertimeLeave);//??????????????????
			if(getEmpPageList==null || getEmpPageList.size()<=0){
				pm.setPageNo(0);
				pm.setPageSize(0);
				pm.setTotal(0);
				empovertimeLeave.setOffset(pm.getOffset());
				empovertimeLeave.setLimit(pm.getLimit());
				pm.setRows(new ArrayList<EmpLeave>());
				return pm;
			}
			
			List<Long> employeeIdList = new ArrayList<Long>();
			for(EmpLeave data:getEmpPageList){
				employeeIdList.add(data.getEmployeeId());
			}
			List<EmpLeave> overtimeSumUsedDay=empLeaveMapper.getSumOvertimeDay(empovertimeLeave.getYear(),employeeIdList);
			Map<Long, EmpLeave> empMap=new LinkedHashMap<Long, EmpLeave>();
			
			for (EmpLeave empLeave : overtimeSumUsedDay) {
				empMap.put(empLeave.getEmployeeId(), empLeave);
			}
			for (EmpLeave role: getEmpPageList){
				if(empMap!=null&&empMap.containsKey(role.getEmployeeId())){
					double usedDays = empMap.get(role.getEmployeeId()).getUsedDays()!=null?empMap.get(role.getEmployeeId()).getUsedDays():0d;
					double blockedDays = empMap.get(role.getEmployeeId()).getBlockedDays()!=null?empMap.get(role.getEmployeeId()).getBlockedDays():0d;
					double remainDays = empMap.get(role.getEmployeeId()).getAllowRemainDays()!=null?empMap.get(role.getEmployeeId()).getAllowRemainDays():0d;
					role.setUsedDays(usedDays);
					role.setAllowRemainDays(remainDays);
				}else{
					role.setUsedDays(0d);
					role.setAllowRemainDays(0d);
				}
	        }
			pm.setRows(getEmpPageList);
			return pm;
		}
	}
	@Override
	public Map<String, Object> queryInfoByEmp(long empId,Integer year,Integer page, Integer rows) {
		// TODO Auto-generated method stub
		Map<String, Object> result=new HashMap<String, Object>();
		
		
		EmpLeave empLeave=new EmpLeave(); //??????id?????????????????? ??????
		empLeave.setYear(year);
		empLeave.setEmployeeId(empId);
		empLeave.setType(13);
		List<Long> employeeIdList = new ArrayList<Long>();
		employeeIdList.add(empId);
		
		List<EmpLeave> pageList = empLeaveMapper.getPageList(empLeave);
		
		List<EmpLeave> overtimeSumUsedDay=empLeaveMapper.getSumOvertimeDay(year,employeeIdList);//??????????????????
		if(overtimeSumUsedDay!=null && overtimeSumUsedDay.size()>0){   //????????????????????????
			double remainDays = overtimeSumUsedDay.get(0).getAllowRemainDays()!=null?overtimeSumUsedDay.get(0).getAllowRemainDays():0;
			double blockedDays = overtimeSumUsedDay.get(0).getBlockedDays()!=null?overtimeSumUsedDay.get(0).getBlockedDays():0;
			overtimeSumUsedDay.get(0).setAllowRemainDays(remainDays+blockedDays);
			result.put("overtime", overtimeSumUsedDay.get(0));
		}else{                                       //??????????????? ?????? ??????????????? ??????
			List<EmpLeave> getEmpPageList = empLeaveMapper.getEmpPageList(empLeave);//??????????????????
			result.put("overtime", getEmpPageList.get(0));
		}
		
		if(pageList!=null && pageList.size()>0){
			PageModel<EmpLeave> pm = new PageModel<EmpLeave>();
			Integer page1 = page == null ? 0 : page;
			Integer rows1 = rows == null ? 0 : rows;
			pm.setPageNo(page1);
			pm.setPageSize(rows1);
			empLeave.setOffset(pm.getOffset());
			empLeave.setLimit(pm.getLimit());
			Integer total = empLeaveMapper.getCount(empLeave);
			pm.setTotal(total);
			List<EmpLeave> roles = empLeaveMapper.getPageList(empLeave);
			for(EmpLeave data:roles){
				double remainDays = data.getAllowRemainDays()!=null?data.getAllowRemainDays():0;
				double blockedDays = data.getBlockedDays()!=null?data.getBlockedDays():0;
				data.setAllowRemainDays(remainDays+blockedDays);
			}
			pm.setRows(roles);
			result.put("pm", pm);
		}
		
		return result;
	}
	
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> saveOverTimeManage(EmpLeave empovertimeLeave) {
		// TODO Auto-generated method stub
		
		User currentUser = userService.getCurrentUser();
		Map<String, Object> result=new HashMap<String, Object>();
		
		if(empovertimeLeave.getAllowRemainDays()==null || empovertimeLeave.getAllowRemainDays()==0){
			result.put("message", "???????????????????????????");
			result.put("success", false);
			return result;
		}
		if(empovertimeLeave.getStartTimeFormat()==null || empovertimeLeave.getEndTimeFormat()==null){
			result.put("message", "????????????????????????");
			result.put("success", false);
			return result;
		}
		if(empovertimeLeave.getAllowRemainDays()%0.5!=0){
			result.put("message", "????????????????????????????????????0.5????????????");
			result.put("success", false);
			return result;
		}
		
		//?????????????????????????????????
		String WorkDateFormatStart  = DateUtils.format(empovertimeLeave.getStartTimeFormat(),DateUtils.FORMAT_SHORT);
		String WorkDateFormatEnd  = DateUtils.format(empovertimeLeave.getEndTimeFormat(),DateUtils.FORMAT_SHORT);
		empovertimeLeave.setStartTime(DateUtils.parse(WorkDateFormatStart + " 00:00:00"));
		empovertimeLeave.setEndTime(DateUtils.parse(WorkDateFormatEnd + " 23:59:59"));
		//????????????????????????
		empovertimeLeave.setYear(Integer.parseInt(DateUtils.getYear(empovertimeLeave.getEndTime())));
		
		
		
		EmpLeave empLeave=new EmpLeave();
		empLeave.setYear(empovertimeLeave.getYear());
		empLeave.setEmployeeId(empovertimeLeave.getEmployeeId());
		empLeave.setType(13);
		
			empovertimeLeave.setCompanyId(1L);
			empovertimeLeave.setIsActive(0);
			empovertimeLeave.setDelFlag(0);
			empovertimeLeave.setType(13);
			
			empovertimeLeave.setAllowRemainDays(empovertimeLeave.getAllowRemainDays());
			
			empovertimeLeave.setUsedDays(0d);
			empovertimeLeave.setCreateTime(new Date());
			empovertimeLeave.setCreateUser(currentUser.getUserName());
			empLeaveMapper.save(empovertimeLeave);
			//????????????
			//??????
			LeaveRecord record = new LeaveRecord();
			record.setEmployeeId(empovertimeLeave.getEmployeeId());
			record.setType(empovertimeLeave.getType());
			if(empovertimeLeave.getType().intValue()==ConfigConstants.LEAVE_TYPE_13.intValue()){
				record.setDays(empovertimeLeave.getAllowRemainDays());
				record.setDaysUnit(1);//??????
			}
			record.setBillType(ConfigConstants.LEAVE_KEY);
			//overtime
			record.setUpdateType(2);//??????????????????
			record.setCreateTime(new Date());
			record.setCreateUser(currentUser.getEmployee().getCnName());
			record.setDelFlag(0);
			record.setSource(1);//hr??????
			record.setRemark(empovertimeLeave.getRemark());
			leaveRecordMapper.save(record);
			//??????????????????
			this.saveRecordDetail(record.getId(), empovertimeLeave.getId(), empovertimeLeave.getAllowRemainDays(), currentUser);
		
		result.put("message", "???????????????");
		result.put("success", true);
		
		return result;
	}

	public void saveRecordDetail(Long recordId,Long baseEmpLeaveId,Double days,
			User user){
		LeaveRecordDetail recordDetail = new LeaveRecordDetail();
		recordDetail.setLeaveRecordId(recordId);
		recordDetail.setBaseEmpLeaveId(baseEmpLeaveId);
		recordDetail.setDays(days);
		recordDetail.setCreateTime(new Date());
		recordDetail.setCreateUser(user.getEmployee().getCnName());
		recordDetail.setDelFlag(0);
		List<LeaveRecordDetail> isExist1 = leaveRecordDetailMapper.selectByCondition(recordDetail);
		if(isExist1!=null&&isExist1.size()>0){
			isExist1.get(0).setDays(isExist1.get(0).getDays()-days);
			leaveRecordDetailMapper.updateById(isExist1.get(0));
		}else{
			leaveRecordDetailMapper.save(recordDetail);
		}
	}
	
	
	
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> updateOverTimeManage(EmpLeave empovertimeLeave) {
		User currentUser = userService.getCurrentUser();
		Map<String, Object> result=new HashMap<String, Object>();
		if(empovertimeLeave.getAllowRemainDays()==null){
			result.put("message", "???????????????????????????");
			result.put("success", false);
			return result;
		}
		if(empovertimeLeave.getStartTimeFormat()==null || empovertimeLeave.getEndTimeFormat()==null){
			result.put("message", "????????????????????????");
			result.put("success", false);
			return result;
		}
		if(empovertimeLeave.getAllowRemainDays()!=0&&empovertimeLeave.getAllowRemainDays()%0.5!=0){
			result.put("message", "????????????????????????????????????0.5????????????");
			result.put("success", false);
			return result;
		}
		
		//?????????????????????????????????
		String WorkDateFormatStart  = DateUtils.format(empovertimeLeave.getStartTimeFormat(),DateUtils.FORMAT_SHORT);
		String WorkDateFormatEnd  = DateUtils.format(empovertimeLeave.getEndTimeFormat(),DateUtils.FORMAT_SHORT);
		empovertimeLeave.setStartTime(DateUtils.parse(WorkDateFormatStart + " 00:00:00"));
		empovertimeLeave.setEndTime(DateUtils.parse(WorkDateFormatEnd + " 23:59:59"));
		//????????????????????????
		empovertimeLeave.setYear(Integer.parseInt(DateUtils.getYear(empovertimeLeave.getEndTime())));
		

		EmpLeave empLeave=new EmpLeave();
		empLeave.setYear(empovertimeLeave.getYear());
		empLeave.setEmployeeId(empovertimeLeave.getEmployeeId());
		empLeave.setType(13);
		
		//??????????????????
		EmpLeave beforLeave = empLeaveMapper.getById(empovertimeLeave.getId()); //??????????????????		
		if(beforLeave.getBlockedDays()!=0){//??????????????? ????????????????????????
			empovertimeLeave.setStartTime(beforLeave.getStartTime());
			empovertimeLeave.setEndTime(beforLeave.getEndTime());
		}
		empovertimeLeave.setAllowRemainDays(empovertimeLeave.getAllowRemainDays());
		empovertimeLeave.setUpdateTime(new Date());
		empovertimeLeave.setUpdateUser(currentUser.getUserName());
		empovertimeLeave.setVersion(beforLeave.getVersion());
		empovertimeLeave.setType(13);
		empLeaveMapper.updateById(empovertimeLeave);
		//????????????
		//??????
		if(empovertimeLeave.getAllowRemainDays()-beforLeave.getAllowRemainDays()!=0){
			LeaveRecord record = new LeaveRecord();
			record.setEmployeeId(empovertimeLeave.getEmployeeId());
			record.setType(empovertimeLeave.getType());
			if(empovertimeLeave.getType().intValue()==ConfigConstants.LEAVE_TYPE_13.intValue()){
				record.setDays(empovertimeLeave.getAllowRemainDays()-beforLeave.getAllowRemainDays());
				record.setDaysUnit(1);//??????
			}
			record.setBillType(ConfigConstants.LEAVE_KEY);
			record.setCreateTime(new Date());
			record.setCreateUser(currentUser.getEmployee().getCnName());
			record.setUpdateType(2);//??????????????????
			record.setDelFlag(0);
			record.setSource(1);//hr??????
			record.setRemark(empovertimeLeave.getRemark());
			leaveRecordMapper.save(record);
			//??????????????????
			this.saveRecordDetail(record.getId(), empovertimeLeave.getId(), (empovertimeLeave.getAllowRemainDays()-beforLeave.getAllowRemainDays()), currentUser);
		}
		result.put("success", true);	
		result.put("message", "????????????!");
		return result;
	}

	@Override
	public Map<String, Object> queryInfoById(long id) {
		// TODO Auto-generated method stub
		Map<String, Object> result=new HashMap<String, Object>();
		if(id==0){
			result.put("message", "????????????????????????");
			result.put("success", false);
			return result;
		}
		EmpLeave empLeave=new EmpLeave();
		empLeave.setId(id);
		List<EmpLeave> list = empLeaveMapper.getPageList(empLeave);
		if(list!=null && list.size()>0){
			result.put("overtime", list.get(0));
			result.put("success", true);
		}else{
			result.put("message", "????????????????????????");
			result.put("success", false);
		}
		return result;
	}

	@Override
	public void remindWriteOvertimeApply() {
		
		//????????????????????????????????????
		AnnualVacation vacation = new AnnualVacation();
		vacation.setAnnualDate(new Date());
		vacation.setDateType(AnnualVacation.YYPE_LEGAL);
		List<AnnualVacation> vacationList = annualVacationService.getListByCondition(vacation);
		if(vacationList!=null&&vacationList.size()>0){
			//?????????????????????????????????
			EmployeeDuty duty = new EmployeeDuty();
			duty.setDutyDate(vacationList.get(0).getAnnualDate());
			List<EmployeeDuty> dutyList = employeeDutyService.selectByCondition(duty);
			
			//??????????????????
			for(EmployeeDuty data:dutyList){
				try{
					Employee emp = employeeMapper.getById(data.getEmployId());
					//????????????????????????
					String overtimeDate = DateUtils.format(data.getDutyDate(), DateUtils.FORMAT_SHORT);
					String applyTimeLast = DateUtils.format(DateUtils.addDay(data.getDutyDate(), 1), DateUtils.FORMAT_SHORT);
					String content = "Dear " + emp.getCnName() +":<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;??????"+overtimeDate+"??????????????????????????????????????????"+applyTimeLast+"&nbsp;18???????????????????????????????????????????????????????????????????????????3?????????????????????????????????????????????????????????????????????????????????????????????";
					SendMailUtil.sendNormalMail(content, emp.getEmail(), emp.getCnName(),"???????????????????????????????????????");
				}catch(Exception e){
					logger.info("?????????????????????????????????????????????employeeId="+data.getEmployId());
				}
			}
			
		}
		
	}
}
