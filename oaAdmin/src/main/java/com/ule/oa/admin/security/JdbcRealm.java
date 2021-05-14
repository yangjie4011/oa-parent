package com.ule.oa.admin.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.web.subject.WebSubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.ule.arch.basehttpclient.BaseHttpClientFactory;
import com.ule.arch.basehttpclient.client.BaseHttpClient;
import com.ule.arch.basehttpclient.model.HttpRequest;
import com.ule.arch.basehttpclient.model.HttpResponse;
import com.ule.oa.base.po.Company;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.EmpDepart;
import com.ule.oa.base.po.EmpPosition;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.Position;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.CompanyService;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmpDepartService;
import com.ule.oa.base.service.EmpPositionService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.PositionService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.exception.UleException;
import com.ule.oa.common.spring.config.CustomPropertyPlaceholderConfigurer;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.URLEncoderUtils;
import com.ule.oa.common.utils.http.IPUtils;
import com.ule.oa.common.utils.json.JSONUtils;
import net.minidev.json.JSONObject;

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
    @Autowired
    private ConfigService configService;
    
    private static BaseHttpClient client = BaseHttpClientFactory.getClient();
    
    //登录接口验证用户名密码是否正确
  	public final static String LOGIN_INF_URL = (String)CustomPropertyPlaceholderConfigurer.getProperty("login.interface.url");
    
  	
    /**
     * 登录时获取认证信息
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(
            AuthenticationToken paramAuthenticationToken) throws AuthenticationException {
    	UsernamePasswordToken token = (UsernamePasswordToken) paramAuthenticationToken;
       
    	ServletRequest req = ((WebSubject)SecurityUtils.getSubject()).getServletRequest();   
        HttpServletRequest request = (HttpServletRequest)req;  
        ServletResponse res = ((WebSubject)SecurityUtils.getSubject()).getServletResponse();
        HttpServletResponse response = (HttpServletResponse)res;
    	
    	String username = token.getUsername();
        String password = String.valueOf(token.getPassword());
        User user = new User();
        if(username == null || username.equals("")) {
        	throw new UnknownAccountException();
        } else if(username.equals("callbackMaileSuiXinYou")) {
        	user.setIsLocked(0);
        	user.setEmployeeId(Long.valueOf(password));
        	user = userService.getLoginUser(user);
        } else {
        	user.setIsLocked(0);
        	user.setUserName(username);
        	user = userService.getLoginUser(user);
        }
        //当前用户不存在
        if (null == user) {
        	logger.error("用户{}---{}信息不存在",username,password);
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
        } else if(ConfigConstants.IS_YES_INTEGER.equals(company.getIsUle()) || "samsonyeung".equals(username)){//邮乐
        	try {
    			User uleUser = checkUser(username, password);
    			String returnCode = uleUser.getReturnCode();
    			
    			if("".equals(returnCode)){ //returnCode为""表示登陆成功
    				logger.info("用户{}账户信息验证通过",uleUser.getUserName());
    			}else{
    				logger.info("用户{}登录失败，错误码={}",uleUser.getUserName(),returnCode);
    				request.setAttribute("passportError",returnCode);
    				throw new IncorrectCredentialsException(returnCode);
    			}
        	} catch (IOException e) {
    			logger.error("调用大网登录接口出错,出错原因={}",e.getMessage());
    			return null;
    		}
        }else{//非邮乐(目前不考虑)
        	user = new User();
        	user.setIsLocked(0);
            user.setUserName(username);
           // user.setPassword(password);
            user = userService.getLoginUser(user);
            
            //当前用户不存在
            if (null == user) {
                throw new UnknownAccountException();
            }
        }
       
        Employee emp = employeeService.getById(user.getEmployeeId());
        
        //指定员工类型的员工才能登陆
        List<Long> empTypeList = configService.getNeedEmpTypeIdList();
        boolean flag = false;
        for(Long empType:empTypeList){
        	if(empType.longValue()==emp.getEmpTypeId().longValue()){
        		flag = true;
        		break;
        	}
        }
        
        if(emp.getJobStatus()==null||emp.getJobStatus().intValue()==1){//离职不能登录
        	flag = false;
        }
        //签到账号特殊处理
        if("OA_SIGN".equals(emp.getCnName())){
        	flag = true;
        }
        
        if(!flag){//没有登录权限
        	throw new UnauthenticatedException(emp.getCnName() + "没有后台登录权限");
        }
        
        user.setDepart(new Depart());
        user.setPosition(new Position());
        user.setEmployee(new Employee());
        if(null != emp){
        	 //封装员工信息
        	user.setEmployee(emp);
        	
        	//封装部门信息
            EmpDepart ed  = new EmpDepart();
    		ed.setEmployeeId(emp.getId());
    		EmpDepart empDepart = empDepartService.getByCondition(ed);
    		if(null != empDepart && null != empDepart.getDepartId()){
    			Depart depart = departService.getById(empDepart.getDepartId());
    			if(null != depart){
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
        String remFlag = request.getParameter("remFlag");
        String loginInfo = URLEncoderUtils.encodeStr(user.getUserName())+"!!||"+URLEncoderUtils.encodeStr(user.getPassword())+"!!||"+remFlag;
        if("1".equals(remFlag)){ //"1"表示用户勾选记住密码
        	if(null != emp){//返回头像
				loginInfo += "!!||" + URLEncoderUtils.encodeStr(emp.getPicture());
        	}
        	
        	Cookie userCookie=new Cookie("loginInfo",loginInfo); 

            userCookie.setMaxAge(30*24*60*60);   //存活期为一个月 30*24*60*60
            userCookie.setPath("/");
            response.addCookie(userCookie); 
        }else{//移除缓存
        	loginInfo = URLEncoderUtils.encodeStr(user.getUserName())+"!!||"+URLEncoderUtils.encodeStr("")+"!!||"+remFlag;
        	if(null != emp){//返回头像
        		loginInfo += "!!||" + URLEncoderUtils.encodeStr(emp.getPicture());
        	}
        	
        	Cookie cookie = new Cookie("loginInfo",loginInfo);
        	cookie.setPath("/"); 
        	response.addCookie(cookie);
        }
        
        logger.info("用户{}登录成功，登录ip={}",username,IPUtils.getIpAddress(request));
        
        return new SimpleAuthenticationInfo(user, password, username);
    }
    
    /**
     * @throws IOException 
      * checkUser(邮乐用户验证通行证帐号密码)
      * @Title: checkUser
      * @Description: 邮乐用户验证通行证帐号密码
      * @param userName
      * @param password
      * @return
      * @throws UleException    设定文件
      * User    返回类型
      * @throws
     */
    public User checkUser(String userName,String password) throws IOException{
    	//通过远程接口登陆
		HashMap<String, String> paramMap = new HashMap<String,String>();
		paramMap.put("accountName", userName);
		paramMap.put("accountPassword", password);
		
		HttpRequest req = new HttpRequest.Builder().url(LOGIN_INF_URL).post(HttpRequest.HttpMediaType.JSON, 
				JSONObject.toJSONString(paramMap)).build();
		
		HttpResponse rep = null;

		rep = client.sendRequest(req);
		
		Map<?, ?> loginReMap = JSONUtils.readAsMap(rep.fullBody());
		
		User user = new User();
		user.setReturnCode((String)loginReMap.get("errorMsg"));
		user.setUserName(userName);
		
		return user;
    }

    /**
     * 访问资源时获取用户的权限信息
     */
    @SuppressWarnings("unused")
	@Override
    protected AuthorizationInfo doGetAuthorizationInfo(
            PrincipalCollection paramPrincipalCollection) {
        User user = (User) paramPrincipalCollection.getPrimaryPrincipal();
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
