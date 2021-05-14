package com.ule.oa.admin.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.ule.oa.admin.util.IsMenu;
import com.ule.oa.admin.util.IsOperation;
import com.ule.oa.admin.util.ResponseUtil;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.service.EmpLeaveService;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.PageModel;


/**
  * @ClassName: EmpLeaveController
  * @Description: 员工假期控制层
  * @author minsheng
  * @date 2018年1月8日 下午4:55:27
 */
@Controller
@RequestMapping("monthLeaveDetail")
public class MonthLeaveDetailController {
	@Autowired
	private EmpLeaveService empLeaveService;
	
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
	@IsMenu
	@RequestMapping("/index.htm")
	public ModelAndView index(HttpServletRequest request){
		ModelMap map = new ModelMap();
		Date today = DateUtils.getToday();
		today = DateUtils.addMonth(today, -1);
		map.put("startTime", DateUtils.format(today, "yyyy-MM"));
		return new ModelAndView("base/attnManager/monthLeaveDetail",map);
	}
	
	/**
	  * getReportPageList(报表统计-考勤报表-月度月度假期明细表-查询)
	  * @Title: getReportPageList
	  * @Description: 报表统计-考勤报表-月度月度假期明细表-查询
	  * @param cp
	  * @return    设定文件
	  * PageModel<Map<String,Object>>    返回类型
	  * @throws
	 */
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/getReportPageList.htm")
	public PageModel<Map<String,Object>> getReportPageList(Employee cp){
		
		PageModel<Map<String,Object>> pm = empLeaveService.MonthLeaveDetailPageImpl(cp);
		
		return pm;
	}
	
	/**
	 * 报表统计-考勤报表-月度月度假期明细表-导出
	 * @param cp
	 * @param response
	 * @throws UnsupportedEncodingException
	 */
	@IsOperation(returnType=true)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/exportReport.htm")
	public void exportReport(Employee cp,HttpServletResponse response,HttpServletRequest request) throws IOException{
		
		OutputStream os = null;
		
		try {
			cp.setCode(URLDecoder.decode(cp.getCode(),"UTF-8"));
			cp.setCnName(URLDecoder.decode(cp.getCnName(),"UTF-8"));
			cp.setExportStauts(1);
			
			ResponseUtil.setDownLoadExeclInfo(response,request,"月度假期明细报表_");
			os = response.getOutputStream();
			XSSFWorkbook xSSFWorkbook=empLeaveService.exportReportMonthLeaveDetail(cp);
			if(xSSFWorkbook!=null){
				xSSFWorkbook.write(os);
			}
		} catch (Exception e) {
		}finally{
			if(os!=null){
				os.flush();
				os.close();
			}
		}
	}
}
