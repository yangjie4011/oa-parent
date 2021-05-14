package com.ule.oa.base.service;

import java.util.Map;
import com.ule.oa.base.po.Employee;
import com.ule.oa.common.utils.PageModel;



public interface EmployeeSignService {
	
	//员工签到分页查询
	public PageModel<Map<String,Object>> getList(Employee employee);
	
    //立即签到
	public Map<String, Object> sign(Long employeeId) throws Exception;

}
