package com.ule.oa.service.web.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ule.oa.base.po.SendMail;
import com.ule.oa.base.service.SendMailService;
import com.ule.oa.common.cache.distlock.DistLockUtil;
import com.ule.oa.common.utils.http.IPUtils;

@Controller
@RequestMapping("sendEmail")
public class SendEmailController {
	
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
	@ResponseBody
	@RequestMapping("/sendEmail.htm")
	public Map<String, String> sendEmail(HttpServletRequest request) throws Exception{
		String lockValue = DistLockUtil.lock("sendEmail",60*9L);
		Map<String, String> map = new HashMap<String, String>();
		if(StringUtils.isBlank(lockValue)){
			logger.info("邮件发送定时已经启动，请不要重复调用当前定时!!!");
			map.put("response", "请勿重复调用当前定时!!!");
			return map;
		}else{//锁定定时
			logger.info("邮件定时启动,请求方IP={}",IPUtils.getIpAddress(request));
			sendMailService.sendEmail();
			map.put("response", "sendEmail触发成功,请稍后查看数据！");
			return map;
		}
	}
	
	@ResponseBody
	@RequestMapping("/updateStatus.htm")
	public Map<String, String> updateStatus(Long id) throws Exception{
		Map<String, String> map = new HashMap<String, String>();
		logger.info("修改邮件发送状态，id="+id);
		SendMail update = new SendMail();
		update.setUpdateTime(new Date());
		update.setUpdateUser("system");
		update.setId(id);
		update.setSendStatus(SendMail.SEND_STATUS_NO);
		int i = sendMailService.updateById(update);
		map.put("response", "修改成功,updateCount="+i);
		return map;
		
	}
}
