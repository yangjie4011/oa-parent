package com.ule.oa.base.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ule.oa.base.mapper.HrEmpCheckListMapper;
import com.ule.oa.base.po.HrEmpCheckList;
import com.ule.oa.base.service.HrEmpCheckListService;

/**
 * @ClassName: 离职办理清单
 * @Description: 离职办理清单
 * @author yangjie
 * @date 2017年6月6日
 */
@Service
public class HrEmpCheckListServiceImpl implements HrEmpCheckListService{
	
	@Autowired
	private HrEmpCheckListMapper hrEmpCheckListMapper;

	@Override
	public void save(HrEmpCheckList hrEmpCheckList) {
		hrEmpCheckListMapper.save(hrEmpCheckList);
	}

	@Override
	public int updateById(HrEmpCheckList hrEmpCheckList) {
		return hrEmpCheckListMapper.updateById(hrEmpCheckList);
	}

	@Override
	public List<HrEmpCheckList> getListByCondition(HrEmpCheckList hrEmpCheckList) {
		return hrEmpCheckListMapper.getListByCondition(hrEmpCheckList);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void checkByDepart(List<HrEmpCheckList> list) {
		for(HrEmpCheckList checkList:list){
			hrEmpCheckListMapper.updateById(checkList);
		}
	}
	
 

}
