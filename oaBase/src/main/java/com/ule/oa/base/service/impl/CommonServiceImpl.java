package com.ule.oa.base.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ule.oa.base.mapper.AnnualVacationMapper;
import com.ule.oa.base.po.AnnualVacation;
import com.ule.oa.base.service.CommonService;
import com.ule.oa.common.utils.DateUtils;

@Service
public class CommonServiceImpl implements CommonService {
	
	@Autowired
	private AnnualVacationMapper annualVacationMapper;

	@Override
	public int getWorkDaysCount(Date startDate, Date endDate) {
		
		int daysCount = 0;//工作天数
		//查询时间段内假期信息
		AnnualVacation vacation = new AnnualVacation();
		vacation.setStartDate(startDate);
		vacation.setEndDate(endDate);
		List<AnnualVacation> list = annualVacationMapper.getListByCondition(vacation);
		Map<Date,Integer> vacationMap = new HashMap<Date,Integer>();
		for(AnnualVacation data:list){
			vacationMap.put(data.getAnnualDate(), data.getType());
		}
		
		//判断当月每天周末
		while(true){
			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			//周末
			if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY
					||cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
				//周末，如果是节假日调班，算工作日
				if(vacationMap!=null&&vacationMap.containsKey(startDate)
						&&vacationMap.get(startDate).intValue()==1){
					daysCount = daysCount + 1;
				}
			}else{
				//非周末，如果不是节假日，就算工作天数
				daysCount = daysCount + 1;
				//如果非工作日是节假日，daysCount-1
				if(vacationMap!=null&&vacationMap.containsKey(startDate)
						&&vacationMap.get(startDate).intValue()!=1){
					daysCount = daysCount - 1;
				}
			}
			startDate = DateUtils.addDay(startDate, 1);
			if(startDate.getTime()>vacation.getEndDate().getTime()){
				break;
			}
		}
		return daysCount;
	}
	
	

}
