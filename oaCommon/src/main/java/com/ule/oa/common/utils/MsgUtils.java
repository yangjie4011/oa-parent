package com.ule.oa.common.utils;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ule.tools.client.kafka.core.config.KafkaClientsConfig;
import com.ule.tools.client.kafka.producer.Producer;
import com.ule.oa.common.utils.json.JSONUtils;

/**
 * @description 消息工具类
 * @author minsheng
 * @date 2017-04-18 16:44:05
 */
public class MsgUtils {
	private static Logger logger = LoggerFactory.getLogger(MsgUtils.class);
	
	/**
	 * @description 逐单异步发送kafka消息
	 * @param paraMap 参数
	 * @param id	消息id
	 */
	public static void asynSendKafkaMsgByMap(final Map<String,Object> paraMap,final String id){
		new Thread(new Runnable() {
			public void run() {
				try{
					String orderJson = JSONUtils.write(paraMap);
					logger.info("异步发送消息，消息体={}",orderJson);
					
					sendMsg(orderJson, id);
					
				}catch(Exception e){
					logger.info("异步发送消息失败，失败原因={}",e.getMessage());
				}
			}
		}).start();
	}
	
	/**
	 * @description 逐单同步发送kafka消息
	 * @param paraMap 参数
	 * @param id	消息id
	 */
	public static void syncSendKafkaMsgByMap(final Map<String,Object> paraMap,String id) throws Exception{
		try{
			String orderJson = JSONUtils.write(paraMap);
			logger.info("同步发送消息，消息体={}",orderJson);
			
			sendMsg(orderJson, id);
			
		}catch(Exception e){
			logger.info("同步发送消息失败，失败原因={}",e.getMessage());
			throw e;
		}
	}
	
	/**
	 * @description 批量异步发送kafka消息
	 * @param dispatchList 参数
	 * @param id	消息id
	 */
	public static void asynSendKafkaMsgByListMap(final List<Map<String, Object>> dispatchList,final String id){
		new Thread(new Runnable() {
			public void run() {
				if(null != dispatchList && dispatchList.size()>0){
					try{
						String orderJson = JSONUtils.write(dispatchList);
						logger.info("异步发送消息，消息体={}",orderJson);
						
						sendMsg(orderJson, id);
						
					}catch(Exception e){
						logger.info("异步发送消息失败，失败原因={}",e.getMessage());
					}
				}
			}
		}).start();
	}
	
	/**
	 * @description 批量同步发送kafka消息
	 * @param dispatchList 参数
	 * @param id	消息id
	 */
	public static void syncSendKafkaMsgByListMap(final List<Map<String, Object>> dispatchList,final String id) throws Exception{
		if(null != dispatchList && dispatchList.size()>0){
			try{
				String orderJson = JSONUtils.write(dispatchList);
				logger.info("同步发送消息，消息体={}",orderJson);
				
				sendMsg(orderJson, id);
				
			}catch(Exception e){
				logger.info("同步发送消息失败，失败原因={}",e.getMessage());
				throw e;
			}
		}
	}
	
	/**
	 * @description 发送消息
	 * @param data 数据
	 * @param id   消息id
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static void sendMsg(String data,String id) throws Exception{
		try{
			KafkaClientsConfig config = KafkaClientsConfig.DEFAULT_INSTANCE;
			Producer<String> producer = (Producer<String>)config.getOrCreateProducer(id); 
			producer.send("", data);
		}catch(Exception e){
			throw e;
		}
	}
}
