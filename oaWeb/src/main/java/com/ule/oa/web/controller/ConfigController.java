package com.ule.oa.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.ule.oa.base.po.AnnualVacation;
import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.EmployeeDuty;
import com.ule.oa.base.service.AnnualVacationService;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.EmployeeDutyService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.JSON;
import com.ule.oa.common.utils.PageModel;
import com.ule.oa.common.utils.RedisUtils;

@Controller
@RequestMapping("sysConfig")
public class ConfigController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private ConfigService configService;
	@Autowired
	private AnnualVacationService annualVacationService;
	@Autowired
	private EmployeeDutyService employeeDutyService;
	@Autowired
	private UserService userService;

	
	@RequestMapping("/index.htm")
	public ModelAndView index(){
		return new ModelAndView("base/sysConfig/sysConfig");
	}
	
	@ResponseBody
	@RequestMapping("/pageList.htm")
	public PageModel<Config> whList(Config sysConfig){
		PageModel<Config> pm=new PageModel<Config>();
		pm.setRows(new java.util.ArrayList<Config>());
		pm.setTotal(0);
		pm.setPageNo(1);
		try {
			pm = configService.getByPagenation(sysConfig);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return pm;
	}
	
	@ResponseBody
	@RequestMapping("/update.htm")
	public JSON update(HttpServletRequest request,Config sysConfig,String phone,String validateCode){
		JSON json=new JSON(false, null, null);
		
		try {
			
			sysConfig.setUpdateTime(DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_LONG), DateUtils.FORMAT_LONG));
			String phoneKey = "VALIDATA_CODE_"+phone;
			String realCode = RedisUtils.getString(phoneKey);
			if(null != realCode && validateCode.equals(realCode)){
				sysConfig.setUpdateUser(phone);
				configService.updateById(sysConfig);
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
			String code = configService.getPhoneValidateCode(phone,second);
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
	@RequestMapping("getListByCondition")
	public List<Config> getListByCondition(Config config){
		return configService.getListByCondition(config);
	}
	
	@ResponseBody
	@RequestMapping("/vacation.htm")
	public List<Map> isWork(HttpServletRequest request){
		List<Map> map = new ArrayList<Map>();
		String year = request.getParameter("year");
		if(null == year){
			year = DateUtils.getYear(DateUtils.getToday());
		}
		//获取假期数据
		AnnualVacation vacation = new AnnualVacation();
		vacation.setStartDate(DateUtils.parse(year+"-"+"01-01", DateUtils.FORMAT_SHORT));
		vacation.setEndDate(DateUtils.parse(year+"-"+"12-31", DateUtils.FORMAT_SHORT));
		List<AnnualVacation> list = annualVacationService.getListByCondition(vacation);
		//获取值班数据
		EmployeeDuty dutyP = new EmployeeDuty();
		dutyP.setEmployId(userService.getCurrentUser().getEmployee().getId());
		dutyP.setYear(year);
		List<EmployeeDuty> dutyList = employeeDutyService.selectByCondition(dutyP);
		Map<Date,EmployeeDuty> dutyMap = new HashMap<Date,EmployeeDuty>();
		for(EmployeeDuty duty:dutyList){
			Map va = new HashMap();
			va.put("day", DateUtils.format(duty.getDutyDate(), DateUtils.FORMAT_SHORT));
			va.put("type", 1);
			va.put("subject","值");
			va.put("workType",5);
			va.put("source",1);
			map.add(va);
			dutyMap.put(duty.getDutyDate(), duty);
		}
		if(list!=null&&list.size()>0){
			for(AnnualVacation v:list){
				Map va = new HashMap();
				if(v.getType().intValue()==AnnualVacation.YYPE_WORK.intValue()){
					//上班
					if(dutyMap==null||!dutyMap.containsKey(v.getAnnualDate())){
						va.put("day", DateUtils.format(v.getAnnualDate(), DateUtils.FORMAT_SHORT));
						va.put("type", 1);
						va.put("subject",v.getSubject());
						va.put("workType",v.getType());
						va.put("source",0);
						map.add(va);
					}
				}else{
					//不上班
					if(dutyMap==null||!dutyMap.containsKey(v.getAnnualDate())){
						va.put("day",DateUtils.format(v.getAnnualDate(), DateUtils.FORMAT_SHORT));
						va.put("type", 0);
						va.put("subject",v.getSubject());
						va.put("workType",v.getType());
						va.put("source",0);
						map.add(va);
					}else if(dutyMap!=null&&dutyMap.containsKey(v.getAnnualDate())){
						for(int i=0;i<map.size();i++){
							if(DateUtils.format(v.getAnnualDate(), DateUtils.FORMAT_SHORT).equals(map.get(i).get("day").toString())){
								map.get(i).put("subject",v.getSubject());
							}
						}
					}
				}
			}
		}
		return map;
	}
     
}
