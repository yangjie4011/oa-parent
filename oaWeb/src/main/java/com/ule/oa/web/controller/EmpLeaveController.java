 /**
 * @Title: EmpLeaveController.java
 * @Package com.ule.oa.web.controller
 * @Description: TODO
 * @Copyright: Copyright (c) 2017
 * @Company:邮乐网 *
 * @author zhangjintao
 * @date 2017年6月26日 上午10:43:57
 */

package com.ule.oa.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.ule.oa.base.po.EmpApplicationLeave;
import com.ule.oa.base.po.EmpApplicationLeaveAbolish;
import com.ule.oa.base.po.EmpApplicationLeaveDetail;
import com.ule.oa.base.po.LeaveRecord;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.EmpApplicationLeaveAbolishService;
import com.ule.oa.base.service.EmpApplicationLeaveService;
import com.ule.oa.base.service.EmpLeaveService;
import com.ule.oa.base.service.EmployeeAppService;
import com.ule.oa.base.service.LeaveRecordService;
import com.ule.oa.base.service.impl.ActivitiServiceImpl;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.web.util.ReturnUrlUtil;
import com.ule.oa.web.util.Token;

 /**
 * @ClassName: EmpLeaveController
 * @Description: 请假相关
 * @author zhangJintao
 * @date 2017年6月26日 上午10:43:57
 */

@Controller
@RequestMapping("empLeave")
public class EmpLeaveController {

	@Resource
	private EmpLeaveService empLeaveService;
	@Resource
	private LeaveRecordService leaveRecordService;
	@Autowired
	private EmployeeAppService employeeAppService;
	@Autowired
	private EmpApplicationLeaveService empApplicationLeaveService;
	@Autowired
	private ActivitiServiceImpl activitiServiceImpl;
	@Autowired
	private EmpApplicationLeaveAbolishService empApplicationLeaveAbolishService;
	@Autowired
	private ConfigService configService;
	
    @RequestMapping("/myLeaveView")
	public ModelAndView myLeaveView(String urlType) throws OaException{//我得考勤主页面
    	Long empId = employeeAppService.getCurrentEmployeeId();
    	Map<String, Object> map = empLeaveService.getMyViewLeave(empId);
    	//年假是否能透支
		boolean overDrawFlag = configService.checkYearLeaveCanOverDraw();
		map.put("overDrawFlag", overDrawFlag);
    	
    	//获取其它调休type=13的假期（由人事给员工增加的假期）
		double otherRestLeaveCount = empLeaveService.getOtherRestLeaveCount(empId);
		//map.put("remainRestHolidayDays", otherRestLeaveCount+(map.get("remainRestHolidayDays")!=null?(Double)map.get("remainRestHolidayDays"):0d));
		map.put("remainRestHolidayDays", otherRestLeaveCount);
    	String returnUrl = "/login/index.htm";//返回主页面
    	if(urlType != null && urlType.equals("1")){//返回个人中心
    		returnUrl = "/employee/indexPerson.htm";
    	}
		map.put("returnUrl",returnUrl);
		return new ModelAndView("base/leave/myLeave",map);
	}

    @RequestMapping("/yearLeaveView")
	public ModelAndView yearLeaveView(Long employeeId,int year) throws OaException{//年假详情
    	Map<String, Object> map = empLeaveService.getYearLeaveView(employeeId,year);
		return new ModelAndView("base/leave/yearLeave",map);
	}

    @RequestMapping("/sickLeaveView")
	public ModelAndView sickLeaveView(Long employeeId,int year) throws OaException{//年假详情
    	Map<String, Object> map = empLeaveService.getSickLeaveView(employeeId,year);
		return new ModelAndView("base/leave/sickLeave",map);
	}

    @RequestMapping("/restLeaveView")
	public ModelAndView restLeaveView(Long employeeId,int year) throws OaException{//年假详情
    	Map<String, Object> map = empLeaveService.getRestLeaveView(employeeId,year);
		return new ModelAndView("base/leave/restLeave",map);
	}
    
    @RequestMapping("/leaveRecord.htm")
	@Token(generate=true)//生成token
    public String approval(HttpServletRequest request,String urlType){
		request.setAttribute("returnUrl", ReturnUrlUtil.getReturnUrl(urlType));
		return "base/leave/leaveRecord";
	}
    
