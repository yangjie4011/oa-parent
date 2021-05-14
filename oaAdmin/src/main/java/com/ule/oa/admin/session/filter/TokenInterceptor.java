package com.ule.oa.admin.session.filter;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.ule.oa.admin.util.Token;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.RedisUtils;
import com.ule.oa.common.utils.UUIDGenerator;

public class TokenInterceptor extends HandlerInterceptorAdapter{
	
	@Autowired
	private UserService userService;
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	
	/**
	  * preHandle(这里用一句话描述这个方法的作用)
	  * @Title: preHandle
	  * @Description: 实现拦截器父类的方法，在内部验证token访问令牌
	  * @param request
	  * @param response
	  * @param handler
	  * @return
	  * @throws Exception    设定文件
	  * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object)
	  * @throws
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
		throws Exception {
		if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            Token annotation = method.getAnnotation(Token.class);//通过反射拿到方法名上添加的注解
            
            if (annotation != null) {
                boolean needGenerateSession = annotation.generate();//生成token，如果方法上添加的是generate=true，则返回true，否则默认返回false
                boolean needRemoveSession = annotation.remove();//移除token，同上
                String empId = String.valueOf(userService.getCurrentUser().getEmployeeId());
                
                if (needGenerateSession) {//是否需要生成token
                	String uuId = UUIDGenerator.generate();
                    request.setAttribute("token", uuId);
                    response.setHeader("token",uuId);
                    RedisUtils.setString(ConfigConstants.ADMIN_REDIS_PRE+empId,uuId,1800000);
                }else if (needRemoveSession) {//是否需要移除token,只有在调用保存方法的时候才涉及到是否需要移除token
                	String clientToken = request.getParameter("token");//存储在客户端的token
                    if (isRepeatSubmit(ConfigConstants.ADMIN_REDIS_PRE+empId,clientToken)) {//判断是否重复点击
                    	logger.error(empId+"请勿重复点击提交按钮");
                    	response.sendError(ConfigConstants.TOKEN_CHECK_CODE);//表单重复提交
                    	
                        return false;
                    }
                    
                    //移除token
                    RedisUtils.delete(ConfigConstants.ADMIN_REDIS_PRE+empId);
                    request.getSession(false).removeAttribute("token");
                }
                  
            }
            return true;    
        } else {
            return super.preHandle(request, response, handler);    
        }
	}
	
	/**
	 * @throws Exception 
	  * isRepeatSubmit(验证是否重复点击)
	  * @Title: isRepeatSubmit
	  * @Description: 验证是否重复点击
	  * @param request
	  * @return    设定文件
	  * boolean    返回类型
	  * @throws
	 */
	private boolean isRepeatSubmit(String key,String clinetToken) throws Exception {
        String serverToken = RedisUtils.getString(key);//存储在服务端的token
        if (serverToken == null) {
        	logger.error(key+"服务端token未获取到");
            return true;
        }
        if (clinetToken == null) {//存储在客户端的token
        	logger.error(key+"客户端token未获取到");
            return true;
        }
        if (!serverToken.equals(clinetToken)) {//验证token是否一致
        	logger.error(key+"token已经失效");
            return true;
        }
        return false;
    }

}
