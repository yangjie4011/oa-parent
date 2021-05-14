package com.ule.oa.admin.security;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.session.SessionException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ule.oa.base.po.User;
import com.ule.oa.common.utils.http.IPUtils;

/**
  * @ClassName: SystemLogoutFilter
  * @Description: 系统退出后需要清空session
  * @author minsheng
  * @date 2017年6月21日 下午2:16:38
 */
@Service
public class SystemLogoutFilter extends LogoutFilter {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        //在这里执行退出系统前需要清空的数据
    	Subject subject = getSubject(request, response);
        String redirectUrl = getRedirectUrl(request, response, subject);

        try {
        	User user = (User) subject.getPrincipal();
        	
            subject.logout();
            
            if(null != user){
            	logger.info("用户={},ip={}退出系统",user.getUserName(),IPUtils.getIpAddress((HttpServletRequest)request));
            }
        } catch (SessionException ise) {
        	logger.error("退出系统失败", ise);
        }

        issueRedirect(request, response, redirectUrl);
        //返回false表示不执行后续的过滤器，直接返回跳转到登录页面

        return false;

    }
}

