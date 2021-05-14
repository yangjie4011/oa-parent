package com.ule.oa.base.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ule.oa.base.common.OaCommon;
import com.ule.oa.base.mapper.EmpApplicationOvertimeLateMapper;
import com.ule.oa.base.po.EmpApplicationOvertimeLate;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.po.RunTask;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.EmpApplicationOvertimeLateService;
import com.ule.oa.base.service.EmployeeClassService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.RunTaskService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.util.AttnStatisticsUtil;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.DateUtils;

/**
 * @ClassName: 晚到申请
 * @Description: 晚到申请
 * @author yangjie
 * @date 2017年6月19日
 */
@Service
public class EmpApplicationOvertimeLateServiceImpl implements
		EmpApplicationOvertimeLateService {
	
	@Autowired
	private EmpApplicationOvertimeLateMapper empApplicationOvertimeLateMapper;
	@Autowired
	private RunTaskService runTaskService;
	@Autowired
	private UserService userService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private EmployeeClassService employeeClassService;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String,Object> save(EmpApplicationOvertimeLate userOvertimeLate,User user) throws Exception{
		Map<String,Object> map = new HashMap<String, Object>();
		empApplicationOvertimeLateMapper.save(userOvertimeLate);
		Map<String,String> result = runTaskService.startFlow(RunTask.RUN_CODE_100, user, userOvertimeLate.getId().toString());
		if(OaCommon.CODE_ERROR.equals(result.get("code"))){
			throw new OaException(result.get("msg"));
		}
		map.put("success", true);
		return map;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String,Object> updateById(EmpApplicationOvertimeLate userOvertimeLate,User user) throws Exception{
		Map<String,Object> map = new HashMap<String, Object>();
		if(userOvertimeLate.getApprovalStatus() != null && userOvertimeLate.getApprovalStatus() != EmpApplicationOvertimeLate.APPROVAL_STATUS_YES) {
			empApplicationOvertimeLateMapper.updateById(userOvertimeLate);
		}
		if(userOvertimeLate.getApprovalStatus().intValue() == EmpApplicationOvertimeLate.APPROVAL_STATUS_YES) {
			Map<String,String> result = runTaskService.nextFlow(userOvertimeLate.getRuProcdefId(), user, userOvertimeLate.getApprovalReason(), userOvertimeLate.getId().toString());
			if(OaCommon.CODE_ERROR.equals(result.get("code"))){
				throw new OaException(result.get("msg"));
			}
			if("RESIGN_HR".equals(userOvertimeLate.getNodeCode())){
				if(userOvertimeLate.getApprovalStatus() != null && userOvertimeLate.getApprovalStatus().intValue() == EmpApplicationOvertimeLate.APPROVAL_STATUS_YES) {
					empApplicationOvertimeLateMapper.updateById(userOvertimeLate);
				}
				//充工时
				EmpApplicationOvertimeLate old = empApplicationOvertimeLateMapper.getById(userOvertimeLate.getId());
				if(old.getActualTime()==null||"".equals(old.getActualTime())){
					throw new OaException("实际晚到时间为空，不能审批通过！");
				}
				Employee employee = employeeService.getById(old.getEmployeeId());
				EmployeeClass employeeClass = new EmployeeClass();
				employeeClass.setEmployId(employee.getId());
				employeeClass.setClassDate(DateUtils.addDay(old.getOverTimeDate(), 1));
				EmployeeClass result_e = employeeClassService.getEmployeeClassSetting(employeeClass);
				if(result_e!=null){
					AttnStatisticsUtil attnStatisticsUtil= new AttnStatisticsUtil();
					Date actime = DateUtils.parse(old.getActualTime(),"HH:mm");
					Date sttime = DateUtils.parse("09:00","HH:mm");
					int workHours = (int)(actime.getTime()-sttime.getTime())/(1800*1000);
					attnStatisticsUtil.calWorkHours(employee.getCompanyId(), employee.getId(), user.getEmployee().getCnName(),
							DateUtils.addDay(old.getOverTimeDate(), 1),
							(workHours+1)*0.5>0?(workHours+1)*0.5:0, RunTask.RUN_CODE_100,result_e.getStartTime(),
							DateUtils.parse(DateUtils.format(DateUtils.addDay(old.getOverTimeDate(), 1), DateUtils.FORMAT_SHORT)+" "+old.getActualTime()+":00", DateUtils.FORMAT_LONG),null,old.getReason(),old.getId());
				}
			}
		} else if(userOvertimeLate.getApprovalStatus().intValue() == EmpApplicationOvertimeLate.APPROVAL_STATUS_NO) {
			Map<String,String> result = runTaskService.refuse(userOvertimeLate.getRuProcdefId(), user, userOvertimeLate.getApprovalReason());
			if(OaCommon.CODE_ERROR.equals(result.get("code"))){
				throw new OaException(result.get("msg"));
			}
		}
		map.put("success", true);
		return map;
	}

	@Override
	public EmpApplicationOvertimeLate getById(Long id) {
		return empApplicationOvertimeLateMapper.getById(id);
	}

	@Override
	public int getEaoByEmpAndDateCount(
			EmpApplicationOvertimeLate userOvertimeLate) {
		return empApplicationOvertimeLateMapper.getEaoByEmpAndDateCount(userOvertimeLate);
	}

}