    /**首页的考勤展现**/
	@RequestMapping("/getLeaveOfMain.htm")
	@ResponseBody
    public ModelMap getLeaveOfMain() throws OaException{
		ModelMap map = new ModelMap();
		Long empId = employeeAppService.getCurrentEmployeeId();
		Map<String, Object> dataMap = empLeaveService.getMyViewLeave(empId);
		
		map.put("leftYearLeave", dataMap.get("remainYearHolidayDays"));
		map.put("leftSickLeave", dataMap.get("remainSickHolidayDays"));
		//获取其它调休type=13的假期（由人事给员工增加的假期）
		double otherRestLeaveCount = empLeaveService.getOtherRestLeaveCount(empId);
		//map.put("leftRestLeave", otherRestLeaveCount+(dataMap.get("remainRestHolidayDays")!=null?(Double)dataMap.get("remainRestHolidayDays"):0d));
		map.put("leftRestLeave", otherRestLeaveCount);
		return map;
	}
	
	@RequestMapping("/SickLeaveSplit.htm")
	@ResponseBody
	public Map<String,Object> refreshOtherLeave(){
		Map<String,Object> result = new HashMap<>();
		try {
			empLeaveService.SickLeaveSplit();
			result.put("success",true);
		} catch (Exception e) {
			result.put("message",e.getMessage());
			result.put("success",false);
		}
		return result;
	}
	
	/**
	 * 获取流水列表
	 * @param yearType
	 * @return
	 */
	@RequestMapping("/getRecordList.htm")
	@ResponseBody
	public Map<String,Object> getRecordList(String yearType){
		Map<String,Object> result = new HashMap<>();
		try {
			List<LeaveRecord> list = leaveRecordService.getRecordListByYearAndEmployee(yearType);
			result.put("success",true);
			result.put("list",list);
		} catch (Exception e) {
			result.put("message",e.getMessage());
			result.put("success",false);
		}
		return result;
	}
	
	/**
	 * 流水关联单据详情
	 * @param employeeId
	 * @param year
	 * @return
	 * @throws OaException
	 */
	@RequestMapping("/billDetail")
	public ModelAndView billDetail(Long billId,String billType) throws OaException{
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("billType", billType);
    	if("leave".equals(billType)) {
    		EmpApplicationLeave leave = empApplicationLeaveService.getById(billId);
    		map.put("leave", leave);
			List<EmpApplicationLeaveDetail> detailList = empApplicationLeaveService.getLeaveDetailList(billId);
			map.put("detailList", detailList);
			map.put("canApprove", false);
			if (leave.getApprovalStatus().intValue() == 100 || leave.getApprovalStatus().intValue() == 200) {
				Task task = activitiServiceImpl.queryTaskByProcessInstanceId(leave.getProcessInstanceId());
				map.put("actName", task != null ? task.getName() : "");
			}
    	}else if("cancelLeave".equals(billType)) {
    		EmpApplicationLeaveAbolish leaveAbolish = empApplicationLeaveAbolishService.getById(billId);
			if(leaveAbolish!=null){
				map.put("leaveAbolish", leaveAbolish);
				EmpApplicationLeave leave  = empApplicationLeaveService.getById(leaveAbolish.getLeaveId());
				map.put("leave", leave);
				List<EmpApplicationLeaveDetail> detailList = empApplicationLeaveService.getLeaveDetailList(leave.getId());
				map.put("leaveDetail", detailList.get(0));
				map.put("isSelf", false);
				map.put("canApprove", false);
				Map<String,String> map1 = empApplicationLeaveAbolishService.
						getActualEndTime(leave.getEmployeeId(), detailList.get(0).getLeaveType(), detailList.get(0).getStartTime(), leaveAbolish.getStartTime());
				map.put("actualEndTime", map1.get("endTime"));
				//审批显示
				if(leaveAbolish.getApprovalStatus().intValue()==100||leaveAbolish.getApprovalStatus().intValue()==200) {
					Task task = activitiServiceImpl.queryTaskByProcessInstanceId(leaveAbolish.getProcessInstanceId());
					map.put("actName", task!=null?task.getName():"");
				}
			}
    	}
    	return new ModelAndView("base/leave/bill_detail",map);
    }
	
}
