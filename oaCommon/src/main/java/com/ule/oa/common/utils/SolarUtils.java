package com.ule.oa.common.utils;

import java.text.ParseException;
import java.util.Date;

import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;

public class SolarUtils {
	/**
	* @1900-2100区间内的公历、农历互转
	* @charset UTF-8
	* @Author  Jea杨(JJonline@JJonline.Cn) 
	* @Time    2014-7-21
	* @Time    2016-8-13 Fixed 2033hex、Attribution Annals
	* @Time    2016-9-25 Fixed lunar LeapMonth Param Bug
	* @Time    2017-7-24 Fixed use getTerm Func Param Error.use solar year,NOT lunar year
	* @Version 1.0.3
	* @公历转农历：calendar.solar2lunar(1987,11,01); //[you can ignore params of prefix 0]
	* @农历转公历：calendar.lunar2solar(1987,09,10); //[you can ignore params of prefix 0]
	*/
	 // 允许输入的最小年份  
    private final static int MIN_YEAR = 1900;  
    // 允许输入的最大年份  
    private final static int MAX_YEAR = 2100;  
    private final static String START_DATE = "19000130";  
	 
	    /**
	      * 农历1900-2100的润大小信息表
	      * @Array Of Property
	      * @return Hex 
	      */
	    private static int[] lunarInfo = {0x04bd8,0x04ae0,0x0a570,0x054d5,0x0d260,0x0d950,0x16554,0x056a0,0x09ad0,0x055d2,//1900-1909
                0x04ae0,0x0a5b6,0x0a4d0,0x0d250,0x1d255,0x0b540,0x0d6a0,0x0ada2,0x095b0,0x14977,//1910-1919
                0x04970,0x0a4b0,0x0b4b5,0x06a50,0x06d40,0x1ab54,0x02b60,0x09570,0x052f2,0x04970,//1920-1929
                0x06566,0x0d4a0,0x0ea50,0x06e95,0x05ad0,0x02b60,0x186e3,0x092e0,0x1c8d7,0x0c950,//1930-1939
                0x0d4a0,0x1d8a6,0x0b550,0x056a0,0x1a5b4,0x025d0,0x092d0,0x0d2b2,0x0a950,0x0b557,//1940-1949
                0x06ca0,0x0b550,0x15355,0x04da0,0x0a5b0,0x14573,0x052b0,0x0a9a8,0x0e950,0x06aa0,//1950-1959
                0x0aea6,0x0ab50,0x04b60,0x0aae4,0x0a570,0x05260,0x0f263,0x0d950,0x05b57,0x056a0,//1960-1969
                0x096d0,0x04dd5,0x04ad0,0x0a4d0,0x0d4d4,0x0d250,0x0d558,0x0b540,0x0b6a0,0x195a6,//1970-1979
                0x095b0,0x049b0,0x0a974,0x0a4b0,0x0b27a,0x06a50,0x06d40,0x0af46,0x0ab60,0x09570,//1980-1989
                0x04af5,0x04970,0x064b0,0x074a3,0x0ea50,0x06b58,0x055c0,0x0ab60,0x096d5,0x092e0,//1990-1999
                0x0c960,0x0d954,0x0d4a0,0x0da50,0x07552,0x056a0,0x0abb7,0x025d0,0x092d0,0x0cab5,//2000-2009
                0x0a950,0x0b4a0,0x0baa4,0x0ad50,0x055d9,0x04ba0,0x0a5b0,0x15176,0x052b0,0x0a930,//2010-2019
                0x07954,0x06aa0,0x0ad50,0x05b52,0x04b60,0x0a6e6,0x0a4e0,0x0d260,0x0ea65,0x0d530,//2020-2029
                0x05aa0,0x076a3,0x096d0,0x04afb,0x04ad0,0x0a4d0,0x1d0b6,0x0d250,0x0d520,0x0dd45,//2030-2039
                0x0b5a0,0x056d0,0x055b2,0x049b0,0x0a577,0x0a4b0,0x0aa50,0x1b255,0x06d20,0x0ada0,//2040-2049
                /**Add By JJonline@JJonline.Cn**/
                0x14b63,0x09370,0x049f8,0x04970,0x064b0,0x168a6,0x0ea50, 0x06b20,0x1a6c4,0x0aae0,//2050-2059
                0x0a2e0,0x0d2e3,0x0c960,0x0d557,0x0d4a0,0x0da50,0x05d55,0x056a0,0x0a6d0,0x055d4,//2060-2069
                0x052d0,0x0a9b8,0x0a950,0x0b4a0,0x0b6a6,0x0ad50,0x055a0,0x0aba4,0x0a5b0,0x052b0,//2070-2079
                0x0b273,0x06930,0x07337,0x06aa0,0x0ad50,0x14b55,0x04b60,0x0a570,0x054e4,0x0d160,//2080-2089
                0x0e968,0x0d520,0x0daa0,0x16aa6,0x056d0,0x04ae0,0x0a9d4,0x0a2d0,0x0d150,0x0f252,//2090-2099
                0x0d520};
	 
