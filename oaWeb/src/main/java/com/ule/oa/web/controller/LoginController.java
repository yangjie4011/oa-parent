package com.ule.oa.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.ule.arch.basehttpclient.BaseHttpClientFactory;
import com.ule.arch.basehttpclient.client.BaseHttpClient;
import com.ule.arch.basehttpclient.model.HttpRequest;
import com.ule.arch.basehttpclient.model.HttpResponse;
import com.ule.arch.basehttpclient.standard.util.ContentCoverter;
import com.ule.oa.base.common.OaCommon;
import com.ule.oa.base.po.Company;
import com.ule.oa.base.po.CompanyConfig;
import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.User;
import com.ule.oa.base.po.tbl.DailyHeathSignTbl;
import com.ule.oa.base.service.ClassSetPersonService;
import com.ule.oa.base.service.CompanyConfigService;
import com.ule.oa.base.service.CompanyService;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.DailyHeathSignService;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.JSON;
import com.ule.oa.common.utils.JsonWriter;
import com.ule.oa.common.utils.json.JSONUtils;
import com.ule.oa.web.security.JdbcRealm;

@Controller
@RequestMapping("login")
public class LoginController{
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private JdbcRealm jdbcRealm;
	
	@Autowired
	private UserService userService;
	@Autowired
	private CompanyConfigService companyConfigService;
	@Autowired
	private ClassSetPersonService classSetPersonService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private DepartService departService;
	@Autowired
	private ConfigService configService;
	@Resource
	private CompanyService companyService;
	@Autowired
	private DailyHeathSignService dailyHeathSignService;
	
	private static BaseHttpClient client = BaseHttpClientFactory.getClient();
	/**
     * ?????????????????????(???????????????????????????)
     * @return
     */
    @RequestMapping(value = "/login.htm", method = RequestMethod.GET)
    public String login(HttpServletRequest request) {
    	request.setAttribute("forwardSuccessUrl", request.getAttribute("forwardSuccessUrl"));//???????????????????????????url
    	
    	Subject currentUser = SecurityUtils.getSubject();
    	if(currentUser.isAuthenticated()){//?????????????????????????????????????????????????????????????????????????????????session????????????????????????????????????????????????..............???
    		currentUser.logout();
    	}
    	
        return "security/login";
    }
    
