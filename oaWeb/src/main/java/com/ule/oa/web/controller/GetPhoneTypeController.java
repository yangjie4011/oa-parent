package com.ule.oa.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ule.oa.common.exception.OaException;

@Controller
@RequestMapping("phoneType")
public class GetPhoneTypeController {
	
	@RequestMapping("/getPhoneType.htm")
	public String index(HttpServletRequest request) throws OaException{
		return "common/getPhoneType";
	}
}
