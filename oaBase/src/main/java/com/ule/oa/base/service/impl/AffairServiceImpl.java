package com.ule.oa.base.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ule.oa.base.mapper.AffairMapper;
import com.ule.oa.base.po.Affair;
import com.ule.oa.base.service.AffairService;

/**
  * @ClassName: AffairServiceImpl
  * @Description: 日程信息业务层
  * @author wufei
  * @date 2017年5月17日 上午9:56:25
 */
@Service
public class AffairServiceImpl implements AffairService{
	
	@Autowired
	private AffairMapper affairMapper;
	
	@Override
	public List<Affair> getListByCondition(Affair affair) {
		return affairMapper.getListByCondition(affair);
	}
	
    @Override
    public int save(Affair affair){
	    return affairMapper.save(affair);
    }
   
    @Override
    public int updateById(Affair affair){
	    return affairMapper.updateById(affair);
    }


   
}
