package com.ule.oa.base.mapper;

import java.util.List;
import com.ule.oa.base.po.ApplicationEmployeeDutyDetail;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

/**
 * @ClassName: 值班申请明细表
 * @Description: 值班申请明细表
 * @author yangjie
 * @date 2017年8月31日
 */
public interface ApplicationEmployeeDutyDetailMapper extends OaSqlMapper{
	
    List<ApplicationEmployeeDutyDetail> selectByCondition(ApplicationEmployeeDutyDetail dutyDetail);
	
	Integer batchSave(List<ApplicationEmployeeDutyDetail> list);
	
	Integer updateById(ApplicationEmployeeDutyDetail dutyDetail);
	
	void deleteByDutyId(ApplicationEmployeeDutyDetail dutyDetail);

}
