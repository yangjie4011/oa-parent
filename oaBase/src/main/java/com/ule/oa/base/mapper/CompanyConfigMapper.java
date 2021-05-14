package com.ule.oa.base.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ule.oa.base.po.CompanyConfig;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

/**
  * @ClassName: ConfigMapper
  * @Description: 配置信息
  * @author minsheng
  * @date 2017年5月8日 下午5:14:34
 */
public interface CompanyConfigMapper extends OaSqlMapper{
	/**
	  * save(保存系统配置信息)
	  * @Title: save
	  * @Description: 保存系统配置信息
	  * @param model
	  * @return    设定文件
	  * @see com.ule.oa.base.service.CompanyConfigService#save(com.ule.oa.base.po.CompanyConfig)
	  * @throws
	 */
	public void save(CompanyConfig companyConfig);
	
	/**
	  * updateById(这里用一句话描述这个方法的作用)
	  * @Title: updateById
	  * @Description: 根据主键修改配置信息
	  * @param config
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
	public int updateById(CompanyConfig companyConfig);
	
	/**
	  * getListByCondition(根据条件获取所有配置信息)
	  * @Title: getListByCondition
	  * @Description: 根据条件获取所有配置信息
	  * @param config
	  * @return    设定文件
	  * List<Config>    返回类型
	  * @throws
	 */
	public List<CompanyConfig> getListByCondition(CompanyConfig companyConfig);

	Integer count(CompanyConfig companyConfig);

	public List<CompanyConfig> getByPagenation(CompanyConfig companyConfig);

	public void delete(CompanyConfig companyConfig);
	
	//获取员工工时类型
	public String getWorkTypeByEmployeeId(@Param("employeeId")Long employeeId);
	
	//公司配置信息
	int getReportCount(CompanyConfig config);
	
	List<CompanyConfig> getReportPageList(CompanyConfig config);
	
	CompanyConfig getConfigInfoById(@Param("id") long id);
	
	void saveConfig(CompanyConfig config);

	List<CompanyConfig> getCompanyinfo();

	void updateCompanyConfig(CompanyConfig config);

	void deleteCompanyConfig(CompanyConfig config);
	
	CompanyConfig getById(Long id);

	public String getGroupEmail(@Param("groupName")String groupName);
	
	public List<CompanyConfig> getListByCodes(@Param("codeList")List<String> codeList);
}