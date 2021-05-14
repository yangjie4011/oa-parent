package com.ule.oa.base.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ule.oa.base.mapper.AnnualVacationMapper;
import com.ule.oa.base.mapper.ApplicationEmployeeClassDetailMapper;
import com.ule.oa.base.mapper.ApplicationEmployeeClassMapper;
import com.ule.oa.base.mapper.ClassSettingMapper;
import com.ule.oa.base.mapper.EmployeeClassMapper;
import com.ule.oa.base.mapper.EmployeeMapper;
import com.ule.oa.base.mapper.ScheduleGroupMapper;
import com.ule.oa.base.mapper.SurplusWorkhoursMapper;
import com.ule.oa.base.po.ApplicationEmployeeClass;
import com.ule.oa.base.po.ApplicationEmployeeClassDetail;
import com.ule.oa.base.po.CompanyConfig;
import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.service.AnnualVacationService;
import com.ule.oa.base.service.ApplicationEmployeeClassDetailService;
import com.ule.oa.base.service.CompanyConfigService;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.utils.DateUtils;

/**
 * @ClassName: 排班申请明细表
 * @Description: 排班申请明细表
 * @author yangjie
 * @date 2017年8月30日
 */
@Service
public class ApplicationEmployeeClassDetailServiceImpl implements ApplicationEmployeeClassDetailService{
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private ApplicationEmployeeClassDetailMapper applicationEmployeeClassDetailMapper;
	@Resource
	private AnnualVacationService annualVacationService;
	@Resource
	private CompanyConfigService companyConfigService;
	@Resource
	private EmployeeService employeeService;
	@Resource
	private ConfigService configService;
	@Resource
	private AnnualVacationMapper annualVacationMapper;
	@Autowired
	private UserService userService;
	@Autowired
	private ClassSettingMapper classSettingMapper;
	@Autowired
	private EmployeeMapper employeeMapper;
	@Autowired
	private ApplicationEmployeeClassMapper applicationEmployeeClassMapper;
	@Autowired
	private SurplusWorkhoursMapper surplusWorkhoursMapper;
	@Autowired
	private EmployeeClassMapper employeeClassMapper;
	@Autowired
	private ScheduleGroupMapper scheduleGroupMapper;

	@Override
	public ApplicationEmployeeClassDetail getEmployeeClassSetting(
			ApplicationEmployeeClassDetail applicationEmployeeClass) {
		return null;
	}

	@Override
	public Map<Long, ApplicationEmployeeClassDetail> getEmployeeClassMap(
			ApplicationEmployeeClassDetail condition) {
		return null;
	}

	@Override
	public List<ApplicationEmployeeClassDetail> getEmployeeClassPeriodList(
			ApplicationEmployeeClassDetail condition) {
		return null;
	}

	@Override
	public ApplicationEmployeeClassDetail getEmployeeClass(
			ApplicationEmployeeClassDetail condition) {
		return null;
	}

	@Override
	public List<ApplicationEmployeeClassDetail> getEmployeeClassInfoByDepartId(
			Long departId) throws Exception {
		return null;
	}

	@Override
	public Integer getMustClassSettingCountByDepartId(Long departId) {
		return null;
	}

	@Override
	public List<ApplicationEmployeeClassDetail> selectByCondition(
			ApplicationEmployeeClassDetail classDetail) {
		return applicationEmployeeClassDetailMapper.selectByCondition(classDetail);
	}

