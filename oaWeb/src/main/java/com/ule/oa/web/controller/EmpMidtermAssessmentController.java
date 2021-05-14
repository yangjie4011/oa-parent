package com.ule.oa.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ule.oa.base.po.EmpMidtermAssessment;
import com.ule.oa.base.po.EmpSelfAssessment;
import com.ule.oa.base.service.EmpMidtermAssessmentService;
import com.ule.oa.common.utils.JSON;

/**
 * 
  * @ClassName: EmpMidtermAssessmentController
  * @Description: 员工转正期中评估
  * @author jiwenhang
  * @date 2017年5月27日 下午1:19:03
 */
@Controller
@RequestMapping("empMidtermAssessment")
public class EmpMidtermAssessmentController {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private EmpMidtermAssessmentService empMidtermAssessmentService;
	
	@RequestMapping("/index.htm")
	public String index(HttpServletRequest request){
		Long id = 1L;//员工ID
		empMidtermAssessmentService.setEmp(request, id);
		return "base/empMidtermAssessment/empMidtermAssessment_index";
	}
	
	@ResponseBody
	@RequestMapping("/saveOrUpdate.htm")
	public JSON saveAndUpdate(EmpMidtermAssessment model){
		JSON json = new JSON(false, null, null);
		try {
			json.setResult(empMidtermAssessmentService.saveAndUpdate(model));
			json.setSuccess(true);
			json.setMessage("执行成功!");
		} catch (Exception e) {
			json.setResult(new EmpSelfAssessment());
			json.setMessage("执行失败!");
		}
		return json;
	}
	
}
