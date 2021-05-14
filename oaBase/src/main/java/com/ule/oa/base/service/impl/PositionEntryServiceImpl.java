package com.ule.oa.base.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
import com.ule.oa.base.mapper.CompanyConfigMapper;
import com.ule.oa.base.mapper.DepartMapper;
import com.ule.oa.base.mapper.EmpApplicationRegisterMapper;
import com.ule.oa.base.mapper.EmpDepartMapper;
import com.ule.oa.base.mapper.EmpMsgMapper;
import com.ule.oa.base.mapper.EmpPositionMapper;
import com.ule.oa.base.mapper.EmployeeMapper;
import com.ule.oa.base.mapper.PositionEntryMapper;
import com.ule.oa.base.mapper.UserMapper;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.EmpApplicationRegister;
import com.ule.oa.base.po.EmpDepart;
import com.ule.oa.base.po.EmpEntryRegistration;
import com.ule.oa.base.po.EmpMsg;
import com.ule.oa.base.po.EmpPosition;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.SendMail;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.CompanyConfigService;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.HiActinstService;
import com.ule.oa.base.service.PositionEntryService;
import com.ule.oa.base.service.SendMailService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.po.CommonPo;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.ExcelUtil;
import com.ule.oa.common.utils.PageModel;
import com.ule.oa.common.utils.json.JSONUtils;

/**
 * @ClassName: 入职查询
 * @Description: 入职查询
 * @author zhoujinliang
 * @date 2018年3月21日16:55:09
 */
@Service
public class PositionEntryServiceImpl implements PositionEntryService{
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private PositionEntryMapper EmpEntryRegistrationMapper;
	@Autowired
	private HiActinstService hiActinstService;	
	@Resource
	private DepartService departService;
	@Autowired
	private EmpApplicationRegisterMapper empApplicationRegisterMapper;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private EmpDepartMapper empDepartMapper;
	@Autowired
	private EmpPositionMapper empPositionMapper;
	@Autowired
	private EmpMsgMapper empMsgMapper;
	@Autowired
	private DepartMapper departMapper;
	@Autowired
	private SendMailService	sendMailService;
	@Autowired
	private CompanyConfigService companyConfigService;
	@Autowired
	private EmployeeMapper employeeMapper;
	@Autowired
	private UserService userService;
	
	private static BaseHttpClient client = BaseHttpClientFactory.getClient();

