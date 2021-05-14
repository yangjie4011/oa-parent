package com.ule.oa.base.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.flowable.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ibm.icu.text.SimpleDateFormat;
import com.ule.arch.basehttpclient.BaseHttpClientFactory;
import com.ule.arch.basehttpclient.client.BaseHttpClient;
import com.ule.arch.basehttpclient.model.HttpRequest;
import com.ule.arch.basehttpclient.model.HttpResponse;
import com.ule.arch.basehttpclient.standard.util.ContentCoverter;
import com.ule.oa.base.cache.CompanyConfigCacheManager;
import com.ule.oa.base.cache.ConfigCacheManager;
import com.ule.oa.base.mapper.ApplicationEmployeeClassDetailMapper;
import com.ule.oa.base.mapper.ApplicationEmployeeClassMapper;
import com.ule.oa.base.mapper.ClassSettingMapper;
import com.ule.oa.base.mapper.CompanyConfigMapper;
import com.ule.oa.base.mapper.CompanyFloorMapper;
import com.ule.oa.base.mapper.CompanyMapper;
import com.ule.oa.base.mapper.CompanyPositionLevelMapper;
import com.ule.oa.base.mapper.ConfigMapper;
import com.ule.oa.base.mapper.DepartMapper;
import com.ule.oa.base.mapper.EmpApplicationRegisterMapper;
import com.ule.oa.base.mapper.EmpDepartMapper;
import com.ule.oa.base.mapper.EmpMsgMapper;
import com.ule.oa.base.mapper.EmpPositionMapper;
import com.ule.oa.base.mapper.EmpTypeMapper;
import com.ule.oa.base.mapper.EmployeeClassMapper;
import com.ule.oa.base.mapper.EmployeeMapper;
import com.ule.oa.base.mapper.PositionMapper;
import com.ule.oa.base.mapper.RabcRoleMapper;
import com.ule.oa.base.mapper.RabcUserRoleMapper;
import com.ule.oa.base.mapper.ScheduleGroupMapper;
import com.ule.oa.base.mapper.UserMapper;
import com.ule.oa.base.po.ApplicationEmployeeClass;
import com.ule.oa.base.po.ApplicationEmployeeClassDetail;
import com.ule.oa.base.po.AttnUsers;
import com.ule.oa.base.po.ClassSetting;
import com.ule.oa.base.po.Company;
import com.ule.oa.base.po.CompanyConfig;
import com.ule.oa.base.po.CompanyFloor;
import com.ule.oa.base.po.CompanyPositionLevel;
import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.EmpApplicationRegister;
import com.ule.oa.base.po.EmpDepart;
import com.ule.oa.base.po.EmpMsg;
import com.ule.oa.base.po.EmpPosition;
import com.ule.oa.base.po.EmpScheduleGroup;
import com.ule.oa.base.po.EmpType;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.po.Position;
import com.ule.oa.base.po.RabcRole;
import com.ule.oa.base.po.RabcUserRole;
import com.ule.oa.base.po.SendMail;
import com.ule.oa.base.po.User;
import com.ule.oa.base.po.tbl.ViewTaskInfoTbl;
import com.ule.oa.base.service.AnnualVacationService;
import com.ule.oa.base.service.CompanyConfigService;
import com.ule.oa.base.service.EmpApplicationRegisterService;
import com.ule.oa.base.service.EmpLeaveService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.SendMailService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.service.ViewTaskInfoService;
import com.ule.oa.base.util.HanyupinyinUtil;
import com.ule.oa.base.vo.TaskVO;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.po.CommonPo;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.json.JSONUtils;
/**
 * @ClassName: EmployeeRegister
 * @Description: 员工入职登记表
 * @author yangjie
 * @date 2017年5月22日
*/

@Service
public class EmpApplicationRegisterServiceImpl implements EmpApplicationRegisterService {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private EmpApplicationRegisterMapper employeeRegisterMapper;
	@Autowired
	private EmployeeMapper employeeMapper;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private EmpDepartMapper empDepartMapper;
	@Autowired
	private EmpPositionMapper empPositionMapper;
	@Autowired
	private DepartMapper departMapper;
	@Autowired
	private PositionMapper positionMapper;
	@Autowired
	private CompanyMapper companyMapper;
	@Autowired
	private EmpLeaveService empLeaveService;
	@Autowired
	private UserService userService;
	@Autowired
	private EmpMsgMapper empMsgMapper;
	@Autowired
	private SendMailService	sendMailService;
	@Autowired
	private EmpTypeMapper empTypeMapper;
	@Autowired
	private CompanyFloorMapper companyFloorMapper;
	@Resource
	private ConfigCacheManager configCacheManager;
	@Resource
	private CompanyConfigCacheManager companyConfigCacheManager;
	@Autowired
	private ActivitiServiceImpl activitiServiceImpl;
	@Autowired
	private ViewTaskInfoService viewTaskInfoService;
	@Autowired
	private ConfigMapper configMapper;
	@Autowired
	private CompanyConfigMapper companyConfigMapper;
	@Autowired
	private ClassSettingMapper classSettingMapper;
	@Autowired
	private AnnualVacationService annualVacationService;
	@Autowired
	private EmployeeClassMapper employeeClassMapper;
	@Autowired
	private CompanyConfigService companyConfigService;
	@Autowired
	private ScheduleGroupMapper scheduleGroupMapper;
	@Autowired
	private ApplicationEmployeeClassMapper applicationEmployeeClassMapper;
	@Autowired
	private ApplicationEmployeeClassDetailMapper applicationEmployeeClassDetailMapper;
	@Autowired
	private CompanyPositionLevelMapper companyPositionLevelMapper;
	@Autowired
	private RabcRoleMapper rabcRoleMapper;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private RabcUserRoleMapper rabcUserRoleMapper;
	
	private static BaseHttpClient client = BaseHttpClientFactory.getClient();
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String,Object> save(EmpApplicationRegister employeeRegister,User user) throws Exception{
		Map<String,Object> map = new HashMap<String, Object>();
		if(employeeRegister.getCompanyId()==null){
			throw new OaException("公司名称不能为空!");
		}
		if(employeeRegister.getEmployeeTypeId()==null){
			throw new OaException("员工类型不能为空!");
		}
		if(employeeRegister.getWorkType()==null){
			throw new OaException("工时种类不能为空!");
		}
		if(employeeRegister.getWhetherScheduling()==null){
			throw new OaException("是否排班不能为空!");
		}
		if(StringUtils.isBlank(employeeRegister.getCnName())){
			throw new OaException("中文名不能为空!");
		}
		if(employeeRegister.getBirthDate()==null){
			throw new OaException("出生日期不能为空!");
		}
		String pattern = "^[\\u4e00-\\u9fa5]{0,}$";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(employeeRegister.getCnName());
		if(!m.matches()){
			throw new OaException("中文名格式不正确！");
		}
		if(StringUtils.isBlank(employeeRegister.getMobile())){
			throw new OaException("手机号不能为空!");
		}
		String pattern1 = "^1[3456789]\\d{9}$";
		Pattern r1 = Pattern.compile(pattern1);
		Matcher m1 = r1.matcher(employeeRegister.getMobile());
		if(!m1.matches()){
			throw new OaException("手机号格式不正确！");
		}
		if(employeeRegister.getEmploymentDate()==null){
			throw new OaException("入职日期不能为空!");
		}
		if(employeeRegister.getDepartId()==null){
			throw new OaException("部门不能为空!");
		}
		if(employeeRegister.getPositionId()==null){
			throw new OaException("职位不能为空!");
		}
		if(StringUtils.isBlank(employeeRegister.getPositionLevel())){
			throw new OaException("职级不能为空!");
		}
		if(StringUtils.isBlank(employeeRegister.getPositionSeq())){
			throw new OaException("职位序列不能为空!");
		}
		if(employeeRegister.getLeader()==null){
			throw new OaException("汇报对象不能为空!");
		}
		if(employeeRegister.getType()==null){
			throw new OaException("请勾选外包/实习生转内!");
		}
		if(StringUtils.isBlank(employeeRegister.getWorkAddressProvince())){
			throw new OaException("请选择工作地点!");
		}
		if(StringUtils.isBlank(employeeRegister.getWorkAddressCity())){
			throw new OaException("请选择工作地点!");
		}
		employeeRegister.setWorkAddressType(1);//默认外地员工
		if("上海".equals(employeeRegister.getWorkAddressProvince())||"上海市".equals(employeeRegister.getWorkAddressCity())){
			employeeRegister.setWorkAddressType(0);//上海视为本地
		}
		if(employeeRegister.getType()==1){
			//外包/实习生转内
			if(StringUtils.isBlank(employeeRegister.getCode())){
				throw new OaException("员工编号不能为空!");
			}
			Employee emp = employeeMapper.getInfoByCode(employeeRegister.getCode());
			if(emp==null){
				throw new OaException("员工编号有误!");
			}
			if(emp.getQuitTime()==null){
				throw new OaException("员工尚未填写离职日期！");
			}
			//入职日期必须大于离职日期
			if(Integer.valueOf(DateUtils.format(emp.getQuitTime(), DateUtils.FORMAT_SIMPLE))
					>=Integer.valueOf(DateUtils.format(employeeRegister.getEmploymentDate(), DateUtils.FORMAT_SIMPLE))){
				throw new OaException("入职日期需在离职日期后！");
			}
			
			employeeRegister.setSeatNo(emp.getSeatCode());
			//查询楼层id
			CompanyFloor floorParam = new CompanyFloor();
			floorParam.setFloorNum(emp.getFloorCode()!=null?Long.valueOf(emp.getFloorCode()):0);
			List<CompanyFloor> floorList = companyFloorMapper.getListByCondition(floorParam);
			employeeRegister.setFloorId((floorList!=null&&floorList.size()>0)?floorList.get(0).getId():null);
			employeeRegister.setEmail(emp.getEmail());
			employeeRegister.setExtensionNumber(emp.getExtensionNumber());
			employeeRegister.setFingerprintId(emp.getFingerprintId());
		}else{
			//普通入职（邮箱暂时取中文名转拼音）
			//验证通行证是否存在
 			String username = HanyupinyinUtil.getPinyinString(employeeRegister.getCnName());
 			HashMap<String, String> paramMap = new HashMap<String,String>();
 			paramMap.put("username", username);
 			//读取公司配置表
 			List<CompanyConfig> companyConfigList = companyConfigCacheManager.get("companyCode");
 			boolean isTom = false;
 			if(companyConfigList!=null&&companyConfigList.size()>0){
 				if("TOM".equals(companyConfigList.get(0).getDisplayCode())){
 					isTom = true;
 				}
 			}
			
			HttpRequest req = new HttpRequest.Builder().url(ConfigConstants.ULEACCOUNT_EXIST).post(HttpRequest.HttpMediaType.FORM_URLENCODE, 
    				ContentCoverter.formConvertAsString(paramMap)).build();
			HttpResponse rep = client.sendRequest(req);
			
			String response = rep.fullBody();

			Map<?, ?> responseMap = JSONUtils.readAsMap(response);
		    String resultCode = (String)responseMap.get("errorCode");
			if("".equals(resultCode)){
				username = (String)responseMap.get("username");
				employeeRegister.setEmail(username+"@ule.com");
			}else{
				throw new OaException("验证用户存在接口-系统错误");
			}
		}
		employeeRegister.setDelFlag(CommonPo.STATUS_NORMAL);
		employeeRegister.setCreateTime(new Date());
		employeeRegister.setCreateUser(user.getEmployee().getCnName());
		employeeRegister.setEntryStatus(EmpApplicationRegister.ENTRY_STATUS_ING);
		employeeRegister.setEmployeeId(user.getEmployee().getId());
		employeeRegister.setDepartName(user.getDepart().getName());
		
		//XXX----------------------------开始流程------------------------------
		String processInstanceId = activitiServiceImpl.startByKey(ConfigConstants.ENTRY_KEY);
		employeeRegister.setProcessInstanceId(processInstanceId);
		employeeRegister.setApprovalStatus(ConfigConstants.DOING_STATUS);
		employeeRegisterMapper.save(employeeRegister);
		
		//-----------------start-----------------------保存流程节点信息
		ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
		flow.setAssigneeName(user.getEmployee().getCnName());
		flow.setDepartName(user.getDepart().getName());
		flow.setPositionName(user.getPosition().getPositionName());
		flow.setFinishTime(new Date());
		flow.setComment("新员工入职");
		flow.setProcessId(processInstanceId);
		flow.setProcessKey(ConfigConstants.ENTRY_KEY);
		flow.setStatu(0);
		viewTaskInfoService.save(flow);
		
		sendEntryMail(employeeRegister, user);


		map.put("success", true);
		return map;
	}


