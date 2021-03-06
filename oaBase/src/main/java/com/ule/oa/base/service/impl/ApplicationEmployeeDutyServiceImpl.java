package com.ule.oa.base.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.ule.oa.base.mapper.AnnualVacationMapper;
import com.ule.oa.base.mapper.ApplicationEmployeeDutyDetailMapper;
import com.ule.oa.base.mapper.ApplicationEmployeeDutyMapper;
import com.ule.oa.base.mapper.CompanyMapper;
import com.ule.oa.base.mapper.DepartMapper;
import com.ule.oa.base.mapper.EmpMsgMapper;
import com.ule.oa.base.mapper.EmployeeDutyMapper;
import com.ule.oa.base.mapper.EmployeeMapper;
import com.ule.oa.base.mapper.HiActinstMapper;
import com.ule.oa.base.po.AnnualVacation;
import com.ule.oa.base.po.ApplicationEmployeeDuty;
import com.ule.oa.base.po.ApplicationEmployeeDutyDetail;
import com.ule.oa.base.po.Company;
import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.EmpMsg;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeDuty;
import com.ule.oa.base.po.HiActinst;
import com.ule.oa.base.po.RunTask;
import com.ule.oa.base.po.SendMail;
import com.ule.oa.base.po.User;
import com.ule.oa.base.po.tbl.ViewTaskInfoTbl;
import com.ule.oa.base.service.AnnualVacationService;
import com.ule.oa.base.service.ApplicationEmployeeDutyService;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.RunTaskService;
import com.ule.oa.base.service.SendMailService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.service.ViewTaskInfoService;
import com.ule.oa.base.util.SendMailUtil;
import com.ule.oa.base.vo.TaskVO;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.po.CommonPo;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.ExcelUtil;
import com.ule.oa.common.utils.PageModel;
import com.ule.oa.common.utils.http.IPUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * @ClassName: ???????????????
 * @Description: ???????????????
 * @author yangjie
 * @date 2017???8???31???
 */
