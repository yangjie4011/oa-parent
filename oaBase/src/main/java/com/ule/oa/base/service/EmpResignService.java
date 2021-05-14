package com.ule.oa.base.service;

import com.ule.oa.base.po.EmpResign;

/**
 * @ClassName: 员工离职申请
 * @Description: 员工离职申请
 * @author yangjie
 * @date 2017年5月25日
 */
public interface EmpResignService {
	
	public void save(EmpResign empResign);
	
    public int updateById(EmpResign empResign);
	
	public EmpResign getById(Long id);
	
	public EmpResign getByEmployeeId(Long employeeId);

}
