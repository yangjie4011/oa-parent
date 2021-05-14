package com.ule.oa.base.service;

import java.util.Date;
import java.util.List;

import com.ule.oa.base.po.EmpTraining;
import com.ule.oa.common.exception.OaException;

public interface EmpTrainingService {
	 public int save(EmpTraining record);

	 public int updateById(EmpTraining record) throws OaException;
	 
	 public List<EmpTraining> getListByCondition(EmpTraining empTraining);

	 /**
	  * 
	   * 批量逻辑删除提交申请的数据
	   * @Title: deleteBatchNotApply
	   * @param empTrainings
	   * @param employeeId
	   * @param updateUser
	   * @param updateTime
	   * @return    设定文件
	   * int    返回类型
	   * @throws
	  */
	 public int deleteBatchNotApply(List<EmpTraining> empTrainings,
			Long employeeId, String updateUser,
			Date updateTime);

	 /**
	   * 批量保存申请提交的数据
	   * @Title: saveBatch
	   * @param empTrainings
	   * @return    设定文件
	   * int    返回类型
	   * @throws
	  */
	 public int saveBatch(List<EmpTraining> empTrainings);
}
