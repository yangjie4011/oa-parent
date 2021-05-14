package com.ule.oa.base.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ule.oa.base.mapper.DepartPositionMapper;
import com.ule.oa.base.po.DepartPosition;
import com.ule.oa.base.service.DepartPositionService;

/**
  * @ClassName: DepartPositionService
  * @Description: 部门职位信息业务层
  * @author minsheng
  * @date 2017年5月12日 下午4:30:00
 */
@Service
public class DepartPositionServiceImpl implements DepartPositionService {
	@Autowired
	private DepartPositionMapper departPositionMapper;
	
	@Override
	public List<DepartPosition> getListByCondition(DepartPosition departPosition){
		return departPositionMapper.getListByCondition(departPosition);
	}
	
	@Override
	public int save(DepartPosition departPosition){
		return departPositionMapper.save(departPosition);
	}
	
	@Override
	public int delByPositionId(Long positionId){
		return departPositionMapper.delByPositionId(positionId);
	}
}	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
