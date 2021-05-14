package com.ule.oa.base.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ule.arch.basehttpclient.BaseHttpClientFactory;
import com.ule.arch.basehttpclient.client.BaseHttpClient;
import com.ule.arch.basehttpclient.model.HttpRequest;
import com.ule.arch.basehttpclient.model.HttpResponse;
import com.ule.arch.basehttpclient.standard.util.ContentCoverter;
import com.ule.oa.base.cache.ValidcodeCacheManager;
import com.ule.oa.base.mapper.DepartMapper;
import com.ule.oa.base.mapper.UserMapper;
import com.ule.oa.base.po.CompanyConfig;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeApp;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.CompanyConfigService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.SmsService;
import com.ule.oa.base.service.RabcRoleService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.spring.config.CustomPropertyPlaceholderConfigurer;
import com.ule.oa.common.utils.json.JSONUtils;

import net.minidev.json.JSONObject;

/**
  * @ClassName: UserServiceImpl
  * @Description: 用户信息业务层
  * @author minsheng
  * @date 2017年5月10日 下午2:57:05
 */
@Service
public class UserServiceImpl implements UserService{
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private RabcRoleService uRoleService;
	@Autowired
	private DepartMapper departMapper;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private SmsService smsService;
	@Autowired
	private CompanyConfigService companyConfigService;
	@Autowired
	private ValidcodeCacheManager validcodeCacheManager;
	@Value("${cache.env}")
	private String env;
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	//登录接口验证用户名密码是否正确
  	public final static String LOGIN_INF_URL = CustomPropertyPlaceholderConfigurer.getProperty("login.interface.url").toString();
  	
  	private static BaseHttpClient client = BaseHttpClientFactory.getClient();

	/**
	  * save(保存用户信息)
	  * @Title: save
	  * @Description: 保存用户信息
	  * @param user
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
   @Override
   public int save(User user){
	   return userMapper.save(user);
   }
   
   /**
     * updateById(更新用户信息)
     * @Title: updateById
     * @Description: 更新用户信息
     * @param user
     * @return    设定文件
     * int    返回类型
     * @throws
    */
   @Override
   public int updateById(User user){
	   return userMapper.updateById(user);
   }
   
   
   /**
     * getListByCondition(根据条件获取所有用户信息)
     * @Title: getListByCondition
     * @Description: 根据条件获取所有用户信息
     * @param user
     * @return    设定文件
     * List<User>    返回类型
     * @throws
    */
   @Override
   public List<User> getListByCondition(User user){
	   return userMapper.getListByCondition(user);
   }
   
   /**
     * getUserByCondition(根据条件获取用户信息)
     * @Title: getUserByCondition
     * @Description: 根据条件获取用户信息
     * @param user
     * @return    设定文件
     * User    返回类型
     * @throws
    */
   @Override
   public User getByCondition(User user){
	   List<User> users = getListByCondition(user);
	   
	   if(null != users && users.size()>0){
		   return users.get(0);
	   }
	   
	   return null;
   }
   
   /**
     * getCurrentUser(获取当前登录用户)
     * @Title: getCurrentUser
     * @Description: 获取当前登录用户
     * @return    设定文件
     * User    返回类型
     * @throws
    */
   @Override
   public User getCurrentUser(){
	   try {
		   return (User) SecurityUtils.getSubject().getPrincipal();
	   } catch (Exception e) {
           logger.error("error on getCurrentUser : {}", e.getMessage());
       }
	   return null;
   }
   
   /**
     * getCurrentAccount(这里用一句话描述这个方法的作用)
     * @Title: getCurrentAccount
     * @Description: 获取当前登录用户名
     * @return    设定文件
     * String    返回类型
     * @throws
    */
   @Override
   public String getCurrentAccount() {
	   User user = getCurrentUser();
	   if(null == user){
		   return "";
	   }
	   
	   return user.getUserName();
	}
   
