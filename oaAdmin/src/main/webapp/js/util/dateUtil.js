/**
 * 日期工具类
 */
var DateUtil = function() {
	/***************************************************************************
	 * 获得当前时间
	 */
	this.getCurrentDate = function() {
		return new Date();
	};
	/***************************************************************************
	 * 获得本周起止时间
	 */
	this.getCurrentWeek = function() {
		// 起止日期数组
		var startStop = new Array();
		// 获取当前时间
		var currentDate = this.getCurrentDate();
		// 返回date是一周中的某一天
		var week = currentDate.getDay();

		// 一天的毫秒数
		var millisecond = 1000 * 60 * 60 * 24;
		// 减去的天数
		var minusDay = week != 0 ? week - 1 : 6;
		// alert(minusDay);
		// 本周 周一
		var monday = new Date(currentDate.getTime() - (minusDay * millisecond));
		// 本周 周日
		var sunday = new Date(monday.getTime() + (6 * millisecond));
		// 添加本周时间
		startStop.push(monday);// 本周起始时间
		// 添加本周最后一天时间
		startStop.push(sunday);// 本周终止时间
		// 返回
		return startStop;
	};

	/***************************************************************************
	 * 获得本月的起止时间
	 */
	this.getCurrentMonth = function() {
		// 起止日期数组
		var startStop = new Array();
		// 获取当前时间
		var currentDate = this.getCurrentDate();
		// 获得当前月份0-11
		var currentMonth = currentDate.getMonth();
		// 获得当前年份4位年
		var currentYear = currentDate.getFullYear();
		// 求出本月第一天
		var firstDay = new Date(currentYear, currentMonth, 1);

		// 当为12月的时候年份需要加1
		// 月份需要更新为0 也就是下一年的第一个月
		if (currentMonth == 11) {
			currentYear++;
			currentMonth = 0;// 就为
		} else {
			// 否则只是月份增加,以便求的下一月的第一天
			currentMonth++;
		}

		// 一天的毫秒数
		var millisecond = 1000 * 60 * 60 * 24;
		// 下月的第一天
		var nextMonthDayOne = new Date(currentYear, currentMonth, 1);
		// 求出上月的最后一天
		var lastDay = new Date(nextMonthDayOne.getTime() - millisecond);

		// 添加至数组中返回
		startStop.push(firstDay);
		startStop.push(lastDay);
		// 返回
		return startStop;
	};

	/***************************************************************************
	 * 得到本季度开始的月份
	 * 
	 * @param month
	 *            需要计算的月份
	 **************************************************************************/
	this.getQuarterSeasonStartMonth = function(month) {
		var spring = 0; // 春
		var summer = 3; // 夏
		var fall = 6; // 秋
		var winter = 9;// 冬
		// 月份从0-11
		if (month < 3) {
			return spring;
		}

		if (month < 6) {
			return summer;
		}

		if (month < 9) {
			return fall;
		}

		return winter;
	};

	/**
	 * 获得该月的天数
	 * 
	 * @param year年份
	 * @param month月份
	 */
	this.getMonthDays = function(year, month) {
		// 本月第一天 1-31
		var relativeDate = new Date(year, month, 1);
		// 获得当前月份0-11
		var relativeMonth = relativeDate.getMonth();
		// 获得当前年份4位年
		var relativeYear = relativeDate.getFullYear();

		// 当为12月的时候年份需要加1
		// 月份需要更新为0 也就是下一年的第一个月
		if (relativeMonth == 11) {
			relativeYear++;
			relativeMonth = 0;
		} else {
			// 否则只是月份增加,以便求的下一月的第一天
			relativeMonth++;
		}
		// 一天的毫秒数
		var millisecond = 1000 * 60 * 60 * 24;
		// 下月的第一天
		var nextMonthDayOne = new Date(relativeYear, relativeMonth, 1);
		// 返回得到上月的最后一天,也就是本月总天数
		return new Date(nextMonthDayOne.getTime() - millisecond).getDate();
	};

	/**
	 * 获得本季度的起止日期
	 */
	this.getCurrentSeason = function() {
		// 起止日期数组
		var startStop = new Array();
		// 获取当前时间
		var currentDate = this.getCurrentDate();
		// 获得当前月份0-11
		var currentMonth = currentDate.getMonth();
		// 获得当前年份4位年
		var currentYear = currentDate.getFullYear();
		// 获得本季度开始月份
		var quarterSeasonStartMonth = this
				.getQuarterSeasonStartMonth(currentMonth);
		// 获得本季度结束月份
		var quarterSeasonEndMonth = quarterSeasonStartMonth + 2;

		// 获得本季度开始的日期
		var quarterSeasonStartDate = new Date(currentYear,
				quarterSeasonStartMonth, 1);
		// 获得本季度结束的日期
		var quarterSeasonEndDate = new Date(currentYear, quarterSeasonEndMonth,
				this.getMonthDays(currentYear, quarterSeasonEndMonth));
		// 加入数组返回
		startStop.push(quarterSeasonStartDate);
		startStop.push(quarterSeasonEndDate);
		// 返回
		return startStop;
	};

	/***************************************************************************
	 * 得到本年的起止日期
	 * 
	 */
	this.getCurrentYear = function() {
		// 起止日期数组
		var startStop = new Array();
		// 获取当前时间
		var currentDate = this.getCurrentDate();
		// 获得当前年份4位年
		var currentYear = currentDate.getFullYear();

		// 本年第一天
		var currentYearFirstDate = new Date(currentYear, 0, 1);
		// 本年最后一天
		var currentYearLastDate = new Date(currentYear, 11, 31);
		// 添加至数组
		startStop.push(currentYearFirstDate);
		startStop.push(currentYearLastDate);
		// 返回
		return startStop;
	};

	/**
	 * 返回上一个月的第一天Date类型
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 */
	this.getPriorMonthFirstDay = function(year, month) {
		// 年份为0代表,是本年的第一月,所以不能减
		if (month == 0) {
			month = 11;// 月份为上年的最后月份
			year--;// 年份减1
			return new Date(year, month, 1);
		}
		// 否则,只减去月份
		month--;
		return new Date(year, month, 1);
	};

	/***************************************************************************
	 * 获得上一月的起止日期
	 **************************************************************************/
	this.getPreviousMonth = function() {
		// 起止日期数组
		var startStop = new Array();
		// 获取当前时间
		var currentDate = this.getCurrentDate();
		// 获得当前月份0-11
		var currentMonth = currentDate.getMonth();
		// 获得当前年份4位年
		var currentYear = currentDate.getFullYear();
		// 获得上一个月的第一天
		var priorMonthFirstDay = this.getPriorMonthFirstDay(currentYear,
				currentMonth);
		// 获得上一月的最后一天
		var priorMonthLastDay = new Date(priorMonthFirstDay.getFullYear(),
				priorMonthFirstDay.getMonth(), this.getMonthDays(
						priorMonthFirstDay.getFullYear(), priorMonthFirstDay
								.getMonth()));
		// 添加至数组
		startStop.push(priorMonthFirstDay);
		startStop.push(priorMonthLastDay);
		// 返回
		return startStop;
	};

	/***************************************************************************
	 * 获得上一周的起止日期
	 **************************************************************************/
	this.getPreviousWeek = function() {
		// 起止日期数组
		var startStop = new Array();
		// 获取当前时间
		var currentDate = this.getCurrentDate();
		// 返回date是一周中的某一天
		var week = currentDate.getDay();
		// 一天的毫秒数
		var millisecond = 1000 * 60 * 60 * 24;
		// 减去的天数
		var minusDay = week != 0 ? week - 1 : 6;
		// 获得当前周的第一天
		var currentWeekDayOne = new Date(currentDate.getTime()
				- (millisecond * minusDay));
		// 上周最后一天即本周开始的前一天
		var priorWeekLastDay = new Date(currentWeekDayOne.getTime()
				- millisecond);
		// 上周的第一天
		var priorWeekFirstDay = new Date(priorWeekLastDay.getTime()
				- (millisecond * 6));

		// 添加至数组
		startStop.push(priorWeekFirstDay);
		startStop.push(priorWeekLastDay);

		return startStop;
	};

	/**
	 * 得到上季度的起始日期 year 这个年应该是运算后得到的当前本季度的年份 month 这个应该是运算后得到的当前季度的开始月份
	 */
	this.getPriorSeasonFirstDay = function(year, month) {
		var spring = 0; // 春
		var summer = 3; // 夏
		var fall = 6; // 秋
		var winter = 9;// 冬
		// 月份从0-11
		switch (month) {// 季度的其实月份
		case spring:
			// 如果是第一季度则应该到去年的冬季
			year--;
			month = winter;
			break;
		case summer:
			month = spring;
			break;
		case fall:
			month = summer;
			break;
		case winter:
			month = fall;
			break;
		}
		;
		return new Date(year, month, 1);
	};

	/***************************************************************************
	 * 得到上季度的起止日期
	 **************************************************************************/
	this.getPreviousSeason = function() {
		// 起止日期数组
		var startStop = new Array();
		// 获取当前时间
		var currentDate = this.getCurrentDate();
		// 获得当前月份0-11
		var currentMonth = currentDate.getMonth();
		// 获得当前年份4位年
		var currentYear = currentDate.getFullYear();
		// 上季度的第一天
		var priorSeasonFirstDay = this.getPriorSeasonFirstDay(currentYear,
				currentMonth);
		// 上季度的最后一天
		var priorSeasonLastDay = new Date(priorSeasonFirstDay.getFullYear(),
				priorSeasonFirstDay.getMonth() + 2, this.getMonthDays(
						priorSeasonFirstDay.getFullYear(), priorSeasonFirstDay
								.getMonth() + 2));
		// 添加至数组
		startStop.push(priorSeasonFirstDay);
		startStop.push(priorSeasonLastDay);
		return startStop;
	};

	/***************************************************************************
	 * 得到去年的起止日期
	 **************************************************************************/
	this.getPreviousYear = function() {
		// 起止日期数组
		var startStop = new Array();
		// 获取当前时间
		var currentDate = this.getCurrentDate();
		// 获得当前年份4位年
		var currentYear = currentDate.getFullYear();
		currentYear--;
		var priorYearFirstDay = new Date(currentYear, 0, 1);
		var priorYearLastDay = new Date(currentYear, 11, 1);
		// 添加至数组
		startStop.push(priorYearFirstDay);
		startStop.push(priorYearLastDay);
		return startStop;
	};
};

