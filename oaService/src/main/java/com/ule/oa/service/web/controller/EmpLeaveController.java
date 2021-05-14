package com.ule.oa.service.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ule.oa.base.po.CompanyConfig;
import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.EmpLeave;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.service.CompanyConfigService;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.EmpLeaveService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.LeaveRecordService;
import com.ule.oa.common.cache.distlock.DistLockUtil;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.JSON;
import com.ule.oa.common.utils.JsonWriter;
import com.ule.oa.common.utils.json.JSONUtils;

/**
 * @ClassName: EmpLeaveController
 * @Description: 员工假期控制层
 * @author minsheng
 * @date 2017年6月13日 下午3:42:56
 */
@Controller
@RequestMapping("empLeave")
public class EmpLeaveController {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private EmpLeaveService empLeaveService;
	@Autowired
	private LeaveRecordService leaveRecordService;
	@Autowired
	private EmployeeService employeeService;
	@Resource
	private CompanyConfigService companyConfigService;
	@Resource
	private ConfigService configService;

	/**
	 * updateRemainLeave(对外接口-索引假期占用添加并扣减剩余假期信息) @Title:
	 * updateRemainLeave @Description: 对外接口-索引假期占用添加并扣减剩余假期信息 @return 设定文件 JSON
	 * 返回类型 @throws
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/updateEmpLeaveApply.htm", method = RequestMethod.POST)
	@ResponseBody
	public JSON updateEmpLeaveApply(HttpServletRequest request) {
		try {
			String json = request.getParameter("data");
			logger.info(json);

			empLeaveService.updateEmpLeaveApply(JSONUtils.read(json, List.class));

			return JsonWriter.successfulJson();
		} catch (Exception e) {
			logger.error(e.getMessage());

			return JsonWriter.failedJson(false, e.getMessage());
		}
	}

	/**
	 * updateLeaveApply(对外接口-假期申请审批后调的接口) @Title: updateLeaveApply @Description:
	 * 对外接口-假期申请审批后调的接口 @param request @return 设定文件 JSON 返回类型 @throws
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/updateEmpLeaveAudit.htm", method = RequestMethod.POST)
	@ResponseBody
	public JSON updateEmpLeaveAudit(HttpServletRequest request) {
		try {
			String json = request.getParameter("data");
			logger.info(json);

			empLeaveService.updateEmpLeaveAudit(JSONUtils.read(json, List.class));

			return JsonWriter.successfulJson();
		} catch (Exception e) {
			return JsonWriter.failedJson(false, e.getMessage());
		}
	}

	/**
	 * calculationOurAge(自动计算司龄-计算完司龄以后，会将对应的年假和病假也一起计算出来) @Title:
	 * calculationOurAge @Description: 自动计算司龄-计算完司龄以后，会将对应的年假和病假也一起计算出来 void
	 * 返回类型 @throws
	 */
	@ResponseBody
	@RequestMapping("/calculationOurAge.htm")
	public Map<String, String> calculationOurAge() {
		new Thread(new Runnable() {
			public void run() {
				empLeaveService.calculationOurAge();
			}
		}).start();
		Map<String, String> map = new HashMap<String, String>();
		map.put("response", "calculationOurAge触发成功,请稍后查看数据！");
		return map;
	}

	/**
	 * initNextYearLeave(初始化下一年的年假) @Title: initNextYearLeave @Description:
	 * 初始化下一年的年假 void 返回类型 @throws
	 */
	@ResponseBody
	@RequestMapping("/initNextYearLeave.htm")
	public Map<String, String> initNextYearLeave() {
		String lockValue = DistLockUtil.lock("initNextYearLeave", 60 * 5L);
		Map<String, String> map = new HashMap<String, String>();
		if (StringUtils.isBlank(lockValue)) {
			logger.info("初始化下一年的年假定时已经启动，请不要重复调用当前定时!!!");
			map.put("response", "请勿重复调用当前定时!!!");
			return map;
		} else {// 锁定定时
			new Thread(new Runnable() {
				public void run() {
					empLeaveService.initNextYearLeave();
				}
			}).start();
			map.put("response", "initNextYearLeave触发成功,请稍后查看数据！");
			return map;
		}
	}
	
	@ResponseBody
	@RequestMapping("/initNextYearLeaveByYear.htm")
	public Map<String, String> initNextYearLeaveByYear(String year,Integer betDays) {
		String lockValue = DistLockUtil.lock("initNextYearLeaveByYear", 60 * 5L);
		Map<String, String> map = new HashMap<String, String>();
		if (StringUtils.isBlank(lockValue)) {
			logger.info("初始化下一年的年假定时已经启动，请不要重复调用当前定时!!!");
			map.put("response", "请勿重复调用当前定时!!!");
			return map;
		} else {// 锁定定时
			new Thread(new Runnable() {
				public void run() {
					empLeaveService.initNextYearLeaveByYear(year,betDays);
				}
			}).start();
			map.put("response", "initNextYearLeaveByYear触发成功,请稍后查看数据！");
			return map;
		}
	}
	

	/**
	 * initNextSickLeave(初始化下一年的病假) @Title: initNextSickLeave @Description:
	 * 初始化下一年的年假 void 返回类型 @throws
	 */
	@ResponseBody
	@RequestMapping("/initNextSickLeave.htm")
	public Map<String, String> initNextSickLeave() {
		String lockValue = DistLockUtil.lock("initNextSickLeave", 60 * 5L);
		Map<String, String> map = new HashMap<String, String>();
		if (StringUtils.isBlank(lockValue)) {
			logger.info("初始化下一年的病假定时已经启动，请不要重复调用当前定时!!!");
			map.put("response", "请勿重复调用当前定时!!!");
			return map;
		} else {// 锁定定时
			new Thread(new Runnable() {
				public void run() {
					empLeaveService.initNextSickLeave();
				}
			}).start();
			map.put("response", "initNextSickLeave触发成功,请稍后查看数据！");
			return map;
		}
	}

	/**
	 * calculationYearLeaveByEmpId(根据员工id计算员工的年假) @Title:
	 * calculationYearLeaveByEmpId @Description: 根据员工id计算员工的年假 @param empId 设定文件
	 * void 返回类型 @throws
	 */
	@RequestMapping("/calculationYearLeaveByEmpId.htm")
	public void calculationYearLeaveByEmpId(Long empId) {
		empLeaveService.calculationYearLeaveByEmpId(empId);
	}

	@RequestMapping("/updateTimeByIds.htm")
	public void updateTimeByIds(String ids, String startTime, String endTime) {
		List<Long> idList = new ArrayList<Long>();
		for (String id : ids.split(",")) {
			idList.add(Long.parseLong(id));
		}

		EmpLeave empLeave = new EmpLeave();
		empLeave.setStartTime(DateUtils.parse(startTime));
		empLeave.setEndTime(DateUtils.parse(endTime));
		empLeave.setIds(idList);
		empLeaveService.updateTimeByIds(empLeave);
	}


}
