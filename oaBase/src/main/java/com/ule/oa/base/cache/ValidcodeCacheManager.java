package com.ule.oa.base.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ule.oa.common.cache.CacheHelper;
import com.ule.oa.common.cache.CacheKeyConstants;
import com.ule.oa.common.utils.RedisUtils;

/**
 * 验证码缓存
 * 
 * @author 樊航
 */
@Component
public class ValidcodeCacheManager {
	
	private Logger logger = LoggerFactory.getLogger(ValidcodeCacheManager.class);
	
	/*@Value("${http.session.timeout}")
	private Integer timeout;
*/

	public String getKey(String phone) {
		return CacheHelper.getKey(CacheKeyConstants.OA_VALICODE) + phone;
	}

	public String get(String phone) {
		String valiCode = null;
		try {
			valiCode = RedisUtils.getString(getKey(phone));
		} catch (Exception e) {
			logger.error("从redis读取验证码信息失败", e);
		}
		return valiCode;
	}
	
	/**
	 * 保存验证码到缓存(默认有效时间30分钟)
	 * 
	 * @param phone 手机号
	 * @param validcode 验证码
	 */
	public boolean put(String phone, String code) {
		try {
			RedisUtils.setString(getKey(phone), code, 30*60);
			return true;
		} catch (Exception e) {
			logger.error("将验证码信息写入redis失败", e);
			return false;
		}
	}
	
	/**
	 * 保存验证码到缓存
	 * 
	 * @param phone 手机号
	 * @param validcode 验证码
	 * @param seconds 超时时间,秒
	 */
	public boolean put(String phone, String code, Integer seconds) {
		try {
			RedisUtils.setString(getKey(phone), code, seconds);
			return true;
		} catch (Exception e) {
			logger.error("将验证码信息写入redis失败", e);
			return false;
		}
		 
	}
	
	public boolean delete(String phone) {
		 try {
			 RedisUtils.delete(getKey(phone));
			return true;
		} catch (Exception e) {
			logger.error("将验证码信息从redis删除失败", e);
			return false;
		}
	}
}
