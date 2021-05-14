package com.ule.oa.base.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ule.oa.base.po.AnnualVacation;
import com.ule.oa.base.po.AttnSignRecord;
import com.ule.oa.base.po.User;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.PageModel;

/**
 * @ClassName: 每年假期表
 * @Description: 每年假期表
 * @author yangjie
 * @date 2017年6月16日
 */
public interface AnnualVacationService {
	
	public List<AnnualVacation> getListByCondition(AnnualVacation vacation);
	
	public boolean judgeWorkOrNot(Date date);
	
	public boolean judgeWorkOrNot(Date date,Map<Date,Integer> vacation);
	
	public boolean judgeVacation(Date date);
	
	public boolean judgeWorkOrNot(Map<String,AnnualVacation> map, String date);
	
	public Map<String,Boolean> judgeWorkOrNot(String month);
	
	/**
	  * getCurrVacttionBySubAndType(根据指定的节日名称和类型获取当年当年的节日信息)
	  * @Title: getCurrVacttionBySubAndType
	  * @Description: 根据指定的节日名称和类型获取当年当年的节日信息(type:1-工作日、2-年休假、3-法定节假日、4-节假日)
	  * @param vacation
	  * @return    设定文件
	  * List<AnnualVacation>    返回类型
	  * @throws
	 */
	public List<AnnualVacation> getCurrVacttionBySubAndType(AnnualVacation vacation);
	
	//获取次月5日（双休日，法定节假日往后延期）
	public Date get5WorkingDayNextmonth(int num);
	
	//获取指定日期前num个工作日
	public Date getWorkingDayPre(int num,Date date);
	
	//获取指定日期后num个工作日
	public Date getWorkingDayNext(int num,Date date);
	
	//判断是否超过次月3日或者5日（双休日，法定节假日往后延期）
	public boolean check5WorkingDayNextmonth(String applyDate,boolean flag);
	
	/**
	  * getDayType(判断当前日期是正常工作日、周末加班、法定节假日（1：正常工作日，2：周末，3：法定节假日）)
	  * @Title: getDayType
	  * @Description: 判断当前日期是正常工作日、周末加班、法定节假日（1：正常工作日，2：周末，3：法定节假日）
	  * @param date
	  * @return    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	public Integer getDayType(Date date);
	
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
	public Date isBackWorkDate(Date date,int num) throws Exception;

	/**
	 * @throws Exception 
	  * isBackWorkDateOnSystemDate(当前时间往前推多少指工作日)
	  * @Title: isBackWorkDateOnSystemDate
	  * @Description: 当前时间往前推多少指工作日——时间
	  * @param num
	  * @return    设定文件
	  * Date    返回类型
	  * @throws
	 */
	public Date isBackWorkDateOnSystemDate(Date date,int num,Map<Date,Integer> vacationMap) throws Exception;
	
	/**
	 * @throws Exception 
	  * isBackWorkDateOnSystemDate(获取两个时间相差多个工作日)
	  * @Title: isBackWorkDateOnSystemDate
	  * @Description: 当前时间往前推多少指工作日——时间
	  * @param num
	  * @return    设定文件
	  * Date    返回类型
	  * @throws
	 */
	public AttnSignRecord CompareTwoTimesWorkDate(Date date,Date endDate,boolean isClassEmp,Map<Date,Integer> vacationMap) throws Exception;
	
	
	/***
	 * 获取时间段内的节假日
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	Map<Date, Boolean> judgeWorkOrNotByPriod(Date startDate, Date endDate);

	/**
	 * 获取指定月份的指定工作日
	 * @param num
	 * @param 
	 * @return
	 */
	Date getWorkingDayOfMonth(Date anyDay,int num);
	
	//获取法定节假日前连续休息日集合
	
	Map<Date,AnnualVacation> getVacationMap(Date time);

	public void saveVacation(AnnualVacation vacation, User user) throws OaException;

	public List<Map<String,Object>> getAnnualDateByYearAndSubject(String year, String vacation);

	public List<Date> getAnnualDateByCondition(AnnualVacation vacation);
	
	public PageModel<AnnualVacation> getListByPage(AnnualVacation vacation);
	
	void delete(Long id);
	
}
