package com.ule.oa.base.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ule.oa.base.mapper.AnnualVacationMapper;
import com.ule.oa.base.po.AnnualVacation;
import com.ule.oa.base.po.AttnSignRecord;
import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.AnnualVacationService;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.PageModel;

/**
 * @ClassName: 每年假期表
 * @Description: 每年假期表
 * @author yangjie
 * @date 2017年6月16日
 */
@Service
public class AnnualVacationServiceImpl implements AnnualVacationService {
	
	@Autowired
	private AnnualVacationMapper annualVacationMapper;
	@Resource
	private ConfigService configService;

	@Override
	public List<AnnualVacation> getListByCondition(AnnualVacation vacation) {
		return annualVacationMapper.getListByCondition(vacation);
	}

	@Override
	public boolean judgeWorkOrNot(Date date) {
		AnnualVacation vacation = new AnnualVacation();
		vacation.setAnnualDate(date);
		List<AnnualVacation> list = annualVacationMapper.getListByCondition(vacation);
		if(list!=null&&list.size()>0){
			//判断是否是工作日
			if(list.get(0).getType().intValue()==AnnualVacation.YYPE_WORK.intValue()){
				return true;
			}else{
				return false;
			}
		}
		//判断是否是周末
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY
				||cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
			return false;
		 }
		return true;
	}
	
	@Override
	public boolean judgeWorkOrNot(Map<String,AnnualVacation> map, String date) {
		if(map.containsKey(date)){
			//判断是否是工作日
			if(map.get(date).getType().intValue()==AnnualVacation.YYPE_WORK.intValue()){
				return true;
			}else{
				return false;
			}
		}
		//判断是否是周末
		Calendar cal = Calendar.getInstance();
		cal.setTime(DateUtils.parse(date, DateUtils.FORMAT_SHORT));
		if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY
				||cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
			return false;
		 }
		return true;
	}

	@Override
	public boolean judgeVacation(Date date) {
		AnnualVacation vacation = new AnnualVacation();
		vacation.setAnnualDate(date);
		List<AnnualVacation> list = annualVacationMapper.getListByCondition(vacation);
		if(list!=null&&list.size()>0){
			//判断是否是工作日
			if(list.get(0).getType().intValue()==AnnualVacation.YYPE_WORK.intValue()){
				return true;
			}else{
				return false;
			}
		}
		return true;
	}

	@Override
	public Map<String, Boolean> judgeWorkOrNot(String month) {
		Map<String, Boolean> result = new HashMap<String, Boolean>();
		AnnualVacation vacation = new AnnualVacation();
		Date startDate = DateUtils.parse(month+"-01", DateUtils.FORMAT_SHORT);
		vacation.setStartDate(startDate);
		//获取月的最后一天
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		calendar.add(Calendar.MONTH, 1);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		Date monthEnd = calendar.getTime();
		vacation.setEndDate(monthEnd);
		List<AnnualVacation> list = annualVacationMapper.getListByCondition(vacation);
		//判断当月每天周末
		while(true){
			//判断是否是周末
			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			result.put(DateUtils.format(startDate, DateUtils.FORMAT_SHORT), true);
			if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY
					||cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
				result.put(DateUtils.format(startDate, DateUtils.FORMAT_SHORT), false);
			}
			startDate = DateUtils.addDay(startDate, 1);
			if(startDate.getTime()>vacation.getEndDate().getTime()){
				break;
			}
		}
		//循环当月假期表
		for(AnnualVacation annualVacation:list){
			if(annualVacation.getType().intValue()==AnnualVacation.YYPE_WORK.intValue()){
				result.put(DateUtils.format(annualVacation.getAnnualDate(), DateUtils.FORMAT_SHORT), true);
			}else{
				result.put(DateUtils.format(annualVacation.getAnnualDate(), DateUtils.FORMAT_SHORT), false);
			}
		}
		return result;
	}
	
	@Override
	public Map<Date, Boolean> judgeWorkOrNotByPriod(Date startDate,Date endDate) {
		Map<Date, Boolean> result = new HashMap<Date, Boolean>();
		AnnualVacation vacation = new AnnualVacation();
		vacation.setStartDate(startDate);
		vacation.setEndDate(endDate);
		List<AnnualVacation> list = annualVacationMapper.getListByCondition(vacation);
		//判断当月每天周末
		Calendar cal = Calendar.getInstance();
		while(true){
			//判断是否是周末
			cal.setTime(startDate);
			result.put(startDate, true);
			if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
				result.put(startDate, false);
			}
			startDate = DateUtils.addDay(startDate, 1);
			if(startDate.getTime()>vacation.getEndDate().getTime()){
				break;
			}
		}
		//循环当月假期表
		for(AnnualVacation annualVacation:list){
			if(annualVacation.getType().intValue()==AnnualVacation.YYPE_WORK.intValue()){
				result.put(annualVacation.getAnnualDate(), true);
			}else{
				result.put(annualVacation.getAnnualDate(), false);
			}
		}
		return result;
	}
	
	/**
	  * getCurrVacttionBySubAndType(根据指定的节日名称和类型获取当年当年的节日信息)
	  * @Title: getCurrVacttionBySubAndType
	  * @Description: 根据指定的节日名称和类型获取当年当年的节日信息(type:1-工作日、2-年休假、3-法定节假日、4-节假日)
	  * @param vacation
	  * @return    设定文件
	  * List<AnnualVacation>    返回类型
	  * @throws
	 */
	@Override
	public List<AnnualVacation> getCurrVacttionBySubAndType(AnnualVacation vacation){
		vacation.setYear(Integer.parseInt(DateUtils.getYear(new Date())));
		
		return annualVacationMapper.getListByCondition(vacation);
	}

	@Override
	public Date get5WorkingDayNextmonth(int num) {
		String fristDay = DateUtils.getNow("yyyy-MM") + "-01";
		Date fristDate = DateUtils.parse(fristDay,DateUtils.FORMAT_SHORT);
		int count = 0;
		while(true){
			if(judgeWorkOrNot(fristDate)){
				count = count+1;
			}
			if(count>=num){
				break;
			}
			fristDate = DateUtils.addDay(fristDate, 1);
		}
		return fristDate;
	}

	@Override
	public Date getWorkingDayOfMonth(Date anyDay,int num) {
		String fristDay = DateUtils.format(anyDay, "yyyy-MM") + "-01";
		Date fristDate = DateUtils.parse(fristDay,DateUtils.FORMAT_SHORT);
		int count = 1;
		while(true){
			if(judgeWorkOrNot(fristDate)){
				count = count+1;
			}
			if(count>=num){
				break;
			}
			fristDate = DateUtils.addDay(fristDate, 1);
		}
		return fristDate;
	}
	
	@Override
	public Date getWorkingDayPre(int num, Date date) {
		int count = 0;
		while(true){
			if(judgeWorkOrNot(date)){
				count = count+1;
			}
			if(count>=num){
				break;
			}
			date = DateUtils.addDay(date,-1);
		}
		return date;
	}
	
	@Override
	public Date getWorkingDayNext(int num, Date date) {
		int count = 0;
		while(true){
			if(judgeWorkOrNot(date)){
				count = count+1;
			}
			if(count>=num){
				break;
			}
			date = DateUtils.addDay(date,1);
		}
		return date;
	}

	@Override
	public boolean check5WorkingDayNextmonth(String applyDate,boolean flag) {
		//次月5个工作日
		Config configCondition = new Config();
		configCondition.setCode("timeLimit5");
		if(flag){
			configCondition.setCode("timeLimit7");
		}
		List<Config> list = configService.getListByCondition(configCondition);
		int num = Integer.valueOf(list.get(0).getDisplayCode());
		String nowDate10 = DateUtils.format(get5WorkingDayNextmonth(num), DateUtils.FORMAT_SHORT);
		int nowCountDays = DateUtils.getIntervalDays(DateUtils.parse(nowDate10, DateUtils.FORMAT_SHORT),DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_SHORT),DateUtils.FORMAT_SHORT));
		if(nowCountDays > 0) {
			String nowDate01 = DateUtils.getNow("yyyy-MM") + "-01";
			int nowCountDays01 = DateUtils.getIntervalDays(DateUtils.parse(nowDate01, DateUtils.FORMAT_SHORT),DateUtils.parse(applyDate,DateUtils.FORMAT_SHORT));
			if(nowCountDays01 < 0) {
				return true;
			}
		} else {
			String nowDate01 = DateUtils.getNow("yyyy-MM") + "-01";
			Date addDate = DateUtils.addMonth(DateUtils.parse(nowDate01, DateUtils.FORMAT_SHORT), -1);
			int nowCountDays01 = DateUtils.getIntervalDays(addDate,DateUtils.parse(applyDate,DateUtils.FORMAT_SHORT));
			if(nowCountDays01 < 0) {
				return true;
			}
		}
		return false;
	}
	
	/**
	  * getDayType(判断当前日期是正常工作日、周末加班、法定节假日（1：正常工作日，2：周末，3：法定节假日）)
	  * @Title: getDayType
	  * @Description: 判断当前日期是正常工作日、周末加班、法定节假日（1：正常工作日，2：周末，3：法定节假日）
	  * @param date
	  * @return    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	public Integer getDayType(Date date) {
		Integer type = 0;//1:工作日，2：周末，3：法定节假日
		
		AnnualVacation vacation = new AnnualVacation();
		vacation.setAnnualDate(DateUtils.parse(DateUtils.format(date),DateUtils.FORMAT_SHORT));
		List<AnnualVacation> list = annualVacationMapper.getListByCondition(vacation);
		if(list!=null&&list.size()>0){
			//判断是不是法定节假日
			if(list.get(0).getType().intValue() == AnnualVacation.YYPE_LEGAL.intValue()){//法定节假日
				type = 3;
				
				return type;
			}else if(list.get(0).getType().intValue() == AnnualVacation.YYPE_WORK.intValue()){//工作日
				type = 1;
				return type;
			}else if(list.get(0).getType().intValue() == AnnualVacation.YYPE_VACATION.intValue()){//法定调休日
				type = 4;
				return type;
			}
		}
		
		//判断是否是周末
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY
				||cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
			type = 2;
			return type;
		 }
		
		type = 1;
		return type;
	}

	/**
	 * @throws Exception 
	  * isBackWorkDate(判断是指定月份倒数第几个工作日)
	  * @Title: isBackWorkDate
	  * @Description: 判断是指定月份倒数第几个工作日
	  * @param num
	  * @return    设定文件
	  * Date    返回类型
	  * @throws						
	 */								
	public Date isBackWorkDate(Date date,int num) throws Exception{
		Date lastDate = DateUtils.getLastDay(date);
		
		int count = 0;
		while(true){
			if(judgeWorkOrNot(lastDate)){//是否是工作日
				count += 1;
			}
			
			if(count == num){//是倒数第几个工作日则跳出循环
				break;
			}
									
			lastDate = DateUtils.addDay(lastDate, -1);
		}
		
		return lastDate;
	}
	
	/**
	 * @throws Exception 
	  * isBackWorkDate(判断是指定月份倒数第几个工作日)
	  * @Title: isBackWorkDate
	  * @Description: 判断是指定月份倒数第几个工作日
	  * @param num
	  * @return    设定文件
	  * Date    返回类型
	  * @throws						
	 */								
	public Date isBackWorkDateOnSystemDate(Date date,int num,Map<Date,Integer> vacationMap) throws Exception{

		
		Integer count = 0;
		while(true){
			if(judgeWorkOrNot(date,vacationMap)){//是否是工作日
				count += 1;
			}
			
			if(count == num){//是倒数第几个工作日则跳出循环
				break;
			}
									
			date = DateUtils.addDay(date, -1);
		}
		
		return date;
	}
	
	/**
	 * @throws Exception 
	  * isBackWorkDate(计算两个日期相差多少工作日)
	  * @Title: isBackWorkDate
	  * @Description: 计算两个日期相差多少工作日
	  * @param 
	  * @return    AttnSignRecord
	  * AttnSignRecord
	  * @throws						
	 */								
	public AttnSignRecord CompareTwoTimesWorkDate (Date date,Date endDate,boolean isClassEmp,Map<Date,Integer> vacationMap) throws Exception{
		AttnSignRecord temple=new AttnSignRecord();
		int count = 0;
		StringBuilder str=new StringBuilder();
		Date startDateNum=date;
		SimpleDateFormat simple=new SimpleDateFormat("yyyy-MM-dd");
		if(2!=DateUtils.compareDate(date, endDate)){
			temple.setCount(0);
			return temple;
		}else{
			while(true){
				if(judgeWorkOrNot(startDateNum,vacationMap)){//是否是工作日					
					str.append(simple.format(startDateNum)+"， ");		
					startDateNum = DateUtils.addDay(startDateNum, 1);				
					count += 1;					
				}else{
					if(isClassEmp){
						//排班人员 双休、法定也算
						str.append(simple.format(startDateNum)+"， ");		
						startDateNum = DateUtils.addDay(startDateNum, 1);				
						count += 1;	
					}else{
						startDateNum = DateUtils.addDay(startDateNum, 1);
					}
				}
				if(startDateNum.equals(endDate)){//两个时间相等 则跳出循环
					if(judgeWorkOrNot(startDateNum,vacationMap) ||isClassEmp){
						str.append(simple.format(startDateNum)+"， ");		
						startDateNum = DateUtils.addDay(startDateNum, 1);
						count += 1; 
					}
					break;
				}
			}
		}
		temple.setStr(str.toString().substring(0, str.length()));
		temple.setCount(count);
		return temple;
	}

	@Override
	public Map<Date, AnnualVacation> getVacationMap(Date time) {
		//查询排班时间内的所有法定假期
		AnnualVacation vacation = new AnnualVacation();
		List<Integer> typeList = new ArrayList<Integer>();
		typeList.add(AnnualVacation.YYPE_LEGAL);
		typeList.add(AnnualVacation.YYPE_VACATION);
		vacation.setTypeList(typeList);
		vacation.setStartDate(time);
		vacation.setEndDate(DateUtils.getLastDay(time));
		List<AnnualVacation> vacationList1 = getListByCondition(vacation);
	
		List<AnnualVacation> vacationList = new ArrayList<AnnualVacation>();
		if(vacationList1!=null&&vacationList1.size()>0){
			//获取节假日前连续非工作日
			List<Date> startList = new ArrayList<Date>();
			Date startDate = DateUtils.addDay(vacationList1.get(0).getAnnualDate(), -1);
			while(true){
				if(judgeWorkOrNot(startDate)){
					break;
				}
				startList.add(startDate);
				startDate = DateUtils.addDay(startDate, -1);
			}
			//获取节假日后连续非工作日
			List<Date> endList = new ArrayList<Date>();
			Date endDate = DateUtils.addDay(vacationList1.get(vacationList1.size()-1).getAnnualDate(),1);
			while(true){
				if(judgeWorkOrNot(endDate)){
					break;
				}
				endList.add(endDate);
				endDate = DateUtils.addDay(endDate, 1);
			}
			if(startList!=null&&startList.size()>0){
				for(int j=startList.size()-1;j>=0;j--){
					AnnualVacation va = new AnnualVacation();
					va.setAnnualDate(startList.get(j));
					vacationList.add(va);
				}
			}
			for(AnnualVacation va:vacationList1){
				vacationList.add(va);
			}
			if(endList!=null&&endList.size()>0){
				for(Date date:endList){
					AnnualVacation va = new AnnualVacation();
					va.setAnnualDate(date);
					vacationList.add(va);
				}
			}
		}
		
		Map<Date,AnnualVacation> vacationMap = new HashMap<Date,AnnualVacation>();
		for(AnnualVacation va:vacationList){
			vacationMap.put(va.getAnnualDate(), va);
		}
		return vacationMap;
	}
	
	/**
	 * 保存假期
	 * @throws OaException 
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveVacation(AnnualVacation vacation,User user) throws OaException {
		if(vacation == null){
			throw new OaException("参数错误！");
		}
		Date annualDate = vacation.getAnnualDate();
		Integer type = vacation.getType();
		String subject = vacation.getSubject();
		Integer dateType = vacation.getDateType();
		Integer year = vacation.getYear();
		String content = vacation.getContent();
		if(annualDate == null){
			throw new OaException("请选择日期！");
		}
		if(type == null || StringUtils.isBlank(subject)){
			throw new OaException("请选择修改类型！");
		}
		if(dateType == null){
			throw new OaException("请选择假期类型！");
		}
		//查询假期表中是否存在当天数据
		AnnualVacation annualVacation = annualVacationMapper.getVacationByDate(annualDate);
		if(annualVacation == null){
			//插入一条数据
			annualVacation = new AnnualVacation();
			annualVacation.setAnnualDate(annualDate);
			annualVacation.setYear(year);
			annualVacation.setType(type);
			annualVacation.setSubject(subject);
			annualVacation.setDateType(0);
			annualVacation.setCreateTime(new Date());
			annualVacation.setCreateUser(user.getId().toString());
			annualVacation.setDelFlag(0);
			annualVacation.setVersion(0L);
			annualVacation.setContent(content);
			annualVacationMapper.save(annualVacation);
		}else{
			annualVacation.setAnnualDate(annualDate);
			annualVacation.setType(type);
			annualVacation.setSubject(subject);
			annualVacation.setDateType(dateType);
			annualVacation.setUpdateTime(new Date());
			annualVacation.setUpdateUser(user.getId().toString());
			annualVacationMapper.updateById(annualVacation);
		}
	}

	@Override
	public boolean judgeWorkOrNot(Date date, Map<Date, Integer> vacation) {
		if(vacation!=null&&vacation.containsKey(date)){
			//判断是否是工作日
			if(vacation.get(date).intValue()==AnnualVacation.YYPE_WORK.intValue()){
				return true;
			}else{
				return false;
			}
		}
		//判断是否是周末
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY
				||cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
			return false;
		 }
		return true;
	}
	
	/**
	 * 获取节假日日期
	 */
	@Override
	public List<Map<String,Object>> getAnnualDateByYearAndSubject(String year, String vacation) {
		List<Map<String,Object>> dateList = annualVacationMapper.getAnnualDateByYearAndSubject(year,vacation);
		for(Map<String,Object> date:dateList){
			date.put("weekDay", "星期"+DateUtils.getWeek((Date) date.get("day")));
		}
		return dateList;
	}
	
	@Override
	public List<Date> getAnnualDateByCondition(AnnualVacation vacation) {
		return annualVacationMapper.getAnnualDateByCondition(vacation);
	}

	@Override
	public PageModel<AnnualVacation> getListByPage(AnnualVacation vacation) {
		int page = vacation.getPage() == null ? 1 : vacation.getPage();
		int rows = vacation.getRows() == null ? 10 : vacation.getRows();
		
		PageModel<AnnualVacation> pm = new PageModel<AnnualVacation>();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		
		Integer total = annualVacationMapper.getCount(vacation);
		pm.setTotal(total);
		
		vacation.setOffset(pm.getOffset());
		vacation.setLimit(pm.getLimit());
		
		List<AnnualVacation> list = annualVacationMapper.getListByPage(vacation);
		
		pm.setRows(list);
		return pm;
	}

	@Override
	public void delete(Long id) {
		AnnualVacation del = new AnnualVacation();
		del.setDelFlag(1);
		del.setId(id);
		annualVacationMapper.updateById(del);
	}

}
