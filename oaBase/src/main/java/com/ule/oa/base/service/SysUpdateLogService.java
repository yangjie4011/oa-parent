package com.ule.oa.base.service;

import java.util.List;
import java.util.Map;

import com.ule.oa.base.po.tbl.SysUpdateLogTbl;

public interface SysUpdateLogService {
	public int save(SysUpdateLogTbl sysUpdateLog);
	public List<SysUpdateLogTbl> queryByCondition(SysUpdateLogTbl sysUpdateLog);
}
