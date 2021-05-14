package com.ule.oa.base.service;

import java.util.List;

import com.ule.oa.base.po.CoopCompany;

/**
  * @ClassName: CoopCompanyService
  * @Description: 合作公司业务层接口
  * @author minsheng
  * @date 2017年5月19日 下午5:41:46
 */
public interface CoopCompanyService {
	public int save(CoopCompany coopCompany);

	public int updateById(CoopCompany coopCompany);
	
	public List<CoopCompany> getListByCondition(CoopCompany coopCompany);
	
	public CoopCompany getByCondition(CoopCompany coopCompany);
}
