package com.ule.oa.base.service;

import java.util.List;

import com.ule.oa.base.po.ClassSetPerson;

/**
 * @ClassName: 排班人表
 * @Description: 排班人表
 * @author yangjie
 * @date 2017年9月12日
 */
public interface ClassSetPersonService {
	
	public ClassSetPerson getByEmployeeId(Long employeeId);
	
	public boolean isSetPerson(Long employeeId);
	
	public List<ClassSetPerson> getAll();
}
