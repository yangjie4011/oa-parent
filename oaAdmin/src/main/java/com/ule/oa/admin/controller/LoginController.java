package com.ule.oa.admin.controller;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ule.oa.admin.security.JdbcRealm;
import com.ule.oa.base.po.RabcResource;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.RabcResourceService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.JSON;
import com.ule.oa.common.utils.JsonWriter;

@Controller
@RequestMapping("login")
public class LoginController{
	
	@Autowired
    private JdbcRealm jdbcRealm;
	@Autowired
	private UserService userService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private RabcResourceService resourceService;
	
	/**
     * 跳转到登录页面(默认跳转到登录页面)
     * @return
     */
    @RequestMapping(value = "/login.htm", method = RequestMethod.GET)
    public String login() {
    	//默认执行退出方法
    	Subject subject = SecurityUtils.getSubject();
    	subject.logout();
        return "security/login";
    }
	
    /**
     * 登录失败或重复登录时，过滤器不拦截继续执行controller方法
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/login.htm", method = RequestMethod.POST)
    public @ResponseBody JSON doLogin(HttpServletRequest request) {

        String error = getLoginFailureInfo(request);
        
        if (null != error) {
            //登录失败
            return JsonWriter.failedMessage(error);
        } else {
            //重复登录，此处和WarehouseFormAuthenticationFilter处理登录成功一致
            jdbcRealm.clearCachedAuthorizationInfo();
            User user = (User) SecurityUtils.getSubject().getPrincipal();
            
            return JsonWriter.successfulResult(user);
        }
    }
    
    /**
     * @throws OaException 
      * index(跳转到主页)
      * @Title: index
      * @Description: 跳转到主页
      * @return    设定文件
      * String    返回类型
      * @throws
     */
    @RequestMapping("/index.htm")
    public String index(HttpServletRequest request,HttpServletResponse response) throws OaException{
    	User user = userService.getCurrentUser();
    	request.setAttribute("emp", employeeService.getCurrentEmployee());
    	
    	//查询一级菜单(暂时将所有的一级菜单都加载出来，后面要根据职位加载菜单)
    	List<RabcResource> resList = resourceService.getFristAdminMenuListByUserId(user.getId());
    	request.setAttribute("resList", resList);
    	
    	return "index/main";
    }
    
    /**
      * getLoginFailureInfo(这里用一句话描述这个方法的作用)
      * @Title: getLoginFailureInfo
      * @Description: 获取登录错误提示
      * @param request
      * @return    设定文件
      * String    返回类型
      * @throws
     */
    private String getLoginFailureInfo(HttpServletRequest request) {

        String exceptionClassName = (String) request.getAttribute("shiroLoginFailure");
        if (null == exceptionClassName) {
            return null;
        }
        if (UnknownAccountException.class.getName().equals(exceptionClassName)) {
            return "用户名/密码错误";
        } else if (IncorrectCredentialsException.class.getName().equals(exceptionClassName)) {
            String passportError = (String) request.getAttribute("passportError");
        	return passportError;
        } else if (AuthenticationException.class.getName().equals(exceptionClassName)) {
            return "当前用户没有后台登录权限";
        } else {
            return "登录错误";
        }
    }
    
    /**
     * @throws OaException 
      * toDefault(跳转到左侧页面)
      * @Title: toDefault
      * @Description: 跳转到左侧页面
      * void    返回类型
      * @throws
     */
    @RequestMapping("/toLeft.htm")
    public String toLeft(HttpServletRequest request,Integer id) throws OaException{
    	
    	//根据一级菜单查询子菜单
    	if(null != id){
	    	List<RabcResource> resList = resourceService.getAllAdminTabListByUserIdAndParentId(userService.getCurrentUser().getId(),id);
	    	request.setAttribute("resList", resList);
    	}else{
    		request.setAttribute("resList", new ArrayList<RabcResource>());
    	}
    	
    	request.setAttribute("emp", employeeService.getCurrentEmployee());
    	
    	return "index/left";
    }
    
    @ResponseBody
    @RequestMapping("/getLeftMenu.htm")
    public List<RabcResource> getLeftMenu(Integer id) throws OaException{
    
    	List<RabcResource> resList = null;
    	//根据一级菜单查询子菜单
    	if(null != id){
	    	resList = resourceService.getAllAdminTabListByUserIdAndParentId(userService.getCurrentUser().getId(),id);
    	}else{
    		
    	}
    	
    	return resList;
    }
    @ResponseBody
    @RequestMapping("/getThirdLeftMenu.htm")
    public List<RabcResource> getThirdLeftMenu(Integer id) throws OaException{
    
    	List<RabcResource> resList = null;
    	//根据一级菜单查询子菜单
    	if(null != id){
	    	resList = resourceService.getAllAdminTabListByUserIdAndParentId(userService.getCurrentUser().getId(),id);
    	}else{
    		
    	}
    	
    	return resList;
    }
    
    /**
     * @throws OaException 
      * toDefault(跳转到默认页面)
      * @Title: toDefault
      * @Description: 跳转到默认页面
      * void    返回类型
      * @throws
     */
    @RequestMapping("/toDefault.htm")
    public String toDefault(HttpServletRequest request) throws OaException{
    	
    	request.setAttribute("emp", employeeService.getCurrentEmployee());
    	
    	return "index/default";
    }
}
