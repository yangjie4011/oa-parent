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
 * @ClassName: 排班申请表
 * @Description: 排班申请表
 * @author yangjie
 * @date 2017年8月31日
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
		//判断用户是否排班人
		List<ScheduleGroup> schedulerList = scheduleGroupMapper.getListByScheduler(user.getEmployee().getId());
		if(schedulerList==null || schedulerList.size() <=0){
			throw new OaException("没有排班权限！");
		}
		Long groupId = schedulerList.get(0).getId();//排班组别ID
		Long departId = schedulerList.get(0).getDepartId();//排班组别的部门id
		//判断下个月排班数据是否存在
		ApplicationEmployeeClass condition = new ApplicationEmployeeClass();
		condition.setClassMonth(DateUtils.getFirstDay(DateUtils.addMonth(new Date(),1)));
		condition.setDepartId(departId);
		condition.setGroupId(groupId);
		List<ApplicationEmployeeClass> list = applicationEmployeeClassMapper.getByCondition(condition);
		if(list!=null&&list.size()>0){
			throw new OaException("下个月排班已存在！");
		}else{
			//查询排班组别下的所有员工
			List<Employee> employeeList = scheduleGroupMapper.getAllGroupEmp(groupId,null,condition.getClassMonth());
			//查询组别部门信息
			Depart depart = departService.getById(departId);
			Long workTypeId = null;
			Long whetherScheduleId = null;
			CompanyConfig companyConfigConditon = new CompanyConfig();//查询配置表条件
			companyConfigConditon.setCode("typeOfWork");//工时类型
			List<CompanyConfig> workTypeList = companyConfigService.getListByCondition(companyConfigConditon);
			for(CompanyConfig companyConfig:workTypeList){
				if("standard".equals(companyConfig.getDisplayCode())){
					workTypeId = companyConfig.getId();
					break;
				}
			}
			Config configCondition = new Config();
			configCondition.setCode("whetherScheduling");//是否排班
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
				//只有标准工时并且排班的员工要排班
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
				//下个月的第一天
				Date month_start1 = DateUtils.getFirstDay(DateUtils.addMonth(new Date(),1));
				//下个月的最后一天
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
					//下个月的第一天
					Date month_start = DateUtils.getFirstDay(DateUtils.addMonth(new Date(),1));
					//下个月的最后一天
					Date month_end = DateUtils.getLastDay(DateUtils.addMonth(new Date(),1));
					boolean isEndTime = true;
					do{
						//如果入职时间在该月，需去除部分的应出勤时间
						if(employee.getQuitTime()==null&&employee.getFirstEntryTime()!=null&&DateUtils.format(month_start, "yyyy-MM").equals(DateUtils.format(employee.getFirstEntryTime(), "yyyy-MM"))){
							if(employee.getFirstEntryTime().getTime()>month_start.getTime()){
								if(annualVacationService.judgeWorkOrNot(month_start)){
									shouldTime = shouldTime - 8;
								}
							}
						}
						//如果离职时间在该月，需去除部分的应出勤时间
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
				throw new OaException("该单据已经提交审核过，不能重复提交！");
			}
			
			//节假日前四个工作日
			Config configCondition = new Config();
			configCondition.setCode("dutyCommitTimeLimit");
			List<Config> limit = configService.getListByCondition(configCondition);
			int num = Integer.valueOf(limit.get(0).getDisplayCode());
			
			Date lastSubDate = annualVacationService.getWorkingDayPre(num,DateUtils.addDay(list.get(0).getClassMonth(), -1));
			if(DateUtils.getIntervalDays(new Date(),lastSubDate)<0){
				throw new OaException("排班申请需提前4个工作日提交！");
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
				map.put("message", i+"位员工工时不足，请修改排班！");
				return map;
			}
			
			//查询该部门的排班人和审核人
			List<ScheduleGroup> schedulerList = scheduleGroupMapper.getListByScheduler(user.getEmployee().getId());
			if(schedulerList==null || schedulerList.size() <=0){
				map.put("success", false);
				map.put("message","没有排班权限！");
				return map;
			}
			Long auditor = schedulerList.get(0).getAuditor();//排班审核人
			
			//指定下一步审批人
			Map<String, Object> variables = new HashMap<>();
			variables.put("approver",auditor+"");
			//启动流程，默认提交
			ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(ConfigConstants.SCHEDULING_KEY,variables);
			
			//修改排班状态
			applicationEmployeeClass.setProcessInstanceId(processInstance.getId());
			applicationEmployeeClass.setApprovalStatus(ConfigConstants.DOING_STATUS);
			applicationEmployeeClass.setUpdateUser(user.getEmployee().getCnName());
			applicationEmployeeClass.setUpdateTime(new Date());
			applicationEmployeeClassMapper.updateById(applicationEmployeeClass);
			
			//-----------------start-----------------------保存流程节点信息
			ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
			flow.setAssigneeName(user.getEmployee().getCnName());
			flow.setDepartName(user.getDepart().getName());
			flow.setPositionName(user.getPosition().getPositionName());
			flow.setFinishTime(new Date());
			flow.setComment("排班申请");
			flow.setProcessId(processInstance.getId());
			flow.setProcessKey(ConfigConstants.SCHEDULING_KEY);
			flow.setStatu(0);
			viewTaskInfoService.save(flow);
			
			//判断排班人和下个节点是否是一个操作人，是的话，直接启动下一个流程
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
				//指定下一步审批人
				Map<String, Object> variables1 = new HashMap<>();
				variables1.put("isContainLegal",containLeagl);
				Task task1 = taskService.createTaskQuery().taskId(task.getId()).list().get(0);
		    	taskService.addComment(task1.getId(), task1.getProcessInstanceId(), "排班申请");
		    	//-----------------start-----------------------保存流程节点信息
				ViewTaskInfoTbl flow1 = new ViewTaskInfoTbl();
				flow1.setAssigneeName(user.getEmployee().getCnName());
				flow1.setDepartName(user.getDepart().getName());
				flow1.setPositionName(user.getPosition().getPositionName());
				flow1.setFinishTime(new Date());
				flow1.setComment("排班申请");
				flow1.setProcessId(processInstance.getId());
				flow1.setProcessKey(ConfigConstants.SCHEDULING_KEY);
				flow1.setStatu(100);
				viewTaskInfoService.save(flow1);
		    	taskService.complete(task1.getId(), variables1);
			}
			map.put("success", true);
			map.put("message", "已提交，审核通过后生效!");
		}else{
			throw new OaException("系统异常，请刷新重试！");
		}
		return map;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void completeTask(HttpServletRequest request,String processId, String comment,
			String commentType) throws Exception {
		
		logger.info("排班申请completeTask:start。。");
		Long time1 = System.currentTimeMillis();
			
		User user= userService.getCurrentUser();
		logger.info("排班申请completeTask入参:processId="+processId+";comment="+comment+";commentType="+commentType
			+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));

		Task task = activitiServiceImpl.queryTaskByProcessInstanceId(processId);
		if(task==null){
			throw new OaException("实例Id为"+processId+"的流程不存在！");
		}
		boolean type = StringUtils.startsWithIgnoreCase(task.getTaskDefinitionKey(), "personnel");
		ApplicationEmployeeClass employeeClass = applicationEmployeeClassMapper.queryByProcessInstanceId(processId);
		if(employeeClass!=null){
			if(employeeClass.getIsMove().intValue()==0){
				//每月1号18:00前过期
				Date date = DateUtils.parse(DateUtils.format(employeeClass.getClassMonth(), DateUtils.FORMAT_SHORT)+" 18:00:00",DateUtils.FORMAT_LONG);
				if(DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_SHORT), DateUtils.FORMAT_SHORT).getTime()>date.getTime()){
					throw new OaException("已超过审批时间无法审批！");
				}
			}
			Integer approvalStatus ;
			if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.PASS)) {
				approvalStatus=type?ConfigConstants.PASS_STATUS:ConfigConstants.DOING_STATUS;
				ApplicationEmployeeClassDetail condition = new ApplicationEmployeeClassDetail();
				condition.setAttnApplicationEmployClassId(employeeClass.getId());
				
				//查询排班时间内的所有法定假期
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
				//-----------------start-----------------------保存流程节点信息
				saveFlow(user,comment,processId,approvalStatus);
				//-----------------end-------------------------
				//判断当前节点是否有办理人assignee,没有则将当前代理人设为办理人
				if(StringUtils.isBlank(task.getAssignee())) {
					activitiServiceImpl.claimTask(task.getId(), String.valueOf(userService.getCurrentUser().getEmployeeId()));
				}
				activitiServiceImpl.completeTask(task.getId(), "审批通过", variables,ConfigConstants.PASS);
			}else if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.REFUSE)) {
				approvalStatus=ConfigConstants.REFUSE_STATUS;
				//-----------------start-----------------------保存流程节点信息
				saveFlow(user,comment,processId,approvalStatus);
				//-----------------end-------------------------
				//判断当前节点是否有办理人assignee,没有则将当前代理人设为办理人
				if(StringUtils.isBlank(task.getAssignee())) {
					activitiServiceImpl.claimTask(task.getId(), String.valueOf(userService.getCurrentUser().getEmployeeId()));
				}
				activitiServiceImpl.completeTask(task.getId(), comment,null,ConfigConstants.REFUSE);
				if(employeeClass.getIsMove().intValue()==0){//排班被拒绝，生成一份备份数据
					backUp(employeeClass);
				}
			}else{
				approvalStatus=ConfigConstants.BACK_STATUS;
				//-----------------start-----------------------保存流程节点信息
				saveFlow(user,comment,processId,approvalStatus);
				//-----------------end-------------------------
				activitiServiceImpl.completeTask(task.getId(), comment,null,ConfigConstants.BACK);
                if(employeeClass.getIsMove().intValue()==0){//排班被拒绝，生成一份备份数据
                	backUp(employeeClass);
				}
			}
			ApplicationEmployeeClass update = new ApplicationEmployeeClass();
			update.setApprovalStatus(approvalStatus);
			update.setId(employeeClass.getId());
			applicationEmployeeClassMapper.updateById(update);
		}else{
			throw new OaException("流程实例为"+processId+"的排班审批数据不存在！");
		}
		
		Long time2 = System.currentTimeMillis();
		logger.info("排班申请completeTask:use time="+(time2-time1));
		logger.info("排班申请completeTask:end。。");
	}
	
	//备份排班拒绝的数据
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
				throw new OaException("排班申请审核中，不能删除！");
			}else{
				if(applicationEmployeeClassMapper.deleteById(list.get(0))>0){
					ApplicationEmployeeClassDetail detail =new ApplicationEmployeeClassDetail();
					detail.setAttnApplicationEmployClassId(id);
					detail.setUpdateTime(new Date());
					detail.setUpdateUser(user.getEmployee().getCnName());
					applicationEmployeeClassDetailMapper.deleteByEmployeeClassId(detail);
				}else{
					throw new OaException("系统异常，请刷新重试！");
				}
			}
		}else{
			throw new OaException("排班申请数据不存在！");
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
		//先要删除之前排过但被取消掉的班次
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
			//判断员工是否是新员工
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
					//判断该员工是否存在初始化排班数据
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
							throw new OaException("系统异常，请刷新重试！");
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
			//查询已排班人数
			List<ApplicationEmployeeClassDetail> classEmployeeNum = applicationEmployeeClassDetailMapper.getclassEmployeeNum(classdetailId);
			//已排班size就不能重复计算
			employeeClass.setUpdateTime(new Date());
			employeeClass.setEmployeeNum(employeeClassList.get(0).getEmployeeNum()+employeeNum);
			employeeClass.setUpdateUser(user.getEmployee().getCnName());
			employeeClass.setClassEmployeeNum((classEmployeeNum!=null&&classEmployeeNum.size()>0)?classEmployeeNum.size():0);
			employeeClass.setApprovalStatus(null);
			int count = applicationEmployeeClassMapper.updateById(employeeClass);
			if(count<=0){
				throw new OaException("系统异常，请刷新重试！");
			}
		}else{
			throw new OaException("系统异常，请刷新重试！");
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> moveEmployeeClass(HttpServletRequest request,Long classdetailId, User user,Map<String,Map<String,String>> map,Map<String,Map<String,String>> map1)
			throws OaException {
		
		logger.info("排班申请moveEmployeeClass:start。。");
		Long time1 = System.currentTimeMillis();
		logger.info("排班申请moveEmployeeClass入参:classdetailId="+classdetailId
			+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));
		
		//查询该部门的排班人和审核人
		List<ScheduleGroup> schedulerList = scheduleGroupMapper.getListByScheduler(user.getEmployee().getId());
		if(schedulerList==null || schedulerList.size() <=0){
			throw new OaException("没有排班权限！");
		}
		//排班申请主表数据
		ApplicationEmployeeClass applicationEmployeeClass = new ApplicationEmployeeClass();
		applicationEmployeeClass.setId(classdetailId);
		List<ApplicationEmployeeClass> list = applicationEmployeeClassMapper.getByCondition(applicationEmployeeClass);
		//应出勤小时数
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
		
		
		//查询该月的法定节假日
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
					//调班记录
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
					//原先班次记录
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
					//调班记录
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
					//原先班次记录
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
					
					//调班包含法定节假日，需提前四天
					if(vaContainMap!=null&&vaContainMap.containsKey(detail.getKey())){
						
						//节假日前四个工作日
						Config configCondition = new Config();
						configCondition.setCode("dutyCommitTimeLimit");
						List<Config> limit = configService.getListByCondition(configCondition);
						int num = Integer.valueOf(limit.get(0).getDisplayCode());
						
						Date lastSubDate = annualVacationService.getWorkingDayPre(num,DateUtils.parse(detail.getKey(), "yyyy-MM-dd"));
						if(DateUtils.getIntervalDays(new Date(),lastSubDate)<0){
							throw new OaException("值班申请需提前4个工作日提交！");
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
					//查看之前是否有排班
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
		//校验工时是否充足
		for (Map.Entry<Long, Double>  detail: workHoursMap.entrySet()) {
			Double minus = (minusMap!=null&&minusMap.containsKey(detail.getKey()))?minusMap.get(detail.getKey()):0d;
			Double add = (addMap!=null&&addMap.containsKey(detail.getKey()))?addMap.get(detail.getKey()):0d;
			Double must = 0d;
			//查询员工已排班时间
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
//				throw new OaException("有员工工时不足！");
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

			Long auditor = schedulerList.get(0).getAuditor();//排班审核人
			//启动流程
			Map<String, Object> variables = new HashMap<>();
			variables.put("approver",auditor+"");
			ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("scheduling",variables);
			
			//-----------------start-----------------------保存流程节点信息
			ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
			flow.setAssigneeName(user.getEmployee().getCnName());
			flow.setDepartName(user.getDepart().getName());
			flow.setPositionName(user.getPosition().getPositionName());
			flow.setFinishTime(new Date());
			flow.setComment("排班申请");
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
			applicationEmployeeClass.setMoveType(0);//调班申请
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
		    	taskService.addComment(task1.getId(), task1.getProcessInstanceId(), "排班申请");
		    	
		    	//-----------------start-----------------------保存流程节点信息
				ViewTaskInfoTbl flow1 = new ViewTaskInfoTbl();
				flow1.setAssigneeName(user.getEmployee().getCnName());
				flow1.setDepartName(user.getDepart().getName());
				flow1.setPositionName(user.getPosition().getPositionName());
				flow1.setFinishTime(new Date());
				flow1.setComment("排班申请");
				flow1.setProcessId(processInstance.getId());
				flow1.setProcessKey(ConfigConstants.SCHEDULING_KEY);
				flow1.setStatu(100);
				viewTaskInfoService.save(flow1);
				
		    	taskService.complete(task1.getId(), variables1);
		    	
			}
		}
		
		Long time2 = System.currentTimeMillis();
		logger.info("排班申请moveEmployeeClass:use time="+(time2-time1));
		logger.info("排班申请moveEmployeeClass:end。。");
		
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
					//删除排班数据
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
						//排班日期包含法定节假日，生成值班数据
						if(vacationMap!=null&&vacationMap.containsKey(DateUtils.parse(detail.getKey(), "yyyy-MM-dd"))){
							EmployeeDuty duty = new EmployeeDuty();
							duty.setEmployId(Long.valueOf(employeeId));
							duty.setDutyDate(DateUtils.parse(detail.getKey(), "yyyy-MM-dd"));
							duty.setDutyItem("法定节假日排班");
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
					//存在的修改，不存在的新增
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
						//存在修改，不存在新增
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
								//排班日期包含法定节假日，生成值班数据
								if(vacationMap!=null&&vacationMap.containsKey(classDetail.getClassDate())){
									ClassSetting setting = classSettingMapper.getById(classDetail.getClassSettingId());
									EmployeeDuty duty = new EmployeeDuty();
									duty.setEmployId(classDetail.getEmployId());
									duty.setDutyDate(classDetail.getClassDate());
									duty.setDutyItem("法定节假日排班");
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
								//排班日期包含法定节假日，生成值班数据
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
									duty.setDutyItem("法定节假日排班");
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
	
	//获取员工的应出勤时间(只支持操作时间未当月的)
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
				//如果入职时间在该月，需去除部分的应出勤时间
				if(old.getQuitTime()==null&&old.getFirstEntryTime()!=null&&DateUtils.format(month_start, "yyyy-MM").equals(DateUtils.format(old.getFirstEntryTime(), "yyyy-MM"))){
					if(old.getFirstEntryTime().getTime()<=month_start.getTime()){
						if(annualVacationService.judgeWorkOrNot(month_start)){
							shouldTime = shouldTime + 8;
						}
					}
				}
				//如果离职时间在该月，需去除部分的应出勤时间
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
			logger.error("获取用户={}应出勤失败",old.getCnName(),e);
		}
		return shouldTime;
	}
	
	/**
	  * exportEmpClassReprotById(导出排班查询报表)
	  * @Title: exportEmpClassReprotById
	  * @Description: 导出排班查询报表
	  * @param empClassId
	  * @throws Exception    设定文件
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
			Map<String,String[]> titleMap = getEmpClassReportTitle(yearMonth);//动态生成表头
			String[] title2 = titleMap.get("t2");//第二行表头
			String[] title3 = titleMap.get("t3");//第三行表头
			// 表头标题样式
			HSSFCellStyle colstyle = workbook.createCellStyle();
			colstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
			colstyle.setBorderBottom(XSSFCellStyle.BORDER_THIN); //下边框
			colstyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);//左边框
			colstyle.setBorderTop(XSSFCellStyle.BORDER_THIN);//上边框
			colstyle.setBorderRight(XSSFCellStyle.BORDER_THIN);//右边框
			
			//建立第一行表头
			HSSFRow row = sheet.createRow((short) 0);
			//创建第一行表头内容
			ExcelUtil.createRow(row, 0, null, HSSFCell.CELL_TYPE_STRING,yearMonth+"排班表");
			sheet.addMergedRegion(new CellRangeAddress(0,0,0,2));
			
			//建立第二行表头
			row = sheet.createRow((short) 1);
			//创建第二行表头内容
			for(int colIndex = 0; colIndex < title2.length; colIndex++){
				if(colIndex >=0 && colIndex <= 2){
					sheet.setColumnWidth(colIndex, 5000);//设置表格宽度
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
			
			//建立第三行表头(从第二行开始算，第一行表头是动态生成的数据)
			row = sheet.createRow((short) 2);
			//创建第三行表头内容
			for(int colIndex = 3; colIndex < title3.length; colIndex++){
				if(colIndex >=3 && colIndex <= title3.length - 2){
					sheet.setColumnWidth(colIndex, 1200);//设置表格宽度
				}else{
					sheet.setColumnWidth(colIndex, 5000);//设置表格宽度
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
				
				if(null != empId && empId.equals(data.get("empId"))){//是同一个员工记录
					mustAttnTime += (Double)data.get("mustAttnTime");
					ExcelUtil.createRow(row, 34, null, HSSFCell.CELL_TYPE_NUMERIC,mustAttnTime);
					ExcelUtil.createRow(row, Integer.valueOf(classDay+"") + 2, null, HSSFCell.CELL_TYPE_STRING,data.get("name"));//排班工时
				}else{
					row = sheet.createRow((short) index);
					ExcelUtil.createRow(row, Integer.valueOf(classDay+"") + 2, null, HSSFCell.CELL_TYPE_STRING,data.get("name"));//排班工时
					
					Position position = positionService.getByEmpId((Long)data.get("empId"));
					ExcelUtil.createRow(row, 0, null, HSSFCell.CELL_TYPE_STRING,data.get("empName"));
					ExcelUtil.createRow(row, 1, null, HSSFCell.CELL_TYPE_STRING,null == position ? "" : position.getPositionName());
					ExcelUtil.createRow(row, 2, null, HSSFCell.CELL_TYPE_NUMERIC,data.get("shouldTime"));//应出勤工时
					ExcelUtil.createRow(row, 34, null, HSSFCell.CELL_TYPE_NUMERIC,(Double)data.get("mustAttnTime"));
					
					mustAttnTime = (Double)data.get("mustAttnTime");
					empId = (Long)data.get("empId");
					index++;
				}
			}
		} else {
			ExcelUtil.createRow(sheet.createRow(1), 0, null,HSSFCell.CELL_TYPE_STRING, "无数据");
		}
		
		ClassSetting classSetting = new ClassSetting();
		classSetting.setDepartId(list.get(0).getDepartId());
		List<ClassSetting> classSettingList = classSettingMapper.getListByCondition(classSetting);
		
		HSSFSheet sheet1 = workbook.createSheet("班次");
		//建立第一行表头
		HSSFRow row = sheet1.createRow((short) 0);
		//创建第一行表头内容
		ExcelUtil.createRow(row, 0, null, HSSFCell.CELL_TYPE_STRING,"班次名称");
		ExcelUtil.createRow(row, 1, null, HSSFCell.CELL_TYPE_STRING,"开始时间");
		ExcelUtil.createRow(row, 2, null, HSSFCell.CELL_TYPE_STRING,"结束时间");
		ExcelUtil.createRow(row, 3, null, HSSFCell.CELL_TYPE_STRING,"应出勤工时（小时）");
		
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
		String[] titles2 = {"姓名", "职位", "应出勤工时","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",""};
		String[] titles3 = {"","","","1", "2", "3" ,"4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", 
				"16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27","","","","",""};
		
		//获得当月最大天数
		Calendar cal = Calendar.getInstance();
		cal.setTime(DateUtils.parse(yearMonth + "-01 00:00:00"));
		Integer maxDay = cal.getActualMaximum(Calendar.DATE);
		
		//动态生成第三行标题
		if(maxDay == 28){
			titles3[30] = "28";
			titles2[31] = "排班总工时";
		}else if(maxDay == 29){
			titles3[30] = "28";
			titles3[31] = "29";
			titles2[32] = "排班总工时";
		}else if(maxDay == 30){
			titles3[30] = "28";
			titles3[31] = "29";
			titles3[32] = "30";
			titles2[33] = "排班总工时";
		}else{
			titles3[30] = "28";
			titles3[31] = "29";
			titles3[32] = "30";
			titles3[33] = "31";
			titles2[34] = "排班总工时";
		}
		
		titleMap.put("t3", titles3);
		
		//动态生成第二行标题
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
			Map<String,String[]> titleMap = getEmpClassReportTitle(yearMonth);//动态生成表头
			String[] title2 = titleMap.get("t2");//第二行表头
			String[] title3 = titleMap.get("t3");//第三行表头
			// 表头标题样式
			HSSFCellStyle colstyle = workbook.createCellStyle();
			colstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
			colstyle.setBorderBottom(XSSFCellStyle.BORDER_THIN); //下边框
			colstyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);//左边框
			colstyle.setBorderTop(XSSFCellStyle.BORDER_THIN);//上边框
			colstyle.setBorderRight(XSSFCellStyle.BORDER_THIN);//右边框
			
			//建立第一行表头
			HSSFRow row = sheet.createRow((short) 0);
			//创建第一行表头内容
			ExcelUtil.createRow(row, 0, null, HSSFCell.CELL_TYPE_STRING,yearMonth+"排班表");
			sheet.addMergedRegion(new CellRangeAddress(0,0,0,2));
			
			//建立第二行表头
			row = sheet.createRow((short) 1);
			//创建第二行表头内容
			for(int colIndex = 0; colIndex < title2.length; colIndex++){
				if(colIndex >=0 && colIndex <= 2){
					sheet.setColumnWidth(colIndex, 5000);//设置表格宽度
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
			
			//建立第三行表头(从第二行开始算，第一行表头是动态生成的数据)
			row = sheet.createRow((short) 2);
			//创建第三行表头内容
			for(int colIndex = 3; colIndex < title3.length; colIndex++){
				if(colIndex >=3 && colIndex <= title3.length - 2){
					sheet.setColumnWidth(colIndex, 1200);//设置表格宽度
				}else{
					sheet.setColumnWidth(colIndex, 5000);//设置表格宽度
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
				
				if(null != empId && empId.equals(data.get("empId"))){//是同一个员工记录
					mustAttnTime += (Double)data.get("mustAttnTime");
					ExcelUtil.createRow(row, 34, null, HSSFCell.CELL_TYPE_NUMERIC,mustAttnTime);
					ExcelUtil.createRow(row, Integer.valueOf(classDay+"") + 2, null, HSSFCell.CELL_TYPE_STRING,data.get("name"));//排班工时
				}else{
					row = sheet.createRow((short) index);
					ExcelUtil.createRow(row, Integer.valueOf(classDay+"") + 2, null, HSSFCell.CELL_TYPE_STRING,data.get("name"));//排班工时
					
					Position position = positionService.getByEmpId((Long)data.get("empId"));
					ExcelUtil.createRow(row, 0, null, HSSFCell.CELL_TYPE_STRING,data.get("empName"));
					ExcelUtil.createRow(row, 1, null, HSSFCell.CELL_TYPE_STRING,null == position ? "" : position.getPositionName());
					ExcelUtil.createRow(row, 2, null, HSSFCell.CELL_TYPE_NUMERIC,data.get("shouldTime"));//应出勤工时
					ExcelUtil.createRow(row, 34, null, HSSFCell.CELL_TYPE_NUMERIC,(Double)data.get("mustAttnTime"));
					
					mustAttnTime = (Double)data.get("mustAttnTime");
					empId = (Long)data.get("empId");
					index++;
				}
			}
		} else {
			ExcelUtil.createRow(sheet.createRow(1), 0, null,HSSFCell.CELL_TYPE_STRING, "无数据");
		}
		
		ClassSetting classSetting = new ClassSetting();
		List<ClassSetting> classSettingList = classSettingMapper.getListByCondition(classSetting);
		
		HSSFSheet sheet1 = workbook.createSheet("班次");
		//建立第一行表头
		HSSFRow row = sheet1.createRow((short) 0);
		//创建第一行表头内容
		ExcelUtil.createRow(row, 0, null, HSSFCell.CELL_TYPE_STRING,"班次名称");
		ExcelUtil.createRow(row, 1, null, HSSFCell.CELL_TYPE_STRING,"简称");
		ExcelUtil.createRow(row, 2, null, HSSFCell.CELL_TYPE_STRING,"开始时间");
		ExcelUtil.createRow(row, 3, null, HSSFCell.CELL_TYPE_STRING,"结束时间");
		ExcelUtil.createRow(row, 4, null, HSSFCell.CELL_TYPE_STRING,"应出勤工时（小时）");
		
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
		//同步数据到排班表
		ApplicationEmployeeClass employClass = applicationEmployeeClassMapper.queryByProcessInstanceId(processInstanceId);
		if(employClass != null){
			Map<Date,AnnualVacation> vacationMap = annualVacationService.getVacationMap(employClass.getClassMonth());
			ApplicationEmployeeClassDetail param = new ApplicationEmployeeClassDetail();
			param.setAttnApplicationEmployClassId(employClass.getId());
			
			if(employClass.getIsMove().intValue()==0){
				    param.setIsMove(0);
				    List<ApplicationEmployeeClassDetail> list = applicationEmployeeClassDetailMapper.selectByCondition(param);
				    //排班审核
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
							//排班日期包含法定节假日，生成值班数据
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
								duty.setDutyItem("法定节假日排班");
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
				//调班审核
				for(ApplicationEmployeeClassDetail classDetail:list){
					//存在修改，不存在新增
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
							//排班日期包含法定节假日，生成值班数据
							if(vacationMap!=null&&vacationMap.containsKey(classDetail.getClassDate())){
								ClassSetting setting = classSettingMapper.getById(classDetail.getClassSettingId());
								EmployeeDuty duty = new EmployeeDuty();
								duty.setEmployId(classDetail.getEmployId());
								duty.setDutyDate(classDetail.getClassDate());
								duty.setDutyItem("法定节假日排班");
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
							//排班日期包含法定节假日，生成值班数据
							if(vacationMap!=null&&vacationMap.containsKey(classDetail.getClassDate())){
								EmployeeDuty duty = new EmployeeDuty();
								duty.setEmployId(classDetail.getEmployId());
								duty.setDutyDate(classDetail.getClassDate());
								duty.setDutyItem("法定节假日排班");
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
							//排班日期包含法定节假日，生成值班数据
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
								duty.setDutyItem("法定节假日排班");
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
				//重新计算考勤
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
			
				    //重新计算考勤
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
			taskVO.setProcessName("排班申请");
			if(employeeClass.getIsMove().intValue()==1){
				taskVO.setProcessName("调班申请");
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
    
	//分页查询待办
	@Override
	public PageModel<ApplicationEmployeeClass> getHandlingListByPage(
			ApplicationEmployeeClass employeeclass) {
		employeeclass.setApprovalStatus(ConfigConstants.DOING_STATUS);//待审批
		employeeclass.setAssignee(String.valueOf(userService.getCurrentUser().getEmployee().getId()));//待办人
		if(employeeclass.getFirstDepart()!=null&&!"".equals(employeeclass.getFirstDepart())){//部门
			employeeclass.setDepartId(Long.valueOf(employeeclass.getFirstDepart()));
		}
		if(employeeclass.getMonth()!=null&&!"".equals(employeeclass.getMonth())){//年月
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
        
		Map<Date,Double> isExistShouldTime = new HashMap<Date,Double>();//所在月份是否存在应出勤时间
		Map<Long,String> isExistGroupName = new HashMap<Long,String>();//是否存在组别名称
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
	
    //根据日期获取应出勤时间
	@Override
	public Double getShouldTime(Date startTime,Date endTime){
		
		boolean isEndTime1 = true;
	    double should_time = 0;
	    do{
	    	if(annualVacationService.judgeWorkOrNot(startTime)){//判断是否是周末或者节假日
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
		
		ApplicationEmployeeClass employeeClass = applicationEmployeeClassMapper.getById(id);//申请单
		
		//获取所有班次
		List<ClassSetting> settList = classSettingMapper.getList();
		Map<String,Double> setMap = new HashMap<String,Double>();
		for(ClassSetting set:settList){
			setMap.put(String.valueOf(set.getId()), set.getMustAttnTime());
		}
		
		Map<String,Object> result = new HashMap<String,Object>();//返回结果
		//表头，星期和天数
		List<String> weekDays = new ArrayList<String>();
		//表头，天数
		List<String> days = new ArrayList<String>();
		weekDays.add("职位");
		weekDays.add("姓名");
		weekDays.add("应出勤工时");
		weekDays.add("排班天数");
		weekDays.add("排班工时");
		days.add("");
		days.add("");
		days.add("");
		days.add("");
		days.add("");
		
		Date fristDay = DateUtils.getFirstDay(employeeClass.getClassMonth());
		Date lastDay = DateUtils.getLastDay(employeeClass.getClassMonth());
		//标题
		result.put("title", employeeClass.getDepartName()+"日常工作排班表"+"-"+DateUtils.format(employeeClass.getClassMonth(), "yyyy年MM月"));
		//申请日期
		result.put("applyDate", DateUtils.format(employeeClass.getCreateTime(), DateUtils.FORMAT_SHORT));
		//审核状态
		if(employeeClass!=null&&
				employeeClass.getApprovalStatus().intValue()==ConfigConstants.DOING_STATUS.intValue()){
			result.put("approvalStatus", "待审核");
		}else if(employeeClass!=null&&
				employeeClass.getApprovalStatus().intValue()==ConfigConstants.PASS_STATUS.intValue()){
			result.put("approvalStatus", "已通过");
		}else if(employeeClass!=null&&
				employeeClass.getApprovalStatus().intValue()==ConfigConstants.REFUSE_STATUS.intValue()){
			result.put("approvalStatus", "已拒绝");
		}else if(employeeClass!=null&&
				employeeClass.getApprovalStatus().intValue()==ConfigConstants.BACK_STATUS.intValue()){
			result.put("approvalStatus", "已撤回");
		}else if(employeeClass!=null&&
				employeeClass.getApprovalStatus().intValue()==ConfigConstants.OVERDUE_STATUS.intValue()){
			result.put("approvalStatus", "已失效");
		}else if(employeeClass!=null&&
				employeeClass.getApprovalStatus().intValue()==ConfigConstants.OVERDUEPASS_STATUS.intValue()){
			result.put("approvalStatus", "失效同意");
		}else if(employeeClass!=null&&
				employeeClass.getApprovalStatus().intValue()==ConfigConstants.OVERDUEREFUSE_STATUS.intValue()){
			result.put("approvalStatus", "失效拒绝");
		}
		
		//查询组别
		result.put("groupName","");
		if(employeeClass.getGroupId()!=null){
			ScheduleGroup group = scheduleGroupMapper.getGroupById(employeeClass.getGroupId());
			result.put("groupName",group.getName());
		}
		
		List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
		
		List<Map<String,Object>> datas1 = new ArrayList<Map<String,Object>>();
        
		//排班申请直接查询排班申请表，调班申请查询排班表
		if(employeeClass.getIsMove().intValue()==ApplicationEmployeeClass.IS_MOVE_0){
			datas = applicationEmployeeClassMapper.getDetailById(employeeClass.getId(),0L);
			datas1 = applicationEmployeeClassMapper.getClassHoursMap(employeeClass.getId());//统计的排班申请明细表
			//组装表格数据
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
			//标题
			result.put("title", employeeClass.getDepartName()+"日常工作调班表"+"-"+DateUtils.format(employeeClass.getClassMonth(), "yyyy年MM月"));
			//查询调班申请员工个数
			List<Long> emoloyIds = applicationEmployeeClassMapper.getEmployeeIdListById(employeeClass.getId());
			//统计职位，姓名，应出勤工时，排班天数，排班工时
			EmployeeClass param1 = new EmployeeClass();
			param1.setDepartId(employeeClass.getDepartId());
			param1.setStartTime(fristDay);
			param1.setEndTime(lastDay);
			param1.setEmployeeIds(emoloyIds);
			datas1 = employeeClassMapper.getClassHoursMap(param1);//统计的最终表
			if(datas1 ==null|| datas1.size()<=0) {
				Double sd = getShouldTime(fristDay,lastDay);
				datas1 = applicationEmployeeClassMapper.getClassHoursMap(employeeClass.getId());
				for(Map<String,Object> data : datas1) {
					data.put("dayCount", 0);
					data.put("must_attn_time", 0d);
					data.put("should_time", sd);
				}
			}
			
			//统计出没有修改过的数据
			ApplicationEmployeeClass param = new ApplicationEmployeeClass();
			param.setDepartId(employeeClass.getDepartId());
			param.setStartTime(fristDay);
			param.setEndTime(lastDay);
			param.setEmployeeIds(emoloyIds);
			datas = applicationEmployeeClassMapper.getEmpClassReprotById(param);
			//组装没有修改过的数据
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
			
			//统计修改前的员工每天的班次
			datas = applicationEmployeeClassMapper.getDetailById(employeeClass.getId(),0L);
			//组装表格数据
			for(Map<String,Object> data1 : datas1){
				String employ_id = String.valueOf(data1.get("employ_id"));
				for(Map<String,Object> data : datas){
					String empId = String.valueOf(data.get("empId"));
					if(employ_id.equals(empId)){
						if(data.get("classSettingId")!=null){
							data1.put(String.valueOf(data.get("classDay")), String.valueOf(data.get("name"))+","+String.valueOf(data.get("classDate"))+","+String.valueOf(data.get("classSettingId"))+",move");
						}else{
							data1.put(String.valueOf(data.get("classDay")), "休,"+String.valueOf(data.get("classDate"))+",0"+",move");
						}
					}
				}
			}
			//统计修改之后的班次
			//组装表格数据
			datas = applicationEmployeeClassMapper.getDetailById(employeeClass.getId(),1L);
			for(Map<String,Object> data1 : datas1){
				String employ_id = String.valueOf(data1.get("employ_id"));
				for(Map<String,Object> data : datas){
					String empId = String.valueOf(data.get("empId"));
					if(employ_id.equals(empId)){
						if(data1.containsKey(String.valueOf(data.get("classDay")))){
							//查询该员工当天现有的班次
							Long nowSetId = employeeClassMapper.getClassSettingIdByEmployIdAndDate(Long.valueOf(employ_id),DateUtils.parse(String.valueOf(data.get("classDate")), DateUtils.FORMAT_SHORT));
							if(data.get("classSettingId")!=null){
								if(nowSetId!=null){
									//重新计算应出勤
									data1.put("must_attn_time", 
											(Double)data1.get("must_attn_time")
											-(setMap.containsKey(String.valueOf(nowSetId))?setMap.get(String.valueOf(nowSetId)):0)
											+(setMap.containsKey(String.valueOf(data.get("classSettingId")))?setMap.get(String.valueOf(data.get("classSettingId"))):0));
								}else{
									//重新计算应出勤及排班天数
									data1.put("must_attn_time", (Double)data1.get("must_attn_time")
											+(setMap.containsKey(String.valueOf(data.get("classSettingId")))?setMap.get(String.valueOf(data.get("classSettingId"))):0));
									data1.put("dayCount", Integer.valueOf(String.valueOf(data1.get("dayCount")))+1);
								}
								data1.put(String.valueOf(data.get("classDay")), data1.get(String.valueOf(data.get("classDay")))+","+String.valueOf(data.get("name"))+","+String.valueOf(data.get("classSettingId")));
							}else{
								if(nowSetId!=null){
									//重新计算应出勤
									data1.put("must_attn_time", (Double)data1.get("must_attn_time")
											-(setMap.containsKey(String.valueOf(nowSetId))?setMap.get(String.valueOf(nowSetId)):0));
									data1.put("dayCount", Integer.valueOf(String.valueOf(data1.get("dayCount")))-1);
								}
								data1.put(String.valueOf(data.get("classDay")), data1.get(String.valueOf(data.get("classDay")))+",休,0");
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
		employeeclass.setAssignee(String.valueOf(userService.getCurrentUser().getEmployee().getId()));//待办人
		if(employeeclass.getFirstDepart()!=null&&!"".equals(employeeclass.getFirstDepart())){//部门
			employeeclass.setDepartId(Long.valueOf(employeeclass.getFirstDepart()));
		}
		if(employeeclass.getMonth()!=null&&!"".equals(employeeclass.getMonth())){//年月
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
        
		Map<Date,Double> isExistShouldTime = new HashMap<Date,Double>();//所在月份是否存在应出勤时间
		Map<Long,String> isExistGroupName = new HashMap<Long,String>();//是否存在组别名称
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
		employeeclass.setApprovalStatus(ConfigConstants.OVERDUE_STATUS);//已失效
		employeeclass.setAssignee(String.valueOf(userService.getCurrentUser().getEmployee().getId()));//待办人
		if(employeeclass.getFirstDepart()!=null&&!"".equals(employeeclass.getFirstDepart())){//部门
			employeeclass.setDepartId(Long.valueOf(employeeclass.getFirstDepart()));
		}
		if(employeeclass.getMonth()!=null&&!"".equals(employeeclass.getMonth())){//年月
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
        
		Map<Date,Double> isExistShouldTime = new HashMap<Date,Double>();//所在月份是否存在应出勤时间
		Map<Long,String> isExistGroupName = new HashMap<Long,String>();//是否存在组别名称
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
		result.put("date",DateUtils.format(DateUtils.parse(date, DateUtils.FORMAT_SHORT), DateUtils.FORMAT_SHORT_CN));//日期
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
        Calendar cal = Calendar.getInstance(); // 获得一个日历
        Date datet = null;
        try {
            datet = f.parse(date);
            cal.setTime(datet);
        } catch (ParseException e) {
            logger.error("getSettingInfo转换日期失败",e);
        }
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0){
        	w = 0;
        }    
		result.put("weekDay",weekDays[w]);//星期
		//原班次
		if(setId!=null&&setId.intValue()!=0){//0是虚拟的班次id，代表休息，不存在
			ClassSetting old = classSettingMapper.getById(setId);
			result.put("setName", old!=null?old.getName():"");
			result.put("startTime", old!=null?DateUtils.format(old.getStartTime(), DateUtils.FORMAT_HH_MM):"");
			result.put("endTime", old!=null?DateUtils.format(old.getEndTime(), DateUtils.FORMAT_HH_MM):"");
		}else if(setId!=null&&setId.intValue()==0){
			result.put("setName", "休息");
			result.put("startTime", "");
			result.put("endTime", "");
		}
		//调整后班次
		if(newSetId!=null&&newSetId.intValue()!=0){
			ClassSetting set = classSettingMapper.getById(newSetId);
			result.put("newSetName", set!=null?set.getName():"");
			result.put("newStartTime", set!=null?DateUtils.format(set.getStartTime(), DateUtils.FORMAT_HH_MM):"");
			result.put("newEndTime", set!=null?DateUtils.format(set.getEndTime(), DateUtils.FORMAT_HH_MM):"");
		}else if(newSetId!=null&&newSetId.intValue()==0){
			result.put("newSetName", "休息");
			result.put("newStartTime", "");
			result.put("newEndTime", "");
		}
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> hrMoveAtAdmin(HttpServletRequest request,String date, Long employId,
			Long setId,String remark) throws OaException {
		
		logger.info("排班申请hrMoveAtAdmin:start。。");
		Long time1 = System.currentTimeMillis();
			
		User user = userService.getCurrentUser();
		logger.info("排班申请hrMoveAtAdmin入参:date="+date+";employId="+employId+";setId="+setId
				+";remark="+remark+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));
		
		Map<String,Object> result = new HashMap<String,Object>();
		
		//查询员工基本信息
		Employee emp = employeeService.getById(employId);
		if(emp==null||emp.getId()==null){
			result.put("message","该员工信息不存在，请确认！");
			result.put("success",false);
			return result;
		}
		
		//不允许调入职时间前的班次
		Date moveDate = DateUtils.parse(date, DateUtils.FORMAT_SHORT);
		if(emp.getFirstEntryTime()!=null&&emp.getFirstEntryTime().getTime()>moveDate.getTime()){
			result.put("message","不允许调整入职时间前的班次！");
			result.put("success",false);
			return result;
		}
		
		double shouldTime = getShouldTime(DateUtils.getFirstDay(moveDate),emp);
		
		//不允许调及离职时间后的班次
		if(emp.getQuitTime()!=null&&emp.getQuitTime().getTime()<moveDate.getTime()){
			result.put("message","不允许调整值时间后的班次！");
			result.put("success",false);
			return result;
		}
		
		//查询员工的基础信息（部门及组别）
		Depart depart = departService.getInfoByEmpId(employId);
		if(depart==null||depart.getId()==null){
			result.put("message","员工不存在部门信息，不能调班！");
			result.put("success",false);
			return result;
		}
		ScheduleGroup group = scheduleGroupMapper.getGroupByEmployeeId(employId);//组别信息
		//生成调班记录
		ApplicationEmployeeClass record = new ApplicationEmployeeClass();
		record.setDepartId(depart.getId());
		record.setDepartName(depart.getName());
		record.setClassMonth(DateUtils.getFirstDay(moveDate));
		record.setEmployeeNum(1);
		record.setClassEmployeeNum(1);//调班人数
		record.setClassSettingPerson(user.getEmployee().getCnName());
		record.setApprovalStatus(ConfigConstants.PASS_STATUS);
		record.setVersion(0L);
		record.setDelFlag(0);
		record.setCreateUser(user.getEmployee().getCnName());
		record.setCreateTime(new Date());
		record.setIsMove(1);//调班
		record.setProcessInstanceId(null);//人事调班不走流程
		record.setEmployeeId(user.getEmployee().getId());
		record.setGroupId(group!=null?group.getId():null);
		record.setMoveType(1);//人事调班
		record.setRemark(remark);
		applicationEmployeeClassMapper.save(record);
		List<ApplicationEmployeeClassDetail> moveList = new ArrayList<ApplicationEmployeeClassDetail>();
		//新记录
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
		//原先班次记录
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
		
		//查询调班日期是否是法定节假日
		AnnualVacation vacation = new AnnualVacation();
		List<Integer> typeList = new ArrayList<Integer>();
		typeList.add(AnnualVacation.YYPE_LEGAL);
		typeList.add(AnnualVacation.YYPE_VACATION);
		vacation.setTypeList(typeList);
		vacation.setStartDate(moveDate);
		vacation.setEndDate(moveDate);
		List<AnnualVacation> vacationList = annualVacationService.getListByCondition(vacation);
		
		//修改最终排班数据
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
				//排班日期包含法定节假日，生成值班数据
				if(vacationList!=null&&vacationList.size()>0){
					ClassSetting setting = classSettingMapper.getById(setId);
					EmployeeDuty duty = new EmployeeDuty();
					duty.setEmployId(employId);
					duty.setDutyDate(moveDate);
					duty.setDutyItem("法定节假日排班");
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
				//排班日期包含法定节假日，生成值班数据
				if(vacationList!=null&&vacationList.size()>0){
					EmployeeDuty duty = new EmployeeDuty();
					duty.setEmployId(employId);
					duty.setDutyDate(moveDate);
					duty.setDutyItem("法定节假日排班");
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
				//排班日期包含法定节假日，生成值班数据
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
					duty.setDutyItem("法定节假日排班");
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
		
		//重新计算考勤
		Date nowDate = DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_SHORT), DateUtils.FORMAT_SHORT);
		if(moveDate.getTime()<nowDate.getTime()){
			TransNormal transNormal = new TransNormal();
			transNormal.setEmployeeIds(String.valueOf(employId));
			transNormal.setStartTime(moveDate);
			transNormal.setEndTime(moveDate);
			transNormalService.recalculateAttnByCondition(transNormal);
		}
		
		result.put("message","调班成功！");
		result.put("success",true);
		
		Long time2 = System.currentTimeMillis();
		logger.info("排班申请hrMoveAtAdmin:use time="+(time2-time1));
		logger.info("排班申请hrMoveAtAdmin:end。。");
		
		return result;
	}

	@Override
	public Map<String, String> getSettingInfos(String date, Long setId,
			Long classId,Long empId) {
		Map<String,String> result = new HashMap<String,String>();
		result.put("date",DateUtils.format(DateUtils.parse(date, DateUtils.FORMAT_SHORT), DateUtils.FORMAT_SHORT_CN));//日期

		//result.put("date",date.substring(5,10));//日期
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
        Calendar cal = Calendar.getInstance(); // 获得一个日历
        Date datet = null;
        try {
            datet = f.parse(date);
            cal.setTime(datet);
        } catch (ParseException e) {
            logger.error("getSettingInfos转换日期失败",e);
        }
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0){
        	w = 0;
        }  
		result.put("weekDay",weekDays[w]);//星期
		
		if(empId==null && setId==null){
			return result;
		}else{
			if(empId==null){
				//原班次
				if(setId!=null&&setId.intValue()!=0){//0是虚拟的班次id，代表休息，不存在
					ClassSetting old = classSettingMapper.getById(setId);
					result.put("setName", old!=null?old.getName():"");
					result.put("startTime", old!=null?DateUtils.format(old.getStartTime(), DateUtils.FORMAT_HH_MM):"");
					result.put("endTime", old!=null?DateUtils.format(old.getEndTime(), DateUtils.FORMAT_HH_MM):"");
				}else if(setId!=null&&setId.intValue()==0){
					result.put("setName", "休息");
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
			logger.error("根据id="+id+"未查到申请数据");
			return null;
		}
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		
		List<Map<String,Object>> datas = applicationEmployeeClassMapper.getDetailById(id, Long.valueOf(String.valueOf(applyData.getIsMove())));
		
		//调班查出未调日期的班次信息
		if(applyData.getIsMove().intValue()==ApplicationEmployeeClass.IS_MOVE_1){
			//查询调班申请员工个数
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
		String fileName = departName+"日常工作排班表"+"-"+DateUtils.format(applyData.getClassMonth(), "MM")+"月";
		String excelName = fileName;
		HSSFWorkbook workbook = new HSSFWorkbook();
		
		HSSFSheet sheet = workbook.createSheet(excelName);
		if (result != null && result.size() > 0) {
			String yearMonth = (String)result.get(0).get("yearMonth");
			Map<String,String[]> titleMap = getEmpClassReportTitle(yearMonth);//动态生成表头
			String[] title2 = titleMap.get("t2");//第二行表头
			String[] title3 = titleMap.get("t3");//第三行表头
			// 表头标题样式
			HSSFCellStyle colstyle = workbook.createCellStyle();
			colstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
			colstyle.setBorderBottom(XSSFCellStyle.BORDER_THIN); //下边框
			colstyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);//左边框
			colstyle.setBorderTop(XSSFCellStyle.BORDER_THIN);//上边框
			colstyle.setBorderRight(XSSFCellStyle.BORDER_THIN);//右边框
			
			//建立第一行表头
			HSSFRow row = sheet.createRow((short) 0);
			//创建第一行表头内容
			ExcelUtil.createRow(row, 0, null, HSSFCell.CELL_TYPE_STRING,fileName);
			sheet.addMergedRegion(new CellRangeAddress(0,0,0,2));
			
			//建立第二行表头
			row = sheet.createRow((short) 1);
			//创建第二行表头内容
			for(int colIndex = 0; colIndex < title2.length; colIndex++){
				if(colIndex >=0 && colIndex <= 2){
					sheet.setColumnWidth(colIndex, 5000);//设置表格宽度
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
			
			//建立第三行表头(从第二行开始算，第一行表头是动态生成的数据)
			row = sheet.createRow((short) 2);
			//创建第三行表头内容
			for(int colIndex = 3; colIndex < title3.length; colIndex++){
				if(colIndex >=3 && colIndex <= title3.length - 2){
					sheet.setColumnWidth(colIndex, 1200);//设置表格宽度
				}else{
					sheet.setColumnWidth(colIndex, 5000);//设置表格宽度
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
				
				if(null != empId && empId.equals(data.get("empId"))){//是同一个员工记录
					mustAttnTime += data.get("mustAttnTime")!=null?(Double)data.get("mustAttnTime"):0;
					ExcelUtil.createRow(row, 34, null, HSSFCell.CELL_TYPE_NUMERIC,mustAttnTime);
					ExcelUtil.createRow(row, Integer.valueOf(classDay+"") + 2, null, HSSFCell.CELL_TYPE_STRING,data.get("name"));//排班工时
				}else{
					row = sheet.createRow((short) index);
					ExcelUtil.createRow(row, Integer.valueOf(classDay+"") + 2, null, HSSFCell.CELL_TYPE_STRING,data.get("name"));//排班工时
					
					Position position = positionService.getByEmpId((Long)data.get("empId"));
					ExcelUtil.createRow(row, 0, null, HSSFCell.CELL_TYPE_STRING,data.get("empName"));
					ExcelUtil.createRow(row, 1, null, HSSFCell.CELL_TYPE_STRING,null == position ? "" : position.getPositionName());
					ExcelUtil.createRow(row, 2, null, HSSFCell.CELL_TYPE_NUMERIC,data.get("shouldTime"));//应出勤工时
					ExcelUtil.createRow(row, 34, null, HSSFCell.CELL_TYPE_NUMERIC,(Double)data.get("mustAttnTime"));
					
					mustAttnTime = data.get("mustAttnTime")!=null?(Double)data.get("mustAttnTime"):0;
					empId = (Long)data.get("empId");
					index++;
				}
			}
		} else {
			ExcelUtil.createRow(sheet.createRow(1), 0, null,HSSFCell.CELL_TYPE_STRING, "无数据");
		}
		
		ClassSetting classSetting = new ClassSetting();
		List<ClassSetting> classSettingList = classSettingMapper.getListByCondition(classSetting);
		
		HSSFSheet sheet1 = workbook.createSheet("班次");
		//建立第一行表头
		HSSFRow row = sheet1.createRow((short) 0);
		//创建第一行表头内容
		ExcelUtil.createRow(row, 0, null, HSSFCell.CELL_TYPE_STRING,"班次名称");
		ExcelUtil.createRow(row, 1, null, HSSFCell.CELL_TYPE_STRING,"开始时间");
		ExcelUtil.createRow(row, 2, null, HSSFCell.CELL_TYPE_STRING,"结束时间");
		ExcelUtil.createRow(row, 3, null, HSSFCell.CELL_TYPE_STRING,"应出勤工时（小时）");
		
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
			result.put("message","请选择部门！");
			result.put("success",false);
			return result;
		}
		if(groupId==null){
			result.put("message","请选择组别！");
			result.put("success",false);
			return result;
		}
		//默认查询下个月的排班申请数据
		//判断下个月排班数据是否存在
		ApplicationEmployeeClass condition = new ApplicationEmployeeClass();
		condition.setClassMonth(DateUtils.getFirstDay(DateUtils.addMonth(new Date(),1)));
		condition.setDepartId(departId);
		condition.setGroupId(groupId);
		condition.setIsMove(condition.IS_MOVE_0);
		List<Integer> approvalStatusList = new ArrayList<Integer>();
		approvalStatusList.add(ConfigConstants.DOING_STATUS);//审批中
		approvalStatusList.add(ConfigConstants.PASS_STATUS);//通过
		approvalStatusList.add(ConfigConstants.OVERDUE_STATUS);//失效
		approvalStatusList.add(ConfigConstants.OVERDUEPASS_STATUS);//失效同意
		condition.setApprovalStatusList(approvalStatusList);
		List<ApplicationEmployeeClass> list = applicationEmployeeClassMapper.getByCondition(condition);
		if(list!=null&&list.size()>0){
			result.put("message","下个月排班已存在！");
			result.put("success",false);
			return result;
		}else{
			//部门
			Depart depart = departService.getById(departId);
			String title = depart.getName()+"日常工作排班表-"+DateUtils.format(DateUtils.addMonth(new Date(),1),"yyyy年MM月");
			result.put("title", title);
			//组别
			ScheduleGroup group = scheduleGroupMapper.getGroupById(groupId);
			result.put("groupName", group.getName());
			result.put("approvalStatus", "未提交");
			Date fristDay = DateUtils.getFirstDay(DateUtils.addMonth(new Date(),1));//次月第一天
			Date lastDay = DateUtils.getLastDay(DateUtils.addMonth(new Date(),1));//次月最后一天
			//查询排班日期内的所有节假日信息
			AnnualVacation vacation = new AnnualVacation();
			vacation.setStartDate(fristDay);
			vacation.setEndDate(lastDay);
			List<AnnualVacation> vacationList = annualVacationService.getListByCondition(vacation);
			Map<Date,Integer> vacationMap = new HashMap<Date,Integer>();
			for(AnnualVacation va:vacationList){
				vacationMap.put(va.getAnnualDate(), va.getType());
			}
			
			//表头，星期和天数
			List<String> weekDays = new ArrayList<String>();
			//表头，天数
			List<String> days = new ArrayList<String>();
			//表头，日期
			List<String> dates = new ArrayList<String>();
			weekDays.add("员工编号");
			weekDays.add("职位");
			weekDays.add("姓名");
			weekDays.add("应出勤工时");
			weekDays.add("排班天数");
			weekDays.add("排班工时");
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
			//查询该组别下面员工信息
			List<Map<String,Object>> datas = employeeMapper.getEmployeeMapByGroupId(groupId,condition.getClassMonth());
			//查询未提交的数据
			ApplicationEmployeeClass unCommit = applicationEmployeeClassMapper.getUnCommitData(condition);
			//组装没有修改过的数据
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
				List<Map<String,Object>> datas2 = applicationEmployeeClassMapper.getClassHoursMap(unCommit.getId());//统计的排班申请明细表
				for(Map<String,Object> data : datas){
					//组装每天的班次
					String employ_id = String.valueOf(data.get("employ_id"));
					for(Map<String,Object> data1 : datas1){
						String empId = String.valueOf(data1.get("empId"));
						if(employ_id.equals(empId)&&data1.get("classSettingId")!=null){
							data.put(String.valueOf(data1.get("classDay")), String.valueOf(data1.get("name"))+","+String.valueOf(data1.get("classDate"))+","+String.valueOf(data1.get("classSettingId"))+","+String.valueOf(data1.get("mustAttnTime")));
						}
					}
					//组装员工的排班天数，排班工时
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
				//组装没有修改过的数据
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
	    	if(annualVacationService.judgeWorkOrNot(startTime,vacationMap)){//判断是否是周末或者节假日
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
		
		logger.info("排班申请saveSchedule:start。。");
		Long time1 = System.currentTimeMillis();
			
		User user= userService.getCurrentUser();
		logger.info("排班申请saveSchedule入参:info="+info+";departId="+departId+";groupId="+groupId
			+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));

		Map<String, Object> result = new HashMap<String, Object>();
		if(departId==null){
			result.put("message","部门不能为空！");
			result.put("success",false);
			return result;
		}
		if(groupId==null){
			result.put("message","组别不能为空！");
			result.put("success",false);
			return result;
		}
		
		//查询该部门下个月是否有未提交审核的排班申请数据
		ApplicationEmployeeClass condition = new ApplicationEmployeeClass();
		condition.setClassMonth(DateUtils.getFirstDay(DateUtils.addMonth(new Date(),1)));
		condition.setDepartId(departId);
		condition.setGroupId(groupId);
		condition.setIsMove(condition.IS_MOVE_0);
		ApplicationEmployeeClass unCommit = applicationEmployeeClassMapper.getUnCommitData(condition);
		JSONObject array = JSONObject.fromObject(info);
		Map<String, String> map = (Map)array;//排班详细信息
		Date fristDay = DateUtils.getFirstDay(DateUtils.addMonth(new Date(),1));//次月第一天
		Date lastDay = DateUtils.getLastDay(DateUtils.addMonth(new Date(),1));//次月最后一天
		//查询排班日期内的所有节假日信息
		AnnualVacation vacation = new AnnualVacation();
		vacation.setStartDate(fristDay);
		vacation.setEndDate(lastDay);
		List<AnnualVacation> vacationList = annualVacationService.getListByCondition(vacation);
		Map<Date,Integer> vacationMap = new HashMap<Date,Integer>();
		for(AnnualVacation va:vacationList){
			vacationMap.put(va.getAnnualDate(), va.getType());
		}
		//应出勤集合
		Map<String,Double> shouldTimeMap = new HashMap<String,Double>();
		//员工名称集合
		Map<String,String> nameMap = new HashMap<String,String>();
		if(unCommit!=null){
			//直接修改申请明细数据
            for (Map.Entry<String, String> detail: map.entrySet()) {
            	//查询明细是否存在数据
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
    						logger.error("员工id="+employId+"未查到用户信息。");
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
             //查询已排班人数
			List<ApplicationEmployeeClassDetail> classEmployeeNum = applicationEmployeeClassDetailMapper.getclassEmployeeNum(unCommit.getId());
			//已排班size就不能重复计算
			ApplicationEmployeeClass employeeClass = new ApplicationEmployeeClass();
			employeeClass.setUpdateTime(new Date());
			employeeClass.setUpdateUser(user.getEmployee().getCnName());
			employeeClass.setClassEmployeeNum((classEmployeeNum!=null&&classEmployeeNum.size()>0)?classEmployeeNum.size():0);
			employeeClass.setId(unCommit.getId());
			applicationEmployeeClassMapper.updateById(employeeClass);
		}else{
			//查询该组别下面员工信息
			List<Map<String,Object>> datas = employeeMapper.getEmployeeMapByGroupId(groupId,condition.getClassMonth());
			Map<String,String> moveNumMap = new HashMap<String,String>();
			//直接新增申请与申请明细
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
						logger.error("员工id="+employId+"未查到用户信息。");
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
			//保存排班申请明细
			if(applicationEmployeeClassList!=null&&applicationEmployeeClassList.size()>0){
				applicationEmployeeClassDetailMapper.batchSave(applicationEmployeeClassList);
			}
		}
		result.put("success",true);
		result.put("message","保存成功！");
		
		Long time2 = System.currentTimeMillis();
		logger.info("排班申请saveSchedule:use time="+(time2-time1));
		logger.info("排班申请saveSchedule:end。。");
		
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> commitSchedule(HttpServletRequest request,Long departId, Long groupId) throws OaException{
		
		logger.info("排班申请commitSchedule:start。。");
		Long time1 = System.currentTimeMillis();
			
		User user= userService.getCurrentUser();
		logger.info("排班申请commitSchedule入参:departId="+departId+";groupId="+groupId
			+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));
		
		Map<String, Object> result = new HashMap<String, Object>();
		if(departId==null){
			result.put("message","部门不能为空！");
			result.put("success",false);
			return result;
		}
		if(groupId==null){
			result.put("message","组别不能为空！");
			result.put("success",false);
			return result;
		}
		//查询该部门下个月是否有未提交审核的排班申请数据
		ApplicationEmployeeClass condition = new ApplicationEmployeeClass();
		condition.setClassMonth(DateUtils.getFirstDay(DateUtils.addMonth(new Date(),1)));
		condition.setDepartId(departId);
		condition.setGroupId(groupId);
		condition.setIsMove(condition.IS_MOVE_0);
		ApplicationEmployeeClass unCommit = applicationEmployeeClassMapper.getUnCommitData(condition);
		if(unCommit!=null){

			//查询该部门的排班人和审核人
			ScheduleGroup schedulerList = scheduleGroupMapper.getGroupById(groupId);
			
			if(!"107".equals(user.getDepart().getCode())){
				//节假日前四个工作日
				Config configCondition = new Config();
				configCondition.setCode("dutyCommitTimeLimit");
				List<Config> limit = configService.getListByCondition(configCondition);
				int num = Integer.valueOf(limit.get(0).getDisplayCode());
				
				Date lastSubDate = annualVacationService.getWorkingDayPre(num,DateUtils.addDay(unCommit.getClassMonth(), -1));
				if(DateUtils.getIntervalDays(new Date(),lastSubDate)<0){
					result.put("success", false);
					result.put("message","排班申请需提前4个工作日提交！");
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
				result.put("message", "排班表中有员工应出勤工时未排满，请确认排满后提交！");
				return result;
			}
			
			
			Long auditor = schedulerList.getAuditor();//排班审核人
			
			//指定下一步审批人
			Map<String, Object> variables = new HashMap<>();
			variables.put("approver",auditor+"");
			//启动流程，默认提交
			ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(ConfigConstants.SCHEDULING_KEY,variables);
			
			//修改排班状态
			unCommit.setProcessInstanceId(processInstance.getId());
			unCommit.setApprovalStatus(ConfigConstants.DOING_STATUS);
			unCommit.setUpdateUser(user.getEmployee().getCnName());
			unCommit.setUpdateTime(new Date());
			applicationEmployeeClassMapper.updateById(unCommit);
			
			//-----------------start-----------------------保存流程节点信息
			ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
			flow.setAssigneeName(user.getEmployee().getCnName());
			flow.setDepartName(user.getDepart().getName());
			flow.setPositionName(user.getPosition().getPositionName());
			flow.setFinishTime(new Date());
			flow.setComment("排班申请");
			flow.setProcessId(processInstance.getId());
			flow.setProcessKey(ConfigConstants.SCHEDULING_KEY);
			flow.setStatu(0);
			viewTaskInfoService.save(flow);
			
			//判断排班人和下个节点是否是一个操作人，是的话，直接启动下一个流程
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
				//指定下一步审批人
				Map<String, Object> variables1 = new HashMap<>();
				variables1.put("isContainLegal",containLeagl);
				Task task1 = taskService.createTaskQuery().taskId(task.getId()).list().get(0);
		    	taskService.addComment(task1.getId(), task1.getProcessInstanceId(), "排班申请");
		    	//-----------------start-----------------------保存流程节点信息
				ViewTaskInfoTbl flow1 = new ViewTaskInfoTbl();
				flow1.setAssigneeName(user.getEmployee().getCnName());
				flow1.setDepartName(user.getDepart().getName());
				flow1.setPositionName(user.getPosition().getPositionName());
				flow1.setFinishTime(new Date());
				flow1.setComment("排班申请");
				flow1.setProcessId(processInstance.getId());
				flow1.setProcessKey(ConfigConstants.SCHEDULING_KEY);
				flow1.setStatu(100);
				viewTaskInfoService.save(flow1);
		    	taskService.complete(task1.getId(), variables1);
			}
			result.put("success", true);
			result.put("message", "已提交，审核通过后生效!");
			
		}else{
			result.put("success", false);     
			result.put("message", "没有可提交的排班数据！");      
		}
		
		Long time2 = System.currentTimeMillis();
		logger.info("排班申请commitSchedule:use time="+(time2-time1));
		logger.info("排班申请commitSchedule:end。。");
		
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> changeClassSet(HttpServletRequest request,String month, Long departId,
			Long groupId, String info) throws OaException {
		
		logger.info("调班申请changeClassSet start。。");
		Long time1 = System.currentTimeMillis();
		
		User user= userService.getCurrentUser();
		logger.info("调班申请changeClassSet入参:month="+month+";departId="+departId+";groupId="+groupId
				+";info="+info+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));

		Map<String, Object> result = new HashMap<String, Object>();
		if(month==null||"".equals(month)){
			result.put("message","月份不能为空！");
			result.put("success",false);
			return result;
		}
		if(departId==null){
			result.put("message","部门不能为空！");
			result.put("success",false);
			return result;
		}
		if(groupId==null){
			result.put("message","组别不能为空！");
			result.put("success",false);
			return result;
		}
		if("{}".equals(info)){
			result.put("message","调班信息为空！");
			result.put("success",false);
			return result;
		}
		boolean containLeagl = false;//调班是否包含法定假日
		
		//查询该部门的排班人和审核人
		ScheduleGroup schedulerList = scheduleGroupMapper.getGroupById(groupId);
		
		JSONObject infoObj = JSONObject.fromObject(info);
		Map<String, String> map = (Map)infoObj;//调班详细信息
		
		Date fristDay = DateUtils.parse(month+"-01", DateUtils.FORMAT_SHORT);//第一天
		Date lastDay = DateUtils.getLastDay(DateUtils.parse(month+"-01", DateUtils.FORMAT_SHORT));//最后一天
		//查询排班日期内的所有节假日信息
		AnnualVacation vacation = new AnnualVacation();
		vacation.setStartDate(fristDay);
		vacation.setEndDate(lastDay);
		List<AnnualVacation> vacationList = annualVacationService.getListByCondition(vacation);
		Map<Date,Integer> vacationMap = new HashMap<Date,Integer>();
		Map<Date,Date> legalMap = new HashMap<Date,Date>();//法定节假日集合
		for(AnnualVacation va:vacationList){
			vacationMap.put(va.getAnnualDate(), va.getType());
			if(va.getType().intValue()==AnnualVacation.YYPE_LEGAL.intValue()){
				legalMap.put(va.getAnnualDate(), va.getAnnualDate());
			}
		}
		//应出勤集合
		Map<String,Double> shouldTimeMap = new HashMap<String,Double>();
		//员工名称集合
		Map<String,String> nameMap = new HashMap<String,String>();
		List<ApplicationEmployeeClassDetail> applicationEmployeeClassList = new ArrayList<ApplicationEmployeeClassDetail>();//明细集合
		Map<String,String> moveNumMap = new HashMap<String,String>();
		for (Map.Entry<String,String> detail: map.entrySet()) {
			String employId = detail.getKey().split("_")[0];
			if(!(moveNumMap!=null&&moveNumMap.containsKey(employId))){
				moveNumMap.put(employId, employId);
			}
			String employName = "";
			double shouldTime = 0d;
			String classSettingId = (detail.getValue()!=null&&!"".equals(detail.getValue())&&!"undefined".equals(detail.getValue()))?detail.getValue():null;
			
			//调班包含法定节假日，需提前四天
			if(legalMap!=null&&legalMap.containsKey(DateUtils.parse(detail.getKey().split("_")[1], DateUtils.FORMAT_SHORT))){
				
				//节假日前四个工作日
				Config configCondition = new Config();
				configCondition.setCode("dutyCommitTimeLimit");
				List<Config> limit = configService.getListByCondition(configCondition);
				int num = Integer.valueOf(limit.get(0).getDisplayCode());
				
				Date lastSubDate = annualVacationService.getWorkingDayPre(num,DateUtils.parse(detail.getKey().split("_")[1], DateUtils.FORMAT_SHORT));
				if(DateUtils.getIntervalDays(new Date(),lastSubDate)<0){
					result.put("success",false);
					result.put("message","值前4个工作日提交！");
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
					logger.error("员工id="+employId+"未查到用户信息。");
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
		
		Long auditor = schedulerList.getAuditor();//排班审核人
		//启动流程
		Map<String, Object> variables = new HashMap<>();
		variables.put("approver",auditor+"");
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("scheduling",variables);
		
		//-----------------start-----------------------保存流程节点信息
		ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
		flow.setAssigneeName(user.getEmployee().getCnName());
		flow.setDepartName(user.getDepart().getName());
		flow.setPositionName(user.getPosition().getPositionName());
		flow.setFinishTime(new Date());
		flow.setComment("排班申请");
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
		applicationEmployeeClass.setMoveType(0);//调班申请
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
	    	taskService.addComment(task1.getId(), task1.getProcessInstanceId(), "排班申请");
	    	
	    	//-----------------start-----------------------保存流程节点信息
			ViewTaskInfoTbl flow1 = new ViewTaskInfoTbl();
			flow1.setAssigneeName(user.getEmployee().getCnName());
			flow1.setDepartName(user.getDepart().getName());
			flow1.setPositionName(user.getPosition().getPositionName());
			flow1.setFinishTime(new Date());
			flow1.setComment("排班申请");
			flow1.setProcessId(processInstance.getId());
			flow1.setProcessKey(ConfigConstants.SCHEDULING_KEY);
			flow1.setStatu(100);
			viewTaskInfoService.save(flow1);
			
	    	taskService.complete(task1.getId(), variables1);
	    }
		result.put("sucess", true);
		result.put("message","调班申请成功！");
		
		Long time2 = System.currentTimeMillis();
		logger.info("调班申请changeClassSet:use time="+(time2-time1));
		logger.info("调班申请changeClassSet:end。。");
		
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
		//查询当前登录人未排班人或者审核人的排班组别
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
		approvalStatusList.add(ConfigConstants.DOING_STATUS);//审批中
		approvalStatusList.add(ConfigConstants.PASS_STATUS);//通过
		approvalStatusList.add(ConfigConstants.REFUSE_STATUS);//拒绝
		approvalStatusList.add(ConfigConstants.BACK_STATUS);//撤销
		approvalStatusList.add(ConfigConstants.OVERDUE_STATUS);//失效
		approvalStatusList.add(ConfigConstants.OVERDUEPASS_STATUS);//失效同意
		approvalStatusList.add(ConfigConstants.OVERDUEREFUSE_STATUS);//失效拒绝
		condition.setApprovalStatusList(approvalStatusList);
		List<ApplicationEmployeeClass> list = getByCondition(condition);
		Map<Long,String> isExistGroupName = new HashMap<Long,String>();//是否存在组别名称
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
