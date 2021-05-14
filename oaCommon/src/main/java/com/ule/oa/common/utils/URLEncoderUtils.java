package com.ule.oa.common.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
  * @ClassName: URLEncoderUtils
  * @Description: 编码和结果工具类
  * @author minsheng
  * @date 2017年9月18日 下午4:22:25
 */
public class URLEncoderUtils {
	private static Logger logger = LoggerFactory.getLogger(URLEncoderUtils.class);
	
	/**
	  * encode(对字符串进行编码)
	  * @Title: encode
	  * @Description: 对字符串进行编码
	  * @param str
	  * @return    设定文件
	  * String    返回类型
	  * @throws
	 */
	public static String encodeStr(String str){
    	try {
    		if(StringUtils.isBlank(str)){
    			str = "";
    		}
			return URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error(str + "转码报错,错误原因=" + e.getMessage());
		}
    	
    	return "";
    }
	
	/**
	  * decodeStr(对字符串进行解码)
	  * @Title: decodeStr
	  * @Description: 对字符串进行解码
	  * @param str
	  * @return    设定文件
	  * String    返回类型
	  * @throws
	 */
	public static String decodeStr(String str){
		try {
    		if(StringUtils.isBlank(str)){
    			str = "";
    		}
			return URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error(str + "转码报错,错误原因=" + e.getMessage());
		}
    	
    	return "";
	}
}
