package com.ule.oa.admin.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ule.cachecloud.utils.CacheCloudClientUtil;
import com.ule.cachecloud.utils.SerializeHelper;
import com.ule.oa.common.cache.CacheHelper;
import com.ule.oa.common.cache.CacheKeyConstants;

@Component
public class HttpSessionCacheManager {
	
	private Logger logger = LoggerFactory.getLogger(HttpSessionCacheManager.class);

	@Value("${http.session.timeout}")
	private Integer timeout;
	
	public String getKey(String sid) {
		return CacheHelper.getKey(CacheKeyConstants.OA_HTTP_SESSION_V2) + sid;
	}
	
	public HttpSessionCacheWrapper get(String sid) {
		try {
			byte[] bytes = CacheCloudClientUtil.getObjectBytes(getKey(sid));
			if (null == bytes) {
				return null;
			}
			return (HttpSessionCacheWrapper) SerializeHelper.unserialize(bytes);
		} catch (Exception e) {
			logger.error("从redis读取session信息失败", e);
			return null;
		}
	}
	
	public boolean put(String sid, HttpSessionCacheWrapper session) {
		try {
			byte[] bytes = SerializeHelper.serialize(session);
			CacheCloudClientUtil.set(getKey(sid), bytes, timeout * 60);
			return true;
		} catch (Exception e) {
			logger.error("将session信息写入redis失败", e);
			return false;
		}
	}
	
	public boolean remove(String sid) {
		try {
			CacheCloudClientUtil.delete(getKey(sid));
			return true;
		} catch (Exception e) {
			logger.error("将session信息从redis删除失败", e);
			return false;
		}
	}
	
}
