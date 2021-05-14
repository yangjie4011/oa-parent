package com.ule.oa.base.service;

import java.util.List;

import com.ule.oa.base.po.HrEmpCheckList;

/**
 * @ClassName: 离职办理清单
 * @Description: 离职办理清单
 * @author yangjie
 * @date 2017年6月6日
 */
public interface HrEmpCheckListService {
	
    public void save(HrEmpCheckList hrEmpCheckList);
	
	public int updateById(HrEmpCheckList hrEmpCheckList);
	
	public List<HrEmpCheckList> getListByCondition(HrEmpCheckList hrEmpCheckList);
	
	public void checkByDepart(List<HrEmpCheckList> list);

}
