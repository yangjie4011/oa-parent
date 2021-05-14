package com.ule.oa.base.po;

import com.ule.oa.base.po.tbl.EmployeeDeviceTbl;

public class EmployeeDevice extends EmployeeDeviceTbl {

	private static final long serialVersionUID = 655542437789054595L;
	
	private String userAgent;
	
	private String resolution;//分辨率

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}
	

}
