package com.ule.oa.base.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import com.alibaba.druid.support.json.JSONUtils;
import com.ule.oa.base.common.ResponseDTO;
import com.ule.oa.base.mapper.CompanyConfigMapper;
import com.ule.oa.base.mapper.EmpLeaveMapper;
import com.ule.oa.base.mapper.LeaveApplyRegisterMapper;
import com.ule.oa.base.mapper.LeaveRecordDetailMapper;
import com.ule.oa.base.mapper.LeaveRecordMapper;
import com.ule.oa.base.po.CompanyConfig;
import com.ule.oa.base.po.CompanyPositionLevel;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.EmpApplicationLeave;
import com.ule.oa.base.po.EmpDepart;
import com.ule.oa.base.po.EmpLeave;
import com.ule.oa.base.po.EmpPosition;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.po.LeaveApplyRegister;
import com.ule.oa.base.po.LeaveRecord;
import com.ule.oa.base.po.LeaveRecordDetail;
import com.ule.oa.base.po.Position;
import com.ule.oa.base.po.RunTask;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmpApplicationLeaveService;
import com.ule.oa.base.service.EmpDepartService;
import com.ule.oa.base.service.EmpLeaveService;
import com.ule.oa.base.service.EmpPositionService;
import com.ule.oa.base.service.EmployeeClassService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.LeaveApplyRegisterService;
import com.ule.oa.base.service.PositionService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.util.AttnStatisticsUtil;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.PageModel;

