package com.ule.oa.base.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.ule.oa.base.po.TimingRunTask;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

public interface TimingRunTaskMapper extends OaSqlMapper{
	/**
	 * 保存
	 * @param timingRunTask
	 * @return
	 */
	int save(TimingRunTask timingRunTask);
	/**
	 * 
	 * @return
	 */
	List<TimingRunTask> getList();
	
	int updateById(TimingRunTask timingRunTask);
	
	/**
	 * 找到存在存在待审批的审批人
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	List<Map<String, Object>> getAssigneeListWait(@Param("beginDate")Date beginDate, @Param("endDate")Date endDate);
	
	
	List<Map<String, Object>> getWaitAssigneeList(@Param("beginDate")Date beginDate, @Param("endDate")Date endDate);
	
	int deleteByCodeAndEntityId(@Param("reProcdefCode")String reProcdefCode,@Param("entityId")String entityId);
}