	    /**
	      * 公历每个月份的天数普通表
	      * @Array Of Property
	      * @return Number 
	      */
	    private static int[] solarMonth = {31,28,31,30,31,30,31,31,30,31,30,31};
	 
	    /**
	      * 天干地支之天干速查表
	      * @Array Of Property trans["甲","乙","丙","丁","戊","己","庚","辛","壬","癸"]
	      * @return Cn string 
	      */
	    private static String[] Gan = {"甲","乙","丙","丁","戊","己","庚","辛","壬","癸"};
	 
	    /**
	      * 天干地支之地支速查表
	      * @Array Of Property 
	      * @trans["子","丑","寅","卯","辰","巳","午","未","申","酉","戌","亥"]
	      * @return Cn string 
	      */
	    private static String[] Zhi = {"子","丑","寅","卯","辰","巳","午","未","申","酉","戌","亥"};
	 
	    /**
	      * 天干地支之地支速查表<=>生肖
	      * @Array Of Property 
	      * @trans["鼠","牛","虎","兔","龙","蛇","马","羊","猴","鸡","狗","猪"]
	      * @return Cn string 
	      */
	    private static String[] Animals= {"鼠","牛","虎","兔","龙","蛇","马","羊","猴","鸡","狗","猪"};
	 
	    /**
	      * 24节气速查表 
	      * 节气对应的月份是固定的
	      * 节气计算公式:Y*D+C-L
	      * Y:年份  D:基数0.2422 C:节气变量  L:闰年数(当前世纪到Y为止)
	      * @Array Of Property 
	      * @trans["小寒","大寒","立春","雨水","惊蛰","春分","清明","谷雨","立夏","小满","芒种","夏至","小暑","大暑","立秋","处暑","白露","秋分","寒露","霜降","立冬","小雪","大雪","冬至"]
	      * @return Cn string 
	      */
	    private static String[] solarTerm = {"小寒","大寒","立春","雨水","惊蛰","春分","清明","谷雨","立夏","小满","芒种","夏至","小暑","大暑","立秋","处暑","白露","秋分","寒露","霜降","立冬","小雪","大雪","冬至"};
	 
