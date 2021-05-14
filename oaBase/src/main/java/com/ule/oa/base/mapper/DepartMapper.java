package com.ule.oa.base.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.util.jdbc.OaSqlMapper;
import com.ule.oa.common.persistence.SqlMapper;

public interface DepartMapper extends OaSqlMapper{
	
	public Depart getById(Long id); 
	
	public int updateById(Depart model);

	public void save(Depart model);

	public List<Depart> getListByCondition(Depart model);

	public List<Depart> getChildrenList(Depart model);
	
	public Depart getInfoById(Long id); 
	
	public List<Depart> getDListByParentId(Depart model); 
	
	public Depart getByEmpId(@Param("empId")Long empId); 
	
	public Depart getDepartByEmpId(Depart model); 
	
	public List<Depart> getTreeAppCondition(Depart depart);
	
	List<Depart> getListByLeaderOrPower(Depart depart);
	
	int getLeaderOrPowerCount(@Param("leader")Long leader);
	//获取所有部门负责人id
	List<Depart> getLeaders();
	
	public List<Depart> getByParentId(Long parentId);
	
	List<Depart> getFirstDepart();
	
	List<Depart> getAllDepart();
	
	String getByGroupId(@Param("groupId")Long groupId);

	public List<Depart> getAllDepartByLeaderId(@Param("empId")Long empId);

	public List<Employee> getEmpListByDepartAndCondition(@Param("departId")Long departId, @Param("condition")String condition);

	public List<Depart> getAllDepartByEmpList(@Param("empList")List<Employee> empList);
	
	//获取后台员工弹框部门树数据
	List<Map<String,Object>> getEmployeeTreeList();
	
	public Depart getByLeaderAndId(@Param("id")Long id,@Param("empId")Long empId);
	
	/**
	 * 修改是否排班属性
	 */
	int updateWhetherScheduling(Depart model); 
	
	/**
	 * 
	 * @param code
	 * @param name
	 * @return
	 */
	public Depart getByCodeOrName(@Param("code")String code,@Param("name")String name);
}