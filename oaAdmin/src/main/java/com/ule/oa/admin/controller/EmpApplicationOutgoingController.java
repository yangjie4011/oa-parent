package com.ule.oa.admin.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
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
import com.ule.oa.base.po.EmpApplicationOutgoing;
import com.ule.oa.base.po.User;
import com.ule.oa.base.po.tbl.ViewTaskInfoTbl;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmpApplicationOutgoingService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.service.UserTaskService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.PageModel;

/**
 * @ClassName: 外出申请
 * @Description: 外出申请
 * @author yangjie
 * @date 2017年6月9日
 */
@Controller
@RequestMapping("empApplicationOutgoing")
public class EmpApplicationOutgoingController {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private UserTaskService userTaskService;
	@Autowired
	private EmpApplicationOutgoingService empApplicationOutgoingService;
	@Autowired
	private UserService userService;
	@Autowired
	private DepartService departService;
	
	/**
	  * index(外出查询报表首页)
	  * @Title: index
	  * @Description: 外出查询报表首页
	  * @param request
	  * @return    设定文件
	  * String    返回类型
	  * @throws
	 */
	@IsMenu
	@RequestMapping("/index.htm")
	public ModelAndView index(HttpServletRequest request){
		return new ModelAndView("base/outgoingManage/outgoingQuery");
	}
	
