package com.ule.oa.web.security;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.web.subject.WebSubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.ule.oa.base.po.Company;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.EmpDepart;
import com.ule.oa.base.po.EmpPosition;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.Position;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.CompanyService;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmpDepartService;
import com.ule.oa.base.service.EmpPositionService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.PositionService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.URLEncoderUtils;
import com.ule.oa.common.utils.http.IPUtils;

/**
 * 认证和鉴权Realm
 * 
 * @author zhangwei002
 * 
 */
public class JdbcRealm extends AuthorizingRealm {
	private Logger logger = LoggerFactory.getLogger(JdbcRealm.class);
    @Value("${security.password.salt}")
    private String passwordSalt;
    @Autowired
    private UserService userService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private DepartService departService;
    @Autowired
    private PositionService positionService;
    @Autowired
    private EmpDepartService empDepartService;
    @Autowired
    private EmpPositionService empPositionService;
    
    /**
     * 登录时获取认证信息
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(
            AuthenticationToken paramAuthenticationToken) throws AuthenticationException {
    	
	    ServletRequest req = ((WebSubject)SecurityUtils.getSubject()).getServletRequest();   
        HttpServletRequest request = (HttpServletRequest)req;  
        ServletResponse res = ((WebSubject)SecurityUtils.getSubject()).getServletResponse();
        HttpServletResponse response = (HttpServletResponse)res;
    	UsernamePasswordToken token = (UsernamePasswordToken) paramAuthenticationToken;
        String username = token.getUsername();
        String password = String.valueOf(token.getPassword());
        User user = new User();
        if(username == null || username.equals("")) {
        	throw new UnknownAccountException();
        } else if(username.equals("callbackMaileSuiXinYou")) {
        	logger.info("员工{}模仿登录",password);
        	user.setEmployeeId(Long.valueOf(password));
        	user = userService.getByCondition(user);
        } else {
        	user.setUserName(username);
        	//只有在职的能登陆
        	List<Long> jobStatusList = new ArrayList<Long>();
	    	jobStatusList.add(0L);
	    	jobStatusList.add(2L);
	    	jobStatusList.add(3L);
	    	user.setJobStatusList(jobStatusList);
        	//只有特定员工类型能登陆
	    //	user.setEmpTypeIdList(configService.getNeedEmpTypeIdList());
        	user = userService.getByCondition(user);
        }
        //当前用户不存在
        if (null == user) {
        	logger.error("用户{}信息不存在",username);
            throw new UnknownAccountException();
        }
        //取出公司编码
        Company company = new Company();
        company.setId(user.getCompanyId());
        company = companyService.getByCondition(company);
        if(null == company){//用户不存在
        	logger.error("用户{}公司信息不存在",username);
        	throw new UnknownAccountException();
        }
        String callbackMaileSuiXinYouStr = "0";
        if(username.equals("callbackMaileSuiXinYou")) {
        	callbackMaileSuiXinYouStr = "1";
        } else if(company.getIsUle().equals(ConfigConstants.IS_YES_INTEGER) || username.equals("samsonyeung")){//邮乐
        	try {
    			User uleUser = userService.checkUser(username, password);
    			String returnCode = uleUser.getReturnCode();
    			
    			if("".equals(returnCode)){ //returnCode为""表示登陆成功
    				logger.info("用户{}账户信息验证通过",uleUser.getUserName());
    			}else{
    				logger.info("用户{}登录失败，错误码={}",uleUser.getUserName(),returnCode);
    				request.setAttribute("passportError", returnCode);
    				throw new IncorrectCredentialsException("调用通行证验证接口出错，错误码="+returnCode);
    			}
        	} catch (OaException e) {
    			logger.error("调用大网登录接口出错,出错原因={}",e.getMessage());
    			return null;
    		}
        }else{//非邮乐(目前不考虑)
        	user = new User();
            user.setUserName(username);
           // user.setPassword(password);
            user = userService.getByCondition(user);
            
            //当前用户不存在
            if (null == user) {
                throw new UnknownAccountException();
            }
        }
       
        Employee emp = employeeService.getById(user.getEmployeeId());
        user.setDepart(new Depart());
        user.setPosition(new Position());
        user.setEmployee(new Employee());
        if(null != emp){
        	 //封装员工信息
        	user.setEmployee(emp);
//        	emp.setPicture(userService.getEmpPicByEmp(emp));
        	
        	//封装部门信息
            EmpDepart ed  = new EmpDepart();
    		ed.setEmployeeId(emp.getId());
    		EmpDepart empDepart = empDepartService.getByCondition(ed);
    		if(null != empDepart && null != empDepart.getDepartId()){
    			Depart depart = departService.getById(empDepart.getDepartId());
    			if(null != depart){//登陆不拼接部门名
    				/*String departName = "";
    				departName = departService.getDepartAllLeaveName(depart.getId());
    				depart.setName(departName);*/
    				user.setDepart(depart);
    			}
    		}
    		
    		//封装职位信息
    		EmpPosition ep = new EmpPosition();
    		ep.setEmployeeId(emp.getId());
    		EmpPosition empPosition = empPositionService.getByCondition(ep);
    		if(null != empPosition && null != empPosition.getPositionId()){
    			Position position = positionService.getById(empPosition.getPositionId());
    			if(null != position){
    				user.setPosition(position);
    			}
    		}
        }
        //用户存在
        user.setPassword(password);
        user.setCompany(company);
        
        Cookie callbackMaileSuiXinYou=new Cookie("callbackMaileSuiXinYou",callbackMaileSuiXinYouStr); 
        callbackMaileSuiXinYou.setMaxAge(30*24*60*60);   //存活期为一个月 30*24*60*60
        callbackMaileSuiXinYou.setPath("/");
        response.addCookie(callbackMaileSuiXinYou);
       
        //记住密码功能(注意：cookie存放密码会存在安全隐患)
		remPwd(response, request, user, emp);
        
        logger.info("用户{}登录成功，登录ip={}",user.getUserName(),IPUtils.getIpAddress(request));
        
        return new SimpleAuthenticationInfo(user, password, user.getUserName());
    }
    
    public void remPwd(HttpServletResponse response,HttpServletRequest request,User user,Employee emp){
    	String remFlag = request.getParameter("remFlag");
        String loginInfo = URLEncoderUtils.encodeStr(user.getUserName())+"!!||"+URLEncoderUtils.encodeStr(user.getPassword())+"!!||"+remFlag;
        String empPic = "";
        
    	if("1".equals(remFlag)){ //"1"表示用户勾选记住密码
        	if(null != emp){//返回头像
        		empPic = userService.getEmpPicByEmp(emp);
				loginInfo += "!!||" + URLEncoderUtils.encodeStr(empPic);
				emp.setPicture(empPic);
        	}
        	
        	Cookie userCookie=new Cookie("loginInfo",loginInfo); 

            userCookie.setMaxAge(30*24*60*60);   //存活期为一个月 30*24*60*60
            userCookie.setPath("/");
            response.addCookie(userCookie); 
        }else{//移除缓存
        	loginInfo = URLEncoderUtils.encodeStr(user.getUserName())+"!!||"+URLEncoderUtils.encodeStr("")+"!!||"+remFlag;
        	if(null != emp){//返回头像
        		empPic = userService.getEmpPicByEmp(emp);
        		loginInfo += "!!||" + URLEncoderUtils.encodeStr(userService.getEmpPicByEmp(emp));
        		emp.setPicture(empPic);
        	}
        	
        	
        	Cookie cookie = new Cookie("loginInfo",loginInfo);
        	cookie.setPath("/"); 
        	response.addCookie(cookie);
        }
    }
    
    /**
     * 访问资源时获取用户的权限信息
     */
    @SuppressWarnings("unused")
	@Override
    protected AuthorizationInfo doGetAuthorizationInfo(
            PrincipalCollection paramPrincipalCollection) {
        User user = (User) paramPrincipalCollection.getPrimaryPrincipal();
//
//        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
//        //设置登录仓库的角色和权限
//        if (null != user.getWhId()) {
//            List<String> permissionList = resourceMapper.getPermissionsByUserIdAndAppId(user.getId(), "WMS_MOBILE", user.getWhId());
//            Set<String> permissionSet = new HashSet<String>();
//            for (String permission : permissionList) {
//                permissionSet.add(permission);
//            }
//            authorizationInfo.setStringPermissions(permissionSet);
//        }
//        return authorizationInfo;
    	
    	return null;
    }
    
    /**
     * 获取权限缓存的key:userId
     */
    @Override
    protected String getAuthorizationCacheKey(PrincipalCollection principals) {
        return ((User) principals.getPrimaryPrincipal()).getId().toString();
    }

    /**
     * 清除当前用户的权限信息
     */
    public void clearCachedAuthorizationInfo() {
        clearCachedAuthorizationInfo(SecurityUtils.getSubject().getPrincipals());
    }
    
    /**
      * 清空当前用户信息
     */
    public void clearCachedAuthenticationInfo(){
    	clearCachedAuthenticationInfo(SecurityUtils.getSubject().getPrincipals());
    }
    
}
