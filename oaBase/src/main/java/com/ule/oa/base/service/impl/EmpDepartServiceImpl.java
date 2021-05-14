package com.ule.oa.base.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ule.oa.base.mapper.EmpDepartMapper;
import com.ule.oa.base.po.EmpDepart;
import com.ule.oa.base.service.EmpDepartService;
import com.ule.oa.common.exception.OaException;

@Service
public class EmpDepartServiceImpl implements EmpDepartService{

	@Autowired
	private EmpDepartMapper empDepartMapper;

	@Override
	public void save(EmpDepart model) {
		empDepartMapper.save(model);
	}

	@Override
	public int updateById(EmpDepart model) throws Exception {
		int updateCount = 0;
		
		try {
			updateCount = empDepartMapper.updateById(model);
			
			if(updateCount == 0){
				throw new OaException("您当前修改的员工部门信息已经被其它人修改过，请重新编辑");
			}
			
		} catch (Exception e) {
			throw e;
		}
		
		return updateCount;
	}

	@Override
	public List<EmpDepart> getListByCondition(EmpDepart model) {
		return empDepartMapper.getListByCondition(model);
	}

	@Override
	public EmpDepart getByCondition(EmpDepart model){
		List<EmpDepart> list = getListByCondition(model);
		
		if(null != list && list.size()>0){
			return list.get(0);
		}
		
		return model;
	}

	@Override
	public int updateByEmployeeId(EmpDepart model) {
		return empDepartMapper.updateByEmployeeId(model);
	}
}
