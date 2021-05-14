package com.ule.oa.base.mapper;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ule.oa.base.po.EmpTraining;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

/**
  * @ClassName: EmpTrainingMapper
  * @Description: 员工培训信息
  * @author minsheng
  * @date 2017年5月8日 下午5:14:46
 */
public interface EmpTrainingMapper extends OaSqlMapper{
	/**
	 * @return 
	  * save(保存员工培训信息)
	  * @Title: save
	  * @Description: 保存员工培训信息
	  * @param empTraining    设定文件
	  * void    返回类型
	  * @throws
	 */
	public int save(EmpTraining empTraining);
	
	/**
	  * updateById(更新员工培训信息)
	  * @Title: updateById
	  * @Description: 更新员工培训信息
	  * @param empTraining
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
	public int updateById(EmpTraining empTraining);
	
	/**
	  * getListByCondition(查找员工所有培训信息)
	  * @Title: getListByCondition
	  * @Description: 查找员工所有培训信息
	  * @param empTraining
	  * @return    设定文件
	  * List<EmpTraining>    返回类型
	  * @throws
	 */
	public List<EmpTraining> getListByCondition(EmpTraining empTraining);

	public int deleteBatchNotApply(@Param("list") List<EmpTraining> empTrainings,
			@Param("employeeId")Long employeeId, @Param("updateUser")String updateUser,
			@Param("updateTime")Date updateTime);

	public int saveBatch(List<EmpTraining> empTrainings);
	
	List<EmpTraining> getListByEmployeeId(@Param("employeeId")Long employeeId);
	
	int deleteByEmployeeId(@Param("employeeId")Long employeeId,@Param("updateUser")String updateUser,@Param("updateTime")Date updateTime,@Param("isCompanyTraining")Integer isCompanyTraining);
}