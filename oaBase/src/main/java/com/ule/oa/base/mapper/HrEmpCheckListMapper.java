package com.ule.oa.base.mapper;

import java.util.List;
import com.ule.oa.base.po.HrEmpCheckList;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

/**
 * @ClassName: 离职办理清单
 * @Description: 离职办理清单
 * @author yangjie
 * @date 2017年6月6日
 */
public interface HrEmpCheckListMapper extends OaSqlMapper{
	
    void save(HrEmpCheckList hrEmpCheckList);
	
	int updateById(HrEmpCheckList hrEmpCheckList);
	
	List<HrEmpCheckList> getListByCondition(HrEmpCheckList hrEmpCheckList);

}
