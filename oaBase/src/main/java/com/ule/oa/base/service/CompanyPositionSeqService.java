package com.ule.oa.base.service;

import java.util.List;

import com.ule.oa.base.po.CompanyPositionSeq;
import com.ule.oa.common.utils.PageModel;

public interface CompanyPositionSeqService {
	
	/**
	 * 
	  * 添加新的职位序列
	  * @Title: save
	  * @param companyPositionLevel    设定文件
	  * void    返回类型
	  * @throws
	 */
	public Integer save(CompanyPositionSeq companyPositionSeq);
	
	/**
	 * 
	  * 逻辑删除职位序列
	  * @Title: save
	  * @param companyPositionLevel    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	public Integer delete(Long id);
	
	/**
	 * @throws Exception 
	  * 更新职位序列数据
	  * @Title: update
	  * @param companyPositionLevel
	  * @return    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	public Integer updateById(CompanyPositionSeq companyPositionSeq) throws Exception;
	
	/**
	 * 
	  * 分页查询
	  * @Title: getByPagenation
	  * @param companyPositionLevel
	  * @return    设定文件
	  * PageModel<CompanyPositionLevel>    返回类型
	  * @throws
	 */
	public PageModel<CompanyPositionSeq> getByPagenation(CompanyPositionSeq companyPositionSeq);
	
	public List<CompanyPositionSeq> getListByCondition(CompanyPositionSeq companyPositionSeq);
	
	public CompanyPositionSeq getByCondition(CompanyPositionSeq companyPositionSeq);
	
	public CompanyPositionSeq getById(Long id);

	public int queryCode(String code, int id);
	
	public int queryName(String name, int id);

	public Integer updatePositionSeqById(CompanyPositionSeq companyPositionSeq);
	
	//查询员工的职位序列
	public List<CompanyPositionSeq> getListByEmployeeId(Long employeeId);
}