	@Override
	public void sendMsg(Long employeeId,User user,String title,String text){
		EmpMsg dhMsg = new EmpMsg();
		dhMsg.setDelFlag(CommonPo.STATUS_NORMAL);
		dhMsg.setType(EmpMsg.type_200);
		dhMsg.setCompanyId(user.getCompanyId());
		dhMsg.setEmployeeId(employeeId);
		dhMsg.setTitle(title);
		dhMsg.setContent(text);
		dhMsg.setReadFlag(EmpMsg.READ_FLAG_NO);
		dhMsg.setCreateTime(new Date());
		empMsgMapper.save(dhMsg);
	}
	
	@Override
	public EmpApplicationRegister getById(Long id) {
		return employeeRegisterMapper.getById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> delay(EmpApplicationRegister employeeRegister)
			throws Exception {
		User user = userService.getCurrentUser();
		Map<String,Object> map = new HashMap<String, Object>();
		employeeRegister.setHrCooperateUser(user.getEmployee().getCnName());
		employeeRegister.setHrCooperateDate(new Date());
		employeeRegister.setProcessStatus(EmpApplicationRegister.PROCESS_STATUS_ING);
		employeeRegister.setEntryStatus(EmpApplicationRegister.ENTRY_STATUS_ING);
		
		EmpApplicationRegister old = employeeRegisterMapper.getById(employeeRegister.getId());
		if(old!=null){
			employeeRegister.setDelayEntryDate(employeeRegister.getEmploymentDate());
			employeeRegisterMapper.updateById(employeeRegister);
			employeeRegister.setToEmails(old.getToEmails());
			employeeRegister.setToPersions(old.getToPersions());
			//获取所有需要通知的员工id
			List<String> positionNames = new ArrayList<String>();
			positionNames.add("企业文化专员");positionNames.add("人力资源高级经理");positionNames.add("HRBP专员");
			positionNames.add("人力资源经理");positionNames.add("高级HRBP");positionNames.add("HRBP");positionNames.add("HRBP助理");
			positionNames.add("人力资源助理");positionNames.add("IT主管");positionNames.add("IT高级专员");positionNames.add("行政经理");
			positionNames.add("行政专员");positionNames.add("行政前台");positionNames.add("IT经理");
			EmpPosition empPosition = new EmpPosition();
			empPosition.setPositionNames(positionNames);
			List<EmpPosition> empPositionList = empPositionMapper.getListByPositionName(empPosition);
			List<String> emailList = new ArrayList<String>();
			for(EmpPosition emp:empPositionList){
				//发送消息
				sendMsg(emp.getEmployeeId(),user,"新员工入职延期提醒","新员工"+old.getCnName()+"已延期至"+DateUtils.format(employeeRegister.getDelayEntryDate(),"yyyy年MM月dd日")+"入职，请各部门知晓");
			}
			sendMsg(old.getLeader(),user,"新员工入职延期提醒","新员工"+old.getCnName()+"已延期至"+DateUtils.format(employeeRegister.getDelayEntryDate(),"yyyy年MM月dd日")+"入职，请各部门知晓");
			
			//新员工逐级汇报对象到部门负责人为止
			Long reportToLeader = old.getLeader();//申请人汇报对象
			List<Long> reportToLeaderList = new ArrayList<Long>();
			int count = 0;
			while(true){
				count = count + 1;
				if(reportToLeader!=null){
					reportToLeaderList.add(reportToLeader);
					//判断是否是部门负责人
					List<Depart> departList = departMapper.getAllDepartByLeaderId(reportToLeader);
					if(departList!=null&&departList.size()>0){
						break;
					}else{
						Employee reportToLeaderObject = employeeService.getById(reportToLeader);
						reportToLeader = reportToLeaderObject!=null?reportToLeaderObject.getReportToLeader():null;
					}
				}else{
					logger.info("请假申请:申请人汇报对象为空。");
				}
				//对象关系过多，自动跳出循环，防止死循环
				if(count>10){
					break;
				}
			}
			if(reportToLeaderList!=null&&reportToLeaderList.size()>0){
				List<Employee> leaderList = employeeService.getListByIds(reportToLeaderList);
				for(Employee data:leaderList){
					emailList.add(data.getEmail());
				}
			}
			
			Depart depart = departMapper.getById(old.getDepartId());
			if(depart!=null&&depart.getLeader()!=null&&!depart.getLeader().equals(old.getLeader())){
				sendMsg(depart.getLeader(),user,"新员工入职延期提醒","新员工"+old.getCnName()+"已延期至"+DateUtils.format(employeeRegister.getDelayEntryDate(),"yyyy年MM月dd日")+"入职，请各部门知晓");
				Employee receiverDh = employeeMapper.getById(depart.getLeader());
				if(receiverDh!=null && StringUtils.isNotBlank(receiverDh.getEmail())){
					emailList.add(receiverDh.getEmail());
				}
			}
		
		//发送邮件给PMO,IT,行政,人事
		//查询群组email
		//发邮件给PMO群组:
		if(depart != null && ("117".equals(depart.getCode())||"116".equals(depart.getCode())
				||"134".equals(depart.getCode())||"135".equals(depart.getCode()))){	
			String pmoEmail = companyConfigService.getGroupEmail(ConfigConstants.GROUP_EMAIL_PMO);
			emailList.add(pmoEmail);
		}
		//发邮件给IT群组
		String itEmail = companyConfigService.getGroupEmail(ConfigConstants.GROUP_EMAIL_IT);
		emailList.add(itEmail);
		//发邮件给行政群组
		String adminEmail = companyConfigService.getGroupEmail(ConfigConstants.GROUP_EMAIL_ADMIN);
		emailList.add(adminEmail);
		//发邮件给人事群组
		String hrEmail = companyConfigService.getGroupEmail(ConfigConstants.GROUP_EMAIL_HR);
		emailList.add(hrEmail);
		//发邮件给抄送人
		//抄送人id转成邮箱
		if(StringUtils.isNotBlank(employeeRegister.getToPersions())){
			List<Long> employeeIdList = new ArrayList<Long>();
			for(String employeeId:employeeRegister.getToPersions().split(",")){
				employeeIdList.add(Long.valueOf(employeeId));
			}
			Employee toPersionsP = new Employee();
			toPersionsP.setIds(employeeIdList);
			List<Employee> toPersions = employeeMapper.getListByCondition(toPersionsP);
			for(Employee data:toPersions){
				emailList.add(data.getEmail());
			}
		}
		//发邮件给抄送邮件
		if(StringUtils.isNoneBlank(employeeRegister.getToEmails())){
			List<String> toEmails = Arrays.asList(employeeRegister.getToEmails().split(","));
			emailList.addAll(toEmails);
		}
		//去重
		List<String> emailDistinctList = emailList.stream().distinct().collect(Collectors.toList());
		sendDelayEntryMail(user,old, emailDistinctList,employeeRegister.getDelayEntryDate());
		}else{
			throw new OaException("员工数据不存在！");
		}
		map.put("success", true);
		return map;
	}

	@Override
	public EmpApplicationRegister queryByProcessInstanceId(
			String processInstanceId) {
		return employeeRegisterMapper.queryByProcessInstanceId(processInstanceId);
	}

	@Override
	public void setValueToVO(TaskVO taskVO, String processInstanceId,String taskId) {
		EmpApplicationRegister register = employeeRegisterMapper.queryByProcessInstanceId(processInstanceId);
        if(register!=null){
        	taskVO.setProcessName("入职登记申请");
    		taskVO.setCreatorDepart(register.getDepartName());
    		taskVO.setCreator(register.getCreateUser());
    		taskVO.setCreateTime(register.getCreateTime());
    		taskVO.setReProcdefCode("10");
    		taskVO.setProcessId(register.getProcessInstanceId());
    		taskVO.setResourceId(String.valueOf(register.getId()));
    		taskVO.setRedirectUrl("/employeeRegister/toHandle.htm?flag=no&id="+register.getId());
    		if(!(taskVO.getProcessStatu()==null)) {
    			taskVO.setRedirectUrl("/employeeRegister/toHandle.htm?flag=can&id="+register.getId()+"&taskId="+taskId);
    		}
    		taskVO.setProcessStatu(register.getApprovalStatus());
			taskVO.setOperation(ConfigConstants.STATUS.getOperationByStatu(register.getApprovalStatus()));
        }
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String,Object> completeTask(String processId, String comment,
			String commentType, EmpApplicationRegister param) throws Exception {
		Map<String,Object> result = new HashMap<String,Object>();
		User user = userService.getCurrentUser();
		logger.info("员工入职任务处理：taskId="+param.getTaskId());
		Task task = activitiServiceImpl.getTaskById(param.getTaskId());
		if(task==null){
			throw new OaException("taskId="+param.getTaskId()+"的任务不存在！");
		}
		if(!task.getProcessInstanceId().equals(processId)){
			throw new OaException("taskId="+param.getTaskId()+"的任务与实例Id="+processId+"的流程不匹配！");
		}
		boolean type = StringUtils.startsWithIgnoreCase(task.getTaskDefinitionKey(), "personnel");
		EmpApplicationRegister register = employeeRegisterMapper.queryByProcessInstanceId(processId);
		if(register!=null){
			
			Integer approvalStatus ;
			if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.PASS)) {
				approvalStatus=type?ConfigConstants.PASS_STATUS:ConfigConstants.DOING_STATUS;
			}else if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.REFUSE)) {
				approvalStatus=ConfigConstants.REFUSE_STATUS;
			}else {
				approvalStatus=ConfigConstants.BACK_STATUS;
			}
			
