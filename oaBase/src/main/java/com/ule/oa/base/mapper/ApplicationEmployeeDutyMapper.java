package com.ule.oa.base.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.ApplicationEmployeeClass;
import com.ule.oa.base.po.ApplicationEmployeeDuty;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

/**
 * @ClassName: 值班申请表
 * @Description: 值班申请表
 * @author yangjie
 * @date 2017年8月31日
 */
public interface ApplicationEmployeeDutyMapper extends OaSqlMapper{
	
    Integer save(ApplicationEmployeeDuty duty);
	
	List<ApplicationEmployeeDuty> getByCondition(ApplicationEmployeeDuty duty);
	
	Integer getByConditionCount(ApplicationEmployeeDuty employeeDuty);
	
	Integer updateById(ApplicationEmployeeDuty duty);
	
	ApplicationEmployeeDuty getById(@Param("id")Long id);
	
	int deleteById(ApplicationEmployeeDuty duty);
	
	ApplicationEmployeeDuty queryByProcessInstanceId(String processInstanceId);

	List<ApplicationEmployeeDuty> myDutyTaskList(ApplicationEmployeeDuty duty);
	
	Integer myDutyTaskListCount(ApplicationEmployeeDuty employeeDuty);
}
