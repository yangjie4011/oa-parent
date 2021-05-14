package com.ule.oa.base.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ule.oa.base.common.OaCommon;
import com.ule.oa.base.po.AttnWorkHours;
import com.ule.oa.base.po.ClassSetting;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.po.RunTask;
import com.ule.oa.base.service.AttnStatisticsService;
import com.ule.oa.base.service.ClassSettingService;
import com.ule.oa.base.service.EmployeeClassService;
import com.ule.oa.common.spring.SpringContextUtils;
import com.ule.oa.common.utils.DateUtils;

public class AttnStatisticsUtil {
	
	/**
	 * 
	 * @param companyId 公司ID
	 * @param employeeId 员工ID
	 * @param updateUser 更新姓名
	 * @param workDate 填充日期
	 * @param workHours 填充时间
	 * @param runCode	流程CODE
	 */
	public void calWorkHours(Long companyId, Long employeeId,
			String updateUser, Date workDate, Double workHours, String runCode,Date start,Date end,String leaveType,String dataReason,Long billId) {
		AttnWorkHours condition = new AttnWorkHours();
		condition.setCompanyId(companyId);
		condition.setEmployeeId(employeeId);
		condition.setWorkDate(workDate);
		condition.setDataType(Integer.valueOf(runCode));
		condition.setWorkHours(workHours);
		condition.setCreateUser(updateUser);
		condition.setStartTime(start);
		condition.setEndTime(end);
		condition.setBillId(billId);
		if("60".equals(runCode)||"130".equals(runCode)){
			condition.setLeaveType(Integer.valueOf(leaveType));
		}
		//其他假期
		if("12".equals(leaveType)){
			condition.setDataReason(dataReason);
		}
		AttnStatisticsService attnStatisticsService = SpringContextUtils.getContext().getBean(AttnStatisticsService.class);
		attnStatisticsService.setAttStatisticsForm(condition);
	}

