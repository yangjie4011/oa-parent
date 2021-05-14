package com.ule.oa.service.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ule.oa.base.service.AttnSignRecordService;
import com.ule.oa.common.cache.distlock.DistLockUtil;
import com.ule.oa.common.utils.http.IPUtils;

/**
  * @ClassName: AttnSignRecordController
  * @Description: 打卡记录控制层
  * @author minsheng
  * @date 2017年11月8日 下午3:15:52
 */
@Controller
@RequestMapping("attnSignRecord")
public class AttnSignRecordController {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private AttnSignRecordService attnSignRecordService;
	
	/**
	 * @throws Exception 
	  * signRecordExMsgRemind(晚到提醒)
	  * @Title: signRecordExMsgRemind
	  * @Description: 综合工时晚到提醒
	  * void    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/signRecordExMsgRemind.htm")
	public Map<String, String> signRecordExMsgRemind(HttpServletRequest request){
		String lockValue = DistLockUtil.lock("signRecordExMsgRemind",60*5L);
		Map<String, String> map = new HashMap<String, String>();
		if(StringUtils.isBlank(lockValue)){
			logger.info("晚到提醒定时已经启动，请不要重复调用当前定时!!!");
			map.put("response", "请勿重复调用当前定时!!!");
			return map;
		}else{//锁定定时
			logger.info("晚到提醒定时启动,请求方IP={}",IPUtils.getIpAddress(request));
			attnSignRecordService.signRecordExMsgRemind();
			//旷工定时
			attnSignRecordService.absenteeismAlert();
			map.put("response", "signRecordExMsgRemind触发成功,请稍后查看数据！");
			return map;
		}
	}
	
}
