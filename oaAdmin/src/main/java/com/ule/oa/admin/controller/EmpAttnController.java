package com.ule.oa.admin.controller;

import java.io.IOException;
import java.io.OutputStream;
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
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import com.ule.oa.admin.util.IsMenu;
import com.ule.oa.admin.util.IsOperation;
import com.ule.oa.admin.util.ResponseUtil;
import com.ule.oa.base.po.AttnUpdateLog;
import com.ule.oa.base.po.ClassSetting;
import com.ule.oa.base.po.EmpAttn;
import com.ule.oa.base.po.RabcResource;
import com.ule.oa.base.po.TransNormal;
import com.ule.oa.base.service.AttnUpdateLogService;
import com.ule.oa.base.service.ClassSettingService;
import com.ule.oa.base.service.EmpAttnService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.RabcResourceService;
import com.ule.oa.base.service.TransNormalService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.PageModel;

@Controller
@RequestMapping("empAttn")
public class EmpAttnController {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private EmpAttnService empAttnService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private ClassSettingService classSettingService;
	@Autowired
	private AttnUpdateLogService attnUpdateLogService;
	@Autowired
	private RabcResourceService resourceService;
	@Autowired
	private UserService userService;
	@Autowired
	private TransNormalService transNormalService;
	
	@IsMenu
	@RequestMapping("/empAttnDetailReport.htm")
	public ModelAndView index(){
		ModelMap map = new ModelMap();
		Date today = DateUtils.getToday();
		map.put("endTime", DateUtils.format(today, DateUtils.FORMAT_SHORT));
		today = DateUtils.addDay(today, -3);
		map.put("startTime", DateUtils.format(today, DateUtils.FORMAT_SHORT));
		return new ModelAndView("base/attnManager/empAttnDetailReport",map);
	}
	
	@RequestMapping("/attnSettingIndex.htm")
	public ModelAndView attnSettingIndex() throws OaException{
		ModelMap map = new ModelMap();
		Date today = DateUtils.getToday();
		map.put("endTime", DateUtils.format(today, DateUtils.FORMAT_SHORT));
		today = DateUtils.addDay(today, -3);
		map.put("startTime", DateUtils.format(today, DateUtils.FORMAT_SHORT));
		map.put("companyId", employeeService.getCurrentEmployee().getCompanyId());
		return new ModelAndView("base/attnManager/attnSetting",map);
	}
	
	@RequestMapping("/attnClassIndex.htm")
	public ModelAndView attnClassIndex() throws OaException{
		ModelMap map = new ModelMap();
		map.put("companyId", employeeService.getCurrentEmployee().getCompanyId());
		return new ModelAndView("base/attnManager/attnClassShow",map);
	}
	
