package com.ule.oa.base.mapper;

import java.util.List;

import com.ule.oa.base.po.EmpApplication;
import com.ule.oa.base.po.tbl.EmpApplicationTbl;
import com.ule.oa.base.util.jdbc.OaSqlMapper;
import com.ule.oa.common.persistence.SqlMapper;

public interface EmpApplicationMapper extends OaSqlMapper{
	
    int deleteById(Long id);

    int insert(EmpApplication record);

    int updateById(EmpApplication record);

    
    /**
      * 条件查询申请列表
      * selectByCondition(这里用一句话描述这个方法的作用)
      * @Title: selectByCondition
      * @param empApplication
      * @return    设定文件
      * List<EmpApplication>    返回类型
      * @throws
     */
    List<EmpApplication> selectByCondition(EmpApplication empApplication);

    /**
     * 
      * 批量保存记录的日志
      * @Title: save
      * @Description: TODO
      * @param list    设定文件
      * void    返回类型
      * @throws
     */
	void insertBatch(List<EmpApplicationTbl> list);

	/**
	  * 获取当前员工资料申请最大版本号
	  * @Title: getMaxVersionByEmpId
	  * @param cURRENT_EMPLOYEE_ID
	  * @return    设定文件
	  * String    返回类型
	  * @throws
	 */
	String getMaxVersionByEmpId(Long employeeId);

	/**
	  * 更新审核状态和审核人
	  * @Title: updateAppyLogStatus
	  * @Description: TODO
	  * @param approvalId
	  * @param employeeId
	  * @param approvalStatus    设定文件
	  * void    返回类型
	  * @throws
	 */
	void updateAppyLogStatus(EmpApplication empApplication);
}