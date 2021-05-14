package com.ule.oa.admin.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import com.google.common.collect.Maps;
import com.ule.oa.admin.util.IsMenu;
import com.ule.oa.admin.util.IsOperation;
import com.ule.oa.admin.util.ResponseUtil;
import com.ule.oa.base.mapper.EmpLeaveMapper;
import com.ule.oa.base.po.CompanyConfig;
import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.EmpLeave;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.RequestQueryLeaveRecord;
import com.ule.oa.base.po.ResponseQueryLeaveRecord;
import com.ule.oa.base.service.CompanyConfigService;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.EmpLeaveService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.common.cache.distlock.DistLockUtil;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.PageModel;

/**
  * @ClassName: EmpLeaveController
  * @Description: 员工假期控制层
  * @author minsheng
  * @date 2018年1月8日 下午4:55:27
 */
@Controller
@RequestMapping("empLeave")
public class EmpLeaveController {
	@Autowired
	private EmpLeaveService empLeaveService;
	@Autowired
    private EmpLeaveMapper empLeaveMapper;
	@Autowired
	private EmployeeService employeeService;
	@Resource
	private CompanyConfigService companyConfigService;
	@Resource
	private ConfigService configService;
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	  * index(假期余额报表首页)
	  * @Title: index
	  * @Description: 假期余额报表首页
	  * @param request
	  * @return    设定文件
	  * String    返回类型
	  * @throws
	 */
	@RequestMapping("/index.htm")
	public ModelAndView index(HttpServletRequest request){
		ModelMap map = new ModelMap();
		Date today = DateUtils.getToday();
		map.put("endTime", DateUtils.format(today, DateUtils.FORMAT_SHORT));
		today = DateUtils.addDay(today, -7);
		map.put("startTime", DateUtils.format(today, DateUtils.FORMAT_SHORT));
		return new ModelAndView("base/leaveManager/leaveReamin_report",map);
	}
	
	//剩余假期
	@IsMenu
	@RequestMapping("/leaveRemain.htm")
	public ModelAndView leaveRemain(HttpServletRequest request){
		ModelMap map = new ModelMap();
		map.put("nowYear", Integer.parseInt(DateUtils.getYear(new Date())));
		
		return new ModelAndView("base/leaveManager/leave_remain",map);
	}
	