@Service
public class LeaveApplyRegisterServiceImpl implements LeaveApplyRegisterService {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private UserService userService;
	@Autowired
	private LeaveApplyRegisterMapper leaveApplyRegisterMapper;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private EmpDepartService empDepartService;
	@Autowired
	private DepartService departService;
	@Autowired
	private EmpPositionService empPositionService;
	@Autowired
	private PositionService positionService;
	@Autowired
	private CompanyConfigMapper companyConfigMapper;
	@Autowired
	private LeaveRecordMapper leaveRecordMapper;
	@Autowired
	private EmpLeaveMapper empLeaveMapper;
	@Autowired
	private LeaveRecordDetailMapper leaveRecordDetailMapper;
	@Autowired
	private EmpLeaveService empLeaveService;
	@Autowired
	private EmployeeClassService employeeClassService;
	@Autowired
	private EmpApplicationLeaveService empApplicationLeaveService;
	@Autowired
	private ConfigService configService;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ResponseDTO registerLeave(LeaveApplyRegister data,BindingResult result) throws Exception{
		
		
		
		//???????????????????????????
		User user = userService.getCurrentUser();
		if(user == null || user.getEmployee() == null) {
			throw new OaException("??????????????????");
		}
		
		logger.info("registerLeave:employeeId="+data.getEmployeeId()+";leaveType="+data.getLeaveType()+";startTime="+data.getStartTime()
		    +";endTime="+data.getEndTime()+";leaveDaye="+data.getLeaveDays()+";leaveHours="+data.getLeaveHours()+";childrenNum="+data.getChildrenNum()
		    +";dayType="+data.getDayType()+";production="+data.getLivingState()+";childrenBirthday="+data.getChildrenBirthday()+";relatives="+data.getRelatives()
		    +";reason="+data.getReason()+";optuser="+user.getEmployee().getCnName());
		
		//????????????????????????
		if(result.hasErrors()) {
			 List<ObjectError> errorList = result.getAllErrors();
			 String message = "";
			 for(ObjectError error : errorList){
				 message += error.getDefaultMessage();
	         }
			 throw new OaException(message);
		}
		
		Employee employee = employeeService.getById(data.getEmployeeId());
		if(employee == null) {
			throw new OaException("??????????????????????????????");
		}
		
		//??????????????????
		Date lastRegisterTime = DateUtils.addMonth(DateUtils.getFirstDay(new Date()), -1);
        if(data.getStartTime().getTime()<lastRegisterTime.getTime()) {
        	throw new OaException("???????????????????????????????????????");
        }
        data.setDelFlag(0);
        data.setCreateTime(new Date());
        data.setCreateUser(user.getEmployee().getCnName());
		data.setCode(employee.getCode());
		data.setCnName(employee.getCnName());
		//????????????
		CompanyConfig typeOfWork = companyConfigMapper.getById(employee.getWorkType());
		data.setTypeOfWork(typeOfWork!=null?typeOfWork.getDisplayName():"");
		
		//??????????????????????????????????????????
		EmpDepart ed  = new EmpDepart();
 		ed.setEmployeeId(data.getEmployeeId());
 		EmpDepart empDepart = empDepartService.getByCondition(ed);
		if(null != empDepart && null != empDepart.getDepartId()){
			Depart depart = departService.getById(empDepart.getDepartId());
			data.setDepartName(depart!=null?depart.getName():"");
		}
		EmpPosition ep = new EmpPosition();
 		ep.setEmployeeId(data.getEmployeeId());
 		EmpPosition empPosition = empPositionService.getByCondition(ep);
 		if(null != empPosition && null != empPosition.getPositionId()){
 			Position position = positionService.getById(empPosition.getPositionId());
 			data.setPositionName(position!=null?position.getPositionName():"");
 		}
		
 		AttnStatisticsUtil attnStatisticsUtil= new AttnStatisticsUtil();
		//??????????????????????????????????????????
		if(ConfigConstants.LEAVE_TYPE_1.equals(data.getLeaveType())) {
			//??????
		    //?????????????????????
	        boolean overDrawFlag = configService.checkYearLeaveCanOverDraw();
            if(!overDrawFlag) {
                Double actualDays = empLeaveService.getActualYearDays(data.getEndTime(), data.getEmployeeId());
                if(actualDays<data.getLeaveDays().doubleValue()){
                    throw new OaException("??????????????????????????????????????????????????????");
                }
            }
			leaveApplyRegisterMapper.save(data);//????????????
			reduceYearLeave(employee,data,user);
			//??????????????????
			attnStatisticsUtil.calWorkHours(user.getEmployee().getCnName(),employee,data.getStartTime(),data.getEndTime(),RunTask.RUN_CODE_60,String.valueOf(data.getLeaveType()),data.getLeaveDays().doubleValue(),0,data.getReason(),data.getId());
		}else if(ConfigConstants.LEAVE_TYPE_2.equals(data.getLeaveType())) {
			//??????
			leaveApplyRegisterMapper.save(data);//????????????
			List<Map<String, Object>> planLeaves = new ArrayList<Map<String, Object>>();
			Map<String, Object> planLeave = new HashMap<String,Object>();
			planLeave.put("companyId", employee.getCompanyId());
			planLeave.put("employeeId", employee.getId());
			planLeave.put("type", data.getLeaveType());
			planLeave.put("planDays", data.getLeaveDays());
			planLeave.put("planStartTime",data.getStartTime());
			planLeave.put("planEndTime",data.getEndTime());
			planLeave.put("applyStatus", true);
			planLeave.put("optUser", user.getEmployee().getCnName());
			planLeave.put("empapplicationleaveId", data.getId());
			planLeave.put("remark", data.getReason());
			planLeaves.add(planLeave);
		    empLeaveService.updateSickEmpLeaveByHrRegister(planLeaves);
			attnStatisticsUtil.calWorkHours(user.getEmployee().getCnName(),employee, data.getStartTime(), data.getEndTime(),RunTask.RUN_CODE_60, String.valueOf(data.getLeaveType()),data.getLeaveDays().doubleValue(),0,data.getReason(),data.getId());
		}else if(ConfigConstants.LEAVE_TYPE_3.equals(data.getLeaveType())) {
			//??????
			leaveApplyRegisterMapper.save(data);//????????????
			reduceNormalLeave(employee,data,user);
			attnStatisticsUtil.calWorkHours(user.getEmployee().getCnName(),employee, data.getStartTime(), data.getEndTime(),RunTask.RUN_CODE_60, String.valueOf(data.getLeaveType()),data.getLeaveDays().doubleValue(),0,data.getReason(),data.getId());
		}else if(ConfigConstants.LEAVE_TYPE_4.equals(data.getLeaveType())) {
			//?????????
			leaveApplyRegisterMapper.save(data);//????????????
			reduceNormalLeave(employee,data,user);
			attnStatisticsUtil.calWorkHours(user.getEmployee().getCnName(),employee, data.getStartTime(), data.getEndTime(),RunTask.RUN_CODE_60, String.valueOf(data.getLeaveType()),Double.valueOf(data.getChildrenNum()),data.getDayType(),data.getReason(),data.getId());
		}else if(ConfigConstants.LEAVE_TYPE_5.equals(data.getLeaveType())) {
			//??????
			leaveApplyRegisterMapper.save(data);//????????????
			reduceRestLeave(employee,data,user);
			attnStatisticsUtil.calWorkHours(user.getEmployee().getCnName(),employee, data.getStartTime(), data.getEndTime(),RunTask.RUN_CODE_60, String.valueOf(data.getLeaveType()),data.getLeaveDays().doubleValue(),0,data.getReason(),data.getId());
		}else if(ConfigConstants.LEAVE_TYPE_6.equals(data.getLeaveType())) {
			//?????????
			leaveApplyRegisterMapper.save(data);//????????????
			reduceNormalLeave(employee,data,user);
			attnStatisticsUtil.calWorkHours(user.getEmployee().getCnName(),employee, data.getStartTime(), data.getEndTime(),RunTask.RUN_CODE_60, String.valueOf(data.getLeaveType()),data.getLeaveDays().doubleValue(),0,data.getReason(),data.getId());
		}else if(ConfigConstants.LEAVE_TYPE_7.equals(data.getLeaveType())) {
			//??????
			leaveApplyRegisterMapper.save(data);//????????????
			reduceNormalLeave(employee,data,user);
			attnStatisticsUtil.calWorkHours(user.getEmployee().getCnName(),employee, data.getStartTime(), data.getEndTime(),RunTask.RUN_CODE_60, String.valueOf(data.getLeaveType()),data.getLeaveDays().doubleValue(),0,data.getReason(),data.getId());
		}else if(ConfigConstants.LEAVE_TYPE_8.equals(data.getLeaveType())) {
			//?????????
			leaveApplyRegisterMapper.save(data);//????????????
			reduceNormalLeave(employee,data,user);
			attnStatisticsUtil.calWorkHours(user.getEmployee().getCnName(),employee, data.getStartTime(), data.getEndTime(),RunTask.RUN_CODE_60, String.valueOf(data.getLeaveType()),data.getLeaveDays().doubleValue(),0,data.getReason(),data.getId());
		}else if(ConfigConstants.LEAVE_TYPE_9.equals(data.getLeaveType())) {
			//?????????
			leaveApplyRegisterMapper.save(data);//????????????
			reduceNormalLeave(employee,data,user);
			attnStatisticsUtil.calWorkHours(user.getEmployee().getCnName(),employee, data.getStartTime(), data.getEndTime(),RunTask.RUN_CODE_60, String.valueOf(data.getLeaveType()),data.getLeaveDays().doubleValue(),0,data.getReason(),data.getId());
		}else if(ConfigConstants.LEAVE_TYPE_10.equals(data.getLeaveType())) {
			//??????
			leaveApplyRegisterMapper.save(data);//????????????
			reduceNormalLeave(employee,data,user);
			attnStatisticsUtil.calWorkHours(user.getEmployee().getCnName(),employee, data.getStartTime(), data.getEndTime(),RunTask.RUN_CODE_60, String.valueOf(data.getLeaveType()),data.getLeaveDays().doubleValue(),0,data.getReason(),data.getId());
		}else if(ConfigConstants.LEAVE_TYPE_11.equals(data.getLeaveType())) {
			//??????
			leaveApplyRegisterMapper.save(data);//????????????
			reduceNormalLeave(employee,data,user);
			attnStatisticsUtil.calWorkHours(user.getEmployee().getCnName(),employee, data.getStartTime(), data.getEndTime(),RunTask.RUN_CODE_60, String.valueOf(data.getLeaveType()),data.getLeaveDays().doubleValue(),0,data.getReason(),data.getId());
		}else if(ConfigConstants.LEAVE_TYPE_12.equals(data.getLeaveType())){
			//??????
			leaveApplyRegisterMapper.save(data);//????????????
			reduceNormalLeave(employee,data,user);
			attnStatisticsUtil.calWorkHours(user.getEmployee().getCnName(),employee, data.getStartTime(), data.getEndTime(),RunTask.RUN_CODE_60, String.valueOf(data.getLeaveType()),data.getLeaveDays().doubleValue(),0,data.getReason(),data.getId());
		}
		
		return new ResponseDTO(ResponseDTO.SUCCESS_CODE,"???????????????");
	}
	
