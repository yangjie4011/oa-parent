package com.ule.oa.base.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ule.oa.base.mapper.CoopCompanyMapper;
import com.ule.oa.base.po.CoopCompany;
import com.ule.oa.base.service.CoopCompanyService;

/**
  * @ClassName: CoopCompanyServiceImpl
  * @Description: 合作公司业务层
  * @author minsheng
  * @date 2017年5月19日 下午5:42:19
 */
@Service
public class CoopCompanyServiceImpl implements CoopCompanyService {
	@Autowired
	private CoopCompanyMapper coopCompanyMapper;
	
	@Override
	public int save(CoopCompany coopCompany){
		return coopCompanyMapper.save(coopCompany);
	}

	@Override
	public int updateById(CoopCompany coopCompany){
		return coopCompanyMapper.updateById(coopCompany);
	}
	
	@Override
	public List<CoopCompany> getListByCondition(CoopCompany coopCompany){
		return coopCompanyMapper.getListByCondition(coopCompany);
	}
	
	@Override
	public CoopCompany getByCondition(CoopCompany coopCompany){
		List<CoopCompany> list = getListByCondition(coopCompany);
		
		if(null != list && list.size()>0){
			return list.get(0);
		}
		
		return coopCompany;
	}
}
