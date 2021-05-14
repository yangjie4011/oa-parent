package com.ule.oa.base.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.ule.oa.base.po.ApplicationEmployeeClass;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeClass;

public interface EmployeeClassService {

	/**
	  * 查询员工多个日期的排班开始结束时间
	  * @Title: getEmployeeClassSetting
	  * @param employee
	  * @param datas 日期list
	  * @return    设定文件
	  * Map<String,Object>    返回类型
	  * @throws
	 */
	public Map<String,Object> getEmployeeClassSetting(Employee employee,List<String> datas);
	
	/**
	  * 查询员工多个日期的排班开始结束时间
	  * @Title: getEmployeeClassSetting
	  * @param employee
	  * @param datas 日期list
	  * @return    设定文件
	  * Map<String,Object>    返回类型
	  * @throws
	 */
	public Map<Date,EmployeeClass> getEmployeeClassSetting(Employee employee,Date startDate,Date endDate);
	
	/**
	  * 查询员工多个日期的排班开始结束时间
	  * @Title: getEmployeeClassSetting
	  * @param employee
	  * @param datas 日期list
	  * @return    设定文件
	  * Map<String,Object>    返回类型
	  * @throws
	 */
	public Map<Long,List<EmployeeClass>> getEmployeeClassSetting(List<Long> employeeIds,Date startDate,Date endDate);
	/**
	  * 根据条件查询
	  * @Title: getEmployeeClassSetting
	  * @param employeeClass
	  * @return    设定文件
	  * ClassSetting    返回类型
	  * @throws
	 */
	EmployeeClass getEmployeeClassSetting(EmployeeClass employeeClass);
	
	/**
	  * 根据条件查询
	  * @Title: getEmployeeClassSetting
	  * @param employeeClass
	  * @return    设定文件
	  * ClassSetting    返回类型
	  * @throws
	 */
	EmployeeClass getEmployeeClassSetting(EmployeeClass employeeClass,Map<Long,String> workTypeMap,Map<Long,String> schedulingMap);
	
	/**
	  * 查询一批员工排班数据key:员工id，value：该员工排班数据对象
	  * @Title: getEmployeeClassMap
	  * @param classCondition
	  * @return    设定文件
	  * Map<Long,EmployeeClass>    返回类型
	  * @throws
	 */
	Map<Long, EmployeeClass> getEmployeeClassMap(EmployeeClass condition);
	
	/**
	  * 查询某个员工月度总应出勤(classDate:yyyy-mm-dd,employId)
	  * @Title: getEmployeeClassHours
	  * @Description: TODO
	  * @param condition
	  * @return    设定文件
	  * EmployeeClass    返回类型
	  * @throws
	 */
	EmployeeClass getEmployeeClassHours(EmployeeClass condition);

	/**
	  * 根据日期批量查询员工排班数据
	  * @Title: getEmployeeClassList
	  * @param condition
	  * @return    设定文件
	  * List<EmployeeClass>    返回类型
	  * @throws
	 */
	List<EmployeeClass> getEmployeeClassPeriodList(EmployeeClass condition);
	
	/**
	  * 根据条件查询单个排班数据
	  * @param condition
	  * @return    设定文件
	  * List<EmployeeClass>    返回类型
	  * @throws
	 */
	EmployeeClass getEmployeeClass(EmployeeClass condition);
	
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
	public List<EmployeeClass> getEmployeeClassInfoByDepartId(Long departId) throws Exception;
	
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
	
	/**
	  * save(逐单保存员工排班信息)
	  * @Title: save
	  * @Description: 逐单保存员工排班信息
	  * @param list
	  * @return    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	public Integer save(EmployeeClass employeeClass);
	
	/**
	  * batchSave(批量保存员工排班信息)
	  * @Title: batchSave
	  * @Description: 批量保存员工排班信息
	  * @param list
	  * @return    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	public Integer batchSave(List<EmployeeClass> list);
	
	public int deleteByQuitTime(EmployeeClass employeeClass);
	
	/**
	  * mustArrangeOfWorkMind(排班消息提醒)
	  * @Title: mustArrangeOfWorkMind
	  * @Description: 排班消息提醒
	  * @throws Exception    设定文件
	  * void    返回类型
	  * @throws
	 */
	public void mustArrangeOfWorkMind() throws Exception;
	
	List<EmployeeClass> selectByCondition(EmployeeClass employeeClass);
	
	public EmployeeClass getEmployeeClassByCondition(EmployeeClass condition);
	
	/**
	 * 返回json格式的排班数据
	 * @param employeeClass
	 * @param classDate 
	 * @return
	 */
	public String getEmployeeClassJsonData(String employeeId,String classDate);
	
	public Map<String,Object> queryClassByCondition(Long departId,String month, Long id, boolean isQueryflag, Long gruopId) throws Exception;
	
	public Map<String, Object> queryClassByMonth(ApplicationEmployeeClass empClassTemp);

	public List<Map<String,Object>>  getClassHoursMapList(EmployeeClass employeeClass);
	
	public Map<Long,Integer> getLeastAttendanceHours(List<Employee> empList,Date month);
	
	/**
	 * 导出排班数据
	 * @param departId
	 * @param groupId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public HSSFWorkbook exportScheduleDataByMonthAndGroupId(Long departId,Long groupId,String month);
	
	/**
	 * 删除startDate之后的排班数据
	 * @param startDate
	 * @return
	 */
	int deleteByStartDate(Date startDate);
}