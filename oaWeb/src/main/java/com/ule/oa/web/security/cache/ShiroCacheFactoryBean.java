package com.ule.oa.web.security.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.ule.cachecloud.utils.CacheCloudClientUtil;
import com.ule.cachecloud.utils.SerializeHelper;
import com.ule.oa.common.cache.CacheHelper;
import com.ule.oa.common.cache.redis.JedisUtils;

/**
 * Shiro缓存工厂类，用Redis实现符合Shiro格式的缓存
 * @author zhangwei002
 *
 * @param <String>
 * @param <V>
 */
public abstract class ShiroCacheFactoryBean<V> implements FactoryBean<Cache<String, V>>, InitializingBean {
    
    private Logger logger = LoggerFactory.getLogger(ShiroCacheFactoryBean.class);
    
    private Cache<String, V> cache;
    
    /**
     * 超时时间，单位秒
     * @return
     */
    protected abstract int getExpireSeconds();
    
    /**
     * 前辍，注意：前辍请不要以'_'结束
     * @return
     */
    protected abstract String getKeyPrefix();
    
    /**
     * 环境_前辍_业务key
     * @param key
     * @return
     */
    private String getKey(String key) {
        return CacheHelper.getKey(getKeyPrefix() + "_" + key);
    }
    
    /**
     * 从redis的key中获取业务key
     * @param key
     * @return
     */
    private String getKeyFromRedisKey(String key) {
        int lastIdx = key.lastIndexOf("_");
        return key.substring(lastIdx + 1);
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        this.cache = new Cache<String, V>() {
            @Override
            @SuppressWarnings("unchecked")
            public V get(String key) throws CacheException {
                try {
                    byte[] bytes = CacheCloudClientUtil.getObjectBytes(getKey(key));
                    if (null == bytes) {
                        return null;
                    }
                    return (V) SerializeHelper.unserialize(bytes);
                } catch (Exception e) {
                    logger.error("error on get cache, key = [{}]", key);
                    throw new CacheException();
                }
            }

            @Override
            public V put(String key, V value) throws CacheException {
                try {
                    V previous = get(key);
                    byte[] bytes = SerializeHelper.serialize(value);
                    CacheCloudClientUtil.set(getKey(key), bytes, getExpireSeconds());
                    return previous;
                } catch (Exception e) {
                    logger.error("error on put cache, key = [{}]", key);
                    throw new CacheException();
                }
            }

            @Override
            public V remove(String key) throws CacheException {
                try {
                    V previous = get(key);
                    CacheCloudClientUtil.delete(getKey(key));
                    return previous;
                } catch (Exception e) {
                    logger.error("error on remove cache, key = [{}]", key);
                    throw new CacheException();
                }
            }

            @Override
            public int size() {
                try {
                    return JedisUtils.keysMatch(CacheHelper.getKey(getKeyPrefix()) + "*").size();
                } catch (Exception e) {
                    logger.error("error on size cache");
                    throw new CacheException();
                }
            }
            
            @Override
            public Set<String> keys() {
                try {
                    Set<String> keys = JedisUtils.keysMatch(CacheHelper.getKey(getKeyPrefix()) + "*");
                    if (!CollectionUtils.isEmpty(keys)) {
                        Set<String> ks = new HashSet<String>();
                        for (String key : keys) {
                            ks.add(getKeyFromRedisKey(key));
                        }
                        return Collections.unmodifiableSet(new LinkedHashSet<String>(ks));
                    }
                    return Collections.emptySet();
                } catch (Exception e) {
                    logger.error("error on keys cache");
                    throw new CacheException();
                }
            }

            @Override
            public Collection<V> values() {
                try {
                    Set<String> keys = JedisUtils.keysMatch(CacheHelper.getKey(getKeyPrefix()) + "*");
                    if (keys.isEmpty()) {
                        return Collections.emptyList();
                    }
                    List<V> values = new ArrayList<V>();
                    for (String key : keys) {
                        values.add(get(getKeyFromRedisKey(key)));
                    }
                    return values;
                } catch (Exception e) {
                    logger.error("error on values cache");
                    throw new CacheException();
                }
            }
            
            @Override
            public void clear() throws CacheException {
                try {
                    JedisUtils.delMatch(CacheHelper.getKey(getKeyPrefix()) + "*");
                } catch (Exception e) {
                    logger.error("error on clear cache");
                    throw new CacheException();
                }
            }
        };
    }

    @Override
    public Cache<String, V> getObject() throws Exception {
        return this.cache;
    }

    @Override
    public Class<?> getObjectType() {
        return Cache.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
