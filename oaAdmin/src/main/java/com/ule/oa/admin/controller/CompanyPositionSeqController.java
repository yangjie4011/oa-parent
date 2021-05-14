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
import com.ule.oa.base.po.CompanyPositionSeq;
import com.ule.oa.base.service.CompanyPositionSeqService;
import com.ule.oa.common.utils.JSON;
import com.ule.oa.common.utils.JsonWriter;
import com.ule.oa.common.utils.PageModel;

@Controller
@RequestMapping("companyPositionSeq")
public class CompanyPositionSeqController {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Resource
	private CompanyPositionSeqService companyPositionSeqService;
	
	@RequestMapping("/index.htm")
	public ModelAndView index(){
		return new ModelAndView("base/companyPositionSeq/companyPositionSeq_index");
	}
	
	@ResponseBody
	@RequestMapping("/getListByCondition.htm")
	public List<CompanyPositionSeq> getListByCondition(CompanyPositionSeq companyPositionSeq){

		return companyPositionSeqService.getListByCondition(companyPositionSeq);
	}
	
	//职位序列操作
	@ResponseBody
	@RequestMapping("/getReportPageList.htm")
	public PageModel<CompanyPositionSeq> getListByConditionPage(CompanyPositionSeq companyPositionSeq){
		PageModel<CompanyPositionSeq> pm=new PageModel<CompanyPositionSeq>();
		pm.setRows(new java.util.ArrayList<CompanyPositionSeq>());			
		try {			
 			pm = companyPositionSeqService.getByPagenation(companyPositionSeq);			
		} catch (Exception e) {

		}			
		return pm;
	}
	
	@ResponseBody
	@RequestMapping("/getbyId.htm")
	public CompanyPositionSeq getCompanyPositionSeqbyId(Long id){

		CompanyPositionSeq config=null;
		try {
			config=companyPositionSeqService.getById(id);
		} catch (Exception e) {

		}		
		return config;
	}
	
	@ResponseBody
	@RequestMapping("/add.htm")
	public JSON addCompanyForm(CompanyPositionSeq config){

		Map<String,Object> map = new HashMap<String,Object>();		
		try{
			companyPositionSeqService.save(config);			
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
	public JSON update(CompanyPositionSeq config){

		Map<String,Object> map = new HashMap<String,Object>();
		
		try{		
			companyPositionSeqService.updatePositionSeqById(config);			
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
	public JSON deleteCompanyCompanyPositionSeq(long id){

		Map<String,Object> map = new HashMap<String,Object>();		
		try{
			companyPositionSeqService.delete(id);			
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

		int row=companyPositionSeqService.queryName(name,id);			
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

		int row=companyPositionSeqService.queryCode(code,id);	
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
