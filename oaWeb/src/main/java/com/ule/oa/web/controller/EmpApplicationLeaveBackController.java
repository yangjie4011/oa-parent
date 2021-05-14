package com.ule.oa.web.controller;

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
import com.ule.oa.base.po.EmpApplicationLeave;
import com.ule.oa.base.po.EmpApplicationLeaveAbolish;
import com.ule.oa.base.po.EmpApplicationLeaveDetail;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.EmpApplicationLeaveAbolishService;
import com.ule.oa.base.service.EmpApplicationLeaveService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.service.impl.ActivitiServiceImpl;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.web.util.ReturnUrlUtil;
import com.ule.oa.web.util.Token;

/**
 * @ClassName: 销假申请
 * @Description: 销假申请
 * @author yangjie
 * @date 2018年4月17日
 */
@Controller
@RequestMapping("empApplicationLeaveBack")
public class EmpApplicationLeaveBackController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private UserService userService;
	@Autowired
	private EmpApplicationLeaveService empApplicationLeaveService;
	@Autowired
	private EmpApplicationLeaveAbolishService empApplicationLeaveAbolishService;
	@Autowired
	private ActivitiServiceImpl activitiServiceImpl;
	
	//请假申请首页
	@RequestMapping("/index.htm")
	@Token(generate=true)//生成token
	public String index(HttpServletRequest request, Long leaveId, String urlType){
		User user = userService.getCurrentUser();
		try {
			EmpApplicationLeave leave  = empApplicationLeaveService.getById(leaveId);
			request.setAttribute("leave", leave);
			List<EmpApplicationLeaveDetail> detailList = empApplicationLeaveService.getLeaveDetailList(leaveId);
			request.setAttribute("detailList", detailList);
			logger.info("销假首页："+user.getEmployeeId());
			request.setAttribute("returnUrl", ReturnUrlUtil.getReturnUrl(urlType));
		} catch (Exception e) {
			logger.error(user.getEmployee().getCnName()+"销假-首页："+e.toString());
			return "base/empApplicationLeaveBack/index";
		}
		return "base/empApplicationLeaveBack/index";
	}
	//保存
	@ResponseBody
	@RequestMapping("/saveAbolishLeave.htm")
	@Token(remove = true)
	public Map<String, Object> save(HttpServletRequest request,EmpApplicationLeaveAbolish leaveAbolish){
		User user = userService.getCurrentUser();
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			map = empApplicationLeaveAbolishService.save(request,leaveAbolish,user);
		}catch(Exception e){
			logger.error(user.getEmployee().getCnName()+"销假申请-提交："+e.toString());
			map.put("repeatFlag", false);
			map.put("message", e.getMessage());
			map.put("success",false);
			return map;
		}
		return map;
	}
	
	//销假申请审批页
	@RequestMapping("/approve.htm")
	@Token(generate=true)//生成token
	public String approve(HttpServletRequest request,Long leaveAbolishId,String flag,String urlType){
		User user = userService.getCurrentUser();
		try {
			
			if(leaveAbolishId!=null){
				EmpApplicationLeaveAbolish leaveAbolish = empApplicationLeaveAbolishService.getById(leaveAbolishId);
				if(leaveAbolish!=null){
					request.setAttribute("leaveAbolish", leaveAbolish);
					EmpApplicationLeave leave  = empApplicationLeaveService.getById(leaveAbolish.getLeaveId());
					request.setAttribute("leave", leave);//原请假申请单信息（员工信息）
					List<EmpApplicationLeaveDetail> detailList = empApplicationLeaveService.getLeaveDetailList(leave.getId());
					request.setAttribute("leaveDetail", detailList.get(0));//原申请单明细（假期类型等数据）
					request.setAttribute("isSelf", false);
					request.setAttribute("canApprove", false);
					Map<String,String> map = empApplicationLeaveAbolishService.
							getActualEndTime(leave.getEmployeeId(), detailList.get(0).getLeaveType(), detailList.get(0).getStartTime(), leaveAbolish.getStartTime());
					request.setAttribute("actualEndTime", map.get("endTime"));
					//审批显示
					if(leaveAbolish.getApprovalStatus().intValue()==100||leaveAbolish.getApprovalStatus().intValue()==200) {
						if(leave.getEmployeeId().longValue()==user.getEmployeeId().longValue()){
							request.setAttribute("isSelf", true);
						}
						//办理中,taskService查询任务
						Task task = activitiServiceImpl.queryTaskByProcessInstanceId(leaveAbolish.getProcessInstanceId());
						request.setAttribute("actName", task!=null?task.getName():"");
						if(StringUtils.equalsIgnoreCase("can", flag)) {
							request.setAttribute("canApprove", true);
						}
					}
				}else{
					logger.error("根据ID="+leaveAbolishId+"未查到销假申请单。");
				}
			}else{
				logger.error(user.getEmployee().getCnName()+"销假申请单ID为空");
			}
			request.setAttribute("returnUrl", ReturnUrlUtil.getReturnUrl(urlType));
		} catch (Exception e) {
			logger.error(user.getEmployee().getCnName()+"销假-审批首页："+e.toString());
			return "base/empApplicationLeaveBack/approve";
		}
		return "base/empApplicationLeaveBack/approve";
	}
	
	
	@ResponseBody
	@RequestMapping("/getActualEndTime.htm")
	public Map<String, String> getActualEndTime(Long leaveId,String startTime,String endTime){
		Map<String,String> map = new HashMap<String, String>();
		try{
			map = empApplicationLeaveAbolishService.checkStartTime(leaveId,
					DateUtils.parse(startTime, DateUtils.FORMAT_LONG),DateUtils.parse(endTime, DateUtils.FORMAT_LONG));
			map.put("success", "true");
		}catch(Exception e){
			map.put("success", "false");
			map.put("msg", e.getMessage());
			return map;
		}
		return map;
	}
}
