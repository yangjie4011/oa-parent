package com.ule.oa.base.service;

import java.util.Date;
import java.util.List;

import com.ule.oa.base.po.EmpUrgentContact;
import com.ule.oa.common.exception.OaException;

public interface EmpUrgentContactService {
	public int save(EmpUrgentContact empUrgentContact);
	
	public int updateById(EmpUrgentContact empUrgentContact) throws OaException;
	
	public List<EmpUrgentContact> getListByCondition(EmpUrgentContact empUrgentContact);

	/**
	  * 批量保存资料申请的数据
	  * @Title: saveBatch
	  * @param empUrgentContacts    设定文件
	  * void    返回类型
	  * @throws
	 */
	public int saveBatch(List<EmpUrgentContact> empUrgentContacts);

	/**
	 * @param cURRENT_EMPLOYEE_ID 
	  * 批量删除资料申请的数据
	  * @Title: deleteBatchNotApply
	  * @param empUrgentContacts
	  * @param cURRENT_USER_NAME
	  * @param cURRENT_TIME    设定文件
	  * void    返回类型
	  * @throws
	 */
	public int deleteBatchNotApply(List<EmpUrgentContact> empUrgentContacts,
			Long employeeId, String updateUser, Date updateTime);
}
