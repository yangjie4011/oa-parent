package com.ule.oa.service.kafka.handler;

import java.util.Date;
import java.util.Map;

import kafka.utils.VerifiableProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ule.oa.base.po.AttnTaskRecord;
import com.ule.oa.base.service.AttnTaskRecordService;
import com.ule.oa.common.spring.SpringContextUtils;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.json.JSONUtils;
import com.ule.tools.client.kafka.consumer.handler.AbstractConsumerHandler;

public class AttnTaskRecordHandler  extends AbstractConsumerHandler<String>{
	private Logger logger = LoggerFactory.getLogger(AttnTaskRecordHandler.class);

	public AttnTaskRecordHandler(VerifiableProperties props) {
		super(props);
	}

	public void handle(String key, String orderJson) {
		
		logger.info("[AttnTaskRecordHandler][INVOKE]param orderData{}",orderJson);
		long start = System.currentTimeMillis();

		try {
			Map<?, ?> map = JSONUtils.readAsMap(orderJson);
			AttnTaskRecord attnTaskRecord = new AttnTaskRecord();
			
			Date startTime = null;
			Date endTime = null;
			String employeeIds = null;
			if(null != map.get("beginDate")){
				String beginDate = String.valueOf(map.get("beginDate"));
				startTime = DateUtils.parse(beginDate, DateUtils.FORMAT_SHORT);
			}
			if(null != map.get("endDate")){
				String endDate = String.valueOf(map.get("endDate"));
				endTime = DateUtils.parse(endDate, DateUtils.FORMAT_SHORT);
			}
			if(null != map.get("employeeIds")){
				employeeIds = String.valueOf(map.get("employeeIds"));
			}
			
			attnTaskRecord.setStartTime(startTime);
			attnTaskRecord.setEndTime(endTime);
			attnTaskRecord.setEmployeeIds(employeeIds);
			AttnTaskRecordService service = SpringContextUtils.getContext().getBean(AttnTaskRecordService.class);
			service.setTaskRecord(attnTaskRecord);
		} catch (Exception e) {
			logger.error("[AttnTaskRecordHandler:handle]error consumer wait attn statistics from oa", e);
		}
		
		long end = System.currentTimeMillis();
		logger.info("[AttnTaskRecordHandler:handle][END]cost:{}",(end - start));
	}
}
