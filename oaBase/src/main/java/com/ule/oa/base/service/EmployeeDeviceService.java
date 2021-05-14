package com.ule.oa.base.service;

import javax.servlet.http.HttpServletResponse;

import com.ule.oa.base.po.EmployeeDevice;

public interface EmployeeDeviceService {
	
    void save(HttpServletResponse response,EmployeeDevice device);
    
    EmployeeDevice getByEmployeeId(Long employeeId);

}
