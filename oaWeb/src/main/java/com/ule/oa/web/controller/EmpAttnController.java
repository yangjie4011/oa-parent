 /**
 * @Title: EmpAttnController.java
 * @Package com.ule.oa.web.controller
 * @Description: TODO
 * @Copyright: Copyright (c) 2017
 * @Company:邮乐网 *
 * @author zhangjintao
 * @date 2017年6月22日 上午10:51:15
 */

package com.ule.oa.web.controller;

import java.net.SocketException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ule.oa.base.po.AttnStatistics;
import com.ule.oa.base.po.CompanyConfig;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.service.AttnStatisticsService;
import com.ule.oa.base.service.CompanyConfigService;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.JSON;
import com.ule.oa.web.util.ReturnUrlUtil;

 /**
 * @ClassName: EmpAttnController
 * @Description: 员工考勤controller
 * @author zhangjintao
 * @date 2017年6月22日 上午10:51:15
 */

@Controller
@RequestMapping("empAttn")
public class EmpAttnController {
	
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Resource
    private AttnStatisticsService attnStatisticsService;

    @Resource
    private DepartService departService;

    @Resource
    private EmployeeService employeeService;
    
    @Resource
    private UserService userService;
    
    @Resource
    private CompanyConfigService companyConfigService;
    
    @RequestMapping("/index.htm")
    public ModelAndView index(AttnStatistics condition,String urlType) throws OaException{
    	ModelMap map = new ModelMap();
    	
    	AttnStatistics attnStatistics = attnStatisticsService.getTotalAttStatistics(condition);/**计算截止到当日的总出勤，应出勤**/
    	
    	/**传到页面的时间，防止被改变**/
    	final String startTime = DateUtils.format(condition.getStartTime(), DateUtils.FORMAT_SHORT);
    	final String endTime = DateUtils.format(condition.getEndTime(), DateUtils.FORMAT_SHORT);
    	
    	if(null != attnStatistics){
    		
        	AttnStatistics monthAttnStatistics = attnStatisticsService.getMonthTotalMustAtten(condition);/**计算月度总的应出勤**/
    		
    		Double allAttnTime,mustAttnTime,monthTotalMustAttnTime;
    		
    		allAttnTime = attnStatistics.getActAttnTime(); 
    		if("comprehensive".equals(attnStatistics.getWorkTypeName())){
    			allAttnTime = attnStatistics.getAllAttnTime(); 
    		}
    		mustAttnTime = attnStatistics.getMustAttnTime();
    		monthTotalMustAttnTime = monthAttnStatistics.getMustAttnTime();
    		
        	//如果考勤时间大于 应出勤时间 则等于应出勤时间 
        	if(allAttnTime>mustAttnTime) {
        		allAttnTime=mustAttnTime;
        	}
        	
        	map.put("totalAllAttnTime", allAttnTime);
        	map.put("totalMustAttnTime", mustAttnTime);
        	map.put("monthTotalMustAttnTime", monthTotalMustAttnTime);
        	
        	Double proportion;
        	if(mustAttnTime.intValue() == 0){
            	proportion = 100.0;
        	}else{
            	proportion = allAttnTime*100/mustAttnTime;
        	}
        	if(proportion >100) {
        		proportion=100.0;//比例最高100%
        	}
        	map.put("proportion",proportion);
        	map.put("startTime", startTime);
        	map.put("endTime", endTime);
        	map.put("employId", attnStatistics.getEmployId());
        	map.put("workTypeName", attnStatistics.getWorkTypeName());
        	map.put("urlType", urlType);
        	
        	Employee employee = new Employee();
        	employee.setReportToLeader(userService.getCurrentUser().getEmployeeId());
        	int leaderCount = employeeService.getListByConditionCount(employee);
        	if(leaderCount > 0){
        		map.put("isLeader","true");
        	}else{
        		map.put("isLeader","false");
        	}
    	}
        
    	String returnUrl = "/login/index.htm";//返回主页面
    	if(urlType != null && urlType.equals("1")){//返回个人中心页面
    		returnUrl = "/employee/indexPerson.htm";
    	}
		map.put("returnUrl",returnUrl);
		return new ModelAndView("attendance/myAttendance",map);
    }
    
