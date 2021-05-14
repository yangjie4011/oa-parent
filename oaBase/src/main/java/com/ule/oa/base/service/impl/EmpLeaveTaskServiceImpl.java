package com.ule.oa.base.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ule.oa.base.mapper.EmpLeaveTaskMapper;
import com.ule.oa.base.po.EmpLeaveTask;
import com.ule.oa.base.service.EmpLeaveTaskService;

/**
 * @ClassName: EmpLeaveTaskService
 * @Description: 员工假期任务业务层
 * @author minsheng
 * @date 2017年7月4日 下午3:54:09
*/
@Service
public class EmpLeaveTaskServiceImpl implements EmpLeaveTaskService {
	@Autowired
	EmpLeaveTaskMapper empLeaveTaskMapper;
	
	/**
	  * deleteAllByType(删除全表-物理删除)
	  * @Title: deleteAllByType
	  * @Description: 删除全表-物理删除
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public int deleteAllByType(Integer type){
		return empLeaveTaskMapper.deleteAllByType(type);
	}
	
	/**
	  * deleteAllByCondition(删除任务表)
	  * @Title: deleteAllByCondition
	  * @Description: 删除任务表
	  * @param task
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public int deleteAllByCondition(EmpLeaveTask task){
		return empLeaveTaskMapper.deleteAllByCondition(task);
	}

	/**
	  * getCount(根据条件获取记录数)
	  * @Title: getCount
	  * @Description: 根据条件获取记录数
	  * @param task
	  * @return    设定文件
	  * Long    返回类型
	  * @throws
	 */
	@Override
	public Long getCount(EmpLeaveTask task) {
		return empLeaveTaskMapper.getCount(task);
	}
	
	/**
	  * save(生成任务表)
	  * @Title: save
	  * @Description: 生成任务表
	  * @param task
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public int save(EmpLeaveTask task){
		return empLeaveTaskMapper.save(task);
	}
}
