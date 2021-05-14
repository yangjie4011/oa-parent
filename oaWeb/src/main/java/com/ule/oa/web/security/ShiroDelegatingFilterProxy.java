package com.ule.oa.web.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.filter.DelegatingFilterProxy;

public class ShiroDelegatingFilterProxy extends DelegatingFilterProxy {

    private String ignores;
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        String uri = req.getRequestURI().replace(req.getContextPath(), "");
        if (!uri.startsWith(ignores)) {
            super.doFilter(request, response, filterChain);
        } else {
            filterChain.doFilter(request, response);
        }
    }
    
    public String getIgnores() {
        return ignores;
    }

    public void setIgnores(String ignores) {
        this.ignores = ignores;
    }

}