	/**
	 * ??????????????????
	 * @param employee
	 * @param data
	 * @param startTime
	 * @param endTime
	 * @param reduceDays
	 * @return
	 */
	private void reduceYearLeave(Employee employee,LeaveApplyRegister data,User user) throws Exception{
		
		String optUser = user.getEmployee().getCnName();
		
		List<EmpLeave> planLeaves = new ArrayList<EmpLeave>();
		EmpLeave planLeaveP = new EmpLeave();
		planLeaveP.setType(ConfigConstants.LEAVE_TYPE_1);
		planLeaveP.setEmployeeId(employee.getId());
		planLeaveP.setPlanStartTime(data.getStartTime());
		planLeaveP.setPlanEndTime(data.getEndTime());
		planLeaveP.setPlanDays(data.getLeaveDays().doubleValue());
		planLeaves.add(planLeaveP);
		
		LeaveRecord record = new LeaveRecord();
		record.setEmployeeId(employee.getId());
		record.setType(ConfigConstants.LEAVE_TYPE_1);
		record.setDays(data.getLeaveDays().doubleValue());
		record.setBillId(data.getId());
		record.setBillType(ConfigConstants.REGISTER_LEAVE_KEY);
		record.setDaysUnit(0);// ??????
		record.setCreateTime(new Date());
		record.setCreateUser(optUser);
		record.setDelFlag(0);
		record.setSource(2);//HR??????
		record.setRemark(data.getReason());
		leaveRecordMapper.save(record);
		
		for (EmpLeave planLeave : planLeaves) {
			Double planDays = planLeave.getPlanDays();//??????????????????

			Map<String, Object> totalLeave = empLeaveMapper.getMaxAllowDays(planLeave);
			if (null == totalLeave || totalLeave.size() == 0) {
				throw new OaException("?????????????????????");
			}
			// ???????????????????????? <= (?????????????????? - ??????????????????)
			Double totalAllowRemainDays = Double.valueOf(totalLeave.get("allowRemainDays") + "");// ?????????????????????????????????
			Double totalBlockedDays = Double.valueOf(totalLeave.get("blockedDays") + "");// ??????????????????
			if (planDays > totalAllowRemainDays - totalBlockedDays) {
				throw new OaException("?????????????????????");
			}

			// 2.??????1??????????????????????????????????????????
			List<EmpLeave> leaves = empLeaveMapper.getAllowDays(planLeave);
			Integer year = null;
			Double remainPlanDays = planDays;// ????????????????????????
			Double remainBlockedDays = planDays;// ???????????????????????????
			loop: for (EmpLeave empLeave : leaves) {
				EmpLeave leave = empLeaveMapper.getById(empLeave.getId());
				leave.setUpdateUser(optUser);
				leave.setUpdateTime(new Date());

				// ????????????
				EmpLeave totalleave = empLeave;
				totalleave = empLeaveMapper.getById(empLeave.getId());
				totalleave.setUpdateUser(optUser);

				if (null == year || !year.equals(empLeave.getYear())) {
                    //???????????????????????????
					remainBlockedDays = remainBlockedDays > leave.getAllowRemainDays() - leave.getBlockedDays()
							? (leave.getAllowRemainDays() - leave.getBlockedDays()) : remainBlockedDays;

					totalleave.setUsedDays(totalleave.getUsedDays()+remainBlockedDays);
					totalleave.setAllowRemainDays(totalleave.getAllowRemainDays()-remainBlockedDays);
					empLeaveMapper.updateById(totalleave);
					remainBlockedDays = planDays > remainBlockedDays ? planDays - remainBlockedDays
							: remainBlockedDays;

					year = empLeave.getYear();
					continue;
				}

				if (remainPlanDays <= leave.getAllowRemainDays()) {
					LeaveRecordDetail recordDetail = new LeaveRecordDetail();
					recordDetail.setLeaveRecordId(record.getId());
					recordDetail.setBaseEmpLeaveId(leave.getId());
					recordDetail.setDays(remainPlanDays);
					recordDetail.setCreateTime(new Date());
					recordDetail.setCreateUser(optUser);
					recordDetail.setDelFlag(0);
					leaveRecordDetailMapper.save(recordDetail);
					leave.setUsedDays(leave.getUsedDays()+remainPlanDays);
					leave.setAllowRemainDays(leave.getAllowRemainDays() - remainPlanDays);
					empLeaveMapper.updateById(leave);

					break loop;
				} else {
					LeaveRecordDetail recordDetail = new LeaveRecordDetail();
					recordDetail.setLeaveRecordId(record.getId());
					recordDetail.setBaseEmpLeaveId(leave.getId());
					recordDetail.setDays(leave.getAllowRemainDays());
					recordDetail.setCreateTime(new Date());
					recordDetail.setCreateUser(optUser);
					recordDetail.setDelFlag(0);
					leaveRecordDetailMapper.save(recordDetail);
					remainPlanDays = remainPlanDays - leave.getAllowRemainDays();
					leave.setUsedDays(leave.getUsedDays()+leave.getAllowRemainDays());
					leave.setAllowRemainDays(0.0);
					empLeaveMapper.updateById(leave);

				}
				year = leave.getYear();
			}
		}
		
	}
	