	    /**
	      * 1900-2100各年的24节气日期速查表
	      * @Array Of Property 
	      * @return 0x string For splice
	      */
	    private static String[] sTermInfo = {"9778397bd097c36b0b6fc9274c91aa","97b6b97bd19801ec9210c965cc920e","97bcf97c3598082c95f8c965cc920f",
	              "97bd0b06bdb0722c965ce1cfcc920f","b027097bd097c36b0b6fc9274c91aa","97b6b97bd19801ec9210c965cc920e",
	              "97bcf97c359801ec95f8c965cc920f","97bd0b06bdb0722c965ce1cfcc920f","b027097bd097c36b0b6fc9274c91aa",
	              "97b6b97bd19801ec9210c965cc920e","97bcf97c359801ec95f8c965cc920f","97bd0b06bdb0722c965ce1cfcc920f",
	              "b027097bd097c36b0b6fc9274c91aa","9778397bd19801ec9210c965cc920e","97b6b97bd19801ec95f8c965cc920f",
	              "97bd09801d98082c95f8e1cfcc920f","97bd097bd097c36b0b6fc9210c8dc2","9778397bd197c36c9210c9274c91aa",
	              "97b6b97bd19801ec95f8c965cc920e","97bd09801d98082c95f8e1cfcc920f","97bd097bd097c36b0b6fc9210c8dc2",
	              "9778397bd097c36c9210c9274c91aa","97b6b97bd19801ec95f8c965cc920e","97bcf97c3598082c95f8e1cfcc920f",
	              "97bd097bd097c36b0b6fc9210c8dc2","9778397bd097c36c9210c9274c91aa","97b6b97bd19801ec9210c965cc920e",
	              "97bcf97c3598082c95f8c965cc920f","97bd097bd097c35b0b6fc920fb0722","9778397bd097c36b0b6fc9274c91aa",
	              "97b6b97bd19801ec9210c965cc920e","97bcf97c3598082c95f8c965cc920f","97bd097bd097c35b0b6fc920fb0722",
	              "9778397bd097c36b0b6fc9274c91aa","97b6b97bd19801ec9210c965cc920e","97bcf97c359801ec95f8c965cc920f",
	              "97bd097bd097c35b0b6fc920fb0722","9778397bd097c36b0b6fc9274c91aa","97b6b97bd19801ec9210c965cc920e",
	              "97bcf97c359801ec95f8c965cc920f","97bd097bd097c35b0b6fc920fb0722","9778397bd097c36b0b6fc9274c91aa",
	              "97b6b97bd19801ec9210c965cc920e","97bcf97c359801ec95f8c965cc920f","97bd097bd07f595b0b6fc920fb0722",
	              "9778397bd097c36b0b6fc9210c8dc2","9778397bd19801ec9210c9274c920e","97b6b97bd19801ec95f8c965cc920f",
	              "97bd07f5307f595b0b0bc920fb0722","7f0e397bd097c36b0b6fc9210c8dc2","9778397bd097c36c9210c9274c920e",
	              "97b6b97bd19801ec95f8c965cc920f","97bd07f5307f595b0b0bc920fb0722","7f0e397bd097c36b0b6fc9210c8dc2",
	              "9778397bd097c36c9210c9274c91aa","97b6b97bd19801ec9210c965cc920e","97bd07f1487f595b0b0bc920fb0722",
	              "7f0e397bd097c36b0b6fc9210c8dc2","9778397bd097c36b0b6fc9274c91aa","97b6b97bd19801ec9210c965cc920e",
	              "97bcf7f1487f595b0b0bb0b6fb0722","7f0e397bd097c35b0b6fc920fb0722","9778397bd097c36b0b6fc9274c91aa",
	              "97b6b97bd19801ec9210c965cc920e","97bcf7f1487f595b0b0bb0b6fb0722","7f0e397bd097c35b0b6fc920fb0722",
	              "9778397bd097c36b0b6fc9274c91aa","97b6b97bd19801ec9210c965cc920e","97bcf7f1487f531b0b0bb0b6fb0722",
	              "7f0e397bd097c35b0b6fc920fb0722","9778397bd097c36b0b6fc9274c91aa","97b6b97bd19801ec9210c965cc920e",
	              "97bcf7f1487f531b0b0bb0b6fb0722","7f0e397bd07f595b0b6fc920fb0722","9778397bd097c36b0b6fc9274c91aa",
	              "97b6b97bd19801ec9210c9274c920e","97bcf7f0e47f531b0b0bb0b6fb0722","7f0e397bd07f595b0b0bc920fb0722",
	              "9778397bd097c36b0b6fc9210c91aa","97b6b97bd197c36c9210c9274c920e","97bcf7f0e47f531b0b0bb0b6fb0722",
	              "7f0e397bd07f595b0b0bc920fb0722","9778397bd097c36b0b6fc9210c8dc2","9778397bd097c36c9210c9274c920e",
	              "97b6b7f0e47f531b0723b0b6fb0722","7f0e37f5307f595b0b0bc920fb0722","7f0e397bd097c36b0b6fc9210c8dc2",
	              "9778397bd097c36b0b70c9274c91aa","97b6b7f0e47f531b0723b0b6fb0721","7f0e37f1487f595b0b0bb0b6fb0722",
	              "7f0e397bd097c35b0b6fc9210c8dc2","9778397bd097c36b0b6fc9274c91aa","97b6b7f0e47f531b0723b0b6fb0721",
	              "7f0e27f1487f595b0b0bb0b6fb0722","7f0e397bd097c35b0b6fc920fb0722","9778397bd097c36b0b6fc9274c91aa",
	              "97b6b7f0e47f531b0723b0b6fb0721","7f0e27f1487f531b0b0bb0b6fb0722","7f0e397bd097c35b0b6fc920fb0722",
	              "9778397bd097c36b0b6fc9274c91aa","97b6b7f0e47f531b0723b0b6fb0721","7f0e27f1487f531b0b0bb0b6fb0722",
	              "7f0e397bd097c35b0b6fc920fb0722","9778397bd097c36b0b6fc9274c91aa","97b6b7f0e47f531b0723b0b6fb0721",
	              "7f0e27f1487f531b0b0bb0b6fb0722","7f0e397bd07f595b0b0bc920fb0722","9778397bd097c36b0b6fc9274c91aa",
	              "97b6b7f0e47f531b0723b0787b0721","7f0e27f0e47f531b0b0bb0b6fb0722","7f0e397bd07f595b0b0bc920fb0722",
	              "9778397bd097c36b0b6fc9210c91aa","97b6b7f0e47f149b0723b0787b0721","7f0e27f0e47f531b0723b0b6fb0722",
	              "7f0e397bd07f595b0b0bc920fb0722","9778397bd097c36b0b6fc9210c8dc2","977837f0e37f149b0723b0787b0721",
	              "7f07e7f0e47f531b0723b0b6fb0722","7f0e37f5307f595b0b0bc920fb0722","7f0e397bd097c35b0b6fc9210c8dc2",
	              "977837f0e37f14998082b0787b0721","7f07e7f0e47f531b0723b0b6fb0721","7f0e37f1487f595b0b0bb0b6fb0722",
	              "7f0e397bd097c35b0b6fc9210c8dc2","977837f0e37f14998082b0787b06bd","7f07e7f0e47f531b0723b0b6fb0721",
	              "7f0e27f1487f531b0b0bb0b6fb0722","7f0e397bd097c35b0b6fc920fb0722","977837f0e37f14998082b0787b06bd",
	              "7f07e7f0e47f531b0723b0b6fb0721","7f0e27f1487f531b0b0bb0b6fb0722","7f0e397bd097c35b0b6fc920fb0722",
	              "977837f0e37f14998082b0787b06bd","7f07e7f0e47f531b0723b0b6fb0721","7f0e27f1487f531b0b0bb0b6fb0722",
	              "7f0e397bd07f595b0b0bc920fb0722","977837f0e37f14998082b0787b06bd","7f07e7f0e47f531b0723b0b6fb0721",
	              "7f0e27f1487f531b0b0bb0b6fb0722","7f0e397bd07f595b0b0bc920fb0722","977837f0e37f14998082b0787b06bd",
	              "7f07e7f0e47f149b0723b0787b0721","7f0e27f0e47f531b0b0bb0b6fb0722","7f0e397bd07f595b0b0bc920fb0722",
	              "977837f0e37f14998082b0723b06bd","7f07e7f0e37f149b0723b0787b0721","7f0e27f0e47f531b0723b0b6fb0722",
	              "7f0e397bd07f595b0b0bc920fb0722","977837f0e37f14898082b0723b02d5","7ec967f0e37f14998082b0787b0721",
	              "7f07e7f0e47f531b0723b0b6fb0722","7f0e37f1487f595b0b0bb0b6fb0722","7f0e37f0e37f14898082b0723b02d5",
	              "7ec967f0e37f14998082b0787b0721","7f07e7f0e47f531b0723b0b6fb0722","7f0e37f1487f531b0b0bb0b6fb0722",
	              "7f0e37f0e37f14898082b0723b02d5","7ec967f0e37f14998082b0787b06bd","7f07e7f0e47f531b0723b0b6fb0721",
	              "7f0e37f1487f531b0b0bb0b6fb0722","7f0e37f0e37f14898082b072297c35","7ec967f0e37f14998082b0787b06bd",
	              "7f07e7f0e47f531b0723b0b6fb0721","7f0e27f1487f531b0b0bb0b6fb0722","7f0e37f0e37f14898082b072297c35",
	              "7ec967f0e37f14998082b0787b06bd","7f07e7f0e47f531b0723b0b6fb0721","7f0e27f1487f531b0b0bb0b6fb0722",
	              "7f0e37f0e366aa89801eb072297c35","7ec967f0e37f14998082b0787b06bd","7f07e7f0e47f149b0723b0787b0721",
	              "7f0e27f1487f531b0b0bb0b6fb0722","7f0e37f0e366aa89801eb072297c35","7ec967f0e37f14998082b0723b06bd",
	              "7f07e7f0e47f149b0723b0787b0721","7f0e27f0e47f531b0723b0b6fb0722","7f0e37f0e366aa89801eb072297c35",
	              "7ec967f0e37f14998082b0723b06bd","7f07e7f0e37f14998083b0787b0721","7f0e27f0e47f531b0723b0b6fb0722",
	              "7f0e37f0e366aa89801eb072297c35","7ec967f0e37f14898082b0723b02d5","7f07e7f0e37f14998082b0787b0721",
	              "7f07e7f0e47f531b0723b0b6fb0722","7f0e36665b66aa89801e9808297c35","665f67f0e37f14898082b0723b02d5",
	              "7ec967f0e37f14998082b0787b0721","7f07e7f0e47f531b0723b0b6fb0722","7f0e36665b66a449801e9808297c35",
	              "665f67f0e37f14898082b0723b02d5","7ec967f0e37f14998082b0787b06bd","7f07e7f0e47f531b0723b0b6fb0721",
	              "7f0e36665b66a449801e9808297c35","665f67f0e37f14898082b072297c35","7ec967f0e37f14998082b0787b06bd",
	              "7f07e7f0e47f531b0723b0b6fb0721","7f0e26665b66a449801e9808297c35","665f67f0e37f1489801eb072297c35",
	              "7ec967f0e37f14998082b0787b06bd","7f07e7f0e47f531b0723b0b6fb0721","7f0e27f1487f531b0b0bb0b6fb0722"};
	 
