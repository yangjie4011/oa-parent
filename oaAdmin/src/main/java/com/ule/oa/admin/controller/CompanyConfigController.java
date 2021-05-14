package com.ule.oa.admin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.ule.oa.base.po.CompanyConfig;
import com.ule.oa.base.service.CompanyConfigService;
import com.ule.oa.base.service.CompanyService;
import com.ule.oa.common.utils.JSON;
import com.ule.oa.common.utils.JsonWriter;
import com.ule.oa.common.utils.PageModel;

@Controller
@RequestMapping("companyConfig")
public class CompanyConfigController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private CompanyConfigService companyConfigService;
	
	@Resource
	private CompanyService companyService;
	
	@ResponseBody
	@RequestMapping("/getListByCondition.htm")
	public List<CompanyConfig> getListByCondition(CompanyConfig companyConfig){
		return companyConfigService.getListByCondition(companyConfig);
	}
	
	//公司配置
	
	@RequestMapping("/index.htm")
	public ModelAndView index(){  
		return new ModelAndView("companyConfig/sysCompanyConfig"); 
	}
	
	@ResponseBody
	@RequestMapping("/getReportPageList.htm")
	public PageModel<CompanyConfig> getReportPageList(CompanyConfig config){	
		PageModel<CompanyConfig> pm=new PageModel<CompanyConfig>();
		pm.setRows(new java.util.ArrayList<CompanyConfig>());		
		try {
			pm = companyConfigService.getByPagenation(config);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}		
		return pm;
	}	
	
	@ResponseBody
	@RequestMapping("/getConfigbyId.htm")
	public CompanyConfig getConfigbyId(int id){
		CompanyConfig config=null;
		try {
			config=companyConfigService.getConfigInfoById(id);			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}		
		return config;
	}
	
	@ResponseBody
	@RequestMapping("/getCompanyinfo.htm")
	public List<CompanyConfig> getCompanyinfo(){
		return companyConfigService.getCompanyinfo();	
	}
	
	@ResponseBody
	@RequestMapping("/addCompanyForm.htm")
	public JSON addCompanyForm(CompanyConfig config){
		Map<String,Object> map = new HashMap<String,Object>();		
		try{
			CompanyConfig companyConfig = companyConfigService.saveConfig(config);			
			map.put("message", "保存成功");
			map.put("flag", true);
			map.put("companyConfig", companyConfig);
		}catch(Exception e){
			map.put("flag", false);
			map.put("message", "保存失败,"+e.getMessage());
		} 		
		return JsonWriter.successfulResult(map);
	}
	
	@ResponseBody
	@RequestMapping("/updateCompanyConfig.htm")
	public JSON update(CompanyConfig config){
		Map<String,Object> map = new HashMap<String,Object>();
		
		try{
			companyConfigService.updateCompanyConfig(config);			
			map.put("message", "修改成功");
			map.put("flag", true);
		}catch(Exception e){
			map.put("flag", false);
			map.put("message", "修改失败,msg="+e.getMessage());
		}
		
		return JsonWriter.successfulResult(map);
	}
	
	@ResponseBody
	@RequestMapping("/deleteCompanyConfig.htm")
	public JSON deleteCompanyConfig(int id){
		Map<String,Object> map = new HashMap<String,Object>();
		
		try{			
			companyConfigService.deleteCompanyConfig(id);			
			map.put("message", "删除成功");
			map.put("flag", true);
		}catch(Exception e){
			map.put("flag", false);
			map.put("message", "删除失败,"+e.getMessage());
		}		
		return JsonWriter.successfulResult(map);
	}
}
