package com.ule.oa.base.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.flowable.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ule.oa.base.mapper.EmpApplicationOutgoingMapper;
import com.ule.oa.base.mapper.RabcUserMapper;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.EmpApplicationOutgoing;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.po.RunTask;
import com.ule.oa.base.po.User;
import com.ule.oa.base.po.tbl.ViewTaskInfoTbl;
import com.ule.oa.base.service.AnnualVacationService;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmpApplicationOutgoingService;
import com.ule.oa.base.service.EmployeeClassService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.SendMailService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.service.ViewTaskInfoService;
import com.ule.oa.base.util.AttnStatisticsUtil;
import com.ule.oa.base.vo.TaskVO;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.ExcelUtil;
import com.ule.oa.common.utils.PageModel;
import com.ule.oa.common.utils.http.IPUtils;

/**
 * @ClassName: 外出申请
 * @Description: 外出申请
 * @author yangjie
 * @date 2017年6月9日
 */
@Service
public class EmpApplicationOutgoingServiceImpl implements EmpApplicationOutgoingService {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private EmpApplicationOutgoingMapper empApplicationOutgoingMapper;
	@Autowired
	private UserService userService;
	@Autowired
	private DepartService departService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private ActivitiServiceImpl activitiServiceImpl;
	@Autowired
	private ViewTaskInfoService viewTaskInfoService;
	@Autowired
	private AnnualVacationService annualVacationService;
	@Autowired
	private SendMailService	sendMailService;
	@Autowired
	private EmployeeClassService employeeClassService;
	@Autowired
	private RabcUserMapper rabcUserMapper;


	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String,Object> save(EmpApplicationOutgoing userOutgoing) throws Exception{
		Map<String,Object> map = new HashMap<String, Object>();
		//外出流程开始
		String processInstanceId = activitiServiceImpl.startByKey(ConfigConstants.OUTGOING_KEY);
		
		userOutgoing.setProcessInstanceId(processInstanceId);
		empApplicationOutgoingMapper.save(userOutgoing);
		//-----------------start-----------------------保存流程节点信息
		ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
		flow.setAssigneeName(userOutgoing.getCnName());
		flow.setDepartName(userOutgoing.getDepartName());
		flow.setPositionName(userOutgoing.getPositionName());
		flow.setFinishTime(userOutgoing.getSubmitDate());
		flow.setComment(userOutgoing.getReason());
		flow.setProcessId(processInstanceId);
		flow.setProcessKey(ConfigConstants.OUTGOING_KEY);
		flow.setStatu(0);
		viewTaskInfoService.save(flow);
		map.put("success", true);
		return map;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String,Object> updateById(EmpApplicationOutgoing userOutgoing) throws Exception{
		Map<String,Object> map = new HashMap<String, Object>();
		User user = userService.getCurrentUser();
		if(userOutgoing.getApprovalStatus() != null && (userOutgoing.getApprovalStatus() != EmpApplicationOutgoing.APPROVAL_STATUS_YES || userOutgoing.getApprovalStatus().intValue() != ConfigConstants.OVERDUEPASS_STATUS)) {
			empApplicationOutgoingMapper.updateById(userOutgoing);
		}
		if(userOutgoing.getApprovalStatus().intValue() == EmpApplicationOutgoing.APPROVAL_STATUS_YES || userOutgoing.getApprovalStatus().intValue() == ConfigConstants.OVERDUEPASS_STATUS) {
				if(userOutgoing.getApprovalStatus() != null && (userOutgoing.getApprovalStatus().intValue() == EmpApplicationOutgoing.APPROVAL_STATUS_YES || userOutgoing.getApprovalStatus().intValue() == ConfigConstants.OVERDUEPASS_STATUS)) {
					empApplicationOutgoingMapper.updateById(userOutgoing);
				}
				//充工时
				EmpApplicationOutgoing old = empApplicationOutgoingMapper.getById(userOutgoing.getId());
				Employee employee = employeeService.getById(old.getEmployeeId());
				AttnStatisticsUtil attnStatisticsUtil= new AttnStatisticsUtil();
				/****如果班次是9-18点且外出的结束时间为12点-13点，则将外出的结束时间设为13点****/
				if(userOutgoing.getEmployeeId() != null && userOutgoing.getOutDate() != null){
					EmployeeClass employeeClass = new EmployeeClass();
					employeeClass.setEmployId(userOutgoing.getEmployeeId());
					employeeClass.setClassDate(userOutgoing.getOutDate());
					//查询该员工当天的班次
					EmployeeClass empClass = employeeClassService.getEmployeeClassSetting(employeeClass);
					if(empClass != null && empClass.getStartTime() != null && empClass.getEndTime() != null){
						String startHour = DateUtils.format(empClass.getStartTime(), "HH");
						String endHour = DateUtils.format(empClass.getEndTime(), "HH");
						String oldEndTime = DateUtils.format(old.getEndTime(), "HH");
						if("09".equals(startHour) && "18".equals(endHour) && "12".equals(oldEndTime)){
							String endDate = DateUtils.format(old.getEndTime(), DateUtils.FORMAT_SHORT);
							String endTimeStr = endDate + " 13:00:00";
							old.setEndTime(DateUtils.parse(endTimeStr));
						}
					}
				}
				/****如果班次是9-18点且外出的结束时间为12点-13点，则将外出的结束时间设为13点****/

				attnStatisticsUtil.calWorkHours(employee.getCompanyId(), employee.getId(), 
						user.getEmployee().getCnName(), old.getOutDate(), old.getDuration(), RunTask.RUN_CODE_70,old.getStartTime(),old.getEndTime(),null,old.getReason(),old.getId());
		}
		map.put("success", true);
		return map;
	}

	@Override
	public EmpApplicationOutgoing getById(Long id) {
		return empApplicationOutgoingMapper.getById(id);
	}
	
	public List<EmpApplicationOutgoing> getReportList(EmpApplicationOutgoing empApplicationOutgoing){
		List<EmpApplicationOutgoing> ogs = empApplicationOutgoingMapper.getReportPageList(empApplicationOutgoing);
		
		//设置第一级审批人
		ogs.forEach((EmpApplicationOutgoing og) -> {
			try{
				ViewTaskInfoTbl taskInfo = null;
				taskInfo = viewTaskInfoService.getFirstAuditUser(og.getProcessInstanceId(),ConfigConstants.OUTGOING_KEY,true);
				og.setAuditUser("");
				if(null != taskInfo){
					og.setAuditUser(taskInfo.getAssigneeName());
				}
			}catch(Exception e){
				og.setAuditUser(" ");
				logger.error("单据{}获取第一级审批人出错",og.getId());
			}
		});
		
		return ogs;
	}

	/**
	  * getReportPageList(分页查询外出查询报表)
	  * @Title: getReportPageList
	  * @Description: 分页查询外出查询报表
	  * @param empApplicationOutgoing
	  * @return    设定文件
	  * PageModel<EmpApplicationOutgoing>    返回类型
	  * @throws
	 */
	public PageModel<EmpApplicationOutgoing> getReportPageList(EmpApplicationOutgoing empApplicationOutgoing){
		int page = empApplicationOutgoing.getPage() == null ? 0 : empApplicationOutgoing.getPage();
		int rows = empApplicationOutgoing.getRows() == null ? 0 : empApplicationOutgoing.getRows();
		
		PageModel<EmpApplicationOutgoing> pm = new PageModel<EmpApplicationOutgoing>();
		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
		if(deptDataByUserList==null || deptDataByUserList.size()==0){
			pm.setPageNo(0);
			pm.setPageSize(0);
			pm.setTotal(0);
			empApplicationOutgoing.setOffset(pm.getOffset());
			empApplicationOutgoing.setLimit(pm.getLimit());
			pm.setRows(new ArrayList<EmpApplicationOutgoing>());
			return pm;
		}else{
			empApplicationOutgoing.setCurrentUserDepart(deptDataByUserList);
			List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(currentUser.getEmployeeId());//下属员工
			if(subEmployeeIdList!=null&&subEmployeeIdList.size()>0){
				empApplicationOutgoing.setSubEmployeeIdList(subEmployeeIdList);
			}
			pm.setPageNo(page);
			pm.setPageSize(rows);
			
			//封装部门
			Integer firstDepart = empApplicationOutgoing.getFirstDepart();//页面上一级部门
			Integer secondDepart = empApplicationOutgoing.getSecondDepart();//页面上二级部门
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
			empApplicationOutgoing.setDepartList(departList);
			if(null != empApplicationOutgoing.getEndTime()){
				empApplicationOutgoing.setEndTime(DateUtils.parse(DateUtils.format(empApplicationOutgoing.getEndTime(),DateUtils.FORMAT_SHORT) + " 23:59:59"));
			}
			
			Integer total = empApplicationOutgoingMapper.getReportCount(empApplicationOutgoing);
			pm.setTotal(total);
			
			empApplicationOutgoing.setOffset(pm.getOffset());
			empApplicationOutgoing.setLimit(pm.getLimit());
			
			List<EmpApplicationOutgoing> ogs = getReportList(empApplicationOutgoing);
			
			pm.setRows(ogs);
			return pm;
		}	
	}
	
	public HSSFWorkbook exportReport(EmpApplicationOutgoing empApplicationOutgoing){
		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
		if(deptDataByUserList==null || deptDataByUserList.size()==0){
			return null;
		}else{
			empApplicationOutgoing.setCurrentUserDepart(deptDataByUserList);
			List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(currentUser.getEmployeeId());//下属员工
			if(subEmployeeIdList!=null&&subEmployeeIdList.size()>0){
				empApplicationOutgoing.setSubEmployeeIdList(subEmployeeIdList);
			}
			//封装部门
			Integer firstDepart = empApplicationOutgoing.getFirstDepart();//页面上一级部门
			Integer secondDepart = empApplicationOutgoing.getSecondDepart();//页面上二级部门
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
			empApplicationOutgoing.setDepartList(departList);
			if(null != empApplicationOutgoing.getEndTime()){
				empApplicationOutgoing.setEndTime(DateUtils.parse(DateUtils.format(empApplicationOutgoing.getEndTime(),DateUtils.FORMAT_SHORT) + " 23:59:59"));
			}
			
			List<EmpApplicationOutgoing> list = getReportList(empApplicationOutgoing);
			List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
			Map<String, Object> map = null;
			
			for(EmpApplicationOutgoing go:list){
				map = new HashMap<String,Object>();
				map.put("code", go.getCode());
				map.put("cnName", go.getCnName());
				map.put("departName", go.getDepartName());
				map.put("positionName", go.getPositionName());
				map.put("workType", go.getWorkType());
				map.put("startTime", DateUtils.format(go.getStartTime(),DateUtils.FORMAT_LONG));
				map.put("endTime", DateUtils.format(go.getEndTime(),DateUtils.FORMAT_LONG));
				map.put("duration", go.getDuration()+"小时");
				map.put("address", go.getAddress());
				map.put("reason", go.getReason());
				map.put("submitDate", DateUtils.format(go.getSubmitDate(),DateUtils.FORMAT_LONG));
				map.put("auditUser", go.getAuditUser());
				map.put("approvalStatusDesc", go.getApprovalStatusDesc());
				
				datas.add(map);
			}
			
			String[] titles = { "员工编号","员工姓名","部门","职位名称","工时制","开始时间","结束时间","外出时长","外出地点","外出事由","申请日期","批核人","状态"};
			String[] keys = { "code","cnName","departName","positionName","workType","startTime","endTime","duration","address","reason",
					"submitDate","auditUser","approvalStatusDesc"};
			
			return ExcelUtil.exportExcel(datas,keys,titles,"workbook.xls");
		}	
	}

	@Override
	public PageModel<EmpApplicationOutgoing> getOutTotalPageList(EmpApplicationOutgoing empApplicationOutgoing) {
		int page = empApplicationOutgoing.getPage() == null ? 0 : empApplicationOutgoing.getPage();
		int rows = empApplicationOutgoing.getRows() == null ? 0 : empApplicationOutgoing.getRows();
		
		PageModel<EmpApplicationOutgoing> pm = new PageModel<EmpApplicationOutgoing>();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		
		//封装部门
		Integer firstDepart = empApplicationOutgoing.getFirstDepart();//页面上一级部门
		Integer secondDepart = empApplicationOutgoing.getSecondDepart();//页面上二级部门
		List<Integer> departList = getDepartList(firstDepart,secondDepart);
		empApplicationOutgoing.setDepartList(departList);
		
		Integer total = empApplicationOutgoingMapper.getOutTotalCount(empApplicationOutgoing);
		pm.setTotal(total);
		
		empApplicationOutgoing.setOffset(pm.getOffset());
		empApplicationOutgoing.setLimit(pm.getLimit());
		
		List<EmpApplicationOutgoing> ogs = getOutTotalList(empApplicationOutgoing);
		
		pm.setRows(ogs);
		return pm;
	}
	
	private List<EmpApplicationOutgoing> getOutTotalList(EmpApplicationOutgoing empApplicationOutgoing) {
		List<EmpApplicationOutgoing> list = empApplicationOutgoingMapper.getOutTotalPageList(empApplicationOutgoing);
		return list;
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
	public HSSFWorkbook exportOutGoingTotal(EmpApplicationOutgoing empApplicationOutgoing) {

		List<Integer> departList = getDepartList(empApplicationOutgoing.getFirstDepart(),empApplicationOutgoing.getSecondDepart());
		empApplicationOutgoing.setDepartList(departList);
	    
		List<EmpApplicationOutgoing> list = getOutTotalList(empApplicationOutgoing);

	    @SuppressWarnings("unchecked")
		List<Map<String,Object>> datas =list.stream().map(o ->{
	    	Map<String,Object> map = new HashMap<>();
	    		try {
					//BeanUtils.populate(o,map);
					map = BeanUtils.describe(o);
				} catch (Exception e) {
					
				}
	        return map;
	    }).collect(Collectors.toList());
		
		String[] titles = { "年份","员工编号","姓名","部门","外出次数","外出时长"};
		String[] keys = { "year","code","cnName","departName","times","duration"};
		
		return ExcelUtil.exportExcel(datas,keys,titles,"workbook.xls");
	}
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void completeTask(HttpServletRequest request,String instanceId, String comment, String commentType)throws Exception {

		logger.info("外出申请单completeTask:start。。");
		Long time1 = System.currentTimeMillis();
		
		User user = userService.getCurrentUser();
		logger.info("外出申请单completeTask入参:instanceId="+instanceId+";comment="+comment+";commentType="+commentType
			+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));

		Task task = activitiServiceImpl.queryTaskByProcessInstanceId(instanceId);
		boolean type = StringUtils.startsWithIgnoreCase(task.getTaskDefinitionKey(), "end_");
		EmpApplicationOutgoing outgoing = queryByProcessInstanceId(instanceId);
		// 外出
		if(outgoing!=null){
			//通过或拒绝添加有效期限制
			if((StringUtils.equalsIgnoreCase(commentType, ConfigConstants.PASS)|| StringUtils.equalsIgnoreCase(commentType, ConfigConstants.REFUSE))
					&& annualVacationService.check5WorkingDayNextmonth(DateUtils.format(outgoing.getOutDate(), DateUtils.FORMAT_SHORT),type)){
				logger.error(userService.getCurrentUser().getEmployee().getCnName()+"审批员工Id="+outgoing.getEmployeeId()+"的外出："+"已超出有效时间,无法操作！");
				throw new OaException("已超出有效时间,无法操作！");
			}else{
				Integer approvalStatus ;
				if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.PASS)) {
					approvalStatus=type?ConfigConstants.PASS_STATUS:ConfigConstants.DOING_STATUS;
					
				}else if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.REFUSE)) {
					approvalStatus=ConfigConstants.REFUSE_STATUS;
				}else if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.OVERDUEPASS)){
					/**失效同意**/
					approvalStatus = type ? ConfigConstants.OVERDUEPASS_STATUS : ConfigConstants.DOING_STATUS;
				}else if((StringUtils.equalsIgnoreCase(commentType, ConfigConstants.OVERDUEREFUSE))){
					/**失效拒绝**/
					approvalStatus = ConfigConstants.OVERDUEREFUSE_STATUS;
				}else {
					approvalStatus=ConfigConstants.BACK_STATUS;
				}
				outgoing.setApprovalStatus(approvalStatus);
				outgoing.setUpdateTime(new Date());
				outgoing.setUpdateUser(user.getEmployee().getCnName());
				updateById(outgoing);
				//判断当前节点是否有办理人assignee,没有则将当前代理人设为办理人
				if(StringUtils.isBlank(task.getAssignee())) {
					activitiServiceImpl.claimTask(task.getId(), String.valueOf(userService.getCurrentUser().getEmployeeId()));
				}
				//-----------------start-----------------------保存流程节点信息
				sendMailService.sendCommentMail(employeeService.getByEmpId(outgoing.getEmployeeId()).get(0).getEmail(), "外出申请单", task.getName()+"-"+user.getEmployee().getCnName(), approvalStatus);
				ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
				flow.setAssigneeName(user.getEmployee().getCnName());
				flow.setDepartName(user.getDepart().getName());
				flow.setPositionName(user.getPosition().getPositionName());
				flow.setFinishTime(new Date());
				flow.setComment(comment);
				flow.setProcessId(instanceId);
				flow.setProcessKey(activitiServiceImpl.queryProcessByInstanceId(instanceId).getKey());
				flow.setStatu(approvalStatus);
				viewTaskInfoService.save(flow);
				//-----------------end-------------------------
				activitiServiceImpl.completeTask(task.getId(), comment,null,commentType);
			}
		}else{
			logger.error("流程实例为"+instanceId+"的外出审批数据不存在！");
			throw new OaException("流程实例为"+instanceId+"的外出审批数据不存在！");
		}
		
		Long time2 = System.currentTimeMillis();
		logger.info("外出申请单completeTask:use time="+(time2-time1));
		logger.info("外出申请单completeTask:end。。");
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void completeTaskByAdmin(HttpServletRequest request,String instanceId, String comment,String commentType, User user, Task task) throws Exception {
		
		logger.info("外出申请单completeTaskByAdmin:start。。");
		Long time1 = System.currentTimeMillis();
		logger.info("外出申请单completeTaskByAdmin入参:instanceId="+instanceId+";comment="+comment+";commentType="+commentType
			+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));

		EmpApplicationOutgoing outgoing = queryByProcessInstanceId(instanceId);
		if(null == task) {
			throw new OaException("该外出已被结束流程,请确认后再操作。");
		}
		boolean type = StringUtils.startsWithIgnoreCase(task.getTaskDefinitionKey(), "end_");
		if(outgoing!=null){
			Integer approvalStatus ;
			if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.PASS)) {
				approvalStatus=type?ConfigConstants.PASS_STATUS:ConfigConstants.DOING_STATUS;
				
			}else if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.OVERDUEPASS)){
				approvalStatus=type?ConfigConstants.OVERDUEPASS_STATUS:ConfigConstants.DOING_STATUS;							
 			}else if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.REFUSE)) {
				approvalStatus=ConfigConstants.REFUSE_STATUS;
			}else if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.OVERDUEREFUSE)){
				approvalStatus=ConfigConstants.OVERDUEREFUSE_STATUS;
 			}else {
				approvalStatus=ConfigConstants.BACK_STATUS;
			}
			outgoing.setApprovalStatus(approvalStatus);
			outgoing.setUpdateTime(new Date());
			outgoing.setUpdateUser(user.getEmployee().getCnName());
			outgoing.setNodeCode(task.getName());
			updateById(outgoing);
			//判断当前节点是否有办理人assignee,没有则将当前代理人设为办理人
			if(StringUtils.isBlank(task.getAssignee())) {
				activitiServiceImpl.claimTask(task.getId(), String.valueOf(userService.getCurrentUser().getEmployeeId()));
			}
			sendMailService.sendCommentMail(employeeService.getByEmpId(outgoing.getEmployeeId()).get(0).getEmail(), "外出申请单", task.getName()+"-"+user.getEmployee().getCnName(), approvalStatus);
			//-----------------start-----------------------保存流程节点信息
			ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
			flow.setAssigneeName(user.getEmployee().getCnName());
			flow.setDepartName(user.getDepart().getName());
			flow.setPositionName(user.getPosition().getPositionName());
			flow.setFinishTime(new Date());
			flow.setComment(comment);
			flow.setProcessId(instanceId);
			flow.setProcessKey(activitiServiceImpl.queryProcessByInstanceId(instanceId).getKey());
			flow.setStatu(approvalStatus);
			viewTaskInfoService.save(flow);
			//-----------------end-------------------------
			Map<String,Object> isAdmin = new HashMap<String,Object>();
			isAdmin.put("isAdmin", true);
			activitiServiceImpl.completeTask(task.getId(), comment,isAdmin,commentType);
		}else{
			logger.error("流程实例为"+instanceId+"的外出审批数据不存在！");
		}
		
		Long time2 = System.currentTimeMillis();
		logger.info("外出申请单completeTaskByAdmin:use time="+(time2-time1));
		logger.info("外出申请单completeTaskByAdmin:end。。");
		
	}

	
	@Override
	public EmpApplicationOutgoing queryByProcessInstanceId(String instanceId) {
		return empApplicationOutgoingMapper.queryByProcessId(instanceId);
	}
	
	@Override
	public void setValueToVO(TaskVO taskVO, String processInstanceId) {
		
		//外出申请
		EmpApplicationOutgoing outgoing = queryByProcessInstanceId(processInstanceId);
		taskVO.setProcessName("外出申请单");
		String redirectUrl = "/empApplicationOutgoing/approval.htm?flag=no&outgoingId="+outgoing.getId();
		if(!(taskVO.getProcessStatu()==null)) {
			redirectUrl = "/empApplicationOutgoing/approval.htm?flag=can&outgoingId="+outgoing.getId();
		}
		taskVO.setCreatorDepart(outgoing.getDepartName());
		taskVO.setProcessStatu(outgoing.getApprovalStatus());
		taskVO.setOperation(ConfigConstants.STATUS.getOperationByStatu(outgoing.getApprovalStatus()));
		taskVO.setCreator(outgoing.getCreateUser());
		taskVO.setCreateTime(outgoing.getCreateTime());
		taskVO.setView3(outgoing.getAddress());
		taskVO.setView4(DateUtils.getTime(outgoing.getStartTime(), outgoing.getEndTime()) + "&nbsp;&nbsp;" + outgoing.getDuration() + "小时");
		taskVO.setView5(outgoing.getReason());
		taskVO.setReProcdefCode("70");
		taskVO.setProcessId(outgoing.getProcessInstanceId());
		taskVO.setResourceId(String.valueOf(outgoing.getId()));
		
		taskVO.setRedirectUrl(redirectUrl);
	}

	@Override
	public void updateProcessInstanceId(EmpApplicationOutgoing newOutgoing) {
		empApplicationOutgoingMapper.updateById(newOutgoing);
	}
	
	/**
	 * 查询待办
	 */
	@Override
	public PageModel<EmpApplicationOutgoing> getApprovePageList(EmpApplicationOutgoing empApplicationOutgoing) {
		
		int page = empApplicationOutgoing.getPage() == null ? 0 : empApplicationOutgoing.getPage();
		int rows = empApplicationOutgoing.getRows() == null ? 0 : empApplicationOutgoing.getRows();
		
		PageModel<EmpApplicationOutgoing> pm = new PageModel<EmpApplicationOutgoing>();

		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
		if(deptDataByUserList==null || deptDataByUserList.size()==0){
			pm.setPageNo(0);
			pm.setPageSize(0);
			pm.setTotal(0);
			empApplicationOutgoing.setOffset(pm.getOffset());
			empApplicationOutgoing.setLimit(pm.getLimit());
			pm.setRows(new ArrayList<EmpApplicationOutgoing>());
			return pm;
		}else{
			empApplicationOutgoing.setCurrentUserDepart(deptDataByUserList);//数据权限

			
			List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(currentUser.getEmployeeId());//下属员工
			if(subEmployeeIdList!=null&&subEmployeeIdList.size()>0){
				empApplicationOutgoing.setSubEmployeeIdList(subEmployeeIdList);
			}
			pm.setPageNo(page);
			pm.setPageSize(rows);
			//封装部门
			Integer firstDepart = empApplicationOutgoing.getFirstDepart();//页面上一级部门
			Integer secondDepart = empApplicationOutgoing.getSecondDepart();//页面上二级部门
			List<Integer> departList = getDepartList(firstDepart,secondDepart);
			empApplicationOutgoing.setDepartList(departList);
			Integer total = empApplicationOutgoingMapper.getApprovePageListCount(empApplicationOutgoing);
			pm.setTotal(total);
			
			empApplicationOutgoing.setOffset(pm.getOffset());
			empApplicationOutgoing.setLimit(pm.getLimit());
			
			List<EmpApplicationOutgoing> empApplicationOutgoingList = empApplicationOutgoingMapper.getApprovePageList(empApplicationOutgoing);
			pm.setRows(empApplicationOutgoingList);
			return pm;
		}	
	}
	
	/**
	 * 查询已办单据
	 */
	@Override
	public PageModel<EmpApplicationOutgoing> getAuditedPageList(EmpApplicationOutgoing empApplicationOutgoing) {
		int page = empApplicationOutgoing.getPage() == null ? 0 : empApplicationOutgoing.getPage();
		int rows = empApplicationOutgoing.getRows() == null ? 0 : empApplicationOutgoing.getRows();
		
		PageModel<EmpApplicationOutgoing> pm = new PageModel<EmpApplicationOutgoing>();
		pm.setPageNo(page);
		pm.setPageSize(rows);

		//封装部门
		Integer firstDepart = empApplicationOutgoing.getFirstDepart();//页面上一级部门
		Integer secondDepart = empApplicationOutgoing.getSecondDepart();//页面上二级部门
		List<Integer> departList = getDepartList(firstDepart,secondDepart);
		empApplicationOutgoing.setDepartList(departList);
		
		Integer total = empApplicationOutgoingMapper.getAuditedPageListCount(empApplicationOutgoing);
		pm.setTotal(total);
		
		empApplicationOutgoing.setOffset(pm.getOffset());
		empApplicationOutgoing.setLimit(pm.getLimit());
		
		List<EmpApplicationOutgoing> empApplicationOutgoingList = empApplicationOutgoingMapper.getAuditedPageList(empApplicationOutgoing);
		for (EmpApplicationOutgoing empApplicationOutgoing2 : empApplicationOutgoingList) {
			String auditUser = userService.getCurrentUser().getEmployee().getCnName();
			empApplicationOutgoing2.setAuditUser(auditUser);
		}
		pm.setRows(empApplicationOutgoingList);
		return pm;
	}
	
	/**
	 * 查询失效单据
	 */
	@Override
	public PageModel<EmpApplicationOutgoing> getInvalidPageList(EmpApplicationOutgoing empApplicationOutgoing) {
		int page = empApplicationOutgoing.getPage() == null ? 0 : empApplicationOutgoing.getPage();
		int rows = empApplicationOutgoing.getRows() == null ? 0 : empApplicationOutgoing.getRows();
		
		PageModel<EmpApplicationOutgoing> pm = new PageModel<EmpApplicationOutgoing>();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		empApplicationOutgoing.setApprovalStatus(ConfigConstants.OVERDUE_STATUS);
		//封装部门
		Integer firstDepart = empApplicationOutgoing.getFirstDepart();//页面上一级部门
		Integer secondDepart = empApplicationOutgoing.getSecondDepart();//页面上二级部门
		List<Integer> departList = getDepartList(firstDepart,secondDepart);
		empApplicationOutgoing.setDepartList(departList);
 		Integer total = empApplicationOutgoingMapper.getApprovePageListCount(empApplicationOutgoing);
		pm.setTotal(total);
		
		
		empApplicationOutgoing.setOffset(pm.getOffset());
		empApplicationOutgoing.setLimit(pm.getLimit());
		
		List<EmpApplicationOutgoing> empApplicationOutgoingList = empApplicationOutgoingMapper.getApprovePageList(empApplicationOutgoing);
		for (EmpApplicationOutgoing empApplicationOutgoing2 : empApplicationOutgoingList) {
			String auditUser = userService.getCurrentUser().getEmployee().getCnName();
			empApplicationOutgoing2.setAuditUser(auditUser);
		}
		pm.setRows(empApplicationOutgoingList);
		return pm;
	}
	
}
