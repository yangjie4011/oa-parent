package com.ule.oa.base.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ule.oa.base.common.OaCommon;
import com.ule.oa.base.mapper.EmpApplicationLeaveDetailMapper;
import com.ule.oa.base.mapper.EmpApplicationLeaveMapper;
import com.ule.oa.base.mapper.EmpApplicationOvertimeMapper;
import com.ule.oa.base.mapper.EmpLeaveMapper;
import com.ule.oa.base.mapper.EmployeeMapper;
import com.ule.oa.base.mapper.LeaveRecordDetailMapper;
import com.ule.oa.base.mapper.LeaveRecordMapper;
import com.ule.oa.base.po.ClassSetting;
import com.ule.oa.base.po.EmpApplicationLeave;
import com.ule.oa.base.po.EmpApplicationLeaveDetail;
import com.ule.oa.base.po.EmpApplicationOvertime;
import com.ule.oa.base.po.EmpLeave;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.po.LeaveRecord;
import com.ule.oa.base.po.LeaveRecordDetail;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.AnnualVacationService;
import com.ule.oa.base.service.ClassSettingService;
import com.ule.oa.base.service.EmployeeClassService;
import com.ule.oa.base.service.LeaveRecordService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;

@Service
public class LeaveRecordServiceImpl implements LeaveRecordService {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private EmpLeaveMapper empLeaveMapper;
	@Autowired
	private LeaveRecordMapper leaveRecordMapper;
	@Autowired
	private LeaveRecordDetailMapper leaveRecordDetailMapper;
	@Autowired
	private AnnualVacationService annualVacationService;
	@Autowired
	private EmployeeMapper employeeMapper;
	@Autowired
	private EmployeeClassService employeeClassService;
	@Autowired
	private ClassSettingService classSettingService;
	@Autowired
	private UserService userService;

