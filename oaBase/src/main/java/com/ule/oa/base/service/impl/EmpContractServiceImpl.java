package com.ule.oa.base.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ule.oa.base.mapper.EmpContractMapper;
import com.ule.oa.base.po.EmpContract;
import com.ule.oa.base.service.EmpContractService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;

@Service
public class EmpContractServiceImpl implements EmpContractService {
	@Autowired
	private EmpContractMapper empContractMapper;
	
	@Resource
	private UserService userService;
	
	private Date CURRENT_TIME = DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_LONG), DateUtils.FORMAT_LONG);//当前时间
	
	@Override
	public List<EmpContract> getListByCondition(EmpContract empContract){
		return empContractMapper.getListByCondition(empContract);
	}
	
	@Override
	public EmpContract getByCondition(EmpContract empContract){
		List<EmpContract> list = getListByCondition(empContract);
		
		if(null != list && list.size()>0){
			return list.get(0);
		}
		return empContract;
	}
	
	@Override
	public int updateById(EmpContract empContract) throws Exception{
		empContract.setCreateUser(userService.getCurrentAccount());
		empContract.setCreateTime(CURRENT_TIME);
		empContract.setUpdateUser(userService.getCurrentAccount());
		empContract.setUpdateTime(CURRENT_TIME);
		if(null==empContract.getDelFlag()){
			empContract.setDelFlag(ConfigConstants.IS_NO_INTEGER);
		}
		
		int updateCount = empContractMapper.updateById(empContract);
		
		if(updateCount == 0){
			throw new OaException("您当前修改的员工合同信息已经被其它人修改过，请重新编辑");
		}
		
		return updateCount;
	}

	@Override
	public int save(EmpContract empContract) {
		empContract.setCreateUser(userService.getCurrentAccount());
		empContract.setCreateTime(CURRENT_TIME);
		
		return empContractMapper.save(empContract);
	}
}
