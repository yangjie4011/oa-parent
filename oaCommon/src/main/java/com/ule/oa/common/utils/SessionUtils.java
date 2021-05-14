package com.ule.oa.common.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
  * @ClassName: SessionUtils
  * @Description: session工具类
  * @author minsheng
  * @date 2017年5月10日 上午10:52:22
 */
public class SessionUtils {
	/**
	  * getSessionId(获得sessionId)
	  * @Title: getSessionId
	  * @Description: 获得sessionId
	  * @param request
	  * @return    设定文件
	  * String    返回类型
	  * @throws
	 */
	public static String getSessionId(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null && cookies.length > 0) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("sessionId")) {
					return cookie.getValue();
				}
			}
		}
        return null;
    }
	
	/**
	  * generateSid(生成一个sessionId)
	  * @Title: generateSid
	  * @Description: 生成一个sessionId
	  * @return    设定文件
	  * String    返回类型
	  * @throws
	 */
	public static String generateSid() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }
}
