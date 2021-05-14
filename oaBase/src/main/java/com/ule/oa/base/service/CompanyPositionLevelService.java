package com.ule.oa.base.service;

import java.util.List;

import com.ule.oa.base.po.CompanyPositionLevel;
import com.ule.oa.common.utils.PageModel;

public interface CompanyPositionLevelService {
	
	/**
	 * 
	  * 添加新的职位级别
	  * @Title: save
	  * @param companyPositionLevel    设定文件
	  * void    返回类型
	  * @throws
	 */
	public Integer save(CompanyPositionLevel companyPositionLevel);
	
	/**
	 * 
	  * 逻辑删除职位级别
	  * @Title: save
	  * @param companyPositionLevel    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	public Integer delete(long id);
	
	/**
	 * @throws Exception 
	  * 更新职位级别数据
	  * @Title: update
	  * @Description: TODO
	  * @param companyPositionLevel
	  * @return    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	public Integer updateById(CompanyPositionLevel companyPositionLevel) throws Exception;
	
	/**
	 * @throws Exception 
	  * 更新职位级别数据
	  * @Title: update
	  * @Description: TODO
	  * @param companyPositionLevel
	  * @return    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	public Integer updatePositionLevelById(CompanyPositionLevel companyPositionLevel) throws Exception;
	
	/**
	 * 
	  * 分页查询
	  * @Title: getByPagenation
	  * @param companyPositionLevel
	  * @return    设定文件
	  * PageModel<CompanyPositionLevel>    返回类型
	  * @throws
	 */
	public PageModel<CompanyPositionLevel> getByPagenation(CompanyPositionLevel companyPositionLevel);
	
	public List<CompanyPositionLevel> getListByCondition(CompanyPositionLevel companyPositionLevel);
	
	public CompanyPositionLevel getByCondition(CompanyPositionLevel companyPositionLevel);
	
	public CompanyPositionLevel getById(Long id);

	public int queryCode(String code, int id);

	public int queryName(String name, int id);
}


