package com.ule.oa.base.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.util.jdbc.OaSqlMapper;
import com.ule.oa.base.vo.TaskVO;


/**
 */
public interface ViewMyTaskMapper extends OaSqlMapper{
	public List<Map<String,Object>> queryMyTask(Map<String,Object> param);

	public List<Map<String, Object>> queryAllTask(Map<String, Object> param);
	
	public List<TaskVO> queryAllTask1(@Param("employeeId")Long employeeId);
	
	public List<TaskVO> queryAllTask2(@Param("employeeId")Long employeeId);
}
