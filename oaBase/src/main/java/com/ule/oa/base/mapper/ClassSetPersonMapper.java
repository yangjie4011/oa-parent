package com.ule.oa.base.mapper;

import java.util.List;
import com.ule.oa.base.po.ClassSetPerson;
import com.ule.oa.base.util.jdbc.OaSqlMapper;


/**
 * @ClassName: 排班人表
 * @Description: 排班人表
 * @author yangjie
 * @date 2017年9月12日
 */
public interface ClassSetPersonMapper extends OaSqlMapper{
	
	ClassSetPerson getByEmployeeId(Long employeeId);
	
	List<ClassSetPerson> getAll();

}
