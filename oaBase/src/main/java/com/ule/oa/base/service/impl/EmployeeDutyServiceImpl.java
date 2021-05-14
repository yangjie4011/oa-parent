package com.ule.oa.base.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ule.oa.base.mapper.EmployeeDutyMapper;
import com.ule.oa.base.po.EmployeeDuty;
import com.ule.oa.base.service.EmployeeDutyService;

/**
 * @ClassName: 员工值班表
 * @Description: 员工值班表
 * @author yangjie
 * @date 2018年1月22日
 */
@Service
public class EmployeeDutyServiceImpl implements EmployeeDutyService {
	
	@Resource
	private EmployeeDutyMapper employeeDutyMapper;

	@Override
	public List<EmployeeDuty> selectByCondition(EmployeeDuty duty) {
		return employeeDutyMapper.selectByCondition(duty);
	}

}
