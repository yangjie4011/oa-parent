package com.ule.oa.base.mapper;

import java.util.List;
import com.ule.oa.base.po.SchedulePlan;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

public interface SchedulePlanMapper extends OaSqlMapper{
	
	public SchedulePlan getById(Long id); 
	
	public int updateById(SchedulePlan model);

	public void save(SchedulePlan model);

	public List<SchedulePlan> getListByCondition(SchedulePlan model);
	
	Integer count(SchedulePlan model);
	
	public List<SchedulePlan> getByPagenation(SchedulePlan model);
	
	
	
}