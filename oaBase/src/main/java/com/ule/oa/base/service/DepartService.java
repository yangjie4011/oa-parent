package com.ule.oa.base.service;

import java.util.List;
import java.util.Map;

import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.Employee;
import com.ule.oa.common.exception.OaException;

public interface DepartService {
	/**
	  * save(员工部门信息)
	  * @Title: save
	  * @Description: 保存员工部门信息
	  * @param model    设定文件
	  * void    返回类型
	  * @throws
	 */
	public void save(Depart model);
	
	/**
	  * updateById(更新员工部门信息)
	  * @Title: updateById
	  * @Description: 根据主键修改员工部门信息
	  * @param config
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
	public int updateById(Depart config);
	
	/**
	  * getListByCondition(根据条件获取所有员工部门信息)
	  * @Title: getListByCondition
	  * @Description: 根据条件获取所有员工部门信息
	  * @param config
	  * @return    设定文件
	  * List<Config>    返回类型
	  * @throws
	 */
	public List<Depart> getListByCondition(Depart config);
	
	public Depart getByCondition(Depart model);

	public List<Map<String, String>> getTreeList(Depart model);
	
	public List<Map<String, Object>> getEmployeeTreeList(Depart model);
	
	public List<Map<String, String>> getTreeAppList(Depart depart);
  
	public List<Depart> getDepartList(Long id);
	
	/**
	 * 
	  * getEmpList(获取当前组内员工信息)
	  * @Title: getEmpList
	  * @param id
	  * @return    设定文件
	  * Object    返回类型
	  * @throws
	 */
	public List<Employee> getEmpList(Long id);
	
	public Depart getById(Long id); 
	
	/**
	 * 根据部门名称查询员工信息
	 * @param departName
	 * @return
	 */
	public List<Employee> getPowerList(String departId);
	
	/**
	 * 根据id获取所需信息
	 * @param id
	 * @return
	 */
	public Depart getInfoById(Long id);
	
	/**
	 * 根据父类id查询部门信息
	 * @param id
	 * @return
	 */
	public List<Depart> getDListByParentId(Depart depart); 
	
	/**
	 * 根据父类id查询部门人员信息
	 * @param id
	 * @return
	 */
	public List<Depart> getDEListByParentId(Depart depart); 
	
	/**
	 * 根据员工id查询部门负责人及信息
	 * @param depart
	 * @return
	 * 		leaderName:部门负责人名称
	 *		leaderId:部门负责人Id
	 *		managerName:leader
	 *		managerId:leaderId
	 *		empName:人员
	 *		empId:人员id
	 *		name:部门名称
	 *		id:部门id
	 *		type:部门类型
	 *		parentId:上一级部门id
	 */
	public Depart getInfoByEmpId(Long empId);

	/**
	  * 根据部门负责人或者代理人查询，该人员的负责部门列表
	  * @Title: getListByLeaderOrPower
	  * @param model
	  * @return    设定文件
	  * List<Depart>    返回类型
	  * @throws
	 */
	List<Depart> getListByLeaderOrPower(Depart model);
	
	/**
	  * getCurrentDepart(获取当前登录用户对应的部门)
	  * @Title: getCurrentDepart
	  * @Description: 获取当前登录用户对应的部门
	  * @return
	  * @throws OaException    设定文件
	  * Depart    返回类型
	  * @throws
	 */
	public Depart getCurrentDepart() throws OaException;
	
	public Long getCurrentDepartId() throws OaException;
	
	//判断汇报对象是否为部门负责人
	public boolean checkLeaderIsDh(Long leader);
	
	public List<Depart> getByParentId(Long parentId);
	
	/**
	  * getFirstDepart(获得一级部门)
	  * @Title: getFirstDepart
	  * @Description: 获得一级部门
	  * @return    设定文件
	  * List<Depart>    返回类型
	  * @throws
	 */
	public List<Depart> getFirstDepart();
	
	/**
	  * getAllDepart(获取所有部门包括历史部门)
	  * @Title: getAllDepart
	  * @Description: 获取所有部门包括历史部门
	  * @return    设定文件
	  * List<Depart>    返回类型
	  * @throws
	 */
	public List<Depart> getAllDepart();
	

	/**
	 * 根据部门id获取一级部门+当前部门
	 * @param parentDepart
	 * @param childDepartName
	 * @return
	 */
	String setDepartName(Depart parentDepart, String childDepartName);

	/**
	 * 根据ID获取所有层级部门名字
	 * @param departId
	 * @return
	 */
	String getDepartAllLeaveName(Long departId);

	/**
	 * 根据传过来的一级二级部门获取部门id列表
	 * @param firstDepart
	 * @param secondDepart
	 * @return
	 */
	List<Integer> getDepartList(Integer firstDepart, Integer secondDepart);

	public List<Depart> getAllDepartByLeaderId(Long empId);

	public List<Employee> getEmpListByDepartAndCondition(Long departId, String condition);
	
	//根据员工查询部门
	public List<Depart> getAllDepartByEmpList(List<Employee> empList);
	
	//获得登录人所负责的部门和其下属标准工时员工的部门集合
	public List<Depart> getSubordinateDepartList();

	public void updateDeptLeaderById(Depart depart) throws OaException;
	
	public void deleteById(Long departId) throws OaException;
	
	Map<String,Object> getDepartAndEmpListByCodeOrName(String code,String name);

}
