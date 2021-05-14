package com.ule.oa.common.cache;

import com.ule.oa.common.spring.config.CustomPropertyPlaceholderConfigurer;

public class CacheHelper {
	
	protected static final String CACHE_KEY_PREFIX = "OA3_KEY_";

	private static String env = (String) CustomPropertyPlaceholderConfigurer.getProperty("cache.env");
	
	/**
	 * 获取缓存的key
	 */
	public static String getKey(String keySuffix) {
		return env + "_" + keySuffix;
	}
	
	public static String getMemcachedKey(String keySuffix) {
		return env + "_" + CACHE_KEY_PREFIX + keySuffix;
	}
	
}