    @RequestMapping(value = "/serverUpgrade.htm", method = RequestMethod.GET)
    public String serverUpgrade(HttpServletRequest request) {
        return "error/serverUpgrade";
    }
    /**
     * ??????????????????????????????
     * @return
     */
    @RequestMapping(value = "/loginSuiXinYou.htm")
    public ModelAndView loginSuiXinYou(HttpServletResponse response,String mail, String token,String index) {
    	
    	logger.info("mail=" + mail + ",token=" + token +",index="+index);
    	
    	User isLogin = userService.getCurrentUser();
    	
    	if(isLogin != null){
    		
    		List<DailyHeathSignTbl> isSign = dailyHeathSignService
        			.getByEmployeeIdAndSignDate(isLogin.getEmployee().getId(), 2, new Date());
        	
    		 if(StringUtils.isNotBlank(index)&&"dwqd".equals(index)){
             	ModelAndView loginSuiXinYou = new ModelAndView("forward:/locationCheckIn/index.htm");
         		loginSuiXinYou.addObject("isNotBack", true);
         		//?????????????????????????????????
         		loginSuiXinYou.addObject("otherPlace", false);
         		
         		Employee emp = employeeService.getById(isLogin.getEmployee().getId());
         		
         		if(emp.getWorkAddressType()!=null&&emp.getWorkAddressType().intValue()==1){
         			loginSuiXinYou.addObject("otherPlace", true);
         		}
         		loginSuiXinYou.addObject("workAddressType", emp.getWorkAddressType());
         		loginSuiXinYou.addObject("workAddressProvince", emp.getWorkAddressProvince());
         		return loginSuiXinYou;
       		}
             
             if(StringUtils.isNotBlank(index)&&"jksb".equals(index)){
             	ModelAndView loginSuiXinYou = new ModelAndView("forward:/dayHeath/index.htm");
         		if(isSign!=null&&isSign.size()>0){
         			loginSuiXinYou.addObject("isSign", true);
         		}
         		return loginSuiXinYou;
       		}
             
            Date now = new Date();
 			Date endTime = DateUtils.parse("2020-04-01 00:00:01", DateUtils.FORMAT_LONG);
    		
    		if((isSign==null||isSign.size()<=0)&&now.getTime()<endTime.getTime()){
        		List<DailyHeathSignTbl> signInfo = dailyHeathSignService
            			.getByEmployeeIdAndSignDate(isLogin.getEmployee().getId(), 1, null);
        		ModelAndView loginSuiXinYou = new ModelAndView("forward:/dayHeath/index.htm");
        		loginSuiXinYou.addObject("needBaseInfo", true);
        		if(signInfo!=null&&signInfo.size()>0){
        			loginSuiXinYou.addObject("needBaseInfo", false);
        		}
        		return loginSuiXinYou;
        	}
        	
    		return new ModelAndView("forward:/login/index.htm");
          
    	}
    	
    	if(mail == null || mail.equals("")) {
    		return new ModelAndView("forward:/login/login.htm");
    	}
    	try {
    		//????????????token???????????????
    		String tokenUrl = ConfigConstants.VERIFY_OA_TOKEN;
    		try{
	    		Config config = new Config();
	    		config.setCode("sxyDomain");
	    		Config conf = configService.getByCondition(config);
	    		if(null != conf && null != conf.getId()){
	    			tokenUrl = conf.getDisplayCode();
	    		}
    		}catch(Exception e){
    			logger.error("??????token????????????");
    		}
    		Map<String,String> paramMap = new HashMap<String,String>();
    		paramMap.put("token", token);
    		
    		HttpRequest req = new HttpRequest.Builder().url(tokenUrl).post(HttpRequest.HttpMediaType.FORM_URLENCODE, 
    				ContentCoverter.formConvertAsString(paramMap)).build();
    		
    		HttpResponse rep = null;
    		try {
				rep = client.sendRequest(req);
			} catch (IOException e1) {
				logger.error("mail=" + mail+"??????token?????????msg="+e1.getMessage());
			}
    		
			String str = rep!=null?rep.fullBody():"";
			logger.info("str=" + str);
			
			@SuppressWarnings("unchecked")
			Map<String, String> strMap = (Map<String, String>) JSONUtils.readAsMap(str);
//    		Map<String, String> strMap = new HashMap<String, String>();
//    		strMap.put("code", "000");
//    		strMap.put("mail", "yangjie@ule.com");
			if(strMap.get("code").equals("000") && strMap.get("mail").equals(mail)) {
				Employee employee = new Employee();
		    	employee.setEmail(mail);
		    	
		    	//??????????????????????????????
		    	List<Long> jobStatusList = new ArrayList<Long>();
		    	jobStatusList.add(0L);
		    	jobStatusList.add(2L);
		    	jobStatusList.add(3L);
		    	employee.setJobStatusList(jobStatusList);
		    	
		    	//???????????????????????????????????????
		    //	employee.setEmpTypeIdList(configService.getNeedEmpTypeIdList());
		    	employee = employeeService.getByCondition(employee);
				if(employee == null || employee.getId() == null) {
					return new ModelAndView("forward:/login/login.htm");
				}
				Subject currentUser = SecurityUtils.getSubject();
				User user = new User();
				if (currentUser.isAuthenticated()) {
					user = userService.getCurrentUser();
					logger.info("username=" + user.getUserName());
				}
				if(user == null || user.getEmployeeId() == null || !user.getEmployeeId().equals(employee.getId())) {
					UsernamePasswordToken upToken = new UsernamePasswordToken("callbackMaileSuiXinYou", employee.getId().toString());
					upToken.setRememberMe(false);
					try {
						//?????????????????????????????????????????????
						Cookie dataSourceUrl=new Cookie("dataSourceUrl","0"); 
						dataSourceUrl.setMaxAge(30*24*60*60);   //????????????????????? 30*24*60*60
						dataSourceUrl.setPath("/");
						response.addCookie(dataSourceUrl);
						
						currentUser.login(upToken);
					} catch (Exception e) {
					}
					user = userService.getCurrentUser();
					logger.info("username1=" + user.getUserName());
				}
				if(user== null) {
					return new ModelAndView("forward:/login/login.htm");
				} else {
					List<DailyHeathSignTbl> isSign = dailyHeathSignService
		        			.getByEmployeeIdAndSignDate(user.getEmployee().getId(), 2, new Date());
					 
					if(StringUtils.isNotBlank(index)&&"dwqd".equals(index)){
			            	ModelAndView loginSuiXinYou = new ModelAndView("forward:/locationCheckIn/index.htm");
			        		loginSuiXinYou.addObject("isNotBack", true);
			        		//?????????????????????????????????
			         		loginSuiXinYou.addObject("otherPlace", false);
			         		
			         		Employee emp = employeeService.getById(user.getEmployee().getId());
			         		
			         		if(emp.getWorkAddressType()!=null&&emp.getWorkAddressType().intValue()==1){
			         			loginSuiXinYou.addObject("otherPlace", true);
			         		}
			         		loginSuiXinYou.addObject("workAddressType", emp.getWorkAddressType());
			         		loginSuiXinYou.addObject("workAddressProvince", emp.getWorkAddressProvince());
			        		return loginSuiXinYou;
		      		}
		            
		            if(StringUtils.isNotBlank(index)&&"jksb".equals(index)){
		            	ModelAndView loginSuiXinYou = new ModelAndView("forward:/dayHeath/index.htm");
		        		if(isSign!=null&&isSign.size()>0){
		        			loginSuiXinYou.addObject("isSign", true);
		        		}
		        		return loginSuiXinYou;
		      		}
		            
		            Date now = new Date();
		 			Date endTime = DateUtils.parse("2020-04-01 00:00:01", DateUtils.FORMAT_LONG);
					
					if((isSign==null||isSign.size()<=0)&&now.getTime()<endTime.getTime()){
		        		List<DailyHeathSignTbl> signInfo = dailyHeathSignService
		            			.getByEmployeeIdAndSignDate(user.getEmployee().getId(), 1, null);
		        		ModelAndView loginSuiXinYou = new ModelAndView("forward:/dayHeath/index.htm");
		        		loginSuiXinYou.addObject("needBaseInfo", true);
		        		if(signInfo!=null&&signInfo.size()>0){
		        			loginSuiXinYou.addObject("needBaseInfo", false);
		        		}
		        		return loginSuiXinYou;
		        	}
		        	
		    		return new ModelAndView("forward:/login/index.htm");
					
				}
			} else {
				return new ModelAndView("forward:/login/login.htm");
			}
		} catch (Exception e1) {
			return new ModelAndView("forward:/login/login.htm");
		}
    }
    
    
    /**
     * ???????????????????????????????????????????????????????????????controller??????
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/login.htm", method = RequestMethod.POST)
    public @ResponseBody JSON doLogin(HttpServletRequest request) {
        String error = getLoginFailureInfo(request);
        
        if (null != error) {
            //????????????
            return JsonWriter.failedMessage(error);
        } else {
            //????????????????????????WarehouseFormAuthenticationFilter????????????????????????
            jdbcRealm.clearCachedAuthorizationInfo();
            User user = (User) SecurityUtils.getSubject().getPrincipal();
            
            return JsonWriter.successfulResult(user);
        }
    }
    
    /**
      * index(???????????????)
      * @Title: index
      * @Description: ???????????????
      * @return    ????????????
      * String    ????????????
      * @throws
     */
    @RequestMapping("/index.htm")
    public String index(HttpServletRequest request,HttpServletResponse response){
    	User user = userService.getCurrentUser();
    	
    	String userAgent = request.getHeader("user-agent");
    	logger.info("userAgent="+userAgent);
    	
    	List<DailyHeathSignTbl> isSign = dailyHeathSignService
    			.getByEmployeeIdAndSignDate(user.getEmployee().getId(), 2, new Date());
    
    	Date now = new Date();
		Date endTime = DateUtils.parse("2020-04-01 00:00:01", DateUtils.FORMAT_LONG);
    	
    	if((isSign==null||isSign.size()<=0)&&now.getTime()<endTime.getTime()){
    		List<DailyHeathSignTbl> signInfo = dailyHeathSignService
        			.getByEmployeeIdAndSignDate(user.getEmployee().getId(), 1, null);
    		request.setAttribute("needBaseInfo",true);
    		if(signInfo!=null&&signInfo.size()>0){
    			request.setAttribute("needBaseInfo",false);
    		}
    		return "dayHeath/sign";
    	}
    	
    	//????????????MO????????????,??????????????????
    	Cookie dataSourceUrl=new Cookie("dataSourceUrl","0"); 
		dataSourceUrl.setMaxAge(30*24*60*60);   //????????????????????? 30*24*60*60
		dataSourceUrl.setPath("/");
		response.addCookie(dataSourceUrl);
    	String forwardSuccessUrl = request.getParameter("forwardSuccessUrl");//?????????????????????????????????
    	if(!StringUtils.isEmpty(forwardSuccessUrl)){
    		return forwardSuccessUrl;
    	}
    	
    	
    	boolean isSetPerson = classSetPersonService.isSetPerson(user.getEmployee().getId());
    	request.setAttribute("isSetPerson",isSetPerson);
    	boolean isDh = departService.checkLeaderIsDh(user.getEmployee().getId());
    	request.setAttribute("isDh",isDh);
    	if(user.getUserName().equals("admin")) {
    		request.setAttribute("ruCode30", "30");
    		request.setAttribute("ruCode100", "100");
    		request.setAttribute("depart", "107");
    		request.setAttribute("isSetPerson",true);
    		request.setAttribute("isDh",true);
    	} else if(user.getEmployee().getWorkType() != null) {
    		CompanyConfig config = new CompanyConfig();
    		config.setId(user.getEmployee().getWorkType());
    		config = companyConfigService.getByCondition(config);
    		if(config!=null&&"standard".equals(config.getDisplayCode())) {
    			if(OaCommon.getRunCode100Map().containsKey(user.getDepart().getCode())) {
    				request.setAttribute("ruCode100", "100");
    			}
    		}
    	}
    	if("??????????????????".equals(user.getPosition().getPositionName())||"????????????????????????".equals(user.getPosition().getPositionName())||"HRBP??????".equals(user.getPosition().getPositionName())||"???????????????????????????".equals(user.getPosition().getPositionName())||"??????????????????".equals(user.getPosition().getPositionName())
    			||"??????HRBP".equals(user.getPosition().getPositionName())||"HRBP".equals(user.getPosition().getPositionName())
    			||"HRBP??????".equals(user.getPosition().getPositionName())||"??????????????????".equals(user.getPosition().getPositionName())
    			||"????????????????????????".equals(user.getPosition().getPositionName())||"????????????????????????".equals(user.getPosition().getPositionName())){
    		request.setAttribute("depart", "107");
    	}
    	
    	Employee emp = employeeService.getById(user.getEmployee().getId());
    	
    	request.setAttribute("workAddressType", emp.getWorkAddressType());
    	request.setAttribute("workAddressProvince", emp.getWorkAddressProvince());
    	request.setAttribute("companyId", user.getEmployee().getCompanyId());
    	request.setAttribute("companyName", user.getCompany().getName());
		request.setAttribute("cnName", user.getEmployee().getCnName());
		request.setAttribute("departName", user.getDepart().getName());
		request.setAttribute("positionName", StringUtils.isEmpty(user.getPosition().getPositionName())?"???":user.getPosition().getPositionName() );	
		request.setAttribute("email", StringUtils.isEmpty(user.getEmployee().getEmail())?"???":user.getEmployee().getEmail());
		request.setAttribute("extensionNumber", StringUtils.isEmpty(user.getEmployee().getExtensionNumber())?"???":user.getEmployee().getExtensionNumber());
		request.setAttribute("empPic", user.getEmployee().getPicture());
		return "index/main";
    }
    
