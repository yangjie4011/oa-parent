package com.ule.oa.base.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.flowable.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.google.common.collect.Maps;
import com.google.zxing.BarcodeFormat;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font.FontStyle;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPRow;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.ule.oa.base.cache.ConfigCacheManager;
import com.ule.oa.base.common.OaCommon;
import com.ule.oa.base.mapper.AttnWorkHoursMapper;
import com.ule.oa.base.mapper.EmpApplicationBusinessDetailMapper;
import com.ule.oa.base.mapper.EmpApplicationBusinessMapper;
import com.ule.oa.base.mapper.PositionMapper;
import com.ule.oa.base.mapper.RabcUserMapper;
import com.ule.oa.base.mapper.RuProcdefMapper;
import com.ule.oa.base.mapper.TimingRunTaskMapper;
import com.ule.oa.base.po.AttnWorkHours;
import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.EmpApplicationBusiness;
import com.ule.oa.base.po.EmpApplicationBusinessDetail;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.po.Position;
import com.ule.oa.base.po.RuProcdef;
import com.ule.oa.base.po.RunTask;
import com.ule.oa.base.po.TimingRunTask;
import com.ule.oa.base.po.TransNormal;
import com.ule.oa.base.po.User;
import com.ule.oa.base.po.tbl.ViewTaskInfoTbl;
import com.ule.oa.base.service.AnnualVacationService;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmpApplicationBusinessService;
import com.ule.oa.base.service.EmployeeClassService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.RabcUserRoleService;
import com.ule.oa.base.service.SendMailService;
import com.ule.oa.base.service.TransNormalService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.service.ViewTaskInfoService;
import com.ule.oa.base.util.AttnStatisticsUtil;
import com.ule.oa.base.util.SendMailUtil;
import com.ule.oa.base.vo.TaskVO;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.po.CommonPo;
import com.ule.oa.common.utils.CommonUtils;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.ExcelUtil;
import com.ule.oa.common.utils.MD5Encoder;
import com.ule.oa.common.utils.PDFUtils;
import com.ule.oa.common.utils.PageModel;
import com.ule.oa.common.utils.QRCodeUtils;
import com.ule.oa.common.utils.http.IPUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * @ClassName: ???????????????
 * @Description: ???????????????
 * @author yangjie
 * @date 2017???6???13???
 */
@Service
public class EmpApplicationBusinessServiceImpl implements EmpApplicationBusinessService {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private AwayWorkPdf pdf;
	@Autowired
	private EmpApplicationBusinessMapper empApplicationBusinessMapper;
	@Autowired
	private EmpApplicationBusinessDetailMapper empApplicationBusinessDetailMapper;
	@Autowired
	private UserService userService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private TimingRunTaskMapper timingRunTaskMapper;
	@Autowired
	private RuProcdefMapper ruProcdefMapper;
	@Resource
	private ConfigService configService;
	@Autowired
	private DepartService departService;
	@Autowired
	private ActivitiServiceImpl activitiServiceImpl;
	@Autowired
	private ViewTaskInfoService viewTaskInfoService;
	@Autowired
	private AnnualVacationService annualVacationService;
	@Resource
	private ConfigCacheManager configCacheManager;
	@Autowired
	private SendMailService	sendMailService;
	@Autowired
	private EmployeeClassService employeeClassService;
	@Autowired
	private PositionMapper positionMapper;
	@Autowired
	private RabcUserMapper rabcUserMapper;
	@Autowired
	private RabcUserRoleService rabcUserRoleService;
	@Autowired
	private AttnWorkHoursMapper attnWorkHoursMapper;
	@Autowired
	private TransNormalService transNormalService;
	
	@Override
	public Map<String,Object> updateById(EmpApplicationBusiness business, String type) throws Exception{
		Map<String,Object> map = new HashMap<String, Object>();
		User user = userService.getCurrentUser();
		//??????business???????????????????????????
		empApplicationBusinessMapper.updateById(business);
		if(business.getApprovalStatus().intValue() == EmpApplicationBusiness.APPROVAL_STATUS_YES) {
			EmpApplicationBusiness old = empApplicationBusinessMapper.getById(business.getId());
			//??????
			if(type.equals("1")&&business.getApprovalStatus().intValue()==ConfigConstants.PASS_STATUS.intValue()){
				//??????????????????,???????????????,?????????
				TimingRunTask timingRunTask = new TimingRunTask();
				timingRunTask.setProcessStatus(TimingRunTask.PROCESS_STATUS0);
				timingRunTask.setReProcdefCode(ConfigConstants.BUSINESS_CODE);
				timingRunTask.setCreatorId(business.getEmployeeId());
				timingRunTask.setEntityId(business.getId().toString());
				timingRunTask.setStartTime(business.getEndTime());
				timingRunTask.setCreateUser(user.getEmployee().getCnName());
				timingRunTaskMapper.save(timingRunTask);
				//????????????
				//????????????????????????,???????????????????????????????????????????????????
				/*if(business.getOriginalBillId() != null){
					//?????????????????????????????????
					EmpApplicationBusiness originalBill = empApplicationBusinessMapper.getById(Long.valueOf(business.getOriginalBillId()));
					//????????????????????????????????????
					Date startTime = originalBill.getStartTime();
					Date endTime = originalBill.getEndTime();
					attnStatisticsMapper.deleteOriginalBillWorkHour(startTime,endTime,originalBill.getEmployeeId());
					
				}*/
				Employee employee = employeeService.getById(old.getEmployeeId());
				AttnStatisticsUtil attnStatisticsUtil= new AttnStatisticsUtil();
				//????????????
				Date startDate = DateUtils.parse(DateUtils.format(old.getStartTime(), DateUtils.FORMAT_SHORT),DateUtils.FORMAT_SHORT);
				Date endDate = DateUtils.parse(DateUtils.format(old.getEndTime(), DateUtils.FORMAT_SHORT),DateUtils.FORMAT_SHORT);
				EmployeeClass employeeClass = new EmployeeClass();
				employeeClass.setEmployId(employee.getId());
				while(true){
					employeeClass.setClassDate(startDate);
					EmployeeClass empClass = employeeClassService.getEmployeeClassSetting(employeeClass);
					if(empClass!=null){
						attnStatisticsUtil.calWorkHours(user.getCompanyId(), employee.getId(), user.getEmployee().getCnName(), startDate, 
								empClass.getMustAttnTime(), RunTask.RUN_CODE_80, 
								DateUtils.parse(DateUtils.format(startDate, DateUtils.FORMAT_SHORT)+" "+DateUtils.format(empClass.getStartTime(), DateUtils.FORMAT_HH_MM_SS),DateUtils.FORMAT_LONG), 
								DateUtils.parse(DateUtils.format(startDate, DateUtils.FORMAT_SHORT)+" "+DateUtils.format(empClass.getEndTime(), DateUtils.FORMAT_HH_MM_SS),DateUtils.FORMAT_LONG), 
								null, old.getReason(), old.getId());
					}
					startDate = DateUtils.addDay(startDate, 1);
					if(startDate.getTime()>endDate.getTime()){
						break;
					}
				}
			}
		}
		map.put("success", true);
		return map;
	}

	@Override
	public EmpApplicationBusiness getById(Long id) {
		return empApplicationBusinessMapper.getById(id);
	}