	    /**
	      * 数字转中文速查表
	      * @Array Of Property 
	      * @trans ['日','一','二','三','四','五','六','七','八','九','十']
	      * @return Cn string 
	      */
	    private static String[] nStr1 = {"日","一","二","三","四","五","六","七","八","九","十"};
	 
	    /**
	      * 日期转农历称呼速查表
	      * @Array Of Property 
	      * @trans ["初","十","廿","卅"]
	      * @return Cn string 
	      */
	    private static String[] nStr2 = {"初","十","廿","卅"};
	 
	    /**
	      * 月份转农历称呼速查表
	      * @Array Of Property 
	      * @trans ["正","一","二","三","四","五","六","七","八","九","十","冬","腊"]
	      * @return Cn string 
	      */
	    private static String[] nStr3 = {"正月","二月","三月","四月","五月","六月","七月","八月","九月","十月","冬月","腊月"};
	    /**
	     * 每个月最后一天之前的都算该星座
	     */
	    private final static int[] astroArr = { 20, 19, 21, 20, 21, 22, 23,23, 23, 24, 23, 22 };
	    private final static String[] asrtoNameArr = { "摩羯座","水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座","天蝎座", "射手座", "摩羯座" };
	    /**
	      * 返回农历y年一整年的总天数
	      * @param lunar Year
	      * @return Number
	      * @eg:var count = calendar.lYearDays(1987) ;//count=387
	      */
	    public static int solarYearDays(int year) {
	        int sum = 29*12;
	        for(int i=0x8000; i>0x8; i>>=1) { 
	        	sum += (lunarInfo[year-1900]&0xfff0&i)!=0 ? 1: 0; 
        	}
	        return sum+leapDays(year);
	    }
	 
