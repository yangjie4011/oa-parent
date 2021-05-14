package com.ule.oa.web.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ule.oa.base.po.CompanyPositionLevel;
import com.ule.oa.base.service.CompanyPositionLevelService;
import com.ule.oa.common.utils.PageModel;

@Controller
@RequestMapping("companyPositionLevel")
public class CompanyPositionLevelController {
	
	@Resource
	private CompanyPositionLevelService companyPositionLevelService;
	
	@RequestMapping("/index.htm")
	public ModelAndView index(){
		return new ModelAndView("base/companyPositionLevel/companyPositionLevel_index");
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
}