	/**
	 * ??????????????????
	 * @param employee
	 * @param data
	 * @param user
	 * @throws Exception
	 */
	public void reduceRestLeave(Employee employee,LeaveApplyRegister data,User user) throws Exception{
		String optUser = user.getEmployee().getCnName();
		
		List<EmpLeave> planLeaves = new ArrayList<EmpLeave>();
		EmpLeave planLeaveP = new EmpLeave();
		planLeaveP.setType(ConfigConstants.LEAVE_TYPE_5);
		planLeaveP.setEmployeeId(employee.getId());
		planLeaveP.setPlanStartTime(data.getStartTime());
		planLeaveP.setPlanEndTime(data.getEndTime());
		planLeaveP.setPlanDays(data.getLeaveHours().doubleValue());
		planLeaves.add(planLeaveP);
		
		LeaveRecord record = new LeaveRecord();
		record.setEmployeeId(employee.getId());
		record.setType(ConfigConstants.LEAVE_TYPE_5);
		record.setDays(data.getLeaveHours().doubleValue());
		record.setBillId(data.getId());
		record.setBillType(ConfigConstants.REGISTER_LEAVE_KEY);
		record.setDaysUnit(1);// ??????
		record.setCreateTime(new Date());
		record.setCreateUser(optUser);
		record.setDelFlag(0);
		record.setSource(2);//HR??????
		record.setRemark(data.getReason());
		leaveRecordMapper.save(record);
		
		Double totalPlanDays = 0D;
		Double totalBlackDays = 0D;
		for (EmpLeave planLeave : planLeaves) {// ???????????????????????????????????????(?????????????????????????????????????????????)
			Double planDays = planLeave.getPlanDays();// ??????????????????
			totalPlanDays += planDays;

			try {
				Double remainUsedDays = planDays;// ???????????????????????????
				Double remainUsedTotalDays = planDays;// ???????????????
				//??????????????????????????????13 --> 5 
					    //?????????????????????????????? --> ?????????????????????????????????????????????
				List<EmpLeave> leaveDays = empLeaveMapper.getAllowedRestLeaveDays(planLeave);
				
				Double totalAllowDays = 0d;
				for(EmpLeave leave:leaveDays) {
					totalAllowDays += (leave.getAllowRemainDays()-leave.getBlockedDays());
				}
				if(totalAllowDays<planDays) {
					throw new OaException("????????????????????????");
				}
				
				if(leaveDays != null && leaveDays.size() > 0){
					for (EmpLeave empLeave : leaveDays) {
						remainUsedDays = remainUsedDays > empLeave.getAllowRemainDays()
								? empLeave.getAllowRemainDays() : remainUsedDays;// ????????????????????????
						empLeave.setUpdateUser(optUser);
						empLeave.setUpdateTime(new Date());
						empLeave.setUsedDays(empLeave.getUsedDays()+remainUsedDays);
						empLeave.setAllowRemainDays(empLeave.getAllowRemainDays()-remainUsedDays);
						empLeaveMapper.updateById(empLeave);
						remainUsedTotalDays = remainUsedTotalDays - remainUsedDays;
						// ????????????
						LeaveRecordDetail recordDetail = new LeaveRecordDetail();
						recordDetail.setLeaveRecordId(record.getId());
						recordDetail.setBaseEmpLeaveId(empLeave.getId());
						recordDetail.setDays(remainUsedDays);
						recordDetail.setCreateTime(new Date());
						recordDetail.setCreateUser(optUser);
						recordDetail.setDelFlag(0);
						List<LeaveRecordDetail> isExist = leaveRecordDetailMapper.selectByCondition(recordDetail);
						if (isExist != null && isExist.size() > 0) {
							isExist.get(0).setDays(isExist.get(0).getDays() + remainUsedDays);
							leaveRecordDetailMapper.updateById(isExist.get(0));
						} else {
							leaveRecordDetailMapper.save(recordDetail);
						}
						totalBlackDays += remainUsedDays;
						remainUsedDays = remainUsedTotalDays;
						if(remainUsedTotalDays <= 0){
							break;
						}
					}
					if(remainUsedTotalDays > 0){
						throw new OaException("???????????????" + totalPlanDays + "??????,??????????????????" + (totalPlanDays-totalBlackDays) + "??????,??????????????????");
					}
				}else{
					throw new OaException("??????????????????????????????");
				}

			} catch (Exception e) {
				logger.error("????????????????????????????????????????????????=" + e.toString());
				throw e;
			}
		}
	}
	