// 对Date的扩展，将 Date 转化为指定格式的String
// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，
// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)
// 例子：
// (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423
// (new Date()).Format("yyyy-M-d h:m:s.S") ==> 2006-7-2 8:9:4.18
Date.prototype.Format = function(fmt) { // author: meizz
	var o = {
		"M+" : this.getMonth() + 1, // 月份
		"d+" : this.getDate(), // 日
		"h+" : this.getHours(), // 小时
		"m+" : this.getMinutes(), // 分
		"s+" : this.getSeconds(), // 秒
		"q+" : Math.floor((this.getMonth() + 3) / 3), // 季度
		"S" : this.getMilliseconds()
	// 毫秒
	};
	if (/(y+)/.test(fmt))
		fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "")
				.substr(4 - RegExp.$1.length));
	for ( var k in o)
		if (new RegExp("(" + k + ")").test(fmt))
			fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k])
					: (("00" + o[k]).substr(("" + o[k]).length)));
	return fmt;
};

//计算时间差，否则每个地方自己写很麻烦
//开始时间，结束时间，返回类别
var getDateDiff = function (beginDate,endDate,diffType) {
	
    var a = new Date(beginDate.replace(/-/g, '/'));
    var b = new Date(endDate.replace(/-/g, '/'));
    var d = b.getTime() - a.getTime();
    
    //作为除数的数字
    var timeType =1;
    switch (diffType) {
        case"second":
            timeType =1000;
        break;
        case"minute":
            timeType =1000*60;
        break;
        case"hour":
            timeType =1000*3600;
        break;
        case"day":
            timeType =1000*3600*24;
        break;
        default:
        break;
    }
    d = parseInt(d / parseInt(timeType));
    return d;
}

//调用方法：
//var time1 = new Date().format("yyyy-MM-dd HH:mm:ss");     
//var time2 = new Date().format("yyyy-MM-dd");   