package com.ule.oa.base.mapper;

import com.ule.oa.base.po.EmpApplicationOvertimeLate;
import com.ule.oa.base.util.jdbc.OaSqlMapper;
import com.ule.oa.common.persistence.SqlMapper;

/**
 * @ClassName: 晚到申请
 * @Description: 晚到申请
 * @author yangjie
 * @date 2017年6月19日
 */
public interface EmpApplicationOvertimeLateMapper extends OaSqlMapper{
	
    int save(EmpApplicationOvertimeLate userOvertimeLate);
	
	int updateById(EmpApplicationOvertimeLate userOvertimeLate);
	
	EmpApplicationOvertimeLate getById(Long id);
	
	int getEaoByEmpAndDateCount(EmpApplicationOvertimeLate userOvertimeLate);
	
	

}
