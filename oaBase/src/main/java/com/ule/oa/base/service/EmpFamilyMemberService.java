package com.ule.oa.base.service;

import java.util.Date;
import java.util.List;

import com.ule.oa.base.po.EmpFamilyMember;
import com.ule.oa.common.exception.OaException;

/**
  * @ClassName: EmpFamilyMemberService
  * @Description: 员工家属业务层接口
  * @author minsheng
  * @date 2017年5月19日 下午1:34:25
 */
public interface EmpFamilyMemberService {
	public int save(EmpFamilyMember empFamilyMember);
	
	public int updateById(EmpFamilyMember empFamilyMember) throws OaException;
	
	public List<EmpFamilyMember> getListByCondition(EmpFamilyMember empFamilyMember);
	
	public EmpFamilyMember getByCondition(EmpFamilyMember empFamilyMember);

	/**
	  * 批量保存本次申请通过的数据
	  * @Title: saveBatch
	  * @param empFamilyMembers
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
	public int saveBatch(List<EmpFamilyMember> empFamilyMembers);

	/**
	 * @param employeeId 
	  * 逻辑删除未提交的数据
	  * @Title: deleteBatchNotApply
	  * @param empFamilyMembers
	  * @param currentUser
	  * @param currenTime    设定文件
	  * void    返回类型
	  * @throws
	 */
	public int deleteBatchNotApply(List<EmpFamilyMember> empFamilyMembers,
			Long employeeId, String currentUser, Date currenTime);
}
