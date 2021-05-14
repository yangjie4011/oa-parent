package com.ule.oa.base.service.impl;

import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonObject;
import com.ule.oa.base.mapper.EmployeeDeviceMapper;
import com.ule.oa.base.po.EmployeeDevice;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.EmployeeDeviceService;
import com.ule.oa.base.service.UserService;

@Service
public class EmployeeDeviceServiceImpl implements EmployeeDeviceService {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private EmployeeDeviceMapper employeeDeviceMapper;
	@Autowired
	private UserService userService;

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void save(HttpServletResponse response,EmployeeDevice device) {
		User user = userService.getCurrentUser();
		EmployeeDevice old = employeeDeviceMapper.getByEmployeeId(user.getEmployeeId());
		if(old==null){
			JsonObject json = new JsonObject();
			json.addProperty("user-agent", device.getUserAgent());
			json.addProperty("resolution", device.getResolution());
			device.setEmployeeId(user.getEmployeeId());
			device.setInfo(json.toString());
			device.setCreateTime(new Date());
			device.setCreateUser(user.getEmployee().getCnName());
			employeeDeviceMapper.save(device);
			Cookie isSaveExt=new Cookie("isSaveExt","0"); 
			isSaveExt.setMaxAge(12*30*24*60*60);   //存活期为一年 12*30*24*60*60
			isSaveExt.setPath("/");
			response.addCookie(isSaveExt);
		}
	}

	@Override
	public EmployeeDevice getByEmployeeId(Long employeeId) {
		return employeeDeviceMapper.getByEmployeeId(employeeId);
	}


}
