package com.ule.oa.admin.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import com.ule.oa.base.po.CompanyConfig;
import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.EmpType;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.Position;
import com.ule.oa.base.service.CompanyConfigService;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmpTypeService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.PositionService;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.JSON;
import com.ule.oa.common.utils.JsonWriter;
import com.ule.oa.common.utils.PageModel;
import com.ule.oa.common.utils.json.JSONUtils;

@Controller
@RequestMapping("sysConfig")
public class ConfigController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private ConfigService configService;
	@Resource
	private CompanyConfigService companyConfigService;
	@Autowired
	private EmpTypeService empTypeService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private PositionService positionService; 
	@Autowired
	private DepartService departService;
	
	@ResponseBody
	@RequestMapping("getListByCondition")
	public List<Config> getListByCondition(Config config){

		return configService.getListByCondition(config);
	}
	
	@RequestMapping("toUploadFile.htm")
	public ModelAndView toUploadFile(){
		return new ModelAndView("base/sysConfig/fileImport");
	}
	
	
	@RequestMapping(value="/uploadExcel.htm",method = RequestMethod.POST)  
    @ResponseBody  
    public String uploadExcel(MultipartFile file){  

        String msg = "上传失败";
        OutputStream out = null;
        InputStream in = null;
		try{
			in = file.getInputStream();  
            byte[] buffer = new byte[1024];  
            int len = 0;
            String fileName = DateUtils.format(new Date(),DateUtils.FORMAT_SHORT) + "_" + file.getOriginalFilename();//文件名称
            String filePath = ConfigConstants.UPLOAD_FILE_URL + fileName;//文件最终上传的位置  
            
            //如果上传路径不存在，则创建一个文件夹
            File uploadFile = new File(ConfigConstants.UPLOAD_FILE_URL);
            if(!uploadFile.exists()){
            	uploadFile.mkdir();
            }
            
            //上传的文件是否存在，存在则删除
            File uploadFile2 = new File(filePath);
            if(uploadFile2.exists()){
            	if(!uploadFile2.delete()) {
            		logger.error("删除文件失败。。。");
            	}
            	
            }
            
            out = new FileOutputStream(filePath);  
            while ((len = in.read(buffer)) != -1) {  
                out.write(buffer, 0, len);  
            }  
			msg = "上传成功";
        }catch(Exception e){
        	msg = "上传失败,原因=" + e.getMessage();
        }finally {
        	if(out!=null) {
        		try {
					out.close();
				} catch (IOException e) {
				}  
        	}
        	if(in!=null) {
        		try {
					in.close();
				} catch (IOException e) {
				}
        	}
        }
		
		return msg;
    }  
	
	//系统配置代码	
	
	@RequestMapping("/index.htm")
	public ModelAndView index(){
		return new ModelAndView("base/sysConfig/sysConfig");
	}
	
	@ResponseBody
	@RequestMapping("/getReportPageList.htm")
	public PageModel<Config> getReportPageList(Config config){		

 		PageModel<Config> pm=new PageModel<Config>();
 		pm.setRows(new java.util.ArrayList<Config>());			
		try {			
 			pm = configService.getByPagenation(config);			
		} catch (Exception e) {

		}			
		return pm;
	}	
	
	@ResponseBody
	@RequestMapping("/getConfigbyId.htm")
	public Config getConfigbyId(Long id){

		Config config=null;
		try {
			config=configService.getConfigInfoById(id);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}		
		return config;
	}
	
	@ResponseBody
	@RequestMapping("/addConfigForm.htm")
	public JSON addCompanyForm(Config config){

		Map<String,Object> map = new HashMap<String,Object>();		
		try{
			configService.saveConfig(config);			
			map.put("message", "保存成功");
			map.put("flag", true);									
		}catch(Exception e){
			map.put("flag", false);
			map.put("message", "保存失败,"+e.getMessage());
		}		
		return JsonWriter.successfulResult(map);
	}
	
	@ResponseBody
	@RequestMapping("/updateConfig.htm")
	public JSON update(Config config,HttpServletRequest request){

		Map<String,Object> map = new HashMap<String,Object>();
		
		try{		
			configService.updateConfig(config);			
			map.put("message", "修改成功");
			map.put("flag", true);					
		}catch(Exception e){
			map.put("flag", false);
			map.put("message", "修改失败,msg="+e.getMessage());
		}		
		return JsonWriter.successfulResult(map);
	}
	
	@ResponseBody
	@RequestMapping("/deleteConfig.htm")
	public JSON deleteCompanyConfig(long id,HttpServletRequest request){

		Map<String,Object> map = new HashMap<String,Object>();		
		try{
			configService.deleteConfig(id);			
			map.put("message", "删除成功");
			map.put("flag", true);				
		}catch(Exception e){
			map.put("flag", false);
			map.put("message", "删除失败,"+e.getMessage());
		}		
		return JsonWriter.successfulResult(map);
	}
	
	
	
	//TODO:导入员工初始数据
	@ResponseBody 
	@RequestMapping(value = "/inportEmp.htm", produces = "text/json;charset=UTF-8")
	public ModelAndView readExcel(@RequestParam(value="excelFile") MultipartFile file,HttpServletRequest request){  
		 ModelAndView mv = new ModelAndView();  
		 //判断文件是否为空  
		    if(file == null){  
		        mv.addObject("msg", "fail");  
		        return mv;  
		    }  
		    boolean result ;
		    try {
		    	result = configService.inportEmpInfo(file);
		    	mv.addObject("msg",result?"true":"fail");
			} catch (Exception e) {
				logger.error("导入员工基本数据-----失败");
				mv.addObject("msg","fail");
			}
		    return mv;
	}
	
	@ResponseBody 
	@RequestMapping(value = "/getSelectData.htm", produces = "text/json;charset=UTF-8")
	public void getSelectData(HttpServletRequest request,HttpServletResponse response) throws IOException{
		response.setContentType("text/plain");  
        response.setHeader("Pragma", "No-cache");  
        response.setHeader("Cache-Control", "no-cache");  
        response.setDateHeader("Expires", 0);  
        PrintWriter out = response.getWriter();       
        String jsonpCallback = request.getParameter("jsonpCallback");//客户端请求参数  
        Map<String,Object> result = new HashMap<String,Object>();
       
        //工时类型
        CompanyConfig typeOfWork = new CompanyConfig();
        typeOfWork.setCode("typeOfWork");
        List<CompanyConfig> typeOfWorkList = companyConfigService.getListByCondition(typeOfWork);
        List typeOfWorkList0 = new ArrayList<>();
        for(CompanyConfig data:typeOfWorkList){
        	Map<String,String> data0 = new HashMap<String,String>();
        	data0.put("id", String.valueOf(data.getId()));
        	data0.put("name", data.getDisplayName());
        	typeOfWorkList0.add(data0);
        }
        result.put("workTypeName", typeOfWorkList0);
       
        //婚姻状况
        Config maritalStatus = new Config();
        maritalStatus.setCode("maritalStatus");
        List<Config> maritalStatusLsit = configService.getListByCondition(maritalStatus);
        List maritalStatusLsit0 = new ArrayList<>();
        for(Config data:maritalStatusLsit){
        	Map<String,String> data0 = new HashMap<String,String>();
        	data0.put("id", String.valueOf(data.getId()));
        	data0.put("name", data.getDisplayName());
        	maritalStatusLsit0.add(data0);
        }
        result.put("maritalStatusName", maritalStatusLsit0);
       
        //政治面貌
        Config politicalStatus = new Config();
        politicalStatus.setCode("politicalStatus");
        List<Config> politicalStatusLsit = configService.getListByCondition(politicalStatus);
        List politicalStatusLsit0 = new ArrayList<>();
        for(Config data:politicalStatusLsit){
        	Map<String,String> data0 = new HashMap<String,String>();
        	data0.put("id", String.valueOf(data.getId()));
        	data0.put("name", data.getDisplayName());
        	politicalStatusLsit0.add(data0);
        }
        result.put("politicalName", politicalStatusLsit0);
      
        //文化程度
        Config educationLevel = new Config();
        educationLevel.setCode("educationLevel");
        List<Config> educationLevelLsit = configService.getListByCondition(educationLevel);
        List educationLevelLsit0 = new ArrayList<>();
        for(Config data:educationLevelLsit){
        	Map<String,String> data0 = new HashMap<String,String>();
        	data0.put("id", String.valueOf(data.getId()));
        	data0.put("name", data.getDisplayName());
        	educationLevelLsit0.add(data0);
        }
        result.put("degreeOfEducation", educationLevelLsit0);
        
        //是否排班
        Config whetherScheduling = new Config();
        whetherScheduling.setCode("whetherScheduling");
        List<Config> whetherSchedulingLsit = configService.getListByCondition(whetherScheduling);
        List whetherSchedulingLsit0 = new ArrayList<>();
        for(Config data:whetherSchedulingLsit){
        	Map<String,String> data0 = new HashMap<String,String>();
        	data0.put("id", String.valueOf(data.getId()));
        	data0.put("name", data.getDisplayName());
        	whetherSchedulingLsit0.add(data0);
        }
        result.put("whetherSchedulingName", whetherSchedulingLsit0);
        
        //部门
        List<Depart> departList = departService.getFirstDepart();
        List departList0 = new ArrayList<>();
        for(Depart data:departList){
        	Map<String,String> data0 = new HashMap<String,String>();
        	data0.put("id", String.valueOf(data.getId()));
        	data0.put("name",data.getName());
        	departList0.add(data0);
        }
        result.put("departList", departList0);
       
        //部门负责人
        List<Employee> departHeaderList = employeeService.getDepartHeaderList();
        List departHeaderList0 = new ArrayList<>();
        for(Employee data:departHeaderList){
        	Map<String,String> data0 = new HashMap<String,String>();
        	data0.put("id", String.valueOf(data.getId()));
        	data0.put("name",data.getCnName());
        	departHeaderList0.add(data0);
        }
        result.put("departLeaderName", departHeaderList0);
        
        //员工类型
        EmpType empType =new EmpType();
        empType.setCompanyId(1L);
        List<EmpType> empTypeList = empTypeService.getListByCondition(empType);
        for (EmpType empType2 : empTypeList) {
        	empType2.setEmptypeId(empType2.getId());
        	empType2.setId(null);
		}
        result.put("empTypeId", empTypeList);
        
        out.println(jsonpCallback+"("+JSONUtils.write(result)+")");//返回jsonp格式数据  
        out.flush();  
        out.close();  
	}
	
	@ResponseBody 
	@RequestMapping(value = "/getSelectDataByDepartId.htm", produces = "text/json;charset=UTF-8")
	public void getSelectDataByDepartId(HttpServletRequest request,HttpServletResponse response,Long departId) throws IOException{

		response.setContentType("text/plain");  
        response.setHeader("Pragma", "No-cache");  
        response.setHeader("Cache-Control", "no-cache");  
        response.setDateHeader("Expires", 0);  
        PrintWriter out = response.getWriter();       
        String jsonpCallback = request.getParameter("jsonpCallback");//客户端请求参数  
        Map<String,Object> result = new HashMap<String,Object>();
        //汇报对象
        Map<Integer, List<Employee>> littelList = new HashMap<Integer, List<Employee>>();
		List<Employee> mLeaderList = employeeService.getMLeaderByDepartId(departId);
		Integer minValue = 0;
		if(mLeaderList != null && mLeaderList.size() > 0) {
			minValue = mLeaderList.get(0).getEmpPosition().getPosition().getSeqRank();
			for (Employee employee : mLeaderList) {
				if(employee.getEmpPosition().getPosition().getSeqRank() < minValue) {
					minValue = employee.getEmpPosition().getPosition().getSeqRank();
				}
				List<Employee> eList = new ArrayList<Employee>();
				if(littelList.containsKey(employee.getEmpPosition().getPosition().getSeqRank())) {
					eList = littelList.get(employee.getEmpPosition().getPosition().getSeqRank());
				}
				eList.add(employee);
				littelList.put(employee.getEmpPosition().getPosition().getSeqRank(), eList);
			}
		}
		List<Employee> returnList = littelList.get(minValue);
		List returnList0 = new ArrayList<>();
		if(returnList!=null&&returnList.size()>0){
		  for(Employee data:returnList){
	        	Map<String,String> data0 = new HashMap<String,String>();
	        	data0.put("id", String.valueOf(data.getId()));
	        	data0.put("name",data.getCnName());
	        	returnList0.add(data0);
	        }
		}
		result.put("reportToLeader", returnList0);
        //职位
		Position position = new Position();
		position.setDepartId(departId);
		List<Position> positionList = positionService.getListByCondition(position);
		List positionList0 = new ArrayList<>();
        for(Position data:positionList){
        	Map<String,String> data0 = new HashMap<String,String>();
        	data0.put("id", String.valueOf(data.getId()));
        	data0.put("name",data.getPositionName());
        	positionList0.add(data0);
        }
		result.put("positionId", positionList0);
       
		out.println(jsonpCallback+"("+JSONUtils.write(result)+")");//返回jsonp格式数据  
        out.flush();  
        out.close();  
	}
}
