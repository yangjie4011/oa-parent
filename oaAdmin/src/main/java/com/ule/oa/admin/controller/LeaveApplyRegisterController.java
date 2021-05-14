package com.ule.oa.admin.controller;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.ule.oa.admin.util.IsMenu;
import com.ule.oa.admin.util.IsOperation;
import com.ule.oa.admin.util.Token;
import com.ule.oa.base.common.ResponseDTO;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.LeaveApplyRegister;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.EmpApplicationLeaveService;
import com.ule.oa.base.service.LeaveApplyRegisterService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.PageModel;
import com.ule.oa.common.utils.RedisUtils;
import com.ule.oa.common.utils.UUIDGenerator;

/**
 * 休假登记
 * @author yangjie
 *
 */
@Controller
@RequestMapping("leaveApplyRegister")
public class LeaveApplyRegisterController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private EmpApplicationLeaveService empApplicationLeaveService;
	@Autowired
	private LeaveApplyRegisterService leaveApplyRegisterService;
	@Autowired
	private UserService userService;
	
	@IsMenu
	@Token(generate=true)//生成token
	@RequestMapping("/index.htm")
	public ModelAndView index(){
		
		return new ModelAndView("base/leaveManager/leave_apply_register");
	}
	
	/**
	 * 根据请假日期获取年假，事假，调休，病假，其它类型假期的请假小时数
	 * @param employeeId
	 * @param type
	 * @param date
	 * @param leaveType
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getLeaveHours.htm")
	public Map<String, Object> getLeaveHours(Long employeeId, String type, String date, String leaveType) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = empApplicationLeaveService.getLeaveHours(employeeId, type, date, leaveType);
		} catch (Exception e) {
			map.put("code", "9999");
			map.put("message", "系统异常！");
			logger.error(employeeId + "获取请假开始结束小时数失败", e);
			return map;
		}
		return map;
	}
	
	/**
	 * 根据开始结束时间计算年假，事假，调休，病假，其它类型假期的请假天数
	 * @param empId
	 * @param startTime1
	 * @param startTime2
	 * @param endTime1
	 * @param endTime2
	 * @param leaveType
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/calculatedLeaveDays.htm")
	public Map<String, Object> getLeaveDays(Long empId, String startTime1, String startTime2, String endTime1,
			String endTime2, String leaveType) {
		return empApplicationLeaveService.calculatedLeaveDays(empId, startTime1, startTime2, endTime1, endTime2, leaveType);
	}
	
	/**
	 * 根据员工ID获取子女数（出生日期在当前时间往前推一年之后的孩子）
	 * @param employeeId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getChildren.htm")
	public Map<String, Object> getChildren(Long employeeId) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = empApplicationLeaveService.getChildren(employeeId);
		} catch (Exception e) {
			logger.error(employeeId + "请假-获取子女数：",e);
		}
		return map;
	}
	
	/**
	 * 根据请假开始时间和天数获取产假额外请假天数（结束日期往前推一个月，存在的法定假日需要额外补給员工）
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getLeaveCountStartEndTime.htm")
	public Map<String, Object> getLeaveCountStartEndTime(HttpServletRequest request) {
		Map<String, Object> map = empApplicationLeaveService.getExtraMaternityLeaveDays(request);
		return map;
	}
	
	/**
	 * 假期登记
	 * @param request
	 * @return
	 */
	@Token(remove = true)
	@ResponseBody
	@RequestMapping("/registerLeave.htm")
	public ResponseDTO registerLeave(@Validated LeaveApplyRegister data,BindingResult result) {
		Map<String,Object> map = new HashMap<String,Object>();
		User user = userService.getCurrentUser();
		ResponseDTO response = null;
		try {
			response = leaveApplyRegisterService.registerLeave(data,result);
		} catch (Exception e) {
			logger.error("休假登记失败：",e);
			response = new ResponseDTO(ResponseDTO.FAIL_CODE,e.getMessage());
		}finally{
			String uuId = UUIDGenerator.generate();
            try {
				RedisUtils.setString(ConfigConstants.ADMIN_REDIS_PRE+String.valueOf(user.getEmployee().getId()),uuId,1800000);
				map.put("token", uuId);
				if(response!=null) {
					response.setData(map);
				}
			} catch (Exception e1) {
				logger.error(user.getEmployee().getCnName()+"registerLeave：",e1);
			}
		}	
		return response;
	}
	
	/**
	 * 休假登记分页查询
	 * @param data
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getRegisterLeaveListByPage.htm")
	public PageModel<LeaveApplyRegister> getRegisterLeaveListByPage(LeaveApplyRegister data){

		PageModel<LeaveApplyRegister> pm=new PageModel<LeaveApplyRegister>();
		pm.setRows(new java.util.ArrayList<LeaveApplyRegister>());
		
		try {
			pm = leaveApplyRegisterService.getRegisterLeaveListByPage(data);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
}
