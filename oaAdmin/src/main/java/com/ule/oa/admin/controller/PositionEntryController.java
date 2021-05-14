package com.ule.oa.admin.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
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
import com.ule.oa.base.po.EmpEntryRegistration;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.PositionEntryService;
import com.ule.oa.common.utils.PageModel;

/**
 * @ClassName: 入职查询
 * @Description: 入职查询
 * @author zhoujinliang
 * @date 2018年3月21日15:40:52
 */

@Controller
@RequestMapping("entry") 
public class PositionEntryController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private PositionEntryService entryService;
	
	@IsMenu
	@RequestMapping("/index.htm")
	public ModelAndView index(){
		return new ModelAndView("base/position/entryQuery");
	}
	
	//入离职管理-入职查询-查询
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/getReportPageList.htm")
	public PageModel<EmpEntryRegistration> getReportPageList(EmpEntryRegistration entry){	

		PageModel<EmpEntryRegistration> pm=new PageModel<EmpEntryRegistration>();
		pm.setRows(new java.util.ArrayList<EmpEntryRegistration>());		
		try {
			
			pm = entryService.getReportPageList(entry);	
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}		
		return pm;
	}
	
	/**
	 * 入离职管理-入职查询-导出
	 * @param entry
	 * @param response
	 * @throws UnsupportedEncodingException
	 */
	@IsOperation(returnType=true)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/exportReport.htm")
	public void exportReport(EmpEntryRegistration entry, HttpServletResponse response,HttpServletRequest request) throws IOException{
	
		OutputStream os = null;
		try {
			ResponseUtil.setDownLoadExeclInfo(response,request,"入职查询_");
			HSSFWorkbook hSSFWorkbook = entryService.exportExcel(entry);		
			os = response.getOutputStream();
			hSSFWorkbook.write(os);
		} catch (Exception e) {
			
		}finally{
			if(os!=null){
				os.flush();
				os.close();
			}
		}
	}
	
	//入离职管理-入职查询-取消入职
	@ResponseBody
	@RequestMapping("/cancelEntry.htm")
	public Map<String,Object> cancelEntry(Long entryApplyId){
	
		Map<String,Object> result = Maps.newHashMap();
		try {
			result = entryService.cancelEntry(entryApplyId);
		} catch (Exception e) {
			result.put("sucess",false);
			result.put("msg",e.getMessage());
		}
		return result;
	}
}
