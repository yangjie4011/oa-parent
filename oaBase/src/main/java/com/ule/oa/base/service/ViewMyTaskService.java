package com.ule.oa.base.service;

import java.util.List;
import java.util.Map;

import com.ule.oa.base.vo.TaskVO;

/**
 */
public interface ViewMyTaskService {
	public List<Map<String,Object>> queryMyAskTask(Map<String,Object> param);

	List<Map<String, Object>> queryAskTask(Map<String, Object> param);
	
	List<TaskVO> queryAskTask(Long employeeId);
	
	List<TaskVO> queryAskTask2(Long employeeId);
	
}
