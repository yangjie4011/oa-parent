package com.ule.oa.base.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.EmpApplicationRegister;
import com.ule.oa.base.util.jdbc.OaSqlMapper;
import com.ule.oa.common.persistence.SqlMapper;


/**
 * @ClassName: EmployeeRegister
 * @Description: 员工入职登记表
 * @author yangjie
 * @date 2017年5月22日
*/
public interface EmpApplicationRegisterMapper  extends OaSqlMapper{
	
    Long save(EmpApplicationRegister employeeRegister);
	
	int updateById(EmpApplicationRegister employeeRegister);
	
	EmpApplicationRegister getById(@Param("id")Long id);
	
	EmpApplicationRegister queryByProcessInstanceId(String processInstanceId);
	
	List<EmpApplicationRegister> getByCondition(EmpApplicationRegister employeeRegister);

}
