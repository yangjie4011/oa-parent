package com.ule.oa.admin.session.filter;

import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.ule.oa.admin.util.IsMenu;
import com.ule.oa.admin.util.IsOperation;
import com.ule.oa.base.mapper.RabcRoleMapper;
import com.ule.oa.base.po.RabcResource;
import com.ule.oa.base.service.RabcResourceService;
import com.ule.oa.base.service.UserService;

/**
  * @ClassName: MenuPowerInterceptor
  * @Description: 拦截所有url查询用户是否具有操作权限和菜单权限
  * @author minsheng
  * @date 2018年1月5日 上午10:31:59
 */
public class MenuPowerInterceptor extends HandlerInterceptorAdapter {
	@Autowired
	private UserService userService;
	@Autowired
	private RabcResourceService resourceService;
	@Autowired
	private RabcRoleMapper uRoleMapper;
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
            String requestUri = request.getRequestURI();
            
            //拦截是否具有菜单权限
            IsMenu isMenu = method.getAnnotation(IsMenu.class);
            if (isMenu != null) {
                //查询员工所有菜单权限
            	Long userId = userService.getCurrentUser().getId();
            	String menuUrl = requestUri.substring(9, requestUri.length());
            	
            	RabcResource rabcResource = resourceService.getMenuByUserIdAndUrl(userId,menuUrl);
            
            	boolean flag = false;
        		if(rabcResource!=null&&StringUtils.isNotBlank(rabcResource.getUrl())){
        			flag = true;
        		}
        		
            	if(flag){
            		return true;    
            	}else{
            		request.getRequestDispatcher("/WEB-INF/pages/error/403.jsp").forward(request, response);
                	return false;
            	}
            }
            
            //拦截是否具有操作权限
            IsOperation isOperation = method.getAnnotation(IsOperation.class);
            if (isOperation != null) {
            	//查询员工所有操作权限 
            	Long userId = userService.getCurrentUser().getId();
            	String operationUrlList[] = requestUri.split("/");
            	String operationUrl = operationUrlList[operationUrlList.length-2]+"/"+operationUrlList[operationUrlList.length-1];
            	
            	RabcResource rabcResource = resourceService.getOperationByUserIdAndUrl(userId,operationUrl);
            	boolean flag = false;
            	
        		if(rabcResource!=null&&StringUtils.isNotBlank(rabcResource.getUrl())){
        			flag = true;
        		}
            	
        		if(flag){
            		return true;  
            	}else{
            		if(isOperation.returnType()){
            			request.getRequestDispatcher("/WEB-INF/pages/error/no_operation.jsp").forward(request, response);
            			return false;
                	}else{
                		response.sendError(403);
                		return false;
                	}
            	}
            }
            return true;    
        } else {
            return super.preHandle(request, response, handler);    
        }
	}
	
}
