package com.ule.oa.base.mapper;

import com.ule.oa.base.po.EmployeeDevice;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

public interface EmployeeDeviceMapper extends OaSqlMapper{
	
    Integer save(EmployeeDevice device);
	
	Integer update(EmployeeDevice device);
	
	EmployeeDevice getByEmployeeId(Long employeeId);
	

}
