package com.ule.oa.base.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ule.oa.base.mapper.ServiceMonitorMapper;
import com.ule.oa.base.service.ServiceMonitorService;

/**
  * @ClassName: ServiceMonitorServiceImpl
  * @Description: 服务监控
  * @author minsheng
  * @date 2017年1月11日 上午11:27:49
 */
@Service
public class ServiceMonitorServiceImpl implements ServiceMonitorService {
	@Autowired
	private ServiceMonitorMapper serviceMonitorMapper;
	
	/**
	  * getServiceMonitor(服务监控--内网)
	  * @Title: getServiceMonitor
	  * @Description: TODO
	  * @return    设定文件
	  * @throws
	 */
	public String getServiceMonitor(){
		serviceMonitorMapper.getServiceMonitor();
		return "SUCCESS";
	}
	
	/**
	  * getExtranetServiceMonitor(服务监控--外网)
	  * @Title: getExtranetServiceMonitor
	  * @Description: TODO
	  * @return    设定文件
	  * @throws
	 */
	public String getExtranetServiceMonitor(){
		return "SUCCESS";
	}
}
