package com.ule.oa.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ule.oa.base.po.AttnSignRecord;
import com.ule.oa.base.po.EmpApplicationOvertimeLate;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.HiActinst;
import com.ule.oa.base.po.RunTask;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.AttnSignRecordService;
import com.ule.oa.base.service.EmpApplicationOvertimeLateService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.HiActinstService;
import com.ule.oa.base.service.RunTaskService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.po.CommonPo;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils; 
import com.ule.oa.web.util.ReturnUrlUtil;
import com.ule.oa.web.util.Token;

/**
 * @ClassName: 晚到申请
 * @Description: 晚到申请
 * @author yangjie
 * @date 2017年6月19日
 */
@Controller
@RequestMapping("empApplicationOvertimeLate")
public class EmpApplicationOvertimeLateController {
	
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private UserService userService;
	@Autowired
	private EmpApplicationOvertimeLateService empApplicationOvertimeLateService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private AttnSignRecordService attnSignRecordService;
	@Autowired
	private HiActinstService hiActinstService;
	@Autowired
	private RunTaskService runTaskService;
	
	//延时工作申请首页
	@RequestMapping("/index.htm")
	@Token(generate=true)//生成token
	public String index(HttpServletRequest request, String urlType){
		User user = userService.getCurrentUser();
		try {
			request.setAttribute("returnUrl", ReturnUrlUtil.getReturnUrl(urlType));
		} catch (Exception e) {
			logger.error(user.getEmployee().getCnName()+"晚到-首页："+e.toString());
			return "base/empApplicationOvertimeLate/index";
		}
		return "base/empApplicationOvertimeLate/index";
	}
	
	@ResponseBody
	@RequestMapping("/init.htm")
	public Map<String, Object> init(HttpServletRequest request){
		Map<String,Object> map = new HashMap<String, Object>();
		User user = userService.getCurrentUser();
		try{
			String overTimeDate = request.getParameter("overTimeDate");
			Date otDate = DateUtils.parse(overTimeDate, DateUtils.FORMAT_SHORT);
			int counDate = DateUtils.getIntervalDays(DateUtils.addDay(otDate, 1), DateUtils.getToday());
			if(counDate == 0) {
				map = getSignTime(user,overTimeDate);
				map.put("success", true);
			} else {
				map.put("success", false);
				map.put("msg", "请选择今天前的日期！");
			}
		}catch(Exception e){
			logger.error(user.getEmployee().getCnName()+"晚到-获取前天打卡记录："+e.toString());
			map.put("success",false);
		}
		return map;
	}
	
