package com.ule.oa.base.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ule.oa.base.mapper.ViewMyTaskMapper;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.service.ViewMyTaskService;
import com.ule.oa.base.vo.TaskVO;

@Service
public class ViewMyTaskServiceImpl implements ViewMyTaskService{
	
	
	@Autowired
	private ViewMyTaskMapper viewMyTaskMapper;
	@Autowired
	private UserService userService;
	@Override
	public List<Map<String, Object>> queryMyAskTask(Map<String,Object> param) {
		param.put("empId", userService.getCurrentUser().getEmployeeId());
		return viewMyTaskMapper.queryMyTask(param);
	}
	
	@Override
	public List<Map<String, Object>> queryAskTask(Map<String,Object> param) {
		return viewMyTaskMapper.queryAllTask(param);
	}

	@Override
	public List<TaskVO> queryAskTask(Long employeeId) {
		return viewMyTaskMapper.queryAllTask1(employeeId);
	}

	@Override
	public List<TaskVO> queryAskTask2(Long employeeId) {
		return viewMyTaskMapper.queryAllTask2(employeeId);
	}

}
