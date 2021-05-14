package com.ule.oa.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.flowable.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ule.oa.base.common.OaCommon;
import com.ule.oa.base.mapper.AnnualVacationMapper;
import com.ule.oa.base.mapper.EmpLeaveMapper;
import com.ule.oa.base.po.AnnualVacation;
import com.ule.oa.base.po.ClassSetting;
import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.EmpApplicationLeave;
import com.ule.oa.base.po.EmpApplicationLeaveDetail;
import com.ule.oa.base.po.EmpFamilyMember;
import com.ule.oa.base.po.EmpLeave;
import com.ule.oa.base.po.EmpLeaveReport;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.po.HiActinst;
import com.ule.oa.base.po.RunTask;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.AnnualVacationService;
import com.ule.oa.base.service.ClassSettingService;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.EmpApplicationLeaveDetailService;
import com.ule.oa.base.service.EmpApplicationLeaveService;
import com.ule.oa.base.service.EmpFamilyMemberService;
import com.ule.oa.base.service.EmpLeaveReportService;
import com.ule.oa.base.service.EmployeeClassService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.HiActinstService;
import com.ule.oa.base.service.RunTaskService;
import com.ule.oa.base.service.SmsService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.service.impl.ActivitiServiceImpl;
import com.ule.oa.common.spring.config.CustomPropertyPlaceholderConfigurer;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.RedisUtils;
import com.ule.oa.common.utils.UUIDGenerator;
import com.ule.oa.web.util.ReturnUrlUtil;
import com.ule.oa.web.util.Token;

/**
 * @ClassName: 请假申请
 * @Description: 请假申请
 * @author yangjie
 * @date 2017年6月14日
 */
@Controller
@RequestMapping("empApplicationLeave")
public class EmpApplicationLeaveController {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private EmpApplicationLeaveService empApplicationLeaveService;
	@Autowired
	private UserService userService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private ConfigService configService;
	@Autowired
	private HiActinstService hiActinstService;
	@Autowired
	private EmployeeClassService employeeClassService;
	@Autowired
	private EmpFamilyMemberService empFamilyMemberService;
	@Autowired
	private RunTaskService runTaskService;
	@Autowired
	private ClassSettingService classSettingService;
	@Autowired
	private ActivitiServiceImpl activitiServiceImpl;
	@Autowired
	private EmpLeaveMapper empLeaveMapper;
	@Autowired
	private EmpLeaveReportService empLeaveReportService;
	@Autowired
	private AnnualVacationService annualVacationService;
	@Autowired
	private AnnualVacationMapper annualVacationMapper;
	@Autowired
	private EmpApplicationLeaveDetailService empApplicationLeaveDetailService;
	@Autowired
	private SmsService smsService;
	private final static String ENV = (String) CustomPropertyPlaceholderConfigurer.getProperty("cache.env");

	// 请假申请首页
	@RequestMapping("/index.htm")
	@Token(generate = true) // 生成token
	public String index(HttpServletRequest request, String urlType) {
		User user = userService.getCurrentUser();
		try {
			logger.info("请假首页：" + user.getEmployeeId());
			request.setAttribute("returnUrl", ReturnUrlUtil.getReturnUrl(urlType));
			// 新增员工id
			request.setAttribute("employeeId", user.getEmployeeId());
		} catch (Exception e) {
			logger.error(user.getEmployee().getCnName() + "请假-首页：" + e.toString());
			return "base/empApplicationLeave/index";
		}
		return "base/empApplicationLeave/index";
	}

	// 请假申请首页
	@RequestMapping("/leaveRule.htm")
	public String leaveRule(HttpServletRequest request) {
		return "base/empApplicationLeave/leaveRule";
	}

