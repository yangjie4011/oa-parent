package com.ule.oa.base.service;

import java.util.List;

import com.ule.oa.base.po.CompanyConfig;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.exception.SmsException;
import com.ule.oa.common.utils.PageModel;

/**
  * @ClassName: ConfigService
  * @Description: 系统配置信息
  * @author minsheng
  * @date 2017年5月8日 下午1:54:37
 */
public interface CompanyConfigService {
	/**
	  * save(保存配置信息)
	  * @Title: save
	  * @Description: 保存配置信息
	  * @param model    设定文件
	  * void    返回类型
	  * @throws
	 */
	public void save(CompanyConfig model);
	
	/**
	  * updateById(这里用一句话描述这个方法的作用)
	  * @Title: updateById
	  * @Description: 根据主键修改配置信息
	  * @param config
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
	public int updateById(CompanyConfig config);
	
	/**
	  * getListByCode(根据配置code获取配置信息)
	  * @Title: getListByCode
	  * @Description: 根据配置code获取配置信息
	  * @param code
	  * @return    设定文件
	  * List<CompanyConfig>    返回类型
	  * @throws
	 */
	public List<CompanyConfig> getListByCode(String code);
	
	/**
	  * getListByCondition(根据条件获取所有配置信息)
	  * @Title: getListByCondition
	  * @Description: 根据条件获取所有配置信息
	  * @param config
	  * @return    设定文件
	  * List<Config>    返回类型
	  * @throws
	 */
	public List<CompanyConfig> getListByCondition(CompanyConfig config);
	
	/**
	  * getByCondition(根据查询条件查询配置信息)
	  * @Title: getByCondition
	  * @Description: 根据查询条件查询配置信息
	  * @param config
	  * @return    设定文件
	  * Config    返回类型
	  * @throws
	 */
	public CompanyConfig getByCondition(CompanyConfig config);

	/**
	 * 根据手机号码获取验证码
	 * @param phone
	 * @param second 
	 * @return
	 * @throws SmsException 
	 * @throws OaException 
	 */
	public String getPhoneValidateCode(String phone, Integer second) throws OaException, SmsException;

	//获取员工工时类型
	public String getWorkTypeByEmployeeId(Long employeeId);
	
	//公司配置接口 ~~
	//公司配置interface
	/**
	 * 分页查询
	  * getByPagenation(这里用一句话描述这个方法的作用)
	  * @Title: getByPagenation
	  * @Description: 
	  * @param SysCompanyConfig
	  * @return    设定文件
	  * PageModel<com.ule.oa.web.po.SysCompanyConfig>    返回类型
	  * @throws
	 */
	public PageModel<CompanyConfig> getByPagenation(CompanyConfig sysCompanyConfig);

	
	/**
	 * @throws OaException 
	  * 通过id获取单独配置
	  * query(这里用一句话描述这个方法的作用)
	  * @Title: qeruy
	  * @Description: 查询
	  * @param id
	  * void    SysCompanyConfig
	  * @throws
	 */
	public CompanyConfig getConfigInfoById(int id);

	
	/**
	 * @return 
	 * @throws OaException 
	  * 新增配置数据
	  * insert(这里用一句话描述这个方法的作用)
	  * @Title: insert
	  * @Description: 新增
	  * @param SysCompanyConfig
	  * void    返回类型
	  * @throws
	 */
	public CompanyConfig saveConfig(CompanyConfig config);
	/**
	 * @throws OaException 
	  * 获取公司id 名称
	  * (获取公司id 名称)
	  * @Title: query
	  * @Description: 获取
	  * @param SysCompanyConfig
	  * List    返回类型
	  * @throws
	 */
	
	public List<CompanyConfig> getCompanyinfo();
	/**
	 * @throws OaException 
	  * 修改配置数据
	  * insert(这里用一句话描述这个方法的作用)
	  * @Title: update
	  * @Description: 修改
	  * @param SysCompanyConfig
	  * void    返回类型
	  * @throws
	 */
	public void updateCompanyConfig(CompanyConfig config);
	
	/**
	 * @throws OaException 
	  * 删除配置数据
	  * update(修改id=1)
	  * @Title: update
	  * @Description: 修改
	  * @param SysCompanyConfig
	  * void    返回类型
	  * @throws
	 */
	public void deleteCompanyConfig(long id);
	
	public String getGroupEmail(String groupName);

	public PageModel<CompanyConfig> getPageList(CompanyConfig user);

}
