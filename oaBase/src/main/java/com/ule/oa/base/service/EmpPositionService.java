package com.ule.oa.base.service;

import java.util.List;

import com.ule.oa.base.po.EmpPosition;
import com.ule.oa.base.po.EmployeeApp;
import com.ule.oa.common.exception.OaException;

public interface EmpPositionService {
	/**
	  * save(员工职位信息)
	  * @Title: save
	  * @Description: 保存员工职位信息
	  * @param model    设定文件
	  * void    返回类型
	  * @throws
	 */
	public void save(EmpPosition model);
	
	/**
	 * @throws OaException 
	  * updateById(更新员工职位信息)
	  * @Title: updateById
	  * @Description: 根据主键修改员工职位信息
	  * @param config
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
	public int updateById(EmpPosition config) throws OaException;
	
	/**
	  * getListByCondition(根据条件获取所有员工职位信息)
	  * @Title: getListByCondition
	  * @Description: 根据条件获取所有员工职位信息
	  * @param config
	  * @return    设定文件
	  * List<Config>    返回类型
	  * @throws
	 */
	public List<EmpPosition> getListByCondition(EmpPosition config);
	
	/**
	  * getByCondition(根据条件检索员工职位信息)
	  * @Title: getByCondition
	  * @Description: 根据条件检索员工职位信息
	  * @param model
	  * @return    设定文件
	  * EmpPosition    返回类型
	  * @throws
	 */
	public EmpPosition getByCondition(EmpPosition model);
	
	/**
	  * updateEmpPositionInfo(更新-在职信息)
	  * @Title: updateEmpPositionInfo
	  * @Description: 更新员工在职信息
	  * @param employee
	  * @return
	  * @throws Exception    设定文件
	  * boolean    返回类型
	  * @throws
	 */
	public boolean updateEmpPositionInfo(EmployeeApp employee) throws Exception;
}
