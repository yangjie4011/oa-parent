package com.ule.oa.base.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ule.oa.base.mapper.CompanyPositionSeqMapper;
import com.ule.oa.base.po.CompanyPositionSeq;
import com.ule.oa.base.service.CompanyPositionSeqService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.PageModel;

@Service
public class CompanyPositionSeqServiceImpl implements CompanyPositionSeqService{
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private CompanyPositionSeqMapper companyPositionSeqMapper;

	@Override
	public Integer save(CompanyPositionSeq companyPositionSeq) {
		Date updateTime = DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_LONG), DateUtils.FORMAT_LONG);
		companyPositionSeq.setUpdateTime(updateTime);
		companyPositionSeq.setCreateTime(updateTime);
		companyPositionSeq.setDelFlag(0);
		return companyPositionSeqMapper.insert(companyPositionSeq);
	}

	@Override
	public Integer delete(Long id) {
		CompanyPositionSeq companyPositionSeq=new CompanyPositionSeq();		
		Date updateTime = DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_LONG), DateUtils.FORMAT_LONG);
		companyPositionSeq.setUpdateTime(updateTime);
		companyPositionSeq.setId(id);
		companyPositionSeq.setDelFlag(1);
		return companyPositionSeqMapper.updatePositionSeqById(companyPositionSeq);
	}

	@Override
	public Integer updateById(CompanyPositionSeq companyPositionSeq) throws Exception {
		int updateCount = companyPositionSeqMapper.updateById(companyPositionSeq);
		
		if(updateCount == 0){
			throw new OaException("您当前修改的员工职位信息已经被其它人修改过，请重新编辑");
		}
		
		return updateCount;
	}
	
	@Override
	public Integer updatePositionSeqById(CompanyPositionSeq companyPositionSeq){
		Date updateTime = DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_LONG), DateUtils.FORMAT_LONG);
		companyPositionSeq.setUpdateTime(updateTime);	
		return companyPositionSeqMapper.updatePositionSeqById(companyPositionSeq);
	}

	@Override
	public PageModel<CompanyPositionSeq> getByPagenation(CompanyPositionSeq companyPositionSeq) {
		int page = companyPositionSeq.getPage() == null ? 0 : companyPositionSeq.getPage();
		int rows = companyPositionSeq.getRows() == null ? 0 : companyPositionSeq.getRows();
		
		PageModel<CompanyPositionSeq> pm = new PageModel<CompanyPositionSeq>();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		int total = companyPositionSeqMapper.count(companyPositionSeq);
		pm.setTotal(total);
		companyPositionSeq.setOffset(pm.getOffset());
		companyPositionSeq.setLimit(pm.getLimit());
		List<CompanyPositionSeq> companyPositionSeqs=companyPositionSeqMapper.getByPagenation(companyPositionSeq);
		pm.setRows(companyPositionSeqs);
		return pm;
	}

	@Override
	public List<CompanyPositionSeq> getListByCondition(CompanyPositionSeq companyPositionSeq){
		return companyPositionSeqMapper.getListByCondition(companyPositionSeq);
	}
	
	@Override
	public CompanyPositionSeq getByCondition(CompanyPositionSeq companyPositionSeq){
		List<CompanyPositionSeq> list = getListByCondition(companyPositionSeq);
		
		if(null != list && list.size()>0){
			return list.get(0);
		}
		
		return companyPositionSeq;
	}
	
	@Override
	public CompanyPositionSeq getById(Long id){
		return companyPositionSeqMapper.getPositionSeqById(id);
	}

	@Override
	public int queryCode(String code, int id) {
		return companyPositionSeqMapper.queryCode(code,id);
	}

	@Override
	public int queryName(String name, int id) {
		return companyPositionSeqMapper.queryName(name,id);
	}

	@Override
	public List<CompanyPositionSeq> getListByEmployeeId(Long employeeId) {
		return companyPositionSeqMapper.getListByEmployeeId(employeeId);
	}
}
