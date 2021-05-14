package com.ule.oa.base.mapper;

import java.util.List;
import java.util.Map;

import com.ule.oa.base.po.EmpLeaveTask;
import com.ule.oa.base.util.jdbc.OaSqlMapper;
import com.ule.oa.common.persistence.SqlMapper;

public interface EmpLeaveTaskMapper extends OaSqlMapper{
    int deleteAllByType(Integer type);
    int batchSaveYearTaskByType(Map<String,Object> data);
    int save(EmpLeaveTask record);
    EmpLeaveTask getById(Long id);
    int updateById(EmpLeaveTask record);
    Long getCount(EmpLeaveTask task);
	List<EmpLeaveTask> getListByCondition(EmpLeaveTask record);
	int deleteAllByCondition(EmpLeaveTask task);
}