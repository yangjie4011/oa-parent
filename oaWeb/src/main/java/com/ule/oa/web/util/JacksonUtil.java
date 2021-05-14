package com.ule.oa.web.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ule.oa.common.exception.OaException;

public class JacksonUtil {
	private static Logger logger = LoggerFactory.getLogger(JacksonUtil.class);

	public static String serialize(Object o) {
		ObjectMapper mapper = new ObjectMapper();
		String json = null;
		try {
			json = mapper.writeValueAsString(o);
		} catch (JsonProcessingException e) {
			logger.info("转换错误", e);
		}
		return json;
	};

	public static <T> T parse(String response, Class<T> responseClass) throws OaException {
		ObjectMapper objectMapper = new ObjectMapper();
		T o = null;
		try {
			o = objectMapper.readValue(response, responseClass);
		} catch (Exception e) {
			logger.info("解析错误：", e);
			throw new OaException("解析错误");
		}
		return o;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object parseJsonToObj(String json,Class cls) throws OaException{
		ObjectMapper objectMapper = new ObjectMapper();
		try {			
			return objectMapper.readValue(json, cls);
		} catch (Exception e) {
			logger.error("解析response出错", e);
			throw new OaException("解析response出错" + e.getMessage());
		}
	}
}
