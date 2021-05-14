package com.ule.oa.common.utils;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.ule.oa.common.spring.ApplicationInitBean;


@Component
public class ApplicationListener implements org.springframework.context.ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private List<ApplicationInitBean> initBeanList;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		
		//排除spring mvc的servlet子上下文
		if (null != event.getApplicationContext().getParent()) {
			return;
		}
		
		for (ApplicationInitBean bean : initBeanList) {
			bean.init();
		}
	}

}
