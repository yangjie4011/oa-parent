package com.ule.oa.web.security.cache;

import org.apache.shiro.authz.AuthorizationInfo;

import com.ule.oa.common.cache.CacheKeyConstants;

/**
 * æéįžå­
 * 
 */
public class AuthorizationCache extends ShiroCacheFactoryBean<AuthorizationInfo> {

    @Override
    protected int getExpireSeconds() {
        return 2 * 60 * 60;
    }

    @Override
    protected String getKeyPrefix() {
        return CacheKeyConstants.SHIRO_AUTHR_USER;
    }
}
