package com.ule.oa.service.web.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
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
import com.ule.oa.base.po.EmpLeave;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.service.AnnualVacationService;
import com.ule.oa.base.service.AttnReportService;
import com.ule.oa.base.service.CompanyConfigService;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.EmpLeaveService;
import com.ule.oa.common.cache.distlock.DistLockUtil;
import com.ule.oa.common.utils.DateUtils;

@Controller
@RequestMapping("taskAbate")
public class TaskAbateController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private EmpLeaveService empLeaveService;
	@Autowired
	private AnnualVacationService annualVacationService;
	@Autowired
	private AttnReportService attnReportService;
	@Autowired
	private ConfigService configService;
	@Autowired
	private CompanyConfigService companyConfigService;
	@Autowired
	private EmployeeMapper employeeMapper;
	/**
	 * @throws Exception 
	  * @Title: 任务失效定时
	  * @Description: 凌晨1.30分跑失效定时
	  * void    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/taskAbate.htm")
	public Map<String, String> taskFailure(HttpServletRequest request) throws Exception{
		String lockValue = DistLockUtil.lock("taskAbate",60*9L);
		Map<String, String> map = new HashMap<String, String>();
		if(StringUtils.isBlank(lockValue)){
			logger.info("失效发送定时已经启动，请不要重复调用当前定时!!! ");
			map.put("response", "请勿重复调用当前定时!!!");
			return map;
		}else{
			
			Date today = new Date();
			//当天是本月的第6个工作日，生成数据
			Date next6WorkDay = annualVacationService.get5WorkingDayNextmonth(6);
			if(DateUtils.format(today, DateUtils.FORMAT_SHORT).equals(DateUtils.format(next6WorkDay, DateUtils.FORMAT_SHORT))){
				
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
				Date lastMonthStart = DateUtils.addMonth(DateUtils.getFirstDay(today), -1);
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
								workTypeMap,whetherSchedulingMap,vacationMap,"system");
					}catch(Exception e){
						logger.error("generateMonthWCandJTallowReport:"+data.getCnName()+"出错；"+e.getMessage());
					}
				}
			}
			
			//其它调休失效
			try{
				Date invalidDate = DateUtils.parse(DateUtils.format(new Date(), DateUtils.FORMAT_SHORT)+" 23:59:59", DateUtils.FORMAT_LONG);
				List<EmpLeave> invalidList = empLeaveService.getInvalidOtherRestLeaveList(invalidDate);
				for(EmpLeave data:invalidList){
					Date endTime = annualVacationService.getWorkingDayNext(6, data.getEndTime());
					if(endTime.getTime()<=invalidDate.getTime()){
						empLeaveService.reduceInvalidOtherRestLeave(data);
					}
				}
			}catch(Exception e){
				logger.error("其它调休失效定时，失败原因="+e.getMessage());
			}
			//申请单据失效
			try {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
				logger.info("失效定时开始操作，时间为"+df.format(System.currentTimeMillis()));
				empLeaveService.Failure();
				map.put("response", "taskAbate触发成功,请稍后查看数据！");
				return map;
			} catch (Exception e) {
				logger.error("失效定时，失败原因="+e.getMessage());
				map.put("response", "发送失败");
				return map;
			}
			
		}
	}
}
