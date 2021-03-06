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
import com.ule.oa.base.common.OaCommon;
import com.ule.oa.base.mapper.AttnWorkHoursMapper;
import com.ule.oa.base.mapper.EmpApplicationLeaveAbolishMapper;
import com.ule.oa.base.mapper.EmpApplicationLeaveDetailMapper;
import com.ule.oa.base.mapper.EmpApplicationLeaveMapper;
import com.ule.oa.base.mapper.LeaveRecordDetailMapper;
import com.ule.oa.base.mapper.LeaveRecordMapper;
import com.ule.oa.base.mapper.RabcUserMapper;
import com.ule.oa.base.po.AttnWorkHours;
import com.ule.oa.base.po.ClassSetting;
import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.EmpApplicationLeave;
import com.ule.oa.base.po.EmpApplicationLeaveAbolish;
import com.ule.oa.base.po.EmpApplicationLeaveDetail;
import com.ule.oa.base.po.EmpLeave;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.po.LeaveRecord;
import com.ule.oa.base.po.LeaveRecordDetail;
import com.ule.oa.base.po.RunTask;
import com.ule.oa.base.po.User;
import com.ule.oa.base.po.tbl.ViewTaskInfoTbl;
import com.ule.oa.base.service.AnnualVacationService;
import com.ule.oa.base.service.ClassSettingService;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.EmpApplicationLeaveAbolishService;
import com.ule.oa.base.service.EmpLeaveService;
import com.ule.oa.base.service.EmployeeClassService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.SendMailService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.service.ViewTaskInfoService;
import com.ule.oa.base.util.AttnStatisticsUtil;
import com.ule.oa.base.vo.TaskVO;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.po.CommonPo;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.ExcelUtil;
import com.ule.oa.common.utils.PageModel;
import com.ule.oa.common.utils.http.IPUtils;

@Service
public class EmpApplicationLeaveAbolishServiceImpl implements EmpApplicationLeaveAbolishService {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired 
	private EmpApplicationLeaveAbolishMapper empApplicationLeaveAbolishMapper;
	@Autowired
	private UserService userService;
	@Autowired
	private ActivitiServiceImpl activitiServiceImpl;
	@Autowired
	private ConfigService configService;
	@Autowired
	private AnnualVacationService annualVacationService;
	@Autowired
	private EmpApplicationLeaveMapper empApplicationLeaveMapper;
	@Autowired
	private EmpApplicationLeaveDetailMapper empApplicationLeaveDetailMapper;
	@Autowired
	private ViewTaskInfoService viewTaskInfoService;
	@Autowired
	private EmpLeaveService empLeaveService;
	@Autowired
	private LeaveRecordMapper leaveRecordMapper;
	@Autowired
	private LeaveRecordDetailMapper leaveRecordDetailMapper;
	@Autowired
	private EmployeeClassService employeeClassService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private ClassSettingService classSettingService;
	@Autowired
	private AttnWorkHoursMapper attnWorkHoursMapper;
	@Autowired
	private SendMailService sendMailService;
	@Autowired
	private RabcUserMapper rabcUserMapper;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> save(HttpServletRequest request,EmpApplicationLeaveAbolish leaveAbolish,
			User user) throws Exception {
		
		logger.info("???????????????save:start??????");
		Long time1 = System.currentTimeMillis();
		logger.info("???????????????save??????:leaveId="+leaveAbolish.getLeaveId()+";startTime="+leaveAbolish.getStartTime()+";endTime="+leaveAbolish.getEndTime()
				+";leaveDays="+leaveAbolish.getLeaveDays()+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));

		Map<String,Object> map = new HashMap<String, Object>();
		
	    if(leaveAbolish.getLeaveId()==null){
			throw new OaException("??????????????????????????????");
		}
	    
	    //????????????????????????????????????????????????
	    List<EmpApplicationLeaveAbolish> isExist = empApplicationLeaveAbolishMapper.getByLeaveId(leaveAbolish.getLeaveId());
	    if(isExist!=null&&isExist.size()>0){
	    	throw new OaException("????????????????????????????????????????????????");
	    }
	    
	    if(leaveAbolish.getStartTime()==null){
			throw new OaException("???????????????????????????");
		}
	    
	    if(leaveAbolish.getEndTime()==null){
			throw new OaException("???????????????????????????");
		}
	    
	    if(leaveAbolish.getLeaveDays()==null){
			throw new OaException("?????????????????????");
		}
	    
	    //?????????????????????
	    EmpApplicationLeave leave = empApplicationLeaveMapper.getById(leaveAbolish.getLeaveId());
	    if(leave==null){
	    	throw new OaException("??????????????????????????????");
	    }
	    
	    //??????????????????
	    List<EmpApplicationLeaveDetail> detailList = empApplicationLeaveDetailMapper.getListByCondition(leaveAbolish.getLeaveId());
	    
	    if(detailList==null||detailList.size()<=0){
	    	throw new OaException("??????????????????????????????");
	    }
	    
	    if(ConfigConstants.LEAVE_TYPE_5.intValue()==detailList.get(0).getLeaveType().intValue()){
	    	if(leaveAbolish.getLeaveHours()>detailList.get(0).getLeaveHours()){
	    		throw new OaException("????????????????????????????????????????????????");
	    	}
	    	if(leaveAbolish.getLeaveHours()<=0){
	    		throw new OaException("??????????????????????????????????????????????????????");
	    	}
	    }else{
            if(leaveAbolish.getLeaveDays()>detailList.get(0).getLeaveDays()){
            	throw new OaException("??????????????????????????????????????????????????????");
	    	}
            if(leaveAbolish.getLeaveDays()<=0){
            	
            }
	    }
	    
	    //?????????????????????????????????????????????????????????????????????>9,?????????????????????
	    if(ConfigConstants.LEAVE_TYPE_1.intValue()==detailList.get(0).getLeaveType().intValue()
	    		||ConfigConstants.LEAVE_TYPE_2.intValue()==detailList.get(0).getLeaveType().intValue()
				||ConfigConstants.LEAVE_TYPE_5.intValue()==detailList.get(0).getLeaveType().intValue()
				||ConfigConstants.LEAVE_TYPE_11.intValue()==detailList.get(0).getLeaveType().intValue()
				||ConfigConstants.LEAVE_TYPE_12.intValue()==detailList.get(0).getLeaveType().intValue()){
	    	EmployeeClass employeeClass = new EmployeeClass();
			employeeClass.setEmployId(leave.getEmployeeId());
			employeeClass.setClassDate(leaveAbolish.getStartTime());
			EmployeeClass empClass = employeeClassService.getEmployeeClassSetting(employeeClass);
	    	if(empClass!=null){
	    		if(empClass.getMustAttnTime()>9){
	    			if(DateUtils.format(leaveAbolish.getStartTime(), DateUtils.FORMAT_SHORT).equals(DateUtils.format(leave.getStartTime(), DateUtils.FORMAT_SHORT))){
	    				if(!DateUtils.format(leaveAbolish.getStartTime(), DateUtils.FORMAT_LONG).equals(DateUtils.format(leave.getStartTime(), DateUtils.FORMAT_LONG))){
	    					throw new OaException("????????????????????????"+DateUtils.format(leave.getStartTime(), DateUtils.FORMAT_LONG_MM)+"!");
	    				}
	    			}else{
	    				if(!DateUtils.format(leaveAbolish.getStartTime(), DateUtils.FORMAT_HH_MM).equals(DateUtils.format(employeeClass.getStartTime(), DateUtils.FORMAT_HH_MM))){
	    					throw new OaException("????????????????????????"+DateUtils.format(employeeClass.getClassDate(), DateUtils.FORMAT_SHORT)+" "+DateUtils.format(employeeClass.getStartTime(), DateUtils.FORMAT_HH_MM)+"!");
	    				}
	    			}
	    		}
	    	}
	    }
		
