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

import com.ule.oa.base.service.AttentanceSetService;
import com.ule.oa.common.cache.distlock.DistLockUtil;
import com.ule.oa.common.utils.http.IPUtils;

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
	private AttentanceSetService attentanceSetService;
	
	/**
	 * @throws Exception 
	  * sendMailToAttentanceChangeEmp(考勤时间变更邮件提醒)
	  * @Title: sendMailToAttentanceChangeEmp
	  * @Description: 考勤时间变更邮件提醒(月初月末6点提醒一次)
	  * void    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/sendMailToAttentanceChangeEmp.htm")
	public Map<String, String> sendMailToAttentanceChangeEmp(HttpServletRequest request) throws Exception{
		String lockValue = DistLockUtil.lock("attentanceChange",60*5L);
		Map<String, String> map = new HashMap<String, String>();
		if(StringUtils.isBlank(lockValue)){
			logger.info("考勤时间变更邮件提醒定时已经启动，请不要重复调用当前定时!!!");
			map.put("response", "请勿重复调用当前定时!!!");
			return map;
		}else{//锁定定时
			logger.info("考勤时间变更邮件提醒定时启动,请求方IP={}",IPUtils.getIpAddress(request));
			try{
				attentanceSetService.sendMailToClassChangeEmp();
				map.put("response", "sendMailToAttentanceChangeEmp触发成功,请稍后查看数据！");
			}catch(Exception e){
				
			}
			return map;
		}
	}

}