	    /**
	      * 返回农历y年闰月是哪个月；若y年没有闰月 则返回0
	      * @param lunar Year
	      * @return Number (0-12)
	      * @eg:var leapMonth = calendar.leapMonth(1987) ;//leapMonth=6
	      */
	    public static int leapMonth(int year) { //闰字编码 \u95f0
	        return lunarInfo[year-1900] & 0xf;
	    }
	 
	    /**
	      * 返回农历y年闰月的天数 若该年没有闰月则返回0
	      * @param lunar Year
	      * @return Number (0、29、30)
	      * @eg:var leapMonthDay = calendar.leapDays(1987) ;//leapMonthDay=29
	      */
	    public static int leapDays(int year) {
	    	 
	         
	        if( leapMonth(year) !=0 )  { 
	            return((lunarInfo[year - 1900] & 0xf0000) == 0 ? 29 : 30); 
	        }
	        return (0);
	    }
	 
	    /**
	      * 返回农历y年m月（非闰月）的总天数，计算m为闰月时的天数请使用leapDays方法
	      * @param lunar Year
	      * @return Number (-1、29、30)
	     * @throws Exception 
	      * @eg:var MonthDay = calendar.monthDays(1987,9) ;//MonthDay=29
	      */
	    public static int lunarMonthDays(int year,int month) throws Exception {
	    	  
	       //月份参数从1至12,抛异常
	        if ((month > 12) || (month < 1)) {  
	            throw(new Exception("月份有错！"));  
	        } 
	        int bit = 1 << (16-month);  
	        if(((lunarInfo[year - 1900] & 0x0FFFF)&bit)==0){  
	            return 29;  
	        }else {  
	            return 30;  
	        }  
	    }
	 
