package com.ule.oa.base.service;

import java.util.List;

import com.ule.oa.base.po.EmpApplicationBusinessDetail;

public interface EmpApplicationBusinessDetailService {
	List<EmpApplicationBusinessDetail> getListByCondition(Long businessId);
}
