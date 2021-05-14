package com.ule.oa.admin.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ule.oa.base.service.SendMailService;
import com.ule.oa.common.cache.distlock.DistLockUtil;
import com.ule.oa.common.utils.http.IPUtils;

@Controller
@RequestMapping("otherWork")
public class OtherWorkController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private SendMailService sendMailService;
	/**
	 * @throws Exception 
	  * @Title: 定时发邮件
	  * @Description: 每10分钟查一次邮件表，发邮件
	  * void    返回类型
	  * @throws
	 */
	@RequestMapping("/sendEmail.htm")
	public void sendEmail(HttpServletRequest request) throws Exception{
	
		String lockValue = DistLockUtil.lock("attnExMsgRemind",60*9L);
		if(StringUtils.isBlank(lockValue)){
			logger.info("手动调用发送邮件,请不要重复调用!!!");
			return;
		}else{//锁定定时
			logger.info("手动调用发送邮件,请求方IP={}",IPUtils.getIpAddress(request));
			sendMailService.sendEmail();
		}
	}
}
