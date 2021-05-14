package com.ule.oa.common.cache.distlock;

import org.springframework.util.Assert;

import redis.clients.jedis.Jedis;

import com.ule.cachecloud.config.JedisConfig;
import com.ule.cachecloud.utils.CacheCloudClientUtil;
import com.ule.oa.common.utils.UUIDGenerator;

public class DistLockUtil {

	private static final String KEY_DIST_LOCK = "DIST_LOCK";
	
	private static final Long EXPIRE_THIRTY_DAYS = 30 * 24 * 60 * 60L;

//	/**
//	 * 获取锁，枚举参数
//	 * @param lock
//	 * @return
//	 */
//	public static String lock(DistLock lock) {
//		return lock(lock.getCode(), lock.getExpire());
//	}
	
	/**
	 * 无失效时间
	 * @param code
	 * @return
	 */
	public static String lock(String code) {
		return lock(code, EXPIRE_THIRTY_DAYS);
	}
	
	/**
	 * 获取锁
	 * @param code
	 * @param expire 失效时间，单位秒
	 * @return
	 */
	public static String lock(String code, Long expire) {
		Jedis jedis = null;
		try {
			jedis = CacheCloudClientUtil.getJedis();
			String key = getKey(code);
			String value = getValue();
			if (null != expire) {
				String replay = jedis.set(key, value, "NX", "EX", expire);
				if (null == replay) {
					return null;
				} else if ("OK".equals(replay)) {
					return value;
				}
			}
			else {
				Long replay = jedis.setnx(key, value);
				if (replay == 1) {
					return value;
				} else {
					return null;
				}
			}
			return value;
		} catch (Exception e) {
			throw new RuntimeException("error set dist lock into redis, cause : ", e);
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
	}
	
	/**
	 * 解锁
	 * @param code
	 * @param value
	 * @return true-成功，false-失败
	 */
	public static boolean unLock(String code, String value) {
		Assert.notNull(value);
		Jedis jedis = null;
		try {
			jedis = CacheCloudClientUtil.getJedis();
			String key = getKey(code);
			if (value.equals(jedis.get(key))) {
				jedis.del(key);
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			throw new RuntimeException("error unLock dist lock into redis, cause : ", e);
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
	}
	
	/**
	 * 解锁
	 * @param code
	 * @return
	 */
	public static void unLock(String code) {
		Jedis jedis = null;
		try {
			jedis = CacheCloudClientUtil.getJedis();
			String key = getKey(code);
			jedis.del(key);
		} catch (Exception e) {
			throw new RuntimeException("error unLock dist lock into redis, cause : ", e);
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
	}
	
	private static String getKey(String code) {
		return JedisConfig.getPreWord() + KEY_DIST_LOCK + "_" + code;
	}
	
	private static String getValue() {
		return UUIDGenerator.generate();
	}

}
