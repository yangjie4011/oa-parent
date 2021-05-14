package com.ule.oa.base.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ule.oa.base.mapper.EmpMsgMapper;
import com.ule.oa.base.mapper.EmployeeMapper;
import com.ule.oa.base.mapper.RemoteWorkRegisterMapper;
import com.ule.oa.base.po.AnnualVacation;
import com.ule.oa.base.po.ApplicationEmployeeClass;
import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.EmpMsg;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.po.Position;
import com.ule.oa.base.po.RemoteWorkRegister;
import com.ule.oa.base.service.AnnualVacationService;
import com.ule.oa.base.service.ApplicationEmployeeClassService;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.EmpMsgService;
import com.ule.oa.base.service.EmployeeClassService;
import com.ule.oa.base.service.RemoteWorkRegisterService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.PageModel;

/**
 * @ClassName: 远程工作管理
 * @Description: 远程工作管理实现类
 * @author zhoujinliang
 * @date 2020年2月24日14:47:22
 */
@Service
public class RemoteWorkRegisterServiceImpl implements RemoteWorkRegisterService {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired 
	private RemoteWorkRegisterMapper empWorkManagementMapper;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private EmployeeMapper employeeMapper;
	@Autowired
	private UserService userService;
	@Autowired
	private ApplicationEmployeeClassService applicationEmployeeClassService;
	@Autowired
	private AnnualVacationService annualVacationService;
	@Autowired
	private ConfigService configService;
	@Autowired
	private EmployeeClassService employeeClassService;
	
	@Override
	public Map<String, Object> queryWorkRegistByCondition(Long empId,
			String month) {
		// TODO Auto-generated method stub
		Map<String,Object> result = new HashMap<String,Object>();
		Date now = new Date();
		if(month==null||"".equals(month)){
			month = DateUtils.format(now, "yyyy-MM");
		}else{
			now = DateUtils.parse(month+"-01", DateUtils.FORMAT_SHORT);
		}
		
		//表头，星期和天数
		List<String> weekDays = new ArrayList<String>();
		//表头，天数
		List<String> days = new ArrayList<String>();
		
		List<String> dates = new ArrayList<String>();
		weekDays.add("员工编号");
		weekDays.add("职位");
		weekDays.add("姓名");
		weekDays.add("应出勤工时");
		days.add("");
		days.add("");
		days.add("");
		days.add("");
		
		dates.add("");
		dates.add("");
		dates.add("");
		dates.add("");
		
		Date fristDay = DateUtils.getFirstDay(now);
		Date lastDay = DateUtils.getLastDay(now);
		
		Double shouldTime = applicationEmployeeClassService.getShouldTime(fristDay, lastDay);
		
		List<Employee> byEmpId = employeeMapper.getByEmpId(empId);
		result.put("shouldTime", shouldTime);
		result.put("employee", byEmpId.get(0));
		RemoteWorkRegister remoteWorkRegister =new RemoteWorkRegister();
		remoteWorkRegister.setEmployeeId(empId);
		remoteWorkRegister.setStartTime(fristDay);
		remoteWorkRegister.setEndTime(lastDay);
		remoteWorkRegister.setDelFlag(0);
		remoteWorkRegister.setIsRemote(0L);
		
		//点击员工 的远程工作集合
		List<RemoteWorkRegister> datas1 = empWorkManagementMapper.getDetailByEidAndMonth(remoteWorkRegister);
	
		
		int i = 1;
		dates.add(DateUtils.format(fristDay, "yyyy-MM-dd"));
		while(true){
			String week = DateUtils.getWeek(fristDay);
			weekDays.add(week);
			days.add(String.valueOf(i));
			fristDay = DateUtils.addDay(fristDay, 1);
			
			dates.add(DateUtils.format(fristDay, "yyyy-MM-dd"));
			i = i + 1;
			if(!DateUtils.format(fristDay, "yyyy-MM").equals(DateUtils.format(lastDay, "yyyy-MM"))){
				break;
			}
		}
		result.put("weekDays", weekDays);
		result.put("days", days);
		Map<String, Object> mpadata=new HashMap<String, Object>();
		for(RemoteWorkRegister data1 : datas1){
			String day = DateUtils.getDayOfMonth(data1.getRegisterDate());
			mpadata.put(day, data1);
		}
		result.put("dates", dates);
		result.put("workDetail", mpadata);
		String nowDate = DateUtils.format(new Date(), DateUtils.FORMAT_LONG);
		result.put("nowDate",nowDate);
		
		result.put("count", 0);
		if(datas1!=null&&datas1.size()>0){
			result.put("count", datas1.size());
		}
		
		return result;	
	}