	//????????????-??????????????????-??????????????????-??????
	@IsOperation(returnType=false)//????????????????????????
	@ResponseBody
	@RequestMapping("/getAttnReportPageList.htm")
	public PageModel<Map<String,Object>> getAttnReportPageList(EmpAttn empAttn){
	
		PageModel<Map<String,Object>> pm=new PageModel<Map<String,Object>>();
		pm.setRows(new java.util.ArrayList<Map<String,Object>>());
		
		try {
			pm = empAttnService.getAttnReportPageList(empAttn);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	//????????????-??????????????????-??????????????????-??????
	@IsOperation(returnType=true)//????????????????????????
	@RequestMapping("/exportAttnReport.htm")
	public void exportAttnReport(EmpAttn empAttn,HttpServletResponse response,HttpServletRequest request) throws IOException{
		
		OutputStream os = null;
		//??????????????????
		try {
			ResponseUtil.setDownLoadExeclInfo(response,request,"????????????_");
			HSSFWorkbook hSSFWorkbook = empAttnService.exportAttnReport(empAttn);
			os = response.getOutputStream();
			if(hSSFWorkbook!=null){
				hSSFWorkbook.write(os);
			}

		} catch (Exception e) {
			
		}finally{
			if(null != os){
				os.flush();
				os.close();
			}
		}
	}
	

	@IsMenu
	@RequestMapping("/empSignRecordReport.htm")
	public ModelAndView empSignRecordReport(){
		ModelMap map = new ModelMap();
		Date today = DateUtils.getToday();
		map.put("endTime", DateUtils.format(today, DateUtils.FORMAT_SHORT));
		today = DateUtils.addDay(today, -3);
		map.put("startTime", DateUtils.format(today, DateUtils.FORMAT_SHORT));
		return new ModelAndView("base/attnManager/empSignRecordReport",map);
	}
	
	//????????????-??????????????????-??????????????????-??????
	@IsOperation(returnType=false)//????????????????????????
	@ResponseBody
	@RequestMapping("/getSignRecordReportPageList.htm")
	public PageModel<Map<String,Object>> getSignRecordReportPageList(EmpAttn empAttn){
		
		PageModel<Map<String,Object>> pm=new PageModel<Map<String,Object>>();
		pm.setRows(new java.util.ArrayList<Map<String,Object>>());
		
		try {
			pm = empAttnService.getSignRecordReportPageList(empAttn);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	//????????????-??????????????????-??????????????????-??????
	@IsOperation(returnType=true)//????????????????????????
	@RequestMapping("/exportSignReportReport.htm")
	public void exportSignReportReport(EmpAttn empAttn,HttpServletResponse response,HttpServletRequest request) throws IOException{
		
		OutputStream os = null;
		try {
			ResponseUtil.setDownLoadExeclInfo(response,request,"????????????_");
			HSSFWorkbook hSSFWorkbook = empAttnService.exportSignReportReport(empAttn);
			os = response.getOutputStream();
			if(hSSFWorkbook!=null){
				hSSFWorkbook.write(os);
			}
		} catch (Exception e) {
			
		}finally{
			if(null != os){
				os.flush();
				os.close();
			}
		}
	}
	

	
	@ResponseBody
	@RequestMapping("/getAllClassShowList.htm")
	public PageModel<ClassSetting> getAllClassShowList(ClassSetting classSetting){
		
		PageModel<ClassSetting> pm=new PageModel<ClassSetting>();
		pm.setRows(new java.util.ArrayList<ClassSetting>());
		
		try {
			pm = empAttnService.getAllClassShowList(classSetting);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	@IsMenu
	@RequestMapping("/monthLackDetailReport.htm")
	public ModelAndView monthLackDetailReport(){
		
		ModelMap map = new ModelMap();
		Date today = DateUtils.getToday();
		today = DateUtils.addMonth(today, -1);
		map.put("startTime", DateUtils.format(today, "yyyy-MM"));
		return new ModelAndView("base/report/monthLackDetail",map);
	}
	
	//????????????-????????????-?????????????????????-??????
	@IsOperation(returnType=false)//????????????????????????
	@ResponseBody
	@RequestMapping("/getMonthLackDetailPageList.htm")
	public PageModel<Map<String,Object>> getMonthLackDetailPageList(EmpAttn empAttn){
	
		PageModel<Map<String,Object>> pm=new PageModel<Map<String,Object>>();
		pm.setRows(new java.util.ArrayList<Map<String,Object>>());
		
		try {
			pm = empAttnService.getMonthLackDetailPageList(empAttn);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	//????????????-????????????-?????????????????????-??????
	@IsOperation(returnType=true)//????????????????????????
	@RequestMapping("/exportMonthLackDetail.htm")
	public void exportMonthLackDetail(EmpAttn empAttn,HttpServletResponse response,HttpServletRequest request) throws IOException{
		
		OutputStream os = null;
		try {
			ResponseUtil.setDownLoadExeclInfo(response,request,"?????????????????????_");
			HSSFWorkbook hSSFWorkbook = empAttnService.exportMonthLackDetailReport(empAttn);
			os = response.getOutputStream();
			if(hSSFWorkbook!=null){
				hSSFWorkbook.write(os);
				os.flush();
				os.close();
			}
		} catch (Exception e) {
			
		}finally{
			if(null != os){
				os.flush();
				os.close();
			}
		}
	}
	
	@IsMenu
	@RequestMapping("/monthLackTotalReport.htm")
	public ModelAndView monthLackTotalReport(){
	
		ModelMap map = new ModelMap();
		Date today = DateUtils.getToday();
		today = DateUtils.addMonth(today, -1);
		map.put("startTime", DateUtils.format(today, "yyyy-MM"));
		return new ModelAndView("base/report/monthLackTotal",map);
	}
	
	//????????????-????????????-?????????????????????-??????
	@IsOperation(returnType=false)//????????????????????????
	@ResponseBody
	@RequestMapping("/getMonthLackTotalPageList.htm")
	public PageModel<Map<String,Object>> getMonthLackTotalPageList(EmpAttn empAttn){
		
		PageModel<Map<String,Object>> pm=new PageModel<Map<String,Object>>();
		pm.setRows(new java.util.ArrayList<Map<String,Object>>());
		
		try {
			pm = empAttnService.getMonthLackTotalPageList(empAttn);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	//????????????-????????????-?????????????????????-??????
	@IsOperation(returnType=true)//????????????????????????
	@RequestMapping("/exportMonthLackTotal.htm")
	public void exportMonthLackTotal(EmpAttn empAttn,HttpServletResponse response,HttpServletRequest request) throws IOException{
	
		OutputStream os = null;
		try {
			ResponseUtil.setDownLoadExeclInfo(response,request,"?????????????????????_");
			HSSFWorkbook hSSFWorkbook = empAttnService.exportMonthLackTotalReport(empAttn);
			os = response.getOutputStream();
			if(hSSFWorkbook!=null){
				hSSFWorkbook.write(os);
				os.flush();
				os.close();
			}
		} catch (Exception e) {
			
		}finally{
			if(null != os){
				os.flush();
				os.close();
			}
		}
	}
	
	@RequestMapping("/importSignRecord.htm")
	public ModelAndView importSignRecord(){
		return new ModelAndView("base/attnManager/importSignRecord");
	}
    
    @RequestMapping(value="/uploadSignExcel",method = RequestMethod.POST)  
    @ResponseBody  
    public Map<String, String> upload(@RequestParam(value="file",required = false)MultipartFile file,HttpServletRequest request, HttpServletResponse response){  
    
        String result = null;
		try {
			result = empAttnService.readExcelFile(file);
		} catch (IOException e) {
			result = "??????excel??????";
		}  
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("response", result);
        return map;  
    }  
    
	@ResponseBody
	@RequestMapping("/reCalculate.htm")
	public Map<String, String> reCalculate(final TransNormal transNormal){
		
		Map<String, String> map = new HashMap<String, String>();
		map = transNormalService.recalculateAttnByCondition(transNormal);
		return map;
	}
	
	//??????????????????
    @RequestMapping(value="/updateSetting.htm",method = RequestMethod.POST)  
    @ResponseBody  
    public Map<String, Object> updateSetting(ClassSetting setting){ 
    
        Map<String,Object> result = new HashMap<String,Object>();
		try {
			classSettingService.updateById(setting);
			result.put("message", "????????????");
			result.put("flag", true);
		} catch (Exception e) {
			result.put("message", e.getMessage());
			result.put("flag", false);
		}
		return result; 
    }
    
    @IsMenu
    @RequestMapping("/revisionAttendance.htm")
	public ModelAndView revisionAttendance(HttpServletRequest request){
    
    	//??????????????????3???tab??????
    	Long userId = userService.getCurrentUser().getId();
        //??????????????????????????????
    	List<RabcResource> resList = resourceService.getAllAdminTabListByUserId(userId);
    	boolean queryFlag = false;//????????????????????????
    	boolean absoluteFlag = false;//????????????????????????
    	boolean relativeFlag = false;//????????????????????????
    	for(RabcResource data:resList){
			if("modifyQuery".equals(data.getCode())){
				queryFlag = true;
			}
			if("absoluteModification".equals(data.getCode())){
				absoluteFlag = true;
			}
			if("relativeModification".equals(data.getCode())){
				relativeFlag = true;
			}
    	}
    	request.setAttribute("queryFlag", queryFlag);
    	request.setAttribute("absoluteFlag", absoluteFlag);
    	request.setAttribute("relativeFlag", relativeFlag);
		return new ModelAndView("base/attnManager/revisionAttendance");
	}
    
    //????????????-??????????????????-??????????????????-????????????
  	@IsOperation(returnType=false)//????????????????????????
    @RequestMapping(value="/absolutUpdateAttnTime.htm",method = RequestMethod.POST)  
    @ResponseBody  
    public Map<String, Object> absolutUpdateAttnTime(String employeeIds,String attnDate,String startTime,String endTime,String dataType,String remark){  
  		
        Map<String,Object> result = new HashMap<String,Object>();
		try {
			result=	empAttnService.updateAttnTime(employeeIds, attnDate, startTime, endTime, dataType,remark);	
		} catch (Exception e) {
			result.put("message", e.getMessage());
			result.put("flag", false);
		}
		return result;  
		 
    }
    
  	//????????????-??????????????????-??????????????????-????????????
  	@IsOperation(returnType=false)//????????????????????????
    @RequestMapping(value="/relativeUpdateAttnTime.htm",method = RequestMethod.POST)  
    @ResponseBody  
    public Map<String, Object> relativeUpdateAttnTime(String employeeIds,String attnDate,String startTime,String endTime,String dataType,String remark){  
  		
        Map<String,Object> result = new HashMap<String,Object>();
		try {
			result=	empAttnService.updateAttnTime(employeeIds, attnDate, startTime, endTime, dataType,remark);	

		} catch (Exception e) {
			result.put("message", e.getMessage());
			result.put("flag", false);
		}
		return result;  
	
    }
  	
  	@RequestMapping(value="/deleteSignRecordById.htm")  
    @ResponseBody  
    public Map<String, Object> deleteSignRecordById(Long id,Integer delFlag){  
  		
        Map<String,Object> result = new HashMap<String,Object>();
		try {
			empAttnService.deleteSignRecordById(id, delFlag);
			result.put("flag", true);
		} catch (Exception e) {
			result.put("message", e.getMessage());
			result.put("flag", false);
		}
	    return result;  
		 
    }
  	
    
    //????????????-??????????????????-??????????????????-????????????-??????
    @IsOperation(returnType=false)//????????????????????????
    @ResponseBody
	@RequestMapping("/getUpdateLogList.htm")
	public PageModel<AttnUpdateLog> getUpdateLogList(AttnUpdateLog attnUpdateLog){
    	
		PageModel<AttnUpdateLog> pm=new PageModel<AttnUpdateLog>();
		pm.setRows(new java.util.ArrayList<AttnUpdateLog>());
		
		try {
			pm = attnUpdateLogService.getPageList(attnUpdateLog);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
    
    //??????????????????
    @RequestMapping(value="/getAttTimeById.htm",method = RequestMethod.POST)  
    @ResponseBody  
    public Map<String,Object> getAttTimeById(String employeeIds,String attnDate,Integer type){  
    	
    	 Map<String,Object> result = new HashMap<String,Object>();
    	 if(employeeIds==null||attnDate==null){
    		 result.put("flag", false);
    		 return result;
    	 }
    	 EmpAttn attnTimeByIdAndAttnTime =null;
    	 try {
    		 attnTimeByIdAndAttnTime = empAttnService.getAttnTimeByIdAndAttnTime(employeeIds,attnDate,type);
    		 
    		 if(type==3){
    			if(attnTimeByIdAndAttnTime.getAttnDate()==null){
    				result.put("upAttn", true);
    				result.put("downAttn", true);
    			}
    			if(attnTimeByIdAndAttnTime.getStartWorkTime()==null){
    				result.put("upAttn", true);
    			}
    			if(attnTimeByIdAndAttnTime.getEndWorkTime()==null){
    				result.put("downAttn", true);
    			}	
    		}
    		result.put("result", attnTimeByIdAndAttnTime);
    		result.put("message", "????????????");
  			result.put("flag", true);
  		} catch (Exception e) {
  			if(type==3){
  				Date nowDate = DateUtils.parse(attnDate, DateUtils.FORMAT_SHORT);
  				int compareDayDate = DateUtils.compareDayDate(nowDate,new Date());
  				if(compareDayDate==2){
  					result.put("upAttn", true);
  	    			result.put("downAttn", true);
  				}
    		}
  			result.put("message", e.getMessage());
  			result.put("flag", false);
  		}
	    return result;  
  		  
    }
}