	// 获取员工产前假信息
	@ResponseBody
	@RequestMapping("/getAntenatalLeave.htm")
	public Map<String, Object> getAntenatalLeave(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		String birthDays = request.getParameter("birthDays");
		Date birthDate = DateUtils.addDay(DateUtils.parse(birthDays, DateUtils.FORMAT_SHORT), -15);
		User user = userService.getCurrentUser();
		try {
			map.put("birthDays", DateUtils.format(birthDate, DateUtils.FORMAT_SHORT));
		} catch (Exception e) {
			logger.error(user.getEmployee().getCnName() + "请假-获取产前假信息：" + e.toString());
			map.put("birthDays", DateUtils.format(birthDate, DateUtils.FORMAT_SHORT));
		}
		return map;
	}

	// 获取员工产假信息 计算 开始日期 和结束日期
	@ResponseBody
	@RequestMapping("/getLeaveCountStartEndTime.htm")
	public Map<String, Object> getLeaveCountStartEndTime(HttpServletRequest request) {
		Map<String, Object> map = empApplicationLeaveService.getExtraMaternityLeaveDays(request);
		return map;
	}

	// 丧假撤销 把 type 当做结束时间
	@ResponseBody
	@RequestMapping("/countLeave10.htm")
	public Map<String, Object> countLeave10(HttpServletRequest request, String type, String date, String leaveType) {
		Map<String, Object> map = new HashMap<String, Object>();
		User user = userService.getCurrentUser();
		try {
			// 新增 丧假需求 优先给 丧假一个计算 通过传过来的 type 天数 来计算结束时间
			if ("10".equals(leaveType)) {
				boolean endTimeFlag = true;
				int countEnd = 0;// 计数 看要往后推多少天
				int i = 0;// 循环累加
				if (type.equals("1") || type.equals("3")) {
					String endTime = null;
					int days = Integer.parseInt(type);

					while (endTimeFlag) {
						Integer dateType = annualVacationService
								.getDayType(DateUtils.addDay(DateUtils.parse(date, DateUtils.FORMAT_SHORT), i));

						if (dateType == 1) {
							countEnd = countEnd + 1;
						}
						if (countEnd == days) {
							Date endTimes = DateUtils.addDay(DateUtils.parse(date, DateUtils.FORMAT_SHORT), i);
							endTime = DateUtils.format(endTimes, DateUtils.FORMAT_SHORT);
							endTimeFlag = false;
						}
						i++;
					}
					map.put("endTime", endTime);

				} else { // 撤销假期 type 作为结束时间 来计算
					Date endTime = DateUtils.parse(type, DateUtils.FORMAT_SHORT);
					while (endTimeFlag) {
						Date addDayFor = DateUtils.addDay(DateUtils.parse(date, DateUtils.FORMAT_SHORT), i);
						Integer dateType = annualVacationService.getDayType(addDayFor);
						if (dateType == 1) {
							countEnd = countEnd + 1;
						}
						if (addDayFor.equals(endTime)) {
							endTimeFlag = false;
						}
						i++;
					}
					map.put("days", countEnd);
				}
				map.put("code", "0000");
			}
		} catch (Exception e) {
			map.put("code", "9999");
			map.put("message", "系统异常！");
			logger.error(user.getEmployee().getCnName() + "计算丧假异常原因：" + e.toString());
			return map;
		}
		return map;
	}

	// 获取事假、年假、病假、调休,其他的请假开始，结束时间 //丧假撤销 把 type 当做结束时间
	@ResponseBody
	@RequestMapping("/getLeaveHours.htm")
	public Map<String, Object> getLeaveHours(HttpServletRequest request, String type, String date, String leaveType) {
		Map<String, Object> map = new HashMap<String, Object>();
		User user = userService.getCurrentUser();
		try {
			map = empApplicationLeaveService.getLeaveHours(user.getEmployeeId(), type, date, leaveType);
		} catch (Exception e) {
			map.put("code", "9999");
			map.put("message", "系统异常！");
			logger.error(user.getEmployee().getCnName() + "获取请假开始结束小时数：", e);
			return map;
		}
		return map;
	}

