package com.ule.oa.common.utils;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

import com.ule.oa.common.exception.OaException;

/**
 * 数据验证工具包
 * <hr>
 * 若参数不满足条件,则抛出异常:new OaException(message)<br>
 * <hr>
 * spring-core/src/main/java/org/springframework/util/Assert.java
 * 
 * @author 樊航
 */
public final class OaAssert {

	private OaAssert() {
	}

	/**
	 * 若表达式为true,则抛出异常
	 * 
	 * @param expression
	 *            表达式
	 * @param message
	 *            异常message
	 * @throws OaException
	 *             new OaException(message)
	 */
	public static void isTrue(boolean expression, String message)
			throws OaException {
		if (!expression) {
			throw new OaException(message);
		}
	}
	
	/**
	 * @param expression
	 * @param message
	 * @param errorVles 错误信息
	 */
	public static void isTrue(boolean expression, String message, String[] errorVles)
			throws OaException {
		if (!expression) {
			throw new OaException(message,errorVles);
		}
	}

	/**
	 * 若参数不为null,则抛出异常<br>
	 * object != null
	 * 
	 * @param object
	 *            待验证的参数
	 * @param message
	 *            抛出异常的信息
	 * @throws OaException
	 *             new OaException(message)
	 */
	public static void isNull(Object object, String message)
			throws OaException {
		if (object != null) {
			throw new OaException(message);
		}
	}

	/**
	 * 若参数为null,则抛出异常<br>
	 * object != null
	 * 
	 * @param object
	 *            待验证的参数
	 * @param message
	 *            抛出异常的信息
	 * @throws OaException
	 *             new OaException(message)
	 */
	public static void notNull(Object object, String message)
			throws OaException {
		if (object == null) {
			throw new OaException(message);
		}
	}
	
	public static void notNull(Object object, String message, String[] errorValues)
			throws OaException {
		if (object == null) {
			throw new OaException(message,errorValues);
		}
	}

	/**
	 * 若参数不为null或空字符,则抛出异常<br>
	 * !StringUtils.isEmpty(text)
	 * 
	 * @param text
	 *            待验证的参数
	 * @param message
	 *            抛出异常的信息
	 * @throws OaException
	 *             new OaException(message)
	 * @see org.apache.commons.lang3.StringUtils#isEmpty(CharSequence)
	 */
	public static void isEmpty(String text, String message) throws OaException {
		if (!StringUtils.isEmpty(text)) {
			throw new OaException(message);
		}
	}

	/**
	 * 若参数为null或空字符,则抛出异常<br>
	 * StringUtils.isEmpty(text)
	 * 
	 * @param text
	 *            待验证的参数
	 * @param message
	 *            抛出异常的信息
	 * @throws OaException
	 *             new OaException(message)
	 * @see org.apache.commons.lang3.StringUtils#isEmpty(CharSequence)
	 */
	public static void notEmpty(String text, String message)
			throws OaException {
		if (StringUtils.isEmpty(text)) {
			throw new OaException(message);
		}
	}

	/**
	 * 若参数不为null或空字符(清空两端空格),则抛出异常<br>
	 * StringUtils.isBlank(text)
	 * 
	 * @param text
	 *            待验证的参数
	 * @param message
	 *            抛出异常的信息
	 * @throws OaException
	 *             new OaException(message)
	 * @see org.apache.commons.lang3.StringUtils#isBlank(CharSequence)
	 */
	public static void isBlank(String text, String message) throws OaException {
		if (!StringUtils.isBlank(text)) {
			throw new OaException(message);
		}
	}

	/**
	 * 若参数为null或空字符(清空两端空格),则抛出异常<br>
	 * !StringUtils.isBlank(text)
	 * 
	 * @param text
	 *            待验证的参数
	 * @param message
	 *            抛出异常的信息
	 * @throws OaException
	 *             new OaException(message)
	 * @see org.apache.commons.lang3.StringUtils#isBlank(CharSequence)
	 */
	public static void notBlank(String text, String message)
			throws OaException {
		if (StringUtils.isBlank(text)) {
			throw new OaException(message);
		}
	}
	
	/**
	 * 
	 * @param text
	 * @param message
	 * @param errorVles 错误值
	 */
	public static void notBlank(String text, String message, String[] errorVles)
			throws OaException {
		if (StringUtils.isBlank(text)) {
			throw new OaException(message,errorVles);
		}
	}

	/**
	 * 若参数不是数字,则抛出异常<br>
	 * !StringUtils.isNumeric(text)
	 * 
	 * @param text
	 *            待验证字符串
	 * @param message
	 *            抛出异常信息
	 * @throws OaException
	 *             new OaException(message)
	 * @see org.apache.commons.lang3.StringUtils#isNumeric(CharSequence)
	 */
	public static void isNumeric(String text, String message)
			throws OaException {
		if (!StringUtils.isNumeric(text)) {
			throw new OaException(message);
		}
	}
	
	public static void isNumeric(String text, String message,String[] errorValues)
			throws OaException {
		if (!StringUtils.isNumeric(text)) {
			throw new OaException(message,errorValues);
		}
	}
	
	/**
	 * 若参数不是数字和""，null,则抛出异常<br>
	 * !StringUtils.isNumeric(text)
	 * 
	 * @param text
	 *            待验证字符串
	 * @param message
	 *            抛出异常信息
	 * @throws OaException
	 *             new OaException(message)
	 * @see org.apache.commons.lang3.StringUtils#isNumeric(CharSequence)
	 */
	public static void isNumericOrBlank(String text, String message)
			throws OaException {
		if(StringUtils.isNotBlank(text)){
			if (!StringUtils.isNumeric(text)) {
				throw new OaException(message);
			}
		}
	}
	
	public static void isNumericOrBlank(String text, String message,String[] errorValues)
			throws OaException {
		if(StringUtils.isNotBlank(text)){
			if (!StringUtils.isNumeric(text)) {
				throw new OaException(message,errorValues);
			}
		}
	}

	/**
	 * 若集合参数为null或isEmpty,则抛出异常
	 * 
	 * @param collection
	 *            待验证的集合参数
	 * @param message
	 *            抛出异常的信息
	 * @throws OaException
	 *             new OaException(message)
	 */
	public static void notEmpty(Collection<?> collection, String message)
			throws OaException {
		if (collection == null || collection.isEmpty()) {
			throw new OaException(message);
		}
	}
	
	/**
	 * @param errorValues 异常value
	 */
	public static void notEmpty(Collection<?> collection, String message,String[] errorValues)
			throws OaException {
		if (collection == null || collection.isEmpty()) {
			throw new OaException(message, errorValues);
		}
	}
	
	/**
	 * 断言集合为空
	 * @param collection 集合对象
	 * @param message 异常信息
	 * @throws OaException
	 */
	public static void empty(Collection<?> collection, String message)
			throws OaException {
		if (null != collection && !collection.isEmpty()) {
			throw new OaException(message);
		}
	}

	/**
	 * 若数组参数为null或length为0,则抛出异常
	 * 
	 * @param array
	 *            待验证的数组参数
	 * @param message
	 *            抛出异常的信息
	 * @throws OaException
	 *             new OaException(message)
	 */
	public static void notEmpty(Object[] array, String message)
			throws OaException {
		if (array == null || array.length==0) {
			throw new OaException(message);
		}
	}

}