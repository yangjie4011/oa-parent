package com.ule.oa.base.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ule.oa.base.mapper.CompanyConfigMapper;
import com.ule.oa.base.po.CompanyConfig;
import com.ule.oa.common.cache.CacheInitBean;
import com.ule.oa.common.utils.CommonUtils;
import com.ule.oa.common.utils.RedisUtils;
import com.ule.oa.common.utils.json.JSONUtils;

/**
  * @ClassName: ConfigCacheManager
  * @Description: 公司配置表缓存
  * @author minsheng
  * @date 2017年5月8日 下午2:18:15
 */
@Component("companyConfigCacheManager")
public class CompanyConfigCacheManager implements CacheInitBean {
	
	private final Logger logger = LoggerFactory.getLogger(CompanyConfigCacheManager.class);
	
	@Autowired
	private CompanyConfigMapper companyConfigMapper;

	@Override
	public void init() {
		logger.info("************初始化公司配置缓存 begin**************");
		List<CompanyConfig> allConfig = companyConfigMapper.getListByCondition(null);
		Map<String, List<CompanyConfig>> cacheMap = new HashMap<String, List<CompanyConfig>>();
		for (CompanyConfig config : allConfig) {
			if (!cacheMap.containsKey(config.getCode())) {
				cacheMap.put(config.getCode(), new ArrayList<CompanyConfig>());
			}
			cacheMap.get(config.getCode()).add(config);
		}
		
		for (Entry<String, List<CompanyConfig>> entry : cacheMap.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
		
		logger.info("************初始化公司配置缓存 end**************");
	}
	
	public void reload(){
		init();
	}
	
	public void put(String code, CompanyConfig config) {
		List<CompanyConfig> configList = new ArrayList<CompanyConfig>();
		configList.add(config);
		
		try {
			RedisUtils.setString(CommonUtils.getSysCompanyConfigKey(code), serialize(configList));
		} catch (Exception e) {
			logger.error("设置公司配置缓存失败", e);
			throw new RuntimeException("设置公司配置缓存失败");
		}
	}
	
	public void put(String code, List<CompanyConfig> configList) {
		try {
			RedisUtils.setString(CommonUtils.getSysCompanyConfigKey(code), serialize(configList));
		} catch (Exception e) {
			logger.error("设置公司配置缓存失败", e);
			throw new RuntimeException("设置公司配置缓存失败");
		}
	}
	
	public List<CompanyConfig> get(String code) {
		String content = null;
		try {
			content = RedisUtils.getString(CommonUtils.getSysCompanyConfigKey(code));
		} catch (Exception e) {
			logger.error("读取公司配置缓存失败，key=[" + CommonUtils.getSysCompanyConfigKey(code) + "]", e);
			throw new RuntimeException("读取公司配置缓存失败，key=[" + CommonUtils.getSysCompanyConfigKey(code) + "]");
		}
		if (null != content) {
			return JSONUtils.read(content, new TypeReference<List<CompanyConfig>>() {});
		}
		return null;
	}
	
	private String serialize(List<CompanyConfig> configList) {
		return JSONUtils.write(configList);
	}

}
