package com.ule.oa.web.session;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

public abstract class HttpSessionWrapper implements HttpSession {

	private HttpSession session;
	
	public HttpSessionWrapper(){}

	public HttpSessionWrapper(HttpSession session) {
		this.session = session;
	}

	public int getMaxInactiveInterval() {
		return session.getMaxInactiveInterval();
	}

	public ServletContext getServletContext() {
		return session.getServletContext();
	}

	public HttpSessionContext getSessionContext() {
		return session.getSessionContext();
	}

	public Object getValue(String arg0) {
		return session.getValue(arg0);
	}

	public String[] getValueNames() {
		return session.getValueNames();
	}

	public void putValue(String arg0, Object arg1) {
		session.putValue(arg0, arg1);
	}

	public void removeValue(String arg0) {
		session.removeValue(arg0);
	}

	public void setMaxInactiveInterval(int arg0) {
		session.setMaxInactiveInterval(arg0);
	}

}
