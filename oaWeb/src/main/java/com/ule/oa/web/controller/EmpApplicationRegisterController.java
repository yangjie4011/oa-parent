package com.ule.oa.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.flowable.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.google.common.collect.Maps;
import com.ule.arch.basehttpclient.BaseHttpClientFactory;
import com.ule.arch.basehttpclient.client.BaseHttpClient;
import com.ule.arch.basehttpclient.model.HttpCallBack;
import com.ule.arch.basehttpclient.model.HttpRequest;
import com.ule.arch.basehttpclient.model.HttpResponse;
import com.ule.arch.basehttpclient.standard.util.ContentCoverter;
import com.ule.oa.base.cache.ConfigCacheManager;
import com.ule.oa.base.po.AttnSignRecord;
import com.ule.oa.base.po.CompanyFloor;
import com.ule.oa.base.po.EmpApplicationRegister;
import com.ule.oa.base.po.EmpDepart;
import com.ule.oa.base.po.EmpType;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.HiActinst;
import com.ule.oa.base.po.RunTask;
import com.ule.oa.base.po.TransNormal;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.AttnSignRecordService;
import com.ule.oa.base.service.CompanyFloorService;
import com.ule.oa.base.service.EmpApplicationRegisterService;
import com.ule.oa.base.service.EmpTypeService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.HiActinstService;
import com.ule.oa.base.service.RunTaskService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.service.impl.ActivitiServiceImpl;
import com.ule.oa.base.vo.TaskVO;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.RedisUtils;
import com.ule.oa.common.utils.UUIDGenerator;
import com.ule.oa.common.utils.json.JSONUtils;
import com.ule.oa.web.util.ReturnUrlUtil;
import com.ule.oa.web.util.Token;

/**
  * @ClassName: EmpApplicationRegisterController
  * @Description: 入职登记控制层
  * @author yangjie
  * @date 2017年10月6日 上午10:11:45
 */
