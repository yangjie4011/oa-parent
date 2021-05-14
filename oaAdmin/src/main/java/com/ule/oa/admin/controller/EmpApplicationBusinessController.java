package com.ule.oa.admin.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
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
import com.ule.oa.base.po.EmpApplicationBusiness;
import com.ule.oa.base.po.EmpApplicationBusinessDetail;
import com.ule.oa.base.po.User;
import com.ule.oa.base.po.tbl.ViewTaskInfoTbl;
import com.ule.oa.base.service.EmpApplicationBusinessService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.service.UserTaskService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.JSON;
import com.ule.oa.common.utils.JsonWriter;
import com.ule.oa.common.utils.PageModel;

/**
 * @ClassName: 出差申请表
 * @Description: 出差申请表
 * @author yangjie
 * @date 2017年6月13日
 */
@Controller
@RequestMapping("empApplicationBusiness")
public class EmpApplicationBusinessController {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private EmpApplicationBusinessService empApplicationBusinessService;
	@Autowired
	private UserService userService;
	@Autowired
	private UserTaskService userTaskService;
	/**
	  * index(出差查询报表首页)
	  * @Title: index
	  * @Description: 出差查询报表首页
	  * @param request
	  * @return    设定文件
	  * String    返回类型
	  * @throws
	 */
	@IsMenu
	@RequestMapping("/index.htm")
	public ModelAndView index(HttpServletRequest request){
		return new ModelAndView("base/attnManager/business_report");
	}
	
	@IsMenu
	@RequestMapping("/businessApprove.htm")
	public ModelAndView index(){
		return new ModelAndView("base/business/businessApprove");
	}
	//统计报表
	@IsMenu
	@RequestMapping("/businessReport.htm")
	public ModelAndView approveIndex(HttpServletRequest request){
		String nowYear = DateUtils.getYear(new Date());
		request.setAttribute("nowYear", nowYear);
		return new ModelAndView("base/business/businessReport");
	}
	
	/**
	  * toView(跳转到出差总结报告查看页面)
	  * @Title: toView
	  * @Description: 跳转到出差总结报告查看页面
	  * @param request
	  * @return    设定文件
	  * String    返回类型
	  * @throws
	 */
	@RequestMapping("/toView.htm")
	public String toView(HttpServletRequest request,Long id){

		User user = userService.getCurrentUser();
		try {
			//获得单据信息
			EmpApplicationBusiness business  = empApplicationBusinessService.getById(id);
			request.setAttribute("business", business);
			
			//获得单据明细
			List<EmpApplicationBusinessDetail> detailList = empApplicationBusinessService.getBusinessDetailList(business.getId());
			request.setAttribute("detailList", detailList);
			
			if(business.getProcessinstanceReportId()==null){
				business.setProcessinstanceReportId("NotProcessinstanceReportId");
			}
			//查找出差报表流程
			List<ViewTaskInfoTbl> taskFlows = userTaskService.queryTaskFlow(business.getProcessinstanceReportId(), business.getApprovalReportStatus());

			request.setAttribute("hiActinstList", taskFlows);
		} catch (Exception e) {
			logger.error(user.getEmployee().getCnName()+"出差报告-审批首页：",e);
		}
		
		return "base/attnManager/business_sum_report_view";
	}
	
