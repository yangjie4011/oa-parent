package com.ule.oa.base.service;

import java.util.List;

import com.ule.oa.base.po.EmpDepart;

public interface EmpDepartService {
	/**
	  * save(员工部门信息)
	  * @Title: save
	  * @Description: 保存员工部门信息
	  * @param model    设定文件
	  * void    返回类型
	  * @throws
	 */
	public void save(EmpDepart model);
	
	/**
	 * @throws Exception 
	 * @throws OaException 
	  * updateById(更新员工部门信息)
	  * @Title: updateById
	  * @Description: 根据主键修改员工部门信息
	  * @param config
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
	public int updateById(EmpDepart config) throws Exception;
	
	/**
	  * getListByCondition(根据条件获取所有员工部门信息)
	  * @Title: getListByCondition
	  * @Description: 根据条件获取所有员工部门信息
	  * @param config
	  * @return    设定文件
	  * List<Config>    返回类型
	  * @throws
	 */
	public List<EmpDepart> getListByCondition(EmpDepart config);
	
	/**
	  * getByCondition(根据条件检索员工部门信息)
	  * @Title: getByCondition
	  * @Description: 根据条件检索员工部门信息
	  * @param model
	  * @return    设定文件
	  * EmpDepart    返回类型
	  * @throws
	 */
	public EmpDepart getByCondition(EmpDepart model);
	
	public int updateByEmployeeId(EmpDepart model);

}