	    /**
	      * 返回公历(!)y年m月的天数
	      * @param solar Year
	      * @return Number (-1、28、29、30、31)
	     * @throws Exception 
	      * @eg:var solarMonthDay = calendar.leapDays(1987) ;//solarMonthDay=30
	      */
	    public static int solarDays(int year ,int month) throws Exception {
	        if(month > 12 || month < 1) {
	        	 throw(new Exception("月份有错！"));  
	        } //若参数错误 返回-1
	        int ms = month - 1;
	        if(ms == 1) { //2月份的闰平规律测算后确认返回28或29
	            return(((year%4 == 0) && (year%100 != 0) || (year%400 == 0))? 29: 28);
	        }else {
	            return(solarMonth[ms]);
	        }
	    }
	 
	    /**
	     * 农历年份转换为干支纪年
	     * @param  lYear 农历年的年份数
	     * @return Cn string
	     */
	    public static String toGanZhiYear(int lYear) {
	        int ganKey = (lYear - 3) % 10;
	        int zhiKey = (lYear - 3) % 12;
	        if(ganKey == 0) {
	        	ganKey = 10;//如果余数为0则为最后一个天干
	        } 
	        if(zhiKey == 0) {
	        	zhiKey = 12;//如果余数为0则为最后一个地支
	        } 
	        return Gan[ganKey-1] + Zhi[zhiKey-1];
	        
	    }
	 
	    /**
	     * 公历月、日判断所属星座
	     * @param  cMonth [description]
	     * @param  cDay [description]
	     * @return Cn string
	     */
	    public static String toAstro(int cMonth,int cDay) {
	        return cDay < astroArr[cMonth - 1] ? asrtoNameArr[cMonth - 1]
	                : asrtoNameArr[cMonth];
	    }
	 
	    /**
	      * 传入offset偏移量返回干支
	      * @param offset 相对甲子的偏移量
	      * @return Cn string
	      */
	    public static String toGanZhi(int offset) {
	        return Gan[offset%10] + Zhi[offset%12];
	    }
	 
	    /**
	      * 传入公历(!)y年获得该年第n个节气的公历日期
	      * @param y公历年(1900-2100)；n二十四节气中的第几个节气(1~24)；从n=1(小寒)算起 
	      * @return day Number
	     * @throws Exception 
	      * @eg:var _24 = calendar.getTerm(1987,3) ;//_24=4;意即1987年2月4日立春
	      */
	    //TODO:返回信息有问题,待修改
	    public static int getTerm(int year,int index) throws Exception {
	        if(year<1900 || year>2100) {
	        	throw(new Exception("年份有错！")); 
	        	}
	        if(index < 1 || index > 24) {
	        	throw(new Exception("节气角标有错！")); 
	        	}
	        String _table = sTermInfo[year-1900];
	        String[] _info = {
	        		String.valueOf(Integer.parseInt(_table.substring(0,5),16)),
	        		String.valueOf(Integer.parseInt(_table.substring(5,10),16)),
    				String.valueOf(Integer.parseInt(_table.substring(10,15),16)),
					String.valueOf(Integer.parseInt(_table.substring(15,20),16)),
					String.valueOf(Integer.parseInt(_table.substring(20,25),16)),
					String.valueOf(Integer.parseInt(_table.substring(25,30),16))
	        };
	        String[] _calday = {
	            _info[0].substring(0,1),
	            _info[0].substring(1,3),
	            _info[0].substring(3,4),
	            _info[0].substring(4,6),
	            
	            _info[1].substring(0,1),
	            _info[1].substring(1,3),
	            _info[1].substring(3,4),
	            _info[1].substring(4,6),
	            
	            _info[2].substring(0,1),
	            _info[2].substring(1,3),
	            _info[2].substring(3,4),
	            _info[2].substring(4,6),
	            
	            _info[3].substring(0,1),
	            _info[3].substring(1,3),
	            _info[3].substring(3,4),
	            _info[3].substring(4,6),
	            
	            _info[4].substring(0,1),
	            _info[4].substring(1,3),
	            _info[4].substring(3,4),
	            _info[4].substring(4,6),
	            
	            _info[5].substring(0,1),
	            _info[5].substring(1,3),
	            _info[5].substring(3,4),
	            _info[5].substring(4,6),
	        };
	       // return parseInt(_calday[n-1]);
	        return Integer.parseInt(_calday[index-1]);
	    }	
	    
