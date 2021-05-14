package com.ule.oa.base.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ule.oa.base.mapper.TransNormalMapper;
import com.ule.oa.base.po.TransNormal;
import com.ule.oa.base.service.TransNormalDataService;

@Service
public class TransNormalDataServiceImpl implements TransNormalDataService{
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource
	private TransNormalMapper transNormalMapper;

	@Override
	public List<TransNormal> getTransNormalList(TransNormal transNormal) {
		return transNormalMapper.getTransNormalList(transNormal);
	}

	@Override
	public Integer getTotalRows(TransNormal transNormal) {
		return transNormalMapper.getTotalRows(transNormal);//查询总行数;
	}

	@Override
	public List<TransNormal> getListByCardId(TransNormal transNormal) {
		return transNormalMapper.getListByCardId(transNormal);
	}

}
