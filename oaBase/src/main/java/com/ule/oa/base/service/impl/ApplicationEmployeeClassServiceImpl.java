package com.ule.oa.base.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ule.oa.base.cache.ConfigCacheManager;
import com.ule.oa.base.mapper.ApplicationEmployeeClassDetailMapper;
import com.ule.oa.base.mapper.ApplicationEmployeeClassMapper;
import com.ule.oa.base.mapper.ClassSettingMapper;
import com.ule.oa.base.mapper.EmployeeClassMapper;
import com.ule.oa.base.mapper.EmployeeDutyMapper;
import com.ule.oa.base.mapper.EmployeeMapper;
import com.ule.oa.base.mapper.ScheduleGroupMapper;
import com.ule.oa.base.po.AnnualVacation;
import com.ule.oa.base.po.ApplicationEmployeeClass;
import com.ule.oa.base.po.ApplicationEmployeeClassDetail;
import com.ule.oa.base.po.ClassSetting;
import com.ule.oa.base.po.CompanyConfig;
import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.po.EmployeeDuty;
import com.ule.oa.base.po.Position;
import com.ule.oa.base.po.ScheduleGroup;
import com.ule.oa.base.po.TransNormal;
import com.ule.oa.base.po.User;
import com.ule.oa.base.po.tbl.ViewTaskInfoTbl;
import com.ule.oa.base.service.AnnualVacationService;
import com.ule.oa.base.service.ApplicationEmployeeClassService;
import com.ule.oa.base.service.CompanyConfigService;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.PositionService;
import com.ule.oa.base.service.TransNormalService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.service.ViewTaskInfoService;
import com.ule.oa.base.util.SendMailUtil;
import com.ule.oa.base.vo.TaskVO;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.ExcelUtil;
import com.ule.oa.common.utils.PageModel;
import com.ule.oa.common.utils.http.IPUtils;

/**
 * @ClassName: ???????????????
 * @Description: ???????????????
 * @author yangjie
 * @date 2017???8???31???
 */
@Service
public class ApplicationEmployeeClassServiceImpl implements ApplicationEmployeeClassService{
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private ApplicationEmployeeClassMapper applicationEmployeeClassMapper;
	@Resource
	private ApplicationEmployeeClassDetailMapper applicationEmployeeClassDetailMapper;
	@Resource
	private EmployeeMapper employeeMapper;
	@Autowired
	private EmployeeClassMapper employeeClassMapper;
	@Autowired
	private AnnualVacationService annualVacationService;
	@Resource
	private CompanyConfigService companyConfigService;
	@Resource
	private ConfigService configService;
	@Resource
	private EmployeeService employeeService;
	@Autowired
	private PositionService positionService;
	@Autowired
	private EmployeeDutyMapper employeeDutyMapper;
	@Autowired
	private ClassSettingMapper classSettingMapper;
	@Autowired
	private DepartService departService;
	@Autowired
	private ActivitiServiceImpl activitiServiceImpl;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private TaskService taskService;
	@Resource
	private ConfigCacheManager configCacheManager;
	@Autowired
	private UserService userService;
	@Autowired
	private ViewTaskInfoService viewTaskInfoService;
	@Autowired
	private ScheduleGroupMapper scheduleGroupMapper;
	@Autowired
	private TransNormalService transNormalService;
	
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addLastMonth(User user) throws Exception{
		//???????????????????????????
		List<ScheduleGroup> schedulerList = scheduleGroupMapper.getListByScheduler(user.getEmployee().getId());
		if(schedulerList==null || schedulerList.size() <=0){
			throw new OaException("?????????????????????");
		}
		Long groupId = schedulerList.get(0).getId();//????????????ID
		Long departId = schedulerList.get(0).getDepartId();//?????????????????????id
		//???????????????????????????????????????
		ApplicationEmployeeClass condition = new ApplicationEmployeeClass();
		condition.setClassMonth(DateUtils.getFirstDay(DateUtils.addMonth(new Date(),1)));
		condition.setDepartId(departId);
		condition.setGroupId(groupId);
		List<ApplicationEmployeeClass> list = applicationEmployeeClassMapper.getByCondition(condition);
		if(list!=null&&list.size()>0){
			throw new OaException("???????????????????????????");
		}else{
			//????????????????????????????????????
			List<Employee> employeeList = scheduleGroupMapper.getAllGroupEmp(groupId,null,condition.getClassMonth());
			//????????????????????????
			Depart depart = departService.getById(departId);
			Long workTypeId = null;
			Long whetherScheduleId = null;
			CompanyConfig companyConfigConditon = new CompanyConfig();//?????????????????????
			companyConfigConditon.setCode("typeOfWork");//????????????
			List<CompanyConfig> workTypeList = companyConfigService.getListByCondition(companyConfigConditon);
			for(CompanyConfig companyConfig:workTypeList){
				if("standard".equals(companyConfig.getDisplayCode())){
					workTypeId = companyConfig.getId();
					break;
				}
			}
			Config configCondition = new Config();
			configCondition.setCode("whetherScheduling");//????????????
			List<Config> whetherSchedulingList = configService.getListByCondition(configCondition);
            for(Config config:whetherSchedulingList){
				if("yes".equals(config.getDisplayCode())){
					whetherScheduleId = config.getId();
					break;
				}
			}
            List<Employee> match = new ArrayList<Employee>();
			if(employeeList!=null&&employeeList.size()>0){
				int count=0;
				//????????????????????????????????????????????????
				for(Employee employee:employeeList){
					if(employee.getQuitTime()!=null&&employee.getQuitTime().getTime()<DateUtils.getFirstDay(DateUtils.addMonth(new Date(),1)).getTime()){
						continue;
					}
					if(employee.getWorkType().equals(workTypeId)&&employee.getWhetherScheduling().equals(whetherScheduleId)){
						match.add(employee);
						count = count+1;
					}
				}
				ApplicationEmployeeClass applicationEmployeeClass = new ApplicationEmployeeClass();
				applicationEmployeeClass.setDepartId(departId);
				applicationEmployeeClass.setGroupId(groupId);
				applicationEmployeeClass.setDepartName(depart.getName());
				applicationEmployeeClass.setClassMonth(DateUtils.getFirstDay(DateUtils.addMonth(new Date(),1)));
				applicationEmployeeClass.setEmployeeNum(count);
				applicationEmployeeClass.setClassEmployeeNum(0);
				applicationEmployeeClass.setClassSettingPerson(user.getEmployee().getCnName());
				applicationEmployeeClass.setVersion(0L);
				applicationEmployeeClass.setDelFlag(0);
				applicationEmployeeClass.setCreateTime(new Date());
				applicationEmployeeClass.setCreateUser(user.getEmployee().getCnName());
				applicationEmployeeClass.setIsMove(0);
				applicationEmployeeClass.setEmployeeId(user.getEmployee().getId());
				applicationEmployeeClassMapper.save(applicationEmployeeClass);				
				//?????????????????????
				Date month_start1 = DateUtils.getFirstDay(DateUtils.addMonth(new Date(),1));
				//????????????????????????
				Date month_end1 = DateUtils.getLastDay(DateUtils.addMonth(new Date(),1));
				boolean isEndTime1 = true;
			    double should_time = 0;
			    do{
			    	if(annualVacationService.judgeWorkOrNot(month_start1)){
			    		should_time += 8;
					}
					month_start1 = DateUtils.addDay(month_start1, 1);
					if(DateUtils.getIntervalDays(month_start1, month_end1) < 0) {
						isEndTime1 = false;
					}
				} while(isEndTime1);
				for(Employee employee:match){
					double shouldTime = should_time;
					List<ApplicationEmployeeClassDetail> applicationEmployeeClassList = new ArrayList<ApplicationEmployeeClassDetail>();
					//?????????????????????
					Date month_start = DateUtils.getFirstDay(DateUtils.addMonth(new Date(),1));
					//????????????????????????
					Date month_end = DateUtils.getLastDay(DateUtils.addMonth(new Date(),1));
					boolean isEndTime = true;
					do{
						//???????????????????????????????????????????????????????????????
						if(employee.getQuitTime()==null&&employee.getFirstEntryTime()!=null&&DateUtils.format(month_start, "yyyy-MM").equals(DateUtils.format(employee.getFirstEntryTime(), "yyyy-MM"))){
							if(employee.getFirstEntryTime().getTime()>month_start.getTime()){
								if(annualVacationService.judgeWorkOrNot(month_start)){
									shouldTime = shouldTime - 8;
								}
							}
						}
						//???????????????????????????????????????????????????????????????
						if(employee.getQuitTime()!=null&&DateUtils.format(month_start, "yyyy-MM").equals(DateUtils.format(employee.getQuitTime(), "yyyy-MM"))){
							if(employee.getQuitTime().getTime()<month_start.getTime()){
								if(annualVacationService.judgeWorkOrNot(month_start)){
									shouldTime = shouldTime - 8;
								}
							}
						}
						ApplicationEmployeeClassDetail applicationEmployeeClassDetail = new ApplicationEmployeeClassDetail();
						applicationEmployeeClassDetail.setAttnApplicationEmployClassId(applicationEmployeeClass.getId());
						applicationEmployeeClassDetail.setClassDate(month_start);
						applicationEmployeeClassDetail.setCompanyId(user.getCompanyId());
						applicationEmployeeClassDetail.setEmployId(employee.getId());
						applicationEmployeeClassDetail.setEmployName(employee.getCnName());
						applicationEmployeeClassDetail.setClassSettingPerson(user.getEmployee().getCnName());
						applicationEmployeeClassDetail.setCreateTime(new Date());
						applicationEmployeeClassDetail.setCreateUser(user.getEmployee().getCnName());
						applicationEmployeeClassDetail.setDelFlag(0);
						applicationEmployeeClassDetail.setIsMove(0);
						applicationEmployeeClassList.add(applicationEmployeeClassDetail);
						month_start = DateUtils.addDay(month_start, 1);
						if(DateUtils.getIntervalDays(month_start, month_end) < 0) {
							isEndTime = false;
						}
					} while(isEndTime);
					for(ApplicationEmployeeClassDetail classDetail:applicationEmployeeClassList){
						classDetail.setShouldTime(shouldTime);
					}
					applicationEmployeeClassDetailMapper.batchSave(applicationEmployeeClassList);
			    }
		   }else{
				   
		   }
		}
   }

