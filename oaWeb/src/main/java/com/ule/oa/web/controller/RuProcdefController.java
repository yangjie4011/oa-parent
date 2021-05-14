package com.ule.oa.web.controller;


import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.ule.oa.base.common.OaCommon;
import com.ule.oa.base.po.EmpApplicationAbnormalAttendance;
import com.ule.oa.base.po.EmpApplicationBusiness;
import com.ule.oa.base.po.EmpApplicationLeave;
import com.ule.oa.base.po.EmpApplicationLeaveDetail;
import com.ule.oa.base.po.EmpApplicationOutgoing;
import com.ule.oa.base.po.EmpApplicationOvertime;
import com.ule.oa.base.po.EmpApplicationOvertimeLate;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.RuProcdef;
import com.ule.oa.base.po.RunTask;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.EmpApplicationAbnormalAttendanceService;
import com.ule.oa.base.service.EmpApplicationBusinessService;
import com.ule.oa.base.service.EmpApplicationLeaveDetailService;
import com.ule.oa.base.service.EmpApplicationLeaveService;
import com.ule.oa.base.service.EmpApplicationOutgoingService;
import com.ule.oa.base.service.EmpApplicationOvertimeLateService;
import com.ule.oa.base.service.EmpApplicationOvertimeService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.RuProcdefService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.PageModel;
import com.ule.oa.web.util.ReturnUrlUtil;

