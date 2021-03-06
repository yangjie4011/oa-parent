package com.ule.oa.base.service.impl;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ule.oa.base.cache.ConfigCacheManager;
import com.ule.oa.base.mapper.AttnUpdateLogMapper;
import com.ule.oa.base.mapper.AttnWorkHoursMapper;
import com.ule.oa.base.mapper.EmpAttnMapper;
import com.ule.oa.base.mapper.EmpSignRecordMapper;
import com.ule.oa.base.mapper.EmployeeBaseInfoMapper;
import com.ule.oa.base.mapper.EmployeeClassMapper;
import com.ule.oa.base.mapper.RabcUserMapper;
import com.ule.oa.base.po.AttnSignRecord;
import com.ule.oa.base.po.AttnUpdateLog;
import com.ule.oa.base.po.AttnWorkHours;
import com.ule.oa.base.po.ClassSetting;
import com.ule.oa.base.po.CompanyConfig;
import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.EmpAttn;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeBaseInfoDTO;
import com.ule.oa.base.po.EmployeeBaseInfoParam;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.po.RunTask;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.AnnualVacationService;
import com.ule.oa.base.service.AttnStatisticsService;
import com.ule.oa.base.service.CompanyConfigService;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmpAttnService;
import com.ule.oa.base.service.EmployeeClassService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.ExcelUtil;
import com.ule.oa.common.utils.PageModel;

@Service
public class EmpAttnServiceImpl implements EmpAttnService{
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String NIGHT=" 09:00:00";//9???
    private final String EIGHTEEN=" 18:00:00";//18???

	@Resource
	private EmpAttnMapper empAttnMapper;
	@Resource
	private DepartService departService;
	@Resource
	private AnnualVacationService annualVacationService;
	@Resource
	private EmployeeService employeeService;
	@Resource
	private EmpSignRecordMapper empSignRecordMapper;
	@Resource
	private ConfigCacheManager configCacheManager;
	@Resource
	private AttnWorkHoursMapper attnWorkHoursMapper;
	@Autowired
	private UserService userService;
	@Autowired
	private AttnStatisticsService attnStatisticsService;
	@Autowired
	private AttnUpdateLogMapper attnUpdateLogMapper;
	@Autowired
	private EmployeeClassMapper employeeClassMapper;
	@Autowired
	private EmployeeClassService employeeClassService;
	@Autowired
	private ConfigService configService;
	@Autowired
	private RabcUserMapper rabcUserMapper;
	@Autowired
	private EmployeeBaseInfoMapper employeeBaseInfoMapper;
	@Autowired
	private CompanyConfigService companyConfigService;

	

	@Override
	public PageModel<Map<String,Object>> getAttnReportPageList(EmpAttn empAttn) {
		
		//??????????????????????????????????????????????????????
		if(empAttn.getEmpTypeId()==null){
			empAttn.setEmpTypeIdList(configService.getNeedEmpTypeIdList());
		}
		
		if(empAttn.getFirstDepart()!=null) {
			empAttn.setDepartId(Long.valueOf(empAttn.getFirstDepart()));
		}
		
		int page = empAttn.getPage() == null ? 0 : empAttn.getPage();
		int rows = empAttn.getRows() == null ? 0 : empAttn.getRows();
		
		PageModel<Map<String,Object>> pm = new PageModel<Map<String,Object>>();
		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
		if(deptDataByUserList==null || deptDataByUserList.size()==0){
			pm.setPageNo(0);
			pm.setPageSize(0);
			pm.setTotal(0);
			empAttn.setOffset(pm.getOffset());
			empAttn.setLimit(pm.getLimit());
			pm.setRows(new ArrayList<Map<String,Object>>());
			return pm;
		}else{
			List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(currentUser.getEmployeeId());//????????????
			if(subEmployeeIdList!=null&&subEmployeeIdList.size()>0){
				empAttn.setSubEmployeeIdList(subEmployeeIdList);
			}

			empAttn.setCurrentUserDepart(deptDataByUserList);
			pm.setPageNo(page);
			pm.setPageSize(rows);
			Integer total = empAttnMapper.getAttnReportCount(empAttn);
			pm.setTotal(total);
			
			empAttn.setOffset(pm.getOffset());
			empAttn.setLimit(pm.getLimit());
			
			List<Map<String,Object>> datas = getgetAttnReporteList(empAttn);
			
			pm.setRows(datas);
			return pm;
		}	
	}
	
	private List<Map<String,Object>> getgetAttnReporteList(EmpAttn empAttn){
		
		List<Map<String,Object>> datas = empAttnMapper.getAttnReportPageList(empAttn);
		
		//???????????????
		Map<Date, Boolean> workDayMap = annualVacationService.judgeWorkOrNotByPriod(empAttn.getStartTime(), empAttn.getEndTime());
		//????????????
		EmployeeClass classParam = new EmployeeClass();
		classParam.setStartTime(empAttn.getStartTime());
		classParam.setEndTime(empAttn.getEndTime());
		List<EmployeeClass> classList = employeeClassMapper.selectByCondition(classParam);
		Map<String,EmployeeClass> classMap = new HashMap<String,EmployeeClass>();
		for(EmployeeClass data:classList) {
			classMap.put(DateUtils.format(data.getClassDate(),DateUtils.FORMAT_SHORT)+"_"+String.valueOf(data.getEmployId()), data);
		}
		//?????????
		CompanyConfig companyConfigConditon = new CompanyConfig();//?????????????????????
		companyConfigConditon.setCode("typeOfWork");//????????????
		List<CompanyConfig> workTypeList = companyConfigService.getListByCondition(companyConfigConditon);
		Map<Long,CompanyConfig> workTypeMap = workTypeList.stream().collect(Collectors.toMap(CompanyConfig :: getId,a->a,(k1,k2)->k1));	
		//????????????
		Config configCondition = new Config();
		configCondition.setCode("whetherScheduling");//????????????
		List<Config> whetherSchedulingList = configService.getListByCondition(configCondition);
		Map<Long,Config> whetherSchedulingMap = whetherSchedulingList.stream().collect(Collectors.toMap(Config :: getId,a->a,(k1,k2)->k1));
		//?????????????????????
		List<Map<String,Object>> notSignList = empAttnMapper.getNotSignWorkHoursList(empAttn.getStartTime(), empAttn.getEndTime());
		Map<String,Map<String,Object>> notSignMap = new HashMap<String,Map<String,Object>>();
		for(Map<String,Object> data:notSignList) {
			String key = DateUtils.format((Date)data.get("work_date"), DateUtils.FORMAT_SHORT)+"_"+data.get("employ_id");
			notSignMap.put(key, data);
		}
		//????????????
		List<Map<String,Object>> signList = empAttnMapper.getSignWorkHoursList(empAttn.getStartTime(), empAttn.getEndTime());
		Map<String,Map<String,Object>> signMap = new HashMap<String,Map<String,Object>>();
		for(Map<String,Object> data:signList) {
			String key = DateUtils.format((Date)data.get("work_date"), DateUtils.FORMAT_SHORT)+"_"+data.get("employ_id");
			signMap.put(key, data);
		}
		
	    String dataType = "";
	    String leaveType = "";
	    Date classSetStartTime,classSetEndTime,attnDate;//???????????????????????????
	    Date classStartTime,classEndTime;//?????????????????????????????????????????????????????????
	    Date startWorkTime = null;
	    Date endWorkTime = null;
	    Double mustAttnTime,absenteeismTime;
	    final String good = "??????", bad = "??????";
	    String startWorkStatus,endWorkStatus,classStartStatus,classEndStatus,attnDateStr;
	    Integer isInterDay = 0;
	    for(Map<String, Object> o:datas){
			//?????????
			Long workType = (Long) o.get("workType");
			if(workTypeMap.containsKey(workType)){
				o.put("workTypeName", workTypeMap.get(workType).getDisplayName());
			}else{
				o.put("workTypeName", "");
			}
			//????????????
			attnDate = (Date) o.get("attnDate");
			attnDateStr = DateUtils.format(attnDate,DateUtils.FORMAT_SHORT);
			//????????????
			Long whetherScheduling = (Long) o.get("whetherScheduling");
			//????????????
			String className = "";
			String whetherSchedulingCode = (whetherSchedulingMap!=null&&whetherSchedulingMap.containsKey(whetherScheduling))?whetherSchedulingMap.get(whetherScheduling).getDisplayCode():"";
			String workTypeCode = (workTypeMap!=null&&workTypeMap.containsKey(workType))?workTypeMap.get(workType).getDisplayCode():"";		
					
			if("yes".equals(whetherSchedulingCode) && "standard".equals(workTypeCode)){
				String key = DateUtils.format(attnDate, DateUtils.FORMAT_SHORT)+"_"+(Long) o.get("employId");
				if(classMap!=null&&classMap.containsKey(key)) {
					classSetStartTime = classMap.get(key).getStartTime();
					classSetEndTime = classMap.get(key).getEndTime();
					className = classMap.get(key).getName();
					isInterDay = classMap.get(key).getIsInterDay();
					o.put("classSetStartTime", classSetStartTime);
					o.put("classSetEndTime", classSetEndTime);
				}
			}else{
				if(workDayMap!=null&&workDayMap.containsKey(attnDate)) {
					boolean isWorkDay = workDayMap.get(attnDate);
					if(isWorkDay){/**?????????**/
						className = "A";//?????????
						classSetStartTime = DateUtils.parse(attnDateStr+NIGHT, DateUtils.FORMAT_LONG);
						classSetEndTime = DateUtils.parse(attnDateStr+EIGHTEEN, DateUtils.FORMAT_LONG);
						o.put("classSetStartTime", classSetStartTime);
						o.put("classSetEndTime", classSetEndTime);
					}else{/**????????????**/
						className = "??????";//?????????
					}
				}else {
					className = "A";//?????????
					classSetStartTime = DateUtils.parse(attnDateStr+NIGHT, DateUtils.FORMAT_LONG);
					classSetEndTime = DateUtils.parse(attnDateStr+EIGHTEEN, DateUtils.FORMAT_LONG);
					o.put("classSetStartTime", classSetStartTime);
					o.put("classSetEndTime", classSetEndTime);
				}
			}
			o.put("className", className);
			//?????????????????????
			String key =  attnDateStr +"_"+ o.get("employId");
			if(notSignMap!=null&&notSignMap.containsKey(key)) {
				dataType = (String) notSignMap.get(key).get("data_type");
				leaveType = (String) notSignMap.get(key).get("leave_type");
			}
			if(null != dataType && dataType.length()>0){
				dataType = getReasonByType(dataType,leaveType);
				o.put("dataType", dataType);
			}
			//????????????
			if(signMap!=null&&signMap.containsKey(key)) {
				startWorkTime = (Date) signMap.get(key).get("start_time");
				endWorkTime = (Date) signMap.get(key).get("end_time");
			}
			
			//??????????????????
			classSetStartTime = (Date) o.get("classSetStartTime");
			classSetEndTime = (Date) o.get("classSetEndTime");
			classStartTime = (Date) o.get("classStartTime");
			classEndTime = (Date) o.get("classEndTime");
			mustAttnTime = o.get("mustAttnTime")!=null?(Double) o.get("mustAttnTime"):8d;
			Integer absenteeismTime_m = o.get("absenteeismTime")!=null?(Integer) o.get("absenteeismTime"):0;
			if(mustAttnTime>=8){//????????????
				absenteeismTime = absenteeismTime_m/480.0;
			}else{
				if(mustAttnTime>0){
					absenteeismTime =  absenteeismTime_m/(mustAttnTime*60);
				}else{
					absenteeismTime = 0d;
				}
				
			}
			o.put("absenteeismTime",absenteeismTime);
			
			if(mustAttnTime.intValue() == 0){//????????????
				startWorkStatus = bad;
				endWorkStatus = bad;
				classStartStatus = bad;
				classEndStatus = bad;
			}else{
				
				
				
				if(null == classSetStartTime){/*???????????????????????????????????????*/
					
					classSetStartTime = DateUtils.parse(attnDateStr+NIGHT, DateUtils.FORMAT_LONG);
					classSetEndTime = DateUtils.parse(attnDateStr+EIGHTEEN, DateUtils.FORMAT_LONG);
					
					
				}else{

					classSetStartTime = concatDateTime(attnDate, classSetStartTime);
					classSetEndTime = concatDateTime(attnDate, classSetEndTime);
					
					if(1 == isInterDay.intValue()){//??????+1
						classSetEndTime = DateUtils.addDay(classSetEndTime, 1);
					}
				}
				
				if(classStartTime==null || classStartTime.after(classSetStartTime)){
					classStartStatus = bad;
				}else{
					classStartStatus = good;
				}
				
				if(classEndTime==null || classEndTime.before(classSetEndTime)){
					classEndStatus = bad;
				}else{
					classEndStatus = good;
				}
				
				if(startWorkTime==null || startWorkTime.after(classSetStartTime)){
					startWorkStatus = bad;
				}else{
					startWorkStatus = good;
				}
				
				if(endWorkTime==null || endWorkTime.before(classSetEndTime)){
					endWorkStatus = bad;
				}else{
					endWorkStatus = good;
				}
				
			}

			classStartTime = (Date) o.get("classStartTime");
			classEndTime = (Date) o.get("classEndTime");

			o.put("classStartTime", DateUtils.format(classStartTime,"HH:mm:ss"));
			o.put("classEndTime", DateUtils.format(classEndTime,"HH:mm:ss"));
			o.put("startWorkTime", DateUtils.format(startWorkTime,"HH:mm:ss"));
			o.put("endWorkTime", DateUtils.format(endWorkTime,"HH:mm:ss"));
			
			o.put("classStartStatus", classStartStatus);
			o.put("classEndStatus", classEndStatus);
			o.put("startWorkStatus", startWorkStatus);
			o.put("endWorkStatus", endWorkStatus);
			
		}
		
		return datas;
	}

