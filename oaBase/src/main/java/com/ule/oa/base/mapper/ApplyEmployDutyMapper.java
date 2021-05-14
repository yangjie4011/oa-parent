package com.ule.oa.base.mapper;

import com.ule.oa.base.po.ApplyEmployDuty;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

/**
 * @ClassName: 值班申请
 * @Description: 值班申请
 * @author yangjie
 * @date 2017年12月11日
 */
public interface ApplyEmployDutyMapper extends OaSqlMapper{
	
	Integer updateById(ApplyEmployDuty duty);

}
