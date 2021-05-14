package com.ule.oa.base.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ule.oa.base.mapper.CompanySeatMapper;
import com.ule.oa.base.po.CompanySeat;
import com.ule.oa.base.service.CompanySeatService;

/**
  * @ClassName: CompanyServiceImpl
  * @Description: 公司业务层
  * @author minsheng
  * @date 2017年5月8日 下午4:37:23
 */
@Service
public class CompanySeatServiceImpl implements CompanySeatService {
	@Autowired
	private CompanySeatMapper companySeatMapper;

	@Override
	public List<CompanySeat> getListByCondition(CompanySeat model) {
		return companySeatMapper.getListByCondition(model);
	}
}
