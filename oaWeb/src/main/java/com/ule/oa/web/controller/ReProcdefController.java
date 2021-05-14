package com.ule.oa.web.controller;

import java.util.List;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.ule.oa.base.po.ReProcdef;
import com.ule.oa.base.service.ReProcdefService;
import com.ule.oa.common.utils.PageModel;

@Controller
@RequestMapping("/reProcdef")
public class ReProcdefController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private ReProcdefService reProcdefService;
	
	@RequestMapping("/index.htm")
	public ModelAndView index(){
		return new ModelAndView("base/apply/apply");
	}
	
	/**
	 * 分页查询列表
	 * @param reProcdef
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getPageList.htm")
	public PageModel<ReProcdef> getPageList(ReProcdef reProcdef){
		PageModel<ReProcdef> pm = new PageModel<ReProcdef>();
		pm.setRows(new java.util.ArrayList<ReProcdef>());
		pm.setTotal(0);
		pm.setPageNo(1);
		try {
			pm = reProcdefService.getByPagenation(reProcdef);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return pm;
	}
	
	/**
	  * 根据选择的流程类型加载流程名称下拉
	  * @Title: getNameByProcessType
	  * @Description: 根据选择的流程类型加载流程名称下拉
	  * @param reProcdef
	  * @return    设定文件
	  * List<ReProcdef>    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/getNameByProcessType.htm")
	public List<ReProcdef> getNameByProcessType(ReProcdef reProcdef){
		return reProcdefService.getListByCondition(reProcdef);
	}

}
