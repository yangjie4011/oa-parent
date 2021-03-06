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
		//??????
		int currentYear = Integer.valueOf(DateUtils.getYear(new Date()));
		int lastYear = currentYear -1;
		
		/**???????????????4??????6????????????**/
		Date theSixthWorkDay = annualVacationService.getWorkingDayOfMonth(DateUtils.parse(String.valueOf(currentYear)+"-04-01",DateUtils.FORMAT_SHORT), 6);
		Date thisDay = DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_SHORT), DateUtils.FORMAT_SHORT);
		
		Map<String,Object> data = new HashMap<String,Object>();
		//??????????????????
		data.put("employeeId", employeeId);//??????ID
		data.put("yearDays", 0d);//??????????????????
		data.put("legalYearDays", 0d);//????????????????????????
		data.put("welfareYearDays", 0d);//????????????????????????
		data.put("paidSickDays", 0d);//????????????????????????
		//??????????????????
		data.put("lastYearDaysRemain", 0d);//??????????????????
		data.put("lastLegalYearDaysRemain", 0d);//????????????????????????
		data.put("lastWelfareYearDaysRemain", 0d);//????????????????????????
		data.put("legalYearDaysRemain", 0d);//???????????????????????????
		data.put("welfareYearDaysRemain", 0d);//????????????????????????
		data.put("yearDaysRemain", 0d);//????????????
		data.put("paidSickDaysRemain", 0d);//??????????????????
		data.put("offDaysRemain", 0d);//??????????????????
		data.put("lastOffDaysRemain", 0d);//??????????????????
		//??????????????????????????????
		data.put("yearDaysSofar", 0d);//????????????
		data.put("legalYearDaysSofar", 0d);//?????????????????????
		data.put("welfareYearDaysSofar", 0d);//??????????????????
		data.put("paidSickDaysSofar", 0d);//????????????
		//???????????????????????????
		data.put("yearDaysUsed", 0d);//?????????????????????
		data.put("legalYearDaysUsed", 0d);//???????????????????????????
		data.put("welfareYearDaysUsed", 0d);//???????????????????????????
		data.put("paidSickDaysUsed", 0d);//???????????????????????????
		data.put("unPaidSickDaysUsed", 0d);//??????????????????????????????
		data.put("affairsUsed", 0d);//?????????????????????
		data.put("offUsed", 0d);//??????????????????????????????
		data.put("lastOffUsed", 0d);//??????????????????????????????
		data.put("maritalUsed", 0d);//?????????????????????
		data.put("funeralUsed", 0d);//?????????????????????
		data.put("paternityUsed", 0d);//????????????????????????
		data.put("prenatalUsed", 0d);//????????????????????????
		data.put("maternityUsed", 0d);//?????????????????????
		data.put("lactationUsed", 0d);//????????????????????????
		data.put("abortionUsed", 0d);//????????????????????????
		data.put("otherUsed", 0d);//?????????????????????
		//??????????????????
		data.put("yearOverUsed", 0d);//??????
		data.put("paidSickOverUsed", 0d);//??????
		//??????????????????
		Double lastYearDaysUsed = 0d;//??????????????????
		Double lastLegalYearDaysUsed = 0d;//????????????????????????
		Double lastWelfareYearDaysUsed = 0d;//????????????????????????
		//????????????
		Double lastYearDays = 0d;//??????????????????
		Double lastLegalYearDays = 0d;//????????????????????????
		Double lastWelfareYearDays = 0d;//????????????????????????
		//????????????
		Double offDays = 0d;//??????????????????
		Double lastOffDays = 0d;//??????????????????
		//??????????????????????????????
		Double yearActualDays = 0d;//????????????????????????
		Double legalYearActualDays = 0d;//??????????????????????????????
		Double welfareYearActualDays = 0d;//??????????????????????????????
		Double paidSickActualDays = 0d;//??????????????????????????????
		//???????????????
		data.put("yearValidity", "");//?????????????????????
		data.put("lastYearValidity", "");//?????????????????????
		data.put("paidSickValidity", "");//?????????????????????
		data.put("offValidity", "");//?????????????????????
		data.put("lastOffValidity", "");//?????????????????????
		
		//?????????????????????
		List<LeaveRecord> list = leaveRecordMapper.getUsedLeaveListByEmployeeId(employeeId);
		for(LeaveRecord record:list){
			int type = record.getType().intValue();
			//??????-1,??????-2,??????-3,?????????-4,??????-5,?????????-6,??????-7,?????????-8,?????????-9,??????-10,??????-11,??????-12
			switch(type){
			   case 1://??????
				   //??????
				   if(currentYear==record.getYear().intValue()){
					   data.put("yearDaysUsed",Double.valueOf(String.valueOf(data.get("yearDaysUsed")))+record.getDays());//??????
					   if(record.getCategory().intValue()==1){//??????
						   data.put("legalYearDaysUsed", Double.valueOf(String.valueOf(data.get("legalYearDaysUsed")))+record.getDays());//????????????
					   }
                       if(record.getCategory().intValue()==2){//??????
                    	   data.put("welfareYearDaysUsed", Double.valueOf(String.valueOf(data.get("welfareYearDaysUsed")))+record.getDays());//????????????
					   }
				   }
				   //??????
				   if(lastYear==record.getYear().intValue()){
					   //??????????????????
					   lastYearDaysUsed = lastYearDaysUsed + record.getDays();
					   if(record.getCategory().intValue()==1){
						   //????????????????????????
						   lastLegalYearDaysUsed = lastLegalYearDaysUsed + record.getDays();
					   }
                       if(record.getCategory().intValue()==2){
                    	   //????????????????????????
                    	   lastWelfareYearDaysUsed = lastWelfareYearDaysUsed + record.getDays();
					   }
				   }
				   break;
			   case 2://??????
				   //??????
				   if(currentYear==record.getYear().intValue()){
					   if(record.getCategory().intValue()==0){//??????
						   data.put("paidSickDaysUsed",Double.valueOf(String.valueOf(data.get("paidSickDaysUsed")))+record.getDays());//????????????
					   }
					   if(record.getCategory().intValue()==2){//?????????
						   data.put("unPaidSickDaysUsed",Double.valueOf(String.valueOf(data.get("unPaidSickDaysUsed")))+record.getDays());//????????????
					   }
				   }
				   break;
			   case 3://??????
				   //??????
				   if(currentYear==record.getYear().intValue()){
					   data.put("maritalUsed", Double.valueOf(String.valueOf(data.get("maritalUsed")))+record.getDays());
				   }
				   break;
			   case 4://?????????
				   //??????
				   if(currentYear==record.getYear().intValue()){
					   data.put("lactationUsed", Double.valueOf(String.valueOf(data.get("lactationUsed")))+record.getDays());
				   }
				   break;
			   case 5://??????
				   //????????????
				   if(ConfigConstants.LEAVE_KEY.equals(record.getBillType())){
					   //??????parentId????????????????????????
					   EmpLeave parentLeave = empLeaveMapper.getById(record.getParendId());
					   if(parentLeave!=null&&parentLeave.getYear().intValue()==currentYear){
						   //??????????????????
						   data.put("offUsed", Double.valueOf(String.valueOf(data.get("offUsed")))+record.getDays());
					   }
					   if(parentLeave!=null&&parentLeave.getYear().intValue()==lastYear){
						   //??????????????????
						   data.put("lastOffUsed", Double.valueOf(String.valueOf(data.get("lastOffUsed")))+record.getDays());
					   }
				   }
				   //????????????
				   if(ConfigConstants.OVERTIME_KEY.equals(record.getBillType())){
					   //??????parentId????????????????????????
					   EmpLeave parentLeave = empLeaveMapper.getById(record.getParendId());
					   if(parentLeave!=null&&parentLeave.getYear().intValue()==currentYear){
						   //??????????????????
						   offDays = offDays + record.getDays();
					   }
					   if(parentLeave!=null&&parentLeave.getYear().intValue()==lastYear){
						   //??????????????????
						   lastOffDays = lastOffDays + record.getDays();
					   }
				   }
				   break;
			   case 6://?????????
				   //??????
				   if(currentYear==record.getYear().intValue()){
					   data.put("prenatalUsed", Double.valueOf(String.valueOf(data.get("prenatalUsed")))+record.getDays());
				   }
				   break;
			   case 7://??????
				   //??????
				   if(currentYear==record.getYear().intValue()){
					   data.put("maternityUsed", Double.valueOf(String.valueOf(data.get("maternityUsed")))+record.getDays());//??????
				   }
				   break;
			   case 8://?????????
				   if(currentYear==record.getYear().intValue()){
					   data.put("abortionUsed", Double.valueOf(String.valueOf(data.get("abortionUsed")))+record.getDays());//?????????
				   }
				   break;
			   case 9://?????????
				   if(currentYear==record.getYear().intValue()){
					   data.put("paternityUsed", Double.valueOf(String.valueOf(data.get("paternityUsed")))+record.getDays());//?????????
				   }
				   break;
			   case 10://??????
				   if(currentYear==record.getYear().intValue()){
					   data.put("funeralUsed", Double.valueOf(String.valueOf(data.get("funeralUsed")))+record.getDays());//??????
				   }
				   break;
			   case 11://??????
				   if(currentYear==record.getYear().intValue()){
					   data.put("affairsUsed", Double.valueOf(String.valueOf(data.get("affairsUsed")))+record.getDays());//??????
				   }
				   break;
			   case 12://??????
				   if(currentYear==record.getYear().intValue()){
					   data.put("otherUsed", Double.valueOf(String.valueOf(data.get("otherUsed")))+record.getDays());//??????
				   }
				   break;
			   default:
				   break;
			}
		}
		
		//??????????????????
		EmpLeave yearListP = new EmpLeave();
		yearListP.setEmployeeId(employeeId);
		yearListP.setType(ConfigConstants.LEAVE_TYPE_1);
		List<Integer> yearList = new ArrayList<Integer>();
		yearList.add(currentYear);
		yearList.add(lastYear);
		yearListP.setYearList(yearList);
		List<EmpLeave> yearLeaveList = empLeaveMapper.getListByCondition(yearListP);
		
		for(EmpLeave leave : yearLeaveList){
			//??????
			if(leave.getYear().intValue()==currentYear&&leave.getCategory().intValue()==0){
				//??????????????????
				data.put("yearDays", Double.valueOf(String.valueOf(data.get("yearDays")))+leave.getAllowDays());
				//????????????????????????
				yearActualDays = yearActualDays + leave.getActualDays();
				data.put("yearValidity", DateUtils.format(leave.getStartTime(), DateUtils.FORMAT_SHORT)+"???"+DateUtils.format(leave.getEndTime(), DateUtils.FORMAT_SHORT));//?????????????????????
				
			}
			if(leave.getYear().intValue()==currentYear&&leave.getCategory().intValue()==1){
				//????????????????????????
				data.put("legalYearDays", Double.valueOf(String.valueOf(data.get("legalYearDays")))+leave.getAllowDays());
				//??????????????????????????????
				legalYearActualDays = legalYearActualDays + leave.getActualDays();
			}
            if(leave.getYear().intValue()==currentYear&&leave.getCategory().intValue()==2){
            	//????????????????????????
            	data.put("welfareYearDays", Double.valueOf(String.valueOf(data.get("welfareYearDays")))+leave.getAllowDays());
            	//??????????????????????????????
            	welfareYearActualDays = welfareYearActualDays + leave.getActualDays();
			}
            //??????
            if(leave.getYear().intValue()==lastYear&&leave.getCategory().intValue()==0){
				//??????????????????
            	lastYearDays = lastYearDays + leave.getAllowDays();
            	data.put("lastYearValidity", DateUtils.format(leave.getStartTime(), DateUtils.FORMAT_SHORT)+"???"+DateUtils.format(leave.getEndTime(), DateUtils.FORMAT_SHORT));//?????????????????????
			
			}
			if(leave.getYear().intValue()==lastYear&&leave.getCategory().intValue()==1){
				//????????????????????????
				lastLegalYearDays = lastLegalYearDays + leave.getAllowDays();
			}
            if(leave.getYear().intValue()==lastYear&&leave.getCategory().intValue()==2){
            	//????????????????????????
            	lastWelfareYearDays = lastWelfareYearDays + leave.getAllowDays();
			}
		}
		
		//????????????
		data.put("yearDaysRemain", Double.valueOf(String.valueOf(data.get("yearDays")))-Double.valueOf(String.valueOf(data.get("yearDaysUsed"))));
		//????????????????????????
		data.put("legalYearDaysRemain", Double.valueOf(String.valueOf(data.get("legalYearDays")))-Double.valueOf(String.valueOf(data.get("legalYearDaysUsed"))));
		//??????????????????
		data.put("welfareYearDaysRemain", Double.valueOf(String.valueOf(data.get("welfareYearDays")))-Double.valueOf(String.valueOf(data.get("welfareYearDaysUsed"))));
	    if(thisDay.before(theSixthWorkDay)){
	    	//??????????????????
	    	data.put("lastYearDaysRemain", lastYearDays-lastYearDaysUsed);
	    	//????????????????????????
			data.put("lastLegalYearDaysRemain", lastLegalYearDays-lastLegalYearDaysUsed);
			//????????????????????????
			data.put("lastWelfareYearDaysRemain", lastWelfareYearDays-lastWelfareYearDaysUsed);
		}
		//????????????????????????
		EmpLeave sickListP = new EmpLeave();
		sickListP.setEmployeeId(employeeId);
		sickListP.setType(ConfigConstants.LEAVE_TYPE_2);
		List<EmpLeave> sickLeaveList = empLeaveMapper.getListByCondition(sickListP);
		for(EmpLeave leave : sickLeaveList){
			if(leave.getCategory().intValue()==0){
				//????????????????????????
				data.put("paidSickDays", Double.valueOf(String.valueOf(data.get("paidSickDays")))+leave.getAllowDays());
				//??????????????????????????????
				paidSickActualDays = paidSickActualDays + leave.getActualDays();
				data.put("paidSickValidity", DateUtils.format(leave.getStartTime(), DateUtils.FORMAT_SHORT)+"???"+DateUtils.format(leave.getEndTime(), DateUtils.FORMAT_SHORT));//?????????????????????
				
			}
		}
		//??????????????????
		Double paidSickDaysRemain = (Double.valueOf(String.valueOf(data.get("paidSickDays")))-Double.valueOf(String.valueOf(data.get("paidSickDaysUsed"))))>0?Double.valueOf(String.valueOf(data.get("paidSickDays")))-Double.valueOf(String.valueOf(data.get("paidSickDaysUsed"))):0;
		data.put("paidSickDaysRemain", paidSickDaysRemain);
		//????????????
		data.put("offDaysRemain", offDays-Double.valueOf(String.valueOf(data.get("offUsed"))));//??????????????????
		if(thisDay.before(theSixthWorkDay)){
			//??????????????????
			data.put("lastOffDaysRemain", lastOffDays - Double.valueOf(String.valueOf(data.get("lastOffUsed"))));
		}
		//??????????????????????????????
		Double yearDaysSofar = (yearActualDays-Double.valueOf(String.valueOf(data.get("yearDaysUsed"))))>0?yearActualDays-Double.valueOf(String.valueOf(data.get("yearDaysUsed"))):0d;
		data.put("yearDaysSofar", yearDaysSofar);//??????????????????????????????????????????
		Double legalYearDaysSofar = (legalYearActualDays-Double.valueOf(String.valueOf(data.get("legalYearDaysUsed"))))>0?legalYearActualDays-Double.valueOf(String.valueOf(data.get("legalYearDaysUsed"))):0d;
		data.put("legalYearDaysSofar", legalYearDaysSofar);//???????????????????????????????????????????????????
		Double welfareYearDaysSofar = (welfareYearActualDays-Double.valueOf(String.valueOf(data.get("welfareYearDaysUsed"))))>0?welfareYearActualDays-Double.valueOf(String.valueOf(data.get("welfareYearDaysUsed"))):0d;
		data.put("welfareYearDaysSofar", welfareYearDaysSofar);//????????????????????????????????????????????????
		Double paidSickDaysSofar = (paidSickActualDays-Double.valueOf(String.valueOf(data.get("paidSickDaysUsed"))))>0?paidSickActualDays-Double.valueOf(String.valueOf(data.get("paidSickDaysUsed"))):0d;
		data.put("paidSickDaysSofar", paidSickDaysSofar);//??????????????????????????????????????????
		//??????????????????
		Double yearOverUsed = (Double.valueOf(String.valueOf(data.get("yearDaysUsed")))-yearActualDays)>0?Double.valueOf(String.valueOf(data.get("yearDaysUsed")))-yearActualDays:0d;
		data.put("yearOverUsed", yearOverUsed);//??????
		Double paidSickOverUsed = (Double.valueOf(String.valueOf(data.get("paidSickDaysUsed")))-paidSickActualDays)>0?Double.valueOf(String.valueOf(data.get("paidSickDaysUsed")))-paidSickActualDays:0d;
		data.put("paidSickOverUsed", paidSickOverUsed);//??????
		
		//??????????????????
		EmpLeave offListP = new EmpLeave();
		offListP.setEmployeeId(employeeId);
		offListP.setType(ConfigConstants.LEAVE_TYPE_5);
		offListP.setYearList(yearList);
		offListP.setCategory(0);
		List<EmpLeave> offLeaveList = empLeaveMapper.getListByCondition(offListP);
		for(EmpLeave leave : offLeaveList){
			if(leave.getCategory().intValue()==0&&leave.getYear().intValue()==currentYear){
				data.put("offValidity", DateUtils.format(leave.getStartTime(), DateUtils.FORMAT_SHORT)+"???"+DateUtils.format(leave.getEndTime(), DateUtils.FORMAT_SHORT));//?????????????????????
			}
			if(leave.getCategory().intValue()==0&&leave.getYear().intValue()==lastYear){
				data.put("lastOffValidity", DateUtils.format(leave.getStartTime(), DateUtils.FORMAT_SHORT)+"???"+DateUtils.format(leave.getEndTime(), DateUtils.FORMAT_SHORT));//?????????????????????
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
		//????????????
		Date dayStart = DateUtils.parse(startTime1,DateUtils.FORMAT_SHORT);
		//????????????
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
				map.put("message","????????????????????????");
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
			logger.error(emp.getCnName()+"??????-???????????????"+e.toString());
			map.put("success",false);
		}
		return map;
	}
	
	/**
	 * ???????????????????????????
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void initLeaveRecord() {
		//??????????????????????????????id??????????????????????????????????????????
		List<Long> leaveRecordIds = leaveRecordMapper.getIdByBillType("leave");
		//??????????????????????????????????????????
		leaveRecordMapper.deleteByBillType("leave"); 
		//??????????????????
		if(leaveRecordIds.size() > 0){
			leaveRecordMapper.deleteDetailByLeaveRecordId(leaveRecordIds);
		}
		
		leaveRecordIds = leaveRecordMapper.getIdByBillType("cancelLeave");
		//??????????????????????????????????????????
		leaveRecordMapper.deleteByBillType("cancelLeave"); 
		//??????????????????
		if(leaveRecordIds.size() > 0){
			leaveRecordMapper.deleteDetailByLeaveRecordId(leaveRecordIds);
		}
		
		//???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
		//??????????????????
		//1.????????????????????????0??????????????????????????????
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
			//2.????????????
			leaveRecordMapper.save(leaveRecord);
			//3.??????????????????
			LeaveRecordDetail leaveRecordDetail = new LeaveRecordDetail();
			leaveRecordDetail.setLeaveRecordId(leaveRecord.getId());
			leaveRecordDetail.setBaseEmpLeaveId(empLeave.getId());
			leaveRecordDetail.setDays(empLeave.getUsedDays());
			leaveRecordDetail.setCreateTime(new Date());
			leaveRecordDetail.setCreateUser("systemInit");
			leaveRecordDetail.setDelFlag(0);
			leaveRecordDetailMapper.save(leaveRecordDetail);
		}
		//??????????????????.
		//1.????????????????????????0???????????????
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
			//2.????????????
			leaveRecordMapper.save(leaveRecord);
			//3.??????????????????
			LeaveRecordDetail leaveRecordDetail = new LeaveRecordDetail();
			leaveRecordDetail.setLeaveRecordId(leaveRecord.getId());
			leaveRecordDetail.setBaseEmpLeaveId(empLeave.getId());
			leaveRecordDetail.setDays(empLeave.getUsedDays());
			leaveRecordDetail.setCreateTime(new Date());
			leaveRecordDetail.setCreateUser("systemInit");
			leaveRecordDetail.setDelFlag(0);
			leaveRecordDetailMapper.save(leaveRecordDetail);
		}
		//??????????????????0????????????????????????
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
			//2.????????????
			leaveRecordMapper.save(leaveRecord);
			//3.??????????????????
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
