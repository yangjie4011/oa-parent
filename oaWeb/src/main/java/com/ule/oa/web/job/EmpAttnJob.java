 /**
 * @Title: EmpAttnJob.java
 * @Package com.ule.oa.web.job
 * @Description: TODO
 * @Copyright: Copyright (c) 2017
 * @Company:邮乐网 *
 * @author zhangjintao
 * @date 2017年7月6日 下午2:30:55
 */

package com.ule.oa.web.job;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ule.oa.base.service.TransNormalService;

 /**
 * @ClassName: EmpAttnJob
 * @Description: 每天定时计算考勤
 * @author zhangjintao
 * @date 2017年7月6日 下午2:30:55
 */

@Component
public class EmpAttnJob {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private TransNormalService transNormalService;
	
	/*@Scheduled(cron = "0 0 8 * * ? ")//每天早上8点
	public void run(){
		
		logger.info("考勤计算开始...");
		transNormalService.setAttnSignRecord(null);
	}*/

}