	/**
	 * ????????????????????????????????????????????????
	 * @param employee
	 * @param data
	 * @param user
	 */
	private void reduceNormalLeave(Employee employee,LeaveApplyRegister data,User user) throws Exception{
		//??????
		LeaveRecord record = new LeaveRecord();
		record.setEmployeeId(employee.getId());
		record.setType(data.getLeaveType());
		record.setDays(data.getLeaveDays().doubleValue());
		record.setBillId(data.getId());
		record.setBillType(ConfigConstants.REGISTER_LEAVE_KEY);
		record.setDaysUnit(0);//??????
		record.setCreateTime(new Date());
		record.setCreateUser(user.getEmployee().getCnName());
		record.setDelFlag(0);
		record.setSource(2);//HR??????
		record.setRemark(data.getReason());
		leaveRecordMapper.save(record);
		//?????????
		if(DateUtils.format(data.getStartTime(),"yyyy").equals(DateUtils.format(data.getEndTime(),"yyyy"))){
			EmpLeave isExist = new EmpLeave();
			isExist.setType(data.getLeaveType());
			isExist.setYear(Integer.valueOf(DateUtils.format(data.getStartTime(), "yyyy")));
			isExist.setEmployeeId(employee.getId());
			List<EmpLeave> isExistList = empLeaveService.getByListCondition(isExist);
			if(isExistList!=null&&isExistList.size()>0){
				isExistList.get(0).setUsedDays(isExistList.get(0).getUsedDays()+data.getLeaveDays().doubleValue());
				empLeaveService.updateById(isExistList.get(0));
				//????????????
				saveRecordDetail(record.getId(),isExistList.get(0).getId(),data.getLeaveDays().doubleValue(),user);
			}else{
				EmpLeave empLeave = new EmpLeave();
				empLeave.setCompanyId(employee.getCompanyId());
				empLeave.setEmployeeId(employee.getId());
				empLeave.setYear(Integer.valueOf(DateUtils.format(data.getStartTime(), "yyyy")));
				empLeave.setType(data.getLeaveType());
				empLeave.setUsedDays(data.getLeaveDays().doubleValue());
				empLeave.setCategory(0);
				empLeave.setIsActive(0);
				empLeave.setCreateTime(new Date());
				empLeave.setCreateUser( user.getEmployee().getCnName());
				empLeave.setDelFlag(0);
				empLeaveService.save(empLeave);
				//????????????
				saveRecordDetail(record.getId(),empLeave.getId(),data.getLeaveDays().doubleValue(),user);
			}
		}else{
			//????????????????????????????????????
			//????????????????????????????????????????????????????????????????????????
			Double leaveDaysCurrent = 0d;
			if(data.getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_3.intValue()
					||data.getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_9.intValue()
					||data.getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_10.intValue()
					||data.getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_6.intValue()
					||data.getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_7.intValue()
					||data.getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_8.intValue()
					||data.getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_4.intValue()){
				Date endDate = DateUtils.parse(DateUtils.format(data.getStartTime(), "yyyy")+"-12-31", DateUtils.FORMAT_SHORT);
				leaveDaysCurrent = DateUtils.getIntervalDays(data.getStartTime(), endDate).doubleValue()+1;
			}else{
				//???????????????12-31???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
				Date endDate = DateUtils.parse(DateUtils.format(data.getStartTime(), "yyyy")+"-12-31", DateUtils.FORMAT_SHORT);
				EmployeeClass employeeClass = new EmployeeClass();
				employeeClass.setEmployId(employee.getId());
				employeeClass.setClassDate(endDate);
				EmployeeClass empClass = employeeClassService.getEmployeeClassSetting(employeeClass);
				while(true){
					if(empClass!=null){
						break;
					}
					endDate = DateUtils.addDay(endDate, -1);
					employeeClass.setClassDate(endDate);
					empClass = employeeClassService.getEmployeeClassSetting(employeeClass);
				}
				//???????????????????????????????????????????????????????????????????????????
				Map<String, Object> leaveDaysCurrentMap = empApplicationLeaveService.calculatedLeaveDays(employee.getId(),DateUtils.format(data.getStartTime(), DateUtils.FORMAT_SHORT),
					DateUtils.format(data.getStartTime(),DateUtils.FORMAT_HH),DateUtils.format(empClass.getClassDate(), DateUtils.FORMAT_SHORT)
					,DateUtils.format(empClass.getEndTime(),DateUtils.FORMAT_HH),String.valueOf(data.getLeaveType()));
				leaveDaysCurrent = Double.valueOf(String.valueOf(leaveDaysCurrentMap.get("leaveDays")));
			}
			EmpLeave isExist = new EmpLeave();
			isExist.setType(data.getLeaveType());
			isExist.setYear(Integer.valueOf(DateUtils.format(data.getStartTime(), "yyyy")));
			isExist.setEmployeeId(employee.getId());
			List<EmpLeave> isExistList = empLeaveService.getByListCondition(isExist);
			if(isExistList!=null&&isExistList.size()>0){
				isExistList.get(0).setUsedDays(isExistList.get(0).getUsedDays()+leaveDaysCurrent);
				empLeaveService.updateById(isExistList.get(0));
				//????????????
				saveRecordDetail(record.getId(),isExistList.get(0).getId(),leaveDaysCurrent,user);
			}else{
				EmpLeave empLeave = new EmpLeave();
				empLeave.setCompanyId(employee.getCompanyId());
				empLeave.setEmployeeId(employee.getId());
				empLeave.setYear(Integer.valueOf(DateUtils.format(data.getStartTime(), "yyyy")));
				empLeave.setType(data.getLeaveType());
				empLeave.setUsedDays(leaveDaysCurrent);
				empLeave.setCategory(0);
				empLeave.setIsActive(0);
				empLeave.setCreateTime(new Date());
				empLeave.setCreateUser(user.getEmployee().getCnName());
				empLeave.setDelFlag(0);
				empLeaveService.save(empLeave);
				//????????????
				saveRecordDetail(record.getId(),empLeave.getId(),leaveDaysCurrent,user);
			}
			isExist.setYear(Integer.valueOf(DateUtils.format(data.getEndTime(), "yyyy")));
			List<EmpLeave> isExistListNext = empLeaveService.getByListCondition(isExist);
			if(isExistListNext!=null&&isExistListNext.size()>0){
				isExistListNext.get(0).setUsedDays(isExistListNext.get(0).getUsedDays()+data.getLeaveDays().doubleValue()-leaveDaysCurrent);
				empLeaveService.updateById(isExistListNext.get(0));
				//????????????
				saveRecordDetail(record.getId(),isExistListNext.get(0).getId(),data.getLeaveDays().doubleValue()-leaveDaysCurrent,user);
			}else{
				EmpLeave empLeave = new EmpLeave();
				empLeave.setCompanyId(employee.getCompanyId());
				empLeave.setEmployeeId(employee.getId());
				empLeave.setYear(Integer.valueOf(DateUtils.format(data.getEndTime(), "yyyy")));
				empLeave.setType(data.getLeaveType());
				empLeave.setUsedDays(data.getLeaveDays().doubleValue()-leaveDaysCurrent);
				empLeave.setCategory(0);
				empLeave.setIsActive(0);
				empLeave.setCreateTime(new Date());
				empLeave.setCreateUser(user.getEmployee().getCnName());
				empLeave.setDelFlag(0);
				empLeaveService.save(empLeave);
				//????????????
				saveRecordDetail(record.getId(),empLeave.getId(),data.getLeaveDays().doubleValue()-leaveDaysCurrent,user);
			}
		}
	}
	
	private void saveRecordDetail(Long recordId,Long baseEmpLeaveId,Double days,
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
			isExist1.get(0).setDays(isExist1.get(0).getDays()+days);
			leaveRecordDetailMapper.updateById(isExist1.get(0));
		}else{
			leaveRecordDetailMapper.save(recordDetail);
		}
	}

	@Override
	public PageModel<LeaveApplyRegister> getRegisterLeaveListByPage(LeaveApplyRegister data) {
		
		int page = data.getPage() == null ? 0 : data.getPage();
		int rows = data.getRows() == null ? 0 : data.getRows();
		
		PageModel<LeaveApplyRegister> pm = new PageModel<LeaveApplyRegister>();
		
		pm.setPageNo(page);
		pm.setPageSize(rows);
		Integer total = leaveApplyRegisterMapper.getRegisterLeaveCountByCondition(data);
		pm.setTotal(total);
		
		data.setOffset(pm.getOffset());
		data.setLimit(pm.getLimit());
		
		List<LeaveApplyRegister> list = leaveApplyRegisterMapper.getRegisterLeaveListByCondition(data);
		
		pm.setRows(list);
		
		return pm;
	}

}
