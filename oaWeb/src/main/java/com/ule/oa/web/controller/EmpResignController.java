package com.ule.oa.web.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ule.oa.base.po.EmpResign;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.EmpResignService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.po.CommonPo;

/**
 * @ClassName: 员工离职申请
 * @Description: 员工离职申请
 * @author yangjie
 * @date 2017年5月25日
 */
@Controller
@RequestMapping("empResign")
public class EmpResignController {
	
	@Autowired
	private EmpResignService empResignService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private UserService userService;
	
	//离职申请提交页面
	@RequestMapping("/toSubmit.htm")
	public String toEmployeeRegister(HttpServletRequest request){
		User user = userService.getCurrentUser();
		List<Employee> list = employeeService.getByEmpId(user.getEmployeeId());
		request.setAttribute("employee", list.get(0));
		return "base/employeeResign/resign_submit";
	}
	
	//离职申请提交
	@ResponseBody
	@RequestMapping("/save.htm")
	public Map<String, Object> save(EmpResign empResign){
		Map<String,Object> map = new HashMap<String, Object>();
		if(empResign.getDepartId()==null){
			map.put("message", "部门不能为空");
			return map;
		}
		if(empResign.getEmployeeId()==null){
			map.put("message", "员工不能为空");
			return map;
		}
		if(empResign.getResignationDate()==null){
			map.put("message", "辞职日期不能为空");
			return map;
		}
		if(empResign.getLastDate()==null){
			map.put("message", "最后工作日不能为空");
			return map;
		}
		empResign.setDelFlag(CommonPo.STATUS_NORMAL);
		empResign.setCreateTime(new Date());
		empResign.setCreateUser("");
		empResign.setTurnoverStatus(EmpResign.TURNOVER_STATUS_SUBMIT);
		empResign.setCompanyId(1L);
		try{
			Employee emp = employeeService.getById(empResign.getEmployeeId());
			if(emp==null){
				map.put("message", "所选员工不存在");
				return map;
			}
			empResign.setEntryDate(emp.getFirstEntryTime());
			empResignService.save(empResign);
			map.put("message", "保存成功");
		}catch(Exception e){
			map.put("message", "保存失败");
		}
		return map;
	}
	
	//员工离职处理页面
	@RequestMapping("/toHandle.htm")
	public String toHandle(HttpServletRequest request,Long id,String nodeCode, Long ruProcdefId){
		//1-领导,2-人事
		request.setAttribute("nodeCode", request.getParameter("nodeCode"));
		request.setAttribute("ruProcdefId", request.getParameter("ruProcdefId"));
		if(id != null){
			EmpResign empResign = empResignService.getById(id);
			request.setAttribute("empResign", empResign);
		}
		return "base/employeeResign/resign_handle";
	}
	
	//员工离职处理
	@ResponseBody
	@RequestMapping("/handle.htm")
	public Map<String, Object> handle(HttpServletRequest request,EmpResign empResign){
		Map<String,Object> map = new HashMap<String, Object>();
		EmpResign old = empResignService.getById(empResign.getId());
		//提交状态时，只能部门领导操作
		if(EmpResign.TURNOVER_STATUS_SUBMIT==old.getTurnoverStatus()){
			if(StringUtils.isBlank(empResign.getLeaderOpinion())){
				map.put("message", "部门意见不能为空！");
				return map;
			}
			if(empResign.getLeaderDate()==null){
				map.put("message", "离职日期不能为空！");
				return map;
			}
			empResign.setUpdateTime(new Date());
			empResign.setUpdateUser("updateUser");
			empResign.setLeader(1L);
			empResign.setLeaderName("leaderName");
			empResign.setTurnoverStatus(EmpResign.TURNOVER_STATUS_LEADER);
			empResign.setHrOpinion(null);
			empResign.setHrDate(null);
			empResign.setPayrollDate(null);
		}
		//部门领导意见时，只能人事操作
		else if(EmpResign.TURNOVER_STATUS_LEADER==old.getTurnoverStatus()){
			empResign.setUpdateTime(new Date());
			empResign.setUpdateUser("updateUser");
			empResign.setHrId(1L);
			empResign.setHrName("hrName");
			empResign.setTurnoverStatus(EmpResign.TURNOVER_STATUS_HR);
			empResign.setLeaderOpinion(null);
			empResign.setLeaderDate(null);
		} //部门领导意见时，只能人事操作 
		else if(EmpResign.TURNOVER_STATUS_HR==old.getTurnoverStatus()){
					if(StringUtils.isBlank(empResign.getHrOpinion())){
						map.put("message", "人力资源部意见不能为空！");
						return map;
					}
					if(empResign.getHrDate()==null){
						map.put("message", "离职日期不能为空！");
						return map;
					}
					if(empResign.getPayrollDate()==null){
						map.put("message", "薪资核算日期不能为空！");
						return map;
					}
					empResign.setUpdateTime(new Date());
					empResign.setUpdateUser("updateUser");
					empResign.setHrId(1L);
					empResign.setHrName("hrName");
					empResign.setTurnoverStatus(EmpResign.TURNOVER_STATUS_HR);
					empResign.setLeaderOpinion(null);
					empResign.setLeaderDate(null);
				}
		try{
			empResignService.updateById(empResign);
			map.put("message", "保存成功");
		}catch(Exception e){
			map.put("message", "保存失败");
		}
		return map;
	}

}
