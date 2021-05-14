package com.ule.oa.web.controller;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ule.oa.base.po.SendMail;
import com.ule.oa.base.service.SendMailService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.json.JSONUtils;

/**
  * oa发送邮件
  * @ClassName: SendMailController
  * @author wufei
  * @date 2017年6月30日 下午1:53:28
 */
@Controller
@RequestMapping("/sendMail")
public class SendMailController {
	
	@Autowired
	private SendMailService sendMailService;
	
	@RequestMapping("/index.htm")
	public String index(){
		return "base/sendMail";
	}
	
	/**
	 * 发送邮件
	 * @param request
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/list.htm")
	public String list(HttpServletRequest request, SendMail sendMail){
		return JSONUtils.write(null);
	}
	
	/**
	 * 测试发邮件
	 * @param request
	 * @param sendMail
	 * @throws OaException
	 */
	@ResponseBody
	@RequestMapping(value = "/testSendMail.htm", produces = "text/json;charset=UTF-8")
	public String testSendMail(HttpServletRequest request) throws OaException{
		Map<String, Object> returnMap = new HashMap<String,Object>();
		sendMailService.definiteSendMail();
		returnMap.put("code", "0000");
		returnMap.put("msg", "发送成功");
		return JSONUtils.write(returnMap);
	}
}
