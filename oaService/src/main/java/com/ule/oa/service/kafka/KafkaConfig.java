package com.ule.oa.service.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ule.oa.common.spring.ApplicationInitBean;
import com.ule.tools.client.kafka.consumer.Consumer;
import com.ule.tools.client.kafka.core.config.KafkaClientsConfig;

@Component
public class KafkaConfig implements ApplicationInitBean {
	
	private Logger logger = LoggerFactory.getLogger(KafkaConfig.class);

	public void init() { 
		//获取默认路径下的kafka-clients-cfg.xml    
		KafkaClientsConfig config = KafkaClientsConfig.DEFAULT_INSTANCE;
		//参数为配置文件中Consumer的id内容
		
		try {
			Consumer consumer = config.newConsumer("com.ule.oa.service.attnTaskRecordConsumer");
			//参数为启动时间,单位为Seconds,如果<0，则一直开下去，直到结束
			consumer.startReceive();
		} catch (Exception e) {
			logger.error("error on start kafka com.ule.oa.service.attnTaskRecordConsumer", e);
		}
		
		try {
			Consumer consumer = config.newConsumer("com.ule.oa.service.signRecordToAttnWorkConsumer");
			//参数为启动时间,单位为Seconds,如果<0，则一直开下去，直到结束
			consumer.startReceive();
		} catch (Exception e) {
			logger.error("error on start kafka com.ule.oa.service.signRecordToAttnWorkConsumer", e);
		}
		
		try {
			Consumer consumer = config.newConsumer("com.ule.oa.service.attnWorkToAttnStatisConsumer");
			//参数为启动时间,单位为Seconds,如果<0，则一直开下去，直到结束
			consumer.startReceive();
		} catch (Exception e) {
			logger.error("error on start kafka com.ule.oa.service.attnWorkToAttnStatisConsumer", e);
		}
	}
	
}
