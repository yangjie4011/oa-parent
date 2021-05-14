package com.ule.oa.base.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ule.oa.base.mapper.EmpTrainingMapper;
import com.ule.oa.base.po.EmpTraining;
import com.ule.oa.base.service.EmpTrainingService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;

@Service
public class EmpTrainingServiceImpl implements EmpTrainingService {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private UserService userService;
	
	@Autowired
	private EmpTrainingMapper empTrainingMapper;
	
	private Date CURRENT_TIME = DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_LONG), DateUtils.FORMAT_LONG);//当前时间
	
	@Override
	public int save(EmpTraining empTraining) {
		empTraining.setCreateUser(userService.getCurrentAccount());
		empTraining.setCreateTime(CURRENT_TIME);
		empTraining.setUpdateUser(userService.getCurrentAccount());
		empTraining.setUpdateTime(CURRENT_TIME);
		empTraining.setDelFlag(ConfigConstants.IS_NO_INTEGER);
		empTraining.setVersion(ConfigConstants.DEFAULT_VERSION);
		
		return empTrainingMapper.save(empTraining);
	}

	@Override
	public int updateById(EmpTraining empTraining) throws OaException {
		empTraining.setUpdateUser(userService.getCurrentAccount());
		empTraining.setUpdateTime(CURRENT_TIME);
		Integer count = empTrainingMapper.updateById(empTraining);
		
		if(0 == count){
			throw new OaException("本条数据已被其他人更新，请确认！");
		}
		return count;
	}

	@Override
	public List<EmpTraining> getListByCondition(EmpTraining empTraining){
		return empTrainingMapper.getListByCondition(empTraining);
	}

	@Override
	public int deleteBatchNotApply(List<EmpTraining> empTrainings,
			Long employeeId, String updateUser, Date updateTime) {
		return empTrainingMapper.deleteBatchNotApply(empTrainings,employeeId,updateUser,updateTime);
	}

	@Override
	public int saveBatch(List<EmpTraining> empTrainings) {
        if(null != empTrainings && !empTrainings.isEmpty()){
        	return empTrainingMapper.saveBatch(empTrainings);
        }
		return 0;
	}
}
