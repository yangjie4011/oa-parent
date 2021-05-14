package com.ule.oa.base.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ule.oa.base.cache.CompanyConfigCacheManager;
import com.ule.oa.base.mapper.CompanyConfigMapper;
import com.ule.oa.base.po.CompanyConfig;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.service.CompanyConfigService;
import com.ule.oa.base.service.SmsService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.exception.SmsException;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.PageModel;

/**
  * @ClassName: ConfigServiceImpl
  * @Description: 系统配置
  * @author minsheng
  * @date 2017年5月8日 下午1:56:26
 */
@Service
public class CompanyConfigServiceImpl implements CompanyConfigService{
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private CompanyConfigMapper companyConfigMapper;
	@Resource
	private CompanyConfigCacheManager companyConfigCacheManager;
	
	
	@Resource
	private SmsService smsService;

	/**
	  * save(保存系统配置信息)
	  * @Title: save
	  * @Description: 保存系统配置信息
	  * @param model
	  * @return    设定文件
	  * @see com.ule.oa.base.service.CompanyConfigService#save(com.ule.oa.base.po.CompanyConfig)
	  * @throws
	 */
	@Override
	public void save(CompanyConfig config) {
		companyConfigMapper.save(config);
		
		//刷新缓存
		companyConfigCacheManager.reload();
	}

	/**
	  * updateById(这里用一句话描述这个方法的作用)
	  * @Title: updateById
	  * @Description: 根据主键修改配置信息
	  * @param config
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public int updateById(CompanyConfig config) {
		//刷新缓存
		companyConfigCacheManager.reload();
		
		return companyConfigMapper.updateById(config);
	}
	
	/**
	  * getListByCode(根据配置code获取配置信息)
	  * @Title: getListByCode
	  * @Description: 根据配置code获取配置信息
	  * @param code
	  * @return    设定文件
	  * List<CompanyConfig>    返回类型
	  * @throws
	 */
	@Override
	public List<CompanyConfig> getListByCode(String code){
		CompanyConfig config = new CompanyConfig();
		config.setCode(code);
		
		return getListByCondition(config);
	}

	/**
	  * getListByCondition(根据条件获取所有配置信息)
	  * @Title: getListByCondition
	  * @Description: 根据条件获取所有配置信息
	  * @param config
	  * @return    设定文件
	  * List<Config>    返回类型
	  * @throws
	 */
	@Override
	public List<CompanyConfig> getListByCondition(CompanyConfig config) {
//		List<CompanyConfig> configList = cacheManager.get(config.getCode());
//		if (null == configList) {
//			synchronized (this) {
//				if (null == configList) {
//					configList = companyConfigMapper.getListByCondition(config);
//					if (null != configList) {
//						cacheManager.put(config.getCode(), configList);
//					}
//				}
//			}
//		}
		
		return companyConfigMapper.getListByCondition(config);
	}
	
	/**
	  * getById(根据主键查询配置信息)
	  * @Title: getById
	  * @Description: 根据主键查询配置信息
	  * @param id
	  * @return    设定文件
	  * Config    返回类型
	  * @throws
	 */
	public CompanyConfig getById(Long id){
		CompanyConfig config = new CompanyConfig();
		config.setId(id);
		
		return getConfig(config);
	}
	
	/**
	  * getByCondition(根据查询条件查询配置信息)
	  * @Title: getByCondition
	  * @Description: 根据查询条件查询配置信息
	  * @param config
	  * @return    设定文件
	  * Config    返回类型
	  * @throws
	 */
	@Override
	public CompanyConfig getByCondition(CompanyConfig config){
		return getConfig(config);
	}
	
	public CompanyConfig getConfig(CompanyConfig config){
		List<CompanyConfig> confList = getListByCondition(config);
		
		if(null != confList && confList.size() >0){
			return confList.get(0);
		}
		
		return null;
	}

	@Override
	public String getPhoneValidateCode(String phone,Integer second) throws OaException, SmsException {
		
		String template = "您的验证码为: {password}, 请于"+(second/60)+"分钟内及时使用【邮包裹后台】";
		String code = smsService.sendRandomCode(phone, template);
		
		return code;
	}

	@Override
	public String getWorkTypeByEmployeeId(Long employeeId) {
		return companyConfigMapper.getWorkTypeByEmployeeId(employeeId);
	}
	
	//公司系统配置 

	
	@Override
	public PageModel<CompanyConfig> getByPagenation(CompanyConfig sysCompanyConfig) {
		int page = sysCompanyConfig.getPage() == null ? 0 : sysCompanyConfig.getPage();
		int rows = sysCompanyConfig.getRows() == null ? 0 : sysCompanyConfig.getRows();
		
		PageModel<CompanyConfig> pm = new PageModel<CompanyConfig>();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		Integer total = companyConfigMapper.getReportCount(sysCompanyConfig);
		pm.setTotal(total);
		
		sysCompanyConfig.setOffset(pm.getOffset());
		sysCompanyConfig.setLimit(pm.getLimit());
		
		List<CompanyConfig> roles = companyConfigMapper.getReportPageList(sysCompanyConfig);
		pm.setRows(roles);
		return pm;
	}


	@Override
	public CompanyConfig getConfigInfoById(int id) {
		return companyConfigMapper.getConfigInfoById(id);
	}


	@Override
	public CompanyConfig saveConfig(CompanyConfig config) {
		Date updateTime = DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_LONG), DateUtils.FORMAT_LONG);
		config.setUpdateTime(updateTime);
		config.setCreateTime(updateTime);
		config.setDelFlag(0);
		config.setCreateUser("");
		config.setUpdateUser("");
		companyConfigMapper.saveConfig(config);
		
		//刷新缓存
		companyConfigCacheManager.reload();
		return config;
	}

	@Override
	public List<CompanyConfig> getCompanyinfo() {
		return companyConfigMapper.getCompanyinfo();
	}

	@Override
	public void updateCompanyConfig(CompanyConfig config) {
		Date updateTime = DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_LONG), DateUtils.FORMAT_LONG);
		config.setUpdateTime(updateTime);
		companyConfigMapper.updateCompanyConfig(config);
		
		//刷新缓存
		companyConfigCacheManager.reload();
	}

	@Override
	public void deleteCompanyConfig(long id) {
		CompanyConfig config = companyConfigMapper.getConfigInfoById(id);
		config.setDelFlag(1);
		config.setId(id);
		Date updateTime = DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_LONG), DateUtils.FORMAT_LONG);
		config.setUpdateTime(updateTime);
		companyConfigMapper.deleteCompanyConfig(config);
		
		//刷新缓存
		companyConfigCacheManager.reload();
	}

	@Override
	public String getGroupEmail(String groupName) {
		String email = companyConfigMapper.getGroupEmail(groupName);
		return email;
	}

	@Override
	public PageModel<CompanyConfig> getPageList(CompanyConfig user) {
		// TODO Auto-generated method stub
		int page = user.getPage() == null ? 0 : user.getPage();
		int rows = user.getRows() == null ? 0 : user.getRows();

		PageModel<CompanyConfig> pm = new PageModel<CompanyConfig>();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		Integer total = companyConfigMapper.count(user);
		pm.setTotal(total);
		user.setOffset(pm.getOffset());
		user.setLimit(pm.getLimit());
		List<CompanyConfig> employees = companyConfigMapper.getListByCondition(user);
		pm.setRows(employees);
		return pm;
	}
}