	@Override
	public List<ApplicationEmployeeClassDetail> getEmployeeClassHours(
			ApplicationEmployeeClassDetail condition) {
		ApplicationEmployeeClass applicationEmployeeClass = new ApplicationEmployeeClass();
		applicationEmployeeClass.setId(condition.getAttnApplicationEmployClassId());
		List<ApplicationEmployeeClass> employeeClassList = applicationEmployeeClassMapper.getByCondition(applicationEmployeeClass);
		List<ApplicationEmployeeClassDetail> list = new ArrayList<ApplicationEmployeeClassDetail>();
		List<ApplicationEmployeeClassDetail> list1 = new ArrayList<ApplicationEmployeeClassDetail>();
		if(employeeClassList!=null&&employeeClassList.size()>0){
			if(employeeClassList.get(0).getIsMove().intValue()==0){
				if(employeeClassList.get(0).getApprovalStatus()==null||employeeClassList.get(0).getApprovalStatus().intValue()==100
						||employeeClassList.get(0).getApprovalStatus().intValue()==300||employeeClassList.get(0).getApprovalStatus().intValue()==400){
					 list = applicationEmployeeClassDetailMapper.getEmployeeClassHours(condition);
					 list1 = applicationEmployeeClassDetailMapper.selectByCondition(condition);
				}else if(employeeClassList.get(0).getApprovalStatus().intValue()==200){
					EmployeeClass param2 = new EmployeeClass();
					param2.setDepartId(employeeClassList.get(0).getDepartId());
					param2.setStartTime(employeeClassList.get(0).getClassMonth());
					param2.setEndTime(DateUtils.getLastDay(employeeClassList.get(0).getClassMonth()));
					List<EmployeeClass> list2 = employeeClassMapper.getClassHours(param2);
					for(EmployeeClass eClass:list2){
						ApplicationEmployeeClassDetail detail = new ApplicationEmployeeClassDetail();
						detail.setEmployName(eClass.getEmployName());
						detail.setEmployId(eClass.getEmployId());
						detail.setShouldTime(eClass.getShouldTime());
						detail.setMustAttnTime(eClass.getMustAttnTime());
						list.add(detail);
					}
					List<EmployeeClass> list3 = employeeClassMapper.selectByCondition(param2);
					for(EmployeeClass eClass:list3){
						ApplicationEmployeeClassDetail detail = new ApplicationEmployeeClassDetail();
						detail.setEmployName(eClass.getEmployName());
						detail.setEmployId(eClass.getEmployId());
						detail.setShouldTime(eClass.getShouldTime());
						detail.setClassDate(eClass.getClassDate());
						detail.setClassSettingId(eClass.getClassSettingId());
						detail.setIsMove(0);
						list1.add(detail);
					}
				}
			}else if(employeeClassList.get(0).getIsMove().intValue()==1){
				condition.setIsMove(1);
				list = applicationEmployeeClassDetailMapper.getEmployeeClassHours(condition);
				list1 = applicationEmployeeClassDetailMapper.selectByCondition(condition);
				List<ApplicationEmployeeClassDetail> returnList = new ArrayList<ApplicationEmployeeClassDetail>();
				applicationEmployeeClass.setId(null);
				applicationEmployeeClass.setDepartId(employeeClassList.get(0).getDepartId());
				applicationEmployeeClass.setClassMonth(employeeClassList.get(0).getClassMonth());
				applicationEmployeeClass.setIsMove(0);
				List<ApplicationEmployeeClass> original = applicationEmployeeClassMapper.getByCondition(applicationEmployeeClass);
				List<ApplicationEmployeeClassDetail> originalList = new ArrayList<ApplicationEmployeeClassDetail>();
				EmployeeClass param3 = new EmployeeClass();
				param3.setDepartId(employeeClassList.get(0).getDepartId());
				param3.setStartTime(employeeClassList.get(0).getClassMonth());
				param3.setEndTime(DateUtils.getLastDay(employeeClassList.get(0).getClassMonth()));
				List<EmployeeClass> list2 = employeeClassMapper.getClassHours(param3);
				String flag = "";
				for(EmployeeClass eClass:list2){
					flag += eClass.getEmployId()+",";
					ApplicationEmployeeClassDetail detail = new ApplicationEmployeeClassDetail();
					detail.setEmployName(eClass.getEmployName());
					detail.setEmployId(eClass.getEmployId());
					detail.setShouldTime(eClass.getShouldTime());
					detail.setMustAttnTime(eClass.getMustAttnTime());
					originalList.add(detail);
				}
				for(ApplicationEmployeeClassDetail detail:list){
					if(flag.indexOf(String.valueOf(detail.getEmployId()))==-1){
						detail.setMustAttnTime(detail.getMustAttnTime()/2);
						originalList.add(detail);
					}
				}
				for(ApplicationEmployeeClassDetail param:originalList){
					List<Date> classDateList = new ArrayList<Date>();
					for(ApplicationEmployeeClassDetail param1:list1){
						if(param.getEmployId().equals(param1.getEmployId())){
							classDateList.add(param1.getClassDate());
						}
					}
					if(classDateList!=null&&classDateList.size()>0){
						ApplicationEmployeeClassDetail condition1 = new ApplicationEmployeeClassDetail();
						condition1.setEmployId(param.getEmployId());
						condition1.setClassDateList(classDateList);
						List<ApplicationEmployeeClassDetail> minus = applicationEmployeeClassDetailMapper.getEmployeeClassHoursByDates(condition1);
						param.setMinusHours((minus!=null&&minus.size()>0)?minus.get(0).getMustAttnTime():0);
					}
					for(ApplicationEmployeeClassDetail param2:list){
						if(param.getEmployId().equals(param2.getEmployId())){
							double time1 = param.getMustAttnTime()==null?0:param.getMustAttnTime();
							double time2 = param2.getMustAttnTime()==null?0:param2.getMustAttnTime();
							double time3 = param.getMinusHours()==null?0:param.getMinusHours();
							param.setMustAttnTime(time1+time2-time3);
							returnList.add(param);
						}
					}
				}
				return returnList;
			}
		}
		//List<Employee> empList = employeeMapper.getListByDepartId(condition.getDepartId());
		List<Employee> empList = scheduleGroupMapper.getAllGroupEmp(condition.getGroupId(),null,employeeClassList.get(0).getClassMonth());
		Long workTypeId = null;
		Long whetherScheduleId = null;
		CompanyConfig companyConfigConditon = new CompanyConfig();
		companyConfigConditon.setCode("typeOfWork");
		List<CompanyConfig> workTypeList = companyConfigService.getListByCondition(companyConfigConditon);
		for(CompanyConfig companyConfig:workTypeList){
			if("standard".equals(companyConfig.getDisplayCode())){
				workTypeId = companyConfig.getId();
				break;
			}
		}
		Config configCondition = new Config();
		configCondition.setCode("whetherScheduling");
		List<Config> whetherSchedulingList = configService.getListByCondition(configCondition);
        for(Config config:whetherSchedulingList){
			if("yes".equals(config.getDisplayCode())){
				whetherScheduleId = config.getId();
				break;
			}
		}
        List<Employee> should_list = new ArrayList<Employee>();
		for(Employee employee:empList){
			if(employee.getQuitTime()!=null&&employee.getQuitTime().getTime()<employeeClassList.get(0).getClassMonth().getTime()){
				continue;
			}
			should_list.add(employee);
		}
		Map<Long,ApplicationEmployeeClassDetail> map = new HashMap<Long,ApplicationEmployeeClassDetail>();
		for(ApplicationEmployeeClassDetail classDetail:list){
			map.put(classDetail.getEmployId(), classDetail);
		}
		List<ApplicationEmployeeClassDetail> new_emoloyee = new ArrayList<ApplicationEmployeeClassDetail>();
		for(Employee employee:should_list){
			if(map!=null&&map.containsKey(employee.getId())){
				map.get(employee.getId()).setFirstEntryTime(employee.getFirstEntryTime());
				map.get(employee.getId()).setQuitTime(employee.getQuitTime());
				map.get(employee.getId()).setShouldTime(getShouldTime(employee,employeeClassList.get(0).getClassMonth()));
			}else if(map!=null&&!map.containsKey(employee.getId())){
				ApplicationEmployeeClassDetail detail = new ApplicationEmployeeClassDetail();
				detail.setEmployId(employee.getId());
				detail.setEmployName(employee.getCnName());
				detail.setMustAttnTime(0d);
				detail.setFirstEntryTime(employee.getFirstEntryTime());
				detail.setQuitTime(employee.getQuitTime());
				detail.setShouldTime(getShouldTime(employee,employeeClassList.get(0).getClassMonth()));
				new_emoloyee.add(detail);
			}
		}
		for (Map.Entry<Long, ApplicationEmployeeClassDetail> entry: map.entrySet()) {
			if(entry.getValue().getMustAttnTime()==null){
				entry.getValue().setMustAttnTime(0d);
			}
			new_emoloyee.add(entry.getValue());
		}
		return new_emoloyee;
	}
	
