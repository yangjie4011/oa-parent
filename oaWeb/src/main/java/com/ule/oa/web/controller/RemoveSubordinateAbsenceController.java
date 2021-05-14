package com.ule.oa.web.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.flowable.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ule.oa.base.cache.ConfigCacheManager;
import com.ule.oa.base.po.RemoveSubordinateAbsence;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.RemoveSubordinateAbsenceService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.service.impl.ActivitiServiceImpl;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.web.util.ReturnUrlUtil;
import com.ule.oa.web.util.Token;

/**
  * @ClassName: RemoveSubAbsenceController
  * @Description: 消下属缺勤控制层
  * @author xujintao
  * @date 2019年07月03日 上午10:11:45
 */
@Controller
@RequestMapping("removeSubordinateAbsence")
public class RemoveSubordinateAbsenceController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private UserService userService;
	@Resource
	private ConfigCacheManager configCacheManager;
	@Autowired
	private ActivitiServiceImpl activitiServiceImpl;
	@Autowired
	private RemoveSubordinateAbsenceService removeSubordinateAbsenceService;
	
	//消下属缺勤控制层申请审批页面
	@RequestMapping("/approval.htm")
	@Token(generate=true)//生成token
	public String approval(HttpServletRequest request,Long removeSubordinateAbsenceId,String flag, String urlType){
		User user = userService.getCurrentUser();
		try {
			request.setAttribute("imgUrl",(user.getEmployee().getPicture()!=null&&!"".equals(user.getEmployee().getPicture()))?user.getEmployee().getPicture():ConfigConstants.HEAD_PICTURE_URL);
			RemoveSubordinateAbsence removeSubordinateAbsence = removeSubordinateAbsenceService.getById(removeSubordinateAbsenceId);
			request.setAttribute("removeSubordinateAbsence", removeSubordinateAbsence);
			request.setAttribute("isSelf", false);
			request.setAttribute("canApprove", false);
			if(removeSubordinateAbsence.getApproalStatus().intValue()==100) {
				if(removeSubordinateAbsence.getEmployeeId().longValue()==user.getEmployeeId().longValue()){
					request.setAttribute("isSelf", true);
				}
				//办理中,taskService查询任务
				Task task = activitiServiceImpl.queryTaskByProcessInstanceId(removeSubordinateAbsence.getProcessinstanceId());
				request.setAttribute("actName", task.getName());
				if(StringUtils.equalsIgnoreCase("can", flag)) {
					request.setAttribute("canApprove", true);
				}
			}
			request.setAttribute("returnUrl", ReturnUrlUtil.getReturnUrl(urlType));
		} catch (Exception e) {
			logger.error(user.getEmployee().getCnName()+"消下属缺勤-审批首页："+e.toString());
			return "base/removeSubordinateAbsence/approval";
		}
		return "base/removeSubordinateAbsence/approval";
	}
	
}
