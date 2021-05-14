package com.ule.oa.common.utils.http;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * 重定向时强制到https
 * @author zhangwei002
 *
 */
public class HttpsRedirectServletResponseWrapper extends
        HttpServletResponseWrapper {
    
//    private Logger logger = LoggerFactory.getLogger(HttpsRedirectServletResponseWrapper.class);
    
    private HttpServletRequest request;

    public HttpsRedirectServletResponseWrapper(HttpServletRequest request, HttpServletResponse response) {
        super(response);
        this.request = request;
    }

    /**
     * 如果是https请求，则设置https跳转的response
     */
    @Override
    public void sendRedirect(String location) throws IOException {
//        logger.info("重定向地址before:" + location);
        if (isHtppsRequest(request) && !isHttpsLocation(location)) {
            location = getHttpsUrl(location);
        }
//        logger.info("重定向地址after:" + location);
        super.sendRedirect(location);
    }

    private String getHttpsUrl(String location) {
        if (location.startsWith("http://")) {
            location = location.replace("http://", "https://");
        } else {
            String httpsLocation = "https://" + request.getServerName();
            String header = request.getHeader("X-Forwarded-Proto");
            if (null == header || header.indexOf("https") == -1) {
                httpsLocation += ":" + request.getServerPort();
            }
            location = httpsLocation + location;
        }
        return location;
    }
    
    private boolean isHtppsRequest(HttpServletRequest request) {
        String scheme = request.getScheme();
        String header = request.getHeader("X-Forwarded-Proto");
//        logger.info("url:" + request.getRequestURL().toString());
//        logger.info("scheme:" + scheme);
//        logger.info("header:" + header);
        return ((header != null && header.indexOf("https") > -1)
                || (scheme != null && scheme.indexOf("https") > -1));
    }
    
    private boolean isHttpsLocation(String location) {
        return location.startsWith("https://");
    }
    
}