	@Override
	public int save(RemoteWorkRegister remoteWorkRegister) {
		return empWorkManagementMapper.save(remoteWorkRegister);
	}

	@Override
	public int updateById(RemoteWorkRegister remoteWorkRegister) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveMapStr(String info) {
		// TODO Auto-generated method stub
		JSONObject array = JSONObject.fromObject(info);
		Map<String, String> map = (Map)array;//排班详细信息
	
		if(map!=null){
			//直接修改申请明细数据
            for (Map.Entry<String, String> detail: map.entrySet()) {
            	//查询明细是否存在数据      .split("_")[0] 班次 .split("_")[1] 0远程 2不远程 逻辑删除  .split("_")[2]是原本存在的id值
            	String employId = detail.getKey().split("_")[0];
            	String classDate = detail.getKey().split("_")[1];
            	String classSettingId = (detail.getValue().split("_")[0]!=null&&!"".equals(detail.getValue().split("_")[0])&&!"undefined".equals(detail.getValue().split("_")[0]))?detail.getValue().split("_")[0]:null;
            	String isRemote =(detail.getValue().split("_")[1]!=null&&!"".equals(detail.getValue().split("_")[1])&&!"undefined".equals(detail.getValue().split("_")[1]))?detail.getValue().split("_")[1]:null;	
            	String workId =(detail.getValue().split("_")[2]!=null&&!"".equals(detail.getValue().split("_")[2])&&!"undefined".equals(detail.getValue().split("_")[2]))?detail.getValue().split("_")[2]:null;	

            	if(StringUtils.isNotBlank(workId)&&!"undefined".equals(workId) ){//原本存在的远程 修改或逻辑删除
            		RemoteWorkRegister remoteWorkRegisters=new RemoteWorkRegister();
            		remoteWorkRegisters.setId(Long.parseLong(workId));
            		remoteWorkRegisters.setEmployeeId(Long.parseLong(employId));
            		remoteWorkRegisters.setClasssSettingId((classSettingId!=null?Long.parseLong(classSettingId):null));           		
            		remoteWorkRegisters.setRegisterDate(DateUtils.parse(classDate,DateUtils.FORMAT_SHORT));
            		remoteWorkRegisters.setUpdateTime(new Date());
            		remoteWorkRegisters.setUpdateUser(userService.getCurrentUser().getEmployee().getCnName());          		
            		if("2".equals(isRemote)){
            			remoteWorkRegisters.setDelFlag(1);
            			remoteWorkRegisters.setIsRemote(1L);
            			//逻辑删除
            			logger.info("逻辑删除远程登记信息 日期" +classDate + " 员工id"+employId+  " id"+workId+ "操作人:"+userService.getCurrentUser().getEmployee().getCnName());
            		}else if("0".equals(isRemote)){
            			remoteWorkRegisters.setDelFlag(0);
            			remoteWorkRegisters.setIsRemote(Long.parseLong(isRemote));
            			//修班改次
            			logger.info("修班改次远程登记信息 日期" +classDate + " 员工id"+employId+  " id"+workId+  "操作人:"+userService.getCurrentUser().getEmployee().getCnName());
            		}
        			empWorkManagementMapper.updateById(remoteWorkRegisters);
            	}else{
            		if("0".equals(isRemote)){
                		//登记分两种情况 一种直接登记  一直换班次
                		RemoteWorkRegister remoteWorkRegisters=new RemoteWorkRegister();
                		remoteWorkRegisters.setEmployeeId(Long.parseLong(employId));
                		remoteWorkRegisters.setClasssSettingId((classSettingId!=null?Long.parseLong(classSettingId):null));
                		remoteWorkRegisters.setIsRemote(Long.parseLong(isRemote));
                		remoteWorkRegisters.setApproveStatus("2");//默认为不提交
                		remoteWorkRegisters.setRegisterDate(DateUtils.parse(classDate,DateUtils.FORMAT_SHORT));
                		remoteWorkRegisters.setCreateTime(new Date());
                		remoteWorkRegisters.setDelFlag(0);
                		remoteWorkRegisters.setCreateUser(userService.getCurrentUser().getEmployee().getCnName());
                		empWorkManagementMapper.save(remoteWorkRegisters);
                		logger.info("新增远程登记信息 日期" +classDate + " 员工id"+employId+ "操作人:"+userService.getCurrentUser().getEmployee().getCnName());
                	}
            }
            }
		}
	}
	
