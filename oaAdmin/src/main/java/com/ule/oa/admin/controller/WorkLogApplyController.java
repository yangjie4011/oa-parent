package com.ule.oa.admin.controller;

import java.io.IOException;
import java.io.OutputStream;
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
import com.google.common.collect.Maps;
import com.ule.oa.admin.util.IsMenu;
import com.ule.oa.admin.util.ResponseUtil;
import com.ule.oa.base.po.BaseEmpWorkLog;
import com.ule.oa.base.po.RabcResource;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.BaseEmpWorkLogService;
import com.ule.oa.base.service.RabcResourceService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.service.UserTaskService;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.PageModel;

/**
 * 员工工作日志
 * @author yangjie
 *
 */
@Controller
@RequestMapping("workLog")
public class WorkLogApplyController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private UserService userService;
	@Autowired
	private BaseEmpWorkLogService baseEmpWorkLogService;
	@Autowired
	private UserTaskService userTaskService;
	@Autowired
	private RabcResourceService resourceService;
	
	/**
	 * 工作日志填写页
	 * @return
	 */
	@IsMenu
	@RequestMapping("/workLogFillIn.htm")
	public String workLogFillIn(HttpServletRequest request){
		request.setAttribute("month", DateUtils.format(new Date(), DateUtils.FORMAT_YYYY_MM));
		User user = userService.getCurrentUser();
		request.setAttribute("cnName", user.getEmployee().getCnName());
		request.setAttribute("departName", user.getDepart().getName());
		return "base/workManagement/worklog_fillIn";
	}
	
	/**
	 * 工作日志填写-查询
	 * @param month
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryWorkLogDetalInfoByMonth.htm")
	public Map<String,Object> queryWorkLogDetalInfoByMonth(String month){
	
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			result = baseEmpWorkLogService.queryWorkLogDetalInfoByMonth(month);
		}catch(Exception e){
			logger.error("getUnCommitData:",e);
		}
		return result;
	}
	
	/**
	 * 工作日志审阅
	 * @return
	 */
	@IsMenu
	@RequestMapping("/workLogApproval.htm")
	public String workLogApproval(HttpServletRequest request){
		request.setAttribute("month", DateUtils.format(new Date(), DateUtils.FORMAT_YYYY_MM));
		User user = userService.getCurrentUser();
		//查询员工所有菜单权限
    	List<RabcResource> resList = resourceService.getAllAdminTabListByUserId(user.getId());
    	boolean leaderApproval = false;//主管审阅
    	boolean hrApproval = false;//人事审阅
    	for(RabcResource data:resList){
			if("workLog_leader_approval".equals(data.getCode())){
				leaderApproval = true;
			}
			if("workLog_hr_approval".equals(data.getCode())){
				hrApproval = true;
			}
    	}
    	request.setAttribute("leaderApproval", leaderApproval);
    	request.setAttribute("hrApproval", hrApproval);
		return "base/workManagement/workLogApproval";
	}
	
	/**
	 * 工作日志查询
	 * @return
	 */
	@IsMenu
	@RequestMapping("/workLogSearch.htm")
	public String workLogSearch(HttpServletRequest request){
		return "base/workManagement/workLogSearch";
	}
	
	// 保存
	@ResponseBody
	@RequestMapping("/save.htm")
	public Map<String, Object> save(BaseEmpWorkLog workLog) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = baseEmpWorkLogService.save(workLog);
		} catch (Exception e) {
			map.put("repeatFlag", false);
			map.put("message", e.getMessage());
			map.put("success", false);
			return map;
		}
		return map;
	}
	
	//获取流程详情弹框
	@ResponseBody
	@RequestMapping("/getProcessInfoDiv.htm")
	public Map<String, Object> getProcessInfoDiv(String workDate,Long employeeId) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = baseEmpWorkLogService.getProcessInfoDiv(workDate,employeeId);
		} catch (Exception e) {
			map.put("repeatFlag", false);
			map.put("message", e.getMessage());
			map.put("success", false);
			return map;
		}
		return map;
	}
	
	//工作日志-工作日志审阅-分页查询
	@ResponseBody
	@RequestMapping("/getWorkLogPage.htm") 
	public Map<String,Object> getWorkLogPage(Long departId,String leaderName,String month,String empCode,String empCnName,Integer page,Integer rows,String index){
	
		Map<String,Object> result = new HashMap<String,Object>();
		//分页
		PageModel<Map<String,Object>> pm=new PageModel<Map<String,Object>>();
		pm.setRows(new java.util.ArrayList<Map<String,Object>>());
		try{
			result = baseEmpWorkLogService.getApporvalPage(month,departId,leaderName,empCode,empCnName,page,rows,index);
		}catch(Exception e){
			logger.error("getWorkLogPage:",e);
			result.put("message",e.getMessage());
			result.put("success",false);
		}
		return result;
	}
	
	//工作日志-工作日志查询-分页查询
	@ResponseBody
	@RequestMapping("/getWorkLogSearchPage.htm") 
	public Map<String,Object> getWorkLogSearchPage(Long departId,String leaderName,String month,String empCode,String empCnName,Integer page,Integer rows){
	
		Map<String,Object> result = new HashMap<String,Object>();
		//分页
		PageModel<Map<String,Object>> pm=new PageModel<Map<String,Object>>();
		pm.setRows(new java.util.ArrayList<Map<String,Object>>());
		try{
			result = baseEmpWorkLogService.getWorkLogSearchPage(month,departId,leaderName,empCode,empCnName,page,rows);
		}catch(Exception e){
			logger.error("getWorkLogPage:",e);
			result.put("message",e.getMessage());
			result.put("success",false);
		}
		return result;
	}
	

	/**
	 * 审批
	 * @param request
	 * @param processId
	 * @param commentType
	 * @return
	 */
	@RequestMapping("/completeTask.htm")
	@ResponseBody
	public Map<String,Object> completeTask(HttpServletRequest request,String processId,String commentType){
		Map<String,Object> result = Maps.newHashMap();
		try {
			userTaskService.completeTask(request,processId,"",commentType);
			result.put("success", true);
			result.put("message","审批成功！");
		}catch (Exception e) {
			result.put("success", false);
			result.put("message", e.getMessage());
		}
		return result;
	}
	

    /**
     * 工作日志-工作日志查询-导出
     * @param departId
     * @param leaderName
     * @param month
     * @param empCode
     * @param empCnName
     * @param response
     * @param request
     * @throws IOException
     */
	@ResponseBody
	@RequestMapping("/exportWorkLog.htm")
	public void exportWorkLog(Long departId,String leaderName,String month,String empCode,String empCnName,HttpServletResponse response,HttpServletRequest request) throws IOException{
		
		OutputStream os = null;
		try {
			ResponseUtil.setDownLoadExeclInfo(response,request,"员工工作日志_");
			HSSFWorkbook hSSFWorkbook = baseEmpWorkLogService.exportWorkLog(month, departId, leaderName, empCode, empCnName);
			os = response.getOutputStream();
			hSSFWorkbook.write(os);
           os.flush();
           os.close();
		} catch (Exception e) {
			logger.error("导出延时工作登记表失败,原因=",e);
		}finally{
			if(null != os){
				os.flush();
				os.close();
			}
		}
	}
}
