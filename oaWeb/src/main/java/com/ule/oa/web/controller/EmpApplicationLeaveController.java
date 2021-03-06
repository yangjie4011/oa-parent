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
 * @ClassName: ????????????
 * @Description: ????????????
 * @author yangjie
 * @date 2017???6???14???
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

	// ??????????????????
	@RequestMapping("/index.htm")
	@Token(generate = true) // ??????token
	public String index(HttpServletRequest request, String urlType) {
		User user = userService.getCurrentUser();
		try {
			logger.info("???????????????" + user.getEmployeeId());
			request.setAttribute("returnUrl", ReturnUrlUtil.getReturnUrl(urlType));
			// ????????????id
			request.setAttribute("employeeId", user.getEmployeeId());
		} catch (Exception e) {
			logger.error(user.getEmployee().getCnName() + "??????-?????????" + e.toString());
			return "base/empApplicationLeave/index";
		}
		return "base/empApplicationLeave/index";
	}

	// ??????????????????
	@RequestMapping("/leaveRule.htm")
	public String leaveRule(HttpServletRequest request) {
		return "base/empApplicationLeave/leaveRule";
	}

	// ???????????????????????????
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
			logger.error(user.getEmployee().getCnName() + "??????-????????????????????????" + e.toString());
			map.put("birthDays", DateUtils.format(birthDate, DateUtils.FORMAT_SHORT));
		}
		return map;
	}

	// ???????????????????????? ?????? ???????????? ???????????????
	@ResponseBody
	@RequestMapping("/getLeaveCountStartEndTime.htm")
	public Map<String, Object> getLeaveCountStartEndTime(HttpServletRequest request) {
		Map<String, Object> map = empApplicationLeaveService.getExtraMaternityLeaveDays(request);
		return map;
	}

	// ???????????? ??? type ??????????????????
	@ResponseBody
	@RequestMapping("/countLeave10.htm")
	public Map<String, Object> countLeave10(HttpServletRequest request, String type, String date, String leaveType) {
		Map<String, Object> map = new HashMap<String, Object>();
		User user = userService.getCurrentUser();
		try {
			// ?????? ???????????? ????????? ?????????????????? ?????????????????? type ?????? ?????????????????????
			if ("10".equals(leaveType)) {
				boolean endTimeFlag = true;
				int countEnd = 0;// ?????? ????????????????????????
				int i = 0;// ????????????
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

				} else { // ???????????? type ?????????????????? ?????????
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
			map.put("message", "???????????????");
			logger.error(user.getEmployee().getCnName() + "???????????????????????????" + e.toString());
			return map;
		}
		return map;
	}

	// ???????????????????????????????????????,???????????????????????????????????? //???????????? ??? type ??????????????????
	@ResponseBody
	@RequestMapping("/getLeaveHours.htm")
	public Map<String, Object> getLeaveHours(HttpServletRequest request, String type, String date, String leaveType) {
		Map<String, Object> map = new HashMap<String, Object>();
		User user = userService.getCurrentUser();
		try {
			map = empApplicationLeaveService.getLeaveHours(user.getEmployeeId(), type, date, leaveType);
		} catch (Exception e) {
			map.put("code", "9999");
			map.put("message", "???????????????");
			logger.error(user.getEmployee().getCnName() + "????????????????????????????????????", e);
			return map;
		}
		return map;
	}

	// ????????????
	@SuppressWarnings("rawtypes")
	@ResponseBody
	@RequestMapping("/getChildren.htm")
	public Map getChildren(HttpServletRequest request) {
		User user = userService.getCurrentUser();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = empApplicationLeaveService.getChildren(user.getEmployee().getId());
		} catch (Exception e) {
			logger.error(user.getEmployee().getCnName() + "??????-??????????????????" + e.toString());
		}
		return map;
	}

	// ??????????????????
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ResponseBody
	@RequestMapping("/getLeaveTypes.htm")
	public List<Map> getLeaveTypes(EmpApplicationLeave leave) {
		User user = userService.getCurrentUser();
		Integer year = Integer.valueOf(DateUtils.getYear(new Date()))-1;
		List<Map> result = new ArrayList<Map>();
		try {
			// ???????????????????????????????????????
			if (user != null && user.getEmployeeId() != null) {
				Config sysConfig = new Config();
				sysConfig.setCode("leaveType");
				List<Config> leaveTypeList = configService.getListByCondition(sysConfig);
				Employee employee = employeeService.getById(user.getEmployeeId());
				if (employee != null && employee.getFirstEntryTime() != null) {
					// ???????????????3??????
					// ???????????????3?????? ??????????????????5???23????????? 8???23?????????
					Date flagStaus = DateUtils.addMonth(employee.getFirstEntryTime(), 3);
					// 2-date??????d2
					if (2 != DateUtils.compareDate(flagStaus, new Date())) {
						for (Config config : leaveTypeList) {
							if ("??????".equals(config.getDisplayName()) || "??????".equals(config.getDisplayName())
									|| "??????".equals(config.getDisplayName())) {
								Map map = new HashMap();
								map.put("id", config.getDisplayCode());
								map.put("name", config.getDisplayName());
								result.add(map);
							} else if ("??????".equals(config.getDisplayName())) {
								List<Integer> typeList = new ArrayList<Integer>();
								typeList.add(ConfigConstants.LEAVE_TYPE_5);
								typeList.add(ConfigConstants.LEAVE_TYPE_13);
								if (getAllowRemainDays(user, typeList,year)) {
									Map map = new HashMap();
									map.put("id", ConfigConstants.LEAVE_TYPE_5);
									map.put("name", "??????");
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
							if ("??????".equals(config.getDisplayName())) {
								List<Integer> typeList = new ArrayList<Integer>();
								typeList.add(ConfigConstants.LEAVE_TYPE_1);
								if (getAllowRemainDays(user, typeList,year)) {
									map.put("id", config.getDisplayCode());
									map.put("name", config.getDisplayName());
									result.add(map);
								}
							} else if ("??????".equals(config.getDisplayName())) {
								List<Integer> typeList = new ArrayList<Integer>();
								typeList.add(ConfigConstants.LEAVE_TYPE_5);
								typeList.add(ConfigConstants.LEAVE_TYPE_13);
								if (getAllowRemainDays(user, typeList,year)) {
									map.put("id", ConfigConstants.LEAVE_TYPE_5);
									map.put("name", "??????");
									result.add(map);
								}
							}
							if (employee.getSex().intValue() == 0) {
								if (!"??????".equals(config.getDisplayName()) && !"??????".equals(config.getDisplayName())
										&& !"?????????".equals(config.getDisplayName())
										&& !"??????".equals(config.getDisplayName())
										&& !"?????????".equals(config.getDisplayName())
										&& !"?????????".equals(config.getDisplayName())) {
									map.put("id", config.getDisplayCode());
									map.put("name", config.getDisplayName());
									result.add(map);
								}
							} else if (employee.getSex().intValue() == 1) {
								if (!"??????".equals(config.getDisplayName()) && !"??????".equals(config.getDisplayName())
										&& !"?????????".equals(config.getDisplayName())) {
									map.put("id", config.getDisplayCode());
									map.put("name", config.getDisplayName());
									result.add(map);
								}
							}
						}
					}
				} else {
					for (Config config : leaveTypeList) {
						if ("??????".equals(config.getDisplayName()) || "??????".equals(config.getDisplayName())
								|| "??????".equals(config.getDisplayName()) || "??????".equals(config.getDisplayName())) {
							Map map = new HashMap();
							map.put("id", config.getDisplayCode());
							map.put("name", config.getDisplayName());
							result.add(map);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(user.getEmployee().getCnName() + "??????-?????????????????????" + e.getMessage());
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

	// ??????
	@ResponseBody
	@RequestMapping("/save.htm")
	@Token(remove = true)
	public Map<String, Object> save(HttpServletRequest request) {
		User user = userService.getCurrentUser();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = empApplicationLeaveService.save(request, user);
		} catch (Exception e) {
			logger.error(user.getEmployee().getCnName() + "??????-?????????" + e.toString());
			String uuId = UUIDGenerator.generate();
            try {
				RedisUtils.setString(String.valueOf(user.getEmployee().getId()),uuId,1800000);
				map.put("token", uuId);
			} catch (Exception e1) {
				logger.error("???????????????"+e1.toString());
			}
			map.put("repeatFlag", false);
			map.put("message", e.getMessage());
			map.put("success", false);
			return map;
		}
		return map;
	}

	// ????????????
	@ResponseBody
	@RequestMapping("/saveAbolishLeave.htm")
	@Token(remove = true)
	public Map<String, Object> saveAbolishLeave(HttpServletRequest request) {
		User user = userService.getCurrentUser();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = empApplicationLeaveService.saveAbolishLeave(request, user);
		} catch (Exception e) {
			logger.error(user.getEmployee().getCnName() + "??????-?????????" + e.toString());
			map.put("repeatFlag", false);
			map.put("message", e.getMessage());
			map.put("success", false);
			return map;
		}
		return map;
	}

	// ?????????????????????
	@RequestMapping("/approval.htm")
	@Token(generate = true) // ??????token
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
			// ????????????
			if (leave.getApprovalStatus().intValue() == 100 || leave.getApprovalStatus().intValue() == 200) {
				if (leave.getEmployeeId().longValue() == user.getEmployeeId().longValue()) {
					request.setAttribute("isSelf", true);
				}
				// ?????????,taskService????????????
				Task task = activitiServiceImpl.queryTaskByProcessInstanceId(leave.getProcessInstanceId());
				request.setAttribute("actName", task != null ? task.getName() : "");
				if (StringUtils.equalsIgnoreCase("can", flag)) {
					request.setAttribute("canApprove", true);
				}
			}
			request.setAttribute("returnUrl", ReturnUrlUtil.getReturnUrl(urlType));
			// ????????????
			Map<String, Object> result = empApplicationLeaveService.getLeaveCountInfoByEmpId(leave.getEmployeeId());
			// ????????????
			request.setAttribute("allowAnnualLeave", result.get("allowAnnualLeave"));
			// ????????????
			request.setAttribute("usedAnnualLeave", result.get("usedAnnualLeave"));
			// ??????????????????
			request.setAttribute("lastUsedAnnualLeave", result.get("lastUsedAnnualLeave"));
			// ????????????
			request.setAttribute("allowSickLeave", result.get("allowSickLeave"));
			// ????????????
			request.setAttribute("usedSickLeave", result.get("usedSickLeave"));
			// ????????????
			request.setAttribute("allowDayOff", result.get("allowDayOff"));
			// ????????????
			request.setAttribute("usedDayOff", result.get("usedDayOff"));
			// ????????????
			request.setAttribute("affairsLeave", result.get("affairsLeave"));
		} catch (Exception e) {
			logger.error(user.getEmployee().getCnName() + "??????-???????????????" + e.toString());
			return "base/empApplicationLeave/approve";
		}
		return "base/empApplicationLeave/approve";
	}

	// ????????????????????????
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
		// ??????????????????
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
				logger.info("???????????????????????????{}", conf.getDisplayCode());
				smsService.sendMessage(conf.getDisplayCode(), "[MO???????????????-" + ENV + "]PROBLEM:" + reason
						+ "??????????????????????????????[??????????????????=" + DateUtils.format(new Date(), DateUtils.FORMAT_DD_HH_MM) + "]?????????");
			} catch (Exception e) {
				logger.error("???{}????????????MO?????????????????????????????????:{}", conf.getDisplayCode(), e.getMessage());
			}
		}
	}

	@ResponseBody
	@RequestMapping("/getLeaveDays.htm")
	public Map<String, Object> getLeaveDays(Long empId, String startTime1, String startTime2, String endTime1,
			String endTime2, String leaveType) {
		return empApplicationLeaveService.calculatedLeaveDays(empId, startTime1, startTime2, endTime1, endTime2, leaveType);
	}

	// ??????????????????
	@ResponseBody
	@RequestMapping("/calculatedLeaveDays.htm")
	public Map<String, Object> calculatedLeaveDays(HttpServletRequest request) {
		User user = userService.getCurrentUser();

		// ????????????-???
		String startTime1 = request.getParameter("startTime1");
		// ????????????-??????
		String startTime2 = request.getParameter("startTime2");
		// ????????????-???
		String endTime1 = request.getParameter("endTime1");
		// ????????????-??????
		String endTime2 = request.getParameter("endTime2");
		// ????????????
		String leaveType = request.getParameter("leaveType");

		return empApplicationLeaveService.calculatedLeaveDays(user.getEmployeeId(), startTime1, startTime2, endTime1, endTime2, leaveType);
	}

	/**
	 * ????????????ID????????????????????????????????????????????????
	 * 
	 * @param employeeId
	 * @param month
	 * @param type
	 */
	@RequestMapping("/generateLeavelReports.htm")
	public void generateLeavelReports(
			Long employeeId/* , String month, Integer type */) {
		// ????????????????????????????????????,?????????????????????????????????????????????,?????????????????????????????????????????????,?????????????????????????????????????????????????????????????????????????????????????????????
		try {
			if (null == employeeId) {
				logger.warn("????????????ID??????????????????????????????????????????????????????! EmployeeId??????!");
				return;
			}
			// ????????????????????????????????????
			EmpLeaveReport elrModel = new EmpLeaveReport();
			/*
			 * elrModel.setEmployeeId(employeeId);
			 * if(StringUtils.isNotBlank(month)){ elrModel.setMonth(month); }
			 * if(null != type){ elrModel.setType(type); }
			 */
			empLeaveReportService.deleteByCondition(elrModel);

			// ??????????????????
			EmpApplicationLeave model = new EmpApplicationLeave();
			model.setEmployeeId(employeeId);
			model.setApprovalStatus(EmpApplicationLeave.APPROVAL_STATUS_YES);
			List<EmpApplicationLeave> ealList = empApplicationLeaveService.getListByCondition(model);
			if (null == ealList || ealList.isEmpty()) {
				logger.info("????????????ID??????????????????????????????????????????????????????! EmployeeId[{}]??????????????????!", employeeId);
				return;
			} else {
				List<EmpApplicationLeaveDetail> leaveDetailList = null;
				// List<Date> months = null;
				List<EmpLeaveReport> empLeaveReportList = new ArrayList<EmpLeaveReport>();
				// ??????????????????,??????????????????????????????
				for (EmpApplicationLeave eal : ealList) {
					// ????????????id????????????????????????
					leaveDetailList = empApplicationLeaveService.getLeaveDetailList(eal.getId());
					if (null != leaveDetailList && !leaveDetailList.isEmpty()) {
						for (EmpApplicationLeaveDetail eald : leaveDetailList) {
							if (null != eald) {
								/*
								 * //???????????? months =
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
			logger.error("??????EmployeeIdID[" + employeeId + "]??????????????????????????????!", e);
		}
	}

	/**
	 * ????????????????????????(????????????????????????????????????????????????????????????)
	 */
	@RequestMapping("/generateOldDataToLeavelReports.htm")
	public void generateOldDataToLeavelReports() {
		logger.info("OA????????????????????????????????????generateOldDataToLeavelReports");
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
				logger.info("????????????ID[{}]??????????????????????????????!", eal.getId());
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
			result.put("message", "???????????????????????????????????????");
			result.put("success", false);
			logger.error("??????EmployeeId[" + employeeId + "]?????????????????????????????????????????????!", e);
		}
		return result;
	}

	// ????????????????????????
	@ResponseBody
	@RequestMapping("/getLeaveBackHours.htm")
	public Map<String, Object> getLeaveBackHours(HttpServletRequest request, String date, String leaveType,
			String leaveId) {

		Map<String, Object> map = new HashMap<String, Object>();
		User user = userService.getCurrentUser();
		try {
			// ???????????????????????????,????????????????????????
			String startTime = ""; // ?????????????????????
			String startHour = "";
			boolean isFirstDay = true; // ??????????????????????????????
			List<EmpApplicationLeaveDetail> empApplicationLeaveDetailList = empApplicationLeaveDetailService
					.getList(Long.parseLong(leaveId));
			if (empApplicationLeaveDetailList != null && empApplicationLeaveDetailList.size() > 0
					&& empApplicationLeaveDetailList.get(0).getStartTime() != null) {
				startTime = DateUtils.format(empApplicationLeaveDetailList.get(0).getStartTime(),
						DateUtils.FORMAT_SHORT);
				startHour = DateUtils.format(empApplicationLeaveDetailList.get(0).getStartTime(), DateUtils.FORMAT_HH_MM);
			}
			// ??????????????????????????????????????????
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
					map.put("message", "????????????????????????");
				}
			}
		} catch (Exception e) {
			map.put("code", "9999");
			map.put("message", "???????????????");
			logger.error(user.getEmployee().getCnName() + "????????????????????????????????????" + e.toString());
			return map;
		}
		return map;
	}
}
