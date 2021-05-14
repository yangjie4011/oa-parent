package com.ule.oa.base.mapper;

import com.ule.oa.base.po.ApplyEmployClass;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

/**
 * @ClassName: 排班申请
 * @Description: 排班申请
 * @author yangjie
 * @date 2017年12月11日
 */
public interface ApplyEmployClassMapper extends OaSqlMapper{
	
	Integer updateById(ApplyEmployClass employyClass);

}
