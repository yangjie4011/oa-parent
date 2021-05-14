 /**
 * @Title: AttnWorkHoursController.java
 * @Package com.ule.oa.service.web.controller
 * @Description: TODO
 * @Copyright: Copyright (c) 2017
 * @Company:邮乐网 *
 * @author zhangjintao
 * @date 2017年6月16日 上午10:02:10
 */

package com.ule.oa.service.web.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ule.oa.base.service.AttnWorkHoursService;
/**
 * @ClassName: AttnWorkHoursController
 * @Description: 考勤打卡数据汇总
 * @author zhangjintao
 * @date 2017年6月16日 上午10:02:10
 */

@Controller
@RequestMapping("attnWorkHours")
public class AttnWorkHoursController {
	
	@Resource
	private AttnWorkHoursService attnWorkHoursService;
	
	@RequestMapping("/setWorkHours.htm")
	public void setAttnSignRecord(){
		
		attnWorkHoursService.setWorkHours(null);
	}

}
