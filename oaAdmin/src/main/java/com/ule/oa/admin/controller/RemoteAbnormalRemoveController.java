package com.ule.oa.admin.controller;

import java.util.Date;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.google.common.collect.Maps;
import com.ule.oa.admin.util.IsMenu;
import com.ule.oa.base.po.RemoteAbnormalRemoveDTO;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.RemoteAbnormalRemoveService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.PageModel;

/**
 * 远程异常消除
 * @author yangjie
 *
 */
@Controller
@RequestMapping("remoteAbnormalRemove")
public class RemoteAbnormalRemoveController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private RemoteAbnormalRemoveService remoteAbnormalRemoveService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private UserService userService;
	
	//工作管理-远程异常消除-页面
	@IsMenu
	@RequestMapping("/index.htm")
	public ModelAndView index(HttpServletRequest request){
		try {
			request.setAttribute("date",DateUtil.formatDate(new Date(), "yyyy-MM-dd"));
			request.setAttribute("companyId", employeeService.getCurrentEmployee().getCompanyId());
		} catch (OaException e) {
			
		}
		return new ModelAndView("base/workManagement/remote_abnormal_remove");
	}
	
	//工作管理-远程异常消除-查询
	@ResponseBody
	@RequestMapping("/getPageList.htm")
	public PageModel<RemoteAbnormalRemoveDTO> getPageList(RemoteAbnormalRemoveDTO param) {
		
		PageModel<RemoteAbnormalRemoveDTO> pm = new PageModel<RemoteAbnormalRemoveDTO>();
		pm.setRows(new java.util.ArrayList<RemoteAbnormalRemoveDTO>());
		pm.setTotal(0);
		pm.setPageNo(1);

		try {
			pm = remoteAbnormalRemoveService.getPageList(param);
		} catch (Exception e) {
			logger.error("RemoteAbnormalRemoveController getPageList"+e.getMessage());
		}

		return pm;
	}
	
	
	//工作管理-远程异常消除-审阅
	@ResponseBody
	@RequestMapping(value="/review.htm")
	public Map<String, Object> review(String employeeIds,String registerDates,Integer approvalStatus,String approvalReason) {
		Map<String,Object> result = Maps.newHashMap();
		logger.info("remoteAbnormalRemove review入参：employeeIds="+employeeIds+";registerDates="+registerDates+";approvalStatus="+approvalStatus+";approvalReason="+approvalReason);
		
		String[] employeeIdsList = employeeIds.split(",");
		String[] registerDatesList = registerDates.split(",");
		StringBuffer msg = new StringBuffer();
        User user = userService.getCurrentUser();
        if(user==null||user.getEmployee()==null){
        	result.put("msg", "请重新登录");
        	return result;
        }
		
		for(int i=0;i<employeeIdsList.length;i++){
			try{
				Long employeeId = Long.valueOf(employeeIdsList[i]);
				Date registerDate = DateUtils.parse(registerDatesList[i], DateUtils.FORMAT_SHORT);
				Map<String,String> data = remoteAbnormalRemoveService.review(employeeId, registerDate, approvalStatus, approvalReason,i);
				if("false".equals(data.get("success"))){
					msg.append(data.get("msg"));
				}
				
			}catch(Exception e){
				msg.append("\n第" + (i + 1) + "条数据： "+e.getMessage());
			}
		}
		
		if(StringUtils.isNotBlank(msg)){
			result.put("msg",msg);
		}else{
			result.put("msg","审阅成功！");
		}
	
		return result;
	}

	@ResponseBody
	@RequestMapping(value="/repairDate.htm")
	public String repairDate() {
		String result = "";
		try {
			remoteAbnormalRemoveService.repairDate();
			result = "success";
		} catch (Exception e) {
			result = "fail";
			logger.error("repairDate:"+e.getMessage());
		}

		return result;
	}
	

}
