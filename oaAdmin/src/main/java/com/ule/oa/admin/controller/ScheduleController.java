package com.ule.oa.admin.controller;

import java.io.IOException;
import java.io.OutputStream;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import com.ule.oa.admin.util.IsMenu;
import com.ule.oa.admin.util.IsOperation;
import com.ule.oa.admin.util.ResponseUtil;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.RabcResource;
import com.ule.oa.base.po.ScheduleGroup;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.RabcResourceService;
import com.ule.oa.base.service.ScheduleService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.JSON;
import com.ule.oa.common.utils.JsonWriter;
import com.ule.oa.common.utils.PageModel;
import com.ule.oa.common.utils.json.JSONUtils;

/**
 * 
  * @ClassName: ScheduleController
  * @Description: 排班
  * @author xujintao
  * @date 2019年3月6日 下午1:53:28
 */
@Controller
@RequestMapping("schedule")
public class ScheduleController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private ScheduleService scheduleService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private UserService userService;
	@Autowired
	private RabcResourceService resourceService;
	@Autowired
	private DepartService departService;
	/**
	 * 排班组别
	 * @param request
	 * @return
	 */
	@IsMenu
	@RequestMapping("/scheduleGroupSeting.htm")
	public ModelAndView scheduleClass(HttpServletRequest request){
		return new ModelAndView("base/schedule/schedule_group");
	}
	/**
	 * 排班员工管理查询
	 * @param request
	 * @return
	 */
	@IsMenu
	@RequestMapping("/empManagement.htm") 
	public ModelAndView empManagement(HttpServletRequest request){
		User user = userService.getCurrentUser();
		//查询员工所有菜单权限
    	List<RabcResource> resList = resourceService.getAllAdminTabListByUserId(user.getId());
    	boolean empClassUpdate = false;//修改权限标记
    	for(RabcResource data:resList){
			if("empClassUpdate".equals(data.getCode())){
				empClassUpdate = true;
			}
    	}
    	request.setAttribute("empClassUpdate", empClassUpdate);
		
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
    	
    	
		return new ModelAndView("base/schedule/empManagement");
	}
	/**
	 * 考勤管理-排班管理-排班员工管理查询
	 * @param depart
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getEmpClassListByCondition.htm")
	public PageModel<Employee> getvertimeManagePageList(Employee empClass){
		
		
		PageModel<Employee> pm=new PageModel<Employee>();
		pm.setRows(new java.util.ArrayList<Employee>());
		try {
			pm = scheduleService.getEmpClassListByCondition(empClass);
		} catch (Exception e) {
			logger.error("getOvertimeManagePageList",e);
		}
		
		return pm;
	}
	/**
	 * 考勤管理-排班管理-排班员工管理查询批量修改属性
	 * @param depart
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateEmpClassInfo.htm")
	public Map<String, Object> updateEmpClassInfo(String isWhetherScheduling,String empClassIds){
		
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			scheduleService.updateEmpClassInfo(isWhetherScheduling,empClassIds);
			map.put("code", "0000");
			map.put("message", "修改成功");
		}catch(Exception e){
			map.put("code", "9999");
			map.put("message", e.getMessage());
		}
		return map;
	}
	
	
	//排班定时接口
		
		
	/**
	 * 
	 * @param depart
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getGroupTreeList.htm")
	public JSON getTreeList(Depart depart){
	
		return JsonWriter.successfulResult(JSONUtils.write(scheduleService.getTreeList(depart)));
	}
	
	/**
	 * 查询部门下所有班组信息
	 * @param request
	 * @param departId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getGroupListByDepartId.htm")
	public String getGroupListByDepartId(HttpServletRequest request, Long departId){

		List<ScheduleGroup> groupList = scheduleService.getGroupListByDepartId(departId);
		return JSONUtils.write(groupList);
	}
	/**
	 * 根据班组id查询班组信息
	 * @param request
	 * @param groupId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getGroupInfoByGroupId.htm")
	public String getGroupInfoByGroupId(HttpServletRequest request, Long groupId){
	
		List<ScheduleGroup> groupList = scheduleService.getGroupInfoByGroupId(groupId);
		return JSONUtils.write(groupList);
	}
	
	/**
	 * 考勤管理-排班管理-排班组别设置-添加下级分组
	 * @param depart
	 * @return
	 */
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/addScheduleGroup.htm")
	public Map<String, Object> addScheduleGroup(ScheduleGroup group){
		
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			scheduleService.saveScheduleGroup(group);
			map.put("code", "0000");
			map.put("message", "保存成功");
		}catch(OaException o){
			map.put("code", "9999");
			map.put("message", o.getMessage());
		}catch(Exception e){
			map.put("code", "9999");
			map.put("message", e.getMessage());
		}
		return map;
	}
	
	/**
	 * 考勤管理-排班管理-排班组别设置-编辑分组
	 * @param depart
	 * @return
	 */
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/updateScheduleGroup.htm")
	public Map<String, Object> updateScheduleGroup(ScheduleGroup group){
		
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			scheduleService.saveScheduleGroup(group);
			map.put("code", "0000");
			map.put("message", "保存成功");
		}catch(OaException o){
			map.put("code", "9999");
			map.put("message", o.getMessage());
		}catch(Exception e){
			map.put("code", "9999");
			map.put("message", e.getMessage());
		}
		return map;
	}
	
	/**
	 * 考勤管理-排班管理-排班组别设置-删除分组
	 * @param depart
	 * @return
	 */
	//@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/deleteScheduleGroup.htm")
	public Map<String, Object> deleteScheduleGroup(Long groupId){
		
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			scheduleService.deleteScheduleGroup(groupId);
			map.put("code", "0000");
			map.put("message", "删除成功");
		}catch(OaException o){
			map.put("code", "9999");
			map.put("message", o.getMessage());
		}catch(Exception e){
			map.put("code", "9999");
			map.put("message", e.getMessage());
		}
		return map;
	}
	
	
	/**
	 * 查询组内所有员工信息
	 * @param request
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getAllGroupEmp.htm")
	public String getAllGroupEmp(HttpServletRequest request, Long id,String condition){
		
		List<Employee> empList = scheduleService.getAllGroupEmp(id,condition);
		return JSONUtils.write(empList);
	}
	
	/**
	 * 查询该组所属部门下所有未分配组的员工
	 * @param request
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getUngroupedEmp.htm")
	public String getUngroupedEmp(HttpServletRequest request,Employee emp){
		
		List<Employee> empList = scheduleService.getUngroupedEmp(emp);
		return JSONUtils.write(empList);
	}
	
	/**
	 * 考勤管理-排班管理-排班组别设置-添加组员
	 * @param empList
	 * @param groupId
	 * @return
	 */
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/addMember.htm")
	public Map<String, Object> addMember(String empList,Long groupId){
	
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			scheduleService.addMember(empList,groupId);
			map.put("code", "0000");
			map.put("message", "保存成功");
		}catch(OaException o){
			map.put("code", "9999");
			map.put("message", o.getMessage());
		}catch(Exception e){
			map.put("code", "9999");
			map.put("message", e.getMessage());
		}
		
		return map;
	}
	
	//考勤管理-排班管理-排班组别设置-删除组员
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/delMember.htm")
	public Map<String, Object> delMember(Long empId,Long groupId){
		
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			scheduleService.delMember(empId,groupId);
			map.put("code", "0000");
			map.put("message", "删除成功");
		}catch(OaException o){
			map.put("code", "9999");
			map.put("message", o.getMessage());
		}catch(Exception e){
			map.put("code", "9999");
			map.put("message", e.getMessage());
		}
		
		return map;
	}
	
	/**
	 * 查询所有需要排班的部门
	  * getScheduleDepartList(这里用一句话描述这个方法的作用)
	  * @Title: getScheduleDepartList
	  * @Description: TODO
	  * @return    设定文件
	  * List<Depart>    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping(value="/getScheduleDepartList.htm")
	public List<Depart> getScheduleDepartList(){
	
		return scheduleService.getScheduleDepartList();
	}
	//导出排班模板
	@ResponseBody
	@RequestMapping("/exportScheduleTemplate.htm")
	public void exportScheduleTemplate(HttpServletResponse response,HttpServletRequest request,Long departId,Long groupId) throws IOException{
		
		OutputStream os = null;
		try {
			ResponseUtil.setDownLoadExeclInfo(response,request,"排班导入模板");
			HSSFWorkbook hSSFWorkbook = scheduleService.exportScheduleTemplate(departId,groupId);
			os = response.getOutputStream();
			hSSFWorkbook.write(os);
		}catch (Exception e) {
			logger.error("下载排班导入模板失败,原因=",e);
		}finally{
			if(null != os){
				os.flush();
				os.close();
			}
		}
	}
	/**
	 * 考勤管理-排班管理-排班-上传排班
	 * @param file
	 * @param departId
	 * @param groupId
	 * @return
	 */
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping(value="/importScheduleTemplate.htm",method = RequestMethod.POST)
	public Map<String, String> importScheduleTemplate(@RequestParam(value="file",required = false)MultipartFile file,Long departId,Long groupId) {
		
		String result = "failed";
		String resultMsg = null;
		try {
			Map<String,String> resultMap = scheduleService.importScheduleTemplate(file,departId,groupId);
			result = resultMap.get("result"); 
			resultMsg =  resultMap.get("resultMsg");
		} catch (OaException o) {
			resultMsg = o.getMessage();
		} catch (Exception e) {
			resultMsg = "网络延迟，请稍后重试";
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("result", result);
		map.put("resultMsg", resultMsg);
        return map;
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	@IsMenu
	@RequestMapping("/departManage.htm")
	public ModelAndView departManage(HttpServletRequest request,Depart depart){
		try {
			request.setAttribute("companyId", employeeService.getCurrentEmployee().getCompanyId());
			
		} catch (OaException e) {
			
		}
		return new ModelAndView("base/schedule/depart_manage");
	}
	
	//考勤管理-排班管理-排班部门管理-修改
	//@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/updateWhetherSchdule.htm")
	public Map<String, Object> updateWhetherSchdule(Depart depart){
		
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			scheduleService.updateDepartWhetherScheduling(depart);
			map.put("code", "0000");
			map.put("message", "修改成功");
		}catch(Exception e){
			map.put("code", "9999");
			map.put("message", e.getMessage());
		}
		
		return map;
	}
	
	
	/**
	 * 考勤管理-排班管理-调班-上传调班
	 * @param file
	 * @param departId
	 * @param groupId
	 * @return
	 */
	//@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping(value="/importChangeClassTemplate.htm",method = RequestMethod.POST)
	public Map<String, String> importChangeClassTemplate(@RequestParam(value="file",required = false)MultipartFile file,String month,Long departId,Long groupId) {
		
		String result = "failed";
		String resultMsg = null;
		try {
			Map<String,String> resultMap = scheduleService.importChangeClassTemplate(file,month,departId,groupId);
			result = resultMap.get("result"); 
			resultMsg =  resultMap.get("resultMsg");
		} catch (OaException o) {
			resultMsg = o.getMessage();
		} catch (Exception e) {
			resultMsg = "网络延迟，请稍后重试";
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("result", result);
		map.put("resultMsg", resultMsg);
        return map;
	}
	
	/**
	 *  考勤管理-排班管理-调班-下载调班模板
	 * @param response
	 * @param request
	 * @param departId
	 * @param groupId
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping("/exportChangeClassTemplate.htm")
	public void exportChangeClassTemplate(HttpServletResponse response,HttpServletRequest request,String month,Long departId,Long groupId) throws IOException{
		
		OutputStream os = null;
		try {
			ResponseUtil.setDownLoadExeclInfo(response,request,"调班导入模板");
			HSSFWorkbook hSSFWorkbook = scheduleService.exportChangeClassTemplate(month, departId,groupId);
			os = response.getOutputStream();
			hSSFWorkbook.write(os);
		}catch (Exception e) {
			logger.error("下载调班导入模板失败,原因=",e);
		}finally{
			if(null != os){
				os.flush();
				os.close();
			}
		}
	}
}
