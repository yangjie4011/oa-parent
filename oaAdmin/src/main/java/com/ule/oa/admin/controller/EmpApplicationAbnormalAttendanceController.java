package com.ule.oa.admin.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.ule.oa.base.po.CompanyConfig;
import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.EmpApplicationAbnormalAttendance;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.po.User;
import com.ule.oa.base.po.tbl.ViewTaskInfoTbl;
import com.ule.oa.base.service.CompanyConfigService;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmpApplicationAbnormalAttendanceService;
import com.ule.oa.base.service.EmployeeClassService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.service.UserTaskService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.JSON;
import com.ule.oa.common.utils.JsonWriter;
import com.ule.oa.common.utils.PageModel;

/**
 * @ClassName: 异常考勤
 * @Description: 异常考勤
 * @author yangjie
 * @date 2017年12月18日
 */

@Controller
@RequestMapping("empApplicationAbnormalAttendance")
public class EmpApplicationAbnormalAttendanceController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private EmpApplicationAbnormalAttendanceService empApplicationAbnormalAttendanceService;
	@Autowired
	private DepartService departService;
	@Autowired
	private EmployeeClassService employeeClassService;
	@Autowired
	private CompanyConfigService companyConfigService;
	@Autowired
	private ConfigService configService;
	@Autowired
	private UserService userService;
	@Autowired
	private UserTaskService userTaskService;
	
	@IsMenu
	@RequestMapping("/index.htm")
	public ModelAndView index(){
		return new ModelAndView("base/attnManager/abnormalAttendance");
	}
	
	//考勤管理-日常考勤管理-异常考勤查询
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/getReportPageList.htm")
	public PageModel<EmpApplicationAbnormalAttendance> getReportPageList(EmpApplicationAbnormalAttendance attendance){
	
		if(attendance.getFirstDepart()!=null){
			attendance.setDepartId(Long.valueOf(attendance.getFirstDepart()));
		}
		
		PageModel<EmpApplicationAbnormalAttendance> pm=new PageModel<EmpApplicationAbnormalAttendance>();
		pm.setRows(new java.util.ArrayList<EmpApplicationAbnormalAttendance>());
		
		try {
			pm = empApplicationAbnormalAttendanceService.getReportPageList(attendance);
//			Map<String, String> map = new HashMap<String, String>();
//			//1.准备工时类型map，key:配置ID，value：配置编码
//			CompanyConfig companyConfigConditon = new CompanyConfig();//查询配置表条件
//			companyConfigConditon.setCode("typeOfWork");//工时类型
//			List<CompanyConfig> workTypeList = companyConfigService.getListByCondition(companyConfigConditon);
//			
//			Config configCondition = new Config();
//			configCondition.setCode("whetherScheduling");//是否排班
//			List<Config> whetherSchedulingList = configService.getListByCondition(configCondition);
//			
//	        Map<Long,String> workTypeMap = new HashMap<Long,String>();//存放配置的map
//	        Map<Long,String> schedulingMap = new HashMap<Long,String>();//存放配置的map
//	        
//	        for(CompanyConfig cConfig:workTypeList){
//	        	workTypeMap.put(cConfig.getId(), cConfig.getDisplayCode());
//	        }
//	        for(Config config:whetherSchedulingList){
//	        	schedulingMap.put(config.getId(), config.getDisplayCode());
//	        }
//			for(EmpApplicationAbnormalAttendance attn:pm.getRows()){
//				map = getAttnStatistics(attn,workTypeMap,schedulingMap);
//				attn.setStartResult(map.get("startResult"));
//				attn.setEndResult(map.get("endResult"));
//			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	//考勤管理-日常考勤管理-下属异常考勤查询
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/getSubReportPageList.htm")
	public PageModel<EmpApplicationAbnormalAttendance> getSubReportPageList(EmpApplicationAbnormalAttendance attendance){
		
		if(attendance.getFirstDepart()!=null){
			attendance.setDepartId(Long.valueOf(attendance.getFirstDepart()));
		}
		
		PageModel<EmpApplicationAbnormalAttendance> pm=new PageModel<EmpApplicationAbnormalAttendance>();
		pm.setRows(new java.util.ArrayList<EmpApplicationAbnormalAttendance>());
		
		try {
			pm = empApplicationAbnormalAttendanceService.getReportPageList(attendance);
//			Map<String, String> map = new HashMap<String, String>();
//			//1.准备工时类型map，key:配置ID，value：配置编码
//			CompanyConfig companyConfigConditon = new CompanyConfig();//查询配置表条件
//			companyConfigConditon.setCode("typeOfWork");//工时类型
//			List<CompanyConfig> workTypeList = companyConfigService.getListByCondition(companyConfigConditon);
//			
//			Config configCondition = new Config();
//			configCondition.setCode("whetherScheduling");//是否排班
//			List<Config> whetherSchedulingList = configService.getListByCondition(configCondition);
//			
//	        Map<Long,String> workTypeMap = new HashMap<Long,String>();//存放配置的map
//	        Map<Long,String> schedulingMap = new HashMap<Long,String>();//存放配置的map
//	        
//	        for(CompanyConfig cConfig:workTypeList){
//	        	workTypeMap.put(cConfig.getId(), cConfig.getDisplayCode());
//	        }
//	        for(Config config:whetherSchedulingList){
//	        	schedulingMap.put(config.getId(), config.getDisplayCode());
//	        }
//			for(EmpApplicationAbnormalAttendance attn:pm.getRows()){
//				map = getAttnStatistics(attn,workTypeMap,schedulingMap);
//				attn.setStartResult(map.get("startResult"));
//				attn.setEndResult(map.get("endResult"));
//			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	@IsMenu
	@RequestMapping("/attnApprove.htm")
	public ModelAndView attnApprove(){
		return new ModelAndView("base/attnManager/abnormalAttendanceApprove");
	}
	
	@ResponseBody
	@RequestMapping("/getAttnApprovePageList.htm")
	public PageModel<EmpApplicationAbnormalAttendance> getAttnApprovePageList(EmpApplicationAbnormalAttendance attendance){
		
		attendance.setApprovalStatus(ConfigConstants.DOING_STATUS);
		List<Integer> departList = new ArrayList<Integer>();
		if(attendance.getSecondDepart()!=null){
			departList.add(attendance.getSecondDepart());
			attendance.setDepartList(departList);
		}else if(attendance.getSecondDepart()==null&&attendance.getFirstDepart()!=null){
			departList.add(attendance.getFirstDepart());
			List<Depart> list = departService.getByParentId(Long.valueOf(String.valueOf(attendance.getFirstDepart())));
		    for(Depart depart:list){
		    	departList.add(Integer.valueOf(String.valueOf(depart.getId())));
		    }
		    attendance.setDepartList(departList);
		}
		PageModel<EmpApplicationAbnormalAttendance> pm=new PageModel<EmpApplicationAbnormalAttendance>();
		pm.setRows(new java.util.ArrayList<EmpApplicationAbnormalAttendance>());
		try {
			attendance.setAssignee(String.valueOf(userService.getCurrentUser().getEmployee().getId()));
			pm = empApplicationAbnormalAttendanceService.getReportPageList(attendance);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	//获取上下班考勤结果
	public Map<String, String> getAttnStatistics(EmpApplicationAbnormalAttendance attn,Map<Long,String> workTypeMap, Map<Long,String> schedulingMap) {
		
		Map<String, String> map = new HashMap<String, String>();
		EmployeeClass employeeClass1 = new EmployeeClass(); 
		employeeClass1.setEmployId(attn.getEmployeeId());
		employeeClass1.setClassDate(attn.getAbnormalDate());
		employeeClass1 = employeeClassService.getEmployeeClassSetting(employeeClass1,workTypeMap,schedulingMap);
		Date ecStartTime = new Date();
		Date ecEndTime = new Date();
		if(employeeClass1 != null) {
			String fmtS = DateUtils.format(attn.getAbnormalDate(), "yyyy-MM-dd") + " " +  DateUtils.format(employeeClass1.getStartTime(), "HH:mm:ss");
			String fmtE = DateUtils.format(attn.getAbnormalDate(), "yyyy-MM-dd") + " " +  DateUtils.format(employeeClass1.getEndTime(), "HH:mm:ss");
			if("标准工时".equals(attn.getWorkType1())){
				ecStartTime = DateUtils.addMinute(DateUtils.parse(fmtS), 5);
			}else{
				ecStartTime = DateUtils.parse(fmtS);
			}
			ecEndTime = DateUtils.parse(fmtE);
			//开始时间小于开始时间
			int startWorkTime = DateUtils.compareDate(ecStartTime, ecEndTime);
			if(startWorkTime == 1) {
				ecEndTime = DateUtils.addDay(ecEndTime, 1);
			}
			if(attn.getStartPunchTime()!=null){
				//开始时间小于开始时间
				if(DateUtils.compareDate(attn.getStartPunchTime(), ecStartTime) != 1) {
					map.put("startResult", "正常");
				}else{
					map.put("startResult", "异常");
				}
			}else{
				map.put("startResult", "空卡");
			}
			if(attn.getEndPunchTime()!=null){
				if(DateUtils.compareDate(attn.getEndPunchTime(),ecEndTime) != 2) {
					map.put("endResult", "正常");
				}else{
					map.put("endResult", "异常");
				}
			}else{
				map.put("endResult", "空卡");
			}
		}else{
			map.put("startResult", "正常");
			map.put("endResult", "正常");
		}
		return map;
	}
	
	/**
	 * 导出
	 * @param model
	 * @param response
	 * @throws UnsupportedEncodingException
	 */
	@ResponseBody
	@RequestMapping("/exportReport.htm")
	public void exportReport(EmpApplicationAbnormalAttendance attendance, HttpServletResponse response,HttpServletRequest request) throws IOException{
	
		OutputStream os = null;
		try{
			attendance.setCode(URLDecoder.decode(attendance.getCode(),"UTF-8"));
			attendance.setCnName(URLDecoder.decode(attendance.getCnName(),"UTF-8"));
			attendance.setApplyName(URLDecoder.decode(attendance.getApplyName(),"UTF-8"));
			List<Integer> departList = new ArrayList<Integer>();
			if(attendance.getSecondDepart()!=null){
				departList.add(attendance.getSecondDepart());
				attendance.setDepartList(departList);
			}else if(attendance.getSecondDepart()==null&&attendance.getFirstDepart()!=null){
				departList.add(attendance.getFirstDepart());
				List<Depart> list = departService.getByParentId(Long.valueOf(String.valueOf(attendance.getFirstDepart())));
			    for(Depart depart:list){
			    	departList.add(Integer.valueOf(String.valueOf(depart.getId())));
			    }
			    attendance.setDepartList(departList);
			}
			List<EmpApplicationAbnormalAttendance> list = empApplicationAbnormalAttendanceService.getExportReportList(attendance);
			Map<String, String> map = new HashMap<String, String>();
			//1.准备工时类型map，key:配置ID，value：配置编码
			CompanyConfig companyConfigConditon = new CompanyConfig();//查询配置表条件
			companyConfigConditon.setCode("typeOfWork");//工时类型
			List<CompanyConfig> workTypeList = companyConfigService.getListByCondition(companyConfigConditon);
			
			Config configCondition = new Config();
			configCondition.setCode("whetherScheduling");//是否排班
			List<Config> whetherSchedulingList = configService.getListByCondition(configCondition);
			
	        Map<Long,String> workTypeMap = new HashMap<Long,String>();//存放配置的map
	        Map<Long,String> schedulingMap = new HashMap<Long,String>();//存放配置的map
	        
	        for(CompanyConfig cConfig:workTypeList){
	        	workTypeMap.put(cConfig.getId(), cConfig.getDisplayCode());
	        }
	        for(Config config:whetherSchedulingList){
	        	schedulingMap.put(config.getId(), config.getDisplayCode());
	        }
			for(EmpApplicationAbnormalAttendance attn:list){
				map = getAttnStatistics(attn,workTypeMap,schedulingMap);
				attn.setStartResult(map.get("startResult"));
				attn.setEndResult(map.get("endResult"));
			}
			HSSFWorkbook workbook = empApplicationAbnormalAttendanceService.exportExcel(list);
			ResponseUtil.setDownLoadExeclInfo(response,request,"异常考勤_");
			os = response.getOutputStream();
			workbook.write(os);
            os.flush();
            os.close();
		}catch(Exception e){
			logger.error("导出异常考勤报表失败,原因=",e);
		}finally{
			if(null != os){
				os.flush();
				os.close();
			}
		}
    }
	//考勤已办
	@ResponseBody
	@RequestMapping("/myAttnTaskList.htm")
	public PageModel<EmpApplicationAbnormalAttendance> myAttnTaskList(EmpApplicationAbnormalAttendance attendance){
	
		List<Integer> departList = new ArrayList<Integer>();
		User user = userService.getCurrentUser();
		attendance.setAssignee(user.getEmployeeId().toString());
		if(attendance.getSecondDepart()!=null){
			departList.add(attendance.getSecondDepart());
			attendance.setDepartList(departList);
		}else if(attendance.getSecondDepart()==null&&attendance.getFirstDepart()!=null){
			departList.add(attendance.getFirstDepart());
			List<Depart> list = departService.getByParentId(Long.valueOf(String.valueOf(attendance.getFirstDepart())));
		    for(Depart depart:list){
		    	departList.add(Integer.valueOf(String.valueOf(depart.getId())));
		    }
		    attendance.setDepartList(departList);
		}
		PageModel<EmpApplicationAbnormalAttendance> pm=new PageModel<EmpApplicationAbnormalAttendance>();
		try {
			attendance.setAssignee(String.valueOf(userService.getCurrentUser().getEmployee().getId()));
			pm = empApplicationAbnormalAttendanceService.myAttnTaskList(attendance);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	//审批 多选 确认操作
	@ResponseBody
	@RequestMapping("/completeAttn.htm")
	public Map<String,Object> completeAttn(HttpServletRequest request,String processIds,String comment,String commentType){
		
		Map<String,Object> result = Maps.newHashMap();
		try {
			for(String processId:processIds.split(",")){
				this.empApplicationAbnormalAttendanceService.completeTask(request,processId, comment, commentType);
			}
			result.put("sucess", true);
		} catch (OaException e) {
			result.put("sucess", false);
			result.put("msg", e.getMessage());
		}catch (Exception e) {
			result.put("sucess", false);
			result.put("msg", "审批考勤出现问题,与开发人员联系");
		}
		return result;
	}
	//详情 div
	@ResponseBody
	@RequestMapping("/getAttnDetail.htm")
	public Map<String,Object> getLeaveDetail(String processId){
		
		Map<String,Object> result = Maps.newHashMap();
		try {
			//假期申请单信息
			EmpApplicationAbnormalAttendance attn = empApplicationAbnormalAttendanceService.queryByProcessInstanceId(processId);
			result.put("attn", attn);	
			//流程信息
			List<ViewTaskInfoTbl> taskFlows = userTaskService.queryTaskFlow(processId,attn.getApprovalStatus());
			result.put("flows", taskFlows);
			result.put("sucess", true);
		} catch (Exception e) {
			result.put("sucess", false);
			result.put("msg", "考勤申请单有问题,与开发人员联系");
		}
		return result;
	}
	
	//异常考勤失效列表
	@ResponseBody
	@RequestMapping("/getOverdueListByPage.htm")
	public PageModel<EmpApplicationAbnormalAttendance> getOverdueListByPage(EmpApplicationAbnormalAttendance attendance){
		
		attendance.setApprovalStatus(ConfigConstants.OVERDUE_STATUS);
		List<Integer> departList = new ArrayList<Integer>();
		if(attendance.getSecondDepart()!=null){
			departList.add(attendance.getSecondDepart());
			attendance.setDepartList(departList);
		}else if(attendance.getSecondDepart()==null&&attendance.getFirstDepart()!=null){
			departList.add(attendance.getFirstDepart());
			List<Depart> list = departService.getByParentId(Long.valueOf(String.valueOf(attendance.getFirstDepart())));
		    for(Depart depart:list){
		    	departList.add(Integer.valueOf(String.valueOf(depart.getId())));
		    }
		    attendance.setDepartList(departList);
		}
		PageModel<EmpApplicationAbnormalAttendance> pm=new PageModel<EmpApplicationAbnormalAttendance>();
		pm.setRows(new java.util.ArrayList<EmpApplicationAbnormalAttendance>());
		try {
			attendance.setAssignee(String.valueOf(userService.getCurrentUser().getEmployee().getId()));
			pm = empApplicationAbnormalAttendanceService.getReportPageList(attendance);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	//考勤管理-异常考勤管理-异常考勤查询-失效同意
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/passInvalidAbnormalAttendance.htm")
	public JSON passInvalidAbnormalAttendance(HttpServletRequest request,String processId,String comment){
		
		Map<String,Object> map = new HashMap<String,Object>();		
		try{
			userTaskService.completeTaskByAdmin(request,processId,comment,ConfigConstants.OVERDUEPASS);	
			map.put("message", "操作成功");
			map.put("flag", true);									
		}catch(Exception e){
			logger.error("passInvalidAbnormalAttendance失败：",e);
			map.put("flag", false);
			map.put("message", "操作失败,"+e.getMessage());
		}		
		return JsonWriter.successfulResult(map);
	}
	
	//考勤管理-异常考勤管理-异常考勤查询-失效拒绝
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/refuseInvalidAbnormalAttendance.htm")
	public JSON refuseInvalidAbnormalAttendance(HttpServletRequest request,String processId,String comment){
		
		Map<String,Object> map = new HashMap<String,Object>();		
		try{
			userTaskService.completeTaskByAdmin(request,processId,comment,ConfigConstants.OVERDUEREFUSE);	
			map.put("message", "操作成功");
			map.put("flag", true);									
		}catch(Exception e){
			logger.error("refuseInvalidAbnormalAttendance失败：",e);
			map.put("flag", false);
			map.put("message", "操作失败,"+e.getMessage());
		}		
		return JsonWriter.successfulResult(map);
	}
	
	@ResponseBody
	@RequestMapping("/updateApprovalStatus.htm")
	public JSON updateApprovalStatus(HttpServletRequest request,Long id,Integer approvalStatus){
		
		Map<String,Object> map = new HashMap<String,Object>();		
		try{
			empApplicationAbnormalAttendanceService.updateApprovalStatusById(id, approvalStatus);
			map.put("message", "操作成功");
			map.put("flag", true);									
		}catch(Exception e){
			map.put("flag", false);
			map.put("message", "操作失败,"+e.getMessage());
		}		
		return JsonWriter.successfulResult(map);
	}
	
	
}