@Controller
@RequestMapping("employeeRegister")
public class EmpApplicationRegisterController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private EmpApplicationRegisterService employeeRegisterService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private UserService userService;
	@Autowired
	private HiActinstService hiActinstService;
	@Autowired
	private RunTaskService runTaskService;
	@Resource
	private ConfigCacheManager configCacheManager;
	@Autowired
	private ActivitiServiceImpl activitiServiceImpl;
	@Autowired
	private AttnSignRecordService attnSignRecordService;
	@Autowired
	private CompanyFloorService companyFloorService;
	@Autowired
	private EmpTypeService empTypeService;
	
	private static BaseHttpClient client = BaseHttpClientFactory.getClient();
	
	//新员工入职登记页面
	@RequestMapping("/toRegister.htm")
	@Token(generate=true)//生成token
	public String toEmployeeRegister(HttpServletRequest request, String urlType){
		request.setAttribute("returnUrl", ReturnUrlUtil.getReturnUrl(urlType));
		return "base/empApplicationRegister/employee_register";
	}
	
	//新员工入职登记
	@ResponseBody
	@RequestMapping("/save.htm")
	@Token(remove = true)
	public Map<String, Object> save(HttpServletRequest request,EmpApplicationRegister emp){
		User user = userService.getCurrentUser();
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			map = employeeRegisterService.save(emp,user);
		}catch(Exception e){
			logger.error(user.getEmployee().getCnName()+"入职-提交："+e.toString());
			String uuId = UUIDGenerator.generate();
            try {
				RedisUtils.setString(String.valueOf(user.getEmployee().getId()),uuId,1800000);
				map.put("token", uuId);
			} catch (Exception e1) {
				logger.error("新员工入职申请："+e1.toString());
			}
			map.put("repeatFlag", false);
			map.put("success",false);
			map.put("message",e.getMessage());
			return map;
		}
		return map;
	}
	
	//新员工入职登记处理页面
	@RequestMapping("/toHandle.htm")
	@Token(generate=true)//生成token
	public String toEmployeeHandle(HttpServletRequest request,String flag,Long id, String urlType,String taskId){
		User user = userService.getCurrentUser();
		try {
			//1-行政,2-it,3-人事
			if(id != null){
				EmpApplicationRegister emp = employeeRegisterService.getById(id);
				request.setAttribute("emp", emp);
				//外包/实习生转内，需限制最早入职日期
				if(emp.getType()==1){
					Employee old = employeeService.getEmployeeByCode(emp.getCode());
					//入职日期必须大于离职日期
					request.setAttribute("fristEntryTime", old.getQuitTime()!=null?DateUtils.format(DateUtils.addDay(old.getQuitTime(), 1), DateUtils.FORMAT_SHORT):null);
				}
				//查询楼层
				if(emp.getType()==1&&emp.getFloorId()!=null){
					CompanyFloor floor = companyFloorService.getById(emp.getFloorId());
					request.setAttribute("floorNum",floor!=null?floor.getFloorNum():"");
				}
 				List<Task> taskList = activitiServiceImpl.getInstanceTasks(emp.getProcessInstanceId());
 				String taskName = "";
 				for(Task task:taskList){
					if(task.getId().equals(taskId)){
						emp.setNodeCode(task.getTaskDefinitionKey());
					}
					String isPersonnel=task.getName().equals("人事")?"yes":"no";
					request.setAttribute("isPersonnel", isPersonnel);
					taskName += task.getName()+",";
				}
 				if((taskId==null||"".equals(taskId))&&(taskList!=null&&taskList.size()>0)){
 					taskId = taskList.get(0).getId();
 				}
 				
 				//如果任务没有结束，需生成员工识别号
 				if(taskList!=null&&taskList.size()>0){
 					Map<String,Object> identificationNumMap =  employeeService.getIdentificationNum(emp.getCnName(), emp.getBirthDate());
 					String identificationNum = (String) identificationNumMap.get("num");
 					Boolean isRepeat =  (Boolean) identificationNumMap.get("isRepeat");
 					request.setAttribute("isRepeat", isRepeat);
 					if(isRepeat) {
 						List<Employee> oldList = (List<Employee>) identificationNumMap.get("repeatList");
 						if(oldList!=null&&oldList.size()>0) {
 							request.setAttribute("email",oldList.get(0).getEmail());
 						}
 					}
 					request.setAttribute("identificationNum", identificationNum);
 					String prefix = "SP";
 					List<EmpType> typeList = empTypeService.getListByCondition(null);
 					Map<Long,String> typeMap = typeList.stream().collect(Collectors.toMap(EmpType :: getId, EmpType:: getTypeCName));
 					if(typeMap!=null&&typeMap.containsKey(emp.getEmployeeTypeId())){
 						if("外包员工".equals(typeMap.get(emp.getEmployeeTypeId()))){
 							prefix = "WB";
 						}else if("实习生".equals(typeMap.get(emp.getEmployeeTypeId()))){
 							prefix = "IN";
 						}else if("劳务制员工".equals(typeMap.get(emp.getEmployeeTypeId()))){
 							prefix = "LW";
 						}
 					}
 					String code = employeeService.getMaxCode(prefix);
 					request.setAttribute("code", code);
 				}
 				
				TaskVO taskVO = new TaskVO();
				taskVO.setActName("".equals(taskName)?"":taskName.substring(0, taskName.length()-1));
				taskVO.setProcessId(emp.getProcessInstanceId());
				request.setAttribute("taskVO", taskVO);
 			    request.setAttribute("isSelf", false);
 			    request.setAttribute("canApprove", false);
 				//审批显示
 				if(emp.getApprovalStatus().intValue()==100) {
 					if(emp.getEmployeeId().longValue()==user.getEmployeeId().longValue()){
 						request.setAttribute("isSelf", true);
 					}
 					if(StringUtils.equalsIgnoreCase("can", flag)) {
 						request.setAttribute("canApprove", true);
 					}
 				}
				if("企业文化专员".equals(user.getPosition().getPositionName())||"人力资源高级经理".equals(user.getPosition().getPositionName())||"HRBP专员".equals(user.getPosition().getPositionName())||"人力资源及行政总监".equals(user.getPosition().getPositionName())||"人力资源经理".equals(user.getPosition().getPositionName())
		    			||"高级HRBP".equals(user.getPosition().getPositionName())||"HRBP".equals(user.getPosition().getPositionName())
		    			||"HRBP助理".equals(user.getPosition().getPositionName())||"人力资源助理".equals(user.getPosition().getPositionName())
		    			||"薪资福利高级专员".equals(user.getPosition().getPositionName())||"高级企业文化专员".equals(user.getPosition().getPositionName())){
		    		request.setAttribute("mobile",emp.getMobile());
		    	}else{
		    		request.setAttribute("mobile",emp.getMobile().substring(0,3)+"******");
		    	}
			}
			request.setAttribute("returnUrl", ReturnUrlUtil.getReturnUrl(urlType));
			request.setAttribute("taskId", taskId);
		} catch (Exception e) {
			logger.error(user.getEmployee().getCnName()+"入职-审批页面："+e.toString());
			return "base/empApplicationRegister/employee_register_handle";
		}
		return "base/empApplicationRegister/employee_register_handle";
	}
	
	//校验邮箱
	@ResponseBody
	@RequestMapping("/checkEmail.htm")
	public Map<String, Object> checkEmail(String email){
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			Employee param = new Employee();
			param.setEmail(email);
			List<Employee> list = employeeService.getListByCondition(param);
			if(list!=null&&list.size()>0){
				map.put("message", "该邮箱已被占用!");
				map.put("success", false);
				return map;
			}
			map.put("success", true);
			return map;
		}catch(Exception e){
			map.put("success",false);
			map.put("message",e.getMessage());
			return map;
		}
	}
	
	//入职审批流程页面
	@RequestMapping("/process.htm")
	public String process(HttpServletRequest request, Long registerId,Long ruProcdefId){
		EmpApplicationRegister register = employeeRegisterService.getById(registerId);
		request.setAttribute("register", register);
		RunTask param = new RunTask();
		param.setReProcdefCode(RunTask.RUN_CODE_10);
		param.setEntityId(String.valueOf(registerId));
		RunTask runTask = runTaskService.getRunTask(param);
		request.setAttribute("runTask", runTask);
		List<HiActinst> hiActinstList = hiActinstService.getListByRunId(ruProcdefId);
		//组装职位信息
		List<Long> empIdList = new ArrayList<Long>();
		List<Employee> list = new ArrayList<Employee>();
		for(HiActinst hiActinst:hiActinstList){
			if(hiActinst.getAssigneeId()!=null){
				empIdList.add(hiActinst.getAssigneeId());
			}
		}
		if(empIdList!=null&&empIdList.size()>0){
			list = employeeService.getByEmpIdList(empIdList);
		}
		for(HiActinst hiActinst:hiActinstList){
			for(Employee employee:list){
				if(employee.getId().equals(hiActinst.getAssigneeId())){
					hiActinst.setPositionName(employee.getPositionName());
				}
			}
		}
		request.setAttribute("hiActinstList", hiActinstList);
		return "base/empApplicationRegister/process";
	}
	
	//新员工入职登记行政处理
	@ResponseBody
	@RequestMapping("/adHandle.htm")
	@Token(remove = true)
	public Map<String, Object> adHandle(HttpServletRequest request,EmpApplicationRegister emp){
		Map<String,Object> map = new HashMap<String, Object>();
		User user = userService.getCurrentUser();
		try{
			emp.setNodeCode("executive");
			employeeRegisterService.completeTask(emp.getProcessInstanceId(),"行政审批通过",ConfigConstants.PASS, emp);
			map.put("success",true);
		}catch(Exception e){
			logger.error(user.getEmployee().getCnName()+"入职-行政处理："+e.toString());
			map.put("repeatFlag", false);
			map.put("success",false);
			map.put("message",e.getMessage());
			return map;
		}
		return map;
	}
	
	//新员工入职登记it处理
	@ResponseBody
	@RequestMapping("/itHandle.htm")
	@Token(remove = true)
	public Map<String, Object> itHandle(HttpServletRequest request,EmpApplicationRegister emp){
		Map<String,Object> map = new HashMap<String, Object>();
		User user = userService.getCurrentUser();
		try{
			emp.setNodeCode("IT");
			employeeRegisterService.completeTask(emp.getProcessInstanceId(),"IT审批通过",ConfigConstants.PASS, emp);
			map.put("success",true);
		}catch(Exception e){
			logger.error(user.getEmployee().getCnName()+"入职-IT处理："+e.toString());
			map.put("repeatFlag", false);
			map.put("success",false);
			map.put("message",e.getMessage());
			return map;
		}
		return map;
	}
	
	//确认入职
	@ResponseBody
	@RequestMapping("/confirm.htm")
	@Token(remove = true)
	public Map<String, Object> confirm(HttpServletRequest request,EmpApplicationRegister emp){
		logger.info("员工编号："+emp.getCode()+";指纹ID："+emp.getFingerprintId());
		Map<String,Object> map = new HashMap<String, Object>();
		User user = userService.getCurrentUser();
		try{
			emp.setNodeCode("confirm");
			Map<String,Object> result = employeeRegisterService.completeTask(emp.getProcessInstanceId(),"人事审批通过",ConfigConstants.PASS, emp);
			map.put("success",true);
			//确认入职之后，如果指纹id存在，重新拉取入职当天的打卡记录
			if(emp.getFingerprintId()!=null){
				try {
					EmpApplicationRegister register = employeeRegisterService.queryByProcessInstanceId(emp.getProcessInstanceId());
					if(register!=null){
						final Map<String, String> paramMap = new HashMap<String,String>();
						TransNormal transNormal = new TransNormal();
						transNormal.setCardid(emp.getFingerprintId().intValue());
						transNormal.setStartTime(DateUtils.parse(DateUtils.format(register.getDelayEntryDate()!=null?register.getDelayEntryDate():register.getEmploymentDate(), DateUtils.FORMAT_SHORT)+" 00:00:00", DateUtils.FORMAT_LONG));
						transNormal.setEndTime(DateUtils.parse(DateUtils.format(register.getDelayEntryDate()!=null?register.getDelayEntryDate():register.getEmploymentDate(), DateUtils.FORMAT_SHORT)+" 23:59:59", DateUtils.FORMAT_LONG));
						paramMap.put("data", JSONUtils.write(transNormal));
						logger.info("确认入职，获取当天打卡数据开始");
						
						String TRANS_NORMAL_URL = "/transNormal/getListByCardId.htm";
			    	    String OA_SERVICE_URL = configCacheManager.getConfigDisplayCode("MO_SERVICE_URL");
			    	    TRANS_NORMAL_URL = OA_SERVICE_URL + TRANS_NORMAL_URL;
						logger.info("请求的地址为：{},参数为：{}",TRANS_NORMAL_URL,paramMap);
						
						HttpRequest req = new HttpRequest.Builder().url(TRANS_NORMAL_URL).post(HttpRequest.HttpMediaType.FORM_URLENCODE, 
			    				ContentCoverter.formConvertAsString(paramMap)).build();
						
						client.sendRequestAsyn(req, new HttpCallBack() {
			                
							public void onFailure(Exception e) {
								logger.error("确认入职，获取当天打卡数据失败,失败原因={}",e.getMessage());
			                }

			                public void onResponse(HttpResponse response) {
			                	String responseStr = response.fullBody();
			                	Map<?, ?> responseMap = JSONUtils.readAsMap(responseStr);
         						if((Boolean) responseMap.get("success")){
         							List<Map> list = (List<Map>) responseMap.get("data");
         							Long employId = (Long) result.get("id");
         							String employName = (String) result.get("name");
         							for(Map data:list){
         								AttnSignRecord signRecord = new AttnSignRecord();
         								signRecord.setCompanyId(user.getCompanyId());
         								signRecord.setType(0);
         								signRecord.setEmployeeId(employId);
         								signRecord.setEmployeeName(employName);
         								Date date = new Date();
         								date.setTime((long) data.get("evttime"));
         								signRecord.setSignTime(date);
         								signRecord.setCreateTime(new Date());
         								signRecord.setCreateUser(user.getEmployee().getCnName());
         								signRecord.setDelFlag(0);
         								attnSignRecordService.save(signRecord);
         							}
         						}
			                }
			            });
					}
				} catch (Exception e) {
					logger.error("确认入职，获取当天打卡数据,失败原因={}",e.getMessage());
				}
			}
		}catch(Exception e){
			logger.error(user.getEmployee().getCnName()+"入职-确认入职："+e.toString());
			String uuId = UUIDGenerator.generate();
            try {
				RedisUtils.setString(String.valueOf(user.getEmployee().getId()),uuId,1800000);
				map.put("token", uuId);
			} catch (Exception e1) {
				logger.error("确认入职："+e1.toString());
			}
			map.put("repeatFlag", false);
			map.put("success",false);
			map.put("message",e.getMessage());
			return map;
		}
		return map;
	}
	
	//取消入职
	@ResponseBody
	@RequestMapping("/cancel.htm")
	@Token(remove = true)
	public Map<String, Object> cancel(HttpServletRequest request,EmpApplicationRegister emp){
		Map<String,Object> map = new HashMap<String, Object>();
		User user = userService.getCurrentUser();
		try{
			emp.setNodeCode("cancel");
			employeeRegisterService.completeTask(emp.getProcessInstanceId(),"取消入职",ConfigConstants.REFUSE, emp);
			map.put("success",true);
		}catch(Exception e){
			logger.error(user.getEmployee().getCnName()+"入职-取消入职："+e.toString());
			map.put("repeatFlag", false);
			map.put("success",false);
			map.put("message",e.getMessage());
			return map;
		}
		return map;
	}
	
	//延期入职
	@ResponseBody
	@RequestMapping("/delay.htm")
	@Token(remove = true)
	public Map<String, Object> delay(HttpServletRequest request,EmpApplicationRegister emp){
		Map<String,Object> map = new HashMap<String, Object>();
		User user = userService.getCurrentUser();
		try{
			map = employeeRegisterService.delay(emp);
		}catch(Exception e){
			logger.error(user.getEmployee().getCnName()+"入职-延期入职："+e.toString());
			map.put("repeatFlag", false);
			map.put("success",false);
			map.put("message",e.getMessage());
			return map;
		}
		return map;
	}
	
	//根据部门获取汇报人
	@ResponseBody
	@RequestMapping("/getReportPerson.htm")
	public List<Employee> getReportPerson(Long departId){
		List<Employee> pm=new ArrayList<Employee>();
		EmpDepart empDepart = new EmpDepart();
		empDepart.setDepartId(departId);
		Employee em = new Employee();
		em.setEmpDepart(empDepart);
		try {
			pm = employeeService.getReportPerson(em);
		} catch (Exception e) {
			
		}
		return pm;
	}
	
	@RequestMapping("/back.htm")
	@ResponseBody
	@Token(remove = true)
	public Map<String,Object> backTask(String processId,String taskId,String message){
		Map<String,Object> result = Maps.newHashMap();
		try {
			employeeRegisterService.backEntry(processId,taskId,message);
			result.put("success", true);
		}catch (Exception e) {
			result.put("success", false);
			result.put("message", e.getMessage());
		}
		return result;
	}
}
