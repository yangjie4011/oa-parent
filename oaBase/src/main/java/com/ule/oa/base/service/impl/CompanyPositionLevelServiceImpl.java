package com.ule.oa.base.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ule.oa.base.mapper.CompanyPositionLevelMapper;
import com.ule.oa.base.po.CompanyPositionLevel;
import com.ule.oa.base.service.CompanyPositionLevelService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.PageModel;

@Service
public class CompanyPositionLevelServiceImpl implements CompanyPositionLevelService{
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private CompanyPositionLevelMapper companyPositionLevelMapper;

	@Override
	public Integer save(CompanyPositionLevel companyPositionLevel) {
		Date updateTime = DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_LONG), DateUtils.FORMAT_LONG);		
		companyPositionLevel.setUpdateTime(updateTime);
		companyPositionLevel.setCreateTime(updateTime);
		companyPositionLevel.setDelFlag(0);
		return companyPositionLevelMapper.insert(companyPositionLevel);
	}

	@Override
	public Integer delete(long id){
		CompanyPositionLevel companyPositionLevel =new CompanyPositionLevel();
		companyPositionLevel.setId(id);
		companyPositionLevel.setDelFlag(1);		
		Date updateTime = DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_LONG), DateUtils.FORMAT_LONG);
		companyPositionLevel.setUpdateTime(updateTime);
		return companyPositionLevelMapper.updatePositionLevelById(companyPositionLevel);
	}

	@Override
	public Integer updateById(CompanyPositionLevel companyPositionLevel) throws Exception {
		int updateCount = companyPositionLevelMapper.updateById(companyPositionLevel);
		
		if(updateCount == 0){
			throw new OaException("您当前修改的员工职位信息已经被其它人修改过，请重新编辑");
		}
		
		return updateCount;
	}

	@Override
	public PageModel<CompanyPositionLevel> getByPagenation(CompanyPositionLevel companyPositionLevel) {
		int page = companyPositionLevel.getPage() == null ? 0 : companyPositionLevel.getPage();
		int rows = companyPositionLevel.getRows() == null ? 0 : companyPositionLevel.getRows();
		
		PageModel<CompanyPositionLevel> pm = new PageModel<CompanyPositionLevel>();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		int total = companyPositionLevelMapper.count(companyPositionLevel);
		pm.setTotal(total);
		companyPositionLevel.setOffset(pm.getOffset());
		companyPositionLevel.setLimit(pm.getLimit());
		List<CompanyPositionLevel> companyPositionLevels=companyPositionLevelMapper.getByPagenation(companyPositionLevel);
		pm.setRows(companyPositionLevels);
		return pm;
	}

	@Override
	public List<CompanyPositionLevel> getListByCondition(CompanyPositionLevel companyPositionLevel){
		return companyPositionLevelMapper.getListByCondition(companyPositionLevel);
	}
	
	@Override
	public CompanyPositionLevel getByCondition(CompanyPositionLevel companyPositionLevel){
		List<CompanyPositionLevel> list =  companyPositionLevelMapper.getListByCondition(companyPositionLevel);
		
		if(null != list && list.size()>0){
			return list.get(0);
		}
		
		return companyPositionLevel;
	}
	
	@Override
	public CompanyPositionLevel getById(Long id){
		return companyPositionLevelMapper.getById(id);
	}

	@Override
	public int queryCode(String code,int id) {
		return companyPositionLevelMapper.queryCode(code,id);
	}

	@Override
	public int queryName(String name,int id) {
		return companyPositionLevelMapper.queryName(name,id);
	}

	@Override
	public Integer updatePositionLevelById(
			CompanyPositionLevel companyPositionLevel) throws Exception {
		Date updateTime = DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_LONG), DateUtils.FORMAT_LONG);
		companyPositionLevel.setUpdateTime(updateTime);
		return companyPositionLevelMapper.updatePositionLevelById(companyPositionLevel);
	}
}
