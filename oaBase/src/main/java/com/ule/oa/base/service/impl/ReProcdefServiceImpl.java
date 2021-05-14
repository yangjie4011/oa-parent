package com.ule.oa.base.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ule.oa.base.mapper.ReProcdefMapper;
import com.ule.oa.base.po.ReProcdef;
import com.ule.oa.base.service.ReProcdefService;
import com.ule.oa.common.utils.PageModel;

@Service
public class ReProcdefServiceImpl implements ReProcdefService{

	@Autowired
	private ReProcdefMapper reProcdefMapper;
	
	@Override
	public ReProcdef getById(Long id) {
		return reProcdefMapper.getById(id);
	}
	
	@Override
	public ReProcdef getByCode(String code) {
		return reProcdefMapper.getByCode(code);
	}

	@Override
	public List<ReProcdef> getPageList(ReProcdef reProcdef) {
		return reProcdefMapper.getPageList(reProcdef);
	}

	@Override
	public Integer getCount(ReProcdef reProcdef) {
		return reProcdefMapper.getCount(reProcdef);
	}

	@Override
	public Long save(ReProcdef reProcdef) {
		return reProcdefMapper.save(reProcdef);
	}

	@Override
	public void batchSave(ReProcdef reProcdef) {
		reProcdefMapper.batchSave(reProcdef);
	}

	@Override
	public Integer updateById(Long id) {
		return reProcdefMapper.updateById(id);
	}

	@Override
	public PageModel<ReProcdef> getByPagenation(ReProcdef reProcdef) {
		int page = reProcdef.getPage() == null ? 0 : reProcdef.getPage();
		int rows = reProcdef.getRows() == null ? 0 : reProcdef.getRows();
		
		PageModel<ReProcdef> pm = new PageModel<ReProcdef>();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		int total = reProcdefMapper.getCount(reProcdef);
		pm.setTotal(total);
		reProcdef.setOffset(pm.getOffset());
		reProcdef.setLimit(pm.getLimit());
		List<ReProcdef> ruProcdefList = reProcdefMapper.getPageList(reProcdef);
		pm.setRows(ruProcdefList);
		return pm;
	}

	@Override
	public List<ReProcdef> getListByCondition(ReProcdef reProcdef) {
		return reProcdefMapper.getListByCondition(reProcdef);
	}

}
