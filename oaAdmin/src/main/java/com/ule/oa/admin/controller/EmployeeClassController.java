package com.ule.oa.admin.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.google.common.collect.Maps;
import com.ule.oa.admin.util.IsMenu;
import com.ule.oa.admin.util.IsOperation;
import com.ule.oa.admin.util.ResponseUtil;
import com.ule.oa.base.po.ApplicationEmployeeClass;
import com.ule.oa.base.po.ClassSetting;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.EmpApplicationLeave;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.po.ScheduleGroup;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.ApplicationEmployeeClassService;
import com.ule.oa.base.service.ApplicationEmployeeDutyService;
import com.ule.oa.base.service.ClassSettingService;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmployeeClassService;
import com.ule.oa.base.service.ScheduleService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.PageModel;
import com.ule.oa.common.utils.json.JSONUtils;


@Controller
@RequestMapping("employeeClass")
public class EmployeeClassController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private EmployeeClassService employeeClassService;
	@Resource
	private ApplicationEmployeeDutyService applicationEmployeeDutyService;
	@Resource
	private ApplicationEmployeeClassService applicationEmployeeClassService;
	@Resource
	private ClassSettingService classSettingService;
	@Autowired
	private UserService userService;
	@Autowired
	private ScheduleService scheduleService;
	@Autowired
	private DepartService departService;
	
	
	@RequestMapping(value="/getClassTime.htm", produces = "text/json;charset=UTF-8")
	@ResponseBody
	public String getClassTime(String employeeId,String classDate) throws OaException{
	
		EmployeeClass employeeClass = new EmployeeClass();
		employeeClass.setEmployId(Long.parseLong(employeeId));
		employeeClass.setClassDate(DateUtils.parse(classDate,DateUtils.FORMAT_SHORT));
		EmployeeClass empClass = employeeClassService.getEmployeeClassByCondition(employeeClass);
		
		Map<String, String> map = new HashMap<String, String>();
		if(null != empClass && null != empClass.getId()){
			String startTime = classDate + " " + DateUtils.format(empClass.getStartTime(), DateUtils.FORMAT_HH_MM_SS);
			String endTime = classDate + " " + DateUtils.format(empClass.getEndTime(), DateUtils.FORMAT_HH_MM_SS);
			
			map.put("className", empClass.getName());
			map.put("startTime", startTime);
			map.put("endTime", endTime);
		}else{//没有排班，默认就按照早9晚18点
			map.put("className", "默认班次");
			map.put("startTime", classDate + " 9:00:00");
			map.put("endTime", classDate + " 18:00:00");
		}
		return JSONUtils.write(map);
	}
	
	//值班查询
	@RequestMapping("/dutyQuery.htm")
	public ModelAndView dutyQuery(){
		return new ModelAndView("base/employeeClass/dutyQuery");
	}
	
	//查询值班
	@RequestMapping("/queryDuty.htm")
	@ResponseBody
	public List<Map<String,Object>> queryDuty(String vacationName,String year,Long departId) throws OaException{
	
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		try{
			result = applicationEmployeeDutyService.queryDutyByCondition(vacationName, year, departId);
		}catch(Exception e){
			logger.error("queryDuty:",e);
		}
		return result;
	}
	
	 //导出值班
	@ResponseBody
	@RequestMapping("/exportDuty.htm")
	public void exportDuty(String vacationName,String year,Long departId,HttpServletResponse response,HttpServletRequest request) throws IOException{
		
		OutputStream os = null;
		try {
			vacationName = URLDecoder.decode(vacationName,"UTF-8");
			ResponseUtil.setDownLoadExeclInfo(response,request,"值班查询_");
			HSSFWorkbook hSSFWorkbook = applicationEmployeeDutyService.exportDuty(vacationName, year, departId);
			os = response.getOutputStream();
			hSSFWorkbook.write(os);
		} catch (Exception e) {
			logger.error("导出值班报表失败,原因=",e);
		}finally{
			if(null != os){
				os.flush();
				os.close();
			}
		}
	}
	
	
	//考勤管理-排班管理-排班查询-导出
	@IsOperation(returnType=true)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/exportClass.htm")
	public void exportClass(String month,Long departId,Long groupId,HttpServletResponse response,HttpServletRequest request) throws IOException{
		
		OutputStream os = null;
		try {
			ResponseUtil.setDownLoadExeclInfo(response,request,"排班查询_");
			HSSFWorkbook hSSFWorkbook = applicationEmployeeClassService.exportClass(month, departId,groupId);
			os = response.getOutputStream();
			hSSFWorkbook.write(os);
		} catch (Exception e) {
			logger.error("导出排班报表失败,原因=",e);
		}finally{
			if(null != os){
				os.flush();
				os.close();
			}
		}
	}
	
	//获取班次
	@RequestMapping("/getClassSet.htm")
	@ResponseBody
	public List<ClassSetting> getClassSet(Long groupId) throws OaException{
		logger.info("..enter to function {},param data:{}", Thread.currentThread().getStackTrace()[1].getMethodName(), "groupId="+groupId);
		List<ClassSetting> result = new ArrayList<ClassSetting>();
		try{
			ClassSetting rest = new ClassSetting();
			rest.setName("休息");
			rest.setId(null);
			result.add(rest);
			ClassSetting param = new ClassSetting();
			param.setGroupId(groupId);
			List<ClassSetting> list= classSettingService.getListByCondition(param);
			for(ClassSetting set:list){
				result.add(set);
			}
		}catch(Exception e){
			logger.error("getClassSet:",e);
		}
		return result;
	}
	
	
	//获取白班班次信息
	@RequestMapping("/getDayShiftClass.htm")
	@ResponseBody
	public List<Map<String,Object>> getDayShiftClass(String classDate,Long employeeId) throws OaException{
		logger.info("getDayShiftClass:classDate="+classDate+";employeeId="+employeeId);
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		try{
			EmployeeClass employeeClass = new EmployeeClass();
			employeeClass.setEmployId(employeeId);
			employeeClass.setClassDate(DateUtils.parse(classDate,DateUtils.FORMAT_SHORT));
			EmployeeClass empClass = employeeClassService.getEmployeeClassByCondition(employeeClass);
			if(empClass!=null&&empClass.getStartTime()!=null){
				Map<String,Object> data = new HashMap<String,Object>();
				data.put("id", empClass.getClassSettingId());
				data.put("name", empClass.getName());
				data.put("startTime", DateUtils.format(empClass.getStartTime(), DateUtils.FORMAT_HH_MM));
				data.put("endTime", DateUtils.format(empClass.getEndTime(), DateUtils.FORMAT_HH_MM));
				data.put("mustAttnTime", empClass.getMustAttnTime());
				result.add(data);
			}else{
				ClassSetting param = new ClassSetting();
				param.setStartTime(DateUtils.parse("09:00:00","hh:mm:ss"));
				param.setEndTime(DateUtils.parse("18:00:00","hh:mm:ss"));
				param.setDelFlag(0);
				List<ClassSetting> list= classSettingService.getListByCondition(param);
				if(list!=null&&list.size()>0){
					Map<String,Object> data = new HashMap<String,Object>();
					data.put("id", list.get(0).getId());
					data.put("name", list.get(0).getName());
					data.put("startTime", DateUtils.format(list.get(0).getStartTime(), DateUtils.FORMAT_HH_MM));
					data.put("endTime", DateUtils.format(list.get(0).getEndTime(), DateUtils.FORMAT_HH_MM));
					data.put("mustAttnTime", list.get(0).getMustAttnTime());
					result.add(data);
				}
			}
		}catch(Exception e){
			logger.error("getClassSet:",e);
		}
		return result;
	}
	
	
	//排班审核查询页面
	@IsMenu
	@RequestMapping("/audit.htm")
	public ModelAndView audit(){
		return new ModelAndView("base/employeeClass/audit");
	}
	
	//排班申请待办分页查询
	@ResponseBody
	@RequestMapping("/getHandlingListByPage.htm")
	public PageModel<ApplicationEmployeeClass> getHandlingListByPage(ApplicationEmployeeClass employeeClass){
	
		PageModel<ApplicationEmployeeClass> pm=new PageModel<ApplicationEmployeeClass>();
		pm.setRows(new java.util.ArrayList<ApplicationEmployeeClass>());
		
		try {
			pm = applicationEmployeeClassService.getHandlingListByPage(employeeClass);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	//获取排班申请单详情数据
	@ResponseBody
	@RequestMapping("/getDetailById.htm")
	public Map<String,Object> getDetailById(Long id){
		
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			result = applicationEmployeeClassService.getDetailById(id);
		}catch(Exception e){
			logger.error("getDetailById:",e);
		}
		return result;
	}
	
	@ResponseBody
	@RequestMapping("/completeTask.htm")
	public Map<String,Object> completeTask(HttpServletRequest request,String processIds,String comment,String commentType){
		Map<String,Object> result = Maps.newHashMap();
		try {
			for(String processId:processIds.split(",")){
				applicationEmployeeClassService.completeTask(request,processId, comment, commentType);
			}
			result.put("sucess", true);
		} catch (OaException e) {
			result.put("sucess", false);
			result.put("msg", e.getMessage());
		}catch (Exception e) {
			result.put("sucess", false);
			result.put("msg", "审批假期出现问题,与开发人员联系");
		}
		return result;
	}
	
	//排班申请已办分页查询
	@ResponseBody
	@RequestMapping("/getHandledListByPage.htm")
	public PageModel<ApplicationEmployeeClass> getHandledListByPage(ApplicationEmployeeClass employeeClass){
	
		PageModel<ApplicationEmployeeClass> pm=new PageModel<ApplicationEmployeeClass>();
		pm.setRows(new java.util.ArrayList<ApplicationEmployeeClass>());
		
		try {
			pm = applicationEmployeeClassService.getHandledListByPage(employeeClass);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	//排班申请失效分页查询
	@ResponseBody
	@RequestMapping("/getInvalidListByPage.htm")
	public PageModel<ApplicationEmployeeClass> getInvalidListByPage(ApplicationEmployeeClass employeeClass){
	
		PageModel<ApplicationEmployeeClass> pm=new PageModel<ApplicationEmployeeClass>();
		pm.setRows(new java.util.ArrayList<ApplicationEmployeeClass>());
		
		try {
			pm = applicationEmployeeClassService.getInvalidListByPage(employeeClass);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	//获取某员工某天班次信息
	@ResponseBody
	@RequestMapping("/getSettingInfo.htm")
	public Map<String,String> getSettingInfo(String date,Long setId,Long newSetId){
		
		Map<String,String> result = new HashMap<String,String>();
		try {
		    result = applicationEmployeeClassService.getSettingInfo(date,setId,newSetId);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return result;
	}
	
	//人事后台调班
	@ResponseBody
	@RequestMapping("/hrMoveAtAdmin.htm")
	public Map<String,Object> hrMoveAtAdmin(HttpServletRequest request,String date,Long employId,Long setId ,String remark){
		
		Map<String,Object> result = new HashMap<String,Object>();
		try {
		    result = applicationEmployeeClassService.hrMoveAtAdmin(request,date,employId,setId,remark);
		    result.put("success",true);
		} catch (Exception e) {
			result.put("message",e.getMessage());
			result.put("success",false);
			logger.error(e.getMessage(),e);
		}
		return result;
	}
	//获取某员工某天班次信息
	@ResponseBody
	@RequestMapping("/getSettingInfos.htm")
	public Map<String,String> getSettingInfos(String date,Long setId,Long classId,Long empId){
		
		Map<String,String> result = new HashMap<String,String>();
		try {
			result = applicationEmployeeClassService.getSettingInfos(date,setId,classId,empId);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return result;
	}	
	
	//导出排班申请单详情数据
	@ResponseBody
	@RequestMapping("/exportDetailById.htm")
	public void exportDetailById(Long id,HttpServletResponse response,HttpServletRequest request) throws IOException{
		
		OutputStream os = null;
		try {
			ResponseUtil.setDownLoadExeclInfo(response,request,"排班查询_");
			HSSFWorkbook hSSFWorkbook = applicationEmployeeClassService.exportDetailById(id);
			os = response.getOutputStream();
			hSSFWorkbook.write(os);
		} catch (Exception e) {
			logger.error("导出排班报表失败,原因=",e);
		}finally{
			if(null != os){
				os.flush();
				os.close();
			}
		}
	}
	
	//排班查询
	@IsMenu
	@RequestMapping("/schedule.htm")
	public ModelAndView schedule(HttpServletRequest request){
		User user = userService.getCurrentUser();
		//判断登录人是否是排班人（是的话默认加载部门和组别）
    	List<ScheduleGroup> groupList = scheduleService.getListByScheduler(user.getEmployeeId());
    	request.setAttribute("isScheduler", false);
    	if(groupList!=null&&groupList.size()>0){
    		request.setAttribute("isScheduler", true);
    		List<Depart> departList = new ArrayList<Depart>();
    		Map<Long,Long> departMap = new HashMap<Long,Long>();
    		for(ScheduleGroup group:groupList){
    			if(!(departMap!=null&&departMap.containsKey(group.getDepartId()))){
    				departMap.put(group.getDepartId(), group.getDepartId());
    			}
    		}
    		for (Map.Entry<Long,Long> entry : departMap.entrySet()) {
    			Depart depart = departService.getById(entry.getKey());
    			departList.add(depart);
    		}
    		request.setAttribute("departList", departList);
    	}
		return new ModelAndView("base/employeeClass/schedule");
	}
	
	//考勤管理-排班管理-排班-新增排班
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/getUnCommitData.htm")
	public Map<String,Object> getUnCommitData(Long departId,Long groupId){
	
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			result = applicationEmployeeClassService.getUnCommitData(departId,groupId);
		}catch(Exception e){
			logger.error("getUnCommitData:",e);
		}
		return result;
	}
	
	//考勤管理-排班管理-排班-保存
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/saveSchedule.htm")
	public Map<String,Object> saveSchedule(HttpServletRequest request,Long departId,Long groupId,String info){
		
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			result = applicationEmployeeClassService.saveSchedule(request,info,departId,groupId);
		}catch(Exception e){
			result.put("success",false);
			result.put("message",e.toString());
			logger.error("saveSchedule:",e);
		}
		return result;
	}
	
	//考勤管理-排班管理-排班-提交
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/commitSchedule.htm")
	public Map<String,Object> commitSchedule(HttpServletRequest request,Long departId,Long groupId){
	
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			result = applicationEmployeeClassService.commitSchedule(request,departId,groupId);
		}catch(Exception e){
			logger.error("saveSchedule:",e);
		}
		return result;
	}
	
	//考勤管理-排班管理-排班-排班记录
	@IsOperation(returnType=false)//需要校验操作权限
	@RequestMapping("/getScheduleRecord.htm")
	@ResponseBody
	public PageModel<ApplicationEmployeeClass> getScheduleRecord(ApplicationEmployeeClass empClassTemp) throws OaException{
		
		PageModel<ApplicationEmployeeClass> pm=new PageModel<ApplicationEmployeeClass>();
		try{
			pm.setRows(new java.util.ArrayList<ApplicationEmployeeClass>());
			pm=applicationEmployeeClassService.getScheduleRecord(empClassTemp);
		}catch(Exception e){
			logger.error("queryClassSetting:",e);
		}
		return pm;
	}	
	
	// 合并controller
	//考勤管理-排班管理-排班查询-查询
	@IsOperation(returnType=false)//需要校验操作权限
	@RequestMapping("/queryClass.htm")
	@ResponseBody
	public Map<String,Object> queryClass(ApplicationEmployeeClass empClass) throws OaException{
		
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			result = employeeClassService.queryClassByMonth(empClass);
		
		}catch(Exception e){
			logger.error("queryClass:",e);
		}
		return result;
	}	
	
	//考勤管理-排班管理-排班查询-查询单个按钮        
	@RequestMapping("/queryClassInfo.htm")
	@ResponseBody
	public Map<String,Object> queryClassInfo(String month,Long departId,Long id,boolean isQueryflag,Long groupId) throws OaException{
		
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			
			result = employeeClassService.queryClassByCondition(departId, month,id,isQueryflag,groupId);
		}catch(Exception e){
			logger.error("queryDuty:",e);
		}
		return result;
	}
	
	//查询班次管理   
	@RequestMapping("/queryClassSetting.htm")
	@ResponseBody
	public PageModel<ClassSetting> queryClassSeting(ClassSetting classSet) throws OaException{
	
		PageModel<ClassSetting> pm=new PageModel<ClassSetting>();
		try{
			pm.setRows(new java.util.ArrayList<ClassSetting>());
			pm = classSettingService.getReportPageList(classSet);
			
		}catch(Exception e){
			logger.error("queryClassSetting:",e);
		}
		return pm;
	}
	
	//考勤管理-排班管理-班次管理-启用  
	@IsOperation(returnType=false)//需要校验操作权限
	@RequestMapping("/updateClassSettingEnableById.htm")
	@ResponseBody
	public Map<String,Object> updateClassSettingEnableById(ClassSetting classSet) throws OaException{
		
		Map<String,Object> result=new HashMap<String,Object>();
		try{
			classSettingService.updateById(classSet);
			result.put("success", true);
		}catch(Exception e){
			result.put("success", false);
			logger.error("updateClassSettingEnableById:",e);
		}
		return result;
	}
	
	//考勤管理-排班管理-班次管理-禁用 
	@IsOperation(returnType=false)//需要校验操作权限
	@RequestMapping("/updateClassSettingNoEnableById.htm")
	@ResponseBody
	public Map<String,Object> updateClassSettingNoEnableById(ClassSetting classSet) throws OaException{
	
		Map<String,Object> result=new HashMap<String,Object>();
		try{
			classSettingService.updateById(classSet);
			result.put("success", true);
		}catch(Exception e){
			result.put("success", false);
			logger.error("updateClassSettingNoEnableById:",e);
		}
		return result;
	}
	
	//考勤管理-排班管理-班次管理-添加班次 
	@IsOperation(returnType=false)//需要校验操作权限
	@RequestMapping("/insertClassSetting.htm")
	@ResponseBody
	public Map<String,Object> insertClassSetting(ClassSetting classSet) throws OaException{
	
		Map<String,Object> result=new HashMap<String,Object>();
		try{
			classSettingService.save(classSet);
			result.put("success", true);
		}catch(Exception e){
			result.put("success", false);
			result.put("msg", e.getMessage());
			logger.error("insertClassSetting:",e);
		}
		return result;
	}
	
	//考勤管理-排班管理-班次管理-修改 
	@IsOperation(returnType=false)//需要校验操作权限
	@RequestMapping("/updateClassName.htm")
	@ResponseBody
	public Map<String,Object> updateClassName(ClassSetting classSet) throws OaException{
	
		Map<String,Object> result=new HashMap<String,Object>();
		try{
			classSettingService.updateClassName(classSet);
			result.put("success", true);
		}catch(Exception e){
			result.put("success", false);
			result.put("msg", e.getMessage());
			logger.error("updateClassName:",e);
		}
		return result;
	}
	
	//考勤管理-排班管理-班次管理-删除 
	@IsOperation(returnType=false)//需要校验操作权限
	@RequestMapping("/deleteClassSettingById.htm")
	@ResponseBody
	public Map<String,Object> deleteClassSettingById(ClassSetting classSet) throws OaException{
	
		Map<String,Object> result=new HashMap<String,Object>();
		try{
			classSettingService.updateById(classSet);
			result.put("success", true);
		}catch(Exception e){
			result.put("success", false);
			logger.error("deleteClassSettingById:",e);
		}
		return result;
	}
	
	//通过id 查询 排班信息 
	@RequestMapping("/queryClassById.htm")
	@ResponseBody
	public Map<String,Object> queryClassById(Long id) throws OaException{
		
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			ClassSetting classSet = classSettingService.getById(id);
			result.put("classSet", classSet);
			result.put("success", true);
		}catch(Exception e){
			logger.error("queryClassById:",e);
			result.put("success", false);
		}
		return result;
	}	
	
	//考勤管理-排班管理-调班-提交 
	@IsOperation(returnType=false)//需要校验操作权限
	@RequestMapping("/changeClassSet.htm")
	@ResponseBody
	public Map<String,Object> changeClassSet(HttpServletRequest request,String month,Long departId,Long groupId,String map) throws OaException{
		
		Map<String,Object> result=new HashMap<String,Object>();
		
		try{
			result= applicationEmployeeClassService.changeClassSet(request,month, departId, groupId, map);
			
			result.put("success", true);
		}catch(Exception e){
			result.put("success", false);
			result.put("msg", e.getMessage());
			logger.error("changeClassSet:",e);
		}
		return result;
	}
	
	//考勤管理-排班管理-调班-查询 
	@IsOperation(returnType=false)//需要校验操作权限
	@RequestMapping("/queryChangeClassInfo.htm")
	@ResponseBody
	public Map<String,Object> queryChangeClassInfo(String month,Long departId,Long id,boolean isQueryflag,Long groupId) throws OaException{
	
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			
			result = employeeClassService.queryClassByCondition(departId, month,id,isQueryflag,groupId);
		}catch(Exception e){
			logger.error("queryDuty:",e);
		}
		return result;
	}
	
	//考勤管理-排班管理-调班-调班记录 
	@IsOperation(returnType=false)//需要校验操作权限
	@RequestMapping("/queryChangeClass.htm")
	@ResponseBody
	public PageModel<ApplicationEmployeeClass> queryChangeClass(ApplicationEmployeeClass empClassTemp) throws OaException{
	
		PageModel<ApplicationEmployeeClass> pm=new PageModel<ApplicationEmployeeClass>();
		try{
			pm.setRows(new java.util.ArrayList<ApplicationEmployeeClass>());
			pm=applicationEmployeeClassService.queryChangeClass(empClassTemp);
		}catch(Exception e){
			logger.error("queryChangeClass:",e);
		}
		return pm;
	}	
	
	//调班
	@IsMenu
	@RequestMapping("/changeClass.htm")
	public ModelAndView changeClass(HttpServletRequest request){
		
		User user = userService.getCurrentUser();
		//判断登录人是否是排班人（是的话默认加载部门和组别）
    	List<ScheduleGroup> groupList = scheduleService.getListByScheduler(user.getEmployeeId());
    	request.setAttribute("isScheduler", false);
    	if(groupList!=null&&groupList.size()>0){
    		request.setAttribute("isScheduler", true);
    		List<Depart> departList = new ArrayList<Depart>();
    		Map<Long,Long> departMap = new HashMap<Long,Long>();
    		for(ScheduleGroup group:groupList){
    			if(!(departMap!=null&&departMap.containsKey(group.getDepartId()))){
    				departMap.put(group.getDepartId(), group.getDepartId());
    			}
    		}
    		for (Map.Entry<Long,Long> entry : departMap.entrySet()) {
    			Depart depart = departService.getById(entry.getKey());
    			departList.add(depart);
    		}
    		request.setAttribute("departList", departList);
    	}
    	request.setAttribute("nowMonth", DateUtils.getNow("YYYY-MM"));
		return new ModelAndView("base/employeeClass/empChangeClass");
	}
	
	//排班查询
	@IsMenu
	@RequestMapping("/classQuery.htm")
	public ModelAndView classQuery(HttpServletRequest request){
		request.setAttribute("nowMonth", DateUtils.getNow("YYYY-MM"));
		return new ModelAndView("base/employeeClass/empClassQuery");
	}
	
	//班次管理
	@IsMenu
	@RequestMapping("/classSetting.htm")
	public ModelAndView dutyQuery(HttpServletRequest request){
		request.setAttribute("scheduleList", scheduleService.getScheduleList());
		return new ModelAndView("base/employeeClass/empClassSetting");
	}
	
	/**
	 * 导出指定组别月份的排班数据
	 * @param month
	 * @param departId
	 * @param groupId
	 * @param response
	 * @param request
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping("/exportScheduleDataByMonth.htm")
	public void exportScheduleDataByMonth(String month, Long departId,Long groupId,HttpServletResponse response,HttpServletRequest request) throws IOException{
	
		OutputStream os = null;
		try{
			HSSFWorkbook workbook = employeeClassService.exportScheduleDataByMonthAndGroupId(departId, groupId, month);
			ResponseUtil.setDownLoadExeclInfo(response,request,"排班_");
            os = response.getOutputStream();
    		if(workbook!=null){
    			workbook.write(os);
    		}
		}catch(Exception e){
			logger.error("导出排班数据失败,原因=",e);
		}finally{
			if(null != os){
				os.flush();
				os.close();
			}
		}
    }
	
}
