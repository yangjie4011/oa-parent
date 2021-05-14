package com.ule.oa.web.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.AttnSignRecordService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.RedisUtils;
import com.ule.oa.common.utils.http.IPUtils;
import com.ule.oa.common.utils.json.JSONUtils;
import com.ule.oa.web.util.CardSignature;
import com.ule.oa.web.util.ReturnUrlUtil;

/** 
 * @ClassName: 定位签到
* @Description: 定位签到
* @author yangjie
* @date 2020年02月05日
*/
@Controller
@RequestMapping("locationCheckIn")
public class LocationCheckInController {
	
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
	private AttnSignRecordService attnSignRecordService;
    @Autowired
	private UserService userService;
    @Autowired
    private EmployeeService employeeService;
    
    //定位签到首页
	@RequestMapping("/index.htm")
	public ModelAndView index(HttpServletRequest request, String urlType){
		ModelAndView mv = new ModelAndView("base/locationCheckIn/index");// 微信定位页面
		Date time  = new Date();
		User user = userService.getCurrentUser();
		mv.addObject("otherPlace", false);
		try {
			mv.addObject("oaDate",DateUtils.format(time, DateUtils.FORMAT_SHORT_CN));
			mv.addObject("oaTime",DateUtils.format(time, DateUtils.FORMAT_HH_MM));
			mv.addObject("returnUrl",ReturnUrlUtil.getReturnUrl(urlType));
			Employee emp = employeeService.getById(user.getEmployee().getId());
			
			if(emp.getWorkAddressType()!=null&&emp.getWorkAddressType().intValue()==1){
     			mv.addObject("otherPlace", true);
     		}
		} catch (Exception e) {
			return mv;
		}
		return mv;
	}
	
	@ResponseBody 
	@RequestMapping(value = "/getWxConfig.htm", produces = "text/json;charset=UTF-8")
	public void getWxConfig(HttpServletRequest request,HttpServletResponse response) throws IOException{
        response.setContentType("text/plain");  
        response.setHeader("Pragma", "No-cache");  
        response.setHeader("Cache-Control", "no-cache");  
        response.setDateHeader("Expires", 0);  
        Map<String, Object> map = new HashMap<String, Object>();
        String appid = "wx572a19b1a0e16d30";//公众号appid
		try {
			map.put("config", CardSignature.getSignature(request, appid,""));
			map.put("success", true);
		} catch (Exception e) {
			map.put("success", false);
		}
        PrintWriter out = response.getWriter();       
        String jsonpCallback = request.getParameter("jsonpCallback");//客户端请求参数  
        out.println(jsonpCallback+"("+JSONUtils.write(map)+")");//返回jsonp格式数据  
        out.flush();  
        out.close();  
	}
	
	@ResponseBody 
	@RequestMapping(value = "/getWxConfig1.htm")
	public Map<String, Object> getWxConfig1(HttpServletRequest request,String url) throws IOException{
		Map<String, Object> map = new HashMap<String, Object>();
        String appid = "wx572a19b1a0e16d30";//公众号appid
		try {
			map.put("config", CardSignature.getSignature(request, appid,url));
			map.put("success", true);
		} catch (Exception e) {
			map.put("success", false);
		}
		return map;
	}
	
	//签到
	@ResponseBody
	@RequestMapping("/checkIn.htm")
	//@Token(remove = true)
	public Map<String, Object> save(HttpServletRequest request,String locationResult) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			String ip = IPUtils.getIpAddress(request);
			map = attnSignRecordService.locationCheckIn(ip,locationResult);
		} catch (Exception e) {
			map.put("message", e.getMessage());
			map.put("success", false);
			return map;
		}
		return map;
	}
	
	//清除签到定位所需的缓存校验数据
	@ResponseBody
	@RequestMapping("/removeRedis.htm")
	//@Token(remove = true)
	public Map<String, Object> removeRedis(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			RedisUtils.delete("WX_ACCESS_TOKEN");
			RedisUtils.delete("WX_JSAPI_TICKECT");
		} catch (Exception e) {
			map.put("message", e.getMessage());
			map.put("success", false);
			return map;
		}
		return map;
	}
	
	@ResponseBody
	@RequestMapping("/getWXRedis.htm")
	public Map<String, Object> getWXRedis(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map.put("WX_ACCESS_TOKEN", RedisUtils.getString("WX_ACCESS_TOKEN"));
			map.put("WX_JSAPI_TICKECT", RedisUtils.getString("WX_JSAPI_TICKECT"));
		} catch (Exception e) {
			map.put("message", e.getMessage());
			map.put("success", false);
			return map;
		}
		return map;
	}

}