	//获取员工的应出勤时间 
	public Double getShouldTime(Employee employee,Date month_start){
		double shouldTime = 0;
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(month_start);
			calendar.add(Calendar.MONTH, 1);
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			Date month_end = calendar.getTime();
			boolean isEndTime = true;
			do{
				if(employee.getQuitTime()==null&&employee.getFirstEntryTime()!=null&&DateUtils.format(month_start, "yyyy-MM").equals(DateUtils.format(employee.getFirstEntryTime(), "yyyy-MM"))){
					if(employee.getFirstEntryTime().getTime()<=month_start.getTime()){
						if(annualVacationService.judgeWorkOrNot(month_start)){
							shouldTime = shouldTime + 8;
						}
					}
				}else if(employee.getQuitTime()==null&&employee.getFirstEntryTime()!=null&&Integer.valueOf(DateUtils.format(month_start, "yyyyMM"))>Integer.valueOf(DateUtils.format(employee.getFirstEntryTime(), "yyyyMM"))){
					if(annualVacationService.judgeWorkOrNot(month_start)){
						shouldTime = shouldTime + 8;
					}
				}
				if(employee.getQuitTime()!=null&&DateUtils.format(month_start, "yyyy-MM").equals(DateUtils.format(employee.getQuitTime(), "yyyy-MM"))){
					if(employee.getQuitTime().getTime()>=month_start.getTime()){
						if(annualVacationService.judgeWorkOrNot(month_start)){
							shouldTime = shouldTime + 8;
						}
					}
				}
				month_start = DateUtils.addDay(month_start, 1);
				if(DateUtils.getIntervalDays(month_start, month_end) < 0) {
					isEndTime = false;
				}
			} while(isEndTime);
		} catch (Exception e) {
			logger.error("获取用户={}应出勤时间失败",employee.getCnName(),e);
		}
		return shouldTime;
	}

	@Override
	public List<ApplicationEmployeeClassDetail> getEmployeeClassSetByMonth(
			ApplicationEmployeeClassDetail condition) {
		return applicationEmployeeClassDetailMapper.getEmployeeClassSetByMonth(condition);
	}

	@Override
	public int deleteByQuitTime(ApplicationEmployeeClassDetail condition) {
		return applicationEmployeeClassDetailMapper.deleteByQuitTime(condition);
	}
}
