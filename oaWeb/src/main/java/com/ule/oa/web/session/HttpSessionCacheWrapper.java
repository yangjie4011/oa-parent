package com.ule.oa.web.session;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.security.web.savedrequest.Enumerator;

public class HttpSessionCacheWrapper extends HttpSessionWrapper implements Serializable {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Map<String, Object> attributeMap;
	
	private String sessionId;
	
	private long creationTime;
	
	private long lastAccessedTime;
	
	private boolean isNew = true;
	
	public HttpSessionCacheWrapper(){}
	
	public HttpSessionCacheWrapper(HttpSession session, Map<String, Object> sessionMap) {
		super(session);
		this.attributeMap = sessionMap;
		this.sessionId = SessionContext.getContext().getSessionId();
		this.creationTime = System.currentTimeMillis();
		this.lastAccessedTime = System.currentTimeMillis();
	}
	
	@Override
	public Object getAttribute(String key) {
		return attributeMap.get(key);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return new Enumerator<String>(attributeMap.keySet(), true);
	}

	@Override
	public void invalidate() {
//		SessionContext.remove();
	    attributeMap.clear();
	}

	@Override
	public void removeAttribute(String key) {
		attributeMap.remove(key);
	}

	@Override
	public void setAttribute(String key, Object value) {
		attributeMap.put(key, value);
	}
	
	@Override
	public String getId() {
		return sessionId;
	}

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public long getLastAccessedTime() {
        return lastAccessedTime;
    }
    
    @Override
    public boolean isNew() {
        return this.isNew;
    }

    public void access() {
        this.lastAccessedTime = System.currentTimeMillis();
        this.isNew = false;
    }
	
}
