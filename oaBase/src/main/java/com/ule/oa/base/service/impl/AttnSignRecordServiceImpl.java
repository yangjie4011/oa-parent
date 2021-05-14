package com.ule.oa.base.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ule.arch.basehttpclient.BaseHttpClientFactory;
import com.ule.arch.basehttpclient.client.BaseHttpClient;
import com.ule.arch.basehttpclient.model.HttpRequest;
import com.ule.arch.basehttpclient.model.HttpResponse;
import com.ule.oa.base.mapper.AttnSignRecordMapper;
import com.ule.oa.base.mapper.EmpPositionMapper;
import com.ule.oa.base.mapper.EmployeeBaseInfoMapper;
import com.ule.oa.base.mapper.EmployeeClassMapper;
import com.ule.oa.base.mapper.EmployeeMapper;
import com.ule.oa.base.po.AnnualVacation;
import com.ule.oa.base.po.AttnSignRecord;
import com.ule.oa.base.po.CompanyConfig;
import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.EmpApplicationLeave;
import com.ule.oa.base.po.EmpMsg;
import com.ule.oa.base.po.EmpPosition;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeBaseInfoDTO;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.po.SendMail;
import com.ule.oa.base.po.TransNormal;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.AnnualVacationService;
import com.ule.oa.base.service.AttnSignRecordService;
import com.ule.oa.base.service.CompanyConfigService;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.EmpApplicationLeaveService;
import com.ule.oa.base.service.EmpApplicationRegisterService;
import com.ule.oa.base.service.EmpMsgService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.SendMailService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.util.SendMailUtil;
import com.ule.oa.common.po.CommonPo;
import com.ule.oa.common.utils.CalDistanceUtil;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;

/**
  * @ClassName: AttnSignRecordServiceImpl
  * @Description: 员工考勤记录业务层
  * @author 张金涛
  * @date 2017年10月6日 上午10:15:50
 */
@Service
public class AttnSignRecordServiceImpl implements AttnSignRecordService{
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private AttnSignRecordMapper attnSignRecordMapper;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private CompanyConfigService companyConfigService;
	@Autowired
	private EmpMsgService empMsgService;
	@Autowired
	private ConfigService configService;
	@Autowired
	private AnnualVacationService annualVacationService;
	@Autowired
	private EmpApplicationLeaveService empApplicationLeaveService;
	@Autowired
	private EmpPositionMapper empPositionMapper;
	@Autowired
	private EmployeeMapper employeeMapper;
	@Autowired
	private EmpApplicationRegisterService empApplicationRegisterService;
	@Autowired
	private SendMailService sendMailService;
	@Autowired
	private EmployeeClassMapper employeeClassMapper;
	@Autowired
	private UserService userService;
	@Autowired
	private EmployeeBaseInfoMapper employeeBaseInfoMapper;
	
	private static BaseHttpClient client = BaseHttpClientFactory.getClient();
	
	@Override
	public Integer getMaxAttnId() {
		return attnSignRecordMapper.getMaxAttnId(DateUtils.addMonth(new Date(), -1));
	}


	@Override
	public Integer saveTransToAttnSignBatch(List<TransNormal> list, Long uleId, Date currentTime, String createUser) {
		return attnSignRecordMapper.saveTransToAttnSignBatch(list,uleId,currentTime,createUser);
	}

	@Override
	public List<AttnSignRecord> getSignRecordList(AttnSignRecord condition) {
		return attnSignRecordMapper.getSignRecordList(condition);
	}


	@Override
	public List<AttnSignRecord> getListBefore9(AttnSignRecord condition) {
		return attnSignRecordMapper.getListBefore9(condition);
	}

	@Override
	public Integer save(AttnSignRecord signRecord) {
		return attnSignRecordMapper.insert(signRecord);
	}


	@Override
	public List<Map<String, Object>> getEmpSignRecordReport(Date startTime,
			Date endTime, Long departId,List<Long> empTypeIdList) {
		return attnSignRecordMapper.getEmpSignRecordReport( startTime,
				 endTime,  departId,empTypeIdList);
	}
	
