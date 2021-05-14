package com.ule.oa.admin.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.google.common.collect.Maps;
import com.ule.oa.admin.util.IsMenu;
import com.ule.oa.admin.util.IsOperation;
import com.ule.oa.admin.util.ResponseUtil;
import com.ule.oa.base.po.EmpAttn;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.service.EmpAttnService;
import com.ule.oa.base.service.EmployeeSignService;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.PageModel;


//员工签到
@Controller
@RequestMapping("employeeSign")
public class EmployeeSignController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private EmployeeSignService employeeSignService;
	@Resource
	private EmpAttnService empAttnService;
	
	//员工签到
	@IsMenu
	@RequestMapping("/index.htm")
	public ModelAndView index(HttpServletRequest request){
		return new ModelAndView("base/employeeSign/index");
	}
	
	//员工签到查询页面
	@IsMenu
	@RequestMapping("/report.htm")
	public ModelAndView report(HttpServletRequest request){
		Date today = DateUtils.getToday();
		request.setAttribute("endTime", DateUtils.format(today, DateUtils.FORMAT_SHORT));
		today = DateUtils.addDay(today, -3);
		request.setAttribute("startTime", DateUtils.format(today, DateUtils.FORMAT_SHORT));
		return new ModelAndView("base/employeeSign/report");
	}
	
	//定位签到查询页面
	@IsMenu
	@RequestMapping("/locationCheckIn.htm")
	public ModelAndView locationCheckIn(HttpServletRequest request){
		Date today = DateUtils.getToday();
		request.setAttribute("endTime", DateUtils.format(today, DateUtils.FORMAT_SHORT));
		today = DateUtils.addDay(today, -3);
		request.setAttribute("startTime", DateUtils.format(today, DateUtils.FORMAT_SHORT));
		return new ModelAndView("base/employeeSign/locationCheckIn");
	}
	
	//考勤管理-日常考勤管理-定位工签到-查询
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/getLocationCheckInData.htm")
	public PageModel<Map<String,Object>> getLocationCheckInData(EmpAttn empAttn){
	
		PageModel<Map<String,Object>> pm=new PageModel<Map<String,Object>>();
		pm.setRows(new java.util.ArrayList<Map<String,Object>>());
		
		try {
			pm = empAttnService.getLocationCheckInData(empAttn);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	//考勤管理-日常考勤管理-定位签到-导出
	@IsOperation(returnType=true)//需要校验操作权限
	@RequestMapping("/exportLocationCheckInData.htm")
	public void exportLocationCheckInData(EmpAttn empAttn,HttpServletResponse response,HttpServletRequest request) throws IOException{
	
		OutputStream os = null;
		try {
			ResponseUtil.setDownLoadExeclInfo(response,request,"定位签到_");
			HSSFWorkbook hSSFWorkbook = empAttnService.exportLocationCheckInData(empAttn);
			os = response.getOutputStream();
			if(hSSFWorkbook!=null){
				hSSFWorkbook.write(os);
			}
		} catch (Exception e) {
			
		}finally{
			if(os!=null) {
				os.flush();
				os.close();
			}
		}
	}

	//考勤管理-日常考勤管理-员工签到查询-查询
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/getReportPageList.htm")
	public PageModel<Map<String,Object>> getReportPageList(EmpAttn empAttn){
	
		PageModel<Map<String,Object>> pm=new PageModel<Map<String,Object>>();
		pm.setRows(new java.util.ArrayList<Map<String,Object>>());
		
		try {
			pm = empAttnService.getEmployeeSignReportPageList(empAttn);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	//考勤管理-日常考勤管理-员工签到查询-导出
	@IsOperation(returnType=true)//需要校验操作权限
	@RequestMapping("/exportEmployeeSignReport.htm")
	public void exportSignReportReport(EmpAttn empAttn,HttpServletResponse response,HttpServletRequest request) throws IOException{
	
		OutputStream os = null;
		try {
			ResponseUtil.setDownLoadExeclInfo(response,request,"员工本人签到_");
			HSSFWorkbook hSSFWorkbook = empAttnService.exportEmployeeSignReport(empAttn);
			os = response.getOutputStream();
			if(hSSFWorkbook!=null){
				hSSFWorkbook.write(os);
			}
		} catch (Exception e) {
			
		}finally{
			if(os!=null) {
				os.flush();
				os.close();
			}
		}
	}
	
	@ResponseBody
	@RequestMapping("/getList.htm")
	public PageModel<Map<String,Object>> getList(Employee employee){
		
		PageModel<Map<String,Object>> pm=new PageModel<Map<String,Object>>();
		pm.setRows(new java.util.ArrayList<Map<String,Object>>());
		
		try {
			pm = employeeSignService.getList(employee);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	@ResponseBody
	@RequestMapping("/getSystemTime.htm")
	public Map<String,Object> getSystemTime(String type){
		
		Map<String,Object> result = new HashMap<String,Object>();
		try {
			String currentTime = DateUtils.format(new Date(), type);
			result.put("sucess",true);
			result.put("msg",currentTime);
		} catch (Exception e) {
			result.put("sucess",false);
			logger.error(e.getMessage(),e);
		}
		return result;
		
	}
	
	@ResponseBody
	@RequestMapping("/sign.htm")
	public Map<String,Object> sign(Long employeeId){
		
		Map<String,Object> result = Maps.newHashMap();
		try {
			result = employeeSignService.sign(employeeId);
		} catch (Exception e) {
			result.put("sucess",false);
			result.put("msg",e.getMessage());
		}
		return result;
	}

}
