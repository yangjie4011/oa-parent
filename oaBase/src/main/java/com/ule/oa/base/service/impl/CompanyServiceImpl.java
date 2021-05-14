package com.ule.oa.base.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ule.oa.base.mapper.CompanyMapper;
import com.ule.oa.base.po.Company;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.CompanyService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.exception.OaException;

/**
  * @ClassName: CompanyServiceImpl
  * @Description: 公司业务层
  * @author minsheng
  * @date 2017年5月8日 下午4:37:23
 */
@Service
public class CompanyServiceImpl implements CompanyService {
	@Autowired
	private CompanyMapper companyMapper;
	@Autowired
	private UserService userService;
	
	/**
	  * save(保存公司信息)
	  * @Title: save
	  * @Description: 保存公司信息
	  * @param company    设定文件
	  * void    返回类型
	  * @throws
	 */
	@Override
	public void save(Company company){
		companyMapper.save(company);
	}
	
	/**
	  * updateById(根据主键修改公司信息)
	  * @Title: updateById
	  * @Description: 根据主键修改公司信息
	  * @param company
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
	@Override
	public int updateById(Company company){
		return companyMapper.updateById(company);
	}
	
	/**
	  * getListByCondition(根据条件获取所有公司信息)
	  * @Title: getListByCondition
	  * @Description: 根据条件获取所有公司信息
	  * @param company
	  * @return    设定文件
	  * List<Company>    返回类型
	  * @throws
	 */
	@Override
	public List<Company> getListByCondition(Company company){
		return companyMapper.getListByCondition(company);
	}
	
	/**
	  * getByCondition(根据搜索条件查询公司信息)
	  * @Title: getByCondition
	  * @Description: 根据搜索条件查询公司信息
	  * @param company
	  * @return    设定文件
	  * Company    返回类型
	  * @throws
	 */
	@Override
	public Company getByCondition(Company company){
		List<Company> companyList = getListByCondition(company);
		
		if(null != companyList && companyList.size()>0){
			return companyList.get(0);
		}else{
			return new Company();
		}
	}
	
	/**
	  * getCurrentCompanyId(获取当前登录用户公司id)
	  * @Title: getCurrentCompanyId
	  * @Description: 获取当前登录用户公司id
	  * @return    设定文件
	  * Long    返回类型
	  * @throws
	 */
	@Override
	public Long getCurrentCompanyId() throws OaException{
		User user = userService.getCurrentUser();
		if(user == null){
			throw new OaException("获取当前登录用户公司信息失败，请重新登录");
		}
		
		return user.getCompany().getId();
	}
}
