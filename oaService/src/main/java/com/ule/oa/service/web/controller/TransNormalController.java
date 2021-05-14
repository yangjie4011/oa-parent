package com.ule.oa.service.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ule.oa.base.po.TransNormal;
import com.ule.oa.base.service.AttnWorkHoursService;
import com.ule.oa.base.service.TransNormalDataService;
import com.ule.oa.base.service.TransNormalService;
import com.ule.oa.common.utils.json.JSONUtils;

@Controller
@RequestMapping("transNormal")
public class TransNormalController {
	private Logger logger = LoggerFactory.getLogger(TransNormalController.class);

	@Resource
	private TransNormalService transNormalService;
	
	@Resource
	private AttnWorkHoursService attnWorkHoursService;
	
	@Resource
	private TransNormalDataService transNormalDataService;
	
	/**
	  * 定时统计考勤 
	  * setAttnSignRecord(这里用一句话描述这个方法的作用)
	  * @Title: setAttnSignRecord
	  * void    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/setAttnSignRecord.htm")
	public Map<String, String> setAttnSignRecord(){
		
		transNormalService.setAttnSignRecord(null);
		Map<String, String> map = new HashMap<String, String>();
		map.put("response", "setAttnSignRecord触发成功,请稍后查看数据！");
		return map;
	}
	
	/**
	  * startAttnByTime(根据时间跑考勤规则数据)
	  * @Title: startAttnByTime
	  * @Description: 根据时间跑考勤规则数据
	  * @param transNormal
	  * @return    设定文件
	  * Map<String,String>    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/startAttnByTime.htm")
	public Map<String, String> startAttnByTime(HttpServletRequest request){
		final String data = request.getParameter("data");
		
		new Thread(new Runnable() {
			public void run() {
				try{
					logger.info("手动触发考勤规则,para={}",data);
					TransNormal transNormal = JSONUtils.read(data, TransNormal.class);
					
					transNormalService.startAttnByTime(transNormal);
				}catch(Exception e){
					logger.error("异步触发出错了");
				}
			}
		}).start();
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("response", "触发成功,请稍后查看数据！");
		return map;
	}
	
	/**
	  * getListByCardId(根据指纹id获取打卡数据)
	  * @Title: getListByCardId
	  * @Description: 根据指纹id获取打卡数据
	  * @param transNormal
	  * @return    设定文件
	  * List<TransNormal>    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/getListByCardId.htm")
	public Map<String,Object> getListByCardId(HttpServletRequest request){
	    String data = request.getParameter("data");
	    Map<String,Object> result = new HashMap<String,Object>();
		List<TransNormal>  list = new ArrayList<TransNormal>();
	    try{
			logger.info("根据指纹id查询打卡记录,para={}",data);
			TransNormal transNormal = JSONUtils.read(data, TransNormal.class);
			list = transNormalDataService.getListByCardId(transNormal);
			result.put("success", true);
			result.put("data", list);
		}catch(Exception e){
			result.put("success", false);
			logger.error("根据指纹id查询打卡记录失败:"+e.getMessage());
			return result;
		}
		return result;
	}

}
