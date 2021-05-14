package com.ule.oa.base.service;

import java.util.Date;
import java.util.List;

import com.ule.oa.base.po.EmpSchool;

/**
  * @ClassName: EmpSchoolService
  * @Description: 员工教育情况业务接口层
  * @author minsheng
  * @date 2017年5月19日 下午2:24:44
 */
public interface EmpSchoolService {
	 public int save(EmpSchool empSchool);

	 public int updateById(EmpSchool empSchool) throws Exception;
	 
	 public List<EmpSchool> getListByCondition(EmpSchool empSchool);

	 /**
	   * 批量逻辑删除提交申请的数据
	   * @Title: deleteBatchNotApply
	   * @param empSchools
	   * @return    设定文件
	   * int    返回类型
	   * @throws
	  */
	 public int deleteBatchNotApply(List<EmpSchool> empSchools,
			Long employeeId, String updateUser,Date udateTime);

	 /**
	  * 批量保存提交申请的数据
	  * @Title: saveBatch
	  * @param empSchools
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	  */
	 public int saveBatch(List<EmpSchool> empSchools);
}
