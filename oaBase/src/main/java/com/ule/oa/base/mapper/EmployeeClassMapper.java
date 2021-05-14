package com.ule.oa.base.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.ApplicationEmployeeClass;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

public interface EmployeeClassMapper  extends OaSqlMapper{

	/**
	  * 关联查询当天排班情况
	  * @Title: getEmployeeClassSetting
	  * @param employeeClass
	  * @return    设定文件
	  * EmployeeClass    返回类型
	  * @throws
	 */
	EmployeeClass getEmployeeClassSetting(EmployeeClass employeeClass);

	/**
	  * 根据日期批量查询员工排班数据
	  * @Title: getEmployeeClassList
	  * @param condition
	  * @return    设定文件
	  * List<EmployeeClass>    返回类型
	  * @throws
	 */
	List<EmployeeClass> getEmployeeClassList(EmployeeClass condition);

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
	  * getMustClassSettingCountByDepartId(根据部门id获取需要排班人数)
	  * @Title: getMustClassSettingCountByDepartId
	  * @Description: 根据部门id获取需要排班人数
	  * @param departId
	  * @return    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	Integer getMustClassSettingCountByDepartId(Long departId);
	
	/**
	  * getAlreadyClassSettingByDepartIdAndMonth(根据部门id和排班时间获取已经排班人数和排班人（查看当月和下月已经排班人数和排班人）)
	  * @Title: getAlreadyClassSettingByDepartIdAndMonth
	  * @Description: 根据部门id和排班时间获取已经排班人数和排班人（查看当月和下月已经排班人数和排班人）
	  * @return    设定文件
	  * EmployeeClass    返回类型
	  * @throws
	 */
	EmployeeClass getAlreadyClassSettingByDepartIdAndMonth(EmployeeClass employeeClass);
	
	/**
	  * save(逐单保存员工排班信息)
	  * @Title: save
	  * @Description: 逐单保存员工排班信息
	  * @param list
	  * @return    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	Integer save(EmployeeClass employeeClass);
	
	/**
	  * batchSave(批量保存员工排班信息)
	  * @Title: batchSave
	  * @Description: 批量保存员工排班信息
	  * @param list
	  * @return    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	Integer batchSave(List<EmployeeClass> list);
	
	List<EmployeeClass> selectByCondition(EmployeeClass employeeClass);
	
	int updateById(EmployeeClass employeeClass);
	
	int deleteByQuitTime(EmployeeClass employeeClass);
	
	List<EmployeeClass> getClassHours(EmployeeClass condition);
	
	List<Map<String,Object>> getClassHoursMap(EmployeeClass condition);
	
	//通过排班id 来查询排班信息
	List<Map<String,Object>> getClassHoursMapByid(EmployeeClass condition);
	
	/**
	  * getClassSettingIdByEmployIdAndDate(根据日期和时间获取员工班次Id)
	  * @Title: getClassSettingIdByEmployIdAndDate
	  * @Description: 根据日期和时间获取员工班次Id
	  * @param employId
	  * @param classDate
	  * @return    设定文件
	  * Long    返回类型
	  * @throws
	 */
	Long getClassSettingIdByEmployIdAndDate(@Param("employId")Long employId,@Param("classDate")Date classDate);

	List<EmployeeClass> getEmployeeThisMonthClassList(EmployeeClass condition);

	int updateGroupId(EmployeeClass empClass);
	
	//查询下月已排班天数
	int getClassCountByMonth(@Param("groupId")Long groupId, @Param("fristDay")Date fristDay, @Param("lastDay")Date lastDay);
	
	EmployeeClass getByEmployIdAndDate(@Param("employId")Long employId,@Param("classDate")Date classDate);
	
	int deleteByIdList(@Param("idList")List<Long> idList,@Param("updateUser")String updateUser);
	
	/**
	 * 导出排班数据
	 * @param departId
	 * @param groupId
	 * @param starDate
	 * @param endDate
	 * @return
	 */
	List<EmployeeClass> exportScheduleDataByMonthAndGroupId(@Param("departId")Long departId,@Param("groupId")Long groupId,@Param("startDate")Date startDate,@Param("endDate")Date endDate);
	
	/**
	 * 删除startDate之后的排班数据
	 * @param startDate
	 * @return
	 */
	int deleteByStartDate(@Param("startDate")Date startDate);
}