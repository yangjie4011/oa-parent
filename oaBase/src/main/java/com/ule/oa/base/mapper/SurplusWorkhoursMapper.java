package com.ule.oa.base.mapper;

import java.util.List;
import com.ule.oa.base.po.SurplusWorkhours;
import com.ule.oa.base.util.jdbc.OaSqlMapper;


/**
 * @ClassName: 排班员工剩余工时
 * @Description: 排班员工剩余工时
 * @author yangjie
 * @date 2017年11月9日
 */
public interface SurplusWorkhoursMapper extends OaSqlMapper{
	
    Integer batchSave(List<SurplusWorkhours> list);
	
	List<SurplusWorkhours> selectByCondition(SurplusWorkhours surplusWorkhours);
	
	int updateById(SurplusWorkhours surplusWorkhours);

}
