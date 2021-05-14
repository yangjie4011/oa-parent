package com.ule.oa.common.cache;

/**
 * 缓存刷新bean，定时调用刷新
 */
public interface CacheRefreshBean {

	/**
	 * 刷新方法
	 */
	public void refresh();
	
}
