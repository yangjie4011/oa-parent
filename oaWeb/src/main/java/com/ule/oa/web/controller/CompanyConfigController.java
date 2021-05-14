package com.ule.oa.web.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ule.oa.base.po.CompanyConfig;
import com.ule.oa.base.service.CompanyConfigService;
import com.ule.oa.base.service.CompanyService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.JSON;
import com.ule.oa.common.utils.PageModel;
import com.ule.oa.common.utils.RedisUtils;

@Controller
@RequestMapping("companyConfig")
public class CompanyConfigController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private CompanyConfigService companyConfigService;
	
	@Resource
	private CompanyService companyService;
	
	@RequestMapping("/index.htm")
	public ModelAndView index(){
		return new ModelAndView("companyConfig/companyConfig");
	}
	
	
	@ResponseBody
	@RequestMapping("/pageList.htm")
	public PageModel<CompanyConfig> companyConfigList(CompanyConfig companyConfig){
		PageModel<CompanyConfig> pm=new PageModel<CompanyConfig>();
		pm.setRows(new java.util.ArrayList<CompanyConfig>());
		pm.setTotal(0);
		pm.setPageNo(1);
		try {
			pm = companyConfigService.getByPagenation(companyConfig);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return pm;
	}
	
	@ResponseBody
	@RequestMapping("/update.htm")
	public JSON update(HttpServletRequest request,CompanyConfig companyConfig,String phone,String validateCode){
		JSON json=new JSON(false, null, null);
		
		try {
			
			companyConfig.setUpdateTime(DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_LONG), DateUtils.FORMAT_LONG));
			String phoneKey = "VALIDATA_CODE_"+phone;
			String realCode = RedisUtils.getString(phoneKey);
			if(null != realCode && validateCode.equals(realCode)){
				companyConfig.setUpdateUser(phone);
				companyConfigService.updateById(companyConfig);
				RedisUtils.delete(phoneKey);
				json.setSuccess(true);
				json.setMessage("修改成功!");
				logger.info("配置修改成功,手机号为[{}]", phone);
			}else{
				logger.error("手机验证码不正确！");
				json.setMessage("手机验证码不正确！");
			}
			
		} catch (OaException e) {
			logger.error(e.getMessage(), e);
			json.setMessage(e.getMessage());
		} catch (Exception e1) {
			logger.error(e1.getMessage(), e1);
			json.setMessage(e1.getMessage());
		}
		return json;
	}
	
	@ResponseBody
	@RequestMapping("/getValidateCode.htm")
	public JSON getValidateCode(String phone){
		Integer second = 600; 
		JSON json=new JSON(false, null, null);
		try {
			String code = companyConfigService.getPhoneValidateCode(phone,second);
			RedisUtils.setString("VALIDATA_CODE_"+phone, code,second);
			
			json.setSuccess(true);
			json.setResult(phone);
			json.setMessage("验证码已发送!");
			logger.info("本次操作验证码为[{}]", code);
		} catch (OaException e) {
			logger.error(e.getMessage(), e);
			json.setMessage(e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.setMessage(e.getMessage());
		}
		return json;
	}
	
	@ResponseBody
	@RequestMapping("/getListByCondition.htm")
	public List<CompanyConfig> getListByCondition(CompanyConfig companyConfig){
		return companyConfigService.getListByCondition(companyConfig);
	}
	
	@ResponseBody
	@RequestMapping("/getByCondition.htm")
	public CompanyConfig getByCondition(CompanyConfig companyConfig){
		return companyConfigService.getByCondition(companyConfig);
	}
}