	//date1?????????????????????date2????????????
	private Date concatDateTime(Date date1,Date date2){
		Calendar cal1 = Calendar.getInstance(); //??????????????????  
		Calendar cal2 = Calendar.getInstance();  
		
		cal1.setTime(date1); //??????????????????????????????????????????  
		cal2.setTime(date2);
	    
	    int i = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);  
	    cal2.add(Calendar.YEAR, i);
	    
	    i = cal1.get(Calendar.MONTH) - cal2.get(Calendar.MONTH);  
	    cal2.add(Calendar.MONTH, i);
		
	    i = cal1.get(Calendar.DATE) - cal2.get(Calendar.DATE);  
	    cal2.add(Calendar.DATE, i);
		
		return cal2.getTime();  
	}
	
	private String getReasonByType(String dataType, String leaveType){
		
		StringBuffer sb = new StringBuffer("");
		String reason = "";
		
		String[] dataTypeArray = dataType.split(",");
		String[] dataTypeArray1 =  Arrays.stream(dataTypeArray).distinct().toArray(String[]::new);
		for(String type:dataTypeArray1){
			if(RunTask.RUN_CODE_10.equals(type)){
				sb.append("??????????????????,");
			}else if(RunTask.RUN_CODE_20.equals(type)){
				sb.append("????????????,");
			}else if(RunTask.RUN_CODE_30.equals(type)){
				sb.append("??????????????????,");
			}else if(RunTask.RUN_CODE_40.equals(type)||RunTask.RUN_CODE_41.equals(type)){
				sb.append("????????????,");
			}else if(RunTask.RUN_CODE_50.equals(type)){
				sb.append("??????,");
			}else if(RunTask.RUN_CODE_60.equals(type)){
				
				/**???????????????????????????**/
				if(null == leaveType || "".equals(leaveType)){
					sb.append("????????????,");
				}else{
					sb.append(getLeaveReasonByType(leaveType));
				}
				
			}else if(RunTask.RUN_CODE_70.equals(type)){
				sb.append("????????????,");
			}else if(RunTask.RUN_CODE_80.equals(type)){
				sb.append("????????????,");
			}else if(RunTask.RUN_CODE_90.equals(type)){
				sb.append("????????????,");
			}else if(RunTask.RUN_CODE_100.equals(type)){
				sb.append("??????????????????,");
			}else if(RunTask.RUN_CODE_110.equals(type)){
				sb.append("??????,");
			}else if(RunTask.RUN_CODE_120.equals(type)){
				sb.append("??????????????????,");
			}else if(RunTask.RUN_CODE_140.equals(type)){
				sb.append("???????????????,");
			}else if("5".equals(type)){
				sb.append("????????????,");
			}else if("6".equals(type)){
				sb.append("??????,");
			}
		}
		
		Integer length = sb.length();
		if(length>0){
			reason = sb.substring(0,length-1);
		}
		
		return reason;
	}
	
	private String getLeaveReasonByType(String leaveType){
		
		StringBuffer sb = new StringBuffer("");
		String reason = "";
		
		String[] leaveTypeArray = leaveType.split(",");
		String[] leaveTypeArray1 =  Arrays.stream(leaveTypeArray).distinct().toArray(String[]::new);
		
		for(String type:leaveTypeArray1){
			if("1".equals(type)){
				sb.append("??????,");
			}else if("2".equals(type)){
				sb.append("??????,");
			}else if("3".equals(type)){
				sb.append("??????,");
			}else if("4".equals(type)){
				sb.append("?????????,");
			}else if("5".equals(type)){
				sb.append("??????,");
			}else if("6".equals(type)){
				sb.append("?????????,");
			}else if("7".equals(type)){
				sb.append("??????,");
			}else if("8".equals(type)){
				sb.append("?????????,");
			}else if("9".equals(type)){
				sb.append("?????????,");
			}else if("10".equals(type)){
				sb.append("??????,");
			}else if("11".equals(type)){
				sb.append("??????,");
			}else if("12".equals(type)){
				sb.append("??????,");
			}
		}
		
		reason = sb.toString();
		return reason;
	}
	
	private List<Integer> getDepartList(Integer firstDepart,Integer secondDepart){
		
		List<Integer> departList = new ArrayList<Integer>();
		if(secondDepart!=null){
			departList.add(secondDepart);
		}else if(secondDepart==null&&firstDepart!=null){
			
			departList.add(firstDepart);
			List<Depart> list = departService.getByParentId(Long.valueOf(String.valueOf(firstDepart)));
		    
		    for(Depart depart:list){
		    	departList.add(Integer.valueOf(String.valueOf(depart.getId())));
		    };
		}

		
		if(departList.isEmpty()){
			departList = null;
		}
		return departList;
	}

	@Override
	public HSSFWorkbook exportAttnReport(EmpAttn empAttn) {
		
		//??????????????????????????????????????????????????????
		if(empAttn.getEmpTypeId()==null){
			empAttn.setEmpTypeIdList(configService.getNeedEmpTypeIdList());
		}
		
		//????????????????????????
		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
		
		if(deptDataByUserList==null || deptDataByUserList.size()==0){
			return null;
		}else{
			//??????????????????????????????
			List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(currentUser.getEmployeeId());//????????????
			if(subEmployeeIdList!=null&&subEmployeeIdList.size()>0){
				empAttn.setSubEmployeeIdList(subEmployeeIdList);
			}
			
			empAttn.setCurrentUserDepart(deptDataByUserList);
			if(empAttn.getFirstDepart()!=null) {
				empAttn.setDepartId(Long.valueOf(empAttn.getFirstDepart()));
			}
		    
		    List<Map<String,Object>> datas = getgetAttnReporteList(empAttn);
	
			
			String[] titles = { "????????????","????????????","??????","????????????","?????????","????????????","??????",
					            "??????","??????????????????","??????????????????","??????????????????","??????????????????",
				             	"??????????????????","??????????????????","??????????????????","??????????????????","??????????????????",
				             	"??????????????????","???????????????","???????????????","???????????????","???????????????","	????????????"};
			String[] keys = { "code","cnName","departName","positionName","workTypeName","attnDate","dayofweek",
					          "className","startWorkTime","startWorkStatus","endWorkTime","endWorkStatus",
					          "classStartTime","classStartStatus","classEndTime","classEndStatus","mustAttnTime",
					          "allAttnTime","lackAttnTime","comeLateTime","leftEarlyTime","absenteeismTime","dataType"};
			
			return ExcelUtil.exportExcel(datas,keys,titles,"workbook.xls");
		}	
	}

	@Override
	public PageModel<Map<String, Object>> getSignRecordReportPageList(EmpAttn empAttn) {
		
		//??????????????????????????????????????????????????????
		if(empAttn.getEmpTypeId()==null){
			empAttn.setEmpTypeIdList(configService.getNeedEmpTypeIdList());
		}
		
	    if(empAttn.getFirstDepart()!=null) {
	    	empAttn.setDepartId(Long.valueOf(empAttn.getFirstDepart()));
	    }
		
		int page = empAttn.getPage() == null ? 0 : empAttn.getPage();
		int rows = empAttn.getRows() == null ? 0 : empAttn.getRows();
		
		if(empAttn.getStartTime()!=null){
			empAttn.setStartTime(DateUtils.parse(DateUtils.format(empAttn.getStartTime(), DateUtils.FORMAT_SHORT)+" 00:00:01", DateUtils.FORMAT_LONG));
		}
		
		if(empAttn.getEndTime()!=null){
			empAttn.setEndTime(DateUtils.parse(DateUtils.format(empAttn.getEndTime(), DateUtils.FORMAT_SHORT)+" 23:59:59", DateUtils.FORMAT_LONG));
		}
		
		PageModel<Map<String,Object>> pm = new PageModel<Map<String,Object>>();
		//????????????????????????
		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
		
		if(deptDataByUserList==null || deptDataByUserList.size()==0){
			pm.setPageNo(0);
			pm.setPageSize(0);
			pm.setTotal(0);
			empAttn.setOffset(pm.getOffset());
			empAttn.setLimit(pm.getLimit());
			pm.setRows(new ArrayList<Map<String,Object>>());
			return pm;
		}else{
			//??????????????????????????????
			List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(currentUser.getEmployeeId());//????????????
			if(subEmployeeIdList!=null&&subEmployeeIdList.size()>0){
				empAttn.setSubEmployeeIdList(subEmployeeIdList);
			}
			
			empAttn.setCurrentUserDepart(deptDataByUserList);
			pm.setPageNo(page);
			pm.setPageSize(rows);
			Integer total = empAttnMapper.getSignRecordReportCount(empAttn);
			pm.setTotal(total);
			
			empAttn.setOffset(pm.getOffset());
			empAttn.setLimit(pm.getLimit());
			
			//???????????????
			Map<Date, Boolean> workDayMap = annualVacationService.judgeWorkOrNotByPriod(empAttn.getStartTime(), empAttn.getEndTime());
			//????????????
			EmployeeClass classParam = new EmployeeClass();
			classParam.setStartTime(empAttn.getStartTime());
			classParam.setEndTime(empAttn.getEndTime());
			List<EmployeeClass> classList = employeeClassMapper.selectByCondition(classParam);
			Map<String,EmployeeClass> classMap = new HashMap<String,EmployeeClass>();
			for(EmployeeClass data:classList) {
				classMap.put(DateUtils.format(data.getClassDate(),DateUtils.FORMAT_SHORT)+"_"+String.valueOf(data.getEmployId()), data);
			}
			
			//??????
			List<Depart> departList = departService.getAllDepart();
			Map<Long,String> departMap = departList.stream().collect(Collectors.toMap(Depart :: getId, Depart :: getName));
			//?????????
			CompanyConfig companyConfigConditon = new CompanyConfig();//?????????????????????
			companyConfigConditon.setCode("typeOfWork");//????????????
			List<CompanyConfig> workTypeList = companyConfigService.getListByCondition(companyConfigConditon);
			Map<Long,CompanyConfig> workTypeMap = workTypeList.stream().collect(Collectors.toMap(CompanyConfig :: getId,a->a,(k1,k2)->k1));	
			//????????????
			Config configCondition = new Config();
			configCondition.setCode("whetherScheduling");//????????????
			List<Config> whetherSchedulingList = configService.getListByCondition(configCondition);
			Map<Long,Config> whetherSchedulingMap = whetherSchedulingList.stream().collect(Collectors.toMap(Config :: getId,a->a,(k1,k2)->k1));
			
			List<Map<String,Object>> datas = empAttnMapper.getSignRecordReportList(empAttn);
			
			for(Map<String,Object> map:datas){
				
				Long departId = (Long) map.get("departId");
				if(departMap.containsKey(departId)){
					map.put("departName", departMap.get(departId));
				}else{
					map.put("departName", "");
				}
				Long workType = (Long) map.get("workType");
				if(workTypeMap.containsKey(workType)){
					map.put("workTypeName", workTypeMap.get(workType).getDisplayName());
				}else{
					map.put("workTypeName", "");
				}
				Long whetherScheduling = (Long) map.get("whetherScheduling");
				String classPriod = "";
				String className = "";
				if("yes".equals(whetherSchedulingMap.get(whetherScheduling).getDisplayCode()) && "standard".equals(workTypeMap.get(workType).getDisplayCode())){
					String key = (String) map.get("attnDate")+"_"+(Long) map.get("employeeId");
					if(classMap!=null&&classMap.containsKey(key)) {
						Date classStartTime = classMap.get(key).getStartTime();
						Date classEndTime = classMap.get(key).getEndTime();
						classPriod = DateUtils.format(classStartTime, "HH:mm")+"-"+DateUtils.format(classEndTime, "HH:mm");
						className = classMap.get(key).getName();
					}
				}else{
					Date attnDate = DateUtils.parse((String) map.get("attnDate"), DateUtils.FORMAT_SHORT);
					if(workDayMap!=null&&workDayMap.containsKey(attnDate)) {
						boolean isWorkDay = workDayMap.get(attnDate);
						if(isWorkDay){/**?????????**/
							classPriod = "9:00-18:00";
							className = "A";//?????????
						}else{/**????????????**/
							classPriod = "";
						}
					}else {
						classPriod = "9:00-18:00";
						className = "A";//?????????
					}
				}
				map.put("classPriod",classPriod);
				map.put("className",className);
			}
			
			pm.setRows(datas);
			return pm;
		}	
	}

	@Override
	public HSSFWorkbook exportSignReportReport(EmpAttn empAttn) {
		
		//??????????????????????????????????????????????????????
		if(empAttn.getEmpTypeId()==null){
			empAttn.setEmpTypeIdList(configService.getNeedEmpTypeIdList());
		}
		//????????????????????????
		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
		
		if(deptDataByUserList==null || deptDataByUserList.size()==0){
			return null;
		}else{
			//??????????????????????????????
			List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(currentUser.getEmployeeId());//????????????
			if(subEmployeeIdList!=null&&subEmployeeIdList.size()>0){
				empAttn.setSubEmployeeIdList(subEmployeeIdList);
			}
			
			empAttn.setCurrentUserDepart(deptDataByUserList);
			
			Map<Date, Boolean> workDayMap = annualVacationService.judgeWorkOrNotByPriod(empAttn.getStartTime(), empAttn.getEndTime());
	        
			if(empAttn.getFirstDepart() != null) {
				empAttn.setDepartId(Long.valueOf(empAttn.getFirstDepart()));
			}
		    
		    if(empAttn.getStartTime()!=null){
				empAttn.setStartTime(DateUtils.parse(DateUtils.format(empAttn.getStartTime(), DateUtils.FORMAT_SHORT)+" 00:00:01", DateUtils.FORMAT_LONG));
			}
			
			if(empAttn.getEndTime()!=null){
				empAttn.setEndTime(DateUtils.parse(DateUtils.format(empAttn.getEndTime(), DateUtils.FORMAT_SHORT)+" 23:59:59", DateUtils.FORMAT_LONG));
			}
			
			//????????????
			EmployeeClass classParam = new EmployeeClass();
			classParam.setStartTime(empAttn.getStartTime());
			classParam.setEndTime(empAttn.getEndTime());
			List<EmployeeClass> classList = employeeClassMapper.selectByCondition(classParam);
			Map<String,EmployeeClass> classMap = new HashMap<String,EmployeeClass>();
			for(EmployeeClass data:classList) {
				classMap.put(DateUtils.format(data.getClassDate(),DateUtils.FORMAT_SHORT)+"_"+String.valueOf(data.getEmployId()), data);
			}
			//??????
			List<Depart> departList = departService.getAllDepart();
			Map<Long,String> departMap = departList.stream().collect(Collectors.toMap(Depart :: getId, Depart :: getName));
			//?????????
			CompanyConfig companyConfigConditon = new CompanyConfig();//?????????????????????
			companyConfigConditon.setCode("typeOfWork");//????????????
			List<CompanyConfig> workTypeList = companyConfigService.getListByCondition(companyConfigConditon);
			Map<Long,CompanyConfig> workTypeMap = workTypeList.stream().collect(Collectors.toMap(CompanyConfig :: getId,a->a,(k1,k2)->k1));	
			//????????????
			Config configCondition = new Config();
			configCondition.setCode("whetherScheduling");//????????????
			List<Config> whetherSchedulingList = configService.getListByCondition(configCondition);
			Map<Long,Config> whetherSchedulingMap = whetherSchedulingList.stream().collect(Collectors.toMap(Config :: getId,a->a,(k1,k2)->k1));
		    
		    List<Map<String,Object>> datas = empAttnMapper.getSignRecordReportList(empAttn);
			
			for(Map<String,Object> map:datas){
				
				Long departId = (Long) map.get("departId");
				if(departMap.containsKey(departId)){
					map.put("departName", departMap.get(departId));
				}else{
					map.put("departName", "");
				}
				Long workType = (Long) map.get("workType");
				if(workTypeMap.containsKey(workType)){
					map.put("workTypeName", workTypeMap.get(workType).getDisplayName());
				}else{
					map.put("workTypeName", "");
				}
				
				Long whetherScheduling = (Long) map.get("whetherScheduling");
				String classPriod = "";
				String className = "";
				if("yes".equals(whetherSchedulingMap.get(whetherScheduling).getDisplayCode()) && "standard".equals(workTypeMap.get(workType).getDisplayCode())){
					String key = (String) map.get("attnDate")+"_"+(Long) map.get("employeeId");
					if(classMap!=null&&classMap.containsKey(key)) {
						Date classStartTime = classMap.get(key).getStartTime();
						Date classEndTime = classMap.get(key).getEndTime();
						classPriod = DateUtils.format(classStartTime, "HH:mm")+"-"+DateUtils.format(classEndTime, "HH:mm");
						className = classMap.get(key).getName();
					}
				}else{
					Date attnDate = DateUtils.parse((String) map.get("attnDate"), DateUtils.FORMAT_SHORT);
					if(workDayMap!=null&&workDayMap.containsKey(attnDate)) {
						boolean isWorkDay = workDayMap.get(attnDate);
						if(isWorkDay){/**?????????**/
							classPriod = "9:00-18:00";
							className = "A";//?????????
						}else{/**????????????**/
							classPriod = "";
						}
					}else {
						classPriod = "9:00-18:00";
						className = "A";//?????????
					}
				}
				map.put("classPriod",classPriod);
				map.put("className",className);
			}
	
			
			String[] titles = { "????????????","????????????","??????","?????????","??????","??????","????????????","????????????"};
			String[] keys = { "code","cnName","departName","workTypeName","attnDate","dayofweek","classPriod","signTime"};
			
			return ExcelUtil.exportExcel(datas,keys,titles,"workbook.xls");
		}	
	}

	@Override
	public PageModel<ClassSetting> getAllClassShowList(ClassSetting classSetting) {

		
		int page = classSetting.getPage() == null ? 0 : classSetting.getPage();
		int rows = classSetting.getRows() == null ? 0 : classSetting.getRows();
		
		PageModel<ClassSetting> pm = new PageModel<ClassSetting>();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		Integer total = empAttnMapper.getClassSettingCount(classSetting);
		pm.setTotal(total);
		
		classSetting.setOffset(pm.getOffset());
		classSetting.setLimit(pm.getLimit());
		
		List<ClassSetting> datas = empAttnMapper.getClassSettingList(classSetting);
		
		pm.setRows(datas);
		return pm;
	}

	@Override
	public PageModel<Map<String, Object>> getMonthLackDetailPageList(EmpAttn empAttn) {
		Date endTime = DateUtils.getLastDay(empAttn.getStartTime());
		empAttn.setEndTime(endTime);
		List<Integer> departList = getDepartList(empAttn.getFirstDepart(),empAttn.getSecondDepart());
	    empAttn.setDepartList(departList);
		
		int page = empAttn.getPage() == null ? 0 : empAttn.getPage();
		int rows = empAttn.getRows() == null ? 0 : empAttn.getRows();
		
		PageModel<Map<String,Object>> pm = new PageModel<Map<String,Object>>();
		//??????????????????
		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
		if(deptDataByUserList==null || deptDataByUserList.size()==0 ){
			pm.setPageNo(0);
			pm.setPageSize(0);
			pm.setTotal(0);
			empAttn.setOffset(pm.getOffset());
			empAttn.setLimit(pm.getLimit());
			pm.setRows(new ArrayList<>());
			return pm;
		}else{
			List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(currentUser.getEmployeeId());//????????????
			if(subEmployeeIdList!=null&&subEmployeeIdList.size()>0){
				empAttn.setSubEmployeeIdList(subEmployeeIdList);
			}

			//??????????????????
			empAttn.setCurrentUserDepart(deptDataByUserList);
			pm.setPageNo(page);
			pm.setPageSize(rows);
			//??????????????????????????????
			if(empAttn.getEmpTypeId()==null){
				empAttn.setEmpTypeIdList(configService.getNeedEmpTypeIdList());
			}
			
			Integer total = empAttnMapper.getMonthLackDetailCount(empAttn);
			pm.setTotal(total);
			
			empAttn.setOffset(pm.getOffset());
			empAttn.setLimit(pm.getLimit());
			
			List<Map<String,Object>> datas = getMonthLackDetailList(empAttn);
			pm.setRows(datas);
			return pm;
		}
	}

	private List<Map<String, Object>> getMonthLackDetailList(EmpAttn empAttn) {
		List<Map<String,Object>> datas = empAttnMapper.getMonthLackDetailList(empAttn);
		//?????????
		CompanyConfig companyConfigConditon = new CompanyConfig();//?????????????????????
		companyConfigConditon.setCode("typeOfWork");//????????????
		List<CompanyConfig> workTypeList = companyConfigService.getListByCondition(companyConfigConditon);
		Map<Long,CompanyConfig> workTypeMap = workTypeList.stream().collect(Collectors.toMap(CompanyConfig :: getId,a->a,(k1,k2)->k1));	
		//????????????
		EmployeeClass classParam = new EmployeeClass();
		classParam.setStartTime(empAttn.getStartTime());
		classParam.setEndTime(empAttn.getEndTime());
		List<EmployeeClass> classList = employeeClassMapper.selectByCondition(classParam);
		Map<Long,List<EmployeeClass>> classMap = classList.stream().collect(Collectors.groupingBy(EmployeeClass :: getEmployId));
		//??????
		String attnDate = DateUtils.format(empAttn.getStartTime(), DateUtils.FORMAT_YYYY_MM);
		
		for(Map<String,Object> data:datas) {
			//?????????
			Long workType = (Long) data.get("workType");
			String workTypeName = "";
			if(workTypeMap!=null&&workType!=null&&workTypeMap.containsKey(workType)) {
				workTypeName = workTypeMap.get(workType).getDisplayName();
				if("comprehensive".equals(workTypeMap.get(workType).getDisplayCode())) {
					Double mustAttnTime = 	(Double) data.get("mustAttnTime");
					Double allAttnTime = (Double) data.get("allAttnTime");
					if(mustAttnTime!=null&&allAttnTime!=null) {
						data.put("lackAttnTime", (mustAttnTime-allAttnTime)>0?(mustAttnTime-allAttnTime):0d);
					}
				}
			}
			data.put("workTypeName", workTypeName);
			//??????
			Long employId = (Long) data.get("employId");
			String className = "A";
			if(classMap!=null&&employId!=null&&classMap.containsKey(employId)) {
				className = classMap.get(employId).get(0).getName();
			}
			data.put("className", className);
			//??????
			data.put("attnDate", attnDate);
		}
		
		
		return datas;
	}

	@Override
	public HSSFWorkbook exportMonthLackDetailReport(EmpAttn empAttn) {
		//??????????????????
		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
		if(deptDataByUserList==null || deptDataByUserList.size()==0){
			return null;
		}else{
			List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(currentUser.getEmployeeId());//????????????
			if(subEmployeeIdList!=null&&subEmployeeIdList.size()>0){
				empAttn.setSubEmployeeIdList(subEmployeeIdList);
			}

			Date endTime = DateUtils.getLastDay(empAttn.getStartTime());
			empAttn.setEndTime(endTime);
			List<Integer> departList = getDepartList(empAttn.getFirstDepart(),empAttn.getSecondDepart());
			empAttn.setDepartList(departList);
			//????????????
			empAttn.setCurrentUserDepart(deptDataByUserList);
			//??????????????????????????????
			if(empAttn.getEmpTypeId()==null){
				empAttn.setEmpTypeIdList(configService.getNeedEmpTypeIdList());
			}
			
			List<Map<String,Object>> datas = getMonthLackDetailList(empAttn);
			
			String[] titles = { "????????????","??????","??????","????????????","?????????","??????","??????","??????+???????????????","???????????????","???????????????"};
			String[] keys = { "code","cnName","departName","positionName","workTypeName","className","attnDate","lateAndEarly","absenteeismTime","lackAttnTime"};
			
			return ExcelUtil.exportExcel(datas,keys,titles,"workbook.xls");
		}
	}

	@Override
	public PageModel<Map<String, Object>> getMonthLackTotalPageList(
			EmpAttn empAttn) {
		Date endTime = DateUtils.getLastDay(empAttn.getStartTime());
		empAttn.setEndTime(endTime);
		List<Integer> departList = getDepartList(empAttn.getFirstDepart(),empAttn.getSecondDepart());
	    empAttn.setDepartList(departList);
		
		int page = empAttn.getPage() == null ? 0 : empAttn.getPage();
		int rows = empAttn.getRows() == null ? 0 : empAttn.getRows();
		
		PageModel<Map<String,Object>> pm = new PageModel<Map<String,Object>>();
		//??????????????????
		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
		if(deptDataByUserList==null || deptDataByUserList.size()==0){
			pm.setPageNo(0);
			pm.setPageSize(0);
			pm.setTotal(0);
			empAttn.setOffset(pm.getOffset());
			empAttn.setLimit(pm.getLimit());
			pm.setRows(new ArrayList<>());
			return pm;
		}else{
			List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(currentUser.getEmployeeId());//????????????
			if(subEmployeeIdList!=null&&subEmployeeIdList.size()>0){
				empAttn.setSubEmployeeIdList(subEmployeeIdList);
			}
			pm.setPageNo(page);
			pm.setPageSize(rows);
			empAttn.setCurrentUserDepart(deptDataByUserList);
			
			if(empAttn.getEmpTypeId()==null){
				empAttn.setEmpTypeIdList(configService.getNeedEmpTypeIdList());
			}
			
			
			Integer total = empAttnMapper.getMonthLackTotalCount(empAttn);
			pm.setTotal(total);
			
			empAttn.setOffset(pm.getOffset());
			empAttn.setLimit(pm.getLimit());
			
			List<Map<String,Object>> datas = getMonthLackTotalList(empAttn);
			
			pm.setRows(datas);
			return pm;
		}
	}

	private List<Map<String, Object>> getMonthLackTotalList(EmpAttn empAttn) {

		final List<Map<String,Object>> datas = empAttnMapper.getMonthLackTotalList(empAttn);
		final String yearMonth = DateUtils.format(empAttn.getStartTime(),"yyyy-MM");
		
		datas.stream()
        .filter(map -> null == map.get("workDate"))
        .map(map -> map.put("workDate", yearMonth))
        .collect(Collectors.toList());
		
		return datas;
	}

	@Override
	public HSSFWorkbook exportMonthLackTotalReport(EmpAttn empAttn) {
		//??????????????????
		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
		if(deptDataByUserList==null || deptDataByUserList.size()==0){
			return null;
		}else{
			List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(currentUser.getEmployeeId());//????????????
			if(subEmployeeIdList!=null&&subEmployeeIdList.size()>0){
				empAttn.setSubEmployeeIdList(subEmployeeIdList);
			}
			Date endTime = DateUtils.getLastDay(empAttn.getStartTime());
			empAttn.setEndTime(endTime);
			List<Integer> departList = getDepartList(empAttn.getFirstDepart(),empAttn.getSecondDepart());
			empAttn.setDepartList(departList);
			empAttn.setCurrentUserDepart(deptDataByUserList);
			if(empAttn.getEmpTypeId()==null){
				empAttn.setEmpTypeIdList(configService.getNeedEmpTypeIdList());
			}
			
			List<Map<String,Object>> datas = getMonthLackTotalList(empAttn);
			
			String[] titles = { "????????????","??????","??????","????????????","??????","????????????"};
			String[] keys = { "code","cnName","departName","positionName","workDate","total"};
			
			return ExcelUtil.exportExcel(datas,keys,titles,"workbook.xls");
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public String readExcelFile(MultipartFile file) throws IOException {
		
		/**code,email,attnRecordId,signTime**/
 		List<List<Object>> datas = ExcelUtil.readExcel(file, "signRecord.xlsx", 2);
		
 		if(null != datas){
 			datas.remove(0);
			datas.stream().forEach(list -> {  
				    String code;
					String emial;
					Date signTime;
					code = (String) list.get(0);
					emial = (String) list.get(1);
					signTime = (Date) list.get(2);
					saveSignRecordOfExcel(code,emial,signTime);
					
	        }); 
			
			return "????????????";
 		}else{
 			return "???????????????excel???????????????";
 		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveSignRecordOfExcel(String code,String email,Date signTime){
		
		//1.??????code??????email????????????   
		Employee eCondition = new Employee();
		Employee emp = null;
		
		if(null != code){
			eCondition.setCode(code);
			emp = employeeService.getByCondition(eCondition);
		}else{
			if(null != email ){
				eCondition.setEmail(email);
				emp = employeeService.getByCondition(eCondition);
			}else{
				
			}
		}
		
		//2.????????????????????????????????????????????????
		if(null != emp && null != emp.getId()){
			Date CURRENT_TIME = DateUtils.parse(DateUtils.getNow(),DateUtils.FORMAT_LONG);
			
			AttnSignRecord signRecord = new AttnSignRecord();
			signRecord.setCompanyId(emp.getCompanyId());
			signRecord.setEmployeeId(emp.getId());
			signRecord.setEmployeeName(emp.getCnName());
			signRecord.setSignTime(signTime);
			signRecord.setCreateTime(CURRENT_TIME);
			signRecord.setCreateUser("oaAdmin");
			signRecord.setUpdateTime(CURRENT_TIME);
			signRecord.setUpdateUser("oaAdmin");
			signRecord.setDelFlag(0);
			signRecord.setType(0);
			empSignRecordMapper.insert(signRecord);
		}else{
			logger.error("?????????????????? {}??????????????? {} ?????????????????????",code,email);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Map<String, Object> updateAttnTime(String employeeIds,
			String attnDate, String startTime, String endTime, String dataType,String remark)
			throws OaException {
		Map<String, Object> result = new HashMap<String, Object>();
		if(employeeIds==null||"".equals(employeeIds)){
			throw new OaException("?????????????????????");
		}
		if(attnDate==null||"".equals(attnDate)){
			throw new OaException("???????????????????????????");
		}
		if(!("2".equals(dataType)||"3".equals(dataType))){
			throw new OaException("???????????????????????????");
		}
		logger.info("??????????????????????????????employeeIds="+employeeIds+",attnDate="+attnDate
				+",startTime="+startTime+",endTime="+endTime
				+",dataType="+dataType);
		User user = userService.getCurrentUser();
		String createUser = user.getEmployee().getCnName();
		Long companyId = user.getCompanyId();
		for(String employeeId:employeeIds.split(",")){
			AttnUpdateLog attnUpdateLog = new AttnUpdateLog();
			 if("3".equals(dataType)){
				//?????????????????????????????????????????????????????????????????????
				//?????????????????????????????????????????????
				AttnWorkHours param = new AttnWorkHours();
				param.setEmployId(Long.valueOf(employeeId));
				param.setWorkDate(DateUtils.parse(attnDate, DateUtils.FORMAT_SHORT));
				List<AttnWorkHours> absoluteList = attnWorkHoursMapper.getAbsoluteAttnWorkHoursList(param);
				if(absoluteList!=null&&absoluteList.size()>0){
					absoluteList.get(0).setStartTime(DateUtils.parse(startTime, DateUtils.FORMAT_LONG));
					absoluteList.get(0).setEndTime(DateUtils.parse(endTime,DateUtils.FORMAT_LONG));
					absoluteList.get(0).setUpdateTime(new Date());
					absoluteList.get(0).setUpdateUser(createUser);
					attnWorkHoursMapper.updateById(absoluteList.get(0));
					Date formDate  = absoluteList.get(0).getWorkDate();//????????????
			 		Date yesterday = DateUtils.addDay(DateUtils.getToday(),-1);
					if(formDate.after(yesterday)){//???????????????????????????????????????????????????????????????????????????????????????
						logger.info("????????????????????????????????????????????????????????????????????????????????????");
					}else{
						/**??????????????????**/
						attnStatisticsService.calculateAttnForm(absoluteList.get(0).getCompanyId(),absoluteList.get(0).getEmployeeId(),formDate,yesterday);
					}
				}else{
					AttnWorkHours attnWorkHours = new AttnWorkHours();
					attnWorkHours.setCompanyId(companyId);
					attnWorkHours.setEmployId(Long.valueOf(employeeId));
					attnWorkHours.setWorkDate(DateUtils.parse(attnDate, DateUtils.FORMAT_SHORT));
					attnWorkHours.setDataType(Integer.valueOf(dataType));
					attnWorkHours.setStartTime(DateUtils.parse(startTime,DateUtils.FORMAT_LONG));
					attnWorkHours.setEndTime(DateUtils.parse(endTime,DateUtils.FORMAT_LONG));
					attnWorkHours.setCreateTime(new Date());
					attnWorkHours.setCreateUser(createUser);
					attnWorkHours.setDataReason("HR??????");
					attnWorkHours.setDelFlag(0);
					attnStatisticsService.setAttStatisticsForm(attnWorkHours);
				}
				attnUpdateLog.setUpdateStartTime(DateUtils.parse(startTime, DateUtils.FORMAT_LONG));
				attnUpdateLog.setUpdateEndTime(DateUtils.parse(endTime,DateUtils.FORMAT_LONG));	
			}else if("2".equals(dataType)){
				//??????????????????????????????
				AttnWorkHours param = new AttnWorkHours();
				param.setEmployId(Long.valueOf(employeeId));
				param.setWorkDate(DateUtils.parse(attnDate, DateUtils.FORMAT_SHORT));
				List<AttnWorkHours> clockedList = attnWorkHoursMapper.getClockedAttnWorkHoursList(param);
				if(clockedList!=null&&clockedList.size()>0){
					Date inputTime = DateUtils.parse(startTime,DateUtils.FORMAT_LONG);
					Date clockedStartTime = null;
					Date clockedEndTime = null;
					for(AttnWorkHours clocked:clockedList){
						if(clockedStartTime==null){
							clockedStartTime = clocked.getStartTime();
						}else{
							if(clocked.getStartTime()!=null&&clocked.getStartTime().getTime()<clockedStartTime.getTime()){
								clockedStartTime = clocked.getStartTime();
							}
						}
                        if(clockedEndTime==null){
                        	clockedEndTime =  clocked.getEndTime();
						}else{
							if(clocked.getEndTime()!=null&&clocked.getEndTime().getTime()>clockedEndTime.getTime()){
								clockedEndTime = clocked.getEndTime();
							}
						}
					}
					//????????????????????????????????????????????????????????????
					AttnWorkHours attnWorkHours = new AttnWorkHours();
					attnWorkHours.setCompanyId(companyId);
					attnWorkHours.setEmployId(Long.valueOf(employeeId));
					attnWorkHours.setWorkDate(DateUtils.parse(attnDate, DateUtils.FORMAT_SHORT));
					attnWorkHours.setDataType(Integer.valueOf(dataType));
					attnWorkHours.setCreateTime(new Date());
					attnWorkHours.setCreateUser(createUser);
					attnWorkHours.setDataReason("HR??????");
					attnWorkHours.setDelFlag(0);
					
					EmployeeClass employeeClass = new EmployeeClass();
					employeeClass.setEmployId(Long.valueOf(employeeId));
					employeeClass.setClassDate(DateUtils.parse(attnDate, DateUtils.FORMAT_SHORT));
					EmployeeClass empClass = employeeClassService.getEmployeeClassSetting(employeeClass);
			    	if(empClass!=null){
			    		//????????????????????????????????????????????????????????????????????????????????????
			    		if(empClass.getStartTime().getTime()>=inputTime.getTime()){
			    			//???????????????????????????
			    			attnWorkHours.setStartTime(inputTime);
			    			attnWorkHours.setEndTime(clockedEndTime);
			    			if(clockedEndTime==null){
			    				attnWorkHours.setEndTime(inputTime);
			    			}
							attnStatisticsService.setAttStatisticsForm(attnWorkHours);
			    		}else if(empClass.getEndTime().getTime()<=inputTime.getTime()){
			    			//???????????????????????????
			    			attnWorkHours.setStartTime(clockedStartTime);
			    			if(clockedStartTime==null){
			    				attnWorkHours.setStartTime(inputTime);
			    			}
							attnWorkHours.setEndTime(inputTime);
							attnStatisticsService.setAttStatisticsForm(attnWorkHours);
			    		}else if(clockedStartTime!=null&&clockedEndTime!=null){
			    			//??????????????????????????????????????????????????????
			    			if(clockedStartTime.getTime()>=inputTime.getTime()){
			    				attnWorkHours.setStartTime(inputTime);
								attnWorkHours.setEndTime(clockedEndTime);
								attnStatisticsService.setAttStatisticsForm(attnWorkHours);
			    			}else if(clockedEndTime.getTime()<=inputTime.getTime()){
			    				attnWorkHours.setStartTime(inputTime);
								attnWorkHours.setEndTime(clockedEndTime);
								attnStatisticsService.setAttStatisticsForm(attnWorkHours);
			    			}
			    		}else if(clockedStartTime!=null&&clockedEndTime==null){
			    			//?????????????????????????????????
			    			if(clockedStartTime.getTime()>=inputTime.getTime()){
			    				attnWorkHours.setStartTime(inputTime);
			    				attnWorkHours.setEndTime(clockedStartTime);
			    				attnStatisticsService.setAttStatisticsForm(attnWorkHours);
			    			}else{
			    				attnWorkHours.setStartTime(clockedStartTime);
			    				attnWorkHours.setEndTime(inputTime);
			    				attnStatisticsService.setAttStatisticsForm(attnWorkHours);
			    			}
						}else if(clockedStartTime==null&&clockedEndTime!=null){
							//?????????????????????????????????
							if(clockedEndTime.getTime()>=inputTime.getTime()){
								attnWorkHours.setStartTime(inputTime);
			    				attnWorkHours.setEndTime(clockedEndTime);
			    				attnStatisticsService.setAttStatisticsForm(attnWorkHours);
							}
						}else{
							//???????????????
							attnWorkHours.setStartTime(inputTime);
		    				attnWorkHours.setEndTime(inputTime);
		    				attnStatisticsService.setAttStatisticsForm(attnWorkHours);
						}
			    	}else{
			    		logger.error("???????????????"+employeeId+"??????????????????,???????????????????????????????????????????????????????????????");
			    	}
				}else{
					logger.error("???????????????"+employeeId+"????????????????????????,????????????????????????");
				}
				AttnSignRecord signRecord = new AttnSignRecord();
				signRecord.setCompanyId(companyId);
				signRecord.setEmployeeId(Long.valueOf(employeeId));
				signRecord.setSignTime(DateUtils.parse(startTime,DateUtils.FORMAT_LONG));
				signRecord.setCreateTime(new Date());
				signRecord.setCreateUser(createUser);
				signRecord.setRemark("HR??????");
				signRecord.setDelFlag(0);
				signRecord.setType(2);
				empSignRecordMapper.insert(signRecord);
				attnUpdateLog.setInsertAttnTime(DateUtils.parse(startTime, DateUtils.FORMAT_LONG));
			}
			attnUpdateLog.setEmployeeId(Long.valueOf(employeeId));
			attnUpdateLog.setUpdateAttnDate(DateUtils.parse(attnDate, DateUtils.FORMAT_SHORT));		
			attnUpdateLog.setType(Integer.valueOf(dataType));
			attnUpdateLog.setRemark(remark);
			attnUpdateLog.setCreateTime(new Date());
			attnUpdateLog.setCreateUser(createUser);
			try {
				attnUpdateLogMapper.insert(attnUpdateLog);
			} catch (Exception e) {

			}
			
		}
		result.put("flag", true);
		result.put("message", "????????????????????????");
		return result;
	}

	@Override
	public EmpAttn getAttnTimeByIdAndAttnTime(String employeeIds, String attnDate,Integer type) throws Exception {		
		EmpAttn attn=new EmpAttn();
		attn.setEmployId(Long.parseLong(employeeIds));
		attn.setAttnDate(DateUtils.parse(attnDate, DateUtils.FORMAT_SHORT));
		EmpAttn attnTimeByIdAndAttnTime =new EmpAttn();
		if(type==2){
			attnTimeByIdAndAttnTime= empAttnMapper.getAttnTimeByIdAndAttnTime(attn);
		}else if(type==3){
			AttnWorkHours attnWorkHours=new AttnWorkHours();
			attnWorkHours.setEmployeeId(Long.parseLong(employeeIds));
			attnWorkHours.setWorkDate(DateUtils.parse(attnDate, DateUtils.FORMAT_SHORT));
			List<AttnWorkHours> absoluteList = attnWorkHoursMapper.getAbsoluteAttnWorkHoursList(attnWorkHours);
			if(absoluteList!=null && absoluteList.size()>0){
				Date startTime = absoluteList.get(0).getStartTime();
				Date endTime = absoluteList.get(0).getEndTime();
				attnTimeByIdAndAttnTime.setStartWorkTime(startTime);
				attnTimeByIdAndAttnTime.setEndWorkTime(endTime);
				attnTimeByIdAndAttnTime.setAttnDate(absoluteList.get(0).getWorkDate());
			}else{
				attnTimeByIdAndAttnTime= empAttnMapper.getAttnTimeByIdAndAttnTime(attn);				
			}
			
		}
		if(attnTimeByIdAndAttnTime.getAttnDate()==null){
			return attnTimeByIdAndAttnTime;
		}
		if(attnTimeByIdAndAttnTime.getStartWorkTime()!=null){
			Date startWorkTime = attnTimeByIdAndAttnTime.getStartWorkTime();
			String substringStart = startWorkTime.toString().substring(10, 18);
			Date parse = DateUtils.parse(substringStart, "HH:mm:ss");
			attnTimeByIdAndAttnTime.setStartWorkTime(DateUtils.parse(substringStart, "HH:mm:ss"));
		}
		if(attnTimeByIdAndAttnTime.getEndWorkTime()!=null){
			Date endWorkTime = attnTimeByIdAndAttnTime.getEndWorkTime();			
			String substringEnd = endWorkTime.toString().substring(10, 18);		
			Date parseend = DateUtils.parse(substringEnd, "HH:mm:ss");		
			attnTimeByIdAndAttnTime.setEndWorkTime(DateUtils.parse(substringEnd, "HH:mm:ss"));
		}
		return attnTimeByIdAndAttnTime;
	}

	@Override
	public PageModel<Map<String, Object>> getEmployeeSignReportPageList(
			EmpAttn empAttn) {
		
		Map<Date, Boolean> workDayMap = annualVacationService.judgeWorkOrNotByPriod(empAttn.getStartTime(), empAttn.getEndTime());
		
		if(empAttn.getFirstDepart() != null) {
			empAttn.setDepartId(Long.valueOf(empAttn.getFirstDepart()));
		}
		
		int page = empAttn.getPage() == null ? 0 : empAttn.getPage();
		int rows = empAttn.getRows() == null ? 0 : empAttn.getRows();
		
		PageModel<Map<String,Object>> pm = new PageModel<Map<String,Object>>();
		
		//??????????????????????????????????????????????????????
		if(empAttn.getEmpTypeId()==null){
			empAttn.setEmpTypeIdList(configService.getNeedEmpTypeIdList());
		}
	
		//????????????????????????
		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
		
		if(deptDataByUserList==null || deptDataByUserList.size()==0){
			pm.setPageNo(0);
			pm.setPageSize(0);
			pm.setTotal(0);
			empAttn.setOffset(pm.getOffset());
			empAttn.setLimit(pm.getLimit());
			pm.setRows(new ArrayList<Map<String,Object>>());
			return pm;
		}else{
			//??????????????????????????????
			List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(currentUser.getEmployeeId());//????????????
			if(subEmployeeIdList!=null&&subEmployeeIdList.size()>0){
				empAttn.setSubEmployeeIdList(subEmployeeIdList);
			}
			empAttn.setCurrentUserDepart(deptDataByUserList);
			pm.setPageNo(page);
			pm.setPageSize(rows);
			Integer total = empAttnMapper.getEmployeeSignReportCount(empAttn);
			pm.setTotal(total);
			
			empAttn.setOffset(pm.getOffset());
			empAttn.setLimit(pm.getLimit());
			
			//????????????
			EmployeeClass classParam = new EmployeeClass();
			classParam.setStartTime(empAttn.getStartTime());
			classParam.setEndTime(empAttn.getEndTime());
			List<EmployeeClass> classList = employeeClassMapper.selectByCondition(classParam);
			Map<String,EmployeeClass> classMap = new HashMap<String,EmployeeClass>();
			for(EmployeeClass data:classList) {
				classMap.put(DateUtils.format(data.getClassDate(),DateUtils.FORMAT_SHORT)+"_"+String.valueOf(data.getEmployId()), data);
			}
			//??????
			List<Depart> departList = departService.getAllDepart();
			Map<Long,String> departMap = departList.stream().collect(Collectors.toMap(Depart :: getId, Depart :: getName));
			//?????????
			CompanyConfig companyConfigConditon = new CompanyConfig();//?????????????????????
			companyConfigConditon.setCode("typeOfWork");//????????????
			List<CompanyConfig> workTypeList = companyConfigService.getListByCondition(companyConfigConditon);
			Map<Long,CompanyConfig> workTypeMap = workTypeList.stream().collect(Collectors.toMap(CompanyConfig :: getId,a->a,(k1,k2)->k1));	
			//????????????
			Config configCondition = new Config();
			configCondition.setCode("whetherScheduling");//????????????
			List<Config> whetherSchedulingList = configService.getListByCondition(configCondition);
			Map<Long,Config> whetherSchedulingMap = whetherSchedulingList.stream().collect(Collectors.toMap(Config :: getId,a->a,(k1,k2)->k1));
			
			List<Map<String,Object>> datas = empAttnMapper.getEmployeeSignReportList(empAttn);
			
			for(Map<String,Object> map:datas){
				
				Long departId = (Long) map.get("departId");
				if(departMap.containsKey(departId)){
					map.put("departName", departMap.get(departId));
				}else{
					map.put("departName", "");
				}
				Long workType = (Long) map.get("workType");
				if(workTypeMap.containsKey(workType)){
					map.put("workTypeName", workTypeMap.get(workType).getDisplayName());
				}else{
					map.put("workTypeName", "");
				}
				String classPriod = "";
				String className = "";
				Long whetherScheduling = (Long) map.get("whetherScheduling");
				if("yes".equals(whetherSchedulingMap.get(whetherScheduling).getDisplayCode()) && "standard".equals(workTypeMap.get(workType).getDisplayCode())){
					String key = (String) map.get("attnDate")+"_"+(Long) map.get("employeeId");
					if(classMap!=null&&classMap.containsKey(key)) {
						Date classStartTime = classMap.get(key).getStartTime();
						Date classEndTime = classMap.get(key).getEndTime();
						classPriod = DateUtils.format(classStartTime, "HH:mm")+"-"+DateUtils.format(classEndTime, "HH:mm");
						className = classMap.get(key).getName();
					}
					
				}else{
					Date attnDate = DateUtils.parse((String) map.get("attnDate"), DateUtils.FORMAT_SHORT);
					if(workDayMap!=null&&workDayMap.containsKey(attnDate)) {
						boolean isWorkDay = workDayMap.get(attnDate);
						if(isWorkDay){/**?????????**/
							classPriod = "9:00-18:00";
							className = "A";//?????????
						}else{/**????????????**/
							classPriod = "";
						}
					}else {
						classPriod = "9:00-18:00";
						className = "A";//?????????
					}
				}
				map.put("classPriod",classPriod);
				map.put("className",className);
			}
			
			pm.setRows(datas);
			return pm;
		}	
	}

	@Override
	public HSSFWorkbook exportEmployeeSignReport(EmpAttn empAttn) {
		
		//??????????????????????????????????????????????????????
		if(empAttn.getEmpTypeId()==null){
			empAttn.setEmpTypeIdList(configService.getNeedEmpTypeIdList());
		}
		
		//????????????????????????
		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
		if(deptDataByUserList==null || deptDataByUserList.size()==0){
			return null;
		}else{
			
			//??????????????????????????????
			List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(currentUser.getEmployeeId());//????????????
			if(subEmployeeIdList!=null&&subEmployeeIdList.size()>0){
				empAttn.setSubEmployeeIdList(subEmployeeIdList);
			}
			empAttn.setCurrentUserDepart(deptDataByUserList);
			
			Map<Date, Boolean> workDayMap = annualVacationService.judgeWorkOrNotByPriod(empAttn.getStartTime(), empAttn.getEndTime());
			
			if(empAttn.getFirstDepart()!=null) {
				empAttn.setDepartId(Long.valueOf(empAttn.getFirstDepart()));
			}
		    
			//????????????
			EmployeeClass classParam = new EmployeeClass();
			classParam.setStartTime(empAttn.getStartTime());
			classParam.setEndTime(empAttn.getEndTime());
			List<EmployeeClass> classList = employeeClassMapper.selectByCondition(classParam);
			Map<String,EmployeeClass> classMap = new HashMap<String,EmployeeClass>();
			for(EmployeeClass data:classList) {
				classMap.put(DateUtils.format(data.getClassDate(),DateUtils.FORMAT_SHORT)+"_"+String.valueOf(data.getEmployId()), data);
			}
		    //??????
			List<Depart> departList = departService.getAllDepart();
			Map<Long,String> departMap = departList.stream().collect(Collectors.toMap(Depart :: getId, Depart :: getName));
			//?????????
			CompanyConfig companyConfigConditon = new CompanyConfig();//?????????????????????
			companyConfigConditon.setCode("typeOfWork");//????????????
			List<CompanyConfig> workTypeList = companyConfigService.getListByCondition(companyConfigConditon);
			Map<Long,CompanyConfig> workTypeMap = workTypeList.stream().collect(Collectors.toMap(CompanyConfig :: getId,a->a,(k1,k2)->k1));	
			//????????????
			Config configCondition = new Config();
			configCondition.setCode("whetherScheduling");//????????????
			List<Config> whetherSchedulingList = configService.getListByCondition(configCondition);
			Map<Long,Config> whetherSchedulingMap = whetherSchedulingList.stream().collect(Collectors.toMap(Config :: getId,a->a,(k1,k2)->k1));
		    
		    List<Map<String,Object>> datas = empAttnMapper.getEmployeeSignReportList(empAttn);
			
			for(Map<String,Object> map:datas){
				
				Long departId = (Long) map.get("departId");
				if(departMap.containsKey(departId)){
					map.put("departName", departMap.get(departId));
				}else{
					map.put("departName", "");
				}
				Long workType = (Long) map.get("workType");
				if(workTypeMap.containsKey(workType)){
					map.put("workTypeName", workTypeMap.get(workType).getDisplayName());
				}else{
					map.put("workTypeName", "");
				}
				String classPriod = "";
				String className = "";
				Long whetherScheduling = (Long) map.get("whetherScheduling");
				if("yes".equals(whetherSchedulingMap.get(whetherScheduling).getDisplayCode()) && "standard".equals(workTypeMap.get(workType).getDisplayCode())){
					String key = (String) map.get("attnDate")+"_"+(Long) map.get("employeeId");
					if(classMap!=null&&classMap.containsKey(key)) {
						Date classStartTime = classMap.get(key).getStartTime();
						Date classEndTime = classMap.get(key).getEndTime();
						classPriod = DateUtils.format(classStartTime, "HH:mm")+"-"+DateUtils.format(classEndTime, "HH:mm");
						className = classMap.get(key).getName();
					}
				}else{
					Date attnDate = DateUtils.parse((String) map.get("attnDate"), DateUtils.FORMAT_SHORT);
					if(workDayMap!=null&&workDayMap.containsKey(attnDate)) {
						boolean isWorkDay = workDayMap.get(attnDate);
						if(isWorkDay){/**?????????**/
							classPriod = "9:00-18:00";
							className = "A";//?????????
						}else{/**????????????**/
							classPriod = "";
						}
					}else {
						classPriod = "9:00-18:00";
						className = "A";//?????????
					}
				}
				map.put("classPriod",classPriod);
				map.put("className",className);
			}
	
			
			String[] titles = { "????????????","????????????","??????","?????????","????????????","??????","??????","????????????","????????????"};
			String[] keys = { "code","cnName","departName","workTypeName","attnDate","dayofweek","className","classPriod","signTime"};
			
			return ExcelUtil.exportExcel(datas,keys,titles,"workbook.xls");
		}	
	}

	@Override
	public PageModel<Map<String, Object>> getLocationCheckInData(EmpAttn empAttn) {
		
		int page = empAttn.getPage() == null ? 0 : empAttn.getPage();
		int rows = empAttn.getRows() == null ? 0 : empAttn.getRows();
		
		PageModel<Map<String,Object>> pm = new PageModel<Map<String,Object>>();
		
		//??????????????????????????????????????????????????????
		if(empAttn.getEmpTypeId()==null){
			empAttn.setEmpTypeIdList(configService.getNeedEmpTypeIdList());
		}
	
		//????????????????????????
		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
		
		if(deptDataByUserList==null || deptDataByUserList.size()==0){
			pm.setPageNo(0);
			pm.setPageSize(0);
			pm.setTotal(0);
			empAttn.setOffset(pm.getOffset());
			empAttn.setLimit(pm.getLimit());
			pm.setRows(new ArrayList<Map<String,Object>>());
			return pm;
		}else{
			//??????????????????????????????
			List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(currentUser.getEmployeeId());//????????????
			//?????????????????????????????????
			EmployeeBaseInfoParam param = new EmployeeBaseInfoParam();
			param.setCode(empAttn.getCode());
			param.setCnName(empAttn.getCnName());
			param.setDepartId(empAttn.getDepartId());
			param.setEmpTypeId(empAttn.getEmpTypeId());
			if(empAttn.getEmpTypeId()==null){
				param.setEmpTypeIdList(configService.getNeedEmpTypeIdList());
			}
			if(subEmployeeIdList!=null&&subEmployeeIdList.size()>0){
				param.setSubEmployeeIdList(subEmployeeIdList);//??????????????????
			}
			param.setCurrentUserDepart(deptDataByUserList);//???????????????????????????
			param.setWhetherScheduling(empAttn.getWhetherScheduling());
			param.setWorkType(empAttn.getWorkType());
			List<EmployeeBaseInfoDTO> baseInfoList = employeeBaseInfoMapper.getBaseInfoList(param);
			Map<Long,EmployeeBaseInfoDTO> baseInfoMap = baseInfoList.stream().collect(Collectors.toMap(EmployeeBaseInfoDTO :: getId,a->a,(k1,k2)->k1));
			
			//?????????
			CompanyConfig companyConfigConditon = new CompanyConfig();//?????????????????????
			companyConfigConditon.setCode("typeOfWork");//????????????
			List<CompanyConfig> workTypeList = companyConfigService.getListByCondition(companyConfigConditon);
			Map<Long,CompanyConfig> workTypeMap = workTypeList.stream().collect(Collectors.toMap(CompanyConfig :: getId,a->a,(k1,k2)->k1));
			
			List<Long> employeeIdList = new ArrayList<Long>();
			for(EmployeeBaseInfoDTO data:baseInfoList){
				employeeIdList.add(data.getId());
			}
			
			EmployeeClass classParam = new EmployeeClass();
			classParam.setStartTime(empAttn.getStartTime());
			classParam.setEndTime(empAttn.getEndTime());
			classParam.setEmployeeIds(employeeIdList);
			List<EmployeeClass> classList = employeeClassMapper.getEmployeeClassList(classParam);
			Map<Long,List<EmployeeClass>> classMap = classList.stream().collect(Collectors.groupingBy(EmployeeClass :: getEmployId));

			empAttn.setEmployeeIdList(employeeIdList);//?????????????????????????????????
			pm.setPageNo(page);
			pm.setPageSize(rows);
			Integer total = empAttnMapper.getLocationCheckInDataCount(empAttn);
			pm.setTotal(total);
			
			empAttn.setOffset(pm.getOffset());
			empAttn.setLimit(pm.getLimit());
			
			List<Map<String,Object>> datas = empAttnMapper.getLocationCheckInDataList(empAttn);
			
			for(Map<String,Object> map:datas){
				Long key = (Long) map.get("employeeId");
				String attnDate = (String) map.get("attnDate");
				
				if(baseInfoMap!=null&&baseInfoMap.containsKey(key)){
					map.put("code", baseInfoMap.get(key).getCode());//????????????
					map.put("cnName", baseInfoMap.get(key).getCnName());//????????????
					map.put("departName", baseInfoMap.get(key).getDepartName());//????????????
					map.put("workTypeName", "");//?????????
					if(workTypeMap!=null&&workTypeMap.containsKey(baseInfoMap.get(key).getWorkType())){
						map.put("workTypeName", workTypeMap.get(baseInfoMap.get(key).getWorkType()).getDisplayName());//?????????
					}
				}
				
				//??????
				Double distance = (Double)map.get("distance");
				String distanceStr = " ";
				if(distance!=null){
					 DecimalFormat df = new DecimalFormat("#.00");
					 distanceStr = df.format(distance);
				}
				map.put("distance", distanceStr);
				//??????
				
				String address = (String) map.get("address");
				if(StringUtils.isNotBlank(address)&&!"{}".equals(address)){
					JSONObject addressObj = JSON.parseObject(address);
					if(addressObj!=null){
						String resultAddress = addressObj.getString("result");
						addressObj = JSON.parseObject(resultAddress);
						address = addressObj!=null&&addressObj.containsKey("address")?addressObj.getString("address"):" ";
					}
				}else{
					address = " ";
				}
				map.put("address", address);
				
				map.put("className","");
				map.put("classPriod","");
				if(classMap!=null&&classMap.containsKey(key)){
					Map<Date,List<EmployeeClass>> classMapEmpDate = classMap.get(key).stream().collect(Collectors.groupingBy(EmployeeClass :: getClassDate));
				    
					if(classMapEmpDate!=null&&classMapEmpDate.containsKey(DateUtils.parse(attnDate, DateUtils.FORMAT_SHORT))){
						EmployeeClass classP = classMapEmpDate.get(DateUtils.parse(attnDate, DateUtils.FORMAT_SHORT)).get(0);
				    	if(classP!=null&&classP.getStartTime()!=null){
				    		map.put("className",classP.getName());
					    	String classPriod = DateUtils.format(classP.getStartTime(), "HH:mm")
					    			+" - "+DateUtils.format(classP.getEndTime(), "HH:mm");
					    	map.put("classPriod",classPriod);
				    	}else{
				    		map.put("className","??????");
				    	}
				    }
				}
				
			}
			
			pm.setRows(datas);
			return pm;
		}	
	}

	@Override
	public HSSFWorkbook exportLocationCheckInData(EmpAttn empAttn) {
		//??????????????????????????????????????????????????????
		if(empAttn.getEmpTypeId()==null){
			empAttn.setEmpTypeIdList(configService.getNeedEmpTypeIdList());
		}
		
		//????????????????????????
		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
		if(deptDataByUserList==null || deptDataByUserList.size()==0){
			return null;
		}else{
			
			//??????????????????????????????
			List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(currentUser.getEmployeeId());//????????????
			//?????????????????????????????????
			EmployeeBaseInfoParam param = new EmployeeBaseInfoParam();
			param.setCode(empAttn.getCode());
			param.setCnName(empAttn.getCnName());
			param.setDepartId(empAttn.getDepartId());
			param.setEmpTypeId(empAttn.getEmpTypeId());
			if(empAttn.getEmpTypeId()==null){
				param.setEmpTypeIdList(configService.getNeedEmpTypeIdList());
			}
			if(subEmployeeIdList!=null&&subEmployeeIdList.size()>0){
				param.setSubEmployeeIdList(subEmployeeIdList);
			}
			param.setCurrentUserDepart(deptDataByUserList);
			param.setWhetherScheduling(empAttn.getWhetherScheduling());
			param.setWorkType(empAttn.getWorkType());
			List<EmployeeBaseInfoDTO> baseInfoList = employeeBaseInfoMapper.getBaseInfoList(param);
			Map<Long,EmployeeBaseInfoDTO> baseInfoMap = baseInfoList.stream().collect(Collectors.toMap(EmployeeBaseInfoDTO :: getId,a->a,(k1,k2)->k1));
			
			//?????????
			CompanyConfig companyConfigConditon = new CompanyConfig();//?????????????????????
			companyConfigConditon.setCode("typeOfWork");//????????????
			List<CompanyConfig> workTypeList = companyConfigService.getListByCondition(companyConfigConditon);
			Map<Long,CompanyConfig> workTypeMap = workTypeList.stream().collect(Collectors.toMap(CompanyConfig :: getId,a->a,(k1,k2)->k1));
			
			List<Long> employeeIdList = new ArrayList<Long>();
			for(EmployeeBaseInfoDTO data:baseInfoList){
				employeeIdList.add(data.getId());
			}
			
			EmployeeClass classParam = new EmployeeClass();
			classParam.setStartTime(empAttn.getStartTime());
			classParam.setEndTime(empAttn.getEndTime());
			classParam.setEmployeeIds(employeeIdList);
			List<EmployeeClass> classList = employeeClassMapper.getEmployeeClassList(classParam);
			Map<Long,List<EmployeeClass>> classMap = classList.stream().collect(Collectors.groupingBy(EmployeeClass :: getEmployId));
		    
		    List<Map<String,Object>> datas = empAttnMapper.getLocationCheckInDataList(empAttn);
			
			for(Map<String,Object> map:datas){
				
				String sucessStr = "??????";
				Integer delFlag = (Integer)map.get("delFlag");
				sucessStr = (delFlag!=null&&delFlag.intValue()==0)?"??????":"??????";
				map.put("delFlag", sucessStr);
				
				Long key = (Long) map.get("employeeId");
				String attnDate = (String) map.get("attnDate");
				
				if(baseInfoMap!=null&&baseInfoMap.containsKey(key)){
					map.put("code", baseInfoMap.get(key).getCode());//????????????
					map.put("cnName", baseInfoMap.get(key).getCnName());//????????????
					map.put("departName", baseInfoMap.get(key).getDepartName());//????????????
					map.put("workTypeName", "");//?????????
					if(workTypeMap!=null&&workTypeMap.containsKey(baseInfoMap.get(key).getWorkType())){
						map.put("workTypeName", workTypeMap.get(baseInfoMap.get(key).getWorkType()).getDisplayName());//?????????
					}
				}
				
				//??????
				Double distance = (Double)map.get("distance");
				String distanceStr = " ";
				if(distance!=null){
					 double d = 114.145;
					 DecimalFormat df = new DecimalFormat("#.00");
					 distanceStr = df.format(d);
				}
				map.put("distance", distanceStr);
				//??????
				String address = (String) map.get("address");
				if(StringUtils.isNotBlank(address)&&!"{}".equals(address)){
					JSONObject addressObj = JSON.parseObject(address);
					if(addressObj!=null){
						String resultAddress = addressObj.getString("result");
						addressObj = JSON.parseObject(resultAddress);
						address = addressObj!=null&&addressObj.containsKey("address")?addressObj.getString("address"):" ";
					}
				}else{
					address = " ";
				}
				map.put("address", address);
				
				map.put("className","");
				map.put("classPriod","");
				if(classMap!=null&&classMap.containsKey(key)){
					Map<Date,List<EmployeeClass>> classMapEmpDate = classMap.get(key).stream().collect(Collectors.groupingBy(EmployeeClass :: getClassDate));
				    
					if(classMapEmpDate!=null&&classMapEmpDate.containsKey(DateUtils.parse(attnDate, DateUtils.FORMAT_SHORT))){
						EmployeeClass classP = classMapEmpDate.get(DateUtils.parse(attnDate, DateUtils.FORMAT_SHORT)).get(0);
				    	map.put("className",classP.getName());
				    	String classPriod = DateUtils.format(classP.getStartTime(), "HH:mm")
				    			+" - "+DateUtils.format(classP.getEndTime(), "HH:mm");
				    	map.put("classPriod",classPriod);
				    }
				}
			}
	
			String[] titles = { "????????????","????????????","??????","?????????","????????????","??????","??????","????????????","????????????","??????","ip??????","????????????(???)","????????????"};
			String[] keys = { "code","cnName","departName","workTypeName","attnDate","dayofweek","className","classPriod","signTime","address","ip","distance","delFlag"};
			
			return ExcelUtil.exportExcel(datas,keys,titles,"workbook.xls");
		}	
	}

	@Override
	public void deleteSignRecordById(Long id, Integer delFlag) {
		AttnSignRecord data = new AttnSignRecord();
		data.setId(id);
		data.setDelFlag(delFlag);
		empSignRecordMapper.updateByPrimaryKeySelective(data);
	} 
}
