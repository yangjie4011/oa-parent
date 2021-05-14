package com.ule.oa.base.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.EmpDepart;
import com.ule.oa.base.util.jdbc.OaSqlMapper;


public interface EmpDepartMapper extends OaSqlMapper{

	void save(EmpDepart model);

	int updateById(EmpDepart model);

	List<EmpDepart> getListByCondition(EmpDepart model);
	
	EmpDepart getById(Long id);
	
	int updateByEmployeeId(EmpDepart model);
	
	//获取在职员工部门关系数据
	List<EmpDepart> getPayRollList(@Param("departId")Long departId);
}