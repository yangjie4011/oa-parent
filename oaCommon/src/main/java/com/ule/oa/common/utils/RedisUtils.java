package com.ule.oa.common.utils;

import com.ule.cachecloud.utils.CacheCloudClientUtil;

/**
  * @ClassName: RedisUtils
  * @Description: 针对redis 增删改查操作
  * @author minsheng
  * @date 2017年9月13日 下午6:04:30
 */
public class RedisUtils {
	public final static String PREFIX = "OA_";
	
	public static String getKey(String key){
		return PREFIX + key;
	}
	
	/**
	 * @throws Exception 
	  * setString(存储)
	  * @Title: put
	  * @Description: 存储字符类型数据-永久有效
	  * @param key
	  * @param value
	  * @param time    失效时间
	  * void    返回类型
	  * @throws
	 */
	public static void setString(String key,String value) throws Exception{
		CacheCloudClientUtil.set(getKey(key), value);
	}
	
	/**
	 * @throws Exception 
	  * setString(存储)
	  * @Title: put
	  * @Description: 存储字符类型数据-需要设置有效期
	  * @param key
	  * @param value
	  * @param time    失效时间
	  * void    返回类型
	  * @throws
	 */
	public static void setString(String key,String value,int seconds) throws Exception{
		CacheCloudClientUtil.set(getKey(key), value, seconds);
	}
	
	/**
	  * setBytes(这里用一句话描述这个方法的作用)
	  * @Title: setBytes
	  * @Description: 存储字节类型数据
	  * @param key
	  * @param bytes
	  * @param seconds
	  * @throws Exception    设定文件
	  * void    返回类型
	  * @throws
	 */
	public static void setBytes(String key,byte[] bytes,int seconds) throws Exception{
		CacheCloudClientUtil.set(getKey(key), bytes, seconds);
	}
	
	/**
	 * @throws Exception 
	  * put(删除)
	  * @Title: put
	  * @Description: TODO
	  * @param key
	  * void    返回类型
	  * @throws
	 */
	public static void delete(String key) throws Exception{
		CacheCloudClientUtil.delete(getKey(key));
	}
	
	/**
	 * @throws Exception 
	  * getString(查询)
	  * @Title: getString
	  * @Description: TODO
	  * @param key
	  * @param value
	  * @param time    超时时间
	  * void    返回类型
	  * @throws
	 */
	public static String getString(String key) throws Exception{
		return CacheCloudClientUtil.getString(getKey(key));
	}
	
	/**
	  * getBytes(获取字节类型数据)
	  * @Title: getBytes
	  * @Description: 获取字节类型数据
	  * @param key
	  * @return
	  * @throws Exception    设定文件
	  * byte[]    返回类型
	  * @throws
	 */
	public static byte[] getBytes(String key) throws Exception{
		return CacheCloudClientUtil.getObjectBytes(getKey(key));
	}
}
