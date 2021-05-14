package com.ule.oa.base.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ule.oa.base.po.Config;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.exception.SmsException;
import com.ule.oa.common.utils.PageModel;

public interface ConfigService {
	/**
	  * getListByCode(根据code获取配置信息)
	  * @Title: getListByCode
	  * @Description: 根据code获取配置信息
	  * @param code
	  * @return    设定文件
	  * List<Config>    返回类型
	  * @throws
	 */
	public List<Config> getListByCode(String code);

		/**
	  * updateById(这里用一句话描述这个方法的作用)
	  * @Title: updateById
	  * @Description: 根据主键修改配置信息
	  * @param config
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
	public int updateById(Config config);
	
	/**
	  * getListByCondition(根据条件获取所有配置信息)
	  * @Title: getListByCondition
	  * @Description: 根据条件获取所有配置信息
	  * @param config
	  * @return    设定文件
	  * List<Config>    返回类型
	  * @throws
	 */
	public List<Config> getListByCondition(Config config);
	
	/**
	  * getByCondition(根据查询条件查询配置信息)
	  * @Title: getByCondition
	  * @Description: 根据查询条件查询配置信息
	  * @param config
	  * @return    设定文件
	  * Config    返回类型
	  * @throws
	 */
	public Config getByCondition(Config config);

	/**
	 * 根据手机号码获取验证码
	 * @param phone
	 * @param second 
	 * @return
	 * @throws SmsException 
	 * @throws OaException 
	 */
	public String getPhoneValidateCode(String phone, Integer second) throws OaException, SmsException;

	//系统配置service
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
	public PageModel<Config> getByPagenation(Config sysCompanyConfig);
	
	/**
	 * @throws OaException 
	  * id获取对象
	  * query(这里用一句话描述这个方法的作用)
	  * @Title: insert
	  * @Description: 获取
	  * @param SysConfig
	  * void    ConfigTbl
	  * @throws
	 */
	public Config getConfigInfoById(Long id);	
	/**
	 * @throws OaException 
	  * 新增配置数据
	  * insert(这里用一句话描述这个方法的作用)
	  * @Title: insert
	  * @Description: 新增
	  * @param SysCompanyConfig
	  * void    返回类型
	  * @throws
	 */
	public void saveConfig(Config config);
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
	
	public List<Config> getConfiginfo();
	/**
	 * @throws OaException 
	  * 修改配置数据
	  * insert(这里用一句话描述这个方法的作用)
	  * @Title: update
	  * @Description: 修改
	  * @param ConfigTbl
	  * void    返回类型
	  * @throws
	 */
	public void updateConfig(Config config);
	
	/**
	 * @throws OaException 
	  * 删除配置数据
	  * update(修改id=1)
	  * @Title: update
	  * @Description: 修改
	  * @param ConfigTbl
	  * void    返回类型
	  * @throws
	 */
	public void deleteConfig(long id); 
	
	/**
	 * 导入员工信息
	 * @param file
	 * @throws Exception 
	 */
	public boolean inportEmpInfo(MultipartFile file) throws Exception;
	
	//获取需要的员工类型列表
	public List<Long> getNeedEmpTypeIdList();
	
	/**
	 * 判断年假是否能透支
	 * @return
	 */
	public boolean checkYearLeaveCanOverDraw();
}
