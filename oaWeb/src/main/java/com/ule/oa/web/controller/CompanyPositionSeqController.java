package com.ule.oa.web.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ule.oa.base.po.CompanyPositionSeq;
import com.ule.oa.base.service.CompanyPositionSeqService;

@Controller
@RequestMapping("companyPositionSeq")
public class CompanyPositionSeqController {

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
}