	@Override
	public Map<String, Object> isWorkDate(String date,Long employeeId) {
		// TODO Auto-generated method stub
		
		Map<String,Object> result = new HashMap<String,Object>();
		
		Date dateFomart = DateUtils.parse(date,DateUtils.FORMAT_SHORT);
		Date firstDay = DateUtils.getFirstDay(new Date());//这个月第一天
		Date lastDay = DateUtils.getLastDay(new Date());//这个月最后一天
		
		Long whetherScheduling = null;
		
		Config param = new Config();
		param.setCode("whetherScheduling");
        List<Config> whetherSchedulingLsit = configService.getListByCondition(param);
        for(Config data:whetherSchedulingLsit){
        	if("yes".equals(data.getDisplayCode())){
        		whetherScheduling = data.getId();
        	}
        }
		
        Employee emp = employeeMapper.getById(employeeId);
        
        if(emp!=null&&emp.getWhetherScheduling()!=null&&whetherScheduling!=null
        		&&whetherScheduling.longValue()==emp.getWhetherScheduling().longValue()){
        	EmployeeClass toadycondition = new EmployeeClass();
			toadycondition.setEmployId(employeeId);
			toadycondition.setClassDate(DateUtils.parse(date, DateUtils.FORMAT_SHORT));
			EmployeeClass empClass = employeeClassService.getEmployeeClassSetting(toadycondition);
			if(empClass==null||empClass.getStartTime()==null){
				result.put("success", false);
    			result.put("msg", "不能登记非工作日的日期");
    			return result;
			}
        }else{
        	//非工作日期不能登记
    		boolean isWorkDate = annualVacationService.judgeWorkOrNot(dateFomart);
    		if(!isWorkDate){//非工作日
    			result.put("success", false);
    			result.put("msg", "不能登记非工作日的日期");
    			return result;
    		}
        }
		
//		int firstMonthDate = DateUtils.compareDate(dateFomart,firstDay);
//		if(firstMonthDate==2){//小于
//			result.put("success", false);
//			result.put("msg", "不能登记月前的日期");
//			return result;
//		}
//		int lastMonthDate = DateUtils.compareDate(dateFomart,lastDay);
//		if(lastMonthDate==1){//登记时间大于月末
//			//判断今天是否是 月末的三个工作日之中
//			Date monthlast3Day = DateUtils.getLastDay(new Date());
//			//如果登记下一个月的数据  只能在本月末倒数三个工作日之中登记
//			boolean isEndTime1 = true;
//		    Integer day3 = 0;
//		    do{
//		    	if(annualVacationService.judgeWorkOrNot(monthlast3Day)){//判断是否是周末或者节假日
//		    		day3 += 1;
//				}
//		    	monthlast3Day = DateUtils.addDay(monthlast3Day, -1);
//				if(day3==2) {
//					isEndTime1 = false;
//				}
//			} while(isEndTime1);
//		    
//		    Date nextMonthLastDay = DateUtils.getLastDay(DateUtils.addMonth(new Date(),1));//下个月最后一天  不能大于下个月的最后一天
//		    if(DateUtils.compareDate(dateFomart,nextMonthLastDay)==1){
//		    	result.put("success", false);
//				result.put("msg", "不能编辑信息");
//				return result;
//		    }
//		    int compareDate = DateUtils.compareDate(new Date(),monthlast3Day);//当天时间大于 月末的最后三天方可编辑 //当天时间大于 月末的最后三天方可编辑
//			if(compareDate!=2){//当天时间大于 月末的最后三天方可编辑
//				
//			}else{
//				result.put("success", false);
//				result.put("msg", "不能编辑下个月的信息 只能在月末三个工作日内");
//				return result;
//			}
//		}
		
	    
		result.put("success", true);
		return result;
	}	
}
