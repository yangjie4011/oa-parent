package com.ule.oa.base.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ule.oa.base.mapper.SchedulePlanMapper;
import com.ule.oa.base.po.SchedulePlan;
import com.ule.oa.base.service.SchedulePlanService;
import com.ule.oa.common.utils.PageModel;

@Service
public class SchedulePlanServiceImpl implements SchedulePlanService{

	@Autowired
	private SchedulePlanMapper schedulePlanMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void save(SchedulePlan model) {
		schedulePlanMapper.save(model);
	}

	@Override
	public int updateById(SchedulePlan model) {
		return schedulePlanMapper.updateById(model);
	}

	@Override
	public List<SchedulePlan> getListByCondition(SchedulePlan model) {
		return schedulePlanMapper.getListByCondition(model);
	}

	@Override
	public SchedulePlan getById(Long id) {
		return schedulePlanMapper.getById(id);
	}

	@Override
	public PageModel<SchedulePlan> getByPagenation(SchedulePlan model) {
		int page = model.getPage() == null ? 0 : model.getPage();
		int rows = model.getRows() == null ? 0 : model.getRows();
		PageModel<SchedulePlan> pm = new PageModel<SchedulePlan>();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		int total = schedulePlanMapper.count(model);
		pm.setTotal(total);
		model.setOffset(pm.getOffset());
		model.setLimit(pm.getLimit());
		List<SchedulePlan> schedulePlanList = schedulePlanMapper.getByPagenation(model);
		if(schedulePlanList != null && schedulePlanList.size() > 0) {
			for (SchedulePlan schedulePlan : schedulePlanList) {
				schedulePlan.setStartEndTime(schedulePlan.getStartTime() + '~' + schedulePlan.getEndTime());
			}
		}
		pm.setRows(schedulePlanList);
		return pm;
	}

}
