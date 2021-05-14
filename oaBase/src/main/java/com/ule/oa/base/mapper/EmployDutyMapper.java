package com.ule.oa.base.mapper;

import java.util.List;

import com.ule.oa.base.po.EmployDuty;
import com.ule.oa.base.util.jdbc.OaSqlMapper;
import com.ule.oa.common.persistence.SqlMapper;


/**
 * @ClassName: 员工值班表
 * @Description: 员工值班表
 * @author yangjie
 * @date 2018年1月9日
 */
public interface EmployDutyMapper extends OaSqlMapper{
	
	List<EmployDuty> selectByCondition(EmployDuty duty);

}
