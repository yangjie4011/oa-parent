package com.ule.oa.base.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.ApplicationEmployeeClass;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

/**
 * @ClassName: 排班申请表
 * @Description: 排班申请表
 * @author yangjie
 * @date 2017年8月31日
 */
public interface ApplicationEmployeeClassMapper extends OaSqlMapper{
	
	ApplicationEmployeeClass getById(@Param("id")Long id);
	
	Integer save(ApplicationEmployeeClass applicationEmployeeClass);
	
	List<ApplicationEmployeeClass> getByCondition(ApplicationEmployeeClass applicationEmployeeClass);
	
	int getCountByCondition(ApplicationEmployeeClass applicationEmployeeClass);
	
	List<ApplicationEmployeeClass> getPassList(ApplicationEmployeeClass applicationEmployeeClass);
	
	Integer updateById(ApplicationEmployeeClass applicationEmployeeClass);
	
	int deleteById(ApplicationEmployeeClass applicationEmployeeClass);
	
	List<Map<String,Object>> getEmpClassReprotById(ApplicationEmployeeClass applicationEmployeeClass);
	
	ApplicationEmployeeClass queryByProcessInstanceId(String processInstanceId);
	
	//分页查询待办/失效列表
	List<ApplicationEmployeeClass> getHandlingListByPage(ApplicationEmployeeClass employeeClass);
	
	//查询待办/失效总数
	Integer getHandlingCount(ApplicationEmployeeClass employeeClass);
	
	//查询申请表排班明细（调班申请不用）
	List<Map<String,Object>> getDetailById(@Param("id")Long id,@Param("isMove")Long isMove);
	
	//分页查询已办列表
	List<ApplicationEmployeeClass> getHandledListByPage(ApplicationEmployeeClass employeeClass);
	
	//查询已办总数
	Integer getHandledCount(ApplicationEmployeeClass employeeClass);
	
	//查询排班申请详细信息
	List<Map<String,Object>> getClassHoursMap(Long id);
	
	//根据审批id 来查询排班
	List<Map<String,Object>> getEmpClassById(ApplicationEmployeeClass applicationEmployeeClass);
	
	//根据调班申请id查询调整过的员工id List
	List<Long> getEmployeeIdListById(@Param("id")Long id);
	
	//根据审批id 来查询调班数据
	List<Map<String,Object>> getChangeEmpClassById(ApplicationEmployeeClass applicationEmployeeClass);
	
	//根据月份、组id、查询最终数据
	List<Map<String,Object>> getFlagDataByInfo(ApplicationEmployeeClass applicationEmployeeClass);
	
	//根据月份、组id、查询最终数据 总记录
	List<Map<String, Object>> getFlagDataCountByInfo(ApplicationEmployeeClass changeClassParam);
	
	//根据部门id 组id 统计 当月 排班查询的总数据
	List<ApplicationEmployeeClass> getCountDataCountByInfo(ApplicationEmployeeClass changeClassParam);

	List<ApplicationEmployeeClass> getCountEmpByInfo(ApplicationEmployeeClass empClass);
	
	//获取未提交审核的排班申请数据
	ApplicationEmployeeClass getUnCommitData(ApplicationEmployeeClass empClass);

	List<ApplicationEmployeeClass> getNotSubmittedClass(ApplicationEmployeeClass condition);
	
	//保存排班数据
	
}