	/**
	  * getReportPageList(考勤管理-考勤查询-出差查询-查询)
	  * @Title: getReportPageList
	  * @Description: 考勤管理-考勤查询-出差查询-查询
	  * @param empApplicationOutgoing
	  * @return    设定文件
	  * PageModel<EmpApplicationOutgoing>    返回类型
	  * @throws
	 */
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/getReportPageList.htm")
	public PageModel<EmpApplicationBusiness> getReportPageList(EmpApplicationBusiness empApplicationBusiness){

		PageModel<EmpApplicationBusiness> pm=new PageModel<EmpApplicationBusiness>();
		pm.setRows(new java.util.ArrayList<EmpApplicationBusiness>());
		
		try {
			pm = empApplicationBusinessService.getReportPageList(empApplicationBusiness);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	/**
	  * getReportPageList(考勤管理-出差管理-出差统计-查询)
	  * @Title: getReportPageList
	  * @Description: 考勤管理-考勤查询-出差查询-查询
	  * @param empApplicationOutgoing
	  * @return    设定文件
	  * PageModel<EmpApplicationOutgoing>    返回类型
	  * @throws
	 */
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/getGroupListByInfo.htm")
	public PageModel<EmpApplicationBusiness> getGroupListByInfo(EmpApplicationBusiness empApplicationBusiness,Integer typeNum){
		
		PageModel<EmpApplicationBusiness> pm=new PageModel<EmpApplicationBusiness>();
		pm.setRows(new java.util.ArrayList<EmpApplicationBusiness>());
		try {
			pm = empApplicationBusinessService.getGroupListByInfo(empApplicationBusiness,typeNum);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return pm;
	}
	
	/**
	  * getReportPageList(考勤管理-出差管理-出差审批-查询)
	  * @Title: getReportPageList
	  * @Description: 考勤管理-考勤查询-出差查询-查询
	  * @param empApplicationOutgoing
	  * @return    设定文件
	  * PageModel<EmpApplicationOutgoing>    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/getApproveList.htm")
	public PageModel<EmpApplicationBusiness> getApproveList(EmpApplicationBusiness empApplicationBusiness,Integer typeNum){
	
		PageModel<EmpApplicationBusiness> pm=new PageModel<EmpApplicationBusiness>();
		pm.setRows(new java.util.ArrayList<EmpApplicationBusiness>());
		try {
			pm = empApplicationBusinessService.getApproveList(empApplicationBusiness,typeNum);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return pm;
	}
	
	
	
	
	/**
	  * exportReport(考勤管理-考勤查询-出差查询-导出)
	  * @Title: exportReport
	  * @Description: 考勤管理-考勤查询-出差查询-导出
	  * @param empApplicationOutgoing
	  * @param response
	  * @throws IOException    设定文件
	  * void    返回类型
	  * @throws
	 */
	@IsOperation(returnType=true)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/exportReport.htm")
	public void exportReport(EmpApplicationBusiness empApplicationBusiness,HttpServletResponse response,HttpServletRequest request) throws IOException{//导出数据并发送邮件
	
		OutputStream os = null;
		try {
			empApplicationBusiness.setCode(URLDecoder.decode(empApplicationBusiness.getCode(),"UTF-8"));
			empApplicationBusiness.setCnName(URLDecoder.decode(empApplicationBusiness.getCnName(),"UTF-8"));
			ResponseUtil.setDownLoadExeclInfo(response,request,"出差查询_");
			HSSFWorkbook hSSFWorkbook = empApplicationBusinessService.exportReport(empApplicationBusiness);
			os = response.getOutputStream();
			if(hSSFWorkbook!=null){
				hSSFWorkbook.write(os);
				os.flush();
				os.close();
			}
		} catch (Exception e) {
			logger.error("导出出差查询报表失败,原因=",e);
		}finally{
			if(null != os){
				os.flush();
				os.close();
			}
		}
	}
	
	/**
	  * exportReport(考勤管理-出差管理-出差统计-导出)
	  * @Title: exportReport
	  * @Description: 考勤管理-考勤查询-出差查询-导出
	  * @param empApplicationOutgoing
	  * @param response
	  * @throws IOException    设定文件
	  * void    返回类型
	  * @throws
	 */
	@IsOperation(returnType=true)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/exportGroupReport.htm")
	public void exportGroupReport(EmpApplicationBusiness empApplicationBusiness,Integer typeNum,HttpServletResponse response,HttpServletRequest request) throws IOException{//导出数据并发送邮件
	
		OutputStream os = null;
		try {
			
			if(StringUtils.isNotBlank(empApplicationBusiness.getAddress())){
				empApplicationBusiness.setAddress(URLDecoder.decode(empApplicationBusiness.getAddress(),"UTF-8"));
			}
			ResponseUtil.setDownLoadExeclInfo(response,request,"出差统计报表_");
			HSSFWorkbook hSSFWorkbook = empApplicationBusinessService.exportGroupReport(empApplicationBusiness,typeNum);
			os = response.getOutputStream();
			if(hSSFWorkbook!=null){
				hSSFWorkbook.write(os);
				os.flush();
				os.close();
			}
		} catch (Exception e) {
			logger.error("导出出差统计报表失败,原因=",e);
		}finally{
			if(null != os){
				os.flush();
				os.close();
			}
		}
	}
	
	/**
	 * 出差审批
	 * @param processIds
	 * @param comment
	 * @param commentType
	 * @return
	 */
	//考勤管理-出差管理-出差审批-审批
	@ResponseBody
	@RequestMapping("/completeTask.htm")
	public Map<String,Object> completeTask(HttpServletRequest request,@RequestParam("processIds")List<String> processIds,String comment,String commentType){
		Map<String,Object> result = Maps.newHashMap();
		try {
			logger.info("empApplicationBusiness/completeTask参数:processIds="+processIds+";comment="+comment+";commentType="+commentType);
			if(processIds == null || processIds.size() == 0){
				throw new OaException("请选择审批单据！");
			}
			for (String processId : processIds) {
				empApplicationBusinessService.completeTask(request,processId, comment, commentType);
			}
			result.put("success", true);
			result.put("msg", "操作成功！");
		} catch (OaException e) {
			result.put("success", false);
			result.put("msg", e.getMessage());
		}catch (Exception e) {
			result.put("success", false);
			result.put("msg", "审批假期出现问题,与开发人员联系");
		}
		return result;
	}
	
	
	//考勤管理-出差管理-出差审批-失效同意
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/completePassAbate.htm")
	public JSON completePassAbate(HttpServletRequest request,String processIds,String comment,String commentType){
		
		Map<String,Object> map = new HashMap<String,Object>();		
		try{
			userTaskService.completeTaskByAdmin(request,processIds,comment,commentType);			
			map.put("success", true);		
			map.put("msg", "操作成功");
		}catch(Exception e){
			map.put("success", false);
			map.put("msg", "失效失败,"+e.getMessage());
		}		
		return JsonWriter.successfulResult(map);
	}
	
	//考勤管理-出差管理-出差审批-失效拒绝
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/completeRefuseAbate.htm")
	public JSON completeRefuseAbate(HttpServletRequest request,String processIds,String comment,String commentType){
		
		Map<String,Object> map = new HashMap<String,Object>();		
		try{
			userTaskService.completeTaskByAdmin(request,processIds,comment,commentType);	
			map.put("success", true);		
			map.put("msg", "操作成功");
		}catch(Exception e){
			map.put("success", false);
			map.put("msg", "失效失败,"+e.getMessage());
		}		
		return JsonWriter.successfulResult(map);
	}

	
	
	//考勤管理-出差管理-出差审批获取流程信息
	@ResponseBody
	@RequestMapping("/getProcessbyId.htm")
	public Map<String,Object> getProcessbyId(String processId){
	
		Map<String,Object> result = Maps.newHashMap();
		try {
			//假期申请单信息
			EmpApplicationBusiness empBusiness = empApplicationBusinessService.queryByProcessInstanceId(processId);
			result.put("empBusiness", empBusiness);	
			List<EmpApplicationBusinessDetail> detailList = empApplicationBusinessService.getBusinessDetailList(empBusiness.getId());
			result.put("detailList", detailList);
			//流程信息
			List<ViewTaskInfoTbl> taskFlows = userTaskService.queryTaskFlow(processId,empBusiness.getApprovalStatus().intValue());
			result.put("flows", taskFlows);
			result.put("success", true);
		} catch (Exception e) {
			result.put("success", false);
			result.put("msg", "出差申请单有问题,与开发人员联系");
		}
		return result;
	}

}
