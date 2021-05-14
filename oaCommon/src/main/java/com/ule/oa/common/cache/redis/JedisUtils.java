package com.ule.oa.common.cache.redis;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;

import com.ule.cachecloud.config.JedisConfig;
import com.ule.cachecloud.utils.CacheCloudClientUtil;

/**
 * Redis有返回值的工具类
 * @author zhangwei002
 *
 */
public class JedisUtils {
    
    private static Logger logger = LoggerFactory.getLogger(JedisUtils.class);
	
    /**
     * 删除
     * @param key
     * @return
     * @throws Exception
     */
	public static Long del(String key) throws Exception {
	    Jedis jedis = null;
		try {
			jedis = CacheCloudClientUtil.getJedis();
			return jedis.del(JedisConfig.getPreWord() + key);
		} finally {
			CacheCloudClientUtil.closeJedis(jedis);
		}
	}
	
	/**
	 * 根据正则key删除
	 * @param pattern
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public static Long delMatch(String pattern) throws Exception {
	    Jedis jedis = null;
	    try {
	        jedis = CacheCloudClientUtil.getJedis();
	        Set<String> keys = jedis.keys(JedisConfig.getPreWord() + pattern);
	        long count = 0;
	        if (null != keys && keys.size() > 0) {
	            logger.info("缓存{}共有{}条", pattern, keys.size());
	            for (String key : keys) {
	                Long replay = jedis.del(key);
	                count += replay;
	            }
	            if (count % 1000 == 0) {
	                logger.info("删除缓存{},replay={}", pattern, count);
	            }
	        }
	        return count;
	    } finally {
	        CacheCloudClientUtil.closeJedis(jedis);
	    }
	}
	
	/**
	 * 获取匹配的key集合
	 * @param pattern
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public static Set<String> keysMatch(String pattern) throws Exception {
        Jedis jedis = null;
        try {
            jedis = CacheCloudClientUtil.getJedis();
            return jedis.keys(JedisConfig.getPreWord() + pattern);
        } finally {
            CacheCloudClientUtil.closeJedis(jedis);
        }
    }
	
	public static long incr(String key) throws Exception {
	    Jedis jedis = null;
        try {
            jedis = CacheCloudClientUtil.getJedis();
            return jedis.incr(JedisConfig.getPreWord() + key);
        } finally {
            CacheCloudClientUtil.closeJedis(jedis);
        }
	}
	
}