	/**
	  * getReportPageList(考勤管理-假期管理-剩余假期-查询)
	  * @Title: getReportPageList
	  * @Description: 考勤管理-假期管理-剩余假期-查询
	  * @param empLeave
	  * @return    设定文件
	  * PageModel<EmpLeave>    返回类型
	  * @throws
	 */
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/getReportPageList.htm")
	public PageModel<EmpLeave> getReportPageList(Employee employee){
		
		if(employee.getYear()==null || employee.getYear()==0){
			employee.setYear( Integer.parseInt(DateUtils.getYear(new Date())));
		}
		PageModel<EmpLeave> pm=new PageModel<EmpLeave>();
		pm.setRows(new java.util.ArrayList<EmpLeave>());
		try {
			pm = empLeaveService.getReportPageList(employee);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	/**
	  * exportReport(导出假期余额报表)
	  * @Title: exportReport
	  * @Description: 导出假期余额报表
	  * @param empLeave
	  * @param response
	  * @throws IOException    设定文件
	  * void    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/exportReport.htm")
	public void exportReport(Employee employee,HttpServletResponse response,HttpServletRequest request) throws IOException{//导出数据并发送邮件
	
		OutputStream os = null;
		try {
			String agent=request.getHeader("User-Agent").toLowerCase();
			employee.setCode(URLDecoder.decode(employee.getCode(),"UTF-8"));
			employee.setCnName(URLDecoder.decode(employee.getCnName(),"UTF-8"));
			
			ResponseUtil.setDownLoadExeclInfo(response,request,"假期余额_");
			HSSFWorkbook hSSFWorkbook = empLeaveService.exportReport(employee);
			os = response.getOutputStream();
			if(hSSFWorkbook!=null){
				hSSFWorkbook.write(os);
			};
		} catch (Exception e) {
			logger.error("导出假期余额报表失败,原因=",e);
		}finally{
			if(null != os){
				os.flush();
				os.close();
			}
		}
	}
	
	/**
	  * exportLeaveRemainReport(考勤管理-假期管理-剩余假期-导出)
	  * @Title: exportReport
	  * @Description: 考勤管理-假期管理-剩余假期-导出
	  * @param empLeave
	  * @param response
	  * @throws IOException    设定文件
	  * void    返回类型
	  * @throws
	 */
	@IsOperation(returnType=true)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/exportLeaveRemainReport.htm")
	public void exportLeaveRemainReport(Employee employee,HttpServletResponse response,HttpServletRequest request) throws IOException{//导出数据并发送邮件
		
		OutputStream os = null;
		try {
			String agent=request.getHeader("User-Agent").toLowerCase();
			employee.setCode(URLDecoder.decode(employee.getCode(),"UTF-8"));
			employee.setCnName(URLDecoder.decode(employee.getCnName(),"UTF-8"));
			
			ResponseUtil.setDownLoadExeclInfo(response,request,"剩余假期_");
			HSSFWorkbook hSSFWorkbook = empLeaveService.exportLeaveRemainReport(employee);
			os = response.getOutputStream();
			if(hSSFWorkbook!=null){
				hSSFWorkbook.write(os);
			}
		} catch (Exception e) {
			logger.error("导出假期余额报表失败,原因=",e);
		}finally{
			if(null != os){
				os.flush();
				os.close();
			}
		}
	}
	
	@ResponseBody
	@RequestMapping("/exportLeaveRemainReportNew.htm")
	public void exportLeaveRemainReportNew(Employee employee,HttpServletResponse response,HttpServletRequest request) throws IOException{//导出数据并发送邮件
		
		OutputStream os = null;
		try {
			employee.setCode(URLDecoder.decode(employee.getCode(),"UTF-8"));
			employee.setCnName(URLDecoder.decode(employee.getCnName(),"UTF-8"));
			
			ResponseUtil.setDownLoadExeclInfo(response,request,"剩余假期_");
			HSSFWorkbook hSSFWorkbook = empLeaveService.exportLeaveRemainReportNew(employee);
			os = response.getOutputStream();
			hSSFWorkbook.write(os);
		} catch (Exception e) {
			logger.error("导出假期余额报表失败,原因=",e);
		}finally{
			if(null != os){
				os.flush();
				os.close();
			}
		}
	}
	
	
	/**
	 * 考勤管理-假期管理-剩余假期-上传
	 * @param file
	 * @return
	 */
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping(value="/importEmpLeaveList",method = RequestMethod.POST)
	public Map<String, String> importEmpLeaveList(@RequestParam(value="file",required = false)MultipartFile file) {

		String result = null;
		try {
			result = empLeaveService.importEmpLeaveList(file);
		} catch (OaException e) {
			result = e.getMessage();
		} catch (Exception e) {
			result = "网络不给力，请稍后重试";
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("response", result);
        return map; 
	}
	
	@ResponseBody
	@RequestMapping("/updateEmpLeave.htm")
	public Map<String,Object> updateEmpLeave(EmpLeave empLeave){
		
		Map<String,Object> result = Maps.newHashMap();
		try {
			result = this.empLeaveService.updateUsedLeaveByEmpId(empLeave);
		} catch (Exception e) {
			result.put("sucess",false);
			result.put("msg",e.getMessage());
		}
		return result;
	}
	
	//考勤管理-假期管理-剩余假期-修改
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/updateEmpLeaveNew.htm")
	public Map<String,Object> updateEmpLeaveNew(EmpLeave empLeave){
		
		Map<String,Object> result = Maps.newHashMap();
		try {
			result = this.empLeaveService.updateLeaveByEmpId(empLeave);
		} catch (Exception e) {
			result.put("sucess",false);
			result.put("msg",e.getMessage());
		}
		return result;
	}
	
	//年休假基数
	@IsMenu
	@RequestMapping("/leaveRadix.htm")
	public ModelAndView leaveRadix(HttpServletRequest request){
		String year = DateUtils.getYear(new Date());
		request.setAttribute("year", year);
		return new ModelAndView("base/leaveManager/leave_radix");
	}
	
	/**
	  * getLeaveRadixList(考勤管理-假期管理-年休假基数-查询)
	  * @Title: getLeaveRadixList
	  * @Description: 考勤管理-假期管理-年休假基数-查询
	  * @param employee
	  * @return    设定文件
	  * PageModel<Map<String,Object>>    返回类型
	  * @throws
	 */
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/getLeaveRadixList.htm")
	public PageModel<Map<String,Object>> getLeaveRadixList(Employee employee){
		
		PageModel<Map<String,Object>> pm=new PageModel<Map<String,Object>>();
		pm.setRows(new java.util.ArrayList<Map<String,Object>>());
		
		try {
			pm = empLeaveService.getLeaveRadixList(employee);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	

	//假期计算器
	@ResponseBody
	@RequestMapping("/queryEmpLeaveInfoByDate.htm")
	public Map<String,Object> queryEmpLeaveInfoByDate(Employee emp){
		
		Map<String,Object> result = Maps.newHashMap();
		try {
			result = this.empLeaveService.queryEmpLeaveInfoByDate(emp.getQuitTime(),emp.getId());
			result.put("sucess",true);
		} catch (Exception e) {
			result.put("sucess",false);
			result.put("msg",e.getMessage());
		}
		return result;
	}
	
	@ResponseBody
	@RequestMapping("/updateLeaveRadix.htm")
	public Map<String,Object> updateLeaveRuduix(String remarkRecord,Double legalRaduix,Double welfareRaduix,Integer updateType,Long employeeId){
		
		Map<String,Object> result = Maps.newHashMap();
		try {
			result = this.empLeaveService.updateLeaveRuduix(legalRaduix,welfareRaduix,updateType,employeeId,remarkRecord);
		} catch (Exception e) {
			result.put("sucess",false);
			result.put("msg",e.getMessage());
		}
		return result;
	}
	
	//假期流水
	@IsMenu
	@RequestMapping("/leaveRecord.htm")
	public ModelAndView leaveRecord(HttpServletRequest request){
		ModelMap map = new ModelMap();
		map.put("nowYear", Integer.parseInt(DateUtils.getYear(new Date())));
		
		return new ModelAndView("base/leaveManager/leave_record",map);
	}
	
	/**
	 * 考勤管理-假期管理-假期流水-查询
	 * @param requestQueryLeaveRecord
	 * @return
	 */
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/getLeaveRecordList.htm")
	public PageModel<ResponseQueryLeaveRecord> getLeaveRecordList(RequestQueryLeaveRecord requestQueryLeaveRecord){
		
		PageModel<ResponseQueryLeaveRecord> pm=new PageModel<ResponseQueryLeaveRecord>();
		pm.setRows(new java.util.ArrayList<ResponseQueryLeaveRecord>());
		
		try {
			pm = empLeaveService.getLeaveRecordList(requestQueryLeaveRecord);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
		
	}
	
	@ResponseBody
	@RequestMapping(value="/reduceYearLeave.htm",method = RequestMethod.GET)
	public Map<String, String> reduceYearLeave(String codes) {
		
		//扣减日期
		List<String> dateList = new ArrayList<String>();
		dateList.add("2021-02-10");
		
		//查询排班类型和工时类型配置表
		CompanyConfig companyConfigConditon = new CompanyConfig();//查询配置表条件
		companyConfigConditon.setCode("typeOfWork");//工时类型
		List<CompanyConfig> workTypeList = companyConfigService.getListByCondition(companyConfigConditon);
		Map<Long,String> workTypeMap =  workTypeList.stream().collect(Collectors.toMap(CompanyConfig :: getId, CompanyConfig :: getDisplayCode));
		
		Config configCondition = new Config();
		configCondition.setCode("whetherScheduling");//是否排班
		List<Config> whetherSchedulingList = configService.getListByCondition(configCondition);
		Map<Long,String> whetherSchedulingMap = whetherSchedulingList.stream().collect(Collectors.toMap(Config :: getId, Config :: getDisplayCode));
		
		Long whetherScheduling = 488L;//非排班
		for(Config data:whetherSchedulingList) {
			if("no".equals(data.getDisplayCode())) {
				whetherScheduling = data.getId();
				break;
			}
		}
		
		//扣减非排班员工的年假
		Employee reduceParam = new Employee();
		reduceParam.setWhetherScheduling(whetherScheduling);
		List<Long> jobStatusList = new ArrayList<Long>();
		jobStatusList.add(0L);//在职
		jobStatusList.add(2L);//待离职
		reduceParam.setJobStatusList(jobStatusList);
		
		List<Employee> employeeList  = employeeService.getListByCondition(reduceParam);
		for(Employee data:employeeList){
			try {
				logger.info("扣减员工:"+data.getCode()+"_"+data.getCnName()+"的年假开始");
				//扣减假期
				empLeaveService.reduceYearLeave(data.getId(), dateList, 
									data.getCnName(), workTypeMap.get(data.getWorkType()), whetherSchedulingMap.get(data.getWhetherScheduling()));
				logger.info("扣减员工:"+data.getCode()+"_"+data.getCnName()+"的年假结束");
			} catch (OaException e) {
				logger.error("扣减员工:"+data.getCode()+"_"+data.getCnName()+"的年假失败，原因="+e.getMessage());
			} catch (Exception e) {
				logger.error("扣减员工:"+data.getCode()+"_"+data.getCnName()+"的年假失败，原因="+e.getMessage());
			}
		}
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("response", "扣减结束");
        return map; 
	}
	
	@RequestMapping("/reduceLeaveHtm.htm")
	public ModelAndView reduceLeaveHtm(HttpServletRequest request){
		return new ModelAndView("base/attnManager/reduce_leave");
	}
	
	/**
	 * 扣减假期
	 * @param file
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/reduceAffairAndYearLeave.htm",method = RequestMethod.POST)
	public Map<String, String> reduceAffairAndYearLeave(@RequestParam(value="file",required = false)MultipartFile file) {
		
		String lockValue = DistLockUtil.lock("reduceAffairAndYearLeave", 6 * 5L);
		Map<String, String> map = new HashMap<String, String>();
		if (StringUtils.isBlank(lockValue)) {
			logger.info("扣除假期已经调用，请不要重复调用当前定时!!!");
			map.put("response", "请勿重复调用！");
			return map;
		} else {// 锁定定时
			try {
				empLeaveService.reduceAffairAndYearLeave(file);
			} catch (Exception e) {
				logger.error("扣除假期失败，失败原因="+e.getMessage());
			}
			map.put("response", "扣除假期触发成功,请稍后查看数据！");
			return map;
		}
	}
	
	@ResponseBody
    @RequestMapping("/updateById.htm")
    public Map<String,Object> updateById(Long id,Double blockDays,Double remainDays,Double useDays){
        
        Map<String,Object> result = Maps.newHashMap();
        try {
            EmpLeave leave = empLeaveMapper.getById(id);
            if(leave!=null) {
                leave.setBlockedDays(blockDays);
                leave.setAllowRemainDays(remainDays);
                leave.setUsedDays(useDays);
                empLeaveService.updateById(leave);
            }
        } catch (Exception e) {
            result.put("sucess",false);
            result.put("msg",e.getMessage());
        }
        return result;
    }
		
}