    /**首页的考勤展现**/
	@RequestMapping("/getAttnOfMain.htm")
	@ResponseBody
    public ModelMap getAttnOfMain(AttnStatistics condition) throws OaException{
		
        ModelMap map = new ModelMap();
    	
    	AttnStatistics attnStatistics = attnStatisticsService.getTotalAttStatistics(condition);/**计算截止到当日的总出勤，应出勤**/
    	if(null != attnStatistics){
    		
        	AttnStatistics monthAttnStatistics = attnStatisticsService.getMonthTotalMustAtten(condition);/**计算月度总的应出勤**/
    		
    		Double allAttnTime,mustAttnTime,monthTotalMustAttnTime;
    		
    		allAttnTime = attnStatistics.getActAttnTime(); 
    		if("comprehensive".equals(attnStatistics.getWorkTypeName())){
    			allAttnTime = attnStatistics.getAllAttnTime(); 
    		}
    		mustAttnTime = attnStatistics.getMustAttnTime();
    		monthTotalMustAttnTime = monthAttnStatistics.getMustAttnTime();
        	
        	//如果考勤时间大于 应出勤时间 则等于应出勤时间 
        	if(allAttnTime>mustAttnTime) {
        		allAttnTime=mustAttnTime;
        	}
    		
        	map.put("totalAllAttnTime", allAttnTime);
        	map.put("totalMustAttnTime", mustAttnTime);
        	map.put("monthTotalMustAttnTime", monthTotalMustAttnTime);
        	
        	Double proportion;
        	if(mustAttnTime.intValue() == 0){
            	proportion = 100.0;
        	}else{
            	proportion = allAttnTime*100/mustAttnTime;
        	}
        	if(proportion >100) {
        		proportion=100.0;//比例最高100%
        	}        	
        	map.put("proportion",proportion);
    	}
		return map;
    }
    
    @RequestMapping(value="/subIndex.htm")
    public ModelAndView subIndex(AttnStatistics condition,String urlType) throws OaException{
    	ModelMap map = new ModelMap();
    	
    	final String backStartTime = DateUtils.format(condition.getStartTime(), DateUtils.FORMAT_SHORT);
    	final String backEndTime = DateUtils.format(condition.getEndTime(), DateUtils.FORMAT_SHORT);
    	Long reportToLeader = condition.getReportToLeader();
    	
    	AttnStatistics attnStatistics = attnStatisticsService.getTotalAttStatistics(condition);
    	if(null != attnStatistics){
    		
        	AttnStatistics monthAttnStatistics = attnStatisticsService.getMonthTotalMustAtten(condition);/**计算月度总的应出勤**/
        	
            Double allAttnTime,mustAttnTime,monthTotalMustAttnTime;
    		
    		allAttnTime = attnStatistics.getActAttnTime(); 
    		mustAttnTime = attnStatistics.getMustAttnTime();
    		monthTotalMustAttnTime = monthAttnStatistics.getMustAttnTime();
    		
        	map.put("totalAllAttnTime", allAttnTime);
        	map.put("totalMustAttnTime", mustAttnTime);
        	map.put("monthTotalMustAttnTime", monthTotalMustAttnTime);
        	
        	Double proportion;
        	if(mustAttnTime.intValue() == 0){
            	proportion = 100.0;
        	}else{
            	proportion = allAttnTime*100/mustAttnTime;
        	}
        	if(proportion >100) {
        		proportion=100.0;//比例最高100%
        	}
        	map.put("proportion",proportion);
        	
        	map.put("startTime", backStartTime);
        	map.put("endTime", backEndTime);
        	map.put("employId", attnStatistics.getEmployId());
        	map.put("workTypeName", attnStatistics.getWorkTypeName());
        	map.put("urlType", urlType);
        	
        	Employee employee = employeeService.getById(attnStatistics.getEmployId());
        	map.put("employeeName", employee.getCnName());
    	}
    	
    	if(null == reportToLeader){
    		reportToLeader = employeeService.getCurrentEmployeeId();
    	}
    	String returnUrl = "/empAttn/subAttendance.htm?employId="+reportToLeader+"&startTime="+backStartTime
    			         +"&endTime="+backEndTime+"&urlType="+urlType;
		map.put("returnUrl", returnUrl);
        
		return new ModelAndView("attendance/subAttendanceDetail",map);
    }
    
