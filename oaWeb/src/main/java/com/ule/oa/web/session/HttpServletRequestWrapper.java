package com.ule.oa.web.session;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class HttpServletRequestWrapper extends
		javax.servlet.http.HttpServletRequestWrapper {
	
	public HttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
	}

	public HttpSession getSession(boolean create) {
	    HttpSessionCacheWrapper cacheSession = SessionContext.getContext().getSession();
		if (null == cacheSession && create) {
		    Map<String, Object> sessionMap = new HashMap<String, Object>();
		    cacheSession = new HttpSessionCacheWrapper(super.getSession(), sessionMap);
			SessionContext.getContext().setSession(cacheSession);
		}
		return cacheSession;
	}

	public HttpSession getSession() {
		return getSession(true);
	}

}
