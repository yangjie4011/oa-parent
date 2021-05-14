package com.ule.oa.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ule.oa.base.po.HiActinst;
import com.ule.oa.base.service.HiActinstService;
import com.ule.oa.common.utils.PageModel;

@Controller
@RequestMapping("/hiActinst")
public class HiActinstController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private HiActinstService hiActinstService;
	
	/**
	 * 跳转流程详细信息页面
	 * @return
	 */
	@RequestMapping("/hiActinst_detail.htm")
	public ModelAndView hiActinst_detail(HttpServletRequest request, String ruTaskId){
		request.setAttribute("ruTaskId", ruTaskId);
		return new ModelAndView("base/apply/hiActinst_detail");
	}
	
	/**
	 * 查询列表
	 * @param ruProcdef
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getList.htm")
	public PageModel<HiActinst> getList(HttpServletResponse response, String ruTaskId){
		HiActinst hiActinst = new HiActinst();
		hiActinst.setRuTaskId(Long.valueOf(ruTaskId));
		List<HiActinst> hiActinstList = hiActinstService.getList(hiActinst);
		PageModel<HiActinst> pm = new PageModel<HiActinst>();
		pm.setRows(hiActinstList);
		return pm;
	}

}