	// 获取子女
	@SuppressWarnings("rawtypes")
	@ResponseBody
	@RequestMapping("/getChildren.htm")
	public Map getChildren(HttpServletRequest request) {
		User user = userService.getCurrentUser();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = empApplicationLeaveService.getChildren(user.getEmployee().getId());
		} catch (Exception e) {
			logger.error(user.getEmployee().getCnName() + "请假-获取子女数：" + e.toString());
		}
		return map;
	}

	// 获取假期类型
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ResponseBody
	@RequestMapping("/getLeaveTypes.htm")
	public List<Map> getLeaveTypes(EmpApplicationLeave leave) {
		User user = userService.getCurrentUser();
		Integer year = Integer.valueOf(DateUtils.getYear(new Date()))-1;
		List<Map> result = new ArrayList<Map>();
		try {
			// 查看员工入职天数，员工性别
			if (user != null && user.getEmployeeId() != null) {
				Config sysConfig = new Config();
				sysConfig.setCode("leaveType");
				List<Config> leaveTypeList = configService.getListByCondition(sysConfig);
				Employee employee = employeeService.getById(user.getEmployeeId());
				if (employee != null && employee.getFirstEntryTime() != null) {
					// 入职未超过3个月
					// 入职未超过3个月 他们想这样做5月23号入职 8月23号可请
					Date flagStaus = DateUtils.addMonth(employee.getFirstEntryTime(), 3);
					// 2-date小于d2
					if (2 != DateUtils.compareDate(flagStaus, new Date())) {
						for (Config config : leaveTypeList) {
							if ("事假".equals(config.getDisplayName()) || "病假".equals(config.getDisplayName())
									|| "其他".equals(config.getDisplayName())) {
								Map map = new HashMap();
								map.put("id", config.getDisplayCode());
								map.put("name", config.getDisplayName());
								result.add(map);
							} else if ("调休".equals(config.getDisplayName())) {
								List<Integer> typeList = new ArrayList<Integer>();
								typeList.add(ConfigConstants.LEAVE_TYPE_5);
								typeList.add(ConfigConstants.LEAVE_TYPE_13);
								if (getAllowRemainDays(user, typeList,year)) {
									Map map = new HashMap();
									map.put("id", ConfigConstants.LEAVE_TYPE_5);
									map.put("name", "调休");
									result.add(map);
								}
							}
						}
					} else {
						/*
						 * EmpFamilyMember empFamilyMember=new
						 * EmpFamilyMember();
						 * empFamilyMember.setEmployeeId(user.getEmployeeId());
						 * //List<EmpFamilyMember> familyBabylist =
						 * empFamilyMemberService.getListByCondition(
						 * empFamilyMember);
						 */
						for (Config config : leaveTypeList) {
							Map map = new HashMap();
							if ("年假".equals(config.getDisplayName())) {
								List<Integer> typeList = new ArrayList<Integer>();
								typeList.add(ConfigConstants.LEAVE_TYPE_1);
								if (getAllowRemainDays(user, typeList,year)) {
									map.put("id", config.getDisplayCode());
									map.put("name", config.getDisplayName());
									result.add(map);
								}
							} else if ("调休".equals(config.getDisplayName())) {
								List<Integer> typeList = new ArrayList<Integer>();
								typeList.add(ConfigConstants.LEAVE_TYPE_5);
								typeList.add(ConfigConstants.LEAVE_TYPE_13);
								if (getAllowRemainDays(user, typeList,year)) {
									map.put("id", ConfigConstants.LEAVE_TYPE_5);
									map.put("name", "调休");
									result.add(map);
								}
							}
							if (employee.getSex().intValue() == 0) {
								if (!"年假".equals(config.getDisplayName()) && !"调休".equals(config.getDisplayName())
										&& !"产前假".equals(config.getDisplayName())
										&& !"产假".equals(config.getDisplayName())
										&& !"哺乳假".equals(config.getDisplayName())
										&& !"流产假".equals(config.getDisplayName())) {
									map.put("id", config.getDisplayCode());
									map.put("name", config.getDisplayName());
									result.add(map);
								}
							} else if (employee.getSex().intValue() == 1) {
								if (!"年假".equals(config.getDisplayName()) && !"调休".equals(config.getDisplayName())
										&& !"陪产假".equals(config.getDisplayName())) {
									map.put("id", config.getDisplayCode());
									map.put("name", config.getDisplayName());
									result.add(map);
								}
							}
						}
					}
				} else {
					for (Config config : leaveTypeList) {
						if ("年假".equals(config.getDisplayName()) || "事假".equals(config.getDisplayName())
								|| "病假".equals(config.getDisplayName()) || "其他".equals(config.getDisplayName())) {
							Map map = new HashMap();
							map.put("id", config.getDisplayCode());
							map.put("name", config.getDisplayName());
							result.add(map);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(user.getEmployee().getCnName() + "请假-获取假期类型：" + e.getMessage());
		}
		return result;
	}

	public boolean getAllowRemainDays(User user, List<Integer> typeList,Integer year) {
		double allowRemainDays = 0;
		List<EmpLeave> currentMIsEx = empLeaveMapper.getAllowRemainDays(user.getEmployee().getId(),typeList,year);
		if (currentMIsEx != null && currentMIsEx.size() > 0) {
			for (EmpLeave empLeave : currentMIsEx) {
				allowRemainDays = allowRemainDays + empLeave.getAllowRemainDays();
			}
		}
		if (allowRemainDays > 0) {
			return true;
		}
		return false;
	}

	// 保存
	@ResponseBody
	@RequestMapping("/save.htm")
	@Token(remove = true)
	public Map<String, Object> save(HttpServletRequest request) {
		User user = userService.getCurrentUser();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = empApplicationLeaveService.save(request, user);
		} catch (Exception e) {
			logger.error(user.getEmployee().getCnName() + "请假-提交：" + e.toString());
			String uuId = UUIDGenerator.generate();
            try {
				RedisUtils.setString(String.valueOf(user.getEmployee().getId()),uuId,1800000);
				map.put("token", uuId);
			} catch (Exception e1) {
				logger.error("请假申请："+e1.toString());
			}
			map.put("repeatFlag", false);
			map.put("message", e.getMessage());
			map.put("success", false);
			return map;
		}
		return map;
	}

	// 保存销假
	@ResponseBody
	@RequestMapping("/saveAbolishLeave.htm")
	@Token(remove = true)
	public Map<String, Object> saveAbolishLeave(HttpServletRequest request) {
		User user = userService.getCurrentUser();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = empApplicationLeaveService.saveAbolishLeave(request, user);
		} catch (Exception e) {
			logger.error(user.getEmployee().getCnName() + "销假-提交：" + e.toString());
			map.put("repeatFlag", false);
			map.put("message", e.getMessage());
			map.put("success", false);
			return map;
		}
		return map;
	}

	// 请假申请审批页
	@RequestMapping("/approval.htm")
	@Token(generate = true) // 生成token
	public String approval(HttpServletRequest request, Long leaveId, String flag, String urlType) {
		User user = userService.getCurrentUser();
		try {
			request.setAttribute("imgUrl",
					(user.getEmployee().getPicture() != null && !"".equals(user.getEmployee().getPicture()))
							? user.getEmployee().getPicture() : ConfigConstants.HEAD_PICTURE_URL);
			EmpApplicationLeave leave = empApplicationLeaveService.getById(leaveId);
			request.setAttribute("leave", leave);
			List<EmpApplicationLeaveDetail> detailList = empApplicationLeaveService.getLeaveDetailList(leaveId);
			request.setAttribute("detailList", detailList);
			request.setAttribute("isSelf", false);
			request.setAttribute("canApprove", false);
			// 审批显示
			if (leave.getApprovalStatus().intValue() == 100 || leave.getApprovalStatus().intValue() == 200) {
				if (leave.getEmployeeId().longValue() == user.getEmployeeId().longValue()) {
					request.setAttribute("isSelf", true);
				}
				// 办理中,taskService查询任务
				Task task = activitiServiceImpl.queryTaskByProcessInstanceId(leave.getProcessInstanceId());
				request.setAttribute("actName", task != null ? task.getName() : "");
				if (StringUtils.equalsIgnoreCase("can", flag)) {
					request.setAttribute("canApprove", true);
				}
			}
			request.setAttribute("returnUrl", ReturnUrlUtil.getReturnUrl(urlType));
			// 假期详情
			Map<String, Object> result = empApplicationLeaveService.getLeaveCountInfoByEmpId(leave.getEmployeeId());
			// 允许年假
			request.setAttribute("allowAnnualLeave", result.get("allowAnnualLeave"));
			// 已用年假
			request.setAttribute("usedAnnualLeave", result.get("usedAnnualLeave"));
			// 去年已用年假
			request.setAttribute("lastUsedAnnualLeave", result.get("lastUsedAnnualLeave"));
			// 允许病假
			request.setAttribute("allowSickLeave", result.get("allowSickLeave"));
			// 已用病假
			request.setAttribute("usedSickLeave", result.get("usedSickLeave"));
			// 允许调休
			request.setAttribute("allowDayOff", result.get("allowDayOff"));
			// 已用调休
			request.setAttribute("usedDayOff", result.get("usedDayOff"));
			// 已用事假
			request.setAttribute("affairsLeave", result.get("affairsLeave"));
		} catch (Exception e) {
			logger.error(user.getEmployee().getCnName() + "请假-审批首页：" + e.toString());
			return "base/empApplicationLeave/approve";
		}
		return "base/empApplicationLeave/approve";
	}

	// 请假审批流程页面
	@RequestMapping("/process.htm")
	public String process(HttpServletRequest request, Long leaveId, Long ruProcdefId) {
		EmpApplicationLeave leave = empApplicationLeaveService.getById(leaveId);
		request.setAttribute("leave", leave);
		RunTask param = new RunTask();
		param.setReProcdefCode(RunTask.RUN_CODE_60);
		param.setEntityId(String.valueOf(leaveId));
		RunTask runTask = runTaskService.getRunTask(param);
		request.setAttribute("runTask", runTask);
		List<HiActinst> hiActinstList = hiActinstService.getListByRunId(ruProcdefId);
		// 组装职位信息
		List<Long> empIdList = new ArrayList<Long>();
		List<Employee> list = new ArrayList<Employee>();
		for (HiActinst hiActinst : hiActinstList) {
			if (hiActinst.getAssigneeId() != null) {
				empIdList.add(hiActinst.getAssigneeId());
			}
		}
		if (empIdList != null && empIdList.size() > 0) {
			list = employeeService.getByEmpIdList(empIdList);
		}
		for (HiActinst hiActinst : hiActinstList) {
			for (Employee employee : list) {
				if (employee.getId().equals(hiActinst.getAssigneeId())) {
					hiActinst.setPositionName(employee.getPositionName());
				}
			}
		}
		request.setAttribute("hiActinstList", hiActinstList);
		return "base/empApplicationLeave/process";
	}

	public void sendErrorMsgByType(String reason) {
		List<Config> configs = configService.getListByCode(ConfigConstants.LEAVE_APPLY_WARN_PHONE);
		for (Config conf : configs) {
			try {
				logger.info("发送异常信息给用户{}", conf.getDisplayCode());
				smsService.sendMessage(conf.getDisplayCode(), "[MO办公自动化-" + ENV + "]PROBLEM:" + reason
						+ "出现问题，请尽快处理[短信生成时间=" + DateUtils.format(new Date(), DateUtils.FORMAT_DD_HH_MM) + "]！！！");
			} catch (Exception e) {
				logger.error("给{}用户发送MO预警信息失败，失败原因:{}", conf.getDisplayCode(), e.getMessage());
			}
		}
	}

	@ResponseBody
	@RequestMapping("/getLeaveDays.htm")
	public Map<String, Object> getLeaveDays(Long empId, String startTime1, String startTime2, String endTime1,
			String endTime2, String leaveType) {
		return empApplicationLeaveService.calculatedLeaveDays(empId, startTime1, startTime2, endTime1, endTime2, leaveType);
	}

	// 计算请假天数
	@ResponseBody
	@RequestMapping("/calculatedLeaveDays.htm")
	public Map<String, Object> calculatedLeaveDays(HttpServletRequest request) {
		User user = userService.getCurrentUser();

		// 开始时间-天
		String startTime1 = request.getParameter("startTime1");
		// 开始时间-时分
		String startTime2 = request.getParameter("startTime2");
		// 结束时间-天
		String endTime1 = request.getParameter("endTime1");
		// 结束时间-时分
		String endTime2 = request.getParameter("endTime2");
		// 假期类型
		String leaveType = request.getParameter("leaveType");

		return empApplicationLeaveService.calculatedLeaveDays(user.getEmployeeId(), startTime1, startTime2, endTime1, endTime2, leaveType);
	}

	/**
	 * 根据员工ID和年月和假期类型重跑假期报表数据
	 * 
	 * @param employeeId
	 * @param month
	 * @param type
	 */
	@RequestMapping("/generateLeavelReports.htm")
	public void generateLeavelReports(
			Long employeeId/* , String month, Integer type */) {
		// 根据月份将报表中数据删除,月份为空则删除所有该员工的数据,月份不为空则直接删除该月份数据,在生成报表数据的时候需要判断假期明细中的时间是否在传入月份之内
		try {
			if (null == employeeId) {
				logger.warn("根据员工ID和年月和假期类型重跑假期报表数据失败! EmployeeId为空!");
				return;
			}
			// 删除假期明细报表中的数据
			EmpLeaveReport elrModel = new EmpLeaveReport();
			/*
			 * elrModel.setEmployeeId(employeeId);
			 * if(StringUtils.isNotBlank(month)){ elrModel.setMonth(month); }
			 * if(null != type){ elrModel.setType(type); }
			 */
			empLeaveReportService.deleteByCondition(elrModel);

			// 获取当前假期
			EmpApplicationLeave model = new EmpApplicationLeave();
			model.setEmployeeId(employeeId);
			model.setApprovalStatus(EmpApplicationLeave.APPROVAL_STATUS_YES);
			List<EmpApplicationLeave> ealList = empApplicationLeaveService.getListByCondition(model);
			if (null == ealList || ealList.isEmpty()) {
				logger.info("根据员工ID和年月和假期类型重跑假期报表数据失败! EmployeeId[{}]假期信息为空!", employeeId);
				return;
			} else {
				List<EmpApplicationLeaveDetail> leaveDetailList = null;
				// List<Date> months = null;
				List<EmpLeaveReport> empLeaveReportList = new ArrayList<EmpLeaveReport>();
				// 循环假期列表,获取当前假期所属月份
				for (EmpApplicationLeave eal : ealList) {
					// 根据假期id获取假期明细数据
					leaveDetailList = empApplicationLeaveService.getLeaveDetailList(eal.getId());
					if (null != leaveDetailList && !leaveDetailList.isEmpty()) {
						for (EmpApplicationLeaveDetail eald : leaveDetailList) {
							if (null != eald) {
								/*
								 * //匹配月份 months =
								 * DateUtils.findMonths(eald.getStartTime(),
								 * eald.getEndTime());
								 * if(months.contains(month)){
								 * 
								 * }
								 */
								empApplicationLeaveService.assembleReport(eal.getEmployeeId(), eal.getDepartId(),
										eal.getCnName(), eald, empLeaveReportList);
							}
						}

					}
				}
			}
		} catch (Exception e) {
			logger.error("根据EmployeeIdID[" + employeeId + "]统计假期报表数据失败!", e);
		}
	}

	/**
	 * 统计所有假期报表(调用的时候回删除假期明细报表中的所有数据)
	 */
	@RequestMapping("/generateOldDataToLeavelReports.htm")
	public void generateOldDataToLeavelReports() {
		logger.info("OA初始化请假报表数据开始：generateOldDataToLeavelReports");
		empLeaveReportService.deleteAll();
		EmpApplicationLeave model = new EmpApplicationLeave();
		model.setApprovalStatus(EmpApplicationLeave.APPROVAL_STATUS_YES);
		;
		List<EmpApplicationLeave> ealList = empApplicationLeaveService.getListByCondition(model);
		if (null != ealList && !ealList.isEmpty()) {
			List<EmpApplicationLeaveDetail> leaveDetailList = null;
			for (EmpApplicationLeave eal : ealList) {
				leaveDetailList = empApplicationLeaveService.getLeaveDetailList(eal.getId());
				if (null != leaveDetailList && !leaveDetailList.isEmpty()) {
					List<EmpLeaveReport> empLeaveReportList = new ArrayList<EmpLeaveReport>();
					for (EmpApplicationLeaveDetail eald : leaveDetailList) {
						if (null != eald) {
							try {
								empApplicationLeaveService.assembleReport(eal.getEmployeeId(), eal.getDepartId(),
										eal.getCnName(), eald, empLeaveReportList);
							} catch (Exception e) {
							}
						}
					}
				}
				logger.info("根据假期ID[{}]统计假期报表数据成功!", eal.getId());
			}
		}
	}

	@RequestMapping("/empLeaveInfoByID.htm")
	@ResponseBody
	public Map<String, Object> getEmpLeaveCountInfoByEmpId(Long employeeId) {
		Map<String, Object> result = new HashMap<>();
		try {

			result = this.empApplicationLeaveService.getLeaveCountInfoByEmpId(employeeId);
			result.put("success", true);
		} catch (Exception e) {
			result.put("message", "查询假期数据综合信息失败！");
			result.put("success", false);
			logger.error("根据EmployeeId[" + employeeId + "]查询该员工假期数据综合信息失败!", e);
		}
		return result;
	}

	// 获取销假开始小时
	@ResponseBody
	@RequestMapping("/getLeaveBackHours.htm")
	public Map<String, Object> getLeaveBackHours(HttpServletRequest request, String date, String leaveType,
			String leaveId) {

		Map<String, Object> map = new HashMap<String, Object>();
		User user = userService.getCurrentUser();
		try {
			// 查询假期申请单信息,获取假期开始时间
			String startTime = ""; // 申请单开始时间
			String startHour = "";
			boolean isFirstDay = true; // 销假日期是否是第一天
			List<EmpApplicationLeaveDetail> empApplicationLeaveDetailList = empApplicationLeaveDetailService
					.getList(Long.parseLong(leaveId));
			if (empApplicationLeaveDetailList != null && empApplicationLeaveDetailList.size() > 0
					&& empApplicationLeaveDetailList.get(0).getStartTime() != null) {
				startTime = DateUtils.format(empApplicationLeaveDetailList.get(0).getStartTime(),
						DateUtils.FORMAT_SHORT);
				startHour = DateUtils.format(empApplicationLeaveDetailList.get(0).getStartTime(), DateUtils.FORMAT_HH_MM);
			}
			// 判断销假日期是否是假期第一天
			if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(date)) {
				isFirstDay = startTime.equals(date);
			}
			EmployeeClass employeeClass = new EmployeeClass();
			employeeClass.setEmployId(user.getEmployeeId());
			employeeClass.setClassDate(DateUtils.parse(date, DateUtils.FORMAT_SHORT));
			EmployeeClass empClass = employeeClassService.getEmployeeClassSetting(employeeClass);
			String hoursList = "";
			if (empClass != null) {
				map.put("code", "0000");
				hoursList = isFirstDay ? startHour : DateUtils.format(empClass.getStartTime(), "HH:mm");
				map.put("list", hoursList);
			} else {
				employeeClass.setClassDate(DateUtils.addDay(DateUtils.parse(date, DateUtils.FORMAT_SHORT), -1));
				empClass = employeeClassService.getEmployeeClassSetting(employeeClass);
				if (empClass != null && empClass.getIsInterDay() == 1) {
					map.put("code", "0000");
					hoursList = isFirstDay ? startHour : DateUtils.format(empClass.getStartTime(), "HH:mm");
					map.put("list", hoursList);
				} else {
					map.put("code", "1111");
					map.put("message", "今天不需要请假！");
				}
			}
		} catch (Exception e) {
			map.put("code", "9999");
			map.put("message", "系统异常！");
			logger.error(user.getEmployee().getCnName() + "获取请假开始结束小时数：" + e.toString());
			return map;
		}
		return map;
	}
}
