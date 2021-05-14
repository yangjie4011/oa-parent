package com.ule.oa.base.service;

import java.util.List;

import com.ule.oa.base.po.Company;
import com.ule.oa.common.exception.OaException;

/**
  * @ClassName: CompanyService
  * @Description: 公司业务层
  * @author minsheng
  * @date 2017年5月8日 下午4:36:59
 */
public interface CompanyService {
	/**
	  * save(保存公司信息)
	  * @Title: save
	  * @Description: 保存公司信息
	  * @param company    设定文件
	  * void    返回类型
	  * @throws
	 */
	public void save(Company company);
	
	/**
	  * updateById(根据主键修改公司信息)
	  * @Title: updateById
	  * @Description: 根据主键修改公司信息
	  * @param company
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
	public int updateById(Company company);
	
	/**
	  * getListByCondition(根据条件获取所有公司信息)
	  * @Title: getListByCondition
	  * @Description: 根据条件获取所有公司信息
	  * @param company
	  * @return    设定文件
	  * List<Company>    返回类型
	  * @throws
	 */
	public List<Company> getListByCondition(Company company);
	
	/**
	  * getByCondition(根据搜索条件查询公司信息)
	  * @Title: getCompanyByCondition
	  * @Description: 根据搜索条件查询公司信息
	  * @param company
	  * @return    设定文件
	  * Company    返回类型
	  * @throws
	 */
	public Company getByCondition(Company company);
	
	/**
	 * @throws OaException 
	  * getCurrentCompanyId(获取当前登录用户公司id)
	  * @Title: getCurrentCompanyId
	  * @Description: 获取当前登录用户公司id
	  * @return    设定文件
	  * Long    返回类型
	  * @throws
	 */
	public Long getCurrentCompanyId() throws OaException;
}
