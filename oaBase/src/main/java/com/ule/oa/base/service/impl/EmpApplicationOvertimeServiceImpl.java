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
 * @ClassName: 延时工作申请
 * @Description: 延时工作申请
 * @author yangjie
 * @date 2017年6月12日
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
		logger.info("延时工作申请save入参:ApplyDate="+userOvertime.getApplyDate()+";ApplyType="+userOvertime.getApplyType()+";ExpectStartTime="+userOvertime.getExpectStartTime()
				+";ExpectEndTime="+userOvertime.getExpectEndTime()+";Reason="+userOvertime.getReason()
				+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));

		if(userOvertime.getApplyDate()==null){
			throw new OaException("延时工作日期不能为空！");
		}
		//查询具有申请权限的部门
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
		
		//法定节假日
		boolean isAgent = true;
		//法定调休日
	    boolean isAgent1 = true;
		
	    //查询假期表
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
		
		//没有权限的部门只能提法定节假日申请
		if(!hasPrivilege&&isAgent){
			throw new OaException("无日常延时工作申请权限，可联系人事咨询！");
		}
		
		//18点前可以提前天及以后的加班，18点后只能提今天及以后的加班
		Config configCondition = new Config();
		configCondition.setCode("timeLimit1");
		List<Config> limit = configService.getListByCondition(configCondition);
		int num = Integer.valueOf(limit.get(0).getDisplayCode());
		
		String nowDate18 = DateUtils.getNow("yyyy-MM-dd") + " 18:00:00";
		if(DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_LONG), DateUtils.FORMAT_LONG).getTime()<=DateUtils.parse(nowDate18, DateUtils.FORMAT_LONG).getTime()){
			//18点前可以提前及以后的加班
			if(DateUtils.getIntervalDays(DateUtils.parse(DateUtils.getNow("yyyy-MM-dd"),DateUtils.FORMAT_SHORT),userOvertime.getApplyDate())<num-1){
				throw new OaException("18点前只能申请前一天及以后的延时工作！");
			}
		}else{
			//18点后只能提今天及以后的加班
			if(DateUtils.getIntervalDays(DateUtils.parse(DateUtils.getNow("yyyy-MM-dd"),DateUtils.FORMAT_SHORT),userOvertime.getApplyDate())<num){
				throw new OaException("18点后只能提今天及以后的延时工作！");
			}
		}
		if(userOvertime.getApplyType()==null){
			throw new OaException("申请事由不能为空");
		}
        if(userOvertime.getExpectStartTime()==null){
        	throw new OaException("预计开始时间不能为空！");
		}
        if(userOvertime.getExpectEndTime()==null){
        	throw new OaException("预计结束时间不能为空！");
		}
        if(StringUtils.isBlank(userOvertime.getReason())){
        	throw new OaException("申请事由不能为空！");
		}
		userOvertime.setEmployeeId(user.getEmployeeId());
		userOvertime.setApplyDate(userOvertime.getApplyDate());
		int count = getEaoByEmpAndDateCount(userOvertime);
		if(count>0){
			throw new OaException("当天已申请加班！");
		}
		userOvertime.setStartTime(DateUtils.getFirstDay(userOvertime.getApplyDate()));
		userOvertime.setEndTime(DateUtils.getLastDay(userOvertime.getApplyDate()));
		Double monthCount = getTotalWorkHours(userOvertime);
		if(monthCount!=null&&monthCount+userOvertime.getExpectDuration()>36){
			throw new OaException("当月加班申请不能超过36小时！");
		}
		
		String date = DateUtils.format(userOvertime.getApplyDate(), DateUtils.FORMAT_SHORT);
		
		//查询当年未完成调休小时数
		userOvertime.setStartTime(DateUtils.parse(date.substring(0, 4)+"-01-01",DateUtils.FORMAT_SHORT));
		userOvertime.setEndTime(DateUtils.parse(date.substring(0, 4)+"-12-31",DateUtils.FORMAT_SHORT));
		Double unCompleteHours = getUnCompleteHours(userOvertime);
		
		//查询当年调休剩余
		EmpLeave empLeave = new EmpLeave();
		empLeave.setEmployeeId(user.getEmployee().getId());
		empLeave.setYear(Integer.valueOf(date.substring(0, 4)));
		empLeave.setType(ConfigConstants.LEAVE_TYPE_5);
		empLeave.setIsActive(0);
		EmpLeave empLeaveY = empLeaveService.getByCondition(empLeave);
		if(empLeaveY!=null&&empLeaveY.getAllowRemainDays()!=null){
			
			if((empLeaveY.getAllowRemainDays()+userOvertime.getExpectDuration()+unCompleteHours)>80d){
				throw new OaException("当年加班申请累计不能超过80小时，请及时调休！");
			}
		}
		
		//查月的时候要加上parentId
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
					throw new OaException("当月加班申请不能超过36小时！");
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
		
		//法定假日前一天不能查过24点
		AnnualVacation legal = new AnnualVacation();
		legal.setType(AnnualVacation.YYPE_LEGAL);
		List<AnnualVacation> legalList = annualVacationService.getListByCondition(legal);
		Map<Date,Date> legalBeforeMap = new HashMap<Date,Date>();
		for(AnnualVacation va:legalList){
			legalBeforeMap.put(DateUtils.addDay(va.getAnnualDate(), -1), DateUtils.addDay(va.getAnnualDate(), -1));
		}
		if(legalBeforeMap!=null&&legalBeforeMap.containsKey(userOvertime.getApplyDate())){
			if(userOvertime.getExpectEndTime().getTime()>over24.getTime()){
				throw new OaException("法定节假日前一天加班时间不能超过当天24点！");
			}
		}
		
		boolean isduty = false;
		Date start = null;
		Date end = null;
		if(!isAgent){
			//判断有没有值班安排
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
		//法定节假日无值班数据
		if(!isAgent&&!isduty){
			throw new OaException("当日为法定节假日，需有值班安排！");
		}
		if(!isAgent&&isduty){
			if(!(userOvertime.getExpectStartTime().getTime()>=start.getTime()&&userOvertime.getExpectEndTime().getTime()<=end.getTime())){
				throw new OaException("法定节假日加班时间只能在排班时间段内！");
			}
			//法定假日不能超过当天24点
			if(userOvertime.getExpectEndTime().getTime()>over24.getTime()){
				throw new OaException("法定节假日加班时间不能超过当天24点！");
			}
		}
		
		boolean isduty1 = false;
		Date end1 = null;
		if(!isAgent1){
			//判断有没有值班安排
			EmployDuty dutyP = new EmployDuty();
			dutyP.setEmployId(userOvertime.getEmployeeId());
			dutyP.setDutyDate(userOvertime.getApplyDate());
			List<EmployDuty> dutyList = employDutyMapper.selectByCondition(dutyP);
		    if(dutyList!=null&&dutyList.size()>0){
		    	isduty1 = true;
				end1 = dutyList.get(0).getEndTime();
		    }
		}
		//法定节假日无值班数据
		if(!isAgent1&&!isduty1){
			throw new OaException("当日为法定调休日，需有值班安排！");
		}
		if(!isAgent1&&isduty1){
			if(userOvertime.getExpectStartTime().getTime()<DateUtils.addHour(end1, 1).getTime()){
				throw new OaException("法定调休日加班时间不能在排班时间内！");
			}
			//法定调休不能超过当天24点
			if(userOvertime.getExpectEndTime().getTime()>over24.getTime()){
				throw new OaException("法定调休日加班时间不能超过当天24点！");
			}
		}
		//XXX----------------------------开始流程------------------------------
		String processInstanceId = activitiServiceImpl.startByKey(ConfigConstants.OVERTIME_KEY);
		
		//-----------------start-----------------------保存流程节点信息
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
		
		//流程与业务数据关联
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
		//员工调休的汇总不存在
		Employee employee = employeeMapper.getById(old.getEmployeeId());
		//流水记录
		LeaveRecord leaveRecord = new LeaveRecord();
		leaveRecord.setEmployeeId(employee.getId());
		leaveRecord.setType(ConfigConstants.LEAVE_TYPE_5);
		leaveRecord.setDays(old.getActualDuration());
		leaveRecord.setBillId(old.getId());
		leaveRecord.setBillType(ConfigConstants.OVERTIME_KEY);
		leaveRecord.setDaysUnit(1);//单位
		leaveRecord.setCreateTime(new Date());
		leaveRecord.setCreateUser(user.getEmployee().getCnName());
		leaveRecord.setDelFlag(0);
		leaveRecord.setSource(0);
		leaveRecord.setRemark("延时工作申请");
		leaveRecordMapper.save(leaveRecord);
		if(empLeaveList!=null&&empLeaveList.getId()==null){
			//生成两条数据
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
		    //流水明细
			LeaveRecordDetail recordDetail = new LeaveRecordDetail();
			recordDetail.setLeaveRecordId(leaveRecord.getId());
			recordDetail.setBaseEmpLeaveId(month.getId());
			recordDetail.setDays(old.getActualDuration());
			recordDetail.setCreateTime(new Date());
			recordDetail.setCreateUser(user.getEmployee().getCnName());
			recordDetail.setDelFlag(0);
			leaveRecordDetailMapper.save(recordDetail);
		}else if(empLeaveList!=null&&empLeaveList.getId()!=null){
			//修改年的数据
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
			//存在，查看当月的明细是否存在
			EmpLeave currentM = new EmpLeave();
			currentM.setEmployeeId(old.getEmployeeId());
			currentM.setYear(Integer.valueOf(date.substring(5, 7)));
			currentM.setType(ConfigConstants.LEAVE_TYPE_5);
			currentM.setIsActive(0);
			currentM.setParendId(empLeaveList.getId());			
			EmpLeave currentMIsEx = empLeaveService.getByCondition(currentM);
			if(currentMIsEx!=null&&currentMIsEx.getId()!=null){
				//修改
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
				//流水明细
				LeaveRecordDetail recordDetail = new LeaveRecordDetail();
				recordDetail.setLeaveRecordId(leaveRecord.getId());
				recordDetail.setBaseEmpLeaveId(currentMIsEx.getId());
				recordDetail.setDays(old.getActualDuration());
				recordDetail.setCreateTime(new Date());
				recordDetail.setCreateUser(user.getEmployee().getCnName());
				recordDetail.setDelFlag(0);
				leaveRecordDetailMapper.save(recordDetail);
			}else{
				//新增
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
			    //流水明细
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
	
	//获取未完成的加班小时数
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
		
		Long dpId = null;//部门id
		for(Map<String,Object> map : listMap){
			dpId = (Long)map.get("depart_id");
			
			map.put("depart_name", employeeAppService.getDepartName(dpId));
		}
		
		return listMap;
	}
	
	/**
	  * getApplyOverTimeSumReport(月度加班统计报表导出)
	  * @Title: getApplyOverTimeSumReport
	  * @Description: 月度加班统计报表导出
	  * @param applyStartDate
	  * @param applyEndDate
	  * @param departId
	  * @return
	  * @throws Exception    设定文件
	  * List<Map<String,Object>>    返回类型
	  * @throws
	 */
	public List<Map<String,Object>> getApplyOverTimeSumReport(Date applyStartDate,Date applyEndDate,Long departId) throws Exception{
		EmpApplicationOvertime userOvertime = new EmpApplicationOvertime();
		userOvertime.setApplyStartDate(applyStartDate);
		userOvertime.setApplyEndDate(applyEndDate);
		userOvertime.setDepartId(departId);
		userOvertime.setApprovalStatus(ConfigConstants.PASS_STATUS);
		
		//1.抓取未计算之前的加班数据
		List<Map<String,Object>> reportList = empApplicationOvertimeMapper.getApplyOverTimeSumReport(userOvertime);
		
		//2.第一轮计算，生成月度加班明细表
		for(Map<String,Object> reportMap : reportList){
			getApplyOverTime(reportMap);
		}
		
		//3.第二轮计算,生成月度加班统计表
		Long preEmpId = null;//上一条记录的员工id
		Long preMonth = null;//上一条记录的月份
		Map<String,Object> map = null;
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		Long dpId = null;//部门id
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
				
				//部门显示修改为一级部门+当前部门
				dpId = (Long)map.get("depart_id");
				map.put("depart_name", employeeAppService.getDepartName(dpId));
				
				//封装剩余调休小时数
				map.put("remainRest", getRemainRest(applyStartDate, (Long)reportMap.get("month"),(Long)reportMap.get("employee_id")));
				//封装总调休小时数
				map.put("canUseTime", getRemainRest(applyStartDate, null,(Long)reportMap.get("employee_id")));
				
				list.add(map);
			}
			
			preEmpId = (Long)reportMap.get("employee_id");
			preMonth = (Long)reportMap.get("month");
		}
		
		return list;
	}
	
	public Double getRemainRest(Date applyStartDate,Long month,Long employeeId){
		//封装剩余调休小时数
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
				//延时工作日期
				Date applyDate = (Date)reportMap.get("apply_date");
				//判断当前申请时间是1.5倍，还是2倍，还是3倍
				Integer dayType = annualVacationService.getDayType(applyDate);
				Integer maxOverTime = 8;//双休和法定节假日超过8小时补助一次[和人事确认过加班结束时间-加班开始时间>=9则补助一次，确认时间：2017-12-26 14:50:00,分机:4022，改动点已经通知过PM]
				Date compareDate_21 = DateUtils.parse(DateUtils.format(applyDate,DateUtils.FORMAT_SHORT) + " 21:00:00");
				Date compareDate_23 = DateUtils.parse(DateUtils.format(applyDate,DateUtils.FORMAT_SHORT) + " 23:00:00");
				Date actualEndTime = (Date)reportMap.get("actual_end_time");//实际加班结束时间
				Double actualDuration = (Double)reportMap.get("actual_duration");
				
				//计算1.5/2/3倍加班工资
				reportMap.put("time1", 0.00);
				reportMap.put("time2", 0.00);
				reportMap.put("time3", 0.00);
				reportMap.put("meals", 0);
				reportMap.put("trafficMeals1", 0);
				reportMap.put("trafficMeals2", 0);
				if(dayType.intValue() == 1){//工作日加班
					reportMap.put("time1", actualDuration);
					
					//根据排班的结束时间往后推3小时算一次餐费
					/*Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("employeeId", reportMap.get("employee_id") + "");
					paramMap.put("classDate", DateUtils.format(applyDate,DateUtils.FORMAT_SHORT));
					String OA_URL = configCacheManager.getConfigDisplayCode("MO_WEB_URL");*/
					String response = employeeClassService.getEmployeeClassJsonData((reportMap.get("employee_id") + ""), DateUtils.format(applyDate,DateUtils.FORMAT_SHORT));
					logger.info("本次请求返回结果：{}",response);
					
					Map<String, String> returnMap = JSONUtils.read(response, Map.class);
					Date compareDate_meals = DateUtils.addHour(DateUtils.parse(returnMap.get("endTime")),3);//排班时间（默认：9点到18点）
					logger.info("员工"+reportMap.get("cn_name")+"安排班次{}-{}["+returnMap.get("className")+"]",returnMap.get("startTime"),returnMap.get("endTime"));
					
					if(actualEndTime.compareTo(compareDate_meals) >= 0){
						reportMap.put("meals", 1);
					}
				}else if(dayType.intValue() == 2){//周末加班
					reportMap.put("time2", actualDuration);
					
					if(actualDuration >= maxOverTime){
						reportMap.put("meals", 1);
					}
				}else if(dayType.intValue() == 3){//法定节假日加班
					reportMap.put("time3", actualDuration);
					
					if(actualDuration >= maxOverTime){
						reportMap.put("meals", 1);
					}
				}
				
				//计算晚间交通费次数1(21：00-22:59)
				if(actualEndTime.compareTo(compareDate_21) >= 0 && actualEndTime.compareTo(compareDate_23) < 0){
					reportMap.put("trafficMeals1", 1);
				}else if(actualEndTime.compareTo(compareDate_23) >= 0){//计算晚间交通费次数2(23点之后)
					reportMap.put("trafficMeals2", 1);
				}
			}catch(Exception e){
			logger.error("查询加班出错：{}",e.toString());
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
			List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(currentUser.getEmployeeId());//下属员工
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
			List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(currentUser.getEmployeeId());//下属员工
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
					//封装数据
					Map<String,Object> sdoMap = new HashMap<String, Object>();
					sdoMap.put("code",re.getCode());
					sdoMap.put("cnName",re.getCnName());
					sdoMap.put("departName",re.getDepartName());
					sdoMap.put("positionName",re.getPositionName());
					sdoMap.put("applyDate",DateUtils.format(re.getApplyDate(), DateUtils.FORMAT_SHORT));
					sdoMap.put("dayofweek",re.getDayofweek());
					String applyType = "";
					if(re.getApplyType().intValue()==100){
						applyType = "项目";
					}else if(re.getApplyType().intValue()==200){
						applyType = "会议";
					}else if(re.getApplyType().intValue()==300){
						applyType = "日常工作";
					}else if(re.getApplyType().intValue()==400){
						applyType = "其他";
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
						approvalStatus = "待审批";
					}else if(re.getApprovalStatus().intValue()==200){
						approvalStatus = "已审批";
					}else if(re.getApprovalStatus().intValue()==300){
						approvalStatus = "已拒绝";
					}else if(re.getApprovalStatus().intValue()==400){
						approvalStatus = "已撤销";
					}
					sdoMap.put("approvalStatus",approvalStatus);
					sdoMap.put("reason",re.getReason());
					sMapList.add(sdoMap);
				}
			}
			String[] keys={"code", "cnName", "departName", "positionName","applyDate","dayofweek", "applyType", "expectStartTime", "expectEndTime", "expectDuration", "actualStartTime", "actualEndTime", "actualDuration", "auditUser","submitDate","approvalStatus","reason"};
			String[] titles={"员工编号", "申请人", "部门","职位","加班日期", "星期", "加班事由", "预计加班开始时间", "预计加班结束时间", "预计加班小时数", "实际加班开始时间", "实际加班结束时间", "实际加班小时数", "批核主管","加班申请日期","状态","事由说明"}; 
			return ExcelUtil.exportExcel(sMapList, keys, titles, "加班查询报表.xls");
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
		//查询登录用户数据权限
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
			List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(currentUser.getEmployeeId());//下属员工			
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
			//数据权限
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
				//1.抓取未计算之前的加班数据
				
				reportList = empApplicationOvertimeMapper.getApplyOverTimeSumReportByPage(userOvertime);
			}
			
			//2.第一轮计算，生成月度加班明细表
			for(Map<String,Object> reportMap : reportList){
				getApplyOverTime(reportMap);
			}
			
			//3.第二轮计算,生成月度加班统计表
			Long preEmpId = null;//上一条记录的员工id
			String preMonth = null;//上一条记录的月份
			Map<String,Object> map = null;
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			Long dpId = null;//部门id
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
					
					//部门显示修改为一级部门+当前部门
					dpId = (Long)map.get("depart_id");
					map.put("depart_name", employeeAppService.getDepartName(dpId));
					//封装剩余调休小时数
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
		//校验数据权限
		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
		if(deptDataByUserList==null || deptDataByUserList.size()==0){
			return null;
		}else{
			List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(currentUser.getEmployeeId());//下属员工
			
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
			//数据权限
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
			//1.抓取未计算之前的加班数据
			List<Map<String,Object>> reportList = empApplicationOvertimeMapper.getApplyOverTimeSumReportByPage(userOvertime);
			
			//2.第一轮计算，生成月度加班明细表
			for(Map<String,Object> reportMap : reportList){
				getApplyOverTime(reportMap);
			}
			
			//3.第二轮计算,生成月度加班统计表
			Long preEmpId = null;//上一条记录的员工id
			String preMonth = null;//上一条记录的月份
			Map<String,Object> map = null;
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			Long dpId = null;//部门id
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
					
					//部门显示修改为一级部门+当前部门
					dpId = (Long)map.get("depart_id");
					map.put("depart_name", employeeAppService.getDepartName(dpId));
					//封装剩余调休小时数
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
			String[] titles={"员工编号", "申请人", "部门","职位","年月", "1.5倍小时数", "2倍小时数", "3倍小时数", "加班总计小时数","剩余调休小时数", "晚间餐费次数", "晚间交通费次数（21:00-22:59）", "晚间交通费次数（23点以后）"}; 
			return ExcelUtil.exportExcel(list0, keys, titles, "月度加班汇总.xls");
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
        	taskVO.setProcessName("延时工作申请");
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
				taskVO.setView4(DateUtils.format(overtime.getActualStartTime(), ConfigConstants.DATEFORMAT)+"~"+DateUtils.format(overtime.getActualEndTime(), ConfigConstants.DATEFORMAT) + "&nbsp;&nbsp;" + overtime.getActualDuration() + "小时");
			} else {
				taskVO.setView4(DateUtils.format(overtime.getExpectStartTime(), ConfigConstants.DATEFORMAT)+"~"+DateUtils.format(overtime.getExpectEndTime(), ConfigConstants.DATEFORMAT) + "&nbsp;&nbsp;" + overtime.getExpectDuration() + "小时");
			}
			taskVO.setView5(overtime.getReason());
        }
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void endHandle(String processInstanceId, User user) throws Exception{
		EmpApplicationOvertime old = empApplicationOvertimeMapper.queryByProcessInstanceId(processInstanceId);
		//法定节假日不算调休
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
		
		logger.info("延时工作申请completeTask:start。。");
		Long time1 = System.currentTimeMillis();
	
		User user = userService.getCurrentUser();
		logger.info("延时工作申请completeTask入参:processId="+processId+";comment="+comment+";commentType="+commentType
			+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));

		Task task = activitiServiceImpl.queryTaskByProcessInstanceId(processId);
		EmpApplicationOvertime overtime = empApplicationOvertimeMapper.queryByProcessInstanceId(processId);
		if(task==null){
			throw new OaException("实例Id为"+processId+"的流程不存在！");
		}else if(!StringUtils.equalsIgnoreCase(task.getAssignee(), user.getEmployeeId().toString())
				&&!StringUtils.equalsIgnoreCase(overtime.getEmployeeId().toString(), user.getEmployeeId().toString())){
			task = activitiServiceImpl.queryTaskByTaskIdAndCandidate(task.getId(), user.getEmployeeId().toString());
			if(task == null) {
				throw new OaException("该条申请单没有权限操作,请确认后再操作。");
			}
		}
		boolean type = StringUtils.startsWithIgnoreCase(task.getTaskDefinitionKey(), "personnel");
		if(overtime!=null){
			if(annualVacationService.check5WorkingDayNextmonth(DateUtils.format(overtime.getApplyDate(), DateUtils.FORMAT_SHORT),type)){
				logger.error(userService.getCurrentUser().getEmployee().getCnName()+"审批员工Id="+overtime.getEmployeeId()+"的加班申请:已超出有效时间,无法操作！");
				throw new OaException("已超出有效时间,无法操作！");
			}
			if(param!=null){
				overtime.setActualStartTime(param.getActualStartTime());
				overtime.setActualEndTime(param.getActualEndTime());
				
				//法定节假日
				boolean isAgent = true;
				//法定调休日
			    boolean isAgent1 = true;
			    //查询假期表
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
					//判断有没有值班安排
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
				//法定节假日无值班数据
				if(!isAgent&&!isduty){
					throw new OaException("当日为法定节假日，需有值班安排！");
				}
				if(!isAgent&&isduty){
					if(!(overtime.getActualStartTime().getTime()>=start.getTime()&&overtime.getActualEndTime().getTime()<=end.getTime())){
						throw new OaException("法定节假日加班时间只能在排班时间段内！");
					}
					//法定假日不能超过当天24点
					if(overtime.getActualEndTime().getTime()>over24.getTime()){
						throw new OaException("法定节假日加班时间不能超过当天24点！");
					}
				}
				
				boolean isduty1 = false;
				Date end1 = null;
				if(!isAgent1){
					//判断有没有值班安排
					EmployDuty dutyP = new EmployDuty();
					dutyP.setEmployId(overtime.getEmployeeId());
					dutyP.setDutyDate(overtime.getApplyDate());
					List<EmployDuty> dutyList = employDutyMapper.selectByCondition(dutyP);
				    if(dutyList!=null&&dutyList.size()>0){
				    	isduty1 = true;
						end1 = dutyList.get(0).getEndTime();
				    }
				}
				//法定节假日无值班数据
				if(!isAgent1&&!isduty1){
					throw new OaException("当日为法定调休日，需有值班安排！");
				}
				if(!isAgent1&&isduty1){
					if(overtime.getActualStartTime().getTime()<DateUtils.addHour(end1, 1).getTime()){
						throw new OaException("法定调休日加班时间不能在排班时间内！");
					}
					//法定调休不能超过当天24点
					if(overtime.getActualEndTime().getTime()>over24.getTime()){
						throw new OaException("法定调休日加班时间不能超过当天24点！");
					}
				}
				
				Map<String,Object> result = getAcTualTime(overtime);
				if(param.getActualStartTime().getTime()>param.getActualEndTime().getTime()){
					param.setActualEndTime(DateUtils.addDay(param.getActualEndTime(), 1));
				}
				if(!"".equals(result.get("actualEndTime1"))){
					if(DateUtils.compareDate(DateUtils.parse(result.get("actualEndTime1").toString(), DateUtils.FORMAT_LONG), param.getActualEndTime())==2){
						throw new OaException("不能晚于考勤时间！");
					}
				}else{
					throw new OaException("加班打卡记录缺失,不能提交！");
				}
				if(param.getActualDuration()>Double.valueOf(String.valueOf(result.get("duration")))){
					throw new OaException("实际工作时长与加班时间不符！");
				}
				if("1".equals(result.get("flag"))){
					throw new OaException("该时间段未安排排班！");
				}
				String date = DateUtils.format(overtime.getApplyDate(), DateUtils.FORMAT_SHORT);
				
				//查询当年未完成调休小时数
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
						throw new OaException("当年加班申请累计不能超过80小时，请及时调休！");
					}
				}
				empLeave.setYear(Integer.valueOf(date.substring(5, 7)));
				//查月的时候要加上parentId
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
							throw new OaException("当月加班申请不能超过36小时！");
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
					throw new OaException("实际小时数不能大于预计小时数！");
				}
			}
			empApplicationOvertimeMapper.updateById(update);
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
			//判断当前节点是否有办理人assignee,没有则将当前代理人设为办理人
			if(StringUtils.isBlank(task.getAssignee())) {
				activitiServiceImpl.claimTask(task.getId(), String.valueOf(user.getEmployeeId()));
			}
			activitiServiceImpl.completeTask(task.getId(),comment,null,commentType);
		}else{
			throw new OaException("流程实例为"+processId+"的加班审批数据不存在！");
		}
		
		Long time2 = System.currentTimeMillis();
		logger.info("延时工作申请completeTask:use time="+(time2-time1));
		logger.info("延时工作申请completeTask:end。。");
		
	}
	
	
	public Map<String, Object> getAcTualTime(EmpApplicationOvertime ovrertime) {
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
			actuaStartTime = DateUtils.format(DateUtils.addHour(empClass.getEndTime(), 1), "HH:mm");                    
			//XXX:规则更改：无论排班或是正常班,打卡时间匹配规则:只找加班人员正常下班12小时内的打卡时间.
			//只查询下班后1-12小时内的打卡，取最晚的那条打卡
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
			
			actuaStartTime = DateUtils.format(DateUtils.addHour(dutyList.get(0).getEndTime(), 1), "HH:mm"); 
			
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

	@Override
	public void updateProcessInstanceId(EmpApplicationOvertime newOvertime) {
			empApplicationOvertimeMapper.updateById(newOvertime);
	}

	@Override
	public void setValueToVO1(TaskVO taskVO, String processInstanceId,
			String key) {
		EmpApplicationOvertime overtime = empApplicationOvertimeMapper.queryByProcessInstanceId(processInstanceId);
        if(overtime!=null){
        	taskVO.setProcessName("延时工作申请");
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
				taskVO.setView4(DateUtils.format(overtime.getActualStartTime(), ConfigConstants.DATEFORMAT)+"~"+DateUtils.format(overtime.getActualEndTime(), ConfigConstants.DATEFORMAT) + "&nbsp;&nbsp;" + overtime.getActualDuration() + "小时");
			} else {
				taskVO.setView4(DateUtils.format(overtime.getExpectStartTime(), ConfigConstants.DATEFORMAT)+"~"+DateUtils.format(overtime.getExpectEndTime(), ConfigConstants.DATEFORMAT) + "&nbsp;&nbsp;" + overtime.getExpectDuration() + "小时");
			}
			taskVO.setView5(overtime.getReason());
        }
	}

	@Override
	public void backProcess(String processInstanceId, String taskDefKey)
			throws Exception {
		EmpApplicationBusiness overtime = empApplicationBusinessMapper.queryByProcessId(processInstanceId);
		if(overtime!=null){
			//查询flowable流程历史记录
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
			empovertimeLeave.setType(13);//调休管理数据
			
			empovertimeLeave.setOffset(pm.getOffset());
			empovertimeLeave.setLimit(pm.getLimit());
			
			List<Long> jobStatusList=new ArrayList<Long>();
			jobStatusList.add(0L);
			jobStatusList.add(2L);
			empovertimeLeave.setJobStatusList(jobStatusList);
			
			//查询指定员工类型数据
			empovertimeLeave.setEmpTypeIdList(configService.getNeedEmpTypeIdList());

			Integer total = empLeaveMapper.getEmpPageCount(empovertimeLeave);
			pm.setTotal(total);
			List<EmpLeave> getEmpPageList = empLeaveMapper.getEmpPageList(empovertimeLeave);//查询全部员工
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
		
		
		EmpLeave empLeave=new EmpLeave(); //通过id查询下面的子 加班
		empLeave.setYear(year);
		empLeave.setEmployeeId(empId);
		empLeave.setType(13);
		List<Long> employeeIdList = new ArrayList<Long>();
		employeeIdList.add(empId);
		
		List<EmpLeave> pageList = empLeaveMapper.getPageList(empLeave);
		
		List<EmpLeave> overtimeSumUsedDay=empLeaveMapper.getSumOvertimeDay(year,employeeIdList);//汇总统计集合
		if(overtimeSumUsedDay!=null && overtimeSumUsedDay.size()>0){   //修改已存在的假期
			double remainDays = overtimeSumUsedDay.get(0).getAllowRemainDays()!=null?overtimeSumUsedDay.get(0).getAllowRemainDays():0;
			double blockedDays = overtimeSumUsedDay.get(0).getBlockedDays()!=null?overtimeSumUsedDay.get(0).getBlockedDays():0;
			overtimeSumUsedDay.get(0).setAllowRemainDays(remainDays+blockedDays);
			result.put("overtime", overtimeSumUsedDay.get(0));
		}else{                                       //修改未存在 没有 其他调休的 员工
			List<EmpLeave> getEmpPageList = empLeaveMapper.getEmpPageList(empLeave);//查询全部员工
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
			result.put("message", "请填写调休小时数！");
			result.put("success", false);
			return result;
		}
		if(empovertimeLeave.getStartTimeFormat()==null || empovertimeLeave.getEndTimeFormat()==null){
			result.put("message", "请填写调休日期！");
			result.put("success", false);
			return result;
		}
		if(empovertimeLeave.getAllowRemainDays()%0.5!=0){
			result.put("message", "调休小时数不正确，请填写0.5的倍数！");
			result.put("success", false);
			return result;
		}
		
		//把开始结束时间处理转换
		String WorkDateFormatStart  = DateUtils.format(empovertimeLeave.getStartTimeFormat(),DateUtils.FORMAT_SHORT);
		String WorkDateFormatEnd  = DateUtils.format(empovertimeLeave.getEndTimeFormat(),DateUtils.FORMAT_SHORT);
		empovertimeLeave.setStartTime(DateUtils.parse(WorkDateFormatStart + " 00:00:00"));
		empovertimeLeave.setEndTime(DateUtils.parse(WorkDateFormatEnd + " 23:59:59"));
		//按结束时间转换年
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
			//新增流水
			//流水
			LeaveRecord record = new LeaveRecord();
			record.setEmployeeId(empovertimeLeave.getEmployeeId());
			record.setType(empovertimeLeave.getType());
			if(empovertimeLeave.getType().intValue()==ConfigConstants.LEAVE_TYPE_13.intValue()){
				record.setDays(empovertimeLeave.getAllowRemainDays());
				record.setDaysUnit(1);//单位
			}
			record.setBillType(ConfigConstants.LEAVE_KEY);
			//overtime
			record.setUpdateType(2);//类型为：剩余
			record.setCreateTime(new Date());
			record.setCreateUser(currentUser.getEmployee().getCnName());
			record.setDelFlag(0);
			record.setSource(1);//hr修改
			record.setRemark(empovertimeLeave.getRemark());
			leaveRecordMapper.save(record);
			//新增流水详情
			this.saveRecordDetail(record.getId(), empovertimeLeave.getId(), empovertimeLeave.getAllowRemainDays(), currentUser);
		
		result.put("message", "新增成功！");
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
			result.put("message", "请填写调休小时数！");
			result.put("success", false);
			return result;
		}
		if(empovertimeLeave.getStartTimeFormat()==null || empovertimeLeave.getEndTimeFormat()==null){
			result.put("message", "请填写调休日期！");
			result.put("success", false);
			return result;
		}
		if(empovertimeLeave.getAllowRemainDays()!=0&&empovertimeLeave.getAllowRemainDays()%0.5!=0){
			result.put("message", "调休小时数不正确，请填写0.5的倍数！");
			result.put("success", false);
			return result;
		}
		
		//把开始结束时间处理转换
		String WorkDateFormatStart  = DateUtils.format(empovertimeLeave.getStartTimeFormat(),DateUtils.FORMAT_SHORT);
		String WorkDateFormatEnd  = DateUtils.format(empovertimeLeave.getEndTimeFormat(),DateUtils.FORMAT_SHORT);
		empovertimeLeave.setStartTime(DateUtils.parse(WorkDateFormatStart + " 00:00:00"));
		empovertimeLeave.setEndTime(DateUtils.parse(WorkDateFormatEnd + " 23:59:59"));
		//按结束时间转换年
		empovertimeLeave.setYear(Integer.parseInt(DateUtils.getYear(empovertimeLeave.getEndTime())));
		

		EmpLeave empLeave=new EmpLeave();
		empLeave.setYear(empovertimeLeave.getYear());
		empLeave.setEmployeeId(empovertimeLeave.getEmployeeId());
		empLeave.setType(13);
		
		//取之前的数据
		EmpLeave beforLeave = empLeaveMapper.getById(empovertimeLeave.getId()); //修改前的数据		
		if(beforLeave.getBlockedDays()!=0){//有锁定假期 不能修改假期时间
			empovertimeLeave.setStartTime(beforLeave.getStartTime());
			empovertimeLeave.setEndTime(beforLeave.getEndTime());
		}
		empovertimeLeave.setAllowRemainDays(empovertimeLeave.getAllowRemainDays());
		empovertimeLeave.setUpdateTime(new Date());
		empovertimeLeave.setUpdateUser(currentUser.getUserName());
		empovertimeLeave.setVersion(beforLeave.getVersion());
		empovertimeLeave.setType(13);
		empLeaveMapper.updateById(empovertimeLeave);
		//新增流水
		//流水
		if(empovertimeLeave.getAllowRemainDays()-beforLeave.getAllowRemainDays()!=0){
			LeaveRecord record = new LeaveRecord();
			record.setEmployeeId(empovertimeLeave.getEmployeeId());
			record.setType(empovertimeLeave.getType());
			if(empovertimeLeave.getType().intValue()==ConfigConstants.LEAVE_TYPE_13.intValue()){
				record.setDays(empovertimeLeave.getAllowRemainDays()-beforLeave.getAllowRemainDays());
				record.setDaysUnit(1);//单位
			}
			record.setBillType(ConfigConstants.LEAVE_KEY);
			record.setCreateTime(new Date());
			record.setCreateUser(currentUser.getEmployee().getCnName());
			record.setUpdateType(2);//类型为：剩余
			record.setDelFlag(0);
			record.setSource(1);//hr修改
			record.setRemark(empovertimeLeave.getRemark());
			leaveRecordMapper.save(record);
			//新增流水详情
			this.saveRecordDetail(record.getId(), empovertimeLeave.getId(), (empovertimeLeave.getAllowRemainDays()-beforLeave.getAllowRemainDays()), currentUser);
		}
		result.put("success", true);	
		result.put("message", "修改成功!");
		return result;
	}

	@Override
	public Map<String, Object> queryInfoById(long id) {
		// TODO Auto-generated method stub
		Map<String, Object> result=new HashMap<String, Object>();
		if(id==0){
			result.put("message", "查询失败，为空值");
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
			result.put("message", "查询失败，为空值");
			result.put("success", false);
		}
		return result;
	}

	@Override
	public void remindWriteOvertimeApply() {
		
		//判断当天是否是法定节假日
		AnnualVacation vacation = new AnnualVacation();
		vacation.setAnnualDate(new Date());
		vacation.setDateType(AnnualVacation.YYPE_LEGAL);
		List<AnnualVacation> vacationList = annualVacationService.getListByCondition(vacation);
		if(vacationList!=null&&vacationList.size()>0){
			//查询当天值班表所有员工
			EmployeeDuty duty = new EmployeeDuty();
			duty.setDutyDate(vacationList.get(0).getAnnualDate());
			List<EmployeeDuty> dutyList = employeeDutyService.selectByCondition(duty);
			
			//发送邮件提醒
			for(EmployeeDuty data:dutyList){
				try{
					Employee emp = employeeMapper.getById(data.getEmployId());
					//发送异常考勤邮件
					String overtimeDate = DateUtils.format(data.getDutyDate(), DateUtils.FORMAT_SHORT);
					String applyTimeLast = DateUtils.format(DateUtils.addDay(data.getDutyDate(), 1), DateUtils.FORMAT_SHORT);
					String content = "Dear " + emp.getCnName() +":<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;您在"+overtimeDate+"的法定节假日有值班安排，请在"+applyTimeLast+"&nbsp;18点前提交预计延时工作申请，逾期将无法申报！并在次月3个工作日内完成延时工作申请全部流程。如未实际值班请忽略此邮件。";
					SendMailUtil.sendNormalMail(content, emp.getEmail(), emp.getCnName(),"法定节假日延时工作申请提醒");
				}catch(Exception e){
					logger.info("延时工作登记申请提醒发送失败：employeeId="+data.getEmployId());
				}
			}
			
		}
		
	}
}
