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
 * @ClassName: ????????????
 * @Description: ????????????
 * @author zhoujinliang
 * @date 2018???3???21???16:55:09
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
				//????????????
				o.put("name",o.get("name"));
				o.put("typeCName",o.get("type_c_name"));
				o.put("cnName",o.get("cn_Name"));
				Date employmentDate,createTime;				
				employmentDate = (Date) o.get("employment_date");
				createTime = (Date) o.get("create_Time");
				o.put("employmentDate", DateUtils.format(employmentDate,"yyyy???MM???dd???  HH???mm???ss???"));
				o.put("createTime", DateUtils.format(createTime,"yyyy???MM???dd???  HH???mm???ss???"));				
				o.put("dpname",o.get("dpname"));
				o.put("leaderName",o.get("LeaderName"));
				o.put("deptLeaderName",o.get("deptLeaderName"));
				o.put("positionName",o.get("positionName"));
				o.put("email",o.get("email"));
				int entryStatus = ((Long)o.get("ENTRY_STATUS")).intValue();
				if(entryStatus==3) {
					o.put("entryStatus","?????????");
				}
				else{
					o.put("entryStatus","?????????");
				}	
				if(entryStatus == 1){					
					o.put("StatusDesc","?????????");
				}else if(entryStatus == 2){
					o.put("StatusDesc","?????????");
				}else if(entryStatus == 3) {
					o.put("StatusDesc","?????????");
				}
			}	
		String[] keys={"name", "typeCName", "cnName", "employmentDate","dpname","leaderName", "deptLeaderName", "positionName", "email", "createTime","StatusDesc","entryStatus"};
		String[] titles={"????????????", "????????????", "??????","????????????","??????", "????????????", "???????????????", "??????", "????????????", "????????????","????????????","????????????"}; 
		return ExcelUtil.exportExcel(datas,keys,titles,"????????????.xls");
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
		logger.info("???????????????-????????????-???????????????entryApplyId="+entryApplyId);
		User user = userService.getCurrentUser();
		Map<String,Object> result = new HashMap<String,Object>();
		if(entryApplyId==null){
			result.put("sucess",false);
			result.put("msg","???????????????????????????");
			return result;
		}
		EmpApplicationRegister param = new EmpApplicationRegister();
		param.setId(entryApplyId);
		List<EmpApplicationRegister> list = empApplicationRegisterMapper.getByCondition(param);
		if(list==null||list.size()<=0){
			result.put("sucess",false);
			result.put("msg","???????????????????????????");
			return result;
		}
		//?????????????????????id??????????????????
		//(1)??????????????????????????????????????????????????????(2)??????????????????????????????3?????????????????????????????????????????????4???????????????web???????????????
		if(list.get(0).getBackEmployeeId()==null){
			result.put("sucess",false);
			result.put("msg","??????????????????????????????????????????");
			return result;
		}
		Employee employee = employeeService.getById(list.get(0).getBackEmployeeId());
		if(employee==null){
			result.put("sucess",false);
			result.put("msg","??????????????????????????????????????????");
			return result;
		}
		
		//????????????????????????
		List<String> positionNames = new ArrayList<String>();
		positionNames.add("??????????????????");positionNames.add("????????????????????????");positionNames.add("HRBP??????");
		positionNames.add("??????????????????");positionNames.add("??????HRBP");positionNames.add("HRBP");positionNames.add("HRBP??????");
		positionNames.add("??????????????????");positionNames.add("IT??????");positionNames.add("IT????????????");positionNames.add("????????????");
		positionNames.add("????????????");positionNames.add("????????????");positionNames.add("IT??????");
		EmpPosition empPosition = new EmpPosition();
		empPosition.setPositionNames(positionNames);
		List<EmpPosition> empPositionList = empPositionMapper.getListByPositionName(empPosition);
		List<SendMail> sendMailList = new ArrayList<SendMail>();
		for(EmpPosition emp:empPositionList){
			//????????????
			sendMsg(emp.getEmployeeId(),user,"???????????????????????????","?????????"+list.get(0).getCnName()+"????????????????????????????????????");
		}
		sendMsg(list.get(0).getLeader(),user,"???????????????????????????","?????????"+list.get(0).getCnName()+"????????????????????????????????????");
		
		//???????????????????????????????????????????????????
		Long reportToLeader = list.get(0).getLeader();//?????????????????????
		List<Long> reportToLeaderList = new ArrayList<Long>();
		int count = 0;
		while(true){
			count = count + 1;
			if(reportToLeader!=null){
				reportToLeaderList.add(reportToLeader);
				//??????????????????????????????
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
				logger.info("????????????:??????????????????????????????");
			}
			//?????????????????????????????????????????????????????????
			if(count>10){
				break;
			}
		}
		
		Depart depart = departMapper.getById(list.get(0).getDepartId());
		if(depart!=null&&depart.getLeader()!=null&&!depart.getLeader().equals(list.get(0).getLeader())){
			sendMsg(depart.getLeader(),user,"???????????????????????????","?????????"+list.get(0).getCnName()+"????????????????????????????????????");
			Employee receiverDh = employeeMapper.getById(depart.getLeader());
			if(receiverDh!=null){
				SendMail sendMail = sendCancelEntryMail(user, list.get(0), receiverDh.getEmail());
				sendMailList.add(sendMail);
			}
		}
		//???????????????PMO,IT,??????,??????
		//????????????email
		//????????????PMO??????:
		if(depart != null && ("117".equals(depart.getCode())||"116".equals(depart.getCode())
				||"134".equals(depart.getCode())||"135".equals(depart.getCode()))){	
			String pmoEmail = companyConfigService.getGroupEmail(ConfigConstants.GROUP_EMAIL_PMO);
			SendMail sendMailToPMO = sendCancelEntryMail(user, list.get(0), pmoEmail);
			sendMailList.add(sendMailToPMO);
		}
		//????????????IT??????
		String itEmail = companyConfigService.getGroupEmail(ConfigConstants.GROUP_EMAIL_IT);
		SendMail sendMailToIT = sendCancelEntryMail(user, list.get(0), itEmail);
		sendMailList.add(sendMailToIT);
		//????????????????????????
		String adminEmail = companyConfigService.getGroupEmail(ConfigConstants.GROUP_EMAIL_ADMIN);
		SendMail sendMailToAdmin = sendCancelEntryMail(user, list.get(0), adminEmail);
		sendMailList.add(sendMailToAdmin);
		//????????????????????????
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
		//???sso??????????????????
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
	            	logger.info("?????????????????????start:username="+userCancel.getUserName());
					HashMap<String, String> paramMap = new HashMap<String,String>();
					paramMap.put("username", userCancel.getUserName());
					paramMap.put("jobStatus", "3");
					
					HttpRequest req1 = new HttpRequest.Builder().url(ConfigConstants.ULEACCOUNT_QUIT).post(HttpRequest.HttpMediaType.FORM_URLENCODE, 
		    				ContentCoverter.formConvertAsString(paramMap)).build();
		    		HttpResponse rep1 = client.sendRequest(req1);
					String response = rep1.fullBody();
					
					Map<?, ?> responseMap = JSONUtils.readAsMap(response);
					String resultCode = (String)responseMap.get("errorCode");
					logger.info("?????????????????????end:resultCode="+resultCode);
	            }else{
	            	logger.info("?????????????????????start:username="+userCancel.getUserName()+"?????????");
	            }
			}
		}catch(Exception e){
			logger.error("?????????????????????error:"+e.getMessage());
		}
		result.put("sucess",true);
		result.put("msg","?????????????????????");
		return result;
	}
	
	/**
	 * ????????????????????????
	 * @param user
	 * @param register
	 * @param receiver
	 * @return
	 * @throws IOException
	 */
	private SendMail sendCancelEntryMail(User user, EmpApplicationRegister register, String receiverEmail)
			throws IOException {
		//????????????????????????
		//????????????
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String nowDateStr = sdf.format(new Date());
		//??????????????????
		Depart depart = departMapper.getById(register.getDepartId());
		String params[]={
				depart.getName(),
				register.getEmployeeTypeId() == 15 ? "??????":"",
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
			sendMail.setSubject("???????????????????????????"+register.getCnName());
		}else{
			sendMail.setSubject("???????????????????????????"+register.getCnName()+"(??????)");
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
	 * ??????????????????
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
		//?????????????????????????????????????????????
		String msg=MessageFormat.format(prop.getProperty(templetPropertie),params);
		logger.info("WillSendMail"+msg);
		return msg;
	}
	
}
