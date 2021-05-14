package com.ule.oa.admin.security;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;

import com.ule.oa.base.po.User;
import com.ule.oa.common.utils.JsonWriter;
import com.ule.oa.common.utils.http.HttpWriter;
import com.ule.oa.common.utils.json.JSONUtils;

public class LoginFormAuthenticationFilter extends FormAuthenticationFilter {
    @Autowired
    private JdbcRealm jdbcRealm;

    /**
     * 登录成功之后
     */
    @Override
    protected boolean onLoginSuccess(AuthenticationToken token,
            Subject subject, ServletRequest request, ServletResponse response) throws Exception {
        // 清除之前的权限
        jdbcRealm.clearCachedAuthorizationInfo();
        jdbcRealm.clearCachedAuthenticationInfo();
        // 返回用户信息
        User user = (User) subject.getPrincipal();
        HttpWriter.writerJson((HttpServletResponse) response, JSONUtils.write(JsonWriter.successfulResult(user)));
        
        return false;
    }

}
