package com.ule.oa.service.kafka.handler;

import java.util.Date;
import java.util.List;
import java.util.Map;

import kafka.utils.VerifiableProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ule.oa.base.po.AttnStatistics;
import com.ule.oa.base.service.AttnStatisticsService;
import com.ule.oa.common.spring.SpringContextUtils;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.json.JSONUtils;
import com.ule.tools.client.kafka.consumer.handler.AbstractConsumerHandler;

public class AttnStatisticsHandler  extends AbstractConsumerHandler<String>{
	private Logger logger = LoggerFactory.getLogger(AttnStatisticsHandler.class);

	public AttnStatisticsHandler(VerifiableProperties props) {
		super(props);
	}

	public void handle(String key, String orderJson) {
		
		logger.info("[AttnStatisticsHandler:handle][INVOKE]param orderData{}",orderJson);
		long start = System.currentTimeMillis();

		try {
			Map<?, ?> map = JSONUtils.readAsMap(orderJson);
			AttnStatistics attnStatistics = new AttnStatistics();
			
			Date startTime = null;
			Date endTime = null;
			List<Long> ids = null;
			if(null != map.get("beginDate")){
				String beginDate = String.valueOf(map.get("beginDate"));
				startTime = DateUtils.parse(beginDate, DateUtils.FORMAT_SHORT);
			}
			if(null != map.get("endDate")){
				String endDate = String.valueOf(map.get("endDate"));
				endTime = DateUtils.parse(endDate, DateUtils.FORMAT_SHORT);
			}
			if(null != map.get("ids")){
				ids = (List<Long>) map.get("ids");
			}
			
			attnStatistics.setEmployeeIds(ids);
			attnStatistics.setStartTime(startTime);
			attnStatistics.setEndTime(endTime);
			AttnStatisticsService service = SpringContextUtils.getContext().getBean(AttnStatisticsService.class);
			service.setAttStatistics(attnStatistics);
		} catch (Exception e) {
			logger.error("[AttnStatisticsHandler:handle]error consumer wait attn statistics from oa", e);
		}
		
		long end = System.currentTimeMillis();
		logger.info("[AttnStatisticsHandler:handle][END]cost:{}",(end - start));
	}

}