package com.ule.oa.admin.session;


/**
 * session上下文
 * @author zhangwei@tomstaff.com
 * @date 2014年12月12日下午3:37:24
 */
public class SessionContext {
	
	//将session保存在线程
	static ThreadLocal<SessionContext> sessionContext = new ThreadLocal<SessionContext>();
	
	private String sessionId;
	
	private HttpSessionCacheWrapper session;
	
	public SessionContext(String sessionId, HttpSessionCacheWrapper session) {
	    this.sessionId = sessionId;
	    this.session = session;
	}
	
	public static void setContext(SessionContext context) {
		sessionContext.set(context);
	}
	
	public static SessionContext getContext() {
		return sessionContext.get();
	}
	
	public static void remove() {
	    sessionContext.remove();
	}
	
	public HttpSessionCacheWrapper getSession() {
	    if (null != session) {
	        session.access();
	    }
	    return session;
	}
	
	public void setSession(HttpSessionCacheWrapper session) {
		this.session = session;
	}
	
	public String getSessionId() {
		return sessionId;
	}
	
}
