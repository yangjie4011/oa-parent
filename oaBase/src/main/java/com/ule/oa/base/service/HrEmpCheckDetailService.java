package com.ule.oa.base.service;

import java.util.List;

import com.ule.oa.base.po.HrEmpCheckDetail;

/**
 * @ClassName: 离职办理清单详细
 * @Description: 离职办理清单详细
 * @author yangjie
 * @date 2017年6月6日
 */
public interface HrEmpCheckDetailService {
	
    public void save(HrEmpCheckDetail hrEmpCheckDetail);
	
	public int updateById(HrEmpCheckDetail hrEmpCheckDetail);
	
	public List<HrEmpCheckDetail> getListByCondition(HrEmpCheckDetail hrEmpCheckDetail);

}
