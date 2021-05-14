package com.ule.oa.base.service;

import java.util.List;

import com.ule.oa.base.po.EmployeeDuty;

/**
 * @ClassName: 员工值班表
 * @Description: 员工值班表
 * @author yangjie
 * @date 2018年1月22日
 */
public interface EmployeeDutyService {
	
	 List<EmployeeDuty> selectByCondition(EmployeeDuty duty);

}
