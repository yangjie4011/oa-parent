package com.ule.oa.base.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ule.oa.base.mapper.EmpAchievementMapper;
import com.ule.oa.base.po.EmpAchievement;
import com.ule.oa.base.service.EmpAchievementService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;

@Service
public class EmpAchievementServiceImpl implements EmpAchievementService {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private EmpAchievementMapper empAchievementMapper;
	
	@Resource
	private UserService userService;
	
	private Date CURRENT_TIME = DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_LONG), DateUtils.FORMAT_LONG);//当前时间
	
	public List<EmpAchievement> getListByCondition(EmpAchievement empAchievement){
		return empAchievementMapper.getListByCondition(empAchievement);
	}

	@Override
	public Integer save(EmpAchievement empAchievement) {
		
		empAchievement.setCreateUser(userService.getCurrentAccount());
		empAchievement.setCreateTime(CURRENT_TIME);
		empAchievement.setUpdateUser(userService.getCurrentAccount());
		empAchievement.setUpdateTime(CURRENT_TIME);
		empAchievement.setDelFlag(ConfigConstants.IS_NO_INTEGER);

		Integer count = empAchievementMapper.save(empAchievement);
		return count;
	}

	@Override
	public Integer updateById(EmpAchievement empAchievement) throws OaException {
		
		empAchievement.setUpdateUser(userService.getCurrentAccount());
		empAchievement.setUpdateTime(CURRENT_TIME);

		Integer count = empAchievementMapper.updateById(empAchievement);
		
		if(0 == count){
			throw new OaException("本条数据已被其他人更新，请确认！");
		}
		return count;
	}
}
