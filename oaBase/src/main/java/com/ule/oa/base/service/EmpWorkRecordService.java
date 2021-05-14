package com.ule.oa.base.service;

import java.util.Date;
import java.util.List;

import com.ule.oa.base.po.EmpWorkRecord;
import com.ule.oa.common.exception.OaException;

public interface EmpWorkRecordService {
	/**
	  * save(保存员工工作经历)
	  * @Title: save
	  * @Description: 保存员工工作经历
	  * @param record    设定文件
	  * void    返回类型
	  * @throws
	 */
	public void save(EmpWorkRecord record);

	/**
	  * getListByCondition(获取所有员工的工作经历)
	  * @Title: getListByCondition
	  * @Description: 获取所有员工的工作经历
	  * @param id
	  * @return    设定文件
	  * List<EmpWorkRecord>    返回类型
	  * @throws
	 */
	public List<EmpWorkRecord> getListByCondition(EmpWorkRecord empWorkRecord);

	/**
	 * @throws OaException 
	  * updateById(根据主键更新员工工作经历)
	  * @Title: updateById
	  * @Description: 根据主键更新员工工作经历
	  * @param record
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
	public int updateById(EmpWorkRecord record) throws OaException;

	/**
	 * 
	  * 批量保存申请修改的员工资料数据
	  * @Title: saveBatch
	  * @param empWorkRecords
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
	public int saveBatch(List<EmpWorkRecord> empWorkRecords);

	/**
	 * 
	  * 批量删除申请修改的数据
	  * @Title: deleteBatchNotApply
	  * @param empWorkRecords
	  * @param cURRENT_EMPLOYEE_ID
	  * @param cURRENT_USER_NAME
	  * @param cURRENT_TIME    设定文件
	  * void    返回类型
	  * @throws
	 */
	public int deleteBatchNotApply(List<EmpWorkRecord> empWorkRecords,
			Long employeeId, String updateUser,
			Date updateTime);

}