	@RequestMapping("/getAttStatisticsList.htm")
	@ResponseBody
    public List<Map<String, Object>> getAttStatisticsDetailList(AttnStatistics condition) throws OaException{
		
		return attnStatisticsService.getAttStatisticsDetailList(condition);
    }
	
	@RequestMapping("/subAttendance.htm")
    public ModelAndView subAttendance(AttnStatistics condition,String urlType){
    	ModelMap map = new ModelMap();
    	
    	map.put("employId", condition.getEmployId());
    	map.put("startTime", DateUtils.format(condition.getStartTime(), DateUtils.FORMAT_SHORT));
    	map.put("endTime", DateUtils.format(condition.getEndTime(), DateUtils.FORMAT_SHORT));
    	map.put("urlType", urlType);
		map.put("returnUrl", ReturnUrlUtil.getReturnUrl("3"));
    	
		return new ModelAndView("attendance/subAttendance",map);
    }
    
	@RequestMapping("/getSubAttStatisticsList.htm")
	@ResponseBody
	public List<Map<String, String>> getSubAttStatisticsList(AttnStatistics condition) {
		
		List<Map<String, String>> list = attnStatisticsService.getSubAttStatisticsList(condition);
		return list;
	}
	
	//考勤明细页面
    
	@RequestMapping("/toEmployeeAttn.htm")
	public ModelAndView toEmployeeAttn() {
		ModelMap map  = new ModelMap();
		Date today = DateUtils.getToday();
		map.put("today", DateUtils.format(today, "yyyy-MM-dd"));
		map.put("thisMonth", DateUtils.format(today, "yyyy年MM月"));
		
		return new ModelAndView("attendance/employeeAttn",map);
	}
	
	//考勤明细页面各项异常汇总
	@ResponseBody
	@RequestMapping("/getEmpAttnNumber.htm")
	public Map<String,String> getEmpAttnNumber(String date,Long departId,String type) throws Exception{
		
		Date cDate;
		AttnStatistics condition = new AttnStatistics();
		condition.setDepartId(departId);
		if("day".equals(type)){
			cDate = DateUtils.parse(date, DateUtils.FORMAT_SHORT);
			condition.setStartTime(cDate);
			cDate = DateUtils.addDay(cDate, 1);
			condition.setEndTime(cDate);
		}else{
			cDate = DateUtils.parse(date, "yyyy年MM月");
			cDate = DateUtils.getFirstDay(cDate);
			condition.setStartTime(cDate);
			cDate = DateUtils.addMonth(cDate, 1);
			condition.setEndTime(cDate);
		}
		
		Map<String,String> numberMap = attnStatisticsService.getNumberByType(condition);
		return numberMap;
	}
	
	//员工异常考勤详细
	@RequestMapping("/employeeAttnDetail.htm")
	public ModelAndView employeeAttnDetail(String date,Long departId,String departName,String type,String total,String timeType){
		ModelMap map = new ModelMap();
		map.put("date", date);
		map.put("departId", departId);
		map.put("departName", departName);
		map.put("type", type);
		map.put("total", total);
		map.put("timeType", timeType);
		String showType = null;
		if(type.equals("late")){
			showType = "迟到";
		}else if(type.equals("early")){
			showType = "早退";
		}else if(type.equals("lack")){
			showType = "缺勤";
		}else if(type.equals("absent")){
			showType = "旷工";
		}
		map.put("showType", showType);
		return new ModelAndView("attendance/employeeAttnDetail",map);
	}
	

