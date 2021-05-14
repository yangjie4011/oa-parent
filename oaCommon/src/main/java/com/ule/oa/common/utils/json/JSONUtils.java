package com.ule.oa.common.utils.json;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @Description: JSON序列化
 * @author zhangwei@tomstaff.com
 */
public class JSONUtils {
	
	private static Logger logger = LoggerFactory.getLogger(JSONUtils.class);
	
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	private static ObjectMapper objectMapperNonNull = new ObjectMapper();
	
	static {
		objectMapperNonNull.setSerializationInclusion(Include.NON_NULL);
	}

	/**
	 * @Description: 将json字符串读为java对象
	 * @author zhangwei
	 */
	public static <T> T read(String content, Class<T> valueType) {
		try {
			return objectMapper.readValue(content, valueType);
		} catch (Exception e) {
			logger.error("error on read json [" + content + "]", e);
			throw new RuntimeException("error on read json", e);
		}
	}
	
	/**
	 * @Description: 将json字符串读为java对象,支持复杂泛型
	 * @author zhangwei
	 */
	public static <T> T read(String content, TypeReference<T> valueTypeRef) {
		try {
			return objectMapper.readValue(content, valueTypeRef);
		} catch (Exception e) {
			logger.error("error on read json [" + content + "]", e);
			throw new RuntimeException("error on read json", e);
		}
	}
	
	/**
	 * @Description: 将json字符串读为java对象集合
	 * @author zhangwei
	 */
	public static <T> List<T> readList(String content, Class<T> valueType) {
		try {
			JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, valueType);
			return objectMapper.readValue(content, javaType);
		} catch (Exception e) {
			logger.error("error on read json [" + content + "]", e);
			throw new RuntimeException("error on read json", e);
		}
	}
	
	/**
	 * @Description: 反序列化为Map
	 * @author zhangwei
	 * @date 2015年7月14日上午11:22:03
	 * @version 2.2.0
	 */
	public static Map<?,?> readAsMap(String content) {
		return read(content, Map.class);
	}
	
	/**
	 * @Description: 将java对象写为json字符串
	 * @author zhangwei
	 */
	public static String write(Object value) {
		try {
			return objectMapper.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			logger.error("error on write json", e);
			throw new RuntimeException("error on write json", e);
		}
	}
	
	public static String writeExcludeNull(Object value) {
		try {
			return objectMapperNonNull.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			logger.error("error on write json", e);
			throw new RuntimeException("error on write json", e);
		}
	}
	
}
