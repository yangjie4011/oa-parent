package com.ule.oa.service.web.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ule.oa.base.service.AttentanceSetService;
import com.ule.oa.base.service.AttnStatisticsService;
import com.ule.oa.base.service.EmpApplicationOvertimeService;
import com.ule.oa.common.cache.distlock.DistLockUtil;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.http.IPUtils;



@Controller
@RequestMapping("attnStatistics")
public class AttnStatisticsController {	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private AttnStatisticsService attnStatisticsService;
	@Resource
	private EmpApplicationOvertimeService empApplicationOvertimeService;
	@Resource
	private AttentanceSetService attentanceSetService;
	
	@RequestMapping("/setAttStatistics.htm")
	public void setAttStatistics(){
		
		attnStatisticsService.setAttStatistics(null);
	}

	/**
	 * @throws Exception 
	  * attnExMsgRemind(异常考勤提醒(每月一号提醒一次))
	  * @Title: attnExMsgRemind
	  * @Description: 异常考勤提醒(每月一号提醒一次)
	  * void    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/attnExMsgRemind.htm")
	public Map<String, String> attnExMsgRemind(HttpServletRequest request) throws Exception{
		String lockValue = DistLockUtil.lock("attnExMsgRemind",60*5L);
		Map<String, String> map = new HashMap<String, String>();
		if(StringUtils.isBlank(lockValue)){
			logger.info("异常考勤提醒定时已经启动，请不要重复调用当前定时!!!");
			map.put("response", "请勿重复调用当前定时!!!");
			return map;
		}else{//锁定定时
			logger.info("异常考勤提醒定时启动,请求方IP={}",IPUtils.getIpAddress(request));
			try{
				attnStatisticsService.attnExMsgRemind();
				map.put("response", "attnExMsgRemind触发成功,请稍后查看数据！");
			}catch(Exception e){
				
			}
			try{
				//法定节假日填写加班提醒
				empApplicationOvertimeService.remindWriteOvertimeApply();
			}catch(Exception e1){
				
			}
			
//			try {
//				String month = DateUtils.format(new Date(), "yyyy-MM");
//				attentanceSetService.initAllEmployClass(month);
//			} catch (Exception e) {
//				logger.error("initAllEmployClass:"+e.getMessage());
//			}
			return map;
		}
	}
}
