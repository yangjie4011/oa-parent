package com.ule.oa.base.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ule.oa.base.service.SmsService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.exception.SmsException;

/**
 * 短信服务
 * 
 * @author 樊航
 */
@Service
public class SmsServiceImpl implements SmsService {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	  * sendRandomCode(发验证码)
	  * @Title: sendRandomCode
	  * @Description: 发验证码
	  * @param phone
	  * @param template
	  * @return
	  * @throws OaException
	  * @throws SmsException    设定文件
	  * @see com.ule.oa.base.service.SmsService#sendRandomCode(java.lang.String, java.lang.String)
	  * @throws
	 */
	@Override
	public String sendRandomCode(String phone, String template) throws OaException, SmsException {
//		OaAssert.notBlank(phone, "手机号码不能为空");
//		OaAssert.notBlank(template, "短信模版不能为空");
//		
//		try {
//			String result = BasicServiceTools.smsSendRandomCode(phone, "250103", template, "2");
//			JSONObject json = null;
//			try {
//				json = JSONObject.fromObject(result);
//				logger.info("验证码:"+json);
//			} catch (Exception e) {
//				throw new OaException("验证码发送失败");
//			}
//			if (!"0000".equals(json.getString("returnCode"))) {
//				throw new OaException(json.getString("returnMessage"));
//			}
//			String code = (String) json.get("randomCode");
//			return code;
//		} catch (Exception e) {
//			throw new OaException(e.getMessage());
//		}
		return null;
	}

	/**
	  * sendMessage(发短信)
	  * @Title: sendMessage
	  * @Description: 发短信
	  * @param phone
	  * @param content
	  * @return
	  * @throws OaException
	  * @throws SmsException    设定文件
	  * @see com.ule.oa.base.service.SmsService#sendMessage(java.lang.String, java.lang.String)
	  * @throws
	 */
	@Override
	public boolean sendMessage(String phone, String content) throws OaException, SmsException {
//		OaAssert.notBlank(phone, "手机号码不能为空");
//		OaAssert.notBlank(content, "短信内容不能为空");
//		
//		String result = null;
//		try {
//			result = BasicServiceTools.smsSend(phone, content, "0314", "2");
//		} catch (Exception e) {
//			throw new SmsException(e);
//		}
//		try {
//			JSONObject json = JSONObject.fromObject(result);
//			String code = json.getString("returnCode");
//			if ("0000".equals(code)) {
//				return true;
//			}
//			String returnMessage = json.getString("returnMessage");
//			logger.info("短信发送失败" + returnMessage);
//			return false;
//		} catch (Exception e) {
//			throw new OaException(e);
//		}
		return false;
		
	}
}