    @RequestMapping("/indexAdd.htm")
    public String indexAdd(HttpServletRequest request){
    	User user = userService.getCurrentUser();
    	boolean isSetPerson = classSetPersonService.isSetPerson(user.getEmployee().getId());
    	request.setAttribute("isSetPerson",isSetPerson);
    	boolean isDh = departService.checkLeaderIsDh(user.getEmployee().getId());
    	request.setAttribute("isDh",isDh);
    	Integer allRunCount = 4;
    	if(user.getUserName().equals("admin")) {
    		request.setAttribute("ruCode30", "30");
    		request.setAttribute("ruCode100", "100");
    		request.setAttribute("depart", "107");
    		request.setAttribute("isSetPerson", true);
    		request.setAttribute("isDh",true);
    	} else if(user.getEmployee().getWorkType() != null) {
    		CompanyConfig config = new CompanyConfig();
    		config.setId(user.getEmployee().getWorkType());
    		config = companyConfigService.getByCondition(config);
    		if(config!=null&&"standard".equals(config.getDisplayCode())) {
    			allRunCount++;
    			if(OaCommon.getRunCode100Map().containsKey(user.getDepart().getCode())) {
    				request.setAttribute("ruCode100", "100");
    				allRunCount++;
    			}
    		}
    	}
    	if("??????????????????".equals(user.getPosition().getPositionName())||"????????????????????????".equals(user.getPosition().getPositionName())||"HRBP??????".equals(user.getPosition().getPositionName())||"???????????????????????????".equals(user.getPosition().getPositionName())||"??????????????????".equals(user.getPosition().getPositionName())
    			||"??????HRBP".equals(user.getPosition().getPositionName())||"HRBP".equals(user.getPosition().getPositionName())
    			||"HRBP??????".equals(user.getPosition().getPositionName())||"??????????????????".equals(user.getPosition().getPositionName())
    			||"????????????????????????".equals(user.getPosition().getPositionName())||"????????????????????????".equals(user.getPosition().getPositionName())){
    		request.setAttribute("depart", "107");
    	}
    	if(user.getEmployee().getWorkAddressType()==1){
    		allRunCount = 2;
    	}
    	request.setAttribute("workAddressType", user.getEmployee().getWorkAddressType());
    	request.setAttribute("workAddressProvince", user.getEmployee().getWorkAddressProvince());
    	request.setAttribute("allRunCount", allRunCount);
    	return "index/mainAdd";
    }
    
