package com.ule.oa.admin.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
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
import com.ule.oa.base.mapper.EmployeeMapper;
import com.ule.oa.base.po.CompanyConfig;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.QuitHistory;
import com.ule.oa.base.service.EmpLeaveService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.PositionQuitService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.PageModel;
import com.ule.oa.common.utils.json.JSONUtils;
/**
 * @ClassName: 离职查询
 * @Description: 离职查询
 * @author zhoujinliang
 * @date 2018年3月21日15:40:52
 */
@Controller
@RequestMapping("quit")
public class PositionQuitController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private PositionQuitService quitService;
	@Autowired
	EmployeeMapper employeeMapper;
	@Autowired
	EmpLeaveService empLeaveService;
	@Autowired
	private EmployeeService employeeService;
	
	@IsMenu
	@RequestMapping("/index.htm")
	public ModelAndView index(){
		return new ModelAndView("base/position/quitQuery");
	}
	
	//入离职管理-离职查询-查询
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/getReportPageList.htm")
	public PageModel<QuitHistory> getReportPageList(QuitHistory emp){
		
		PageModel<QuitHistory> pm=new PageModel<QuitHistory>();
		pm.setRows(new java.util.ArrayList<QuitHistory>());		
		try {			
			pm = quitService.getReportPageList(emp);	
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}		
		return pm;
	}
	
	@IsMenu
	@RequestMapping("/notify.htm")
	public ModelAndView quitNotify(Employee emp,HttpServletRequest request){
		 List<CompanyConfig> quitSendMailEmps = quitService.quitSendMailEmps();
		
		request.setAttribute("sendMailEmps", quitSendMailEmps);
		return new ModelAndView("base/position/quitNotify") ;
	}
	
	//入离职管理-员工离职通知-查询
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/getQuitNotifyPageList.htm")
	public PageModel<Employee> getPageList(Employee employee){
		
		PageModel<Employee> pm=new PageModel<Employee>();
		pm.setRows(new java.util.ArrayList<Employee>());
		pm.setTotal(0);
		pm.setPageNo(1);
		
		try {
			pm = employeeService.getQuitPageList(employee);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}		
		return pm;
	}
	
	
	/**
	 * 入离职管理-离职查询-导出
	 * @param quit
	 * @param response
	 * @throws UnsupportedEncodingException
	 */
	@ResponseBody
	@RequestMapping("/exportReport.htm")
	public void exportReport(Employee quit, HttpServletResponse response,HttpServletRequest request) throws IOException{
		
		OutputStream os = null;
		try {
			ResponseUtil.setDownLoadExeclInfo(response,request,"离职查询_");
			HSSFWorkbook hSSFWorkbook = quitService.exportExcel(quit);
			os = response.getOutputStream();
			hSSFWorkbook.write(os);
	        os.flush();
	        os.close();
		} catch (Exception e) {
			
		}finally{
			if(null != os){
				os.flush();
				os.close();
			}
		}
	}
	
	//入离职管理-员工离职通知-导出
	@IsOperation(returnType=true)//需要校验操作权限
	@RequestMapping("/exportQuitEmployeeList.htm")
	public void exportQuitEmployeeList(Employee employee,HttpServletResponse response,HttpServletRequest request) throws IOException{

		OutputStream os = null;
		try {
			employee.setCode(URLDecoder.decode(employee.getCode(),"UTF-8"));
			employee.setCnName(URLDecoder.decode(employee.getCnName(),"UTF-8"));
			ResponseUtil.setDownLoadExeclInfo(response,request,"离职通知列表_");
			HSSFWorkbook hSSFWorkbook = employeeService.exportQuitEmployeeList(employee);			
			os = response.getOutputStream();
			hSSFWorkbook.write(os);
		} catch (Exception e) {
			
		}finally{
			if(os != null){
		        os.flush();
		        os.close();
			}
		}
	}
	
	//入离职管理-员工离职通知-取消离职
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/updateEmpQuitInfo.htm")
	public Map<String,Object> updateEmpQuitInfo(Employee employee) throws Exception{
		logger.info("..enter to function {},param data:{}", Thread.currentThread().getStackTrace()[1].getMethodName(), JSONUtils.write(employee));
		Map<String,Object> result = Maps.newHashMap();
		try { 
			result = quitService.updateEmpQuitInfo(employee);
		}catch(OaException e){
			result.put("flag", false);
			result.put("code", "0001");
		}catch(Exception e){
			result.put("flag", false);
			result.put("code", "0002");
		}
		return result;
	}
	
}