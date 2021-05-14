package com.ule.oa.base.mapper;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.EmpResign;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

/**
 * @ClassName: 员工离职申请
 * @Description: 员工离职申请
 * @author yangjie
 * @date 2017年5月25日
 */

public interface EmpResignMapper extends OaSqlMapper{
	
	void save(EmpResign empResign);
	
    int updateById(EmpResign empResign);
	
    EmpResign getById(@Param("id")Long id);
    
    EmpResign getByEmployeeId(@Param("employeeId")Long employeeId);

}
