package com.ule.oa.common.spring;

/**
 * spring容器完成加载后，执行init方法
 * @author zhangwei
 * @date 2015年12月3日下午4:14:58
 * @version 3.0.0
 */
public interface ApplicationInitBean {

	/**
	 * 此方法在spring的ApplicationListener接口的onApplicationEvent被调用
	 * @author zhangwei
	 * @date 2015年12月3日下午4:15:38
	 * @version 3.0.0
	 */
	public void init();
	
}
