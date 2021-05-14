package com.ule.oa.admin.service.impl;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ule.oa.BaseServiceTest;
import com.ule.oa.base.cache.ConfigCacheManager;
import com.ule.oa.base.po.Config;

public class ConfigServiceImplTest extends BaseServiceTest{
	@Autowired
	private ConfigCacheManager configCacheManager;
	
	@Test
	public void testGetConfig(){
		String code = "nation";
		
		List<Config> confs = configCacheManager.get(code);
		
		System.out.println("code\tdescription\tdisplay_name\tdisplay_code\trank\tdel_flag\tremark");
		confs.forEach((Config conf) -> {
			System.out.println(conf.getCode() + "\t" + conf.getDescription() + "\t" + conf.getDisplayName() + "\t"
					+conf.getDisplayCode() + "\t" + conf.getRank() + "\t" + conf.getDelFlag() + "\t" + conf.getRemark());
		});
	}
}
