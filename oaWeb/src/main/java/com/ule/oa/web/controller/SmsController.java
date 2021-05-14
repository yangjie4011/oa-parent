package com.ule.oa.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ule.oa.base.cache.ValidcodeCacheManager;
import com.ule.oa.base.service.SmsService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.exception.SmsException;
import com.ule.oa.common.utils.JSON;
import com.ule.oa.common.utils.JsonWriter;

/**
  * @ClassName: SmsController
  * @Description: 返送短信和验证码
  * @author minsheng
  * @date 2017年6月5日 上午9:22:56
 */
@Controller
@RequestMapping("sms")
public class SmsController {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private SmsService smsService;
	@Autowired
	private ValidcodeCacheManager validcodeCacheManager;
	
	/**
	  * sendRandomCode(发送验证码)
	  * @Title: sendRandomCode
	  * @Description: 发送验证码
	  * @param phone
	  * @return
	  * @throws OaException
	  * @throws SmsException    设定文件
	  * String    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/sendRandomCode.htm")
	public JSON sendRandomCode(String phone) throws OaException, SmsException{
		try{
			String randomCode = smsService.sendRandomCode(phone, "您的验证码为:{password}, 请于30分钟内及时使用【OA】");
			validcodeCacheManager.put(phone, randomCode);
			
			logger.info("手机号[{}]本次发送的验证码为[{}]",phone,randomCode);
		}catch(Exception e){
			return JsonWriter.failedMessage("修改密码失败，原因:"+e.getMessage());
		}
		
		return JsonWriter.successfulJson();
	}

	/**
	  * sendMessage(发送短信)
	  * @Title: sendMessage
	  * @Description: 发送短信
	  * @param phone
	  * @return
	  * @throws OaException
	  * @throws SmsException    设定文件
	  * String    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/sendMessage.htm")
	public JSON sendMessage(String phone,String message) throws OaException, SmsException{
		try{
			smsService.sendMessage(phone,message);
		}catch(Exception e){
			return JsonWriter.failedMessage("发送短信失败，原因:"+e.getMessage());
		}
		
		return JsonWriter.successfulJson();
	}
}