	@Override
	public List<EmpApplicationBusinessDetail> getBusinessDetailList(Long businessId) {
		return empApplicationBusinessDetailMapper.getListByCondition(businessId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String,Object> save(HttpServletRequest request,User user) throws Exception{
		Map<String,Object> map = new HashMap<String, Object>();
		String list = request.getParameter("businessDetailList");
		String travelList = request.getParameter("travelClassList");
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		String address = request.getParameter("address");
		String vehicle = request.getParameter("vehicle");
		String businessType = request.getParameter("businessType");
		String reason = request.getParameter("reason");
		String duration = request.getParameter("duration");
		String travelProvinceBeginEnd = request.getParameter("travelProvinceBeginEnd");
		String travelCityBeginEnd = request.getParameter("travelCityBeginEnd");
		if(StringUtils.isBlank(startTime)){
			throw new OaException("???????????????????????????");
		}
		//18????????????????????????????????????????????????18??????????????????????????????????????????
		//????????????18?????????
		String nowDate18Str = DateUtils.getNow("yyyy-MM-dd") + " 18:00:00";
		Date nowDate18 = DateUtils.parse(nowDate18Str);
		Date startDate = DateUtils.parse(startTime+" 00:00:00");
		//???18??????????????????????????????
		int compareDate = DateUtils.compareDate(nowDate18, new Date());
		if(compareDate <= 1){
			//18???????????????????????????
			//????????????????????????
			Date yesterday = DateUtils.parse(DateUtils.format(DateUtils.addDay(new Date(), -1), "yyyy-MM-dd")+" 00:00:00");
			//?????????????????????????????????
			if(DateUtils.compareDate(startDate,yesterday) > 1){
				throw new OaException("18??????????????????????????????????????????");
			}
		}else{
			//18???????????????????????????
			//????????????????????????
			Date nowDate = DateUtils.parse(DateUtils.getNow("yyyy-MM-dd") + " 00:00:00");
			if(DateUtils.compareDate(startDate,nowDate) > 1){
				throw new OaException("18?????????????????????????????????????????????");
			}
		}
		if(StringUtils.isBlank(travelProvinceBeginEnd)){
			throw new OaException("????????????????????????");
		}
		if(StringUtils.isBlank(travelCityBeginEnd)){
			throw new OaException("????????????????????????");
		}
		if(StringUtils.isBlank(endTime)){
			throw new OaException("???????????????????????????");
		}
		if(StringUtils.isBlank(duration)||"0".equals(duration)){
			throw new OaException("??????????????????0???");
		}
		if(StringUtils.isBlank(address)){
			throw new OaException("?????????????????????");
		}
		if(StringUtils.isBlank(vehicle)){
			throw new OaException("???????????????????????????");
		}
		if(StringUtils.isBlank(businessType)){
			throw new OaException("???????????????????????????");
		}
		if(StringUtils.isBlank(reason)){
			throw new OaException("??????/???????????????????????????");
		}
		EmpApplicationBusiness business = new EmpApplicationBusiness();
		business.setEmployeeId(user.getEmployeeId());
		business.setCnName(user.getEmployee().getCnName());
		business.setCode(user.getEmployee().getCode());
		business.setDepartId(user.getDepart().getId());
		business.setDepartName(user.getDepart().getName());
		business.setPositionId(user.getPosition().getId());
		business.setPositionName(user.getPosition().getPositionName());
		business.setStartTime(DateUtils.parse(startTime,DateUtils.FORMAT_SHORT));
		business.setEndTime(DateUtils.parse(endTime,DateUtils.FORMAT_SHORT));
		business.setDuration(Double.valueOf(duration));
		business.setVehicle(Integer.valueOf(vehicle));
		business.setBusinessType(Integer.valueOf(businessType));
		business.setReason(reason);
		business.setDelFlag(CommonPo.STATUS_NORMAL);
		business.setVersion(null);
		business.setCreateTime(new Date());
		business.setCreateUser(user.getEmployee().getCnName());
		business.setSubmitDate(new Date());
		business.setApprovalStatus(EmpApplicationBusiness.APPROVAL_STATUS_WAIT);
		List<EmpApplicationBusinessDetail> detailList = new ArrayList<EmpApplicationBusinessDetail>();
		JSONArray array = JSONArray.fromObject(list);
		JSONArray addressList = JSONArray.fromObject(travelList);
		if(array==null||array.size()<=0){
			throw new OaException("????????????????????????????????????????????????");
		}
		for (int i = 0; i < addressList.size(); i++) {			
			JSONObject jsonObject = JSONObject.fromObject(addressList.get(i), new JsonConfig());
			if(!jsonObject.toString().equals("{}")){
				switch(i){
					case 0:business.setTravelProvince1(jsonObject.getString("travelProvince"));
					   	   business.setTravelCity1(jsonObject.getString("travelCity"));
					    break;
					case 1:business.setTravelProvince2(jsonObject.getString("travelProvince"));
						   business.setTravelCity2(jsonObject.getString("travelCity"));
					    break;
					case 2:business.setTravelProvince3(jsonObject.getString("travelProvince"));
				   	   	   business.setTravelCity3(jsonObject.getString("travelCity"));
					    break;
					case 3:business.setTravelProvince4(jsonObject.getString("travelProvince"));
				   	   	   business.setTravelCity4(jsonObject.getString("travelCity"));
					    break;
					case 4:business.setTravelProvince5(jsonObject.getString("travelProvince"));
						   business.setTravelCity5(jsonObject.getString("travelCity"));
					    break;
				}
			}
		}
		business.setAddress(address);
		business.setStartProvinceAddress(travelProvinceBeginEnd);
		business.setStartCityAddress(travelCityBeginEnd);
		business.setEndProvinceAddress(travelProvinceBeginEnd);
		business.setEndCityAddress(travelCityBeginEnd);
		for(int i=0;i<array.size();i++){
			EmpApplicationBusinessDetail detail = new EmpApplicationBusinessDetail();
			JSONObject jsonObject = JSONObject.fromObject(array.get(i), new JsonConfig());
	        if(!jsonObject.containsKey("workPlan")){
	        	throw new OaException("?????????????????????????????????");
			}
	        if(jsonObject.getString("workPlan")==null||"".equals(jsonObject.getString("workPlan"))){
	        	throw new OaException("?????????????????????????????????");
			}
	        if(!jsonObject.containsKey("workStartDate")){
	        	throw new OaException("?????????????????????????????????");
	        }
	        if(jsonObject.getString("workStartDate")==null||"".equals(jsonObject.getString("workStartDate"))){
	        	throw new OaException("?????????????????????????????????");
			}
	        if(!jsonObject.containsKey("workEndDate")){
	        	throw new OaException("?????????????????????????????????");
	        }
	        if(jsonObject.getString("workEndDate")==null||"".equals(jsonObject.getString("workEndDate"))){
	        	throw new OaException("?????????????????????????????????");
			}
			detail.setWorkPlan(jsonObject.getString("workPlan"));
			detail.setWorkObjective(jsonObject.getString("workTarget"));
			detail.setDelFlag(CommonPo.STATUS_NORMAL);
			detail.setVersion(null);
			detail.setCreateTime(new Date());
			detail.setCreateUser(user.getUserName());
			detail.setWorkStartDate(DateUtils.parse(jsonObject.getString("workStartDate"),DateUtils.FORMAT_SHORT));
			detail.setWorkEndDate(DateUtils.parse(jsonObject.getString("workEndDate"),DateUtils.FORMAT_SHORT));
			detailList.add(detail);
		}
		//????????????
		Map<String ,Object> variables = setProcessVariables(business,user);
		String processInstanceId = activitiServiceImpl.startByKey(ConfigConstants.BUSINESS_KEY, variables);
		business.setProcessinstanceId(processInstanceId);
		empApplicationBusinessMapper.save(business);
		//-----------------start-----------------------????????????????????????
		ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
		flow.setAssigneeName(business.getCnName());
		flow.setDepartName(business.getDepartName());
		flow.setPositionName(business.getPositionName());
		flow.setFinishTime(business.getSubmitDate());
		flow.setComment(business.getReason());
		flow.setTaskId("proposer");
		flow.setProcessId(processInstanceId);
		flow.setProcessKey(ConfigConstants.BUSINESS_KEY);
		flow.setStatu(0);
		viewTaskInfoService.save(flow);
		if(business.getId()!=null){
			for(EmpApplicationBusinessDetail detail:detailList){
				detail.setBusinessId(business.getId());
			}
			empApplicationBusinessDetailMapper.batchSave(detailList);
		}
		map.put("success", true);
		return map;
	}
	
	public Map<String,Object> setProcessVariables(EmpApplicationBusiness business,User user){
		Map<String ,Object> variables = new HashMap<String ,Object>();
		variables.put("vehicle", business.getVehicle());
		//?????????????????????????????????VEP????????????????????????????????????????????????VEP
		//???????????????VEP?????????????????????
		boolean headerIsVEP = false;//????????????????????????VEP
		List<Config> vepList = configService.getListByCode("VEP");
		for(Config data:vepList){
			Employee param = new Employee();
			param.setCode(data.getDisplayCode());
			Employee vep = employeeService.getByCondition(param);
			if(user.getDepart().getLeader().equals(vep.getId())){
				headerIsVEP = true;
				break;
			}
		}
		variables.put("headerIsVEP", headerIsVEP);
		boolean isVEPManager = false;//?????????VEP???????????????
		List<Config> conList = configService.getListByCode("VEP_MANAGER_DEPART");
		for(Config config:conList){
			if(config.getDisplayCode().equals(user.getDepart().getCode())){
				isVEPManager = true;
				break;
			}
		}
		boolean needLeader = false;//??????????????????????????????
		List<Config> needLeaderList = configService.getListByCode("business_apply_nedd_leader_approve_depart");
		for(Config config:needLeaderList){
			if(config.getDisplayCode().equals(user.getDepart().getCode())){
				needLeader = true;
				break;
			}
		}
		variables.put("needLeader", needLeader);
		variables.put("isVEPManager", isVEPManager);
		variables.put("employeeId", user.getEmployeeId());
		return variables;
	}

	@Override
	@Transactional(rollbackFor = Exception.class,propagation=Propagation.REQUIRES_NEW)
	public Map<String,Object> saveWorkSummary(HttpServletRequest request,User user) throws Exception{
		
		String list = request.getParameter("businessDetailList");
		String id = request.getParameter("id");
		String remark = request.getParameter("remark");
		
		logger.info("??????????????????saveWorkSummary??????:list="+list+";id="+id+";remark="+remark
				+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		
		EmpApplicationBusiness business = getById(Long.valueOf(id));
		business.setRemark(remark);
		empApplicationBusinessMapper.updateById(business);
		JSONArray array = JSONArray.fromObject(list);
		if(array==null||array.size()<=0){
			throw new OaException("????????????????????????????????????????????????");
		}
		for(int i=0;i<array.size();i++){
			EmpApplicationBusinessDetail detail = new EmpApplicationBusinessDetail();
			JSONObject jsonObject = JSONObject.fromObject(array.get(i), new JsonConfig());
			if(!jsonObject.containsKey("workSummary")){
				throw new OaException("????????????????????????????????????????????????");
			}
			if(jsonObject.getString("workSummary")==null||"".equals(jsonObject.getString("workSummary"))){
				throw new OaException("????????????????????????????????????????????????");
			}
			detail.setId(jsonObject.getLong("id"));
			detail.setBusinessId(Long.valueOf(id));
			detail.setWorkSummary(jsonObject.getString("workSummary"));
			detail.setUpdateTime(new Date());
			detail.setUpdateUser(user.getEmployee().getCnName());
			empApplicationBusinessDetailMapper.updateById(detail);
		}
		//????????????
		completeReportTask(request,business.getProcessinstanceReportId(),"",null);
		map.put("success", true);
		return map;
	}

	/**
	 * @throws OaException 
	 * @throws MessagingException 
	 * @throws IOException 
	 * @throws DocumentException 
	  * exportApplyResultByBusinessId(?????????????????????)
	  * @Title: exportApplyResultByBusinessId
	  * @Description: ?????????????????????
	  * @throws
	 */
	@Override
	public void exportApplyResultByBusinessId(Long businessId,Long runId) throws DocumentException, IOException, MessagingException, OaException{
		EmpApplicationBusiness main = null;
		List<EmpApplicationBusinessDetail> detailList = null;
		
		main = getById(businessId);
		detailList = getBusinessDetailList(businessId);
		
		String filePath = ConfigConstants.OA_RSAKEY  + SendMailUtil.formatPath(MD5Encoder.md5Hex(DateUtils.getNow())) + ".pdf";
		pdf.buildAwayWorkPdf(main, detailList,viewTaskInfoService.queryTasksByProcessId(main.getProcessinstanceId()),filePath);
		
		Employee currentEmployee = employeeService.getCurrentEmployee();
		String toMail = currentEmployee.getEmail();
//		String toMail = "minsheng@ule.com";
		//????????????
		SendMailUtil.sendPdfMail(toMail, "???????????????", filePath);

	}
	
	/**
	  * exportApplyReportByBusinessId(????????????????????????)
	  * @Title: exportApplyReportByBusinessId
	  * @Description: ????????????????????????
	  * @throws
	 */
	@Override
	public void exportApplyReportByBusinessId(Long businessId,Long ruProcdefId) {
		List<EmpApplicationBusinessDetail> detail = getBusinessDetailList(businessId);//????????????
		EmpApplicationBusiness head = getById(businessId);//????????????

		try {
			String filePath = ConfigConstants.OA_RSAKEY  + SendMailUtil.formatPath(MD5Encoder.md5Hex(DateUtils.getNow())) + ".pdf";
			
			//??????pdf
			writeApplyResult(head, detail,viewTaskInfoService.queryTasksByProcessId(head.getProcessinstanceReportId()),filePath);
			
			//????????????
			Employee currentEmployee = employeeService.getCurrentEmployee();
			String toMail = currentEmployee.getEmail();
//			String toMail = "minsheng@ule.com";
			
			logger.info("??????id="+ businessId +"???????????????????????????" + toMail);
			
			SendMailUtil.sendPdfMail(toMail, "??????????????????", filePath);
		} catch (Exception e) {
			
		}
	}
	
	public RuProcdef getSignInfo(String entryId,String procdefCode,String nodeModule){
		RuProcdef runTask = new RuProcdef();
		runTask.setEntityId(entryId);
		runTask.setReProcdefCode(procdefCode);
		runTask.setNodeModule(nodeModule);
		
		return ruProcdefMapper.getRunTaskByCodeAndEntry(runTask);//??????
	}
	
	/**
	 * @throws DocumentException 
	 * @throws FileNotFoundException 
	  * testWriteApplyResult(?????????????????????????????????????????????)
	  * @Title: testWriteApplyResult
	  * @Description: ????????????????????????
	  * void    ????????????
	  * @throws
	 */
	public void writeApplyResult(EmpApplicationBusiness head,List<EmpApplicationBusinessDetail> details,List<ViewTaskInfoTbl> flows,String filePath) throws FileNotFoundException, DocumentException{
		//????????????
		Document document = new Document();
		//?????????????????????
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
		
		//????????????
		document.open();
		
		Integer contentFontSize = 12;
		
		try{
			//????????????
			document.add(PDFUtils.setParagraph("??????????????????", 15, FontStyle.BOLD.getValue(),PDFUtils.ALIGN_CENTER));
			
			//??????(6???)
			PdfPTable table = PDFUtils.createTable(12,100,10,0);
			
			//?????????????????????
			List<PdfPRow> listRow = table.getRows();
			//????????????
			float[] columnWidths = { 4f, 4f, 4f, 4f ,4f ,4f, 4f, 4f, 4f, 4f ,4f ,4f};
			table.setWidths(columnWidths);
	
			//?????????
			PdfPCell cell = null;
			PdfPCell cells[] = new PdfPCell[12];
			PdfPRow row = new PdfPRow(cells);
			
			String address = head.getAddress();
			String[] split = address.split("-");
			String strAddress="";
			for (int i = 0; i < split.length; i++) {
				if(i==0 ||i==split.length-1){
				}else{
					strAddress+=split[i]+",";
				}
			}
			String substring = "";
			if("".equals(strAddress)){
			    substring = address;
			}else{
				substring = strAddress.substring(0, strAddress.length()-1);
			}
			//??????????????????
			cells[0] = new PdfPCell(PDFUtils.setParagraph("???    ???", contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));//???????????????
			cells[0].setColspan(2);
			cells[2] = new PdfPCell(PDFUtils.setParagraph(CommonUtils.converToString(head.getCnName()), contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[2].setColspan(2);
			cells[4] = new PdfPCell(PDFUtils.setParagraph("??????", contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[4].setColspan(2);
			cells[6] = new PdfPCell(PDFUtils.setParagraph(CommonUtils.converToString(head.getPositionName()), contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[6].setColspan(2);
			cells[8] = new PdfPCell(PDFUtils.setParagraph("????????????", contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[8].setColspan(2);
			cells[10] = new PdfPCell(PDFUtils.setParagraph(CommonUtils.converToString(substring), contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[10].setColspan(2);
			listRow.add(row);
			
			//?????????
			cells = new PdfPCell[12];
			row = new PdfPRow(cells);
			
			//??????????????????
			cells[0] = new PdfPCell(PDFUtils.setParagraph("????????????", contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));//???????????????
			cells[0].setColspan(2);
			cells[2] = new PdfPCell(PDFUtils.setParagraph(String.valueOf(head.getDuration()) + "???", contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[2].setColspan(4);
			cells[6] = new PdfPCell(PDFUtils.setParagraph("????????????", contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[6].setColspan(2);
			//????????????
			Date sumaryDate = null;
			if(null != flows && flows.size() > 0 ){
				sumaryDate = flows.get(flows.size() - 1).getFinishTime();
			}
			cells[8] = new PdfPCell(PDFUtils.setParagraph(DateUtils.format(sumaryDate), contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[8].setColspan(4);
			listRow.add(row);
			
			//?????????
			cells = new PdfPCell[12];
			row = new PdfPRow(cells);
			
			//??????????????????
			cells[0] = new PdfPCell(PDFUtils.setParagraph("????????????", contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));//???????????????
			cells[0].setColspan(2);
			cells[2] = new PdfPCell(PDFUtils.setParagraph(CommonUtils.converToString(head.getReason()), contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[2].setColspan(10);
			listRow.add(row);
			
			//?????????
			cells = new PdfPCell[12];
			row = new PdfPRow(cells);
			
			//??????????????????
			cells[0] = new PdfPCell(PDFUtils.setParagraph("????????????", contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));//???????????????
			cells[0].setColspan(2);
			cells[2] = new PdfPCell(PDFUtils.setParagraph(CommonUtils.converToString(head.getAddress()+"???????????????????????????????????????"), contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[2].setColspan(10);
			listRow.add(row);
			
			//?????????
			cells = new PdfPCell[12];
			row = new PdfPRow(cells);
			
			//??????????????????
			cells[0] = new PdfPCell(PDFUtils.setParagraph("???????????????????????????????????????:(?????????)", contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));//???????????????
			cells[0].setColspan(12);
			listRow.add(row);
			
			//?????????
			cells = new PdfPCell[12];
			row = new PdfPRow(cells);
			
			//??????????????????
			cells[0] = new PdfPCell(PDFUtils.setParagraph("?????????", contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//???????????????
			cells[0].setColspan(2);
			cells[2] = new PdfPCell(PDFUtils.setParagraph("?????????????????????????????????", contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//???????????????
			cells[2].setColspan(8);
			cells[10] = new PdfPCell(PDFUtils.setParagraph("??????", contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//???????????????
			cells[10].setColspan(2);
			listRow.add(row);
			
			//?????????
			cells = new PdfPCell[12];
			row = new PdfPRow(cells);
			
			//??????????????????
			int count = 0;
			int index = 0;
			int whiteCount = 5;//?????????
			EmpApplicationBusinessDetail detail = null;
			while(count < 3){
				cells[index] = new PdfPCell();
				PdfPTable tItemTable= PDFUtils.createTable(1,100,0,0);
				cells[index].addElement(tItemTable);//??????????????????????????????
				boolean flagReson=true;
				for(int i = 0;i < details.size();i++){
					detail = details.get(i);
					String detailStr = "";
					if(count == 0){
						detailStr = DateUtils.format(detail.getWorkStartDate(),DateUtils.FORMAT_SHORT)+"-"+DateUtils.format(detail.getWorkEndDate(),DateUtils.FORMAT_SHORT);
					}else if(count == 1){
						detailStr = detail.getWorkSummary();
					}else{
						if(flagReson){
							detailStr =head.getRemark();
							flagReson=false;
						}
						
					}
					
					cell = new PdfPCell(PDFUtils.setParagraph(detailStr, contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));
					cell.setBorder(0);
					tItemTable.addCell(cell);
					whiteCount--;
				}
				whiteCount = details.size();
				
				if(whiteCount <= 15){
					for(int j = 0 ; j < 15 - whiteCount ; j++){
						cell = new PdfPCell(PDFUtils.setParagraph(" ", contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));
						cell.setBorder(0);
						tItemTable.addCell(cell);
					}
				}
				
				if(count == 0){//?????????????????????1???
					cells[index].setColspan(2);
					index += 2;
				}else if(count == 1){//?????????????????????4???
					cells[index].setColspan(8);
					index += 8;
				}else if(count == 2){//?????????????????????1???
					cells[index].setColspan(2);
				}
				count++;
			}
			listRow.add(row);
			
			//?????????
			cells = new PdfPCell[12];
			row = new PdfPRow(cells);
			
			//??????????????????
			cells[0] = new PdfPCell(PDFUtils.setParagraph("???????????????", contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//???????????????
			cells[0].setColspan(2);
			cells[2] = new PdfPCell(PDFUtils.setParagraph(" ", contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//???????????????
			cells[2].setColspan(4);
			cells[6] = new PdfPCell(PDFUtils.setParagraph("???????????????", contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//???????????????
			cells[6].setColspan(4);
			cells[10] = new PdfPCell(PDFUtils.setParagraph(" ", contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//???????????????
			cells[10].setColspan(2);
			listRow.add(row);
			
			//?????????
			cells = new PdfPCell[12];
			row = new PdfPRow(cells);
			
			//??????????????????
			whiteCount = 8;//?????????
			cells[0] = new PdfPCell();
			PdfPTable tItemTable= PDFUtils.createTable(1,100,0,0);
			cells[0].addElement(tItemTable);//??????????????????????????????
			cells[0].setColspan(12);
			//?????????
			String tmpStr = "";
			while(whiteCount > 0){
				if(whiteCount == 8){tmpStr = "????????????:";}else{tmpStr = " ";}
				cell = new PdfPCell(PDFUtils.setParagraph(tmpStr, contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));
				cell.setBorder(0);
				tItemTable.addCell(cell);
				whiteCount--;
			}
			listRow.add(row);
			
			//?????????
			cells = new PdfPCell[12];
			row = new PdfPRow(cells);
			
			//??????????????????
			cells[0] = new PdfPCell(PDFUtils.setParagraph("??? ??? ???", contentFontSize, FontStyle.BOLD.getValue(), PDFUtils.ALIGN_CENTER));//???????????????
			cells[0].setColspan(3);
			cells[3] = new PdfPCell(PDFUtils.setParagraph("??????????????????", contentFontSize, FontStyle.BOLD.getValue(), PDFUtils.ALIGN_CENTER));//???????????????
			cells[3].setColspan(3);
			cells[6] = new PdfPCell(PDFUtils.setParagraph("?????????????????????", contentFontSize, FontStyle.BOLD.getValue(), PDFUtils.ALIGN_CENTER));//???????????????
			cells[6].setColspan(3);
			cells[9] = new PdfPCell(PDFUtils.setParagraph("CEO/COO??????", contentFontSize, FontStyle.BOLD.getValue(), PDFUtils.ALIGN_CENTER));//???????????????
			cells[9].setColspan(3);
			listRow.add(row);
			
			//?????????
			cells = new PdfPCell[12];
			row = new PdfPRow(cells);
			
			//??????????????????
			count = 0;//??????????????????
			index = 0;//???????????????
			ViewTaskInfoTbl flow01 = null;//???????????????
			ViewTaskInfoTbl flow02 = null;//????????????
			ViewTaskInfoTbl flow03 = null;//coo??????
			ViewTaskInfoTbl flow04 = null;//VEP??????
			ViewTaskInfoTbl flow05 = null;//VEP??????
			for(ViewTaskInfoTbl flow : flows){
				if("START".equals(flow.getTaskId())){
					flow01 = flow;
				}else if("RESIGN_DH".equals(flow.getTaskId())){
					flow02 = flow;
				}else if("RESIGN_COO".equals(flow.getTaskId())){
					flow03 = flow;
				}else if("VEP_1".equals(flow.getTaskId())){
					flow04 = flow;
				}else if("VEP_2".equals(flow.getTaskId())){
					flow05 = flow;
				}
			}
			//????????????
			while(count <= 3){
				String account = "";
				String time = "";
				ViewTaskInfoTbl task = null; 
				ViewTaskInfoTbl task1 = null; 
				ViewTaskInfoTbl task2 = null; 
				if(count == 0){
					task = flow01;//??????
				}else if(count == 1){
					task = flow02;//??????
				}else if(count == 2){
					//?????????????????????????????????VEP????????????????????????????????????????????????VEP
					Depart depart = departService.getInfoByEmpId(head.getEmployeeId());
					boolean headerIsVEP = false;
					List<Config> vepList = configService.getListByCode("VEP");
					for(Config data:vepList){
						Employee param = new Employee();
						param.setCode(data.getDisplayCode());
						Employee vep = employeeService.getByCondition(param);
						if(vep.getId().toString().equals(depart.getLeaderId())){
							headerIsVEP = true;
							break;
						}
					}
					if(headerIsVEP){
						task = flow02;
					}
					task1 = flow04; 
				    task2 = flow05; 
				}else{
					task = flow03;//??????
				}
				if(null != task){
					account = task.getAssigneeName();
					time = DateUtils.format(task.getFinishTime());
				}
				if(task1!=null){
			    	account = task1.getAssigneeName();
			    	time = DateUtils.format(task1.getFinishTime());
			    }
			    if(task2!=null){
			    	account =task2.getAssigneeName();
			    	time = DateUtils.format(task2.getFinishTime());
			    }
				cells[index] = new PdfPCell();
				tItemTable= PDFUtils.createTable(1,100,0,0);
				cells[index].addElement(tItemTable);//??????????????????????????????
				cell = new PdfPCell(PDFUtils.setParagraph("??????:" + CommonUtils.converToString(account), contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
				cell.setBorder(0);
				tItemTable.addCell(cell);
				//????????????
				whiteCount = 3;
				while(whiteCount > 0){
					cell = new PdfPCell(PDFUtils.setParagraph(" ", contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
					cell.setBorder(0);
					whiteCount --;
					tItemTable.addCell(cell);
				}
				cell = new PdfPCell(PDFUtils.setParagraph("??????:" + time, contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
				cell.setBorder(0);
				tItemTable.addCell(cell);
				cells[index].setColspan(3);

				index += 3;
				count++;
			}
			listRow.add(row);
			
			//???????????????????????????
			document.add(table);
			
			try{
				//???????????????
				String OA_URL = configCacheManager.getConfigDisplayCode("MO_WEB_URL");
				String codePath = OA_URL +"/empApplicationBusiness/approveWorkSummary.htm?businessId="+head.getId()+"&flag=no";
//				+ "/runTask/nonAuth/toView.htm?empId="+head.getEmployeeId()+"&ruTaskId="+ hiActinstList.get(0).getRuTaskId() 
//							+ "&sign=" + MD5Encoder.md5Hex(hiActinstList.get(0).getRuTaskId() + "");
				String imagePath = ConfigConstants.OA_RSAKEY  + SendMailUtil.formatPath(MD5Encoder.md5Hex(DateUtils.getNow())) + ".png";//?????????????????????
				QRCodeUtils.encode(codePath, imagePath, BarcodeFormat.QR_CODE, 20, 20);//???????????????
				Image image = Image.getInstance(imagePath);
				image.setAlignment(PDFUtils.ALIGN_RIGHT);
				document.add(image);//?????????????????????pdf???
				File delfile = new File(imagePath);//???????????????
		        if(!delfile.delete()) {
		        	logger.error("??????????????????");
		        }

		        //???????????????????????????
		        document.add(PDFUtils.setParagraph("????????????????????????????????????", contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_RIGHT));
		        document.add(PDFUtils.setParagraph("????????????????????????MO????????????-" + System.currentTimeMillis() + head.getId(), contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_RIGHT));
			}catch(Exception e){
				logger.error("???????????????????????????????????????,????????????={}",e);
			}
		}catch(Exception e){
			
		}finally{
			//????????????
			document.close();
			//???????????????
			writer.close();
		}
	}
	
	public List<EmpApplicationBusiness> getReportList(EmpApplicationBusiness empApplicationBusiness){
		List<EmpApplicationBusiness> buss = empApplicationBusinessMapper.getReportPageList(empApplicationBusiness);
		
		//????????????????????????
		buss.forEach((EmpApplicationBusiness bus) -> {
			try{
				ViewTaskInfoTbl taskInfo = null;
				taskInfo = viewTaskInfoService.getFirstAuditUser(bus.getProcessinstanceId(),ConfigConstants.BUSINESS_KEY,true);
				if(null != taskInfo){
					bus.setAuditUser(taskInfo.getAssigneeName());
				}
			}catch(Exception e){
				bus.setAuditUser(" ");
				logger.error("??????{}??????????????????????????????",bus.getId());
			}
		});
		
		return buss;
	}

	/**
	  * getReportPageList(??????????????????????????????)
	  * @Title: getReportPageList
	  * @Description: ??????????????????????????????
	  * @param empApplicationOutgoing
	  * @return    ????????????
	  * PageModel<EmpApplicationOutgoing>    ????????????
	  * @throws
	 */
	public PageModel<EmpApplicationBusiness> getReportPageList(EmpApplicationBusiness empApplicationBusiness){
		int page = empApplicationBusiness.getPage() == null ? 0 : empApplicationBusiness.getPage();
		int rows = empApplicationBusiness.getRows() == null ? 0 : empApplicationBusiness.getRows();
		
		PageModel<EmpApplicationBusiness> pm = new PageModel<EmpApplicationBusiness>();
		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
		if(deptDataByUserList==null || deptDataByUserList.size()==0){
			pm.setPageNo(0);
			pm.setPageSize(0);
			pm.setTotal(0);
			empApplicationBusiness.setOffset(pm.getOffset());
			empApplicationBusiness.setLimit(pm.getLimit());
			pm.setRows(new ArrayList<EmpApplicationBusiness>());
			return pm;
		}else{
			empApplicationBusiness.setCurrentUserDepart(deptDataByUserList);
			List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(currentUser.getEmployeeId());//????????????
			if(subEmployeeIdList!=null&&subEmployeeIdList.size()>0){
				empApplicationBusiness.setSubEmployeeIdList(subEmployeeIdList);
			}
			pm.setPageNo(page);
			pm.setPageSize(rows);
			
			//????????????
			Integer firstDepart = empApplicationBusiness.getFirstDepart();//?????????????????????
			Integer secondDepart = empApplicationBusiness.getSecondDepart();//?????????????????????
			List<Integer> departList = new ArrayList<Integer>();
			if(null != secondDepart){//?????????????????????
				departList.add(secondDepart);
			}else if(null == secondDepart && null != firstDepart){//????????????????????????
				//????????????????????????????????????????????????????????????????????????
				departList.add(firstDepart);
				List<Depart> departs = departService.getByParentId(Long.valueOf(firstDepart+""));
				departs.forEach((Depart dp) -> {
					departList.add(Integer.parseInt(dp.getId()+""));
				});
			}
			empApplicationBusiness.setDepartList(departList);
			if(null != empApplicationBusiness.getEndTime()){
				empApplicationBusiness.setEndTime(DateUtils.parse(DateUtils.format(empApplicationBusiness.getEndTime(),DateUtils.FORMAT_SHORT) + " 23:59:59"));
			
			}
			Integer total = empApplicationBusinessMapper.getReportCount(empApplicationBusiness);
			pm.setTotal(total);
			
			
			empApplicationBusiness.setOffset(pm.getOffset());
			empApplicationBusiness.setLimit(pm.getLimit());
			List<EmpApplicationBusiness> ogs = getReportList(empApplicationBusiness);
	
			pm.setRows(ogs);
			return pm;
		}	
	}
	
	/**
	  * exportReport(????????????????????????)
	  * @Title: exportReport
	  * @Description: ????????????????????????
	  * @param empApplicationOutgoing
	  * @return    ????????????
	  * HSSFWorkbook    ????????????
	  * @throws
	 */
	public HSSFWorkbook exportReport(EmpApplicationBusiness empApplicationBusiness){
		//????????????
		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
		if(deptDataByUserList==null || deptDataByUserList.size()==0){
			return null;
		}else{
			empApplicationBusiness.setCurrentUserDepart(deptDataByUserList);
			List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(currentUser.getEmployeeId());//????????????
			if(subEmployeeIdList!=null&&subEmployeeIdList.size()>0){
				empApplicationBusiness.setSubEmployeeIdList(subEmployeeIdList);
			}
			Integer firstDepart = empApplicationBusiness.getFirstDepart();//?????????????????????
			Integer secondDepart = empApplicationBusiness.getSecondDepart();//?????????????????????
			List<Integer> departList = new ArrayList<Integer>();
			if(null != secondDepart){//?????????????????????
				departList.add(secondDepart);
			}else if(null == secondDepart && null != firstDepart){//????????????????????????
				//????????????????????????????????????????????????????????????????????????
				departList.add(firstDepart);
				List<Depart> departs = departService.getByParentId(Long.valueOf(firstDepart+""));
				departs.forEach((Depart dp) -> {
					departList.add(Integer.parseInt(dp.getId()+""));
				});
			}
			empApplicationBusiness.setDepartList(departList);
			if(null != empApplicationBusiness.getEndTime()){
				empApplicationBusiness.setEndTime(DateUtils.parse(DateUtils.format(empApplicationBusiness.getEndTime(),DateUtils.FORMAT_SHORT) + " 23:59:59"));
			}
			
			List<EmpApplicationBusiness> list = getReportList(empApplicationBusiness);
			List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
			Map<String, Object> map = null;
			
			for(EmpApplicationBusiness bus:list){
				map = new HashMap<String,Object>();
				map.put("code", bus.getCode());
				map.put("cnName", bus.getCnName());
				map.put("departName", bus.getDepartName());
				map.put("positionName", bus.getPositionName());
				map.put("workType", bus.getWorkType());
				map.put("startTime", DateUtils.format(bus.getStartTime(),DateUtils.FORMAT_SHORT));
				map.put("endTime", DateUtils.format(bus.getEndTime(),DateUtils.FORMAT_SHORT));
				map.put("address", bus.getAddress());
				map.put("duration", bus.getDuration());
				map.put("vehicle", getVehicle(bus.getVehicle()));
				map.put("reason", bus.getReason());
				map.put("submitDate", DateUtils.format(bus.getSubmitDate(),DateUtils.FORMAT_LONG));
				map.put("auditUser", bus.getAuditUser());
				map.put("approvalStatusDesc", bus.getApprovalStatusDesc());
				
				datas.add(map);
			}
			
			String[] titles = { "????????????","????????????","??????","????????????","?????????","????????????","????????????","????????????","????????????","????????????","????????????","????????????","?????????","??????"};
			String[] keys = { "code","cnName","departName","positionName","workType","startTime","endTime","duration","address","vehicle","reason",
					"submitDate","auditUser","approvalStatusDesc"};
			
			return ExcelUtil.exportExcel(datas,keys,titles,"workbook.xls");
		}	
	}
	
	public String getVehicle(Integer vehicle){
		if(null == vehicle){return "";};
		
		if(vehicle.equals(EmpApplicationBusiness.VEHICLE_TRAIN)){
			return "??????";
		}else if(vehicle.equals(EmpApplicationBusiness.VEHICLE_AIR)){
			return "??????";
		}else if(vehicle.equals(EmpApplicationBusiness.VEHICLE_BUS)){
			return "??????";
		}else{
			return vehicle + "";
		}
	}
	@Override
	public EmpApplicationBusiness queryByProcessInstanceId(String instanceId) {
		return empApplicationBusinessMapper.queryByProcessId(instanceId);
	}
	@Override
	public void setValueToVO(TaskVO taskVO, String processInstanceId) {
		//????????????
			EmpApplicationBusiness business = queryByProcessInstanceId(processInstanceId);
			taskVO.setProcessName("???????????????");
			String redirectUrl = "/empApplicationBusiness/approval.htm?flag=no&businessId="+business.getId();
			if(taskVO.getProcessStatu()!=null) {
				redirectUrl = "/empApplicationBusiness/approval.htm?flag=can&businessId="+business.getId();
			}
			taskVO.setCreatorDepart(business.getDepartName());
			taskVO.setProcessStatu(business.getApprovalStatus());
			taskVO.setOperation(ConfigConstants.STATUS.getOperationByStatu(business.getApprovalStatus()));
			taskVO.setCreator(business.getCreateUser());
			taskVO.setCreateTime(business.getCreateTime());
			taskVO.setView3(business.getReason());
			taskVO.setView4(business.getAddress());
			String view5 = DateUtils.getTime(business.getStartTime(), business.getEndTime(),"MM-dd", "yyyy-MM-dd") + "&nbsp;&nbsp;" + business.getDuration() + "???";
			taskVO.setView5(view5);
			taskVO.setView6(OaCommon.getVehicleTypeMap().get(business.getVehicle()));
			taskVO.setReProcdefCode(ConfigConstants.BUSINESS_CODE);
			taskVO.setProcessId(business.getProcessinstanceId());
			taskVO.setResourceId(String.valueOf(business.getId()));
			taskVO.setRedirectUrl(redirectUrl);
		
	}
	@Override
	public EmpApplicationBusiness queryByReportProcessInstanceId(String instanceId) {
		return empApplicationBusinessMapper.queryByReportProcessId(instanceId);
	}
	
	@Override
	public void setValueToReportVO(TaskVO taskVO, String processInstanceId) {
		//??????????????????
		EmpApplicationBusiness business = queryByReportProcessInstanceId(processInstanceId);
		if(business!=null){
			taskVO.setProcessName("??????????????????");
			String redirectUrl = "/empApplicationBusiness/approveWorkSummary.htm?flag=no&businessId="+business.getId();
			if(taskVO.getProcessStatu()!=null) {
				redirectUrl = "/empApplicationBusiness/approveWorkSummary.htm?flag=can&businessId="+business.getId();
			}
			taskVO.setCreatorDepart(business.getDepartName());
			taskVO.setProcessStatu(business.getApprovalReportStatus());
			taskVO.setOperation(ConfigConstants.STATUS.getOperationByStatu(business.getApprovalReportStatus()));
			taskVO.setCreator(business.getCreateUser());
			taskVO.setCreateTime(business.getCreateTime());
			taskVO.setView3(business.getReason());
			taskVO.setView4(business.getAddress());
			String view5 = DateUtils.getTime(business.getStartTime(), business.getEndTime(),"MM-dd", "yyyy-MM-dd") + "&nbsp;&nbsp;" + business.getDuration() + "???";
			taskVO.setView5(view5);
			taskVO.setView6(OaCommon.getVehicleTypeMap().get(business.getVehicle()));
			taskVO.setReProcdefCode(ConfigConstants.BUSINESSREPORT_CODE);
			taskVO.setProcessId(business.getProcessinstanceReportId());
			taskVO.setResourceId(String.valueOf(business.getId()));
			taskVO.setRedirectUrl(redirectUrl);
		}
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void completeTask(HttpServletRequest request,String instanceId, String comment, String commentType) throws Exception {
		
		User user = userService.getCurrentUser();
		logger.info("???????????????completeTask??????:instanceId="+instanceId+";comment="+comment+";commentType="+commentType
				+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));
		
		Task task = activitiServiceImpl.queryTaskByProcessInstanceId(instanceId);
		EmpApplicationBusiness business = queryByProcessInstanceId(instanceId);
		// ??????
		if(business!=null){
			boolean isHeader =employeeService.isLeaderByEmpId(business.getEmployeeId());
			boolean type = isHeader?StringUtils.equalsIgnoreCase(task.getTaskDefinitionKey(), "personnelLeader"):StringUtils.startsWithIgnoreCase(task.getTaskDefinitionKey(), "end_");
			boolean isPersonnel = task.getName().contains("??????") ? true : false;
			if((StringUtils.equalsIgnoreCase(commentType, ConfigConstants.PASS)|| StringUtils.equalsIgnoreCase(commentType, ConfigConstants.REFUSE)) && annualVacationService.check5WorkingDayNextmonth(DateUtils.format(business.getStartTime(), DateUtils.FORMAT_SHORT),isPersonnel)){
				logger.error(userService.getCurrentUser().getEmployee().getCnName()+"????????????Id="+business.getEmployeeId()+"???????????????:?????????????????????,???????????????");
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
				business.setApprovalStatus(approvalStatus);
				business.setUpdateTime(new Date());
				business.setUpdateUser(user.getEmployee().getCnName());
				updateById(business,"1");
				//????????????????????????????????????assignee,??????????????????????????????????????????
				if(StringUtils.isBlank(task.getAssignee())) {
					activitiServiceImpl.claimTask(task.getId(), String.valueOf(userService.getCurrentUser().getEmployeeId()));
				}
				sendMailService.sendCommentMail(employeeService.getByEmpId(business.getEmployeeId()).get(0).getEmail(), "???????????????",task.getName()+"-"+user.getEmployee().getCnName(), approvalStatus);
				//-----------------start-----------------------????????????????????????
				ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
				flow.setAssigneeName(user.getEmployee().getCnName());
				flow.setDepartName(user.getDepart().getName());
				flow.setPositionName(user.getPosition().getPositionName());
				flow.setFinishTime(new Date());
				flow.setComment(comment);
				flow.setProcessId(instanceId);
				flow.setProcessKey(ConfigConstants.BUSINESS_KEY);
				flow.setTaskId(task.getTaskDefinitionKey());
				flow.setStatu(approvalStatus);
				viewTaskInfoService.save(flow);
				//-----------------end-------------------------
				activitiServiceImpl.completeTask(task.getId(), comment,null,commentType);
			}
		}else{
			logger.error("???????????????"+instanceId+"?????????????????????????????????");
			throw new OaException("???????????????"+instanceId+"?????????????????????????????????");
		}
	
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void completeReportTask(HttpServletRequest request,String instanceId, String comment, String commentType) throws Exception {
		
		User user = userService.getCurrentUser();
		logger.info("??????????????????completeReportTask??????:instanceId="+instanceId+";comment="+comment+";commentType="+commentType
				+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));
		
		Task task = activitiServiceImpl.queryTaskByProcessInstanceId(instanceId);
		EmpApplicationBusiness business = queryByReportProcessInstanceId(instanceId);
		boolean type = StringUtils.startsWithIgnoreCase(task.getTaskDefinitionKey(), "end_");
		String taskId="";
		if(business!=null){
			if(annualVacationService.check5WorkingDayNextmonth(DateUtils.format(business.getStartTime(), DateUtils.FORMAT_SHORT),type)){
				logger.error(userService.getCurrentUser().getEmployee().getCnName()+"????????????Id="+business.getEmployeeId()+"???????????????:?????????????????????,???????????????");
				throw new OaException("????????????????????????,???????????????");
			}else{
				Integer approvalStatus ;
				if(StringUtils.isBlank(commentType)) {
					approvalStatus=0;
					taskId="START";
					comment = business.getReason();//?????????????????????????????????
				}else if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.PASS)) {
					if(type) {
						taskId="RESIGN_COO";
						approvalStatus=ConfigConstants.PASS_STATUS;
					}else {
						taskId = task.getTaskDefinitionKey();
						approvalStatus=ConfigConstants.DOING_STATUS;
					}
				}else if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.REFUSE)) {
					approvalStatus=ConfigConstants.REFUSE_STATUS;
				}else{
					approvalStatus=ConfigConstants.BACK_STATUS;
				}
				business.setApprovalReportStatus(approvalStatus==0?100:approvalStatus);
				business.setUpdateTime(new Date());
				business.setUpdateUser(user.getEmployee().getCnName());
				updateById(business,"2");
				//????????????????????????????????????assignee,??????????????????????????????????????????
				if(StringUtils.isBlank(task.getAssignee())) {
					activitiServiceImpl.claimTask(task.getId(), String.valueOf(userService.getCurrentUser().getEmployeeId()));
				}
				sendMailService.sendCommentMail(employeeService.getByEmpId(business.getEmployeeId()).get(0).getEmail(), "?????????????????????", task.getName()+"-"+user.getEmployee().getCnName(), approvalStatus);
				//-----------------start-----------------------????????????????????????
				ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
				flow.setAssigneeName(user.getEmployee().getCnName());
				flow.setDepartName(user.getDepart().getName());
				flow.setPositionName(user.getPosition().getPositionName());
				flow.setFinishTime(new Date());
				flow.setComment(comment);
				flow.setProcessId(instanceId);
				flow.setProcessKey(ConfigConstants.BUSINESSREPORT_KEY);
				flow.setTaskId(taskId);
				flow.setStatu(approvalStatus);
				viewTaskInfoService.save(flow);
				//-----------------end-------------------------
				if(StringUtils.isBlank(commentType)) {
					activitiServiceImpl.completeTask(task.getId());
				}else {
					activitiServiceImpl.completeTask(task.getId(), comment,null,commentType);
				}
			}
		}else{
			logger.error("???????????????"+instanceId+"???????????????????????????????????????");
			throw new OaException("???????????????"+instanceId+"???????????????????????????????????????");
		}
		
	}

	@Override
	@Transactional()
	public Map<String, String> startReportTask(String reProcdefCode, Employee employee, String entityId) throws Exception{
		Map<String, String> result = Maps.newHashMap();
		Map<String,Object> variables = Maps.newHashMap(); 
		EmpApplicationBusiness business = getById(Long.valueOf(entityId));
		variables.put("proposerId", business.getEmployeeId());
		//????????????????????????
		Depart depart = departService.getInfoByEmpId(business.getEmployeeId());
		//?????????????????????????????????VEP????????????????????????????????????????????????VEP
		boolean headerIsVEP = false;//????????????????????????VEP
		List<Config> vepList = configService.getListByCode("VEP");
		for(Config data:vepList){
			Employee param = new Employee();
			param.setCode(data.getDisplayCode());
			Employee vep = employeeService.getByCondition(param);
			if(vep!=null&&vep.getId()!=null&&vep.getId().toString().equals(depart.getLeaderId())){
				headerIsVEP = true;
				break;
			}
		}
		variables.put("headerIsVEP", headerIsVEP);
		boolean isVEPManager = false;//?????????VEP???????????????
		List<Config> conList = configService.getListByCode("VEP_MANAGER_DEPART");
		for(Config config:conList){
			if(config.getDisplayCode().equals(depart.getCode())){
				isVEPManager = true;
				break;
			}
		}
		boolean needLeader = false;//??????????????????????????????
		List<Config> needLeaderList = configService.getListByCode("business_apply_nedd_leader_approve_depart");
		for(Config config:needLeaderList){
			if(config.getDisplayCode().equals(depart.getCode())){
				needLeader = true;
				break;
			}
		}
		variables.put("needLeader", needLeader);
		variables.put("isVEPManager", isVEPManager);
		variables.put("employeeId",business.getEmployeeId());
		String reportId = activitiServiceImpl.startByKey(ConfigConstants.BUSINESSREPORT_KEY,variables);
		activitiServiceImpl.setAssignessToTask(activitiServiceImpl.queryTaskByProcessInstanceId(reportId).getId(), business.getEmployeeId().toString());
		business.setProcessinstanceReportId(reportId);
		business.setApprovalReportStatus(ConfigConstants.ASK_STATUS);
		empApplicationBusinessMapper.updateById(business);
		result.put("code", OaCommon.CODE_OK);
		return result;
	}

	@Override
	public void updateProcessInstanceId(EmpApplicationBusiness newBusiness) {
		empApplicationBusinessMapper.updateById(newBusiness);
	}
	
	/**
	 * ??????????????????
	 * @throws Exception 
	 */
	@Override
	public Map<String, Object> update(HttpServletRequest request, User user) throws Exception {
		
		logger.info("-------------????????????????????????--------------");
		Map<String,Object> map = new HashMap<String, Object>();
		String originalBillId = request.getParameter("originalBillId");//????????????id
		String list = request.getParameter("businessDetailList");
		String travelList = request.getParameter("travelClassList");
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		String address = request.getParameter("address");
		String vehicle = request.getParameter("vehicle");
		String businessType = request.getParameter("businessType");
		String reason = request.getParameter("reason");
		String duration = request.getParameter("duration");
		String travelProvinceBeginEnd = request.getParameter("travelProvinceBeginEnd");
		String travelCityBeginEnd = request.getParameter("travelCityBeginEnd");
		
		if(StringUtils.isBlank(originalBillId)){
			throw new OaException("?????????????????????????????????");
		}
		
		EmpApplicationBusiness originalBill = empApplicationBusinessMapper.getById(Long.valueOf(originalBillId));
		
		if(originalBill==null){
			throw new OaException("?????????????????????????????????");
		}
		
		if(StringUtils.isBlank(startTime)){
			throw new OaException("???????????????????????????");
		}
		if(StringUtils.isBlank(endTime)){
			throw new OaException("???????????????????????????");
		}
		//????????????????????????????????????????????????18:00??????????????????
		//????????????18?????????
		//???????????????????????????
		Date originalBillEndTime = originalBill.getEndTime();
		//????????????????????????????????????
		Date nextWorkDay = originalBillEndTime;
		for(int i = 1; i < 15; i++){
			nextWorkDay = DateUtils.addDay(nextWorkDay, i);
			if(annualVacationService.getDayType(nextWorkDay) == 1){
				break;
			};
		}
		//???18????????????????????????????????????????????????
		Date lastUpdateTime = DateUtils.parse(DateUtils.format(nextWorkDay, DateUtils.FORMAT_SHORT)+ " 18:00:00", DateUtils.FORMAT_LONG);
		Date updateDate = new Date();
		if(updateDate.getTime()>lastUpdateTime.getTime()){
			throw new OaException("?????????????????????????????????????????????????????????????????????????????????18:00??????????????????");
		}
		if(StringUtils.isBlank(travelProvinceBeginEnd)){
			throw new OaException("????????????????????????");
		}
		if(StringUtils.isBlank(travelCityBeginEnd)){
			throw new OaException("????????????????????????");
		}
		if(StringUtils.isBlank(endTime)){
			throw new OaException("???????????????????????????");
		}
		if(StringUtils.isBlank(duration)||"0".equals(duration)){
			throw new OaException("??????????????????0???");
		}
		if(StringUtils.isBlank(address)){
			throw new OaException("?????????????????????");
		}
		if(StringUtils.isBlank(vehicle)){
			throw new OaException("???????????????????????????");
		}
		if(StringUtils.isBlank(businessType)){
			throw new OaException("???????????????????????????");
		}
		if(StringUtils.isBlank(reason)){
			throw new OaException("??????/???????????????????????????");
		}
		EmpApplicationBusiness business = new EmpApplicationBusiness();
		business.setOriginalBillId(Long.parseLong(originalBillId));		//??????????????????id
		business.setEmployeeId(user.getEmployeeId());
		business.setCnName(user.getEmployee().getCnName());
		business.setCode(user.getEmployee().getCode());
		business.setDepartId(user.getDepart().getId());
		business.setDepartName(user.getDepart().getName());
		business.setPositionId(user.getPosition().getId());
		business.setPositionName(user.getPosition().getPositionName());
		business.setStartTime(DateUtils.parse(startTime,DateUtils.FORMAT_SHORT));
		business.setEndTime(DateUtils.parse(endTime,DateUtils.FORMAT_SHORT));
		business.setDuration(Double.valueOf(duration));
		business.setVehicle(Integer.valueOf(vehicle));
		business.setBusinessType(Integer.valueOf(businessType));
		business.setReason(reason);
		business.setDelFlag(CommonPo.STATUS_NORMAL);
		business.setVersion(null);
		business.setCreateTime(new Date());
		business.setCreateUser(user.getEmployee().getCnName());
		business.setSubmitDate(new Date());
		business.setApprovalStatus(EmpApplicationBusiness.APPROVAL_STATUS_WAIT);
		List<EmpApplicationBusinessDetail> detailList = new ArrayList<EmpApplicationBusinessDetail>();
		JSONArray array = JSONArray.fromObject(list);
		JSONArray addressList = JSONArray.fromObject(travelList);
		if(array==null||array.size()<=0){
			throw new OaException("????????????????????????????????????????????????");
		}
		for (int i = 0; i < addressList.size(); i++) {			
			JSONObject jsonObject = JSONObject.fromObject(addressList.get(i), new JsonConfig());
			if(!jsonObject.toString().equals("{}")){
				switch(i){
					case 0:business.setTravelProvince1(jsonObject.getString("travelProvince"));
					   	   business.setTravelCity1(jsonObject.getString("travelCity"));
					    break;
					case 1:business.setTravelProvince2(jsonObject.getString("travelProvince"));
						   business.setTravelCity2(jsonObject.getString("travelCity"));
					    break;
					case 2:business.setTravelProvince3(jsonObject.getString("travelProvince"));
				   	   	   business.setTravelCity3(jsonObject.getString("travelCity"));
					    break;
					case 3:business.setTravelProvince4(jsonObject.getString("travelProvince"));
				   	   	   business.setTravelCity4(jsonObject.getString("travelCity"));
					    break;
					case 4:business.setTravelProvince5(jsonObject.getString("travelProvince"));
						   business.setTravelCity5(jsonObject.getString("travelCity"));
					    break;
				}
			}
		}
		business.setAddress(address);
		business.setStartProvinceAddress(travelProvinceBeginEnd);
		business.setStartCityAddress(travelCityBeginEnd);
		business.setEndProvinceAddress(travelProvinceBeginEnd);
		business.setEndCityAddress(travelCityBeginEnd);
		for(int i=0;i<array.size();i++){
			EmpApplicationBusinessDetail detail = new EmpApplicationBusinessDetail();
			JSONObject jsonObject = JSONObject.fromObject(array.get(i), new JsonConfig());
			if(!jsonObject.containsKey("workStartDate")){
	        	throw new OaException("?????????????????????????????????");
			}
	        if(StringUtils.isBlank(jsonObject.getString("workStartDate"))){
	        	throw new OaException("?????????????????????????????????");
			}
	        if(!jsonObject.containsKey("workEndDate")){
	        	throw new OaException("?????????????????????????????????");
			}
	        if(StringUtils.isBlank(jsonObject.getString("workEndDate"))){
	        	throw new OaException("?????????????????????????????????");
			}
	        if(!jsonObject.containsKey("workPlan")){
	        	throw new OaException("?????????????????????????????????");
			}
	        if(StringUtils.isBlank(jsonObject.getString("workPlan"))){
	        	throw new OaException("?????????????????????????????????");
			}
			detail.setWorkPlan(jsonObject.getString("workPlan"));
			detail.setWorkObjective(jsonObject.getString("workTarget"));
			detail.setDelFlag(CommonPo.STATUS_NORMAL);
			detail.setVersion(null);
			detail.setCreateTime(new Date());
			detail.setCreateUser(user.getUserName());
			detail.setWorkStartDate(DateUtils.parse(jsonObject.getString("workStartDate"),DateUtils.FORMAT_SHORT));
			detail.setWorkEndDate(DateUtils.parse(jsonObject.getString("workEndDate"),DateUtils.FORMAT_SHORT));
			detailList.add(detail);
		}
		
		//??????????????????????????????????????????
		Task task = activitiServiceImpl.queryTaskByProcessInstanceId(originalBill.getProcessinstanceId());
		
		//????????????
		ViewTaskInfoTbl flowOriginal = new ViewTaskInfoTbl();
		flowOriginal.setAssigneeName(user.getEmployee().getCnName());
		flowOriginal.setDepartName(user.getDepart().getName());
		flowOriginal.setPositionName(user.getPosition().getPositionName());
		flowOriginal.setFinishTime(new Date());
		flowOriginal.setComment("????????????");
		flowOriginal.setProcessId(originalBill.getProcessinstanceId());
		flowOriginal.setProcessKey(ConfigConstants.BUSINESS_KEY);
		flowOriginal.setTaskId("");
		flowOriginal.setStatu(ConfigConstants.BACK_STATUS);
		viewTaskInfoService.save(flowOriginal);
		
		if(task!=null){
			//????????????
			activitiServiceImpl.completeTask(task.getId(),"????????????",null,ConfigConstants.BACK);
		}else{

			//????????????????????????
			AttnWorkHours attnWorkHours = new AttnWorkHours();
			attnWorkHours.setDelFlag(CommonPo.STATUS_DELETE);
			attnWorkHours.setBillId(originalBill.getId());
			attnWorkHours.setDataType(Integer.valueOf(RunTask.RUN_CODE_80));
			attnWorkHours.setUpdateTime(new Date());
			attnWorkHours.setUpdateUser(user.getEmployee().getCnName());
			attnWorkHours.setDataReason("????????????");
			attnWorkHoursMapper.deleteByBillId(attnWorkHours);
			
			//??????????????????????????????
			if(originalBill.getProcessinstanceReportId()!=null){
				
				Task reportTask = activitiServiceImpl.queryTaskByProcessInstanceId(originalBill.getProcessinstanceReportId());
				
				//????????????
				ViewTaskInfoTbl reportFlow = new ViewTaskInfoTbl();
				reportFlow.setAssigneeName(user.getEmployee().getCnName());
				reportFlow.setDepartName(user.getDepart().getName());
				reportFlow.setPositionName(user.getPosition().getPositionName());
				reportFlow.setFinishTime(new Date());
				reportFlow.setComment("????????????");
				reportFlow.setProcessId(originalBill.getProcessinstanceReportId());
				reportFlow.setProcessKey(ConfigConstants.BUSINESSREPORT_KEY);
				reportFlow.setTaskId("");
				reportFlow.setStatu(ConfigConstants.BACK_STATUS);
				viewTaskInfoService.save(reportFlow);
				
				if(reportTask!=null){
					//??????????????????
					activitiServiceImpl.completeTask(reportTask.getId(),"????????????",null,ConfigConstants.BACK);
				}
				
				//??????????????????
				originalBill.setApprovalReportStatus(ConfigConstants.BACK_STATUS);
				empApplicationBusinessMapper.updateById(originalBill);
				
			}else{
				//????????????????????????
				timingRunTaskMapper.deleteByCodeAndEntityId(RunTask.RUN_CODE_80, String.valueOf(originalBill.getId()));
				
			}
			
			//??????????????????
			TransNormal transNormal = new TransNormal();
			transNormal.setEmployeeIds(String.valueOf(originalBill.getEmployeeId()));
			transNormal.setStartTime(originalBill.getStartTime());
			transNormal.setEndTime(originalBill.getEndTime());
			transNormalService.recalculateAttnByCondition(transNormal);
		}
		
		//??????????????????
		originalBill.setApprovalStatus(ConfigConstants.BACK_STATUS);
		empApplicationBusinessMapper.updateById(originalBill);
		
		//?????????????????????
		Map<String ,Object> variables = setProcessVariables(business,user);
		logger.info("-------------????????????--------------");
		String processInstanceId = activitiServiceImpl.startByKey(ConfigConstants.BUSINESS_KEY, variables);
		business.setProcessinstanceId(processInstanceId);
		logger.info("-------------?????????????????????????????????--------------");
		empApplicationBusinessMapper.save(business);
		//-----------------start-----------------------????????????????????????
		ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
		flow.setAssigneeName(business.getCnName());
		flow.setDepartName(business.getDepartName());
		flow.setPositionName(business.getPositionName());
		flow.setFinishTime(business.getSubmitDate());
		flow.setComment(business.getReason());
		flow.setTaskId("proposer");
		flow.setProcessId(processInstanceId);
		flow.setProcessKey(ConfigConstants.BUSINESS_KEY);
		flow.setStatu(0);
		logger.info("-------------????????????????????????--------------");
		viewTaskInfoService.save(flow);
		if(business.getId()!=null && detailList != null && detailList.size()>0){
			for(EmpApplicationBusinessDetail detail:detailList){
				detail.setBusinessId(business.getId());
			}
			logger.info("-------------???????????????????????????????????????--------------");
			empApplicationBusinessDetailMapper.batchSave(detailList);
		}
		map.put("success", true);
		logger.info("-------------????????????????????????--------------");
		return map;
	}
	
	/**
     * ??????????????????
     * @param str
     * @return
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            //??????\t?????????\n????????????\r????????????\t
            Matcher m = ConfigConstants.REPLACE_BLACK_PATTERN.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }
    
    
    public static String replaceChinese(String str) {
        String dest = "";
        if (str!=null) {
            Matcher m = ConfigConstants.REPLACE_CHINESE_PATTERN.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

	@Override
	public void completeReportTaskBySystem(String instanceId, String comment,
			String commentType) throws Exception {
		Task task = activitiServiceImpl.queryTaskByProcessInstanceId(instanceId);
		if(task==null){
			throw new OaException("???????????????"+instanceId+"?????????????????????");
		}
		EmpApplicationBusiness business = queryByReportProcessInstanceId(instanceId);
		boolean type = StringUtils.startsWithIgnoreCase(task.getTaskDefinitionKey(), "end_");
		String taskId="";
		if(business!=null){
			Integer approvalStatus ;
			if(StringUtils.isBlank(commentType)) {
				approvalStatus=0;
				taskId="START";
				//?????????????????????????????????
				comment = business.getReason();
			}else if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.PASS)) {
				if(type) {
					taskId="RESIGN_COO";
					approvalStatus=ConfigConstants.PASS_STATUS;
				}else {
					taskId = task.getTaskDefinitionKey();
					approvalStatus=ConfigConstants.DOING_STATUS;
				}
			}else if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.REFUSE)) {
				approvalStatus=ConfigConstants.REFUSE_STATUS;
			}else{
				approvalStatus=ConfigConstants.BACK_STATUS;
			}
			//???????????????????????????????????????
			List<String> assigneeIdList = employeeService.getAssigneeIdListByProcinstId(instanceId);
			if(assigneeIdList==null||assigneeIdList.size()<=0){
				throw new OaException("???????????????"+instanceId+"????????????????????????");
			}
			Employee assignee  = employeeService.getById(Long.valueOf(assigneeIdList.get(0)));
			Position assigneeP = positionMapper.getByEmpId(Long.valueOf(assigneeIdList.get(0)));
			Depart assigneeD = departService.getInfoByEmpId(Long.valueOf(assigneeIdList.get(0)));
			business.setApprovalReportStatus(approvalStatus==0?100:approvalStatus);
			business.setUpdateTime(new Date());
			business.setUpdateUser(assignee.getCnName());
			updateById(business,"2");
			//????????????????????????????????????assignee,??????????????????????????????????????????
			if(StringUtils.isBlank(task.getAssignee())) {
				activitiServiceImpl.claimTask(task.getId(),assigneeIdList.get(0));
			}
			sendMailService.sendCommentMail(employeeService.getByEmpId(business.getEmployeeId()).get(0).getEmail(), "?????????????????????", task.getName()+"-"+assignee.getCnName(), approvalStatus);
			//-----------------start-----------------------????????????????????????
			ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
			flow.setAssigneeName(assignee.getCnName());
			flow.setDepartName(assigneeD.getName());
			flow.setPositionName(assigneeP.getPositionName());
			flow.setFinishTime(new Date());
			flow.setComment(comment);
			flow.setProcessId(instanceId);
			flow.setProcessKey(ConfigConstants.BUSINESSREPORT_KEY);
			flow.setTaskId(taskId);
			flow.setStatu(approvalStatus);
			viewTaskInfoService.save(flow);
			//-----------------end-------------------------
			if(StringUtils.isBlank(commentType)) {
				activitiServiceImpl.completeTask(task.getId());
			}else {
				activitiServiceImpl.completeTask(task.getId(), comment,null,commentType);
			}
		}else{
			logger.error("???????????????"+instanceId+"???????????????????????????????????????");
			throw new OaException("???????????????"+instanceId+"???????????????????????????????????????");
		}
		
	}

	@Override
	public PageModel<EmpApplicationBusiness> getGroupListByInfo(
			EmpApplicationBusiness empApplicationBusiness, Integer typeNum) {
		
		int page = empApplicationBusiness.getPage() == null ? 0 : empApplicationBusiness.getPage();
		int rows = empApplicationBusiness.getRows() == null ? 0 : empApplicationBusiness.getRows();
		
		PageModel<EmpApplicationBusiness> pm = new PageModel<EmpApplicationBusiness>();
		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
		if(deptDataByUserList==null || deptDataByUserList.size()==0){
			pm.setPageNo(0);
			pm.setPageSize(0);
			pm.setTotal(0);
			empApplicationBusiness.setOffset(pm.getOffset());
			empApplicationBusiness.setLimit(pm.getLimit());
			pm.setRows(new ArrayList<EmpApplicationBusiness>());
			return pm;
		}else{
			empApplicationBusiness.setCurrentUserDepart(deptDataByUserList);
			pm.setPageNo(page);
			pm.setPageSize(rows);
			Integer total=1;
			List<EmpApplicationBusiness> ogs =null;
			empApplicationBusiness.setOffset(pm.getOffset());
			empApplicationBusiness.setLimit(pm.getLimit());
			empApplicationBusiness.setEmpTypeIdList(configService.getNeedEmpTypeIdList());
	
			Map<Long, EmpApplicationBusiness> empMap=new LinkedHashMap<Long, EmpApplicationBusiness>();
			//typeNum: 0??????????????????1??????????????????2??????????????????3???????????????
			switch (typeNum) {
			case 0:
				Date firstEntryTime = DateUtils.parse(String.valueOf(empApplicationBusiness.getYear())+"-12-31 23:59:59", DateUtils.FORMAT_LONG);
				empApplicationBusiness.setFirstEntryTime(firstEntryTime);
				Date quitTime = DateUtils.parse(String.valueOf(empApplicationBusiness.getYear())+"-01-01 00:00:00", DateUtils.FORMAT_LONG);
				empApplicationBusiness.setQuitTime(quitTime);
				List<EmpApplicationBusiness> getEmpPageList = empApplicationBusinessMapper.getEmpPageList(empApplicationBusiness);//??????????????????
			    total = empApplicationBusinessMapper.getEmpPageListCount(empApplicationBusiness);
				List<EmpApplicationBusiness> businessList = empApplicationBusinessMapper.getUserGroupList(empApplicationBusiness);			
				for (EmpApplicationBusiness business : businessList) {
					empMap.put(business.getEmployeeId(), business);
				}
				for (EmpApplicationBusiness emp: getEmpPageList){
					if(empMap!=null&&empMap.containsKey(emp.getEmployeeId())){					
						emp.setFrequencyNum(empMap.get(emp.getEmployeeId()).getFrequencyNum());
						emp.setPeopleNum(empMap.get(emp.getEmployeeId()).getPeopleNum());
						emp.setDuration(empMap.get(emp.getEmployeeId()).getDuration());
					}
		        }
				ogs=getEmpPageList;
				break;
			case 1:
				List<Integer> deptIdsByUser = rabcUserRoleService.getDeptIdsByUser(currentUser.getEmployeeId());
				empApplicationBusiness.setDepartList(deptIdsByUser);
				List<EmpApplicationBusiness> getDepartList = empApplicationBusinessMapper.getDepartList(empApplicationBusiness);//??????????????????
			    total = empApplicationBusinessMapper.getDepartListCount(empApplicationBusiness);
				List<EmpApplicationBusiness> businessDepartList = empApplicationBusinessMapper.getDepartGroupList(empApplicationBusiness);
				
				for (EmpApplicationBusiness business : businessDepartList) {
					empMap.put(business.getDepartId(), business);
				}
				for (EmpApplicationBusiness emp: getDepartList){
					if(empMap!=null&&empMap.containsKey(emp.getDepartId())){					
						emp.setFrequencyNum(empMap.get(emp.getDepartId()).getFrequencyNum());
						emp.setPeopleNum(empMap.get(emp.getDepartId()).getPeopleNum());
						emp.setDuration(empMap.get(emp.getDepartId()).getDuration());
					}
		        }
				ogs=getDepartList;
				break;
			case 2:
				total=1;
				ogs=empApplicationBusinessMapper.getYearGroupList(empApplicationBusiness);
				if(ogs!=null && ogs.size()==0){
					empApplicationBusiness.setPeopleNum(0);
					empApplicationBusiness.setDuration(0d);
					empApplicationBusiness.setFrequencyNum(0);
					ogs.add(empApplicationBusiness);
				}
				break;
			case 3:
				total=1;
				ogs=empApplicationBusinessMapper.getAdreessGroupList(empApplicationBusiness);
				for (EmpApplicationBusiness empApplicationBusiness2 : ogs) {
					empApplicationBusiness2.setAddress(empApplicationBusiness.getAddress());
				}
				break;
			}
			pm.setTotal(total);
			pm.setRows(ogs);
		}	
		return pm;
	}

	
	
	
	@Override
	public PageModel<EmpApplicationBusiness> getApproveList(
			EmpApplicationBusiness empApplicationBusiness, Integer typeNum) {
		// TODO Auto-generated method stub
		int page = empApplicationBusiness.getPage() == null ? 0 : empApplicationBusiness.getPage();
		int rows = empApplicationBusiness.getRows() == null ? 0 : empApplicationBusiness.getRows();		
		PageModel<EmpApplicationBusiness> pm = new PageModel<EmpApplicationBusiness>();		
		pm.setPageNo(page);
		pm.setPageSize(rows);
		Integer total=1;
		List<EmpApplicationBusiness> ogs =null;
		empApplicationBusiness.setOffset(pm.getOffset());
		empApplicationBusiness.setLimit(pm.getLimit());
		//typeNum: 0?????????1??????,2??????
		switch (typeNum) {
		case 0:
			empApplicationBusiness.setAssignee(String.valueOf(userService.getCurrentUser().getEmployee().getId()));
			empApplicationBusiness.setApprovalStatus(100);
			total=empApplicationBusinessMapper.getReportCount(empApplicationBusiness);
			ogs=empApplicationBusinessMapper.getReportPageList(empApplicationBusiness);
			break;
		case 1:
			empApplicationBusiness.setAssignee(String.valueOf(userService.getCurrentUser().getEmployee().getId()));
			total=empApplicationBusinessMapper.myTaskListCount(empApplicationBusiness);
			ogs=empApplicationBusinessMapper.myTaskList(empApplicationBusiness);
			break;
		case 2:
			empApplicationBusiness.setAssignee(String.valueOf(userService.getCurrentUser().getEmployee().getId()));
			empApplicationBusiness.setApprovalStatus(500);
			total=empApplicationBusinessMapper.getReportCount(empApplicationBusiness);
			ogs=empApplicationBusinessMapper.getReportPageList(empApplicationBusiness);
			break;	
		}
		pm.setTotal(total);
		pm.setRows(ogs);
		return pm;
	}

	@Override
	public HSSFWorkbook exportGroupReport(
			EmpApplicationBusiness empApplicationBusiness, Integer typeNum) {
		// TODO Auto-generated method stub
		
		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
		if(deptDataByUserList==null || deptDataByUserList.size()==0){
			return null;
		}else{
			empApplicationBusiness.setCurrentUserDepart(deptDataByUserList);
			List<EmpApplicationBusiness> ogs=null;
			empApplicationBusiness.setEmpTypeIdList(configService.getNeedEmpTypeIdList());
			Map<Long, EmpApplicationBusiness> empMap=new LinkedHashMap<Long, EmpApplicationBusiness>();
		//typeNum: 0??????????????????1??????????????????2??????????????????3???????????????
		
			switch (typeNum) {
			case 0: 
				Date firstEntryTime = DateUtils.parse(String.valueOf(empApplicationBusiness.getYear())+"-12-31 23:59:59", DateUtils.FORMAT_LONG);
				empApplicationBusiness.setFirstEntryTime(firstEntryTime);
				Date quitTime = DateUtils.parse(String.valueOf(empApplicationBusiness.getYear())+"-01-01 00:00:00", DateUtils.FORMAT_LONG);
				empApplicationBusiness.setQuitTime(quitTime);
				List<EmpApplicationBusiness> getEmpPageList = empApplicationBusinessMapper.getEmpPageList(empApplicationBusiness);//??????????????????
				List<EmpApplicationBusiness> businessList = empApplicationBusinessMapper.getUserGroupList(empApplicationBusiness);			
				for (EmpApplicationBusiness business : businessList) {
					empMap.put(business.getEmployeeId(), business);
				}
				for (EmpApplicationBusiness emp: getEmpPageList){
					if(empMap!=null&&empMap.containsKey(emp.getEmployeeId())){					
						emp.setFrequencyNum(empMap.get(emp.getEmployeeId()).getFrequencyNum());
						emp.setPeopleNum(empMap.get(emp.getEmployeeId()).getPeopleNum());
						emp.setDuration(empMap.get(emp.getEmployeeId()).getDuration());
					}
		        }
				ogs=getEmpPageList;
				break;
			case 1:
				List<Integer> deptIdsByUser = rabcUserRoleService.getDeptIdsByUser(currentUser.getEmployeeId());
				empApplicationBusiness.setDepartList(deptIdsByUser);
				List<EmpApplicationBusiness> getDepartList = empApplicationBusinessMapper.getDepartList(empApplicationBusiness);//??????????????????
				List<EmpApplicationBusiness> businessDepartList = empApplicationBusinessMapper.getDepartGroupList(empApplicationBusiness);
				
				for (EmpApplicationBusiness business : businessDepartList) {
					empMap.put(business.getDepartId(), business);
				}
				for (EmpApplicationBusiness emp: getDepartList){
					if(empMap!=null&&empMap.containsKey(emp.getDepartId())){					
						emp.setFrequencyNum(empMap.get(emp.getDepartId()).getFrequencyNum());
						emp.setPeopleNum(empMap.get(emp.getDepartId()).getPeopleNum());
						emp.setDuration(empMap.get(emp.getDepartId()).getDuration());
					}
		        }
				ogs=getDepartList;
				break;
			case 2:
				ogs=empApplicationBusinessMapper.getYearGroupList(empApplicationBusiness);
				if(ogs!=null && ogs.size()==0){
					empApplicationBusiness.setPeopleNum(0);
					empApplicationBusiness.setDuration(0d);
					empApplicationBusiness.setFrequencyNum(0);
					ogs.add(empApplicationBusiness);
				}
				break;
			case 3:
				ogs=empApplicationBusinessMapper.getAdreessGroupList(empApplicationBusiness);
				for (EmpApplicationBusiness empApplicationBusiness2 : ogs) {
					empApplicationBusiness2.setAddress(empApplicationBusiness.getAddress());
				}
				break;
			}
			List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
			Map<String, Object> map = null;
			for(EmpApplicationBusiness bus:ogs){
				map = new HashMap<String,Object>();
				map.put("year", empApplicationBusiness.getYear()+"???");
				map.put("cnName", bus.getCnName());
				map.put("departName", bus.getDepartName());
				map.put("address", bus.getAddress());
				map.put("peopleNum", bus.getPeopleNum());
				map.put("frequencyNum", bus.getFrequencyNum());
				map.put("duration",bus.getDuration());
				map.put("money","");
				datas.add(map);
			}
			String[] titles = { "??????","??????","??????","??????","??????","??????","??????","??????????????????"};
			String[] keys = {"year","cnName","departName","address","peopleNum","frequencyNum","duration","money"};
			return ExcelUtil.exportExcel(datas,keys,titles,"workbook.xls");
		}	
}

	@Override
	public void completeTaskByAdmin(HttpServletRequest request,String processId, String comment,
			String commentType, User user, Task task) throws Exception {
		
		logger.info("???????????????completeTaskByAdmin??????:processId="+processId+";comment="+comment+";commentType="+commentType
				+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));
		
		EmpApplicationBusiness business = queryByProcessInstanceId(processId);
		// ??????
		if(business!=null){
			boolean isHeader =employeeService.isLeaderByEmpId(business.getEmployeeId());
			boolean type = isHeader?StringUtils.equalsIgnoreCase(task.getTaskDefinitionKey(), "personnelLeader"):StringUtils.startsWithIgnoreCase(task.getTaskDefinitionKey(), "end_");
	
			Integer approvalStatus ;
			if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.PASS)) {
				approvalStatus=type?ConfigConstants.PASS_STATUS:ConfigConstants.DOING_STATUS;
				
			}else if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.REFUSE)) {
				approvalStatus=ConfigConstants.REFUSE_STATUS;
			}else if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.OVERDUEPASS)){
				/**????????????**/
				approvalStatus =type?ConfigConstants.OVERDUEPASS_STATUS:ConfigConstants.DOING_STATUS;
			}else if((StringUtils.equalsIgnoreCase(commentType, ConfigConstants.OVERDUEREFUSE))){
				/**????????????**/
				approvalStatus = ConfigConstants.OVERDUEREFUSE_STATUS;
			}else {
				approvalStatus=ConfigConstants.BACK_STATUS;
			}
			business.setApprovalStatus(approvalStatus);
			business.setUpdateTime(new Date());
			business.setUpdateUser(user.getEmployee().getCnName());
			updateById(business,"1");
			//????????????????????????????????????assignee,??????????????????????????????????????????
			if(StringUtils.isBlank(task.getAssignee())) {
				activitiServiceImpl.claimTask(task.getId(), String.valueOf(userService.getCurrentUser().getEmployeeId()));
			}
			sendMailService.sendCommentMail(employeeService.getByEmpId(business.getEmployeeId()).get(0).getEmail(), "???????????????",task.getName()+"-"+user.getEmployee().getCnName(), approvalStatus);
			//-----------------start-----------------------????????????????????????
			ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
			flow.setAssigneeName(user.getEmployee().getCnName());
			flow.setDepartName(user.getDepart().getName());
			flow.setPositionName(user.getPosition().getPositionName());
			flow.setFinishTime(new Date());
			flow.setComment(comment);
			flow.setProcessId(processId);
			flow.setProcessKey(ConfigConstants.BUSINESS_KEY);
			flow.setTaskId(task.getTaskDefinitionKey());
			flow.setStatu(approvalStatus);
			viewTaskInfoService.save(flow);
			//-----------------end-------------------------
			activitiServiceImpl.completeTask(task.getId(), comment,null,commentType);
			
		}else{
			logger.error("???????????????"+processId+"???????????????????????????????????????");
			throw new OaException("???????????????"+processId+"???????????????????????????????????????");
		}
		
	}

	@Override
	public void completeTaskBySystem(HttpServletRequest request, String instanceId, String comment, String commentType)
			throws Exception {
		User user = userService.getCurrentUser();
		logger.info("???????????????completeTask??????:instanceId="+instanceId+";comment="+comment+";commentType="+commentType
				+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));
		
		Task task = activitiServiceImpl.queryTaskByProcessInstanceId(instanceId);
		EmpApplicationBusiness business = queryByProcessInstanceId(instanceId);
		// ??????
		if(business!=null){
			boolean isHeader =employeeService.isLeaderByEmpId(business.getEmployeeId());
			boolean type = isHeader?StringUtils.equalsIgnoreCase(task.getTaskDefinitionKey(), "personnelLeader"):StringUtils.startsWithIgnoreCase(task.getTaskDefinitionKey(), "end_");
			
			Integer approvalStatus ;
			if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.PASS)) {
				approvalStatus=type?ConfigConstants.PASS_STATUS:ConfigConstants.DOING_STATUS;
				
			}else if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.REFUSE)) {
				approvalStatus=ConfigConstants.REFUSE_STATUS;
			}else {
				approvalStatus=ConfigConstants.BACK_STATUS;
			}
			business.setApprovalStatus(approvalStatus);
			business.setUpdateTime(new Date());
			business.setUpdateUser("?????????");
			updateById(business,"1");
			//????????????????????????????????????assignee,??????????????????????????????????????????
			if(StringUtils.isBlank(task.getAssignee())) {
				activitiServiceImpl.claimTask(task.getId(), String.valueOf(userService.getCurrentUser().getEmployeeId()));
			}
			sendMailService.sendCommentMail(employeeService.getByEmpId(business.getEmployeeId()).get(0).getEmail(), "???????????????",task.getName()+"-"+"?????????", approvalStatus);
			//-----------------start-----------------------????????????????????????
			ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
			flow.setAssigneeName("?????????");
			flow.setDepartName("??????????????????");
			flow.setPositionName("????????????");
			flow.setFinishTime(new Date());
			flow.setComment(comment);
			flow.setProcessId(instanceId);
			flow.setProcessKey(ConfigConstants.BUSINESS_KEY);
			flow.setTaskId(task.getTaskDefinitionKey());
			flow.setStatu(approvalStatus);
			viewTaskInfoService.save(flow);
			//-----------------end-------------------------
			activitiServiceImpl.completeTask(task.getId(), comment,null,commentType);
			
		}else{
			logger.error("???????????????"+instanceId+"?????????????????????????????????");
			throw new OaException("???????????????"+instanceId+"?????????????????????????????????");
		}
		
	}
}
