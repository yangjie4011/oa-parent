package com.ule.oa.base.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ule.oa.base.mapper.EmpResignMapper;
import com.ule.oa.base.po.EmpResign;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.EmpResignService;
import com.ule.oa.base.service.RunTaskService;
import com.ule.oa.base.service.UserService;

/**
 * @ClassName: 员工离职申请
 * @Description: 员工离职申请
 * @author yangjie
 * @date 2017年5月25日
 */
@Service
public class EmpResignServiceImpl implements EmpResignService {
	
	@Autowired
	private EmpResignMapper empResignMapper;
	@Autowired
	private RunTaskService runTaskService;
	@Autowired
	private UserService userService;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void save(EmpResign empResign) {
		empResignMapper.save(empResign);
		User user = userService.getCurrentUser();
		//runTaskService.startFlow(RunTask.RUN_CODE_20, user, empResign.getId().toString());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateById(EmpResign empResign) {
		//runTaskService.nextFlow(empResign.getRuProcdefId(), userService.getCurrentUser(), "", empResign.getId().toString());
		return empResignMapper.updateById(empResign);
	}

	@Override
	public EmpResign getById(Long id) {
		return empResignMapper.getById(id);
	}

	@Override
	public EmpResign getByEmployeeId(Long employeeId) {
		return empResignMapper.getByEmployeeId(employeeId);
	}

}