@Controller
@RequestMapping("/ruProcdef_delete")
public class RuProcdefController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private RuProcdefService ruProcdefService;
	@Autowired
	private UserService userService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private EmpApplicationAbnormalAttendanceService empApplicationAbnormalAttendanceService;
	@Autowired
	private EmpApplicationLeaveService empApplicationLeaveService;
	@Autowired
	private EmpApplicationLeaveDetailService empApplicationLeaveDetailService;
	@Autowired
	private EmpApplicationOvertimeService empApplicationOvertimeService;
	@Autowired
	private EmpApplicationOvertimeLateService empApplicationOvertimeLateService;
	@Autowired
	private EmpApplicationBusinessService empApplicationBusinessService;
	@Autowired
	private EmpApplicationOutgoingService empApplicationOutgoingService;
	@RequestMapping("/index.htm")
	public ModelAndView index(){
		return new ModelAndView("base/cooperation/index");
	}
	
	/**
	 * 跳转已处理页面
	 * @return
	 */
	@RequestMapping("/ruProcdef_processing.htm")
	public ModelAndView ruProcdef_processing(){
		return new ModelAndView("base/cooperation/ruProcdef_processing");
	}
	
	/**
	 * 跳转我的审批页面
	 * @return
	 */
	@RequestMapping("/my_examine.htm")
	public ModelAndView my_examine(HttpServletRequest request, String current, String urlType){
		User user = userService.getCurrentUser();
		Employee employee = new Employee();
    	employee.setReportToLeader(user.getEmployeeId());
    	int leaderCount = employeeService.getListByConditionCount(employee);
    	if(leaderCount > 0){
    		request.setAttribute("isLeader", "true");
    	}else{
    		request.setAttribute("isLeader", "false");
    	}
    	if("企业文化专员".equals(user.getPosition().getPositionName())||"人力资源高级经理".equals(user.getPosition().getPositionName())||"HRBP专员".equals(user.getPosition().getPositionName())||"人力资源及行政总监".equals(user.getPosition().getPositionName())||"人力资源经理".equals(user.getPosition().getPositionName())
    			||"高级HRBP".equals(user.getPosition().getPositionName())||"HRBP".equals(user.getPosition().getPositionName())
    			||"HRBP助理".equals(user.getPosition().getPositionName())||"人力资源助理".equals(user.getPosition().getPositionName())
    			||"薪资福利高级专员".equals(user.getPosition().getPositionName())||"高级企业文化专员".equals(user.getPosition().getPositionName())){
    		request.setAttribute("isLeader", "true");
    	}
    	if(current == null || current.equals("")) {
    		current = "0";
    	}
    	request.setAttribute("current", current);
    	request.setAttribute("returnUrl", ReturnUrlUtil.getReturnUrl(urlType));
		return new ModelAndView("base/cooperation/my_examine");
	}
	
	/**
	  * getCount(我的待办查询总的记录数)
	  * @Title: getCount
	  * @Description: 我的待办查询总的记录数
	  * @param ruProcdef
	  * @return    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/getCount.htm")
	public Integer getCount(RuProcdef ruProcdef){
		User user = userService.getCurrentUser();
		ruProcdef.setAssigneeId(user.getEmployee().getId());
		return ruProcdefService.getCount(ruProcdef);
	}
	
	/**
	 * 分页查询列表
	 * @param ruProcdef
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getPageList.htm")
	public PageModel<RuProcdef> getPageList(RuProcdef ruProcdef, String flag){
		if("main".equals(flag)) {
			ruProcdef.setOffset(0);
			ruProcdef.setLimit(5);
		} else {
			ruProcdef.setOffset(null);
			ruProcdef.setLimit(null);
		}
		PageModel<RuProcdef> pm = new PageModel<RuProcdef>();
		pm.setRows(new java.util.ArrayList<RuProcdef>());
		pm.setTotal(0);
		pm.setPageNo(1);
		//获取当前登录人
		User user = userService.getCurrentUser();
		logger.info("ruProcdef/getPageList代办:"+user.getEmployeeId());
		ruProcdef.setAssigneeId(user.getEmployee().getId());
		try {
			//FIXME:销假
			pm = ruProcdefService.getByPagenation(ruProcdef);
			if("allsel".equals(flag)) {
				for (RuProcdef rp : pm.getRows()) {
					if(rp.getReProcdefCode().equals(RunTask.RUN_CODE_41)||rp.getReProcdefCode().equals(RunTask.RUN_CODE_40)||rp.getReProcdefCode().equals(RunTask.RUN_CODE_120)) {//考勤异常申诉
						EmpApplicationAbnormalAttendance empApplicationAbnormalAttendance = empApplicationAbnormalAttendanceService.getById(Long.valueOf(rp.getEntityId()));
						String view3 = "";
						String view4 = "";
						if(empApplicationAbnormalAttendance.getStartTime()==null){
							view3 = "空卡~";
						}else{
							view3 = DateUtils.format(empApplicationAbnormalAttendance.getStartTime(), "HH:mm")+"~";
						}
						if(empApplicationAbnormalAttendance.getEndTime()==null){
							view3 += "空卡";
						}else{
							view3 += DateUtils.format(empApplicationAbnormalAttendance.getEndTime(), "HH:mm");
						}
						if(empApplicationAbnormalAttendance.getStartPunchTime()==null){
							view4 = "空卡~";
						}else{
							view4 = DateUtils.format(empApplicationAbnormalAttendance.getStartPunchTime(), "HH:mm")+"~";
						}
						if(empApplicationAbnormalAttendance.getEndPunchTime()==null){
							view4 += "空卡";
						}else{
							view4 += DateUtils.format(empApplicationAbnormalAttendance.getEndPunchTime(), "HH:mm");
						}
						if(empApplicationAbnormalAttendance.getApplyType().intValue()==1){
							rp.setView6(empApplicationAbnormalAttendance.getCnName());
						}
						rp.setView3(view3);
						rp.setView4(view4);
						rp.setView5(empApplicationAbnormalAttendance.getReason());
					} else if(rp.getReProcdefCode().equals(RunTask.RUN_CODE_60)) {//请假申请
						EmpApplicationLeave empApplicationLeave = empApplicationLeaveService.getById(Long.valueOf(rp.getEntityId()));
						List<EmpApplicationLeaveDetail> ealList = empApplicationLeaveDetailService.getList(Long.valueOf(rp.getEntityId()));
						String view3 = "";
						String view4 = "";
						Date startTime = null;
						Date endTime = null;
						Double leaveDays = 0D;
						for (EmpApplicationLeaveDetail empApplicationLeaveDetail : ealList) {
							if(!view3.equals("")) {
								view3 = view3 + "/";
							}
							view3 = view3 + OaCommon.getLeaveTypeMap().get(empApplicationLeaveDetail.getLeaveType());
							if(startTime == null) {
								startTime = empApplicationLeaveDetail.getStartTime();
							} else {
								if(DateUtils.compareDate(startTime, empApplicationLeaveDetail.getStartTime()) == 1 ) {
									startTime = empApplicationLeaveDetail.getStartTime();
								}
							}
							
							if(endTime == null) {
								endTime = empApplicationLeaveDetail.getEndTime();
							} else {
								if(DateUtils.compareDate(endTime, empApplicationLeaveDetail.getEndTime()) == 2) {
									endTime = empApplicationLeaveDetail.getEndTime();
								}
							}
							leaveDays = leaveDays + empApplicationLeaveDetail.getLeaveDays();
						}
						
						view4 = view4 + "&nbsp;&nbsp;" + leaveDays + "天";
						view4 = getTime(startTime, endTime);
						rp.setView3(view3);
						rp.setView4(view4);
						rp.setView5(empApplicationLeave.getReason());
					} else if(rp.getReProcdefCode().equals(RunTask.RUN_CODE_30)) {//延时工作
						EmpApplicationOvertime empApplicationOvertime = empApplicationOvertimeService.getById(Long.valueOf(rp.getEntityId()));
						if(empApplicationOvertime.getApplyType().equals(100)) {
							rp.setView3(empApplicationOvertime.getProjectName());
						} else {
							rp.setView3(OaCommon.getApplyTypeMap().get(empApplicationOvertime.getApplyType()));
						}
						String view4 = "";
						if(empApplicationOvertime.getActualStartTime() != null) {
							view4 = getTime(empApplicationOvertime.getActualStartTime(), empApplicationOvertime.getActualEndTime());
							view4 = view4 + "&nbsp;&nbsp;" + empApplicationOvertime.getActualDuration() + "小时";
						} else {
							view4 = getTime(empApplicationOvertime.getExpectStartTime(), empApplicationOvertime.getExpectEndTime());
							view4 = view4 + "&nbsp;&nbsp;" + empApplicationOvertime.getExpectDuration() + "小时";
						}
						rp.setView4(view4);
						rp.setView5(empApplicationOvertime.getReason());
					} else if(rp.getReProcdefCode().equals(RunTask.RUN_CODE_80)||rp.getReProcdefCode().equals(RunTask.RUN_CODE_81)) {//出差申请
						EmpApplicationBusiness empApplicationBusiness = empApplicationBusinessService.getById(Long.valueOf(rp.getEntityId()));
						rp.setView3(empApplicationBusiness.getReason());
						rp.setView4(empApplicationBusiness.getAddress());
						String view5 = getTime(empApplicationBusiness.getStartTime(), empApplicationBusiness.getEndTime(),"MM-dd", "yyyy-MM-dd");
						view5 = view5 + "&nbsp;&nbsp;" + empApplicationBusiness.getDuration() + "天";
						rp.setView5(view5);
						rp.setView6(OaCommon.getVehicleTypeMap().get(empApplicationBusiness.getVehicle()));
					} else if(rp.getReProcdefCode().equals(RunTask.RUN_CODE_100)) {//晚到申请
						EmpApplicationOvertimeLate empApplicationOvertimeLate = empApplicationOvertimeLateService.getById(Long.valueOf(rp.getEntityId()));
						rp.setView3(DateUtils.format(empApplicationOvertimeLate.getOverTimeStartTime()) + "~" + DateUtils.format(empApplicationOvertimeLate.getOverTimeEndTime()));
						rp.setView4(empApplicationOvertimeLate.getAllowTime());
						rp.setView5(empApplicationOvertimeLate.getActualTime());
						rp.setView6(empApplicationOvertimeLate.getReason());
					} else if(rp.getReProcdefCode().equals(RunTask.RUN_CODE_70)) {//外出申请
						EmpApplicationOutgoing empApplicationOutgoing = empApplicationOutgoingService.getById(Long.valueOf(rp.getEntityId()));
						rp.setView3(empApplicationOutgoing.getAddress());
						String view4 = getTime(empApplicationOutgoing.getStartTime(), empApplicationOutgoing.getEndTime());
						view4 = view4 + "&nbsp;&nbsp;" + empApplicationOutgoing.getDuration() + "小时";
						rp.setView4(view4);
						rp.setView5(empApplicationOutgoing.getReason());
					} else if(rp.getReProcdefCode().equals(RunTask.RUN_CODE_90)||rp.getReProcdefCode().equals(RunTask.RUN_CODE_91)) {//出差申请
						EmpApplicationBusiness empApplicationBusiness = empApplicationBusinessService.getById(Long.valueOf(rp.getEntityId()));
						
//						empApplicationBusinessDetailService.getListByCondition(Long.valueOf(rp.getEntityId()));
						rp.setView3(empApplicationBusiness.getReason());
						rp.setView4(empApplicationBusiness.getAddress());
						String view5 = getTime(empApplicationBusiness.getStartTime(), empApplicationBusiness.getEndTime(),"MM-dd", "yyyy-MM-dd");
						view5 = view5 + "&nbsp;&nbsp;" + empApplicationBusiness.getDuration() + "天";
						rp.setView5(view5);
						rp.setView6(OaCommon.getVehicleTypeMap().get(empApplicationBusiness.getVehicle()));
					}
				}
			}
		} catch (Exception e) {
			pm.getRows();
			logger.error(e.getMessage(),e);
		}
		return pm;
	}
	
	public String getTime(Date startTime, Date endTime,String format1, String format) {
		String view4 = "";
		if(DateUtils.compareDate(DateUtils.parse(DateUtils.format(startTime), "yyyy"), DateUtils.parse(DateUtils.getNow(), "yyyy")) == 0) {
			view4 = DateUtils.format(startTime, format1);
		} else {
			view4 = view4 + DateUtils.format(startTime, format);
		}
		
		if(DateUtils.compareDate(DateUtils.parse(DateUtils.format(startTime), "yyyy-MM-dd"), DateUtils.parse(DateUtils.format(endTime), "yyyy-MM-dd")) == 0) {
			if(!format.equals("yyyy-MM-dd")) {
				view4 = view4 + "~" + DateUtils.format(endTime, "HH:mm") ;
			}
		} else if(DateUtils.compareDate(DateUtils.parse(DateUtils.format(startTime), "yyyy"), DateUtils.parse(DateUtils.format(endTime), "yyyy")) == 0) {
			view4 = view4 + "~" + DateUtils.format(endTime, format1);
		} else {
			view4 = view4 + "~" + DateUtils.format(endTime, format);
		}
		return view4;
	}
	
	public String getTime(Date startTime, Date endTime) {
		return getTime(startTime,endTime,"MM-dd HH:mm","yyyy-MM-dd HH:mm");
	}

}