   /**
    * @throws OaException 
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
   public User checkUser(String userName,String password) throws OaException{
   		//通过远程接口登陆
		HashMap<String, String> paramMap = new HashMap<String,String>();
		paramMap.put("accountName", userName);
		paramMap.put("accountPassword", password);
		
		HttpRequest req = new HttpRequest.Builder().url(LOGIN_INF_URL).post(HttpRequest.HttpMediaType.JSON, 
				JSONObject.toJSONString(paramMap)).build();
		HttpResponse rep = null;
		User user = new User();
		
		try {
			rep = client.sendRequest(req);
			String loginReStr = rep.fullBody();
			Map<?, ?> loginReMap = JSONUtils.readAsMap(loginReStr);
			user.setReturnCode((String)loginReMap.get("errorMsg"));
			user.setUserName(userName);
			
		} catch (IOException e) {
			
		}
		return user;
		
   }
  
  public String getEmpPicByEmp(Employee emp){
	  return getEmpPicByEmp(emp.getPicture(), emp.getEmail());
  }
  
  public String getEmpPicByEmp(EmployeeApp emp){
	  return getEmpPicByEmp(emp.getPicture(), emp.getEmail());
  }
  
  @SuppressWarnings("unchecked")
public String getEmpPicByEmp(String empPic,String email){
	  try{
		  if(StringUtils.isNotBlank(empPic)){
			  logger.info("用户已经上传头像，不用获取微信头像");
			  return empPic;
		  }else if(StringUtils.isBlank(email)){
			  logger.info("用户邮箱为空，无法获取微信头像");
			  return empPic;
		  }
		  
		  logger.info("邮箱{}获取微信头像开始... ...",email);
		  
		  //获取随心邮用户头像接口地址
		  CompanyConfig companyConfig = new CompanyConfig();
		  companyConfig.setCode("sxyEmpPic");
		  CompanyConfig cc = companyConfigService.getByCondition(companyConfig);
		  String picUrl = "";
		  if(null != cc && StringUtils.isNotBlank(cc.getDisplayCode())){
			  picUrl = cc.getDisplayCode();
		  }else{
			  return "";
		  }
		  
		  HashMap<String, String> paramMap = new HashMap<String,String>();
		  paramMap.put("username", email);
		  
		  if(!StringUtils.isBlank(env) && (env.equalsIgnoreCase("dev") || env.equalsIgnoreCase("local"))){
			  picUrl = "http://wxmailatest.tom.com/wxmail/auth/getUserInfoByOa";
		  }
		 
		  HttpRequest req = new HttpRequest.Builder().url(picUrl).post(HttpRequest.HttpMediaType.FORM_URLENCODE, 
  				ContentCoverter.formConvertAsString(paramMap)).build();
  		  HttpResponse rep = client.sendRequest(req);
		  String picReStr = rep.fullBody();
		  
		  logger.info("邮箱{}获取微信头像返回的数据={}",email,picReStr);
			
		  Map<?, ?> picReMap = JSONUtils.readAsMap(picReStr);
		  Map<?, ?> msgReMap = (Map<?, ?>)picReMap.get("ret_msg");
		  
		  if("000".equals((String)picReMap.get("ret_code"))){
			    LinkedHashMap  userInfoList = (LinkedHashMap) msgReMap.get("userinfo");
				if(null == userInfoList || userInfoList.size() == 0){
					empPic = "";
				}else{
					empPic = (String)userInfoList.get("headimgurl");
				}
		  }else{
			  logger.error("调用随心邮获取用户头像失败,原因={}",(String)msgReMap.get("message") );
		  }
		  
		  logger.info("邮箱{}获取微信头像结束,图片地址={}",email,empPic);
	  }catch(Exception e){
		  logger.error("获取随心邮用户头像失败,原因={}",e.getMessage());
	  }
		
	  return empPic;
  }
  
  public boolean haveApprovalAuthority() {
	  boolean isLeader = false;
	  User user = getCurrentUser();
	  Employee employee = new Employee();
  	employee.setReportToLeader(user.getEmployeeId());
  	int leaderCount = employeeService.getListByConditionCount(employee);
  	if(leaderCount > 0){
  		isLeader =true;
  	}
  	if("企业文化专员".equals(user.getPosition().getPositionName())||"人力资源高级经理".equals(user.getPosition().getPositionName())||"HRBP专员".equals(user.getPosition().getPositionName())||"人力资源及行政总监".equals(user.getPosition().getPositionName())||"人力资源经理".equals(user.getPosition().getPositionName())
  			||"高级HRBP".equals(user.getPosition().getPositionName())||"HRBP".equals(user.getPosition().getPositionName())
  			||"HRBP助理".equals(user.getPosition().getPositionName())||"人力资源助理".equals(user.getPosition().getPositionName())
  			||"薪资福利高级专员".equals(user.getPosition().getPositionName())||"高级企业文化专员".equals(user.getPosition().getPositionName())){
  		isLeader=true;
  	}
  	return isLeader;
  }

	@Override
	public User getByCode(String code) {
		return userMapper.getByCode(code);
	}

	@Override
	public User getUserByEmployeeId(Long employeeId) {
		return userMapper.getByEmployeeId(employeeId);
	}

	@Override
	public User getLoginUser(User user) {
		return userMapper.getLoginUser(user);
	}
}
