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
import com.ule.oa.base.mapper.ConfigMapper;
import com.ule.oa.base.po.Config;
import com.ule.oa.common.cache.CacheInitBean;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.CommonUtils;
import com.ule.oa.common.utils.RedisUtils;
import com.ule.oa.common.utils.json.JSONUtils;

/**
  * @ClassName: ConfigCacheManager
  * @Description: 配置表缓存
  * @author minsheng
  * @date 2017年5月8日 下午2:18:15
 */
@Component("configCacheManager")
public class ConfigCacheManager implements CacheInitBean {
	
	private final Logger logger = LoggerFactory.getLogger(ConfigCacheManager.class);
	
	@Autowired
	private ConfigMapper configMapper;

	@Override
	public void init() {
		logger.info("************初始化系统配置缓存 begin**************");
		List<Config> allConfig = configMapper.getListByCondition(null);
		Map<String, List<Config>> cacheMap = new HashMap<String, List<Config>>();
		for (Config config : allConfig) {
			if (!cacheMap.containsKey(config.getCode())) {
				cacheMap.put(config.getCode(), new ArrayList<Config>());
			}
			cacheMap.get(config.getCode()).add(config);
		}
		
		for (Entry<String, List<Config>> entry : cacheMap.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
		
		logger.info("************初始化系统配置缓存 end**************");
	}
	
	public void reload(){
		init();
	}
	
	public void put(String code, Config config) {
		List<Config> configList = new ArrayList<Config>();
		configList.add(config);
		
		try {
			RedisUtils.setString(CommonUtils.getSysConfigKey(code), serialize(configList));
		} catch (Exception e) {
			logger.error("设置系统配置缓存失败", e);
			throw new RuntimeException("设置系统配置缓存失败");
		}
	}
	
	public void put(String code, List<Config> configList) {
		try {
			RedisUtils.setString(CommonUtils.getSysConfigKey(code), serialize(configList));
		} catch (Exception e) {
			logger.error("设置系统配置缓存失败", e);
			throw new RuntimeException("设置系统配置缓存失败");
		}
	}
	
	public List<Config> get(String code) {
		
		if(null == code || "".equals(code)){
			return null;
		}
		
		String content = null;
		try {
			content = RedisUtils.getString(CommonUtils.getSysConfigKey(code));
		} catch (Exception e) {
			logger.error("读取系统配置缓存失败，key=[" + CommonUtils.getSysConfigKey(code) + "]", e);
			throw new RuntimeException("读取系统配置缓存失败，key=[" + CommonUtils.getSysConfigKey(code) + "]");
		}
		if (null != content) {
			return JSONUtils.read(content, new TypeReference<List<Config>>() {});
		}
		return null;
	}
	
	public List<Config> getListByCode(String code){
		List<Config> list = get(code);
		
		if(null == list || list.size() == 0){
			Config dbConf = new Config();
			dbConf.setCode(code);
			List<Config> dConfs = configMapper.getListByCondition(dbConf);
			
			return dConfs;
		}
		
		return list;
	}
	
	public String getConfigDisplayCode(String code) throws OaException {
		List<Config> list = getListByCode(code);
		String value = null;
		
		if(null != list && !list.isEmpty()){
			Config config = list.get(0);
			value = config.getDisplayCode();
			
			return value;
		}
		
		throw new OaException("根据code{"+code+"}找不到对应的值");
	}
	
	private String serialize(List<Config> configList) {
		return JSONUtils.write(configList);
	}

}