	@SuppressWarnings("deprecation")
	public Map<String, Object> getSignTime(User user,String overTimeDate){
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("actualTime","");
		map.put("endWorkTime","");	
		map.put("allowTime","");
		AttnSignRecord condition = new AttnSignRecord();
		condition.setEmployeeId(user.getEmployeeId());
		condition.setStartTime(DateUtils.parse(overTimeDate+" 19:00:00", DateUtils.FORMAT_LONG));
		Date date = DateUtils.parse(overTimeDate, DateUtils.FORMAT_SHORT);
		date.setDate(date.getDate()+1);
		condition.setEndTime(DateUtils.parse(DateUtils.format(date, DateUtils.FORMAT_SHORT)+" 06:10:00", DateUtils.FORMAT_LONG));
		List<AttnSignRecord> list = attnSignRecordService.getListBefore9(condition);
		if(list!=null&&list.size()>0){
			Date date_21 = DateUtils.parse(overTimeDate+" 21:00:00", DateUtils.FORMAT_LONG);
			Date date_00 = DateUtils.parse(overTimeDate+" 23:59:59", DateUtils.FORMAT_LONG);
			Date date_06 = DateUtils.parse(DateUtils.format(date, DateUtils.FORMAT_SHORT)+" 06:00:00", DateUtils.FORMAT_LONG);
			//19-21点，第二天上班时间可调整为9:30及之前
			if(list.get(0).getSignTime().getTime()>=condition.getStartTime().getTime()
					&&list.get(0).getSignTime().getTime()<date_21.getTime()){
				map.put("type",0);
				map.put("endWorkTime", DateUtils.format(list.get(0).getSignTime(),"HH:mm"));	
				map.put("allowTime","09:30");
				AttnSignRecord sign = new AttnSignRecord();
				sign.setSignTime(DateUtils.parse(DateUtils.format(date, DateUtils.FORMAT_SHORT)+" 09:00:00",DateUtils.FORMAT_LONG));
				sign.setEmployeeId(user.getEmployeeId());
				List<AttnSignRecord> signRecord = attnSignRecordService.getSignRecordList(sign);
				map.put("actualTime","");
				if(signRecord!=null&&signRecord.size()>0){
					map.put("actualTime",DateUtils.format(signRecord.get(0).getSignTime(),"HH:mm"));
				}
			}else if(list.get(0).getSignTime().getTime()>=date_21.getTime()
					&&list.get(0).getSignTime().getTime()<=date_00.getTime()){
				//21到00点之间，可调整为10点
				map.put("type",0);
				map.put("endWorkTime", DateUtils.format(list.get(0).getSignTime(),"HH:mm"));	
				map.put("allowTime","10:00");
				AttnSignRecord sign = new AttnSignRecord();
				sign.setSignTime(DateUtils.parse(DateUtils.format(date, DateUtils.FORMAT_SHORT)+" 09:00:00",DateUtils.FORMAT_LONG));
				sign.setEmployeeId(user.getEmployeeId());
				List<AttnSignRecord> signRecord = attnSignRecordService.getSignRecordList(sign);
				map.put("actualTime","");
				if(signRecord!=null&&signRecord.size()>0){
					map.put("actualTime", DateUtils.format(signRecord.get(0).getSignTime(),"HH:mm"));
				}
			}else if(list.get(0).getSignTime().getTime()>date_00.getTime()
					&&list.get(0).getSignTime().getTime()<date_06.getTime()){
				//00点到06点之间，可调整为13点
				map.put("type",0);
				map.put("endWorkTime", DateUtils.format(list.get(0).getSignTime(), "HH:mm"));	
				map.put("allowTime","13:00");
				AttnSignRecord sign = new AttnSignRecord();
				sign.setSignTime(DateUtils.parse(DateUtils.format(date, DateUtils.FORMAT_SHORT)+" 09:00:00",DateUtils.FORMAT_LONG));
				sign.setEmployeeId(user.getEmployeeId());
				List<AttnSignRecord> signRecord = attnSignRecordService.getSignRecordList(sign);
				map.put("actualTime","");
				if(signRecord!=null&&signRecord.size()>0){
					map.put("actualTime", DateUtils.format(signRecord.get(0).getSignTime(), "HH:mm"));
				}
			}else if(list.get(0).getSignTime().getTime()>=date_06.getTime()){
				//06点之后，可以调休
				map.put("type",0);
				map.put("endWorkTime", DateUtils.format(list.get(0).getSignTime(), "HH:mm"));	
				map.put("allowTime","18:00");
				AttnSignRecord sign = new AttnSignRecord();
				sign.setSignTime(DateUtils.parse(DateUtils.format(date, DateUtils.FORMAT_SHORT)+" 09:00:00",DateUtils.FORMAT_LONG));
				sign.setEmployeeId(user.getEmployeeId());
				List<AttnSignRecord> signRecord = attnSignRecordService.getSignRecordList(sign);
				map.put("actualTime","");
				if(signRecord!=null&&signRecord.size()>0){
					map.put("actualTime", DateUtils.format(signRecord.get(0).getSignTime(), "HH:mm"));
				}
			}else{
				map.put("type",2);
			}
		}else{
			map.put("type",1);
		}
		return map;
	}
	
