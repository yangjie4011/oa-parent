package com.ule.oa.service.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ule.oa.base.service.EmployeeClassService;
import com.ule.oa.common.cache.distlock.DistLockUtil;
import com.ule.oa.common.utils.http.IPUtils;

/**
  * @ClassName: EmployeeClassController
  * @Description: 员工排班控制层
  * @author minsheng
  * @date 2017年12月1日 上午10:43:09
 */
@Controller
@RequestMapping("employeeClass")
public class EmployeeClassController {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private EmployeeClassService employeeClassService;
	
	/**
	 * @throws Exception 
	  * mustArrangeOfWorkMind(排班提醒)
	  * @Title: mustArrangeOfWorkMind
	  * @Description: 排班提醒
	  * void    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/mustArrangeOfWorkMind.htm")
	public Map<String, String> mustArrangeOfWorkMind(HttpServletRequest request) throws Exception{
		String lockValue = DistLockUtil.lock("mustArrangeOfWorkMind",60*5L);
		Map<String, String> map = new HashMap<String, String>();
		if(StringUtils.isBlank(lockValue)){
			logger.info("排班提醒定时已经启动，请不要重复调用当前定时!!!");
			map.put("response", "请勿重复调用当前定时!!!");
			return map;
		}else{//锁定定时
			logger.info("排班提醒定时启动,请求方IP={}",IPUtils.getIpAddress(request));
			employeeClassService.mustArrangeOfWorkMind();
			map.put("response", "mustArrangeOfWorkMind触发成功,请稍后查看数据！");
			return map;
		}
	}
}
