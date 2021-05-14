package com.ule.oa.base.service;

import java.util.List;
import java.util.Map;

import com.ule.oa.base.po.ApplicationEmployeeClassDetail;

/**
 * @ClassName: 排班申请明细表
 * @Description: 排班申请明细表
 * @author yangjie
 * @date 2017年8月30日
 */
public interface ApplicationEmployeeClassDetailService {
	
	/**
	  * 根据条件查询
	  * @Title: getEmployeeClassSetting
	  * @param employeeClass
	  * @return    设定文件
	  * ClassSetting    返回类型
	  * @throws
	 */
	ApplicationEmployeeClassDetail getEmployeeClassSetting(ApplicationEmployeeClassDetail applicationEmployeeClass);
	
	/**
	  * 查询一批员工排班数据key:员工id，value：该员工排班数据对象
	  * @Title: getEmployeeClassMap
	  * @param classCondition
	  * @return    设定文件
	  * Map<Long,EmployeeClass>    返回类型
	  * @throws
	 */
	Map<Long, ApplicationEmployeeClassDetail> getEmployeeClassMap(ApplicationEmployeeClassDetail condition);

	/**
	  * 根据日期批量查询员工排班数据
	  * @Title: getEmployeeClassList
	  * @param condition
	  * @return    设定文件
	  * List<EmployeeClass>    返回类型
	  * @throws
	 */
	List<ApplicationEmployeeClassDetail> getEmployeeClassPeriodList(ApplicationEmployeeClassDetail condition);
	
	/**
	  * 根据条件查询单个排班数据
	  * @param condition
	  * @return    设定文件
	  * List<EmployeeClass>    返回类型
	  * @throws
	 */
	ApplicationEmployeeClassDetail getEmployeeClass(ApplicationEmployeeClassDetail condition);
	
	/**
	 * @throws Exception 
	  * getEmployeeClassInfoByDepartId(根据部门获取员工排班信息)
	  * @Title: getEmployeeClassInfoByDepartId
	  * @Description: 根据部门获取员工排班信息
	  * @param departId
	  * @return    设定文件
	  * List<EmployeeClass>    返回类型
	  * @throws
	 */
	public List<ApplicationEmployeeClassDetail> getEmployeeClassInfoByDepartId(Long departId) throws Exception;
	
	/**
	  * getMustClassSettingCountByDepartId(根据部门id获取需要排班人数)
	  * @Title: getMustClassSettingCountByDepartId
	  * @Description: 根据部门id获取需要排班人数
	  * @param departId
	  * @return    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	public Integer getMustClassSettingCountByDepartId(Long departId);
	
	public List<ApplicationEmployeeClassDetail> selectByCondition(ApplicationEmployeeClassDetail classDetail);
	
	public List<ApplicationEmployeeClassDetail> getEmployeeClassHours(ApplicationEmployeeClassDetail condition);
	
	List<ApplicationEmployeeClassDetail> getEmployeeClassSetByMonth(ApplicationEmployeeClassDetail condition);
	
	int deleteByQuitTime(ApplicationEmployeeClassDetail condition);

}
