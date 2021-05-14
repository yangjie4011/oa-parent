package com.ule.oa.common.utils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.ule.oa.common.exception.OaException;

public class DateUtils {
	public static final String FORMAT_YYYY_MM = "yyyy-MM";
	
	public static final String FORMAT_SIMPLE = "yyyyMMdd";
	/**
	 * 英文简写（默认）如：2010-12-01
	 */
	public static final String FORMAT_SHORT = "yyyy-MM-dd";
	/**
	 * 英文全称 如：2010-12-01 23:15:06
	 */
	public static final String FORMAT_LONG = "yyyy-MM-dd HH:mm:ss";
	/**
	 * 精确到毫秒的完整时间 如：yyyy-MM-dd HH:mm:ss.S
	 */
	public static final String FORMAT_FULL = "yyyy-MM-dd HH:mm:ss.S";
	/**
	 * 中文简写 如：2010年12月01日
	 */
	public static final String FORMAT_SHORT_CN = "yyyy年MM月dd日";
	/**
	 * 中文全称 如：2010年12月01日 23时15分06秒
	 */
	public static final String FORMAT_LONG_CN = "yyyy年MM月dd日  HH时mm分ss秒";
	/**
	 * 中文带时间全称 如：2010年12月01日 23:15
	 */
	public static final String FORMAT_LONG_CN_MM = "yyyy年MM月dd日 HH:mm";
	/**
	 * 精确到毫秒的完整中文时间
	 */
	public static final String FORMAT_FULL_CN = "yyyy年MM月dd日  HH时mm分ss秒SSS毫秒";

	public static final String FORMAT_LONG_MM = "yyyy-MM-dd HH:mm";
	
	public static final String FORMAT_HH = "HH";
	
	public static final String FORMAT_HH_MM = "HH:mm";
	
	public static final String FORMAT_HH_MM_SS = "HH:mm:ss";
	
	public static final String FORMAT_MM_DD = "MM月dd日";
	
	public static final String FORMAT_DD_HH_MM = "dd日HH时mm分";
	/**
	 * 获得默认的 date pattern
	 */
	public static String getDatePattern() {
		return FORMAT_LONG;
	}

	/**
	 * 根据预设格式返回当前日期
	 * 
	 * @return
	 */
	public static String getNow() {
		return format(new Date());
	}

	/**
	 * 根据用户格式返回当前日期
	 * 
	 * @param format
	 * @return
	 */
	public static String getNow(String format) {
		return format(new Date(), format);
	}

	/**
	 * 使用预设格式格式化日期
	 * 
	 * @param date
	 * @return
	 */
	public static String format(Date date) {
		return format(date, getDatePattern());
	}

	/**
	 * 使用用户格式格式化日期
	 * 
	 * @param date
	 *            日期
	 * @param pattern
	 *            日期格式
	 * @return
	 */
	public static String format(Date date, String pattern) {
		String returnValue = "";
		if (date != null) {
			SimpleDateFormat df = new SimpleDateFormat(pattern);
			returnValue = df.format(date);
		}
		return (returnValue);
	}

	/**
	 * 使用预设格式提取字符串日期
	 * 
	 * @param strDate
	 *            日期字符串
	 * @return
	 * @throws ParseException 
	 */
	public static Date parse(String strDate) {
		return parse(strDate, getDatePattern());
	}

