package com.ule.oa.base.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ule.oa.base.mapper.QuitHistoryMapper;
import com.ule.oa.base.po.QuitHistory;
import com.ule.oa.base.service.QuitHistoryService;


//离职历史表
@Service
public class QuitHistoryServiceImpl implements QuitHistoryService {
	
	@Autowired
	private QuitHistoryMapper quitHistoryMapper;

	@Override
	public int save(QuitHistory quitHistory) {
		return quitHistoryMapper.save(quitHistory);
	}

}
