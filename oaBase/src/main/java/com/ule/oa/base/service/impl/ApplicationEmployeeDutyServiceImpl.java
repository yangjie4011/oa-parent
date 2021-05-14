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
 * @ClassName: 排班申请表
 * @Description: 排班申请表
 * @author yangjie
 * @date 2017年8月31日
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
			throw new OaException("节假日不能为空！");
		}
		if(duty.getDepartId()==null){
			throw new OaException("部门不能为空！");
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
				throw new OaException("不是该部门负责人！");
			}
		}else{
			throw new OaException("不是该部门负责人！");
		}
		duty.setDelFlag(0);
		duty.setVersion(0L);
		duty.setClassSettingPerson(user.getEmployee().getCnName());
		duty.setCreateTime(new Date());
		duty.setCreateUser(user.getEmployee().getCnName());
		duty.setYear(DateUtils.format(new Date(),"yyyy"));
		duty.setEmployeeId(user.getEmployee().getId());
		if("元旦".equals(duty.getVacationName())){
			duty.setYear(DateUtils.format(DateUtils.addYear(new Date(), 1),"yyyy"));
		}
		//提前四个工作日提交
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
			//获取节假日前连续非工作日
			startDate = DateUtils.addDay(list.get(0).getAnnualDate(), -1);
			while(true){
				if(annualVacationService.judgeWorkOrNot(startDate)){
					break;
				}
				startDate = DateUtils.addDay(startDate, -1);
			}
		}else{
			throw new OaException("没有请假数据，请联系管理员！");
		}
		Date lastSubDate = annualVacationService.getWorkingDayPre(4,startDate);
		if(DateUtils.getIntervalDays(new Date(),lastSubDate)<0){
			throw new OaException("值班申请需提前4个工作日提交！");
		}
		List<ApplicationEmployeeDuty> list1 = applicationEmployeeDutyMapper.getByCondition(duty);
		if(list1!=null&&list1.size()>0){
			throw new OaException("值班不能重复申请！");
		}
		int count = applicationEmployeeDutyMapper.save(duty);
		if(count<=0){
			throw new OaException("系统异常，请刷新重试！");
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateEmployeeDuty(User user, Long dutyId,List<ApplicationEmployeeDutyDetail> list) throws OaException {
		ApplicationEmployeeDutyDetail param = new ApplicationEmployeeDutyDetail();
		param.setAttnApplicationEmployDutyId(dutyId);
		List<ApplicationEmployeeDutyDetail> orgrnial = applicationEmployeeDutyDetailMapper.selectByCondition(param);
		//找到新增，删除，修改的事项
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
		//找到该删除的id
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
		//主表employeeIds变动
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
				throw new OaException("尚未编辑值班！");
			}
			//XXX----------------------------开始流程------------------------------
			String processInstanceId = activitiServiceImpl.startByKey(ConfigConstants.DUTY_KEY);
			
			//-----------------start-----------------------保存流程节点信息
			ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
			flow.setAssigneeName(user.getEmployee().getCnName());
			flow.setDepartName(user.getDepart().getName());
			flow.setPositionName(user.getPosition().getPositionName());
			flow.setFinishTime(new Date());
			flow.setComment("排班申请");
			flow.setProcessId(processInstanceId);
			flow.setProcessKey(ConfigConstants.DUTY_KEY);
			flow.setStatu(0);
			viewTaskInfoService.save(flow);
			
			//流程与业务数据关联
			applicationEmployeeDuty.setProcessInstanceId(processInstanceId);
			applicationEmployeeDuty.setApprovalStatus(ConfigConstants.DOING_STATUS);
			applicationEmployeeDuty.setUpdateUser(user.getEmployee().getCnName());
			applicationEmployeeDuty.setUpdateTime(new Date());
			//修改排班状态
			int count = applicationEmployeeDutyMapper.updateById(applicationEmployeeDuty);
			if(count<=0){
				throw new OaException("系统异常，请刷新重试！");
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
		
		User user = userService.getCurrentUser();
		logger.info("值班申请completeTask入参:processId="+processId+";comment="+comment+";commentType="+commentType
				+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));
		
		Task task = activitiServiceImpl.queryTaskByProcessInstanceId(processId);
		if(task==null){
			throw new OaException("实例Id为"+processId+"的流程不存在！");
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
				//复制生成新的 值班数据单、详情单  （方便于值班安排）
				ApplicationEmployeeDuty dutyCopy=new ApplicationEmployeeDuty();
				dutyCopy=duty;
				dutyCopy.setProcessInstanceId(null);
				dutyCopy.setApprovalStatus(null);
				dutyCopy.setUpdateUser(user.getEmployee().getCnName());
				dutyCopy.setCreateUser(user.getEmployee().getCnName());
				dutyCopy.setUpdateTime(new Date());
				dutyCopy.setSubmitDate(null);
				applicationEmployeeDutyMapper.save(dutyCopy);
				//复制生成新的 值班详情单  （方便于值班安排）
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
			//判断当前节点是否有办理人assignee,没有则将当前代理人设为办理人
			if(StringUtils.isBlank(task.getAssignee())) {
				activitiServiceImpl.claimTask(task.getId(), String.valueOf(user.getEmployeeId()));
			}
			if(type){
				param.setVersion(0L);
				//查询该部门同个假期是否有其他的申请数据，如果有就修改版本号为老版本（0-最新版本，1-老版本）
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
			//-----------------start-----------------------保存流程节点信息
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
			activitiServiceImpl.completeTask(task.getId(),"审批通过",null,commentType);
		}else{
			throw new OaException("流程实例为"+processId+"的值班审批数据不存在！");
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
				throw new OaException("值班申请审核中，不能删除！");
			}else{
				if(applicationEmployeeDutyMapper.deleteById(list.get(0))>0){
					ApplicationEmployeeDutyDetail detail =new ApplicationEmployeeDutyDetail();
					detail.setAttnApplicationEmployDutyId(id);
					detail.setUpdateTime(new Date());
					detail.setUpdateUser(user.getEmployee().getCnName());
					applicationEmployeeDutyDetailMapper.deleteByDutyId(detail);
				}else{
					throw new OaException("系统异常，请刷新重试！");
				}
			}
		}else{
			throw new OaException("值班申请数据不存在！");
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
		//原始单据
		ApplicationEmployeeDuty old = applicationEmployeeDutyMapper.getById(dutyId);
		
		//提前四个工作日提交
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
			//获取节假日前连续非工作日
			startDate = DateUtils.addDay(list1.get(0).getAnnualDate(), -1);
			while(true){
				if(annualVacationService.judgeWorkOrNot(startDate)){
					break;
				}
				startDate = DateUtils.addDay(startDate, -1);
			}
		}else{
			throw new OaException("没有请假数据，请联系管理员！");
		}
		Date lastSubDate = annualVacationService.getWorkingDayPre(4,startDate);
		if(DateUtils.getIntervalDays(new Date(),lastSubDate)<0){
			throw new OaException("值班申请需提前4个工作日提交！");
		}
		
		//XXX----------------------------开始流程------------------------------
		String processInstanceId = activitiServiceImpl.startByKey(ConfigConstants.DUTY_KEY);
		
		//-----------------start-----------------------保存流程节点信息
		ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
		flow.setAssigneeName(user.getEmployee().getCnName());
		flow.setDepartName(user.getDepart().getName());
		flow.setPositionName(user.getPosition().getPositionName());
		flow.setFinishTime(new Date());
		flow.setComment("排班申请");
		flow.setProcessId(processInstanceId);
		flow.setProcessKey(ConfigConstants.DUTY_KEY);
		flow.setStatu(0);
		viewTaskInfoService.save(flow);
		
		//流程与业务数据关联,重新生成调班数据
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
			throw new OaException("系统异常，请刷新重试！");
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
		map.put("message", "已提交，审核通过后生效!");
		return map;
	}
	
	@Override
	public void exportEmpDutyReprotById(Long departId,String vacationName,String year,User user) throws Exception {
		
		//最终值班数据
		EmployeeDuty param = new EmployeeDuty();
		param.setDepartId(departId);
		param.setYear(year);
		param.setVacationName(vacationName);
		List<EmployeeDuty> list = employeeDutyMapper.selectByCondition(param);
		//收件人邮箱
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
		//根据部门，年份，节假日名称查看值班个数
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
			
			//查询值班申请表
			ApplicationEmployeeDuty apply = new ApplicationEmployeeDuty();
			apply.setDepartId(Long.valueOf(key.split("_")[0]));
			apply.setYear(key.split("_")[1]);
			apply.setVacationName(key.split("_")[2]);
			List<ApplicationEmployeeDuty> applyList = applicationEmployeeDutyMapper.getByCondition(apply);
			if(applyList!=null&&applyList.size()>0){
				result.put("applyDate",DateUtils.format(applyList.get(0).getCreateTime(), DateUtils.FORMAT_SHORT));
			}
			
			//查询部门
			Depart depart = departService.getById(Long.valueOf(key.split("_")[0]));
			result.put("title", "");
			if(depart!=null){
				result.put("title", key.split("_")[1]+depart.getName()+key.split("_")[2]+"值班安排");
			}
			
			//查询值班数据
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
				data.setWeekDay("星期"+DateUtils.getWeek(data.getDutyDate()));
			}
			
			//查询值班申请表
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
				// 表头标题样式
				HSSFCellStyle colstyle = workbook.createCellStyle();
				colstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
				colstyle.setBorderBottom(XSSFCellStyle.BORDER_THIN); //下边框
				colstyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);//左边框
				colstyle.setBorderTop(XSSFCellStyle.BORDER_THIN);//上边框
				colstyle.setBorderRight(XSSFCellStyle.BORDER_THIN);//右边框
				
				//第一行
				HSSFRow row = sheet.createRow((short) 0);
				//第一行内容
				HSSFCellStyle colstyle1 = workbook.createCellStyle();
				colstyle1.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
				ExcelUtil.createRow(row, 0, colstyle1, HSSFCell.CELL_TYPE_STRING,year+vacationName+"值班安排");
				sheet.addMergedRegion(new CellRangeAddress(0,0,0,8));
				sheet.setColumnWidth(0, 5000);//设置表格宽度
				//第3行
				row = sheet.createRow((short) 2);
				ExcelUtil.createRow(row, 0, null, HSSFCell.CELL_TYPE_STRING,"公司：");
				ExcelUtil.createRow(row, 1, null, HSSFCell.CELL_TYPE_STRING,userService.getCurrentUser().getCompany().getName());
				ExcelUtil.createRow(row, 5, null, HSSFCell.CELL_TYPE_STRING,"部门：");
				ExcelUtil.createRow(row, 6, null, HSSFCell.CELL_TYPE_STRING,departName);
				sheet.addMergedRegion(new CellRangeAddress(2,2,1,4));
				sheet.addMergedRegion(new CellRangeAddress(2,2,5,8));
				sheet.setColumnWidth(2, 5000);//设置表格宽度
				
				//第5行
				row = sheet.createRow((short) 4);
				ExcelUtil.createRow(row, 0, colstyle, HSSFCell.CELL_TYPE_STRING,"日期");
				ExcelUtil.createRow(row, 1, colstyle, HSSFCell.CELL_TYPE_STRING,"星期");
				ExcelUtil.createRow(row, 2, colstyle, HSSFCell.CELL_TYPE_STRING,"员工编号");
				ExcelUtil.createRow(row, 3, colstyle, HSSFCell.CELL_TYPE_STRING,"员工姓名");
				ExcelUtil.createRow(row, 4, colstyle, HSSFCell.CELL_TYPE_STRING,"时间要求");
				ExcelUtil.createRow(row, 5, colstyle, HSSFCell.CELL_TYPE_STRING,"");
				sheet.addMergedRegion(new CellRangeAddress(4,4,4,5));
				ExcelUtil.createRow(row, 6, colstyle, HSSFCell.CELL_TYPE_STRING,"工作小时数");
				ExcelUtil.createRow(row, 7, colstyle, HSSFCell.CELL_TYPE_STRING,"工作内容描述");
				ExcelUtil.createRow(row, 8, colstyle, HSSFCell.CELL_TYPE_STRING,"备注");
				sheet.setColumnWidth(4, 5000);//设置表格宽度
				
				//值班数据
				for(int i=0;i<list.size();i++){
					row = sheet.createRow((short) (5+i));
					ExcelUtil.createRow(row, 0, colstyle, HSSFCell.CELL_TYPE_STRING,DateUtils.format(list.get(i).getDutyDate(), DateUtils.FORMAT_SHORT));
					ExcelUtil.createRow(row, 1, colstyle, HSSFCell.CELL_TYPE_STRING,list.get(i).getWeekDay());
					ExcelUtil.createRow(row, 2, colstyle, HSSFCell.CELL_TYPE_STRING,list.get(i).getEmployCode());
					ExcelUtil.createRow(row, 3, colstyle, HSSFCell.CELL_TYPE_STRING,list.get(i).getEmployName());
					ExcelUtil.createRow(row, 4, colstyle, HSSFCell.CELL_TYPE_STRING,"From："+DateUtils.format(list.get(i).getStartTime(),"HH:mm"));
					ExcelUtil.createRow(row, 5, colstyle, HSSFCell.CELL_TYPE_STRING,"To："+DateUtils.format(list.get(i).getEndTime(),"HH:mm"));
					ExcelUtil.createRow(row, 6, colstyle, HSSFCell.CELL_TYPE_STRING,list.get(i).getWorkHours());
					ExcelUtil.createRow(row, 7, colstyle, HSSFCell.CELL_TYPE_STRING,list.get(i).getDutyItem());
					ExcelUtil.createRow(row, 8, colstyle, HSSFCell.CELL_TYPE_STRING,list.get(i).getRemark());
					sheet.setColumnWidth(5+i, 5000);//设置表格宽度
				}
				
				row = sheet.createRow((short) (list.size()+6));
				ExcelUtil.createRow(row, 0, null, HSSFCell.CELL_TYPE_STRING,"制表人：");
				ExcelUtil.createRow(row, 4, null, HSSFCell.CELL_TYPE_STRING,"部门负责人批准：");
				ExcelUtil.createRow(row, 5, null, HSSFCell.CELL_TYPE_STRING,classSettingPerson);
				sheet.setColumnWidth(list.size()+6, 5000);//设置表格宽度
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
				ExcelUtil.createRow(row, 0, null, HSSFCell.CELL_TYPE_STRING,"人力资源部批准：");
				ExcelUtil.createRow(row, 1, null, HSSFCell.CELL_TYPE_STRING,(hiActinstList!=null&&hiActinstList.size()>0)?hiActinstList.get(0).getUpdateUser():"");
				sheet.setColumnWidth(list.size()+10, 5000);//设置表格宽度
			} else {
				ExcelUtil.createRow(sheet.createRow(1), 0, null,HSSFCell.CELL_TYPE_STRING, "无数据");
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
		//根据条件删除原先的值班数据
		EmployeeDuty delete = new EmployeeDuty();
		delete.setYear(old.getYear());
		delete.setDepartId(old.getDepartId());
		delete.setVacationName(old.getVacationName());
		delete.setSource(0);
		employeeDutyMapper.deleteByCondition(delete);
		//重新将申请表的数据同步到值班表
		ApplicationEmployeeDutyDetail param = new ApplicationEmployeeDutyDetail();
		param.setAttnApplicationEmployDutyId(old.getId());
		List<ApplicationEmployeeDutyDetail> orgrnial = applicationEmployeeDutyDetailMapper.selectByCondition(param);
		for(ApplicationEmployeeDutyDetail detail:orgrnial){
			for(String employeeId:detail.getEmployeeIds().split(",")){
				//判断值班数据是否存在
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
		//发送消息
		EmployeeDuty dutyP = new EmployeeDuty();
		dutyP.setYear(old.getYear());
		dutyP.setDepartId(old.getDepartId());
		dutyP.setVacationName(old.getVacationName());
		dutyP.setSource(0);
		List<EmployeeDuty> dutyList = employeeDutyMapper.selectByCondition(dutyP);
		for(EmployeeDuty duty:dutyList){
			if(dateMap!=null&&dateMap.containsKey(duty.getEmployId())){
				String date = dateMap.get(duty.getEmployId());
				date = date+"、"+ DateUtils.format(duty.getDutyDate(), DateUtils.FORMAT_MM_DD);
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
		dhMsg.setTitle("值班提醒");
		dhMsg.setContent(employee.getCnName()+"你已被安排于"+dateMap.get(employee.getId())+"进行值班，已可申请当日延时工作请知晓。");
		dhMsg.setReadFlag(EmpMsg.READ_FLAG_NO);
		dhMsg.setCreateTime(new Date());
		empMsgMapper.save(dhMsg);
	}
	
	public void sendMail(Employee employee,Map<Long,String> dateMap){
		StringBuilder sbuilder = new StringBuilder();
        sbuilder.append(employee.getCnName()+"你已被安排于"+dateMap.get(employee.getId())+"进行值班，已可申请当日延时工作请知晓。");
        List<SendMail> sendMailList = new ArrayList<SendMail>();
		SendMail sendMail = new SendMail();
		sendMail.setReceiver(employee.getEmail());
		sendMail.setSubject("值班提醒");
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
			taskVO.setProcessName("值班申请");
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
					og.setHrAuditor(taskInfo.getAssigneeName());	//人事节点
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

		ApplicationEmployeeDuty employeeDuty = applicationEmployeeDutyMapper.getById(id);//申请单
		
		
		
		Map<String,Object> result = new HashMap<String,Object>();//返回结果
		//表头
		List<String> weekDays = new ArrayList<String>();
		weekDays.add("日期");
		weekDays.add("星期");
		weekDays.add("员工编号");
		weekDays.add("员工姓名");
		weekDays.add("时间要求");
		weekDays.add("工作小时数");
		weekDays.add("工作内容简述");
		weekDays.add("备注");
		
		//标题
		result.put("title", employeeDuty.getYear()+"年"+employeeDuty.getVacationName()+"值班安排");
		//申请日期
		result.put("applyDate", DateUtils.format(employeeDuty.getCreateTime(), DateUtils.FORMAT_SHORT));
		//审核状态
		if(employeeDuty!=null&&
				employeeDuty.getApprovalStatus().intValue()==ConfigConstants.DOING_STATUS.intValue()){
			result.put("approvalStatus", "审核中");
		}else if(employeeDuty!=null&&
				employeeDuty.getApprovalStatus().intValue()==ConfigConstants.PASS_STATUS.intValue()){
			result.put("approvalStatus", "已通过");
		}else if(employeeDuty!=null&&
				employeeDuty.getApprovalStatus().intValue()==ConfigConstants.REFUSE_STATUS.intValue()){
			result.put("approvalStatus", "已拒绝");
		}else if(employeeDuty!=null&&
				employeeDuty.getApprovalStatus().intValue()==ConfigConstants.BACK_STATUS.intValue()){
			result.put("approvalStatus", "已撤回");
		}else if(employeeDuty!=null&&
				employeeDuty.getApprovalStatus().intValue()==ConfigConstants.OVERDUE_STATUS.intValue()){
			result.put("approvalStatus", "已失效");
		}else if(employeeDuty!=null&&
				employeeDuty.getApprovalStatus().intValue()==ConfigConstants.OVERDUEPASS_STATUS.intValue()){
			result.put("approvalStatus", "失效同意");
		}else if(employeeDuty!=null&&
				employeeDuty.getApprovalStatus().intValue()==ConfigConstants.OVERDUEREFUSE_STATUS.intValue()){
			result.put("approvalStatus", "失效拒绝");
		}
		//添加公司名称
		Company companyName = companyMapper.getById(employeeDuty.getCompanyId());
		if(companyName!=null){
			result.put("companyName", companyName.getName());
		}
		ApplicationEmployeeDutyDetail dutyDetail=new ApplicationEmployeeDutyDetail();
		dutyDetail.setAttnApplicationEmployDutyId(employeeDuty.getId());
		//根据值班id 查询值班详情
		List<ApplicationEmployeeDutyDetail> selectByCondition = applicationEmployeeDutyDetailMapper.selectByCondition(dutyDetail);
		List<ApplicationEmployeeDutyDetail> dutyMoveList=new ArrayList<ApplicationEmployeeDutyDetail>();
		List<ApplicationEmployeeDutyDetail>  dutyList= new ArrayList<ApplicationEmployeeDutyDetail>();
		for (ApplicationEmployeeDutyDetail applicationEmployeeDutyDetail : selectByCondition) {
			applicationEmployeeDutyDetail.setWeekStr(DateUtils.getWeek(applicationEmployeeDutyDetail.getVacationDate()));
			if(applicationEmployeeDutyDetail.getIsMove()==0){
				dutyList.add(applicationEmployeeDutyDetail);//最终值班
			}else{
				dutyMoveList.add(applicationEmployeeDutyDetail);//值班之前调准的数据
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
		//根据值班id 查询值班详情
		List<ApplicationEmployeeDutyDetail> list = applicationEmployeeDutyDetailMapper.selectByCondition(dutyDetail);
		
		
		//查询值班申请表
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
				// 表头标题样式
				HSSFCellStyle colstyle = workbook.createCellStyle();
				colstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
				colstyle.setBorderBottom(XSSFCellStyle.BORDER_THIN); //下边框
				colstyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);//左边框
				colstyle.setBorderTop(XSSFCellStyle.BORDER_THIN);//上边框
				colstyle.setBorderRight(XSSFCellStyle.BORDER_THIN);//右边框
				
				//第一行
				HSSFRow row = sheet.createRow((short) 0);
				//第一行内容
				HSSFCellStyle colstyle1 = workbook.createCellStyle();
				colstyle1.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
				ExcelUtil.createRow(row, 0, colstyle1, HSSFCell.CELL_TYPE_STRING,list.get(0).getYear()+list.get(0).getVacationName()+"值班安排");
				sheet.addMergedRegion(new CellRangeAddress(0,0,0,6));
				sheet.setColumnWidth(0, 5000);//设置表格宽度
				//第3行
				row = sheet.createRow((short) 2);
				ExcelUtil.createRow(row, 0, null, HSSFCell.CELL_TYPE_STRING,"公司：");
				Company companyName = companyMapper.getById(applyList.get(0).getCompanyId());
				ExcelUtil.createRow(row, 1, null, HSSFCell.CELL_TYPE_STRING,companyName.getName());
				ExcelUtil.createRow(row, 4, null, HSSFCell.CELL_TYPE_STRING,"部门：");
				ExcelUtil.createRow(row, 5, null, HSSFCell.CELL_TYPE_STRING,list.get(0).getDepartName());
				sheet.addMergedRegion(new CellRangeAddress(2,2,1,3));
				sheet.addMergedRegion(new CellRangeAddress(2,2,5,6));
				sheet.setColumnWidth(2, 5000);//设置表格宽度

				//第5行
				row = sheet.createRow((short) 4);
				ExcelUtil.createRow(row, 0, colstyle, HSSFCell.CELL_TYPE_STRING,"日期");
				ExcelUtil.createRow(row, 1, colstyle, HSSFCell.CELL_TYPE_STRING,"星期");
				ExcelUtil.createRow(row, 2, colstyle, HSSFCell.CELL_TYPE_STRING,"员工编号");
				ExcelUtil.createRow(row, 3, colstyle, HSSFCell.CELL_TYPE_STRING,"员工姓名");
				ExcelUtil.createRow(row, 4, colstyle, HSSFCell.CELL_TYPE_STRING,"时间要求");
				ExcelUtil.createRow(row, 5, colstyle, HSSFCell.CELL_TYPE_STRING,"");
				sheet.addMergedRegion(new CellRangeAddress(4,4,4,5));
				ExcelUtil.createRow(row, 6, colstyle, HSSFCell.CELL_TYPE_STRING,"工作小时数");
				ExcelUtil.createRow(row, 7, colstyle, HSSFCell.CELL_TYPE_STRING,"工作内容描述");
				ExcelUtil.createRow(row, 8, colstyle, HSSFCell.CELL_TYPE_STRING,"备注");
				sheet.setColumnWidth(4, 5000);//设置表格宽度
				
				//值班数据
				for(int i=0;i<list.size();i++){
					row = sheet.createRow((short) (5+i));
					if(list.get(i).getIsMove()==0){//只导出 最后的值班数据
						//制表前
						ExcelUtil.createRow(row, 0, colstyle, HSSFCell.CELL_TYPE_STRING,DateUtils.format(list.get(i).getVacationDate(), DateUtils.FORMAT_SHORT));
						ExcelUtil.createRow(row, 1, colstyle, HSSFCell.CELL_TYPE_STRING,"星期"+DateUtils.getWeek(list.get(i).getVacationDate()));						
						ExcelUtil.createRow(row, 2, colstyle, HSSFCell.CELL_TYPE_STRING,list.get(i).getCodes());
						ExcelUtil.createRow(row, 3, colstyle, HSSFCell.CELL_TYPE_STRING,list.get(i).getNames());
						ExcelUtil.createRow(row, 4, colstyle, HSSFCell.CELL_TYPE_STRING,"From："+DateUtils.format(list.get(i).getStartTime(),"HH:mm"));
						ExcelUtil.createRow(row, 5, colstyle, HSSFCell.CELL_TYPE_STRING,"To："+DateUtils.format(list.get(i).getEndTime(),"HH:mm"));
						ExcelUtil.createRow(row, 6, colstyle, HSSFCell.CELL_TYPE_STRING,list.get(i).getWorkHours());
						ExcelUtil.createRow(row, 7, colstyle, HSSFCell.CELL_TYPE_STRING,list.get(i).getDutyItem());
						ExcelUtil.createRow(row, 8, colstyle, HSSFCell.CELL_TYPE_STRING,list.get(i).getRemarks());
					}
				
					sheet.setColumnWidth(5+i, 5000);//设置表格宽度
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
				
				
				ExcelUtil.createRow(row, 0, null, HSSFCell.CELL_TYPE_STRING,"人力资源部批准：");
				
				
				try{
					ViewTaskInfoTbl taskInfo = null;
					taskInfo = viewTaskInfoService.getFirstAuditUser(applyList.get(0).getProcessInstanceId(),ConfigConstants.DUTY_KEY,false);
					if(null != taskInfo){
						 applyList.get(0).setHrAuditor(taskInfo.getAssigneeName());	//人事节点
					}
				}catch(Exception e){
					 applyList.get(0).setHrAuditor(" ");
				}
				
				ExcelUtil.createRow(row, 1, null, HSSFCell.CELL_TYPE_STRING,(applyList.get(0).getHrAuditor()));
				sheet.setColumnWidth(list.size()+6, 5000);//设置表格宽度
				
			} else {
				ExcelUtil.createRow(sheet.createRow(1), 0, null,HSSFCell.CELL_TYPE_STRING, "无数据");
			}
			return workbook;
	}
		return null;
	}
	
	
	/**
	 * 导出值班模板
	 */
	@Override
	public HSSFWorkbook exportScheduleTemplate(Long departId, String year, String vacation) {
		//获取登录人
		User user = userService.getCurrentUser();
		Depart depart = departService.getById(departId);
		//创建工作簿
		HSSFWorkbook workbook = new HSSFWorkbook();
		//表格样式
		HSSFCellStyle colstyle = workbook.createCellStyle();
		colstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
		colstyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直   
		colstyle.setBorderBottom(XSSFCellStyle.BORDER_THIN); //下边框
		colstyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);//左边框
		colstyle.setBorderTop(XSSFCellStyle.BORDER_THIN);//上边框
		colstyle.setBorderRight(XSSFCellStyle.BORDER_THIN);//右边框
		//sheet1
		HSSFSheet sheet1 = workbook.createSheet("值班安排");
        //日期下拉框内容
        //根据节假日和年份查出节假日日期
		List<String> dateList = new ArrayList<String>();
        List<Map<String,Object>> annualVacationList = annualVacationService.getAnnualDateByYearAndSubject(year,vacation);
        for (Map<String, Object> map : annualVacationList) {
        	String dateStr = DateUtils.format((Date)map.get("day"),DateUtils.FORMAT_SHORT);
        	dateList.add(dateStr);
		}
        String[] dateArray = dateList.toArray(new String[dateList.size()]);
        //生成下拉框内容从第4行开始到第500行，可通过下拉框选择值班日期
        ExcelUtil.creatComoboxRow(sheet1, 4, 500, 0, 0, dateArray);
        //时间要求下拉框
        String[] timeArray = {"00:00:00","01:00:00","02:00:00","03:00:00","04:00:00","05:00:00","06:00:00",
        					  "07:00:00","08:00:00","09:00:00","10:00:00","11:00:00","12:00:00","13:00:00",
        					  "14:00:00","15:00:00","16:00:00","17:00:00","18:00:00","19:00:00","20:00:00",
        					  "21:00:00","22:00:00","23:00:00","24:00:00"};
        ExcelUtil.creatComoboxRow(sheet1, 4, 500, 3, 3, timeArray);
        ExcelUtil.creatComoboxRow(sheet1, 4, 500, 4, 4, timeArray);
		//设置表格宽度
		sheet1.setColumnWidth(0, 5000);
		sheet1.setColumnWidth(1, 5000);
		sheet1.setColumnWidth(2, 5000);
		sheet1.setColumnWidth(3, 2500);
		sheet1.setColumnWidth(4, 2500);
		sheet1.setColumnWidth(5, 10000);
		sheet1.setColumnWidth(6, 6000);
		//建立第一行表头
		HSSFRow row = sheet1.createRow((short) 0);
		String title1 = year + "年"+ vacation +"值班安排 ";
		ExcelUtil.createRow(row, 0, colstyle, HSSFCell.CELL_TYPE_STRING, title1);
		//建立第二行表头
		HSSFRow row2 = sheet1.createRow((short) 1);
		String companyName = "公司:" + user.getCompany().getName();
		String departName = "部门:" + depart.getName();
		ExcelUtil.createRow(row2, 0, colstyle, HSSFCell.CELL_TYPE_STRING, companyName);
		ExcelUtil.createRow(row2, 3, colstyle, HSSFCell.CELL_TYPE_STRING, departName);
		//建立第三行表头
		HSSFRow row3 = sheet1.createRow((short) 2);
		ExcelUtil.createRow(row3, 0, colstyle, HSSFCell.CELL_TYPE_STRING, "日期");
		ExcelUtil.createRow(row3, 1, colstyle, HSSFCell.CELL_TYPE_STRING, "员工编号");
		ExcelUtil.createRow(row3, 2, colstyle, HSSFCell.CELL_TYPE_STRING, "员工姓名");
		ExcelUtil.createRow(row3, 3, colstyle, HSSFCell.CELL_TYPE_STRING, "时间要求");
		ExcelUtil.createRow(row3, 5, colstyle, HSSFCell.CELL_TYPE_STRING, "工作内容简述");
		ExcelUtil.createRow(row3, 6, colstyle, HSSFCell.CELL_TYPE_STRING, "备注");
		//建立第四行表头
		HSSFRow row4 = sheet1.createRow((short) 3);
		ExcelUtil.createRow(row4, 3, colstyle, HSSFCell.CELL_TYPE_STRING, "From");
		ExcelUtil.createRow(row4, 4, colstyle, HSSFCell.CELL_TYPE_STRING, "To");
		//合并单元格
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
		//设置合并单元格边框样式
		// 下边框
		RegionUtil.setBorderBottom(1, cra1, sheet1,workbook);
		RegionUtil.setBorderBottom(1, cra2, sheet1,workbook);
		RegionUtil.setBorderBottom(1, cra3, sheet1,workbook);
		RegionUtil.setBorderBottom(1, cra4, sheet1,workbook);
		RegionUtil.setBorderBottom(1, cra5, sheet1,workbook);
		RegionUtil.setBorderBottom(1, cra6, sheet1,workbook);
		RegionUtil.setBorderBottom(1, cra7, sheet1,workbook);
		RegionUtil.setBorderBottom(1, cra8, sheet1,workbook);
		RegionUtil.setBorderBottom(1, cra9, sheet1,workbook);
		// 右边框
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
		// 获取当前登录人
		User user = userService.getCurrentUser();
		Map<String,Object> result = new HashMap<String,Object>();
		if(departId==null){
			logger.error("参数错误！");
			throw new OaException("请选择部门！");
		}
		if(year==null){
			logger.error("参数错误！");
			throw new OaException("请选择年份！");
		}
		if(vacation==null){
			logger.error("参数错误！");
			throw new OaException("请选择节假日！");
		}
		Depart depart = departMapper.getById(departId);
		Long unCommitDutyId = getUnCommitAndEditableDutyId(departId,user,year,vacation);
		String title = year +"年"+ vacation + "值班安排";
		result.put("title", title);
		result.put("company", user.getCompany().getName());
		result.put("depart",depart.getName());
		result.put("approvalStatus", "未提交");
		if(unCommitDutyId != null){
			//根据申请单id查询申请单详情
			ApplicationEmployeeDutyDetail dutyDetail = new ApplicationEmployeeDutyDetail();
			dutyDetail.setAttnApplicationEmployDutyId(unCommitDutyId);
			List<ApplicationEmployeeDutyDetail> applicationEmployeeDutyDetailList = applicationEmployeeDutyDetailMapper.selectByCondition(dutyDetail);
			//设置展示字段
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
		
		//查询值班
		//根据条件查询值班申请（如果查不到直接统计最终值班记录）
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
			//值班部门
			duty.put("departName",applyList.get(0).getDepartName());
			//部门id
			duty.put("departId",applyList.get(0).getDepartId());
			//年份
			duty.put("year",applyList.get(0).getYear());
			//法定节假日
			duty.put("vacationName",applyList.get(0).getVacationName());
			//排班人
			duty.put("classSetPerson",applyList.get(0).getClassSettingPerson());
			//申请日期
			duty.put("applyDate",DateUtils.format(applyList.get(0).getCreateTime(), DateUtils.FORMAT_SHORT));
			//值班人数（统计出最终表的人数就好了）
			List<EmployeeDuty> dutyNumList = employeeDutyMapper.getDutyNum(dutyNumparam);
			duty.put("dutyNum",0);
			if(dutyNumList!=null&&dutyNumList.size()>0){
				duty.put("dutyNum",dutyNumList.size());
			}
			//值班批核人
			duty.put("auditor", "");
			if(StringUtils.isNotBlank(applyList.get(0).getProcessInstanceId())){
				List<ViewTaskInfoTbl> updateList = viewTaskInfoService.queryTasksByProcessId(applyList.get(0).getProcessInstanceId());
				if(updateList!=null&&updateList.size()>=2){
					duty.put("auditor", updateList.get(0).getAssigneeName());
				}
			}
		}else{
			//直接统计值班最终数据
			//部门id
			duty.put("departId",departId);
			//值班部门
			duty.put("departName","");
			Depart depart = departMapper.getById(departId);
			duty.put("departName",depart!=null?depart.getName():"");
			//年份
			duty.put("year",year);
			//法定节假日
			duty.put("vacationName",vacationName);
			//排班人
			duty.put("classSetPerson","");
			//申请日期
			duty.put("applyDate","");
			//值班人数（统计出最终表的人数就好了）
			List<EmployeeDuty> dutyNumList = employeeDutyMapper.getDutyNum(dutyNumparam);
			duty.put("dutyNum",0);
			//值班批核人
			duty.put("auditor", "");
			if(dutyNumList!=null&&dutyNumList.size()>0){
				duty.put("dutyNum",dutyNumList.size());
				duty.put("classSetPerson",dutyNumList.get(0).getCreateUser());
				duty.put("applyDate",DateUtils.format(dutyNumList.get(0).getCreateTime(), DateUtils.FORMAT_SHORT));
				duty.put("auditor", dutyNumList.get(0).getCreateUser());
				result.put("duty", duty);
			}else{
				throw new OaException("未查到值班记录！");
			}
		}
		
		//查询流水
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
		//列表信息
		List<EmployeeDuty> list = employeeDutyMapper.getDutyDetail(param);
		for(EmployeeDuty data:list){
			data.setWeekDay("星期"+DateUtils.getWeek(data.getDutyDate()));
		}
		result.put("list", list);
		//公司名称
		result.put("companyName", "");
		//部门名称
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
	    //标题
		result.put("title", year+"年"+vacationName+"值班安排");
		result.put("success",true);
		return result;
	}
	/**
	 * 保存值班申请单
	 * @throws OaException 
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveApplicationDutyDetail(Long departId, String year, String vacation,List<ApplicationEmployeeDutyDetail> detailList) throws OaException {
		logger.info("-------保存值班申请单开始------");
		if(departId == null){
			logger.error("参数错误！");
			throw new OaException("请选择值班部门！");
		}
		if(year == null){
			logger.error("参数错误！");
			throw new OaException("请选择年份！");
		}
		if(vacation == null){
			logger.error("参数错误！");
			throw new OaException("请选择法定节假日！");
		}
		
		// 获取当前登录人
		User user = userService.getCurrentUser();
		Depart depart = departService.getById(departId);
		//获取值班安排中所有员工(去重)
		Set<String> empSet = new HashSet<String>();
		if(detailList != null && detailList.size() > 0){
			for (ApplicationEmployeeDutyDetail applicationEmployeeDutyDetail : detailList) {
				empSet.add(applicationEmployeeDutyDetail.getEmployeeIds());
			}
		}
		//将所有员工拼接一个字符串
		String empIds = StringUtils.join(empSet, ",");
		//判断是否存在未提交的可编辑的值班申请单
		Long unCommitAndEditableDutyId = getUnCommitAndEditableDutyId(departId,user,year,vacation);
		//新建申请单
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
			//修改申请单
			ApplicationEmployeeDuty applicationEmployeeDuty = new ApplicationEmployeeDuty();
			applicationEmployeeDuty.setId(unCommitAndEditableDutyId);
			applicationEmployeeDuty.setDutyNum(empSet.size());
			applicationEmployeeDuty.setEmployeeIds(empIds);
			applicationEmployeeDuty.setClassSettingPerson(user.getEmployee().getCnName());
			applicationEmployeeDuty.setUpdateTime(new Date());
			applicationEmployeeDuty.setUpdateUser(user.getEmployee().getId().toString());
			applicationEmployeeDutyMapper.updateById(applicationEmployeeDuty);
			//将原有的申请单详情数据标记删除
			ApplicationEmployeeDutyDetail dutyDetail = new ApplicationEmployeeDutyDetail();
			dutyDetail.setAttnApplicationEmployDutyId(unCommitAndEditableDutyId);
			applicationEmployeeDutyDetailMapper.deleteByDutyId(dutyDetail);
		}
		//保存申请单详情
		if(detailList != null && detailList.size() > 0){
			//根据节假日和年份查出节假日所有日期
			List<Date> dateList = new ArrayList<Date>();
			List<Map<String,Object>> annualVacationList = annualVacationService.getAnnualDateByYearAndSubject(year,vacation);
			for (Map<String, Object> map : annualVacationList) {
				dateList.add((Date)map.get("day"));
			}
			//查询部门下可值班的所有员工
			Employee employee = new Employee();
			employee.setDepartId(departId);
			List<Employee> empList = employeeMapper.getPageList(employee);
			List<String> empIdList = new ArrayList<String>();
			for (Employee emp : empList) {
				empIdList.add(emp.getId().toString());
			}
			for (ApplicationEmployeeDutyDetail applicationEmployeeDutyDetail : detailList) {
				//判断节假日中是否包含该日期
		        if(!dateList.contains(applicationEmployeeDutyDetail.getVacationDate())){
		        	logger.error("所选日期不在法定节假日中！");
					throw new OaException("所选日期不在法定节假日中！");
		        }
				Date startTime = applicationEmployeeDutyDetail.getStartTime();
				Date endTime = applicationEmployeeDutyDetail.getEndTime();
				Long intervalHours = DateUtils.getIntervalHours(startTime, endTime);
				//工作时间满5减1满10减2
				if(5 <intervalHours && intervalHours<10  ){
					intervalHours -= 1;
				}
				if(intervalHours >= 10  ){
					intervalHours -= 2;
				}
				applicationEmployeeDutyDetail.setWorkHours(intervalHours.doubleValue() > 0 ? intervalHours.doubleValue() : 0);
//				if(applicationEmployeeDutyDetail.getWorkHours().equals(0)){
//					logger.error("开始时间需大于结束时间！");
//					throw new OaException("开始时间需大于结束时间");
//				}
				//值班员工
				Employee dutyEmp = employeeMapper.getById(Long.parseLong(applicationEmployeeDutyDetail.getEmployeeIds()));
				//判断员工是否在该部门下
				if(!empIdList.contains(applicationEmployeeDutyDetail.getEmployeeIds())){
					logger.error("所选员工不在可值班人选中！");
					throw new OaException("请选择该部门下员工！");
				}
				//判断值班表中是否有排班申请(source = 1)的数据
				EmployeeDuty empDuty = new EmployeeDuty();
				empDuty.setEmployId(Long.parseLong(applicationEmployeeDutyDetail.getEmployeeIds()));
				empDuty.setDutyDate(applicationEmployeeDutyDetail.getVacationDate());
				empDuty.setSource(1);
				List<EmployeeDuty> existDutyList = employeeDutyMapper.getDutyListBySource(empDuty);
				if(existDutyList != null && existDutyList.size() > 0){
					String dutyDate = DateUtils.format(applicationEmployeeDutyDetail.getVacationDate(),DateUtils.FORMAT_SHORT);
					logger.error(dutyEmp.getCnName()+"在"+dutyDate+"已存在排班");
					throw new OaException(dutyEmp.getCnName()+"在"+dutyDate+"已存在排班");
				}
				applicationEmployeeDutyDetail.setAttnApplicationEmployDutyId(unCommitAndEditableDutyId);
				applicationEmployeeDutyDetail.setCreateTime(new Date());
				applicationEmployeeDutyDetail.setDelFlag(0);
				applicationEmployeeDutyDetail.setIsMove(0);
			}
			applicationEmployeeDutyDetailMapper.batchSave(detailList);
		}
		logger.info("-------保存值班申请单结束------");
	}
	
	/**
	 * 查询可编辑的值班申请单
	 * @param departId
	 * @param year
	 * @param vacation
	 * @return
	 * @throws OaException
	 */
	private Long getUnCommitAndEditableDutyId(Long departId,User user,String year,String vacation) throws OaException {
		//未提交的申请单id
		Long unCommitDutyId = null;
		//判断是否有排班权限，用户是否为部门负责人
		List<Depart> departList = departService.getAllDepartByLeaderId(user.getEmployee().getId());
		//departList不为空则用户为部门负责人，同时判断该部门是否由该部门负责人排班
//		if(departList != null && departList.size() > 0){
//			List<Long> departIdList = new ArrayList<Long>();
//			for (Depart depart : departList) {
//				departIdList.add(depart.getId());
//			}
//			if(!departIdList.contains(departId)){
//				logger.error("没有排班权限！");
//				throw new OaException("没有排班权限！");
//			}
//		}
		//人事可排所有部门
		//判断该节假日是否存在值班
		EmployeeDuty empDuty = new EmployeeDuty();
		empDuty.setDepartId(departId);
		empDuty.setYear(year);
		empDuty.setVacationName(vacation);
		List<EmployeeDuty> empDutyList = employeeDutyMapper.getExistDutyListByCondition(empDuty);
		if(empDutyList != null && empDutyList.size() > 0){
			logger.error("该节假日已存在值班安排！");
			throw new OaException("该节假日已存在值班安排！");
		}
		//查询所有申请单
		ApplicationEmployeeDuty condition = new ApplicationEmployeeDuty();
		condition.setYear(year);
		condition.setVacationName(vacation);
		condition.setDepartId(departId);
		condition.setType(0);
		List<ApplicationEmployeeDuty> applicationEmployeeDutyList = applicationEmployeeDutyMapper.getByCondition(condition);
		if(applicationEmployeeDutyList != null && applicationEmployeeDutyList.size() > 0){
			for (ApplicationEmployeeDuty applicationEmployeeDuty : applicationEmployeeDutyList) {
				//判断是否有未提交的申请单，记录申请单id
				if(applicationEmployeeDuty.getApprovalStatus() == null){
					unCommitDutyId = applicationEmployeeDuty.getId();
				}else if(applicationEmployeeDuty.getApprovalStatus().equals(ConfigConstants.DOING_STATUS) || 
						applicationEmployeeDuty.getApprovalStatus().equals(ConfigConstants.PASS_STATUS) ||
						applicationEmployeeDuty.getApprovalStatus().equals(ConfigConstants.OVERDUE_STATUS) ||
						applicationEmployeeDuty.getApprovalStatus().equals(ConfigConstants.OVERDUEPASS_STATUS)){
					logger.error("值班表已提交无法再次编辑或上传，如有调整请联系人事！");
					throw new OaException("值班表已提交无法再次编辑或上传，如有调整请联系人事！");
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
		logger.info("值班申请commitDutyByHr入参:departId="+departId+";year="+year+";vacationName="+vacationName
				+";info="+info+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));
		
		Map<String, Object> result = new HashMap<String, Object>();
		if(StringUtils.isBlank(departId)){
			result.put("success", false);
			result.put("message", "部门不能为空！");
			return result;
		}
		if(StringUtils.isBlank(year)){
			result.put("success", false);
			result.put("message", "年份不能为空！");
			return result;
		}
		if(StringUtils.isBlank(vacationName)){
			result.put("success", false);
			result.put("message", "法定节假日不能为空！");
			return result;
		}
		
		Depart depart = departMapper.getById(Long.valueOf(departId));
		//值班历史明细（包括调整前与调整后）
		List<ApplicationEmployeeDutyDetail> dutyHistoryDetail = new ArrayList<ApplicationEmployeeDutyDetail>();
		
		//查询老的历史数据
		EmployeeDuty param = new EmployeeDuty();
		param.setDepartId(Long.valueOf(departId));
		param.setYear(year);
		param.setVacationName(vacationName);
		List<EmployeeDuty> oldDutyList = employeeDutyMapper.getDutyDetail(param);
		
		//生成历史记录
		ApplicationEmployeeDuty dutyHistory = new ApplicationEmployeeDuty();
		dutyHistory.setCompanyId(user.getCompanyId());//公司
		dutyHistory.setDepartId(Long.valueOf(departId));//值班部门
		dutyHistory.setDepartName(depart!=null?depart.getName():"");//值班部门
		dutyHistory.setYear(year);//年份
		dutyHistory.setVacationName(vacationName);//法定节假日
		dutyHistory.setCreateTime(new Date());//生成时间
		dutyHistory.setType(2);//值班类型
		dutyHistory.setCreateUser(user.getEmployee().getCnName());//值班调班人
		dutyHistory.setApprovalStatus(200);
		dutyHistory.setApprovalReason("值班人事调班");
		dutyHistory.setDelFlag(0);
		dutyHistory.setVersion(0L);
		applicationEmployeeDutyMapper.save(dutyHistory);
		
		JSONArray infoArray = JSONArray.fromObject(info);
		List<EmployeeDuty> newDutyList = new ArrayList<EmployeeDuty>();//调整后值班记录
		
		for(int i=0;i<infoArray.size();i++){
			//最新值班记录
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
			duty.setSource(2);//人事直接调班
			duty.setRemark(dutyObject.getString("remark"));
			newDutyList.add(duty);
			//调整后历史明细记录
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
		//调整后历史增加-排班值班 部分
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
		//调整前值班历史记录
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
		//生成值班历史明细记录
		applicationEmployeeDutyDetailMapper.batchSave(dutyHistoryDetail);
		//删除老的值班数据
		List<Integer> sourceList = new ArrayList<Integer>();
		sourceList.add(0);//值班申请
		sourceList.add(2);//hr直接调班
		param.setSourceList(sourceList);
		param.setUpdateTime(new Date());
		param.setUpdateUser(user.getEmployee().getCnName());
		employeeDutyMapper.deleteOldDuty(param);
		//生成最新值班数据
		employeeDutyMapper.batchSave(newDutyList);
		result.put("success", true);
		result.put("message", "修改成功！");
		return result;
	}

	@Override
	public Map<String, Object> showHistoryDetail(Long id) {
		Map<String,Object> result = new HashMap<String,Object>();
		if(id==null){
			result.put("success", false);
			result.put("message", "参数有误！");
			return result;
		}
		ApplicationEmployeeDuty duty = applicationEmployeeDutyMapper.getById(id);
		if(duty==null){
			result.put("success", false);
			result.put("message", "参数有误,未查到流水记录！");
			return result;
		}
		result.put("title", duty.getYear()+"年"+duty.getVacationName()+"值班安排（更新）");
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
			data.setWeekStr("星期"+DateUtils.getWeek(data.getVacationDate()));
		}
		result.put("success", true);
		result.put("list", list);
		return result;
	}
	
	/**
	 * 提交值班
	 * @throws OaException 
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void commitDuty(HttpServletRequest request,Long departId, String year, String vacation) throws OaException {
			
		User user= userService.getCurrentUser();
		logger.info("commitDuty入参:departId="+departId+";year="+year+";vacation="+vacation
			+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));
		
		if(departId == null || year == null || vacation == null){
			logger.error("参数缺失！");
			throw new OaException("参数缺失！");
		}
		//查询该部门下个月是否有未提交审核的排班申请数据
		Long unCommitDutyId = getUnCommitAndEditableDutyId(departId, user, year, vacation);
		if(unCommitDutyId == null){
			logger.error("没有可提交的值班申请！");
			throw new OaException("没有可提交的值班申请！");
		}
		//获得节假日开始日期
		//判断登录人部门，如果不是人力资源行政部则增加时间限制
		if(!"107".equals(user.getDepart().getCode())){
			List<Map<String,Object>> dateList = annualVacationService.getAnnualDateByYearAndSubject(year,vacation);
			Date annualStartDate = (Date) dateList.get(0).get("day");
			//节假日前四个工作日
			Config configCondition = new Config();
			configCondition.setCode("dutyCommitTimeLimit");
			List<Config> limit = configService.getListByCondition(configCondition);
			int num = Integer.valueOf(limit.get(0).getDisplayCode());
			Date lastSubDate = annualVacationService.getWorkingDayPre(num,annualStartDate);
			if(DateUtils.getIntervalDays(new Date(),lastSubDate)<0){
				logger.error("法定节假日安排最晚请于节假日前第4个工作日提交！");
				throw new OaException("法定节假日安排最晚请于节假日前第4个工作日提交！");
			}
		};
		
		ApplicationEmployeeDutyDetail dutyDetail = new ApplicationEmployeeDutyDetail();
		dutyDetail.setAttnApplicationEmployDutyId(unCommitDutyId);
		List<ApplicationEmployeeDutyDetail> detailList = applicationEmployeeDutyDetailMapper.selectByCondition(dutyDetail);
		if(detailList==null||detailList.size()<=0){
			logger.error("尚未编辑值班！");
			throw new OaException("尚未编辑值班！");
		}
		
		//启动流程
		String processInstanceId = activitiServiceImpl.startByKey(ConfigConstants.DUTY_KEY);
		
		//修改排班状态
		ApplicationEmployeeDuty empDuty = new ApplicationEmployeeDuty();
		empDuty.setId(unCommitDutyId);
		empDuty.setSubmitDate(new Date());
		empDuty.setProcessInstanceId(processInstanceId);
		empDuty.setClassSettingPerson(user.getEmployee().getCnName());
		empDuty.setApprovalStatus(ConfigConstants.DOING_STATUS);
		empDuty.setUpdateUser(user.getEmployee().getCnName());
		empDuty.setUpdateTime(new Date());
		//修改排班状态
		int count = applicationEmployeeDutyMapper.updateById(empDuty);
		if(count<=0){
			logger.error("系统异常，请刷新重试！");
			throw new OaException("系统异常，请刷新重试！");
		}
		
		//-----------------start-----------------------保存流程节点信息
		ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
		flow.setAssigneeName(user.getEmployee().getCnName());
		flow.setDepartName(user.getDepart().getName());
		flow.setPositionName(user.getPosition().getPositionName());
		flow.setFinishTime(new Date());
		flow.setComment("值班申请");
		flow.setProcessId(processInstanceId);
		flow.setProcessKey(ConfigConstants.DUTY_KEY);
		flow.setStatu(0);
		viewTaskInfoService.save(flow);
		
		logger.info("-------------提交值班结束--------------");
	}
	
	/**
	 * 查询值班记录
	 */
	@Override
	public PageModel<ApplicationEmployeeDuty> getDutyRecord(ApplicationEmployeeDuty requestParam) {
		//获得登录人
		User user = userService.getCurrentUser();
		PageModel<ApplicationEmployeeDuty> pm = new PageModel<ApplicationEmployeeDuty>();
		int page = requestParam.getPage() == null ? 0 : requestParam.getPage();
		int rows = requestParam.getRows() == null ? 0 : requestParam.getRows();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		requestParam.setLimit(pm.getLimit());
		requestParam.setOffset(pm.getOffset());	
		//如果查询条件中未选择部门，则判断登录人是否为部门负责人，部门负责人只能查看负责部门下的值班记录
		if(requestParam.getDepartId() == null){
			//查看登录人是否为部门负责人
			List<Depart> departList = departService.getAllDepartByLeaderId(user.getEmployee().getId());
			if(departList != null && departList.size() > 0){
				//部门负责人
				List<Long> departIds = new ArrayList<Long>();
				for (Depart depart : departList) {
					departIds.add(depart.getId());
				}
				//设置可查询部门
				requestParam.setDepartIds(departIds);
			}
			//人事可查所有不设置该条件
		}
		requestParam.setType(0);
		List<Integer> approvalStatusList = new ArrayList<Integer>();
		approvalStatusList.add(ConfigConstants.DOING_STATUS);//审批中
		approvalStatusList.add(ConfigConstants.PASS_STATUS);//通过
		approvalStatusList.add(ConfigConstants.REFUSE_STATUS);//拒绝
		approvalStatusList.add(ConfigConstants.BACK_STATUS);//撤销
		approvalStatusList.add(ConfigConstants.OVERDUE_STATUS);//失效
		approvalStatusList.add(ConfigConstants.OVERDUEPASS_STATUS);//失效同意
		approvalStatusList.add(ConfigConstants.OVERDUEREFUSE_STATUS);//失效拒绝
		requestParam.setApprovalStatusList(approvalStatusList);
		List<ApplicationEmployeeDuty> list = applicationEmployeeDutyMapper.getByCondition(requestParam);
		//查询人事审核人
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
		// 获取当前登录人
		User user = userService.getCurrentUser();
		Map<String,Object> result = new HashMap<String,Object>();
		if(id==null){
			logger.error("参数错误！");
			throw new OaException("请选择值班申请单！");
		}
		ApplicationEmployeeDuty applicationEmployeeDuty = applicationEmployeeDutyMapper.getById(id);
		String title = applicationEmployeeDuty.getYear() +"年"+ applicationEmployeeDuty.getVacationName() + "值班安排";
		result.put("title", title);
		result.put("companyName", user.getCompany().getName());
		result.put("departName",applicationEmployeeDuty.getDepartName());
		result.put("approvalStatus", applicationEmployeeDuty.getApprovalStatus());
		//根据申请单id查询申请单详情
		ApplicationEmployeeDutyDetail dutyDetail = new ApplicationEmployeeDutyDetail();
		dutyDetail.setAttnApplicationEmployDutyId(id);
		List<ApplicationEmployeeDutyDetail> applicationEmployeeDutyDetailList = applicationEmployeeDutyDetailMapper.selectByCondition(dutyDetail);
		//设置展示字段
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
		logger.info("导入值班-----开始");
		// 获取当前登录人
		User user = userService.getCurrentUser();
		//返回结果
		Map<String, String> resultMap = new HashMap<String, String>();
		// 判断文件是否存在
		if (null == file) {
			logger.error("文件不存在！");
			throw new OaException("文件不存在！");
		}
		// 获得文件名
		String fileName = file.getOriginalFilename();
		/**1.判断文件是否是excel文件*/
		if (!fileName.endsWith("xls") && !fileName.endsWith("xlsx")) {
			logger.error(fileName + "不是excel文件");
			throw new OaException("请上传标准表格文件！");
		}
		List<ApplicationEmployeeDutyDetail> detailList = new LinkedList<ApplicationEmployeeDutyDetail>();
		// 读取文件转换为实体类
		List<List<Object>> excelList = ExcelUtil.readExcel(file, fileName, 6);
		if(departId == null){
			logger.error("请选择所需安排值班部门！");
			throw new OaException("请选择所需安排值班部门！");
		}
		Depart checkDepart = departMapper.getById(departId);
		//检查上传部门是否为所选部门
		String importDepartName =  (String) excelList.get(1).get(3);
		importDepartName = importDepartName.split(":")[1];
		if(importDepartName == null || !checkDepart.getName().equals(importDepartName)){
			logger.error("上传值班表部门与所选部门不符，请确认所需上传部门选择值班部门！");
			throw new OaException("上传值班表部门与所选部门不符，请确认所需上传部门选择值班部门！");
		}
		//查看表中有无值班数据
		if(excelList.size() < 5){
			logger.error("上传值班表中无数据，请确认是否已安排值班！");
			throw new OaException("上传值班表中无数据，请确认是否已安排值班！");

		}
		//查询所有日期
		for(int i = 4; i < excelList.size(); i++){
			ApplicationEmployeeDutyDetail applicationEmployeeDutyDetail = new ApplicationEmployeeDutyDetail();
			//从第五行开始，每一行数据为一条记录
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
			//查询部门下可排值班的所有员工
			Employee employee = new Employee();
			employee.setDepartId(departId);
			List<Employee> empList = employeeMapper.getPageList(employee);
			List<String> empIdList = new ArrayList<String>();
			for (Employee emp : empList) {
				empIdList.add(emp.getId().toString());
			}
			if(empId == null || !empIdList.contains(empId)){
				logger.error(empName+"未在所上传值班部门下，请确认值班表中员工信息！");
				throw new OaException(empName+"未在所上传值班部门下，请确认值班表中员工信息！");
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
		resultMap.put("resultMsg", "导入成功！");
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
					og.setHrAuditor(taskInfo.getAssigneeName());	//人事节点
				}
			}catch(Exception e){
				og.setHrAuditor(" ");
			}
		}
		
		pm.setRows(list);
		return pm;
	}
}
