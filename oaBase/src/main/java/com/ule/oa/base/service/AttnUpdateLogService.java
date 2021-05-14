package com.ule.oa.base.service;

import com.ule.oa.base.po.AttnUpdateLog;
import com.ule.oa.common.utils.PageModel;

public interface AttnUpdateLogService {
	
	public PageModel<AttnUpdateLog> getPageList(AttnUpdateLog attnUpdateLog);

}
