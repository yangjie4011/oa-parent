package com.ule.oa.base.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ule.oa.base.mapper.EmpFamilyMemberMapper;
import com.ule.oa.base.po.EmpFamilyMember;
import com.ule.oa.base.service.EmpFamilyMemberService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.ConfigConstants;

/**
  * @ClassName: EmpFamilyMemberServiceImpl
  * @Description: 员工家属业务层
  * @author minsheng
  * @date 2017年5月19日 下午1:34:54
 */
@Service
public class EmpFamilyMemberServiceImpl implements EmpFamilyMemberService {
	@Autowired
	private EmpFamilyMemberMapper empFamilyMemberMapper;
	@Autowired
	private UserService userService;
	
	@Override
	public int save(EmpFamilyMember empFamilyMember){
		empFamilyMember.setCreateTime(new Date());
		empFamilyMember.setCreateUser(userService.getCurrentAccount());
		empFamilyMember.setDelFlag(ConfigConstants.IS_NO_INTEGER);
		
		return empFamilyMemberMapper.save(empFamilyMember);
	}
	
	@Override
	public int updateById(EmpFamilyMember empFamilyMember) throws OaException{
		empFamilyMember.setUpdateTime(new Date());
		empFamilyMember.setUpdateUser(userService.getCurrentAccount());
		int updateCount = empFamilyMemberMapper.updateById(empFamilyMember);
		
		if(updateCount == 0){
			throw new OaException("您当前编辑的员工家属信息已经被其它人修改过，请重新编辑");
		}
		
		return updateCount;
	}

	@Override
	public List<EmpFamilyMember> getListByCondition(EmpFamilyMember empFamilyMember) {
		return empFamilyMemberMapper.getListByCondition(empFamilyMember);
	}

	@Override
	public EmpFamilyMember getByCondition(EmpFamilyMember empFamilyMember) {
		List<EmpFamilyMember> list = getListByCondition(empFamilyMember);
		
		if(null != list && list.size()>0){
			return list.get(0);
		}
		
		return empFamilyMember;
	}

	@Override
	public int saveBatch(List<EmpFamilyMember> empFamilyMembers) {
		if(null != empFamilyMembers && !empFamilyMembers.isEmpty()){
		   return empFamilyMemberMapper.saveBatch(empFamilyMembers);
		}else{
		   return 0;
		}
	}

	@Override
	public int deleteBatchNotApply(List<EmpFamilyMember> empFamilyMembers,Long employeeId,
			String currentUser, Date currentTime) {
		return empFamilyMemberMapper.deleteBatchNotApply(empFamilyMembers,employeeId,currentUser,currentTime);
	}
	
	
}
