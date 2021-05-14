package com.ule.oa.web.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ule.oa.base.po.AttnStatistics;
import com.ule.oa.common.utils.DateUtils;

public class TestCalinda {
	
	protected final static Logger logger = LoggerFactory.getLogger(TestCalinda.class);

	public static void main(String[] args) {
		
		String yearMonth = "2017-01";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
		
		AttnStatistics condition = new AttnStatistics();
		String firstDay,lastDay;
		
		Calendar cal=Calendar.getInstance();

		Date month = DateUtils.parse(yearMonth, "yyyy-MM");
		cal.setTime(month);
		cal.set(Calendar.DAY_OF_MONTH,1);
		condition.setStartTime(cal.getTime());
		
		cal.add(Calendar.MONTH, 1);
		cal.set(Calendar.DAY_OF_MONTH,0);
		condition.setEndTime(cal.getTime());
		 

		firstDay = format.format(condition.getStartTime());
		System.out.println("firstDay:"+firstDay);
		
		lastDay = format.format(condition.getEndTime());
		System.out.println("lastDay:"+lastDay);

		Date eDate = DateUtils.getToday();
		Date sdate = DateUtils.addMonth(eDate, -1);
		long betweendays=(long) ((eDate.getTime()-sdate.getTime())/(1000 * 60 * 60 *24)+0.5);//天数间隔
		System.out.println("间隔天数:"+betweendays);
        final Calendar c = Calendar.getInstance();
        Integer i = 0;
        while (sdate.getTime()<=eDate.getTime()) {
        	i++;
			System.out.println(i+"循环到的sdate:"+DateUtils.format(sdate));
        	
			c.setTime(sdate);
			c.add(Calendar.DATE, 1); // 日期加1天
			sdate = c.getTime();
         }
        
        Date now = new Date();
        c.setTime(now);
		System.out.println("现在的时间:"+c.get(Calendar.HOUR_OF_DAY));
		System.out.println("现在的时间:"+(c.get(Calendar.HOUR_OF_DAY)!=14));
		
        Integer HOUR_OF_DAY = c.get(Calendar.HOUR_OF_DAY);//现在是几点
        if(HOUR_OF_DAY != 11){
        	 logger.info("当前时间为：{}，不发送kafka。",HOUR_OF_DAY);
        }else{
       	     logger.info("当前时间为：{}，发送kafka。",HOUR_OF_DAY);
        }
		
		

		cal.set(Calendar.HOUR, 9);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND,0);
		System.out.println("设置9点后的时间:"+DateUtils.format(cal.getTime(), DateUtils.FORMAT_FULL));
		
		Date date1 = new Date();
		Date date2 = DateUtils.addMinute(date1, 32);
		Long  minute = getIntervalMinutes(date2,date1);
		System.out.println("两个时间的间隔:"+minute);
		
		sdate = DateUtils.getToday();
		System.out.println("两个时间相等:"+(sdate.equals(DateUtils.getToday())));
		Double sdfsd = 5.372;
		Double sdfsd1 = 5.321;
	    int sdfsd2 = sdfsd1.compareTo(sdfsd);
		logger.info("比较,{}",sdfsd2);
	}
	
	private static Long getIntervalMinutes(Date date, Date otherDate) {
		long time = date.getTime() - otherDate.getTime();
		return (long) time == 0 ? 0 : time/(60 * 1000);
	}

}
