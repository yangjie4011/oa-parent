package com.ule.oa.base.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ule.oa.base.mapper.EmpAppraiseMapper;
import com.ule.oa.base.po.EmpAppraise;
import com.ule.oa.base.service.EmpAppraiseService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;

@Service
public class EmpAppraiseServiceImpl implements EmpAppraiseService {
	@Autowired
	private EmpAppraiseMapper empAppraiseMapper;
	
	@Resource
	private UserService userService;
	
	private Date CURRENT_TIME = DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_LONG), DateUtils.FORMAT_LONG);//当前时间

	@Override
	public List<EmpAppraise> getListByCondition(EmpAppraise empAppraise) {
		return empAppraiseMapper.getListByCondition(empAppraise);
	}

	@Override
	public Integer save(EmpAppraise empAppraise) {
		
		empAppraise.setCreateUser(userService.getCurrentAccount());
		empAppraise.setCreateTime(CURRENT_TIME);
		empAppraise.setUpdateUser(userService.getCurrentAccount());
		empAppraise.setUpdateTime(CURRENT_TIME);
		empAppraise.setDelFlag(ConfigConstants.IS_NO_INTEGER);
		
		return empAppraiseMapper.save(empAppraise);
	}

	@Override
	public Integer updateById(EmpAppraise empAppraise) throws Exception {
		empAppraise.setUpdateUser(userService.getCurrentAccount());
		empAppraise.setUpdateTime(CURRENT_TIME);
		
		Integer count = empAppraiseMapper.updateById(empAppraise);
		
		if(0 == count){
			throw new OaException("本条数据已被其他人更新，请确认！");
		}
		return count;
	}

}