		//??????5???????????????????????????????????????????????????????????????5???????????????????????????????????????????????????
		Config configCondition = new Config();
		configCondition.setCode("timeLimit5");
		List<Config> limit = configService.getListByCondition(configCondition);
		int num = Integer.valueOf(limit.get(0).getDisplayCode());
		String nowDate10 = DateUtils.format(annualVacationService.get5WorkingDayNextmonth(num), DateUtils.FORMAT_SHORT);
		int nowCountDays = DateUtils.getIntervalDays(DateUtils.parse(nowDate10, DateUtils.FORMAT_SHORT),DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_SHORT),DateUtils.FORMAT_SHORT));
		if(nowCountDays > 0) {
			String nowDate01 = DateUtils.getNow("yyyy-MM") + "-01";
			int nowCountDays01 = DateUtils.getIntervalDays(DateUtils.parse(nowDate01, DateUtils.FORMAT_SHORT),leaveAbolish.getStartTime());
		
			if(detailList.get(0).getLeaveType().intValue()!=ConfigConstants.LEAVE_TYPE_7.intValue()){		
				if(nowCountDays01 < 0) {
					throw new OaException("??????????????????3???????????????????????????????????????");
				}
			}
		} else {
			String nowDate01 = DateUtils.getNow("yyyy-MM") + "-01";
			Date addDate = DateUtils.addMonth(DateUtils.parse(nowDate01, DateUtils.FORMAT_SHORT), -1);
			int nowCountDays01 = DateUtils.getIntervalDays(addDate,leaveAbolish.getStartTime());
			if(nowCountDays01 < 0) {
				throw new OaException("?????????????????????????????????");
			}
		}
		
	    leaveAbolish.setApprovalStatus(ConfigConstants.DOING_STATUS);
		
	    leaveAbolish.setCreateTime(new Date());
	    leaveAbolish.setCreateUser(user.getEmployee().getCnName());

		String processInstanceId = activitiServiceImpl.startByKey(ConfigConstants.CANCELLEAVE_KEY);
		//???????????????????????????
		leaveAbolish.setProcessInstanceId(processInstanceId);
		empApplicationLeaveAbolishMapper.save(leaveAbolish);
		

		//-----------------start-----------------------????????????????????????
		ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
		flow.setAssigneeName(user.getEmployee().getCnName());
		flow.setDepartName(user.getDepart().getName());
		flow.setPositionName(user.getPosition().getPositionName());
		flow.setFinishTime(leaveAbolish.getCreateTime());
		flow.setComment(leaveAbolish.getComment());
		flow.setProcessId(processInstanceId);
		flow.setProcessKey(ConfigConstants.CANCELLEAVE_KEY);
		flow.setStatu(0);
		viewTaskInfoService.save(flow);
		
		//??????????????????????????????
		Map<String,Object> v = Maps.newHashMap();
		v.put("leaveDay", leave.getDuration());
		activitiServiceImpl.setVariablesByProcessInstanceId(processInstanceId,v);
		map.put("success", true);
		
		Long time2 = System.currentTimeMillis();
		logger.info("???????????????save:use time="+(time2-time1));
		logger.info("???????????????save:end??????");
		
	    return map;
	}

	@Override
	public EmpApplicationLeaveAbolish queryByProcessInstanceId(
			String instanceId) {
		return empApplicationLeaveAbolishMapper.queryByProcessId(instanceId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void completeTask(HttpServletRequest request,String processId, String comment,
			String commentType) throws Exception {
		
		logger.info("??????????????????completeTask:start??????");
		Long time1 = System.currentTimeMillis();
		
		User user = userService.getCurrentUser();
		logger.info("??????????????????completeTask??????:processId="+processId+";comment="+comment+";commentType="+commentType
			+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));
		
		Task task = activitiServiceImpl.queryTaskByProcessInstanceId(processId);
		EmpApplicationLeaveAbolish leaveAbolish = queryByProcessInstanceId(processId);
		
		if(leaveAbolish==null){
			logger.error("???????????????"+processId+"???????????????????????????");
			throw new OaException("??????????????????????????????");
		}
		
		EmpApplicationLeave leave = empApplicationLeaveMapper.getById(leaveAbolish.getLeaveId());
		if(null == task) {
			throw new OaException("????????????????????????????????????,????????????????????????");
		}else if(!StringUtils.equalsIgnoreCase(task.getAssignee(), user.getEmployeeId().toString())
				&&!StringUtils.equalsIgnoreCase(leave.getEmployeeId().toString(), user.getEmployeeId().toString())){
			task = activitiServiceImpl.queryTaskByTaskIdAndCandidate(task.getId(), user.getEmployeeId().toString());
			if(task == null) {
				throw new OaException("????????????????????????????????????,????????????????????????");
			}
		}
		boolean type = StringUtils.startsWithIgnoreCase(task.getTaskDefinitionKey(), "end_");
		
		//??????????????????????????????	
		if(annualVacationService.check5WorkingDayNextmonth(DateUtils.format(leaveAbolish.getStartTime(), DateUtils.FORMAT_SHORT),type) && leave.getLeaveTypeFlag()!=0){			
			logger.error(user.getEmployee().getCnName()+"????????????Id="+leave.getEmployeeId()+"?????????????????????????????????,???????????????");
			throw new OaException("?????????????????????,???????????????");
		}else{
			Integer approvalStatus ;
			if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.PASS)) {
				approvalStatus=type?ConfigConstants.PASS_STATUS:ConfigConstants.DOING_STATUS;
				
			}else if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.REFUSE)) {
				approvalStatus=ConfigConstants.REFUSE_STATUS;
			}else {
				approvalStatus=ConfigConstants.BACK_STATUS;
			}
			leaveAbolish.setApprovalStatus(approvalStatus);
			leaveAbolish.setUpdateTime(new Date());
			leaveAbolish.setUpdateUser(user.getEmployee().getCnName());
			empApplicationLeaveAbolishMapper.updateById(leaveAbolish);
			//????????????????????????????????????assignee,??????????????????????????????????????????
			if(StringUtils.isBlank(task.getAssignee())) {
				activitiServiceImpl.claimTask(task.getId(), String.valueOf(user.getEmployeeId()));
			}
			sendMailService.sendCommentMail(employeeService.getByEmpId(leave.getEmployeeId()).get(0).getEmail(), "???????????????", task.getName()+"-"+user.getEmployee().getCnName(), approvalStatus);
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
			activitiServiceImpl.completeTask(task.getId(), comment,null,commentType);
		}
		Long time2 = System.currentTimeMillis();
		logger.info("??????????????????completeTask:use time="+(time2-time1));
		logger.info("??????????????????completeTask:end??????");
	}

	@Override
	public void setValueToVO(TaskVO taskVO, String processInstanceId) {
		EmpApplicationLeaveAbolish leaveAbolish = queryByProcessInstanceId(processInstanceId);
		if(leaveAbolish!=null){
			EmpApplicationLeave leave = empApplicationLeaveMapper.getById(leaveAbolish.getLeaveId());
			List<EmpApplicationLeaveDetail> leaveDetailList = new ArrayList<EmpApplicationLeaveDetail>();
			if(leave!=null){
			    leaveDetailList = empApplicationLeaveDetailMapper.getListByCondition(leave.getId());
			}else{
				logger.error("??????id="+leaveAbolish.getLeaveId()+"????????????????????????");
			}
			
			taskVO.setProcessName("????????????");
			String redirectUrl = "/empApplicationLeaveBack/approve.htm?flag=no&leaveAbolishId="+leaveAbolish.getId();
			if(!(taskVO.getProcessStatu()==null)) {
				redirectUrl = "/empApplicationLeaveBack/approve.htm?flag=can&leaveAbolishId="+leaveAbolish.getId();
			}
			taskVO.setRedirectUrl(redirectUrl);
			taskVO.setCreatorDepart(leave!=null?leave.getDepartName():"");
			taskVO.setProcessStatu(leaveAbolish.getApprovalStatus());
			taskVO.setOperation(ConfigConstants.STATUS.getOperationByStatu(leaveAbolish.getApprovalStatus()));
			taskVO.setCreator(leaveAbolish.getCreateUser());
			taskVO.setCreateTime(leaveAbolish.getCreateTime());
			if(leaveDetailList!=null&&leaveDetailList.size()>0){
				taskVO.setView3(OaCommon.getLeaveTypeMap().get(leaveDetailList.get(0).getLeaveType()));
			}
			taskVO.setView4(DateUtils.format(leaveAbolish.getStartTime(), ConfigConstants.DATEFORMAT)+"~"+DateUtils.format(leaveAbolish.getEndTime(), ConfigConstants.DATEFORMAT) + "&nbsp;&nbsp;" + leaveAbolish.getLeaveDays() + "???");
			taskVO.setView5(leaveAbolish.getComment()!=null?leaveAbolish.getComment():"");
			taskVO.setReProcdefCode("130");
			taskVO.setProcessId(leaveAbolish.getProcessInstanceId());
			taskVO.setResourceId(String.valueOf(leaveAbolish.getId()));
		}else{
			logger.error("??????processInstanceId="+processInstanceId+"????????????????????????");
		}
		
	}

	@Override
	public EmpApplicationLeaveAbolish getById(Long id) {
		return empApplicationLeaveAbolishMapper.getById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void endHandle(String processInstanceId, User user) throws Exception {
		EmpApplicationLeaveAbolish leaveAbolish = queryByProcessInstanceId(processInstanceId);
		if(leaveAbolish!=null){
			//???????????????
			EmpApplicationLeave leave = empApplicationLeaveMapper.getById(leaveAbolish.getLeaveId());
			List<EmpApplicationLeaveDetail> leaveDetailList = empApplicationLeaveDetailMapper.getListByCondition(leave.getId());
			
			int leaveType = leaveDetailList.get(0).getLeaveType().intValue();
			//??????????????????,?????????
			List<Map<String, Object>> planLeaves = new ArrayList<Map<String, Object>>();
			
			if(leaveDetailList.get(0).getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_1.intValue()
					||leaveDetailList.get(0).getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_2.intValue()
					||leaveDetailList.get(0).getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_5.intValue()){
				Map<String, Object> planLeave = new HashMap<String,Object>();
				planLeave.put("companyId", user.getEmployee().getCompanyId());
				planLeave.put("employeeId", leave.getEmployeeId());
				planLeave.put("type", leaveDetailList.get(0).getLeaveType());
				planLeave.put("planDays", leaveAbolish.getLeaveDays());
				if(leaveDetailList.get(0).getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_5.intValue()){
					planLeave.put("planDays", leaveAbolish.getLeaveHours());
				}
				planLeave.put("planStartTime",leaveAbolish.getStartTime());
				planLeave.put("planEndTime",leaveAbolish.getEndTime());
				planLeave.put("optUser", user.getEmployee().getCnName());
				planLeave.put("empapplicationleaveId", leaveAbolish.getId());
				if(leaveDetailList.get(0).getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_2.intValue()){
					planLeave.put("originalBillId", leave.getId());//?????????id
				}
				planLeaves.add(planLeave);
					
				boolean flag = true;
				if(planLeaves!=null&&planLeaves.size()>0){
					logger.info("??????????????????????????????="+planLeaves.toString());
					flag = empLeaveService.updateEmpLeaveAbolish(planLeaves);		
					if(!flag){
						throw new OaException("????????????????????????!");
					}
				}
			}else{
				//??????
				LeaveRecord record = new LeaveRecord();
				record.setEmployeeId(leave.getEmployeeId());
				record.setType(leaveDetailList.get(0).getLeaveType());
				record.setDays(-leaveAbolish.getLeaveDays());
				record.setDaysUnit(0);//??????
				if(leaveDetailList.get(0).getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_5.intValue()){
					record.setDays(-leaveAbolish.getLeaveHours());
					record.setDaysUnit(1);//??????
				}
				record.setBillId(leaveAbolish.getId());
				record.setBillType(ConfigConstants.CANCELLEAVE_KEY);
				
				record.setCreateTime(new Date());
				record.setCreateUser(user.getEmployee().getCnName());
				record.setDelFlag(0);
				record.setSource(0);
				record.setRemark("????????????");
				leaveRecordMapper.save(record);
				//?????????
				if(DateUtils.format(leaveAbolish.getStartTime(),"yyyy").equals(DateUtils.format(leaveAbolish.getEndTime(),"yyyy"))){
					EmpLeave isExist = new EmpLeave();
					isExist.setType(leaveDetailList.get(0).getLeaveType());
					isExist.setYear(Integer.valueOf(DateUtils.format(leaveAbolish.getStartTime(), "yyyy")));
					isExist.setEmployeeId(leave.getEmployeeId());
					List<EmpLeave> isExistList = empLeaveService.getByListCondition(isExist);
					if(isExistList!=null&&isExistList.size()>0){
						isExistList.get(0).setUsedDays(isExistList.get(0).getUsedDays()-leaveAbolish.getLeaveDays());
						empLeaveService.updateById(isExistList.get(0));
						//????????????
						saveRecordDetail(record.getId(),isExistList.get(0).getId(),leaveAbolish.getLeaveDays(),user);
					}else{
						logger.error("??????id="+leaveAbolish.getId()+"????????????????????????????????????");
					}
				}else{
					//????????????????????????????????????
					//????????????????????????????????????????????????????????????????????????
					Double leaveDaysCurrent = 0d;
					if(leaveDetailList.get(0).getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_3.intValue()
							||leaveDetailList.get(0).getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_9.intValue()
							||leaveDetailList.get(0).getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_10.intValue()
							||leaveDetailList.get(0).getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_6.intValue()
							||leaveDetailList.get(0).getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_7.intValue()
							||leaveDetailList.get(0).getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_8.intValue()
							||leaveDetailList.get(0).getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_4.intValue()){
						Date endDate = DateUtils.parse(DateUtils.format(leaveAbolish.getStartTime(), "yyyy")+"-12-31", DateUtils.FORMAT_SHORT);
						leaveDaysCurrent = DateUtils.getIntervalDays(leaveAbolish.getStartTime(), endDate).doubleValue()+1;
					}else{
						//???????????????12-31???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
						Date endDate = DateUtils.parse(DateUtils.format(leaveAbolish.getStartTime(), "yyyy")+"-12-31", DateUtils.FORMAT_SHORT);
						EmployeeClass employeeClass = new EmployeeClass();
						employeeClass.setEmployId(leave.getEmployeeId());
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
						Map<String, Object> leaveDaysCurrentMap = calculatedLeaveDays(leave.getEmployeeId(),DateUtils.format(leaveAbolish.getStartTime(), DateUtils.FORMAT_SHORT),
							DateUtils.format(leaveAbolish.getStartTime(),DateUtils.FORMAT_HH),DateUtils.format(empClass.getClassDate(), DateUtils.FORMAT_SHORT)
							,DateUtils.format(empClass.getEndTime(),DateUtils.FORMAT_HH),String.valueOf(leaveDetailList.get(0).getLeaveType()));
						leaveDaysCurrent = Double.valueOf(String.valueOf(leaveDaysCurrentMap.get("leaveDays")));
					}
					EmpLeave isExist = new EmpLeave();
					isExist.setType(leaveDetailList.get(0).getLeaveType());
					isExist.setYear(Integer.valueOf(DateUtils.format(leaveAbolish.getStartTime(), "yyyy")));
					isExist.setEmployeeId(leave.getEmployeeId());
					List<EmpLeave> isExistList = empLeaveService.getByListCondition(isExist);
					if(isExistList!=null&&isExistList.size()>0){
						isExistList.get(0).setUsedDays(isExistList.get(0).getUsedDays()-leaveDaysCurrent);
						empLeaveService.updateById(isExistList.get(0));
						//????????????
						saveRecordDetail(record.getId(),isExistList.get(0).getId(),leaveDaysCurrent,user);
					}else{
						logger.error("??????id="+leaveAbolish.getId()+"????????????????????????????????????");
					}
					isExist.setYear(Integer.valueOf(DateUtils.format(leaveAbolish.getEndTime(), "yyyy")));
					List<EmpLeave> isExistListNext = empLeaveService.getByListCondition(isExist);
					if(isExistListNext!=null&&isExistListNext.size()>0){
						isExistListNext.get(0).setUsedDays(isExistListNext.get(0).getUsedDays()-(leaveAbolish.getLeaveDays()-leaveDaysCurrent));
						empLeaveService.updateById(isExistListNext.get(0));
						//????????????
						saveRecordDetail(record.getId(),isExistListNext.get(0).getId(),leaveAbolish.getLeaveDays()-leaveDaysCurrent,user);
					}else{
						logger.error("??????id="+leaveAbolish.getId()+"????????????????????????????????????");
					}
				}
			}
			
			//??????????????????????????????????????????????????????
			AttnWorkHours attnWorkHours = new AttnWorkHours();
			attnWorkHours.setDelFlag(CommonPo.STATUS_DELETE);
			attnWorkHours.setBillId(leave.getId());
			attnWorkHours.setDataType(Integer.valueOf(RunTask.RUN_CODE_60));
			attnWorkHours.setUpdateTime(new Date());
			attnWorkHours.setUpdateUser(user.getEmployee().getCnName());
			attnWorkHours.setDataReason("???????????????");
			attnWorkHoursMapper.deleteByBillId(attnWorkHours);
		    
			//?????????????????????????????????????????????????????????
			Map<String,String> actualEndTime = getActualEndTime(leave.getEmployeeId(),
					leaveDetailList.get(0).getLeaveType(),leaveDetailList.get(0).getStartTime(),leaveAbolish.getStartTime());
			
			Date actualEndDate = null;
			
			if(ConfigConstants.LEAVE_TYPE_1.intValue()==leaveType||ConfigConstants.LEAVE_TYPE_2.intValue()==leaveType
					||ConfigConstants.LEAVE_TYPE_5.intValue()==leaveType||ConfigConstants.LEAVE_TYPE_11.intValue()==leaveType
					||ConfigConstants.LEAVE_TYPE_12.intValue()==leaveType){//??????????????????????????????????????????????????????????????????
				actualEndDate = DateUtils.parse(actualEndTime.get("endTime"), DateUtils.FORMAT_LONG_MM);
			}else{
				actualEndDate = DateUtils.parse(actualEndTime.get("endTime"), DateUtils.FORMAT_SHORT);
			}
			
			//?????????????????????????????????
			Employee employee = employeeService.getById(leave.getEmployeeId());
			
			AttnStatisticsUtil attnStatisticsUtil= new AttnStatisticsUtil();
			Double leaveHours = null;
			//?????????
			if(leaveDetailList.get(0).getLeaveType().intValue() == 4) {
				if(leaveDetailList.get(0).getChildrenNum() != null) {
					leaveHours = Double.valueOf(leaveDetailList.get(0).getChildrenNum());
				} else {
					leaveHours = 1d;
				}
			}
			if(leaveDetailList.get(0).getStartTime().getTime()!=actualEndDate.getTime()){
				//?????????????????????
				if(leaveDetailList.get(0).getLeaveType().intValue() == 6){
					attnStatisticsUtil.calWorkHours(user.getEmployee().getCnName(),employee, leaveDetailList.get(0).getStartTime(), actualEndDate,RunTask.RUN_CODE_130, String.valueOf(leaveDetailList.get(0).getLeaveType()), leaveHours,0,"???????????????",leaveAbolish.getId());
				}else if(leaveDetailList.get(0).getLeaveType().intValue() == 4){
					attnStatisticsUtil.calWorkHours(user.getEmployee().getCnName(),employee, leaveDetailList.get(0).getStartTime(), actualEndDate,RunTask.RUN_CODE_130, String.valueOf(leaveDetailList.get(0).getLeaveType()), leaveHours,leaveDetailList.get(0).getDayType(),"???????????????",leaveAbolish.getId());
				}else{
					attnStatisticsUtil.calWorkHours(user.getEmployee().getCnName(),employee, leaveDetailList.get(0).getStartTime(), actualEndDate,RunTask.RUN_CODE_130, String.valueOf(leaveDetailList.get(0).getLeaveType()), leaveHours,0,"???????????????",leaveAbolish.getId());
				}
			}
		}else{
			logger.error("???????????????"+processInstanceId+"???????????????????????????");
		}
	}
	
	public void saveRecordDetail(Long recordId,Long baseEmpLeaveId,Double days,
			User user){
		LeaveRecordDetail recordDetail = new LeaveRecordDetail();
		recordDetail.setLeaveRecordId(recordId);
		recordDetail.setBaseEmpLeaveId(baseEmpLeaveId);
		recordDetail.setDays(-days);
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
	
	public Map<String, Object> calculatedLeaveDays(Long empId,String startTime1,String startTime2,String endTime1,String endTime2,String leaveType){
		Employee emp = employeeService.getById(empId);
		
		Map<Long,ClassSetting> csMap = new HashMap<Long, ClassSetting>();
		List<ClassSetting> csList = classSettingService.getList();
		for (ClassSetting classSetting : csList) {
			csMap.put(classSetting.getId(), classSetting);
		}
		Map<String,Object> map = new HashMap<String, Object>();
		//????????????
		Date dayStart = DateUtils.parse(startTime1,DateUtils.FORMAT_SHORT);
		//????????????
		Date dayEnd = DateUtils.parse(endTime1,DateUtils.FORMAT_SHORT);
		String beginData = "";
		String endData = "";
		int length = DateUtils.getIntervalDays(dayStart, dayEnd) + 1;
		if(OaCommon.getLeaveMap().containsKey(leaveType)) {
			beginData = startTime1;
			endData = endTime1;
		} 
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try{
			if(StringUtils.isBlank(startTime1)||StringUtils.isBlank(startTime2)||
					StringUtils.isBlank(endTime1)||StringUtils.isBlank(endTime2)){
				map.put("message","????????????????????????");
				map.put("success",false);
				return map;
			}
			double leaveDays = 0;
			double leaveHours = 0;
			
			boolean isEndTime = true;
			List<String> datas = new ArrayList<String>();
			do{
				datas.add(DateUtils.format(dayStart, DateUtils.FORMAT_SHORT));
				dayStart = DateUtils.addDay(dayStart, 1);
				if(DateUtils.getIntervalDays(dayStart, dayEnd) < 0) {
					isEndTime = false;
				}
			} while(isEndTime);
			if(OaCommon.getLeaveMap().containsKey(leaveType)) {
				leaveDays = length;
			}else{
				resultMap = employeeClassService.getEmployeeClassSetting(emp,datas);
				beginData = (String) resultMap.get("beginData");
				endData = (String) resultMap.get("endData");
				for (Map.Entry<String,Object> entry : resultMap.entrySet()) {
					if(!entry.getKey().equals("beginData") && !entry.getKey().equals("endData")&&!entry.getKey().equals(startTime1)&&!entry.getKey().equals(endTime1)) {
						EmployeeClass result_e = (EmployeeClass) resultMap.get(entry.getKey());
						if(result_e != null) {
							if(result_e.getClassDate() != null) {
								boolean standard = ("09:00".equals(DateUtils.format(result_e.getStartTime(), "HH:mm"))&&"18:00".equals(DateUtils.format(result_e.getEndTime(), "HH:mm")))?true:false;
								boolean standard1 = (result_e.getMustAttnTime()==8)?true:false;
								boolean standard2 = (result_e.getMustAttnTime()>=16)?true:false;
								boolean standard3 = (result_e.getMustAttnTime()>=10&&result_e.getMustAttnTime()<16)?true:false;
								boolean standard4 = (result_e.getMustAttnTime()<8)?true:false;
								if(standard){
									if("5".equals(leaveType)){
										leaveHours = leaveHours + 8;
									}
									leaveDays = leaveDays + 1;
								}else if(standard1){
									if("5".equals(leaveType)){
										leaveHours = leaveHours + 8;
									}
									leaveDays = leaveDays + 1;
								}else if(standard2){
									if("5".equals(leaveType)){
										leaveHours = leaveHours + 16;
									}
									leaveDays = leaveDays + 2;
								}else if(standard3){
									if("5".equals(leaveType)){
										leaveHours = leaveHours + 12;
									}
									leaveDays = leaveDays + 1.5;
								}else if(standard4){
									if("5".equals(leaveType)){
										leaveHours = leaveHours + result_e.getMustAttnTime();
									}
									leaveDays = leaveDays + 1;
								}
							}
						}
					}
				}
			}
			
			if(beginData.equals(startTime1)) {
				EmployeeClass classSetting = (EmployeeClass) resultMap.get(beginData);
				Boolean isCommon = false;
			    if(classSetting != null){
					isCommon = true;
				}
				if(isCommon) {
					boolean standard = ("09:00".equals(DateUtils.format(classSetting.getStartTime(), "HH:mm"))&&"18:00".equals(DateUtils.format(classSetting.getEndTime(), "HH:mm")))?true:false;
					boolean standard1 = (classSetting.getMustAttnTime()==8)?true:false;
					boolean standard2 = (classSetting.getMustAttnTime()>=16)?true:false;
					boolean standard3 = (classSetting.getMustAttnTime()>=10&&classSetting.getMustAttnTime()<16)?true:false;
					boolean standard4 = (classSetting.getMustAttnTime()<8)?true:false;
					if(standard){
						if(length == 1){
							if((Integer.valueOf(startTime2).intValue()==9&&Integer.valueOf(endTime2).intValue()==14)||(Integer.valueOf(startTime2).intValue()==9&&Integer.valueOf(endTime2).intValue()==13)){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 3;
									leaveDays = leaveDays + 0.5;
								}else{
									leaveDays = leaveDays + 0.5;
								}
							}else if(Integer.valueOf(startTime2).intValue()==12&&Integer.valueOf(endTime2).intValue()==18){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 5;
								}
								leaveDays = leaveDays + 0.5;
							}else if(Integer.valueOf(startTime2).intValue()==9&&Integer.valueOf(endTime2).intValue()==18){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 8;
								}
								leaveDays = leaveDays + 1;
							}
						}else{
							if(Integer.valueOf(startTime2).intValue()==9){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 8;
								}
								leaveDays = leaveDays + 1;
							}else if(Integer.valueOf(startTime2).intValue()==12){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 5;
								}
								leaveDays = leaveDays + 0.5;
							}
						}
					}else if(standard1){
						if(length == 1){
							if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getStartTime(), "HH")).intValue()
									&& Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getEndTime(),-300), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 4;
								}
								leaveDays = leaveDays + 0.5;
							}else if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 300), "HH")).intValue()
									&& Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 4;
								}
								leaveDays = leaveDays + 0.5;
							}else if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getStartTime(), "HH")).intValue()
									&& Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 8;
								}
								leaveDays = leaveDays + 1;
							}
						}else{
							if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getStartTime(), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 8;
								}
								leaveDays = leaveDays + 1;
							}else if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 300), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 4;
								}
								leaveDays = leaveDays + 0.5;
							}
						}
					}else if(standard2){
						if(length == 1){
							if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getStartTime(), "HH")).intValue()
									&& Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 16;
								}
								leaveDays = leaveDays + 2;
							}else if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(),240), "HH")).intValue()
									&& Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 12;
								}
								leaveDays = leaveDays + 1.5;
							}else if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 480), "HH")).intValue()
									&& Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 8;
								}
								leaveDays = leaveDays + 1;
							}else if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 720), "HH")).intValue()
									&& Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 4;
								}
								leaveDays = leaveDays + 0.5;
							}
						}else{
							if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getStartTime(), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 16;
								}
								leaveDays = leaveDays + 2;
							}else if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(),240), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 12;
								}
								leaveDays = leaveDays + 1.5;
							}else if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 480), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 8;
								}
								leaveDays = leaveDays + 1;
							}else if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 720), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 4;
								}
								leaveDays = leaveDays + 0.5;
							}
						}
					}else if(standard3){
						if(length == 1){
							if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getStartTime(), "HH")).intValue()
									&& Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 12;
								}
								leaveDays = leaveDays + 1.5;
							}else if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 240), "HH")).intValue()
									&& Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 8;
								}
								leaveDays = leaveDays + 1;
							}else if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 480), "HH")).intValue()
									&& Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 4;
								}
								leaveDays = leaveDays + 0.5;
							}
						}else{
							if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getStartTime(), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 12;
								}
								leaveDays = leaveDays + 1.5;
							}else if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 240), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 8;
								}
								leaveDays = leaveDays + 1;
							}else if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 480), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 4;
								}
								leaveDays = leaveDays + 0.5;
							}
						}
					}else if(standard4){
						if("5".equals(leaveType)){
							leaveHours = leaveHours + classSetting.getMustAttnTime();
						}
						leaveDays = leaveDays + 1;
					}
				}
			}
			
			if(length >1 && endData.equals(endTime1)) {
				EmployeeClass classSetting = (EmployeeClass) resultMap.get(endData);
				Boolean isCommon = false; 
				if(classSetting != null){
					isCommon = true;
				}
				if(isCommon) {
					boolean standard = ("09:00".equals(DateUtils.format(classSetting.getStartTime(), "HH:mm"))&&"18:00".equals(DateUtils.format(classSetting.getEndTime(), "HH:mm")))?true:false;
					boolean standard1 = (classSetting.getMustAttnTime()==8)?true:false;
					boolean standard2 = (classSetting.getMustAttnTime()>=16)?true:false;
					boolean standard3 = (classSetting.getMustAttnTime()>=10&&classSetting.getMustAttnTime()<16)?true:false;
					boolean standard4 = (classSetting.getMustAttnTime()<8)?true:false;
					if(standard){
						if(Integer.valueOf(endTime2).intValue()==18){
							if("5".equals(leaveType)){
								leaveHours = leaveHours + 8;
							}
							leaveDays = leaveDays + 1;
						}else if(Integer.valueOf(endTime2).intValue()==14||Integer.valueOf(endTime2).intValue()==13){
							if("5".equals(leaveType)){
								leaveHours = leaveHours + 3;
								leaveDays = leaveDays + 0.5;
							}else{
								leaveDays = leaveDays + 0.5;
							}
						}
					}else if(standard1){
						if(Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()){
							if("5".equals(leaveType)){
								leaveHours = leaveHours + 8;
							}
							leaveDays = leaveDays + 1;
						}else if(Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getEndTime(), -300), "HH")).intValue()){
							if("5".equals(leaveType)){
								leaveHours = leaveHours + 4;
							}
							leaveDays = leaveDays + 0.5;
						}
					}else if(standard2){
						if(Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()){
							if("5".equals(leaveType)){
								leaveHours = leaveHours + 16;
							}
							leaveDays = leaveDays + 2;
						}
					}else if(standard3){
						if(Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()){
							if("5".equals(leaveType)){
								leaveHours = leaveHours + 12;
							}
							leaveDays = leaveDays + 1.5;
						}
					}else if(standard4){
						if("5".equals(leaveType)){
							leaveHours = leaveHours + classSetting.getMustAttnTime();
						}
						leaveDays = leaveDays + 1;
					}
				} 
			}
			map.put("leaveDays",leaveDays);
			map.put("leaveHours",leaveHours);
			map.put("success", true);
		}catch(Exception e){
			logger.error(emp.getCnName()+"??????-???????????????"+e.toString());
			map.put("success",false);
		}
		return map;
	}

	@Override
	public Map<String, String> getActualEndTime(Long employeeId,
			int leaveType, Date startTime, Date endTime) {
		String startT = "";
		String endDatT = "";
		Map<String,String> result = new HashMap<String,String>();
		
		if(ConfigConstants.LEAVE_TYPE_1.intValue()==leaveType||ConfigConstants.LEAVE_TYPE_2.intValue()==leaveType
				||ConfigConstants.LEAVE_TYPE_5.intValue()==leaveType||ConfigConstants.LEAVE_TYPE_11.intValue()==leaveType
				||ConfigConstants.LEAVE_TYPE_12.intValue()==leaveType){//??????????????????????????????????????????????????????????????????
			if(DateUtils.format(startTime, DateUtils.FORMAT_LONG_MM).equals(DateUtils.format(endTime, DateUtils.FORMAT_LONG_MM))){
				result.put("endTime", DateUtils.format(startTime, DateUtils.FORMAT_LONG_MM));
				return result;
			}
			//?????????????????????????????????
			EmployeeClass employeeClass = new EmployeeClass();
			employeeClass.setEmployId(employeeId);
			employeeClass.setClassDate(endTime);
			EmployeeClass empClass = employeeClassService.getEmployeeClassSetting(employeeClass);
			//???????????????????????????????????????
	    	if(empClass!=null){
	    		if(DateUtils.format(endTime, "HH:mm").equals(DateUtils.format(empClass.getStartTime(), "HH:mm"))){
	    			//?????????????????????????????????????????????????????????????????????????????????????????????????????????
	    			Date endDate = DateUtils.addDay(endTime, -1);
					employeeClass.setClassDate(endDate);
					EmployeeClass empClassBefore = employeeClassService.getEmployeeClassSetting(employeeClass);
					while(true){
						if(empClassBefore!=null){
							break;
						}
						endDate = DateUtils.addDay(endDate, -1);
						employeeClass.setClassDate(endDate);
						empClassBefore = employeeClassService.getEmployeeClassSetting(employeeClass);
					}
					endDatT = DateUtils.format(empClassBefore.getClassDate(), DateUtils.FORMAT_SHORT)+" "+DateUtils.format(empClassBefore.getEndTime(), "HH:mm");
	    		}else{
	    			boolean standard = ("09:00".equals(DateUtils.format(empClass.getStartTime(), "HH:mm"))&&"18:00".equals(DateUtils.format(empClass.getEndTime(), "HH:mm")))?true:false;
	    			boolean standard1 = (empClass.getMustAttnTime()==8)?true:false;
	    			boolean standard2 = (empClass.getMustAttnTime()>=16)?true:false;
	    			boolean standard3 = (empClass.getMustAttnTime()>=10&&empClass.getMustAttnTime()<16)?true:false;
	    	        if(standard){
	    	        	//?????????????????????????????????????????????????????????????????????
		    		    if(ConfigConstants.LEAVE_TYPE_5.intValue()==leaveType){//?????????9-18??????????????????????????????9-13???????????????????????????12-18??????
		    				endDatT = DateUtils.format(empClass.getClassDate(), DateUtils.FORMAT_SHORT)+" "+DateUtils.format(DateUtils.addHour(empClass.getStartTime(),4), "HH:mm");
		    			}else{//????????????????????????????????????9-18??????????????????????????????9-14???????????????????????????12-18??????
		    				endDatT = DateUtils.format(empClass.getClassDate(), DateUtils.FORMAT_SHORT)+" "+DateUtils.format(DateUtils.addHour(empClass.getStartTime(),5), "HH:mm");
		    			}
	    	        }else if(standard1){
	    	        	endDatT = DateUtils.format(empClass.getClassDate(), DateUtils.FORMAT_SHORT)+" "+DateUtils.format(DateUtils.addHour(empClass.getStartTime(),4), "HH:mm");
	    	        }else if(standard2){
	    	        	endDatT = DateUtils.format(endTime, DateUtils.FORMAT_LONG_MM);
	    	        }else if(standard3){
	    	        	endDatT = DateUtils.format(endTime, DateUtils.FORMAT_LONG_MM);
	    	        }
	    		}
	    	}else{
	    		
	    	}
		}else if(ConfigConstants.LEAVE_TYPE_3.intValue()==leaveType||ConfigConstants.LEAVE_TYPE_6.intValue()==leaveType
				||ConfigConstants.LEAVE_TYPE_7.intValue()==leaveType||ConfigConstants.LEAVE_TYPE_8.intValue()==leaveType
				||ConfigConstants.LEAVE_TYPE_9.intValue()==leaveType||ConfigConstants.LEAVE_TYPE_4.intValue()==leaveType){//???????????????????????????????????????????????????(?????????????????????)
			if(DateUtils.format(startTime, DateUtils.FORMAT_LONG_MM).equals(DateUtils.format(endTime, DateUtils.FORMAT_LONG_MM))){
				result.put("endTime", DateUtils.format(startTime, DateUtils.FORMAT_SHORT));
				return result;
			}
			Date endDate = DateUtils.addDay(endTime, -1);
			endDatT = DateUtils.format(endDate, DateUtils.FORMAT_SHORT);
		}else if(ConfigConstants.LEAVE_TYPE_10.intValue()==leaveType){//?????????????????????????????????
			if(DateUtils.format(startTime, DateUtils.FORMAT_LONG_MM).equals(DateUtils.format(endTime, DateUtils.FORMAT_LONG_MM))){
				result.put("endTime", DateUtils.format(startTime, DateUtils.FORMAT_SHORT));
				return result;
			}
			Date endDate = DateUtils.addDay(endTime, -1);
			EmployeeClass employeeClass = new EmployeeClass();
			employeeClass.setEmployId(employeeId);
			employeeClass.setClassDate(endDate);
			EmployeeClass empClassBefore = employeeClassService.getEmployeeClassSetting(employeeClass);
			while(true){
				if(empClassBefore!=null){
					break;
				}
				endDate = DateUtils.addDay(endDate, -1);
				employeeClass.setClassDate(endDate);
				empClassBefore = employeeClassService.getEmployeeClassSetting(employeeClass);
			}
			endDatT = DateUtils.format(endDate, DateUtils.FORMAT_SHORT);
		}
    	result.put("endTime", endDatT);
		return result;
	}

	@Override
	public Map<String, String> checkStartTime(Long leaveId,Date startTime,Date endTime) throws Exception {
		    if(leaveId==null){
				throw new OaException("??????????????????????????????");
			}
		    //?????????????????????
		    EmpApplicationLeave leave = empApplicationLeaveMapper.getById(leaveId);
		    if(leave==null){
		    	throw new OaException("??????????????????????????????");
		    }
		    //??????????????????
		    List<EmpApplicationLeaveDetail> detailList = empApplicationLeaveDetailMapper.getListByCondition(leaveId);
		  
		    if(detailList==null||detailList.size()<=0){
		    	throw new OaException("??????????????????????????????");
		    }
			//?????????????????????????????????????????????????????????????????????>9,?????????????????????
		    if(ConfigConstants.LEAVE_TYPE_1.intValue()==detailList.get(0).getLeaveType().intValue()
		    		||ConfigConstants.LEAVE_TYPE_2.intValue()==detailList.get(0).getLeaveType().intValue()
					||ConfigConstants.LEAVE_TYPE_5.intValue()==detailList.get(0).getLeaveType().intValue()
					||ConfigConstants.LEAVE_TYPE_11.intValue()==detailList.get(0).getLeaveType().intValue()
					||ConfigConstants.LEAVE_TYPE_12.intValue()==detailList.get(0).getLeaveType().intValue()){
		    	EmployeeClass employeeClass = new EmployeeClass();
				employeeClass.setEmployId(leave.getEmployeeId());
				employeeClass.setClassDate(startTime);
				EmployeeClass empClass = employeeClassService.getEmployeeClassSetting(employeeClass);
		    	if(empClass!=null){
		    		if(empClass.getMustAttnTime()>9){
		    			if(DateUtils.format(startTime, DateUtils.FORMAT_SHORT).equals(DateUtils.format(leave.getStartTime(), DateUtils.FORMAT_SHORT))){
		    				if(!DateUtils.format(startTime, DateUtils.FORMAT_LONG).equals(DateUtils.format(leave.getStartTime(), DateUtils.FORMAT_LONG))){
		    					throw new OaException("????????????????????????"+DateUtils.format(leave.getStartTime(), DateUtils.FORMAT_LONG_MM)+"!");
		    				}
		    			}else{
		    				if(!DateUtils.format(startTime, DateUtils.FORMAT_HH_MM).equals(DateUtils.format(employeeClass.getStartTime(), DateUtils.FORMAT_HH_MM))){
		    					throw new OaException("????????????????????????"+DateUtils.format(employeeClass.getClassDate(), DateUtils.FORMAT_SHORT)+" "+DateUtils.format(employeeClass.getStartTime(), DateUtils.FORMAT_HH_MM)+"!");
		    				}
		    			}
		    		}
		    	}
		    }
		Map<String,String> result =  getActualEndTime(leave.getEmployeeId(),
				detailList.get(0).getLeaveType(), startTime, endTime);
		return result;
	}

	@Override
	public PageModel<EmpApplicationLeaveAbolish> getReportPageList(
			EmpApplicationLeaveAbolish leave) {
		leave.setDepartId(leave.getFirstDepart());
		if(leave.getApplyStartDate()!=null){
			leave.setApplyStartDate(DateUtils.parse(DateUtils.format(leave.getApplyStartDate(), DateUtils.FORMAT_SHORT)+" 00:00:01", DateUtils.FORMAT_LONG));
		}
		if(leave.getApplyEndDate()!=null){
			leave.setApplyEndDate(DateUtils.parse(DateUtils.format(leave.getApplyEndDate(), DateUtils.FORMAT_SHORT)+" 23:59:59", DateUtils.FORMAT_LONG));
		}
		
		int page = leave.getPage() == null ? 0 : leave.getPage();
		int rows = leave.getRows() == null ? 0 : leave.getRows();
		
		PageModel<EmpApplicationLeaveAbolish> pm = new PageModel<EmpApplicationLeaveAbolish>();
		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
		if(deptDataByUserList==null || deptDataByUserList.size()==0){
			pm.setPageNo(0);
			pm.setPageSize(0);
			pm.setTotal(0);
			leave.setOffset(pm.getOffset());
			leave.setLimit(pm.getLimit());
			pm.setRows(new ArrayList<EmpApplicationLeaveAbolish>());
			return pm;
		}else{
			leave.setCurrentUserDepart(deptDataByUserList);
			List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(currentUser.getEmployeeId());//????????????
			if(subEmployeeIdList!=null&&subEmployeeIdList.size()>0){
				leave.setSubEmployeeIdList(subEmployeeIdList);
			}
			pm.setPageNo(page);
			pm.setPageSize(rows);
			Integer total = empApplicationLeaveAbolishMapper.getReportCount(leave);
			pm.setTotal(total);
			
			leave.setOffset(pm.getOffset());
			leave.setLimit(pm.getLimit());
			
			List<EmpApplicationLeaveAbolish> roles = empApplicationLeaveAbolishMapper.getReportPageList(leave);
			
			for(EmpApplicationLeaveAbolish og:roles){
				//????????????   ???????????????????????????
				if(!(og.getLeaveDays()==null)){
					if(og.getLeaveDays().equals(og.getLeaveDaysDetail())){
						og.setStartTimeDetail(null);
						og.setActualEndTime(null);
					}else{
						Map<String,String> map = 
								this.getActualEndTime(og.getEmployeeId(), og.getLeaveType(), og.getStartTimeDetail(), og.getStartTime());		
						og.setActualEndTime(map.get("endTime"));
					}
				}
				//??????3?????????????????????????????????????????????????????????   11
				try{
					ViewTaskInfoTbl taskInfo = null;
					if(og.getLeaveDays()>=3){
						taskInfo = viewTaskInfoService.getFirstAuditUser(og.getProcessInstanceId(),ConfigConstants.LEAVE_KEY,true);
					}else{
						taskInfo = viewTaskInfoService.getFirstAuditUser(og.getProcessInstanceId(),ConfigConstants.LEAVE_KEY,false);
					}
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
	@Transactional(rollbackFor = Exception.class)
	public void completeTaskByAdmin(HttpServletRequest request,String instanceId, String comment,
		String commentType, User user, Task task) throws Exception {
		
		logger.info("???????????????completeTaskByAdmin:start??????");
		Long time1 = System.currentTimeMillis();	
		logger.info("???????????????completeTaskByAdmin??????:instanceId="+instanceId+";comment="+comment+";commentType="+commentType
			+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));
		
		EmpApplicationLeaveAbolish leaveAbolish = queryByProcessInstanceId(instanceId);
		if(leaveAbolish==null){
			logger.error("???????????????"+instanceId+"?????????????????????????????????");
			throw new OaException("??????????????????????????????");
		}
		
		EmpApplicationLeave leave = empApplicationLeaveMapper.getById(leaveAbolish.getLeaveId());
		if(null == task) {
			throw new OaException("??????????????????????????????,????????????????????????");
		}
		boolean type = StringUtils.startsWithIgnoreCase(task.getTaskDefinitionKey(), "end_");
		// ??????
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
		leaveAbolish.setApprovalStatus(approvalStatus);
		leaveAbolish.setNodeCode(task.getName());
		empApplicationLeaveAbolishMapper.updateById(leaveAbolish);
		//updateById(leave);
		//????????????????????????????????????assignee,??????????????????????????????????????????
		if(StringUtils.isBlank(task.getAssignee())) {
			activitiServiceImpl.claimTask(task.getId(), String.valueOf(userService.getCurrentUser().getEmployeeId()));
		}
		sendMailService.sendCommentMail(employeeService.getByEmpId(leave.getEmployeeId()).get(0).getEmail(), "???????????????", task.getName()+"-"+user.getEmployee().getCnName(), approvalStatus);
		//-----------------start-----------------------????????????????????????
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
		isAdmin.put("isAdmin", true);
		activitiServiceImpl.completeTask(task.getId(), comment,isAdmin,commentType);
		
		
		Long time2 = System.currentTimeMillis();
		logger.info("???????????????completeTaskByAdmin:use time="+(time2-time1));
		logger.info("???????????????completeTaskByAdmin:end??????");
		
	}

	@Override
	public PageModel<EmpApplicationLeaveAbolish> myLeaveTaskList(
			EmpApplicationLeaveAbolish leave) {
		if(leave.getApplyStartDate()!=null){
			leave.setApplyStartDate(DateUtils.parse(DateUtils.format(leave.getApplyStartDate(), DateUtils.FORMAT_SHORT)+" 00:00:01", DateUtils.FORMAT_LONG));
		}
		if(leave.getApplyEndDate()!=null){
			leave.setApplyEndDate(DateUtils.parse(DateUtils.format(leave.getApplyEndDate(), DateUtils.FORMAT_SHORT)+" 23:59:59", DateUtils.FORMAT_LONG));
		}
		
		int page = leave.getPage() == null ? 0 : leave.getPage();
		int rows = leave.getRows() == null ? 0 : leave.getRows();
		
		PageModel<EmpApplicationLeaveAbolish> pm = new PageModel<EmpApplicationLeaveAbolish>();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		Integer total = empApplicationLeaveAbolishMapper.myLeaveTaskListCount(leave);
		pm.setTotal(total);
		
		leave.setOffset(pm.getOffset());
		leave.setLimit(pm.getLimit());
		
		List<EmpApplicationLeaveAbolish> roles = empApplicationLeaveAbolishMapper.myLeaveTaskList(leave);
		for(EmpApplicationLeaveAbolish og:roles){
			//????????????   ???????????????????????????
			if(!(og.getLeaveDays()==null)){
				if(og.getLeaveDays().equals(og.getLeaveDaysDetail())){
					og.setStartTimeDetail(null);
					og.setActualEndTime(null);
				}else{
					Map<String,String> map = 
							this.getActualEndTime(og.getEmployeeId(), og.getLeaveType(), og.getStartTimeDetail(), og.getStartTime());		
					og.setActualEndTime(map.get("endTime"));
				}
			}
		}	
		pm.setRows(roles);
		return pm;
	}

	@Override
	public HSSFWorkbook exportExcel(EmpApplicationLeaveAbolish leave) {
		// TODO Auto-generated method stub
		List<Map<String,Object>> sMapList = new ArrayList<Map<String,Object>>();
		if(leave.getApplyStartDate()!=null){
			leave.setApplyStartDate(DateUtils.parse(DateUtils.format(leave.getApplyStartDate(), DateUtils.FORMAT_SHORT)+" 00:00:01", DateUtils.FORMAT_LONG));
		}
		if(leave.getApplyEndDate()!=null){
			leave.setApplyEndDate(DateUtils.parse(DateUtils.format(leave.getApplyEndDate(), DateUtils.FORMAT_SHORT)+" 23:59:59", DateUtils.FORMAT_LONG));
		}
		List<EmpApplicationLeaveAbolish> list = empApplicationLeaveAbolishMapper.getReportPageList(leave);
		for(EmpApplicationLeaveAbolish og:list){
			
			
			//????????????   ???????????????????????????
			if(!(og.getLeaveDays()==null)){
				if(og.getLeaveDays().equals(og.getLeaveDaysDetail())){
					og.setStartTimeDetail(null);
					og.setActualEndTime(null);
				}else{
					Map<String,String> map = 
							this.getActualEndTime(og.getEmployeeId(), og.getLeaveType(), og.getStartTimeDetail(), og.getStartTime());		
					og.setActualEndTime(map.get("endTime"));
				}
			}
			
			//??????3?????????????????????????????????????????????????????????
			try{
				ViewTaskInfoTbl taskInfo = null;
				if(og.getLeaveDays()>=3){
					taskInfo = viewTaskInfoService.getFirstAuditUser(og.getProcessInstanceId(),ConfigConstants.LEAVE_KEY,true);
				}else{
					taskInfo = viewTaskInfoService.getFirstAuditUser(og.getProcessInstanceId(),ConfigConstants.LEAVE_KEY,false);
				}
				og.setAuditUser("");
				if(null != taskInfo){
					og.setAuditUser(taskInfo.getAssigneeName());
				}
			}catch(Exception e){
				og.setAuditUser(" ");
			}
		}
		if(list!=null&&list.size()>0) {
			for (EmpApplicationLeaveAbolish re : list) {
				//????????????
				Map<String,Object> sdoMap = new HashMap<String, Object>();
				sdoMap.put("code",re.getCode());
				sdoMap.put("cnName",re.getCnName());
				sdoMap.put("departName",re.getDepartName());
				sdoMap.put("positionName",re.getPositionName());
				sdoMap.put("workType",re.getWorkType());
				String leaveType = "";
				if(re.getLeaveType().intValue()==1){
					leaveType = "??????";
				}else if(re.getLeaveType().intValue()==2){
					leaveType = "??????";
				}else if(re.getLeaveType().intValue()==3){
					leaveType = "??????";
				}else if(re.getLeaveType().intValue()==4){
					leaveType = "?????????";
				}else if(re.getLeaveType().intValue()==5){
					leaveType = "??????";
				}else if(re.getLeaveType().intValue()==6){
					leaveType = "?????????";
				}else if(re.getLeaveType().intValue()==7){
					leaveType = "??????";
				}else if(re.getLeaveType().intValue()==8){
					leaveType = "?????????";
				}else if(re.getLeaveType().intValue()==9){
					leaveType = "?????????";
				}else if(re.getLeaveType().intValue()==10){
					leaveType = "??????";
				}else if(re.getLeaveType().intValue()==11){
					leaveType = "??????";
				}else if(re.getLeaveType().intValue()==12){
					leaveType = "??????";
				}
				sdoMap.put("leaveType",leaveType);
				sdoMap.put("startTimeDetail",DateUtils.format(re.getStartTimeDetail(), DateUtils.FORMAT_LONG));
				sdoMap.put("endTimeDetail",re.getActualEndTime());
				if(re.getLeaveType().intValue()==5){
					sdoMap.put("leaveDaysDetail",(re.getLeaveDaysDetail()==null?0:re.getLeaveDaysDetail())-(re.getLeaveDays()==null?0:re.getLeaveDays())+"("+((re.getLeaveHoursDetail()==null?0:re.getLeaveHoursDetail())-(re.getLeaveHours()==null?0:re.getLeaveHours()))+"??????)");
				}else{
					sdoMap.put("leaveDaysDetail",(re.getLeaveDaysDetail()==null?0:re.getLeaveDaysDetail())-(re.getLeaveDays()==null?0:re.getLeaveDays()));
				}
				
				sdoMap.put("startTime",DateUtils.format(re.getStartTime(), DateUtils.FORMAT_LONG));
				sdoMap.put("endTime",DateUtils.format(re.getEndTime(), DateUtils.FORMAT_LONG));
				if(re.getLeaveType().intValue()==5){
					sdoMap.put("leaveDays",re.getLeaveDays()+"("+re.getLeaveHours()+"??????)");
				}else{
					sdoMap.put("leaveDays",re.getLeaveDays());
				}
				
				sdoMap.put("createTime",DateUtils.format(re.getCreateTime(), DateUtils.FORMAT_LONG));
				sdoMap.put("auditUser",re.getAuditUser());
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
				sMapList.add(sdoMap);
			}
		}
		String[] keys={"code", "cnName", "departName", "positionName","workType","leaveType","startTimeDetail", "endTimeDetail", "leaveDaysDetail","startTime", "endTime", "leaveDays","createTime","auditUser","approvalStatus"};
		String[] titles={"????????????", "????????????", "??????","????????????","?????????", "????????????", "???????????????????????????", "???????????????????????????", "????????????","??????????????????", "??????????????????", "????????????", "????????????", "?????????","??????"}; 
		return ExcelUtil.exportExcel(sMapList, keys, titles, "??????????????????.xls");
	}	
}
