package com.ule.oa.base.service;

import java.util.Date;
import java.util.List;

import com.ule.oa.base.po.AttnTaskRecord;
import com.ule.oa.base.po.Employee;

public interface AttnTaskRecordService {

    int insert(AttnTaskRecord record);

    List<AttnTaskRecord> selectByCondition(AttnTaskRecord attnTaskRecord);

    int updateById(AttnTaskRecord record);

    /**
      * 获取已经 拉过考勤数据的最大日期
      * @Title: getMaxDateOfTask
      * @return    设定文件
      * Date    返回类型
      * @throws
     */
	Date getMaxDateOfTask();
	
	/**
	  * 根据日期插入考勤任务,只能被kafka触发，记录日期所有员工考勤任务状态
	  * @Title: setTaskRecord
	  * @param record    设定文件
	  * void    返回类型
	  * @throws
	 */
	void setTaskRecord(AttnTaskRecord record);

	/**
	  * 根据条件查询所欲考勤任务未完成的员工
	  * @Title: selectEmpByAttnTask
	  * @Description: TODO
	  * @param attnTaskRecordCondition
	  * @return    设定文件
	  * List<Employee>    返回类型
	  * @throws
	 */
	List<Employee> selectEmpByAttnTask(AttnTaskRecord attnTaskRecordCondition);

	int saveBatch(List<AttnTaskRecord> list);
}