	/**
	 * 使用用户格式提取字符串日期
	 * 
	 * @param strDate
	 *            日期字符串
	 * @param pattern
	 *            日期格式
	 * @return
	 * @throws ParseException 
	 */
	public static Date parse(String strDate, String pattern) {
		if(StringUtils.isEmpty(strDate)){
			return null;
		}
		SimpleDateFormat df = new SimpleDateFormat(pattern);
		try {
			return df.parse(strDate);
		} catch (ParseException e) {
			return null;
		}
	}
	/**
	  * addYear(在指定日期上加上指定年份)
	  * @Title: addYear
	  * @Description: 在指定日期上加上指定年份
	  * @param date
	  * @param n
	  * @return    设定文件
	  * Date    返回类型
	  * @throws
	 */
	public static Date addYear(Date date, int n){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.YEAR, n);
		return cal.getTime();
	}

	/**
	 * 在日期上增加数个整月
	 * 
	 * @param date
	 *            日期
	 * @param n
	 *            要增加的月数
	 * @return
	 */
	public static Date addMonth(Date date, int n) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, n);
		return cal.getTime();
	}
	
	/**
	 * 在日期上增加天数
	 * 
	 * @param date
	 *            日期
	 * @param n
	 *            要增加的天数
	 * @return
	 */
	public static Date addDay(Date date, int n) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, n);
		return cal.getTime();
	}
	
	public static Date addHour(Date date, int n) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR, n);
		return cal.getTime();
	}
	
	/**
	 * 在日期上增加分钟
	 * 
	 * @param date
	 *            日期
	 * @param n
	 *            要增加的天数
	 * @return
	 */
	public static Date addMinute(Date date, int n) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, n);
		return cal.getTime();
	}

	/**
	 * 获取时间戳
	 */
	public static String getTimeString() {
		SimpleDateFormat df = new SimpleDateFormat(FORMAT_FULL);
		Calendar calendar = Calendar.getInstance();
		return df.format(calendar.getTime());
	}

	/**
	 * 获取日期年份
	 * 
	 * @param date
	 *            日期
	 * @return
	 */
	public static String getYear(Date date) {
		return format(date).substring(0, 4);
	}
	
	/**
	 * 获取日期小时
	 * 
	 * @param date
	 *            日期
	 * @return
	 */
	public static String getHours(Date date) {
		return format(date).substring(11, 13);
	}
	
	/**
	 * 获取日期月份日
	 * 
	 * @param date
	 *            日期
	 * @return
	 */
	public static String getDayOfMonth(Date date) {
		return format(date).substring(8, 10);
	}
	
	/**
	 * 获取日期年月
	 * 
	 * @param date
	 *            日期
	 * @return
	 */
	public static String getYearAndMonth(Date date) {
		return format(date).substring(0, 7);
	}
	
	/**
	 * 获取月
	 * 
	 * @param date
	 *            日期
	 * @return
	 */
	public static String getMonthofYear(Date date) {
		return format(date).substring(5, 7);
	}
	/**
	 * 获取日期年月
	 *  zjl
	 * @param date
	 *            日期
	 * @return
	 */
	public static String getYearAndMonthAndDay(Date date) {
		return format(date).substring(0, 10);
	}

	
	/**
	 * 按默认格式的字符串距离今天的天数
	 * 
	 * @param date
	 *            日期字符串
	 * @return
	 * @throws ParseException 
	 */
	public static int countDays(String date) {
		long t = Calendar.getInstance().getTime().getTime();
		Calendar c = Calendar.getInstance();
		c.setTime(parse(date));
		long t1 = c.getTime().getTime();
		return (int) (t / 1000 - t1 / 1000) / 3600 / 24;
	}

	/**
	 * 按用户格式字符串距离今天的天数
	 * 
	 * @param date
	 *            日期字符串
	 * @param format
	 *            日期格式
	 * @return
	 * @throws ParseException 
	 */
	public static int countDays(String date, String format) {
		long t = Calendar.getInstance().getTime().getTime();
		Calendar c = Calendar.getInstance();
		c.setTime(parse(date, format));
		long t1 = c.getTime().getTime();
		return (int) (t / 1000 - t1 / 1000) / 3600 / 24;
	}
	public static String convenrtDateToStr(Date date ,String formate){
		SimpleDateFormat df = new SimpleDateFormat(formate);
		if(null!=date){
			return df.format(date);
		}
		return "";
	}
	public static String convenrtDataBefore(Date date ,String formate,Integer hour){
		if(null==date){
			return "";
		}
	    Calendar Cal= Calendar.getInstance();      
	    Cal.setTime(date);      
	    Cal.add(Calendar.HOUR_OF_DAY,hour);      
	   return  convenrtDateToStr(Cal.getTime(),formate);
	}
	
	/**
	 * 获取当天，没有时分秒
	 */
	public static Date getToday() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}
	
	/**
	 * @Description: 截取时间
	 * @author zhangwei
	 */
	public static Date truncate(Date date, int field) {
		return org.apache.commons.lang3.time.DateUtils.truncate(date, field);
	}
	
	/**
	 * @Description: 截取时间
	 * @author zhangwei
	 */
	public static Calendar truncate(Calendar date, int field) {
		return org.apache.commons.lang3.time.DateUtils.truncate(date, field);
	}
	
	/**
	  * getIntervalSeconds(计算两个时间相差的秒)
	  * @Title: getIntervalSeconds
	  * @Description: 计算两个时间相差的秒
	  * @param startDate
	  * @param endDate
	  * @return    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	public static long getIntervalSeconds(Date startDate, Date endDate){
		Calendar calst = Calendar.getInstance();
        Calendar caled = Calendar.getInstance();

        calst.setTime(startDate);
        caled.setTime(endDate);
        
        return ((caled.getTime().getTime()/1000)-(calst.getTime().getTime()/1000));  
	}
	
	/**
	 * 相差数值过大,误差很大
	 * @param startDate
	 *            日期
	 * @param endDate
	 *            另一个日期
	 * @return 相差天数
	 */
	public static Integer getIntervalDays(Date startDate, Date endDate) {
		
        Calendar calst = Calendar.getInstance();
        Calendar caled = Calendar.getInstance();

        calst.setTime(startDate);
        caled.setTime(endDate);
 
         //设置时间为0时   
         calst.set(Calendar.HOUR_OF_DAY, 0);   
         calst.set(Calendar.MINUTE, 0);   
         calst.set(Calendar.SECOND, 0);   
         caled.set(Calendar.HOUR_OF_DAY, 0);   
         caled.set(Calendar.MINUTE, 0);   
         caled.set(Calendar.SECOND, 0);   
        //得到两个日期相差的天数   
        int days = ((int)(caled.getTime().getTime()/1000)-(int)(calst.getTime().getTime()/1000))/3600/24;   
         
        return days;   
	}
	
	/** 
	 * 无误差
     * 计算两个阳历日期相差的天数。 
     * @param startDate 开始时间 
     * @param endDate 截至时间 
     * @return (int)天数 
     */  
    @SuppressWarnings("unused")
	private static int daysBetween(Date startDate, Date endDate) {  
        int days = 0;  
        //将转换的两个时间对象转换成Calendar对象  
        Calendar can1 = Calendar.getInstance();  
        can1.setTime(startDate);  
        Calendar can2 = Calendar.getInstance();  
        can2.setTime(endDate);  
        //拿出两个年份  
        int year1 = can1.get(Calendar.YEAR);  
        int year2 = can2.get(Calendar.YEAR);  
        //天数  
  
        Calendar can = null;  
        //如果can1 < can2  
        //减去小的时间在这一年已经过了的天数  
        //加上大的时间已过的天数  
        if(can1.before(can2)){  
            days -= can1.get(Calendar.DAY_OF_YEAR);  
            days += can2.get(Calendar.DAY_OF_YEAR);  
            can = can1;  
        }else{  
            days -= can2.get(Calendar.DAY_OF_YEAR);  
            days += can1.get(Calendar.DAY_OF_YEAR);  
            can = can2;  
        }  
        for (int i = 0; i < Math.abs(year2-year1); i++) {  
            //获取小的时间当前年的总天数  
            days += can.getActualMaximum(Calendar.DAY_OF_YEAR);  
            //再计算下一年。  
            can.add(Calendar.YEAR, 1);  
        }  
        return days;  
    }
	
	/**
	 * @throws ParseException 
	  * getIntervalMonths(计算两个时间相差的月份)
	  * @Title: getIntervalMonths
	  * @Description: 计算两个时间相差的月份
	  * @param date
	  * @param otherDate
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
	public static int getIntervalMonths(Date startDate, Date endDate) {
        Calendar bef = Calendar.getInstance();  
        Calendar aft = Calendar.getInstance();  
        bef.setTime(parse(DateUtils.format(endDate),"yyyy-MM"));  
        aft.setTime(parse(DateUtils.format(startDate),"yyyy-MM"));  
        int result = aft.get(Calendar.MONTH) - bef.get(Calendar.MONTH);  
        int month = (aft.get(Calendar.YEAR) - bef.get(Calendar.YEAR)) * 12;  
        
        return Math.abs(month + result);     
	}
	
	/**
	 * @param startDate 开始日期
	 * @param endDate 结束日期
	 * @Description:计算日期相差的小时数
	 * @return
	 */
	public static Long getIntervalHours(Date startDate, Date endDate) {
		long time = endDate.getTime() - startDate.getTime();
		return (long) time == 0 ? 0 : time/(60 * 60 * 1000);
	}

	/**
	 * @param date 日期
	 * @param otherDate 另一个日期
	 * @Description:日期相差分钟数
	 * @return
	 */
	public static Long getIntervalMinutes(Date date, Date otherDate) {
		long time = Math.abs(date.getTime() - otherDate.getTime());
		return (long) time == 0 ? 0 : time/(60 * 1000);
	}
	
	public static Long getIntervalMinutesF(Date date, Date otherDate) {
		long time = date.getTime() - otherDate.getTime();
		return (long) time == 0 ? 0 : time/(60 * 1000);
	}
	
	/**
	 * 比较两个日期大小 ：0-相等、1-date大于d2、2-date小于d2
	 * @param date
	 * @param d2
	 * @return
	 */
	public static int compareDate(Date date, Date d2) {
		
		date = parse(format(date,"yyyy-MM-dd HH:mm"), "yyyy-MM-dd HH:mm");
		d2 = parse(format(d2,"yyyy-MM-dd HH:mm"), "yyyy-MM-dd HH:mm");
		if(date!=null&&d2!=null&&date.getTime() > d2.getTime()) {
			return 1;
		} else if(date!=null&&d2!=null&&date.getTime() < d2.getTime()) {
			return 2;
		} else {
			return 0;
		}
	}
	
	/**
	 * 比较两个日期大小 ：0-相等、1-date大于d2、2-date小于d2
	 * @param date
	 * @param d2
	 * @return
	 */
	public static int compareDayDate(Date date, Date d2) {
		
		date = parse(format(date,"yyyy-MM-dd"), "yyyy-MM-dd");
		d2 = parse(format(d2,"yyyy-MM-dd"), "yyyy-MM-dd");
		if(date!=null&&d2!=null&&date.getTime() > d2.getTime()) {
			return 1;
		} else if(date!=null&&d2!=null&&date.getTime() < d2.getTime()) {
			return 2;
		} else {
			return 0;
		}
	}
	
	/**
	 * 判断一个日期是否比另一个日期早 ：startDate比endDate早, 返回true, 否则返回 false
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static Boolean isBefore(Date startDate, Date endDate) {
		
		startDate = parse(format(startDate, FORMAT_LONG), FORMAT_LONG);
		endDate = parse(format(endDate, FORMAT_LONG), FORMAT_LONG);
		if(startDate!=null&&endDate!=null&&startDate.getTime() < endDate.getTime()) {
			return true;
		}
		return false;
	}
	
	/**
	  * getMaxDaysOfYear(获取指定年天数)
	  * @Title: getMaxDaysOfYear
	  * @Description: 获取指定年天数
	  * @param year
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
	public static int getMaxDaysOfYear(int year) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		return cal.getActualMaximum(Calendar.DAY_OF_YEAR);
	}
	
	/**
	  * getHalfDaysByPoint(获取半天，4.4=4天，4.6=4.5)
	  * @Title: getHalfDaysByPoint
	  * @Description: 获取半天，4.4=4天，4.6=4.5
	  * @param num
	  * @return    设定文件
	  * Double    返回类型
	  * @throws
	 */
	public static Double getHalfDaysByPoint(Double num){
		BigDecimal days = BigDecimal.valueOf(num);
		days = days.setScale(1, BigDecimal.ROUND_HALF_UP);
		int zs = days.intValue();//整数部分
		Double xs = days.doubleValue() - zs;//小数部分
		if(xs<0.5){
			num = days.intValue()+0.0;
		}else{
			num = days.intValue()+0.5;
		}
		
		return num;
	}
	
	/**
	 * @throws OaException 
	  * isInterDay(根据开始时间和结束时间判断是否跨天【0：不跨天，1：跨天】)
	  * @Title: isInterDay
	  * @Description: 根据开始时间和结束时间判断是否跨天【0：不跨天，1：跨天】
	  * @param startTime
	  * @param endTime
	  * @return    设定文件
	  * boolean    返回类型
	  * @throws
	 */
	public static Integer isInterDay(Date startTime,Date endTime) throws OaException{
		Integer isInterDay = 0;
		
		if(startTime.compareTo(endTime) <= 0){
			String time1 = format(startTime,FORMAT_SIMPLE);//yyyyMMdd
			String time2 = format(endTime,FORMAT_SIMPLE);//yyyyMMdd
			
			//判断是否是同一天
			if(time1.equals(time2)){
				isInterDay = 0;//不垮天
			}else{
				isInterDay = 1;//跨天
			}
		}else{
			throw new OaException("开始时间比结束时间大");
		}
		
		return isInterDay;
	}
	
	/**
	  * getLastYearMonth(获取指定时间的上一个年月)
	  * @Title: getLastYearMonth
	  * @Description: 获取指定时间的上一个年月
	  * @param date
	  * @return    设定文件
	  * String    返回类型
	  * @throws
	 */
	public static String getLastYearMonth(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, -1);
		
		return format(cal.getTime(),FORMAT_YYYY_MM);
	}
	
	/**
	  * getLastDay(获取指定日期的最后一天，例如指定日期是：7月12号，最后一天就是7月31号)
	  * @Title: getLastDay
	  * @Description: 获取指定日期的最后一天，例如指定日期是：7月12号，最后一天就是7月31号
	  * @param datadate
	  * @return
	  * @throws Exception    设定文件
	  * Date    返回类型
	  * @throws
	 */
	public static Date getLastDay(Date date){
       String day_last = null;
       
       //创建日历
       Calendar calendar = Calendar.getInstance();
       calendar.setTime(date);
       calendar.add(Calendar.MONTH, 1);    //加一个月
       calendar.set(Calendar.DATE, 1);     //设置为该月第一天
       calendar.add(Calendar.DATE, -1);    //再减一天即为上个月最后一天
       day_last = format(calendar.getTime(), FORMAT_SHORT);
       return parse(day_last, FORMAT_SHORT);
   }
   
	/**
	  * getFirstDay(获取指定日期的最后一天，例如指定日期是：7月12号，最后一天就是7月1号)
	  * @Title: getFirstDay
	  * @Description: 获取指定日期的最后一天，例如指定日期是：7月12号，最后一天就是7月1号
	  * @param datadate
	  * @return
	  * @throws Exception    设定文件
	  * Date    返回类型
	  * @throws
	 */
   public static Date getFirstDay(Date date){
       String day_first = null;
       
       //创建日历
       Calendar calendar = Calendar.getInstance();
       calendar.setTime(date);
       calendar.set(Calendar.DAY_OF_MONTH, 1);
       day_first = format(calendar.getTime(), FORMAT_SHORT);
       return parse(day_first, FORMAT_SHORT);
   }
   
   /**
    * 获取两个日期之间的所有日期
    * @param dBegin 开始日期
    * @param dEnd 结束日期
    * @return
    */
   public static List<Date> findDates(Date dBegin, Date dEnd) {
	   List<Date> dateList = new ArrayList<Date>();
	   dateList.add(dBegin);
	   Calendar calBegin = Calendar.getInstance();
	   // 使用给定的 Date 设置此 Calendar 的时间
	   calBegin.setTime(dBegin);
	   Calendar calEnd = Calendar.getInstance();
	   // 使用给定的 Date 设置此 Calendar 的时间
	   calEnd.setTime(dEnd);
	   // 测试此日期是否在指定日期之后
	   while (dEnd.after(calBegin.getTime())) {
		   // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
		   calBegin.add(Calendar.DAY_OF_MONTH, 1);
		   //开始年份小于结束年份,记录开始天时间
		   if(calEnd.get(Calendar.YEAR) > calBegin.get(Calendar.YEAR)){
			   dateList.add(calBegin.getTime()); 
		   }
		   //同一年,则比较月份
		   if(calEnd.get(Calendar.YEAR) == calBegin.get(Calendar.YEAR)){
			   //开始月份小于结束月份,记录开始月份
			   if(calEnd.get(Calendar.MONTH) > calBegin.get(Calendar.MONTH)){
				   dateList.add(calBegin.getTime()); 
			   }
			 //开始月份等于结束月份,则比较日期
			   if(calEnd.get(Calendar.MONTH) == calBegin.get(Calendar.MONTH)){
				   //开始日期小于结束日期,记录开始日期
				   if((calEnd.get(Calendar.DAY_OF_MONTH)) > calBegin.get(Calendar.DAY_OF_MONTH)){
					   dateList.add(calBegin.getTime());
				   }
				   //开始日期等于结束日期,记录结束日期
				   if((calEnd.get(Calendar.DAY_OF_MONTH)) == calBegin.get(Calendar.DAY_OF_MONTH)){
					   dateList.add(calEnd.getTime());
				   }
			   }
		   }
	   }
	   return dateList;
	}
   
   /**
    * 获取两个日期之间的所有月份
    * @param dBegin 开始日期
    * @param dEnd 结束日期
    * @return
    */
   public static List<Date> findMonths(Date dBegin, Date dEnd) {
	   List<Date> dateList = new ArrayList<Date>();
	   Calendar calBegin = Calendar.getInstance();
	   // 使用给定的 Date 设置此 Calendar 的时间
	   calBegin.setTime(dBegin);
	   dateList.add(calBegin.getTime());
	   Calendar calEnd = Calendar.getInstance();
	   // 使用给定的 Date 设置此 Calendar 的时间
	   calEnd.setTime(dEnd);
	   // 测试此日期是否在指定日期之后
	   while (dEnd.after(calBegin.getTime())) {
		   // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
		   calBegin.add(Calendar.MONTH, 1);
		   //开始年份小于结束年份,记录开始月份
		   if((calEnd.get(Calendar.YEAR)) > calBegin.get(Calendar.YEAR)){
			   dateList.add(calBegin.getTime());
		   }
		   //同一年,则比较月份
		   if((calEnd.get(Calendar.YEAR)) == calBegin.get(Calendar.YEAR)){
			   //开始月份小于结束月份,记录开始月份
			   if((calEnd.get(Calendar.MONTH)) > calBegin.get(Calendar.MONTH)){
				   dateList.add(calBegin.getTime());
			   }
			   //开始月份等于结束月份,记录结束月份
			   if((calEnd.get(Calendar.MONTH)) == calBegin.get(Calendar.MONTH)){
				   dateList.add(calEnd.getTime());
			   }
		   }
	   }
	   return dateList;
	}
   
   /**
     * getWeek(根据指定时间判断是星期几)
     * @Title: getWeek
     * @Description: 根据指定时间判断是星期几
     * @param date
     * @return    设定文件
     * String    返回类型
     * @throws
    */
   public static String getWeek(Date date){
	   String day = "";
	   Calendar cal = Calendar.getInstance();
	   cal.setTime(date);

	   switch (cal.get(Calendar.DAY_OF_WEEK)) {
	   case 1:
		   day = "日";
		   break;
	   case 2:
		   day = "一";
		   break;
		   
	   case 3:
		   day = "二";
		   break;
		   
	   case 4:
		   day = "三";
		   break;
		   
	   case 5:
		   day = "四";
		   break;
		   
	   case 6:
		   day = "五";
		   break;
		   
	   case 7:
		   day = "六";
		   break;
		   
	   default:
		   break;
	   }
	   
	   return day;
   }
   
   /**
    * 获取当年的最后一天
    * @param year
    * @return
    */
   public static Date getCurrYearLast(){
       Calendar currCal=Calendar.getInstance();  
       int currentYear = currCal.get(Calendar.YEAR);
       return getYearLast(currentYear);
   }
   /**
    * 获取某年最后一天日期
    * @param year 年份
    * @return Date
    */
   public static Date getYearLast(int year){
       Calendar calendar = Calendar.getInstance();
       calendar.clear();
       calendar.set(Calendar.YEAR, year);
       calendar.roll(Calendar.DAY_OF_YEAR, -1);
       Date currYearLast = calendar.getTime();       
       return currYearLast;
   }
   
   /**
    * 获取某年开始一天日期
    * @param year 年份
    * @return Date
    */
   /**
    * 获取某年开始一天日期
    * @param year 年份
    * @return Date
    */
   public static Date getYearBegin(Date date){
	   Calendar currCal=Calendar.getInstance(); 
	   currCal.clear();
       currCal.set(Calendar.YEAR, Integer.valueOf(getYear(date)));
       return currCal.getTime();
   }
   public static Date getYearBegin(){
	   Calendar currCal=Calendar.getInstance(); 
	   currCal.clear();
       Calendar calendar = Calendar.getInstance();
       currCal.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
       return currCal.getTime();
   }
   
   
   
   public static String getTime(Date startTime, Date endTime,String format1, String format) {
		String view4 = "";
		if(DateUtils.compareDate(DateUtils.parse(DateUtils.format(startTime), "yyyy"), DateUtils.parse(DateUtils.getNow(), "yyyy")) == 0) {
			view4 = DateUtils.format(startTime, format1);
		} else {
			view4 = view4 + DateUtils.format(startTime, format);
		}
		
		if(DateUtils.compareDate(DateUtils.parse(DateUtils.format(startTime), "yyyy-MM-dd"), DateUtils.parse(DateUtils.format(endTime), "yyyy-MM-dd")) == 0) {
			if(!format.equals("yyyy-MM-dd")) {
				view4 = view4 + "~" + DateUtils.format(endTime, "HH:mm") ;
			}
		} else if(DateUtils.compareDate(DateUtils.parse(DateUtils.format(startTime), "yyyy"), DateUtils.parse(DateUtils.format(endTime), "yyyy")) == 0) {
			view4 = view4 + "~" + DateUtils.format(endTime, format1);
		} else {
			view4 = view4 + "~" + DateUtils.format(endTime, format);
		}
		return view4;
	}
	
	public static String getTime(Date startTime, Date endTime) {
		return getTime(startTime,endTime,"MM-dd HH:mm","yyyy-MM-dd HH:mm");
	}
	
	/**
	 * 开始时间增加month月份,是否超过结束时间
	 * @param startTime  开始时间
	 * @param lastTime  结束时间
	 * @param month  增加的时间
	 * @return
	 */
	public static boolean isSpendMonths(Date startTime, Date lastTime,int month ) {
		if(startTime!=null&&lastTime!=null) {
			String s = format(lastTime);
			String e = format(addMonth(startTime, month));
			if(StringUtils.isNotBlank(s)&&StringUtils.isNotBlank(e)) {
				Date sd = parse(s, "yyyy-MM-dd");
				Date ed = parse(e, "yyyy-MM-dd");
				if(sd!=null&&ed!=null){
					return sd.getTime() - ed.getTime()  > 0;
				}
			}
			
		}
		return false;
	}
	
	/**
	 * 获取员工的司龄
	 * @param joinDay
	 * @return
	 */
	public static Double getJoinYear(Date joinDay,Date quitDay){
		Calendar left=Calendar.getInstance(); 
		Calendar join = Calendar.getInstance();
		join.setTime(joinDay);
		left.setTime(quitDay==null?new Date():quitDay);
        int nowYear = left.get(Calendar.YEAR);
        int joinYear = join.get(Calendar.YEAR);
        Integer workYear =nowYear - joinYear;
        Date lastDate = addYear(joinDay, workYear);
        return workYear.doubleValue() - getIntervalDays(left.getTime(), lastDate).doubleValue()/365;
	}
	
	public static Date getNextYearBegin() {
		return addYear(getYearBegin(), 1);
	}
	
	/**
	 * @param startDate 开始日期
	 * @param endDate 结束日期
	 * @Description:计算日期相差的小时数
	 * @return
	 */
	public static Double getIntervalHours(Date startTime, Date endTime, Date startRestTime, Date endRestTime) {
		if(startTime!=null&&endTime!=null&&startRestTime!=null&&endRestTime!=null) {
			Double hours = 0.0;
			long time = 0L;
	        if(endTime.getTime()<=startRestTime.getTime()) {
	        	time = endTime.getTime() - startTime.getTime();
	        }else if(startTime.getTime()<startRestTime.getTime()&&endTime.getTime()>startRestTime.getTime()&&endTime.getTime()<=endRestTime.getTime()) {
	        	time = startRestTime.getTime() - startTime.getTime();
	        }else if(startTime.getTime()<startRestTime.getTime()&&endTime.getTime()>endRestTime.getTime()) {
	        	time = endTime.getTime() - startTime.getTime()-3600*1000;
	        }else if(startTime.getTime()>=startRestTime.getTime()&&endTime.getTime()>endRestTime.getTime()) {
	        	time = endTime.getTime() - endRestTime.getTime();
	        }else if(startTime.getTime()>=endRestTime.getTime()) {
	        	time = endTime.getTime() - startTime.getTime();
	        }
	        time = time == 0 ? 0 : time/(30 * 60 * 1000);
			return (double)time/2;
		}
		return 0.0;
		
	}
	
	public static void main(String[] args) {
		

	}
}

