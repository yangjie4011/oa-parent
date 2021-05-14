package com.ule.oa.web.controller;

import java.util.Date;
import java.util.HashMap;
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
import com.ule.oa.base.po.BaseEmpWorkLog;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.BaseEmpWorkLogService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.service.impl.ActivitiServiceImpl;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.web.util.ReturnUrlUtil;
import com.ule.oa.web.util.Token;

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
	private ActivitiServiceImpl activitiServiceImpl;
	
	/**
	 * 工作日志填写页
	 * @return
	 */
	@RequestMapping("/index.htm")
	public String index(HttpServletRequest request, String workDate){
		request.setAttribute("workDate", workDate);
		if(StringUtils.isBlank(workDate)){
			request.setAttribute("workDate", DateUtils.format(new Date(), DateUtils.FORMAT_SHORT));
		}
		request.setAttribute("returnUrl", ReturnUrlUtil.getReturnUrl("11"));
		return "workLog/index";
	}
	
	/**
	 * 日志须知
	 * @return
	 */
	@RequestMapping("/notice.htm")
	public String notice(HttpServletRequest request){
		return "workLog/notice";
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
	
	// 请假申请审批页
	@RequestMapping("/approval.htm")
	@Token(generate = true) // 生成token
	public String approval(HttpServletRequest request, Long taskId, String flag, String urlType,String index) {
		User user = userService.getCurrentUser();
		try {
			BaseEmpWorkLog workLog = baseEmpWorkLogService.getById(taskId);
			if(workLog!=null&&StringUtils.isNotBlank(workLog.getWorkContent())){
				request.setAttribute("workContentList", workLog.getWorkContent().split("==="));
			}
			if(workLog!=null&&StringUtils.isNotBlank(workLog.getNextDayWorkPlan())){
				request.setAttribute("workPlanList", workLog.getNextDayWorkPlan().split("==="));
			}
			if(workLog!=null&&StringUtils.isNotBlank(workLog.getWorkProblem())){
				request.setAttribute("workProblemList", workLog.getWorkProblem().split("==="));
			}
			request.setAttribute("workLog", workLog);
			request.setAttribute("isSelf", false);
			request.setAttribute("index", index);
			request.setAttribute("canApprove", false);
			if(workLog!=null&&workLog.getApprovalStatus().equals(ConfigConstants.DOING_STATUS)) {
				if(workLog.getEmployeeId().equals(user.getEmployeeId())){
					request.setAttribute("isSelf", true);
				}
				//办理中,taskService查询任务
				Task task = activitiServiceImpl.queryTaskByProcessInstanceId(workLog.getProcessId());
				request.setAttribute("actName", task.getName());
				if(StringUtils.equalsIgnoreCase("can", flag)) {
					request.setAttribute("canApprove", true);
				}
			}
			request.setAttribute("returnUrl", ReturnUrlUtil.getReturnUrl(urlType));
		} catch (Exception e) {
			logger.error(user.getEmployee().getCnName() + "员工日志-审批首页：" + e.toString());
			return "workLog/approve";
		}
		return "workLog/approve";
	}

}
