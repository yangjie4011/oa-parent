package com.ule.oa.service.kafka.handler;

import java.util.Date;
import java.util.List;
import java.util.Map;

import kafka.utils.VerifiableProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ule.oa.base.po.AttnWorkHours;
import com.ule.oa.base.service.AttnWorkHoursService;
import com.ule.oa.common.spring.SpringContextUtils;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.json.JSONUtils;
import com.ule.tools.client.kafka.consumer.handler.AbstractConsumerHandler;

public class AttnWorkHoursHandler extends AbstractConsumerHandler<String>{
	private Logger logger = LoggerFactory.getLogger(AttnWorkHoursHandler.class);

	public AttnWorkHoursHandler(VerifiableProperties props) {
		super(props);
	}

	public void handle(String key, String orderJson) {
		
		logger.info("[AttnWorkHoursHandler:handle][INVOKE]param orderData{}",orderJson);
		long start = System.currentTimeMillis();

		try {
			Map<?, ?> map = JSONUtils.readAsMap(orderJson);
			AttnWorkHours attnWorkHours = new AttnWorkHours();
			
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
			
			attnWorkHours.setStartTime(startTime);
			attnWorkHours.setEndTime(endTime);
			attnWorkHours.setEmployeeIds(ids);
			AttnWorkHoursService service = SpringContextUtils.getContext().getBean(AttnWorkHoursService.class);
			service.setWorkHours(attnWorkHours);
		} catch (Exception e) {
			logger.error("[AttnWorkHoursHandler:handle]error consumer wait attn workHours from oa", e);
		}
		
		long end = System.currentTimeMillis();
		logger.info("[AttnWorkHoursHandler:handle][END]cost:{}",(end - start));
	}

}