	    /** 
	     * 计算两个阳历日期相差的天数。 
	     * @param startDate 开始时间 
	     * @param endDate 截至时间 
	     * @return (int)天数 
	     * @author liu 2017-3-2 
	     */  
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
	      * 传入农历数字月份返回汉语通俗表示法
	      * @param lunar month
	      * @return Cn string
	      * @eg:var cnMonth = calendar.toChinaMonth(12) ;//cnMonth='腊月'
	      */
	    public static String toChinaMonth(int month) { // 月 => \u6708
	        if(month>12 || month<1) {
	        	throw new RuntimeException("月份信息有误！");
			} //若参数错误 返回-1
	        return nStr3[month-1];
	    }
	 
	    /**
	      * 传入农历日期数字返回汉字表示法
	      * @param lunar day
	      * @return Cn string
	      * @eg:var cnDay = calendar.toChinaDay(21) ;//cnMonth='廿一'
	      */
	    public static String toChinaDay(int day){ //日 => \u65e5
	        String s;
	        switch (day) {
	            case 10:
	            s = "初十"; break;
	        case 20:
	            s = "廿十"; break;
	        case 30:
	            s = "卅十"; break;
	        default :
	            s =nStr2[day/10] + nStr1[day%10];
	        }
	        return(s);
	    }
	 
	    /**
	      * 年份转生肖[!仅能大致转换] => 精确划分生肖分界线是“立春”
	      * @param y year
	      * @return Cn string
	      * @eg:var animal = calendar.getAnimal(1987) ;//animal='兔'
	      */
	   public static String getAnimal(int year) {
	        return Animals[(year - 4) % 12];
	    }
	 
	    /**
	      * 传入阳历年月日获得详细的公历、农历object信息 <=>JSON
	      * @param y  solar year
	      * @param m  solar month
	      * @param d  solar day
	      * @return JSON object
	     * @throws Exception 
	      * @eg:console.log(calendar.solar2lunar(1987,11,01));
	      */
	    public static String solar2lunar(String solarDate) throws Exception { //参数区间1900.1.31~2100.12.31
	    	int solarYear = Integer.parseInt(solarDate.substring(0, 4));  
	    	int solarMonth = Integer.parseInt(solarDate.substring(4, 6));  
	    	int solarDay = Integer.parseInt(solarDate.substring(6, 8));  
	    	
	    	//该日期所属的星座
	        String astro = toAstro(solarMonth,solarDay);
	    	int i;  
	        int temp = 0;  
	        int lunarYear;  
	        int lunarMonth; //农历月份  
	        int lunarDay; //农历当月第几天  
	        boolean isLeapYear ;
	        boolean leapMonthFlag =false;  
	        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");  
	        Date myDate = null;  
	        Date startDate = null;  
	        try {  
	            myDate = formatter.parse(solarDate);  
	            startDate = formatter.parse(START_DATE);  
	        } catch (ParseException e) {  

	        }  
	        Calendar calendar = Calendar.getInstance();
	        calendar.setTime(myDate);
	        int iWeek = calendar.get(Calendar.DAY_OF_WEEK);
	        
	        //星期几
	        String week = "星期"+nStr1[iWeek-1];
	        int offset = daysBetween(myDate,startDate);  
	        for (i = MIN_YEAR; i <= MAX_YEAR; i++){  
	            temp = solarYearDays(i);  //求当年农历年天数  
	            if (offset - temp < 1){  
	                break;  
	            }else{  
	                offset -= temp;  
	            }  
	        }  
	        lunarYear = i;  
	  
	        int leapMonth = leapMonth(lunarYear);//计算该年闰哪个月  
	        //设定当年是否有闰月  
	        if (leapMonth > 0){  
	            isLeapYear = true;  
	        }else{  
	            isLeapYear = false;  
	        }  
	  
	        for (i = 1;  i<=12; i++) {  
	            if(i==leapMonth+1 && isLeapYear){  
	                temp = leapDays(lunarYear);  
	                isLeapYear = false;  
	                leapMonthFlag = true;  
	                i--;  
	            }else{  
	                temp = lunarMonthDays(lunarYear, i);  
	            }  
	            offset -= temp;  
	            if(offset<=0){  
	                break;  
	            }  
	        }  
	  
	        offset += temp;  
	        lunarMonth = i;  //农历月
	        lunarDay = offset;//农历日
	  
	        return "阴历："+lunarYear+"年"+(leapMonthFlag&(lunarMonth==leapMonth)?"闰":"")+lunarMonth+"月"+lunarDay+"日";  
	    }  
	 
