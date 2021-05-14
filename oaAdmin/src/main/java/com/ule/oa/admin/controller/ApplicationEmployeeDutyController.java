package com.ule.oa.admin.controller;

import java.io.IOException;
import java.io.OutputStream;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import com.google.common.collect.Maps;
import com.ule.oa.admin.util.IsMenu;
import com.ule.oa.admin.util.IsOperation;
import com.ule.oa.admin.util.ResponseUtil;
import com.ule.oa.base.common.OaCommon;
import com.ule.oa.base.po.ApplicationEmployeeDuty;
import com.ule.oa.base.po.ApplicationEmployeeDutyDetail;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.AnnualVacationService;
import com.ule.oa.base.service.ApplicationEmployeeDutyService;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.PageModel;
import com.ule.oa.common.utils.json.JSONUtils;

/**
 * @ClassName: 法定节假日值班管理
 * @Description: 法定节假日值班管理
 * @author yangjie
 * @date 2019年05月16日
 */

@Controller
@RequestMapping("duty")
public class ApplicationEmployeeDutyController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private ApplicationEmployeeDutyService applicationEmployeeDutyService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private DepartService departService;
	
	@Autowired
	private AnnualVacationService annualVacationService;
	
	public final static String THIS_YEAR = "thisYear";
	
	public final static String ERROR_MESSAGE = "网络延迟，请稍后重试！";
	
	
	//考勤管理-法定节假日值班管理-值班查询-页面
	@IsMenu
	@RequestMapping("/dutyQuery.htm")
	public ModelAndView index(){
		String thisYear = DateUtils.getYear(new Date());
		ModelAndView htm = new ModelAndView("base/employeeClass/dutyQuery");
		htm.addObject(THIS_YEAR, thisYear);
		return htm;
	}
	
	//考勤管理-法定节假日值班管理-值班查询-查询
	@IsOperation(returnType=false)//需要校验操作权限
	@RequestMapping("/queryDuty.htm")
	@ResponseBody
	public Map<String,Object> queryDuty(String vacationName,String year,Long departId) {
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			result = applicationEmployeeDutyService.queryDuty(vacationName, year, departId);
		}catch(Exception e){
			result.put(OaCommon.CODE_SUCCESS, false);
			result.put(OaCommon.CODE_MESSAGE, e.getMessage());
			logger.error("queryDuty:",e);
		}
		return result;
		
	}
	
	//考勤管理-法定节假日值班管理-值班查询-查看
	@RequestMapping("/showDutyDetail.htm")
	@ResponseBody
	public Map<String,Object> showDutyDetail(String vacationName,String year,Long departId){
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			result = applicationEmployeeDutyService.showDutyDetail(departId, year,vacationName);
		}catch(Exception e){
			result.put(OaCommon.CODE_SUCCESS, false);
			result.put(OaCommon.CODE_MESSAGE, e.getMessage());
			logger.error("showDutyDetail:",e);
		}
		return result;
	}
	
	//考勤管理-法定节假日值班管理-值班查询-提交
	@IsOperation(returnType=false)//需要校验操作权限
	@RequestMapping("/commitDutyByHr.htm")
	@ResponseBody
	public Map<String,Object> commitDutyByHr(HttpServletRequest request,String departId,String year,String vacationName,String info){
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			result = applicationEmployeeDutyService.commitDutyByHr(request,departId, year,vacationName,info);
		}catch(Exception e){
			result.put(OaCommon.CODE_SUCCESS, false);
			result.put(OaCommon.CODE_MESSAGE, e.getMessage());
			logger.error("commitDutyByHr:",e);
		}
		return result;
	}
	
	//考勤管理-法定节假日值班管理-值班查询-提交
	@RequestMapping("/showHistoryDetail.htm")
	@ResponseBody
	public Map<String,Object> showHistoryDetail(Long id){
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			result = applicationEmployeeDutyService.showHistoryDetail(id);
		}catch(Exception e){
			result.put(OaCommon.CODE_SUCCESS, false);
			result.put(OaCommon.CODE_MESSAGE, e.getMessage());
			logger.error("showHistoryDetail:",e);
		}
		return result;
	}
	
	//考勤管理-法定节假日值班管理-值班查询-导出
	@IsOperation(returnType=true)//需要校验操作权限
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
			
		
	/**
	 * 值班
	 * @param request
	 * @return
	 */
	@IsMenu
	@RequestMapping("/duty.htm")
	public ModelAndView duty(HttpServletRequest request){
		//查询登录人负责的所有部门
		User user = userService.getCurrentUser();
		//部门负责人只可排自己部门下的人员
		List<Depart> departList = departService.getAllDepartByLeaderId(user.getEmployee().getId());
		if(departList == null || departList.isEmpty()){
			//非部门负责人(暂定非部门负责人即为人事)可排所有部门
			departList = departService.getAllDepart();
		}
		String thisYear = DateUtils.getYear(new Date());
		request.setAttribute("departList", departList);
		request.setAttribute(THIS_YEAR, thisYear);
		return new ModelAndView("base/employeeDuty/duty");
	}
	
	//导出值班模板
	@ResponseBody
	@RequestMapping("/exportDutyTemplate.htm")
	public void exportDutyTemplate(HttpServletResponse response,HttpServletRequest request,Long departId,String year,String vacation) throws IOException{
		OutputStream os = null;
		try {
			ResponseUtil.setDownLoadExeclInfo(response,request,"值班上传模板");
			HSSFWorkbook hSSFWorkbook = applicationEmployeeDutyService.exportScheduleTemplate(departId,year,vacation);
			os = response.getOutputStream();
			hSSFWorkbook.write(os);
		}catch (Exception e) {
			logger.error("下载值班上传模板失败,原因=",e);
		}finally{
			if(null != os){
				os.flush();
				os.close();
			}
		}
	}
	//法定节假日值班管理-值班-值班-新增值班
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/getUnCommitDuty.htm") 
	public Map<String,Object> getUnCommitDuty(Long departId,String year,String vacation){
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			result = applicationEmployeeDutyService.getUnCommitDuty(departId,year,vacation);
		}catch(OaException o){
			logger.error("getUnCommitDuty:",o);
			result.put(OaCommon.CODE_MESSAGE,o.getMessage());
			result.put(OaCommon.CODE_SUCCESS,false);
		}catch(Exception e){
			logger.error("getUnCommitDuty:",e);
			result.put(OaCommon.CODE_MESSAGE,e.getMessage());
			result.put(OaCommon.CODE_SUCCESS,false);
		}
		return result;
	}

	//值班审核查询页面
	@RequestMapping("/audit.htm")
	public ModelAndView audit(HttpServletRequest request){
		//查询登录人负责的所有部门
		User user = userService.getCurrentUser();
		//部门负责人只可排自己部门下的人员
		List<Depart> departList = departService.getAllDepartByLeaderId(user.getEmployee().getId());

		if(departList == null || departList.isEmpty()){
			//非部门负责人(暂定非部门负责人即为人事)可排所有部门
			departList = departService.getAllDepart();
		}
		String thisYear = DateUtils.getYear(new Date());
		request.setAttribute("departList", departList);
		request.setAttribute(THIS_YEAR, thisYear);
		return new ModelAndView("base/employeeDuty/audit");
	}	

	//值班申请待办分页查询
	@ResponseBody
	@RequestMapping("/getHandlingListByPage.htm")
	public PageModel<ApplicationEmployeeDuty> getHandlingListByPage(ApplicationEmployeeDuty employeeDuty){
		PageModel<ApplicationEmployeeDuty> pm=new PageModel<ApplicationEmployeeDuty>();
		pm.setRows(new java.util.ArrayList<ApplicationEmployeeDuty>());
		try {
			employeeDuty.setAssignee(String.valueOf(userService.getCurrentUser().getEmployee().getId()));
			employeeDuty.setApprovalStatus(ConfigConstants.DOING_STATUS);
			pm = applicationEmployeeDutyService.getHandlingListByPage(employeeDuty);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	//值班申请已办分页查询
	@ResponseBody
	@RequestMapping("/getHandledListByPage.htm")
	public PageModel<ApplicationEmployeeDuty> getHandledListByPage(ApplicationEmployeeDuty employeeDuty){
		PageModel<ApplicationEmployeeDuty> pm=new PageModel<ApplicationEmployeeDuty>();
		pm.setRows(new java.util.ArrayList<ApplicationEmployeeDuty>());
		employeeDuty.setAssignee(String.valueOf(userService.getCurrentUser().getEmployee().getId()));
		try {
			List <Integer> intList=new ArrayList<Integer>();
			intList.add(ConfigConstants.PASS_STATUS);
			intList.add(ConfigConstants.REFUSE_STATUS);
			employeeDuty.setApprovalStatusList(intList);
			pm = applicationEmployeeDutyService.getDutyTaskListByPage(employeeDuty);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	//值班申请失效分页查询
	@ResponseBody
	@RequestMapping("/getInvalidListByPage.htm")
	public PageModel<ApplicationEmployeeDuty> getInvalidListByPage(ApplicationEmployeeDuty employeeDuty){
		PageModel<ApplicationEmployeeDuty> pm=new PageModel<ApplicationEmployeeDuty>();
		pm.setRows(new java.util.ArrayList<ApplicationEmployeeDuty>());
		employeeDuty.setAssignee(String.valueOf(userService.getCurrentUser().getEmployee().getId()));
		try {
			employeeDuty.setApprovalStatus(ConfigConstants.OVERDUE_STATUS);
			pm = applicationEmployeeDutyService.getHandlingListByPage(employeeDuty);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	//值班申请待办分页查询
	@ResponseBody
	@RequestMapping("/getDetailById.htm")
	public Map<String,Object> getDetailById(Long id){
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			result = applicationEmployeeDutyService.getDetailById(id);
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
				applicationEmployeeDutyService.completeTask(request,processId, comment, commentType);
			}
			result.put(OaCommon.CODE_SUCCESS, true);
		} catch (OaException e) {
			result.put(OaCommon.CODE_SUCCESS, false);
			result.put(OaCommon.CODE_MESSAGE, e.getMessage());
		}catch (Exception e) {
			result.put(OaCommon.CODE_SUCCESS, false);
			result.put(OaCommon.CODE_MESSAGE, "审批假期出现问题,与开发人员联系");
		}
		return result;
	}
	
	//导出值班申请单详情数据
	@ResponseBody
	@RequestMapping("/exportDetailById.htm")
	public void exportDetailById(Long id,HttpServletResponse response,HttpServletRequest request) throws IOException{
		OutputStream os = null;
		try {
			ResponseUtil.setDownLoadExeclInfo(response,request,"值班审核_");
			HSSFWorkbook hSSFWorkbook = applicationEmployeeDutyService.exportDutyDetailById(id);
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
	
	@ResponseBody
	@RequestMapping("/getDutyDateList.htm")
	public Map<String,Object> getDutyDateList(HttpServletRequest request,String year,String vacation){
		Map<String,Object> result = new HashMap<String,Object>();
		try {
			//根据节假日和年份查出节假日日期
			List<Map<String,Object>> dateList = annualVacationService.getAnnualDateByYearAndSubject(year,vacation);
			result.put(OaCommon.CODE_SUCCESS, true);
			result.put("dateList", dateList);
		} catch (Exception e) {
			result.put(OaCommon.CODE_SUCCESS, false);
			result.put(OaCommon.CODE_MESSAGE, ERROR_MESSAGE);
		}
		return result;
	}
	/**
	 * 查询组内所有员工信息
	 * @param request
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getEmpListByDepartAndCondition.htm")
	public String getEmpListByDepartAndCondition(Long departId,String condition){
		//查询部门下所有在职员工
		List<Employee> empList = departService.getEmpListByDepartAndCondition(departId,condition);
		return JSONUtils.write(empList);
	}
	
	
	/**
	 * 保存值班安排
	 * @param departId
	 * @param year
	 * @param vacation
	 * @param dutyDetailList
	 * @return
	 */
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping(value="/saveApplicationDutyDetail.htm")
	public Map<String,Object> saveApplicationDutyDetail(Long departId, String year, String vacation,@RequestBody List<ApplicationEmployeeDutyDetail> dutyDetailList){
		Map<String,Object> result = new HashMap<String,Object>();
		try {
			//根据节假日和年份查出节假日日期
			applicationEmployeeDutyService.saveApplicationDutyDetail(departId,year,vacation,dutyDetailList);
			result.put(OaCommon.CODE_SUCCESS, true);
			result.put(OaCommon.CODE_SUCCESS, "保存成功！");
			
		} catch (OaException o) {
			result.put(OaCommon.CODE_SUCCESS, false);
			result.put(OaCommon.CODE_SUCCESS, o.getMessage());
		}catch (Exception e) {
			result.put(OaCommon.CODE_SUCCESS, false);
			result.put(OaCommon.CODE_SUCCESS, ERROR_MESSAGE);
		}
		
		return result;
	}
	
	
	/**
	 * 提交值班
	 * @param departId
	 * @param year
	 * @param vacation
	 * @return
	 */
	//考勤管理-法定节假日值班管理-值班-提交审核
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping(value="/commitDuty.htm")
	public Map<String,Object> commitDuty(HttpServletRequest request,Long departId, String year, String vacation){
		Map<String,Object> result = new HashMap<String,Object>();
		try {
			//根据节假日和年份查出节假日日期
			applicationEmployeeDutyService.commitDuty(request,departId,year,vacation);
			result.put(OaCommon.CODE_SUCCESS, true);
			result.put(OaCommon.CODE_MESSAGE, "提交成功！");
		} catch (OaException o) {
			result.put(OaCommon.CODE_SUCCESS, false);
			result.put(OaCommon.CODE_MESSAGE, o.getMessage());
		}catch (Exception e) {
			result.put(OaCommon.CODE_SUCCESS, false);
			result.put(OaCommon.CODE_MESSAGE, ERROR_MESSAGE);
		}
		return result;
	}
	
	//考勤管理-法定节假日值班管理-值班-值班记录
	@IsOperation(returnType=false)//需要校验操作权限
	@RequestMapping("/getDutyRecord.htm")
	@ResponseBody
	public PageModel<ApplicationEmployeeDuty> getDutyRecord(ApplicationEmployeeDuty requestParam) throws OaException{
		PageModel<ApplicationEmployeeDuty> pm=new PageModel<ApplicationEmployeeDuty>();
		try{
			pm.setRows(new java.util.ArrayList<ApplicationEmployeeDuty>());
			pm=applicationEmployeeDutyService.getDutyRecord(requestParam);
		}catch(Exception e){
			logger.error("getDutyRecord:"+e.getMessage());
		}
		return pm;
	}	
	
	//值班申请待办分页查询
	@ResponseBody
	@RequestMapping("/getDutyDetailById.htm")
	public Map<String,Object> getDutyDetailById(Long id){
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			result = applicationEmployeeDutyService.getDutyDetailById(id);
			result.put(OaCommon.CODE_SUCCESS,true);
		}catch(OaException o){
			result.put(OaCommon.CODE_SUCCESS,false);
			result.put(OaCommon.CODE_MESSAGE,o.getMessage());
		}catch(Exception e){
			result.put(OaCommon.CODE_SUCCESS,false);
			result.put(OaCommon.CODE_MESSAGE,ERROR_MESSAGE);
		}
		
		return result;
	}
	/**
	 * 考勤管理-排班管理-排班-上传排班
	 * @param file
	 * @param departId
	 * @param groupId
	 * @return
	 */
	//考勤管理-法定节假日值班管理-值班-上传值班
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/importDutyTemplate.htm")
	public Map<String, String> importDutyTemplate(@RequestParam(value="file",required = false)MultipartFile file,Long departId, String year, String vacationName) {
		String result = "failed";
		String resultMsg = null;
		try {
			Map<String,String> resultMap = applicationEmployeeDutyService.importDutyTemplate(file,departId,year,vacationName);
			result = resultMap.get("result"); 
			resultMsg =  resultMap.get("resultMsg");
		} catch (OaException o) {
			resultMsg = o.getMessage();
		} catch (Exception e) {
			resultMsg = "无法上传，请下载最新节假日模板，安排值班";
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("result", result);
		map.put("resultMsg", resultMsg);
        return map;
	}
}
