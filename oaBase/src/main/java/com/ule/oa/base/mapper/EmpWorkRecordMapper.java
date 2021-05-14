package com.ule.oa.base.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.EmpWorkRecord;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

/**
  * @ClassName: EmpWorkRecordMapper
  * @Description: 员工工作经历
  * @author minsheng
  * @date 2017年5月9日 上午9:07:33
 */
public interface EmpWorkRecordMapper extends OaSqlMapper{
	/**
	  * save(保存员工工作经历)
	  * @Title: save
	  * @Description: 保存员工工作经历
	  * @param record    设定文件
	  * void    返回类型
	  * @throws
	 */
	void save(EmpWorkRecord record);

	/**
	  * getListByCondition(获取所有员工的工作经历)
	  * @Title: getListByCondition
	  * @Description: 获取所有员工的工作经历
	  * @param empWorkRecord
	  * @return    设定文件
	  * List<EmpWorkRecord>    返回类型
	  * @throws
	 */
	List<EmpWorkRecord> getListByCondition(EmpWorkRecord empWorkRecord);

	/**
	  * updateById(根据主键更新员工工作经历)
	  * @Title: updateById
	  * @Description: 根据主键更新员工工作经历
	  * @param record
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
	int updateById(EmpWorkRecord record);

	int saveBatch(List<EmpWorkRecord> empWorkRecords);

	int deleteBatchNotApply(@Param("list")List<EmpWorkRecord> empWorkRecords,@Param("employeeId")Long employeeId,
			@Param("updateUser")String updateUser, @Param("updateTime")Date updateTime);
	
	List<EmpWorkRecord> getListByEmployeeId(@Param("employeeId")Long employeeId);
	
	int deleteByEmployeeId(@Param("employeeId")Long employeeId,@Param("updateUser")String updateUser,@Param("updateTime")Date updateTime);

}