    /**
     * index(???????????????)
     * @Title: index
     * @Description: ???????????????
     * @return    ????????????
     * String    ????????????
     * @throws
    */
   @RequestMapping("/allRun.htm")
	public String allRun(HttpServletRequest request,
			HttpServletResponse response) {
		User user = userService.getCurrentUser();
		boolean isSetPerson = classSetPersonService.isSetPerson(user.getEmployee().getId());
    	request.setAttribute("isSetPerson",isSetPerson);
    	boolean isDh = departService.checkLeaderIsDh(user.getEmployee().getId());
    	request.setAttribute("isDh",isDh);
    	Integer allRunCount = 4;
    	if(user.getUserName().equals("admin")) {
    		request.setAttribute("ruCode30", "30");
    		request.setAttribute("ruCode100", "100");
    		request.setAttribute("depart", "107");
    		request.setAttribute("isSetPerson", true);
    		request.setAttribute("isDh",true);
    	} else if(user.getEmployee().getWorkType() != null) {
    		CompanyConfig config = new CompanyConfig();
    		config.setId(user.getEmployee().getWorkType());
    		config = companyConfigService.getByCondition(config);
    		if(config!=null&&"standard".equals(config.getDisplayCode())) {
    			allRunCount++;
    			if(OaCommon.getRunCode100Map().containsKey(user.getDepart().getCode())) {
    				request.setAttribute("ruCode100", "100");
    				allRunCount++;
    			}
    		}
    	}
    	if("??????????????????".equals(user.getPosition().getPositionName())||"????????????????????????".equals(user.getPosition().getPositionName())||"HRBP??????".equals(user.getPosition().getPositionName())||"???????????????????????????".equals(user.getPosition().getPositionName())||"??????????????????".equals(user.getPosition().getPositionName())
    			||"??????HRBP".equals(user.getPosition().getPositionName())||"HRBP".equals(user.getPosition().getPositionName())
    			||"HRBP??????".equals(user.getPosition().getPositionName())||"??????????????????".equals(user.getPosition().getPositionName())
    			||"????????????????????????".equals(user.getPosition().getPositionName())||"????????????????????????".equals(user.getPosition().getPositionName())){
    		request.setAttribute("depart", "107");
    	}
    	if(user.getEmployee().getWorkAddressType()==1){
    		allRunCount = 2;
    	}
    	request.setAttribute("workAddressType", user.getEmployee().getWorkAddressType());
    	request.setAttribute("workAddressProvince", user.getEmployee().getWorkAddressProvince());
		request.setAttribute("allRunCount", allRunCount);
		return "index/allRun";
	}
    
