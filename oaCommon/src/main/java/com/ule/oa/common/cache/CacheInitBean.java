package com.ule.oa.common.cache;

/**
 * 缓存初始化bean
 * 系统启动时调用init方法初始化
 */
public interface CacheInitBean {

	/**
	 * 初始化方法
	 */
	public void init();
	
}
