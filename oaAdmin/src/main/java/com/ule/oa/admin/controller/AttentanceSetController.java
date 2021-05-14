package com.ule.oa.admin.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import com.ule.oa.admin.util.IsMenu;
import com.ule.oa.base.po.AttentanceSetDTO;
import com.ule.oa.base.service.AttentanceSetService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.common.cache.distlock.DistLockUtil;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.PageModel;

/**
 * 考勤管理-考勤设置
 * @author yangjie
 *
 */
@Controller
@RequestMapping("attentanceSet")
public class AttentanceSetController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private AttentanceSetService attentanceSetService;
	
	//考勤管理-考勤设置-页面
	@IsMenu
	@RequestMapping("/index.htm")
	public ModelAndView index(HttpServletRequest request){
		try {
			request.setAttribute("month",DateUtil.formatDate(new Date(), "yyyy-MM"));
			request.setAttribute("companyId", employeeService.getCurrentEmployee().getCompanyId());
		} catch (OaException e) {
			
		}
		return new ModelAndView("base/attnManager/attentance_set");
	}
	
	//考勤管理-考勤设置-查询
	@ResponseBody
	@RequestMapping("/getPageList.htm")
	public PageModel<AttentanceSetDTO> getPageList(AttentanceSetDTO param) {
		
		PageModel<AttentanceSetDTO> pm = new PageModel<AttentanceSetDTO>();
		pm.setRows(new java.util.ArrayList<AttentanceSetDTO>());
		pm.setTotal(0);
		pm.setPageNo(1);

		try {
			pm = attentanceSetService.getPageList(param);
		} catch (Exception e) {
			logger.error("AttentanceSetController getPageList"+e.getMessage());
		}

		return pm;
	}
	
	/**
	 * 考勤管理-考勤设置-上传模板
	 * @param file
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/importTemplate.htm",method = RequestMethod.POST)
	public Map<String, String> importTemplate(@RequestParam(value="file",required = false)MultipartFile file) {

		String result = null;
		try {
			Map<String,Object> resultMap = attentanceSetService.importTemplate(file);
			result = (String) resultMap.get("resultMsg");
		} catch (OaException e) {
			result = e.getMessage();
		} catch (Exception e) {
			result = "请下载最新模板后重试";
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("response", result);
        return map;
	}
	
	@ResponseBody
	@RequestMapping(value="/initAllEmployClass.htm")
	public Map<String, String> initAllEmployClass(String month) {

		String lockValue = DistLockUtil.lock("initAllEmployClass",60*1L);
		Map<String, String> map = new HashMap<String, String>();
		if(StringUtils.isBlank(lockValue)){
			logger.info("initAllEmployClass已经启动，请不要重复调用!!!");
			map.put("message", "请勿重复调用");
			return map;
		}else{
			try {
				attentanceSetService.initAllEmployClass(month);
				map.put("message", "true");
			} catch (Exception e) {
				map.put("message", e.getMessage());
			}
		}
        return map;
	}

}
