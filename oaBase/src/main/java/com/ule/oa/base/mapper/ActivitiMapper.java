package com.ule.oa.base.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.util.jdbc.OaSqlMapper;

public interface ActivitiMapper extends OaSqlMapper {
	/**
	 * 通过流程实例id查询流程key
	 * @param instanceId
	 * @return
	 */
	public String queryKeyByInstanceId(String instanceId);
	/**
	 * 条件查询已办任务实例
	 * @param map
	 * @return
	 */
	public List<Map<String, Object>> queryTaskByAssignee(Map<String, Object> map);
	
	/**
	 * 查询待办总数
	 * @param map
	 * @return
	 */
	List<Integer> queryTaskCount(@Param("processinstanceIdList")List<String> processinstanceIdList);
	
	
}
