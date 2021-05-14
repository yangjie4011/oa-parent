package com.ule.oa.base.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.EmpScheduleGroup;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.ScheduleGroup;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

public interface ScheduleGroupMapper extends OaSqlMapper{

	List<ScheduleGroup> getListByDepartId(@Param("departId") Long departId);

	Integer getEmpCount(@Param("groupId")Long groupId);

	ScheduleGroup getGroupById(@Param("groupId")Long groupId);

	Integer isNotSameName(ScheduleGroup group);

	void insertScheduleGroup(ScheduleGroup scheduleGroup);

	void updateScheduleGroup(ScheduleGroup scheduleGroup);

	List<Employee> getAllGroupEmp(@Param("groupId")Long groupId, @Param("condition")String condition,@Param("classMonth")Date classMonth);

	String getDepartNameByGroupId(@Param("groupId")Long groupId);
	
	//根据员工id获取组别信息
	ScheduleGroup getGroupByEmployeeId(@Param("employeeId")Long employeeId);

	List<Employee> getUngroupedEmp(@Param("emp")Employee emp);

	void addMember(EmpScheduleGroup empScheduleGroup);

	void delMember(@Param("empId")Long empId, @Param("groupId")Long groupId, @Param("updateUser")Long updateUser, @Param("updateTime")Date updateTime);
	
	//根据条件查询排班组别列表
	List<ScheduleGroup> getListByCondition(ScheduleGroup scheduleGroup);
	
	//根据排班人查询排班组别列表
	List<ScheduleGroup> getListByScheduler(@Param("scheduler")Long scheduler);
	
	/**
	 * 查询排班人或者审核人为某员工的组别列表
	 * @param employeeId
	 * @return
	 */
	List<ScheduleGroup> getListBySchedulerOrAuditor(@Param("employeeId")Long employeeId);
	
	List<Depart> getScheduleDepartList();
	
	void delByEmpId(@Param("empId")Long id);

	List<Long> getAllEmpIdByGroupId(@Param("groupId")Long groupId,@Param("classMonth")Date classMonth);
	
	/**
	 * 根据条件查询排班组 部门名称列表
	 * @param scheduleGroup
	 * @return
	 */
	List<ScheduleGroup> getAllListByCondition(ScheduleGroup scheduleGroup);
	
	/**
	 * 排班员工管理查询
	 * @param empclass
	 * @return
	 */
	List<Employee> getEmpClassListByCondition(Employee empclass);

	/**
	 * 排班员工管理查询数
	 * @param empclass
	 * @return
	 */
	Integer getEmpClassListByConditionCount(Employee empclass);
}