	@ResponseBody
	@RequestMapping("/getEmpAttnTimes.htm")
	public List<Map<String,String>> getEmpAttnTimes(String date,Long departId,String type,String timeType) throws Exception{
		Date cDate;
		AttnStatistics condition = new AttnStatistics();
		condition.setDepartId(departId);
		condition.setType(type);
		if("day".equals(timeType)){
			cDate = DateUtils.parse(date, DateUtils.FORMAT_SHORT);
			condition.setStartTime(cDate);
			cDate = DateUtils.addDay(cDate, 1);
			condition.setEndTime(cDate);
		}else{
			cDate = DateUtils.parse(date, "yyyy年MM月");
			cDate = DateUtils.getFirstDay(cDate);
			condition.setStartTime(cDate);
			cDate = DateUtils.addMonth(cDate, 1);
			condition.setEndTime(cDate);
		}
		
		List<Map<String,String>> list = attnStatisticsService.getEmpAttnTimes(condition);
		
		return list;
	}
	
	@RequestMapping("/empAttnTimesDetail.htm")
	public ModelAndView empAttnTimesDetail(String date,Long employeeId,String employeeName,String departName,String count,String type,String timeType){
		
		ModelMap map = new ModelMap();
		map.put("date", date);
		map.put("employeeId", employeeId);
		map.put("employeeName", employeeName);
		map.put("departName", departName);
		map.put("count", count);
		map.put("type", type);
		map.put("timeType", timeType);
		String showType = null;
		if(type.equals("late")){
			showType = "迟到";
		}else if(type.equals("early")){
			showType = "早退";
		}else if(type.equals("lack")){
			showType = "缺勤";
		}else if(type.equals("absent")){
			showType = "旷工";
		}
		map.put("showType", showType);
		
		Employee employee = employeeService.getById(employeeId);
		Long workTypeId = employee.getWorkType();
		CompanyConfig condition = new CompanyConfig();
		condition.setId(workTypeId);
		CompanyConfig config = companyConfigService.getByCondition(condition);
		map.put("workTypeName", config.getDisplayCode());
		
		return new ModelAndView("attendance/empAttnTimesDetail",map);
	}
	
	@ResponseBody
	@RequestMapping("/getEmpAttnTimesDetail.htm")
	public List<AttnStatistics> getEmpAttnTimesDetail(String date,Long employeeId,String type,String timeType) throws Exception{
		Date cDate;
		AttnStatistics condition = new AttnStatistics();
		condition.setType(type);
		condition.setEmployId(employeeId);
		if("day".equals(timeType)){
			cDate = DateUtils.parse(date, DateUtils.FORMAT_SHORT);
			condition.setStartTime(cDate);
			cDate = DateUtils.addDay(cDate, 1);
			condition.setEndTime(cDate);
		}else{
			cDate = DateUtils.parse(date, "yyyy年MM月");
			cDate = DateUtils.getFirstDay(cDate);
			condition.setStartTime(cDate);
			cDate = DateUtils.addMonth(cDate, 1);
			condition.setEndTime(cDate);
		}
		
		List<AttnStatistics> list = attnStatisticsService.getAttStatisticsList(condition);
		return list;
		
	}
	
	@RequestMapping("/toAttnExport.htm")
	public ModelAndView toAttnExport(){
		return new ModelAndView("attendance/attnExport");
	}
	
	@ResponseBody
	@RequestMapping("/exportAttnData.htm")
	public JSON exportData(String reportId,@DateTimeFormat(pattern="yyyy-MM-dd")Date startTime,
			@DateTimeFormat(pattern="yyyy-MM-dd")Date endTime,Long departId){//导出数据并发送邮件
		
		//reportId:empAttnDetail员工打卡明细,actualTimeRecord实时打卡明细
		JSON json = new JSON(true, null, null);
		try {
			attnStatisticsService.exportData( reportId, startTime, endTime, departId);
			json.setMessage("导出成功，请稍后查看邮箱！");
		} catch (Exception e) {
			logger.info("导出邮件失败={}",e.getMessage());
			if(e.getCause() instanceof MailSendException || e.getCause() instanceof MessagingException || e.getCause() instanceof SocketException){
				json.setMessage("邮件系统繁忙，请重试");
			}else{
				json.setMessage("导出失败，请稍后重试！");
			}
			
			json.setSuccess(false);
			json.setResult(e.getMessage());
		}
		return json;
	}
}