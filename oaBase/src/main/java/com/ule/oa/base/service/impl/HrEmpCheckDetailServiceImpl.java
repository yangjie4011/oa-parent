package com.ule.oa.base.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ule.oa.base.mapper.HrEmpCheckDetailMapper;
import com.ule.oa.base.po.HrEmpCheckDetail;
import com.ule.oa.base.service.HrEmpCheckDetailService;

/**
 * @ClassName: 离职办理清单详细
 * @Description: 离职办理清单详细
 * @author yangjie
 * @date 2017年6月6日
 */
@Service
public class HrEmpCheckDetailServiceImpl implements HrEmpCheckDetailService{
	
	@Autowired
	private HrEmpCheckDetailMapper hrEmpCheckDetailMapper;

	@Override
	public void save(HrEmpCheckDetail hrEmpCheckDetail) {
		hrEmpCheckDetailMapper.save(hrEmpCheckDetail);
	}

	@Override
	public int updateById(HrEmpCheckDetail hrEmpCheckDetail) {
		return hrEmpCheckDetailMapper.updateById(hrEmpCheckDetail);
	}

	@Override
	public List<HrEmpCheckDetail> getListByCondition(
			HrEmpCheckDetail hrEmpCheckDetail) {
		return hrEmpCheckDetailMapper.getListByCondition(hrEmpCheckDetail);
	}

}
