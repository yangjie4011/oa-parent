package com.ule.oa.base.mapper;

import java.util.List;
import com.ule.oa.base.po.HrEmpCheckDetail;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

/**
 * @ClassName: 离职办理清单详细
 * @Description: 离职办理清单详细
 * @author yangjie
 * @date 2017年6月6日
 */
public interface HrEmpCheckDetailMapper extends OaSqlMapper{
	
    void save(HrEmpCheckDetail hrEmpCheckDetail);
	
	int updateById(HrEmpCheckDetail hrEmpCheckDetail);
	
	List<HrEmpCheckDetail> getListByCondition(HrEmpCheckDetail hrEmpCheckDetail);

}
