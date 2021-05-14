package com.ule.oa.admin.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
import com.ule.oa.base.po.EmpApplicationLeave;
import com.ule.oa.base.po.EmpApplicationLeaveAbolish;
import com.ule.oa.base.po.EmpApplicationLeaveDetail;
import com.ule.oa.base.po.User;
import com.ule.oa.base.po.tbl.ViewTaskInfoTbl;
import com.ule.oa.base.service.EmpApplicationLeaveAbolishService;
import com.ule.oa.base.service.EmpApplicationLeaveService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.service.UserTaskService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.JSON;
import com.ule.oa.common.utils.JsonWriter;
import com.ule.oa.common.utils.PageModel;
import com.ule.oa.common.utils.RedisUtils;
import com.ule.oa.common.utils.UUIDGenerator;
/**
 * @ClassName: 销假查询
 * @Description: 销假查询
 * @author zhoujinliang
 * @date 2017年12月18日
 */

@Controller
@RequestMapping("empApplicationLeaveBack")
public class EmpApplicationLeaveBackController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private EmpApplicationLeaveService empApplicationLeaveService;
	@Autowired
	private UserTaskService userTaskService;
	@Autowired
	private UserService userService;
	@Autowired
	private EmpApplicationLeaveAbolishService empApplicationLeaveAbolishService;
	
	
	@IsMenu
	@RequestMapping("/index.htm")
	public ModelAndView index(){
		return new ModelAndView("base/leaveManager/leave_abolish");
	}
	
	@IsMenu
	@RequestMapping("/approveIndex.htm")
	public ModelAndView approveIndex(){
		return new ModelAndView("base/leaveManager/leaveAbolish_approve");
	}
	

	//考勤管理-假期管理-销假查询-查询
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@Token(generate=true)//生成token
	@RequestMapping("/getReportPageList.htm")
	public PageModel<EmpApplicationLeaveAbolish> getLeaveAbolishPageList(EmpApplicationLeaveAbolish leave){	
		
		PageModel<EmpApplicationLeaveAbolish> pm=new PageModel<EmpApplicationLeaveAbolish>();
		pm.setRows(new java.util.ArrayList<EmpApplicationLeaveAbolish>());	
		try {
			pm = empApplicationLeaveAbolishService.getReportPageList(leave);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	//销假审批
	@ResponseBody
	@Token(generate=true)//生成token
	@RequestMapping("/getApproveIngPageList.htm")
	public PageModel<EmpApplicationLeaveAbolish> getApprovePageList(EmpApplicationLeaveAbolish leave){		
		
		leave.setApprovalStatus(ConfigConstants.DOING_STATUS);
		PageModel<EmpApplicationLeaveAbolish> pm=new PageModel<EmpApplicationLeaveAbolish>();
		pm.setRows(new java.util.ArrayList<EmpApplicationLeaveAbolish>());
		
		try {
			leave.setAssignee(String.valueOf(userService.getCurrentUser().getEmployee().getId()));
			pm = empApplicationLeaveAbolishService.getReportPageList(leave);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	
	@ResponseBody
	@RequestMapping("/getleaveAbolishDetail.htm")
	public Map<String,Object> getleaveAbolishDetail(String processId){
		
		User user = userService.getCurrentUser();
		Map<String,Object> result = Maps.newHashMap();
		try {
			if(processId!=null){
				EmpApplicationLeaveAbolish leaveAbolish = empApplicationLeaveAbolishService.queryByProcessInstanceId(processId);
				if(leaveAbolish!=null){
					result.put("leaveAbolish", leaveAbolish);
					EmpApplicationLeave leave  = empApplicationLeaveService.getById(leaveAbolish.getLeaveId());
					result.put("leave", leave);//原请假申请单信息（员工信息）
					List<EmpApplicationLeaveDetail> detailList = empApplicationLeaveService.getLeaveDetailList(leave.getId());
					result.put("detailList", detailList.get(0));//原申请单明细（假期类型等数据）
					result.put("isSelf", false);
					result.put("canApprove", false);
					Map<String,String> map = empApplicationLeaveAbolishService.
							getActualEndTime(leave.getEmployeeId(), detailList.get(0).getLeaveType(), detailList.get(0).getStartTime(), leaveAbolish.getStartTime());
					result.put("actualEndTime", map.get("endTime"));
					//审批显示
					if(leaveAbolish.getApprovalStatus().intValue()==100||leaveAbolish.getApprovalStatus().intValue()==200) {
						if(leave.getEmployeeId().longValue()==user.getEmployeeId().longValue()){
							result.put("isSelf", true);
						}
						
					}
					//流程信息
					List<ViewTaskInfoTbl> taskFlows = userTaskService.queryTaskFlow(processId,leaveAbolish.getApprovalStatus());
					result.put("flows", taskFlows);
					Map<String,Object> leaveInfo = empApplicationLeaveService.getLeaveCountInfoByEmpId(leave.getEmployeeId());
					result.put("leaveInfo", leaveInfo);
					result.put("sucess", true);
				}else{
					logger.error("根据ID="+processId+"未查到销假申请单。");
				}
			}else{
				logger.error(user.getEmployee().getCnName()+"销假申请单ID为空");
			}
		} catch (Exception e) {
			result.put("sucess", false);
			result.put("msg", "销假申请单有问题,与开发人员联系");
		}
		return result;
	}
	
	
	@ResponseBody
	@RequestMapping("/myLeaveTaskList.htm")
	public PageModel<EmpApplicationLeaveAbolish> getMyLeaveTaskList(EmpApplicationLeaveAbolish leave){
	
		leave.setDepartId(leave.getFirstDepart());
		User user = userService.getCurrentUser();
		leave.setAssignee(user.getEmployeeId().toString());
		
		PageModel<EmpApplicationLeaveAbolish> pm=new PageModel<EmpApplicationLeaveAbolish>();
		try {
			leave.setAssignee(String.valueOf(userService.getCurrentUser().getEmployee().getId()));
			pm = empApplicationLeaveAbolishService.myLeaveTaskList(leave);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	@Token(remove = true)
	@ResponseBody
	@RequestMapping("/completeLeave.htm")
	public Map<String,Object> completeLeave(HttpServletRequest request,String processIds,String comment,String commentType){
		User user = userService.getCurrentUser();
		Map<String,Object> result = Maps.newHashMap();
		try {
			for(String processId:processIds.split(",")){
				this.empApplicationLeaveAbolishService.completeTask(request,processId, comment, commentType);
			}
			result.put("sucess", true);
		} catch (OaException e) {
			result.put("sucess", false);
			result.put("msg", e.getMessage());
		}catch (Exception e) {
			result.put("sucess", false);
			result.put("msg", "审批销假假期出现问题,与开发人员联系");
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
	
    //考勤管理-假期管理-销假查询-拒绝
	@IsOperation(returnType=false)//需要校验操作权限
	@Token(remove = true)
	@ResponseBody
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
	@RequestMapping("/getAbatePageList.htm")
	public PageModel<EmpApplicationLeaveAbolish> getAbatePageList(EmpApplicationLeaveAbolish leave){
		
		leave.setApprovalStatus(ConfigConstants.OVERDUE_STATUS);
		PageModel<EmpApplicationLeaveAbolish> pm=new PageModel<EmpApplicationLeaveAbolish>();
		pm.setRows(new java.util.ArrayList<EmpApplicationLeaveAbolish>());
		
		try {
			leave.setAssignee(String.valueOf(userService.getCurrentUser().getEmployee().getId()));
			
			pm = empApplicationLeaveAbolishService.getReportPageList(leave);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	/**
	 * 考勤管理-假期管理-销假查询-导出
	 * @param model
	 * @param response
	 * @throws UnsupportedEncodingException
	 */
	@IsOperation(returnType=true)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/exportReport.htm")
	public void exportReport(EmpApplicationLeaveAbolish leave, HttpServletResponse response,HttpServletRequest request) throws IOException{
	
		leave.setLimit(null);
		leave.setOffset(null);
		OutputStream os = null;
		try{
			leave.setCode(URLDecoder.decode(leave.getCode(),"UTF-8"));
			leave.setCnName(URLDecoder.decode(leave.getCnName(),"UTF-8"));
			leave.setDepartId(leave.getFirstDepart());
			
			HSSFWorkbook workbook = empApplicationLeaveAbolishService.exportExcel(leave);
			ResponseUtil.setDownLoadExeclInfo(response,request,"销假查询_");
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
	
	//考勤管理-假期管理-销假查询-失效同意
	@IsOperation(returnType=false)//需要校验操作权限
	@Token(remove = true)
	@ResponseBody
	@RequestMapping("/passInvalidCancelLeave.htm")
	public JSON passInvalidCancelLeave(HttpServletRequest request,String processId,String comment){
		User user = userService.getCurrentUser();
		Map<String,Object> map = new HashMap<String,Object>();		
		try{
			userTaskService.completeTaskByAdmin(request,processId,comment,ConfigConstants.OVERDUEPASS);	
			map.put("message", "操作成功");
			map.put("flag", true);									
		}catch(Exception e){
			logger.error("passInvalidCancelLeave失败："+e.getMessage());
			map.put("flag", false);
			map.put("message", "操作失败,"+e.getMessage());
		}finally{
			String uuId = UUIDGenerator.generate();
            try {
				RedisUtils.setString(ConfigConstants.ADMIN_REDIS_PRE+String.valueOf(user.getEmployee().getId()),uuId,1800000);
				map.put("token", uuId);
			} catch (Exception e1) {
				logger.error(user.getEmployee().getCnName()+"passInvalidCancelLeave：",e1);
			}
		}			
		return JsonWriter.successfulResult(map);
	}
	
	//考勤管理-假期管理-销假查询-失效拒绝
	@IsOperation(returnType=false)//需要校验操作权限
	@Token(remove = true)
	@ResponseBody
	@RequestMapping("/refuseInvalidCancelLeave.htm")
	public JSON refuseInvalidCancelLeave(HttpServletRequest request,String processId,String comment){
		User user = userService.getCurrentUser();
		Map<String,Object> map = new HashMap<String,Object>();		
		try{
			userTaskService.completeTaskByAdmin(request,processId,comment,ConfigConstants.OVERDUEREFUSE);	
			map.put("message", "操作成功");
			map.put("flag", true);									
		}catch(Exception e){
			logger.error("refuseInvalidCancelLeave失败：",e);
			map.put("flag", false);
			map.put("message", "操作失败,"+e.getMessage());
		}finally{
			String uuId = UUIDGenerator.generate();
            try {
				RedisUtils.setString(ConfigConstants.ADMIN_REDIS_PRE+String.valueOf(user.getEmployee().getId()),uuId,1800000);
				map.put("token", uuId);
			} catch (Exception e1) {
				logger.error(user.getEmployee().getCnName()+"refuseInvalidCancelLeave：",e1);
			}
		}			
		return JsonWriter.successfulResult(map);
	}
}
