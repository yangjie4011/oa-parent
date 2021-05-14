package com.ule.oa.web.session.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ule.oa.common.spring.SpringContextUtils;
import com.ule.oa.common.utils.SessionUtils;
import com.ule.oa.common.utils.http.HttpsRedirectServletResponseWrapper;
import com.ule.oa.web.session.HttpServletRequestWrapper;
import com.ule.oa.web.session.HttpSessionCacheManager;
import com.ule.oa.web.session.HttpSessionCacheWrapper;
import com.ule.oa.web.session.SessionContext;

public class HttpSessionWrapperFilter implements Filter {

	//sessionId,即cookie的key
	private String sessionId;

	private String cookieDomain;

	private String cookiePath;
	
	private String ignores;
	
	/**
	 * session缓存管理对象
	 */
	private HttpSessionCacheManager sessionCacheManager;
	
	public void doFilter(ServletRequest servletRequest,
			ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		
		if (isIgnore(request)) {
		    filterChain.doFilter(request, new HttpsRedirectServletResponseWrapper(request, response));
		} else {
		    //sessionId值
		    String sid = SessionUtils.getSessionId(request);
		    
		    //生成sessionId值
		    if (sid == null) {
		        sid = SessionUtils.generateSid();
		        addCookie(response, sid);
		    }
		    
		    //获取缓存session
		    HttpSessionCacheWrapper session = sessionCacheManager.get(sid);
		    
		    //设置到线程中
		    SessionContext.setContext(new SessionContext(sid, session));
		    
		    filterChain.doFilter(new HttpServletRequestWrapper(request),
		            new HttpsRedirectServletResponseWrapper(request, response));
		    
		    //更新到缓存
		    sessionCacheManager.put(sid, SessionContext.getContext().getSession());
		}
	}

    private void addCookie(HttpServletResponse response, String sid) {
        Cookie cookie = new Cookie(sessionId, sid);
        cookie.setMaxAge(-1);
        if (cookieDomain != null && cookieDomain.length() > 0) {
        	cookie.setDomain(cookieDomain);
        }
        cookie.setPath(cookiePath);
        response.addCookie(cookie);
    }

    private boolean isIgnore(HttpServletRequest request) {
        return request.getRequestURI().startsWith(request.getContextPath() + ignores);
    }

	public void init(FilterConfig filterConfig) throws ServletException {
	    ignores = filterConfig.getInitParameter("ignores");
	    sessionId = filterConfig.getInitParameter("sessionId");
		cookieDomain = filterConfig.getInitParameter("cookieDomain");
		cookiePath = filterConfig.getInitParameter("cookiePath");
		if (null == cookiePath || cookiePath.length() == 0) {
			cookiePath = filterConfig.getServletContext().getContextPath();
		}
		
		sessionCacheManager = SpringContextUtils.getContext().getBean(HttpSessionCacheManager.class);
	}

	@Override
	public void destroy() {
		sessionCacheManager = null;
	}
	
}