	    /**
	      * 传入农历年月日以及传入的月份是否闰月获得详细的公历、农历object信息 <=>JSON
	      * @param y  lunar year
	      * @param m  lunar month
	      * @param d  lunar day
	      * @param isLeapMonth  lunar month is leap or not.[如果是农历闰月第四个参数赋值true即可]
	      * @return JSON object
	      * @eg:console.log(calendar.lunar2solar(1987,9,10));
	      */
	    public static String lunar2solar(String lunarDate, boolean leapMonthFlag) throws Exception{  
	        int lunarYear = Integer.parseInt(lunarDate.substring(0, 4));  
	        int lunarMonth = Integer.parseInt(lunarDate.substring(4, 6));  
	        int lunarDay = Integer.parseInt(lunarDate.substring(6, 8));  
	  
//	        checkLunarDate(lunarYear, lunarMonth, lunarDay, leapMonthFlag);  
	  
	        int offset = 0;  
	  
	        for (int i = MIN_YEAR; i < lunarYear; i++) {  
	            int yearDaysCount = solarYearDays(i); // 求阴历某年天数  
	            offset += yearDaysCount;  
	        }  
	        //计算该年闰几月  
	        int leapMonth = leapMonth(lunarYear);  
	  
	        if(leapMonthFlag & leapMonth != lunarMonth){  
	            throw(new Exception("您输入的闰月标志有误！"));  
	        }  
	  
	        //当年没有闰月或月份早于闰月或和闰月同名的月份  
	        if(leapMonth==0|| (lunarMonth < leapMonth) || (lunarMonth==leapMonth && !leapMonthFlag)){  
	            for (int i = 1; i < lunarMonth; i++) {  
	                int tempMonthDaysCount = lunarMonthDays(lunarYear, i);  
	                offset += tempMonthDaysCount;  
	            }  
	  
	            // 检查日期是否大于最大天  
	            if (lunarDay > lunarMonthDays(lunarYear, lunarMonth)) {  
	                throw(new Exception("不合法的农历日期！"));  
	            }  
	            offset += lunarDay; // 加上当月的天数  
	        }else{//当年有闰月，且月份晚于或等于闰月  
	            for (int i = 1; i < lunarMonth; i++) {  
	                int tempMonthDaysCount = lunarMonthDays(lunarYear, i);  
	                offset += tempMonthDaysCount;  
	            }  
	            if (lunarMonth>leapMonth) {  
	                int temp = leapDays(lunarYear); // 计算闰月天数  
	                offset += temp; // 加上闰月天数  
	  
	                if (lunarDay > lunarMonthDays(lunarYear, lunarMonth)) {  
	                    throw(new Exception("不合法的农历日期！"));  
	                }  
	                offset += lunarDay;  
	            }else { // 如果需要计算的是闰月，则应首先加上与闰月对应的普通月的天数  
	                // 计算月为闰月  
	                int temp = lunarMonthDays(lunarYear, lunarMonth); // 计算非闰月天数  
	                offset += temp;  
	  
	                if (lunarDay > leapDays(lunarYear)) {  
	                    throw(new Exception("不合法的农历日期！"));  
	                }   
	                offset += lunarDay;  
	            }  
	        }  
	  
	        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");  
	        Date myDate = null;  
	        myDate = formatter.parse(START_DATE);  
	        Calendar c = Calendar.getInstance();  
	        c.setTime(myDate);  
	        c.add(Calendar.DATE, offset);  
	        myDate = c.getTime();  
	  
	        return formatter.format(myDate); 
	}
}
