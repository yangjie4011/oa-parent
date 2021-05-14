package com.ule.oa.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ule.oa.base.po.CoopCompany;
import com.ule.oa.base.service.CoopCompanyService;

/**
  * @ClassName: CoopCompanyController
  * @Description: 合作公司业务层
  * @author minsheng
  * @date 2017年5月22日 下午9:36:56
 */
@Controller
@RequestMapping("coopCompany")
public class CoopCompanyController {
	@Autowired
	private CoopCompanyService coopCompanyService;
	
	/**
	  * getListByCondition(根据选择的员工类型加载合作下拉)
	  * @Title: getListByCondition
	  * @Description: 根据选择的员工类型加载合作下拉
	  * @param coopCompany
	  * @return    设定文件
	  * List<CoopCompany>    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/getListByCondition.htm")
	public List<CoopCompany> getListByCondition(CoopCompany coopCompany){
		return coopCompanyService.getListByCondition(coopCompany);
	}
}
