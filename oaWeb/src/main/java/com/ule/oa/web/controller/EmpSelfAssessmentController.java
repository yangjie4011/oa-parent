package com.ule.oa.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ule.oa.base.po.EmpSelfAssessment;
import com.ule.oa.base.service.EmpSelfAssessmentService;
import com.ule.oa.common.utils.JSON;

/**
 * 
  * @ClassName: EmpSelfAssessmentController
  * @Description: 员工自我评估
  * @author jiwenhang
  * @date 2017年5月24日 上午11:06:05
 */
@Controller
@RequestMapping("empSelfAssessment")
public class EmpSelfAssessmentController {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private EmpSelfAssessmentService empSelfAssessmentService;
	
	@RequestMapping("/index.htm")
	public String index(HttpServletRequest request){
		Long id = 1L;//员工ID
		empSelfAssessmentService.setEmp(request, id);
		return "base/empSelfAssessment/empSelfAssessment_index";
	}
	
	@ResponseBody
	@RequestMapping("/saveAndUpdate.htm")
	public JSON saveAndUpdate(EmpSelfAssessment model){
		JSON json = new JSON(false, null, null);
		try {
			json.setResult(empSelfAssessmentService.saveAndUpdate(model));
			json.setSuccess(true);
			json.setMessage("执行成功!");
		} catch (Exception e) {
			json.setResult(new EmpSelfAssessment());
			json.setMessage("执行失败!");
		}
		return json;
	}
}
