package com.ule.oa.admin.util;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ule.oa.common.cache.CacheInitBean;
import com.ule.oa.common.spring.ApplicationInitBean;

@Component
public class CacheInitListener implements ApplicationInitBean {
	
	private Logger logger = LoggerFactory.getLogger(CacheInitListener.class);

	@Value("${cache.init}")
	private boolean required;
	
	@Autowired
	private List<CacheInitBean> cacheInitBeanList;
	
	/**
	 * 系统启动时初始化缓存
	 */
	@Override
	public void init() {
		if (!required) {
			return;
		}
		
		logger.info("初始化系统缓存开始");
		
		for (CacheInitBean bean : cacheInitBeanList) {
			bean.init();
		}
		
		logger.info("初始化系统缓存结束");
	}

}
