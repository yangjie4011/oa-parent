package com.ule.oa.web.controller;

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
import com.ule.oa.base.po.TransNormal;
import com.ule.oa.base.service.TransNormalService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.utils.http.IPUtils;

/**
  * @ClassName: TransNormalController
  * @Description: 手动触发考勤规则数据重跑
  * @author minsheng
  * @date 2017年9月14日 下午4:05:25
 */
@Controller
@RequestMapping("transNormal")
public class TransNormalController {
	private Logger logger = LoggerFactory.getLogger(TransNormalController.class);
	
	@Autowired
	private UserService userService;
	@Autowired
	private TransNormalService transNormalService;
	
	
	/**
	  * startAttnByPage(跳转到根据页面时间跑考勤规则数据页面)
	  * @Title: startAttnByPage
	  * @Description: 跳转到根据页面时间跑考勤规则数据页面
	  * @return    设定文件
	  * ModelAndView    返回类型
	  * @throws
	 */
	@RequestMapping("/startAttnByPage.htm")
	public ModelAndView startAttnByPage(){
		return new ModelAndView("attendance/attnByTime");
	}
	
	/**
	  * startAttnByTime(根据时间跑考勤规则数据)
	  * @Title: startAttnByTime
	  * @Description: 根据时间跑考勤规则数据
	  * @param transNormal
	  * @return    设定文件
	  * Map<String,String>    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/startAttnByTime.htm")
	public Map<String, String> startAttnByTime(HttpServletRequest request, final TransNormal transNormal){
		logger.info("用户={},ip={}手动触发考勤规则数据定时",userService.getCurrentAccount(),IPUtils.getIpAddress(request));
		Map<String, String> map = new HashMap<String, String>();
		map = transNormalService.recalculateAttnByCondition(transNormal);
		return map;
	}
	
}