   @ResponseBody
   @RequestMapping("getLogoAsync")
   public Map<String,String> getLogoAsync(Company company){
	   Map map = new HashMap<String, String>();
	   
	   company = companyService.getByCondition(company);
	   String code = company.getCode();
	   
	   /**??????code????????????**/
	   if(code == null){//??????????????????ule
		  code = "ULE";
	   }
	   
	   if(code.equalsIgnoreCase("ULE")){
		   map.put("imgSrc", "/ule/oa/i/hgc-icon-logo.png");
	   }else if (code.equalsIgnoreCase("TOM")) {
		   map.put("imgSrc", "/ule/oa/i/hgc-icon-tom2.png");
	   }
	   
	   return map;
   }
   
    /**
     * index(???????????????)
     * @Title: index
     * @Description: ???????????????
     * @return    ????????????
     * String    ????????????
     * @throws
    */
   @RequestMapping("/index1.htm")
   public String index1(){
   	return "index/main1";
   }
    
    /**
      * getLoginFailureInfo(?????????????????????????????????????????????)
      * @Title: getLoginFailureInfo
      * @Description: ????????????????????????
      * @param request
      * @return    ????????????
      * String    ????????????
      * @throws
     */
    private String getLoginFailureInfo(HttpServletRequest request) {
        String exceptionClassName = (String) request.getAttribute("shiroLoginFailure");
        if (null == exceptionClassName) {
            return null;
        }
        if (UnknownAccountException.class.getName().equals(exceptionClassName)) {
            return "?????????/????????????";
        } else if (IncorrectCredentialsException.class.getName().equals(exceptionClassName)) {
            String passportError = (String) request.getAttribute("passportError");
        	return passportError;
        } else {
            return "????????????";
        }
    }
    
    
}
