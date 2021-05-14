package com.ule.oa.base.service;

import java.util.List;

import com.ule.oa.base.po.EmpPostRecord;

/**
  * @ClassName: EmpPostRecordService
  * @Description: 员工岗位记录业务层
  * @author minsheng
  * @date 2017年6月22日 下午7:52:41
 */
public interface EmpPostRecordService {
	public List<EmpPostRecord> getListByCondition(EmpPostRecord empPostRecord);
	
	/**
	  * 保存一条数据
	  * @Title: save
	  * @param empPostRecord
	  * @return    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	Integer save(EmpPostRecord empPostRecord);
	
	/**
	 * @throws Exception 
	  * 更新一条数据
	  * @Title: updateById
	  * @param empPostRecord
	  * @return    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	Integer updateById(EmpPostRecord empPostRecord) throws Exception;
}
