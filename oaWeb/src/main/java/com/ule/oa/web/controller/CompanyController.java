package com.ule.oa.web.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ule.oa.base.po.Company;
import com.ule.oa.base.service.CompanyService;

/**
  * @ClassName: CompanyController
  * @Description: 获得公司信息
  * @author minsheng
  * @date 2017年5月10日 下午4:49:30
 */
@Controller
@RequestMapping("company")
public class CompanyController {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private CompanyService companyService;
	
	@ResponseBody
	@RequestMapping("/getListByCondition.htm")
	public List<Company> getListByCondition(Company company){
		return companyService.getListByCondition(company);
	}
}