	@Override
	public Map<String, Object> getUsedLeaveListByEmployeeId(Long employeeId) {
		//当年
		int currentYear = Integer.valueOf(DateUtils.getYear(new Date()));
		int lastYear = currentYear -1;
		
		/**判断是否为4月第6个工作日**/
		Date theSixthWorkDay = annualVacationService.getWorkingDayOfMonth(DateUtils.parse(String.valueOf(currentYear)+"-04-01",DateUtils.FORMAT_SHORT), 6);
		Date thisDay = DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_SHORT), DateUtils.FORMAT_SHORT);
		
		Map<String,Object> data = new HashMap<String,Object>();
		//当年假期总数
		data.put("employeeId", employeeId);//员工ID
		data.put("yearDays", 0d);//今年年假总数
		data.put("legalYearDays", 0d);//今年法定年假总数
		data.put("welfareYearDays", 0d);//今年福利年假总数
		data.put("paidSickDays", 0d);//今年带薪病假总数
		//剩余假期天数
		data.put("lastYearDaysRemain", 0d);//去年年假剩余
		data.put("lastLegalYearDaysRemain", 0d);//去年法定年假剩余
		data.put("lastWelfareYearDaysRemain", 0d);//去年福利年假剩余
		data.put("legalYearDaysRemain", 0d);//当年年法定年假剩余
		data.put("welfareYearDaysRemain", 0d);//当年福利年假剩余
		data.put("yearDaysRemain", 0d);//年假剩余
		data.put("paidSickDaysRemain", 0d);//带薪病假剩余
		data.put("offDaysRemain", 0d);//当年调休剩余
		data.put("lastOffDaysRemain", 0d);//去年调休剩余
		//截止目前剩余假期天数
		data.put("yearDaysSofar", 0d);//当年年假
		data.put("legalYearDaysSofar", 0d);//当年年法定年假
		data.put("welfareYearDaysSofar", 0d);//当年福利年假
		data.put("paidSickDaysSofar", 0d);//带薪病假
		//当年已使用假期天数
		data.put("yearDaysUsed", 0d);//年假当年已使用
		data.put("legalYearDaysUsed", 0d);//法定年假当年已使用
		data.put("welfareYearDaysUsed", 0d);//福利年假当年已使用
		data.put("paidSickDaysUsed", 0d);//带薪病假当年已使用
		data.put("unPaidSickDaysUsed", 0d);//非带薪病假当年已使用
		data.put("affairsUsed", 0d);//事假当年已使用
		data.put("offUsed", 0d);//调休小时数当年已使用
		data.put("lastOffUsed", 0d);//调休小时数去年已使用
		data.put("maritalUsed", 0d);//婚假当年已使用
		data.put("funeralUsed", 0d);//丧假当年已使用
		data.put("paternityUsed", 0d);//陪产假当年已使用
		data.put("prenatalUsed", 0d);//产前假当年已使用
		data.put("maternityUsed", 0d);//产假当年已使用
		data.put("lactationUsed", 0d);//哺乳假当年已使用
		data.put("abortionUsed", 0d);//流产假当年已使用
		data.put("otherUsed", 0d);//其他当年已使用
		//透支假期天数
		data.put("yearOverUsed", 0d);//年假
		data.put("paidSickOverUsed", 0d);//带薪
		//去年年假已用
		Double lastYearDaysUsed = 0d;//去年年假已用
		Double lastLegalYearDaysUsed = 0d;//去年法定年假已用
		Double lastWelfareYearDaysUsed = 0d;//去年福利年假已用
		//去年年假
		Double lastYearDays = 0d;//去年年假总数
		Double lastLegalYearDays = 0d;//去年法定年假总数
		Double lastWelfareYearDays = 0d;//去年福利年假总数
		//调休总数
		Double offDays = 0d;//今年调休总数
		Double lastOffDays = 0d;//去年调休总数
		//截止目前实际假期天数
		Double yearActualDays = 0d;//当年年假实际天数
		Double legalYearActualDays = 0d;//当年法定年假实际天数
		Double welfareYearActualDays = 0d;//当年福利年假实际天数
		Double paidSickActualDays = 0d;//当年带薪病假实际天数
		//假期有效期
		data.put("yearValidity", "");//当年年假有效期
		data.put("lastYearValidity", "");//去年年假有效期
		data.put("paidSickValidity", "");//带薪病假有效期
		data.put("offValidity", "");//当年调休有效期
		data.put("lastOffValidity", "");//去年调休有效期
		
		//使用和新增流水
		List<LeaveRecord> list = leaveRecordMapper.getUsedLeaveListByEmployeeId(employeeId);
		for(LeaveRecord record:list){
			int type = record.getType().intValue();
			//年假-1,病假-2,婚假-3,哺乳假-4,调休-5,产前假-6,产假-7,流产假-8,陪产假-9,丧假-10,事假-11,其他-12
			switch(type){
			   case 1://年假
				   //当年
				   if(currentYear==record.getYear().intValue()){
					   data.put("yearDaysUsed",Double.valueOf(String.valueOf(data.get("yearDaysUsed")))+record.getDays());//年假
					   if(record.getCategory().intValue()==1){//法定
						   data.put("legalYearDaysUsed", Double.valueOf(String.valueOf(data.get("legalYearDaysUsed")))+record.getDays());//法定年假
					   }
                       if(record.getCategory().intValue()==2){//福利
                    	   data.put("welfareYearDaysUsed", Double.valueOf(String.valueOf(data.get("welfareYearDaysUsed")))+record.getDays());//福利年假
					   }
				   }
				   //去年
				   if(lastYear==record.getYear().intValue()){
					   //去年年假已用
					   lastYearDaysUsed = lastYearDaysUsed + record.getDays();
					   if(record.getCategory().intValue()==1){
						   //去年法定年假已用
						   lastLegalYearDaysUsed = lastLegalYearDaysUsed + record.getDays();
					   }
                       if(record.getCategory().intValue()==2){
                    	   //去年福利年假已用
                    	   lastWelfareYearDaysUsed = lastWelfareYearDaysUsed + record.getDays();
					   }
				   }
				   break;
			   case 2://病假
				   //当年
				   if(currentYear==record.getYear().intValue()){
					   if(record.getCategory().intValue()==0){//带薪
						   data.put("paidSickDaysUsed",Double.valueOf(String.valueOf(data.get("paidSickDaysUsed")))+record.getDays());//带薪病假
					   }
					   if(record.getCategory().intValue()==2){//非带薪
						   data.put("unPaidSickDaysUsed",Double.valueOf(String.valueOf(data.get("unPaidSickDaysUsed")))+record.getDays());//带薪病假
					   }
				   }
				   break;
			   case 3://婚假
				   //当年
				   if(currentYear==record.getYear().intValue()){
					   data.put("maritalUsed", Double.valueOf(String.valueOf(data.get("maritalUsed")))+record.getDays());
				   }
				   break;
			   case 4://哺乳假
				   //当年
				   if(currentYear==record.getYear().intValue()){
					   data.put("lactationUsed", Double.valueOf(String.valueOf(data.get("lactationUsed")))+record.getDays());
				   }
				   break;
			   case 5://调休
				   //调休已用
				   if(ConfigConstants.LEAVE_KEY.equals(record.getBillType())){
					   //根据parentId获取总假期的年份
					   EmpLeave parentLeave = empLeaveMapper.getById(record.getParendId());
					   if(parentLeave!=null&&parentLeave.getYear().intValue()==currentYear){
						   //当年已用调休
						   data.put("offUsed", Double.valueOf(String.valueOf(data.get("offUsed")))+record.getDays());
					   }
					   if(parentLeave!=null&&parentLeave.getYear().intValue()==lastYear){
						   //去年已用调休
						   data.put("lastOffUsed", Double.valueOf(String.valueOf(data.get("lastOffUsed")))+record.getDays());
					   }
				   }
				   //调休总数
				   if(ConfigConstants.OVERTIME_KEY.equals(record.getBillType())){
					   //根据parentId获取总假期的年份
					   EmpLeave parentLeave = empLeaveMapper.getById(record.getParendId());
					   if(parentLeave!=null&&parentLeave.getYear().intValue()==currentYear){
						   //今年调休总数
						   offDays = offDays + record.getDays();
					   }
					   if(parentLeave!=null&&parentLeave.getYear().intValue()==lastYear){
						   //去年调休总数
						   lastOffDays = lastOffDays + record.getDays();
					   }
				   }
				   break;
			   case 6://产前假
				   //当年
				   if(currentYear==record.getYear().intValue()){
					   data.put("prenatalUsed", Double.valueOf(String.valueOf(data.get("prenatalUsed")))+record.getDays());
				   }
				   break;
			   case 7://产假
				   //当年
				   if(currentYear==record.getYear().intValue()){
					   data.put("maternityUsed", Double.valueOf(String.valueOf(data.get("maternityUsed")))+record.getDays());//产假
				   }
				   break;
			   case 8://流产假
				   if(currentYear==record.getYear().intValue()){
					   data.put("abortionUsed", Double.valueOf(String.valueOf(data.get("abortionUsed")))+record.getDays());//流产假
				   }
				   break;
			   case 9://陪产假
				   if(currentYear==record.getYear().intValue()){
					   data.put("paternityUsed", Double.valueOf(String.valueOf(data.get("paternityUsed")))+record.getDays());//陪产假
				   }
				   break;
			   case 10://丧假
				   if(currentYear==record.getYear().intValue()){
					   data.put("funeralUsed", Double.valueOf(String.valueOf(data.get("funeralUsed")))+record.getDays());//丧假
				   }
				   break;
			   case 11://事假
				   if(currentYear==record.getYear().intValue()){
					   data.put("affairsUsed", Double.valueOf(String.valueOf(data.get("affairsUsed")))+record.getDays());//事假
				   }
				   break;
			   case 12://其他
				   if(currentYear==record.getYear().intValue()){
					   data.put("otherUsed", Double.valueOf(String.valueOf(data.get("otherUsed")))+record.getDays());//其他
				   }
				   break;
			   default:
				   break;
			}
		}
		
		//年假假期基数
		EmpLeave yearListP = new EmpLeave();
		yearListP.setEmployeeId(employeeId);
		yearListP.setType(ConfigConstants.LEAVE_TYPE_1);
		List<Integer> yearList = new ArrayList<Integer>();
		yearList.add(currentYear);
		yearList.add(lastYear);
		yearListP.setYearList(yearList);
		List<EmpLeave> yearLeaveList = empLeaveMapper.getListByCondition(yearListP);
		
		for(EmpLeave leave : yearLeaveList){
			//今年
			if(leave.getYear().intValue()==currentYear&&leave.getCategory().intValue()==0){
				//今年年假总数
				data.put("yearDays", Double.valueOf(String.valueOf(data.get("yearDays")))+leave.getAllowDays());
				//当年年假实际天数
				yearActualDays = yearActualDays + leave.getActualDays();
				data.put("yearValidity", DateUtils.format(leave.getStartTime(), DateUtils.FORMAT_SHORT)+"至"+DateUtils.format(leave.getEndTime(), DateUtils.FORMAT_SHORT));//当年年假有效期
				
			}
			if(leave.getYear().intValue()==currentYear&&leave.getCategory().intValue()==1){
				//今年法定年假总数
				data.put("legalYearDays", Double.valueOf(String.valueOf(data.get("legalYearDays")))+leave.getAllowDays());
				//当年法定年假实际天数
				legalYearActualDays = legalYearActualDays + leave.getActualDays();
			}
            if(leave.getYear().intValue()==currentYear&&leave.getCategory().intValue()==2){
            	//今年福利年假总数
            	data.put("welfareYearDays", Double.valueOf(String.valueOf(data.get("welfareYearDays")))+leave.getAllowDays());
            	//当年福利年假实际天数
            	welfareYearActualDays = welfareYearActualDays + leave.getActualDays();
			}
            //去年
            if(leave.getYear().intValue()==lastYear&&leave.getCategory().intValue()==0){
				//去年年假总数
            	lastYearDays = lastYearDays + leave.getAllowDays();
            	data.put("lastYearValidity", DateUtils.format(leave.getStartTime(), DateUtils.FORMAT_SHORT)+"至"+DateUtils.format(leave.getEndTime(), DateUtils.FORMAT_SHORT));//去年年假有效期
			
			}
			if(leave.getYear().intValue()==lastYear&&leave.getCategory().intValue()==1){
				//去年法定年假总数
				lastLegalYearDays = lastLegalYearDays + leave.getAllowDays();
			}
            if(leave.getYear().intValue()==lastYear&&leave.getCategory().intValue()==2){
            	//去年福利年假总数
            	lastWelfareYearDays = lastWelfareYearDays + leave.getAllowDays();
			}
		}
		
		//当年年假
		data.put("yearDaysRemain", Double.valueOf(String.valueOf(data.get("yearDays")))-Double.valueOf(String.valueOf(data.get("yearDaysUsed"))));
		//当年法定年假剩余
		data.put("legalYearDaysRemain", Double.valueOf(String.valueOf(data.get("legalYearDays")))-Double.valueOf(String.valueOf(data.get("legalYearDaysUsed"))));
		//当年福利剩余
		data.put("welfareYearDaysRemain", Double.valueOf(String.valueOf(data.get("welfareYearDays")))-Double.valueOf(String.valueOf(data.get("welfareYearDaysUsed"))));
	    if(thisDay.before(theSixthWorkDay)){
	    	//去年年假剩余
	    	data.put("lastYearDaysRemain", lastYearDays-lastYearDaysUsed);
	    	//去年法定年假剩余
			data.put("lastLegalYearDaysRemain", lastLegalYearDays-lastLegalYearDaysUsed);
			//去年福利年假剩余
			data.put("lastWelfareYearDaysRemain", lastWelfareYearDays-lastWelfareYearDaysUsed);
		}
		//带薪病假假期基数
		EmpLeave sickListP = new EmpLeave();
		sickListP.setEmployeeId(employeeId);
		sickListP.setType(ConfigConstants.LEAVE_TYPE_2);
		List<EmpLeave> sickLeaveList = empLeaveMapper.getListByCondition(sickListP);
		for(EmpLeave leave : sickLeaveList){
			if(leave.getCategory().intValue()==0){
				//今年带薪病假总数
				data.put("paidSickDays", Double.valueOf(String.valueOf(data.get("paidSickDays")))+leave.getAllowDays());
				//当年带薪病假实际天数
				paidSickActualDays = paidSickActualDays + leave.getActualDays();
				data.put("paidSickValidity", DateUtils.format(leave.getStartTime(), DateUtils.FORMAT_SHORT)+"至"+DateUtils.format(leave.getEndTime(), DateUtils.FORMAT_SHORT));//带薪病假有效期
				
			}
		}
		//带薪病假剩余
		Double paidSickDaysRemain = (Double.valueOf(String.valueOf(data.get("paidSickDays")))-Double.valueOf(String.valueOf(data.get("paidSickDaysUsed"))))>0?Double.valueOf(String.valueOf(data.get("paidSickDays")))-Double.valueOf(String.valueOf(data.get("paidSickDaysUsed"))):0;
		data.put("paidSickDaysRemain", paidSickDaysRemain);
		//调休剩余
		data.put("offDaysRemain", offDays-Double.valueOf(String.valueOf(data.get("offUsed"))));//当年调休剩余
		if(thisDay.before(theSixthWorkDay)){
			//去年调休剩余
			data.put("lastOffDaysRemain", lastOffDays - Double.valueOf(String.valueOf(data.get("lastOffUsed"))));
		}
		//截止目前剩余假期天数
		Double yearDaysSofar = (yearActualDays-Double.valueOf(String.valueOf(data.get("yearDaysUsed"))))>0?yearActualDays-Double.valueOf(String.valueOf(data.get("yearDaysUsed"))):0d;
		data.put("yearDaysSofar", yearDaysSofar);//当年年假截止目前剩余假期天数
		Double legalYearDaysSofar = (legalYearActualDays-Double.valueOf(String.valueOf(data.get("legalYearDaysUsed"))))>0?legalYearActualDays-Double.valueOf(String.valueOf(data.get("legalYearDaysUsed"))):0d;
		data.put("legalYearDaysSofar", legalYearDaysSofar);//当年年法定年假截止目前剩余假期天数
		Double welfareYearDaysSofar = (welfareYearActualDays-Double.valueOf(String.valueOf(data.get("welfareYearDaysUsed"))))>0?welfareYearActualDays-Double.valueOf(String.valueOf(data.get("welfareYearDaysUsed"))):0d;
		data.put("welfareYearDaysSofar", welfareYearDaysSofar);//当年福利年假截止目前剩余假期天数
		Double paidSickDaysSofar = (paidSickActualDays-Double.valueOf(String.valueOf(data.get("paidSickDaysUsed"))))>0?paidSickActualDays-Double.valueOf(String.valueOf(data.get("paidSickDaysUsed"))):0d;
		data.put("paidSickDaysSofar", paidSickDaysSofar);//带薪病假截止目前剩余假期天数
		//透支假期天数
		Double yearOverUsed = (Double.valueOf(String.valueOf(data.get("yearDaysUsed")))-yearActualDays)>0?Double.valueOf(String.valueOf(data.get("yearDaysUsed")))-yearActualDays:0d;
		data.put("yearOverUsed", yearOverUsed);//年假
		Double paidSickOverUsed = (Double.valueOf(String.valueOf(data.get("paidSickDaysUsed")))-paidSickActualDays)>0?Double.valueOf(String.valueOf(data.get("paidSickDaysUsed")))-paidSickActualDays:0d;
		data.put("paidSickOverUsed", paidSickOverUsed);//带薪
		
		//调休的有效期
		EmpLeave offListP = new EmpLeave();
		offListP.setEmployeeId(employeeId);
		offListP.setType(ConfigConstants.LEAVE_TYPE_5);
		offListP.setYearList(yearList);
		offListP.setCategory(0);
		List<EmpLeave> offLeaveList = empLeaveMapper.getListByCondition(offListP);
		for(EmpLeave leave : offLeaveList){
			if(leave.getCategory().intValue()==0&&leave.getYear().intValue()==currentYear){
				data.put("offValidity", DateUtils.format(leave.getStartTime(), DateUtils.FORMAT_SHORT)+"至"+DateUtils.format(leave.getEndTime(), DateUtils.FORMAT_SHORT));//当年调休有效期
			}
			if(leave.getCategory().intValue()==0&&leave.getYear().intValue()==lastYear){
				data.put("lastOffValidity", DateUtils.format(leave.getStartTime(), DateUtils.FORMAT_SHORT)+"至"+DateUtils.format(leave.getEndTime(), DateUtils.FORMAT_SHORT));//去年调休有效期
			}
		}
		
		return data;
	}
	
	public Map<String, Object> calculatedLeaveDays(Long empId,String startTime1,String startTime2,String endTime1,String endTime2,String leaveType){
		Employee emp = employeeMapper.getById(empId);
		
		Map<Long,ClassSetting> csMap = new HashMap<Long, ClassSetting>();
		List<ClassSetting> csList = classSettingService.getList();
		for (ClassSetting classSetting : csList) {
			csMap.put(classSetting.getId(), classSetting);
		}
		Map<String,Object> map = new HashMap<String, Object>();
		//开始时间
		Date dayStart = DateUtils.parse(startTime1,DateUtils.FORMAT_SHORT);
		//结束时间
		Date dayEnd = DateUtils.parse(endTime1,DateUtils.FORMAT_SHORT);
		String beginData = "";
		String endData = "";
		int length = DateUtils.getIntervalDays(dayStart, dayEnd) + 1;
		if(OaCommon.getLeaveMap().containsKey(leaveType)) {
			beginData = startTime1;
			endData = endTime1;
		} 
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try{
			if(StringUtils.isBlank(startTime1)||StringUtils.isBlank(startTime2)||
					StringUtils.isBlank(endTime1)||StringUtils.isBlank(endTime2)){
				map.put("message","请假时间不能为空");
				map.put("success",false);
				return map;
			}
			double leaveDays = 0;
			double leaveHours = 0;
			
			boolean isEndTime = true;
			List<String> datas = new ArrayList<String>();
			do{
				datas.add(DateUtils.format(dayStart, DateUtils.FORMAT_SHORT));
				dayStart = DateUtils.addDay(dayStart, 1);
				if(DateUtils.getIntervalDays(dayStart, dayEnd) < 0) {
					isEndTime = false;
				}
			} while(isEndTime);
			if(OaCommon.getLeaveMap().containsKey(leaveType)) {
				leaveDays = length;
			}else{
				resultMap = employeeClassService.getEmployeeClassSetting(emp,datas);
				beginData = (String) resultMap.get("beginData");
				endData = (String) resultMap.get("endData");
				for (Map.Entry<String,Object> entry : resultMap.entrySet()) {
					if(!entry.getKey().equals("beginData") && !entry.getKey().equals("endData")&&!entry.getKey().equals(startTime1)&&!entry.getKey().equals(endTime1)) {
						EmployeeClass result_e = (EmployeeClass) resultMap.get(entry.getKey());
						if(result_e != null) {
							if(result_e.getClassDate() != null) {
								boolean standard = ("09:00".equals(DateUtils.format(result_e.getStartTime(), "HH:mm"))&&"18:00".equals(DateUtils.format(result_e.getEndTime(), "HH:mm")))?true:false;
								boolean standard1 = (result_e.getMustAttnTime()==8)?true:false;
								boolean standard2 = (result_e.getMustAttnTime()>=16)?true:false;
								boolean standard3 = (result_e.getMustAttnTime()>=10&&result_e.getMustAttnTime()<16)?true:false;
								if(standard){
									if("5".equals(leaveType)){
										leaveHours = leaveHours + 8;
									}
									leaveDays = leaveDays + 1;
								}else if(standard1){
									if("5".equals(leaveType)){
										leaveHours = leaveHours + 8;
									}
									leaveDays = leaveDays + 1;
								}else if(standard2){
									if("5".equals(leaveType)){
										leaveHours = leaveHours + 16;
									}
									leaveDays = leaveDays + 2;
								}else if(standard3){
									if("5".equals(leaveType)){
										leaveHours = leaveHours + 12;
									}
									leaveDays = leaveDays + 1.5;
								}
							}
						}
					}
				}
			}
			
			if(beginData.equals(startTime1)) {
				EmployeeClass classSetting = (EmployeeClass) resultMap.get(beginData);
				Boolean isCommon = false;
			    if(classSetting != null){
					isCommon = true;
				}
				if(isCommon) {
					boolean standard = ("09:00".equals(DateUtils.format(classSetting.getStartTime(), "HH:mm"))&&"18:00".equals(DateUtils.format(classSetting.getEndTime(), "HH:mm")))?true:false;
					boolean standard1 = (classSetting.getMustAttnTime()==8)?true:false;
					boolean standard2 = (classSetting.getMustAttnTime()>=16)?true:false;
					boolean standard3 = (classSetting.getMustAttnTime()>=10&&classSetting.getMustAttnTime()<16)?true:false;
					if(standard){
						if(length == 1){
							if((Integer.valueOf(startTime2).intValue()==9&&Integer.valueOf(endTime2).intValue()==14)||(Integer.valueOf(startTime2).intValue()==9&&Integer.valueOf(endTime2).intValue()==13)){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 3;
									leaveDays = leaveDays + 0.5;
								}else{
									leaveDays = leaveDays + 0.5;
								}
							}else if(Integer.valueOf(startTime2).intValue()==12&&Integer.valueOf(endTime2).intValue()==18){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 5;
								}
								leaveDays = leaveDays + 0.5;
							}else if(Integer.valueOf(startTime2).intValue()==9&&Integer.valueOf(endTime2).intValue()==18){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 8;
								}
								leaveDays = leaveDays + 1;
							}
						}else{
							if(Integer.valueOf(startTime2).intValue()==9){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 8;
								}
								leaveDays = leaveDays + 1;
							}else if(Integer.valueOf(startTime2).intValue()==12){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 5;
								}
								leaveDays = leaveDays + 0.5;
							}
						}
					}else if(standard1){
						if(length == 1){
							if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getStartTime(), "HH")).intValue()
									&& Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getEndTime(),-300), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 4;
								}
								leaveDays = leaveDays + 0.5;
							}else if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 300), "HH")).intValue()
									&& Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 4;
								}
								leaveDays = leaveDays + 0.5;
							}else if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getStartTime(), "HH")).intValue()
									&& Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 8;
								}
								leaveDays = leaveDays + 1;
							}
						}else{
							if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getStartTime(), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 8;
								}
								leaveDays = leaveDays + 1;
							}else if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 300), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 4;
								}
								leaveDays = leaveDays + 0.5;
							}
						}
					}else if(standard2){
						if(length == 1){
							if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getStartTime(), "HH")).intValue()
									&& Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 16;
								}
								leaveDays = leaveDays + 2;
							}else if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(),240), "HH")).intValue()
									&& Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 12;
								}
								leaveDays = leaveDays + 1.5;
							}else if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 480), "HH")).intValue()
									&& Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 8;
								}
								leaveDays = leaveDays + 1;
							}else if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 720), "HH")).intValue()
									&& Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 4;
								}
								leaveDays = leaveDays + 0.5;
							}
						}else{
							if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getStartTime(), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 16;
								}
								leaveDays = leaveDays + 2;
							}else if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(),240), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 12;
								}
								leaveDays = leaveDays + 1.5;
							}else if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 480), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 8;
								}
								leaveDays = leaveDays + 1;
							}else if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 720), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 4;
								}
								leaveDays = leaveDays + 0.5;
							}
						}
					}else if(standard3){
						if(length == 1){
							if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getStartTime(), "HH")).intValue()
									&& Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 12;
								}
								leaveDays = leaveDays + 1.5;
							}else if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 240), "HH")).intValue()
									&& Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 8;
								}
								leaveDays = leaveDays + 1;
							}else if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 480), "HH")).intValue()
									&& Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 4;
								}
								leaveDays = leaveDays + 0.5;
							}
						}else{
							if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getStartTime(), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 12;
								}
								leaveDays = leaveDays + 1.5;
							}else if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 240), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 8;
								}
								leaveDays = leaveDays + 1;
							}else if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 480), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 4;
								}
								leaveDays = leaveDays + 0.5;
							}
						}
					}
				}
			}
			
			if(length >1 && endData.equals(endTime1)) {
				EmployeeClass classSetting = (EmployeeClass) resultMap.get(endData);
				Boolean isCommon = false; 
				if(classSetting != null){
					isCommon = true;
				}
				if(isCommon) {
					boolean standard = ("09:00".equals(DateUtils.format(classSetting.getStartTime(), "HH:mm"))&&"18:00".equals(DateUtils.format(classSetting.getEndTime(), "HH:mm")))?true:false;
					boolean standard1 = (classSetting.getMustAttnTime()==8)?true:false;
					boolean standard2 = (classSetting.getMustAttnTime()>=16)?true:false;
					boolean standard3 = (classSetting.getMustAttnTime()>=10&&classSetting.getMustAttnTime()<16)?true:false;
					if(standard){
						if(Integer.valueOf(endTime2).intValue()==18){
							if("5".equals(leaveType)){
								leaveHours = leaveHours + 8;
							}
							leaveDays = leaveDays + 1;
						}else if(Integer.valueOf(endTime2).intValue()==14||Integer.valueOf(endTime2).intValue()==13){
							if("5".equals(leaveType)){
								leaveHours = leaveHours + 3;
								leaveDays = leaveDays + 0.5;
							}else{
								leaveDays = leaveDays + 0.5;
							}
						}
					}else if(standard1){
						if(Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()){
							if("5".equals(leaveType)){
								leaveHours = leaveHours + 8;
							}
							leaveDays = leaveDays + 1;
						}else if(Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getEndTime(), -300), "HH")).intValue()){
							if("5".equals(leaveType)){
								leaveHours = leaveHours + 4;
							}
							leaveDays = leaveDays + 0.5;
						}
					}else if(standard2){
						if(Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()){
							if("5".equals(leaveType)){
								leaveHours = leaveHours + 16;
							}
							leaveDays = leaveDays + 2;
						}
					}else if(standard3){
						if(Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()){
							if("5".equals(leaveType)){
								leaveHours = leaveHours + 12;
							}
							leaveDays = leaveDays + 1.5;
						}
					}
				} 
			}
			map.put("leaveDays",leaveDays);
			map.put("leaveHours",leaveHours);
			map.put("success", true);
		}catch(Exception e){
			logger.error(emp.getCnName()+"请假-计算假期："+e.toString());
			map.put("success",false);
		}
		return map;
	}
	
	/**
	 * 初始化所有假期流水
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void initLeaveRecord() {
		//查出所有被删除的流水id，用来删除流水详情表中的数据
		List<Long> leaveRecordIds = leaveRecordMapper.getIdByBillType("leave");
		//删除所有单据类型为假期的流水
		leaveRecordMapper.deleteByBillType("leave"); 
		//删除假期详情
		if(leaveRecordIds.size() > 0){
			leaveRecordMapper.deleteDetailByLeaveRecordId(leaveRecordIds);
		}
		
		leaveRecordIds = leaveRecordMapper.getIdByBillType("cancelLeave");
		//删除所有单据类型为假期的流水
		leaveRecordMapper.deleteByBillType("cancelLeave"); 
		//删除假期详情
		if(leaveRecordIds.size() > 0){
			leaveRecordMapper.deleteDetailByLeaveRecordId(leaveRecordIds);
		}
		
		//生成流水时，年假只生成福利和法定的流水，调休只生成整年的调休流水，不生成月份的流水
		//生成年假流水
		//1.查出所有已用大于0的法定和福利年假信息
		List<EmpLeave> yearLeaves = empLeaveMapper.getYearLeaveList();
		for (EmpLeave empLeave : yearLeaves) {
			LeaveRecord leaveRecord = new LeaveRecord();
			leaveRecord.setEmployeeId(empLeave.getEmployeeId());
			leaveRecord.setType(ConfigConstants.LEAVE_TYPE_1);
			leaveRecord.setDays(empLeave.getUsedDays());
			leaveRecord.setDaysUnit(0);
			leaveRecord.setBillId(0L);
			leaveRecord.setBillType(ConfigConstants.LEAVE_KEY);
			leaveRecord.setUpdateType(0);
			leaveRecord.setCreateTime(new Date());
			leaveRecord.setCreateUser("systemInit");
			leaveRecord.setDelFlag(0);
			leaveRecord.setSource(0);
			//2.保存流水
			leaveRecordMapper.save(leaveRecord);
			//3.保存流水详情
			LeaveRecordDetail leaveRecordDetail = new LeaveRecordDetail();
			leaveRecordDetail.setLeaveRecordId(leaveRecord.getId());
			leaveRecordDetail.setBaseEmpLeaveId(empLeave.getId());
			leaveRecordDetail.setDays(empLeave.getUsedDays());
			leaveRecordDetail.setCreateTime(new Date());
			leaveRecordDetail.setCreateUser("systemInit");
			leaveRecordDetail.setDelFlag(0);
			leaveRecordDetailMapper.save(leaveRecordDetail);
		}
		//生成调休流水.
		//1.查出所有已用大于0的调休信息
		List<EmpLeave> offLeaves = empLeaveMapper.getOffLeaveList();
		for (EmpLeave empLeave : offLeaves) {
			LeaveRecord leaveRecord = new LeaveRecord();
			leaveRecord.setEmployeeId(empLeave.getEmployeeId());
			leaveRecord.setType(ConfigConstants.LEAVE_TYPE_5);
			leaveRecord.setDays(empLeave.getUsedDays());
			leaveRecord.setDaysUnit(1);
			leaveRecord.setBillId(0L);
			leaveRecord.setBillType(ConfigConstants.LEAVE_KEY);
			leaveRecord.setUpdateType(0);
			leaveRecord.setCreateTime(new Date());
			leaveRecord.setCreateUser("systemInit");
			leaveRecord.setDelFlag(0);
			leaveRecord.setSource(0);
			//2.保存流水
			leaveRecordMapper.save(leaveRecord);
			//3.保存流水详情
			LeaveRecordDetail leaveRecordDetail = new LeaveRecordDetail();
			leaveRecordDetail.setLeaveRecordId(leaveRecord.getId());
			leaveRecordDetail.setBaseEmpLeaveId(empLeave.getId());
			leaveRecordDetail.setDays(empLeave.getUsedDays());
			leaveRecordDetail.setCreateTime(new Date());
			leaveRecordDetail.setCreateUser("systemInit");
			leaveRecordDetail.setDelFlag(0);
			leaveRecordDetailMapper.save(leaveRecordDetail);
		}
		//生成已用大于0的其他假期的流水
		List<EmpLeave> otherLeaves = empLeaveMapper.getOtherLeaveList();
		for (EmpLeave empLeave : otherLeaves) {
			LeaveRecord leaveRecord = new LeaveRecord();
			leaveRecord.setEmployeeId(empLeave.getEmployeeId());
			leaveRecord.setType(empLeave.getType());
			leaveRecord.setDays(empLeave.getUsedDays());
			leaveRecord.setDaysUnit(0);
			leaveRecord.setBillId(0L);
			leaveRecord.setBillType(ConfigConstants.LEAVE_KEY);
			leaveRecord.setUpdateType(0);
			leaveRecord.setCreateTime(new Date());
			leaveRecord.setCreateUser("systemInit");
			leaveRecord.setDelFlag(0);
			leaveRecord.setSource(0);
			//2.保存流水
			leaveRecordMapper.save(leaveRecord);
			//3.保存流水详情
			LeaveRecordDetail leaveRecordDetail = new LeaveRecordDetail();
			leaveRecordDetail.setLeaveRecordId(leaveRecord.getId());
			leaveRecordDetail.setBaseEmpLeaveId(empLeave.getId());
			leaveRecordDetail.setDays(empLeave.getUsedDays());
			leaveRecordDetail.setCreateTime(new Date());
			leaveRecordDetail.setCreateUser("systemInit");
			leaveRecordDetail.setDelFlag(0);
			leaveRecordDetailMapper.save(leaveRecordDetail);
		}
	}

	@Override
	public List<LeaveRecord> getRecordListByYearAndEmployee(String yearType) {
		List<LeaveRecord> list = new ArrayList<LeaveRecord>();
		User user = userService.getCurrentUser();
		if(user==null||user.getEmployeeId()==null) {
			return list;
		}
		Integer year = Integer.valueOf(DateUtils.getYear(new Date()));
		if("lastYear".equals(yearType)) {
			year = year - 1;
		}
		list = leaveRecordMapper.getRecordListByYearAndEmployee(user.getEmployeeId(),year);
		return list;
	}
}
