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
import com.ule.oa.admin.util.IsMenu;
import com.ule.oa.base.po.CompanyPositionLevel;
import com.ule.oa.base.service.CompanyPositionLevelService;
import com.ule.oa.common.utils.JSON;
import com.ule.oa.common.utils.JsonWriter;
import com.ule.oa.common.utils.PageModel;

@Controller
@RequestMapping("companyPositionLevel")
public class CompanyPositionLevelController {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Resource
	private CompanyPositionLevelService companyPositionLevelService;
	
	@IsMenu
	@RequestMapping("/index.htm")
	public ModelAndView index(){
		return new ModelAndView("base/companyPositionLevel/companyPositionLevelIndex");
	}
	
	@ResponseBody
	@RequestMapping("/pageList.htm")
	public PageModel<CompanyPositionLevel> getByPagenation(CompanyPositionLevel companyPositionLevel) {
		return null;
	}

	@ResponseBody
	@RequestMapping("/getListByCondition.htm")
	public List<CompanyPositionLevel> getListByCondition(CompanyPositionLevel companyPositionLevel){
		return companyPositionLevelService.getListByCondition(companyPositionLevel);
	}
	
	//职位等级操作
	@ResponseBody
	@RequestMapping("/getReportPageList.htm")
	public PageModel<CompanyPositionLevel> getListByConditionPageModel(CompanyPositionLevel companyPositionLevel){
		PageModel<CompanyPositionLevel> pm=new PageModel<CompanyPositionLevel>();
			pm.setRows(new java.util.ArrayList<CompanyPositionLevel>());			
			try {			
	 			pm = companyPositionLevelService.getByPagenation(companyPositionLevel);			
			} catch (Exception e) {
				
			}			
		return pm;
	}
	
	@ResponseBody
	@RequestMapping("/getbyId.htm")
	public CompanyPositionLevel getCompanyPositionLevelbyId(Long id){
		CompanyPositionLevel config=null;
		try {
			config=companyPositionLevelService.getById(id);
		} catch (Exception e) {

		}		
		return config;
	}
	
	@ResponseBody
	@RequestMapping("/add.htm")
	public JSON addCompanyForm(CompanyPositionLevel config){

		Map<String,Object> map = new HashMap<String,Object>();		
		try{
			companyPositionLevelService.save(config);			
			map.put("message", "保存成功");
			map.put("flag", true);									
		}catch(Exception e){
			map.put("flag", false);
			map.put("message", "保存失败,"+e.getMessage());
		}		
		return JsonWriter.successfulResult(map);
	}
	
	@ResponseBody
	@RequestMapping("/update.htm")
	public JSON update(CompanyPositionLevel config){
		Map<String,Object> map = new HashMap<String,Object>();
		
		try{		
			companyPositionLevelService.updatePositionLevelById(config);			
			map.put("message", "修改成功");
			map.put("flag", true);					
		}catch(Exception e){
			map.put("flag", false);
			map.put("message", "修改失败,msg="+e.getMessage());
		}		
		return JsonWriter.successfulResult(map);
	}
	
	@ResponseBody
	@RequestMapping("/delete.htm")
	public JSON deleteCompanyCompanyPositionLevel(long id){
		Map<String,Object> map = new HashMap<String,Object>();		
		try{
			companyPositionLevelService.delete(id);			
			map.put("message", "删除成功");
			map.put("flag", true);				
		}catch(Exception e){
			map.put("flag", false);
			map.put("message", "删除失败,"+e.getMessage());
		}		
		return JsonWriter.successfulResult(map);
	}
	
	@ResponseBody
	@RequestMapping("/queryName.htm")
	public boolean queryName(String name,int id){
		int row=companyPositionLevelService.queryName(name,id);			
		if(id!=0){		
			if(row!=1){  //修改名字数据有 就行数 为1				
				return true;
			}
		}else if(id==0){		
			if(row>0){
				return true;
			}				
		}
		return false;	
	}
	
	@ResponseBody
	@RequestMapping("/queryCode.htm")
	public boolean queryCode(String code,int id){		
		int row=companyPositionLevelService.queryCode(code,id);	
		if(id!=0){
			if(row!=1){  //修改名字数据有 就行数 为1				
				return true;
			}
		}else if(id==0){		
			if(row>0){
				return true;
			}				
		}
		return false;	
	}
}
