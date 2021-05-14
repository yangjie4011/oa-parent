package com.ule.oa.admin.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
import com.ule.oa.admin.util.IsMenu;
import com.ule.oa.admin.util.IsOperation;
import com.ule.oa.admin.util.ResponseUtil;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.EmpApplicationOvertime;
import com.ule.oa.base.po.EmpLeave;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmpApplicationOvertimeService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.PageModel;


/**
 * @ClassName: 加班
 * @Description: 加班
 * @author yangjie
 * @date 2017年12月11日
 */

@Controller
@RequestMapping("empApplicationOvertime")
public class EmpApplicationOvertimeController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private EmpApplicationOvertimeService empApplicationOvertimeService;
	@Autowired
	private DepartService departService;
	
	@IsMenu
	@RequestMapping("/index.htm")
	public ModelAndView index(){
		return new ModelAndView("base/attnManager/overtime");
	}
	
	@IsMenu
	@RequestMapping("/overtimeManage.htm")
	public ModelAndView overtimeManage(HttpServletRequest request){
		String nowYear = DateUtils.getYear(new Date());
		request.setAttribute("nowYear", nowYear);
		return new ModelAndView("base/attnManager/overtimeManage");
	}
	
	//考勤管理-延时工作管理-调休小时数管理-查询
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/getOvertimeManagePageList.htm")
	public PageModel<EmpLeave> getOvertimeManagePageList(EmpLeave empovertimeLeave){
		
		PageModel<EmpLeave> pm=new PageModel<EmpLeave>();
		pm.setRows(new java.util.ArrayList<EmpLeave>());
		try {
			empovertimeLeave.setType(13);
			pm = empApplicationOvertimeService.getOvertimeManagePageList(empovertimeLeave);
		} catch (Exception e) {
			logger.error("getOvertimeManagePageList"+e.getMessage(),e);
		}
		
		return pm;
	}
	
	//考勤管理-延时工作管理-调休小时数管理-查询详情
	//@IsOperation(returnType=false)//需要校验操作权限*/
	@RequestMapping("/queryInfoByEmp.htm")
	@ResponseBody
	public Map<String,Object> queryInfoByEmp(long employeeId, Integer year,Integer page, Integer rows) throws OaException{
		
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			result = empApplicationOvertimeService.queryInfoByEmp(employeeId,year,page,rows);		
			result.put("success", true);
		}catch(Exception e){
			result.put("success", false);
			result.put("msg", e.toString());
			logger.error("queryInfoByEmp:",e);
		}
		return result;
	}	
	//考勤管理-延时工作管理-调休小时数管理-修改查询
	//@IsOperation(returnType=false)//需要校验操作权限*/
	@RequestMapping("/queryInfoById.htm")
	@ResponseBody
	public Map<String,Object> queryInfoById(long id) throws OaException{
		
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			result = empApplicationOvertimeService.queryInfoById(id);		
			result.put("success", true);
		}catch(Exception e){
			result.put("success", false);
			result.put("msg", e.toString());
			logger.error("queryInfoById:",e);
		}
		return result;
	}	
	
	
	
	
	//考勤管理-延时工作管理-调休小时数管理-新增
	@IsOperation(returnType=false)
	@RequestMapping("/saveOverTimeManage.htm")
	@ResponseBody
	public Map<String,Object> saveOverTimeManage(EmpLeave empovertimeLeave) throws OaException{
	
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			result = empApplicationOvertimeService.saveOverTimeManage(empovertimeLeave);
		}catch(Exception e){
			result.put("success", false);
			result.put("message", e.toString());
			logger.error("saveOverTimeManage:"+e.toString());
		}
		return result;
	}	
	
	//考勤管理-延时工作管理-调休小时数管理-修改
	@IsOperation(returnType=false)
	@RequestMapping("/updateOverTimeManage.htm")
	@ResponseBody
	public Map<String,Object> updateOverTimeManage(EmpLeave empovertimeLeave) throws OaException{
		
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			result = empApplicationOvertimeService.updateOverTimeManage(empovertimeLeave);
		}catch(Exception e){
			result.put("success", false);
			result.put("msg", e.toString());
			logger.error("updateOverTimeManage:"+e.toString());
		}
		return result;
	}	
	
	
	//考勤管理-考勤查询-加班查询-查询
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/getReportPageList.htm")
	public PageModel<EmpApplicationOvertime> getReportPageList(EmpApplicationOvertime empApplicationOvertime){
		
		List<Integer> departList = new ArrayList<Integer>();
		if(empApplicationOvertime.getSecondDepart()!=null){
			departList.add(empApplicationOvertime.getSecondDepart());
			empApplicationOvertime.setDepartList(departList);
		}else if(empApplicationOvertime.getSecondDepart()==null&&empApplicationOvertime.getFirstDepart()!=null){
			departList.add(empApplicationOvertime.getFirstDepart());
			List<Depart> list = departService.getByParentId(Long.valueOf(String.valueOf(empApplicationOvertime.getFirstDepart())));
		    for(Depart depart:list){
		    	departList.add(Integer.valueOf(String.valueOf(depart.getId())));
		    }
		    empApplicationOvertime.setDepartList(departList);
		}
		PageModel<EmpApplicationOvertime> pm=new PageModel<EmpApplicationOvertime>();
		pm.setRows(new java.util.ArrayList<EmpApplicationOvertime>());
		
		try {
			pm = empApplicationOvertimeService.getReportPageList(empApplicationOvertime);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	/**
	 * 考勤管理-考勤查询-加班查询-导出
	 * @param model
	 * @param response
	 * @throws UnsupportedEncodingException
	 */
	@IsOperation(returnType=true)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/exportReport.htm")
	public void exportReport(EmpApplicationOvertime empApplicationOvertime, HttpServletResponse response,HttpServletRequest request) throws IOException{
		
		OutputStream os = null;
		try{
			empApplicationOvertime.setCode(URLDecoder.decode(empApplicationOvertime.getCode(),"UTF-8"));
			empApplicationOvertime.setCnName(URLDecoder.decode(empApplicationOvertime.getCnName(),"UTF-8"));
			List<Integer> departList = new ArrayList<Integer>();
			if(empApplicationOvertime.getSecondDepart()!=null){
				departList.add(empApplicationOvertime.getSecondDepart());
				empApplicationOvertime.setDepartList(departList);
			}else if(empApplicationOvertime.getSecondDepart()==null&&empApplicationOvertime.getFirstDepart()!=null){
				departList.add(empApplicationOvertime.getFirstDepart());
				List<Depart> list = departService.getByParentId(Long.valueOf(String.valueOf(empApplicationOvertime.getFirstDepart())));
			    for(Depart depart:list){
			    	departList.add(Integer.valueOf(String.valueOf(depart.getId())));
			    }
			    empApplicationOvertime.setDepartList(departList);
			}
			HSSFWorkbook workbook = empApplicationOvertimeService.exportExcel(empApplicationOvertime);
			ResponseUtil.setDownLoadExeclInfo(response,request,"加班查询_");	
			os = response.getOutputStream();
			if(workbook!=null){
				workbook.write(os);
				os.flush();
				os.close();
			}
		}catch(Exception e){
			logger.error("导出加班查询报表失败,原因=",e);
		}finally{
			if(null != os){
				os.flush();
				os.close();
			}
		}
    }
	
	@ResponseBody
	@RequestMapping("/getTime.htm")
	public Map<String,String> getTime(String type){
	
		Map<String,String> pm=new HashMap<String,String>();
		//1-上周，2-本月，3-上月
		try {
			if("1".equals(type)){
				Calendar preWeekSundayCal = Calendar.getInstance();
				Calendar preWeekMondayCal = Calendar.getInstance();
				//设置时间成本周第一天(周日)
				preWeekSundayCal.set(Calendar.DAY_OF_WEEK,1);
				preWeekMondayCal.set(Calendar.DAY_OF_WEEK,1);
				//上周日时间
				preWeekSundayCal.add(Calendar.DATE, -6);
				//上周一时间
				preWeekMondayCal.add(Calendar.DATE, 0);
				pm.put("applyStartDate",DateUtils.format(preWeekSundayCal.getTime(), DateUtils.FORMAT_SHORT));
				pm.put("applyEndDate",DateUtils.format(preWeekMondayCal.getTime(), DateUtils.FORMAT_SHORT));
			}else if("2".equals(type)){
				Date now = new Date();
				pm.put("applyStartDate",DateUtils.format(DateUtils.getFirstDay(now), DateUtils.FORMAT_SHORT));
				pm.put("applyEndDate",DateUtils.format(DateUtils.getLastDay(now), DateUtils.FORMAT_SHORT));
			}else if("3".equals(type)){
				Calendar c = Calendar.getInstance();
				c.add(Calendar.MONTH, -1);
				pm.put("applyStartDate",DateUtils.format(DateUtils.getFirstDay(c.getTime()), DateUtils.FORMAT_SHORT));
				pm.put("applyEndDate",DateUtils.format(DateUtils.getLastDay(c.getTime()), DateUtils.FORMAT_SHORT));
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	@IsMenu
	@RequestMapping("/sumReport.htm")
	public ModelAndView sumReport(){
		return new ModelAndView("base/report/overtime");
	}
	
	//报表统计-考勤报表-月度加班汇总-查询
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/getApplyOverTimeSumReportByPage.htm")
	public PageModel<Map<String,Object>> getApplyOverTimeSumReportByPage(EmpApplicationOvertime empApplicationOvertime){
	
		PageModel<Map<String,Object>> pm=new PageModel<Map<String,Object>>();
		pm.setRows(new java.util.ArrayList<Map<String,Object>>());
		
		try {
			pm = empApplicationOvertimeService.getApplyOverTimeSumReportByPage(empApplicationOvertime);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	/**
	 * 报表统计-考勤报表-月度加班汇总-导出
	 * @param model
	 * @param response
	 * @throws UnsupportedEncodingException
	 */
	@IsOperation(returnType=true)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/exportSumReport.htm")
	public void exportSumReport(EmpApplicationOvertime empApplicationOvertime, HttpServletResponse response) throws IOException{
	
		OutputStream os = null;
		try{
			HSSFWorkbook workbook = empApplicationOvertimeService.exportSumReport(empApplicationOvertime);
			//设置返回类型
			String fileName = URLEncoder.encode("月度加班汇总_" + DateUtils.format(empApplicationOvertime.getApplyStartDate(),DateUtils.FORMAT_SIMPLE) + ".xls","UTF-8");
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-disposition", "attachment; filename="+fileName);
			os = response.getOutputStream();
			if(workbook!=null){
				workbook.write(os);
				os.flush();
				os.close();
			}
		}catch(Exception e){
			logger.error("月度加班汇总报表失败,原因="+e.toString());
		}finally{
			if(null != os){
				os.flush();
				os.close();
			}
		}
    }

}
