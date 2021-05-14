package com.ule.oa.admin.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
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
import com.ule.oa.admin.util.Token;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.EmpApplicationLeave;
import com.ule.oa.base.po.EmpApplicationLeaveDetail;
import com.ule.oa.base.po.User;
import com.ule.oa.base.po.tbl.ViewTaskInfoTbl;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmpApplicationLeaveService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.service.UserTaskService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.JSON;
import com.ule.oa.common.utils.JsonWriter;
import com.ule.oa.common.utils.PageModel;
import com.ule.oa.common.utils.RedisUtils;
import com.ule.oa.common.utils.UUIDGenerator;

/**
 * @ClassName: 请假查询
 * @Description: 请假查询
 * @author yangjie
 * @date 2017年12月18日
 */

@Controller
@RequestMapping("empApplicationLeave")
public class EmpApplicationLeaveController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private EmpApplicationLeaveService empApplicationLeaveService;
	@Autowired
	private DepartService departService;
	@Autowired
	private UserTaskService userTaskService;
	@Autowired
	private UserService userService;
	
	@IsMenu
	@RequestMapping("/index.htm")
	public ModelAndView index(){
		return new ModelAndView("base/leaveManager/leave");
	}
	
	@IsMenu
	@RequestMapping("/approveIndex.htm")
	public ModelAndView approveIndex(){
		return new ModelAndView("base/leaveManager/leave_approve");
	}
	
	//考勤管理-假期管理-休假查询-查询
	@IsOperation(returnType=false)//需要校验操作权限
	@Token(generate=true)//生成token
	@ResponseBody
	@RequestMapping("/getReportPageList.htm")
	public PageModel<EmpApplicationLeave> getReportPageList(EmpApplicationLeave leave){

		List<Integer> departList = new ArrayList<Integer>();
		if(leave.getSecondDepart()!=null){
			departList.add(leave.getSecondDepart());
			leave.setDepartList(departList);
		}else if(leave.getSecondDepart()==null&&leave.getFirstDepart()!=null){
			departList.add(leave.getFirstDepart());
			List<Depart> list = departService.getByParentId(Long.valueOf(String.valueOf(leave.getFirstDepart())));
		    for(Depart depart:list){
		    	departList.add(Integer.valueOf(String.valueOf(depart.getId())));
		    }
		    leave.setDepartList(departList);
		}
		PageModel<EmpApplicationLeave> pm=new PageModel<EmpApplicationLeave>();
		pm.setRows(new java.util.ArrayList<EmpApplicationLeave>());
		
		try {
			pm = empApplicationLeaveService.getReportPageList(leave);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	@ResponseBody
	@RequestMapping("/getApproveIngPageList.htm")
	@Token(generate=true)//生成token
	public PageModel<EmpApplicationLeave> getApprovePageList(EmpApplicationLeave leave){

		leave.setApprovalStatus(ConfigConstants.DOING_STATUS);
		List<Integer> departList = new ArrayList<Integer>();
		if(leave.getSecondDepart()!=null){
			departList.add(leave.getSecondDepart());
			leave.setDepartList(departList);
		}else if(leave.getSecondDepart()==null&&leave.getFirstDepart()!=null){
			departList.add(leave.getFirstDepart());
			List<Depart> list = departService.getByParentId(Long.valueOf(String.valueOf(leave.getFirstDepart())));
		    for(Depart depart:list){
		    	departList.add(Integer.valueOf(String.valueOf(depart.getId())));
		    }
		    leave.setDepartList(departList);
		}
		PageModel<EmpApplicationLeave> pm=new PageModel<EmpApplicationLeave>();
		pm.setRows(new java.util.ArrayList<EmpApplicationLeave>());
		
		try {
			leave.setAssignee(String.valueOf(userService.getCurrentUser().getEmployee().getId()));
			pm = empApplicationLeaveService.getReportPageList(leave);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	@ResponseBody
	@RequestMapping("/getAbatePageList.htm")
	public PageModel<EmpApplicationLeave> getAbatePageList(EmpApplicationLeave leave){

		leave.setApprovalStatus(ConfigConstants.OVERDUE_STATUS);
	
		PageModel<EmpApplicationLeave> pm=new PageModel<EmpApplicationLeave>();
		pm.setRows(new java.util.ArrayList<EmpApplicationLeave>());
		
		try {
			leave.setAssignee(String.valueOf(userService.getCurrentUser().getEmployee().getId()));
			
			pm = empApplicationLeaveService.getAbatePageList(leave);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	@ResponseBody
	@RequestMapping("/myLeaveTaskList.htm")
	public PageModel<EmpApplicationLeave> getMyLeaveTaskList(EmpApplicationLeave leave){
		
		List<Integer> departList = new ArrayList<Integer>();
		User user = userService.getCurrentUser();
		leave.setAssignee(user.getEmployeeId().toString());
		if(leave.getSecondDepart()!=null){
			departList.add(leave.getSecondDepart());
			leave.setDepartList(departList);
		}else if(leave.getSecondDepart()==null&&leave.getFirstDepart()!=null){
			departList.add(leave.getFirstDepart());
			List<Depart> list = departService.getByParentId(Long.valueOf(String.valueOf(leave.getFirstDepart())));
		    for(Depart depart:list){
		    	departList.add(Integer.valueOf(String.valueOf(depart.getId())));
		    }
		    leave.setDepartList(departList);
		}
		PageModel<EmpApplicationLeave> pm=new PageModel<EmpApplicationLeave>();
		try {
			leave.setAssignee(String.valueOf(userService.getCurrentUser().getEmployee().getId()));
			pm = empApplicationLeaveService.myLeaveTaskList(leave);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	//考勤管理-假期管理-休假查询-拒绝
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@Token(remove = true)
	@RequestMapping("/refuseLeave.htm")
	public JSON refuseLeave(HttpServletRequest request,String processInstanceId,String comment){
		User user = userService.getCurrentUser();
		Map<String,Object> map = new HashMap<String,Object>();		
		try{
			userTaskService.completeTaskByAdmin(request,processInstanceId,comment,ConfigConstants.REFUSE);	
			try{
				//发送邮件和消息
				empApplicationLeaveService.sendRefuseLeaveNotice(processInstanceId,comment);
			}catch(Exception e1){
				logger.error("refuseLeave:processInstanceId="+processInstanceId+"发送邮件和消息失败。");
			}
			map.put("message", "拒绝成功");
			map.put("flag", true);									
		}catch(Exception e){
			map.put("flag", false);
			map.put("message", "拒绝失败,"+e.getMessage());
		}finally{
			String uuId = UUIDGenerator.generate();
            try {
				RedisUtils.setString(ConfigConstants.ADMIN_REDIS_PRE+String.valueOf(user.getEmployee().getId()),uuId,1800000);
				map.put("token", uuId);
			} catch (Exception e1) {
				logger.error(user.getEmployee().getCnName()+"refuseLeave：",e1);
			}
		}		
		return JsonWriter.successfulResult(map);
	}
	
	@ResponseBody
	@RequestMapping("/completeLeave.htm")
	@Token(remove = true)
	public Map<String,Object> completeLeave(HttpServletRequest request,String processIds,String comment,String commentType){
		User user = userService.getCurrentUser();
		Map<String,Object> result = Maps.newHashMap();
		try {
			logger.info("completeLeave参数:processIds="+processIds+";comment="+comment+";commentType="+commentType);
			for(String processId:processIds.split(",")){
				this.empApplicationLeaveService.completeTask(request,processId, comment, commentType);
			}
			result.put("sucess", true);
		} catch (OaException e) {
			result.put("sucess", false);
			result.put("msg", e.getMessage());
		}catch (Exception e) {
			result.put("sucess", false);
			result.put("msg", "审批假期出现问题,与开发人员联系");
		}finally{
			String uuId = UUIDGenerator.generate();
            try {
				RedisUtils.setString(ConfigConstants.ADMIN_REDIS_PRE+String.valueOf(user.getEmployee().getId()),uuId,1800000);
				result.put("token", uuId);
			} catch (Exception e1) {
				logger.error(user.getEmployee().getCnName()+"completeLeave：",e1);
			}
		}
		return result;
	}
	
	@ResponseBody
	@RequestMapping("/getLeaveDetail.htm")
	public Map<String,Object> getLeaveDetail(String processId){
		
		Map<String,Object> result = Maps.newHashMap();
		try {
			//假期申请单信息
			EmpApplicationLeave leave = empApplicationLeaveService.queryByProcessInstanceId(processId);
			result.put("leave", leave);
			List<EmpApplicationLeaveDetail> detailList = empApplicationLeaveService.getLeaveDetailList(leave.getId());
			result.put("detailList", detailList);
			//流程信息
			List<ViewTaskInfoTbl> taskFlows = userTaskService.queryTaskFlow(processId,leave.getApprovalStatus());
			result.put("flows", taskFlows);
			Map<String,Object> leaveInfo = empApplicationLeaveService.getLeaveCountInfoByEmpId(leave.getEmployeeId());
			result.put("leaveInfo", leaveInfo);
			result.put("sucess", true);
		} catch (Exception e) {
			result.put("sucess", false);
			result.put("msg", "假期申请单有问题,与开发人员联系");
		}
		return result;
	}
	
	/**
	 * //考勤管理-假期管理-休假查询-导出
	 * @param model
	 * @param response
	 * @throws UnsupportedEncodingException
	 */
	@IsOperation(returnType=true)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/exportReport.htm")
	public void exportReport(EmpApplicationLeave leave, HttpServletResponse response,HttpServletRequest request) throws IOException{
	
		OutputStream os = null;
		try{
			leave.setCode(URLDecoder.decode(leave.getCode(),"UTF-8"));
			leave.setCnName(URLDecoder.decode(leave.getCnName(),"UTF-8"));
			List<Integer> departList = new ArrayList<Integer>();
			if(leave.getSecondDepart()!=null){
				departList.add(leave.getSecondDepart());
				leave.setDepartList(departList);
			}else if(leave.getSecondDepart()==null&&leave.getFirstDepart()!=null){
				departList.add(leave.getFirstDepart());
				List<Depart> list = departService.getByParentId(Long.valueOf(String.valueOf(leave.getFirstDepart())));
			    for(Depart depart:list){
			    	departList.add(Integer.valueOf(String.valueOf(depart.getId())));
			    }
			    leave.setDepartList(departList);
			}
			HSSFWorkbook workbook = empApplicationLeaveService.exportExcel(leave);
			ResponseUtil.setDownLoadExeclInfo(response,request,"请假查询_");
            os = response.getOutputStream();
    		if(workbook!=null){
    			workbook.write(os);
    			os.flush();
    			os.close();
    		}
            
		}catch(Exception e){
			logger.error("导出请假查询报表失败,原因=",e);
		}finally{
			if(null != os){
				os.flush();
				os.close();
			}
		}
    }
	
	//考勤管理-假期管理-休假查询-失效同意
	@IsOperation(returnType=false)//需要校验操作权限
	@Token(remove = true)
	@ResponseBody
	@RequestMapping("/passInvalidLeave.htm")
	public JSON passInvalidLeave(HttpServletRequest request,String processId,String comment){
		User user = userService.getCurrentUser();
		Map<String,Object> map = new HashMap<String,Object>();		
		try{
			userTaskService.completeTaskByAdmin(request,processId,comment,ConfigConstants.OVERDUEPASS);	
			map.put("message", "操作成功");
			map.put("flag", true);									
		}catch(Exception e){
			logger.error("passInvalidLeave失败："+e.getMessage());
			map.put("flag", false);
			map.put("message", "操作失败,"+e.getMessage());
		}finally{
			String uuId = UUIDGenerator.generate();
            try {
				RedisUtils.setString(ConfigConstants.ADMIN_REDIS_PRE+String.valueOf(user.getEmployee().getId()),uuId,1800000);
				map.put("token", uuId);
			} catch (Exception e1) {
				logger.error(user.getEmployee().getCnName()+"passInvalidLeave：",e1);
			}
		}		
		return JsonWriter.successfulResult(map);
	}
	
	//考勤管理-假期管理-休假查询-失效拒绝
	@IsOperation(returnType=false)//需要校验操作权限
	@Token(remove = true)
	@ResponseBody
	@RequestMapping("/refuseInvalidLeave.htm")
	public JSON refuseInvalidLeave(HttpServletRequest request,String processId,String comment){
		User user = userService.getCurrentUser();
		Map<String,Object> map = new HashMap<String,Object>();		
		try{
			userTaskService.completeTaskByAdmin(request,processId,comment,ConfigConstants.OVERDUEREFUSE);	
			map.put("message", "操作成功");
			map.put("flag", true);									
		}catch(Exception e){
			logger.error("refuseInvalidLeave失败："+e.getMessage());
			map.put("flag", false);
			map.put("message", "操作失败,"+e.getMessage());
		}finally{
			String uuId = UUIDGenerator.generate();
            try {
				RedisUtils.setString(ConfigConstants.ADMIN_REDIS_PRE+String.valueOf(user.getEmployee().getId()),uuId,1800000);
				map.put("token", uuId);
			} catch (Exception e1) {
				logger.error(user.getEmployee().getCnName()+"refuseInvalidLeave：",e1);
			}
		}		
		return JsonWriter.successfulResult(map);
	}
	
	@ResponseBody
	@RequestMapping("/repairErrorEmpClassApply.htm")
	public Map<String,Object> repairErrorEmpClassApply(){
		Map<String,Object> result = Maps.newHashMap();
		try {
			empApplicationLeaveService.repairErrorEmpClassApply();
			result.put("sucess", true);
		} catch (Exception e) {
			result.put("sucess", false);
			result.put("msg", e.getMessage());
		}
		return result;
	}
	
	@ResponseBody
	@RequestMapping("/repairErrorWorkHours.htm")
	public Map<String,Object> repairErrorWorkHours(Long id,Integer delFlag){
		Map<String,Object> result = Maps.newHashMap();
		try {
			empApplicationLeaveService.repairErrorWorkHours(id, delFlag);
			result.put("sucess", true);
		} catch (Exception e) {
			result.put("sucess", false);
			result.put("msg", e.getMessage());
		}
		return result;
	}
	
	@ResponseBody
	@RequestMapping("/updateLeaveTime.htm")
	public Map<String,Object> updateLeaveTime(Long id,String startTime,String endTime){
		Map<String,Object> result = Maps.newHashMap();
		try {
			result = empApplicationLeaveService.updateLeaveTime(id, startTime, endTime);
		} catch (Exception e) {
			result.put("msg", e.getMessage());
		}
		return result;
	}
	
	
	
}
