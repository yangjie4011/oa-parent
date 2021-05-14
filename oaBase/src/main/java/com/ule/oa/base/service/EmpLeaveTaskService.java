package com.ule.oa.base.service;

import com.ule.oa.base.po.EmpLeaveTask;

/**
  * @ClassName: EmpLeaveTaskService
  * @Description: 员工假期任务业务层
  * @author minsheng
  * @date 2017年7月4日 下午3:54:09
 */
public interface EmpLeaveTaskService {
	/**
	  * deleteAllByType(删除全表-物理删除)
	  * @Title: deleteAllByType
	  * @Description: 删除全表-物理删除
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
	int deleteAllByType(Integer type);
	
	/**
	  * deleteAllByCondition(删除任务表)
	  * @Title: deleteAllByCondition
	  * @Description: 删除任务表
	  * @param task
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
	int deleteAllByCondition(EmpLeaveTask task);
	
	/**
	  * getCount(根据条件获取记录数)
	  * @Title: getCount
	  * @Description: 根据条件获取记录数
	  * @param task
	  * @return    设定文件
	  * Long    返回类型
	  * @throws
	 */
	Long getCount(EmpLeaveTask task);
	
	/**
	  * save(生成任务表)
	  * @Title: save
	  * @Description: 生成任务表
	  * @param task
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
	public int save(EmpLeaveTask task);
}
