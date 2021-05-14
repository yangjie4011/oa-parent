package com.ule.oa.base.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ule.oa.base.mapper.EmpSchoolMapper;
import com.ule.oa.base.po.EmpSchool;
import com.ule.oa.base.service.EmpSchoolService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.ConfigConstants;

/**
  * @ClassName: EmpSchoolServiceImpl
  * @Description: 员工教育情况业务层
  * @author minsheng
  * @date 2017年5月19日 下午2:25:22
 */
@Service
public class EmpSchoolServiceImpl implements EmpSchoolService {
	@Autowired
	private EmpSchoolMapper empSchoolMapper;
	@Autowired
	private UserService userService;

	@Override
	public int save(EmpSchool empSchool) {
		empSchool.setCreateTime(new Date());
		empSchool.setCreateUser(userService.getCurrentAccount());
		empSchool.setVersion(ConfigConstants.DEFAULT_VERSION);
		empSchool.setDelFlag(ConfigConstants.IS_NO_INTEGER);
		
		return empSchoolMapper.save(empSchool);
	}

	@Override
	public int updateById(EmpSchool empSchool) throws Exception {
		empSchool.setUpdateTime(new Date());
		empSchool.setUpdateUser(userService.getCurrentAccount());
		int updateCount = empSchoolMapper.updateById(empSchool);
			
		if(updateCount == 0){
			throw new OaException("您当前编辑的员工教育经历已经被其它人修改过，请重新编辑");
		}
		
		return updateCount;
	}

	@Override
	public List<EmpSchool> getListByCondition(EmpSchool empSchool){
		return empSchoolMapper.getListByCondition(empSchool);
	}

	@Override
	public int deleteBatchNotApply(List<EmpSchool> empSchools, Long employeeId,
			String updateUser, Date udateTime) {
		return empSchoolMapper.deleteBatchNotApply(empSchools,employeeId,updateUser,udateTime);
	}

	@Override
	public int saveBatch(List<EmpSchool> empSchools) {
		if(null != empSchools && !empSchools.isEmpty()){
			return empSchoolMapper.saveBatch(empSchools);
		}
		return 0;
	}
}