@Service
public class ApplicationEmployeeDutyServiceImpl implements
		ApplicationEmployeeDutyService {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private ApplicationEmployeeDutyMapper applicationEmployeeDutyMapper;
	@Resource
	private ApplicationEmployeeDutyDetailMapper applicationEmployeeDutyDetailMapper;
	@Resource
	private EmployeeMapper employeeMapper;
	@Autowired
	private RunTaskService runTaskService;
	@Resource
	private AnnualVacationMapper annualVacationMapper;
	@Resource
	private EmployeeDutyMapper employeeDutyMapper;
	@Autowired
	private EmpMsgMapper empMsgMapper;
	@Autowired
	private SendMailService	sendMailService;
	@Resource
	private DepartService departService;
	@Resource
	private AnnualVacationService annualVacationService;
	@Resource
	private EmployeeService employeeService;
	@Resource
	private HiActinstMapper hiActinstMapper;
	@Autowired
	private UserService userService;
	@Autowired
	private DepartMapper departMapper;
	@Autowired
	private ActivitiServiceImpl activitiServiceImpl;
	@Autowired
	private ViewTaskInfoService viewTaskInfoService;
	@Autowired
	private CompanyMapper companyMapper;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private ConfigService configService;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addEmployeeDuty(User user, ApplicationEmployeeDuty duty)
			throws OaException {
		if(duty.getVacationName()==null||"".equals(duty.getVacationName())){
			throw new OaException("????????????????????????");
		}
		if(duty.getDepartId()==null){
			throw new OaException("?????????????????????");
		}
		Depart model = new Depart();
		model.setLeader(user.getEmployee().getId());
		List<Depart> dapartList = departService.getListByLeaderOrPower(model);
		if(dapartList!=null&&dapartList.size()>0){
			boolean flag = true;
			for(Depart depart:dapartList){
				if(duty.getDepartId().equals(depart.getId())){
					flag = false;
					break;
				}
			}
			if(flag){
				throw new OaException("???????????????????????????");
			}
		}else{
			throw new OaException("???????????????????????????");
		}
		duty.setDelFlag(0);
		duty.setVersion(0L);
		duty.setClassSettingPerson(user.getEmployee().getCnName());
		duty.setCreateTime(new Date());
		duty.setCreateUser(user.getEmployee().getCnName());
		duty.setYear(DateUtils.format(new Date(),"yyyy"));
		duty.setEmployeeId(user.getEmployee().getId());
		if("??????".equals(duty.getVacationName())){
			duty.setYear(DateUtils.format(DateUtils.addYear(new Date(), 1),"yyyy"));
		}
		//???????????????????????????
		AnnualVacation vacation = new AnnualVacation();
		List<Integer> typeList = new ArrayList<Integer>();
		typeList.add(AnnualVacation.YYPE_LEGAL);
		typeList.add(AnnualVacation.YYPE_VACATION);
		vacation.setTypeList(typeList);
		vacation.setStartDate(DateUtils.parse(duty.getYear()+"-01-01", DateUtils.FORMAT_SHORT));
		vacation.setEndDate(DateUtils.parse(duty.getYear()+"-12-31", DateUtils.FORMAT_SHORT));
		vacation.setSubject(duty.getVacationName());
		List<AnnualVacation> list = annualVacationService.getListByCondition(vacation);
		Date startDate = null;
		if(list!=null&&list.size()>0){
			//????????????????????????????????????
			startDate = DateUtils.addDay(list.get(0).getAnnualDate(), -1);
			while(true){
				if(annualVacationService.judgeWorkOrNot(startDate)){
					break;
				}
				startDate = DateUtils.addDay(startDate, -1);
			}
		}else{
			throw new OaException("??????????????????????????????????????????");
		}
		Date lastSubDate = annualVacationService.getWorkingDayPre(4,startDate);
		if(DateUtils.getIntervalDays(new Date(),lastSubDate)<0){
			throw new OaException("?????????????????????4?????????????????????");
		}
		List<ApplicationEmployeeDuty> list1 = applicationEmployeeDutyMapper.getByCondition(duty);
		if(list1!=null&&list1.size()>0){
			throw new OaException("???????????????????????????");
		}
		int count = applicationEmployeeDutyMapper.save(duty);
		if(count<=0){
			throw new OaException("?????????????????????????????????");
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateEmployeeDuty(User user, Long dutyId,List<ApplicationEmployeeDutyDetail> list) throws OaException {
		ApplicationEmployeeDutyDetail param = new ApplicationEmployeeDutyDetail();
		param.setAttnApplicationEmployDutyId(dutyId);
		List<ApplicationEmployeeDutyDetail> orgrnial = applicationEmployeeDutyDetailMapper.selectByCondition(param);
		//???????????????????????????????????????
		List<ApplicationEmployeeDutyDetail> addList = new ArrayList<ApplicationEmployeeDutyDetail>();
		List<ApplicationEmployeeDutyDetail> updateList = new ArrayList<ApplicationEmployeeDutyDetail>();
		List<ApplicationEmployeeDutyDetail> deleteList = new ArrayList<ApplicationEmployeeDutyDetail>();
		List<Long> updateIds = new ArrayList<Long>();
		for(ApplicationEmployeeDutyDetail detail:list){
			if(detail.getId()==null){
				detail.setCreateTime(new Date());
				detail.setCreateUser(user.getEmployee().getCnName());
				detail.setDelFlag(0);
				detail.setAttnApplicationEmployDutyId(dutyId);
				addList.add(detail);
			}else{
				detail.setUpdateTime(new Date());
				detail.setUpdateUser(user.getEmployee().getCnName());
				updateList.add(detail);
				updateIds.add(detail.getId());
			}
		}
		//??????????????????id
		for(ApplicationEmployeeDutyDetail o:orgrnial){
			if(!updateIds.contains(o.getId())){
				ApplicationEmployeeDutyDetail delete = new ApplicationEmployeeDutyDetail();
				delete.setId(o.getId());
				deleteList.add(delete);
			}		
		}
		if(addList!=null&&addList.size()>0){
			applicationEmployeeDutyDetailMapper.batchSave(addList);
		}
		for(ApplicationEmployeeDutyDetail update:updateList){
			applicationEmployeeDutyDetailMapper.updateById(update);
		}
		for(ApplicationEmployeeDutyDetail delete:deleteList){
			delete.setUpdateTime(new Date());
			delete.setUpdateUser(user.getEmployee().getCnName());
			delete.setDelFlag(1);
			applicationEmployeeDutyDetailMapper.updateById(delete);
		}
		//??????employeeIds??????
		List<ApplicationEmployeeDutyDetail> now = applicationEmployeeDutyDetailMapper.selectByCondition(param);
		List<String> idsList = new ArrayList<String>();
		for(ApplicationEmployeeDutyDetail detail:now){
			for(String id:detail.getEmployeeIds().split(",")){
				idsList.add(id);
			}
		}
		for(int i=0;i<idsList.size()-1;i++){
			for(int j=idsList.size()-1;j>i;j--){
				 if(idsList.get(j).equals(idsList.get(i)))  {
					 idsList.remove(j);
			     } 
			}
		}
		String employeeIds = "";
		for(String emplpyeeId:idsList){
			employeeIds += emplpyeeId+",";
		}
		ApplicationEmployeeDuty old =  applicationEmployeeDutyMapper.getById(dutyId);
		old.setEmployeeIds(employeeIds.substring(0, employeeIds.length()-1));
		old.setDutyNum(idsList.size());
		applicationEmployeeDutyMapper.updateById(old);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> submitApprove(Long dutyId, User user)
			throws OaException {
		Map<String,Object> map = new HashMap<String, Object>();
		ApplicationEmployeeDuty applicationEmployeeDuty = new ApplicationEmployeeDuty();
		applicationEmployeeDuty.setId(dutyId);
		List<ApplicationEmployeeDuty> list = applicationEmployeeDutyMapper.getByCondition(applicationEmployeeDuty);
		if(list!=null&&list.size()>0){
			ApplicationEmployeeDutyDetail param = new ApplicationEmployeeDutyDetail();
			param.setAttnApplicationEmployDutyId(dutyId);
			List<ApplicationEmployeeDutyDetail> detailList = applicationEmployeeDutyDetailMapper.selectByCondition(param);
			if(detailList==null||detailList.size()<=0){
				throw new OaException("?????????????????????");
			}
			//XXX----------------------------????????????------------------------------
			String processInstanceId = activitiServiceImpl.startByKey(ConfigConstants.DUTY_KEY);
			
			//-----------------start-----------------------????????????????????????
			ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
			flow.setAssigneeName(user.getEmployee().getCnName());
			flow.setDepartName(user.getDepart().getName());
			flow.setPositionName(user.getPosition().getPositionName());
			flow.setFinishTime(new Date());
			flow.setComment("????????????");
			flow.setProcessId(processInstanceId);
			flow.setProcessKey(ConfigConstants.DUTY_KEY);
			flow.setStatu(0);
			viewTaskInfoService.save(flow);
			
			//???????????????????????????
			applicationEmployeeDuty.setProcessInstanceId(processInstanceId);
			applicationEmployeeDuty.setApprovalStatus(ConfigConstants.DOING_STATUS);
			applicationEmployeeDuty.setUpdateUser(user.getEmployee().getCnName());
			applicationEmployeeDuty.setUpdateTime(new Date());
			//??????????????????
			int count = applicationEmployeeDutyMapper.updateById(applicationEmployeeDuty);
			if(count<=0){
				throw new OaException("?????????????????????????????????");
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
		
		User user = userService.getCurrentUser();
		logger.info("????????????completeTask??????:processId="+processId+";comment="+comment+";commentType="+commentType
				+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));
		
		Task task = activitiServiceImpl.queryTaskByProcessInstanceId(processId);
		if(task==null){
			throw new OaException("??????Id???"+processId+"?????????????????????");
		}
		boolean type = StringUtils.startsWithIgnoreCase(task.getTaskDefinitionKey(), "personnel");
		ApplicationEmployeeDuty duty = applicationEmployeeDutyMapper.queryByProcessInstanceId(processId);
		if(duty!=null){
			ApplicationEmployeeDuty param = new ApplicationEmployeeDuty();
			param.setId(duty.getId());
			Integer approvalStatus ;
			if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.PASS)) {
				approvalStatus=type?ConfigConstants.PASS_STATUS:ConfigConstants.DOING_STATUS;
				
			}else if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.REFUSE)) {
				approvalStatus=ConfigConstants.REFUSE_STATUS;
				//?????????????????? ???????????????????????????  ???????????????????????????
				ApplicationEmployeeDuty dutyCopy=new ApplicationEmployeeDuty();
				dutyCopy=duty;
				dutyCopy.setProcessInstanceId(null);
				dutyCopy.setApprovalStatus(null);
				dutyCopy.setUpdateUser(user.getEmployee().getCnName());
				dutyCopy.setCreateUser(user.getEmployee().getCnName());
				dutyCopy.setUpdateTime(new Date());
				dutyCopy.setSubmitDate(null);
				applicationEmployeeDutyMapper.save(dutyCopy);
				//?????????????????? ???????????????  ???????????????????????????
				ApplicationEmployeeDutyDetail dutyDetailCopy=new ApplicationEmployeeDutyDetail();
				dutyDetailCopy.setAttnApplicationEmployDutyId(param.getId());
				List<ApplicationEmployeeDutyDetail> dutyDetailCopyList = applicationEmployeeDutyDetailMapper.selectByCondition(dutyDetailCopy);
				List<ApplicationEmployeeDutyDetail> dutyDetailCopyListlists= new ArrayList<ApplicationEmployeeDutyDetail>();
				for (ApplicationEmployeeDutyDetail applicationEmployeeDutyDetail : dutyDetailCopyList) {
					applicationEmployeeDutyDetail.setAttnApplicationEmployDutyId(dutyCopy.getId());
					dutyDetailCopyListlists.add(applicationEmployeeDutyDetail);
				}
				applicationEmployeeDutyDetailMapper.batchSave(dutyDetailCopyListlists);
			}else {
				approvalStatus=ConfigConstants.BACK_STATUS;
			}
			param.setApprovalStatus(approvalStatus);
			//????????????????????????????????????assignee,??????????????????????????????????????????
			if(StringUtils.isBlank(task.getAssignee())) {
				activitiServiceImpl.claimTask(task.getId(), String.valueOf(user.getEmployeeId()));
			}
			if(type){
				param.setVersion(0L);
				//??????????????????????????????????????????????????????????????????????????????????????????????????????0-???????????????1-????????????
				ApplicationEmployeeDuty otherDuty = new ApplicationEmployeeDuty();
				otherDuty.setDepartId(duty.getDepartId());
				otherDuty.setYear(duty.getYear());
				otherDuty.setVacationName(duty.getVacationName());
				otherDuty.setApprovalStatus(200);
				List<ApplicationEmployeeDuty> list = applicationEmployeeDutyMapper.getByCondition(otherDuty);
				for(ApplicationEmployeeDuty duty1:list){
					if(!(duty1.getId().longValue()==duty.getId().longValue())){
						duty1.setVersion(1L);
						applicationEmployeeDutyMapper.updateById(duty1);
					}
				}
			}
			applicationEmployeeDutyMapper.updateById(param);
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
			activitiServiceImpl.completeTask(task.getId(),"????????????",null,commentType);
		}else{
			throw new OaException("???????????????"+processId+"?????????????????????????????????");
		}
	}
	




	@Override
	public List<ApplicationEmployeeDuty> getByCondition(
			ApplicationEmployeeDuty duty) {
		return applicationEmployeeDutyMapper.getByCondition(duty);
	}

	@Override
	public ApplicationEmployeeDuty getById(Long id) {
		return applicationEmployeeDutyMapper.getById(id);
	} 

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteEmployeeDuty(Long id, User user) throws Exception {
		ApplicationEmployeeDuty duty = new ApplicationEmployeeDuty();
		duty.setId(id);
		duty.setUpdateTime(new Date());
		duty.setUpdateUser(user.getEmployee().getCnName());
		List<ApplicationEmployeeDuty> list = applicationEmployeeDutyMapper.getByCondition(duty);
		if(list!=null&&list.size()>0){
			if(list.get(0).getApprovalStatus()!=null){
				throw new OaException("???????????????????????????????????????");
			}else{
				if(applicationEmployeeDutyMapper.deleteById(list.get(0))>0){
					ApplicationEmployeeDutyDetail detail =new ApplicationEmployeeDutyDetail();
					detail.setAttnApplicationEmployDutyId(id);
					detail.setUpdateTime(new Date());
					detail.setUpdateUser(user.getEmployee().getCnName());
					applicationEmployeeDutyDetailMapper.deleteByDutyId(detail);
				}else{
					throw new OaException("?????????????????????????????????");
				}
			}
		}else{
			throw new OaException("??????????????????????????????");
		}
	}

	@Override
	public List<ApplicationEmployeeDutyDetail> selectByCondition(
			ApplicationEmployeeDutyDetail duty) {
		return applicationEmployeeDutyDetailMapper.selectByCondition(duty);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> moveEmployeeDuty(User user, Long dutyId,
			List<ApplicationEmployeeDutyDetail> list) throws OaException {
		Map<String,Object> map = new HashMap<String, Object>();
		List<String> idsList = new ArrayList<String>();
		for(ApplicationEmployeeDutyDetail detail:list){
			for(String id:detail.getEmployeeIds().split(",")){
				idsList.add(id);
			}
		}
		for(int i=0;i<idsList.size()-1;i++){
			for(int j=idsList.size()-1;j>i;j--){
				 if(idsList.get(j).equals(idsList.get(i)))  {
					 idsList.remove(j);
			     } 
			}
		}
		String employeeIds = "";
		for(String emplpyeeId:idsList){
			employeeIds += emplpyeeId+",";
		}
		//????????????
		ApplicationEmployeeDuty old = applicationEmployeeDutyMapper.getById(dutyId);
		
		//???????????????????????????
		AnnualVacation vacation = new AnnualVacation();
		List<Integer> typeList = new ArrayList<Integer>();
		typeList.add(AnnualVacation.YYPE_LEGAL);
		typeList.add(AnnualVacation.YYPE_VACATION);
		vacation.setTypeList(typeList);
		vacation.setStartDate(DateUtils.parse(old.getYear()+"-01-01", DateUtils.FORMAT_SHORT));
		vacation.setEndDate(DateUtils.parse(old.getYear()+"-12-31", DateUtils.FORMAT_SHORT));
		vacation.setSubject(old.getVacationName());
		List<AnnualVacation> list1 = annualVacationService.getListByCondition(vacation);
		Date startDate = null;
		if(list1!=null&&list1.size()>0){
			//????????????????????????????????????
			startDate = DateUtils.addDay(list1.get(0).getAnnualDate(), -1);
			while(true){
				if(annualVacationService.judgeWorkOrNot(startDate)){
					break;
				}
				startDate = DateUtils.addDay(startDate, -1);
			}
		}else{
			throw new OaException("??????????????????????????????????????????");
		}
		Date lastSubDate = annualVacationService.getWorkingDayPre(4,startDate);
		if(DateUtils.getIntervalDays(new Date(),lastSubDate)<0){
			throw new OaException("?????????????????????4?????????????????????");
		}
		
		//XXX----------------------------????????????------------------------------
		String processInstanceId = activitiServiceImpl.startByKey(ConfigConstants.DUTY_KEY);
		
		//-----------------start-----------------------????????????????????????
		ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
		flow.setAssigneeName(user.getEmployee().getCnName());
		flow.setDepartName(user.getDepart().getName());
		flow.setPositionName(user.getPosition().getPositionName());
		flow.setFinishTime(new Date());
		flow.setComment("????????????");
		flow.setProcessId(processInstanceId);
		flow.setProcessKey(ConfigConstants.DUTY_KEY);
		flow.setStatu(0);
		viewTaskInfoService.save(flow);
		
		//???????????????????????????,????????????????????????
		ApplicationEmployeeDuty now = new ApplicationEmployeeDuty();
		now.setDelFlag(0);
		now.setVersion(1L);
		now.setClassSettingPerson(user.getEmployee().getCnName());
		now.setCreateTime(new Date());
		now.setCreateUser(user.getEmployee().getCnName());
		now.setYear(old.getYear());
		now.setDepartId(old.getDepartId());
		now.setDepartName(old.getDepartName());
		now.setDutyNum(idsList.size());
		now.setEmployeeIds(employeeIds.substring(0, employeeIds.length()-1));
		now.setVacationName(old.getVacationName());
		now.setProcessInstanceId(processInstanceId);
		now.setApprovalStatus(ConfigConstants.DOING_STATUS);
		now.setUpdateUser(user.getEmployee().getCnName());
		now.setUpdateTime(new Date());
		
		int count = applicationEmployeeDutyMapper.save(now);
		if(count<=0){
			throw new OaException("?????????????????????????????????");
		}
		
		List<ApplicationEmployeeDutyDetail> addList = new ArrayList<ApplicationEmployeeDutyDetail>();
		for(ApplicationEmployeeDutyDetail detail:list){
				detail.setCreateTime(new Date());
				detail.setCreateUser(user.getEmployee().getCnName());
				detail.setDelFlag(0);
				detail.setAttnApplicationEmployDutyId(now.getId());
				addList.add(detail);
		}
		
		if(addList!=null&&addList.size()>0){
			applicationEmployeeDutyDetailMapper.batchSave(addList);
		}
		map.put("success", true);
		map.put("message", "?????????????????????????????????!");
		return map;
	}
	
	@Override
	public void exportEmpDutyReprotById(Long departId,String vacationName,String year,User user) throws Exception {
		
		//??????????????????
		EmployeeDuty param = new EmployeeDuty();
		param.setDepartId(departId);
		param.setYear(year);
		param.setVacationName(vacationName);
		List<EmployeeDuty> list = employeeDutyMapper.selectByCondition(param);
		//???????????????
		Employee currentEmployee = employeeService.getCurrentEmployee();
		String toMail = currentEmployee.getEmail();
		String departName = "";
		Depart depart = departMapper.getById(departId);
		if(depart!=null){
			departName = depart.getName();
		}
		String fileName = year+"-"+vacationName+"-"+departName+"-"+list.get(0).getClassSettingPerson();
		String subject = fileName;
		String sttachmentName = fileName;
		HSSFWorkbook workbook = new HSSFWorkbook();
		workbook = exportDuty(vacationName,year,departId);
		SendMailUtil.sendExcelMail(workbook,toMail,subject,sttachmentName);
		
	}

	@Override
	public List<Map<String,Object>> queryDutyByCondition(String vacationName,
			String year, Long departId) throws Exception {
		if(year==null||"".equals(year)){
			year = DateUtils.getYear(new Date());
		}
		//?????????????????????????????????????????????????????????
		EmployeeDuty param = new EmployeeDuty();
		param.setDepartId(departId);
		param.setYear(year);
		param.setVacationName(vacationName);
		List<EmployeeDuty> groupList = employeeDutyMapper.getGroupByCondition(param);
		
		Map<String,Map<String, Object>> result1= new HashMap<String,Map<String, Object>>();
		for(EmployeeDuty duty:groupList){
			Map<String, Object> result2 = new HashMap<String, Object>();
			String key = String.valueOf(duty.getDepartId())+"_"+duty.getYear()+"_"+duty.getVacationName();
			result1.put(key, result2);
		}
		
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		for (Entry<String, Map<String, Object>> entry : result1.entrySet()) {
			String key = entry.getKey();
			
			Map<String,Object>  result = new HashMap<String,Object>();
			result.put("applyDate", "");
			
			//?????????????????????
			ApplicationEmployeeDuty apply = new ApplicationEmployeeDuty();
			apply.setDepartId(Long.valueOf(key.split("_")[0]));
			apply.setYear(key.split("_")[1]);
			apply.setVacationName(key.split("_")[2]);
			List<ApplicationEmployeeDuty> applyList = applicationEmployeeDutyMapper.getByCondition(apply);
			if(applyList!=null&&applyList.size()>0){
				result.put("applyDate",DateUtils.format(applyList.get(0).getCreateTime(), DateUtils.FORMAT_SHORT));
			}
			
			//????????????
			Depart depart = departService.getById(Long.valueOf(key.split("_")[0]));
			result.put("title", "");
			if(depart!=null){
				result.put("title", key.split("_")[1]+depart.getName()+key.split("_")[2]+"????????????");
			}
			
			//??????????????????
			EmployeeDuty param1 = new EmployeeDuty();
			param1.setDepartId(Long.valueOf(key.split("_")[0]));
			param1.setYear(key.split("_")[1]);
			param1.setVacationName(key.split("_")[2]);
			List<EmployeeDuty> list = employeeDutyMapper.selectByCondition(param1);
			result.put("list", list);
			resultList.add(result);
		}
        return resultList;
	}

	@Override
	public HSSFWorkbook exportDuty(String vacationName, String year,
			Long departId) {
			if(year==null||"".equals(year)){
				year = DateUtils.getYear(new Date());
			}
		    EmployeeDuty param = new EmployeeDuty();
			param.setDepartId(departId);
			param.setYear(year);
			param.setVacationName(vacationName);
			List<EmployeeDuty> list = employeeDutyMapper.getDutyDetail(param);
			for(EmployeeDuty data:list){
				data.setWeekDay("??????"+DateUtils.getWeek(data.getDutyDate()));
			}
			
			//?????????????????????
			ApplicationEmployeeDuty apply = new ApplicationEmployeeDuty();
			apply.setDepartId(departId);
			apply.setYear(year);
			apply.setVacationName(vacationName);
			apply.setType(0);
			apply.setApprovalStatus(200);
			List<ApplicationEmployeeDuty> applyList = applicationEmployeeDutyMapper.getByCondition(apply);
			String classSettingPerson = "";
			if(applyList!=null&&applyList.size()>0){
				classSettingPerson = applyList.get(0).getClassSettingPerson();
			}
			
			Depart depart = departService.getById(departId);
			String departName = "";
			if(depart!=null){
				departName = depart.getName();
			}
			String fileName = year+"-"+vacationName+"-"+departName;
			if(!"".equals(classSettingPerson)){
				fileName = fileName+"-"+classSettingPerson;
			}
			String excelName = fileName;
			HSSFWorkbook workbook = new HSSFWorkbook();
			
			HSSFSheet sheet = workbook.createSheet(excelName);
			if (list != null && list.size() > 0) {
				// ??????????????????
				HSSFCellStyle colstyle = workbook.createCellStyle();
				colstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// ????????????
				colstyle.setBorderBottom(XSSFCellStyle.BORDER_THIN); //?????????
				colstyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);//?????????
				colstyle.setBorderTop(XSSFCellStyle.BORDER_THIN);//?????????
				colstyle.setBorderRight(XSSFCellStyle.BORDER_THIN);//?????????
				
				//?????????
				HSSFRow row = sheet.createRow((short) 0);
				//???????????????
				HSSFCellStyle colstyle1 = workbook.createCellStyle();
				colstyle1.setAlignment(HSSFCellStyle.ALIGN_CENTER);// ????????????
				ExcelUtil.createRow(row, 0, colstyle1, HSSFCell.CELL_TYPE_STRING,year+vacationName+"????????????");
				sheet.addMergedRegion(new CellRangeAddress(0,0,0,8));
				sheet.setColumnWidth(0, 5000);//??????????????????
				//???3???
				row = sheet.createRow((short) 2);
				ExcelUtil.createRow(row, 0, null, HSSFCell.CELL_TYPE_STRING,"?????????");
				ExcelUtil.createRow(row, 1, null, HSSFCell.CELL_TYPE_STRING,userService.getCurrentUser().getCompany().getName());
				ExcelUtil.createRow(row, 5, null, HSSFCell.CELL_TYPE_STRING,"?????????");
				ExcelUtil.createRow(row, 6, null, HSSFCell.CELL_TYPE_STRING,departName);
				sheet.addMergedRegion(new CellRangeAddress(2,2,1,4));
				sheet.addMergedRegion(new CellRangeAddress(2,2,5,8));
				sheet.setColumnWidth(2, 5000);//??????????????????
				
				//???5???
				row = sheet.createRow((short) 4);
				ExcelUtil.createRow(row, 0, colstyle, HSSFCell.CELL_TYPE_STRING,"??????");
				ExcelUtil.createRow(row, 1, colstyle, HSSFCell.CELL_TYPE_STRING,"??????");
				ExcelUtil.createRow(row, 2, colstyle, HSSFCell.CELL_TYPE_STRING,"????????????");
				ExcelUtil.createRow(row, 3, colstyle, HSSFCell.CELL_TYPE_STRING,"????????????");
				ExcelUtil.createRow(row, 4, colstyle, HSSFCell.CELL_TYPE_STRING,"????????????");
				ExcelUtil.createRow(row, 5, colstyle, HSSFCell.CELL_TYPE_STRING,"");
				sheet.addMergedRegion(new CellRangeAddress(4,4,4,5));
				ExcelUtil.createRow(row, 6, colstyle, HSSFCell.CELL_TYPE_STRING,"???????????????");
				ExcelUtil.createRow(row, 7, colstyle, HSSFCell.CELL_TYPE_STRING,"??????????????????");
				ExcelUtil.createRow(row, 8, colstyle, HSSFCell.CELL_TYPE_STRING,"??????");
				sheet.setColumnWidth(4, 5000);//??????????????????
				
				//????????????
				for(int i=0;i<list.size();i++){
					row = sheet.createRow((short) (5+i));
					ExcelUtil.createRow(row, 0, colstyle, HSSFCell.CELL_TYPE_STRING,DateUtils.format(list.get(i).getDutyDate(), DateUtils.FORMAT_SHORT));
					ExcelUtil.createRow(row, 1, colstyle, HSSFCell.CELL_TYPE_STRING,list.get(i).getWeekDay());
					ExcelUtil.createRow(row, 2, colstyle, HSSFCell.CELL_TYPE_STRING,list.get(i).getEmployCode());
					ExcelUtil.createRow(row, 3, colstyle, HSSFCell.CELL_TYPE_STRING,list.get(i).getEmployName());
					ExcelUtil.createRow(row, 4, colstyle, HSSFCell.CELL_TYPE_STRING,"From???"+DateUtils.format(list.get(i).getStartTime(),"HH:mm"));
					ExcelUtil.createRow(row, 5, colstyle, HSSFCell.CELL_TYPE_STRING,"To???"+DateUtils.format(list.get(i).getEndTime(),"HH:mm"));
					ExcelUtil.createRow(row, 6, colstyle, HSSFCell.CELL_TYPE_STRING,list.get(i).getWorkHours());
					ExcelUtil.createRow(row, 7, colstyle, HSSFCell.CELL_TYPE_STRING,list.get(i).getDutyItem());
					ExcelUtil.createRow(row, 8, colstyle, HSSFCell.CELL_TYPE_STRING,list.get(i).getRemark());
					sheet.setColumnWidth(5+i, 5000);//??????????????????
				}
				
				row = sheet.createRow((short) (list.size()+6));
				ExcelUtil.createRow(row, 0, null, HSSFCell.CELL_TYPE_STRING,"????????????");
				ExcelUtil.createRow(row, 4, null, HSSFCell.CELL_TYPE_STRING,"????????????????????????");
				ExcelUtil.createRow(row, 5, null, HSSFCell.CELL_TYPE_STRING,classSettingPerson);
				sheet.setColumnWidth(list.size()+6, 5000);//??????????????????
				List<HiActinst> hiActinstList = new ArrayList<HiActinst>();
				if(applyList!=null&&applyList.size()>0){
					RunTask task = new RunTask();
					task.setEntityId(String.valueOf(applyList.get(0).getId()));
					task.setReProcdefCode(RunTask.RUN_CODE_110);
					RunTask runTask = runTaskService.getRunTask(task);
					
					if(runTask!=null){
						HiActinst hiActinst = new HiActinst();
						hiActinst.setRuTaskId(runTask.getId());
						hiActinst.setNodeCode("RESIGN_HR");
						hiActinstList = hiActinstMapper.getList(hiActinst);
					}
				}
				
				row = sheet.createRow((short) (list.size()+10));
				ExcelUtil.createRow(row, 0, null, HSSFCell.CELL_TYPE_STRING,"????????????????????????");
				ExcelUtil.createRow(row, 1, null, HSSFCell.CELL_TYPE_STRING,(hiActinstList!=null&&hiActinstList.size()>0)?hiActinstList.get(0).getUpdateUser():"");
				sheet.setColumnWidth(list.size()+10, 5000);//??????????????????
			} else {
				ExcelUtil.createRow(sheet.createRow(1), 0, null,HSSFCell.CELL_TYPE_STRING, "?????????");
			}
			return workbook;
	}

	@Override
	public List<EmployeeDuty> querySingleDutyByCondition(String vacationName,
			String year, Long departId) throws Exception {
		EmployeeDuty param = new EmployeeDuty();
		if(year==null||"".equals(year)){
			year = DateUtils.getYear(new Date());
		}
		param.setYear(year);
		param.setDepartId(departId);
		param.setVacationName(vacationName);
		List<EmployeeDuty> list = employeeDutyMapper.getGroupByCondition(param);
		
		for(EmployeeDuty duty:list){
			List<EmployeeDuty> employList = employeeDutyMapper.getEmployDutyCountByCondition(duty);
			duty.setEmployDutyCount(0);
			if(employList!=null&&employList.size()>0){
				duty.setEmployDutyCount(employList.size());
			}
			Depart depart  = departMapper.getById(duty.getDepartId());
			if(depart!=null){
				duty.setDepartName(depart.getName());
			}
		}
		return list;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void endHandle(String processInstanceId, User user) {
		ApplicationEmployeeDuty old = applicationEmployeeDutyMapper.queryByProcessInstanceId(processInstanceId);
		//???????????????????????????????????????
		EmployeeDuty delete = new EmployeeDuty();
		delete.setYear(old.getYear());
		delete.setDepartId(old.getDepartId());
		delete.setVacationName(old.getVacationName());
		delete.setSource(0);
		employeeDutyMapper.deleteByCondition(delete);
		//?????????????????????????????????????????????
		ApplicationEmployeeDutyDetail param = new ApplicationEmployeeDutyDetail();
		param.setAttnApplicationEmployDutyId(old.getId());
		List<ApplicationEmployeeDutyDetail> orgrnial = applicationEmployeeDutyDetailMapper.selectByCondition(param);
		for(ApplicationEmployeeDutyDetail detail:orgrnial){
			for(String employeeId:detail.getEmployeeIds().split(",")){
				//??????????????????????????????
				EmployeeDuty employeeDuty = new EmployeeDuty();
				employeeDuty.setDutyDate(detail.getVacationDate());
				employeeDuty.setEmployId(Long.valueOf(employeeId));
				List<EmployeeDuty> dutyList = employeeDutyMapper.selectByCondition(employeeDuty);
				if(dutyList!=null&&dutyList.size()>0){
					EmployeeDuty update = new EmployeeDuty();
					update.setDutyDate(detail.getVacationDate());
					update.setEmployId(Long.valueOf(employeeId));
					update.setDutyItem(detail.getDutyItem());
					update.setStartTime(detail.getStartTime());
					update.setEndTime(detail.getEndTime());
					update.setWorkHours(detail.getWorkHours());
					update.setRemark(detail.getRemarks());
					update.setSource(0);
					update.setUpdateTime(new Date());
					update.setUpdateUser(user.getEmployee().getCnName());
					employeeDutyMapper.update(update);
				}else{
					Employee employee = employeeMapper.getById(Long.valueOf(employeeId));
					EmployeeDuty add = new EmployeeDuty();
					add.setCompanyId(user.getCompanyId());
					add.setDepartId(old.getDepartId());
					add.setEmployId(Long.valueOf(employeeId));
					add.setEmployName(employee.getCnName());
					add.setYear(old.getYear());
					add.setVacationName(old.getVacationName());
					add.setClassSettingPerson(old.getClassSettingPerson());
					add.setDelFlag(0);
					add.setDutyDate(detail.getVacationDate());
					add.setDutyItem(detail.getDutyItem());
					add.setRemark(detail.getRemarks());
					add.setStartTime(detail.getStartTime());
					add.setEndTime(detail.getEndTime());
					add.setWorkHours(detail.getWorkHours());
					add.setSource(0);
					add.setCreateTime(new Date());
					add.setCreateUser(user.getEmployee().getCnName());
					employeeDutyMapper.save(add);
				}
			}
		}
		Map<Long,String> dateMap = new HashMap<Long,String>();
		//????????????
		EmployeeDuty dutyP = new EmployeeDuty();
		dutyP.setYear(old.getYear());
		dutyP.setDepartId(old.getDepartId());
		dutyP.setVacationName(old.getVacationName());
		dutyP.setSource(0);
		List<EmployeeDuty> dutyList = employeeDutyMapper.selectByCondition(dutyP);
		for(EmployeeDuty duty:dutyList){
			if(dateMap!=null&&dateMap.containsKey(duty.getEmployId())){
				String date = dateMap.get(duty.getEmployId());
				date = date+"???"+ DateUtils.format(duty.getDutyDate(), DateUtils.FORMAT_MM_DD);
				dateMap.put(duty.getEmployId(), date);
			}else{
				dateMap.put(duty.getEmployId(), DateUtils.format(duty.getDutyDate(), DateUtils.FORMAT_MM_DD));
			}
		}
		for (Map.Entry<Long,String> entry : dateMap.entrySet()) {
			Long employeeId = entry.getKey();
			Employee employee = employeeMapper.getById(employeeId);
			if(employee!=null){
				sendMsg(employee,user,dateMap);
				sendMail(employee,dateMap);
			}
		}
	}
	
	public void sendMsg(Employee employee,User user,Map<Long,String> dateMap){
		EmpMsg dhMsg = new EmpMsg();
		dhMsg.setDelFlag(CommonPo.STATUS_NORMAL);
		dhMsg.setType(EmpMsg.type_200);
		dhMsg.setCompanyId(user.getCompanyId());
		dhMsg.setEmployeeId(employee.getId());
		dhMsg.setTitle("????????????");
		dhMsg.setContent(employee.getCnName()+"??????????????????"+dateMap.get(employee.getId())+"?????????????????????????????????????????????????????????");
		dhMsg.setReadFlag(EmpMsg.READ_FLAG_NO);
		dhMsg.setCreateTime(new Date());
		empMsgMapper.save(dhMsg);
	}
	
	public void sendMail(Employee employee,Map<Long,String> dateMap){
		StringBuilder sbuilder = new StringBuilder();
        sbuilder.append(employee.getCnName()+"??????????????????"+dateMap.get(employee.getId())+"?????????????????????????????????????????????????????????");
        List<SendMail> sendMailList = new ArrayList<SendMail>();
		SendMail sendMail = new SendMail();
		sendMail.setReceiver(employee.getEmail());
		sendMail.setSubject("????????????");
		sendMail.setSendStatus(SendMail.SEND_STATUS_NO);
		sendMail.setText(sbuilder.toString());
		sendMail.setOaMail(SendMail.OA_MAIL_P);
		sendMailList.add(sendMail);
		sendMailService.batchSave(sendMailList);	
	}

	@Override
	public ApplicationEmployeeDuty queryByProcessInstanceId(
			String processInstanceId) {
		return applicationEmployeeDutyMapper.queryByProcessInstanceId(processInstanceId);
	}

	@Override
	public void setValueToVO(TaskVO taskVO, String processInstanceId) {
		ApplicationEmployeeDuty duty = applicationEmployeeDutyMapper.queryByProcessInstanceId(processInstanceId);
		if(duty!=null){
			taskVO.setProcessName("????????????");
			taskVO.setCreatorDepart(duty.getDepartName());
			taskVO.setCreator(duty.getClassSettingPerson());
			taskVO.setCreateTime(duty.getCreateTime());
			taskVO.setReProcdefCode("110");
			taskVO.setProcessId(duty.getProcessInstanceId());
			taskVO.setResourceId(String.valueOf(duty.getId()));
			taskVO.setRedirectUrl("/employeeClass/approveVacation.htm?flag=no&dutyId="+duty.getId());
			if(!(taskVO.getProcessStatu()==null)) {
				taskVO.setRedirectUrl("/employeeClass/approveVacation.htm?flag=can&dutyId="+duty.getId());
			}
			taskVO.setProcessStatu(duty.getApprovalStatus());
			if(duty.getApprovalStatus()!=null){
				taskVO.setOperation(ConfigConstants.STATUS.getOperationByStatu(duty.getApprovalStatus()));
			}
		}
	}

	@Override
	public void updateDuty(ApplicationEmployeeDuty duty) throws OaException {
		applicationEmployeeDutyMapper.updateById(duty);
	}

	@Override
	public PageModel<ApplicationEmployeeDuty> getHandlingListByPage(
			ApplicationEmployeeDuty employeeDuty) {
		int page = employeeDuty.getPage() == null ? 0 : employeeDuty.getPage();
		int rows = employeeDuty.getRows() == null ? 0 : employeeDuty.getRows();
		
		PageModel<ApplicationEmployeeDuty> pm = new PageModel<ApplicationEmployeeDuty>();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		Integer total = applicationEmployeeDutyMapper.getByConditionCount(employeeDuty);
		pm.setTotal(total);
		
		employeeDuty.setOffset(pm.getOffset());
		employeeDuty.setLimit(pm.getLimit());
		
		List<ApplicationEmployeeDuty> list = applicationEmployeeDutyMapper.getByCondition(employeeDuty);
        
		for(ApplicationEmployeeDuty og:list){
			try{
				ViewTaskInfoTbl taskInfo = null;
				taskInfo = viewTaskInfoService.getFirstAuditUser(og.getProcessInstanceId(),ConfigConstants.DUTY_KEY,false);
				if(null != taskInfo){
					og.setHrAuditor(taskInfo.getAssigneeName());	//????????????
				}
			}catch(Exception e){
				og.setHrAuditor(" ");
			}
		}
		
		pm.setRows(list);
		return pm;
	}

	@SuppressWarnings("null")
	@Override
	public Map<String, Object> getDetailById(Long id) {
		// TODO Auto-generated method stub

		ApplicationEmployeeDuty employeeDuty = applicationEmployeeDutyMapper.getById(id);//?????????
		
		
		
		Map<String,Object> result = new HashMap<String,Object>();//????????????
		//??????
		List<String> weekDays = new ArrayList<String>();
		weekDays.add("??????");
		weekDays.add("??????");
		weekDays.add("????????????");
		weekDays.add("????????????");
		weekDays.add("????????????");
		weekDays.add("???????????????");
		weekDays.add("??????????????????");
		weekDays.add("??????");
		
		//??????
		result.put("title", employeeDuty.getYear()+"???"+employeeDuty.getVacationName()+"????????????");
		//????????????
		result.put("applyDate", DateUtils.format(employeeDuty.getCreateTime(), DateUtils.FORMAT_SHORT));
		//????????????
		if(employeeDuty!=null&&
				employeeDuty.getApprovalStatus().intValue()==ConfigConstants.DOING_STATUS.intValue()){
			result.put("approvalStatus", "?????????");
		}else if(employeeDuty!=null&&
				employeeDuty.getApprovalStatus().intValue()==ConfigConstants.PASS_STATUS.intValue()){
			result.put("approvalStatus", "?????????");
		}else if(employeeDuty!=null&&
				employeeDuty.getApprovalStatus().intValue()==ConfigConstants.REFUSE_STATUS.intValue()){
			result.put("approvalStatus", "?????????");
		}else if(employeeDuty!=null&&
				employeeDuty.getApprovalStatus().intValue()==ConfigConstants.BACK_STATUS.intValue()){
			result.put("approvalStatus", "?????????");
		}else if(employeeDuty!=null&&
				employeeDuty.getApprovalStatus().intValue()==ConfigConstants.OVERDUE_STATUS.intValue()){
			result.put("approvalStatus", "?????????");
		}else if(employeeDuty!=null&&
				employeeDuty.getApprovalStatus().intValue()==ConfigConstants.OVERDUEPASS_STATUS.intValue()){
			result.put("approvalStatus", "????????????");
		}else if(employeeDuty!=null&&
				employeeDuty.getApprovalStatus().intValue()==ConfigConstants.OVERDUEREFUSE_STATUS.intValue()){
			result.put("approvalStatus", "????????????");
		}
		//??????????????????
		Company companyName = companyMapper.getById(employeeDuty.getCompanyId());
		if(companyName!=null){
			result.put("companyName", companyName.getName());
		}
		ApplicationEmployeeDutyDetail dutyDetail=new ApplicationEmployeeDutyDetail();
		dutyDetail.setAttnApplicationEmployDutyId(employeeDuty.getId());
		//????????????id ??????????????????
		List<ApplicationEmployeeDutyDetail> selectByCondition = applicationEmployeeDutyDetailMapper.selectByCondition(dutyDetail);
		List<ApplicationEmployeeDutyDetail> dutyMoveList=new ArrayList<ApplicationEmployeeDutyDetail>();
		List<ApplicationEmployeeDutyDetail>  dutyList= new ArrayList<ApplicationEmployeeDutyDetail>();
		for (ApplicationEmployeeDutyDetail applicationEmployeeDutyDetail : selectByCondition) {
			applicationEmployeeDutyDetail.setWeekStr(DateUtils.getWeek(applicationEmployeeDutyDetail.getVacationDate()));
			if(applicationEmployeeDutyDetail.getIsMove()==0){
				dutyList.add(applicationEmployeeDutyDetail);//????????????
			}else{
				dutyMoveList.add(applicationEmployeeDutyDetail);//???????????????????????????
			}
		}
		result.put("classDetail", dutyList);
		result.put("dutyMoveList", dutyMoveList);
		result.put("weekDays",weekDays);
		return result;
	}

	@Override
	public HSSFWorkbook exportDutyDetailById(Long id) {
		// TODO Auto-generated method stub
		if(id==null){
			return null;
		}
		ApplicationEmployeeDutyDetail dutyDetail=new ApplicationEmployeeDutyDetail();
		dutyDetail.setAttnApplicationEmployDutyId(id);
		//????????????id ??????????????????
		List<ApplicationEmployeeDutyDetail> list = applicationEmployeeDutyDetailMapper.selectByCondition(dutyDetail);
		
		
		//?????????????????????
		ApplicationEmployeeDuty apply = new ApplicationEmployeeDuty();
		apply.setId(id);
		List<ApplicationEmployeeDuty> applyList = applicationEmployeeDutyMapper.getByCondition(apply);
		String classSettingPerson = "";
		if(applyList!=null&&applyList.size()>0){
			classSettingPerson = applyList.get(0).getClassSettingPerson();
		}
		if(list!=null && list.size()>0){
			String fileName = list.get(0).getYear()+"-"+list.get(0).getVacationName()+"-"+list.get(0).getDepartName();
			if(!"".equals(classSettingPerson)){
				fileName = fileName+"-"+classSettingPerson;
			}
			String excelName = fileName;
			HSSFWorkbook workbook = new HSSFWorkbook();
			
			HSSFSheet sheet = workbook.createSheet(excelName);
			if (list != null && list.size() > 0) {
				// ??????????????????
				HSSFCellStyle colstyle = workbook.createCellStyle();
				colstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// ????????????
				colstyle.setBorderBottom(XSSFCellStyle.BORDER_THIN); //?????????
				colstyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);//?????????
				colstyle.setBorderTop(XSSFCellStyle.BORDER_THIN);//?????????
				colstyle.setBorderRight(XSSFCellStyle.BORDER_THIN);//?????????
				
				//?????????
				HSSFRow row = sheet.createRow((short) 0);
				//???????????????
				HSSFCellStyle colstyle1 = workbook.createCellStyle();
				colstyle1.setAlignment(HSSFCellStyle.ALIGN_CENTER);// ????????????
				ExcelUtil.createRow(row, 0, colstyle1, HSSFCell.CELL_TYPE_STRING,list.get(0).getYear()+list.get(0).getVacationName()+"????????????");
				sheet.addMergedRegion(new CellRangeAddress(0,0,0,6));
				sheet.setColumnWidth(0, 5000);//??????????????????
				//???3???
				row = sheet.createRow((short) 2);
				ExcelUtil.createRow(row, 0, null, HSSFCell.CELL_TYPE_STRING,"?????????");
				Company companyName = companyMapper.getById(applyList.get(0).getCompanyId());
				ExcelUtil.createRow(row, 1, null, HSSFCell.CELL_TYPE_STRING,companyName.getName());
				ExcelUtil.createRow(row, 4, null, HSSFCell.CELL_TYPE_STRING,"?????????");
				ExcelUtil.createRow(row, 5, null, HSSFCell.CELL_TYPE_STRING,list.get(0).getDepartName());
				sheet.addMergedRegion(new CellRangeAddress(2,2,1,3));
				sheet.addMergedRegion(new CellRangeAddress(2,2,5,6));
				sheet.setColumnWidth(2, 5000);//??????????????????

				//???5???
				row = sheet.createRow((short) 4);
				ExcelUtil.createRow(row, 0, colstyle, HSSFCell.CELL_TYPE_STRING,"??????");
				ExcelUtil.createRow(row, 1, colstyle, HSSFCell.CELL_TYPE_STRING,"??????");
				ExcelUtil.createRow(row, 2, colstyle, HSSFCell.CELL_TYPE_STRING,"????????????");
				ExcelUtil.createRow(row, 3, colstyle, HSSFCell.CELL_TYPE_STRING,"????????????");
				ExcelUtil.createRow(row, 4, colstyle, HSSFCell.CELL_TYPE_STRING,"????????????");
				ExcelUtil.createRow(row, 5, colstyle, HSSFCell.CELL_TYPE_STRING,"");
				sheet.addMergedRegion(new CellRangeAddress(4,4,4,5));
				ExcelUtil.createRow(row, 6, colstyle, HSSFCell.CELL_TYPE_STRING,"???????????????");
				ExcelUtil.createRow(row, 7, colstyle, HSSFCell.CELL_TYPE_STRING,"??????????????????");
				ExcelUtil.createRow(row, 8, colstyle, HSSFCell.CELL_TYPE_STRING,"??????");
				sheet.setColumnWidth(4, 5000);//??????????????????
				
				//????????????
				for(int i=0;i<list.size();i++){
					row = sheet.createRow((short) (5+i));
					if(list.get(i).getIsMove()==0){//????????? ?????????????????????
						//?????????
						ExcelUtil.createRow(row, 0, colstyle, HSSFCell.CELL_TYPE_STRING,DateUtils.format(list.get(i).getVacationDate(), DateUtils.FORMAT_SHORT));
						ExcelUtil.createRow(row, 1, colstyle, HSSFCell.CELL_TYPE_STRING,"??????"+DateUtils.getWeek(list.get(i).getVacationDate()));						
						ExcelUtil.createRow(row, 2, colstyle, HSSFCell.CELL_TYPE_STRING,list.get(i).getCodes());
						ExcelUtil.createRow(row, 3, colstyle, HSSFCell.CELL_TYPE_STRING,list.get(i).getNames());
						ExcelUtil.createRow(row, 4, colstyle, HSSFCell.CELL_TYPE_STRING,"From???"+DateUtils.format(list.get(i).getStartTime(),"HH:mm"));
						ExcelUtil.createRow(row, 5, colstyle, HSSFCell.CELL_TYPE_STRING,"To???"+DateUtils.format(list.get(i).getEndTime(),"HH:mm"));
						ExcelUtil.createRow(row, 6, colstyle, HSSFCell.CELL_TYPE_STRING,list.get(i).getWorkHours());
						ExcelUtil.createRow(row, 7, colstyle, HSSFCell.CELL_TYPE_STRING,list.get(i).getDutyItem());
						ExcelUtil.createRow(row, 8, colstyle, HSSFCell.CELL_TYPE_STRING,list.get(i).getRemarks());
					}
				
					sheet.setColumnWidth(5+i, 5000);//??????????????????
				}
				
				row = sheet.createRow((short) (list.size()+6));
				List<HiActinst> hiActinstList = new ArrayList<HiActinst>();
				if(applyList!=null&&applyList.size()>0){
					RunTask task = new RunTask();
					task.setEntityId(String.valueOf(applyList.get(0).getId()));
					task.setReProcdefCode(RunTask.RUN_CODE_110);
					RunTask runTask = runTaskService.getRunTask(task);
					
					if(runTask!=null){
						HiActinst hiActinst = new HiActinst();
						hiActinst.setRuTaskId(runTask.getId());
						hiActinst.setNodeCode("RESIGN_HR");
						hiActinstList = hiActinstMapper.getList(hiActinst);
					}
				}
				
				
				ExcelUtil.createRow(row, 0, null, HSSFCell.CELL_TYPE_STRING,"????????????????????????");
				
				
				try{
					ViewTaskInfoTbl taskInfo = null;
					taskInfo = viewTaskInfoService.getFirstAuditUser(applyList.get(0).getProcessInstanceId(),ConfigConstants.DUTY_KEY,false);
					if(null != taskInfo){
						 applyList.get(0).setHrAuditor(taskInfo.getAssigneeName());	//????????????
					}
				}catch(Exception e){
					 applyList.get(0).setHrAuditor(" ");
				}
				
				ExcelUtil.createRow(row, 1, null, HSSFCell.CELL_TYPE_STRING,(applyList.get(0).getHrAuditor()));
				sheet.setColumnWidth(list.size()+6, 5000);//??????????????????
				
			} else {
				ExcelUtil.createRow(sheet.createRow(1), 0, null,HSSFCell.CELL_TYPE_STRING, "?????????");
			}
			return workbook;
	}
		return null;
	}
	
	
	/**
	 * ??????????????????
	 */
	@Override
	public HSSFWorkbook exportScheduleTemplate(Long departId, String year, String vacation) {
		//???????????????
		User user = userService.getCurrentUser();
		Depart depart = departService.getById(departId);
		//???????????????
		HSSFWorkbook workbook = new HSSFWorkbook();
		//????????????
		HSSFCellStyle colstyle = workbook.createCellStyle();
		colstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// ????????????
		colstyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// ??????   
		colstyle.setBorderBottom(XSSFCellStyle.BORDER_THIN); //?????????
		colstyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);//?????????
		colstyle.setBorderTop(XSSFCellStyle.BORDER_THIN);//?????????
		colstyle.setBorderRight(XSSFCellStyle.BORDER_THIN);//?????????
		//sheet1
		HSSFSheet sheet1 = workbook.createSheet("????????????");
        //?????????????????????
        //?????????????????????????????????????????????
		List<String> dateList = new ArrayList<String>();
        List<Map<String,Object>> annualVacationList = annualVacationService.getAnnualDateByYearAndSubject(year,vacation);
        for (Map<String, Object> map : annualVacationList) {
        	String dateStr = DateUtils.format((Date)map.get("day"),DateUtils.FORMAT_SHORT);
        	dateList.add(dateStr);
		}
        String[] dateArray = dateList.toArray(new String[dateList.size()]);
        //???????????????????????????4???????????????500??????????????????????????????????????????
        ExcelUtil.creatComoboxRow(sheet1, 4, 500, 0, 0, dateArray);
        //?????????????????????
        String[] timeArray = {"00:00:00","01:00:00","02:00:00","03:00:00","04:00:00","05:00:00","06:00:00",
        					  "07:00:00","08:00:00","09:00:00","10:00:00","11:00:00","12:00:00","13:00:00",
        					  "14:00:00","15:00:00","16:00:00","17:00:00","18:00:00","19:00:00","20:00:00",
        					  "21:00:00","22:00:00","23:00:00","24:00:00"};
        ExcelUtil.creatComoboxRow(sheet1, 4, 500, 3, 3, timeArray);
        ExcelUtil.creatComoboxRow(sheet1, 4, 500, 4, 4, timeArray);
		//??????????????????
		sheet1.setColumnWidth(0, 5000);
		sheet1.setColumnWidth(1, 5000);
		sheet1.setColumnWidth(2, 5000);
		sheet1.setColumnWidth(3, 2500);
		sheet1.setColumnWidth(4, 2500);
		sheet1.setColumnWidth(5, 10000);
		sheet1.setColumnWidth(6, 6000);
		//?????????????????????
		HSSFRow row = sheet1.createRow((short) 0);
		String title1 = year + "???"+ vacation +"???????????? ";
		ExcelUtil.createRow(row, 0, colstyle, HSSFCell.CELL_TYPE_STRING, title1);
		//?????????????????????
		HSSFRow row2 = sheet1.createRow((short) 1);
		String companyName = "??????:" + user.getCompany().getName();
		String departName = "??????:" + depart.getName();
		ExcelUtil.createRow(row2, 0, colstyle, HSSFCell.CELL_TYPE_STRING, companyName);
		ExcelUtil.createRow(row2, 3, colstyle, HSSFCell.CELL_TYPE_STRING, departName);
		//?????????????????????
		HSSFRow row3 = sheet1.createRow((short) 2);
		ExcelUtil.createRow(row3, 0, colstyle, HSSFCell.CELL_TYPE_STRING, "??????");
		ExcelUtil.createRow(row3, 1, colstyle, HSSFCell.CELL_TYPE_STRING, "????????????");
		ExcelUtil.createRow(row3, 2, colstyle, HSSFCell.CELL_TYPE_STRING, "????????????");
		ExcelUtil.createRow(row3, 3, colstyle, HSSFCell.CELL_TYPE_STRING, "????????????");
		ExcelUtil.createRow(row3, 5, colstyle, HSSFCell.CELL_TYPE_STRING, "??????????????????");
		ExcelUtil.createRow(row3, 6, colstyle, HSSFCell.CELL_TYPE_STRING, "??????");
		//?????????????????????
		HSSFRow row4 = sheet1.createRow((short) 3);
		ExcelUtil.createRow(row4, 3, colstyle, HSSFCell.CELL_TYPE_STRING, "From");
		ExcelUtil.createRow(row4, 4, colstyle, HSSFCell.CELL_TYPE_STRING, "To");
		//???????????????
		CellRangeAddress cra1 = new CellRangeAddress(0,0,0,6);
		CellRangeAddress cra2 = new CellRangeAddress(1,1,0,2);
		CellRangeAddress cra3 = new CellRangeAddress(1,1,3,6);
		CellRangeAddress cra4 = new CellRangeAddress(2,2,3,4);
		CellRangeAddress cra5 = new CellRangeAddress(2,3,0,0);
		CellRangeAddress cra6 = new CellRangeAddress(2,3,1,1);
		CellRangeAddress cra7 = new CellRangeAddress(2,3,2,2);
		CellRangeAddress cra8 = new CellRangeAddress(2,3,5,5);
		CellRangeAddress cra9 = new CellRangeAddress(2,3,6,6);
		sheet1.addMergedRegion(cra1);
		sheet1.addMergedRegion(cra2);
		sheet1.addMergedRegion(cra3);
		sheet1.addMergedRegion(cra4);
		sheet1.addMergedRegion(cra5);
		sheet1.addMergedRegion(cra6);
		sheet1.addMergedRegion(cra7);
		sheet1.addMergedRegion(cra8);
		sheet1.addMergedRegion(cra9);
		//?????????????????????????????????
		// ?????????
		RegionUtil.setBorderBottom(1, cra1, sheet1,workbook);
		RegionUtil.setBorderBottom(1, cra2, sheet1,workbook);
		RegionUtil.setBorderBottom(1, cra3, sheet1,workbook);
		RegionUtil.setBorderBottom(1, cra4, sheet1,workbook);
		RegionUtil.setBorderBottom(1, cra5, sheet1,workbook);
		RegionUtil.setBorderBottom(1, cra6, sheet1,workbook);
		RegionUtil.setBorderBottom(1, cra7, sheet1,workbook);
		RegionUtil.setBorderBottom(1, cra8, sheet1,workbook);
		RegionUtil.setBorderBottom(1, cra9, sheet1,workbook);
		// ?????????
		RegionUtil.setBorderRight(1, cra1, sheet1,workbook);
		RegionUtil.setBorderRight(1, cra2, sheet1,workbook);
		RegionUtil.setBorderRight(1, cra3, sheet1,workbook);
		RegionUtil.setBorderRight(1, cra4, sheet1,workbook);
		RegionUtil.setBorderRight(1, cra4, sheet1,workbook);
		RegionUtil.setBorderRight(1, cra5, sheet1,workbook);
		RegionUtil.setBorderRight(1, cra6, sheet1,workbook);
		RegionUtil.setBorderRight(1, cra7, sheet1,workbook);
		RegionUtil.setBorderRight(1, cra8, sheet1,workbook);
		RegionUtil.setBorderRight(1, cra9, sheet1,workbook);
		return workbook;
	}

	@Override
	public Map<String, Object> getUnCommitDuty(Long departId, String year, String vacation) throws OaException {
		// ?????????????????????
		User user = userService.getCurrentUser();
		Map<String,Object> result = new HashMap<String,Object>();
		if(departId==null){
			logger.error("???????????????");
			throw new OaException("??????????????????");
		}
		if(year==null){
			logger.error("???????????????");
			throw new OaException("??????????????????");
		}
		if(vacation==null){
			logger.error("???????????????");
			throw new OaException("?????????????????????");
		}
		Depart depart = departMapper.getById(departId);
		Long unCommitDutyId = getUnCommitAndEditableDutyId(departId,user,year,vacation);
		String title = year +"???"+ vacation + "????????????";
		result.put("title", title);
		result.put("company", user.getCompany().getName());
		result.put("depart",depart.getName());
		result.put("approvalStatus", "?????????");
		if(unCommitDutyId != null){
			//???????????????id?????????????????????
			ApplicationEmployeeDutyDetail dutyDetail = new ApplicationEmployeeDutyDetail();
			dutyDetail.setAttnApplicationEmployDutyId(unCommitDutyId);
			List<ApplicationEmployeeDutyDetail> applicationEmployeeDutyDetailList = applicationEmployeeDutyDetailMapper.selectByCondition(dutyDetail);
			//??????????????????
			for (ApplicationEmployeeDutyDetail applicationEmployeeDutyDetail : applicationEmployeeDutyDetailList) {
				Employee employee = employeeMapper.getById(Long.parseLong(applicationEmployeeDutyDetail.getEmployeeIds()));
				applicationEmployeeDutyDetail.setWeekStr(DateUtils.getWeek(applicationEmployeeDutyDetail.getVacationDate()));
				applicationEmployeeDutyDetail.setNames(employee.getCnName());
				applicationEmployeeDutyDetail.setCodes(employee.getCode());
				String startHours = DateUtils.format(applicationEmployeeDutyDetail.getStartTime(), DateUtils.FORMAT_HH_MM);
				String endHours = DateUtils.format(applicationEmployeeDutyDetail.getEndTime(), DateUtils.FORMAT_HH_MM);
				applicationEmployeeDutyDetail.setStartHours(startHours);
				applicationEmployeeDutyDetail.setEndHours(endHours);
			}
			result.put("dutyDetailList", applicationEmployeeDutyDetailList);
		}
		result.put("success", true);
		return result;
	}

	@Override
	public Map<String, Object> queryDuty(String vacationName, String year,
			Long departId) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		//????????????
		//?????????????????????????????????????????????????????????????????????????????????
		ApplicationEmployeeDuty apply = new ApplicationEmployeeDuty();
		apply.setDepartId(departId);
		apply.setYear(year);
		apply.setVacationName(vacationName);
		apply.setApprovalStatus(200);
		apply.setType(0);
		List<ApplicationEmployeeDuty> applyList = applicationEmployeeDutyMapper.getByCondition(apply);
		Map<String,Object> duty = new HashMap<String, Object>();
		EmployeeDuty dutyNumparam = new EmployeeDuty();
		dutyNumparam.setYear(year);
		dutyNumparam.setDepartId(departId);
		dutyNumparam.setVacationName(vacationName);
		if(applyList!=null&&applyList.size()>0){
			//????????????
			duty.put("departName",applyList.get(0).getDepartName());
			//??????id
			duty.put("departId",applyList.get(0).getDepartId());
			//??????
			duty.put("year",applyList.get(0).getYear());
			//???????????????
			duty.put("vacationName",applyList.get(0).getVacationName());
			//?????????
			duty.put("classSetPerson",applyList.get(0).getClassSettingPerson());
			//????????????
			duty.put("applyDate",DateUtils.format(applyList.get(0).getCreateTime(), DateUtils.FORMAT_SHORT));
			//??????????????????????????????????????????????????????
			List<EmployeeDuty> dutyNumList = employeeDutyMapper.getDutyNum(dutyNumparam);
			duty.put("dutyNum",0);
			if(dutyNumList!=null&&dutyNumList.size()>0){
				duty.put("dutyNum",dutyNumList.size());
			}
			//???????????????
			duty.put("auditor", "");
			if(StringUtils.isNotBlank(applyList.get(0).getProcessInstanceId())){
				List<ViewTaskInfoTbl> updateList = viewTaskInfoService.queryTasksByProcessId(applyList.get(0).getProcessInstanceId());
				if(updateList!=null&&updateList.size()>=2){
					duty.put("auditor", updateList.get(0).getAssigneeName());
				}
			}
		}else{
			//??????????????????????????????
			//??????id
			duty.put("departId",departId);
			//????????????
			duty.put("departName","");
			Depart depart = departMapper.getById(departId);
			duty.put("departName",depart!=null?depart.getName():"");
			//??????
			duty.put("year",year);
			//???????????????
			duty.put("vacationName",vacationName);
			//?????????
			duty.put("classSetPerson","");
			//????????????
			duty.put("applyDate","");
			//??????????????????????????????????????????????????????
			List<EmployeeDuty> dutyNumList = employeeDutyMapper.getDutyNum(dutyNumparam);
			duty.put("dutyNum",0);
			//???????????????
			duty.put("auditor", "");
			if(dutyNumList!=null&&dutyNumList.size()>0){
				duty.put("dutyNum",dutyNumList.size());
				duty.put("classSetPerson",dutyNumList.get(0).getCreateUser());
				duty.put("applyDate",DateUtils.format(dutyNumList.get(0).getCreateTime(), DateUtils.FORMAT_SHORT));
				duty.put("auditor", dutyNumList.get(0).getCreateUser());
				result.put("duty", duty);
			}else{
				throw new OaException("????????????????????????");
			}
		}
		
		//????????????
		apply.setType(null);
		List<Integer> typeList = new ArrayList<Integer>();
		typeList.add(1);
		typeList.add(2);
		apply.setTypeList(typeList);
		apply.setApprovalStatus(null);
		List<ApplicationEmployeeDuty> historyList = applicationEmployeeDutyMapper.getByCondition(apply);
		result.put("success",true);
		result.put("duty", duty);
		result.put("historyList", historyList);
		return result;
	}

	@Override
	public Map<String,Object> showDutyDetail(Long departId, String year,
			String vacationName) {
		Map<String,Object> result = new HashMap<String,Object>();
		EmployeeDuty param = new EmployeeDuty();
		param.setDepartId(departId);
		param.setYear(year);
		param.setVacationName(vacationName);
		//????????????
		List<EmployeeDuty> list = employeeDutyMapper.getDutyDetail(param);
		for(EmployeeDuty data:list){
			data.setWeekDay("??????"+DateUtils.getWeek(data.getDutyDate()));
		}
		result.put("list", list);
		//????????????
		result.put("companyName", "");
		//????????????
		result.put("departName", "");
		if(list!=null&&list.size()>0){
			if(list.get(0).getCompanyId()!=null){
				Company company = companyMapper.getById(list.get(0).getCompanyId());
				result.put("companyName", company!=null?company.getName():"");
			}
			if(list.get(0).getDepartId()!=null){
				Depart deaprt = departMapper.getById(list.get(0).getDepartId());
				result.put("departName", deaprt!=null?deaprt.getName():"");
			}
		}
	    //??????
		result.put("title", year+"???"+vacationName+"????????????");
		result.put("success",true);
		return result;
	}
	/**
	 * ?????????????????????
	 * @throws OaException 
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveApplicationDutyDetail(Long departId, String year, String vacation,List<ApplicationEmployeeDutyDetail> detailList) throws OaException {
		logger.info("-------???????????????????????????------");
		if(departId == null){
			logger.error("???????????????");
			throw new OaException("????????????????????????");
		}
		if(year == null){
			logger.error("???????????????");
			throw new OaException("??????????????????");
		}
		if(vacation == null){
			logger.error("???????????????");
			throw new OaException("???????????????????????????");
		}
		
		// ?????????????????????
		User user = userService.getCurrentUser();
		Depart depart = departService.getById(departId);
		//?????????????????????????????????(??????)
		Set<String> empSet = new HashSet<String>();
		if(detailList != null && detailList.size() > 0){
			for (ApplicationEmployeeDutyDetail applicationEmployeeDutyDetail : detailList) {
				empSet.add(applicationEmployeeDutyDetail.getEmployeeIds());
			}
		}
		//????????????????????????????????????
		String empIds = StringUtils.join(empSet, ",");
		//?????????????????????????????????????????????????????????
		Long unCommitAndEditableDutyId = getUnCommitAndEditableDutyId(departId,user,year,vacation);
		//???????????????
		if(unCommitAndEditableDutyId == null){
			ApplicationEmployeeDuty applicationEmployeeDuty = new ApplicationEmployeeDuty();
			applicationEmployeeDuty.setCompanyId(user.getCompanyId());
			applicationEmployeeDuty.setDepartId(departId);
			applicationEmployeeDuty.setDepartName(depart.getName());
			applicationEmployeeDuty.setDutyNum(empSet.size());
			applicationEmployeeDuty.setEmployeeIds(empIds);
			applicationEmployeeDuty.setClassSettingPerson(user.getEmployee().getCnName());
			applicationEmployeeDuty.setYear(year);
			applicationEmployeeDuty.setVacationName(vacation);
			applicationEmployeeDuty.setDelFlag(0);
			applicationEmployeeDuty.setCreateTime(new Date());
			applicationEmployeeDuty.setCreateUser(user.getEmployee().getId().toString());
			applicationEmployeeDuty.setType(0);
			applicationEmployeeDutyMapper.save(applicationEmployeeDuty);
			unCommitAndEditableDutyId = applicationEmployeeDuty.getId();
		}else{
			//???????????????
			ApplicationEmployeeDuty applicationEmployeeDuty = new ApplicationEmployeeDuty();
			applicationEmployeeDuty.setId(unCommitAndEditableDutyId);
			applicationEmployeeDuty.setDutyNum(empSet.size());
			applicationEmployeeDuty.setEmployeeIds(empIds);
			applicationEmployeeDuty.setClassSettingPerson(user.getEmployee().getCnName());
			applicationEmployeeDuty.setUpdateTime(new Date());
			applicationEmployeeDuty.setUpdateUser(user.getEmployee().getId().toString());
			applicationEmployeeDutyMapper.updateById(applicationEmployeeDuty);
			//?????????????????????????????????????????????
			ApplicationEmployeeDutyDetail dutyDetail = new ApplicationEmployeeDutyDetail();
			dutyDetail.setAttnApplicationEmployDutyId(unCommitAndEditableDutyId);
			applicationEmployeeDutyDetailMapper.deleteByDutyId(dutyDetail);
		}
		//?????????????????????
		if(detailList != null && detailList.size() > 0){
			//???????????????????????????????????????????????????
			List<Date> dateList = new ArrayList<Date>();
			List<Map<String,Object>> annualVacationList = annualVacationService.getAnnualDateByYearAndSubject(year,vacation);
			for (Map<String, Object> map : annualVacationList) {
				dateList.add((Date)map.get("day"));
			}
			//???????????????????????????????????????
			Employee employee = new Employee();
			employee.setDepartId(departId);
			List<Employee> empList = employeeMapper.getPageList(employee);
			List<String> empIdList = new ArrayList<String>();
			for (Employee emp : empList) {
				empIdList.add(emp.getId().toString());
			}
			for (ApplicationEmployeeDutyDetail applicationEmployeeDutyDetail : detailList) {
				//???????????????????????????????????????
		        if(!dateList.contains(applicationEmployeeDutyDetail.getVacationDate())){
		        	logger.error("???????????????????????????????????????");
					throw new OaException("???????????????????????????????????????");
		        }
				Date startTime = applicationEmployeeDutyDetail.getStartTime();
				Date endTime = applicationEmployeeDutyDetail.getEndTime();
				Long intervalHours = DateUtils.getIntervalHours(startTime, endTime);
				//???????????????5???1???10???2
				if(5 <intervalHours && intervalHours<10  ){
					intervalHours -= 1;
				}
				if(intervalHours >= 10  ){
					intervalHours -= 2;
				}
				applicationEmployeeDutyDetail.setWorkHours(intervalHours.doubleValue() > 0 ? intervalHours.doubleValue() : 0);
//				if(applicationEmployeeDutyDetail.getWorkHours().equals(0)){
//					logger.error("????????????????????????????????????");
//					throw new OaException("?????????????????????????????????");
//				}
				//????????????
				Employee dutyEmp = employeeMapper.getById(Long.parseLong(applicationEmployeeDutyDetail.getEmployeeIds()));
				//?????????????????????????????????
				if(!empIdList.contains(applicationEmployeeDutyDetail.getEmployeeIds())){
					logger.error("???????????????????????????????????????");
					throw new OaException("??????????????????????????????");
				}
				//???????????????????????????????????????(source = 1)?????????
				EmployeeDuty empDuty = new EmployeeDuty();
				empDuty.setEmployId(Long.parseLong(applicationEmployeeDutyDetail.getEmployeeIds()));
				empDuty.setDutyDate(applicationEmployeeDutyDetail.getVacationDate());
				empDuty.setSource(1);
				List<EmployeeDuty> existDutyList = employeeDutyMapper.getDutyListBySource(empDuty);
				if(existDutyList != null && existDutyList.size() > 0){
					String dutyDate = DateUtils.format(applicationEmployeeDutyDetail.getVacationDate(),DateUtils.FORMAT_SHORT);
					logger.error(dutyEmp.getCnName()+"???"+dutyDate+"???????????????");
					throw new OaException(dutyEmp.getCnName()+"???"+dutyDate+"???????????????");
				}
				applicationEmployeeDutyDetail.setAttnApplicationEmployDutyId(unCommitAndEditableDutyId);
				applicationEmployeeDutyDetail.setCreateTime(new Date());
				applicationEmployeeDutyDetail.setDelFlag(0);
				applicationEmployeeDutyDetail.setIsMove(0);
			}
			applicationEmployeeDutyDetailMapper.batchSave(detailList);
		}
		logger.info("-------???????????????????????????------");
	}
	
	/**
	 * ?????????????????????????????????
	 * @param departId
	 * @param year
	 * @param vacation
	 * @return
	 * @throws OaException
	 */
	private Long getUnCommitAndEditableDutyId(Long departId,User user,String year,String vacation) throws OaException {
		//?????????????????????id
		Long unCommitDutyId = null;
		//????????????????????????????????????????????????????????????
		List<Depart> departList = departService.getAllDepartByLeaderId(user.getEmployee().getId());
		//departList?????????????????????????????????????????????????????????????????????????????????????????????
//		if(departList != null && departList.size() > 0){
//			List<Long> departIdList = new ArrayList<Long>();
//			for (Depart depart : departList) {
//				departIdList.add(depart.getId());
//			}
//			if(!departIdList.contains(departId)){
//				logger.error("?????????????????????");
//				throw new OaException("?????????????????????");
//			}
//		}
		//????????????????????????
		//????????????????????????????????????
		EmployeeDuty empDuty = new EmployeeDuty();
		empDuty.setDepartId(departId);
		empDuty.setYear(year);
		empDuty.setVacationName(vacation);
		List<EmployeeDuty> empDutyList = employeeDutyMapper.getExistDutyListByCondition(empDuty);
		if(empDutyList != null && empDutyList.size() > 0){
			logger.error("????????????????????????????????????");
			throw new OaException("????????????????????????????????????");
		}
		//?????????????????????
		ApplicationEmployeeDuty condition = new ApplicationEmployeeDuty();
		condition.setYear(year);
		condition.setVacationName(vacation);
		condition.setDepartId(departId);
		condition.setType(0);
		List<ApplicationEmployeeDuty> applicationEmployeeDutyList = applicationEmployeeDutyMapper.getByCondition(condition);
		if(applicationEmployeeDutyList != null && applicationEmployeeDutyList.size() > 0){
			for (ApplicationEmployeeDuty applicationEmployeeDuty : applicationEmployeeDutyList) {
				//??????????????????????????????????????????????????????id
				if(applicationEmployeeDuty.getApprovalStatus() == null){
					unCommitDutyId = applicationEmployeeDuty.getId();
				}else if(applicationEmployeeDuty.getApprovalStatus().equals(ConfigConstants.DOING_STATUS) || 
						applicationEmployeeDuty.getApprovalStatus().equals(ConfigConstants.PASS_STATUS) ||
						applicationEmployeeDuty.getApprovalStatus().equals(ConfigConstants.OVERDUE_STATUS) ||
						applicationEmployeeDuty.getApprovalStatus().equals(ConfigConstants.OVERDUEPASS_STATUS)){
					logger.error("??????????????????????????????????????????????????????????????????????????????");
					throw new OaException("??????????????????????????????????????????????????????????????????????????????");
				}
			}
		}
		return unCommitDutyId;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> commitDutyByHr(HttpServletRequest request,String departId, String year,
			String vacationName, String info) {
		
		User user = userService.getCurrentUser();
		logger.info("????????????commitDutyByHr??????:departId="+departId+";year="+year+";vacationName="+vacationName
				+";info="+info+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));
		
		Map<String, Object> result = new HashMap<String, Object>();
		if(StringUtils.isBlank(departId)){
			result.put("success", false);
			result.put("message", "?????????????????????");
			return result;
		}
		if(StringUtils.isBlank(year)){
			result.put("success", false);
			result.put("message", "?????????????????????");
			return result;
		}
		if(StringUtils.isBlank(vacationName)){
			result.put("success", false);
			result.put("message", "??????????????????????????????");
			return result;
		}
		
		Depart depart = departMapper.getById(Long.valueOf(departId));
		//???????????????????????????????????????????????????
		List<ApplicationEmployeeDutyDetail> dutyHistoryDetail = new ArrayList<ApplicationEmployeeDutyDetail>();
		
		//????????????????????????
		EmployeeDuty param = new EmployeeDuty();
		param.setDepartId(Long.valueOf(departId));
		param.setYear(year);
		param.setVacationName(vacationName);
		List<EmployeeDuty> oldDutyList = employeeDutyMapper.getDutyDetail(param);
		
		//??????????????????
		ApplicationEmployeeDuty dutyHistory = new ApplicationEmployeeDuty();
		dutyHistory.setCompanyId(user.getCompanyId());//??????
		dutyHistory.setDepartId(Long.valueOf(departId));//????????????
		dutyHistory.setDepartName(depart!=null?depart.getName():"");//????????????
		dutyHistory.setYear(year);//??????
		dutyHistory.setVacationName(vacationName);//???????????????
		dutyHistory.setCreateTime(new Date());//????????????
		dutyHistory.setType(2);//????????????
		dutyHistory.setCreateUser(user.getEmployee().getCnName());//???????????????
		dutyHistory.setApprovalStatus(200);
		dutyHistory.setApprovalReason("??????????????????");
		dutyHistory.setDelFlag(0);
		dutyHistory.setVersion(0L);
		applicationEmployeeDutyMapper.save(dutyHistory);
		
		JSONArray infoArray = JSONArray.fromObject(info);
		List<EmployeeDuty> newDutyList = new ArrayList<EmployeeDuty>();//?????????????????????
		
		for(int i=0;i<infoArray.size();i++){
			//??????????????????
			EmployeeDuty duty = new EmployeeDuty();
			JSONObject dutyObject = JSONObject.fromObject(infoArray.get(i), new JsonConfig());
			duty.setCompanyId(user.getCompanyId());
			duty.setDepartId(Long.valueOf(departId));
			duty.setEmployId(dutyObject.getLong("employId"));
			duty.setEmployName(dutyObject.getString("employName"));
			duty.setYear(year);
			duty.setVacationName(vacationName);
			String dutyDate = dutyObject.getString("dutyDate");
			duty.setDutyDate(DateUtils.parse(dutyDate, DateUtils.FORMAT_SHORT));
			duty.setDutyItem(dutyObject.getString("workItems"));
			duty.setStartTime(DateUtils.parse(dutyDate+" "+dutyObject.getString("startTime")+":00", DateUtils.FORMAT_LONG));
			duty.setEndTime(DateUtils.parse(dutyDate+" "+dutyObject.getString("endTime")+":00", DateUtils.FORMAT_LONG));
			duty.setWorkHours(dutyObject.getDouble("workHours"));
			duty.setClassSettingPerson(user.getEmployee().getCnName());
			duty.setCreateTime(new Date());
			duty.setCreateUser(user.getEmployee().getCnName());
			duty.setDelFlag(0);
			duty.setSource(2);//??????????????????
			duty.setRemark(dutyObject.getString("remark"));
			newDutyList.add(duty);
			//???????????????????????????
			ApplicationEmployeeDutyDetail afterDuty = new ApplicationEmployeeDutyDetail();
			afterDuty.setAttnApplicationEmployDutyId(dutyHistory.getId());
			afterDuty.setVacationDate(DateUtils.parse(dutyDate, DateUtils.FORMAT_SHORT));
			afterDuty.setDutyItem(dutyObject.getString("workItems"));
			afterDuty.setEmployeeIds(dutyObject.getString("employId"));
			afterDuty.setStartTime(DateUtils.parse(dutyDate+" "+dutyObject.getString("startTime")+":00", DateUtils.FORMAT_LONG));
			afterDuty.setEndTime(DateUtils.parse(dutyDate+" "+dutyObject.getString("endTime")+":00", DateUtils.FORMAT_LONG));
			afterDuty.setWorkHours(dutyObject.getDouble("workHours"));
			afterDuty.setCreateTime(new Date());
			afterDuty.setCreateUser(user.getEmployee().getCnName());
			afterDuty.setRemarks(dutyObject.getString("remark"));
			afterDuty.setIsMove(0);
			afterDuty.setDelFlag(0);
			dutyHistoryDetail.add(afterDuty);
		}
		//?????????????????????-???????????? ??????
		for(EmployeeDuty before:oldDutyList){
			if(before.getSource().intValue()==1){
				ApplicationEmployeeDutyDetail afterDuty = new ApplicationEmployeeDutyDetail();
				afterDuty.setAttnApplicationEmployDutyId(dutyHistory.getId());
				afterDuty.setVacationDate(before.getDutyDate());
				afterDuty.setDutyItem(before.getDutyItem());
				afterDuty.setEmployeeIds(String.valueOf(before.getEmployId()));
				afterDuty.setStartTime(before.getStartTime());
				afterDuty.setEndTime(before.getEndTime());
				afterDuty.setWorkHours(before.getWorkHours());
				afterDuty.setCreateTime(new Date());
				afterDuty.setCreateUser(user.getEmployee().getCnName());
				afterDuty.setRemarks(before.getRemark());
				afterDuty.setIsMove(0);
				afterDuty.setDelFlag(0);
				dutyHistoryDetail.add(afterDuty);
			}
		}
		//???????????????????????????
		for(EmployeeDuty before:oldDutyList){
			ApplicationEmployeeDutyDetail beforeDuty = new ApplicationEmployeeDutyDetail();
			beforeDuty.setAttnApplicationEmployDutyId(dutyHistory.getId());
			beforeDuty.setVacationDate(before.getDutyDate());
			beforeDuty.setDutyItem(before.getDutyItem());
			beforeDuty.setEmployeeIds(String.valueOf(before.getEmployId()));
			beforeDuty.setStartTime(before.getStartTime());
			beforeDuty.setEndTime(before.getEndTime());
			beforeDuty.setWorkHours(before.getWorkHours());
			beforeDuty.setCreateTime(new Date());
			beforeDuty.setCreateUser(user.getEmployee().getCnName());
			beforeDuty.setRemarks(before.getRemark());
			beforeDuty.setIsMove(1);
			beforeDuty.setDelFlag(0);
			dutyHistoryDetail.add(beforeDuty);
		}
		//??????????????????????????????
		applicationEmployeeDutyDetailMapper.batchSave(dutyHistoryDetail);
		//????????????????????????
		List<Integer> sourceList = new ArrayList<Integer>();
		sourceList.add(0);//????????????
		sourceList.add(2);//hr????????????
		param.setSourceList(sourceList);
		param.setUpdateTime(new Date());
		param.setUpdateUser(user.getEmployee().getCnName());
		employeeDutyMapper.deleteOldDuty(param);
		//????????????????????????
		employeeDutyMapper.batchSave(newDutyList);
		result.put("success", true);
		result.put("message", "???????????????");
		return result;
	}

	@Override
	public Map<String, Object> showHistoryDetail(Long id) {
		Map<String,Object> result = new HashMap<String,Object>();
		if(id==null){
			result.put("success", false);
			result.put("message", "???????????????");
			return result;
		}
		ApplicationEmployeeDuty duty = applicationEmployeeDutyMapper.getById(id);
		if(duty==null){
			result.put("success", false);
			result.put("message", "????????????,????????????????????????");
			return result;
		}
		result.put("title", duty.getYear()+"???"+duty.getVacationName()+"????????????????????????");
		result.put("companyName","");
		if(duty.getCompanyId()!=null){
			Company company = companyMapper.getById(duty.getCompanyId());
			result.put("companyName",company!=null?company.getName():"");
		}
		result.put("departName",duty.getDepartName());
		ApplicationEmployeeDutyDetail param = new ApplicationEmployeeDutyDetail();
		param.setAttnApplicationEmployDutyId(id);
		List<ApplicationEmployeeDutyDetail> list =  applicationEmployeeDutyDetailMapper.selectByCondition(param);
		for(ApplicationEmployeeDutyDetail data:list){
			data.setWeekStr("??????"+DateUtils.getWeek(data.getVacationDate()));
		}
		result.put("success", true);
		result.put("list", list);
		return result;
	}
	
	/**
	 * ????????????
	 * @throws OaException 
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void commitDuty(HttpServletRequest request,Long departId, String year, String vacation) throws OaException {
			
		User user= userService.getCurrentUser();
		logger.info("commitDuty??????:departId="+departId+";year="+year+";vacation="+vacation
			+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));
		
		if(departId == null || year == null || vacation == null){
			logger.error("???????????????");
			throw new OaException("???????????????");
		}
		//?????????????????????????????????????????????????????????????????????
		Long unCommitDutyId = getUnCommitAndEditableDutyId(departId, user, year, vacation);
		if(unCommitDutyId == null){
			logger.error("?????????????????????????????????");
			throw new OaException("?????????????????????????????????");
		}
		//???????????????????????????
		//??????????????????????????????????????????????????????????????????????????????
		if(!"107".equals(user.getDepart().getCode())){
			List<Map<String,Object>> dateList = annualVacationService.getAnnualDateByYearAndSubject(year,vacation);
			Date annualStartDate = (Date) dateList.get(0).get("day");
			//???????????????????????????
			Config configCondition = new Config();
			configCondition.setCode("dutyCommitTimeLimit");
			List<Config> limit = configService.getListByCondition(configCondition);
			int num = Integer.valueOf(limit.get(0).getDisplayCode());
			Date lastSubDate = annualVacationService.getWorkingDayPre(num,annualStartDate);
			if(DateUtils.getIntervalDays(new Date(),lastSubDate)<0){
				logger.error("????????????????????????????????????????????????4?????????????????????");
				throw new OaException("????????????????????????????????????????????????4?????????????????????");
			}
		};
		
		ApplicationEmployeeDutyDetail dutyDetail = new ApplicationEmployeeDutyDetail();
		dutyDetail.setAttnApplicationEmployDutyId(unCommitDutyId);
		List<ApplicationEmployeeDutyDetail> detailList = applicationEmployeeDutyDetailMapper.selectByCondition(dutyDetail);
		if(detailList==null||detailList.size()<=0){
			logger.error("?????????????????????");
			throw new OaException("?????????????????????");
		}
		
		//????????????
		String processInstanceId = activitiServiceImpl.startByKey(ConfigConstants.DUTY_KEY);
		
		//??????????????????
		ApplicationEmployeeDuty empDuty = new ApplicationEmployeeDuty();
		empDuty.setId(unCommitDutyId);
		empDuty.setSubmitDate(new Date());
		empDuty.setProcessInstanceId(processInstanceId);
		empDuty.setClassSettingPerson(user.getEmployee().getCnName());
		empDuty.setApprovalStatus(ConfigConstants.DOING_STATUS);
		empDuty.setUpdateUser(user.getEmployee().getCnName());
		empDuty.setUpdateTime(new Date());
		//??????????????????
		int count = applicationEmployeeDutyMapper.updateById(empDuty);
		if(count<=0){
			logger.error("?????????????????????????????????");
			throw new OaException("?????????????????????????????????");
		}
		
		//-----------------start-----------------------????????????????????????
		ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
		flow.setAssigneeName(user.getEmployee().getCnName());
		flow.setDepartName(user.getDepart().getName());
		flow.setPositionName(user.getPosition().getPositionName());
		flow.setFinishTime(new Date());
		flow.setComment("????????????");
		flow.setProcessId(processInstanceId);
		flow.setProcessKey(ConfigConstants.DUTY_KEY);
		flow.setStatu(0);
		viewTaskInfoService.save(flow);
		
		logger.info("-------------??????????????????--------------");
	}
	
	/**
	 * ??????????????????
	 */
	@Override
	public PageModel<ApplicationEmployeeDuty> getDutyRecord(ApplicationEmployeeDuty requestParam) {
		//???????????????
		User user = userService.getCurrentUser();
		PageModel<ApplicationEmployeeDuty> pm = new PageModel<ApplicationEmployeeDuty>();
		int page = requestParam.getPage() == null ? 0 : requestParam.getPage();
		int rows = requestParam.getRows() == null ? 0 : requestParam.getRows();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		requestParam.setLimit(pm.getLimit());
		requestParam.setOffset(pm.getOffset());	
		//?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
		if(requestParam.getDepartId() == null){
			//???????????????????????????????????????
			List<Depart> departList = departService.getAllDepartByLeaderId(user.getEmployee().getId());
			if(departList != null && departList.size() > 0){
				//???????????????
				List<Long> departIds = new ArrayList<Long>();
				for (Depart depart : departList) {
					departIds.add(depart.getId());
				}
				//?????????????????????
				requestParam.setDepartIds(departIds);
			}
			//????????????????????????????????????
		}
		requestParam.setType(0);
		List<Integer> approvalStatusList = new ArrayList<Integer>();
		approvalStatusList.add(ConfigConstants.DOING_STATUS);//?????????
		approvalStatusList.add(ConfigConstants.PASS_STATUS);//??????
		approvalStatusList.add(ConfigConstants.REFUSE_STATUS);//??????
		approvalStatusList.add(ConfigConstants.BACK_STATUS);//??????
		approvalStatusList.add(ConfigConstants.OVERDUE_STATUS);//??????
		approvalStatusList.add(ConfigConstants.OVERDUEPASS_STATUS);//????????????
		approvalStatusList.add(ConfigConstants.OVERDUEREFUSE_STATUS);//????????????
		requestParam.setApprovalStatusList(approvalStatusList);
		List<ApplicationEmployeeDuty> list = applicationEmployeeDutyMapper.getByCondition(requestParam);
		//?????????????????????
		for (ApplicationEmployeeDuty applicationEmployeeDuty : list) {
			if(StringUtils.isNotBlank(applicationEmployeeDuty.getProcessInstanceId())){
				List<ViewTaskInfoTbl> updateList = viewTaskInfoService.queryTasksByProcessId(applicationEmployeeDuty.getProcessInstanceId());
				if(updateList!=null&&updateList.size()>=2){
					applicationEmployeeDuty.setHrAuditor(updateList.get(0).getAssigneeName());
				}
			}
			applicationEmployeeDuty.setHrAuditor(StringUtils.isBlank(applicationEmployeeDuty.getHrAuditor()) ?"":applicationEmployeeDuty.getHrAuditor());
		}
		Integer count = applicationEmployeeDutyMapper.getByConditionCount(requestParam);
		pm.setRows(list);
		pm.setTotal(count);
		
		return pm;
	}

	@Override
	public Map<String, Object> getDutyDetailById(Long id) throws OaException {
		// ?????????????????????
		User user = userService.getCurrentUser();
		Map<String,Object> result = new HashMap<String,Object>();
		if(id==null){
			logger.error("???????????????");
			throw new OaException("???????????????????????????");
		}
		ApplicationEmployeeDuty applicationEmployeeDuty = applicationEmployeeDutyMapper.getById(id);
		String title = applicationEmployeeDuty.getYear() +"???"+ applicationEmployeeDuty.getVacationName() + "????????????";
		result.put("title", title);
		result.put("companyName", user.getCompany().getName());
		result.put("departName",applicationEmployeeDuty.getDepartName());
		result.put("approvalStatus", applicationEmployeeDuty.getApprovalStatus());
		//???????????????id?????????????????????
		ApplicationEmployeeDutyDetail dutyDetail = new ApplicationEmployeeDutyDetail();
		dutyDetail.setAttnApplicationEmployDutyId(id);
		List<ApplicationEmployeeDutyDetail> applicationEmployeeDutyDetailList = applicationEmployeeDutyDetailMapper.selectByCondition(dutyDetail);
		//??????????????????
		for (ApplicationEmployeeDutyDetail applicationEmployeeDutyDetail : applicationEmployeeDutyDetailList) {
			Employee employee = employeeMapper.getById(Long.parseLong(applicationEmployeeDutyDetail.getEmployeeIds()));
			applicationEmployeeDutyDetail.setWeekStr(DateUtils.getWeek(applicationEmployeeDutyDetail.getVacationDate()));
			applicationEmployeeDutyDetail.setNames(employee.getCnName());
			applicationEmployeeDutyDetail.setCodes(employee.getCode());
			String startHours = DateUtils.format(applicationEmployeeDutyDetail.getStartTime(), DateUtils.FORMAT_HH_MM);
			String endHours = DateUtils.format(applicationEmployeeDutyDetail.getEndTime(), DateUtils.FORMAT_HH_MM);
			applicationEmployeeDutyDetail.setStartHours(startHours);
			applicationEmployeeDutyDetail.setEndHours(endHours);
		}
		result.put("dutyDetailList", applicationEmployeeDutyDetailList);
		result.put("success", true);
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, String> importDutyTemplate(MultipartFile file, Long departId, String year, String vacation) throws OaException, IOException {
		logger.info("????????????-----??????");
		// ?????????????????????
		User user = userService.getCurrentUser();
		//????????????
		Map<String, String> resultMap = new HashMap<String, String>();
		// ????????????????????????
		if (null == file) {
			logger.error("??????????????????");
			throw new OaException("??????????????????");
		}
		// ???????????????
		String fileName = file.getOriginalFilename();
		/**1.?????????????????????excel??????*/
		if (!fileName.endsWith("xls") && !fileName.endsWith("xlsx")) {
			logger.error(fileName + "??????excel??????");
			throw new OaException("??????????????????????????????");
		}
		List<ApplicationEmployeeDutyDetail> detailList = new LinkedList<ApplicationEmployeeDutyDetail>();
		// ??????????????????????????????
		List<List<Object>> excelList = ExcelUtil.readExcel(file, fileName, 6);
		if(departId == null){
			logger.error("????????????????????????????????????");
			throw new OaException("????????????????????????????????????");
		}
		Depart checkDepart = departMapper.getById(departId);
		//???????????????????????????????????????
		String importDepartName =  (String) excelList.get(1).get(3);
		importDepartName = importDepartName.split(":")[1];
		if(importDepartName == null || !checkDepart.getName().equals(importDepartName)){
			logger.error("?????????????????????????????????????????????????????????????????????????????????????????????");
			throw new OaException("?????????????????????????????????????????????????????????????????????????????????????????????");
		}
		//??????????????????????????????
		if(excelList.size() < 5){
			logger.error("???????????????????????????????????????????????????????????????");
			throw new OaException("???????????????????????????????????????????????????????????????");

		}
		//??????????????????
		for(int i = 4; i < excelList.size(); i++){
			ApplicationEmployeeDutyDetail applicationEmployeeDutyDetail = new ApplicationEmployeeDutyDetail();
			//???????????????????????????????????????????????????
			List<Object> list = excelList.get(i);
			Date vacationDate = (Date)list.get(0);
			String empCode = (String)list.get(1);
			String empName = (String)list.get(2);
			Date startHour = (Date)list.get(3);
			Date endHour = (Date)list.get(4);
			String dateStr = DateUtils.format(vacationDate, DateUtils.FORMAT_SHORT);
			String startHourStr = DateUtils.format(startHour, DateUtils.FORMAT_HH_MM_SS);
			String endHourStr = DateUtils.format(endHour, DateUtils.FORMAT_HH_MM_SS);
			Date startTime = DateUtils.parse(dateStr +" "+startHourStr);
			Date endTime = DateUtils.parse(dateStr +" "+endHourStr);
			String dutyItem = (String)list.get(5);
			String remarks = (String)list.get(6);
			applicationEmployeeDutyDetail.setVacationDate(vacationDate);
			String empId = employeeMapper.getEmpIdByCode(empCode).toString();
			//??????????????????????????????????????????
			Employee employee = new Employee();
			employee.setDepartId(departId);
			List<Employee> empList = employeeMapper.getPageList(employee);
			List<String> empIdList = new ArrayList<String>();
			for (Employee emp : empList) {
				empIdList.add(emp.getId().toString());
			}
			if(empId == null || !empIdList.contains(empId)){
				logger.error(empName+"?????????????????????????????????????????????????????????????????????");
				throw new OaException(empName+"?????????????????????????????????????????????????????????????????????");
			}
			applicationEmployeeDutyDetail.setEmployeeIds(empId.toString());
			applicationEmployeeDutyDetail.setStartTime(startTime);
			applicationEmployeeDutyDetail.setEndTime(endTime);
			applicationEmployeeDutyDetail.setDutyItem(dutyItem);
			applicationEmployeeDutyDetail.setRemarks(remarks);
			detailList.add(applicationEmployeeDutyDetail);
		}
		saveApplicationDutyDetail(departId, year, vacation, detailList);
		
		resultMap.put("result", "success");
		resultMap.put("resultMsg", "???????????????");
		return resultMap;
	}

	@Override
	public PageModel<ApplicationEmployeeDuty> getDutyTaskListByPage(
			ApplicationEmployeeDuty employeeDuty) {
		// TODO Auto-generated method stub
		int page = employeeDuty.getPage() == null ? 0 : employeeDuty.getPage();
		int rows = employeeDuty.getRows() == null ? 0 : employeeDuty.getRows();
		
		PageModel<ApplicationEmployeeDuty> pm = new PageModel<ApplicationEmployeeDuty>();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		Integer total = applicationEmployeeDutyMapper.myDutyTaskListCount(employeeDuty);
		pm.setTotal(total);
		
		employeeDuty.setOffset(pm.getOffset());
		employeeDuty.setLimit(pm.getLimit());
		
		List<ApplicationEmployeeDuty> list = applicationEmployeeDutyMapper.myDutyTaskList(employeeDuty);
        
		for(ApplicationEmployeeDuty og:list){
			try{
				ViewTaskInfoTbl taskInfo = null;
				taskInfo = viewTaskInfoService.getFirstAuditUser(og.getProcessInstanceId(),ConfigConstants.DUTY_KEY,false);
				if(null != taskInfo){
					og.setHrAuditor(taskInfo.getAssigneeName());	//????????????
				}
			}catch(Exception e){
				og.setHrAuditor(" ");
			}
		}
		
		pm.setRows(list);
		return pm;
	}
}
