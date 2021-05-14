package com.ule.oa.admin.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ule.oa.base.mapper.EmployeeMapper;
import com.ule.oa.base.po.AnnualVacation;
import com.ule.oa.base.po.CompanyConfig;
import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.AnnualVacationService;
import com.ule.oa.base.service.AttnReportService;
import com.ule.oa.base.service.CompanyConfigService;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.utils.DateUtils;

/**
 * 测试定时接口数据
 * @author yangjie
 *
 */
@Controller
@RequestMapping("testData")
public class TimerTestDataController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private AttnReportService attnReportService;
	@Autowired
	private AnnualVacationService annualVacationService;
	@Autowired
	private ConfigService configService;
	@Autowired
	private CompanyConfigService companyConfigService;
	@Autowired
	private EmployeeMapper employeeMapper;
	@Autowired
	private UserService userService;
	
	
	@ResponseBody
	@RequestMapping("/generateWCandJTallowReport.htm")
	public Map<String, String> generateWCandJTallowReport(String month) {
		
		User user = userService.getCurrentUser();
		
		Map<String, String> result = new HashMap<String, String>();
		
		//查询排班类型和工时类型配置表
		CompanyConfig companyConfigConditon = new CompanyConfig();//查询配置表条件
		companyConfigConditon.setCode("typeOfWork");//工时类型
		List<CompanyConfig> workTypeList = companyConfigService.getListByCondition(companyConfigConditon);
		Map<Long,String> workTypeMap =  workTypeList.stream().collect(Collectors.toMap(CompanyConfig :: getId, CompanyConfig :: getDisplayCode));
		
		Config configCondition = new Config();
		configCondition.setCode("whetherScheduling");//是否排班
		List<Config> whetherSchedulingList = configService.getListByCondition(configCondition);
		Map<Long,String> whetherSchedulingMap = whetherSchedulingList.stream().collect(Collectors.toMap(Config :: getId, Config :: getDisplayCode));
		
		//查询上个月的法定节假日
		Date today = DateUtils.parse(month, DateUtils.FORMAT_SHORT);
		Date lastMonthStart = DateUtils.getFirstDay(today);
		Date lastMonthEnd = DateUtils.getLastDay(lastMonthStart);
		AnnualVacation vacationParam = new AnnualVacation();
		vacationParam.setStartDate(lastMonthStart);
		vacationParam.setEndDate(lastMonthEnd);
		List<AnnualVacation> vacationList = annualVacationService.getListByCondition(vacationParam);
		Map<Date,Integer> vacationMap = vacationList.stream().collect(Collectors.toMap(AnnualVacation :: getAnnualDate, AnnualVacation :: getType));
		List<Long> empTypeIdList = configService.getNeedEmpTypeIdList();		
		//查询所有符合条件的员工（del_flag=0+离职时间>=上月一号+入职时间<=上月月末+非外地员工）
		List<Employee> empList = employeeMapper.getAttnReportNeedList(lastMonthStart,empTypeIdList);
		//根据员工生成月度总结报表
		for(Employee data:empList){
			try{
				attnReportService.generateDayWCandJTallowReportByEmp(data,lastMonthStart,lastMonthEnd,
						workTypeMap,whetherSchedulingMap,vacationMap,user.getEmployee().getCnName());
			}catch(Exception e){
				logger.error("generateMonthWCandJTallowReport:"+data.getCnName()+"出错；"+e.getMessage());
			}
		}
		
		result.put("msg", "餐费与交通补贴报表生成成功！");
		
		return result;
	}
	

}