	/**
	 * 
	 * @param employee 员工
	 * @param strDate 开始时间
	 * @param enDate  结束时间
	 * @param runCode 流程CODE
	 * @param leaveType 请假类型
	 * @param leaveHours 小时
	 */
	public void calWorkHours(String updateUser,Employee employee, Date strDate, Date enDate, String runCode,
			String leaveType, Double leaveHours,int dayType,String dataReason,Long billId) {
		Map<Long, ClassSetting> csMap = new HashMap<Long, ClassSetting>();
		ClassSettingService classSettingService = SpringContextUtils.getContext().getBean(ClassSettingService.class);
		List<ClassSetting> csList = classSettingService.getList();
		for (ClassSetting classSetting : csList) {
			csMap.put(classSetting.getId(), classSetting);
		}
		// 开始时间-天
		String startTime1 = DateUtils.format(strDate, DateUtils.FORMAT_SHORT);
		// 开始时间-时分
		String startTime2 = DateUtils.format(strDate, "HH");
		// 结束时间-天
		String endTime1 = DateUtils.format(enDate, DateUtils.FORMAT_SHORT);
		// 结束时间-时分
		String endTime2 = DateUtils.format(enDate, "HH");
		// 假期类型
		// 开始时间
		Date dayStart = DateUtils.parse(startTime1, DateUtils.FORMAT_SHORT);
		// 结束时间
		Date dayEnd = DateUtils.parse(endTime1, DateUtils.FORMAT_SHORT);
		int length = DateUtils.getIntervalDays(dayStart, dayEnd) + 1;
		if (OaCommon.getLeaveMap().containsKey(leaveType)) {
		}
		try {
			// 计算整天
			boolean isEndTime = true;
			Map<String, Long> timeMap = new HashMap<String, Long>();
			String beginData = "";
			String endData = "";
			List<String> datas = new ArrayList<String>();
			do {
				datas.add(DateUtils.format(dayStart, DateUtils.FORMAT_SHORT));
				dayStart = DateUtils.addDay(dayStart, 1);
				if (DateUtils.getIntervalDays(dayStart, dayEnd) < 0) {
					isEndTime = false;
				}
			} while (isEndTime);
			EmployeeClassService employeeClassService = SpringContextUtils.getContext().getBean(EmployeeClassService.class);
			Map<String, Object> resultMap = employeeClassService.getEmployeeClassSetting(employee, datas);
			beginData = (String) resultMap.get("beginData");
			endData = (String) resultMap.get("endData");
			if (beginData.equals(startTime1)) {
				if(RunTask.RUN_CODE_60.equals(runCode)&&("11".equals(leaveType)||"1".equals(leaveType)||"2".equals(leaveType)
						||"5".equals(leaveType)||"12".equals(leaveType))){
					EmployeeClass classSetting = (EmployeeClass) resultMap.get(beginData);
					Boolean isCommon = false;
				    if(classSetting != null){
						isCommon = true;
					}
					if(isCommon) {
						double beginLeaveDays = 0;
						boolean standard = ("09:00".equals(DateUtils.format(classSetting.getStartTime(), "HH:mm"))&&"18:00".equals(DateUtils.format(classSetting.getEndTime(), "HH:mm")))?true:false;
						boolean standard1 = (classSetting.getMustAttnTime()==8)?true:false;
						if(length == 1){
							if(standard1&&!standard){
								if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getStartTime(), "HH")).intValue()
										&& Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getEndTime(),-300), "HH")).intValue()){
									classSetting.setStartTime(DateUtils.parse(startTime1+" "+startTime2+":00:00", DateUtils.FORMAT_LONG));
									classSetting.setEndTime(DateUtils.addMinute(classSetting.getEndTime(),-240));
								}else if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(),300), "HH")).intValue()
										&& Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()){
									classSetting.setStartTime(DateUtils.addMinute(classSetting.getStartTime(),240));
								}
							}else{
								classSetting.setStartTime(DateUtils.parse(startTime1+" "+startTime2+":00:00", DateUtils.FORMAT_LONG));
								classSetting.setEndTime(DateUtils.parse(endTime1+" "+endTime2+":00:00", DateUtils.FORMAT_LONG));
							}
							this.calWorkHours(employee.getCompanyId(),
									employee.getId(), updateUser,DateUtils.parse(startTime1, DateUtils.FORMAT_SHORT),
									beginLeaveDays, runCode,classSetting.getStartTime(),classSetting.getEndTime(),leaveType,dataReason,billId);
						}else{
							if(standard1&&!standard){
								if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(),300), "HH")).intValue()){
									classSetting.setStartTime(DateUtils.addMinute(classSetting.getStartTime(),240));
								}
							}else{
								classSetting.setStartTime(DateUtils.parse(startTime1+" "+startTime2+":00:00", DateUtils.FORMAT_LONG));
							}
							this.calWorkHours(employee.getCompanyId(),
									employee.getId(), updateUser,DateUtils.parse(startTime1, DateUtils.FORMAT_SHORT),
									beginLeaveDays, runCode,classSetting.getStartTime(),classSetting.getEndTime(),leaveType,dataReason,billId);
						}
					}
				}else{
					EmployeeClass classSetting = (EmployeeClass) resultMap.get(beginData);
					Boolean isCommon = false;
			        if (classSetting != null) {
						isCommon = true;
					}
					if(isCommon){
						//哺乳假
						if("4".equals(leaveType)){
							if (leaveHours != null) {
								int preHours = (int) (leaveHours*60);
								if(dayType == 100){
									this.calWorkHours(employee.getCompanyId(),
											employee.getId(), updateUser,DateUtils.parse(startTime1, DateUtils.FORMAT_SHORT),
											leaveHours, runCode,DateUtils.parse(startTime1+" "+DateUtils.format(classSetting.getStartTime(), "HH:mm:ss"), DateUtils.FORMAT_LONG),
											DateUtils.parse(startTime1+" "+DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), preHours), "HH:mm:ss"), DateUtils.FORMAT_LONG),leaveType,dataReason,billId);
								}else if(dayType == 200){
									this.calWorkHours(employee.getCompanyId(),
											employee.getId(), updateUser,DateUtils.parse(startTime1, DateUtils.FORMAT_SHORT),
											leaveHours, runCode,DateUtils.parse(startTime1+" "+DateUtils.format(DateUtils.addMinute(classSetting.getEndTime(), -preHours), "HH:mm:ss"), DateUtils.FORMAT_LONG),
											DateUtils.parse(startTime1+" "+DateUtils.format(classSetting.getEndTime(), "HH:mm:ss"), DateUtils.FORMAT_LONG),leaveType,dataReason,billId);
								}
							}
						}else{
							//除年假，病假，调休，其它，事假，哺乳假 外的所有类型假期
							this.calWorkHours(employee.getCompanyId(),
									employee.getId(), updateUser,DateUtils.parse(startTime1, DateUtils.FORMAT_SHORT),
									8d, runCode,classSetting.getStartTime(),
									classSetting.getEndTime(),leaveType,dataReason,billId);
						}
					}
				}
			}

			if (length > 1 && endData.equals(endTime1)) {
				if(RunTask.RUN_CODE_60.equals(runCode)&&("11".equals(leaveType)||"1".equals(leaveType)||"2".equals(leaveType)
						||"5".equals(leaveType)||"12".equals(leaveType))){
					EmployeeClass classSetting = (EmployeeClass) resultMap.get(endData);
					Boolean isCommon = false; 
					if(classSetting != null){
						isCommon = true;
					}
					if(isCommon) {
						double endLeaveDays = 0;
						boolean standard = ("09:00".equals(DateUtils.format(classSetting.getStartTime(), "HH:mm"))&&"18:00".equals(DateUtils.format(classSetting.getEndTime(), "HH:mm")))?true:false;
						boolean standard1 = (classSetting.getMustAttnTime()==8)?true:false;
						if(standard1&&!standard){
							if(Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getEndTime(),-300), "HH")).intValue()){
								classSetting.setEndTime(DateUtils.addMinute(classSetting.getEndTime(),-240));
							}
						 }else{
							 classSetting.setEndTime(DateUtils.parse(endTime1+" "+endTime2+":00:00", DateUtils.FORMAT_LONG));
						 }
						this.calWorkHours(employee.getCompanyId(),
								employee.getId(), updateUser,DateUtils.parse(endData, DateUtils.FORMAT_SHORT),
								endLeaveDays, runCode,classSetting.getStartTime(),classSetting.getEndTime(),leaveType,dataReason,billId);
					} 
				}else{
					EmployeeClass classSetting = (EmployeeClass) resultMap.get(endData);
					Boolean isCommon = false;
			        if (classSetting != null) {
						isCommon = true;
					}
					if(isCommon){
						//哺乳假
						if("4".equals(leaveType)){
							if (leaveHours != null) {
								int preHours = (int) (leaveHours*60);
								if(dayType == 100){
									this.calWorkHours(employee.getCompanyId(),
											employee.getId(), updateUser,DateUtils.parse(endTime1, DateUtils.FORMAT_SHORT),
											leaveHours, runCode,DateUtils.parse(endTime1+" "+DateUtils.format(classSetting.getStartTime(), "HH:mm:ss"), DateUtils.FORMAT_LONG),
											DateUtils.parse(endTime1+" "+DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), preHours), "HH:mm:ss"), DateUtils.FORMAT_LONG),leaveType,dataReason,billId);
								}else if(dayType == 200){
									this.calWorkHours(employee.getCompanyId(),
											employee.getId(), updateUser,DateUtils.parse(endTime1, DateUtils.FORMAT_SHORT),
											leaveHours, runCode,DateUtils.parse(endTime1+" "+DateUtils.format(DateUtils.addMinute(classSetting.getEndTime(), -preHours), "HH:mm:ss"), DateUtils.FORMAT_LONG),
											DateUtils.parse(endTime1+" "+DateUtils.format(classSetting.getEndTime(), "HH:mm:ss"), DateUtils.FORMAT_LONG),leaveType,dataReason,billId);
								}
							}
						}else{//除年假，病假，调休，其它，事假，哺乳假 外的所有类型假期
							this.calWorkHours(employee.getCompanyId(),
									employee.getId(), updateUser,DateUtils.parse(endData, DateUtils.FORMAT_SHORT),
									8d, runCode,classSetting.getStartTime(),classSetting.getEndTime(),leaveType,dataReason,billId);
						}
					}
				}
			}
			for (Map.Entry<String, Object> entry : resultMap.entrySet()) {
				if (!entry.getKey().equals("beginData")
						&& !entry.getKey().equals("endData")&&!entry.getKey().equals(startTime1)&&!entry.getKey().equals(endTime1)) {
					EmployeeClass result_e = (EmployeeClass) resultMap
							.get(entry.getKey());
					if (result_e != null) {
						if (result_e.getClassDate() != null) {
							String reDate = DateUtils.format(result_e.getClassDate(), DateUtils.FORMAT_SHORT);
							if(!reDate.equals(startTime1) && !reDate.equals(endTime1)) {
								timeMap.put(DateUtils.format(
										result_e.getClassDate(),
										DateUtils.FORMAT_SHORT), result_e
										.getClassSettingId());
								Double mustAttnTime = result_e.getMustAttnTime();
								if (leaveHours != null) {//哺乳假
									mustAttnTime = leaveHours;
									if(dayType == 100){
										result_e.setEndTime(DateUtils.addMinute(result_e.getStartTime(),((int)leaveHours.doubleValue())*60));
									}else if(dayType == 200){
										result_e.setStartTime(DateUtils.addMinute(result_e.getEndTime(),(-(int)leaveHours.doubleValue())*60));
									}
									this.calWorkHours(employee.getCompanyId(),
											employee.getId(), updateUser,result_e.getClassDate(),
											mustAttnTime, runCode,result_e.getStartTime(),result_e.getEndTime(),leaveType,dataReason,billId);
								}else{
									this.calWorkHours(employee.getCompanyId(),
											employee.getId(), updateUser,result_e.getClassDate(),
											mustAttnTime, runCode,result_e.getStartTime(),result_e.getEndTime(),leaveType,dataReason,billId);
								}
								
							}
						}
					}
				}
			}
		} catch (Exception e) {
		}
	}

}
