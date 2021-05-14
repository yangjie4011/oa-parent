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
 * @ClassName: 出差申请表
 * @Description: 出差申请表
 * @author yangjie
 * @date 2017年6月13日
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
		//无论business状态是什么都要执行
		empApplicationBusinessMapper.updateById(business);
		if(business.getApprovalStatus().intValue() == EmpApplicationBusiness.APPROVAL_STATUS_YES) {
			EmpApplicationBusiness old = empApplicationBusinessMapper.getById(business.getId());
			//出差
			if(type.equals("1")&&business.getApprovalStatus().intValue()==ConfigConstants.PASS_STATUS.intValue()){
				//出差审批完成,生成任务表,充时间
				TimingRunTask timingRunTask = new TimingRunTask();
				timingRunTask.setProcessStatus(TimingRunTask.PROCESS_STATUS0);
				timingRunTask.setReProcdefCode(ConfigConstants.BUSINESS_CODE);
				timingRunTask.setCreatorId(business.getEmployeeId());
				timingRunTask.setEntityId(business.getId().toString());
				timingRunTask.setStartTime(business.getEndTime());
				timingRunTask.setCreateUser(user.getEmployee().getCnName());
				timingRunTaskMapper.save(timingRunTask);
				//添加工时
				//如果是修改申请单,则将原单据添加的工时修改为删除标识
				/*if(business.getOriginalBillId() != null){
					//查询原申请单添加的工时
					EmpApplicationBusiness originalBill = empApplicationBusinessMapper.getById(Long.valueOf(business.getOriginalBillId()));
					//删除上次申请单添加的工时
					Date startTime = originalBill.getStartTime();
					Date endTime = originalBill.getEndTime();
					attnStatisticsMapper.deleteOriginalBillWorkHour(startTime,endTime,originalBill.getEmployeeId());
					
				}*/
				Employee employee = employeeService.getById(old.getEmployeeId());
				AttnStatisticsUtil attnStatisticsUtil= new AttnStatisticsUtil();
				//充值考勤
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
			throw new OaException("去程日期不能为空！");
		}
		//18点前只能提昨天及之后的出差，超过18点就只能提当日及当日后的出差
		//获取当日18点时间
		String nowDate18Str = DateUtils.getNow("yyyy-MM-dd") + " 18:00:00";
		Date nowDate18 = DateUtils.parse(nowDate18Str);
		Date startDate = DateUtils.parse(startTime+" 00:00:00");
		//将18点时间与当前时间比较
		int compareDate = DateUtils.compareDate(nowDate18, new Date());
		if(compareDate <= 1){
			//18点前，可提昨日出差
			//获取昨日零点日期
			Date yesterday = DateUtils.parse(DateUtils.format(DateUtils.addDay(new Date(), -1), "yyyy-MM-dd")+" 00:00:00");
			//出差开始日期在昨天之前
			if(DateUtils.compareDate(startDate,yesterday) > 1){
				throw new OaException("18点前可申请昨日及之后的出差！");
			}
		}else{
			//18点后，可提今日出差
			//获取今日零点日期
			Date nowDate = DateUtils.parse(DateUtils.getNow("yyyy-MM-dd") + " 00:00:00");
			if(DateUtils.compareDate(startDate,nowDate) > 1){
				throw new OaException("18点后只能申请今日及之后的出差！");
			}
		}
		if(StringUtils.isBlank(travelProvinceBeginEnd)){
			throw new OaException("始发地不能为空！");
		}
		if(StringUtils.isBlank(travelCityBeginEnd)){
			throw new OaException("始发地不能为空！");
		}
		if(StringUtils.isBlank(endTime)){
			throw new OaException("返程日期不能为空！");
		}
		if(StringUtils.isBlank(duration)||"0".equals(duration)){
			throw new OaException("时长必须大于0！");
		}
		if(StringUtils.isBlank(address)){
			throw new OaException("地点不能为空！");
		}
		if(StringUtils.isBlank(vehicle)){
			throw new OaException("交通工具不能为空！");
		}
		if(StringUtils.isBlank(businessType)){
			throw new OaException("出差事由不能为空！");
		}
		if(StringUtils.isBlank(reason)){
			throw new OaException("项目/业务名称不能为空！");
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
			throw new OaException("每日行程及工作计划安排不能为空！");
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
	        	throw new OaException("工作计划安排不能为空！");
			}
	        if(jsonObject.getString("workPlan")==null||"".equals(jsonObject.getString("workPlan"))){
	        	throw new OaException("工作计划安排不能为空！");
			}
	        if(!jsonObject.containsKey("workStartDate")){
	        	throw new OaException("任务开始时间不能为空！");
	        }
	        if(jsonObject.getString("workStartDate")==null||"".equals(jsonObject.getString("workStartDate"))){
	        	throw new OaException("任务开始时间不能为空！");
			}
	        if(!jsonObject.containsKey("workEndDate")){
	        	throw new OaException("任务结束时间不能为空！");
	        }
	        if(jsonObject.getString("workEndDate")==null||"".equals(jsonObject.getString("workEndDate"))){
	        	throw new OaException("任务结束时间不能为空！");
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
		//开启流程
		Map<String ,Object> variables = setProcessVariables(business,user);
		String processInstanceId = activitiServiceImpl.startByKey(ConfigConstants.BUSINESS_KEY, variables);
		business.setProcessinstanceId(processInstanceId);
		empApplicationBusinessMapper.save(business);
		//-----------------start-----------------------保存流程节点信息
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
		//查询员工所在部门是否是VEP负责的部门与所在部门负责人是否是VEP
		//查询所有的VEP（分管副总裁）
		boolean headerIsVEP = false;//部门负责人是否是VEP
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
		boolean isVEPManager = false;//是否是VEP负责的部门
		List<Config> conList = configService.getListByCode("VEP_MANAGER_DEPART");
		for(Config config:conList){
			if(config.getDisplayCode().equals(user.getDepart().getCode())){
				isVEPManager = true;
				break;
			}
		}
		boolean needLeader = false;//是否需要汇报对象审批
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
		
		logger.info("出差总结报告saveWorkSummary入参:list="+list+";id="+id+";remark="+remark
				+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		
		EmpApplicationBusiness business = getById(Long.valueOf(id));
		business.setRemark(remark);
		empApplicationBusinessMapper.updateById(business);
		JSONArray array = JSONArray.fromObject(list);
		if(array==null||array.size()<=0){
			throw new OaException("每日行程及任务完成情况不能为空！");
		}
		for(int i=0;i<array.size();i++){
			EmpApplicationBusinessDetail detail = new EmpApplicationBusinessDetail();
			JSONObject jsonObject = JSONObject.fromObject(array.get(i), new JsonConfig());
			if(!jsonObject.containsKey("workSummary")){
				throw new OaException("每日行程及任务完成情况不能为空！");
			}
			if(jsonObject.getString("workSummary")==null||"".equals(jsonObject.getString("workSummary"))){
				throw new OaException("每日行程及任务完成情况不能为空！");
			}
			detail.setId(jsonObject.getLong("id"));
			detail.setBusinessId(Long.valueOf(id));
			detail.setWorkSummary(jsonObject.getString("workSummary"));
			detail.setUpdateTime(new Date());
			detail.setUpdateUser(user.getEmployee().getCnName());
			empApplicationBusinessDetailMapper.updateById(detail);
		}
		//提交流程
		completeReportTask(request,business.getProcessinstanceReportId(),"",null);
		map.put("success", true);
		return map;
	}

	/**
	 * @throws OaException 
	 * @throws MessagingException 
	 * @throws IOException 
	 * @throws DocumentException 
	  * exportApplyResultByBusinessId(导出出差申请表)
	  * @Title: exportApplyResultByBusinessId
	  * @Description: 导出出差申请表
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
		//发送邮件
		SendMailUtil.sendPdfMail(toMail, "出差申请表", filePath);

	}
	
	/**
	  * exportApplyReportByBusinessId(导出出差总结报告)
	  * @Title: exportApplyReportByBusinessId
	  * @Description: 导出出差总结报告
	  * @throws
	 */
	@Override
	public void exportApplyReportByBusinessId(Long businessId,Long ruProcdefId) {
		List<EmpApplicationBusinessDetail> detail = getBusinessDetailList(businessId);//明细信息
		EmpApplicationBusiness head = getById(businessId);//头部信息

		try {
			String filePath = ConfigConstants.OA_RSAKEY  + SendMailUtil.formatPath(MD5Encoder.md5Hex(DateUtils.getNow())) + ".pdf";
			
			//生成pdf
			writeApplyResult(head, detail,viewTaskInfoService.queryTasksByProcessId(head.getProcessinstanceReportId()),filePath);
			
			//发送邮件
			Employee currentEmployee = employeeService.getCurrentEmployee();
			String toMail = currentEmployee.getEmail();
//			String toMail = "minsheng@ule.com";
			
			logger.info("流程id="+ businessId +"出差总结报告发送至" + toMail);
			
			SendMailUtil.sendPdfMail(toMail, "出差总结报告", filePath);
		} catch (Exception e) {
			
		}
	}
	
	public RuProcdef getSignInfo(String entryId,String procdefCode,String nodeModule){
		RuProcdef runTask = new RuProcdef();
		runTask.setEntityId(entryId);
		runTask.setReProcdefCode(procdefCode);
		runTask.setNodeModule(nodeModule);
		
		return ruProcdefMapper.getRunTaskByCodeAndEntry(runTask);//流程
	}
	
	/**
	 * @throws DocumentException 
	 * @throws FileNotFoundException 
	  * testWriteApplyResult(这里用一句话描述这个方法的作用)
	  * @Title: testWriteApplyResult
	  * @Description: 生成出差总结报告
	  * void    返回类型
	  * @throws
	 */
	public void writeApplyResult(EmpApplicationBusiness head,List<EmpApplicationBusinessDetail> details,List<ViewTaskInfoTbl> flows,String filePath) throws FileNotFoundException, DocumentException{
		//创建文件
		Document document = new Document();
		//创建一个书写器
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
		
		//打开文件
		document.open();
		
		Integer contentFontSize = 12;
		
		try{
			//添加表头
			document.add(PDFUtils.setParagraph("出差总结报告", 15, FontStyle.BOLD.getValue(),PDFUtils.ALIGN_CENTER));
			
			//建表(6列)
			PdfPTable table = PDFUtils.createTable(12,100,10,0);
			
			//获得表格中的列
			List<PdfPRow> listRow = table.getRows();
			//设置列宽
			float[] columnWidths = { 4f, 4f, 4f, 4f ,4f ,4f, 4f, 4f, 4f, 4f ,4f ,4f};
			table.setWidths(columnWidths);
	
			//第一行
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
			//第一行单元格
			cells[0] = new PdfPCell(PDFUtils.setParagraph("姓    名", contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));//单元格内容
			cells[0].setColspan(2);
			cells[2] = new PdfPCell(PDFUtils.setParagraph(CommonUtils.converToString(head.getCnName()), contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[2].setColspan(2);
			cells[4] = new PdfPCell(PDFUtils.setParagraph("职务", contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[4].setColspan(2);
			cells[6] = new PdfPCell(PDFUtils.setParagraph(CommonUtils.converToString(head.getPositionName()), contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[6].setColspan(2);
			cells[8] = new PdfPCell(PDFUtils.setParagraph("出差地点", contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[8].setColspan(2);
			cells[10] = new PdfPCell(PDFUtils.setParagraph(CommonUtils.converToString(substring), contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[10].setColspan(2);
			listRow.add(row);
			
			//第二行
			cells = new PdfPCell[12];
			row = new PdfPRow(cells);
			
			//第二行单元格
			cells[0] = new PdfPCell(PDFUtils.setParagraph("出差时长", contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));//单元格内容
			cells[0].setColspan(2);
			cells[2] = new PdfPCell(PDFUtils.setParagraph(String.valueOf(head.getDuration()) + "天", contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[2].setColspan(4);
			cells[6] = new PdfPCell(PDFUtils.setParagraph("总结日期", contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[6].setColspan(2);
			//总结日期
			Date sumaryDate = null;
			if(null != flows && flows.size() > 0 ){
				sumaryDate = flows.get(flows.size() - 1).getFinishTime();
			}
			cells[8] = new PdfPCell(PDFUtils.setParagraph(DateUtils.format(sumaryDate), contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[8].setColspan(4);
			listRow.add(row);
			
			//第三行
			cells = new PdfPCell[12];
			row = new PdfPRow(cells);
			
			//第三行单元格
			cells[0] = new PdfPCell(PDFUtils.setParagraph("出差事由", contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));//单元格内容
			cells[0].setColspan(2);
			cells[2] = new PdfPCell(PDFUtils.setParagraph(CommonUtils.converToString(head.getReason()), contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[2].setColspan(10);
			listRow.add(row);
			
			//第三行
			cells = new PdfPCell[12];
			row = new PdfPRow(cells);
			
			//第三行单元格
			cells[0] = new PdfPCell(PDFUtils.setParagraph("出差路线", contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));//单元格内容
			cells[0].setColspan(2);
			cells[2] = new PdfPCell(PDFUtils.setParagraph(CommonUtils.converToString(head.getAddress()+"（按此路线报销往返交通费）"), contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[2].setColspan(10);
			listRow.add(row);
			
			//第四行
			cells = new PdfPCell[12];
			row = new PdfPRow(cells);
			
			//第四行单元格
			cells[0] = new PdfPCell(PDFUtils.setParagraph("出差每日行程及任务完成情况:(可附页)", contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));//单元格内容
			cells[0].setColspan(12);
			listRow.add(row);
			
			//第五行
			cells = new PdfPCell[12];
			row = new PdfPRow(cells);
			
			//第五行单元格
			cells[0] = new PdfPCell(PDFUtils.setParagraph("时间段", contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//单元格内容
			cells[0].setColspan(2);
			cells[2] = new PdfPCell(PDFUtils.setParagraph("工作内容及任务完成情况", contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//单元格内容
			cells[2].setColspan(8);
			cells[10] = new PdfPCell(PDFUtils.setParagraph("备注", contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//单元格内容
			cells[10].setColspan(2);
			listRow.add(row);
			
			//第六行
			cells = new PdfPCell[12];
			row = new PdfPRow(cells);
			
			//第六行单元格
			int count = 0;
			int index = 0;
			int whiteCount = 5;//空白行
			EmpApplicationBusinessDetail detail = null;
			while(count < 3){
				cells[index] = new PdfPCell();
				PdfPTable tItemTable= PDFUtils.createTable(1,100,0,0);
				cells[index].addElement(tItemTable);//将表格嵌套到单元格中
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
				
				if(count == 0){//第一个单元格跨1行
					cells[index].setColspan(2);
					index += 2;
				}else if(count == 1){//第二个单元格跨4行
					cells[index].setColspan(8);
					index += 8;
				}else if(count == 2){//第三个单元格跨1行
					cells[index].setColspan(2);
				}
				count++;
			}
			listRow.add(row);
			
			//第七行
			cells = new PdfPCell[12];
			row = new PdfPRow(cells);
			
			//第七行单元格
			cells[0] = new PdfPCell(PDFUtils.setParagraph("实际总费用", contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//单元格内容
			cells[0].setColspan(2);
			cells[2] = new PdfPCell(PDFUtils.setParagraph(" ", contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//单元格内容
			cells[2].setColspan(4);
			cells[6] = new PdfPCell(PDFUtils.setParagraph("超预算金额", contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//单元格内容
			cells[6].setColspan(4);
			cells[10] = new PdfPCell(PDFUtils.setParagraph(" ", contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//单元格内容
			cells[10].setColspan(2);
			listRow.add(row);
			
			//第八行
			cells = new PdfPCell[12];
			row = new PdfPRow(cells);
			
			//第八行单元格
			whiteCount = 8;//空白行
			cells[0] = new PdfPCell();
			PdfPTable tItemTable= PDFUtils.createTable(1,100,0,0);
			cells[0].addElement(tItemTable);//将表格嵌套到单元格中
			cells[0].setColspan(12);
			//空白行
			String tmpStr = "";
			while(whiteCount > 0){
				if(whiteCount == 8){tmpStr = "超支说明:";}else{tmpStr = " ";}
				cell = new PdfPCell(PDFUtils.setParagraph(tmpStr, contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));
				cell.setBorder(0);
				tItemTable.addCell(cell);
				whiteCount--;
			}
			listRow.add(row);
			
			//第九行
			cells = new PdfPCell[12];
			row = new PdfPRow(cells);
			
			//第九行单元格
			cells[0] = new PdfPCell(PDFUtils.setParagraph("报 告 人", contentFontSize, FontStyle.BOLD.getValue(), PDFUtils.ALIGN_CENTER));//单元格内容
			cells[0].setColspan(3);
			cells[3] = new PdfPCell(PDFUtils.setParagraph("部门主管审批", contentFontSize, FontStyle.BOLD.getValue(), PDFUtils.ALIGN_CENTER));//单元格内容
			cells[3].setColspan(3);
			cells[6] = new PdfPCell(PDFUtils.setParagraph("分管副总裁审批", contentFontSize, FontStyle.BOLD.getValue(), PDFUtils.ALIGN_CENTER));//单元格内容
			cells[6].setColspan(3);
			cells[9] = new PdfPCell(PDFUtils.setParagraph("CEO/COO审批", contentFontSize, FontStyle.BOLD.getValue(), PDFUtils.ALIGN_CENTER));//单元格内容
			cells[9].setColspan(3);
			listRow.add(row);
			
			//第十行
			cells = new PdfPCell[12];
			row = new PdfPRow(cells);
			
			//第十行单元格
			count = 0;//第几个单元格
			index = 0;//单元格下标
			ViewTaskInfoTbl flow01 = null;//提交人签字
			ViewTaskInfoTbl flow02 = null;//主管签字
			ViewTaskInfoTbl flow03 = null;//coo签字
			ViewTaskInfoTbl flow04 = null;//VEP签字
			ViewTaskInfoTbl flow05 = null;//VEP签字
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
			//流程倒叙
			while(count <= 3){
				String account = "";
				String time = "";
				ViewTaskInfoTbl task = null; 
				ViewTaskInfoTbl task1 = null; 
				ViewTaskInfoTbl task2 = null; 
				if(count == 0){
					task = flow01;//流程
				}else if(count == 1){
					task = flow02;//流程
				}else if(count == 2){
					//查询员工所在部门是否是VEP负责的部门与所在部门负责人是否是VEP
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
					task = flow03;//流程
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
				cells[index].addElement(tItemTable);//将表格嵌套到单元格中
				cell = new PdfPCell(PDFUtils.setParagraph("签字:" + CommonUtils.converToString(account), contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
				cell.setBorder(0);
				tItemTable.addCell(cell);
				//增加空行
				whiteCount = 3;
				while(whiteCount > 0){
					cell = new PdfPCell(PDFUtils.setParagraph(" ", contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
					cell.setBorder(0);
					whiteCount --;
					tItemTable.addCell(cell);
				}
				cell = new PdfPCell(PDFUtils.setParagraph("日期:" + time, contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
				cell.setBorder(0);
				tItemTable.addCell(cell);
				cells[index].setColspan(3);

				index += 3;
				count++;
			}
			listRow.add(row);
			
			//把表格添加到文件中
			document.add(table);
			
			try{
				//生成二维码
				String OA_URL = configCacheManager.getConfigDisplayCode("MO_WEB_URL");
				String codePath = OA_URL +"/empApplicationBusiness/approveWorkSummary.htm?businessId="+head.getId()+"&flag=no";
//				+ "/runTask/nonAuth/toView.htm?empId="+head.getEmployeeId()+"&ruTaskId="+ hiActinstList.get(0).getRuTaskId() 
//							+ "&sign=" + MD5Encoder.md5Hex(hiActinstList.get(0).getRuTaskId() + "");
				String imagePath = ConfigConstants.OA_RSAKEY  + SendMailUtil.formatPath(MD5Encoder.md5Hex(DateUtils.getNow())) + ".png";//二维码临时地址
				QRCodeUtils.encode(codePath, imagePath, BarcodeFormat.QR_CODE, 20, 20);//生成二维码
				Image image = Image.getInstance(imagePath);
				image.setAlignment(PDFUtils.ALIGN_RIGHT);
				document.add(image);//将二维码添加到pdf中
				File delfile = new File(imagePath);//删除二维码
		        if(!delfile.delete()) {
		        	logger.error("删除文件失败");
		        }

		        //生成二维码备注信息
		        document.add(PDFUtils.setParagraph("扫一扫，查看电子单据内容", contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_RIGHT));
		        document.add(PDFUtils.setParagraph("此单据内容由邮乐MO系统出具-" + System.currentTimeMillis() + head.getId(), contentFontSize, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_RIGHT));
			}catch(Exception e){
				logger.error("生成出差总结报告二维码失败,失败原因={}",e);
			}
		}catch(Exception e){
			
		}finally{
			//关闭文档
			document.close();
			//关闭书写器
			writer.close();
		}
	}
	
	public List<EmpApplicationBusiness> getReportList(EmpApplicationBusiness empApplicationBusiness){
		List<EmpApplicationBusiness> buss = empApplicationBusinessMapper.getReportPageList(empApplicationBusiness);
		
		//设置第一级审批人
		buss.forEach((EmpApplicationBusiness bus) -> {
			try{
				ViewTaskInfoTbl taskInfo = null;
				taskInfo = viewTaskInfoService.getFirstAuditUser(bus.getProcessinstanceId(),ConfigConstants.BUSINESS_KEY,true);
				if(null != taskInfo){
					bus.setAuditUser(taskInfo.getAssigneeName());
				}
			}catch(Exception e){
				bus.setAuditUser(" ");
				logger.error("单据{}获取第一级审批人出错",bus.getId());
			}
		});
		
		return buss;
	}

	/**
	  * getReportPageList(分页查询出差查询报表)
	  * @Title: getReportPageList
	  * @Description: 分页查询出差查询报表
	  * @param empApplicationOutgoing
	  * @return    设定文件
	  * PageModel<EmpApplicationOutgoing>    返回类型
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
			List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(currentUser.getEmployeeId());//下属员工
			if(subEmployeeIdList!=null&&subEmployeeIdList.size()>0){
				empApplicationBusiness.setSubEmployeeIdList(subEmployeeIdList);
			}
			pm.setPageNo(page);
			pm.setPageSize(rows);
			
			//封装部门
			Integer firstDepart = empApplicationBusiness.getFirstDepart();//页面上一级部门
			Integer secondDepart = empApplicationBusiness.getSecondDepart();//页面上二级部门
			List<Integer> departList = new ArrayList<Integer>();
			if(null != secondDepart){//选择了二级部门
				departList.add(secondDepart);
			}else if(null == secondDepart && null != firstDepart){//只选择了一级部门
				//根据一级部门将一级部门下面的所有二级部门查询出来
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
	  * exportReport(导出出差查询报表)
	  * @Title: exportReport
	  * @Description: 导出出差查询报表
	  * @param empApplicationOutgoing
	  * @return    设定文件
	  * HSSFWorkbook    返回类型
	  * @throws
	 */
	public HSSFWorkbook exportReport(EmpApplicationBusiness empApplicationBusiness){
		//封装部门
		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
		if(deptDataByUserList==null || deptDataByUserList.size()==0){
			return null;
		}else{
			empApplicationBusiness.setCurrentUserDepart(deptDataByUserList);
			List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(currentUser.getEmployeeId());//下属员工
			if(subEmployeeIdList!=null&&subEmployeeIdList.size()>0){
				empApplicationBusiness.setSubEmployeeIdList(subEmployeeIdList);
			}
			Integer firstDepart = empApplicationBusiness.getFirstDepart();//页面上一级部门
			Integer secondDepart = empApplicationBusiness.getSecondDepart();//页面上二级部门
			List<Integer> departList = new ArrayList<Integer>();
			if(null != secondDepart){//选择了二级部门
				departList.add(secondDepart);
			}else if(null == secondDepart && null != firstDepart){//只选择了一级部门
				//根据一级部门将一级部门下面的所有二级部门查询出来
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
			
			String[] titles = { "员工编号","员工姓名","部门","职位名称","工时制","开始时间","结束时间","出差天数","出差地点","交通工具","出差事由","申请日期","批核人","状态"};
			String[] keys = { "code","cnName","departName","positionName","workType","startTime","endTime","duration","address","vehicle","reason",
					"submitDate","auditUser","approvalStatusDesc"};
			
			return ExcelUtil.exportExcel(datas,keys,titles,"workbook.xls");
		}	
	}
	
	public String getVehicle(Integer vehicle){
		if(null == vehicle){return "";};
		
		if(vehicle.equals(EmpApplicationBusiness.VEHICLE_TRAIN)){
			return "火车";
		}else if(vehicle.equals(EmpApplicationBusiness.VEHICLE_AIR)){
			return "飞机";
		}else if(vehicle.equals(EmpApplicationBusiness.VEHICLE_BUS)){
			return "汽车";
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
		//出差申请
			EmpApplicationBusiness business = queryByProcessInstanceId(processInstanceId);
			taskVO.setProcessName("出差申请单");
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
			String view5 = DateUtils.getTime(business.getStartTime(), business.getEndTime(),"MM-dd", "yyyy-MM-dd") + "&nbsp;&nbsp;" + business.getDuration() + "天";
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
		//出差报告申请
		EmpApplicationBusiness business = queryByReportProcessInstanceId(processInstanceId);
		if(business!=null){
			taskVO.setProcessName("出差总结报告");
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
			String view5 = DateUtils.getTime(business.getStartTime(), business.getEndTime(),"MM-dd", "yyyy-MM-dd") + "&nbsp;&nbsp;" + business.getDuration() + "天";
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
		logger.info("出差申请单completeTask入参:instanceId="+instanceId+";comment="+comment+";commentType="+commentType
				+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));
		
		Task task = activitiServiceImpl.queryTaskByProcessInstanceId(instanceId);
		EmpApplicationBusiness business = queryByProcessInstanceId(instanceId);
		// 出差
		if(business!=null){
			boolean isHeader =employeeService.isLeaderByEmpId(business.getEmployeeId());
			boolean type = isHeader?StringUtils.equalsIgnoreCase(task.getTaskDefinitionKey(), "personnelLeader"):StringUtils.startsWithIgnoreCase(task.getTaskDefinitionKey(), "end_");
			boolean isPersonnel = task.getName().contains("人事") ? true : false;
			if((StringUtils.equalsIgnoreCase(commentType, ConfigConstants.PASS)|| StringUtils.equalsIgnoreCase(commentType, ConfigConstants.REFUSE)) && annualVacationService.check5WorkingDayNextmonth(DateUtils.format(business.getStartTime(), DateUtils.FORMAT_SHORT),isPersonnel)){
				logger.error(userService.getCurrentUser().getEmployee().getCnName()+"审批员工Id="+business.getEmployeeId()+"的出差申请:已超出有效时间,无法操作！");
				throw new OaException("已超出有效时间,无法操作！");
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
				//判断当前节点是否有办理人assignee,没有则将当前代理人设为办理人
				if(StringUtils.isBlank(task.getAssignee())) {
					activitiServiceImpl.claimTask(task.getId(), String.valueOf(userService.getCurrentUser().getEmployeeId()));
				}
				sendMailService.sendCommentMail(employeeService.getByEmpId(business.getEmployeeId()).get(0).getEmail(), "出差申请单",task.getName()+"-"+user.getEmployee().getCnName(), approvalStatus);
				//-----------------start-----------------------保存流程节点信息
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
			logger.error("流程实例为"+instanceId+"的出差审批数据不存在！");
			throw new OaException("流程实例为"+instanceId+"的出差审批数据不存在！");
		}
	
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void completeReportTask(HttpServletRequest request,String instanceId, String comment, String commentType) throws Exception {
		
		User user = userService.getCurrentUser();
		logger.info("出差总结报告completeReportTask入参:instanceId="+instanceId+";comment="+comment+";commentType="+commentType
				+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));
		
		Task task = activitiServiceImpl.queryTaskByProcessInstanceId(instanceId);
		EmpApplicationBusiness business = queryByReportProcessInstanceId(instanceId);
		boolean type = StringUtils.startsWithIgnoreCase(task.getTaskDefinitionKey(), "end_");
		String taskId="";
		if(business!=null){
			if(annualVacationService.check5WorkingDayNextmonth(DateUtils.format(business.getStartTime(), DateUtils.FORMAT_SHORT),type)){
				logger.error(userService.getCurrentUser().getEmployee().getCnName()+"审批员工Id="+business.getEmployeeId()+"的出差报告:已超出有效时间,无法操作！");
				throw new OaException("已出有效超过时间,无法操作！");
			}else{
				Integer approvalStatus ;
				if(StringUtils.isBlank(commentType)) {
					approvalStatus=0;
					taskId="START";
					comment = business.getReason();//报告的理由默认为出差的
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
				//判断当前节点是否有办理人assignee,没有则将当前代理人设为办理人
				if(StringUtils.isBlank(task.getAssignee())) {
					activitiServiceImpl.claimTask(task.getId(), String.valueOf(userService.getCurrentUser().getEmployeeId()));
				}
				sendMailService.sendCommentMail(employeeService.getByEmpId(business.getEmployeeId()).get(0).getEmail(), "出差报告申请单", task.getName()+"-"+user.getEmployee().getCnName(), approvalStatus);
				//-----------------start-----------------------保存流程节点信息
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
			logger.error("流程实例为"+instanceId+"的出差报告审批数据不存在！");
			throw new OaException("流程实例为"+instanceId+"的出差报告审批数据不存在！");
		}
		
	}

	@Override
	@Transactional()
	public Map<String, String> startReportTask(String reProcdefCode, Employee employee, String entityId) throws Exception{
		Map<String, String> result = Maps.newHashMap();
		Map<String,Object> variables = Maps.newHashMap(); 
		EmpApplicationBusiness business = getById(Long.valueOf(entityId));
		variables.put("proposerId", business.getEmployeeId());
		//查询员工所在部门
		Depart depart = departService.getInfoByEmpId(business.getEmployeeId());
		//查询员工所在部门是否是VEP负责的部门与所在部门负责人是否是VEP
		boolean headerIsVEP = false;//部门负责人是否是VEP
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
		boolean isVEPManager = false;//是否是VEP负责的部门
		List<Config> conList = configService.getListByCode("VEP_MANAGER_DEPART");
		for(Config config:conList){
			if(config.getDisplayCode().equals(depart.getCode())){
				isVEPManager = true;
				break;
			}
		}
		boolean needLeader = false;//是否需要汇报对象审批
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
	 * 修改出差单据
	 * @throws Exception 
	 */
	@Override
	public Map<String, Object> update(HttpServletRequest request, User user) throws Exception {
		
		logger.info("-------------修改出差申请开始--------------");
		Map<String,Object> map = new HashMap<String, Object>();
		String originalBillId = request.getParameter("originalBillId");//原始单据id
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
			throw new OaException("未查到原始出差申请单！");
		}
		
		EmpApplicationBusiness originalBill = empApplicationBusinessMapper.getById(Long.valueOf(originalBillId));
		
		if(originalBill==null){
			throw new OaException("未查到原始出差申请单！");
		}
		
		if(StringUtils.isBlank(startTime)){
			throw new OaException("去程日期不能为空！");
		}
		if(StringUtils.isBlank(endTime)){
			throw new OaException("返程日期不能为空！");
		}
		//原申请单的返程日期后第一个工作日18:00前可提交修改
		//获取当日18点时间
		//原申请单的返程日期
		Date originalBillEndTime = originalBill.getEndTime();
		//获取返程日期后一个工作日
		Date nextWorkDay = originalBillEndTime;
		for(int i = 1; i < 15; i++){
			nextWorkDay = DateUtils.addDay(nextWorkDay, i);
			if(annualVacationService.getDayType(nextWorkDay) == 1){
				break;
			};
		}
		//将18点时间与返程日期后一个工作日比较
		Date lastUpdateTime = DateUtils.parse(DateUtils.format(nextWorkDay, DateUtils.FORMAT_SHORT)+ " 18:00:00", DateUtils.FORMAT_LONG);
		Date updateDate = new Date();
		if(updateDate.getTime()>lastUpdateTime.getTime()){
			throw new OaException("已超过本次出差可修改时间，请在出差返程日后第一个工作日18:00前完成修改！");
		}
		if(StringUtils.isBlank(travelProvinceBeginEnd)){
			throw new OaException("始发地不能为空！");
		}
		if(StringUtils.isBlank(travelCityBeginEnd)){
			throw new OaException("始发地不能为空！");
		}
		if(StringUtils.isBlank(endTime)){
			throw new OaException("返程日期不能为空！");
		}
		if(StringUtils.isBlank(duration)||"0".equals(duration)){
			throw new OaException("时长必须大于0！");
		}
		if(StringUtils.isBlank(address)){
			throw new OaException("地点不能为空！");
		}
		if(StringUtils.isBlank(vehicle)){
			throw new OaException("交通工具不能为空！");
		}
		if(StringUtils.isBlank(businessType)){
			throw new OaException("出差事由不能为空！");
		}
		if(StringUtils.isBlank(reason)){
			throw new OaException("项目/业务名称不能为空！");
		}
		EmpApplicationBusiness business = new EmpApplicationBusiness();
		business.setOriginalBillId(Long.parseLong(originalBillId));		//保存原始单据id
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
			throw new OaException("每日行程及工作计划安排不能为空！");
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
	        	throw new OaException("任务开始时间不能为空！");
			}
	        if(StringUtils.isBlank(jsonObject.getString("workStartDate"))){
	        	throw new OaException("任务开始时间不能为空！");
			}
	        if(!jsonObject.containsKey("workEndDate")){
	        	throw new OaException("任务结束时间不能为空！");
			}
	        if(StringUtils.isBlank(jsonObject.getString("workEndDate"))){
	        	throw new OaException("任务结束时间不能为空！");
			}
	        if(!jsonObject.containsKey("workPlan")){
	        	throw new OaException("工作计划安排不能为空！");
			}
	        if(StringUtils.isBlank(jsonObject.getString("workPlan"))){
	        	throw new OaException("工作计划安排不能为空！");
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
		
		//查看老的出差申请单是否完成，
		Task task = activitiServiceImpl.queryTaskByProcessInstanceId(originalBill.getProcessinstanceId());
		
		//记录步骤
		ViewTaskInfoTbl flowOriginal = new ViewTaskInfoTbl();
		flowOriginal.setAssigneeName(user.getEmployee().getCnName());
		flowOriginal.setDepartName(user.getDepart().getName());
		flowOriginal.setPositionName(user.getPosition().getPositionName());
		flowOriginal.setFinishTime(new Date());
		flowOriginal.setComment("修改申请");
		flowOriginal.setProcessId(originalBill.getProcessinstanceId());
		flowOriginal.setProcessKey(ConfigConstants.BUSINESS_KEY);
		flowOriginal.setTaskId("");
		flowOriginal.setStatu(ConfigConstants.BACK_STATUS);
		viewTaskInfoService.save(flowOriginal);
		
		if(task!=null){
			//结束流程
			activitiServiceImpl.completeTask(task.getId(),"修改申请",null,ConfigConstants.BACK);
		}else{

			//删除老的考勤明细
			AttnWorkHours attnWorkHours = new AttnWorkHours();
			attnWorkHours.setDelFlag(CommonPo.STATUS_DELETE);
			attnWorkHours.setBillId(originalBill.getId());
			attnWorkHours.setDataType(Integer.valueOf(RunTask.RUN_CODE_80));
			attnWorkHours.setUpdateTime(new Date());
			attnWorkHours.setUpdateUser(user.getEmployee().getCnName());
			attnWorkHours.setDataReason("修改申请");
			attnWorkHoursMapper.deleteByBillId(attnWorkHours);
			
			//查看出差报告是否生成
			if(originalBill.getProcessinstanceReportId()!=null){
				
				Task reportTask = activitiServiceImpl.queryTaskByProcessInstanceId(originalBill.getProcessinstanceReportId());
				
				//记录步骤
				ViewTaskInfoTbl reportFlow = new ViewTaskInfoTbl();
				reportFlow.setAssigneeName(user.getEmployee().getCnName());
				reportFlow.setDepartName(user.getDepart().getName());
				reportFlow.setPositionName(user.getPosition().getPositionName());
				reportFlow.setFinishTime(new Date());
				reportFlow.setComment("修改申请");
				reportFlow.setProcessId(originalBill.getProcessinstanceReportId());
				reportFlow.setProcessKey(ConfigConstants.BUSINESSREPORT_KEY);
				reportFlow.setTaskId("");
				reportFlow.setStatu(ConfigConstants.BACK_STATUS);
				viewTaskInfoService.save(reportFlow);
				
				if(reportTask!=null){
					//结束报告流程
					activitiServiceImpl.completeTask(reportTask.getId(),"修改申请",null,ConfigConstants.BACK);
				}
				
				//修改单据状态
				originalBill.setApprovalReportStatus(ConfigConstants.BACK_STATUS);
				empApplicationBusinessMapper.updateById(originalBill);
				
			}else{
				//关闭报告定时任务
				timingRunTaskMapper.deleteByCodeAndEntityId(RunTask.RUN_CODE_80, String.valueOf(originalBill.getId()));
				
			}
			
			//重新计算考勤
			TransNormal transNormal = new TransNormal();
			transNormal.setEmployeeIds(String.valueOf(originalBill.getEmployeeId()));
			transNormal.setStartTime(originalBill.getStartTime());
			transNormal.setEndTime(originalBill.getEndTime());
			transNormalService.recalculateAttnByCondition(transNormal);
		}
		
		//修改单据状态
		originalBill.setApprovalStatus(ConfigConstants.BACK_STATUS);
		empApplicationBusinessMapper.updateById(originalBill);
		
		//开启新流程流程
		Map<String ,Object> variables = setProcessVariables(business,user);
		logger.info("-------------开启流程--------------");
		String processInstanceId = activitiServiceImpl.startByKey(ConfigConstants.BUSINESS_KEY, variables);
		business.setProcessinstanceId(processInstanceId);
		logger.info("-------------保存修改后的出差申请单--------------");
		empApplicationBusinessMapper.save(business);
		//-----------------start-----------------------保存流程节点信息
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
		logger.info("-------------保存流程节点信息--------------");
		viewTaskInfoService.save(flow);
		if(business.getId()!=null && detailList != null && detailList.size()>0){
			for(EmpApplicationBusinessDetail detail:detailList){
				detail.setBusinessId(business.getId());
			}
			logger.info("-------------保存修改后的出差申请单详情--------------");
			empApplicationBusinessDetailMapper.batchSave(detailList);
		}
		map.put("success", true);
		logger.info("-------------修改出差申请结束--------------");
		return map;
	}
	
	/**
     * 去除空格汉字
     * @param str
     * @return
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            //空格\t、回车\n、换行符\r、制表符\t
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
			throw new OaException("流程实例为"+instanceId+"的任务已结束！");
		}
		EmpApplicationBusiness business = queryByReportProcessInstanceId(instanceId);
		boolean type = StringUtils.startsWithIgnoreCase(task.getTaskDefinitionKey(), "end_");
		String taskId="";
		if(business!=null){
			Integer approvalStatus ;
			if(StringUtils.isBlank(commentType)) {
				approvalStatus=0;
				taskId="START";
				//报告的理由默认为出差的
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
			//查询任务当前节点所有待办人
			List<String> assigneeIdList = employeeService.getAssigneeIdListByProcinstId(instanceId);
			if(assigneeIdList==null||assigneeIdList.size()<=0){
				throw new OaException("流程实例为"+instanceId+"的任务无待办人！");
			}
			Employee assignee  = employeeService.getById(Long.valueOf(assigneeIdList.get(0)));
			Position assigneeP = positionMapper.getByEmpId(Long.valueOf(assigneeIdList.get(0)));
			Depart assigneeD = departService.getInfoByEmpId(Long.valueOf(assigneeIdList.get(0)));
			business.setApprovalReportStatus(approvalStatus==0?100:approvalStatus);
			business.setUpdateTime(new Date());
			business.setUpdateUser(assignee.getCnName());
			updateById(business,"2");
			//判断当前节点是否有办理人assignee,没有则将当前代理人设为办理人
			if(StringUtils.isBlank(task.getAssignee())) {
				activitiServiceImpl.claimTask(task.getId(),assigneeIdList.get(0));
			}
			sendMailService.sendCommentMail(employeeService.getByEmpId(business.getEmployeeId()).get(0).getEmail(), "出差报告申请单", task.getName()+"-"+assignee.getCnName(), approvalStatus);
			//-----------------start-----------------------保存流程节点信息
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
			logger.error("流程实例为"+instanceId+"的出差报告审批数据不存在！");
			throw new OaException("流程实例为"+instanceId+"的出差报告审批数据不存在！");
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
			//typeNum: 0按员工分组，1按部门分组，2按年份分组，3按地点分组
			switch (typeNum) {
			case 0:
				Date firstEntryTime = DateUtils.parse(String.valueOf(empApplicationBusiness.getYear())+"-12-31 23:59:59", DateUtils.FORMAT_LONG);
				empApplicationBusiness.setFirstEntryTime(firstEntryTime);
				Date quitTime = DateUtils.parse(String.valueOf(empApplicationBusiness.getYear())+"-01-01 00:00:00", DateUtils.FORMAT_LONG);
				empApplicationBusiness.setQuitTime(quitTime);
				List<EmpApplicationBusiness> getEmpPageList = empApplicationBusinessMapper.getEmpPageList(empApplicationBusiness);//查询全部员工
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
				List<EmpApplicationBusiness> getDepartList = empApplicationBusinessMapper.getDepartList(empApplicationBusiness);//查询全部员工
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
		//typeNum: 0代办，1已办,2失效
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
		//typeNum: 0按员工分组，1按部门分组，2按年份分组，3按地点分组
		
			switch (typeNum) {
			case 0: 
				Date firstEntryTime = DateUtils.parse(String.valueOf(empApplicationBusiness.getYear())+"-12-31 23:59:59", DateUtils.FORMAT_LONG);
				empApplicationBusiness.setFirstEntryTime(firstEntryTime);
				Date quitTime = DateUtils.parse(String.valueOf(empApplicationBusiness.getYear())+"-01-01 00:00:00", DateUtils.FORMAT_LONG);
				empApplicationBusiness.setQuitTime(quitTime);
				List<EmpApplicationBusiness> getEmpPageList = empApplicationBusinessMapper.getEmpPageList(empApplicationBusiness);//查询全部员工
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
				List<EmpApplicationBusiness> getDepartList = empApplicationBusinessMapper.getDepartList(empApplicationBusiness);//查询全部员工
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
				map.put("year", empApplicationBusiness.getYear()+"年");
				map.put("cnName", bus.getCnName());
				map.put("departName", bus.getDepartName());
				map.put("address", bus.getAddress());
				map.put("peopleNum", bus.getPeopleNum());
				map.put("frequencyNum", bus.getFrequencyNum());
				map.put("duration",bus.getDuration());
				map.put("money","");
				datas.add(map);
			}
			String[] titles = { "时间","姓名","部门","地点","人数","次数","天数","费用（暂无）"};
			String[] keys = {"year","cnName","departName","address","peopleNum","frequencyNum","duration","money"};
			return ExcelUtil.exportExcel(datas,keys,titles,"workbook.xls");
		}	
}

	@Override
	public void completeTaskByAdmin(HttpServletRequest request,String processId, String comment,
			String commentType, User user, Task task) throws Exception {
		
		logger.info("出差申请单completeTaskByAdmin入参:processId="+processId+";comment="+comment+";commentType="+commentType
				+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));
		
		EmpApplicationBusiness business = queryByProcessInstanceId(processId);
		// 出差
		if(business!=null){
			boolean isHeader =employeeService.isLeaderByEmpId(business.getEmployeeId());
			boolean type = isHeader?StringUtils.equalsIgnoreCase(task.getTaskDefinitionKey(), "personnelLeader"):StringUtils.startsWithIgnoreCase(task.getTaskDefinitionKey(), "end_");
	
			Integer approvalStatus ;
			if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.PASS)) {
				approvalStatus=type?ConfigConstants.PASS_STATUS:ConfigConstants.DOING_STATUS;
				
			}else if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.REFUSE)) {
				approvalStatus=ConfigConstants.REFUSE_STATUS;
			}else if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.OVERDUEPASS)){
				/**失效同意**/
				approvalStatus =type?ConfigConstants.OVERDUEPASS_STATUS:ConfigConstants.DOING_STATUS;
			}else if((StringUtils.equalsIgnoreCase(commentType, ConfigConstants.OVERDUEREFUSE))){
				/**失效拒绝**/
				approvalStatus = ConfigConstants.OVERDUEREFUSE_STATUS;
			}else {
				approvalStatus=ConfigConstants.BACK_STATUS;
			}
			business.setApprovalStatus(approvalStatus);
			business.setUpdateTime(new Date());
			business.setUpdateUser(user.getEmployee().getCnName());
			updateById(business,"1");
			//判断当前节点是否有办理人assignee,没有则将当前代理人设为办理人
			if(StringUtils.isBlank(task.getAssignee())) {
				activitiServiceImpl.claimTask(task.getId(), String.valueOf(userService.getCurrentUser().getEmployeeId()));
			}
			sendMailService.sendCommentMail(employeeService.getByEmpId(business.getEmployeeId()).get(0).getEmail(), "出差申请单",task.getName()+"-"+user.getEmployee().getCnName(), approvalStatus);
			//-----------------start-----------------------保存流程节点信息
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
			logger.error("流程实例为"+processId+"的出差异常审批数据不存在！");
			throw new OaException("流程实例为"+processId+"的出差异常审批数据不存在！");
		}
		
	}

	@Override
	public void completeTaskBySystem(HttpServletRequest request, String instanceId, String comment, String commentType)
			throws Exception {
		User user = userService.getCurrentUser();
		logger.info("出差申请单completeTask入参:instanceId="+instanceId+";comment="+comment+";commentType="+commentType
				+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));
		
		Task task = activitiServiceImpl.queryTaskByProcessInstanceId(instanceId);
		EmpApplicationBusiness business = queryByProcessInstanceId(instanceId);
		// 出差
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
			business.setUpdateUser("杨国雄");
			updateById(business,"1");
			//判断当前节点是否有办理人assignee,没有则将当前代理人设为办理人
			if(StringUtils.isBlank(task.getAssignee())) {
				activitiServiceImpl.claimTask(task.getId(), String.valueOf(userService.getCurrentUser().getEmployeeId()));
			}
			sendMailService.sendCommentMail(employeeService.getByEmpId(business.getEmployeeId()).get(0).getEmail(), "出差申请单",task.getName()+"-"+"杨国雄", approvalStatus);
			//-----------------start-----------------------保存流程节点信息
			ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
			flow.setAssigneeName("杨国雄");
			flow.setDepartName("管理层办公室");
			flow.setPositionName("运营总裁");
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
			logger.error("流程实例为"+instanceId+"的出差审批数据不存在！");
			throw new OaException("流程实例为"+instanceId+"的出差审批数据不存在！");
		}
		
	}
}