	@Override
	public List<ApplicationEmployeeClass> getByCondition(
			ApplicationEmployeeClass applicationEmployeeClass) {
		return applicationEmployeeClassMapper.getByCondition(applicationEmployeeClass);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> submitApprove(Long classdetailId, User user)
			throws OaException {
		
		Map<String,Object> map = new HashMap<String, Object>();
		
		ApplicationEmployeeClass applicationEmployeeClass = new ApplicationEmployeeClass();
		applicationEmployeeClass.setId(classdetailId);
		List<ApplicationEmployeeClass> list = applicationEmployeeClassMapper.getByCondition(applicationEmployeeClass);
		if(list!=null&&list.size()>0){
			
			if(list.get(0).getApprovalStatus()!=null){
				throw new OaException("??????????????????????????????????????????????????????");
			}
			
			//???????????????????????????
			Config configCondition = new Config();
			configCondition.setCode("dutyCommitTimeLimit");
			List<Config> limit = configService.getListByCondition(configCondition);
			int num = Integer.valueOf(limit.get(0).getDisplayCode());
			
			Date lastSubDate = annualVacationService.getWorkingDayPre(num,DateUtils.addDay(list.get(0).getClassMonth(), -1));
			if(DateUtils.getIntervalDays(new Date(),lastSubDate)<0){
				throw new OaException("?????????????????????4?????????????????????");
			}
			
			ApplicationEmployeeClassDetail condition = new ApplicationEmployeeClassDetail();
			condition.setAttnApplicationEmployClassId(classdetailId);
			List<ApplicationEmployeeClassDetail> classHoursList = applicationEmployeeClassDetailMapper.getEmployeeClassHours(condition);
			int i = 0;
			for(ApplicationEmployeeClassDetail classHour:classHoursList){
				if(!(classHour.getMustAttnTime()!=null&&classHour.getMustAttnTime()>=classHour.getShouldTime())){
					i = i+1;
				}
			}
			if(i>0){
				map.put("success", false);
				map.put("fullFlag", true);
				map.put("message", i+"??????????????????????????????????????????");
				return map;
			}
			
			//???????????????????????????????????????
			List<ScheduleGroup> schedulerList = scheduleGroupMapper.getListByScheduler(user.getEmployee().getId());
			if(schedulerList==null || schedulerList.size() <=0){
				map.put("success", false);
				map.put("message","?????????????????????");
				return map;
			}
			Long auditor = schedulerList.get(0).getAuditor();//???????????????
			
			//????????????????????????
			Map<String, Object> variables = new HashMap<>();
			variables.put("approver",auditor+"");
			//???????????????????????????
			ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(ConfigConstants.SCHEDULING_KEY,variables);
			
			//??????????????????
			applicationEmployeeClass.setProcessInstanceId(processInstance.getId());
			applicationEmployeeClass.setApprovalStatus(ConfigConstants.DOING_STATUS);
			applicationEmployeeClass.setUpdateUser(user.getEmployee().getCnName());
			applicationEmployeeClass.setUpdateTime(new Date());
			applicationEmployeeClassMapper.updateById(applicationEmployeeClass);
			
			//-----------------start-----------------------????????????????????????
			ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
			flow.setAssigneeName(user.getEmployee().getCnName());
			flow.setDepartName(user.getDepart().getName());
			flow.setPositionName(user.getPosition().getPositionName());
			flow.setFinishTime(new Date());
			flow.setComment("????????????");
			flow.setProcessId(processInstance.getId());
			flow.setProcessKey(ConfigConstants.SCHEDULING_KEY);
			flow.setStatu(0);
			viewTaskInfoService.save(flow);
			
			//????????????????????????????????????????????????????????????????????????????????????????????????
			if(user.getEmployee().getId().equals(auditor)){
				
				Map<Date,AnnualVacation> vacationMap = annualVacationService.getVacationMap(list.get(0).getClassMonth());
				boolean containLeagl = false;
				if(vacationMap!=null&&vacationMap.size()>0){
					List<ApplicationEmployeeClassDetail> detailList = applicationEmployeeClassDetailMapper.selectByCondition(condition);
					for(ApplicationEmployeeClassDetail de:detailList){
						if(de.getClassSettingId()!=null&&vacationMap.containsKey(de.getClassDate())){
							containLeagl = true;
							break;
						}
					}
				}
				
				Task task = activitiServiceImpl.queryTaskByProcessInstanceId(processInstance.getId());
				//????????????????????????
				Map<String, Object> variables1 = new HashMap<>();
				variables1.put("isContainLegal",containLeagl);
				Task task1 = taskService.createTaskQuery().taskId(task.getId()).list().get(0);
		    	taskService.addComment(task1.getId(), task1.getProcessInstanceId(), "????????????");
		    	//-----------------start-----------------------????????????????????????
				ViewTaskInfoTbl flow1 = new ViewTaskInfoTbl();
				flow1.setAssigneeName(user.getEmployee().getCnName());
				flow1.setDepartName(user.getDepart().getName());
				flow1.setPositionName(user.getPosition().getPositionName());
				flow1.setFinishTime(new Date());
				flow1.setComment("????????????");
				flow1.setProcessId(processInstance.getId());
				flow1.setProcessKey(ConfigConstants.SCHEDULING_KEY);
				flow1.setStatu(100);
				viewTaskInfoService.save(flow1);
		    	taskService.complete(task1.getId(), variables1);
			}
			map.put("success", true);
			map.put("message", "?????????????????????????????????!");
		}else{
			throw new OaException("?????????????????????????????????");
		}
		return map;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void completeTask(HttpServletRequest request,String processId, String comment,
			String commentType) throws Exception {
		
		logger.info("????????????completeTask:start??????");
		Long time1 = System.currentTimeMillis();
			
		User user= userService.getCurrentUser();
		logger.info("????????????completeTask??????:processId="+processId+";comment="+comment+";commentType="+commentType
			+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));

		Task task = activitiServiceImpl.queryTaskByProcessInstanceId(processId);
		if(task==null){
			throw new OaException("??????Id???"+processId+"?????????????????????");
		}
		boolean type = StringUtils.startsWithIgnoreCase(task.getTaskDefinitionKey(), "personnel");
		ApplicationEmployeeClass employeeClass = applicationEmployeeClassMapper.queryByProcessInstanceId(processId);
		if(employeeClass!=null){
			if(employeeClass.getIsMove().intValue()==0){
				//??????1???18:00?????????
				Date date = DateUtils.parse(DateUtils.format(employeeClass.getClassMonth(), DateUtils.FORMAT_SHORT)+" 18:00:00",DateUtils.FORMAT_LONG);
				if(DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_SHORT), DateUtils.FORMAT_SHORT).getTime()>date.getTime()){
					throw new OaException("????????????????????????????????????");
				}
			}
			Integer approvalStatus ;
			if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.PASS)) {
				approvalStatus=type?ConfigConstants.PASS_STATUS:ConfigConstants.DOING_STATUS;
				ApplicationEmployeeClassDetail condition = new ApplicationEmployeeClassDetail();
				condition.setAttnApplicationEmployClassId(employeeClass.getId());
				
				//??????????????????????????????????????????
				Map<Date,AnnualVacation> vacationMap = annualVacationService.getVacationMap(employeeClass.getClassMonth());
				boolean containLeagl = false;
				if(vacationMap!=null&&vacationMap.size()>0){
					List<ApplicationEmployeeClassDetail> detailList = applicationEmployeeClassDetailMapper.selectByCondition(condition);
					for(ApplicationEmployeeClassDetail de:detailList){
						if(de.getClassSettingId()!=null&&vacationMap.containsKey(de.getClassDate())){
							containLeagl = true;
							break;
						}
					}
				}
				
				Map<String, Object> variables = new HashMap<>();
				variables.put("isContainLegal",containLeagl);
				//-----------------start-----------------------????????????????????????
				saveFlow(user,comment,processId,approvalStatus);
				//-----------------end-------------------------
				//????????????????????????????????????assignee,??????????????????????????????????????????
				if(StringUtils.isBlank(task.getAssignee())) {
					activitiServiceImpl.claimTask(task.getId(), String.valueOf(userService.getCurrentUser().getEmployeeId()));
				}
				activitiServiceImpl.completeTask(task.getId(), "????????????", variables,ConfigConstants.PASS);
			}else if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.REFUSE)) {
				approvalStatus=ConfigConstants.REFUSE_STATUS;
				//-----------------start-----------------------????????????????????????
				saveFlow(user,comment,processId,approvalStatus);
				//-----------------end-------------------------
				//????????????????????????????????????assignee,??????????????????????????????????????????
				if(StringUtils.isBlank(task.getAssignee())) {
					activitiServiceImpl.claimTask(task.getId(), String.valueOf(userService.getCurrentUser().getEmployeeId()));
				}
				activitiServiceImpl.completeTask(task.getId(), comment,null,ConfigConstants.REFUSE);
				if(employeeClass.getIsMove().intValue()==0){//??????????????????????????????????????????
					backUp(employeeClass);
				}
			}else{
				approvalStatus=ConfigConstants.BACK_STATUS;
				//-----------------start-----------------------????????????????????????
				saveFlow(user,comment,processId,approvalStatus);
				//-----------------end-------------------------
				activitiServiceImpl.completeTask(task.getId(), comment,null,ConfigConstants.BACK);
                if(employeeClass.getIsMove().intValue()==0){//??????????????????????????????????????????
                	backUp(employeeClass);
				}
			}
			ApplicationEmployeeClass update = new ApplicationEmployeeClass();
			update.setApprovalStatus(approvalStatus);
			update.setId(employeeClass.getId());
			applicationEmployeeClassMapper.updateById(update);
		}else{
			throw new OaException("???????????????"+processId+"?????????????????????????????????");
		}
		
		Long time2 = System.currentTimeMillis();
		logger.info("????????????completeTask:use time="+(time2-time1));
		logger.info("????????????completeTask:end??????");
	}
	
	//???????????????????????????
	public void backUp(ApplicationEmployeeClass employeeClass){
		ApplicationEmployeeClassDetail param = new ApplicationEmployeeClassDetail();
		param.setAttnApplicationEmployClassId(employeeClass.getId());
		List<ApplicationEmployeeClassDetail> list = applicationEmployeeClassDetailMapper.selectByCondition(param);
		ApplicationEmployeeClass backUp = new ApplicationEmployeeClass();
		backUp.setDepartId(employeeClass.getDepartId());
		backUp.setGroupId(employeeClass.getGroupId());
		backUp.setDepartName(employeeClass.getDepartName());
		backUp.setClassMonth(employeeClass.getClassMonth());
		backUp.setEmployeeNum(employeeClass.getEmployeeNum());
		backUp.setClassEmployeeNum(employeeClass.getClassEmployeeNum());
		backUp.setClassSettingPerson(employeeClass.getClassSettingPerson());
		backUp.setVersion(0L);
		backUp.setDelFlag(0);
		backUp.setCreateTime(employeeClass.getCreateTime());
		backUp.setCreateUser(employeeClass.getCreateUser());
		backUp.setIsMove(0);
		backUp.setEmployeeId(employeeClass.getEmployeeId());
		applicationEmployeeClassMapper.save(backUp);
		for(ApplicationEmployeeClassDetail detail:list){
			detail.setAttnApplicationEmployClassId(backUp.getId());
		}
		applicationEmployeeClassDetailMapper.batchSave(list);
	}
	
	public void saveFlow(User user,String comment,String processId,Integer approvalStatus){
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
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteEmployeeClass(Long id,User user) throws Exception {
		ApplicationEmployeeClass employeeClass = new ApplicationEmployeeClass();
		employeeClass.setId(id);
		employeeClass.setUpdateTime(new Date());
		employeeClass.setUpdateUser(user.getEmployee().getCnName());
		List<ApplicationEmployeeClass> list = applicationEmployeeClassMapper.getByCondition(employeeClass);
		if(list!=null&&list.size()>0){
			if(list.get(0).getApprovalStatus()!=null){
				throw new OaException("???????????????????????????????????????");
			}else{
				if(applicationEmployeeClassMapper.deleteById(list.get(0))>0){
					ApplicationEmployeeClassDetail detail =new ApplicationEmployeeClassDetail();
					detail.setAttnApplicationEmployClassId(id);
					detail.setUpdateTime(new Date());
					detail.setUpdateUser(user.getEmployee().getCnName());
					applicationEmployeeClassDetailMapper.deleteByEmployeeClassId(detail);
				}else{
					throw new OaException("?????????????????????????????????");
				}
			}
		}else{
			throw new OaException("??????????????????????????????");
		}
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void save(Long classdetailId,String classMonth,Map<String,Map<String,String>> map,Map<String,Map<String,String>> map1,User user) throws OaException {
		ApplicationEmployeeClass employeeClass = new ApplicationEmployeeClass();
		employeeClass.setId(classdetailId);
		List<ApplicationEmployeeClass> employeeClassList = applicationEmployeeClassMapper.getByCondition(employeeClass);
		int employeeNum = 0;
		//????????????????????????????????????????????????
		if(map1!=null){
			for (Map.Entry<String, Map<String,String>> entry : map1.entrySet()) {
				String employeeId = entry.getKey();
				Map<String,String> classDetailMap = entry.getValue();
				if(classDetailMap!=null){
					for (Map.Entry<String, String>  detail: classDetailMap.entrySet()) {
						ApplicationEmployeeClassDetail applicationEmployeeClassDetail = new ApplicationEmployeeClassDetail();
						applicationEmployeeClassDetail.setUpdateTime(new Date());
						applicationEmployeeClassDetail.setUpdateUser(user.getEmployee().getCnName());
						applicationEmployeeClassDetail.setClassDate(DateUtils.parse(detail.getKey(), "yyyy-MM-dd"));
						applicationEmployeeClassDetail.setEmployId(Long.valueOf(employeeId));
						applicationEmployeeClassDetail.setAttnApplicationEmployClassId(classdetailId);
						applicationEmployeeClassDetail.setClassSettingId(null);
						applicationEmployeeClassDetailMapper.updateByCondition(applicationEmployeeClassDetail);
					}
				}
			}
		}
		for (Map.Entry<String, Map<String,String>> entry : map.entrySet()) {
			String employeeId = entry.getKey();
			ApplicationEmployeeClassDetail check = new ApplicationEmployeeClassDetail();
			check.setAttnApplicationEmployClassId(classdetailId);
			check.setEmployId(Long.valueOf(employeeId));
			//??????????????????????????????
			int isNew = applicationEmployeeClassDetailMapper.checkEmployeeisNew(check);
			Employee old = employeeMapper.getById(Long.valueOf(employeeId));
			double shouldTime = 0;
			if(isNew==0){
				employeeNum = employeeNum + 1;
				shouldTime = getShouldTime(employeeClassList.get(0).getClassMonth(),old);
			}
			Map<String,String> classDetailMap = entry.getValue();
			if(classDetailMap!=null){
				
				List<ApplicationEmployeeClassDetail> batch = new ArrayList<ApplicationEmployeeClassDetail>();
				for (Map.Entry<String, String>  detail: classDetailMap.entrySet()) {  
					//????????????????????????????????????????????????
					ApplicationEmployeeClassDetail applicationEmployeeClassDetail = new ApplicationEmployeeClassDetail();
					applicationEmployeeClassDetail.setClassDate(DateUtils.parse(detail.getKey(), "yyyy-MM-dd"));
					applicationEmployeeClassDetail.setEmployId(Long.valueOf(employeeId));
					applicationEmployeeClassDetail.setAttnApplicationEmployClassId(classdetailId);
  					List isInit = applicationEmployeeClassDetailMapper.selectByCondition(applicationEmployeeClassDetail);
					if(isInit!=null && isInit.size()>0){
						applicationEmployeeClassDetail.setClassSettingId(Long.valueOf(detail.getValue()));
						applicationEmployeeClassDetail.setUpdateTime(new Date());
						applicationEmployeeClassDetail.setUpdateUser(user.getEmployee().getCnName());
						int i = applicationEmployeeClassDetailMapper.updateByCondition(applicationEmployeeClassDetail);
						if(i<=0){
							throw new OaException("?????????????????????????????????");
						}
					}else{
						applicationEmployeeClassDetail.setClassSettingId(Long.valueOf(detail.getValue()));
						applicationEmployeeClassDetail.setCreateTime(new Date());
						applicationEmployeeClassDetail.setCreateUser(user.getEmployee().getCnName());
						applicationEmployeeClassDetail.setClassSettingPerson(user.getEmployee().getCnName());
						applicationEmployeeClassDetail.setDelFlag(0);
						applicationEmployeeClassDetail.setVersion(0L);
						applicationEmployeeClassDetail.setIsMove(0);
						applicationEmployeeClassDetail.setCompanyId(user.getCompanyId());
						applicationEmployeeClassDetail.setEmployName(old.getCnName());
						applicationEmployeeClassDetail.setShouldTime(shouldTime);
						batch.add(applicationEmployeeClassDetail);
					}
					
				}
				if(batch!=null&&batch.size()>0){
					applicationEmployeeClassDetailMapper.batchSave(batch);
				}
			}
		}
		if(employeeClassList!=null&&employeeClassList.size()>0){
			//?????????????????????
			List<ApplicationEmployeeClassDetail> classEmployeeNum = applicationEmployeeClassDetailMapper.getclassEmployeeNum(classdetailId);
			//?????????size?????????????????????
			employeeClass.setUpdateTime(new Date());
			employeeClass.setEmployeeNum(employeeClassList.get(0).getEmployeeNum()+employeeNum);
			employeeClass.setUpdateUser(user.getEmployee().getCnName());
			employeeClass.setClassEmployeeNum((classEmployeeNum!=null&&classEmployeeNum.size()>0)?classEmployeeNum.size():0);
			employeeClass.setApprovalStatus(null);
			int count = applicationEmployeeClassMapper.updateById(employeeClass);
			if(count<=0){
				throw new OaException("?????????????????????????????????");
			}
		}else{
			throw new OaException("?????????????????????????????????");
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> moveEmployeeClass(HttpServletRequest request,Long classdetailId, User user,Map<String,Map<String,String>> map,Map<String,Map<String,String>> map1)
			throws OaException {
		
		logger.info("????????????moveEmployeeClass:start??????");
		Long time1 = System.currentTimeMillis();
		logger.info("????????????moveEmployeeClass??????:classdetailId="+classdetailId
			+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));
		
		//???????????????????????????????????????
		List<ScheduleGroup> schedulerList = scheduleGroupMapper.getListByScheduler(user.getEmployee().getId());
		if(schedulerList==null || schedulerList.size() <=0){
			throw new OaException("?????????????????????");
		}
		//????????????????????????
		ApplicationEmployeeClass applicationEmployeeClass = new ApplicationEmployeeClass();
		applicationEmployeeClass.setId(classdetailId);
		List<ApplicationEmployeeClass> list = applicationEmployeeClassMapper.getByCondition(applicationEmployeeClass);
		//??????????????????
		ApplicationEmployeeClassDetail param1 = new ApplicationEmployeeClassDetail();
		param1.setAttnApplicationEmployClassId(classdetailId);
		List<ApplicationEmployeeClassDetail>  workHoursList =  applicationEmployeeClassDetailMapper.getEmployeeClassHours(param1);
		Map<Long,Double> workHoursMap = new HashMap<Long,Double>();
		for(ApplicationEmployeeClassDetail detail:workHoursList){
			workHoursMap.put(detail.getEmployId(), detail.getShouldTime());
		}
		
		ClassSetting setting = new ClassSetting();
		//setting.setDepartId(list.get(0).getDepartId());
		List<ClassSetting> settingList = classSettingMapper.getListByCondition(setting);
		Map<Long,Double> setHoursMap = new HashMap<Long,Double>();
		for(ClassSetting data:settingList){
			setHoursMap.put(data.getId(), data.getMustAttnTime());
		}
		
		
		//??????????????????????????????
		AnnualVacation vacation = new AnnualVacation();
		List<Integer> typeList = new ArrayList<Integer>();
		typeList.add(AnnualVacation.YYPE_LEGAL);
		vacation.setTypeList(typeList);
		vacation.setStartDate(list.get(0).getClassMonth());
		vacation.setEndDate(DateUtils.getLastDay(list.get(0).getClassMonth()));
		List<AnnualVacation> vaList = annualVacationService.getListByCondition(vacation);
		Map<String,String> vaContainMap = new HashMap<String,String>();
		for(AnnualVacation va:vaList){
			vaContainMap.put(DateUtils.format(va.getAnnualDate(), DateUtils.FORMAT_SHORT), DateUtils.format(va.getAnnualDate(), DateUtils.FORMAT_SHORT));
		}
		
		Map<Long,Double> minusMap = new HashMap<Long,Double>();
		
		
		List<ApplicationEmployeeClassDetail> moveList = new ArrayList<ApplicationEmployeeClassDetail>();
		for (Map.Entry<String, Map<String,String>> entry : map1.entrySet()) {
			String employeeId = entry.getKey();
			Map<String,String> classDetailMap = entry.getValue();
			minusMap.put(Long.valueOf(employeeId), 0d);
			if(classDetailMap!=null){
				for (Map.Entry<String, String>  detail: classDetailMap.entrySet()) {
					//????????????
					ApplicationEmployeeClassDetail applicationEmployeeClassDetail = new ApplicationEmployeeClassDetail();
					applicationEmployeeClassDetail.setCompanyId(user.getCompanyId());
					applicationEmployeeClassDetail.setEmployId(Long.valueOf(employeeId));
					applicationEmployeeClassDetail.setCreateTime(new Date());
					applicationEmployeeClassDetail.setCreateUser(user.getEmployee().getCnName());
					applicationEmployeeClassDetail.setClassSettingId(null);
					applicationEmployeeClassDetail.setClassSettingPerson(user.getEmployee().getCnName());
					applicationEmployeeClassDetail.setClassDate(DateUtils.parse(detail.getKey(), "yyyy-MM-dd"));
					applicationEmployeeClassDetail.setDelFlag(0);
					applicationEmployeeClassDetail.setIsMove(1);
					//??????????????????
					Long oldSettingId = employeeClassMapper.getClassSettingIdByEmployIdAndDate(Long.valueOf(employeeId),DateUtils.parse(detail.getKey(), "yyyy-MM-dd"));
					ApplicationEmployeeClassDetail oldSchedule = new ApplicationEmployeeClassDetail();
					oldSchedule.setCompanyId(user.getCompanyId());
					oldSchedule.setEmployId(Long.valueOf(employeeId));
					oldSchedule.setCreateTime(new Date());
					oldSchedule.setCreateUser(user.getEmployee().getCnName());
					oldSchedule.setClassSettingId(oldSettingId);
					oldSchedule.setClassSettingPerson(user.getEmployee().getCnName());
					oldSchedule.setClassDate(DateUtils.parse(detail.getKey(), "yyyy-MM-dd"));
					oldSchedule.setDelFlag(0);
					oldSchedule.setIsMove(0);
					
					if(workHoursMap!=null&&workHoursMap.containsKey(Long.valueOf(employeeId))){
						double shouldTime = workHoursMap.get(Long.valueOf(employeeId));
						applicationEmployeeClassDetail.setShouldTime(shouldTime);
						oldSchedule.setShouldTime(shouldTime);
					}else{
						Employee old = employeeMapper.getById(Long.valueOf(employeeId));
						double shouldTime = getShouldTime(list.get(0).getClassMonth(),old);
						applicationEmployeeClassDetail.setShouldTime(shouldTime);
						oldSchedule.setShouldTime(shouldTime);
					}
					moveList.add(applicationEmployeeClassDetail);
					moveList.add(oldSchedule);
					minusMap.put(Long.valueOf(employeeId), minusMap.get(Long.valueOf(employeeId))+setHoursMap.get(Long.valueOf(detail.getValue())));
				}
			}
		}
		
		Map<Long,Double> addMap = new HashMap<Long,Double>();
		
		for (Map.Entry<String, Map<String,String>> entry : map.entrySet()) {
			String employeeId = entry.getKey();
			Map<String,String> classDetailMap = entry.getValue();
			addMap.put(Long.valueOf(employeeId), 0d);
			if(classDetailMap!=null){
				for (Map.Entry<String, String>  detail: classDetailMap.entrySet()) {
					//????????????
					ApplicationEmployeeClassDetail applicationEmployeeClassDetail = new ApplicationEmployeeClassDetail();
					applicationEmployeeClassDetail.setCompanyId(user.getCompanyId());
					applicationEmployeeClassDetail.setEmployId(Long.valueOf(employeeId));
					applicationEmployeeClassDetail.setCreateTime(new Date());
					applicationEmployeeClassDetail.setCreateUser(user.getEmployee().getCnName());
					applicationEmployeeClassDetail.setClassSettingId(Long.valueOf(detail.getValue()));
					applicationEmployeeClassDetail.setClassSettingPerson(user.getEmployee().getCnName());
					applicationEmployeeClassDetail.setClassDate(DateUtils.parse(detail.getKey(), "yyyy-MM-dd"));
					applicationEmployeeClassDetail.setDelFlag(0);
					applicationEmployeeClassDetail.setIsMove(1);
					//??????????????????
					Long oldSettingId = employeeClassMapper.getClassSettingIdByEmployIdAndDate(Long.valueOf(employeeId),DateUtils.parse(detail.getKey(), "yyyy-MM-dd"));
					ApplicationEmployeeClassDetail oldSchedule = new ApplicationEmployeeClassDetail();
					oldSchedule.setCompanyId(user.getCompanyId());
					oldSchedule.setEmployId(Long.valueOf(employeeId));
					oldSchedule.setCreateTime(new Date());
					oldSchedule.setCreateUser(user.getEmployee().getCnName());
					oldSchedule.setClassSettingId(oldSettingId);
					oldSchedule.setClassSettingPerson(user.getEmployee().getCnName());
					oldSchedule.setClassDate(DateUtils.parse(detail.getKey(), "yyyy-MM-dd"));
					oldSchedule.setDelFlag(0);
					oldSchedule.setIsMove(0);
					
					//?????????????????????????????????????????????
					if(vaContainMap!=null&&vaContainMap.containsKey(detail.getKey())){
						
						//???????????????????????????
						Config configCondition = new Config();
						configCondition.setCode("dutyCommitTimeLimit");
						List<Config> limit = configService.getListByCondition(configCondition);
						int num = Integer.valueOf(limit.get(0).getDisplayCode());
						
						Date lastSubDate = annualVacationService.getWorkingDayPre(num,DateUtils.parse(detail.getKey(), "yyyy-MM-dd"));
						if(DateUtils.getIntervalDays(new Date(),lastSubDate)<0){
							throw new OaException("?????????????????????4?????????????????????");
						}
					}
					
					if(workHoursMap!=null&&workHoursMap.containsKey(Long.valueOf(employeeId))){
						double shouldtime = workHoursMap.get(Long.valueOf(employeeId));
						applicationEmployeeClassDetail.setShouldTime(shouldtime);
						oldSchedule.setShouldTime(shouldtime);
					}else{
						Employee old = employeeMapper.getById(Long.valueOf(employeeId));
						double shouldtime = getShouldTime(list.get(0).getClassMonth(),old);
						applicationEmployeeClassDetail.setShouldTime(shouldtime);
						oldSchedule.setShouldTime(shouldtime);
					}
					moveList.add(applicationEmployeeClassDetail);
					moveList.add(oldSchedule);
					//???????????????????????????
					EmployeeClass isExist = new EmployeeClass();
					isExist.setEmployId(Long.valueOf(employeeId));
					isExist.setClassDate(DateUtils.parse(detail.getKey(), "yyyy-MM-dd"));
					EmployeeClass isExistList = employeeClassMapper.getByEmployIdAndDate(Long.valueOf(employeeId),DateUtils.parse(detail.getKey(), "yyyy-MM-dd"));
					if(isExistList!=null){
						addMap.put(Long.valueOf(employeeId), addMap.get(Long.valueOf(employeeId))+setHoursMap.get(Long.valueOf(detail.getValue()))-isExistList.getMustAttnTime());
					}else{
						addMap.put(Long.valueOf(employeeId), addMap.get(Long.valueOf(employeeId))+setHoursMap.get(Long.valueOf(detail.getValue())));
					}
				}  
			}
		}
		
		Date startTime = list.get(0).getClassMonth();
		Date endTime = DateUtils.getLastDay(list.get(0).getClassMonth());
		//????????????????????????
		for (Map.Entry<Long, Double>  detail: workHoursMap.entrySet()) {
			Double minus = (minusMap!=null&&minusMap.containsKey(detail.getKey()))?minusMap.get(detail.getKey()):0d;
			Double add = (addMap!=null&&addMap.containsKey(detail.getKey()))?addMap.get(detail.getKey()):0d;
			Double must = 0d;
			//???????????????????????????
			EmployeeClass condition = new EmployeeClass();
			condition.setStartTime(startTime);
			condition.setEndTime(endTime);
			condition.setEmployId(detail.getKey());
			EmployeeClass total = employeeClassMapper.getEmployeeClassHours(condition);
			if(total!=null&&total.getMustAttnTime()!=null){
				must = total.getMustAttnTime();
			}
			Employee emp = employeeMapper.getById(detail.getKey());
			double shouldTime = getShouldTime(list.get(0).getClassMonth(),emp);
			
//			if(must-minus+add<shouldTime){
//				throw new OaException("????????????????????????");
//			}
		}
		
		Map<Long,Long> moveNum = new HashMap<Long,Long>();
		Map<Long,String> employeeName = new HashMap<Long,String>();
		for(ApplicationEmployeeClassDetail move:moveList){
			if(moveNum!=null&&!moveNum.containsKey(move.getEmployId())){
				moveNum.put(move.getEmployId(), move.getEmployId());
			}
			if(employeeName!=null&&!employeeName.containsKey(move.getEmployId())){
				Employee employ = employeeMapper.getById(move.getEmployId());
				employeeName.put(move.getEmployId(), employ.getCnName());
				move.setEmployName(employ.getCnName());
			}else{
				move.setEmployName(employeeName.get(move.getEmployId()));
			}
		}
		
		if(list!=null&&list.size()>0){

			Long auditor = schedulerList.get(0).getAuditor();//???????????????
			//????????????
			Map<String, Object> variables = new HashMap<>();
			variables.put("approver",auditor+"");
			ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("scheduling",variables);
			
			//-----------------start-----------------------????????????????????????
			ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
			flow.setAssigneeName(user.getEmployee().getCnName());
			flow.setDepartName(user.getDepart().getName());
			flow.setPositionName(user.getPosition().getPositionName());
			flow.setFinishTime(new Date());
			flow.setComment("????????????");
			flow.setProcessId(processInstance.getId());
			flow.setProcessKey(ConfigConstants.SCHEDULING_KEY);
			flow.setStatu(0);
			viewTaskInfoService.save(flow);
			
			applicationEmployeeClass.setDepartId(list.get(0).getDepartId());
			applicationEmployeeClass.setDepartName(list.get(0).getDepartName());
			applicationEmployeeClass.setClassMonth(list.get(0).getClassMonth());
			applicationEmployeeClass.setEmployeeNum(list.get(0).getEmployeeNum());
			applicationEmployeeClass.setClassEmployeeNum(moveNum.size());
			applicationEmployeeClass.setClassSettingPerson(user.getEmployee().getCnName());
			applicationEmployeeClass.setApprovalStatus(ConfigConstants.DOING_STATUS);
			applicationEmployeeClass.setVersion(0L);
			applicationEmployeeClass.setDelFlag(0);
			applicationEmployeeClass.setCreateUser(user.getEmployee().getCnName());
			applicationEmployeeClass.setCreateTime(new Date());
			applicationEmployeeClass.setIsMove(1);
			applicationEmployeeClass.setProcessInstanceId(processInstance.getId());
			applicationEmployeeClass.setEmployeeId(user.getEmployee().getId());
			applicationEmployeeClass.setGroupId(list.get(0).getGroupId());
			applicationEmployeeClass.setMoveType(0);//????????????
			applicationEmployeeClassMapper.save(applicationEmployeeClass);
			
			for(ApplicationEmployeeClassDetail move:moveList){
				move.setAttnApplicationEmployClassId(applicationEmployeeClass.getId());
			}
			applicationEmployeeClassDetailMapper.batchSave(moveList);
			
			if(user.getEmployee().getId().equals(auditor)){
				
				Map<Date,AnnualVacation> vacationMap = annualVacationService.getVacationMap(list.get(0).getClassMonth());
				boolean containLeagl = false;
				if(vacationMap!=null&&vacationMap.size()>0){
					for(ApplicationEmployeeClassDetail de:moveList){
						if(de.getClassSettingId()!=null&&vacationMap.containsKey(de.getClassDate())){
							containLeagl = true;
							break;
						}
					}
				}
				
				Map<String, Object> variables1 = new HashMap<>();
				variables1.put("isContainLegal",containLeagl);
				Task task = activitiServiceImpl.queryTaskByProcessInstanceId(processInstance.getId());
				Task task1 = taskService.createTaskQuery().taskId(task.getId()).list().get(0);
		    	taskService.addComment(task1.getId(), task1.getProcessInstanceId(), "????????????");
		    	
		    	//-----------------start-----------------------????????????????????????
				ViewTaskInfoTbl flow1 = new ViewTaskInfoTbl();
				flow1.setAssigneeName(user.getEmployee().getCnName());
				flow1.setDepartName(user.getDepart().getName());
				flow1.setPositionName(user.getPosition().getPositionName());
				flow1.setFinishTime(new Date());
				flow1.setComment("????????????");
				flow1.setProcessId(processInstance.getId());
				flow1.setProcessKey(ConfigConstants.SCHEDULING_KEY);
				flow1.setStatu(100);
				viewTaskInfoService.save(flow1);
				
		    	taskService.complete(task1.getId(), variables1);
		    	
			}
		}
		
		Long time2 = System.currentTimeMillis();
		logger.info("????????????moveEmployeeClass:use time="+(time2-time1));
		logger.info("????????????moveEmployeeClass:end??????");
		
		return null;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> HrMoveEmployeeClass(Long classdetailId,
			User user, Map<String, Map<String, String>> map,Map<String,Map<String,String>> map1) throws OaException {
		logger.info("employeeClassId="+classdetailId+";user="+user.getEmployee().getCnName());
		
		ApplicationEmployeeClass employeeClass = new ApplicationEmployeeClass();
		employeeClass.setId(classdetailId);
		List<ApplicationEmployeeClass> old = applicationEmployeeClassMapper.getByCondition(employeeClass);
			
		Map<Date,AnnualVacation> vacationMap = annualVacationService.getVacationMap(old.get(0).getClassMonth());
		
		Map<Long,String> employeeName = new HashMap<Long,String>();
		
		for (Map.Entry<String, Map<String,String>> entry : map1.entrySet()) {
			String employeeId = entry.getKey();
			Map<String,String> classDetailMap = entry.getValue();
			if(classDetailMap!=null){
				for (Map.Entry<String, String>  detail: classDetailMap.entrySet()) {
					//??????????????????
					EmployeeClass con = new EmployeeClass();
					con.setEmployId(Long.valueOf(employeeId));
					con.setClassDate(DateUtils.parse(detail.getKey(), "yyyy-MM-dd"));
					EmployeeClass employeeClassList = employeeClassMapper.getByEmployIdAndDate(Long.valueOf(employeeId),DateUtils.parse(detail.getKey(), "yyyy-MM-dd"));
					if(employeeClassList!=null){
						con.setUpdateTime(new Date());
						con.setUpdateUser(user.getEmployee().getCnName());
						con.setId(employeeClassList.getId());
						con.setVersion(employeeClassList.getVersion());
						con.setDelFlag(1);
						employeeClassMapper.updateById(con);
						//??????????????????????????????????????????????????????
						if(vacationMap!=null&&vacationMap.containsKey(DateUtils.parse(detail.getKey(), "yyyy-MM-dd"))){
							EmployeeDuty duty = new EmployeeDuty();
							duty.setEmployId(Long.valueOf(employeeId));
							duty.setDutyDate(DateUtils.parse(detail.getKey(), "yyyy-MM-dd"));
							duty.setDutyItem("?????????????????????");
							duty.setUpdateTime(new Date());
							duty.setUpdateUser(user.getEmployee().getCnName());
							duty.setDelFlag(1);
							duty.setSource(2);
							employeeDutyMapper.update(duty);
						}
					}
				}
			}
		}
		for (Map.Entry<String, Map<String,String>> entry : map.entrySet()) {
			String employeeId = entry.getKey();
			Employee employ = employeeMapper.getById(Long.valueOf(employeeId));
			Map<String,String> classDetailMap = entry.getValue();
			List<ApplicationEmployeeClassDetail> employClassList = new ArrayList<ApplicationEmployeeClassDetail>();
			if(classDetailMap!=null){
				for (Map.Entry<String, String>  detail: classDetailMap.entrySet()) {
					//????????????????????????????????????
					ApplicationEmployeeClassDetail param = new ApplicationEmployeeClassDetail();
					param.setClassDate(DateUtils.parse(detail.getKey(), "yyyy-MM-dd"));
					param.setEmployId(Long.valueOf(employeeId));
					param.setAttnApplicationEmployClassId(classdetailId);
					List<ApplicationEmployeeClassDetail> detailList = applicationEmployeeClassDetailMapper.selectByCondition(param);
					if(detailList!=null&&detailList.size()>0){
						ApplicationEmployeeClassDetail applicationEmployeeClassDetail = new ApplicationEmployeeClassDetail();
						applicationEmployeeClassDetail.setUpdateTime(new Date());
						applicationEmployeeClassDetail.setUpdateUser(user.getEmployee().getCnName());
						applicationEmployeeClassDetail.setClassSettingId(Long.valueOf(detail.getValue()));
						applicationEmployeeClassDetail.setClassDate(DateUtils.parse(detail.getKey(), "yyyy-MM-dd"));
						applicationEmployeeClassDetail.setEmployId(Long.valueOf(employeeId));
						applicationEmployeeClassDetail.setEmployName(detailList.get(0).getEmployName());
						applicationEmployeeClassDetail.setShouldTime(detailList.get(0).getShouldTime());
						applicationEmployeeClassDetail.setAttnApplicationEmployClassId(classdetailId);
						applicationEmployeeClassDetail.setIsMove(1);
						employClassList.add(applicationEmployeeClassDetail);
					}else{
						ApplicationEmployeeClassDetail add = new ApplicationEmployeeClassDetail();
						add.setAttnApplicationEmployClassId(classdetailId);
						add.setClassDate(DateUtils.parse(detail.getKey(), "yyyy-MM-dd"));
						add.setCompanyId(user.getCompanyId());
						add.setEmployId(Long.valueOf(employeeId));
						add.setEmployName(employ.getCnName());
						add.setClassSettingPerson(user.getEmployee().getCnName());
						add.setCreateTime(new Date());
						add.setCreateUser(user.getEmployee().getCnName());
						add.setClassSettingId(Long.valueOf(detail.getValue()));
						add.setDelFlag(0);
						add.setIsMove(1);
						employClassList.add(add);
					}
				}  
				if(employClassList!=null&&employClassList.size()>0){
					for(ApplicationEmployeeClassDetail classDetail:employClassList){
						//??????????????????????????????
						EmployeeClass con = new EmployeeClass();
						con.setEmployId(classDetail.getEmployId());
						con.setClassDate(classDetail.getClassDate());
						EmployeeClass employeeClassList = employeeClassMapper.getByEmployIdAndDate(classDetail.getEmployId(),classDetail.getClassDate());
						if(employeeClassList!=null){
							if(classDetail.getClassSettingId()!=null){
								con.setClassSettingId(classDetail.getClassSettingId());
								con.setUpdateTime(new Date());
								con.setUpdateUser(user.getEmployee().getCnName());
								con.setId(employeeClassList.getId());
								con.setClassSettingPerson(employeeClassList.getClassSettingPerson());
								con.setVersion(employeeClassList.getVersion());
								employeeClassMapper.updateById(con);
								//??????????????????????????????????????????????????????
								if(vacationMap!=null&&vacationMap.containsKey(classDetail.getClassDate())){
									ClassSetting setting = classSettingMapper.getById(classDetail.getClassSettingId());
									EmployeeDuty duty = new EmployeeDuty();
									duty.setEmployId(classDetail.getEmployId());
									duty.setDutyDate(classDetail.getClassDate());
									duty.setDutyItem("?????????????????????");
									duty.setStartTime(DateUtils.parse(DateUtils.format(classDetail.getClassDate(), DateUtils.FORMAT_SHORT)+" "+DateUtils.format(setting.getStartTime(), DateUtils.FORMAT_HH_MM_SS),
											DateUtils.FORMAT_LONG));
									duty.setEndTime(DateUtils.parse(DateUtils.format(classDetail.getClassDate(), DateUtils.FORMAT_SHORT)+" "+DateUtils.format(setting.getEndTime(), DateUtils.FORMAT_HH_MM_SS),
											DateUtils.FORMAT_LONG));
									duty.setWorkHours(setting.getMustAttnTime());
									duty.setClassSettingPerson(old.get(0).getClassSettingPerson());
									duty.setUpdateTime(new Date());
									duty.setUpdateUser(user.getEmployee().getCnName());
									duty.setSource(2);
									employeeDutyMapper.update(duty);
								}
							}
						}else{
							if(classDetail.getClassSettingId()!=null){
								EmployeeClass attnEmployeeClass = new EmployeeClass();
								attnEmployeeClass.setCompanyId(user.getCompanyId());
								attnEmployeeClass.setDepartId(old.get(0).getDepartId());
								attnEmployeeClass.setEmployId(classDetail.getEmployId());
								attnEmployeeClass.setEmployName(classDetail.getEmployName());
								attnEmployeeClass.setClassDate(classDetail.getClassDate());
								attnEmployeeClass.setClassSettingId(classDetail.getClassSettingId());
								attnEmployeeClass.setClassSettingPerson(user.getEmployee().getCnName());
								attnEmployeeClass.setCreateTime(new Date());
								attnEmployeeClass.setCreateUser(user.getEmployee().getCnName());
								attnEmployeeClass.setShouldTime(classDetail.getShouldTime());
								employeeClassMapper.save(attnEmployeeClass);
								//??????????????????????????????????????????????????????
								if(vacationMap!=null&&vacationMap.containsKey(classDetail.getClassDate())){
									ClassSetting setting = classSettingMapper.getById(classDetail.getClassSettingId());
									EmployeeDuty duty = new EmployeeDuty();
									duty.setCompanyId(user.getCompanyId());
									duty.setDepartId(old.get(0).getDepartId());
									duty.setEmployId(classDetail.getEmployId());
									duty.setEmployName(employ.getCnName());
									duty.setYear(DateUtils.format(old.get(0).getClassMonth(), "yyyy"));
									duty.setVacationName(vacationMap.get(classDetail.getClassDate()).getSubject());
									duty.setDutyDate(classDetail.getClassDate());
									duty.setDutyItem("?????????????????????");
									duty.setStartTime(DateUtils.parse(DateUtils.format(classDetail.getClassDate(), DateUtils.FORMAT_SHORT)+" "+DateUtils.format(setting.getStartTime(), DateUtils.FORMAT_HH_MM_SS),
											DateUtils.FORMAT_LONG));
									duty.setEndTime(DateUtils.parse(DateUtils.format(classDetail.getClassDate(), DateUtils.FORMAT_SHORT)+" "+DateUtils.format(setting.getEndTime(), DateUtils.FORMAT_HH_MM_SS),
											DateUtils.FORMAT_LONG));
									duty.setWorkHours(setting.getMustAttnTime());
									duty.setClassSettingPerson(old.get(0).getClassSettingPerson());
									duty.setCreateTime(new Date());
									duty.setCreateUser(user.getEmployee().getCnName());
									duty.setDelFlag(0);
									duty.setSource(2);
									employeeDutyMapper.save(duty);
								}
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	//??????????????????????????????(?????????????????????????????????)
	public double getShouldTime(Date month_start,Employee old){
		double shouldTime = 0;
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(month_start);
			calendar.add(Calendar.MONTH, 1);
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			Date month_end = calendar.getTime();
			boolean isEndTime = true;
			do{
				//???????????????????????????????????????????????????????????????
				if(old.getQuitTime()==null&&old.getFirstEntryTime()!=null&&DateUtils.format(month_start, "yyyy-MM").equals(DateUtils.format(old.getFirstEntryTime(), "yyyy-MM"))){
					if(old.getFirstEntryTime().getTime()<=month_start.getTime()){
						if(annualVacationService.judgeWorkOrNot(month_start)){
							shouldTime = shouldTime + 8;
						}
					}
				}
				//???????????????????????????????????????????????????????????????
				if(old.getQuitTime()!=null&&DateUtils.format(month_start, "yyyy-MM").equals(DateUtils.format(old.getQuitTime(), "yyyy-MM"))){
					if(old.getQuitTime().getTime()>=month_start.getTime()){
						if(annualVacationService.judgeWorkOrNot(month_start)){
							shouldTime = shouldTime + 8;
						}
					}
				}
				month_start = DateUtils.addDay(month_start, 1);
				if(DateUtils.getIntervalDays(month_start, month_end) < 0) {
					isEndTime = false;
				}
			} while(isEndTime);
		} catch (Exception e) {
			logger.error("????????????={}???????????????",old.getCnName(),e);
		}
		return shouldTime;
	}
	
	/**
	  * exportEmpClassReprotById(????????????????????????)
	  * @Title: exportEmpClassReprotById
	  * @Description: ????????????????????????
	  * @param empClassId
	  * @throws Exception    ????????????
	  * @see com.ule.oa.attendance.service.ApplicationEmployeeClassService#exportEmpClassReprotById(java.lang.Long)
	  * @throws
	 */
	public void exportEmpClassReprotById(Long empClassId) throws Exception {
		
		ApplicationEmployeeClass employClass = new ApplicationEmployeeClass();
		employClass.setId(empClassId);
		List<ApplicationEmployeeClass> list = applicationEmployeeClassMapper.getByCondition(employClass);
		list.get(0).setStartTime(list.get(0).getClassMonth());
		list.get(0).setEndTime(DateUtils.getLastDay(list.get(0).getClassMonth()));
		List<Map<String,Object>> datas = applicationEmployeeClassMapper.getEmpClassReprotById(list.get(0));
		
		Employee currentEmployee = employeeService.getCurrentEmployee();
		String toMail = currentEmployee.getEmail();
		String departName = departService.getDepartAllLeaveName(list.get(0).getDepartId());
		String fileName = departName+"-"+list.get(0).getClassSettingPerson()+"-"+DateUtils.format(list.get(0).getClassMonth(), "yyyyMM");
		String subject = fileName;
		String sttachmentName = fileName;
		String excelName = fileName;
		HSSFWorkbook workbook = new HSSFWorkbook();
		
		HSSFSheet sheet = workbook.createSheet(excelName);
		if (datas != null && datas.size() > 0) {
			String yearMonth = (String)datas.get(0).get("yearMonth");
			Map<String,String[]> titleMap = getEmpClassReportTitle(yearMonth);//??????????????????
			String[] title2 = titleMap.get("t2");//???????????????
			String[] title3 = titleMap.get("t3");//???????????????
			// ??????????????????
			HSSFCellStyle colstyle = workbook.createCellStyle();
			colstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// ????????????
			colstyle.setBorderBottom(XSSFCellStyle.BORDER_THIN); //?????????
			colstyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);//?????????
			colstyle.setBorderTop(XSSFCellStyle.BORDER_THIN);//?????????
			colstyle.setBorderRight(XSSFCellStyle.BORDER_THIN);//?????????
			
			//?????????????????????
			HSSFRow row = sheet.createRow((short) 0);
			//???????????????????????????
			ExcelUtil.createRow(row, 0, null, HSSFCell.CELL_TYPE_STRING,yearMonth+"?????????");
			sheet.addMergedRegion(new CellRangeAddress(0,0,0,2));
			
			//?????????????????????
			row = sheet.createRow((short) 1);
			//???????????????????????????
			for(int colIndex = 0; colIndex < title2.length; colIndex++){
				if(colIndex >=0 && colIndex <= 2){
					sheet.setColumnWidth(colIndex, 5000);//??????????????????
				}
				
				Object obj = title2[colIndex];
				if(obj instanceof Long || obj instanceof Double || obj instanceof Integer || obj instanceof Float || obj instanceof BigDecimal){
					ExcelUtil.createRow(row, colIndex, colstyle, HSSFCell.CELL_TYPE_NUMERIC, obj);
				}else{
					ExcelUtil.createRow(row, colIndex, colstyle, HSSFCell.CELL_TYPE_STRING, obj);
				}
        	}
			sheet.addMergedRegion(new CellRangeAddress(1,2,0,0));
    		sheet.addMergedRegion(new CellRangeAddress(1,2,1,1));
    		sheet.addMergedRegion(new CellRangeAddress(1,2,2,2));
    		sheet.addMergedRegion(new CellRangeAddress(1,2,34,34));
			
			//?????????????????????(???????????????????????????????????????????????????????????????)
			row = sheet.createRow((short) 2);
			//???????????????????????????
			for(int colIndex = 3; colIndex < title3.length; colIndex++){
				if(colIndex >=3 && colIndex <= title3.length - 2){
					sheet.setColumnWidth(colIndex, 1200);//??????????????????
				}else{
					sheet.setColumnWidth(colIndex, 5000);//??????????????????
				}
				
				Object obj = title3[colIndex];
				if(obj instanceof Long || obj instanceof Double || obj instanceof Integer || obj instanceof Float || obj instanceof BigDecimal){
					ExcelUtil.createRow(row, colIndex, colstyle, HSSFCell.CELL_TYPE_NUMERIC, obj);
				}else{
					ExcelUtil.createRow(row, colIndex, colstyle, HSSFCell.CELL_TYPE_STRING, obj);
				}
        	}
			
			Long empId = null;
			Double mustAttnTime = 0.0;
			int index = 3;
			for(Map<String,Object> data : datas){
				Long classDay = Long.valueOf(String.valueOf(data.get("classDay")));
				
				if(null != empId && empId.equals(data.get("empId"))){//????????????????????????
					mustAttnTime += (Double)data.get("mustAttnTime");
					ExcelUtil.createRow(row, 34, null, HSSFCell.CELL_TYPE_NUMERIC,mustAttnTime);
					ExcelUtil.createRow(row, Integer.valueOf(classDay+"") + 2, null, HSSFCell.CELL_TYPE_STRING,data.get("name"));//????????????
				}else{
					row = sheet.createRow((short) index);
					ExcelUtil.createRow(row, Integer.valueOf(classDay+"") + 2, null, HSSFCell.CELL_TYPE_STRING,data.get("name"));//????????????
					
					Position position = positionService.getByEmpId((Long)data.get("empId"));
					ExcelUtil.createRow(row, 0, null, HSSFCell.CELL_TYPE_STRING,data.get("empName"));
					ExcelUtil.createRow(row, 1, null, HSSFCell.CELL_TYPE_STRING,null == position ? "" : position.getPositionName());
					ExcelUtil.createRow(row, 2, null, HSSFCell.CELL_TYPE_NUMERIC,data.get("shouldTime"));//???????????????
					ExcelUtil.createRow(row, 34, null, HSSFCell.CELL_TYPE_NUMERIC,(Double)data.get("mustAttnTime"));
					
					mustAttnTime = (Double)data.get("mustAttnTime");
					empId = (Long)data.get("empId");
					index++;
				}
			}
		} else {
			ExcelUtil.createRow(sheet.createRow(1), 0, null,HSSFCell.CELL_TYPE_STRING, "?????????");
		}
		
		ClassSetting classSetting = new ClassSetting();
		classSetting.setDepartId(list.get(0).getDepartId());
		List<ClassSetting> classSettingList = classSettingMapper.getListByCondition(classSetting);
		
		HSSFSheet sheet1 = workbook.createSheet("??????");
		//?????????????????????
		HSSFRow row = sheet1.createRow((short) 0);
		//???????????????????????????
		ExcelUtil.createRow(row, 0, null, HSSFCell.CELL_TYPE_STRING,"????????????");
		ExcelUtil.createRow(row, 1, null, HSSFCell.CELL_TYPE_STRING,"????????????");
		ExcelUtil.createRow(row, 2, null, HSSFCell.CELL_TYPE_STRING,"????????????");
		ExcelUtil.createRow(row, 3, null, HSSFCell.CELL_TYPE_STRING,"???????????????????????????");
		
		if(classSettingList!=null&&classSettingList.size()>0){
			for(int i=0;i<classSettingList.size();i++){
				row = sheet1.createRow((short) (i+1));
				ExcelUtil.createRow(row, 0, null, HSSFCell.CELL_TYPE_STRING,classSettingList.get(i).getName());
				ExcelUtil.createRow(row, 1, null, HSSFCell.CELL_TYPE_STRING,DateUtils.format(classSettingList.get(i).getStartTime(), "HH:mm"));
				ExcelUtil.createRow(row, 2, null, HSSFCell.CELL_TYPE_STRING,DateUtils.format(classSettingList.get(i).getEndTime(), "HH:mm"));
				ExcelUtil.createRow(row, 3, null, HSSFCell.CELL_TYPE_NUMERIC,(Double)classSettingList.get(i).getMustAttnTime());
			}
		}

		SendMailUtil.sendExcelMail(workbook,toMail,subject,sttachmentName);
	}
	
	public Map<String,String[]> getEmpClassReportTitle(String yearMonth){
		Map<String,String[]> titleMap = new HashMap<String,String[]>();		
		String[] titles2 = {"??????", "??????", "???????????????","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",""};
		String[] titles3 = {"","","","1", "2", "3" ,"4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", 
				"16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27","","","","",""};
		
		//????????????????????????
		Calendar cal = Calendar.getInstance();
		cal.setTime(DateUtils.parse(yearMonth + "-01 00:00:00"));
		Integer maxDay = cal.getActualMaximum(Calendar.DATE);
		
		//???????????????????????????
		if(maxDay == 28){
			titles3[30] = "28";
			titles2[31] = "???????????????";
		}else if(maxDay == 29){
			titles3[30] = "28";
			titles3[31] = "29";
			titles2[32] = "???????????????";
		}else if(maxDay == 30){
			titles3[30] = "28";
			titles3[31] = "29";
			titles3[32] = "30";
			titles2[33] = "???????????????";
		}else{
			titles3[30] = "28";
			titles3[31] = "29";
			titles3[32] = "30";
			titles3[33] = "31";
			titles2[34] = "???????????????";
		}
		
		titleMap.put("t3", titles3);
		
		//???????????????????????????
		String date = "";
		for(int index = 3;index < titles3.length - 1;index++){
			date = titles3[index];
			if(StringUtils.isNotBlank(date)){
				titles2[index] = DateUtils.getWeek(DateUtils.parse(yearMonth + "-" + date + " 00:00:00"));
			}
		}
		titleMap.put("t2", titles2);
		
		return titleMap;
	}

	@Override
	public List<ApplicationEmployeeClass> getPassList(
			ApplicationEmployeeClass applicationEmployeeClass) {
		return applicationEmployeeClassMapper.getPassList(applicationEmployeeClass);
	}

	@Override
	public HSSFWorkbook exportClass(String month, Long departId,Long groupId) throws Exception{
		ApplicationEmployeeClass employClass = new ApplicationEmployeeClass();
		Date now = new Date();
		if(month==null||"".equals(month)){
			month = DateUtils.format(now, "yyyy-MM");
		}else{
			now = DateUtils.parse(month+"-01", DateUtils.FORMAT_SHORT);
		}
		Date fristDay = DateUtils.getFirstDay(now);
		Date lastDay = DateUtils.getLastDay(now);
		employClass.setStartTime(fristDay);
		employClass.setEndTime(lastDay);
		employClass.setDepartId(departId);
		if(groupId!=null){
			employClass.setGroupId(groupId);
		}
		
		List<Map<String,Object>> datas = applicationEmployeeClassMapper.getEmpClassReprotById(employClass);
		
		Employee currentEmployee = employeeService.getCurrentEmployee();
		String toMail = currentEmployee.getEmail();
		String departName = departService.getDepartAllLeaveName(departId);
		String fileName = departName+"-"+DateUtils.format(fristDay, "yyyyMM");
		String subject = fileName;
		String sttachmentName = fileName;
		String excelName = fileName;
		HSSFWorkbook workbook = new HSSFWorkbook();
		
		HSSFSheet sheet = workbook.createSheet(excelName);
		if (datas != null && datas.size() > 0) {
			String yearMonth = (String)datas.get(0).get("yearMonth");
			Map<String,String[]> titleMap = getEmpClassReportTitle(yearMonth);//??????????????????
			String[] title2 = titleMap.get("t2");//???????????????
			String[] title3 = titleMap.get("t3");//???????????????
			// ??????????????????
			HSSFCellStyle colstyle = workbook.createCellStyle();
			colstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// ????????????
			colstyle.setBorderBottom(XSSFCellStyle.BORDER_THIN); //?????????
			colstyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);//?????????
			colstyle.setBorderTop(XSSFCellStyle.BORDER_THIN);//?????????
			colstyle.setBorderRight(XSSFCellStyle.BORDER_THIN);//?????????
			
			//?????????????????????
			HSSFRow row = sheet.createRow((short) 0);
			//???????????????????????????
			ExcelUtil.createRow(row, 0, null, HSSFCell.CELL_TYPE_STRING,yearMonth+"?????????");
			sheet.addMergedRegion(new CellRangeAddress(0,0,0,2));
			
			//?????????????????????
			row = sheet.createRow((short) 1);
			//???????????????????????????
			for(int colIndex = 0; colIndex < title2.length; colIndex++){
				if(colIndex >=0 && colIndex <= 2){
					sheet.setColumnWidth(colIndex, 5000);//??????????????????
				}
				
				Object obj = title2[colIndex];
				if(obj instanceof Long || obj instanceof Double || obj instanceof Integer || obj instanceof Float || obj instanceof BigDecimal){
					ExcelUtil.createRow(row, colIndex, colstyle, HSSFCell.CELL_TYPE_NUMERIC, obj);
				}else{
					ExcelUtil.createRow(row, colIndex, colstyle, HSSFCell.CELL_TYPE_STRING, obj);
				}
        	}
			sheet.addMergedRegion(new CellRangeAddress(1,2,0,0));
    		sheet.addMergedRegion(new CellRangeAddress(1,2,1,1));
    		sheet.addMergedRegion(new CellRangeAddress(1,2,2,2));
    		sheet.addMergedRegion(new CellRangeAddress(1,2,34,34));
			
			//?????????????????????(???????????????????????????????????????????????????????????????)
			row = sheet.createRow((short) 2);
			//???????????????????????????
			for(int colIndex = 3; colIndex < title3.length; colIndex++){
				if(colIndex >=3 && colIndex <= title3.length - 2){
					sheet.setColumnWidth(colIndex, 1200);//??????????????????
				}else{
					sheet.setColumnWidth(colIndex, 5000);//??????????????????
				}
				
				Object obj = title3[colIndex];
				if(obj instanceof Long || obj instanceof Double || obj instanceof Integer || obj instanceof Float || obj instanceof BigDecimal){
					ExcelUtil.createRow(row, colIndex, colstyle, HSSFCell.CELL_TYPE_NUMERIC, obj);
				}else{
					ExcelUtil.createRow(row, colIndex, colstyle, HSSFCell.CELL_TYPE_STRING, obj);
				}
        	}
			
			Long empId = null;
			Double mustAttnTime = 0.0;
			int index = 3;
			for(Map<String,Object> data : datas){
				Long classDay = Long.valueOf(String.valueOf(data.get("classDay")));
				
				if(null != empId && empId.equals(data.get("empId"))){//????????????????????????
					mustAttnTime += (Double)data.get("mustAttnTime");
					ExcelUtil.createRow(row, 34, null, HSSFCell.CELL_TYPE_NUMERIC,mustAttnTime);
					ExcelUtil.createRow(row, Integer.valueOf(classDay+"") + 2, null, HSSFCell.CELL_TYPE_STRING,data.get("name"));//????????????
				}else{
					row = sheet.createRow((short) index);
					ExcelUtil.createRow(row, Integer.valueOf(classDay+"") + 2, null, HSSFCell.CELL_TYPE_STRING,data.get("name"));//????????????
					
					Position position = positionService.getByEmpId((Long)data.get("empId"));
					ExcelUtil.createRow(row, 0, null, HSSFCell.CELL_TYPE_STRING,data.get("empName"));
					ExcelUtil.createRow(row, 1, null, HSSFCell.CELL_TYPE_STRING,null == position ? "" : position.getPositionName());
					ExcelUtil.createRow(row, 2, null, HSSFCell.CELL_TYPE_NUMERIC,data.get("shouldTime"));//???????????????
					ExcelUtil.createRow(row, 34, null, HSSFCell.CELL_TYPE_NUMERIC,(Double)data.get("mustAttnTime"));
					
					mustAttnTime = (Double)data.get("mustAttnTime");
					empId = (Long)data.get("empId");
					index++;
				}
			}
		} else {
			ExcelUtil.createRow(sheet.createRow(1), 0, null,HSSFCell.CELL_TYPE_STRING, "?????????");
		}
		
		ClassSetting classSetting = new ClassSetting();
		List<ClassSetting> classSettingList = classSettingMapper.getListByCondition(classSetting);
		
		HSSFSheet sheet1 = workbook.createSheet("??????");
		//?????????????????????
		HSSFRow row = sheet1.createRow((short) 0);
		//???????????????????????????
		ExcelUtil.createRow(row, 0, null, HSSFCell.CELL_TYPE_STRING,"????????????");
		ExcelUtil.createRow(row, 1, null, HSSFCell.CELL_TYPE_STRING,"??????");
		ExcelUtil.createRow(row, 2, null, HSSFCell.CELL_TYPE_STRING,"????????????");
		ExcelUtil.createRow(row, 3, null, HSSFCell.CELL_TYPE_STRING,"????????????");
		ExcelUtil.createRow(row, 4, null, HSSFCell.CELL_TYPE_STRING,"???????????????????????????");
		
		if(classSettingList!=null&&classSettingList.size()>0){
			for(int i=0;i<classSettingList.size();i++){
				row = sheet1.createRow((short) (i+1));
				ExcelUtil.createRow(row, 0, null, HSSFCell.CELL_TYPE_STRING,classSettingList.get(i).getFullName());
				ExcelUtil.createRow(row, 1, null, HSSFCell.CELL_TYPE_STRING,classSettingList.get(i).getName());
				ExcelUtil.createRow(row, 2, null, HSSFCell.CELL_TYPE_STRING,DateUtils.format(classSettingList.get(i).getStartTime(), "HH:mm"));
				ExcelUtil.createRow(row, 3, null, HSSFCell.CELL_TYPE_STRING,DateUtils.format(classSettingList.get(i).getEndTime(), "HH:mm"));
				ExcelUtil.createRow(row, 4, null, HSSFCell.CELL_TYPE_NUMERIC,(Double)classSettingList.get(i).getMustAttnTime());
			}
		}

		return workbook;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void endHandle(String processInstanceId, User user) {
		//????????????????????????
		ApplicationEmployeeClass employClass = applicationEmployeeClassMapper.queryByProcessInstanceId(processInstanceId);
		if(employClass != null){
			Map<Date,AnnualVacation> vacationMap = annualVacationService.getVacationMap(employClass.getClassMonth());
			ApplicationEmployeeClassDetail param = new ApplicationEmployeeClassDetail();
			param.setAttnApplicationEmployClassId(employClass.getId());
			
			if(employClass.getIsMove().intValue()==0){
				    param.setIsMove(0);
				    List<ApplicationEmployeeClassDetail> list = applicationEmployeeClassDetailMapper.selectByCondition(param);
				    //????????????
					for(ApplicationEmployeeClassDetail classDetail:list){
						if(classDetail.getClassSettingId()!=null){
							EmployeeClass attnEmployeeClass = new EmployeeClass();
							attnEmployeeClass.setCompanyId(classDetail.getCompanyId());
							attnEmployeeClass.setDepartId(employClass.getDepartId());
							attnEmployeeClass.setEmployId(classDetail.getEmployId());
							attnEmployeeClass.setEmployName(classDetail.getEmployName());
							attnEmployeeClass.setClassDate(classDetail.getClassDate());
							attnEmployeeClass.setClassSettingId(classDetail.getClassSettingId());
							attnEmployeeClass.setClassSettingPerson(classDetail.getClassSettingPerson());
							attnEmployeeClass.setShouldTime(classDetail.getShouldTime());
							attnEmployeeClass.setCreateTime(new Date());
							attnEmployeeClass.setCreateUser(user.getEmployee().getCnName());
							attnEmployeeClass.setGroupId(employClass.getGroupId());
							employeeClassMapper.save(attnEmployeeClass);
							//??????????????????????????????????????????????????????
							if(vacationMap!=null&&vacationMap.containsKey(classDetail.getClassDate())){
								ClassSetting setting = classSettingMapper.getById(classDetail.getClassSettingId());
								EmployeeDuty duty = new EmployeeDuty();
								duty.setCompanyId(user.getCompanyId());
								duty.setDepartId(employClass.getDepartId());
								duty.setEmployId(classDetail.getEmployId());
								duty.setEmployName(classDetail.getEmployName());
								duty.setYear(DateUtils.format(employClass.getClassMonth(), "yyyy"));
								duty.setVacationName(vacationMap.get(classDetail.getClassDate()).getSubject());
								duty.setDutyDate(classDetail.getClassDate());
								duty.setDutyItem("?????????????????????");
								duty.setStartTime(DateUtils.parse(DateUtils.format(classDetail.getClassDate(), DateUtils.FORMAT_SHORT)+" "+DateUtils.format(setting.getStartTime(), DateUtils.FORMAT_HH_MM_SS),
										DateUtils.FORMAT_LONG));
								duty.setEndTime(DateUtils.parse(DateUtils.format(classDetail.getClassDate(), DateUtils.FORMAT_SHORT)+" "+DateUtils.format(setting.getEndTime(), DateUtils.FORMAT_HH_MM_SS),
										DateUtils.FORMAT_LONG));
								duty.setWorkHours(setting.getMustAttnTime());
								duty.setClassSettingPerson(employClass.getClassSettingPerson());
								duty.setCreateTime(new Date());
								duty.setCreateUser(user.getEmployee().getCnName());
								duty.setDelFlag(0);
								duty.setSource(1);
								employeeDutyMapper.save(duty);
							}
						}
					}
			}else if(employClass.getIsMove().intValue()==1){
				param.setIsMove(1);
				List<ApplicationEmployeeClassDetail> list = applicationEmployeeClassDetailMapper.selectByCondition(param);
				//????????????
				for(ApplicationEmployeeClassDetail classDetail:list){
					//??????????????????????????????
					EmployeeClass con = new EmployeeClass();
					con.setEmployId(classDetail.getEmployId());
					con.setClassDate(classDetail.getClassDate());
					EmployeeClass employeeClassList = employeeClassMapper.getByEmployIdAndDate(classDetail.getEmployId(),classDetail.getClassDate());
					if(employeeClassList!=null){
						if(classDetail.getClassSettingId()!=null){
							con.setClassSettingId(classDetail.getClassSettingId());
							con.setUpdateTime(new Date());
							con.setUpdateUser(user.getEmployee().getCnName());
							con.setId(employeeClassList.getId());
							con.setClassSettingPerson(employeeClassList.getClassSettingPerson());
							con.setVersion(employeeClassList.getVersion());
							employeeClassMapper.updateById(con);
							//??????????????????????????????????????????????????????
							if(vacationMap!=null&&vacationMap.containsKey(classDetail.getClassDate())){
								ClassSetting setting = classSettingMapper.getById(classDetail.getClassSettingId());
								EmployeeDuty duty = new EmployeeDuty();
								duty.setEmployId(classDetail.getEmployId());
								duty.setDutyDate(classDetail.getClassDate());
								duty.setDutyItem("?????????????????????");
								duty.setStartTime(DateUtils.parse(DateUtils.format(classDetail.getClassDate(), DateUtils.FORMAT_SHORT)+" "+DateUtils.format(setting.getStartTime(), DateUtils.FORMAT_HH_MM_SS),
										DateUtils.FORMAT_LONG));
								duty.setEndTime(DateUtils.parse(DateUtils.format(classDetail.getClassDate(), DateUtils.FORMAT_SHORT)+" "+DateUtils.format(setting.getEndTime(), DateUtils.FORMAT_HH_MM_SS),
										DateUtils.FORMAT_LONG));
								duty.setWorkHours(setting.getMustAttnTime());
								duty.setClassSettingPerson(employClass.getClassSettingPerson());
								duty.setUpdateTime(new Date());
								duty.setUpdateUser(user.getEmployee().getCnName());
								duty.setSource(1);
								employeeDutyMapper.update(duty);
							}
						}else{
							con.setClassSettingId(null);
							con.setUpdateTime(new Date());
							con.setUpdateUser(user.getEmployee().getCnName());
							con.setId(employeeClassList.getId());
							con.setClassSettingPerson(employeeClassList.getClassSettingPerson());
							con.setVersion(employeeClassList.getVersion());
							con.setDelFlag(1);
							employeeClassMapper.updateById(con);
							//??????????????????????????????????????????????????????
							if(vacationMap!=null&&vacationMap.containsKey(classDetail.getClassDate())){
								EmployeeDuty duty = new EmployeeDuty();
								duty.setEmployId(classDetail.getEmployId());
								duty.setDutyDate(classDetail.getClassDate());
								duty.setDutyItem("?????????????????????");
								duty.setUpdateTime(new Date());
								duty.setUpdateUser(user.getEmployee().getCnName());
								duty.setDelFlag(1);
								employeeDutyMapper.update(duty);
							}
						}
					}else{
						if(classDetail.getClassSettingId()!=null){
							EmployeeClass attnEmployeeClass = new EmployeeClass();
							attnEmployeeClass.setCompanyId(classDetail.getCompanyId());
							attnEmployeeClass.setDepartId(employClass.getDepartId());
							attnEmployeeClass.setEmployId(classDetail.getEmployId());
							attnEmployeeClass.setEmployName(classDetail.getEmployName());
							attnEmployeeClass.setClassDate(classDetail.getClassDate());
							attnEmployeeClass.setClassSettingId(classDetail.getClassSettingId());
							attnEmployeeClass.setClassSettingPerson(classDetail.getClassSettingPerson());
							attnEmployeeClass.setShouldTime(classDetail.getShouldTime());
							attnEmployeeClass.setCreateTime(new Date());
							attnEmployeeClass.setCreateUser(user.getEmployee().getCnName());
							attnEmployeeClass.setGroupId(employClass.getGroupId());
							employeeClassMapper.save(attnEmployeeClass);
							//??????????????????????????????????????????????????????
							if(vacationMap!=null&&vacationMap.containsKey(classDetail.getClassDate())){
								ClassSetting setting = classSettingMapper.getById(classDetail.getClassSettingId());
								EmployeeDuty duty = new EmployeeDuty();
								duty.setCompanyId(user.getCompanyId());
								duty.setDepartId(employClass.getDepartId());
								duty.setEmployId(classDetail.getEmployId());
								duty.setEmployName(classDetail.getEmployName());
								duty.setYear(DateUtils.format(employClass.getClassMonth(), "yyyy"));
								duty.setVacationName(vacationMap.get(classDetail.getClassDate()).getSubject());
								duty.setDutyDate(classDetail.getClassDate());
								duty.setDutyItem("?????????????????????");
								duty.setStartTime(DateUtils.parse(DateUtils.format(classDetail.getClassDate(), DateUtils.FORMAT_SHORT)+" "+DateUtils.format(setting.getStartTime(), DateUtils.FORMAT_HH_MM_SS),
										DateUtils.FORMAT_LONG));
								duty.setEndTime(DateUtils.parse(DateUtils.format(classDetail.getClassDate(), DateUtils.FORMAT_SHORT)+" "+DateUtils.format(setting.getEndTime(), DateUtils.FORMAT_HH_MM_SS),
										DateUtils.FORMAT_LONG));
								duty.setWorkHours(setting.getMustAttnTime());
								duty.setClassSettingPerson(employClass.getClassSettingPerson());
								duty.setCreateTime(new Date());
								duty.setCreateUser(user.getEmployee().getCnName());
								duty.setDelFlag(0);
								duty.setSource(1);
								employeeDutyMapper.save(duty);
							}
						}
					}
				}
				//??????????????????
				if(list.get(0).getClassDate().getTime()<DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_SHORT), DateUtils.FORMAT_SHORT).getTime()){
					TransNormal transNormal = new TransNormal();
					Map<Long,String> employeeIdMap = new HashMap<Long,String>();
					String employeeIds = "";
					for(ApplicationEmployeeClassDetail de:list){
						if(!employeeIdMap.containsKey(de.getEmployId())){
							employeeIdMap.put(de.getEmployId(), String.valueOf(de.getEmployId()));
							employeeIds += String.valueOf(de.getEmployId())+",";
						}
					}
					
					transNormal.setEmployeeIds(employeeIds.substring(0, employeeIds.length()-1));
					transNormal.setStartTime(list.get(0).getClassDate());
					transNormal.setEndTime(list.get(list.size()-1).getClassDate());
					transNormalService.recalculateAttnByCondition(transNormal);
			
				    //??????????????????
				}
			}
		}
	}

	@Override
	public ApplicationEmployeeClass queryByProcessInstanceId(
			String processInstanceId) {
		return applicationEmployeeClassMapper.queryByProcessInstanceId(processInstanceId);
	}

	@Override
	public void setValueToVO(TaskVO taskVO, String processInstanceId) {
		ApplicationEmployeeClass employeeClass = applicationEmployeeClassMapper.queryByProcessInstanceId(processInstanceId);
		if(employeeClass!=null){
			taskVO.setProcessName("????????????");
			if(employeeClass.getIsMove().intValue()==1){
				taskVO.setProcessName("????????????");
			}
			taskVO.setCreatorDepart(employeeClass.getDepartName());
			taskVO.setCreator(employeeClass.getClassSettingPerson());
			taskVO.setCreateTime(employeeClass.getCreateTime());
			taskVO.setReProcdefCode("50");
			taskVO.setProcessId(employeeClass.getProcessInstanceId());
			taskVO.setResourceId(String.valueOf(employeeClass.getId()));
			taskVO.setRedirectUrl("/employeeClass/approveNormal.htm?flag=no&employeeClassId="+employeeClass.getId());
			if(!(taskVO.getProcessStatu()==null)) {
				taskVO.setRedirectUrl("/employeeClass/approveNormal.htm?flag=can&employeeClassId="+employeeClass.getId());
			}
			taskVO.setProcessStatu(employeeClass.getApprovalStatus());
			taskVO.setOperation(ConfigConstants.STATUS.getOperationByStatu(employeeClass.getApprovalStatus()));
		}
	}

	@Override
	public void updateProcessInstanceId(ApplicationEmployeeClass newScheduling) {
		applicationEmployeeClassMapper.updateById(newScheduling);
	}
    
	//??????????????????
	@Override
	public PageModel<ApplicationEmployeeClass> getHandlingListByPage(
			ApplicationEmployeeClass employeeclass) {
		employeeclass.setApprovalStatus(ConfigConstants.DOING_STATUS);//?????????
		employeeclass.setAssignee(String.valueOf(userService.getCurrentUser().getEmployee().getId()));//?????????
		if(employeeclass.getFirstDepart()!=null&&!"".equals(employeeclass.getFirstDepart())){//??????
			employeeclass.setDepartId(Long.valueOf(employeeclass.getFirstDepart()));
		}
		if(employeeclass.getMonth()!=null&&!"".equals(employeeclass.getMonth())){//??????
			employeeclass.setClassMonth(DateUtils.parse(employeeclass.getMonth()+"-01", DateUtils.FORMAT_SHORT));
		}
		int page = employeeclass.getPage() == null ? 0 : employeeclass.getPage();
		int rows = employeeclass.getRows() == null ? 0 : employeeclass.getRows();
		
		PageModel<ApplicationEmployeeClass> pm = new PageModel<ApplicationEmployeeClass>();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		Integer total = applicationEmployeeClassMapper.getHandlingCount(employeeclass);
		pm.setTotal(total);
		
		employeeclass.setOffset(pm.getOffset());
		employeeclass.setLimit(pm.getLimit());
		
		List<ApplicationEmployeeClass> list = applicationEmployeeClassMapper.getHandlingListByPage(employeeclass);
        
		Map<Date,Double> isExistShouldTime = new HashMap<Date,Double>();//???????????????????????????????????????
		Map<Long,String> isExistGroupName = new HashMap<Long,String>();//????????????????????????
		for(ApplicationEmployeeClass data:list){
			if(isExistShouldTime!=null&&isExistShouldTime.containsKey(data.getClassMonth())){
				data.setShouldTime(isExistShouldTime.get(data.getClassMonth()));
			}else{
				Double shouldTime = getShouldTime(data.getClassMonth(),DateUtils.getLastDay(data.getClassMonth()));
				data.setShouldTime(shouldTime);
				isExistShouldTime.put(data.getClassMonth(), shouldTime);
			}
			if(isExistGroupName!=null&&isExistGroupName.containsKey(data.getGroupId())){
				data.setGroupName(isExistGroupName.get(data.getGroupId()).split(",")[0]);
				data.setAuditorName(isExistGroupName.get(data.getGroupId()).split(",")[1]);
			}else{
				ScheduleGroup scheduleGroup = scheduleGroupMapper.getGroupById(data.getGroupId()!=null?data.getGroupId():0L);
				Employee employee = employeeMapper.getById(scheduleGroup!=null?scheduleGroup.getAuditor():0L);
				data.setGroupName(scheduleGroup!=null?scheduleGroup.getName():" ");
				data.setAuditorName(employee!=null?employee.getCnName():" ");
				isExistGroupName.put(data.getGroupId(), (scheduleGroup!=null?scheduleGroup.getName():" ")+","+(employee!=null?employee.getCnName():" "));
			}
		}
		
		pm.setRows(list);
		return pm;
	}
	
    //?????????????????????????????????
	@Override
	public Double getShouldTime(Date startTime,Date endTime){
		
		boolean isEndTime1 = true;
	    double should_time = 0;
	    do{
	    	if(annualVacationService.judgeWorkOrNot(startTime)){//????????????????????????????????????
	    		should_time += 8;
			}
	    	startTime = DateUtils.addDay(startTime, 1);
			if(DateUtils.getIntervalDays(startTime, endTime) < 0) {
				isEndTime1 = false;
			}
		} while(isEndTime1);
	    
		return should_time;
	
	}

	@Override
	public Map<String,Object> getDetailById(Long id) {
		
		ApplicationEmployeeClass employeeClass = applicationEmployeeClassMapper.getById(id);//?????????
		
		//??????????????????
		List<ClassSetting> settList = classSettingMapper.getList();
		Map<String,Double> setMap = new HashMap<String,Double>();
		for(ClassSetting set:settList){
			setMap.put(String.valueOf(set.getId()), set.getMustAttnTime());
		}
		
		Map<String,Object> result = new HashMap<String,Object>();//????????????
		//????????????????????????
		List<String> weekDays = new ArrayList<String>();
		//???????????????
		List<String> days = new ArrayList<String>();
		weekDays.add("??????");
		weekDays.add("??????");
		weekDays.add("???????????????");
		weekDays.add("????????????");
		weekDays.add("????????????");
		days.add("");
		days.add("");
		days.add("");
		days.add("");
		days.add("");
		
		Date fristDay = DateUtils.getFirstDay(employeeClass.getClassMonth());
		Date lastDay = DateUtils.getLastDay(employeeClass.getClassMonth());
		//??????
		result.put("title", employeeClass.getDepartName()+"?????????????????????"+"-"+DateUtils.format(employeeClass.getClassMonth(), "yyyy???MM???"));
		//????????????
		result.put("applyDate", DateUtils.format(employeeClass.getCreateTime(), DateUtils.FORMAT_SHORT));
		//????????????
		if(employeeClass!=null&&
				employeeClass.getApprovalStatus().intValue()==ConfigConstants.DOING_STATUS.intValue()){
			result.put("approvalStatus", "?????????");
		}else if(employeeClass!=null&&
				employeeClass.getApprovalStatus().intValue()==ConfigConstants.PASS_STATUS.intValue()){
			result.put("approvalStatus", "?????????");
		}else if(employeeClass!=null&&
				employeeClass.getApprovalStatus().intValue()==ConfigConstants.REFUSE_STATUS.intValue()){
			result.put("approvalStatus", "?????????");
		}else if(employeeClass!=null&&
				employeeClass.getApprovalStatus().intValue()==ConfigConstants.BACK_STATUS.intValue()){
			result.put("approvalStatus", "?????????");
		}else if(employeeClass!=null&&
				employeeClass.getApprovalStatus().intValue()==ConfigConstants.OVERDUE_STATUS.intValue()){
			result.put("approvalStatus", "?????????");
		}else if(employeeClass!=null&&
				employeeClass.getApprovalStatus().intValue()==ConfigConstants.OVERDUEPASS_STATUS.intValue()){
			result.put("approvalStatus", "????????????");
		}else if(employeeClass!=null&&
				employeeClass.getApprovalStatus().intValue()==ConfigConstants.OVERDUEREFUSE_STATUS.intValue()){
			result.put("approvalStatus", "????????????");
		}
		
		//????????????
		result.put("groupName","");
		if(employeeClass.getGroupId()!=null){
			ScheduleGroup group = scheduleGroupMapper.getGroupById(employeeClass.getGroupId());
			result.put("groupName",group.getName());
		}
		
		List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
		
		List<Map<String,Object>> datas1 = new ArrayList<Map<String,Object>>();
        
		//?????????????????????????????????????????????????????????????????????
		if(employeeClass.getIsMove().intValue()==ApplicationEmployeeClass.IS_MOVE_0){
			datas = applicationEmployeeClassMapper.getDetailById(employeeClass.getId(),0L);
			datas1 = applicationEmployeeClassMapper.getClassHoursMap(employeeClass.getId());//??????????????????????????????
			//??????????????????
			for(Map<String,Object> data1 : datas1){
				String employ_id = String.valueOf(data1.get("employ_id"));
				Position position = positionService.getByEmpId((Long)data1.get("employ_id"));
				data1.put("positionName", position!=null?position.getPositionName():"");
				for(Map<String,Object> data : datas){
					String empId = String.valueOf(data.get("empId"));
					if(employ_id.equals(empId)&&data.get("classSettingId")!=null){
						data1.put(String.valueOf(data.get("classDay")), String.valueOf(data.get("name"))+","+String.valueOf(data.get("classDate"))+","+String.valueOf(data.get("classSettingId"))+",notMove");
					}
				}
			}
		}else if(employeeClass.getIsMove().intValue()==ApplicationEmployeeClass.IS_MOVE_1){
			//??????
			result.put("title", employeeClass.getDepartName()+"?????????????????????"+"-"+DateUtils.format(employeeClass.getClassMonth(), "yyyy???MM???"));
			//??????????????????????????????
			List<Long> emoloyIds = applicationEmployeeClassMapper.getEmployeeIdListById(employeeClass.getId());
			//?????????????????????????????????????????????????????????????????????
			EmployeeClass param1 = new EmployeeClass();
			param1.setDepartId(employeeClass.getDepartId());
			param1.setStartTime(fristDay);
			param1.setEndTime(lastDay);
			param1.setEmployeeIds(emoloyIds);
			datas1 = employeeClassMapper.getClassHoursMap(param1);//??????????????????
			if(datas1 ==null|| datas1.size()<=0) {
				Double sd = getShouldTime(fristDay,lastDay);
				datas1 = applicationEmployeeClassMapper.getClassHoursMap(employeeClass.getId());
				for(Map<String,Object> data : datas1) {
					data.put("dayCount", 0);
					data.put("must_attn_time", 0d);
					data.put("should_time", sd);
				}
			}
			
			//?????????????????????????????????
			ApplicationEmployeeClass param = new ApplicationEmployeeClass();
			param.setDepartId(employeeClass.getDepartId());
			param.setStartTime(fristDay);
			param.setEndTime(lastDay);
			param.setEmployeeIds(emoloyIds);
			datas = applicationEmployeeClassMapper.getEmpClassReprotById(param);
			//??????????????????????????????
			for(Map<String,Object> data1 : datas1){
				String employ_id = String.valueOf(data1.get("employ_id"));
				Position position = positionService.getByEmpId((Long)data1.get("employ_id"));
				data1.put("positionName", position!=null?position.getPositionName():"");
				for(Map<String,Object> data : datas){
					String empId = String.valueOf(data.get("empId"));
					if(employ_id.equals(empId)){
						data1.put(String.valueOf(data.get("classDay")), String.valueOf(data.get("name"))+","+String.valueOf(data.get("classDate"))+","+String.valueOf(data.get("classSettingId"))+",notMove");
					}
				}
			}
			
			//???????????????????????????????????????
			datas = applicationEmployeeClassMapper.getDetailById(employeeClass.getId(),0L);
			//??????????????????
			for(Map<String,Object> data1 : datas1){
				String employ_id = String.valueOf(data1.get("employ_id"));
				for(Map<String,Object> data : datas){
					String empId = String.valueOf(data.get("empId"));
					if(employ_id.equals(empId)){
						if(data.get("classSettingId")!=null){
							data1.put(String.valueOf(data.get("classDay")), String.valueOf(data.get("name"))+","+String.valueOf(data.get("classDate"))+","+String.valueOf(data.get("classSettingId"))+",move");
						}else{
							data1.put(String.valueOf(data.get("classDay")), "???,"+String.valueOf(data.get("classDate"))+",0"+",move");
						}
					}
				}
			}
			//???????????????????????????
			//??????????????????
			datas = applicationEmployeeClassMapper.getDetailById(employeeClass.getId(),1L);
			for(Map<String,Object> data1 : datas1){
				String employ_id = String.valueOf(data1.get("employ_id"));
				for(Map<String,Object> data : datas){
					String empId = String.valueOf(data.get("empId"));
					if(employ_id.equals(empId)){
						if(data1.containsKey(String.valueOf(data.get("classDay")))){
							//????????????????????????????????????
							Long nowSetId = employeeClassMapper.getClassSettingIdByEmployIdAndDate(Long.valueOf(employ_id),DateUtils.parse(String.valueOf(data.get("classDate")), DateUtils.FORMAT_SHORT));
							if(data.get("classSettingId")!=null){
								if(nowSetId!=null){
									//?????????????????????
									data1.put("must_attn_time", 
											(Double)data1.get("must_attn_time")
											-(setMap.containsKey(String.valueOf(nowSetId))?setMap.get(String.valueOf(nowSetId)):0)
											+(setMap.containsKey(String.valueOf(data.get("classSettingId")))?setMap.get(String.valueOf(data.get("classSettingId"))):0));
								}else{
									//????????????????????????????????????
									data1.put("must_attn_time", (Double)data1.get("must_attn_time")
											+(setMap.containsKey(String.valueOf(data.get("classSettingId")))?setMap.get(String.valueOf(data.get("classSettingId"))):0));
									data1.put("dayCount", Integer.valueOf(String.valueOf(data1.get("dayCount")))+1);
								}
								data1.put(String.valueOf(data.get("classDay")), data1.get(String.valueOf(data.get("classDay")))+","+String.valueOf(data.get("name"))+","+String.valueOf(data.get("classSettingId")));
							}else{
								if(nowSetId!=null){
									//?????????????????????
									data1.put("must_attn_time", (Double)data1.get("must_attn_time")
											-(setMap.containsKey(String.valueOf(nowSetId))?setMap.get(String.valueOf(nowSetId)):0));
									data1.put("dayCount", Integer.valueOf(String.valueOf(data1.get("dayCount")))-1);
								}
								data1.put(String.valueOf(data.get("classDay")), data1.get(String.valueOf(data.get("classDay")))+",???,0");
							}
						}
					}
				}
			}
		}
		int i = 1;
		while(true){
			String week = DateUtils.getWeek(fristDay);
			weekDays.add(week);
			days.add(String.valueOf(i));
			fristDay = DateUtils.addDay(fristDay, 1);
			i = i + 1;
			if(!DateUtils.format(fristDay, "yyyy-MM").equals(DateUtils.format(lastDay, "yyyy-MM"))){
				break;
			}
		}
		result.put("weekDays", weekDays);
		result.put("days", days);
		result.put("classDetail", datas1);
		result.put("count", 0);
		if(datas1!=null&&datas1.size()>0){
			result.put("count", datas1.size());
		}
		
		return result;
	}

	@Override
	public PageModel<ApplicationEmployeeClass> getHandledListByPage(
			ApplicationEmployeeClass employeeclass) {
		employeeclass.setAssignee(String.valueOf(userService.getCurrentUser().getEmployee().getId()));//?????????
		if(employeeclass.getFirstDepart()!=null&&!"".equals(employeeclass.getFirstDepart())){//??????
			employeeclass.setDepartId(Long.valueOf(employeeclass.getFirstDepart()));
		}
		if(employeeclass.getMonth()!=null&&!"".equals(employeeclass.getMonth())){//??????
			employeeclass.setClassMonth(DateUtils.parse(employeeclass.getMonth()+"-01", DateUtils.FORMAT_SHORT));
		}
		int page = employeeclass.getPage() == null ? 0 : employeeclass.getPage();
		int rows = employeeclass.getRows() == null ? 0 : employeeclass.getRows();
		
		PageModel<ApplicationEmployeeClass> pm = new PageModel<ApplicationEmployeeClass>();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		Integer total = applicationEmployeeClassMapper.getHandledCount(employeeclass);
		pm.setTotal(total);
		
		employeeclass.setOffset(pm.getOffset());
		employeeclass.setLimit(pm.getLimit());
		
		List<ApplicationEmployeeClass> list = applicationEmployeeClassMapper.getHandledListByPage(employeeclass);
        
		Map<Date,Double> isExistShouldTime = new HashMap<Date,Double>();//???????????????????????????????????????
		Map<Long,String> isExistGroupName = new HashMap<Long,String>();//????????????????????????
		for(ApplicationEmployeeClass data:list){
			if(isExistShouldTime!=null&&isExistShouldTime.containsKey(data.getClassMonth())){
				data.setShouldTime(isExistShouldTime.get(data.getClassMonth()));
			}else{
				Double shouldTime = getShouldTime(data.getClassMonth(),DateUtils.getLastDay(data.getClassMonth()));
				data.setShouldTime(shouldTime);
				isExistShouldTime.put(data.getClassMonth(), shouldTime);
			}
			if(isExistGroupName!=null&&isExistGroupName.containsKey(data.getGroupId())){
				data.setGroupName(isExistGroupName.get(data.getGroupId()).split(",")[0]);
				data.setAuditorName(isExistGroupName.get(data.getGroupId()).split(",")[1]);
			}else{
				ScheduleGroup scheduleGroup = scheduleGroupMapper.getGroupById(data.getGroupId()!=null?data.getGroupId():0L);
				Employee employee = employeeMapper.getById(scheduleGroup!=null?scheduleGroup.getAuditor():0L);
				data.setGroupName(scheduleGroup!=null?scheduleGroup.getName():" ");
				data.setAuditorName(employee!=null?employee.getCnName():" ");
				isExistGroupName.put(data.getGroupId(), (scheduleGroup!=null?scheduleGroup.getName():" ")+","+(employee!=null?employee.getCnName():" "));
			}
		}
		
		pm.setRows(list);
		return pm;
	}

	@Override
	public PageModel<ApplicationEmployeeClass> getInvalidListByPage(
			ApplicationEmployeeClass employeeclass) {
		employeeclass.setApprovalStatus(ConfigConstants.OVERDUE_STATUS);//?????????
		employeeclass.setAssignee(String.valueOf(userService.getCurrentUser().getEmployee().getId()));//?????????
		if(employeeclass.getFirstDepart()!=null&&!"".equals(employeeclass.getFirstDepart())){//??????
			employeeclass.setDepartId(Long.valueOf(employeeclass.getFirstDepart()));
		}
		if(employeeclass.getMonth()!=null&&!"".equals(employeeclass.getMonth())){//??????
			employeeclass.setClassMonth(DateUtils.parse(employeeclass.getMonth()+"-01", DateUtils.FORMAT_SHORT));
		}
		int page = employeeclass.getPage() == null ? 0 : employeeclass.getPage();
		int rows = employeeclass.getRows() == null ? 0 : employeeclass.getRows();
		
		PageModel<ApplicationEmployeeClass> pm = new PageModel<ApplicationEmployeeClass>();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		Integer total = applicationEmployeeClassMapper.getHandlingCount(employeeclass);
		pm.setTotal(total);
		
		employeeclass.setOffset(pm.getOffset());
		employeeclass.setLimit(pm.getLimit());
		
		List<ApplicationEmployeeClass> list = applicationEmployeeClassMapper.getHandlingListByPage(employeeclass);
        
		Map<Date,Double> isExistShouldTime = new HashMap<Date,Double>();//???????????????????????????????????????
		Map<Long,String> isExistGroupName = new HashMap<Long,String>();//????????????????????????
		for(ApplicationEmployeeClass data:list){
			if(isExistShouldTime!=null&&isExistShouldTime.containsKey(data.getClassMonth())){
				data.setShouldTime(isExistShouldTime.get(data.getClassMonth()));
			}else{
				Double shouldTime = getShouldTime(data.getClassMonth(),DateUtils.getLastDay(data.getClassMonth()));
				data.setShouldTime(shouldTime);
				isExistShouldTime.put(data.getClassMonth(), shouldTime);
			}
			if(isExistGroupName!=null&&isExistGroupName.containsKey(data.getGroupId())){
				data.setGroupName(isExistGroupName.get(data.getGroupId()).split(",")[0]);
				data.setAuditorName(isExistGroupName.get(data.getGroupId()).split(",")[1]);
			}else{
				ScheduleGroup scheduleGroup = scheduleGroupMapper.getGroupById(data.getGroupId()!=null?data.getGroupId():0L);
				Employee employee = employeeMapper.getById(scheduleGroup!=null?scheduleGroup.getAuditor():0L);
				data.setGroupName(scheduleGroup!=null?scheduleGroup.getName():" ");
				data.setAuditorName(employee!=null?employee.getCnName():" ");
				isExistGroupName.put(data.getGroupId(), (scheduleGroup!=null?scheduleGroup.getName():" ")+","+(employee!=null?employee.getCnName():" "));
			}
		}
		
		pm.setRows(list);
		return pm;
	}

	@Override
	public Map<String, String> getSettingInfo(String date, Long setId,
			Long newSetId) {
		Map<String,String> result = new HashMap<String,String>();
		result.put("date",DateUtils.format(DateUtils.parse(date, DateUtils.FORMAT_SHORT), DateUtils.FORMAT_SHORT_CN));//??????
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String[] weekDays = { "?????????", "?????????", "?????????", "?????????", "?????????", "?????????", "?????????" };
        Calendar cal = Calendar.getInstance(); // ??????????????????
        Date datet = null;
        try {
            datet = f.parse(date);
            cal.setTime(datet);
        } catch (ParseException e) {
            logger.error("getSettingInfo??????????????????",e);
        }
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0){
        	w = 0;
        }    
		result.put("weekDay",weekDays[w]);//??????
		//?????????
		if(setId!=null&&setId.intValue()!=0){//0??????????????????id???????????????????????????
			ClassSetting old = classSettingMapper.getById(setId);
			result.put("setName", old!=null?old.getName():"");
			result.put("startTime", old!=null?DateUtils.format(old.getStartTime(), DateUtils.FORMAT_HH_MM):"");
			result.put("endTime", old!=null?DateUtils.format(old.getEndTime(), DateUtils.FORMAT_HH_MM):"");
		}else if(setId!=null&&setId.intValue()==0){
			result.put("setName", "??????");
			result.put("startTime", "");
			result.put("endTime", "");
		}
		//???????????????
		if(newSetId!=null&&newSetId.intValue()!=0){
			ClassSetting set = classSettingMapper.getById(newSetId);
			result.put("newSetName", set!=null?set.getName():"");
			result.put("newStartTime", set!=null?DateUtils.format(set.getStartTime(), DateUtils.FORMAT_HH_MM):"");
			result.put("newEndTime", set!=null?DateUtils.format(set.getEndTime(), DateUtils.FORMAT_HH_MM):"");
		}else if(newSetId!=null&&newSetId.intValue()==0){
			result.put("newSetName", "??????");
			result.put("newStartTime", "");
			result.put("newEndTime", "");
		}
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> hrMoveAtAdmin(HttpServletRequest request,String date, Long employId,
			Long setId,String remark) throws OaException {
		
		logger.info("????????????hrMoveAtAdmin:start??????");
		Long time1 = System.currentTimeMillis();
			
		User user = userService.getCurrentUser();
		logger.info("????????????hrMoveAtAdmin??????:date="+date+";employId="+employId+";setId="+setId
				+";remark="+remark+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));
		
		Map<String,Object> result = new HashMap<String,Object>();
		
		//????????????????????????
		Employee emp = employeeService.getById(employId);
		if(emp==null||emp.getId()==null){
			result.put("message","???????????????????????????????????????");
			result.put("success",false);
			return result;
		}
		
		//????????????????????????????????????
		Date moveDate = DateUtils.parse(date, DateUtils.FORMAT_SHORT);
		if(emp.getFirstEntryTime()!=null&&emp.getFirstEntryTime().getTime()>moveDate.getTime()){
			result.put("message","??????????????????????????????????????????");
			result.put("success",false);
			return result;
		}
		
		double shouldTime = getShouldTime(DateUtils.getFirstDay(moveDate),emp);
		
		//???????????????????????????????????????
		if(emp.getQuitTime()!=null&&emp.getQuitTime().getTime()<moveDate.getTime()){
			result.put("message","???????????????????????????????????????");
			result.put("success",false);
			return result;
		}
		
		//????????????????????????????????????????????????
		Depart depart = departService.getInfoByEmpId(employId);
		if(depart==null||depart.getId()==null){
			result.put("message","?????????????????????????????????????????????");
			result.put("success",false);
			return result;
		}
		ScheduleGroup group = scheduleGroupMapper.getGroupByEmployeeId(employId);//????????????
		//??????????????????
		ApplicationEmployeeClass record = new ApplicationEmployeeClass();
		record.setDepartId(depart.getId());
		record.setDepartName(depart.getName());
		record.setClassMonth(DateUtils.getFirstDay(moveDate));
		record.setEmployeeNum(1);
		record.setClassEmployeeNum(1);//????????????
		record.setClassSettingPerson(user.getEmployee().getCnName());
		record.setApprovalStatus(ConfigConstants.PASS_STATUS);
		record.setVersion(0L);
		record.setDelFlag(0);
		record.setCreateUser(user.getEmployee().getCnName());
		record.setCreateTime(new Date());
		record.setIsMove(1);//??????
		record.setProcessInstanceId(null);//????????????????????????
		record.setEmployeeId(user.getEmployee().getId());
		record.setGroupId(group!=null?group.getId():null);
		record.setMoveType(1);//????????????
		record.setRemark(remark);
		applicationEmployeeClassMapper.save(record);
		List<ApplicationEmployeeClassDetail> moveList = new ArrayList<ApplicationEmployeeClassDetail>();
		//?????????
		ApplicationEmployeeClassDetail recordDetail = new ApplicationEmployeeClassDetail();
		recordDetail.setCompanyId(user.getCompanyId());
		recordDetail.setEmployId(employId);
		recordDetail.setCreateTime(new Date());
		recordDetail.setCreateUser(user.getEmployee().getCnName());
		recordDetail.setClassSettingId((setId!=null&&setId.intValue()!=0)?setId:null);
		recordDetail.setClassSettingPerson(user.getEmployee().getCnName());
		recordDetail.setClassDate(moveDate);
		recordDetail.setDelFlag(0);
		recordDetail.setIsMove(1);
		recordDetail.setAttnApplicationEmployClassId(record.getId());
		recordDetail.setShouldTime(shouldTime);
		recordDetail.setEmployName(emp.getCnName());
		moveList.add(recordDetail);
		//??????????????????
		Long oldSettingId = employeeClassMapper.getClassSettingIdByEmployIdAndDate(employId,moveDate);
		ApplicationEmployeeClassDetail oldRecordDetail = new ApplicationEmployeeClassDetail();
		oldRecordDetail.setCompanyId(user.getCompanyId());
		oldRecordDetail.setEmployId(employId);
		oldRecordDetail.setCreateTime(new Date());
		oldRecordDetail.setCreateUser(user.getEmployee().getCnName());
		oldRecordDetail.setClassSettingId(oldSettingId);
		oldRecordDetail.setClassSettingPerson(user.getEmployee().getCnName());
		oldRecordDetail.setClassDate(moveDate);
		oldRecordDetail.setDelFlag(0);
		oldRecordDetail.setIsMove(0);
		oldRecordDetail.setAttnApplicationEmployClassId(record.getId());
		oldRecordDetail.setShouldTime(shouldTime);
		oldRecordDetail.setEmployName(emp.getCnName());
		moveList.add(oldRecordDetail);
		applicationEmployeeClassDetailMapper.batchSave(moveList);
		
		//??????????????????????????????????????????
		AnnualVacation vacation = new AnnualVacation();
		List<Integer> typeList = new ArrayList<Integer>();
		typeList.add(AnnualVacation.YYPE_LEGAL);
		typeList.add(AnnualVacation.YYPE_VACATION);
		vacation.setTypeList(typeList);
		vacation.setStartDate(moveDate);
		vacation.setEndDate(moveDate);
		List<AnnualVacation> vacationList = annualVacationService.getListByCondition(vacation);
		
		//????????????????????????
		EmployeeClass con = new EmployeeClass();
		con.setEmployId(employId);
		con.setClassDate(moveDate);
		EmployeeClass employeeClassList = employeeClassMapper.getByEmployIdAndDate(employId,moveDate);
		if(employeeClassList!=null){
			if(setId!=null&&setId.intValue()!=0){
				con.setClassSettingId(setId);
				con.setUpdateTime(new Date());
				con.setUpdateUser(user.getEmployee().getCnName());
				con.setId(employeeClassList.getId());
				con.setClassSettingPerson(user.getEmployee().getCnName());
				con.setVersion(employeeClassList.getVersion());
				employeeClassMapper.updateById(con);
				//??????????????????????????????????????????????????????
				if(vacationList!=null&&vacationList.size()>0){
					ClassSetting setting = classSettingMapper.getById(setId);
					EmployeeDuty duty = new EmployeeDuty();
					duty.setEmployId(employId);
					duty.setDutyDate(moveDate);
					duty.setDutyItem("?????????????????????");
					duty.setStartTime(DateUtils.parse(DateUtils.format(moveDate, DateUtils.FORMAT_SHORT)+" "+DateUtils.format(setting.getStartTime(), DateUtils.FORMAT_HH_MM_SS),
							DateUtils.FORMAT_LONG));
					duty.setEndTime(DateUtils.parse(DateUtils.format(moveDate, DateUtils.FORMAT_SHORT)+" "+DateUtils.format(setting.getEndTime(), DateUtils.FORMAT_HH_MM_SS),
							DateUtils.FORMAT_LONG));
					duty.setWorkHours(setting.getMustAttnTime());
					duty.setClassSettingPerson(user.getEmployee().getCnName());
					duty.setUpdateTime(new Date());
					duty.setUpdateUser(user.getEmployee().getCnName());
					duty.setSource(1);
					employeeDutyMapper.update(duty);
				}
			}else{
				con.setClassSettingId(null);
				con.setUpdateTime(new Date());
				con.setUpdateUser(user.getEmployee().getCnName());
				con.setId(employeeClassList.getId());
				con.setClassSettingPerson(employeeClassList.getClassSettingPerson());
				con.setVersion(employeeClassList.getVersion());
				con.setDelFlag(1);
				employeeClassMapper.updateById(con);
				//??????????????????????????????????????????????????????
				if(vacationList!=null&&vacationList.size()>0){
					EmployeeDuty duty = new EmployeeDuty();
					duty.setEmployId(employId);
					duty.setDutyDate(moveDate);
					duty.setDutyItem("?????????????????????");
					duty.setUpdateTime(new Date());
					duty.setUpdateUser(user.getEmployee().getCnName());
					duty.setDelFlag(1);
					employeeDutyMapper.update(duty);
				}
			}
		}else{
			if(setId!=null&&setId.intValue()!=0){
				EmployeeClass attnEmployeeClass = new EmployeeClass();
				attnEmployeeClass.setCompanyId(user.getEmployee().getCompanyId());
				attnEmployeeClass.setDepartId(depart.getId());
				attnEmployeeClass.setGroupId(group!=null?group.getId():null);
				attnEmployeeClass.setEmployId(employId);
				attnEmployeeClass.setEmployName(emp.getCnName());
				attnEmployeeClass.setClassDate(moveDate);
				attnEmployeeClass.setClassSettingId(setId);
				attnEmployeeClass.setClassSettingPerson(user.getEmployee().getCnName());
				attnEmployeeClass.setShouldTime(shouldTime);
				attnEmployeeClass.setCreateTime(new Date());
				attnEmployeeClass.setCreateUser(user.getEmployee().getCnName());
				employeeClassMapper.save(attnEmployeeClass);
				//??????????????????????????????????????????????????????
				if(vacationList!=null&&vacationList.size()>0){
					ClassSetting setting = classSettingMapper.getById(setId);
					EmployeeDuty duty = new EmployeeDuty();
					duty.setCompanyId(user.getCompanyId());
					duty.setDepartId(depart.getId());
					duty.setEmployId(employId);
					duty.setEmployName(emp.getCnName());
					duty.setYear(DateUtils.format(moveDate, "yyyy"));
					duty.setVacationName(vacationList.get(0).getSubject());
					duty.setDutyDate(moveDate);
					duty.setDutyItem("?????????????????????");
					duty.setStartTime(DateUtils.parse(DateUtils.format(moveDate, DateUtils.FORMAT_SHORT)+" "+DateUtils.format(setting.getStartTime(), DateUtils.FORMAT_HH_MM_SS),
							DateUtils.FORMAT_LONG));
					duty.setEndTime(DateUtils.parse(DateUtils.format(moveDate, DateUtils.FORMAT_SHORT)+" "+DateUtils.format(setting.getEndTime(), DateUtils.FORMAT_HH_MM_SS),
							DateUtils.FORMAT_LONG));
					duty.setWorkHours(setting.getMustAttnTime());
					duty.setClassSettingPerson(user.getEmployee().getCnName());
					duty.setCreateTime(new Date());
					duty.setCreateUser(user.getEmployee().getCnName());
					duty.setDelFlag(0);
					duty.setSource(1);
					employeeDutyMapper.save(duty);
				}
			}
		}
		
		//??????????????????
		Date nowDate = DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_SHORT), DateUtils.FORMAT_SHORT);
		if(moveDate.getTime()<nowDate.getTime()){
			TransNormal transNormal = new TransNormal();
			transNormal.setEmployeeIds(String.valueOf(employId));
			transNormal.setStartTime(moveDate);
			transNormal.setEndTime(moveDate);
			transNormalService.recalculateAttnByCondition(transNormal);
		}
		
		result.put("message","???????????????");
		result.put("success",true);
		
		Long time2 = System.currentTimeMillis();
		logger.info("????????????hrMoveAtAdmin:use time="+(time2-time1));
		logger.info("????????????hrMoveAtAdmin:end??????");
		
		return result;
	}

	@Override
	public Map<String, String> getSettingInfos(String date, Long setId,
			Long classId,Long empId) {
		Map<String,String> result = new HashMap<String,String>();
		result.put("date",DateUtils.format(DateUtils.parse(date, DateUtils.FORMAT_SHORT), DateUtils.FORMAT_SHORT_CN));//??????

		//result.put("date",date.substring(5,10));//??????
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String[] weekDays = { "?????????", "?????????", "?????????", "?????????", "?????????", "?????????", "?????????" };
        Calendar cal = Calendar.getInstance(); // ??????????????????
        Date datet = null;
        try {
            datet = f.parse(date);
            cal.setTime(datet);
        } catch (ParseException e) {
            logger.error("getSettingInfos??????????????????",e);
        }
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0){
        	w = 0;
        }  
		result.put("weekDay",weekDays[w]);//??????
		
		if(empId==null && setId==null){
			return result;
		}else{
			if(empId==null){
				//?????????
				if(setId!=null&&setId.intValue()!=0){//0??????????????????id???????????????????????????
					ClassSetting old = classSettingMapper.getById(setId);
					result.put("setName", old!=null?old.getName():"");
					result.put("startTime", old!=null?DateUtils.format(old.getStartTime(), DateUtils.FORMAT_HH_MM):"");
					result.put("endTime", old!=null?DateUtils.format(old.getEndTime(), DateUtils.FORMAT_HH_MM):"");
				}else if(setId!=null&&setId.intValue()==0){
					result.put("setName", "??????");
					result.put("startTime", "");
					result.put("endTime", "");
				}
			}else{
				ApplicationEmployeeClass changeClassParam =new ApplicationEmployeeClass();
				changeClassParam.setClassDate(DateUtils.parse(date, DateUtils.FORMAT_SHORT));
				changeClassParam.setEmployeeId(empId);
				if(classId!=null){
					changeClassParam.setAttnApplicationEmployClassId(classId);
				}
				List<Map<String,Object>> changeEmpClassById = applicationEmployeeClassMapper.getChangeEmpClassById(changeClassParam);			
				for (Map<String, Object> map : changeEmpClassById) {
					if(map.get("is_move")!=null  ){
						if(map.get("is_move").equals(0)){
							result.put("setName", String.valueOf(map.get("name")));
							result.put("startTime", String.valueOf( map.get("start_time")));
							result.put("endTime",String.valueOf(map.get("end_time")));
						}else{
							if(setId==null && classId==null){
								result.put("setName", String.valueOf(map.get("name")));
								result.put("startTime", String.valueOf( map.get("start_time")));
								result.put("endTime",String.valueOf(map.get("end_time")));
							}
							result.put("newSetName", String.valueOf(map.get("name")));
							result.put("newStartTime",String.valueOf(map.get("start_time")));
							result.put("newEndTime",String.valueOf(map.get("end_time")));
						}
					}
				}
			}
		}
		return result;
		
		
	}

	@Override
	public HSSFWorkbook exportDetailById(Long id) throws Exception{
	    ApplicationEmployeeClass applyData = applicationEmployeeClassMapper.getById(id);
		if(applyData==null){
			logger.error("??????id="+id+"?????????????????????");
			return null;
		}
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		
		List<Map<String,Object>> datas = applicationEmployeeClassMapper.getDetailById(id, Long.valueOf(String.valueOf(applyData.getIsMove())));
		
		//???????????????????????????????????????
		if(applyData.getIsMove().intValue()==ApplicationEmployeeClass.IS_MOVE_1){
			//??????????????????????????????
			List<Long> emoloyIds = applicationEmployeeClassMapper.getEmployeeIdListById(applyData.getId());
			
			ApplicationEmployeeClass param = new ApplicationEmployeeClass();
			param.setDepartId(applyData.getDepartId());
			param.setGroupId(applyData.getGroupId());
			param.setStartTime(applyData.getClassMonth());
			param.setEndTime(DateUtils.getLastDay(applyData.getClassMonth()));
			param.setEmployeeIds(emoloyIds);
			List<Map<String,Object>> oldDatas = applicationEmployeeClassMapper.getEmpClassReprotById(param);
			
			for(int i=0;i<=result.size()-1;i++){
				for(Map<String,Object> data:datas){
					if(result.get(i).get("empId").equals(data.get("empId"))&&result.get(i).get("classDate").equals(data.get("classDate"))){
						result.set(i, data);
					}
				}
			}
			
			for(Map<String,Object> oldData:oldDatas){
				if(oldData.get("classSettingId")!=null){
					result.add(oldData);
				}
			}
			
		}else{
			for(Map<String,Object> data:datas){
				if(data.get("classSettingId")!=null){
					result.add(data);
				}
			}
		}
		
		String departName = applyData.getDepartName();
		String fileName = departName+"?????????????????????"+"-"+DateUtils.format(applyData.getClassMonth(), "MM")+"???";
		String excelName = fileName;
		HSSFWorkbook workbook = new HSSFWorkbook();
		
		HSSFSheet sheet = workbook.createSheet(excelName);
		if (result != null && result.size() > 0) {
			String yearMonth = (String)result.get(0).get("yearMonth");
			Map<String,String[]> titleMap = getEmpClassReportTitle(yearMonth);//??????????????????
			String[] title2 = titleMap.get("t2");//???????????????
			String[] title3 = titleMap.get("t3");//???????????????
			// ??????????????????
			HSSFCellStyle colstyle = workbook.createCellStyle();
			colstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// ????????????
			colstyle.setBorderBottom(XSSFCellStyle.BORDER_THIN); //?????????
			colstyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);//?????????
			colstyle.setBorderTop(XSSFCellStyle.BORDER_THIN);//?????????
			colstyle.setBorderRight(XSSFCellStyle.BORDER_THIN);//?????????
			
			//?????????????????????
			HSSFRow row = sheet.createRow((short) 0);
			//???????????????????????????
			ExcelUtil.createRow(row, 0, null, HSSFCell.CELL_TYPE_STRING,fileName);
			sheet.addMergedRegion(new CellRangeAddress(0,0,0,2));
			
			//?????????????????????
			row = sheet.createRow((short) 1);
			//???????????????????????????
			for(int colIndex = 0; colIndex < title2.length; colIndex++){
				if(colIndex >=0 && colIndex <= 2){
					sheet.setColumnWidth(colIndex, 5000);//??????????????????
				}
				
				Object obj = title2[colIndex];
				if(obj instanceof Long || obj instanceof Double || obj instanceof Integer || obj instanceof Float || obj instanceof BigDecimal){
					ExcelUtil.createRow(row, colIndex, colstyle, HSSFCell.CELL_TYPE_NUMERIC, obj);
				}else{
					ExcelUtil.createRow(row, colIndex, colstyle, HSSFCell.CELL_TYPE_STRING, obj);
				}
        	}
			sheet.addMergedRegion(new CellRangeAddress(1,2,0,0));
    		sheet.addMergedRegion(new CellRangeAddress(1,2,1,1));
    		sheet.addMergedRegion(new CellRangeAddress(1,2,2,2));
    		sheet.addMergedRegion(new CellRangeAddress(1,2,34,34));
			
			//?????????????????????(???????????????????????????????????????????????????????????????)
			row = sheet.createRow((short) 2);
			//???????????????????????????
			for(int colIndex = 3; colIndex < title3.length; colIndex++){
				if(colIndex >=3 && colIndex <= title3.length - 2){
					sheet.setColumnWidth(colIndex, 1200);//??????????????????
				}else{
					sheet.setColumnWidth(colIndex, 5000);//??????????????????
				}
				
				Object obj = title3[colIndex];
				if(obj instanceof Long || obj instanceof Double || obj instanceof Integer || obj instanceof Float || obj instanceof BigDecimal){
					ExcelUtil.createRow(row, colIndex, colstyle, HSSFCell.CELL_TYPE_NUMERIC, obj);
				}else{
					ExcelUtil.createRow(row, colIndex, colstyle, HSSFCell.CELL_TYPE_STRING, obj);
				}
        	}
			
			Long empId = null;
			Double mustAttnTime = 0.0;
			int index = 3;
			for(Map<String,Object> data : result){
				Long classDay = Long.valueOf(String.valueOf(data.get("classDay")));
				
				if(null != empId && empId.equals(data.get("empId"))){//????????????????????????
					mustAttnTime += data.get("mustAttnTime")!=null?(Double)data.get("mustAttnTime"):0;
					ExcelUtil.createRow(row, 34, null, HSSFCell.CELL_TYPE_NUMERIC,mustAttnTime);
					ExcelUtil.createRow(row, Integer.valueOf(classDay+"") + 2, null, HSSFCell.CELL_TYPE_STRING,data.get("name"));//????????????
				}else{
					row = sheet.createRow((short) index);
					ExcelUtil.createRow(row, Integer.valueOf(classDay+"") + 2, null, HSSFCell.CELL_TYPE_STRING,data.get("name"));//????????????
					
					Position position = positionService.getByEmpId((Long)data.get("empId"));
					ExcelUtil.createRow(row, 0, null, HSSFCell.CELL_TYPE_STRING,data.get("empName"));
					ExcelUtil.createRow(row, 1, null, HSSFCell.CELL_TYPE_STRING,null == position ? "" : position.getPositionName());
					ExcelUtil.createRow(row, 2, null, HSSFCell.CELL_TYPE_NUMERIC,data.get("shouldTime"));//???????????????
					ExcelUtil.createRow(row, 34, null, HSSFCell.CELL_TYPE_NUMERIC,(Double)data.get("mustAttnTime"));
					
					mustAttnTime = data.get("mustAttnTime")!=null?(Double)data.get("mustAttnTime"):0;
					empId = (Long)data.get("empId");
					index++;
				}
			}
		} else {
			ExcelUtil.createRow(sheet.createRow(1), 0, null,HSSFCell.CELL_TYPE_STRING, "?????????");
		}
		
		ClassSetting classSetting = new ClassSetting();
		List<ClassSetting> classSettingList = classSettingMapper.getListByCondition(classSetting);
		
		HSSFSheet sheet1 = workbook.createSheet("??????");
		//?????????????????????
		HSSFRow row = sheet1.createRow((short) 0);
		//???????????????????????????
		ExcelUtil.createRow(row, 0, null, HSSFCell.CELL_TYPE_STRING,"????????????");
		ExcelUtil.createRow(row, 1, null, HSSFCell.CELL_TYPE_STRING,"????????????");
		ExcelUtil.createRow(row, 2, null, HSSFCell.CELL_TYPE_STRING,"????????????");
		ExcelUtil.createRow(row, 3, null, HSSFCell.CELL_TYPE_STRING,"???????????????????????????");
		
		if(classSettingList!=null&&classSettingList.size()>0){
			for(int i=0;i<classSettingList.size();i++){
				row = sheet1.createRow((short) (i+1));
				ExcelUtil.createRow(row, 0, null, HSSFCell.CELL_TYPE_STRING,classSettingList.get(i).getName());
				ExcelUtil.createRow(row, 1, null, HSSFCell.CELL_TYPE_STRING,DateUtils.format(classSettingList.get(i).getStartTime(), "HH:mm"));
				ExcelUtil.createRow(row, 2, null, HSSFCell.CELL_TYPE_STRING,DateUtils.format(classSettingList.get(i).getEndTime(), "HH:mm"));
				ExcelUtil.createRow(row, 3, null, HSSFCell.CELL_TYPE_NUMERIC,(Double)classSettingList.get(i).getMustAttnTime());
			}
		}

		return workbook;
	}

	@Override
	public PageModel<ApplicationEmployeeClass> queryChangeClass(
			ApplicationEmployeeClass empClassTemp) {
		// TODO Auto-generated method stub
		
		PageModel<ApplicationEmployeeClass> pm=new PageModel<ApplicationEmployeeClass>();
		int page = empClassTemp.getPage() == null ? 0 : empClassTemp.getPage();
		int rows = empClassTemp.getRows() == null ? 0 : empClassTemp.getRows();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		empClassTemp.setLimit(pm.getLimit());
		empClassTemp.setOffset(pm.getOffset());	
		
		empClassTemp.setDepartId(empClassTemp.getDepartId());
		empClassTemp.setClassMonth(DateUtils.parse(empClassTemp.getMonth()+"-01", DateUtils.FORMAT_SHORT));
		if(empClassTemp.getGroupId()!=null){
			empClassTemp.setGroupId(empClassTemp.getGroupId());
		}
		empClassTemp.setIsMove(empClassTemp.IS_MOVE_1);
		List<ApplicationEmployeeClass> byCondition = getByCondition(empClassTemp);
		if(byCondition!=null &&byCondition.size()>0){
			for (int i = 0; i < byCondition.size(); i++) {
				if(byCondition.get(i).getGroupId()!=null){
					ScheduleGroup scheduleGroup = scheduleGroupMapper.getGroupById(byCondition.get(i).getGroupId());
					byCondition.get(i).setGroupName(scheduleGroup.getName());
				}
			}		
		}
		pm.setRows(byCondition);
		pm.setTotal(applicationEmployeeClassMapper.getCountByCondition(empClassTemp));
		
		return pm;
	}
	
	

	@Override
	public Map<String, Object> getUnCommitData(Long departId, Long groupId) {
		Map<String,Object> result = new HashMap<String,Object>();
		if(departId==null){
			result.put("message","??????????????????");
			result.put("success",false);
			return result;
		}
		if(groupId==null){
			result.put("message","??????????????????");
			result.put("success",false);
			return result;
		}
		//??????????????????????????????????????????
		//???????????????????????????????????????
		ApplicationEmployeeClass condition = new ApplicationEmployeeClass();
		condition.setClassMonth(DateUtils.getFirstDay(DateUtils.addMonth(new Date(),1)));
		condition.setDepartId(departId);
		condition.setGroupId(groupId);
		condition.setIsMove(condition.IS_MOVE_0);
		List<Integer> approvalStatusList = new ArrayList<Integer>();
		approvalStatusList.add(ConfigConstants.DOING_STATUS);//?????????
		approvalStatusList.add(ConfigConstants.PASS_STATUS);//??????
		approvalStatusList.add(ConfigConstants.OVERDUE_STATUS);//??????
		approvalStatusList.add(ConfigConstants.OVERDUEPASS_STATUS);//????????????
		condition.setApprovalStatusList(approvalStatusList);
		List<ApplicationEmployeeClass> list = applicationEmployeeClassMapper.getByCondition(condition);
		if(list!=null&&list.size()>0){
			result.put("message","???????????????????????????");
			result.put("success",false);
			return result;
		}else{
			//??????
			Depart depart = departService.getById(departId);
			String title = depart.getName()+"?????????????????????-"+DateUtils.format(DateUtils.addMonth(new Date(),1),"yyyy???MM???");
			result.put("title", title);
			//??????
			ScheduleGroup group = scheduleGroupMapper.getGroupById(groupId);
			result.put("groupName", group.getName());
			result.put("approvalStatus", "?????????");
			Date fristDay = DateUtils.getFirstDay(DateUtils.addMonth(new Date(),1));//???????????????
			Date lastDay = DateUtils.getLastDay(DateUtils.addMonth(new Date(),1));//??????????????????
			//?????????????????????????????????????????????
			AnnualVacation vacation = new AnnualVacation();
			vacation.setStartDate(fristDay);
			vacation.setEndDate(lastDay);
			List<AnnualVacation> vacationList = annualVacationService.getListByCondition(vacation);
			Map<Date,Integer> vacationMap = new HashMap<Date,Integer>();
			for(AnnualVacation va:vacationList){
				vacationMap.put(va.getAnnualDate(), va.getType());
			}
			
			//????????????????????????
			List<String> weekDays = new ArrayList<String>();
			//???????????????
			List<String> days = new ArrayList<String>();
			//???????????????
			List<String> dates = new ArrayList<String>();
			weekDays.add("????????????");
			weekDays.add("??????");
			weekDays.add("??????");
			weekDays.add("???????????????");
			weekDays.add("????????????");
			weekDays.add("????????????");
			days.add("");
			days.add("");
			days.add("");
			days.add("");
			days.add("");
			days.add("");
			dates.add("");
			dates.add("");
			dates.add("");
			dates.add("");
			dates.add("");
			dates.add("");
			int i = 1;
			while(true){
				String week = DateUtils.getWeek(fristDay);
				weekDays.add(week);
				days.add(String.valueOf(i));
				dates.add(DateUtils.format(fristDay, DateUtils.FORMAT_SHORT));
				fristDay = DateUtils.addDay(fristDay, 1);
				i = i + 1;
				if(!DateUtils.format(fristDay, "yyyy-MM").equals(DateUtils.format(lastDay, "yyyy-MM"))){
					break;
				}
			}
			result.put("weekDays", weekDays);
			result.put("days", days);
			result.put("dates", dates);
			//?????????????????????????????????
			List<Map<String,Object>> datas = employeeMapper.getEmployeeMapByGroupId(groupId,condition.getClassMonth());
			//????????????????????????
			ApplicationEmployeeClass unCommit = applicationEmployeeClassMapper.getUnCommitData(condition);
			//??????????????????????????????
			for(Map<String,Object> data : datas){
				String employ_id = String.valueOf(data.get("employ_id"));
				Position position = positionService.getByEmpId(Long.valueOf(employ_id));
				data.put("positionName", position!=null?position.getPositionName():"");
				Date firstEntryTime = data.get("firstEntryTime")!=null?(Date)data.get("firstEntryTime"):null;
				Date quitTime = data.get("quitTime")!=null?(Date)data.get("quitTime"):null;
				Date actualStartTime = (DateUtils.format(DateUtils.addMonth(new Date(),1), DateUtils.FORMAT_YYYY_MM).equals(DateUtils.format(firstEntryTime, DateUtils.FORMAT_YYYY_MM)))
						?firstEntryTime:DateUtils.getFirstDay(DateUtils.addMonth(new Date(),1));
				Date actualEndTime = (quitTime!=null&&DateUtils.format(DateUtils.addMonth(new Date(),1), DateUtils.FORMAT_YYYY_MM).equals(DateUtils.format(quitTime, DateUtils.FORMAT_YYYY_MM)))
						?quitTime:lastDay;
				data.put("should_time", getShouldTime(actualStartTime,actualEndTime,vacationMap));
			}
			
			if(unCommit!=null){
				List<Map<String,Object>> datas1 = applicationEmployeeClassMapper.getDetailById(unCommit.getId(),0L);
				List<Map<String,Object>> datas2 = applicationEmployeeClassMapper.getClassHoursMap(unCommit.getId());//??????????????????????????????
				for(Map<String,Object> data : datas){
					//?????????????????????
					String employ_id = String.valueOf(data.get("employ_id"));
					for(Map<String,Object> data1 : datas1){
						String empId = String.valueOf(data1.get("empId"));
						if(employ_id.equals(empId)&&data1.get("classSettingId")!=null){
							data.put(String.valueOf(data1.get("classDay")), String.valueOf(data1.get("name"))+","+String.valueOf(data1.get("classDate"))+","+String.valueOf(data1.get("classSettingId"))+","+String.valueOf(data1.get("mustAttnTime")));
						}
					}
					//??????????????????????????????????????????
					data.put("dayCount",0);		
					data.put("must_attn_time", 0);		
					for(Map<String,Object> data2 : datas2){
						String empId = String.valueOf(data2.get("employ_id"));
						if(employ_id.equals(empId)){
							data.put("dayCount",data2.get("dayCount"));		
							data.put("must_attn_time", data2.get("must_attn_time"));		
						}
					}
				}
			}else{
				//??????????????????????????????
				for(Map<String,Object> data : datas){
					data.put("dayCount",0);		
					data.put("must_attn_time", 0);				
				}
			}
			result.put("classDetail", datas);
			result.put("success",true);
		}
		return result;
	}
	
    public Double getShouldTime(Date startTime,Date endTime,Map<Date,Integer> vacationMap){
		
		boolean isEndTime1 = true;
	    double should_time = 0;
	    do{
	    	if(annualVacationService.judgeWorkOrNot(startTime,vacationMap)){//????????????????????????????????????
	    		should_time += 8;
			}
	    	startTime = DateUtils.addDay(startTime, 1);
			if(DateUtils.getIntervalDays(startTime, endTime) < 0) {
				isEndTime1 = false;
			}
		} while(isEndTime1);
	    
		return should_time;
	
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> saveSchedule(HttpServletRequest request,String info, Long departId,
			Long groupId) {
		
		logger.info("????????????saveSchedule:start??????");
		Long time1 = System.currentTimeMillis();
			
		User user= userService.getCurrentUser();
		logger.info("????????????saveSchedule??????:info="+info+";departId="+departId+";groupId="+groupId
			+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));

		Map<String, Object> result = new HashMap<String, Object>();
		if(departId==null){
			result.put("message","?????????????????????");
			result.put("success",false);
			return result;
		}
		if(groupId==null){
			result.put("message","?????????????????????");
			result.put("success",false);
			return result;
		}
		
		//?????????????????????????????????????????????????????????????????????
		ApplicationEmployeeClass condition = new ApplicationEmployeeClass();
		condition.setClassMonth(DateUtils.getFirstDay(DateUtils.addMonth(new Date(),1)));
		condition.setDepartId(departId);
		condition.setGroupId(groupId);
		condition.setIsMove(condition.IS_MOVE_0);
		ApplicationEmployeeClass unCommit = applicationEmployeeClassMapper.getUnCommitData(condition);
		JSONObject array = JSONObject.fromObject(info);
		Map<String, String> map = (Map)array;//??????????????????
		Date fristDay = DateUtils.getFirstDay(DateUtils.addMonth(new Date(),1));//???????????????
		Date lastDay = DateUtils.getLastDay(DateUtils.addMonth(new Date(),1));//??????????????????
		//?????????????????????????????????????????????
		AnnualVacation vacation = new AnnualVacation();
		vacation.setStartDate(fristDay);
		vacation.setEndDate(lastDay);
		List<AnnualVacation> vacationList = annualVacationService.getListByCondition(vacation);
		Map<Date,Integer> vacationMap = new HashMap<Date,Integer>();
		for(AnnualVacation va:vacationList){
			vacationMap.put(va.getAnnualDate(), va.getType());
		}
		//???????????????
		Map<String,Double> shouldTimeMap = new HashMap<String,Double>();
		//??????????????????
		Map<String,String> nameMap = new HashMap<String,String>();
		if(unCommit!=null){
			//??????????????????????????????
            for (Map.Entry<String, String> detail: map.entrySet()) {
            	//??????????????????????????????
            	String employId = detail.getKey().split("_")[0];
            	String classDate = detail.getKey().split("_")[1];
            	String classSettingId = (detail.getValue()!=null&&!"".equals(detail.getValue())&&!"undefined".equals(detail.getValue()))?detail.getValue():null;
            	ApplicationEmployeeClassDetail isExistParam = new ApplicationEmployeeClassDetail();
            	isExistParam.setEmployId(Long.valueOf(employId));
            	isExistParam.setClassDate(DateUtils.parse(classDate, DateUtils.FORMAT_SHORT));
            	isExistParam.setAttnApplicationEmployClassId(unCommit.getId());
            	List<ApplicationEmployeeClassDetail> isExist = applicationEmployeeClassDetailMapper.selectByCondition(isExistParam);
            	if(isExist!=null&&isExist.size()>0){
            		isExist.get(0).setClassSettingId((classSettingId!=null&&!"undefined".equals(classSettingId)&&!"".equals(classSettingId))?Long.valueOf(classSettingId):null);
            		isExist.get(0).setUpdateTime(new Date());
            		isExist.get(0).setUpdateUser(user.getEmployee().getCnName());
            		applicationEmployeeClassDetailMapper.updateSetIdById(isExist.get(0));
            	}else{
            		Employee emp = null;
            		String employName = "";
    				double shouldTime = 0d;
    				if(nameMap!=null&&nameMap.containsKey(employId)){
    					employName = nameMap.get(employId);
    				}else{
    					if(employId!=null&&!"".equals(employId)){
    						emp = employeeMapper.getById(Long.valueOf(employId));
    						employName = emp!=null?emp.getCnName():"";
    						nameMap.put(employId, employName);
    					}else{
    						logger.error("??????id="+employId+"????????????????????????");
    					}
    				}
    				if(shouldTimeMap!=null&&shouldTimeMap.containsKey(employId)){
    					shouldTime = shouldTimeMap.get(employId);
    				}else{
    					if(emp==null){
    						emp = employeeMapper.getById(Long.valueOf(employId));
    					}
    					Date firstEntryTime = emp.getFirstEntryTime();
    					Date quitTime = emp.getQuitTime();
    					Date actualStartTime = (DateUtils.format(DateUtils.addMonth(new Date(),1), DateUtils.FORMAT_YYYY_MM).equals(DateUtils.format(firstEntryTime, DateUtils.FORMAT_YYYY_MM)))
    							?firstEntryTime:fristDay;
    					Date actualEndTime = (quitTime!=null&&DateUtils.format(DateUtils.addMonth(new Date(),1), DateUtils.FORMAT_YYYY_MM).equals(DateUtils.format(quitTime, DateUtils.FORMAT_YYYY_MM)))
    							?quitTime:lastDay;
    					shouldTime = getShouldTime(actualStartTime,actualEndTime,vacationMap);
    					shouldTimeMap.put(employId, shouldTime);
    				}
            		ApplicationEmployeeClassDetail applicationEmployeeClassDetail = new ApplicationEmployeeClassDetail();
    				applicationEmployeeClassDetail.setAttnApplicationEmployClassId(unCommit.getId());
    				applicationEmployeeClassDetail.setClassDate(DateUtils.parse(detail.getKey().split("_")[1], DateUtils.FORMAT_SHORT));
    				applicationEmployeeClassDetail.setCompanyId(user.getCompanyId());
    				applicationEmployeeClassDetail.setEmployId((employId!=null&&!"".equals(employId))?Long.valueOf(employId):null);
    				applicationEmployeeClassDetail.setEmployName(employName);
    				applicationEmployeeClassDetail.setClassSettingPerson(user.getEmployee().getCnName());
    				applicationEmployeeClassDetail.setCreateTime(new Date());
    				applicationEmployeeClassDetail.setCreateUser(user.getEmployee().getCnName());
    				applicationEmployeeClassDetail.setDelFlag(0);
    				applicationEmployeeClassDetail.setIsMove(0);
    				applicationEmployeeClassDetail.setClassSettingId((classSettingId!=null&&!"undefined".equals(classSettingId)&&!"".equals(classSettingId))?Long.valueOf(classSettingId):null);
    				applicationEmployeeClassDetail.setShouldTime(shouldTime);
    				applicationEmployeeClassDetailMapper.save(applicationEmployeeClassDetail);
            	}
			}
             //?????????????????????
			List<ApplicationEmployeeClassDetail> classEmployeeNum = applicationEmployeeClassDetailMapper.getclassEmployeeNum(unCommit.getId());
			//?????????size?????????????????????
			ApplicationEmployeeClass employeeClass = new ApplicationEmployeeClass();
			employeeClass.setUpdateTime(new Date());
			employeeClass.setUpdateUser(user.getEmployee().getCnName());
			employeeClass.setClassEmployeeNum((classEmployeeNum!=null&&classEmployeeNum.size()>0)?classEmployeeNum.size():0);
			employeeClass.setId(unCommit.getId());
			applicationEmployeeClassMapper.updateById(employeeClass);
		}else{
			//?????????????????????????????????
			List<Map<String,Object>> datas = employeeMapper.getEmployeeMapByGroupId(groupId,condition.getClassMonth());
			Map<String,String> moveNumMap = new HashMap<String,String>();
			//?????????????????????????????????
			Depart depart = departService.getById(departId);
			ApplicationEmployeeClass applicationEmployeeClass = new ApplicationEmployeeClass();
			applicationEmployeeClass.setDepartId(departId);
			applicationEmployeeClass.setGroupId(groupId);
			applicationEmployeeClass.setDepartName(depart!=null?depart.getName():"");
			applicationEmployeeClass.setClassMonth(DateUtils.getFirstDay(DateUtils.addMonth(new Date(),1)));
			applicationEmployeeClass.setEmployeeNum(datas.size());
			applicationEmployeeClass.setClassSettingPerson(user.getEmployee().getCnName());
			applicationEmployeeClass.setVersion(0L);
			applicationEmployeeClass.setDelFlag(0);
			applicationEmployeeClass.setCreateTime(new Date());
			applicationEmployeeClass.setCreateUser(user.getEmployee().getCnName());
			applicationEmployeeClass.setIsMove(0);
			applicationEmployeeClass.setEmployeeId(user.getEmployee().getId());
			
			List<ApplicationEmployeeClassDetail> applicationEmployeeClassList = new ArrayList<ApplicationEmployeeClassDetail>();
			for (Map.Entry<String,String> detail: map.entrySet()) {
				String employId = detail.getKey().split("_")[0];
				if(!(moveNumMap!=null&&moveNumMap.containsKey(employId))){
					moveNumMap.put(employId, employId);
				}
				String employName = "";
				double shouldTime = 0d;
				String classSettingId = detail.getValue();
				Employee emp = null;
				if(nameMap!=null&&nameMap.containsKey(employId)){
					employName = nameMap.get(employId);
				}else{
					if(employId!=null&&!"".equals(employId)){
						emp = employeeMapper.getById(Long.valueOf(employId));
						employName = emp!=null?emp.getCnName():"";
						nameMap.put(employId, employName);
					}else{
						logger.error("??????id="+employId+"????????????????????????");
					}
				}
				if(shouldTimeMap!=null&&shouldTimeMap.containsKey(employId)){
					shouldTime = shouldTimeMap.get(employId);
				}else{
					if(emp==null){
						emp = employeeMapper.getById(Long.valueOf(employId));
					}
					Date firstEntryTime = emp.getFirstEntryTime();
					Date quitTime = emp.getQuitTime();
					Date actualStartTime = (DateUtils.format(DateUtils.addMonth(new Date(),1), DateUtils.FORMAT_YYYY_MM).equals(DateUtils.format(firstEntryTime, DateUtils.FORMAT_YYYY_MM)))
							?firstEntryTime:fristDay;
					Date actualEndTime = (quitTime!=null&&DateUtils.format(DateUtils.addMonth(new Date(),1), DateUtils.FORMAT_YYYY_MM).equals(DateUtils.format(quitTime, DateUtils.FORMAT_YYYY_MM)))
							?quitTime:lastDay;
					shouldTime = getShouldTime(actualStartTime,actualEndTime,vacationMap);
					shouldTimeMap.put(employId, shouldTime);
				}
				ApplicationEmployeeClassDetail applicationEmployeeClassDetail = new ApplicationEmployeeClassDetail();
				applicationEmployeeClassDetail.setClassDate(DateUtils.parse(detail.getKey().split("_")[1], DateUtils.FORMAT_SHORT));
				applicationEmployeeClassDetail.setCompanyId(user.getCompanyId());
				applicationEmployeeClassDetail.setEmployId((employId!=null&&!"".equals(employId))?Long.valueOf(employId):null);
				applicationEmployeeClassDetail.setEmployName(employName);
				applicationEmployeeClassDetail.setClassSettingPerson(user.getEmployee().getCnName());
				applicationEmployeeClassDetail.setCreateTime(new Date());
				applicationEmployeeClassDetail.setCreateUser(user.getEmployee().getCnName());
				applicationEmployeeClassDetail.setDelFlag(0);
				applicationEmployeeClassDetail.setIsMove(0);
				applicationEmployeeClassDetail.setClassSettingId((classSettingId!=null&&!"undefined".equals(classSettingId)&&!"".equals(classSettingId))?Long.valueOf(classSettingId):null);
				applicationEmployeeClassDetail.setShouldTime(shouldTime);
				applicationEmployeeClassList.add(applicationEmployeeClassDetail);
			}
			applicationEmployeeClass.setClassEmployeeNum(moveNumMap.size());
			applicationEmployeeClassMapper.save(applicationEmployeeClass);
			for(ApplicationEmployeeClassDetail data:applicationEmployeeClassList){
				data.setAttnApplicationEmployClassId(applicationEmployeeClass.getId());
			}
			//????????????????????????
			if(applicationEmployeeClassList!=null&&applicationEmployeeClassList.size()>0){
				applicationEmployeeClassDetailMapper.batchSave(applicationEmployeeClassList);
			}
		}
		result.put("success",true);
		result.put("message","???????????????");
		
		Long time2 = System.currentTimeMillis();
		logger.info("????????????saveSchedule:use time="+(time2-time1));
		logger.info("????????????saveSchedule:end??????");
		
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> commitSchedule(HttpServletRequest request,Long departId, Long groupId) throws OaException{
		
		logger.info("????????????commitSchedule:start??????");
		Long time1 = System.currentTimeMillis();
			
		User user= userService.getCurrentUser();
		logger.info("????????????commitSchedule??????:departId="+departId+";groupId="+groupId
			+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));
		
		Map<String, Object> result = new HashMap<String, Object>();
		if(departId==null){
			result.put("message","?????????????????????");
			result.put("success",false);
			return result;
		}
		if(groupId==null){
			result.put("message","?????????????????????");
			result.put("success",false);
			return result;
		}
		//?????????????????????????????????????????????????????????????????????
		ApplicationEmployeeClass condition = new ApplicationEmployeeClass();
		condition.setClassMonth(DateUtils.getFirstDay(DateUtils.addMonth(new Date(),1)));
		condition.setDepartId(departId);
		condition.setGroupId(groupId);
		condition.setIsMove(condition.IS_MOVE_0);
		ApplicationEmployeeClass unCommit = applicationEmployeeClassMapper.getUnCommitData(condition);
		if(unCommit!=null){

			//???????????????????????????????????????
			ScheduleGroup schedulerList = scheduleGroupMapper.getGroupById(groupId);
			
			if(!"107".equals(user.getDepart().getCode())){
				//???????????????????????????
				Config configCondition = new Config();
				configCondition.setCode("dutyCommitTimeLimit");
				List<Config> limit = configService.getListByCondition(configCondition);
				int num = Integer.valueOf(limit.get(0).getDisplayCode());
				
				Date lastSubDate = annualVacationService.getWorkingDayPre(num,DateUtils.addDay(unCommit.getClassMonth(), -1));
				if(DateUtils.getIntervalDays(new Date(),lastSubDate)<0){
					result.put("success", false);
					result.put("message","?????????????????????4?????????????????????");
					return result;
				}
			}
			
			ApplicationEmployeeClassDetail classHourParam = new ApplicationEmployeeClassDetail();
			classHourParam.setAttnApplicationEmployClassId(unCommit.getId());
			List<ApplicationEmployeeClassDetail> classHoursList = applicationEmployeeClassDetailMapper.getEmployeeClassHours(classHourParam);
			int i = 0;
			for(ApplicationEmployeeClassDetail classHour:classHoursList){
				if(!(classHour.getMustAttnTime()!=null&&classHour.getMustAttnTime()>=classHour.getShouldTime())){
					i = i+1;
				}
			}
			if(i>0){
				result.put("success", false);
				result.put("message", "???????????????????????????????????????????????????????????????????????????");
				return result;
			}
			
			
			Long auditor = schedulerList.getAuditor();//???????????????
			
			//????????????????????????
			Map<String, Object> variables = new HashMap<>();
			variables.put("approver",auditor+"");
			//???????????????????????????
			ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(ConfigConstants.SCHEDULING_KEY,variables);
			
			//??????????????????
			unCommit.setProcessInstanceId(processInstance.getId());
			unCommit.setApprovalStatus(ConfigConstants.DOING_STATUS);
			unCommit.setUpdateUser(user.getEmployee().getCnName());
			unCommit.setUpdateTime(new Date());
			applicationEmployeeClassMapper.updateById(unCommit);
			
			//-----------------start-----------------------????????????????????????
			ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
			flow.setAssigneeName(user.getEmployee().getCnName());
			flow.setDepartName(user.getDepart().getName());
			flow.setPositionName(user.getPosition().getPositionName());
			flow.setFinishTime(new Date());
			flow.setComment("????????????");
			flow.setProcessId(processInstance.getId());
			flow.setProcessKey(ConfigConstants.SCHEDULING_KEY);
			flow.setStatu(0);
			viewTaskInfoService.save(flow);
			
			//????????????????????????????????????????????????????????????????????????????????????????????????
			if(schedulerList.getScheduler().equals(auditor)){
				
				Map<Date,AnnualVacation> vacationMap = annualVacationService.getVacationMap(unCommit.getClassMonth());
				boolean containLeagl = false;
				if(vacationMap!=null&&vacationMap.size()>0){
					List<ApplicationEmployeeClassDetail> detailList = applicationEmployeeClassDetailMapper.selectByCondition(classHourParam);
					for(ApplicationEmployeeClassDetail de:detailList){
						if(de.getClassSettingId()!=null&&vacationMap.containsKey(de.getClassDate())){
							containLeagl = true;
							break;
						}
					}
				}
				
				Task task = activitiServiceImpl.queryTaskByProcessInstanceId(processInstance.getId());
				//????????????????????????
				Map<String, Object> variables1 = new HashMap<>();
				variables1.put("isContainLegal",containLeagl);
				Task task1 = taskService.createTaskQuery().taskId(task.getId()).list().get(0);
		    	taskService.addComment(task1.getId(), task1.getProcessInstanceId(), "????????????");
		    	//-----------------start-----------------------????????????????????????
				ViewTaskInfoTbl flow1 = new ViewTaskInfoTbl();
				flow1.setAssigneeName(user.getEmployee().getCnName());
				flow1.setDepartName(user.getDepart().getName());
				flow1.setPositionName(user.getPosition().getPositionName());
				flow1.setFinishTime(new Date());
				flow1.setComment("????????????");
				flow1.setProcessId(processInstance.getId());
				flow1.setProcessKey(ConfigConstants.SCHEDULING_KEY);
				flow1.setStatu(100);
				viewTaskInfoService.save(flow1);
		    	taskService.complete(task1.getId(), variables1);
			}
			result.put("success", true);
			result.put("message", "?????????????????????????????????!");
			
		}else{
			result.put("success", false);     
			result.put("message", "?????????????????????????????????");      
		}
		
		Long time2 = System.currentTimeMillis();
		logger.info("????????????commitSchedule:use time="+(time2-time1));
		logger.info("????????????commitSchedule:end??????");
		
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> changeClassSet(HttpServletRequest request,String month, Long departId,
			Long groupId, String info) throws OaException {
		
		logger.info("????????????changeClassSet start??????");
		Long time1 = System.currentTimeMillis();
		
		User user= userService.getCurrentUser();
		logger.info("????????????changeClassSet??????:month="+month+";departId="+departId+";groupId="+groupId
				+";info="+info+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));

		Map<String, Object> result = new HashMap<String, Object>();
		if(month==null||"".equals(month)){
			result.put("message","?????????????????????");
			result.put("success",false);
			return result;
		}
		if(departId==null){
			result.put("message","?????????????????????");
			result.put("success",false);
			return result;
		}
		if(groupId==null){
			result.put("message","?????????????????????");
			result.put("success",false);
			return result;
		}
		if("{}".equals(info)){
			result.put("message","?????????????????????");
			result.put("success",false);
			return result;
		}
		boolean containLeagl = false;//??????????????????????????????
		
		//???????????????????????????????????????
		ScheduleGroup schedulerList = scheduleGroupMapper.getGroupById(groupId);
		
		JSONObject infoObj = JSONObject.fromObject(info);
		Map<String, String> map = (Map)infoObj;//??????????????????
		
		Date fristDay = DateUtils.parse(month+"-01", DateUtils.FORMAT_SHORT);//?????????
		Date lastDay = DateUtils.getLastDay(DateUtils.parse(month+"-01", DateUtils.FORMAT_SHORT));//????????????
		//?????????????????????????????????????????????
		AnnualVacation vacation = new AnnualVacation();
		vacation.setStartDate(fristDay);
		vacation.setEndDate(lastDay);
		List<AnnualVacation> vacationList = annualVacationService.getListByCondition(vacation);
		Map<Date,Integer> vacationMap = new HashMap<Date,Integer>();
		Map<Date,Date> legalMap = new HashMap<Date,Date>();//?????????????????????
		for(AnnualVacation va:vacationList){
			vacationMap.put(va.getAnnualDate(), va.getType());
			if(va.getType().intValue()==AnnualVacation.YYPE_LEGAL.intValue()){
				legalMap.put(va.getAnnualDate(), va.getAnnualDate());
			}
		}
		//???????????????
		Map<String,Double> shouldTimeMap = new HashMap<String,Double>();
		//??????????????????
		Map<String,String> nameMap = new HashMap<String,String>();
		List<ApplicationEmployeeClassDetail> applicationEmployeeClassList = new ArrayList<ApplicationEmployeeClassDetail>();//????????????
		Map<String,String> moveNumMap = new HashMap<String,String>();
		for (Map.Entry<String,String> detail: map.entrySet()) {
			String employId = detail.getKey().split("_")[0];
			if(!(moveNumMap!=null&&moveNumMap.containsKey(employId))){
				moveNumMap.put(employId, employId);
			}
			String employName = "";
			double shouldTime = 0d;
			String classSettingId = (detail.getValue()!=null&&!"".equals(detail.getValue())&&!"undefined".equals(detail.getValue()))?detail.getValue():null;
			
			//?????????????????????????????????????????????
			if(legalMap!=null&&legalMap.containsKey(DateUtils.parse(detail.getKey().split("_")[1], DateUtils.FORMAT_SHORT))){
				
				//???????????????????????????
				Config configCondition = new Config();
				configCondition.setCode("dutyCommitTimeLimit");
				List<Config> limit = configService.getListByCondition(configCondition);
				int num = Integer.valueOf(limit.get(0).getDisplayCode());
				
				Date lastSubDate = annualVacationService.getWorkingDayPre(num,DateUtils.parse(detail.getKey().split("_")[1], DateUtils.FORMAT_SHORT));
				if(DateUtils.getIntervalDays(new Date(),lastSubDate)<0){
					result.put("success",false);
					result.put("message","??????4?????????????????????");
					return result;
				}
			}
			
			Employee emp = null;
			if(nameMap!=null&&nameMap.containsKey(employId)){
				employName = nameMap.get(employId);
			}else{
				if(employId!=null&&!"".equals(employId)){
					emp = employeeMapper.getById(Long.valueOf(employId));
					employName = emp!=null?emp.getCnName():"";
					nameMap.put(employId, employName);
				}else{
					logger.error("??????id="+employId+"????????????????????????");
				}
			}
			if(shouldTimeMap!=null&&shouldTimeMap.containsKey(employId)){
				shouldTime = shouldTimeMap.get(employId);
			}else{
				if(emp==null){
					emp = employeeMapper.getById(Long.valueOf(employId));
				}
				Date firstEntryTime = emp.getFirstEntryTime();
				Date quitTime = emp.getQuitTime();
				Date actualStartTime = (DateUtils.format(DateUtils.addMonth(new Date(),1), DateUtils.FORMAT_YYYY_MM).equals(DateUtils.format(firstEntryTime, DateUtils.FORMAT_YYYY_MM)))
						?firstEntryTime:fristDay;
				Date actualEndTime = (quitTime!=null&&DateUtils.format(DateUtils.addMonth(new Date(),1), DateUtils.FORMAT_YYYY_MM).equals(DateUtils.format(quitTime, DateUtils.FORMAT_YYYY_MM)))
						?quitTime:lastDay;
				shouldTime = getShouldTime(actualStartTime,actualEndTime,vacationMap);
				shouldTimeMap.put(employId, shouldTime);
			}
			ApplicationEmployeeClassDetail applicationEmployeeClassDetail = new ApplicationEmployeeClassDetail();
			//applicationEmployeeClassDetail.setAttnApplicationEmployClassId(applicationEmployeeClass.getId());
			applicationEmployeeClassDetail.setClassDate(DateUtils.parse(detail.getKey().split("_")[1], DateUtils.FORMAT_SHORT));
			applicationEmployeeClassDetail.setCompanyId(user.getCompanyId());
			applicationEmployeeClassDetail.setEmployId((employId!=null&&!"".equals(employId))?Long.valueOf(employId):null);
			applicationEmployeeClassDetail.setEmployName(employName);
			applicationEmployeeClassDetail.setClassSettingPerson(user.getEmployee().getCnName());
			applicationEmployeeClassDetail.setCreateTime(new Date());
			applicationEmployeeClassDetail.setCreateUser(user.getEmployee().getCnName());
			applicationEmployeeClassDetail.setDelFlag(0);
			applicationEmployeeClassDetail.setIsMove(1);
			applicationEmployeeClassDetail.setClassSettingId((classSettingId!=null&&!"undefined".equals(classSettingId)&&!"".equals(classSettingId))?Long.valueOf(classSettingId):null);
			applicationEmployeeClassDetail.setShouldTime(shouldTime);
			applicationEmployeeClassList.add(applicationEmployeeClassDetail);
			
			Long oldSettingId = employeeClassMapper.getClassSettingIdByEmployIdAndDate(Long.valueOf(employId),DateUtils.parse(detail.getKey().split("_")[1], DateUtils.FORMAT_SHORT));
			ApplicationEmployeeClassDetail oldSchedule = new ApplicationEmployeeClassDetail();
			oldSchedule.setClassDate(DateUtils.parse(detail.getKey().split("_")[1], DateUtils.FORMAT_SHORT));
			oldSchedule.setCompanyId(user.getCompanyId());
			oldSchedule.setEmployId((employId!=null&&!"".equals(employId))?Long.valueOf(employId):null);
			oldSchedule.setEmployName(employName);
			oldSchedule.setClassSettingPerson(user.getEmployee().getCnName());
			oldSchedule.setCreateTime(new Date());
			oldSchedule.setCreateUser(user.getEmployee().getCnName());
			oldSchedule.setDelFlag(0);
			oldSchedule.setIsMove(0);
			oldSchedule.setClassSettingId(oldSettingId);
			oldSchedule.setShouldTime(shouldTime);
			applicationEmployeeClassList.add(oldSchedule);
			
			if(legalMap!=null&&legalMap.containsKey(DateUtils.parse(detail.getKey().split("_")[1], DateUtils.FORMAT_SHORT))){
				containLeagl = true;
			}
		}
		
		Long auditor = schedulerList.getAuditor();//???????????????
		//????????????
		Map<String, Object> variables = new HashMap<>();
		variables.put("approver",auditor+"");
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("scheduling",variables);
		
		//-----------------start-----------------------????????????????????????
		ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
		flow.setAssigneeName(user.getEmployee().getCnName());
		flow.setDepartName(user.getDepart().getName());
		flow.setPositionName(user.getPosition().getPositionName());
		flow.setFinishTime(new Date());
		flow.setComment("????????????");
		flow.setProcessId(processInstance.getId());
		flow.setProcessKey(ConfigConstants.SCHEDULING_KEY);
		flow.setStatu(0);
		viewTaskInfoService.save(flow);
		
		Depart depart = departService.getById(departId);
		
		ApplicationEmployeeClass applicationEmployeeClass = new ApplicationEmployeeClass();
		applicationEmployeeClass.setDepartId(departId);
		applicationEmployeeClass.setDepartName(depart!=null?depart.getName():null);
		applicationEmployeeClass.setClassMonth(fristDay);
		applicationEmployeeClass.setEmployeeNum(moveNumMap.size());
		applicationEmployeeClass.setClassEmployeeNum(moveNumMap.size());
		applicationEmployeeClass.setClassSettingPerson(user.getEmployee().getCnName());
		applicationEmployeeClass.setApprovalStatus(ConfigConstants.DOING_STATUS);
		applicationEmployeeClass.setVersion(0L);
		applicationEmployeeClass.setDelFlag(0);
		applicationEmployeeClass.setCreateUser(user.getEmployee().getCnName());
		applicationEmployeeClass.setCreateTime(new Date());
		applicationEmployeeClass.setIsMove(1);
		applicationEmployeeClass.setProcessInstanceId(processInstance.getId());
		applicationEmployeeClass.setEmployeeId(user.getEmployee().getId());
		applicationEmployeeClass.setGroupId(groupId);
		applicationEmployeeClass.setMoveType(0);//????????????
		applicationEmployeeClassMapper.save(applicationEmployeeClass);
		
		for(ApplicationEmployeeClassDetail move:applicationEmployeeClassList){
			move.setAttnApplicationEmployClassId(applicationEmployeeClass.getId());
		}
		applicationEmployeeClassDetailMapper.batchSave(applicationEmployeeClassList);
		
		if(user.getEmployee().getId().equals(auditor)){
			
			Map<String, Object> variables1 = new HashMap<>();
			variables1.put("isContainLegal",containLeagl);
			Task task = activitiServiceImpl.queryTaskByProcessInstanceId(processInstance.getId());
			Task task1 = taskService.createTaskQuery().taskId(task.getId()).list().get(0);
	    	taskService.addComment(task1.getId(), task1.getProcessInstanceId(), "????????????");
	    	
	    	//-----------------start-----------------------????????????????????????
			ViewTaskInfoTbl flow1 = new ViewTaskInfoTbl();
			flow1.setAssigneeName(user.getEmployee().getCnName());
			flow1.setDepartName(user.getDepart().getName());
			flow1.setPositionName(user.getPosition().getPositionName());
			flow1.setFinishTime(new Date());
			flow1.setComment("????????????");
			flow1.setProcessId(processInstance.getId());
			flow1.setProcessKey(ConfigConstants.SCHEDULING_KEY);
			flow1.setStatu(100);
			viewTaskInfoService.save(flow1);
			
	    	taskService.complete(task1.getId(), variables1);
	    }
		result.put("sucess", true);
		result.put("message","?????????????????????");
		
		Long time2 = System.currentTimeMillis();
		logger.info("????????????changeClassSet:use time="+(time2-time1));
		logger.info("????????????changeClassSet:end??????");
		
		return result;
    }

	@Override
	public PageModel<ApplicationEmployeeClass> getScheduleRecord(
			ApplicationEmployeeClass condition) {
		PageModel<ApplicationEmployeeClass> pm=new PageModel<ApplicationEmployeeClass>();
		int page = condition.getPage() == null ? 0 : condition.getPage();
		int rows = condition.getRows() == null ? 0 : condition.getRows();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		condition.setLimit(pm.getLimit());
		condition.setOffset(pm.getOffset());	
		
		condition.setDepartId(condition.getDepartId());
		if(condition.getGroupId()!=null){
			condition.setGroupId(condition.getGroupId());
		}
		//???????????????????????????????????????????????????????????????
		User user = userService.getCurrentUser();
		List<ScheduleGroup> groupList = new ArrayList<ScheduleGroup>();
		if(user!=null&&user.getEmployee()!=null) {
			groupList = scheduleGroupMapper.getListBySchedulerOrAuditor(user.getEmployee().getId());
		}
		List<Long> groupIdList = groupList.stream().map(ScheduleGroup::getId).collect(Collectors.toList());
		if(groupIdList!=null&&!groupIdList.isEmpty()) {
			condition.setGroupIdList(groupIdList);
		}
		
		condition.setIsMove(ApplicationEmployeeClass.IS_MOVE_0);
		List<Integer> approvalStatusList = new ArrayList<Integer>();
		approvalStatusList.add(ConfigConstants.DOING_STATUS);//?????????
		approvalStatusList.add(ConfigConstants.PASS_STATUS);//??????
		approvalStatusList.add(ConfigConstants.REFUSE_STATUS);//??????
		approvalStatusList.add(ConfigConstants.BACK_STATUS);//??????
		approvalStatusList.add(ConfigConstants.OVERDUE_STATUS);//??????
		approvalStatusList.add(ConfigConstants.OVERDUEPASS_STATUS);//????????????
		approvalStatusList.add(ConfigConstants.OVERDUEREFUSE_STATUS);//????????????
		condition.setApprovalStatusList(approvalStatusList);
		List<ApplicationEmployeeClass> list = getByCondition(condition);
		Map<Long,String> isExistGroupName = new HashMap<Long,String>();//????????????????????????
		for(ApplicationEmployeeClass data:list){
			if(isExistGroupName!=null&&isExistGroupName.containsKey(data.getGroupId())){
				data.setGroupName(isExistGroupName.get(data.getGroupId()).split(",")[0]);
				data.setAuditorName(isExistGroupName.get(data.getGroupId()).split(",")[1]);
			}else{
				ScheduleGroup scheduleGroup = scheduleGroupMapper.getGroupById(data.getGroupId()!=null?data.getGroupId():0L);
				Employee employee = employeeMapper.getById(scheduleGroup!=null?scheduleGroup.getAuditor():0L);
				data.setGroupName(scheduleGroup!=null?scheduleGroup.getName():" ");
				data.setAuditorName(employee!=null?employee.getCnName():" ");
				isExistGroupName.put(data.getGroupId(), (scheduleGroup!=null?scheduleGroup.getName():" ")+","+(employee!=null?employee.getCnName():" "));
			}
		}
		pm.setRows(list);
		pm.setTotal(applicationEmployeeClassMapper.getCountByCondition(condition));
		
		return pm;
	}
}