	/**
	 * @throws Exception 
	  * signRecordExMsgRemind(员工考勤异常消息提醒-排班员工不提醒)
	  * @Title: signRecordExMsgRemind
	  * @Description: 员工考勤异常消息提醒-排班员工不提醒
	  * void    返回类型
	  * @throws
	 */
	public void signRecordExMsgRemind(){
		long startTime = System.currentTimeMillis();
		logger.info("忘打卡和晚到提醒定时start ... ...");
		
		try{
			//1.判断今天是否需要打卡
			logger.info("[step01]判断今天是否需要打卡 ... ...");
			Integer dateType = annualVacationService.getDayType(new Date());
			if(null != dateType && dateType != 1){//非工作日
				long endTime = System.currentTimeMillis();
				logger.info("{}是非工作日",DateUtils.format(new Date(),DateUtils.FORMAT_SHORT_CN));
				logger.info("忘打卡和晚到提醒定时end,cost={}",endTime - startTime);
				return;
			}
			
			//2	获取综合工时的配置
			logger.info("[step02]获取综合工时的配置 ... ...");
			CompanyConfig companyConfig = new CompanyConfig();
			companyConfig.setCode(CompanyConfig.TYPE_OF_WORK);
			companyConfig.setDisplayCode(CompanyConfig.TYPE_OF_WORK_ZH);
			CompanyConfig zhConf = companyConfigService.getByCondition(companyConfig);
			
			//3 获取标准工时的配置
			logger.info("[step03]获取标准工时的配置 ... ...");
			companyConfig.setDisplayCode(CompanyConfig.TYPE_OF_WORK_BZ);
			CompanyConfig bzConf = companyConfigService.getByCondition(companyConfig);
			
			//4 获取排班的配置
			logger.info("[step04]获取排班的配置 ... ...");
			Config config = new Config();
			config.setCode(Config.CLASS_TYPE);
			config.setDisplayCode(Config.CLASS_TYPE_YES);
			Config yConf = configService.getByCondition(config);
			
			//5	获取所有综合工时员工
			logger.info("[step05]获取所有综合工时员工 ... ...");
			Employee emp = new Employee();
			emp.setJobStatus(ConfigConstants.IS_ACTIVE_INTEGER);
			emp.setDelFlag(ConfigConstants.IS_NO_INTEGER);
			//只通知指定员工类型员工+工作地点是上海的员工
			emp.setEmpTypeIdList(configService.getNeedEmpTypeIdList());
			emp.setWorkAddressType(0);
			
			List<Employee> emps = employeeService.getListByCondition(emp);
			
			//6.获取不用提醒人群员工编号
			logger.info("[step06]获取不用提醒人群员工编号 ... ...");
			CompanyConfig msgConfig = new CompanyConfig();
			msgConfig.setCode(CompanyConfig.SIGN_RECORD_EX_MSG_REMAIND_EXCEPT);
			List<CompanyConfig> msgList = companyConfigService.getListByCondition(msgConfig);
			Map<String,String> msgMap = msgList.stream().collect(Collectors.toMap(CompanyConfig :: getDisplayCode, CompanyConfig :: getDisplayCode));
			
			//7	判断员工最早打卡时间是否早于9点，如有异常则消息提醒
			for(Employee employee : emps){
				try{
					attnSignExMind(employee, msgMap, zhConf, bzConf, yConf);
				}catch(Exception e){
					logger.error("员工{}生成忘打卡和晚到提醒出错,msg={}",employee.getCnName(),e.getMessage());
				}
			}
		}catch(Exception e){
			logger.error("忘打卡和晚到提醒定时异常!!!"+e.getMessage());
		}
		
		long endTime = System.currentTimeMillis();
		logger.info("忘打卡和晚到提醒定时end,cost={}",endTime - startTime);
	}
	
	//@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void attnSignExMind(Employee employee,Map<String,String> msgMap,CompanyConfig zhConf,CompanyConfig bzConf,Config yConf) throws Exception{
		logger.info("[step07]判断员工是否需要发送晚到或忘打卡提醒[排班人员不发送提醒] ... ...");
		if(!msgMap.isEmpty() && StringUtils.isNotEmpty(employee.getCode()) 
				&& StringUtils.isNotBlank(msgMap.get(employee.getCode())) 
				&& msgMap.get(employee.getCode()).equals(employee.getCode())){
			logger.info("员工{}不用发送晚到或忘打卡提醒",employee.getCnName());
			return;
		}
		
		Long workType = employee.getWorkType();//工时类型
		Long classType = employee.getWhetherScheduling();//是否排班
		if(null == workType){
			logger.error("员工{}工时类型为空,无法判断是否晚到",employee.getCnName());
			return;
		}else{
			if(workType.equals(bzConf.getId()) && null != classType && classType.equals(yConf.getId())){
				logger.error("员工{}为排班员工，不用判断是否晚到",employee.getCnName());
				return;
			}
		}
		
		logger.info("[step08]查询员工{}打卡记录 ... ...",employee.getCnName());
		String attnTime = DateUtils.format(new Date(),DateUtils.FORMAT_SHORT);
		AttnSignRecord attnSignRecord = new AttnSignRecord();
		attnSignRecord.setEmployeeId(employee.getId());
		attnSignRecord.setStartTime(DateUtils.parse(attnTime + " 00:00:00"));
		attnSignRecord.setEndTime(DateUtils.parse(attnTime + " 23:59:59"));
		
