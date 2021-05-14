package com.ule.oa.common.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

/**
  * @ClassName: LogListener
  * @Description: 日志监听器
  * @author minsheng
  * @date 2018年1月8日 上午10:56:44
 */
@Service
public class LogListener implements ApplicationListener<ApplicationEvent>{
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if(event instanceof LogEvent){
			LogEvent logEvent = (LogEvent)event;
			try {
				logEvent.writeLog();
			} catch (Exception e) {
				logger.error("写日志失败=" + e.getMessage());
			}
		}
	}
}
