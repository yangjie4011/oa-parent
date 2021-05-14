package com.ule.oa.base.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.BaseEmpWorkLog;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.RequestParamQueryEmpCondition;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

/**
 * 员工工作日志
 * @author yangjie
 *
 */
public interface BaseEmpWorkLogMapper extends OaSqlMapper{
	
	BaseEmpWorkLog selectEffectiveApplyByCondition(@Param("workDate")Date workDate,@Param("employeeId")Long employeeId);
	
	/**
	 * 保存
	 * @param workLog
	 * @return
	 */
	Integer save(BaseEmpWorkLog workLog);
	
	/**
	 * 根据ID修改信息
	 * @param workLog
	 * @return
	 */
	Integer updateById(BaseEmpWorkLog workLog);
	
	/**
	 * 根据条件查询列表数据
	 * @param workLog
	 * @return
	 */
	List<BaseEmpWorkLog> selectByCondition(BaseEmpWorkLog workLog);
	
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	BaseEmpWorkLog getById(@Param("id")Long id);
	
	/**
	 * 根据流程id查询
	 * @param processId
	 * @return
	 */
	BaseEmpWorkLog getByProcessId(@Param("processId")String processId);
	
	/**
	 * 查询最新单据
	 * @param workDate
	 * @param employeeId
	 * @return
	 */
	BaseEmpWorkLog getByWorkDateAndEmployeeId(@Param("workDate")Date workDate,@Param("employeeId")Long employeeId);
	
	/**
	 * 根据条件查询员工列表
	 * @param condition
	 * @return
	 */
	List<Employee> getListByCondition(RequestParamQueryEmpCondition condition);
	
	/**
	 * 根据条件查询员工总数
	 * @param condition
	 * @return
	 */
	Integer getCountByCondition(RequestParamQueryEmpCondition condition);

}