	//保存
	@ResponseBody
	@RequestMapping("/save.htm")
	@Token(remove = true)
	public Map<String, Object> save(HttpServletRequest request,EmpApplicationOvertimeLate overtimeLate){
		User user = userService.getCurrentUser();
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			String overTimeDate = request.getParameter("overTimeDate");
			Date otDate = DateUtils.parse(overTimeDate, DateUtils.FORMAT_SHORT);
			int counDate = DateUtils.getIntervalDays(DateUtils.addDay(otDate, 1), DateUtils.getToday());
			if(counDate != 0) {
				map.put("success", false);
				map.put("message", "晚到申请日期只可以选择昨天");
				return map;
			}
		    if(overtimeLate.getOverTimeDate()==null){
			   throw new OaException("延迟工作日期不能为空！");
            }
		    if(overtimeLate.getOverTimeStartTime()==null){
				throw new OaException("延迟工作时间不能为空！");
	        }
		    if(overtimeLate.getOverTimeEndTime()==null){
				throw new OaException("延迟工作时间不能为空！");
	        }
		    if(overtimeLate.getAllowTime()==null||"".equals(overtimeLate.getAllowTime())){
				throw new OaException("允许晚到时间不能为空！");
	        }
		    if(overtimeLate.getActualTime()==null||"".equals(overtimeLate.getActualTime())){
				throw new OaException("实际晚到时间不能为空！");
	        }
            if(overtimeLate.getReason()==null||"".equals(overtimeLate.getReason())){
        	   throw new OaException("申请理由不能为空！");
            }
			overtimeLate.setEmployeeId(user.getEmployee().getId());
			overtimeLate.setCnName(user.getEmployee().getCnName());
			overtimeLate.setCode(user.getEmployee().getCode());
			overtimeLate.setDepartId(user.getDepart().getId());
			overtimeLate.setDepartName(user.getDepart().getName());
			overtimeLate.setPositionId(user.getPosition().getId());
			overtimeLate.setPositionName(user.getPosition().getPositionName());
			overtimeLate.setSubmitDate(new Date());

			overtimeLate.setDelFlag(CommonPo.STATUS_NORMAL);
			overtimeLate.setCreateTime(new Date());
			overtimeLate.setCreateUser(user.getEmployee().getCnName());
			overtimeLate.setApprovalStatus(EmpApplicationOvertimeLate.APPROVAL_STATUS_WAIT);
			overtimeLate.setVersion(null);
			int count = empApplicationOvertimeLateService.getEaoByEmpAndDateCount(overtimeLate);
			if(count>0){
				throw new OaException("当天已申请晚到！");
			}
			map = empApplicationOvertimeLateService.save(overtimeLate,user);
		}catch(Exception e){
			logger.error(user.getEmployee().getCnName()+"晚到-提交："+e.toString());
			map.put("repeatFlag", false);
			map.put("message",e.getMessage());
			map.put("success",false);
			return map;
		}
		return map;
	}
	
	//审批页面
	@RequestMapping("/approval.htm")
	@Token(generate=true)//生成token
	public String approveExcept(HttpServletRequest request,Long overtimeLateId,String nodeCode, Long ruProcdefId, String urlType){
		User user = userService.getCurrentUser();
		try {
			request.setAttribute("imgUrl",(user.getEmployee().getPicture()!=null&&!"".equals(user.getEmployee().getPicture()))?user.getEmployee().getPicture():ConfigConstants.HEAD_PICTURE_URL);
			request.setAttribute("nodeCode", nodeCode);
			request.setAttribute("ruProcdefId", ruProcdefId);
			EmpApplicationOvertimeLate overtimeLate = empApplicationOvertimeLateService.getById(overtimeLateId);
			request.setAttribute("overtimeLate", overtimeLate);
			List<HiActinst> hiActinstList = hiActinstService.getWaitListByRunId(ruProcdefId);
			request.setAttribute("hiActinstList", hiActinstList);
			RunTask param = new RunTask();
			param.setReProcdefCode(RunTask.RUN_CODE_100);
			param.setEntityId(String.valueOf(overtimeLateId));
			RunTask runTask = runTaskService.getRunTask(param);
			request.setAttribute("runTask", runTask);
			request.setAttribute("returnUrl", ReturnUrlUtil.getReturnUrl(urlType));
		} catch (Exception e) {
			logger.error(user.getEmployee().getCnName()+"晚到-审批首页："+e.toString());
			return "base/empApplicationOvertimeLate/approval";
		}
		return "base/empApplicationOvertimeLate/approval";
	}
	
	//晚到申请流程页面
	@RequestMapping("/process.htm")
	public String process(HttpServletRequest request, Long overtimeLateId,Long ruProcdefId){
		EmpApplicationOvertimeLate overtimeLate = empApplicationOvertimeLateService.getById(overtimeLateId);
		request.setAttribute("overtimeLate", overtimeLate);
		RunTask param = new RunTask();
		param.setReProcdefCode(RunTask.RUN_CODE_100);
		param.setEntityId(String.valueOf(overtimeLateId));
		RunTask runTask = runTaskService.getRunTask(param);
		request.setAttribute("runTask", runTask);
		List<HiActinst> hiActinstList = hiActinstService.getListByRunId(ruProcdefId);
		//组装职位信息
		List<Long> empIdList = new ArrayList<Long>();
		List<Employee> list = new ArrayList<Employee>();
		for(HiActinst hiActinst:hiActinstList){
			if(hiActinst.getAssigneeId()!=null){
				empIdList.add(hiActinst.getAssigneeId());
			}
		}
		if(empIdList!=null&&empIdList.size()>0){
			list = employeeService.getByEmpIdList(empIdList);
		}
		for(HiActinst hiActinst:hiActinstList){
			for(Employee employee:list){
				if(employee.getId().equals(hiActinst.getAssigneeId())){
					hiActinst.setPositionName(employee.getPositionName());
				}
			}
		}
		request.setAttribute("hiActinstList", hiActinstList);
		return "base/empApplicationOvertimeLate/process";
	}
	
	//通过
	@ResponseBody
	@RequestMapping("/pass.htm")
	@Token(remove = true)
	public Map<String, Object> pass(HttpServletRequest request,EmpApplicationOvertimeLate overtimelate){
		User user = userService.getCurrentUser();
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			overtimelate.setUpdateTime(new Date());
			overtimelate.setUpdateUser(user.getEmployee().getCnName());
			overtimelate.setApprovalStatus(EmpApplicationOvertimeLate.APPROVAL_STATUS_YES);
			map = empApplicationOvertimeLateService.updateById(overtimelate,user);
		}catch(Exception e){
			logger.error(user.getEmployee().getCnName()+"晚到-审批通过："+e.toString());
			map.put("repeatFlag", false);
			map.put("message", e.getMessage());
			map.put("success",false);
			return map;
		}
		return map;
	}
	
	//拒绝
	@ResponseBody
	@RequestMapping("/refuse.htm")
	@Token(remove = true)
	public Map<String, Object> refuse(HttpServletRequest request,EmpApplicationOvertimeLate overtimeLate){
		User user = userService.getCurrentUser();
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			overtimeLate.setUpdateTime(new Date());
			overtimeLate.setUpdateUser(user.getEmployee().getCnName());
			overtimeLate.setApprovalStatus(EmpApplicationOvertimeLate.APPROVAL_STATUS_NO);
			map = empApplicationOvertimeLateService.updateById(overtimeLate,user);
		}catch(Exception e){
			logger.error(user.getEmployee().getCnName()+"晚到-审批拒绝："+e.toString());
			map.put("repeatFlag", false);
			map.put("message",e.getMessage());
			map.put("success",false);
			return map;
		}
		return map;
	}

}
