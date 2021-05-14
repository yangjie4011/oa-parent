package com.ule.oa.base.mapper;

import java.util.List;

import com.ule.oa.base.po.EmpApplicationLeaveAbolish;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

/**
 * @ClassName: 销假申请表
 * @Description:销假申请表
 * @author yangjie
 * @date 2018年11月22日
 */
public interface EmpApplicationLeaveAbolishMapper extends OaSqlMapper{
	
    void save(EmpApplicationLeaveAbolish leaveAbolish);
	
	int updateById(EmpApplicationLeaveAbolish leaveAbolish);
	
	EmpApplicationLeaveAbolish getById(Long id);

	EmpApplicationLeaveAbolish queryByProcessId(String instanceId);
	
	List<EmpApplicationLeaveAbolish> getByLeaveId(Long leaveId);

	List<EmpApplicationLeaveAbolish> getReportPageList(
			EmpApplicationLeaveAbolish leaveAbolish);

	Integer getReportCount(EmpApplicationLeaveAbolish leave);

	Integer myLeaveTaskListCount(EmpApplicationLeaveAbolish leave);

	List<EmpApplicationLeaveAbolish> myLeaveTaskList(
			EmpApplicationLeaveAbolish leave);
	 
	

}
