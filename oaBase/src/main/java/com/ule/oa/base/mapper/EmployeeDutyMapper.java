package com.ule.oa.base.mapper;

import java.util.List;

import com.ule.oa.base.po.EmployeeDuty;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

/**
 * @ClassName: 员工值班表
 * @Description: 员工值班表
 * @author yangjie
 * @date 2018年1月9日
 */
public interface EmployeeDutyMapper extends OaSqlMapper{
	
	Integer save(EmployeeDuty duty);
	
	Integer batchSave(List<EmployeeDuty> dutyList);
	
	Integer update(EmployeeDuty duty);
	
    List<EmployeeDuty> selectByCondition(EmployeeDuty duty);
    
    Integer deleteByCondition(EmployeeDuty duty);
    
    List<EmployeeDuty> getGroupByCondition(EmployeeDuty duty);
    
    //根据部门，年份，节假日查询统计值班人数
  	public List<EmployeeDuty> getEmployDutyCountByCondition(EmployeeDuty duty);
  	
  	//获取值班人数
  	List<EmployeeDuty> getDutyNum(EmployeeDuty duty);

  	//查询已存在的排班
	List<EmployeeDuty> getExistDutyListByCondition(EmployeeDuty empDuty);
	
	//获取值班详细信息
	List<EmployeeDuty> getDutyDetail(EmployeeDuty duty);
	
	//删除老的值班数据
	int deleteOldDuty(EmployeeDuty empDuty);
	
	//根据source查询值班数据
	List<EmployeeDuty> getDutyListBySource(EmployeeDuty empDuty);
  	
}