	/**
	  * getReportPageList(考勤管理-考勤查询-外出查询-查询)
	  * @Title: getReportPageList
	  * @Description: 考勤管理-考勤查询-外出查询-查询
	  * @param empApplicationOutgoing
	  * @return    设定文件
	  * PageModel<EmpApplicationOutgoing>    返回类型
	  * @throws
	 */
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/getReportPageList.htm")
	public PageModel<EmpApplicationOutgoing> getReportPageList(EmpApplicationOutgoing empApplicationOutgoing){
		
		PageModel<EmpApplicationOutgoing> pm=new PageModel<EmpApplicationOutgoing>();
		pm.setRows(new java.util.ArrayList<EmpApplicationOutgoing>());
		
		try {
			pm = empApplicationOutgoingService.getReportPageList(empApplicationOutgoing);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	/**
	  * exportReport(考勤管理-考勤查询-外出查询-导出)
	  * @Title: exportReport
	  * @Description: 考勤管理-考勤查询-外出查询-导出
	  * @param empApplicationOutgoing
	  * @param response
	  * @throws IOException    设定文件
	  * void    返回类型
	  * @throws
	 */
	@IsOperation(returnType=true)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/exportReport.htm")
	public void exportReport(EmpApplicationOutgoing empApplicationOutgoing,HttpServletResponse response,HttpServletRequest request) throws IOException{//导出数据并发送邮件

		OutputStream os = null;
		try {
			empApplicationOutgoing.setCode(URLDecoder.decode(empApplicationOutgoing.getCode(),"UTF-8"));
			empApplicationOutgoing.setCnName(URLDecoder.decode(empApplicationOutgoing.getCnName(),"UTF-8"));
			
			ResponseUtil.setDownLoadExeclInfo(response,request,"外出查询_");
			HSSFWorkbook hSSFWorkbook = empApplicationOutgoingService.exportReport(empApplicationOutgoing);
			os = response.getOutputStream();
			if(hSSFWorkbook!=null){
				hSSFWorkbook.write(os);
				os.flush();
				os.close();
			}
		} catch (Exception e) {
			logger.error("导出外出查询报表失败,原因=",e);
		}finally{
			if(null != os){
				os.flush();
				os.close();
			}
		}
	}
	
	
	@RequestMapping("/outGoingTotal.htm")
	public ModelAndView outGoingTotal(){
		String year = DateUtils.getYear(DateUtils.getToday());
		return new ModelAndView("base/report/outGoingTotal","year",year);
	}
	
	@ResponseBody
	@RequestMapping("/getOutTotalPageList.htm")
	public PageModel<EmpApplicationOutgoing> getOutTotalPageList(EmpApplicationOutgoing empApplicationOutgoing){
		
		PageModel<EmpApplicationOutgoing> pm=new PageModel<EmpApplicationOutgoing>();
		pm.setRows(new java.util.ArrayList<EmpApplicationOutgoing>());
		
		try {
			pm = empApplicationOutgoingService.getOutTotalPageList(empApplicationOutgoing);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	@ResponseBody
	@RequestMapping("/exportOutGoingTotal.htm")
	public void exportOutGoingTotal(EmpApplicationOutgoing empApplicationOutgoing,HttpServletResponse response) throws IOException{//导出数据并发送邮件
		
		OutputStream os = null;
		try {
			empApplicationOutgoing.setCode(URLDecoder.decode(empApplicationOutgoing.getCode(),"UTF-8"));
			empApplicationOutgoing.setCnName(URLDecoder.decode(empApplicationOutgoing.getCnName(),"UTF-8"));
			
			//设置返回类型
			String fileName = URLEncoder.encode("外出汇总统计_" + DateUtils.format(new Date(),DateUtils.FORMAT_SIMPLE) + ".xls","UTF-8");
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-disposition", "attachment; filename="+fileName);
			
			HSSFWorkbook hSSFWorkbook = empApplicationOutgoingService.exportOutGoingTotal(empApplicationOutgoing);
			os = response.getOutputStream();
			hSSFWorkbook.write(os);
            os.flush();
            os.close();
		} catch (Exception e) {
			logger.error("导出外出查询报表失败,原因=",e);
		}finally{
			if(null != os){
				os.flush();
				os.close();
			}
		}
	}
	/**
	 * 外出申请单详情
	 * @param processId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getOutgoingDetail.htm")
	public Map<String,Object> getOutgoingDetail(String processInstanceId){

		Map<String,Object> result = Maps.newHashMap();
		try {
			//假期申请单信息
			EmpApplicationOutgoing outgoingDetail = empApplicationOutgoingService.queryByProcessInstanceId(processInstanceId);
			result.put("outgoingDetail", outgoingDetail);
			//流程信息
			List<ViewTaskInfoTbl> taskFlows = userTaskService.queryTaskFlow(processInstanceId,outgoingDetail.getApprovalStatus());
			result.put("flows", taskFlows);
			result.put("success", true);
		} catch (Exception e) {
			result.put("success", false);
			result.put("msg", "外出申请单有问题,与开发人员联系");
		}
		return result;
	}
	
	@IsMenu
	@RequestMapping("/approveIndex.htm")
	public ModelAndView approveIndex(){
		return new ModelAndView("base/outgoingManage/outgoingApproval");
	}
	/**
	 * 待办查询
	 * @param empApplicationOutgoing
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getApprovePageList.htm")
	public PageModel<EmpApplicationOutgoing> getApprovePageList(EmpApplicationOutgoing empApplicationOutgoing){
	
		empApplicationOutgoing.setApprovalStatus(ConfigConstants.DOING_STATUS);
		List<Integer> departList = new ArrayList<Integer>();
		if(empApplicationOutgoing.getSecondDepart()!=null){
			departList.add(empApplicationOutgoing.getSecondDepart());
			empApplicationOutgoing.setDepartList(departList);
		}else if(empApplicationOutgoing.getSecondDepart()==null&&empApplicationOutgoing.getFirstDepart()!=null){
			departList.add(empApplicationOutgoing.getFirstDepart());
			List<Depart> list = departService.getByParentId(Long.valueOf(String.valueOf(empApplicationOutgoing.getFirstDepart())));
		    for(Depart depart:list){
		    	departList.add(Integer.valueOf(String.valueOf(depart.getId())));
		    }
		    empApplicationOutgoing.setDepartList(departList);
		}
		PageModel<EmpApplicationOutgoing> pm=new PageModel<EmpApplicationOutgoing>();
		pm.setRows(new java.util.ArrayList<EmpApplicationOutgoing>());
		
		try {
			empApplicationOutgoing.setAuditUser(String.valueOf(userService.getCurrentUser().getEmployee().getId()));
			pm = empApplicationOutgoingService.getApprovePageList(empApplicationOutgoing);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	/**
	 * 已办查询
	 * @param empApplicationOutgoing
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getAuditedPageList.htm")
	public PageModel<EmpApplicationOutgoing> getAuditedPageList(EmpApplicationOutgoing empApplicationOutgoing){
		
		List<Integer> departList = new ArrayList<Integer>();
		User user = userService.getCurrentUser();
		empApplicationOutgoing.setAuditUser(user.getEmployeeId().toString());
		if(empApplicationOutgoing.getSecondDepart()!=null){
			departList.add(empApplicationOutgoing.getSecondDepart());
			empApplicationOutgoing.setDepartList(departList);
		}else if(empApplicationOutgoing.getSecondDepart()==null&&empApplicationOutgoing.getFirstDepart()!=null){
			departList.add(empApplicationOutgoing.getFirstDepart());
			List<Depart> list = departService.getByParentId(Long.valueOf(String.valueOf(empApplicationOutgoing.getFirstDepart())));
		    for(Depart depart:list){
		    	departList.add(Integer.valueOf(String.valueOf(depart.getId())));
		    }
		    empApplicationOutgoing.setDepartList(departList);
		}
		PageModel<EmpApplicationOutgoing> pm=new PageModel<EmpApplicationOutgoing>();
		try {
			empApplicationOutgoing.setAuditUser(String.valueOf(userService.getCurrentUser().getEmployee().getId()));
			pm = empApplicationOutgoingService.getAuditedPageList(empApplicationOutgoing);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	/**
	 * 失效查询
	 * @param empApplicationOutgoing
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getInvalidPageList.htm")
	public PageModel<EmpApplicationOutgoing> getInvalidPageList(EmpApplicationOutgoing empApplicationOutgoing){
	
		empApplicationOutgoing.setApprovalStatus(ConfigConstants.OVERDUE_STATUS);
	
		PageModel<EmpApplicationOutgoing> pm=new PageModel<EmpApplicationOutgoing>();
		pm.setRows(new java.util.ArrayList<EmpApplicationOutgoing>());
		
		try {
			empApplicationOutgoing.setAuditUser(String.valueOf(userService.getCurrentUser().getEmployee().getId()));
			
			pm = empApplicationOutgoingService.getInvalidPageList(empApplicationOutgoing);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	/**
	 * 消下属缺勤审批
	 * @param processIds
	 * @param comment
	 * @param commentType
	 * @return
	 */
	//考勤管理-外出管理-外出审批-审批
	@ResponseBody
	@RequestMapping("/completeTask.htm")
	public Map<String,Object> completeTask(HttpServletRequest request,@RequestParam("processIds")List<String> processIds,String comment,String commentType){
		Map<String,Object> result = Maps.newHashMap();
		try {
			if(processIds == null || processIds.size() == 0){
				throw new OaException("请选择审批单据！");
			}
			for (String processId : processIds) {
				empApplicationOutgoingService.completeTask(request,processId, comment, commentType);
			}
			result.put("sucess", true);
			result.put("msg", "操作成功！");
		} catch (OaException e) {
			result.put("sucess", false);
			result.put("msg", e.getMessage());
		}catch (Exception e) {
			result.put("sucess", false);
			result.put("msg", "审批假期出现问题,与开发人员联系");
		}
		return result;
	}
	
	/**
	 * 失效同意
	 * @param processId
	 * @param comment
	 * @param commentType
	 * @return
	 */
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/invalidPass.htm")
	public Map<String,Object> invalidPass(HttpServletRequest request,String processId,String comment,String commentType){
		Map<String,Object> result = Maps.newHashMap();
		try {
			userTaskService.completeTaskByAdmin(request,processId, comment, commentType);
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
	 * 失效拒绝
	 * @param processId
	 * @param comment
	 * @param commentType
	 * @return
	 */
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/invalidRefuse.htm")
	public Map<String,Object> invalidRefuse(HttpServletRequest request,String processId,String comment,String commentType){
		Map<String,Object> result = Maps.newHashMap();
		try {
			userTaskService.completeTaskByAdmin(request,processId, comment, commentType);
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
}
