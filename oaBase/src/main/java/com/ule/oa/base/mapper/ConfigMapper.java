package com.ule.oa.base.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.Config;
import com.ule.oa.base.util.jdbc.OaSqlMapper;
import com.ule.oa.common.persistence.SqlMapper;

public interface ConfigMapper extends OaSqlMapper{
	/**
	  * save(保存系统配置信息)
	  * @Title: save
	  * @Description: 保存系统配置信息
	  * @param model
	  * @return    设定文件
	  * @see com.ule.oa.base.service.CompanyConfigService#save(com.ule.oa.base.po.CompanyConfig)
	  * @throws
	 */
	public void save(Config sysConfig);
	
	/**
	  * updateById(这里用一句话描述这个方法的作用)
	  * @Title: updateById
	  * @Description: 根据主键修改配置信息
	  * @param config
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
	public int updateById(Config sysConfig);
	
	/**
	  * getListByCondition(根据条件获取所有配置信息)
	  * @Title: getListByCondition
	  * @Description: 根据条件获取所有配置信息
	  * @param config
	  * @return    设定文件
	  * List<Config>    返回类型
	  * @throws
	 */
	public List<Config> getListByCondition(Config sysConfig);

	Integer count(Config sysConfig);

	public List<Config> getByPagenation(Config sysConfig);

	public void delete(Config sysConfig);
	
	//系统配置mapper
	
	int getReportCount(Config config);
	
	List<Config> getReportPageList(Config config);	

	Config getConfigInfoById(@Param("id") Long id);
	
	void saveConfig(Config config);

	void updateConfig(Config config);

	void deleteConfig(Config config);
	
	Config getById(Long id);
	
	public List<Config> getListByCodes(@Param("codeList")List<String> codeList);
}