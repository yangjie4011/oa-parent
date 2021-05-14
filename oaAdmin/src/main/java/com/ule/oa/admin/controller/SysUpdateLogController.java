package com.ule.oa.admin.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.ule.oa.base.po.tbl.SysUpdateLogTbl;
import com.ule.oa.base.service.SysUpdateLogService;
import com.ule.oa.common.utils.json.JSONUtils;

@Controller
@RequestMapping("updateLog")
public class SysUpdateLogController {
	@Autowired
	private SysUpdateLogService sysUpdateLogService;
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@ResponseBody
	@RequestMapping("/queryRaduixLog.htm")
	public Map<String,Object> queryRaduixLog(Long employeeId){

		Map<String,Object> result = Maps.newHashMap();
		try {
			SysUpdateLogTbl sysUpdateLog = new SysUpdateLogTbl();
			sysUpdateLog.setDelFlag(0);
			sysUpdateLog.setResourceEmployeeId(employeeId);
			sysUpdateLog.setResourceType(2);
			result.put("sucess",true);
			result.put("data",this.sysUpdateLogService.queryByCondition(sysUpdateLog));
		} catch (Exception e) {
			result.put("sucess",false);
			result.put("msg",e.getMessage());
		}
		return result;
	}
}
