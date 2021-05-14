package com.ule.oa.base.mapper;

import java.util.List;

import com.ule.oa.base.po.ApplyEmployDutyDetail;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

/**
 * @ClassName: 值班申请
 * @Description: 值班申请
 * @author yangjie
 * @date 2017年12月11日
 */
public interface ApplyEmployDutyDetailMapper extends OaSqlMapper{
	
	List<ApplyEmployDutyDetail> getList();

}