			if(param!=null){
				
				//IT部门操作
				if("IT".equals(param.getNodeCode())){
					if(param.getEmail()==null||"".equals(param.getEmail())){
						throw new OaException("邮箱不能为空！");
					}
					param.setItCooperateUser(user.getEmployee().getCnName());
					param.setItCooperateDate(new Date());
					param.setProcessStatus(EmpApplicationRegister.PROCESS_STATUS_ING);
				}
				
				//行政部门操作
                if("executive".equals(param.getNodeCode())){
                	if(param.getFloorId()==null){
        				throw new OaException("楼层不能为空！");
        			}
        			if(param.getSeatNo()==null||"".equals(param.getSeatNo())){
        				throw new OaException("座位号不能为空！");
        			}
        			param.setAdCooperateUser(user.getEmployee().getCnName());
        			param.setAdCooperateDate(new Date());
        			param.setProcessStatus(EmpApplicationRegister.PROCESS_STATUS_ING);
				}
                
                //确认入职
                if("confirm".equals(param.getNodeCode())){
                	
            		param.setHrCooperateUser(user.getEmployee().getCnName());
                	param.setHrCooperateDate(new Date());
                	param.setProcessStatus(EmpApplicationRegister.PROCESS_STATUS_YES);
                	param.setEntryStatus(EmpApplicationRegister.ENTRY_STATUS_YES);
                	
                	String OA_SERVICE_URL = configCacheManager.getConfigDisplayCode("MO_SERVICE_URL");
             		
                	//校验指纹ID是否存在
             		String userid = "";
             		if(param.getFingerprintId()!=null){
             			//查询考勤机有没有对应的用户
             			HashMap<String, String> paramMap3 = new HashMap<String,String>();
             			paramMap3.put("fingerprintId", String.valueOf(param.getFingerprintId()));
             			logger.info("入职根据指纹ID查询用户地址:"+OA_SERVICE_URL+"/attnUsers/selectByFingerprintId.htm");
             			
             			HttpRequest req = new HttpRequest.Builder().url(OA_SERVICE_URL+"/attnUsers/selectByFingerprintId.htm").post(HttpRequest.HttpMediaType.FORM_URLENCODE, 
                				ContentCoverter.formConvertAsString(paramMap3)).build();
             			HttpResponse rep = client.sendRequest(req);
            			String response3 = rep.fullBody();
             			
            			Map<?, ?> responseMap3 = JSONUtils.readAsMap(response3);
             			String resultCode = (String)responseMap3.get("result");
             			if("success".equals(resultCode)){
             				userid = (String)responseMap3.get("userid");
             			}else{
             				throw new OaException((String)responseMap3.get("message"));
             			}
             		}
             		
             		//根据员工识别号查询员工信息
         			List<Employee> oldList = employeeService.getByIdentificationNum(param.getIdentificationNum());
         			boolean reEntry = false;
         			String oldCode = register.getCode();
         			if(oldList!=null&&oldList.size()>0) {
         				reEntry = true;
         				oldCode = oldList.get(0).getCode();
         				register.setEmail(oldList.get(0).getEmail());
         			}
 
         			//录入员工信息
         			Employee employee = new Employee();
         			initBaseInfo(employee,param,register,result,user);
         			//判断入职类型（0-正常入职，1-外包/实习生转内）
         		    
         			if(register.getType()==0&&!reEntry){
         				//正常入职，会新建通行证账号
         				logger.info("创建通行证start");
         				initSSOAccount(employee,param,
         						 register,result,user,userid,OA_SERVICE_URL);
         				logger.info("创建通行证end");
         			}else{
         				//外包/实习生转内,使用老的通行证
         				User userOld = userService.getByCode(oldCode);
         				if(userOld!=null){
         					User userNew = new User();
    						userNew.setUserName(userOld.getUserName());
    						userNew.setCompanyId(user.getCompanyId());
    						userNew.setEmployeeId(employee.getId());
    						userNew.setIsLocked(0);
    						userNew.setCreateTime(new Date());
    						userNew.setCreateUser(user.getEmployee().getCnName());
    						userNew.setDelFlag(CommonPo.STATUS_NORMAL);
    						userService.save(userNew);
    					    try{
    					    	logger.info("激活员工通行证start:username="+userOld.getUserName());
    							HashMap<String, String> paramMap = new HashMap<String,String>();
    							paramMap.put("username", userOld.getUserName());
    							paramMap.put("jobStatus", "2");

         						HttpRequest req = new HttpRequest.Builder().url(ConfigConstants.ULEACCOUNT_QUIT).post(HttpRequest.HttpMediaType.FORM_URLENCODE, 
                        				ContentCoverter.formConvertAsString(paramMap)).build();
                     			HttpResponse rep = client.sendRequest(req);
                    			String response = rep.fullBody();
         						
         						Map<?, ?> responseMap = JSONUtils.readAsMap(response);
         						String resultCode = (String)responseMap.get("errorCode");
         						logger.info("激活员工通行证end:resultCode="+resultCode);
    					    }catch(Exception e){
    					    	logger.error("激活员工通行证失败:username="+userOld.getUserName());
    					    }
         				}else{
         					logger.error("员工="+register.getCnName()+"的OA通行证创建错误");
         				}
         				
         			}
         			
         			//合同制员工要单独初始化的数据
         			EmpType empType = empTypeMapper.getById(register.getEmployeeTypeId());
         			if("合同制员工".equals(empType.getTypeCName())){
         				//初始化假期数据
    					empLeaveService.generateDefaultYearLeave(employee,Integer.valueOf(DateUtils.getNow("yyyy")),register.getDelayEntryDate()!=null?register.getDelayEntryDate():register.getEmploymentDate());
    					empLeaveService.generateDefaultSickLeave(employee,Integer.valueOf(DateUtils.getNow("yyyy")),register.getDelayEntryDate()!=null?register.getDelayEntryDate():register.getEmploymentDate());
    					
    					//初始化排班数据及（排班人员生成员工组别关系数据）
    					initEmployeeClass(register,employee,user);
    					
    					//M级员工初始化后台权限
    					initPrivilegeData(register,employee,user);
    					
         			}
         			
					//发送成功邮件
					sendSuccessEmail(register,user);
					
					//绑定员工与打卡机的关系
					if(param.getFingerprintId()!=null){
						try{
							AttnUsers attnUsers = new AttnUsers();
							attnUsers.setUserid(Integer.valueOf(userid));
							attnUsers.setOaEmpId(employee.getId());
							HashMap<String, String> paramMap4 = new HashMap<String,String>();
							paramMap4.put("jsonData", JSONUtils.write(attnUsers));
							logger.info("入职关联employeeId地址:"+OA_SERVICE_URL+"/attnUsers/updateById.htm");
							
							HttpRequest req = new HttpRequest.Builder().url(OA_SERVICE_URL+"/attnUsers/updateById.htm").post(HttpRequest.HttpMediaType.FORM_URLENCODE, 
                    				ContentCoverter.formConvertAsString(paramMap4)).build();
                 			HttpResponse rep = client.sendRequest(req);
                			String response4 = rep.fullBody();

							logger.info("关联考勤机结果:"+response4);
						}catch(Exception e){
							logger.error("员工="+employee.getId()+"与打卡机关系绑定失败。。。");
						}
					}
         			
                }
                
                		//取消入职
                if("cancel".equals(param.getNodeCode())){
                	cancelEntry(param,register,user);
                }
			}
			param.setApprovalStatus(approvalStatus);
			param.setId(register.getId());
			employeeRegisterMapper.updateById(param);
			//-----------------start-----------------------保存流程节点信息
			ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
			flow.setAssigneeName(user.getEmployee().getCnName());
			flow.setDepartName(user.getDepart().getName());
			flow.setPositionName(user.getPosition().getPositionName());
			flow.setFinishTime(new Date());
			flow.setComment(comment);
			flow.setProcessId(processId);
			flow.setProcessKey(activitiServiceImpl.queryProcessKeyByDefinitionId(task.getProcessDefinitionId()));
			flow.setStatu(approvalStatus);
			viewTaskInfoService.save(flow);
			//-----------------end-------------------------
			//判断当前节点是否有办理人assignee,没有则将当前代理人设为办理人
			if(StringUtils.isBlank(task.getAssignee())) {
				activitiServiceImpl.claimTask(task.getId(), String.valueOf(user.getEmployeeId()));
			}
			activitiServiceImpl.completeTask(task.getId(),comment,null,commentType);
		}else{
			throw new OaException("流程实例为"+processId+"的入职审批数据不存在！");
		}
		return result;
	}

	@Override
	public List<EmpApplicationRegister> getByCondition(EmpApplicationRegister param) throws Exception {
		return employeeRegisterMapper.getByCondition(param);
	}

	@Override
	public void updateProcessInstanceId(EmpApplicationRegister newRegister) {
		employeeRegisterMapper.updateById(newRegister);
	}
	
	//初始化权限数据
	public void initPrivilegeData(EmpApplicationRegister register,Employee employee,User user){
		
		Position position = positionMapper.getById(register.getPositionId());
		
		if(position!=null){
			CompanyPositionLevel level = companyPositionLevelMapper.getById(position.getPositionLevelId());
			if("M".equals(level.getCode())){
				//查看员工所在部门是否存在 “直接主管的角色“，有的话赋予权限
				List<RabcRole> roleList = rabcRoleMapper.getListByDepartIdAndName(register.getDepartId(), "直接主管");
				if(roleList!=null&&roleList.size()>0){
					Long roleId = roleList.get(0).getId();
					User account = userMapper.getByEmployeeId(employee.getId());
					if(account!=null){
						RabcUserRole userRole = new RabcUserRole();
						userRole.setRoleId(roleId);
						userRole.setUserId(account.getId());
						userRole.setCreateTime(new Date());
						userRole.setCreateUser(user.getEmployee().getCnName());
						userRole.setDelFlag(0);
						rabcUserRoleMapper.save(userRole);
					}
				}
				
			}
					
		}
		
	}
	
	//初始化员工基本信息
	public void initBaseInfo(Employee employee,EmpApplicationRegister param,
			EmpApplicationRegister register,Map<String,Object> result,User user) throws Exception{
		    employee.setCompanyId(register.getCompanyId());//公司id
			employee.setEmpTypeId(register.getEmployeeTypeId());//员工类型
			employee.setWorkType(register.getWorkType());//工时种类
			employee.setWhetherScheduling(register.getWhetherScheduling());//是否排班
			employee.setReportToLeader(register.getLeader());//汇报对象
			employee.setIdentificationNum(param.getIdentificationNum());
			employee.setBirthday(register.getBirthDate());
			employee.setWorkAddressProvince(register.getWorkAddressProvince());
			employee.setWorkAddressCity(register.getWorkAddressCity());
			employee.setWorkAddressType(register.getWorkAddressType());
			employee.setPositionLevel(register.getPositionLevel());
			employee.setPositionSeq(register.getPositionSeq());
			String prefix = "SP";//员工编号前缀
			EmpType empType = empTypeMapper.getById(register.getEmployeeTypeId());
			if(empType!=null&&"外包员工".equals(empType.getTypeCName())){
				prefix = "WB";
			}else if(empType!=null&&"实习生".equals(empType.getTypeCName())){
				prefix = "IN";
			}else if(empType!=null&&"劳务制员工".equals(empType.getTypeCName())){
				prefix = "LW";
			}
	        Integer maxCode = employeeMapper.getMaxCodeByPrefix(prefix);
	        if(maxCode==null){
	        	maxCode = 0;
	        }
	        String code = prefix+generateCode(4,maxCode+1);
			employee.setCode(code);//员工编号
			employee.setCnName(register.getCnName());//员工姓名
			employee.setEngName(register.getEngName());//英文名
			employee.setEmail(register.getEmail());//邮件
			employee.setMobile(register.getMobile());//手机号
			employee.setExtensionNumber(register.getExtensionNumber());//分机号
			employee.setSex(register.getSex());
			//employee.setEntryStatus(0);//入职状态
			employee.setSeatCode(register.getSeatNo());//座位号
			CompanyFloor floor = companyFloorMapper.selectByPrimaryKey(register.getFloorId());
			if(floor!=null){
				employee.setFloorCode(floor.getFloorNum().toString());//楼层
			}
			employee.setFirstEntryTime(register.getDelayEntryDate()!=null?register.getDelayEntryDate():register.getEmploymentDate());//第一次入职时间
			employee.setJobStatus(0);//工作状态
			employee.setDelFlag(CommonPo.STATUS_NORMAL);
			employee.setVersion(0L);
			employee.setCreateUser(user.getEmployee().getCnName());
			employee.setCreateTime(new Date());
			employee.setFingerprintId(param.getFingerprintId());
			employee.setAutoCalculateLeave(1);
			employeeMapper.save(employee);
			//回传员工id
			param.setBackEmployeeId(employee.getId());
			result.put("id", employee.getId());
			result.put("name", employee.getCnName());
			//员工部门信息
			EmpDepart empDepart = new EmpDepart();
			empDepart.setDelFlag(CommonPo.STATUS_NORMAL);
			empDepart.setCreateUser(user.getEmployee().getCnName());
			empDepart.setCreateTime(new Date());
			empDepart.setDepartId(register.getDepartId());
			empDepart.setEmployeeId(employee.getId());
			empDepartMapper.save(empDepart);
			//员工职位信息
			EmpPosition empPosition = new EmpPosition();
			empPosition.setDelFlag(CommonPo.STATUS_NORMAL);
			empPosition.setCreateUser(user.getEmployee().getCnName());
			empPosition.setCreateTime(new Date());
			empPosition.setPositionId(register.getPositionId());
			empPosition.setEmployeeId(employee.getId());
			empPositionMapper.save(empPosition);
	}
	
	//生成通行证账号
	public void initSSOAccount(Employee employee,EmpApplicationRegister param,
			EmpApplicationRegister register,Map<String,Object> result,User user,String userid,String OA_SERVICE_URL) throws Exception{
		    //验证通行证是否存在
			String username = register.getEmail().split("@")[0];
			HashMap<String, String> paramMap = new HashMap<String,String>();
			paramMap.put("username", username);
			//读取公司配置表	
			List<CompanyConfig> companyConfigList = companyConfigService.getListByCode("companyCode");
			boolean isTom = false;
			if(companyConfigList!=null&&companyConfigList.size()>0){
				if("TOM".equals(companyConfigList.get(0).getDisplayCode())){
					isTom = true;
				}
			}
			logger.info("是否需要生成通行证="+!isTom);
			String resultCode = "";
			if(!isTom){
				logger.info("获取最新通行证url"+ConfigConstants.ULEACCOUNT_EXIST);
				
				HttpRequest req = new HttpRequest.Builder().url(ConfigConstants.ULEACCOUNT_EXIST).post(HttpRequest.HttpMediaType.FORM_URLENCODE, 
        				ContentCoverter.formConvertAsString(paramMap)).build();
     			HttpResponse rep = client.sendRequest(req);
    			String response = rep.fullBody();

				logger.info("获取最新通行证返回结果"+response);
				Map<?, ?> responseMap = JSONUtils.readAsMap(response);
			    resultCode = (String)responseMap.get("errorCode");
			    if("".equals(resultCode)){
			    	username =  (String)responseMap.get("username");
			    	//创建通行证
					HashMap<String, String> paramMap1 = new HashMap<String,String>();
					paramMap1.put("staffId", "-1");
					paramMap1.put("name", register.getCnName());
					paramMap1.put("username", username);
					paramMap1.put("email", register.getEmail());
					paramMap1.put("telephone", register.getMobile());
					paramMap1.put("status", "2");
					logger.info("新建通行证url="+ConfigConstants.ULEACCOUNT_ADD);
					
					HttpRequest req1 = new HttpRequest.Builder().url(ConfigConstants.ULEACCOUNT_ADD).post(HttpRequest.HttpMediaType.FORM_URLENCODE, 
	        				ContentCoverter.formConvertAsString(paramMap1)).build();
	     			HttpResponse rep1 = client.sendRequest(req1);
	    			String response1 = rep1.fullBody();

					logger.info("新建通行证返回="+response1);
					Map<?, ?> responseMap1 = JSONUtils.readAsMap(response1);
					String resultCode1 = (String)responseMap1.get("errorCode");
					if("".equals(resultCode1)){
						//创建user
						User userNew = new User();
						userNew.setUserName(username);
						userNew.setCompanyId(register.getCompanyId());
						userNew.setEmployeeId(employee.getId());
						userNew.setIsLocked(0);
						userNew.setCreateTime(new Date());
						userNew.setCreateUser(user.getEmployee().getCnName());
						userNew.setDelFlag(CommonPo.STATUS_NORMAL);
						userService.save(userNew);
					}else{
						throw new OaException("新增用户接口失败");
					}
			    }else{
					throw new OaException("验证用户存在接口-系统错误");
				}
			}
	}
	
	//发送入职成功邮件
	public void sendSuccessEmail(EmpApplicationRegister register,User user) throws Exception{
		//发送通知邮件
		List<String> emailList = new ArrayList<String>();
		Depart depart = departMapper.getById(register.getDepartId());

		//新员工逐级汇报对象到部门负责人为止
		Long reportToLeader = register.getLeader();//申请人汇报对象
		List<Long> reportToLeaderList = new ArrayList<Long>();
		int count = 0;
		while(true){
			count = count + 1;
			if(reportToLeader!=null){
				reportToLeaderList.add(reportToLeader);
				//判断是否是部门负责人
				List<Depart> departList = departMapper.getAllDepartByLeaderId(reportToLeader);
				if(departList!=null&&departList.size()>0){
					break;
				}else{
					Employee reportToLeaderObject = employeeService.getById(reportToLeader);
					reportToLeader = reportToLeaderObject!=null?reportToLeaderObject.getReportToLeader():null;
				}
			}else{
				logger.info("请假申请:申请人汇报对象为空。");
			}
			//对象关系过多，自动跳出循环，防止死循环
			if(count>10){
				break;
			}
		}
		if(reportToLeaderList!=null&&reportToLeaderList.size()>0){
			List<Employee> leaderList = employeeService.getListByIds(reportToLeaderList);
			for(Employee data:leaderList){
				emailList.add(data.getEmail());
			}
		}
		
		//发邮件给部门负责人
		if(depart!=null&&depart.getLeader()!=null&&!depart.getLeader().equals(register.getLeader())){
			Employee departLeader = employeeMapper.getById(depart.getLeader());
			emailList.add(departLeader.getEmail());
		}
		//发邮件给PMO群组:
		if(depart != null && ("117".equals(depart.getCode())||"116".equals(depart.getCode())
				||"134".equals(depart.getCode())||"135".equals(depart.getCode()))){	
			String pmoEmail = companyConfigService.getGroupEmail(ConfigConstants.GROUP_EMAIL_PMO);
			emailList.add(pmoEmail);
		}
		//发邮件给IT群组
		String itEmail = companyConfigService.getGroupEmail(ConfigConstants.GROUP_EMAIL_IT);
		emailList.add(itEmail);
		//发邮件给行政群组
		String adminEmail = companyConfigService.getGroupEmail(ConfigConstants.GROUP_EMAIL_ADMIN);
		emailList.add(adminEmail);
		//发邮件给人事群组
		String hrEmail = companyConfigService.getGroupEmail(ConfigConstants.GROUP_EMAIL_HR);
		emailList.add(hrEmail);
		//发邮件给抄送人
		//抄送人id转成邮箱
		if(StringUtils.isNotBlank(register.getToPersions())){
			List<Long> employeeIdList = new ArrayList<Long>();
			for(String employeeId:register.getToPersions().split(",")){
				employeeIdList.add(Long.valueOf(employeeId));
			}
			Employee toPersionsP = new Employee();
			toPersionsP.setIds(employeeIdList);
			List<Employee> toPersions = employeeMapper.getListByCondition(toPersionsP);
			for(Employee data:toPersions){
				emailList.add(data.getEmail());
			}
		}
		//发邮件给抄送邮件
		if(StringUtils.isNoneBlank(register.getToEmails())){
			List<String> toEmails = Arrays.asList(register.getToEmails().split(","));
			emailList.addAll(toEmails);
		}
		//去重
		List<String> emailDistinctList = emailList.stream().distinct().collect(Collectors.toList());

		sendConfirmEntryTemplet(user, register, emailDistinctList);
	}
	
	//初始化排班信息
	public void initEmployeeClass(EmpApplicationRegister register,Employee employee,User user) {
		logger.info("新员工"+register.getCnName()+"初始化常规班次开始。");
		Config whetherScheduling = configMapper.getById(register.getWhetherScheduling());
	    CompanyConfig typeOfWork = companyConfigMapper.getById(register.getWorkType());
	    if("standard".equals(typeOfWork.getDisplayCode())&&"yes".equals(whetherScheduling.getDisplayCode())){
	    	//生成员工组别关系数据
	    	if(register.getGroupId()!=null){
	    		EmpScheduleGroup scheduleGroup = new EmpScheduleGroup();
	    		scheduleGroup.setEmpId(employee.getId());
	    		scheduleGroup.setGroupId(register.getGroupId());
	    		scheduleGroup.setCreateTime(new Date());
	    		scheduleGroup.setCreateUser(user.getEmployee().getId());
	    		scheduleGroup.setDelFlag(0);
	    		scheduleGroup.setVersion(0L);
		    	scheduleGroupMapper.addMember(scheduleGroup);
	    	}
	    	//生成入职月的班次数据
	    	ClassSetting param = new ClassSetting();
	    	//param.setDepartId(register.getDepartId());
	    	param.setStartTime(DateUtils.parse("09:00:00", DateUtils.FORMAT_HH_MM_SS));
	    	param.setEndTime(DateUtils.parse("18:00:00", DateUtils.FORMAT_HH_MM_SS));
	    	List<ClassSetting> setList = classSettingMapper.getListByCondition(param);
	    	if(setList!=null&&setList.size()>0){
	    		Long classSettingId = setList.get(0).getId();
	    		//获取入职日期
	    		Date entryDate = register.getDelayEntryDate()!=null?register.getDelayEntryDate():register.getEmploymentDate();
	    		Date monthEnd = DateUtils.getLastDay(entryDate);
	    		List<EmployeeClass> initList = new ArrayList<EmployeeClass>();
	    		List<ApplicationEmployeeClassDetail> moveList = new ArrayList<ApplicationEmployeeClassDetail>();
	    		boolean isEnd = true;
			    double should_time = 0;
			    do{
			    	if(annualVacationService.judgeWorkOrNot(entryDate)){
			    		should_time += 8;
			    		EmployeeClass employeeClass = new EmployeeClass();
			    		employeeClass.setCompanyId(register.getCompanyId());
			    		employeeClass.setDepartId(register.getDepartId());
			    		employeeClass.setGroupId(register.getGroupId());
			    		employeeClass.setEmployId(employee.getId());
			    		employeeClass.setEmployName(employee.getCnName());
			    		employeeClass.setClassDate(entryDate);
			    		employeeClass.setClassSettingId(classSettingId);
			    		employeeClass.setCreateTime(new Date());
			    		employeeClass.setCreateUser(user.getEmployee().getCnName());
			    		employeeClass.setClassSettingPerson(user.getEmployee().getCnName());
			    		initList.add(employeeClass);
			    		//新记录
			    		ApplicationEmployeeClassDetail recordDetail = new ApplicationEmployeeClassDetail();
			    		recordDetail.setCompanyId(user.getCompanyId());
			    		recordDetail.setEmployId(employee.getId());
			    		recordDetail.setCreateTime(new Date());
			    		recordDetail.setCreateUser(user.getEmployee().getCnName());
			    		recordDetail.setClassSettingId(classSettingId);
			    		recordDetail.setClassSettingPerson(user.getEmployee().getCnName());
			    		recordDetail.setClassDate(entryDate);
			    		recordDetail.setDelFlag(0);
			    		recordDetail.setIsMove(1);
			    		
			    		recordDetail.setEmployName(employee.getCnName());
			    		moveList.add(recordDetail);
			    	    //老记录
			    		ApplicationEmployeeClassDetail oldRecordDetail = new ApplicationEmployeeClassDetail();
			    		oldRecordDetail.setCompanyId(user.getCompanyId());
			    		oldRecordDetail.setEmployId(employee.getId());
			    		oldRecordDetail.setCreateTime(new Date());
			    		oldRecordDetail.setCreateUser(user.getEmployee().getCnName());
			    		oldRecordDetail.setClassSettingId(null);
			    		oldRecordDetail.setClassSettingPerson(user.getEmployee().getCnName());
			    		oldRecordDetail.setClassDate(entryDate);
			    		oldRecordDetail.setDelFlag(0);
			    		oldRecordDetail.setIsMove(0);
			    		oldRecordDetail.setEmployName(employee.getCnName());
			    		moveList.add(oldRecordDetail);
					}
			    	entryDate = DateUtils.addDay(entryDate, 1);
					if(DateUtils.getIntervalDays(entryDate, monthEnd) < 0) {
						isEnd = false;
					}
				} while(isEnd);
	    		for(EmployeeClass data:initList){
	    			data.setShouldTime(should_time);
	    		}
	    		
	    		if(initList!=null&&initList.size()>0){
	    			logger.info("新员工"+register.getCnName()+"初始化常规班次"+initList.size()+"条数据。");
	    			employeeClassMapper.batchSave(initList);
	    		}
	    		
	    		//生成调班记录
	    		Depart depart = departMapper.getById(register.getDepartId());
	    		ApplicationEmployeeClass record = new ApplicationEmployeeClass();
	    		record.setDepartId(register.getDepartId());
	    		record.setDepartName(depart!=null?depart.getName():null);
	    		record.setClassMonth(DateUtils.getFirstDay(employee.getFirstEntryTime()));
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
	    		record.setGroupId(register.getGroupId());
	    		record.setMoveType(2);//入职申请
	    		record.setRemark("入职申请");
	    		applicationEmployeeClassMapper.save(record);
	    	    
                for(ApplicationEmployeeClassDetail recordDetail:moveList){
                	recordDetail.setAttnApplicationEmployClassId(record.getId());
		    		recordDetail.setShouldTime(should_time);
	    		}
                if(moveList!=null&&moveList.size()>0){
                	applicationEmployeeClassDetailMapper.batchSave(moveList);
                }
	    		logger.info("新员工"+register.getCnName()+"初始化常规班次结束。");
	    	}else{
	    		logger.info("新员工"+register.getCnName()+"所在部门没有09:00-18:00的班次,暂时不初始化班次。");
	    	}
	    }else{
	    	logger.info("新员工"+register.getCnName()+"工时类型和是否排班不符合条件，不初始化班次。");
	    }
		
	}
	
	//取消入职
	public void cancelEntry(EmpApplicationRegister param,
			EmpApplicationRegister register,User user) throws Exception{
		param.setHrCooperateUser(user.getEmployee().getCnName());
    	param.setHrCooperateDate(new Date());
    	param.setProcessStatus(EmpApplicationRegister.PROCESS_STATUS_YES);
    	param.setEntryStatus(EmpApplicationRegister.ENTRY_STATUS_NO);
    	
    	//取消入职发送通知
		List<String> positionNames = new ArrayList<String>();
		positionNames.add("企业文化专员");positionNames.add("人力资源高级经理");positionNames.add("HRBP专员");
		positionNames.add("人力资源经理");positionNames.add("高级HRBP");positionNames.add("HRBP");positionNames.add("HRBP助理");
		positionNames.add("人力资源助理");positionNames.add("IT主管");positionNames.add("IT高级专员");positionNames.add("行政经理");
		positionNames.add("行政专员");positionNames.add("行政前台");positionNames.add("IT经理");
		EmpPosition empPosition = new EmpPosition();
		empPosition.setPositionNames(positionNames);
		List<EmpPosition> empPositionList = empPositionMapper.getListByPositionName(empPosition);
		List<String> emailList = new ArrayList<String>();
		for(EmpPosition emp:empPositionList){
			//发送消息
			sendMsg(emp.getEmployeeId(),user,"新员工取消入职提醒","新员工"+register.getCnName()+"已取消入职，请各部门知晓");
		}
		sendMsg(register.getLeader(),user,"新员工取消入职提醒","新员工"+register.getCnName()+"已取消入职，请各部门知晓");
		
		//新员工逐级汇报对象到部门负责人为止
		Long reportToLeader = register.getLeader();//申请人汇报对象
		List<Long> reportToLeaderList = new ArrayList<Long>();
		int count = 0;
		while(true){
			count = count + 1;
			if(reportToLeader!=null){
				reportToLeaderList.add(reportToLeader);
				//判断是否是部门负责人
				List<Depart> departList = departMapper.getAllDepartByLeaderId(reportToLeader);
				if(departList!=null&&departList.size()>0){
					break;
				}else{
					Employee reportToLeaderObject = employeeService.getById(reportToLeader);
					reportToLeader = reportToLeaderObject!=null?reportToLeaderObject.getReportToLeader():null;
				}
			}else{
				logger.info("请假申请:申请人汇报对象为空。");
			}
			//对象关系过多，自动跳出循环，防止死循环
			if(count>10){
				break;
			}
		}
		if(reportToLeaderList!=null&&reportToLeaderList.size()>0){
			List<Employee> leaderList = employeeService.getListByIds(reportToLeaderList);
			for(Employee data:leaderList){
				emailList.add(data.getEmail());
			}
		}
		
		Depart depart = departMapper.getById(register.getDepartId());
		if(depart!=null&&depart.getLeader()!=null&&!depart.getLeader().equals(register.getLeader())){
			sendMsg(depart.getLeader(),user,"新员工取消入职提醒","新员工"+register.getCnName()+"已取消入职，请各部门知晓");
			Employee receiverDh = employeeMapper.getById(depart.getLeader());
			if(receiverDh!=null){
				emailList.add(receiverDh.getEmail());
			}
		}
		//发送邮件给PMO,IT,行政,人事
		//查询群组email
		//发邮件给PMO群组:
		if(depart != null && ("117".equals(depart.getCode())||"116".equals(depart.getCode())
				||"134".equals(depart.getCode())||"135".equals(depart.getCode()))){	
			String pmoEmail = companyConfigService.getGroupEmail(ConfigConstants.GROUP_EMAIL_PMO);
			emailList.add(pmoEmail);
		}
		//发邮件给IT群组
		String itEmail = companyConfigService.getGroupEmail(ConfigConstants.GROUP_EMAIL_IT);
		emailList.add(itEmail);
		//发邮件给行政群组
		String adminEmail = companyConfigService.getGroupEmail(ConfigConstants.GROUP_EMAIL_ADMIN);
		emailList.add(adminEmail);
		//发邮件给人事群组
		String hrEmail = companyConfigService.getGroupEmail(ConfigConstants.GROUP_EMAIL_HR);
		emailList.add(hrEmail);
		//发邮件给抄送人
		//抄送人id转成邮箱
		if(StringUtils.isNotBlank(register.getToPersions())){
			List<Long> employeeIdList = new ArrayList<Long>();
			for(String employeeId:register.getToPersions().split(",")){
				employeeIdList.add(Long.valueOf(employeeId));
			}
			Employee toPersionsP = new Employee();
			toPersionsP.setIds(employeeIdList);
			List<Employee> toPersions = employeeMapper.getListByCondition(toPersionsP);
			for(Employee data:toPersions){
				emailList.add(data.getEmail());
			}
		}
		//发邮件给抄送邮件
		if(StringUtils.isNoneBlank(register.getToEmails())){
			List<String> toEmails = Arrays.asList(register.getToEmails().split(","));
			emailList.addAll(toEmails);
		}
		//去重
		List<String> emailDistinctList = emailList.stream().distinct().collect(Collectors.toList());

		sendCancelEntryMail(user, register, emailDistinctList);

	}
	
	//撤销入职
	@SuppressWarnings("unused")
	@Override
	public void backEntry(String processId, String taskId, String message) throws OaException {
		User user = userService.getCurrentUser();
		EmpApplicationRegister param=new EmpApplicationRegister();
		EmpApplicationRegister register = employeeRegisterMapper.queryByProcessInstanceId(processId);
		if(register==null){
			throw new OaException("流程实例为"+processId+"的入职撤销数据不存在！");
		}
		param.setId(register.getId());
		param.setApprovalStatus(ConfigConstants.BACK_STATUS);
		param.setEntryStatus(EmpApplicationRegister.ENTRY_STATUS_NO);		
		Task task = activitiServiceImpl.getTaskById(taskId);
		//把状态改为已撤销
		param.setEntryStatus(4);
		employeeRegisterMapper.updateById(param);
		//-----------------start-----------------------保存流程节点信息
		ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
		flow.setAssigneeName(user.getEmployee().getCnName());
		flow.setDepartName(user.getDepart().getName());
		flow.setPositionName(user.getPosition().getPositionName());
		flow.setFinishTime(new Date());
		flow.setComment(message);
		flow.setProcessId(processId);
		flow.setProcessKey(activitiServiceImpl.queryProcessKeyByDefinitionId(task.getProcessDefinitionId()));
		flow.setStatu(ConfigConstants.BACK_STATUS);
		viewTaskInfoService.save(flow);
		//-----------------end-------------------------
		//判断当前节点是否有办理人assignee,没有则将当前代理人设为办理人
		if(StringUtils.isBlank(task.getAssignee())) {
			activitiServiceImpl.claimTask(task.getId(), String.valueOf(user.getEmployeeId()));
		}
		activitiServiceImpl.completeTask(task.getId(),message,null,ConfigConstants.BACK);
	}
	
	/**
	 * 读取模板文件
	 * @param params
	 * @param templetPropertie
	 * @return
	 * @throws IOException
	 */
	@Override
	public String readEmailTemplet(String[] params, String templetPropertie) throws IOException {
		Properties prop = new Properties();
		InputStream in = this.getClass().getClassLoader().getResourceAsStream(
		        "emailTemplet/emailContent.properties");
		BufferedReader bf = new BufferedReader(new InputStreamReader(in,"UTF-8"));  
		prop.load(bf);
		//从配置文件中读取模板字符串替换
		String msg=MessageFormat.format(prop.getProperty(templetPropertie),params);
		logger.info("WillSendMail"+msg);
		return msg;
	}
	/**
	 * 入职申请发送邮件
	 * @param employeeRegister
	 * @param user
	 * @param leader
	 * @param company
	 * @param empType
	 * @param position
	 * @param depart
	 * @throws IOException
	 */
	private void sendEntryMail(EmpApplicationRegister employeeRegister, User user) throws IOException {
		
		//给汇报人和部门负责人发消息和邮件
		String text = "新员工"+employeeRegister.getCnName()+"将会在"+DateUtils.format(employeeRegister.getEmploymentDate(),"yyyy年MM月dd日")+"加入本部门，请做好工作安排等相关事宜。";
		sendMsg(employeeRegister.getLeader(),user,"新员工入职提醒",text);
		//汇报对象
		Employee leader = employeeMapper.getById(employeeRegister.getLeader());
		//公司
		Company company = companyMapper.getById(employeeRegister.getCompanyId());
		//员工类型
		EmpType empType = empTypeMapper.getById(employeeRegister.getEmployeeTypeId());
		//职位
		Position position = positionMapper.getById(employeeRegister.getPositionId());
		//部门负责人
		Depart depart = departMapper.getById(employeeRegister.getDepartId());
		
		//发送邮件集合
		List<String> emailList = new ArrayList<String>();
		//发送邮件给汇报对象
		if(leader!=null&&StringUtils.isNotBlank(leader.getEmail())){
			emailList.add(leader.getEmail());
		}
		
		//新员工逐级汇报对象到部门负责人为止
		Long reportToLeader = leader.getReportToLeader();//申请人汇报对象
		List<Long> reportToLeaderList = new ArrayList<Long>();
		int count = 0;
		while(true){
			count = count + 1;
			if(reportToLeader!=null){
				reportToLeaderList.add(reportToLeader);
				//判断是否是部门负责人
				List<Depart> departList = departMapper.getAllDepartByLeaderId(reportToLeader);
				if(departList!=null&&departList.size()>0){
					break;
				}else{
					Employee reportToLeaderObject = employeeService.getById(reportToLeader);
					reportToLeader = reportToLeaderObject!=null?reportToLeaderObject.getReportToLeader():null;
				}
			}else{
				logger.info("请假申请:申请人汇报对象为空。");
			}
			//对象关系过多，自动跳出循环，防止死循环
			if(count>10){
				break;
			}
		}
		if(reportToLeaderList!=null&&reportToLeaderList.size()>0){
			List<Employee> leaderList = employeeService.getListByIds(reportToLeaderList);
			for(Employee data:leaderList){
				emailList.add(data.getEmail());
			}
		}
		
		//发送邮件给部门负责人
		if(depart!=null&&depart.getLeader()!=null&&!depart.getLeader().equals(employeeRegister.getLeader())){
			sendMsg(depart.getLeader(),user,"新员工入职提醒",text);
			Employee departLeader = employeeMapper.getById(depart.getLeader());
			emailList.add(departLeader.getEmail());
		}
		//发送邮件给PMO,IT,行政,人事
		//查询群组email
		//发邮件给PMO群组:
		//技术开发部入职发送(117)
		if(depart != null && ("117".equals(depart.getCode())||"116".equals(depart.getCode())
				||"134".equals(depart.getCode())||"135".equals(depart.getCode()))){
			String pmoEmail = companyConfigService.getGroupEmail(ConfigConstants.GROUP_EMAIL_PMO);
			emailList.add(pmoEmail);
		}
		//发邮件给IT群组:
		String itEmail = companyConfigService.getGroupEmail(ConfigConstants.GROUP_EMAIL_IT);
		emailList.add(itEmail);
		//发邮件给行政群组
		String adminEmail = companyConfigService.getGroupEmail(ConfigConstants.GROUP_EMAIL_ADMIN);
		emailList.add(adminEmail);
		//发邮件给人事群组
		String hrEmail = companyConfigService.getGroupEmail(ConfigConstants.GROUP_EMAIL_HR);
		emailList.add(hrEmail);
		//发邮件给抄送人
		//抄送人id转成邮箱
		if(StringUtils.isNotBlank(employeeRegister.getToPersions())){
			List<Long> employeeIdList = new ArrayList<Long>();
			for(String employeeId:employeeRegister.getToPersions().split(",")){
				employeeIdList.add(Long.valueOf(employeeId));
			}
			Employee toPersionsP = new Employee();
			toPersionsP.setIds(employeeIdList);
			List<Employee> toPersions = employeeMapper.getListByCondition(toPersionsP);
			for(Employee data:toPersions){
				emailList.add(data.getEmail());
			}
		}
		//发邮件给抄送邮件
		if(StringUtils.isNoneBlank(employeeRegister.getToEmails())){
			List<String> toEmails = Arrays.asList(employeeRegister.getToEmails().split(","));
			emailList.addAll(toEmails);
		}
		//去重
		List<String> emailDistinctList = emailList.stream().distinct().collect(Collectors.toList());
		
		//发送邮件封装参数
		//日期转化
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String entryTimeStr = sdf.format(employeeRegister.getEmploymentDate());
		String nowDateStr = sdf.format(new Date());
		String usedMO = "合同制员工".equals(empType.getTypeCName()) ? "是" : "否";
		String params[]={
				StringUtils.isEmpty(company.getName()) ? "" : company.getName(),
				StringUtils.isEmpty(employeeRegister.getCnName()) ? "" : employeeRegister.getCnName(),
				StringUtils.isEmpty(employeeRegister.getEngName())? "" : employeeRegister.getEngName(),
				StringUtils.isEmpty(empType.getTypeCName()) ? "" : empType.getTypeCName(),
				StringUtils.isEmpty(entryTimeStr) ? "" : entryTimeStr,
				StringUtils.isEmpty(depart.getName()) ? "" : depart.getName(),
				StringUtils.isEmpty(position.getPositionName()) ? "" : position.getPositionName(),
				StringUtils.isEmpty(leader.getCnName()) ? "" : leader.getCnName(),
				usedMO,
				StringUtils.isEmpty(employeeRegister.getRemarks()) ? "" : employeeRegister.getRemarks(),
				nowDateStr,
				user.getEmployee().getCnName(),
				user.getDepart().getName()
				};
		String templetPropertie = "entryTemplet";
		String msg = readEmailTemplet(params, templetPropertie);
      
		List<SendMail> sendMailList = new ArrayList<SendMail>();
		for (String receiverEmail : emailDistinctList) {
			SendMail sendMail = new SendMail();
			sendMail.setReceiver(receiverEmail);
			if("外包员工".equals(empType.getTypeCName())){
				sendMail.setSubject("新员工入职通知——"+employeeRegister.getCnName()+"(外包)");
			}else if("实习生".equals(empType.getTypeCName())){
				sendMail.setSubject("新员工入职通知——"+employeeRegister.getCnName()+"(实习生)");
			}else{
				sendMail.setSubject("新员工入职通知——"+employeeRegister.getCnName());
			}
			sendMail.setSendStatus(SendMail.SEND_STATUS_NO);
			sendMail.setText(msg);
			sendMail.setOaMail(SendMail.OA_MAIL_P);
			sendMailList.add(sendMail);
		}
		
		//员工识别号是否需要复用邮件
		Map<String,Object> identificationNumMap =  employeeService.getIdentificationNum(employeeRegister.getCnName(), employeeRegister.getBirthDate());
		if((Boolean) identificationNumMap.get("isRepeat")){
			
			List<Employee> list = (List<Employee>) identificationNumMap.get("repeatList");
			
			SendMail sendMail = new SendMail();
			sendMail.setReceiver(hrEmail);
			sendMail.setSubject("员工识别号复用提醒("+identificationNumMap.get("num")+")");
			sendMail.setSendStatus(SendMail.SEND_STATUS_NO);
			
			String sendText = depart.getName()+"待入职员工  "+employeeRegister.getCnName()+"，出生日期："
					+ DateUtils.format(employeeRegister.getBirthDate(), DateUtils.FORMAT_SHORT_CN) +"，与已离职的"
					+ list.get(0).getCnName() +"（员工编号："+list.get(0).getCode()+"，员工识别号："+list.get(0).getIdentificationNum()+"）"
					+ "系统判断为同一人，请HR做出准确判断；若非同一人，可在“确认入职”界面修改员工识别号后提交。";
			
			sendMail.setText(sendText);
			sendMail.setOaMail(SendMail.OA_MAIL_P);
			sendMailList.add(sendMail);
			//发送给IT
			SendMail sendMail_IT = new SendMail();
			sendMail_IT.setReceiver(itEmail);
			sendMail_IT.setSubject("员工识别号复用提醒("+identificationNumMap.get("num")+")");
			sendMail_IT.setSendStatus(SendMail.SEND_STATUS_NO);
			sendMail_IT.setText(sendText);
			sendMail_IT.setOaMail(SendMail.OA_MAIL_P);
			sendMailList.add(sendMail_IT);
			
			//发送给PMO
			if(depart != null && ("117".equals(depart.getCode())||"116".equals(depart.getCode())
					||"134".equals(depart.getCode())||"135".equals(depart.getCode()))){
				
				SendMail sendMail_PMO = new SendMail();
				sendMail_PMO.setReceiver(adminEmail);
				sendMail_PMO.setSubject("员工识别号复用提醒("+identificationNumMap.get("num")+")");
				sendMail_PMO.setSendStatus(SendMail.SEND_STATUS_NO);
				sendMail_PMO.setText(sendText);
				sendMail_PMO.setOaMail(SendMail.OA_MAIL_P);
				sendMailList.add(sendMail_PMO);
			}
		}
		
		if(sendMailList!=null&&sendMailList.size()>0){
			sendMailService.batchSave(sendMailList);
		}
	}
	/**
	 * 延期入职发送邮件
	 * @param user
	 * @param register
	 * @param receiver
	 * @param delayEntryDate 
	 * @return
	 * @throws IOException
	 */
	private void sendDelayEntryMail(User user, EmpApplicationRegister register, List<String> receiverEmailList, Date delayEntryDate)
			throws IOException {
		//发送邮件封装参数
		//日期转化
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String oldEntryDateStr = sdf.format(register.getEmploymentDate());
		String delayEntryDateStr = sdf.format(delayEntryDate);
		String nowDateStr = sdf.format(new Date());
		//汇报对象
		Employee leader = employeeMapper.getById(register.getLeader());
		//公司
		Company company = companyMapper.getById(register.getCompanyId());
		//员工类型
		EmpType empType = empTypeMapper.getById(register.getEmployeeTypeId());
		//职位
		Position position = positionMapper.getById(register.getPositionId());
		//部门负责人
		Depart depart = departMapper.getById(register.getDepartId());
		String usedMO = "合同制员工".equals(empType.getTypeCName()) ? "是" : "否";
		String params[]={
				StringUtils.isEmpty(oldEntryDateStr) ? "" : oldEntryDateStr,
				StringUtils.isEmpty(company.getName()) ? "" : company.getName(),
				StringUtils.isEmpty(register.getCnName()) ? "" : register.getCnName(),
				StringUtils.isEmpty(register.getEngName())? "" : register.getEngName(),
				StringUtils.isEmpty(empType.getTypeCName()) ? "" : empType.getTypeCName(),
				StringUtils.isEmpty(delayEntryDateStr) ? "" : delayEntryDateStr,
				StringUtils.isEmpty(depart.getName()) ? "" : depart.getName(),
				StringUtils.isEmpty(position.getPositionName()) ? "" : position.getPositionName(),
				StringUtils.isEmpty(leader.getCnName()) ? "" : leader.getCnName(),
				usedMO,
				StringUtils.isEmpty(register.getRemarks()) ? "" : register.getRemarks(),
				nowDateStr,
				user.getEmployee().getCnName(),
				user.getDepart().getName()
				};
		String templetPropertie = "delayEntryTemplet";
		String msg = readEmailTemplet(params, templetPropertie);
		List<SendMail> sendMailList = new ArrayList<SendMail>();	      
		for (String receiverEmail : receiverEmailList) {
			SendMail sendMail = new SendMail();
			sendMail.setReceiver(receiverEmail);
			if("外包员工".equals(empType.getTypeCName())){
				sendMail.setSubject("新员工入职通知更新——"+register.getCnName()+"(外包)");
			}else if("实习生".equals(empType.getTypeCName())){
				sendMail.setSubject("新员工入职通知更新——"+register.getCnName()+"(实习生)");
			}else{
				sendMail.setSubject("新员工入职通知更新——"+register.getCnName());
			}
			sendMail.setSendStatus(SendMail.SEND_STATUS_NO);
			sendMail.setText(msg);
			sendMail.setOaMail(SendMail.OA_MAIL_P);
			sendMailList.add(sendMail);
		}
		if(sendMailList!=null&&sendMailList.size()>0){
			sendMailService.batchSave(sendMailList);
		}
	}
	/**
	 * 取消入职发送邮件
	 * @param user
	 * @param register
	 * @param receiver
	 * @return
	 * @throws IOException
	 */
	private void sendCancelEntryMail(User user, EmpApplicationRegister register, List<String> receiverEmailList)
			throws IOException {
		//发送邮件封装参数
		//日期转化
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String nowDateStr = sdf.format(new Date());
		//查询员工部门
		Depart depart = departMapper.getById(register.getDepartId());
		//员工类型
		EmpType empType = empTypeMapper.getById(register.getEmployeeTypeId());
		String empTypeName = "新员工";
		if("外包员工".equals(empType.getTypeCName())){
			empTypeName = "新外包员工";
		}else if("实习生".equals(empType.getTypeCName())){
			empTypeName = "实习生";
		}
		String params[]={
				depart.getName(),
				empTypeName,
				register.getCnName(),
				nowDateStr,
				user.getEmployee().getCnName(),
				user.getDepart().getName()
				};
		String templetPropertie = "cancelEntryTemplet";
		String msg = readEmailTemplet(params, templetPropertie);
		
		List<SendMail> sendMailList = new ArrayList<SendMail>();	
		for (String receiverEmail : receiverEmailList) {
			SendMail sendMail = new SendMail();
			sendMail.setReceiver(receiverEmail);
			if("外包员工".equals(empType.getTypeCName())){
				sendMail.setSubject("新员工入职取消——"+register.getCnName()+"(外包)");
			}else if("实习生".equals(empType.getTypeCName())){
				sendMail.setSubject("新员工入职取消——"+register.getCnName()+"(实习生)");
			}else{
				sendMail.setSubject("新员工入职取消——"+register.getCnName());
			}
			sendMail.setSendStatus(SendMail.SEND_STATUS_NO);
			sendMail.setText(msg);
			sendMail.setOaMail(SendMail.OA_MAIL_P);
			sendMailList.add(sendMail);
		}
		if(sendMailList!=null&&sendMailList.size()>0){
			sendMailService.batchSave(sendMailList);
		}
	}
	/**
	 * 确认入职发送邮件
	 * @param user
	 * @param register
	 * @param receiver
	 * @return
	 * @throws IOException
	 */
	private void sendConfirmEntryTemplet(User user, EmpApplicationRegister register, List<String> receiverEmailList)
			throws IOException {
		//发送邮件封装参数
		//日期转化
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String entryTimeStr = sdf.format(register.getEmploymentDate());
		String nowDateStr = sdf.format(new Date());
		//汇报对象
		Employee leader = employeeMapper.getById(register.getLeader());
		//公司
		Company company = companyMapper.getById(register.getCompanyId());
		//员工类型
		EmpType empType = empTypeMapper.getById(register.getEmployeeTypeId());
		//职位
		Position position = positionMapper.getById(register.getPositionId());
		//部门负责人
		Depart depart = departMapper.getById(register.getDepartId());
		String usedMO = "合同制员工".equals(empType.getTypeCName()) ? "是" : "否";
		String params[]={
				StringUtils.isEmpty(company.getName()) ? "" : company.getName(),
				StringUtils.isEmpty(register.getCnName()) ? "" : register.getCnName(),
				StringUtils.isEmpty(register.getEngName())? "" : register.getEngName(),
				StringUtils.isEmpty(empType.getTypeCName()) ? "" : empType.getTypeCName(),
				StringUtils.isEmpty(entryTimeStr) ? "" : entryTimeStr,
				StringUtils.isEmpty(depart.getName()) ? "" : depart.getName(),
				StringUtils.isEmpty(position.getPositionName()) ? "" : position.getPositionName(),
				StringUtils.isEmpty(leader.getCnName()) ? "" : leader.getCnName(),
				usedMO,
				StringUtils.isEmpty(register.getSeatNo()) ? "" : register.getSeatNo(),
				StringUtils.isEmpty(register.getExtensionNumber()) ? "" : register.getExtensionNumber(),
				StringUtils.isEmpty(register.getEmail()) ? "" : register.getEmail(),
				StringUtils.isEmpty(register.getRemarks()) ? "" : register.getRemarks(),
				nowDateStr,
				user.getEmployee().getCnName(),
				user.getDepart().getName()
				};
		String templetPropertie = "confirmEntryTemplet";
		String msg = readEmailTemplet(params, templetPropertie);
		
		List<SendMail> sendMailList = new ArrayList<SendMail>();	
		for (String receiverEmail : receiverEmailList) {
			SendMail sendMail = new SendMail();
			sendMail.setReceiver(receiverEmail);
			if("外包员工".equals(empType.getTypeCName())){
				sendMail.setSubject("新员工到岗通知——"+register.getCnName()+"(外包)");
			}else if("实习生".equals(empType.getTypeCName())){
				sendMail.setSubject("新员工到岗通知——"+register.getCnName()+"(实习生)");
			}else{
				sendMail.setSubject("新员工到岗通知——"+register.getCnName());
			}
			sendMail.setSendStatus(SendMail.SEND_STATUS_NO);
			sendMail.setText(msg);
			sendMail.setOaMail(SendMail.OA_MAIL_P);
			sendMailList.add(sendMail);
		}
		if(sendMailList!=null&&sendMailList.size()>0){
			sendMailService.batchSave(sendMailList);
		}
	}
	
	public static String generateCode(Integer length, Integer number) {
		String f = "%0" + length + "d";
		return String.format(f, number);
	}

}
