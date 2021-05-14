package com.ule.oa.admin.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ule.oa.admin.util.IsOperation;
import com.ule.oa.base.po.AnnualVacation;
import com.ule.oa.base.po.EmpApplicationOutgoing;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.AnnualVacationService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.utils.PageModel;

/***
 * 
 * @author yangjie
 *
 */
@Controller
@RequestMapping("vacation")
public class AnnualVacationController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private AnnualVacationService annualVacationService;
	@Autowired
	private UserService userService;
	
	@RequestMapping("/toList.htm")
	public ModelAndView toList(HttpServletRequest request){
		return new ModelAndView("base/vacation/toList");
	}
	
	@RequestMapping("/toAdd.htm")
	public ModelAndView toAdd(HttpServletRequest request){
		return new ModelAndView("base/vacation/toAdd");
	}
	
	@ResponseBody
	@RequestMapping("/list.htm")
	public PageModel<AnnualVacation> list(AnnualVacation vacation){
		
		PageModel<AnnualVacation> pm=new PageModel<AnnualVacation>();
		pm.setRows(new java.util.ArrayList<AnnualVacation>());
		
		try {
			pm = annualVacationService.getListByPage(vacation);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	@ResponseBody
	@RequestMapping("/delete.htm")
	public Map<String,Object> delete(Long id){
		Map<String,Object> result = new HashMap<String,Object>();
		try {
			annualVacationService.delete(id);
			result.put("msg", "删除成功");
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return result;
	}
	
	@ResponseBody
	@RequestMapping("/add.htm")
	public Map<String,Object> add(AnnualVacation vacation){
		Map<String,Object> result = new HashMap<String,Object>();
		try {
			User user = userService.getCurrentUser();
			annualVacationService.saveVacation(vacation, user);
			result.put("msg", "新增成功");
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return result;
	}

}
