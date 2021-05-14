package com.ule.oa.base.service;

import com.ule.oa.common.exception.OaException;

/**
  * @ClassName: EmpQuitRecordService
  * @Description: 员工离职记录业务层接口
  * @author minsheng
  * @date 2017年7月27日 下午2:12:12
 */
public interface EmpQuitRecordService {
	/**
	 * @throws OaException 
	  * empJobStatusDoc(离职员工归档)
	  * @Title: empJobStatusDoc
	  * @Description: 离职员工归档
	  * void    返回类型
	  * @throws
	 */
	public void empJobStatusDoc() throws OaException;
}
