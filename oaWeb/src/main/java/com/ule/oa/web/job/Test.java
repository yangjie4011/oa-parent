package com.ule.oa.web.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
  * @ClassName: Test
  * @Description: 定时任务测试类
  * @author minsheng
  * @date 2017年7月6日 上午10:22:50
 */
@Component
public class Test {
	
	@Scheduled(cron = "0 0/1 * * * ? ")
	public void test(){
//		System.out.println("1234");
	}
}
