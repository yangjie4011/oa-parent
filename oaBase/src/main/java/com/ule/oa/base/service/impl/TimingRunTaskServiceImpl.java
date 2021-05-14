package com.ule.oa.base.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ule.oa.base.common.OaCommon;
import com.ule.oa.base.mapper.EmployeeMapper;
import com.ule.oa.base.mapper.TimingRunTaskMapper;
import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.QuitHistory;
import com.ule.oa.base.po.TimingRunTask;
import com.ule.oa.base.service.AnnualVacationService;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.EmpApplicationBusinessService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.RunTaskService;
import com.ule.oa.base.service.TimingRunTaskService;
import com.ule.oa.base.util.SendMailUtil;
import com.ule.oa.common.utils.DateUtils;

@Service
public class TimingRunTaskServiceImpl implements TimingRunTaskService{
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private TimingRunTaskMapper timingRunTaskMapper;
	@Autowired
	private RunTaskService runTaskService;
	@Autowired
	private EmpApplicationBusinessService empApplicationBusinessService;
	@Autowired
	private EmployeeMapper employeeMapper;
	@Resource
	private AnnualVacationService annualVacationService;
	@Resource
	private ConfigService configService;
	@Autowired
	private EmployeeService employeeService;
	
	@Override
	public List<TimingRunTask> getList() {
		return timingRunTaskMapper.getList();
	}

	@Override
	public void startRunTask() {
		
		/**1.第一个定时**/
		List<TimingRunTask> list = getList();
		Map<Long,Employee> empMap = new HashMap<Long, Employee>();
		for (TimingRunTask timingRunTask : list) {
			Employee employee = new Employee();
			if(empMap.containsKey(timingRunTask.getCreatorId())) {
				employee = empMap.get(timingRunTask.getCreatorId());
			} else {
				employee = employeeMapper.getById(timingRunTask.getCreatorId());
			}
			try {
				Map<String, String> mapCode = empApplicationBusinessService.startReportTask(timingRunTask.getReProcdefCode(), employee, timingRunTask.getEntityId());
				if(OaCommon.CODE_OK.equals(mapCode.get("code"))) {
					timingRunTask.setProcessStatus(TimingRunTask.PROCESS_STATUS1);
					timingRunTaskMapper.updateById(timingRunTask);
				}
			} catch (Exception e) {
				logger.error("定时启动流程"+e.toString());
			}
		}
		
		/**2.第二个定时，每个月第4个工作日，提醒上个月的待审批**/
		
		
		List<String> numList = new ArrayList<>();
		List<Config> configList = configService.getListByCode("mustArrangeOfAssignMind");
		if(null != configList && !configList.isEmpty()){/**存在配置的天数**/
			Config asConfig = configList.get(0);
			String displayCode = asConfig.getDisplayCode();
			String[] codeArr = displayCode.split(",");
			numList =  Stream.of(codeArr).collect(Collectors.toList());
		}else{                                          /**不存在配置的天数，用默认的**/
			numList.add("4");
		}
		
		numList.forEach(day ->{
			int assignMindDay = day==null?4:Integer.parseInt(day);
			Date fourthDay = DateUtils.parse(DateUtils.format(annualVacationService.get5WorkingDayNextmonth(assignMindDay), DateUtils.FORMAT_SHORT), DateUtils.FORMAT_SHORT);
			Date today = DateUtils.parse(DateUtils.format(DateUtils.getToday(), DateUtils.FORMAT_SHORT), DateUtils.FORMAT_SHORT);
			
			if(fourthDay.compareTo(today) == 0){/**如果今天刚好是第四天**/
				
				Date lastMonthDay = DateUtils.addMonth(today, -1);
				try {
					Date beginDate = DateUtils.getFirstDay(lastMonthDay);
					Date endDate = DateUtils.getLastDay(lastMonthDay);
					
					/**找到存在待审批的人**/
					List<Map<String,Object>> assigneeList = timingRunTaskMapper.getWaitAssigneeList(beginDate,endDate);
					
					if(null != assigneeList && !assigneeList.isEmpty()){
						
						assigneeList.forEach(map ->{
							
							Long assigneeId = Long.valueOf((String)map.get("assigneeId")); 
							Employee assigneeEmployee = employeeMapper.getById(assigneeId);
							
							if(null != assigneeEmployee){
								String email = assigneeEmployee.getEmail();
								String content = "";
								
								content = "Dear " + assigneeEmployee.getCnName() +":<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" 
								        + "明日为考勤审批截止日，尚有未审批的单据等待您的审批，请尽快处理哦！";
								try {
									SendMailUtil.sendNormalMail(content, email, assigneeEmployee.getCnName(), "待审批提醒");
								} catch (Exception e) {
									logger.error("发送待审批邮件出错，{}",e.getMessage());
								}
							}
						});
					}else{
						
					}
				} catch (Exception e) {
					logger.error("提醒上个月的待审批出错，{}",e.getMessage());
				}
			}else{
				
			}
		});
	
	}
}
