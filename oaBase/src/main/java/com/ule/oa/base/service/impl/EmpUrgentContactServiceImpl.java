package com.ule.oa.base.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ule.oa.base.mapper.EmpUrgentContactMapper;
import com.ule.oa.base.po.EmpUrgentContact;
import com.ule.oa.base.service.EmpUrgentContactService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.ConfigConstants;

@Service
public class EmpUrgentContactServiceImpl implements EmpUrgentContactService {
	@Autowired
	private EmpUrgentContactMapper empUrgentContactMapper;
	@Autowired
	private UserService userService;
	
	@Override
	public int save(EmpUrgentContact empUrgentContact){
		empUrgentContact.setCreateTime(new Date());
		empUrgentContact.setCreateUser(userService.getCurrentAccount());
		empUrgentContact.setDelFlag(ConfigConstants.IS_NO_INTEGER);
		
		return empUrgentContactMapper.save(empUrgentContact);
	}
	
	@Override
	public int updateById(EmpUrgentContact empUrgentContact) throws OaException{
		empUrgentContact.setUpdateTime(new Date());
		empUrgentContact.setUpdateUser(userService.getCurrentAccount());
		int updateCount = empUrgentContactMapper.updateById(empUrgentContact);
		
		if(updateCount == 0){
			throw new OaException("您当前编辑的紧急联系人信息已经被其它人修改过，请重新编辑");
		}
		
		return updateCount;
	}
	
	@Override
	public List<EmpUrgentContact> getListByCondition(EmpUrgentContact empUrgentContact){
		return empUrgentContactMapper.getListByCondition(empUrgentContact);
	}

	@Override
	public int saveBatch(List<EmpUrgentContact> empUrgentContacts) {
		if(null==empUrgentContacts || empUrgentContacts.isEmpty()){
			return 0;
		}else{
			return empUrgentContactMapper.saveBatch(empUrgentContacts);
		}
		
	}

	@Override
	public int deleteBatchNotApply(List<EmpUrgentContact> empUrgentContacts,Long employeeId,
			String updateUser, Date updateTime) {
		return empUrgentContactMapper.deleteBatchNotApply(empUrgentContacts,employeeId,updateUser,updateTime);
		
	}
}
