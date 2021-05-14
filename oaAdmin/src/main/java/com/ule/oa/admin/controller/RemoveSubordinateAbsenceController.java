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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.google.common.collect.Maps;
import com.ule.oa.admin.util.IsMenu;
import com.ule.oa.admin.util.IsOperation;
import com.ule.oa.admin.util.ResponseUtil;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.RemoveSubordinateAbsence;
import com.ule.oa.base.po.tbl.ViewTaskInfoTbl;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.RemoveSubordinateAbsenceService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.service.UserTaskService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.PageModel;


@Controller
@RequestMapping("removeSubordinateAbsence")
public class RemoveSubordinateAbsenceController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private DepartService departService;
	@Autowired
	private RemoveSubordinateAbsenceService removeSubordinateAbsenceService;
	@Autowired
	private UserTaskService userTaskService;
	@Autowired
	private UserService userService;
	
	
	//考勤管理——>延时工作管理——>消下属缺勤页面
	@IsMenu
	@RequestMapping("/getRemoveSubordinateAbsence.htm")
	public ModelAndView empEliminateSubordinateAttendance(HttpServletRequest request){

		List<Depart> departList = departService.getSubordinateDepartList();
		String thisMonth = DateUtils.getYearAndMonth(new Date());
		request.setAttribute("departList", departList);
		request.setAttribute("thisMonth", thisMonth);
		return new ModelAndView("base/delayWork/removeSubordinateAbsence");
	}
	//考勤管理——>延时工作管理——>消下属缺勤审批页面
	@IsMenu 
	@RequestMapping("/removeAbsenceApproval.htm")
	public ModelAndView empEliminateSubordinateAttendanceApproval(HttpServletRequest request){

		List<Depart> departList = departService.getSubordinateDepartList();
		String thisMonth = DateUtils.getYearAndMonth(new Date());
		request.setAttribute("departList", departList);
		request.setAttribute("thisMonth", thisMonth);
		return new ModelAndView("base/delayWork/removeSubordinateAbsenceAudit");
	}
	//考勤管理——>延时工作管理——>消下属缺勤查询页面
	@IsMenu
	@RequestMapping("/removeAbsenceQuery.htm")
	public ModelAndView empEliminateSubordinateAttendanceQuery(HttpServletRequest request){

		List<Depart> departList = departService.getSubordinateDepartList();
		String thisMonth = DateUtils.getYearAndMonth(new Date());
		request.setAttribute("departList", departList);
		request.setAttribute("thisMonth", thisMonth);
		return new ModelAndView("base/delayWork/removeSubordinateAbsenceQuery");
	}
	
	//考勤管理-延时工作管理-消下属考勤查询
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/getReportPageList.htm")
	public PageModel<RemoveSubordinateAbsence> getReportPageList(RemoveSubordinateAbsence attendanceQuery){

		PageModel<RemoveSubordinateAbsence> pm=new PageModel<RemoveSubordinateAbsence>();
		pm.setRows(new java.util.ArrayList<RemoveSubordinateAbsence>());
		attendanceQuery.setSoftType(2);
		try {
			pm = removeSubordinateAbsenceService.getReportPageList(attendanceQuery);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return pm;
	}
	//考勤管理——>延时工作管理——>消下属缺勤查询、审批获取流程信息
	@ResponseBody
	@RequestMapping("/getProcessbyId.htm")
	public Map<String,Object> getProcessbyId(String processId){
		Map<String,Object> result = Maps.newHashMap();
		try {
			//假期申请单信息
			RemoveSubordinateAbsence subAttn = removeSubordinateAbsenceService.queryByProcessInstanceId(processId);
			result.put("subAttn", subAttn);	
			//流程信息
			List<ViewTaskInfoTbl> taskFlows = userTaskService.queryTaskFlow(processId,subAttn.getApproalStatus().intValue());
			result.put("flows", taskFlows);
			result.put("sucess", true);
		} catch (Exception e) {
			result.put("sucess", false);
			result.put("msg", "考勤申请单有问题,与开发人员联系");
		}
		return result;
	}
	
	
	//考勤管理-延时工作管理-消下属缺勤-查询
	@IsOperation(returnType=true)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/getRemoveSubordinateAbsencePage.htm") 
	public Map<String,Object> getRemoveSubordinateAbsencePage(Long departId,String month,String empCode,String empCnName,Integer page,Integer rows){

		Map<String,Object> result = new HashMap<String,Object>();
		//分页
		PageModel<Map<String,Object>> pm=new PageModel<Map<String,Object>>();
		pm.setRows(new java.util.ArrayList<Map<String,Object>>());
		try{
			result = removeSubordinateAbsenceService.getRemoveSubordinateAbsencePage(departId,month,empCode,empCnName,page,rows);
		}catch(Exception e){
			logger.error("getRemoveSubordinateAbsencePage:",e);
			result.put("message",e.getMessage());
			result.put("success",false);
		}
		return result;
	}
	
	/**
	 * 获取登记详情
	 * @param empId
	 * @param delayDate
	 * @return
	 */
	//考勤管理——>延时工作管理——>消下属缺勤 查询缺勤详情信息
	@ResponseBody
	@RequestMapping("/getAttnDetail.htm") 
	public Map<String,Object> getAttnDetail(Long empId,String attnDate){
		
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			Date date = DateUtils.parse(attnDate,DateUtils.FORMAT_SHORT);
			result = removeSubordinateAbsenceService.getAttnDetail(empId,date);
		}catch(Exception e){
			logger.error("getAttnDetail:",e);
			result.put("message",e.getMessage());
			result.put("success",false);
		}
		return result;
	}
	
	/**
	 * 提交消下属缺勤
	 * @param departId
	 * @param year
	 * @param vacation
	 * @return
	 */
	//考勤管理-延时工作管理-消下属缺勤-提交
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping(value="/commitApplicationForm.htm")
	public Map<String,Object> commitApplicationForm(HttpServletRequest request,RemoveSubordinateAbsence removeSubordinateAbsence){
	
		Map<String,Object> result = new HashMap<String,Object>();
		try {
			//根据节假日和年份查出节假日日期
			removeSubordinateAbsenceService.commitApplicationForm(request,removeSubordinateAbsence);
			result.put("success", true);
			result.put("message", "提交成功！");
		} catch (OaException o) {
			logger.error("commitRemoveSubordinateAbsence:",0);
			result.put("success", false);
			result.put("message", o.getMessage());
		}catch (Exception e) {
			logger.error("commitRemoveSubordinateAbsence:",e);
			result.put("success", false);
			result.put("message", "网络延迟，请稍后重试！");
		}
		return result;
	}
	
	/**
	 * 消下属缺勤审批
	 * @param processIds
	 * @param comment
	 * @param commentType
	 * @return
	 */
	//考勤管理-延时工作管理-消下属缺勤-审批
	@ResponseBody
	@RequestMapping("/completeTask.htm")
	public Map<String,Object> completeTask(HttpServletRequest request,@RequestParam("processIds")List<String> processIds,String comment,String commentType){
		Map<String,Object> result = Maps.newHashMap();
		try {
			logger.info("completeTask参数:processIds="+processIds+";comment="+comment+";commentType="+commentType);
			if(processIds == null || processIds.size() == 0){
				throw new OaException("请选择审批单据！");
			}
			for (String processId : processIds) {
				removeSubordinateAbsenceService.completeTask(request,processId, comment, commentType);
			}
			result.put("sucess", true);
			result.put("msg", "操作成功！");
		} catch (OaException e) {
			result.put("sucess", false);
			result.put("msg", e.getMessage());
		}catch (Exception e) {
			result.put("sucess", false);
			result.put("msg", "审批出现问题,与开发人员联系");
		}
		return result;
	}
	
	/**
	 * 消下属缺勤审批
	 * @param processIds
	 * @param comment
	 * @param commentType
	 * @return
	 */
	//考勤管理-延时工作管理-消下属缺勤-审批 失效同意
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/completePass.htm")
	public Map<String,Object> completePass(HttpServletRequest request,@RequestParam("processIds")List<String> processIds,String comment,String commentType){
		Map<String,Object> result = Maps.newHashMap();
		try {
			if(processIds == null || processIds.size() == 0){
				throw new OaException("请选择审批单据！");
			}
			for (String processId : processIds) {
				removeSubordinateAbsenceService.completeTask(request,processId, comment, commentType);
			}
			result.put("sucess", true);
			result.put("msg", "操作成功！");
		} catch (OaException e) {
			result.put("sucess", false);
			result.put("msg", e.getMessage());
		}catch (Exception e) {
			result.put("sucess", false);
			result.put("msg", "审批出现问题,与开发人员联系");
		}
		return result;
	}
	
	/**
	 * 消下属缺勤审批
	 * @param processIds
	 * @param comment
	 * @param commentType
	 * @return
	 */
	//考勤管理-延时工作管理-消下属缺勤-审批 失效拒绝
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/completeRefuse.htm")
	public Map<String,Object> completeRefuse(HttpServletRequest request,@RequestParam("processIds")List<String> processIds,String comment,String commentType){
		Map<String,Object> result = Maps.newHashMap();
		try {
			if(processIds == null || processIds.size() == 0){
				throw new OaException("请选择审批单据！");
			}
			for (String processId : processIds) {
				removeSubordinateAbsenceService.completeTask(request,processId, comment, commentType);
			}
			result.put("sucess", true);
			result.put("msg", "操作成功！");
		} catch (OaException e) {
			result.put("sucess", false);
			result.put("msg", e.getMessage());
		}catch (Exception e) {
			result.put("sucess", false);
			result.put("msg", "审批出现问题,与开发人员联系");
		}
		return result;
	}
	
	
	//考勤管理-延时工作管理-消下属缺勤审批 代办查询
	@ResponseBody
	@RequestMapping("/getPendingListByPage.htm")
	public PageModel<RemoveSubordinateAbsence> getHandlingListByPage(RemoveSubordinateAbsence subAttn){
		
		PageModel<RemoveSubordinateAbsence> pm=new PageModel<RemoveSubordinateAbsence>();
		pm.setRows(new java.util.ArrayList<RemoveSubordinateAbsence>());
		try {
			subAttn.setSoftType(1);
			subAttn.setAssignee(String.valueOf(userService.getCurrentUser().getEmployee().getId()));
			subAttn.setApproalStatus(ConfigConstants.DOING_STATUS.longValue());
			pm = removeSubordinateAbsenceService.getReportPageList(subAttn);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	//考勤管理-延时工作管理-消下属缺勤审批 已办查询
	@ResponseBody
	@RequestMapping("/getDoneListByPage.htm")
	public PageModel<RemoveSubordinateAbsence> getDoneListByPage(RemoveSubordinateAbsence subAttn){
	
		PageModel<RemoveSubordinateAbsence> pm=new PageModel<RemoveSubordinateAbsence>();
		pm.setRows(new java.util.ArrayList<RemoveSubordinateAbsence>());
		
		try {
			subAttn.setAssignee(String.valueOf(userService.getCurrentUser().getEmployee().getId()));
			List<Long> approalStatusList=new ArrayList<Long>();
			approalStatusList.add(ConfigConstants.PASS_STATUS.longValue());
			approalStatusList.add(ConfigConstants.REFUSE_STATUS.longValue());
			approalStatusList.add(ConfigConstants.OVERDUEPASS_STATUS.longValue());
			approalStatusList.add(ConfigConstants.OVERDUEREFUSE_STATUS.longValue());
			subAttn.setApproalStatusList(approalStatusList);
			pm = removeSubordinateAbsenceService.getReportPageList(subAttn);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	//考勤管理-延时工作管理-消下属缺勤审批 失效查询
	@ResponseBody
	@RequestMapping("/getInvalidListByPage.htm")
	public PageModel<RemoveSubordinateAbsence> getInvalidListByPage(RemoveSubordinateAbsence subAttn){
	
		PageModel<RemoveSubordinateAbsence> pm=new PageModel<RemoveSubordinateAbsence>();
		pm.setRows(new java.util.ArrayList<RemoveSubordinateAbsence>());
		try {
			subAttn.setSoftType(1);
			subAttn.setAssignee(String.valueOf(userService.getCurrentUser().getEmployee().getId()));
			subAttn.setApproalStatus(ConfigConstants.OVERDUE_STATUS.longValue());
			pm = removeSubordinateAbsenceService.getReportPageList(subAttn);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	/**
	 * 导出
	 * @param model
	 * @param response
	 * @throws UnsupportedEncodingException
	 */
	//考勤管理-延时工作管理-消下属缺勤查询 导出
	@IsOperation(returnType=true)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/exportReport.htm")
	public void exportReport(RemoveSubordinateAbsence subAttn, HttpServletResponse response,HttpServletRequest request) throws IOException{
	
		OutputStream os = null;
		try{
			subAttn.setEmpCode(URLDecoder.decode(subAttn.getEmpCode(),"UTF-8"));
			subAttn.setEmpName(URLDecoder.decode(subAttn.getEmpName(),"UTF-8"));
			HSSFWorkbook workbook = removeSubordinateAbsenceService.exportExcel(subAttn);
			ResponseUtil.setDownLoadExeclInfo(response,request,"消下属缺勤_");
			if(workbook!=null){
				os = response.getOutputStream();
				workbook.write(os);
			}
		}catch(Exception e){
			logger.error("导出消下属缺勤报表失败,原因=",e);
		}finally{
			if(null != os){
				os.flush();
				os.close();
			}
		}
    }
}