		AttnSignRecord signRecord = attnSignRecordMapper.getFirstSignRecordByCondition(attnSignRecord);
		if(null != signRecord){//判断是否晚到
			Date signTime = signRecord.getSignTime();//实际打卡时间
			Date workStartTime = workType.equals(zhConf.getId()) ? DateUtils.parse(attnTime + " 09:01:00") : DateUtils.parse(attnTime + " 09:06:00");//上班开始时间
			if(signTime.compareTo(workStartTime) >= 0){
				long seconds = DateUtils.getIntervalSeconds(DateUtils.parse(attnTime + " 09:00:00") , signTime);//晚到时间(精确到秒)
				long hours = seconds / 3600;
				long minutes = 0;
				if(seconds % 3600 > 0){
//						minutes = seconds % 3600 % 60 == 0 ? seconds % 3600 / 60 : seconds % 3600 / 60 + 1;//晚到分钟数(不满一分钟按一分钟计算)
					minutes = seconds % 3600 / 60 ;//晚到分钟数(不满一分钟按0分钟计算)
				}
				String content = "您今天晚到";
				if(hours > 0 ){
					content += hours + "小时";
				}
				if(minutes > 0){
					content += minutes + "分钟";
				}
				content += ",请注意上班时间!!!";
				
				logger.info("[step09]员工{}生成晚到消息提醒开始...",employee.getCnName());
				//生成晚到消息提醒
				EmpMsg empMsg = new EmpMsg();
				empMsg.setDelFlag(CommonPo.STATUS_NORMAL);
				empMsg.setType(EmpMsg.type_200);
				empMsg.setCompanyId(employee.getCompanyId());
				empMsg.setEmployeeId(employee.getId());
				empMsg.setTitle("晚到提醒");
				empMsg.setContent(content);
				empMsg.setReadFlag(EmpMsg.READ_FLAG_NO);
				empMsg.setCreateUser(ConfigConstants.API);
				empMsg.setCreateTime(new Date());
				empMsgService.save(empMsg);
				logger.info("[step09]员工{}生成晚到消息提醒结束!!!",employee.getCnName());
			}
		}else{//忘打卡
			//判断用户有没有请假
			EmpApplicationLeave applyLeave = new EmpApplicationLeave();
			applyLeave.setApprovalStatus(EmpApplicationLeave.APPROVAL_STATUS_YES);
			applyLeave.setEmployeeId(employee.getId());
			applyLeave.setApplyDate(new Date());
			List<EmpApplicationLeave> applyLeaveList = empApplicationLeaveService.getListByCondition(applyLeave);
			
			//当天员工请假不提醒忘记打卡
			if(null != applyLeaveList && applyLeaveList.size() > 0){
				logger.error("员工{}今日11点前未打卡",employee.getCnName());
				return;
			}
			
			//生成晚到消息提醒
			logger.info("[step09]员工{}生成忘打卡消息提醒开始...",employee.getCnName());
			EmpMsg empMsg = new EmpMsg();
			empMsg.setDelFlag(CommonPo.STATUS_NORMAL);
			empMsg.setType(EmpMsg.type_200);
			empMsg.setCompanyId(employee.getCompanyId());
			empMsg.setEmployeeId(employee.getId());
			empMsg.setTitle("忘打卡提醒");
			empMsg.setContent("您今日11点前未打卡,如忘记打卡请尽早补打卡!!!");
			empMsg.setReadFlag(EmpMsg.READ_FLAG_NO);
			empMsg.setCreateUser(ConfigConstants.API);
			empMsg.setCreateTime(new Date());
			empMsgService.save(empMsg);
			logger.info("[step09]员工{}生成忘打卡消息提醒结束!!!",employee.getCnName());
			
			//发送异常考勤邮件
			String content = "Dear " + employee.getCnName() +":<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;您今日11点前未打卡,如忘记打卡请尽早补打卡!!!";
			
			try {
				SendMailUtil.sendNormalMail(content, employee.getEmail(), employee.getCnName(), "忘打卡提醒");
				
				logger.info("[step10]员工{}生成忘打卡邮件提醒",employee.getCnName());
			} catch (Exception e) {
				logger.error("员工{}发送忘打卡提醒邮件失败",employee.getCnName());
			}
		}
	}
	//旷工提醒操作

		@Override
		public void absenteeismAlert(){
			// TODO Auto-generated method stub
			/** 连续三个工作日旷工（全天旷工），第四个工作日发送旷工邮件提醒，发送对象HR全组 
			 *  白名单：邮乐公司（部门负责人、司机、秘书、杨总、瞿总
				外出、任何请假，异常考勤(提的申请的)  出差(提的申请的 四个都是） 过滤掉
			 *  法定节假日、周末不算里面
			 * */
			//1.获取当前系统时间 排查假日
			//2.白名单、条件过滤掉员工
			//3.逻辑计算未打卡超过3天的 员工  (当前时间 逻辑计算前面三天是否未打卡)
			//4.发送邮件
			//String attnTime = DateUtils.format(new Date(),DateUtils.FORMAT_SHORT);
			logger.info("旷工提醒定时start ... ...");
			//1.判断今天是否需要打卡
			logger.info("[step01]判断今天是否需要打卡 ... ...");
			Integer dateType = annualVacationService.getDayType(new Date());
 			if(null != dateType && dateType != 1){//非工作日
				logger.info("{}是非工作日",DateUtils.format(new Date(),DateUtils.FORMAT_SHORT_CN));
				return;
			}
 			boolean isClassEmp=false;
 			Long classId = null;//排班id
 			//判断是否排班
 			CompanyConfig companyConfigConditon = new CompanyConfig();//查询配置表条件
			companyConfigConditon.setCode("typeOfWork");//工时类型
			List<CompanyConfig> workTypeList = companyConfigService.getListByCondition(companyConfigConditon);
			
			Config configCondition = new Config();
			configCondition.setCode("whetherScheduling");//是否排班
			List<Config> whetherSchedulingList = configService.getListByCondition(configCondition);
			for (Config config : whetherSchedulingList) {
				if(config.getDisplayCode().equals("yes")){
					classId = config.getId();
					break;
				}
			}
			
			//查询所有节假日信息
			List<AnnualVacation> vacationList = annualVacationService.getListByCondition(null);
			Map<Date,Integer> vacationMap = new HashMap<Date,Integer>();
			for(AnnualVacation va:vacationList){
				vacationMap.put(va.getAnnualDate(), va.getType());
			}
 			
			//按工作日往前推三天
			Date WorkDateStart=null;
			Date WorkDateEnd=null;
			try {			
				WorkDateStart = annualVacationService.isBackWorkDateOnSystemDate(DateUtils.addDay(new Date(), -1),3,vacationMap);	
				WorkDateEnd = annualVacationService.isBackWorkDateOnSystemDate(DateUtils.addDay(new Date(), -1),1,vacationMap);
			} catch (Exception e1) {
				
			}
			String WorkDateFormatStart  = DateUtils.format(WorkDateStart,DateUtils.FORMAT_SHORT);
			String WorkDateFormatEnd  = DateUtils.format(WorkDateEnd,DateUtils.FORMAT_SHORT);
			logger.info("WorkDateFormatStart="+WorkDateFormatStart+";WorkDateFormatEnd="+WorkDateFormatEnd);
			
			String toDay = DateUtils.format(WorkDateEnd,DateUtils.FORMAT_SHORT);
			Date toDaydate = DateUtils.parse(toDay + " 00:00:00");
			//1.1	获取所有综合工时员工
			logger.info("[step02]获取所有综合工时员工 ... ...");
			Employee emp = new Employee();
			emp.setCompanyId(1L);
			List<Long> JobStatusList=new ArrayList<Long>();
			JobStatusList.add(0L);//在职
			JobStatusList.add(2L);//待离职
			emp.setJobStatusList(JobStatusList);
			emp.setDelFlag(ConfigConstants.IS_NO_INTEGER);
			emp.setWorkAddressType(0);//本地员工
			logger.info("[step03]开始过滤白名单，请假、异常的员工... ...");
			//添加白名单  过滤的一些员工
			emp.setAbsenteeismids(whiteList());
			//从未打过卡的员工 拼接
			String entryEmpStr=new String();
			//从未打卡的员工 flag
			boolean unAttnFlag=false;
			
			//判断员工有没有打过卡
			List<AttnSignRecord> signRecordList = null;
			//只通知指定员工类型员工
			emp.setEmpTypeIdList(configService.getNeedEmpTypeIdList());
		
			List<Employee> emps = employeeService.getListByCondition(emp);
			List<Long> absenteeismIds=new ArrayList<Long>();
			List<AttnSignRecord> absenteeismIdsAndLastDay=new ArrayList<AttnSignRecord>();
			logger.info("[step04]开始查看是否有旷工三日的员工... ...");	
			//判断 是否为周末 周1 时间改成四天，周二改成五天 未打卡 法定节假日 往前推+2天
			for (Employee employees : emps) {
				AttnSignRecord attnSignRecord = new AttnSignRecord();
				//所有正常要查的员工，看是否三天有异常
				attnSignRecord.setEmployeeId(employees.getId());
				//如果是星期一，星期二就要把时间弄成五天  		
				attnSignRecord.setStartTime(DateUtils.parse(WorkDateFormatStart + " 00:00:00"));
				attnSignRecord.setEndTime(DateUtils.parse(WorkDateFormatEnd + " 23:59:59"));
				List<Long> attnThirdAbsenteeismAlertEmp = attnSignRecordMapper.getAttnThirdAbsenteeismAlertEmp(attnSignRecord);
				//如果signRecord 为空 代表 这三天都没有打卡 视为旷工
				if(attnThirdAbsenteeismAlertEmp.size()==0){				
					String WorkDateFormatStartTemple=null;
					//获取员工最后一次打卡
					AttnSignRecord attnLastDayAbsenteeismAlertEmpTemple =null;
					attnLastDayAbsenteeismAlertEmpTemple = attnSignRecordMapper.getAttnLastDayAbsenteeismAlertEmp(attnSignRecord);				
					
					//新员工入职时间 
					Employee entryEmp=null;
					if(attnLastDayAbsenteeismAlertEmpTemple==null){
						entryEmp = employeeService.getById(employees.getId());
						AttnSignRecord condition=new AttnSignRecord();
						condition.setEmployeeId(employees.getId());
						signRecordList= attnSignRecordMapper.getSignRecordList(condition);
						if(signRecordList.size()!=0){
							attnLastDayAbsenteeismAlertEmpTemple=new AttnSignRecord();
							//如果 员工都是打卡异常 且有打过卡 取员工 开始加入MO系统时间 为打卡时间
							Date createTime = entryEmp.getCreateTime();
							
							Date empCreateDate = DateUtils.parse(DateUtils.format(createTime, DateUtils.FORMAT_SHORT), DateUtils.FORMAT_SHORT);
							attnLastDayAbsenteeismAlertEmpTemple.setAlertStartTime(empCreateDate);
							attnLastDayAbsenteeismAlertEmpTemple.setEmployeeId(employees.getId());
						}
						
					}
					
					if(attnLastDayAbsenteeismAlertEmpTemple==null && signRecordList.size()==0){
						
						Integer intervalDays = DateUtils.getIntervalDays(entryEmp.getFirstEntryTime(), toDaydate);
						Map<String, String> empInfo = employeeMapper.queryEmpBaseInfoById(employees.getId());
						if(intervalDays>=4){
							entryEmpStr+="旷工三日以上，从未在公司打过卡的员工编号 ："+empInfo.get("code")+", 部门：" +empInfo.get("departName")+"， 姓名："+employees.getCnName() +" <br/>";
							logger.info("旷工三日以上，从未在公司打过卡的员工编号 ："+empInfo.get("code") +", 部门：" +empInfo.get("departName")+"， 姓名："+employees.getCnName() +" <br/>");							
							unAttnFlag=true;
						}
					}else{
						//查询请假、出差、那些白名单的员工  在旷工时间段中 取出最后的 工作流 时间 在往前面推 时间 计算出此员工 旷工多少天				
						AttnSignRecord queryAlertMax = attnSignRecordMapper.queryAlertMaxTime(attnLastDayAbsenteeismAlertEmpTemple.getAlertStartTime(), attnLastDayAbsenteeismAlertEmpTemple.getEmployeeId());			
						if(queryAlertMax!=null){
							int compareDate = DateUtils.compareDate(queryAlertMax.getEndTime(), toDaydate);
							Date queryAlertMaxTime=null;
							if(compareDate==1){
								queryAlertMaxTime=DateUtils.parse(WorkDateFormatEnd + " 00:00:00");
								WorkDateFormatStartTemple  = DateUtils.format(queryAlertMaxTime);
							}else{
								queryAlertMaxTime=queryAlertMax.getEndTime();
								WorkDateFormatStartTemple  = DateUtils.format( DateUtils.addDay(queryAlertMaxTime, 1),DateUtils.FORMAT_SHORT);
							}
							
						}else{
							WorkDateFormatStartTemple  = DateUtils.format( DateUtils.addDay(attnLastDayAbsenteeismAlertEmpTemple.getAlertStartTime(), 1),DateUtils.FORMAT_SHORT);					
						}
					//此员工属于排班员工	
					if(employees.getWhetherScheduling().equals(classId)){
						//近三个工作日	如果在最后请假期间则 不进入排班判断
						if(queryAlertMax!=null&&DateUtils.compareDate(attnSignRecord.getStartTime(),queryAlertMax.getEndTime())!=1){
							continue;
						}
						//判断 近三天 是否排班 
						Date classDayStart=DateUtils.addDay(toDaydate,-2);
						Date classDayEnd=toDaydate;
						EmployeeClass employeeClass=new EmployeeClass();
						employeeClass.setStartTime(classDayStart);
						employeeClass.setEndTime(classDayEnd);
						employeeClass.setEmployId(employees.getId());
						List<EmployeeClass> employeeClassList = employeeClassMapper.getEmployeeClassList(employeeClass);
						//排班 白班加起来 小于3 则此员工排班有休息
						if(employeeClassList.size()<3){
							//判断排班为休息 休息classSetId==null
							continue;
						}else{
							isClassEmp=true;
							EmployeeClass employeeClassFor=new EmployeeClass();
							//查询白班 是多少号 旷工计算天数往前推
							boolean classflag=true;
							while (classflag) {
								classDayStart = DateUtils.addDay(classDayStart,-1);
								int compareDate = DateUtils.compareDate(classDayStart, DateUtils.addDay(attnLastDayAbsenteeismAlertEmpTemple.getAlertStartTime(), 1));
								//排班 休息时间 要大于 最后打卡时间 取最在 排班里休息的时间  PS:双休法定 还算旷工时间
								if(compareDate==1){
									employeeClassFor.setEmployId(employees.getId());
									employeeClassFor.setClassDate(classDayStart);
									EmployeeClass employeeClassSetting = employeeClassMapper.getEmployeeClassSetting(employeeClassFor);
									if(employeeClassSetting==null){
										WorkDateFormatStartTemple=DateUtils.format(DateUtils.addDay(classDayStart, 1),DateUtils.FORMAT_SHORT);
										classflag=false;
									}
								}else{
									classflag=false;
								}
							}
						}
					}	
						attnSignRecord.setStartTime(DateUtils.parse(WorkDateFormatStart + " 00:00:00"));
						attnLastDayAbsenteeismAlertEmpTemple.setAlertStartTime(DateUtils.parse(WorkDateFormatStartTemple + " 00:00:00"));				
						attnLastDayAbsenteeismAlertEmpTemple.setAlertEndTime(DateUtils.parse(WorkDateFormatEnd + " 00:00:00"));		
						try {
							logger.info("compareTwoTimesWorkDate:start="+attnLastDayAbsenteeismAlertEmpTemple.getAlertStartTime()+";end="+attnLastDayAbsenteeismAlertEmpTemple.getAlertEndTime());
							AttnSignRecord compareTwoTimesWorkDate = annualVacationService.CompareTwoTimesWorkDate(attnLastDayAbsenteeismAlertEmpTemple.getAlertStartTime(),attnLastDayAbsenteeismAlertEmpTemple.getAlertEndTime(),isClassEmp,vacationMap);
							logger.info("compareTwoTimesWorkDate:str="+compareTwoTimesWorkDate.getStr());
							isClassEmp=false;
							if(compareTwoTimesWorkDate.getCount()>=3){
								attnLastDayAbsenteeismAlertEmpTemple.setAbsenteeismAlertDay(compareTwoTimesWorkDate.getCount());
								attnLastDayAbsenteeismAlertEmpTemple.setStr(compareTwoTimesWorkDate.getStr());
								attnLastDayAbsenteeismAlertEmpTemple.setEmployeeId(employees.getId());	
								absenteeismIdsAndLastDay.add(attnLastDayAbsenteeismAlertEmpTemple);
								absenteeismIds.add(employees.getId());
							}
						} catch (Exception e) {
							
						}//计算旷工有几天工作日
					}
				}	
			}	
	 		List<SendMail> sendMailList = new ArrayList<SendMail>();
			StringBuilder strTitle=new StringBuilder();
			StringBuilder str=new StringBuilder();
			String tempTitle=new String();
			String temp=new String();
			String exception="";//异常报错信息
			if(absenteeismIdsAndLastDay!=null && absenteeismIdsAndLastDay.size()>0){
				unAttnFlag=false;//这里会一起发送邮件
				logger.info("[step05]开始发邮件通知相关人事部门... ...");
				double  size = absenteeismIds.size();
				double  tempSize= Math.ceil(size/10);
				if(absenteeismIds.size()<10){
					for (int i = 0; i < absenteeismIds.size(); i++) {
						Map<String, String> empInfo = employeeMapper.queryEmpBaseInfoById(absenteeismIdsAndLastDay.get(i).getEmployeeId());
						//temp.append(empInfo.get("departName")+","+empInfo.get("cnName")+"日期："+WorkDateFormat +"	"+attnEndtTime +"\n");
						tempTitle=empInfo.get("departName")+"，"+empInfo.get("code")+"，"+empInfo.get("cnName")+" 日期："+DateUtils.format(absenteeismIdsAndLastDay.get(i).getAlertStartTime(),DateUtils.FORMAT_SHORT_CN) +" 到 "+DateUtils.format(absenteeismIdsAndLastDay.get(i).getAlertEndTime(),DateUtils.FORMAT_SHORT_CN)+"共计:"+absenteeismIdsAndLastDay.get(i).getAbsenteeismAlertDay()+"天 <br/>";
						temp=empInfo.get("departName")+"，"+empInfo.get("code")+"，"+empInfo.get("cnName")+" 旷工日期："+absenteeismIdsAndLastDay.get(i).getStr()+" 已连续 "+absenteeismIdsAndLastDay.get(i).getAbsenteeismAlertDay()+" 天旷工，请HR注意 <br/>";					
						strTitle.append(tempTitle);
						str.append(temp);
					}
					str.append(entryEmpStr);
					List<EmpPosition> positionName = sendMailPositionName();
					User userTemp=new User();
					userTemp.setCompanyId(1L);
						for (EmpPosition sendMailEmps:positionName) {
							Employee receiver = employeeMapper.getById(sendMailEmps.getEmployeeId());		
							try {
								if(receiver!=null){//邮件  每天都列出来
									SendMail sendMail = new SendMail();
									sendMail.setReceiver(receiver.getEmail());
									sendMail.setSubject("员工旷工提醒");
									sendMail.setSendStatus(SendMail.SEND_STATUS_NO);
									sendMail.setText(str.toString());
									sendMail.setOaMail(SendMail.OA_MAIL_P);
									sendMailList.add(sendMail);	
								}	
								
								empApplicationRegisterService.sendMsg(sendMailEmps.getEmployeeId(),userTemp,"员工旷工提醒",strTitle.toString());	//消息		带共计多少天	
							} catch (Exception e) {
								exception=e.getMessage();
								logger.info(exception);
								logger.info("发送短信通知失败：员工旷工数太多报错，数据库字段容载不下，超过存储字节，请联系人事查看相关数据");  																
								continue;
								//数据库 容不了那么多行数据 就会报错，要抛异常。
							}				
						}
						//发邮箱接口	
						if(sendMailList!=null&&sendMailList.size()>0){
							try {
								sendMailService.batchSave(sendMailList);
							}catch (Exception e) {
								exception=e.getMessage();
								logger.info(exception);
								logger.info("发送人事通知失败：员工旷工数太多报错，数据库字段容载不下，超过存储字节，请联系人事查看相关数据");	
							}	
						}
					strTitle.delete(0, strTitle.length());
					str.delete(0, str.length());
				}else{
					int x,y;
					x=0;
					y=10;
					for (int i = 0; i < tempSize; i++) {
						List<Long> subList = null;
						if(i+1==tempSize){
							//最后一步  x随基数相乘,y==size
							y=(int) size;
							subList = absenteeismIds.subList(x, y);
						}else{
							subList = absenteeismIds.subList(x, y);
						}					
						for (int j = 0; j < subList.size(); j++) {
							Map<String, String> empInfo = employeeMapper.queryEmpBaseInfoById(subList.get(j));
							//temp.append(empInfo.get("departName")+","+empInfo.get("cnName")+"日期："+WorkDateFormat +"	"+attnEndtTime +"\n");
							tempTitle=empInfo.get("departName")+"，"+empInfo.get("code")+"，"+empInfo.get("cnName")+" 日期："+DateUtils.format(absenteeismIdsAndLastDay.get(j).getAlertStartTime(),DateUtils.FORMAT_SHORT_CN) +" 到 "+DateUtils.format(absenteeismIdsAndLastDay.get(j).getAlertEndTime(),DateUtils.FORMAT_SHORT_CN)+"共计："+absenteeismIdsAndLastDay.get(j).getAbsenteeismAlertDay()+"天 <br/>";
							temp=empInfo.get("departName")+"，"+empInfo.get("code")+"，"+empInfo.get("cnName")+" 旷工日期："+absenteeismIdsAndLastDay.get(j).getStr()+" 已连续 "+absenteeismIdsAndLastDay.get(j).getAbsenteeismAlertDay()+" 天旷工，请HR注意 <br/>";					
							strTitle.append(tempTitle);
							str.append(temp);
						}
						str.append(entryEmpStr);
						entryEmpStr="";//上面发送的 从未打卡的员工 这里清空消息
						x=10*(i+1);
						y=(i+2)*10;
						//发送邮件 20个员工 yipp
						List<EmpPosition> positionName = sendMailPositionName();
						User userTemp=new User();
						userTemp.setCompanyId(1L);
							for (EmpPosition sendMailEmps:positionName) {
								Employee receiver = employeeMapper.getById(sendMailEmps.getEmployeeId());
								try {
									if(receiver!=null){
										SendMail sendMail = new SendMail();
										sendMail.setReceiver(receiver.getEmail());
										sendMail.setSubject("员工旷工提醒");
										sendMail.setSendStatus(SendMail.SEND_STATUS_NO);
										sendMail.setText(str.toString());
										sendMail.setOaMail(SendMail.OA_MAIL_P);
										sendMailList.add(sendMail);	
									}
									empApplicationRegisterService.sendMsg(sendMailEmps.getEmployeeId(),userTemp,"员工旷工提醒",strTitle.toString());									
								} catch (Exception e) {
									exception=e.getMessage();
									logger.info(exception);
									logger.info("发送短信通知失败：员工旷工数太多报错，数据库字段容载不下，超过存储字节，请联系人事查看相关数据");	
									continue;
								}					
							}
							//发邮箱接口	
							if(sendMailList!=null&&sendMailList.size()>0){
								try {
									sendMailService.batchSave(sendMailList);
								}catch (Exception e) {
									exception=e.getMessage();
									logger.info(exception);
									logger.info("发送人事通知失败：员工旷工数太多报错，数据库字段容载不下，超过存储字节，请联系人事查看相关数据");	
									continue;
								}	
							}
						strTitle.delete(0, strTitle.length());
						str.delete(0, str.length());
					}	
				}	
				logger.info("[step06]有旷工三日的员工... ..."+absenteeismIds.size() +"个");
			  }else if(unAttnFlag){//有从未打过卡的员工 并且 期间没有 旷工的员工  这里单独发邮件
				  User userTemp=new User();
				  userTemp.setCompanyId(1L);
				  List<EmpPosition> positionName = sendMailPositionName();
				  for (EmpPosition sendMailEmps:positionName) {
						Employee receiver = employeeMapper.getById(sendMailEmps.getEmployeeId());		
						try {
							if(receiver!=null){//邮件  每天都列出来
								SendMail sendMail = new SendMail();
								sendMail.setReceiver(receiver.getEmail());
								sendMail.setSubject("员工旷工提醒");
								sendMail.setSendStatus(SendMail.SEND_STATUS_NO);
								sendMail.setText(entryEmpStr.toString());
								sendMail.setOaMail(SendMail.OA_MAIL_P);
								sendMailList.add(sendMail);	
							}	
							
							//empApplicationRegisterService.sendMsg(sendMailEmps.getEmployeeId(),userTemp,"员工旷工提醒",entryEmpStr.toString());	//消息		带共计多少天															
						} catch (Exception e) {
							exception=e.getMessage();
							logger.info(exception);
							logger.info("发送人事通知失败：员工旷工数太多报错，数据库字段容载不下，超过存储字节，请联系人事查看相关数据");							
							continue;
						}				
					}
				  	//发邮箱接口	
					if(sendMailList!=null&&sendMailList.size()>0){
						try {
							sendMailService.batchSave(sendMailList);
						}catch (Exception e) {
							exception=e.getMessage();
							logger.info(exception);
							logger.info("发送人事通知失败：员工旷工数太多报错，数据库字段容载不下，超过存储字节，请联系人事查看相关数据");	
						}	
					}
			  }else{
				logger.info("当前 "+ DateUtils.format(new Date(),DateUtils.FORMAT_LONG_CN) +"没有旷工三日的员工... ...");  
				  return ;
			  }
			}
		public List<Long> whiteList(){
			/**  白名单：邮乐公司（部门负责人、司机、秘书、杨总、瞿总
			外出、任何请假，异常考勤(提的申请的)  出差(提的申请的 四个都是） 过滤掉*/
			List<Long> ids=new ArrayList<Long> ();
			//1.1白名单 职位
			List<String> positionNames = new ArrayList<String>();
			positionNames.add("司机");positionNames.add("总裁秘书");
			EmpPosition empPosition = new EmpPosition();
			empPosition.setPositionNames(positionNames);
			List<EmpPosition> listByPositionName = empPositionMapper.getListByPositionName(empPosition);		
	        for (EmpPosition empPositions : listByPositionName) {
	 			ids.add(empPositions.getEmployeeId());
			}
			//1.2白名单 部门负责人  select * from base_depart where  leader is not null
	    	List<Long> DepartmentLeaders = attnSignRecordMapper.getADepartmentLeaders();
			ids.addAll(DepartmentLeaders);
			
			List<CompanyConfig> absenteeismWhiteList = companyConfigService.getListByCode("absenteeismWhiteList");
			
			Employee empCodeList=new Employee();
			List<String> codeList=new ArrayList<String>();
			
			for (CompanyConfig companyConfig : absenteeismWhiteList) {
				codeList.add(companyConfig.getDisplayCode());
			} 
			empCodeList.setCodeList(codeList);
			List<Employee> empAbsenteeismWhiteList = employeeService.getListByCondition(empCodeList);
			for (Employee employee : empAbsenteeismWhiteList) {
				ids.add(employee.getId());
			}
			return ids;
		}
		public List<EmpPosition> sendMailPositionName(){
			List<String> positionNames = new ArrayList<String>();
			positionNames.add("人力资源及行政总监");positionNames.add("人力资源高级经理");
			positionNames.add("人力资源经理");positionNames.add("人力资源助理");
			positionNames.add("高级HRBP");positionNames.add("HRBP");positionNames.add("HRBP专员");
			positionNames.add("薪资福利高级专员");positionNames.add("企业文化专员");
			EmpPosition empPosition = new EmpPosition();
			empPosition.setPositionNames(positionNames);
			return  empPositionMapper.getListByPositionName(empPosition);
		}
		
		private static final double ULE_LAT = 31.2466862800;//ULE纬度
		private static final double ULE_LNG = 121.5648686900;//ULE经度
		
		private static final double ULE_BEIJING_LAT = 39.922610;//ULE北京纬度
		private static final double ULE_BEIJING_LNG = 116.354530;//ULE北京经度

		@Override
		public Map<String,Object>  locationCheckIn(String ip, String locationResult) {
			Map<String,Object> result = new HashMap<String,Object>();
			//获取当前用户
			User user = userService.getCurrentUser();
			if(user==null||user.getEmployee()==null){
				result.put("message", "未获取到用户信息，签到失败。");
				result.put("success", false);
				return result;
			}else{
				
				Employee emp = employeeService.getById(user.getEmployee().getId());
				
				if(!"北京".equals(emp.getWorkAddressProvince())) {
					result.put("message", "签到失败，暂时只支持北京员工签到。");
					result.put("success", false);
					return result;
				}
				
				
				logger.info(user.getEmployee().getCnName()+"签到开始;location="+locationResult);
				String address = "{}";
				Double distance = null;
				try{
					JSONObject location = JSON.parseObject(locationResult);
				    if(location!=null){
					   String latitude = location.getString("latitude");
					   String longitude = location.getString("longitude");
					   if(StringUtils.isNotBlank(latitude)&&StringUtils.isNotBlank(longitude)){
						   String loctionP = latitude+","+longitude;
						   String url = "http://apis.map.qq.com/ws/geocoder/v1/?location="+loctionP+"&key=3VVBZ-6THKX-UQG4A-7DYHR-6VXK5-44BGN";
						   
						   HttpRequest req = new HttpRequest.Builder().url(url).get().build();
						   HttpResponse rep = client.sendRequest(req);
						   address = rep.fullBody();
						   
						   distance = CalDistanceUtil.GetDistance(ULE_BEIJING_LNG, ULE_BEIJING_LAT,Double.valueOf(longitude), Double.valueOf(latitude));
						   if(distance>100){
							   AttnSignRecord checkIn = new AttnSignRecord();
							   checkIn.setDistance(distance);
							   checkIn.setAddress(address);
							   checkIn.setIp(ip);
							   checkIn.setLocationResult(locationResult);
							   checkIn.setEmployeeId(user.getEmployee().getId());
							   checkIn.setEmployeeName(user.getEmployee().getCnName());
							   checkIn.setDelFlag(1);
							   checkIn.setType(5);
							   checkIn.setCreateTime(new Date());
							   checkIn.setSignTime(new Date());
							   checkIn.setCompanyId(user.getCompanyId());
							   attnSignRecordMapper.insert(checkIn);
							   logger.info(user.getEmployee().getCnName()+"签到失败;distance="+distance);
							   result.put("message", "签到失败 请确保距离办公地点100米以内，尝试重新签到或联系HR同事。");
							   result.put("success", false);
							   return result;
						   }else{
							    AttnSignRecord checkIn = new AttnSignRecord();
								checkIn.setDistance(distance);
								checkIn.setAddress(address);
								checkIn.setIp(ip);
								checkIn.setLocationResult(locationResult);
								checkIn.setEmployeeId(user.getEmployee().getId());
								checkIn.setEmployeeName(user.getEmployee().getCnName());
								checkIn.setDelFlag(0);
								checkIn.setType(5);
								checkIn.setCreateTime(new Date());
								checkIn.setSignTime(new Date());
								checkIn.setCompanyId(user.getCompanyId());
								attnSignRecordMapper.insert(checkIn);
								logger.info(user.getEmployee().getCnName()+"签到结束");
								result.put("success", true);
						   }
					   }else{
						   result.put("success", false);
					   }
				    }else{
				    	result.put("success", false);
				    }
				}catch(Exception e){
					result.put("success", false);
					logger.error(user.getEmployee().getCnName()+"签到开始;location="+locationResult+"地址解析失败。。。");
				}
				
				
			}
			return result;
		}


		@Override
		public List<Map<String, Object>> getSSDKMXReport(Date startTime,
				Date endTime, Long departId, List<Long> empTypeIdList) {
			//先查出符合条件的所有员工
			List<EmployeeBaseInfoDTO> baseInfoList = employeeBaseInfoMapper.getYGKQMXReport(startTime, endTime, departId, empTypeIdList);
			List<Long> employeeIdList = new ArrayList<Long>();
			
			for(EmployeeBaseInfoDTO info:baseInfoList){
				employeeIdList.add(info.getId());
			}
			
			//返回结果
			List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
			
			//筛选出需要的打卡记录
			if(employeeIdList==null||employeeIdList.size()<=0){
				return datas;
			}
			
			//打卡记录
			List<AttnSignRecord> recordList = attnSignRecordMapper.getSSDKMXReport(startTime, endTime, employeeIdList);
			Map<Long,List<AttnSignRecord>> recordMap = recordList.stream().collect(Collectors.groupingBy(AttnSignRecord :: getEmployeeId));
			
			//班次
    		EmployeeClass employeeClassP = new EmployeeClass();
			employeeClassP.setStartTime(startTime);
			employeeClassP.setEndTime(endTime);
			List<EmployeeClass> employeeClassList = employeeClassMapper.getEmployeeClassList(employeeClassP);
			Map<Long,List<EmployeeClass>> employeeClassMap = employeeClassList.stream().collect(Collectors.groupingBy(EmployeeClass :: getEmployId));
			
			//工时制
			CompanyConfig companyConfigConditon = new CompanyConfig();//查询配置表条件
			companyConfigConditon.setCode("typeOfWork");//工时类型
			List<CompanyConfig> workTypeList = companyConfigService.getListByCondition(companyConfigConditon);
			Map<Long,CompanyConfig> workTypeMap = workTypeList.stream().collect(Collectors.toMap(CompanyConfig :: getId,a->a,(k1,k2)->k1));
			
			
			//组装报表结果
			for(EmployeeBaseInfoDTO info:baseInfoList){
				
				Map<Date,List<EmployeeClass>> employeeClassListEmpDateMap = new HashMap<Date,List<EmployeeClass>>();
    			if(employeeClassMap!=null&&employeeClassMap.containsKey(info.getId())){
    				employeeClassListEmpDateMap = employeeClassMap.get(info.getId()).stream().collect(Collectors.groupingBy(EmployeeClass :: getClassDate));
    			}
				
				String workTypeName = "";//工时类型
				if(workTypeMap!=null&&workTypeMap.containsKey(info.getWorkType())){
					workTypeName = workTypeMap.get(info.getWorkType()).getDisplayName();
				}

				if(recordMap!=null&&recordMap.containsKey(info.getId())){
					
					List<AttnSignRecord> recordListEmp = recordMap.get(info.getId());
					for(AttnSignRecord record:recordListEmp){
						Map<String, Object> data = new HashMap<String, Object>();
						data.put("code", info.getCode());//员工编号
						data.put("cn_name", info.getCnName());//员工姓名
						data.put("departName", info.getDepartName());//部门名称
						data.put("workTypeName", workTypeName);//工时类型
						data.put("attn_date", DateUtils.format(record.getSignTime(), DateUtils.FORMAT_SHORT));//考勤日期
						data.put("dayofweek", "星期"+DateUtils.getWeek(record.getSignTime()));//星期
						data.put("sign_time", DateUtils.format(record.getSignTime(), DateUtils.FORMAT_HH_MM_SS));//打卡时间
						data.put("className", "休息");//班次名称
						Date attnDate = DateUtils.parse(DateUtils.format(record.getSignTime(), DateUtils.FORMAT_SHORT),DateUtils.FORMAT_SHORT);
						List<EmployeeClass> employeeClassListEmpDate = new ArrayList<EmployeeClass>();
		    			if(employeeClassListEmpDateMap!=null&&employeeClassListEmpDateMap.containsKey(attnDate)){
		    				employeeClassListEmpDate = employeeClassListEmpDateMap.get(attnDate);
		    				if(employeeClassListEmpDate!=null&&employeeClassListEmpDate.size()>0){
		    					data.put("className", employeeClassListEmpDate.get(0).getName());
		    				}
		    			}
						datas.add(data);
					}
				}
			}
			
			return datas;
		}
	}
