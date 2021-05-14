package com.ule.oa.admin.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.ule.oa.BaseServiceTest;
import com.ule.oa.base.cache.CompanyConfigCacheManager;

public class CompanyConfigServiceImplTest extends BaseServiceTest{
	@Autowired
	private CompanyConfigCacheManager companyConfigCacheManager;
	
	public void testGetCompanyConfig(){
		String code = "";
		
		companyConfigCacheManager.get(code);
	}
}
