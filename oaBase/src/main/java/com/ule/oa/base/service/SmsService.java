package com.ule.oa.base.service;

import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.exception.SmsException;


/**
 * 短信服务
 * 
 * @author 樊航
 */
public interface SmsService {
	
	/**
	 * 发送随机数验证码
	 * 
	 * @param phone 手机号
	 * @param template 短信内容模版, 必须包含"{password}",否则发送失败
	 * @return
	 * @throws OaException
	 */
	public String sendRandomCode(String phone, String template) throws OaException, SmsException;
	
	/**
	 * 发送短信
	 * 
	 * @param phone 手机号
	 * @param content 短信内容
	 * @return
	 * @throws OaException
	 */
	public boolean sendMessage(String phone, String content) throws OaException, SmsException;

}