	@Override
	public PageModel<EmpEntryRegistration> getReportPageList(
			EmpEntryRegistration entryRegistration) {
		int page = entryRegistration.getPage() == null ? 0 : entryRegistration.getPage();
		int rows = entryRegistration.getRows() == null ? 0 : entryRegistration.getRows();			
		PageModel<EmpEntryRegistration> pm = new PageModel<EmpEntryRegistration>();
		
		
		pm.setPageNo(page);
		pm.setPageSize(rows);
		int total = EmpEntryRegistrationMapper.getReportCount(entryRegistration);
		
		pm.setTotal(total);
		entryRegistration.setOffset(pm.getOffset());
		entryRegistration.setLimit(pm.getLimit());
				
		List<EmpEntryRegistration> roles = EmpEntryRegistrationMapper.getReportPageList(entryRegistration);
		Date now = DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_SHORT), DateUtils.FORMAT_SHORT);
		for(EmpEntryRegistration data:roles ){
			if(DateUtils.addMonth(data.getEmploymentDate(),1).getTime()>=now.getTime()&&data.getEntryStatus().intValue()==3){
				data.setCanCancel(0);
			}else{
				data.setCanCancel(1);
			}
		}
		
	
		pm.setRows(roles);
		return pm;
	}



	@Override
	public int getEaoByEmpAndDateCount(EmpEntryRegistration entryRegistration) {
		return 0;
	}



	@Override
	public List<Map<String,Object>> getExportReportList(
			EmpEntryRegistration entryRegistration) {
		return EmpEntryRegistrationMapper.getExportReportList(entryRegistration);
	}

	@Override
	public HSSFWorkbook exportExcel(EmpEntryRegistration entryRegistration) {
		List<Integer> departList = getDepartList(entryRegistration.getFirstDepart(),entryRegistration.getSecondDepart());
		entryRegistration.setDepartList(departList);	
	    List<Map<String,Object>> datas = getExportReportList(entryRegistration);    
			for (Map<String, Object> o : datas) {
				//封装数据
				o.put("name",o.get("name"));
				o.put("typeCName",o.get("type_c_name"));
				o.put("cnName",o.get("cn_Name"));
				Date employmentDate,createTime;				
				employmentDate = (Date) o.get("employment_date");
				createTime = (Date) o.get("create_Time");
				o.put("employmentDate", DateUtils.format(employmentDate,"yyyy年MM月dd日  HH时mm分ss秒"));
				o.put("createTime", DateUtils.format(createTime,"yyyy年MM月dd日  HH时mm分ss秒"));				
				o.put("dpname",o.get("dpname"));
				o.put("leaderName",o.get("LeaderName"));
				o.put("deptLeaderName",o.get("deptLeaderName"));
				o.put("positionName",o.get("positionName"));
				o.put("email",o.get("email"));
				int entryStatus = ((Long)o.get("ENTRY_STATUS")).intValue();
				if(entryStatus==3) {
					o.put("entryStatus","未延期");
				}
				else{
					o.put("entryStatus","已延期");
				}	
				if(entryStatus == 1){					
					o.put("StatusDesc","未入职");
				}else if(entryStatus == 2){
					o.put("StatusDesc","待入职");
				}else if(entryStatus == 3) {
					o.put("StatusDesc","已入职");
				}
			}	
		String[] keys={"name", "typeCName", "cnName", "employmentDate","dpname","leaderName", "deptLeaderName", "positionName", "email", "createTime","StatusDesc","entryStatus"};
		String[] titles={"公司名称", "员工类型", "姓名","入职日期","部门", "汇报对象", "部门负责人", "职位", "工作邮箱", "申请日期","入职状态","是否延期"}; 
		return ExcelUtil.exportExcel(datas,keys,titles,"入职查询.xls");
	}
	
	private List<Integer> getDepartList(Integer firstDepart,Integer secondDepart){
		
		List<Integer> departList = new ArrayList<Integer>();
		if(secondDepart!=null){
			departList.add(secondDepart);
		}else if(secondDepart==null&&firstDepart!=null){
			
			departList.add(firstDepart);
			List<Depart> list = departService.getByParentId(Long.valueOf(String.valueOf(firstDepart)));
		    
		    for(Depart depart:list){
		    	departList.add(Integer.valueOf(String.valueOf(depart.getId())));
		    };
		}	
		if(departList.isEmpty()){
			departList = null;
		}
		return departList;
	}



	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> cancelEntry(Long entryApplyId) throws Exception{
		logger.info("入离职管理-入职查询-取消入职：entryApplyId="+entryApplyId);
		User user = userService.getCurrentUser();
		Map<String,Object> result = new HashMap<String,Object>();
		if(entryApplyId==null){
			result.put("sucess",false);
			result.put("msg","入职申请单不存在！");
			return result;
		}
		EmpApplicationRegister param = new EmpApplicationRegister();
		param.setId(entryApplyId);
		List<EmpApplicationRegister> list = empApplicationRegisterMapper.getByCondition(param);
		if(list==null||list.size()<=0){
			result.put("sucess",false);
			result.put("msg","入职申请单不存在！");
			return result;
		}
		//根据回传的员工id删除员工信息
		//(1)申请单改成入职状态由已入职改为未入职(2)员工表对应员工删除（3）删除员工部门关系，职位关系（4）删除对应web端登录账号
		if(list.get(0).getBackEmployeeId()==null){
			result.put("sucess",false);
			result.put("msg","通过申请单未找到对应的员工！");
			return result;
		}
		Employee employee = employeeService.getById(list.get(0).getBackEmployeeId());
		if(employee==null){
			result.put("sucess",false);
			result.put("msg","通过申请单未找到对应的员工！");
			return result;
		}
		
		//发送取消入职通知
		List<String> positionNames = new ArrayList<String>();
		positionNames.add("企业文化专员");positionNames.add("人力资源高级经理");positionNames.add("HRBP专员");
		positionNames.add("人力资源经理");positionNames.add("高级HRBP");positionNames.add("HRBP");positionNames.add("HRBP助理");
		positionNames.add("人力资源助理");positionNames.add("IT主管");positionNames.add("IT高级专员");positionNames.add("行政经理");
		positionNames.add("行政专员");positionNames.add("行政前台");positionNames.add("IT经理");
		EmpPosition empPosition = new EmpPosition();
		empPosition.setPositionNames(positionNames);
		List<EmpPosition> empPositionList = empPositionMapper.getListByPositionName(empPosition);
		List<SendMail> sendMailList = new ArrayList<SendMail>();
		for(EmpPosition emp:empPositionList){
			//发送消息
			sendMsg(emp.getEmployeeId(),user,"新员工取消入职提醒","新员工"+list.get(0).getCnName()+"已取消入职，请各部门知晓");
		}
		sendMsg(list.get(0).getLeader(),user,"新员工取消入职提醒","新员工"+list.get(0).getCnName()+"已取消入职，请各部门知晓");
		
		//新员工逐级汇报对象到部门负责人为止
		Long reportToLeader = list.get(0).getLeader();//申请人汇报对象
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
					if(reportToLeaderObject!=null){
						SendMail sendMail = sendCancelEntryMail(user, list.get(0), reportToLeaderObject.getEmail());
						sendMailList.add(sendMail);
					}
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
		
		Depart depart = departMapper.getById(list.get(0).getDepartId());
		if(depart!=null&&depart.getLeader()!=null&&!depart.getLeader().equals(list.get(0).getLeader())){
			sendMsg(depart.getLeader(),user,"新员工取消入职提醒","新员工"+list.get(0).getCnName()+"已取消入职，请各部门知晓");
			Employee receiverDh = employeeMapper.getById(depart.getLeader());
			if(receiverDh!=null){
				SendMail sendMail = sendCancelEntryMail(user, list.get(0), receiverDh.getEmail());
				sendMailList.add(sendMail);
			}
		}
		//发送邮件给PMO,IT,行政,人事
		//查询群组email
		//发邮件给PMO群组:
		if(depart != null && ("117".equals(depart.getCode())||"116".equals(depart.getCode())
				||"134".equals(depart.getCode())||"135".equals(depart.getCode()))){	
			String pmoEmail = companyConfigService.getGroupEmail(ConfigConstants.GROUP_EMAIL_PMO);
			SendMail sendMailToPMO = sendCancelEntryMail(user, list.get(0), pmoEmail);
			sendMailList.add(sendMailToPMO);
		}
		//发邮件给IT群组
		String itEmail = companyConfigService.getGroupEmail(ConfigConstants.GROUP_EMAIL_IT);
		SendMail sendMailToIT = sendCancelEntryMail(user, list.get(0), itEmail);
		sendMailList.add(sendMailToIT);
		//发邮件给行政群组
		String adminEmail = companyConfigService.getGroupEmail(ConfigConstants.GROUP_EMAIL_ADMIN);
		SendMail sendMailToAdmin = sendCancelEntryMail(user, list.get(0), adminEmail);
		sendMailList.add(sendMailToAdmin);
		//发邮件给人事群组
		String hrEmail = companyConfigService.getGroupEmail(ConfigConstants.GROUP_EMAIL_HR);
		SendMail sendMailToHR = sendCancelEntryMail(user, list.get(0), hrEmail);
		sendMailList.add(sendMailToHR);

		if(sendMailList!=null&&sendMailList.size()>0){
			sendMailService.batchSave(sendMailList);
		}
		
		employee.setDelFlag(1);
		employeeService.updateById(employee);
		EmpApplicationRegister employeeRegister = new EmpApplicationRegister();
		employeeRegister.setId(entryApplyId);
		employeeRegister.setEntryStatus(1);
		empApplicationRegisterMapper.updateById(employeeRegister);
		EmpDepart empDepart = new EmpDepart();
		empDepart.setDelFlag(1);
		empDepart.setEmployeeId(list.get(0).getBackEmployeeId());
		empDepartMapper.updateByEmployeeId(empDepart);
		EmpPosition empPositionP = new EmpPosition();
		empPositionP.setDelFlag(1);
		empPositionP.setEmployeeId(list.get(0).getBackEmployeeId());
		empPositionMapper.updateByEmployeeId(empPositionP);
		User userCancel = userMapper.getByEmployeeId(list.get(0).getBackEmployeeId());
		User userP = new User();
		userP.setDelFlag(1);
		userP.setEmployeeId(list.get(0).getBackEmployeeId());
		userMapper.updateByEmployeeId(userP);
		//将sso通行证注销掉
		try{
			if(userCancel!=null){
     			HashMap<String, String> paramExistMap = new HashMap<String,String>();
     			paramExistMap.put("searchType", "1");
     			paramExistMap.put("searchValue", userCancel.getUserName());
				
				HttpRequest req = new HttpRequest.Builder().url(ConfigConstants.ULEACCOUNT_IS_EXIST).post(HttpRequest.HttpMediaType.FORM_URLENCODE, 
	    				ContentCoverter.formConvertAsString(paramExistMap)).build();
	    		HttpResponse rep = client.sendRequest(req);
				String responseExist = rep.fullBody();
				
				Map<?, ?> responseExistMap = JSONUtils.readAsMap(responseExist);
			    String resultCodeExist = (String)responseExistMap.get("errorCode");
	            if("".equals(resultCodeExist)){
	            	logger.info("注销员工通行证start:username="+userCancel.getUserName());
					HashMap<String, String> paramMap = new HashMap<String,String>();
					paramMap.put("username", userCancel.getUserName());
					paramMap.put("jobStatus", "3");
					
					HttpRequest req1 = new HttpRequest.Builder().url(ConfigConstants.ULEACCOUNT_QUIT).post(HttpRequest.HttpMediaType.FORM_URLENCODE, 
		    				ContentCoverter.formConvertAsString(paramMap)).build();
		    		HttpResponse rep1 = client.sendRequest(req1);
					String response = rep1.fullBody();
					
					Map<?, ?> responseMap = JSONUtils.readAsMap(response);
					String resultCode = (String)responseMap.get("errorCode");
					logger.info("注销员工通行证end:resultCode="+resultCode);
	            }else{
	            	logger.info("注销员工通行证start:username="+userCancel.getUserName()+"不存在");
	            }
			}
		}catch(Exception e){
			logger.error("注销员工通行证error:"+e.getMessage());
		}
		result.put("sucess",true);
		result.put("msg","取消入职成功！");
		return result;
	}
	
	/**
	 * 取消入职发送邮件
	 * @param user
	 * @param register
	 * @param receiver
	 * @return
	 * @throws IOException
	 */
	private SendMail sendCancelEntryMail(User user, EmpApplicationRegister register, String receiverEmail)
			throws IOException {
		//发送邮件封装参数
		//日期转化
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String nowDateStr = sdf.format(new Date());
		//查询员工部门
		Depart depart = departMapper.getById(register.getDepartId());
		String params[]={
				depart.getName(),
				register.getEmployeeTypeId() == 15 ? "外包":"",
				register.getCnName(),
				nowDateStr,
				user.getEmployee().getCnName(),
				user.getDepart().getName()
				};
		String templetPropertie = "cancelEntryTemplet";
		String msg = readEmailTemplet(params, templetPropertie);
  			      
		SendMail sendMail = new SendMail();
		sendMail.setReceiver(receiverEmail);
		if(register.getEmployeeTypeId() != 15){
			sendMail.setSubject("新员工入职取消——"+register.getCnName());
		}else{
			sendMail.setSubject("新员工入职取消——"+register.getCnName()+"(外包)");
		}
		sendMail.setSendStatus(SendMail.SEND_STATUS_NO);
		sendMail.setText(msg);
		sendMail.setOaMail(SendMail.OA_MAIL_P);
		return sendMail;
	}
	
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
	
	/**
	 * 读取模板文件
	 * @param params
	 * @param templetPropertie
	 * @return
	 * @throws IOException
	 */
	private String readEmailTemplet(String[] params, String templetPropertie) throws IOException {
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
	
}
