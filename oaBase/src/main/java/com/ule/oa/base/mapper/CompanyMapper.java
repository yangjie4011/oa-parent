package com.ule.oa.base.mapper;

import java.util.List;

import com.ule.oa.base.po.Company;
import com.ule.oa.base.util.jdbc.OaSqlMapper;
import com.ule.oa.common.persistence.SqlMapper;

/**
  * @ClassName: CompanyMapper
  * @Description: 公司信息
  * @author minsheng
  * @date 2017年5月8日 下午5:14:21
 */
public interface CompanyMapper extends OaSqlMapper{
	public void save(Company company);
	
	public int updateById(Company company);
	
	public List<Company> getListByCondition(Company company);
	
	public Company getById(Long id);
}