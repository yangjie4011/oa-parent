package com.ule.oa.web.controller;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ule.oa.BaseControllerTest;
import com.ule.oa.base.po.ActPermission;
import com.ule.oa.base.service.ActPermissionService;

public class ActPermissionControllerTest extends BaseControllerTest{
	
	protected final static Logger logger = LoggerFactory.getLogger(ActPermissionControllerTest.class);

	@Resource
	private ActPermissionService actPermissionService;
	
	//@Test
    public void getEmpIdByPermission() {
    	try {
        	ActPermission actPermission = new ActPermission();
        	actPermission.setActId("222");
        	actPermission.setActName("sss");
    		List<?> list = actPermissionService.queryEmpIdByPermission(actPermission);
    		logger.info(list.toString());
		} catch (Exception e) {
			logger.error("出错",e);
		}
	}
    
    //@Test
    public void insertPermission() {
    	try {
        	ActPermission actPermission = new ActPermission();
        	actPermission.setProcessName("单元测试的数据");
        	actPermission.setActId("222");
        	actPermission.setActName("sss");
        	int i = actPermissionService.insertPermission(actPermission);
    		logger.info(i+"");
		} catch (Exception e) {
			logger.error("出错",e);
		}
	}
	
	@Test
    public void getListByPermission() {
    	try {
        	ActPermission actPermission = new ActPermission();
        	actPermission.setActId("222");
        	actPermission.setActName("sss");
    		List<ActPermission> list = actPermissionService.queryListByPermission(actPermission);
    		logger.info(list.toString());
		} catch (Exception e) {
			logger.error("出错",e);
		}
	}
}
